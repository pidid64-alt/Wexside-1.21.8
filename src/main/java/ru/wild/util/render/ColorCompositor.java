package ru.wild.util.render;

public class ColorCompositor {
   public int handle(int var1, int var2, double var3) {
      if (var3 < 0.0) {
         var3 = 0.0;
      }

      if (var3 > 1.0) {
         var3 = 1.0;
      }

      int var5 = var1 >> 24 & 0xFF;
      int var6 = var2 >> 24 & 0xFF;
      if (var5 == 0) {
         var5 = 255;
      }

      if (var6 == 0) {
         var6 = 255;
      }

      int var7 = (int)Math.round(var5 + (var6 - var5) * var3);
      return var7 << 24 | PackedColor.resolve(var1, var2, (float)var3);
   }

   public int handle(int var1, int var2, int var3) {
      return this.handle(var1, var2, var3, 255);
   }

   public static int handle(int var0, double var1) {
      int var3 = (int)Math.round(var1 * 255.0);
      int var4 = var0 & 16777215;
      return var3 << 24 | var4;
   }

   public int handle(int var1, int var2, int var3, int var4) {
      return (var4 & 0xFF) << 24 | (var1 & 0xFF) << 16 | (var2 & 0xFF) << 8 | var3 & 0xFF;
   }

   public static int process(int var0, int var1, int var2, int var3) {
      return (var3 & 0xFF) << 24 | (var0 & 0xFF) << 16 | (var1 & 0xFF) << 8 | var2 & 0xFF;
   }

   public static int handle(int var0) {
      return var0 >>> 24 & 0xFF;
   }

   public static int process(int var0) {
      return var0 >>> 16 & 0xFF;
   }

   public static int compute(int var0) {
      return var0 >>> 8 & 0xFF;
   }

   public static int resolve(int var0) {
      return var0 & 0xFF;
   }

   public static int handle(int var0, int var1, float var2) {
      float var3 = var2;
      if (var3 <= 0.0F) {
         return var0;
      }

      if (var3 >= 1.0F) {
         return var1;
      }

      int var4 = var0 >>> 24 & 0xFF;
      int var5 = var1 >>> 24 & 0xFF;
      int var6 = Math.round(var4 + (var5 - var4) * var3);
      return (var6 & 0xFF) << 24 | PackedColor.resolve(var0, var1, var3);
   }
}
