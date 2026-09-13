package ru.wild.network;

import java.util.Locale;

public final class ServerAddressNormalizer {
   public static final String instance = "";
   private static final int data = 64;

   private ServerAddressNormalizer() {
   }

   public static String handle(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.trim().toLowerCase(Locale.ROOT);
      if (var1.endsWith(".")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      if (!var1.isEmpty() && var1.length() <= 64 && var1.indexOf(46) >= 0) {
         return process(var1) ? var1 : "";
      } else {
         return "";
      }
   }

   private static boolean process(String var0) {
      for (int var1 = 0; var1 < var0.length(); var1++) {
         if (!handle(var0.charAt(var1))) {
            return false;
         }
      }

      return true;
   }

   private static boolean handle(char var0) {
      return var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '.' || var0 == '-' || var0 == ':' || var0 == '_';
   }
}
