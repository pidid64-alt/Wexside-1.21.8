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

public final class WildLogoRenderer {
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
   private static int renderer;
   private static int handler;
   private static int animationDraw;
   private static int pointEncode;
   private static int animator;
   private static int source;
   private static int target;
   private static int pending;

   private WildLogoRenderer() {
   }

   public static void handle(
      float var0,
      float var1,
      float var2,
      int var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      float var12,
      boolean var13
   ) {
      handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, false);
   }

   public static void handle(float var0, float var1, float var2, int var3, int var4, float var5, boolean var6) {
      handle(var0, var1, var2, 5, 1.0F, 1.0F, 1.0F, var3, 0, 0, 0, 0, var5, var6, true);
   }

   private static void handle(
      float var0,
      float var1,
      float var2,
      int var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      float var12,
      boolean var13,
      boolean var14
   ) {
      MinecraftClient var15 = MinecraftClient.getInstance();
      if (var15 != null && var15.getWindow() != null && !var15.getWindow().hasZeroWidthOrHeight()) {
         float var16 = Math.max(10.0F, var2 * 0.35F);
         OpenGlStateSnapshot.NetworkState var17 = OpenGlStateSnapshot.handle();

         try {
            handle();
            if (var14) {
               GL11.glViewport(0, 0, var15.getWindow().getFramebufferWidth(), var15.getWindow().getFramebufferHeight());
               GL11.glDisable(3089);
            }

            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(1, 771, 1, 771);
            GL30.glBindVertexArray(context);
            data.handle();
            GL20.glUniform2f(state, var15.getWindow().getFramebufferWidth(), var15.getWindow().getFramebufferHeight());
            GL20.glUniform4f(cache, var0 - var16, var1 - var16, var2 + var16 * 2.0F, var2 + var16 * 2.0F);
            GL20.glUniform4f(output, var0, var1, var2, var2);
            GL20.glUniform3f(current, handle(var7), process(var7), compute(var7));
            GL20.glUniform3f(active, handle(var8), process(var8), compute(var8));
            GL20.glUniform4f(mode, handle(var9), process(var9), compute(var9), resolve(var9));
            GL20.glUniform4f(selection, handle(var10), process(var10), compute(var10), resolve(var10));
            GL20.glUniform4f(enabled, handle(var11), process(var11), compute(var11), resolve(var11));
            GL20.glUniform1f(renderer, (float)(System.currentTimeMillis() % 1000000L) * 0.001F);
            GL20.glUniform1f(handler, var12);
            GL20.glUniform1f(animationDraw, var13 ? 1.0F : 0.0F);
            GL20.glUniform1f(pointEncode, var4);
            GL20.glUniform1f(animator, var5);
            GL20.glUniform1f(source, var6);
            GL20.glUniform1i(target, var3);
            GL20.glUniform1f(pending, var14 ? 1.0F : 0.0F);
            GL11.glDrawArrays(4, 0, 6);
         } finally {
            GL20.glUseProgram(0);
            GL30.glBindVertexArray(0);
            OpenGlStateSnapshot.compute(var17);
         }
      }
   }

   private static void handle() {
      if (data == null) {
         data = ShaderBuildReporter.handle("assets/wild/shaders/hud/wild_logo.vert", "assets/wild/shaders/clickgui/sidebar_nav.frag");
         state = data.handle("uViewport");
         cache = data.handle("uDrawRect");
         output = data.handle("uBoxRect");
         current = data.handle("uAccentTop");
         active = data.handle("uAccentBottom");
         mode = data.handle("uMuted");
         selection = data.handle("uFill");
         enabled = data.handle("uOutline");
         renderer = data.handle("uTime");
         handler = data.handle("uAlpha");
         animationDraw = data.handle("uLightMode");
         pointEncode = data.handle("uHover");
         animator = data.handle("uActive");
         source = data.handle("uPop");
         target = data.handle("uIcon");
         pending = data.handle("uIconOnly");
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

   private static float resolve(int var0) {
      return (var0 >>> 24 & 0xFF) / 255.0F;
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
