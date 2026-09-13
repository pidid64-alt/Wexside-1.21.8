package ru.wild.util.math;
public final class SpringAnimationSpec {
   private final float instance;
   private final float data;
   private final float context;
   private final float config;

   private static SpringAnimationSpec handle(float var0, float var1, float var2, float var3) {
      EasingStylePresets var4 = EasingStylePresets.handle();
      return new SpringAnimationSpec(var4.handle(var0), var4.process(var1), var4.compute(var2), var4.compute(var3));
   }

   public static SpringAnimationSpec handle() {
      return handle(0.045F, 0.85F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec process() {
      return new SpringAnimationSpec(0.03F, 0.87F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec compute() {
      return new SpringAnimationSpec(0.075F, 0.86F, 0.002F, 0.002F);
   }

   public static SpringAnimationSpec resolve() {
      return new SpringAnimationSpec(0.045F, 0.85F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec update() {
      return handle(0.065F, 0.75F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec apply() {
      return handle(0.12F, 0.9F, 0.02F, 0.02F);
   }

   public static SpringAnimationSpec execute() {
      return handle(0.05F, 0.84F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec prepare() {
      return handle(0.062F, 0.86F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec check() {
      return handle(0.08F, 0.55F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec onTick() {
      return handle(0.105F, 0.68F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec select() {
      return handle(0.038F, 0.86F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec refresh() {
      return handle(0.052F, 0.72F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec render() {
      return handle(0.018F, 0.88F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec tick() {
      return handle(0.012F, 0.92F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec drawAnimation() {
      return handle(0.1F, 0.88F, 0.002F, 0.002F);
   }

   public static SpringAnimationSpec encodePoint() {
      return handle(0.035F, 0.88F, 0.001F, 0.001F);
   }

   public static SpringAnimationSpec animate() {
      return new SpringAnimationSpec(0.06111111F, (float)Math.exp(-0.4F), 0.001F, 0.001F);
   }
   public SpringAnimationSpec(float var1, float var2, float var3, float var4) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
   }
   public float load() {
      return this.instance;
   }
   public float save() {
      return this.data;
   }
   public float submit() {
      return this.context;
   }
   public float unload() {
      return this.config;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SpringAnimationSpec var2)) {
         return false;
      } else if (Float.compare(this.load(), var2.load()) != 0) {
         return false;
      } else if (Float.compare(this.save(), var2.save()) != 0) {
         return false;
      } else {
         return Float.compare(this.submit(), var2.submit()) != 0 ? false : Float.compare(this.unload(), var2.unload()) == 0;
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.load());
      var2 = var2 * 59 + Float.floatToIntBits(this.save());
      var2 = var2 * 59 + Float.floatToIntBits(this.submit());
      return var2 * 59 + Float.floatToIntBits(this.unload());
   }
   @Override
   public String toString() {
      return "SpringSpec(stiffness="
         + this.load()
         + ", damping="
         + this.save()
         + ", settleDistance="
         + this.submit()
         + ", settleVelocity="
         + this.unload()
         + ")";
   }
}
