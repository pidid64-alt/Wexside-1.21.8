package ru.wild.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.AttackTrailSegment;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.shader.ShaderBuildReporter;

public final class HitRefractionRenderer {
   public static final int instance = 10;
   private static final String data = "assets/wild/shaders/world/hit_refraction.vsh";
   private static final String context = "assets/wild/shaders/world/hit_refraction.frag";
   private static final HitRefractionRenderer config = new HitRefractionRenderer();
   private ShaderBuildReporter state;
   private int cache = -1;
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
   private int target;
   private int pending;
   private int previous;
   private int latest;
   private int summary;
   private int matrixBlend;
   private int vectorMatch;
   private int itemProject;
   private final FloatBuffer responseCompute = BufferUtils.createFloatBuffer(16);
   private final FloatBuffer providerFetch = BufferUtils.createFloatBuffer(16);
   private final FloatBuffer profileDraw = BufferUtils.createFloatBuffer(30);
   private final FloatBuffer vectorPerform = BufferUtils.createFloatBuffer(30);
   private final FloatBuffer eventAttach = BufferUtils.createFloatBuffer(40);
   private final FloatBuffer serverRead = BufferUtils.createFloatBuffer(30);
   private final Matrix4f positionAdvance = new Matrix4f();
   private final Vector4f frameCheck = new Vector4f();
   private final Vector3f moduleCollect = new Vector3f();

   private HitRefractionRenderer() {
   }

