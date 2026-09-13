package ru.wild.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.AttackArcSegment;
import ru.wild.render.OpenGlStateSnapshot;

public final class PlasmaPinchRenderer {
   public static final int instance = 12;
   private static final String data = "assets/wild/shaders/world/plasma_pinch.vsh";
   private static final String context = "assets/wild/shaders/world/plasma_pinch.frag";
   private static final PlasmaPinchRenderer config = new PlasmaPinchRenderer();
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
   private int animator;
   private int source;
   private int target;
   private final FloatBuffer pending = BufferUtils.createFloatBuffer(16);
   private final FloatBuffer previous = BufferUtils.createFloatBuffer(16);
   private final FloatBuffer latest = BufferUtils.createFloatBuffer(36);
   private final FloatBuffer summary = BufferUtils.createFloatBuffer(12);
   private final FloatBuffer matrixBlend = BufferUtils.createFloatBuffer(48);
   private final FloatBuffer vectorMatch = BufferUtils.createFloatBuffer(36);
   private final Matrix4f itemProject = new Matrix4f();
   private final Vector4f responseCompute = new Vector4f();

   private PlasmaPinchRenderer() {
   }

   public static PlasmaPinchRenderer handle() {
      return config;
   }
   public void handle(MinecraftClient var1, List<AttackArcSegment> var2, Matrix4f var3, Matrix4f var4, Vec3d var5, float var6) {
      if (RenderSystem.isOnRenderThread()) {
         if (var1 != null && var2 != null && !var2.isEmpty() && var3 != null && var4 != null && var5 != null) {
            Window var7 = var1.getWindow();
            if (var7 != null && !var7.hasZeroWidthOrHeight()) {
               int var8 = var7.getFramebufferWidth();
               int var9 = var7.getFramebufferHeight();
               if (var8 > 0 && var9 > 0) {
                  long var10 = System.currentTimeMillis();
                  this.latest.clear();
                  this.summary.clear();
                  this.matrixBlend.clear();
                  this.vectorMatch.clear();
                  int var12 = 0;

                  for (AttackArcSegment var14 : var2) {
                     if (var12 >= 12) {
                        break;
                     }

                     float var15 = var14.handle(var10);
                     if (!(var15 >= 1.0F)) {
                        this.responseCompute.set((float)(var14.handle() - var5.x), (float)(var14.process() - var5.y), (float)(var14.compute() - var5.z), 1.0F);
                        var3.transform(this.responseCompute);
                        float var16 = this.responseCompute.x;
                        float var17 = this.responseCompute.y;
                        float var18 = this.responseCompute.z;
                        if (!(var18 > var14.render() + 0.1F)) {
                           this.latest.put(var16).put(var17).put(var18);
                           this.summary.put(var15);
                           this.matrixBlend.put(var14.check()).put(var14.onTick()).put(var14.refresh()).put(var14.select());
                           int var19 = var14.tick();
                           this.vectorMatch.put((var19 >> 16 & 0xFF) / 255.0F).put((var19 >> 8 & 0xFF) / 255.0F).put((var19 & 0xFF) / 255.0F);
                           var12++;
                        }
                     }
                  }

                  if (var12 != 0) {
                     this.latest.flip();
                     this.summary.flip();
                     this.matrixBlend.flip();
                     this.vectorMatch.flip();
                     this.compute();
                     if (this.state != null && this.source != 0) {
                        int var24 = this.handle(var1);
                        if (var24 > 0) {
                           int var25 = this.process(var1);
                           this.pending.clear();
                           var4.get(this.pending);
                           this.itemProject.set(var4).invert();
                           this.previous.clear();
                           this.itemProject.get(this.previous);
                           OpenGlStateSnapshot.NetworkState var26 = OpenGlStateSnapshot.handle();
                           boolean var22 = false /* VF: Semaphore variable */;

                           label269: {
                              try {
                                 var22 = true;
                                 if (this.animator == 0) {
                                    this.animator = GL30.glGenFramebuffers();
                                 }

                                 GL30.glBindFramebuffer(36160, this.animator);
                                 GL30.glFramebufferTexture2D(36160, 36064, 3553, var24, 0);
                                 GL11.glDrawBuffer(36064);
                                 if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                                    var22 = false;
                                    break label269;
                                 }

                                 GL11.glViewport(0, 0, var8, var9);
                                 GL11.glDisable(3089);
                                 GL11.glDisable(2884);
                                 GL11.glDisable(2929);
                                 GL11.glDisable(36281);
                                 GL11.glColorMask(true, true, true, true);
                                 GL11.glDepthMask(false);
                                 GL11.glEnable(3042);
                                 GL14.glBlendEquation(32774);
                                 GL11.glBlendFunc(1, 769);
                                 this.state.handle();
                                 if (this.cache >= 0) {
                                    GL20.glUniform1i(this.cache, 0);
                                 }

                                 if (this.output >= 0) {
                                    GL20.glUniformMatrix4fv(this.output, false, this.pending);
                                 }

                                 if (this.current >= 0) {
                                    GL20.glUniformMatrix4fv(this.current, false, this.previous);
                                 }

                                 if (this.active >= 0) {
                                    GL20.glUniform2f(this.active, var8, var9);
                                 }

                                 if (this.mode >= 0) {
                                    GL20.glUniform1f(this.mode, var6);
                                 }

                                 if (this.selection >= 0) {
                                    GL20.glUniform1i(this.selection, var12);
                                 }

                                 if (this.enabled >= 0) {
                                    GL20.glUniform1i(this.enabled, var25 > 0 ? 1 : 0);
                                 }

                                 if (this.renderer >= 0) {
                                    GL20.glUniform3fv(this.renderer, this.latest);
                                 }

                                 if (this.handler >= 0) {
                                    GL20.glUniform1fv(this.handler, this.summary);
                                 }

                                 if (this.animationDraw >= 0) {
                                    GL20.glUniform4fv(this.animationDraw, this.matrixBlend);
                                 }

                                 if (this.pointEncode >= 0) {
                                    GL20.glUniform3fv(this.pointEncode, this.vectorMatch);
                                 }

                                 GL13.glActiveTexture(33984);
                                 GL11.glBindTexture(3553, var25);
                                 GL30.glBindVertexArray(this.source);
                                 GL11.glDrawArrays(4, 0, 6);
                                 GL30.glBindVertexArray(0);
                                 var22 = false;
                              } finally {
                                 if (var22) {
                                    if (this.animator != 0) {
                                       GL30.glBindFramebuffer(36160, this.animator);
                                       GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                    }

                                    GL13.glActiveTexture(33984);
                                    GL11.glBindTexture(3553, 0);
                                    GL20.glUseProgram(0);
                                    OpenGlStateSnapshot.compute(var26);
                                 }
                              }

                              if (this.animator != 0) {
                                 GL30.glBindFramebuffer(36160, this.animator);
                                 GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                              }

                              GL13.glActiveTexture(33984);
                              GL11.glBindTexture(3553, 0);
                              GL20.glUseProgram(0);
                              OpenGlStateSnapshot.compute(var26);
                              return;
                           }

                           if (this.animator != 0) {
                              GL30.glBindFramebuffer(36160, this.animator);
                              GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                           }

                           GL13.glActiveTexture(33984);
                           GL11.glBindTexture(3553, 0);
                           GL20.glUseProgram(0);
                           OpenGlStateSnapshot.compute(var26);
                        }
                     }
                  }
               }
            }
         }
      }
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

