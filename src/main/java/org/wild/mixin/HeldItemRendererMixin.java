package org.wild.mixin;

import com.google.common.base.MoreObjects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.HeldItemRenderEvent;
import ru.wild.api.event.MutableHandRenderEvent;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.movement.NoSlow;
import ru.wild.modules.player.FreeCamera;
import ru.wild.modules.visuals.Hands;
import ru.wild.modules.visuals.SwingAnimation;
import ru.wild.render.HandFramebufferCapture;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
   @Unique
   private Hand wild$currentHand;
   @Shadow
   private ItemStack mainHand;
   @Shadow
   private ItemStack offHand;
   @Shadow
   private float equipProgressMainHand;
   @Shadow
   private float lastEquipProgressMainHand;
   @Shadow
   private float equipProgressOffHand;
   @Shadow
   private float lastEquipProgressOffHand;

   @Shadow
   protected abstract void renderFirstPersonItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      VertexConsumerProvider var9,
      int var10
   );

   @Inject(
      method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void fullRenderItemOverride(float var1, MatrixStack var2, Immediate var3, ClientPlayerEntity var4, int var5, CallbackInfo var6) {
      if (WildClient.prepare()) {
         var6.cancel();
         if (FreeCamera.render()) {
            HandFramebufferCapture.handle()
               .handle(
                  false,
                  false,
                  MinecraftClient.getInstance().getWindow().getFramebufferWidth(),
                  MinecraftClient.getInstance().getWindow().getFramebufferHeight()
               );
         } else {
            Hands var7 = WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(Hands.class) : null;
            HandFramebufferCapture var8 = HandFramebufferCapture.handle();
            var8.handle(
               var7 != null && var7.handle(Hand.MAIN_HAND),
               var7 != null && var7.handle(Hand.OFF_HAND),
               MinecraftClient.getInstance().getWindow().getFramebufferWidth(),
               MinecraftClient.getInstance().getWindow().getFramebufferHeight()
            );
            float var9 = var4.getHandSwingProgress(var1);
            Hand var10 = (Hand)MoreObjects.firstNonNull(var4.preferredHand, Hand.MAIN_HAND);
            float var11 = var4.getLerpedPitch(var1);
            float var12 = MathHelper.lerp(var1, var4.lastRenderPitch, var4.renderPitch);
            float var13 = MathHelper.lerp(var1, var4.lastRenderYaw, var4.renderYaw);
            MinecraftClient var14 = MinecraftClient.getInstance();
            if (ViewRotationCoordinator.instance) {
               var2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F));
               var2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0.0F));
            } else {
               var2.multiply(RotationAxis.POSITIVE_X.rotationDegrees((var4.getPitch(var1) - var12) * 0.1F));
               var2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((var4.getYaw(var1) - var13) * 0.1F));
            }

            boolean var15 = true;
            boolean var16 = true;
            ItemStack var17 = var4.getMainHandStack();
            ItemStack var18 = NoSlow.handle(var4.getOffHandStack());
            boolean var19 = var17.isOf(Items.BOW) || var18.isOf(Items.BOW);
            boolean var20 = var17.isOf(Items.CROSSBOW) || var18.isOf(Items.CROSSBOW);
            if (var19 || var20) {
               if (var4.isUsingItem()) {
                  ItemStack var21 = var4.getActiveItem();
                  Hand var22 = var4.getActiveHand();
                  if (var21.isOf(Items.BOW) || var21.isOf(Items.CROSSBOW)) {
                     var15 = var22 == Hand.MAIN_HAND;
                     var16 = var22 == Hand.OFF_HAND;
                  }
               } else if (this.isChargedCrossbow(var17)) {
                  var16 = false;
               }
            }

            if (var15) {
               float var34 = var10 == Hand.MAIN_HAND ? var9 : 0.0F;
               float var36 = 1.0F - MathHelper.lerp(var1, this.lastEquipProgressMainHand, this.equipProgressMainHand);
               var2.push();
               HeldItemRenderEvent var23 = new HeldItemRenderEvent(var2, Hand.MAIN_HAND);
               EventHandlerInvoker.handle(var23);
               boolean var24 = var7 != null && var7.handle(Hand.MAIN_HAND);
               Object var25 = var24 ? var8.handle(Hand.MAIN_HAND, var3) : var3;
               this.wild$currentHand = Hand.MAIN_HAND;

               try {
                  this.renderFirstPersonItem(var4, var1, var11, Hand.MAIN_HAND, var34, this.mainHand, var36, var2, (VertexConsumerProvider)var25, var5);
               } finally {
                  this.wild$currentHand = null;
                  if (var24) {
                     var8.handle(Hand.MAIN_HAND);
                  }

                  var2.pop();
               }
            }

            if (var16) {
               float var35 = var10 == Hand.OFF_HAND ? var9 : 0.0F;
               float var37 = 1.0F - MathHelper.lerp(var1, this.lastEquipProgressOffHand, this.equipProgressOffHand);
               var2.push();
               HeldItemRenderEvent var38 = new HeldItemRenderEvent(var2, Hand.OFF_HAND);
               EventHandlerInvoker.handle(var38);
               boolean var39 = var7 != null && var7.handle(Hand.OFF_HAND);
               Object var40 = var39 ? var8.handle(Hand.OFF_HAND, var3) : var3;
               this.wild$currentHand = Hand.OFF_HAND;

               try {
                  this.renderFirstPersonItem(var4, var1, var11, Hand.OFF_HAND, var35, NoSlow.handle(this.offHand), var37, var2, (VertexConsumerProvider)var40, var5);
               } finally {
                  this.wild$currentHand = null;
                  if (var39) {
                     var8.handle(Hand.OFF_HAND);
                  }

                  var2.pop();
               }
            }

            var3.draw();
         }
      }
   }

   @WrapOperation(
      method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V"
      )
   )
   private void wild$renderScaledItem(
      ItemRenderer var1,
      LivingEntity var2,
      ItemStack var3,
      ItemDisplayContext var4,
      MatrixStack var5,
      VertexConsumerProvider var6,
      World var7,
      int var8,
      int var9,
      int var10,
      Operation<Void> var11
   ) {
      if (WildClient.prepare()) {
         VertexConsumerProvider var12 = var6;
         Hands var13 = WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(Hands.class) : null;
         if (this.wild$currentHand != null && var13 != null && var13.handle(this.wild$currentHand)) {
            var12 = HandFramebufferCapture.handle().process(this.wild$currentHand, var12);
         }

         float var14 = SwingAnimation.handle(this.wild$currentHand);
         if (Math.abs(var14 - 1.0F) <= 1.0E-4F) {
            var11.call(new Object[]{var1, var2, var3, var4, var5, var12, var7, var8, var9, var10});
         } else {
            var5.push();
            var5.scale(var14, var14, var14);

            try {
               var11.call(new Object[]{var1, var2, var3, var4, var5, var12, var7, var8, var9, var10});
            } finally {
               var5.pop();
            }
         }
      }
   }

   @WrapOperation(
      method = "renderFirstPersonItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FFLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V",
         ordinal = 2
      )
   )
   private void handAnimationHook(
      HeldItemRenderer var1,
      float var2,
      float var3,
      MatrixStack var4,
      int var5,
      Arm var6,
      Operation<Void> var7,
      @Local(ordinal = 0, argsOnly = true) AbstractClientPlayerEntity var8,
      @Local(ordinal = 0, argsOnly = true) Hand var9
   ) {
      MutableHandRenderEvent var10 = new MutableHandRenderEvent(var4, var9, var2);
      EventHandlerInvoker.handle(var10);
      if (!var10.handle()) {
         var7.call(new Object[]{var1, var2, var3, var4, var5, var6});
      }
   }

   @Unique
   private boolean isChargedCrossbow(ItemStack var1) {
      return var1.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(var1);
   }
}
