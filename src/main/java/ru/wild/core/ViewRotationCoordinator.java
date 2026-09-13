package ru.wild.core;

import ru.wild.api.event.CameraRotationEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MouseMotionEvent;
import ru.wild.util.math.NumericTransform;

public class ViewRotationCoordinator extends ClientComponent {
   public static boolean instance;
   public static boolean data;
   public static float context;
   public static float config;

   @EventHandler
   public void handle(MouseMotionEvent var1) {
      if (instance) {
         this.handle(var1.compute(), var1.resolve());
         var1.process();
      }
   }

   @EventHandler
   public void handle(CameraRotationEvent var1) {
      if (instance) {
         var1.handle(context);
         var1.process(config);
      } else {
         context = var1.compute();
         config = var1.resolve();
      }
   }

   private void handle(double var1, double var3) {
      config = NumericTransform.onTick((float)(config + var3 * 0.15), -90.0F, 90.0F);
      context = (float)(context + var1 * 0.15);
   }
}
