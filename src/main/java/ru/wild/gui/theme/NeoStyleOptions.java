package ru.wild.gui.theme;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.api.setting.Setting;
import ru.wild.config.HudProfileConfig;
import ru.wild.gui.hud.HudElementMetadata;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class NeoStyleOptions {
   private static final Map<String, DoubleAnimator> instance = new HashMap<>();

   private static float handle(RoundedRectRenderer var0, ResettableSettingGroup var1, String var2, String var3) {
      float var4 = 5.0F;
      float var5 = 32.0F;
      float var6 = 4.0F;
      float var7 = var5 - var6 * 2.0F;
      float var8 = var4 + var6 + var7 + 8.0F;
      var8 += TextMeasureCache.handle(FontRegistry.current, "w", 14.0F).instance + 3.0F;
      var8 += TextMeasureCache.handle(FontRegistry.instance, "wildclient.org", 22.0F).instance + 4.0F;
      var8 += TextMeasureCache.handle(FontRegistry.current, "k", 14.0F).instance + 4.0F;
      var8 += TextMeasureCache.handle(FontRegistry.current, var3, 18.0F).instance + 4.0F;
      var8 += TextMeasureCache.handle(FontRegistry.config, var2, 22.0F).instance;
      var8 += 10.0F + var4;
      return Math.max(210.0F, var8);
   }

   public static NeoStyleOptions.Bounds handle(RoundedRectRenderer var0, ResettableSettingGroup var1, float var2, float var3, float var4, float var5) {
      if (var1.handle().isEmpty()) {
         return new NeoStyleOptions.Bounds(var2, var3, 0.0F, 0.0F);
      }

      HudElementMetadata var6 = var1.getClass().getAnnotation(HudElementMetadata.class);
      String var7 = var6 != null ? var6.handle() : "Settings";
      String var8 = var6 != null && !var6.process().isEmpty() ? var6.process() : "e";
      float var9 = handle(var0, var1, var7, var8);
      float var10 = 5.0F;
      float var11 = 32.0F;
      float var12 = 8.0F;
      float var13 = 4.0F;
      float var14 = 20.0F;
      float var15 = 28.0F;
      float var16 = 20.0F;
      float var17 = 20.0F;
      float var18 = var12 * 2.0F;

      for (Setting var20 : var1.handle()) {
         if (handle(var20)) {
            if (var20 instanceof BooleanSetting) {
               var18 += var14;
            } else if (var20 instanceof NumberSetting) {
               var18 += var15;
            } else if (var20 instanceof ModeSetting) {
               var18 += var16;
            } else if (var20 instanceof ActionSetting) {
               var18 += var17;
            } else if (var20 instanceof ChoiceSetting var21) {
               var18 += var14 + handle(var21) * var14 * var21.output.update();
            }
         }
      }

      float var28 = var10 + var11 + var13 + var18 + var10;
      MinecraftClient var29 = MinecraftClient.getInstance();
      float var30 = var29.getWindow().getFramebufferWidth();
      float var22 = var29.getWindow().getFramebufferHeight();
      float var23 = 10.0F;
      boolean var24 = var2 + var4 + var23 + var9 > var30;
      boolean var25 = var3 + var28 + var23 > var22;
      float var26 = var24 ? var2 - var9 - var23 : var2 + var4 + var23;
      if (var26 + var9 > var30) {
         var26 = var30 - var9 - var23;
      }

      if (var26 < var23) {
         var26 = var23;
      }

      float var27 = var25 ? var3 + var5 - var28 : var3;
      if (var27 + var28 > var22) {
         var27 = var22 - var28 - var23;
      }

      if (var27 < var23) {
         var27 = var23;
      }

      return new NeoStyleOptions.Bounds(var26, var27, var9, var28);
   }

   public static void handle(
      RoundedRectRenderer var0,
      ResettableSettingGroup var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      boolean var11,
      boolean var12
   ) {
      if (!var1.handle().isEmpty() && !(var8 <= 0.01F)) {
         HudElementMetadata var13 = var1.getClass().getAnnotation(HudElementMetadata.class);
         String var14 = var13 != null ? var13.handle() : "Settings";
         String var15 = var13 != null && !var13.process().isEmpty() ? var13.process() : "e";
         float var16 = handle(var0, var1, var14, var15);
         float var17 = 5.0F;
         float var18 = 32.0F;
         float var19 = 4.0F;
         float var20 = var18 - var19 * 2.0F;
         float var21 = 8.0F;
         float var22 = 4.0F;
         float var23 = 20.0F;
         float var24 = 28.0F;
         float var25 = 20.0F;
         float var26 = 20.0F;
         float var27 = var21 * 2.0F;

         for (Setting var29 : var1.handle()) {
            if (handle(var29)) {
               if (var29 instanceof BooleanSetting) {
                  var27 += var23;
               } else if (var29 instanceof NumberSetting) {
                  var27 += var24;
               } else if (var29 instanceof ModeSetting) {
                  var27 += var25;
               } else if (var29 instanceof ActionSetting) {
                  var27 += var26;
               } else if (var29 instanceof ChoiceSetting var30) {
                  var30.output.handle();
                  var30.output.handle(var30.state ? 1.0 : 0.0, 0.2F, Easings.handler, false);
                  var27 += var23 + handle(var30) * var23 * var30.output.update();
               }
            }
         }

         float var99 = var17 + var18 + var22 + var27 + var17;
         MinecraftClient var100 = MinecraftClient.getInstance();
         float var101 = var100.getWindow().getFramebufferWidth();
         float var31 = var100.getWindow().getFramebufferHeight();
         float var32 = 10.0F;
         boolean var33 = var2 + var4 + var32 + var16 > var101;
         boolean var34 = var3 + var99 + var32 > var31;
         float var35 = var33 ? var2 - var16 - var32 : var2 + var4 + var32;
         if (var35 + var16 > var101) {
            var35 = var101 - var16 - var32;
         }

         if (var35 < var32) {
            var35 = var32;
         }

         float var36 = var34 ? var3 + var5 - var99 : var3;
         if (var36 + var99 > var31) {
            var36 = var31 - var99 - var32;
         }

         if (var36 < var32) {
            var36 = var32;
         }

         float var37 = (1.0F - var8) * 10.0F;
         float var38 = var35 + (var33 ? var37 : -var37);
         float var39 = (1.0F - var8) * 10.0F;
         float var40 = var36 + (var34 ? var39 : -var39);
         int var41 = (int)(255.0F * var8);
         int var42 = PackedColor.compute(10, 10, 10, (int)(40.0F * var8));
         int var43 = PackedColor.compute(28, 30, 30, (int)(140.0F * var8));
         int var44 = PackedColor.compute(255, 255, 255, (int)(10.0F * var8));
         int var45 = var1 instanceof ThemePresets var46 ? var46.prepare(var8) : PackedColor.update(RoundedRectRenderer.ColorState.apply(255, 255), var41);
         int var102 = PackedColor.compute(255, 255, 255, var41);
         int var47 = PackedColor.compute(255, 255, 255, (int)(122.0F * var8));
         int var48 = PackedColor.compute(255, 255, 255, (int)(120.0F * var8));
         boolean var49 = handle(var1);
         ThemeShaderApplier.DataRecord var50 = process(var1);
         if (var49) {
            var42 = ThemeShaderApplier.handle(var8);
            var43 = ThemeShaderApplier.handle(var8);
            var44 = PackedColor.compute(0, 0, 0, 0);
            var102 = ThemeShaderApplier.process(var8);
            var47 = ThemeShaderApplier.compute(var8);
            var48 = ThemeShaderApplier.compute(var8);
         }

         var0.update(var8);
         float var51 = 14.0F;
         if (!var49 || !ThemeShaderApplier.handle(null, var38, var40, var16, var99, var51, var50.distance(), var50.blur(), var50.intensity(), 1, false, var8)) {
            var0.handle(23.0F);
            var0.handle(var38, var40, var16, var99, var51, var8);
            var0.handle(var38, var40, var16, var99, var51, var42);
            var0.handle(var38, var40, var16, var99, var51, var44, 1.0F);
         }

         float var52 = var38 + var17;
         float var53 = var40 + var17;
         float var54 = var16 - var17 * 2.0F;
         if (!var49 || !ThemeShaderApplier.handle(null, var52, var53, var54, var18, 11.0F, var50.distance(), var50.blur(), var50.intensity(), 1, false, var8)) {
            var0.handle(var52, var53, var54, var18, 11.0F, 11.0F, 4.0F, 4.0F, var43);
         }

         float var55 = var52 + var19;
         float var56 = var53 + var19;
         var0.handle(var55, var56, var20, var20, 7.0F, var45);
         float var57 = 28.0F;
         float var58 = TextMeasureCache.handle(FontRegistry.current, "o", var57).instance;
         var0.handle(FontRegistry.current, var55 + (var20 - var58) / 2.0F, var56 + var20 / 2.0F + 6.0F, var57, "o", PackedColor.compute(255, 255, 255, var41));
         float var59 = var55 + var20 + 8.0F;
         float var60 = var53 + var18 / 2.0F + 4.5F;
         var0.handle(FontRegistry.current, var59, var60 - 0.5F, 14.0F, "w", var48);
         var59 += TextMeasureCache.handle(FontRegistry.current, "w", 14.0F).instance + 3.0F;
         var0.handle(FontRegistry.instance, var59, var60, 22.0F, "wildclient.org", var47);
         var59 += TextMeasureCache.handle(FontRegistry.instance, "wildclient.org", 22.0F).instance + 4.0F;
         var0.handle(FontRegistry.current, var59, var60 - 0.5F, 12.0F, "k", var48);
         var59 += TextMeasureCache.handle(FontRegistry.current, "k", 12.0F).instance + 4.0F;
         var0.handle(FontRegistry.current, var59, var60, 18.0F, var15, var45);
         var59 += TextMeasureCache.handle(FontRegistry.current, var15, 18.0F).instance + 4.0F;
         var0.handle(FontRegistry.config, var59, var60, 22.0F, var14, var102);
         float var61 = var38 + var17;
         float var62 = var53 + var18 + var22;
         float var63 = var16 - var17 * 2.0F;
         if (!var49 || !ThemeShaderApplier.handle(null, var61, var62, var63, var27, 9.0F, var50.distance(), var50.blur(), var50.intensity(), 2, true, var8)) {
            var0.handle(var61, var62, var63, var27, 4.0F, 4.0F, 11.0F, 11.0F, var43);
         }

         float var64 = 1.5F;
         var0.handle(var61 + var21, var62 + var21, var64, var27 - var21 * 2.0F, 0.5F, var45);
         float var65 = var62 + var21;
         float var66 = var61 + var21 + var64 + 6.5F;
         float var67 = var63 - (var66 - var61) - var21;
         float var68 = 22.0F;
         float var69 = 20.0F;
         float var70 = 5.0F;
         var0.handle(var61, var62, var63, var27, 4.0F, 4.0F, 11.0F, 11.0F);

         for (Setting var72 : var1.handle()) {
            if (handle(var72)) {
               if (var72 instanceof BooleanSetting var73) {
                  float var110 = 12.0F;
                  float var114 = var66 + var67 - var110;
                  float var119 = var65 + (var23 - var110) / 2.0F;
                  float var123 = var67 - var110 - 6.0F;
                  handle(var0, FontRegistry.instance, var73.instance, var66, var65 + var23 / 2.0F + var70, var68, var102, var65, var23, var123);
                  var73.state.handle();
                  var73.state.handle(var73.compute() ? 1.0 : 0.0, 0.15F, Easings.handler, false);
                  int var126 = PackedColor.compute(255, 255, 255, (int)(10.0F * var8));
                  boolean var129 = var12 && handle(var9, var10, var66, var65, var67, var23);
                  if (!var49
                     || !ThemeShaderApplier.handle(
                        null, var114, var119, var110, var110, 3.0F, var50.distance(), var50.blur(), var50.intensity(), var129 ? 2 : 1, var129, var8
                     )) {
                     var0.handle(var114, var119, var110, var110, 3.0F, var126);
                  }

                  if (var73.compute()) {
                     float var132 = TextMeasureCache.handle(FontRegistry.current, "j", 10.0F).instance;
                     var0.handle(FontRegistry.current, var114 + (var110 - var132) / 2.0F, var119 + var110 / 2.0F + 3.0F, 10.0F, "j", var47);
                  }

                  if (var11 && handle(var9, var10, var66, var65, var67, var23)) {
                     var73.process(!var73.compute());
                     HudProfileConfig.resolve();
                  }

                  var65 += var23;
               } else if (var72 instanceof NumberSetting var74) {
                  String var109 = process(var74.compute());
                  float var113 = TextMeasureCache.handle(FontRegistry.instance, var109, var69).instance;
                  float var118 = var67 - var113 - 6.0F;
                  handle(var0, FontRegistry.instance, var74.instance, var66, var65 + 13.0F, var68, var102, var65, var24, var118);
                  var0.handle(FontRegistry.instance, var66 + var67 - var113, var65 + 13.0F, var69, var109, var45);
                  float var122 = var66;
                  float var125 = var65 + var24 - 5.0F;
                  float var128 = var67;
                  boolean var131 = var12 && handle(var9, var10, var122 - 4.0F, var125 - 6.0F, var128 + 8.0F, 16.0F);
                  if (var49) {
                     ThemeShaderApplier.handle(
                        null, var122, var125 - 2.0F, var128, 7.0F, 3.5F, var50.distance(), var50.blur(), var50.intensity(), 2, true, var8
                     );
                  } else {
                     var0.handle(var122, var125, var128, 3.0F, 1.5F, PackedColor.compute(255, 255, 255, (int)(10.0F * var8)));
                  }

                  DoubleAnimator var134 = instance.computeIfAbsent(var14 + "_" + var74.instance, var0x -> new DoubleAnimator());
                  var134.handle();
                  float var136 = (var74.compute() - var74.state) / (var74.cache - var74.state);
                  var134.handle(var136, 0.2F, Easings.handler, false);
                  float var138 = var134.update();
                  var0.handle(var122, var125, var128 * var138, 3.0F, 1.5F, var45);
                  float var139 = 8.0F;
                  float var140 = 10.0F;
                  float var141 = var122 + var128 * var138 - var139 / 2.0F;
                  if (!var49
                     || !ThemeShaderApplier.handle(
                        null,
                        var141,
                        var125 - (var140 - 3.0F) / 2.0F,
                        var139,
                        var140,
                        2.0F,
                        var50.distance(),
                        var50.blur(),
                        var50.intensity(),
                        var131 ? 2 : 1,
                        var131,
                        var8
                     )) {
                     var0.handle(var141, var125 - (var140 - 3.0F) / 2.0F, var139, var140, 2.0F, PackedColor.compute(255, 255, 255, var41));
                  }

                  if (!var49) {
                     var0.handle(var141 + 2.5F, var125 - (var140 - 3.0F) / 2.0F + 2.5F, 1.0F, 5.0F, 0.5F, PackedColor.compute(100, 100, 100, var41));
                     var0.handle(var141 + 4.5F, var125 - (var140 - 3.0F) / 2.0F + 2.5F, 1.0F, 5.0F, 0.5F, PackedColor.compute(100, 100, 100, var41));
                  }

                  if (var131) {
                     float var142 = var74.state + (var9 - var122) / var128 * (var74.cache - var74.state);
                     var142 = Math.max(var74.state, Math.min(var74.cache, var142));
                     var142 = (float)(Math.round(var142 * (1.0 / var74.output)) / (1.0 / var74.output));
                     var74.handle(var142);
                     HudProfileConfig.resolve();
                  }

                  var65 += var24;
               } else if (var72 instanceof ModeSetting var75) {
                  String var108 = var75.compute();
                  float var112 = TextMeasureCache.handle(FontRegistry.instance, var108, var69).instance;
                  float var117 = var67 - var112 - 6.0F;
                  handle(var0, FontRegistry.instance, var75.instance, var66, var65 + var25 / 2.0F + var70, var68, var102, var65, var25, var117);
                  float var121 = var66 + var67 - var112;
                  var0.handle(FontRegistry.instance, var121, var65 + var25 / 2.0F + var70, var69, var108, var47);
                  if (var11 && handle(var9, var10, var66, var65, var67, var25)) {
                     var75.current = (var75.current + 1) % var75.config.size();
                     var75.state = var75.config.get(var75.current);
                     HudProfileConfig.resolve();
                  }

                  var65 += var25;
               } else if (var72 instanceof ActionSetting var76) {
                  String var107 = var76.update();
                  float var111 = TextMeasureCache.handle(FontRegistry.instance, var107, var69).instance;
                  float var115 = Math.max(70.0F, var111 + 18.0F);
                  var115 = Math.min(var115, var67 * 0.55F);
                  float var120 = 16.0F;
                  float var124 = var66 + var67 - var115;
                  float var127 = var65 + (var26 - var120) / 2.0F;
                  float var130 = var124 - var66 - 6.0F;
                  handle(var0, FontRegistry.instance, var76.instance, var66, var65 + var26 / 2.0F + var70, var68, var102, var65, var26, var130);
                  boolean var133 = var12 && handle(var9, var10, var124, var127, var115, var120);
                  if (!var49
                     || !ThemeShaderApplier.handle(
                        null, var124, var127, var115, var120, 4.0F, var50.distance(), var50.blur(), var50.intensity(), var133 ? 2 : 1, var133, var8
                     )) {
                     var0.handle(var124, var127, var115, var120, 4.0F, PackedColor.compute(255, 255, 255, (int)(14.0F * var8)));
                     var0.handle(var124, var127, var115, var120, 4.0F, var44, 0.6F);
                  }

                  String var135 = handle(var107, FontRegistry.instance, var69, var115 - 8.0F);
                  float var137 = TextMeasureCache.handle(FontRegistry.instance, var135, var69).instance;
                  var0.handle(FontRegistry.instance, var124 + (var115 - var137) / 2.0F, var127 + var120 / 2.0F + var70, var69, var135, var47);
                  if (var11 && handle(var9, var10, var124, var127, var115, var120)) {
                     var76.resolve();
                     HudProfileConfig.resolve();
                  }

                  var65 += var26;
               } else if (var72 instanceof ChoiceSetting var77) {
                  float var78 = var77.output.update();
                  float var79 = 26.0F;
                  float var80 = TextMeasureCache.handle(FontRegistry.cache, "m", var79).instance;
                  float var81 = var67 - var80 - 6.0F;
                  handle(var0, FontRegistry.instance, var77.instance, var66, var65 + var23 / 2.0F + var70, var68, var102, var65, var23, var81);
                  float var82 = var66 + var67 - var80 / 2.0F;
                  float var83 = var65 + var23 / 2.0F;
                  var0.handle(var82, var83);
                  var0.handle(FontRegistry.cache, -var80 / 2.0F, var79 / 3.0F, var79, "m", var47);
                  var0.prepare();
                  if (var11 && handle(var9, var10, var66, var65, var67, var23)) {
                     var77.state = !var77.state;
                  }

                  var65 += var23;
                  if (var78 > 0.001F) {
                     float var84 = handle(var77) * var23;
                     float var85 = var84 * var78;
                     var0.handle(var61, var65, var63, var85, 0.0F, 0.0F, 0.0F, 0.0F);
                     float var86 = var65;
                     float var87 = var65 + var85;
                     float var88 = var65 - var84 * (1.0F - var78);

                     for (BooleanSetting var90 : var77.config) {
                        if (handle(var90)) {
                           float var91 = 12.0F;
                           float var92 = var66 + var67 - var91;
                           float var93 = var88 + (var23 - var91) / 2.0F;
                           float var94 = var67 - 10.0F - var91 - 6.0F;
                           handle(var0, FontRegistry.instance, var90.instance, var66 + 10.0F, var88 + var23 / 2.0F + var70, var69, var47, var88, var23, var94);
                           var90.state.handle();
                           var90.state.handle(var90.compute() ? 1.0 : 0.0, 0.15F, Easings.handler, false);
                           int var95 = PackedColor.compute(255, 255, 255, (int)(10.0F * var8));
                           boolean var96 = var12 && handle(var9, var10, var66, var88, var67, var23);
                           boolean var97 = var93 >= var86 && var93 + var91 <= var87;
                           if (!var49
                              || !var97
                              || !ThemeShaderApplier.handle(
                                 null, var92, var93, var91, var91, 3.0F, var50.distance(), var50.blur(), var50.intensity(), var96 ? 2 : 1, var96, var8 * var78
                              )) {
                              var0.handle(var92, var93, var91, var91, 3.0F, var95);
                           }

                           if (var90.compute()) {
                              float var98 = TextMeasureCache.handle(FontRegistry.current, "j", 10.0F).instance;
                              var0.handle(FontRegistry.current, var92 + (var91 - var98) / 2.0F, var93 + var91 / 2.0F + 3.0F, 10.0F, "j", var47);
                           }

                           if (var77.state && var11 && handle(var9, var10, var66, var88, var67, var23)) {
                              boolean var145 = var77.cache && var90.resolve() && var77.compute() <= 1;
                              if (!var145) {
                                 var90.process(!var90.compute());
                                 HudProfileConfig.resolve();
                              }
                           }

                           var88 += var23;
                        }
                     }

                     var0.apply();
                     var65 += var85;
                  }
               }
            }
         }

         var0.apply();
         var0.onTick();
      }
   }

   public static void handle(
      RoundedRectRenderer var0, ResettableSettingGroup var1, ThemeRenderer.PrimaryCacheEntry var2, ThemeRenderer var3, float var4, float var5
   ) {
      handle(var0, var1, var2.data, var2.context, var2.config, var2.state, var4, var5, var2.output, var3.execute(), var3.prepare(), var3.onTick(), var3.check());
   }

   private static boolean handle(Setting var0) {
      try {
         return var0 == null || var0.data == null || !var0.data.get();
      } catch (Throwable var2) {
         return true;
      }
   }

   private static int handle(ChoiceSetting var0) {
      int var1 = 0;

      for (BooleanSetting var3 : var0.config) {
         if (handle(var3)) {
            var1++;
         }
      }

      return var1;
   }

   private static boolean handle(ResettableSettingGroup var0) {
      for (Setting var2 : var0.handle()) {
         if (var2 instanceof ModeSetting var3 && var3.instance.equals("Стилистика")) {
            return ThemePresets.handle(var3.compute());
         }
      }

      return false;
   }

   private static ThemeShaderApplier.DataRecord process(ResettableSettingGroup var0) {
      float var1 = 5.5F;
      float var2 = 18.0F;
      float var3 = 0.72F;
      String var4 = "Выпуклая";

      for (Setting var6 : var0.handle()) {
         if (var6 instanceof NumberSetting var7) {
            if (var7.instance.equals("Нео дистанция")) {
               var1 = var7.compute();
            } else if (var7.instance.equals("Нео размытие")) {
               var2 = var7.compute();
            } else if (var7.instance.equals("Нео интенсивность")) {
               var3 = var7.compute();
            }
         } else if (var6 instanceof ModeSetting var8 && var8.instance.equals("Нео форма")) {
            var4 = var8.compute();
         }
      }

      return ThemeShaderApplier.handle(var1, var2, var3, var4);
   }

   private static String handle(String var0, FontObject var1, float var2, float var3) {
      if (var0 == null || var3 <= 0.0F) {
         return "";
      }

      if (TextMeasureCache.handle(var1, var0, var2).instance <= var3) {
         return var0;
      }

      String var4 = "...";
      float var5 = TextMeasureCache.handle(var1, var4, var2).instance;
      if (var5 > var3) {
         return "";
      }

      int var6 = var0.length();

      while (var6 > 0 && TextMeasureCache.handle(var1, var0.substring(0, var6), var2).instance + var5 > var3) {
         var6--;
      }

      return var6 <= 0 ? var4 : var0.substring(0, var6) + var4;
   }

   private static void handle(
      RoundedRectRenderer var0, FontObject var1, String var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9
   ) {
      float var10 = TextMeasureCache.handle(var1, var2, var5).instance;
      if (var10 <= var9) {
         var0.handle(var1, var3, var4, var5, var2, var6);
      } else {
         float var11 = var10 - var9;
         long var12 = 8000L;
         float var14 = (float)(System.currentTimeMillis() % var12) / (float)var12;
         float var15 = 0.0F;
         if (var14 < 0.2F) {
            var15 = 0.0F;
         } else if (var14 < 0.45F) {
            float var16 = (var14 - 0.2F) / 0.3F;
            var15 = handle(var16);
         } else if (var14 < 0.7F) {
            var15 = 1.0F;
         } else if (var14 < 0.95F) {
            float var18 = (var14 - 0.7F) / 0.25F;
            var15 = 1.0F - handle(var18);
         } else {
            var15 = 0.0F;
         }

         float var19 = var11 * var15;
         var0.handle(var3, var7, Math.max(1.0F, var9), var8, 0.0F, 0.0F, 0.0F, 0.0F);
         var0.handle(var1, var3 - var19, var4, var5, var2, var6);
         var0.apply();
      }
   }

   private static float handle(float var0) {
      float var1 = 2.0F;
      float var2 = var1 + 1.0F;
      float var3 = var0 - 1.0F;
      return 1.0F + var2 * var3 * var3 * var3 + var1 * var3 * var3;
   }

   private static String process(float var0) {
      int var1 = Math.round(var0 * 10.0F);
      return var1 / 10 + "." + Math.abs(var1 % 10);
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 <= var2 + var4 && var1 >= var3 && var1 <= var3 + var5;
   }

   public record Bounds(float x, float y, float width, float height) {
      public boolean contains(float var1, float var2, float var3) {
         return var1 >= this.x - var3 && var1 <= this.x + this.width + var3 && var2 >= this.y - var3 && var2 <= this.y + this.height + var3;
      }
   }
}
