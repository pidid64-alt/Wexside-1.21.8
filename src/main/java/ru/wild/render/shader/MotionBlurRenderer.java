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

public final class MotionBlurRenderer implements AutoCloseable {
   private static final MotionBlurRenderer instance = new MotionBlurRenderer();
   private static final String data = "assets/wild/shaders/world/world_volume.vert";
   private static final String context = "assets/wild/shaders/postfx/motion_blur.frag";
   private static final float config = 1.0E-5F;
   private final MotionBlurRenderer.State state = new MotionBlurRenderer.State();
   private final Matrix4f cache = new Matrix4f();
   private final Matrix4f output = new Matrix4f();
   private Vec3d current = Vec3d.ZERO;
   private float active;
   private float mode;
   private MotionBlurRenderer.ScreenState selection;
   private int enabled;
   private int renderer;
   private int handler;
   private int animationDraw;
   private boolean pointEncode;
   private boolean animator;
   private boolean source;

   private MotionBlurRenderer() {
   }

   public static MotionBlurRenderer handle() {
      return instance;
   }
   public void handle(MinecraftClient var1, Camera var2, Matrix4f var3, Matrix4f var4, MotionBlurRenderer.CacheEntry var5) {
      if (!this.animator && var1 != null && var2 != null && var3 != null && var4 != null && var5 != null) {
         if (var1.world != null && var1.player != null && handle(var1)) {
            Window var6 = var1.getWindow();
            int var7 = var6.getFramebufferWidth();
            int var8 = var6.getFramebufferHeight();
            if (var7 > 1 && var8 > 1 && !(var5.instance <= 1.0E-5F)) {
               Framebuffer var9 = var1.getFramebuffer();
               if (var9 == null) {
                  this.process();
               } else {
                  int var10 = handle(var9.getColorAttachment());
                  int var11 = handle(var9.getDepthAttachment());
                  if (var10 > 0 && var11 > 0) {
                     float var12 = this.handle(var2);
                     float var13 = this.source ? handle(handle(var2.getYaw() - this.active) * 0.0062F, -0.24F, 0.24F) : 0.0F;
                     float var14 = this.source ? handle((var2.getPitch() - this.mode) * -0.0074F, -0.24F, 0.24F) : 0.0F;
                     if (this.source && !(var12 < var5.active * 4.0E-5F)) {
                        OpenGlStateSnapshot.NetworkState var15 = OpenGlStateSnapshot.handle();
                        boolean var16 = false;
                        boolean var24 = false /* VF: Semaphore variable */;

                        label231: {
                           label219: {
                              label232: {
                                 try {
                                    var24 = true;
                                    this.compute();
                                    if (!this.animator && this.handle(this.state, var7, var8)) {
                                       if (!this.handle(var10, var7, var8, this.state)) {
                                          this.handle(var2, var3, var4);
                                          var24 = false;
                                          break label231;
                                       }

                                       Matrix4f var17 = new Matrix4f(var4).invert();
                                       Matrix4f var18 = new Matrix4f(var3).invert();
                                       Vec3d var19 = var2.getPos();
                                       var18.m30((float)var19.x);
                                       var18.m31((float)var19.y);
                                       var18.m32((float)var19.z);
                                       MotionBlurRenderer.TransformState var20 = new MotionBlurRenderer.TransformState(
                                          var7, var8, this.state.data, var11, var19, var17, var18, var5, var12, var13, var14
                                       );
                                       var16 = this.handle(var10, var20);
                                       this.handle(var2, var3, var4);
                                       var24 = false;
                                       break label219;
                                    }

                                    this.handle(var2, var3, var4);
                                    var24 = false;
                                    break label232;
                                 } catch (Throwable var25) {
                                    this.animator = true;
                                    System.err.println("[SilkFlow] renderer disabled: " + var25.getMessage());
                                    var25.printStackTrace();
                                    var24 = false;
                                 } finally {
                                    if (var24) {
                                       if (var16 && this.renderer != 0) {
                                          GL30.glBindFramebuffer(36160, this.renderer);
                                          GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                       }

                                       GL13.glActiveTexture(33985);
                                       GL11.glBindTexture(3553, 0);
                                       GL13.glActiveTexture(33984);
                                       GL11.glBindTexture(3553, 0);
                                       GL20.glUseProgram(0);
                                       OpenGlStateSnapshot.compute(var15);
                                    }
                                 }

                                 if (var16 && this.renderer != 0) {
                                    GL30.glBindFramebuffer(36160, this.renderer);
                                    GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                 }

                                 GL13.glActiveTexture(33985);
                                 GL11.glBindTexture(3553, 0);
                                 GL13.glActiveTexture(33984);
                                 GL11.glBindTexture(3553, 0);
                                 GL20.glUseProgram(0);
                                 OpenGlStateSnapshot.compute(var15);
                                 return;
                              }

                              if (var16 && this.renderer != 0) {
                                 GL30.glBindFramebuffer(36160, this.renderer);
                                 GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                              }

                              GL13.glActiveTexture(33985);
                              GL11.glBindTexture(3553, 0);
                              GL13.glActiveTexture(33984);
                              GL11.glBindTexture(3553, 0);
                              GL20.glUseProgram(0);
                              OpenGlStateSnapshot.compute(var15);
                              return;
                           }

                           if (var16 && this.renderer != 0) {
                              GL30.glBindFramebuffer(36160, this.renderer);
                              GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                           }

                           GL13.glActiveTexture(33985);
                           GL11.glBindTexture(3553, 0);
                           GL13.glActiveTexture(33984);
                           GL11.glBindTexture(3553, 0);
                           GL20.glUseProgram(0);
                           OpenGlStateSnapshot.compute(var15);
                           return;
                        }

                        if (var16 && this.renderer != 0) {
                           GL30.glBindFramebuffer(36160, this.renderer);
                           GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                        }

                        GL13.glActiveTexture(33985);
                        GL11.glBindTexture(3553, 0);
                        GL13.glActiveTexture(33984);
                        GL11.glBindTexture(3553, 0);
                        GL20.glUseProgram(0);
                        OpenGlStateSnapshot.compute(var15);
                     } else {
                        this.handle(var2, var3, var4);
                     }
                  } else {
                     this.handle(var2, var3, var4);
                  }
               }
            } else {
               this.handle(var2, var3, var4);
            }
         } else {
            this.process();
         }
      }
   }

