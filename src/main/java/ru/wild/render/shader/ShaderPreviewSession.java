package ru.wild.render.shader;

import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemeColors;

public final class ShaderPreviewSession implements AutoCloseable {
   private final ShaderGraphCompiler instance;
   private LivePreviewRenderer data = LivePreviewRenderer.PREVIEW_ONLY;
   private String context = "";
   private String config = "";

   public ShaderPreviewSession(ShaderGraphCompiler var1) {
      this.instance = var1;
      PresetManager.handle().handle(var1);
   }

   public LivePreviewRenderer handle() {
      return this.data;
   }

   public void handle(LivePreviewRenderer var1) {
      if (var1 != null) {
         this.data = var1;
      }
   }

   public void handle(ShaderGraph var1) {
      if (var1 != null && this.instance != null) {
         ShaderBuildResult var2 = this.instance.handle(var1);
         this.context = var2.hash();
         this.config = var2.error() == null ? "" : var2.error();
         PresetManager.handle().handle(this.data, var1, var2);
         ThemeShaderProgramCache.handle().handle(this.data, var2);
      }
   }

   public ShaderBuildResult process(ShaderGraph var1) {
      if (var1 != null && this.instance != null) {
         ShaderBuildResult var2 = this.instance.handle(var1);
         this.context = var2.hash();
         this.config = var2.error() == null ? "" : var2.error();
         return var2;
      } else {
         return null;
      }
   }

   public boolean handle(String var1, ShaderGraph var2) {
      if (var2 != null && this.instance != null) {
         String var3 = PresetManager.onTick(var1);
         if (var3.isBlank()) {
            this.config = "Shader name is empty";
            return false;
         }

         ShaderBuildResult var4 = this.instance.handle(var2);
         this.context = var4.hash();
         this.config = var4.error() == null ? "" : var4.error();
         if (!this.config.isBlank()) {
            return false;
         }

         PresetManager.handle().handle(var3, var2, var4);
         ThemeShaderProgramCache.handle().handle(var3, var4);
         return true;
      } else {
         return false;
      }
   }

   public void handle(
      ShaderGraph var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, ThemeColors var10, float var11
   ) {
      if (var1 != null && var10 != null) {
         this.handle(var1);
         DiffuseShaderRenderer.handle(this.data, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   public String process() {
      return !this.config.isBlank() ? this.config : ThemeShaderProgramCache.handle().handle(this.data);
   }

   public String compute() {
      return this.context == null ? "" : this.context;
   }

   @Override
   public void close() {
      this.context = "";
      this.config = "";
   }
}
