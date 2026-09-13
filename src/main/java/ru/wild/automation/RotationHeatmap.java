package ru.wild.automation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RotationHeatmap {
   private static final Gson enabled = new GsonBuilder().create();
   public int instance;
   public int data;
   public int context;
   public float config;
   public float state;
   public float cache;
   public float output;
   public float current;
   public RotationTrainingSampler active;
   public float[][] mode;
   public float[][] selection;

   public RotationHeatmap() {
   }

   public RotationHeatmap(int var1, int var2, int var3, RotationTrainingSampler var4, float[][] var5, float[][] var6) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.active = var4;
      this.mode = var5;
      this.selection = var6;
   }

   public boolean handle(int var1, int var2) {
      return this.active != null && this.active.handle(var1, var2) && this.instance == var1 && this.data == var2 && this.mode != null && this.selection != null;
   }

   public float process(int var1, int var2) {
      return handle(this.mode, var1, var2);
   }

   public float compute(int var1, int var2) {
      return handle(this.selection, var1, var2);
   }

   public int handle(int var1) {
      return this.mode != null && var1 >= 0 && var1 < this.mode.length && this.mode[var1] != null ? this.mode[var1].length : 0;
   }

   private static float handle(float[][] var0, int var1, int var2) {
      if (var0 != null && var1 >= 0 && var1 < var0.length) {
         float[] var3 = var0[var1];
         return var3 != null && var3.length != 0 ? var3[Math.floorMod(var2, var3.length)] : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public boolean handle(Path var1) {
      try {
         Files.createDirectories(var1.getParent());

         try (BufferedWriter var2 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8)) {
            enabled.toJson(this, var2);
         }

         return true;
      } catch (Throwable var7) {
         return false;
      }
   }

   public static RotationHeatmap process(Path var0) {
      try {
         if (!Files.isRegularFile(var0)) {
            return null;
         }

         try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            return (RotationHeatmap)enabled.fromJson(var1, RotationHeatmap.class);
         }
      } catch (Throwable var6) {
         return null;
      }
   }
}
