package ru.wild.gui.screen;

import ru.wild.render.BlurStateManager;
import ru.wild.util.math.EasingFunctions;

public class ClickGuiCloseTransition extends BlurStateManager {
   public static boolean process() {
      if (!BlurStateManager.moduleCollect && BlurStateManager.mode.select() > 0.0) {
         BlurStateManager.mode.handle(0.0, 0.4F, EasingFunctions.summary);
         BlurStateManager.moduleCollect = true;
      }

      return false;
   }
}
