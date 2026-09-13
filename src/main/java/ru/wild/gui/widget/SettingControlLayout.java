package ru.wild.gui.widget;

import java.awt.Color;
import java.util.HashMap;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.StringSetting;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.math.Easings;
import ru.wild.util.math.FrameInterpolation;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.InputNames;

public class SettingControlLayout {
   public static DoubleAnimator instance = new DoubleAnimator();
   public static HashMap<String, Float> data = new HashMap<>();
   public static HashMap<String, Float> context = new HashMap<>();
   public static HashMap<String, Float> config = new HashMap<>();

   public static float handle(RoundedRectRenderer var0, Setting var1) {
      if (var1 instanceof FloatSetting) {
         return ((FloatSetting)var1).compute();
      }

      if (var1 instanceof BooleanSetting) {
         return 10.0F;
      }

      if (var1 instanceof NumberSetting) {
         return 19.0F;
      }

      if (var1 instanceof ModeSetting var15) {
         float var16 = 105.47F;
         float var17 = 2.0F;
         float var18 = 10.075F;
         float var19 = 3.0F;
         float var20 = -2.0F;
         float var21 = var19;
         float var22 = 0.0F;

         for (String var24 : var15.config) {
            float var25 = RoundedRectRenderer.handle(FontRegistry.instance, var24, 12.0F).instance + var19 * 2.0F;
            if (var21 + var25 > var16 && var21 > var19) {
               var21 = var19;
               var22 += var18 + var20;
            }

            var21 += var25 + var17;
         }

         return var22 + var18 + 12.0F;
      } else {
         if (var1 instanceof KeybindSetting) {
            return 13.0F;
         }

         if (var1 instanceof StringSetting) {
            return 15.0F;
         }

         if (var1 instanceof ColorSetting) {
            return 15.0F;
         }

         if (var1 instanceof ChoiceSetting var2) {
            float var3 = 0.0F;
            float var4 = 10.0F;
            float var5 = 0.0F;
            float var6 = var4;
            float var7 = 3.0F;
            float var8 = 10.0F;
            float var9 = 4.0F;
            float var10 = 105.47F;

            for (BooleanSetting var12 : var2.config) {
               float var13 = RoundedRectRenderer.handle(FontRegistry.instance, var12.instance, 12.0F).instance;
               float var14 = var13 + var9 * 2.0F;
               if (var5 + var14 > 0.0F + var10) {
                  var5 = 0.0F;
                  var6 += var8 + var7;
               }

               var5 += var14 + var7;
            }

            return var6 - 0.0F + var8;
         } else {
            return 15.0F;
         }
      }
   }

