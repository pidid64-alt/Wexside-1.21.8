package ru.wild.network;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.option.ServerList;

public final class BravoHvhServerRegistrar {
   private static final String instance = "BravoHvH";
   private static final String data = "wi.bravohvh.su";
   private static final AtomicBoolean context = new AtomicBoolean(false);

   private BravoHvhServerRegistrar() {
   }

   public static void handle(MinecraftClient var0) {
      if (var0 != null && context.compareAndSet(false, true)) {
         try {
            ServerList var1 = new ServerList(var0);
            var1.loadFile();
            if (handle(var1, "wi.bravohvh.su")) {
               return;
            }

            ServerInfo var2 = new ServerInfo("BravoHvH", "wi.bravohvh.su", ServerType.OTHER);
            var1.add(var2, false);
            var1.saveFile();
         } catch (Throwable var3) {
         }
      }
   }

   private static boolean handle(ServerList var0, String var1) {
      String var2 = handle(var1);
      if (var2.isEmpty()) {
         return true;
      }

      int var3 = var0.size();

      for (int var4 = 0; var4 < var3; var4++) {
         ServerInfo var5 = var0.get(var4);
         if (var5 != null && handle(var5.address).equals(var2)) {
            return true;
         }
      }

      return false;
   }

   private static String handle(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.trim().toLowerCase(Locale.ROOT);
      if (var1.endsWith(":25565")) {
         var1 = var1.substring(0, var1.length() - ":25565".length());
      }

      return var1;
   }
}
