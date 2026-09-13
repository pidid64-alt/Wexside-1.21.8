package ru.wild.automation.combat;

import java.security.SecureRandom;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.network.MovementPacketTracker;
import ru.wild.network.ServerTickRateTracker;

public final class SmartCritsHelper implements MinecraftContext {
   private static final long instance = 624L;
   private static final float data = 10.0F;
   private static final float context = 8.0F;
   private static final float config = 13.0F;
   private static long state = System.currentTimeMillis();
   private static long cache;
   private static int output;
   private static int current = -1;
   private static int active;

   private SmartCritsHelper() {
   }

   public static void handle() {
      cache = System.currentTimeMillis();
      current = -1;
   }

   public static void process() {
      current = -1;
   }

   public static void compute() {
      output++;
   }

   public static void resolve() {
      if (active-- <= 0) {
         state = System.currentTimeMillis() + 175L;
         active = new SecureRandom().nextInt(6, 7);
      } else {
         state = System.currentTimeMillis();
      }
   }

   public static void update() {
      if (prepare() > 800L) {
         active = 7;
      }

      state = System.currentTimeMillis();
   }

   public static boolean handle(LivingEntity var0, int var1) {
      if (toggleState.player == null || toggleState.world == null || toggleState.options == null || var0 == null || !var0.isAlive()) {
         return false;
      }

      if (!handle(var1)) {
         return false;
      }

      if (!toggleState.player.isGliding() && !toggleState.player.getAbilities().flying) {
         SmartCritsHelper.DataRecord var2 = process(var1);
         if (handle(var2)) {
            return true;
         } else {
            boolean var3 = resolve(var2);
            boolean var4 = AttackAura.animationSchedule.process("Умные криты");
            boolean var5 = compute(var2);
            boolean var6 = check();
            if (handle(var2, var4, var5)) {
               return false;
            } else if (var6) {
               return true;
            } else if (var1 <= 0) {
               return apply(var2);
            } else {
               return var3 ? update(var2) : handle(var2, var1);
            }
         }
      } else {
         return true;
      }
   }

   public static boolean handle(LivingEntity var0) {
      return toggleState.player != null && toggleState.player.isSprinting() && !toggleState.player.isSwimming() && !toggleState.player.isGliding()
         ? handle(var0, 1)
         : false;
   }

   public static boolean process(LivingEntity var0) {
      return toggleState.player == null || !toggleState.player.isSprinting() || toggleState.player.isSwimming() || toggleState.player.isGliding();
   }

   public static boolean handle(boolean var0) {
      if (!var0 || toggleState.player == null || toggleState.interactionManager == null) {
         return true;
      } else if (current == output) {
         return false;
      } else if (toggleState.player.isUsingItem() && toggleState.player.getActiveItem().getItem() == Items.SHIELD) {
         toggleState.interactionManager.stopUsingItem(toggleState.player);
         toggleState.player.stopUsingItem();
         current = output;
         return false;
      } else {
         return true;
      }
   }

   private static boolean handle(int var0) {
      float var1 = Math.max(0.0F, var0);
      float var2 = (float)Math.max(1.0, ServerTickRateTracker.handle());
      long var3 = Math.max(0L, Math.round(Math.max(0.0F, apply() - var1) * 50.0F * (20.0F / var2)));
      return prepare() >= var3;
   }

   private static float apply() {
      double var0 = execute();
      return MathHelper.clamp((float)(10.0 * (1.0 - var0)), 8.0F, 13.0F);
   }

   private static double execute() {
      if (toggleState.player == null) {
         return 0.0;
      }

      double var0 = 0.0;
      double var2 = 1.0;

      for (EquipmentSlot var7 : EquipmentSlot.values()) {
         ItemStack var8 = toggleState.player.getEquippedStack(var7);
         if (!var8.isEmpty()) {
            AttributeModifiersComponent var9 = (AttributeModifiersComponent)var8.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            if (var9 != null) {
               for (Entry var11 : var9.modifiers()) {
                  if (var11.attribute() == EntityAttributes.ATTACK_SPEED && var11.slot().matches(var7)) {
                     EntityAttributeModifier var12 = var11.modifier();
                     if (var12.operation() == Operation.ADD_MULTIPLIED_BASE) {
                        var0 += var12.value();
                     } else if (var12.operation() == Operation.ADD_MULTIPLIED_TOTAL) {
                        var2 *= 1.0 + var12.value();
                     }
                  }
               }
            }
         }
      }

      return (1.0 + var0) * var2 - 1.0;
   }

