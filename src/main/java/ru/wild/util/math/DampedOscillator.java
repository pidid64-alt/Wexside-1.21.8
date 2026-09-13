package ru.wild.util.math;

import ru.wild.gui.theme.MotionSpringPresets;

public final class DampedOscillator {
   private static final float instance = (float) (Math.PI * 2);
   private static final float data = 0.05F;
   private final float context;
   private final float config;
   private final float state;
   private final float cache;
   private float output;
   private float current;

   public DampedOscillator(MotionSpringPresets.DataRecord var1) {
      this.context = (float) (Math.PI * 2) * var1.frequencyHz();
      this.config = Math.max(0.05F, var1.dampingRatio());
      this.state = var1.settleDistance();
      this.cache = var1.settleDistance() * this.context;
   }

   public void handle(float var1) {
      this.output = var1;
      this.current = 0.0F;
   }

   public float handle(float var1, float var2) {
      if (!Float.isFinite(var1)) {
         return this.output;
      }

      if (var2 > 0.0F && Float.isFinite(var2)) {
         float var3 = var2;

         while (var3 > 0.0F) {
            float var4 = Math.min(var3, 0.05F);
            this.process(var1, var4);
            var3 -= var4;
         }

         if (Float.isFinite(this.output) && Float.isFinite(this.current)) {
            if (Math.abs(this.output - var1) <= this.state && Math.abs(this.current) <= this.cache) {
               this.handle(var1);
            }

            return this.output;
         } else {
            this.handle(var1);
            return this.output;
         }
      } else {
         return this.output;
      }
   }

   private void process(float var1, float var2) {
      float var3 = this.output - var1;
      float var4 = this.current;
      float var5 = this.context;
      float var6 = this.config;
      if (var6 < 0.999F) {
         float var7 = var5 * (float)Math.sqrt(1.0F - var6 * var6);
         float var8 = (float)Math.exp(-var6 * var5 * var2);
         float var9 = (float)Math.cos(var7 * var2);
         float var10 = (float)Math.sin(var7 * var2);
         float var11 = (var4 + var6 * var5 * var3) / var7;
         float var12 = var8 * (var3 * var9 + var11 * var10);
         float var13 = -var6 * var5 * var12 + var8 * var7 * (var11 * var9 - var3 * var10);
         this.output = var1 + var12;
         this.current = var13;
      } else if (var6 < 1.001F) {
         float var14 = (float)Math.exp(-var5 * var2);
         float var16 = var4 + var5 * var3;
         float var18 = var14 * (var3 + var16 * var2);
         float var20 = -var5 * var18 + var14 * var16;
         this.output = var1 + var18;
         this.current = var20;
      } else {
         float var15 = var5 * (float)Math.sqrt(var6 * var6 - 1.0F);
         float var17 = -var6 * var5 + var15;
         float var19 = -var6 * var5 - var15;
         float var21 = (var4 - var17 * var3) / (var19 - var17);
         float var22 = var3 - var21;
         float var23 = (float)Math.exp(var17 * var2);
         float var24 = (float)Math.exp(var19 * var2);
         this.output = var1 + var22 * var23 + var21 * var24;
         this.current = var22 * var17 * var23 + var21 * var19 * var24;
      }
   }

   public float handle() {
      return this.output;
   }

   public float process() {
      return this.current;
   }
}
