package ru.wild.automation;

import net.minecraft.util.math.MathHelper;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.automation.combat.AuraRotationPlanner;
import ru.wild.core.ClientComponent;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.player.CameraRotationInterpolator;
import ru.wild.util.player.MouseSensitivityQuantizer;
import ru.wild.util.player.RotationAngles;

public class RotationController extends ClientComponent {
   private static RotationController.PrimaryState renderer;
   private static RotationController.Contract handler;
   public static RotationController.Mode instance = RotationController.Mode.IDLE;
   public static float data;
   public static float context;
   public static float config;
   public static float state;
   public static int cache;
   public static int output;
   public static int current;
   public static RotationAngles active;
   public static boolean mode;
   public static void handle(RotationController.PrimaryState var0, Runnable var1) {
      renderer = var0;
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         var1.run();
         var4 = false;
      } finally {
         if (var4) {
            renderer = null;
         }
      }

      renderer = null;
   }

   public static boolean handle() {
      return !instance.equals(RotationController.Mode.IDLE);
   }

   private void compute() {
      if (handler != null) {
         RotationController.State var2 = handler.nextStep();
         if (var2 != null && !var2.config && var2.instance != null) {
            handle(var2.instance, var2.data, var2.context);
         } else {
            this.process();
         }
      } else {
         RotationAngles var1 = new RotationAngles(ViewRotationCoordinator.context, ViewRotationCoordinator.config);
         if (handle(var1, config, state)) {
            this.process();
         }
      }
   }

   @EventHandler
   public void handle(MovementInputEvent var1) {
      AuraRotationPlanner.handle(var1);
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (instance.equals(RotationController.Mode.AIM) && current > output) {
         if (mode) {
            this.process();
         } else {
            instance = RotationController.Mode.RESET;
         }
      }

      if (instance.equals(RotationController.Mode.RESET)) {
         this.compute();
      }

      current++;
   }

   public static void handle(RotationAngles var0, float var1, float var2, float var3, float var4, int var5, int var6, boolean var7) {
      process(var0, var1, var2, var3, var4, var5, var6, var7, null);
   }

   public static void handle(
      RotationAngles var0, float var1, float var2, float var3, float var4, int var5, int var6, boolean var7, RotationController.Contract var8
   ) {
      process(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private static void process(
      RotationAngles var0, float var1, float var2, float var3, float var4, int var5, int var6, boolean var7, RotationController.Contract var8
   ) {
      if (renderer != null) {
         var1 = renderer.instance;
         var2 = renderer.data;
         var3 = renderer.context;
         var4 = renderer.config;
         var7 = renderer.state;
      }

      if (cache <= var6) {
         if (CameraRotationInterpolator.instance && var0 != null) {
            CameraRotationInterpolator.handle();
            float var9 = var0.instance + CameraRotationInterpolator.data;
            float var10 = MathHelper.clamp(
               MathHelper.clamp(var0.data + CameraRotationInterpolator.context, CameraRotationInterpolator.config, CameraRotationInterpolator.state),
               -90.0F,
               90.0F
            );
            var0 = new RotationAngles(var9, var10);
         }

         if (var7) {
            ViewRotationCoordinator.instance = false;
            if (toggleState.player != null) {
               ViewRotationCoordinator.context = toggleState.player.getYaw();
               ViewRotationCoordinator.config = toggleState.player.getPitch();
            }
         } else if (instance.equals(RotationController.Mode.IDLE)) {
            ViewRotationCoordinator.instance = true;
         }

         data = var1;
         context = var2;
         config = var3;
         state = var4;
         output = var5;
         cache = var6;
         instance = RotationController.Mode.AIM;
         mode = var7;
         handler = var8;
         active = var0;
         handle(var0, var1, var2);
      }
   }

   public static void handle(RotationAngles var0, float var1, float var2, float var3, float var4, int var5, int var6) {
      handle(var0, var1, var2, var3, var4, var5, var6, true);
   }

   public static void handle(RotationAngles var0, float var1, float var2, int var3, int var4) {
      handle(var0, var1, var1, var2, var2, var3, var4, false);
   }

   public static void handle(RotationController.Contract var0) {
      if (var0 != null && handler == var0 && !instance.equals(RotationController.Mode.IDLE)) {
         instance = RotationController.Mode.RESET;
         mode = false;
         current = 0;
      }
   }

   public static void process(RotationController.Contract var0) {
      if (handler == var0) {
         handler = null;
      }
   }

   static boolean handle(RotationAngles var0, float var1, float var2) {
      if (toggleState.player == null) {
         return false;
      }

      RotationAngles var3 = new RotationAngles(toggleState.player);
      float var4 = NumericTransform.execute(var0.instance - var3.instance);
      float var5 = var0.data - var3.data;
      float var6 = Math.min(Math.abs(var4), var1);
      float var7 = Math.min(Math.abs(var5), var2);
      toggleState.player
         .setYaw(toggleState.player.headYaw = toggleState.player.headYaw + MouseSensitivityQuantizer.handle(MathHelper.clamp(var4, -var6, var6)));
      toggleState.player
         .setPitch(MathHelper.clamp(toggleState.player.getPitch() + MouseSensitivityQuantizer.handle(MathHelper.clamp(var5, -var7, var7)), -90.0F, 90.0F));
      current = 0;
      return new RotationAngles(toggleState.player).handle(var0) < 1.0F;
   }

   public void process() {
      instance = RotationController.Mode.IDLE;
      cache = 0;
      mode = false;
      handler = null;
      ViewRotationCoordinator.instance = ViewRotationCoordinator.data;
   }

   @FunctionalInterface
   public interface Contract {
      RotationController.State nextStep();
   }

   public enum Mode {
      AIM,
      RESET,
      IDLE;
   }

   public static final class PrimaryState {
      public final float instance;
      public final float data;
      public final float context;
      public final float config;
      public final boolean state;

      public PrimaryState(float var1, float var2, float var3, float var4, boolean var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
      }
   }

   public static final class State {
      public final RotationAngles instance;
      public final float data;
      public final float context;
      public final boolean config;

      public State(RotationAngles var1, float var2, float var3, boolean var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }

      public static RotationController.State handle() {
         return new RotationController.State(null, 0.0F, 0.0F, true);
      }
   }
}
