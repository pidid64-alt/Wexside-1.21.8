package ru.wild.api.event;

import net.minecraft.client.MinecraftClient;

public final class MouseClickContextEvent extends Event {
   private final MinecraftClient instance;
   private final long data;

   public MouseClickContextEvent(MinecraftClient var1, long var2) {
      this.instance = var1;
      this.data = var2;
   }

   public MinecraftClient compute() {
      return this.instance;
   }

   public long resolve() {
      return this.data;
   }
}
