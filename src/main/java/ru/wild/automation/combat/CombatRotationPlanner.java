package ru.wild.automation.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.CombatRaycast;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

public class CombatRotationPlanner implements MinecraftContext {
   private static final int instance = 15;
   private static final float data = 20.0F;
   private static final float context = 50.0F;
   private static final float config = 70.0F;
   private static final float state = 120.0F;
   private static final String[] cache = new String[]{"Human Track", "Wave Drift", "Pulse Jerk", "Overstep", "Anchor Hold"};
   private static final String[] output = new String[]{"Closest Box", "Upper Body", "Velocity Lead", "Side Sweep", "Sticky Point"};
   private static int current = -1;
   private static int active = -1;
   private static int mode = -1;
   private static int renderer;
   private static int handler;
   private static int animationDraw;
   private static int pointEncode;
   private static int animator;
   private static int source = 5;
   private static boolean target;
   private static float pending;
   private static float previous;
   private static float latest;
   private static float summary;
   private static float matrixBlend;
   private static float vectorMatch;
   private static float itemProject;
   private static float responseCompute;
   private static int providerFetch = 1;
   private static float profileDraw;
   private static float vectorPerform;
   private static float eventAttach;
   private static float serverRead;
   private static float positionAdvance;
   private static int frameCheck = 1;

