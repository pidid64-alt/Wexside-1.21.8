package ru.wild.render.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gl.DynamicUniformStorage.Uploadable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.GLFW;
import ru.wild.modules.visuals.Chams;
import ru.wild.render.EntityRenderIdAccess;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.TexturedQuadsRenderer;

public final class PrismaticChamsPipeline {
   private static final int instance = 1048576;
   private static final int data = 5;
   private static final long context = System.nanoTime();
   private static final Identifier config = Identifier.of("wild", "core/prismatic_chams");
   private static final int state = new Std140SizeCalculator().putVec4().putVec4().putVec4().putVec4().putVec4().putVec4().putIVec4().get();
   private static final RenderPipeline cache = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/sss_chams_visible"))
         .withVertexShader(config)
         .withFragmentShader(config)
         .withSampler("u_ScreenTexture")
         .withUniform("PrismaticChams", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withColorWrite(true, true)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline output = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/sss_chams_depth"))
         .withVertexShader(config)
         .withFragmentShader(config)
         .withSampler("u_ScreenTexture")
         .withUniform("PrismaticChams", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withColorWrite(true, true)
         .withDepthWrite(true)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderLayer current = TexturedQuadsRenderer.handle(
      RenderLayer.of(
         "wild/sss_chams_visible",
         1048576,
         false,
         true,
         cache,
         MultiPhaseParameters.builder()
            .texture(RenderPhase.NO_TEXTURE)
            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .build(false)
      ),
      PrismaticChamsPipeline::handle
   );
   private static final RenderLayer active = TexturedQuadsRenderer.handle(
      RenderLayer.of(
         "wild/sss_chams_depth",
         1048576,
         false,
         true,
         output,
         MultiPhaseParameters.builder()
            .texture(RenderPhase.NO_TEXTURE)
            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .build(false)
      ),
      PrismaticChamsPipeline::handle
   );
   private static final PrismaticChamsPipeline.DataRecord mode = new PrismaticChamsPipeline.DataRecord(
      new Vector4f(0.12F, 0.82F, 1.0F, 1.0F),
      new Vector4f(0.82F, 0.18F, 1.0F, 1.0F),
      new Vector4f(0.0F, 0.0F, 0.0F, 0.0F),
      new Vector4f(1.35F, 1.0F, 0.72F, 0.0F),
      new Vector4f(1.0F, 0.0F, 0.0F, 0.0F),
      new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
      0,
      0,
      0,
      0
   );
   private static DynamicUniformStorage<PrismaticChamsPipeline.DataRecord> selection;
   private static PrismaticChamsPipeline.DataRecord enabled = mode;
   private static GpuBufferSlice renderer;
   private static GpuTexture handler;
   private static GpuTextureView animationDraw;
   private static TextureFormat pointEncode;
   private static int animator;
   private static int source;
   private static boolean target;

   private PrismaticChamsPipeline() {
   }

   public static void handle() {
      if (current == null || active == null) {
         RenderDiagnostics.handle().process("PrismaticChamsShaderRegistry.init", new IllegalStateException("SSS chams shader registry failed"));
      }
   }

   public static RenderLayer process() {
      return current;
   }

   public static RenderLayer compute() {
      return active;
   }

   public static RenderLayer handle(Chams var0) {
      return var0 != null && !var0.drawAnimation() ? active : current;
   }

   public static void resolve() {
      target = false;
      if (render()) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null) {
            Framebuffer var1 = var0.getFramebuffer();
            if (var1 != null) {
               GpuTexture var2 = var1.getColorAttachment();
               if (var2 != null && !var2.isClosed()) {
                  int var3 = Math.max(1, var2.getWidth(0));
                  int var4 = Math.max(1, var2.getHeight(0));
                  handle(var2, var3, var4);
                  if (handler != null && animationDraw != null && !handler.isClosed() && !animationDraw.isClosed()) {
                     RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(var2, handler, 0, 0, 0, 0, 0, var3, var4);
                     animator = var3;
                     source = var4;
                     target = true;
                     RenderSystem.setShaderTexture(1, animationDraw);
                  }
               }
            }
         }
      }
   }

   public static void handle(Chams var0, LivingEntityRenderState var1, float var2, float var3) {
      if (var0 == null) {
         enabled = mode;
      } else {
         float[] var4 = var0.encodePoint();
         float[] var5 = var0.animate();
         Vec3d var6 = refresh();
         float var7 = (float)(System.nanoTime() - context) / 1.0E9F;
         float var8 = handle(var1);
         float var9 = var0.drawAnimation() ? 0.0F : (var0.tick() ? 1.0F : 2.0F);
         Vector4f var10 = select();
         enabled = new PrismaticChamsPipeline.DataRecord(
            new Vector4f(var4[0], var4[1], var4[2], var4[3]),
            new Vector4f(var5[0], var5[1], var5[2], var5[3]),
            new Vector4f((float)var6.x, (float)var6.y, (float)var6.z, var7),
            new Vector4f(var0.serverRead.compute(), var0.positionAdvance.compute(), var0.frameCheck.compute(), 0.0F),
            new Vector4f(var3, var2, var8, var9),
            var10,
            var0.render(),
            0,
            0,
            0
         );
         execute();
      }
   }

