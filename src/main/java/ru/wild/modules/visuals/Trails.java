package ru.wild.modules.visuals;

import java.util.List;
import java.util.Map;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.TargetSelectorRegistry;
import ru.wild.gui.hud.HudElementRenderer;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.render.shader.ShaderBuildResult;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderParameter;
import ru.wild.render.shader.TrailsGlassShader;

@ModuleRegister(name = "Trails", description = "Оставляет за игроком красивый след.", category = ModuleCategory.Visuals)
public final class Trails extends Module implements HudElementRenderer {
   private static final int target = 64;
   private static final int pending = 65;
   private static final int previous = 4;
   private static final int latest = 14;
   private static final int summary = 12;
   private static final int matrixBlend = 926;
   private static final double vectorMatch = 0.0036;
   private static final double itemProject = 0.00108;
   private static final double responseCompute = 36.0;
   private static final float providerFetch = 0.3F;
   private static final float profileDraw = 1.45F;
   private static final float vectorPerform = 0.42F;
   private static final float eventAttach = 0.95F;
   private static final float serverRead = 0.3F;
   private static final float positionAdvance = 0.3F;
   private static final float frameCheck = 0.18F;
   private static final float moduleCollect = 0.4F;
   private static final float providerClose = 0.16F;
   private static final float presetSave = 3.8F;
   private static final float windowConvert = 11.0F;
   private static final float presetWrite = 5.5F;
   private static final double colorMeasure = 0.36;
   private static final float animationSchedule = 12.0F;
   private static final float rendererScan = 0.5F;
   private static final long sourceBuild = System.nanoTime();
   public static final ShaderPresetSetting source = new ShaderPresetSetting("Foundry Shader", LivePreviewRenderer.TRAILS);
   private static final float[] outputCollapse = new float[13];
   private static final float[] profileInvoke = new float[13];
   private static final float[] sourceSchedule = new float[13];
   private final double[] timerRender = new double[64];
   private final double[] scaleSave = new double[64];
   private final double[] colorCompute = new double[64];
   private final float[] scaleAdapt = new float[64];
   private int textureRun;
   private final double[] indexBind = new double[65];
   private final double[] actionRead = new double[65];
   private final double[] configCollapse = new double[65];
   private final float[] dataValidate = new float[65];
   private final double[] scaleRender = new double[926];
   private final double[] clientRefresh = new double[926];
   private final double[] keyFilter = new double[926];
   private final double[] requestAdapt = new double[926];
   private final double[] timerMeasure = new double[926];
   private final double[] vectorEncode = new double[926];
   private final double[] requestReceive = new double[926];
   private final double[] windowProcess = new double[926];
   private final double[] packetSave = new double[926];
   private final float[] entryAnimate = new float[926];
   private final float[] playerCollect = new float[926];
   private int stateApply;
   private long matrixFilter;
   private float layerSample = 0.18F;
   private double worldSend;
   private double targetWrite;
   private double resultEncode;
   private float messageParse;
   private boolean providerRead;
   private float matrixBlend2 = 0.95F;
   private final Trails.ShaderState scalePerform = new Trails.ShaderState();

   public Trails() {
      TrailsGlassShader.handle();
      this.handle(source);
   }

   @Override
   public void handle() {
      this.render();
      super.handle();
      TargetSelectorRegistry.handle().handle(this, this);
   }

   @Override
   public void process() {
      TargetSelectorRegistry.handle().handle(this);
      this.render();
      super.process();
   }

   @Override
   public LivePreviewRenderer compute() {
      return LivePreviewRenderer.TRAILS;
   }

   @Override
   public String resolve() {
      String var1 = refresh();
      return var1 != null && !var1.isBlank() ? var1 : null;
   }

   @Override
   public boolean update() {
      return true;
   }

