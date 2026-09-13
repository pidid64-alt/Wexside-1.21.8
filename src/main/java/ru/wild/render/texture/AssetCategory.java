package ru.wild.render.texture;

public enum AssetCategory {
   MODELS("models", "Модели"),
   ITEMS("items", "Предметы"),
   PETS("pets", "Питомцы");

   private final String instance;
   private final String data;

   AssetCategory(String var3, String var4) {
      this.instance = var3;
      this.data = var4;
   }

   public String handle() {
      return this.instance;
   }

   public String process() {
      return this.data;
   }

   public static AssetCategory handle(String var0) {
      if (var0 != null) {
         for (AssetCategory var4 : values()) {
            if (var4.instance.equalsIgnoreCase(var0)) {
               return var4;
            }
         }
      }

      return MODELS;
   }
}
