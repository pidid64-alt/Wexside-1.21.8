package ru.wild.gui.screen;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.util.math.MatrixStack;
import org.wild.module.api.Module;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.Setting;
import ru.wild.gui.widget.ColorControlRenderer;
import ru.wild.gui.widget.SettingControlLayout;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.Easings;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.player.SyntheticKeyState;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.InputNames;
import ru.wild.util.text.KeycodeNames;

public class ModuleSearchPanel extends BlurStateManager {
   private static final char[] actionRead = new char[65535];

   public static void handle(RoundedRectRenderer var0, MatrixStack var1, int var2, int var3, float var4) {
      if (BlurStateManager.profileDraw) {
         SyntheticKeyState.handle().handle("Search");
      } else {
         SyntheticKeyState.handle().process("Search");
      }

      if (BlurStateManager.profileDraw) {
         boolean var5 = InputNames.handle(259);
         long var6 = System.currentTimeMillis();
         if (var5) {
            if (!BlurStateManager.eventAttach) {
               BlurStateManager.eventAttach = true;
               BlurStateManager.serverRead = var6;
               BlurStateManager.vectorPerform = var6;
               if (!BlurStateManager.providerFetch.isEmpty()) {
                  BlurStateManager.providerFetch = BlurStateManager.providerFetch.substring(0, BlurStateManager.providerFetch.length() - 1);
               }
            } else if (var6 - BlurStateManager.serverRead > 500L && var6 - BlurStateManager.vectorPerform > 30L) {
               if (!BlurStateManager.providerFetch.isEmpty()) {
                  BlurStateManager.providerFetch = BlurStateManager.providerFetch.substring(0, BlurStateManager.providerFetch.length() - 1);
               }

               BlurStateManager.vectorPerform = var6;
            }
         } else {
            BlurStateManager.eventAttach = false;
            BlurStateManager.serverRead = 0L;
         }
      } else {
         BlurStateManager.eventAttach = false;
         BlurStateManager.serverRead = 0L;
      }

      int var63 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.update(1, 1), (int)(20.4F * var4));
      int var64 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(10.2F * var4));
      int var7 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(255.0F * var4));
      int var8 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(15.3F * var4));
      int var9 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(102.0F * var4));
      int var10 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), (int)(255.0F * var4));
      int var11 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.compute(1, 1), (int)(178.5F * var4));
      Color var12 = RoundedRectRenderer.ColorState.refresh(
         RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(56.0F * var4))
      );
      float var13 = BlurStateManager.providerClose + 104.735F;
      float var14 = BlurStateManager.presetSave + 34.025F;
      float var15 = 261.5F;
      float var16 = 209.5F;
      float var17 = var13 + 5.0F;
      float var18 = var14 + 5.0F;
      float var19 = var15 - 10.0F;
      float var20 = var16 - 10.0F;
      var0.handle(var17, var18, var19, var20, 0.0F, 0.0F, 0.0F, 0.0F);
      List<Module> var21 = BlurStateManager.timerRender;
      if (BlurStateManager.profileDraw && !BlurStateManager.providerFetch.isEmpty()) {
         String var22 = BlurStateManager.providerFetch.toLowerCase().trim();
         String var23 = handle(var22);
         var21 = BlurStateManager.timerRender.stream().filter(var2x -> {
            String var3x = var2x.displayName.toLowerCase();
            return var3x.contains(var22) || !var23.equals(var22) && var3x.contains(var23);
         }).collect(Collectors.toList());
      }

      float var65 = 0.0F;
      float var66 = 0.0F;
      float var24 = 0.0F;
      float var25 = 0.0F;
      float var26 = 0.0F;
      int var27 = 1;

      for (Module var29 : var21) {
         var29.toggleAnimation.handle();
         var29.toggleAnimation.handle(var29.enabled ? 1.0 : 0.0, 0.15F, Easings.pending);
         var29.enableAnimation.process(var29.enabled ? AnimationDirection.FORWARDS : AnimationDirection.BACKWARDS);
         float var30 = var29.toggleAnimation.update();
         float var31 = 12.0F;
         float var32 = 12.0F;
         float var33 = BlurStateManager.handle(var29).update();
         float var34 = BlurStateManager.process(var29).update();
         if (BlurStateManager.scaleSave.contains(var29) || var33 > 0.0F || var34 > 0.0F) {
            for (Setting var36 : var29.apply()) {
               var32 += SettingControlLayout.handle(var0, var36);
            }

            var32 = Math.max(var32, 20.0F);
            var31 = 12.0F + (var32 - 12.0F) * var33;
         }

         if (var27 % 2 == 0) {
            float var75 = var65 + var24 - 30.0F;
            float var78 = 21.325F;
            var78 += var31;
            float var37 = var75 + var78;
            var26 = Math.max(var26, var37);
            var24 += var31;
         } else {
            float var76 = var65 + var66;
            float var80 = 21.325F;
            var80 += var31;
            float var83 = var76 + var80;
            var25 = Math.max(var25, var83);
            var66 += var31;
            var65 += 30.325F;
         }

         var27++;
      }

      float var67 = Math.max(var25, var26);
      float var68 = var67 + 150.0F;
      float var69 = BlurStateManager.providerClose + 104.735F;
      float var70 = BlurStateManager.presetSave + 34.025F;
      boolean var72 = handle(var2, var3, var69 + 5.0F, var70 + 5.0F, var15 - 10.0F, var16 - 10.0F);
      BlurStateManager.handle().resolve(6.0F);
      BlurStateManager.handle().handle(var72);
      BlurStateManager.handle().compute();
      BlurStateManager.handle().handle(NumericTransform.onTick(var68, 260.0F, 9999.0F), var16 - 10.0F);
      float var73 = -0.35F;
      float var74 = -0.7F;
      int var77 = 1;
      float var82 = BlurStateManager.handle().prepare();
      float var84 = 0.0F;
      float var38 = 0.0F;

      for (Module var40 : var21) {
         if (var77 % 2 == 0) {
            float var85 = var40.toggleAnimation.update();
            float var86 = var82 + var38 - 30.0F;
            float var87 = 12.0F;
            float var89 = 12.0F;
            float var91 = BlurStateManager.handle(var40).update();
            float var92 = BlurStateManager.process(var40).update();
            if (BlurStateManager.scaleSave.contains(var40) || var91 > 0.0F) {
               for (Setting var97 : var40.apply()) {
                  var89 += SettingControlLayout.handle(var0, var97) + 0.5F;
               }

               var89 = Math.max(var89, 20.0F);
               var87 = 12.0F * var91 + (var89 - 12.0F) * var91;
            }

            float var95 = var87;
            if (!(var91 > 0.0F) && !(var92 > 0.0F)) {
               var0.handle(BlurStateManager.providerClose + 238.35F, BlurStateManager.presetSave + 43.365F + var86, 121.47F, 21.325F, 6.5F, var63, 0.1F);
               var0.handle(BlurStateManager.providerClose + 238.35F, BlurStateManager.presetSave + 43.365F + var86, 121.47F, 21.325F, 6.5F, var64);
            } else {
               var0.handle(BlurStateManager.providerClose + 238.35F, BlurStateManager.presetSave + 43.365F + var86, 121.47F, 21.325F + var95, 6.5F, var63, 0.1F);
               var0.handle(BlurStateManager.providerClose + 238.35F, BlurStateManager.presetSave + 43.365F + var86, 121.47F, 21.325F + var95, 6.5F, var64);
               if (var92 > 0.01F) {
                  var0.handle(
                     BlurStateManager.providerClose + 238.515F, BlurStateManager.presetSave + 64.69F + var86, 121.47F, 1.0F, PackedColor.compute(var63, var92)
                  );
               }
            }

            float var98 = BlurStateManager.providerClose + 247.895F;
            float var99 = BlurStateManager.presetSave + 49.555F + var86;
            var0.handle(FontRegistry.instance, var98, var99 + 6.6F, 14.0F, var40.displayName, PackedColor.update(var9, var10, var85));
            float var100 = BlurStateManager.compute(var40).update();
            if (var40.bindingActive || var40.keyCode != -1 || var100 > 0.0F) {
               float var102 = 10.0F;
               String var105 = var40.bindingActive ? "..." : (var40.keyCode != -1 ? KeycodeNames.handle(var40.keyCode) : "");
               float var108 = var105.isEmpty() ? 0.0F : RoundedRectRenderer.handle(FontRegistry.instance, var105, 12.0F).instance;
               float var111 = 6.0F;
               float var114 = Math.max(var111, var108 + 6.0F);
               float var117 = RoundedRectRenderer.handle(FontRegistry.instance, var40.displayName, 14.0F).instance;
               float var119 = var98 + var117 + 4.0F;
               float var120 = var99 - 0.35F;
               float var121 = var119;
               float var122 = var114;
               float var123 = var120;
               float var124 = var102;
               var0.handle(var121, var123, var122, var124, 3.0F, PackedColor.compute(var63, var100), 0.1F);
               var0.handle(var121, var123, var122, var124, 3.0F, PackedColor.compute(var8, var100));
               if (!var105.isEmpty()) {
                  var0.handle(
                     FontRegistry.instance,
                     var121 + var122 / 2.0F - var108 / 2.0F - 0.2F,
                     var123 + 2.0F + 5.25F,
                     12.0F,
                     var105,
                     PackedColor.compute(var40.bindingActive ? var7 : var9, var100)
                  );
               }
            }

            var0.handle(
               BlurStateManager.providerClose + 348.415F - 1.5F, BlurStateManager.presetSave + 52.505F + var86 - 1.5F + var73, 6.0F, 6.0F, 3.0F, var63, 0.08F
            );
            var0.handle(BlurStateManager.providerClose + 348.415F - 1.5F, BlurStateManager.presetSave + 52.505F + var86 - 1.5F + var73, 6.0F, 6.0F, 3.0F, var8);
            var0.handle(
               BlurStateManager.providerClose + 349.27F - 0.75F,
               BlurStateManager.presetSave + 53.365F + var86 - 0.78F + var73,
               3.0F,
               3.0F,
               1.5F,
               PackedColor.update(var9, var7, var85)
            );
            var0.handle(
               BlurStateManager.providerClose + 349.27F + 0.7F,
               BlurStateManager.presetSave + 53.365F + var86 + var73,
               0.1F,
               0.1F,
               1.5F,
               2.575F,
               0.1F,
               PackedColor.update(0, var12.getRGB(), var85)
            );
            if (!var40.apply().isEmpty()) {
               var0.handle(
                  FontRegistry.context,
                  BlurStateManager.providerClose + 337.975F,
                  BlurStateManager.presetSave + 52.81F + var86 - 1.5F + var74 + 6.5F + 6.0F - 6.0F * var91,
                  11.0F,
                  "S",
                  PackedColor.update(0, var7, var91)
               );
               var0.handle(
                  FontRegistry.context,
                  BlurStateManager.providerClose + 337.975F,
                  BlurStateManager.presetSave + 52.81F + var86 - 1.5F + var74 + 6.5F + 6.0F * var91,
                  11.0F,
                  "R",
                  PackedColor.update(var9, 0, var91)
               );
            }

            if (var91 > 0.0F || var92 > 0.0F) {
               float var103 = BlurStateManager.presetSave + 64.69F + var86 + 4.0F;
               float var106 = BlurStateManager.providerClose + 238.35F + 9.0F;
               float var109 = 105.47F;
               float var112 = 0.0F;

               for (Setting var118 : var40.apply()) {
                  var112 += SettingControlLayout.handle(
                        var0,
                        var118,
                        var106,
                        var103 + var112,
                        var109,
                        var2,
                        var3,
                        PackedColor.compute(var63, var92),
                        PackedColor.compute(var7, var92),
                        PackedColor.compute(var8, var92),
                        PackedColor.compute(var9, var92),
                        PackedColor.compute(var10, var92),
                        var4 * var92
                     )
                     * var92;
               }

               var38 += var95;
            }
         } else {
            float var41 = var40.toggleAnimation.update();
            float var42 = var82 + var84;
            float var43 = 12.0F;
            float var44 = 12.0F;
            float var45 = BlurStateManager.handle(var40).update();
            float var46 = BlurStateManager.process(var40).update();
            if (BlurStateManager.scaleSave.contains(var40) || var45 > 0.0F) {
               for (Setting var48 : var40.apply()) {
                  var44 += SettingControlLayout.handle(var0, var48) + 0.5F;
               }

               var44 = Math.max(var44, 20.0F);
               var43 = 12.0F * var45 + (var44 - 12.0F) * var45;
            }

            float var93 = var43;
            if (!(var45 > 0.0F) && !(var46 > 0.0F)) {
               var0.handle(BlurStateManager.providerClose + 111.885F, BlurStateManager.presetSave + 43.365F + var42, 121.47F, 21.325F, 6.5F, var63, 0.1F);
               var0.handle(BlurStateManager.providerClose + 111.885F, BlurStateManager.presetSave + 43.365F + var42, 121.47F, 21.325F, 6.5F, var64);
            } else {
               var0.handle(
                  BlurStateManager.providerClose + 111.885F, BlurStateManager.presetSave + 43.365F + var42, 121.47F, 21.325F + var93, 6.5F, var63, 0.1F
               );
               var0.handle(BlurStateManager.providerClose + 111.885F, BlurStateManager.presetSave + 43.365F + var42, 121.47F, 21.325F + var93, 6.5F, var64);
               if (var46 > 0.01F) {
                  var0.handle(
                     BlurStateManager.providerClose + 111.885F, BlurStateManager.presetSave + 64.69F + var42, 121.47F, 1.0F, PackedColor.compute(var63, var46)
                  );
               }
            }

            float var96 = BlurStateManager.providerClose + 121.425F;
            float var49 = BlurStateManager.presetSave + 49.555F + var42;
            var0.handle(FontRegistry.instance, var96, var49 + 6.6F, 14.0F, var40.displayName, PackedColor.update(var9, var10, var41));
            float var50 = BlurStateManager.compute(var40).update();
            if (var40.bindingActive || var40.keyCode != -1 || var50 > 0.0F) {
               float var51 = 10.0F;
               String var52 = var40.bindingActive ? "..." : (var40.keyCode != -1 ? KeycodeNames.handle(var40.keyCode) : "");
               float var53 = var52.isEmpty() ? 0.0F : RoundedRectRenderer.handle(FontRegistry.instance, var52, 12.0F).instance;
               float var54 = 6.0F;
               float var55 = Math.max(var54, var53 + 6.0F);
               float var56 = RoundedRectRenderer.handle(FontRegistry.instance, var40.displayName, 14.0F).instance;
               float var57 = var96 + var56 + 4.0F;
               float var58 = var49 - 0.35F;
               float var59 = var57;
               float var60 = var55;
               float var61 = var58;
               float var62 = var51;
               var0.handle(var59, var61, var60, var62, 3.0F, PackedColor.compute(var63, var50), 0.1F);
               var0.handle(var59, var61, var60, var62, 3.0F, PackedColor.compute(var8, var50));
               if (!var52.isEmpty()) {
                  var0.handle(
                     FontRegistry.instance,
                     var59 + var60 / 2.0F - var53 / 2.0F - 0.2F,
                     var61 + 2.0F + 5.25F,
                     12.0F,
                     var52,
                     PackedColor.compute(var40.bindingActive ? var7 : var9, var50)
                  );
               }
            }

            var0.handle(
               BlurStateManager.providerClose + 221.875F - 1.5F, BlurStateManager.presetSave + 52.505F + var42 - 1.5F + var73, 6.0F, 6.0F, 3.0F, var63, 0.08F
            );
            var0.handle(BlurStateManager.providerClose + 221.875F - 1.5F, BlurStateManager.presetSave + 52.505F + var42 - 1.5F + var73, 6.0F, 6.0F, 3.0F, var8);
            var0.handle(
               BlurStateManager.providerClose + 222.735F - 0.75F,
               BlurStateManager.presetSave + 53.365F + var42 - 0.78F + var73,
               3.0F,
               3.0F,
               1.5F,
               PackedColor.update(var9, var7, var41)
            );
            var0.handle(
               BlurStateManager.providerClose + 222.735F + 0.7F,
               BlurStateManager.presetSave + 53.365F + var42 + var73,
               0.1F,
               0.1F,
               1.5F,
               2.575F,
               0.1F,
               PackedColor.update(0, var12.getRGB(), var41)
            );
            if (!var40.apply().isEmpty() && !var40.apply().isEmpty()) {
               var0.handle(
                  FontRegistry.context,
                  BlurStateManager.providerClose + 211.48F,
                  BlurStateManager.presetSave + 52.81F + var42 - 1.5F + var74 + 6.5F + 6.0F - 6.0F * var45,
                  11.0F,
                  "S",
                  PackedColor.update(0, var7, var45)
               );
               var0.handle(
                  FontRegistry.context,
                  BlurStateManager.providerClose + 211.48F,
                  BlurStateManager.presetSave + 52.81F + var42 - 1.5F + var74 + 6.5F + 6.0F * var45,
                  11.0F,
                  "R",
                  PackedColor.update(var9, 0, var45)
               );
            }

            if (var45 > 0.0F || var46 > 0.0F) {
               float var101 = BlurStateManager.presetSave + 64.69F + var42 + 4.0F;
               float var104 = BlurStateManager.providerClose + 111.885F + 9.0F;
               float var107 = 105.47F;
               float var110 = 0.0F;

               for (Setting var116 : var40.apply()) {
                  var110 += SettingControlLayout.handle(
                        var0,
                        var116,
                        var104,
                        var101 + var110,
                        var107,
                        var2,
                        var3,
                        PackedColor.compute(var63, var46),
                        PackedColor.compute(var7, var46),
                        PackedColor.compute(var8, var46),
                        PackedColor.compute(var9, var46),
                        PackedColor.compute(var10, var46),
                        var4 * var46
                     )
                     * var46;
               }

               var84 += var93;
            }

            var82 += 30.325F;
         }

         var77++;
      }

      var0.apply();
      BlurStateManager.handle()
         .handle(var0, BlurStateManager.providerClose + 104.735F + 261.5F - 5.0F + 1.0F, BlurStateManager.presetSave + 34.025F + 5.0F, 2.0F, 194.5F, var4);
      if (BlurStateManager.animationDraw != null && BlurStateManager.animationDraw instanceof ColorSetting) {
         ColorControlRenderer.handle(
            var0,
            BlurStateManager.animationDraw,
            var2,
            var3,
            PackedColor.compute(var63, BlurStateManager.cache.check()),
            PackedColor.compute(var11, BlurStateManager.cache.check()),
            PackedColor.compute(var9, BlurStateManager.cache.check()),
            var4 * BlurStateManager.cache.check()
         );
      }
   }

   private static String handle(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length());

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         var1.append(var3 < actionRead.length && actionRead[var3] != 0 ? actionRead[var3] : var3);
      }

      return var1.toString();
   }

   public static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var1 >= var3 && var0 < var2 + var4 && var1 < var3 + var5;
   }

   static {
      String var0 = "йцукенгшщзхъфывапролджэячсмитьбю";
      String var1 = "qwertyuiop[]asdfghjkl;'zxcvbnm,.";

      for (int var2 = 0; var2 < var0.length(); var2++) {
         actionRead[var0.charAt(var2)] = var1.charAt(var2);
      }
   }
}
