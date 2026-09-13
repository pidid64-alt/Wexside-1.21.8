package ru.wild.network;

import com.sun.jna.Callback;

public interface DiscordJoinGameCallback extends Callback {
   void handle(String var1);
}
