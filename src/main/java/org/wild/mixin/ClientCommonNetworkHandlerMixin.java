package org.wild.mixin;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ResourcePackPolicy;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.network.BotConfigurationNetworkHandler;
import ru.wild.network.ServerSwitchGuard;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class ClientCommonNetworkHandlerMixin {
   @Shadow
   protected ServerInfo serverInfo;
   @Shadow
   protected MinecraftClient client;
   @Shadow
   protected ClientConnection connection;

   @Inject(method = "onDisconnected", at = @At("TAIL"))
   private void onDisconnected(DisconnectionInfo var1, CallbackInfo var2) {
      ServerSwitchGuard.handle(this.serverInfo, var1);
   }

   @Inject(method = "onDisconnected", at = @At("HEAD"))
   private void wild$restoreHostBeforeDisconnect(DisconnectionInfo var1, CallbackInfo var2) {
      if (((Object)this) instanceof ClientPlayNetworkHandler var3) {
         HeadlessBotEngine.process(var3);
      } else if (((Object)this) instanceof ClientConfigurationNetworkHandler && !(((Object)this) instanceof BotConfigurationNetworkHandler)) {
         HeadlessBotEngine.render();
      }
   }

   @Inject(method = "onResourcePackSend", at = @At("HEAD"))
   private void wild$preferVanillaServerResourcePack(ResourcePackSendS2CPacket var1, CallbackInfo var2) {
      if (var1 != null && this.client != null && this.wild$playerHelperWantsLoad()) {
         if ((this.wild$isFunTimeEndpoint(var1.url()) || this.wild$isFunTimeEndpoint(this.wild$currentServerAddress())) && this.serverInfo != null) {
            this.serverInfo.setResourcePackPolicy(ResourcePackPolicy.ENABLED);
         }
      }
   }

   private boolean wild$playerHelperWantsLoad() {
      if (!WildClient.prepare()) {
         return false;
      }

      try {
         if (WildClient.drawProfile() && WildClient.instance != null && WildClient.instance.data != null) {
            PlayerHelper var1 = (PlayerHelper)WildClient.instance.data.process(PlayerHelper.class);
            return var1 != null && var1.source.process("Load");
         } else {
            return false;
         }
      } catch (Throwable var2) {
         return false;
      }
   }

   private String wild$currentServerAddress() {
      try {
         if (this.serverInfo != null && this.serverInfo.address != null) {
            return this.serverInfo.address;
         }

         ServerInfo var1 = this.client.getCurrentServerEntry();
         return var1 == null ? "" : var1.address;
      } catch (Throwable var2) {
         return "";
      }
   }

   private boolean wild$isFunTimeEndpoint(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.toLowerCase(Locale.ROOT);
         return var2.contains("funtime") || var2.contains("fun-time") || var2.contains("ftmc");
      } else {
         return false;
      }
   }
}
