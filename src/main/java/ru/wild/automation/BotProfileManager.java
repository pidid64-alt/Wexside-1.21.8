package ru.wild.automation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import ru.wild.WildClient;

public final class BotProfileManager {
   private static final int instance = 1;
   private static final int data = 256;
   private static final long context = 350L;
   private static final Pattern config = Pattern.compile("[A-Za-z0-9_]{1,16}");
   private static final Gson state = new GsonBuilder().setPrettyPrinting().create();
   private static final Object cache = new Object();
   private static final Object output = new Object();
   private static final ScheduledExecutorService current = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-Bot-Profiles");
      var1.setDaemon(true);
      return var1;
   });
   private static ScheduledFuture<?> active;
   private static boolean mode;
   private static boolean selection;
   private static boolean enabled;

   private BotProfileManager() {
   }

   public static void handle() {
      Path var0 = apply();
      boolean var1 = Files.isRegularFile(var0) || Files.isRegularFile(process(var0));
      List<BotProfileManager.ServerEntry> var2 = handle(var0);
      boolean var3 = false;
      if (var2 == null) {
         var2 = handle(process(var0));
         var3 = var2 != null;
      }

      if (var2 == null) {
         synchronized (cache) {
            enabled = !var1;
         }
      } else {
         synchronized (cache) {
            enabled = true;
         }

         for (BotProfileManager.ServerEntry var5 : var2) {
            HeadlessBotEngine.handle(var5.nickname(), var5.address());
         }

         if (var3) {
            process();
         }
      }
   }

   public static void process() {
      synchronized (cache) {
         if (!selection) {
            enabled = true;
            mode = true;
            if (active != null) {
               active.cancel(false);
            }

            active = current.schedule(BotProfileManager::resolve, 350L, TimeUnit.MILLISECONDS);
         }
      }
   }

   public static void compute() {
      boolean var0;
      synchronized (cache) {
         if (selection) {
            return;
         }

         selection = true;
         var0 = enabled;
         mode = false;
         if (active != null) {
            active.cancel(false);
            active = null;
         }
      }

      if (var0) {
         update();
      }

      current.shutdown();

      try {
         current.awaitTermination(1L, TimeUnit.SECONDS);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   private static void resolve() {
      synchronized (cache) {
         if (selection || !mode) {
            active = null;
            return;
         }

         mode = false;
         active = null;
      }

      boolean var5 = update();
      synchronized (cache) {
         if (!var5) {
            mode = true;
         }

         if (!selection && mode && active == null) {
            active = current.schedule(BotProfileManager::resolve, 350L, TimeUnit.MILLISECONDS);
         }
      }
   }

   private static boolean update() {
      synchronized (output) {
         JsonObject var1 = new JsonObject();
         var1.addProperty("format", "wild-bot-profiles");
         var1.addProperty("version", 1);
         JsonArray var2 = new JsonArray();

         for (HeadlessBotEngine.ServerEntry var4 : HeadlessBotEngine.process()) {
            if (var4.name() != null && var4.address() != null && !var4.address().isBlank()) {
               JsonObject var5 = new JsonObject();
               var5.addProperty("nickname", var4.name());
               var5.addProperty("address", var4.address());
               var2.add(var5);
            }
         }

         var1.add("profiles", var2);

         boolean var10000;
         try {
            handle(apply(), state.toJson(var1).getBytes(StandardCharsets.UTF_8));
            var10000 = true;
         } catch (Throwable var7) {
            System.out.println("[BotProfiles] Save failed: " + var7.getMessage());
            return false;
         }

         return var10000;
      }
   }

   private static List<BotProfileManager.ServerEntry> handle(Path var0) {
      if (var0 != null && Files.isRegularFile(var0)) {
         try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            JsonElement var2 = JsonParser.parseReader(var1);
            if (!var2.isJsonObject()) {
               return null;
            }

            JsonObject var3 = var2.getAsJsonObject();
            JsonElement var4 = var3.get("profiles");
            if (var4 != null && var4.isJsonArray()) {
               ArrayList var17 = new ArrayList();
               HashSet var6 = new HashSet();

               for (JsonElement var8 : var4.getAsJsonArray()) {
                  if (var17.size() < 256 && var8.isJsonObject()) {
                     JsonObject var9 = var8.getAsJsonObject();
                     String var10 = handle(var9, "nickname").trim();
                     String var11 = handle(var9, "address").trim();
                     String var12 = var10.toLowerCase(Locale.ROOT);
                     if (config.matcher(var10).matches() && !var11.isEmpty() && var11.length() <= 255 && var6.add(var12)) {
                        var17.add(new BotProfileManager.ServerEntry(var10, var11));
                     }
                  }
               }

               return List.copyOf(var17);
            } else {
               return null;
            }
         } catch (Throwable var15) {
            System.out.println("[BotProfiles] Load failed for " + var0.getFileName() + ": " + var15.getMessage());
            return null;
         }
      } else {
         return null;
      }
   }

   private static String handle(JsonObject var0, String var1) {
      try {
         JsonElement var2 = var0.get(var1);
         return var2 != null && !var2.isJsonNull() ? var2.getAsString() : "";
      } catch (Throwable var3) {
         return "";
      }
   }

   private static Path apply() {
      File var0 = WildClient.instance != null && WildClient.instance.cache != null ? WildClient.instance.cache : WildClient.process();
      return new File(var0, "bots.json").toPath();
   }

   private static Path process(Path var0) {
      return var0.resolveSibling(var0.getFileName() + ".bak");
   }

   private static void handle(Path var0, byte[] var1) throws Exception {
      Path var2 = var0.getParent();
      if (var2 != null) {
         Files.createDirectories(var2);
      }

      Path var3 = var0.resolveSibling(var0.getFileName() + ".tmp");

      try (FileChannel var4 = FileChannel.open(var3, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
         ByteBuffer var5 = ByteBuffer.wrap(var1);

         while (var5.hasRemaining()) {
            var4.write(var5);
         }

         var4.force(true);
      }

      try {
         Files.move(var3, var0, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException var9) {
         Files.move(var3, var0, StandardCopyOption.REPLACE_EXISTING);
      }

      try {
         Files.copy(var0, process(var0), StandardCopyOption.REPLACE_EXISTING);
      } catch (Throwable var8) {
      }
   }

   record ServerEntry(String nickname, String address) {
   }
}
