package ru.wild.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gl.DynamicUniformStorage.Uploadable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import ru.wild.modules.visuals.Stardust;

public final class StardustSkyRenderer {
   private static final Identifier instance = Identifier.of("minecraft", "core/stardust_sky");
   private static final BlendFunction data = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
   private static final int context = new Std140SizeCalculator()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putIVec4()
      .get();
   private static final RenderPipeline config = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/stardust_sky"))
         .withVertexShader(instance)
         .withFragmentShader(instance)
         .withUniform("StardustSky", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION, DrawMode.TRIANGLES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(data)
         .build()
   );
   private static final int state = 48;
   private static final int cache = 14;
   private static final float output = 128.0F;
   private static final Vector4f current = new Vector4f();
   private static final Vector3f active = new Vector3f();
   private static final Matrix4f mode = new Matrix4f();
   private static final Matrix4f selection = new Matrix4f();
   private static final Vector3f enabled = new Vector3f();
   private static final Matrix4f renderer = new Matrix4f();
   private static final long handler = System.nanoTime();
   private static final long animationDraw = 4096000000000L;
   private static DynamicUniformStorage<StardustSkyRenderer.DataRecord> pointEncode;
   private static GpuBuffer animator;
   private static int source;
   private static boolean target;

   private StardustSkyRenderer() {
   }

   public static void handle() {
   }

   public static void handle(Matrix4f var0, Matrix4f var1) {
      try {
         if (var0 == null || var1 == null) {
            selection.identity();
            return;
         }

         renderer.set(var1).mul(var0);
         if (Math.abs(renderer.determinant()) <= 1.0E-8F) {
            selection.identity();
            return;
         }

         selection.set(renderer).invert();
      } catch (Throwable var3) {
         selection.identity();
      }
   }

