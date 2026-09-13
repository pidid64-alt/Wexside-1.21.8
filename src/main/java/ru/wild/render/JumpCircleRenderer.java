package ru.wild.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gl.DynamicUniformStorage.Uploadable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class JumpCircleRenderer {
   private static final int instance = 262144;
   private static final int data = new Std140SizeCalculator().putVec4().get();
   private static final Identifier context = Identifier.of("wild", "core/jump_circle");
   private static final Identifier config = Identifier.of("wild", "core/jump_circle_irid");
   private static final RenderPipeline state = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/jump_circle_lens"))
         .withVertexShader(context)
         .withFragmentShader(context)
         .withUniform("JumpCircle", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline cache = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/jump_circle_lens_irid"))
         .withVertexShader(config)
         .withFragmentShader(config)
         .withUniform("JumpCircle", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer output = TexturedQuadsRenderer.handle(
      RenderLayer.of("wild/jump_circle_lens", 262144, false, true, state, MultiPhaseParameters.builder().build(false)), JumpCircleRenderer::handle
   );
   private static final RenderLayer current = TexturedQuadsRenderer.handle(
      RenderLayer.of("wild/jump_circle_lens_irid", 262144, false, true, cache, MultiPhaseParameters.builder().build(false)), JumpCircleRenderer::handle
   );
   private static final JumpCircleRenderer.DataRecord active = new JumpCircleRenderer.DataRecord(1.0F, 1.0F, 1.0F);
   private static DynamicUniformStorage<JumpCircleRenderer.DataRecord> mode;
   private static JumpCircleRenderer.DataRecord selection = active;
   private static GpuBufferSlice enabled;

   private JumpCircleRenderer() {
   }

   public static void handle() {
      if (output == null || current == null) {
         throw new IllegalStateException("JumpCircle shader registry failed");
      }
   }

   public static RenderLayer process() {
      return output;
   }

   public static RenderLayer compute() {
      return current;
   }

   public static RenderLayer resolve() {
      return output;
   }

   public static void handle(float var0, float var1, float var2) {
      selection = new JumpCircleRenderer.DataRecord(var0, var1, var2);
      enabled = execute() ? apply().write(selection) : null;
   }

   public static void update() {
      if (mode != null && execute()) {
         mode.clear();
      }

      enabled = null;
   }

   private static void handle(RenderPass var0) {
      GpuBufferSlice var1 = enabled;
      if (var1 != null) {
         var0.setUniform("JumpCircle", var1);
      }
   }

   private static DynamicUniformStorage<JumpCircleRenderer.DataRecord> apply() {
      if (mode == null) {
         mode = new DynamicUniformStorage("Wild JumpCircle UBO", data, 4);
      }

      return mode;
   }

   private static boolean execute() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   record DataRecord(float iridescentSpeed, float brightness, float opacity) implements Uploadable {
      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1).putVec4(this.iridescentSpeed, this.brightness, this.opacity, 0.0F);
      }
   }
}
