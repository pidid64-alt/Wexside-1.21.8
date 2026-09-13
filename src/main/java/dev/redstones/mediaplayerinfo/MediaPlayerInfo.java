package dev.redstones.mediaplayerinfo;

import dev.redstones.mediaplayerinfo.impl.DummyMediaPlayerInfo;
import dev.redstones.mediaplayerinfo.impl.win.WindowsMediaPlayerInfo;
import java.util.List;
import java.util.Locale;

public interface MediaPlayerInfo {
   MediaPlayerInfo INSTANCE = MediaPlayerInfo.SystemMediaPlayerInfo.getInstance();

   List<IMediaSession> getMediaSessions();

   final class SystemMediaPlayerInfo {
      private static final MediaPlayerInfo INSTANCE = create();

      private static MediaPlayerInfo create() {
         String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
         return var0.startsWith("windows") && WindowsMediaPlayerInfo.isAvailable() ? new WindowsMediaPlayerInfo() : new DummyMediaPlayerInfo();
      }

      public static MediaPlayerInfo getInstance() {
         return INSTANCE;
      }
   }
}
