package ru.wild.render.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.render.RoundedRectRenderer;

public final class HoloZoomShader {
   private static final HoloZoomShader instance = new HoloZoomShader();
   private static final String data = "assets/wild/shaders/blur/blur_fullscreen.vert";
   private static final String context = "assets/wild/shaders/foundry/grid.frag";
   private static final String config = "assets/wild/shaders/foundry/grid_composite.frag";
   private static final float state = 1.0F;
   private static final float cache = 310.0F;
   private static final float output = 34.0F;
   private static final float current = 92.0F;
   private static final float active = 18.0F;
   private final OffscreenRenderTarget mode = new OffscreenRenderTarget();
   private ShaderBuildReporter selection;
   private ShaderBuildReporter enabled;
   private int renderer;
   private int handler;
   private int animationDraw = -1;
   private int pointEncode = -1;
   private int animator = -1;
   private int source = -1;
   private int target = -1;
   private int pending = -1;
   private int previous = -1;
   private int latest = -1;
   private int summary = -1;
   private int matrixBlend = -1;
   private int vectorMatch = -1;
   private int itemProject = -1;
   private int responseCompute = -1;
   private int providerFetch = -1;
   private int profileDraw = -1;
   private boolean vectorPerform;
   private boolean eventAttach;
   private boolean serverRead;
   private long positionAdvance;
   private float frameCheck;
   private float moduleCollect;
   private float providerClose;
   private float presetSave;
   private float windowConvert;
   private float presetWrite;

   private HoloZoomShader() {
   }

