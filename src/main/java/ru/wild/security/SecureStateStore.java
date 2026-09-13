package ru.wild.security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import ru.wild.WildClient;

public final class SecureStateStore {
   private static final Gson instance = new Gson();
   private static volatile boolean data;

   private SecureStateStore() {
   }

   public static GuardRuntimeIdentity handle() {
      data = false;
      GuardRuntimeIdentity var0 = null;
      boolean var1 = false;
      boolean var2 = false;

      for (Path var4 : compute()) {
         if (Files.exists(var4)) {
            var1 = true;

            try {
               String var5 = Files.readString(var4, StandardCharsets.UTF_8);
               SecureStateStore.DataRecord var6 = handle(var5);
               if (var6 != null && var6.state != null) {
                  GuardRuntimeIdentity var7 = handle(var6.state, var6.legacy);
                  if (var0 == null || process(var7) > process(var0)) {
                     var0 = var7;
                  }
               } else {
                  var2 = true;
               }
            } catch (Throwable var8) {
               var2 = true;
            }
         }
      }

      if (var0 == null) {
         data = var1 && var2;
         var0 = GuardRuntimeIdentity.handle();
         handle(var0);
      } else {
         handle(var0);
      }

      return handle(var0, false);
   }

   public static void handle(GuardRuntimeIdentity var0) {
      handle(var0, false);
      String var1 = compute(var0);

      for (Path var3 : resolve()) {
         try {
            Files.createDirectories(var3.getParent());
            Path var4 = var3.resolveSibling(
               var3.getFileName() + "." + ProcessHandle.current().pid() + "." + Thread.currentThread().getId() + "." + System.nanoTime() + ".tmp"
            );
            Files.writeString(var4, var1, StandardCharsets.UTF_8);

            try {
               Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException var6) {
               Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (IOException var7) {
         }
      }
   }

   public static boolean process() {
      return data;
   }

   private static long process(GuardRuntimeIdentity var0) {
      long var1 = var0.config ? Math.max(var0.state, 1L) : 0L;
      return Math.max(var0.context, var1);
   }

   private static String compute(GuardRuntimeIdentity var0) {
      String var1 = instance.toJson(var0);
      String var2 = Base64.getUrlEncoder().withoutPadding().encodeToString(var1.getBytes(StandardCharsets.UTF_8));
      String var3 = process(var2 + "|" + update() + "|" + handle(2));
      JsonObject var4 = new JsonObject();
      var4.addProperty("v", 2);
      var4.addProperty("data", var2);
      var4.addProperty("sum", var3);
      return instance.toJson(var4);
   }

   private static SecureStateStore.DataRecord handle(String var0) {
      JsonObject var1 = JsonParser.parseString(var0).getAsJsonObject();
      String var2 = var1.get("data").getAsString();
      String var3 = var1.get("sum").getAsString();
      byte[] var4 = Base64.getUrlDecoder().decode(var2);
      GuardRuntimeIdentity var5 = (GuardRuntimeIdentity)instance.fromJson(new String(var4, StandardCharsets.UTF_8), GuardRuntimeIdentity.class);
      String var6 = process(var2 + "|" + update() + "|" + handle(2));
      if (handle(var3, var6)) {
         return new SecureStateStore.DataRecord(var5, false);
      }

      String var7 = process(var2 + "|" + apply() + "|" + handle(2));
      if (handle(var3, var7)) {
         return new SecureStateStore.DataRecord(var5, false);
      }

      String var8 = process(var2 + "|" + execute() + "|" + handle(2));
      if (handle(var3, var8)) {
         return new SecureStateStore.DataRecord(var5, false);
      }

      String var9 = var5 == null ? "" : String.valueOf(var5.data);
      if (!var9.isBlank()) {
         String var10 = process(var2 + "|" + var9 + "|" + handle(1));
         if (handle(var3, var10)) {
            return new SecureStateStore.DataRecord(var5, true);
         }
      }

      return null;
   }

   private static List<Path> compute() {
      ArrayList var0 = new ArrayList();
      var0.addAll(resolve());
      String var1 = System.getenv("APPDATA");
      String var2 = System.getenv("LOCALAPPDATA");
      String var3 = System.getProperty("user.home", ".");
      if (var1 != null && !var1.isBlank()) {
         var0.add(Path.of(var1, "WildClient", "state.dat"));
      }

      if (var2 != null && !var2.isBlank()) {
         var0.add(Path.of(var2, "WildClient", "cache.dat"));
      }

      var0.add(Path.of(var3, ".wildclient", "state.dat"));
      var0.add(Path.of(var3, ".minecraft", "wildclient", "state.dat"));
      return var0;
   }

   private static List<Path> resolve() {
      return List.of(WildClient.process().toPath().resolve("auth").resolve("state.dat"));
   }

   private static String process(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         return HexFormat.of().formatHex(var1.digest(var0.getBytes(StandardCharsets.UTF_8)));
      } catch (Throwable var2) {
         throw new IllegalStateException(var2);
      }
   }

   private static boolean handle(String var0, String var1) {
      if (var0 != null && var1 != null) {
         byte[] var2 = var0.getBytes(StandardCharsets.UTF_8);
         byte[] var3 = var1.getBytes(StandardCharsets.UTF_8);
         if (var2.length != var3.length) {
            return false;
         }

         int var4 = 0;

         for (int var5 = 0; var5 < var2.length; var5++) {
            var4 |= var2[var5] ^ var3[var5];
         }

         return var4 == 0;
      } else {
         return false;
      }
   }

   private static GuardRuntimeIdentity handle(GuardRuntimeIdentity var0, boolean var1) {
      if (var0 == null) {
         var0 = GuardRuntimeIdentity.handle();
      }

      if (var0.instance == null || var0.instance.isBlank()) {
         var0.instance = UUID.randomUUID().toString();
      }

      String var2 = var0.data == null ? "" : var0.data;
      if (!var2.isBlank() && !var2.equals("wild-1.21.8-1787661348375")) {
         var0.config = false;
         var0.state = 0L;
         var0.cache = 0;
         var0.output = 0;
         var0.current = "";
      }

      if (var1 && !var2.isBlank() && !var2.equals("wild-1.21.8-1787661348375") && handle(var0, "E4", var2)) {
         var0.config = false;
         var0.state = 0L;
         var0.cache = 0;
         var0.output = 0;
         var0.current = "";
      }

      var0.data = "wild-1.21.8-1787661348375";
      var0.context = Math.max(0L, var0.context);
      var0.state = Math.max(0L, var0.state);
      var0.cache = Math.max(0, var0.cache);
      var0.output = Math.max(0, var0.output);
      if (var0.current == null) {
         var0.current = "";
      }

      return var0;
   }

   private static boolean handle(GuardRuntimeIdentity var0, String var1, String var2) {
      String var3 = process(var2 + "|" + var1 + "|wild-fuse-v1");
      return handle(String.valueOf(var0.current), var3);
   }

   private static String update() {
      return process("wild|state|seal|2");
   }

   private static String apply() {
      String var0 = "-----BEGIN PUBLIC KEY-----\nMCowBQYDK2VwAyEAgqu9hOrz4JQKl2izQlnpj+d8jkT988LVfYfXPvKyt2Y=\n-----END PUBLIC KEY-----\n"
         .replace("-----BEGIN PUBLIC KEY-----", "")
         .replace("-----END PUBLIC KEY-----", "")
         .replaceAll("\\s+", "");
      return process(var0 + "|state|2");
   }

   private static String execute() {
      String var0 = "-----BEGIN PUBLIC KEY-----\nMCowBQYDK2VwAyEAgqu9hOrz4JQKl2izQlnpj+d8jkT988LVfYfXPvKyt2Y=\n-----END PUBLIC KEY-----\n"
         .replace("-----BEGIN PUBLIC KEY-----", "")
         .replace("-----END PUBLIC KEY-----", "")
         .replaceAll("\\s+", "");
      return process(var0 + "|state|1.21.8");
   }

   private static String handle(int var0) {
      return "wild-state-v" + var0;
   }

   record DataRecord(GuardRuntimeIdentity state, boolean legacy) {
   }
}
