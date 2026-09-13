package ru.wild.gui.theme;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderNodeRegistry;

public final class FoundryTemplateRegistry {
   public static final List<FoundryTemplateRegistry.CacheEntry> instance = new ArrayList<>();

   private static boolean handle(LivePreviewRenderer var0) {
      for (LivePreviewRenderer var4 : LivePreviewRenderer.tick()) {
         if (var4 == var0) {
            return true;
         }
      }

      return false;
   }

   private FoundryTemplateRegistry() {
   }

   public static ShaderGraph handle(FoundryTemplateRegistry.CacheEntry var0, ShaderNodeRegistry var1) {
      return var0.cache.apply(var1);
   }

   static {
      for (BuiltinThemePresets.PrimaryDataRecord var1 : BuiltinThemePresets.instance) {
         if (handle(var1.target())) {
            instance.add(
               new FoundryTemplateRegistry.CacheEntry(var1.title(), var1.description(), var1.target(), var1.complexity(), var1.nodes(), var1.builder())
            );
         }
      }
   }

   public static final class CacheEntry {
      public final String instance;
      public final String data;
      public final LivePreviewRenderer context;
      public final String config;
      public final List<String> state;
      final Function<ShaderNodeRegistry, ShaderGraph> cache;

      public CacheEntry(String var1, String var2, LivePreviewRenderer var3, String var4, List<String> var5, Function<ShaderNodeRegistry, ShaderGraph> var6) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4 != null && !var4.isBlank() ? var4 : "Custom";
         this.state = var5 == null ? List.of() : List.copyOf(var5);
         this.cache = var6;
      }
   }
}
