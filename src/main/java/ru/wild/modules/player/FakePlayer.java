package ru.wild.modules.player;

import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "FakePlayer", category = ModuleCategory.Player, description = "Создаёт локального WildBot для тренировки атак и тотемов")
public final class FakePlayer extends Module {
   private static final UUID source = UUID.nameUUIDFromBytes("WildClient:WildBot".getBytes(StandardCharsets.UTF_8));
   private static final int target = -1337;
   private static final int pending = 20;
   private static final float previous = 0.1F;
   private static final float latest = 0.5F;
   private static final float summary = 1.5F;
   private final ModeSetting matrixBlend = new ModeSetting(
      "Броня", "Копировать", "Копировать", "Без брони", "Кожаная", "Кольчужная", "Золотая", "Железная", "Алмазная", "Незеритовая"
   );
   private final BooleanSetting vectorMatch = new BooleanSetting("Снятие тотемов", true);
   private final ModeSetting itemProject = new ModeSetting("Поведение", "Манекен", "Манекен", "Подвижный");
   private final NumberSetting responseCompute = new NumberSetting("Активность", 1.0F, 0.3F, 1.5F, 0.05F, false)
      .handle(() -> !this.itemProject.process("Подвижный"));
   private final BooleanSetting providerFetch = new BooleanSetting("Прыжки", true).handle(() -> !this.itemProject.process("Подвижный"));
   private final BooleanSetting profileDraw = new BooleanSetting("Замахи", true).handle(() -> !this.itemProject.process("Подвижный"));
   private FakePlayer.FileEntry vectorPerform;
   private ClientWorld eventAttach;
   private int serverRead;
   private String positionAdvance;
   private double frameCheck;
   private double moduleCollect;
   private double providerClose;
   private int presetSave = 1;
   private int windowConvert;
   private double presetWrite = 3.0;
   private int colorMeasure;
   private int animationSchedule;
   private int rendererScan;
   private int sourceBuild;

   public FakePlayer() {
      this.handle(this.itemProject, this.responseCompute, this.providerFetch, this.profileDraw, this.matrixBlend, this.vectorMatch);
   }

   @Override
   public void handle() {
      this.render();
      super.handle();
   }

