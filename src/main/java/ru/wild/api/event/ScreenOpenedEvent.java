package ru.wild.api.event;

import net.minecraft.client.gui.screen.Screen;

public class ScreenOpenedEvent extends Event {
   private final Screen instance;
   private boolean data;

   public ScreenOpenedEvent(Screen var1) {
      this.instance = var1;
   }

   public Screen compute() {
      return this.instance;
   }

   public void resolve() {
      this.data = true;
   }

   public boolean update() {
      return this.data;
   }
}
