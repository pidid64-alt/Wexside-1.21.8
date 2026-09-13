package ru.wild.gui.screen;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import ru.wild.WildClient;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.StringSetting;
import ru.wild.core.MinecraftContext;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.math.FrameInterpolation;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.InputNames;

public final class ModuleSettingListRenderer {
   private static final float instance = 12.0F;
   private static final float data = 10.0F;
   private static final float context = 4.0F;
   private static final float config = 12.0F;
   private static final float state = 10.0F;
   private static final float cache = 3.0F;
   private static final float output = 8.0F;
   private static final float current = 13.0F;
   private static final float active = 4.0F;
   private static final float mode = 5.0F;
   private static final Map<String, Float> selection = new HashMap<>();
   private static final Map<String, Float> enabled = new HashMap<>();
   private static final Map<String, Float> renderer = new HashMap<>();

   private ModuleSettingListRenderer() {
   }

   public static float handle(RoundedRectRenderer var0, Setting var1, float var2) {
      if (var1 instanceof FloatSetting) {
         return ((FloatSetting)var1).compute();
      } else if (var1 instanceof BooleanSetting) {
         return 13.0F;
      } else if (var1 instanceof ActionSetting) {
         return 14.0F;
      } else if (var1 instanceof NumberSetting) {
         return 22.0F;
      } else if (var1 instanceof ModeSetting var5) {
         float var6 = handle(var0, var5.config.toArray(new String[0]), var2);
         return 10.0F + var6 * 15.0F;
      } else if (var1 instanceof KeybindSetting) {
         return 12.0F;
      } else if (var1 instanceof StringSetting) {
         return 14.0F;
      } else if (var1 instanceof ColorSetting) {
         return 14.0F;
      } else if (var1 instanceof ChoiceSetting var3) {
         float var4 = handle(var0, var3.config.stream().map(var0x -> var0x.instance).toArray(String[]::new), var2);
         return 10.0F + var4 * 15.0F;
      } else {
         return 12.0F;
      }
   }

   public static float handle(RoundedRectRenderer var0, Iterable<Setting> var1, float var2) {
      float var3 = 0.0F;

      for (Setting var5 : var1) {
         if (var5 != null && !var5.data.get()) {
            var3 += handle(var0, var5, var2) + 4.0F;
         }
      }

      return Math.max(0.0F, var3 - 4.0F);
   }

