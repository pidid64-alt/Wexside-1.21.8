package ru.wild.render.shader;

import java.util.List;

public record ShaderBuildResult(String fragmentSource, String hash, String error, List<ShaderParameter> exposedUniforms) {
   public ShaderBuildResult(String fragmentSource, String hash, String error, List<ShaderParameter> exposedUniforms) {
      exposedUniforms = exposedUniforms == null ? List.of() : List.copyOf(exposedUniforms);
      this.fragmentSource = fragmentSource;
      this.hash = hash;
      this.error = error;
      this.exposedUniforms = exposedUniforms;
   }

   public ShaderBuildResult(String var1, String var2, String var3) {
      this(var1, var2, var3, List.of());
   }

   public boolean ok() {
      return this.error == null || this.error.isBlank();
   }
}
