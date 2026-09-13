package ru.wild.automation.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.api.event.CameraRotationEvent;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;

public class SensitivityAimPattern implements MinecraftContext {
   private static long instance;

   public static void handle(LivingEntity var0, CameraRotationEvent var1) {
      if (var0 != null && toggleState.player != null && toggleState.world != null) {
         Vec3d var2 = var0.getPos()
            .add(0.0, MathHelper.clamp(toggleState.player.getEyePos().y - var0.getY(), 0.0, 1.0), 0.0)
            .subtract(toggleState.player.getEyePos())
            .normalize();
         float var3 = (float)Math.toDegrees(Math.atan2(-var2.x, var2.z));
         float var4 = toggleState.player.getYaw();
         float var5 = MathHelper.wrapDegrees(var3 - var4);
         float var6 = MathHelper.clamp(AttackAura.profileDraw.compute(), 0.02F, 0.4F);
         float var7 = handle();
         float var8 = 1.0F - (float)Math.pow(1.0F - var6, var7);
         float var9 = var4 + var5 * var8;
         toggleState.player.setYaw(var9);
         toggleState.player.headYaw = var9;
         var1.handle(var9);
      }
   }

   private static float handle() {
      long var0 = System.nanoTime();
      if (instance == 0L) {
         instance = var0;
         return 1.0F;
      } else {
         float var2 = (float)(var0 - instance) / 1.6666667E7F;
         instance = var0;
         return MathHelper.clamp(var2, 0.25F, 4.0F);
      }
   }
}
