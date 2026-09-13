package ru.wild.api.event;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class HeldItemRenderEvent extends Event {
   private final MatrixStack instance;
   private final Hand data;

   public HeldItemRenderEvent(MatrixStack var1, Hand var2) {
      this.instance = var1;
      this.data = var2;
   }

   public MatrixStack compute() {
      return this.instance;
   }

   public Hand resolve() {
      return this.data;
   }

   public boolean update() {
      return this.data == Hand.MAIN_HAND;
   }
}
