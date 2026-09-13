package ru.wild.render;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.wild.util.render.ColorCompositor;

public final class WorldQuadBuilder {
   private static final float instance = 1.0E-6F;
   private final Camera data;
   private final Matrix4f context;
   private final Matrix3f config;
   private final VertexConsumer state;
   private final Vec3d cache;

   public WorldQuadBuilder(WorldRenderContext var1, Entry var2, VertexConsumer var3) {
      this(Objects.requireNonNull(var1, "renderer").handle(), var2, var3);
   }

   public WorldQuadBuilder(Camera var1, Entry var2, VertexConsumer var3) {
      this.data = Objects.requireNonNull(var1, "camera");
      Objects.requireNonNull(var2, "entry");
      this.state = Objects.requireNonNull(var3, "consumer");
      this.cache = this.data.getPos();
      this.context = new Matrix4f(var2.getPositionMatrix());
      this.config = new Matrix3f(var2.getNormalMatrix());
   }

   public void handle(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, int var5) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5);
   }

   public void handle(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, int var5, int var6, int var7, int var8) {
      Objects.requireNonNull(var1, "v0");
      Objects.requireNonNull(var2, "v1");
      Objects.requireNonNull(var3, "v2");
      Objects.requireNonNull(var4, "v3");
      this.handle(var1, var5);
      this.handle(var2, var6);
      this.handle(var3, var7);
      this.handle(var4, var8);
   }

   public void handle(
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      int var25
   ) {
      this.handle(new Vec3d(var1, var3, var5), new Vec3d(var7, var9, var11), new Vec3d(var13, var15, var17), new Vec3d(var19, var21, var23), var25);
   }

   public void handle(
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      float var25,
      float var26,
      float var27,
      float var28,
      float var29,
      float var30,
      float var31,
      float var32,
      int var33
   ) {
      this.handle(
         new Vec3d(var1, var3, var5),
         new Vec3d(var7, var9, var11),
         new Vec3d(var13, var15, var17),
         new Vec3d(var19, var21, var23),
         var25,
         var26,
         var27,
         var28,
         var29,
         var30,
         var31,
         var32,
         var33
      );
   }

   public void handle(Vec3d var1, Vec3d var2, int var3) {
      Objects.requireNonNull(var1, "min");
      Objects.requireNonNull(var2, "max");
      if (!(var1.x > var2.x) && !(var1.y > var2.y) && !(var1.z > var2.z)) {
         Vec3d var4 = new Vec3d(var1.x, var1.y, var1.z);
         Vec3d var5 = new Vec3d(var1.x, var1.y, var2.z);
         Vec3d var6 = new Vec3d(var1.x, var2.y, var1.z);
         Vec3d var7 = new Vec3d(var1.x, var2.y, var2.z);
         Vec3d var8 = new Vec3d(var2.x, var1.y, var1.z);
         Vec3d var9 = new Vec3d(var2.x, var1.y, var2.z);
         Vec3d var10 = new Vec3d(var2.x, var2.y, var1.z);
         Vec3d var11 = new Vec3d(var2.x, var2.y, var2.z);
         this.handle(var4, var8, var10, var6, var3);
         this.handle(var5, var7, var11, var9, var3);
         this.handle(var4, var5, var9, var8, var3);
         this.handle(var6, var10, var11, var7, var3);
         this.handle(var4, var6, var7, var5, var3);
         this.handle(var8, var9, var11, var10, var3);
      } else {
         throw new IllegalArgumentException("Minimum corner must be less than or equal to maximum corner.");
      }
   }

   public void process(Vec3d var1, Vec3d var2, int var3) {
      this.handle(var1, var2, var3, var3);
   }

   public void handle(Vec3d var1, Vec3d var2, int var3, int var4) {
      Objects.requireNonNull(var1, "start");
      Objects.requireNonNull(var2, "end");
      Vector3f var5 = this.handle(var1, var2);
      this.handle(var1, var3, var5);
      this.handle(var2, var4, var5);
   }

   public void handle(
      Vec3d var1,
      Vec3d var2,
      Vec3d var3,
      Vec3d var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13
   ) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var13, var13, var13);
   }

   public void handle(
      Vec3d var1,
      Vec3d var2,
      Vec3d var3,
      Vec3d var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      int var14,
      int var15,
      int var16
   ) {
      Objects.requireNonNull(var1, "v0");
      Objects.requireNonNull(var2, "v1");
      Objects.requireNonNull(var3, "v2");
      Objects.requireNonNull(var4, "v3");
      this.handle(var1, var5, var6, var13);
      this.handle(var2, var7, var8, var14);
      this.handle(var3, var9, var10, var15);
      this.handle(var4, var11, var12, var16);
   }

   private void handle(Vec3d var1, int var2) {
      Vec3d var3 = this.handle(var1);
      VertexConsumer var4 = this.state.vertex(this.context, (float)var3.x, (float)var3.y, (float)var3.z);
      var4.color(ColorCompositor.process(var2), ColorCompositor.compute(var2), ColorCompositor.resolve(var2), ColorCompositor.handle(var2));
      this.handle(var4);
   }

   private void handle(Vec3d var1, float var2, float var3, int var4) {
      Vec3d var5 = this.handle(var1);
      VertexConsumer var6 = this.state.vertex(this.context, (float)var5.x, (float)var5.y, (float)var5.z);
      var6.texture(var2, var3);
      var6.color(ColorCompositor.process(var4), ColorCompositor.compute(var4), ColorCompositor.resolve(var4), ColorCompositor.handle(var4));
      this.handle(var6);
   }

   private void handle(Vec3d var1, int var2, Vector3f var3) {
      Vec3d var4 = this.handle(var1);
      VertexConsumer var5 = this.state.vertex(this.context, (float)var4.x, (float)var4.y, (float)var4.z);
      var5.color(ColorCompositor.process(var2), ColorCompositor.compute(var2), ColorCompositor.resolve(var2), ColorCompositor.handle(var2));
      var5.normal(var3.x, var3.y, var3.z);
      this.handle(var5);
   }

   private void handle(VertexConsumer var1) {
      Objects.requireNonNull(var1, "vertex");

      try {
         Method var2 = var1.getClass().getMethod("next");
         var2.invoke(var1);
      } catch (NoSuchMethodException var5) {
      } catch (IllegalAccessException var6) {
         throw new IllegalStateException("Unable to access vertex finalization method", var6);
      } catch (InvocationTargetException var7) {
         Throwable var3 = var7.getCause();
         if (var3 instanceof RuntimeException var8) {
            throw var8;
         }

         if (var3 instanceof Error var4) {
            throw var4;
         }

         throw new IllegalStateException("Vertex finalization failed", var3);
      }
   }

   private Vec3d handle(Vec3d var1) {
      return var1.subtract(this.cache);
   }

   private Vector3f handle(Vec3d var1, Vec3d var2) {
      Vec3d var3 = var2.subtract(var1);
      Vector3f var4 = new Vector3f((float)var3.x, (float)var3.y, (float)var3.z);
      if (var4.lengthSquared() <= 1.0E-6F) {
         var4.set(0.0F, 1.0F, 0.0F);
      }

      var4.normalize();
      this.config.transform(var4);
      if (var4.lengthSquared() <= 1.0E-6F) {
         var4.set(0.0F, 1.0F, 0.0F);
      }

      var4.normalize();
      return var4;
   }
}
