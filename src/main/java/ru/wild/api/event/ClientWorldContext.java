package ru.wild.api.event;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public final class ClientWorldContext extends Event {
   private final MinecraftClient instance;
   private final ClientPlayerEntity data;
   private final ClientWorld context;

   public ClientWorldContext(MinecraftClient var1, ClientPlayerEntity var2, ClientWorld var3) {
      this.instance = Objects.requireNonNull(var1, "client");
      this.data = Objects.requireNonNull(var2, "player");
      this.context = Objects.requireNonNull(var3, "world");
   }

   public MinecraftClient compute() {
      return this.instance;
   }

   public ClientPlayerEntity resolve() {
      return this.data;
   }

   public ClientWorld update() {
      return this.context;
   }
}
