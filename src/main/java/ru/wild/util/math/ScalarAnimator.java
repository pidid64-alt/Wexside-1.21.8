package ru.wild.util.math;

public class ScalarAnimator {
   private long instance;
   private double data;
   private double context;
   private double config;
   private double state;
   private AnimationCurve cache;
   private ParametricEasing output;
   private AnimationInterpolationMode current;
   private boolean active;

   public ScalarAnimator() {
      this.cache = EasingFunctions.cache;
      this.output = new CubicBezierEasing();
      this.current = AnimationInterpolationMode.EASING;
      this.active = false;
   }

   public ScalarAnimator handle(double var1, double var3) {
      return this.handle(var1, var3, EasingFunctions.cache, false);
   }

   public ScalarAnimator handle(double var1, double var3, AnimationCurve var5) {
      return this.handle(var1, var3, var5, false);
   }

   public ScalarAnimator handle(double var1, double var3, ParametricEasing var5) {
      return this.handle(var1, var3, var5, false);
   }

   public ScalarAnimator handle(double var1, double var3, boolean var5) {
      return this.handle(var1, var3, EasingFunctions.cache, var5);
   }

   public ScalarAnimator handle(double var1, double var3, AnimationCurve var5, boolean var6) {
      if (this.handle(var6, var1)) {
         if (this.onTick()) {
            System.out.println("Animate cancelled due to target val equals from val");
         }

         return this;
      } else {
         this.handle(AnimationInterpolationMode.EASING)
            .handle(var5)
            .handle(var3 * 1000.0)
            .handle(System.currentTimeMillis())
            .process(this.check())
            .compute(var1);
         if (this.onTick()) {
            System.out.println("#animate {\n    to value: " + this.prepare() + "\n    from value: " + this.check() + "\n    duration: " + this.apply() + "\n}");
         }

         return this;
      }
   }

   public ScalarAnimator handle(double var1, double var3, ParametricEasing var5, boolean var6) {
      if (this.handle(var6, var1)) {
         if (this.onTick()) {
            System.out.println("Animate cancelled due to target val equals from val");
         }

         return this;
      } else {
         this.handle(AnimationInterpolationMode.BEZIER)
            .handle(var5)
            .handle(var3 * 1000.0)
            .handle(System.currentTimeMillis())
            .process(this.check())
            .compute(var1);
         if (this.onTick()) {
            System.out
               .println(
                  "#animate {\n    to value: "
                     + this.prepare()
                     + "\n    from value: "
                     + this.check()
                     + "\n    duration: "
                     + this.apply()
                     + "\n    type: "
                     + this.select().name()
                     + "\n}"
               );
         }

         return this;
      }
   }

   public boolean handle() {
      boolean var1 = this.process();
      if (var1) {
         if (this.select().equals(AnimationInterpolationMode.BEZIER)) {
            this.resolve(this.handle(this.execute(), this.prepare(), this.render().handle(this.resolve())));
         } else {
            this.resolve(this.handle(this.execute(), this.prepare(), this.refresh().ease(this.resolve())));
         }
      } else {
         this.handle(0L);
         this.resolve(this.prepare());
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
      return (System.currentTimeMillis() - this.update()) / this.apply();
   }

   public boolean handle(boolean var1, double var2) {
      return var1 && this.process() && (var2 == this.execute() || var2 == this.prepare() || var2 == this.check());
   }

   public double handle(double var1, double var3, double var5) {
      return var1 + (var3 - var1) * var5;
   }

   public long update() {
      return this.instance;
   }

   public double apply() {
      return this.data;
   }

   public double execute() {
      return this.context;
   }

   public double prepare() {
      return this.config;
   }

   public double check() {
      return this.state;
   }

   public boolean onTick() {
      return this.active;
   }

   public AnimationInterpolationMode select() {
      return this.current;
   }

   public AnimationCurve refresh() {
      return this.cache;
   }

   public ParametricEasing render() {
      return this.output;
   }

   public ScalarAnimator handle(long var1) {
      this.instance = var1;
      return this;
   }

   public ScalarAnimator handle(double var1) {
      this.data = var1;
      return this;
   }

   public ScalarAnimator process(double var1) {
      this.context = var1;
      return this;
   }

   public ScalarAnimator compute(double var1) {
      this.config = var1;
      return this;
   }

   public ScalarAnimator resolve(double var1) {
      this.state = var1;
      return this;
   }

   public ScalarAnimator handle(AnimationCurve var1) {
      this.cache = var1;
      return this;
   }

   public ScalarAnimator handle(boolean var1) {
      this.active = var1;
      return this;
   }

   public ScalarAnimator handle(ParametricEasing var1) {
      this.output = var1;
      return this;
   }

   public ScalarAnimator handle(AnimationInterpolationMode var1) {
      this.current = var1;
      return this;
   }
}
