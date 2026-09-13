package ru.wild.render.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;

public final class DepthRenderTarget {
   public int instance = 0;
   public int data = 0;
   public int context = 0;
   public int config = 0;
   public int state = 0;

   public void handle(int var1, int var2) {
      if (var1 <= 0 || var2 <= 0) {
         this.handle();
      } else if (this.instance == 0 || this.data == 0 || this.context == 0 || this.config != var1 || this.state != var2) {
         this.handle();
         this.config = var1;
         this.state = var2;
         OpenGlStateSnapshot.NetworkState var3 = OpenGlStateSnapshot.handle();

         int var4;
         try {
            this.data = GL11.glGenTextures();
            GL11.glBindTexture(3553, this.data);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            VertexLayoutBinding.handle(32856, this.config, this.state, 6408, 5121);
            this.context = GL11.glGenTextures();
            GL11.glBindTexture(3553, this.context);
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 34892, 0);
            VertexLayoutBinding.handle(33190, this.config, this.state, 6402, 5125);
            this.instance = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, this.instance);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, this.data, 0);
            GL30.glFramebufferTexture2D(36160, 36096, 3553, this.context, 0);
            GL11.glDrawBuffer(36064);
            GL11.glReadBuffer(36064);
            var4 = GL30.glCheckFramebufferStatus(36160);
         } finally {
            OpenGlStateSnapshot.compute(var3);
         }

         if (var4 != 36053) {
            this.handle();
            throw new IllegalStateException("DepthRenderTarget incomplete: status=" + var4);
         }
      }
   }

   public void handle() {
      if (this.instance != 0) {
         GL30.glDeleteFramebuffers(this.instance);
         this.instance = 0;
      }

      if (this.data != 0) {
         GL11.glDeleteTextures(this.data);
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
