package ru.wild.util.player;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.wild.mixin.acceser.KeyBindingAccessor;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.network.MovementPacketTracker;

public final class MovementPhysics implements MinecraftContext {
   private static float instance;
   private static float data;
   private static boolean context;
   private static boolean config;

   public static double handle(float var0, float var1, float var2) {
      if (var1 < 0.0F) {
         var0 += 180.0F;
      }

      float var3 = 1.0F;
      if (var1 < 0.0F) {
         var3 = -0.5F;
      }

      if (var1 > 0.0F) {
         var3 = 0.5F;
      }

      if (var2 > 0.0F) {
         var0 -= 90.0F * var3;
      }

      if (var2 < 0.0F) {
         var0 += 90.0F * var3;
      }

      return Math.toRadians(var0);
   }

   public static boolean handle() {
      return toggleState.player != null && toggleState.player.input != null && toggleState.player.input.playerInput != null
         ? toggleState.player.input.playerInput.forward()
            || toggleState.player.input.playerInput.backward()
            || toggleState.player.input.playerInput.left()
            || toggleState.player.input.playerInput.right()
         : false;
   }

   public static double[] handle(double var0) {
      float[] var2 = process();
      return handle(var2[0], var2[1], var0);
   }

   public static double[] handle(float var0, float var1, double var2) {
      return handle(var0, var1, toggleState.player.getYaw(), var2);
   }

   public static double[] handle(float var0, float var1, float var2, double var3) {
      if (var0 != 0.0F) {
         if (var1 > 0.0F) {
            var2 += var0 > 0.0F ? -45.0F : 45.0F;
         } else if (var1 < 0.0F) {
            var2 += var0 > 0.0F ? 45.0F : -45.0F;
         }

         var1 = 0.0F;
         var0 = var0 > 0.0F ? 1.0F : -1.0F;
      }

      double var5 = Math.sin(Math.toRadians(var2 + 90.0F));
      double var7 = Math.cos(Math.toRadians(var2 + 90.0F));
      double var9 = var0 * var3 * var7 + var1 * var3 * var5;
      double var11 = var0 * var3 * var5 - var1 * var3 * var7;
      return new double[]{var9, var11};
   }

   public static void handle(MovementInputEvent var0, float var1) {
      if (toggleState.player != null) {
         float var2 = toggleState.player.isFlyingVehicle() ? toggleState.player.getYaw() : MovementPacketTracker.handle(toggleState.player.getYaw());
         float[] var3 = handle(var0.compute(), var0.resolve(), var2, var1);
         var0.handle(var3[0]);
         var0.process(var3[1]);
      }
   }

   public static float[] process() {
      float var0 = 0.0F;
      float var1 = 0.0F;
      if (toggleState.currentScreen != null) {
         return new float[]{0.0F, 0.0F};
      }

      if (handle(toggleState.options.forwardKey)) {
         var0++;
      }

      if (handle(toggleState.options.backKey)) {
         var0--;
      }

      if (handle(toggleState.options.leftKey)) {
         var1++;
      }

      if (handle(toggleState.options.rightKey)) {
         var1--;
      }

      return new float[]{var0, var1};
   }

   private static boolean handle(KeyBinding var0) {
      if (var0 == null) {
         return false;
      }

      try {
         Key var1 = ((KeyBindingAccessor)var0).wild$getBoundKey();
         if (var1 != null && var1.getCategory() == Type.KEYSYM && var1.getCode() != InputUtil.UNKNOWN_KEY.getCode()) {
            return InputUtil.isKeyPressed(toggleState.getWindow().getHandle(), var1.getCode());
         }
      } catch (Throwable var2) {
      }

      return var0.isPressed();
   }

   private static void handle(float var0, float var1) {
      if (toggleState.player != null && toggleState.player.input != null && toggleState.player.input.playerInput != null) {
         boolean var2 = var0 > 0.0F;
         boolean var3 = var0 < 0.0F;
         boolean var4 = var1 > 0.0F;
         boolean var5 = var1 < 0.0F;
         boolean var6 = toggleState.player.input.playerInput.jump();
         boolean var7 = toggleState.player.input.playerInput.sneak();
         boolean var8 = toggleState.player.input.playerInput.sprint();
         toggleState.player.input.playerInput = new PlayerInput(var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public static void handle(float var0, Vec3d var1) {
      float[] var2 = process();
      float var3 = var2[0];
      float var4 = var2[1];
      if (var3 == 0.0F && var4 == 0.0F) {
         resolve();
      } else {
         Box var5 = AttackAura.textureRun.getBoundingBox();
         double var6 = MathHelper.lerp(Math.random(), var5.minX, var5.maxX);
         double var8 = MathHelper.lerp(Math.random(), var5.minY, var5.maxY);
         double var10 = MathHelper.lerp(Math.random(), var5.minZ, var5.maxZ);
         var8 = MathHelper.clamp(var8, AttackAura.textureRun.getY() + 0.2, AttackAura.textureRun.getY() + AttackAura.textureRun.getHeight() - 0.2);
         Vec3d var12 = new Vec3d(var6, var8, var10).subtract(toggleState.player.getEyePos()).normalize();
         float var13 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var12.z, var12.x)) - 90.0);
         float var14 = toggleState.player.isFlyingVehicle() ? toggleState.player.getYaw() : MovementPacketTracker.handle(toggleState.player.getYaw());
         handle(handle(var3, var4, var14, toggleState.player.isFlyingVehicle() ? toggleState.player.getYaw() : var13));
      }
   }

