package ru.wild.automation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket.Reason;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.wild.mixin.acceser.MinecraftClientAccessor;
import ru.wild.core.ClientContextExecutor;

public final class HeadlessBotEngine {
   public static final List<HeadlessBotSession> instance = new CopyOnWriteArrayList<>();
   public static final List<HeadlessBotSession> data = new CopyOnWriteArrayList<>();
   private static final Set<String> context = ConcurrentHashMap.newKeySet();
   private static final Object config = new Object();
   private static final Map<String, HeadlessBotEngine.Task> state = new LinkedHashMap<>();
   private static final AtomicLong cache = new AtomicLong();
   private static volatile HeadlessBotSession output;
   private static ClientWorld current;
   private static ClientPlayerEntity active;
   private static ClientPlayerInteractionManager mode;
   private static boolean selection;
   private static boolean enabled;
   private static HeadlessBotSession renderer;
   private static HeadlessBotSession handler;
   private static double animationDraw;
   private static double pointEncode;
   private static double animator;
   private static float source;
   private static float target;
   private static boolean pending;
   private static boolean previous;
   private static int latest;

   private HeadlessBotEngine() {
   }

   public static HeadlessBotSession handle() {
      return output;
   }

   public static List<HeadlessBotEngine.ServerEntry> process() {
      synchronized (config) {
         ArrayList var1 = new ArrayList(state.size());

         for (HeadlessBotEngine.Task var3 : state.values()) {
            var1.add(var3.handle());
         }

         return List.copyOf(var1);
      }
   }

   public static HeadlessBotEngine.ServerEntry handle(String var0) {
      synchronized (config) {
         HeadlessBotEngine.Task var2 = state.get(onTick(var0));
         return var2 == null ? null : var2.handle();
      }
   }

   public static boolean process(String var0) {
      HeadlessBotEngine.ServerEntry var1 = handle(var0);
      if (var1 != null && !var1.address().isBlank()) {
         MinecraftClient var2 = drawAnimation();
         if (var2 != null && !var2.isOnThread()) {
            var2.execute(() -> process(var0));
            return true;
         } else {
            compute(var0);
            return HeadlessBotConnector.handle(var1.name(), var1.address());
         }
      } else {
         return false;
      }
   }

   public static boolean compute(String var0) {
      Thread var2 = null;
      boolean var3 = false;
      HeadlessBotSession var1;
      synchronized (config) {
         HeadlessBotEngine.Task var5 = state.get(onTick(var0));
         if (var5 == null) {
            return false;
         }

         var1 = var5.output;
         if (var1 == null) {
            if (!var5.context.handle()) {
               return false;
            }

            var2 = var5.current;
            var5.current = null;
            var5.cache = cache.incrementAndGet();
            var5.handle(HeadlessBotEngine.Status.DISCONNECTED, "Connection cancelled");
            var3 = true;
         } else {
            var5.handle(HeadlessBotEngine.Status.DISCONNECTED, "Disconnected by user");
         }
      }

      if (var3) {
         if (var2 != null) {
            var2.interrupt();
         }

         apply(var0);
         return true;
      } else {
         compute(var1);
         return true;
      }
   }

   public static boolean resolve(String var0) {
      String var1 = onTick(var0);
      synchronized (config) {
         HeadlessBotEngine.Task var3 = state.get(var1);
         if (var3 == null || var3.output != null || var3.context.handle()) {
            return false;
         }

         state.remove(var1);
      }

      apply(var0);
      BotProfileManager.process();
      return true;
   }

   static boolean handle(String var0, String var1) {
      String var2 = var0 == null ? "" : var0.trim();
      String var3 = var1 == null ? "" : var1.trim();
      String var4 = onTick(var2);
      if (!var4.isEmpty() && !var3.isEmpty()) {
         synchronized (config) {
            if (state.containsKey(var4)) {
               return false;
            }

            state.put(var4, HeadlessBotEngine.Task.handle(var2, var3));
            return true;
         }
      } else {
         return false;
      }
   }

