package ru.wild.gui.screen;

import ru.wild.config.StudioProfileGate;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.GlyphTrailAnimation;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

public final class StudioScreen {
   private static final String instance = "Foundry";
   private static final String data = "Studio";
   private static final String context = "a";
   private static final float config = 92.0F;
   private static final float state = 86.0F;
   private final GlyphTrailAnimation cache = new GlyphTrailAnimation();
   private static final float output = 88.0F;

   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = var2.handle(UiAnimationKeys.handle());
      float var8 = var3.select();
      float var9 = var3.tick();
      var1.handle(var3.check(), var3.onTick(), var3.refresh(), var8, var5.handle(4.0F), ModuleStateHelper.prepare(var6));
      var1.handle(
         var3.render(), var3.onTick(), var9, var8, var5.handle(4.0F), var5.handle(16.0F), var5.handle(4.0F), var5.handle(4.0F), ModuleStateHelper.prepare(var6)
      );
      if (var6.unload()) {
         var1.handle(
            var3.check() + 1.0F,
            var3.onTick() + 1.0F,
            Math.max(1.0F, var3.refresh() - 2.0F),
            Math.max(1.0F, var8 - 2.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            ModuleStateHelper.process(var6, 0.78F),
            1.0F
         );
         var1.handle(
            var3.render() + 1.0F,
            var3.onTick() + 1.0F,
            Math.max(1.0F, var9 - 2.0F),
            Math.max(1.0F, var8 - 2.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            Math.max(0.0F, var5.handle(16.0F) - 1.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            ModuleStateHelper.process(var6, 0.78F),
            1.0F
         );
      }

      var1.handle(
         var3.render(),
         var3.onTick(),
         var9,
         var8,
         var5.handle(4.0F),
         var5.handle(16.0F),
         var5.handle(4.0F),
         var5.handle(4.0F),
         ThemeColors.handle(var6.check(), var6.select(), var7)
      );
      if (var7 > 0.01F) {
         var1.handle(
            var3.render() + 1.0F,
            var3.onTick() + 1.0F,
            Math.max(1.0F, var9 - 2.0F),
            Math.max(1.0F, var8 - 2.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            Math.max(0.0F, var5.handle(16.0F) - 1.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            Math.max(0.0F, var5.handle(4.0F) - 1.0F),
            ThemeColors.handle(var6.save(), Math.round(50.0F * var7)),
            1.0F
         );
      }

      this.process(var1, var2, var3, var4);
      this.update(var1, var2, var3, var4);
      this.compute(var1, var2, var3, var4);
      this.handle(var1, var3, var4);
      this.handle(var1, var2, var3, var4, var7);
   }

   public static float handle(ViewportLayoutState var0, GuiMetrics var1) {
      return process(var0, var1);
   }

   public static float process(ViewportLayoutState var0, GuiMetrics var1) {
      return update(var0, var1);
   }

   public static float compute(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = apply(var0, var1) - var1.handle(8.0F) - handle(var0, var1);
      return Math.round(Math.max(var0.check() + var0.refresh() * 0.3F, var2));
   }

   public static float resolve(ViewportLayoutState var0, GuiMetrics var1) {
      return execute(var0, var1);
   }

   public static float handle(GuiMetrics var0) {
      return Math.round(var0.handle(92.0F));
   }

   public static float update(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(Math.max(var1.handle(24.0F), var0.select() - var1.handle(12.0F)));
   }

   public static float apply(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = var0.check() + var0.refresh() - var1.handle(16.0F) - compute(var1) - var1.handle(10.0F) - handle(var1);
      return Math.round(Math.max(var0.check() + var0.refresh() * 0.48F, var2));
   }

   private static float compute(GuiMetrics var0) {
      float var1 = ModuleStateHelper.handle(FontRegistry.config, handle(), 12.0F);
      float var2 = ModuleStateHelper.handle(FontRegistry.state, "g", 12.0F);
      return Math.max(var0.handle(86.0F), var1 + var0.handle(8.0F) + var2);
   }

   public static float execute(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.onTick() + (var0.select() - update(var0, var1)) * 0.5F);
   }

   public static float process(GuiMetrics var0) {
      return Math.round(var0.handle(88.0F));
   }

   public static float prepare(ViewportLayoutState var0, GuiMetrics var1) {
      return update(var0, var1);
   }

   public static float check(ViewportLayoutState var0, GuiMetrics var1) {
      return execute(var0, var1);
   }

   private static float resolve(GuiMetrics var0) {
      float var1 = var0.handle(6.0F);
      float var2 = var0.handle(10.0F);
      float var3 = ModuleStateHelper.handle(FontRegistry.instance, "Src by SoftArax", 12.0F);
      float var4 = ModuleStateHelper.handle(FontRegistry.current, "k", 8.0F);
      float var5 = var0.handle(12.0F);
      float var6 = ModuleStateHelper.handle(FontRegistry.config, "Диагностика", 12.0F);
      return var0.handle(16.0F) + var2 + var1 + var3 + var1 + var4 + var1 + var5 + var1 + var6;
   }

   public static float onTick(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = var0.check() + resolve(var1) + var1.handle(14.0F);
      float var3 = apply(var0, var1) - var1.handle(8.0F) - process(var1);
      return Math.round(Math.max(var2, var3));
   }

   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = var3.onTick();
      float var8 = var5.handle(6.0F);
      float var9 = 12.0F;
      float var10 = var5.handle(10.0F);
      float var11 = ModuleStateHelper.handle(FontRegistry.instance, "Src by SoftArax", var9);
      float var12 = ModuleStateHelper.handle(FontRegistry.current, "k", 8.0F);
      float var13 = var5.handle(12.0F);
      String var14 = var2.executeCache() ? "P" : var2.bindIndex().handle();
      String var15 = var2.executeCache()
         ? "Профиль"
         : (
            var2.validateData()
               ? "Диагностика"
               : (var2.collapseConfig() ? "AutoBuy" : (var2.renderScale() ? "Studio" : (var2.refreshClient() ? "Bots" : var2.bindIndex().process())))
         );
      float var16 = var3.select();
      float var17 = var3.check() + var5.handle(16.0F);
      this.handle(var1, var5, var17, var7, var16, var10, var6);
      var17 += var10 + var8;
      ModuleStateHelper.handle(var1, var5, FontRegistry.instance, var17, var7, var16, var9, "Src by SoftArax", var6.animate());
      var17 += var11 + var8;
      ModuleStateHelper.handle(var1, var5, FontRegistry.current, var17 + var5.handle(1.0F), var7, var16, 8.0F, "k", var6.encodePoint());
      var17 += var12 + var8;
      if (var2.executeCache()) {
         this.handle(var1, var5, FontRegistry.context, var14, var17, var7, var16, var9, var13, var6.resolve(), 0.0F);
      } else if (var2.validateData()) {
         this.process(var1, var5, var17 + var13 * 0.5F, var7 + var16 * 0.5F, var6.resolve());
      } else if (var2.collapseConfig()) {
         this.handle(var1, var5, var17 + var13 * 0.5F, var7 + var16 * 0.5F, var5.handle(0.8F), var6.resolve());
      } else if (var2.renderScale()) {
         this.handle(var1, var5, FontRegistry.current, "a", var17 + var5.handle(1.5F), var7, var16, var9, var13, var6.resolve(), 0.0F);
      } else if (var2.refreshClient()) {
         this.compute(var1, var5, var17 + var13 * 0.5F, var7 + var16 * 0.5F, var6.resolve());
      } else {
         this.handle(var1, var5, FontRegistry.current, var14, var17, var7, var16, var9, var13, var6.resolve(), 0.0F);
      }

      var17 += var13 + var8;
      ModuleStateHelper.handle(var1, var5, FontRegistry.config, var17, var7, var16, var9, var15, var6.load());
   }

   private void handle(RoundedRectRenderer var1, ViewportLayoutState var2, ThemeRenderContext var3) {
      GuiMetrics var4 = var3.update();
      ThemeColors var5 = var3.apply();
      String var6 = handle();
      float var7 = ModuleStateHelper.handle(FontRegistry.config, var6, 12.0F);
      String var8 = "g";
      float var9 = ModuleStateHelper.handle(FontRegistry.state, var8, 12.0F);
      float var10 = var2.check() + var2.refresh() - var4.handle(16.0F) - var9;
      float var11 = var10 - var4.handle(8.0F) - var7;
      float var12 = var2.onTick();
      ModuleStateHelper.handle(var1, var4, FontRegistry.config, var11, var12, var2.select(), 12.0F, var6, var5.load());
      ModuleStateHelper.handle(var1, var4, FontRegistry.state, var10, var12, var2.select(), 12.0F, var8, var5.resolve());
   }

   private void compute(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = apply(var3, var5);
      float var8 = execute(var3, var5);
      float var9 = handle(var5);
      float var10 = update(var3, var5);
      float var11 = var2.handle(UiAnimationKeys.matchVector());
      float var12 = var2.cancelIndex() ? 1.0F : 0.0F;
      float var13 = Math.max(var11, var12);
      int var14 = var6.unload()
         ? ThemeColors.handle(ModuleStateHelper.handle(var6, 0.18F), ThemeColors.handle(var6.save(), 42), var13 * 0.36F)
         : ThemeColors.handle(var6.check(), ThemeColors.handle(var6.submit(), 62), 0.16F + var13 * 0.18F);
      var1.handle(var7, var8, var9, var10, var5.handle(6.0F), var14);
      var1.handle(
         var7,
         var8,
         var9,
         var10,
         var5.handle(6.0F),
         ThemeColors.handle(var6.unload() ? ModuleStateHelper.process(var6, 0.76F) : var6.select(), ThemeColors.handle(var6.save(), 120), var13),
         Math.max(0.55F, var5.handle(0.6F))
      );
      float var15 = 9.5F;
      float var16 = var5.handle(15.0F);
      float var17 = ModuleStateHelper.handle(FontRegistry.config, "Foundry", var15);
      float var18 = var5.handle(7.0F);
      float var19 = var16 + var18 + var17;
      float var20 = Math.round(var7 + (var9 - var19) * 0.5F);
      float var21 = var20 + var16 * 0.5F;
      float var22 = var8 + var10 * 0.5F;
      float var23 = Math.round(var20 + var16 + var18);
      this.handle(var1, var5, var21, var22, ThemeColors.handle(ModuleStateHelper.process(var6), var6.save(), 0.45F + var13 * 0.35F));
      ModuleStateHelper.handle(
         var1,
         var5,
         FontRegistry.config,
         var23,
         var8,
         var10,
         var15,
         "Foundry",
         ThemeColors.handle(ModuleStateHelper.process(var6), ModuleStateHelper.handle(var6), 0.72F + var13 * 0.28F)
      );
      if (ModuleStateHelper.handle(var2, var7, var8, var9, var10)) {
         var2.handle("header:foundry", "Foundry", var7 + var9 * 0.5F, var8 + var10 + var5.handle(8.0F));
      }
   }

   private void resolve(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      float var7 = onTick(var3, var5);
      float var8 = check(var3, var5);
      float var9 = process(var5);
      float var10 = prepare(var3, var5);
      float var11 = var2.handle(UiAnimationKeys.fetchProvider());
      float var12 = var2.execute() != null ? 1.0F : 0.0F;
      float var13 = Math.max(var11, var12);
      int var14 = var6.unload()
         ? ThemeColors.handle(ModuleStateHelper.handle(var6, 0.18F), ThemeColors.handle(var6.save(), 42), var13 * 0.36F)
         : ThemeColors.handle(var6.check(), ThemeColors.handle(var6.submit(), 62), 0.16F + var13 * 0.18F);
      var1.handle(var7, var8, var9, var10, var5.handle(6.0F), var14);
      var1.handle(
         var7,
         var8,
         var9,
         var10,
         var5.handle(6.0F),
         ThemeColors.handle(var6.unload() ? ModuleStateHelper.process(var6, 0.76F) : var6.select(), ThemeColors.handle(var6.save(), 120), var13),
         Math.max(0.55F, var5.handle(0.6F))
      );
      String var15 = var2.prepare();
      float var16 = 9.5F;
      float var17 = ModuleStateHelper.handle(FontRegistry.config, var15, var16);
      float var18 = Math.round(var7 + (var9 - var17) * 0.5F);
      ModuleStateHelper.handle(
         var1,
         var5,
         FontRegistry.config,
         var18,
         var8,
         var10,
         var16,
         var15,
         ThemeColors.handle(ModuleStateHelper.process(var6), var6.save(), 0.55F + var13 * 0.35F)
      );
      if (ModuleStateHelper.handle(var2, var7, var8, var9, var10)) {
         var2.handle("header:account", "ЛКМ: следующий · ПКМ: предыдущий", var7 + var9 * 0.5F, var8 + var10 + var5.handle(8.0F));
      }
   }

   private void update(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      if (StudioProfileGate.handle()) {
         GuiMetrics var5 = var4.update();
         ThemeColors var6 = var4.apply();
         float var7 = compute(var3, var5);
         float var8 = resolve(var3, var5);
         float var9 = handle(var3, var5);
         float var10 = process(var3, var5);
         float var11 = var2.handle(UiAnimationKeys.computeResponse());
         float var12 = var2.renderScale() ? 1.0F : 0.0F;
         float var13 = Math.max(var11, var12);
         int var14 = var6.unload()
            ? ThemeColors.handle(ModuleStateHelper.handle(var6, 0.18F), ThemeColors.handle(var6.save(), 42), var13 * 0.36F)
            : ThemeColors.handle(var6.check(), ThemeColors.handle(var6.submit(), 62), 0.16F + var13 * 0.18F);
         var1.handle(var7, var8, var9, var10, var5.handle(6.0F), var14);
         var1.handle(
            var7,
            var8,
            var9,
            var10,
            var5.handle(6.0F),
            ThemeColors.handle(var6.unload() ? ModuleStateHelper.process(var6, 0.76F) : var6.select(), ThemeColors.handle(var6.save(), 120), var13),
            Math.max(0.55F, var5.handle(0.6F))
         );
         this.handle(
            var1,
            var5,
            FontRegistry.current,
            "a",
            var7 + var5.handle(2.0F),
            var8,
            var10,
            13.0F,
            var9,
            ThemeColors.handle(ModuleStateHelper.process(var6), var6.save(), 0.5F + var13 * 0.35F),
            0.0F
         );
         if (ModuleStateHelper.handle(var2, var7, var8, var9, var10)) {
            var2.handle("header:studio", "Studio", var7 + var9 * 0.5F, var8 + var10 + var5.handle(8.0F));
         }
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4, float var5) {
      GuiMetrics var6 = var4.update();
      ThemeColors var7 = var4.apply();
      float var8 = var3.onTick();
      String var9 = var2.encodeVector();
      float var10 = var2.handle(UiAnimationKeys.render());
      float var11 = (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1.0) * 0.5);
      boolean var12 = var2.receiveRequest();
      int var13 = ThemeColors.handle(var7.animate(), var7.load(), var5);
      float var14 = var6.handle(16.0F);
      float var15 = var6.handle(27.0F);
      float var16 = var3.tick();
      float var17 = var3.select();
      float var18 = var16 - var14 - var15;
      float var19 = 0.0F;
      boolean var20 = var9.isEmpty();
      if (!var20) {
         float var21 = ModuleStateHelper.handle(FontRegistry.instance, var9, 12.0F);
         if (var21 > var18) {
            var19 = var18 - var21;
         }
      }

      int var27 = (int)Math.floor(var3.render());
      int var22 = (int)Math.floor(var3.onTick());
      int var23 = (int)Math.ceil(var3.render() + var16 - var15);
      int var24 = (int)Math.ceil(var3.onTick() + var17);
      var1.handle(var27, var22, Math.max(0, var23 - var27), Math.max(0, var24 - var22));
      int var25 = ThemeColors.handle(var7.resolve(), Math.round(255.0F * var11));
      this.cache
         .handle(var1, var6, FontRegistry.instance, var9, var3.render() + var14 + var19, var8, var17, 12.0F, var13, var12, var25, System.currentTimeMillis());
      if (var20 && !var12 && !this.cache.handle()) {
         String var26 = var2.refreshClient() && !var2.executeCache() ? "Search bots..." : "Search...";
         ModuleStateHelper.handle(var1, var6, FontRegistry.instance, var3.render() + var14, var8, var17, 12.0F, var26, var13);
      }

      var1.apply();
      float var28 = Math.max(var5 * 0.3F, var10);
      ModuleStateHelper.handle(
         var1,
         var6,
         FontRegistry.state,
         var3.render() + var16 - var6.handle(27.0F),
         var8,
         var17,
         12.0F,
         "l",
         ThemeColors.handle(var7.resolve(), Math.round(255.0F * var28))
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5) {
      float var6 = var2.handle(0.72F);
      float var7 = 3.1F * var6;
      float var8 = Math.max(1.0F, var2.handle(0.85F));
      float var9 = var3 - 6.2F * var6;
      float var10 = var3 + 5.0F * var6;
      float var11 = var4 - 5.4F * var6;
      float var12 = var4 + 5.2F * var6;
      int var13 = ThemeColors.handle(var5, 138);
      var1.handle(var9 + var7 * 0.5F, var11 + var7 * 0.5F, var10 - var9, var8, var8, var13);
      var1.handle(var9 + var7 * 0.5F, var12 + var7 * 0.5F, var10 - var9, var8, var8, var13);
      var1.handle(var9 + var7 * 0.5F, var11 + var7 * 0.5F, var8, var12 - var11, var8, var13);
      var1.handle(var10 + var7 * 0.5F - var8, var11 + var7 * 0.5F, var8, var12 - var11, var8, var13);
      var1.process(var9 + var7 * 0.5F, var11 + var7 * 0.5F, var7, 0.0F, 1.0F, var5);
      var1.process(var10 + var7 * 0.5F, var11 + var7 * 0.5F, var7, 0.0F, 1.0F, var5);
      var1.process(var9 + var7 * 0.5F, var12 + var7 * 0.5F, var7, 0.0F, 1.0F, var5);
      var1.process(var10 + var7 * 0.5F, var12 + var7 * 0.5F, var7, 0.0F, 1.0F, var5);
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5) {
      float var6 = var2.handle(1.0F);
      float var7 = Math.round(var3 - 5.5F * var6);
      float var8 = Math.round(var4 - 5.5F * var6);
      var1.handle(var7, var8, 11.0F * var6, 11.0F * var6, 3.2F * var6, var5, Math.max(0.6F, 0.75F * var6));
      var1.handle(var7 + 2.6F * var6, var8 + 7.0F * var6, 1.3F * var6, 2.0F * var6, 0.65F * var6, ThemeColors.handle(var5, 180));
      var1.handle(var7 + 4.9F * var6, var8 + 5.2F * var6, 1.3F * var6, 3.8F * var6, 0.65F * var6, var5);
      var1.handle(var7 + 7.2F * var6, var8 + 3.0F * var6, 1.3F * var6, 6.0F * var6, 0.65F * var6, ThemeColors.handle(var5, 220));
   }

   private void compute(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5) {
      float var6 = var2.handle(0.72F);
      float var7 = 11.5F * var6;
      float var8 = 8.5F * var6;
      float var9 = var3 - var7 * 0.5F;
      float var10 = var4 - var8 * 0.38F;
      var1.handle(var9, var10, var7, var8, 2.5F * var6, var5, Math.max(0.65F, var2.handle(0.7F)));
      var1.process(var3 - 2.6F * var6, var10 + 3.4F * var6, 1.15F * var6, 0.0F, 1.0F, var5);
      var1.process(var3 + 2.6F * var6, var10 + 3.4F * var6, 1.15F * var6, 0.0F, 1.0F, var5);
      var1.handle(var3 - 2.5F * var6, var10 + 6.1F * var6, 5.0F * var6, Math.max(0.7F, var2.handle(0.65F)), 0.5F * var6, ThemeColors.handle(var5, 190));
      var1.handle(var3 - Math.max(0.4F, var2.handle(0.38F)), var10 - 2.5F * var6, Math.max(0.8F, var2.handle(0.76F)), 2.5F * var6, 0.4F * var6, var5);
      var1.process(var3, var10 - 3.0F * var6, 1.1F * var6, 0.0F, 1.0F, var5);
   }

   private static String handle() {
      Menu var0 = Menu.drawAnimation();
      int var1 = var0 != null && var0.keyCode != -1 ? var0.keyCode : 344;
      return KeycodeNames.handle(var1);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, float var5, float var6, ThemeColors var7) {
      float var8 = 9.5F;
      int var9 = ThemeColors.handle(var7.animate(), var7.save(), 0.34F);
      this.handle(var1, var2, FontRegistry.current, "w", var3, var4, var5, var8, var6, ThemeColors.handle(var9, 220), -0.45F);
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      FontObject var3,
      String var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      float var11
   ) {
      float var12 = ModuleStateHelper.handle(var3, var4, var8);
      float var13 = var5 + (var9 - var12) * 0.5F;
      float var14 = ModuleStateHelper.handle(var2, var3, var6, var7, var8) + var2.handle(var11);
      ModuleStateHelper.handle(var1, var2, var3, var13, var14, var8, var4, var10);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, float var5, int var6) {
      float var7 = var2.handle(0.75F);
      var1.handle(var3 - 7.0F * var7, var4 - 6.0F * var7, 2.5F * var7, var5 * 1.5F, var5, var6);
      var1.handle(var3 - 4.8F * var7, var4 - 4.6F * var7, 11.0F * var7, var5 * 1.5F, var5, var6);
      var1.handle(var3 - 3.6F * var7, var4 - 3.1F * var7, 8.5F * var7, 6.0F * var7, var2.handle(1.5F), ThemeColors.handle(var6, 128));
      var1.handle(var3 - 2.8F * var7, var4 + 3.0F * var7, 9.2F * var7, var5 * 1.4F, var5, var6);
      var1.process(var3 - 2.2F * var7, var4 + 6.3F * var7, 1.7F * var7, 0.0F, 1.0F, var6);
      var1.process(var3 + 5.0F * var7, var4 + 6.3F * var7, 1.7F * var7, 0.0F, 1.0F, var6);
   }
}
