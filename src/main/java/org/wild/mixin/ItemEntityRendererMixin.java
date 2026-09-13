package org.wild.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.wild.modules.visuals.ItemPhysic;
import ru.wild.render.ItemGroundStateCarrier;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
   @Inject(method = "updateRenderState(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;F)V", at = @At("TAIL"))
   private void wild$updateItemPhysicState(ItemEntity var1, ItemEntityRenderState var2, float var3, CallbackInfo var4) {
      if (var2 instanceof ItemGroundStateCarrier var5) {
         var5.wild$setItemPhysicOnGround(var1.isOnGround());
      }
   }

   @ModifyArgs(
      method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0)
   )
   private void wild$removeGroundBob(Args var1, ItemEntityRenderState var2, MatrixStack var3, VertexConsumerProvider var4, int var5) {
      if (ItemPhysic.handle(var2)) {
         Box var6 = var2.itemRenderState.getModelBoundingBox();
         float var7 = (float)Math.max(0.0, -var6.minY + ItemPhysic.refresh());
         var1.set(1, var7);
      }
   }

   @Inject(
      method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/ItemEntityRenderer;renderStack(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/ItemStackEntityRenderState;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/Box;)V",
         shift = Shift.BEFORE
      )
   )
   private void wild$applyItemPhysicTransform(ItemEntityRenderState var1, MatrixStack var2, VertexConsumerProvider var3, int var4, CallbackInfo var5) {
      if (ItemPhysic.handle(var1) || ItemPhysic.process(var1)) {
         float var6 = ItemEntity.getRotation(var1.age, var1.uniqueOffset);
         var2.multiply(RotationAxis.POSITIVE_Y.rotation(-var6));
         if (ItemPhysic.handle(var1)) {
            var2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ItemPhysic.render()));
         } else {
            var2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ItemPhysic.handle(var1.age)));
         }
      }
   }
}
