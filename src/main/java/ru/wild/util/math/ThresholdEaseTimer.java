package ru.wild.util.math;

public class ThresholdEaseTimer extends SmoothTimer {
   private final float state;

   public ThresholdEaseTimer(int var1, double var2, float var4) {
      super(var1, var2);
      this.state = var4;
   }

   public ThresholdEaseTimer(int var1, double var2, float var4, AnimationDirection var5) {
      super(var1, var2, var5);
      this.state = var4;
   }

   @Override
   protected boolean execute() {
      return true;
   }

   @Override
   protected double process(double var1) {
      double var3 = var1 / this.data;
      float var5 = this.state + 1.0F;
      return Math.max(0.0, 1.0 + var5 * Math.pow(var3 - 1.0, 3.0) + this.state * Math.pow(var3 - 1.0, 2.0));
   }
}
