package ru.wild.render.shader;

public record ShaderPinDefinition(String id, String label, ShaderValueType type, ShaderPinDirection direction, String defaultExpression) {
   public static ShaderPinDefinition input(String var0, String var1, ShaderValueType var2, String var3) {
      return new ShaderPinDefinition(var0, var1, var2, ShaderPinDirection.INPUT, var3);
   }

   public static ShaderPinDefinition output(String var0, String var1, ShaderValueType var2) {
      return new ShaderPinDefinition(var0, var1, var2, ShaderPinDirection.OUTPUT, "");
   }
}
