package ru.wild.render.shader;

import ru.wild.gui.theme.BuiltinThemePresets;

public final class FoundryPresetFactory {
   private FoundryPresetFactory() {
   }

   public static ShaderGraph handle(ShaderNodeRegistry var0) {
      return BuiltinThemePresets.handle(var0);
   }
}
