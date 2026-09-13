package ru.wild.gui.widget;

import ru.wild.gui.screen.ModuleSearchPanel;
import ru.wild.render.BlurStateManager;
import ru.wild.util.render.LegacyGuiProjection;

public class RectangleHitTest extends BlurStateManager {
   public static boolean handle(double var0, double var2, double var4, double var6) {
      float[] var8 = LegacyGuiProjection.handle((float)var0, (float)var2);
      float var9 = var8[0];
      float var10 = var8[1];
      float var11 = BlurStateManager.providerClose + 104.735F + 5.0F;
      float var12 = BlurStateManager.presetSave + 34.025F + 5.0F;
      float var13 = 251.5F;
      float var14 = 199.5F;
      if (!BlurStateManager.moduleCollect && ModuleSearchPanel.handle(var9, var10, var11, var12, var13, var14)) {
         BlurStateManager.handle().handle(var6);
         return true;
      } else {
         return false;
      }
   }
}
