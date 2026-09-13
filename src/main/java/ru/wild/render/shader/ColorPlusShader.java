package ru.wild.render.shader;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexArrayBuffer;

public final class ColorPlusShader implements AutoCloseable {
   private static final String instance = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final String data = "assets/wild/shaders/colorplus/cp_grade.frag";
   private static final String context = "assets/wild/shaders/colorplus/cp_bloom_extract.frag";
   private static final String config = "assets/wild/shaders/colorplus/cp_bloom_blur.frag";
   private static final int state = 4;
   private static volatile ColorPlusShader cache;
   private final ShaderFailureCache output = new ShaderFailureCache();
   private VertexArrayBuffer current;
   private ShaderFailureCache.ShaderState active;
   private ShaderFailureCache.ShaderState mode;
   private ShaderFailureCache.ShaderState selection;
   private int enabled;
   private int renderer;
   private int handler;
   private int animationDraw;
   private int pointEncode;
   private int animator;
   private int source;
   private int target;
   private int pending;
   private int previous;
   private boolean latest;
   private boolean summary;
   private boolean matrixBlend;

   public static ColorPlusShader handle() {
      ColorPlusShader var0 = cache;
      if (var0 != null) {
         return var0;
      }

      synchronized (ColorPlusShader.class) {
         if (cache == null) {
            cache = new ColorPlusShader();
         }

         return cache;
      }
   }

