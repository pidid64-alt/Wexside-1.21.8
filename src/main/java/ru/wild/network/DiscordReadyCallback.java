package ru.wild.network;

import com.sun.jna.Callback;
import org.wild.rpc.DiscordUser;

public interface DiscordReadyCallback extends Callback {
   void apply(DiscordUser var1);
}
