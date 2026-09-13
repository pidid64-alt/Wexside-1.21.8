package ru.wild.security;

public final class GuardViolationException extends RuntimeException {
   public GuardViolationException() {
      super((String)null);
   }

   public GuardViolationException(String var1) {
      super(var1 != null && !var1.isBlank() ? var1 : null);
   }
}
