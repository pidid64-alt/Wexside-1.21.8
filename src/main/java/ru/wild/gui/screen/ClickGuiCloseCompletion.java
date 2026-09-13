package ru.wild.gui.screen;

import ru.wild.render.BlurStateManager;

public class ClickGuiCloseCompletion extends BlurStateManager {
   public static void process() {
      if (BlurStateManager.moduleCollect && BlurStateManager.mode.compute()) {
         BlurStateManager.moduleCollect = false;
      }
   }
}
