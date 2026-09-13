package ru.wild.util.math;

import ru.wild.core.AnimationClock;

public final class SpringFloat implements AnimationClock.Predicate {
   private static final float instance = 1.0E-4F;
   private static final float data = 0.016666668F;
   private static final float context = 0.1F;
   private final AnimationClock config;
   private final SpringParameters state;
   private final float cache;
   private final float output;
   private final float current;
   private final float active;
   private float mode;
   private float selection;
   private float enabled;
   private FloatEasing renderer = FloatEasing.handle();

   public SpringFloat(AnimationClock var1, SpringParameters var2, float var3, float var4, float var5, float var6, float var7) {
      if (var1 == null) {
         throw new IllegalArgumentException("animationSystem must not be null");
      }

      if (var2 == null) {
         throw new IllegalArgumentException("config must not be null");
      }

      if (var4 > var5) {
         throw new IllegalArgumentException("minValue must be <= maxValue");
      }

      if (!(var6 <= 0.0F) && !(var7 <= 0.0F)) {
         this.config = var1;
         this.state = var2;
         this.cache = var4;
         this.output = var5;
         this.current = var6;
         this.active = var7;
         float var8 = this.update(var3);
         this.mode = var8;
         this.selection = var8;
         this.enabled = 0.0F;
      } else {
         throw new IllegalArgumentException("tolerances must be > 0");
      }
   }

   public void handle(FloatEasing var1) {
      this.renderer = var1 == null ? FloatEasing.handle() : var1;
   }

   public void process(float var1) {
      float var2 = this.update(var1);
      this.mode = var2;
      this.selection = var2;
      this.enabled = 0.0F;
      this.config.process(this);
   }

   public void compute(float var1) {
      float var2 = this.update(var1);
      if (Math.abs(var2 - this.selection) <= this.current * 0.25F) {
         this.selection = var2;
         if (this.resolve()) {
            this.process(var2);
         }
      } else {
         this.selection = var2;
         this.config.handle(this);
      }
   }

   public float handle() {
      float var1 = 0.0F;
      float var2 = this.output - this.cache;
      if (var2 > 0.0F) {
         var1 = (this.mode - this.cache) / var2;
      }

      float var3 = this.renderer.ease(apply(var1));
      return this.cache + var3 * var2;
   }

   public float process() {
      return this.mode;
   }

   public float compute() {
      return this.selection;
   }

   public boolean resolve() {
      float var1 = Math.abs(this.selection - this.mode);
      return var1 <= this.current && Math.abs(this.enabled) <= this.active;
   }

   @Override
   public boolean handle(float var1) {
      float var2 = var1;
      if (var2 < 1.0E-4F) {
         var2 = 1.0E-4F;
      } else if (var2 > 0.1F) {
         var2 = 0.1F;
      }

      boolean var3 = true;

      while (var2 > 0.0F && var3) {
         float var4 = Math.min(var2, 0.016666668F);
         var3 = this.resolve(var4);
         var2 -= var4;
      }

      return var3;
   }

   private boolean resolve(float var1) {
      float var2 = (float)((Math.PI * 2) * this.state.handle());
      float var3 = 2.0F * this.state.process() * var2;
      float var4 = var2 * var2;
      float var5 = this.mode - this.selection;
      float var6 = -var4 * var5 - var3 * this.enabled;
      this.enabled += var6 * var1;
      this.mode = this.mode + this.enabled * var1;
      if (Float.isNaN(this.mode) || Float.isInfinite(this.mode) || Float.isNaN(this.enabled) || Float.isInfinite(this.enabled)) {
         this.mode = this.selection;
         this.enabled = 0.0F;
         return false;
      }

      if (this.mode < this.cache) {
         this.mode = this.cache;
         this.enabled = 0.0F;
         return false;
      }

      if (this.mode > this.output) {
         this.mode = this.output;
         this.enabled = 0.0F;
         return false;
      }

      float var7 = this.mode - this.selection;
      if ((!(var5 > 0.0F) || !(var7 < 0.0F)) && (!(var5 < 0.0F) || !(var7 > 0.0F))) {
         if (this.resolve()) {
            this.mode = this.selection;
            this.enabled = 0.0F;
            return false;
         } else {
            return true;
         }
      } else {
         this.mode = this.selection;
         this.enabled = 0.0F;
         return false;
      }
   }

   private float update(float var1) {
      if (var1 <= this.cache) {
         return this.cache;
      } else {
         return var1 >= this.output ? this.output : var1;
      }
   }

   private static float apply(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }
}
