package ru.wild.core;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.shader.ShaderBuildReporter;
import ru.wild.util.render.RoundedRectRenderer;

public final class HudFerrofluidRenderer {
   private static final HudFerrofluidRenderer instance = new HudFerrofluidRenderer();
   private static final String data = "assets/wild/shaders/blur/blur_fullscreen.vert";
   private static final String context = "assets/wild/shaders/hud/hud_ferrofluid_surface.frag";
   private ShaderBuildReporter config;
   private int state;
   private int cache;
   private int output = -1;
   private int current = -1;
   private int active = -1;
   private int mode = -1;
   private int selection = -1;
   private int enabled = -1;
   private int renderer = -1;
   private int handler = -1;
   private int animationDraw = -1;
   private int pointEncode = -1;
   private int animator = -1;
   private int source = -1;
   private int target = -1;
   private int pending = -1;
   private int previous = -1;
   private boolean latest;
   private boolean summary;

   private HudFerrofluidRenderer() {
   }

   public static boolean handle(
      RoundedRectRenderer var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      boolean var7,
      int var8,
      int var9,
      int var10,
      int var11,
      boolean var12,
      boolean var13,
      float var14
   ) {
      return instance.process(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   private boolean process(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      boolean var8,
      int var9,
      int var10,
      int var11,
      int var12,
      boolean var13,
      boolean var14,
      float var15
   ) {
      MinecraftClient var16 = MinecraftClient.getInstance();
      if (!this.summary && var1 != null && var16 != null && var16.getWindow() != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && !(var7 <= 0.001F)) {
         int var17 = var16.getWindow().getFramebufferWidth();
         int var18 = var16.getWindow().getFramebufferHeight();
         if (var17 > 1 && var18 > 1 && this.handle()) {
            var1.compute();
            float var19 = Math.max(18.0F, var6 * 2.8F);
            float var20 = ThemeRenderer.handle().execute();
            float var21 = ThemeRenderer.handle().prepare();
            OpenGlStateSnapshot.NetworkState var22 = OpenGlStateSnapshot.handle();

            try {
               GL11.glViewport(0, 0, var17, var18);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glDisable(3089);
               GL11.glDepthMask(false);
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               GL11.glDisable(36281);
               this.config.handle();
               if (this.output >= 0) {
                  GL20.glUniform2f(this.output, var17, var18);
               }

               if (this.current >= 0) {
                  GL20.glUniform1f(this.current, (float)(System.nanoTime() % 720000000000L) / 1.0E9F);
               }

               if (this.active >= 0) {
                  GL20.glUniform4f(this.active, var2 - var19, var3 - var19, var4 + var19 * 2.0F, var5 + var19 * 2.0F);
               }

               if (this.mode >= 0) {
                  GL20.glUniform4f(this.mode, var2, var3, var4, var5);
               }

               if (this.selection >= 0) {
                  GL20.glUniform1f(this.selection, Math.max(0.0F, var6));
               }

               if (this.enabled >= 0) {
                  GL20.glUniform1f(this.enabled, handle(var7));
               }

               if (this.renderer >= 0) {
                  GL20.glUniform1f(this.renderer, var8 ? 1.0F : 0.0F);
               }

               if (this.handler >= 0) {
                  process(this.handler, var9);
               }

               if (this.animationDraw >= 0) {
                  process(this.animationDraw, var10);
               }

               if (this.pointEncode >= 0) {
                  handle(this.pointEncode, var11);
               }

               if (this.animator >= 0) {
                  handle(this.animator, var12);
               }

               if (this.source >= 0) {
                  GL20.glUniform2f(this.source, var20, var21);
               }

               if (this.target >= 0) {
                  GL20.glUniform1f(this.target, var13 ? 1.0F : 0.0F);
               }

               if (this.pending >= 0) {
                  GL20.glUniform1f(this.pending, var14 ? 1.0F : 0.0F);
               }

               if (this.previous >= 0) {
                  GL20.glUniform1f(this.previous, handle(var15));
               }

               GL30.glBindVertexArray(this.state);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindVertexArray(0);
               return true;
            } catch (Throwable var28) {
               this.summary = true;
               return false;
            } finally {
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var22);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean handle() {
      if (!this.latest) {
         this.latest = true;

         try {
            this.config = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/hud/hud_ferrofluid_surface.frag");
            this.output = this.config.handle("uResolution");
            this.current = this.config.handle("uTime");
            this.active = this.config.handle("uDrawRect");
            this.mode = this.config.handle("uElementRect");
            this.selection = this.config.handle("uRadius");
            this.enabled = this.config.handle("uAlpha");
            this.renderer = this.config.handle("uInset");
            this.handler = this.config.handle("uSurfaceColor");
            this.animationDraw = this.config.handle("uOutlineColor");
            this.pointEncode = this.config.handle("uAccentTop");
            this.animator = this.config.handle("uAccentBottom");
            this.source = this.config.handle("uMouse");
            this.target = this.config.handle("uShadow");
            this.pending = this.config.handle("uOutline");
            this.previous = this.config.handle("uLightMode");
            this.state = GL30.glGenVertexArrays();
            this.cache = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.state);
            GL15.glBindBuffer(34962, this.cache);
            float[] var1 = new float[]{
               -1.0F,
               -1.0F,
               0.0F,
               0.0F,
               1.0F,
               -1.0F,
               1.0F,
               0.0F,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               -1.0F,
               -1.0F,
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               -1.0F,
               1.0F,
               0.0F,
               1.0F
            };
            GL15.glBufferData(34962, var1, 35044);
            byte var2 = 16;
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, 5126, false, var2, 0L);
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, 5126, false, var2, 8L);
            GL15.glBindBuffer(34962, 0);
            GL30.glBindVertexArray(0);
            return true;
         } catch (Throwable var3) {
            this.summary = true;
            this.config = null;
            return false;
         }
      } else {
         return this.config != null && this.state != 0;
      }
   }

   private static void handle(int var0, int var1) {
      GL20.glUniform3f(var0, handle(var1), process(var1), compute(var1));
   }

   private static void process(int var0, int var1) {
      GL20.glUniform4f(var0, handle(var1), process(var1), compute(var1), resolve(var1));
   }

   private static float handle(int var0) {
      return (var0 >>> 16 & 0xFF) / 255.0F;
   }

   private static float process(int var0) {
      return (var0 >>> 8 & 0xFF) / 255.0F;
   }

   private static float compute(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private static float resolve(int var0) {
      return (var0 >>> 24 & 0xFF) / 255.0F;
   }

   private static float handle(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }
}
