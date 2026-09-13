package ru.wild.util.player;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.Mutable;
import ru.wild.network.MovementPacketTracker;
import ru.wild.network.ServerTickRateTracker;

public final class MovementEnvironmentProbe {
   private static final float instance = 0.9001F;
   private static final float data = 1.0E-6F;
   private static final double context = 0.015;
   private static final long config = 260L;
   private static final long state = 70L;
   private static volatile boolean cache = true;
   private static volatile boolean output = false;
   private static volatile boolean current = false;
   private static volatile boolean active = false;
   private static volatile long mode = 0L;
   private static volatile long selection = 0L;
   private static volatile long enabled = 0L;
   private static volatile long renderer = 0L;
   private static volatile long handler = 0L;
   private static volatile MovementProbeState animationDraw = MovementProbeState.UNAVAILABLE;

   private MovementEnvironmentProbe() {
   }

   public static void handle() {
      cache = true;
      fetch();
   }

   public static void process() {
      handle(resolve() == MovementProbeState.READY);
   }

   public static void handle(boolean var0) {
      long var1 = System.currentTimeMillis();
      if (var0) {
         animationDraw = MovementProbeState.READY;
         handler = var1;
         unload();
      } else {
         animationDraw = MovementProbeState.UNAVAILABLE;
         renderer = var1;
      }
   }

   public static boolean compute() {
      return check() && resolve() == MovementProbeState.WAITING;
   }

   public static MovementProbeState resolve() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         ClientPlayerEntity var1 = var0.player;
         if (process(var1, var0)) {
            unload();
            return MovementProbeState.UNAVAILABLE;
         }

         if (!handle(var1, var0)) {
            return MovementProbeState.UNAVAILABLE;
         }

