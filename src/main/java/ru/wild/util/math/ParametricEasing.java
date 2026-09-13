package ru.wild.util.math;

public abstract class ParametricEasing {
   private final CubicBezierPoint instance = new CubicBezierPoint(0.0, 0.0);
   private final CubicBezierPoint data = new CubicBezierPoint(1.0, 1.0);
   private CubicBezierPoint context;
   private CubicBezierPoint config;

   public ParametricEasing(CubicBezierPoint var1, CubicBezierPoint var2) {
      this.handle(var1);
      this.process(var2);
   }

   public ParametricEasing() {
   }

   public abstract double handle(double var1);

   public CubicBezierPoint handle() {
      return this.instance;
   }

   public CubicBezierPoint process() {
      return this.data;
   }

   public void handle(CubicBezierPoint var1) {
      this.context = var1;
   }

   public void process(CubicBezierPoint var1) {
      this.config = var1;
   }

   public CubicBezierPoint compute() {
      return this.context;
   }

   public CubicBezierPoint resolve() {
      return this.config;
   }
}
