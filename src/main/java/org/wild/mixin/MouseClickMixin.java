package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MouseClickContextEvent;
import ru.wild.core.EventDispatchBoundary;

@Mixin(Mouse.class)
public class MouseClickMixin {
   @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
   private void handleMenuMouseClick(long var1, int var3, int var4, int var5, CallbackInfo var6) {
      EventDispatchBoundary.handle();
      MinecraftClient var7 = MinecraftClient.getInstance();
      if (!wild$isWindowInputUsable(var7, var1)) {
         var6.cancel();
      } else {
         double[] var8 = new double[1];
         double[] var9 = new double[1];
         GLFW.glfwGetCursorPos(var1, var8, var9);
         MouseButtonEvent var10 = new MouseButtonEvent(var1, var3, var4, var5, var8[0], var9[0], var7.currentScreen != null);
         EventHandlerInvoker.handle(var10);
         if (!var10.handle() && !var10.check()) {
            EventHandlerInvoker.handle(new InputButtonEvent(var1, -100 - var3, 0, var4, var5));
         }

         if (var10.handle()) {
            var6.cancel();
         }
      }
   }

   @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
   private void preventCursorLock(CallbackInfo var1) {
      EventDispatchBoundary.handle();
      MinecraftClient var2 = MinecraftClient.getInstance();
      long var3 = 0L;
      if (var2 != null && var2.getWindow() != null) {
         var3 = var2.getWindow().getHandle();
      }

      if (wild$isWindowInputUsable(var2, var3) && var2.currentScreen == null) {
         MouseClickContextEvent var5 = new MouseClickContextEvent(var2, var3);
         EventHandlerInvoker.handle(var5);
         if (var5.handle()) {
            var1.cancel();
         }
      } else {
         var1.cancel();
      }
   }

   @Unique
   private static boolean wild$isWindowInputUsable(MinecraftClient var0, long var1) {
      if (var0 != null && var0.getWindow() != null && var1 != 0L && var0.isWindowFocused()) {
         Window var3 = var0.getWindow();
         return var1 == var3.getHandle() && !var3.hasZeroWidthOrHeight() && var3.getFramebufferWidth() > 0 && var3.getFramebufferHeight() > 0;
      } else {
         return false;
      }
   }
}
