package ru.wild.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.shader.ShaderBuildReporter;

public final class GravityGridRenderer {
   private static final GravityGridRenderer instance = new GravityGridRenderer();
   private static final int data = 32;
   private static final String context = "assets/wild/shaders/blur/blur_fullscreen.vert";
   private static final String config = "assets/wild/shaders/hud/gravity_grid.frag";
   private final float[] state = new float[128];
   private final float[] cache = new float[32];
   private ShaderBuildReporter output;
   private int current;
   private int active;
   private int mode = -1;
   private int selection = -1;
   private int enabled = -1;
   private int renderer = -1;
   private int handler = -1;
   private int animationDraw = -1;
   private int pointEncode = -1;
   private int animator = -1;
   private int source = -1;
   private boolean target;
   private boolean pending;

   private GravityGridRenderer() {
   }

   public static GravityGridRenderer handle() {
      return instance;
   }
   public void handle(int var1, int var2, ThemeRenderer.CacheEntry[] var3, int var4, String var5, float var6, float var7, float var8, int var9, int var10) {
      if (!this.pending && var1 > 0 && var2 > 0 && !(var8 <= 0.01F)) {
         if (this.process()) {
            int var11 = Math.max(0, Math.min(32, Math.min(var4, var3 == null ? 0 : var3.length)));

            for (int var12 = 0; var12 < 32; var12++) {
               int var13 = var12 * 4;
               if (var12 < var11 && var3[var12] != null) {
                  ThemeRenderer.CacheEntry var14 = var3[var12];
                  boolean var15 = var5 != null && var5.equals(var14.instance);
                  this.state[var13] = var14.data;
                  this.state[var13 + 1] = var14.context;
                  this.state[var13 + 2] = Math.max(1.0F, var14.config);
                  this.state[var13 + 3] = Math.max(var14.cache, var14.output);
                  this.cache[var12] = Math.max(0.0F, var14.state) * (var15 ? 2.25F : 1.0F);
               } else {
                  this.state[var13] = 0.0F;
                  this.state[var13 + 1] = 0.0F;
                  this.state[var13 + 2] = 1.0F;
                  this.state[var13 + 3] = 1.0F;
                  this.cache[var12] = 0.0F;
               }
            }

            OpenGlStateSnapshot.NetworkState var22 = OpenGlStateSnapshot.handle();
            boolean var19 = false /* VF: Semaphore variable */;

            label209: {
               try {
                  var19 = true;
                  GL11.glViewport(0, 0, var1, var2);
                  GL11.glDisable(3089);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  this.output.handle();
                  if (this.mode >= 0) {
                     GL20.glUniform2f(this.mode, var1, var2);
                  }

                  if (this.selection >= 0) {
                     GL20.glUniform1f(this.selection, (float)(System.nanoTime() % 240000000000L) / 1.0E9F);
                  }

                  if (this.enabled >= 0) {
                     GL20.glUniform1f(this.enabled, Math.max(0.0F, Math.min(1.0F, var8)));
                  }

                  if (this.renderer >= 0) {
                     GL20.glUniform2f(this.renderer, var6, var7);
                  }

                  if (this.handler >= 0) {
                     GL20.glUniform1i(this.handler, var11);
                  }

                  if (this.animationDraw >= 0) {
                     GL20.glUniform4fv(this.animationDraw, this.state);
                  }

                  if (this.pointEncode >= 0) {
                     GL20.glUniform1fv(this.pointEncode, this.cache);
                  }

                  if (this.animator >= 0) {
                     GL20.glUniform3f(this.animator, handle(var9), process(var9), compute(var9));
                  }

                  if (this.source >= 0) {
                     GL20.glUniform3f(this.source, handle(var10), process(var10), compute(var10));
                  }

                  GL30.glBindVertexArray(this.current);
                  GL11.glDrawArrays(4, 0, 6);
                  GL30.glBindVertexArray(0);
                  var19 = false;
                  break label209;
               } catch (Throwable var20) {
                  this.pending = true;
                  var19 = false;
               } finally {
                  if (var19) {
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var22);
                  }
               }

               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var22);
               return;
            }

            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(var22);
         }
      }
   }

   private boolean process() {
      if (!this.target) {
         this.target = true;

         try {
            this.output = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/hud/gravity_grid.frag");
            this.mode = this.output.handle("uResolution");
            this.selection = this.output.handle("uTime");
            this.enabled = this.output.handle("uAlpha");
            this.renderer = this.output.handle("uCursor");
            this.handler = this.output.handle("uWellCount");
            this.animationDraw = this.output.handle("uWells[0]");
            this.pointEncode = this.output.handle("uMass[0]");
            this.animator = this.output.handle("uAccentTop");
            this.source = this.output.handle("uAccentBottom");
            this.current = GL30.glGenVertexArrays();
            this.active = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.current);
            GL15.glBindBuffer(34962, this.active);
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
            this.pending = true;
            this.output = null;
            return false;
         }
      } else {
         return this.output != null && this.current != 0;
      }
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
}
