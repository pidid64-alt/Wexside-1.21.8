package ru.wild.modules.movement;

import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.player.MovementPhysics;

@ModuleRegister(name = "Timer", description = "Ускорение игры", category = ModuleCategory.Movement)
public class Timer extends Module {
   public static float source = 1.0F;
   private final ModeSetting target = new ModeSetting("Режим", "Умный", "Умный", "Бёрст", "Грим");
   private final NumberSetting pending = new NumberSetting("Скорость", 2.0F, 0.0F, 10.0F, 0.01F, false).handle(() -> !this.target.process("Умный"));
   private final BooleanSetting previous = new BooleanSetting("Умный сброс", true).handle(() -> !this.target.process("Умный"));
   private final NumberSetting latest = new NumberSetting("Скорость убывания", 3.8F, 0.15F, 5.0F, 0.1F, false)
      .handle(() -> !this.target.process("Умный") || !this.previous.compute());
   private final NumberSetting summary = new NumberSetting("Окно дрифта", 110.0F, 40.0F, 120.0F, 1.0F, false).handle(() -> !this.target.process("Бёрст"));
   private final NumberSetting matrixBlend = new NumberSetting("Скорость бёрста", 3.0F, 1.5F, 6.0F, 0.1F, false).handle(() -> !this.target.process("Бёрст"));
   private final NumberSetting vectorMatch = new NumberSetting("Скорость зарядки", 0.6F, 0.1F, 0.95F, 0.05F, false).handle(() -> !this.target.process("Бёрст"));
   private final NumberSetting itemProject = new NumberSetting("Запас до флага", 15.0F, 0.0F, 40.0F, 1.0F, false).handle(() -> !this.target.process("Бёрст"));
   private final NumberSetting responseCompute = new NumberSetting("Скорость грима", 2.0F, 1.0F, 6.0F, 0.1F, false).handle(() -> !this.target.process("Грим"));
   private final KeybindSetting providerFetch = new KeybindSetting("Кнопка буста", -1).handle(() -> !this.target.process("Грим"));
   private final BooleanSetting profileDraw = new BooleanSetting("Ускорять в воздухе", false).handle(() -> this.target.process("Грим"));
   private final float vectorPerform = 100.0F;
   private float eventAttach = 0.0F;
   private boolean serverRead = false;
   private double positionAdvance = 0.0;
   private long frameCheck = 0L;
   private boolean moduleCollect = false;
   private boolean providerClose = false;
   private float presetSave = 0.0F;
   private long windowConvert = 0L;
   private long presetWrite = 0L;

   public Timer() {
      this.handle(
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw
      );
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (this.target.process("Грим")) {
         this.render();
      } else if (!this.profileDraw.compute() || Module.client.player != null && !Module.client.player.isOnGround()) {
         if (this.target.process("Бёрст")) {
            this.refresh();
         } else if (!this.previous.compute()) {
            source = this.pending.compute();
         } else {
            if (this.serverRead) {
               this.eventAttach = this.eventAttach - this.latest.compute();
               source = 1.0F;
               if (this.eventAttach <= 0.0F) {
                  this.eventAttach = 0.0F;
                  this.serverRead = false;
               }
            } else {
               source = this.pending.compute();
               this.eventAttach = this.eventAttach + this.latest.compute();
               if (this.eventAttach >= 100.0F) {
                  this.eventAttach = 100.0F;
                  this.serverRead = true;
               }
            }
         }
      } else {
         source = 1.0F;
         this.tick();
      }
   }