   public void handle(int var1, int var2) {
      this.process();
      if (var1 > 0 && var2 > 0) {
         if (this.state.context > 0 && (this.state.context != var1 || this.state.config != var2)) {
            this.handle(this.state);
         }
      } else {
         this.handle(this.state);
      }
   }

   public void process() {
      this.source = false;
      this.current = Vec3d.ZERO;
      this.active = 0.0F;
      this.mode = 0.0F;
      this.cache.identity();
      this.output.identity();
   }

   private boolean handle(int var1, MotionBlurRenderer.TransformState var2) {
      if (this.renderer == 0) {
         this.renderer = GL30.glGenFramebuffers();
      }

      GL30.glBindFramebuffer(36160, this.renderer);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, var1, 0);
      GL11.glDrawBuffer(36064);
      if (GL30.glCheckFramebufferStatus(36160) != 36053) {
         return true;
      }

      GL11.glViewport(0, 0, var2.instance, var2.data);
      GL11.glDisable(3089);
      GL11.glDisable(2929);
      GL11.glDisable(2884);
      GL11.glDisable(3042);
      GL11.glDisable(36281);
      GL11.glColorMask(true, true, true, true);
      GL11.glDepthMask(false);
      this.selection.instance.handle();
      this.handle(var2);
      GL30.glBindVertexArray(this.handler);
      ShaderViewportTracker.handle().handle(2);
      GL11.glDrawArrays(4, 0, 6);
      GL30.glBindVertexArray(0);
      return true;
   }

   private void handle(MotionBlurRenderer.TransformState var1) {
      process(this.selection.data, 0);
      process(this.selection.context, 1);
      handle(this.selection.config, var1.instance, var1.data);
      handle(this.selection.state, var1.cache);
      handle(this.selection.cache, var1.output);
      handle(this.selection.output, this.output);
      handle(this.selection.current, this.cache);
      handle(this.selection.active, (float)var1.state.x, (float)var1.state.y, (float)var1.state.z);
      handle(this.selection.mode, (float)this.current.x, (float)this.current.y, (float)this.current.z);
      handle(this.selection.selection, handle(var1.current.instance, 0.0F, 1.0F));
      handle(this.selection.enabled, handle(var1.current.data, 0.05F, 4.0F));
      handle(this.selection.renderer, handle(var1.current.config, 1.0F, 128.0F));
      handle(this.selection.handler, handle(var1.current.state, 0.02F, 4.0F));
      handle(this.selection.animationDraw, handle(var1.current.cache, 0.0F, 3.0F));
      handle(this.selection.pointEncode, handle(var1.current.output, 0.0F, 1.0F));
      handle(this.selection.animator, handle(var1.current.current, 0.2F, 8.0F));
      handle(this.selection.source, handle(var1.current.active, 0.01F, 4.0F));
      handle(this.selection.target, var1.active);
      handle(this.selection.pending, var1.mode, var1.selection);
      process(this.selection.previous, Math.max(3, Math.min(12, var1.current.context)));
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var1.context);
      GL13.glActiveTexture(33985);
      GL11.glBindTexture(3553, var1.config);
      GL13.glActiveTexture(33984);
   }

   private boolean handle(int var1, int var2, int var3, MotionBlurRenderer.State var4) {
      if (var1 > 0 && var4 != null && var4.instance > 0 && var2 > 0 && var3 > 0) {
         if (this.enabled == 0) {
            this.enabled = GL30.glGenFramebuffers();
         }

         GL30.glBindFramebuffer(36008, this.enabled);
         GL30.glFramebufferTexture2D(36008, 36064, 3553, var1, 0);
         if (GL30.glCheckFramebufferStatus(36008) != 36053) {
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
            return false;
         } else {
            GL30.glBindFramebuffer(36009, var4.instance);
            GL11.glReadBuffer(36064);
            GL11.glDrawBuffer(36064);
            GL30.glBlitFramebuffer(0, 0, var2, var3, 0, 0, var2, var3, 16384, 9728);
            GL30.glBindFramebuffer(36008, this.enabled);
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean handle(MotionBlurRenderer.State var1, int var2, int var3) {
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

   private void compute() {
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
         this.selection = new MotionBlurRenderer.ScreenState();
         this.pointEncode = true;
      }
   }

   private float handle(Camera var1) {
      if (this.source && var1 != null) {
         Vec3d var2 = var1.getPos();
         float var3 = handle(var1.getYaw() - this.active);
         float var4 = var1.getPitch() - this.mode;
         double var5 = var2.distanceTo(this.current);
         float var7 = Math.abs(var3) * 0.00175F + Math.abs(var4) * 0.00225F;
         float var8 = (float)Math.min(0.12, var5 * 0.045);
         return var7 + var8;
      } else {
         return 1.0F;
      }
   }

   private void handle(Camera var1, Matrix4f var2, Matrix4f var3) {
      if (var1 != null && var2 != null && var3 != null) {
         this.cache.set(var2);
         this.output.set(var3);
         this.current = var1.getPos();
         this.active = var1.getYaw();
         this.mode = var1.getPitch();
         this.source = true;
      } else {
         this.process();
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

   private static boolean resolve() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   private static float handle(float var0) {
      var0 %= 360.0F;
      if (var0 >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   private static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static void process(int var0, int var1) {
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

   private static void handle(int var0, Matrix4f var1) {
      if (var0 >= 0 && var1 != null) {
         MemoryStack var2 = MemoryStack.stackPush();

         try {
            FloatBuffer var3 = var2.mallocFloat(16);
            var1.get(var3);
            GL20.glUniformMatrix4fv(var0, false, var3);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }
      }
   }

   private void handle(MotionBlurRenderer.State var1) {
      if (var1 != null) {
         if (var1.instance != 0 && resolve()) {
            GL30.glDeleteFramebuffers(var1.instance);
         }

         if (var1.data != 0 && resolve()) {
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
      this.process();
      if (!resolve()) {
         this.update();
      } else {
         this.handle(this.state);
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

         if (this.selection != null) {
            this.selection.instance.process();
            this.selection = null;
         }

         this.pointEncode = false;
         this.animator = false;
      }
   }

   private void update() {
      this.state.instance = 0;
      this.state.data = 0;
      this.state.context = 0;
      this.state.config = 0;
      this.enabled = 0;
      this.renderer = 0;
      this.handler = 0;
      this.animationDraw = 0;
      this.selection = null;
      this.pointEncode = false;
      this.animator = false;
   }

   public static final class CacheEntry {
      public float instance = 0.72F;
      public float data = 1.05F;
      public int context = 7;
      public float config = 34.0F;
      public float state = 0.54F;
      public float cache = 0.58F;
      public float output = 0.72F;
      public float current = 2.25F;
      public float active = 0.42F;
   }

   static final class ScreenState {
      final ShaderBuildReporter instance = ShaderBuildReporter.handle(
         "assets/wild/shaders/world/world_volume.vert", "assets/wild/shaders/postfx/motion_blur.frag"
      );
      final int data = this.instance.handle("u_ScreenTexture");
      final int context = this.instance.handle("u_DepthTexture");
      final int config = this.instance.handle("u_Resolution");
      final int state = this.instance.handle("u_InverseProjectionMatrix");
      final int cache = this.instance.handle("u_InverseViewMatrix");
      final int output = this.instance.handle("u_PreviousProjectionMatrix");
      final int current = this.instance.handle("u_PreviousViewMatrix");
      final int active = this.instance.handle("u_CameraPos");
      final int mode = this.instance.handle("u_PreviousCameraPos");
      final int selection = this.instance.handle("u_Strength");
      final int enabled = this.instance.handle("u_TemporalScale");
      final int renderer = this.instance.handle("u_MaxRadius");
      final int handler = this.instance.handle("u_EdgeFocus");
      final int animationDraw = this.instance.handle("u_ChromaticPhase");
      final int pointEncode = this.instance.handle("u_DepthGuard");
      final int animator = this.instance.handle("u_Decay");
      final int source = this.instance.handle("u_Activation");
      final int target = this.instance.handle("u_GlobalMotion");
      final int pending = this.instance.handle("u_CameraVelocity");
      final int previous = this.instance.handle("u_Samples");
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
      final int config;
      final Vec3d state;
      final Matrix4f cache;
      final Matrix4f output;
      final MotionBlurRenderer.CacheEntry current;
      final float active;
      final float mode;
      final float selection;

      TransformState(
         int var1,
         int var2,
         int var3,
         int var4,
         Vec3d var5,
         Matrix4f var6,
         Matrix4f var7,
         MotionBlurRenderer.CacheEntry var8,
         float var9,
         float var10,
         float var11
      ) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
         this.mode = var10;
         this.selection = var11;
      }
   }
}
