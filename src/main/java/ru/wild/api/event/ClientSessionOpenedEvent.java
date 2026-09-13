package ru.wild.api.event;

import java.time.Instant;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;

public final class ClientSessionOpenedEvent extends Event {
   private final MinecraftClient instance;
   private final Instant data;

   public ClientSessionOpenedEvent(MinecraftClient var1) {
      this(var1, Instant.now());
   }

   public ClientSessionOpenedEvent(MinecraftClient var1, Instant var2) {
      this.instance = var1;
      this.data = Objects.requireNonNull(var2, "timestamp");
   }

   public MinecraftClient compute() {
      return this.instance;
   }

   public Instant resolve() {
      return this.data;
   }
}
