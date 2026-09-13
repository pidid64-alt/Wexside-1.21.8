package ru.wild.render.shader;

import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.gui.screen.ModuleSearchPanel;
import ru.wild.render.BlurStateManager;
import ru.wild.util.math.AnimationDirection;

public class BlurFramebufferResizer extends BlurStateManager {
   public static void handle(int var0, int var1) {
      float var2 = BlurStateManager.providerClose;
      float var3 = BlurStateManager.presetSave;
      float var4 = 0.0F;

      for (ModuleCategory var8 : BlurStateManager.rendererScan) {
         if (ModuleSearchPanel.handle(var0, var1, var2, var3 + 43.365F + var4 - 2.0F, 104.34F, 21.325F) && BlurStateManager.sourceSchedule != var8) {
            BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
            BlurStateManager.animationDraw = null;
            BlurStateManager.sourceSchedule = var8;
            BlurStateManager.timerRender = WildClient.instance.data.handle(BlurStateManager.sourceSchedule);
            BlurStateManager.context.compute();
            BlurStateManager.config.compute();
            BlurStateManager.handle().apply();
            WildClient.instance.selection.handle(var8);
         }

         var4 += 24.0F;
      }
   }
}
