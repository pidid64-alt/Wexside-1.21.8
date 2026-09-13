package ru.wild.modules.visuals;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.modules.player.ClickPearl;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "Predictions", description = "Показ предикта траэктории полета", category = ModuleCategory.Visuals)
public class Predictions extends Module {
   private static final int source = 240;
   private static final int target = 96;
   private static final int pending = 2097152;
   private static final int previous = 72;
   private static final int latest = 10;
   private static final int summary = 6;
   private static final int matrixBlend = 8;
   private static final int vectorMatch = 6;
   private static final long itemProject = System.nanoTime();
   private static final float responseCompute = 1.5F;
   private static final float providerFetch = 0.5F;
   private static final float profileDraw = 0.7F;
   private static final float vectorPerform = -20.0F;
   private static final double eventAttach = 0.99;
   private static final double serverRead = 0.8;
   private static final float positionAdvance = 0.1F;
   private static final float frameCheck = 0.1F;
   private static final float moduleCollect = 0.1F;
   private static final double providerClose = 64.0;
   private static final double presetSave = 16.0;
   private static final double[] windowConvert = new double[73];
   private static final double[] presetWrite = new double[73];
   private static final float[] colorMeasure = new float[73];
   private static final double[] animationSchedule = new double[7];
   private static final double[] rendererScan = new double[7];
   private static final float[] sourceBuild = new float[7];
   private static final float[] outputCollapse = new float[11];
   private static final Identifier profileInvoke;
   private static final BlendFunction sourceSchedule;
   private static final RenderPipeline timerRender;
   private static final RenderPipeline scaleSave;
   private static final RenderPipeline colorCompute;
   private static final RenderPipeline scaleAdapt;
   private static final RenderLayer textureRun;
   private static final RenderLayer indexBind;
   private static final RenderLayer actionRead;
   private static final RenderLayer configCollapse;
   private final BooleanSetting dataValidate = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting scaleRender = new BooleanSetting("AimPreview", true);
   private final BooleanSetting clientRefresh = new BooleanSetting("ShowOwner", false);
   private final ThemePresets keyFilter = new ThemePresets() {};
   private final List<Predictions.NamedEntry> requestAdapt = new ArrayList<>();
   private final Map<String, Predictions.NamedEntry> timerMeasure = new HashMap<>();
   private ItemStack vectorEncode;
   private final List<Predictions.NamedEntry> requestReceive = new ArrayList<>();
   private long windowProcess = Long.MIN_VALUE;
   private final double[] packetSave = new double[11];
   private final double[] entryAnimate = new double[11];

