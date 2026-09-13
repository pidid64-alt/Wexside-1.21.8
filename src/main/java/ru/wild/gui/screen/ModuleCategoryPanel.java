package ru.wild.gui.screen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ColorSetting;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.widget.ColorControlRenderer;
import ru.wild.gui.widget.ModuleCategoryColumn;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.TransitionShader;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.LegacyGuiProjection;
import ru.wild.util.render.RoundedRectRenderer;

public final class ModuleCategoryPanel {
   private static final float instance = 120.0F;
   private static final float data = 8.0F;
   private static final float context = 17.0F;
   private static final float config = 8.0F;
   private static final float state = 156.0F;
   private static final float cache = 22.0F;
   private static final EnumMap<ModuleCategory, ModuleCategoryColumn> output = new EnumMap<>(ModuleCategory.class);
   private static final Map<Module, EasedDoubleAnimator> current = new HashMap<>();
   private static final Set<Module> active = new HashSet<>();
   private static boolean mode = false;
   private static boolean selection = false;
   private static final EasedDoubleAnimator enabled = new EasedDoubleAnimator();

   private ModuleCategoryPanel() {
   }

   public static EasedDoubleAnimator handle(Module var0) {
      return current.computeIfAbsent(var0, var0x -> new EasedDoubleAnimator());
   }

   public static void process(Module var0) {
      EasedDoubleAnimator var1 = handle(var0);
      if (active.contains(var0)) {
         active.remove(var0);
         var1.handle(0.0, 0.18F, EasingFunctions.handler);
      } else {
         active.add(var0);
         var1.handle(1.0, 0.18F, EasingFunctions.handler);
      }
   }

   public static boolean handle(RoundedRectRenderer var0, double var1, double var3, int var5) {
      float[] var6 = LegacyGuiProjection.handle((float)var1, (float)var3);
      int var7 = (int)var6[0];
      int var8 = (int)var6[1];
      ModuleCategoryPanel.LayoutMetrics var9 = ModuleCategoryPanel.LayoutMetrics.handle();
      if (!var9.instance) {
         return false;
      }

      handle(var9.mode);
      if (BlurStateManager.animationDraw != null) {
         float var10 = ColorControlRenderer.handle(BlurStateManager.pointEncode);
         if (NumericTransform.handle(var7, var8, var10, BlurStateManager.animator, 160.0F, 119.0F)) {
            ColorSettingInput.handle(var7, var8, var5);
            return true;
         }
      }

      if (handle(var7, var8, var5, var9)) {
         return true;
      }

      if (process(var7, var8, var5, var9)) {
         return true;
      }

      if (var5 == 0 && handle(var9, var7, var8)) {
         return false;
      }

      for (ModuleCategoryColumn var11 : output.values()) {
         if (var11.handle(var0, var7, var8, var5)) {
            return true;
         }
      }

      if (BlurStateManager.previous != null && var5 >= 0 && var5 <= 8) {
         int var14 = -100 - var5;
         BlurStateManager.previous.config = var14;
         BlurStateManager.previous.output = false;
         BlurStateManager.previous = null;
         return true;
      }

      if (BlurStateManager.matrixBlend != null && var5 >= 0 && var5 <= 8) {
         int var13 = -100 - var5;
         BlurStateManager.matrixBlend.keyCode = var13;
         BlurStateManager.matrixBlend.bindingActive = false;
         BlurStateManager.matrixBlend = null;
         return true;
      }

      if (BlurStateManager.latest != null && var5 == 0) {
         BlurStateManager.latest.current = false;
         BlurStateManager.latest = null;
      }

      if (BlurStateManager.animationDraw != null && var5 == 0) {
         BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
         BlurStateManager.animationDraw = null;
         BlurStateManager.pointEncode = 0.0F;
         BlurStateManager.animator = 0.0F;
      }

      return false;
   }