   public static HitRefractionRenderer handle() {
      return config;
   }
   public void handle(MinecraftClient var1, List<AttackTrailSegment> var2, Matrix4f var3, Matrix4f var4, Vec3d var5, boolean var6, boolean var7, float var8) {
      if (RenderSystem.isOnRenderThread()) {
         if (var1 != null && var2 != null && !var2.isEmpty() && var3 != null && var4 != null && var5 != null) {
            Window var9 = var1.getWindow();
            if (var9 != null && !var9.hasZeroWidthOrHeight()) {
               int var10 = var9.getFramebufferWidth();
               int var11 = var9.getFramebufferHeight();
               if (var10 > 0 && var11 > 0) {
                  long var12 = System.currentTimeMillis();
                  this.profileDraw.clear();
                  this.vectorPerform.clear();
                  this.eventAttach.clear();
                  this.serverRead.clear();
                  int var14 = 0;

                  for (AttackTrailSegment var16 : var2) {
                     if (var14 >= 10) {
                        break;
                     }

                     float var17 = var16.handle(var12);
                     if (!(var17 >= 1.0F)) {
                        float var18 = 1.0F - (float)Math.pow(2.0, -9.0 * var17);
                        float var19 = var16.check() * var18;
                        float var20 = var16.check() * 0.16F * (0.45F + 0.55F * (1.0F - var17));
                        float var21 = handle(0.0F, 0.1F, var17);
                        float var22 = 1.0F - handle(0.5F, 1.0F, var17);
                        float var23 = process(var21 * var22, 0.0F, 1.0F);
                        if (!(var23 <= 8.0E-4F)) {
                           float var24 = var16.onTick() * (0.7F + 0.3F * (1.0F - handle(0.15F, 1.0F, var17)));
                           this.frameCheck.set((float)(var16.handle() - var5.x), (float)(var16.process() - var5.y), (float)(var16.compute() - var5.z), 1.0F);
                           var3.transform(this.frameCheck);
                           float var25 = this.frameCheck.x;
                           float var26 = this.frameCheck.y;
                           float var27 = this.frameCheck.z;
                           if (!(var27 > var19 + var20 + 0.1F)) {
                              this.moduleCollect.set((float)var16.resolve(), (float)var16.update(), (float)var16.apply());
                              var3.transformDirection(this.moduleCollect);
                              float var28 = this.moduleCollect.length();
                              if (var28 < 1.0E-5F) {
                                 this.moduleCollect.set(0.0F, 1.0F, 0.0F);
                              } else {
                                 this.moduleCollect.div(var28);
                              }

                              this.profileDraw.put(var25).put(var26).put(var27);
                              this.vectorPerform.put(this.moduleCollect.x).put(this.moduleCollect.y).put(this.moduleCollect.z);
                              this.eventAttach.put(var19).put(var20).put(var23).put(var24);
                              int var29 = var16.select();
                              this.serverRead.put((var29 >> 16 & 0xFF) / 255.0F).put((var29 >> 8 & 0xFF) / 255.0F).put((var29 & 0xFF) / 255.0F);
                              var14++;
                           }
                        }
                     }
                  }

                  if (var14 != 0) {
                     this.profileDraw.flip();
                     this.vectorPerform.flip();
                     this.eventAttach.flip();
                     this.serverRead.flip();
                     this.compute();
                     if (this.state != null && this.vectorMatch != 0 && this.cache >= 0) {
                        if (this.handle(var10, var11)) {
                           int var34 = this.handle(var1);
                           if (var34 > 0) {
                              int var35 = var7 ? this.process(var1) : 0;
                              if (this.handle(var34, var10, var11)) {
                                 this.responseCompute.clear();
                                 var4.get(this.responseCompute);
                                 this.positionAdvance.set(var4).invert();
                                 this.providerFetch.clear();
                                 this.positionAdvance.get(this.providerFetch);
                                 OpenGlStateSnapshot.NetworkState var36 = OpenGlStateSnapshot.handle();
                                 boolean var32 = false /* VF: Semaphore variable */;

                                 label313: {
                                    try {
                                       var32 = true;
                                       if (this.matrixBlend == 0) {
                                          this.matrixBlend = GL30.glGenFramebuffers();
                                       }

                                       GL30.glBindFramebuffer(36160, this.matrixBlend);
                                       GL30.glFramebufferTexture2D(36160, 36064, 3553, var34, 0);
                                       GL11.glDrawBuffer(36064);
                                       if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                                          var32 = false;
                                          break label313;
                                       }

                                       GL11.glViewport(0, 0, var10, var11);
                                       GL11.glDisable(3089);
                                       GL11.glDisable(2884);
                                       GL11.glDisable(2929);
                                       GL11.glDisable(3042);
                                       GL11.glDisable(36281);
                                       GL11.glColorMask(true, true, true, true);
                                       GL11.glDepthMask(false);
                                       this.state.handle();
                                       GL20.glUniform1i(this.cache, 0);
                                       if (this.output >= 0) {
                                          GL20.glUniform1i(this.output, 1);
                                       }

                                       if (this.current >= 0) {
                                          GL20.glUniformMatrix4fv(this.current, false, this.responseCompute);
                                       }

                                       if (this.active >= 0) {
                                          GL20.glUniformMatrix4fv(this.active, false, this.providerFetch);
                                       }

                                       if (this.mode >= 0) {
                                          GL20.glUniform2f(this.mode, var10, var11);
                                       }

                                       if (this.selection >= 0) {
                                          GL20.glUniform1f(this.selection, var8);
                                       }

                                       if (this.enabled >= 0) {
                                          GL20.glUniform1i(this.enabled, var14);
                                       }

                                       if (this.renderer >= 0) {
                                          GL20.glUniform1i(this.renderer, var6 ? 1 : 0);
                                       }

                                       if (this.handler >= 0) {
                                          GL20.glUniform1i(this.handler, var35 > 0 ? 1 : 0);
                                       }

                                       if (this.animationDraw >= 0) {
                                          GL20.glUniform3fv(this.animationDraw, this.profileDraw);
                                       }

                                       if (this.pointEncode >= 0) {
                                          GL20.glUniform3fv(this.pointEncode, this.vectorPerform);
                                       }

                                       if (this.animator >= 0) {
                                          GL20.glUniform4fv(this.animator, this.eventAttach);
                                       }

                                       if (this.source >= 0) {
                                          GL20.glUniform3fv(this.source, this.serverRead);
                                       }

                                       GL13.glActiveTexture(33984);
                                       GL11.glBindTexture(3553, this.pending);
                                       GL13.glActiveTexture(33985);
                                       GL11.glBindTexture(3553, var35);
                                       GL13.glActiveTexture(33984);
                                       GL30.glBindVertexArray(this.vectorMatch);
                                       GL11.glDrawArrays(4, 0, 6);
                                       GL30.glBindVertexArray(0);
                                       var32 = false;
                                    } finally {
                                       if (var32) {
                                          if (this.matrixBlend != 0) {
                                             GL30.glBindFramebuffer(36160, this.matrixBlend);
                                             GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                          }

                                          GL13.glActiveTexture(33985);
                                          GL11.glBindTexture(3553, 0);
                                          GL13.glActiveTexture(33984);
                                          GL11.glBindTexture(3553, 0);
                                          GL20.glUseProgram(0);
                                          OpenGlStateSnapshot.compute(var36);
                                       }
                                    }

                                    if (this.matrixBlend != 0) {
                                       GL30.glBindFramebuffer(36160, this.matrixBlend);
                                       GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                    }

                                    GL13.glActiveTexture(33985);
                                    GL11.glBindTexture(3553, 0);
                                    GL13.glActiveTexture(33984);
                                    GL11.glBindTexture(3553, 0);
                                    GL20.glUseProgram(0);
                                    OpenGlStateSnapshot.compute(var36);
                                    return;
                                 }

                                 if (this.matrixBlend != 0) {
                                    GL30.glBindFramebuffer(36160, this.matrixBlend);
                                    GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                 }

                                 GL13.glActiveTexture(33985);
                                 GL11.glBindTexture(3553, 0);
                                 GL13.glActiveTexture(33984);
                                 GL11.glBindTexture(3553, 0);
                                 GL20.glUseProgram(0);
                                 OpenGlStateSnapshot.compute(var36);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   private boolean handle(int var1, int var2, int var3) {
      OpenGlStateSnapshot.NetworkState var4 = OpenGlStateSnapshot.handle();
      boolean var8 = false /* VF: Semaphore variable */;

      boolean var10;
      label71: {
         try {
            var8 = true;
            if (this.summary == 0) {
               this.summary = GL30.glGenFramebuffers();
            }

            GL11.glDisable(3089);
            GL11.glDisable(3042);
            GL11.glDisable(2884);
            GL11.glDisable(2929);
            GL11.glDisable(36281);
            GL30.glBindFramebuffer(36008, this.summary);
            GL30.glFramebufferTexture2D(36008, 36064, 3553, var1, 0);
            if (GL30.glCheckFramebufferStatus(36008) != 36053) {
               var10 = false;
               var8 = false;
               break label71;
            }

            GL30.glBindFramebuffer(36009, this.target);
            GL11.glReadBuffer(36064);
            GL11.glDrawBuffer(36064);
            GL30.glBlitFramebuffer(0, 0, var2, var3, 0, 0, var2, var3, 16384, 9728);
            var10 = true;
            var8 = false;
         } finally {
            if (var8) {
               if (this.summary != 0) {
                  GL30.glBindFramebuffer(36008, this.summary);
                  GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
               }

               OpenGlStateSnapshot.compute(var4);
            }
         }

         if (this.summary != 0) {
            GL30.glBindFramebuffer(36008, this.summary);
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
         }

         OpenGlStateSnapshot.compute(var4);
         return var10;
      }

      if (this.summary != 0) {
         GL30.glBindFramebuffer(36008, this.summary);
         GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
      }

      OpenGlStateSnapshot.compute(var4);
      return var10;
   }

   private int handle(MinecraftClient var1) {
      Framebuffer var2 = var1.getFramebuffer();
      if (var2 == null) {
         return 0;
      } else {
         return var2.getColorAttachment() instanceof GlTexture var4 ? var4.getGlId() : 0;
      }
   }

   private int process(MinecraftClient var1) {
      Framebuffer var2 = var1.getFramebuffer();
      if (var2 == null) {
         return 0;
      } else {
         return var2.getDepthAttachment() instanceof GlTexture var4 ? var4.getGlId() : 0;
      }
   }

   private boolean handle(int var1, int var2) {
      if (this.pending != 0 && (this.previous != var1 || this.latest != var2 || this.target == 0)) {
         this.resolve();
      }

      if (this.pending == 0) {
         OpenGlStateSnapshot.NetworkState var3 = OpenGlStateSnapshot.handle();

         try {
            this.pending = GL11.glGenTextures();
            GL11.glBindTexture(3553, this.pending);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            VertexLayoutBinding.handle(32856, var1, var2, 6408, 5121);
            this.target = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, this.target);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, this.pending, 0);
            GL11.glDrawBuffer(36064);
            if (GL30.glCheckFramebufferStatus(36160) != 36053) {
               this.resolve();
               return false;
            }
         } finally {
            OpenGlStateSnapshot.compute(var3);
         }
      }

      this.previous = var1;
      this.latest = var2;
      return true;
   }

   private void compute() {
      if (this.vectorMatch == 0) {
         OpenGlStateSnapshot.NetworkState var1 = OpenGlStateSnapshot.handle();

         try {
            this.vectorMatch = GL30.glGenVertexArrays();
            this.itemProject = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.vectorMatch);
            GL15.glBindBuffer(34962, this.itemProject);
            float[] var2 = new float[]{
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
            GL15.glBufferData(34962, var2, 35044);
            byte var3 = 16;
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, 5126, false, var3, 0L);
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, 5126, false, var3, 8L);
         } finally {
            OpenGlStateSnapshot.compute(var1);
         }
      }

      if (this.state == null) {
         try {
            this.state = ShaderBuildReporter.handle("assets/wild/shaders/world/hit_refraction.vsh", "assets/wild/shaders/world/hit_refraction.frag");
            this.cache = this.state.handle("u_scene");
            this.output = this.state.handle("u_depth");
            this.current = this.state.handle("u_proj");
            this.active = this.state.handle("u_invProj");
            this.mode = this.state.handle("u_resolution");
            this.selection = this.state.handle("u_time");
            this.enabled = this.state.handle("u_count");
            this.renderer = this.state.handle("u_chroma");
            this.handler = this.state.handle("u_depthOcclusion");
            this.animationDraw = this.state.handle("u_center[0]");
            this.pointEncode = this.state.handle("u_axis[0]");
            this.animator = this.state.handle("u_shape[0]");
            this.source = this.state.handle("u_glow[0]");
         } catch (Throwable var7) {
            this.state = null;
         }
      }
   }

   private void resolve() {
      if (this.target != 0) {
         GL30.glDeleteFramebuffers(this.target);
         this.target = 0;
      }

      if (this.pending != 0) {
         GL11.glDeleteTextures(this.pending);
         this.pending = 0;
      }

      this.previous = 0;
      this.latest = 0;
   }

   public void process() {
      if (RenderSystem.isOnRenderThread()) {
         this.resolve();
         if (this.summary != 0) {
            GL30.glDeleteFramebuffers(this.summary);
            this.summary = 0;
         }

         if (this.matrixBlend != 0) {
            GL30.glDeleteFramebuffers(this.matrixBlend);
            this.matrixBlend = 0;
         }

         if (this.itemProject != 0) {
            GL15.glDeleteBuffers(this.itemProject);
            this.itemProject = 0;
         }

         if (this.vectorMatch != 0) {
            GL30.glDeleteVertexArrays(this.vectorMatch);
            this.vectorMatch = 0;
         }

         if (this.state != null) {
            this.state.process();
            this.state = null;
         }
      }
   }

   private static float handle(float var0, float var1, float var2) {
      if (var0 == var1) {
         return var2 < var0 ? 0.0F : 1.0F;
      }

      float var3 = process((var2 - var0) / (var1 - var0), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }
}