   public static ClientPlayerEntity compute() {
      return active;
   }

   public static boolean handle(ClientPlayNetworkHandler var0) {
      if (output == null) {
         return false;
      }

      if (var0 instanceof HeadlessBotPlayHandler) {
         return false;
      }

      MinecraftClient var1 = drawAnimation();
      return var1 != null && var0 != var1.getNetworkHandler();
   }

   public static void handle(PlayerPositionLookS2CPacket var0, ClientPlayNetworkHandler var1) {
      ClientConnection var2 = var1.getConnection();
      ClientPlayerEntity var3 = active;
      if (var3 != null && !var3.hasVehicle()) {
         PlayerPosition var4 = PlayerPosition.fromEntity(var3);
         PlayerPosition var5 = PlayerPosition.apply(var4, var0.change(), var0.relatives());
         var3.setPosition(var5.position());
         var3.setVelocity(var5.deltaMovement());
         var3.setYaw(var5.yaw());
         var3.setPitch(var5.pitch());
      }

      var2.send(new TeleportConfirmC2SPacket(var0.teleportId()));
      if (var3 != null) {
         var2.send(new Full(var3.getX(), var3.getY(), var3.getZ(), var3.getYaw(), var3.getPitch(), var3.isOnGround(), var3.horizontalCollision));
         handle(var3);
      }
   }

   public static boolean handle(HeadlessBotSession var0) {
      MinecraftClient var1 = drawAnimation();
      if (var0 != null
         && var1 != null
         && var1.isOnThread()
         && !enabled
         && instance.contains(var0)
         && var0.update()
         && var0.onTick()
         && var0.execute() != null
         && var0.prepare() != null
         && var0.check() != null) {
         HeadlessBotSession var2 = output;
         if (output == null) {
            if (var1.world == null
               || var1.player == null
               || var1.interactionManager == null
               || var1.player.networkHandler == null
               || !var1.player.networkHandler.getConnection().isOpen()) {
               return false;
            }

            current = var1.world;
            active = var1.player;
            mode = var1.interactionManager;
         }

         if (var2 != null && var2 != var0 && var2.prepare() != null) {
            var2.prepare().input = new Input();
         }

         output = var0;
         if (active != null) {
            active.input = new Input();
            selection = false;
            handle(active);
         }

         var1.world = var0.execute();
         ((MinecraftClientAccessor)var1).wild$setWorld(var0.execute());
         var1.player = var0.prepare();
         var1.interactionManager = var0.check();
         var0.prepare().input = new KeyboardInput(var1.options);
         var1.setCameraEntity(var0.prepare());
         var1.worldRenderer.reload();
         return true;
      } else {
         return false;
      }
   }

   public static void resolve() {
      animate();
      MinecraftClient var0 = drawAnimation();
      HeadlessBotSession var1 = output;
      output = null;
      if (var1 != null || current != null || active != null || mode != null) {
         if (var0 == null) {
            current = null;
            active = null;
            mode = null;
         } else {
            if (var1 != null && var1.prepare() != null) {
               var1.prepare().input = new Input();
            }

            if (current != null) {
               var0.world = current;
               ((MinecraftClientAccessor)var0).wild$setWorld(current);
            }

            if (active != null) {
               active.input = new KeyboardInput(var0.options);
               var0.player = active;
               var0.setCameraEntity(active);
            }

            if (mode != null) {
               var0.interactionManager = mode;
            }

            selection = false;
            var0.worldRenderer.reload();
            current = null;
            active = null;
            mode = null;
         }
      }
   }

   public static synchronized boolean update(String var0) {
      String var1 = onTick(var0);
      return !var1.isEmpty() && prepare(var1) == null ? context.add(var1) : false;
   }

   public static void apply(String var0) {
      context.remove(onTick(var0));
   }

