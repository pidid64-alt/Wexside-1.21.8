package ru.wild.render.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
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
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.TexturedQuadsRenderer;
import ru.wild.util.render.GradientHelper;

public final class ShaderSamplerHelper {
   private static final int instance = 2097152;
   private static final int data = 7;
   private static final int context = new Std140SizeCalculator()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putMat4f()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .get();
   private static final Identifier config = Identifier.of("wild", "core/chinahat_depth");
   private static final Identifier state = Identifier.of("wild", "core/chinahat");
   private static final Identifier cache = Identifier.of("wild", "core/chinahat_aura");
   private static final RenderPipeline output = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/chinahat_depth"))
         .withVertexShader(config)
         .withFragmentShader(config)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.TRIANGLES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withColorWrite(false, false)
         .withDepthWrite(true)
         .withoutBlend()
         .build()
   );
   private static final RenderPipeline current = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/chinahat_material"))
         .withVertexShader(state)
         .withFragmentShader(state)
         .withSampler("u_SceneColor")
         .withSampler("u_SceneDepth")
         .withUniform("ChinaHatMaterial", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.TRIANGLES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withColorWrite(true, false)
         .withDepthWrite(false)
         .withoutBlend()
         .build()
   );
   private static final RenderPipeline active = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/chinahat_aura"))
         .withVertexShader(cache)
         .withFragmentShader(cache)
         .withSampler("u_SceneColor")
         .withSampler("u_SceneDepth")
         .withUniform("ChinaHatMaterial", UniformType.UNIFORM_BUFFER)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.TRIANGLES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withColorWrite(true, false)
         .withDepthWrite(true)
         .withoutBlend()
         .build()
   );
   private static final RenderLayer mode = RenderLayer.of("wild/chinahat_depth", 2097152, false, false, output, MultiPhaseParameters.builder().build(false));
   private static final RenderLayer selection = TexturedQuadsRenderer.handle(
      RenderLayer.of("wild/chinahat_material", 2097152, false, false, current, MultiPhaseParameters.builder().build(false)), ShaderSamplerHelper::handle
   );
   private static final RenderLayer enabled = TexturedQuadsRenderer.handle(
      RenderLayer.of("wild/chinahat_aura", 2097152, false, false, active, MultiPhaseParameters.builder().build(false)), ShaderSamplerHelper::process
   );
   private static final DistinctUniformPair<ShaderSamplerHelper.TransformState> renderer = new DistinctUniformPair<>(
      new ShaderSamplerHelper.TransformState(), new ShaderSamplerHelper.TransformState()
   );
   private static DynamicUniformStorage<ShaderSamplerHelper.TransformState> handler;
   private static GpuBufferSlice animationDraw;
   private static GpuTexture pointEncode;
   private static GpuTextureView animator;
   private static TextureFormat source;
   private static GpuTexture target;
   private static GpuTextureView pending;
   private static TextureFormat previous;
   private static int latest;
   private static int summary;
   private static boolean matrixBlend;
   private static boolean vectorMatch;
   private static volatile boolean itemProject;
   private static final SamplerDirtyFlags responseCompute = new SamplerDirtyFlags();
   private static final Runnable providerFetch = () -> {
      responseCompute.resolve();
      if (responseCompute.process()) {
         onTick();
      }
   };

   private ShaderSamplerHelper() {
   }

   public static void handle() {
      if (mode == null || selection == null || enabled == null) {
         RenderDiagnostics.handle().process("ChinaHatShaderRegistry.init", new IllegalStateException("ChinaHat shader registry failed"));
      }
   }

   public static RenderLayer process() {
      return mode;
   }

   public static RenderLayer compute() {
      return selection;
   }

   public static RenderLayer resolve() {
      return enabled;
   }

   public static boolean handle(
      float var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      Matrix4fc var19,
      float var20,
      float var21,
      float var22,
      float var23,
      float var24,
      float var25,
      float var26,
      float var27,
      float var28,
      float var29,
      float var30,
      float var31
   ) {
      matrixBlend = false;
      animationDraw = null;
      if (!refresh() || var19 == null) {
         return false;
      }

      if (responseCompute.process() && !onTick()) {
         return false;
      }

      MinecraftClient var32 = MinecraftClient.getInstance();
      if (var32 == null) {
         return false;
      }

      Framebuffer var33 = var32.getFramebuffer();
      if (var33 == null) {
         return false;
      }

      GpuTexture var34 = var33.getColorAttachment();
      GpuTexture var35 = var33.getDepthAttachment();
      if (var34 != null && var35 != null && !var34.isClosed() && !var35.isClosed()) {
         int var36 = Math.max(1, var34.getWidth(0));
         int var37 = Math.max(1, var34.getHeight(0));
         if (var35.getWidth(0) == var36 && var35.getHeight(0) == var37) {
            try {
               handle(var34, var35, var36, var37);
               if (pointEncode != null
                  && target != null
                  && animator != null
                  && pending != null
                  && !pointEncode.isClosed()
                  && !target.isClosed()
                  && !animator.isClosed()
                  && !pending.isClosed()) {
                  CommandEncoder var38 = RenderSystem.getDevice().createCommandEncoder();
                  var38.copyTextureToTexture(var34, pointEncode, 0, 0, 0, 0, 0, var36, var37);
                  var38.copyTextureToTexture(var35, target, 0, 0, 0, 0, 0, var36, var37);
                  ShaderSamplerHelper.TransformState var41 = renderer.handle();
                  var41.handle(
                     var0,
                     var1,
                     var2,
                     var3,
                     var4,
                     var5,
                     var36,
                     var37,
                     var6,
                     var7,
                     var8,
                     var9,
                     var10,
                     var11,
                     var12,
                     var13,
                     var14,
                     var15,
                     var16,
                     var17,
                     var18,
                     var19,
                     var20,
                     var21,
                     var22,
                     var23,
                     var24,
                     var25,
                     var26,
                     var27,
                     var28,
                     var29,
                     var30,
                     var31
                  );
                  animationDraw = prepare().write(var41);
                  renderer.process();
                  matrixBlend = animationDraw != null;
                  vectorMatch = false;
                  return matrixBlend;
               } else {
                  return false;
               }
            } catch (RuntimeException var40) {
               animationDraw = null;
               RuntimeException var39 = handle((RuntimeException)null);
               if (var39 != null && var39 != var40) {
                  var40.addSuppressed(var39);
               }

               if (!vectorMatch) {
                  vectorMatch = true;
                  RenderDiagnostics.handle().handle("ChinaHat scene capture", var40);
               }

               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void update() {
      if (matrixBlend) {
         if (!refresh()) {
            handle("ChinaHat depth restore is outside the render context");
         }

         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 == null) {
            handle("ChinaHat depth restore has no client");
         }

         Framebuffer var1 = var0.getFramebuffer();
         GpuTexture var2 = var1 == null ? null : var1.getDepthAttachment();
         if (target == null
            || target.isClosed()
            || var2 == null
            || var2.isClosed()
            || var2.getWidth(0) != latest
            || var2.getHeight(0) != summary
            || var2.getFormat() != previous) {
            handle("ChinaHat depth restore target is unavailable");
         }

         RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(target, var2, 0, 0, 0, 0, 0, latest, summary);
      }
   }

   public static void apply() {
      if (responseCompute.process()) {
         if (refresh()) {
            onTick();
         }

         animationDraw = null;
         matrixBlend = false;
      } else {
         if (handler != null && refresh()) {
            handler.clear();
         }

         animationDraw = null;
         matrixBlend = false;
      }
   }

   public static boolean execute() {
      responseCompute.handle();
      if (refresh()) {
         return onTick();
      }

      check();
      return false;
   }

   private static void handle(RenderPass var0) {
      GpuBufferSlice var1 = animationDraw;
      if (!matrixBlend || var1 == null) {
         handle("ChinaHat material slice is not prepared");
      }

      if (animator == null || pending == null || animator.isClosed() || pending.isClosed()) {
         handle("ChinaHat scene snapshot is unavailable");
      }

      var0.setUniform("ChinaHatMaterial", var1);
      var0.bindSampler("u_SceneColor", animator);
      var0.bindSampler("u_SceneDepth", pending);
   }

   private static void process(RenderPass var0) {
      GpuBufferSlice var1 = animationDraw;
      if (!matrixBlend || var1 == null || animator == null || pending == null || animator.isClosed() || pending.isClosed()) {
         handle("ChinaHat aura material is not prepared");
      }

      var0.setUniform("ChinaHatMaterial", var1);
      var0.bindSampler("u_SceneColor", animator);
      var0.bindSampler("u_SceneDepth", pending);
   }

   private static void handle(GpuTexture var0, GpuTexture var1, int var2, int var3) {
      TextureFormat var4 = var0.getFormat();
      TextureFormat var5 = var1.getFormat();
      if (pointEncode == null
         || target == null
         || animator == null
         || pending == null
         || pointEncode.isClosed()
         || target.isClosed()
         || animator.isClosed()
         || pending.isClosed()
         || latest != var2
         || summary != var3
         || source != var4
         || previous != var5) {
         RuntimeException var6 = handle((RuntimeException)null);
         if (var6 != null) {
            throw var6;
         }

         if (select()) {
            throw new IllegalStateException("ChinaHat scene targets could not be released");
         }

         pointEncode = RenderSystem.getDevice().createTexture("Wild ChinaHat Scene Color", 7, var4, var2, var3, 1, 1);
         animator = RenderSystem.getDevice().createTextureView(pointEncode);
         pointEncode.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         pointEncode.setTextureFilter(FilterMode.NEAREST, false);
         target = RenderSystem.getDevice().createTexture("Wild ChinaHat Scene Depth", 7, var5, var2, var3, 1, 1);
         pending = RenderSystem.getDevice().createTextureView(target);
         target.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         target.setTextureFilter(FilterMode.NEAREST, false);
         source = var4;
         previous = var5;
         latest = var2;
         summary = var3;
      }
   }

   private static DynamicUniformStorage<ShaderSamplerHelper.TransformState> prepare() {
      if (handler == null) {
         handler = new DynamicUniformStorage("Wild ChinaHat Material", context, 4);
      }

      return handler;
   }

   private static void check() {
      if (responseCompute.compute()) {
         try {
            MinecraftClient var0 = MinecraftClient.getInstance();
            if (var0 == null) {
               responseCompute.update();
               return;
            }

            var0.execute(providerFetch);
         } catch (RuntimeException var1) {
            responseCompute.update();
            process(var1);
         }
      }
   }

   private static boolean onTick() {
      if (!refresh()) {
         return false;
      }

      animationDraw = null;
      matrixBlend = false;
      RuntimeException var0 = null;
      DynamicUniformStorage var1 = handler;
      if (var1 != null) {
         SamplerLease.DataRecord var2 = SamplerLease.handle(var1, DynamicUniformStorage::close, var0x -> false);
         var0 = handle(var0, var2.failure());
         if (var2.released()) {
            handler = null;
         }
      }

      var0 = handle(var0);
      boolean var4 = handler == null && !select();
      responseCompute.handle(var4);
      if (var0 != null) {
         process(var0);
      }

      if (var4) {
         itemProject = false;
      }

      return var4;
   }

   private static RuntimeException handle(RuntimeException var0) {
      GpuTextureView var1 = animator;
      if (var1 != null) {
         SamplerLease.DataRecord var2 = SamplerLease.handle(var1, var0x -> {
            if (!var0x.isClosed()) {
               var0x.close();
            }
         }, GpuTextureView::isClosed);
         var0 = handle(var0, var2.failure());
         if (var2.released()) {
            animator = null;
         }
      }

      GpuTextureView var5 = pending;
      if (var5 != null) {
         SamplerLease.DataRecord var3 = SamplerLease.handle(var5, var0x -> {
            if (!var0x.isClosed()) {
               var0x.close();
            }
         }, GpuTextureView::isClosed);
         var0 = handle(var0, var3.failure());
         if (var3.released()) {
            pending = null;
         }
      }

      if (animator == null) {
         GpuTexture var6 = pointEncode;
         if (var6 != null) {
            SamplerLease.DataRecord var4 = SamplerLease.handle(var6, var0x -> {
               if (!var0x.isClosed()) {
                  var0x.close();
               }
            }, GpuTexture::isClosed);
            var0 = handle(var0, var4.failure());
            if (var4.released()) {
               pointEncode = null;
            }
         }
      }

      if (pending == null) {
         GpuTexture var7 = target;
         if (var7 != null) {
            SamplerLease.DataRecord var8 = SamplerLease.handle(var7, var0x -> {
               if (!var0x.isClosed()) {
                  var0x.close();
               }
            }, GpuTexture::isClosed);
            var0 = handle(var0, var8.failure());
            if (var8.released()) {
               target = null;
            }
         }
      }

      if (!select()) {
         source = null;
         previous = null;
         latest = 0;
         summary = 0;
      }

      return var0;
   }

   private static boolean select() {
      return animator != null || pointEncode != null || pending != null || target != null;
   }

   private static RuntimeException handle(RuntimeException var0, RuntimeException var1) {
      if (var1 == null) {
         return var0;
      }

      if (var0 == null) {
         return var1;
      }

      if (var0 != var1) {
         var0.addSuppressed(var1);
      }

      return var0;
   }

   private static void process(RuntimeException var0) {
      if (!itemProject) {
         itemProject = true;

         try {
            RenderDiagnostics.handle().handle("ChinaHat resource release", var0);
         } catch (RuntimeException var2) {
         }
      }
   }

   private static boolean refresh() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   private static void handle(String var0) {
      IllegalStateException var1 = new IllegalStateException(var0);
      RenderDiagnostics.handle().process("ChinaHatShaderRegistry.material", var1);
      throw var1;
   }

   static final class TransformState implements Uploadable {
      private final Matrix4f instance = new Matrix4f();
      private float data;
      private float context;
      private float config;
      private float state;
      private float cache;
      private float output;
      private float current;
      private float active;
      private float mode;
      private float selection;
      private float enabled;
      private float renderer;
      private float handler;
      private float animationDraw;
      private float pointEncode;
      private float animator;
      private float source;
      private float target;
      private float pending;
      private float previous;
      private float latest;
      private float summary;
      private float matrixBlend;
      private float vectorMatch;
      private float itemProject;
      private float responseCompute;
      private float providerFetch;
      private float profileDraw;
      private float vectorPerform;
      private float eventAttach;
      private float serverRead;
      private float positionAdvance;
      private float frameCheck;
      private float moduleCollect;
      private float providerClose;

      void handle(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         int var7,
         int var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13,
         float var14,
         float var15,
         float var16,
         float var17,
         float var18,
         float var19,
         float var20,
         float var21,
         Matrix4fc var22,
         float var23,
         float var24,
         float var25,
         float var26,
         float var27,
         float var28,
         float var29,
         float var30,
         float var31,
         float var32,
         float var33,
         float var34
      ) {
         this.data = handle(var1);
         this.context = handle(var2);
         this.config = handle(var3);
         this.state = handle(var4);
         this.cache = handle(var5);
         this.output = handle(var6);
         this.current = var7;
         this.active = var8;
         this.mode = 1.0F / var7;
         this.selection = 1.0F / var8;
         this.enabled = Math.max(0.0F, Math.min(1.0F, var9));
         this.renderer = process(var10);
         this.handler = process(var11);
         this.animationDraw = process(var12);
         this.pointEncode = Math.max(0.0F, process(var13));
         this.animator = Math.max(0.0F, process(var14));
         this.source = Math.max(0.0F, process(var15));
         this.target = Math.max(0.0F, process(var16));
         this.pending = Math.max(0.0F, process(var17));
         this.previous = handle(var18);
         this.latest = process(var19);
         this.summary = process(var20);
         this.matrixBlend = process(var21);
         this.instance.set(var22);
         this.vectorMatch = process(var23);
         this.itemProject = process(var24);
         this.responseCompute = process(var25);
         this.providerFetch = process(var26);
         this.profileDraw = process(var27);
         this.vectorPerform = process(var28);
         this.eventAttach = process(var29);
         this.serverRead = process(var30);
         this.positionAdvance = process(var31);
         this.frameCheck = process(var32);
         this.moduleCollect = process(var33);
         this.providerClose = process(var34);
      }

      private static float handle(float var0) {
         return Math.max(0.0F, Math.min(1.0F, var0));
      }

      private static float process(float var0) {
         return Float.isFinite(var0) ? var0 : 0.0F;
      }

      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1)
            .putVec4(this.data, this.context, this.config, 1.0F)
            .putVec4(this.state, this.cache, this.output, 1.0F)
            .putVec4(this.current, this.active, this.mode, this.selection)
            .putVec4(this.enabled, 0.62F, 1.18F, GradientHelper.pointEncode)
            .putVec4(this.renderer, this.handler, this.animationDraw, this.pointEncode)
            .putVec4(this.animator, this.source, this.target, this.pending)
            .putVec4(this.previous, GradientHelper.pointEncode, GradientHelper.animator, 0.0F)
            .putVec4(this.latest, this.summary, this.matrixBlend, 0.0F)
            .putMat4f(this.instance)
            .putVec4(this.vectorMatch, this.itemProject, this.responseCompute, 1.0F)
            .putVec4(this.providerFetch, this.profileDraw, this.vectorPerform, 0.0F)
            .putVec4(this.eventAttach, this.serverRead, this.positionAdvance, 0.0F)
            .putVec4(this.frameCheck, this.moduleCollect, this.providerClose, 0.0F);
      }
   }
}
