package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.RotationAngles;

public final class AdaptiveJitterAimPattern implements MinecraftContext {
   private static final long instance = 520L;
   private static float data;
   private static float context;
   private static float config;
   private static float state;
   private static float cache;
   private static Vec3d output;
   private static Vec3d current;
   private static int active;
   private static boolean mode;
   private static long renderer;
   private static int handler;
   private static int animationDraw = ThreadLocalRandom.current().nextInt(7, 15);
   private static boolean pointEncode;
   private static long animator;
   private static int source = Integer.MIN_VALUE;
   private static boolean target;
   private static long pending;

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null && toggleState.world != null && var0 != null) {
         target = false;
         long var1 = System.currentTimeMillis();
         process(var0);
         output = var0.getPos()
            .add(Math.sin(var1 / 900.0) * 0.2F, var0.getHeight() / 2.0F + var0.getHeight() / 2.5F * Math.sin(var1 / 700.0), Math.cos(var1 / 700.0) * 0.12F);
         current = toggleState.player.getEyePos();
         Vec3d var3 = output.subtract(current).normalize();
         float var4 = (float)Math.toDegrees(Math.atan2(-var3.x, var3.z));
         float var5 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var3.y, Math.hypot(var3.x, var3.z))), -90.0, 90.0);
         float var6 = handle(3.0, 11.0, 90.0);
         float var7 = process(3.0, 11.0, 100.0);
         state = var6;
         cache = var7;
         float[] var8 = AttackAura.process(var0);
         float[] var9 = new float[]{var8[0], var8[1], var8[0] + var8[1]};
         boolean var10 = AdaptiveAttackTiming.handle(var0, false, true, true, -200L, var9);
         if (var10) {
            data = 1.0F;
         }

         if (pointEncode) {
            if (var1 > animator) {
               pointEncode = false;
            } else {
               context = toggleState.player.getYaw() - (ThreadLocalRandom.current().nextBoolean() ? -handle(20.0F, 40.0F) : handle(20.0F, 30.0F));
               config = handle(85.0F, 90.0F);
            }
         }

         if (data != 0.0F) {
            if (!pointEncode) {
               context = var4;
               config = var5;
            }

            data--;
         }

         if (mode && var1 >= renderer) {
            mode = false;
         }

         RotationController.handle(
            new RotationAngles(context + var6, MathHelper.clamp(config + var7, -90.0F, 90.0F)),
            handle(40.124813F, 55.41284F),
            pointEncode ? 360.0F : handle(4.412848F, 12.412894F),
            handle(40.124813F, 140.41284F),
            handle(40.124813F, 140.41284F),
            0,
            1,
            false
         );
      }
   }

   public static void handle() {
      mode = true;
      renderer = System.currentTimeMillis() + 150L;
      active = (active + 1) % 2;
      handler++;
      if (handler >= animationDraw) {
         handler = 0;
         animationDraw = ThreadLocalRandom.current().nextInt(4, 6);
         pointEncode = true;
         animator = System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(60, 110);
      }
   }

   public static void process() {
      if (toggleState.player != null && (update() || target)) {
         if (!target) {
            target = true;
            pending = System.currentTimeMillis();
         }

         resolve();
         handle(false);
      } else {
         handle(true);
      }
   }

   public static void compute() {
      if (target) {
         if (toggleState.player == null) {
            handle(true);
         } else {
            resolve();
         }
      }
   }

   private static void resolve() {
      long var0 = System.currentTimeMillis();
      float var2 = MathHelper.clamp((float)(var0 - pending) / 520.0F, 0.0F, 1.0F);
      float var3 = 1.0F - var2;
      float var4 = var3 * var3;
      float var5 = handle(3.0, 11.0, 90.0) * var4;
      float var6 = process(3.0, 11.0, 100.0) * var4;
      float var7 = handle(5.0F, 42.0F, var4);
      float var8 = handle(3.0F, 18.0F, var4);
      float var9 = handle(4.0F, 32.0F, var4);
      RotationController.handle(
         new RotationAngles(ViewRotationCoordinator.context + var5, MathHelper.clamp(ViewRotationCoordinator.config + var6, -90.0F, 90.0F)),
         var7,
         var8,
         var9,
         var9,
         0,
         1,
         false
      );
      if (var2 >= 1.0F) {
         handle(true);
      }
   }

   private static void handle(boolean var0) {
      data = 0.0F;
      state = 0.0F;
      cache = 0.0F;
      output = null;
      current = null;
      active = 0;
      mode = false;
      renderer = 0L;
      handler = 0;
      animationDraw = ThreadLocalRandom.current().nextInt(7, 15);
      pointEncode = false;
      animator = 0L;
      source = Integer.MIN_VALUE;
      if (var0) {
         target = false;
         pending = 0L;
         if (toggleState.player != null) {
            context = toggleState.player.getYaw();
            config = toggleState.player.getPitch();
         }
      }
   }

   private static void process(LivingEntity var0) {
      if (source != var0.getId()) {
         source = var0.getId();
         data = 0.0F;
         pointEncode = false;
         context = toggleState.player.getYaw();
         config = toggleState.player.getPitch();
      }
   }

   private static boolean update() {
      return source != Integer.MIN_VALUE || output != null || current != null || data != 0.0F || mode || pointEncode || state != 0.0F || cache != 0.0F;
   }

   private static float handle(float var0, float var1) {
      return ThreadLocalRandom.current().nextFloat(var0, var1);
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static float handle(double var0, double var2, double var4) {
      return (float)(Math.sin(System.currentTimeMillis() / var4) * handle((float)var0, (float)var2));
   }

   private static float process(double var0, double var2, double var4) {
      return (float)(Math.cos(System.currentTimeMillis() / var4) * handle((float)var0, (float)var2));
   }
}
