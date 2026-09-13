package ru.wild.util.player;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import ru.wild.core.MinecraftContext;
import ru.wild.util.math.NumericTransform;

public final class EntityAimGeometry implements MinecraftContext {
   public static double handle(Entity var0) {
      return process(var0).length();
   }

   public static RotationAngles handle() {
      return new RotationAngles(toggleState.player.getYaw(), toggleState.player.getPitch());
   }

   public static boolean handle(Entity var0, float var1, boolean var2) {
      return handle(var0) < var1;
   }

   public static Vec3d process(Entity var0) {
      Vec3d var1 = toggleState.player.getEyePos();
      return handle(var1, var0).subtract(var1);
   }

   public static Vec3d handle(Vec3d var0, Box var1) {
      return new Vec3d(
         NumericTransform.resolve(var0.x, var1.minX, var1.maxX),
         NumericTransform.resolve(var0.y, var1.minY, var1.maxY),
         NumericTransform.resolve(var0.z, var1.minZ, var1.maxZ)
      );
   }

   public static Vec3d handle(Vec3d var0, Entity var1) {
      return handle(var0, var1.getBoundingBox());
   }

   public static Vec3d handle(LivingEntity var0) {
      double var1 = var0.getWidth() / 2.0F;
      double var3 = MathHelper.clamp(var0.getY() - 6.0, 0.0, var0.getHeight());
      double var5 = MathHelper.clamp(toggleState.player.getX() - var0.getX(), -var1, var1);
      double var7 = MathHelper.clamp(toggleState.player.getZ() - var0.getZ(), -var1, var1);
      return new Vec3d(
         var0.getX() - toggleState.player.getX() + var5, var0.getY() - toggleState.player.getY() - 0.8F, var0.getZ() - toggleState.player.getZ() + var7
      );
   }

   public static Vec3d process(LivingEntity var0) {
      double var1 = MathHelper.clamp(var0.getY() - var0.getY(), 0.0, var0.getHeight());
      double var3 = MathHelper.clamp(toggleState.player.getX() - var0.getX(), 0.0, 0.0);
      double var5 = MathHelper.clamp(toggleState.player.getZ() - var0.getZ(), 0.0, 0.0);
      return new Vec3d(
         var0.getX() - toggleState.player.getX() + var3, var0.getY() - toggleState.player.getY() - 0.8F, var0.getZ() - toggleState.player.getZ() + var5
      );
   }

   public static Vec3d compute(LivingEntity var0) {
      double var1 = MathHelper.clamp(var0.getEyeY() - var0.getY(), 0.0, var0.getHeight());
      double var3 = MathHelper.clamp(toggleState.player.getX() - var0.getX(), 0.0, 0.0);
      double var5 = MathHelper.clamp(toggleState.player.getZ() - var0.getZ(), 0.0, 0.0);
      return new Vec3d(
         var0.getX() - toggleState.player.getX() + var3, var0.getY() - toggleState.player.getEyeY() + var1, var0.getZ() - toggleState.player.getZ() + var5
      );
   }

   public static Vec3d resolve(LivingEntity var0) {
      double var1 = var0.getWidth() / 2.0F;
      double var3 = MathHelper.clamp(var0.getEyeY() - var0.getY(), 0.0, var0.getHeight());
      double var5 = MathHelper.clamp(toggleState.player.getX() - var0.getX(), -var1, var1);
      double var7 = MathHelper.clamp(toggleState.player.getZ() - var0.getZ(), -var1, var1);
      return new Vec3d(
         var0.getX() - toggleState.player.getX() + var5, var0.getY() - toggleState.player.getEyeY() + var3, var0.getZ() - toggleState.player.getZ() + var7
      );
   }

   public static double handle(float var0, float var1, float var2) {
      if (var1 < 0.0F) {
         var0 += 180.0F;
      }

      float var3 = 1.0F;
      if (var1 < 0.0F) {
         var3 = -0.5F;
      }

      if (var1 > 0.0F) {
         var3 = 0.5F;
      }

      if (var2 > 0.0F) {
         var0 -= 90.0F * var3;
      }

      if (var2 < 0.0F) {
         var0 += 90.0F * var3;
      }

      return Math.toRadians(var0);
   }

