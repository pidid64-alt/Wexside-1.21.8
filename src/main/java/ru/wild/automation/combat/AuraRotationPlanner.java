package ru.wild.automation.combat;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.math.ResettableTimer;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

public final class AuraRotationPlanner implements MinecraftContext {
   private static final int instance = 41;
   private static final long data = 60L;
   private static final long context = 150L;
   private static final float config = 250.0F;
   private static final float state = 180.0F;
   private static final float cache = 90.0F;
   private static final int output = 15;
   private static final boolean current = false;
   private static final RotationController.Contract active = AuraRotationPlanner::apply;
   private static RotationAngles mode = new RotationAngles(0.0F, 0.0F);
   private static RotationAngles renderer;
   private static LivingEntity handler;
   private static boolean animationDraw;
   private static boolean pointEncode;
   private static boolean animator;
   private static int source;
   private static float target;
   private static float pending;
   private static float previous;
   private static float latest;
   private static boolean summary;

   private AuraRotationPlanner() {
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player == null) {
         prepare();
      } else {
         if (var0 != null && SmartCritsHelper.handle(var0, 1)) {
            renderer = process(var0);
            handler = var0;
            animationDraw = true;
            pointEncode = true;
         }

         update();
      }
   }

   public static void handle() {
      if (toggleState.player == null) {
         prepare();
      } else {
         update();
      }
   }

   public static void process() {
      prepare();
   }

   public static String compute() {
      return !pointEncode ? "IDLE" : (animator ? "SNAP" : "RETURN");
   }

   public static void handle(MovementInputEvent var0) {
      if (pointEncode && toggleState.player != null && ViewRotationCoordinator.instance && !resolve()) {
         float var1 = (float)Math.toRadians(ViewRotationCoordinator.context - toggleState.player.getYaw());
         float var2 = MathHelper.cos(var1);
         float var3 = MathHelper.sin(var1);
         float var4 = var0.compute();
         float var5 = var0.resolve();
         var0.handle(Math.round(var4 * var2 + var5 * var3));
         var0.process(Math.round(var5 * var2 - var4 * var3));
      }
   }

   private static boolean resolve() {
      return AttackAura.textureRun != null
         && (
            AttackAura.outputCollapse.process("Free")
               || AttackAura.outputCollapse.process("Target")
               || AttackAura.outputCollapse.process("Преследование")
               || AttackAura.presetWrite.process("Тестовый")
         );
   }

   private static void update() {
      onTick();
      if (!pointEncode || toggleState.player == null || renderer == null) {
         animationDraw = false;
      } else if (animationDraw || !RotationController.instance.equals(RotationController.Mode.RESET)) {
         RotationAngles var0 = new RotationAngles(toggleState.player);
         boolean var1 = !animationDraw;
         animationDraw = false;
         animator = !var1;
         if (var1) {
            if (source++ > 41) {
               handle(handle(var0, check()));
               execute();
               return;
            }
         } else {
            source = 0;
         }

         handle(var1 ? handle(var0, check(), null) : handle(var0, renderer, handler));
      }
   }

   public static RotationAngles handle(RotationAngles var0, RotationAngles var1, LivingEntity var2) {
      if (toggleState.player == null) {
         return var0;
      } else {
         ResettableTimer var3 = AdaptiveAttackTiming.animate();
         int var4 = AdaptiveAttackTiming.context;
         double var5 = MathHelper.clamp(2.0F - (float)var3.process() / (250.0F + handle(0.0, 10.0)), var4 % 5 / 100.0F + var4 % 2 / 4.0F, 1.0F);
         RotationAngles var7 = process(
            var0, var2 != null ? var1 : handle(var1, MathHelper.lerp(var5, 0.0, mode.instance), MathHelper.lerp(var5, 0.0, mode.data))
         );
         float var8 = var7.instance;
         float var9 = var7.data;
         float var10 = (float)Math.hypot(Math.abs(var8), Math.abs(var9));
         float var11 = Math.max(var10, 1.0E-4F);
         float var12 = Math.abs(var8 / var11) * 180.0F;
         float var13 = Math.abs(var9 / var11) * 180.0F;
         float var14 = MathHelper.clamp(var8, -var12, var12);
         float var15 = MathHelper.clamp(var9, -var13, var13);
         if (!var3.handle(20.0)) {
            return var0;
         } else if (var2 != null && SmartCritsHelper.handle(var2, 0) && var10 < 90.0F) {
            mode = new RotationAngles(var14 < 0.0F ? handle(25.0, 40.0) : -handle(25.0, 40.0), var15 < 0.0F ? handle(10.0, 20.0) : -handle(10.0, 20.0));
            return handle(var0, var14, var15);
         } else {
            return handle(
               var0,
               MathHelper.lerp(var2 == null && var3.handle(150.0) ? 1.0F : handle(0.5F, 0.65F), 0.0F, var14) + target,
               MathHelper.lerp(var2 == null && var3.handle(150.0) ? 1.0F : handle(0.5F, 0.65F), 0.0F, var15) + pending
            );
         }
      }
   }

   private static RotationAngles handle(RotationAngles var0, double var1, double var3) {
      return handle(var0, (float)var1, (float)var3);
   }

   private static RotationAngles handle(RotationAngles var0, float var1, float var2) {
      return new RotationAngles(var0.instance + var1, MathHelper.clamp(var0.data + var2, -90.0F, 90.0F));
   }

   private static RotationAngles handle(RotationAngles var0, RotationAngles var1) {
      return var1;
   }

   private static RotationController.State apply() {
      onTick();
      animationDraw = false;
      if (pointEncode && toggleState.player != null && renderer != null) {
         animator = false;
         RotationAngles var0 = new RotationAngles(toggleState.player);
         if (source++ > 41) {
            RotationAngles var1 = handle(var0, check());
            prepare();
            return new RotationController.State(var1, 360.0F, 360.0F, false);
         } else {
            return new RotationController.State(handle(var0, check(), null), 360.0F, 360.0F, false);
         }
      } else {
         prepare();
         return RotationController.State.handle();
      }
   }

   private static void handle(RotationAngles var0) {
      RotationController.handle(var0, 360.0F, 360.0F, 360.0F, 360.0F, 0, 15, false, active);
   }

   private static void execute() {
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.mode = false;
      RotationController.active = null;
      RotationController.current = 0;
      ViewRotationCoordinator.instance = ViewRotationCoordinator.data;
      prepare();
   }

   private static void prepare() {
      pointEncode = false;
      animationDraw = false;
      renderer = null;
      handler = null;
      source = 0;
      target = 0.0F;
      pending = 0.0F;
      summary = false;
      RotationController.process(active);
   }

   private static RotationAngles check() {
      return new RotationAngles(ViewRotationCoordinator.context, ViewRotationCoordinator.config);
   }

   private static void onTick() {
      boolean var0 = ViewRotationCoordinator.instance;
      float var1 = ViewRotationCoordinator.context;
      float var2 = ViewRotationCoordinator.config;
      target = 0.0F;
      pending = 0.0F;
      previous = var1;
      latest = var2;
      summary = var0;
   }

   private static RotationAngles process(LivingEntity var0) {
      Vec3d var1 = PlayerRaycast.process(var0.getBoundingBox());
      return handle(var1);
   }

   private static RotationAngles handle(Vec3d var0) {
      Vec3d var1 = var0.subtract(toggleState.player.getEyePos());
      return new RotationAngles(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var1.z, var1.x)) - 90.0),
         (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(var1.y, Math.hypot(var1.x, var1.z))))
      );
   }

   private static RotationAngles process(RotationAngles var0, RotationAngles var1) {
      return new RotationAngles(MathHelper.wrapDegrees(var1.instance - var0.instance), MathHelper.wrapDegrees(var1.data - var0.data));
   }

   private static float handle(float var0, float var1) {
      return MathHelper.lerp(new SecureRandom().nextFloat(), var0, var1);
   }

   private static float handle(double var0, double var2) {
      return (float)ThreadLocalRandom.current().nextDouble(var0, var2);
   }
}
