package ru.wild.util.math;

public class CubicBezierEasing extends ParametricEasing {
   @Override
   public double handle(double var1) {
      double var3 = 1.0 - var1;
      double var5 = var3 * var3;
      double var7 = var1 * var1;
      CubicBezierPoint var9 = this.compute().handle();
      return this.handle()
         .handle()
         .process(var5, var3)
         .process(var9.handle(3.0 * var5 * var1))
         .process(var9.handle(this.resolve()).handle(3.0 * var3 * var7))
         .process(var9.handle(this.process()).handle(var7 * var1))
         .compute();
   }

   public static class State {
      private CubicBezierEasing instance = new CubicBezierEasing();

      public State(CubicBezierEasing var1) {
         this.instance = var1;
      }

      public State() {
      }

      public CubicBezierEasing.State handle(CubicBezierPoint var1) {
         this.instance.handle(var1);
         return this;
      }

      public CubicBezierEasing.State process(CubicBezierPoint var1) {
         this.instance.process(var1);
         return this;
      }

      public CubicBezierEasing handle() {
         return this.instance;
      }
   }
}