   static void handle(String var0, long var1) {
      if (compute(var0, var1)) {
         apply(var0);
      }
   }

   public static boolean process(HeadlessBotSession var0) {
      if (!update(var0)) {
         return false;
      }

      data.remove(var0);
      if (!instance.contains(var0)) {
         var0.refresh().handle();
         instance.add(var0);
      }

      if (!update(var0)) {
         instance.remove(var0);
         return false;
      } else {
         return true;
      }
   }

   public static void compute(HeadlessBotSession var0) {
      if (var0 != null) {
         MinecraftClient var1 = drawAnimation();
         if (var1 != null && !var1.isOnThread()) {
            var1.execute(() -> compute(var0));
         } else {
            boolean var2 = var0.tick();
            if (output == var0) {
               resolve();
            }

            if (renderer == var0) {
               renderer = null;
               enabled = false;
            }

            if (handler == var0) {
               handler = null;
            }

            data.remove(var0);
            instance.remove(var0);
            if (prepare(var0)) {
               apply(var0.handle());
            }

            if (var2) {
               try {
                  var0.refresh().resolve();
               } catch (Throwable var5) {
               }
            }

            try {
               var0.resolve();
            } catch (Throwable var4) {
            }

            var0.encodePoint();
         }
      }
   }

   public static HeadlessBotSession execute(String var0) {
      return prepare(onTick(var0));
   }

   public static List<HeadlessBotSession> update() {
      ArrayList var0 = new ArrayList(instance.size() + data.size());
      var0.addAll(instance);

      for (HeadlessBotSession var2 : data) {
         if (!var0.contains(var2)) {
            var0.add(var2);
         }
      }

      return List.copyOf(var0);
   }

   public static void apply() {
      for (HeadlessBotSession var1 : data) {
         apply(var1);
      }

      for (HeadlessBotSession var3 : instance) {
         if (apply(var3) && instance.contains(var3) && var3 != output) {
            execute(var3);
         }
      }

      if (output != null) {
         encodePoint();
      }
   }

   private static void encodePoint() {
      ClientPlayerEntity var0 = active;
      if (var0 != null) {
         ClientPlayNetworkHandler var1 = var0.networkHandler;
         if (var1 != null) {
            ClientConnection var2 = var1.getConnection();
            if (var2 != null && var2.isOpen()) {
               if (var2.getPacketListener() == var1) {
                  try {
                     var2.tick();
                     if (output == null || active != var0) {
                        return;
                     }

                     if (var0.getHealth() <= 0.0F) {
                        if (!selection) {
                           selection = true;
                           var1.sendPacket(new ClientStatusC2SPacket(Mode.PERFORM_RESPAWN));
                        }
                     } else {
                        selection = false;
                     }

                     var0.tick();
                     handle(var0, var1);
                     var1.sendPacket(ClientTickEndC2SPacket.INSTANCE);
                  } catch (Throwable var5) {
                     var5.printStackTrace();
                     HeadlessBotSession var4 = output;
                     if (var4 != null) {
                        HeadlessBotConnector.handle(var4, "§cbackground host tick failed: " + var5.getClass().getSimpleName());
                     }

                     resolve();
                  }
               }
            } else {
               if (var2 != null) {
                  var2.handleDisconnection();
               }

               if (output != null) {
                  resolve();
               }
            }
         }
      }
   }

   private static void handle(ClientPlayerEntity var0) {
      animationDraw = var0.getX();
      pointEncode = var0.getY();
      animator = var0.getZ();
      source = var0.getYaw();
      target = var0.getPitch();
      pending = var0.isOnGround();
      previous = var0.horizontalCollision;
      latest = 0;
   }

