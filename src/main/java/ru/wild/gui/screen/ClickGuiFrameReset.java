package ru.wild.gui.screen;

import ru.wild.WildClient;
import ru.wild.render.BlurStateManager;

public class ClickGuiFrameReset extends BlurStateManager {
   public static void process() {
      if ((BlurStateManager.summary != null || BlurStateManager.source || BlurStateManager.target || BlurStateManager.pending)
         && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }

      BlurStateManager.source = false;
      BlurStateManager.target = false;
      BlurStateManager.pending = false;
      BlurStateManager.summary = null;
      BlurStateManager.vectorMatch = 0.0F;
      BlurStateManager.itemProject = 0.0F;
      BlurStateManager.responseCompute = 0.0F;
   }
}
