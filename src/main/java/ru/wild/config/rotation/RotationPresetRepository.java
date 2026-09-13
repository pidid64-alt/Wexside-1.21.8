package ru.wild.config.rotation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import ru.wild.WildClient;

public final class RotationPresetRepository {
   private static final Gson instance = new GsonBuilder().setPrettyPrinting().create();
   private static final RotationPresetRepository data = new RotationPresetRepository();
   private final List<RotationPresetRepository.NamedEntry> context = new ArrayList<>();
   private boolean config;

   private RotationPresetRepository() {
   }

   public static RotationPresetRepository handle() {
      return data;
   }

   public synchronized List<RotationPresetRepository.NamedEntry> process() {
      this.compute();
      return List.copyOf(this.context);
   }

   public synchronized RotationPresetRepository.NamedEntry handle(String var1, RotationPreset var2) {
      this.compute();
      String var3 = resolve(var1);
      if (!var3.isEmpty() && var2 != null) {
         long var4 = System.currentTimeMillis();
         RotationPresetRepository.NamedEntry var6 = new RotationPresetRepository.NamedEntry(UUID.randomUUID().toString(), var3, var2.update(), var4, var4);
         this.context.add(0, var6);
         this.resolve();
         return var6;
      } else {
         return null;
      }
   }

   public synchronized RotationPresetRepository.NamedEntry handle(String var1, String var2, RotationPreset var3) {
      this.compute();
      String var4 = resolve(var2);
      if (var1 != null && !var4.isEmpty() && var3 != null) {
         for (int var5 = 0; var5 < this.context.size(); var5++) {
            RotationPresetRepository.NamedEntry var6 = this.context.get(var5);
            if (var6.id().equals(var1)) {
               RotationPresetRepository.NamedEntry var7 = new RotationPresetRepository.NamedEntry(
                  var6.id(), var4, var3.update(), var6.createdAt(), System.currentTimeMillis()
               );
               this.context.set(var5, var7);
               this.update();
               this.resolve();
               return var7;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public synchronized boolean handle(String var1) {
      RotationPresetRepository.NamedEntry var2 = this.compute(var1);
      return var2 != null && RotationPreset.process(var2.key());
   }

   public synchronized boolean process(String var1) {
      this.compute();
      boolean var2 = this.context.removeIf(var1x -> var1x.id().equals(var1));
      if (var2) {
         this.resolve();
      }

      return var2;
   }

   public synchronized RotationPresetRepository.NamedEntry compute(String var1) {
      this.compute();
      if (var1 == null) {
         return null;
      }

      for (RotationPresetRepository.NamedEntry var3 : this.context) {
         if (var3.id().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   private void compute() {
      if (!this.config) {
         File var1 = apply();
         if (var1 != null) {
            this.config = true;
            if (var1.isFile()) {
               try {
                  String var2 = Files.readString(var1.toPath(), StandardCharsets.UTF_8);
                  RotationPresetRepository.DataRecord var3 = (RotationPresetRepository.DataRecord)instance.fromJson(
                     var2, RotationPresetRepository.DataRecord.class
                  );
                  if (var3 == null || var3.presets == null) {
                     return;
                  }

                  for (RotationPresetRepository.NamedEntry var5 : var3.presets) {
                     RotationPresetRepository.NamedEntry var6 = handle(var5);
                     if (var6 != null && this.context.stream().noneMatch(var1x -> var1x.id().equals(var6.id()))) {
                        this.context.add(var6);
                     }
                  }

                  this.update();
               } catch (Throwable var7) {
               }
            }
         }
      }
   }

   private void resolve() {
      File var1 = apply();
      if (var1 != null) {
         try {
            File var2 = var1.getParentFile();
            if (var2 != null) {
               Files.createDirectories(var2.toPath());
            }

            Files.writeString(var1.toPath(), instance.toJson(new RotationPresetRepository.DataRecord(1, this.context)), StandardCharsets.UTF_8);
         } catch (Throwable var3) {
         }
      }
   }

   private void update() {
      this.context.sort(Comparator.comparingLong(RotationPresetRepository.NamedEntry::updatedAt).reversed());
   }

   private static RotationPresetRepository.NamedEntry handle(RotationPresetRepository.NamedEntry var0) {
      if (var0 != null && var0.key() != null && !var0.key().isBlank()) {
         String var1 = resolve(var0.name());
         if (var1.isEmpty()) {
            return null;
         }

         String var2 = var0.id() != null && !var0.id().isBlank() ? var0.id() : UUID.randomUUID().toString();
         long var3 = Math.max(0L, var0.createdAt());
         long var5 = Math.max(var3, var0.updatedAt());
         return new RotationPresetRepository.NamedEntry(var2, var1, var0.key().trim(), var3, var5);
      } else {
         return null;
      }
   }

   private static String resolve(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.replaceAll("\\p{Cntrl}", "").trim().replaceAll("\\s{2,}", " ");
      return var1.length() > 40 ? var1.substring(0, 40).trim() : var1;
   }

   private static File apply() {
      return WildClient.instance != null && WildClient.instance.cache != null ? new File(WildClient.instance.cache, "custom-rotation-presets.json") : null;
   }

   record DataRecord(int version, List<RotationPresetRepository.NamedEntry> presets) {
   }

   public record NamedEntry(String id, String name, String key, long createdAt, long updatedAt) {
   }
}
