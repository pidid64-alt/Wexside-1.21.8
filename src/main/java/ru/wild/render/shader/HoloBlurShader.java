package ru.wild.render.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.VertexArrayBuffer;

public final class HoloBlurShader implements AutoCloseable {
   private static final String instance = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final String data = "assets/wild/shaders/clickgui/holo_blur.frag";
   private static volatile HoloBlurShader context;
   private final ShaderFailureCache config = new ShaderFailureCache();
   private VertexArrayBuffer state;
   private ShaderFailureCache.ShaderState cache;
   private int output;
   private int current;
   private int active;
   private long mode;
   private float selection;
   private long enabled;
   private boolean renderer;
   private boolean handler;
   private boolean animationDraw;
   private float pointEncode = 0.5F;
   private float animator = 0.5F;
   private long source;
   private float target;
   private float pending;
   private static final float previous = 0.85F;

   public static HoloBlurShader handle() {
      HoloBlurShader var0 = context;
      if (var0 != null) {
         return var0;
      }

      synchronized (HoloBlurShader.class) {
         if (context == null) {
            context = new HoloBlurShader();
         }

         return context;
      }
   }

   private HoloBlurShader() {
   }
   public void handle(
      int var1,
      int var2,
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
      float var15
   ) {
      if (!this.handler) {
         if (var1 > 1 && var2 > 1) {
            if (!(var5 <= 0.001F)) {
               OpenGlStateSnapshot.NetworkState var16 = OpenGlStateSnapshot.handle();
               boolean var24 = false /* VF: Semaphore variable */;

               label94: {
                  label112: {
                     label113: {
                        try {
                           var24 = true;
                           this.process();
                           if (this.handler) {
                              var24 = false;
                              break label94;
                           }

                           this.process(var1, var2);
                           if (this.output == 0) {
                              var24 = false;
                              break label112;
                           }

                           GL11.glDisable(2929);
                           GL11.glDisable(2884);
                           GL11.glDisable(3089);
                           GL11.glColorMask(true, true, true, true);
                           GL11.glEnable(3042);
                           GL14.glBlendFuncSeparate(770, 771, 1, 771);
                           GL11.glViewport(0, 0, var1, var2);
                           this.cache.handle();
                           this.cache.handle("uViewport", var1, var2);
                           this.cache.handle("uRect", 0.0F, 0.0F, var1, var2);
                           this.cache.handle("uScene", 0);
                           this.cache.handle("uResolution", var1, var2);
                           this.cache.handle("uTime", this.compute());
                           float var17 = process(var1 <= 0 ? 0.0F : var3 / var1);
                           float var18 = process(var2 <= 0 ? 0.0F : var4 / var2);
                           this.cache.handle("uMouse", var17, var18);
                           this.cache.handle("uIntensity", process(var5));
                           this.cache.handle("uBlurMax", Math.max(0.0F, var6));
                           this.cache.handle("uTint", process(var7));
                           this.cache.handle("uMouseInfluence", Math.max(0.0F, var8));
                           this.cache.handle("uClarityRadius", Math.max(0.05F, var9));
                           this.cache.handle("uNoiseScale", Math.max(0.5F, var10));
                           this.cache.handle("uFlowSpeed", var11);
                           this.cache.handle("uContrast", process(var12));
                           this.cache.handle("uVignette", process(var13));
                           this.cache.handle("uBrightness", process(var14));
                           this.cache.handle("uSaturation", process(var15));
                           float var19 = process(var5);
                           float var20 = this.handle(var19, var17, var18);
                           this.cache.handle("uEntry", var20);
                           this.cache.handle("uEntryCenter", this.pointEncode, this.animator);
                           GL13.glActiveTexture(33984);
                           GL11.glBindTexture(3553, this.output);
                           this.state.handle();
                           var24 = false;
                           break label113;
                        } catch (Throwable var25) {
                           this.handler = true;
                           RenderDiagnostics.handle().process("HoloBlurBackground.render", var25);
                           var24 = false;
                        } finally {
                           if (var24) {
                              GL13.glActiveTexture(33984);
                              GL11.glBindTexture(3553, 0);
                              GL20.glUseProgram(0);
                              OpenGlStateSnapshot.compute(var16);
                           }
                        }

                        GL13.glActiveTexture(33984);
                        GL11.glBindTexture(3553, 0);
                        GL20.glUseProgram(0);
                        OpenGlStateSnapshot.compute(var16);
                        return;
                     }

                     GL13.glActiveTexture(33984);
                     GL11.glBindTexture(3553, 0);
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var16);
                     return;
                  }

                  GL13.glActiveTexture(33984);
                  GL11.glBindTexture(3553, 0);
                  GL20.glUseProgram(0);
                  OpenGlStateSnapshot.compute(var16);
                  return;
               }

               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var16);
            }
         }
      }
   }

   private void process(int var1, int var2) {
      if (this.output == 0) {
         this.output = GL11.glGenTextures();
         if (this.output == 0) {
            return;
         }

         GL11.glBindTexture(3553, this.output);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         this.current = 0;
         this.active = 0;
      } else {
         GL11.glBindTexture(3553, this.output);
      }

      if (this.current == var1 && this.active == var2) {
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, var1, var2);
      } else {
         GL11.glCopyTexImage2D(3553, 0, 32856, 0, 0, var1, var2, 0);
         this.current = var1;
         this.active = var2;
      }
   }

   private void process() {
      if (!this.renderer) {
         try {
            this.state = new VertexArrayBuffer();
            this.cache = this.config.handle("clickgui_holo", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/clickgui/holo_blur.frag");
            this.mode = System.nanoTime();
            this.enabled = this.mode;
            this.selection = 0.0F;
            this.renderer = true;
         } catch (Throwable var2) {
            this.handler = true;
            RenderDiagnostics.handle().process("HoloBlurBackground.ensure", var2);
         }
      }
   }

   private float handle(float var1, float var2, float var3) {
      long var4 = System.nanoTime();
      boolean var6 = var1 > this.pending + 1.0E-4F;
      boolean var7 = var1 < this.pending - 1.0E-4F;
      this.pending = var1;
      if (var1 < 0.012F) {
         this.animationDraw = false;
         this.source = 0L;
         this.target = 0.0F;
         return 0.0F;
      }

      if (!this.animationDraw) {
         this.animationDraw = true;
         this.source = var4;
         this.pointEncode = process(var2);
         this.animator = process(var3);
         this.target = 0.0F;
      }

      float var8;
      if (this.source == 0L) {
         this.source = var4;
         var8 = 0.0F;
      } else {
         float var9 = (float)(var4 - this.source) / 1.0E9F;
         var8 = process(var9 / 0.85F);
      }

      float var11 = 1.0F - (1.0F - var8) * (1.0F - var8) * (1.0F - var8);
      float var10 = Math.min(var11, handle(var1));
      if (var7) {
         var10 = Math.min(var10, handle(var1));
      }

      if (var6 && var10 > this.target) {
         this.target = var10;
      } else {
         this.target = var10;
      }

      return process(this.target);
   }

   private static float handle(float var0) {
      float var1 = process(var0 / 0.6F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private float compute() {
      long var1 = System.nanoTime();
      if (this.enabled == 0L) {
         this.enabled = var1;
         return this.selection;
      }

      float var3 = (float)(var1 - this.enabled) / 1.0E9F;
      this.enabled = var1;
      if (!Float.isFinite(var3) || var3 < 0.0F) {
         var3 = 0.0F;
      }

      this.selection = this.selection + Math.min(var3, 0.1F);
      if (this.selection > 720.0F) {
         this.selection -= 720.0F;
      }

      return this.selection;
   }

   private static float process(float var0) {
      if (Float.isNaN(var0)) {
         return 0.0F;
      } else if (var0 < 0.0F) {
         return 0.0F;
      } else {
         return var0 > 1.0F ? 1.0F : var0;
      }
   }

   @Override
   public void close() {
      try {
         if (this.output != 0) {
            GL11.glDeleteTextures(this.output);
            this.output = 0;
         }

         if (this.state != null) {
            this.state.close();
            this.state = null;
         }

         this.config.close();
      } catch (Throwable var2) {
      }

      this.renderer = false;
      this.mode = 0L;
      this.enabled = 0L;
      this.selection = 0.0F;
      this.animationDraw = false;
      this.pointEncode = 0.5F;
      this.animator = 0.5F;
      this.source = 0L;
      this.target = 0.0F;
      this.pending = 0.0F;
   }

   public void handle(int var1, int var2) {
      try {
         if (this.output != 0) {
            GL11.glDeleteTextures(this.output);
            this.output = 0;
         }

         this.current = 0;
         this.active = 0;
         if (this.state != null) {
            try {
               this.state.close();
            } catch (Throwable var5) {
            }

            this.state = null;
         }

         try {
            this.config.close();
         } catch (Throwable var4) {
         }

         this.cache = null;
         this.renderer = false;
         this.handler = false;
         this.mode = 0L;
         this.enabled = 0L;
         this.selection = 0.0F;
         this.animationDraw = false;
         this.pointEncode = 0.5F;
         this.animator = 0.5F;
         this.source = 0L;
         this.target = 0.0F;
         this.pending = 0.0F;
      } catch (Throwable var6) {
      }
   }
}
