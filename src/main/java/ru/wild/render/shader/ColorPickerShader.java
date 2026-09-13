package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexArrayBuffer;

public final class ColorPickerShader {
   private static final String instance = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final String data = "assets/wild/shaders/colorplus/sb_spectrum.frag";
   private static final String context = "assets/wild/shaders/colorplus/hue_strip.frag";
   private static final String config = "assets/wild/shaders/colorplus/cp_preview.frag";
   private static ShaderFailureCache state;
   private static VertexArrayBuffer cache;
   private static ShaderFailureCache.ShaderState output;
   private static ShaderFailureCache.ShaderState current;
   private static ShaderFailureCache.ShaderState active;
   private static boolean mode;

   private ColorPickerShader() {
   }

   public static synchronized ShaderFailureCache.ShaderState handle() {
      if (mode) {
         return null;
      }

      apply();
      return output;
   }

   public static synchronized ShaderFailureCache.ShaderState process() {
      if (mode) {
         return null;
      }

      apply();
      return current;
   }

   public static synchronized ShaderFailureCache.ShaderState compute() {
      if (mode) {
         return null;
      }

      apply();
      return active;
   }

   public static synchronized VertexArrayBuffer resolve() {
      if (mode) {
         return null;
      }

      apply();
      return cache;
   }

   public static synchronized boolean handle(
      float var0, float var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10, float var11, boolean var12
   ) {
      if (!mode && !(var2 <= 1.0F) && !(var3 <= 1.0F) && !(var11 <= 0.001F) && execute()) {
         apply();
         if (!mode && active != null && cache != null) {
            MinecraftClient var13 = MinecraftClient.getInstance();
            if (var13 != null && var13.getWindow() != null) {
               int var14 = Math.max(1, var13.getWindow().getFramebufferWidth());
               int var15 = Math.max(1, var13.getWindow().getFramebufferHeight());
               OpenGlStateSnapshot.NetworkState var16 = OpenGlStateSnapshot.handle();

               try {
                  GL11.glViewport(0, 0, var14, var15);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glDepthMask(false);
                  GL11.glColorMask(true, true, true, true);
                  GlStateManager._enableBlend();
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  active.handle();
                  active.handle("uViewport", var14, var15);
                  active.handle("uRect", var0, var1, var2, var3);
                  active.handle("u_ElementRect", var0, var1, var2, var3);
                  active.handle("uRectSize", var2, var3);
                  active.handle("uCornerRadius", Math.max(0.0F, var10));
                  active.handle("uCurrentColor", handle(var4), process(var4), compute(var4), resolve(var4));
                  active.handle("uInitialColor", handle(var5), process(var5), compute(var5), resolve(var5));
                  active.handle("uAccentTop", handle(var6), process(var6), compute(var6));
                  active.handle("uAccentBottom", handle(var7), process(var7), compute(var7));
                  active.handle("uMouse", var8 - var0, var9 - var1);
                  active.handle("uTime", (float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
                  active.handle("uAlpha", Math.max(0.0F, Math.min(1.0F, var11)));
                  active.handle("uLive", var12 ? 1.0F : 0.0F);
                  cache.handle();
                  return true;
               } catch (Throwable var22) {
                  update();
                  mode = true;
                  return false;
               } finally {
                  GL20.glUseProgram(0);
                  OpenGlStateSnapshot.compute(var16);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void apply() {
      if (output == null || current == null || active == null || cache == null) {
         try {
            if (state == null) {
               state = new ShaderFailureCache();
            }

            if (cache == null) {
               cache = new VertexArrayBuffer();
            }

            if (output == null) {
               output = state.handle("cp_sb", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/colorplus/sb_spectrum.frag");
            }

            if (current == null) {
               current = state.handle("cp_hue", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/colorplus/hue_strip.frag");
            }

            if (active == null) {
               active = state.handle("cp_preview", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/colorplus/cp_preview.frag");
            }
         } catch (Throwable var1) {
            update();
            mode = true;
         }
      }
   }

   public static synchronized void update() {
      try {
         if (cache != null) {
            try {
               cache.close();
            } catch (Throwable var2) {
            }

            cache = null;
         }

         if (state != null) {
            try {
               state.close();
            } catch (Throwable var1) {
            }

            state = null;
         }

         output = null;
         current = null;
         active = null;
         mode = false;
      } catch (Throwable var3) {
      }
   }

   private static boolean execute() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   private static float handle(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   private static float process(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   private static float compute(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private static float resolve(int var0) {
      return (var0 >>> 24 & 0xFF) / 255.0F;
   }
}
