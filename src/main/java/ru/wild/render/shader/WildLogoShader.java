package ru.wild.render.shader;

import java.nio.FloatBuffer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;

public final class WildLogoShader {
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

   private WildLogoShader() {
   }

   static void handle(float var0, float var1, float var2, int var3, int var4, float var5, boolean var6) {
      MinecraftClient var7 = MinecraftClient.getInstance();
      if (var7 != null && var7.getWindow() != null && !var7.getWindow().hasZeroWidthOrHeight()) {
         float var8 = Math.max(10.0F, var2 * 0.34F);
         float var9 = var0 - var8;
         float var10 = var1 - var8;
         float var11 = var2 + var8 * 2.0F;
         float var12 = var2 + var8 * 2.0F;
         OpenGlStateSnapshot.NetworkState var13 = OpenGlStateSnapshot.handle();

         try {
            handle();
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            GL30.glBindVertexArray(context);
            data.handle();
            GL20.glUniform2f(state, var7.getWindow().getFramebufferWidth(), var7.getWindow().getFramebufferHeight());
            GL20.glUniform4f(cache, var9, var10, var11, var12);
            GL20.glUniform4f(output, var0, var1, var2, var2);
            int var14 = var6 ? -15066598 : var3;
            int var15 = var6 ? var3 : var4;
            GL20.glUniform3f(current, handle(var14), process(var14), compute(var14));
            GL20.glUniform3f(active, handle(var15), process(var15), compute(var15));
            GL20.glUniform1f(mode, (float)(System.currentTimeMillis() % 1000000L) * 0.001F);
            GL20.glUniform1f(selection, var5);
            GL20.glUniform1f(enabled, var6 ? 1.0F : 0.0F);
            GL11.glDrawArrays(4, 0, 6);
         } finally {
            GL20.glUseProgram(0);
            GL30.glBindVertexArray(0);
            OpenGlStateSnapshot.compute(var13);
         }
      }
   }

   private static void handle() {
      if (data == null) {
         data = ShaderBuildReporter.handle("assets/wild/shaders/hud/wild_logo.vert", "assets/wild/shaders/hud/wild_logo.frag");
         state = data.handle("uViewport");
         cache = data.handle("uDrawRect");
         output = data.handle("uBoxRect");
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
