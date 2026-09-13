package ru.wild.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.GuiRenderContext;
import ru.wild.render.shader.GuiRippleShader;

public final class ClickGuiOverlayHook {
   private static volatile boolean instance = false;

   private ClickGuiOverlayHook() {
   }

   public static void handle() {
      if (!instance) {
         instance = true;
         EventHandlerInvoker.handle(
            new Object() {
               @EventHandler
               public void handle(GuiRenderContext var1) {
                  MinecraftClient var2 = var1.compute();
                  if (var2 != null) {
                     if (var2.currentScreen instanceof ClickGuiScreen) {
                        if (!ClickGuiOverlayHook.handle(var2)) {
                           return;
                        }

                        double[] var3 = new double[1];
                        double[] var4 = new double[1];
                        if (var2.getWindow() != null) {
                           GLFW.glfwGetCursorPos(var2.getWindow().getHandle(), var3, var4);
                           if (var2.mouse != null) {
                              var2.mouse.unlockCursor();
                           }
                        }

                        GuiRippleShader var5 = GuiRippleShader.handle();
                        boolean var6 = var5.handle((Object)var2.currentScreen)
                           && var5.handle(var2.getWindow().getFramebufferWidth(), var2.getWindow().getFramebufferHeight());
                        boolean var9 = false /* VF: Semaphore variable */;

                        try {
                           var9 = true;
                           ClickGuiRenderer.handle(var1.resolve(), null, (int)var3[0], (int)var4[0], var2.getRenderTickCounter().getDynamicDeltaTicks());
                           var1.resolve().compute();
                           var9 = false;
                        } finally {
                           if (var9) {
                              if (var6) {
                                 var5.compute();
                              }
                           }
                        }

                        if (var6) {
                           var5.compute();
                        }
                     }
                  }
               }
            }
         );
      }
   }

   static boolean handle(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         Window var1 = var0.getWindow();
         return !var1.hasZeroWidthOrHeight() && var1.getFramebufferWidth() > 0 && var1.getFramebufferHeight() > 0;
      } else {
         return false;
      }
   }
}
