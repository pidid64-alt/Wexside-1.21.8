package org.wild.mixin;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.visuals.FullBright;

@Mixin(EntityRenderer.class)
public class EntityRendererLightMixin {
   @Inject(method = "getLight(Lnet/minecraft/entity/Entity;F)I", at = @At("RETURN"), cancellable = true)
   private void wild$torchLight(Entity var1, float var2, CallbackInfoReturnable<Integer> var3) {
      if (FullBright.latest) {
         int var4 = FullBright.handle(var1.getX(), var1.getY() + var1.getHeight() * 0.5, var1.getZ());
         if (var4 > 0) {
            int var5 = var3.getReturnValueI();
            int var6 = LightmapTextureManager.getBlockLightCoordinates(var5);
            if (var4 > var6) {
               int var7 = LightmapTextureManager.getSkyLightCoordinates(var5);
               var3.setReturnValue(LightmapTextureManager.pack(var4, var7));
            }
         }
      }
   }
}
