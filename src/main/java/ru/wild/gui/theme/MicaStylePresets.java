package ru.wild.gui.theme;

import java.util.Set;
import ru.wild.config.FoundryStorage;
import ru.wild.render.shader.DiffuseQuadShader;
import ru.wild.render.shader.ShaderBuildResult;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderGraphCompiler;
import ru.wild.render.shader.ShaderNodeRegistry;

public final class MicaStylePresets {
   private static boolean instance;
   private static final Set<String> data = Set.of(
      "Adaptive Mica Plate",
      "Velvet Module Card",
      "Nebula Panel Bloom",
      "Aurora Button Pulse",
      "Entity Aura Mask",
      "Holographic Nametag",
      "Trail Energy Ribbon",
      "Magnetic Rim Glow",
      "Pulse Health Ribbon",
      "Phase Chams Film",
      "Prism Sky Wash",
      "Menu Mica Backdrop",
      "Vivid Veil"
   );

   private MicaStylePresets() {
   }

   public static synchronized void handle(ShaderNodeRegistry var0, ShaderGraphCompiler var1) {
      if (!instance && var0 != null && var1 != null) {
         instance = true;
         FoundryStorage var2 = FoundryStorage.handle();
         var2.handle(var0);
         PresetManager.handle().handle(var1);
         DiffuseQuadShader.handle().handle(var1, var0);

         for (BuiltinThemePresets.PrimaryDataRecord var4 : BuiltinThemePresets.instance) {
            try {
               ShaderGraph var5 = BuiltinThemePresets.handle(var4, var0);
               if (var5 != null) {
                  var5.handle(var4.target().handle());
                  ShaderBuildResult var6 = var1.handle(var5);
                  if (!var6.ok()) {
                     System.out.println("[FoundryBootstrap] skipped failed preset " + var4.title() + ": " + var6.error());
                  } else {
                     PresetManager.handle().handle(var4.title(), var5, var6, PresetManager.PrimaryMode.PRESET);
                  }
               }
            } catch (Throwable var12) {
               System.out.println("[FoundryBootstrap] failed to publish preset " + var4.title() + ": " + var12.getMessage());
            }
         }

         for (SavedThemePreset var15 : var2.process()) {
            try {
               if (!handle(var15)) {
                  ShaderGraph var17 = var2.handle(var15.handle(), var0);
                  if (var17 != null) {
                     ShaderBuildResult var19 = var1.handle(var17);
                     if (!var19.ok()) {
                        System.out.println("[FoundryBootstrap] skipped failed slot " + var15.process() + ": " + var19.error());
                     } else {
                        PresetManager.handle().handle(var15.process(), var17, var19, process(var15));
                     }
                  }
               }
            } catch (Throwable var11) {
               System.out.println("[FoundryBootstrap] failed to publish " + var15.process() + ": " + var11.getMessage());
            }
         }

         for (LivePreviewRenderer var20 : LivePreviewRenderer.values()) {
            SavedThemePreset var7 = var2.compute(var20);
            if (var7 != null) {
               try {
                  ShaderGraph var8 = var2.handle(var7.handle(), var0);
                  if (var8 != null) {
                     var8.handle(var20.handle());
                     ShaderBuildResult var9 = var1.handle(var8);
                     if (!var9.ok()) {
                        System.out.println("[FoundryBootstrap] skipped failed bound target " + var20.handle() + ": " + var9.error());
                     } else {
                        PresetManager.handle().handle(var20, var8, var9);
                     }
                  }
               } catch (Throwable var10) {
                  System.out.println("[FoundryBootstrap] failed to publish " + var20.handle() + ": " + var10.getMessage());
               }
            }
         }
      }
   }

   public static Set<String> handle() {
      return data;
   }

   private static boolean handle(SavedThemePreset var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = PresetManager.onTick(var0.process());
      return data.contains(var1);
   }

   private static PresetManager.PrimaryMode process(SavedThemePreset var0) {
      if (var0 == null) {
         return PresetManager.PrimaryMode.USER;
      } else {
         String var1 = var0.prepare();
         if ("preset".equalsIgnoreCase(var1)) {
            return PresetManager.PrimaryMode.PRESET;
         } else {
            return !"imported".equalsIgnoreCase(var1) && !"shared".equalsIgnoreCase(var1) ? PresetManager.PrimaryMode.USER : PresetManager.PrimaryMode.IMPORTED;
         }
      }
   }
}
