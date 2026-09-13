package ru.wild.api.event;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRenderEvent extends Event {
   private final MatrixStack instance;
   private final float data;

   public WorldRenderEvent(MatrixStack var1, float var2) {
      this.instance = var1;
      this.data = var2;
   }
   public MatrixStack compute() {
      return this.instance;
   }
   public float resolve() {
      return this.data;
   }
}
