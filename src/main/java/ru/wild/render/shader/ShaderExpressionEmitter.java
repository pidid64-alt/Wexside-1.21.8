package ru.wild.render.shader;

@FunctionalInterface
public interface ShaderExpressionEmitter {
   String emit(ShaderExpressionResolver var1, ShaderGraphBlock var2, String var3);
}
