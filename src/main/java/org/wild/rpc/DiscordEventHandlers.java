package org.wild.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import ru.wild.network.DiscordDisconnectCallback;
import ru.wild.network.DiscordErrorCallback;
import ru.wild.network.DiscordJoinGameCallback;
import ru.wild.network.DiscordJoinRequestCallback;
import ru.wild.network.DiscordReadyCallback;
import ru.wild.network.DiscordSpectateCallback;

public class DiscordEventHandlers extends Structure {
   public DiscordDisconnectCallback disconnected;
   public DiscordJoinRequestCallback joinRequest;
   public DiscordSpectateCallback spectateGame;
   public DiscordReadyCallback ready;
   public DiscordErrorCallback errored;
   public DiscordJoinGameCallback joinGame;

   protected List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }
}
