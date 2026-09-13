package ru.wild.gui.screen;

import ru.wild.WildClient;
import ru.wild.api.setting.ColorSetting;
import ru.wild.gui.widget.ColorControlRenderer;
import ru.wild.render.BlurStateManager;

public class ColorSettingInput extends BlurStateManager {
   public static boolean handle(int var0, int var1, int var2) {
      if (BlurStateManager.animationDraw instanceof ColorSetting var3 && (BlurStateManager.pointEncode != 0.0F || BlurStateManager.animator != 0.0F)) {
         float var13 = ColorControlRenderer.handle(BlurStateManager.pointEncode);
         float var5 = BlurStateManager.animator;
         float var6 = ColorControlRenderer.process(var13);
         float var7 = ColorControlRenderer.compute(var5);
         float var8 = ColorControlRenderer.resolve(var13);
         float var9 = ColorControlRenderer.update(var5);
         float var10 = ColorControlRenderer.apply(var5);
         float var11 = ColorControlRenderer.execute(var5);
         float var12 = 148.0F;
         if (var2 == 0 && ModuleSearchPanel.handle(var0, var1, var6, var7, 132.0F, 62.0F)) {
            BlurStateManager.source = true;
            BlurStateManager.target = false;
            BlurStateManager.pending = false;
            handle(var3, var0, var1, var6, var7);
            process();
            return true;
         } else if (var2 == 0 && ModuleSearchPanel.handle(var0, var1, var8, var7, 10.0F, 62.0F)) {
            BlurStateManager.target = true;
            BlurStateManager.source = false;
            BlurStateManager.pending = false;
            handle(var3, var1, var7);
            process();
            return true;
         } else if (var2 == 0 && ModuleSearchPanel.handle(var0, var1, var6, var9, var12, 7.0F)) {
            BlurStateManager.pending = true;
            BlurStateManager.target = false;
            BlurStateManager.source = false;
            handle(var3, var0, var6, var12);
            process();
            return true;
         } else if (var2 == 0 && ModuleSearchPanel.handle(var0, var1, var6, var10, var12, 10.0F)) {
            process(var3, var0, var6, var12);
            process();
            return true;
         } else if ((var2 == 0 || var2 == 1) && ModuleSearchPanel.handle(var0, var1, var6, var11, var12, 10.0F)) {
            handle(var3, var0, var6, var12, var2 == 1);
            process();
            return true;
         } else {
            return ModuleSearchPanel.handle(var0, var1, var13, var5, 160.0F, 119.0F);
         }
      } else {
         return false;
      }
   }

   public static void handle(ColorSetting var0, int var1, int var2, float var3, float var4) {
      float var5 = Math.max(0.0F, Math.min(var1 - var3, 132.0F));
      float var6 = Math.max(0.0F, Math.min(var2 - var4, 62.0F));
      var0.renderer = var5 / 132.0F;
      var0.handler = 1.0F - var6 / 62.0F;
   }

   public static void handle(ColorSetting var0, int var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(var1 - var2, 62.0F));
      var0.handle(var3 / 62.0F * 360.0F);
   }

   public static void handle(ColorSetting var0, int var1, float var2, float var3) {
      var0.process((var1 - var2) / var3);
   }

   private static void process(ColorSetting var0, int var1, float var2, float var3) {
      byte var4 = 5;
      int var5 = Math.max(0, Math.min(var4 - 1, (int)((var1 - var2) / var3 * var4)));
      float[] var6 = new float[]{0.0F, 180.0F, -30.0F, 30.0F, 120.0F};
      var0.handle(var0.update() + var6[var5]);
      if (var0.renderer < 0.05F) {
         var0.renderer = 0.65F;
      }

      if (var0.handler < 0.08F) {
         var0.handler = 0.85F;
      }
   }

   private static void handle(ColorSetting var0, int var1, float var2, float var3, boolean var4) {
      byte var5 = 9;
      int var6 = Math.max(0, Math.min(var5 - 1, (int)((var1 - var2) / var3 * var5)));
      if (var6 == 8) {
         if (!var4) {
            var0.execute();
         }
      } else {
         if (var4) {
            var0.resolve(var6);
         } else {
            var0.compute(var6);
         }
      }
   }

   private static void process() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }
}
