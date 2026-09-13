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
import ru.wild.api.event.MouseScrollEvent;
import ru.wild.core.EventDispatchBoundary;

@Mixin(Mouse.class)
public class MouseScrollMixin {
   @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
   private void handleMenuMouseScroll(long var1, double var3, double var5, CallbackInfo var7) {
      EventDispatchBoundary.handle();
      MinecraftClient var8 = MinecraftClient.getInstance();
      if (!wild$isWindowInputUsable(var8, var1)) {
         var7.cancel();
      } else {
         double[] var9 = new double[1];
         double[] var10 = new double[1];
         GLFW.glfwGetCursorPos(var1, var9, var10);
         MouseScrollEvent var11 = new MouseScrollEvent(var1, var3, var5, var9[0], var10[0], var8.currentScreen != null);
         EventHandlerInvoker.handle(var11);
         if (!var11.handle() && !var11.prepare() && Math.abs(var5) > 1.0E-4) {
            int var12 = var5 > 0.0 ? -200 : -201;
            EventHandlerInvoker.handle(new InputButtonEvent(var1, var12, 0, 1, 0));
         }

         if (var11.handle()) {
            var7.cancel();
         }
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
