package ru.wild.gui.widget;

public final class PopupPlacementEngine {
   private static final float instance = 0.001F;
   private final float data;
   private final float context;
   private final float config;

   public PopupPlacementEngine(float var1, float var2, float var3) {
      if (!Float.isFinite(var1) || var1 < 0.0F) {
         throw new IllegalArgumentException("positionMargin must be a non-negative finite value");
      }

      if (!Float.isFinite(var2) || var2 < 0.0F) {
         throw new IllegalArgumentException("cursorHorizontalOffset must be a non-negative finite value");
      }

      if (Float.isFinite(var3) && !(var3 < 0.0F)) {
         this.data = var1;
         this.context = var2;
         this.config = var3;
      } else {
         throw new IllegalArgumentException("cursorVerticalOffset must be a non-negative finite value");
      }
   }

   public PopupPlacementEngine.DataRecord handle(double var1, double var3, float var5, float var6, int var7, int var8) {
      return this.handle(var1, var3, var5, var6, var7, var8, 1.0F);
   }

   public PopupPlacementEngine.DataRecord handle(double var1, double var3, float var5, float var6, int var7, int var8, float var9) {
      float var10 = Math.max(1, var7);
      float var11 = Math.max(1, var8);
      float var12 = Math.max(0.0F, var5);
      float var13 = Math.max(0.0F, var6);
      float var14 = handle(var9);
      float var15 = this.handle(var1, var10);
      float var16 = this.handle(var3, var11);
      float var17 = this.handle(var15, var12, var10, var14);
      float var18 = this.process(var16, var13, var11, var14);
      return new PopupPlacementEngine.DataRecord(var17, var18);
   }

   private float handle(float var1, float var2, float var3, float var4) {
      float var5 = this.data;
      float var6 = process(var1, var5, Math.max(var5, var3 - var5));
      float var7 = this.handle(var3, var5, var4);
      float var8 = this.compute(var2, var3, var5, var4);
      if (var8 < var7) {
         float var12 = var5;
         float var13 = Math.max(var5, var3 - var5 - var2);
         float var14 = var3 * 0.5F - var2 * 0.5F;
         return process(var14, var12, var13);
      }

      float var9 = var6 + this.context;
      if (var9 >= var7 && var9 <= var8) {
         return var9;
      }

      float var10 = var6 - this.context - var2;
      if (var10 >= var7 && var10 <= var8) {
         return var10;
      }

      float var11 = var6 - var2 * 0.5F;
      return process(var11, var7, var8);
   }

   private float process(float var1, float var2, float var3, float var4) {
      float var5 = this.data;
      float var6 = process(var1, var5, Math.max(var5, var3 - var5));
      float var7 = this.handle(var3, var5, var4);
      float var8 = this.resolve(var2, var3, var5, var4);
      if (var8 < var7) {
         float var12 = var5;
         float var13 = Math.max(var5, var3 - var5 - var2);
         float var14 = var3 * 0.5F - var2 * 0.5F;
         return process(var14, var12, var13);
      }

      float var9 = var6 + this.config;
      if (var9 >= var7 && var9 <= var8) {
         return var9;
      }

      float var10 = var6 - this.config - var2;
      if (var10 >= var7 && var10 <= var8) {
         return var10;
      }

      float var11 = var6 - var2 * 0.5F;
      return process(var11, var7, var8);
   }

   private float handle(float var1, float var2, float var3) {
      float var4 = handle(var3);
      if (Float.isFinite(var4) && !(var4 <= 0.001F)) {
         float var5 = var1 * 0.5F;
         return var5 + (var2 - var5) / var4;
      } else {
         return var2;
      }
   }

   private float compute(float var1, float var2, float var3, float var4) {
      float var5 = handle(var4);
      if (Float.isFinite(var5) && !(var5 <= 0.001F)) {
         float var6 = var2 * 0.5F;
         float var7 = var2 - var3;
         return var6 + (var7 - var6) / var5 - var1;
      } else {
         return var2 - var3 - var1;
      }
   }

   private float resolve(float var1, float var2, float var3, float var4) {
      float var5 = handle(var4);
      if (Float.isFinite(var5) && !(var5 <= 0.001F)) {
         float var6 = var2 * 0.5F;
         float var7 = var2 - var3;
         return var6 + (var7 - var6) / var5 - var1;
      } else {
         return var2 - var3 - var1;
      }
   }

   private float handle(double var1, float var3) {
      float var4 = handle(var1);
      if (Float.isNaN(var4)) {
         return var3 * 0.5F;
      }

      float var5 = this.data;
      return process(var4, var5, Math.max(var5, var3 - var5));
   }

   private static float handle(double var0) {
      if (!Double.isFinite(var0)) {
         return Float.NaN;
      } else if (var0 > Float.MAX_VALUE) {
         return Float.MAX_VALUE;
      } else {
         return var0 < -Float.MAX_VALUE ? -Float.MAX_VALUE : (float)var0;
      }
   }

   private static float handle(float var0) {
      if (!Float.isFinite(var0)) {
         return 1.0F;
      } else {
         return var0 <= 0.001F ? 1.0F : var0;
      }
   }

   private static float process(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public record DataRecord(float x, float y) {
      public DataRecord(float x, float y) {
         if (Float.isFinite(x) && Float.isFinite(y)) {
            this.x = x;
            this.y = y;
         } else {
            throw new IllegalArgumentException("Popup placement coordinates must be finite");
         }
      }
   }
}
