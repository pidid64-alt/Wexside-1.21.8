package ru.wild.api.event;
import net.minecraft.client.gui.screen.Screen;

public class ScreenInputEvent extends Event {
   private Screen instance;
   private int data;
   public Screen compute() {
      return this.instance;
   }
   public int resolve() {
      return this.data;
   }
   public ScreenInputEvent(Screen var1, int var2) {
      this.instance = var1;
      this.data = var2;
   }
}
