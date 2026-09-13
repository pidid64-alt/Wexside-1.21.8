package ru.wild.util.math;

public final class Vec2f {
   private float instance;
   private float data;

   public Vec2f(float var1, float var2) {
      this.instance = var1;
      this.data = var2;
   }

   public void handle(float var1, float var2, float var3) {
      this.instance = this.process(var1, this.instance, var3);
      this.data = this.process(var2, this.data, var3);
   }

   public void handle(float var1, float var2) {
      this.instance = this.process(this.instance, var1, 1.0F);
      this.data = this.process(this.data, var2, 1.0F);
   }

   public float process(float var1, float var2, float var3) {
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      float var4 = var1 - var2;
      float var5 = Math.abs(var4) * var3;
      return var5 < 0.1F ? var1 : var2 + (var4 > 0.0F ? var5 : -var5);
   }

   public float handle() {
      return this.instance;
   }

   public void handle(float var1) {
      this.instance = var1;
   }

   public float process() {
      return this.data;
   }

   public void process(float var1) {
      this.data = var1;
   }
}
