package ru.wild.gui.widget;

import ru.wild.gui.screen.ClickGuiBackdropRenderer;
import ru.wild.gui.screen.ColorSettingInput;
import ru.wild.gui.screen.ModuleListRenderer;
import ru.wild.gui.screen.ModuleSearchPanel;
import ru.wild.render.BlurStateManager;
import ru.wild.render.shader.BlurFramebufferResizer;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.GuiScaleMetrics;
import ru.wild.util.render.LegacyGuiProjection;
import ru.wild.util.render.RoundedRectRenderer;

public class RoundedHitTest extends BlurStateManager {
   public static boolean handle(RoundedRectRenderer var0, double var1, double var3, int var5) {
      int var6 = (int)LegacyGuiProjection.handle((float)var1, (float)var3)[0];
      int var7 = (int)LegacyGuiProjection.handle((float)var1, (float)var3)[1];
      GuiScaleMetrics var8 = new GuiScaleMetrics(BlurStateManager.instance);
      BlurStateManager.providerClose = (int)NumericTransform.onTick(
         BlurStateManager.providerClose, 0.0F, LegacyGuiProjection.handle(var8.handle()) - BlurStateManager.windowConvert
      );
      BlurStateManager.presetSave = (int)NumericTransform.onTick(
         BlurStateManager.presetSave, 0.0F, LegacyGuiProjection.handle(var8.process()) - BlurStateManager.presetWrite
      );
      if (!BlurStateManager.moduleCollect) {
         float var9 = BlurStateManager.providerClose + 111.885F;
         float var10 = BlurStateManager.presetSave + 6.185F;
         float var11 = 124.04F;
         float var12 = 21.325F;
         if (var5 == 0 && ModuleSearchPanel.handle(var6, var7, var9, var10, var11, var12)) {
            BlurStateManager.profileDraw = true;
            return true;
         }

         BlurFramebufferResizer.handle(var6, var7);
         if (ColorSettingInput.handle(var6, var7, var5)) {
            return true;
         }

         if (ModuleListRenderer.handle(var0, var6, var7, var5)) {
            return true;
         }

         ClickGuiBackdropRenderer.handle(var1, var3, var5);
      }

      if (BlurStateManager.previous != null && var5 >= 0 && var5 <= 2) {
         int var13 = -100 - var5;
         BlurStateManager.previous.config = var13;
         BlurStateManager.previous.output = false;
         BlurStateManager.previous = null;
         return true;
      } else {
         return false;
      }
   }
}
