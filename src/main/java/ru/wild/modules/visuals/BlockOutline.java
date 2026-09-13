package ru.wild.modules.visuals;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.TargetSelectorRegistry;
import ru.wild.gui.hud.HudElementRenderer;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.TexturedQuadsRenderer;
import ru.wild.render.WorldVertexBuffer;

@ModuleRegister(name = "BlockOutline", category = ModuleCategory.Visuals, description = "Плавная светящаяся обводка блока под прицелом")
public final class BlockOutline extends Module implements HudElementRenderer {
   private static final double positionAdvance = 0.0022;
   private static final int[] frameCheck = new int[]{0, 1, 1, 5, 5, 4, 4, 0, 2, 3, 3, 7, 7, 6, 6, 2, 0, 2, 1, 3, 5, 7, 4, 6};
   private static final int[] moduleCollect = new int[]{0, 1, 5, 4, 2, 6, 7, 3, 0, 4, 6, 2, 1, 3, 7, 5, 0, 2, 3, 1, 4, 5, 7, 6};
   public final NumberSetting source = new NumberSetting("Плавность", 0.55F, 0.0F, 1.0F, 0.01F, true);
   public final NumberSetting target = new NumberSetting("Прозрачность", 1.0F, 0.05F, 1.0F, 0.01F, true);
   public final NumberSetting pending = new NumberSetting("Толщина", 2.0F, 0.5F, 6.0F, 0.1F, false);
   public final NumberSetting previous = new NumberSetting("Расширение", 0.0F, 0.0F, 0.2F, 0.005F, false);
   public final BooleanSetting latest = new BooleanSetting("Свечение", true);
   public final NumberSetting summary = new NumberSetting("Сила свечения", 1.2F, 0.2F, 3.0F, 0.05F, false).handle(() -> !this.latest.compute());
   public final BooleanSetting matrixBlend = new BooleanSetting("Заливка", false);
   public final NumberSetting vectorMatch = new NumberSetting("Прозрачность заливки", 0.22F, 0.02F, 0.8F, 0.01F, true)
      .handle(() -> !this.matrixBlend.compute());
   public final BooleanSetting itemProject = new BooleanSetting("Пульсация", false);
   public final NumberSetting responseCompute = new NumberSetting("Скорость пульсации", 2.0F, 0.2F, 6.0F, 0.1F, false)
      .handle(() -> !this.itemProject.compute());
   public final BooleanSetting providerFetch = new BooleanSetting("Сквозь стены", false);
   public final ModeSetting profileDraw = new ModeSetting("Цвет", "Тема", "Тема", "Свой", "Радуга");
   public final ColorSetting vectorPerform = new ColorSetting("Свой цвет", 50.0F, 0.82F, 1.0F).process(() -> !this.profileDraw.process("Свой"));
   public final NumberSetting eventAttach = new NumberSetting("Скорость радуги", 1.0F, 0.1F, 4.0F, 0.1F, false)
      .handle(() -> !this.profileDraw.process("Радуга"));
   public final ShaderPresetSetting serverRead = new ShaderPresetSetting("Foundry Shader", LivePreviewRenderer.ESP);
   private final double[] providerClose = new double[6];
   private final double[] presetSave = new double[6];
   private final double[] windowConvert = new double[24];
   private boolean presetWrite;
   private float colorMeasure;
   private long animationSchedule;

