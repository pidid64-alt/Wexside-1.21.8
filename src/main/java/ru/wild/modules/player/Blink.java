package ru.wild.modules.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldReadyEvent;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(
   name = "Blink",
   description = "Замедляет пакеты имитируя пинг",
   category = ModuleCategory.Player,
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class Blink extends Module {
   private static final int source = -1258291201;
   private final List<Packet<?>> target = new ArrayList<>();
   private final BooleanSetting pending = new BooleanSetting("Пульсировать", false);
   private final NumberSetting previous = new NumberSetting("Задержка", 12.0F, 1.0F, 40.0F, 1.0F, false).handle(() -> !this.pending.compute());
   private final BooleanSetting latest = new BooleanSetting("Сброс при ударе", false);
   private final BooleanSetting summary = new BooleanSetting("Отображать модель", true);
   private final BooleanSetting matrixBlend = new BooleanSetting("Убирать от первого лица", true).handle(() -> !this.summary.compute());
   private Vec3d vectorMatch;
   private boolean itemProject;
   private boolean responseCompute;
   private boolean providerFetch;
   private long profileDraw;

   public Blink() {
      this.handle(this.pending, this.previous, this.latest, this.summary, this.matrixBlend);
   }

   @Override
   public void handle() {
      if (!this.render()) {
         this.setEnabled(false);
      } else {
         this.target.clear();
         this.vectorMatch = Module.client.player.getPos();
         this.itemProject = false;
         this.providerFetch = false;
         this.profileDraw = System.currentTimeMillis();
         super.handle();
      }
   }

   @Override
   public void process() {
      if (!this.responseCompute) {
         this.refresh();
      }

      this.target.clear();
      this.vectorMatch = null;
      this.itemProject = false;
      this.providerFetch = false;
      this.responseCompute = false;
      super.process();
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.compute() && !this.itemProject && this.render()) {
         if (this.providerFetch) {
            this.providerFetch = false;
            if (var1.resolve() instanceof PlayerInteractEntityC2SPacket) {
               return;
            }
         }

         this.target.add(var1.resolve());
         var1.process();
      }
   }

   @EventHandler
   public void handle(EntityAttackEvent var1) {
      if (this.latest.compute()
         && this.render()
         && var1.compute() instanceof PlayerEntity var2
         && var2 != Module.client.player
         && !(var2 instanceof ClientPlayerEntity)) {
         this.refresh();
         this.target.clear();
         this.vectorMatch = Module.client.player.getPos();
         this.providerFetch = true;
         this.profileDraw = System.currentTimeMillis();
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (this.pending.compute() && !this.target.isEmpty()) {
         if (System.currentTimeMillis() - this.profileDraw >= this.tick()) {
            this.refresh();
            this.target.clear();
            this.vectorMatch = Module.client.player != null ? Module.client.player.getPos() : null;
            this.profileDraw = System.currentTimeMillis();
         }
      }
   }

   @EventHandler
   public void handle(WorldRenderContext var1) {
      if (this.summary.compute() && this.vectorMatch != null && Module.client.player != null && Module.client.world != null) {
         if (Module.client.options.getPerspective() != Perspective.FIRST_PERSON || !this.matrixBlend.compute()) {
            Box var2 = Module.client.player.getBoundingBox().offset(this.vectorMatch.subtract(Module.client.player.getPos()));
            this.handle(var1, var2, -1258291201);
         }
      }
   }

   @EventHandler
   public void handle(WorldReadyEvent var1) {
      this.responseCompute = true;
      this.setEnabled(false);
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.responseCompute = true;
      this.setEnabled(false);
   }

   private void refresh() {
      if (!this.target.isEmpty() && Module.client.getNetworkHandler() != null) {
         this.itemProject = true;

         try {
            for (Packet var2 : this.target) {
               Module.client.getNetworkHandler().sendPacket(var2);
            }
         } finally {
            this.itemProject = false;
         }
      }
   }

   private boolean render() {
      return Module.client.player != null && Module.client.world != null && Module.client.getNetworkHandler() != null;
   }

   private long tick() {
      return Math.round(this.previous.compute() * 50.0F);
   }

   private void handle(WorldRenderContext var1, Box var2, int var3) {
      Vec3d var4 = new Vec3d(var2.minX, var2.minY, var2.minZ);
      Vec3d var5 = new Vec3d(var2.maxX, var2.maxY, var2.maxZ);
      Vec3d var6 = new Vec3d(var4.x, var4.y, var4.z);
      Vec3d var7 = new Vec3d(var4.x, var4.y, var5.z);
      Vec3d var8 = new Vec3d(var4.x, var5.y, var4.z);
      Vec3d var9 = new Vec3d(var4.x, var5.y, var5.z);
      Vec3d var10 = new Vec3d(var5.x, var4.y, var4.z);
      Vec3d var11 = new Vec3d(var5.x, var4.y, var5.z);
      Vec3d var12 = new Vec3d(var5.x, var5.y, var4.z);
      Vec3d var13 = new Vec3d(var5.x, var5.y, var5.z);
      var1.update().handle(var6, var10, 1.0, var3, false);
      var1.update().handle(var10, var11, 1.0, var3, false);
      var1.update().handle(var11, var7, 1.0, var3, false);
      var1.update().handle(var7, var6, 1.0, var3, false);
      var1.update().handle(var8, var12, 1.0, var3, false);
      var1.update().handle(var12, var13, 1.0, var3, false);
      var1.update().handle(var13, var9, 1.0, var3, false);
      var1.update().handle(var9, var8, 1.0, var3, false);
      var1.update().handle(var6, var8, 1.0, var3, false);
      var1.update().handle(var10, var12, 1.0, var3, false);
      var1.update().handle(var11, var13, 1.0, var3, false);
      var1.update().handle(var7, var9, 1.0, var3, false);
   }
}
