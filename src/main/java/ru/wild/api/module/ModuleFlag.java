package ru.wild.api.module;

import java.util.Locale;

public enum ModuleFlag {
   NEW("New", -14494738, "E", 10),
   RISKY("Risky", -50340, "I", 20),
   PATCHED("Patched", -20448, "O", 30),
   GRIM("Grim", -15681151, "Q", 40),
   MATRIX("Matrix", -5083905, "W", 50),
   VIP("VIP", -6511697, "T", 60),
   COMBAT("Combat", -45709, "f", 200),
   MOVEMENT("Movement", -10034009, "b", 210),
   VISUALS("Visuals", -8861697, "n", 220),
   PLAYER("Player", -11930, "m", 230),
   MISC("Misc", -3889153, "v", 240);

   private final String instance;
   private final int data;
   private final String context;
   private final int config;

   ModuleFlag(String var3, int var4, String var5, int var6) {
      this.instance = var3;
      this.data = var4;
      this.context = var5;
      this.config = var6;
   }

   public String handle() {
      return this.instance;
   }

   public int process() {
      return this.data;
   }

   public String compute() {
      return this.context;
   }

   public int resolve() {
      return this.config;
   }

   public static ModuleFlag handle(String var0) {
      if (var0 == null) {
         return null;
      }

      String var1 = process(var0);
      if (var1.isEmpty()) {
         return null;
      }

      for (ModuleFlag var5 : values()) {
         if (process(var5.name()).equals(var1) || process(var5.instance).equals(var1)) {
            return var5;
         }
      }

      return null;
   }

   public static ModuleFlag handle(ModuleCategory var0) {
      if (var0 == null) {
         return null;
      }

      return switch (var0) {
         case Combat -> COMBAT;
         case Movement -> MOVEMENT;
         case Visuals -> VISUALS;
         case Player -> PLAYER;
         case Misc -> MISC;
      };
   }

   private static String process(String var0) {
      String var1 = var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT);
      if (var1.startsWith("#")) {
         var1 = var1.substring(1);
      }

      return var1.replace("-", "").replace("_", "").replace(" ", "");
   }
}
