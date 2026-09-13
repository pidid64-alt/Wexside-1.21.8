package org.wild.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.render.FramebufferCapture;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererEntityCaptureMixin {
   @Inject(method = "renderEntity", at = @At("HEAD"))
   private void captureEntity(
      Entity var1, double var2, double var4, double var6, float var8, MatrixStack var9, VertexConsumerProvider var10, CallbackInfo var11
   ) {
      FramebufferCapture var12 = FramebufferCapture.handle();
      if (var12.refresh()) {
         var12.handle(var1, var2, var4, var6, var8, var9);
      }
   }
}
