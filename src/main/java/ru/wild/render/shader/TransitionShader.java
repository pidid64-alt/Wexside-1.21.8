package ru.wild.render.shader;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.texture.FramebufferAttachments;

public final class TransitionShader {
   private static final TransitionShader instance = new TransitionShader();
   private static final float data = 1.08F;
   private static final int context = 10;
   private static final int config = -4205825;
   private static final int state = -8547073;
   private final TransitionShader.State cache = new TransitionShader.State();
   private final TransitionShader.State output = new TransitionShader.State();
   private final TransitionShader.State current = new TransitionShader.State();
   private final List<TransitionShader.CacheEntry> active = new ArrayList<>();
   private ShaderBuildReporter mode;
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
   private int summary;
   private int matrixBlend;
   private int vectorMatch;
   private int itemProject;

   private TransitionShader() {
   }

   public static TransitionShader handle() {
      return instance;
   }

   public void handle(float var1, float var2, int var3, int var4) {
      if (Menu.handle(Menu.eventAttach)) {
         MinecraftClient var5 = MinecraftClient.getInstance();
         if (!process(var5)) {
            this.process();
         } else {
            Window var6 = var5.getWindow();
            int var7 = var6.getFramebufferWidth();
            int var8 = var6.getFramebufferHeight();
            if (var7 > 0 && var8 > 0) {
               if (this.active.isEmpty()) {
                  if (!this.handle(var5, this.cache, var7, var8)) {
                     this.process();
                     return;
                  }
               } else if (!this.process(this.cache, var7, var8)) {
                  this.process();
                  return;
               }

               if (!this.handle(var5, var7, var8)) {
                  this.process();
               } else {
                  TransitionShader.CacheEntry var9 = new TransitionShader.CacheEntry();
                  var9.state = handle(var1, 0.0F, Math.max(0.0F, var7 - 1.0F));
                  var9.cache = handle(var2, 0.0F, Math.max(0.0F, var8 - 1.0F));
                  var9.output = var3;
                  var9.current = var4;
                  if (!this.handle(var9.instance, var7, var8)) {
                     this.handle(var9.instance);
                     if (this.active.isEmpty()) {
                        this.resolve();
                     }
                  } else {
                     this.active.add(var9);
                     GuiRippleShader.handle().update();
                  }
               }
            } else {
               this.resolve();
            }
         }
      }
   }

   public void handle(double var1, double var3, int var5, int var6) {
      MinecraftClient var7 = MinecraftClient.getInstance();
      if (!process(var7)) {
         this.process();
      } else {
         Window var8 = var7.getWindow();
         int var9 = var8.getFramebufferWidth();
         int var10 = var8.getFramebufferHeight();
         int var11 = var8.getScaledWidth();
         int var12 = var8.getScaledHeight();
         if (var9 > 0 && var10 > 0 && var11 > 0 && var12 > 0) {
            float var13 = (float)(var1 * var9 / var11);
            float var14 = (float)(var3 * var10 / var12);
            this.handle(var13, var14, var5, var6);
         } else {
            this.process();
         }
      }
   }

