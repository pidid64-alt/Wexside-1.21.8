package ru.wild.modules.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.command.PartyCommand;
import ru.wild.gui.hud.PartyWaypointRenderer;
import ru.wild.gui.hud.WaypointArrowRenderer;
import ru.wild.network.JsonRpcHandler;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.player.CombatRaycast;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "Party", description = "Метки пати в мире: бинд ставит метку, видна сокланам через сервер VDS", category = ModuleCategory.Misc)
public class Party extends Module {
   private static final String pending = "ws://49.12.210.82:8080/ws";
   private static final long previous = 600000L;
   private static final long latest = 420000L;
   private static final long summary = 180000L;
   private static final long matrixBlend = 250L;
   private static final double vectorMatch = 300.0;
   private static final double itemProject = 3.0;
   private static final float responseCompute = 6.0F;
   private static final double providerFetch = 1.35;
   private static final double profileDraw = 0.45;
   private static final double vectorPerform = 8.0;
   private static final char eventAttach = '\u0001';
   public final KeybindSetting source = new KeybindSetting("Кнопка метки", -1);
   public final BooleanSetting target = new BooleanSetting("Показ меток", true);
   private final PartyWaypointRenderer serverRead = new PartyWaypointRenderer();
   private final WaypointArrowRenderer positionAdvance = new WaypointArrowRenderer();
   private final Map<String, Entity> frameCheck = new HashMap<>();
   private final Set<String> moduleCollect = new HashSet<>();
   private final Map<String, Party.ModuleState> providerClose = new HashMap<>();
   private final Map<String, Party.PrimaryModuleState> presetSave = new HashMap<>();
   private final Party.ModuleState windowConvert = new Party.ModuleState();
   private final StringBuilder presetWrite = new StringBuilder(32);
   private long colorMeasure;
   private long animationSchedule;

   public Party() {
      this.handle(this.source, this.target);
   }

   public String refresh() {
      String var1 = PartyCommand.resolve();
      return var1 != null ? var1 : "";
   }

   @Override
   public void handle() {
      super.handle();
      if (this.enabled) {
         this.tick();
      }
   }

   @Override
   public void process() {
      super.process();
      JsonRpcHandler.handle();
      this.frameCheck.clear();
      this.providerClose.clear();
   }

