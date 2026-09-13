package ru.wild.render.shader;

public final class SamplerLease {
   private SamplerLease() {
   }

   public static <T> SamplerLease.DataRecord handle(T var0, SamplerLease.Predicate<T> var1, SamplerLease.PrimaryPredicate<T> var2) {
      if (var0 == null) {
         return new SamplerLease.DataRecord(true, null);
      }

      try {
         var1.close(var0);
         return new SamplerLease.DataRecord(true, null);
      } catch (RuntimeException var6) {
         RuntimeException var3 = var6;

         try {
            return new SamplerLease.DataRecord(var2.isClosed(var0), var3);
         } catch (RuntimeException var5) {
            if (var5 != var6) {
               var6.addSuppressed(var5);
            }

            return new SamplerLease.DataRecord(false, var6);
         }
      }
   }

   public record DataRecord(boolean released, RuntimeException failure) {
   }

   @FunctionalInterface
   public interface Predicate<T> {
      void close(T var1);
   }

   @FunctionalInterface
   public interface PrimaryPredicate<T> {
      boolean isClosed(T var1);
   }
}
