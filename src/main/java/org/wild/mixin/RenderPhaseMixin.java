package org.wild.mixin;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPhase.class)
public abstract class RenderPhaseMixin {
   @Inject(method = "startDrawing", at = @At("HEAD"), cancellable = true)
   private void preventTargetStart(CallbackInfo var1) {
   }

   @Inject(method = "endDrawing", at = @At("HEAD"), cancellable = true)
   private void preventTargetEnd(CallbackInfo var1) {
   }
}
