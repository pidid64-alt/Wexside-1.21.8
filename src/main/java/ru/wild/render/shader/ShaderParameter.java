package ru.wild.render.shader;

import java.util.Arrays;

public record ShaderParameter(String name, String uniformName, ShaderParameter.Mode kind, float[] defaults, float minimum, float maximum, float increment) {
   public ShaderParameter(String name, String uniformName, ShaderParameter.Mode kind, float[] defaults, float minimum, float maximum, float increment) {
      name = name != null && !name.isBlank() ? name.trim() : "Value";
      uniformName = uniformName != null && !uniformName.isBlank() ? uniformName.trim() : "u_Value";
      defaults = defaults == null ? new float[]{0.0F, 0.0F, 0.0F, 1.0F} : Arrays.copyOf(defaults, Math.max(4, defaults.length));
      if (!Float.isFinite(minimum)) {
         minimum = 0.0F;
      }

      if (!Float.isFinite(maximum) || maximum <= minimum) {
         maximum = minimum + 1.0F;
      }

      if (!Float.isFinite(increment) || increment <= 0.0F) {
         increment = 0.01F;
      }

      this.name = name;
      this.uniformName = uniformName;
      this.kind = kind;
      this.defaults = defaults;
      this.minimum = minimum;
      this.maximum = maximum;
      this.increment = increment;
   }

   public float defaultFloat() {
      return this.defaults[0];
   }

   public int defaultRgba() {
      int var1 = channel(this.defaults[0]);
      int var2 = channel(this.defaults[1]);
      int var3 = channel(this.defaults[2]);
      int var4 = channel(this.defaults[3]);
      return var4 << 24 | var1 << 16 | var2 << 8 | var3;
   }

   private static int channel(float var0) {
      return !Float.isFinite(var0) ? 0 : Math.max(0, Math.min(255, Math.round(var0 * 255.0F)));
   }

   public enum Mode {
      FLOAT,
      COLOR;
   }
}
