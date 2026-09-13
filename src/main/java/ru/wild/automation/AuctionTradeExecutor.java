package ru.wild.automation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import ru.wild.modules.misc.AhHelper;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.util.math.Stopwatch;

public final class AuctionTradeExecutor {
   private static final int instance = 5;
   private static final long data = 900L;
   private static final Stopwatch context = new Stopwatch();
   private static AuctionTradeExecutor.Mode config = AuctionTradeExecutor.Mode.NONE;
   private static boolean state = false;
   private static boolean cache = false;
   private static boolean output = false;
   private static int current = 0;

   private AuctionTradeExecutor() {
   }

   public static void handle() {
      config = AuctionTradeExecutor.Mode.NONE;
      state = false;
      cache = false;
      output = false;
      current = 0;
      encodePoint();
   }

   public static void process() {
      if (output) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0.player != null) {
            if (compute(var0)) {
               output = false;
               current = 0;
            } else if (!drawAnimation()) {
               output = false;
               current = 0;
            } else if (current >= 5) {
               if (context.update(900L)) {
                  output = false;
               }
            } else {
               if (context.update(900L)) {
                  process(var0);
               }
            }
         }
      }
   }

   public static boolean compute() {
      return state;
   }

   static boolean resolve() {
      return cache;
   }

   public static boolean update() {
      return config == AuctionTradeExecutor.Mode.SELL || AutoBuy.target;
   }

   public static boolean apply() {
      return config == AuctionTradeExecutor.Mode.RESELL || AutoBuy.pending;
   }

   public static boolean execute() {
      return !state && !apply();
   }

   public static boolean prepare() {
      if (!execute()) {
         return false;
      }

      config = AuctionTradeExecutor.Mode.SELL;
      encodePoint();
      return true;
   }

   public static void handle(boolean var0) {
      if (config == AuctionTradeExecutor.Mode.SELL) {
         config = AuctionTradeExecutor.Mode.NONE;
      }

      AutoBuy.target = false;
      encodePoint();
      resolve(var0);
   }

   public static boolean check() {
      if (cache && !update()) {
         config = AuctionTradeExecutor.Mode.RESELL;
         encodePoint();
         return true;
      } else {
         return false;
      }
   }

   public static void process(boolean var0) {
      if (config == AuctionTradeExecutor.Mode.RESELL) {
         config = AuctionTradeExecutor.Mode.NONE;
      }

      AutoBuy.pending = false;
      encodePoint();
      resolve(var0);
   }

   public static void compute(boolean var0) {
      if (var0 && !state) {
         prepare();
      } else {
         encodePoint();
         resolve(true);
      }
   }

   public static void onTick() {
      state = false;
      cache = true;
      encodePoint();
   }

   public static void select() {
      state = true;
      cache = true;
      if (config == AuctionTradeExecutor.Mode.SELL) {
         config = AuctionTradeExecutor.Mode.NONE;
      }

      encodePoint();
   }

   public static void refresh() {
      state = false;
      cache = true;
      encodePoint();
   }

   public static void render() {
      cache = true;
      encodePoint();
   }

   public static void tick() {
      state = false;
      cache = false;
      if (config == AuctionTradeExecutor.Mode.RESELL) {
         config = AuctionTradeExecutor.Mode.NONE;
      }

      encodePoint();
   }

   public static void resolve(boolean var0) {
      if (var0) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1.player != null) {
            if (AutoBuy.source != null && AutoBuy.source.enabled) {
               handle(var1);
            }
         }
      }
   }

   private static void handle(MinecraftClient var0) {
      if (compute(var0)) {
         output = false;
         current = 0;
      } else if (drawAnimation()) {
         output = false;
         current = 0;
         try {
            var method = AutoBuy.class.getDeclaredMethod("blendMatrix");
            method.setAccessible(true);
            method.invoke(AutoBuy.source);
         } catch (ReflectiveOperationException var2) {
            throw new IllegalStateException("Unable to reset AutoBuy auction state", var2);
         }
      } else {
         var0.player.networkHandler.sendChatCommand("ah");
      }
   }

   private static void process(MinecraftClient var0) {
      var0.player.networkHandler.sendChatCommand("ah");
      current++;
      context.handle();
   }

   private static boolean compute(MinecraftClient var0) {
      return var0.currentScreen instanceof GenericContainerScreen var1 && AhHelper.handle(var1);
   }

   private static boolean drawAnimation() {
      return AutoBuy.source != null && AutoBuy.source.latest.process("FunTime");
   }

   private static void encodePoint() {
      AutoBuy.target = config == AuctionTradeExecutor.Mode.SELL;
      AutoBuy.pending = config == AuctionTradeExecutor.Mode.RESELL;
      AutoBuy.previous = state;
   }

   enum Mode {
      NONE,
      SELL,
      RESELL;
   }
}
