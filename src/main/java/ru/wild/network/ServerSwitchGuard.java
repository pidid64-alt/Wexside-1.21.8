package ru.wild.network;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.DisconnectionInfo;
import ru.wild.core.ProxiesScreen;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.modules.misc.UnHook;

public final class ServerSwitchGuard {
   private static final long instance = 3500L;
   private static final Object data = new Object();
   private static volatile long context;
   private static volatile ServerInfo config;
   private static volatile boolean state;
   private static volatile ServerInfo cache;

   private ServerSwitchGuard() {
   }

   public static void handle(ServerInfo var0, DisconnectionInfo var1) {
      if (var1 != null && var1.reason() != null) {
         String var2;
         try {
            var2 = handle(var1.reason().getString());
         } catch (Throwable var10) {
            return;
         }

         if (process(var2)) {
            ServerInfo var3 = var0;
            if (var3 == null) {
               MinecraftClient var4 = MinecraftClient.getInstance();
               if (var4 != null) {
                  try {
                     var3 = var4.getCurrentServerEntry();
                  } catch (Throwable var9) {
                     var3 = null;
                  }
               }
            }

            if (var3 == null) {
               var3 = cache;
            }

            if (var3 != null) {
               ServerInfo var11;
               try {
                  var11 = handle(var3);
               } catch (Throwable var8) {
                  return;
               }

               synchronized (data) {
                  config = var11;
                  context = System.currentTimeMillis() + 3500L;
                  state = true;
               }
            }
         }
      }
   }

   public static void handle(MinecraftClient var0) {
      if (state && var0 != null) {
         try {
            process(var0);
         } catch (Throwable var13) {
         }

         if (var0.currentScreen instanceof DisconnectedScreen) {
            ServerInfo var1;
            long var2;
            synchronized (data) {
               if (!state) {
                  return;
               }

               var2 = context;
               var1 = config;
            }

            if (System.currentTimeMillis() >= var2) {
               if (var0.getNetworkHandler() == null) {
                  if (var1 != null && var1.address != null && !var1.address.isBlank()) {
                     try {
                        ServerAddress var15 = ServerAddress.parse(var1.address);
                        ConnectScreen.connect(process(), var0, var15, var1, false, null);
                     } catch (Throwable var11) {
                     } finally {
                        handle();
                     }
                  } else {
                     handle();
                  }
               }
            }
         }
      }
   }

   public static void handle() {
      synchronized (data) {
         state = false;
         context = 0L;
         config = null;
      }
   }

   public static void process(MinecraftClient var0) {
      if (var0 != null) {
         try {
            if (var0.getNetworkHandler() == null) {
               return;
            }

            ServerInfo var1 = var0.getCurrentServerEntry();
            if (var1 == null || var1.address == null || var1.address.isBlank()) {
               return;
            }

            cache = handle(var1);
         } catch (Throwable var2) {
         }
      }
   }

   private static ServerInfo handle(ServerInfo var0) {
      ServerInfo var1 = new ServerInfo(var0.name, var0.address, var0.getServerType());
      var1.copyWithSettingsFrom(var0);
      return var1;
   }

   private static Screen process() {
      return (Screen)(UnHook.target ? new MultiplayerScreen(new TitleScreen()) : new ProxiesScreen(new WildMainMenuScreen()));
   }

   private static String handle(String var0) {
      return var0 != null && !var0.isBlank() ? var0.replaceAll("§.", "").replace('§', ' ').toLowerCase(Locale.ROOT).trim() : "";
   }

   private static boolean process(String var0) {
      return var0 == null
         ? false
         : var0.contains("слишком много перемещаетесь между серверами")
            || var0.contains("ошибка сетевого протокола")
            || var0.contains("network protocol error")
            || var0.contains("too many server transfers")
            || var0.contains("too many moves between servers");
   }
}
