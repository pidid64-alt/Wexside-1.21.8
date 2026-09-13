package ru.wild.network;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.wild.rpc.DiscordEventHandlers;
import org.wild.rpc.DiscordRichPresence;

public interface DiscordRpcHelper extends Library {
   void Discord_UpdateHandlers(DiscordEventHandlers var1);

   void Discord_UpdatePresence(DiscordRichPresence var1);

   void Discord_Respond(String var1, int var2);

   void Discord_Register(String var1, String var2);

   void Discord_Shutdown();

   void Discord_UpdateConnection();

   void Discord_RegisterSteamGame(String var1, String var2);

   void Discord_RunCallbacks();

   void Discord_Initialize(String var1, DiscordEventHandlers var2, boolean var3, String var4);

   void Discord_ClearPresence();

   class State {
      private static DiscordRpcHelper instance;
      private static boolean data = false;

      public static DiscordRpcHelper handle() {
         if (!data) {
            data = true;

            try {
               instance = (DiscordRpcHelper)Native.loadLibrary("discord-rpc", DiscordRpcHelper.class);
            } catch (UnsatisfiedLinkError var1) {
               instance = null;
            }
         }

         return instance;
      }

      public static boolean process() {
         return handle() != null;
      }
   }
}
