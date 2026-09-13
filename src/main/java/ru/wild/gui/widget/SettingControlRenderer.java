package ru.wild.gui.widget;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.MultiSelectSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.core.FoundryEntityPreviewRenderer;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.ColorPickerShader;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

public final class SettingControlRenderer {
   public static final float instance = 186.0F;
   private static final long data = 5200L;
   private static final int context = -1577754;
   private static final int config = -3945532;

   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, Setting var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      switch (var3) {
         case BooleanSetting var10:
            this.handle(var1, var2, var10, var4, var5, var6, var7);
            break;
         case NumberSetting var11:
            this.handle(var1, var2, var11, var4, var5, var6, var7);
            break;
         case ColorSetting var12:
            this.handle(var1, var2, var12, var4, var5, var6, var7);
            break;
         case ModeSetting var13:
            this.handle(var1, var2, var13, var4, var5, var6, var7);
            break;
         case ShaderPresetSetting var14:
            this.handle(var1, var2, var14, var4, var5, var6, var7);
            break;
         case ChoiceSetting var15:
            this.handle(var1, var2, var15, var4, var5, var6, var7);
            break;
         case MultiSelectSetting var16:
            this.handle(var1, var2, var16, var16.instance, var16.output.isEmpty() ? "none" : var16.update(), var4, var5, var6, var7);
            break;
         case KeybindSetting var17:
            String var20 = var2.checkKey() == var17 ? "..." : (var17.config == -1 ? "n/a" : KeycodeNames.handle(var17.config));
            this.handle(var1, var2, var17, var17.instance, var20, var4, var5, var6, var7);
            break;
         case StringSetting var18:
            String var21 = var2.filterEntity() == var18 ? var18.state + "|" : var18.state;
            this.handle(var1, var2, var18, var18.instance, var21.isEmpty() ? "empty" : var21, var4, var5, var6, var7);
            break;
         case ActionSetting var19:
            this.handle(var1, var2, var19, var19.instance, var19.update(), var4, var5, var6, var7);
            break;
         default:
      }
   }

   public float handle(Setting var1, GuiMetrics var2, ModernClickGuiState var3) {
      return switch (var1) {
         case NumberSetting var6 -> var2.handle(22.0F);
         case ShaderPresetSetting var7 -> var2.handle(18.0F);
         case ColorSetting var8 -> {
            float var11 = var3.handle(UiAnimationKeys.prepare(var8));
            yield var2.handle(16.0F) + var2.handle(186.0F) * var11;
         }
         case FloatSetting var9 -> var2.handle(var9.compute());
         case ChoiceSetting var10 -> this.handle(var10, var2);
         default -> var2.handle(14.0F);
      };
   }

   public float handle(Setting var1, GuiMetrics var2) {
      return switch (var1) {
         case NumberSetting var5 -> var2.handle(22.0F);
         case ShaderPresetSetting var6 -> var2.handle(18.0F);
         case ColorSetting var7 -> var2.handle(22.0F);
         case FloatSetting var8 -> var2.handle(var8.compute());
         case ChoiceSetting var9 -> this.handle(var9, var2);
         default -> var2.handle(14.0F);
      };
   }

   private float handle(ChoiceSetting var1, GuiMetrics var2) {
      float var3 = (var2.drawAnimation() - var2.handle(32.0F)) * 0.7F;
      int var4 = ModuleStateHelper.handle(var1, var3, var2);
      float var5 = var2.handle(14.0F);
      float var6 = var2.handle(3.0F);
      return var2.handle(2.0F) + var4 * var5 + (var4 > 1 ? (var4 - 1) * var6 : 0.0F);
   }

   public static float handle(float var0) {
      return var0 * 0.4F;
   }

   public static float handle(float var0, float var1) {
      return var0 + var1 - handle(var1);
   }

   public static float handle(ModeSetting var0, float var1, GuiMetrics var2) {
      float var3 = var1 * 0.52F;
      float var4 = ModuleStateHelper.handle(FontRegistry.instance, var0.state, 10.0F);
      return Math.max(var2.handle(52.0F), Math.min(var3, var4 + var2.handle(26.0F)));
   }

   public static float handle(ModeSetting var0, float var1, float var2, GuiMetrics var3) {
      return var1 + var2 - handle(var0, var2, var3);
   }

   public static float handle(float var0, GuiMetrics var1) {
      return var0 - var1.handle(1.0F);
   }

   public static float handle(GuiMetrics var0) {
      return var0.handle(16.0F);
   }

   public static float process(float var0) {
      return var0;
   }

   public static float process(float var0, float var1) {
      return var0;
   }

   public static float handle(ShaderPresetSetting var0, float var1, GuiMetrics var2) {
      String var3 = var0 == null ? "None" : var0.prepare();
      float var4 = var1 * 0.62F;
      float var5 = ModuleStateHelper.handle(FontRegistry.instance, var3, 10.0F);
      return Math.max(var2.handle(86.0F), Math.min(var4, var5 + var2.handle(38.0F)));
   }

   public static float handle(ShaderPresetSetting var0, float var1, float var2, GuiMetrics var3) {
      return var1 + var2 - handle(var0, var2, var3);
   }

   public static float process(float var0, GuiMetrics var1) {
      return var0 - var1.handle(1.0F);
   }

   public static float process(GuiMetrics var0) {
      return var0.handle(18.0F);
   }

   public static float handle(ShaderPresetSetting var0, GuiMetrics var1) {
      int var2 = var0 == null ? 1 : Math.max(1, var0.compute().size());
      return var1.handle(8.0F) + var2 * compute(var1) + var1.handle(6.0F);
   }

   public static float compute(GuiMetrics var0) {
      return var0.handle(58.0F);
   }

   public static float handle(float var0, float var1, GuiMetrics var2) {
      return var0 + var1 - var2.handle(12.0F) - var2.handle(3.0F);
   }

   public static float compute(float var0, GuiMetrics var1) {
      return var0 - var1.handle(1.0F);
   }

   public static float resolve(GuiMetrics var0) {
      return var0.handle(18.0F);
   }

   public static float process(float var0, float var1, GuiMetrics var2) {
      return var0 + var1 - var2.handle(12.0F) - var2.handle(3.0F);
   }

   public static float resolve(float var0, GuiMetrics var1) {
      return var0 - var1.handle(2.0F);
   }

   public static float update(GuiMetrics var0) {
      return var0.handle(18.0F);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, BooleanSetting var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      float var10 = var2.handle(UiAnimationKeys.handle(var3), var3.compute() ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      float var11 = var8.handle(12.0F);
      float var12 = var4 + var6 - var11;
      boolean var13 = var2.projectLayer() == var3;
      String var14 = var13 ? var3.instance + " ..." : ModuleStateHelper.handle(var3);
      this.handle(var1, var8, var14, var4, var5, var8.handle(14.0F), 12.0F, var12 - var4 - var8.handle(8.0F), ModuleStateHelper.handle(var9));
      String var15 = UiAnimationKeys.compute(var3);
      float var16 = var2.handle(
         var15,
         ModuleStateHelper.handle(var2, var12 - var8.handle(3.0F), var5 - var8.handle(2.0F), var11 + var8.handle(6.0F), var11 + var8.handle(6.0F))
            ? 1.0F
            : 0.0F,
         SpringAnimationSpec.onTick()
      );
      float var17 = ModuleStateHelper.handle(var16, var2.process(var15));
      float var18 = var5 + var8.handle(1.0F);
      float var19 = Math.max(0.0F, Math.min(1.0F, var10));
      var1.handle(var17, var12 + var11 * 0.5F, var18 + var11 * 0.5F);

      try {
         float var20 = var19 * var19 * (3.0F - 2.0F * var19);
         float var21 = Math.max(0.5F, var8.handle(1.0F));
         var1.handle(var12, var18, var11, var11, var8.handle(4.0F), ThemeColors.handle(var9.check(), var9.select(), var16 * 0.4F));
         var1.handle(
            var12 + var21,
            var18 + var21,
            var11 - var21 * 2.0F,
            var11 - var21 * 2.0F,
            Math.max(0.0F, var8.handle(3.0F) - var21),
            ThemeColors.handle(var9.save(), Math.round(18.0F + 78.0F * var19))
         );
         var1.handle(
            var12,
            var18,
            var11,
            var11,
            var8.handle(4.0F),
            ThemeColors.handle(var9.select(), ThemeColors.handle(var9.save(), 104), Math.max(var19 * 0.52F, var16)),
            0.5F
         );
         if (var19 > 0.001F) {
            float var22 = 7.0F;
            float var23 = ModuleStateHelper.handle(FontRegistry.current, "j", var22);
            var1.handle(0.66F + 0.34F * var20, var12 + var11 * 0.5F, var18 + var11 * 0.5F);

            try {
               ModuleStateHelper.handle(
                  var1,
                  var8,
                  FontRegistry.current,
                  var12 + (var11 - var23) * 0.5F,
                  var18,
                  var11,
                  var22,
                  "j",
                  ThemeColors.handle(var9.load(), Math.round(238.0F * var19))
               );
            } finally {
               var1.check();
            }
         }
      } finally {
         var1.check();
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, NumberSetting var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      float var10 = (var3.config - var3.state) / (var3.cache - var3.state);
      String var11 = UiAnimationKeys.handle(var3) + "_prog";
      float var12 = var2.handle(var11, var10, SpringAnimationSpec.onTick());
      float var13 = var2.handle(UiAnimationKeys.execute(var3));
      float var14 = Math.max(0.0F, Math.min(1.0F, var12 + (var10 - var12) * var13 * 0.85F));
      String var15 = UiAnimationKeys.process(var3);
      float var16 = var2.handle(
         var15, ModuleStateHelper.handle(var2, var4, var5 + var8.handle(11.0F), var6, var8.handle(14.0F)) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      long var17 = System.currentTimeMillis();
      float var19 = (float)(var17 % 1000L) / 1000.0F;
      float var20 = var10 - var12;
      float var21 = var3.state + var14 * (var3.cache - var3.state);
      String var22 = var3.resolve() ? var3.process(var3.config) : ModuleStateHelper.process(var21, var3.output);
      float var23 = ModuleStateHelper.handle(FontRegistry.config, var22, 12.0F);
      int var24 = ThemeColors.handle(ModuleStateHelper.process(var9), var9.save(), var13 * 0.8F);
      float var25 = var4 + var6 - var23;
      this.handle(var1, var8, var3.instance, var4, var5, var8.handle(14.0F), 12.0F, var25 - var4 - var8.handle(8.0F), ModuleStateHelper.handle(var9));
      ModuleStateHelper.handle(var1, var8, FontRegistry.config, var25, var5, var8.handle(14.0F), 12.0F, var22, var24);
      float var26 = var5 + var8.handle(17.0F);
      float var27 = var8.handle(5.0F);
      float var28 = var27 * 0.5F;
      float var29 = var6 * var14;
      var1.handle(var4, var26, var6, var27, var28, ThemeColors.handle(var9.check(), var9.select(), var16 * 0.42F));
      int var30 = var3.resolve() ? var3.enabled.length - 1 : 5;

      for (int var31 = 1; var31 < var30; var31++) {
         float var32 = var4 + var6 * var31 / var30 - var8.handle(0.5F);
         var1.handle(var32, var26 + var8.handle(1.0F), var8.handle(1.0F), var27 - var8.handle(2.0F), var8.handle(0.5F), var9.select());
      }

      if (var29 > 1.0F) {
         var1.handle(var4, var26, var29, var27, var28, var9.submit(), var9.save());
         float var48 = 0.75F + 0.25F * (float)Math.sin(var19 * Math.PI * 2.0);
         float var50 = var14 * (1.0F + var13 * 0.6F);
         int var33 = var9.unload()
            ? ThemeColors.handle(46, 59, 70, Math.round((18.0F + var13 * 10.0F) * var48 * var50))
            : ThemeColors.handle(var9.save(), Math.round((22.0F + var13 * 18.0F) * var48 * var50));
         var1.handle(
            var4,
            var26,
            var29,
            var27,
            var28,
            var8.handle((var9.unload() ? 7 : 10) + var13 * 6.0F) * var48 * var50,
            var8.handle(var9.unload() ? 1.5F : 2.0F),
            var33
         );
         float var34 = var8.handle(8.0F + var13 * 6.0F);
         float var35 = Math.max(var4, var4 + var29 - var34);
         var1.handle(
            var35,
            var26 - var8.handle(0.5F),
            Math.min(var34, var29),
            var27 + var8.handle(1.0F),
            var28,
            ThemeColors.handle(var9.save(), 0),
            ThemeColors.handle(var9.save(), Math.round((40.0F + var13 * 35.0F) * var48 * var50))
         );
      }

      float var49 = Math.abs(var20);
      float var51 = var13 * Math.min(0.3F, var49 * 8.0F);
      float var52 = Math.min(0.5F, var49 * 5.0F + var51);
      float var53 = var8.handle(5.5F);
      float var54 = var53 * 2.0F;
      float var36 = ModuleStateHelper.handle(var16, var2.process(var15), 0.018F, 0.006F);
      float var37 = 1.0F + var13 * 0.12F;
      float var38 = var54 * (1.0F + var52) * var36 * var37;
      float var39 = var54 * (1.0F - var52 * 0.35F) * var36 * var37;
      float var40 = var39 * 0.5F;
      float var41 = var4 + var6 * var14;
      float var42 = Math.signum(var20) * Math.min(var8.handle(1.5F), var49 * var8.handle(20.0F));
      var41 += var42;
      float var43 = var41 - var38 * 0.5F;
      float var44 = var26 + (var27 - var39) * 0.5F;
      if (var13 > 0.01F) {
         float var45 = 0.6F + 0.4F * (float)Math.sin(var19 * Math.PI * 3.0);
         float var46 = var8.handle(14.0F) * var13 * var45;
         int var47 = var9.unload()
            ? ThemeColors.handle(46, 59, 70, Math.round(22.0F * var13 * var45))
            : ThemeColors.handle(var9.save(), Math.round(35.0F * var13 * var45));
         var1.handle(
            var43 - var8.handle(2.0F),
            var44 - var8.handle(2.0F),
            var38 + var8.handle(4.0F),
            var39 + var8.handle(4.0F),
            var40 + var8.handle(2.0F),
            var46,
            var8.handle(2.0F),
            var47
         );
      }

      if (var14 > 0.01F) {
         float var56 = 0.5F + 0.5F * (float)Math.sin(var19 * Math.PI * 2.0);
         int var59 = var9.unload()
            ? ThemeColors.handle(46, 59, 70, Math.round(14.0F * var14 * var56))
            : ThemeColors.handle(var9.save(), Math.round(18.0F * var14 * var56));
         var1.handle(
            var43 - var8.handle(1.0F),
            var44 - var8.handle(1.0F),
            var38 + var8.handle(2.0F),
            var39 + var8.handle(2.0F),
            var40 + var8.handle(1.0F),
            var8.handle(8.0F) * var56 * var14,
            var8.handle(1.0F),
            var59
         );
      }

      var1.handle(
         var43 + var8.handle(0.5F),
         var44 + var8.handle(1.0F),
         var38,
         var39,
         var40,
         var8.handle(3.0F),
         var8.handle(0.5F),
         var9.unload() ? ThemeColors.handle(46, 59, 70, 40) : ThemeColors.handle(0, 0, 0, 50)
      );
      var1.process(var43, var44, var38, var39, var40, ModuleStateHelper.update(var9), ModuleStateHelper.apply(var9));
      if (var14 > 0.01F) {
         float var57 = Math.max(var14, var13);
         var1.handle(
            var43 + var8.handle(1.0F),
            var44 + var8.handle(1.0F),
            var38 - var8.handle(2.0F),
            var39 - var8.handle(2.0F),
            Math.max(0.0F, var40 - var8.handle(1.0F)),
            ThemeColors.handle(var9.save(), Math.round((80.0F + var13 * 40.0F) * var57)),
            0.7F
         );
      }

      var1.handle(
         var41 - var53 * 0.4F,
         var44 + var8.handle(1.0F),
         var53 * 0.8F,
         var39 * 0.3F,
         var40 * 0.4F,
         var9.unload() ? ThemeColors.handle(46, 59, 70, 16) : ThemeColors.handle(var9.load(), 18)
      );
      float var58 = var26 - var8.handle(3.0F);
      float var60 = var27 + var8.handle(6.0F);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ColorSetting var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      float var10 = var2.handle(UiAnimationKeys.prepare(var3));
      float var11 = var2.handle(UiAnimationKeys.select(var3), var3.resolve(), SpringAnimationSpec.onTick());
      float var12 = var2.handle(UiAnimationKeys.refresh(var3), var3.animationDraw, SpringAnimationSpec.onTick());
      int var13 = ModuleStateHelper.compute(var11, var3.renderer, var3.handler, var12);
      float var14 = var8.handle(12.0F);
      float var15 = var4 + var6 - var14;
      float var16 = var5 + var8.handle(1.0F);
      float var17 = var8.handle(3.0F);
      this.handle(var1, var8, var3.instance, var4, var5, var8.handle(14.0F), 12.0F, var15 - var4 - var8.handle(8.0F), ModuleStateHelper.handle(var9));
      String var18 = UiAnimationKeys.compute(var3);
      float var19 = var2.handle(
         var18,
         ModuleStateHelper.handle(var2, var15 - var8.handle(3.0F), var16 - var8.handle(3.0F), var14 + var8.handle(6.0F), var14 + var8.handle(6.0F))
            ? 1.0F
            : 0.0F,
         SpringAnimationSpec.onTick()
      );
      var1.handle(ModuleStateHelper.handle(var19, var2.process(var18)), var15 + var14 * 0.5F, var16 + var14 * 0.5F);

      try {
         var1.handle(var15, var16, var14, var14, var17, var17, var17, var17);

         try {
            this.handle(var1, var15, var16, var14, var14, this.handle(var8, 0.74F), 1.0F);
         } finally {
            var1.apply();
         }

         var1.handle(var15, var16, var14, var14, var17, var13);
         var1.handle(var15, var16, var14, var14 * 0.55F, var17, var17, 0.0F, 0.0F, ThemeColors.handle(-1, 60), ThemeColors.handle(-1, 60), 0, 0);
         int var20 = ThemeColors.handle(var9.unload() ? -16777216 : -1, 102);
         var1.handle(var15, var16, var14, var14, var17, ThemeColors.handle(var20, ThemeColors.handle(var9.save(), 180), var19), 0.5F);
      } finally {
         var1.check();
      }

      if (var10 > 0.01F) {
         this.handle(var1, var2, var3, var4, var5 + var8.handle(16.0F), var6, var10, var7);
      }
   }
   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, ColorSetting var3, float var4, float var5, float var6, float var7, ThemeRenderContext var8
   ) {
      GuiMetrics var9 = var8.update();
      ThemeColors var10 = var8.apply();
      float var11 = var9.handle(186.0F) * var7;
      float var12 = var9.handle(5.0F);
      float var13 = var9.handle(12.0F);
      float var14 = var9.handle(5.0F);
      float var15 = var9.handle(9.0F);
      float var16 = var9.handle(16.0F);
      float var17 = var9.handle(16.0F);
      float var18 = var9.handle(14.0F);
      float var19 = var9.handle(12.0F);
      float var20 = var6 - var13 - var14;
      float var21 = var11 - var12 * 2.0F - var15 - var16 - var17 - var18 - var19 - var14 * 5.0F;
      float var22 = var5 + var12;
      float var23 = var4 + var20 + var14;
      float var24 = var22 + var21 + var14;
      float var25 = var24 + var15 + var14;
      float var26 = var25 + var16 + var14;
      float var27 = var26 + var17 + var14;
      float var28 = var27 + var18 + var14;
      float var29 = var9.handle(5.0F);
      if (var2.evaluateWorld() == var3 && var7 > 0.025F) {
         var2.convertWindow(var4);
         var2.writePreset(var22);
         var2.measureColor(Math.max(0.0F, var20));
         var2.scheduleAnimation(Math.max(0.0F, var21));
         var2.scanRenderer(var23);
         var2.buildSource(var22);
         var2.collapseOutput(Math.max(0.0F, var13));
         var2.invokeProfile(Math.max(0.0F, var21));
         var2.scheduleSource(var4);
         var2.renderTimer(var24);
         var2.saveScale(Math.max(0.0F, var6));
         var2.computeColor(Math.max(0.0F, var15));
         var2.adaptScale(var4);
         var2.runTexture(var25);
         var2.bindIndex(Math.max(0.0F, var6));
         var2.readAction(Math.max(0.0F, var16));
         var2.collapseConfig(var4);
         var2.validateData(var26);
         var2.renderScale(Math.max(0.0F, var6));
         var2.refreshClient(Math.max(0.0F, var17));
         var2.filterKey(var4);
         var2.adaptRequest(var27);
         var2.measureTimer(Math.max(0.0F, var6));
         var2.encodeVector(Math.max(0.0F, var18));
      }

      if (!(var21 <= 1.0F) && !(var20 <= 1.0F)) {
         var1.update(var7);

         try {
            float var30 = var2.handle(UiAnimationKeys.select(var3), var3.resolve(), SpringAnimationSpec.onTick());
            float var31 = var2.handle(UiAnimationKeys.refresh(var3), var3.animationDraw, SpringAnimationSpec.onTick());
            int var32 = ModuleStateHelper.handle(var30, 1.0F, 1.0F);
            int var33 = ThemeColors.handle(255, 255, 255, 255);
            int var34 = ThemeColors.handle(0, 0, 0, 255);
            int var35 = ThemeColors.handle(0, 0, 0, 0);
            var1.handle(
               var4 - var9.handle(3.0F),
               var5 + var9.handle(1.0F),
               var6 + var9.handle(6.0F),
               var11 - var9.handle(2.0F),
               var9.handle(7.0F),
               ThemeColors.handle(var10.prepare(), var10.check(), var7)
            );
            var1.handle(
               var4 - var9.handle(3.0F), var5 + var9.handle(1.0F), var6 + var9.handle(6.0F), var11 - var9.handle(2.0F), var9.handle(7.0F), var10.select(), 0.5F
            );
            var1.compute();
            var1.handle(var4, var22, var20, var21, var29, var29, var29, var29);
            boolean var58 = false /* VF: Semaphore variable */;

            try {
               var58 = true;
               var1.handle(var4, var22, var20, var21, var33, var32, var32, var33);
               var1.handle(var4, var22, var20, var21, var35, var35, var34, var34);
               var58 = false;
            } finally {
               if (var58) {
                  var1.compute();
                  var1.apply();
               }
            }

            var1.compute();
            var1.apply();
            var1.handle(var4, var22, var20, var21, var29, var10.refresh(), 0.5F);
            ModuleStateHelper.handle(var1, var23, var22, var13, var21, var29);
            var1.handle(var23, var22, var13, var21, var29, var10.refresh(), 0.5F);
            float var36 = var2.handle(UiAnimationKeys.check(var3), var3.renderer, SpringAnimationSpec.onTick());
            float var37 = var2.handle(UiAnimationKeys.onTick(var3), 1.0F - var3.handler, SpringAnimationSpec.onTick());
            float var38 = var4 + var36 * var20;
            float var39 = var22 + var37 * var21;
            float var40 = var9.handle(5.0F);
            int var41 = ModuleStateHelper.handle(var30, var36, 1.0F - var37);
            var1.handle(
               var38 - var40,
               var39 - var40,
               var40 * 2.0F,
               var40 * 2.0F,
               var40,
               var9.handle(4.0F),
               var9.handle(1.0F),
               var10.unload() ? ThemeColors.handle(0, 0, 0, 34) : ThemeColors.handle(var41, 40)
            );
            var1.handle(var38 - var40, var39 - var40, var40 * 2.0F, var40 * 2.0F, var40, var10.load(), 1.5F);
            var1.handle(
               var38 - var40 + 1.0F,
               var39 - var40 + 1.0F,
               var40 * 2.0F - 2.0F,
               var40 * 2.0F - 2.0F,
               Math.max(0.0F, var40 - 1.0F),
               ThemeColors.handle(0, 0, 0, 80),
               0.5F
            );
            float var42 = var22 + var30 * var21;
            float var43 = var9.handle(4.0F);
            float var44 = var13 + var9.handle(2.0F);
            var1.handle(var23 - var9.handle(1.0F), var42 - var43 * 0.5F, var44, var43, var9.handle(2.0F), var10.load());
            var1.handle(var23 - var9.handle(1.0F), var42 - var43 * 0.5F, var44, var43, var9.handle(2.0F), ThemeColors.handle(0, 0, 0, 60), 0.5F);
            var1.handle(var4, var24, var6, var15, var9.handle(3.0F), var9.handle(3.0F), var9.handle(3.0F), var9.handle(3.0F));

            try {
               this.handle(var1, var4, var24, var6, var15, this.handle(var9, 1.0F), 1.0F);
               int var45 = ModuleStateHelper.compute(var30, var3.renderer, var3.handler, 0.0F);
               int var46 = ModuleStateHelper.compute(var30, var3.renderer, var3.handler, 1.0F);
               var1.handle(var4, var24, var6, var15, var9.handle(3.0F), var45, var46);
            } finally {
               var1.apply();
            }

            var1.handle(var4, var24, var6, var15, var9.handle(3.0F), var10.refresh(), 0.5F);
            float var62 = var4 + var31 * var6;
            var1.handle(
               var62 - var9.handle(2.0F),
               var24 - var9.handle(2.0F),
               var9.handle(4.0F),
               var15 + var9.handle(4.0F),
               var9.handle(2.0F),
               var9.handle(4.0F),
               var9.handle(1.0F),
               ThemeColors.handle(0, 0, 0, 70)
            );
            var1.handle(var62 - var9.handle(1.5F), var24 - var9.handle(1.0F), var9.handle(3.0F), var15 + var9.handle(2.0F), var9.handle(1.5F), var10.load());
            var1.handle(
               var62 - var9.handle(1.5F),
               var24 - var9.handle(1.0F),
               var9.handle(3.0F),
               var15 + var9.handle(2.0F),
               var9.handle(1.5F),
               ThemeColors.handle(0, 0, 0, 80),
               0.5F
            );
            this.handle(var1, var9, var10, var4, var25, var6, var16, var30, var3.renderer, var3.handler, var31);
            this.handle(var1, var9, var10, var3, var4, var26, var6, var17, var31);
            int var63 = ModuleStateHelper.compute(var30, var3.renderer, var3.handler, var31);
            int var47 = var2.process(var3);
            this.handle(var1, var2, var9, var10, var3, var4, var27, var6, var18, var63, var47);
            this.handle(var1, var2, var9, var10, var3, var4, var28, var6, var19, var63, var31);
         } finally {
            var1.onTick();
         }
      }
   }
   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      ColorSetting var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11
   ) {
      float var12 = var3.handle(4.0F);
      float var13 = (var8 - var12) * 0.5F;
      float var14 = var6;
      float var15 = var6 + var13 + var12;
      float var16 = Math.min(var9, var13);
      float var17 = var16 * 0.5F;
      float var18 = var14 + (var13 - var16) * 0.5F;
      float var19 = var15 + (var13 - var16) * 0.5F;
      if (var2.evaluateWorld() == var5) {
         var2.receiveRequest(var15);
         var2.processWindow(var13);
      }

      float[] var20 = this.handle(var1, var18, var7, var16, var16);
      float[] var21 = this.handle(var1, var19, var7, var16, var16);
      float var22 = this.handle(var1, var17);
      float var23 = var1.render();
      boolean var24 = var2.evaluateWorld() == var5 && !var2.filterMatrix();
      var1.compute();
      if (!var24
         || !ColorPickerShader.handle(
            var20[0], var20[1], var20[2], var20[3], var10, var11, var4.save(), var4.submit(), var2.sampleLayer(), var2.sendWorld(), var22, var23, true
         )) {
         if ((var10 >>> 24 & 0xFF) < 250) {
            var1.handle(var18, var7, var16, var16, var17, var17, var17, var17);
            boolean var32 = false /* VF: Semaphore variable */;

            try {
               var32 = true;
               this.handle(var1, var18, var7, var16, var16, this.handle(var3, 1.0F), 1.0F);
               var32 = false;
            } finally {
               if (var32) {
                  var1.apply();
               }
            }

            var1.apply();
         }

         var1.process(var18 + var17, var7 + var17, var17, 0.0F, 1.0F, var10);
      }

      if (!var24
         || !ColorPickerShader.handle(
            var21[0], var21[1], var21[2], var21[3], var11, var10, var4.submit(), var4.save(), var2.sampleLayer(), var2.sendWorld(), var22, var23, false
         )) {
         if ((var11 >>> 24 & 0xFF) < 250) {
            var1.handle(var19, var7, var16, var16, var17, var17, var17, var17);
            boolean var29 = false /* VF: Semaphore variable */;

            try {
               var29 = true;
               this.handle(var1, var19, var7, var16, var16, this.handle(var3, 1.0F), 1.0F);
               var29 = false;
            } finally {
               if (var29) {
                  var1.apply();
               }
            }

            var1.apply();
         }

         var1.process(var19 + var17, var7 + var17, var17, 0.0F, 1.0F, var11);
      }

      var1.handle(var18, var7, var16, var16, var17, var4.refresh(), 0.5F);
      var1.handle(var19, var7, var16, var16, var17, var4.refresh(), 0.5F);
      float var25 = var15 - var12 * 0.5F;
      var1.process(
         var25 - var3.handle(0.5F),
         var7 + var3.handle(2.0F),
         var3.handle(1.0F),
         var9 - var3.handle(4.0F),
         var3.handle(0.5F),
         ThemeColors.handle(var4.save(), 120),
         ThemeColors.handle(var4.submit(), 90)
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      ColorSetting var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      float var11
   ) {
      float var12 = var8 * 0.62F;
      float var13 = var8 * 0.32F;
      float var14 = var6;
      float var15 = var6 + var8 - var13;
      float var16 = var9 + var3.handle(4.0F);
      float var17 = var7 - var3.handle(2.0F);
      float var18 = var3.handle(3.0F);
      if (var2.evaluateWorld() == var5) {
         var2.savePacket(var14);
         var2.animateEntry(var17);
         var2.collectPlayer(var12);
         var2.applyState(var16);
         var2.filterMatrix(var15);
         var2.sampleLayer(var17);
         var2.sendWorld(var13);
         var2.writeTarget(var16);
      }

      boolean var19 = var2.drawClient() == var5;
      boolean var20 = var2.writeProfile() == var5;
      boolean var21 = System.currentTimeMillis() / 500L % 2L == 0L;
      int var22 = ThemeColors.handle(var4.check(), ThemeColors.handle(var4.save(), 36), var19 ? 1.0F : 0.0F);
      var1.handle(var14, var17, var12, var16, var18, var22);
      if (var19) {
         var1.handle(var14, var17, var12, var16, var18, ThemeColors.handle(var4.save(), 220), 1.0F);
      } else {
         var1.handle(var14, var17, var12, var16, var18, var4.refresh(), 0.5F);
      }

      String var23;
      if (var19) {
         String var24 = var2.matchProvider();
         var23 = "#" + (var24 == null ? "" : var24) + (var21 ? "|" : " ");
      } else {
         var23 = String.format("#%02X%02X%02X", var10 >>> 16 & 0xFF, var10 >>> 8 & 0xFF, var10 & 0xFF);
      }

      int var30 = var19 ? var4.load() : var4.animate();
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.instance,
         var14 + var3.handle(6.0F),
         var17,
         var16,
         8.0F,
         ModuleStateHelper.handle(FontRegistry.instance, var23, 8.0F, var12 - var3.handle(12.0F)),
         var30
      );
      int var25 = ThemeColors.handle(var4.check(), ThemeColors.handle(var4.save(), 36), var20 ? 1.0F : 0.0F);
      var1.handle(var15, var17, var13, var16, var18, var25);
      if (var20) {
         var1.handle(var15, var17, var13, var16, var18, ThemeColors.handle(var4.save(), 220), 1.0F);
      } else {
         var1.handle(var15, var17, var13, var16, var18, var4.refresh(), 0.5F);
      }

      String var26;
      if (var20) {
         String var27 = var2.matchEffect();
         var26 = (var27 == null ? "" : var27) + (var21 ? "|" : " ") + "%";
      } else {
         var26 = Math.round(var11 * 100.0F) + "%";
      }

      int var31 = var20 ? var4.load() : var4.animate();
      float var28 = ModuleStateHelper.handle(FontRegistry.instance, var26, 8.0F);
      float var29 = var15 + (var13 - var28) * 0.5F;
      ModuleStateHelper.handle(var1, var3, FontRegistry.instance, var29, var17, var16, 8.0F, var26, var31);
   }

   private float[] handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      float[] var6 = var1.select().update();
      float var7 = this.handle(var6, var2, var3);
      float var8 = this.process(var6, var2, var3);
      float var9 = this.handle(var6, var2 + var4, var3);
      float var10 = this.process(var6, var2 + var4, var3);
      float var11 = this.handle(var6, var2 + var4, var3 + var5);
      float var12 = this.process(var6, var2 + var4, var3 + var5);
      float var13 = this.handle(var6, var2, var3 + var5);
      float var14 = this.process(var6, var2, var3 + var5);
      float var15 = Math.min(Math.min(var7, var9), Math.min(var11, var13));
      float var16 = Math.min(Math.min(var8, var10), Math.min(var12, var14));
      float var17 = Math.max(Math.max(var7, var9), Math.max(var11, var13));
      float var18 = Math.max(Math.max(var8, var10), Math.max(var12, var14));
      return new float[]{var15, var16, Math.max(0.0F, var17 - var15), Math.max(0.0F, var18 - var16)};
   }

   private float handle(RoundedRectRenderer var1, float var2) {
      float[] var3 = var1.select().update();
      float var4 = (float)Math.sqrt(var3[0] * var3[0] + var3[3] * var3[3]);
      float var5 = (float)Math.sqrt(var3[1] * var3[1] + var3[4] * var3[4]);
      return var2 * Math.max(0.001F, (var4 + var5) * 0.5F);
   }

   private float handle(float[] var1, float var2, float var3) {
      return var1[0] * var2 + var1[1] * var3 + var1[2];
   }

   private float process(float[] var1, float var2, float var3) {
      return var1[3] * var2 + var1[4] * var3 + var1[5];
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      float var12 = var2.handle(3.0F);
      byte var13 = 5;
      float var14 = (var6 - var12 * (var13 - 1)) / var13;
      float var15 = var2.handle(4.0F);
      float[] var16 = new float[]{0.0F, 0.5F, -0.083333336F, 0.083333336F, 0.33333334F};
      float var17 = Math.max(0.65F, var9);
      float var18 = Math.max(0.72F, var10);

      for (int var19 = 0; var19 < var13; var19++) {
         float var20 = var4 + var19 * (var14 + var12);
         float var21 = var8 + var16[var19];
         var1.handle(var20, var5, var14, var7, var15, var15, var15, var15);

         try {
            if (var11 < 0.995F) {
               this.handle(var1, var20, var5, var14, var7, this.handle(var2, 0.92F), 1.0F);
            }

            var1.handle(var20, var5, var14, var7, var15, ModuleStateHelper.compute(var21, var17, var18, var11));
         } finally {
            var1.apply();
         }

         var1.handle(var20, var5, var14, var7, var15, var19 == 0 ? ThemeColors.handle(var3.save(), 120) : var3.refresh(), 0.5F);
      }
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, ColorSetting var4, float var5, float var6, float var7, float var8, float var9
   ) {
      byte var10 = 9;
      float var11 = var2.handle(3.0F);
      float var12 = (var7 - var11 * (var10 - 1)) / var10;
      float var13 = var2.handle(4.0F);
      int var14 = var4.check();

      for (int var15 = 0; var15 < var10; var15++) {
         float var16 = var5 + var15 * (var12 + var11);
         boolean var17 = var15 == 8;
         boolean var18 = !var17 && var15 < var4.pointEncode.size();
         var1.handle(var16, var6, var12, var8, var13, var13, var13, var13);

         try {
            this.handle(var1, var16, var6, var12, var8, this.handle(var2, 0.92F), var18 ? 0.8F : 0.35F);
            if (var18) {
               var1.handle(var16, var6, var12, var8, var13, var4.pointEncode.get(var15));
            } else {
               var1.handle(var16, var6, var12, var8, var13, var17 ? ThemeColors.handle(var3.save(), 18) : var3.check());
            }
         } finally {
            var1.apply();
         }

         if (var17) {
            float var19 = ModuleStateHelper.handle(FontRegistry.current, "O", 8.0F);
            ModuleStateHelper.handle(
               var1,
               var2,
               FontRegistry.current,
               var16 + (var12 - var19) * 0.5F,
               var6,
               var8,
               8.0F,
               "O",
               ThemeColors.handle(var3.save(), Math.round(160.0F + 70.0F * var9))
            );
         }

         boolean var23 = var18 && var4.pointEncode.get(var15) == var14;
         if (var23) {
            float var20 = ModuleStateHelper.handle(FontRegistry.current, "j", 7.0F);
            ModuleStateHelper.handle(
               var1, var2, FontRegistry.current, var16 + (var12 - var20) * 0.5F, var6, var8, 7.0F, "j", ThemeColors.handle(var3.load(), 220)
            );
         }

         int var24 = var23 ? ThemeColors.handle(var3.save(), 160) : (var17 ? ThemeColors.handle(var3.save(), 95) : var3.refresh());
         var1.handle(var16, var6, var12, var8, var13, var24, var23 ? 0.8F : 0.5F);
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (!(var4 <= 0.0F) && !(var5 <= 0.0F) && !(var6 <= 0.0F)) {
         boolean var8 = false;

         for (float var9 = var3; var9 < var3 + var5; var9 += var6) {
            boolean var10 = var8;
            float var11 = Math.min(var6, var3 + var5 - var9);

            for (float var12 = var2; var12 < var2 + var4; var12 += var6) {
               float var13 = Math.min(var6, var2 + var4 - var12);
               var1.handle(var12, var9, var13, var11, ThemeColors.handle(var10 ? -1577754 : -3945532, Math.round(255.0F * var7)));
               var10 = !var10;
            }

            var8 = !var8;
         }
      }
   }

   private float handle(GuiMetrics var1, float var2) {
      return Math.max(4.5F, var1.handle(6.0F * var2));
   }
   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ModeSetting var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      float var10 = var2.handle(UiAnimationKeys.update(var3));
      float var11 = handle(var3, var6, var8);
      float var12 = handle(var3, var4, var6, var8);
      float var13 = handle(var8);
      float var14 = handle(var5, var8);
      float var15 = var8.handle(5.0F);
      this.handle(var1, var8, var3.instance, var4, var5, var8.handle(14.0F), 12.0F, var12 - var4 - var8.handle(8.0F), ModuleStateHelper.handle(var9));
      String var16 = UiAnimationKeys.compute(var3);
      float var17 = var2.handle(var16, ModuleStateHelper.handle(var2, var12, var14, var11, var13) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      float var18 = ModuleStateHelper.handle(var17, var2.process(var16));
      var1.handle(var18, var12 + var11 * 0.5F, var14 + var13 * 0.5F);
      boolean var27 = false /* VF: Semaphore variable */;

      try {
         var27 = true;
         var1.handle(var12, var14, var11, var13, var15, ThemeColors.handle(var9.check(), var9.select(), Math.max(var10, var17 * 0.58F)));
         var1.handle(var12, var14, var11, var13, var15, ThemeColors.handle(var9.refresh(), ThemeColors.handle(var9.save(), 120), Math.max(var10, var17)), 0.5F);
         var1.process(
            var12 + var8.handle(1.5F),
            var14 + var8.handle(3.0F),
            var8.handle(1.5F),
            var13 - var8.handle(6.0F),
            var8.handle(1.0F),
            ThemeColors.handle(var9.save(), 200),
            ThemeColors.handle(var9.submit(), 180)
         );
         ModuleStateHelper.handle(
            var1,
            var8,
            FontRegistry.instance,
            var12 + var8.handle(7.0F),
            var14,
            var13,
            10.0F,
            ModuleStateHelper.handle(FontRegistry.instance, var3.state, 10.0F, var11 - var8.handle(22.0F)),
            ModuleStateHelper.handle(var9)
         );
         float var19 = var12 + var11 - var8.handle(12.0F);
         float var20 = var14 + var13 * 0.5F;
         int var21 = ThemeColors.handle(ThemeColors.handle(var9.save(), 160), var9.save(), Math.max(var10, var17 * 0.5F));
         float var22 = 1.0F - 2.0F * var10;
         if (Math.abs(var22) > 0.01F) {
            var1.handle(var22, var19, var20);

            try {
               ModuleStateHelper.handle(var1, var8, FontRegistry.current, var19, var14, var13, 7.0F, "k", var21);
            } finally {
               var1.check();
            }

            var27 = false;
         } else {
            var27 = false;
         }
      } finally {
         if (var27) {
            var1.check();
         }
      }

      var1.check();
      if (var10 > 0.01F) {
         this.handle(var1, var2, var3, var4, var5, var6, var10, var7);
      }
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, ModeSetting var3, float var4, float var5, float var6, float var7, ThemeRenderContext var8
   ) {
      GuiMetrics var9 = var8.update();
      ThemeColors var10 = var8.apply();
      float var11 = handle(var6);
      float var12 = handle(var4, var6);
      float var13 = var5 + var9.handle(14.0F) + var9.handle(4.0F);
      float var14 = var9.handle(18.0F);
      float var15 = var9.handle(3.0F);
      float var16 = var15 * 2.0F + var3.config.size() * var14;
      float var17 = var9.handle(6.0F);
      var1.update(var7);

      try {
         var1.handle(var12, var13, var11, var16 * var7, var17, var10.check());
         var1.handle(var12, var13, var11, var16 * var7, var17, var10.refresh(), 0.5F);
         if (var7 > 0.5F) {
            for (int var18 = 0; var18 < var3.config.size(); var18++) {
               String var19 = var3.config.get(var18);
               boolean var20 = var18 == var3.current;
               float var21 = var13 + var15 + var18 * var14;
               if (var20) {
                  var1.handle(
                     var12 + var9.handle(2.0F),
                     var21,
                     var11 - var9.handle(4.0F),
                     var14,
                     var9.handle(4.0F),
                     ThemeColors.handle(var10.submit(), 35),
                     ThemeColors.handle(var10.save(), 20)
                  );
               }

               String var22 = UiAnimationKeys.compute(var3, var18);
               boolean var23 = ModuleStateHelper.handle(var2, var12, var21, var11, var14);
               float var24 = var2.handle(var22, var23 ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
               if (var24 > 0.01F && !var20) {
                  var1.handle(
                     var12 + var9.handle(2.0F),
                     var21,
                     var11 - var9.handle(4.0F),
                     var14,
                     var9.handle(4.0F),
                     ThemeColors.handle(var10.prepare(), var10.onTick(), var24)
                  );
               }

               int var25 = var20 ? var10.save() : (var24 > 0.2F ? ModuleStateHelper.handle(var10) : ModuleStateHelper.process(var10));
               if (var20) {
                  var1.process(
                     var12 + var9.handle(4.0F),
                     var21 + var9.handle(3.0F),
                     var9.handle(1.5F),
                     var14 - var9.handle(6.0F),
                     var9.handle(1.0F),
                     ThemeColors.handle(var10.save(), 200),
                     ThemeColors.handle(var10.submit(), 180)
                  );
               }

               var1.handle(ModuleStateHelper.handle(var24, var2.process(var22), 0.012F, 0.004F), var12 + var11 * 0.5F, var21 + var14 * 0.5F);

               try {
                  ModuleStateHelper.handle(
                     var1,
                     var9,
                     FontRegistry.instance,
                     var12 + var9.handle(10.0F),
                     var21,
                     var14,
                     10.0F,
                     ModuleStateHelper.handle(FontRegistry.instance, var19, 10.0F, var11 - var9.handle(18.0F)),
                     var25
                  );
               } finally {
                  var1.check();
               }
            }
         }
      } finally {
         var1.onTick();
      }
   }
   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, ShaderPresetSetting var3, float var4, float var5, float var6, ThemeRenderContext var7
   ) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      var3.compute();
      float var10 = var8.handle(18.0F);
      float var11 = var2.handle(UiAnimationKeys.update(var3));
      float var12 = handle(var3, var6, var8);
      float var13 = handle(var3, var4, var6, var8);
      float var14 = process(var5, var8);
      float var15 = process(var8);
      float var16 = var8.handle(6.0F);
      this.handle(var1, var8, var3.instance, var4, var5, var10, 12.0F, var13 - var4 - var8.handle(8.0F), ModuleStateHelper.handle(var9));
      String var17 = UiAnimationKeys.compute(var3);
      float var18 = var2.handle(var17, ModuleStateHelper.handle(var2, var13, var14, var12, var15) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      float var19 = Math.max(var11, var18);
      var1.handle(ModuleStateHelper.handle(var18, var2.process(var17), 0.014F, 0.004F), var13 + var12 * 0.5F, var14 + var15 * 0.5F);
      boolean var31 = false /* VF: Semaphore variable */;

      try {
         var31 = true;
         var1.handle(var13, var14, var12, var15, var16, ThemeColors.handle(var9.check(), var9.select(), var19 * 0.7F));
         var1.handle(var13, var14, var12, var15, var16, ThemeColors.handle(var9.refresh(), ThemeColors.handle(var9.save(), 120), var19), 0.5F);
         float var20 = var8.handle(12.0F);
         float var21 = var13 + var8.handle(4.0F);
         float var22 = var14 + (var15 - var20) * 0.5F;
         var1.process(var21, var22, var20, var20, var8.handle(4.0F), ThemeColors.handle(var9.save(), 190), ThemeColors.handle(var9.submit(), 150));
         float var23 = ModuleStateHelper.handle(FontRegistry.current, "w", 7.0F);
         ModuleStateHelper.handle(var1, var8, FontRegistry.current, var21 + (var20 - var23) * 0.5F, var22, var20, 7.0F, "w", var9.load());
         float var24 = var13 + var12 - var8.handle(11.0F);
         float var25 = 1.0F - 2.0F * var11;
         if (Math.abs(var25) > 0.01F) {
            var1.handle(var25, var24, var14 + var15 * 0.5F);
            boolean var34 = false /* VF: Semaphore variable */;

            try {
               var34 = true;
               ModuleStateHelper.handle(
                  var1, var8, FontRegistry.current, var24, var14, var15, 7.0F, "k", ThemeColors.handle(var9.animate(), var9.save(), var19)
               );
               var34 = false;
            } finally {
               if (var34) {
                  var1.check();
               }
            }

            var1.check();
         }

         String var26 = var3.prepare();
         int var27 = var3.onTick()
            ? ThemeColors.handle(ModuleStateHelper.compute(var9), var9.submit(), 0.45F)
            : ThemeColors.handle(ModuleStateHelper.process(var9), ModuleStateHelper.handle(var9), var18 * 0.48F + var11 * 0.22F);
         ModuleStateHelper.handle(
            var1,
            var8,
            FontRegistry.instance,
            var13 + var8.handle(20.0F),
            var14,
            var15,
            10.0F,
            ModuleStateHelper.handle(FontRegistry.instance, var26, 10.0F, var12 - var8.handle(36.0F)),
            var27
         );
         var31 = false;
      } finally {
         if (var31) {
            var1.check();
         }
      }

      var1.check();
      if (var11 > 0.01F) {
         this.handle(var1, var2, var3, var4, var5, var6, var11, var7);
      }
   }
   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, ShaderPresetSetting var3, float var4, float var5, float var6, float var7, ThemeRenderContext var8
   ) {
      GuiMetrics var9 = var8.update();
      ThemeColors var10 = var8.apply();
      var3.compute();
      float var11 = process(var6);
      float var12 = process(var4, var6);
      float var13 = var5 + var9.handle(18.0F) + var9.handle(5.0F);
      float var14 = compute(var9);
      float var15 = var9.handle(4.0F);
      float var16 = var15 * 2.0F + var3.config.size() * var14;
      float var17 = var9.handle(8.0F);
      var1.update(var7);

      try {
         var1.handle(
            var12, var13, var11, var16 * var7, var17, var9.handle(14.0F), var9.handle(1.0F), ThemeColors.handle(var10.save(), Math.round(34.0F * var7))
         );
         var1.handle(var12, var13, var11, var16 * var7, var17, ThemeColors.handle(var10.check(), var10.select(), 0.28F));
         var1.handle(var12, var13, var11, var16 * var7, var17, ThemeColors.handle(var10.refresh(), ThemeColors.handle(var10.save(), 112), var7), 0.55F);
         if (var7 > 0.45F) {
            for (int var18 = 0; var18 < var3.config.size(); var18++) {
               String var19 = var3.config.get(var18);
               boolean var20 = var3.process(var19);
               float var21 = var13 + var15 + var18 * var14;
               String var22 = UiAnimationKeys.compute(var3, var18);
               float var23 = var2.handle(var22, ModuleStateHelper.handle(var2, var12, var21, var11, var14) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
               float var24 = var12 + var9.handle(7.0F);
               float var25 = var21 + var9.handle(6.0F);
               float var26 = var9.handle(76.0F);
               float var27 = var14 - var9.handle(12.0F);
               if (var20) {
                  var1.handle(
                     var12 + var9.handle(3.0F),
                     var21 + var9.handle(1.0F),
                     var11 - var9.handle(6.0F),
                     var14 - var9.handle(2.0F),
                     var9.handle(6.0F),
                     ThemeColors.handle(var10.submit(), 42),
                     ThemeColors.handle(var10.save(), 24)
                  );
                  var1.handle(
                     var12 + var9.handle(5.0F),
                     var21 + var9.handle(4.0F),
                     var11 - var9.handle(10.0F),
                     var14 - var9.handle(8.0F),
                     var9.handle(7.0F),
                     var9.handle(10.0F),
                     var9.handle(1.0F),
                     ThemeColors.handle(var10.save(), Math.round(24.0F * var7))
                  );
               } else if (var23 > 0.01F) {
                  var1.handle(
                     var12 + var9.handle(3.0F),
                     var21 + var9.handle(1.0F),
                     var11 - var9.handle(6.0F),
                     var14 - var9.handle(2.0F),
                     var9.handle(6.0F),
                     ThemeColors.handle(var10.prepare(), var10.select(), var23)
                  );
               }

               this.handle(var1, var2, var8, var3, var19, var24, var25, var26, var27, var7);
               float var28 = var24 + var26 + var9.handle(10.0F);
               int var29 = var20 ? var10.save() : ThemeColors.handle(ModuleStateHelper.process(var10), ModuleStateHelper.handle(var10), var23 * 0.55F);
               String var30 = this.handle(var3, var19);
               var1.handle(ModuleStateHelper.handle(var23, var2.process(var22), 0.01F, 0.003F), var12 + var11 * 0.5F, var21 + var14 * 0.5F);
               boolean var37 = false /* VF: Semaphore variable */;

               try {
                  var37 = true;
                  ModuleStateHelper.handle(
                     var1,
                     var9,
                     FontRegistry.instance,
                     var28,
                     var21 + var9.handle(8.0F),
                     var9.handle(16.0F),
                     10.0F,
                     ModuleStateHelper.handle(FontRegistry.instance, var19, 10.0F, var12 + var11 - var9.handle(12.0F) - var28),
                     var29
                  );
                  ModuleStateHelper.handle(
                     var1,
                     var9,
                     FontRegistry.instance,
                     var28,
                     var21 + var9.handle(29.0F),
                     var9.handle(14.0F),
                     8.0F,
                     ModuleStateHelper.handle(FontRegistry.instance, var30, 8.0F, var12 + var11 - var9.handle(12.0F) - var28),
                     ThemeColors.handle(var10.encodePoint(), var10.submit(), var20 ? 0.55F : var23 * 0.38F)
                  );
                  var37 = false;
               } finally {
                  if (var37) {
                     var1.check();
                  }
               }

               var1.check();
            }
         }
      } finally {
         var1.onTick();
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ThemeRenderContext var3,
      ShaderPresetSetting var4,
      String var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      GuiMetrics var11 = var3.update();
      ThemeColors var12 = var3.apply();
      float var13 = var11.handle(5.0F);
      var1.handle(var6, var7, var8, var9, var13, ThemeColors.handle(var12.prepare(), var12.check(), 0.5F));
      boolean var14 = false;
      if (!"None".equalsIgnoreCase(var5) && PresetManager.handle().update(var5)) {
         MinecraftClient var15 = MinecraftClient.getInstance();
         int var16 = var15 == null ? Math.max(1, Math.round(var6 + var8)) : Math.max(1, var15.getWindow().getFramebufferWidth());
         int var17 = var15 == null ? Math.max(1, Math.round(var7 + var9)) : Math.max(1, var15.getWindow().getFramebufferHeight());
         ShaderGraph var18 = PresetManager.handle().compute(var5);
         LivePreviewRenderer var19 = LivePreviewRenderer.handle(var18 == null ? null : var18.process());
         if (var19 == LivePreviewRenderer.PREVIEW_ONLY) {
            var19 = var4.mode;
         }

         if (var18 != null) {
            FoundryEntityPreviewRenderer.handle(
               var1, var3, var5, var19, var18, var6, var7, var8, var9, var16, var17, var2.sampleLayer(), var2.sendWorld(), var10
            );
            var14 = true;
         }
      }

      if (!var14) {
         if ("None".equalsIgnoreCase(var5)) {
            var1.handle(
               var6 + var11.handle(5.0F),
               var7 + var11.handle(5.0F),
               var8 - var11.handle(10.0F),
               var9 - var11.handle(10.0F),
               var11.handle(4.0F),
               var12.render(),
               0.6F
            );
         } else {
            var1.process(var6, var7, var8, var9, var13, ThemeColors.handle(var12.save(), 72), ThemeColors.handle(var12.submit(), 48));
         }
      }

      var1.handle(var6, var7, var8, var9, var13, ThemeColors.handle(var12.save(), Math.round(70.0F * var10)), 0.55F);
   }

   private String handle(ShaderPresetSetting var1, String var2) {
      if ("None".equalsIgnoreCase(var2)) {
         return var1.mode.process() + " slot hidden";
      }

      ShaderGraph var3 = PresetManager.handle().compute(var2);
      LivePreviewRenderer var4 = LivePreviewRenderer.handle(var3 == null ? null : var3.process());
      if (var4 == LivePreviewRenderer.PREVIEW_ONLY) {
         var4 = var1.mode;
      }

      int var5 = PresetManager.handle().prepare(var2).size();
      PresetManager.PrimaryMode var6 = PresetManager.handle().apply(var2);
      PresetManager.Mode var7 = PresetManager.handle().execute(var2);
      return var6.name().toLowerCase(Locale.ROOT) + " / " + var4.process() + " / " + var5 + " uniforms / " + var7.name().toLowerCase(Locale.ROOT);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ChoiceSetting var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      float var10 = this.handle((Setting)var3, var8);
      float var11 = var8.handle(14.0F);
      float var12 = var8.handle(3.0F);
      float var13 = var8.handle(3.0F);
      float var14 = var6 * 0.7F;
      float var15 = var4 + var6 - var14;
      this.handle(var1, var8, var3.instance, var4, var5, var10, 12.0F, var15 - var4 - var8.handle(8.0F), ModuleStateHelper.handle(var9));
      float var16 = 0.0F;
      int var17 = 0;
      float var18 = var8.handle(3.0F);

      for (int var19 = 0; var19 < var3.config.size(); var19++) {
         BooleanSetting var20 = var3.config.get(var19);
         float var21 = var2.handle(UiAnimationKeys.handle(var3, var19), var20.compute() ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
         float var22 = Math.max(0.0F, Math.min(1.0F, var21));
         boolean var23 = var2.projectLayer() == var20;
         String var24 = ModuleStateHelper.handle(var20);
         float var25 = ModuleStateHelper.handle(FontRegistry.instance, var24, 8.0F);
         float var26 = Math.max(var8.handle(18.0F), var25 + var8.handle(8.0F));
         if (var16 > 0.0F && var16 + var26 > var14) {
            var17++;
            var16 = 0.0F;
         }

         float var27 = var15 + var16;
         float var28 = var5 + var8.handle(1.0F) + var17 * (var11 + var18);
         boolean var29 = ModuleStateHelper.handle(var2, var27, var28 - var8.handle(1.0F), var26, var11 + var8.handle(2.0F));
         String var30 = UiAnimationKeys.process(var3, var19);
         float var31 = var2.handle(var30, var29 ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
         var1.handle(ModuleStateHelper.handle(var31, var2.process(var30), 0.026F, 0.008F), var27 + var26 * 0.5F, var28 + var11 * 0.5F);

         try {
            float var32 = Math.max(0.5F, var8.handle(0.75F));
            float var33 = Math.max(0.0F, var26 - var32 * 2.0F);
            float var34 = Math.max(0.0F, var11 - var32 * 2.0F);
            int var35 = ThemeColors.handle(var9.check(), var9.select(), Math.min(1.0F, 0.2F + var31 * 0.38F + var22 * 0.14F));
            int var36 = ThemeColors.handle(ThemeColors.handle(var9.execute(), var9.apply(), var9.unload() ? 0.46F : 0.82F), var9.unload() ? 164 : 208);
            int var37 = ThemeColors.handle(ThemeColors.handle(var9.submit(), var9.save(), 0.5F), var9.unload() ? 58 : 72);
            int var38 = ThemeColors.handle(ThemeColors.handle(var9.submit(), var9.save(), 0.5F), var9.unload() ? 96 : 112);
            var1.handle(var27, var28, var26, var11, var13, var35);
            var1.handle(var27 + var32, var28 + var32, var33, var34, Math.max(0.0F, var13 - var32), ThemeColors.handle(var36, var37, var22 * 0.48F));
            var1.handle(var27, var28, var26, var11, var13, ThemeColors.handle(var9.select(), var38, Math.max(var22 * 0.58F, var31 * 0.72F)), 0.5F);
            int var39 = ThemeColors.handle(
               ThemeColors.handle(ModuleStateHelper.compute(var9), ModuleStateHelper.process(var9), ModuleStateHelper.process(var31)),
               ModuleStateHelper.handle(var9),
               var22
            );
            String var40 = var23 ? "..." : ModuleStateHelper.handle(FontRegistry.instance, var24, 8.0F, var26 - var8.handle(6.0F));
            float var41 = ModuleStateHelper.handle(FontRegistry.instance, var40, 8.0F);
            ModuleStateHelper.handle(var1, var8, FontRegistry.instance, var27 + (var26 - var41) * 0.5F, var28, var11, 8.0F, var40, var39);
         } finally {
            var1.check();
         }

         var16 += var26 + var12;
      }
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, Setting var3, String var4, String var5, float var6, float var7, float var8, ThemeRenderContext var9
   ) {
      GuiMetrics var10 = var9.update();
      ThemeColors var11 = var9.apply();
      float var12 = var10.handle(14.0F);
      String var13 = var5 == null ? "" : var5;
      float var14 = ModuleStateHelper.handle(FontRegistry.instance, var13, 10.0F);
      float var15 = Math.min(var8 * 0.5F, var14);
      float var16 = Math.max(var10.handle(36.0F), var15 + var10.handle(14.0F));
      float var17 = var10.handle(16.0F);
      float var18 = var6 + var8 - var16;
      float var19 = var7 + (var12 - var17) * 0.5F;
      float var20 = var10.handle(5.0F);
      this.handle(var1, var10, var4, var6, var7, var12, 12.0F, var18 - var6 - var10.handle(8.0F), ModuleStateHelper.handle(var11));
      String var21 = UiAnimationKeys.compute(var3);
      float var22 = var2.handle(var21, ModuleStateHelper.handle(var2, var18, var19, var16, var17) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.handle(ModuleStateHelper.handle(var22, var2.process(var21)), var18 + var16 * 0.5F, var19 + var17 * 0.5F);

      try {
         var1.handle(var18, var19, var16, var17, var20, ThemeColors.handle(var11.check(), var11.select(), var22 * 0.72F));
         var1.handle(var18, var19, var16, var17, var20, ThemeColors.handle(var11.select(), ThemeColors.handle(var11.save(), 95), var22), 0.5F);
         String var23 = ModuleStateHelper.handle(FontRegistry.instance, var13, 10.0F, var16 - var10.handle(8.0F));
         float var24 = ModuleStateHelper.handle(FontRegistry.instance, var23, 10.0F);
         float var25 = var18 + (var16 - var24) * 0.5F;
         ModuleStateHelper.handle(
            var1,
            var10,
            FontRegistry.instance,
            var25,
            var19,
            var17,
            10.0F,
            var23,
            ThemeColors.handle(ModuleStateHelper.process(var11), ModuleStateHelper.handle(var11), var22 * 0.46F)
         );
      } finally {
         var1.check();
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, String var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      if (var3 != null && !var3.isEmpty() && !(var8 <= 1.0F) && !(var6 <= 1.0F)) {
         float var10 = ModuleStateHelper.handle(var2, FontRegistry.config, var3, var7);
         if (var10 <= var8) {
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var4, var5, var6, var7, var3, var9);
         } else {
            float var11 = var10 - var8;
            float var12 = var11 * this.handle();
            var1.handle(var4, var5, Math.max(1.0F, var8), var6, 0.0F, 0.0F, 0.0F, 0.0F);
            ModuleStateHelper.handle(var1, var2, FontRegistry.config, var4 - var12, var5, var6, var7, var3, var9);
            var1.apply();
         }
      }
   }

   private float handle() {
      float var1 = (float)(System.currentTimeMillis() % 5200L) / 5200.0F;
      if (var1 < 0.22F) {
         return 0.0F;
      } else if (var1 < 0.46F) {
         return this.compute((var1 - 0.22F) / 0.24F);
      } else if (var1 < 0.62F) {
         return 1.0F;
      } else {
         return var1 < 0.86F ? 1.0F - this.compute((var1 - 0.62F) / 0.24F) : 0.0F;
      }
   }

   private float compute(float var1) {
      float var2 = Math.max(0.0F, Math.min(1.0F, var1));
      return var2 * var2 * var2 * (var2 * (var2 * 6.0F - 15.0F) + 10.0F);
   }
}
