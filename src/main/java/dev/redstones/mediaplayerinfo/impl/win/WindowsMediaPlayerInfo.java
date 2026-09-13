package dev.redstones.mediaplayerinfo.impl.win;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WindowsMediaPlayerInfo implements MediaPlayerInfo {
   private static final String RESOURCE_PATH = "/mediaplayerinfo/natives/win/MediaPlayerInfo.dll";
   private static final boolean LOADED;
   private static final Throwable LOAD_ERROR;
   private static final AtomicBoolean LOAD_ERROR_LOGGED = new AtomicBoolean(false);

   public WindowsMediaPlayerInfo() {
      if (!LOADED) {
         logLoadError();
      }
   }

   public static boolean isAvailable() {
      if (!LOADED) {
         logLoadError();
      }

      return LOADED;
   }

   @Override
   public native List<IMediaSession> getMediaSessions();

   private static void logLoadError() {
      if (LOAD_ERROR_LOGGED.compareAndSet(false, true)) {
         System.err.println("[Wild][MusicPlayer] MediaPlayerInfo native load failed: " + errorSummary(LOAD_ERROR));
      }
   }

   private static String errorSummary(Throwable var0) {
      if (var0 == null) {
         return "unknown";
      }

      String var1 = var0.getMessage();
      return var0.getClass().getSimpleName() + (var1 != null && !var1.isBlank() ? ": " + var1 : "");
   }

   private static WindowsMediaPlayerInfo.LoadResult loadNative() {
      try {
         Path var0 = Files.createTempDirectory("mediaplayerinfo-");
         Path var1 = var0.resolve("MediaPlayerInfo.dll");

         try (InputStream var2 = WindowsMediaPlayerInfo.class.getResourceAsStream("/mediaplayerinfo/natives/win/MediaPlayerInfo.dll")) {
            if (var2 == null) {
               throw new IOException("Resource not found: /mediaplayerinfo/natives/win/MediaPlayerInfo.dll");
            }

            Files.copy(var2, var1, StandardCopyOption.REPLACE_EXISTING);
         }

         System.load(var1.toAbsolutePath().toString());

         try {
            Files.deleteIfExists(var1);
            Files.deleteIfExists(var0);
         } catch (IOException var6) {
            var1.toFile().deleteOnExit();
            var0.toFile().deleteOnExit();
         }

         return new WindowsMediaPlayerInfo.LoadResult(true, null);
      } catch (Throwable var8) {
         return new WindowsMediaPlayerInfo.LoadResult(false, var8);
      }
   }

   static {
      WindowsMediaPlayerInfo.LoadResult var0 = loadNative();
      LOADED = var0.loaded();
      LOAD_ERROR = var0.error();
   }

   record LoadResult(boolean loaded, Throwable error) {
   }
}
