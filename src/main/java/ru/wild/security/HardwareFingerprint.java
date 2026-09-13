package ru.wild.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class HardwareFingerprint {
   private static final String instance = "UNKNOWN";
   private static final boolean data = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   private static final long context = Long.getLong("wild.hwid.processTimeoutMs", 1500L);
   private static volatile String config;
   private static volatile String state;
   private static volatile String cache;
   private static volatile String output;

   private HardwareFingerprint() {
   }

   public static String handle() {
      String var0 = config;
      if (var0 != null) {
         return var0;
      }

      synchronized (HardwareFingerprint.class) {
         var0 = config;
         if (var0 == null) {
            config = var0 = update() + "|" + check();
         }

         return var0;
      }
   }

   private static String resolve() {
      String var0 = state;
      if (var0 != null) {
         return var0;
      }

      synchronized (HardwareFingerprint.class) {
         var0 = state;
         if (var0 == null) {
            state = var0 = update() + "|" + apply() + "|" + execute() + "|" + prepare() + "|" + check();
         }

         return var0;
      }
   }

   public static String process() {
      String var0 = cache;
      if (var0 != null) {
         return var0;
      }

      synchronized (HardwareFingerprint.class) {
         var0 = cache;
         if (var0 == null) {
            cache = var0 = process(handle());
         }

         return var0;
      }
   }
   public static String compute() {
      String var0 = output;
      if (var0 != null) {
         return var0;
      }

      Class<HardwareFingerprint> var1 = HardwareFingerprint.class;
      synchronized (HardwareFingerprint.class){} // $VF: monitorenter 

      try {
         var0 = output;
         if (var0 == null) {
            output = var0 = process(resolve());
         }
         return var0;
      } finally {
      }
   }

   public static boolean handle(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim().toLowerCase(Locale.ROOT);
         return process(var1, process().toLowerCase(Locale.ROOT)) ? true : process(var1, compute().toLowerCase(Locale.ROOT));
      } else {
         return false;
      }
   }

   private static String update() {
      return handle("csproduct", "UUID");
   }

   private static String apply() {
      return handle("diskdrive", "SerialNumber");
   }

   private static String execute() {
      return handle("baseboard", "SerialNumber");
   }

   private static String prepare() {
      return handle("cpu", "ProcessorId");
   }

   private static String check() {
      String var0 = data ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
      if (var0 != null && !var0.isBlank()) {
         return var0.trim();
      } else {
         return data ? handle("computersystem", "Name") : handle(new String[]{"hostname"});
      }
   }

   private static String handle(String var0, String var1) {
      if (!data) {
         return "UNKNOWN";
      }

      List<String> var2 = process("wmic", var0, "get", var1);
      boolean var3 = false;

      for (String var5 : var2) {
         String var6 = var5.trim();
         if (!var6.isEmpty()) {
            if (var3) {
               return var6;
            }

            var3 = true;
         }
      }

      return "UNKNOWN";
   }

   private static String handle(String... var0) {
      for (String var2 : process(var0)) {
         if (var2 != null && !var2.isBlank()) {
            return var2.trim();
         }
      }

      return "UNKNOWN";
   }

   private static List<String> process(String... var0) {
      Process var1 = null;

      try {
         var1 = new ProcessBuilder(var0).redirectErrorStream(true).start();
         if (!var1.waitFor(context, TimeUnit.MILLISECONDS)) {
            var1.destroyForcibly();
            return List.of();
         } else {
            String var2 = new String(var1.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return var2.lines().toList();
         }
      } catch (Throwable var3) {
         if (var1 != null) {
            var1.destroyForcibly();
         }

         return List.of();
      }
   }

   private static String process(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         byte[] var2 = var1.digest(var0.getBytes(StandardCharsets.UTF_8));
         return HexFormat.of().formatHex(var2);
      } catch (Throwable var3) {
         throw new IllegalStateException("                                   ", var3);
      }
   }

   private static boolean process(String var0, String var1) {
      if (var0 != null && var1 != null) {
         byte[] var2 = var0.getBytes(StandardCharsets.UTF_8);
         byte[] var3 = var1.getBytes(StandardCharsets.UTF_8);
         if (var2.length != var3.length) {
            return false;
         }

         int var4 = 0;

         for (int var5 = 0; var5 < var2.length; var5++) {
            var4 |= var2[var5] ^ var3[var5];
         }

         return var4 == 0;
      } else {
         return false;
      }
   }
}
