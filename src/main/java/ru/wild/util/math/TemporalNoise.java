package ru.wild.util.math;

public final class TemporalNoise {
   public static final int instance = 512000;
   public static final int data = 128;
   public static final int context = 65535;
   private static final int config = 3600000;
   private static final long state = System.nanoTime();

   private TemporalNoise() {
   }

   public static int handle() {
      return (int)((System.nanoTime() - state) / 1000000L);
   }

   public static float process() {
      return Math.floorMod(handle(), 3600000) * 0.001F;
   }

   public static float compute() {
      return Math.floorMod(handle(), 512000) * 0.001F;
   }

   public static int handle(int var0) {
      int var1 = Math.floorMod(var0, 512000);
      int var2 = (int)(var1 * 128L / 1000L);
      return var2 >= 65535 ? 65534 : var2;
   }
}
