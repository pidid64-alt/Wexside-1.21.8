package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.MouseMotionEvent;
import ru.wild.api.event.MouseUpdateEvent;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.math.NumericTransform;

@Mixin(Mouse.class)
public abstract class MouseMixin {
   @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
   private void cancelCameraMovement(CallbackInfo var1) {
      EventDispatchBoundary.handle();
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (!isMouseWindowUsable(var2)) {
         var1.cancel();
      } else if (var2.currentScreen == null) {
         MouseUpdateEvent var3 = new MouseUpdateEvent(var2);
         EventHandlerInvoker.handle(var3);
         if (var3.handle()) {
            var1.cancel();
         }
      }
   }

   @Inject(
      method = "updateMouse",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"),
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   private void onLook(double var1, CallbackInfo var3, double var4, double var6, double var8, double var10, double var12, int var14) {
      MinecraftClient var15 = MinecraftClient.getInstance();
      if (!isMouseWindowUsable(var15)) {
         var3.cancel();
      } else {
         if (var15 != null && var15.player != null) {
            MouseMotionEvent var16 = new MouseMotionEvent(var4, var6 * var14);
            EventHandlerInvoker.handle(var16);
            if (!var16.handle()) {
               var15.player.changeLookDirection(var16.instance, var16.data);
            }

            var3.cancel();
         }
      }
   }

   @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
   private void onMouseScroll(long var1, double var3, double var5, CallbackInfo var7) {
      EventDispatchBoundary.handle();
      MinecraftClient var8 = MinecraftClient.getInstance();
      if (isMouseWindowUsable(var8)) {
         if (var8.currentScreen == null) {
            if (PlayerHelper.presetSave && var5 != 0.0) {
               PlayerHelper.windowConvert -= (float)(var5 * 0.075F);
               PlayerHelper.windowConvert = NumericTransform.onTick(PlayerHelper.windowConvert, 0.02F, 2.0F);
               var7.cancel();
            }
         }
      }
   }

   private static boolean isMouseWindowUsable(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null && var0.isWindowFocused()) {
         Window var1 = var0.getWindow();
         return !var1.hasZeroWidthOrHeight() && var1.getFramebufferWidth() > 0 && var1.getFramebufferHeight() > 0;
      } else {
         return false;
      }
   }
}
