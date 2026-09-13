package ru.wild.render;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public final class GlBreadcrumbLogger {
   private static final DateTimeFormatter instance = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static final Set<String> data = ConcurrentHashMap.newKeySet();
   private static final Object context = new Object();
   private static final boolean config = Boolean.getBoolean("wild.debug.gl.breadcrumbs");
   private static File state;
   private static boolean cache;
   private static boolean output;

   private GlBreadcrumbLogger() {
   }

   public static boolean handle() {
      return config;
   }

   public static void handle(String var0, String var1) {
      if (config) {
         process("[" + var0 + "] " + var1);
      }
   }

   public static void process(String var0, String var1) {
      process("[" + var0 + "] " + var1);
   }

   public static void handle(String var0, String var1, String var2) {
      if (data.add(var0)) {
         process("[" + var1 + "] " + var2);
      }
   }

   public static boolean process() {
      try {
         return GLFW.glfwGetCurrentContext() != 0L;
      } catch (Throwable var1) {
         return false;
      }
   }

   public static String handle(String var0) {
      StringBuilder var1 = null;

      try {
         for (int var2 = 0; var2 < 16; var2++) {
            int var3 = GL11.glGetError();
            if (var3 == 0) {
               break;
            }

            if (var1 == null) {
               var1 = new StringBuilder();
            } else {
               var1.append(", ");
            }

            var1.append(handle(var3));
         }
      } catch (Throwable var4) {
         return null;
      }

      if (var1 == null) {
         return null;
      }

      String var5 = var1.toString();
      process("[gl-error] before " + var0 + ": " + var5);
      return var5;
   }

   public static String handle(int var0) {
      return switch (var0) {
         case 1280 -> "GL_INVALID_ENUM";
         case 1281 -> "GL_INVALID_VALUE";
         case 1282 -> "GL_INVALID_OPERATION";
         case 1283 -> "GL_STACK_OVERFLOW";
         case 1284 -> "GL_STACK_UNDERFLOW";
         case 1285 -> "GL_OUT_OF_MEMORY";
         case 33305 -> "GL_FRAMEBUFFER_UNDEFINED";
         default -> "0x" + Integer.toHexString(var0);
      };
   }

   public static String compute() {
      if (!process()) {
         return "no current GL context on " + Thread.currentThread().getName();
      }

      try {
         return "vendor="
            + GL11.glGetString(7936)
            + " renderer="
            + GL11.glGetString(7937)
            + " version="
            + GL11.glGetString(7938)
            + " thread="
            + Thread.currentThread().getName();
      } catch (Throwable var1) {
         return "context query failed: " + var1;
      }
   }

   private static void process(String var0) {
      try {
         synchronized (context) {
            File var2 = resolve();
            if (var2 == null) {
               return;
            }

            try (PrintWriter var3 = new PrintWriter(new FileWriter(var2, true))) {
               if (!output) {
                  output = true;
                  var3.println();
                  var3.println("=== session " + LocalTime.now().format(instance) + " | " + compute() + " ===");
               }

               var3.println(LocalTime.now().format(instance) + " " + var0);
               var3.flush();
            }
         }
      } catch (Throwable var10) {
      }
   }

   private static File resolve() {
      if (cache) {
         return state;
      }

      cache = true;

      try {
         MinecraftClient var0 = MinecraftClient.getInstance();
         File var1 = var0 != null && var0.runDirectory != null ? var0.runDirectory : new File(".");
         state = new File(var1, "wild-gl.log");
      } catch (Throwable var2) {
         state = null;
      }

      return state;
   }
}
