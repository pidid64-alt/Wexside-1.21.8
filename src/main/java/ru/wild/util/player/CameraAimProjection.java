package ru.wild.util.player;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.wild.mixin.acceser.GameRendererAccessor;
import ru.wild.core.MinecraftContext;
import ru.wild.util.render.RoundedRectRenderer;

public final class CameraAimProjection implements MinecraftContext {
   private CameraAimProjection() {
   }

   public static double handle(LivingEntity var0) {
      if (toggleState.player != null && var0 != null) {
         Vec3d var1 = toggleState.player.getEyePos();
         Vec3d var2 = handle();
         Box var3 = var0.getBoundingBox();
         double var4 = var3.minX;
         double var6 = var3.minY;
         double var8 = var3.minZ;
         double var10 = var3.maxX;
         double var12 = var3.maxY;
         double var14 = var3.maxZ;
         double var16 = (var4 + var10) * 0.5;
         double var18 = (var8 + var14) * 0.5;
         double var20 = 180.0;

         for (int var22 = 0; var22 < 2; var22++) {
            double var23 = var22 == 0 ? var4 : var10;

            for (int var25 = 0; var25 < 2; var25++) {
               double var26 = var25 == 0 ? var6 : var12;

               for (int var28 = 0; var28 < 2; var28++) {
                  double var29 = var28 == 0 ? var8 : var14;
                  var20 = Math.min(var20, handle(var1, var2, new Vec3d(var23, var26, var29)));
               }
            }
         }

         var20 = Math.min(var20, handle(var1, var2, var3.getCenter()));
         var20 = Math.min(var20, handle(var1, var2, new Vec3d(var16, var0.getEyeY(), var18)));
         var20 = Math.min(var20, handle(var1, var2, new Vec3d(var16, var12, var18)));
         var20 = Math.min(var20, handle(var1, var2, new Vec3d(var16, var6, var18)));
         var20 = Math.min(var20, handle(var1, var2, new Vec3d(var4, handle(var3), var18)));
         var20 = Math.min(var20, handle(var1, var2, new Vec3d(var10, handle(var3), var18)));
         var20 = Math.min(var20, handle(var1, var2, new Vec3d(var16, handle(var3), var8)));
         return Math.min(var20, handle(var1, var2, new Vec3d(var16, handle(var3), var14)));
      } else {
         return 180.0;
      }
   }

   public static boolean handle(LivingEntity var0, float var1) {
      return var0 != null && !(var1 <= 0.0F) ? handle(var0) <= var1 * 0.5F : false;
   }

   public static float handle(float var0, int var1) {
      if (toggleState != null && toggleState.gameRenderer != null && var1 > 0 && !(var0 <= 0.0F)) {
         Camera var2 = toggleState.gameRenderer.getCamera();
         float var3 = ((GameRendererAccessor)toggleState.gameRenderer).invokeGetFov(var2, 1.0F, true);
         float var4 = var1 * 0.5F;
         float var5 = (float)Math.toRadians(var3 * 0.5F);
         float var6 = (float)Math.toRadians(var0 * 0.5F);
         return var5 <= 1.0E-4F ? 0.0F : var4 / (float)Math.tan(var5) * (float)Math.tan(var6);
      } else {
         return 0.0F;
      }
   }

   public static void handle(RoundedRectRenderer var0, float var1, int var2, int var3) {
      if (var0 != null && var2 > 0 && var3 > 0) {
         float var4 = handle(var1, var3);
         if (!(var4 <= 1.0F)) {
            float var5 = var2 * 0.5F;
            float var6 = var3 * 0.5F;
            int var7 = handle(255, 255, 255, 210);
            var0.handle(var5 - var4, var6 - var4, var4 * 2.0F, var4 * 2.0F, var4, var7, 1.2F);
         }
      }
   }

   private static Vec3d handle() {
      if (toggleState.gameRenderer != null && toggleState.gameRenderer.getCamera() != null) {
         Camera var0 = toggleState.gameRenderer.getCamera();
         Vector3f var1 = new Vector3f(0.0F, 0.0F, -1.0F);
         new Quaternionf(var0.getRotation()).transform(var1);
         return new Vec3d(var1.x, var1.y, var1.z).normalize();
      } else {
         return toggleState.player != null ? toggleState.player.getRotationVec(1.0F).normalize() : new Vec3d(0.0, 0.0, 1.0);
      }
   }

   private static double handle(Box var0) {
      return (var0.minY + var0.maxY) * 0.5;
   }

   private static double handle(Vec3d var0, Vec3d var1, Vec3d var2) {
      Vec3d var3 = var2.subtract(var0);
      double var4 = var3.length();
      if (var4 < 1.0E-6) {
         return 0.0;
      }

      var3 = var3.multiply(1.0 / var4);
      double var6 = MathHelper.clamp(var1.dotProduct(var3), -1.0, 1.0);
      return Math.toDegrees(Math.acos(var6));
   }

   private static int handle(int var0, int var1, int var2, int var3) {
      return (var3 & 0xFF) << 24 | (var0 & 0xFF) << 16 | (var1 & 0xFF) << 8 | var2 & 0xFF;
   }
}
