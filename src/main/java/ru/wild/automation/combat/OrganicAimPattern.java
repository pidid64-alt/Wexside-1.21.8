package ru.wild.automation.combat;

import java.security.SecureRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.util.player.CombatRaycast;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.RotationAngles;

public final class OrganicAimPattern implements MinecraftContext {
   private static final SecureRandom instance = new SecureRandom();
   private static float data = 24.0F;
   private static float context = 6.0F;

   private OrganicAimPattern() {
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null && var0 != null) {
         Vec3d var1 = EntityAimGeometry.compute(var0);
         float var2 = handle(35.0F, 40.0F);
         float var3 = handle(4.0F, 8.0F);
         float var4 = (float)Math.toDegrees(Math.atan2(-var1.x, var1.z));
         float var5 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var1.y, Math.hypot(var1.x, var1.z))), -90.0, 90.0);
         float var6 = 0.3F;
         if (CombatRaycast.handle(toggleState.player.getYaw(), toggleState.player.getPitch(), 5.0, var0)) {
            var2 = 0.0F;
            var3 = 0.0F;
         }

         data = data + (var2 - data) * var6;
         context = context + (var3 - context) * var6;
         RotationController.handle(new RotationAngles(var4, var5), data, context, handle(360.0, 390.0), handle(360.0, 390.0), (int)handle(3.0, 5.0), 1, false);
      }
   }

   public static void handle() {
   }

   public static void process() {
      data = 24.0F;
      context = 6.0F;
   }

   private static float handle(float var0, float var1) {
      return var1 + (var0 - var1) * instance.nextFloat();
   }

   private static float handle(double var0, double var2) {
      return (float)(var0 + (var2 - var0) * Math.random());
   }
}
