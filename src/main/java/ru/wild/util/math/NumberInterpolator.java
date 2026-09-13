package ru.wild.util.math;

public class NumberInterpolator {
   public static <T extends Number> T handle(T var0, T var1, double var2) {
      double var4 = var0.doubleValue();
      double var6 = var1.doubleValue();
      double var8 = var4 + var2 * (var6 - var4);
      if (var0 instanceof Integer) {
         return (T)Integer.valueOf((int)Math.round(var8));
      } else if (var0 instanceof Double) {
         return (T)Double.valueOf(var8);
      } else if (var0 instanceof Float) {
         return (T)Float.valueOf((float)var8);
      } else if (var0 instanceof Long) {
         return (T)Long.valueOf(Math.round(var8));
      } else if (var0 instanceof Short) {
         return (T)Short.valueOf((short)Math.round(var8));
      } else if (var0 instanceof Byte) {
         return (T)Byte.valueOf((byte)Math.round(var8));
      } else {
         throw new IllegalArgumentException("Unsupported type: " + var0.getClass().getSimpleName());
      }
   }
}
