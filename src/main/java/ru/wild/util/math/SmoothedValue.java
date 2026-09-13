package ru.wild.util.math;

public final class SmoothedValue {
   private final SpringAnimationSpec instance;
   private float data;
   private float context;

   public SmoothedValue(SpringAnimationSpec var1) {
      this.instance = var1;
   }

   public void handle(float var1) {
      this.data = var1;
      this.context = 0.0F;
   }

   public float handle(float var1, float var2) {
      if (Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = this.data;
      }

      if (!Float.isNaN(var2) && !Float.isInfinite(var2) && !(var2 <= 0.0F)) {
         float var3 = Math.max(0.05F, Math.min(4.0F, var2 * 60.0F));
         this.context = this.context + (var1 - this.data) * this.instance.load() * var3;
         this.context = this.context * (float)Math.pow(this.instance.save(), var3);
         this.data = this.data + this.context * var3;
         if (!Float.isNaN(this.data) && !Float.isInfinite(this.data) && !Float.isNaN(this.context) && !Float.isInfinite(this.context)) {
            if (Math.abs(var1 - this.data) <= this.instance.submit() && Math.abs(this.context) <= this.instance.unload()) {
               this.data = var1;
               this.context = 0.0F;
            }

            return this.data;
         } else {
            this.data = var1;
            this.context = 0.0F;
            return this.data;
         }
      } else {
         return this.data;
      }
   }

   public float handle() {
      return this.data;
   }

   public float process() {
      return this.context;
   }
}