   public static String refresh() {
      String var0 = source.refresh();
      return var0 == null ? "" : var0;
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.render();
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      TargetSelectorRegistry.handle().process(this, this);
      if (Module.client.world != null && Module.client.player != null) {
         if (Module.client.options != null && Module.client.options.getPerspective() != null && !Module.client.options.getPerspective().isFirstPerson()) {
            long var2 = System.nanoTime();
            float var4 = this.matrixFilter == 0L ? 0.0F : Math.min((float)(var2 - this.matrixFilter) / 1.0E9F, 0.1F);
            this.matrixFilter = var2;
            float var5;
            if (Module.client.player.isGliding()) {
               var5 = 0.3F;
            } else if (Module.client.player.isSwimming()) {
               var5 = 0.3F;
            } else {
               var5 = 0.95F;
            }

            float var6 = 1.0F - (float)Math.exp(-8.0F * var4);
            this.matrixBlend2 = this.matrixBlend2 + (var5 - this.matrixBlend2) * var6;
            Vec3d var7 = Module.client.player.getLerpedPos(var1.resolve());
            double var8 = var7.x;
            double var10 = var7.y + this.matrixBlend2;
            double var12 = var7.z;
            Vec3d var14 = Module.client.player.getVelocity();
            double var15 = Math.sqrt(var14.x * var14.x + var14.y * var14.y + var14.z * var14.z);
            double var17 = Math.sqrt(var14.x * var14.x + var14.z * var14.z);
            boolean var19 = var17 > 0.02 || var15 > 0.045;
            float var20 = (float)Math.min(1.0, var15 / 0.36);
            float var21 = var19 ? 0.18F + 0.4F * var20 : 0.16F;
            float var22 = 1.0F - (float)Math.exp(-3.8F * var4);
            this.layerSample = this.layerSample + (var21 - this.layerSample) * var22;
            if (var19) {
               this.worldSend = var8;
               this.targetWrite = var10;
               this.resultEncode = var12;
               this.providerRead = true;
               float var23 = 1.0F - (float)Math.exp(-11.0F * var4);
               this.messageParse = this.messageParse + (1.0F - this.messageParse) * var23;
               this.handle(var8, var10, var12);
            } else {
               this.messageParse = this.messageParse * (float)Math.exp(-5.5F * var4);
               if (this.messageParse < 0.01F) {
                  this.messageParse = 0.0F;
                  this.providerRead = false;
               }
            }

            this.handle(var4);
            if (this.textureRun >= 1) {
               Camera var38 = Module.client.gameRenderer.getCamera();
               double var24 = var38.getPos().x;
               double var26 = var38.getPos().y;
               double var28 = var38.getPos().z;
               this.scalePerform.handle(this.encodePoint(), refresh());
               this.tick();
               if (this.stateApply >= 2) {
                  this.drawAnimation();
                  MatrixStack var30 = var1.compute();
                  Matrix4f var31 = var30.peek().getPositionMatrix();
                  Immediate var32 = WorldVertexBuffer.handle();

                  try {
                     VertexConsumer var33 = var32.getBuffer(TrailsGlassShader.process());
                     this.handle(var33, var31, var24, var26, var28, 1.0F, 1.0F);
                     VertexConsumer var34 = var32.getBuffer(TrailsGlassShader.compute());
                     this.handle(var34, var31, var24, var26, var28, 1.45F, 0.42F);
                  } finally {
                     WorldVertexBuffer.process();
                  }
               }
            }
         } else {
            this.render();
         }
      }
   }

   private void handle(double var1, double var3, double var5) {
      if (this.textureRun == 0) {
         this.timerRender[0] = var1;
         this.scaleSave[0] = var3;
         this.colorCompute[0] = var5;
         this.scaleAdapt[0] = 0.0F;
         this.textureRun = 1;
      } else {
         int var7 = this.textureRun - 1;
         double var8 = var1 - this.timerRender[var7];
         double var10 = var3 - this.scaleSave[var7];
         double var12 = var5 - this.colorCompute[var7];
         double var14 = var8 * var8 + var10 * var10 + var12 * var12;
         if (var14 > 36.0) {
            this.render();
            this.handle(var1, var3, var5);
         } else if (!(var14 < 0.0036)) {
            if (this.textureRun == 64) {
               System.arraycopy(this.timerRender, 1, this.timerRender, 0, 63);
               System.arraycopy(this.scaleSave, 1, this.scaleSave, 0, 63);
               System.arraycopy(this.colorCompute, 1, this.colorCompute, 0, 63);
               System.arraycopy(this.scaleAdapt, 1, this.scaleAdapt, 0, 63);
               this.textureRun = 63;
            }

            this.timerRender[this.textureRun] = var1;
            this.scaleSave[this.textureRun] = var3;
            this.colorCompute[this.textureRun] = var5;
            this.scaleAdapt[this.textureRun] = 0.0F;
            this.textureRun++;
         }
      }
   }

