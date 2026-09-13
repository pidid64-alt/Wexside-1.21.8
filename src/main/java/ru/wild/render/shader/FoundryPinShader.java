package ru.wild.render.shader;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.util.render.RoundedRectRenderer;

public final class FoundryPinShader {
   private static final FoundryPinShader instance = new FoundryPinShader();
   private static final String data = "assets/wild/shaders/foundry/pin.vert";
   private static final String context = "assets/wild/shaders/foundry/pin.frag";
   private static final int config = 18;
   private static final int state = 6;
   private static final int cache = 96;
   private static final int output = 576;
   private static final int current = 72;
   private ShaderBuildReporter active;
   private int mode;
   private int selection;
   private FloatBuffer enabled;
   private boolean renderer;
   private boolean handler;
   private boolean animationDraw;
   private int pointEncode;
   private int animator;
   private int source;
   private int target = -1;
   private int pending = -1;

   private FoundryPinShader() {
   }

   public static FoundryPinShader handle() {
      return instance;
   }

   public boolean handle(RoundedRectRenderer var1, int var2, int var3) {
      if (this.handler || var1 == null || var2 <= 0 || var3 <= 0) {
         return false;
      }

      if (!this.compute()) {
         return false;
      }

      var1.compute();
      this.pointEncode = var2;
      this.animator = var3;
      this.source = 0;
      this.animationDraw = true;
      this.enabled.clear();
      return true;
   }

   public void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9) {
      if (this.animationDraw && var1 != null && this.source + 6 <= 576 && !(var4 <= 0.001F) && !(var5 <= 0.001F)) {
         float var10 = Math.max(0.0F, Math.min(1.0F, var1.render()));
         if (!(var10 <= 0.001F)) {
            float[] var11 = var1.select().update();
            float var12 = handle(var11, var2, var3);
            float var13 = process(var11, var2, var3);
            float var14 = handle(var11);
            float var15 = Math.max(1.0F, var4 * var14);
            float var16 = Math.max(0.35F, Math.min(var15, var5 * var14));
            float var17 = var15 + 10.0F + var8 * 9.0F;
            float var18 = (var6 >>> 16 & 0xFF) / 255.0F;
            float var19 = (var6 >>> 8 & 0xFF) / 255.0F;
            float var20 = (var6 & 0xFF) / 255.0F;
            float var21 = (var6 >>> 24 & 0xFF) / 255.0F * var10;
            float var22 = (var7 >>> 16 & 0xFF) / 255.0F;
            float var23 = (var7 >>> 8 & 0xFF) / 255.0F;
            float var24 = (var7 & 0xFF) / 255.0F;
            float var25 = (var7 >>> 24 & 0xFF) / 255.0F * var10;
            this.handle(
               var12 - var17, var13 - var17, var12, var13, var15, var16, var18, var19, var20, var21, var22, var23, var24, var25, var8, var9, -1.0F, -1.0F
            );
            this.handle(
               var12 + var17, var13 - var17, var12, var13, var15, var16, var18, var19, var20, var21, var22, var23, var24, var25, var8, var9, 1.0F, -1.0F
            );
            this.handle(
               var12 + var17, var13 + var17, var12, var13, var15, var16, var18, var19, var20, var21, var22, var23, var24, var25, var8, var9, 1.0F, 1.0F
            );
            this.handle(
               var12 - var17, var13 - var17, var12, var13, var15, var16, var18, var19, var20, var21, var22, var23, var24, var25, var8, var9, -1.0F, -1.0F
            );
            this.handle(
               var12 + var17, var13 + var17, var12, var13, var15, var16, var18, var19, var20, var21, var22, var23, var24, var25, var8, var9, 1.0F, 1.0F
            );
            this.handle(
               var12 - var17, var13 + var17, var12, var13, var15, var16, var18, var19, var20, var21, var22, var23, var24, var25, var8, var9, -1.0F, 1.0F
            );
         }
      }
   }
   public void process() {
      if (this.animationDraw) {
         this.animationDraw = false;
         if (this.source > 0) {
            this.enabled.flip();
            OpenGlStateSnapshot.NetworkState var1 = OpenGlStateSnapshot.handle();
            boolean var6 = false /* VF: Semaphore variable */;

            label74: {
               try {
                  var6 = true;
                  GL11.glViewport(0, 0, this.pointEncode, this.animator);
                  GL11.glDisable(3089);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  this.active.handle();
                  if (this.target >= 0) {
                     GL20.glUniform2f(this.target, this.pointEncode, this.animator);
                  }

                  if (this.pending >= 0) {
                     GL20.glUniform1f(this.pending, ThemeShaderProgramCache.handle().compute());
                  }

                  GL30.glBindVertexArray(this.mode);
                  GL15.glBindBuffer(34962, this.selection);
                  GL15.glBufferSubData(34962, 0L, this.enabled);
                  GL11.glDrawArrays(4, 0, this.source);
                  var6 = false;
                  break label74;
               } catch (Throwable var7) {
                  this.handler = true;
                  var6 = false;
               } finally {
                  if (var6) {
                     GL20.glUseProgram(0);
                     GL30.glBindVertexArray(0);
                     GL15.glBindBuffer(34962, 0);
                     OpenGlStateSnapshot.compute(var1);
                  }
               }

               GL20.glUseProgram(0);
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               OpenGlStateSnapshot.compute(var1);
               return;
            }

            GL20.glUseProgram(0);
            GL30.glBindVertexArray(0);
            GL15.glBindBuffer(34962, 0);
            OpenGlStateSnapshot.compute(var1);
         }
      }
   }

   private boolean compute() {
      if (!this.renderer) {
         this.renderer = true;

         try {
            this.active = ShaderBuildReporter.handle("assets/wild/shaders/foundry/pin.vert", "assets/wild/shaders/foundry/pin.frag");
            this.target = this.active.handle("uViewport");
            this.pending = this.active.handle("uTime");
            this.mode = GL30.glGenVertexArrays();
            this.selection = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.mode);
            GL15.glBindBuffer(34962, this.selection);
            GL15.glBufferData(34962, 41472L, 35048);
            int var1 = 0;
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, 5126, false, 72, var1);
            var1 += 8;
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, 5126, false, 72, var1);
            var1 += 8;
            GL20.glEnableVertexAttribArray(2);
            GL20.glVertexAttribPointer(2, 2, 5126, false, 72, var1);
            var1 += 8;
            GL20.glEnableVertexAttribArray(3);
            GL20.glVertexAttribPointer(3, 4, 5126, false, 72, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(4);
            GL20.glVertexAttribPointer(4, 4, 5126, false, 72, var1);
            var1 += 16;
            GL20.glEnableVertexAttribArray(5);
            GL20.glVertexAttribPointer(5, 4, 5126, false, 72, var1);
            GL15.glBindBuffer(34962, 0);
            GL30.glBindVertexArray(0);
            this.enabled = BufferUtils.createFloatBuffer(10368);
            return true;
         } catch (Throwable var2) {
            this.handler = true;
            this.active = null;
            return false;
         }
      } else {
         return this.active != null && this.mode != 0;
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
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      this.enabled.put(var1).put(var2);
      this.enabled.put(var3).put(var4);
      this.enabled.put(var5).put(var6);
      this.enabled.put(var7).put(var8).put(var9).put(var10);
      this.enabled.put(var11).put(var12).put(var13).put(var14);
      this.enabled.put(Math.max(0.0F, Math.min(1.0F, var15))).put(var16).put(var17).put(var18);
      this.source++;
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
}
