package ru.wild.util.player;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.wild.core.MinecraftContext;

public final class CombatRaycast implements MinecraftContext {
   public static EntityHitResult handle(Entity var0, Vec3d var1, Vec3d var2, Box var3, Predicate<Entity> var4, double var5) {
      World var7 = var0.getWorld();
      double var8 = var5;
      Entity var10 = null;
      Vec3d var11 = null;

      for (Entity var13 : var7.getOtherEntities(var0, var3, var4)) {
         Box var14 = var13.getBoundingBox().expand(var13.getTargetingMargin());
         Optional<Vec3d> var15 = var14.raycast(var1, var2);
         if (var14.contains(var1) || var15.isPresent()) {
            double var16 = var15.<Double>map(var1::squaredDistanceTo).orElse(0.0);
            var16 = Math.sqrt(var16);
            if ((var16 < var8 || var8 == 0.0) && var13.getRootVehicle() != var0.getRootVehicle()) {
               var10 = var13;
               var11 = var15.orElse(var1);
               var8 = var16;
            }
         }
      }

      return var10 == null ? null : new EntityHitResult(var10, var11);
   }

   public static HitResult handle(double var0, float var2, float var3, Entity var4, boolean var5) {
      float var6 = toggleState.getRenderTickCounter().getTickProgress(true);
      Vec3d var7 = toggleState.player.getCameraPosVec(var6);
      Vec3d var8 = handle(var3, var2);
      Vec3d var9 = var7.add(var8.multiply(var0));
      HitResult var10 = handle(var7, var9, ShapeType.COLLIDER, FluidHandling.NONE);
      double var11 = var10.getPos().squaredDistanceTo(var7);
      Box var13 = var4.getBoundingBox().stretch(var8.multiply(var0)).expand(1.0);
      EntityHitResult var14 = ProjectileUtil.raycast(var4, var7, var9, var13, var0x -> !var0x.isSpectator() && var0x.isAlive() && var0x.canHit(), var0 * var0);
      return (HitResult)(var14 == null || !var5 && !(var14.getPos().squaredDistanceTo(var7) < var11) ? var10 : var14);
   }

   public static boolean handle(float var0, float var1, double var2, Entity var4) {
      float var5 = toggleState.getRenderTickCounter().getTickProgress(true);
      Vec3d var6 = toggleState.player.getCameraPosVec(var5);
      Vec3d var7 = handle(var1, var0);
      Vec3d var8 = var6.add(var7.multiply(var2));
      Box var9 = var4.getBoundingBox();
      return var9.contains(var6) || var9.raycast(var6, var8).isPresent();
   }

   public static boolean handle(float var0, float var1, double var2, Entity var4, boolean var5) {
      return process(var0, var1, var2, var4, var5) != null;
   }

   public static EntityHitResult process(float var0, float var1, double var2, Entity var4, boolean var5) {
      if (toggleState.player != null && toggleState.world != null && var4 != null) {
         return handle(var2, var0, var1, toggleState.player, var5) instanceof EntityHitResult var7 && var7.getEntity().equals(var4) ? var7 : null;
      } else {
         return null;
      }
   }

   public static boolean process(float var0, float var1, double var2, Entity var4) {
      return compute(var0, var1, var2, var4, true);
   }

   public static boolean compute(float var0, float var1, double var2, Entity var4, boolean var5) {
      if (toggleState.player != null && toggleState.world != null && var4 != null) {
         Vec3d var6 = toggleState.player.getEyePos();
         Vec3d var7 = handle(var1, var0);
         Vec3d var8 = var6.add(var7.multiply(var2));
         Box var9 = var4.getBoundingBox().expand(var4.getTargetingMargin());
         Optional var10 = var9.raycast(var6, var8);
         if (var9.contains(var6)) {
            return true;
         }

         if (var10.isEmpty()) {
            return false;
         }

         if (var5) {
            return true;
         }

         HitResult var11 = handle(var6, var8, ShapeType.COLLIDER, FluidHandling.NONE);
         return var11.getType() == Type.MISS || ((Vec3d)var10.get()).squaredDistanceTo(var6) < var11.getPos().squaredDistanceTo(var6);
      } else {
         return false;
      }
   }

   public static Vec3d handle(float var0, float var1) {
      float var2 = -var1 * (float) (Math.PI / 180.0) - (float) Math.PI;
      float var3 = -var0 * (float) (Math.PI / 180.0);
      float var4 = MathHelper.cos(var2);
      float var5 = MathHelper.sin(var2);
      float var6 = -MathHelper.cos(var3);
      float var7 = MathHelper.sin(var3);
      return new Vec3d(var5 * var6, var7, var4 * var6);
   }

   public static HitResult handle(Vec3d var0, Vec3d var1, ShapeType var2, FluidHandling var3) {
      return toggleState.world.raycast(new RaycastContext(var0, var1, var2, var3, toggleState.player));
   }

   public static Vec3d process(float var0, float var1) {
      float var2 = (float)(var1 * (Math.PI / 180.0));
      float var3 = (float)(-var0 * (Math.PI / 180.0));
      float var4 = MathHelper.cos(var3);
      float var5 = MathHelper.sin(var3);
      float var6 = MathHelper.cos(var2);
      float var7 = MathHelper.sin(var2);
      return new Vec3d(var5 * var6, -var7, var4 * var6);
   }
   private CombatRaycast() {
   }
}
