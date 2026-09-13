package ru.wild.util.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.wild.core.MinecraftContext;
import ru.wild.util.math.Easings;

public final class PlayerRaycast implements MinecraftContext {
   public static double handle(double var0, double var2, double var4) {
      return Math.min(var4, Math.max(var0, var2));
   }

   public static int handle(int var0, int var1, float var2) {
      return var0 + (int)(var2 * (var1 - var0));
   }

   public static double process(double var0, double var2, double var4) {
      return var0 + var4 * (var2 - var0);
   }

   public static HitResult handle(Vec3d var0, Vec3d var1, ShapeType var2, FluidHandling var3) {
      return toggleState.world.raycast(new RaycastContext(var0, var1, var2, var3, toggleState.player));
   }

   private static double handle(ClientPlayerEntity var0, double var1, double var3) {
      double var5 = var0.getX() - var1;
      double var7 = var0.getZ() - var3;
      return MathHelper.sqrt((float)(var5 * var5 + var7 * var7));
   }

   private static boolean handle(ClientPlayerEntity var0, double var1, double var3, double var5) {
      Vec3d var7 = new Vec3d(var1, var3, var5);
      return toggleState.world != null && handle(var0.getEyePos(), var7, ShapeType.COLLIDER, FluidHandling.NONE).getType() != Type.BLOCK;
   }

   private static boolean handle(ClientPlayerEntity var0, Vec3d var1) {
      Vec3d var2 = new Vec3d(var0.getX(), var0.getEyeY(), var0.getZ());
      return toggleState.world != null && handle(var2, var1, ShapeType.COLLIDER, FluidHandling.NONE).getType() != Type.BLOCK;
   }

   private static boolean handle(ClientPlayerEntity var0, Vec3d var1, float var2) {
      return var2 == 0.0F
         ? handle(var0, var1.x, var1.y, var1.z)
         : handle(var0, var1.x, var1.y, var1.z)
            && handle(var0, var1.x, var1.y + var2, var1.z)
            && handle(var0, var1.x, var1.y - var2, var1.z)
            && handle(var0, var1.x + var2, var1.y, var1.z)
            && handle(var0, var1.x - var2, var1.y, var1.z)
            && handle(var0, var1.x, var1.y, var1.z + var2)
            && handle(var0, var1.x, var1.y, var1.z - var2);
   }

   public static List<Vec3d> handle(Box var0) {
      ArrayList var1 = new ArrayList();
      double var2 = 0.01F;
      byte var4 = 17;
      byte var5 = 5;
      byte var6 = 24;
      byte var7 = 6;
      var0 = var0.offset(-var2, -var2, -var2);
      double[] var8 = new double[]{var0.maxX - var0.minX, var0.maxY - var0.minY, (var0.maxY - var0.minY) / 1.05};
      double[] var9 = new double[]{var0.minX + var8[0] / 2.0, var0.minY, var0.minZ + var8[0] / 2.0};
      double[] var10 = new double[]{var0.minX, var0.minY, var0.minZ};
      double[] var11 = new double[]{var0.maxX, var0.maxY, var0.maxZ};
      float var12 = (float)Math.sqrt(var8[0] * var8[0] + var8[0] * var8[0] + var8[0] * var8[0]) / 2.0F;
      ClientPlayerEntity var13 = toggleState.player;
      if (var13 == null) {
         return null;
      }

      float var14 = (float)(
         (1.0 - Math.min(var13.getPos().distanceTo(new Vec3d(var9[0], var9[1], var9[2])) / 5.0, 1.0))
            * Math.min(var13.getPos().distanceTo(new Vec3d(var9[0], var13.getY(), var9[2])) / 0.6F, 1.0)
      );
      int var15 = handle(var5, var4, var14);
      int var16 = handle(var7, var6, var14);
      float var17 = 0.0F;
      int[] var18 = IntStream.range(0, var15).toArray();
      int var19 = var18.length;

      for (int var20 = 0; var20 < var19; var20++) {
         Integer var21 = var18[var20];
         boolean var22 = var21 == 0 || var21 == var15 - 1;
         double var23 = process(var10[0], var11[0], (float)var21.intValue() / (var15 - 1));
         int[] var25 = IntStream.range(0, var15).toArray();
         int var26 = var25.length;

         for (int var27 = 0; var27 < var26; var27++) {
            Integer var28 = var25[var27];
            boolean var29 = var28 == 0 || var28 == var15 - 1;
            double var30 = process(var10[2], var11[2], (float)var28.intValue() / (var15 - 1));
            int[] var32 = IntStream.range(0, var16).toArray();
            int var33 = var32.length;

            for (int var34 = 0; var34 < var33; var34++) {
               Integer var35 = var32[var34];
               boolean var36 = var35 == 0 || var35 == var16 - 1;
               double var37 = process(var10[1], var11[1], (float)var35.intValue() / (var16 - 1));
               Vec3d var39 = new Vec3d(var23, var37, var30);
               if ((var22 || var29 || var36)
                  && !(var13.getPos().distanceTo(var39.add(0.0, -var13.getEyeHeight(EntityPose.STANDING), 0.0)) < var12)
                  && handle(var13, var39, var17)
                  && !var1.add(var39)) {
                  break;
               }
            }
         }
      }

      return var1;
   }

   private static double handle(Vec3d var0, Vec3d var1) {
      double var2;
      double var4;
      double var6;
      return Math.sqrt((var2 = var0.x - var1.x) * var2 + (var4 = var0.y - var1.y) * var4 + (var6 = var0.z - var1.z) * var6);
   }

   public static Vec3d process(Box var0) {
      return handle(var0, true);
   }

   public static Vec3d handle(Box var0, boolean var1) {
      if (var0 == null) {
         return toggleState.player.getEyePos();
      }

      double[] var2 = new double[]{var0.maxX - var0.minX, var0.maxY - var0.minY, (var0.maxY - var0.minY) / 1.1F};
      double[] var3 = new double[]{var0.minX + var2[0] / 2.0, var0.minY, var0.minZ + var2[0] / 2.0};
      double[] var4 = new double[]{toggleState.player.getY() - var3[1], handle(toggleState.player, var3[0], var3[2])};
      double var5 = handle(Easings.handler.ease((var4[1] - var2[0] / 2.0) / (5.0 + var2[0] / 2.0)), 0.1, 0.95);
      double var7 = handle(var5 * var5, 0.0, 1.0);
      double var9 = handle(var2[2] / 2.0 * var7 + var2[2] / 2.0 * handle(var4[0] + var7, 0.0, 1.0), 0.0, var2[2]);
      Vec3d var11 = new Vec3d(var3[0], var3[1] + var9, var3[2]);
      if (!var1 && !handle(toggleState.player, var11)) {
         var11 = var11.add(0.0, -var9 / 2.0, 0.0);
      }

      if (!(var2[1] <= 1.0) && (var1 || !handle(toggleState.player, var11))) {
         List<Vec3d> var12 = handle(var0);
         float var13 = 1.0F - (float)Math.max(Math.min((var4[1] - 2.0) / 3.0, 1.0), 0.0);
         Vec3d var14 = new Vec3d(toggleState.player.getX(), toggleState.player.getY() + 0.6F + process(var9, var9 / 2.5, var13), toggleState.player.getZ());
         if (var12 != null && var12.size() > 1) {
            var12.sort(Comparator.comparing(var1x -> handle(var14, var1x)));
         }

         return var12 != null && !var12.isEmpty() ? (Vec3d)var12.get(0) : var11;
      } else {
         return var11;
      }
   }
   private PlayerRaycast() {
   }
}
