package ru.wild.security;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import ru.wild.WildClient;
import ru.wild.config.AltVaultStore;
import ru.wild.core.ClientSessionSwitcher;

public final class AccountTierResolver {
   private static volatile String instance = "";
   private static volatile String data = "";
   private static volatile String context = "";
   private static volatile long config;

   private AccountTierResolver() {
   }

   public static void handle(String var0, String var1) {
      instance = process(var0);
      data = var1 == null ? "" : var1.trim();
   }

   public static void handle(MinecraftClient var0) {
      if (var0 != null && var0.getSession() != null) {
         List<AltVaultStore.NamedEntry> var1 = AltVaultStore.handle(handle());
         if (!var1.isEmpty()) {
            String var2 = AltVaultStore.process(handle());
            AltVaultStore.NamedEntry var3 = var1.stream()
               .filter(var1x -> var1x.id().equals(var2))
               .findFirst()
               .orElseGet(
                  () -> var1.stream()
                     .filter(var0xx -> var0xx.lastUsedAt() > 0L)
                     .max(Comparator.comparingLong(AltVaultStore.NamedEntry::lastUsedAt))
                     .orElse(null)
               );
            if (var3 != null && !var3.name().isBlank()) {
               handle(var3.name(), var3.password());
               if (!var3.name().equals(var0.getSession().getUsername())) {
                  ClientSessionSwitcher.handle(var0);
                  boolean var4 = false;
                  if ("PREMIUM".equalsIgnoreCase(var3.type())) {
                     var4 = ClientSessionSwitcher.handle(var0, var3.name());
                  }

                  if (!var4) {
                     ClientSessionSwitcher.process(var0, var3.name());
                  }
               }
            }
         }
      }
   }

   public static void process(MinecraftClient var0) {
      if (var0 != null && var0.getSession() != null) {
         String var1 = process(var0.getSession().getUsername());
         if (!var1.isEmpty()) {
            Thread var2 = new Thread(() -> {
               String var1x;
               try {
                  var1x = handle(var1);
               } catch (Throwable var8) {
                  return;
               }

               if (!var1x.isEmpty()) {
                  String var2x = var1.toLowerCase(Locale.ROOT) + ":" + Integer.toHexString(var1x.hashCode());
                  long var3 = System.currentTimeMillis();
                  synchronized (AccountTierResolver.class) {
                     if (var2x.equals(context) && var3 - config < 4500L) {
                        return;
                     }

                     context = var2x;
                     config = var3;
                  }

                  try {
                     Thread.sleep(1600L);
                  } catch (InterruptedException var7) {
                     Thread.currentThread().interrupt();
                     return;
                  }

                  MinecraftClient var10 = MinecraftClient.getInstance();
                  if (var10 != null) {
                     var10.execute(() -> {
                        if (var10.player != null && var10.player.networkHandler != null && var10.getSession() != null) {
                           if (var1.equalsIgnoreCase(process(var10.getSession().getUsername()))) {
                              var10.player.networkHandler.sendChatCommand("login " + var1x);
                           }
                        }
                     });
                  }
               }
            }, "Wild Alt AutoLogin");
            var2.setDaemon(true);
            var2.start();
         }
      }
   }

   private static String handle(String var0) {
      if (var0.isEmpty()) {
         return "";
      }

      if (var0.equalsIgnoreCase(instance) && !data.isEmpty()) {
         return data;
      }

      for (AltVaultStore.NamedEntry var3 : AltVaultStore.handle(handle())) {
         if ("CRACKED".equalsIgnoreCase(var3.type()) && var0.equalsIgnoreCase(var3.name())) {
            return var3.password() == null ? "" : var3.password().trim();
         }
      }

      return "";
   }

   private static File handle() {
      return WildClient.instance != null && WildClient.instance.cache != null
         ? new File(WildClient.instance.cache, "accounts.json")
         : new File(WildClient.process(), "accounts.json");
   }

   private static String process(String var0) {
      return var0 == null ? "" : var0.trim();
   }
}
