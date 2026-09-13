package ru.wild.util.player;

import net.minecraft.util.math.MathHelper;

public final class CameraRotationInterpolator {
   public static boolean instance;
   public static float data;
   public static float context;
   public static float config = -90.0F;
   public static float state = 90.0F;
   private static float cache;
   private static float output;
   private static float current;
   private static float active;
   private static float mode = 1.0F;

   private CameraRotationInterpolator() {
   }

   public static void handle(float var0, float var1, float var2) {
      cache = var0;
      output = var1;
      mode = MathHelper.clamp(var2, 0.05F, 1.0F);
   }

   public static void handle() {
      if (!instance) {
         current = 0.0F;
         active = 0.0F;
         data = 0.0F;
         context = 0.0F;
      } else {
         current = current + (cache - current) * mode;
         active = active + (output - active) * mode;
         data = current;
         context = active;
      }
   }

   public static void process() {
      instance = false;
      data = 0.0F;
      context = 0.0F;
      config = -90.0F;
      state = 90.0F;
      cache = 0.0F;
      output = 0.0F;
      current = 0.0F;
      active = 0.0F;
      mode = 1.0F;
   }
}
