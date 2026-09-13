package ru.wild.util.math;
public class ResettableTimer {
   private long instance;

   public ResettableTimer() {
      this.handle();
   }

   public boolean handle(double var1) {
      return System.currentTimeMillis() - var1 >= this.instance;
   }

   public boolean process(double var1) {
      boolean var3 = this.handle(var1);
      if (var3) {
         this.handle();
      }

      return var3;
   }

   public void handle() {
      this.instance = System.currentTimeMillis();
   }

   public long process() {
      return System.currentTimeMillis() - this.instance;
   }

   public void handle(long var1) {
      this.instance = System.currentTimeMillis() - var1;
   }
   public long compute() {
      return this.instance;
   }
}
