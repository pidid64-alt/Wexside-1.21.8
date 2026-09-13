package ru.wild.security;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public final class TrustedTimeProvider {
   private static final String instance = System.getProperty("wild.ntp.host", "time.windows.com");
   private static final int data = Integer.getInteger("wild.ntp.port", 123);
   private static final int context = Integer.getInteger("wild.ntp.timeoutMs", 1200);
   private static final long config = Long.getLong("wild.ntp.cacheTtlMs", 300000L);
   private static final long state = Long.getLong("wild.ntp.failCooldownMs", 60000L);
   private static final long cache = 2208988800L;
   private static volatile TrustedTimeProvider.DataRecord output;
   private static volatile long current;

   private TrustedTimeProvider() {
   }

   public static long handle() {
      TrustedTimeProvider.DataRecord var0 = output;
      long var1 = System.nanoTime();
      if (var0 != null && var1 - var0.nanoTime <= config * 1000000L) {
         return var0.epochMillis + (var1 - var0.nanoTime) / 1000000L;
      }

      if (var1 >= current) {
         TrustedTimeProvider.DataRecord var3 = handle(var1);
         if (var3 != null) {
            output = var3;
            return var3.epochMillis;
         }

         current = var1 + state * 1000000L;
      }

      return var0 != null ? var0.epochMillis + (var1 - var0.nanoTime) / 1000000L : System.currentTimeMillis();
   }

   public static long process() {
      TrustedTimeProvider.DataRecord var0 = output;
      long var1 = System.nanoTime();
      return var0 != null ? var0.epochMillis + (var1 - var0.nanoTime) / 1000000L : System.currentTimeMillis();
   }

   public static long compute() {
      return handle() / 1000L;
   }

   public static boolean resolve() {
      TrustedTimeProvider.DataRecord var0 = output;
      return var0 == null ? false : System.nanoTime() - var0.nanoTime <= config * 1000000L;
   }

   public static String update() {
      return resolve() ? "NTP:" + instance : "LOCAL";
   }

   public static void apply() {
      long var0 = System.nanoTime();
      TrustedTimeProvider.DataRecord var2 = handle(var0);
      if (var2 != null) {
         output = var2;
      } else {
         current = var0 + state * 1000000L;
      }
   }

   private static TrustedTimeProvider.DataRecord handle(long var0) {
      try {
         byte[] var2 = new byte[48];
         var2[0] = 35;
         InetAddress var3 = InetAddress.getByName(instance);
         DatagramPacket var4 = new DatagramPacket(var2, var2.length, var3, data);

         try (DatagramSocket var5 = new DatagramSocket()) {
            var5.setSoTimeout(context);
            var5.send(var4);
            DatagramPacket var6 = new DatagramPacket(var2, var2.length);
            var5.receive(var6);
            long var7 = System.nanoTime();
            long var9 = handle(var2);
            return var9 <= 0L ? null : new TrustedTimeProvider.DataRecord(var9, var7);
         }
      } catch (Throwable var14) {
         return null;
      }
   }

   private static long handle(byte[] var0) {
      long var1 = (var0[40] & 255L) << 24 | (var0[41] & 255L) << 16 | (var0[42] & 255L) << 8 | var0[43] & 255L;
      long var3 = (var0[44] & 255L) << 24 | (var0[45] & 255L) << 16 | (var0[46] & 255L) << 8 | var0[47] & 255L;
      long var5 = var1 - 2208988800L;
      long var7 = var5 * 1000L + var3 * 1000L / 4294967296L;
      return var7 > 0L ? var7 : 0L;
   }

   record DataRecord(long epochMillis, long nanoTime) {
   }
}
