package ru.wild.modules.visuals;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModels;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.render.TexturedQuadsRenderer;
import ru.wild.render.WorldVertexBuffer;

@ModuleRegister(name = "DeadEffect", description = "Плавный выход души из модели игрока при потере тотема или смерти.", category = ModuleCategory.Visuals)
public final class DeadEffect extends Module {
   private static final String source = "Тотем";
   private static final String target = "Смерть";
   private static final String pending = "Себя";
   private static final String previous = "Игроки";
   private static final long latest = 180000000L;
   private static final long summary = 700000000L;
   private static final long matrixBlend = 5000000000L;
   private static final float vectorMatch = (float) (Math.PI * 2);
   private static final int itemProject = 96;
   private static final int responseCompute = 36;
   private final ChoiceSetting providerFetch = new ChoiceSetting("События", new BooleanSetting("Тотем", true), new BooleanSetting("Смерть", true));
   private final ChoiceSetting profileDraw = new ChoiceSetting("Цели", new BooleanSetting("Себя", true), new BooleanSetting("Игроки", true));
   private final NumberSetting vectorPerform = new NumberSetting("Длительность", 1.65F, 0.55F, 4.0F, 0.05F, false);
   private final NumberSetting eventAttach = new NumberSetting("Подъём", 1.85F, 0.6F, 4.0F, 0.05F, false);
   private final NumberSetting serverRead = new NumberSetting("Прозрачность", 0.74F, 0.15F, 1.0F, 0.01F, true);
   private final NumberSetting positionAdvance = new NumberSetting("Свечение", 1.15F, 0.0F, 2.4F, 0.05F, false);
   private final NumberSetting frameCheck = new NumberSetting("Частицы", 34.0F, 0.0F, 90.0F, 1.0F, false);
   private final ColorSetting moduleCollect = new ColorSetting("Цвет тотема", 31.0F, 0.82F, 1.0F);
   private final ColorSetting providerClose = new ColorSetting("Цвет смерти", 74.0F, 0.68F, 1.0F);
   private final Map<Integer, DeadEffect.CacheEntry> presetSave = new ConcurrentHashMap<>();
   private final Map<Integer, DeadEffect.DataRecord> windowConvert = new ConcurrentHashMap<>();
   private final Set<Integer> presetWrite = ConcurrentHashMap.newKeySet();
   private LoadedEntityModels colorMeasure;
   private PlayerEntityModel animationSchedule;
   private PlayerEntityModel rendererScan;
   private DeadEffect.DataRecord sourceBuild;
   private DeadEffect.DataRecord outputCollapse;

   public DeadEffect() {
      this.handle(
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach,
         this.serverRead,
         this.positionAdvance,
         this.frameCheck,
         this.moduleCollect,
         this.providerClose
      );
   }

   @Override
   public void handle() {
      this.presetSave.clear();
      this.windowConvert.clear();
      this.presetWrite.clear();
      super.handle();
   }

