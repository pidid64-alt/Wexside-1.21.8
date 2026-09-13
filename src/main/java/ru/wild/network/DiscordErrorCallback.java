package ru.wild.network;

import com.sun.jna.Callback;

public interface DiscordErrorCallback extends Callback {
   void apply(int var1, String var2);
}