         boolean var2 = MovementPacketTracker.process();
         boolean var3 = var1.isOnGround();
         float var4 = MovementPacketTracker.handle();
         double var5 = MovementPacketTracker.update();
         boolean var7 = !var2 && (var4 > 1.0E-6F || var5 < -1.0E-6 || MovementPacketTracker.execute());
         if (var7) {
            if (compute(var1)) {
               return MovementProbeState.WAITING;
            } else {
               return handle(var1) ? MovementProbeState.READY : MovementProbeState.WAITING;
            }
         } else if (!var2 || !var3) {
            return MovementProbeState.WAITING;
         } else if (!compute(var1, var0)) {
            measure();
            return MovementProbeState.UNAVAILABLE;
         } else if (blendMatrix()) {
            measure();
            return MovementProbeState.UNAVAILABLE;
         } else {
            return MovementProbeState.WAITING;
         }
      } else {
         return MovementProbeState.UNAVAILABLE;
      }
   }

   public static boolean update() {
      return resolve() == MovementProbeState.READY;
   }

   public static boolean apply() {
      return !execute();
   }

   public static boolean execute() {
      return prepare();
   }

   public static boolean prepare() {
      return !check() ? false : resolve() == MovementProbeState.WAITING;
   }

   public static boolean check() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         return handle(var0.player, var0);
      }

      unload();
      return false;
   }

   private static boolean handle(ClientPlayerEntity var0, MinecraftClient var1) {
      if (!cache) {
         fetch();
         return false;
      }

      if (process(var0, var1)) {
         unload();
         return false;
      }

      long var2 = System.currentTimeMillis();
      boolean var4 = var1.options.jumpKey.isPressed();
      boolean var5 = var0.isOnGround();
      boolean var6 = MovementPacketTracker.process();
      boolean var7 = !var5 || !var6;
      if (!var4) {
         active = false;
      }

      if (active) {
         return false;
      }

      if (var4) {
         if (!output) {
            output = true;
            current = false;
            mode = var2;
            selection = var2;
            enabled = 0L;
         }

         return true;
      } else {
         if (output && var7) {
            current = true;
            selection = var2;
            enabled = var2 + 70L;
            return true;
         }

         if (output && var5 && var6) {
            if (!current) {
               if (var2 - mode <= 260L) {
                  return true;
               }

               measure();
               return false;
            } else {
               if (var2 <= enabled) {
                  return true;
               }

               unload();
               return false;
            }
         } else {
            if (output && var2 - selection <= 260L) {
               return true;
            }

            unload();
            return false;
         }
      }
   }

   public static boolean onTick() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         ClientPlayerEntity var1 = var0.player;
         if (!handle(var1, var0)) {
            return false;
         }

         if (process(var1, var0)) {
            return false;
         }

         MovementProbeState var2 = resolve();
         return var2 == MovementProbeState.UNAVAILABLE ? false : var1.isSprinting() || MovementPacketTracker.compute();
      } else {
         return false;
      }
   }

   public static boolean select() {
      return onTick();
   }

   public static boolean refresh() {
      return false;
   }

   public static boolean render() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         ClientPlayerEntity var1 = var0.player;
         if (!cache) {
            return false;
         } else if (process(var1, var0)) {
            return false;
         } else {
            return compute(var1) ? false : var0.options.jumpKey.isPressed() && MovementPacketTracker.process() && var1.isOnGround() && compute(var1, var0);
         }
      } else {
         return false;
      }
   }

   public static boolean tick() {
      return check() && resolve() == MovementProbeState.WAITING && !MovementPacketTracker.process();
   }

   public static boolean drawAnimation() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0.player != null && var0.world != null ? !var0.player.isOnGround() || !MovementPacketTracker.process() : false;
   }

   public static void process(boolean var0) {
      cache = var0;
      if (!var0) {
         fetch();
      }
   }

   public static boolean encodePoint() {
      return cache;
   }

   public static long animate() {
      return renderer;
   }

   public static long load() {
      return handler;
   }

   public static MovementProbeState save() {
      return animationDraw;
   }

   public static float submit() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         ClientPlayerEntity var1 = var0.player;
         float var2 = handle(var1, 0.0F);
         float var3 = process(var1);
         float var4 = matchVector();
         return var2 >= var4 ? 0.0F : Math.max(0.0F, (var4 - var2) * var3);
      } else {
         return Float.POSITIVE_INFINITY;
      }
   }

   public static void unload() {
      output = false;
      current = false;
      mode = 0L;
      selection = 0L;
      enabled = 0L;
   }

   public static void fetch() {
      unload();
      active = false;
   }

   private static void measure() {
      unload();
      active = true;
   }

   private static boolean blendMatrix() {
      if (!output) {
         return true;
      } else {
         return current ? false : System.currentTimeMillis() - mode > 260L;
      }
   }

   private static boolean handle(ClientPlayerEntity var0) {
      return handle(var0, 0.0F) >= matchVector();
   }

   private static float handle(ClientPlayerEntity var0, float var1) {
      float var2 = Math.max(0.0F, var1);
      double var3 = ServerTickRateTracker.handle();
      if (var3 > 0.0 && var3 < 19.95) {
         var2 *= (float)(var3 / 20.0);
      }

      return var0.getAttackCooldownProgress(0.5F + var2);
   }

   private static float matchVector() {
      double var0 = ServerTickRateTracker.handle();
      return !(var0 <= 0.0) && !(var0 >= 19.95) ? MathHelper.clamp(0.9001F * (20.0F / (float)var0), 0.9001F, 0.995F) : 0.9001F;
   }

   private static float process(ClientPlayerEntity var0) {
      double var1 = var0.getAttributeValue(EntityAttributes.ATTACK_SPEED);
      return !(var1 <= 0.0) && !Double.isNaN(var1) && !Double.isInfinite(var1) ? (float)(20.0 / var1) : 20.0F;
   }

   private static boolean process(ClientPlayerEntity var0, MinecraftClient var1) {
      if (var0.isSpectator()) {
         return true;
      } else if (var0.isTouchingWater()) {
         return true;
      } else if (var0.isInLava()) {
         return true;
      } else if (var0.isSubmergedIn(FluidTags.WATER)) {
         return true;
      } else if (var0.isSubmergedIn(FluidTags.LAVA)) {
         return true;
      } else if (var0.isSwimming()) {
         return true;
      } else if (var0.isClimbing()) {
         return true;
      } else if (resolve(var0, var1)) {
         return true;
      } else if (var0.hasStatusEffect(StatusEffects.BLINDNESS)) {
         return true;
      } else if (var0.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
         return true;
      } else if (var0.hasStatusEffect(StatusEffects.LEVITATION)) {
         return true;
      } else if (var0.hasVehicle()) {
         return true;
      } else {
         return var0.getAbilities().flying ? true : var0.isGliding();
      }
   }

   private static boolean compute(ClientPlayerEntity var0) {
      return var0.isSprinting() || MovementPacketTracker.compute();
   }

   private static boolean compute(ClientPlayerEntity var0, MinecraftClient var1) {
      if (var1.world == null) {
         return false;
      }

      Box var2 = var0.getBoundingBox().offset(0.0, 0.015, 0.0).contract(1.0E-7);
      return var1.world.isSpaceEmpty(var0, var2);
   }

   private static boolean resolve(ClientPlayerEntity var0, MinecraftClient var1) {
      if (var1.world == null) {
         return false;
      }

      Box var2 = var0.getBoundingBox().contract(1.0E-7);
      int var3 = MathHelper.floor(var2.minX);
      int var4 = MathHelper.floor(var2.maxX);
      int var5 = MathHelper.floor(var2.minY);
      int var6 = MathHelper.floor(var2.maxY);
      int var7 = MathHelper.floor(var2.minZ);
      int var8 = MathHelper.floor(var2.maxZ);
      Mutable var9 = new Mutable();

      for (int var10 = var3; var10 <= var4; var10++) {
         for (int var11 = var5; var11 <= var6; var11++) {
            for (int var12 = var7; var12 <= var8; var12++) {
               var9.set(var10, var11, var12);
               if (var1.world.getBlockState(var9).isOf(Blocks.COBWEB)) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
