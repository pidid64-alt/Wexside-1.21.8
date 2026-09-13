package ru.wild.util.math;

public final class SpringParameters {
   private final float instance;
   private final float data;

   private SpringParameters(float var1, float var2) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException("frequencyHz must be > 0");
      }

      if (var2 <= 0.0F) {
         throw new IllegalArgumentException("dampingRatio must be > 0");
      }

      this.instance = var1;
      this.data = var2;
   }

   public static SpringParameters handle(float var0, float var1) {
      return new SpringParameters(var0, var1);
   }

   public float handle() {
      return this.instance;
   }

   public float process() {
      return this.data;
   }
}