   public static float handle(
      RoundedRectRenderer var0,
      Setting var1,
      float var2,
      float var3,
      float var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      float var12
   ) {
      float var13 = 0.0F;
      if (var1 instanceof FloatSetting) {
         var13 = ((FloatSetting)var1).compute();
      } else if (var1 instanceof BooleanSetting var14) {
         boolean var15 = var14.compute();
         float var16 = 8.0F;
         float var17 = var2 + var4 - var16 - 3.0F;
         float var18 = var3 + 2.0F;
         var14.state.handle();
         var14.state.handle(var15 ? 1.0 : 0.0, 0.15F, Easings.pending);
         var0.handle(var17, var18, var16, var16, 3.0F, var7, 0.1F);
         var0.handle(var17, var18, var16, var16, 3.0F, var9);
         var0.handle(var17 + 2.3F, var18 + 2.2F, 3.42F, 3.425F, 3.0F, PackedColor.update(0, var8, var14.state.update()));
         var0.handle(FontRegistry.instance, var2, var3 + 3.0F + 5.0F, 13.0F, var1.instance, var10);
         var13 = 10.0F;
      } else if (var1 instanceof NumberSetting var41) {
         float var47 = 4.0F;
         float var54 = var3 + 10.0F;
         float var61 = var4 - 2.5F;
         EasedDoubleAnimator var67 = BlurStateManager.handle(var41);
         float var19 = (var41.config - var41.state) / (var41.cache - var41.state);
         double var20 = var67.onTick();
         var67.handle();
         var67.handle(var19, 0.24F, EasingFunctions.handler);
         float var22 = (float)var67.select();
         float var23 = var61 * var22;
         var0.handle(var2, var54 + 2.0F, var61, var47, 2.0F, var7, 0.3F);
         var0.handle(var2, var54 + 2.0F, var61, var47, 2.0F, var9);
         var0.handle(var2 + 1.0F, var54 + 2.5F, var23 - 2.0F, var47 - 1.0F, 2.0F, var8);
         var0.handle(var2 + 1.0F + var23 - 5.0F + (var23 == 0.0F ? 5 : 2), var54 + 2.2F, 5.0F, 3.88F, 2.0F, var11);
         String var24 = var41.mode ? String.format("%.1f%%", var41.config) : String.format("%.1f / %.1f", var41.config, var41.cache);
         var0.handle(FontRegistry.instance, var2, var3 + 1.0F + 7.0F, 13.0F, var1.instance, var10);
         var0.handle(
            FontRegistry.instance,
            var2 + var61 - RoundedRectRenderer.handle(FontRegistry.instance, var24, 13.0F).instance - 2.0F,
            var3 + 7.0F,
            13.0F,
            var24,
            var8
         );
         var13 = 19.0F;
      } else if (var1 instanceof ModeSetting var42) {
         for (String var55 : var42.config) {
            data.putIfAbsent(var55, 0.0F);
         }

         var0.handle(FontRegistry.instance, var2, var3 + 7.0F, 13.0F, var1.instance, var10);
         float var49 = 2.0F;
         float var56 = 10.075F;
         float var62 = 3.0F;
         float var68 = -2.0F;
         float var73 = var62;
         float var78 = 0.0F;

         for (String var88 : var42.config) {
            float var94 = RoundedRectRenderer.handle(FontRegistry.instance, var88, 12.0F).instance + var62 * 2.0F;
            if (var73 + var94 > var4 && var73 > var62) {
               var73 = var62;
               var78 += var56 + var68;
            }

            var73 += var94 + var49;
         }

         float var83 = var3 + 10.0F;
         float var89 = var78 + var56;
         var0.handle(var2, var83, var4, var89, 3.0F, var7, 0.1F);
         var0.handle(var2, var83, var4, var89, 3.0F, var9);
         float var95 = var62;
         float var100 = 1.5F;

         for (String var26 : var42.config) {
            boolean var27 = var26.equals(var42.state);
            float var28 = RoundedRectRenderer.handle(FontRegistry.instance, var26, 12.0F).instance + var62 * 2.0F;
            if (var95 + var28 > var4 && var95 > var62) {
               var95 = var62;
               var100 += var56 + var68;
            }

            float var29 = data.get(var26);
            float var30 = var27 ? 1.0F : 0.0F;
            var29 = FrameInterpolation.handle(var29, var30, 10.0F);
            data.put(var26, var29);
            float var31 = var29;
            int var32 = PackedColor.update(var10, var8, var31);
            var0.handle(FontRegistry.instance, var2 + var95, var83 + var100 + 5.5F, 12.0F, var26, var32);
            var95 += var28 + var49;
         }

         var13 = var89 + 12.0F;
      } else if (var1 instanceof KeybindSetting var43) {
         float var50 = 10.075F;
         String var57 = var1.instance != null && !var1.instance.isEmpty() ? var1.instance : "KEY";
         String var63 = var43.output ? "..." : InputNames.process(var43.config);
         float var69 = RoundedRectRenderer.handle(FontRegistry.instance, var63, 12.0F).instance;
         float var74 = 16.055F;
         float var79 = Math.max(var74, var69 + 8.0F);
         float var84 = var2 + var4 - var79 - 2.0F;
         if (var84 < var2) {
            var84 = var2;
            var79 = var4 - 2.0F;
         }

         var0.handle(FontRegistry.instance, var2, var3 + 1.0F + 6.8F, 13.0F, var57, var10);
         float var90 = var84 - 6.0F;
         float var96 = var79 + 2.0F;
         float var101 = var3;
         float var107 = var50;
         if (var90 < var2) {
            var96 = var90 + var96 - var2;
            var90 = var2;
         }

         var0.handle(var90, var101, var96, var107, 3.0F, var7, 0.1F);
         var0.handle(var90, var101, var96, var107, 3.0F, var9);
         var0.handle(FontRegistry.instance, var90 + var96 / 2.0F - var69 / 2.0F, var101 + 1.5F + 5.7F, 12.0F, var63, var43.output ? var8 : var10);
         var13 = 13.0F;
      } else if (var1 instanceof StringSetting var44) {
         float var51 = 10.075F;
         float var58 = 63.56F;
         float var64 = var2 + 42.0F;
         float var70 = var3;
         float var75 = var64 + 5.0F;
         float var80 = var70 + 1.5F;
         var0.handle(FontRegistry.instance, var2, var3 + 1.0F + 6.5F, 13.0F, var1.instance, var10);
         var0.handle(var64, var70, var58, var51, 3.0F, var7, 0.1F);
         var0.handle(var64, var70, var58 - 10.0F, var51, 3.0F, var9);
         String var85 = var44.state;
         boolean var91 = var85.isEmpty();
         float var97 = var75;
         if (var91) {
            var0.handle(FontRegistry.instance, var75 - 2.0F, var80 - 0.5F + 6.1F, 12.0F, "Enter text", var10);
         } else {
            float var102 = var75;
            float var108 = var64 + var58 - 5.0F;
            float var112 = var75;
            float var116 = var64 + var58 - 5.0F;

            for (int var120 = 0; var120 < var85.length(); var120++) {
               char var123 = var85.charAt(var120);
               String var125 = String.valueOf(var123);
               float var126 = RoundedRectRenderer.handle(FontRegistry.instance, var125, 12.0F).instance;
               if (var102 + var126 > var108) {
                  var97 = var102;
                  break;
               }

               var11 = var10;
               if (var120 >= 16) {
                  float var127 = var112 + RoundedRectRenderer.handle(FontRegistry.instance, var85.substring(0, 16), 12.0F).instance;
                  float var33 = Math.min(30.0F, var116 - var127);
                  if (var33 > 0.0F) {
                     float var34 = (var102 - var127) / var33;
                     var34 = NumericTransform.onTick(var34, 0.0F, 1.0F);
                     int var35 = var10 >> 24 & 0xFF;
                     var35 = (int)(var35 * (1.0F - var34));
                     var11 = RoundedRectRenderer.ColorState.select(var10, var35);
                  } else {
                     var11 = RoundedRectRenderer.ColorState.select(var10, 0);
                  }
               }

               var0.handle(FontRegistry.instance, var102 - 2.0F, var80 - 0.5F + 6.1F, 12.0F, var125, var11);
               var102 += var126;
               var97 = var102;
            }
         }

         boolean var103 = BlurStateManager.latest == var44 && var44.current;
         if (var103) {
            long var109 = System.currentTimeMillis();
            boolean var117 = var109 / 500L % 2L == 0L;
            if (var117) {
               var0.handle(var97 - 3.0F, var80 - 0.5F, 1.0F, 8.0F, 0.5F, var8);
            }
         }

         var13 = 15.0F;
      } else if (var1 instanceof ColorSetting var45) {
         float var52 = 12.0F;
         float var59 = 40.0F;
         float var65 = var2 + var4 - var59 - 2.0F;
         var0.handle(FontRegistry.instance, var2, var3 + 1.0F + 7.0F, 13.0F, var1.instance, var10);
         Color var71 = var45.compute();
         var0.handle(var65 - 10.0F, var3, 46.48F, 10.075F, 3.0F, var7, 0.1F);
         var0.handle(var65 - 10.0F, var3, 46.48F, 10.075F, 3.0F, var9);
         float var76 = var65 + 22.0F;
         float var81 = var3 + 0.8F;
         float var86 = 13.285F;
         float var92 = 8.315F;
         int var98 = Math.round(var45.animationDraw * var12 * 255.0F) << 24 | var71.getRed() << 16 | var71.getGreen() << 8 | var71.getBlue();
         var0.handle(var76, var81, var86, var92, 0.0F, 3.0F, 3.0F, 0.0F);

         try {
            boolean var104 = false;

            for (float var110 = var81; var110 < var81 + var92; var110 += 3.0F) {
               boolean var113 = var104;
               float var118 = Math.min(3.0F, var81 + var92 - var110);

               for (float var121 = var76; var121 < var76 + var86; var121 += 3.0F) {
                  float var124 = Math.min(3.0F, var76 + var86 - var121);
                  var0.handle(var121, var110, var124, var118, PackedColor.compute(var113 ? -12762550 : -14407632, var12 * 0.8F));
                  var113 = !var113;
               }

               var104 = !var104;
            }

            var0.handle(var76, var81, var86, var92, 0.0F, 3.0F, 3.0F, 0.0F, var98);
            var0.process(var76, var81, var86, var92 * 0.55F, 0.0F, 3.0F, 0.0F, 0.0F, PackedColor.compute(-1, var12 * 0.28F), 0);
         } finally {
            var0.apply();
         }

         var0.handle(var76, var81, var86, var92, 0.0F, 3.0F, 3.0F, 0.0F, PackedColor.compute(-1, var12 * 0.4F), 0.5F);
         String var105 = String.format("#%02X%02X%02X", var71.getRed(), var71.getGreen(), var71.getBlue());
         var0.handle(
            FontRegistry.instance,
            var65 + var59 / 2.0F - RoundedRectRenderer.handle(FontRegistry.instance, var105, 12.0F).instance / 2.0F - 14.0F,
            var3 + 1.5F + 5.7F,
            12.0F,
            var105,
            var10
         );
         var13 = 15.0F;
      } else if (var1 instanceof ChoiceSetting var46) {
         var0.handle(FontRegistry.instance, var2, var3 + 7.0F, 13.0F, var1.instance, var10);
         float var53 = var3 + 10.0F;
         float var60 = var2;
         float var66 = var53;
         float var72 = 3.0F;
         float var77 = 10.0F;
         float var82 = 4.0F;

         for (BooleanSetting var93 : var46.config) {
            float var99 = RoundedRectRenderer.handle(FontRegistry.instance, var93.instance, 12.0F).instance;
            float var106 = var99 + var82 * 2.0F;
            if (var60 + var106 > var2 + var4) {
               var60 = var2;
               var66 += var77 + var72;
            }

            var0.handle(var60, var66, var106, var77, 3.0F, var7, 0.1F);
            var0.handle(var60, var66, var106, var77, 3.0F, var9);
            String var111 = var1.instance + "_" + var93.instance;
            config.putIfAbsent(var111, var93.compute() ? 1.0F : 0.0F);
            float var114 = config.get(var111);
            float var119 = var93.compute() ? 1.0F : 0.0F;
            var114 = FrameInterpolation.handle(var114, var119, 10.0F);
            config.put(var111, var114);
            var11 = PackedColor.update(var10, var8, var114);
            var0.handle(FontRegistry.instance, var60 + var82, var66 + 3.0F - 1.0F + 5.0F, 12.0F, var93.instance, var11);
            var60 += var106 + var72;
         }

         var13 = var66 - var3 + var77;
      }

      return var13 + 1.0F;
   }
}
