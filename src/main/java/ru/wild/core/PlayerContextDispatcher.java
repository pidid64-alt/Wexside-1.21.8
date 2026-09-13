package ru.wild.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.wild.api.event.Event;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.HeadlessBotSession;

public final class PlayerContextDispatcher {
   private PlayerContextDispatcher() {
   }

   public static void handle(ClientPlayerEntity var0) {
      handle(var0, new PlayerUpdateEvent());
   }

   public static void handle(HeadlessBotSession var0, Event var1) {
      if (var0 != null) {
         ClientContextExecutor.handle(var0, () -> var0.refresh().handle(var1));
      }
   }

   public static void handle(ClientPlayerEntity var0, Event var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null) {
         if (var2.player == null) {
            EventHandlerInvoker.handle(var1);
         } else if (var0 == var2.player) {
            HeadlessBotSession var4 = HeadlessBotEngine.handle(var0);
            if (var4 != null) {
               var4.refresh().handle(var1);
            } else {
               EventHandlerInvoker.handle(var1);
            }
         } else {
            HeadlessBotSession var3 = HeadlessBotEngine.handle(var0);
            if (var3 != null) {
               ClientContextExecutor.handle(var3, () -> var3.refresh().handle(var1));
            }
         }
      }
   }
}
