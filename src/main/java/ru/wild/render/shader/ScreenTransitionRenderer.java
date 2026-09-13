package ru.wild.render.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.texture.FramebufferAttachments;

public final class ScreenTransitionRenderer {
   private static final ScreenTransitionRenderer instance = new ScreenTransitionRenderer();
   private static final float data = 0.3F;
   private static final float context = 9.0F;
   private static final float config = 0.5F;
   private static final ScreenTransitionRenderer.DataRecord state = new ScreenTransitionRenderer.DataRecord(0.4F, 0.0F, 0.1F, 1.0F);
   private final ScreenTransitionRenderer.State cache = new ScreenTransitionRenderer.State();
   private final ScreenTransitionRenderer.State output = new ScreenTransitionRenderer.State();
   private BlurUpsampleShader current;
   private ShaderBuildReporter active;
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
   private int previous;
   private int latest;
   private int summary;
   private int matrixBlend;
   private boolean vectorMatch;
   private float itemProject;
   private float responseCompute;
   private float providerFetch;

   private ScreenTransitionRenderer() {
   }

   public static ScreenTransitionRenderer handle() {
      return instance;
   }

   public void handle(Screen var1, Screen var2) {
      if (var1 != var2) {
         if (var1 instanceof FrameRenderListener || var2 instanceof FrameRenderListener) {
            this.compute();
         } else if (GlCompatibilityProbe.handle()) {
            this.compute();
         } else if (!Menu.handle(Menu.frameCheck)) {
            this.compute();
         } else {
            MinecraftClient var3 = MinecraftClient.getInstance();
            if (var3 != null && var3.world != null && var2 == null) {
               this.compute();
            } else if (handle(var3, var1, var2) && process(var3)) {
               Window var4 = var3.getWindow();
               int var5 = var4.getFramebufferWidth();
               int var6 = var4.getFramebufferHeight();
               if (var5 > 0 && var6 > 0) {
                  boolean var7 = this.vectorMatch && this.process(this.output, var5, var6) && this.handle(this.output.data, var5, var6, this.cache);
                  if (!var7) {
                     var7 = this.handle(var3, this.cache, var5, var6);
                  }

                  if (!var7) {
                     this.apply();
                  } else {
                     this.vectorMatch = true;
                     this.itemProject = 0.0F;
                     this.responseCompute = 0.0F;
                     this.providerFetch = 0.0F;
                  }
               } else {
                  this.execute();
               }
            } else {
               this.apply();
            }
         }
      }
   }

