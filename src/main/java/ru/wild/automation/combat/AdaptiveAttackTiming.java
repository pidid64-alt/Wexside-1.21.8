package ru.wild.automation.combat;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.network.MovementPacketTracker;
import ru.wild.network.ServerTickRateTracker;
import ru.wild.util.math.ResettableTimer;
import ru.wild.util.player.CombatRaycast;
import ru.wild.util.player.EntityAimGeometry;

public final class AdaptiveAttackTiming implements MinecraftContext {
   private static final SecureRandom config = new SecureRandom();
   static final MinecraftClient state = MinecraftClient.getInstance();
   public static long instance;
   public static int data;
   public static int context;
   private static final ResettableTimer cache = new ResettableTimer();
   private static boolean output;
   private static int current;

   public static void handle() {
      instance++;
   }

   public static void process() {
      instance = 0L;
   }

   public static boolean compute() {
      return instance % 7L == 3L;
   }

   public static PlayerEntity resolve() {
      return state.player;
   }

   public static World update() {
      return state.world;
   }

   public static float handle(float var0) {
      float var1 = 0.2F;
      return (float)(var0 + (config.nextGaussian() * 0.2F * 2.0 - 0.2F));
   }

   public static boolean handle(int var0) {
      return config.nextInt(var0 + 1) >= 1.0F * (1.0F / Math.max(var0, 1.0F));
   }

   public static boolean apply() {
      return config.nextInt(2) == 1;
   }

   public static float handle(float var0, float var1) {
      return config.nextFloat(var0, var1);
   }

   public static float execute() {
      return handle(0.0F, 1.0F);
   }

   public static int prepare() {
      return apply() ? 1 : -1;
   }

   public static int check() {
      if (state.player == null) {
         return -1;
      }

      for (int var0 = 0; var0 < 9; var0++) {
         if (state.player.getInventory().getStack(var0).getItem() instanceof AxeItem) {
            return var0;
         }
      }

      return -1;
   }

