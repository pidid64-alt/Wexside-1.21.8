package ru.wild.render;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public final class GlCompatibilityProbe {
   private static volatile Boolean data;
   private static volatile Boolean context;

   private GlCompatibilityProbe() {
   }

   public static boolean handle() {
      Boolean var0 = handle(System.getProperty("wild.render.weakGl"));
      if (var0 != null) {
         return var0;
      }

      if (data != null) {
         return data;
      }

      synchronized (GlCompatibilityProbe.class) {
         if (data != null) {
            return data;
         }

         data = compute();
         return data;
      }
   }
   public static boolean process() {
      if (context != null) {
         return context;
      }

      Class<GlCompatibilityProbe> var0 = GlCompatibilityProbe.class;
      synchronized (GlCompatibilityProbe.class){} // $VF: monitorenter 

      try {
         if (context != null) {
            boolean var8 = context;
            return var8;
         }

         if (GLFW.glfwGetCurrentContext() == 0L) {
            return false;
         }

         try {
            String var1 = compute(GL11.glGetString(7936));
            String var2 = compute(GL11.glGetString(7937));
            context = handle(var1, "amd")
               || handle(var1, "ati")
               || var1.contains("advanced micro devices")
               || handle(var2, "amd")
               || handle(var2, "ati")
               || var2.contains("radeon");
         } catch (Throwable var6) {
            context = false;
         }

         boolean var10000 = context;
         return var10000;
      } finally {
      }
   }

   private static boolean handle(String var0, String var1) {
      if (var0 != null && !var0.isEmpty()) {
         int var2 = 0;

         while (var2 <= var0.length() - var1.length()) {
            int var3 = var0.indexOf(var1, var2);
            if (var3 < 0) {
               return false;
            }

            int var4 = var3 + var1.length();
            boolean var5 = var3 == 0 || !Character.isLetterOrDigit(var0.charAt(var3 - 1));
            boolean var6 = var4 >= var0.length() || !Character.isLetterOrDigit(var0.charAt(var4));
            if (var5 && var6) {
               return true;
            }

            var2 = var3 + 1;
         }

         return false;
      } else {
         return false;
      }
   }

   private static Boolean handle(String var0) {
      if (var0 != null && !var0.isBlank()) {
         return switch (var0.trim().toLowerCase()) {
            case "true", "1", "yes", "on" -> true;
            case "false", "0", "no", "off" -> false;
            default -> null;
         };
      } else {
         return null;
      }
   }

   private static boolean compute() {
      if (GLFW.glfwGetCurrentContext() == 0L) {
         return false;
      }

      try {
         String var0 = compute(GL11.glGetString(7936));
         String var1 = compute(GL11.glGetString(7937));
         String var2 = GL11.glGetString(7938);
         float var3 = process(var2);
         return !var0.contains("intel") && !var1.contains("intel") && !var1.contains("hd graphics") ? var3 > 0.0F && var3 < 4.0F : true;
      } catch (Throwable var4) {
         return false;
      }
   }

   private static float process(String var0) {
      if (var0 != null && !var0.isBlank()) {
         int var1 = var0.indexOf(32);
         String var2 = var1 >= 0 ? var0.substring(0, var1) : var0;
         int var3 = var2.indexOf(46);
         if (var3 <= 0) {
            return 0.0F;
         }

         try {
            return Float.parseFloat(var2.substring(0, var3));
         } catch (NumberFormatException var5) {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private static String compute(String var0) {
      return var0 == null ? "" : var0.toLowerCase();
   }
}
