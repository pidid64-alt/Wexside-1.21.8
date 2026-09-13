package ru.wild.gui.widget;

import ru.wild.WildClient;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.StringSetting;
import ru.wild.gui.screen.ColorSettingInput;
import ru.wild.gui.screen.ModuleListRenderer;
import ru.wild.gui.screen.ModuleSearchPanel;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.InputNames;

public class SettingRowRenderer extends BlurStateManager {
   public static boolean handle(RoundedRectRenderer var0, Setting var1, float var2, float var3, float var4, int var5, int var6, int var7) {
      if (var1 instanceof BooleanSetting var8) {
         float var9 = 8.0F;
         float var10 = var2 + var4 - var9 - 3.0F;
         float var11 = var3 + 2.0F;
         if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var10, var11, var9, var9)) {
            var8.process(!var8.compute());
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }

            return true;
         }
      }

      if (var1 instanceof KeybindSetting var24) {
         float var31 = 10.075F;
         String var38 = var24.output ? "..." : InputNames.process(var24.config);
         float var45 = RoundedRectRenderer.handle(FontRegistry.instance, var38, 12.0F).instance;
         float var12 = 16.055F;
         float var13 = Math.max(var12, var45 + 8.0F);
         float var14 = var2 + var4 - var13 - 2.0F;
         if (var14 < var2) {
            var14 = var2;
            var13 = var4 - 2.0F;
         }

         float var15 = var14 - 6.0F;
         float var16 = var13 + 2.0F;
         float var17 = var3;
         float var18 = var31;
         if (var15 < var2) {
            var16 = var15 + var16 - var2;
            var15 = var2;
         }

         if (ModuleSearchPanel.handle(var5, var6, var15, var17, var16, var18)) {
            if (var7 == 0) {
               if (BlurStateManager.previous != var24) {
                  if (BlurStateManager.previous != null) {
                     BlurStateManager.previous.output = false;
                  }

                  BlurStateManager.previous = var24;
                  var24.output = true;
               }

               return true;
            }

            if (BlurStateManager.previous == var24 && var7 >= 0 && var7 <= 8) {
               int var78 = -100 - var7;
               var24.config = var78;
               var24.output = false;
               BlurStateManager.previous = null;
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }

               return true;
            }
         }
      }

      if (var1 instanceof ColorSetting var25) {
         float var32 = 40.0F;
         float var39 = var2 + var4 - var32 - 2.0F;
         float var46 = var39 - 10.0F;
         float var52 = var3;
         float var57 = 46.48F;
         float var61 = 10.075F;
         if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var46, var52, var57, var61)) {
            if (BlurStateManager.animationDraw == var25) {
               BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
               BlurStateManager.animationDraw = null;
               BlurStateManager.pointEncode = 0.0F;
               BlurStateManager.animator = 0.0F;
            } else {
               BlurStateManager.animationDraw = var25;
               BlurStateManager.cache.process(AnimationDirection.FORWARDS);
               float[] var69 = ModuleListRenderer.handle(var0, var25);
               if (var69 != null) {
                  BlurStateManager.pointEncode = var69[0];
                  BlurStateManager.animator = var69[1];
               }
            }

            return true;
         }

         if (BlurStateManager.animationDraw == var25 && ColorSettingInput.handle(var5, var6, var7)) {
            return true;
         }
      }

      if (var1 instanceof NumberSetting var26) {
         float var33 = 4.0F;
         float var40 = var3 + 10.0F;
         float var47 = var4 - 2.5F;
         float var53 = var2;
         float var58 = var40 + 2.0F;
         if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var53, var58, var47, var33)) {
            BlurStateManager.summary = var26;
            BlurStateManager.vectorMatch = var53;
            BlurStateManager.itemProject = var58;
            BlurStateManager.responseCompute = var47;
            float var64 = (var5 - var53) / var47;
            var64 = Math.max(0.0F, Math.min(1.0F, var64));
            var26.config = var26.state + (var26.cache - var26.state) * var64;
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }

            return true;
         }
      }

      if (var1 instanceof ActionSetting var27) {
         float var34 = 10.075F;
         float var41 = 60.0F;
         float var48 = var2 + var4 - var41 - 2.0F;
         if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var48, var3, var41, var34)) {
            var27.resolve();
            return true;
         }
      }

      if (var1 instanceof ModeSetting var28) {
         float var35 = 2.0F;
         float var42 = 10.075F;
         float var49 = 3.0F;
         float var54 = -2.0F;
         float var59 = var49;
         float var62 = 0.0F;

         for (String var70 : var28.config) {
            float var73 = RoundedRectRenderer.handle(FontRegistry.instance, var70, 12.0F).instance + var49 * 2.0F;
            if (var59 + var73 > var4 && var59 > var49) {
               var59 = var49;
               var62 += var42 + var54;
            }

            var59 += var73 + var35;
         }

         float var67 = var3 + 10.0F;
         float var71 = var4;
         float var74 = var62 + var42;
         if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var2, var67, var71, var74)) {
            float var76 = var49;
            float var19 = 1.5F;

            for (String var21 : var28.config) {
               float var22 = RoundedRectRenderer.handle(FontRegistry.instance, var21, 12.0F).instance + var49 * 2.0F;
               if (var76 + var22 > var4 && var76 > var49) {
                  var76 = var49;
                  var19 += var42 + var54;
               }

               float var23 = var42;
               if (ModuleSearchPanel.handle(var5, var6, var2 + var76, var67 + var19, var22, var23)) {
                  var28.state = var21;
                  var28.current = var28.config.indexOf(var21);
                  if (WildClient.instance.renderer != null) {
                     WildClient.instance.renderer.compute();
                  }

                  return true;
               }

               var76 += var22 + var35;
            }
         }
      }

      if (var1 instanceof StringSetting var29) {
         float var36 = 10.075F;
         float var43 = 63.56F;
         float var50 = var2 + 42.0F;
         float var55 = var3;
         if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var50, var55, var43, var36)) {
            if (BlurStateManager.latest != var29) {
               if (BlurStateManager.latest != null) {
                  BlurStateManager.latest.current = false;
               }

               BlurStateManager.latest = var29;
               var29.current = true;
            }

            return true;
         }

         if (var7 == 0 && BlurStateManager.latest == var29) {
            BlurStateManager.latest.current = false;
            BlurStateManager.latest = null;
            if (WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         }
      }

      if (var1 instanceof ChoiceSetting var30) {
         float var37 = var3 + 10.0F;
         float var44 = var2;
         float var51 = var37;
         float var56 = 3.0F;
         float var60 = 10.0F;
         float var63 = 4.0F;

         for (BooleanSetting var72 : var30.config) {
            float var75 = RoundedRectRenderer.handle(FontRegistry.instance, var72.instance, 12.0F).instance;
            float var77 = var75 + var63 * 2.0F;
            if (var44 + var77 > var2 + var4) {
               var44 = var2;
               var51 += var60 + var56;
            }

            if (var7 == 0 && ModuleSearchPanel.handle(var5, var6, var44, var51, var77, var60)) {
               var72.process(!var72.compute());
               if (WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }

               return true;
            }

            var44 += var77 + var56;
         }
      }

      return false;
   }
}
