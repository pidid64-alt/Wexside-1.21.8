package ru.wild.gui.screen;

import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.core.CoreDiagnosticsPanel;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.widget.AnimatedUiElement;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.ModulePanelRegistry;
import ru.wild.gui.widget.SurfaceHitResolver;
import ru.wild.gui.widget.SurfaceInteractionRouter;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.gui.widget.UiElementPicker;
import ru.wild.gui.widget.VisibilityTransform;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.util.render.StyledTextRenderer;
import ru.wild.util.text.RichTextParser;

public final class ModernClickGuiInputHandler {
   private final ThemeBrowserLayout instance;
   private final StyledTextRenderer data;
   private final RichTextParser context;
   private AnimatedUiElement config;
   public boolean handle(
      ModernClickGuiState var1,
      ViewportLayoutState var2,
      ModuleLayoutResult var3,
      GuiMetrics var4,
      ThemePaletteRegistry var5,
      SurfaceHitResolver.Mode var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11
   ) {
      var1.execute(var7);
      var1.prepare(var8);
      this.config = null;
      if (var1.checkFrame() && var11 >= 0 && var11 <= 8) {
         var1.update(var11);
         return true;
      }

      if (var6 == SurfaceHitResolver.Mode.MAIN && var11 == 0 && handle(var2, var4, var9, var10)) {
         var1.check(false);
         var1.onTick(false);
         var1.handle((StringSetting)null);
         var1.handle(var7, var8, var2, var4);
         return true;
      }

      if (var6 == SurfaceHitResolver.Mode.THEME && var11 == 0 && var1.synchronizePoint() && process(var2, var4, var9, var10)) {
         var1.check(false);
         var1.onTick(false);
         var1.handle((StringSetting)null);
         var1.process(var7, var8, var2, var4);
         return true;
      }

      if (var11 == 0 && SurfaceInteractionRouter.handle(var9, var10, var6)) {
         var1.check(false);
         var1.onTick(false);
         var1.handle((StringSetting)null);
         return true;
      }

      if (var6 == SurfaceHitResolver.Mode.MAIN && var11 == 0 && var1.validateData()) {
         if (DiagnosticsScreen.process(var2, var4, var9, var10)) {
            var1.update(DiagnosticsScreen.handle(var2, var4, var9));
            var1.encodePoint();
            return true;
         }

         if (DiagnosticsScreen.compute(var2, var4, var9, var10)) {
            var1.apply(DiagnosticsScreen.process(var2, var4, var10));
            var1.animate();
            return true;
         }
      }

      List var12 = var6 == SurfaceHitResolver.Mode.THEME ? this.instance.handle(var1, var2, var4, var5) : this.instance.handle(var1, var2, var3, var4, var9);
      AnimatedUiElement var13 = UiElementPicker.handle(var12, var9, var10, var11);
      if (var13 != null) {
         this.config = var13;
         var1.execute(var13.handle(var9));
         var1.prepare(var13.process(var10));
         boolean var16 = false /* VF: Semaphore variable */;

         try {
            var16 = true;
            var13.handle(var1);
            var16 = false;
         } finally {
            if (var16) {
               var1.execute(var7);
               var1.prepare(var8);
            }
         }

         var1.execute(var7);
         var1.prepare(var8);
         SurfaceInteractionRouter.resolve();
         return true;
      } else {
         if (var11 == 0) {
            var1.check(false);
            var1.onTick(false);
            var1.handle((StringSetting)null);
            var1.performVector();
            if (var6 == SurfaceHitResolver.Mode.MAIN) {
               if (this.compute(var2, var4, var9, var10)) {
                  var1.handle(var7, var8, var2);
               }

               return true;
            }

            if (var6 == SurfaceHitResolver.Mode.THEME && var1.synchronizePoint() && this.resolve(var2, var4, var9, var10)) {
               var1.process(var7, var8, var2);
               return true;
            }
         }

         return var6 != SurfaceHitResolver.Mode.NONE;
      }
   }

   public static boolean handle(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3) {
      if (var0 != null && var1 != null) {
         float var4 = Math.max(14.0F, var1.handle(22.0F));
         float var5 = var0.handle() + var1.resolve() - var4;
         float var6 = var0.process() + var1.update() - var4;
         return var2 >= var5 && var3 >= var6 && var2 < var0.handle() + var1.resolve() && var3 < var0.process() + var1.update();
      } else {
         return false;
      }
   }

