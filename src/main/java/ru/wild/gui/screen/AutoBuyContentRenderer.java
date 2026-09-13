package ru.wild.gui.screen;
import net.minecraft.client.gui.DrawContext;
import ru.wild.WildClient;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.PreviewPlayerGenerator;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.FoundryAssetBrowser;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.ModulePanelRegistry;
import ru.wild.gui.widget.ModulePanelRenderer;
import ru.wild.gui.widget.ModulePlacement;
import ru.wild.gui.widget.ModuleSettingsPanel;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;

public final class AutoBuyContentRenderer {
   private final ModuleSettingsPanel instance;
   private final DiagnosticsScreen data = new DiagnosticsScreen();
   private final FoundryAssetBrowser context = new FoundryAssetBrowser();
   private final PreviewPlayerGenerator config = new PreviewPlayerGenerator();

   public FoundryAssetBrowser handle() {
      return this.context;
   }

   public PreviewPlayerGenerator process() {
      return this.config;
   }
   public void handle(
      RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ViewportLayoutState var4, ModuleLayoutResult var5, ThemeRenderContext var6
   ) {
      GuiMetrics var7 = var6.update();
      ThemeColors var8 = var6.apply();
      float var9 = var4.load();
      var1.handle(
         var4.drawAnimation(),
         var4.encodePoint(),
         var4.animate(),
         var9,
         var7.handle(4.0F),
         var7.handle(4.0F),
         var7.handle(16.0F),
         var7.handle(4.0F),
         ModuleStateHelper.prepare(var8)
      );
      if (var8.unload()) {
         var1.handle(
            var4.drawAnimation() + 1.0F,
            var4.encodePoint() + 1.0F,
            Math.max(1.0F, var4.animate() - 2.0F),
            Math.max(1.0F, var9 - 2.0F),
            Math.max(0.0F, var7.handle(4.0F) - 1.0F),
            Math.max(0.0F, var7.handle(4.0F) - 1.0F),
            Math.max(0.0F, var7.handle(16.0F) - 1.0F),
            Math.max(0.0F, var7.handle(4.0F) - 1.0F),
            ModuleStateHelper.process(var8, 0.82F),
            1.0F
         );
      }

      if (var3.refreshClient()) {
         var1.compute();
         var1.handle(
            var4.drawAnimation(), var4.encodePoint(), var4.animate(), var9, var7.handle(4.0F), var7.handle(4.0F), var7.handle(16.0F), var7.handle(4.0F)
         );

         try {
            this.config.handle(var1, var3, var4, var6);
         } finally {
            var1.compute();
            var1.apply();
         }
      } else if (var3.validateData()) {
         var1.compute();
         var1.handle(
            var4.drawAnimation(), var4.encodePoint(), var4.animate(), var9, var7.handle(4.0F), var7.handle(4.0F), var7.handle(16.0F), var7.handle(4.0F)
         );
         boolean var28 = false /* VF: Semaphore variable */;

         try {
            var28 = true;
            this.data.handle(var1, var3, var4, var6);
            var28 = false;
         } finally {
            if (var28) {
               var1.compute();
               var1.apply();
            }
         }

         var1.compute();
         var1.apply();
      } else if (var3.collapseConfig()) {
         var1.compute();
         var1.handle(
            var4.drawAnimation(), var4.encodePoint(), var4.animate(), var9, var7.handle(4.0F), var7.handle(4.0F), var7.handle(16.0F), var7.handle(4.0F)
         );

         try {
            this.handle(var1, var2, var3, var4, var7, var6);
         } finally {
            var1.compute();
            var1.apply();
         }
      } else if (var3.renderScale()) {
         var1.compute();
         var1.handle(
            var4.drawAnimation(), var4.encodePoint(), var4.animate(), var9, var7.handle(4.0F), var7.handle(4.0F), var7.handle(16.0F), var7.handle(4.0F)
         );

         try {
            this.context.handle(var1, var3, var6, var4.drawAnimation(), var4.encodePoint(), var4.animate(), var9);
         } finally {
            var1.compute();
            var1.apply();
         }
      } else {
         float var10 = ModuleStateHelper.compute(var7);
         float var11 = var4.encodePoint() - var10;
         float var12 = var4.encodePoint() + var9 + var10;
         float var13 = var3.saveScale().compute();
         float var14 = Math.min(1.0F, Math.abs(var13) / Math.max(var7.handle(180.0F), 1.0F));
         float var15 = var3.handle("content:scroll:material", var14, SpringAnimationSpec.handle());
         ModuleStateHelper.handle(
            var1,
            var7,
            var8,
            var4.drawAnimation(),
            var4.encodePoint(),
            var4.animate(),
            var9,
            var7.handle(4.0F),
            var7.handle(4.0F),
            var7.handle(16.0F),
            var7.handle(4.0F),
            var13,
            () -> {
               for (ModulePlacement var10x : var5.process()) {
                  if (!(var10x.compute() + var10x.update() < var11) && !(var10x.compute() > var12)) {
                     this.instance.handle(var1, var2, var3, var10x, var6, var15);
                  }
               }
            }
         );
      }

      if (!var3.collapseConfig() && !var3.validateData() && !var3.renderScale() && !var3.refreshClient()) {
         this.handle(var1, var3, var4, var5, var6);
      }
   }

   private void handle(RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ViewportLayoutState var4, GuiMetrics var5, ThemeRenderContext var6) {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         AutoBuy var7 = WildClient.instance.data.handle(AutoBuy.class);
         ModulePanelRenderer var8 = ModulePanelRegistry.handle(var7);
         if (var8 != null) {
            var8.handle(var1, var2, var3, ModulePanelRegistry.handle(var7, var4, var5), var6);
         }
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ModuleLayoutResult var4, ThemeRenderContext var5) {
      GuiMetrics var6 = var5.update();
      if (!(var4.compute() <= 0.5F)) {
         float var7 = Math.max(var6.handle(24.0F), var3.fetch() * (var3.fetch() / (var3.fetch() + var4.compute())));
         float var8 = Math.min(1.0F, Math.max(0.0F, -var2.convertAction() / var4.compute()));
         float var9 = var3.submit() + (var3.fetch() - var7) * var8;
         float var10 = var3.matchVector() - var6.handle(0.35F);
         ModuleStateHelper.handle(
            var1,
            var6,
            var5.apply(),
            var10,
            var3.submit(),
            var6.load(),
            var3.fetch(),
            var9,
            var7,
            var2.saveScale().compute(),
            0.0F,
            1L,
            var2.sampleLayer(),
            var2.sendWorld(),
            var2::compute
         );
      }
   }
   public AutoBuyContentRenderer(ModuleSettingsPanel var1) {
      this.instance = var1;
   }
}
