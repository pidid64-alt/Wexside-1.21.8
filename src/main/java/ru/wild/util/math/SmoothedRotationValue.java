package ru.wild.util.math;

public class SmoothedRotationValue {
   long instance;
   public float data;
   public float context;
   public float config;

   public SmoothedRotationValue(float var1, float var2, float var3) {
      this.data = var1;
      this.context = var2;
      this.config = var3;
      this.instance = System.currentTimeMillis();
   }

   public float handle() {
      if (Math.abs(this.data - this.context) < 1.0E-4) {
         this.data = this.context;
      }

      int var1;
      if ((var1 = (int)(Math.min((float)(System.currentTimeMillis() - this.instance), 400.0F) / 5.0F)) > 0) {
         this.instance = System.currentTimeMillis();
      }

      for (int var2 = 0; var2 < var1; var2++) {
         this.data = NumericTransform.execute(this.data, this.context, this.config);
      }

      return this.data;
   }

   public float process() {
      if (Math.abs(this.data - this.context) > 1.0E-4) {
         int var1 = (int)(Math.min((float)(System.currentTimeMillis() - this.instance), 400.0F) / 5.0F);
         if (var1 > 0) {
            this.instance = System.currentTimeMillis();
         }

         for (int var2 = 0; var2 < var1; var2++) {
            this.data = (float)this.handle(this.data, this.context, this.config);
         }
      }

      return NumericTransform.handle(this.data);
   }

   public void handle(float var1) {
      this.data = var1;
      this.instance = System.currentTimeMillis();
   }

   double handle(float var1, float var2, float var3) {
      float var4 = (var2 - var1 + 180.0F) % 360.0F - 180.0F;
      return var4 * var3 + var1;
   }
}
