package ru.wild.render;

public final class RenderAttemptGuard {
   private boolean instance;

   public boolean handle() {
      return !this.instance;
   }

   public void process() {
      this.instance = true;
   }

   public <T> T handle(RenderAttemptGuard.Callback<T> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("attempt must not be null");
      }

      if (this.instance) {
         return null;
      }

      try {
         return (T)var1.handle();
      } catch (RuntimeException var3) {
         this.instance = true;
         return null;
      }
   }

   public boolean handle(RenderAttemptGuard.PrimaryCallback var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("attempt must not be null");
      }

      if (this.instance) {
         return false;
      }

      try {
         var1.handle();
         return true;
      } catch (RuntimeException var3) {
         this.instance = true;
         return false;
      }
   }

   public boolean compute() {
      return this.instance;
   }

   @FunctionalInterface
   public interface Callback<T> {
      T handle();
   }

   @FunctionalInterface
   public interface PrimaryCallback {
      void handle();
   }
}