   public static void handle(float var0) {
      float[] var1 = process();
      float var2 = var1[0];
      float var3 = var1[1];
      if (var2 == 0.0F && var3 == 0.0F) {
         resolve();
      } else {
         float var4 = toggleState.player.isFlyingVehicle() ? toggleState.player.getYaw() : MovementPacketTracker.handle(toggleState.player.getYaw());
         handle(handle(var2, var3, var4, toggleState.player.isFlyingVehicle() ? toggleState.player.getYaw() : var0));
      }
   }

   private static void resolve() {
      compute();
   }

   public static void compute() {
      instance = 0.0F;
      data = 0.0F;
      context = false;
      if (config && toggleState.options != null) {
         config = false;
         toggleState.options.forwardKey.setPressed(handle(toggleState.options.forwardKey));
         toggleState.options.backKey.setPressed(handle(toggleState.options.backKey));
         toggleState.options.leftKey.setPressed(handle(toggleState.options.leftKey));
         toggleState.options.rightKey.setPressed(handle(toggleState.options.rightKey));
      }
   }

   private static void handle(float[] var0) {
      float var1 = process(var0[0]);
      float var2 = process(var0[1]);
      config = true;
      toggleState.options.forwardKey.setPressed(var1 > 0.0F);
      toggleState.options.backKey.setPressed(var1 < 0.0F);
      toggleState.options.leftKey.setPressed(var2 > 0.0F);
      toggleState.options.rightKey.setPressed(var2 < 0.0F);
   }

   private static float process(float var0) {
      if (var0 > 0.35F) {
         return 1.0F;
      } else {
         return var0 < -0.35F ? -1.0F : 0.0F;
      }
   }

   public static void process(double var0) {
      if (toggleState.player != null) {
         float var2 = toggleState.player.getYaw();
         double var3 = Math.toRadians(var2);
         double var5 = 0.0;
         double var7 = 0.0;
         Vec2f var9 = toggleState.player.input.getMovementInput();
         float var10 = var9.y;
         float var11 = var9.x;
         if (var10 > 0.0F) {
            var5 -= Math.sin(var3) * var0;
            var7 += Math.cos(var3) * var0;
         }

         if (var10 < 0.0F) {
            var5 += Math.sin(var3) * var0;
            var7 -= Math.cos(var3) * var0;
         }

         if (var11 > 0.0F) {
            var5 += Math.cos(var3) * var0;
            var7 += Math.sin(var3) * var0;
         }

         if (var11 < 0.0F) {
            var5 -= Math.cos(var3) * var0;
            var7 -= Math.sin(var3) * var0;
         }

         if (var11 > 0.0F && var10 > 0.0F) {
            var5 = Math.cos(Math.toRadians(var2 + 45.0F)) * var0;
            var7 = Math.sin(Math.toRadians(var2 + 45.0F)) * var0;
         }

         if (var11 < 0.0F && var10 > 0.0F) {
            var5 = -Math.cos(Math.toRadians(var2 - 45.0F)) * var0;
            var7 = -Math.sin(Math.toRadians(var2 - 45.0F)) * var0;
         }

         if (var11 > 0.0F && var10 < 0.0F) {
            var5 = -Math.cos(Math.toRadians(var2 + 135.0F)) * var0;
            var7 = -Math.sin(Math.toRadians(var2 + 135.0F)) * var0;
         }

         if (var11 < 0.0F && var10 < 0.0F) {
            var5 = Math.cos(Math.toRadians(var2 - 135.0F)) * var0;
            var7 = Math.sin(Math.toRadians(var2 - 135.0F)) * var0;
         }

         toggleState.player.setVelocity(var5, toggleState.player.getVelocity().y, var7);
      }
   }

   public static float[] handle(float var0, float var1, float var2, float var3) {
      if (var0 == 0.0F && var1 == 0.0F) {
         instance = 0.0F;
         data = 0.0F;
         context = false;
         return new float[]{0.0F, 0.0F};
      }

      double var4 = Math.toRadians(MathHelper.wrapDegrees(var3 - var2));
      double var6 = Math.cos(var4);
      double var8 = Math.sin(var4);
      float var10 = (float)(var0 * var6 + var1 * var8);
      float var11 = (float)(var1 * var6 - var0 * var8);
      float var12 = Math.max(Math.abs(var0), Math.abs(var1));
      float var13 = (float)Math.hypot(var10, var11);
      if (var13 != 0.0F) {
         var10 = var10 / var13 * var12;
         var11 = var11 / var13 * var12;
      }

      float var14 = Math.abs(MathHelper.wrapDegrees(var3 - var2));
      float var15 = MathHelper.clamp(0.62F + var14 / 360.0F, 0.62F, 0.88F);
      if (!context) {
         instance = var10;
         data = var11;
         context = true;
      } else {
         instance = instance + (var10 - instance) * var15;
         data = data + (var11 - data) * var15;
      }

      float var16 = (float)Math.hypot(instance, data);
      if (var16 > 1.0E-4F) {
         instance = instance / var16 * var12;
         data = data / var16 * var12;
      }

      if (instance * var10 + data * var11 < 0.0F) {
         instance = var10;
         data = var11;
      }

      return new float[]{instance, data};
   }
   private MovementPhysics() {
   }
}
