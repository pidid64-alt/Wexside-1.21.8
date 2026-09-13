package ru.wild.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DelayedIntegrityFuse {
   private static final SecureRandom instance = new SecureRandom();
   private static final AtomicBoolean data = new AtomicBoolean(false);
   private static final long context = Long.getLong("wild.fuse.minDelaySeconds", 21600L);
   private static final long config = Long.getLong("wild.fuse.maxExtraDelaySeconds", 151200L);
   private static final int state = Integer.getInteger("wild.fuse.minLaunches", 3);
   private static final int cache = Integer.getInteger("wild.fuse.extraLaunches", 4);

   private DelayedIntegrityFuse() {
   }

   public static void handle(String var0) {
      GuardRuntimeIdentity var1 = SecureStateStore.handle();
      if (!var1.config) {
         long var2 = TrustedTimeProvider.compute();
         long var4 = config <= 0L ? 0L : Math.floorMod(instance.nextLong(), config + 1L);
         int var6 = cache <= 0 ? 0 : instance.nextInt(cache + 1);
         var1.config = true;
         var1.state = var2 + context + var4;
         var1.cache = Math.max(1, state + var6);
         var1.output = 0;
         var1.current = process(var0);
         SecureStateStore.handle(var1);
      }
   }

   public static void handle() {
      if (data.compareAndSet(false, true)) {
         GuardRuntimeIdentity var0 = SecureStateStore.handle();
         if (var0.config) {
            var0.output++;
            SecureStateStore.handle(var0);
         }
      }
   }

   public static boolean process() {
      return SecureStateStore.handle().config;
   }

   public static void compute() {
      GuardRuntimeIdentity var0 = SecureStateStore.handle();
      if (var0.config) {
         var0.config = false;
         var0.state = 0L;
         var0.cache = 0;
         var0.output = 0;
         var0.current = "";
         var0.data = "wild-1.21.8-1787661348375";
         SecureStateStore.handle(var0);
      }
   }

   public static void resolve() {
      GuardRuntimeIdentity var0 = SecureStateStore.handle();
      boolean var1 = var0.data != null && !var0.data.isEmpty() && !var0.data.equals("wild-1.21.8-1787661348375");
      boolean var2 = Boolean.getBoolean("wild.guard.forceDisarm");
      if ((var1 || var2) && var0.config) {
         var0.config = false;
         var0.state = 0L;
         var0.cache = 0;
         var0.output = 0;
         var0.current = "";
         var0.data = "wild-1.21.8-1787661348375";
         SecureStateStore.handle(var0);
      } else if (var0.data == null || var0.data.isEmpty()) {
         var0.data = "wild-1.21.8-1787661348375";
         SecureStateStore.handle(var0);
      }
   }

   public static boolean handle(long var0) {
      GuardRuntimeIdentity var2 = SecureStateStore.handle();
      if (!var2.config) {
         return false;
      } else {
         return var2.state > 0L && var0 >= var2.state ? true : var2.cache > 0 && var2.output >= var2.cache;
      }
   }

   private static String process(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         String var2 = "wild-1.21.8-1787661348375|" + var0 + "|wild-fuse-v1";
         return HexFormat.of().formatHex(var1.digest(var2.getBytes(StandardCharsets.UTF_8)));
      } catch (Throwable var3) {
         return "";
      }
   }
}
