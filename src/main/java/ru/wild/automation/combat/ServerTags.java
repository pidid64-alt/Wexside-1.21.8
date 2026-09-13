package ru.wild.automation.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.api.module.ModuleRoles;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.core.manager.FeatureManager;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;

@ModuleRoles(compute = "lichoday")
public final class ServerTags implements MinecraftContext {
   private static final double instance = 115.0;
   private static final float data = 4.5F;
   private static final float context = 3.0F;
   private static final RotationController.Contract config = ServerTags::apply;
   private static final ServerTags.State state = new ServerTags.State();

   private ServerTags() {
   }

   public static String handle() {
      return "FTTESTT";
   }

   public static boolean process() {
      return FeatureManager.handle(ServerTags.class.getAnnotation(ModuleRoles.class));
   }

   public static void handle(LivingEntity var0) {
      if (toggleState.player != null && var0 != null) {
         state.instance = true;
         RotationAngles var1 = new RotationAngles(toggleState.player);
         RotationAngles var2 = process(var0);
         handle(handle(var1, var2, var0));
      }
   }

   public static void compute() {
   }

   public static void resolve() {
      if (toggleState.player == null) {
         update();
      } else if (state.instance && !RotationController.instance.equals(RotationController.Mode.RESET)) {
         RotationAngles var0 = new RotationAngles(toggleState.player);
         RotationAngles var1 = new RotationAngles(ViewRotationCoordinator.context, ViewRotationCoordinator.config);
         if (var0.handle(var1) < 1.0F) {
            RotationController.instance = RotationController.Mode.IDLE;
            RotationController.cache = 0;
            RotationController.mode = false;
            RotationController.active = null;
            RotationController.current = 0;
            RotationController.process(config);
            ViewRotationCoordinator.instance = ViewRotationCoordinator.data;
            update();
         } else {
            handle(handle(var0, var1, null));
         }
      }
   }

   public static void update() {
      state.process();
      RotationController.process(config);
   }

   static RotationAngles handle(RotationAngles var0, RotationAngles var1, LivingEntity var2) {
      if (toggleState.player == null) {
         return var0;
      }

      boolean var3 = var2 != null;
      if (var3 && SmartCritsHelper.handle(var2, 1)) {
         state.data = -1L;
         double var13 = state.handle();
         float var14 = handle(var13) + (float)(Math.sin(var13 / 28.0) * handle(2.5F, 5.5F));
         float var15 = process(var13) + (float)(Math.cos(var13 / 21.0) * handle(1.5F, 3.5F));
         return handle(var0, var1, 145.0F, 135.0F, 0.9F, var14, var15);
      }

      RotationAngles var4 = new RotationAngles(toggleState.player.getYaw(), toggleState.player.getPitch());
      RotationAngles var5 = handle(var0, var4);
      float var6 = Math.max((float)Math.hypot(Math.abs(var5.instance), Math.abs(var5.data)), 1.0E-4F);
      double var7 = state.handle();
      float var9 = handle(var7) + (float)(handle(8, 22) * Math.sin(var7 / 72.0) + handle(2, 6) * Math.sin(var7 / 19.0));
      float var10 = process(var7) + (float)(handle(8, 13) * Math.cos(var7 / 38.0) + handle(1, 4) * Math.cos(var7 / 16.0));
      if (!var3) {
         if (state.data < 0L) {
            state.data = state.handle();
         }

         float var11 = 1.0F - MathHelper.clamp((float)(state.handle() - state.data) / 1000.0F, 0.0F, 1.0F);
         var9 *= var11;
         var10 *= var11;
      } else {
         state.data = -1L;
      }

      float var16 = Math.abs(var5.instance / var6) * (AdaptiveAttackTiming.process(535L) ? 45.0F : 0.0F);
      float var12 = Math.abs(var5.data / var6) * (AdaptiveAttackTiming.process(535L) ? 45.0F : 0.0F);
      return new RotationAngles(
         handle(0.85F, var0.instance, var0.instance + MathHelper.clamp(var5.instance, -var16, var16) + var9),
         MathHelper.clamp(handle(0.85F, var0.data, var0.data + MathHelper.clamp(var5.data, -var12, var12) + var10), -90.0F, 90.0F)
      );
   }

   private static RotationController.State apply() {
      if (state.instance && toggleState.player != null) {
         RotationAngles var0 = new RotationAngles(toggleState.player);
         RotationAngles var1 = new RotationAngles(ViewRotationCoordinator.context, ViewRotationCoordinator.config);
         if (var0.handle(var1) < 1.0F) {
            update();
            return RotationController.State.handle();
         } else {
            RotationAngles var2 = handle(var0, var1, 360.0F, 360.0F, 1.0F, 0.0F, 0.0F);
            return new RotationController.State(var2, 360.0F, 360.0F, false);
         }
      } else {
         update();
         return RotationController.State.handle();
      }
   }

   private static RotationAngles process(LivingEntity var0) {
      Vec3d var1 = PlayerRaycast.process(var0.getBoundingBox());
      Vec3d var2 = var1.subtract(toggleState.player.getEyePos());
      return new RotationAngles(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0),
         (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))), -90.0, 90.0)
      );
   }

   private static void handle(RotationAngles var0) {
      RotationController.handle(var0, 360.0F, 360.0F, 45.0F, 45.0F, 0, 15, false, config);
   }

   private static float handle(double var0) {
      return (float)Math.sin(var0 / 115.0) * 4.5F;
   }

   private static float process(double var0) {
      return (float)Math.cos(var0 / 115.0) * 3.0F;
   }

   private static RotationAngles handle(RotationAngles var0, RotationAngles var1, float var2, float var3, float var4, float var5, float var6) {
      RotationAngles var7 = handle(var0, var1);
      float var8 = Math.max((float)Math.hypot(Math.abs(var7.instance), Math.abs(var7.data)), 1.0E-4F);
      float var9 = Math.abs(var7.instance / var8) * var2;
      float var10 = Math.abs(var7.data / var8) * var3;
      return new RotationAngles(
         handle(var4, var0.instance, var0.instance + MathHelper.clamp(var7.instance, -var9, var9) + var5),
         MathHelper.clamp(handle(var4, var0.data, var0.data + MathHelper.clamp(var7.data, -var10, var10) + var6), -90.0F, 90.0F)
      );
   }

   private static RotationAngles handle(RotationAngles var0, RotationAngles var1) {
      return new RotationAngles(
         MathHelper.wrapDegrees(var1.instance - var0.instance), MathHelper.clamp(MathHelper.wrapDegrees(var1.data - var0.data), -90.0F, 90.0F)
      );
   }

   private static float handle(float var0, float var1) {
      return (float)ThreadLocalRandom.current().nextDouble(var0, var1);
   }

   private static int handle(int var0, int var1) {
      return ThreadLocalRandom.current().nextInt(var0, var1 + 1);
   }

   private static float handle(float var0, float var1, float var2) {
      return var1 + var0 * (var2 - var1);
   }

   static final class State {
      boolean instance;
      long data = -1L;

      long handle() {
         return System.currentTimeMillis();
      }

      void process() {
         this.instance = false;
         this.data = -1L;
      }
   }
}
