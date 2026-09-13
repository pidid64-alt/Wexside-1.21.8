package ru.wild.modules.visuals;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.RenderReentryLock;
import ru.wild.render.shader.ShaderSamplerHelper;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.GradientHelper;
import ru.wild.util.render.IrisCompatibility;

@ModuleRegister(
   name = "ChinaHat",
   description = "Добавляет над головой игроков декоративную шляпу в виде конуса, которая будет повторять цветовую схему выбранной темы.",
   category = ModuleCategory.Visuals
)
public final class ChinaHat extends Module {
   private static final float source = 0.0625F;
   private static final SpringAnimationSpec target = new SpringAnimationSpec(0.066F, 0.68F, 0.001F, 0.001F);
   private static final int[] pending = new int[]{16747247, 16754396, 8648959, 11141102, 16747247};
   private static final int[] previous = new int[]{6750183, 6014975, 4688895, 11730932, 6750183};
   private static final int[] latest = new int[]{16773227, 16751954, 16736157, 9304063, 16773227};
   private static final int[] summary = new int[]{11141048, 6485458, 8228095, 16755188, 11141048};
   private static final int[] matrixBlend = new int[]{8257383, 3405823, 16773210, 16727538, 8257383};
   private static final int[] vectorMatch = new int[]{16754632, 16769167, 11000063, 14067711, 16754632};
   private static final int[] itemProject = new int[]{8033279, 11561983, 5963734, 16736142, 8033279};
   private static final int[] responseCompute = new int[]{16757594, 16739146, 16732041, 13995263, 16757594};
   private static final int[] providerFetch = new int[]{14089215, 9169663, 9149951, 16777215, 14089215};
   private static final int[] profileDraw = new int[]{16736109, 16770140, 6160312, 7179519, 16736109};
   private static final int[] vectorPerform = new int[]{14001919, 16752603, 7733222, 16773260, 14001919};
   private static final int[] eventAttach = new int[]{13172552, 16773466, 3732223, 16735457, 13172552};
   private final SpringAnimation serverRead = new SpringAnimation(0.0F);
   private final Matrix4f positionAdvance = new Matrix4f();
   private final Matrix3f frameCheck = new Matrix3f();
   private final Matrix4f moduleCollect = new Matrix4f();
   private final Matrix3f providerClose = new Matrix3f();
   private final Matrix4f presetSave = new Matrix4f();
   private final Matrix4f windowConvert = new Matrix4f();
   private final Quaternionf presetWrite = new Quaternionf();
   private final Vector3f colorMeasure = new Vector3f();
   private final Vector3f animationSchedule = new Vector3f();
   private final Vector3f rendererScan = new Vector3f();
   private final Vector3f sourceBuild = new Vector3f();
   private final Vector3f outputCollapse = new Vector3f();
   private final Vector3f profileInvoke = new Vector3f();
   private final Vector3f sourceSchedule = new Vector3f();
   private final Vector3f timerRender = new Vector3f();
   private final ChinaHat.SecondaryVertexEmitter scaleSave = new ChinaHat.SecondaryVertexEmitter();
   private final ChinaHat.PrimaryVertexEmitter colorCompute = new ChinaHat.PrimaryVertexEmitter();
   private static final RenderReentryLock scaleAdapt = new RenderReentryLock();
   private static boolean textureRun;
   private boolean indexBind;
   private float actionRead;
   private float configCollapse;
   private float dataValidate;
   private float scaleRender;
   private float clientRefresh;
   private float keyFilter;
   private float requestAdapt;

   public ChinaHat() {
      ShaderSamplerHelper.handle();
   }

   @Override
   public void handle() {
      this.serverRead.handle(0.0F);
      scaleAdapt.resolve();
      this.indexBind = false;
      super.handle();
   }
   @Override
   public void process() {
      boolean var58 = false /* VF: Semaphore variable */;

      try {
         var58 = true;
         this.serverRead.handle(0.0F);
         var58 = false;
      } finally {
         if (var58) {
            try {
               scaleAdapt.resolve();
            } finally {
               try {
                  super.process();
               } finally {
                  ShaderSamplerHelper.execute();
               }
            }
         }
      }

      boolean var30 = false /* VF: Semaphore variable */;

      try {
         var30 = true;
         scaleAdapt.resolve();
         var30 = false;
      } finally {
         if (var30) {
            boolean var22 = false /* VF: Semaphore variable */;

            try {
               var22 = true;
               super.process();
               var22 = false;
            } finally {
               if (var22) {
                  ShaderSamplerHelper.execute();
               }
            }

            ShaderSamplerHelper.execute();
         }
      }

      try {
         super.process();
      } finally {
         ShaderSamplerHelper.execute();
      }
   }

