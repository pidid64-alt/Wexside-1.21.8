package ru.wild.network;

import com.sun.jna.Callback;
import org.wild.rpc.DiscordUser;

public interface DiscordJoinRequestCallback extends Callback {
   void handle(DiscordUser var1);
}