   private static long prepare() {
      return System.currentTimeMillis() - state;
   }

   private static SmartCritsHelper.DataRecord process(int var0) {
      ClientPlayerEntity var1 = toggleState.player;
      boolean var2 = MovementPacketTracker.select();
      boolean var3 = var2 ? MovementPacketTracker.process() : var1.isOnGround();
      float var4 = var2 ? MovementPacketTracker.handle() : (float)var1.fallDistance;
      double var5 = var2 ? MovementPacketTracker.update() : var1.getVelocity().y;
      double var7 = Math.max(0.0, var1.getAttributeValue(EntityAttributes.GRAVITY));
      Box var9 = var1.getBoundingBox();

      for (int var10 = 0; var10 < Math.max(0, var0); var10++) {
         if (var3 && toggleState.options.jumpKey.isPressed()) {
            var3 = false;
            var5 = 0.42;
         } else if (!var3) {
            var5 = (var5 - var7) * 0.98;
            if (var5 < 0.0) {
               var4 += (float)(-var5);
            }
         }

         var9 = var9.offset(0.0, var5, 0.0);
      }

      return new SmartCritsHelper.DataRecord(var3, var4, var5, var9, var1.horizontalCollision);
   }

   private static boolean handle(SmartCritsHelper.DataRecord var0) {
      return toggleState.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || toggleState.player.hasStatusEffect(StatusEffects.LEVITATION)
         || handle(var0.box)
         || toggleState.player.isSubmergedIn(FluidTags.WATER)
         || toggleState.player.isInLava()
         || toggleState.player.isClimbing()
         || toggleState.player.getAbilities().flying;
   }

   private static boolean process(SmartCritsHelper.DataRecord var0) {
      return toggleState.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || toggleState.player.hasStatusEffect(StatusEffects.LEVITATION)
         || toggleState.player.isSubmergedIn(FluidTags.WATER)
         || toggleState.player.isInLava()
         || toggleState.player.isClimbing()
         || toggleState.player.isSwimming()
         || toggleState.player.isGliding()
         || toggleState.player.getAbilities().flying;
   }

   private static boolean check() {
      return toggleState.player.hasStatusEffect(StatusEffects.BLINDNESS) || toggleState.player.hasStatusEffect(StatusEffects.LEVITATION);
   }

   private static boolean compute(SmartCritsHelper.DataRecord var0) {
      return toggleState.options.jumpKey.isPressed() || !var0.onGround && var0.velocityY > 0.08;
   }

   private static boolean handle(SmartCritsHelper.DataRecord var0, boolean var1, boolean var2) {
      if (cache <= 0L || System.currentTimeMillis() - cache > 624L) {
         return false;
      } else {
         return var1 && !var2 ? false : !var0.onGround && var0.fallDistance <= 0.0F && var0.velocityY > -0.03;
      }
   }

   private static boolean resolve(SmartCritsHelper.DataRecord var0) {
      return var0.horizontalCollision || prepare(var0) || check(var0) >= 2;
   }

   private static boolean update(SmartCritsHelper.DataRecord var0) {
      if (execute(var0)) {
         return true;
      }

      float var1 = handle(var0, true);
      double var2 = process(var0, true);
      if (var0.horizontalCollision || prepare(var0)) {
         var1 = Math.min(var1, 0.004F);
         var2 = Math.max(var2, -0.01);
      }

      return !var0.onGround && var0.fallDistance > var1 && var0.velocityY < var2;
   }

   private static boolean handle(SmartCritsHelper.DataRecord var0, int var1) {
      if (var0.onGround || process(var0)) {
         return false;
      }

      if (execute(var0)) {
         return true;
      }

      boolean var2 = var0.fallDistance > handle(var0, false);
      boolean var3 = var0.velocityY < process(var0, false);
      boolean var4 = var1 <= 0 || !var0.onGround;
      return var2 && var3 && var4;
   }

