package ru.wild.util.math;

public class ExponentialEaseTimer extends SmoothTimer {
   public ExponentialEaseTimer(int var1, double var2) {
      super(var1, var2);
   }

   public ExponentialEaseTimer(int var1, double var2, AnimationDirection var4) {
      super(var1, var2, var4);
   }

   @Override
   protected double process(double var1) {
      double var3 = var1 / this.data;
      return -2.0 * Math.pow(var3, 3.0) + 3.0 * Math.pow(var3, 2.0);
   }
}
