package ru.wild.util.math;

public class EaseTimer extends SmoothTimer {
   public EaseTimer(int var1, double var2) {
      super(var1, var2);
   }

   public EaseTimer(int var1, double var2, AnimationDirection var4) {
      super(var1, var2, var4);
   }

   @Override
   protected double process(double var1) {
      double var3 = var1 / this.data;
      return var3 < 0.5 ? 2.0 * Math.pow(var3, 2.0) : 1.0 - Math.pow(-2.0 * var3 + 2.0, 2.0) / 2.0;
   }
}
