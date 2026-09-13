package ru.wild.render.shader;

import java.nio.FloatBuffer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;

public final class AvatarShader {
   private static final FloatBuffer instance = BufferUtils.createFloatBuffer(12);
   private static ShaderBuildReporter data;
   private static int context;
   private static int config;
   private static int state;
   private static int cache;
   private static int output;
   private static int current;
   private static int active;
   private static int mode;
   private static int selection;
   private static int enabled;

   private AvatarShader() {
   }
   public static void handle(float var0, float var1, float var2, int var3, int var4, int var5, float var6, boolean var7) {
      MinecraftClient var8 = MinecraftClient.getInstance();
      if (var8 != null && var8.getWindow() != null && !var8.getWindow().hasZeroWidthOrHeight() && var3 > 0) {
         float var9 = var2 * 0.3F;
         OpenGlStateSnapshot.NetworkState var10 = OpenGlStateSnapshot.handle();
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            var13 = true;
            handle();
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(1, 771, 1, 771);
            GL30.glBindVertexArray(context);
            data.handle();
            GL20.glUniform2f(state, var8.getWindow().getFramebufferWidth(), var8.getWindow().getFramebufferHeight());
            GL20.glUniform4f(cache, var0 - var9, var1 - var9, var2 + var9 * 2.0F, var2 + var9 * 2.0F);
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var3);
            GL20.glUniform1i(output, 0);
            GL20.glUniform3f(current, handle(var4), process(var4), compute(var4));
            GL20.glUniform3f(active, handle(var5), process(var5), compute(var5));
            GL20.glUniform1f(mode, (float)(System.currentTimeMillis() % 1000000L) * 0.001F);
            GL20.glUniform1f(selection, Math.max(0.0F, Math.min(1.0F, var6)));
            GL20.glUniform1f(enabled, var7 ? 1.0F : 0.0F);
            GL11.glDrawArrays(4, 0, 6);
            var13 = false;
         } finally {
            if (var13) {
               GL20.glUseProgram(0);
               GL30.glBindVertexArray(0);
               GL11.glBindTexture(3553, 0);
               OpenGlStateSnapshot.compute(var10);
            }
         }

         GL20.glUseProgram(0);
         GL30.glBindVertexArray(0);
         GL11.glBindTexture(3553, 0);
         OpenGlStateSnapshot.compute(var10);
      }
   }

   private static void handle() {
      if (data == null) {
         data = ShaderBuildReporter.handle("assets/wild/shaders/hud/avatar.vert", "assets/wild/shaders/hud/avatar.frag");
         state = data.handle("uViewport");
         cache = data.handle("uDrawRect");
         output = data.handle("uTexture");
         current = data.handle("uAccentTop");
         active = data.handle("uAccentBottom");
         mode = data.handle("uTime");
         selection = data.handle("uAlpha");
         enabled = data.handle("uLightMode");
      }

      if (context == 0) {
         context = GL30.glGenVertexArrays();
         config = GL15.glGenBuffers();
         GL30.glBindVertexArray(context);
         GL15.glBindBuffer(34962, config);
         GL15.glBufferData(34962, instance, 35044);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, 8, 0L);
         GL30.glBindVertexArray(0);
         GL15.glBindBuffer(34962, 0);
      }
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

   static {
      instance.put(0.0F).put(0.0F);
      instance.put(1.0F).put(0.0F);
      instance.put(1.0F).put(1.0F);
      instance.put(0.0F).put(0.0F);
      instance.put(1.0F).put(1.0F);
      instance.put(0.0F).put(1.0F);
      instance.flip();
   }
}
