package ru.wild.gui.screen;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.BlurStateManager;
import ru.wild.render.shader.TransitionShader;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.LegacyGuiProjection;
import ru.wild.util.render.RoundedRectDrawHelper;
import ru.wild.util.render.RoundedRectRenderer;

public class ClickGuiBackdropRenderer extends BlurStateManager {
   public static void handle(RoundedRectRenderer var0, DrawContext var1, int var2, int var3) {
      float var4 = (float)BlurStateManager.mode.select();
      int var5 = (int)(255.0F * var4);
      int var6 = (int)(100.0F * var4);
      int var7 = (int)(90.0F * var4);
      float var8 = instance.getWindow().getScaledWidth() / 2.0F;
      float var9 = instance.getWindow().getScaledHeight() - 16 + (15.0F - 15.0F * var4);
      int var10 = BlurStateManager.profileInvoke.length;
      float var11 = 18.0F;
      float var12 = var10 * var11;
      float var13 = var8 - var12 / 2.0F;
      int var14 = RoundedRectRenderer.ColorState.select(
         RoundedRectRenderer.ColorState.resolve(RoundedRectRenderer.ColorState.update(1, 1), 1.0F), (int)(15.299999F * var4)
      );
      int var15 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.compute(1, 1), (int)(178.0F * var4));
      RoundedRectDrawHelper.handle(var0, var13 - 9.0F + 1.0F, var9 - 5.0F, var12 + 9.0F - 1.0F, 21.25F, new Vector4f(6.5F, 6.5F, 0.0F, 0.0F), var15);
      var0.handle(var13 - 9.0F + 1.0F, var9 - 5.0F, var12 + 9.0F - 1.0F, 25.0F, 6.5F, var14, 0.5F);
      float var16 = var13;

      for (ThemePalette var20 : BlurStateManager.profileInvoke) {
         var20.data.process(var20 == BlurStateManager.sourceBuild ? AnimationDirection.FORWARDS : AnimationDirection.BACKWARDS);
         var0.handle(
            var16 + 4.5F,
            var9 + 4.76F + 0.5F,
            0.1F,
            0.1F,
            10.0F,
            6.0F,
            0.1F,
            RoundedRectRenderer.ColorState.process(var20.handle(), (int)(var7 * var20.data.check())).getRGB()
         );
         var0.handle(var16, var9 + 0.76F, 9.25F, 9.25F, 10.0F, RoundedRectRenderer.ColorState.process(var20.handle(), var5).getRGB());
         var16 += var11;
      }
   }

   public static void handle(double var0, double var2, int var4) {
      int var5 = (int)LegacyGuiProjection.handle((float)var0, (float)var2)[0];
      int var6 = (int)LegacyGuiProjection.handle((float)var0, (float)var2)[1];
      if (!handle(var5, var6)) {
         float var7 = instance.getWindow().getScaledWidth() / 2.0F;
         float var8 = instance.getWindow().getScaledHeight() - 16;
         int var9 = BlurStateManager.profileInvoke.length;
         float var10 = 18.0F;
         float var11 = var9 * var10;
         float var12 = var7 - var11 / 2.0F;
         float var13 = var12;

         for (ThemePalette var17 : BlurStateManager.profileInvoke) {
            if (NumericTransform.handle(var5, var6, var13, var8, 16.0F, 16.0F) && var17 != BlurStateManager.sourceBuild) {
               BlurStateManager.state.compute();
               TransitionShader.handle().handle(var5, var6, var17.handle().getRGB(), var17.resolve().getRGB());
               BlurStateManager.sourceBuild = var17;
               BlurStateManager.outputCollapse = var17;
               WildClient.instance.selection.handle(var17);
            }

            var13 += var10;
         }
      }
   }

   private static boolean handle(int var0, int var1) {
      return NumericTransform.handle(
         var0, var1, BlurStateManager.providerClose, BlurStateManager.presetSave, BlurStateManager.windowConvert, BlurStateManager.presetWrite
      );
   }
}
