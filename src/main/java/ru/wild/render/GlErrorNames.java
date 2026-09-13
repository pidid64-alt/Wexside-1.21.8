package ru.wild.render;

import java.io.DataOutputStream;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import ru.wild.security.BuildFingerprint;

public final class GlErrorNames {
   private GlErrorNames() {
   }

   public static int handle() {
      try {
         return GL11.glGetInteger(35725);
      } catch (Throwable var1) {
         return -1;
      }
   }

   public static int process() {
      try {
         return GL11.glGetInteger(34016);
      } catch (Throwable var1) {
         return -1;
      }
   }

   public static int compute() {
      try {
         return GL11.glGetInteger(32873);
      } catch (Throwable var1) {
         return -1;
      }
   }

   public static int resolve() {
      try {
         for (int var0 = 0; var0 < 4; var0++) {
            int var1 = GL11.glGetError();
            if (var1 != 0) {
               return var1;
            }
         }

         return 0;
      } catch (Throwable var2) {
         return -1;
      }
   }

   static void handle(BuildFingerprint var0) {
      if (var0 != null) {
         var0.handle(handle());
         var0.handle(process());
         var0.handle(compute());
      }
   }

   public static void handle(DataOutputStream var0) throws IOException {
      var0.writeInt(handle());
      var0.writeInt(process());
      var0.writeInt(compute());
      var0.writeInt(resolve());
   }

   public static String handle(int var0) {
      return switch (var0) {
         case 0 -> "GL_NO_ERROR";
         case 1280 -> "GL_INVALID_ENUM";
         case 1281 -> "GL_INVALID_VALUE";
         case 1282 -> "GL_INVALID_OPERATION";
         case 1285 -> "GL_OUT_OF_MEMORY";
         default -> "0x" + Integer.toHexString(var0);
      };
   }
}
