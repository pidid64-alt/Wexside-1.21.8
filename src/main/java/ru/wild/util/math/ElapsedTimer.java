package ru.wild.util.math;

public class ElapsedTimer {
   private long instance = -1L;

   public ElapsedTimer() {
      this.instance = System.currentTimeMillis();
   }

   public boolean handle(double var1) {
      return System.currentTimeMillis() - this.instance >= var1;
   }

   public boolean handle(boolean var1, double var2) {
      return var1 || this.handle(var2);
   }

   public long handle() {
      return this.instance;
   }

   public void process() {
      this.instance = System.currentTimeMillis();
   }

   public long compute() {
      return System.currentTimeMillis() - this.instance;
   }

   public long resolve() {
      return System.nanoTime() / 1000000L;
   }

   public void handle(long var1) {
      this.instance = var1;
   }
}
