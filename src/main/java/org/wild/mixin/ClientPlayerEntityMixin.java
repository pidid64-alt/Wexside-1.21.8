package org.wild.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.ItemUseEvent;
import ru.wild.api.event.PlayerMotionEvent;
import ru.wild.api.event.PlayerMoveEvent;
import ru.wild.api.event.SlowdownMultiplierEvent;
import ru.wild.automation.BackgroundClientPlayer;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.core.PlayerContextDispatcher;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.misc.LockSlots;
import ru.wild.modules.player.FreeCamera;
import ru.wild.modules.player.NoPush;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
   @Shadow
   public Input input;

   @Inject(method = "tick", at = @At("HEAD"))
   private void onTickHead(CallbackInfo var1) {
      PlayerContextDispatcher.handle((ClientPlayerEntity)(Object)this);
   }

   @Redirect(method = "tickMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
   private float redirectRenderPitchUpdate(ClientPlayerEntity var1) {
      return ViewRotationCoordinator.instance ? MinecraftClient.getInstance().gameRenderer.getCamera().getPitch() : var1.getPitch();
   }

   @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
   private void wild$lockSlotsDropSelected(boolean var1, CallbackInfoReturnable<Boolean> var2) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            LockSlots var3 = WildClient.instance.data.handle(LockSlots.class);
            if (var3 != null && var3.enabled) {
               ClientPlayerEntity var4 = (ClientPlayerEntity)(Object)this;
               if (var3.handle(var4.getInventory().getSelectedSlot())) {
                  var2.setReturnValue(false);
               }
            }
         }
      }
   }

   @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
   private void preMotion(CallbackInfo var1) {
      ClientPlayerEntity var2 = (ClientPlayerEntity)(Object)this;
      if (!(var2 instanceof BackgroundClientPlayer var3 && HeadlessBotEngine.handle() != var3.handle())) {
         PlayerMoveEvent var4 = new PlayerMoveEvent(var2.getX(), var2.getY(), var2.getZ(), var2.getYaw(), var2.getPitch(), var2.isOnGround());
         PlayerContextDispatcher.handle((ClientPlayerEntity)(Object)this, var4);
         if (var4.handle()) {
            var1.cancel();
         }
      }
   }

   @Inject(
      method = "sendMovementPackets",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"),
      cancellable = true
   )
   private void cancelBackgroundBotVanillaMovement(CallbackInfo var1) {
      ClientPlayerEntity var2 = (ClientPlayerEntity)(Object)this;
      if (var2 instanceof BackgroundClientPlayer var3 && HeadlessBotEngine.handle() != var3.handle()) {
         var1.cancel();
      }
   }

   @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
   private boolean usingItemHook(boolean var1) {
      if (var1) {
         ItemUseEvent var2 = new ItemUseEvent((byte)1);
         EventHandlerInvoker.handle(var2);
         if (var2.handle()) {
            return false;
         }
      }

      return var1;
   }

   @Redirect(method = "tickMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
   private float redirectRenderYawUpdate(ClientPlayerEntity var1) {
      return ViewRotationCoordinator.instance ? MinecraftClient.getInstance().gameRenderer.getCamera().getYaw() : var1.getYaw();
   }

   @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;pushOutOfBlocks(DD)V"))
   private void redirectPushOutOfBlocks(ClientPlayerEntity var1, double var2, double var4) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            FreeCamera var6 = WildClient.instance.data.handle(FreeCamera.class);
            if (var6 != null && var6.enabled) {
               return;
            }

            NoPush var7 = (NoPush)WildClient.instance.data.process(NoPush.class);
            if (var7 != null && var7.enabled && var7.pending.compute()) {
            }
         }
      }
   }

   @Inject(method = "tickMovement", at = @At("HEAD"))
   private void onUpdateWalkingPlayer(CallbackInfo var1) {
      ClientPlayerEntity var2 = (ClientPlayerEntity)(Object)this;
      if (var2 != null) {
         Box var3 = var2.getBoundingBox();
         PlayerMotionEvent var4 = new PlayerMotionEvent(var2.getYaw(), var2.getPitch(), var2.getX(), var2.getY(), var2.getZ(), var2.isOnGround(), var3, null);
         PlayerContextDispatcher.handle((ClientPlayerEntity)(Object)this, var4);
         if (!var4.handle()) {
            if (var4.update() != var2.getYaw() || var4.apply() != var2.getPitch()) {
               var2.setYaw(var4.update());
               var2.setPitch(var4.apply());
            }

            if (var4.execute() != var2.getX() || var4.prepare() != var2.getY() || var4.check() != var2.getZ()) {
               var2.refreshPositionAndAngles(var4.execute(), var4.prepare(), var4.check(), var4.update(), var4.apply());
            }

            if (var4.onTick() != var2.isOnGround()) {
               var2.setOnGround(var4.onTick());
            }
         }
      }
   }

   @Redirect(
      method = "applyMovementSpeedFactors",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;multiply(F)Lnet/minecraft/util/math/Vec2f;", ordinal = 1)
   )
   private Vec2f preventSlowdownMultiply(Vec2f var1, float var2) {
      ClientPlayerEntity var3 = (ClientPlayerEntity)(Object)this;
      if (var2 == 0.2F && var3.isUsingItem() && !var3.hasVehicle()) {
         float var4 = var1.y;
         float var5 = var1.x;
         SlowdownMultiplierEvent var6 = new SlowdownMultiplierEvent(var4, var5);
         EventHandlerInvoker.handle(var6);
         var6.compute();
         if (var6.handle()) {
            return var1;
         }
      }

      return var1.multiply(var2);
   }

   @Unique
   private static float getMovementMultiplier(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0.0F;
      } else {
         return var0 ? 1.0F : -1.0F;
      }
   }
}