   private static void handle(ClientPlayerEntity var0, ClientPlayNetworkHandler var1) {
      double var2 = var0.getX() - animationDraw;
      double var4 = var0.getY() - pointEncode;
      double var6 = var0.getZ() - animator;
      double var8 = var0.getYaw() - source;
      double var10 = var0.getPitch() - target;
      latest++;
      boolean var12 = MathHelper.squaredMagnitude(var2, var4, var6) > MathHelper.square(2.0E-4) || latest >= 20;
      boolean var13 = var8 != 0.0 || var10 != 0.0;
      if (var12 && var13) {
         var1.sendPacket(new Full(var0.getX(), var0.getY(), var0.getZ(), var0.getYaw(), var0.getPitch(), var0.isOnGround(), var0.horizontalCollision));
      } else if (var12) {
         var1.sendPacket(new PositionAndOnGround(var0.getX(), var0.getY(), var0.getZ(), var0.isOnGround(), var0.horizontalCollision));
      } else if (var13) {
         var1.sendPacket(new LookAndOnGround(var0.getYaw(), var0.getPitch(), var0.isOnGround(), var0.horizontalCollision));
      } else if (pending != var0.isOnGround() || previous != var0.horizontalCollision) {
         var1.sendPacket(new OnGroundOnly(var0.isOnGround(), var0.horizontalCollision));
      }

      if (var12) {
         animationDraw = var0.getX();
         pointEncode = var0.getY();
         animator = var0.getZ();
         latest = 0;
      }

      if (var13) {
         source = var0.getYaw();
         target = var0.getPitch();
      }

      pending = var0.isOnGround();
      previous = var0.horizontalCollision;
   }

