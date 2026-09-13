package ru.wild.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.RenderPhase.Texture;
import net.minecraft.util.Identifier;

public final class TexturedQuadsRenderer {
   private static final int instance = 1024;
   private static final int data = 256;
   private static final String context = "wild";
   private static final double config = 0.0625;
   private static final double state = 64.0;
   private static final int cache = 128;
   private static final RenderPipeline output = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/position_color_quads"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(true)
         .build()
   );
   private static final RenderPipeline current = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/position_color_quads_no_depth"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   private static final RenderPipeline active = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/position_color_quads_no_depth_blend"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline mode = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/position_color_quads_translucent"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline selection = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/position_color_quads_translucent_no_depth"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline enabled = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.RENDERTYPE_LINES_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, DrawMode.LINES)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(true)
         .build()
   );
   private static final RenderPipeline renderer = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.RENDERTYPE_LINES_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/lines_no_depth"))
         .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, DrawMode.LINES)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   private static final RenderPipeline handler = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline animationDraw = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads_additive"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.ADDITIVE)
         .build()
   );
   private static final RenderPipeline pointEncode = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads_no_depth_additive"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.ADDITIVE)
         .build()
   );
   private static final RenderPipeline animator = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads_no_depth"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderLayer source = RenderLayer.of(
      "wild/world/position_color_quads", 1024, false, true, output, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer target = RenderLayer.of(
      "wild/world/position_color_quads_no_depth", 1024, false, true, current, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer pending = RenderLayer.of(
      "wild/world/position_color_quads_no_depth_blend", 1024, false, true, active, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer previous = RenderLayer.of(
      "wild/world/position_color_quads_translucent", 1024, false, true, mode, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer latest = RenderLayer.of(
      "wild/world/position_color_quads_translucent_no_depth", 1024, false, true, selection, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer summary = RenderLayer.of(
      "wild/world/textured_quads", 1024, false, true, handler, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer matrixBlend = RenderLayer.of(
      "wild/world/textured_quads_additive", 1024, false, true, animationDraw, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer vectorMatch = RenderLayer.of(
      "wild/world/textured_quads_no_depth_additive", 1024, false, true, pointEncode, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer itemProject = RenderLayer.of(
      "wild/world/textured_quads_no_depth", 1024, false, true, animator, MultiPhaseParameters.builder().build(false)
   );
   private static final Map<Double, RenderLayer> responseCompute = new ConcurrentHashMap<>();
   private static final Map<Double, RenderLayer> providerFetch = new ConcurrentHashMap<>();

   private TexturedQuadsRenderer() {
   }

   public static RenderLayer handle() {
      return source;
   }

   public static RenderLayer process() {
      return target;
   }

   public static RenderLayer compute() {
      return pending;
   }

   public static RenderLayer resolve() {
      return previous;
   }

   public static RenderLayer update() {
      return latest;
   }

   public static RenderLayer apply() {
      return summary;
   }

   public static RenderLayer handle(Identifier var0) {
      return RenderLayer.of(var0.toString(), 1024, false, true, animator, MultiPhaseParameters.builder().texture(new Texture(var0, false)).build(false));
   }

   public static RenderLayer process(Identifier var0) {
      return RenderLayer.of(var0.toString(), 1024, false, true, handler, MultiPhaseParameters.builder().texture(new Texture(var0, false)).build(false));
   }

   public static RenderLayer compute(Identifier var0) {
      return RenderLayer.of(var0.toString(), 1024, false, true, animationDraw, MultiPhaseParameters.builder().texture(new Texture(var0, false)).build(false));
   }

   public static RenderLayer resolve(Identifier var0) {
      return RenderLayer.of(var0.toString(), 1024, false, true, pointEncode, MultiPhaseParameters.builder().texture(new Texture(var0, false)).build(false));
   }

   public static RenderLayer handle(double var0) {
      handle(responseCompute);
      double var2 = compute(var0);
      return responseCompute.computeIfAbsent(var2, var0x -> handle(var0x, "wild/world/lines", enabled));
   }

   public static RenderLayer process(double var0) {
      handle(providerFetch);
      double var2 = compute(var0);
      return providerFetch.computeIfAbsent(var2, var0x -> handle(var0x, "wild/world/lines_no_depth", renderer));
   }

   private static RenderLayer handle(double var0, String var2, RenderPipeline var3) {
      LineWidth var4 = new LineWidth(var0 == 0.0 ? OptionalDouble.empty() : OptionalDouble.of(var0));
      return RenderLayer.of(
         var2 + "/" + (var0 == 0.0 ? "default" : Double.toHexString(var0)), 256, false, true, var3, MultiPhaseParameters.builder().lineWidth(var4).build(false)
      );
   }

   public static MultiPhase handle(RenderLayer var0, Consumer<RenderPass> var1) {
      Objects.requireNonNull(var0, "renderLayer");
      if (var0 instanceof MultiPhase var2) {
         RenderPassMutableLayer.handle(var2).withRenderPassSetup(var1);
         return var2;
      } else {
         throw new IllegalArgumentException("Render layer must be a MultiPhase instance.");
      }
   }

   private static double compute(double var0) {
      if (!Double.isFinite(var0)) {
         throw new IllegalArgumentException("Line width must be finite.");
      }

      if (var0 < 0.0) {
         throw new IllegalArgumentException("Line width cannot be negative.");
      }

      if (var0 == 0.0) {
         return 0.0;
      }

      double var2 = Math.min(var0, 64.0);
      double var4 = Math.round(var2 / 0.0625) * 0.0625;
      if (var4 <= 0.0) {
         var4 = 0.0625;
      }

      return var4;
   }

   private static void handle(Map<Double, RenderLayer> var0) {
      if (var0.size() > 128) {
         var0.clear();
      }
   }
}
