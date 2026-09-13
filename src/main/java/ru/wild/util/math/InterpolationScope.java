package ru.wild.util.math;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.wild.core.AnimationClock;

public class InterpolationScope {
   public static double instance;

   public static float handle(float var0, float var1, float var2) {
      float var3 = (var1 - var0) * MathHelper.clamp(AnimationClock.handle().compute() * 15.0F, 0.0F, 1.0F);
      if (var3 > 0.0F) {
         var3 = Math.max(var2, var3);
         var3 = Math.min(var1 - var0, var3);
      } else if (var3 < 0.0F) {
         var3 = Math.min(-var2, var3);
         var3 = Math.max(var1 - var0, var3);
      }

      return var0 + var3;
   }

   public static double handle(double var0, double var2, double var4) {
      double var6 = (var2 - var0) * MathHelper.clamp((float)(AnimationClock.handle().compute() * var4), 0.0F, 1.0F);
      if (var6 > 0.0) {
         var6 = Math.max(var4, var6);
         var6 = Math.min(var2 - var0, var6);
      } else if (var6 < 0.0) {
         var6 = Math.min(-var4, var6);
         var6 = Math.max(var2 - var0, var6);
      }

      return var0 + var6;
   }

   public static float handle(float var0, float var1, float var2, double var3) {
      float var5 = var1 - var0;
      if (var2 < 1.0F) {
         var2 = 1.0F;
      }

      if (var2 > 1000.0F) {
         var2 = 16.0F;
      }

      double var6 = Math.max(var3 * var2 / 16.666666F, 0.5);
      if (var5 > var3) {
         if ((var1 = var1 - (float)var6) < var0) {
            var1 = var0;
         }
      } else if (var5 < -var3) {
         if ((var1 = var1 + (float)var6) > var0) {
            var1 = var0;
         }
      } else {
         var1 = var0;
      }

      return var1;
   }

   public static float handle(float var0, float var1, float var2, float var3, float var4) {
      float var5 = (var1 - var0) * MathHelper.clamp(var4, 0.0F, 1.0F);
      if (var5 < 0.0F) {
         var5 = MathHelper.clamp(var5, -var3, -var2);
      } else {
         var5 = MathHelper.clamp(var5, var2, var3);
      }

      return Math.abs(var5) > Math.abs(var1 - var0) ? var1 : var0 + var5;
   }

   public static double process(double var0, double var2, double var4) {
      return var0 + (var2 - var0) * var4;
   }

   public static float process(float var0, float var1, float var2) {
      float var3 = (float)(instance * (var2 / 1000.0F));
      if (var0 < var1) {
         if (var0 + var3 < var1) {
            var0 += var3;
         } else {
            var0 = var1;
         }
      } else if (var0 - var3 > var1) {
         var0 -= var3;
      } else {
         var0 = var1;
      }

      return var0;
   }

   public static void handle(float var0, float var1, float var2, Runnable var3) {
      GL11.glPushMatrix();
      GL11.glTranslatef(var0, var1, 0.0F);
      GL11.glScalef(var2, var2, 1.0F);
      GL11.glTranslatef(-var0, -var1, 0.0F);
      var3.run();
      GL11.glPopMatrix();
   }

   public static void handle(float var0, float var1, Runnable var2) {
      GL11.glPushMatrix();
      GL11.glTranslatef(var0, var1, 0.0F);
      var2.run();
      GL11.glPopMatrix();
   }
}
