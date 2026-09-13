package ru.wild.core;

import ru.wild.security.AccessGuard;
import ru.wild.security.GuardViolationException;

public final class EventDispatchBoundary {
   private EventDispatchBoundary() {
   }

   public static void handle() {
      try {
         AccessGuard.handle();
      } catch (GuardViolationException var1) {
         throw handle(var1);
      }
   }

   public static void handle(Runnable var0) {
      try {
         var0.run();
      } catch (GuardViolationException var2) {
         throw handle(var2);
      }
   }

   public static RuntimeException handle(GuardViolationException var0) {
      Runtime.getRuntime().halt(0);
      return var0;
   }
}
