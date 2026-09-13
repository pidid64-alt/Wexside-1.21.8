package ru.wild.util.math;
import ru.wild.core.AnimationClock;

public final class SpringAnimation {
   private static final float instance = 0.004166667F;
   private static final float data = 1.0E-4F;
   private static final float context = 0.016666668F;
   private static final float config = 0.25F;
   private static final int state = 60;
   private float cache;
   private float output;
   private float current;
   private long active = Long.MIN_VALUE;

   public SpringAnimation(float var1) {
      this.cache = var1;
   }

   public float handle(float var1, SpringAnimationSpec var2) {
      float var3 = this.apply();
      if (var3 < 0.0F) {
         return this.cache;
      }

      SpringAnimationSpec var4 = var2 == null ? SpringAnimationSpec.handle() : var2;
      this.current += var3;
      int var5 = 0;

      while (this.current >= 0.004166667F && var5 < 60) {
         this.compute(var1, var4);
         this.current -= 0.004166667F;
         var5++;
         if (this.process(var1, var4)) {
            this.handle(var1);
            break;
         }
      }

      if (var5 == 60) {
         this.current = 0.0F;
      }

      return this.cache;
   }

   public float handle(float var1, SpringAnimation.State var2) {
      float var3 = this.apply();
      if (var3 < 0.0F) {
         return this.cache;
      }

      SpringAnimation.State var4 = var2 == null ? SpringAnimation.State.handle() : var2;
      float var5 = this.cache;
      this.cache = handle(this.cache, var1, var3, var4.instance);
      this.output = (this.cache - var5) / Math.max(var3, 1.0E-4F);
      this.output = this.output * (float)Math.exp(-var4.data * var3);
      this.current = 0.0F;
      if (Math.abs(var1 - this.cache) <= var4.context && Math.abs(this.output) <= var4.config) {
         this.handle(var1);
      }

      return this.cache;
   }

   public void handle(float var1) {
      this.cache = var1;
      this.output = 0.0F;
      this.current = 0.0F;
   }

   public boolean process(float var1, SpringAnimationSpec var2) {
      SpringAnimationSpec var3 = var2 == null ? SpringAnimationSpec.handle() : var2;
      return Math.abs(var1 - this.cache) <= var3.submit() && Math.abs(this.output) <= var3.unload();
   }

   public static float handle() {
      AnimationClock var0 = AnimationClock.handle();
      float var1 = var0.compute();
      if (!Float.isFinite(var1) || var1 <= 0.0F) {
         return 0.016666668F;
      } else {
         return var1 < 1.0E-4F ? 1.0E-4F : Math.min(0.25F, var1);
      }
   }

   public static float handle(float var0, float var1, float var2, float var3) {
      float var4 = apply(var2);
      float var5 = 1.0F - (float)Math.exp(-Math.max(0.001F, var3) * var4);
      return var0 + (var1 - var0) * var5;
   }

   public static float process(float var0, float var1, float var2, float var3) {
      float var4 = 1.0F - (float)Math.exp(-Math.max(0.0F, var2) / Math.max(1.0F, var3));
      return var0 + (var1 - var0) * var4;
   }

   public static float handle(float var0, float var1, float var2) {
      return var0 * (float)Math.exp(-Math.max(0.001F, var2) * apply(var1));
   }

   public static float process(float var0, float var1, float var2) {
      return Math.abs(var0) <= 1.0E-6F ? 0.0F : var0 * (float)Math.exp(-Math.max(0.0F, var1) / Math.max(1.0F, var2));
   }

   public static float process(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   public static float compute(float var0, float var1, float var2) {
      float var3 = process(var2);
      return var0 + (var1 - var0) * var3;
   }

   private void compute(float var1, SpringAnimationSpec var2) {
      this.output = this.output + ((var1 - this.cache) * var2.load() - this.output * var2.save());
      this.cache = this.cache + this.output;
   }

   private float apply() {
      AnimationClock var1 = AnimationClock.handle();
      long var2 = var1.resolve();
      if (var2 == this.active) {
         return -1.0F;
      }

      this.active = var2;
      return handle();
   }

   private static float apply(float var0) {
      if (!Float.isFinite(var0) || var0 <= 0.0F) {
         return 0.016666668F;
      } else {
         return var0 < 1.0E-4F ? 1.0E-4F : Math.min(0.25F, var0);
      }
   }
   public float process() {
      return this.cache;
   }
   public float compute() {
      return this.output;
   }
   public float resolve() {
      return this.current;
   }
   public long update() {
      return this.active;
   }
   public void compute(float var1) {
      this.cache = var1;
   }
   public void resolve(float var1) {
      this.output = var1;
   }
   public void update(float var1) {
      this.current = var1;
   }
   public void handle(long var1) {
      this.active = var1;
   }

   public static final class State {
      final float instance;
      final float data;
      final float context;
      final float config;

      public State(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }

      public static SpringAnimation.State handle() {
         return new SpringAnimation.State(18.5F, 1.8F, 0.35F, 18.0F);
      }

      public static SpringAnimation.State process() {
         return new SpringAnimation.State(15.5F, 2.2F, 0.12F, 8.0F);
      }

      public static SpringAnimation.State compute() {
         return new SpringAnimation.State(9.5F, 1.4F, 0.001F, 0.001F);
      }
      public float resolve() {
         return this.instance;
      }
      public float update() {
         return this.data;
      }
      public float apply() {
         return this.context;
      }
      public float execute() {
         return this.config;
      }
   }
}
