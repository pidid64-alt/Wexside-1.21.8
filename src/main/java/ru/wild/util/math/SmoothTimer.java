package ru.wild.util.math;

public abstract class SmoothTimer {
   public ElapsedTimer instance = new ElapsedTimer();
   protected int data;
   protected double context;
   protected AnimationDirection config;

   public SmoothTimer(int var1, double var2) {
      this.data = var1;
      this.context = var2;
      this.config = AnimationDirection.FORWARDS;
   }

   public SmoothTimer(int var1, double var2, AnimationDirection var4) {
      this.data = var1;
      this.context = var2;
      this.config = var4;
   }

   public boolean handle(AnimationDirection var1) {
      return this.resolve() && this.config.equals(var1);
   }

   public double handle() {
      return 1.0 - (double)this.instance.compute() / this.data * this.context;
   }

   public double process() {
      return this.context;
   }

   public void handle(double var1) {
      this.context = var1;
   }

   public void compute() {
      this.instance.process();
   }

   public boolean resolve() {
      return this.instance.handle((double)this.data);
   }

   public void update() {
      this.process(this.config.handle());
   }

   public AnimationDirection apply() {
      return this.config;
   }

   public void process(AnimationDirection var1) {
      if (this.config != var1) {
         this.config = var1;
         this.instance.handle(System.currentTimeMillis() - (this.data - Math.min(this.data, this.instance.compute())));
      }
   }

   public void handle(int var1) {
      this.data = var1;
   }

   protected boolean execute() {
      return false;
   }

   public long prepare() {
      return this.instance.compute();
   }

   public float check() {
      if (this.config == AnimationDirection.FORWARDS) {
         return this.resolve() ? (float)this.context : (float)(this.process(this.instance.compute()) * this.context);
      } else if (this.resolve()) {
         return 0.0F;
      } else if (this.execute()) {
         double var1 = Math.min(this.data, Math.max(0L, this.data - this.instance.compute()));
         return (float)(this.process(var1) * this.context);
      } else {
         return (float)((1.0 - this.process(this.instance.compute())) * this.context);
      }
   }

   protected abstract double process(double var1);
}
