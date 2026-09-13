package org.wild.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.api.event.CameraClipEvent;
import ru.wild.api.event.CameraRotationEvent;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.modules.player.FreeCamera;
import ru.wild.modules.visuals.Animations;

@Mixin(Camera.class)
public abstract class CameraMixin {
   @Unique
   private CameraRotationEvent rotationEvent;
   @Unique
   private float originalYaw;
   @Unique
   private float originalPitch;
   @Unique
   private boolean disableClip;
   @Unique
   private float freeCameraTickProgress;

   @Shadow
   protected abstract void setRotation(float var1, float var2);

   @Shadow
   protected abstract void moveBy(float var1, float var2, float var3);

   @Shadow
   protected abstract void setPos(Vec3d var1);

   @Inject(method = "update", at = @At("HEAD"))
   private void onUpdateHead(BlockView var1, Entity var2, boolean var3, boolean var4, float var5, CallbackInfo var6) {
      this.freeCameraTickProgress = var5;
      CameraClipEvent var7 = new CameraClipEvent();
      EventHandlerInvoker.handle(var7);
      this.disableClip = var7.handle();
      if (var2 != null) {
         this.originalYaw = var2.getYaw(var5);
         this.originalPitch = var2.getPitch(var5);
         this.rotationEvent = new CameraRotationEvent(this.originalYaw, this.originalPitch, var5);
         EventHandlerInvoker.handle(this.rotationEvent);
      } else {
         this.rotationEvent = null;
      }
   }

   @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
   private void redirectSetRotation(Camera var1, float var2, float var3) {
      boolean var4 = this.rotationEvent != null && (this.rotationEvent.compute() != this.originalYaw || this.rotationEvent.resolve() != this.originalPitch);
      float var5 = var4 ? this.rotationEvent.compute() : this.originalYaw;
      float var6 = var4 ? this.rotationEvent.resolve() : this.originalPitch;
      Animations var7 = this.wild$getAnimations();
      if (var7 != null && var7.enabled && var7.source.process("F5") && var7.drawProfile()) {
         if (var7.attachEvent()) {
            this.setRotation(var5 + var7.readServer(), var7.handle(var6));
            return;
         }

         if (var7.performVector() && this.wild$isInverseRotationCall(var2)) {
            this.setRotation(var5 + var7.readServer(), var7.handle(var6));
            return;
         }
      }

      if (var4) {
         if (this.wild$isInverseRotationCall(var2)) {
            this.setRotation(var5 + 180.0F, -var6);
         } else {
            this.setRotation(var5, var6);
         }
      } else {
         this.setRotation(var2, var3);
      }
   }

   @Inject(method = "update", at = @At("RETURN"))
   private void onUpdateReturn(CallbackInfo var1) {
      Animations var2 = this.wild$getAnimations();
      if (var2 != null && var2.enabled && var2.source.process("F5") && var2.fetchProvider()) {
         float var3 = this.originalYaw + var2.advancePosition();
         float var4 = var2.process(this.originalPitch);
         float var5 = 4.0F * var2.checkFrame();
         this.setRotation(var3, var4);
         if (var5 > 0.001F) {
            this.moveBy(-var5, 0.0F, 0.0F);
         }
      }

      FreeCamera var6 = FreeCamera.refresh();
      if (var6 != null && var6.enabled) {
         Vec3d var7 = var6.handle(this.freeCameraTickProgress);
         if (var7 != null) {
            this.setPos(var7);
         }
      }

      this.rotationEvent = null;
      this.disableClip = false;
   }

   @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), argsOnly = true)
   private float modifyCameraDistance(float var1) {
      Animations var2 = this.wild$getAnimations();
      if (var2 != null && var2.enabled && var2.source.process("F5") && var2.computeResponse()) {
         float var3 = var2.matchVector();
         if (var3 < 1.0F) {
            float var4 = 1.0F - (float)Math.pow(1.0F - Animations.profileDraw, 3.0);
            return var1 * var4;
         }
      }

      return var1;
   }

   @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
   private void onClipToSpace(float var1, CallbackInfoReturnable<Float> var2) {
      if (this.disableClip) {
         var2.setReturnValue(var1);
      }
   }

   @Unique
   private Animations wild$getAnimations() {
      if (!WildClient.prepare()) {
         return null;
      } else {
         return WildClient.instance != null && WildClient.instance.data != null ? (Animations)WildClient.instance.data.process(Animations.class) : null;
      }
   }

   @Unique
   private boolean wild$isInverseRotationCall(float var1) {
      return Math.abs(MathHelper.wrapDegrees(var1 - this.originalYaw - 180.0F)) < 0.5F;
   }
}