   public BlockOutline() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach,
         this.serverRead
      );
   }

   @Override
   public void handle() {
      super.handle();
      TargetSelectorRegistry.handle().handle(this, this);
   }

   @Override
   public void process() {
      TargetSelectorRegistry.handle().handle(this);
      this.encodePoint();
      super.process();
   }

   @Override
   public LivePreviewRenderer compute() {
      return LivePreviewRenderer.ESP;
   }

   @Override
   public String resolve() {
      String var1 = this.serverRead == null ? "" : this.serverRead.refresh();
      return var1 != null && !var1.isBlank() ? var1 : null;
   }

   @Override
   public boolean update() {
      return true;
   }
   @EventHandler
   public void handle(WorldRenderEvent var1) {
      TargetSelectorRegistry.handle().process(this, this);
      if (Module.client.world != null && Module.client.player != null && Module.client.gameRenderer != null) {
         boolean var2 = this.refresh();
         float var3 = this.compute(var2);
         if (this.presetWrite && !(var3 <= 0.003F)) {
            Camera var4 = Module.client.gameRenderer.getCamera();
            if (var4 != null) {
               Vec3d var5 = var4.getPos();
               Matrix4f var6 = var1.compute().peek().getPositionMatrix();
               this.handle(var5);
               float var7 = this.itemProject.compute() ? 0.78F + 0.22F * (float)Math.sin(drawAnimation() * this.responseCompute.compute() * Math.PI) : 1.0F;
               float var8 = MathHelper.clamp(var3 * this.target.compute() * var7, 0.0F, 1.0F);
               if (!(var8 <= 0.003F)) {
                  int var9 = this.render();
                  int var10 = var9 >> 16 & 0xFF;
                  int var11 = var9 >> 8 & 0xFF;
                  int var12 = var9 & 0xFF;
                  boolean var13 = !this.providerFetch.compute();
                  float var14 = this.pending.compute();
                  Immediate var15 = WorldVertexBuffer.handle();
                  boolean var20 = false /* VF: Semaphore variable */;

                  try {
                     var20 = true;
                     if (this.matrixBlend.compute()) {
                        int var16 = handle(this.vectorMatch.compute() * var8 * 255.0F);
                        if (var16 > 0) {
                           RenderLayer var17 = var13 ? TexturedQuadsRenderer.resolve() : TexturedQuadsRenderer.update();
                           this.process(var15.getBuffer(var17), var6, var10, var11, var12, var16);
                        }
                     }

                     if (this.latest.compute()) {
                        float var22 = var14 * (2.4F + this.summary.compute());
                        int var24 = handle(0.16F * this.summary.compute() * var8 * 255.0F);
                        if (var24 > 0) {
                           this.handle(var15.getBuffer(handle(var22, var13)), var6, var10, var11, var12, var24);
                        }
                     }

                     int var23 = handle(var8 * 255.0F);
                     this.handle(var15.getBuffer(handle(var14, var13)), var6, var10, var11, var12, var23);
                     var20 = false;
                  } finally {
                     if (var20) {
                        WorldVertexBuffer.process();
                     }
                  }

                  WorldVertexBuffer.process();
               }
            }
         }
      } else {
         this.encodePoint();
      }
   }

   private boolean refresh() {
      HitResult var1 = Module.client.crosshairTarget;
      if (var1 instanceof BlockHitResult var2 && var1.getType() == Type.BLOCK) {
         BlockPos var3 = var2.getBlockPos();
         if (var3 == null) {
            return false;
         }

         BlockState var4 = Module.client.world.getBlockState(var3);
         if (var4 != null && !var4.isAir()) {
            VoxelShape var5 = var4.getOutlineShape(Module.client.world, var3);
            if (var5 != null && !var5.isEmpty()) {
               Box var6 = var5.getBoundingBox();
               double var7 = this.previous.compute() + 0.0022;
               this.presetSave[0] = var3.getX() + var6.minX - var7;
               this.presetSave[1] = var3.getY() + var6.minY - var7;
               this.presetSave[2] = var3.getZ() + var6.minZ - var7;
               this.presetSave[3] = var3.getX() + var6.maxX + var7;
               this.presetSave[4] = var3.getY() + var6.maxY + var7;
               this.presetSave[5] = var3.getZ() + var6.maxZ + var7;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private float compute(boolean var1) {
      long var2 = System.nanoTime();
      float var4 = this.animationSchedule == 0L ? 0.0F : Math.min((float)(var2 - this.animationSchedule) / 1.0E9F, 0.1F);
      this.animationSchedule = var2;
      this.colorMeasure = this.colorMeasure + ((var1 ? 1.0F : 0.0F) - this.colorMeasure) * handle(16.0F, var4);
      if (!var1 && this.colorMeasure < 0.01F) {
         this.colorMeasure = 0.0F;
         this.presetWrite = false;
         return 0.0F;
      }

      if (!var1) {
         return this.colorMeasure;
      }

      if (!this.presetWrite) {
         System.arraycopy(this.presetSave, 0, this.providerClose, 0, 6);
         this.presetWrite = true;
      } else {
         float var5 = MathHelper.lerp(MathHelper.clamp(this.source.compute(), 0.0F, 1.0F), 42.0F, 4.5F);
         float var6 = handle(var5, var4);

         for (int var7 = 0; var7 < 6; var7++) {
            this.providerClose[var7] = this.providerClose[var7] + (this.presetSave[var7] - this.providerClose[var7]) * var6;
         }
      }

      return this.colorMeasure;
   }

   private void handle(Vec3d var1) {
      double var2 = this.providerClose[0] - var1.x;
      double var4 = this.providerClose[1] - var1.y;
      double var6 = this.providerClose[2] - var1.z;
      double var8 = this.providerClose[3] - var1.x;
      double var10 = this.providerClose[4] - var1.y;
      double var12 = this.providerClose[5] - var1.z;

      for (int var14 = 0; var14 < 8; var14++) {
         int var15 = var14 * 3;
         this.windowConvert[var15] = (var14 & 1) == 0 ? var2 : var8;
         this.windowConvert[var15 + 1] = (var14 & 2) == 0 ? var4 : var10;
         this.windowConvert[var15 + 2] = (var14 & 4) == 0 ? var6 : var12;
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, int var3, int var4, int var5, int var6) {
      for (byte var7 = 0; var7 < frameCheck.length; var7 += 2) {
         int var8 = frameCheck[var7] * 3;
         int var9 = frameCheck[var7 + 1] * 3;
         double var10 = this.windowConvert[var8];
         double var12 = this.windowConvert[var8 + 1];
         double var14 = this.windowConvert[var8 + 2];
         double var16 = this.windowConvert[var9];
         double var18 = this.windowConvert[var9 + 1];
         double var20 = this.windowConvert[var9 + 2];
         double var22 = var16 - var10;
         double var24 = var18 - var12;
         double var26 = var20 - var14;
         double var28 = Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
         if (!(var28 < 1.0E-6)) {
            float var30 = (float)(var22 / var28);
            float var31 = (float)(var24 / var28);
            float var32 = (float)(var26 / var28);
            var1.vertex(var2, (float)var10, (float)var12, (float)var14).color(var3, var4, var5, var6).normal(var30, var31, var32);
            var1.vertex(var2, (float)var16, (float)var18, (float)var20).color(var3, var4, var5, var6).normal(var30, var31, var32);
         }
      }
   }

   private void process(VertexConsumer var1, Matrix4f var2, int var3, int var4, int var5, int var6) {
      for (byte var7 = 0; var7 < moduleCollect.length; var7 += 4) {
         for (int var8 = 0; var8 < 4; var8++) {
            int var9 = moduleCollect[var7 + var8] * 3;
            var1.vertex(var2, (float)this.windowConvert[var9], (float)this.windowConvert[var9 + 1], (float)this.windowConvert[var9 + 2])
               .color(var3, var4, var5, var6);
         }
      }
   }

   private static RenderLayer handle(double var0, boolean var2) {
      return var2 ? TexturedQuadsRenderer.handle(var0) : TexturedQuadsRenderer.process(var0);
   }

   private int render() {
      if (this.profileDraw.process("Радуга")) {
         float var1 = drawAnimation() * this.eventAttach.compute() * 0.12F % 1.0F;
         return handle(var1 < 0.0F ? var1 + 1.0F : var1, 0.85F, 1.0F);
      } else {
         return this.profileDraw.process("Свой") ? this.vectorPerform.prepare() & 16777215 : tick();
      }
   }

   private static int tick() {
      try {
         if (WildClient.instance != null && WildClient.instance.selection != null) {
            ThemePalette var0 = WildClient.instance.selection.process();
            if (var0 == ThemePalette.CUSTOM && WildClient.instance.selection.data != null) {
               return WildClient.instance.selection.data.prepare() & 16777215;
            }

            if (var0 != null && var0.handle() != null) {
               return var0.handle().getRGB() & 16777215;
            }
         }
      } catch (Throwable var1) {
      }

      return 6061311;
   }

   private static int handle(float var0, float var1, float var2) {
      float var3 = (float)Math.floor(var0 * 6.0F);
      float var4 = var0 * 6.0F - var3;
      float var5 = var2 * (1.0F - var1);
      float var6 = var2 * (1.0F - var4 * var1);
      float var7 = var2 * (1.0F - (1.0F - var4) * var1);
      float var8;
      float var9;
      float var10;
      switch ((int)var3 % 6) {
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

      return Math.round(var8 * 255.0F) << 16 | Math.round(var9 * 255.0F) << 8 | Math.round(var10 * 255.0F);
   }

   private static int handle(float var0) {
      return MathHelper.clamp(Math.round(var0), 0, 255);
   }

   private static float handle(float var0, float var1) {
      return 1.0F - (float)Math.exp(-var0 * var1);
   }

   private static float drawAnimation() {
      return (float)(System.nanoTime() % 1000000000000L) / 1.0E9F;
   }

   private void encodePoint() {
      this.presetWrite = false;
      this.colorMeasure = 0.0F;
      this.animationSchedule = 0L;
   }
}
