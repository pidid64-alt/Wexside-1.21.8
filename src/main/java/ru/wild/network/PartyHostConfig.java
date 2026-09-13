package ru.wild.network;

public final class PartyHostConfig {
   private final String instance;
   private final int data;

   private PartyHostConfig(String var1, int var2) {
      this.instance = var1;
      this.data = var2;
   }

   public static PartyHostConfig handle() {
      return new PartyHostConfig(handle("wild.party.host", "127.0.0.1"), resolve());
   }

   public static PartyHostConfig handle(String var0, int var1) {
      return var0 != null && !var0.isBlank() && var1 >= 1 && var1 <= 65535 ? new PartyHostConfig(var0.trim(), var1) : handle();
   }

   public String process() {
      return this.instance;
   }

   public int compute() {
      return this.data;
   }

   @Override
   public String toString() {
      return this.instance + ":" + this.data;
   }

   private static String handle(String var0, String var1) {
      String var2 = System.getProperty(var0);
      return var2 != null && !var2.isBlank() ? var2.trim() : var1;
   }

   private static int resolve() {
      String var0 = System.getProperty("wild.party.port");
      if (var0 != null && !var0.isBlank()) {
         try {
            int var1 = Integer.parseInt(var0.trim());
            return var1 >= 1 && var1 <= 65535 ? var1 : 7331;
         } catch (NumberFormatException var2) {
            return 7331;
         }
      } else {
         return 7331;
      }
   }
}
