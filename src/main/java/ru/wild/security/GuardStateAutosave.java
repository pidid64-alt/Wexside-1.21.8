package ru.wild.security;

public final class GuardStateAutosave {
   private static final long instance = 300L;
   private static final long data = Long.getLong("wild.guard.stateSaveIntervalSeconds", 60L);
   private static volatile long context;

   private GuardStateAutosave() {
   }

   public static void handle(long var0) {
      GuardRuntimeIdentity var2 = SecureStateStore.handle();
      if (var2.context <= 0L || var0 + 300L >= var2.context) {
         if (var0 > var2.context && var0 - Math.max(var2.context, context) >= data) {
            var2.context = var0;
            var2.data = "wild-1.21.8-1787661348375";
            SecureStateStore.handle(var2);
            context = var0;
         }
      }
   }
}