   @Override
   public void process() {
      this.presetSave.clear();
      this.windowConvert.clear();
      this.presetWrite.clear();
      super.process();
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.presetSave.clear();
      this.windowConvert.clear();
      this.presetWrite.clear();
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.world != null && var1 != null && !var1.compute()) {
         if (var1.resolve() instanceof EntityStatusS2CPacket var2) {
            if (var2.getEntity(Module.client.world) instanceof PlayerEntity var4 && this.handle(var4)) {
               byte var5 = var2.getStatus();
               if (var5 == 35 && this.providerFetch.process("Тотем")) {
                  this.handle(var4, DeadEffect.Mode.TOTEM);
               } else if (var5 == 3 && this.providerFetch.process("Смерть") && process(var4)) {
                  this.handle(var4, DeadEffect.Mode.DEATH);
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         long var2 = System.nanoTime();
         this.handle(var2);

         for (PlayerEntity var5 : Module.client.world.getPlayers()) {
            if (this.handle(var5)) {
               int var6 = var5.getId();
               if (process(var5)) {
                  if (this.presetWrite.add(var6) && this.providerFetch.process("Смерть")) {
                     this.handle(var5, DeadEffect.Mode.DEATH);
                  }
               } else {
                  this.presetWrite.remove(var6);
               }
            }
         }
      } else {
         this.presetSave.clear();
         this.presetWrite.clear();
      }
   }

   public static void handle(PlayerEntityRenderState var0, PlayerEntityModel var1, MatrixStack var2, VertexConsumerProvider var3, int var4, int var5) {
      DeadEffect var6 = refresh();
      if (var6 != null) {
         var6.handle(var0, var1);
      }
   }

   private void handle(PlayerEntityRenderState var1, PlayerEntityModel var2) {
      if (this.enabled && var1 != null && var2 != null && var1.skinTextures != null) {
         if (!var1.spectator && !var1.invisible && !var1.invisibleToPlayer) {
            DeadEffect.DataRecord var3 = DeadEffect.DataRecord.capture(var2, System.nanoTime());
            this.windowConvert.put(var1.id, var3);
            DeadEffect.CacheEntry var4 = this.presetSave.get(var1.id);
            if (var4 != null) {
               if (var4.config == null) {
                  var4.config = var3;
               }

               if (var4.state == null) {
                  var4.state = DeadEffect.PrimaryPoint3d.fromState(var1);
               }

               if (var4.cache == null) {
                  var4.cache = var1.skinTextures.texture();
               }

               var4.output = handle(var1);
            }
         }
      }
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (this.enabled && Module.client.world != null && Module.client.player != null && var1 != null && !this.presetSave.isEmpty()) {
         long var2 = System.nanoTime();
         this.handle(var2);
         if (!this.presetSave.isEmpty()) {
            Immediate var4 = WorldVertexBuffer.handle();

            try {
               for (Entry var6 : this.presetSave.entrySet()) {
                  int var7 = (Integer)var6.getKey();
                  if (var7 != Module.client.player.getId() || Module.client.options == null || Module.client.options.getPerspective() != Perspective.FIRST_PERSON) {
                     DeadEffect.CacheEntry var8 = (DeadEffect.CacheEntry)var6.getValue();
                     if (!this.handle(var8, var1.compute(), var4, var2)) {
                        this.presetSave.remove(var7, var8);
                     }
                  }
               }
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   private boolean handle(DeadEffect.CacheEntry var1, MatrixStack var2, VertexConsumerProvider var3, long var4) {
      if (var1 != null && var2 != null && var3 != null && var1.state != null) {
         float var6 = (float)(var4 - var1.data) / 1.0E9F;
         float var7 = this.process(var1.instance);
         float var8 = this.handle(var1.instance);
         if (var6 >= var8) {
            return false;
         }

         float var9 = process(var6 / var8);
         float var10 = process(var6 / var7);
         float var11 = handle(var7 * 0.58F, var8, var6);
         float var12 = handle(0.0F, 0.11F, var9);
         float var13 = 1.0F - var11;
         float var14 = process(var12 * var13 * this.serverRead.compute());
         if (var14 <= 0.002F) {
            return true;
         }

         float var15 = handle(var10);
         float var16 = this.eventAttach.compute() * var1.instance.context * (var15 + var11 * 0.075F);
         float var17 = var1.context + var6 * 2.15F;
         float var18 = (float)Math.sin(var17 * 1.7F) * (0.025F + var11 * 0.045F) * var15;
         float var19 = (float)Math.cos(var17 * 1.3F) * (0.025F + var11 * 0.045F) * var15;
         float var20 = 1.0F + (float)Math.sin(var9 * Math.PI * 3.0) * 0.022F * (1.0F - var9);
         float var21 = (var20 + var1.instance.config * var15) * (1.0F + var11 * 0.055F);
         int var22 = this.compute(var1.instance);
         this.handle(var2, var3, var1, var9, var14, var16, var17, var11);
         Identifier var23 = var1.cache;
         if (var23 == null) {
            return true;
         }

         PlayerEntityModel var24 = this.compute(var1.output);
         if (var24 == null) {
            return true;
         }

         RenderLayer var25 = RenderLayer.getEntityTranslucentEmissive(var23);
         int var26 = handle(handle(16777215, var22, var1.instance.active), var14 * var1.instance.current * (1.0F - var11 * 0.22F));
         DeadEffect.DataRecord var27 = var1.config != null ? var1.config : this.resolve(var1.output);
         if (var27 != null) {
            var27.apply(var24);
         }

         this.handle(var24, var2, var3, var25, var1.state, 15728880, OverlayTexture.DEFAULT_UV, var18, var16, var19, var21, var26);
         return true;
      } else {
         return false;
      }
   }

   private void handle(
      PlayerEntityModel var1,
      MatrixStack var2,
      VertexConsumerProvider var3,
      RenderLayer var4,
      DeadEffect.PrimaryPoint3d var5,
      int var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12
   ) {
      if ((var12 >>> 24 & 0xFF) != 0) {
         var2.push();
         this.handle(var2, var5, var8, var9, var10, var11);
         VertexConsumer var13 = var3.getBuffer(var4);
         var1.render(var2, var13, var6, var7, var12);
         var2.pop();
         if (var3 instanceof Immediate var14) {
            var14.draw(var4);
         }
      }
   }

   private void handle(MatrixStack var1, DeadEffect.PrimaryPoint3d var2, float var3, float var4, float var5, float var6) {
      Camera var7 = Module.client.gameRenderer.getCamera();
      Vec3d var8 = var7 == null ? Vec3d.ZERO : var7.getPos();
      var1.translate(var2.x - var8.x + var3, var2.y - var8.y + var4, var2.z - var8.z + var5);
      var1.scale(var2.baseScale, var2.baseScale, var2.baseScale);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - var2.bodyYaw));
      var1.scale(-var6, -var6, var6);
      var1.translate(0.0F, -1.501F, 0.0F);
   }

   private PlayerEntityModel compute(boolean var1) {
      if (this.colorMeasure == null) {
         this.colorMeasure = new LoadedEntityModels(EntityModels.getModels());
      }

      if (var1) {
         if (this.rendererScan == null) {
            this.rendererScan = new PlayerEntityModel(this.colorMeasure.getModelPart(EntityModelLayers.PLAYER_SLIM), true);
            this.outputCollapse = DeadEffect.DataRecord.capture(this.rendererScan, 0L);
         }

         return this.rendererScan;
      } else {
         if (this.animationSchedule == null) {
            this.animationSchedule = new PlayerEntityModel(this.colorMeasure.getModelPart(EntityModelLayers.PLAYER), false);
            this.sourceBuild = DeadEffect.DataRecord.capture(this.animationSchedule, 0L);
         }

         return this.animationSchedule;
      }
   }

   private DeadEffect.DataRecord resolve(boolean var1) {
      this.compute(var1);
      return var1 ? this.outputCollapse : this.sourceBuild;
   }

   private void handle(PlayerEntity var1, DeadEffect.Mode var2) {
      if (var1 != null && this.handle(var1)) {
         int var3 = var1.getId();
         long var4 = System.nanoTime();
         DeadEffect.CacheEntry var6 = this.presetSave.get(var3);
         if (var6 == null || var6.instance != var2 || var4 - var6.data >= 180000000L) {
            DeadEffect.DataRecord var7 = this.windowConvert.get(var3);
            if (var7 != null && var4 - var7.capturedNanos > 700000000L) {
               var7 = null;
            }

            this.presetSave
               .put(
                  var3,
                  new DeadEffect.CacheEntry(
                     var2,
                     var4,
                     (float)(ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0),
                     var7,
                     DeadEffect.PrimaryPoint3d.fromPlayer(var1),
                     compute(var1),
                     resolve(var1)
                  )
               );
            if (this.frameCheck.compute() > 0.5F) {
               double var8 = var1.getX();
               double var10 = var1.getY();
               double var12 = var1.getZ();
               double var14 = Math.max(1.0, var1.getHeight());
               Module.client.execute(() -> this.handle(var8, var10, var12, var14, var2));
            }
         }
      }
   }

   private void handle(double var1, double var3, double var5, double var7, DeadEffect.Mode var9) {
      if (Module.client.particleManager != null) {
         ThreadLocalRandom var10 = ThreadLocalRandom.current();
         int var11 = Math.max(0, Math.round(this.frameCheck.compute()));

         for (int var12 = 0; var12 < var11; var12++) {
            double var13 = var10.nextDouble(Math.PI * 2);
            double var15 = var10.nextDouble(0.08, 0.56);
            double var17 = var1 + Math.cos(var13) * var15;
            double var19 = var3 + var10.nextDouble(0.08, var7 + 0.42);
            double var21 = var5 + Math.sin(var13) * var15;
            double var23 = var10.nextDouble(0.018, 0.07);
            double var25 = Math.cos(var13) * var23;
            double var27 = var10.nextDouble(0.045, 0.145) * var9.renderer;
            double var29 = Math.sin(var13) * var23;
            Module.client.particleManager.addParticle(var9.handle(var12), var17, var19, var21, var25, var27, var29);
         }
      }
   }

   private void handle(long var1) {
      Iterator var3 = this.presetSave.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         DeadEffect.CacheEntry var5 = (DeadEffect.CacheEntry)var4.getValue();
         float var6 = this.handle(var5.instance) + 0.25F;
         if ((float)(var1 - var5.data) > var6 * 1.0E9F) {
            var3.remove();
         }
      }

      this.windowConvert.entrySet().removeIf(var2 -> var1 - var2.getValue().capturedNanos > 5000000000L);
   }

   private void handle(MatrixStack var1, VertexConsumerProvider var2, DeadEffect.CacheEntry var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1 != null && var3.state != null && !(var5 <= 0.01F) && Module.client.gameRenderer != null) {
         Camera var9 = Module.client.gameRenderer.getCamera();
         if (var9 != null) {
            RenderLayer var10 = TexturedQuadsRenderer.compute();
            VertexConsumer var11 = var2.getBuffer(var10);
            Matrix4f var12 = var1.peek().getPositionMatrix();
            Vec3d var13 = var9.getPos();
            DeadEffect.PrimaryPoint3d var14 = var3.state;
            float var15 = (float)(var14.x - var13.x);
            float var16 = (float)(var14.y - var13.y);
            float var17 = (float)(var14.z - var13.z);
            this.handle(var11, var12, var3, var15, var16, var17, var4, var5, var6);
            this.handle(var11, var12, var3, var15, var16, var17, var4, var5, var6, var7);
            this.process(var11, var12, var3, var15, var16, var17, var4, var5, var6);
            this.handle(var11, var12, var3, var15, var16, var17, var4, var5, var6, var7, var8);
            if (var2 instanceof Immediate var18) {
               var18.draw(var10);
            }
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, DeadEffect.CacheEntry var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      for (int var10 = 0; var10 < 3; var10++) {
         float var11 = compute(var7 * (1.22F + var3.instance.enabled) + var10 * 0.31F);
         float var12 = (1.0F - var11) * handle(0.0F, 0.18F, var11);
         float var13 = var3.instance.mode + var11 * var3.instance.selection;
         float var14 = 0.02F + (1.0F - var11) * 0.045F;
         float var15 = var5 + 0.08F + var9 * (0.1F + var10 * 0.075F) + var10 * 0.16F;
         int var16 = handle(handle(this.compute(var3.instance), var3.instance.handler, 0.35F + var10 * 0.18F), var8 * var12 * 0.74F);
         this.handle(var1, var2, var4, var15, var6, var13, var14, var16);
      }
   }

   private void handle(
      VertexConsumer var1, Matrix4f var2, DeadEffect.CacheEntry var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10
   ) {
      float var11 = 1.38F + var9 * 0.72F;

      for (int var12 = 0; var12 < 2; var12++) {
         float var13 = var12 == 0 ? 1.0F : -1.0F;
         int var14 = var12 == 0 ? var3.instance.pointEncode : var3.instance.animator;

         for (int var15 = 0; var15 < 36; var15++) {
            float var16 = var15 / 36.0F;
            float var17 = (var15 + 1) / 36.0F;
            float var18 = var10 + var13 * (var16 * (float) (Math.PI * 2) * 1.72F + var7 * (float) (Math.PI * 2) * 1.15F);
            float var19 = var10 + var13 * (var17 * (float) (Math.PI * 2) * 1.72F + var7 * (float) (Math.PI * 2) * 1.15F);
            float var20 = 0.34F + 0.13F * (float)Math.sin(var16 * Math.PI + var7 * 3.0F);
            float var21 = 0.34F + 0.13F * (float)Math.sin(var17 * Math.PI + var7 * 3.0F);
            float var22 = 0.032F + 0.018F * (1.0F - var7);
            float var23 = var5 + 0.12F + var16 * var11;
            float var24 = var5 + 0.12F + var17 * var11;
            float var25 = (float)Math.sin(var16 * Math.PI) * (1.0F - handle(0.86F, 1.0F, var7));
            int var26 = handle(handle(var14, var3.instance.animationDraw, var16), var8 * var25 * 0.54F);
            int var27 = handle(handle(var14, var3.instance.animationDraw, var17), var8 * var25 * 0.54F);
            this.handle(var1, var2, var4, var6, var23, var24, var18, var19, var20, var21, var22, var26, var27);
         }
      }
   }

   private void process(VertexConsumer var1, Matrix4f var2, DeadEffect.CacheEntry var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var5 + 0.08F + var9 * 0.08F;
      float var11 = var5 + 1.7F + var9 * 0.92F;
      float var12 = 0.1F + 0.055F * (1.0F - var7);
      int var13 = handle(var3.instance.handler, var8 * 0.04F);
      int var14 = handle(handle(this.compute(var3.instance), 16777215, 0.38F), var8 * 0.3F * (1.0F - var7 * 0.34F));
      this.handle(
         var1,
         var2,
         var4 - var12,
         var10,
         var6,
         var4 + var12,
         var10,
         var6,
         var4 + var12 * 0.32F,
         var11,
         var6,
         var4 - var12 * 0.32F,
         var11,
         var6,
         var13,
         var13,
         var14,
         var14
      );
      this.handle(
         var1,
         var2,
         var4,
         var10,
         var6 - var12,
         var4,
         var10,
         var6 + var12,
         var4,
         var11,
         var6 + var12 * 0.32F,
         var4,
         var11,
         var6 - var12 * 0.32F,
         var13,
         var13,
         var14,
         var14
      );
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      DeadEffect.CacheEntry var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      if (!(var11 <= 0.02F)) {
         byte var12 = 18;

         for (int var13 = 0; var13 < var12; var13++) {
            float var14 = compute(var3.context * 0.137F + var13 * 0.6180339F);
            float var15 = compute(var7 * (0.78F + var14 * 0.22F) + var14);
            float var16 = var10 * 0.78F + var13 * 2.399963F + var15 * (float) (Math.PI * 2) * 0.62F;
            float var17 = 0.18F + var15 * (0.38F + var14 * 0.34F);
            float var18 = var4 + (float)Math.cos(var16) * var17;
            float var19 = var6 + (float)Math.sin(var16) * var17;
            float var20 = var5 + 0.52F + var9 * (0.45F + var14 * 0.28F) + var15 * (0.92F + var14 * 0.42F);
            float var21 = (0.025F + var14 * 0.035F) * (1.0F - var15 * 0.42F);
            float var22 = var8 * var11 * (1.0F - var15) * (0.3F + var14 * 0.28F) * this.positionAdvance.compute();
            int var23 = handle(handle(var3.instance.handler, var3.instance.animationDraw, var14), var22);
            this.handle(
               var1,
               var2,
               var18,
               var20 + var21,
               var19,
               var18 + var21,
               var20,
               var19,
               var18,
               var20 - var21,
               var19,
               var18 - var21,
               var20,
               var19,
               var23,
               var23,
               var23,
               var23
            );
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      if ((var8 >>> 24 & 0xFF) != 0) {
         float var9 = Math.max(0.02F, var6 - var7);
         float var10 = var6 + var7;

         for (int var11 = 0; var11 < 96; var11++) {
            float var12 = (float) (Math.PI * 2) * var11 / 96.0F;
            float var13 = (float) (Math.PI * 2) * (var11 + 1) / 96.0F;
            float var14 = (float)Math.cos(var12);
            float var15 = (float)Math.sin(var12);
            float var16 = (float)Math.cos(var13);
            float var17 = (float)Math.sin(var13);
            this.handle(
               var1,
               var2,
               var3 + var14 * var9,
               var4,
               var5 + var15 * var9,
               var3 + var16 * var9,
               var4,
               var5 + var17 * var9,
               var3 + var16 * var10,
               var4,
               var5 + var17 * var10,
               var3 + var14 * var10,
               var4,
               var5 + var15 * var10,
               var8,
               var8,
               var8,
               var8
            );
         }
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12,
      int var13
   ) {
      if ((var12 >>> 24 & 0xFF | var13 >>> 24 & 0xFF) != 0) {
         float var14 = (float)Math.cos(var7);
         float var15 = (float)Math.sin(var7);
         float var16 = (float)Math.cos(var8);
         float var17 = (float)Math.sin(var8);
         this.handle(
            var1,
            var2,
            var3 + var14 * (var9 - var11),
            var5,
            var4 + var15 * (var9 - var11),
            var3 + var14 * (var9 + var11),
            var5,
            var4 + var15 * (var9 + var11),
            var3 + var16 * (var10 + var11),
            var6,
            var4 + var17 * (var10 + var11),
            var3 + var16 * (var10 - var11),
            var6,
            var4 + var17 * (var10 - var11),
            var12,
            var12,
            var13,
            var13
         );
      }
   }

   private void handle(
      VertexConsumer var1,
      Matrix4f var2,
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
      int var15,
      int var16,
      int var17,
      int var18
   ) {
      this.handle(var1, var2, var3, var4, var5, var15);
      this.handle(var1, var2, var6, var7, var8, var16);
      this.handle(var1, var2, var9, var10, var11, var17);
      this.handle(var1, var2, var12, var13, var14, var18);
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, int var6) {
      var1.vertex(var2, var3, var4, var5).color(handle(var6), process(var6), compute(var6), var6 >>> 24 & 0xFF);
   }

   private boolean handle(PlayerEntity var1) {
      if (var1 == null) {
         return false;
      } else {
         return Module.client.player != null && var1.getId() == Module.client.player.getId() ? this.profileDraw.process("Себя") : this.profileDraw.process("Игроки");
      }
   }

   private static boolean process(PlayerEntity var0) {
      return var0 == null || var0.isRemoved() || !var0.isAlive() || var0.getHealth() <= 0.0F || var0.deathTime > 0;
   }

   private float handle(DeadEffect.Mode var1) {
      return this.process(var1) + var1.data;
   }

   private float process(DeadEffect.Mode var1) {
      return Math.max(0.15F, this.vectorPerform.compute() * var1.instance);
   }

   private int compute(DeadEffect.Mode var1) {
      return var1 == DeadEffect.Mode.TOTEM ? this.moduleCollect.prepare() : this.providerClose.prepare();
   }

   private static DeadEffect refresh() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         DeadEffect var0 = WildClient.instance.data.handle(DeadEffect.class);
         return var0 != null && var0.enabled ? var0 : null;
      } else {
         return null;
      }
   }

   private static Identifier compute(PlayerEntity var0) {
      return var0 instanceof AbstractClientPlayerEntity var1 ? var1.getSkinTextures().texture() : null;
   }

   private static boolean resolve(PlayerEntity var0) {
      return var0 instanceof AbstractClientPlayerEntity var1 && var1.getSkinTextures().model() == Model.SLIM;
   }

   private static boolean handle(PlayerEntityRenderState var0) {
      return var0 != null && var0.skinTextures != null && var0.skinTextures.model() == Model.SLIM;
   }

   private static int handle(int var0, float var1) {
      return ColorHelper.getArgb(resolve(Math.round(process(var1) * 255.0F)), handle(var0), process(var0), compute(var0));
   }

   private static int handle(int var0, int var1, float var2) {
      float var3 = process(var2);
      int var4 = Math.round(handle(var0) + (handle(var1) - handle(var0)) * var3);
      int var5 = Math.round(process(var0) + (process(var1) - process(var0)) * var3);
      int var6 = Math.round(compute(var0) + (compute(var1) - compute(var0)) * var3);
      return var4 << 16 | var5 << 8 | var6;
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

   private static int resolve(int var0) {
      return Math.max(0, Math.min(255, var0));
   }

   private static float handle(float var0) {
      float var1 = 1.0F - process(var0);
      return 1.0F - var1 * var1 * var1;
   }

   private static float handle(float var0, float var1, float var2) {
      float var3 = MathHelper.clamp((var2 - var0) / Math.max(1.0E-4F, var1 - var0), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private static float process(float var0) {
      return !Float.isFinite(var0) ? 0.0F : MathHelper.clamp(var0, 0.0F, 1.0F);
   }

   private static float compute(float var0) {
      return var0 - (float)Math.floor(var0);
   }

   static final class CacheEntry {
      final DeadEffect.Mode instance;
      final long data;
      final float context;
      DeadEffect.DataRecord config;
      DeadEffect.PrimaryPoint3d state;
      Identifier cache;
      boolean output;

      CacheEntry(DeadEffect.Mode var1, long var2, float var4, DeadEffect.DataRecord var5, DeadEffect.PrimaryPoint3d var6, Identifier var7, boolean var8) {
         this.instance = var1;
         this.data = var2;
         this.context = var4;
         this.config = var5;
         this.state = var6;
         this.cache = var7;
         this.output = var8;
      }
   }

   record DataRecord(
      long capturedNanos,
      DeadEffect.Point3d head,
      DeadEffect.Point3d hat,
      DeadEffect.Point3d body,
      DeadEffect.Point3d rightArm,
      DeadEffect.Point3d leftArm,
      DeadEffect.Point3d rightLeg,
      DeadEffect.Point3d leftLeg,
      DeadEffect.Point3d leftSleeve,
      DeadEffect.Point3d rightSleeve,
      DeadEffect.Point3d leftPants,
      DeadEffect.Point3d rightPants,
      DeadEffect.Point3d jacket
   ) {

      static DeadEffect.DataRecord capture(PlayerEntityModel var0, long var1) {
         return new DeadEffect.DataRecord(
            var1,
            DeadEffect.Point3d.capture(var0.head),
            DeadEffect.Point3d.capture(var0.hat),
            DeadEffect.Point3d.capture(var0.body),
            DeadEffect.Point3d.capture(var0.rightArm),
            DeadEffect.Point3d.capture(var0.leftArm),
            DeadEffect.Point3d.capture(var0.rightLeg),
            DeadEffect.Point3d.capture(var0.leftLeg),
            DeadEffect.Point3d.capture(var0.leftSleeve),
            DeadEffect.Point3d.capture(var0.rightSleeve),
            DeadEffect.Point3d.capture(var0.leftPants),
            DeadEffect.Point3d.capture(var0.rightPants),
            DeadEffect.Point3d.capture(var0.jacket)
         );
      }

      void apply(PlayerEntityModel var1) {
         this.head.apply(var1.head);
         this.hat.apply(var1.hat);
         this.body.apply(var1.body);
         this.rightArm.apply(var1.rightArm);
         this.leftArm.apply(var1.leftArm);
         this.rightLeg.apply(var1.rightLeg);
         this.leftLeg.apply(var1.leftLeg);
         this.leftSleeve.apply(var1.leftSleeve);
         this.rightSleeve.apply(var1.rightSleeve);
         this.leftPants.apply(var1.leftPants);
         this.rightPants.apply(var1.rightPants);
         this.jacket.apply(var1.jacket);
      }
   }

   enum Mode {
      TOTEM(0.9F, 0.58F, 0.92F, 0.035F, 0.82F, 0.44F, 0.42F, 0.66F, 0.2F, 0.58F, 1.18F, 0.22F, 1.1F, 6815716, 16777215, 16773226, 3669974, 0),
      DEATH(1.22F, 0.78F, 1.26F, 0.085F, 0.72F, 0.6F, 0.52F, 0.58F, 0.34F, 0.44F, 1.55F, 0.1F, 0.82F, 8693759, 15922687, 16739278, 6484991, 1);

      final float instance;
      final float data;
      final float context;
      final float config;
      final float state;
      final float cache;
      final float output;
      final float current;
      final float active;
      final float mode;
      final float selection;
      final float enabled;
      final float renderer;
      final int handler;
      final int animationDraw;
      final int pointEncode;
      final int animator;
      final int source;

      Mode(
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
         int var16,
         int var17,
         int var18,
         int var19,
         int var20
      ) {
         this.instance = var3;
         this.data = var4;
         this.context = var5;
         this.config = var6;
         this.state = var7;
         this.cache = var8;
         this.output = var9;
         this.current = var10;
         this.active = var11;
         this.mode = var12;
         this.selection = var13;
         this.enabled = var14;
         this.renderer = var15;
         this.handler = var16;
         this.animationDraw = var17;
         this.pointEncode = var18;
         this.animator = var19;
         this.source = var20;
      }

      ParticleEffect handle(int var1) {
         int var2 = (var1 + this.source) % 5;
         if (this == TOTEM) {
            return switch (var2) {
               case 0 -> ParticleTypes.TOTEM_OF_UNDYING;
               case 1 -> ParticleTypes.END_ROD;
               case 2 -> ParticleTypes.GLOW;
               case 3 -> ParticleTypes.ELECTRIC_SPARK;
               default -> ParticleTypes.WITCH;
            };
         } else {
            return switch (var2) {
               case 0 -> ParticleTypes.SOUL;
               case 1 -> ParticleTypes.SOUL_FIRE_FLAME;
               case 2 -> ParticleTypes.REVERSE_PORTAL;
               case 3 -> ParticleTypes.WHITE_ASH;
               default -> ParticleTypes.SCULK_SOUL;
            };
         }
      }
   }

   record Point3d(
      float originX,
      float originY,
      float originZ,
      float pitch,
      float yaw,
      float roll,
      float xScale,
      float yScale,
      float zScale,
      boolean visible,
      boolean hidden
   ) {
      static DeadEffect.Point3d capture(ModelPart var0) {
         return new DeadEffect.Point3d(
            var0.originX, var0.originY, var0.originZ, var0.pitch, var0.yaw, var0.roll, var0.xScale, var0.yScale, var0.zScale, var0.visible, var0.hidden
         );
      }

      void apply(ModelPart var1) {
         var1.originX = this.originX;
         var1.originY = this.originY;
         var1.originZ = this.originZ;
         var1.pitch = this.pitch;
         var1.yaw = this.yaw;
         var1.roll = this.roll;
         var1.xScale = this.xScale;
         var1.yScale = this.yScale;
         var1.zScale = this.zScale;
         var1.visible = this.visible;
         var1.hidden = this.hidden;
      }
   }

   record PrimaryPoint3d(double x, double y, double z, float bodyYaw, float baseScale) {

      static DeadEffect.PrimaryPoint3d fromPlayer(PlayerEntity var0) {
         float var1 = Module.client.getRenderTickCounter().getTickProgress(true);
         double var2 = MathHelper.lerp(var1, var0.lastX, var0.getX());
         double var4 = MathHelper.lerp(var1, var0.lastY, var0.getY());
         double var6 = MathHelper.lerp(var1, var0.lastZ, var0.getZ());
         float var8 = MathHelper.lerpAngleDegrees(var1, var0.lastBodyYaw, var0.bodyYaw);
         return new DeadEffect.PrimaryPoint3d(var2, var4, var6, var8, Math.max(0.01F, var0.getScale()));
      }

      static DeadEffect.PrimaryPoint3d fromState(PlayerEntityRenderState var0) {
         return new DeadEffect.PrimaryPoint3d(var0.x, var0.y, var0.z, var0.bodyYaw, Math.max(0.01F, var0.baseScale));
      }
   }
}
