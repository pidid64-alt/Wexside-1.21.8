package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

public class AcceleratedAimPattern implements MinecraftContext {
   static float instance;
   static float data = 0.0F;

   public static void handle(LivingEntity var0, boolean var1, float var2, boolean var3) {
      long var4 = System.currentTimeMillis();
      if (!AttackAura.dataValidate && var4 - AttackAura.actionRead >= AttackAura.configCollapse) {
         AttackAura.dataValidate = true;
         AttackAura.scaleRender = var4;
         AttackAura.clientRefresh = ThreadLocalRandom.current().nextInt(270, 390);
         AttackAura.actionRead = var4;
         AttackAura.configCollapse = ThreadLocalRandom.current().nextLong(16500L, 23200L);
      }

      boolean var6 = false;
      if (AttackAura.dataValidate && var4 - AttackAura.scaleRender >= AttackAura.clientRefresh) {
         AttackAura.dataValidate = false;
      }

      if (var4 - AttackAura.scaleRender >= AttackAura.clientRefresh + 60L) {
         var6 = true;
      }

      Vec3d var7 = PlayerRaycast.process(var0.getBoundingBox()).subtract(toggleState.player.getEyePos());
      float var8 = ViewRotationCoordinator.context;
      if (var1 && EntityAimGeometry.process(var0).length() < var2 && !var3) {
         instance = ClientMathUtil.resolve(6.0F, 7.0F);
      }

      float var9 = (float)EntityAimGeometry.apply(var0);
      float var10 = 360.0F;
      float var11 = ClientMathUtil.resolve(22.0F, 29.0F);
      float var12 = 0.0F;
      float var13 = ClientMathUtil.resolve(0.0F, 3.5F);
      float var14 = (float)Math.cos(System.currentTimeMillis() / 30.0);
      float var15 = (float)Math.sin(System.currentTimeMillis() / 50.0);
      if (instance > 0.0F && Math.abs(var9) < var10) {
         var11 = ClientMathUtil.resolve(90.0F, 120.0F);
         var8 = (float)Math.toDegrees(Math.atan2(-var7.x, var7.z));
         var12 = (var14 + var15) * RotationEngine.handle(1.0F, 6.0F);
         instance--;
      }

      float var16 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7.y, Math.hypot(var7.x, var7.z))), -90.0, 90.0);
      float var17 = var14 * RotationEngine.handle(11.0F, 64.0F) + var12;
      float var18 = var15 * RotationEngine.handle(4.0F, 17.0F) + var12;
      float var19 = var16;
      if (var1 && EntityAimGeometry.process(var0).length() < var2 && !var3) {
         data = var19;
      }

      float var20 = AttackAura.dataValidate ? -ClientMathUtil.resolve(85.0F, 90.0F) : var19;
      RotationAngles var21 = new RotationAngles(var8 + var17, var20 + var18);
      RotationController.handle(
         var21,
         var11,
         AttackAura.dataValidate ? RotationEngine.handle(60.0F, 170.0F) : (var6 ? RotationEngine.handle(60.0F, 170.0F) : RotationEngine.handle(6.0F, 8.0F)),
         25.0F,
         25.0F,
         0,
         15,
         false
      );
   }
}
