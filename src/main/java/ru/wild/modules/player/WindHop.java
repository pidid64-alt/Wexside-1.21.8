package ru.wild.modules.player;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldReadyEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;

@ModuleRegister(name = "WindHop", description = "Подхватывает импульс заряда ветра прыжком", category = ModuleCategory.Player)
public class WindHop extends Module {
   private static final int source = 2;
   private final BooleanSetting target = new BooleanSetting("Подхват импульса", true);
   private int pending = -1;
   private boolean previous;

   public WindHop() {
      this.handle(this.target);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.compute() && this.target.compute() && Module.client.player != null) {
         if (var1.resolve() instanceof PlayerInteractItemC2SPacket var2 && Module.client.player.getStackInHand(var2.getHand()).isOf(Items.WIND_CHARGE)) {
            this.pending = 2;
            this.previous = false;
         }
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (!this.target.compute()) {
         this.refresh();
      } else if (this.pending > 0) {
         this.pending--;
      } else {
         if (this.pending == 0) {
            this.pending = -1;
            this.previous = true;
         }
      }
   }

   @EventHandler
   public void handle(MovementInputEvent var1) {
      if (this.previous && this.target.compute()) {
         var1.handle(true);
         this.refresh();
      }
   }

   @EventHandler
   public void handle(WorldReadyEvent var1) {
      this.refresh();
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.refresh();
   }

   @Override
   public void process() {
      this.refresh();
      super.process();
   }

   private void refresh() {
      this.pending = -1;
      this.previous = false;
   }
}
