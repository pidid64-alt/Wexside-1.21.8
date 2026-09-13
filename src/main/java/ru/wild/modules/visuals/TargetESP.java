package ru.wild.modules.visuals;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.Texture;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.TargetSelectorRegistry;
import ru.wild.gui.hud.HudElementRenderer;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.combat.TriggerBot;
import ru.wild.render.FramebufferCapture;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.render.shader.ThemeShaderApplier;
import ru.wild.util.math.AnimationDirection;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.math.SmoothTimer;
import ru.wild.util.render.DualLayerBoxVertexEmitter;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "TargetESP", description = "Жозки таргет есп", category = ModuleCategory.Visuals)
public class TargetESP extends Module implements HudElementRenderer {
   private static final String vectorMatch = "target_esp";
   public static ModeSetting source = new ModeSetting("Текстура", "Картинка", "Картинка", "Призраки", "Кольцо", "Кубики", "Сфера");
   public static ModeSetting target = new ModeSetting("Режим призраков", "Обычный", "Обычный", "Новый", "Старый", "Орбита", "Спираль")
      .handle(() -> !source.process("Призраки"));
   public static ModeSetting pending = new ModeSetting("Режим картинки", "Клиент", "Клиент", "Ромб", "Ромб 2").handle(() -> !source.process("Картинка"));
   public static ModeSetting previous = new ModeSetting("Режим кубиков", "Новый", "Новый", "Старый", "Орбита").handle(() -> !source.process("Кубики"));
   public static ShaderPresetSetting latest = new ShaderPresetSetting("Foundry Shader", LivePreviewRenderer.ESP);
   private static final Identifier itemProject = Identifier.of("wild", "textures/world/target.png");
   private static final Identifier responseCompute = Identifier.of("wild", "textures/world/targetn2.png");
   private static final Identifier providerFetch = Identifier.of("wild", "textures/world/targetn.png");
   private static final Identifier profileDraw = Identifier.of("wild", "textures/world/glow.png");
   private static final Identifier vectorPerform = Identifier.of("wild", "textures/world/dashbloom.png");
   public static EasedDoubleAnimator summary = new EasedDoubleAnimator();
   public static EasedDoubleAnimator matrixBlend = new EasedDoubleAnimator();
   private LivingEntity eventAttach = null;
   private final Predicate<Entity> serverRead = var1 -> var1 == AttackAura.textureRun || var1 == this.eventAttach;
   private static long positionAdvance = 0L;
   private float frameCheck = 0.0F;
   private long moduleCollect = 0L;
   private final ArrayList<TargetESP.VertexEmitter> providerClose = new ArrayList<>();
   private static long presetSave = System.currentTimeMillis();
   static float windowConvert = 0.0F;
   private static final long presetWrite = 1000L;
   private static final int colorMeasure = 1;
   private static final float animationSchedule = 0.02F;
   private static final int rendererScan = 50;
   private float sourceBuild = 0.0F;
   private static final int outputCollapse = 1024;
   private static final String profileInvoke = "wild";
   private static final RenderPipeline sourceSchedule = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline timerRender = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/textured_quads"))
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer scaleSave = handle(itemProject, timerRender);
   private static final RenderLayer colorCompute = handle(providerFetch, timerRender);
   private static final RenderLayer scaleAdapt = handle(responseCompute, timerRender);
   private static final RenderLayer textureRun = handle(profileDraw, sourceSchedule);
   private static final RenderLayer indexBind = handle(vectorPerform, sourceSchedule);
   private static final RenderPipeline actionRead = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lequal_depth_test"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline configCollapse = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINE_STRIP)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer dataValidate = RenderLayer.of("ring_strip", 1024, false, true, actionRead, MultiPhaseParameters.builder().build(false));
   private static final RenderLayer scaleRender = RenderLayer.of("ring_line", 1024, false, true, configCollapse, MultiPhaseParameters.builder().build(false));
   private static final RenderPipeline clientRefresh = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "pipeline/world/color_quads"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer keyFilter = RenderLayer.of("color_quads", 1024, false, true, clientRefresh, MultiPhaseParameters.builder().build(false));
   private static final RenderPipeline requestAdapt = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline timerMeasure = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "targetesp_cube_lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer vectorEncode = RenderLayer.of(
      "targetesp_cube_lines", 1024, false, true, timerMeasure, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderPipeline requestReceive = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "targetesp_cube_fill"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   static final RenderLayer windowProcess = RenderLayer.of(
      "targetesp_cube_fill", 1024, false, true, requestReceive, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderPipeline packetSave = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "targetesp_cube_outline"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   static final RenderLayer entryAnimate = RenderLayer.of("targetesp_cube_outline", 1024, false, true, packetSave, MultiPhaseParameters.builder().build(false));

   public TargetESP() {
      this.handle(source, target, pending, previous, latest);
   }

   @Override
   public LivePreviewRenderer compute() {
      return LivePreviewRenderer.ESP;
   }

   @Override
   public String resolve() {
      String var1 = refresh();
      return var1 != null && !var1.isBlank() ? var1 : null;
   }

   public static String refresh() {
      String var0 = latest == null ? "" : latest.refresh();
      return var0 == null ? "" : var0;
   }

   @Override
   public boolean update() {
      return true;
   }

   @Override
   public void handle() {
      super.handle();
      TargetSelectorRegistry.handle().handle(this, this);
      this.render();
   }

   @Override
   public void process() {
      FramebufferCapture.handle().handle("target_esp");
      TargetSelectorRegistry.handle().handle(this);
      this.providerClose.clear();
      super.process();
   }
   @EventHandler
   public void handle(WorldRenderEvent var1) {
      TargetSelectorRegistry.handle().process(this, this);
      this.render();
      summary.handle();
      LivingEntity var2 = AttackAura.textureRun != null ? AttackAura.textureRun : TriggerBot.refresh();
      if (Module.client.world != null && Module.client.player != null) {
         AttackAura var3 = (AttackAura)WildClient.instance.data.process(AttackAura.class);
         if (var3 != null) {
            summary.handle(var2 == null ? 0.0 : 1.0, 0.35F, EasingFunctions.handler);
            if (summary.select() > 0.0) {
               if (var2 != null) {
                  if (this.eventAttach != var2) {
                     positionAdvance = 0L;
                     this.moduleCollect = 0L;
                     this.frameCheck = 0.0F;
                  }

                  this.eventAttach = var2;
               }

               if (this.eventAttach != null && !source.process("Не отображать")) {
                  Immediate var4 = WorldVertexBuffer.handle();
                  boolean var7 = false /* VF: Semaphore variable */;

                  try {
                     var7 = true;
                     if (source.process("Картинка") && pending.process("Ромб")) {
                        this.handle(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Картинка") && pending.process("Клиент")) {
                        this.process(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Картинка") && pending.process("Ромб 2")) {
                        this.compute(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Призраки") && target.process("Обычный")) {
                        this.select(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Призраки") && target.process("Новый")) {
                        this.update(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Призраки") && target.process("Старый")) {
                        this.apply(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Призраки") && target.process("Орбита")) {
                        this.execute(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Призраки") && target.process("Спираль")) {
                        this.prepare(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Кольцо")) {
                        this.resolve(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Кубики") && previous.process("Новый")) {
                        this.refresh(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Кубики") && previous.process("Старый")) {
                        this.render(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Кубики") && previous.process("Орбита")) {
                        this.check(var1.compute(), var4, this.eventAttach, var1.resolve());
                     }

                     if (source.process("Сфера")) {
                        this.onTick(var1.compute(), var4, this.eventAttach, var1.resolve());
                        var7 = false;
                     } else {
                        var7 = false;
                     }
                  } finally {
                     if (var7) {
                        WorldVertexBuffer.process();
                     }
                  }

                  WorldVertexBuffer.process();
               }
            } else {
               this.eventAttach = null;
               positionAdvance = 0L;
               this.moduleCollect = 0L;
               this.frameCheck = 0.0F;
               this.providerClose.clear();
            }
         }
      }
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.world != null && Module.client.player != null && var1 != null && var1.compute() != null) {
         String var2 = refresh();
         if (!var2.isBlank() && this.eventAttach != null && !(summary.select() <= 0.001F)) {
            float var3 = var1.compute().getRenderTickCounter().getDynamicDeltaTicks();
            TargetESP.DataRecord var4 = this.handle(this.eventAttach, var3, var1.apply(), var1.execute());
            if (var4 != null) {
               RoundedRectRenderer var5 = var1.resolve();
               if (var5 != null) {
                  var5.compute();
               }

               float var6 = (float)Math.min(0.92, summary.select() * 0.78);
               float var7 = var4.x + var4.w * 0.5F;
               float var8 = var4.y + var4.h * 0.5F;
               int var9 = FramebufferCapture.handle().resolve();
               boolean var10 = ThemeShaderApplier.handle(var2, var9, var4.x, var4.y, var4.w, var4.h, var1.apply(), var1.execute(), var7, var8, tick(), var6);
               if (var10 && var5 != null) {
                  var5.compute();
               }
            }
         }
      }
   }

   private void render() {
      FramebufferCapture.handle().handle("target_esp", this.enabled && !refresh().isBlank(), this.serverRead);
   }

   private TargetESP.DataRecord handle(LivingEntity var1, float var2, int var3, int var4) {
      if (var1 != null && !var1.isRemoved() && var3 > 1 && var4 > 1 && Module.client.gameRenderer != null && Module.client.gameRenderer.getCamera() != null) {
         Vec3d var5 = var1.getLerpedPos(var2);
         Vec3d var6 = var1.getPos();
         Box var7 = var1.getBoundingBox()
            .offset(var5.x - var6.x, var5.y - var6.y, var5.z - var6.z)
            .expand(0.05, Math.max(0.05, var1.getHeight() * 0.035), 0.05);
         float var8 = Float.POSITIVE_INFINITY;
         float var9 = Float.POSITIVE_INFINITY;
         float var10 = Float.NEGATIVE_INFINITY;
         float var11 = Float.NEGATIVE_INFINITY;

         for (int var12 = 0; var12 < 2; var12++) {
            double var13 = var12 == 0 ? var7.minX : var7.maxX;

            for (int var15 = 0; var15 < 2; var15++) {
               double var16 = var15 == 0 ? var7.minY : var7.maxY;

               for (int var18 = 0; var18 < 2; var18++) {
                  double var19 = var18 == 0 ? var7.minZ : var7.maxZ;
                  Vec3d var21 = ClientMathUtil.handle(new Vec3d(var13, var16, var19));
                  if (var21 == null || var21.z <= 0.001F || var21.z > 1.0) {
                     return null;
                  }

                  var8 = Math.min(var8, (float)var21.x);
                  var9 = Math.min(var9, (float)var21.y);
                  var10 = Math.max(var10, (float)var21.x);
                  var11 = Math.max(var11, (float)var21.y);
               }
            }
         }

         if (!Float.isFinite(var8) || !Float.isFinite(var9) || !Float.isFinite(var10) || !Float.isFinite(var11)) {
            return null;
         } else if (!(var10 < 0.0F) && !(var11 < 0.0F) && !(var8 > var3) && !(var9 > var4)) {
            float var22 = Math.max(1.0F, var10 - var8);
            float var23 = Math.max(1.0F, var11 - var9);
            float var14 = Math.min(96.0F, Math.max(18.0F, var22 * 0.28F));
            float var24 = Math.min(96.0F, Math.max(18.0F, var23 * 0.18F));
            float var25 = Math.max(0.0F, var8 - var14);
            float var17 = Math.max(0.0F, var9 - var24);
            float var26 = Math.min(var3, var10 + var14);
            float var27 = Math.min(var4, var11 + var24);
            float var20 = var26 - var25;
            float var28 = var27 - var17;
            return var20 > 2.0F && var28 > 2.0F ? new TargetESP.DataRecord(var25, var17, var20, var28) : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static ThemeColors tick() {
      ThemePalette var0 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
      return ThemeColors.handle(var0, ThemeShaderApplier.resolve());
   }

   private void handle(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      Vec3d var5 = var3.getLerpedPos(var4);
      double var6 = var5.x;
      double var8 = var5.y;
      double var10 = var5.z;
      Vec3d var12 = Module.client.gameRenderer.getCamera().getPos();
      var1.push();
      var1.translate(var6 - var12.x, var8 - var12.y + var3.getHeight() / 1.75F, var10 - var12.z);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-Module.client.gameRenderer.getCamera().getYaw()));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Module.client.gameRenderer.getCamera().getPitch()));
      long var13 = System.currentTimeMillis();
      float var15 = (float)ClientMathUtil.resolve(0.0, 720.0, (Math.sin(var13 / 900.0) + 1.0) / 2.0 * 360.0 * 2.0);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var15));
      matrixBlend.handle();
      int var16 = var3.hurtTime;
      float var17 = (float)Math.sin(var16 * (Math.PI / 20));
      matrixBlend.handle(var17, 0.4F, EasingFunctions.handler);
      float var18 = matrixBlend.update();
      float var19 = (float)summary.select();
      int var20 = PackedColor.compute(200, 70, 70, (int)(255.0F * var19));
      int var21 = PackedColor.update(PackedColor.compute(PackedColor.handle(), var19), var20, matrixBlend.update());
      float var22 = 1.7F - 0.9F * var19 + (0.35F - 0.35F * var18);
      var1.scale(var22, var22, 1.0F);
      RenderLayer var23 = scaleSave;
      Matrix4f var24 = var1.peek().getPositionMatrix();
      VertexConsumer var25 = var2.getBuffer(var23);
      handle(var25, var24, var21, (int)(255.0F * var19));
      var1.pop();
   }

   private void process(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      Vec3d var5 = var3.getLerpedPos(var4);
      double var6 = var5.x;
      double var8 = var5.y;
      double var10 = var5.z;
      Vec3d var12 = Module.client.gameRenderer.getCamera().getPos();
      var1.push();
      var1.translate(var6 - var12.x, var8 - var12.y + var3.getHeight() / 1.75F, var10 - var12.z);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-Module.client.gameRenderer.getCamera().getYaw()));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Module.client.gameRenderer.getCamera().getPitch()));
      long var13 = System.currentTimeMillis();
      float var15 = (float)ClientMathUtil.resolve(0.0, 720.0, (Math.sin(var13 / 1600.0) + 1.0) / 2.0 * 360.0 * 2.0);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var15));
      matrixBlend.handle();
      int var16 = var3.hurtTime;
      float var17 = (float)Math.sin(var16 * (Math.PI / 20));
      matrixBlend.handle(var17, 0.4F, EasingFunctions.handler);
      float var18 = matrixBlend.update();
      float var19 = (float)summary.select();
      int var20 = PackedColor.compute(200, 70, 70, (int)(255.0F * var19));
      int var21 = PackedColor.update(PackedColor.compute(PackedColor.handle(), var19), var20, matrixBlend.update());
      float var22 = 1.5F - 0.9F * var19 + (0.35F - 0.35F * var18);
      var1.scale(var22, var22, 1.0F);
      RenderLayer var23 = colorCompute;
      Matrix4f var24 = var1.peek().getPositionMatrix();
      VertexConsumer var25 = var2.getBuffer(var23);
      handle(var25, var24, var21, (int)(255.0F * var19));
      var1.pop();
   }

   private void compute(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      Vec3d var5 = var3.getLerpedPos(var4);
      double var6 = var5.x;
      double var8 = var5.y;
      double var10 = var5.z;
      Vec3d var12 = Module.client.gameRenderer.getCamera().getPos();
      var1.push();
      var1.translate(var6 - var12.x, var8 - var12.y + var3.getHeight() / 1.75F, var10 - var12.z);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-Module.client.gameRenderer.getCamera().getYaw()));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Module.client.gameRenderer.getCamera().getPitch()));
      long var13 = System.currentTimeMillis();
      float var15 = (float)ClientMathUtil.resolve(0.0, 720.0, (Math.sin(var13 / 1000.0) + 1.0) / 2.0 * 360.0 * 2.0);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var15));
      matrixBlend.handle();
      int var16 = var3.hurtTime;
      float var17 = (float)Math.sin(var16 * (Math.PI / 20));
      matrixBlend.handle(var17, 0.4F, EasingFunctions.handler);
      float var18 = matrixBlend.update();
      float var19 = (float)summary.select();
      int var20 = PackedColor.compute(200, 70, 70, (int)(255.0F * var19));
      int var21 = PackedColor.update(PackedColor.compute(PackedColor.handle(), var19), var20, matrixBlend.update());
      float var22 = 1.25F - 0.6F * var19 + (0.35F - 0.35F * var18);
      var1.scale(var22, var22, 1.0F);
      RenderLayer var23 = scaleAdapt;
      Matrix4f var24 = var1.peek().getPositionMatrix();
      VertexConsumer var25 = var2.getBuffer(var23);
      handle(var25, var24, var21, (int)(255.0F * var19));
      var1.pop();
   }

   private void resolve(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         Vec3d var5 = Module.client.gameRenderer.getCamera().getPos();
         double var6 = var3.lastRenderX + (var3.getX() - var3.lastRenderX) * var4;
         double var8 = var3.lastRenderY + (var3.getY() - var3.lastRenderY) * var4;
         double var10 = var3.lastRenderZ + (var3.getZ() - var3.lastRenderZ) * var4;
         var1.push();
         var1.translate(var6 - var5.x, var8 - var5.y, var10 - var5.z);
         float var12 = (float)summary.select();
         float var13 = var3.getHeight();
         double var14 = var3.getWidth() * 1.0F - 0.2F * matrixBlend.update();
         int var16 = PackedColor.compute(200, 70, 70, (int)(255.0F * var12));
         matrixBlend.handle();
         int var17 = var3.hurtTime;
         float var18 = (float)Math.sin(var17 * (Math.PI / 20));
         matrixBlend.handle(var18, 0.4F, EasingFunctions.handler);
         Matrix4f var19 = var1.peek().getPositionMatrix();
         double var20 = 1800.0;
         double var22 = System.currentTimeMillis() % var20;
         boolean var24 = var22 > var20 / 2.0;
         double var25 = var22 / (var20 / 2.0);
         var25 = var24 ? var25 - 1.0 : 1.0 - var25;
         var25 = var25 < 0.5 ? 2.0 * var25 * var25 : 1.0 - Math.pow(-2.0 * var25 + 2.0, 2.0) / 2.0;
         double var27 = var13 / 1.25F * (var25 > 0.5 ? 1.0 - var25 : var25) * (var24 ? -1 : 1);
         VertexConsumer var29 = var2.getBuffer(dataValidate);

         for (byte var30 = 0; var30 <= 360; var30 += 5) {
            double var31 = Math.toRadians(var30);
            float var33 = (float)(Math.cos(var31) * var14);
            float var34 = (float)(Math.sin(var31) * var14);
            int var35 = PackedColor.update(
               PackedColor.compute(
                  PackedColor.process(PackedColor.update(PackedColor.handle(), 0.5F), PackedColor.update(PackedColor.handle(), 1.0F), var30 * 4, 1), var12
               ),
               var16,
               matrixBlend.update()
            );
            int var36 = var35 >> 16 & 0xFF;
            int var37 = var35 >> 8 & 0xFF;
            int var38 = var35 & 0xFF;
            var29.vertex(var19, var33, (float)(var13 * var25), var34).color(var36, var37, var38, (int)(180.0F * var12));
            var29.vertex(var19, var33, (float)(var13 * var25 + var27), var34).color(var36, var37, var38, 0);
         }

         VertexConsumer var41 = var2.getBuffer(scaleRender);

         for (byte var42 = 0; var42 <= 360; var42 += 5) {
            double var32 = Math.toRadians(var42);
            float var43 = (float)(Math.cos(var32) * var14);
            float var44 = (float)(Math.sin(var32) * var14);
            int var45 = PackedColor.update(
               PackedColor.compute(
                  PackedColor.process(PackedColor.update(PackedColor.handle(), 0.5F), PackedColor.update(PackedColor.handle(), 1.0F), var42 * 4, 1), var12
               ),
               var16,
               matrixBlend.update()
            );
            var41.vertex(var19, var43, (float)(var13 * var25), var44).color(PackedColor.update(var45, (int)(255.0F * var12)));
         }

         var1.pop();
      }
   }

   private void update(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         long var5 = System.currentTimeMillis();
         if (this.moduleCollect == 0L) {
            this.moduleCollect = var5;
         }

         long var7 = var5 - this.moduleCollect;
         if (var7 > 0L) {
            this.frameCheck += (float)(5L * var7) / 900.0F;
         }

         this.moduleCollect = var5;
         Vec3d var9 = var3.getLerpedPos(var4);
         Vec3d var10 = Module.client.gameRenderer.getCamera().getPos();
         double var11 = var9.x - var10.x;
         double var13 = var9.y - var10.y;
         double var15 = var9.z - var10.z;
         float var17 = (float)summary.select();
         matrixBlend.handle();
         int var18 = var3.hurtTime;
         float var19 = (float)Math.sin(var18 * (Math.PI / 20));
         matrixBlend.handle(var19, 0.4F, EasingFunctions.handler);
         float var20 = matrixBlend.update();
         int var21 = PackedColor.handle();
         int var22 = PackedColor.compute(200, 70, 70, (int)(255.0F * var17));
         int var23 = PackedColor.update(PackedColor.compute(var21, var17), var22, var20);
         RenderLayer var24 = textureRun;
         byte var25 = 3;
         byte var26 = 12;
         int var27 = 3 * var25;
         var1.push();
         Camera var28 = Module.client.gameRenderer.getCamera();

         for (byte var29 = 0; var29 < var27; var29 += var25) {
            for (int var30 = 0; var30 < var26; var30++) {
               float var31 = this.frameCheck + var30 * 0.1F;
               float var32 = 0.75F;
               float var33 = 0.5F;
               int var34 = (int)Math.pow(var29, 2.0);
               var1.push();
               double var35 = var11 + var32 * Math.sin(var31 + var34);
               double var37 = var13 + var33 + 0.3F * Math.sin(this.frameCheck + var30 * 0.2F) + 0.2F * var29;
               double var39 = var15 + var32 * Math.cos(var31 - var34);
               var1.translate(var35, var37, var39);
               float var41 = 0.005F + var30 / 2000.0F;
               var1.scale(var41, var41, var41);
               var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var28.getYaw()));
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var28.getPitch()));
               Matrix4f var42 = var1.peek().getPositionMatrix();
               VertexConsumer var43 = var2.getBuffer(var24);
               int var44 = var23;
               int var45 = var44 >> 16 & 0xFF;
               int var46 = var44 >> 8 & 0xFF;
               int var47 = var44 & 0xFF;
               int var48 = (int)(var17 * 255.0F);
               byte var49 = -25;
               byte var50 = 50;
               var43.vertex(var42, var49, var49 + var50, 0.0F)
                  .color(var45, var46, var47, var48)
                  .texture(0.0F, 1.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var43.vertex(var42, var49 + var50, var49 + var50, 0.0F)
                  .color(var45, var46, var47, var48)
                  .texture(1.0F, 1.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var43.vertex(var42, var49 + var50, var49, 0.0F)
                  .color(var45, var46, var47, var48)
                  .texture(1.0F, 0.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var43.vertex(var42, var49, var49, 0.0F)
                  .color(var45, var46, var47, var48)
                  .texture(0.0F, 0.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var1.pop();
            }
         }

         var1.pop();
      }
   }

   private void apply(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         long var5 = System.currentTimeMillis();
         if (this.moduleCollect == 0L) {
            this.moduleCollect = var5;
         }

         long var7 = var5 - this.moduleCollect;
         if (var7 > 0L) {
            this.frameCheck += (float)(5L * var7) / 200.0F;
         }

         this.moduleCollect = var5;
         Vec3d var9 = var3.getLerpedPos(var4);
         Vec3d var10 = Module.client.gameRenderer.getCamera().getPos();
         double var11 = var9.x - var10.x;
         double var13 = var9.y + 1.1F - var10.y;
         double var15 = var9.z - var10.z;
         float var17 = (float)summary.select();
         RenderLayer var18 = textureRun;
         byte var19 = 17;
         byte var20 = 6;
         float var21 = 1.25F;
         float var22 = 1.1F;
         float var23 = this.frameCheck;
         Camera var24 = Module.client.gameRenderer.getCamera();
         double var25 = var3.getWidth() + 0.12F;
         boolean var27 = Module.client.player.canSee(var3);
         VertexConsumer var28 = var2.getBuffer(var18);
         matrixBlend.handle();
         int var29 = var3.hurtTime;
         float var30 = (float)Math.sin(var29 * (Math.PI / 20));
         matrixBlend.handle(var30, 0.4F, EasingFunctions.handler);
         float var31 = matrixBlend.update();
         int var32 = handle(255, var31);

         for (int var33 = 0; var33 < 3; var33++) {
            for (int var34 = 0; var34 <= var19; var34++) {
               double var35 = Math.toRadians(((var34 / 1.5F + var23) * var20 + var33 * 120) % (var20 * 360));
               double var37 = Math.sin(Math.toRadians(var23 * 2.0F + var34 * (var33 + 1)) * var22) / var21;
               float var39 = (float)var34 / var19;
               var1.push();
               var1.translate(var11 + Math.cos(var35) * var25, var13 + var37, var15 + Math.sin(var35) * var25);
               var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var24.getYaw()));
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var24.getPitch()));
               Matrix4f var40 = var1.peek().getPositionMatrix();
               int var41 = handle(var32, (int)(255.0F * var39 * var17));
               int var42 = var41 >> 16 & 0xFF;
               int var43 = var41 >> 8 & 0xFF;
               int var44 = var41 & 0xFF;
               int var45 = var41 >> 24 & 0xFF;
               float var46 = Math.max(0.25F * var39, 0.22F);
               var28.vertex(var40, -var46, var46, 0.0F)
                  .color(var42, var43, var44, var45)
                  .texture(0.0F, 1.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var28.vertex(var40, var46, var46, 0.0F)
                  .color(var42, var43, var44, var45)
                  .texture(1.0F, 1.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var28.vertex(var40, var46, -var46, 0.0F)
                  .color(var42, var43, var44, var45)
                  .texture(1.0F, 0.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var28.vertex(var40, -var46, -var46, 0.0F)
                  .color(var42, var43, var44, var45)
                  .texture(0.0F, 0.0F)
                  .overlay(OverlayTexture.DEFAULT_UV)
                  .light(15728880)
                  .normal(0.0F, 0.0F, 1.0F);
               var1.pop();
            }
         }
      }
   }

   private static float drawAnimation() {
      return (float)(System.currentTimeMillis() % 1000000L) / 1000.0F;
   }

   private float handle(LivingEntity var1) {
      matrixBlend.handle();
      int var2 = var1.hurtTime;
      float var3 = (float)Math.sin(var2 * (Math.PI / 20));
      matrixBlend.handle(var3, 0.4F, EasingFunctions.handler);
      return matrixBlend.update();
   }

   private void handle(
      MatrixStack var1, Immediate var2, Camera var3, RenderLayer var4, double var5, double var7, double var9, float var11, int var12, int var13
   ) {
      if (var13 > 0) {
         var1.push();
         var1.translate(var5, var7, var9);
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var3.getYaw()));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var3.getPitch()));
         var1.scale(var11, var11, var11);
         handle(var2.getBuffer(var4), var1.peek().getPositionMatrix(), var12, var13);
         var1.pop();
      }
   }

   private void execute(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         Camera var5 = Module.client.gameRenderer.getCamera();
         Vec3d var6 = var5.getPos();
         Vec3d var7 = var3.getLerpedPos(var4);
         float var8 = (float)summary.select();
         float var9 = this.handle(var3);
         int var10 = handle(255, var9) & 16777215;
         double var11 = var7.x - var6.x;
         double var13 = var7.z - var6.z;
         double var15 = var7.y - var6.y + var3.getHeight() * 0.5;
         double var17 = var3.getWidth() / 2.0 + 0.5;
         double var19 = var3.getHeight() * 0.18;
         byte var21 = 16;
         float var22 = drawAnimation();

         for (int var23 = 0; var23 < var21; var23++) {
            double var24 = (Math.PI * 2) / var21 * var23 + var22 * 1.4;
            double var26 = var11 + Math.cos(var24) * var17;
            double var28 = var13 + Math.sin(var24) * var17;
            double var30 = var15 + Math.sin(var22 * 2.2 + var23 * 0.6) * var19;
            float var32 = 0.55F + 0.45F * (float)Math.sin(var22 * 2.0 + var23);
            int var33 = (int)(215.0F * var8 * var32);
            float var34 = 0.3F + 0.06F * (float)Math.sin(var22 * 3.0 + var23);
            this.handle(var1, var2, var5, textureRun, var26, var30, var28, var34, var10, var33);
         }
      }
   }

   private void prepare(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         Camera var5 = Module.client.gameRenderer.getCamera();
         Vec3d var6 = var5.getPos();
         Vec3d var7 = var3.getLerpedPos(var4);
         float var8 = (float)summary.select();
         float var9 = this.handle(var3);
         int var10 = handle(255, var9) & 16777215;
         double var11 = var7.x - var6.x;
         double var13 = var7.z - var6.z;
         double var15 = var7.y - var6.y - 0.1;
         double var17 = var3.getWidth() / 2.0 + 0.32;
         double var19 = var3.getHeight() + 0.2;
         double var21 = 2.5;
         byte var23 = 18;
         float var24 = drawAnimation();

         for (int var25 = 0; var25 < 2; var25++) {
            for (int var26 = 0; var26 <= var23; var26++) {
               double var27 = (double)var26 / var23;
               double var29 = var27 * var21 * Math.PI * 2.0 + var24 * 2.0 + var25 * Math.PI;
               double var31 = var11 + Math.cos(var29) * var17;
               double var33 = var13 + Math.sin(var29) * var17;
               double var35 = var15 + var27 * var19;
               int var37 = (int)(220.0F * var8 * (0.3 + 0.7 * Math.sin(var27 * Math.PI)));
               this.handle(var1, var2, var5, textureRun, var31, var35, var33, 0.24F, var10, var37);
            }
         }
      }
   }

   private void check(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         Camera var5 = Module.client.gameRenderer.getCamera();
         Vec3d var6 = var5.getPos();
         Vec3d var7 = var3.getLerpedPos(var4);
         float var8 = (float)summary.select();
         float var9 = this.handle(var3);
         int var10 = handle(255, var9) & 16777215;
         double var11 = var7.x - var6.x;
         double var13 = var7.z - var6.z;
         double var15 = var7.y - var6.y + var3.getHeight() * 0.5;
         double var17 = var3.getWidth() / 2.0 + 0.55;
         byte var19 = 14;
         float var20 = drawAnimation();

         for (int var21 = 0; var21 < var19; var21++) {
            double var22 = (Math.PI * 2) / var19 * var21 + var20 * 1.1;
            double var24 = var11 + Math.cos(var22) * var17;
            double var26 = var13 + Math.sin(var22) * var17;
            double var28 = var15 + Math.sin(var20 * 2.0 + var21) * 0.12;
            var1.push();
            var1.translate(var24, var28, var26);
            var1.push();
            float var30 = (var20 * 50.0F + var21 * 28.0F) % 360.0F;
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var30));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var30 * 0.7F));
            Matrix4f var31 = var1.peek().getPositionMatrix();
            float var32 = 0.16F + 0.02F * (float)Math.sin(var20 * 3.0 + var21);
            DualLayerBoxVertexEmitter.handle(var2.getBuffer(windowProcess), var31, handle(var10, (int)(70.0F * var8)), var32);
            DualLayerBoxVertexEmitter.process(var2.getBuffer(entryAnimate), var31, handle(var10, (int)(230.0F * var8)), var32);
            var1.pop();
            var1.push();
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var5.getYaw()));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var5.getPitch()));
            float var33 = var32 * 2.4F;
            var1.scale(var33, var33, var33);
            handle(var2.getBuffer(indexBind), var1.peek().getPositionMatrix(), var10, (int)(60.0F * var8));
            var1.pop();
            var1.pop();
         }
      }
   }

   private void onTick(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         Camera var5 = Module.client.gameRenderer.getCamera();
         Vec3d var6 = var5.getPos();
         Vec3d var7 = var3.getLerpedPos(var4);
         float var8 = (float)summary.select();
         float var9 = this.handle(var3);
         int var10 = handle(handle(255, var9) & 16777215, (int)(220.0F * var8));
         double var11 = var7.x - var6.x;
         double var13 = var7.y - var6.y + var3.getHeight() * 0.5;
         double var15 = var7.z - var6.z;
         float var17 = (float)(Math.max(var3.getWidth(), var3.getHeight() * 0.5) * 0.72 + 0.3 + var9 * 0.2);
         float var18 = drawAnimation();
         var1.push();
         var1.translate(var11, var13, var15);
         var1.push();
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var18 * 38.0F));
         handle(var2.getBuffer(entryAnimate), var1.peek().getPositionMatrix(), var17, 40, var10);
         var1.pop();
         var1.push();
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var18 * 30.0F));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
         handle(var2.getBuffer(entryAnimate), var1.peek().getPositionMatrix(), var17, 40, var10);
         var1.pop();
         var1.push();
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var18 * 26.0F + 90.0F));
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
         handle(var2.getBuffer(entryAnimate), var1.peek().getPositionMatrix(), var17, 40, var10);
         var1.pop();
         var1.pop();
      }
   }

   private static void handle(VertexConsumer var0, Matrix4f var1, float var2, int var3, int var4) {
      int var5 = var4 >> 16 & 0xFF;
      int var6 = var4 >> 8 & 0xFF;
      int var7 = var4 & 0xFF;
      int var8 = var4 >>> 24 & 0xFF;

      for (int var9 = 0; var9 < var3; var9++) {
         double var10 = (Math.PI * 2) / var3 * var9;
         double var12 = (Math.PI * 2) / var3 * (var9 + 1);
         var0.vertex(var1, (float)(Math.cos(var10) * var2), 0.0F, (float)(Math.sin(var10) * var2)).color(var5, var6, var7, var8);
         var0.vertex(var1, (float)(Math.cos(var12) * var2), 0.0F, (float)(Math.sin(var12) * var2)).color(var5, var6, var7, var8);
      }
   }

   static void handle(VertexConsumer var0, Matrix4f var1, int var2, int var3) {
      int var4 = var2 >> 16 & 0xFF;
      int var5 = var2 >> 8 & 0xFF;
      int var6 = var2 & 0xFF;
      var0.vertex(var1, -0.5F, -0.5F, 0.0F)
         .color(var4, var5, var6, var3)
         .texture(0.0F, 1.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
      var0.vertex(var1, 0.5F, -0.5F, 0.0F)
         .color(var4, var5, var6, var3)
         .texture(1.0F, 1.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
      var0.vertex(var1, 0.5F, 0.5F, 0.0F)
         .color(var4, var5, var6, var3)
         .texture(1.0F, 0.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
      var0.vertex(var1, -0.5F, 0.5F, 0.0F)
         .color(var4, var5, var6, var3)
         .texture(0.0F, 0.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
   }

   private static RenderLayer handle(Identifier var0, RenderPipeline var1) {
      return RenderLayer.of(var0.toString(), 1024, false, true, var1, MultiPhaseParameters.builder().texture(new Texture(var0, false)).build(false));
   }

   private static int handle(int var0, float var1) {
      int var2 = 6061311;

      try {
         ThemePalette var3 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
         if (var3 == ThemePalette.CUSTOM && WildClient.instance.selection.data != null) {
            var2 = WildClient.instance.selection.data.prepare() & 16777215;
         } else if (var3 != null && var3.handle() != null) {
            var2 = var3.handle().getRGB() & 16777215;
         }
      } catch (Throwable var11) {
      }

      float var12 = var1 < 0.0F ? 0.0F : Math.min(var1, 1.0F);
      int var4 = var2 >> 16 & 0xFF;
      int var5 = var2 >> 8 & 0xFF;
      int var6 = var2 & 0xFF;
      int var7 = Math.round(var4 + (235 - var4) * var12);
      int var8 = Math.round(var5 + (70 - var5) * var12);
      int var9 = Math.round(var6 + (70 - var6) * var12);
      int var10 = Math.max(0, Math.min(255, var0));
      return var10 << 24 | var7 << 16 | var8 << 8 | var9;
   }

   static int handle(int var0, int var1) {
      return Math.max(0, Math.min(255, var1)) << 24 | var0 & 16777215;
   }

   private void select(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      if (var3 != null) {
         double var6 = 0.3 + var3.getWidth() / 2.0F;
         matrixBlend.handle();
         int var8 = var3.hurtTime;
         float var9 = (float)Math.sin(var8 * (Math.PI / 20));
         matrixBlend.handle(var9, 0.4F, EasingFunctions.handler);
         float var10 = matrixBlend.update();
         float var11 = 30.0F;
         float var12 = 0.4F - 0.1F * var10;
         double var13 = 6 - (int)(1.0F * var10);
         int var15 = 40 - (int)(12.0F * var10);
         Vec3d var16 = var5.gameRenderer.getCamera().getPos();
         Camera var17 = var5.gameRenderer.getCamera();
         if (positionAdvance == 0L) {
            positionAdvance = System.currentTimeMillis();
         }

         long var18 = System.currentTimeMillis();
         Vec3d var20 = var3.getLerpedPos(var4);
         var20 = new Vec3d(var20.x, var20.y + 0.32 + var3.getHeight() / 2.0F, var20.z);
         double var21 = var20.x + 0.2;
         double var23 = var20.y;
         double var25 = var20.z;
         RenderLayer var27 = textureRun;
         VertexConsumer var28 = var2.getBuffer(var27);
         float var29 = (float)summary.select();
         int var30 = handle((int)(255.0F * var29), var10);
         int var31 = var30;
         int var32 = handle(var30, (int)(210.0F * var29));
         int var33 = handle(var30, (int)(150.0F * var29));
         int var34 = handle(var30, (int)(90.0F * var29));
         var1.push();
         var1.translate(var21 - var16.x, var23 - var16.y, var25 - var16.z);
         float var35 = 0.3F;

         for (int var36 = 0; var36 < var15; var36++) {
            double var37 = 0.05F * (var18 - positionAdvance - var36 * var13) / var11;
            double var39 = Math.sin(var37 * Math.PI) * var6;
            double var41 = Math.cos(var37 * Math.PI) * var6;
            double var43 = Math.cos(var37 * Math.PI) * var6;
            float var45 = (float)var36 / (var15 - 1);
            float var46 = 1.0F - var45 * var35;
            float var47 = var12 * var46;
            var1.push();
            var1.translate(var39, var43, -var41);
            var1.translate(-var47 / 2.0F, -var47 / 2.0F, 0.0F);
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var17.getYaw()));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var17.getPitch()));
            var1.translate(var47 / 2.0F, var47 / 2.0F, 0.0F);
            Matrix4f var48 = var1.peek().getPositionMatrix();
            this.handle(var28, var48, var31, var32, var33, var34, var47);
            var1.pop();
         }

         for (int var50 = 0; var50 < var15; var50++) {
            double var52 = 0.05F * (var18 - positionAdvance - var50 * var13) / var11;
            double var54 = Math.sin(var52 * Math.PI) * var6;
            double var56 = Math.cos(var52 * Math.PI) * var6;
            double var58 = Math.sin(var52 * Math.PI) * var6;
            float var60 = (float)var50 / (var15 - 1);
            float var62 = 1.0F - var60 * var35;
            float var64 = var12 * var62;
            var1.push();
            var1.translate(-var54, var58, -var56);
            var1.translate(-var64 / 2.0F, -var64 / 2.0F, 0.0F);
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var17.getYaw()));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var17.getPitch()));
            var1.translate(var64 / 2.0F, var64 / 2.0F, 0.0F);
            Matrix4f var66 = var1.peek().getPositionMatrix();
            this.handle(var28, var66, var31, var32, var33, var34, var64);
            var1.pop();
         }

         for (int var51 = 0; var51 < var15; var51++) {
            double var53 = 0.05F * (var18 - positionAdvance - var51 * var13) / var11;
            double var55 = Math.sin(var53 * Math.PI) * var6;
            double var57 = Math.cos(var53 * Math.PI) * var6;
            double var59 = Math.sin(var53 * Math.PI) * var6;
            float var61 = (float)var51 / (var15 - 1);
            float var63 = 1.0F - var61 * var35;
            float var65 = var12 * var63;
            var1.push();
            var1.translate(var55, var59, var57);
            var1.translate(-var65 / 2.0F, -var65 / 2.0F, 0.0F);
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var17.getYaw()));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var17.getPitch()));
            var1.translate(var65 / 2.0F, var65 / 2.0F, 0.0F);
            Matrix4f var67 = var1.peek().getPositionMatrix();
            this.handle(var28, var67, var31, var32, var33, var34, var65);
            var1.pop();
         }

         var1.pop();
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, int var3, int var4, int var5, int var6, float var7) {
      int var8 = var3 >> 16 & 0xFF;
      int var9 = var3 >> 8 & 0xFF;
      int var10 = var3 & 0xFF;
      int var11 = var3 >> 24 & 0xFF;
      int var12 = var4 >> 16 & 0xFF;
      int var13 = var4 >> 8 & 0xFF;
      int var14 = var4 & 0xFF;
      int var15 = var4 >> 24 & 0xFF;
      int var16 = var5 >> 16 & 0xFF;
      int var17 = var5 >> 8 & 0xFF;
      int var18 = var5 & 0xFF;
      int var19 = var5 >> 24 & 0xFF;
      int var20 = var6 >> 16 & 0xFF;
      int var21 = var6 >> 8 & 0xFF;
      int var22 = var6 & 0xFF;
      int var23 = var6 >> 24 & 0xFF;
      var1.vertex(var2, 0.0F, -var7, 0.0F).texture(0.0F, 0.0F).color(var8, var9, var10, var11);
      var1.vertex(var2, -var7, -var7, 0.0F).texture(0.0F, 1.0F).color(var12, var13, var14, var15);
      var1.vertex(var2, -var7, 0.0F, 0.0F).texture(1.0F, 1.0F).color(var16, var17, var18, var19);
      var1.vertex(var2, 0.0F, 0.0F, 0.0F).texture(1.0F, 0.0F).color(var20, var21, var22, var23);
   }

   private void refresh(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 != null) {
         Vec3d var5 = Module.client.gameRenderer.getCamera().getPos();
         long var6 = System.currentTimeMillis();
         byte var8 = 24;
         double var9 = 0.4 + var3.getWidth() / 2.0F + 0.35F - 0.35F * summary.update();
         double var11 = var3.getHeight();
         Vec3d var13 = var3.getLerpedPos(var4);
         float var14 = (float)summary.select();
         matrixBlend.handle();
         int var15 = var3.hurtTime;
         float var16 = (float)Math.sin(var15 * (Math.PI / 20));
         matrixBlend.handle(var16, 0.4F, EasingFunctions.handler);
         float var17 = matrixBlend.update();
         int var18 = handle(Math.round(70.0F * var14), var17);
         int var19 = handle(Math.round(225.0F * var14), var17);
         int var20 = handle(255, var17);

         for (int var21 = 0; var21 < var8; var21++) {
            double var22 = Math.sin(var21 * 132.12 + 4.12);
            double var24 = Math.cos(var21 * 453.21 + 1.23);
            double var26 = Math.sin(var21 * 789.34 + 9.87);
            double var28 = var9;
            double var30 = 1.0;
            double var32 = (Math.PI * 2) / var8 * var21;
            double var34 = var6 / 6000.0 * (Math.PI * 2) * var30;
            double var36 = var34 + var32;
            double var38 = Math.cos(var36) * var28;
            double var40 = Math.sin(var36) * var28;
            double var42 = 1.0 + var22 * 0.2;
            double var44 = var32 + var26 * 2.0;
            double var46 = Math.sin(var6 / 9000.0 * (Math.PI * 2) * var42 + var44) * 0.45 + 0.55;
            double var48 = var46 * var11;
            double var50 = var13.x + var38 - var5.x;
            double var52 = var13.y + var48 - var5.y;
            double var54 = var13.z + var40 - var5.z;
            var1.push();
            var1.translate(var50, var52, var54);
            float var56 = 1.0F + 0.15F * (float)Math.sin(var6 / 400.0 + var21 * 1.5);
            float var57 = 0.19F * var56;
            double var58 = var17 * (0.5 + 0.5 * Math.sin(var21 * 123.45));
            if (var58 > 0.05) {
               var57 = (float)(var57 * (1.0 - var58 * 0.2));
               double var60 = var58 * 0.4;
               var1.translate(Math.cos(var36) * var60, 0.0, Math.sin(var36) * var60);
            }

            var1.push();
            float var68 = 12000.0F + (float)var26 * 2000.0F;
            float var61 = (float)(var6 % (long)Math.abs(var68)) / Math.abs(var68) * 360.0F;
            if (var21 % 3 == 0) {
               var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var61));
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var61));
            } else if (var21 % 3 == 1) {
               var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var61));
               var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var61));
            } else {
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var61));
               var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var61));
            }

            VertexConsumer var62 = var2.getBuffer(windowProcess);
            Matrix4f var63 = var1.peek().getPositionMatrix();
            DualLayerBoxVertexEmitter.handle(var62, var63, var18, var57);
            VertexConsumer var64 = var2.getBuffer(entryAnimate);
            DualLayerBoxVertexEmitter.process(var64, var63, var19, var57);
            var1.pop();
            var1.push();
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-Module.client.gameRenderer.getCamera().getYaw()));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Module.client.gameRenderer.getCamera().getPitch()));
            VertexConsumer var65 = var2.getBuffer(indexBind);
            Matrix4f var66 = var1.peek().getPositionMatrix();
            float var67 = var57 * 2.0F;
            var1.scale(var67, var67, var67);
            handle(var65, var66, var20, (int)(70.0F * var14));
            var1.pop();
            var1.pop();
         }
      }
   }

   private void render(MatrixStack var1, Immediate var2, LivingEntity var3, float var4) {
      if (var3 == null) {
         this.providerClose.clear();
      } else {
         Iterator var5 = this.providerClose.iterator();

         while (var5.hasNext()) {
            TargetESP.VertexEmitter var6 = (TargetESP.VertexEmitter)var5.next();
            if (var6.enabled.apply() != AnimationDirection.FORWARDS && var6.enabled.check() <= 0.0F) {
               var5.remove();
            }
         }

         long var19 = System.currentTimeMillis();
         windowConvert = Math.max(0.001F, Math.min(0.1F, (float)(var19 - presetSave) / 1000.0F));
         presetSave = var19;
         if (this.providerClose.size() < 50) {
            this.sourceBuild = this.sourceBuild + windowConvert;

            while (this.sourceBuild >= 0.02F && this.providerClose.size() < 50) {
               this.sourceBuild -= 0.02F;

               for (int var8 = 0; var8 < 1 && this.providerClose.size() < 50; var8++) {
                  double var9 = NumericTransform.handle(0.0F, 360.0F);
                  double var11 = Math.cos(var9 * Math.PI / 180.0) * 0.7F;
                  double var13 = NumericTransform.refresh(0.04F, 0.2F);
                  double var15 = Math.sin(var9 * Math.PI / 180.0) * 0.7F;
                  this.providerClose.add(new TargetESP.VertexEmitter(var3, var11, var13, var15));
               }
            }
         }

         if (!this.providerClose.isEmpty()) {
            float var20 = (float)summary.select();
            matrixBlend.handle();
            int var21 = var3.hurtTime;
            float var10 = (float)Math.sin(var21 * (Math.PI / 20));
            matrixBlend.handle(var10, 0.4F, EasingFunctions.handler);
            float var22 = matrixBlend.update();
            int var12 = handle(255, var22);
            int var23 = handle(255, var22);
            Vec3d var14 = Module.client.gameRenderer.getCamera().getPos();
            float var24 = Module.client.gameRenderer.getCamera().getPitch();
            float var16 = Module.client.gameRenderer.getCamera().getYaw();

            for (TargetESP.VertexEmitter var18 : this.providerClose) {
               var18.handle(var4);
               var18.handle(var1, var2, var12, var23, var20, var22, var4, var14, var24, var16, indexBind);
            }
         }
      }
   }

   record DataRecord(float x, float y, float w, float h) {
   }

   static class VertexEmitter {
      double instance;
      double data;
      double context;
      double config;
      double state;
      double cache;
      double output;
      double current;
      double active;
      long mode;
      LivingEntity selection;
      SmoothTimer enabled = new EaseTimer(500, 1.0);
      private double renderer;

      public VertexEmitter(LivingEntity var1, double var2, double var4, double var6) {
         this.instance = var2;
         this.data = var4;
         this.context = var6;
         this.selection = var1;
         this.mode = System.currentTimeMillis();
         this.renderer = NumericTransform.refresh(0.01F, 0.04F);
      }

      public long handle() {
         return this.mode;
      }

      public void handle(float var1) {
         long var2 = System.currentTimeMillis();
         long var4 = var2 - this.handle();
         this.enabled.process(var4 <= 800L ? AnimationDirection.FORWARDS : AnimationDirection.BACKWARDS);
         this.data = this.data + this.renderer * (TargetESP.windowConvert * 60.0F);
         if (this.selection != null) {
            Vec3d var6 = this.selection.getLerpedPos(var1);
            this.output = this.instance + var6.x;
            this.current = this.data + var6.y;
            this.active = this.context + var6.z;
         }
      }

      public void handle(
         MatrixStack var1, Immediate var2, int var3, int var4, float var5, float var6, float var7, Vec3d var8, float var9, float var10, RenderLayer var11
      ) {
         long var12 = System.currentTimeMillis();
         double var14 = (var12 - this.handle()) / 10.0;
         double var16 = NumericTransform.execute(0.2F);
         this.config = NumericTransform.compute(this.config, this.output - var8.x, var16);
         this.state = NumericTransform.compute(this.state, this.current - var8.y, var16);
         this.cache = NumericTransform.compute(this.cache, this.active - var8.z, var16);
         float var18 = this.enabled.check();
         if (!(var18 <= 0.0F)) {
            float var19 = 1.0F + 0.15F * (float)Math.sin((var12 - this.handle()) / 400.0);
            float var20 = 0.12F + 0.04F * var18;
            var1.push();
            var1.translate(this.config, this.state, this.cache);
            var1.push();
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)var14));
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)var14));
            var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)var14));
            Matrix4f var21 = var1.peek().getPositionMatrix();
            int var22 = TargetESP.handle(var3, (int)(70.0F * var5 * var18));
            VertexConsumer var23 = var2.getBuffer(TargetESP.windowProcess);
            DualLayerBoxVertexEmitter.handle(var23, var21, var22, var20);
            int var24 = TargetESP.handle(var3, (int)(225.0F * var5 * var18));
            VertexConsumer var25 = var2.getBuffer(TargetESP.entryAnimate);
            DualLayerBoxVertexEmitter.process(var25, var21, var24, var20);
            var1.pop();
            var1.push();
            var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var10));
            var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var9));
            VertexConsumer var26 = var2.getBuffer(var11);
            Matrix4f var27 = var1.peek().getPositionMatrix();
            float var28 = var20 * 2.0F;
            var1.scale(var28, var28, var28);
            TargetESP.handle(var26, var27, var4, (int)(70.0F * var5 * var18));
            var1.pop();
            var1.pop();
         }
      }
   }
}