   public static void handle(LivingEntity var0) {
      if (toggleState.player == null) {
         resolve();
      } else if (toggleState.world != null && var0 != null) {
         if (current != var0.getId()) {
            resolve();
            current = var0.getId();
            process();
            compute();
         }

         if (active < 0 || renderer >= animationDraw) {
            process();
         }

         if (mode < 0 || handler >= pointEncode) {
            compute();
         }

         renderer++;
         handler++;
         animator++;
         Vec3d var1 = process(var0).subtract(toggleState.player.getEyePos());
         float var2 = (float)Math.toDegrees(Math.atan2(-var1.x, var1.z));
         float var3 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var1.y, Math.hypot(var1.x, var1.z))), -90.0, 90.0);
         RotationAngles var4 = new RotationAngles(toggleState.player);
         float var5 = MathHelper.wrapDegrees(var2 - var4.instance);
         float var6 = var3 - var4.data;
         float var7 = AttackAura.handle(var0) + AttackAura.target.compute();
         EntityHitResult var8 = CombatRaycast.process(var4.instance, var4.data, var7, var0, false);
         boolean var9 = var8 != null && var8.getEntity() == var0;
         float var12 = renderer + active * 17.0F;
         float var10;
         float var11;
         switch (active) {
            case 0:
               var10 = handle(20.0F, 50.0F, ClientMathUtil.compute(24.0F, 35.0F) * (var9 ? 0.92F : 1.14F));
               var11 = handle(70.0F, 120.0F, ClientMathUtil.compute(76.0F, 96.0F) * (var9 ? 0.96F : 1.1F));
               var5 = var5 * (var9 ? 0.36F : 0.66F)
                  + matrixBlend * (var9 ? 0.55F : 0.95F)
                  + process(var12, 14.0F, 0.42F)
                  + ClientMathUtil.compute(-0.18F, 0.18F);
               var6 = var6 * (var9 ? 0.4F : 0.7F) + vectorMatch * (var9 ? 0.45F : 0.85F) + process(var12, 17.0F, 0.24F);
               break;
            case 1:
               var10 = handle(20.0F, 50.0F, ClientMathUtil.compute(24.0F, 40.0F) * (var9 ? 0.9F : 1.18F));
               var11 = handle(70.0F, 120.0F, ClientMathUtil.compute(90.0F, 118.0F) * (var9 ? 0.9F : 1.0F));
               var5 = var5 * (var9 ? 0.3F : 0.62F) + process(var12, 3.5F, 3.1F) + process(var12, 10.5F, 1.7F) + matrixBlend;
               var6 = var6 * (var9 ? 0.56F : 0.86F) + process(var12, 4.8F, 2.4F) + vectorMatch * 0.7F;
               break;
            case 2:
               var10 = handle(20.0F, 50.0F, ClientMathUtil.compute(38.0F, 50.0F));
               var11 = handle(70.0F, 120.0F, ClientMathUtil.compute(72.0F, 96.0F));
               handle(0.42F, 0.66F, 5.6F, 1.6F);
               handle(4, 4.1F, 1.0F);
               var5 = var5 * (var9 ? 0.28F : 0.84F) + pending + itemProject;
               var6 = var6 * (var9 ? 0.38F : 0.72F) + previous + responseCompute;
               break;
            case 3:
               var10 = handle(20.0F, 50.0F, ClientMathUtil.compute(30.0F, 46.0F) * (var9 ? 0.92F : 1.12F));
               var11 = handle(70.0F, 120.0F, ClientMathUtil.compute(98.0F, 120.0F) * (var9 ? 0.94F : 1.0F));
               var5 = var5 * (var9 ? 0.54F : 0.88F) + process(var5, 8.0F, 2.2F, 5.6F) - process(var12, 6.5F, 1.1F);
               var6 = var6 * (var9 ? 0.32F : 0.66F) + process(var6, 5.0F, 1.0F, 2.9F) + process(var12, 6.8F, 0.55F);
               break;
            case 4:
            default:
               var10 = handle(20.0F, 50.0F, var9 ? ClientMathUtil.compute(20.0F, 29.0F) : ClientMathUtil.compute(34.0F, 49.0F));
               var11 = handle(70.0F, 120.0F, var9 ? ClientMathUtil.compute(92.0F, 115.0F) : ClientMathUtil.compute(78.0F, 100.0F));
               handle(var9);
               var5 = var5 * (var9 ? 0.2F : 0.78F) + latest + compute(var12, 18.0F, 1.15F);
               var6 = var6 * (var9 ? 0.58F : 0.54F) + summary - compute(var12, 15.0F, 0.7F);
         }

         var5 = handle(var5, profileDraw, var9, true, var12);
         var6 = handle(var6, vectorPerform, var9, false, var12);
         profileDraw = var5;
         vectorPerform = var6;
         float var13 = MathHelper.clamp(var5, -var10, var10);
         float var14 = MathHelper.clamp(var6, -var11, var11);
         if (!var9) {
            var13 = resolve(var13, var5, 2.2F);
            var14 = resolve(var14, var6, 1.8F);
         }

         if (var9 && Math.abs(var13) < 0.18F) {
            var13 = 0.0F;
         }

         if (var9 && Math.abs(var14) < 0.12F) {
            var14 = 0.0F;
         }

         target = RotationController.cache <= 15;
         RotationController.handle(
            new RotationAngles(var4.instance + var13, MathHelper.clamp(var4.data + var14, -90.0F, 90.0F)), var10, var11, 30.0F, 30.0F, 2, 15, false
         );
      } else {
         handle();
      }
   }

   public static void handle() {
      if (!target) {
         resolve();
      } else {
         if (toggleState.player != null) {
            ViewRotationCoordinator.context = toggleState.player.getYaw();
            ViewRotationCoordinator.config = toggleState.player.getPitch();
         }

         RotationController.instance = RotationController.Mode.IDLE;
         RotationController.cache = 0;
         RotationController.mode = false;
         RotationController.active = null;
         RotationController.current = 0;
         ViewRotationCoordinator.instance = ViewRotationCoordinator.data;
         resolve();
      }
   }

   private static void process() {
      int var0 = active;

      do {
         active = ClientMathUtil.handle(0, 4);
      } while (active == var0 && var0 >= 0);

      renderer = 0;

      animationDraw = switch (active) {
         case 0 -> ClientMathUtil.handle(78, 128);
         case 1 -> ClientMathUtil.handle(62, 104);
         case 2 -> ClientMathUtil.handle(48, 78);
         case 3 -> ClientMathUtil.handle(56, 92);
         default -> ClientMathUtil.handle(74, 122);
      };
      animator = 0;
      source = active == 4 ? ClientMathUtil.handle(6, 13) : ClientMathUtil.handle(2, 7);
      pending = 0.0F;
      previous = 0.0F;
      latest = ClientMathUtil.compute(-1.2F, 1.2F);
      summary = ClientMathUtil.compute(-0.75F, 0.75F);
      matrixBlend = ClientMathUtil.compute(-1.15F, 1.15F);
      vectorMatch = ClientMathUtil.compute(-0.65F, 0.65F);
      itemProject = 0.0F;
      responseCompute = 0.0F;
      providerFetch = ClientMathUtil.handle(0, 1) == 0 ? -1 : 1;
      ChatLogger.handle("[LonyGrief] rotate -> " + cache[active]);
   }

   private static void compute() {
      int var0 = mode;

      do {
         mode = ClientMathUtil.handle(0, 4);
      } while (mode == var0 && var0 >= 0);

      handler = 0;

      pointEncode = switch (mode) {
         case 0 -> ClientMathUtil.handle(90, 150);
         case 1 -> ClientMathUtil.handle(80, 136);
         case 2 -> ClientMathUtil.handle(58, 108);
         case 3 -> ClientMathUtil.handle(68, 118);
         default -> ClientMathUtil.handle(96, 168);
      };
      eventAttach = ClientMathUtil.compute(0.34F, 0.66F);
      serverRead = ClientMathUtil.compute(0.38F, 0.78F);
      positionAdvance = ClientMathUtil.compute(0.34F, 0.66F);
      frameCheck = ClientMathUtil.handle(0, 1) == 0 ? -1 : 1;
      ChatLogger.handle("[LonyGrief] vector -> " + output[mode]);
   }

   private static void handle(float var0, float var1, float var2, float var3) {
      if (animator >= source) {
         pending = ClientMathUtil.compute(-var2, var2);
         previous = ClientMathUtil.compute(-var3, var3);
         animator = 0;
         source = ClientMathUtil.handle(3, 8);
      } else {
         pending *= var0;
         previous *= var1;
      }
   }

   private static void handle(boolean var0) {
      if (animator >= source) {
         float var1 = var0 ? 2.4F : 3.8F;
         float var2 = var0 ? 1.6F : 2.6F;
         latest = ClientMathUtil.compute(-var1, var1);
         summary = ClientMathUtil.compute(-var2, var2);
         animator = 0;
         source = ClientMathUtil.handle(5, 11);
      }
   }

   private static void handle(int var0, float var1, float var2) {
      if (renderer % var0 == 0) {
         providerFetch = -providerFetch;
         itemProject = var1 * providerFetch;
         responseCompute = ClientMathUtil.compute(-var2, var2);
      } else {
         itemProject *= 0.5F;
         responseCompute *= 0.64F;
      }
   }

   private static Vec3d process(LivingEntity var0) {
      Box var1 = var0.getBoundingBox();
      float var2 = handler + mode * 13.0F;

      return switch (mode) {
         case 0 -> PlayerRaycast.handle(var1, false).add(process(var2, 18.0F, 0.025F), process(var2, 21.0F, 0.035F), process(var2, 20.0F, 0.025F));
         case 1 -> handle(var1, 0.5F + process(var2, 24.0F, 0.13F), 0.72F + process(var2, 31.0F, 0.08F), 0.5F + process(var2, 27.0F, 0.13F));
         case 2 -> handle(var1, 0.5F + process(var2, 30.0F, 0.08F), 0.52F + process(var2, 25.0F, 0.1F), 0.5F + process(var2, 34.0F, 0.08F))
            .add(var0.getVelocity().multiply(ClientMathUtil.compute(1.1F, 2.4F)));
         case 3 -> handle(var0, var1, var2);
         default -> handle(
            var1, eventAttach + process(var2, 36.0F, 0.035F), serverRead + process(var2, 29.0F, 0.045F), positionAdvance + process(var2, 33.0F, 0.035F)
         );
      };
   }

   private static Vec3d handle(LivingEntity var0, Box var1, float var2) {
      Vec3d var3 = handle(var1, 0.5F, 0.55F + process(var2, 28.0F, 0.12F), 0.5F);
      Vec3d var4 = toggleState.player.getPos().subtract(var0.getPos());
      Vec3d var5 = new Vec3d(-var4.z, 0.0, var4.x);
      if (var5.lengthSquared() < 1.0E-4) {
         var5 = new Vec3d(1.0, 0.0, 0.0);
      } else {
         var5 = var5.normalize();
      }

      double var6 = Math.max(var0.getWidth() * 0.38, 0.12);
      double var8 = compute(var2, 42.0F, 1.0F) * var6 * frameCheck;
      return var3.add(var5.multiply(var8));
   }

   private static Vec3d handle(Box var0, float var1, float var2, float var3) {
      float var4 = MathHelper.clamp(var1, 0.08F, 0.92F);
      float var5 = MathHelper.clamp(var2, 0.12F, 0.92F);
      float var6 = MathHelper.clamp(var3, 0.08F, 0.92F);
      return new Vec3d(handle(var0.minX, var0.maxX, var4), handle(var0.minY, var0.maxY, var5), handle(var0.minZ, var0.maxZ, var6));
   }

   private static double handle(double var0, double var2, float var4) {
      return var0 + (var2 - var0) * var4;
   }

   private static float handle(float var0, float var1, boolean var2, boolean var3, float var4) {
      float var5 = Math.abs(var0);
      float var6 = var3 ? 55.0F : 42.0F;
      float var7 = MathHelper.clamp((float)Math.pow(MathHelper.clamp(var5 / var6, 0.0F, 1.0F), 0.72), 0.22F, 1.0F);
      float var8 = var0 * var7;
      float var9 = var3 ? (var2 ? 0.46F : 0.68F) : (var2 ? 0.52F : 0.72F);
      float var10 = var3
         ? process(var4, 19.0F, var2 ? 0.22F : 0.48F) + ClientMathUtil.compute(-0.08F, 0.08F)
         : process(var4, 23.0F, var2 ? 0.16F : 0.34F) + ClientMathUtil.compute(-0.05F, 0.05F);
      return ClientMathUtil.process(var1, var8, var9) + var10;
   }

   private static float handle(float var0, float var1, float var2) {
      return MathHelper.clamp(var2, var0, var1);
   }

   private static float process(float var0, float var1, float var2) {
      return (float)Math.sin(var0 / var1) * var2;
   }

   private static float compute(float var0, float var1, float var2) {
      float var3 = var0 % var1;
      return (var3 / var1 * 2.0F - 1.0F) * var2;
   }

   private static float process(float var0, float var1, float var2, float var3) {
      return Math.abs(var0) <= var1 ? 0.0F : handle(var0) * ClientMathUtil.compute(var2, var3);
   }

   private static float resolve(float var0, float var1, float var2) {
      return !(Math.abs(var1) <= var2) && !(Math.abs(var0) >= var2) ? handle(var1) * var2 : var0;
   }

   private static float handle(float var0) {
      return var0 < 0.0F ? -1.0F : 1.0F;
   }

   private static void resolve() {
      current = -1;
      active = -1;
      mode = -1;
      renderer = 0;
      handler = 0;
      animationDraw = 0;
      pointEncode = 0;
      animator = 0;
      source = 5;
      pending = 0.0F;
      previous = 0.0F;
      latest = 0.0F;
      summary = 0.0F;
      matrixBlend = 0.0F;
      vectorMatch = 0.0F;
      itemProject = 0.0F;
      responseCompute = 0.0F;
      providerFetch = 1;
      profileDraw = 0.0F;
      vectorPerform = 0.0F;
      eventAttach = 0.5F;
      serverRead = 0.55F;
      positionAdvance = 0.5F;
      frameCheck = 1;
      target = false;
   }
}
