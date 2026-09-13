package ru.wild.render.texture;

import java.util.Arrays;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public final class FramebufferAttachments implements AutoCloseable {
   private final int instance;
   private final int data;
   private final int[] context;
   private final int[] config;
   private boolean state;

   private FramebufferAttachments(int var1, int var2, int[] var3, int[] var4) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
   }

   public static FramebufferAttachments handle(int var0, int... var1) {
      int var2 = Math.max(0, GL11.glGetInteger(34016) - 33984);
      int[] var3 = handle(var1);
      int[] var4 = new int[var3.length];
      if (var3.length > 0) {
         GL13.glActiveTexture(33984 + var0);

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = GL11.glGetInteger(handle(var3[var5]));
         }

         GL13.glActiveTexture(33984 + var2);
      }

      return new FramebufferAttachments(var0, var2, var3, var4);
   }

   @Override
   public void close() {
      if (!this.state) {
         this.state = true;
         if (this.context.length > 0) {
            GL13.glActiveTexture(33984 + this.instance);

            for (int var1 = 0; var1 < this.context.length; var1++) {
               GL11.glBindTexture(this.context[var1], this.config[var1]);
            }
         }

         GL13.glActiveTexture(33984 + this.data);
      }
   }

   private static int[] handle(int[] var0) {
      if (var0 != null && var0.length != 0) {
         int[] var1 = Arrays.copyOf(var0, var0.length);
         int var2 = 0;

         for (int var6 : var1) {
            if (var6 > 0) {
               boolean var7 = false;

               for (int var8 = 0; var8 < var2; var8++) {
                  if (var1[var8] == var6) {
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  var1[var2++] = var6;
               }
            }
         }

         return Arrays.copyOf(var1, var2);
      } else {
         return new int[0];
      }
   }

   private static int handle(int var0) {
      switch (var0) {
         case 3552:
            return 32872;
         case 3553:
            return 32873;
         case 32879:
            return 32874;
         case 34037:
            return 34038;
         case 34067:
            return 34068;
         case 35864:
            return 35868;
         case 35866:
            return 35869;
         case 35882:
            return 35884;
         case 36873:
            return 36874;
         default:
            return 32873;
      }
   }
}
