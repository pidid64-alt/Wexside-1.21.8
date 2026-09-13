package org.wild.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen.WorldEntryReason;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.api.event.ScreenOpenedEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldReadyEvent;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.combat.RotationEngine;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.core.ProxiesScreen;
import ru.wild.gui.screen.AltVaultScreen;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.modules.combat.HitBox;
import ru.wild.modules.misc.PvPSafe;
import ru.wild.modules.misc.UnHook;
import ru.wild.modules.player.NoDelay;
import ru.wild.network.ServerSwitchGuard;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.render.shader.ScreenTransitionRenderer;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
   @Shadow
   private int itemUseCooldown;
   @Shadow
   private int attackCooldown;
   @Unique
   private boolean wild$hideOpenedScreen;
   @Unique
   private Screen wild$diagPreviousScreen;

   @Inject(method = "stop", at = @At("HEAD"))
   private void wild$onStop(CallbackInfo var1) {
      if (WildClient.drawProfile()) {
         ScreenTransitionRenderer.handle().compute();
         WildClient.update();
      }
   }

   @Inject(method = "stop", at = @At("RETURN"))
   private void wild$onStopReturned(CallbackInfo var1) {
      WildClient.apply();
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void wild$coreTickHead(CallbackInfo var1) {
      RenderDiagnostics.handle().process();
      EventDispatchBoundary.handle();
      MinecraftClient var2 = (MinecraftClient)(Object)this;
      if (WildClient.drawProfile()) {
         ServerSwitchGuard.process(var2);
         ServerSwitchGuard.handle(var2);
         AltVaultScreen.handle(var2);
      }
   }

   @Inject(method = "tick", at = @At("TAIL"))
   private void wild$coreTickTail(CallbackInfo var1) {
      if (WildClient.drawProfile()) {
         HeadlessBotEngine.apply();
      }

      RenderDiagnostics.handle().compute();
      MinecraftClient var2 = (MinecraftClient)(Object)this;
      if (WildClient.drawProfile() && !var2.isPaused() && var2.player != null && var2.world != null) {
         try {
            EventHandlerInvoker.handle(new ClientUpdateEvent(var2));
         } catch (Throwable var4) {
         }
      }
   }

   @Inject(
      method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$WorldEntryReason;)V",
      at = @At("TAIL")
   )
   private void wild$loadWorld(ClientWorld var1, WorldEntryReason var2, CallbackInfo var3) {
      ServerSwitchGuard.handle();
      if (var1 != null) {
         try {
            EventHandlerInvoker.handle(new WorldJoinedEvent());
            EventHandlerInvoker.handle(new WorldReadyEvent());
         } catch (Throwable var5) {
         }
      }
   }

   @Inject(method = "onResolutionChanged", at = @At("TAIL"))
   private void wild$onResolutionChanged(CallbackInfo var1) {
      MinecraftClient var2 = (MinecraftClient)(Object)this;
      if (var2.getWindow() != null) {
         WildClient.handle(var2.getWindow().getFramebufferWidth(), var2.getWindow().getFramebufferHeight());
      } else {
         WildClient.handle(0, 0);
      }
   }

   @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
   private void wild$onWindowFocusChanged(boolean var1, CallbackInfo var2) {
      MinecraftClient var3 = (MinecraftClient)(Object)this;
      if (!var1 && var3.mouse != null) {
         var3.mouse.unlockCursor();
      }

      WildClient.handle(var1);
   }

   @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
   private void wild$handleScreenSet(Screen var1, CallbackInfo var2) {
      MinecraftClient var3 = (MinecraftClient)(Object)this;
      this.wild$diagPreviousScreen = var3.currentScreen;
      if (WildClient.drawProfile() && !UnHook.target) {
         if (var1 instanceof TitleScreen && !(var1 instanceof WildMainMenuScreen)) {
            try {
               var3.setScreen(new WildMainMenuScreen());
            } catch (Throwable var5) {
            }

            var2.cancel();
         } else if (var1 instanceof MultiplayerScreen && !(var1 instanceof ProxiesScreen)) {
            try {
               Screen var8 = var3.currentScreen instanceof WildMainMenuScreen ? var3.currentScreen : new WildMainMenuScreen();
               var3.setScreen(new ProxiesScreen(var8));
            } catch (Throwable var6) {
            }

            var2.cancel();
         } else {
            if (var1 != null) {
               ScreenOpenedEvent var4 = new ScreenOpenedEvent(var1);
               EventHandlerInvoker.handle(var4);
               if (var4.handle()) {
                  var2.cancel();
                  return;
               }

               if (var4.update()) {
                  this.wild$hideOpenedScreen = true;
               }
            }

            GuiRippleShader.handle().update();
            if (!this.wild$isLoadingScreen(var1)
               && var3.world == null
               && !this.wild$isLoadingScreen(var3.currentScreen)
               && !(var3.currentScreen instanceof FrameRenderListener)
               && !(var1 instanceof FrameRenderListener)) {
               try {
                  ScreenTransitionRenderer.handle().handle(var3.currentScreen, var1);
               } catch (Throwable var7) {
                  ScreenTransitionRenderer.handle().compute();
               }
            } else {
               ScreenTransitionRenderer.handle().compute();
            }
         }
      }
   }

   @Inject(method = "setScreen", at = @At("TAIL"))
   private void wild$postScreenSet(Screen var1, CallbackInfo var2) {
      MinecraftClient var3 = (MinecraftClient)(Object)this;
      if (WildClient.drawProfile()) {
         ScreenRenderDiagnostics.handle(this.wild$diagPreviousScreen, var1);
      }

      if (this.wild$hideOpenedScreen) {
         this.wild$hideOpenedScreen = false;
         if (var1 != null && var3.currentScreen == var1) {
            var3.currentScreen = null;
            if (var3.mouse != null) {
               var3.mouse.lockCursor();
            }
         }
      }

      if (WildClient.drawProfile() && !UnHook.target && var1 != null && !(var1 instanceof FrameRenderListener)) {
         GlCompatibilityProbe.handle(var3);
      }
   }

   @Unique
   private boolean wild$isLoadingScreen(Screen var1) {
      return var1 instanceof LevelLoadingScreen || var1 instanceof DownloadingTerrainScreen || var1 instanceof ConnectScreen || var1 instanceof ProgressScreen;
   }

   @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("HEAD"), cancellable = true)
   private void wild$blockPvpSafeDisconnect(Screen var1, boolean var2, CallbackInfo var3) {
      if (PvPSafe.compute(var2)) {
         var3.cancel();
      }
   }

   @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
   private void wild$onDoAttackHitbox(CallbackInfoReturnable<Boolean> var1) {
      if (WildClient.prepare()) {
         MinecraftClient var2 = (MinecraftClient)(Object)this;
         if (var2.player != null && var2.world != null && var2.interactionManager != null && WildClient.instance != null && WildClient.instance.data != null) {
            HitBox var3 = WildClient.instance.data.handle(HitBox.class);
            if (var3 != null && var3.enabled && HitBox.source.process("Легит")) {
               LivingEntity var4 = var3.refresh();
               if (var4 != null) {
                  HitBox.handle(var4);
                  RotationEngine.handle(var4, true, HitBox.previous.compute());
                  var1.setReturnValue(true);
               }
            }
         }
      }
   }

   @Inject(method = "doAttack", at = @At("RETURN"))
   private void wild$onDoAttackNoDelay(CallbackInfoReturnable<Boolean> var1) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            NoDelay var2 = WildClient.instance.data.handle(NoDelay.class);
            if (var2 != null && var2.enabled && NoDelay.previous.compute()) {
               this.attackCooldown = (int)NoDelay.itemProject.compute();
            }
         }
      }
   }

   @Inject(method = "doItemUse", at = @At("RETURN"))
   private void wild$onDoItemUseNoDelay(CallbackInfo var1) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            NoDelay var2 = WildClient.instance.data.handle(NoDelay.class);
            if (var2 != null && var2.enabled && NoDelay.latest.compute()) {
               this.itemUseCooldown = (int)NoDelay.responseCompute.compute();
            }
         }
      }
   }
}
