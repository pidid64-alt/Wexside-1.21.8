package ru.wild.automation;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TestInstanceBlockStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket.Reason;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.chunk.WorldChunk;
import org.wild.mixin.acceser.ClientPlayNetworkHandlerAccessor;
import ru.wild.network.BotConfigurationNetworkHandler;

public final class HeadlessBotPlayHandler extends ClientPlayNetworkHandler {
   private final HeadlessBotSession instance;
   private final MinecraftClient data;
   private volatile boolean context;

   public HeadlessBotPlayHandler(MinecraftClient var1, ClientConnection var2, ClientConnectionState var3, HeadlessBotSession var4) {
      super(var1, var2, var3);
      this.data = var1;
      this.instance = var4;
   }

   public HeadlessBotSession handle() {
      return this.instance;
   }

   public void sendPacket(Packet<?> var1) {
      if (!this.context) {
         super.sendPacket(var1);
      }
   }

   public void onGameJoin(GameJoinS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (!HeadlessBotEngine.update(this.instance)) {
         HeadlessBotEngine.compute(this.instance);
      } else {
         ClientPlayNetworkHandlerAccessor var2 = (ClientPlayNetworkHandlerAccessor)(Object)this;
         CommonPlayerSpawnInfo var3 = var1.commonPlayerSpawnInfo();
         Properties var4 = new Properties(Difficulty.NORMAL, var1.hardcore(), var3.isFlat());
         var2.wild$setWorldProperties(var4);
         var2.wild$setChunkLoadDistance(var1.viewDistance());
         var2.wild$setSimulationDistance(var1.simulationDistance());
         DetachedClientWorld var5 = new DetachedClientWorld(
            this.instance,
            this,
            var4,
            var3.dimension(),
            var3.dimensionType(),
            var1.viewDistance(),
            var1.simulationDistance(),
            var3.isDebug(),
            var3.seed(),
            var3.seaLevel()
         );
         var2.wild$setWorld(var5);
         StatHandler var6 = new StatHandler();
         ClientRecipeBook var7 = new ClientRecipeBook();
         BackgroundClientPlayer var8 = new BackgroundClientPlayer(this.instance, this.data, var5, this, var6, var7);
         var8.setId(var1.playerEntityId());
         var8.init();
         var8.setReducedDebugInfo(var1.reducedDebugInfo());
         var8.setShowsDeathScreen(var1.showDeathScreen());
         var8.setLastDeathPos(var3.lastDeathLocation());
         var8.setPortalCooldown(var3.portalCooldown());
         handle(var8);
         var5.addEntity(var8);
         ClientPlayerInteractionManager var9 = new ClientPlayerInteractionManager(this.data, this);
         var9.setGameModes(var3.gameMode(), var3.lastGameMode());
         var9.copyAbilities(var8);
         this.instance.handle(var5);
         this.instance.handle(var8);
         this.instance.handle(var9);
         this.instance.handle(true);
         if (!HeadlessBotEngine.process(this.instance)) {
            HeadlessBotEngine.compute(this.instance);
         } else {
            String var10 = "joined the game as " + this.getProfile().getName();
            HeadlessBotEngine.handle(this.instance, HeadlessBotEngine.Status.JOINED, var10);
            if (this.instance.render() || HeadlessBotEngine.handle() == this.instance) {
               this.instance.compute(false);
               HeadlessBotEngine.handle(this.instance);
            }

            HeadlessBotConnector.handle(this.instance, "§a" + var10);
         }
      }
   }

   public void onPlayerPositionLook(PlayerPositionLookS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null && !var2.hasVehicle()) {
         PlayerPosition var3 = PlayerPosition.fromEntity(var2);
         Set var4 = var1.relatives();
         PlayerPosition var5 = PlayerPosition.apply(var3, var1.change(), var4);
         PlayerPosition var6 = new PlayerPosition(var2.getLastRenderPos(), Vec3d.ZERO, var2.lastYaw, var2.lastPitch);
         PlayerPosition var7 = PlayerPosition.apply(var6, var1.change(), var4);
         var2.setPosition(var5.position());
         var2.setVelocity(var5.deltaMovement());
         var2.setYaw(var5.yaw());
         var2.setPitch(var5.pitch());
         var2.setLastPositionAndAngles(var7.position(), var7.yaw(), var7.pitch());
         handle(var2);
         var2.process();
      }

