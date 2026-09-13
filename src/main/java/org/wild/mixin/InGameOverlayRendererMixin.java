package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.modules.misc.Removals;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
   @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderFireOverlay(MatrixStack var0, VertexConsumerProvider var1, CallbackInfo var2) {
      if (Removals.handle("Огонь")) {
         var2.cancel();
      }
   }

   @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderUnderwaterOverlay(MinecraftClient var0, MatrixStack var1, VertexConsumerProvider var2, CallbackInfo var3) {
      if (Removals.handle("Вода")) {
         var3.cancel();
      }
   }

   @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderInWallOverlay(Sprite var0, MatrixStack var1, VertexConsumerProvider var2, CallbackInfo var3) {
      if (Removals.handle("Стена в глазах")) {
         var3.cancel();
      }
   }

   @Inject(method = "renderFloatingItem", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderFloatingItem(MatrixStack var1, float var2, CallbackInfo var3) {
      if (Removals.handle("Анимация тотема")) {
         var3.cancel();
      }
   }
}