   private ColorPlusShader() {
   }
   public void handle(int var1, int var2, int var3, ColorPlusShader.ColorState var4) {
      if (!this.summary && var4 != null) {
         if (var1 > 0) {
            if (var2 > 1 && var3 > 1) {
               if (!(var4.instance <= 0.001F)) {
                  int var5 = GL11.glGetInteger(36006);
                  int var6 = GL11.glGetInteger(36010);
                  int var7 = GL11.glGetInteger(36006);
                  OpenGlStateSnapshot.NetworkState var8 = OpenGlStateSnapshot.handle();
                  boolean var9 = false;
                  boolean var14 = false /* VF: Semaphore variable */;

                  label231: {
                     label232: {
                        label247: {
                           label234: {
                              label248: {
                                 try {
                                    var14 = true;
                                    this.compute();
                                    if (this.summary) {
                                       var14 = false;
                                       break label231;
                                    }

                                    this.process(var2, var3);
                                    if (this.previous != 0) {
                                       if (this.animator != 0) {
                                          if (this.source != 0) {
                                             GL30.glBindFramebuffer(36160, this.previous);
                                             GL30.glFramebufferTexture2D(36160, 36064, 3553, var1, 0);
                                             GL11.glDrawBuffer(36064);
                                             GL11.glReadBuffer(36064);
                                             var9 = true;
                                             if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                                                var14 = false;
                                                break label232;
                                             }

                                             GL11.glDisable(2929);
                                             GL11.glDisable(2884);
                                             GL11.glDisable(3089);
                                             GL11.glDisable(3042);
                                             GL11.glColorMask(true, true, true, true);
                                             this.handle(var2, var3);
                                             if (this.enabled == 0) {
                                                var14 = false;
                                                break label247;
                                             }

                                             float var10 = var4.source;
                                             if (var10 > 0.001F) {
                                                this.handle(var4);
                                             }

                                             GL30.glBindFramebuffer(36160, this.previous);
                                             GL11.glViewport(0, 0, var2, var3);
                                             this.active.handle();
                                             this.active.handle("uViewport", var2, var3);
                                             this.active.handle("uRect", 0.0F, 0.0F, var2, var3);
                                             this.active.handle("uScene", 0);
                                             this.active.handle("uBloomTex", 1);
                                             this.active.handle("uResolution", var2, var3);
                                             this.active.handle("uStrength", handle(var4.instance));
                                             this.active.handle("uExposure", process(var4.data));
                                             this.active.handle("uContrast", process(var4.context));
                                             this.active.handle("uSaturation", process(var4.config));
                                             this.active.handle("uVibrance", process(var4.state));
                                             this.active.handle("uGamma", process(var4.cache));
                                             this.active.handle("uTemperature", process(var4.output));
                                             this.active.handle("uTint", process(var4.current));
                                             this.active.handle("uLift", var4.active, var4.mode, var4.selection);
                                             this.active.handle("uGammaRgb", var4.enabled, var4.renderer, var4.handler);
                                             this.active.handle("uGain", var4.animationDraw, var4.pointEncode, var4.animator);
                                             this.active.handle("uBloomIntensity", handle(var10));
                                             this.active.handle("uBloomThreshold", Math.max(0.05F, var4.target));
                                             this.active.handle("uBloomRadius", Math.max(2.0F, var4.pending));
                                             this.active.handle("uSharpness", handle(var4.previous));
                                             this.active.handle("uVignette", handle(var4.latest));
                                             this.active.handle("uFlipY", var4.summary ? 1.0F : 0.0F);
                                             GL13.glActiveTexture(33985);
                                             GL11.glBindTexture(3553, this.animationDraw);
                                             GL13.glActiveTexture(33984);
                                             GL11.glBindTexture(3553, this.enabled);
                                             this.current.handle();
                                             if (!this.matrixBlend) {
                                                System.out
                                                   .println("[ColorPlus] First successful render at " + var2 + "x" + var3 + " bloom=" + (var10 > 0.001F));
                                                this.matrixBlend = true;
                                                var14 = false;
                                             } else {
                                                var14 = false;
                                             }
                                             break label234;
                                          }

                                          var14 = false;
                                       } else {
                                          var14 = false;
                                       }
                                    } else {
                                       var14 = false;
                                    }
                                    break label248;
                                 } catch (Throwable var15) {
                                    this.summary = true;
                                    System.err.println("[ColorPlus] BROKEN: " + var15.getMessage());
                                    var15.printStackTrace();
                                    var14 = false;
                                 } finally {
                                    if (var14) {
                                       GL13.glActiveTexture(33985);
                                       GL11.glBindTexture(3553, 0);
                                       GL13.glActiveTexture(33984);
                                       GL11.glBindTexture(3553, 0);
                                       GL20.glUseProgram(0);
                                       if (var9 && this.previous != 0) {
                                          GL30.glBindFramebuffer(36160, this.previous);
                                          GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                       }

                                       GL30.glBindFramebuffer(36009, var5);
                                       GL30.glBindFramebuffer(36008, var6);
                                       GL30.glBindFramebuffer(36160, var7);
                                       OpenGlStateSnapshot.compute(var8);
                                    }
                                 }

                                 GL13.glActiveTexture(33985);
                                 GL11.glBindTexture(3553, 0);
                                 GL13.glActiveTexture(33984);
                                 GL11.glBindTexture(3553, 0);
                                 GL20.glUseProgram(0);
                                 if (var9 && this.previous != 0) {
                                    GL30.glBindFramebuffer(36160, this.previous);
                                    GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                 }

                                 GL30.glBindFramebuffer(36009, var5);
                                 GL30.glBindFramebuffer(36008, var6);
                                 GL30.glBindFramebuffer(36160, var7);
                                 OpenGlStateSnapshot.compute(var8);
                                 return;
                              }

                              GL13.glActiveTexture(33985);
                              GL11.glBindTexture(3553, 0);
                              GL13.glActiveTexture(33984);
                              GL11.glBindTexture(3553, 0);
                              GL20.glUseProgram(0);
                              if (var9 && this.previous != 0) {
                                 GL30.glBindFramebuffer(36160, this.previous);
                                 GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                              }

                              GL30.glBindFramebuffer(36009, var5);
                              GL30.glBindFramebuffer(36008, var6);
                              GL30.glBindFramebuffer(36160, var7);
                              OpenGlStateSnapshot.compute(var8);
                              return;
                           }

                           GL13.glActiveTexture(33985);
                           GL11.glBindTexture(3553, 0);
                           GL13.glActiveTexture(33984);
                           GL11.glBindTexture(3553, 0);
                           GL20.glUseProgram(0);
                           if (var9 && this.previous != 0) {
                              GL30.glBindFramebuffer(36160, this.previous);
                              GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                           }

                           GL30.glBindFramebuffer(36009, var5);
                           GL30.glBindFramebuffer(36008, var6);
                           GL30.glBindFramebuffer(36160, var7);
                           OpenGlStateSnapshot.compute(var8);
                           return;
                        }

                        GL13.glActiveTexture(33985);
                        GL11.glBindTexture(3553, 0);
                        GL13.glActiveTexture(33984);
                        GL11.glBindTexture(3553, 0);
                        GL20.glUseProgram(0);
                        if (var9 && this.previous != 0) {
                           GL30.glBindFramebuffer(36160, this.previous);
                           GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                        }

                        GL30.glBindFramebuffer(36009, var5);
                        GL30.glBindFramebuffer(36008, var6);
                        GL30.glBindFramebuffer(36160, var7);
                        OpenGlStateSnapshot.compute(var8);
                        return;
                     }

                     GL13.glActiveTexture(33985);
                     GL11.glBindTexture(3553, 0);
                     GL13.glActiveTexture(33984);
                     GL11.glBindTexture(3553, 0);
                     GL20.glUseProgram(0);
                     if (var9 && this.previous != 0) {
                        GL30.glBindFramebuffer(36160, this.previous);
                        GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                     }

                     GL30.glBindFramebuffer(36009, var5);
                     GL30.glBindFramebuffer(36008, var6);
                     GL30.glBindFramebuffer(36160, var7);
                     OpenGlStateSnapshot.compute(var8);
                     return;
                  }

                  GL13.glActiveTexture(33985);
                  GL11.glBindTexture(3553, 0);
                  GL13.glActiveTexture(33984);
                  GL11.glBindTexture(3553, 0);
                  GL20.glUseProgram(0);
                  if (var9 && this.previous != 0) {
                     GL30.glBindFramebuffer(36160, this.previous);
                     GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                  }

                  GL30.glBindFramebuffer(36009, var5);
                  GL30.glBindFramebuffer(36008, var6);
                  GL30.glBindFramebuffer(36160, var7);
                  OpenGlStateSnapshot.compute(var8);
               }
            }
         }
      }
   }

   private void handle(ColorPlusShader.ColorState var1) {
      GL30.glBindFramebuffer(36160, this.animator);
      GL11.glViewport(0, 0, this.target, this.pending);
      this.mode.handle();
      this.mode.handle("uViewport", this.target, this.pending);
      this.mode.handle("uRect", 0.0F, 0.0F, this.target, this.pending);
      this.mode.handle("uScene", 0);
      this.mode.handle("uResolution", this.target, this.pending);
      this.mode.handle("uThreshold", Math.max(0.05F, var1.target));
      this.mode.handle("uSoftness", 0.4F);
      this.mode.handle("uFlipY", var1.summary ? 1.0F : 0.0F);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, this.enabled);
      this.current.handle();
      GL30.glBindFramebuffer(36160, this.source);
      GL11.glViewport(0, 0, this.target, this.pending);
      this.selection.handle();
      this.selection.handle("uViewport", this.target, this.pending);
      this.selection.handle("uRect", 0.0F, 0.0F, this.target, this.pending);
      this.selection.handle("uScene", 0);
      this.selection.handle("uResolution", this.target, this.pending);
      this.selection.handle("uDirection", 1.0F, 0.0F);
      float var2 = Math.max(2.0F, var1.pending / 4.0F);
      this.selection.handle("uRadius", var2);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, this.animationDraw);
      this.current.handle();
      GL30.glBindFramebuffer(36160, this.animator);
      GL11.glViewport(0, 0, this.target, this.pending);
      this.selection.handle();
      this.selection.handle("uViewport", this.target, this.pending);
      this.selection.handle("uRect", 0.0F, 0.0F, this.target, this.pending);
      this.selection.handle("uScene", 0);
      this.selection.handle("uResolution", this.target, this.pending);
      this.selection.handle("uDirection", 0.0F, 1.0F);
      this.selection.handle("uRadius", var2);
      GL11.glBindTexture(3553, this.pointEncode);
      this.current.handle();
   }

   private void handle(int var1, int var2) {
      if (this.enabled == 0) {
         this.enabled = GL11.glGenTextures();
         if (this.enabled == 0) {
            return;
         }

         GL11.glBindTexture(3553, this.enabled);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         this.renderer = 0;
         this.handler = 0;
      } else {
         GL11.glBindTexture(3553, this.enabled);
      }

      if (this.renderer == var1 && this.handler == var2) {
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, var1, var2);
      } else {
         GL11.glCopyTexImage2D(3553, 0, 32856, 0, 0, var1, var2, 0);
         this.renderer = var1;
         this.handler = var2;
      }
   }

   private void process(int var1, int var2) {
      int var3 = Math.max(2, var1 / 4);
      int var4 = Math.max(2, var2 / 4);
      if (this.previous == 0) {
         this.previous = GL30.glGenFramebuffers();
      }

      if (this.animationDraw == 0 || this.target != var3 || this.pending != var4) {
         this.process();
         this.target = var3;
         this.pending = var4;
         this.animationDraw = compute(this.target, this.pending);
         this.pointEncode = compute(this.target, this.pending);
         this.animator = handle(this.animationDraw);
         this.source = handle(this.pointEncode);
      }
   }

   private static int compute(int var0, int var1) {
      int var2 = GL11.glGenTextures();
      GL11.glBindTexture(3553, var2);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      GL11.glTexImage2D(3553, 0, 32856, var0, var1, 0, 6408, 5121, (ByteBuffer)null);
      GL11.glBindTexture(3553, 0);
      return var2;
   }

   private static int handle(int var0) {
      int var1 = GL30.glGenFramebuffers();
      GL30.glBindFramebuffer(36160, var1);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, var0, 0);
      GL11.glDrawBuffer(36064);
      return var1;
   }

   private void process() {
      if (this.animator != 0) {
         GL30.glDeleteFramebuffers(this.animator);
         this.animator = 0;
      }

      if (this.source != 0) {
         GL30.glDeleteFramebuffers(this.source);
         this.source = 0;
      }

      if (this.animationDraw != 0) {
         GL11.glDeleteTextures(this.animationDraw);
         this.animationDraw = 0;
      }

      if (this.pointEncode != 0) {
         GL11.glDeleteTextures(this.pointEncode);
         this.pointEncode = 0;
      }

      this.target = 0;
      this.pending = 0;
   }

   private void compute() {
      if (!this.latest) {
         try {
            this.current = new VertexArrayBuffer();
            this.active = this.output.handle("colorplus_grade", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/colorplus/cp_grade.frag");
            this.mode = this.output
               .handle("colorplus_bloom_extract", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/colorplus/cp_bloom_extract.frag");
            this.selection = this.output
               .handle("colorplus_bloom_blur", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/colorplus/cp_bloom_blur.frag");
            this.latest = true;
            System.out.println("[ColorPlus] Shaders loaded successfully");
         } catch (Throwable var2) {
            this.summary = true;
            System.err.println("[ColorPlus] Shader load FAILED: " + var2.getMessage());
            var2.printStackTrace();
         }
      }
   }

   private static float handle(float var0) {
      if (Float.isNaN(var0)) {
         return 0.0F;
      } else if (var0 < 0.0F) {
         return 0.0F;
      } else {
         return var0 > 1.0F ? 1.0F : var0;
      }
   }

   private static float process(float var0) {
      if (Float.isNaN(var0)) {
         return 0.0F;
      } else if (var0 < -1.0F) {
         return -1.0F;
      } else {
         return var0 > 1.0F ? 1.0F : var0;
      }
   }

   @Override
   public void close() {
      try {
         if (this.enabled != 0) {
            GL11.glDeleteTextures(this.enabled);
            this.enabled = 0;
         }

         this.process();
         if (this.previous != 0) {
            GL30.glDeleteFramebuffers(this.previous);
            this.previous = 0;
         }

         if (this.current != null) {
            this.current.close();
            this.current = null;
         }

         this.output.close();
      } catch (Throwable var2) {
      }

      this.latest = false;
      this.summary = false;
      this.matrixBlend = false;
   }

   public static final class ColorState {
      public float instance = 1.0F;
      public float data;
      public float context;
      public float config;
      public float state;
      public float cache;
      public float output;
      public float current;
      public float active;
      public float mode;
      public float selection;
      public float enabled;
      public float renderer;
      public float handler;
      public float animationDraw;
      public float pointEncode;
      public float animator;
      public float source;
      public float target = 0.9F;
      public float pending = 64.0F;
      public float previous;
      public float latest;
      public boolean summary = true;
   }
}
