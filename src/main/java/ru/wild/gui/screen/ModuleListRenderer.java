package ru.wild.gui.screen;

import java.util.List;
import java.util.stream.Collectors;
import org.wild.module.api.Module;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.Setting;
import ru.wild.gui.widget.SettingControlLayout;
import ru.wild.gui.widget.SettingRowRenderer;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

public class ModuleListRenderer extends BlurStateManager {
   public static boolean handle(RoundedRectRenderer var0, int var1, int var2, int var3) {
      float var4 = BlurStateManager.providerClose + 104.735F;
      float var5 = BlurStateManager.presetSave + 34.025F;
      float var6 = 261.5F;
      float var7 = 209.5F;
      float var8 = var4 + 5.0F;
      float var9 = var5 + 5.0F;
      float var10 = var6 - 10.0F;
      float var11 = var7 - 10.0F;
      if (!ModuleSearchPanel.handle(var1, var2, var8, var9, var10, var11)) {
         return false;
      }

      List<Module> var12 = BlurStateManager.timerRender;
      if (BlurStateManager.profileDraw && !BlurStateManager.providerFetch.isEmpty()) {
         String var13 = BlurStateManager.providerFetch.toLowerCase().trim();
         var12 = BlurStateManager.timerRender.stream().filter(var1x -> var1x.displayName.toLowerCase().contains(var13)).collect(Collectors.toList());
      }

      int var35 = 1;
      float var14 = BlurStateManager.handle().prepare();
      float var15 = 0.0F;
      float var16 = 0.0F;

      for (Module var18 : var12) {
         float var19 = 12.0F;
         if (BlurStateManager.scaleSave.contains(var18)) {
            for (Setting var21 : var18.apply()) {
               var19 += SettingControlLayout.handle(var0, var21);
            }

            var19 = Math.max(var19, 20.0F);
         }

         if (var35 % 2 == 0) {
            float var37 = var14 + var16 - 30.0F;
            float var39 = BlurStateManager.providerClose + 238.35F;
            float var40 = BlurStateManager.presetSave + 43.365F + var37;
            float var41 = 121.47F;
            float var42 = 21.325F;
            if (BlurStateManager.scaleSave.contains(var18) && var3 == 0) {
               float var44 = BlurStateManager.presetSave + 64.69F + var37 + 4.0F;
               float var47 = BlurStateManager.providerClose + 238.35F + 9.0F;
               float var50 = 105.47F;
               float var53 = 0.0F;

               for (Setting var59 : var18.apply()) {
                  float var62 = var44 + var53;
                  if (SettingRowRenderer.handle(var0, var59, var47, var62, var50, var1, var2, var3)) {
                     return true;
                  }

                  var53 += SettingControlLayout.handle(var0, var59) + 1.0F;
               }
            }

            if (BlurStateManager.scaleSave.contains(var18)) {
               var16 += var19;
            }

            if (ModuleSearchPanel.handle(var1, var2, var39, var40, var41, var42) && var3 == 0) {
               var18.toggle();
            }

            if (ModuleSearchPanel.handle(var1, var2, var39, var40, var41, var42) && var3 == 1 && !var18.apply().isEmpty()) {
               if (BlurStateManager.scaleSave.contains(var18)) {
                  BlurStateManager.scaleSave.remove(var18);
                  BlurStateManager.handle(var18).handle(0.0, 0.6F, EasingFunctions.handler);
                  BlurStateManager.process(var18).handle(0.0, 0.16F, EasingFunctions.pending);
                  if (BlurStateManager.animationDraw != null && var18.apply().contains(BlurStateManager.animationDraw)) {
                     BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
                     BlurStateManager.animationDraw = null;
                     BlurStateManager.pointEncode = 0.0F;
                     BlurStateManager.animator = 0.0F;
                  }
               } else {
                  BlurStateManager.scaleSave.add(var18);
                  BlurStateManager.process(var18).handle(1.0, 0.16F, EasingFunctions.pending);
                  BlurStateManager.handle(var18).handle(1.0, 0.6F, EasingFunctions.handler);
               }
            }

            if (ModuleSearchPanel.handle(var1, var2, var39, var40, var41, var42) && var3 == 2) {
               if (var18.bindingActive) {
                  var18.bindingActive = false;
                  BlurStateManager.matrixBlend = null;
                  BlurStateManager.compute(var18).handle(0.0, 0.2F, EasingFunctions.pending);
               } else {
                  if (BlurStateManager.matrixBlend != null) {
                     BlurStateManager.matrixBlend.bindingActive = false;
                     BlurStateManager.compute(BlurStateManager.matrixBlend).handle(0.0, 0.2F, EasingFunctions.pending);
                  }

                  BlurStateManager.matrixBlend = var18;
                  var18.bindingActive = true;
                  BlurStateManager.compute(var18).handle(1.0, 0.2F, EasingFunctions.pending);
               }

               return true;
            }

            if (var18.bindingActive || var18.keyCode != -1) {
               float var45 = BlurStateManager.providerClose + 247.895F;
               float var48 = BlurStateManager.presetSave + 49.555F + var37;
               float var51 = RoundedRectRenderer.handle(FontRegistry.instance, var18.displayName, 14.0F).instance;
               float var54 = var45 + var51 + 4.0F;
               float var57 = var48 - 1.0F;
               String var60 = var18.bindingActive ? "..." : KeycodeNames.handle(var18.keyCode);
               float var63 = RoundedRectRenderer.handle(FontRegistry.instance, var60, 12.0F).instance;
               float var64 = 16.0F;
               float var65 = Math.max(var64, var63 + 8.0F);
               if (ModuleSearchPanel.handle(var1, var2, var54, var57, var65, 16.0F)) {
                  if (var3 == 2) {
                     if (var18.bindingActive) {
                        var18.bindingActive = false;
                        BlurStateManager.matrixBlend = null;
                        BlurStateManager.compute(var18).handle(0.0, 0.2F, EasingFunctions.pending);
                     } else {
                        if (BlurStateManager.matrixBlend != null) {
                           BlurStateManager.matrixBlend.bindingActive = false;
                           BlurStateManager.compute(BlurStateManager.matrixBlend).handle(0.0, 0.2F, EasingFunctions.pending);
                        }

                        BlurStateManager.matrixBlend = var18;
                        var18.bindingActive = true;
                        BlurStateManager.compute(var18).handle(1.0, 0.2F, EasingFunctions.pending);
                     }

                     return true;
                  }

                  if (var18.bindingActive && var3 >= 0 && var3 <= 8) {
                     int var66 = -100 - var3;
                     var18.keyCode = var66;
                     var18.bindingActive = false;
                     BlurStateManager.matrixBlend = null;
                     BlurStateManager.compute(var18).handle(1.0, 0.2F, EasingFunctions.pending);
                     return true;
                  }
               }
            }
         } else {
            float var36 = var14 + var15;
            float var38 = BlurStateManager.providerClose + 111.885F;
            float var22 = BlurStateManager.presetSave + 43.365F + var36;
            float var23 = 121.47F;
            float var24 = 21.325F;
            if (BlurStateManager.scaleSave.contains(var18) && var3 == 0) {
               float var25 = BlurStateManager.presetSave + 64.69F + var36 + 4.0F;
               float var26 = BlurStateManager.providerClose + 111.885F + 9.0F;
               float var27 = 105.47F;
               float var28 = 0.0F;

               for (Setting var30 : var18.apply()) {
                  float var31 = var25 + var28;
                  if (SettingRowRenderer.handle(var0, var30, var26, var31, var27, var1, var2, var3)) {
                     return true;
                  }

                  var28 += SettingControlLayout.handle(var0, var30) + 1.0F;
               }
            }

            if (BlurStateManager.scaleSave.contains(var18)) {
               var15 += var19;
            }

            if (var18.bindingActive || var18.keyCode != -1) {
               float var43 = BlurStateManager.providerClose + 121.425F;
               float var46 = BlurStateManager.presetSave + 49.555F + var36;
               float var49 = RoundedRectRenderer.handle(FontRegistry.instance, var18.displayName, 14.0F).instance;
               float var52 = var43 + var49 + 4.0F;
               float var55 = var46 - 1.0F;
               String var58 = var18.bindingActive ? "..." : KeycodeNames.handle(var18.keyCode);
               float var61 = RoundedRectRenderer.handle(FontRegistry.instance, var58, 12.0F).instance;
               float var32 = 16.0F;
               float var33 = Math.max(var32, var61 + 8.0F);
               if (ModuleSearchPanel.handle(var1, var2, var52, var55, var33, 16.0F)) {
                  if (var3 == 2) {
                     if (var18.bindingActive) {
                        var18.bindingActive = false;
                        BlurStateManager.matrixBlend = null;
                        BlurStateManager.compute(var18).handle(0.0, 0.2F, EasingFunctions.pending);
                     } else {
                        if (BlurStateManager.matrixBlend != null) {
                           BlurStateManager.matrixBlend.bindingActive = false;
                           BlurStateManager.compute(BlurStateManager.matrixBlend).handle(0.0, 0.2F, EasingFunctions.pending);
                        }

                        BlurStateManager.matrixBlend = var18;
                        var18.bindingActive = true;
                        BlurStateManager.compute(var18).handle(1.0, 0.2F, EasingFunctions.pending);
                     }

                     return true;
                  }

                  if (var18.bindingActive && var3 >= 0 && var3 <= 8) {
                     int var34 = -100 - var3;
                     var18.keyCode = var34;
                     var18.bindingActive = false;
                     BlurStateManager.matrixBlend = null;
                     BlurStateManager.compute(var18).handle(1.0, 0.2F, EasingFunctions.pending);
                     return true;
                  }
               }
            }

            if (ModuleSearchPanel.handle(var1, var2, var38, var22, var23, var24) && var3 == 0) {
               var18.toggle();
            }

            if (ModuleSearchPanel.handle(var1, var2, var38, var22, var23, var24) && var3 == 1 && !var18.apply().isEmpty()) {
               if (BlurStateManager.scaleSave.contains(var18)) {
                  BlurStateManager.scaleSave.remove(var18);
                  BlurStateManager.handle(var18).handle(0.0, 0.6F, EasingFunctions.handler);
                  BlurStateManager.process(var18).handle(0.0, 0.16F, EasingFunctions.pending);
                  if (BlurStateManager.animationDraw != null && var18.apply().contains(BlurStateManager.animationDraw)) {
                     BlurStateManager.cache.process(AnimationDirection.BACKWARDS);
                     BlurStateManager.animationDraw = null;
                     BlurStateManager.pointEncode = 0.0F;
                     BlurStateManager.animator = 0.0F;
                  }
               } else {
                  BlurStateManager.scaleSave.add(var18);
                  BlurStateManager.process(var18).handle(1.0, 0.16F, EasingFunctions.pending);
                  BlurStateManager.handle(var18).handle(1.0, 0.6F, EasingFunctions.handler);
               }
            }

            if (ModuleSearchPanel.handle(var1, var2, var38, var22, var23, var24) && var3 == 2) {
               if (var18.bindingActive) {
                  var18.bindingActive = false;
                  BlurStateManager.matrixBlend = null;
                  BlurStateManager.compute(var18).handle(0.0, 1.0, EasingFunctions.pending);
               } else {
                  if (BlurStateManager.matrixBlend != null) {
                     BlurStateManager.matrixBlend.bindingActive = false;
                     BlurStateManager.compute(BlurStateManager.matrixBlend).handle(0.0, 1.0, EasingFunctions.pending);
                  }

                  BlurStateManager.matrixBlend = var18;
                  var18.bindingActive = true;
                  BlurStateManager.compute(var18).handle(1.0, 1.0, EasingFunctions.pending);
               }

               return true;
            }

            var14 += 30.325F;
         }

         var35++;
      }

      return false;
   }

