package ru.wild.api.event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {
   private Packet<?> instance;
   private PacketEvent.Mode data;

   public boolean compute() {
      return this.data.equals(PacketEvent.Mode.SEND);
   }
   public Packet<?> resolve() {
      return this.instance;
   }
   public PacketEvent.Mode update() {
      return this.data;
   }
   public void handle(Packet<?> var1) {
      this.instance = var1;
   }
   public void handle(PacketEvent.Mode var1) {
      this.data = var1;
   }
   public PacketEvent(Packet<?> var1, PacketEvent.Mode var2) {
      this.instance = var1;
      this.data = var2;
   }

   public enum Mode {
      SEND,
      RECEIVE;
   }
}
