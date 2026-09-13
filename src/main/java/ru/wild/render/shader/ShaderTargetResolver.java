package ru.wild.render.shader;

import java.util.ArrayList;
import java.util.List;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemeColors;

public final class ShaderTargetResolver {
   private ShaderTargetResolver() {
   }

   public static boolean handle(String var0) {
      return PresetManager.handle().update(var0);
   }

   public static boolean handle(LivePreviewRenderer var0) {
      return PresetManager.handle().update(var0);
   }

   public static boolean handle(
      String var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, ThemeColors var9, float var10
   ) {
      return DiffuseShaderRenderer.handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static boolean handle(
      LivePreviewRenderer var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, ThemeColors var9, float var10
   ) {
      return DiffuseShaderRenderer.handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static boolean handle(
      String var0, int var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, ThemeColors var10, float var11
   ) {
      return DiffuseShaderRenderer.handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static String process(String var0) {
      return ThemeShaderProgramCache.handle().handle(var0);
   }

   public static String process(LivePreviewRenderer var0) {
      return ThemeShaderProgramCache.handle().handle(var0);
   }

   public static String compute(String var0) {
      ShaderBuildResult var1 = PresetManager.handle().process(var0);
      return var1 != null && var1.hash() != null ? var1.hash() : ThemeShaderProgramCache.handle().process(var0);
   }

   public static String compute(LivePreviewRenderer var0) {
      ShaderBuildResult var1 = PresetManager.handle().process(var0);
      return var1 != null && var1.hash() != null ? var1.hash() : ThemeShaderProgramCache.handle().process(var0);
   }

   public static ShaderBuildReporter resolve(String var0) {
      String var1 = PresetManager.onTick(var0);
      if (var1.isBlank()) {
         return null;
      }

      ShaderBuildResult var2 = PresetManager.handle().process(var1);
      return var2 == null ? null : ThemeShaderProgramCache.handle().handle(var1, var2);
   }

   public static ShaderBuildReporter resolve(LivePreviewRenderer var0) {
      if (var0 == null) {
         return null;
      }

      ShaderBuildResult var1 = PresetManager.handle().process(var0);
      return var1 == null ? null : ThemeShaderProgramCache.handle().handle(var0, var1);
   }

   public static List<ShaderParameter> update(String var0) {
      return PresetManager.handle().prepare(var0);
   }

   public static List<ShaderParameter> update(LivePreviewRenderer var0) {
      return PresetManager.handle().execute(var0);
   }

   public static List<Setting> apply(String var0) {
      return handle(PresetManager.handle().prepare(var0));
   }

   public static List<Setting> apply(LivePreviewRenderer var0) {
      return handle(PresetManager.handle().execute(var0));
   }

   public static void handle(String var0, String var1, float var2) {
      PresetManager.handle().handle(var0, var1, var2);
   }

   public static void handle(LivePreviewRenderer var0, String var1, float var2) {
      PresetManager.handle().handle(var0, var1, var2);
   }

   public static void handle(String var0, String var1, int var2) {
      PresetManager.handle().handle(var0, var1, var2);
   }

   public static void handle(LivePreviewRenderer var0, String var1, int var2) {
      PresetManager.handle().handle(var0, var1, var2);
   }

   public static void handle(String var0, List<Setting> var1) {
      if (var1 != null) {
         for (Setting var3 : var1) {
            if (var3 instanceof NumberSetting var4) {
               handle(var0, var4.instance, var4.config);
            } else if (var3 instanceof ColorSetting var5) {
               handle(var0, var5.instance, var5.check());
            }
         }
      }
   }

   public static void handle(LivePreviewRenderer var0, List<Setting> var1) {
      if (var1 != null) {
         for (Setting var3 : var1) {
            if (var3 instanceof NumberSetting var4) {
               handle(var0, var4.instance, var4.config);
            } else if (var3 instanceof ColorSetting var5) {
               handle(var0, var5.instance, var5.check());
            }
         }
      }
   }

   public static void execute(String var0) {
      PresetManager.handle().handle(var0);
      ThemeShaderProgramCache.handle().compute(var0);
   }

   public static void execute(LivePreviewRenderer var0) {
      PresetManager.handle().handle(var0);
      ThemeShaderProgramCache.handle().compute(var0);
   }

   public static void handle() {
      ThemeShaderProgramCache.handle().resolve();
   }

   private static List<Setting> handle(List<ShaderParameter> var0) {
      ArrayList var1 = new ArrayList();
      if (var0 == null) {
         return var1;
      }

      for (ShaderParameter var3 : var0) {
         if (var3.kind() == ShaderParameter.Mode.FLOAT) {
            var1.add(new NumberSetting(var3.name(), var3.defaultFloat(), var3.minimum(), var3.maximum(), var3.increment(), false));
         } else {
            var1.add(new ColorSetting(var3.name(), var3.defaultRgba()));
         }
      }

      return var1;
   }
}
