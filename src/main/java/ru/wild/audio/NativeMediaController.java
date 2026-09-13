package ru.wild.audio;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
public final class NativeMediaController {
   private static boolean instance;

   public static native void handle();

   public static native void process();

   public static native void compute();

   public static native void handle(long var0);
   public static boolean resolve() {
      return instance;
   }
   static {
      instance = false;
      label91:
      if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
         Path var0 = null;
         boolean var9 = false /* VF: Semaphore variable */;

         label88: {
            try {
               var9 = true;
               var0 = Files.createTempFile("wild_media_controller", ".dll");

               try (InputStream var1 = NativeMediaController.class.getResourceAsStream("/assets/wild/natives/MediaController.dll")) {
                  if (var1 != null) {
                     Files.copy(var1, var0, StandardCopyOption.REPLACE_EXISTING);
                     System.load(var0.toAbsolutePath().toString());
                     instance = true;
                  }
                  break label88;
               }
            } catch (Exception | UnsatisfiedLinkError var12) {
               instance = false;
               var9 = false;
            } finally {
               if (var9) {
                  if (var0 != null) {
                     var0.toFile().deleteOnExit();
                  }
               }
            }

            if (var0 != null) {
               var0.toFile().deleteOnExit();
            }
            break label91;
         }

         if (var0 != null) {
            var0.toFile().deleteOnExit();
         }
      }
   }
}
