package ru.wild.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.shader.ShaderBuildReporter;
import ru.wild.util.render.RoundedRectRenderer;

public final class ModuleListEffectShader {
   private static final ModuleListEffectShader instance = new ModuleListEffectShader();
   private static final int data = 96;
   private static final String context = "assets/wild/shaders/blur/blur_fullscreen.vert";
   private static final String config = "assets/wild/shaders/hud/arraylist_ferrofluid.frag";
   private final float[] state = new float[384];
   private ShaderBuildReporter cache;
   private int output;
   private int current;
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
   private final float[] serverRead = new float[384];

   private ModuleListEffectShader() {
   }

   public static boolean handle(
      RoundedRectRenderer var0,
      int var1,
      int var2,
      float[] var3,
      float[] var4,
      int var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      int var11,
      int var12,
      boolean var13,
      boolean var14,
      boolean var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20,
      float var21,
      float var22
   ) {
      return instance.process(
         var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22
      );
   }

   private boolean process(
      RoundedRectRenderer var1,
      int var2,
      int var3,
      float[] var4,
      float[] var5,
      int var6,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      int var12,
      int var13,
      boolean var14,
      boolean var15,
      boolean var16,
      float var17,
      float var18,
      float var19,
      float var20,
      float var21,
      float var22,
      float var23
   ) {
      if (!this.eventAttach && var1 != null && var2 > 0 && var3 > 0 && var6 > 0 && var6 <= 96 && var4 != null && !(var9 <= 0.001F)) {
         if (!this.handle()) {
            return false;
         }

         float var24 = Float.MAX_VALUE;
         float var25 = Float.MAX_VALUE;
         float var26 = -Float.MAX_VALUE;
         float var27 = -Float.MAX_VALUE;

         for (int var28 = 0; var28 < 96; var28++) {
            int var29 = var28 * 4;
            if (var28 < var6) {
               float var30 = var4[var29];
               float var31 = var4[var29 + 1];
               float var32 = Math.max(0.0F, var4[var29 + 2]);
               float var33 = Math.max(0.0F, var4[var29 + 3]);
               this.state[var29] = var30;
               this.state[var29 + 1] = var31;
               this.state[var29 + 2] = var32;
               this.state[var29 + 3] = var33;
               if (var32 > 0.5F && var33 > 0.5F) {
                  var24 = Math.min(var24, var30);
                  var25 = Math.min(var25, var31);
                  var26 = Math.max(var26, var30 + var32);
                  var27 = Math.max(var27, var31 + var33);
               }

               if (var5 != null && var5.length >= var29 + 4) {
                  this.serverRead[var29] = handle(var5[var29], -220.0F, 220.0F);
                  this.serverRead[var29 + 1] = handle(var5[var29 + 1], -220.0F, 220.0F);
                  this.serverRead[var29 + 2] = handle(var5[var29 + 2], 0.0F, 2.5F);
                  this.serverRead[var29 + 3] = handle(var5[var29 + 3], 0.0F, 1.0F);
               } else {
                  this.serverRead[var29] = 0.0F;
                  this.serverRead[var29 + 1] = 0.0F;
                  this.serverRead[var29 + 2] = 0.0F;
                  this.serverRead[var29 + 3] = 1.0F;
               }
            } else {
               this.state[var29] = 0.0F;
               this.state[var29 + 1] = 0.0F;
               this.state[var29 + 2] = 0.0F;
               this.state[var29 + 3] = 0.0F;
               this.serverRead[var29] = 0.0F;
               this.serverRead[var29 + 1] = 0.0F;
               this.serverRead[var29 + 2] = 0.0F;
               this.serverRead[var29 + 3] = 1.0F;
            }
         }

         if (var24 != Float.MAX_VALUE && var25 != Float.MAX_VALUE && !(var26 <= var24) && !(var27 <= var25)) {
            var1.compute();
            float var39 = Math.max(24.0F, var7 * 4.2F);
            OpenGlStateSnapshot.NetworkState var40 = OpenGlStateSnapshot.handle();

            boolean var42;
            try {
               GL11.glViewport(0, 0, var2, var3);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glDisable(3089);
               GL11.glDepthMask(false);
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               GL11.glDisable(36281);
               this.cache.handle();
               if (this.active >= 0) {
                  GL20.glUniform2f(this.active, var2, var3);
               }

               if (this.mode >= 0) {
                  GL20.glUniform1f(this.mode, (float)(System.nanoTime() % 720000000000L) / 1.0E9F);
               }

               if (this.selection >= 0) {
                  GL20.glUniform4f(this.selection, var24 - var39, var25 - var39, var26 - var24 + var39 * 2.0F, var27 - var25 + var39 * 2.0F);
               }

               if (this.enabled >= 0) {
                  GL20.glUniform1i(this.enabled, var6);
               }

               if (this.renderer >= 0) {
                  GL20.glUniform4fv(this.renderer, this.state);
               }

               if (this.handler >= 0) {
                  GL20.glUniform1f(this.handler, Math.max(1.0F, var7));
               }

               if (this.animationDraw >= 0) {
                  GL20.glUniform1f(this.animationDraw, Math.max(0.0F, Math.min(1.0F, var8)));
               }

               if (this.pointEncode >= 0) {
                  GL20.glUniform1f(this.pointEncode, Math.max(0.0F, Math.min(1.0F, var9)));
               }

               if (this.animator >= 0) {
                  GL20.glUniform4fv(this.animator, this.serverRead);
               }

               if (this.source >= 0) {
                  GL20.glUniform4f(this.source, var18, var19, Math.max(0.0F, Math.min(1.0F, var20)), Math.max(18.0F, var7 * 5.5F));
               }

               if (this.target >= 0) {
                  GL20.glUniform1f(this.target, Math.max(0.0F, Math.min(2.5F, var21)));
               }

               if (this.pending >= 0) {
                  process(this.pending, var10);
               }

               if (this.previous >= 0) {
                  process(this.previous, var11);
               }

               if (this.latest >= 0) {
                  handle(this.latest, var12);
               }

               if (this.summary >= 0) {
                  handle(this.summary, var13);
               }

               if (this.matrixBlend >= 0) {
                  GL20.glUniform1f(this.matrixBlend, var14 ? 1.0F : 0.0F);
               }

               if (this.vectorMatch >= 0) {
                  GL20.glUniform1f(this.vectorMatch, var15 ? 1.0F : 0.0F);
               }

               if (this.itemProject >= 0) {
                  GL20.glUniform1f(this.itemProject, var16 ? 1.0F : 0.0F);
               }

               if (this.responseCompute >= 0) {
                  GL20.glUniform1f(this.responseCompute, Math.max(0.0F, Math.min(1.0F, var17)));
               }

               if (this.providerFetch >= 0) {
                  GL20.glUniform1f(this.providerFetch, Math.max(1.0F, var22));
               }

               if (this.profileDraw >= 0) {
                  GL20.glUniform1f(this.profileDraw, Math.max(0.0F, Math.min(1.0F, var23)));
               }

               GL30.glBindVertexArray(this.output);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindVertexArray(0);
               return true;
            } catch (Throwable var37) {
               this.eventAttach = true;
               var42 = false;
            } finally {
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var40);
            }

            return var42;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean handle() {
      if (!this.vectorPerform) {
         this.vectorPerform = true;

         try {
            this.cache = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/hud/arraylist_ferrofluid.frag");
            this.active = this.cache.handle("uResolution");
            this.mode = this.cache.handle("uTime");
            this.selection = this.cache.handle("uDrawRect");
            this.enabled = this.cache.handle("uRowCount");
            this.renderer = this.cache.handle("uRows[0]");
            this.handler = this.cache.handle("uRadius");
            this.animationDraw = this.cache.handle("uDirection");
            this.pointEncode = this.cache.handle("uAlpha");
            this.animator = this.cache.handle("uMotionRows[0]");
            this.source = this.cache.handle("uPointer");
            this.target = this.cache.handle("uExposure");
            this.pending = this.cache.handle("uSurfaceColor");
            this.previous = this.cache.handle("uOutlineColor");
            this.latest = this.cache.handle("uAccentTop");
            this.summary = this.cache.handle("uAccentBottom");
            this.matrixBlend = this.cache.handle("uOutline");
            this.vectorMatch = this.cache.handle("uGlow");
            this.itemProject = this.cache.handle("uEdgeHighlight");
            this.responseCompute = this.cache.handle("uLightMode");
            this.providerFetch = this.cache.handle("uFluidCohesion");
            this.profileDraw = this.cache.handle("uSoft");
            this.output = GL30.glGenVertexArrays();
            this.current = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.output);
            GL15.glBindBuffer(34962, this.current);
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
            this.cache = null;
            return false;
         }
      } else {
         return this.cache != null && this.output != 0;
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

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }
}
