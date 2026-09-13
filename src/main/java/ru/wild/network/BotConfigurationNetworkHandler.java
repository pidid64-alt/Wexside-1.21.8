package ru.wild.network;

import java.util.UUID;
import net.fabricmc.fabric.impl.networking.client.ClientConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.network.state.PlayStateFactories.PacketCodecModifierContext;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.text.Text;
import org.wild.mixin.acceser.ClientConfigurationNetworkHandlerAccessor;
import ru.wild.automation.HeadlessBotConnector;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.HeadlessBotPlayHandler;
import ru.wild.automation.HeadlessBotSession;

public final class BotConfigurationNetworkHandler extends ClientConfigurationNetworkHandler {
   private final HeadlessBotSession instance;

   public BotConfigurationNetworkHandler(MinecraftClient var1, ClientConnection var2, ClientConnectionState var3, HeadlessBotSession var4) {
      super(var1, var2, var3);
      this.instance = var4;
   }

   public void onReady(ReadyS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.client);
      if (!HeadlessBotEngine.update(this.instance)) {
         HeadlessBotEngine.compute(this.instance);
      } else {
         ClientConfigurationNetworkHandlerAccessor var2 = (ClientConfigurationNetworkHandlerAccessor)(Object)this;
         Immutable var3 = this.handle(var2);
         ClientConnectionState var4 = new ClientConnectionState(
            var2.wild$profile(),
            this.worldSession,
            var3,
            var2.wild$enabledFeatures(),
            this.brand,
            this.serverInfo,
            this.postDisconnectScreen,
            this.serverCookies,
            var2.wild$chatState(),
            this.customReportDetails,
            this.getServerLinks()
         );
         this.instance.handle(var2.wild$profile());
         ClientConfigurationNetworkAddon var5 = ClientNetworkingImpl.getAddon(this);
         if (var5 != null) {
            var5.handleComplete();
         }

         HeadlessBotPlayHandler var6 = new HeadlessBotPlayHandler(this.client, this.connection, var4, this.instance);
         this.instance.handle(var6);
         this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(var3)), var6);
         this.connection.send(ReadyC2SPacket.INSTANCE);
         this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(var3), new PacketCodecModifierContext() {
            public boolean isInCreativeMode() {
               return false;
            }
         }));
      }
   }

   public void onResourcePackSend(ResourcePackSendS2CPacket var1) {
      UUID var2 = var1.id();
      this.connection.send(new ResourcePackStatusC2SPacket(var2, Status.ACCEPTED));
      this.connection.send(new ResourcePackStatusC2SPacket(var2, Status.DOWNLOADED));
      this.connection.send(new ResourcePackStatusC2SPacket(var2, Status.SUCCESSFULLY_LOADED));
      HeadlessBotConnector.handle(this.instance, "resource pack auto-accepted (config)");
   }

   public void onResourcePackRemove(ResourcePackRemoveS2CPacket var1) {
   }

   public void onServerTransfer(ServerTransferS2CPacket var1) {
      this.connection.disconnect(Text.translatable("disconnect.transfer"));
   }

   private Immutable handle(ClientConfigurationNetworkHandlerAccessor var1) {
      ClientDataPackManager var2 = var1.wild$dataPackManager();
      if (var2 == null) {
         return var1.wild$clientRegistries().createRegistryManager(ResourceFactory.MISSING, var1.wild$registryManager(), this.connection.isLocal());
      }

      LifecycledResourceManager var3 = var2.createResourceManager();

      Immutable var4;
      try {
         var4 = var1.wild$clientRegistries().createRegistryManager(var3, var1.wild$registryManager(), this.connection.isLocal());
      } catch (Throwable var7) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (var3 != null) {
         var3.close();
      }

      return var4;
   }

   public void onDisconnected(DisconnectionInfo var1) {
      String var2 = "config disconnected: " + var1.reason().getString();
      HeadlessBotEngine.handle(this.instance, var2);
      HeadlessBotConnector.handle(this.instance, "§c" + var2);
      HeadlessBotEngine.compute(this.instance);
   }
}
