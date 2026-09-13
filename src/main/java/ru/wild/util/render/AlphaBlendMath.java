package ru.wild.util.render;

public final class AlphaBlendMath {
   private AlphaBlendMath() {
   }

   public static int handle(int var0, int var1) {
      float var2 = (var0 >>> 24 & 0xFF) / 255.0F;
      float var3 = (var1 >>> 24 & 0xFF) / 255.0F;
      float var4 = var2 + var3 * (1.0F - var2);
      if (var4 <= 1.0E-4F) {
         return 0;
      }

      int var5 = handle(var0 >>> 16 & 0xFF, var2, var1 >>> 16 & 0xFF, var3, var4);
      int var6 = handle(var0 >>> 8 & 0xFF, var2, var1 >>> 8 & 0xFF, var3, var4);
      int var7 = handle(var0 & 0xFF, var2, var1 & 0xFF, var3, var4);
      return Math.round(var4 * 255.0F) << 24 | var5 << 16 | var6 << 8 | var7;
   }

   public static int handle(int var0, float var1) {
      float var2 = Math.max(0.0F, Math.min(1.0F, var1));
      int var3 = Math.round((var0 >>> 24 & 0xFF) * var2);
      return var3 << 24 | var0 & 16777215;
   }

   private static int handle(int var0, float var1, int var2, float var3, float var4) {
      double var5 = handle(var0 / 255.0);
      double var7 = handle(var2 / 255.0);
      double var9 = (var5 * var1 + var7 * var3 * (1.0F - var1)) / var4;
      return Math.max(0, Math.min(255, (int)Math.round(process(var9) * 255.0)));
   }

   private static double handle(double var0) {
      return var0 <= 0.04045 ? var0 / 12.92 : Math.pow((var0 + 0.055) / 1.055, 2.4);
   }

   private static double process(double var0) {
      double var2 = Math.max(0.0, Math.min(1.0, var0));
      return var2 <= 0.0031308 ? var2 * 12.92 : 1.055 * Math.pow(var2, 0.4166666666666667) - 0.055;
   }
}
