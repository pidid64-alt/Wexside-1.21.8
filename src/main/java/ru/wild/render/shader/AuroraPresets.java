package ru.wild.render.shader;

import java.util.Locale;

public enum AuroraPresets {
   AURORA("Aurora", "Полярное сияние"),
   STARDUST("Stardust", "Звездная пыль", "Stardust Field"),
   TWILIGHT_RAYLEIGH("Twilight Rayleigh", "Солнечная буря", "Зодиакальный рассвет", "Серебристые мезосферные облака"),
   QUANTUM_NEBULA("Quantum Nebula", "Туманность"),
   CHRONOS_SINGULARITY("Chronos Singularity", "Галактическая вуаль", "Сверхячейка на горизонте");

   private final String instance;
   private final String[] data;

   AuroraPresets(String var3, String... var4) {
      this.instance = var3;
      this.data = var4;
   }

   public String handle() {
      return this.instance;
   }

   public int process() {
      return this.ordinal();
   }

   public static String[] compute() {
      AuroraPresets[] var0 = values();
      String[] var1 = new String[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var2].instance;
      }

      return var1;
   }

   public static AuroraPresets handle(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = process(var0);

         for (AuroraPresets var5 : values()) {
            if (process(var5.instance).equals(var1) || process(var5.name()).equals(var1)) {
               return var5;
            }

            for (String var9 : var5.data) {
               if (process(var9).equals(var1)) {
                  return var5;
               }
            }
         }

         return AURORA;
      } else {
         return AURORA;
      }
   }

   private static String process(String var0) {
      return var0.trim().replace('_', ' ').toLowerCase(Locale.ROOT);
   }
}
