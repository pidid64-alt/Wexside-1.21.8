package ru.wild.api.event;
public class SlowdownMultiplierEvent extends Event {
   private float instance;
   private float data;

   public SlowdownMultiplierEvent(float var1, float var2) {
      this.instance = var1;
      this.data = var2;
   }

   public void compute() {
   }
   public float resolve() {
      return this.instance;
   }
   public float update() {
      return this.data;
   }
   public void handle(float var1) {
      this.instance = var1;
   }
   public void process(float var1) {
      this.data = var1;
   }
}