   public static void refresh() {
      scaleAdapt.handle();
   }

   public static void handle(PlayerEntityRenderState var0, PlayerEntityModel var1, MatrixStack var2, VertexConsumerProvider var3, int var4) {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         ChinaHat var5 = WildClient.instance.data.handle(ChinaHat.class);
         if (var5 != null) {
            var5.handle(var0, var1, var2, var3);
         }
      }
   }

   private void handle(PlayerEntityRenderState var1, PlayerEntityModel var2, MatrixStack var3, VertexConsumerProvider var4) {
      if (this.enabled
         && Module.client != null
         && Module.client.world != null
         && Module.client.player != null
         && var1 != null
         && var2 != null
         && var3 != null
         && var4 != null) {
         if (var1.id == Module.client.player.getId()) {
            if (Module.client.options.getPerspective() != Perspective.FIRST_PERSON) {
               if (!var1.spectator && !var1.invisible && !var1.invisibleToPlayer) {
                  if (IrisCompatibility.update()) {
                     float var5 = this.serverRead.handle(1.0F, target);
                     if (!(var5 <= 0.001F)) {
                        this.colorCompute.handle(var1.age);
                        this.handle(var2.head, var3.peek());
                        this.actionRead = this.colorCompute.instance.instance;
                        this.configCollapse = this.colorCompute.instance.data;
                        this.dataValidate = this.colorCompute.instance.context;
                        this.scaleRender = this.colorCompute.data.instance;
                        this.clientRefresh = this.colorCompute.data.data;
                        this.keyFilter = this.colorCompute.data.context;
                        this.requestAdapt = var5;
                        scaleAdapt.process();
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean render() {
      boolean var0 = textureRun;
      textureRun = false;
      return var0;
   }

   @EventHandler(handle = 4)
   private void handle(WorldRenderContext var1) {
      boolean var2 = scaleAdapt.compute();
      if (!IrisCompatibility.update()) {
         if (!this.indexBind) {
            this.indexBind = ShaderSamplerHelper.execute();
         }
      } else {
         this.indexBind = false;
         if (var2) {
            if (this.enabled && var1 != null && Module.client != null && Module.client.world != null && Module.client.player != null) {
               Entry var3 = var1.apply().peek();
               this.moduleCollect.set(var3.getPositionMatrix()).mul(this.positionAdvance);
               this.providerClose.set(var3.getNormalMatrix()).mul(this.frameCheck);
               this.sourceBuild.set(0.0F, 1.0F, 0.0F).mulDirection(this.moduleCollect).normalize();
               this.outputCollapse.set(0.0F, -0.515625F, 0.0F).mulPosition(this.moduleCollect);
               this.profileInvoke.set(GradientHelper.animationDraw, 0.0F, 0.0F).mulDirection(this.moduleCollect);
               this.sourceSchedule.set(0.0F, 0.0F, GradientHelper.animationDraw).mulDirection(this.moduleCollect);
               this.timerRender.set(0.0F, GradientHelper.animator, 0.0F).mulDirection(this.moduleCollect);
               float var4 = this.tick();
               this.handle(var3, var1.check());
               var1.update().prepare();
               this.presetSave.set(var1.prepare()).invert();
               if (ShaderSamplerHelper.handle(
                  this.actionRead,
                  this.configCollapse,
                  this.dataValidate,
                  this.scaleRender,
                  this.clientRefresh,
                  this.keyFilter,
                  this.requestAdapt,
                  this.colorMeasure.x,
                  this.colorMeasure.y,
                  this.colorMeasure.z,
                  1.0F,
                  this.animationSchedule.x,
                  this.animationSchedule.y,
                  this.animationSchedule.z,
                  0.26F,
                  var4,
                  this.sourceBuild.x,
                  this.sourceBuild.y,
                  this.sourceBuild.z,
                  this.presetSave,
                  this.outputCollapse.x,
                  this.outputCollapse.y,
                  this.outputCollapse.z,
                  this.profileInvoke.x,
                  this.profileInvoke.y,
                  this.profileInvoke.z,
                  this.sourceSchedule.x,
                  this.sourceSchedule.y,
                  this.sourceSchedule.z,
                  this.timerRender.x,
                  this.timerRender.y,
                  this.timerRender.z
               )) {
                  textureRun = true;
                  boolean var5 = false;

                  try {
                     VertexConsumer var6 = var1.update().handle(ShaderSamplerHelper.resolve());
                     this.process(var6, this.moduleCollect, this.providerClose);
                     var5 = true;
                     var1.update().execute().draw(ShaderSamplerHelper.resolve());
                     ShaderSamplerHelper.update();
                     var5 = false;
                     VertexConsumer var7 = var1.update().handle(ShaderSamplerHelper.process());
                     this.handle(var7, this.moduleCollect, this.providerClose);
                     var5 = true;
                     var1.update().execute().draw(ShaderSamplerHelper.process());
                     VertexConsumer var8 = var1.update().handle(ShaderSamplerHelper.compute());
                     this.handle(var8, this.moduleCollect, this.providerClose);
                     var1.update().execute().draw(ShaderSamplerHelper.compute());
                  } finally {
                     if (var5) {
                        ShaderSamplerHelper.update();
                     }
                  }
               }
            }
         }
      }
   }

   private void handle(ModelPart var1, Entry var2) {
      this.positionAdvance.set(var2.getPositionMatrix()).translate(var1.originX * 0.0625F, var1.originY * 0.0625F, var1.originZ * 0.0625F);
      this.frameCheck.set(var2.getNormalMatrix());
      if (var1.pitch != 0.0F || var1.yaw != 0.0F || var1.roll != 0.0F) {
         this.presetWrite.rotationZYX(var1.roll, var1.yaw, var1.pitch);
         this.positionAdvance.rotate(this.presetWrite);
         this.frameCheck.rotate(this.presetWrite);
      }

      if (var1.xScale != 1.0F || var1.yScale != 1.0F || var1.zScale != 1.0F) {
         this.positionAdvance.scale(var1.xScale, var1.yScale, var1.zScale);
         this.handle(var1.xScale, var1.yScale, var1.zScale);
      }
   }

   private void handle(float var1, float var2, float var3) {
      if (Math.abs(var1) != Math.abs(var2) || Math.abs(var2) != Math.abs(var3)) {
         this.frameCheck.scale(1.0F / var1, 1.0F / var2, 1.0F / var3);
      } else if (var1 < 0.0F || var2 < 0.0F || var3 < 0.0F) {
         this.frameCheck.scale(Math.signum(var1), Math.signum(var2), Math.signum(var3));
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, Matrix3f var3) {
      this.scaleSave.handle(var1, var2, var3);
      GradientHelper.handle(this.scaleSave);
   }

   private void process(VertexConsumer var1, Matrix4f var2, Matrix3f var3) {
      this.scaleSave.handle(var1, var2, var3);
      GradientHelper.process(this.scaleSave);
   }

   private void handle(Entry var1, float var2) {
      float var3 = Module.client.world.getSkyAngleRadians(var2);
      float var4 = process((float)Math.cos(var3) * 0.5F + 0.5F, 0.18F, 1.0F);
      this.colorMeasure
         .set(-((float)Math.sin(var3)), 0.36F + var4 * 0.64F, (float)Math.cos(var3) * 0.42F)
         .normalize()
         .mulDirection(var1.getPositionMatrix())
         .normalize();
      this.animationSchedule.set(0.78F + var4 * 0.22F, 0.62F + var4 * 0.27F, 0.52F + var4 * 0.35F);
   }

   private float tick() {
      this.windowConvert.set(this.moduleCollect).invert();
      this.rendererScan.set(0.0F, 0.0F, 0.0F).mulPosition(this.windowConvert);
      float var1 = (float)Math.sqrt(this.rendererScan.x * this.rendererScan.x + this.rendererScan.z * this.rendererScan.z);
      float var2 = this.rendererScan.y - -0.515625F;
      return GradientHelper.handle(var1, var2);
   }

   static void handle(
      VertexConsumer var0,
      Matrix4f var1,
      Matrix3f var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      int var13,
      float var14
   ) {
      float var15 = var1.m00() * var3 + var1.m10() * var4 + var1.m20() * var5 + var1.m30();
      float var16 = var1.m01() * var3 + var1.m11() * var4 + var1.m21() * var5 + var1.m31();
      float var17 = var1.m02() * var3 + var1.m12() * var4 + var1.m22() * var5 + var1.m32();
      float var18 = var2.m00() * var6 + var2.m10() * var7 + var2.m20() * var8;
      float var19 = var2.m01() * var6 + var2.m11() * var7 + var2.m21() * var8;
      float var20 = var2.m02() * var6 + var2.m12() * var7 + var2.m22() * var8;
      float var21 = handle(var18 * var18 + var19 * var19 + var20 * var20);
      var0.vertex(var15, var16, var17)
         .texture(var9, var10)
         .color(var11, var12, var13, handle(Math.round(process(var14, 0.0F, 1.0F) * 255.0F)))
         .normal(var18 * var21, var19 * var21, var20 * var21);
   }

   private static float handle(float var0) {
      return var0 <= 1.0E-6F ? 1.0F : (float)(1.0 / Math.sqrt(var0));
   }

   private static float process(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   private static int handle(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }

   static void handle(int[] var0, float var1, ChinaHat.VertexEmitter var2) {
      float var3 = var1 - (float)Math.floor(var1);
      float var4 = var3 * (var0.length - 1);
      int var5 = Math.min(var0.length - 2, Math.max(0, (int)Math.floor(var4)));
      handle(var0[var5] & 16777215, var0[var5 + 1] & 16777215, resolve(var4 - var5), var2);
   }

   static void handle(ChinaHat.VertexEmitter var0, float var1, ChinaHat.VertexEmitter var2) {
      handle(var0.instance, var0.data, var0.context, 1.0F, 1.0F, 1.0F, var1, var2);
   }

   private static void handle(int var0, int var1, float var2, ChinaHat.VertexEmitter var3) {
      handle(
         (var0 >> 16 & 0xFF) * 0.003921569F,
         (var0 >> 8 & 0xFF) * 0.003921569F,
         (var0 & 0xFF) * 0.003921569F,
         (var1 >> 16 & 0xFF) * 0.003921569F,
         (var1 >> 8 & 0xFF) * 0.003921569F,
         (var1 & 0xFF) * 0.003921569F,
         var2,
         var3
      );
   }

   private static void handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, ChinaHat.VertexEmitter var7) {
      float var8 = process(var0);
      float var9 = process(var1);
      float var10 = process(var2);
      float var11 = process(var3);
      float var12 = process(var4);
      float var13 = process(var5);
      float var14 = 0.41222146F * var8 + 0.53633255F * var9 + 0.051445995F * var10;
      float var15 = 0.2119035F * var8 + 0.6806995F * var9 + 0.10739696F * var10;
      float var16 = 0.08830246F * var8 + 0.28171885F * var9 + 0.6299787F * var10;
      float var17 = 0.41222146F * var11 + 0.53633255F * var12 + 0.051445995F * var13;
      float var18 = 0.2119035F * var11 + 0.6806995F * var12 + 0.10739696F * var13;
      float var19 = 0.08830246F * var11 + 0.28171885F * var12 + 0.6299787F * var13;
      float var20 = (float)Math.cbrt(var14);
      float var21 = (float)Math.cbrt(var15);
      float var22 = (float)Math.cbrt(var16);
      float var23 = (float)Math.cbrt(var17);
      float var24 = (float)Math.cbrt(var18);
      float var25 = (float)Math.cbrt(var19);
      float var26 = process(var6, 0.0F, 1.0F);
      float var27 = compute(
         0.21045426F * var20 + 0.7936178F * var21 - 0.004072047F * var22, 0.21045426F * var23 + 0.7936178F * var24 - 0.004072047F * var25, var26
      );
      float var28 = compute(1.9779985F * var20 - 2.4285922F * var21 + 0.4505937F * var22, 1.9779985F * var23 - 2.4285922F * var24 + 0.4505937F * var25, var26);
      float var29 = compute(
         0.025904037F * var20 + 0.78277177F * var21 - 0.80867577F * var22, 0.025904037F * var23 + 0.78277177F * var24 - 0.80867577F * var25, var26
      );
      if (!handle(var27, var28, var29, 1.0F, var7)) {
         float var30 = 0.0F;
         float var31 = 1.0F;

         for (int var32 = 0; var32 < 6; var32++) {
            float var33 = (var30 + var31) * 0.5F;
            if (handle(var27, var28, var29, var33, var7)) {
               var30 = var33;
            } else {
               var31 = var33;
            }
         }

         handle(var27, var28, var29, var30, var7);
      }

      var7.instance = compute(process(var7.instance, 0.0F, 1.0F));
      var7.data = compute(process(var7.data, 0.0F, 1.0F));
      var7.context = compute(process(var7.context, 0.0F, 1.0F));
   }

   private static boolean handle(float var0, float var1, float var2, float var3, ChinaHat.VertexEmitter var4) {
      float var5 = var0 + 0.39633778F * var1 * var3 + 0.21580376F * var2 * var3;
      float var6 = var0 - 0.105561346F * var1 * var3 - 0.06385417F * var2 * var3;
      float var7 = var0 - 0.08948418F * var1 * var3 - 1.2914855F * var2 * var3;
      float var8 = var5 * var5 * var5;
      float var9 = var6 * var6 * var6;
      float var10 = var7 * var7 * var7;
      var4.instance = 4.0767417F * var8 - 3.3077116F * var9 + 0.23096994F * var10;
      var4.data = -1.268438F * var8 + 2.6097574F * var9 - 0.34131938F * var10;
      var4.context = -0.0041960864F * var8 - 0.7034186F * var9 + 1.7076147F * var10;
      return var4.instance >= 0.0F && var4.instance <= 1.0F && var4.data >= 0.0F && var4.data <= 1.0F && var4.context >= 0.0F && var4.context <= 1.0F;
   }

   private static float process(float var0) {
      return var0 <= 0.04045F ? var0 * 0.07739938F : (float)Math.pow((var0 + 0.055F) * 0.94786733F, 2.4F);
   }

   private static float compute(float var0) {
      return var0 <= 0.0031308F ? var0 * 12.92F : 1.055F * (float)Math.pow(var0, 0.41666666F) - 0.055F;
   }

   private static float resolve(float var0) {
      float var1 = process(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   private static float compute(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   static int[] handle(ThemePalette var0) {
      return switch (var0) {
         case ASTOLFO_RAINBOW -> pending;
         case LAGUNE_RAINBOW -> previous;
         case HALF_RAINBOW -> latest;
         case AURORA_RAINBOW -> summary;
         case NEON_RAINBOW -> matrixBlend;
         case BLOSSOM_RAINBOW -> vectorMatch;
         case ABYSS_RAINBOW -> itemProject;
         case SUNSET_RAINBOW -> responseCompute;
         case GLACIER_RAINBOW -> providerFetch;
         case CHROMA_RAINBOW -> profileDraw;
         case DREAM_RAINBOW -> vectorPerform;
         case TOXIC_RAINBOW -> eventAttach;
         default -> null;
      };
   }

   static final class PrimaryVertexEmitter {
      final ChinaHat.VertexEmitter instance = new ChinaHat.VertexEmitter();
      final ChinaHat.VertexEmitter data = new ChinaHat.VertexEmitter();
      final ChinaHat.VertexEmitter context = new ChinaHat.VertexEmitter();

      void handle(float var1) {
         ThemePalette var2 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
         int[] var3 = ChinaHat.handle(var2);
         if (var3 != null) {
            float var4 = var1 * 0.0062F;
            ChinaHat.handle(var3, var4, this.data);
            ChinaHat.handle(var3, var4 + 0.34F, this.context);
            ChinaHat.handle(this.context, 0.14F, this.instance);
         } else if (var2 == ThemePalette.WILD) {
            this.instance.handle(9348607);
            this.data.handle(6061311);
         } else {
            int var5 = var2.handle().getRGB() & 16777215;
            this.data.handle(var5);
            ChinaHat.handle(this.data, 0.18F, this.instance);
         }
      }
   }

   static final class SecondaryVertexEmitter implements GradientHelper.Callback {
      private VertexConsumer instance;
      private Matrix4f data;
      private Matrix3f context;

      void handle(VertexConsumer var1, Matrix4f var2, Matrix3f var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }

      @Override
      public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
         ChinaHat.handle(this.instance, this.data, this.context, var1, var2, var3, var4, var5, var6, var7, var8, 255, 255, 255, var9);
      }
   }

   static final class VertexEmitter {
      float instance;
      float data;
      float context;

      void handle(int var1) {
         this.instance = (var1 >> 16 & 0xFF) * 0.003921569F;
         this.data = (var1 >> 8 & 0xFF) * 0.003921569F;
         this.context = (var1 & 0xFF) * 0.003921569F;
      }
   }
}