   public Predictions() {
      this.handle(this.dataValidate, this.scaleRender, this.clientRefresh);
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world == null || Module.client.player == null) {
         this.refresh();
      } else if (Module.client.options != null && Module.client.options.getPerspective() != null && Module.client.options.getPerspective().isFirstPerson()) {
         this.handle(var1.resolve());
         if (!this.requestAdapt.isEmpty()) {
            MatrixStack var2 = var1.compute();
            Matrix4f var3 = var2.peek().getPositionMatrix();
            Vec3d var4 = Module.client.gameRenderer.getCamera().getPos();
            RenderLayer var5 = this.dataValidate.compute() ? indexBind : textureRun;
            RenderLayer var6 = this.dataValidate.compute() ? configCollapse : actionRead;
            int var7 = PackedColor.update(PackedColor.handle(), 235);
            Immediate var8 = WorldVertexBuffer.handle();

            try {
               VertexConsumer var9 = var8.getBuffer(var5);

               for (Predictions.NamedEntry var11 : this.requestAdapt) {
                  boolean var12 = var11.hitEntity() != null;
                  boolean var13 = var11.blockHit() != null && var11.blockHit().getType() != Type.MISS;
                  boolean var14 = var12 || var13;
                  int var15 = var12 ? -51112 : var7;
                  this.handle(var9, var3, var4, var11.path(), var15, var14);
               }
            } finally {
               WorldVertexBuffer.process();
            }

            var8 = WorldVertexBuffer.handle();

            try {
               VertexConsumer var28 = var8.getBuffer(var6);

               for (Predictions.NamedEntry var30 : this.requestAdapt) {
                  boolean var31 = var30.hitEntity() != null;
                  boolean var32 = var30.blockHit() != null && var30.blockHit().getType() != Type.MISS;
                  boolean var33 = var31 || var32;
                  int var34 = var31 ? -51112 : var7;
                  int var16 = var34 >> 16 & 0xFF;
                  int var17 = var34 >> 8 & 0xFF;
                  int var18 = var34 & 0xFF;
                  this.process(var28, var3, var4, var30.path(), var34, var33);
                  if (var31) {
                     Box var19 = var30.targetBox() != null ? var30.targetBox() : var30.hitEntity().getBoundingBox();
                     this.handle(
                        var28,
                        var3,
                        var19.minX - var4.x,
                        var19.minY - var4.y,
                        var19.minZ - var4.z,
                        var19.maxX - var4.x,
                        var19.maxY - var4.y,
                        var19.maxZ - var4.z,
                        var16,
                        var17,
                        var18,
                        230
                     );
                     this.handle(var28, var3, var4, var19, var16, var17, var18);
                  } else if (var32) {
                     Vec3d var35 = var30.blockRenderPos();
                     if (var35 != null) {
                        this.handle(
                           var28,
                           var3,
                           var35.x - var4.x,
                           var35.y - var4.y,
                           var35.z - var4.z,
                           var35.x + 1.0 - var4.x,
                           var35.y + 1.0 - var4.y,
                           var35.z + 1.0 - var4.z,
                           var16,
                           var17,
                           var18
                        );
                     }

                     this.handle(var28, var3, var4, var30.blockHit(), var30.landingPos(), var34);
                  }
               }
            } finally {
               WorldVertexBuffer.process();
            }
         }
      } else {
         this.refresh();
      }
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.world != null && Module.client.player != null) {
         if (Module.client.options != null && Module.client.options.getPerspective() != null && Module.client.options.getPerspective().isFirstPerson()) {
            if (!this.requestAdapt.isEmpty()) {
               RoundedRectRenderer var2 = var1.resolve();
               var2.handle(23.0F);

               for (Predictions.NamedEntry var4 : this.requestAdapt) {
                  if (!var4.isPreAim()) {
                     Vec3d var5 = ClientMathUtil.handle(var4.landingPos());
                     if (var5 != null && !(var5.z < 0.0) && !(var5.z > 1.0)) {
                        float var6 = (float)var5.x;
                        float var7 = (float)var5.y;
                        float var8 = 1.0F;
                        this.handle(var2, var6, var7, var8, var4);
                        if (this.clientRefresh.compute() && this.process(var4)) {
                           this.process(var2, var6, var7 - 31.0F * var8, var8, var4);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void handle(float var1) {
      this.requestAdapt.clear();
      if (Module.client.world != null && Module.client.player != null) {
         ArrayList var2 = new ArrayList();
         ItemStack var3 = Module.client.player.getMainHandStack();
         boolean var4 = false;
         if (var3.isEmpty() || !this.handle(var3.getItem())) {
            var3 = Module.client.player.getOffHandStack();
            var4 = true;
         }

         if (this.scaleRender.compute() && !var3.isEmpty() && this.handle(var3.getItem())) {
            Predictions.NamedEntry var5 = this.handle(Module.client.player, var3, var4, var1);
            if (var5 != null) {
               var2.add(var5);
            }
         }

         if (ClickPearl.target && (var3.isEmpty() || !(var3.getItem() instanceof EnderPearlItem))) {
            if (this.vectorEncode == null) {
               this.vectorEncode = new ItemStack(Items.ENDER_PEARL);
            }

            Predictions.NamedEntry var10 = this.handle(Module.client.player, this.vectorEncode, false, var1);
            if (var10 != null) {
               var2.add(var10);
            }
         }

         long var11 = Module.client.world.getTime();
         if (var11 != this.windowProcess) {
            this.windowProcess = var11;
            this.requestReceive.clear();

            for (ProjectileEntity var8 : Module.client.world
               .getEntitiesByClass(ProjectileEntity.class, Module.client.player.getBoundingBox().expand(256.0), var0 -> !(var0 instanceof FireworkRocketEntity))) {
               Predictions.NamedEntry var9 = this.handle(var8, var1);
               if (var9 != null) {
                  this.requestReceive.add(var9);
               }
            }
         }

         var2.addAll(this.requestReceive);
         this.process(var2);
      } else {
         this.timerMeasure.clear();
         this.requestReceive.clear();
         this.windowProcess = Long.MIN_VALUE;
      }
   }

   private boolean handle(Item var1) {
      return var1 instanceof EnderPearlItem
         || var1 instanceof SnowballItem
         || var1 instanceof EggItem
         || var1 instanceof BowItem
         || var1 instanceof CrossbowItem
         || var1 instanceof TridentItem
         || var1 instanceof ThrowablePotionItem
         || var1 instanceof ExperienceBottleItem;
   }

   private Predictions.NamedEntry handle(PlayerEntity var1, ItemStack var2, boolean var3, float var4) {
      Item var5 = var2.getItem();
      Predictions.DataRecord var6 = this.handle(var1, var5);
      Identifier var7 = this.process(var5);
      Camera var8 = Module.client.gameRenderer.getCamera();
      float var9 = var8.getYaw();
      float var10 = var8.getPitch();
      Vec3d var11 = this.handle(var1, var9, var10, var6.speed(), var6.pitchOffset());
      Vec3d var12 = var8.getPos().subtract(0.0, 0.1, 0.0);
      Vec3d var13 = Vec3d.fromPolar(var10, var9);
      Vec3d var14 = Vec3d.fromPolar(0.0F, var9 + 90.0F);
      float var15 = var3 ? -0.3F : 0.3F;
      Vec3d var16 = var12.add(var13.multiply(0.4)).add(var14.multiply(var15)).subtract(0.0, 0.2, 0.0);
      Vec3d var17 = var16.subtract(var12);
      String var18 = (var3 ? "self:off:" : "self:main:") + Registries.ITEM.getId(var5);
      return this.handle(var1, var12, var17, var11, var6.gravity(), 0.99, var6.applyPhysicsBeforeMove(), var4, var18, "You", var7, true);
   }

   private Predictions.DataRecord handle(PlayerEntity var1, Item var2) {
      double var3 = 1.5;
      double var5 = 0.03;
      float var7 = 0.0F;
      boolean var8 = var2 instanceof EnderPearlItem || var2 instanceof SnowballItem || var2 instanceof EggItem;
      if (var2 instanceof BowItem) {
         int var9 = var1.getItemUseTime();
         float var10 = var9 == 0 ? 1.0F : BowItem.getPullProgress(var9);
         var3 = var10 * 3.0;
         var5 = 0.05;
         var8 = false;
      } else if (var2 instanceof CrossbowItem) {
         var3 = 3.15;
         var5 = 0.05;
         var8 = false;
      } else if (var2 instanceof TridentItem) {
         var3 = 2.5;
         var5 = 0.05;
         var8 = false;
      } else if (var2 instanceof ExperienceBottleItem) {
         var3 = 0.7F;
         var5 = 0.07;
         var7 = -20.0F;
         var8 = true;
      } else if (var2 instanceof ThrowablePotionItem) {
         var3 = 0.5;
         var5 = 0.05;
         var7 = -20.0F;
         var8 = true;
      }

      return new Predictions.DataRecord(var3, var5, var7, var8);
   }

   private Vec3d handle(PlayerEntity var1, float var2, float var3, double var4, float var6) {
      float var7 = var2 * (float) (Math.PI / 180.0);
      float var8 = var3 * (float) (Math.PI / 180.0);
      float var9 = (var3 + var6) * (float) (Math.PI / 180.0);
      double var10 = -MathHelper.sin(var7) * MathHelper.cos(var8);
      double var12 = -MathHelper.sin(var9);
      double var14 = MathHelper.cos(var7) * MathHelper.cos(var8);
      Vec3d var16 = new Vec3d(var10, var12, var14).normalize().multiply(var4);
      Vec3d var17 = var1.getMovement();
      return var16.add(var17.x, var1.isOnGround() ? 0.0 : var17.y, var17.z);
   }

   private Predictions.NamedEntry handle(ProjectileEntity var1, float var2) {
      if (!var1.isRemoved() && !(var1.getVelocity().lengthSquared() < 0.001)) {
         Vec3d var3 = var1.getLerpedPos(var2);
         double var4 = var1.getFinalGravity();
         Identifier var6 = this.handle(var1);
         boolean var7 = var1 instanceof ThrownEntity;
         double var8 = var7 && var1.isTouchingWater() ? 0.8 : 0.99;
         return this.handle(
            var1,
            var3,
            Vec3d.ZERO,
            var1.getVelocity(),
            var4,
            var8,
            var7,
            var2,
            "entity:" + var1.getId(),
            Predictions.NamedEntry.resolveOwnerName(var1),
            var6,
            false
         );
      } else {
         return null;
      }
   }

   private Predictions.NamedEntry handle(
      Entity var1,
      Vec3d var2,
      Vec3d var3,
      Vec3d var4,
      double var5,
      double var7,
      boolean var9,
      float var10,
      String var11,
      String var12,
      Identifier var13,
      boolean var14
   ) {
      if (Module.client.world == null) {
         return null;
      }

      Vec3d var15 = var4;
      Vec3d var16 = var2;
      ArrayList<Vec3d> var17 = new ArrayList<>();
      var17.add(var2.add(var3));
      int var18 = 0;
      BlockHitResult var19 = null;
      Entity var20 = null;
      byte var21 = 7;

      for (int var22 = 0; var22 < 240; var22++) {
         Vec3d var23 = var9 ? this.handle(var15, var5, var7) : var15;
         Vec3d var24 = var16.add(var23);
         var19 = Module.client.world.raycast(new RaycastContext(var16, var24, ShapeType.COLLIDER, FluidHandling.NONE, var1));
         Vec3d var25 = var19.getType() != Type.MISS ? var19.getPos() : var24;
         Box var26 = new Box(var16, var25).expand(1.0);
         double var27 = Double.MAX_VALUE;
         Vec3d var29 = null;
         Entity var30 = null;

         for (Entity var32 : Module.client.world.getOtherEntities(var1, var26, var0 -> !var0.isSpectator() && var0.isAlive())) {
            Box var33 = var32.getBoundingBox().expand(0.3);
            Optional var34 = var33.raycast(var16, var25);
            if (var34.isPresent()) {
               double var35 = var16.squaredDistanceTo((Vec3d)var34.get());
               if (var35 < var27) {
                  var27 = var35;
                  var29 = (Vec3d)var34.get();
                  var30 = var32;
               }
            }
         }

         double var38 = Math.max(0.0, 1.0 - (double)(var22 + 1) / var21);
         if (var30 != null) {
            var17.add(var29.add(var3.multiply(var38)));
            var20 = var30;
            var16 = var29;
            var18 = var22 + 1;
            break;
         }

         if (var19.getType() != Type.MISS) {
            var17.add(var19.getPos().add(var3.multiply(var38)));
            var16 = var19.getPos();
            var18 = var22 + 1;
            break;
         }

         var17.add(var24.add(var3.multiply(var38)));
         var16 = var24;
         var15 = var9 ? var23 : this.process(var15, var5, var7);
         var18 = var22 + 1;
      }

      if (var17.size() < 2) {
         return null;
      }

      Box var37 = var20 != null ? this.handle(var20, var10) : null;
      return new Predictions.NamedEntry(var11, this.handle(var17), var16, this.handle(var19), var37, var18, var20, var19, var12, var13, var14);
   }

   private List<Vec3d> handle(List<Vec3d> var1) {
      int var2 = var1.size();
      if (var2 <= 96) {
         return var1;
      }

      int var3 = Math.max(2, (int)Math.ceil(var2 / 96.0));
      ArrayList var4 = new ArrayList(var2 / var3 + 2);

      for (int var5 = 0; var5 < var2; var5 += var3) {
         var4.add((Vec3d)var1.get(var5));
      }

      Vec3d var6 = (Vec3d)var1.get(var2 - 1);
      if (var4.isEmpty() || var4.get(var4.size() - 1) != var6) {
         var4.add(var6);
      }

      return var4;
   }

   private void process(List<Predictions.NamedEntry> var1) {
      if (var1.isEmpty()) {
         this.timerMeasure.clear();
      } else {
         HashMap var2 = new HashMap();

         for (Predictions.NamedEntry var4 : var1) {
            Predictions.NamedEntry var5 = this.handle(var4);
            this.requestAdapt.add(var5);
            var2.put(var5.key(), var5);
         }

         this.timerMeasure.clear();
         this.timerMeasure.putAll(var2);
      }
   }

   private Predictions.NamedEntry handle(Predictions.NamedEntry var1) {
      Predictions.NamedEntry var2 = this.timerMeasure.get(var1.key());
      if (var2 != null && var2.path().size() >= 2 && var1.path().size() >= 2) {
         if (var2.path().get(0).squaredDistanceTo(var1.path().get(0)) > 256.0) {
            return var1;
         }

         List var3 = this.handle(var2.path(), var1.path(), 0.1F);
         float var4 = var1.isPreAim() ? 0.1F : 0.1F;
         Vec3d var5 = this.handle(var2.landingPos(), var1.landingPos(), var4, 64.0);
         Vec3d var6 = this.process(var2.blockRenderPos(), var1.blockRenderPos(), var4, 64.0);
         Box var7 = this.handle(var2, var1, var1.isPreAim() ? 0.1F : 0.1F);
         return var1.withRenderState(var3, var5, var6, var7);
      } else {
         return var1;
      }
   }

   private List<Vec3d> handle(List<Vec3d> var1, List<Vec3d> var2, float var3) {
      ArrayList var4 = new ArrayList(var2.size());

      for (int var5 = 0; var5 < var2.size(); var5++) {
         Vec3d var6 = var5 < var1.size() ? this.handle((Vec3d)var1.get(var5), (Vec3d)var2.get(var5), var3) : (Vec3d)var2.get(var5);
         var4.add(var6);
      }

      return var4;
   }

   private Vec3d handle(Vec3d var1, Vec3d var2, float var3) {
      return new Vec3d(MathHelper.lerp(var3, var1.x, var2.x), MathHelper.lerp(var3, var1.y, var2.y), MathHelper.lerp(var3, var1.z, var2.z));
   }

   private Vec3d handle(Vec3d var1, Vec3d var2, float var3, double var4) {
      return var1.squaredDistanceTo(var2) > var4 ? var2 : this.handle(var1, var2, var3);
   }

   private Vec3d process(Vec3d var1, Vec3d var2, float var3, double var4) {
      if (var1 == null) {
         return var2;
      } else if (var2 == null) {
         return null;
      } else {
         return var1.squaredDistanceTo(var2) > var4 ? var2 : this.handle(var1, var2, var3);
      }
   }

   private Box handle(Predictions.NamedEntry var1, Predictions.NamedEntry var2, float var3) {
      if (var2.targetBox() == null) {
         return null;
      }

      if (var1.targetBox() != null && var1.hitEntity() != null && var2.hitEntity() != null) {
         if (var1.hitEntity().getId() != var2.hitEntity().getId()) {
            return var2.targetBox();
         } else {
            return this.handle(var1.targetBox(), var2.targetBox()) > 16.0 ? var2.targetBox() : this.handle(var1.targetBox(), var2.targetBox(), var3);
         }
      } else {
         return var2.targetBox();
      }
   }

   private Box handle(Box var1, Box var2, float var3) {
      return new Box(
         MathHelper.lerp(var3, var1.minX, var2.minX),
         MathHelper.lerp(var3, var1.minY, var2.minY),
         MathHelper.lerp(var3, var1.minZ, var2.minZ),
         MathHelper.lerp(var3, var1.maxX, var2.maxX),
         MathHelper.lerp(var3, var1.maxY, var2.maxY),
         MathHelper.lerp(var3, var1.maxZ, var2.maxZ)
      );
   }

   private double handle(Box var1, Box var2) {
      double var3 = (var1.minX + var1.maxX - var2.minX - var2.maxX) * 0.5;
      double var5 = (var1.minY + var1.maxY - var2.minY - var2.maxY) * 0.5;
      double var7 = (var1.minZ + var1.maxZ - var2.minZ - var2.maxZ) * 0.5;
      return var3 * var3 + var5 * var5 + var7 * var7;
   }

   private Box handle(Entity var1, float var2) {
      Vec3d var3 = var1.getLerpedPos(var2);
      Vec3d var4 = var1.getPos();
      return var1.getBoundingBox().offset(var3.x - var4.x, var3.y - var4.y, var3.z - var4.z);
   }

   private Vec3d handle(BlockHitResult var1) {
      if (var1 != null && var1.getType() != Type.MISS) {
         BlockPos var2 = var1.getBlockPos();
         return new Vec3d(var2.getX(), var2.getY(), var2.getZ());
      } else {
         return null;
      }
   }

   private void refresh() {
      this.requestAdapt.clear();
      this.timerMeasure.clear();
      this.requestReceive.clear();
      this.windowProcess = Long.MIN_VALUE;
   }

   private Vec3d handle(Vec3d var1, double var2, double var4) {
      return var1.subtract(0.0, var2, 0.0).multiply(var4);
   }

   private Vec3d process(Vec3d var1, double var2, double var4) {
      return var1.multiply(var4).subtract(0.0, var2, 0.0);
   }

   private void handle(VertexConsumer var1, Matrix4f var2, Vec3d var3, List<Vec3d> var4, int var5, boolean var6) {
      if (var4.size() >= 2) {
         float var7 = this.render() * 0.3125F;
         this.handle(var1, var2, var3, var4, var5, var6, 0.072F, 0.024F, 0.56F, var7 + 0.23F, 0.64F, 1.0F);
         this.handle(var1, var2, var3, var4, var5, var6, 0.042F, 0.014F, 0.94F, var7 + 0.37F, 1.0F, 1.0F);
      }
   }

   private void process(VertexConsumer var1, Matrix4f var2, Vec3d var3, List<Vec3d> var4, int var5, boolean var6) {
      if (var4.size() >= 2) {
         float var7 = this.render() * 0.3125F;
         this.handle(var1, var2, var3, var4, var5, var6, 0.235F, 0.066F, 0.16F, var7, 0.25F, 0.0F);
         this.handle(var1, var2, var3, var4, var5, var6, 0.126F, 0.036F, 0.34F, var7 + 0.19F, 0.58F, 0.0F);
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      Vec3d var3,
      List<Vec3d> var4,
      int var5,
      boolean var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12
   ) {
      int var13 = var4.size();
      int var14 = var13 - 1;
      int var15 = var5 >> 16 & 0xFF;
      int var16 = var5 >> 8 & 0xFF;
      int var17 = var5 & 0xFF;
      int var18 = Math.max(120, var5 >>> 24 & 0xFF);
      double var19 = var3.x;
      double var21 = var3.y;
      double var23 = var3.z;

      for (int var25 = 0; var25 <= 10; var25++) {
         double var26 = (outputCollapse[var25] + var10 * 0.07F) * Math.PI * 2.0;
         this.packetSave[var25] = Math.cos(var26);
         this.entryAnimate[var25] = Math.sin(var26);
      }

      for (int var101 = 0; var101 < var14; var101++) {
         Vec3d var102 = (Vec3d)var4.get(var101);
         Vec3d var27 = (Vec3d)var4.get(var101 + 1);
         double var28 = var102.x - var19;
         double var30 = var102.y - var21;
         double var32 = var102.z - var23;
         double var34 = var27.x - var19;
         double var36 = var27.y - var21;
         double var38 = var27.z - var23;
         double var40 = var34 - var28;
         double var42 = var36 - var30;
         double var44 = var38 - var32;
         double var46 = Math.sqrt(var40 * var40 + var42 * var42 + var44 * var44);
         if (!(var46 <= 1.0E-5)) {
            double var48 = var40 / var46;
            double var50 = var42 / var46;
            double var52 = var44 / var46;
            double var54 = Math.abs(var50) < 0.92 ? 0.0 : 1.0;
            double var56 = Math.abs(var50) < 0.92 ? 1.0 : 0.0;
            double var58 = 0.0;
            double var60 = var56 * var52 - var58 * var50;
            double var62 = var58 * var48 - var54 * var52;
            double var64 = var54 * var50 - var56 * var48;
            double var66 = Math.sqrt(var60 * var60 + var62 * var62 + var64 * var64);
            if (var66 <= 1.0E-5) {
               var60 = 1.0;
               var62 = 0.0;
               var64 = 0.0;
            } else {
               var60 /= var66;
               var62 /= var66;
               var64 /= var66;
            }

            double var68 = var50 * var64 - var52 * var62;
            double var70 = var52 * var60 - var48 * var64;
            double var72 = var48 * var62 - var50 * var60;
            float var74 = (float)var101 / var14;
            float var75 = (float)(var101 + 1) / var14;
            float var76 = this.handle(var74, var7, var8, var6);
            float var77 = this.handle(var75, var7, var8, var6);
            var76 *= 1.0F + 0.085F * (float)Math.sin((var74 * 2.7F - var10 * 3.8F + var11 * 0.31F) * Math.PI * 2.0);
            var77 *= 1.0F + 0.085F * (float)Math.sin((var75 * 2.7F - var10 * 3.8F + var11 * 0.31F) * Math.PI * 2.0);

            for (int var78 = 0; var78 < 10; var78++) {
               float var79 = outputCollapse[var78];
               float var80 = outputCollapse[var78 + 1];
               double var81 = this.packetSave[var78];
               double var83 = this.entryAnimate[var78];
               double var85 = this.packetSave[var78 + 1];
               double var87 = this.entryAnimate[var78 + 1];
               double var89 = var60 * var81 + var68 * var83;
               double var91 = var62 * var81 + var70 * var83;
               double var93 = var64 * var81 + var72 * var83;
               double var95 = var60 * var85 + var68 * var87;
               double var97 = var62 * var85 + var70 * var87;
               double var99 = var64 * var85 + var72 * var87;
               this.handle(
                  var1,
                  var2,
                  var28 + var89 * var76,
                  var30 + var91 * var76,
                  var32 + var93 * var76,
                  var15,
                  var16,
                  var17,
                  var18,
                  var74,
                  var12 + var79,
                  var10,
                  var9,
                  var11,
                  var89,
                  var91,
                  var93
               );
               this.handle(
                  var1,
                  var2,
                  var28 + var95 * var76,
                  var30 + var97 * var76,
                  var32 + var99 * var76,
                  var15,
                  var16,
                  var17,
                  var18,
                  var74,
                  var12 + var80,
                  var10,
                  var9,
                  var11,
                  var95,
                  var97,
                  var99
               );
               this.handle(
                  var1,
                  var2,
                  var34 + var95 * var77,
                  var36 + var97 * var77,
                  var38 + var99 * var77,
                  var15,
                  var16,
                  var17,
                  var18,
                  var75,
                  var12 + var80,
                  var10,
                  var9,
                  var11,
                  var95,
                  var97,
                  var99
               );
               this.handle(
                  var1,
                  var2,
                  var34 + var89 * var77,
                  var36 + var91 * var77,
                  var38 + var93 * var77,
                  var15,
                  var16,
                  var17,
                  var18,
                  var75,
                  var12 + var79,
                  var10,
                  var9,
                  var11,
                  var89,
                  var91,
                  var93
               );
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
      int var9,
      int var10,
      int var11,
      int var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      double var18,
      double var20,
      double var22
   ) {
      float var24 = this.process(var13);
      int var25 = this.handle(var9, 255, var17 * 0.18F);
      int var26 = this.handle(var10, 255, var17 * 0.14F);
      int var27 = this.handle(var11, 255, var17 * 0.12F);
      int var28 = MathHelper.clamp(Math.round(var12 * var16 * var24), 0, 255);
      var1.vertex(var2, (float)var3, (float)var5, (float)var7)
         .texture(var13 + var15 * 0.28F, var14)
         .color(var25, var26, var27, var28)
         .normal((float)var18, (float)var20, (float)var22);
   }

   private void handle(VertexConsumer var1, Matrix4f var2, Vec3d var3, BlockHitResult var4, Vec3d var5, int var6) {
      if (var4 != null && var4.getType() != Type.MISS) {
         Direction var7 = var4.getSide();
         Vec3d var8 = var5 != null ? var5 : var4.getPos();
         double var9 = var7.getOffsetX();
         double var11 = var7.getOffsetY();
         double var13 = var7.getOffsetZ();
         double var15 = var8.x - var3.x + var9 * 0.01;
         double var17 = var8.y - var3.y + var11 * 0.01;
         double var19 = var8.z - var3.z + var13 * 0.01;
         double var21;
         double var23;
         double var25;
         double var27;
         double var29;
         double var31;
         if (var7.getAxis() == Axis.Y) {
            var21 = 1.0;
            var23 = 0.0;
            var25 = 0.0;
            var27 = 0.0;
            var29 = 0.0;
            var31 = var7 == Direction.DOWN ? -1.0 : 1.0;
         } else if (var7.getAxis() == Axis.X) {
            var21 = 0.0;
            var23 = 0.0;
            var25 = var7 == Direction.WEST ? -1.0 : 1.0;
            var27 = 0.0;
            var29 = 1.0;
            var31 = 0.0;
         } else {
            var21 = var7 == Direction.NORTH ? -1.0 : 1.0;
            var23 = 0.0;
            var25 = 0.0;
            var27 = 0.0;
            var29 = 1.0;
            var31 = 0.0;
         }

         float var33 = this.render() * 0.454545F;
         int var34 = var6 >> 16 & 0xFF;
         int var35 = var6 >> 8 & 0xFF;
         int var36 = var6 & 0xFF;
         float var37 = 0.92F + 0.08F * (float)Math.sin(var33 * Math.PI * 2.0);
         this.handle(
            var1, var2, var15, var17, var19, var9, var11, var13, var21, var23, var25, var27, var29, var31, 1.2F * var37, var34, var35, var36, 56, var33, 4.0F
         );
         this.handle(
            var1,
            var2,
            var15,
            var17,
            var19,
            var9,
            var11,
            var13,
            var21,
            var23,
            var25,
            var27,
            var29,
            var31,
            0.74F * var37,
            this.handle(var34, 255, 0.18F),
            this.handle(var35, 255, 0.14F),
            this.handle(var36, 255, 0.16F),
            100,
            var33 + 0.27F,
            4.0F
         );
         this.handle(
            var1,
            var2,
            var15,
            var17,
            var19,
            var9,
            var11,
            var13,
            var21,
            var23,
            var25,
            var27,
            var29,
            var31,
            0.54F * var37,
            0.3F * var37,
            this.handle(var34, 255, 0.32F),
            this.handle(var35, 255, 0.26F),
            this.handle(var36, 255, 0.28F),
            130,
            var33,
            4.0F
         );
         this.handle(
            var1,
            var2,
            var15,
            var17,
            var19,
            var21,
            var23,
            var25,
            var27,
            var29,
            var31,
            0.62F * var37,
            0.09F,
            var34,
            var35,
            var36,
            110,
            var33 + 0.21F,
            0.78F,
            2.0F
         );
         this.handle(
            var1,
            var2,
            var15,
            var17,
            var19,
            var21,
            var23,
            var25,
            var27,
            var29,
            var31,
            0.33F * var37,
            0.038F,
            this.handle(var34, 255, 0.36F),
            this.handle(var35, 255, 0.3F),
            this.handle(var36, 255, 0.32F),
            200,
            var33 + 0.46F,
            0.95F,
            2.0F
         );
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, Vec3d var3, Box var4, int var5, int var6, int var7) {
      double var8 = (var4.minX + var4.maxX) * 0.5 - var3.x;
      double var10 = var4.minY - var3.y + 0.035;
      double var12 = (var4.minZ + var4.maxZ) * 0.5 - var3.z;
      double var14 = var4.maxY - var4.minY;
      double var16 = Math.max(var4.maxX - var4.minX, var4.maxZ - var4.minZ) * 0.66 + 0.22;
      long var18 = System.currentTimeMillis();
      float var20 = (float)(var18 % 1800L) / 1800.0F;
      this.handle(var1, var2, var8, var10, var12, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, (float)var16, 0.058F, var5, var6, var7, 215, var20, 1.0F, 2.0F);
      this.handle(
         var1,
         var2,
         var8,
         var10 + var14 * 0.56,
         var12,
         1.0,
         0.0,
         0.0,
         0.0,
         0.0,
         1.0,
         (float)(var16 * 0.86),
         0.04F,
         var5,
         var6,
         var7,
         120,
         var20 + 0.33F,
         0.62F,
         2.0F
      );
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25,
      float var27,
      int var28,
      int var29,
      int var30,
      int var31,
      float var32,
      float var33
   ) {
      for (int var34 = 0; var34 < 8; var34++) {
         float var35 = var34 / 8.0F;
         float var36 = (var34 + 1) / 8.0F;
         float var37 = var35;
         float var38 = var34 == 7 ? 0.999F : var36;
         double var39 = var27 * var35;
         double var41 = var27 * var36;
         int var43 = MathHelper.clamp(Math.round(var31 * (1.0F - var35) * (1.0F - var35)), 0, 255);
         int var44 = MathHelper.clamp(Math.round(var31 * (1.0F - var36) * (1.0F - var36)), 0, 255);

         for (int var45 = 0; var45 < 72; var45++) {
            float var46 = colorMeasure[var45];
            float var47 = colorMeasure[var45 + 1];
            double var48 = windowConvert[var45];
            double var50 = presetWrite[var45];
            double var52 = windowConvert[var45 + 1];
            double var54 = presetWrite[var45 + 1];
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var48,
               var50,
               var41,
               var28,
               var29,
               var30,
               var44,
               var46 + var32 * 0.18F,
               var33 + var38
            );
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var48,
               var50,
               var39,
               var28,
               var29,
               var30,
               var43,
               var46 + var32 * 0.18F,
               var33 + var37
            );
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var52,
               var54,
               var39,
               var28,
               var29,
               var30,
               var43,
               var47 + var32 * 0.18F,
               var33 + var37
            );
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var52,
               var54,
               var41,
               var28,
               var29,
               var30,
               var44,
               var47 + var32 * 0.18F,
               var33 + var38
            );
         }
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25,
      double var27,
      double var29,
      double var31,
      int var33,
      int var34,
      int var35,
      int var36,
      float var37,
      float var38
   ) {
      double var39 = var3 + (var15 * var27 + var21 * var29) * var31;
      double var41 = var5 + (var17 * var27 + var23 * var29) * var31;
      double var43 = var7 + (var19 * var27 + var25 * var29) * var31;
      var1.vertex(var2, (float)var39, (float)var41, (float)var43)
         .texture(var37, var38)
         .color(var33, var34, var35, var36)
         .normal((float)var9, (float)var11, (float)var13);
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25,
      float var27,
      float var28,
      int var29,
      int var30,
      int var31,
      int var32,
      float var33,
      float var34
   ) {
      for (int var35 = 0; var35 < 6; var35++) {
         float var36 = var35 / 6.0F;
         float var37 = (var35 + 1) / 6.0F;
         float var38 = var36;
         float var39 = var35 == 5 ? 0.999F : var37;
         double var40 = var27 * var36;
         double var42 = var27 * var37;
         double var44 = var28 * (1.0 - var36 * var36);
         double var46 = var28 * (1.0 - var37 * var37);
         int var48 = MathHelper.clamp(Math.round(var32 * (1.0F - var36 * 0.62F)), 0, 255);
         int var49 = MathHelper.clamp(Math.round(var32 * (1.0F - var37 * 0.62F)), 0, 255);

         for (int var50 = 0; var50 < 72; var50++) {
            float var51 = colorMeasure[var50];
            float var52 = colorMeasure[var50 + 1];
            double var53 = windowConvert[var50];
            double var55 = presetWrite[var50];
            double var57 = windowConvert[var50 + 1];
            double var59 = presetWrite[var50 + 1];
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var53,
               var55,
               var42,
               var46,
               var37,
               var29,
               var30,
               var31,
               var49,
               var51 + var33 * 0.26F,
               var34 + var39
            );
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var53,
               var55,
               var40,
               var44,
               var36,
               var29,
               var30,
               var31,
               var48,
               var51 + var33 * 0.26F,
               var34 + var38
            );
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var57,
               var59,
               var40,
               var44,
               var36,
               var29,
               var30,
               var31,
               var48,
               var52 + var33 * 0.26F,
               var34 + var38
            );
            this.handle(
               var1,
               var2,
               var3,
               var5,
               var7,
               var9,
               var11,
               var13,
               var15,
               var17,
               var19,
               var21,
               var23,
               var25,
               var57,
               var59,
               var42,
               var46,
               var37,
               var29,
               var30,
               var31,
               var49,
               var52 + var33 * 0.26F,
               var34 + var39
            );
         }
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25,
      double var27,
      double var29,
      double var31,
      double var33,
      float var35,
      int var36,
      int var37,
      int var38,
      int var39,
      float var40,
      float var41
   ) {
      double var42 = var15 * var27 + var21 * var29;
      double var44 = var17 * var27 + var23 * var29;
      double var46 = var19 * var27 + var25 * var29;
      double var48 = var3 + var42 * var31 + var9 * var33;
      double var50 = var5 + var44 * var31 + var11 * var33;
      double var52 = var7 + var46 * var31 + var13 * var33;
      double var54 = var9 * (1.0 - var35 * 0.32F) + var42 * var35 * 0.68F;
      double var56 = var11 * (1.0 - var35 * 0.32F) + var44 * var35 * 0.68F;
      double var58 = var13 * (1.0 - var35 * 0.32F) + var46 * var35 * 0.68F;
      double var60 = Math.sqrt(var54 * var54 + var56 * var56 + var58 * var58);
      if (var60 <= 1.0E-5) {
         var54 = var9;
         var56 = var11;
         var58 = var13;
      } else {
         var54 /= var60;
         var56 /= var60;
         var58 /= var60;
      }

      var1.vertex(var2, (float)var48, (float)var50, (float)var52)
         .texture(var40, var41)
         .color(var36, var37, var38, var39)
         .normal((float)var54, (float)var56, (float)var58);
   }

   private void handle(
      VertexConsumer var1, Matrix4f var2, double var3, double var5, double var7, double var9, double var11, double var13, int var15, int var16, int var17
   ) {
      double var18 = 0.004;
      double var20 = var3 - var18;
      double var22 = var5 - var18;
      double var24 = var7 - var18;
      double var26 = var9 + var18;
      double var28 = var11 + var18;
      double var30 = var13 + var18;
      int var32 = this.handle(var15, 255, 0.3F);
      int var33 = this.handle(var16, 255, 0.26F);
      int var34 = this.handle(var17, 255, 0.26F);
      this.handle(var1, var2, var20, var22, var24, var26, var28, var30, 0.046F, var15, var16, var17, 26);
      this.handle(var1, var2, var20, var22, var24, var26, var28, var30, 0.02F, var15, var16, var17, 60);
      this.handle(var1, var2, var20, var22, var24, var26, var28, var30, 0.008F, var32, var33, var34, 180);
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      float var15,
      int var16,
      int var17,
      int var18,
      int var19
   ) {
      this.handle(var1, var2, var3, var5, var7, var9, var5, var7, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var9, var5, var7, var9, var5, var13, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var9, var5, var13, var3, var5, var13, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var3, var5, var13, var3, var5, var7, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var3, var11, var7, var9, var11, var7, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var9, var11, var7, var9, var11, var13, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var9, var11, var13, var3, var11, var13, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var3, var11, var13, var3, var11, var7, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var3, var5, var7, var3, var11, var7, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var9, var5, var7, var9, var11, var7, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var9, var5, var13, var9, var11, var13, var15, var16, var17, var18, var19, 3.0F);
      this.handle(var1, var2, var3, var5, var13, var3, var11, var13, var15, var16, var17, var18, var19, 3.0F);
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      int var15,
      int var16,
      int var17,
      int var18
   ) {
      double var19 = var9 - var3;
      double var21 = var11 - var5;
      double var23 = var13 - var7;
      double var25 = Math.min(Math.min(var19, var21), var23) * 0.42;
      if (var25 < 0.18) {
         var25 = 0.18;
      }

      if (var25 > 0.38) {
         var25 = 0.38;
      }

      float var27 = 0.028F;
      this.handle(var1, var2, var3, var5, var7, 1.0, 1.0, 1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var9, var5, var7, -1.0, 1.0, 1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var9, var5, var13, -1.0, 1.0, -1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var3, var5, var13, 1.0, 1.0, -1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var3, var11, var7, 1.0, -1.0, 1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var9, var11, var7, -1.0, -1.0, 1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var9, var11, var13, -1.0, -1.0, -1.0, var25, var27, var15, var16, var17, var18);
      this.handle(var1, var2, var3, var11, var13, 1.0, -1.0, -1.0, var25, var27, var15, var16, var17, var18);
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      float var17,
      int var18,
      int var19,
      int var20,
      int var21
   ) {
      this.handle(var1, var2, var3, var5, var7, var3 + var9 * var15, var5, var7, var17, var18, var19, var20, var21, 3.0F);
      this.handle(var1, var2, var3, var5, var7, var3, var5 + var11 * var15, var7, var17, var18, var19, var20, var21, 3.0F);
      this.handle(var1, var2, var3, var5, var7, var3, var5, var7 + var13 * var15, var17, var18, var19, var20, var21, 3.0F);
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      float var21,
      float var22,
      int var23,
      int var24,
      int var25,
      int var26,
      float var27,
      float var28,
      float var29
   ) {
      double var30 = Math.max(0.01, var21 - var22 * 0.5F);
      double var32 = var21 + var22 * 0.5F;
      double var34 = var11 * var19 - var13 * var17;
      double var36 = var13 * var15 - var9 * var19;
      double var38 = var9 * var17 - var11 * var15;

      for (int var40 = 0; var40 < 72; var40++) {
         float var41 = colorMeasure[var40];
         float var42 = colorMeasure[var40 + 1];
         double var43 = windowConvert[var40];
         double var45 = presetWrite[var40];
         double var47 = windowConvert[var40 + 1];
         double var49 = presetWrite[var40 + 1];
         int var51 = this.handle(var26, var41, var27, var28);
         int var52 = this.handle(var26, var42, var27, var28);
         this.handle(
            var1,
            var2,
            var3,
            var5,
            var7,
            var9,
            var11,
            var13,
            var15,
            var17,
            var19,
            var43,
            var45,
            var32,
            var23,
            var24,
            var25,
            var51,
            var41 + var27 * 0.2F,
            var29 + 0.92F,
            var34,
            var36,
            var38
         );
         this.handle(
            var1,
            var2,
            var3,
            var5,
            var7,
            var9,
            var11,
            var13,
            var15,
            var17,
            var19,
            var43,
            var45,
            var30,
            var23,
            var24,
            var25,
            var51,
            var41 + var27 * 0.2F,
            var29 + 0.08F,
            var34,
            var36,
            var38
         );
         this.handle(
            var1,
            var2,
            var3,
            var5,
            var7,
            var9,
            var11,
            var13,
            var15,
            var17,
            var19,
            var47,
            var49,
            var30,
            var23,
            var24,
            var25,
            var52,
            var42 + var27 * 0.2F,
            var29 + 0.08F,
            var34,
            var36,
            var38
         );
         this.handle(
            var1,
            var2,
            var3,
            var5,
            var7,
            var9,
            var11,
            var13,
            var15,
            var17,
            var19,
            var47,
            var49,
            var32,
            var23,
            var24,
            var25,
            var52,
            var42 + var27 * 0.2F,
            var29 + 0.92F,
            var34,
            var36,
            var38
         );
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25,
      int var27,
      int var28,
      int var29,
      int var30,
      float var31,
      float var32,
      double var33,
      double var35,
      double var37
   ) {
      double var39 = var3 + (var9 * var21 + var15 * var23) * var25;
      double var41 = var5 + (var11 * var21 + var17 * var23) * var25;
      double var43 = var7 + (var13 * var21 + var19 * var23) * var25;
      var1.vertex(var2, (float)var39, (float)var41, (float)var43)
         .texture(var31, var32)
         .color(var27, var28, var29, var30)
         .normal((float)var33, (float)var35, (float)var37);
   }

   private int handle(int var1, float var2, float var3, float var4) {
      float var5 = 0.5F + 0.5F * (float)Math.sin((var2 * 3.0F - var3 * 2.0F) * Math.PI * 2.0);
      return MathHelper.clamp(Math.round(var1 * var4 * (0.48F + var5 * 0.52F)), 0, 255);
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      float var15,
      int var16,
      int var17,
      int var18,
      int var19,
      float var20
   ) {
      double var21 = var9 - var3;
      double var23 = var11 - var5;
      double var25 = var13 - var7;
      double var27 = Math.sqrt(var21 * var21 + var23 * var23 + var25 * var25);
      if (!(var27 <= 1.0E-5)) {
         double var29 = var21 / var27;
         double var31 = var23 / var27;
         double var33 = var25 / var27;
         double var35 = Math.abs(var31) < 0.92 ? 0.0 : 1.0;
         double var37 = Math.abs(var31) < 0.92 ? 1.0 : 0.0;
         double var39 = 0.0;
         double var41 = var37 * var33 - var39 * var31;
         double var43 = var39 * var29 - var35 * var33;
         double var45 = var35 * var31 - var37 * var29;
         double var47 = Math.sqrt(var41 * var41 + var43 * var43 + var45 * var45);
         if (var47 <= 1.0E-5) {
            var41 = 1.0;
            var43 = 0.0;
            var45 = 0.0;
         } else {
            var41 /= var47;
            var43 /= var47;
            var45 /= var47;
         }

         double var49 = var31 * var45 - var33 * var43;
         double var51 = var33 * var41 - var29 * var45;
         double var53 = var29 * var43 - var31 * var41;
         double var55 = var15 * 0.5;

         for (int var57 = 0; var57 < 6; var57++) {
            float var58 = sourceBuild[var57];
            float var59 = sourceBuild[var57 + 1];
            double var60 = animationSchedule[var57];
            double var62 = rendererScan[var57];
            double var64 = animationSchedule[var57 + 1];
            double var66 = rendererScan[var57 + 1];
            double var68 = var41 * var60 + var49 * var62;
            double var70 = var43 * var60 + var51 * var62;
            double var72 = var45 * var60 + var53 * var62;
            double var74 = var41 * var64 + var49 * var66;
            double var76 = var43 * var64 + var51 * var66;
            double var78 = var45 * var64 + var53 * var66;
            var1.vertex(var2, (float)(var3 + var68 * var55), (float)(var5 + var70 * var55), (float)(var7 + var72 * var55))
               .texture(0.0F, var20 + var58)
               .color(var16, var17, var18, var19)
               .normal((float)var68, (float)var70, (float)var72);
            var1.vertex(var2, (float)(var3 + var74 * var55), (float)(var5 + var76 * var55), (float)(var7 + var78 * var55))
               .texture(0.0F, var20 + var59)
               .color(var16, var17, var18, var19)
               .normal((float)var74, (float)var76, (float)var78);
            var1.vertex(var2, (float)(var9 + var74 * var55), (float)(var11 + var76 * var55), (float)(var13 + var78 * var55))
               .texture(1.0F, var20 + var59)
               .color(var16, var17, var18, var19)
               .normal((float)var74, (float)var76, (float)var78);
            var1.vertex(var2, (float)(var9 + var68 * var55), (float)(var11 + var70 * var55), (float)(var13 + var72 * var55))
               .texture(1.0F, var20 + var58)
               .color(var16, var17, var18, var19)
               .normal((float)var68, (float)var70, (float)var72);
         }
      }
   }

   private float handle(float var1, float var2, float var3, boolean var4) {
      float var5 = (float)Math.pow(this.compute(var1), 0.72F);
      float var6 = var2 + (var3 - var2) * var5;
      return var4 ? var6 : var6 * (1.0F - var5 * 0.36F);
   }

   private float process(float var1) {
      return this.handle(0.0F, 0.055F, var1) * (1.0F - this.handle(0.885F, 1.0F, var1));
   }

   private float handle(float var1, float var2, float var3) {
      float var4 = this.compute((var3 - var1) / Math.max(1.0E-5F, var2 - var1));
      return var4 * var4 * (3.0F - 2.0F * var4);
   }

   private float compute(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   private int handle(int var1, int var2, float var3) {
      float var4 = this.compute(var3);
      return MathHelper.clamp(Math.round(var1 + (var2 - var1) * var4), 0, 255);
   }

   private float render() {
      return (float)(System.nanoTime() - itemProject) * 1.0E-9F;
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, Predictions.NamedEntry var5) {
      String var6 = this.resolve(var5.ticks() / 20.0F);
      float var7 = 25.0F;
      float var8 = 3.0F;
      float var9 = 3.0F;
      float var10 = 22.0F;
      float var11 = 3.0F;
      float var12 = 6.0F;
      float var13 = RoundedRectRenderer.handle(FontRegistry.instance, var6, var7).instance;
      int var14 = this.handle(var5.icon());
      boolean var15 = var14 > 0;
      float var16 = var15 ? var10 + var11 : 0.0F;
      float var17 = var8 * 2.0F + var16 + var13 + var12;
      float var18 = var9 + Math.max(var15 ? var10 : 0.0F, var7);
      float var19 = var18 / 2.0F;
      var1.handle(var2, var3);
      var1.process(var4, var4);
      float var20 = -var17 / 2.0F;
      float var21 = -var18;
      this.handle(var1, var20, var21, var17, var18, var19, 111.0F);
      float var22 = var20 + var8 + (var15 ? 0.0F : var12 / 2.0F);
      if (var15) {
         float var23 = var20 + var8;
         float var24 = var21 + (var18 - var10) / 2.0F;
         var1.handle(var23, var24 + var10);
         var1.process(1.0F, -1.0F);
         var1.handle(var14, 0.0F, 0.0F, var10, var10);
         var1.check();
         var1.prepare();
         var22 = var23 + var10 + var11;
      }

      float var25 = var21 + var9 + var7 - 10.0F;
      int var26 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), 230);
      var1.handle(FontRegistry.instance, var22 + 1.0F, var25 + 1.0F, var7, var6, var26);
      var1.check();
      var1.prepare();
   }

   private boolean process(Predictions.NamedEntry var1) {
      Identifier var2 = var1.icon();
      return var2 != null && var2.getPath().contains("ender_pearl");
   }

   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, Predictions.NamedEntry var5) {
      String var6 = var5.ownerName();
      if (var6 != null && !var6.isEmpty() && !var6.equals("Unknown") && !var6.equals("You")) {
         float var7 = 22.0F;
         float var8 = 4.0F;
         float var9 = 3.0F;
         float var10 = 18.0F;
         float var11 = 4.0F;
         float var12 = RoundedRectRenderer.handle(FontRegistry.instance, var6, var7).instance;
         float var13 = var8 * 2.0F + var10 + var11 + var12;
         float var14 = var9 + Math.max(var10, var7);
         float var15 = var14 / 2.0F;
         var1.handle(var2, var3);
         var1.process(var4, var4);
         float var16 = -var13 / 2.0F;
         float var17 = -var14;
         this.handle(var1, var16, var17, var13, var14, var15, 111.0F);
         float var18 = var16 + var8;
         float var19 = var17 + (var14 - var10) / 2.0F;
         this.handle(var1, var6, var18, var19, var10, 1.0F);
         float var20 = var18 + var10 + var11;
         float var21 = var17 + var9 + var7 - 10.0F;
         int var22 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), 230);
         var1.handle(FontRegistry.instance, var20 + 1.0F, var21 + 1.0F, var7, var6, var22);
         var1.check();
         var1.prepare();
      }
   }

   private void handle(RoundedRectRenderer var1, String var2, float var3, float var4, float var5, float var6) {
      if (Module.client.getNetworkHandler() != null) {
         PlayerListEntry var7 = null;

         for (PlayerListEntry var9 : Module.client.getNetworkHandler().getPlayerList()) {
            if (var9.getProfile().getName().equalsIgnoreCase(var2)) {
               var7 = var9;
               break;
            }
         }

         if (var7 != null) {
            try {
               Identifier var13 = var7.getSkinTextures().texture();
               AbstractTexture var14 = Module.client.getTextureManager().getTexture(var13);
               if (var14 != null && var14.getGlTexture() instanceof GlTexture var10 && var10.getGlId() > 0) {
                  int var15 = var10.getGlId();
                  GlStateManager._bindTexture(var15);
                  var1.update(var6);
                  var1.handle(var15, var3, var4, var5, var5, 0.125F, 0.125F, 0.25F, 0.25F, 3.0F);
                  var1.handle(var15, var3, var4, var5, var5, 0.625F, 0.125F, 0.75F, 0.25F, 3.0F);
                  var1.onTick();
               }
            } catch (Throwable var12) {
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var7 / 155.0F;
      int var9 = this.keyFilter.handle(255.0F);
      int var10 = this.keyFilter.resolve(var8);
      var1.handle(var2, var3, var4, var5, 12.0F, var9);
   }

   private String resolve(float var1) {
      String var2 = String.format(Locale.US, "%.1f", var1).replace('.', ',');
      return var2 + " сек";
   }

   private int handle(Identifier var1) {
      if (var1 == null) {
         return -1;
      } else {
         TextureManager var2 = Module.client.getTextureManager();
         if (var2 == null) {
            return -1;
         } else {
            AbstractTexture var3 = var2.getTexture(var1);
            if (var3 == null) {
               return -1;
            } else if (var3.getGlTexture() instanceof GlTexture var5) {
               int var6 = var5.getGlId();
               return var6 > 0 ? var6 : -1;
            } else {
               return -1;
            }
         }
      }
   }

   private Identifier process(Item var1) {
      if (var1 instanceof TridentItem) {
         return Identifier.of("minecraft", "textures/item/trident.png");
      } else if (var1 instanceof BowItem || var1 instanceof CrossbowItem) {
         return Identifier.of("minecraft", "textures/item/arrow.png");
      } else if (var1 instanceof ThrowablePotionItem) {
         return Identifier.of("minecraft", "textures/item/potion.png");
      } else if (var1 instanceof SnowballItem) {
         return Identifier.of("minecraft", "textures/item/snowball.png");
      } else if (var1 instanceof EggItem) {
         return Identifier.of("minecraft", "textures/item/egg.png");
      } else if (var1 instanceof ExperienceBottleItem) {
         return Identifier.of("minecraft", "textures/item/experience_bottle.png");
      } else {
         return var1 instanceof EnderPearlItem ? Identifier.of("minecraft", "textures/item/ender_pearl.png") : null;
      }
   }

   private Identifier handle(ProjectileEntity var1) {
      String var2 = Registries.ENTITY_TYPE.getId(var1.getType()).getPath();
      if (var2.contains("trident")) {
         return Identifier.of("minecraft", "textures/item/trident.png");
      } else if (var2.contains("snowball")) {
         return Identifier.of("minecraft", "textures/item/snowball.png");
      } else if (var2.contains("arrow")) {
         return Identifier.of("minecraft", "textures/item/arrow.png");
      } else if (var2.contains("potion")) {
         return Identifier.of("minecraft", "textures/item/potion.png");
      } else if (var2.contains("pearl")) {
         return Identifier.of("minecraft", "textures/item/ender_pearl.png");
      } else if (var2.contains("egg")) {
         return Identifier.of("minecraft", "textures/item/egg.png");
      } else {
         return var2.contains("experience_bottle") ? Identifier.of("minecraft", "textures/item/experience_bottle.png") : null;
      }
   }

   static {
      for (int var0 = 0; var0 <= 72; var0++) {
         float var1 = var0 / 72.0F;
         colorMeasure[var0] = var1;
         double var2 = var1 * Math.PI * 2.0;
         windowConvert[var0] = Math.cos(var2);
         presetWrite[var0] = Math.sin(var2);
      }

      for (int var4 = 0; var4 <= 6; var4++) {
         float var6 = var4 < 6 ? var4 / 6.0F : 0.999F;
         sourceBuild[var4] = var6;
         double var7 = var6 * Math.PI * 2.0;
         animationSchedule[var4] = Math.cos(var7);
         rendererScan[var4] = Math.sin(var7);
      }

      for (int var5 = 0; var5 <= 10; var5++) {
         outputCollapse[var5] = var5 < 10 ? var5 / 10.0F : 0.999F;
      }

      profileInvoke = Identifier.of("wild", "core/prediction_vfx");
      sourceSchedule = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      timerRender = RenderPipelines.register(
         RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
            .withLocation(Identifier.of("wild", "prediction_glass"))
            .withVertexShader(profileInvoke)
            .withFragmentShader(profileInvoke)
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
      );
      scaleSave = RenderPipelines.register(
         RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
            .withLocation(Identifier.of("wild", "prediction_glass_no_depth"))
            .withVertexShader(profileInvoke)
            .withFragmentShader(profileInvoke)
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
      );
      colorCompute = RenderPipelines.register(
         RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
            .withLocation(Identifier.of("wild", "prediction_emission"))
            .withVertexShader(profileInvoke)
            .withFragmentShader(profileInvoke)
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(sourceSchedule)
            .build()
      );
      scaleAdapt = RenderPipelines.register(
         RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
            .withLocation(Identifier.of("wild", "prediction_emission_no_depth"))
            .withVertexShader(profileInvoke)
            .withFragmentShader(profileInvoke)
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, DrawMode.QUADS)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(sourceSchedule)
            .build()
      );
      textureRun = RenderLayer.of("wild_prediction_glass", 2097152, false, true, timerRender, MultiPhaseParameters.builder().build(false));
      indexBind = RenderLayer.of("wild_prediction_glass_no_depth", 2097152, false, true, scaleSave, MultiPhaseParameters.builder().build(false));
      actionRead = RenderLayer.of("wild_prediction_emission", 2097152, false, true, colorCompute, MultiPhaseParameters.builder().build(false));
      configCollapse = RenderLayer.of("wild_prediction_emission_no_depth", 2097152, false, true, scaleAdapt, MultiPhaseParameters.builder().build(false));
   }

   record DataRecord(double speed, double gravity, float pitchOffset, boolean applyPhysicsBeforeMove) {
   }

   record NamedEntry(
      String key,
      List<Vec3d> path,
      Vec3d landingPos,
      Vec3d blockRenderPos,
      Box targetBox,
      int ticks,
      Entity hitEntity,
      BlockHitResult blockHit,
      String ownerName,
      Identifier icon,
      boolean isPreAim
   ) {
      Predictions.NamedEntry withRenderState(List<Vec3d> var1, Vec3d var2, Vec3d var3, Box var4) {
         return new Predictions.NamedEntry(
            this.key, var1, var2, var3, var4, this.ticks, this.hitEntity, this.blockHit, this.ownerName, this.icon, this.isPreAim
         );
      }

      static String resolveOwnerName(ProjectileEntity var0) {
         return var0.getOwner() instanceof PlayerEntity var2 ? var2.getName().getString() : "Unknown";
      }
   }
}
