package ru.wild.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;

public final class WorldEffectsRenderer implements AutoCloseable {
   private static final WorldEffectsRenderer instance = new WorldEffectsRenderer();
   private static final String data = "assets/wild/shaders/world/world_volume.vert";
   private static final String context = "assets/wild/shaders/world/world_fog_fresnel.frag";
   private static final String config = "assets/wild/shaders/world/ambient_particles.frag";
   private static final String state = "assets/wild/shaders/world/world_copy.frag";
   private static final float cache = 1.0E-4F;
   private final WorldEffectsRenderer.State output = new WorldEffectsRenderer.State();
   private final WorldEffectsRenderer.State current = new WorldEffectsRenderer.State();
   private WorldEffectsRenderer.ScreenState active;
   private WorldEffectsRenderer.ScreenState mode;
   private WorldEffectsRenderer.ScreenState selection;
   private int enabled;
   private int renderer;
   private int handler;
   private int animationDraw;
   private boolean pointEncode;
   private boolean animator;

   private WorldEffectsRenderer() {
   }

   public static WorldEffectsRenderer handle() {
      return instance;
   }
   public void handle(MinecraftClient var1, Camera var2, Matrix4f var3, Matrix4f var4, WorldEffectsRenderer.AnimationState var5) {
      if (!this.animator && var1 != null && var2 != null && var3 != null && var4 != null && var5 != null) {
         if (var1.world != null && var1.player != null && handle(var1)) {
            Window var6 = var1.getWindow();
            int var7 = var6.getFramebufferWidth();
            int var8 = var6.getFramebufferHeight();
            if (var7 > 1 && var8 > 1) {
               Framebuffer var9 = var1.getFramebuffer();
               if (var9 != null) {
                  int var10 = handle(var9.getColorAttachment());
                  int var11 = handle(var9.getDepthAttachment());
                  if (var10 > 0 && var11 > 0) {
                     OpenGlStateSnapshot.NetworkState var12 = OpenGlStateSnapshot.handle();
                     boolean var23 = false /* VF: Semaphore variable */;

                     label188: {
                        label178: {
                           label189: {
                              try {
                                 var23 = true;
                                 this.process();
                                 if (!this.animator) {
                                    if (this.handle(this.output, var7, var8)) {
                                       if (this.handle(this.current, var7, var8)) {
                                          if (!this.handle(var10, var7, var8, this.output)) {
                                             var23 = false;
                                             break label188;
                                          }

                                          Vec3d var13 = var2.getPos();
                                          Matrix4f var14 = new Matrix4f(var4).invert();
                                          Matrix4f var15 = new Matrix4f(var3).invert();
                                          var15.m30((float)var13.x);
                                          var15.m31((float)var13.y);
                                          var15.m32((float)var13.z);
                                          Matrix4f var16 = new Matrix4f(var15).mul(var14);
                                          WorldEffectsRenderer.TransformState var17 = new WorldEffectsRenderer.TransformState(
                                             var7, var8, var11, var13, var14, var15, var16, var5
                                          );
                                          int var18 = this.output.data;
                                          int var19 = this.current.data;
                                          if (var5.state > 1.0E-4F) {
                                             this.handle(this.active, var18, var19, var17);
                                             var18 = var19;
                                             var19 = this.output.data;
                                          }

                                          this.handle(this.mode, var18, var19, var17);
                                          var18 = var19;
                                          this.handle(this.selection, var18, var10, var17);
                                          var23 = false;
                                          break label178;
                                       }

                                       var23 = false;
                                    } else {
                                       var23 = false;
                                    }
                                 } else {
                                    var23 = false;
                                 }
                                 break label189;
                              } catch (Throwable var24) {
                                 this.animator = true;
                                 System.err.println("[WorldTweaks] renderer disabled: " + var24.getMessage());
                                 var24.printStackTrace();
                                 var23 = false;
                              } finally {
                                 if (var23) {
                                    if (this.renderer != 0) {
                                       GL30.glBindFramebuffer(36160, this.renderer);
                                       GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                    }

                                    GL20.glUseProgram(0);
                                    OpenGlStateSnapshot.compute(var12);
                                 }
                              }

                              if (this.renderer != 0) {
                                 GL30.glBindFramebuffer(36160, this.renderer);
                                 GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                              }

                              GL20.glUseProgram(0);
                              OpenGlStateSnapshot.compute(var12);
                              return;
                           }

                           if (this.renderer != 0) {
                              GL30.glBindFramebuffer(36160, this.renderer);
                              GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                           }

                           GL20.glUseProgram(0);
                           OpenGlStateSnapshot.compute(var12);
                           return;
                        }

                        if (this.renderer != 0) {
                           GL30.glBindFramebuffer(36160, this.renderer);
                           GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                        }

                        GL20.glUseProgram(0);
                        OpenGlStateSnapshot.compute(var12);
                        return;
                     }

                     if (this.renderer != 0) {
                        GL30.glBindFramebuffer(36160, this.renderer);
                        GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                     }

                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var12);
                  }
               }
            }
         }
      }
   }

   public void handle(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         if (this.output.context > 0 && (this.output.context != var1 || this.output.config != var2)
            || this.current.context > 0 && (this.current.context != var1 || this.current.config != var2)) {
            this.handle(this.output);
            this.handle(this.current);
         }
      } else {
         this.handle(this.output);
         this.handle(this.current);
      }
   }

   private void handle(WorldEffectsRenderer.ScreenState var1, int var2, int var3, WorldEffectsRenderer.TransformState var4) {
      if (var1 != null && var2 > 0 && var3 > 0 && var4 != null) {
         if (this.renderer == 0) {
            this.renderer = GL30.glGenFramebuffers();
         }

         GL30.glBindFramebuffer(36160, this.renderer);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, var3, 0);
         GL11.glDrawBuffer(36064);
         if (GL30.glCheckFramebufferStatus(36160) == 36053) {
            GL11.glViewport(0, 0, var4.instance, var4.data);
            GL11.glDisable(3089);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(3042);
            GL11.glDisable(36281);
            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(false);
            var1.instance.handle();
            this.handle(var1, var2, var4);
            GL30.glBindVertexArray(this.handler);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            GL30.glBindVertexArray(0);
         }
      }
   }

   private void handle(WorldEffectsRenderer.ScreenState var1, int var2, WorldEffectsRenderer.TransformState var3) {
      if (var1.data >= 0) {
         GL20.glUniform1i(var1.data, 0);
      }

      if (var1.context >= 0) {
         GL20.glUniform1i(var1.context, 1);
      }

      if (var1.config >= 0) {
         GL20.glUniform2f(var1.config, var3.instance, var3.data);
      }

      if (var1.state >= 0) {
         GL20.glUniform1f(var1.state, var3.current.animationDraw);
      }

      if (var1.cache >= 0) {
         GL20.glUniform3f(var1.cache, (float)var3.config.x, (float)var3.config.y, (float)var3.config.z);
      }

      if (var1.output >= 0) {
         this.handle(var1.output, var3.state);
      }

      if (var1.current >= 0) {
         this.handle(var1.current, var3.cache);
      }

      if (var1.active >= 0) {
         this.handle(var1.active, var3.output);
      }

      if (var1.mode >= 0) {
         GL20.glUniform3f(var1.mode, var3.current.active, var3.current.mode, var3.current.selection);
      }

      if (var1.selection >= 0) {
         GL20.glUniform3f(var1.selection, var3.current.enabled, var3.current.renderer, var3.current.handler);
      }

      if (var1.enabled >= 0) {
         GL20.glUniform1f(var1.enabled, handle(var3.current.state, 0.0F, 0.1F));
      }

      if (var1.renderer >= 0) {
         GL20.glUniform1f(var1.renderer, handle(var3.current.cache, 0.0F, 1.0F));
      }

      if (var1.handler >= 0) {
         GL20.glUniform1f(var1.handler, handle(var3.current.output, 0.0F, 1.0F));
      }

      if (var1.animationDraw >= 0) {
         GL20.glUniform1f(var1.animationDraw, handle(var3.current.current, 0.0F, 1.0F));
      }

      if (var1.pointEncode >= 0) {
         GL20.glUniform1f(var1.pointEncode, handle(var3.current.instance, 0.0F, 2.0F));
      }

      if (var1.animator >= 0) {
         GL20.glUniform3f(var1.animator, var3.current.data, var3.current.context, var3.current.config);
      }

      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var2);
      GL13.glActiveTexture(33985);
      GL11.glBindTexture(3553, var3.context);
      GL13.glActiveTexture(33984);
   }

   private void handle(int var1, Matrix4f var2) {
      MemoryStack var3 = MemoryStack.stackPush();

      try {
         FloatBuffer var4 = var3.mallocFloat(16);
         var2.get(var4);
         GL20.glUniformMatrix4fv(var1, false, var4);
      } catch (Throwable var7) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (var3 != null) {
         var3.close();
      }
   }

   private boolean handle(int var1, int var2, int var3, WorldEffectsRenderer.State var4) {
      if (var1 > 0 && var4 != null && var4.instance > 0 && var2 > 0 && var3 > 0) {
         if (this.enabled == 0) {
            this.enabled = GL30.glGenFramebuffers();
         }

         GL30.glBindFramebuffer(36008, this.enabled);
         GL30.glFramebufferTexture2D(36008, 36064, 3553, var1, 0);
         if (GL30.glCheckFramebufferStatus(36008) != 36053) {
            return false;
         }

         GL30.glBindFramebuffer(36009, var4.instance);
         GL11.glReadBuffer(36064);
         GL11.glDrawBuffer(36064);
         GL30.glBlitFramebuffer(0, 0, var2, var3, 0, 0, var2, var3, 16384, 9728);
         GL30.glBindFramebuffer(36008, this.enabled);
         GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
         return true;
      } else {
         return false;
      }
   }

   private boolean handle(WorldEffectsRenderer.State var1, int var2, int var3) {
      if (var1 != null && var2 > 0 && var3 > 0) {
         if (var1.data != 0 && (var1.context != var2 || var1.config != var3 || var1.instance == 0)) {
            this.handle(var1);
         }

         if (var1.data == 0) {
            var1.data = GL11.glGenTextures();
            GL11.glBindTexture(3553, var1.data);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            VertexLayoutBinding.handle(32856, var2, var3, 6408, 5121);
            var1.instance = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, var1.instance);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, var1.data, 0);
            GL11.glDrawBuffer(36064);
            if (GL30.glCheckFramebufferStatus(36160) != 36053) {
               this.handle(var1);
               return false;
            }
         }

         var1.context = var2;
         var1.config = var3;
         return true;
      } else {
         return false;
      }
   }

   private void process() {
      if (!this.pointEncode) {
         this.handler = GL30.glGenVertexArrays();
         this.animationDraw = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.handler);
         GL15.glBindBuffer(34962, this.animationDraw);
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
         this.active = new WorldEffectsRenderer.ScreenState("assets/wild/shaders/world/world_fog_fresnel.frag");
         this.mode = new WorldEffectsRenderer.ScreenState("assets/wild/shaders/world/ambient_particles.frag");
         this.selection = new WorldEffectsRenderer.ScreenState("assets/wild/shaders/world/world_copy.frag");
         this.pointEncode = true;
      }
   }

   private static int handle(Object var0) {
      return var0 instanceof GlTexture var1 ? var1.getGlId() : 0;
   }

   private static boolean handle(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         Window var1 = var0.getWindow();
         return !var1.hasZeroWidthOrHeight() && var1.getFramebufferWidth() > 0 && var1.getFramebufferHeight() > 0;
      } else {
         return false;
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static boolean compute() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   private void handle(WorldEffectsRenderer.State var1) {
      if (var1 != null) {
         if (var1.instance != 0 && compute()) {
            GL30.glDeleteFramebuffers(var1.instance);
         }

         if (var1.data != 0 && compute()) {
            GL11.glDeleteTextures(var1.data);
         }

         var1.instance = 0;
         var1.data = 0;
         var1.context = 0;
         var1.config = 0;
      }
   }

   @Override
   public void close() {
      if (!compute()) {
         this.resolve();
      } else {
         this.handle(this.output);
         this.handle(this.current);
         if (this.enabled != 0) {
            GL30.glDeleteFramebuffers(this.enabled);
            this.enabled = 0;
         }

         if (this.renderer != 0) {
            GL30.glDeleteFramebuffers(this.renderer);
            this.renderer = 0;
         }

         if (this.handler != 0) {
            GL30.glDeleteVertexArrays(this.handler);
            this.handler = 0;
         }

         if (this.animationDraw != 0) {
            GL15.glDeleteBuffers(this.animationDraw);
            this.animationDraw = 0;
         }

         handle(this.active);
         handle(this.mode);
         handle(this.selection);
         this.active = null;
         this.mode = null;
         this.selection = null;
         this.pointEncode = false;
         this.animator = false;
      }
   }

   private void resolve() {
      this.output.instance = 0;
      this.output.data = 0;
      this.output.context = 0;
      this.output.config = 0;
      this.current.instance = 0;
      this.current.data = 0;
      this.current.context = 0;
      this.current.config = 0;
      this.enabled = 0;
      this.renderer = 0;
      this.handler = 0;
      this.animationDraw = 0;
      this.active = null;
      this.mode = null;
      this.selection = null;
      this.pointEncode = false;
      this.animator = false;
   }

   private static void handle(WorldEffectsRenderer.ScreenState var0) {
      if (var0 != null) {
         var0.instance.process();
      }
   }

   public static final class AnimationState {
      public float instance;
      public float data = 0.819F;
      public float context;
      public float config = 0.574F;
      public float state;
      public float cache = 0.82F;
      public float output = 0.64F;
      public float current = 0.72F;
      public float active = 0.416F;
      public float mode = 0.482F;
      public float selection = 0.584F;
      public float enabled = 0.5F;
      public float renderer = 0.62F;
      public float handler = 0.78F;
      public float animationDraw;
   }

   static final class ScreenState {
      final ShaderBuildReporter instance;
      final int data;
      final int context;
      final int config;
      final int state;
      final int cache;
      final int output;
      final int current;
      final int active;
      final int mode;
      final int selection;
      final int enabled;
      final int renderer;
      final int handler;
      final int animationDraw;
      final int pointEncode;
      final int animator;

      ScreenState(String var1) {
         this.instance = ShaderBuildReporter.handle("assets/wild/shaders/world/world_volume.vert", var1);
         this.data = this.instance.handle("u_ScreenTexture");
         this.context = this.instance.handle("u_DepthTexture");
         this.config = this.instance.handle("u_Resolution");
         this.state = this.instance.handle("u_Time");
         this.cache = this.instance.handle("u_CameraPos");
         this.output = this.instance.handle("u_InverseProjectionMatrix");
         this.current = this.instance.handle("u_InverseViewMatrix");
         this.active = this.instance.handle("u_InverseViewProjectionMatrix");
         this.mode = this.instance.handle("u_AtmosphereTint");
         this.selection = this.instance.handle("u_SkyColor");
         this.enabled = this.instance.handle("u_FogDensity");
         this.renderer = this.instance.handle("u_HorizonDissolve");
         this.handler = this.instance.handle("u_SkyLift");
         this.animationDraw = this.instance.handle("u_EdgeSoftness");
         this.pointEncode = this.instance.handle("u_WindSpeed");
         this.animator = this.instance.handle("u_WindDirection");
      }
   }

   static final class State {
      int instance;
      int data;
      int context;
      int config;
   }

   static final class TransformState {
      final int instance;
      final int data;
      final int context;
      final Vec3d config;
      final Matrix4f state;
      final Matrix4f cache;
      final Matrix4f output;
      final WorldEffectsRenderer.AnimationState current;

      TransformState(int var1, int var2, int var3, Vec3d var4, Matrix4f var5, Matrix4f var6, Matrix4f var7, WorldEffectsRenderer.AnimationState var8) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
      }
   }
}
