package ru.wild.automation.combat;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.NumberInterpolator;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

public final class RotationEngine implements MinecraftContext {
   static int instance;
   static float data;
   static Stopwatch context = new Stopwatch();
   static Stopwatch config = new Stopwatch();

   public static void handle(LivingEntity var0, boolean var1, float var2, boolean var3) {
      long var4 = System.currentTimeMillis();
      if (!AttackAura.dataValidate && var4 - AttackAura.actionRead >= AttackAura.configCollapse) {
         AttackAura.dataValidate = true;
         AttackAura.scaleRender = var4;
         AttackAura.clientRefresh = ThreadLocalRandom.current().nextInt(270, 390);
         AttackAura.actionRead = var4;
         AttackAura.configCollapse = ThreadLocalRandom.current().nextLong(6500L, 7200L);
      }

      boolean var6 = false;
      if (AttackAura.dataValidate && var4 - AttackAura.scaleRender >= AttackAura.clientRefresh) {
         AttackAura.dataValidate = false;
      }

      if (var4 - AttackAura.scaleRender >= AttackAura.clientRefresh + 40L) {
         var6 = true;
      }

      Vec3d var7 = toggleState.player.getEyePos();
      float var8 = (float)Math.cos(System.currentTimeMillis() / 450.0);
      float var9 = 0.06F * var8;
      float var10 = (float)Math.cos(System.currentTimeMillis() / 500.0);
      float var11 = 0.06F * var10;
      float var12 = (float)Math.cos(System.currentTimeMillis() / 14000.0);
      float var13 = (float)Math.cos(System.currentTimeMillis() / 2500L);
      float var14 = 0.5F * var12;
      Vec3d var15 = EntityAimGeometry.process(var0);
      float var16 = ViewRotationCoordinator.context;
      if (var1 && EntityAimGeometry.process(var0).length() < var2 && !var3) {
         data = ClientMathUtil.resolve(6.0F, 7.0F);
      }

      float var17 = ClientMathUtil.resolve(22.0F, 28.0F);
      float var18 = 0.0F;
      float var19 = ClientMathUtil.resolve(0.0F, 3.5F);
      float var20 = (float)Math.cos(System.currentTimeMillis() / 40.0);
      float var21 = (float)Math.sin(System.currentTimeMillis() / 70.0);
      if (data > 0.0F) {
         var17 = ClientMathUtil.resolve(70.0F, 120.0F);
         var16 = (float)Math.toDegrees(Math.atan2(-var15.x, var15.z));
         var18 = (var20 + var21) * handle(1.0F, 2.0F);
         data--;
      }

      float var22 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var15.y, Math.hypot(var15.x, var15.z))), -90.0, 90.0);
      float var23 = var20 * handle(13.0F, 15.0F) + var18;
      float var24 = var21 * handle(5.0F, 7.0F) + var18;
      float var25 = var22;
      float var26 = AttackAura.dataValidate ? -ClientMathUtil.resolve(80.0F, 90.0F) : var25;
      RotationAngles var27 = new RotationAngles(var16 + var23, var26 + var24);
      RotationController.handle(
         var27, var17, AttackAura.dataValidate ? handle(120.0F, 170.0F) : (var6 ? handle(120.0F, 170.0F) : handle(6.0F, 8.0F)), 25.0F, 25.0F, 0, 15, false
      );
   }

   public static void handle(BlockPos var0, Direction var1) {
      Vec3d var2 = toggleState.player.getEyePos();
      double var3 = var0.getX() + 0.5 + var1.getOffsetX() * 0.5;
      double var5 = var0.getY() + 0.5 + var1.getOffsetY() * 0.5;
      double var7 = var0.getZ() + 0.5 + var1.getOffsetZ() * 0.5;
      if (var1.getAxis() != Axis.X) {
         var3 = MathHelper.clamp(var2.x, var0.getX() + 0.15, var0.getX() + 0.85);
      }

      if (var1.getAxis() != Axis.Y) {
         var5 = MathHelper.clamp(var2.y - 1.2, var0.getY() + 0.15, var0.getY() + 0.85);
      }

      if (var1.getAxis() != Axis.Z) {
         var7 = MathHelper.clamp(var2.z, var0.getZ() + 0.15, var0.getZ() + 0.85);
      }

      Vec3d var9 = new Vec3d(var3, var5, var7).subtract(var2);
      float var10 = (float)Math.toDegrees(Math.atan2(-var9.x, var9.z));
      float var11 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var9.y, Math.hypot(var9.x, var9.z))), -90.0, 90.0);
      RotationController.handle(
         new RotationAngles(var10, var11), ClientMathUtil.resolve(250.0F, 360.0F), ClientMathUtil.resolve(250.0F, 360.0F), 180.0F, 180.0F, 0, 5, false
      );
   }

   public static void handle(LivingEntity var0) {
      Vec3d var1 = toggleState.player.getEyePos();
      Vec3d var2 = var0.getPos().add(0.0, var0.getHeight() * 0.8, 0.0);
      Vec3d var3 = var2.subtract(var1).normalize();
      float var4 = (float)Math.toDegrees(Math.atan2(-var3.x, var3.z));
      float var5 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var3.y, Math.hypot(var3.x, var3.z))), -90.0, 90.0);
      double var6 = Math.max(0.0, toggleState.player.getY() - var0.getY());
      float var8;
      float var9;
      if (var6 > 2.5) {
         var8 = 15.0F;
         var9 = 10.0F;
      } else if (var6 > 1.0) {
         var8 = 45.0F;
         var9 = 35.0F;
      } else {
         var8 = 90.0F;
         var9 = 80.0F;
      }

      float var10 = ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F);
      RotationController.handle(new RotationAngles(var4 + var10, var5 + var10), var8, var9, 30.0F, 30.0F, 1, 15, false);
   }

   public static float handle(float var0, float var1) {
      return NumberInterpolator.handle(var1, var0, new SecureRandom().nextFloat());
   }

   public static void handle(LivingEntity var0, boolean var1) {
      Vec3d var2 = PlayerRaycast.handle(var0.getBoundingBox(), false);
      Vec3d var3 = var2.subtract(toggleState.player.getEyePos());
      float var4 = (float)Math.toDegrees(Math.atan2(-var3.x, var3.z));
      float var5 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var3.y, Math.hypot(var3.x, var3.z))), -89.0, 89.0);
      RotationAngles var6 = new RotationAngles(var4, var5);
      RotationController.handle(var6, ClientMathUtil.handle(120, 180), ClientMathUtil.resolve(30.0F, 63.0F), 30.0F, 30.0F, 1, 15, false);
   }

   public static void process(LivingEntity var0, boolean var1) {
      float var2 = 0.3F * (float)Math.cos(System.currentTimeMillis() / 2200.0);
      float var3 = 0.03F * (float)Math.sin(System.currentTimeMillis() / 900.0) + 0.06F * (float)Math.cos(System.currentTimeMillis() / 1200.0);
      float var4 = 0.2F * (float)Math.cos(System.currentTimeMillis() / 700.0) + 0.04F * (float)Math.sin(System.currentTimeMillis() / 900.0);
      Vec3d var5 = toggleState.player.getEyePos();
      Vec3d var6 = var0.getPos().add(var4, var0.getHeight() - 0.35F - var2, var3).subtract(var5).normalize();
      boolean var7 = false;
      if (var1) {
         instance = 4;
      }

      if (instance > 0) {
         var7 = true;
         instance--;
      }

      float var8 = (float)Math.toDegrees(Math.atan2(-var6.x, var6.z));
      float var9 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var6.y, Math.hypot(var6.x, var6.z))), -90.0, 90.0);
      float var10 = 0.0F;
      if (var7) {
         var10 = (float)(3.0 * Math.sin(System.currentTimeMillis() / 30.0))
            + (float)(ClientMathUtil.handle(3, 4) * Math.cos(System.currentTimeMillis() / 60.0));
      }

      RotationAngles var11 = new RotationAngles(
         var8 + var10 + ThreadLocalRandom.current().nextFloat(-2.0F, 2.0F), var9 + ThreadLocalRandom.current().nextFloat(-2.0F, 2.0F) + var10
      );
      RotationController.handle(
         var11, (float)ClientMathUtil.handle(50.0, 70.0, 70L, context), (float)ClientMathUtil.handle(10.0, 20.0, 65L, config), 30.0F, 30.0F, 1, 15, false
      );
   }

   public static void compute(LivingEntity var0, boolean var1) {
      Vec3d var2 = toggleState.player.getEyePos();
      Vec3d var3 = var0.getPos().add(0.0, var0.getHeight() / 2.0F, 0.0).subtract(var2).normalize();
      float var4 = (float)Math.toDegrees(Math.atan2(-var3.x, var3.z));
      float var5 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var3.y, Math.hypot(var3.x, var3.z))), -90.0, 90.0);
      float var6 = 180.0F;
      float var7 = 45.0F;
      RotationAngles var8 = new RotationAngles(
         var4 + ThreadLocalRandom.current().nextFloat(-2.0F, 2.0F), var5 + ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F)
      );
      RotationController.handle(var8, var7, var6, var7, var6, 0, 15, false);
   }

   public static void resolve(LivingEntity var0, boolean var1) {
      float var2 = 0.02F * (float)Math.sin(System.currentTimeMillis() / 1200.0);
      float var3 = 0.03F * (float)Math.sin(System.currentTimeMillis() / 900.0) + 0.02F * (float)Math.cos(System.currentTimeMillis() / 1200.0);
      float var4 = 0.4F * (float)Math.cos(System.currentTimeMillis() / 700L) + 0.04F * (float)Math.sin(System.currentTimeMillis() / 900.0);
      Vec3d var5 = EntityAimGeometry.compute(var0).add(var3, 0.0, var4);
      boolean var6 = false;
      if (var1) {
         instance = 2;
      }

      if (instance > 0) {
         var6 = true;
         instance--;
      }

      float var7 = (float)Math.toDegrees(Math.atan2(-var5.x, var5.z));
      float var8 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var5.y, Math.hypot(var5.x, var5.z))), -90.0, 90.0);
      float var9 = 0.0F;
      if (var6) {
         var9 = ClientMathUtil.compute(-3.0F, 4.0F) + (float)(2.0 * Math.sin(System.currentTimeMillis() / 30.0));
      }

      float var10 = ClientMathUtil.compute(-3.0F, 3.0F) + (float)(3.0 * Math.cos(System.currentTimeMillis() / 40.0));
      float var11 = ClientMathUtil.compute(-1.0F, 1.0F) + (float)(4.0 * Math.sin(System.currentTimeMillis() / 240.0));
      RotationAngles var12 = new RotationAngles(var7 + var10 + var9, var8 + var11);
      RotationController.handle(var12, ClientMathUtil.handle(38, 43), ClientMathUtil.resolve(3.0F, 5.0F), 30.0F, 30.0F, 1, 15, false);
   }

   public static void update(LivingEntity var0, boolean var1) {
   }

   public static void handle(LivingEntity var0, boolean var1, String var2) {
      float var3 = 0.25F * (float)Math.cos(System.currentTimeMillis() / 1500L);
      float var4 = 0.2F * (float)Math.cos(System.currentTimeMillis() / 700L);
      float var5 = 0.2F * (float)Math.cos(System.currentTimeMillis() / 900L);
      Vec3d var6 = toggleState.player.getEyePos();
      Vec3d var7 = var0.getPos().add(var5, MathHelper.clamp(var6.y - var0.getPos().y, 0.0, 0.8) - var3, var4).subtract(var6).normalize();
      if (var2.contains("Fast")) {
         float var8 = ViewRotationCoordinator.context;
         float var9 = ViewRotationCoordinator.config;
         float var10 = ClientMathUtil.compute(190.0F, 245.0F);
         if (var1) {
            var8 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
            var9 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
         }

         float var11 = 0.0F;
         float var12 = 0.0F;
         RotationController.handle(new RotationAngles(var8 + var11, var9 + var12), var10, var10, 40.0F, 40.0F, 1, 7, false);
      } else if (var2.contains("Smooth")) {
         float var13 = ViewRotationCoordinator.context;
         float var15 = ViewRotationCoordinator.config;
         float var17 = 24.0F;
         if (var1) {
            instance = 3;
            var17 = 88.0F;
         }

         if (instance > 0) {
            var13 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
            var15 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
            instance--;
         }

         float var19 = 0.0F;
         float var21 = 0.0F;
         RotationController.handle(new RotationAngles(var13 + var19, var15 + var21), var17, var17, 40.0F, 40.0F, 1, 7, false);
      } else if (var2.contains("Random")) {
         float var14 = ViewRotationCoordinator.context;
         float var16 = ViewRotationCoordinator.config;
         float var18 = ClientMathUtil.compute(30.0F, 35.0F);
         if (var1) {
            instance = ClientMathUtil.handle(2, 4);
         }

         if (instance > 0) {
            var18 = ClientMathUtil.compute(140.0F, 220.0F);
            var14 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
            var16 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
            instance--;
         }

         float var20 = ThreadLocalRandom.current().nextFloat(-3.0F, 3.0F)
            + (float)(ClientMathUtil.compute(4.0F, 5.0F) * Math.cos(System.currentTimeMillis() / 150.0))
            + (float)(ClientMathUtil.compute(4.0F, 5.0F) * Math.sin(System.currentTimeMillis() / 50.0))
            + (float)(ClientMathUtil.compute(5.0F, 8.0F) * Math.sin(System.currentTimeMillis() / 130.0))
               * (float)(ClientMathUtil.compute(4.0F, 7.0F) * Math.cos(System.currentTimeMillis() / 650.0))
            + (float)(ClientMathUtil.compute(12.0F, 18.0F) * Math.sin(System.currentTimeMillis() / 80.0))
               * (float)(ClientMathUtil.compute(2.0F, 3.0F) * Math.cos(System.currentTimeMillis() / 2650.0));
         float var22 = ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F)
            + (float)(ClientMathUtil.compute(2.0F, 3.0F) * Math.cos(System.currentTimeMillis() / 170.0))
            + (float)(ClientMathUtil.compute(3.0F, 4.0F) * Math.sin(System.currentTimeMillis() / 70.0))
            + (float)(ClientMathUtil.compute(1.0F, 2.0F) * Math.sin(System.currentTimeMillis() / 110.0))
               * (float)(ClientMathUtil.compute(1.0F, 2.0F) * Math.cos(System.currentTimeMillis() / 350.0));
         RotationController.handle(new RotationAngles(var14 + var20 / 4.0F, var16 + var22), var18, var18, 40.0F, 40.0F, 1, 7, false);
      }
   }

   public static void process(LivingEntity var0, boolean var1, String var2) {
      float var3 = 0.25F * (float)Math.cos(System.currentTimeMillis() / 1500L);
      float var4 = 0.2F * (float)Math.cos(System.currentTimeMillis() / 700L);
      float var5 = 0.2F * (float)Math.cos(System.currentTimeMillis() / 900L);
      Vec3d var6 = toggleState.player.getEyePos();
      Vec3d var7 = var0.getPos().add(var5, MathHelper.clamp(var6.y - var0.getPos().y, 0.0, 0.8) - var3, var4).subtract(var6).normalize();
      if (var2.contains("Fast")) {
         float var8 = ViewRotationCoordinator.context;
         float var9 = ViewRotationCoordinator.config;
         float var10 = ClientMathUtil.compute(280.0F, 360.0F);
         if (var1) {
            var8 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
            var9 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
         }

         RotationController.handle(new RotationAngles(var8, var9), var10, var10, 40.0F, 40.0F, 1, 7, false);
      } else if (var2.contains("Smooth")) {
         float var13 = ViewRotationCoordinator.context;
         float var15 = ViewRotationCoordinator.config;
         float var17 = 24.0F;
         if (var1) {
            instance = 2;
            var17 = 130.0F;
         }

         if (instance > 0) {
            var13 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
            var15 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
            instance--;
         }

         RotationController.handle(new RotationAngles(var13, var15), var17, var17, 40.0F, 40.0F, 1, 7, false);
      } else if (var2.contains("Random")) {
         float var14 = ViewRotationCoordinator.context;
         float var16 = ViewRotationCoordinator.config;
         float var18 = ClientMathUtil.compute(30.0F, 35.0F);
         if (var1) {
            instance = 2;
         }

         if (instance > 0) {
            var18 = ClientMathUtil.compute(200.0F, 280.0F);
            var14 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
            var16 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
            instance--;
         }

         float var11 = ThreadLocalRandom.current().nextFloat(-3.0F, 3.0F)
            + (float)(ClientMathUtil.compute(4.0F, 5.0F) * Math.cos(System.currentTimeMillis() / 150.0))
            + (float)(ClientMathUtil.compute(4.0F, 5.0F) * Math.sin(System.currentTimeMillis() / 50.0))
            + (float)(ClientMathUtil.compute(5.0F, 8.0F) * Math.sin(System.currentTimeMillis() / 130.0))
               * (float)(ClientMathUtil.compute(4.0F, 7.0F) * Math.cos(System.currentTimeMillis() / 650.0))
            + (float)(ClientMathUtil.compute(12.0F, 18.0F) * Math.sin(System.currentTimeMillis() / 80.0))
               * (float)(ClientMathUtil.compute(2.0F, 3.0F) * Math.cos(System.currentTimeMillis() / 2650.0));
         float var12 = ThreadLocalRandom.current().nextFloat(-1.0F, 1.0F)
            + (float)(ClientMathUtil.compute(2.0F, 3.0F) * Math.cos(System.currentTimeMillis() / 170.0))
            + (float)(ClientMathUtil.compute(3.0F, 4.0F) * Math.sin(System.currentTimeMillis() / 70.0))
            + (float)(ClientMathUtil.compute(1.0F, 2.0F) * Math.sin(System.currentTimeMillis() / 110.0))
               * (float)(ClientMathUtil.compute(1.0F, 2.0F) * Math.cos(System.currentTimeMillis() / 350.0));
         RotationController.handle(new RotationAngles(var14 + var11 / 4.0F, var16 + var12), var18, var18, 40.0F, 40.0F, 1, 7, false);
      }
   }
   private RotationEngine() {
   }
}
