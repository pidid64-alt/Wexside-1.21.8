package ru.wild.security;

import java.util.concurrent.TimeUnit;
import ru.wild.audio.ProceduralSoundSynthesizer;
import ru.wild.core.EventDispatchBoundary;

public final class AccessGuard {
   private static final long instance = Long.getLong("wild.guard.checkIntervalMs", 1000L);
   private static final long data = Long.getLong("wild.guard.localExpiryGraceMs", TimeUnit.HOURS.toMillis(24L));
   private static volatile boolean context;
   private static volatile String config;
   private static final Object state = new Object();
   private static volatile Thread cache;

   private AccessGuard() {
   }

   public static void handle() {
      if (select()) {
         prepare();
         if (context) {
            throw new GuardViolationException();
         }
      }
   }

   public static boolean process() {
      try {
         handle();
         return true;
      } catch (GuardViolationException var1) {
         throw EventDispatchBoundary.handle(var1);
      }
   }

   public static void handle(String var0) {
      if (handle(TrustedTimeProvider.process())) {
         process(var0);
      }
   }

   private static void process(String var0) {
      context = true;
      config = var0 != null && !var0.isBlank()
         ? var0
         : "Crashpad_Handler: Device loss detected. Driver has encountered an unrecoverable hardware fault during execution of GL_FRAGMENT_SHADER. GL_CONTEXT_LOST (0x0507).";
      throw new GuardViolationException();
   }

   public static String compute() {
      return config;
   }

   private static void resolve() {
      synchronized (state) {
         if (cache == null || !cache.isAlive()) {
            Thread var1 = new Thread(AccessGuard::update, "WildAccessGuard");
            var1.setDaemon(true);
            var1.setPriority(1);
            cache = var1;
            var1.start();
         }
      }
   }

   private static void update() {
      try {
         apply();
      } catch (Throwable var2) {
      }

      for (; !context; apply()) {
         try {
            Thread.sleep(instance);
         } catch (InterruptedException var1) {
            Thread.currentThread().interrupt();
            return;
         }
      }
   }

   private static void apply() {
      if (select()) {
         try {
            execute();
         } catch (Throwable var1) {
         }
      }
   }

   private static void execute() {
      if (select()) {
         DelayedIntegrityFuse.resolve();
         long var0 = TrustedTimeProvider.handle();
         boolean var2 = handle(var0);
         if (var2) {
            ProceduralSoundSynthesizer.handle();
            check();
         }

         if (!var2 && DelayedIntegrityFuse.process()) {
            DelayedIntegrityFuse.compute();
         }

         GuardStateAutosave.handle(var0 / 1000L);
      }
   }

   private static void prepare() {
      if (!context && onTick()) {
         long var0 = TrustedTimeProvider.process();
         if (handle(var0) && (TrustedTimeProvider.resolve() || var0 - 1788525348375L >= data)) {
            check();
         }
      }
   }

   private static boolean handle(long var0) {
      return onTick() && var0 >= 1788525348375L;
   }

   private static void check() {
      process("Unhandled exception at 0x00007FFAC32155B2 (nvoglv64.dll) in App.exe: 0xC0000005: Access violation reading location 0x0000000000000348.");
   }

   private static boolean onTick() {
      return select();
   }

   private static boolean select() {
      if (Boolean.getBoolean("wild.guard.enforce")) {
         return true;
      }

      try {
         String var0 = String.valueOf(AccessGuard.class.getProtectionDomain().getCodeSource().getLocation());
         return var0.toLowerCase().endsWith(".jar");
      } catch (Throwable var1) {
         return true;
      }
   }

   static {
      resolve();
   }
}
