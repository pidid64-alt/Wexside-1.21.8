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
import ru.wild.render.RenderDiagnostics;

public final class StardustShader {
   private static final int instance = 1048576;
   private static final Identifier data = Identifier.of("minecraft", "core/stardust");
   private static final BlendFunction context = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
   private static final RenderPipeline config = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/stardust"))
         .withVertexShader(data)
         .withFragmentShader(data)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(context)
         .build()
   );
   private static final RenderLayer state = RenderLayer.of("wild/stardust", 1048576, false, true, config, MultiPhaseParameters.builder().build(false));

   private StardustShader() {
   }

   public static void handle() {
      if (config == null || state == null) {
         RenderDiagnostics.handle().process("StardustShaderRegistry.init", new IllegalStateException("Stardust shader registry failed"));
      }
   }

   public static RenderLayer process() {
      return state;
   }
}