   public static boolean handle(double var0, double var2, double var4) {
      float[] var6 = LegacyGuiProjection.handle((float)var0, (float)var2);
      float var7 = var6[0];
      float var8 = var6[1];
      handle(ModuleCategory.values());

      for (ModuleCategoryColumn var10 : output.values()) {
         if (var10.handle(var7, var8, var4)) {
            return true;
         }
      }

      return false;
   }

   public static void handle(RoundedRectRenderer var0, int var1, int var2, float var3) {
      if (WildClient.instance != null && WildClient.instance.selection != null && WildClient.instance.selection.data != null) {
         int var4 = WildClient.instance.selection.data.prepare();
         Color var5 = new Color(var4);
         float[] var6 = Color.RGBtoHSB(var5.getRed(), var5.getGreen(), var5.getBlue(), null);
         Color var7 = Color.getHSBColor(var6[0], var6[1] * 0.15F, 0.3F);
         Color var8 = Color.getHSBColor(var6[0], var6[1] * 0.3F, 0.17F);
         Color var9 = Color.getHSBColor(var6[0], var6[1] * 0.3F, 1.0F);
         Color var10 = Color.getHSBColor(var6[0], var6[1] * 0.2F, 1.0F);
         ThemePalette.CUSTOM.handle(var5, var7, var8, var9, Color.WHITE, var10);
         if (WildClient.instance.selection.process() == ThemePalette.CUSTOM) {
            BlurStateManager.outputCollapse = ThemePalette.CUSTOM;
            BlurStateManager.sourceBuild = ThemePalette.CUSTOM;
         }
      }

      ModuleCategoryPanel.LayoutMetrics var11 = ModuleCategoryPanel.LayoutMetrics.handle();
      if (var11.instance) {
         boolean var12 = BlurStateManager.profileDraw || BlurStateManager.providerFetch != null && !BlurStateManager.providerFetch.isEmpty();
         BlurStateManager.handler.handle();
         if (var12 != mode) {
            BlurStateManager.handler.handle(var12 ? 1.0 : 0.0, var12 ? 0.32F : 0.16F, var12 ? EasingFunctions.serverRead : EasingFunctions.renderer, false);
            mode = var12;
         }

         for (int var13 = 0; var13 < var11.mode.length; var13++) {
            ModuleCategory var15 = var11.mode[var13];
            ModuleCategoryColumn var17 = output.computeIfAbsent(var15, ModuleCategoryColumn::new);
            var17.handle(var11.current + var13 * (var11.config + var11.cache), var11.active, var11.config, var11.state);
            var17.handle(var0, var1, var2, var3);
         }

         if (BlurStateManager.animationDraw instanceof ColorSetting) {
            int var14 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.update(1, 1), (int)(100.0F * var3));
            int var16 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.compute(1, 1), (int)(180.0F * var3));
            int var18 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), (int)(200.0F * var3));
            ColorControlRenderer.handle(var0, BlurStateManager.animationDraw, var1, var2, var14, var16, var18, var3 * BlurStateManager.cache.check());
         }

         handle(var0, var11, var3, var1, var2);
         handle(var0, var1, var2, var3, var11);
      }
   }

   private static void handle(RoundedRectRenderer var0, int var1, int var2, float var3, ModuleCategoryPanel.LayoutMetrics var4) {
      enabled.handle();
      enabled.handle(selection ? 1.0 : 0.0, 0.2F, EasingFunctions.handler, false);
      float var5 = enabled.update();
      ThemePalette var6 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
      float var7 = 110.0F;
      float var8 = 22.0F;
      float var9 = var4.data - var7 - 10.0F;
      float var10 = var4.context - var8 - 10.0F;
      int var11 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.update(1, 1), (int)(30.0F * var3));
      int var12 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.compute(1, 1), (int)(200.0F * var3));
      int var13 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), (int)(220.0F * var3));
      var0.handle(var9, var10, var7, var8, 6.0F, var11, 1.0F);
      var0.handle(var9, var10, var7, var8, 6.0F, var12);
      var0.handle(var9 + 6.0F, var10 + 7.0F, 8.0F, 8.0F, 4.0F, RoundedRectRenderer.ColorState.select(var6.handle().getRGB(), (int)(255.0F * var3)));
      var0.handle(FontRegistry.instance, var9 + 20.0F, var10 + 13.0F, 12.0F, var6.instance, var13);
      if (var5 > 0.01F) {
         ThemePalette[] var14 = ThemePalette.values();
         float var15 = 18.0F;
         float var16 = var14.length * var15 + 8.0F;
         float var17 = var9;
         float var18 = var10 - 6.0F - var16 * var5;
         var0.handle(var17, var10 - 6.0F - var16, var7, var16, 0.0F, 0.0F, 6.0F, 6.0F);
         var0.handle(var17, var18, var7, var16, 6.0F, var11, 1.0F);
         var0.handle(var17, var18, var7, var16, 6.0F, var12);
         float var19 = var18 + 4.0F;

         for (ThemePalette var23 : var14) {
            boolean var24 = NumericTransform.handle(var1, var2, var17, var19, var7, var15);
            int var25 = var24 ? RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(60.0F * var3 * var5)) : 0;
            if (var24 || var23 == var6) {
               var0.handle(
                  var17 + 4.0F,
                  var19,
                  var7 - 8.0F,
                  var15,
                  4.0F,
                  var25 != 0 ? var25 : RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(30.0F * var3 * var5))
               );
            }

            var0.handle(
               var17 + 8.0F, var19 + 5.0F, 8.0F, 8.0F, 4.0F, RoundedRectRenderer.ColorState.select(var23.handle().getRGB(), (int)(255.0F * var3 * var5))
            );
            var0.handle(
               FontRegistry.instance,
               var17 + 22.0F,
               var19 + 10.0F,
               11.0F,
               var23.instance,
               RoundedRectRenderer.ColorState.select(var13, (int)(255.0F * var3 * var5))
            );
            if (var23 == ThemePalette.CUSTOM) {
               var0.handle(
                  FontRegistry.instance,
                  var17 + var7 - 30.0F,
                  var19 + 10.0F,
                  9.0F,
                  "[ПКМ]",
                  RoundedRectRenderer.ColorState.select(var13, (int)(120.0F * var3 * var5))
               );
            }

            var19 += var15;
         }

         var0.apply();
      }
   }

   private static boolean handle(int var0, int var1, int var2, ModuleCategoryPanel.LayoutMetrics var3) {
      float var4 = 110.0F;
      float var5 = 22.0F;
      float var6 = var3.data - var4 - 10.0F;
      float var7 = var3.context - var5 - 10.0F;
      if (var2 == 0 && NumericTransform.handle(var0, var1, var6, var7, var4, var5)) {
         selection = !selection;
         return true;
      }

      if (handle(var0, var1, var3)) {
         return false;
      }

      if (selection) {
         ThemePalette[] var8 = ThemePalette.values();
         float var9 = var8.length * 18.0F + 8.0F;
         float var10 = var7 - 6.0F - var9;
         if (NumericTransform.handle(var0, var1, var6, var10, var4, var9)) {
            float var11 = var1 - (var10 + 4.0F);
            int var12 = (int)(var11 / 18.0F);
            if (var12 >= 0 && var12 < var8.length) {
               ThemePalette var13 = var8[var12];
               ThemePalette var14 = WildClient.instance.selection.process();
               if (var2 == 0) {
                  if (var14 != var13) {
                     BlurStateManager.state.compute();
                     TransitionShader.handle().handle(var0, var1, var13.handle().getRGB(), var13.resolve().getRGB());
                     BlurStateManager.sourceBuild = var13;
                     BlurStateManager.outputCollapse = var13;
                     WildClient.instance.selection.handle(var13);
                  }
               } else if (var2 == 1 && var13 == ThemePalette.CUSTOM) {
                  if (var14 != ThemePalette.CUSTOM) {
                     BlurStateManager.state.compute();
                     TransitionShader.handle().handle(var0, var1, ThemePalette.CUSTOM.handle().getRGB(), ThemePalette.CUSTOM.resolve().getRGB());
                     BlurStateManager.sourceBuild = ThemePalette.CUSTOM;
                     BlurStateManager.outputCollapse = ThemePalette.CUSTOM;
                     WildClient.instance.selection.handle(ThemePalette.CUSTOM);
                  }

                  ColorSetting var15 = WildClient.instance.selection.data;
                  if (BlurStateManager.animationDraw == var15) {
                     BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
                     BlurStateManager.animationDraw = null;
                  } else {
                     BlurStateManager.animationDraw = var15;
                     BlurStateManager.cache.process(AnimationDirection.FORWARDS);
                     BlurStateManager.pointEncode = var6 - 160.0F - 6.0F;
                     BlurStateManager.animator = var10;
                  }
               }
            }

            return true;
         }

         if (var2 == 0 || var2 == 1) {
            selection = false;
            if (BlurStateManager.animationDraw == WildClient.instance.selection.data) {
               BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
               BlurStateManager.animationDraw = null;
            }
         }
      }

      return false;
   }

   private static boolean handle(float var0, float var1, ModuleCategoryPanel.LayoutMetrics var2) {
      for (int var3 = 0; var3 < var2.mode.length; var3++) {
         float var4 = var2.current + var3 * (var2.config + var2.cache);
         if (NumericTransform.handle(var0, var1, var4, var2.active, var2.config, var2.state)) {
            return true;
         }
      }

      return false;
   }

   private static boolean handle(ModuleCategoryPanel.LayoutMetrics var0, int var1, int var2) {
      return false;
   }

   private static void handle(RoundedRectRenderer var0, ModuleCategoryPanel.LayoutMetrics var1, float var2, int var3, int var4) {
      float var5 = BlurStateManager.handler.update();
      if (!(var5 <= 0.01F)) {
         float var6 = handle(var1);
         float var7 = var1.current + (var1.output - var6) * 0.5F;
         float var8 = handle(var1, var5);
         float var9 = 17.0F;
         int var10 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.update(1, 1), (int)(30.0F * var2 * var5));
         int var11 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.resolve(1, 1), (int)(205.0F * var2 * var5));
         int var12 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), (int)(220.0F * var2 * var5));
         int var13 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(120.0F * var2 * var5));
         var0.handle(var7, var8, var6, var9, 6.0F, var10, 0.6F);
         var0.handle(var7, var8, var6, var9, 6.0F, var11);
         String var14 = BlurStateManager.providerFetch == null ? "" : BlurStateManager.providerFetch;
         String var15 = var14.isEmpty() ? "" : handle(var14);
         String var16 = handle(var14, var15);
         float var17 = var7 + 6.0F;
         float var18 = var8 + 5.5F + 6.2F;
         if (!var14.isEmpty()) {
            var0.handle(FontRegistry.instance, var17, var18, 11.0F, var14, var12);
            if (!var16.isEmpty()) {
               float var19 = RoundedRectRenderer.handle(FontRegistry.instance, var14, 11.0F).instance;
               float var20 = var17 + Math.min(var19 + 1.0F, var6 - 14.0F);
               var0.handle(FontRegistry.instance, var20, var18, 11.0F, var16, var13);
            }
         }

         if (BlurStateManager.profileDraw) {
            float var21 = RoundedRectRenderer.handle(FontRegistry.instance, var14, 11.0F).instance;
            float var23 = var17 + Math.min(var21 + 1.0F, var6 - 14.0F);
            var0.handle(var23, var8 + 4.0F, 1.0F, var9 - 8.0F, RoundedRectRenderer.ColorState.select(var12, (int)(200.0F * var5)));
         }

         boolean var22 = NumericTransform.handle(var3, var4, var7, var8, var6, var9);
         if (var22) {
            var0.handle(var7, var8, var6, var9, 6.0F, RoundedRectRenderer.ColorState.select(var13, (int)(80.0F * var5)), 0.5F);
         }
      }
   }

   private static boolean process(int var0, int var1, int var2, ModuleCategoryPanel.LayoutMetrics var3) {
      float var4 = handle(var3);
      float var5 = var3.current + (var3.output - var4) * 0.5F;
      float var6 = handle(var3, BlurStateManager.handler.update());
      boolean var7 = NumericTransform.handle(var0, var1, var5, var6, var4, 17.0F);
      if (var2 == 0 && var7) {
         BlurStateManager.profileDraw = true;
         return true;
      }

      if (var2 == 0 && BlurStateManager.profileDraw && !var7) {
         BlurStateManager.profileDraw = false;
      }

      return false;
   }

   private static float handle(ModuleCategoryPanel.LayoutMetrics var0) {
      return Math.min(156.0F, var0.output);
   }

   private static float handle(ModuleCategoryPanel.LayoutMetrics var0, float var1) {
      float var2 = var0.active + var0.state + 8.0F;
      return var2 + (1.0F - var1) * 22.0F;
   }

   private static String handle(String var0) {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         String var1 = var0.trim().toLowerCase();
         if (var1.isEmpty()) {
            return "";
         }

         ArrayList<Module> var2 = WildClient.instance.data.process();
         return var2.stream()
            .filter(var1x -> var1x != null && var1x.displayName != null && var1x.displayName.toLowerCase().contains(var1))
            .min(Comparator.<Module>comparingInt(var1x -> {
               String var2x = var1x.displayName.toLowerCase();
               int var3 = var2x.indexOf(var1);
               return var3 < 0 ? Integer.MAX_VALUE : var3;
            }).thenComparingInt(var0x -> var0x.displayName.length()))
            .map(var0x -> var0x.displayName)
            .orElse("");
      } else {
         return "";
      }
   }

   private static String handle(String var0, String var1) {
      if (var0 == null || var1 == null || var0.isEmpty() || var1.isEmpty()) {
         return "";
      } else {
         return var1.regionMatches(true, 0, var0, 0, var0.length()) ? var1.substring(Math.min(var0.length(), var1.length())) : var1;
      }
   }

   private static void handle(ModuleCategory[] var0) {
      for (ModuleCategory var4 : var0) {
         output.computeIfAbsent(var4, ModuleCategoryColumn::new);
      }
   }

   static final class LayoutMetrics {
      final boolean instance;
      final float data;
      final float context;
      final float config;
      final float state;
      final float cache;
      final float output;
      final float current;
      final float active;
      final ModuleCategory[] mode;

      private LayoutMetrics(
         boolean var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, ModuleCategory[] var10
      ) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
         this.mode = var10;
      }

      static ModuleCategoryPanel.LayoutMetrics handle() {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null && var0.getWindow() != null) {
            float var1 = var0.getWindow().getScaledWidth();
            float var2 = var0.getWindow().getScaledHeight();
            ModuleCategory[] var3 = new ModuleCategory[]{
               ModuleCategory.Combat, ModuleCategory.Movement, ModuleCategory.Visuals, ModuleCategory.Player, ModuleCategory.Misc
            };
            if (var3.length == 0) {
               return new ModuleCategoryPanel.LayoutMetrics(false, var1, var2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var3);
            }

            float var4 = 120.0F;
            float var5 = 8.0F;
            float var6 = var3.length * var4 + (var3.length - 1) * var5;
            float var7 = (var1 - var6) / 2.0F;
            float var8 = NumericTransform.onTick(var2 - 80.0F, 190.0F, 320.0F);
            float var9 = (var2 - var8) / 2.0F;
            return new ModuleCategoryPanel.LayoutMetrics(true, var1, var2, var4, var8, var5, var6, var7, var9, var3);
         } else {
            return new ModuleCategoryPanel.LayoutMetrics(false, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, ModuleCategory.values());
         }
      }
   }
}
