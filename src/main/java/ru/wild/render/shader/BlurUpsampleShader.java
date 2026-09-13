package ru.wild.render.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.texture.FramebufferAttachments;

public final class BlurUpsampleShader {
   private static final int instance = 6;
   private static final float data = 0.5F;
   private static final float context = 30.0F;
   private final ShaderBuildReporter config;
   private final ShaderBuildReporter state;
   private final ShaderBuildReporter cache;
   private final ShaderBuildReporter output;
   private final int current;
   private final int active;
   private final int mode;
   private final int selection;
   private final int enabled;
   private final int renderer;
   private final int handler;
   private final int animationDraw;
   private final int pointEncode;
   private final int animator;
   private final int source;
   private final int target;
   private final int pending;
   private final int previous;
   private int latest;
   private int summary;
   private final BlurUpsampleShader.State[] matrixBlend = new BlurUpsampleShader.State[6];
   private final BlurUpsampleShader.State vectorMatch = new BlurUpsampleShader.State();
   private final BlurUpsampleShader.State itemProject = new BlurUpsampleShader.State();

   public float handle() {
      return 0.5F;
   }

   public float process() {
      return 30.0F;
   }

   public BlurUpsampleShader() {
      this(32856, 5121);
   }

   public BlurUpsampleShader(int var1, int var2) {
      if (var1 == 0) {
         throw new IllegalArgumentException("intermediateInternalFormat must be a valid OpenGL format constant");
      }

      if (var2 == 0) {
         throw new IllegalArgumentException("intermediatePixelType must be a valid OpenGL pixel type constant");
      }

      this.config = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/blur/blur_downsample.frag");
      this.state = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/blur/blur_upsample.frag");
      this.cache = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/blur/blur_small_horizontal.frag");
      this.output = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/blur/blur_small_vertical.frag");
      this.current = var1;
      this.active = var2;
      this.mode = this.config.handle("uSource");
      this.selection = this.config.handle("uTexelSize");
      this.enabled = this.config.handle("uOffset");
      this.renderer = this.state.handle("uSource");
      this.handler = this.state.handle("uTexelSize");
      this.animationDraw = this.state.handle("uOffset");
      this.pointEncode = this.cache.handle("uSource");
      this.animator = this.cache.handle("uTexelSize");
      this.source = this.cache.handle("uRadius");
      this.target = this.output.handle("uSource");
      this.pending = this.output.handle("uTexelSize");
      this.previous = this.output.handle("uRadius");

      for (int var3 = 0; var3 < this.matrixBlend.length; var3++) {
         this.matrixBlend[var3] = new BlurUpsampleShader.State();
      }

      OpenGlStateSnapshot.NetworkState var9 = OpenGlStateSnapshot.handle();

      try {
         this.latest = GL30.glGenVertexArrays();
         this.summary = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.latest);
         GL15.glBindBuffer(34962, this.summary);
         float[] var4 = new float[]{-1.0F, -1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
         GL15.glBufferData(34962, var4, 35044);
         byte var5 = 16;
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, var5, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, var5, 8L);
      } finally {
         OpenGlStateSnapshot.compute(var9);
      }
   }

   public void compute() {
      this.resolve();
      if (this.latest != 0) {
         GL30.glDeleteVertexArrays(this.latest);
         this.latest = 0;
      }

      if (this.summary != 0) {
         GL15.glDeleteBuffers(this.summary);
         this.summary = 0;
      }

      this.config.process();
      this.state.process();
      this.cache.process();
      this.output.process();
   }

   public void resolve() {
      for (BlurUpsampleShader.State var4 : this.matrixBlend) {
         this.process(var4);
      }

      this.process(this.vectorMatch);
      this.process(this.itemProject);
   }

   public int handle(int var1, int var2, int var3, float var4) {
      return this.handle(var1, var2, var3, var4, true);
   }
   public int handle(int var1, int var2, int var3, float var4, boolean var5) {
      if (var1 != 0 && var2 > 0 && var3 > 0) {
         float var6 = Math.max(var4, 0.5F);
         boolean var7 = var6 <= 30.0F;
         int var8 = 0;
         float[] var9 = null;
         if (var7) {
            if (!this.handle(this.vectorMatch, var2, var3) || !this.handle(this.itemProject, var2, var3)) {
               return 0;
            }
         } else {
            var8 = this.handle(var6, var2, var3);
            if (var8 <= 0) {
               return var1;
            }

            var9 = this.handle(var8, var6);
            if (!this.handle(var2, var3, var8) || !this.handle(this.vectorMatch, var2, var3)) {
               return 0;
            }
         }

         OpenGlStateSnapshot.NetworkState var10 = var5 ? OpenGlStateSnapshot.handle() : null;
         boolean var18 = false /* VF: Semaphore variable */;

         int var12;
         try {
            var18 = true;

            try (FramebufferAttachments var11 = FramebufferAttachments.handle(0, 3553)) {
               GL11.glDisable(3089);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glDisable(3042);
               GL11.glDisable(36281);
               GL13.glActiveTexture(33984);
               GL30.glBindVertexArray(this.latest);
               if (var7) {
                  this.process(var1, var2, var3, var6);
               } else {
                  this.handle(var1, var2, var3, var8, var9);
               }

               var12 = this.vectorMatch.data;
            }
         } finally {
            if (var18) {
               GL30.glBindVertexArray(0);
               GL20.glUseProgram(0);
               GL30.glBindFramebuffer(36160, 0);
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               if (var5 && var10 != null) {
                  OpenGlStateSnapshot.compute(var10);
               }
            }
         }

         GL30.glBindVertexArray(0);
         GL20.glUseProgram(0);
         GL30.glBindFramebuffer(36160, 0);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         if (var5 && var10 != null) {
            OpenGlStateSnapshot.compute(var10);
         }

         return var12;
      } else {
         return 0;
      }
   }

   private void process(int var1, int var2, int var3, float var4) {
      this.cache.handle();
      if (this.pointEncode >= 0) {
         GL20.glUniform1i(this.pointEncode, 0);
      }

      if (this.animator >= 0) {
         GL20.glUniform2f(this.animator, 1.0F / Math.max(1, var2), 1.0F / Math.max(1, var3));
      }

      if (this.source >= 0) {
         GL20.glUniform1f(this.source, var4);
      }

      if (this.handle(this.itemProject)) {
         GL11.glBindTexture(3553, var1);
         this.update();
         this.output.handle();
         if (this.target >= 0) {
            GL20.glUniform1i(this.target, 0);
         }

         if (this.pending >= 0) {
            GL20.glUniform2f(this.pending, 1.0F / Math.max(1, var2), 1.0F / Math.max(1, var3));
         }

         if (this.previous >= 0) {
            GL20.glUniform1f(this.previous, var4);
         }

         if (this.handle(this.vectorMatch)) {
            GL11.glBindTexture(3553, this.itemProject.data);
            this.update();
         }
      }
   }

   private void handle(int var1, int var2, int var3, int var4, float[] var5) {
      if (var5 != null && var5.length == var4) {
         int var6 = var1;
         int var7 = var2;
         int var8 = var3;
         this.config.handle();
         if (this.mode >= 0) {
            GL20.glUniform1i(this.mode, 0);
         }

         for (int var9 = 0; var9 < var4; var9++) {
            BlurUpsampleShader.State var10 = this.matrixBlend[var9];
            if (!this.handle(var10)) {
               return;
            }

            if (this.selection >= 0) {
               GL20.glUniform2f(this.selection, 1.0F / Math.max(1, var7), 1.0F / Math.max(1, var8));
            }

            if (this.enabled >= 0) {
               GL20.glUniform1f(this.enabled, var5[var9]);
            }

            GL11.glBindTexture(3553, var6);
            this.update();
            var6 = var10.data;
            var7 = var10.context;
            var8 = var10.config;
         }

         this.state.handle();
         if (this.renderer >= 0) {
            GL20.glUniform1i(this.renderer, 0);
         }

         for (int var11 = var4 - 2; var11 >= 0; var11--) {
            BlurUpsampleShader.State var12 = this.matrixBlend[var11];
            if (!this.handle(var12)) {
               return;
            }

            if (this.handler >= 0) {
               GL20.glUniform2f(this.handler, 1.0F / Math.max(1, var7), 1.0F / Math.max(1, var8));
            }

            if (this.animationDraw >= 0) {
               GL20.glUniform1f(this.animationDraw, var5[var11]);
            }

            GL11.glBindTexture(3553, var6);
            this.update();
            var6 = var12.data;
            var7 = var12.context;
            var8 = var12.config;
         }

         if (this.handle(this.vectorMatch)) {
            if (this.handler >= 0) {
               GL20.glUniform2f(this.handler, 1.0F / Math.max(1, var7), 1.0F / Math.max(1, var8));
            }

            if (this.animationDraw >= 0) {
               GL20.glUniform1f(this.animationDraw, var5.length > 0 ? var5[0] : 0.5F);
            }

            GL11.glBindTexture(3553, var6);
            this.update();
         }
      } else {
         throw new IllegalArgumentException("offsets length must match passCount");
      }
   }

   private void update() {
      ShaderViewportTracker.handle().handle(2);
      GL11.glDrawArrays(5, 0, 4);
   }

   private boolean handle(BlurUpsampleShader.State var1) {
      if (var1 != null && var1.instance != 0 && var1.data != 0 && var1.context > 0 && var1.config > 0) {
         GL30.glBindFramebuffer(36160, var1.instance);
         GL11.glViewport(0, 0, var1.context, var1.config);
         GL11.glDrawBuffer(36064);
         return true;
      } else {
         return false;
      }
   }

   private boolean handle(int var1, int var2, int var3) {
      if (var1 > 0 && var2 > 0 && var3 > 0) {
         for (int var4 = 0; var4 < var3; var4++) {
            int var5 = 1 << var4 + 1;
            int var6 = Math.max(1, var1 / var5);
            int var7 = Math.max(1, var2 / var5);
            if (!this.handle(this.matrixBlend[var4], var6, var7)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
   private boolean handle(BlurUpsampleShader.State var1, int var2, int var3) {
      if (var1 == null) {
         return false;
      }

      if (var2 > 0 && var3 > 0) {
         if (var1.data != 0 && (var1.context != var2 || var1.config != var3)) {
            GL11.glDeleteTextures(var1.data);
            GL30.glDeleteFramebuffers(var1.instance);
            var1.data = 0;
            var1.instance = 0;
         }

         OpenGlStateSnapshot.NetworkState var4;
         boolean var10;
         label82: {
            label100: {
               if (var1.data == 0) {
                  var4 = OpenGlStateSnapshot.handle();
                  boolean var8 = false /* VF: Semaphore variable */;

                  try {
                     var8 = true;
                     var1.data = this.handle(var2, var3);
                     if (var1.data == 0) {
                        var1.context = 0;
                        var1.config = 0;
                        var10 = false;
                        var8 = false;
                        break label82;
                     }

                     var1.instance = this.handle(var1.data);
                     if (var1.instance == 0) {
                        GL11.glDeleteTextures(var1.data);
                        var1.data = 0;
                        var1.context = 0;
                        var1.config = 0;
                        var10 = false;
                        var8 = false;
                        break label100;
                     }

                     var8 = false;
                  } finally {
                     if (var8) {
                        OpenGlStateSnapshot.compute(var4);
                     }
                  }

                  OpenGlStateSnapshot.compute(var4);
               }

               var1.context = var2;
               var1.config = var3;
               return true;
            }

            OpenGlStateSnapshot.compute(var4);
            return var10;
         }

         OpenGlStateSnapshot.compute(var4);
         return var10;
      } else {
         this.process(var1);
         return false;
      }
   }

   private void process(BlurUpsampleShader.State var1) {
      if (var1 != null) {
         if (var1.data != 0) {
            GL11.glDeleteTextures(var1.data);
            var1.data = 0;
         }

         if (var1.instance != 0) {
            GL30.glDeleteFramebuffers(var1.instance);
            var1.instance = 0;
         }

         var1.context = 0;
         var1.config = 0;
      }
   }

   private int handle(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         int var3 = GL11.glGenTextures();
         GL11.glBindTexture(3553, var3);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         VertexLayoutBinding.handle(this.current, var1, var2, 6408, this.active);
         GL11.glBindTexture(3553, 0);
         return var3;
      } else {
         return 0;
      }
   }

   private int handle(int var1) {
      if (var1 <= 0) {
         return 0;
      } else {
         int var2 = GL30.glGenFramebuffers();
         GL30.glBindFramebuffer(36160, var2);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, var1, 0);
         int var3 = GL30.glCheckFramebufferStatus(36160);
         GL30.glBindFramebuffer(36160, 0);
         if (var3 != 36053) {
            GL30.glDeleteFramebuffers(var2);
            GL11.glDeleteTextures(var1);
            throw new IllegalStateException("Blur framebuffer incomplete: status=" + var3);
         } else {
            return var2;
         }
      }
   }

   private int handle(float var1, int var2, int var3) {
      int var4 = 0;
      int var5 = var2;
      int var6 = var3;

      while (var4 < 6 && (var5 > 1 || var6 > 1)) {
         var5 = Math.max(1, var5 / 2);
         var6 = Math.max(1, var6 / 2);
         var4++;
         if (var5 == 1 && var6 == 1) {
            break;
         }
      }

      if (var4 == 0) {
         var4 = 1;
      }

      int var7 = Math.max(1, (int)Math.ceil(Math.sqrt(var1 / 2.0F)));
      return Math.min(var4, var7);
   }

   private float[] handle(int var1, float var2) {
      float[] var3 = new float[var1];

      for (int var4 = 0; var4 < var1; var4++) {
         float var5 = 1.0F / (1 << var4);
         float var6 = var2 / var1;
         var3[var4] = Math.max(0.5F, var6 * var5 * 2.0F + 0.5F);
      }

      return var3;
   }

   static final class State {
      int instance;
      int data;
      int context;
      int config;
   }
}
