package ru.wild.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import ru.wild.core.MinecraftContext;

public final class ClientMathUtil implements MinecraftContext {
   public static final Matrix4f instance = new Matrix4f();
   public static final Matrix4f data = new Matrix4f();
   public static final Matrix4f context = new Matrix4f();

   public static float handle(float var0, float var1) {
      return var0 - var1 / 2.0F;
   }

   public static float handle(float var0) {
      var0 %= 360.0F;
      if (var0 >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   public static Vec3d handle(Vec3d var0) {
      Camera var1 = toggleState.gameRenderer == null ? null : toggleState.gameRenderer.getCamera();
      if (var1 == null && toggleState.getEntityRenderDispatcher() != null) {
         var1 = toggleState.getEntityRenderDispatcher().camera;
      }

      if (var0 != null && var1 != null && toggleState.getWindow() != null) {
         int var2 = toggleState.getWindow().getHeight();
         int[] var3 = new int[4];
         GL11.glGetIntegerv(2978, var3);
         Vector3f var4 = new Vector3f();
         double var5 = var0.x - var1.getPos().x;
         double var7 = var0.y - var1.getPos().y;
         double var9 = var0.z - var1.getPos().z;
         Vector4f var11 = new Vector4f((float)var5, (float)var7, (float)var9, 1.0F).mul(context);
         Matrix4f var12 = new Matrix4f(instance);
         Matrix4f var13 = new Matrix4f(data);
         var12.mul(var13).project(var11.x(), var11.y(), var11.z(), var3, var4);
         return new Vec3d(var4.x, var2 - var4.y, var4.z);
      } else {
         return new Vec3d(0.0, 0.0, 2.0);
      }
   }

   public static double handle() {
      return toggleState.getWindow().getScaleFactor();
   }

   public static float handle(float var0, float var1, float var2, float var3, float var4) {
      return var2 - var1 == 0.0F ? var3 : var3 + (var4 - var3) * ((var0 - var1) / (var2 - var1));
   }

   private static void apply(double var0, double var2) {
      if (var2 < var0) {
         throw new IllegalArgumentException("max не может быть меньше min.");
      }
   }

   public static double handle(double var0, int var2) {
      return Math.round(var0 * Math.pow(10.0, var2)) / Math.pow(10.0, var2);
   }

   public static float handle(float var0, float var1, float var2) {
      return (var0 - var1) / (var2 - var1);
   }

   public static Vector2f handle(Entity var0) {
      Vec3d var1 = var0.getPos().subtract(MinecraftClient.getInstance().player.getPos());
      double var2 = Math.hypot(var1.x, var1.z);
      return new Vector2f((float)Math.toDegrees(Math.atan2(var1.z, var1.x)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(var1.y, var2))));
   }

   static float process(float var0) {
      if ((var0 = var0 % 360.0F) >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   public static float compute(float var0) {
      return (var0 > 0.5 ? 1.0F - var0 : var0) * 2.0F;
   }

   public static double handle(double var0, double var2, double var4) {
      return var0 + var4 * (var2 - var0);
   }

   public static int handle(int var0, int var1, float var2) {
      return var0 + (int)(var2 * (var1 - var0));
   }

   public static float process(float var0, float var1, float var2) {
      return var0 + var2 * (var1 - var0);
   }

   public static boolean handle(double var0, double var2, float var4, float var5, float var6, float var7) {
      return var0 >= var4 && var0 <= var4 + var6 && var2 >= var5 && var2 <= var5 + var7;
   }

   public static double process(double var0, double var2, double var4) {
      return var2 + (var0 - var2) * var4;
   }

   public static Vec3d handle(Vec3d var0, Vec3d var1, float var2) {
      return new Vec3d(process(var0.getX(), var1.getX(), var2), process(var0.getY(), var1.getY(), var2), process(var0.getZ(), var1.getZ(), var2));
   }

   public static double compute(double var0, double var2, double var4) {
      return var2 + var0 * (var4 - var2);
   }

   public static int handle(int var0, int var1) {
      return var0 + (int)(Math.random() * (var1 - var0 + 1));
   }

   public static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 > var2 && var0 < var2 + var4 && var1 > var3 && var1 < var3 + var5;
   }

   public static float process(float var0, float var1) {
      return (float)(Math.random() * (var1 - var0) + var0);
   }

   public static double handle(double var0, double var2, long var4, Stopwatch var6) {
      double var7 = 0.0;
      if (var6.handle(var4)) {
         var7 = process((float)var0, (float)var2);
         var6.handle();
      }

      return var7;
   }

   public static float compute(float var0, float var1, float var2) {
      return (1.0F - MathHelper.clamp(process() * var2, 0.0F, 1.0F)) * var0 + MathHelper.clamp(process() * var2, 0.0F, 1.0F) * var1;
   }

   public static double handle(double var0, double var2) {
      if (var0 == var2) {
         return var0;
      }

      if (var0 > var2) {
         double var4 = var0;
         var0 = var2;
         var2 = var4;
      }

      return ThreadLocalRandom.current().nextDouble() * (var2 - var0) + var0;
   }

   public static float compute(float var0, float var1) {
      return (float)(Math.random() * (var1 - var0) + var0);
   }

   public static double process(double var0, double var2) {
      apply(var0, var2);
      return var0 + ThreadLocalRandom.current().nextDouble() * (var2 - var0);
   }

   public static float resolve(float var0, float var1) {
      apply(var0, var1);
      return var0 + ThreadLocalRandom.current().nextFloat() * (var1 - var0);
   }

   public static double compute(double var0, double var2) {
      return var0 - var2;
   }

   public static float process() {
      float var0 = toggleState.getCurrentFps();
      return var0 > 0.0F ? 1.0F / var0 : 1.0F;
   }

   public static String handle(long var0) {
      long var2 = var0 / 3600000L;
      long var4 = var0 % 3600000L / 60000L;
      long var6 = var0 % 360000L % 60000L / 1000L;
      return String.format("%02d:%02d:%02d", var2, var4, var6);
   }

   public static double process(double var0, int var2) {
      double var3 = Math.pow(10.0, var2);
      return Math.round(var0 * var3) / var3;
   }

   public static double resolve(double var0, double var2) {
      double var4 = Math.round(var0 / var2) * var2;
      BigDecimal var6 = new BigDecimal(var4);
      var6 = var6.setScale(2, RoundingMode.HALF_UP);
      return var6.doubleValue();
   }

   public static double handle(double var0) {
      return Math.round(var0 * 100.0) / 100.0;
   }

   public static double update(double var0, double var2) {
      double var4 = Math.round(var0 / var2) * var2;
      return Math.round(var4 * 100.0) / 100.0;
   }

   public static double resolve(double var0, double var2, double var4) {
      return Math.max(var0, Math.min(var2, var4));
   }

   public static float resolve(float var0, float var1, float var2) {
      return Math.max(var0, Math.min(var1, var2));
   }

   public static int handle(int var0, int var1, int var2) {
      return Math.max(var0, Math.min(var1, var2));
   }

   public static double process(double var0) {
      return resolve(0.0, 1.0, var0);
   }

   public static float resolve(float var0) {
      return resolve(0.0F, 1.0F, var0);
   }

   public static double handle(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = compute(var6, var0);
      double var14 = compute(var8, var2);
      double var16 = compute(var10, var4);
      return MathHelper.sqrt((float)(var12 * var12 + var14 * var14 + var16 * var16));
   }

   public static double handle(BlockPos var0, BlockPos var1) {
      double var2 = compute(var0.getX(), var1.getX());
      double var4 = compute(var0.getY(), var1.getY());
      double var6 = compute(var0.getZ(), var1.getZ());
      return MathHelper.sqrt((float)(var2 * var2 + var4 * var4 + var6 * var6));
   }

   public static float process(float var0, float var1, float var2, float var3, float var4) {
      var0 = resolve(var1, var2, var0);
      float var5 = (var0 - var1) / (var2 - var1);
      return NumberInterpolator.handle(var3, var4, var5);
   }
   private ClientMathUtil() {
   }
}
