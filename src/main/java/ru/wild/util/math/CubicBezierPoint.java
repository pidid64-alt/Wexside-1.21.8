package ru.wild.util.math;

public class CubicBezierPoint {
   private double instance;
   private double data;

   public CubicBezierPoint(double var1, double var3) {
      this.process(var1);
      this.compute(var3);
   }

   public CubicBezierPoint(CubicBezierPoint var1) {
      this.process(var1.process());
      this.compute(var1.compute());
   }

   public CubicBezierPoint handle() {
      return new CubicBezierPoint(this);
   }

   public CubicBezierPoint handle(double var1, double var3) {
      this.process(var1);
      this.compute(var3);
      return this;
   }

   public CubicBezierPoint process(double var1, double var3) {
      this.process(this.process() * var1);
      this.compute(this.compute() * var3);
      return this;
   }

   public CubicBezierPoint handle(double var1) {
      this.process(this.process() * var1);
      this.compute(this.compute() * var1);
      return this;
   }

   public CubicBezierPoint compute(double var1, double var3) {
      this.process(this.process() + var1);
      this.compute(this.compute() + var3);
      return this;
   }

   public CubicBezierPoint handle(CubicBezierPoint var1) {
      this.process(var1.process());
      this.compute(var1.compute());
      return this;
   }

   public CubicBezierPoint process(CubicBezierPoint var1) {
      this.process(this.process() + var1.process());
      this.compute(this.compute() + var1.compute());
      return this;
   }

   public double process() {
      return this.instance;
   }

   public double compute() {
      return this.data;
   }

   public void process(double var1) {
      this.instance = var1;
   }

   public void compute(double var1) {
      this.data = var1;
   }
}
