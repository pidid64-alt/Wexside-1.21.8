package ru.wild.network;

import java.util.UUID;

public record PartyIdentity(UUID uuid, String username, String avatarUrl) {
   public static PartyIdentity of(UUID var0, String var1, String var2) {
      return new PartyIdentity(var0, var1, sanitizeAvatar(var2));
   }

   private static String sanitizeAvatar(String var0) {
      return var0 != null && var0.startsWith("https://") ? var0 : null;
   }
}
