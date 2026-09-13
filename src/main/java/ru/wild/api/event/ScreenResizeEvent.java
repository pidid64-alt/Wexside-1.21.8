package ru.wild.api.event;

public class ScreenResizeEvent extends Event {
   private final int instance;
   private final int data;

   public ScreenResizeEvent(int var1, int var2) {
      this.instance = var1;
      this.data = var2;
   }

   public int compute() {
      return this.instance;
   }

   public int resolve() {
      return this.data;
   }
}
