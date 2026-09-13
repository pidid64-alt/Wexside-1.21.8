package ru.wild.render.texture;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;

public final class OffscreenRenderTarget implements AutoCloseable {
   private static int instance;
   private int data;
   private int context;
   private int config;
   private int state;
   private int cache = 32856;

   private static int execute() {
      if (instance <= 0) {
         instance = Math.max(1, GL11.glGetInteger(3379));
      }

      return instance;
   }

   public void handle(int var1, int var2) {
      this.handle(var1, var2, 32856);
   }

   public void process(int var1, int var2) {
      this.handle(var1, var2, 34842);
   }
   private void handle(int var1, int var2, int var3) {
      if (this.data == 0 || this.context == 0 || this.config != var1 || this.state != var2 || this.cache != var3) {
         if (GLFW.glfwGetCurrentContext() == 0L) {
            this.check();
         } else if (var1 > 0 && var2 > 0) {
            int var4 = execute();
            int var5 = Math.max(1, Math.min(var1, var4));
            int var6 = Math.max(1, Math.min(var2, var4));
            if (this.data == 0 || this.context == 0 || this.config != var5 || this.state != var6 || this.cache != var3) {
               int var7 = GL11.glGetInteger(36006);
               int var8 = GL11.glGetInteger(36010);
               int var9 = GL11.glGetInteger(32873);
               int var10 = this.context;
               this.prepare();
               if (var9 == var10) {
                  var9 = 0;
               }

               this.config = var5;
               this.state = var6;
               this.cache = var3;
               boolean var15 = false /* VF: Semaphore variable */;

               try {
                  var15 = true;
                  this.context = GL11.glGenTextures();
                  GL11.glBindTexture(3553, this.context);
                  GL11.glTexParameteri(3553, 10241, 9729);
                  GL11.glTexParameteri(3553, 10240, 9729);
                  GL11.glTexParameteri(3553, 10242, 33071);
                  GL11.glTexParameteri(3553, 10243, 33071);
                  VertexLayoutBinding.handle(this.cache, this.config, this.state, 6408, this.cache == 34842 ? 5131 : 5121);
                  this.data = GL30.glGenFramebuffers();
                  GL30.glBindFramebuffer(36160, this.data);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, this.context, 0);
                  GL11.glDrawBuffer(36064);
                  if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                     this.prepare();
                     var15 = false;
                  } else {
                     float[] var11 = new float[4];
                     GL11.glGetFloatv(3106, var11);
                     boolean var12 = GL11.glIsEnabled(3089);
                     GL11.glDisable(3089);
                     GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                     GL11.glClear(16384);
                     if (var12) {
                        GL11.glEnable(3089);
                     }

                     GL11.glClearColor(var11[0], var11[1], var11[2], var11[3]);
                     var15 = false;
                  }
               } finally {
                  if (var15) {
                     OpenGlStateSnapshot.handle(36008, var8);
                     OpenGlStateSnapshot.handle(36009, var7);
                     GL11.glBindTexture(3553, var9);
                  }
               }

               OpenGlStateSnapshot.handle(36008, var8);
               OpenGlStateSnapshot.handle(36009, var7);
               GL11.glBindTexture(3553, var9);
            }
         } else {
            this.prepare();
         }
      }
   }

   public void handle() {
      if (this.apply()) {
         GL30.glBindFramebuffer(36160, this.data);
         GL11.glViewport(0, 0, this.config, this.state);
      }
   }

   public int process() {
      return this.data;
   }

   public int compute() {
      return this.context;
   }

   public int resolve() {
      return this.config;
   }

   public int update() {
      return this.state;
   }

   public boolean apply() {
      return this.data != 0 && this.context != 0 && this.config > 0 && this.state > 0;
   }

   private void prepare() {
      if (GLFW.glfwGetCurrentContext() == 0L) {
         this.check();
      } else {
         if (this.data != 0) {
            GL30.glDeleteFramebuffers(this.data);
            this.data = 0;
         }

         if (this.context != 0) {
            GL11.glDeleteTextures(this.context);
            this.context = 0;
         }

         this.config = 0;
         this.state = 0;
      }
   }

   private void check() {
      this.data = 0;
      this.context = 0;
      this.config = 0;
      this.state = 0;
   }

   @Override
   public void close() {
      this.prepare();
   }
}
