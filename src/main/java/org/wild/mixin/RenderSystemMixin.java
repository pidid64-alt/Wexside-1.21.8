package org.wild.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.render.GlErrorNames;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.shader.PrismaticChamsPipeline;
import ru.wild.render.shader.ShaderSamplerHelper;
import ru.wild.render.texture.TextureFilterState;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
   @Inject(method = "flipFrame(JLnet/minecraft/client/util/tracy/TracyFrameCapturer;)V", at = @At("HEAD"))
   private static void flipFrame(long var0, TracyFrameCapturer var2, CallbackInfo var3) {
      WildClient.select();
   }

   @Inject(method = "flipFrame(JLnet/minecraft/client/util/tracy/TracyFrameCapturer;)V", at = @At("TAIL"))
   private static void wild$clearChamsUniforms(long var0, TracyFrameCapturer var2, CallbackInfo var3) {
      PrismaticChamsPipeline.update();
      ShaderSamplerHelper.apply();
      TextureFilterState.compute();
      int var4 = GlErrorNames.resolve();
      if (var4 != 0) {
         RenderDiagnostics.handle().handle("RenderSystem.flipFrame", var4);
      }
   }
}