   public static Runnable[] handle(LivingEntity var0, boolean var1) {
      Runnable[] var2 = new Runnable[]{() -> {}, () -> {}};
      if (var1 && state.player != null) {
         if (var0 instanceof PlayerEntity var3) {
            if (!var3.isBlocking()) {
               return var2;
            }

            ItemStack var4 = var3.getMainHandStack();
            ItemStack var5 = var3.getOffHandStack();
            Item var6 = var4.isEmpty() ? null : var4.getItem();
            Item var7 = var5.isEmpty() ? null : var5.getItem();
            if (var6 == Items.SHIELD || var7 == Items.SHIELD) {
               int var9 = state.player.getInventory().getSelectedSlot();
               int var8;
               if ((var8 = check()) != -1 && var8 != var9) {
                  var2[0] = () -> {
                     if (state.getNetworkHandler() != null) {
                        state.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var8));
                     }
                  };
                  var2[1] = () -> {
                     if (state.getNetworkHandler() != null) {
                        state.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var9));
                     }
                  };
               }
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public static Runnable[] handle(boolean var0) {
      Runnable[] var1 = new Runnable[]{() -> {}, () -> {}};
      if (var0 && state.player != null) {
         if (state.player.isBlocking()) {
            Hand var2 = state.player.getActiveHand();
            if (var2 == null) {
               return var1;
            }

            var1[0] = () -> state.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            var1[1] = () -> state.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(var2, 0, state.player.getYaw(), state.player.getPitch()));
         }

         return var1;
      } else {
         return var1;
      }
   }

   public static Runnable[] process(boolean var0) {
      Runnable[] var1 = new Runnable[]{() -> {}, () -> {}};
      if (var0 && state.player != null) {
         if (state.player.isSprinting() && !state.player.isOnGround() && !state.player.isSubmergedIn(FluidTags.WATER)) {
            var1[0] = () -> {
               state.player.setSprinting(false);
               state.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(state.player, Mode.STOP_SPRINTING));
            };
            var1[1] = () -> {
               state.player.setSprinting(true);
               state.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(state.player, Mode.START_SPRINTING));
            };
         }

         return var1;
      } else {
         return var1;
      }
   }

   public static boolean compute(boolean var0) {
      if (!var0 || state.player == null) {
         return true;
      }

      if (state.player.isSwimming()) {
         return true;
      }

      if (!(state.player.fallDistance > 0.0)) {
         data = 2;
      }

      if (data > 0) {
         data--;
      }

      if (data == 0 && state.player.fallDistance > 0.0) {
         return true;
      }

      boolean var1 = state.world.getBlockState(state.player.getBlockPos()).isOf(Blocks.COBWEB);
      boolean var2 = !state.player.isJumping() && (state.player.isTouchingWater() || state.player.isInLava())
         || state.player.isSubmergedIn(FluidTags.WATER)
         || state.player.isSubmergedIn(FluidTags.LAVA)
         || var1;
      return var2
         || !state.player.isJumping() && state.player.age > 6 && AttackAura.animationSchedule.process("Умные криты")
         || state.player.isClimbing()
         || state.player.hasVehicle()
         || state.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || state.player.hasStatusEffect(StatusEffects.LEVITATION)
         || state.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
         || state.player.getAbilities().flying;
   }

   public static boolean handle(LivingEntity var0, Runnable var1, Runnable var2, Hand var3, boolean var4) {
      return handle(var0, var1, var2, var3, var4, null, false);
   }

   public static boolean handle(LivingEntity var0, Runnable var1, Runnable var2, Hand var3, boolean var4, Runnable var5) {
      return AdaptiveAttackTiming.Task.handle(var0, var1, var2, var3, var4, var5);
   }

   public static void onTick() {
      AdaptiveAttackTiming.Task.handle();
   }

   public static void select() {
      AdaptiveAttackTiming.Task.process();
   }

   public static boolean handle(LivingEntity var0) {
      return AdaptiveAttackTiming.Task.handle(var0);
   }

   static boolean handle(LivingEntity var0, Runnable var1, Runnable var2, Hand var3, boolean var4, Runnable var5, boolean var6) {
      if (var6 && !process(var0)) {
         return false;
      }

      if (var1 != null) {
         var1.run();
      }

      boolean var7 = !var6 || process(var0);
      if (var7 && var0 != null && state.interactionManager != null && state.player != null) {
         state.interactionManager.attackEntity(state.player, var0);
         if (var3 != null) {
            state.player.swingHand(var3);
         }

         if (var4) {
            handle();
         } else {
            process();
         }

         cache.handle();
         context++;
      }

      if (var2 != null) {
         var2.run();
      }

      if (var7 && var0 != null && var5 != null) {
         var5.run();
      }

      return var7 && var0 != null;
   }

   private static boolean process(LivingEntity var0) {
      return var0 != null
         && var0 == AttackAura.textureRun
         && var0.isAlive()
         && !var0.isRemoved()
         && state.player != null
         && state.player.isAlive()
         && state.world != null
         && state.interactionManager != null;
   }

   public static long refresh() {
      if (state.player == null) {
         return 500L;
      }

      double var0 = state.player.getAttributeValue(EntityAttributes.ATTACK_SPEED);
      float var2 = 0.2F;
      long var3 = (long)(1.0 / var0 * 1000.0 * (1.0F - var2));
      if (AttackAura.presetSave.process("Динамичный")) {
         var3 += ThreadLocalRandom.current().nextLong(40L, 60L);
      } else {
         var3 += 30L;
      }

      return Math.max(var3, 400L);
   }

   public static boolean handle(long var0) {
      return cache.handle((double)(refresh() + var0));
   }

   public static boolean render() {
      return handle(0L);
   }

   public static boolean process(long var0) {
      return cache.handle((double)var0);
   }

   public static float tick() {
      return Math.min((float)cache.process() / (float)refresh(), 1.0F);
   }

   public static float drawAnimation() {
      return (float)cache.process();
   }

   public static boolean handle(LivingEntity var0, double var1) {
      return var0 != null && CombatRaycast.handle(MathHelper.wrapDegrees(state.player.getYaw()), state.player.getPitch(), (float)var1, var0);
   }

   public static boolean handle(LivingEntity var0, double var1, boolean var3) {
      return var0 != null && state.player != null && MovementPacketTracker.resolve()
         ? CombatRaycast.compute(
            MathHelper.wrapDegrees(MovementPacketTracker.handle(state.player.getYaw())),
            MovementPacketTracker.process(state.player.getPitch()),
            var1,
            var0,
            var3
         )
         : false;
   }

   public static boolean handle(LivingEntity var0, boolean var1, boolean var2, boolean var3, long var4, float[] var6) {
      if (var2 && var0 != null && !EntityAimGeometry.handle(var0, var6[0], true)) {
         return false;
      }

      if (!handle(var4)) {
         return false;
      }

      boolean var7 = compute(var3);
      if (var7 && var1 && !handle(var0, var6[0])) {
         var7 = false;
      }

      return var7;
   }

   public static boolean handle(LivingEntity var0, boolean var1, boolean var2, long var3, float[] var5) {
      return handle(var0, var1, true, var2, var3, var5);
   }

   public static boolean handle(LivingEntity var0, float[] var1) {
      return var0 != null
         && handle(var0, false, false, -80L, var1)
         && !state.player.isOnGround()
         && !state.player.isSubmergedIn(FluidTags.WATER)
         && state.player.getVelocity().y <= 0.0030162615090425808;
   }

   public static boolean process(LivingEntity var0, float[] var1) {
      return var0 != null
         && handle(var0, false, false, -60L, var1)
         && !state.player.isOnGround()
         && !state.player.isSubmergedIn(FluidTags.WATER)
         && state.player.getVelocity().y <= 0.16477328182606651;
   }

   private static int load() {
      return 3;
   }

   public static void encodePoint() {
      output = false;
      current = 0;
   }

   public static void handle(LivingEntity var0, boolean var1, boolean var2, boolean var3) {
      if (var0 == null || current == 0 || !var3 || var0.hurtTime != 0) {
         encodePoint();
      }

      if (var3 && var0 != null && process(compute() ? 250L : 150L) && state.player.handSwinging) {
         if (!output && current == 0 && var0.hurtTime == 0) {
            output = true;
            current = load();
         }

         if (output && current > 0 && (!var2 || handle(var0, 6.0)) && handle(var0, () -> {}, () -> {}, Hand.MAIN_HAND, var1)) {
            current--;
         }
      }
   }
   private AdaptiveAttackTiming() {
   }
   public static ResettableTimer animate() {
      return cache;
   }

   static final class Task {
      private static final double instance = 45.0;
      private static final double data = 49.2;
      private static final double context = 4.5;
      private static final long config = 250000L;
      private static final long state = 120000L;
      private static final Object cache = new Object();
      private static final Thread output = new Thread(AdaptiveAttackTiming.Task::compute, "Wild Adaptive Tick Edge");
      private static volatile boolean current;
      private static volatile boolean active;
      private static volatile long mode;
      private static volatile LivingEntity selection;
      private static volatile Runnable enabled;
      private static volatile Runnable renderer;
      private static volatile Runnable handler;
      private static volatile Hand animationDraw;
      private static volatile boolean pointEncode;

      private Task() {
      }

      static void handle() {
         if (!current) {
            synchronized (cache) {
               if (!current) {
                  current = true;
                  output.start();
               }
            }
         }
      }

      static boolean handle(LivingEntity var0, Runnable var1, Runnable var2, Hand var3, boolean var4, Runnable var5) {
         handle();
         long var6 = System.nanoTime();
         if (!ServerTickRateTracker.process(var6)) {
            return AdaptiveAttackTiming.handle(var0, var1, var2, var3, var4, var5, false);
         }

         if (active && selection == var0) {
            return false;
         }

         double var8 = ServerTickRateTracker.resolve(var6);
         double var10 = ServerTickRateTracker.compute(var6);
         if (!(var10 <= 4.5) && (!(var8 >= 45.0) || !(var8 <= 49.2))) {
            long var12 = ServerTickRateTracker.handle(var6, 45.0);
            if (var12 <= 250000L) {
               return AdaptiveAttackTiming.handle(var0, var1, var2, var3, var4, var5, true);
            }

            handle(var6 + var12, var0, var1, var2, var3, var4, var5);
            return false;
         } else {
            return AdaptiveAttackTiming.handle(var0, var1, var2, var3, var4, var5, true);
         }
      }

      static boolean handle(LivingEntity var0) {
         return active && selection == var0;
      }

      private static void handle(long var0, LivingEntity var2, Runnable var3, Runnable var4, Hand var5, boolean var6, Runnable var7) {
         synchronized (cache) {
            selection = var2;
            enabled = var3;
            renderer = var4;
            animationDraw = var5;
            pointEncode = var6;
            handler = var7;
            mode = var0;
            active = true;
         }

         LockSupport.unpark(output);
      }

      static void process() {
         synchronized (cache) {
            active = false;
            selection = null;
            enabled = null;
            renderer = null;
            handler = null;
            animationDraw = null;
            pointEncode = false;
            mode = 0L;
         }
      }

      private static void compute() {
         while (true) {
            if (!active) {
               LockSupport.park();
            } else {
               long var0 = mode;
               long var2 = var0 - System.nanoTime();
               if (var2 > 120000L) {
                  LockSupport.parkNanos(var2 - 120000L);
               } else {
                  long var4;
                  while ((var4 = System.nanoTime()) < var0 && active && mode == var0) {
                     Thread.onSpinWait();
                  }

                  if (var4 >= var0) {
                     handle(var0);
                  }
               }
            }
         }
      }

      private static void handle(long var0) {
         LivingEntity var2;
         Runnable var3;
         Runnable var4;
         Runnable var5;
         Hand var6;
         boolean var7;
         synchronized (cache) {
            if (!active || mode != var0) {
               return;
            }

            var2 = selection;
            var3 = enabled;
            var4 = renderer;
            var6 = animationDraw;
            var7 = pointEncode;
            var5 = handler;
            active = false;
            selection = null;
            enabled = null;
            renderer = null;
            handler = null;
            animationDraw = null;
            pointEncode = false;
            mode = 0L;
         }

         AdaptiveAttackTiming.state.execute(() -> {
            try {
               AdaptiveAttackTiming.handle(var2, var3, var4, var6, var7, var5, true);
            } catch (Throwable var7x) {
            }
         });
      }

      static {
         output.setDaemon(true);
      }
   }
}
