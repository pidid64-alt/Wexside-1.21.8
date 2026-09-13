package ru.wild.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public final class PresenceConnectionListener {
   private PresenceConnectionListener() {
   }

   public static void handle() {
      ClientPlayConnectionEvents.JOIN.register((Join)(var0, var1, var2) -> OnlinePresenceBeacon.handle(handle(var2)));
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(var0, var1) -> OnlinePresenceBeacon.handle(""));
   }

   private static String handle(MinecraftClient var0) {
      ServerInfo var1 = var0.getCurrentServerEntry();
      return var1 != null && var1.address != null ? var1.address : "";
   }
}
