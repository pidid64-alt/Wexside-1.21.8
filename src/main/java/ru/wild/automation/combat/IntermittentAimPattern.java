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

public class IntermittentAimPattern implements MinecraftContext {
   private static float instance;
   private static float data;
   private static int context;
   private static boolean config;
   private static long state;
   private static float cache = 2.5F;
   private static float output = 1.2F;
   private static boolean current;
   private static long active;
   private static long mode = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(8500L, 14000L);

   public static void handle() {
      config = false;
      context = 0;
      current = false;
      state = 0L;
      mode = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(8500L, 14000L);
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null) {
         long var1 = System.currentTimeMillis();
         if (!config) {
            instance = toggleState.player.getYaw();
            data = toggleState.player.getPitch();
            config = true;
         }

         if (var1 >= state) {
            cache = ClientMathUtil.resolve(1.6F, 4.6F);
            output = ClientMathUtil.resolve(0.8F, 2.4F);
            state = var1 + ThreadLocalRandom.current().nextLong(140L, 260L);
         }

         if (!current && var1 >= mode) {
            current = true;
            active = var1 + ThreadLocalRandom.current().nextLong(170L, 290L);
            mode = var1 + ThreadLocalRandom.current().nextLong(7800L, 13500L);
         }

         if (current && var1 >= active) {
            current = false;
         }

         Vec3d var3 = EntityAimGeometry.process(var0);
         float var4 = (float)Math.toDegrees(Math.atan2(-var3.x, var3.z));
         float var5 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var3.y, Math.hypot(var3.x, var3.z))), -90.0, 90.0);
         float[] var6 = AttackAura.process(var0);
         float[] var7 = new float[]{var6[0], var6[1], var6[0] + var6[1]};
         boolean var8 = AdaptiveAttackTiming.handle(var0, false, true, true, -45L, var7);
         if (var8) {
            context = 2;
         }

         boolean var9 = context > 0;
         if (context > 0) {
            context--;
         }

         float var10 = MathHelper.wrapDegrees(var4 - instance);
         float var11 = var5 - data;
         float var12 = var9 ? MathHelper.clamp(var10 * 0.92F, -56.0F, 56.0F) : MathHelper.clamp(var10 * 0.34F, -17.0F, 17.0F);
         float var13 = var9 ? MathHelper.clamp(var11 * 0.84F, -46.0F, 46.0F) : MathHelper.clamp(var11 * 0.3F, -12.0F, 12.0F);
         instance += var12;
         data += var13;
         float var14 = (float)Math.sin(var1 / 65.0);
         float var15 = (float)Math.cos(var1 / 48.0);
         instance = instance + var14 * cache;
         data = data + var15 * output;
         if (current) {
            data = MathHelper.clamp(data - ClientMathUtil.resolve(7.5F, 12.5F), -89.0F, 89.0F);
         }

         float var16 = instance;
         float var17 = MathHelper.clamp(data, -89.5F, 89.5F);
         float var18 = var9 ? ClientMathUtil.resolve(66.0F, 94.0F) : ClientMathUtil.resolve(26.0F, 44.0F);
         float var19 = var9 ? ClientMathUtil.resolve(104.0F, 146.0F) : ClientMathUtil.resolve(34.0F, 58.0F);
         RotationController.handle(
            new RotationAngles(var16, var17),
            var18,
            var19,
            ClientMathUtil.handle(30, 48),
            ClientMathUtil.handle(16, 34),
            ClientMathUtil.handle(0, 3),
            15,
            false
         );
      }
   }
}
