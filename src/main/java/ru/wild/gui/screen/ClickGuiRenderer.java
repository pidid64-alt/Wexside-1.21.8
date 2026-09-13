package ru.wild.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.wild.module.api.Module;
import ru.wild.render.BlurStateManager;
import ru.wild.util.render.LegacyGuiProjection;
import ru.wild.util.render.RoundedRectRenderer;

public class ClickGuiRenderer extends BlurStateManager {
   public static void handle(RoundedRectRenderer var0, DrawContext var1, int var2, int var3, float var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      if (var5 != null && var5.getWindow() != null) {
         int var6 = var5.getWindow().getFramebufferWidth();
         int var7 = var5.getWindow().getFramebufferHeight();
         if (var6 > 0 && var7 > 0) {
            int var8 = (int)(var2 / LegacyGuiProjection.instance);
            int var9 = (int)(var3 / LegacyGuiProjection.instance);
            BlurStateManager.colorMeasure = var8;
            BlurStateManager.animationSchedule = var9;
            BlurStateManager.mode.handle();
            BlurStateManager.selection.handle();
            BlurStateManager.enabled.handle();
            BlurStateManager.renderer.handle();
            if (BlurStateManager.timerRender != null) {
               for (Module var11 : BlurStateManager.timerRender) {
                  BlurStateManager.handle(var11).handle();
                  BlurStateManager.process(var11).handle();
                  BlurStateManager.compute(var11).handle();
               }
            }

            BlurStateManager.current.handle(1.0);
            float var17 = BlurStateManager.mode.update();
            if (!(var17 <= 0.001F)) {
               float var18 = var5.getWindow().getScaledWidth();
               float var12 = var5.getWindow().getScaledHeight();
               BlurStateManager.providerClose = var18 / 2.0F - BlurStateManager.windowConvert / 2.0F;
               BlurStateManager.presetSave = var12 / 2.0F - BlurStateManager.presetWrite / 2.0F - (80.0F - 80.0F * var17);
               float var13 = (float)var5.getWindow().getFramebufferWidth() / var5.getWindow().getScaledWidth();
               var0.compute(var13);

               try {
                  if (BlurStateManager.output.compute()) {
                     var0.handle(23.0F);
                  }

                  var0.handle(0.0F, 0.0F, var18, var12, RoundedRectRenderer.ColorState.resolve(0, 0, 0, (int)(140.0F * var17)));
                  ModuleCategoryPanel.handle(var0, var8, var9, var17);
               } finally {
                  var0.prepare();
                  var0.prepare();
               }
            }
         }
      }
   }
}
