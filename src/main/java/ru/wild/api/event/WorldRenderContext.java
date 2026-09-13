package ru.wild.api.event;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class WorldRenderContext extends Event {
   private final MinecraftClient instance;
   private final GameRenderer data;
   private final ru.wild.render.WorldRenderContext context;
   private final float config;

   public WorldRenderContext(MinecraftClient var1, GameRenderer var2, ru.wild.render.WorldRenderContext var3, float var4) {
      this.instance = Objects.requireNonNull(var1, "client");
      this.data = Objects.requireNonNull(var2, "gameRenderer");
      this.context = Objects.requireNonNull(var3, "worldRenderer");
      this.config = var4;
   }

   public MinecraftClient compute() {
      return this.instance;
   }

   public GameRenderer resolve() {
      return this.data;
   }

   public ru.wild.render.WorldRenderContext update() {
      return this.context;
   }

   public MatrixStack apply() {
      return this.context.process();
   }

   public Matrix4f execute() {
      return this.context.compute();
   }

   public Matrix4f prepare() {
      return this.context.update();
   }

   public float check() {
      return this.config;
   }
}
