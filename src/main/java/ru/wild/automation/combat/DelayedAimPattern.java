package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.RotationAngles;

public class DelayedAimPattern implements MinecraftContext {
   static float instance;
   static float data;
   static float context;
   public static long config = 0L;
   public static long state = ThreadLocalRandom.current().nextLong(90000L, 180000L);
   public static boolean cache = false;
   public static long output = 0L;
   public static int current = 0;

   public static void handle(LivingEntity var0) {
      long var1 = System.currentTimeMillis();
      if (!cache && var1 - config >= state) {
         cache = true;
         output = var1;
         current = ThreadLocalRandom.current().nextInt(300, 400);
         config = var1;
         state = ThreadLocalRandom.current().nextLong(9100L, 11200L);
      }

      boolean var3 = false;
      if (cache && var1 - output >= current) {
         cache = false;
      }

      if (var1 - output >= current + 70L) {
         var3 = true;
      }

      Vec3d var4 = EntityAimGeometry.process(var0);
      float var5 = (float)Math.toDegrees(Math.atan2(-var4.x, var4.z));
      float var6 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var4.y, Math.hypot(var4.x, var4.z))), -90.0, 90.0);
      float[] var7 = AttackAura.process(var0);
      float[] var8 = new float[]{var7[0], var7[1], var7[0] + var7[1]};
      boolean var9 = AdaptiveAttackTiming.handle(var0, false, true, true, -50L, var8);
      float var10 = toggleState.player.getYaw();
      float var11 = Math.abs(MathHelper.wrapDegrees(var10 - var5));
      float var12 = ClientMathUtil.resolve(62.0F, 84.0F);
      float var13 = !var3 ? ClientMathUtil.resolve(120.0F, 170.0F) : ClientMathUtil.resolve(9.0F, 13.0F);
      if (var9) {
         instance = 2.0F;
      }

      boolean var14 = false;
      if (instance > 0.0F) {
         var14 = true;
         instance--;
      }

      float var15 = (float)Math.cos(System.currentTimeMillis() / 40.0);
      float var16 = (float)Math.sin(System.currentTimeMillis() / 70.0);
      if (var14) {
         data = var5;
         context = var6;
      }

      float var17 = var15 * ClientMathUtil.resolve(9.0F, 17.0F);
      float var18 = var16 * ClientMathUtil.resolve(4.0F, 13.0F);
      float var19 = cache ? -ClientMathUtil.resolve(85.0F, 90.0F) : context;
      RotationController.handle(
         new RotationAngles(data + var17, var19 + var18),
         var12,
         var13,
         ClientMathUtil.handle(35, 45),
         ClientMathUtil.handle(19, 45),
         ClientMathUtil.handle(0, 3),
         15,
         false
      );
   }
}
