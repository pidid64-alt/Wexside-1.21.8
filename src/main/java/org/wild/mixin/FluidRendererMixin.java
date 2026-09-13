package org.wild.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.modules.misc.Removals;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
   @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$hideLiquids(BlockRenderView var1, BlockPos var2, VertexConsumer var3, BlockState var4, FluidState var5, CallbackInfo var6) {
      boolean var7 = Removals.process("Вода (жидкость)") && var5.isIn(FluidTags.WATER);
      boolean var8 = Removals.process("Лава (жидкость)") && var5.isIn(FluidTags.LAVA);
      if (var7 || var8) {
         var6.cancel();
      }
   }
}
