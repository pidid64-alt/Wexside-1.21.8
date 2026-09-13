package ru.wild.util.math;
public final class Easings {
   public static final double instance = 1.70158;
   public static final double data = 2.5949095;
   public static final double context = 2.70158;
   public static final double config = Math.PI * 2.0 / 3.0;
   public static final double state = Math.PI * 4.0 / 9.0;
   public static final DoubleEasing cache = var0 -> var0;
   public static final DoubleEasing output = handle(2);
   public static final DoubleEasing current = process(2);
   public static final DoubleEasing active = compute(2.0);
   public static final DoubleEasing mode = handle(3);
   public static final DoubleEasing selection = process(3);
   public static final DoubleEasing enabled = compute(3.0);
   public static final DoubleEasing renderer = handle(4);
   public static final DoubleEasing handler = process(4);
   public static final DoubleEasing animationDraw = compute(4.0);
   public static final DoubleEasing pointEncode = handle(5);
   public static final DoubleEasing animator = process(5);
   public static final DoubleEasing source = compute(5.0);
   public static final DoubleEasing target = var0 -> 1.0 - Math.cos(var0 * Math.PI / 2.0);
   public static final DoubleEasing pending = var0 -> Math.sin(var0 * Math.PI / 2.0);
   public static final DoubleEasing previous = var0 -> -(Math.cos(Math.PI * var0) - 1.0) / 2.0;
   public static final DoubleEasing latest = var0 -> 1.0 - Math.sqrt(1.0 - Math.pow(var0, 2.0));
   public static final DoubleEasing summary = var0 -> Math.sqrt(1.0 - Math.pow(var0 - 1.0, 2.0));
   public static final DoubleEasing matrixBlend = var0 -> var0 < 0.5
      ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * var0, 2.0))) / 2.0
      : (Math.sqrt(1.0 - Math.pow(-2.0 * var0 + 2.0, 2.0)) + 1.0) / 2.0;
   public static final DoubleEasing vectorMatch = var0 -> var0 != 0.0 && var0 != 1.0
      ? Math.pow(-2.0, 10.0 * var0 - 10.0) * Math.sin((var0 * 10.0 - 10.75) * (Math.PI * 2.0 / 3.0))
      : var0;
   public static final DoubleEasing itemProject = var0 -> var0 != 0.0 && var0 != 1.0
      ? Math.pow(2.0, -10.0 * var0) * Math.sin((var0 * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0
      : var0;
   public static final DoubleEasing responseCompute = var0 -> {
      if (var0 != 0.0 && var0 != 1.0) {
         return var0 < 0.5
            ? -(Math.pow(2.0, 20.0 * var0 - 10.0) * Math.sin((20.0 * var0 - 11.125) * (Math.PI * 4.0 / 9.0))) / 2.0
            : Math.pow(2.0, -20.0 * var0 + 10.0) * Math.sin((20.0 * var0 - 11.125) * (Math.PI * 4.0 / 9.0)) / 2.0 + 1.0;
      } else {
         return var0;
      }
   };
   public static final DoubleEasing providerFetch = var0 -> var0 != 0.0 ? Math.pow(2.0, 10.0 * var0 - 10.0) : var0;
   public static final DoubleEasing profileDraw = var0 -> var0 != 1.0 ? 1.0 - Math.pow(2.0, -10.0 * var0) : var0;
   public static final DoubleEasing vectorPerform = var0 -> {
      if (var0 != 0.0 && var0 != 1.0) {
         return var0 < 0.5 ? Math.pow(2.0, 20.0 * var0 - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * var0 + 10.0)) / 2.0;
      } else {
         return var0;
      }
   };
   public static final DoubleEasing eventAttach = var0 -> 2.70158 * Math.pow(var0, 3.0) - 1.70158 * Math.pow(var0, 2.0);
   public static final DoubleEasing serverRead = var0 -> 1.0 + 2.70158 * Math.pow(var0 - 1.0, 3.0) + 1.70158 * Math.pow(var0 - 1.0, 2.0);
   public static final DoubleEasing positionAdvance = var0 -> var0 < 0.5
      ? Math.pow(2.0 * var0, 2.0) * (7.189819 * var0 - 2.5949095) / 2.0
      : (Math.pow(2.0 * var0 - 2.0, 2.0) * (3.5949095 * (var0 * 2.0 - 2.0) + 2.5949095) + 2.0) / 2.0;
   public static final DoubleEasing frameCheck = var0 -> {
      double var2 = 7.5625;
      double var4 = 2.75;
      if (var0 < 1.0 / var4) {
         return var2 * Math.pow(var0, 2.0);
      } else if (var0 < 2.0 / var4) {
         return var2 * Math.pow(var0 - 1.5 / var4, 2.0) + 0.75;
      } else {
         return var0 < 2.5 / var4 ? var2 * Math.pow(var0 - 2.25 / var4, 2.0) + 0.9375 : var2 * Math.pow(var0 - 2.625 / var4, 2.0) + 0.984375;
      }
   };
   public static final DoubleEasing moduleCollect = var0 -> 1.0 - frameCheck.ease(1.0 - var0);
   public static final DoubleEasing providerClose = var0 -> var0 < 0.5
      ? (1.0 - frameCheck.ease(1.0 - 2.0 * var0)) / 2.0
      : (1.0 + frameCheck.ease(2.0 * var0 - 1.0)) / 2.0;

   public static DoubleEasing handle(double var0) {
      return var2 -> Math.pow(var2, var0);
   }

   public static DoubleEasing handle(int var0) {
      return handle((double)var0);
   }

   public static DoubleEasing process(double var0) {
      return var2 -> 1.0 - Math.pow(1.0 - var2, var0);
   }

   public static DoubleEasing process(int var0) {
      return process((double)var0);
   }

   public static DoubleEasing compute(double var0) {
      return var2 -> var2 < 0.5 ? Math.pow(2.0, var0 - 1.0) * Math.pow(var2, var0) : 1.0 - Math.pow(-2.0 * var2 + 2.0, var0) / 2.0;
   }
   private Easings() {
   }
}
