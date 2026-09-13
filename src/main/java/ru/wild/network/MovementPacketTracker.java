package ru.wild.network;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;

public final class MovementPacketTracker {
   private static final double instance = 1.0E-7;
   private static volatile boolean data = false;
   private static volatile double context = 0.0;
   private static volatile double config = 0.0;
   private static volatile double state = 0.0;
   private static volatile float cache = 0.0F;
   private static volatile boolean output = true;
   private static volatile boolean current = false;
   private static volatile boolean active = false;
   private static volatile float mode = 0.0F;
   private static volatile float selection = 0.0F;
   private static volatile double enabled = 0.0;
   private static volatile double renderer = 0.0;
   private static volatile boolean handler = false;
   private static volatile long animationDraw = 0L;
   private static volatile long pointEncode = 0L;
   private static volatile long animator = 0L;

   private MovementPacketTracker() {
   }

   public static void handle(Packet<?> var0) {
      if (var0 instanceof ClientCommandC2SPacket var9) {
         handle(var9);
         animationDraw = System.currentTimeMillis();
      } else if (var0 instanceof PlayerMoveC2SPacket var1) {
         boolean var2 = var1.isOnGround();
         if (var1.changesLook()) {
            mode = var1.getYaw(mode);
            selection = var1.getPitch(selection);
            active = true;
            animator = System.currentTimeMillis();
         }

         if (var1.changesPosition()) {
            double var3 = var1.getX(context);
            double var5 = var1.getY(config);
            double var7 = var1.getZ(state);
            handle(var3, var5, var7, var2);
         } else {
            process(var2);
         }

         animationDraw = System.currentTimeMillis();
      }
   }

   public static void process(Packet<?> var0) {
      if (var0 instanceof GameJoinS2CPacket || var0 instanceof PlayerRespawnS2CPacket) {
         refresh();
      }
   }

   private static void handle(ClientCommandC2SPacket var0) {
      Mode var1 = var0.getMode();
      if (var1 == Mode.START_SPRINTING) {
         current = true;
         pointEncode = System.currentTimeMillis();
      } else {
         if (var1 == Mode.STOP_SPRINTING) {
            current = false;
            pointEncode = System.currentTimeMillis();
         }
      }
   }

   private static void handle(double var0, double var2, double var4, boolean var6) {
      if (!data) {
         data = true;
         context = var0;
         config = var2;
         state = var4;
         output = var6;
         cache = 0.0F;
         enabled = 0.0;
         renderer = 0.0;
         handler = false;
      } else {
         double var7 = var2 - config;
         renderer = enabled;
         enabled = var7;
         handler = !var6 && renderer > 1.0E-7 && var7 < -1.0E-7;
         if (var6) {
            cache = 0.0F;
         } else if (var7 < -1.0E-7) {
            cache += (float)(-var7);
         }

         context = var0;
         config = var2;
         state = var4;
         output = var6;
      }
   }

   private static void process(boolean var0) {
      output = var0;
      if (var0) {
         cache = 0.0F;
         handler = false;
      }
   }

   public static float handle() {
      return cache;
   }

   public static boolean process() {
      return output;
   }

   public static boolean compute() {
      return current;
   }

   public static boolean resolve() {
      return active;
   }

   public static float handle(float var0) {
      return active ? mode : var0;
   }

   public static float process(float var0) {
      return active ? selection : var0;
   }

   public static void handle(boolean var0) {
      current = var0;
      pointEncode = System.currentTimeMillis();
   }

   public static double update() {
      return enabled;
   }

   public static double apply() {
      return renderer;
   }

   public static boolean execute() {
      return handler;
   }

   public static long prepare() {
      return animationDraw;
   }

   public static long check() {
      return pointEncode;
   }

   public static long onTick() {
      return animator;
   }

   public static boolean select() {
      return data;
   }

   public static void refresh() {
      data = false;
      context = 0.0;
      config = 0.0;
      state = 0.0;
      cache = 0.0F;
      output = true;
      current = false;
      active = false;
      mode = 0.0F;
      selection = 0.0F;
      enabled = 0.0;
      renderer = 0.0;
      handler = false;
      animationDraw = 0L;
      pointEncode = 0L;
      animator = 0L;
   }
}
