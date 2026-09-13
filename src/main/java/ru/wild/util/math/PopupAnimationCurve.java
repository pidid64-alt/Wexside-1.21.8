package ru.wild.util.math;

public final class PopupAnimationCurve {
   public static final FloatEasing instance = var0 -> {
      float var1 = handle(var0);
      float var2 = 1.0F - var1;
      return 1.0F - var2 * var2 * var2;
   };
   public static final FloatEasing data = var0 -> {
      float var1 = handle(var0);
      if (var1 < 0.5F) {
         float var4 = var1 * 2.0F;
         return 0.5F * var4 * var4 * var4 * var4 * var4;
      } else {
         float var2 = (var1 - 0.5F) * 2.0F;
         float var3 = 1.0F - var2;
         return 1.0F - 0.5F * var3 * var3 * var3 * var3 * var3;
      }
   };
   public static final FloatEasing context = var0 -> {
      float var1 = handle(var0);
      return var1 * var1 * (3.0F - 2.0F * var1);
   };

   private PopupAnimationCurve() {
   }

   private static float handle(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }
}
