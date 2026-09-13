package ru.wild.network;

import org.wild.rpc.DiscordEventHandlers;
import org.wild.rpc.DiscordRichPresence;
import ru.wild.WildClient;
import ru.wild.core.MinecraftContext;
import ru.wild.profile.Profile;

public class SocialLinks implements MinecraftContext {
   private static final String config = "1494051037655339148";
   private static final String state = "https://i.ibb.co/20hRBGzL/gif-gif.gif";
   private static final long cache = 250L;
   private static final long output = 1500L;
   private static final Object current = new Object();
   private static final DiscordReadyCallback active = var0 -> SocialLinks.context = var0 != null && var0.userId != null ? var0.userId : "";
   private static final DiscordDisconnectCallback mode = (var0, var1) -> {};
   private static final DiscordErrorCallback renderer = (var0, var1) -> {};
   private static final DiscordEventHandlers handler = new DiscordEventHandlers();
   public static DiscordRichPresence instance = new DiscordRichPresence();
   public static volatile boolean data;
   public static volatile String context = "";
   private static volatile Thread animationDraw;
   private static volatile DiscordRpcHelper pointEncode;
   private static volatile boolean animator;
   private static volatile String source = "";

   public void handle() {
      synchronized (SocialLinks.class) {
         if (!data) {
            if (DiscordRpcHelper.State.process()) {
               DiscordRpcHelper var2 = DiscordRpcHelper.State.handle();
               data = true;
               animator = true;
               pointEncode = var2;
               source = "";
               instance.startTimestamp = System.currentTimeMillis() / 1000L;
               instance.largeImageKey = "https://i.ibb.co/20hRBGzL/gif-gif.gif";
               instance.largeImageText = String.valueOf(Profile.getUid());
               instance.button_label_1 = "Telegram";
               instance.button_url_1 = "https://t.me/wildclient";
               instance.button_label_2 = "VK";
               instance.button_url_2 = "https://vk.com/wildclient";
               synchronized (current) {
                  var2.Discord_Initialize("1494051037655339148", handler, true, "");
               }

               Thread var8 = new Thread(() -> handle(var2), "TH-RPC-Handler");
               var8.setDaemon(true);
               animationDraw = var8;
               var8.start();
            }
         }
      }
   }

   public static void process() {
      Thread var0;
      DiscordRpcHelper var1;
      synchronized (SocialLinks.class) {
         if (!data && animationDraw == null && pointEncode == null) {
            return;
         }

         data = false;
         animator = false;
         var0 = animationDraw;
         animationDraw = null;
         var1 = pointEncode;
         pointEncode = null;
      }

      source = "";
      if (var0 != null && var0 != Thread.currentThread()) {
         var0.interrupt();

         try {
            var0.join(1500L);
         } catch (InterruptedException var8) {
            Thread.currentThread().interrupt();
         }

         if (var0.isAlive()) {
            System.out.println("[Wild] rpc: callback thread did not quiesce, skipping native shutdown");
            return;
         }
      }

      if (var1 != null) {
         synchronized (current) {
            try {
               var1.Discord_ClearPresence();
            } catch (Throwable var6) {
            }

            try {
               var1.Discord_Shutdown();
            } catch (Throwable var5) {
            }
         }
      }
   }

   private static void handle(DiscordRpcHelper var0) {
      while (animator && !Thread.currentThread().isInterrupted()) {
         try {
            synchronized (current) {
               if (!animator) {
                  break;
               }

               var0.Discord_RunCallbacks();
               if (compute()) {
                  var0.Discord_UpdatePresence(instance);
               }
            }
         } catch (Throwable var5) {
         }

         try {
            Thread.sleep(250L);
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
            break;
         }
      }
   }

   private static boolean compute() {
      WildClient var0 = WildClient.instance;
      String var1 = "Version: " + (var0 == null ? "" : "1.21.8");
      String var2 = "User: " + Profile.getUsername();
      String var3 = var1 + "\u0000" + var2;
      if (var3.equals(source)) {
         return false;
      }

      source = var3;
      instance.details = var1;
      instance.state = var2;
      return true;
   }

   static {
      handler.ready = active;
      handler.disconnected = mode;
      handler.errored = renderer;
   }
}
