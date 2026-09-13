package ru.wild.automation.combat;

import java.security.SecureRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

public final class PredictiveAimPattern implements MinecraftContext {
   private static final SecureRandom instance = new SecureRandom();
   private static final long data = 3500L;
   private static final int context = 31;
   private static final long config = 250L;
   private static final long state = 238L;
   private static int cache;
   private static int output = -1;
   private static boolean current;

   private PredictiveAimPattern() {
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null && var0 != null) {
         current = true;
         RotationAngles var1 = new RotationAngles(toggleState.player);
         RotationAngles var2 = process(var0);
         float[] var3 = AttackAura.process(var0);
         boolean var4 = AdaptiveAttackTiming.handle(var0, false, false, true, 0L, var3);
         boolean var5 = AdaptiveAttackTiming.handle(var0, false, false, true, -50L, var3);
         RotationAngles var6;
         if (toggleState.interactionManager == null) {
            var6 = AuraRotationPlanner.handle(var1, var2, var0);
         } else {
            var6 = handle(var1, var2, var0, var4, var5);
         }

         handle(var6);
      }
   }

   public static void handle() {
      if (current && toggleState.player != null) {
         RotationAngles var0 = new RotationAngles(toggleState.player);
         RotationAngles var1 = new RotationAngles(ViewRotationCoordinator.context, ViewRotationCoordinator.config);
         if (var0.handle(var1) < 1.0F) {
            current = false;
         } else {
            handle(handle(var0, var1, null, false, false));
         }
      }
   }

   public static void process() {
      cache++;
   }

   public static void compute() {
      cache = 0;
      output = -1;
      current = false;
   }

   private static RotationAngles handle(RotationAngles var0, RotationAngles var1, LivingEntity var2, boolean var3, boolean var4) {
      long var5 = (long)AdaptiveAttackTiming.drawAnimation();
      RotationAngles var7 = handle(var0, var1);
      float var8 = var7.instance;
      float var9 = var7.data;
      float var10 = (float)Math.hypot(Math.abs(var8), Math.abs(var9));
      if (var10 < 1.0E-4F) {
         var10 = 1.0E-4F;
      }

      boolean var11 = cache > 0 && cache % 31 == 0 && var5 < 250L;
      if (var11) {
         if (var5 >= 238L && output != cache) {
            toggleState.player.swingHand(Hand.MAIN_HAND);
            output = cache;
         }

         float var12 = var0.instance + MathHelper.clamp(var8, -22.0F, 22.0F);
         return new RotationAngles(var12, -85.0F);
      } else {
         return var2 != null ? handle(var0, var8, var9, var10, var2, var3, var4, var5) : handle(var0, var8, var9, var10, var5);
      }
   }

   private static RotationAngles handle(RotationAngles var0, float var1, float var2, float var3, LivingEntity var4, boolean var5, boolean var6, long var7) {
      boolean var9 = toggleState.player.distanceTo(var4) <= AttackAura.handle(var4);
      boolean var10 = var7 < 180L;
      float var11 = handle(18.0F, 28.0F);
      float var12 = handle(2.8F, 6.2F);
      if (var6) {
         var11 = Math.max(var11, handle(34.0F, 52.0F));
         var12 = Math.max(var12, handle(4.2F, 7.8F));
      }

      if (var10) {
         var11 = Math.max(var11, handle(44.0F, 72.0F));
         var12 = Math.max(var12, handle(5.4F, 10.0F));
      }

      if (Math.abs(var1) > 40.0F) {
         var11 += handle(10.0F, 18.0F);
      }

      if (Math.abs(var1) > 75.0F) {
         var11 += handle(12.0F, 24.0F);
      }

      if (Math.abs(var2) > 20.0F) {
         var12 += handle(1.4F, 3.2F);
      }

      if (Math.abs(var2) > 35.0F) {
         var12 += handle(1.6F, 3.8F);
      }

      float var13 = handle(var1, var3, var11);
      float var14 = handle(var2, var3, var12);
      float var15 = MathHelper.clamp(var1, -var13, var13);
      float var16 = MathHelper.clamp(var2, -var14, var14);
      float var17 = var5 ? 1.0F : (var6 ? handle(0.88F, 0.97F) : (var10 ? handle(0.74F, 0.88F) : handle(0.56F, 0.74F)));
      if (var9 && !var6 && !var10) {
         var17 = Math.max(var17, handle(0.68F, 0.82F));
      }

      float var18 = var9 ? 1.25F : 0.9F;
      if (var6) {
         var18 = Math.max(var18, 1.4F);
      }

      if (var10) {
         var18 = Math.max(var18, 1.55F);
      }

      float var19 = handle(var7, cache, var18, Math.abs(var1));
      float var20 = process(var7, cache, var18, Math.abs(var2));
      if (Math.abs(var1) < 4.0F) {
         var19 *= 0.35F;
      }

      if (Math.abs(var2) < 2.5F) {
         var20 *= 0.25F;
      }

      float var21 = process(var17, var0.instance, var0.instance + var15) + var19;
      float var22 = process(var17, var0.data, var0.data + var16) + var20;
      return new RotationAngles(var21, MathHelper.clamp(var22, -90.0F, 90.0F));
   }

   private static RotationAngles handle(RotationAngles var0, float var1, float var2, float var3, long var4) {
      RotationAngles var6 = switch (cache % 4) {
         case 0 -> new RotationAngles((float)Math.cos((float)var4 / 40.0F + cache % 6), (float)Math.sin((float)var4 / 40.0F + cache % 6));
         case 1 -> new RotationAngles((float)Math.sin((float)var4 / 40.0F + cache % 6), (float)Math.cos((float)var4 / 40.0F + cache % 6));
         case 2 -> new RotationAngles((float)Math.sin((float)var4 / 40.0F + cache % 6), (float)(-Math.cos((float)var4 / 40.0F + cache % 6)));
         default -> new RotationAngles((float)(-Math.cos((float)var4 / 40.0F + cache % 6)), (float)Math.sin((float)var4 / 40.0F + cache % 6));
      };
      float var7 = MathHelper.clamp((float)var4 / 3500.0F, 0.0F, 1.0F);
      float var8 = var4 >= 3500L ? 0.0F : 1.0F - var7 * 0.55F;
      float var9 = var8 > 0.0F ? handle(12.0F, 22.0F) * var6.instance * var8 : 0.0F;
      float var10 = handle(0.35F, 1.35F) * (float)Math.cos(System.currentTimeMillis() / 420.0 + cache);
      float var11 = var8 > 0.0F ? (handle(2.2F, 5.8F) * var6.data + var10) * var8 : 0.0F;
      float var12 = var4 < 180L ? handle(0.0F, 3.5F) : (var4 < 600L ? handle(4.0F, 10.0F) : (var4 >= 3500L ? handle(12.0F, 28.0F) : handle(6.0F, 14.0F)));
      float var13 = var4 < 180L ? handle(0.0F, 1.0F) : (var4 < 600L ? handle(1.2F, 3.0F) : (var4 >= 3500L ? handle(3.0F, 6.8F) : handle(1.5F, 4.2F)));
      float var14 = handle(var1, var3, var12);
      float var15 = handle(var2, var3, var13);
      float var16 = MathHelper.clamp(var1, -var14, var14);
      float var17 = MathHelper.clamp(var2, -var15, var15);
      float var18 = var4 < 180L ? 0.0F : (var4 < 600L ? handle(0.08F, 0.22F) : (var4 >= 3500L ? handle(0.54F, 0.78F) : handle(0.2F, 0.42F)));
      float var19 = process(var18, var0.instance, var0.instance + var16) + var9;
      float var20 = process(var18, var0.data, var0.data + var17) + var11;
      return new RotationAngles(var19, MathHelper.clamp(var20, -90.0F, 90.0F));
   }

   private static RotationAngles process(LivingEntity var0) {
      Vec3d var1 = PlayerRaycast.process(var0.getBoundingBox());
      Vec3d var2 = var1.subtract(toggleState.player.getEyePos());
      return new RotationAngles(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0),
         (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(var2.y, Math.hypot(var2.x, var2.z))))
      );
   }

   private static RotationAngles handle(RotationAngles var0, RotationAngles var1) {
      return new RotationAngles(
         MathHelper.wrapDegrees(var1.instance - var0.instance), MathHelper.clamp(MathHelper.wrapDegrees(var1.data - var0.data), -90.0F, 90.0F)
      );
   }

   private static void handle(RotationAngles var0) {
      RotationController.handle(var0, 360.0F, 360.0F, 45.0F, 45.0F, 0, 15, false);
   }

   private static float handle(long var0, int var2, float var3, float var4) {
      float var5 = (float)Math.sin((float)var0 / 38.0F + var2 * 0.37F) * handle(0.45F, 1.25F)
         + (float)Math.cos((float)var0 / 71.0F + var2 * 0.18F) * handle(0.18F, 0.55F);
      if (handle(var4 > 24.0F ? 0.22F : 0.08F)) {
         var5 += handle(-1.55F, 1.55F);
      }

      return var5 * var3;
   }

   private static float process(long var0, int var2, float var3, float var4) {
      float var5 = (float)Math.sin((float)var0 / 52.0F + var2 * 0.21F) * handle(0.1F, 0.42F)
         + (float)Math.cos((float)var0 / 93.0F + var2 * 0.11F) * handle(0.08F, 0.28F);
      if (handle(var4 > 8.0F ? 0.18F : 0.06F)) {
         var5 += handle(-0.55F, 0.55F);
      }

      return var5 * var3;
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.abs(var0 / var1) * var2;
   }

   private static boolean handle(float var0) {
      return instance.nextFloat() < var0;
   }

   private static float handle(float var0, float var1) {
      return process(instance.nextFloat(), var0, var1);
   }

   private static float process(float var0, float var1, float var2) {
      return var1 + var0 * (var2 - var1);
   }
}
