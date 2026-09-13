package ru.wild.util.math;
public class Stopwatch {
   private long data;
   public long instance = System.currentTimeMillis();

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

   public boolean handle(double var1) {
      return System.currentTimeMillis() - var1 >= this.data;
   }

   public boolean process(double var1) {
      boolean var3 = this.handle(var1);
      if (var3) {
         this.handle();
      }

      return var3;
   }

   public long process() {
      return System.currentTimeMillis() - this.data;
   }

   public void resolve(long var1) {
      this.data = System.currentTimeMillis() - var1;
   }

   public long compute() {
      return System.currentTimeMillis() - this.instance;
   }

   public boolean resolve() {
      return System.currentTimeMillis() - this.instance <= 0L;
   }

   public boolean update(long var1) {
      return System.currentTimeMillis() - this.instance > var1;
   }

   public boolean update() {
      return this.instance < System.currentTimeMillis();
   }

   public boolean handle(long var1, boolean var3) {
      if (System.currentTimeMillis() - this.instance > var1) {
         if (var3) {
            this.handle();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean compute(double var1) {
      return this.apply() >= var1;
   }

   public long apply() {
      return System.currentTimeMillis() - this.instance;
   }

   public long handle(int var1) {
      return System.currentTimeMillis() + var1;
   }

   public long execute() {
      return this.instance;
   }

   public void prepare() {
      this.instance = System.currentTimeMillis();
   }

   public boolean apply(long var1) {
      return System.currentTimeMillis() - this.instance >= var1;
   }

   public boolean execute(long var1) {
      if (System.currentTimeMillis() - this.instance >= var1) {
         this.handle();
         return true;
      } else {
         return false;
      }
   }

   public boolean prepare(long var1) {
      return System.currentTimeMillis() - this.instance >= var1;
   }
   public long check() {
      return this.data;
   }

   public static class PrimaryState {
      public long instance;
      public long data = System.currentTimeMillis();

      public void handle() {
         this.data = System.currentTimeMillis();
      }

      public long process() {
         return System.currentTimeMillis() - this.data;
      }

      public boolean handle(long var1) {
         return System.currentTimeMillis() - this.data > var1;
      }

      public boolean handle(float var1) {
         long var2 = (long)(var1 * 1000.0F);
         return System.currentTimeMillis() - this.data > var2;
      }

      public boolean handle(double var1) {
         return System.currentTimeMillis() - var1 >= this.instance;
      }

      public boolean handle(long var1, boolean var3) {
         boolean var4 = this.process() >= var1;
         if (var4 && var3) {
            this.handle();
         }

         return var4;
      }

      private long resolve() {
         return System.currentTimeMillis();
      }

      public long compute() {
         return this.resolve() - this.data;
      }
      public void process(long var1) {
         this.instance = var1;
      }
      public void compute(long var1) {
         this.data = var1;
      }
   }

   public static class State {
      private long instance;

      private State() {
         this.process();
      }

      public static Stopwatch.State handle() {
         return new Stopwatch.State();
      }

      public void process() {
         this.instance = System.currentTimeMillis();
      }

      public long compute() {
         return System.currentTimeMillis() - this.instance;
      }

      public boolean handle(long var1) {
         return this.compute() >= var1;
      }

      public boolean handle(long var1, boolean var3) {
         boolean var4 = this.compute() >= var1;
         if (var4 && var3) {
            this.process();
         }

         return var4;
      }

      public boolean handle(double var1) {
         return this.compute() >= var1;
      }

      public boolean process(long var1) {
         boolean var3 = this.compute() - var1 >= 0L;
         if (var3) {
            this.process();
         }

         return var3;
      }
      public void compute(long var1) {
         this.instance = var1;
      }
   }
}
