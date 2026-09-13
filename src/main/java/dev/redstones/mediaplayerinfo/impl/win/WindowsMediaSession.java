package dev.redstones.mediaplayerinfo.impl.win;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;

public final class WindowsMediaSession implements IMediaSession {
   private final MediaInfo media;
   private final String owner;
   private final int index;
   private static volatile int cycle = -1;

   public WindowsMediaSession(MediaInfo var1, String var2, int var3) {
      this.media = var1;
      this.owner = var2 == null ? "" : var2;
      this.index = var3;
   }

   @Override
   public MediaInfo getMedia() {
      return this.media;
   }

   @Override
   public String getOwner() {
      return this.owner;
   }

   public int getIndex() {
      return this.index;
   }

   @Override
   public native void play();

   @Override
   public native void pause();

   @Override
   public native void playPause();

   @Override
   public native void stop();

   @Override
   public native void next();

   @Override
   public native void previous();

   @Override
   public void swapCycle() {
      cycle = cycle >= 2 ? 0 : cycle + 1;
   }

   @Override
   public int getCycleType() {
      return cycle;
   }

   public static int getCycle() {
      return cycle;
   }

   public static void setCycle(int var0) {
      cycle = var0;
   }
}