   public static void handle(Camera var0, float var1, float var2) {
      if (!target && !(var2 <= 0.001F)) {
         try {
            compute();
            if (animator == null || source <= 0) {
               return;
            }

            MinecraftClient var3 = MinecraftClient.getInstance();
            if (var3 == null || var3.getFramebuffer() == null) {
               return;
            }

            if (var0 == null) {
               return;
            }

            GpuTextureView var4 = var3.getFramebuffer().getColorAttachmentView();
            GpuTextureView var5 = var3.getFramebuffer().getDepthAttachmentView();
            int var6 = Stardust.animate();
            int var7 = Stardust.load();
            current.set((var6 >>> 16 & 0xFF) / 255.0F, (var6 >>> 8 & 0xFF) / 255.0F, (var6 & 0xFF) / 255.0F, var2);
            active.set((var7 >>> 16 & 0xFF) / 255.0F, (var7 >>> 8 & 0xFF) / 255.0F, (var7 & 0xFF) / 255.0F);
            mode.identity();
            GpuBufferSlice var8 = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), current, active, mode, Stardust.submit());
            GpuBufferSlice var9 = process().write(handle(var3, var0, var1, var2, var6, var7));
            RenderPass var10 = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Wild Stardust Sky", var4, OptionalInt.empty(), var5, OptionalDouble.empty());

            try {
               var10.setPipeline(config);
               RenderSystem.bindDefaultUniforms(var10);
               var10.setUniform("DynamicTransforms", var8);
               var10.setUniform("StardustSky", var9);
               var10.setVertexBuffer(0, animator);
               var10.draw(0, source);
            } catch (Throwable var14) {
               if (var10 != null) {
                  try {
                     var10.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (var10 != null) {
               var10.close();
            }
         } catch (Throwable var15) {
            target = true;
            System.err.println("[Stardust] sky renderer disabled: " + var15.getMessage());
         }
      }
   }

   private static DynamicUniformStorage<StardustSkyRenderer.DataRecord> process() {
      if (pointEncode == null) {
         pointEncode = new DynamicUniformStorage("Wild Stardust Sky UBO", context, 4);
      }

      return pointEncode;
   }

   private static StardustSkyRenderer.DataRecord handle(MinecraftClient var0, Camera var1, float var2, float var3, int var4, int var5) {
      Window var6 = var0.getWindow();
      int var7 = var6 == null ? 1 : Math.max(1, var6.getFramebufferWidth());
      int var8 = var6 == null ? 1 : Math.max(1, var6.getFramebufferHeight());
      enabled.set(0.0F, 0.0F, -1.0F);
      var1.getRotation().transform(enabled);
      enabled.normalize();
      float var9 = var0.world == null ? 0.0F : var0.world.getRainGradient(var2);
      float var10 = handle(var0);
      float var11 = (float)((System.nanoTime() - handler) % 4096000000000L) / 1.0E9F;
      float var12 = Math.max(0.0F, Math.min(1.0F, Stardust.pending.compute() / 3600.0F));
      return new StardustSkyRenderer.DataRecord(
         new Vector4f((var4 >>> 16 & 0xFF) / 255.0F, (var4 >>> 8 & 0xFF) / 255.0F, (var4 & 0xFF) / 255.0F, var3),
         new Vector4f((var5 >>> 16 & 0xFF) / 255.0F, (var5 >>> 8 & 0xFF) / 255.0F, (var5 & 0xFF) / 255.0F, 1.0F),
         new Vector4f(enabled.x, enabled.y, enabled.z, var9),
         new Vector4f(var7, var8, var11, var10),
         new Vector4f(var3, var12, var2, 0.0F),
         new Matrix4f(selection),
         Stardust.save().process(),
         0,
         0,
         0
      );
   }

   private static float handle(MinecraftClient var0) {
      if (var0 != null && var0.world != null) {
         long var1 = Stardust.unload() ? Stardust.itemProject : var0.world.getTimeOfDay();
         long var3 = Math.floorMod(var1, 24000L);
         return (float)var3 / 24000.0F;
      } else {
         return 0.75F;
      }
   }

   private static void compute() {
      if (animator == null && !target) {
         try {
            BufferBuilder var0 = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION);
            float var1 = -0.24F;
            float var2 = 1.0F;

            for (int var3 = 0; var3 < 14; var3++) {
               float var4 = var3 / 14.0F;
               float var5 = (var3 + 1) / 14.0F;
               float var6 = var1 + (var2 - var1) * var4;
               float var7 = var1 + (var2 - var1) * var5;

               for (int var8 = 0; var8 < 48; var8++) {
                  float var9 = var8 / 48.0F;
                  float var10 = (var8 + 1) / 48.0F;
                  handle(var0, var9, var6);
                  handle(var0, var10, var6);
                  handle(var0, var10, var7);
                  handle(var0, var10, var7);
                  handle(var0, var9, var7);
                  handle(var0, var9, var6);
               }
            }

            BuiltBuffer var14 = var0.end();

            try {
               source = var14.getDrawParameters().vertexCount();
               animator = RenderSystem.getDevice().createBuffer(() -> "Wild Stardust Sky Dome", 32, var14.getBuffer());
            } catch (Throwable var12) {
               if (var14 != null) {
                  try {
                     var14.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (var14 != null) {
               var14.close();
            }
         } catch (Throwable var13) {
            target = true;
         }
      }
   }

   private static void handle(BufferBuilder var0, float var1, float var2) {
      float var3 = var1 * (float) (Math.PI * 2);
      float var4 = (float)Math.sqrt(Math.max(0.0F, 1.0F - var2 * var2));
      float var5 = (float)Math.cos(var3) * var4 * 128.0F;
      float var6 = var2 * 128.0F;
      float var7 = (float)Math.sin(var3) * var4 * 128.0F;
      var0.vertex(var5, var6, var7);
   }

   record DataRecord(
      Vector4fc primary,
      Vector4fc secondary,
      Vector4fc cameraWeather,
      Vector4fc resolutionTime,
      Vector4fc params,
      Matrix4fc inverseViewProjection,
      int mode,
      int flagA,
      int flagB,
      int flagC
   ) implements Uploadable {
      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1)
            .putVec4(this.primary)
            .putVec4(this.secondary)
            .putVec4(this.cameraWeather)
            .putVec4(this.resolutionTime)
            .putVec4(this.params)
            .putMat4f(this.inverseViewProjection)
            .putIVec4(this.mode, this.flagA, this.flagB, this.flagC);
      }
   }
}
