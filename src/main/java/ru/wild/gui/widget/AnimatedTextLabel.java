package ru.wild.gui.widget;

import ru.wild.render.font.FontObject;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class AnimatedTextLabel {
   private final DoubleAnimator instance = new DoubleAnimator();
   private String data;
   private String context;
   private int config = -1;
   private double state = Double.NaN;

   public void handle(String var1) {
      this.handle(var1, Double.NaN);
   }

   public void handle(String var1, double var2) {
      if (var1 == null) {
         var1 = "";
      }

      if (this.data == null) {
         this.data = var1;
         this.context = null;
         this.state = var2;
         this.instance.apply(1.0);
      } else if (!var1.equals(this.data)) {
         if (this.instance.update() >= 0.999F) {
            this.context = this.data;
            if (!Double.isNaN(var2) && !Double.isNaN(this.state)) {
               this.config = var2 >= this.state ? 1 : -1;
            }

            this.instance.apply(0.0);
         }

         this.data = var1;
         if (!Double.isNaN(var2)) {
            this.state = var2;
         }
      }

      this.instance.handle();
      this.instance.handle(1.0, 0.22F, Easings.handler, false);
   }

   public void handle(
      RoundedRectRenderer var1, FontObject var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11
   ) {
      String var12 = this.data == null ? "" : this.data;
      float var13 = TextMeasureCache.process(var2, var12, var10);
      this.compute(var1, var2, var3, var4, var5, var6, var7, var8 - var13 * 0.5F, var9, var10, var11);
   }

   public void process(
      RoundedRectRenderer var1, FontObject var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11
   ) {
      this.compute(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   private void compute(
      RoundedRectRenderer var1, FontObject var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11
   ) {
      float var12 = this.instance.update();
      String var13 = this.data == null ? "" : this.data;
      if (!(var12 >= 0.999F) && this.context != null) {
         String var14 = this.context;
         int var15 = var13.length();
         int var16 = var14.length();
         int var17 = Math.min(var15, var16);
         int var18 = 0;

         while (var18 < var17 && var13.charAt(var18) == var14.charAt(var18)) {
            var18++;
         }

         int var19 = 0;

         while (var19 < var17 - var18 && var13.charAt(var15 - 1 - var19) == var14.charAt(var16 - 1 - var19)) {
            var19++;
         }

         String var20 = var13.substring(0, var18);
         String var21 = var13.substring(var18, var15 - var19);
         String var22 = var14.substring(var18, var16 - var19);
         String var23 = var13.substring(var15 - var19);
         float var24 = TextMeasureCache.process(var2, var20, var10);
         float var25 = TextMeasureCache.process(var2, var21, var10);
         if (!var20.isEmpty()) {
            var1.handle(var2, var8, var9, var10, var20, var11);
         }

         float var26 = var8 + var24;
         float var27 = var10;
         int var28 = PackedColor.handle(var11);
         int var29 = PackedColor.handle(var11, (int)(var28 * var12));
         int var30 = PackedColor.handle(var11, (int)(var28 * (1.0F - var12)));
         var1.handle(var3, var4, var5, var6, var7, var7, var7, var7);
         if (!var22.isEmpty()) {
            var1.handle(var2, var26, var9 - this.config * var27 * var12, var10, var22, var30);
         }

         if (!var21.isEmpty()) {
            var1.handle(var2, var26, var9 + this.config * var27 * (1.0F - var12), var10, var21, var29);
         }

         var1.apply();
         if (!var23.isEmpty()) {
            var1.handle(var2, var26 + var25, var9, var10, var23, var11);
         }
      } else {
         var1.handle(var2, var8, var9, var10, var13, var11);
      }
   }
}
