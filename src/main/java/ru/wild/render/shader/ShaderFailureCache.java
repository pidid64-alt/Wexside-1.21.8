package ru.wild.render.shader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.lwjgl.opengl.GL20;
import ru.wild.render.GlBreadcrumbLogger;

public final class ShaderFailureCache implements AutoCloseable {
   private final Map<String, ShaderFailureCache.ShaderState> instance = new LinkedHashMap<>();
   private final Map<String, String> data = new HashMap<>();

   public ShaderFailureCache.ShaderState handle(String var1, String var2, String var3) {
      ShaderFailureCache.ShaderState var4 = this.instance.get(var1);
      if (var4 != null) {
         return var4;
      }

      String var5 = this.data.get(var1);
      if (var5 != null) {
         throw new IllegalStateException("Shader '" + var1 + "' previously failed: " + var5);
      }

      try {
         ShaderFailureCache.ShaderState var6 = new ShaderFailureCache.ShaderState(ShaderBuildReporter.handle(var2, var3));
         this.instance.put(var1, var6);
         return var6;
      } catch (Throwable var8) {
         this.data.put(var1, String.valueOf(var8.getMessage()));
         GlBreadcrumbLogger.process("shader-manager", "cached failure for '" + var1 + "', no further GL attempts: " + var8);
         throw var8 instanceof RuntimeException var7 ? var7 : new IllegalStateException("Shader '" + var1 + "' failed", var8);
      }
   }

   public ShaderFailureCache.ShaderState process(String var1, String var2, String var3) {
      ShaderFailureCache.ShaderState var4 = this.instance.get(var1);
      if (var4 != null) {
         return var4;
      }

      if (this.data.containsKey(var1)) {
         return null;
      }

      try {
         return this.handle(var1, var2, var3);
      } catch (Throwable var6) {
         return null;
      }
   }

   @Override
   public void close() {
      for (ShaderFailureCache.ShaderState var2 : this.instance.values()) {
         var2.close();
      }

      this.instance.clear();
      this.data.clear();
   }

   public void handle() {
   }

   static final class CacheEntry {
      final int instance;
      private int data = -1;
      private int context;
      private float config;
      private float state;
      private float cache;
      private float output;

      CacheEntry(int var1) {
         this.instance = var1;
      }

      boolean handle(int var1, int var2, float var3, float var4, float var5, float var6) {
         if (this.data == var1
            && this.context == var2
            && Float.floatToIntBits(this.config) == Float.floatToIntBits(var3)
            && Float.floatToIntBits(this.state) == Float.floatToIntBits(var4)
            && Float.floatToIntBits(this.cache) == Float.floatToIntBits(var5)
            && Float.floatToIntBits(this.output) == Float.floatToIntBits(var6)) {
            return false;
         }

         this.data = var1;
         this.context = var2;
         this.config = var3;
         this.state = var4;
         this.cache = var5;
         this.output = var6;
         return true;
      }
   }

   public static final class ShaderState implements AutoCloseable {
      private final ShaderBuildReporter instance;
      private final Map<String, ShaderFailureCache.CacheEntry> data = new HashMap<>();

      ShaderState(ShaderBuildReporter var1) {
         this.instance = var1;
      }

      public void handle() {
         GL20.glUseProgram(this.instance.compute());
      }

      public void handle(String var1, int var2) {
         ShaderFailureCache.CacheEntry var3 = this.handle(var1);
         if (var3.instance >= 0 && var3.handle(0, var2, 0.0F, 0.0F, 0.0F, 0.0F)) {
            GL20.glUniform1i(var3.instance, var2);
         }
      }

      public void handle(String var1, float var2) {
         ShaderFailureCache.CacheEntry var3 = this.handle(var1);
         if (var3.instance >= 0 && var3.handle(1, 0, var2, 0.0F, 0.0F, 0.0F)) {
            GL20.glUniform1f(var3.instance, var2);
         }
      }

      public void handle(String var1, float var2, float var3) {
         ShaderFailureCache.CacheEntry var4 = this.handle(var1);
         if (var4.instance >= 0 && var4.handle(2, 0, var2, var3, 0.0F, 0.0F)) {
            GL20.glUniform2f(var4.instance, var2, var3);
         }
      }

      public void handle(String var1, float var2, float var3, float var4) {
         ShaderFailureCache.CacheEntry var5 = this.handle(var1);
         if (var5.instance >= 0 && var5.handle(3, 0, var2, var3, var4, 0.0F)) {
            GL20.glUniform3f(var5.instance, var2, var3, var4);
         }
      }

      public void handle(String var1, float var2, float var3, float var4, float var5) {
         ShaderFailureCache.CacheEntry var6 = this.handle(var1);
         if (var6.instance >= 0 && var6.handle(4, 0, var2, var3, var4, var5)) {
            GL20.glUniform4f(var6.instance, var2, var3, var4, var5);
         }
      }

      private ShaderFailureCache.CacheEntry handle(String var1) {
         ShaderFailureCache.CacheEntry var2 = this.data.get(var1);
         if (var2 == null) {
            var2 = new ShaderFailureCache.CacheEntry(this.instance.handle(var1));
            this.data.put(var1, var2);
         }

         return var2;
      }

      @Override
      public void close() {
         this.instance.process();
         this.data.clear();
      }
   }
}
