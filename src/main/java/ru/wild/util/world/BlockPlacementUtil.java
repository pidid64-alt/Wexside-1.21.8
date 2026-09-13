package ru.wild.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.player.RotationAngles;

public class BlockPlacementUtil {
   public static MinecraftClient instance = MinecraftClient.getInstance();

   public static void handle(MovementInputEvent var0, BlockPos var1, NumberSetting var2, float var3) {
      if (instance.player != null && instance.world != null && var1 != null) {
         Vec3d var4 = instance.player.getPos();
         Vec3d var5 = new Vec3d(var1.getX() + 0.5, instance.player.getY(), var1.getZ() + 0.5);
         double var6 = var4.distanceTo(var5);
         if (var6 <= var2.compute()) {
            var0.handle(0.0F);
            var0.process(0.0F);
            var0.handle(false);
            var0.process(false);
         } else {
            float var8 = (float)Math.toDegrees(Math.atan2(var5.z - var4.z, var5.x - var4.x)) - 90.0F;
            BlockPlacementUtil.DataRecord var9 = handle(var3, 1.2, 0.4);
            boolean var10 = handle();
            var0.process(var10);
            float var11 = 0.0F;
            if (!var10 && var9.hitSolid) {
               float var12 = NumericTransform.execute(var8 - var3);
               var11 = var12 > 0.0F ? -0.8F : 0.8F;
            }

            boolean var13 = !var10 && handle(var3);
            var0.handle(var13);
            handle(var0, var3, var8, var11);
         }
      }
   }

   private static BlockPlacementUtil.DataRecord handle(float var0, double var1, double var3) {
      if (instance.player != null && instance.world != null) {
         Vec3d var5 = instance.player.getEyePos().add(0.0, var3, 0.0);
         double var6 = -Math.sin(Math.toRadians(var0));
         double var8 = Math.cos(Math.toRadians(var0));
         Vec3d var10 = new Vec3d(var6, 0.0, var8).normalize();
         Vec3d var11 = var5.add(var10.multiply(var1));
         RaycastContext var12 = new RaycastContext(var5, var11, ShapeType.OUTLINE, FluidHandling.NONE, instance.player);
         BlockHitResult var13 = instance.world.raycast(var12);
         if (var13.getType() != Type.BLOCK) {
            return new BlockPlacementUtil.DataRecord(false, BlockPos.ORIGIN);
         }

         BlockPos var14 = var13.getBlockPos();
         BlockState var15 = instance.world.getBlockState(var14);
         boolean var16 = !var15.isAir() && !var15.getCollisionShape(instance.world, var14).isEmpty();
         return new BlockPlacementUtil.DataRecord(var16, var14);
      } else {
         return new BlockPlacementUtil.DataRecord(false, BlockPos.ORIGIN);
      }
   }

