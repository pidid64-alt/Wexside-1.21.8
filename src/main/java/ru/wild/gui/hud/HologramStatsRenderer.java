package ru.wild.gui.hud;

import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.render.RoundedRectRenderer;

public final class HologramStatsRenderer {
   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      float var6 = var2.handle(UiAnimationKeys.load());
      if (!(var6 < 0.01F) && var2.stopElement() != null && !var2.stopElement().isEmpty()) {
         GuiMetrics var7 = var3.update();
         ThemeColors var8 = var3.apply();
         String var9 = var2.stopElement();
         float var10 = ModuleStateHelper.handle(var7, FontRegistry.instance, var9, 9.0F);
         float var11 = var7.handle(8.0F);
         float var12 = var7.handle(4.0F);
         float var13 = var10 + var11 * 2.0F;
         float var14 = Math.max(var7.handle(18.0F), var7.handle(9.0F) + var12 * 2.0F);
         float var15 = var7.handle(6.0F);
         float var16 = var2.animateItem() + var7.handle(12.0F);
         float var17 = var2.readValue() - var14 - var7.handle(4.0F);
         if (var16 + var13 > var4 - var7.handle(4.0F)) {
            var16 = var4 - var13 - var7.handle(4.0F);
         }

         if (var16 < var7.handle(4.0F)) {
            var16 = var7.handle(4.0F);
         }

         if (var17 < var7.handle(4.0F)) {
            var17 = var2.readValue() + var7.handle(16.0F);
         }

         var1.update(var6);

         try {
            var1.handle(
               var16,
               var17,
               var13,
               var14,
               var15,
               var7.handle(var8.unload() ? 8.0F : 6.0F),
               var7.handle(var8.unload() ? 1.5F : 1.0F),
               var8.unload() ? ThemeColors.handle(46, 59, 70, 24) : ThemeColors.handle(0, 0, 0, 48)
            );
            int var18 = var8.unload() ? ModuleStateHelper.handle(var8, 0.0F) : ThemeColors.handle(ThemeColors.handle(var8.execute(), var8.apply(), 0.52F), 242);
            int var19 = ThemeColors.handle(var18, ThemeColors.handle(var8.save(), var18 >>> 24 & 0xFF), 0.018F);
            int var20 = ThemeColors.handle(var18, ThemeColors.handle(var8.submit(), var18 >>> 24 & 0xFF), 0.014F);
            var1.process(var16, var17, var13, var14, var15, var19, var20);
            ModuleStateHelper.handle(var1, var7, FontRegistry.instance, var16 + var11, var17, var14, 9.0F, var9, var8.load());
         } finally {
            var1.onTick();
         }
      }
   }
}
