package ru.wild.gui.widget;

import ru.wild.WildClient;
import ru.wild.render.BlurStateManager;

public class TextInputFilter extends BlurStateManager {
   public static boolean handle(char var0, int var1) {
      if (BlurStateManager.latest != null) {
         if (var0 == '\b') {
            if (!BlurStateManager.latest.state.isEmpty()) {
               BlurStateManager.latest.state = BlurStateManager.latest.state.substring(0, BlurStateManager.latest.state.length() - 1);
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }
            }

            return true;
         }

         if (var0 >= ' ' && var0 != 127) {
            if (BlurStateManager.latest.state.length() < 16) {
               BlurStateManager.latest.state = BlurStateManager.latest.state + var0;
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }
            }

            return true;
         }
      }

      if (BlurStateManager.profileDraw) {
         if (var0 == '\b') {
            return true;
         }

         if (var0 >= ' ' && var0 != 127 && (var0 >= 'a' && var0 <= 'z' || var0 >= 'A' && var0 <= 'Z' || var0 >= '0' && var0 <= '9' || var0 == ' ')) {
            if (BlurStateManager.providerFetch.length() < 50) {
               BlurStateManager.providerFetch = BlurStateManager.providerFetch + var0;
            }

            return true;
         }
      }

      return false;
   }
}
