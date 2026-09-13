package ru.wild.automation;

import com.mojang.authlib.GameProfile;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import ru.wild.network.BotConfigurationNetworkHandler;

public final class HeadlessBotLoginHandler implements ClientLoginPacketListener {
   private final MinecraftClient instance = MinecraftClient.getInstance();
   private final ClientConnection data;
   private final HeadlessBotSession context;

   public HeadlessBotLoginHandler(ClientConnection var1, HeadlessBotSession var2) {
      this.data = var1;
      this.context = var2;
   }

   public void onHello(LoginHelloS2CPacket var1) {
      if (!HeadlessBotEngine.update(this.context)) {
         HeadlessBotEngine.compute(this.context);
      } else if (var1.needsAuthentication()) {
         String var8 = "Online-mode authentication is not supported for bot sessions";
         HeadlessBotEngine.handle(this.context, var8);
         HeadlessBotConnector.handle(this.context, "§c" + var8);
         this.data.disconnect(Text.literal("Wild bots support offline-mode servers only (online-mode auth requires a per-account session)"));
      } else {
         Cipher var2;
         Cipher var3;
         LoginKeyC2SPacket var4;
         try {
            SecretKey var5 = NetworkEncryptionUtils.generateSecretKey();
            PublicKey var6 = var1.getPublicKey();
            new BigInteger(NetworkEncryptionUtils.computeServerId(var1.getServerId(), var6, var5)).toString(16);
            var2 = NetworkEncryptionUtils.cipherFromKey(2, var5);
            var3 = NetworkEncryptionUtils.cipherFromKey(1, var5);
            var4 = new LoginKeyC2SPacket(var5, var6, var1.getNonce());
         } catch (Exception var7) {
            HeadlessBotEngine.handle(this.context, "Login protocol error: " + var7.getClass().getSimpleName());
            throw new IllegalStateException("Protocol error", var7);
         }

         this.data.send(var4, PacketCallbacks.always(() -> this.data.setupEncryption(var2, var3)));
      }
   }

   public void onSuccess(LoginSuccessS2CPacket var1) {
      if (!HeadlessBotEngine.update(this.context)) {
         HeadlessBotEngine.compute(this.context);
      } else {
         HeadlessBotEngine.handle(this.context, HeadlessBotEngine.Status.CONFIGURING, "Configuring session ...");
         GameProfile var2 = var1.profile();
         ClientConnectionState var3 = new ClientConnectionState(
            var2,
            this.instance.getTelemetryManager().createWorldSession(false, null, null),
            ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(),
            FeatureFlags.DEFAULT_ENABLED_FEATURES,
            null,
            null,
            null,
            Map.of(),
            null,
            Map.of(),
            ServerLinks.EMPTY
         );
         this.data.transitionInbound(ConfigurationStates.S2C, new BotConfigurationNetworkHandler(this.instance, this.data, var3, this.context));
         this.data.send(EnterConfigurationC2SPacket.INSTANCE);
         this.data.transitionOutbound(ConfigurationStates.C2S);
         this.data.send(new CustomPayloadC2SPacket(new BrandCustomPayload(ClientBrandRetriever.getClientModName())));
         this.data.send(new ClientOptionsC2SPacket(this.instance.options.getSyncedOptions()));
      }
   }

   public void onDisconnect(LoginDisconnectS2CPacket var1) {
      HeadlessBotEngine.handle(this.context, "Login rejected: " + var1.reason().getString());
      this.data.disconnect(var1.reason());
   }

   public void onCompression(LoginCompressionS2CPacket var1) {
      if (!this.data.isLocal()) {
         this.data.setCompressionThreshold(var1.getCompressionThreshold(), false);
      }
   }

   public void onQueryRequest(LoginQueryRequestS2CPacket var1) {
      this.data.send(new LoginQueryResponseC2SPacket(var1.queryId(), null));
   }

   public void onCookieRequest(CookieRequestS2CPacket var1) {
      this.data.send(new CookieResponseC2SPacket(var1.key(), null));
   }

   public void onDisconnected(DisconnectionInfo var1) {
      String var2 = "login disconnected: " + var1.reason().getString();
      HeadlessBotEngine.handle(this.context, var2);
      HeadlessBotConnector.handle(this.context, "§c" + var2);
      HeadlessBotEngine.compute(this.context);
   }

   public boolean isConnectionOpen() {
      return this.data.isOpen();
   }
}
