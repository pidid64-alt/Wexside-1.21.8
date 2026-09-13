package ru.wild.network;

import com.sun.jna.Callback;

public interface DiscordSpectateCallback extends Callback {
   void handle(String var1);
}