   public static void update() {
      if (selection != null && render()) {
         selection.clear();
      }

      renderer = null;
      target = false;
   }

   public static void apply() {
      DynamicUniformStorage var0 = selection;
      selection = null;
      renderer = null;
      target = false;
      if (var0 != null && render()) {
         var0.close();
      }

      check();
   }

   private static void handle(RenderPass var0) {
      GpuBufferSlice var1 = renderer;
      if (var1 == null) {
         RenderDiagnostics.handle().process("PrismaticChamsShaderRegistry.uniform", new IllegalStateException("PrismaticChams uniform slice is not prepared"));
      }

      var0.setUniform("PrismaticChams", var1);
      GpuTextureView var2 = onTick();
      if (var2 == null || var2.isClosed()) {
         RenderDiagnostics.handle().process("PrismaticChamsShaderRegistry.sampler", new IllegalStateException("u_ScreenTexture sampler is unavailable"));
      }

      var0.bindSampler("u_ScreenTexture", var2);
   }

   private static void execute() {
      renderer = render() ? prepare().write(enabled == null ? mode : enabled) : null;
   }

   private static DynamicUniformStorage<PrismaticChamsPipeline.DataRecord> prepare() {
      if (selection == null) {
         selection = new DynamicUniformStorage("SSS Chams UBO", state, 4);
      }

      return selection;
   }

   private static void handle(GpuTexture var0, int var1, int var2) {
      TextureFormat var3 = var0.getFormat();
      if (handler == null
         || animationDraw == null
         || handler.isClosed()
         || animationDraw.isClosed()
         || animator != var1
         || source != var2
         || pointEncode != var3) {
         check();
         handler = RenderSystem.getDevice().createTexture("Wild SSS Chams Screen", 5, var3, var1, var2, 1, 1);
         animationDraw = RenderSystem.getDevice().createTextureView(handler);
         pointEncode = var3;
         animator = var1;
         source = var2;
         handler.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         handler.setTextureFilter(FilterMode.LINEAR, false);
      }
   }

   private static void check() {
      GpuTextureView var0 = animationDraw;
      GpuTexture var1 = handler;
      animationDraw = null;
      handler = null;
      pointEncode = null;
      animator = 0;
      source = 0;
      if (var0 != null && !var0.isClosed()) {
         var0.close();
      }

      if (var1 != null && !var1.isClosed()) {
         var1.close();
      }
   }

   private static GpuTextureView onTick() {
      if (target && animationDraw != null && !animationDraw.isClosed()) {
         return animationDraw;
      } else {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null && var0.getFramebuffer() != null) {
            GpuTextureView var1 = var0.getFramebuffer().getColorAttachmentView();
            return var1 != null && !var1.isClosed() ? var1 : handle("framebuffer color attachment view is unavailable");
         } else {
            return handle("client framebuffer is unavailable");
         }
      }
   }

   private static GpuTextureView handle(String var0) {
      IllegalStateException var1 = new IllegalStateException(var0);
      RenderDiagnostics.handle().process("PrismaticChamsShaderRegistry.screenSampler", var1);
      throw var1;
   }

   private static Vector4f select() {
      int var0 = target && animator > 0 ? animator : 0;
      int var1 = target && source > 0 ? source : 0;
      if (var0 <= 0 || var1 <= 0) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         Window var3 = var2 == null ? null : var2.getWindow();
         if (var3 != null) {
            var0 = var3.getFramebufferWidth();
            var1 = var3.getFramebufferHeight();
         }
      }

      var0 = Math.max(1, var0);
      var1 = Math.max(1, var1);
      return new Vector4f(var0, var1, 1.0F / var0, 1.0F / var1);
   }

   private static Vec3d refresh() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 != null && var0.gameRenderer != null && var0.gameRenderer.getCamera() != null ? var0.gameRenderer.getCamera().getPos() : Vec3d.ZERO;
   }

   private static boolean render() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   private static float handle(LivingEntityRenderState var0) {
      if (var0 == null) {
         return 0.0F;
      }

      int var1 = ((EntityRenderIdAccess)var0).wild$getEntityId();
      int var2 = var1 == Integer.MIN_VALUE ? Float.floatToIntBits((float)var0.x * 17.0F + (float)var0.z * 31.0F) : var1;
      var2 ^= var2 << 13;
      var2 ^= var2 >>> 17;
      var2 ^= var2 << 5;
      return (var2 & 65535) / 65535.0F;
   }

   record DataRecord(
      Vector4fc accentTop,
      Vector4fc accentBottom,
      Vector4fc cameraAndTime,
      Vector4fc params,
      Vector4fc state,
      Vector4fc resolution,
      int mode,
      int flagA,
      int flagB,
      int flagC
   ) implements Uploadable {
      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1)
            .putVec4(this.accentTop)
            .putVec4(this.accentBottom)
            .putVec4(this.cameraAndTime)
            .putVec4(this.params)
            .putVec4(this.state)
            .putVec4(this.resolution)
            .putIVec4(this.mode, this.flagA, this.flagB, this.flagC);
      }
   }
}