   public static boolean process(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3) {
      if (var0 != null && var1 != null) {
         float var4 = Math.max(12.0F, var1.process(18.0F));
         float var5 = var0.projectItem() + var1.save() - var4;
         float var6 = var0.computeResponse() + var1.submit() - var4;
         return var2 >= var5 && var3 >= var6 && var2 < var0.projectItem() + var1.save() && var3 < var0.computeResponse() + var1.submit();
      } else {
         return false;
      }
   }

   public boolean handle(ModernClickGuiState var1, float var2, float var3, float var4, float var5) {
      float var6 = this.config == null ? var4 : this.config.handle(var4);
      float var7 = this.config == null ? var5 : this.config.process(var5);
      var1.execute(var6);
      var1.prepare(var7);
      boolean var8 = SurfaceInteractionRouter.process();
      boolean var9 = var1.encodeSession() != null;
      boolean var10 = var1.projectPlayer() || var1.matchPlayer() || var1.closeCache();
      boolean var11 = var1.load();
      boolean var12 = var1.savePreset();
      boolean var13 = var1.convertWindow();
      boolean var14 = var1.closeProvider();
      boolean var15 = var1.writePreset();
      boolean var16 = ModulePanelRegistry.resolve(var1);
      boolean var17 = var8 || var11 || var12 || var13 || var14 || var15 || var9 || var10 || var16;
      var1.handle((NumberSetting)null);
      var1.compute((ColorSetting)null);
      if (var9) {
         var1.scheduleAnimation();
      }

      if (var10) {
         this.data.handle(var1);
      }

      var1.execute(var2);
      var1.prepare(var3);
      this.config = null;
      return var17;
   }

   public void handle(ModernClickGuiState var1) {
      SurfaceInteractionRouter.process();
      var1.load();
      var1.savePreset();
      var1.convertWindow();
      var1.closeProvider();
      var1.writePreset();
      var1.handle((NumberSetting)null);
      var1.compute((ColorSetting)null);
      var1.performVector();
      ModulePanelRegistry.compute(var1);
      this.config = null;
   }

   public boolean handle(ModernClickGuiState var1, ViewportLayoutState var2, GuiMetrics var3, float var4, float var5, float var6, float var7) {
      var1.execute(var4);
      var1.prepare(var5);
      float var8 = this.config == null ? var6 : this.config.handle(var6);
      float var9 = this.config == null ? var7 : this.config.process(var7);
      if (SurfaceInteractionRouter.process(var8, var9)) {
         return true;
      } else if (var1.advanceOption()) {
         var1.update(DiagnosticsScreen.handle(var2, var3, var8));
         return true;
      } else if (var1.scanEffect()) {
         var1.apply(DiagnosticsScreen.process(var2, var3, var9));
         return true;
      } else if (var1.submitScreen()) {
         var1.resolve(var4, var5);
         return true;
      } else if (var1.runListener()) {
         var1.update(var4, var5);
         return true;
      } else if (var1.receiveEvent()) {
         var1.apply(var4, var5);
         return true;
      } else if (var1.cancelRenderer()) {
         var1.compute(var4, var5);
         return true;
      } else if (var1.projectPlayer() || var1.matchPlayer() || var1.closeCache()) {
         this.data.handle(var1, var8, var9);
         return true;
      } else if (var1.encodeSession() != null) {
         this.data.handle(var1, var8);
         return true;
      } else {
         return ModulePanelRegistry.handle(var1, var8, var9);
      }
   }