   public void handle(float var1) {
      if (!this.active.isEmpty()) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && process(var2) && var2.currentScreen != null) {
            Window var3 = var2.getWindow();
            int var4 = var3.getFramebufferWidth();
            int var5 = var3.getFramebufferHeight();
            if (var4 <= 0 || var5 <= 0 || !this.process(this.cache, var4, var5)) {
               this.resolve();
            } else if (!this.process(var4, var5)) {
               this.process();
            } else {
               TransitionShader.CacheEntry var6 = this.active.get(this.active.size() - 1);
               int var7 = this.handle(var2);
               if (var7 > 0 && this.handle(var2, var6.instance, var4, var5)) {
                  this.process(var1);
                  if (!this.compute(var4, var5)) {
                     this.process();
                  } else if (!this.active.isEmpty()) {
                     this.compute();
                     if (this.mode != null && this.vectorMatch != 0) {
                        if (this.active.size() <= 1 || this.handle(this.output, var4, var5) && this.handle(this.current, var4, var5)) {
                           int var8 = this.cache.data;

                           for (int var9 = 0; var9 < this.active.size(); var9++) {
                              TransitionShader.CacheEntry var10 = this.active.get(var9);
                              boolean var11 = var9 == this.active.size() - 1;
                              int var12 = var11 ? var7 : ((var9 & 1) == 0 ? this.output.data : this.current.data);
                              this.handle(var8, var10.instance.data, var12, var4, var5, var10);
                              var8 = var12;
                           }
                        } else {
                           this.process();
                        }
                     } else {
                        this.process();
                     }
                  }
               } else {
                  this.process();
               }
            }
         } else {
            this.process();
         }
      }
   }

   public void handle(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         if ((this.cache.context <= 0 || this.cache.context == var1 && this.cache.config == var2)
            && (this.output.context <= 0 || this.output.context == var1 && this.output.config == var2)
            && (this.current.context <= 0 || this.current.context == var1 && this.current.config == var2)) {
            for (TransitionShader.CacheEntry var4 : this.active) {
               if (var4.instance.context > 0 && (var4.instance.context != var1 || var4.instance.config != var2)) {
                  this.resolve();
                  return;
               }
            }
         } else {
            this.resolve();
         }
      } else {
         this.resolve();
      }
   }

   public void handle(boolean var1) {
      if (!var1) {
         this.resolve();
      }
   }

   public void process() {
      this.resolve();
   }

   private boolean handle(MinecraftClient var1, int var2, int var3) {
      while (this.active.size() >= 10 && !this.active.isEmpty()) {
         TransitionShader.CacheEntry var4 = this.active.remove(0);
         boolean var5 = this.handle(var4.instance.data, var2, var3, this.cache);
         this.handle(var4.instance);
         if (!var5) {
            return false;
         }
      }

      return true;
   }

   private boolean process(int var1, int var2) {
      for (TransitionShader.CacheEntry var4 : this.active) {
         if (!this.handle(var4.instance, var1, var2)) {
            return false;
         }
      }

      return true;
   }

   private void process(float var1) {
      float var2 = compute(var1);

      for (TransitionShader.CacheEntry var4 : this.active) {
         var4.config += var2;
         var4.data = handle(var4.data + var2 / 1.08F, 0.0F, 1.0F);
         var4.context = resolve(var4.data);
      }
   }

   private boolean compute(int var1, int var2) {
      while (!this.active.isEmpty() && this.active.get(0).data >= 1.0F) {
         TransitionShader.CacheEntry var3 = this.active.remove(0);
         boolean var4 = this.handle(var3.instance.data, var1, var2, this.cache);
         this.handle(var3.instance);
         if (!var4) {
            return false;
         }
      }

      return true;
   }
   private void handle(int var1, int var2, int var3, int var4, int var5, TransitionShader.CacheEntry var6) {
      OpenGlStateSnapshot.NetworkState var7 = OpenGlStateSnapshot.handle();
      boolean var18 = false /* VF: Semaphore variable */;

      label154: {
         try {
            var18 = true;

            try (
               FramebufferAttachments var8 = FramebufferAttachments.handle(0, 3553);
               FramebufferAttachments var9 = FramebufferAttachments.handle(1, 3553);
            ) {
               if (this.matrixBlend == 0) {
                  this.matrixBlend = GL30.glGenFramebuffers();
               }

               GL30.glBindFramebuffer(36160, this.matrixBlend);
               GL30.glFramebufferTexture2D(36160, 36064, 3553, var3, 0);
               GL11.glDrawBuffer(36064);
               if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                  break label154;
               }

               GL11.glViewport(0, 0, var4, var5);
               GL11.glDisable(3089);
               GL11.glDisable(2884);
               GL11.glDisable(2929);
               GL11.glDisable(3042);
               GL11.glDisable(36281);
               GL11.glColorMask(true, true, true, true);
               GL11.glDepthMask(false);
               this.mode.handle();
               this.handle(var4, var5, var6);
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, var1);
               GL13.glActiveTexture(33985);
               GL11.glBindTexture(3553, var2);
               GL30.glBindVertexArray(this.vectorMatch);
               ShaderViewportTracker.handle().handle(2);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindVertexArray(0);
            }
         } finally {
            if (var18) {
               if (this.matrixBlend != 0) {
                  GL30.glBindFramebuffer(36160, this.matrixBlend);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
               }

               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var7);
            }
         }

         if (this.matrixBlend != 0) {
            GL30.glBindFramebuffer(36160, this.matrixBlend);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
         }

         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var7);
         return;
      }

      if (this.matrixBlend != 0) {
         GL30.glBindFramebuffer(36160, this.matrixBlend);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
      }

      GL20.glUseProgram(0);
      OpenGlStateSnapshot.compute(var7);
   }

   private void handle(int var1, int var2, TransitionShader.CacheEntry var3) {
      float var4 = var1 / Math.max(1.0F, var2);
      float var5 = this.handle(var1, var2, var4, var3.state, var3.cache);
      float var6 = var5 * (0.004F + var3.context * 1.145F);
      if (this.selection >= 0) {
         GL20.glUniform1i(this.selection, 0);
      }

      if (this.enabled >= 0) {
         GL20.glUniform1i(this.enabled, 1);
      }

      if (this.renderer >= 0) {
         GL20.glUniform2f(this.renderer, var1, var2);
      }

      if (this.handler >= 0) {
         GL20.glUniform1f(this.handler, var3.context);
      }

      if (this.animationDraw >= 0) {
         GL20.glUniform1f(this.animationDraw, var3.data);
      }

      if (this.pointEncode >= 0) {
         GL20.glUniform1f(this.pointEncode, var3.config);
      }

      if (this.animator >= 0) {
         GL20.glUniform2f(this.animator, var3.state, var3.cache);
      }

      if (this.source >= 0) {
         GL20.glUniform1f(this.source, var4);
      }

      if (this.target >= 0) {
         GL20.glUniform1f(this.target, var6);
      }

      if (this.pending >= 0) {
         GL20.glUniform1f(this.pending, var5);
      }

      if (this.previous >= 0) {
         GL20.glUniform3f(this.previous, (var3.output >>> 16 & 0xFF) / 255.0F, (var3.output >>> 8 & 0xFF) / 255.0F, (var3.output & 0xFF) / 255.0F);
      }

      if (this.latest >= 0) {
         GL20.glUniform3f(this.latest, (var3.current >>> 16 & 0xFF) / 255.0F, (var3.current >>> 8 & 0xFF) / 255.0F, (var3.current & 0xFF) / 255.0F);
      }
   }

   private float handle(int var1, int var2, float var3, float var4, float var5) {
      float var6 = handle(var4 / Math.max(1.0F, var1), 0.0F, 1.0F);
      float var7 = handle(1.0F - var5 / Math.max(1.0F, var2), 0.0F, 1.0F);
      float var8 = handle(var6, var7, 0.0F, 0.0F, var3);
      float var9 = handle(var6, var7, 1.0F, 0.0F, var3);
      float var10 = handle(var6, var7, 1.0F, 1.0F, var3);
      float var11 = handle(var6, var7, 0.0F, 1.0F, var3);
      return Math.max(Math.max(var8, var9), Math.max(var10, var11));
   }

   private boolean handle(MinecraftClient var1, TransitionShader.State var2, int var3, int var4) {
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
   private boolean handle(int var1, int var2, int var3, TransitionShader.State var4) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && this.handle(var4, var2, var3)) {
         OpenGlStateSnapshot.NetworkState var5 = OpenGlStateSnapshot.handle();
         boolean var9 = false /* VF: Semaphore variable */;

         boolean var11;
         label85: {
            try {
               var9 = true;
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
                  if (this.summary != 0) {
                     GL30.glBindFramebuffer(36008, this.summary);
                     GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
                  }

                  OpenGlStateSnapshot.compute(var5);
               }
            }

            if (this.summary != 0) {
               GL30.glBindFramebuffer(36008, this.summary);
               GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
            }

            OpenGlStateSnapshot.compute(var5);
            return var11;
         }

         if (this.summary != 0) {
            GL30.glBindFramebuffer(36008, this.summary);
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
         }

         OpenGlStateSnapshot.compute(var5);
         return var11;
      } else {
         return false;
      }
   }

   private boolean handle(TransitionShader.State var1, int var2, int var3) {
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

      if (this.mode == null) {
         this.mode = ShaderBuildReporter.handle(
            "assets/wild/shaders/postfx/theme_shockwave_transition.vert", "assets/wild/shaders/postfx/theme_shockwave_transition.frag"
         );
         this.selection = this.mode.handle("u_textureOld");
         this.enabled = this.mode.handle("u_textureNew");
         this.renderer = this.mode.handle("u_resolution");
         this.handler = this.mode.handle("u_progress");
         this.animationDraw = this.mode.handle("u_linearProgress");
         this.pointEncode = this.mode.handle("u_time");
         this.animator = this.mode.handle("u_center");
         this.source = this.mode.handle("u_aspect");
         this.target = this.mode.handle("u_radius");
         this.pending = this.mode.handle("u_maxRadius");
         this.previous = this.mode.handle("u_accentTop");
         this.latest = this.mode.handle("u_accentBottom");
      }
   }

   private boolean process(TransitionShader.State var1, int var2, int var3) {
      return var1 != null && var1.instance != 0 && var1.data != 0 && var1.context == var2 && var1.config == var3;
   }

   private void resolve() {
      for (TransitionShader.CacheEntry var2 : this.active) {
         this.handle(var2.instance);
      }

      this.active.clear();
      this.handle(this.cache);
      this.handle(this.output);
      this.handle(this.current);
   }

   private void handle(TransitionShader.State var1) {
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

   private static float compute(float var0) {
      return Float.isFinite(var0) && !(var0 <= 0.0F) ? handle(var0, 0.0F, 6.0F) * 0.05F : 0.0F;
   }

   private static float resolve(float var0) {
      float var1 = handle(var0, 0.0F, 1.0F);
      float var2 = var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
      float var3 = 1.0F - (float)Math.exp(-3.15F * var1);
      return handle(var2 * 0.58F + var3 * 0.42F, 0.0F, 1.0F);
   }

   private static float handle(float var0, float var1, float var2, float var3, float var4) {
      float var5 = (var2 - var0) * var4;
      float var6 = var3 - var1;
      return (float)Math.sqrt(var5 * var5 + var6 * var6);
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   static final class CacheEntry {
      final TransitionShader.State instance = new TransitionShader.State();
      float data;
      float context;
      float config;
      float state;
      float cache;
      int output = -4205825;
      int current = -8547073;
   }

   static final class State {
      int instance;
      int data;
      int context;
      int config;
   }
}
