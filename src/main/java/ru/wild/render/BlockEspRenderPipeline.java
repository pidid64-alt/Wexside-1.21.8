package ru.wild.render;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;

public final class BlockEspRenderPipeline {
   public static final String instance = "BlockEsp";
   public static final int data = new Std140SizeCalculator().putMat4f().putVec4().putVec4().putVec4().putVec4().putVec4().putVec4().putVec4().get();
   public static final VertexFormat context = VertexFormat.builder()
      .add("Position", VertexFormatElement.POSITION)
      .add("UV0", VertexFormatElement.UV0)
      .add("Color", VertexFormatElement.COLOR)
      .add("UV2", VertexFormatElement.UV2)
      .add("Normal", VertexFormatElement.NORMAL)
      .padding(1)
      .build();
   private static final BlendFunction config = new BlendFunction(
      SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA
   );
   private static final Identifier state = Identifier.of("wild", "core/block_esp");
   private static final RenderPipeline cache = RenderPipelines.register(
      handle("pipeline/block_esp_visible").withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST).build()
   );
   private static final RenderPipeline output = RenderPipelines.register(
      handle("pipeline/block_esp_occluded").withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST).build()
   );

   private static Builder handle(String var0) {
      return RenderPipeline.builder(new Snippet[0])
         .withLocation(Identifier.of("wild", var0))
         .withVertexShader(state)
         .withFragmentShader(state)
         .withUniform("Projection", UniformType.UNIFORM_BUFFER)
         .withUniform("BlockEsp", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(context, DrawMode.QUADS)
         .withCull(true)
         .withDepthWrite(false)
         .withDepthBias(-1.0F, -10.0F)
         .withBlend(config);
   }

   private BlockEspRenderPipeline() {
   }

   public static void handle() {
      if (cache == null || output == null) {
         throw new IllegalStateException("BlockESP shader registry failed");
      }
   }

   public static RenderPipeline process() {
      return cache;
   }

   public static RenderPipeline compute() {
      return output;
   }
}
