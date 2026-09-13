package ru.wild.gui.widget;

import ru.wild.WildClient;
import ru.wild.render.BlurStateManager;
import ru.wild.util.math.EasingFunctions;

public class PanelHitTest extends BlurStateManager {
   public static boolean handle(int var0, int var1, int var2) {
      boolean var3 = (var2 & 2) != 0;
      if (var3 && var0 == 70) {
         BlurStateManager.profileDraw = !BlurStateManager.profileDraw;
         if (!BlurStateManager.profileDraw && BlurStateManager.providerFetch == null) {
            BlurStateManager.providerFetch = "";
         }

         return true;
      } else if (BlurStateManager.matrixBlend != null) {
         if (var0 == 256) {
            BlurStateManager.matrixBlend.bindingActive = false;
            BlurStateManager.matrixBlend = null;
         } else if (var0 == 261) {
            BlurStateManager.matrixBlend.keyCode = -1;
            BlurStateManager.matrixBlend.bindingActive = false;
            BlurStateManager.compute(BlurStateManager.matrixBlend).handle(0.0, 0.2F, EasingFunctions.pending);
            BlurStateManager.matrixBlend = null;
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         } else {
            BlurStateManager.matrixBlend.keyCode = var0;
            BlurStateManager.matrixBlend.bindingActive = false;
            BlurStateManager.compute(BlurStateManager.matrixBlend).handle(1.0, 0.2F, EasingFunctions.pending);
            BlurStateManager.matrixBlend = null;
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         }

         return true;
      } else if (BlurStateManager.previous != null) {
         if (var0 == 256) {
            BlurStateManager.previous.output = false;
            BlurStateManager.previous = null;
         } else if (var0 == 261) {
            BlurStateManager.previous.config = -1;
            BlurStateManager.previous.output = false;
            BlurStateManager.previous = null;
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         } else {
            BlurStateManager.previous.config = var0;
            BlurStateManager.previous.output = false;
            BlurStateManager.previous = null;
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         }

         return true;
      } else {
         if (BlurStateManager.latest != null) {
            if (var0 == 256) {
               BlurStateManager.latest.current = false;
               BlurStateManager.latest = null;
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }

               return true;
            }

            if (var0 == 259) {
               if (!BlurStateManager.latest.state.isEmpty()) {
                  BlurStateManager.latest.state = BlurStateManager.latest.state.substring(0, BlurStateManager.latest.state.length() - 1);
                  if (WildClient.instance.renderer != null) {
                     WildClient.instance.renderer.compute();
                  }
               }

               return true;
            }
         }

         if (BlurStateManager.profileDraw) {
            if (var0 == 256) {
               BlurStateManager.profileDraw = false;
               BlurStateManager.providerFetch = "";
               return true;
            }

            if (var0 == 261) {
               BlurStateManager.providerFetch = "";
               return true;
            }

            if (var0 == 259) {
               if (BlurStateManager.providerFetch != null && !BlurStateManager.providerFetch.isEmpty()) {
                  if (var3) {
                     int var4 = BlurStateManager.providerFetch.lastIndexOf(32);
                     BlurStateManager.providerFetch = var4 < 0 ? "" : BlurStateManager.providerFetch.substring(0, var4);
                  } else {
                     BlurStateManager.providerFetch = BlurStateManager.providerFetch.substring(0, BlurStateManager.providerFetch.length() - 1);
                  }

                  return true;
               }

               BlurStateManager.providerFetch = "";
               return true;
            }
         }

         return false;
      }
   }
}
