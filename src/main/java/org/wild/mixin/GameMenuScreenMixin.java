package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.core.ProxiesScreen;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.modules.misc.PvPSafe;
import ru.wild.modules.misc.UnHook;
import ru.wild.network.ServerSwitchGuard;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
   @Shadow
   private ButtonWidget exitButton;
   @Unique
   private ButtonWidget wild$reconnectButton;

   protected GameMenuScreenMixin(Text var1) {
      super(var1);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void wild$disablePvpSafeDisconnectButton(CallbackInfo var1) {
      this.wild$initReconnectButton();
      if (this.exitButton != null && PvPSafe.refresh()) {
         this.exitButton.active = false;
      }

      if (this.wild$reconnectButton != null && PvPSafe.refresh()) {
         this.wild$reconnectButton.active = false;
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void wild$keepPvpSafeDisconnectButtonDisabled(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.exitButton != null && PvPSafe.refresh()) {
         this.exitButton.active = false;
      }

      if (this.wild$reconnectButton != null) {
         boolean var6 = this.wild$hasReconnectTarget();
         this.wild$reconnectButton.visible = var6;
         this.wild$reconnectButton.active = var6 && this.wild$canReconnect();
      }
   }

   @Unique
   private void wild$initReconnectButton() {
      if (this.exitButton != null && this.wild$hasReconnectTarget()) {
         int var1 = this.exitButton.getX();
         int var2 = this.exitButton.getY();
         this.exitButton.setDimensionsAndPosition(100, this.exitButton.getHeight(), var1, var2);
         this.wild$reconnectButton = ButtonWidget.builder(Text.literal("Перезаход"), var1x -> this.wild$reconnect())
            .dimensions(var1 + 104, var2, 100, this.exitButton.getHeight())
            .build();
         this.addDrawableChild(this.wild$reconnectButton);
      }
   }

   @Unique
   private boolean wild$canReconnect() {
      return this.wild$hasReconnectTarget() && !PvPSafe.refresh();
   }

   @Unique
   private boolean wild$hasReconnectTarget() {
      MinecraftClient var1 = this.client;
      if (!UnHook.target && var1 != null && !var1.isInSingleplayer()) {
         ServerInfo var2 = var1.getCurrentServerEntry();
         return var2 != null && var2.address != null && !var2.address.isBlank();
      } else {
         return false;
      }
   }

   @Unique
   private void wild$reconnect() {
      MinecraftClient var1 = this.client;
      if (this.wild$canReconnect()) {
         ServerInfo var2 = var1.getCurrentServerEntry();
         if (var2 != null && var2.address != null && !var2.address.isBlank()) {
            ServerInfo var3 = new ServerInfo(var2.name, var2.address, var2.getServerType());
            var3.copyWithSettingsFrom(var2);
            ServerAddress var4 = ServerAddress.parse(var3.address);
            ServerSwitchGuard.handle();
            GameMenuScreen.disconnect(var1, ClientWorld.QUITTING_MULTIPLAYER_TEXT);
            ConnectScreen.connect(new ProxiesScreen(new WildMainMenuScreen()), var1, var4, var3, false, null);
         }
      }
   }
}
