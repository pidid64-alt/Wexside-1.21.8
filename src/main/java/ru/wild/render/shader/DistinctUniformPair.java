package ru.wild.render.shader;

public final class DistinctUniformPair<T> {
   private final T instance;
   private final T data;
   private T context;

   DistinctUniformPair(T var1, T var2) {
      if (var1 != null && var2 != null && var1 != var2) {
         this.instance = (T)var1;
         this.data = (T)var2;
         this.context = (T)var1;
      } else {
         throw new IllegalArgumentException("ChinaHat uniform values must be distinct");
      }
   }

   T handle() {
      return this.context;
   }

   void process() {
      this.context = this.context == this.instance ? this.data : this.instance;
   }
}
