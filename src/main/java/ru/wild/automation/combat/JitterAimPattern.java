package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.RotationAngles;

public final class JitterAimPattern implements MinecraftContext {
   private static final double instance = 0.27;
   private static final double data = 0.24;
   private static final double context = 0.26;
   private static final double config = 0.2;
   private static final double state = 0.15;
   private static final double cache = 0.2;
   private static final double output = 0.09;
   private static final double current = 0.2;
   private static final double active = 0.12;
   private static final double mode = 0.4;
   private static final long renderer = 80L;
   private static final int handler = 200;
   private static final double animationDraw = 3.0;
   private static final double pointEncode = 0.35;
   private static final double animator = 1.0;
   private static final float source = 38.0F;
   private static final float target = 43.0F;
   private static final float pending = 3.0F;
   private static final float previous = 5.0F;
   private static final float latest = 55.0F;
   private static final float summary = 70.0F;
   private static final float matrixBlend = 8.0F;
   private static final float vectorMatch = 12.0F;
   private static final float itemProject = 30.0F;
   private static final long responseCompute = 100L;
   private static final int providerFetch = 1;
   private static final int profileDraw = 15;
   private static final long vectorPerform = 50L;
   private static final float eventAttach = 20.0F;
   private static final float serverRead = 4.0F;
   private static long positionAdvance;
   private static long frameCheck;
   private static long moduleCollect = -1L;
   private static double providerClose = 0.27;
   private static double presetSave = 0.24;
   private static double windowConvert = 0.26;

   private JitterAimPattern() {
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null && var0 != null) {
         resolve();
         long var1 = System.currentTimeMillis();
         if (moduleCollect < 0L) {
            moduleCollect = var1;
         }

         float var3 = MathHelper.clamp((float)(var1 - moduleCollect) / 50.0F, 0.0F, 4.0F);
         moduleCollect = var1;
         if (var3 <= 0.0F) {
            var3 = 0.05F;
         }

         float var4 = AttackAura.source.compute();
         double var5 = var0.getWidth();
         double var7 = var0.getHeight();
         double var9 = toggleState.player.getEyePos().distanceTo(var0.getPos().add(0.0, var7 * 0.5, 0.0));
         double var11 = MathHelper.clamp(var9 / Math.max(3.0, var4), 0.35, 1.0);
         double var13 = (providerClose - 0.4) * var5 * var11;
         double var15 = (windowConvert - 0.4) * var5 * var11;
         Vec3d var17 = var0.getPos().add(var13, var7 * presetSave, var15);
         Vec3d var18 = var17.subtract(toggleState.player.getEyePos());
         float var19 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var18.z, var18.x)) - 90.0);
         float var20 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var18.y, Math.hypot(var18.x, var18.z))), -89.0, 89.0);
         boolean var21 = var1 < frameCheck;
         float var22 = ClientMathUtil.compute(-3.0F, 3.0F) + (float)(3.0 * Math.cos(var1 / 40.0));
         float var23 = ClientMathUtil.compute(-1.0F, 1.0F) + (float)(4.0 * Math.sin(var1 / 240.0));
         float var24 = 0.0F;
         if (var21) {
            var24 = ClientMathUtil.compute(-3.0F, 4.0F) + (float)(2.0 * Math.sin(var1 / 30.0));
         }

         float var25 = ClientMathUtil.compute(38.0F, 43.0F);
         float var26 = ClientMathUtil.compute(3.0F, 5.0F);
         if (var21) {
            var25 = ClientMathUtil.compute(55.0F, 70.0F);
            var26 = ClientMathUtil.compute(8.0F, 12.0F);
         }

         RotationController.handle(new RotationAngles(var19 + var22 + var24, var20 + var23), var25 * var3, var26 * var3, 30.0F, 30.0F, 1, 15, false);
      }
   }

   public static void handle() {
      frameCheck = System.currentTimeMillis() + 100L;
   }

   public static void process() {
      compute();
   }

   public static void compute() {
      positionAdvance = 0L;
      frameCheck = 0L;
      moduleCollect = -1L;
      providerClose = 0.27;
      presetSave = 0.24;
      windowConvert = 0.26;
   }

   private static void resolve() {
      long var0 = System.currentTimeMillis();
      if (var0 >= positionAdvance) {
         ThreadLocalRandom var2 = ThreadLocalRandom.current();
         providerClose = 0.2 + var2.nextDouble() * 0.15;
         presetSave = 0.2 + var2.nextDouble() * 0.09;
         windowConvert = 0.2 + var2.nextDouble() * 0.12;
         positionAdvance = var0 + 80L + var2.nextInt(200);
      }
   }
}