   private void handle(float var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < this.textureRun; var3++) {
         float var4 = this.scaleAdapt[var3] + var1;
         if (var4 < this.layerSample) {
            if (var2 != var3) {
               this.timerRender[var2] = this.timerRender[var3];
               this.scaleSave[var2] = this.scaleSave[var3];
               this.colorCompute[var2] = this.colorCompute[var3];
            }

            this.scaleAdapt[var2] = var4;
            var2++;
         }
      }

      this.textureRun = var2;
   }

   private void render() {
      this.textureRun = 0;
      this.stateApply = 0;
      this.messageParse = 0.0F;
      this.providerRead = false;
   }

   private void tick() {
      this.stateApply = 0;

      for (int var1 = 0; var1 < this.textureRun; var1++) {
         this.indexBind[var1] = this.timerRender[var1];
         this.actionRead[var1] = this.scaleSave[var1];
         this.configCollapse[var1] = this.colorCompute[var1];
         float var2 = this.scaleAdapt[var1] / Math.max(0.001F, this.layerSample);
         if (var2 < 0.0F) {
            var2 = 0.0F;
         }

         if (var2 > 1.0F) {
            var2 = 1.0F;
         }

         this.dataValidate[var1] = 1.0F - var2 * var2 * (3.0F - 2.0F * var2);
      }

      int var10 = this.textureRun;
      if (this.textureRun > 0 && this.providerRead && this.messageParse > 0.02F) {
         int var11 = this.textureRun - 1;
         double var3 = this.worldSend - this.timerRender[var11];
         double var5 = this.targetWrite - this.scaleSave[var11];
         double var7 = this.resultEncode - this.colorCompute[var11];
         if (var3 * var3 + var5 * var5 + var7 * var7 > 0.00108) {
            this.indexBind[this.textureRun] = this.worldSend;
            this.actionRead[this.textureRun] = this.targetWrite;
            this.configCollapse[this.textureRun] = this.resultEncode;
            this.dataValidate[this.textureRun] = this.messageParse;
            var10 = this.textureRun + 1;
         }
      }

      if (var10 >= 2) {
         this.scaleRender[0] = this.indexBind[0];
         this.clientRefresh[0] = this.actionRead[0];
         this.keyFilter[0] = this.configCollapse[0];
         this.entryAnimate[0] = this.dataValidate[0];
         this.stateApply = 1;

         for (int var12 = 0; var12 < var10 - 1; var12++) {
            int var14 = Math.max(0, var12 - 1);
            int var4 = var12;
            int var20 = var12 + 1;
            int var6 = Math.min(var10 - 1, var12 + 2);
            int var22 = this.handle(var14, var4, var20, var6);

            for (int var8 = 1; var8 <= var22; var8++) {
               if (this.stateApply >= 926) {
                  return;
               }

               float var9 = (float)var8 / var22;
               this.scaleRender[this.stateApply] = handle(this.indexBind[var14], this.indexBind[var4], this.indexBind[var20], this.indexBind[var6], var9);
               this.clientRefresh[this.stateApply] = handle(this.actionRead[var14], this.actionRead[var4], this.actionRead[var20], this.actionRead[var6], var9);
               this.keyFilter[this.stateApply] = handle(
                  this.configCollapse[var14], this.configCollapse[var4], this.configCollapse[var20], this.configCollapse[var6], var9
               );
               this.entryAnimate[this.stateApply] = handle(this.dataValidate[var4], this.dataValidate[var20], var9);
               this.stateApply++;
            }
         }

         this.playerCollect[0] = 0.0F;
         float var13 = 0.0F;

         for (int var15 = 1; var15 < this.stateApply; var15++) {
            double var18 = this.scaleRender[var15] - this.scaleRender[var15 - 1];
            double var21 = this.clientRefresh[var15] - this.clientRefresh[var15 - 1];
            double var23 = this.keyFilter[var15] - this.keyFilter[var15 - 1];
            var13 += (float)Math.sqrt(var18 * var18 + var21 * var21 + var23 * var23);
            this.playerCollect[var15] = var13;
         }

         if (var13 > 1.0E-4F) {
            float var16 = 1.0F / var13;

            for (int var19 = 0; var19 < this.stateApply; var19++) {
               this.playerCollect[var19] = this.playerCollect[var19] * var16;
            }
         } else {
            for (int var17 = 0; var17 < this.stateApply; var17++) {
               this.playerCollect[var17] = this.stateApply > 1 ? (float)var17 / (this.stateApply - 1) : 0.0F;
            }
         }
      }
   }

   private void drawAnimation() {
      if (this.stateApply >= 1) {
         int var1 = Math.min(this.stateApply - 1, 1);
         double var2 = this.scaleRender[var1] - this.scaleRender[0];
         double var4 = this.clientRefresh[var1] - this.clientRefresh[0];
         double var6 = this.keyFilter[var1] - this.keyFilter[0];
         double var8 = Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
         double var10;
         double var12;
         double var14;
         if (var8 > 1.0E-6) {
            var10 = var2 / var8;
            var12 = var4 / var8;
            var14 = var6 / var8;
         } else {
            var10 = 1.0;
            var12 = 0.0;
            var14 = 0.0;
         }

         double var16 = Math.sqrt(var2 * var2 + var6 * var6);
         double var18;
         double var20;
         double var22;
         if (var16 > 1.0E-6) {
            var18 = -var6 / var16;
            var20 = 0.0;
            var22 = var2 / var16;
         } else {
            var18 = 1.0;
            var20 = 0.0;
            var22 = 0.0;
         }

         this.requestAdapt[0] = var18;
         this.timerMeasure[0] = var20;
         this.vectorEncode[0] = var22;
         double var24 = var12 * var22 - var14 * var20;
         double var26 = var14 * var18 - var10 * var22;
         double var28 = var10 * var20 - var12 * var18;
         double var30 = Math.sqrt(var24 * var24 + var26 * var26 + var28 * var28);
         if (var30 > 1.0E-6) {
            var24 /= var30;
            var26 /= var30;
            var28 /= var30;
         } else {
            var24 = 0.0;
            var26 = 1.0;
            var28 = 0.0;
         }

         this.requestReceive[0] = var24;
         this.windowProcess[0] = var26;
         this.packetSave[0] = var28;

         for (int var32 = 1; var32 < this.stateApply; var32++) {
            int var33 = var32 - 1;
            int var34 = Math.min(this.stateApply - 1, var32 + 1);
            double var35 = this.scaleRender[var34] - this.scaleRender[var33];
            double var37 = this.clientRefresh[var34] - this.clientRefresh[var33];
            double var39 = this.keyFilter[var34] - this.keyFilter[var33];
            double var41 = Math.sqrt(var35 * var35 + var37 * var37 + var39 * var39);
            if (var41 < 1.0E-6) {
               this.requestAdapt[var32] = this.requestAdapt[var32 - 1];
               this.timerMeasure[var32] = this.timerMeasure[var32 - 1];
               this.vectorEncode[var32] = this.vectorEncode[var32 - 1];
               this.requestReceive[var32] = this.requestReceive[var32 - 1];
               this.windowProcess[var32] = this.windowProcess[var32 - 1];
               this.packetSave[var32] = this.packetSave[var32 - 1];
            } else {
               double var43 = var35 / var41;
               double var45 = var37 / var41;
               double var47 = var39 / var41;
               double var49 = this.requestAdapt[var32 - 1];
               double var51 = this.timerMeasure[var32 - 1];
               double var53 = this.vectorEncode[var32 - 1];
               double var55 = var49 * var43 + var51 * var45 + var53 * var47;
               var49 -= var43 * var55;
               var51 -= var45 * var55;
               var53 -= var47 * var55;
               double var57 = Math.sqrt(var49 * var49 + var51 * var51 + var53 * var53);
               if (var57 > 1.0E-6) {
                  var49 /= var57;
                  var51 /= var57;
                  var53 /= var57;
               } else {
                  double var59 = Math.sqrt(var35 * var35 + var39 * var39);
                  if (var59 > 1.0E-6) {
                     var49 = -var39 / var59;
                     var51 = 0.0;
                     var53 = var35 / var59;
                  } else {
                     var49 = 1.0;
                     var51 = 0.0;
                     var53 = 0.0;
                  }
               }

               this.requestAdapt[var32] = var49;
               this.timerMeasure[var32] = var51;
               this.vectorEncode[var32] = var53;
               double var76 = var45 * var53 - var47 * var51;
               double var61 = var47 * var49 - var43 * var53;
               double var63 = var43 * var51 - var45 * var49;
               double var65 = Math.sqrt(var76 * var76 + var61 * var61 + var63 * var63);
               if (var65 > 1.0E-6) {
                  var76 /= var65;
                  var61 /= var65;
                  var63 /= var65;
               } else {
                  var76 = 0.0;
                  var61 = 1.0;
                  var63 = 0.0;
               }

               this.requestReceive[var32] = var76;
               this.windowProcess[var32] = var61;
               this.packetSave[var32] = var63;
            }
         }
      }
   }

   private int handle(int var1, int var2, int var3, int var4) {
      double var5 = this.indexBind[var2] - this.indexBind[var1];
      double var7 = this.actionRead[var2] - this.actionRead[var1];
      double var9 = this.configCollapse[var2] - this.configCollapse[var1];
      double var11 = this.indexBind[var3] - this.indexBind[var2];
      double var13 = this.actionRead[var3] - this.actionRead[var2];
      double var15 = this.configCollapse[var3] - this.configCollapse[var2];
      double var17 = this.indexBind[var4] - this.indexBind[var3];
      double var19 = this.actionRead[var4] - this.actionRead[var3];
      double var21 = this.configCollapse[var4] - this.configCollapse[var3];
      double var23 = Math.sqrt(var5 * var5 + var7 * var7 + var9 * var9);
      double var25 = Math.sqrt(var11 * var11 + var13 * var13 + var15 * var15);
      double var27 = Math.sqrt(var17 * var17 + var19 * var19 + var21 * var21);
      double var29 = var23 > 1.0E-6 && var25 > 1.0E-6 ? (var5 * var11 + var7 * var13 + var9 * var15) / (var23 * var25) : 1.0;
      double var31 = var25 > 1.0E-6 && var27 > 1.0E-6 ? (var11 * var17 + var13 * var19 + var15 * var21) / (var25 * var27) : 1.0;
      double var33 = Math.max(1.0 - var29, 1.0 - var31);
      if (var33 < 0.0) {
         var33 = 0.0;
      }

      if (var33 > 2.0) {
         var33 = 2.0;
      }

      double var35 = Math.min(1.0, Math.pow(var33 * 12.0, 0.5));
      int var37 = (int)Math.round(10.0 * var35);
      return 4 + var37;
   }

   private void handle(VertexConsumer var1, Matrix4f var2, double var3, double var5, double var7, float var9, float var10) {
      for (int var11 = 0; var11 < this.stateApply - 1; var11++) {
         float var12 = this.playerCollect[var11];
         float var13 = this.playerCollect[var11 + 1];
         float var14 = this.entryAnimate[var11] * var10 * this.scalePerform.source;
         float var15 = this.entryAnimate[var11 + 1] * var10 * this.scalePerform.source;
         float var16 = 0.3F * var9 * this.scalePerform.animator * process(var12);
         float var17 = 0.3F * var9 * this.scalePerform.animator * process(var13);
         int var18 = compute(var14);
         int var19 = compute(var15);
         if ((var18 | var19) != 0 && (!(var16 <= 1.0E-4F) || !(var17 <= 1.0E-4F))) {
            int var20 = this.scalePerform.handle(var12);
            int var21 = this.scalePerform.handle(var13);
            double var22 = this.scaleRender[var11] - var3;
            double var24 = this.clientRefresh[var11] - var5;
            double var26 = this.keyFilter[var11] - var7;
            double var28 = this.scaleRender[var11 + 1] - var3;
            double var30 = this.clientRefresh[var11 + 1] - var5;
            double var32 = this.keyFilter[var11 + 1] - var7;
            double var34 = this.requestAdapt[var11];
            double var36 = this.timerMeasure[var11];
            double var38 = this.vectorEncode[var11];
            double var40 = this.requestAdapt[var11 + 1];
            double var42 = this.timerMeasure[var11 + 1];
            double var44 = this.vectorEncode[var11 + 1];
            double var46 = this.requestReceive[var11];
            double var48 = this.windowProcess[var11];
            double var50 = this.packetSave[var11];
            double var52 = this.requestReceive[var11 + 1];
            double var54 = this.windowProcess[var11 + 1];
            double var56 = this.packetSave[var11 + 1];

            for (int var58 = 0; var58 < 12; var58++) {
               float var59 = outputCollapse[var58];
               float var60 = profileInvoke[var58];
               float var61 = outputCollapse[var58 + 1];
               float var62 = profileInvoke[var58 + 1];
               float var63 = sourceSchedule[var58];
               float var64 = sourceSchedule[var58 + 1];
               double var65 = var59 * var34 + var60 * var46;
               double var67 = var59 * var36 + var60 * var48;
               double var69 = var59 * var38 + var60 * var50;
               double var71 = var61 * var34 + var62 * var46;
               double var73 = var61 * var36 + var62 * var48;
               double var75 = var61 * var38 + var62 * var50;
               double var77 = var59 * var40 + var60 * var52;
               double var79 = var59 * var42 + var60 * var54;
               double var81 = var59 * var44 + var60 * var56;
               double var83 = var61 * var40 + var62 * var52;
               double var85 = var61 * var42 + var62 * var54;
               double var87 = var61 * var44 + var62 * var56;
               this.handle(var1, var2, var22 + var65 * var16, var24 + var67 * var16, var26 + var69 * var16, var12, var63, var20, var18, var65, var67, var69);
               this.handle(var1, var2, var22 + var71 * var16, var24 + var73 * var16, var26 + var75 * var16, var12, var64, var20, var18, var71, var73, var75);
               this.handle(var1, var2, var28 + var83 * var17, var30 + var85 * var17, var32 + var87 * var17, var13, var64, var21, var19, var83, var85, var87);
               this.handle(var1, var2, var28 + var77 * var17, var30 + var79 * var17, var32 + var81 * var17, var13, var63, var21, var19, var77, var79, var81);
            }
         }
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      int var11,
      int var12,
      double var13,
      double var15,
      double var17
   ) {
      var1.vertex(var2, (float)var3, (float)var5, (float)var7)
         .texture(var9, var10)
         .color(handle(var11), process(var11), compute(var11), var12)
         .normal((float)var13, (float)var15, (float)var17);
   }

   private static float process(float var0) {
      float var1 = process(0.0F, 0.42F, var0);
      float var2 = 1.0F - process(0.92F, 1.0F, var0) * 0.5F;
      return var1 * var2;
   }

   private float encodePoint() {
      return (float)(System.nanoTime() - sourceBuild) / 1.0E9F;
   }

   private static double handle(double var0, double var2, double var4, double var6, float var8) {
      double var9 = var8 * var8;
      double var11 = var9 * var8;
      return 0.5
         * (2.0 * var2 + (-var0 + var4) * var8 + (2.0 * var0 - 5.0 * var2 + 4.0 * var4 - var6) * var9 + (-var0 + 3.0 * var2 - 3.0 * var4 + var6) * var11);
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static float process(float var0, float var1, float var2) {
      float var3 = compute((var2 - var0) / (var1 - var0), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   static float compute(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : (var0 > var2 ? var2 : var0);
   }

   private static int handle(int var0, int var1, int var2) {
      return var0 < var1 ? var1 : (var0 > var2 ? var2 : var0);
   }

   private static int compute(float var0) {
      return handle(Math.round(compute(var0, 0.0F, 1.0F) * 255.0F), 0, 255);
   }

   private static int handle(int var0) {
      return var0 >> 16 & 0xFF;
   }

   private static int process(int var0) {
      return var0 >> 8 & 0xFF;
   }

   private static int compute(int var0) {
      return var0 & 0xFF;
   }

   static int handle(int var0, int var1, float var2) {
      float var3 = compute(var2, 0.0F, 1.0F);
      int var4 = Math.round(handle(var0) + (handle(var1) - handle(var0)) * var3);
      int var5 = Math.round(process(var0) + (process(var1) - process(var0)) * var3);
      int var6 = Math.round(compute(var0) + (compute(var1) - compute(var0)) * var3);
      return var4 << 16 | var5 << 8 | var6;
   }

   static {
      for (int var0 = 0; var0 <= 12; var0++) {
         double var1 = (Math.PI * 2) * var0 / 12.0;
         outputCollapse[var0] = (float)Math.cos(var1);
         profileInvoke[var0] = (float)Math.sin(var1);
         sourceSchedule[var0] = var0 / 12.0F;
      }
   }

   static final class ShaderState {
      private static final int[] instance = new int[]{16747247, 16754396, 8648959, 11141102, 16747247};
      private static final int[] data = new int[]{6750183, 6014975, 4688895, 11730932, 6750183};
      private static final int[] context = new int[]{16773227, 16751954, 16736157, 9304063, 16773227};
      private static final int[] config = new int[]{11141048, 6485458, 8228095, 16755188, 11141048};
      private static final int[] state = new int[]{8257383, 3405823, 16773210, 16727538, 8257383};
      private static final int[] cache = new int[]{16754632, 16769167, 11000063, 14067711, 16754632};
      private static final int[] output = new int[]{8033279, 11561983, 5963734, 16736142, 8033279};
      private static final int[] current = new int[]{16757594, 16739146, 16732041, 13995263, 16757594};
      private static final int[] active = new int[]{14089215, 9169663, 9149951, 16777215, 14089215};
      private static final int[] mode = new int[]{16736109, 16770140, 6160312, 7179519, 16736109};
      private static final int[] selection = new int[]{14001919, 16752603, 7733222, 16773260, 14001919};
      private static final int[] enabled = new int[]{13172552, 16773466, 3732223, 16735457, 13172552};
      private ThemePalette renderer = ThemePalette.WILD;
      private int[] handler;
      private int animationDraw = 7316991;
      private float pointEncode;
      float animator = 1.0F;
      float source = 1.0F;

      void handle(float var1, String var2) {
         this.animator = 1.0F;
         this.source = 1.0F;
         if (!this.process(var1, var2)) {
            this.renderer = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
            this.handler = handle(this.renderer);
            this.pointEncode = var1 * 0.06F;
            if (this.handler == null) {
               this.animationDraw = this.renderer == ThemePalette.WILD ? 8108031 : this.renderer.handle().getRGB() & 16777215;
            }
         }
      }

      int handle(float var1) {
         return this.handler != null ? handle(this.handler, this.pointEncode + (1.0F - var1) * 0.42F) : this.animationDraw;
      }

      private boolean process(float var1, String var2) {
         if (var2 != null && !var2.isBlank()) {
            PresetManager var3 = PresetManager.handle();
            if (!var3.update(var2)) {
               return false;
            }

            ShaderGraph var4 = var3.compute(var2);
            ShaderBuildResult var5 = var3.process(var2);
            List<ShaderParameter> var6 = var3.prepare(var2);
            Map var7 = var3.check(var2);
            int[] var8 = new int[4];
            int var9 = 0;

            for (ShaderParameter var11 : var6) {
               if (var11.kind() == ShaderParameter.Mode.COLOR && var9 < var8.length) {
                  float[] var12 = (float[])var7.get(var11.uniformName());
                  var8[var9++] = handle(var12 == null ? var11.defaults() : var12);
               }
            }

            if (var9 == 0) {
               int var15 = ((var4 == null ? var2 : var4.process() + var2) + (var5 == null ? "" : var5.hash())).hashCode();
               var8[var9++] = handle(var15, 0.0F);
               var8[var9++] = handle(var15, 0.31F);
               var8[var9++] = handle(var15, 0.63F);
            }

            if (var9 == 1) {
               int var16 = var8[0];
               this.handler = new int[]{
                  Trails.handle(var16, 16777215, 0.44F),
                  var16,
                  Trails.handle(var16, 7796735, 0.36F),
                  Trails.handle(var16, 16741065, 0.3F),
                  Trails.handle(var16, 16777215, 0.44F)
               };
            } else if (var9 == 2) {
               this.handler = new int[]{var8[0], Trails.handle(var8[0], var8[1], 0.42F), var8[1], Trails.handle(var8[1], 16777215, 0.34F), var8[0]};
            } else if (var9 == 3) {
               this.handler = new int[]{var8[0], var8[1], var8[2], Trails.handle(var8[2], 16777215, 0.32F), var8[0]};
            } else {
               this.handler = new int[]{var8[0], var8[1], var8[2], var8[3], var8[0]};
            }

            float var17 = handle(var6, var7, 0.5F, "width", "radius", "size", "thick");
            float var18 = handle(var6, var7, 0.66F, "opacity", "alpha", "power", "glow", "intensity");
            float var19 = handle(var6, var7, 0.5F, "flow", "speed", "phase", "time");
            this.animator = Trails.compute(var17 == 0.5F ? 1.0F : 0.68F + var17 * 1.22F, 0.58F, 1.9F);
            this.source = Trails.compute(0.62F + var18 * 0.82F, 0.52F, 1.48F);
            this.pointEncode = var1 * (0.034F + var19 * 0.09F);
            return true;
         } else {
            return false;
         }
      }

      private static float handle(List<ShaderParameter> var0, Map<String, float[]> var1, float var2, String... var3) {
         if (var0 != null && !var0.isEmpty()) {
            for (ShaderParameter var5 : var0) {
               if (var5.kind() == ShaderParameter.Mode.FLOAT) {
                  String var6 = (var5.name() + " " + var5.uniformName()).toLowerCase();
                  boolean var7 = false;

                  for (String var11 : var3) {
                     if (var11 != null && var6.contains(var11)) {
                        var7 = true;
                        break;
                     }
                  }

                  if (var7) {
                     float[] var12 = var1 == null ? null : (float[])var1.get(var5.uniformName());
                     float var13 = var12 != null && var12.length != 0 ? var12[0] : var5.defaultFloat();
                     float var14 = var5.maximum() - var5.minimum();
                     if (Float.isFinite(var13) && !(var14 <= 1.0E-6F)) {
                        return Trails.compute((var13 - var5.minimum()) / var14, 0.0F, 1.0F);
                     }

                     return var2;
                  }
               }
            }

            return var2;
         } else {
            return var2;
         }
      }

      private static int handle(float[] var0) {
         float var1 = var0 != null && var0.length > 0 ? var0[0] : 1.0F;
         float var2 = var0 != null && var0.length > 1 ? var0[1] : 1.0F;
         float var3 = var0 != null && var0.length > 2 ? var0[2] : 1.0F;
         return process(var1) << 16 | process(var2) << 8 | process(var3);
      }

      private static int process(float var0) {
         return !Float.isFinite(var0) ? 0 : Math.max(0, Math.min(255, Math.round(var0 * 255.0F)));
      }

      private static int handle(int var0, float var1) {
         int var2 = var0 ^ -1640531527;
         var2 ^= var2 >>> 16;
         var2 *= 2146121005;
         var2 ^= var2 >>> 15;
         var2 *= -2073254261;
         var2 ^= var2 >>> 16;
         float var3 = ((var2 & 16777215) / 1.6777215E7F + var1) % 1.0F;
         return handle(var3, 0.62F, 1.0F);
      }

      private static int handle(float var0, float var1, float var2) {
         var0 -= (float)Math.floor(var0);
         float var3 = var0 * 6.0F;
         int var4 = (int)Math.floor(var3);
         float var5 = var2 * (1.0F - var1);
         float var6 = var2 * (1.0F - var1 * (var3 - var4));
         float var7 = var2 * (1.0F - var1 * (1.0F - (var3 - var4)));
         float var8;
         float var9;
         float var10;
         switch (var4 % 6) {
            case 0:
               var8 = var2;
               var9 = var7;
               var10 = var5;
               break;
            case 1:
               var8 = var6;
               var9 = var2;
               var10 = var5;
               break;
            case 2:
               var8 = var5;
               var9 = var2;
               var10 = var7;
               break;
            case 3:
               var8 = var5;
               var9 = var6;
               var10 = var2;
               break;
            case 4:
               var8 = var7;
               var9 = var5;
               var10 = var2;
               break;
            default:
               var8 = var2;
               var9 = var5;
               var10 = var6;
         }

         return process(var8) << 16 | process(var9) << 8 | process(var10);
      }

      private static int handle(int[] var0, float var1) {
         float var2 = var1 - (float)Math.floor(var1);
         float var3 = var2 * (var0.length - 1);
         int var4 = Math.min(var0.length - 2, Math.max(0, (int)Math.floor(var3)));
         return Trails.handle(var0[var4] & 16777215, var0[var4 + 1] & 16777215, var3 - var4);
      }

      private static int[] handle(ThemePalette var0) {
         return switch (var0) {
            case ASTOLFO_RAINBOW -> instance;
            case LAGUNE_RAINBOW -> data;
            case HALF_RAINBOW -> context;
            case AURORA_RAINBOW -> config;
            case NEON_RAINBOW -> state;
            case BLOSSOM_RAINBOW -> cache;
            case ABYSS_RAINBOW -> output;
            case SUNSET_RAINBOW -> current;
            case GLACIER_RAINBOW -> active;
            case CHROMA_RAINBOW -> mode;
            case DREAM_RAINBOW -> selection;
            case TOXIC_RAINBOW -> enabled;
            default -> null;
         };
      }
   }
}
