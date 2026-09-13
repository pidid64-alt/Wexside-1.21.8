package ru.wild.render.shader;

public final class ShaderColorPacking {
   public static final int instance = 128;

   private ShaderColorPacking() {
   }

   public static int handle(int var0, int var1) {
      if (var1 > 0 && var0 < var1) {
         int var2 = Math.max(128, var0);

         while (var2 < var1 && var2 <= 1073741823) {
            var2 <<= 1;
         }

         return Math.max(var1, var2);
      } else {
         return Math.max(0, var0);
      }
   }
}
