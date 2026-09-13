package ru.wild.api.event;
public class ItemUseEvent extends Event {
   byte instance;
   public byte compute() {
      return this.instance;
   }
   public void handle(byte var1) {
      this.instance = var1;
   }
   public ItemUseEvent(byte var1) {
      this.instance = var1;
   }
}
