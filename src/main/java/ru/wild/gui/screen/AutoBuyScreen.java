package ru.wild.gui.screen;

import ru.wild.api.module.ModuleCategory;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.hud.OverlayDebugRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.WildLogoRenderer;
import ru.wild.render.shader.WildLogoShader;
import ru.wild.util.render.RoundedRectRenderer;

public final class AutoBuyScreen {
   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      var1.handle(
         var3.compute(),
         var3.resolve(),
         var3.update(),
         var3.apply(),
         var5.handle(16.0F),
         var5.handle(4.0F),
         var5.handle(4.0F),
         var5.handle(16.0F),
         ModuleStateHelper.prepare(var6)
      );
      if (var6.unload()) {
         var1.handle(
            var3.compute() + 1.0F,
            var3.resolve() + 1.0F,
            Math.max(1.0F, var3.update() - 2.0F),
            Math.max(1.0F, var3.apply() - 2.0F),
            Math.max(0.0F, var5.handle(16.0F) - 1.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            Math.max(0.0F, var5.handle(16.0F) - 1.0F),
            ModuleStateHelper.process(var6, 0.82F),
            1.0F
         );
      }

      this.resolve(var1, var2, var3, var4);
      this.update(var1, var2, var3, var4);
      this.process(var1, var2, var3, var4);
      this.compute(var1, var2, var3, var4);
      this.apply(var1, var2, var3, var4);
   }

