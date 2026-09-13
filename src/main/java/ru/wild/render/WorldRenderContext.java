package ru.wild.render;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class WorldRenderContext implements AutoCloseable {
   private static final int instance = 262144;
   private static final BufferAllocator data = new BufferAllocator(262144);
   private static final Immediate context = VertexConsumerProvider.immediate(data);
   private final Camera config;
   private final MatrixStack state;
   private final Matrix4f cache;
   private final Matrix4f output;
   private final Matrix4f current;
   private final BufferAllocator active;
   private final Immediate mode;
   private final float selection;
   private boolean enabled;

   private WorldRenderContext(Camera var1, MatrixStack var2, Matrix4f var3, Matrix4f var4, Matrix4f var5, BufferAllocator var6, Immediate var7, float var8) {
      this.config = var1;
      this.state = var2;
      this.cache = var3;
      this.output = var4;
      this.current = var5;
      this.active = var6;
      this.mode = var7;
      this.selection = var8;
   }

   public static WorldRenderContext handle(MinecraftClient var0, RenderTickCounter var1, Camera var2, Matrix4f var3, Matrix4f var4) {
      Objects.requireNonNull(var0, "client");
      Objects.requireNonNull(var1, "tickCounter");
      Objects.requireNonNull(var2, "camera");
      Objects.requireNonNull(var3, "positionMatrix");
      Objects.requireNonNull(var4, "projectionMatrix");
      MatrixStack var5 = new MatrixStack();
      Matrix4f var6 = new Matrix4f(var3);
      Matrix4f var7 = new Matrix4f(var6);
      var5.multiplyPositionMatrix(new Matrix4f(var6));
      data.clear();
      BufferAllocator var8 = data;
      Immediate var9 = context;
      float var10 = var1.getTickProgress(false);
      return new WorldRenderContext(var2, var5, var6, var7, new Matrix4f(var4), var8, var9, var10);
   }

   public Camera handle() {
      return this.config;
   }

   public MatrixStack process() {
      return this.state;
   }

   public Matrix4f compute() {
      return new Matrix4f(this.cache);
   }

   public Matrix4f resolve() {
      return new Matrix4f(this.output);
   }

   public Matrix4f update() {
      return new Matrix4f(this.current);
   }

   public float apply() {
      return this.selection;
   }

   public Immediate execute() {
      if (this.enabled) {
         throw new IllegalStateException("Cannot access buffers after the world renderer has been closed.");
      } else {
         return this.mode;
      }
   }

   public VertexConsumer handle(RenderLayer var1) {
      Objects.requireNonNull(var1, "layer");
      if (this.enabled) {
         throw new IllegalStateException("Cannot request buffers after the world renderer has been closed.");
      } else {
         return this.mode.getBuffer(var1);
      }
   }

   public void handle(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, int var5, boolean var6) {
      Objects.requireNonNull(var1, "v0");
      Objects.requireNonNull(var2, "v1");
      Objects.requireNonNull(var3, "v2");
      Objects.requireNonNull(var4, "v3");
      RenderLayer var7 = var6 ? TexturedQuadsRenderer.handle() : TexturedQuadsRenderer.process();
      WorldQuadBuilder var8 = new WorldQuadBuilder(this, this.state.peek(), this.handle(var7));
      var8.handle(var1, var2, var3, var4, var5);
   }

   public void handle(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, int var5, int var6, int var7, int var8) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public void handle(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, int var5, int var6, int var7, int var8, boolean var9) {
      Objects.requireNonNull(var1, "v0");
      Objects.requireNonNull(var2, "v1");
      Objects.requireNonNull(var3, "v2");
      Objects.requireNonNull(var4, "v3");
      RenderLayer var10 = var9 ? TexturedQuadsRenderer.resolve() : TexturedQuadsRenderer.update();
      WorldQuadBuilder var11 = new WorldQuadBuilder(this, this.state.peek(), this.handle(var10));
      var11.handle(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void handle(Vec3d var1, Vec3d var2, int var3, boolean var4) {
      Objects.requireNonNull(var1, "min");
      Objects.requireNonNull(var2, "max");
      RenderLayer var5 = var4 ? TexturedQuadsRenderer.handle() : TexturedQuadsRenderer.compute();
      WorldQuadBuilder var6 = new WorldQuadBuilder(this, this.state.peek(), this.handle(var5));
      var6.handle(var1, var2, var3);
   }

   public void handle(Vec3d var1, Vec3d var2, double var3, int var5, boolean var6) {
      Objects.requireNonNull(var1, "start");
      Objects.requireNonNull(var2, "end");
      if (!Double.isFinite(var3)) {
         throw new IllegalArgumentException("Line width must be finite.");
      }

      if (var3 < 0.0) {
         throw new IllegalArgumentException("Line width cannot be negative.");
      }

      RenderLayer var7 = var6 ? TexturedQuadsRenderer.handle(var3) : TexturedQuadsRenderer.process(var3);
      WorldQuadBuilder var8 = new WorldQuadBuilder(this, this.state.peek(), this.handle(var7));
      var8.process(var1, var2, var5);
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
      Objects.requireNonNull(var1, "v0");
      Objects.requireNonNull(var2, "v1");
      Objects.requireNonNull(var3, "v2");
      Objects.requireNonNull(var4, "v3");
      RenderLayer var14 = TexturedQuadsRenderer.apply();
      WorldQuadBuilder var15 = new WorldQuadBuilder(this, this.state.peek(), this.handle(var14));
      var15.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public void prepare() {
      if (!this.enabled) {
         this.mode.draw();
      }
   }

   @Override
   public void close() {
      if (!this.enabled) {
         this.enabled = true;
         this.mode.draw();
         this.active.clear();
      }
   }
}