   public static boolean handle(float var0) {
      if (instance.player == null || instance.world == null) {
         return false;
      }

      if (!instance.player.isOnGround()) {
         return false;
      }

      Vec3d var1 = instance.player.getPos();
      double var2 = -Math.sin(Math.toRadians(var0));
      double var4 = Math.cos(Math.toRadians(var0));
      BlockPos var6 = BlockPos.ofFloored(var1);
      BlockPos var7 = BlockPos.ofFloored(var1.x + var2 * 0.8, var1.y, var1.z + var4 * 0.8);
      BlockPos var8 = var7.up();
      BlockState var9 = instance.world.getBlockState(var7);
      BlockState var10 = instance.world.getBlockState(var8);
      double var11 = var7.getY() - var6.getY();
      if (!(var11 < 0.6) && !(var11 > 1.25)) {
         boolean var13 = !var9.isAir() && !var9.getCollisionShape(instance.world, var7).isEmpty();
         boolean var14 = var10.isAir() || var10.getCollisionShape(instance.world, var8).isEmpty();
         if (var13 && var14) {
            BlockPos var15 = var7.add((int)Math.signum(var2), 0, (int)Math.signum(var4));
            BlockPos var16 = var15.down();
            BlockState var17 = instance.world.getBlockState(var16);
            return !var17.isAir() && !var17.getCollisionShape(instance.world, var16).isEmpty();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void handle(BlockPos var0, NumberSetting var1, NumberSetting var2) {
      if (var0 != null && instance.player != null && instance.world != null) {
         Vec3d var3 = instance.player.getEyePos();
         Vec3d var4 = Vec3d.ofCenter(var0);
         Vec3d var5 = var4.subtract(var3).normalize();
         float var6 = (float)Math.toDegrees(Math.atan2(-var5.x, var5.z));
         float var7 = (float)(-Math.toDegrees(Math.atan2(var5.y, Math.sqrt(var5.x * var5.x + var5.z * var5.z))));
         long var8 = System.currentTimeMillis();
         float var10 = var2.compute();
         float var11 = (float)Math.sin(var8 / 50.0) * var10;
         float var12 = (float)Math.cos(var8 / 40.0) * var10 * 10.0F;
         float var13 = (float)Math.sin(var8 / 40.0) * var10 * 0.5F;
         float var14 = var11 + var12;
         float var15 = var13 * 5.0F;
         RotationAngles var16 = new RotationAngles(var6 + var14, var7 + var15);
         RotationController.handle(var16, var1.compute(), var1.compute(), 30.0F, 30.0F, 0, 18, false);
      }
   }

   public static void process(BlockPos var0, NumberSetting var1, NumberSetting var2) {
      if (var0 != null && instance.player != null && instance.world != null) {
         Vec3d var3 = instance.player.getEyePos();
         Vec3d var4 = Vec3d.ofCenter(var0);
         Vec3d var5 = var4.subtract(var3).normalize();
         float var6 = (float)Math.toDegrees(Math.atan2(-var5.x, var5.z));
         float var7 = (float)(-Math.toDegrees(Math.atan2(var5.y, Math.sqrt(var5.x * var5.x + var5.z * var5.z))));
         long var8 = System.currentTimeMillis();
         float var10 = (float)Math.sin(var8 / 200.0) * 0.8F;
         RotationAngles var11 = new RotationAngles(var6 + var10 * 0.3F, var7 + var10 * 0.2F);
         RotationController.handle(var11, var1.compute() * 1.5F, var1.compute() * 1.2F, 30.0F, 30.0F, 0, 10, false);
      }
   }

   public static boolean handle(BlockPos var0, float var1) {
      if (var0 != null && instance.player != null && instance.world != null) {
         Vec3d var2 = instance.player.getEyePos();
         Vec3d var3 = Vec3d.ofCenter(var0);
         Vec3d var4 = var3.subtract(var2).normalize();
         float var5 = (float)Math.toDegrees(Math.atan2(-var4.x, var4.z));
         float var6 = (float)(-Math.toDegrees(Math.atan2(var4.y, Math.sqrt(var4.x * var4.x + var4.z * var4.z))));
         float var7 = Math.abs(NumericTransform.execute(var5 - instance.player.getYaw()));
         float var8 = Math.abs(NumericTransform.execute(var6 - instance.player.getPitch()));
         return var7 <= var1 && var8 <= var1;
      } else {
         return false;
      }
   }

   public static boolean handle(BlockPos var0, long var1, NumberSetting var3, NumberSetting var4) {
      if (var0 != null && instance.player != null && instance.world != null) {
         double var5 = instance.player.squaredDistanceTo(var0.getX() + 0.5, var0.getY() + 0.5, var0.getZ() + 0.5);
         if (var5 > var4.compute() * var4.compute()) {
            return false;
         }

         if ((float)(System.currentTimeMillis() - var1) < var3.compute()) {
            return false;
         }

         instance.interactionManager.attackBlock(var0, Direction.UP);
         instance.player.swingHand(Hand.MAIN_HAND);
         return true;
      } else {
         return false;
      }
   }

   public static boolean handle(BlockPos var0, Item var1, long var2) {
      if (var0 != null && instance.player != null && instance.world != null && var1 != null) {
         if (System.currentTimeMillis() - var2 < 600L) {
            return false;
         }

         Hand var4 = null;
         if (instance.player.getOffHandStack().getItem() == var1) {
            var4 = Hand.OFF_HAND;
         } else if (instance.player.getMainHandStack().getItem() == var1) {
            var4 = Hand.MAIN_HAND;
         }

         if (var4 == null) {
            return false;
         }

         BlockPos var5 = var0.up();
         if (!instance.world.getBlockState(var5).isReplaceable()) {
            return false;
         }

         Vec3d var6 = Vec3d.ofCenter(var0).add(Vec3d.of(Direction.UP.getVector()).multiply(0.5));
         BlockHitResult var7 = new BlockHitResult(var6, Direction.UP, var0, false);
         instance.interactionManager.interactBlock(instance.player, var4, var7);
         instance.player.swingHand(var4);
         return true;
      } else {
         return false;
      }
   }

   public static boolean handle() {
      if (instance.player == null || instance.world == null) {
         return false;
      }

      if (instance.player.isOnGround()) {
         return false;
      }

      Vec3d var0 = instance.player.getPos();
      BlockPos var1 = BlockPos.ofFloored(var0.x, var0.y - 1.0, var0.z);
      if (handle(var1, 3)) {
         return true;
      }

      float var2 = instance.player.getYaw();
      double var3 = -Math.sin(Math.toRadians(var2));
      double var5 = Math.cos(Math.toRadians(var2));
      Vec3d var7 = var0.add(var3 * 0.8, 0.0, var5 * 0.8);
      BlockPos var8 = BlockPos.ofFloored(var7.x, var7.y - 1.0, var7.z);
      return handle(var8, 3);
   }

   public static boolean handle(BlockPos var0, int var1) {
      if (instance.world == null) {
         return false;
      }

      int var2 = 0;

      for (int var3 = 0; var3 < var1; var3++) {
         BlockPos var4 = var0.down(var3);
         BlockState var5 = instance.world.getBlockState(var4);
         if (handle(var5, var4) || !var5.isAir() && !var5.getCollisionShape(instance.world, var4).isEmpty()) {
            break;
         }

         var2++;
      }

      return var2 >= var1;
   }

   private static boolean handle(BlockState var0, BlockPos var1) {
      if (var0.isAir()) {
         return false;
      } else {
         Block var2 = var0.getBlock();
         if (var2 == Blocks.FARMLAND) {
            return true;
         } else if (var2 == Blocks.SOUL_SAND) {
            return true;
         } else {
            return var2 == Blocks.DIRT_PATH ? true : !var0.getCollisionShape(instance.world, var1).isEmpty();
         }
      }
   }

   private static void handle(MovementInputEvent var0, float var1, float var2, float var3) {
      float var4 = var0.compute();
      float var5 = var0.resolve();
      double var6 = NumericTransform.execute((float)Math.toDegrees(handle(var1, var4, var5)));
      if (var4 == 0.0F && var5 == 0.0F) {
         var0.handle(1.0F);
         var0.process(var3);
      } else {
         float var8 = 0.0F;
         float var9 = 0.0F;
         float var10 = Float.MAX_VALUE;

         for (float var11 = -1.0F; var11 <= 1.0F; var11++) {
            for (float var12 = -1.0F; var12 <= 1.0F; var12++) {
               if (var11 != 0.0F || var12 != 0.0F) {
                  double var13 = NumericTransform.execute((float)Math.toDegrees(handle(var2, var11, var12)));
                  float var15 = (float)Math.abs(var6 - var13);
                  if (var15 < var10) {
                     var10 = var15;
                     var8 = var11;
                     var9 = var12 + var3;
                  }
               }
            }
         }

         var0.handle(var8);
         var0.process(var9);
      }
   }

   private static double handle(float var0, float var1, float var2) {
      if (var1 == 0.0F && var2 == 0.0F) {
         return 0.0;
      }

      double var3 = Math.atan2(var2, var1);
      return var3 + Math.toRadians(var0);
   }

   record DataRecord(boolean hitSolid, BlockPos hitPos) {
   }
}
