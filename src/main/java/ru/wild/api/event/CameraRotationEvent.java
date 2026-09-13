package ru.wild.api.event;

public class CameraRotationEvent extends Event {
   public float instance;
   public float data;
   public float context;

   public CameraRotationEvent(float var1, float var2, float var3) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
   }

   public float compute() {
      return this.instance;
   }

   public void handle(float var1) {
      this.instance = var1;
   }

   public float resolve() {
      return this.data;
   }

   public void process(float var1) {
      this.data = var1;
   }

   public float update() {
      return this.context;
   }

   public void compute(float var1) {
      this.context = var1;
   }
}