   public static HoloZoomShader handle() {
      return instance;
   }
   public boolean handle(
      RoundedRectRenderer var1,
      int var2,
      int var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      ThemeColors var11,
      boolean var12
   ) {
      if (this.eventAttach || var1 == null || var2 <= 0 || var3 <= 0 || var9 <= 0.001F) {
         return false;
      }

      if (!this.process()) {
         return false;
      }

      this.handle(var2, var3, var7, var8);
      var1.compute();
      int var13 = var11 == null ? -29969 : var11.save();
      int var14 = var11 == null ? -8128257 : var11.submit();
      OpenGlStateSnapshot.NetworkState var15 = OpenGlStateSnapshot.handle();
      boolean var21 = false /* VF: Semaphore variable */;

      boolean var24;
      label85: {
         label84: {
            boolean var17;
            try {
               var21 = true;
               this.mode.handle(var2, var3);
               if (!this.mode.apply()) {
                  var24 = false;
                  var21 = false;
                  break label85;
               }

               this.mode.handle();
               GL11.glDrawBuffer(36064);
               GL11.glViewport(0, 0, var2, var3);
               GL11.glDisable(3089);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glDisable(3042);
               GL11.glDepthMask(false);
               GL11.glColorMask(true, true, true, true);
               GL11.glDisable(36281);
               GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               GL11.glClear(16384);
               this.selection.handle();
               handle(this.animationDraw, var2, var3);
               handle(this.pointEncode, var4, var5);
               handle(this.animator, var6);
               handle(this.source, var7, var8);
               handle(this.target, this.frameCheck, this.moduleCollect);
               handle(this.pending, this.providerClose, this.presetSave);
               handle(this.previous, this.windowConvert);
               handle(this.latest, var10);
               handle(this.summary, var9);
               handle(this.matrixBlend, handle(var13), process(var13), compute(var13));
               handle(this.vectorMatch, handle(var14), process(var14), compute(var14));
               handle(this.itemProject, var12 ? 1.0F : 0.0F);
               GL30.glBindVertexArray(this.renderer);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindFramebuffer(36160, var15.instance);
               GL11.glDrawBuffer(var15.context);
               GL11.glViewport(0, 0, var2, var3);
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               this.enabled.handle();
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, this.mode.compute());
               handle(this.responseCompute, 0);
               handle(this.providerFetch, var2, var3);
               handle(this.profileDraw, 1.0F);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindVertexArray(0);
               var24 = true;
               var21 = false;
               break label84;
            } catch (Throwable var22) {
               this.eventAttach = true;
               var17 = false;
               var21 = false;
            } finally {
               if (var21) {
                  GL13.glActiveTexture(33984);
                  GL11.glBindTexture(3553, 0);
                  GL20.glUseProgram(0);
                  GL30.glBindVertexArray(0);
                  OpenGlStateSnapshot.compute(var15);
               }
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, 0);
            GL20.glUseProgram(0);
            GL30.glBindVertexArray(0);
            OpenGlStateSnapshot.compute(var15);
            return var17;
         }

         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         GL20.glUseProgram(0);
         GL30.glBindVertexArray(0);
         OpenGlStateSnapshot.compute(var15);
         return var24;
      }

      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, 0);
      GL20.glUseProgram(0);
      GL30.glBindVertexArray(0);
      OpenGlStateSnapshot.compute(var15);
      return var24;
   }

   private void handle(int var1, int var2, float var3, float var4) {
      long var5 = System.nanoTime();
      float var7 = this.positionAdvance == 0L ? 0.016666668F : (float)(var5 - this.positionAdvance) / 1.0E9F;
      this.positionAdvance = var5;
      if (!Float.isFinite(var7) || var7 <= 0.0F) {
         var7 = 0.016666668F;
      }

      var7 = Math.max(0.001F, Math.min(0.05F, var7));
      if (!this.serverRead) {
         this.frameCheck = var3;
         this.moduleCollect = var4;
         this.providerClose = 0.0F;
         this.presetSave = 0.0F;
         this.windowConvert = 0.0F;
         this.presetWrite = 0.0F;
         this.serverRead = true;
      } else {
         float var8 = ((var3 - this.frameCheck) * 310.0F - this.providerClose * 34.0F) / 1.0F;
         float var9 = ((var4 - this.moduleCollect) * 310.0F - this.presetSave * 34.0F) / 1.0F;
         this.providerClose += var8 * var7;
         this.presetSave += var9 * var7;
         this.frameCheck = this.frameCheck + this.providerClose * var7;
         this.moduleCollect = this.moduleCollect + this.presetSave * var7;
         float var10 = var3 >= 0.0F && var3 <= var1 && var4 >= 0.0F && var4 <= var2 ? 1.0F : 0.0F;
         float var11 = (float)Math.sqrt(this.providerClose * this.providerClose + this.presetSave * this.presetSave);
         float var12 = var10 * handle(0.58F + var11 * 0.0018F, 0.0F, 1.0F);
         float var13 = ((var12 - this.windowConvert) * 92.0F - this.presetWrite * 18.0F) / 1.0F;
         this.presetWrite += var13 * var7;
         this.windowConvert = this.windowConvert + this.presetWrite * var7;
         this.windowConvert = handle(this.windowConvert, 0.0F, 1.0F);
      }
   }

   private boolean process() {
      if (!this.vectorPerform) {
         this.vectorPerform = true;

         try {
            this.selection = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/foundry/grid.frag");
            this.enabled = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/foundry/grid_composite.frag");
            this.animationDraw = this.selection.handle("uResolution");
            this.pointEncode = this.selection.handle("uPan");
            this.animator = this.selection.handle("uZoom");
            this.source = this.selection.handle("uMouse");
            this.target = this.selection.handle("uSpringMouse");
            this.pending = this.selection.handle("uMouseVelocity");
            this.previous = this.selection.handle("uMagnetEnergy");
            this.latest = this.selection.handle("uTime");
            this.summary = this.selection.handle("uAlpha");
            this.matrixBlend = this.selection.handle("uAccentTop");
            this.vectorMatch = this.selection.handle("uAccentBottom");
            this.itemProject = this.selection.handle("uLightMode");
            this.responseCompute = this.enabled.handle("uTexture");
            this.providerFetch = this.enabled.handle("uResolution");
            this.profileDraw = this.enabled.handle("uAlpha");
            this.renderer = GL30.glGenVertexArrays();
            this.handler = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.renderer);
            GL15.glBindBuffer(34962, this.handler);
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
            this.eventAttach = true;
            this.selection = null;
            this.enabled = null;
            return false;
         }
      } else {
         return this.selection != null && this.enabled != null && this.renderer != 0;
      }
   }

   private static void handle(int var0, int var1) {
      if (var0 >= 0) {
         GL20.glUniform1i(var0, var1);
      }
   }

   private static void handle(int var0, float var1) {
      if (var0 >= 0) {
         GL20.glUniform1f(var0, var1);
      }
   }

   private static void handle(int var0, float var1, float var2) {
      if (var0 >= 0) {
         GL20.glUniform2f(var0, var1, var2);
      }
   }

   private static void handle(int var0, float var1, float var2, float var3) {
      if (var0 >= 0) {
         GL20.glUniform3f(var0, var1, var2, var3);
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

   private static float handle(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : Math.min(var0, var2);
   }
}
