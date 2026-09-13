package ru.wild.util.math;

@FunctionalInterface
public interface FloatEasing {
   float ease(float var1);

   static FloatEasing handle() {
      return var0 -> var0;
   }

   default FloatEasing handle(FloatEasing var1) {
      return var1 == null ? this : var2 -> var1.ease(this.ease(var2));
   }
}
