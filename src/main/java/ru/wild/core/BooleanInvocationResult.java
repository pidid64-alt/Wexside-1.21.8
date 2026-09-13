package ru.wild.core;

public enum BooleanInvocationResult {
   TRUE,
   FALSE,
   UNKNOWN;

   public static BooleanInvocationResult handle(boolean var0, boolean var1, Boolean var2) {
      if (!var0) {
         return FALSE;
      } else if (var1 && var2 != null) {
         return var2 ? TRUE : FALSE;
      } else {
         return UNKNOWN;
      }
   }

   public boolean handle() {
      return this == FALSE;
   }
}
