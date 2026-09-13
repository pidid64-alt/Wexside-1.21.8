package ru.wild.util.player;

import ru.wild.core.MinecraftContext;

public class MouseSensitivityQuantizer implements MinecraftContext {
   public static float handle(float var0) {
      return process(var0) * handle();
   }

   public static float handle() {
      return (float)(process() * 0.15);
   }

   public static float process() {
      float var0;
      return (var0 = (float)((Double)toggleState.options.getMouseSensitivity().getValue() * 0.6 + 0.2)) * var0 * var0 * 8.0F;
   }

   public static float process(float var0) {
      return Math.round(var0 / handle());
   }
}