   private void tick() {
      String var1 = Module.client.getSession() != null ? Module.client.getSession().getUsername() : "Unknown";

      try {
         JsonRpcHandler var2 = new JsonRpcHandler("ws://49.12.210.82:8080/ws", var1);
         var2.connect();
      } catch (Exception var3) {
         ChatLogger.handle("§c[Party] Ошибка подключения: §f" + var3.getMessage());
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (!ServerEnvironment.handle() && this.source.compute() > -1) {
         if (var1.resolve() == this.source.compute()) {
            if (var1.apply() == 1) {
               if (Module.client.currentScreen == null) {
                  this.drawAnimation();
               }
            }
         }
      }
   }

   private void drawAnimation() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.animationSchedule >= 250L) {
         JsonRpcHandler var3 = JsonRpcHandler.instance;
         if (var3 == null || !var3.isOpen()) {
            ChatLogger.handle("§c[Party] Нет соединения с сервером меток.");
         } else if (!render()) {
            ChatLogger.handle("§c[Party] Ты не в группе. Создай: §f.party create");
         } else if (Module.client.world != null && Module.client.player != null) {
            this.animationSchedule = var1;
            float var4 = Module.client.getRenderTickCounter().getTickProgress(true);
            Vec3d var5 = Module.client.player.getCameraPosVec(var4);
            Vec3d var6 = CombatRaycast.handle(Module.client.player.getPitch(), Module.client.player.getYaw());
            Vec3d var7 = var5.add(var6.multiply(300.0));
            RaycastContext var8 = new RaycastContext(var5, var7, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player);
            BlockHitResult var9 = Module.client.world.raycast(var8);
            boolean var10 = var9 != null && var9.getType() == Type.BLOCK;
            double var11 = var10 ? var5.squaredDistanceTo(var9.getPos()) : Double.MAX_VALUE;
            Box var13 = Module.client.player.getBoundingBox().stretch(var6.multiply(300.0)).expand(1.0);
            EntityHitResult var14 = CombatRaycast.handle(
               Module.client.player, var5, var7, var13, var0 -> !var0.isSpectator() && var0.isAlive() && var0 != Module.client.player, 300.0
            );
            boolean var16 = false;
            String var17 = "";
            Vec3d var15;
            if (var14 == null || var14.getEntity() == null || var10 && (var14.getPos() == null || !(var14.getPos().squaredDistanceTo(var5) < var11))) {
               if (var10) {
                  var15 = var9.getPos();
               } else {
                  var15 = var7;
               }
            } else {
               Entity var18 = var14.getEntity();
               var17 = var18.getUuidAsString() + handle(var18);
               var16 = true;
               var15 = var18.getPos().add(0.0, var18.getHeight(), 0.0);
            }

            if (this.handle(var15, var16, var17)) {
               var3.process();
               JsonRpcHandler.data.remove(this.encodePoint().toLowerCase(Locale.ROOT));
               ChatLogger.handle("§e[Party] Метка снята.");
            } else {
               var3.handle(var15.x, var15.y, var15.z, var16, var17);
               ChatLogger.handle(
                  "§a[Party] Метка установлена в §f" + MathHelper.floor(var15.x) + "§7/§f" + MathHelper.floor(var15.y) + "§7/§f" + MathHelper.floor(var15.z)
               );
            }
         }
      }
   }

   private boolean handle(Vec3d var1, boolean var2, String var3) {
      JsonRpcHandler.CacheEntry var4 = JsonRpcHandler.data.get(this.encodePoint().toLowerCase(Locale.ROOT));
      if (var4 == null) {
         return false;
      }

      if (var2 && var4.output) {
         return process(var3).equals(process(var4.data == null ? "" : var4.data));
      }

      if (var2 != var4.output) {
         return false;
      }

      double var5 = var1.x - var4.context;
      double var7 = var1.y - var4.config;
      double var9 = var1.z - var4.state;
      return var5 * var5 + var7 * var7 + var9 * var9 <= 9.0;
   }

   private String encodePoint() {
      return Module.client.getSession() != null ? Module.client.getSession().getUsername() : "";
   }

   private static String handle(Entity var0) {
      try {
         String var1 = var0.getName().getString();
         return var1 == null ? "" : var1;
      } catch (Exception var2) {
         return "";
      }
   }

   public static boolean render() {
      String var0 = PartyCommand.resolve();
      return var0 != null && !var0.isBlank();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      JsonRpcHandler var2 = JsonRpcHandler.instance;
      if (var2 != null && var2.isOpen()) {
         var2.handle(this.refresh());
         if (System.currentTimeMillis() - this.colorMeasure > 25000L) {
            this.colorMeasure = System.currentTimeMillis();
            var2.compute();
         }
      }
   }

   @EventHandler(handle = 3)
   public void handle(HudRenderContext var1) {
      if (this.target.compute()) {
         if (!ServerEnvironment.handle()) {
            if (JsonRpcHandler.instance != null && !JsonRpcHandler.data.isEmpty()) {
               RoundedRectRenderer var2 = var1.resolve();
               long var3 = System.currentTimeMillis();
               boolean var5 = false;
               this.animate();

               for (JsonRpcHandler.CacheEntry var7 : JsonRpcHandler.data.values()) {
                  long var8 = var3 - var7.cache;
                  if (var8 <= 600000L) {
                     float var10 = var8 > 420000L ? 1.0F - (float)(var8 - 420000L) / 180000.0F : 1.0F;
                     var10 = MathHelper.clamp(var10, 0.0F, 1.0F);
                     if (!(var10 <= 0.004F)) {
                        Party.PrimaryModuleState var11 = this.handle(var7);
                        float var12 = var11.handle() * var10;
                        if (!(var12 <= 0.004F)) {
                           Entity var13 = this.process(var7);
                           Vec3d var14 = this.handle(var7, var13);
                           if (!this.positionAdvance.handle(var14, var13 == null)) {
                              WaypointArrowRenderer.handle(
                                 var2, var14, var7.instance, this.handle(this.positionAdvance.context), var12, var1.apply(), var1.execute()
                              );
                           } else if (!(this.positionAdvance.context <= 1.5)) {
                              if (!var5) {
                                 PartyWaypointRenderer.handle(var2);
                                 var5 = true;
                              }

                              String var15 = this.handle(var7.instance, var14);
                              String var16 = this.handle(this.positionAdvance.context);
                              float var17 = var13 == null ? this.positionAdvance.data : this.handle(var7.instance, var15, var16);
                              this.serverRead
                                 .handle(var2, this.positionAdvance.instance, var17, var7.instance, var15, var16, var7.instance, var12, var11.process());
                           }
                        }
                     }
                  }
               }

               this.presetSave.keySet().retainAll(JsonRpcHandler.data.keySet());
            }
         }
      }
   }

   private Party.PrimaryModuleState handle(JsonRpcHandler.CacheEntry var1) {
      Party.PrimaryModuleState var2 = this.presetSave.get(var1.instance.toLowerCase(Locale.ROOT));
      if (var2 == null || var2.instance != var1.cache) {
         var2 = new Party.PrimaryModuleState(var1.cache);
         this.presetSave.put(var1.instance.toLowerCase(Locale.ROOT), var2);
      }

      return var2;
   }

   private float handle(String var1, String var2, String var3) {
      return this.positionAdvance.data - this.serverRead.handle(var1, var2, var3) * 0.5F - 6.0F * this.load();
   }

   private void animate() {
      this.frameCheck.clear();
      this.moduleCollect.clear();
      if (Module.client.world != null) {
         for (JsonRpcHandler.CacheEntry var2 : JsonRpcHandler.data.values()) {
            if (var2.output && var2.data != null && !var2.data.isEmpty()) {
               this.moduleCollect.add(process(var2.data));
            }
         }

         if (!this.moduleCollect.isEmpty()) {
            for (Entity var6 : Module.client.world.getEntities()) {
               if (var6.isAlive()) {
                  String var3 = var6.getUuidAsString().toLowerCase(Locale.ROOT);
                  if (this.moduleCollect.contains(var3)) {
                     this.frameCheck.putIfAbsent(var3, var6);
                  }

                  String var4 = handle(var6).toLowerCase(Locale.ROOT);
                  if (!var4.isEmpty() && this.moduleCollect.contains(var4)) {
                     this.frameCheck.putIfAbsent(var4, var6);
                  }
               }
            }
         }
      }
   }

   private static String process(String var0) {
      int var1 = var0.indexOf(1);
      String var2 = var1 >= 0 ? var0.substring(0, var1) : var0;
      return var2.toLowerCase(Locale.ROOT);
   }

   static String handle(String var0) {
      if (var0 == null) {
         return "";
      }

      int var1 = var0.indexOf(1);
      return var1 >= 0 ? var0.substring(var1 + 1) : var0;
   }

   private Entity process(JsonRpcHandler.CacheEntry var1) {
      return var1.output && var1.data != null && !var1.data.isEmpty() ? this.frameCheck.get(process(var1.data)) : null;
   }

   private Vec3d handle(JsonRpcHandler.CacheEntry var1, Entity var2) {
      if (var2 == null) {
         return new Vec3d(var1.context, var1.config, var1.state);
      }

      float var3 = Module.client.getRenderTickCounter().getTickProgress(true);
      Vec3d var4 = var2.getLerpedPos(var3);
      return new Vec3d(var4.x, var4.y + var2.getHeight() + this.handle(var4), var4.z);
   }

   private double handle(Vec3d var1) {
      if (Module.client.gameRenderer != null && Module.client.gameRenderer.getCamera() != null) {
         double var2 = Module.client.gameRenderer.getCamera().getPos().distanceTo(var1);
         double var4 = MathHelper.clamp(var2 / 8.0, 0.0, 1.0);
         return 0.45 + 0.9000000000000001 * var4;
      } else {
         return 1.35;
      }
   }

   private String handle(String var1, Vec3d var2) {
      int var3 = MathHelper.floor(var2.x);
      int var4 = MathHelper.floor(var2.y);
      int var5 = MathHelper.floor(var2.z);
      Party.ModuleState var6 = this.providerClose.computeIfAbsent(var1, var0 -> new Party.ModuleState());
      if (var6.instance == null || var6.data != var3 || var6.context != var4 || var6.config != var5) {
         var6.data = var3;
         var6.context = var4;
         var6.config = var5;
         this.presetWrite.setLength(0);
         this.presetWrite.append(var3).append(", ").append(var4).append(", ").append(var5);
         var6.instance = this.presetWrite.toString();
      }

      return var6.instance;
   }

   private String handle(double var1) {
      int var3 = (int)Math.round(var1);
      if (this.windowConvert.instance == null || this.windowConvert.data != var3) {
         this.windowConvert.data = var3;
         this.presetWrite.setLength(0);
         this.presetWrite.append(var3).append(" м");
         this.windowConvert.instance = this.presetWrite.toString();
      }

      return this.windowConvert.instance;
   }

   private float load() {
      if (Module.client.getWindow() == null) {
         return 2.0F;
      }

      float var1 = Module.client.getWindow().getScaleFactor();
      return var1 <= 0.0F ? 2.0F : var1;
   }

   static final class ModuleState {
      String instance;
      int data = Integer.MIN_VALUE;
      int context = Integer.MIN_VALUE;
      int config = Integer.MIN_VALUE;
   }

   static final class PrimaryModuleState {
      final long instance;
      private final EasedDoubleAnimator data = new EasedDoubleAnimator();
      private final EasedDoubleAnimator context = new EasedDoubleAnimator();

      PrimaryModuleState(long var1) {
         this.instance = var1;
         this.data.handle(1.0, 0.42, EasingFunctions.serverRead);
         this.context.handle(1.0, 0.7, EasingFunctions.selection);
      }

      float handle() {
         this.context.handle();
         this.data.handle();
         return MathHelper.clamp(this.data.update(), 0.0F, 1.0F);
      }

      float process() {
         return MathHelper.clamp(this.context.update(), 0.0F, 1.0F);
      }
   }
}
