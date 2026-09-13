package ru.wild.render.shader;

import java.util.List;
import java.util.Objects;

public final class ShaderNodeDefinition {
   private final String instance;
   private final String data;
   private final String context;
   private final float config;
   private final List<ShaderPinDefinition> state;
   private final List<ShaderPinDefinition> cache;
   private final ShaderExpressionEmitter output;
   private final boolean current;

   public ShaderNodeDefinition(
      String var1, String var2, String var3, float var4, List<ShaderPinDefinition> var5, List<ShaderPinDefinition> var6, ShaderExpressionEmitter var7
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, handle(var3, var6));
   }

   public ShaderNodeDefinition(
      String var1,
      String var2,
      String var3,
      float var4,
      List<ShaderPinDefinition> var5,
      List<ShaderPinDefinition> var6,
      ShaderExpressionEmitter var7,
      boolean var8
   ) {
      this.instance = Objects.requireNonNull(var1, "id");
      this.data = Objects.requireNonNull(var2, "title");
      this.context = Objects.requireNonNull(var3, "category");
      this.config = Math.max(132.0F, var4);
      this.state = List.copyOf(var5);
      this.cache = List.copyOf(var6);
      this.output = Objects.requireNonNull(var7, "emitter");
      this.current = var8;
   }

   public String handle() {
      return this.instance;
   }

   public String process() {
      return this.data;
   }

   public String compute() {
      return this.context;
   }

   public float resolve() {
      return this.config;
   }

   public List<ShaderPinDefinition> update() {
      return this.state;
   }

   public List<ShaderPinDefinition> apply() {
      return this.cache;
   }

   public ShaderExpressionEmitter execute() {
      return this.output;
   }

   public boolean prepare() {
      return this.current;
   }

   public ShaderPinDefinition handle(String var1) {
      for (ShaderPinDefinition var3 : this.state) {
         if (var3.id().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public ShaderPinDefinition process(String var1) {
      for (ShaderPinDefinition var3 : this.cache) {
         if (var3.id().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   private static boolean handle(String var0, List<ShaderPinDefinition> var1) {
      return var1 != null && !var1.isEmpty();
   }
}
