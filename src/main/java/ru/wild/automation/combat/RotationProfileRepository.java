package ru.wild.automation.combat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import ru.wild.WildClient;
import ru.wild.core.MinecraftContext;

public final class RotationProfileRepository implements MinecraftContext {
   private static final Gson instance = new GsonBuilder().setPrettyPrinting().create();
   private final List<RotationProfileRepository.DataRecord> data = new ArrayList<>();
   private long context;
   private int config = -1;
   private long state = -1L;

   public RotationProfileDataset.CacheEntry handle(float var1, float var2, boolean var3) {
      this.handle(false);
      if (this.data.isEmpty()) {
         return null;
      }

      float var4 = (float)Math.hypot(Math.abs(var1), Math.abs(var2));
      float var5 = var4 <= 0.001F ? 1.0F : Math.abs(var1) / var4;
      String var6 = process(var4, var5, var3);
      return this.data.stream().min(Comparator.comparingDouble(var4x -> this.handle(var4x.pattern, var4, var5, var6))).map(var0 -> var0.pattern).orElse(null);
   }

   public int handle() {
      this.handle(false);
      return this.data.size();
   }

   public String process() {
      this.handle(false);
      return this.data.isEmpty() ? "Neuro: no assets" : "Neuro: " + this.data.size() + " patterns";
   }

   public void compute() {
      this.handle(true);
   }

   public static Path resolve() {
      return WildClient.instance != null && WildClient.instance.cache != null
         ? WildClient.instance.cache.toPath().resolve("rotation_assets")
         : toggleState.runDirectory.toPath().resolve("Wild").resolve("rotation_assets");
   }

   public static Path handle(String var0) {
      String var1 = process(var0);
      return resolve().resolve(var1 + ".json");
   }

   public static RotationProfileDataset handle(Path var0) {
      if (!Files.isRegularFile(var0)) {
         return null;
      }

      try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         RotationProfileDataset var2 = (RotationProfileDataset)instance.fromJson(var1, RotationProfileDataset.class);
         handle(var2, compute(var0.getFileName().toString()));
         return var2;
      } catch (Throwable var6) {
         return null;
      }
   }

   public static void handle(Path var0, RotationProfileDataset var1) {
      try {
         Files.createDirectories(var0.getParent());
         handle(var1, compute(var0.getFileName().toString()));

         try (BufferedWriter var2 = Files.newBufferedWriter(var0, StandardCharsets.UTF_8)) {
            instance.toJson(var1, var2);
         }
      } catch (Throwable var7) {
      }
   }

   public static String process(String var0) {
      String var1 = var0 != null && !var0.isBlank() ? var0.trim() : "rotation_lab";
      var1 = var1.replace('\\', '/');
      int var2 = var1.lastIndexOf(47);
      if (var2 >= 0) {
         var1 = var1.substring(var2 + 1);
      }

      if (var1.endsWith(".json")) {
         var1 = var1.substring(0, var1.length() - 5);
      }

      var1 = var1.replaceAll("[^a-zA-Z0-9._-]", "_");
      if (var1.isBlank() || var1.equals(".") || var1.equals("..")) {
         var1 = "rotation_lab";
      }

      return var1;
   }

   private void handle(boolean var1) {
      long var2 = System.currentTimeMillis();
      if (var1 || var2 - this.context >= 1500L) {
         this.context = var2;
         Path var4 = resolve();
         int var5 = 0;
         long var6 = 0L;

         try {
            if (Files.isDirectory(var4)) {
               try (Stream<Path> var8 = Files.list(var4)) {
                  List<Path> var9 = var8.filter(var0 -> Files.isRegularFile(var0) && var0.getFileName().toString().endsWith(".json")).toList();
                  var5 = var9.size();

                  for (Path var11 : var9) {
                     var6 += Files.getLastModifiedTime(var11).toMillis();
                  }
               }
            }
         } catch (Throwable var17) {
         }

         if (var1 || var5 != this.config || var6 != this.state) {
            this.config = var5;
            this.state = var6;
            this.data.clear();
            if (var5 != 0) {
               try (Stream<Path> var18 = Files.list(var4)) {
                  var18.filter(var0 -> Files.isRegularFile(var0) && var0.getFileName().toString().endsWith(".json")).forEach(this::process);
               } catch (Throwable var15) {
               }
            }
         }
      }
   }

   private void process(Path var1) {
      RotationProfileDataset var2 = handle(var1);
      if (var2 != null && var2.cache != null) {
         for (RotationProfileDataset.CacheEntry var4 : var2.cache) {
            if (handle(var4)) {
               this.data.add(new RotationProfileRepository.DataRecord(var1.getFileName().toString(), var4));
            }
         }
      }
   }

   private static void handle(RotationProfileDataset var0, String var1) {
      if (var0 != null) {
         var0.instance = 1;
         if (var0.config == null || var0.config.isBlank()) {
            var0.config = var1;
         }

         if (var0.cache == null) {
            var0.cache = new ArrayList<>();
         }

         var0.cache.removeIf(var0x -> !handle(var0x));

         for (RotationProfileDataset.CacheEntry var3 : var0.cache) {
            if (var3.instance == null || var3.instance.isBlank()) {
               var3.instance = "Mixed";
            }

            var3.enabled.sort(Comparator.comparingInt(var0x -> var0x.instance));
            var3.active = Math.max(var3.active, var3.enabled.get(var3.enabled.size() - 1).instance + 1);
            RotationProfileDataset.PrimaryCacheEntry var4 = var3.enabled.get(var3.enabled.size() - 1);
            var3.state = Math.abs(var3.state) > 0.001F ? var3.state : var4.data;
            var3.cache = Math.abs(var3.cache) > 0.001F ? var3.cache : var4.context;
            var3.selection = Math.max(0.0F, Math.min(1.0F, var3.selection));
         }
      }
   }

   private static boolean handle(RotationProfileDataset.CacheEntry var0) {
      return var0 != null && var0.enabled != null && var0.enabled.size() >= 2;
   }

   private double handle(RotationProfileDataset.CacheEntry var1, float var2, float var3, String var4) {
      float var5 = Math.max(0.001F, var1.handle());
      float var6 = Math.abs(var1.state) / var5;
      double var7 = Math.abs(var5 - var2) * 0.85;
      double var9 = Math.abs(var6 - var3) * 12.0;
      double var11 = handle(var1.instance, var4);
      double var13 = (1.0 - var1.selection) * 5.0;
      double var15 = ThreadLocalRandom.current().nextDouble(0.0, 1.35);
      return var7 + var9 + var11 + var13 + var15;
   }

   private static double handle(String var0, String var1) {
      String var2 = var0 == null ? "" : var0.toLowerCase(Locale.ROOT);
      String var3 = var1 == null ? "" : var1.toLowerCase(Locale.ROOT);
      if (var2.equals(var3) || var2.equals("mixed")) {
         return 0.0;
      } else if (var3.equals("mixed")) {
         return 1.0;
      } else {
         return !var2.contains(var3) && !var3.contains(var2) ? 3.0 : 0.75;
      }
   }

   private static String process(float var0, float var1, boolean var2) {
      if (var2) {
         return "Attack";
      } else if (var0 < 6.0F) {
         return "Micro";
      } else if (var1 < 0.35F) {
         return "Vertical";
      } else {
         return var0 > 28.0F ? "Flick" : "Tracking";
      }
   }

   private static String compute(String var0) {
      return var0 != null && var0.endsWith(".json") ? var0.substring(0, var0.length() - 5) : var0;
   }

   record DataRecord(String file, RotationProfileDataset.CacheEntry pattern) {
   }
}
