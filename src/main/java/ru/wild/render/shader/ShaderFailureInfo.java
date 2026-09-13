package ru.wild.render.shader;

import java.util.Locale;
import ru.wild.render.GlErrorNames;

public final class ShaderFailureInfo {
   public static final int instance = 3;
   public static final int data = 14;
   private static final String context = "none";
   private static final String config = "uniform";
   private static final String state = "varying";
   private static final String cache = "link";
   private static final String output = "compile";
   private static final String current = "sampler";
   private static final String active = "bind";

   private ShaderFailureInfo() {
   }

   public static String handle(String var0, int var1) {
      return "SHADER FAILURE #" + var1 + " stage=" + compute(var0, 96);
   }

   public static String handle(int var0, Throwable var1) {
      return var1 == null ? "cause[" + var0 + "]=unknown" : "cause[" + var0 + "]=" + var1.getClass().getName();
   }

   public static String handle(Throwable var0) {
      return var0 == null ? "message=no throwable" : "message=" + compute(var0.getMessage(), 260);
   }

   public static String process(Throwable var0) {
      String var1 = compute(var0).toLowerCase(Locale.ROOT);
      if (var1.contains("uniform")) {
         return "GLSL DETAIL broken uniform binding/type; check declared name, std140 layout and Java upload type";
      } else if (var1.contains("varying") || var1.contains("in/out")) {
         return "GLSL DETAIL varying mismatch; check vertex output and fragment input names/types";
      } else if (var1.contains("link")) {
         return "GLSL DETAIL program link failed; inspect attached shader interface and sampler layout";
      } else if (var1.contains("compile")) {
         return "GLSL DETAIL shader compile failed; inspect syntax, version and include expansion";
      } else {
         return !var1.contains("sampler") && !var1.contains("bind")
            ? "none"
            : "GLSL DETAIL sampler/binding failure; check texture view lifetime and texture unit isolation";
      }
   }

   public static String handle(StackTraceElement var0) {
      return var0 == null ? "none" : "  at " + var0.getClassName() + "." + var0.getMethodName() + "(" + var0.getFileName() + ":" + var0.getLineNumber() + ")";
   }

   public static String process(String var0, int var1) {
      return "OPENGL ERROR stage=" + compute(var0, 96) + " code=0x" + Integer.toHexString(var1).toUpperCase(Locale.ROOT) + " name=" + GlErrorNames.handle(var1);
   }

   public static String handle() {
      return "GL STATE program=" + GlErrorNames.handle() + " activeTexture=" + GlErrorNames.process() + " texture2D=" + GlErrorNames.compute();
   }

   private static String compute(Throwable var0) {
      if (var0 == null) {
         return "none";
      }

      String var1 = var0.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : var0.getClass().getName();
   }

   private static String compute(String var0, int var1) {
      if (var0 != null && !var0.isBlank()) {
         String var2 = var0.replace('\n', ' ').replace('\r', ' ').trim();
         return var2.length() <= var1 ? var2 : var2.substring(0, Math.max(0, var1 - 3)) + "...";
      } else {
         return "none";
      }
   }
}
