package ru.wild.api.event;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;

public final class ClientUpdateEvent extends Event {
   private final MinecraftClient instance;

   public ClientUpdateEvent(MinecraftClient var1) {
      this.instance = Objects.requireNonNull(var1, "client");
   }

   public MinecraftClient compute() {
      return this.instance;
   }
}
