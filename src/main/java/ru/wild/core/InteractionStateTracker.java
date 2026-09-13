package ru.wild.core;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.Hand;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;

public class InteractionStateTracker extends ClientComponent implements MinecraftContext {
   public static final InteractionStateTracker instance = new InteractionStateTracker();
   public boolean data;
   public boolean context = true;

   @EventHandler
   public void handle(PacketEvent var1) {
      switch (var1.resolve()) {
         case PlayerActionC2SPacket var4 when var4.getAction().equals(Action.RELEASE_USE_ITEM):
            this.context = true;
            break;
         case ClientStatusC2SPacket var5 when var5.getMode().equals(Mode.PERFORM_RESPAWN):
            this.context = true;
            break;
         case PlayerRespawnS2CPacket var6:
            this.context = true;
            break;
         case GameJoinS2CPacket var7:
            this.context = true;
            break;
         default:
      }
   }

   public void handle(Hand var1) {
      if (this.context) {
         toggleState.interactionManager.interactItem(toggleState.player, var1);
         this.context = false;
      }

      this.data = true;
   }
   public void handle(boolean var1) {
      this.data = var1;
   }
   public void process(boolean var1) {
      this.context = var1;
   }
   public boolean handle() {
      return this.data;
   }
   public boolean process() {
      return this.context;
   }
}
