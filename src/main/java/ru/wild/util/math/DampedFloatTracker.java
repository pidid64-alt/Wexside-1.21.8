package ru.wild.util.math;
import ru.wild.core.AnimationClock;

public final class DampedFloatTracker {
   private static final float instance = 0.004166667F;
   private static final float data = 0.25F;
   private static final int context = 60;
   private float config;
   private float state;
   private float cache;
   private long output = Long.MIN_VALUE;

   public DampedFloatTracker(float var1) {
      this.config = var1;
   }

   public float handle(float var1, SpringAnimationSpec var2) {
      AnimationClock var3 = AnimationClock.handle();
      long var4 = var3.resolve();
      if (var4 == this.output) {
         return this.config;
      }

      this.output = var4;
      float var6 = var3.compute();
      if (!Float.isFinite(var6) || var6 <= 0.0F) {
         var6 = 0.004166667F;
      } else if (var6 > 0.25F) {
         var6 = 0.25F;
      }

      this.cache += var6;
      int var7 = 0;

      while (this.cache >= 0.004166667F && var7 < 60) {
         this.compute(var1, var2);
         this.cache -= 0.004166667F;
         var7++;
         if (this.process(var1, var2)) {
            this.handle(var1);
            break;
         }
      }

      if (var7 == 60) {
         this.cache = 0.0F;
      }

      return this.config;
   }

   private void compute(float var1, SpringAnimationSpec var2) {
      this.state = this.state + ((var1 - this.config) * var2.load() - this.state * var2.save());
      this.config = this.config + this.state;
   }

   public void handle(float var1) {
      this.config = var1;
      this.state = 0.0F;
      this.cache = 0.0F;
   }

   public boolean process(float var1, SpringAnimationSpec var2) {
      return Math.abs(var1 - this.config) <= var2.submit() && Math.abs(this.state) <= var2.unload();
   }
   public float handle() {
      return this.config;
   }
   public float process() {
      return this.state;
   }
   public float compute() {
      return this.cache;
   }
   public long resolve() {
      return this.output;
   }
   public void process(float var1) {
      this.config = var1;
   }
   public void compute(float var1) {
      this.state = var1;
   }
   public void resolve(float var1) {
      this.cache = var1;
   }
   public void handle(long var1) {
      this.output = var1;
   }
}
