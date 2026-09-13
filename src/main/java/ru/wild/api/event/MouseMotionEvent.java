package ru.wild.api.event;

public class MouseMotionEvent extends Event {
   public double instance;
   public double data;

   public MouseMotionEvent(double var1, double var3) {
      this.instance = var1;
      this.data = var3;
   }

   public double compute() {
      return this.instance;
   }

   public void handle(double var1) {
      this.instance = var1;
   }

   public double resolve() {
      return this.data;
   }

   public void process(double var1) {
      this.data = var1;
   }
}