   public static Vec3d handle(Vec3d var0, Entity var1, float var2) {
      if (var1 == null) {
         return Vec3d.ZERO;
      }

      Box var3 = var1.getBoundingBox().expand(-var2);
      Vec3d var4 = var3.getCenter();
      Vec3d var5 = null;
      double var6 = Double.MAX_VALUE;

      for (double var8 = 0.0; var8 <= (var3.maxX - var3.minX) / 2.0; var8 += 0.1) {
         for (double var10 = 0.0; var10 <= (var3.maxY - var3.minY) / 2.0; var10 += 0.1) {
            for (double var12 = 0.0; var12 <= (var3.maxZ - var3.minZ) / 2.0; var12 += 0.1) {
               for (int var17 : new int[]{-1, 1}) {
                  for (int var21 : new int[]{-1, 1}) {
                     for (int var25 : new int[]{-1, 1}) {
                        double var26 = var4.x + var17 * var8;
                        double var28 = var4.y + var21 * var10;
                        double var30 = var4.z + var25 * var12;
                        Vec3d var32 = new Vec3d(var26, var28, var30);
                        Vector2f var33 = handle(var32);
                        if (CombatRaycast.handle(6.0, var33.x, var33.y, toggleState.player, false) instanceof EntityHitResult var35
                           && var35.getEntity().equals(var1)) {
                           double var36 = var0.distanceTo(var32);
                           if (var36 < var6) {
                              var6 = var36;
                              var5 = var32;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (var5 != null) {
         return var5;
      }

      double var38 = NumericTransform.resolve(var0.x, var3.minX, var3.maxX);
      double var39 = NumericTransform.resolve(var0.y, var3.minY, var3.maxY);
      double var40 = NumericTransform.resolve(var0.z, var3.minZ, var3.maxZ);
      return new Vec3d(var38, var39, var40);
   }

   public static Vector2f handle(Vec3d var0) {
      return handle(toggleState.player.getPos().add(0.0, toggleState.player.getEyeY(), 0.0), var0);
   }

   public static Vector2f handle(Vec3d var0, Vec3d var1) {
      double var2 = 180.0 / Math.PI;
      Vec3d var4 = var1.subtract(var0);
      double var5 = Math.hypot(var4.x, var4.z);
      float var7 = (float)(NumericTransform.compute(var4.z, var4.x) * (180.0 / Math.PI)) - 90.0F;
      float var8 = (float)(-(NumericTransform.compute(var4.y, var5) * (180.0 / Math.PI)));
      return new Vector2f(var7, var8);
   }

   public static Vec3d compute(Entity var0) {
      float var1 = toggleState.getRenderTickCounter().getTickProgress(false);
      return handle(toggleState.player.getCameraPosVec(var1), var0, Math.min(var0.getWidth(), var0.getHeight()) / 4.0F);
   }

   public static Vector4f update(LivingEntity var0) {
      float var1 = toggleState.getRenderTickCounter().getTickProgress(false);
      Vec3d var2 = toggleState.player.getCameraPosVec(var1);
      Vec3d var3 = compute(var0).subtract(var2);
      float var4 = NumericTransform.execute((float)(Math.toDegrees(Math.atan2(var3.z, var3.x)) - 90.0));
      float var5 = (float)(-Math.toDegrees(Math.atan2(var3.y, Math.sqrt(var3.x * var3.x + var3.z * var3.z))));
      float var6 = NumericTransform.execute(var4 - toggleState.player.getYaw());
      float var7 = var5 - toggleState.player.getPitch();
      return new Vector4f(var4, var5, var6, var7);
   }

   public static double apply(LivingEntity var0) {
      Vector4f var1 = update(var0);
      float var2 = var1.z;
      float var3 = var1.w;
      return Math.sqrt(var2 * var2 + var3 * var3);
   }
   private EntityAimGeometry() {
   }
}
