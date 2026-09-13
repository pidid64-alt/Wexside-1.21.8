package ru.wild.api.event;

import net.minecraft.client.MinecraftClient;

public final class MouseUpdateEvent extends Event {
   private final MinecraftClient instance;

   public MouseUpdateEvent(MinecraftClient var1) {
      this.instance = var1;
   }

   public MinecraftClient compute() {
      return this.instance;
   }
}
