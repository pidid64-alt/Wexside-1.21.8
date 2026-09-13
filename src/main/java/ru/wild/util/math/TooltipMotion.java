package ru.wild.util.math;

public final class TooltipMotion {
   public static final float instance = 0.995F;
   public static final float data = 0.055F;
   public static final float context = 0.085F;
   public static final float config = 0.02F;
   public static final float state = 0.026F;
   public static final float cache = 0.009F;
   public static final float output = 0.055F;
   public static final float current = 0.24F;

   private TooltipMotion() {
   }

   public static float handle(float var0) {
      return 1.0F;
   }

   public static float process(float var0) {
      return handle(0.018F, 0.88F, var0);
   }

   public static float compute(float var0) {
      return handle(var0, 0.035F, 0.19F, 0.74F, 0.985F) * 0.055F;
   }

   public static float resolve(float var0) {
      return handle(var0, 0.025F, 0.17F, 0.68F, 0.975F) * 0.085F;
   }

   public static int update(float var0) {
      return 6;
   }

   public static float handle(float var0, float var1) {
      return 0.02F * update(var0, var1);
   }

   public static float process(float var0, float var1) {
      return 0.026F * update(var0, var1);
   }

   public static float compute(float var0, float var1) {
      float var2 = update(var0, var1);
      return 0.009F * var2 * var2;
   }

   public static float resolve(float var0, float var1) {
      return 0.24F * update(var0, var1);
   }

   private static float update(float var0, float var1) {
      return apply(var1) * handle(0.02F, 0.98F, var0);
   }

   private static float handle(float var0, float var1, float var2, float var3, float var4) {
      return handle(var1, var2, var0) * (1.0F - handle(var3, var4, var0));
   }

   private static float handle(float var0, float var1, float var2) {
      float var3 = apply((var2 - var0) / Math.max(1.0E-6F, var1 - var0));
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private static float apply(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }
}