   private void compute() {
      if (this.source == 0) {
         OpenGlStateSnapshot.NetworkState var1 = OpenGlStateSnapshot.handle();

         try {
            this.source = GL30.glGenVertexArrays();
            this.target = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.source);
            GL15.glBindBuffer(34962, this.target);
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
            this.state = ShaderBuildReporter.handle("assets/wild/shaders/world/plasma_pinch.vsh", "assets/wild/shaders/world/plasma_pinch.frag");
            this.cache = this.state.handle("u_DepthTexture");
            this.output = this.state.handle("u_Proj");
            this.current = this.state.handle("u_InvProj");
            this.active = this.state.handle("u_Resolution");
            this.mode = this.state.handle("u_Time");
            this.selection = this.state.handle("u_Count");
            this.enabled = this.state.handle("u_DepthAvailable");
            this.renderer = this.state.handle("u_Center[0]");
            this.handler = this.state.handle("u_LifeTime[0]");
            this.animationDraw = this.state.handle("u_Params[0]");
            this.pointEncode = this.state.handle("u_CoreTint[0]");
         } catch (Throwable var7) {
            this.state = null;
         }
      }
   }

   public void process() {
      if (RenderSystem.isOnRenderThread()) {
         if (this.animator != 0) {
            GL30.glDeleteFramebuffers(this.animator);
            this.animator = 0;
         }

         if (this.target != 0) {
            GL15.glDeleteBuffers(this.target);
            this.target = 0;
         }

         if (this.source != 0) {
            GL30.glDeleteVertexArrays(this.source);
            this.source = 0;
         }

         if (this.state != null) {
            this.state.process();
            this.state = null;
         }
      }
   }
}
