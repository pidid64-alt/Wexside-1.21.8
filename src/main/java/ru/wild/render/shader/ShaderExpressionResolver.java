package ru.wild.render.shader;

import java.util.Locale;
import java.util.Map;
import ru.wild.gui.theme.LivePreviewRenderer;

public final class ShaderExpressionResolver {
   private final ShaderGraph instance;
   private final ShaderNodeRegistry data;
   private final Map<String, String> context;
   private final Map<String, String> config;
   private final LivePreviewRenderer state;

   ShaderExpressionResolver(ShaderGraph var1, ShaderNodeRegistry var2, Map<String, String> var3, Map<String, String> var4, LivePreviewRenderer var5) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5 == null ? LivePreviewRenderer.PREVIEW_ONLY : var5.resolve();
   }

   public String handle(ShaderGraphBlock var1, String var2) {
      ShaderGraphLink var3 = this.instance.process(var1.handle(), var2);
      if (var3 != null) {
         String var4 = this.context.get(var3.update());
         if (var4 != null) {
            return var4;
         }
      }

      ShaderNodeDefinition var6 = this.data.handle(var1.process());
      if (var6 != null) {
         ShaderPinDefinition var5 = var6.handle(var2);
         if (var5 != null && var5.defaultExpression() != null && !var5.defaultExpression().isBlank()) {
            return var5.defaultExpression();
         }
      }

      return "0.0";
   }

   public String handle(float var1) {
      if (!Float.isFinite(var1)) {
         return "0.0";
      }

      String var2 = String.format(Locale.ROOT, "%.6f", var1);

      while (var2.contains(".") && var2.endsWith("0")) {
         var2 = var2.substring(0, var2.length() - 1);
      }

      if (var2.endsWith(".")) {
         var2 = var2 + "0";
      }

      return var2;
   }

   public String process(ShaderGraphBlock var1, String var2) {
      return "n_" + handle(var1.handle()) + "_" + handle(var2);
   }

   public String handle(ShaderGraphBlock var1) {
      return var1 == null ? "u_Value" : this.config.getOrDefault(var1.handle(), "u_" + handle(var1.handle("name", "Value")));
   }

   public LivePreviewRenderer handle() {
      return this.state;
   }

   public boolean process() {
      return this.state == LivePreviewRenderer.HUD;
   }

   private static String handle(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.replaceAll("[^A-Za-z0-9_]", "_");
         return Character.isDigit(var1.charAt(0)) ? "_" + var1 : var1;
      } else {
         return "x";
      }
   }
}