   public static float handle(
      RoundedRectRenderer var0,
      Setting var1,
      float var2,
      float var3,
      float var4,
      int var5,
      int var6,
      float var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12
   ) {
      if (var7 <= 0.01F) {
         return 0.0F;
      }

      if (var1 instanceof FloatSetting) {
         return ((FloatSetting)var1).compute();
      }

      if (var1 instanceof BooleanSetting var33) {
         float var40 = var2 + var4 - 8.0F;
         float var47 = var3 + 2.0F;
         var0.handle(var40, var47, 8.0F, 8.0F, 2.5F, var8, 0.4F);
         var0.handle(var40, var47, 8.0F, 8.0F, 2.5F, var12);
         if (var33.compute()) {
            var0.handle(var40 + 2.0F, var47 + 2.0F, 4.0F, 4.0F, 2.0F, var9);
         }

         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            Math.max(12.0F, var40 - var2 - 4.0F),
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 13.0F)
         );
         return 13.0F;
      } else if (var1 instanceof NumberSetting var32) {
         float var39 = var3 + 12.0F;
         float var46 = var2 + 4.0F;
         float var55 = var4 - 8.0F;
         float var61 = var32.state;
         float var64 = var32.cache;
         float var67 = NumericTransform.onTick(var32.config, var61, var64);
         float var71 = var64 - var61 > 1.0E-5F ? (var67 - var61) / (var64 - var61) : 0.0F;
         EasedDoubleAnimator var74 = BlurStateManager.handle(var32);
         var74.handle();
         var74.handle(var71, 0.18F, EasingFunctions.handler, true);
         float var76 = var74.update();
         float var77 = var55 * var76;
         String var24 = handle(var67, var32.mode);
         float var25 = RoundedRectRenderer.handle(FontRegistry.instance, var24, 10.0F).instance;
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            Math.max(12.0F, var4 - var25 - 10.0F),
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 22.0F)
         );
         var0.handle(var46, var39, var55, 4.0F, 2.0F, var12);
         if (var77 > 0.5F) {
            var0.handle(var46, var39, var77, 4.0F, 2.0F, var9);
         }

         float var26 = var46 + var77 - 2.5F;
         var0.handle(var26, var39 - 0.5F, 5.0F, 5.0F, 2.0F, var11);
         var0.handle(FontRegistry.instance, var2 + var4 - var25, var3 + 2.0F + 6.5F, 10.0F, var24, var10);
         return 22.0F;
      } else if (var1 instanceof ActionSetting var31) {
         String var38 = var31.update();
         float var45 = RoundedRectRenderer.handle(FontRegistry.instance, var38, 10.0F).instance;
         float var54 = Math.max(32.0F, var45 + 12.0F);
         float var60 = var2 + var4 - var54;
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            Math.max(12.0F, var60 - var2 - 4.0F),
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 14.0F)
         );
         var0.handle(var60, var3 + 1.0F, var54, 11.0F, 3.0F, var8, 0.4F);
         var0.handle(var60, var3 + 1.0F, var54, 11.0F, 3.0F, var12);
         var0.handle(FontRegistry.instance, var60 + var54 * 0.5F - var45 * 0.5F, var3 + 2.0F + 6.5F, 10.0F, var38, var10);
         return 14.0F;
      } else if (var1 instanceof ModeSetting var30) {
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            var4,
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 14.0F)
         );
         float var37 = var2;
         float var44 = var3 + 12.0F;

         for (String var59 : var30.config) {
            float var63 = RoundedRectRenderer.handle(FontRegistry.instance, var59, 10.0F).instance + 20.0F;
            if (var37 + var63 > var2 + var4 && var37 > var2) {
               var37 = var2;
               var44 += 15.0F;
            }

            boolean var66 = var59.equals(var30.state);
            String var70 = var1.instance + ":" + var59;
            selection.putIfAbsent(var70, var66 ? 1.0F : 0.0F);
            float var72 = selection.get(var70);
            var72 = FrameInterpolation.handle(var72, var66 ? 1.0F : 0.0F, 10.0F);
            selection.put(var70, var72);
            int var75 = PackedColor.update(var12, var9, var72 * 0.45F);
            int var23 = PackedColor.update(var10, var11, var72);
            var0.handle(var37, var44, var63, 12.0F, 3.0F, var8, 0.4F);
            var0.handle(var37, var44, var63, 12.0F, 3.0F, var75);
            var0.handle(FontRegistry.instance, var37 + 10.0F, var44 + 2.0F + 5.5F, 10.0F, var59, var23);
            var37 += var63 + 3.0F;
         }

         float var53 = handle(var0, var30.config.toArray(new String[0]), var4);
         return 10.0F + var53 * 15.0F;
      } else if (var1 instanceof KeybindSetting var29) {
         String var36 = var29.output ? "..." : InputNames.process(var29.config);
         float var43 = RoundedRectRenderer.handle(FontRegistry.instance, var36, 10.0F).instance;
         float var51 = Math.max(22.0F, var43 + 8.0F);
         float var58 = var2 + var4 - var51;
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            Math.max(12.0F, var58 - var2 - 4.0F),
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 12.0F)
         );
         var0.handle(var58, var3 + 1.0F, var51, 11.0F, 3.0F, var8, 0.4F);
         var0.handle(var58, var3 + 1.0F, var51, 11.0F, 3.0F, var12);
         var0.handle(FontRegistry.instance, var58 + var51 * 0.5F - var43 * 0.5F, var3 + 2.0F + 6.5F, 10.0F, var36, var29.output ? var9 : var10);
         return 12.0F;
      } else if (var1 instanceof StringSetting var28) {
         float var35 = var4 * 0.35F;
         float var42 = var2 + var4 - var35;
         float var50 = var3 + 1.0F;
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            Math.max(12.0F, var42 - var2 - 4.0F),
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 14.0F)
         );
         var0.handle(var42, var50, var35, 11.0F, 3.0F, var8, 0.4F);
         var0.handle(var42, var50, var35, 11.0F, 3.0F, var12);
         String var57 = var28.state == null ? "" : var28.state;
         if (!var57.isEmpty()) {
            var0.handle(FontRegistry.instance, var42 + 4.0F, var50 + 2.0F + 5.5F, 10.0F, var57, var11);
         }

         return 14.0F;
      } else if (var1 instanceof ColorSetting var27) {
         float var34 = 36.0F;
         float var41 = 11.0F;
         float var49 = var2 + var4 - var34;
         float var56 = var3 + 1.5F;
         int var62 = var27.prepare();
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            Math.max(12.0F, var49 - var2 - 6.0F),
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 14.0F)
         );
         var0.handle(var49, var56, var34, var41, 3.0F, var8, 0.4F);
         var0.handle(var49, var56, var34, var41, 3.0F, var12);
         var0.handle(var49 + 2.0F, var56 + 2.0F, var34 - 4.0F, var41 - 4.0F, 2.0F, RoundedRectRenderer.ColorState.select(var62, (int)(255.0F * var7)));
         String var65 = String.format("#%02X%02X%02X", PackedColor.process(var62), PackedColor.compute(var62), PackedColor.resolve(var62));
         float var69 = RoundedRectRenderer.handle(FontRegistry.instance, var65, 10.0F).instance;
         var0.handle(FontRegistry.instance, var49 - var69 - 4.0F, var3 + 2.0F + 6.5F, 10.0F, var65, var10);
         return 14.0F;
      } else if (var1 instanceof ChoiceSetting var13) {
         handle(
            var0,
            var1.instance,
            var2,
            var3 + 2.0F + 6.5F,
            12.0F,
            var11,
            var2,
            var3 + 1.0F,
            var4,
            11.0F,
            NumericTransform.handle(var5, var6, var2, var3, var4, 14.0F)
         );
         float var14 = var2;
         float var15 = var3 + 12.0F;

         for (BooleanSetting var17 : var13.config) {
            float var18 = RoundedRectRenderer.handle(FontRegistry.instance, var17.instance, 10.0F).instance + 20.0F;
            if (var14 + var18 > var2 + var4 && var14 > var2) {
               var14 = var2;
               var15 += 15.0F;
            }

            String var19 = var1.instance + ":" + var17.instance;
            enabled.putIfAbsent(var19, var17.compute() ? 1.0F : 0.0F);
            float var20 = enabled.get(var19);
            var20 = FrameInterpolation.handle(var20, var17.compute() ? 1.0F : 0.0F, 10.0F);
            enabled.put(var19, var20);
            int var21 = PackedColor.update(var12, var9, var20 * 0.45F);
            int var22 = PackedColor.update(var10, var11, var20);
            var0.handle(var14, var15, var18, 12.0F, 3.0F, var8, 0.4F);
            var0.handle(var14, var15, var18, 12.0F, 3.0F, var21);
            var0.handle(FontRegistry.instance, var14 + 10.0F, var15 + 2.0F + 5.5F, 10.0F, var17.instance, var22);
            var14 += var18 + 3.0F;
         }

         float var48 = handle(var0, var13.config.stream().map(var0x -> var0x.instance).toArray(String[]::new), var4);
         return 10.0F + var48 * 15.0F;
      } else {
         return 12.0F;
      }
   }

   public static boolean handle(RoundedRectRenderer var0, Setting var1, float var2, float var3, float var4, int var5, int var6, int var7) {
      if (var1 instanceof BooleanSetting) {
         float var8 = var2 + var4 - 8.0F;
         float var9 = var3 + 2.0F;
         if (var7 == 0 && NumericTransform.handle(var5, var6, var8, var9, 8.0F, 8.0F)) {
            BooleanSetting var34 = (BooleanSetting)var1;
            var34.process(!var34.compute());
            process();
            return true;
         }
      }

      if (var1 instanceof NumberSetting) {
         float var14 = var3 + 12.0F;
         float var21 = var2 + 4.0F;
         float var10 = var4 - 8.0F;
         if (var7 == 0 && NumericTransform.handle(var5, var6, var21, var14, var10, 6.0F)) {
            NumberSetting var40 = (NumberSetting)var1;
            BlurStateManager.summary = var40;
            BlurStateManager.vectorMatch = var21;
            BlurStateManager.itemProject = var14;
            BlurStateManager.responseCompute = var10;
            handle(var40, var5);
            process();
            return true;
         }
      }

      if (var1 instanceof ActionSetting var15) {
         String var22 = var15.update();
         float var28 = RoundedRectRenderer.handle(FontRegistry.instance, var22, 10.0F).instance;
         float var11 = Math.max(32.0F, var28 + 12.0F);
         float var12 = var2 + var4 - var11;
         if (var7 == 0 && NumericTransform.handle(var5, var6, var12, var3 + 1.0F, var11, 11.0F)) {
            var15.resolve();
            return true;
         }
      }

      if (var1 instanceof ModeSetting var16) {
         float var23 = var2;
         float var29 = var3 + 12.0F;

         for (String var41 : var16.config) {
            float var13 = RoundedRectRenderer.handle(FontRegistry.instance, var41, 10.0F).instance + 20.0F;
            if (var23 + var13 > var2 + var4 && var23 > var2) {
               var23 = var2;
               var29 += 15.0F;
            }

            if (var7 == 0 && NumericTransform.handle(var5, var6, var23, var29, var13, 12.0F)) {
               var16.state = var41;
               var16.current = var16.config.indexOf(var41);
               process();
               return true;
            }

            var23 += var13 + 3.0F;
         }
      }

      if (var1 instanceof KeybindSetting var17) {
         String var24 = var17.output ? "..." : InputNames.process(var17.config);
         float var30 = RoundedRectRenderer.handle(FontRegistry.instance, var24, 10.0F).instance;
         float var36 = Math.max(22.0F, var30 + 8.0F);
         float var42 = var2 + var4 - var36;
         if (NumericTransform.handle(var5, var6, var42, var3 + 1.0F, var36, 11.0F) && var7 == 0) {
            if (BlurStateManager.previous != var17) {
               if (BlurStateManager.previous != null) {
                  BlurStateManager.previous.output = false;
               }

               BlurStateManager.previous = var17;
               var17.output = true;
            }

            return true;
         }
      }

      if (var1 instanceof StringSetting) {
         float var18 = var4 * 0.55F;
         float var25 = var2 + var4 - var18;
         float var31 = var3 + 1.0F;
         if (var7 == 0 && NumericTransform.handle(var5, var6, var25, var31, var18, 11.0F)) {
            StringSetting var39 = (StringSetting)var1;
            if (BlurStateManager.latest != var39) {
               if (BlurStateManager.latest != null) {
                  BlurStateManager.latest.current = false;
               }

               BlurStateManager.latest = var39;
               var39.current = true;
            }

            return true;
         }
      }

      if (var1 instanceof ColorSetting var19) {
         float var26 = 36.0F;
         float var32 = 11.0F;
         float var37 = var2 + var4 - var26;
         float var43 = var3 + 1.5F;
         if (var7 == 0 && NumericTransform.handle(var5, var6, var37, var43, var26, var32)) {
            handle(var19, var37, var43);
            return true;
         }
      }

      if (var1 instanceof ChoiceSetting var20) {
         float var27 = var2;
         float var33 = var3 + 12.0F;

         for (BooleanSetting var44 : var20.config) {
            float var45 = RoundedRectRenderer.handle(FontRegistry.instance, var44.instance, 10.0F).instance + 20.0F;
            if (var27 + var45 > var2 + var4 && var27 > var2) {
               var27 = var2;
               var33 += 15.0F;
            }

            if (var7 == 0 && NumericTransform.handle(var5, var6, var27, var33, var45, 12.0F)) {
               var44.process(!var44.compute());
               process();
               return true;
            }

            var27 += var45 + 3.0F;
         }
      }

      return false;
   }

   public static float handle() {
      return 4.0F;
   }

   private static float handle(RoundedRectRenderer var0, String[] var1, float var2) {
      float var3 = 1.0F;
      float var4 = 0.0F;

      for (String var8 : var1) {
         float var9 = RoundedRectRenderer.handle(FontRegistry.instance, var8, 10.0F).instance + 20.0F;
         if (var4 + var9 > var2 && var4 > 0.0F) {
            var3++;
            var4 = 0.0F;
         }

         var4 += var9 + 3.0F;
      }

      return var3;
   }

   private static void handle(NumberSetting var0, float var1) {
      float var2 = var0.state;
      float var3 = var0.cache;
      float var4 = (var1 - BlurStateManager.vectorMatch) / BlurStateManager.responseCompute;
      var4 = NumericTransform.onTick(var4, 0.0F, 1.0F);
      float var5 = var2 + (var3 - var2) * var4;
      float var6 = var0.output;
      if (var6 > 1.0E-5F) {
         var5 = Math.round(var5 / var6) * var6;
      }

      var0.config = NumericTransform.onTick(var5, var2, var3);
   }

   private static void handle(ColorSetting var0, float var1, float var2) {
      if (BlurStateManager.animationDraw == var0) {
         BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
         BlurStateManager.animationDraw = null;
         BlurStateManager.pointEncode = 0.0F;
         BlurStateManager.animator = 0.0F;
      } else {
         BlurStateManager.animationDraw = var0;
         BlurStateManager.cache.process(AnimationDirection.FORWARDS);
         float var3 = 160.0F;
         float var4 = 119.0F;
         float var5 = MinecraftContext.toggleState.getWindow().getScaledWidth();
         float var6 = MinecraftContext.toggleState.getWindow().getScaledHeight();
         float var7 = var1 + 40.0F;
         float var8 = var2 - 4.0F;
         if (var7 + var3 > var5 - 6.0F) {
            var7 = var1 - var3 - 6.0F;
         }

         var7 = NumericTransform.onTick(var7, 6.0F, var5 - var3 - 6.0F);
         var8 = NumericTransform.onTick(var8, 6.0F, var6 - var4 - 6.0F);
         BlurStateManager.pointEncode = var7;
         BlurStateManager.animator = var8;
      }
   }

   private static String handle(float var0, boolean var1) {
      if (var1) {
         return String.format("%.1f%%", var0);
      }

      if (Math.abs(var0 - Math.round(var0)) < 0.001F) {
         return String.format("%.0f", var0);
      }

      DecimalFormat var2 = new DecimalFormat("#.#");
      return var2.format(var0);
   }

   private static void process() {
      if (WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   private static void handle(
      RoundedRectRenderer var0, String var1, float var2, float var3, float var4, int var5, float var6, float var7, float var8, float var9, boolean var10
   ) {
      if (var1 != null && !var1.isEmpty() && !(var8 <= 3.0F) && !(var9 <= 2.0F)) {
         float var11 = RoundedRectRenderer.handle(FontRegistry.instance, var1, var4).instance;
         if (var11 <= var8 - 1.0F) {
            var0.handle(FontRegistry.instance, var2, var3, var4, var1, var5);
         } else {
            String var12 = var1 + "|" + var4;
            renderer.putIfAbsent(var12, 0.0F);
            float var13 = var10 ? 1.0F : 0.0F;
            float var14 = FrameInterpolation.handle(renderer.get(var12), var13, 12.0F);
            renderer.put(var12, var14);
            float var15 = var11 - var8;
            float var16 = (float)((Math.sin(System.currentTimeMillis() * 0.0035) + 1.0) * 0.5);
            float var17 = var15 * var16 * var14;
            var0.handle(var6, var7, var8, var9, 0.0F, 0.0F, 0.0F, 0.0F);
            var0.handle(FontRegistry.instance, var2 - var17, var3, var4, var1, var5);
            var0.apply();
         }
      }
   }
}