   @Override
   public void process() {
      this.animate();
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null || Module.client.world == null) {
         this.animate();
      } else if (this.vectorPerform != null && !this.vectorPerform.isRemoved() && this.eventAttach == Module.client.world) {
         if (!this.matrixBlend.compute().equals(this.positionAdvance)) {
            this.compute(this.vectorPerform);
         }

         this.encodePoint();
         if (this.serverRead > 0) {
            this.serverRead--;
         } else if (this.vectorPerform.getHealth() < this.vectorPerform.getMaxHealth()) {
            this.vectorPerform.heal(0.1F);
         }
      } else {
         this.render();
      }
   }

   void handle(FakePlayer.FileEntry var1) {
      if (var1 == this.vectorPerform && Module.client.player != null && Module.client.world != null) {
         if (!this.itemProject.process("Подвижный")) {
            this.frameCheck = 0.0;
            this.providerClose = 0.0;
            var1.setSprinting(false);
            if (var1.isOnGround()) {
               this.moduleCollect = 0.0;
               var1.setVelocity(Vec3d.ZERO);
            } else {
               this.moduleCollect = (this.moduleCollect - 0.08) * 0.98;
               var1.move(MovementType.SELF, new Vec3d(0.0, this.moduleCollect, 0.0));
               var1.setVelocity(0.0, var1.getY() - var1.lastY, 0.0);
            }
         } else {
            ThreadLocalRandom var2 = ThreadLocalRandom.current();
            double var3 = Module.client.player.getX() - var1.getX();
            double var5 = Module.client.player.getZ() - var1.getZ();
            double var7 = Math.hypot(var3, var5);
            if (!(var1.getY() < Module.client.player.getY() - 24.0) && !(var7 > 16.0)) {
               float var36 = this.responseCompute.compute();
               if (--this.windowConvert <= 0) {
                  this.presetSave = var2.nextBoolean() ? 1 : -1;
                  this.windowConvert = var2.nextInt(18, 60);
               }

               if (--this.colorMeasure <= 0) {
                  this.presetWrite = var2.nextDouble(1.6, 4.4);
                  this.colorMeasure = var2.nextInt(40, 110);
               }

               if (this.sourceBuild > 0) {
                  this.sourceBuild--;
               } else if (var2.nextFloat() < 0.005F) {
                  this.sourceBuild = var2.nextInt(6, 18);
               }

               double var10 = var7 < 1.0E-4 ? 0.0 : 1.0 / var7;
               double var12 = var3 * var10;
               double var14 = var5 * var10;
               double var16 = MathHelper.clamp((var7 - this.presetWrite) * 0.45, -1.0, 1.0);
               double var18 = var12 * var16 - var14 * this.presetSave * 0.9;
               double var20 = var14 * var16 + var12 * this.presetSave * 0.9;
               double var22 = Math.hypot(var18, var20);
               if (var22 > 1.0) {
                  var18 /= var22;
                  var20 /= var22;
               }

               double var24 = this.sourceBuild > 0 ? 0.0 : 0.26 * var36;
               double var26 = var1.isOnGround() ? 0.3 : 0.1;
               this.frameCheck = this.frameCheck + (var18 * var24 - this.frameCheck) * var26;
               this.providerClose = this.providerClose + (var20 * var24 - this.providerClose) * var26;
               if (this.animationSchedule > 0) {
                  this.animationSchedule--;
               }

               if (var1.isOnGround()) {
                  this.moduleCollect = -0.0784;
                  if (this.providerFetch.compute() && this.animationSchedule == 0 && (var1.horizontalCollision || var2.nextFloat() < 0.035F * var36)) {
                     this.moduleCollect = 0.42;
                     this.animationSchedule = var2.nextInt(25, 70);
                  }
               } else {
                  this.moduleCollect = (this.moduleCollect - 0.08) * 0.98;
               }

               var1.setSprinting(Math.hypot(this.frameCheck, this.providerClose) > 0.18);
               var1.move(MovementType.SELF, new Vec3d(this.frameCheck, this.moduleCollect, this.providerClose));
               var1.setVelocity(var1.getX() - var1.lastX, var1.getY() - var1.lastY, var1.getZ() - var1.lastZ);
               float var28 = (float)Math.toDegrees(Math.atan2(-(Module.client.player.getX() - var1.getX()), Module.client.player.getZ() - var1.getZ()));
               double var29 = Module.client.player.getX() - var1.getX();
               double var31 = Module.client.player.getEyeY() - var1.getEyeY();
               double var33 = Module.client.player.getZ() - var1.getZ();
               float var35 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var31, Math.hypot(var29, var33))), -60.0, 60.0);
               var1.headYaw = var1.headYaw + MathHelper.clamp(MathHelper.wrapDegrees(var28 - var1.headYaw), -30.0F, 30.0F);
               var1.setYaw(var1.headYaw);
               var1.setPitch(var1.getPitch() + MathHelper.clamp(var35 - var1.getPitch(), -15.0F, 15.0F));
               if (this.rendererScan > 0) {
                  this.rendererScan--;
               }

               if (this.profileDraw.compute() && this.rendererScan == 0 && var7 < 3.2 && var2.nextFloat() < 0.3F) {
                  var1.swingHand(Hand.MAIN_HAND);
                  this.rendererScan = var2.nextInt(11, 22);
               }
            } else {
               double var9 = var2.nextDouble(0.0, Math.PI * 2);
               var1.refreshPositionAndAngles(
                  Module.client.player.getX() + Math.cos(var9) * 3.0,
                  Module.client.player.getY(),
                  Module.client.player.getZ() + Math.sin(var9) * 3.0,
                  var1.getYaw(),
                  0.0F
               );
               this.frameCheck = 0.0;
               this.moduleCollect = 0.0;
               this.providerClose = 0.0;
            }
         }
      }
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.animate();
   }

   public static boolean handle(Entity var0) {
      FakePlayer var1 = refresh();
      return var1 != null && var1.compute(var0);
   }

   public static boolean process(Entity var0) {
      FakePlayer var1 = refresh();
      return var1 != null && var0 == var1.vectorPerform;
   }

   static FakePlayer refresh() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         FakePlayer var0 = WildClient.instance.data.handle(FakePlayer.class);
         return var0 != null && var0.enabled ? var0 : null;
      } else {
         return null;
      }
   }

   private void render() {
      this.animate();
      if (Module.client.player != null && Module.client.world != null) {
         GameProfile var1 = new GameProfile(source, "WildBot");
         var1.getProperties().putAll(Module.client.player.getGameProfile().getProperties());
         SkinTextures var2 = Module.client.player.getSkinTextures();
         FakePlayer.FileEntry var3 = new FakePlayer.FileEntry(Module.client.world, var1, var2);
         var3.setId(this.handle(Module.client.world));
         Vec3d var4 = Module.client.player.getRotationVec(1.0F);
         Vec3d var5 = new Vec3d(var4.x, 0.0, var4.z);
         if (var5.lengthSquared() < 1.0E-6) {
            var5 = new Vec3d(0.0, 0.0, 1.0);
         } else {
            var5 = var5.normalize();
         }

         double var6 = Module.client.player.getX() + var5.x * 2.5;
         double var8 = Module.client.player.getY();
         double var10 = Module.client.player.getZ() + var5.z * 2.5;
         float var12 = Module.client.player.getYaw() + 180.0F;
         var3.refreshPositionAndAngles(var6, var8, var10, var12, 0.0F);
         var3.bodyYaw = var12;
         var3.headYaw = var12;
         var3.lastBodyYaw = var12;
         var3.lastHeadYaw = var12;
         var3.setOnGround(true);
         this.process(var3);
         var3.setHealth(var3.getMaxHealth());
         Module.client.world.addEntity(var3);
         this.vectorPerform = var3;
         this.eventAttach = Module.client.world;
         this.serverRead = 0;
         this.frameCheck = 0.0;
         this.moduleCollect = 0.0;
         this.providerClose = 0.0;
         this.windowConvert = 0;
         this.colorMeasure = 0;
         this.animationSchedule = 0;
         this.rendererScan = 0;
         this.sourceBuild = 0;
      }
   }

   private void process(FakePlayer.FileEntry var1) {
      this.compute(var1);
      var1.equipStack(EquipmentSlot.MAINHAND, Module.client.player.getMainHandStack().copy());
      var1.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
   }

   private void compute(FakePlayer.FileEntry var1) {
      switch (this.matrixBlend.compute()) {
         case "Без брони":
            this.handle(var1, null, null, null, null);
            break;
         case "Кожаная":
            this.handle(var1, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
            break;
         case "Кольчужная":
            this.handle(var1, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);
            break;
         case "Золотая":
            this.handle(var1, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);
            break;
         case "Железная":
            this.handle(var1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
            break;
         case "Алмазная":
            this.handle(var1, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
            break;
         case "Незеритовая":
            this.handle(var1, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);
            break;
         default:
            var1.equipStack(EquipmentSlot.HEAD, Module.client.player.getEquippedStack(EquipmentSlot.HEAD).copy());
            var1.equipStack(EquipmentSlot.CHEST, Module.client.player.getEquippedStack(EquipmentSlot.CHEST).copy());
            var1.equipStack(EquipmentSlot.LEGS, Module.client.player.getEquippedStack(EquipmentSlot.LEGS).copy());
            var1.equipStack(EquipmentSlot.FEET, Module.client.player.getEquippedStack(EquipmentSlot.FEET).copy());
      }

      this.positionAdvance = this.matrixBlend.compute();
   }

   private void handle(FakePlayer.FileEntry var1, Item var2, Item var3, Item var4, Item var5) {
      var1.equipStack(EquipmentSlot.HEAD, this.handle(var2));
      var1.equipStack(EquipmentSlot.CHEST, this.handle(var3));
      var1.equipStack(EquipmentSlot.LEGS, this.handle(var4));
      var1.equipStack(EquipmentSlot.FEET, this.handle(var5));
   }

   private ItemStack handle(Item var1) {
      return var1 == null ? ItemStack.EMPTY : new ItemStack(var1);
   }

   private boolean compute(Entity var1) {
      if (var1 == this.vectorPerform && this.vectorPerform != null && !this.vectorPerform.isRemoved() && Module.client.player != null && Module.client.world != null) {
         float var2 = Module.client.player.getAttackCooldownProgress(0.5F);
         boolean var3 = var2 > 0.9F
            && Module.client.player.fallDistance > 0.0
            && !Module.client.player.isOnGround()
            && !Module.client.player.isClimbing()
            && !Module.client.player.isTouchingWater()
            && !Module.client.player.hasStatusEffect(StatusEffects.BLINDNESS)
            && !Module.client.player.hasVehicle()
            && !Module.client.player.isSprinting();
         float var4 = 0.5F * (var3 ? 1.5F : 1.0F);
         this.serverRead = 20;
         Module.client.player.resetLastAttackedTicks();
         this.vectorPerform.animateDamage(Module.client.player.getYaw());
         this.handle(var3, var2);
         this.compute(var3);
         if (!this.vectorMatch.compute()) {
            this.vectorPerform.setHealth(this.vectorPerform.getMaxHealth());
            this.vectorPerform.setAbsorptionAmount(0.0F);
            this.serverRead = 0;
            return true;
         }

         float var5 = Math.max(0.0F, var4);
         float var6 = Math.min(this.vectorPerform.getAbsorptionAmount(), var5);
         if (var6 > 0.0F) {
            this.vectorPerform.setAbsorptionAmount(this.vectorPerform.getAbsorptionAmount() - var6);
            var5 -= var6;
         }

         float var7 = this.vectorPerform.getHealth() - var5;
         if (var7 <= 0.0F) {
            this.tick();
         } else {
            this.vectorPerform.setHealth(var7);
            Module.client.world
               .playSoundClient(
                  this.vectorPerform.getX(),
                  this.vectorPerform.getY(),
                  this.vectorPerform.getZ(),
                  SoundEvents.ENTITY_PLAYER_HURT,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F,
                  false
               );
         }

         return true;
      } else {
         return false;
      }
   }

   private void handle(boolean var1, float var2) {
      Module.client.world
         .playSoundClient(
            Module.client.player.getX(),
            Module.client.player.getY(),
            Module.client.player.getZ(),
            var1 ? SoundEvents.ENTITY_PLAYER_ATTACK_CRIT : (var2 > 0.9F ? SoundEvents.ENTITY_PLAYER_ATTACK_STRONG : SoundEvents.ENTITY_PLAYER_ATTACK_WEAK),
            SoundCategory.PLAYERS,
            1.0F,
            1.0F,
            false
         );
   }

   private void compute(boolean var1) {
      ThreadLocalRandom var2 = ThreadLocalRandom.current();
      int var3 = var1 ? 18 : 7;

      for (int var4 = 0; var4 < var3; var4++) {
         double var5 = this.vectorPerform.getX() + var2.nextDouble(-0.32, 0.32);
         double var7 = this.vectorPerform.getBodyY(var2.nextDouble(0.25, 0.85));
         double var9 = this.vectorPerform.getZ() + var2.nextDouble(-0.32, 0.32);
         double var11 = var2.nextDouble(-0.35, 0.35);
         double var13 = var2.nextDouble(0.05, 0.45);
         double var15 = var2.nextDouble(-0.35, 0.35);
         this.handle(ParticleTypes.CRIT, var5, var7, var9, var11, var13, var15);
      }

      for (int var17 = 0; var17 < 4; var17++) {
         this.handle(
            ParticleTypes.DAMAGE_INDICATOR,
            this.vectorPerform.getX() + var2.nextDouble(-0.2, 0.2),
            this.vectorPerform.getBodyY(var2.nextDouble(0.35, 0.75)),
            this.vectorPerform.getZ() + var2.nextDouble(-0.2, 0.2),
            var2.nextDouble(-0.08, 0.08),
            var2.nextDouble(0.05, 0.18),
            var2.nextDouble(-0.08, 0.08)
         );
      }

      if (Module.client.player.getMainHandStack().hasEnchantments()) {
         for (int var18 = 0; var18 < 12; var18++) {
            this.handle(
               ParticleTypes.ENCHANTED_HIT,
               this.vectorPerform.getX() + var2.nextDouble(-0.35, 0.35),
               this.vectorPerform.getBodyY(var2.nextDouble(0.2, 0.9)),
               this.vectorPerform.getZ() + var2.nextDouble(-0.35, 0.35),
               var2.nextDouble(-0.45, 0.45),
               var2.nextDouble(0.05, 0.5),
               var2.nextDouble(-0.45, 0.45)
            );
         }
      }
   }

   private void tick() {
      ItemStack var1 = new ItemStack(Items.TOTEM_OF_UNDYING);
      this.vectorPerform.setHealth(1.0F);
      this.vectorPerform.deathTime = 0;
      DeathProtectionComponent var2 = (DeathProtectionComponent)var1.get(DataComponentTypes.DEATH_PROTECTION);
      if (var2 != null) {
         var2.applyDeathEffects(var1, this.vectorPerform);
      }

      this.serverRead = 20;
      this.encodePoint();
      this.drawAnimation();
      Module.client.world
         .playSoundClient(
            this.vectorPerform.getX(),
            this.vectorPerform.getY(),
            this.vectorPerform.getZ(),
            SoundEvents.ITEM_TOTEM_USE,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F,
            false
         );
   }

   private void drawAnimation() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();

      for (int var2 = 0; var2 < 72; var2++) {
         double var3 = var1.nextDouble(0.0, Math.PI * 2);
         double var5 = var1.nextDouble(0.05, 0.48);
         double var7 = this.vectorPerform.getX() + Math.cos(var3) * var5;
         double var9 = this.vectorPerform.getBodyY(var1.nextDouble(0.05, 0.95));
         double var11 = this.vectorPerform.getZ() + Math.sin(var3) * var5;
         double var13 = var1.nextDouble(0.12, 0.65);
         double var15 = Math.cos(var3) * var13 + var1.nextDouble(-0.12, 0.12);
         double var17 = var1.nextDouble(0.15, 0.85);
         double var19 = Math.sin(var3) * var13 + var1.nextDouble(-0.12, 0.12);
         this.handle(ParticleTypes.TOTEM_OF_UNDYING, var7, var9, var11, var15, var17, var19);
      }
   }

   private void handle(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      Module.client.world.addParticleClient(var1, true, true, var2, var4, var6, var8, var10, var12);
   }

   private void encodePoint() {
      if (this.vectorPerform != null) {
         if (!this.vectorMatch.compute()) {
            if (!this.vectorPerform.getOffHandStack().isEmpty()) {
               this.vectorPerform.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            }

            this.vectorPerform.setHealth(this.vectorPerform.getMaxHealth());
            this.vectorPerform.setAbsorptionAmount(0.0F);
         } else {
            if (!this.vectorPerform.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
               this.vectorPerform.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
            }
         }
      }
   }

   private int handle(ClientWorld var1) {
      int var2 = -1337;

      while (var1.getEntityById(var2) != null) {
         var2--;
      }

      return var2;
   }

   private void animate() {
      if (this.vectorPerform != null && this.eventAttach != null) {
         this.eventAttach.removeEntity(this.vectorPerform.getId(), RemovalReason.DISCARDED);
      }

      this.vectorPerform = null;
      this.eventAttach = null;
      this.serverRead = 0;
      this.positionAdvance = null;
   }

   static final class FileEntry extends OtherClientPlayerEntity {
      private final SkinTextures instance;

      FileEntry(ClientWorld var1, GameProfile var2, SkinTextures var3) {
         super(var1, var2);
         this.instance = var3;
      }

      public void tick() {
         FakePlayer var1 = FakePlayer.refresh();
         if (var1 != null) {
            var1.handle(this);
         }

         super.tick();
      }

      public SkinTextures getSkinTextures() {
         return this.instance != null ? this.instance : super.getSkinTextures();
      }
   }
}
