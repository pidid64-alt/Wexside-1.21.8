package ru.wild.util.math;

public class EasedDoubleAnimator {
   private long instance;
   private double data;
   private double context;
   private double config;
   private double state;
   private double cache;
   private AnimationCurve output = EasingFunctions.pending;
   private boolean current = false;
   private Runnable active;

   public EasedDoubleAnimator handle(double var1, double var3) {
      return this.handle(var1, var3, EasingFunctions.pending, false);
   }

   public EasedDoubleAnimator handle(double var1, double var3, AnimationCurve var5) {
      return this.handle(var1, var3, var5, false);
   }

   public EasedDoubleAnimator handle(double var1, double var3, boolean var5) {
      return this.handle(var1, var3, EasingFunctions.pending, var5);
   }

   public EasedDoubleAnimator handle(double var1, double var3, AnimationCurve var5, boolean var6) {
      if (this.handle(var6, var1)) {
         if (this.tick()) {
            System.out.println("Animate cancelled due to target val equals from val");
         }
      } else {
         this.handle(var5).handle(var3 * 1000.0).handle(System.currentTimeMillis()).process(this.select()).compute(var1);
         if (this.tick()) {
            System.out
               .println("#animate {\n    to value: " + this.onTick() + "\n    from value: " + this.select() + "\n    duration: " + this.prepare() + "\n}");
         }
      }

      return this;
   }

   public boolean handle() {
      this.update(this.select());
      boolean var1 = this.process();
      if (var1) {
         this.resolve(this.handle(this.check(), this.onTick(), this.render().ease(this.resolve())));
      } else {
         this.handle(0L);
         this.resolve(this.onTick());
         if (this.active != null) {
            this.active.run();
            this.active = null;
         }
      }

      return var1;
   }

   public boolean process() {
      return !this.compute();
   }

   public boolean compute() {
      return this.resolve() >= 1.0;
   }

   public double resolve() {
      return this.data == 0.0 ? 1.0 : (System.currentTimeMillis() - this.execute()) / this.prepare();
   }

   public boolean handle(boolean var1, double var2) {
      return var1 && this.process() && (var2 == this.check() || var2 == this.onTick() || var2 == this.select());
   }

   public double handle(double var1, double var3, double var5) {
      return var1 + (var3 - var1) * var5;
   }

   public EasedDoubleAnimator handle(long var1) {
      this.instance = var1;
      return this;
   }

   public EasedDoubleAnimator handle(double var1) {
      this.data = var1;
      return this;
   }

   public EasedDoubleAnimator process(double var1) {
      this.context = var1;
      return this;
   }

   public EasedDoubleAnimator compute(double var1) {
      this.config = var1;
      return this;
   }

   public EasedDoubleAnimator resolve(double var1) {
      this.state = var1;
      return this;
   }

   public EasedDoubleAnimator update(double var1) {
      this.cache = var1;
      return this;
   }

   public EasedDoubleAnimator handle(AnimationCurve var1) {
      this.output = var1;
      return this;
   }

   public EasedDoubleAnimator handle(boolean var1) {
      this.current = var1;
      return this;
   }

   public EasedDoubleAnimator handle(Runnable var1) {
      this.active = var1;
      return this;
   }

   public float update() {
      return (float)this.select();
   }

   public float apply() {
      return (float)this.refresh();
   }

   public void apply(double var1) {
      this.handle(var1, 1.0E-13);
      this.handle();
      this.resolve(var1);
   }

   public long execute() {
      return this.instance;
   }

   public double prepare() {
      return this.data;
   }

   public double check() {
      return this.context;
   }

   public double onTick() {
      return this.config;
   }

   public double select() {
      return this.state;
   }

   public double refresh() {
      return this.cache;
   }

   public AnimationCurve render() {
      return this.output;
   }

   public boolean tick() {
      return this.current;
   }
}
