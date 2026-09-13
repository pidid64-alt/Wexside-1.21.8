package ru.wild.api.module;

public enum ModuleCategory {
   Combat("Combat", "f"),
   Movement("Movement", "b"),
   Visuals("Visuals", "n"),
   Player("Player", "m"),
   Misc("Misc", "v");

   private final String instance;
   private final String data;

   ModuleCategory(String var3, String var4) {
      this.instance = var3;
      this.data = var4;
   }

   public String handle() {
      return this.data;
   }

   public String process() {
      return this.instance;
   }
}
