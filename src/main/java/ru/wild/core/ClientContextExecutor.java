package ru.wild.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import ru.wild.automation.BackgroundClientPlayer;
import ru.wild.automation.DetachedClientWorld;
import ru.wild.automation.HeadlessBotSession;

public final class ClientContextExecutor {
   private ClientContextExecutor() {
   }
   public static void handle(HeadlessBotSession var0, Runnable var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null && var0 != null) {
         DetachedClientWorld var3 = var0.execute();
         BackgroundClientPlayer var4 = var0.prepare();
         ClientPlayerInteractionManager var5 = var0.check();
         if (var3 != null && var4 != null && var5 != null) {
            ClientWorld var6 = var2.world;
            ClientPlayerEntity var7 = var2.player;
            ClientPlayerInteractionManager var8 = var2.interactionManager;
            boolean var11 = false /* VF: Semaphore variable */;

            try {
               var11 = true;
               var2.world = var3;
               var2.player = var4;
               var2.interactionManager = var5;
               var1.run();
               var11 = false;
            } finally {
               if (var11) {
                  var2.world = var6;
                  var2.player = var7;
                  var2.interactionManager = var8;
               }
            }

            var2.world = var6;
            var2.player = var7;
            var2.interactionManager = var8;
         }
      }
   }
}
