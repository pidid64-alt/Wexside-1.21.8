package ru.wild.api.event;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class FirstPersonHandRenderEvent extends Event {
   private final MatrixStack instance;
   private final Hand data;
   private final float context;
   private final float config;

   public FirstPersonHandRenderEvent(MatrixStack var1, Hand var2, float var3, float var4) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
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

   public float apply() {
      return this.context;
   }

   public float execute() {
      return this.config;
   }
}