      this.connection.send(new TeleportConfirmC2SPacket(var1.teleportId()));
      if (var2 != null) {
         this.connection.send(new Full(var2.getX(), var2.getY(), var2.getZ(), var2.getYaw(), var2.getPitch(), var2.isOnGround(), var2.horizontalCollision));
      }
   }

   public void onHealthUpdate(HealthUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         var2.setHealth(var1.getHealth());
         var2.getHungerManager().setFoodLevel(var1.getFood());
         var2.getHungerManager().setSaturationLevel(var1.getSaturation());
      }
   }

   public void onMapUpdate(MapUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      DetachedClientWorld var2 = this.instance.execute();
      if (var2 != null) {
         MapIdComponent var3 = var1.mapId();
         MapState var4 = var2.getMapState(var3);
         if (var4 == null) {
            var4 = MapState.of(var1.scale(), var1.locked(), var2.getRegistryKey());
            var2.putClientsideMapState(var3, var4);
         }

         var1.apply(var4);
         this.data.getMapTextureManager().setNeedsUpdate(var3, var4);
      }
   }

   public void onInventory(InventoryS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         if (var1.syncId() == 0) {
            var2.playerScreenHandler.updateSlotStacks(var1.revision(), var1.contents(), var1.cursorStack());
         } else if (var1.syncId() == var2.currentScreenHandler.syncId) {
            var2.currentScreenHandler.updateSlotStacks(var1.revision(), var1.contents(), var1.cursorStack());
         }
      }
   }

   public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         ItemStack var3 = var1.getStack();
         int var4 = var1.getSlot();
         if (var1.getSyncId() == 0) {
            var2.playerScreenHandler.setStackInSlot(var4, var1.getRevision(), var3);
         } else if (var1.getSyncId() == var2.currentScreenHandler.syncId) {
            var2.currentScreenHandler.setStackInSlot(var4, var1.getRevision(), var3);
         }
      }
   }

   public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null && var2.currentScreenHandler != null && var2.currentScreenHandler.syncId == var1.getSyncId()) {
         var2.currentScreenHandler.setProperty(var1.getPropertyId(), var1.getValue());
      }
   }

   public void onSetCursorItem(SetCursorItemS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null && var2.currentScreenHandler != null) {
         var2.currentScreenHandler.setCursorStack(var1.contents());
      }
   }

   public void onSetPlayerInventory(SetPlayerInventoryS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         var2.getInventory().setStack(var1.slot(), var1.contents());
      }
   }

   public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null && PlayerInventory.isValidHotbarIndex(var1.slot())) {
         var2.getInventory().setSelectedSlot(var1.slot());
      }
   }

   public void onGameMessage(GameMessageS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onGameMessage(var1);
      }
   }

   public void onChatMessage(ChatMessageS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onChatMessage(var1);
      }
   }

   public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onProfilelessChatMessage(var1);
      }
   }

   public void onOpenScreen(OpenScreenS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onOpenScreen(var1);
      } else {
         BackgroundClientPlayer var2 = this.instance.prepare();
         if (var2 != null) {
            try {
               ScreenHandler var3 = var1.getScreenHandlerType().create(var1.getSyncId(), var2.getInventory());
               if (var3 != null) {
                  var2.currentScreenHandler = var3;
               } else {
                  this.connection.send(new CloseHandledScreenC2SPacket(var1.getSyncId()));
               }
            } catch (Throwable var4) {
               this.connection.send(new CloseHandledScreenC2SPacket(var1.getSyncId()));
               HeadlessBotConnector.handle(this.instance, "§cheadless screen open failed: " + var4.getClass().getSimpleName());
            }
         }
      }
   }

   public void onChunkData(ChunkDataS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      DetachedClientWorld var2 = this.instance.execute();
      if (var2 != null) {
         int var3 = var1.getChunkX();
         int var4 = var1.getChunkZ();
         ChunkData var5 = var1.getChunkData();
         var2.getChunkManager().loadChunkFromPacket(var3, var4, var5.getSectionsDataBuf(), var5.getHeightmap(), var5.getBlockEntities(var3, var4));
         LightData var6 = var1.getLightData();
         ClientPlayNetworkHandlerAccessor var7 = (ClientPlayNetworkHandlerAccessor)(Object)this;
         var2.enqueueChunkUpdate(() -> {
            var7.wild$readLightData(var3, var4, var6, false);
            if (HeadlessBotEngine.handle() == this.instance) {
               WorldChunk var6x = var2.getChunkManager().getWorldChunk(var3, var4, false);
               if (var6x != null) {
                  var7.wild$scheduleRenderChunk(var6x, var3, var4);
                  this.data.worldRenderer.scheduleNeighborUpdates(var6x.getPos());
               }
            }
         });
      }
   }

   public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      DetachedClientWorld var2 = this.instance.execute();
      if (var2 != null) {
         var1.visitUpdates((var1x, var2x) -> var2.handleBlockUpdate(var1x, var2x, 19));
      }
   }

   public void onUnloadChunk(UnloadChunkS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      DetachedClientWorld var2 = this.instance.execute();
      if (var2 != null) {
         var2.getChunkManager().unload(var1.pos());
      }
   }

   public void onPlayerRotation(PlayerRotationS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         var2.setYaw(var1.yRot());
         var2.setPitch(var1.xRot());
         var2.updateLastAngles();
         handle(var2);
         var2.process();
         this.connection.send(new LookAndOnGround(var2.getYaw(), var2.getPitch(), var2.isOnGround(), var2.horizontalCollision));
      }
   }

   public void onPlayerRespawn(PlayerRespawnS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      DetachedClientWorld var3 = this.instance.execute();
      ClientPlayerInteractionManager var4 = this.instance.check();
      if (var2 != null && var3 != null && var4 != null) {
         CommonPlayerSpawnInfo var5 = var1.commonPlayerSpawnInfo();
         ClientPlayNetworkHandlerAccessor var6 = (ClientPlayNetworkHandlerAccessor)(Object)this;
         boolean var7 = !var3.getRegistryKey().equals(var5.dimension());
         DetachedClientWorld var8 = var3;
         if (var7) {
            Properties var9 = var3.getLevelProperties();
            Properties var10 = new Properties(var9.getDifficulty(), var9.isHardcore(), var5.isFlat());
            var8 = new DetachedClientWorld(
               this.instance,
               this,
               var10,
               var5.dimension(),
               var5.dimensionType(),
               var6.wild$getChunkLoadDistance(),
               var6.wild$getSimulationDistance(),
               var5.isDebug(),
               var5.seed(),
               var5.seaLevel()
            );
            var8.handle(var3);
            var6.wild$setWorldProperties(var10);
            var6.wild$setWorld(var8);
         }

         boolean var12 = var1.hasFlag((byte)2);
         BackgroundClientPlayer var13 = new BackgroundClientPlayer(
            this.instance,
            this.data,
            var8,
            this,
            var2.getStatHandler(),
            var2.getRecipeBook(),
            var12 ? var2.getLastPlayerInput() : PlayerInput.DEFAULT,
            var12 && var2.isSprinting()
         );
         var13.setId(var2.getId());
         if (var12) {
            List var11 = var2.getDataTracker().getChangedEntries();
            if (var11 != null) {
               var13.getDataTracker().writeUpdatedEntries(var11);
            }

            var13.setVelocity(var2.getVelocity());
            var13.setYaw(var2.getYaw());
            var13.setPitch(var2.getPitch());
         } else {
            var13.init();
            var13.setYaw(-180.0F);
         }

         if (var1.hasFlag((byte)1)) {
            var13.getAttributes().setFrom(var2.getAttributes());
         } else {
            var13.getAttributes().setBaseFrom(var2.getAttributes());
         }

         var13.setReducedDebugInfo(var2.hasReducedDebugInfo());
         var13.setShowsDeathScreen(var2.showsDeathScreen());
         var13.setLastDeathPos(var5.lastDeathLocation());
         var13.setPortalCooldown(var5.portalCooldown());
         handle(var13);
         var8.addEntity(var13);
         var4.setGameModes(var5.gameMode(), var5.lastGameMode());
         var4.copyAbilities(var13);
         var2.input = new Input();
         this.instance.handle(var8);
         this.instance.handle(var13);
         this.instance.process(false);
         if (HeadlessBotEngine.handle() == this.instance) {
            HeadlessBotEngine.handle(this.instance);
            Screen var14 = this.data.currentScreen;
            if (var14 instanceof DeathScreen || var14 instanceof DownloadingTerrainScreen) {
               this.data.setScreen(null);
            }
         }
      } else {
         HeadlessBotConnector.handle(this.instance, "§cignored respawn without a complete bot session");
      }
   }

   public void onGameStateChange(GameStateChangeS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onGameStateChange(var1);
      } else {
         DetachedClientWorld var2 = this.instance.execute();
         BackgroundClientPlayer var3 = this.instance.prepare();
         Reason var4 = var1.getReason();
         float var5 = var1.getValue();
         if (var4 == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
            if (this.instance.check() != null) {
               this.instance.check().setGameMode(GameMode.byIndex(MathHelper.floor(var5 + 0.5F)));
            }
         } else if (var2 != null && var4 == GameStateChangeS2CPacket.RAIN_STARTED) {
            var2.getLevelProperties().setRaining(true);
            var2.setRainGradient(0.0F);
         } else if (var2 != null && var4 == GameStateChangeS2CPacket.RAIN_STOPPED) {
            var2.getLevelProperties().setRaining(false);
            var2.setRainGradient(1.0F);
         } else if (var2 != null && var4 == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
            var2.setRainGradient(var5);
         } else if (var2 != null && var4 == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
            var2.setThunderGradient(var5);
         } else if (var3 != null && var4 == GameStateChangeS2CPacket.IMMEDIATE_RESPAWN) {
            var3.setShowsDeathScreen(var5 == 0.0F);
         } else if (var3 != null && var4 == GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED) {
            var3.setLimitedCraftingEnabled(var5 == 1.0F);
         }
      }
   }

   public void onPlayerAbilities(PlayerAbilitiesS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         var2.getAbilities().flying = var1.isFlying();
         var2.getAbilities().creativeMode = var1.isCreativeMode();
         var2.getAbilities().invulnerable = var1.isInvulnerable();
         var2.getAbilities().allowFlying = var1.allowFlying();
         var2.getAbilities().setFlySpeed(var1.getFlySpeed());
         var2.getAbilities().setWalkSpeed(var1.getWalkSpeed());
      }
   }

   public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         var2.setExperience(var1.getBarProgress(), var1.getExperienceLevel(), var1.getExperience());
      }
   }

   public void onCloseScreen(CloseScreenS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onCloseScreen(var1);
      } else {
         BackgroundClientPlayer var2 = this.instance.prepare();
         if (var2 != null) {
            var2.currentScreenHandler = var2.playerScreenHandler;
         }
      }
   }

   public void onCooldownUpdate(CooldownUpdateS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      BackgroundClientPlayer var2 = this.instance.prepare();
      if (var2 != null) {
         if (var1.cooldown() == 0) {
            var2.getItemCooldownManager().remove(var1.cooldownGroup());
         } else {
            var2.getItemCooldownManager().set(var1.cooldownGroup(), var1.cooldown());
         }
      }
   }

   public void onCraftFailedResponse(CraftFailedResponseS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onCraftFailedResponse(var1);
      }
   }

   public void onDeathMessage(DeathMessageS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.update(this.instance)) {
         BackgroundClientPlayer var2 = this.instance.prepare();
         if (var2 != null && var2.getId() == var1.playerId()) {
            var2.setHealth(0.0F);
            if (!this.instance.select()) {
               this.instance.process(true);
               if (HeadlessBotEngine.handle() == this.instance && this.data.player == var2 && var2.showsDeathScreen()) {
                  DetachedClientWorld var3 = this.instance.execute();
                  boolean var4 = var3 != null && var3.getLevelProperties().isHardcore();
                  this.data.setScreen(new DeathScreen(var1.message(), var4));
                  HeadlessBotConnector.handle(this.instance, "died");
               } else {
                  this.connection.send(new ClientStatusC2SPacket(Mode.PERFORM_RESPAWN));
                  HeadlessBotConnector.handle(this.instance, "respawning after death ...");
               }
            }
         }
      }
   }

   public void onExplosion(ExplosionS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onExplosion(var1);
      } else {
         BackgroundClientPlayer var2 = this.instance.prepare();
         if (var2 != null) {
            var1.playerKnockback().ifPresent(var2::addVelocityInternal);
         }
      }
   }

   public void onItemPickupAnimation(ItemPickupAnimationS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onItemPickupAnimation(var1);
      }
   }

   public void onLookAt(LookAtS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onLookAt(var1);
      }
   }

   public void onVehicleMove(VehicleMoveS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onVehicleMove(var1);
      }
   }

   public void onSetCameraEntity(SetCameraEntityS2CPacket var1) {
   }

   public void onOpenHorseScreen(OpenHorseScreenS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onOpenHorseScreen(var1);
      } else {
         BackgroundClientPlayer var2 = this.instance.prepare();
         DetachedClientWorld var3 = this.instance.execute();
         if (var2 != null && var3 != null) {
            if (var3.getEntityById(var1.getHorseId()) instanceof AbstractHorseEntity var5) {
               int var6 = var1.getSlotColumnCount();
               SimpleInventory var7 = new SimpleInventory(AbstractHorseEntity.getInventorySize(var6));
               var2.currentScreenHandler = new HorseScreenHandler(var1.getSyncId(), var2.getInventory(), var7, var5, var6);
            }
         }
      }
   }

   public void onOpenWrittenBook(OpenWrittenBookS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onOpenWrittenBook(var1);
      }
   }

   public void onSignEditorOpen(SignEditorOpenS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onSignEditorOpen(var1);
      }
   }

   public void onSetTradeOffers(SetTradeOffersS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onSetTradeOffers(var1);
      } else {
         BackgroundClientPlayer var2 = this.instance.prepare();
         if (var2 != null && var2.currentScreenHandler.syncId == var1.getSyncId() && var2.currentScreenHandler instanceof MerchantScreenHandler var3) {
            var3.setOffers(var1.getOffers());
            var3.setLevelProgress(var1.getLevelProgress());
            var3.setExperienceFromServer(var1.getExperience());
            var3.setLeveled(var1.isLeveled());
            var3.setCanRefreshTrades(var1.isRefreshable());
         }
      }
   }

   public void onStatistics(StatisticsS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onStatistics(var1);
      }
   }

   public void onRecipeBookAdd(RecipeBookAddS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onRecipeBookAdd(var1);
      }
   }

   public void onRecipeBookRemove(RecipeBookRemoveS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onRecipeBookRemove(var1);
      }
   }

   public void onRecipeBookSettings(RecipeBookSettingsS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onRecipeBookSettings(var1);
      }
   }

   public void onPlaySound(PlaySoundS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onPlaySound(var1);
      }
   }

   public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onPlaySoundFromEntity(var1);
      }
   }

   public void onParticle(ParticleS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onParticle(var1);
      }
   }

   public void onTitle(TitleS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onTitle(var1);
      }
   }

   public void onTitleClear(ClearTitleS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onTitleClear(var1);
      }
   }

   public void onTitleFade(TitleFadeS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onTitleFade(var1);
      }
   }

   public void onSubtitle(SubtitleS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onSubtitle(var1);
      }
   }

   public void onOverlayMessage(OverlayMessageS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onOverlayMessage(var1);
      }
   }

   public void onBossBar(BossBarS2CPacket var1) {
   }

   public void onRemoveMessage(RemoveMessageS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onRemoveMessage(var1);
      }
   }

   public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onBlockBreakingProgress(var1);
      }
   }

   public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onBlockEntityUpdate(var1);
      }
   }

   public void onBlockEvent(BlockEventS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onBlockEvent(var1);
      }
   }

   public void onWorldEvent(WorldEventS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onWorldEvent(var1);
      }
   }

   public void onChunkBiomeData(ChunkBiomeDataS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onChunkBiomeData(var1);
      }
   }

   public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onChunkLoadDistance(var1);
      }
   }

   public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onPlayerSpawnPosition(var1);
      }
   }

   public void onStopSound(StopSoundS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onStopSound(var1);
      }
   }

   public void onDebugSample(DebugSampleS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onDebugSample(var1);
      }
   }

   public void onTestInstanceBlockStatus(TestInstanceBlockStatusS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onTestInstanceBlockStatus(var1);
      }
   }

   public void onTickStep(TickStepS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onTickStep(var1);
      }
   }

   public void onUpdateTickRate(UpdateTickRateS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onUpdateTickRate(var1);
      }
   }

   public void onCustomPayload(CustomPayload var1) {
   }

   public void onShowDialog(ShowDialogS2CPacket var1) {
      if (HeadlessBotEngine.handle() == this.instance) {
         super.onShowDialog(var1);
      }
   }

   public void onEnterReconfiguration(EnterReconfigurationS2CPacket var1) {
      NetworkThreadUtils.forceMainThread(var1, this, this.data);
      if (!HeadlessBotEngine.update(this.instance)) {
         HeadlessBotEngine.compute(this.instance);
      } else {
         this.instance.compute(false);
         if (HeadlessBotEngine.handle() == this.instance && this.instance.prepare() != null) {
            this.instance.prepare().input = new Input();
         }

         this.context = true;
         ClientPlayNetworkHandlerAccessor var2 = (ClientPlayNetworkHandlerAccessor)(Object)this;
         ClientConnectionState var3 = new ClientConnectionState(
            this.getProfile(),
            this.worldSession,
            var2.wild$combinedDynamicRegistries(),
            var2.wild$enabledFeatures(),
            this.brand,
            this.serverInfo,
            this.postDisconnectScreen,
            this.serverCookies,
            null,
            this.customReportDetails,
            this.getServerLinks()
         );
         this.instance.handle(false);
         this.instance.process(false);
         HeadlessBotEngine.handle(this.instance, HeadlessBotEngine.Status.RECONFIGURING, "Reconfiguring (server switch) ...");
         this.connection.transitionInbound(ConfigurationStates.S2C, new BotConfigurationNetworkHandler(this.data, this.connection, var3, this.instance));
         this.connection.send(AcknowledgeReconfigurationC2SPacket.INSTANCE);
         this.connection.transitionOutbound(ConfigurationStates.C2S);
         HeadlessBotConnector.handle(this.instance, "reconfiguring (server switch) ...");
      }
   }

   public void onResourcePackSend(ResourcePackSendS2CPacket var1) {
      UUID var2 = var1.id();
      this.connection.send(new ResourcePackStatusC2SPacket(var2, Status.ACCEPTED));
      this.connection.send(new ResourcePackStatusC2SPacket(var2, Status.DOWNLOADED));
      this.connection.send(new ResourcePackStatusC2SPacket(var2, Status.SUCCESSFULLY_LOADED));
      HeadlessBotConnector.handle(this.instance, "resource pack auto-accepted (play)");
   }

   public void onResourcePackRemove(ResourcePackRemoveS2CPacket var1) {
   }

   public void onServerTransfer(ServerTransferS2CPacket var1) {
      this.connection.disconnect(Text.translatable("disconnect.transfer"));
   }

   public void onDisconnected(DisconnectionInfo var1) {
      String var2 = "disconnected: " + var1.reason().getString();
      HeadlessBotEngine.process(this.instance, var2);
      HeadlessBotConnector.handle(this.instance, "§c" + var2);
      HeadlessBotEngine.compute(this.instance);
   }

   private static void handle(BackgroundClientPlayer var0) {
      float var1 = var0.getYaw();
      var0.setHeadYaw(var1);
      var0.lastHeadYaw = var1;
      var0.setBodyYaw(var1);
      var0.lastBodyYaw = var1;
   }

   public boolean isConnectionOpen() {
      return this.connection.isOpen();
   }
}
