package ru.wild.util.math;
public class ActionDelay {
   public long instance = System.currentTimeMillis();

   public ActionDelay() {
      this.handle();
   }

   public void handle() {
      this.instance = System.currentTimeMillis();
   }

   public boolean handle(long var1) {
      return System.currentTimeMillis() - this.instance > var1;
   }

   public void process(long var1) {
      this.instance = System.currentTimeMillis() + var1;
   }

   public void compute(long var1) {
      this.instance = var1;
   }

   public long process() {
      return System.currentTimeMillis() - this.instance;
   }

   public boolean compute() {
      return System.currentTimeMillis() - this.instance <= 0L;
   }

   public boolean resolve(long var1) {
      return System.currentTimeMillis() - this.instance > var1;
   }

   public boolean resolve() {
      return this.instance < System.currentTimeMillis();
   }

   public boolean handle(long var1, boolean var3) {
      boolean var4 = System.currentTimeMillis() - this.instance >= var1;
      if (var4 && var3) {
         this.handle();
      }

      return var4;
   }
   public long update() {
      return this.instance;
   }
}
