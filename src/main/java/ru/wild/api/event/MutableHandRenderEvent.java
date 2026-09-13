package ru.wild.api.event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class MutableHandRenderEvent extends Event {
   private MatrixStack instance;
   private Hand data;
   private float context;
   public MutableHandRenderEvent(MatrixStack var1, Hand var2, float var3) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
   }
   public MatrixStack compute() {
      return this.instance;
   }
   public Hand resolve() {
      return this.data;
   }
   public float update() {
      return this.context;
   }
   public void handle(MatrixStack var1) {
      this.instance = var1;
   }
   public void handle(Hand var1) {
      this.data = var1;
   }
   public void handle(float var1) {
      this.context = var1;
   }
}