   private void refresh() {
      long var1 = System.nanoTime();
      boolean var3 = Module.client.player != null
         && (
            Math.abs(Module.client.player.getX() - Module.client.player.lastX) > 0.001
               || Math.abs(Module.client.player.getZ() - Module.client.player.lastZ) > 0.001
               || !Module.client.player.isOnGround()
         );
      if (this.frameCheck == 0L) {
         this.frameCheck = var1;
         this.positionAdvance = -this.summary.compute();
         this.moduleCollect = false;
         this.providerClose = var3;
         source = this.matrixBlend.compute();
      } else {
         double var4 = (var1 - this.frameCheck) / 1000000.0;
         this.frameCheck = var1;
         if (var4 > 300.0 || var4 < 0.0) {
            var4 = 50.0;
         }

         if (var3 && !this.providerClose) {
            this.positionAdvance = -this.summary.compute();
            this.moduleCollect = false;
         }

         this.providerClose = var3;
         this.positionAdvance += 50.0 - var4;
         if (this.positionAdvance < -this.summary.compute()) {
            this.positionAdvance = -this.summary.compute();
         }

         if (this.moduleCollect) {
            source = this.vectorMatch.compute();
            if (this.positionAdvance <= -this.summary.compute() + 2.0) {
               this.moduleCollect = false;
            }
         } else {
            double var6 = Math.max(0.0, 50.0 - var4);
            if (this.positionAdvance + var6 >= -this.itemProject.compute()) {
               this.moduleCollect = true;
               source = this.vectorMatch.compute();
            } else {
               source = this.matrixBlend.compute();
            }
         }
      }
   }

   private void render() {
      long var1 = System.currentTimeMillis();
      int var3 = this.providerFetch.compute();
      boolean var4 = var3 == -1 || KeybindSetting.process(var3);
      if (!(this.presetSave <= 0.0F) && var4 && var1 - this.presetWrite >= 2000L) {
         source = Math.max(this.responseCompute.compute(), 1.0F);
         this.presetSave = MathHelper.clamp(this.presetSave - (0.0025F * this.responseCompute.compute() - 0.0025F), 0.0F, 1.0F);
      } else {
         source = 1.0F;
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (!var1.compute() && Module.client.player != null) {
         if (this.target.process("Грим")) {
            this.process(var1);
         } else {
            if (this.target.process("Бёрст")) {
               boolean var2 = var1.resolve() instanceof PlayerPositionLookS2CPacket;
               boolean var3 = var1.resolve() instanceof EntityVelocityUpdateS2CPacket var4 && var4.getEntityId() == Module.client.player.getId();
               if (var2 || var3) {
                  this.positionAdvance = -this.summary.compute();
                  this.moduleCollect = true;
                  this.frameCheck = 0L;
               }
            }
         }
      }
   }

   private void process(PacketEvent var1) {
      long var2 = System.currentTimeMillis();
      if (var1.resolve() instanceof PlayerPositionLookS2CPacket) {
         this.presetWrite = var2;
         source = 1.0F;
         this.presetSave = 0.0F;
      } else if (var1.resolve() instanceof EntityVelocityUpdateS2CPacket var4 && var4.getEntityId() == Module.client.player.getId()) {
         source = 1.0F;
         this.presetSave = 0.0F;
      } else {
         if (var1.resolve() instanceof CommonPingS2CPacket && var2 - this.presetWrite > 2000L) {
            if (var2 - this.windowConvert > 25000L) {
               this.windowConvert = var2;
               this.presetSave = 0.0F;
               return;
            }

            if (!MovementPhysics.handle()) {
               this.presetSave = MathHelper.clamp(this.presetSave + 0.005F, 0.0F, 1.0F);
            }

            var1.process();
         }
      }
   }

   private void tick() {
      this.eventAttach = 0.0F;
      this.serverRead = false;
      this.positionAdvance = 0.0;
      this.frameCheck = 0L;
      this.moduleCollect = false;
      this.providerClose = false;
      this.presetSave = 0.0F;
      this.windowConvert = System.currentTimeMillis();
      this.presetWrite = 0L;
   }

   @Override
   public void handle() {
      this.tick();
      super.handle();
   }

   @Override
   public void process() {
      source = 1.0F;
      this.tick();
      super.process();
   }
}