   public static float handle(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.compute() + var1.handle(16.0F));
   }

   public static float process(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.resolve() + var1.handle(16.0F));
   }

   public static float handle(GuiMetrics var0) {
      return var0.handle(40.0F);
   }

   public static float process(GuiMetrics var0) {
      return var0.handle(40.0F);
   }

   public static float compute(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.compute() + var1.handle(16.0F));
   }

   public static float resolve(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.resolve() + var0.apply() - var1.handle(56.0F));
   }

   public static float update(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.resolve() + var1.handle(85.0F));
   }

   public static float compute(GuiMetrics var0) {
      return var0.handle(51.0F);
   }

   public static float apply(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(resolve(var0, var1) - var1.handle(100.0F));
   }

   public static float execute(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(resolve(var0, var1) - var1.handle(50.0F));
   }

   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = compute(var3, var5);
      float var8 = apply(var3, var5);
      float var9 = var2.handle(UiAnimationKeys.update());
      float var10 = var2.handle(UiAnimationKeys.apply());
      float var11 = Math.max(var9, var10) * var5.handle(1.0F);
      float var12 = ModuleStateHelper.handle(var9, var2.process(UiAnimationKeys.update()));
      this.handle(var1, var2, var6, var7, var8 - var11, process(var5), 5, var9, var10, var12);
      if (!this.handle(var2) && ModuleStateHelper.handle(var2, var7, var8, var5.handle(40.0F), var5.handle(40.0F))) {
         var2.handle("tab:autobuy", "AutoBuy", var7 + var5.handle(40.0F), var8 + var5.handle(20.0F));
      }
   }

   private void compute(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = compute(var3, var5);
      float var8 = execute(var3, var5);
      float var9 = var2.handle(UiAnimationKeys.execute());
      float var10 = var2.handle(UiAnimationKeys.prepare());
      float var11 = Math.max(var9, var10) * var5.handle(1.0F);
      float var12 = ModuleStateHelper.handle(var9, var2.process(UiAnimationKeys.execute()));
      this.handle(var1, var2, var6, var7, var8 - var11, process(var5), 6, var9, var10, var12);
      if (!this.handle(var2) && ModuleStateHelper.handle(var2, var7, var8, var5.handle(40.0F), var5.handle(40.0F))) {
         var2.handle("tab:bots", "Bots", var7 + var5.handle(40.0F), var8 + var5.handle(20.0F));
      }
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, ThemeColors var3, float var4, float var5, float var6, int var7, float var8, float var9, float var10
   ) {
      int var11 = var3.unload() ? ThemeColors.handle(var3.save(), var3.load(), 0.45F) : var3.save();
      int var12 = var3.unload() ? ThemeColors.handle(var3.submit(), var3.load(), 0.45F) : var3.submit();
      int var13 = var3.unload() ? ModuleStateHelper.handle(var3, 0.0F) : var3.prepare();
      int var14 = var3.unload() ? ModuleStateHelper.process(var3, 0.9F) : var3.onTick();
      var1.compute();
      WildLogoRenderer.handle(
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var3.encodePoint(),
         var13,
         var14,
         var2.resolve(),
         var3.unload()
      );
   }

   private void resolve(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = handle(var3, var5);
      float var8 = process(var3, var5);
      float var9 = handle(var5);
      var1.compute();
      WildLogoRenderer.handle(var7, var8, var9, var6.save(), var6.submit(), var2.resolve(), var6.unload());
      float var10 = 0.5F + 0.5F * (float)Math.sin((float)System.currentTimeMillis() * 0.00108F);
      float var11 = 19.5F;
      float var12 = var11 * (1.08F + var10 * 0.035F);
      float var13 = ModuleStateHelper.handle(FontRegistry.current, "w", var11);
      float var14 = ModuleStateHelper.handle(FontRegistry.current, "w", var12);
      float var15 = var7 + var9 * 0.5F;
      float var16 = var8 + var9 * 0.5F;
      float var17 = ModuleStateHelper.handle(var5, FontRegistry.current, var11);
      float var18 = ModuleStateHelper.handle(var5, FontRegistry.current, var12);
      float var19 = var15 - var13 * 0.5F;
      float var20 = var15 - var14 * 0.5F;
      float var21 = var16 - var17 * 0.5F - var5.handle(1.0F);
      float var22 = var16 - var18 * 0.5F - var5.handle(1.0F);
      if (!var6.unload()) {
         var1.resolve();

         try {
            ModuleStateHelper.handle(
               var1,
               var5,
               FontRegistry.current,
               var20,
               var22,
               var12,
               "w",
               ThemeColors.handle(ThemeColors.handle(var6.submit(), 120), ThemeColors.handle(var6.save(), 135), var10)
            );
         } finally {
            var1.update();
         }
      }

      ModuleStateHelper.handle(var1, var5, FontRegistry.current, var19, var21, var11, "w", ThemeColors.handle(ModuleStateHelper.resolve(var6), 246));
      var1.handle(var7 + var5.handle(4.0F), var8 + var5.handle(56.0F), var5.handle(32.0F), var5.handle(1.0F), var5.handle(1.0F), var6.render());
      if (!this.handle(var2) && ModuleStateHelper.handle(var2, var7, var8, var9, var9)) {
         var2.handle("logo:themes", "Themes", var7 + var9 + var5.handle(6.0F), var8 + var9 * 0.5F);
      }
   }

   private void update(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = compute(var3, var5);
      float var8 = update(var3, var5);
      ModuleCategory[] var9 = ModuleCategory.values();

      for (int var10 = 0; var10 < var9.length; var10++) {
         ModuleCategory var11 = var9[var10];
         float var12 = var8 + var10 * compute(var5);
         float var13 = var2.handle(UiAnimationKeys.handle(var11));
         float var14 = var2.handle(UiAnimationKeys.process(var11));
         float var15 = Math.max(var13, var14) * var5.handle(1.0F);
         float var16 = ModuleStateHelper.handle(var13, var2.process(UiAnimationKeys.handle(var11)));
         this.handle(var1, var2, var6, var7, var12 - var15, process(var5), var10, var13, var14, var16);
         if (!this.handle(var2) && ModuleStateHelper.handle(var2, var7, var12, var5.handle(40.0F), var5.handle(40.0F))) {
            var2.handle("cat:" + var11.name(), var11.process(), var7 + var5.handle(40.0F), var12 + var5.handle(20.0F));
         }
      }
   }

   private void apply(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = compute(var3, var5);
      float var8 = resolve(var3, var5);
      float var9 = var2.handle(UiAnimationKeys.process());
      float var10 = var2.executeCache() ? 1.0F : 0.0F;
      float var11 = Math.max(var9, var10);
      float var12 = process(var5);
      float var13 = var7 + var12 * 0.5F;
      float var14 = var8 + var12 * 0.5F;
      float var15 = ModuleStateHelper.handle(var9, var2.process(UiAnimationKeys.process()));
      var1.handle(var15, var13, var14);

      try {
         if (var11 > 0.01F) {
            int var16 = var6.unload()
               ? ThemeColors.handle(46, 59, 70, Math.round(18.0F * var11))
               : ThemeColors.handle(var6.submit(), Math.round(38.0F * var11));
            var1.handle(
               var7 - var5.handle(1.5F),
               var8 - var5.handle(1.5F),
               var12 + var5.handle(3.0F),
               var12 + var5.handle(3.0F),
               var5.handle(21.5F),
               var5.handle(14.0F) * var11,
               var5.handle(var6.unload() ? 2.6F : 2.0F),
               var16
            );
         }

         var1.process(
            var13,
            var14,
            var5.handle(20.0F),
            0.0F,
            1.0F,
            ThemeColors.handle(ThemeColors.handle(var6.encodePoint(), 118), ThemeColors.handle(var6.save(), 196), var11)
         );
         var1.process(
            var13, var14, var5.handle(18.25F), 0.0F, 1.0F, var6.unload() ? ThemeColors.handle(255, 255, 255, 218) : ThemeColors.handle(20, 15, 24, 238)
         );
         var1.process(
            var13,
            var14,
            var5.handle(15.8F),
            0.0F,
            1.0F,
            var6.unload() ? ModuleStateHelper.handle(var6, var11) : ThemeColors.handle(var6.prepare(), ThemeColors.handle(var6.submit(), 28), var11)
         );
         OverlayDebugRenderer.handle(
            var1,
            var5,
            var13,
            var14 + var5.handle(0.4F),
            var5.handle(0.88F),
            ThemeColors.handle(ModuleStateHelper.process(var6), ModuleStateHelper.handle(var6), var11 * 0.72F),
            ThemeColors.handle(var6.save(), Math.round(18.0F + 46.0F * var11))
         );
      } finally {
         var1.check();
      }

      if (!this.handle(var2) && ModuleStateHelper.handle(var2, var7, var8, var12, var12)) {
         var2.handle("avatar", "Profile", var7 + var12 + var5.handle(6.0F), var8 + var12 * 0.5F);
      }
   }

   private boolean handle(ModernClickGuiState var1) {
      return var1.executeCache() || var1.validateData();
   }
}
