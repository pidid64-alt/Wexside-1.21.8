package ru.wild.util.render;

import java.util.ArrayDeque;
import java.util.Arrays;

public final class AffineTransformStack {
   private final ArrayDeque<float[]> instance = new ArrayDeque<>();

   public AffineTransformStack() {
      this.process();
   }

   public void handle() {
      this.instance.clear();
      this.process();
   }

   public void process() {
      this.instance.push(new float[]{1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F});
   }

   public void handle(float var1) {
      float var2 = (float)Math.toRadians(var1);
      float var3 = (float)Math.cos(var2);
      float var4 = (float)Math.sin(var2);
      float[] var5 = new float[]{var3, -var4, 0.0F, var4, var3, 0.0F, 0.0F, 0.0F, 1.0F};
      float[] var6 = this.instance.peek();
      this.instance.push(handle(var6, var5));
   }

   public void handle(float var1, float var2) {
      float[] var3 = new float[]{1.0F, 0.0F, var1, 0.0F, 1.0F, var2, 0.0F, 0.0F, 1.0F};
      float[] var4 = this.instance.peek();
      this.instance.push(handle(var4, var3));
   }

   public void process(float var1, float var2) {
      this.handle(-var1, -var2);
   }

   public void handle(float var1, float var2, float var3, float var4) {
      float var5 = var3 - var3 * var1;
      float var6 = var4 - var4 * var2;
      float[] var7 = new float[]{var1, 0.0F, var5, 0.0F, var2, var6, 0.0F, 0.0F, 1.0F};
      float[] var8 = this.instance.peek();
      this.instance.push(handle(var8, var7));
   }

   public void handle(float var1, float var2, float var3) {
      this.handle(var1, var1, var2, var3);
   }

   public void handle(float[] var1) {
      if (var1 != null && var1.length == 9) {
         for (float var5 : var1) {
            if (!Float.isFinite(var5)) {
               throw new IllegalArgumentException("matrix entries must be finite");
            }
         }

         this.instance.push(handle(this.instance.peek(), var1));
      } else {
         throw new IllegalArgumentException("matrix must have length 9");
      }
   }

   public void process(float[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("matrix must not be null");
      }

      if (var1.length != 9) {
         throw new IllegalArgumentException("matrix must have length 9");
      }

      for (float var5 : var1) {
         if (!Float.isFinite(var5)) {
            throw new IllegalArgumentException("matrix entries must be finite");
         }
      }

      if (this.instance.isEmpty()) {
         throw new IllegalStateException("cannot replace top matrix on an empty stack");
      }

      float[] var6 = Arrays.copyOf(var1, var1.length);
      this.instance.pop();
      this.instance.push(var6);
   }

   public ArrayDeque<float[]> compute() {
      ArrayDeque var1 = new ArrayDeque();

      for (float[] var3 : this.instance) {
         var1.addLast(Arrays.copyOf(var3, var3.length));
      }

      return var1;
   }

   public void handle(ArrayDeque<float[]> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("target must not be null");
      }

      var1.clear();
      var1.addAll(this.instance);
   }

   public void process(ArrayDeque<float[]> var1) {
      this.instance.clear();
      if (var1 != null) {
         for (float[] var3 : var1) {
            if (var3 != null && var3.length == 9) {
               this.instance.addLast(Arrays.copyOf(var3, var3.length));
            }
         }
      }

      if (this.instance.isEmpty()) {
         this.process();
      }
   }

   public void compute(ArrayDeque<float[]> var1) {
      this.instance.clear();
      if (var1 != null) {
         this.instance.addAll(var1);
      }

      if (this.instance.isEmpty()) {
         this.process();
      }
   }

   public void handle(float[] var1, float var2, float var3) {
      if (var1 != null && var1.length == 9) {
         var1[0] = 1.0F;
         var1[1] = 0.0F;
         var1[2] = var2;
         var1[3] = 0.0F;
         var1[4] = 1.0F;
         var1[5] = var3;
         var1[6] = 0.0F;
         var1[7] = 0.0F;
         var1[8] = 1.0F;
         this.instance.clear();
         this.instance.push(var1);
      } else {
         throw new IllegalArgumentException("matrix must have length 9");
      }
   }

   public void resolve() {
      if (this.instance.size() > 1) {
         this.instance.pop();
      }
   }

   public void handle(int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         if (this.instance.size() > 1) {
            this.instance.pop();
         }
      }
   }

   public float[] update() {
      return this.instance.peek();
   }

   private static float[] handle(float[] var0, float[] var1) {
      return new float[]{
         var0[0] * var1[0] + var0[1] * var1[3] + var0[2] * var1[6],
         var0[0] * var1[1] + var0[1] * var1[4] + var0[2] * var1[7],
         var0[0] * var1[2] + var0[1] * var1[5] + var0[2] * var1[8],
         var0[3] * var1[0] + var0[4] * var1[3] + var0[5] * var1[6],
         var0[3] * var1[1] + var0[4] * var1[4] + var0[5] * var1[7],
         var0[3] * var1[2] + var0[4] * var1[5] + var0[5] * var1[8],
         var0[6] * var1[0] + var0[7] * var1[3] + var0[8] * var1[6],
         var0[6] * var1[1] + var0[7] * var1[4] + var0[8] * var1[7],
         var0[6] * var1[2] + var0[7] * var1[5] + var0[8] * var1[8]
      };
   }
}
