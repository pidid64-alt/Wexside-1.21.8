package ru.wild.gui.screen;

public final class ServerStatusRecord {
   final String instance;
   String data;
   String context;
   byte[] config;
   long state;
   long cache;

   ServerStatusRecord(String var1, String var2, String var3) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
   }

   String handle() {
      String var1 = AltVaultScreen.CacheEntry.apply(this.data).trim();
      return var1.isBlank() ? AltVaultScreen.CacheEntry.update(this.context) : var1;
   }
}
