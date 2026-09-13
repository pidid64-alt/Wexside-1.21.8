package ru.wild.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import ru.wild.api.event.PacketEvent;

public class ServerTickRateTracker {
   private static final double context = 50.0;
   private static final double config = 1.0E-6;
   private static final double state = 1000000.0;
   private static final long cache = 5000000000L;
   private static final double output = 0.15;
   private static final double current = 0.125;
   public static long instance = System.currentTimeMillis() - 588L;
   public static double data = 20.0;
   private static volatile long active = System.nanoTime();
   private static volatile long mode;
   private static volatile double selection;
   private static volatile double enabled;
   private static volatile boolean renderer;

   public static boolean handle(PacketEvent var0) {
      if (var0 != null && var0.update() == PacketEvent.Mode.RECEIVE) {
         Packet var1 = var0.resolve();
         long var2 = System.nanoTime();
         if (var1 instanceof WorldTimeUpdateS2CPacket) {
            handle(var2, System.currentTimeMillis());
            return true;
         }

         if (var1 instanceof KeepAliveS2CPacket) {
            handle(var2);
         }

         return false;
      } else {
         return false;
      }
   }

   public static void handle(long var0, long var2) {
      long var4 = instance;
      float var6 = (float)(var2 - var4);
      float var7 = var6 / 1000.0F;
      float var8 = var7 > 0.0F ? 20.0F / var7 : 20.0F;
      data = Math.min(var8, 20.0F);
      instance = var2;
      handle(var0, true);
   }

   public static void handle(long var0) {
      handle(var0, false);
   }

   private static void handle(long var0, boolean var2) {
      long var3 = mode;
      if (var3 > 0L) {
         update(var0 - var3);
      }

      mode = var0;
      if (var2 || active == 0L) {
         active = var0;
         renderer = true;
      }

      execute();
   }

   private static void update(long var0) {
      if (var0 > 0L) {
         double var2 = var0 * 1.0E-6;
         double var4 = Math.max(1.0, Math.rint(var2 / 50.0));
         double var6 = Math.abs(var2 - var4 * 50.0);
         double var8 = enabled;
         enabled = var8 <= 0.0 ? var6 : var8 + (var6 - var8) * 0.125;
      }
   }

   private static void execute() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null && var0.player != null && var0.getNetworkHandler() != null) {
         PlayerListEntry var1 = var0.getNetworkHandler().getPlayerListEntry(var0.player.getUuid());
         if (var1 != null) {
            int var2 = var1.getLatency();
            if (var2 > 0 && var2 <= 2000) {
               double var3 = selection;
               selection = var3 <= 0.0 ? var2 : var3 + (var2 - var3) * 0.15;
            }
         }
      }
   }

   public static double handle() {
      return data;
   }

   public static double process() {
      return 20.0 - data;
   }

   public static boolean compute() {
      return process(System.nanoTime());
   }

   public static boolean process(long var0) {
      long var2 = active;
      long var4 = var0 - var2;
      return renderer && var2 > 0L && var4 >= 0L && var4 <= 5000000000L;
   }

   public static double compute(long var0) {
      if (!process(var0)) {
         return 50.0;
      }

      double var2 = resolve(var0);
      double var4 = 50.0 - var2;
      return var4 <= 0.0 ? 50.0 : var4;
   }

   public static double resolve(long var0) {
      long var2 = active;
      if (var2 <= 0L) {
         return 0.0;
      }

      double var4 = (var0 - var2) * 1.0E-6 + selection * 0.5;
      var4 %= 50.0;
      if (var4 < 0.0) {
         var4 += 50.0;
      }

      return var4;
   }

   public static long handle(long var0, double var2) {
      if (!process(var0)) {
         return 0L;
      }

      double var4 = resolve(var0);
      double var6 = var2 - var4;
      if (var6 < 0.0) {
         var6 += 50.0;
      }

      return (long)(var6 * 1000000.0);
   }

   public static double resolve() {
      return selection;
   }

   public static double update() {
      return enabled;
   }

   public static void apply() {
      instance = System.currentTimeMillis() - 588L;
      data = 20.0;
      active = System.nanoTime();
      mode = 0L;
      selection = 0.0;
      enabled = 0.0;
      renderer = false;
   }
}
