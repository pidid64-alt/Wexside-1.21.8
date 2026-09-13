package ru.wild.render.shader;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.util.render.RoundedRectRenderer;

public final class FoundryNodeSurfaceShader {
   private static final FoundryNodeSurfaceShader instance = new FoundryNodeSurfaceShader();
   private static final String data = "assets/wild/shaders/foundry/node_surface.vert";
   private static final String context = "assets/wild/shaders/foundry/node_surface.frag";
   private static final int config = 26;
   private static final int state = 6;
   private static final int cache = 104;
   private ShaderBuildReporter output;
   private int current;
   private int active;
   private int mode = -1;
   private int selection = -1;
   private FloatBuffer enabled;
   private boolean renderer;
   private boolean handler;

   private FoundryNodeSurfaceShader() {
   }

   public static FoundryNodeSurfaceShader handle() {
      return instance;
   }
   public boolean handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      ThemeColors var10,
      float var11,
      float var12,
      int var13,
      int var14,
      boolean var15
   ) {
      if (!this.handler && var1 != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && var13 > 0 && var14 > 0) {
         float var16 = handle(var1.render(), 0.0F, 1.0F);
         if (!(var16 <= 0.001F) && this.process()) {
            float[] var17 = var1.select().update();
            float var18 = handle(var17, var2, var3);
            float var19 = process(var17, var2, var3);
            float var20 = handle(var17, var2 + var4, var3);
            float var21 = process(var17, var2 + var4, var3);
            float var22 = handle(var17, var2 + var4, var3 + var5);
            float var23 = process(var17, var2 + var4, var3 + var5);
            float var24 = handle(var17, var2, var3 + var5);
            float var25 = process(var17, var2, var3 + var5);
            float var26 = handle(var18, var20, var22, var24);
            float var27 = handle(var19, var21, var23, var25);
            float var28 = Math.max(1.0F, process(var18, var20, var22, var24) - var26);
            float var29 = Math.max(1.0F, process(var19, var21, var23, var25) - var27);
            float var30 = handle(var17);
            float var31 = Math.max(1.0F, var6 * var30);
            float var32 = Math.max(9.0F, Math.min(34.0F, (15.0F + var8 * 11.0F + var7 * 5.0F) * var30));
            float var33 = var32 * 2.18F + 4.0F;
            float var34 = var26 - var33;
            float var35 = var27 - var33;
            float var36 = var28 + var33 * 2.0F;
            float var37 = var29 + var33 * 2.0F;
            int var38 = var10 == null ? -36966 : var10.save();
            int var39 = var10 == null ? -8462337 : var10.submit();
            int var40 = var15 ? ThemeColors.handle(238, 242, 250, 214) : ThemeColors.handle(7, 9, 14, 218);
            this.enabled.clear();
            this.handle(
               var34,
               var35,
               var34 - var26,
               var35 - var27,
               var28,
               var29,
               var31,
               var32,
               var38,
               var39,
               var40,
               var7,
               var8,
               var9,
               var16,
               var11 - var26,
               var12 - var27
            );
            this.handle(
               var34 + var36,
               var35,
               var34 + var36 - var26,
               var35 - var27,
               var28,
               var29,
               var31,
               var32,
               var38,
               var39,
               var40,
               var7,
               var8,
               var9,
               var16,
               var11 - var26,
               var12 - var27
            );
            this.handle(
               var34 + var36,
               var35 + var37,
               var34 + var36 - var26,
               var35 + var37 - var27,
               var28,
               var29,
               var31,
               var32,
               var38,
               var39,
               var40,
               var7,
               var8,
               var9,
               var16,
               var11 - var26,
               var12 - var27
            );
            this.handle(
               var34,
               var35,
               var34 - var26,
               var35 - var27,
               var28,
               var29,
               var31,
               var32,
               var38,
               var39,
               var40,
               var7,
               var8,
               var9,
               var16,
               var11 - var26,
               var12 - var27
            );
            this.handle(
               var34 + var36,
               var35 + var37,
               var34 + var36 - var26,
               var35 + var37 - var27,
               var28,
               var29,
               var31,
               var32,
               var38,
               var39,
               var40,
               var7,
               var8,
               var9,
               var16,
               var11 - var26,
               var12 - var27
            );
            this.handle(
               var34,
               var35 + var37,
               var34 - var26,
               var35 + var37 - var27,
               var28,
               var29,
               var31,
               var32,
               var38,
               var39,
               var40,
               var7,
               var8,
               var9,
               var16,
               var11 - var26,
               var12 - var27
            );
            this.enabled.flip();
            var1.compute();
            OpenGlStateSnapshot.NetworkState var41 = OpenGlStateSnapshot.handle();
            boolean var47 = false /* VF: Semaphore variable */;

            boolean var42;
            label88: {
               boolean var43;
               try {
                  var47 = true;
                  GL11.glViewport(0, 0, var13, var14);
                  GL11.glDisable(3089);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glDepthMask(false);
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  this.output.handle();
                  if (this.mode >= 0) {
                     GL20.glUniform2f(this.mode, var13, var14);
                  }

                  if (this.selection >= 0) {
                     GL20.glUniform1f(this.selection, ThemeShaderProgramCache.handle().compute());
                  }

                  GL30.glBindVertexArray(this.current);
                  GL15.glBindBuffer(34962, this.active);
                  GL15.glBufferSubData(34962, 0L, this.enabled);
                  GL11.glDrawArrays(4, 0, 6);
                  var42 = true;
                  var47 = false;
                  break label88;
               } catch (Throwable var48) {
                  this.handler = true;
                  var43 = false;
                  var47 = false;
               } finally {
                  if (var47) {
                     GL20.glUseProgram(0);
                     GL30.glBindVertexArray(0);
                     GL15.glBindBuffer(34962, 0);
                     OpenGlStateSnapshot.compute(var41);
                  }
               }

               GL20.glUseProgram(0);
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               OpenGlStateSnapshot.compute(var41);
               return var43;
            }

            GL20.glUseProgram(0);
            GL30.glBindVertexArray(0);
            GL15.glBindBuffer(34962, 0);
            OpenGlStateSnapshot.compute(var41);
            return var42;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean process() {
      if (!this.renderer) {
         this.renderer = true;

         try {
            this.output = ShaderBuildReporter.handle("assets/wild/shaders/foundry/node_surface.vert", "assets/wild/shaders/foundry/node_surface.frag");
            this.mode = this.output.handle("uViewport");
            this.selection = this.output.handle("uTime");
            this.current = GL30.glGenVertexArrays();
            this.active = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.current);
            GL15.glBindBuffer(34962, this.active);
            GL15.glBufferData(34962, 624L, 35048);
            int var1 = 0;
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, 5126, false, 104, var1);
            var1 += 8;
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, 5126, false, 104, var1);
            var1 += 8;
            GL20.glEnableVertexAttribArray(2);
            GL20.glVertexAttribPointer(2, 4, 5126, false, 104, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(3);
            GL20.glVertexAttribPointer(3, 4, 5126, false, 104, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(4);
            GL20.glVertexAttribPointer(4, 4, 5126, false, 104, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(5);
            GL20.glVertexAttribPointer(5, 4, 5126, false, 104, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(6);
            GL20.glVertexAttribPointer(6, 4, 5126, false, 104, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(7);
            GL20.glVertexAttribPointer(7, 2, 5126, false, 104, var1);
            GL15.glBindBuffer(34962, 0);
            GL30.glBindVertexArray(0);
            this.enabled = BufferUtils.createFloatBuffer(156);
            return true;
         } catch (Throwable var2) {
            this.handler = true;
            this.output = null;
            return false;
         }
      } else {
         return this.output != null && this.current != 0;
      }
   }

   private void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      int var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17
   ) {
      this.enabled.put(var1).put(var2);
      this.enabled.put(var3).put(var4);
      this.enabled.put(var5).put(var6).put(var7).put(var8);
      this.handle(var9);
      this.handle(var10);
      this.handle(var11);
      this.enabled.put(handle(var12, 0.0F, 1.0F)).put(handle(var13, 0.0F, 1.0F)).put(handle(var14, 0.0F, 1.0F)).put(var15);
      this.enabled.put(var16).put(var17);
   }

   private void handle(int var1) {
      this.enabled.put(process(var1)).put(compute(var1)).put(resolve(var1)).put(update(var1));
   }

   private static float handle(float[] var0, float var1, float var2) {
      return var0 != null && var0.length >= 9 ? var0[0] * var1 + var0[1] * var2 + var0[2] : var1;
   }

   private static float process(float[] var0, float var1, float var2) {
      return var0 != null && var0.length >= 9 ? var0[3] * var1 + var0[4] * var2 + var0[5] : var2;
   }

   private static float handle(float[] var0) {
      if (var0 != null && var0.length >= 9) {
         float var1 = (float)Math.sqrt(var0[0] * var0[0] + var0[3] * var0[3]);
         float var2 = (float)Math.sqrt(var0[1] * var0[1] + var0[4] * var0[4]);
         return Math.max(0.001F, (var1 + var2) * 0.5F);
      } else {
         return 1.0F;
      }
   }

   private static float handle(float var0, float var1, float var2, float var3) {
      return Math.min(Math.min(var0, var1), Math.min(var2, var3));
   }

   private static float process(float var0, float var1, float var2, float var3) {
      return Math.max(Math.max(var0, var1), Math.max(var2, var3));
   }

   private static float process(int var0) {
      return (var0 >>> 16 & 0xFF) / 255.0F;
   }

   private static float compute(int var0) {
      return (var0 >>> 8 & 0xFF) / 255.0F;
   }

   private static float resolve(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private static float update(int var0) {
      return (var0 >>> 24 & 0xFF) / 255.0F;
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : Math.min(var0, var2);
   }
}
