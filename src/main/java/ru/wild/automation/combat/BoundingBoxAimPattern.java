package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.RotationAngles;

public class BoundingBoxAimPattern implements MinecraftContext {
   private static final int instance = 15;
   private static boolean data;
   private static int context = -1;
   private static float config;
   private static float state;

   public static void handle(LivingEntity var0) {
      if (toggleState.player == null) {
         process();
      } else if (toggleState.world != null && var0 != null) {
         Box var1 = var0.getBoundingBox();
         Vec3d var2 = toggleState.player.getEyePos();
         boolean var3 = AttackAura.vectorPerform.compute();
         if (context != var0.getId()) {
            context = var0.getId();
            config = ViewRotationCoordinator.context;
            state = ViewRotationCoordinator.config;
         }

         Vec3d var4 = var0.getPos().add(0.0, MathHelper.clamp(var2.y - var0.getY(), 0.0, var0.getHeight()), 0.0).subtract(var2).normalize();
         float var5 = (float)Math.toDegrees(Math.atan2(-var4.x, var4.z));
         float var6 = (float)MathHelper.clamp(Math.toDegrees(Math.asin(-var4.y)), -90.0, 90.0);
         float var7 = Float.MAX_VALUE;
         float var8 = -Float.MAX_VALUE;
         float var9 = Float.MAX_VALUE;
         float var10 = -Float.MAX_VALUE;
         double[][] var11 = new double[][]{
            {var1.minX, var1.minY, var1.minZ},
            {var1.minX, var1.minY, var1.maxZ},
            {var1.minX, var1.maxY, var1.minZ},
            {var1.minX, var1.maxY, var1.maxZ},
            {var1.maxX, var1.minY, var1.minZ},
            {var1.maxX, var1.minY, var1.maxZ},
            {var1.maxX, var1.maxY, var1.minZ},
            {var1.maxX, var1.maxY, var1.maxZ}
         };

         for (double[] var15 : var11) {
            Vec3d var16 = new Vec3d(var15[0], var15[1], var15[2]).subtract(var2);
            float var17 = (float)Math.toDegrees(Math.atan2(-var16.x, var16.z));
            float var18 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var16.y, Math.hypot(var16.x, var16.z))), -90.0, 90.0);
            float var19 = MathHelper.wrapDegrees(var17 - var5);
            float var20 = var18 - var6;
            var7 = Math.min(var7, var19);
            var8 = Math.max(var8, var19);
            var9 = Math.min(var9, var20);
            var10 = Math.max(var10, var20);
         }

         float var36 = 22.0F;
         float var37 = MathHelper.clamp(MathHelper.wrapDegrees(ViewRotationCoordinator.context - var5), var7 - var36, var8 + var36);
         float var38;
         if (var3) {
            float var39 = (float)(System.currentTimeMillis() % 2000L) / 2000.0F;
            float var41 = (float)Math.sin(var39 * Math.PI * 2.0);
            float var43 = (var10 - var9) * 0.75F + ClientMathUtil.resolve(15.0F, 30.0F);
            float var45 = (var9 + var10) * 0.5F;
            var38 = var45 + var41 * var43 + ClientMathUtil.resolve(-4.0F, 4.0F);
         } else {
            var38 = MathHelper.clamp(ViewRotationCoordinator.config - var6, var9 - var36, var10 + var36);
         }

         float var40 = var5 + var37;
         float var42 = MathHelper.clamp(var6 + var38, -90.0F, 90.0F);
         float var44 = ViewRotationCoordinator.context;
         float var46 = ViewRotationCoordinator.config;
         float var47 = Math.abs(MathHelper.wrapDegrees(var44 - config));
         float var48 = Math.abs(var46 - state);
         config = var44;
         state = var46;
         float[] var21 = AttackAura.process(var0);
         float[] var22 = new float[]{var21[0], var21[1], var21[0] + var21[1]};
         boolean var23 = AdaptiveAttackTiming.handle(var0, false, true, true, -50L, var22) && EntityAimGeometry.process(var0).length() < AttackAura.handle(var0);
         if (!var23) {
            float var49 = ClientMathUtil.resolve(0.3F, 0.6F);
            float var50 = ClientMathUtil.resolve(0.2F, 0.4F);
            float var51 = var49 + var47 * 3.5F;
            float var52 = var50 + var48 * 3.5F;
            float var53 = MathHelper.wrapDegrees(var40 - toggleState.player.getYaw());
            float var29 = var42 - toggleState.player.getPitch();
            float var30 = MathHelper.clamp(var53, -var51, var51);
            float var31 = var3 ? MathHelper.clamp(var29, -var52 * 5.0F, var52 * 5.0F) : MathHelper.clamp(var29, -var52, var52);
            float var32 = toggleState.player.getYaw() + var30;
            float var33 = MathHelper.clamp(toggleState.player.getPitch() + var31, -90.0F, 90.0F);
            float var34 = Math.max(var51 * 18.5F, 6.0F);
            float var35 = Math.max(var52 * 18.5F, 4.0F);
            handle(new RotationAngles(var32, var33), var34, var35);
         } else {
            float var24 = toggleState.player.getYaw();
            float var25 = toggleState.player.getPitch();
            float var26 = MathHelper.wrapDegrees(var24 - var5);
            float var27 = var25 - var6;
            boolean var28 = var26 >= var7 && var26 <= var8 && var27 >= var9 && var27 <= var10;
            if (!var28) {
               handle(var1, var2, var24, var25);
            } else {
               handle(new RotationAngles(var24, var25), 6.0F, 4.0F);
            }
         }
      } else {
         handle();
      }
   }

   public static void handle() {
      if (!data) {
         process();
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
         process();
      }
   }

   private static void handle(Box var0, Vec3d var1, float var2, float var3) {
      boolean var4 = ThreadLocalRandom.current().nextBoolean();
      float var5 = var4 ? ClientMathUtil.resolve(0.05F, 0.2F) : ClientMathUtil.resolve(0.8F, 0.95F);
      float var6 = (float)(ThreadLocalRandom.current().nextDouble() * ThreadLocalRandom.current().nextDouble());
      float var7 = 0.5F + var6 * 0.5F;
      float var8 = ClientMathUtil.resolve(-1.0F, 1.0F) * ClientMathUtil.resolve(0.0F, 1.0F);
      float var9 = (var8 + 1.0F) / 2.0F;
      double var10 = var0.minX + (var0.maxX - var0.minX) * var5;
      double var12 = var0.minY + (var0.maxY - var0.minY) * var7;
      double var14 = var0.minZ + (var0.maxZ - var0.minZ) * var9;
      Vec3d var16 = new Vec3d(var10, var12, var14).subtract(var1);
      float var17 = (float)Math.toDegrees(Math.atan2(-var16.x, var16.z));
      float var18 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var16.y, Math.hypot(var16.x, var16.z))), -90.0, 90.0);
      float var19 = Math.abs(MathHelper.wrapDegrees(var17 - var2));
      float var20 = Math.abs(var18 - var3);
      float var21 = ClientMathUtil.resolve(15.0F, 25.0F) + var19 / 90.0F * ClientMathUtil.resolve(30.0F, 40.0F);
      float var22 = ClientMathUtil.resolve(10.0F, 18.0F) + var20 / 90.0F * ClientMathUtil.resolve(20.0F, 27.0F);
      handle(new RotationAngles(var17, var18), var21, var22);
   }

   private static void handle(RotationAngles var0, float var1, float var2) {
      data = RotationController.cache <= 15;
      RotationController.handle(var0, var1, var2, ClientMathUtil.handle(25, 45), ClientMathUtil.handle(10, 25), ClientMathUtil.handle(0, 2), 15, false);
   }

   private static void process() {
      data = false;
      context = -1;
      config = 0.0F;
      state = 0.0F;
   }
}
