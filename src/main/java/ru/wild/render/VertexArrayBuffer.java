package ru.wild.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class VertexArrayBuffer implements AutoCloseable {
   private final int instance = GL30.glGenVertexArrays();
   private final int data = GL15.glGenBuffers();

   public VertexArrayBuffer() {
      GL30.glBindVertexArray(this.instance);
      GL15.glBindBuffer(34962, this.data);
      float[] var1 = new float[]{0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F};
      GL15.glBufferData(34962, var1, 35044);
      GL20.glEnableVertexAttribArray(0);
      GL20.glVertexAttribPointer(0, 2, 5126, false, 8, 0L);
      GL15.glBindBuffer(34962, 0);
      GL30.glBindVertexArray(0);
   }

   public void handle() {
      GL30.glBindVertexArray(this.instance);
      GL11.glDrawArrays(4, 0, 6);
      GL30.glBindVertexArray(0);
   }

   @Override
   public void close() {
      GL30.glDeleteVertexArrays(this.instance);
      GL15.glDeleteBuffers(this.data);
   }
}