   private static boolean apply(SmartCritsHelper.DataRecord var0) {
      float var1 = handle(var0, false);
      double var2 = process(var0, false);
      if (toggleState.player.isOnGround()) {
         return false;
      } else if (!var0.onGround && !process(var0)) {
         return !((float)toggleState.player.fallDistance <= var1) && !(toggleState.player.getVelocity().y >= var2)
            ? !MovementPacketTracker.select() || MovementPacketTracker.handle() > var1 && MovementPacketTracker.update() < var2
            : false;
      } else {
         return false;
      }
   }

   private static boolean execute(SmartCritsHelper.DataRecord var0) {
      return onTick() && !var0.onGround && var0.fallDistance > 0.0F && var0.velocityY < -0.01;
   }

   private static boolean onTick() {
      return toggleState.player.hurtTime > 0 || toggleState.player.hasStatusEffect(StatusEffects.SLOWNESS);
   }

   private static float handle(SmartCritsHelper.DataRecord var0, boolean var1) {
      float var2 = var1 ? 0.01F : 0.03F;
      if (onTick()) {
         var2 = Math.min(var2, var1 ? 0.008F : 0.012F);
      }

      if (var0.horizontalCollision) {
         var2 = Math.min(var2, 0.012F);
      }

      return var2;
   }

   private static double process(SmartCritsHelper.DataRecord var0, boolean var1) {
      double var2 = var1 ? -0.02 : -0.03;
      if (onTick()) {
         var2 = Math.max(var2, var1 ? -0.012 : -0.018);
      }

      if (var0.horizontalCollision) {
         var2 = Math.max(var2, -0.015);
      }

      return var2;
   }

   private static boolean prepare(SmartCritsHelper.DataRecord var0) {
      return !toggleState.world.isSpaceEmpty(toggleState.player, var0.box.expand(0.22, 0.0, 0.22).contract(1.0E-7));
   }

   private static int check(SmartCritsHelper.DataRecord var0) {
      Vec3d var1 = var0.box.getCenter();
      double var2 = var0.box.minY + 0.1;
      double var4 = Math.min(var0.box.maxY - 0.1, var0.box.minY + 0.95);
      int var6 = 0;
      var6 += handle(var1.x + 0.72, var1.z, var2, var4) ? 1 : 0;
      var6 += handle(var1.x - 0.72, var1.z, var2, var4) ? 1 : 0;
      var6 += handle(var1.x, var1.z + 0.72, var2, var4) ? 1 : 0;
      return var6 + (handle(var1.x, var1.z - 0.72, var2, var4) ? 1 : 0);
   }

   private static boolean handle(double var0, double var2, double var4, double var6) {
      return handle(BlockPos.ofFloored(var0, var4, var2)) || handle(BlockPos.ofFloored(var0, var6, var2));
   }

   private static boolean handle(BlockPos var0) {
      return !toggleState.world.getBlockState(var0).isAir() && !toggleState.world.getBlockState(var0).getCollisionShape(toggleState.world, var0).isEmpty();
   }

   private static boolean handle(Box var0) {
      int var1 = (int)Math.floor(var0.minX);
      int var2 = (int)Math.floor(var0.maxX);
      int var3 = (int)Math.floor(var0.minY);
      int var4 = (int)Math.floor(var0.maxY);
      int var5 = (int)Math.floor(var0.minZ);
      int var6 = (int)Math.floor(var0.maxZ);
      Mutable var7 = new Mutable();

      for (int var8 = var1; var8 <= var2; var8++) {
         for (int var9 = var3; var9 <= var4; var9++) {
            for (int var10 = var5; var10 <= var6; var10++) {
               var7.set(var8, var9, var10);
               if (toggleState.world.getBlockState(var7).isOf(Blocks.COBWEB)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   record DataRecord(boolean onGround, float fallDistance, double velocityY, Box box, boolean horizontalCollision) {
   }
}
