package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.config.rotation.RotationPreset;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.CameraRotationInterpolator;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.LocalhostHelper;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

public final class AuraTargetingStrategy implements MinecraftContext {
   private static int instance;
   private static int data = -1;
   private static int context;
   private static long config;
   private static boolean state;
   private static long cache;
   private static int output;
   private static long current;

   private AuraTargetingStrategy() {
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null && toggleState.world != null && var0 != null) {
         RotationPreset var1 = RotationPreset.handle();
         long var2 = System.currentTimeMillis();
         if (data != var0.getId()) {
            data = var0.getId();
            instance = 0;
            context = 0;
            config = 0L;
         }

         if (var1.current != null && !var1.current.equals("Custom")) {
            handle(var1, var0, var2);

            try {
               boolean var20 = compute(var0);
               float[] var21 = handle(var1, var20);
               int var22 = Math.round(var1.summary);
               RotationController.handle(
                  new RotationController.PrimaryState(var21[0], var21[1], var22, var22, var1.matrixBlend), () -> handle(var1.current, var0)
               );
            } finally {
               CameraRotationInterpolator.process();
            }
         } else {
            Vec3d var4 = handle(var0, var1, var2);
            Vec3d var5 = var4.subtract(toggleState.player.getEyePos());
            float var6 = (float)Math.toDegrees(Math.atan2(-var5.x, var5.z));
            float var7 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var5.y, Math.hypot(var5.x, var5.z))), -90.0, 90.0);
            boolean var8 = compute(var0);
            if ("Static".equals(var1.responseCompute)) {
               var7 *= 0.4F;
            } else if ("Locked".equals(var1.responseCompute)) {
               var7 = 0.0F;
            }

            if (var1.providerClose && handle(var2, var1)) {
               var7 = -var1.presetSave;
            }

            float var9 = (float)(Math.cos(var2 / 40.0) * var1.animator) + handle(-var1.animator, var1.animator) * 0.5F;
            float var10 = (float)(Math.sin(var2 / 70.0) * var1.source) + handle(-var1.source, var1.source) * 0.5F;
            float var11 = var6 + var1.providerFetch + var9;
            float var12 = var7 + var1.profileDraw + var10;
            var12 = MathHelper.clamp(var12, var1.vectorPerform, var1.eventAttach);
            var12 = MathHelper.clamp(var12, -90.0F, 90.0F);
            float[] var13 = handle(var1, var8);
            float var14 = var13[0];
            float var15 = var13[1];
            RotationAngles var16 = new RotationAngles(var11, var12);
            int var17 = Math.round(var1.summary);
            RotationController.handle(var16, var14, var15, var17, var17, ThreadLocalRandom.current().nextInt(1, 3), 5, var1.matrixBlend);
         }
      }
   }

   private static void handle(RotationPreset var0, LivingEntity var1, long var2) {
      CameraRotationInterpolator.process();
      CameraRotationInterpolator.instance = true;
      float var4 = var0.providerFetch;
      float var5 = var0.profileDraw;
      boolean var6 = var0.presetWrite != null && !var0.presetWrite.isEmpty()
         || !"Multipoint".equals(var0.mode)
         || var0.latest > 0.001F
         || var0.serverRead > 0.001F;
      if (var6) {
         Box var7 = var1.getBoundingBox();
         Vec3d var8 = PlayerRaycast.handle(var7, false);
         Vec3d var9 = handle(var1, var7, var0, var2);
         float[] var10 = handle(var8);
         float[] var11 = handle(var9);
         var4 += MathHelper.wrapDegrees(var11[0] - var10[0]);
         var5 += var11[1] - var10[1];
      }

      CameraRotationInterpolator.config = var0.vectorPerform;
      CameraRotationInterpolator.state = var0.eventAttach;
      CameraRotationInterpolator.handle(var4 * var0.frameCheck, var5 * var0.frameCheck, var0.positionAdvance);
   }

   private static float[] handle(Vec3d var0) {
      Vec3d var1 = var0.subtract(toggleState.player.getEyePos());
      float var2 = (float)Math.toDegrees(Math.atan2(-var1.x, var1.z));
      float var3 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var1.y, Math.hypot(var1.x, var1.z))), -90.0, 90.0);
      return new float[]{var2, var3};
   }

   private static Vec3d handle(LivingEntity var0, Box var1, RotationPreset var2, long var3) {
      Vec3d var5;
      if (var2.presetWrite != null && !var2.presetWrite.isEmpty()) {
         var5 = process(var0, var1, var2, var3);
      } else {
         var5 = switch (var2.mode) {
            case "Center" -> var1.getCenter();
            case "Eyes" -> new Vec3d(var0.getX(), var0.getEyeY(), var0.getZ());
            case "Closest" -> EntityAimGeometry.handle(toggleState.player.getEyePos(), var0);
            default -> PlayerRaycast.handle(var1, false);
         };
      }

      double var13 = 0.0;
      double var8 = 0.0;
      if (var2.latest > 0.001F) {
         Vec3d var10 = process(var0);
         double var11 = var2.latest * Math.sin(var3 / 300.0);
         var13 = var10.x * var11;
         var8 = var10.z * var11;
      }

      Vec3d var14 = var5.add(var13, 0.0, var8);
      if (var2.serverRead > 0.001F) {
         Vec3d var15 = var0.getVelocity();
         var14 = var14.add(var15.x * var2.serverRead * 3.0, 0.0, var15.z * var2.serverRead * 3.0);
      }

      return var14;
   }

   private static void handle(String var0, LivingEntity var1) {
      switch (var0) {
         case "FunTime":
            AdaptiveJitterAimPattern.handle(var1);
            break;
         case "Smooth":
            SnapAimPattern.handle(var1);
            break;
         default:
            float[] var4 = AttackAura.process(var1);
            float[] var5 = new float[]{var4[0], var4[1], var4[0] + var4[1]};
            boolean var6 = AdaptiveAttackTiming.handle(var1, false, true, true, -50L, var5);
            switch (var0) {
               case "Snap":
                  RotationEngine.handle(var1, var6, "Fast");
                  break;
               case "Holy":
                  RotationEngine.process(var1, var6);
                  break;
               case "Spooky":
                  RotationEngine.handle(var1, var6);
                  break;
               case "Matrix":
                  if (LocalhostHelper.handle("spookytime")) {
                     RotationEngine.handle(var1, var6);
                  } else if (LocalhostHelper.handle("holy")) {
                     RotationEngine.process(var1, var6);
                  } else if (LocalhostHelper.handle("ares")) {
                     RotationEngine.compute(var1, var6);
                  } else {
                     RotationEngine.handle(var1, var6);
                  }
            }
      }
   }

   private static boolean handle(long var0, RotationPreset var2) {
      long var3 = (long)(var2.windowConvert * 1000.0F);
      if (!state && var0 - current >= var3) {
         state = true;
         cache = var0;
         output = ThreadLocalRandom.current().nextInt(200, 320);
         current = var0;
      }

      if (state && var0 - cache >= output) {
         state = false;
      }

      return state;
   }

   private static Vec3d handle(LivingEntity var0, RotationPreset var1, long var2) {
      Box var4 = var0.getBoundingBox();
      Vec3d var5;
      if (var1.presetWrite != null && !var1.presetWrite.isEmpty()) {
         var5 = process(var0, var4, var1, var2);
      } else {
         var5 = switch (var1.mode) {
            case "Center" -> var4.getCenter();
            case "Eyes" -> new Vec3d(var0.getX(), var0.getEyeY(), var0.getZ());
            case "Closest" -> EntityAimGeometry.handle(toggleState.player.getEyePos(), var0);
            default -> PlayerRaycast.handle(var4, false);
         };
      }

      double var19 = Math.max(0.2, var1.previous);
      double var8 = var1.target * Math.sin(var2 / (250.0 / var19));
      double var10 = var1.pending * Math.cos(var2 / (520.0 / var19));
      double var12 = 0.0;
      double var14 = 0.0;
      if (var1.latest > 0.001F) {
         Vec3d var16 = process(var0);
         double var17 = var1.latest * Math.sin(var2 / 300.0);
         var12 = var16.x * var17;
         var14 = var16.z * var17;
      }

      Vec3d var20 = var5.add(var8 + var12, var10, var14);
      if (var1.serverRead > 0.001F) {
         Vec3d var21 = var0.getVelocity();
         var20 = var20.add(var21.x * var1.serverRead * 3.0, 0.0, var21.z * var1.serverRead * 3.0);
      }

      return var20;
   }

   private static Vec3d process(LivingEntity var0, Box var1, RotationPreset var2, long var3) {
      int var5 = var2.presetWrite.size();
      long var6 = (long)(var2.itemProject * 1000.0F / Math.max(0.1F, var2.moduleCollect));
      if ("Cycle".equals(var2.vectorMatch)) {
         if (var3 >= config) {
            context = (context + 1) % var5;
            config = var3 + var6;
         }

         return handle(var0, var1, var2.presetWrite.get(Math.min(context, var5 - 1)));
      } else if ("Random".equals(var2.vectorMatch)) {
         if (var3 >= config) {
            context = ThreadLocalRandom.current().nextInt(var5);
            config = var3 + var6;
         }

         return handle(var0, var1, var2.presetWrite.get(Math.min(context, var5 - 1)));
      } else {
         Vec3d var8 = toggleState.player.getEyePos();
         Vec3d var9 = toggleState.player.getRotationVec(1.0F).normalize();
         Vec3d var10 = null;
         double var11 = Double.MAX_VALUE;

         for (RotationPreset.State var14 : var2.presetWrite) {
            Vec3d var15 = handle(var0, var1, var14);
            Vec3d var16 = var15.subtract(var8).normalize();
            double var17 = Math.acos(MathHelper.clamp(var9.dotProduct(var16), -1.0, 1.0));
            if (var17 < var11) {
               var11 = var17;
               var10 = var15;
            }
         }

         return var10 != null ? var10 : var1.getCenter();
      }
   }

   private static Vec3d handle(LivingEntity var0, Box var1, RotationPreset.State var2) {
      Vec3d var3 = process(var0);
      double var4 = (var1.minX + var1.maxX) * 0.5;
      double var6 = (var1.minZ + var1.maxZ) * 0.5;
      double var8 = var1.maxX - var1.minX;
      double var10 = var1.maxY - var1.minY;
      return new Vec3d(var4 + var3.x * (var2.instance * var8), var1.minY + var2.data * var10, var6 + var3.z * (var2.instance * var8));
   }

   private static Vec3d process(LivingEntity var0) {
      Vec3d var1 = var0.getPos().subtract(toggleState.player.getPos());
      double var2 = Math.hypot(var1.x, var1.z);
      return var2 < 1.0E-4 ? new Vec3d(1.0, 0.0, 0.0) : new Vec3d(-var1.z / var2, 0.0, var1.x / var2);
   }

   private static boolean compute(LivingEntity var0) {
      float[] var1 = AttackAura.process(var0);
      float[] var2 = new float[]{var1[0], var1[1], var1[0] + var1[1]};
      boolean var3 = AdaptiveAttackTiming.handle(var0, false, true, true, -50L, var2);
      if (var3 && EntityAimGeometry.process(var0).length() < AttackAura.handle(var0)) {
         instance = 2;
      }

      if (instance <= 0) {
         return false;
      }

      instance--;
      return true;
   }

   private static float[] handle(RotationPreset var0, boolean var1) {
      float var2 = var1 ? var0.animationDraw : handle(var0.selection, var0.enabled);
      float var3 = var1 ? var0.pointEncode : handle(var0.renderer, var0.handler);
      if ("Static".equals(var0.responseCompute)) {
         var3 *= 0.3F;
      }

      return new float[]{var2, var3};
   }

   private static float handle(float var0, float var1) {
      if (var1 < var0) {
         float var2 = var0;
         var0 = var1;
         var1 = var2;
      }

      return var1 == var0 ? var0 : var0 + ThreadLocalRandom.current().nextFloat() * (var1 - var0);
   }
}
