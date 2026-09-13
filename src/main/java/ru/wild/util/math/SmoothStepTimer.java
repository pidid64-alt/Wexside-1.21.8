package ru.wild.util.math;

public class SmoothStepTimer extends SmoothTimer {
   public SmoothStepTimer(int var1, double var2) {
      super(var1, var2);
   }

   public SmoothStepTimer(int var1, double var2, AnimationDirection var4) {
      super(var1, var2, var4);
   }

   @Override
   protected double process(double var1) {
      double var3 = var1 / this.data;
      return 1.0 - (var3 - 1.0) * (var3 - 1.0);
   }
}
