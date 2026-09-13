package ru.wild.modules.movement;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.ModeSetting;
import ru.wild.util.math.ResettableTimer;

@ModuleRegister(
   name = "NoFall",
   category = ModuleCategory.Movement,
   description = "Предотвращает урон от падения",
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class NoFall extends Module {
   private final ModeSetting source = new ModeSetting("Режим", "Grim v72", "Grim v72", "Grim v73");
   private final ResettableTimer target = new ResettableTimer();
   private final ResettableTimer pending = new ResettableTimer();
   private boolean previous;
   private boolean latest;
   private boolean summary;
   private int matrixBlend;
   private int vectorMatch;

   public NoFall() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         this.previous = Module.client.player.fallDistance > 3.0;
         if (this.source.process("Grim v72")) {
            if (this.matrixBlend > 0) {
               this.matrixBlend--;
            }

            if (this.previous
               && !Module.client.player.isOnGround()
               && this.refresh()
               && Module.client.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)
               && this.pending.handle(200.0)) {
               this.render();
               this.render();
               this.pending.handle();
               this.latest = true;
               this.target.handle();
            }

            if (this.target.handle(300.0)) {
               this.latest = false;
               this.matrixBlend = 0;
            }
         } else {
            if (this.vectorMatch == 2) {
               Timer.source = 0.5F;
            } else if (this.vectorMatch <= 1) {
               Timer.source = 1.0F;
            }

            if (this.vectorMatch > 0) {
               this.vectorMatch--;
            }

            if (this.target.handle(300.0)) {
               this.latest = false;
               this.vectorMatch = 0;
            }
         }
      } else {
         this.tick();
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (!var1.compute()) {
         if (!(var1.resolve() instanceof GameJoinS2CPacket)
            && !(var1.resolve() instanceof DisconnectS2CPacket)
            && !(var1.resolve() instanceof PlayerRespawnS2CPacket)) {
            if (var1.resolve() instanceof PlayerPositionLookS2CPacket && this.latest) {
               if (this.source.process("Grim v72")) {
                  this.matrixBlend = 2;
               } else {
                  this.vectorMatch = 2;
               }

               this.latest = false;
            }
         } else {
            this.tick();
         }
      } else if (Module.client.player != null && !this.summary) {
         if (!this.source.process("Grim v72") || !Module.client.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            if (var1.resolve() instanceof PlayerMoveC2SPacket && this.previous && this.refresh() && this.vectorMatch == 0 && this.pending.handle(100.0)) {
               this.summary = true;
               Module.client.getNetworkHandler().sendPacket(new OnGroundOnly(true, false));
               this.summary = false;
               var1.process();
               this.pending.handle();
               this.target.handle();
               this.latest = true;
            }
         }
      }
   }

   @EventHandler
   public void handle(MovementInputEvent var1) {
      if (this.latest || this.matrixBlend == 2 || this.vectorMatch == 2) {
         var1.handle(false);
      } else if (this.matrixBlend == 1 || this.vectorMatch == 1) {
         var1.handle(true);
      }
   }

   private boolean refresh() {
      double var1 = Math.min(-0.05, Module.client.player.getVelocity().y);
      return Module.client.world.getBlockCollisions(Module.client.player, Module.client.player.getBoundingBox().offset(0.0, var1, 0.0)).iterator().hasNext();
   }

   private void render() {
      Module.client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(Module.client.player, Mode.START_FALL_FLYING));
   }

   private void tick() {
      this.previous = false;
      this.latest = false;
      this.summary = false;
      this.matrixBlend = 0;
      this.vectorMatch = 0;
      Timer.source = 1.0F;
   }

   @Override
   public void process() {
      this.tick();
      super.process();
   }
}
