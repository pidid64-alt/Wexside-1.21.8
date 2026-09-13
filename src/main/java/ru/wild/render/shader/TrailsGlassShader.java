package ru.wild.render.shader;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.util.Identifier;

public final class TrailsGlassShader {
   private static final int instance = 2097152;
   private static final Identifier data = Identifier.of("wild", "core/trails_glass");
   private static final BlendFunction context = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
   private static final RenderPipeline config = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/trails_glass"))
         .withVertexShader(data)
         .withFragmentShader(data)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline state = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/trails_emissive"))
         .withVertexShader(data)
         .withFragmentShader(data)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(context)
         .build()
   );
   private static final RenderLayer cache = RenderLayer.of("wild/trails_glass", 2097152, false, true, config, MultiPhaseParameters.builder().build(false));
   private static final RenderLayer output = RenderLayer.of("wild/trails_emissive", 2097152, false, true, state, MultiPhaseParameters.builder().build(false));

   private TrailsGlassShader() {
   }

   public static void handle() {
      if (cache == null || output == null) {
         throw new IllegalStateException("Trails shader registry failed");
      }
   }

   public static RenderLayer process() {
      return cache;
   }

   public static RenderLayer compute() {
      return output;
   }
}
