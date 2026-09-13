package org.wild.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import java.util.function.Consumer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.wild.render.RenderPassMutableLayer;

@Mixin(MultiPhase.class)
public abstract class RenderLayerMultiPhaseMixin implements RenderPassMutableLayer {
   @Unique
   private Consumer<RenderPass> renderPassSetup;

   @Override
   public MultiPhase withRenderPassSetup(Consumer<RenderPass> var1) {
      this.renderPassSetup = var1;
      return (MultiPhase)(Object)this;
   }

   @Inject(
      method = "draw",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"),
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void applyRenderPassSetup(
      BuiltBuffer var1,
      CallbackInfo var2,
      GpuBufferSlice var3,
      BuiltBuffer var4,
      GpuBuffer var5,
      GpuBuffer var6,
      IndexType var7,
      Framebuffer var8,
      GpuTextureView var9,
      GpuTextureView var10,
      RenderPass var11
   ) {
      if (this.renderPassSetup != null) {
         this.renderPassSetup.accept(var11);
      }
   }
}