   public void process() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      float var2 = var1 != null && var1.getRenderTickCounter() != null ? var1.getRenderTickCounter().getDynamicDeltaTicks() : 0.0F;
      this.handle(var2);
   }

   public void handle(float var1) {
      if (this.vectorMatch) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 == null || !process(var2)) {
            this.apply();
         } else if (var2.currentScreen != null && var2.world == null) {
            Window var3 = var2.getWindow();
            int var4 = var3.getFramebufferWidth();
            int var5 = var3.getFramebufferHeight();
            if (var4 > 0 && var5 > 0 && this.process(this.cache, var4, var5)) {
               this.process(var1);
               if (this.itemProject >= 1.0F) {
                  this.apply();
               } else {
                  int var6 = this.handle(var2);
                  if (var6 > 0 && this.handle(var2, this.output, var4, var5) && this.process(this.output, var4, var5)) {
                     this.update();
                     if (this.active != null && this.summary != 0) {
                        float var7 = this.resolve();
                        float var8 = handle(var7 / 9.0F, 0.0F, 1.0F);
                        int var9 = this.handle(var4, var5, var7);
                        if (var9 <= 0) {
                           var9 = this.output.data;
                           var8 = 0.0F;
                        }

                        this.handle(var6, var4, var5, var9, var8);
                        if (this.itemProject >= 1.0F) {
                           this.apply();
                        }
                     } else {
                        this.apply();
                     }
                  } else {
                     this.apply();
                  }
               }
            } else {
               this.execute();
            }
         } else {
            this.compute();
         }
      }
   }

   public void handle(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         if ((this.cache.context <= 0 || this.cache.context == var1 && this.cache.config == var2)
            && (this.output.context <= 0 || this.output.context == var1 && this.output.config == var2)) {
            if (this.current != null) {
               this.current.resolve();
            }
         } else {
            this.execute();
         }
      } else {
         this.execute();
      }
   }

   public void handle(boolean var1) {
      if (!var1) {
         this.execute();
      }
   }

   private void process(float var1) {
      float var2 = resolve(var1);
      this.providerFetch += var2;
      this.itemProject = handle(this.itemProject + var2 / 0.3F, 0.0F, 1.0F);
      this.responseCompute = state.solve(this.itemProject);
   }

   private float resolve() {
      float var1 = compute(this.responseCompute * 1.6F);
      return 9.0F * var1;
   }

   private int handle(int var1, int var2, float var3) {
      if (var3 < 0.5F) {
         return this.cache.data;
      }

      if (this.current == null) {
         this.current = new BlurUpsampleShader(32856, 5121);
      }

      return this.current.handle(this.cache.data, var1, var2, var3);
   }

   private static float compute(float var0) {
      float var1 = handle(var0, 0.0F, 1.0F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }
   private void handle(int var1, int var2, int var3, int var4, float var5) {
      OpenGlStateSnapshot.NetworkState var6 = OpenGlStateSnapshot.handle();
      boolean var20 = false /* VF: Semaphore variable */;

      label200: {
         try {
            var20 = true;

            try (
               FramebufferAttachments var7 = FramebufferAttachments.handle(0, 3553);
               FramebufferAttachments var8 = FramebufferAttachments.handle(1, 3553);
               FramebufferAttachments var9 = FramebufferAttachments.handle(2, 3553);
            ) {
               if (this.latest == 0) {
                  this.latest = GL30.glGenFramebuffers();
               }

               GL30.glBindFramebuffer(36160, this.latest);
               GL30.glFramebufferTexture2D(36160, 36064, 3553, var1, 0);
               GL11.glDrawBuffer(36064);
               if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                  break label200;
               }

               GL11.glViewport(0, 0, var2, var3);
               GL11.glDisable(3089);
               GL11.glDisable(2884);
               GL11.glDisable(2929);
               GL11.glDisable(3042);
               GL11.glDisable(36281);
               GL11.glColorMask(true, true, true, true);
               GL11.glDepthMask(false);
               this.active.handle();
               this.process(var2, var3, var5);
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, this.cache.data);
               GL13.glActiveTexture(33985);
               GL11.glBindTexture(3553, this.output.data);
               GL13.glActiveTexture(33986);
               GL11.glBindTexture(3553, var4);
               GL30.glBindVertexArray(this.summary);
               ShaderViewportTracker.handle().handle(2);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindVertexArray(0);
            }
         } finally {
            if (var20) {
               if (this.latest != 0) {
                  GL30.glBindFramebuffer(36160, this.latest);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
               }

               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var6);
            }
         }

         if (this.latest != 0) {
            GL30.glBindFramebuffer(36160, this.latest);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
         }

         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var6);
         return;
      }

      if (this.latest != 0) {
         GL30.glBindFramebuffer(36160, this.latest);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
      }

      GL20.glUseProgram(0);
      OpenGlStateSnapshot.compute(var6);
   }

   private void process(int var1, int var2, float var3) {
      if (this.mode >= 0) {
         GL20.glUniform1i(this.mode, 0);
      }

      if (this.selection >= 0) {
         GL20.glUniform1i(this.selection, 1);
      }

      if (this.enabled >= 0) {
         GL20.glUniform1i(this.enabled, 2);
      }

      if (this.renderer >= 0) {
         GL20.glUniform2f(this.renderer, var1, var2);
      }

      if (this.handler >= 0) {
         GL20.glUniform1f(this.handler, this.responseCompute);
      }

      if (this.animationDraw >= 0) {
         GL20.glUniform1f(this.animationDraw, this.itemProject);
      }

      if (this.pointEncode >= 0) {
         GL20.glUniform1f(this.pointEncode, this.responseCompute);
      }

      if (this.animator >= 0) {
         GL20.glUniform1f(this.animator, 1.04F - 0.04F * this.responseCompute);
      }

      if (this.source >= 0) {
         GL20.glUniform1f(this.source, var3);
      }

      if (this.target >= 0) {
         GL20.glUniform1f(this.target, 0.0F);
      }

      if (this.pending >= 0) {
         GL20.glUniform1f(this.pending, this.providerFetch);
      }
   }

   private boolean handle(MinecraftClient var1, ScreenTransitionRenderer.State var2, int var3, int var4) {
      int var5 = this.handle(var1);
      return var5 > 0 && this.handle(var5, var3, var4, var2);
   }

   private int handle(MinecraftClient var1) {
      if (var1 == null) {
         return 0;
      } else {
         Framebuffer var2 = var1.getFramebuffer();
         if (var2 == null) {
            return 0;
         } else {
            return var2.getColorAttachment() instanceof GlTexture var4 ? var4.getGlId() : 0;
         }
      }
   }
   private boolean handle(int var1, int var2, int var3, ScreenTransitionRenderer.State var4) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && this.handle(var4, var2, var3)) {
         OpenGlStateSnapshot.NetworkState var5 = OpenGlStateSnapshot.handle();
         boolean var9 = false /* VF: Semaphore variable */;

         boolean var11;
         label85: {
            try {
               var9 = true;
               if (this.previous == 0) {
                  this.previous = GL30.glGenFramebuffers();
               }

               GL11.glDisable(3089);
               GL11.glDisable(3042);
               GL11.glDisable(2884);
               GL11.glDisable(2929);
               GL11.glDisable(36281);
               GL30.glBindFramebuffer(36008, this.previous);
               GL30.glFramebufferTexture2D(36008, 36064, 3553, var1, 0);
               if (GL30.glCheckFramebufferStatus(36008) != 36053) {
                  var11 = false;
                  var9 = false;
                  break label85;
               }

               GL30.glBindFramebuffer(36009, var4.instance);
               GL11.glReadBuffer(36064);
               GL11.glDrawBuffer(36064);
               GL30.glBlitFramebuffer(0, 0, var2, var3, 0, 0, var2, var3, 16384, 9728);
               var11 = true;
               var9 = false;
            } finally {
               if (var9) {
                  if (this.previous != 0) {
                     GL30.glBindFramebuffer(36008, this.previous);
                     GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
                  }

                  OpenGlStateSnapshot.compute(var5);
               }
            }

            if (this.previous != 0) {
               GL30.glBindFramebuffer(36008, this.previous);
               GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
            }

            OpenGlStateSnapshot.compute(var5);
            return var11;
         }

         if (this.previous != 0) {
            GL30.glBindFramebuffer(36008, this.previous);
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
         }

         OpenGlStateSnapshot.compute(var5);
         return var11;
      } else {
         return false;
      }
   }

   private boolean handle(ScreenTransitionRenderer.State var1, int var2, int var3) {
      if (var1 != null && var2 > 0 && var3 > 0) {
         if (var1.data != 0 && (var1.context != var2 || var1.config != var3 || var1.instance == 0)) {
            this.handle(var1);
         }

         if (var1.data == 0) {
            OpenGlStateSnapshot.NetworkState var4 = OpenGlStateSnapshot.handle();

            try {
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
            } finally {
               OpenGlStateSnapshot.compute(var4);
            }
         }

         var1.context = var2;
         var1.config = var3;
         return true;
      } else {
         return false;
      }
   }

   private void update() {
      if (this.summary == 0) {
         OpenGlStateSnapshot.NetworkState var1 = OpenGlStateSnapshot.handle();

         try {
            this.summary = GL30.glGenVertexArrays();
            this.matrixBlend = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.summary);
            GL15.glBindBuffer(34962, this.matrixBlend);
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

      if (this.active == null) {
         this.active = ShaderBuildReporter.handle(
            "assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/postfx/cinematic_screen_transition.frag"
         );
         this.mode = this.active.handle("uOldScreen");
         this.selection = this.active.handle("uNewScreen");
         this.enabled = this.active.handle("uBlurredScreen");
         this.renderer = this.active.handle("uResolution");
         this.handler = this.active.handle("uProgress");
         this.animationDraw = this.active.handle("uLinearProgress");
         this.pointEncode = this.active.handle("uAlpha");
         this.animator = this.active.handle("uScale");
         this.source = this.active.handle("uBlurMix");
         this.target = this.active.handle("uExposure");
         this.pending = this.active.handle("uTime");
      }
   }

   private boolean process(ScreenTransitionRenderer.State var1, int var2, int var3) {
      return var1 != null && var1.instance != 0 && var1.data != 0 && var1.context == var2 && var1.config == var3;
   }

   private void apply() {
      this.vectorMatch = false;
      this.itemProject = 0.0F;
      this.responseCompute = 0.0F;
      this.providerFetch = 0.0F;
   }

   public void compute() {
      this.execute();
   }

   private void execute() {
      this.apply();
      this.handle(this.cache);
      this.handle(this.output);
      if (this.current != null) {
         this.current.resolve();
      }
   }

   private void handle(ScreenTransitionRenderer.State var1) {
      if (var1 != null) {
         if (var1.instance != 0) {
            GL30.glDeleteFramebuffers(var1.instance);
            var1.instance = 0;
         }

         if (var1.data != 0) {
            GL11.glDeleteTextures(var1.data);
            var1.data = 0;
         }

         var1.context = 0;
         var1.config = 0;
      }
   }

   private static boolean process(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         Window var1 = var0.getWindow();
         return !var1.hasZeroWidthOrHeight() && var1.getFramebufferWidth() > 0 && var1.getFramebufferHeight() > 0;
      } else {
         return false;
      }
   }

   private static boolean handle(MinecraftClient var0, Screen var1, Screen var2) {
      return !(var1 instanceof FrameRenderListener) && !(var2 instanceof FrameRenderListener)
         ? var0 != null && var0.world == null && var1 != null && var2 != null
         : false;
   }

   private static float resolve(float var0) {
      return Float.isFinite(var0) && !(var0 <= 0.0F) ? handle(var0, 0.0F, 6.0F) * 0.05F : 0.0F;
   }

   static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   record DataRecord(float x1, float y1, float x2, float y2) {
      float solve(float var1) {
         float var2 = ScreenTransitionRenderer.handle(var1, 0.0F, 1.0F);
         float var3 = var2;

         for (int var4 = 0; var4 < 7; var4++) {
            float var5 = sample(var3, this.x1, this.x2) - var2;
            if (Math.abs(var5) < 1.0E-5F) {
               return ScreenTransitionRenderer.handle(sample(var3, this.y1, this.y2), 0.0F, 1.0F);
            }

            float var6 = derivative(var3, this.x1, this.x2);
            if (Math.abs(var6) < 1.0E-5F) {
               break;
            }

            var3 = ScreenTransitionRenderer.handle(var3 - var5 / var6, 0.0F, 1.0F);
         }

         float var9 = 0.0F;
         float var10 = 1.0F;
         var3 = var2;

         for (int var11 = 0; var11 < 10; var11++) {
            float var7 = sample(var3, this.x1, this.x2);
            if (Math.abs(var7 - var2) < 1.0E-5F) {
               break;
            }

            if (var7 < var2) {
               var9 = var3;
            } else {
               var10 = var3;
            }

            var3 = (var9 + var10) * 0.5F;
         }

         return ScreenTransitionRenderer.handle(sample(var3, this.y1, this.y2), 0.0F, 1.0F);
      }

      private static float sample(float var0, float var1, float var2) {
         float var3 = 1.0F - var0;
         return 3.0F * var3 * var3 * var0 * var1 + 3.0F * var3 * var0 * var0 * var2 + var0 * var0 * var0;
      }

      private static float derivative(float var0, float var1, float var2) {
         float var3 = 1.0F - var0;
         return 3.0F * var3 * var3 * var1 + 6.0F * var3 * var0 * (var2 - var1) + 3.0F * var0 * var0 * (1.0F - var2);
      }
   }

   static final class State {
      int instance;
      int data;
      int context;
      int config;
   }
}
