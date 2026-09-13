package org.wild.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.HeadlessBotPlayHandler;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.misc.PvPSafe;
import ru.wild.modules.misc.UnHook;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.security.AccountTierResolver;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin implements MinecraftContext {
   @Shadow
   private ClientWorld world;

   @Inject(method = {"onSetCameraEntity", "onOpenScreen", "onOpenHorseScreen", "onSetTradeOffers"}, at = @At("HEAD"), cancellable = true)
   private void wild$suppressBackgroundedHostLeak(CallbackInfo var1) {
      if (HeadlessBotEngine.handle((ClientPlayNetworkHandler)(Object)this)) {
         var1.cancel();
      }
   }

   @Inject(method = "onPlayerPositionLook", at = @At("HEAD"), cancellable = true)
   private void wild$suppressHostTeleport(PlayerPositionLookS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1, var3);
         var2.cancel();
      }
   }

   @Inject(method = "onHealthUpdate", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostHealth(HealthUpdateS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostExplosion(ExplosionS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onPlayerRespawn", at = @At("HEAD"))
   private void wild$redirectHostRespawn(PlayerRespawnS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.prepare();
      }
   }

   @Inject(method = "onPlayerRespawn", at = @At("TAIL"))
   private void wild$restoreBotAfterHostRespawn(PlayerRespawnS2CPacket var1, CallbackInfo var2) {
      HeadlessBotEngine.check();
   }

   @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostInventory(InventoryS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onPlayerAbilities", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostAbilities(PlayerAbilitiesS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onGameStateChange", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostGameState(GameStateChangeS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostSelectedSlot(UpdateSelectedSlotS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onExperienceBarUpdate", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostExperience(ExperienceBarUpdateS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostSlot(ScreenHandlerSlotUpdateS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostScreenProperty(ScreenHandlerPropertyUpdateS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onSetCursorItem", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostCursor(SetCursorItemS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onSetPlayerInventory", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostPlayerInventory(SetPlayerInventoryS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostCloseScreen(CloseScreenS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.execute();
         var2.cancel();
      }
   }

   @Inject(method = "onCooldownUpdate", at = @At("HEAD"), cancellable = true)
   private void wild$redirectHostCooldown(CooldownUpdateS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         NetworkThreadUtils.forceMainThread(var1, var3, toggleState);
         HeadlessBotEngine.handle(var1);
         var2.cancel();
      }
   }

   @Inject(method = "onEnterReconfiguration", at = @At("HEAD"))
   private void wild$hostEnterReconfiguration(EnterReconfigurationS2CPacket var1, CallbackInfo var2) {
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (HeadlessBotEngine.handle(var3)) {
         if (toggleState.isOnThread()) {
            HeadlessBotEngine.select();
         }
      }
   }

   @Inject(
      method = {
            "onEntitySetHeadYaw",
            "onEntity",
            "onMoveMinecartAlongTrack",
            "onEntityVelocityUpdate",
            "onEntityTrackerUpdate",
            "onEntityPositionSync",
            "onEntityPosition",
            "onEntitiesDestroy"
      },
      at = @At("HEAD"),
      cancellable = true
   )
   private void wild$dropEntityPacketsWhenNoWorld(CallbackInfo var1) {
      if (this.world == null) {
         var1.cancel();
      }
   }

   @Inject(method = "onBossBar", at = @At("HEAD"), cancellable = true)
   private void wild$guardBossBar(BossBarS2CPacket var1, CallbackInfo var2) {
      NetworkThreadUtils.forceMainThread(var1, (ClientPlayNetworkHandler)(Object)this, toggleState);

      try {
         toggleState.inGameHud.getBossBarHud().handlePacket(var1);
      } catch (Throwable var4) {
      }

      var2.cancel();
   }

   @ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private String wild$protectOutgoingChatMessage(String var1) {
      return ProtectInfo.process(var1);
   }

   @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
   private void onSendChatMessage(String var1, CallbackInfo var2) {
      if (WildClient.prepare()) {
         EventDispatchBoundary.handle();
         if (WildClient.mode != null && var1.equalsIgnoreCase(WildClient.mode)) {
            UnHook var3 = WildClient.instance.data.handle(UnHook.class);
            if (var3 != null && var3.enabled) {
               var3.setEnabled(false);
               var2.cancel();
               return;
            }
         }

         if (PvPSafe.handle(var1)) {
            var2.cancel();
         } else if (!UnHook.target) {
            String var4 = WildClient.instance.fetchProvider();
            if (var1.startsWith(var4)) {
               WildClient.instance.projectItem().handle(var1);
               var2.cancel();
            }
         }
      }
   }

   @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
   private void wild$blockPvpSafeCommand(String var1, CallbackInfo var2) {
      if (PvPSafe.process(var1)) {
         var2.cancel();
      }
   }

   @Inject(method = "runClickEventCommand", at = @At("HEAD"), cancellable = true)
   private void wild$blockPvpSafeClickCommand(String var1, Screen var2, CallbackInfo var3) {
      if (PvPSafe.process(var1)) {
         var3.cancel();
      }
   }

   @Inject(method = "onGameJoin", at = @At("TAIL"))
   private void onGameJoin(GameJoinS2CPacket var1, CallbackInfo var2) {
      EventDispatchBoundary.handle();
      AccountTierResolver.process(toggleState);
      ClientPlayNetworkHandler var3 = (ClientPlayNetworkHandler)(Object)this;
      if (!(var3 instanceof HeadlessBotPlayHandler) && HeadlessBotEngine.onTick()) {
         HeadlessBotEngine.refresh();
      }
   }

   @Redirect(
      method = "onPlayerList",
      at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false)
   )
   private void suppressUnknownPlayerLog(Logger var1, String var2, Object var3, Object var4) {
      if (!var2.startsWith("Ignoring player info update")) {
         var1.warn(var2, var3, var4);
      }
   }
}