   public static void handle(HealthUpdateS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         var1.updateHealth(var0.getHealth());
         var1.getHungerManager().setFoodLevel(var0.getFood());
         var1.getHungerManager().setSaturationLevel(var0.getSaturation());
      }
   }

   public static void handle(ExplosionS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         var0.playerKnockback().ifPresent(var1::addVelocityInternal);
      }
   }

   public static void handle(InventoryS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         if (var0.syncId() == 0) {
            var1.playerScreenHandler.updateSlotStacks(var0.revision(), var0.contents(), var0.cursorStack());
         } else if (var0.syncId() == var1.currentScreenHandler.syncId) {
            var1.currentScreenHandler.updateSlotStacks(var0.revision(), var0.contents(), var0.cursorStack());
         }
      }
   }

   public static void handle(PlayerAbilitiesS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         var1.getAbilities().flying = var0.isFlying();
         var1.getAbilities().creativeMode = var0.isCreativeMode();
         var1.getAbilities().invulnerable = var0.isInvulnerable();
         var1.getAbilities().allowFlying = var0.allowFlying();
         var1.getAbilities().setFlySpeed(var0.getFlySpeed());
         var1.getAbilities().setWalkSpeed(var0.getWalkSpeed());
      }
   }

   public static void handle(GameStateChangeS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      ClientWorld var2 = current;
      Reason var3 = var0.getReason();
      float var4 = var0.getValue();
      if (var3 == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
         if (mode != null) {
            mode.setGameMode(GameMode.byIndex(MathHelper.floor(var4 + 0.5F)));
         }
      } else if (var2 != null && var3 == GameStateChangeS2CPacket.RAIN_STARTED) {
         var2.getLevelProperties().setRaining(true);
         var2.setRainGradient(0.0F);
      } else if (var2 != null && var3 == GameStateChangeS2CPacket.RAIN_STOPPED) {
         var2.getLevelProperties().setRaining(false);
         var2.setRainGradient(1.0F);
      } else if (var2 != null && var3 == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
         var2.setRainGradient(var4);
      } else if (var2 != null && var3 == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
         var2.setThunderGradient(var4);
      } else if (var1 != null && var3 == GameStateChangeS2CPacket.IMMEDIATE_RESPAWN) {
         var1.setShowsDeathScreen(var4 == 0.0F);
      } else if (var1 != null && var3 == GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED) {
         var1.setLimitedCraftingEnabled(var4 == 1.0F);
      }
   }

   public static void handle(UpdateSelectedSlotS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null && PlayerInventory.isValidHotbarIndex(var0.slot())) {
         var1.getInventory().setSelectedSlot(var0.slot());
      }
   }

   public static void handle(ExperienceBarUpdateS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         var1.setExperience(var0.getBarProgress(), var0.getExperienceLevel(), var0.getExperience());
      }
   }

   public static void handle(ScreenHandlerSlotUpdateS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         if (var0.getSyncId() == 0) {
            var1.playerScreenHandler.setStackInSlot(var0.getSlot(), var0.getRevision(), var0.getStack());
         } else if (var1.currentScreenHandler.syncId == var0.getSyncId()) {
            var1.currentScreenHandler.setStackInSlot(var0.getSlot(), var0.getRevision(), var0.getStack());
         }
      }
   }

   public static void handle(ScreenHandlerPropertyUpdateS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null && var1.currentScreenHandler.syncId == var0.getSyncId()) {
         var1.currentScreenHandler.setProperty(var0.getPropertyId(), var0.getValue());
      }
   }

   public static void handle(SetCursorItemS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         var1.currentScreenHandler.setCursorStack(var0.contents());
      }
   }

   public static void handle(SetPlayerInventoryS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         var1.getInventory().setStack(var0.slot(), var0.contents());
      }
   }

   public static void execute() {
      ClientPlayerEntity var0 = active;
      if (var0 != null) {
         var0.currentScreenHandler = var0.playerScreenHandler;
      }
   }

   public static void handle(CooldownUpdateS2CPacket var0) {
      ClientPlayerEntity var1 = active;
      if (var1 != null) {
         if (var0.cooldown() == 0) {
            var1.getItemCooldownManager().remove(var0.cooldownGroup());
         } else {
            var1.getItemCooldownManager().set(var0.cooldownGroup(), var0.cooldown());
         }
      }
   }

   public static void prepare() {
      HeadlessBotSession var0 = output;
      if (var0 != null) {
         resolve();
         handler = var0;
      }
   }

   public static void check() {
      HeadlessBotSession var0 = handler;
      handler = null;
      selection = false;
      if (output == null && var0 != null && instance.contains(var0) && var0.update() && var0.onTick()) {
         handle(var0);
      }
   }

   public static boolean onTick() {
      return enabled;
   }

   public static void select() {
      HeadlessBotSession var0 = output;
      if (var0 != null) {
         resolve();
         renderer = var0;
         enabled = true;
      }
   }

   public static void refresh() {
      HeadlessBotSession var0 = renderer;
      enabled = false;
      renderer = null;
      if (output == null && var0 != null && instance.contains(var0) && var0.update() && var0.onTick()) {
         MinecraftClient var1 = drawAnimation();
         if (var1 != null) {
            if (var1.currentScreen instanceof DownloadingTerrainScreen) {
               var1.setScreen(null);
            }

            handle(var0);
         }
      }
   }

   public static void process(ClientPlayNetworkHandler var0) {
      if (!(var0 instanceof HeadlessBotPlayHandler)) {
         if (output != null && active != null && active.networkHandler == var0) {
            resolve();
         } else {
            animate();
         }
      }
   }

   public static void render() {
      animate();
   }

   private static boolean apply(HeadlessBotSession var0) {
      try {
         if (var0.compute().isOpen()) {
            var0.compute().tick();
            return true;
         } else {
            var0.compute().handleDisconnection();
            compute(var0);
            return false;
         }
      } catch (Throwable var3) {
         var3.printStackTrace();
         String var2 = "network tick failed: " + var3.getClass().getSimpleName();
         handle(var0, var2);
         HeadlessBotConnector.handle(var0, "§c" + var2);
         compute(var0);
         return false;
      }
   }

   private static void execute(HeadlessBotSession var0) {
      DetachedClientWorld var1 = var0.execute();
      BackgroundClientPlayer var2 = var0.prepare();
      ClientPlayerInteractionManager var3 = var0.check();
      if (var1 != null && var2 != null && var3 != null && var0.onTick()) {
         try {
            ClientContextExecutor.handle(var0, () -> {
               if (var2.getHealth() <= 0.0F) {
                  if (!var0.select()) {
                     var0.process(true);
                     var0.handle(new ClientStatusC2SPacket(Mode.PERFORM_RESPAWN));
                  }
               } else {
                  var0.process(false);
               }

               var3.tick();
               var1.tickEntities();
               var1.tick(() -> true);
               var0.handle(ClientTickEndC2SPacket.INSTANCE);
            });
         } catch (Throwable var6) {
            var6.printStackTrace();
            String var5 = "world tick failed: " + var6.getClass().getSimpleName();
            handle(var0, var5);
            HeadlessBotConnector.handle(var0, "§c" + var5);
            compute(var0);
         }
      }
   }

   public static void tick() {
      ArrayList<Thread> var0 = new ArrayList<>();
      synchronized (config) {
         for (HeadlessBotEngine.Task var3 : state.values()) {
            if (var3.output == null && var3.context.handle()) {
               if (var3.current != null) {
                  var0.add(var3.current);
                  var3.current = null;
               }

               var3.cache = cache.incrementAndGet();
               var3.handle(HeadlessBotEngine.Status.DISCONNECTED, "Connection cancelled");
            }
         }
      }

      for (Thread var9 : var0) {
         var9.interrupt();
      }

      for (HeadlessBotSession var10 : instance) {
         compute(var10);
      }

      for (HeadlessBotSession var11 : data) {
         compute(var11);
      }

      instance.clear();
      data.clear();
      context.clear();
      animate();
   }

   public static HeadlessBotSession handle(Entity var0) {
      if (var0 == null) {
         return null;
      }

      for (HeadlessBotSession var2 : instance) {
         if (var2.prepare() == var0) {
            return var2;
         }
      }

      return null;
   }

   public static boolean process(Entity var0) {
      for (HeadlessBotSession var2 : instance) {
         if (var2.prepare() == var0) {
            return true;
         }
      }

      return false;
   }

   public static MinecraftClient drawAnimation() {
      return MinecraftClient.getInstance();
   }

   private static void animate() {
      enabled = false;
      renderer = null;
      handler = null;

      for (HeadlessBotSession var1 : instance) {
         var1.compute(false);
      }

      for (HeadlessBotSession var3 : data) {
         var3.compute(false);
      }
   }

   private static HeadlessBotSession prepare(String var0) {
      if (var0.isEmpty()) {
         return null;
      }

      for (HeadlessBotSession var2 : instance) {
         if (onTick(var2.handle()).equals(var0)) {
            return var2;
         }
      }

      for (HeadlessBotSession var4 : data) {
         if (onTick(var4.handle()).equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   static long process(String var0, String var1) {
      String var2 = onTick(var0);
      if (!var2.isEmpty() && var1 != null && !var1.isBlank()) {
         long var3;
         synchronized (config) {
            HeadlessBotEngine.Task var6 = state.get(var2);
            if (var6 != null && (var6.output != null || var6.context.handle())) {
               return -1L;
            }

            var3 = cache.incrementAndGet();
            if (var6 == null) {
               var6 = new HeadlessBotEngine.Task(var0.trim(), var1.trim(), var3);
               state.put(var2, var6);
            } else {
               var6.instance = var0.trim();
               var6.data = var1.trim();
               var6.cache = var3;
               var6.output = null;
               var6.current = null;
               var6.handle(HeadlessBotEngine.Status.RESOLVING, "Resolving " + var6.data + " ...");
            }
         }

         BotProfileManager.process();
         return var3;
      } else {
         return -1L;
      }
   }

   static boolean process(String var0, long var1) {
      synchronized (config) {
         HeadlessBotEngine.Task var4 = state.get(onTick(var0));
         return var4 != null && var4.cache == var1 && var4.context.handle();
      }
   }

   static boolean compute(String var0, long var1) {
      synchronized (config) {
         HeadlessBotEngine.Task var4 = state.get(onTick(var0));
         return var4 != null && var4.cache == var1;
      }
   }

   static boolean resolve(HeadlessBotSession var0) {
      synchronized (config) {
         return check(var0) != null;
      }
   }

   public static boolean update(HeadlessBotSession var0) {
      synchronized (config) {
         return var0 != null && !var0.drawAnimation() && var0.update() && check(var0) != null;
      }
   }

   static boolean handle(String var0, long var1, Thread var3) {
      synchronized (config) {
         HeadlessBotEngine.Task var5 = state.get(onTick(var0));
         if (var5 != null && var5.cache == var1 && var5.context.handle()) {
            var5.current = var3;
            return true;
         } else {
            return false;
         }
      }
   }

   static void process(String var0, long var1, Thread var3) {
      synchronized (config) {
         HeadlessBotEngine.Task var5 = state.get(onTick(var0));
         if (var5 != null && var5.cache == var1 && var5.current == var3) {
            var5.current = null;
         }
      }
   }

   static boolean handle(String var0, long var1, HeadlessBotSession var3) {
      synchronized (config) {
         HeadlessBotEngine.Task var5 = state.get(onTick(var0));
         if (var5 != null
            && var5.cache == var1
            && var5.context.handle()
            && var5.output == null
            && var3 != null
            && !var3.drawAnimation()
            && prepare(onTick(var0)) == null) {
            var5.output = var3;
            var5.state = System.currentTimeMillis();
            if (!data.contains(var3)) {
               data.add(var3);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   static void handle(String var0, long var1, HeadlessBotEngine.Status var3, String var4) {
      synchronized (config) {
         HeadlessBotEngine.Task var6 = state.get(onTick(var0));
         if (var6 != null && var6.cache == var1 && var6.context.handle() && var6.context != HeadlessBotEngine.Status.ERROR) {
            var6.handle(var3, var4);
         }
      }
   }
   static void handle(String var0, long var1, String var3) {
      Object var4 = config;
      synchronized (config){} // $VF: monitorenter 

      try {
         HeadlessBotEngine.Task var5 = state.get(onTick(var0));
         if (var5 != null && var5.cache == var1) {
            if (var5.context != HeadlessBotEngine.Status.ERROR) {
               var5.handle(HeadlessBotEngine.Status.ERROR, var3);
            }
         } else {
         }
      } finally {
      }
   }
   static void handle(HeadlessBotSession var0, HeadlessBotEngine.Status var1, String var2) {
      Object var3 = config;
      synchronized (config){} // $VF: monitorenter 

      try {
         HeadlessBotEngine.Task var4 = check(var0);
         if (var4 != null && (var4.context != HeadlessBotEngine.Status.ERROR || var1 == HeadlessBotEngine.Status.ERROR)) {
            var4.handle(var1, var2);
         } else {
         }
      } finally {
      }
   }
   public static void handle(HeadlessBotSession var0, String var1) {
      Object var2 = config;
      synchronized (config){} // $VF: monitorenter 

      try {
         HeadlessBotEngine.Task var3 = check(var0);
         if (var3 != null && var3.context != HeadlessBotEngine.Status.ERROR) {
            var3.handle(HeadlessBotEngine.Status.ERROR, var1);
         }
      } finally {
      }
   }

   static void process(HeadlessBotSession var0, String var1) {
      synchronized (config) {
         HeadlessBotEngine.Task var3 = check(var0);
         if (var3 != null && var3.context != HeadlessBotEngine.Status.ERROR) {
            var3.handle(HeadlessBotEngine.Status.DISCONNECTED, var1);
         }
      }
   }

   static void compute(String var0, String var1) {
      synchronized (config) {
         HeadlessBotEngine.Task var3 = state.get(onTick(var0));
         if (var3 != null && var3.context != HeadlessBotEngine.Status.ERROR) {
            var3.handle(var1);
         }
      }
   }

   static void process(String var0, long var1, String var3) {
      synchronized (config) {
         HeadlessBotEngine.Task var5 = state.get(onTick(var0));
         if (var5 != null && var5.cache == var1 && var5.context != HeadlessBotEngine.Status.ERROR) {
            var5.handle(var3);
         }
      }
   }

   static void compute(HeadlessBotSession var0, String var1) {
      synchronized (config) {
         HeadlessBotEngine.Task var3 = check(var0);
         if (var3 != null && var3.context != HeadlessBotEngine.Status.ERROR) {
            var3.handle(var1);
         }
      }
   }
   private static boolean prepare(HeadlessBotSession var0) {
      Object var1 = config;
      synchronized (config){} // $VF: monitorenter 

      try {
         HeadlessBotEngine.Task var2 = check(var0);
         if (var2 == null) {
            return false;
         }

         var2.cache = cache.incrementAndGet();
         var2.output = null;
         var2.current = null;
         if (var2.context != HeadlessBotEngine.Status.ERROR && var2.context != HeadlessBotEngine.Status.DISCONNECTED) {
            var2.handle(HeadlessBotEngine.Status.DISCONNECTED, "Disconnected");
         } else {
            var2.state = System.currentTimeMillis();
         }
         return true;
      } finally {
      }
   }

   private static HeadlessBotEngine.Task check(HeadlessBotSession var0) {
      if (var0 == null) {
         return null;
      }

      HeadlessBotEngine.Task var1 = state.get(onTick(var0.handle()));
      return var1 != null && var1.output == var0 ? var1 : null;
   }

   static String check(String var0) {
      return var0 == null ? "" : var0.replaceAll("(?i)\\u00A7[0-9A-FK-OR]", "").trim();
   }

   private static String onTick(String var0) {
      return var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT);
   }

   public record ServerEntry(String name, String address, HeadlessBotEngine.Status state, String status, long updatedAt, HeadlessBotSession bot) {
      public boolean isOnline() {
         return this.bot != null && this.bot.update() && this.bot.onTick();
      }

      public boolean isConnecting() {
         return this.state.handle();
      }
   }

   public enum Status {
      SAVED,
      RESOLVING,
      CONNECTING,
      LOGIN,
      CONFIGURING,
      JOINED,
      RECONFIGURING,
      DISCONNECTED,
      ERROR;

      public boolean handle() {
         return this == RESOLVING || this == CONNECTING || this == LOGIN || this == CONFIGURING || this == RECONFIGURING;
      }
   }

   static final class Task {
      String instance;
      String data;
      HeadlessBotEngine.Status context;
      private String config;
      long state;
      long cache;
      HeadlessBotSession output;
      Thread current;

      Task(String var1, String var2, long var3) {
         this.instance = var1;
         this.data = var2;
         this.cache = var3;
         this.context = HeadlessBotEngine.Status.RESOLVING;
         this.config = "Resolving " + var2 + " ...";
         this.state = System.currentTimeMillis();
      }

      static HeadlessBotEngine.Task handle(String var0, String var1) {
         HeadlessBotEngine.Task var2 = new HeadlessBotEngine.Task(var0, var1, 0L);
         var2.handle(HeadlessBotEngine.Status.SAVED, "Saved profile");
         return var2;
      }

      void handle(HeadlessBotEngine.Status var1, String var2) {
         this.context = var1;
         this.config = HeadlessBotEngine.check(var2);
         this.state = System.currentTimeMillis();
      }

      void handle(String var1) {
         this.config = HeadlessBotEngine.check(var1);
         this.state = System.currentTimeMillis();
      }

      HeadlessBotEngine.ServerEntry handle() {
         return new HeadlessBotEngine.ServerEntry(this.instance, this.data, this.context, this.config, this.state, this.output);
      }
   }
}
