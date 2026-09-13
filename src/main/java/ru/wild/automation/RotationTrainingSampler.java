package ru.wild.automation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public final class RotationTrainingSampler {
   private static final Gson config = new GsonBuilder().create();
   private static final float state = 0.9F;
   private static final float cache = 0.999F;
   private static final float output = 1.0E-8F;
   public int[] instance;
   public float[][][] data;
   public float[][] context;
   private transient float[][] current;
   private transient float[][] active;
   private transient float[][][] mode;
   private transient float[][][] selection;
   private transient float[][] enabled;
   private transient float[][] renderer;
   private transient int handler;
   private transient float animationDraw;
   private transient float pointEncode;

   public RotationTrainingSampler() {
   }

   public RotationTrainingSampler(int... var1) {
      this.instance = (int[])var1.clone();
      int var2 = var1.length - 1;
      this.data = new float[var2][][];
      this.context = new float[var2][];

      for (int var3 = 0; var3 < var2; var3++) {
         int var4 = var1[var3];
         int var5 = var1[var3 + 1];
         this.data[var3] = new float[var5][var4];
         this.context[var3] = new float[var5];
         float var6 = (float)Math.sqrt(6.0 / (var4 + var5));

         for (int var7 = 0; var7 < var5; var7++) {
            for (int var8 = 0; var8 < var4; var8++) {
               this.data[var3][var7][var8] = (ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F) * var6;
            }
         }
      }
   }

   public boolean handle() {
      return this.instance != null && this.instance.length >= 2 && this.data != null && this.context != null;
   }

   public boolean handle(int var1, int var2) {
      return this.handle() && this.instance[0] == var1 && this.instance[this.instance.length - 1] == var2;
   }

   private void process() {
      if (this.current == null) {
         this.current = new float[this.instance.length][];

         for (int var1 = 0; var1 < this.instance.length; var1++) {
            this.current[var1] = new float[this.instance[var1]];
         }
      }
   }

   public float[] handle(float[] var1) {
      this.process();
      System.arraycopy(var1, 0, this.current[0], 0, this.instance[0]);
      int var2 = this.data.length;

      for (int var3 = 0; var3 < var2; var3++) {
         float[] var4 = this.current[var3];
         float[] var5 = this.current[var3 + 1];
         float[][] var6 = this.data[var3];
         float[] var7 = this.context[var3];
         boolean var8 = var3 == var2 - 1;

         for (int var9 = 0; var9 < var5.length; var9++) {
            float var10 = var7[var9];
            float[] var11 = var6[var9];

            for (int var12 = 0; var12 < var4.length; var12++) {
               var10 += var11[var12] * var4[var12];
            }

            var5[var9] = var8 ? var10 : (float)Math.tanh(var10);
         }
      }

      return this.current[this.instance.length - 1];
   }

   public void handle(float[][] var1, float[][] var2, int var3, float var4) {
      this.process();
      this.compute();
      int var5 = var1.length;
      int[] var6 = new int[var5];
      int var7 = 0;

      while (var7 < var5) {
         var6[var7] = var7++;
      }

      var7 = this.data.length;

      for (int var8 = 0; var8 < var3; var8++) {
         handle(var6);

         for (int var9 = 0; var9 < var5; var9++) {
            int var10 = var6[var9];
            this.handle(var1[var10]);
            this.handler++;
            this.animationDraw *= 0.9F;
            this.pointEncode *= 0.999F;
            float[] var11 = this.current[var7];
            float[] var12 = this.active[var7];
            float[] var13 = var2[var10];

            for (int var14 = 0; var14 < var11.length; var14++) {
               var12[var14] = var11[var14] - var13[var14];
            }

            for (int var33 = var7 - 1; var33 >= 1; var33--) {
               float[] var15 = this.active[var33];
               float[] var16 = this.active[var33 + 1];
               float[][] var17 = this.data[var33];
               float[] var18 = this.current[var33];

               for (int var19 = 0; var19 < var15.length; var19++) {
                  var15[var19] = 0.0F;
               }

               for (int var39 = 0; var39 < var16.length; var39++) {
                  float var20 = var16[var39];
                  float[] var21 = var17[var39];

                  for (int var22 = 0; var22 < var15.length; var22++) {
                     var15[var22] += var20 * var21[var22];
                  }
               }

               for (int var40 = 0; var40 < var15.length; var40++) {
                  float var42 = var18[var40];
                  var15[var40] *= 1.0F - var42 * var42;
               }
            }

            float var34 = 1.0F / (1.0F - this.animationDraw);
            float var35 = 1.0F / (1.0F - this.pointEncode);

            for (int var36 = 0; var36 < var7; var36++) {
               float[] var37 = this.current[var36];
               float[] var38 = this.active[var36 + 1];
               float[][] var41 = this.data[var36];
               float[] var43 = this.context[var36];
               float[][] var44 = this.mode[var36];
               float[][] var45 = this.selection[var36];
               float[] var23 = this.enabled[var36];
               float[] var24 = this.renderer[var36];

               for (int var25 = 0; var25 < var38.length; var25++) {
                  float var26 = var38[var25];
                  var23[var25] = 0.9F * var23[var25] + 0.100000024F * var26;
                  var24[var25] = 0.999F * var24[var25] + 9.999871E-4F * var26 * var26;
                  var43[var25] -= var4 * (var23[var25] * var34) / ((float)Math.sqrt(var24[var25] * var35) + 1.0E-8F);
                  float[] var27 = var41[var25];
                  float[] var28 = var44[var25];
                  float[] var29 = var45[var25];

                  for (int var30 = 0; var30 < var37.length; var30++) {
                     float var31 = var26 * var37[var30];
                     var28[var30] = 0.9F * var28[var30] + 0.100000024F * var31;
                     var29[var30] = 0.999F * var29[var30] + 9.999871E-4F * var31 * var31;
                     var27[var30] -= var4 * (var28[var30] * var34) / ((float)Math.sqrt(var29[var30] * var35) + 1.0E-8F);
                  }
               }
            }
         }
      }
   }

   public float handle(float[][] var1, float[][] var2) {
      this.process();
      double var3 = 0.0;

      for (int var5 = 0; var5 < var1.length; var5++) {
         float[] var6 = this.handle(var1[var5]);
         float[] var7 = var2[var5];

         for (int var8 = 0; var8 < var6.length; var8++) {
            float var9 = var6[var8] - var7[var8];
            var3 += var9 * var9;
         }
      }

      return (float)(var3 / Math.max(1, var1.length));
   }

   private void compute() {
      this.active = new float[this.instance.length][];

      for (int var1 = 0; var1 < this.instance.length; var1++) {
         this.active[var1] = new float[this.instance[var1]];
      }

      int var5 = this.data.length;
      this.mode = new float[var5][][];
      this.selection = new float[var5][][];
      this.enabled = new float[var5][];
      this.renderer = new float[var5][];

      for (int var2 = 0; var2 < var5; var2++) {
         int var3 = this.instance[var2 + 1];
         int var4 = this.instance[var2];
         this.mode[var2] = new float[var3][var4];
         this.selection[var2] = new float[var3][var4];
         this.enabled[var2] = new float[var3];
         this.renderer[var2] = new float[var3];
      }

      this.handler = 0;
      this.animationDraw = 1.0F;
      this.pointEncode = 1.0F;
   }

   private static void handle(int[] var0) {
      for (int var1 = var0.length - 1; var1 > 0; var1--) {
         int var2 = ThreadLocalRandom.current().nextInt(var1 + 1);
         int var3 = var0[var1];
         var0[var1] = var0[var2];
         var0[var2] = var3;
      }
   }

   public boolean handle(Path var1) {
      try {
         Files.createDirectories(var1.getParent());

         try (BufferedWriter var2 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8)) {
            config.toJson(this, var2);
         }

         return true;
      } catch (Throwable var7) {
         return false;
      }
   }

   public static RotationTrainingSampler process(Path var0) {
      try {
         if (!Files.isRegularFile(var0)) {
            return null;
         }

         try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            RotationTrainingSampler var2 = (RotationTrainingSampler)config.fromJson(var1, RotationTrainingSampler.class);
            return var2 != null && var2.handle() ? var2 : null;
         }
      } catch (Throwable var6) {
         return null;
      }
   }
}
