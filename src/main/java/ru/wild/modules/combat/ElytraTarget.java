package ru.wild.modules.combat;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.OptionalDouble;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.player.LocalhostHelper;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.render.ColoredGeometryEmitter;
import ru.wild.util.render.PackedColor;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "ElytraTarget", description = "Преследует таргета на элитре", category = ModuleCategory.Combat, flags = ModuleFlag.RISKY)
public class ElytraTarget extends Module {
   public final BooleanSetting source = new BooleanSetting("Перегонять", true);
   public final ModeSetting target = new ModeSetting("Режим предикта", "ReallyWorld", "ReallyWorld", "ReallyWorld - 2", "Default");
   public final NumberSetting pending = new NumberSetting("Сила предикта", 2.7F, 1.0F, 5.0F, 0.1F, false);
   public static final NumberSetting previous = new NumberSetting("Радиус обнаружения элитры", 20.0F, 5.0F, 60.0F, 1.0F, false).handle(() -> !refresh());
   public final NumberSetting latest = new NumberSetting("Растояние преследования", 30.0F, 10.0F, 100.0F, 5.0F, false);
   public final BooleanSetting summary = new BooleanSetting("Разворот на 180", false);
   public final BooleanSetting matrixBlend = new BooleanSetting("Рисовать предикт", true);
   public final NumberSetting vectorMatch = new NumberSetting("Прозрачность", 40.0F, 0.0F, 255.0F, 1.0F, false).handle(() -> !this.matrixBlend.compute());
   public final BooleanSetting itemProject = new BooleanSetting("От темы", true).handle(() -> !this.matrixBlend.compute());
   public final ModeSetting responseCompute = new ModeSetting("Вид квадрата", "Обычный", "Обычный", "Пунктир", "Диагонали")
      .handle(() -> !this.matrixBlend.compute());
   private static final double providerFetch = 0.35;
   private static final float profileDraw = 2.5F;
   private Vec3d vectorPerform = null;
   private boolean eventAttach = false;
   private LivingEntity serverRead = null;
   private static final int positionAdvance = 2048;
   private static final RenderPipeline frameCheck = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lequal_depth_test"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderPipeline moduleCollect = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("minecraft", "rendertype_lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer providerClose = RenderLayer.of(
      "elytra_target_fill", 2048, false, true, frameCheck, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer presetSave = RenderLayer.of(
      "elytra_target_line", 2048, false, true, moduleCollect, MultiPhaseParameters.builder().lineWidth(new LineWidth(OptionalDouble.of(2.0))).build(false)
   );

   public ElytraTarget() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute
      );
   }

   public static boolean refresh() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         ElytraTarget var0 = WildClient.instance.data.handle(ElytraTarget.class);
         return var0 != null && var0.enabled;
      } else {
         return false;
      }
   }

   @Override
   public void process() {
      this.vectorPerform = null;
      this.eventAttach = false;
      this.serverRead = null;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      this.vectorPerform = null;
      if (!LocalhostHelper.handle() && Module.client.player != null && Module.client.world != null) {
         if (this.source.compute() && Module.client.player.isGliding()) {
            LivingEntity var2 = this.render();
            if (var2 == null) {
               this.eventAttach = false;
            } else {
               float var3 = Module.client.player.distanceTo(var2);
               Vec3d var4 = this.process(var2);
               this.vectorPerform = var4;
               Vec3d var5 = var4.subtract(Module.client.player.getEyePos());
               if (!(var5.lengthSquared() < 1.0E-7)) {
                  float var6 = (float)Math.toDegrees(Math.atan2(-var5.x, var5.z));
                  float var7 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var5.y, Math.hypot(var5.x, var5.z))), -90.0, 90.0);
                  if (this.handle(var3)) {
                     var6 = MathHelper.wrapDegrees(var6 + 180.0F);
                  }

                  RotationController.handle(new RotationAngles(var6, var7), 32.0F, 32.0F, 360.0F, 360.0F, 0, 12, true);
               }
            }
         } else {
            this.eventAttach = false;
            this.serverRead = null;
         }
      }
   }

   private LivingEntity render() {
      float var1 = previous.compute();
      float var2 = Math.max(this.latest.compute(), var1);
      if (this.handle(this.serverRead) && Module.client.player.distanceTo(this.serverRead) <= var2) {
         return this.serverRead;
      }

      this.serverRead = null;
      LivingEntity var3 = AttackAura.textureRun;
      if (this.handle(var3) && Module.client.player.distanceTo(var3) <= var2) {
         this.serverRead = var3;
         return this.serverRead;
      }

      LivingEntity var4 = null;
      double var5 = var1 * var1;

      for (Entity var8 : Module.client.world.getEntities()) {
         if (var8 instanceof LivingEntity var9 && this.handle(var9)) {
            double var10 = Module.client.player.squaredDistanceTo(var9);
            if (var10 <= var5) {
               var5 = var10;
               var4 = var9;
            }
         }
      }

      this.serverRead = var4;
      return var4;
   }

   private boolean handle(LivingEntity var1) {
      return var1 != null && var1.isAlive() && var1 != Module.client.player && !(var1 instanceof ClientPlayerEntity) && var1.isGliding();
   }

   private Vec3d process(LivingEntity var1) {
      Vec3d var2 = var1.getPos().add(0.0, var1.getHeight() * 0.5, 0.0);
      Vec3d var3 = var1.getVelocity();
      double var4 = this.pending.compute();
      if (this.target.process("Default")) {
         return var2;
      } else if (this.target.process("ReallyWorld - 2")) {
         Vec3d var6 = var1.getRotationVector().normalize().multiply(2.0);
         return var2.add(var6).add(var3.multiply(var4));
      } else {
         return var2.add(var3.multiply(var4));
      }
   }

   private boolean handle(float var1) {
      if (!this.summary.compute()) {
         this.eventAttach = false;
         return false;
      }

      float var2 = Math.max(2.5F, this.pending.compute());
      float var3 = var2 + 3.0F;
      if (!this.eventAttach && var1 <= 2.5F) {
         this.eventAttach = true;
      }

      if (this.eventAttach && var1 >= var3) {
         this.eventAttach = false;
      }

      return this.eventAttach;
   }
   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (this.matrixBlend.compute() && this.vectorPerform != null && Module.client.world != null) {
         Vec3d var2 = Module.client.gameRenderer.getCamera().getPos();
         double var3 = this.vectorPerform.x - 0.35 - var2.x;
         double var5 = this.vectorPerform.y - 0.35 - var2.y;
         double var7 = this.vectorPerform.z - 0.35 - var2.z;
         double var9 = this.vectorPerform.x + 0.35 - var2.x;
         double var11 = this.vectorPerform.y + 0.35 - var2.y;
         double var13 = this.vectorPerform.z + 0.35 - var2.z;
         int var15 = this.itemProject.compute() ? PackedColor.handle() : PackedColor.compute(255, 255, 255, 255);
         int[] var16 = new int[]{var15, var15, var15, var15};
         int var17 = (int)this.vectorMatch.compute();
         boolean var18 = this.responseCompute.process("Диагонали");
         double var19 = this.responseCompute.process("Пунктир") ? 0.12 : 0.5;
         double var21 = this.responseCompute.process("Пунктир") ? 0.1 : 0.0;
         Immediate var23 = WorldVertexBuffer.handle();
         boolean var29 = false /* VF: Semaphore variable */;

         try {
            var29 = true;
            MatrixStack var24 = var1.compute();
            Matrix4f var25 = var24.peek().getPositionMatrix();
            if (var17 > 0) {
               VertexConsumer var26 = var23.getBuffer(providerClose);
               ColoredGeometryEmitter.handle(var26, var25, var3, var5, var7, var9, var11, var13, var16, var17);
            }

            VertexConsumer var31 = var23.getBuffer(presetSave);
            ColoredGeometryEmitter.handle(var31, var25, var3, var5, var7, var9, var11, var13, var16, 255, var19, var21);
            if (var18) {
               this.handle(var31, var25, var15, var3, var5, var7, var9, var11, var13);
               var29 = false;
            } else {
               var29 = false;
            }
         } finally {
            if (var29) {
               WorldVertexBuffer.process();
            }
         }

         WorldVertexBuffer.process();
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, int var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      int var16 = var3 >> 16 & 0xFF;
      int var17 = var3 >> 8 & 0xFF;
      int var18 = var3 & 0xFF;
      short var19 = 255;
      double[][] var20 = new double[][]{
         {var4, var6, var8, var10, var12, var14},
         {var10, var6, var8, var4, var12, var14},
         {var4, var6, var14, var10, var12, var8},
         {var10, var6, var14, var4, var12, var8}
      };

      for (double[] var24 : var20) {
         var1.vertex(var2, (float)var24[0], (float)var24[1], (float)var24[2]).color(var16, var17, var18, var19);
         var1.vertex(var2, (float)var24[3], (float)var24[4], (float)var24[5]).color(var16, var17, var18, var19);
      }
   }
}
