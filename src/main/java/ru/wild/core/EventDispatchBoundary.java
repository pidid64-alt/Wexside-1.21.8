package ru.wild.core;

public final class EventDispatchBoundary {
   private EventDispatchBoundary() {
   }

   public static void handle() {
      // security removed - no-op
   }

   public static void handle(Runnable var0) {
      if (var0 != null) {
         var0.run();
      }
   }

   public static RuntimeException handle(Throwable var0) {
      if (var0 instanceof RuntimeException re) {
         throw re;
      }
      throw new RuntimeException(var0);
   }

   // backward compat stub - old signature that took GuardViolationException
   // now just delegates to generic handler
   public static RuntimeException handleGeneric(Throwable var0) {
      return handle(var0);
   }
}
