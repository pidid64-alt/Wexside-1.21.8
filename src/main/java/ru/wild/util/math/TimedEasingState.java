package ru.wild.util.math;

public class TimedEasingState {
   private EasingFunction instance;
   private long data;
   private long context;
   private long config;
   private double state;
   private double cache;
   private double output;
   private boolean current;

   public TimedEasingState(EasingFunction var1, long var2) {
      this.instance = var1;
      this.config = System.currentTimeMillis();
      this.data = var2;
   }

   public void handle(double var1) {
      this.context = System.currentTimeMillis();
      if (this.data <= 0L) {
         this.cache = var1;
         this.state = var1;
         this.output = var1;
         this.current = true;
      } else {
         if (this.cache != var1) {
            this.cache = var1;
            this.process();
         } else {
            this.current = this.context - this.config >= this.data;
            if (this.current) {
               this.output = var1;
               return;
            }
         }

         double var3 = this.handle();
         double var5 = this.instance.handle().apply(var3);
         if (this.output > var1) {
            this.output = this.state - (this.state - var1) * var5;
         } else {
            this.output = this.state + (var1 - this.state) * var5;
         }

         if (var3 >= 1.0) {
            this.output = var1;
            this.current = true;
         }
      }
   }

   public double handle() {
      if (this.data <= 0L) {
         return 1.0;
      }

      double var1 = (double)(System.currentTimeMillis() - this.config) / this.data;
      return Math.max(0.0, Math.min(1.0, var1));
   }

   public void process() {
      this.config = System.currentTimeMillis();
      this.state = this.output;
      this.current = false;
   }

   public EasingFunction compute() {
      return this.instance;
   }

   public void handle(EasingFunction var1) {
      this.instance = var1;
   }

   public long resolve() {
      return this.data;
   }

   public void handle(long var1) {
      this.data = var1;
   }

   public long update() {
      return this.context;
   }

   public void process(long var1) {
      this.context = var1;
   }

   public long apply() {
      return this.config;
   }

   public void compute(long var1) {
      this.config = var1;
   }

   public double execute() {
      return this.state;
   }

   public void process(double var1) {
      this.state = var1;
   }

   public double prepare() {
      return this.cache;
   }

   public void compute(double var1) {
      this.cache = var1;
   }

   public double check() {
      return this.output;
   }

   public void resolve(double var1) {
      this.output = var1;
   }

   public boolean onTick() {
      return this.current;
   }

   public void handle(boolean var1) {
      this.current = var1;
   }
}
