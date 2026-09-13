package ru.wild.gui.widget;

import ru.wild.WildClient;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.gui.screen.ColorSettingInput;
import ru.wild.render.BlurStateManager;
import ru.wild.util.render.LegacyGuiProjection;

public class ScrollRegionHitTest extends BlurStateManager {
   public static boolean handle(double var0, double var2, int var4, double var5, double var7) {
      int var9 = (int)LegacyGuiProjection.handle((float)var0, (float)var2)[0];
      int var10 = (int)LegacyGuiProjection.handle((float)var0, (float)var2)[1];
      if (BlurStateManager.animationDraw != null && BlurStateManager.animationDraw instanceof ColorSetting) {
         ColorSetting var11 = BlurStateManager.animationDraw;
         float var12 = BlurStateManager.pointEncode;
         float var13 = BlurStateManager.animator;
         if (var12 != 0.0F || var13 != 0.0F) {
            float var14 = ColorControlRenderer.handle(var12);
            float var15 = ColorControlRenderer.process(var14);
            float var16 = ColorControlRenderer.compute(var13);
            float var17 = 148.0F;
            if (BlurStateManager.source) {
               ColorSettingInput.handle(var11, var9, var10, var15, var16);
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }

               return true;
            }

            if (BlurStateManager.target) {
               ColorSettingInput.handle(var11, var10, var16);
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }

               return true;
            }

            if (BlurStateManager.pending) {
               ColorSettingInput.handle(var11, var9, var15, var17);
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }

               return true;
            }
         }
      }

      if (BlurStateManager.summary != null) {
         NumberSetting var18 = BlurStateManager.summary;
         float var19 = (var9 - BlurStateManager.vectorMatch) / BlurStateManager.responseCompute;
         var19 = Math.max(0.0F, Math.min(1.0F, var19));
         var18.config = var18.state + (var18.cache - var18.state) * var19;
         if (WildClient.instance.renderer != null) {
            WildClient.instance.renderer.compute();
         }

         return true;
      } else {
         return false;
      }
   }
}
