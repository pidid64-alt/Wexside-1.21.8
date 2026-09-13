package dev.redstones.mediaplayerinfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;

public final class MediaInfo implements Serializable {
   private final String title;
   private final String artist;
   private final byte[] artworkPng;
   private final long position;
   private final long duration;
   private final boolean playing;

   public MediaInfo(String var1, String var2, byte[] var3, long var4, long var6, boolean var8) {
      this.title = var1 == null ? "" : var1;
      this.artist = var2 == null ? "" : var2;
      this.artworkPng = var3 == null ? new byte[0] : Arrays.copyOf(var3, var3.length);
      this.position = Math.max(0L, var4);
      this.duration = Math.max(0L, var6);
      this.playing = var8;
   }

   public String getTitle() {
      return this.title;
   }

   public String getArtist() {
      return this.artist;
   }

   public byte[] getArtworkPng() {
      return Arrays.copyOf(this.artworkPng, this.artworkPng.length);
   }

   public long getPosition() {
      return this.position;
   }

   public long getDuration() {
      return this.duration;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public boolean getPlaying() {
      return this.playing;
   }

   public BufferedImage getArtwork() {
      if (this.artworkPng.length == 0) {
         return null;
      }

      try {
         return ImageIO.read(new ByteArrayInputStream(this.artworkPng));
      } catch (Exception var2) {
         return null;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof MediaInfo var2)
            ? false
            : this.position == var2.position
               && this.duration == var2.duration
               && this.playing == var2.playing
               && Objects.equals(this.title, var2.title)
               && Objects.equals(this.artist, var2.artist)
               && Arrays.equals(this.artworkPng, var2.artworkPng);
      }
   }

   @Override
   public int hashCode() {
      int var1 = Objects.hash(this.title, this.artist, this.position, this.duration, this.playing);
      return 31 * var1 + Arrays.hashCode(this.artworkPng);
   }

   @Override
   public String toString() {
      return "MediaInfo{title='"
         + this.title
         + "', artist='"
         + this.artist
         + "', position="
         + this.position
         + ", duration="
         + this.duration
         + ", playing="
         + this.playing
         + "}";
   }
}