   public static float[] handle(RoundedRectRenderer var0, ColorSetting var1) {
      if (var1 == null) {
         return null;
      }

      int var2 = 1;
      float var3 = BlurStateManager.handle().prepare();
      float var4 = 0.0F;
      float var5 = 0.0F;

      for (Module var7 : BlurStateManager.timerRender) {
         float var8 = 12.0F;
         if (BlurStateManager.scaleSave.contains(var7)) {
            for (Setting var10 : var7.apply()) {
               var8 += SettingControlLayout.handle(var0, var10);
            }

            var8 = Math.max(var8, 20.0F);
         }

         if (var2 % 2 == 0) {
            float var19 = var3 + var5 - 30.0F;
            if (BlurStateManager.scaleSave.contains(var7)) {
               float var21 = BlurStateManager.presetSave + 64.69F + var19 + 4.0F;
               float var22 = BlurStateManager.providerClose + 238.35F + 9.0F;
               float var23 = 111.47F;
               float var24 = 0.0F;

               for (Setting var26 : var7.apply()) {
                  if (var26 == var1) {
                     float var27 = var22 + var23 - 15.0F;
                     float var28 = var21 + var24 - 5.0F;
                     return new float[]{var27, var28};
                  }

                  var24 += SettingControlLayout.handle(var0, var26) + 3.0F;
               }

               var5 += var8;
            }
         } else {
            float var18 = var3 + var4;
            if (BlurStateManager.scaleSave.contains(var7)) {
               float var20 = BlurStateManager.presetSave + 64.69F + var18 + 4.0F;
               float var11 = BlurStateManager.providerClose + 111.885F + 9.0F;
               float var12 = 111.47F;
               float var13 = 0.0F;

               for (Setting var15 : var7.apply()) {
                  if (var15 == var1) {
                     float var16 = var11 + var12 - 15.0F;
                     float var17 = var20 + var13 - 5.0F;
                     return new float[]{var16, var17};
                  }

                  var13 += SettingControlLayout.handle(var0, var15) + 3.0F;
               }

               var4 += var8;
            }

            var3 += 30.325F;
         }

         var2++;
      }

      return null;
   }
}
