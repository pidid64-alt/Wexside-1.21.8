package org.wild.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.WorldRenderer.BrightnessGetter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.visuals.FullBright;

@Mixin(WorldRenderer.class)
public class WorldRendererLightmapMixin {
   @Inject(
      method = "getLightmapCoordinates(Lnet/minecraft/client/render/WorldRenderer$BrightnessGetter;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
      at = @At("RETURN"),
      cancellable = true
   )
   private static void wild$torchLight(BrightnessGetter var0, BlockRenderView var1, BlockState var2, BlockPos var3, CallbackInfoReturnable<Integer> var4) {
      if (FullBright.latest) {
         int var5 = FullBright.handle(var3.getX(), var3.getY(), var3.getZ());
         if (var5 > 0) {
            int var6 = var4.getReturnValueI();
            int var7 = LightmapTextureManager.getBlockLightCoordinates(var6);
            if (var5 > var7) {
               int var8 = LightmapTextureManager.getSkyLightCoordinates(var6);
               var4.setReturnValue(LightmapTextureManager.pack(var5, var8));
            }
         }
      }
   }
}
