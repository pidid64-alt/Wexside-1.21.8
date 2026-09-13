package ru.wild.gui.widget;

import java.awt.Color;
import ru.wild.api.setting.ColorSetting;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public class ColorControlRenderer extends BlurStateManager {
   public static final float actionRead = 160.0F;
   public static final float configCollapse = 119.0F;
   public static final float dataValidate = 6.0F;
   public static final float scaleRender = 5.0F;
   public static final float clientRefresh = 132.0F;
   public static final float keyFilter = 62.0F;
   public static final float requestAdapt = 10.0F;
   public static final float timerMeasure = 7.0F;
   public static final float vectorEncode = 10.0F;
   public static final float requestReceive = 10.0F;
   private static final int windowProcess = -1577754;
   private static final int packetSave = -3945532;

   public static void handle(RoundedRectRenderer var0, ColorSetting var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      if (var1 != null) {
         if (BlurStateManager.pointEncode != 0.0F || BlurStateManager.animator != 0.0F) {
            handle(var0, var1, BlurStateManager.pointEncode, BlurStateManager.animator, var2, var3, var4, var5, var6, var7);
         }
      }
   }

   private static void handle(RoundedRectRenderer var0, ColorSetting var1, float var2, float var3, int var4, int var5, int var6, int var7, int var8, float var9) {
      float var10 = handle(var2);
      if (BlurStateManager.output.compute()) {
         var0.handle(var10, var3, 160.0F, 119.0F, 6.0F);
      }

      var0.handle(var10, var3, 160.0F, 119.0F, 6.0F, var7);
      var0.handle(var10, var3, 160.0F, 119.0F, 6.0F, var6, 0.35F);
      float var11 = process(var10);
      float var12 = compute(var3);
      float var13 = resolve(var10);
      float var14 = update(var3);
      float var15 = apply(var3);
      float var16 = execute(var3);
      float var17 = var1.resolve();
      float var18 = var1.animationDraw;
      handle(var0, var11, var12, 132.0F, 62.0F, var17, var9);
      var0.handle(var11, var12, 132.0F, 62.0F, 4.0F, PackedColor.compute(var6, var9), 0.45F);
      float var19 = var11 + var1.renderer * 132.0F;
      float var20 = var12 + (1.0F - var1.handler) * 62.0F;
      var0.handle(var19 - 3.0F, var20 - 3.0F, 6.0F, 6.0F, 3.0F, PackedColor.compute(-1, var9));
      var0.handle(var19 - 4.0F, var20 - 4.0F, 8.0F, 8.0F, 4.0F, PackedColor.compute(-16777216, var9 * 0.7F), 0.4F);
      handle(var0, var13, var12, 10.0F, 62.0F, var9);
      var0.handle(var13, var12, 10.0F, 62.0F, 4.0F, PackedColor.compute(var6, var9), 0.45F);
      float var21 = var12 + var17 * 62.0F;
      var0.handle(var13 - 1.5F, var21 - 2.0F, 13.0F, 4.0F, 2.0F, PackedColor.compute(-1, var9));
      var0.handle(var13 - 1.5F, var21 - 2.0F, 13.0F, 4.0F, 2.0F, PackedColor.compute(-16777216, var9 * 0.65F), 0.35F);
      handle(var0, var11, var14, 148.0F, 7.0F, var1, var9);
      float var22 = var11 + var18 * 148.0F;
      var0.handle(var22 - 1.5F, var14 - 1.0F, 3.0F, 9.0F, 1.5F, PackedColor.compute(-1, var9));
      process(var0, var11, var15, 148.0F, 10.0F, var1, var9);
      compute(var0, var11, var16, 148.0F, 10.0F, var1, var9);
   }

   public static float handle(float var0) {
      return var0 + (30.0F - 30.0F * BlurStateManager.cache.check());
   }

   public static float process(float var0) {
      return var0 + 6.0F;
   }

   public static float compute(float var0) {
      return var0 + 6.0F;
   }

   public static float resolve(float var0) {
      return var0 + 6.0F + 132.0F + 5.0F;
   }

   public static float update(float var0) {
      return var0 + 6.0F + 62.0F + 5.0F;
   }

   public static float apply(float var0) {
      return update(var0) + 7.0F + 5.0F;
   }

   public static float execute(float var0) {
      return apply(var0) + 10.0F + 5.0F;
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      Color var7 = Color.getHSBColor(var5, 1.0F, 1.0F);
      var0.handle(var1, var2, var3, var4, 4.0F, PackedColor.compute(-1, var6), PackedColor.compute(var7.getRGB(), var6));
      var0.process(var1, var2, var3, var4, 4.0F, 0, PackedColor.compute(-16777216, var6));
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5) {
      byte var6 = 6;
      float var7 = var4 / var6;

      for (int var8 = 0; var8 < var6; var8++) {
         float var9 = var2 + var8 * var7;
         int var10 = handle(var8 / 6.0F, 1.0F, 1.0F, var5);
         int var11 = handle((var8 + 1.0F) / 6.0F, 1.0F, 1.0F, var5);
         var0.process(var1, var9, var3, var7 + 0.5F, var10, var11);
      }
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, ColorSetting var5, float var6) {
      process(var0, var1, var2, var3, var4, 6.0F, var6);
      int var7 = handle(var5.resolve(), var5.renderer, var5.handler, 0.0F);
      int var8 = handle(var5.resolve(), var5.renderer, var5.handler, var6);
      var0.handle(var1, var2, var3, var4, 3.0F, var7, var8);
      var0.handle(var1, var2, var3, var4, 3.0F, PackedColor.compute(-1, var6 * 0.16F), 0.35F);
   }

   private static void process(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, ColorSetting var5, float var6) {
      float[] var7 = new float[]{0.0F, 0.5F, -0.083333336F, 0.083333336F, 0.33333334F};
      float var8 = 3.0F;
      float var9 = (var3 - var8 * (var7.length - 1)) / var7.length;

      for (int var10 = 0; var10 < var7.length; var10++) {
         float var11 = var1 + var10 * (var9 + var8);
         process(var0, var11, var2, var9, var4, 6.0F, var6 * 0.55F);
         var0.handle(
            var11,
            var2,
            var9,
            var4,
            3.0F,
            handle(var5.resolve() + var7[var10], Math.max(var5.renderer, 0.62F), Math.max(var5.handler, 0.72F), var5.animationDraw * var6)
         );
         var0.handle(var11, var2, var9, var4, 3.0F, PackedColor.compute(var10 == 0 ? -1 : -1996488705, var6 * 0.45F), 0.35F);
      }
   }

   private static void compute(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, ColorSetting var5, float var6) {
      byte var7 = 9;
      float var8 = 3.0F;
      float var9 = (var3 - var8 * (var7 - 1)) / var7;
      int var10 = var5.check();

      for (int var11 = 0; var11 < var7; var11++) {
         float var12 = var1 + var11 * (var9 + var8);
         boolean var13 = var11 == 8;
         boolean var14 = !var13 && var11 < var5.pointEncode.size();
         process(var0, var12, var2, var9, var4, 6.0F, var14 ? var6 * 0.6F : var6 * 0.25F);
         if (var14) {
            var0.handle(var12, var2, var9, var4, 3.0F, PackedColor.compute(var5.pointEncode.get(var11), var6));
         } else {
            var0.handle(var12, var2, var9, var4, 3.0F, PackedColor.compute(var13 ? 1144649215 : 587202559, var6));
         }

         if (var13) {
            handle(var0, FontRegistry.current, "O", var12, var2, var9, var4, 8.0F, PackedColor.compute(-1, var6));
         }

         boolean var15 = var14 && var5.pointEncode.get(var11) == var10;
         if (var15) {
            handle(var0, FontRegistry.current, "j", var12, var2, var9, var4, 7.0F, PackedColor.compute(-1, var6 * 0.9F));
         }

         var0.handle(var12, var2, var9, var4, 3.0F, PackedColor.compute(var15 ? -1 : 2013265919, var6 * (var15 ? 0.9F : 0.34F)), var15 ? 0.6F : 0.35F);
      }
   }

   private static void handle(RoundedRectRenderer var0, FontObject var1, String var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      float var9 = RoundedRectRenderer.handle(var1, var2, var7).instance;
      var0.handle(var1, var3 + (var5 - var9) * 0.5F, var4 + var6 * 0.5F + var7 * 0.32F, var7, var2, var8);
   }

   private static void process(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = false;
      float var8 = var2;

      while (var8 < var2 + var4) {
         boolean var9 = var7;
         float var10 = Math.min(var5, var2 + var4 - var8);

         for (float var11 = var1; var11 < var1 + var3; var11 += var5) {
            float var12 = Math.min(var5, var1 + var3 - var11);
            var0.handle(var11, var8, var12, var10, PackedColor.compute(var9 ? -1577754 : -3945532, var6));
            var9 = !var9;
         }

         var7 = !var7;
         var8 += var5;
      }
   }

   private static int handle(float var0, float var1, float var2, float var3) {
      float var4 = var0 - (float)Math.floor(var0);
      int var5 = Color.HSBtoRGB(var4, prepare(var1), prepare(var2));
      int var6 = Math.round(prepare(var3) * 255.0F);
      return var6 << 24 | var5 & 16777215;
   }

   private static float prepare(float var0) {
      return Float.isFinite(var0) && !(var0 <= 0.0F) ? Math.min(var0, 1.0F) : 0.0F;
   }
}