   public boolean handle(
      ModernClickGuiState var1,
      ViewportLayoutState var2,
      ModuleLayoutResult var3,
      GuiMetrics var4,
      SurfaceHitResolver.Mode var5,
      float var6,
      float var7,
      double var8,
      double var10
   ) {
      if (var5 == SurfaceHitResolver.Mode.THEME) {
         var1.handle((float)var10 * var4.handle(36.0F), var4);
         return true;
      }

      if (var5 != SurfaceHitResolver.Mode.MAIN) {
         return false;
      }

      if (!ModuleStateHelper.handle(var6, var7, var2.drawAnimation(), var2.encodePoint(), var2.animate(), var2.load())) {
         return this.update(var2, var4, var6, var7);
      }

      if (var1.executeCache()) {
         VisibilityTransform var14 = VisibilityTransform.resolve(var1.handle(UiAnimationKeys.animate()), var2, var4);
         CoreDiagnosticsPanel.handle(var2, var4, var14.localX(var6), var14.localY(var7), var10);
         return true;
      }

      if (!var1.validateData()) {
         if (var1.collapseConfig() && this.handle(var1, var2, var4, var6, var7, var10)) {
            return true;
         }

         if (ModulePanelRegistry.handle(var1, var3, var4, var6, var7, var10)) {
            return true;
         }

         var1.process((float)var10 * var4.handle(36.0F), var4);
         return true;
      } else {
         if (DiagnosticsScreen.handle(var2, var4, var6, var7)) {
            float var12 = (float)var10 * var4.handle(36.0F);
            float var13 = (float)var8 * var4.handle(64.0F);
            if (Math.abs(var13) <= 0.001F && (Screen.hasShiftDown() || Screen.hasControlDown())) {
               var13 = (float)var10 * var4.handle(96.0F);
               var12 = 0.0F;
            }

            var1.process(var12, var13);
         }

         return true;
      }
   }

   private boolean handle(ModernClickGuiState var1, ViewportLayoutState var2, GuiMetrics var3, float var4, float var5, double var6) {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         AutoBuy var8 = WildClient.instance.data.handle(AutoBuy.class);
         return var8 != null && ModulePanelRegistry.process(var8)
            ? ModulePanelRegistry.handle(var1, new ModuleLayoutResult(List.of(ModulePanelRegistry.handle(var8, var2, var3)), 0.0F), var3, var4, var5, var6)
            : false;
      } else {
         return false;
      }
   }

   public boolean handle(ModernClickGuiState var1, int var2) {
      return ModulePanelRegistry.handle(var1, var2) ? true : this.context.handle(var1, var2);
   }

   public boolean handle(ModernClickGuiState var1, char var2) {
      return ModulePanelRegistry.handle(var1, var2) ? true : this.context.handle(var1, var2);
   }

   public void handle(ModernClickGuiState var1, float var2) {
      if (!var1.cancelRenderer() && !var1.receiveEvent()) {
         if (!var1.projectPlayer() && !var1.matchPlayer() && !var1.closeCache()) {
            this.data.handle(var1, var2);
         } else {
            this.data.handle(var1, var2, var1.sendWorld());
         }
      }
   }

   private boolean compute(ViewportLayoutState var1, GuiMetrics var2, float var3, float var4) {
      return ModuleStateHelper.handle(var3, var4, var1.check(), var1.onTick(), var1.refresh(), var1.select()) || this.apply(var1, var2, var3, var4);
   }

   private boolean resolve(ViewportLayoutState var1, GuiMetrics var2, float var3, float var4) {
      return ModuleStateHelper.handle(var3, var4, var1.projectItem(), var1.computeResponse(), var2.save(), var2.submit());
   }

   private boolean update(ViewportLayoutState var1, GuiMetrics var2, float var3, float var4) {
      return ModuleStateHelper.handle(var3, var4, var1.handle(), var1.process(), var2.resolve(), var2.update());
   }

   private boolean apply(ViewportLayoutState var1, GuiMetrics var2, float var3, float var4) {
      if (!ModuleStateHelper.handle(var3, var4, var1.compute(), var1.resolve(), var1.update(), var1.apply())) {
         return false;
      }

      if (ModuleStateHelper.handle(var3, var4, var1.compute() + var2.handle(16.0F), var1.resolve() + var2.handle(16.0F), var2.handle(40.0F), var2.handle(40.0F))
         )
       {
         return false;
      }

      float var5 = AutoBuyScreen.compute(var1, var2);
      float var6 = AutoBuyScreen.process(var2);
      if (ModuleStateHelper.handle(var3, var4, var5, AutoBuyScreen.resolve(var1, var2), var6, var6)) {
         return false;
      }

      float var7 = var5;
      float var8 = var1.resolve() + var2.handle(89.0F);

      for (int var9 = 0; var9 < ModuleCategory.values().length; var9++) {
         if (ModuleStateHelper.handle(var3, var4, var7, var8 + var9 * var2.handle(56.0F), var6, var6)) {
            return false;
         }
      }

      float var10 = AutoBuyScreen.apply(var1, var2);
      return !ModuleStateHelper.handle(var3, var4, var7, var10, var6, var6);
   }
   public ModernClickGuiInputHandler(ThemeBrowserLayout var1, StyledTextRenderer var2, RichTextParser var3) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
   }
}
