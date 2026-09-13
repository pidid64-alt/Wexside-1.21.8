package ru.wild.render.shader;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import ru.wild.render.GlBreadcrumbLogger;
import ru.wild.util.io.ResourceReader;

public final class ShaderBuildReporter {
   private static final int instance = 262144;
   private final int data;

   public ShaderBuildReporter(String var1, String var2) {
      this(var1, var2, "inline");
   }

   public ShaderBuildReporter(String var1, String var2, String var3) {
      process(var3);
      GlBreadcrumbLogger.handle("shader build " + var3);
      int var4 = handle(35633, var1, var3 + ":vertex");

      int var5;
      try {
         var5 = handle(35632, var2, var3 + ":fragment");
      } catch (RuntimeException var10) {
         GL20.glDeleteShader(var4);
         throw var10;
      }

      this.data = GL20.glCreateProgram();
      if (this.data == 0) {
         GL20.glDeleteShader(var4);
         GL20.glDeleteShader(var5);
         throw new IllegalStateException("glCreateProgram returned 0 for " + var3 + " (" + GlBreadcrumbLogger.compute() + ")");
      }

      GL20.glAttachShader(this.data, var4);
      GL20.glAttachShader(this.data, var5);
      GL20.glLinkProgram(this.data);
      MemoryStack var6 = MemoryStack.stackPush();

      try {
         IntBuffer var7 = var6.mallocInt(1);
         GL20.glGetProgramiv(this.data, 35714, var7);
         if (var7.get(0) == 0) {
            String var8 = GL20.glGetProgramInfoLog(this.data);
            GL20.glDeleteShader(var4);
            GL20.glDeleteShader(var5);
            GL20.glDeleteProgram(this.data);
            GlBreadcrumbLogger.process("shader", "link failed " + var3 + ": " + var8);
            throw new IllegalStateException("Program link failed (" + var3 + "): " + var8);
         }
      } catch (Throwable var11) {
         if (var6 != null) {
            try {
               var6.close();
            } catch (Throwable var9) {
               var11.addSuppressed(var9);
            }
         }

         throw var11;
      }

      if (var6 != null) {
         var6.close();
      }

      GL20.glDetachShader(this.data, var4);
      GL20.glDetachShader(this.data, var5);
      GL20.glDeleteShader(var4);
      GL20.glDeleteShader(var5);
      GlBreadcrumbLogger.handle("shader", "built " + var3 + " id=" + this.data);
   }

   public static ShaderBuildReporter handle(String var0, String var1) {
      String var2 = ResourceReader.handle(var0);
      String var3 = ResourceReader.handle(var1);
      return new ShaderBuildReporter(var2, var3, var0 + " + " + var1);
   }

   private static void process(String var0) {
      if (!GlBreadcrumbLogger.process()) {
         GlBreadcrumbLogger.process("shader", "no GL context for " + var0 + " thread=" + Thread.currentThread().getName());
         throw new IllegalStateException("No current GL context while building " + var0 + " on thread " + Thread.currentThread().getName());
      }
   }

   private static int handle(int var0, String var1, String var2) {
      String var3 = var0 == 35633 ? "vertex" : "fragment";
      if (var1 != null && !var1.isBlank()) {
         if (var1.length() > 262144) {
            GlBreadcrumbLogger.process("shader", "oversized source " + var2 + " chars=" + var1.length());
            throw new IllegalStateException("Shader source too large for " + var2 + ": " + var1.length());
         }

         GlBreadcrumbLogger.handle("shader", "compiling " + var2 + " chars=" + var1.length());
         int var4 = GL20.glCreateShader(var0);
         if (var4 == 0) {
            int var11 = GL11.glGetError();
            GlBreadcrumbLogger.process(
               "shader", "glCreateShader returned 0 for " + var2 + " glError=" + GlBreadcrumbLogger.handle(var11) + " " + GlBreadcrumbLogger.compute()
            );
            throw new IllegalStateException("glCreateShader returned 0 for " + var2 + " (glError=" + GlBreadcrumbLogger.handle(var11) + ")");
         }

         GL20.glShaderSource(var4, var1);
         int var5 = GL11.glGetError();
         if (var5 != 0) {
            GL20.glDeleteShader(var4);
            GlBreadcrumbLogger.process("shader", "glShaderSource failed " + var2 + " glError=" + GlBreadcrumbLogger.handle(var5));
            throw new IllegalStateException("glShaderSource failed for " + var2 + " (glError=" + GlBreadcrumbLogger.handle(var5) + ")");
         }

         GL20.glCompileShader(var4);
         MemoryStack var6 = MemoryStack.stackPush();

         try {
            IntBuffer var7 = var6.mallocInt(1);
            GL20.glGetShaderiv(var4, 35713, var7);
            if (var7.get(0) == 0) {
               String var8 = GL20.glGetShaderInfoLog(var4);
               GL20.glDeleteShader(var4);
               GlBreadcrumbLogger.process("shader", "compile failed " + var2 + ": " + var8);
               throw new IllegalStateException("Shader compile failed (" + var2 + "): " + var8);
            }
         } catch (Throwable var10) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (var6 != null) {
            var6.close();
         }

         return var4;
      } else {
         GlBreadcrumbLogger.process("shader", "empty source " + var2);
         throw new IllegalStateException("Empty " + var3 + " source for " + var2);
      }
   }

   public void handle() {
      GL20.glUseProgram(this.data);
   }

   public void process() {
      GL20.glDeleteProgram(this.data);
   }

   public int compute() {
      return this.data;
   }

   public int handle(String var1) {
      return GL20.glGetUniformLocation(this.data, var1);
   }
}
