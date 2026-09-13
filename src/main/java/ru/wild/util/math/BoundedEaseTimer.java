package ru.wild.util.math;

public class BoundedEaseTimer extends SmoothTimer {
   float state;
   float cache;
   boolean output;

   public BoundedEaseTimer(int var1, double var2, float var4, float var5, boolean var6) {
      super(var1, var2);
      this.state = var4;
      this.cache = var5;
      this.output = var6;
   }

   public BoundedEaseTimer(int var1, double var2, float var4, float var5, boolean var6, AnimationDirection var7) {
      super(var1, var2, var7);
      this.state = var4;
      this.cache = var5;
      this.output = var6;
   }

   @Override
   protected double process(double var1) {
      double var3 = Math.pow(var1 / this.data, this.cache);
      double var5 = this.state * 0.1F;
      return Math.pow(2.0, -10.0 * (this.output ? Math.sqrt(var3) : var3)) * Math.sin((var3 - var5 / 4.0) * ((Math.PI * 2) / var5)) + 1.0;
   }
}
