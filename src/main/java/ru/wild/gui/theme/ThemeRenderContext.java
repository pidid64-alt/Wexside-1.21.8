package ru.wild.gui.theme;
import ru.wild.gui.screen.GuiMetrics;

public final class ThemeRenderContext {
   private final ThemePalette instance;
   private final GuiMetrics data;
   private final ThemeColors context;
   private final ThemePaletteRegistry config;

   public boolean handle() {
      if (this.context != null) {
         return this.context.unload();
      } else {
         return this.config == null ? false : this.config.compute(this.instance);
      }
   }

   public boolean process() {
      return this.handle();
   }
   ThemeRenderContext(ThemePalette var1, GuiMetrics var2, ThemeColors var3, ThemePaletteRegistry var4) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
   }
   public static ThemeRenderContext.ColorState compute() {
      return new ThemeRenderContext.ColorState();
   }
   public ThemePalette resolve() {
      return this.instance;
   }
   public GuiMetrics update() {
      return this.data;
   }
   public ThemeColors apply() {
      return this.context;
   }
   public ThemePaletteRegistry execute() {
      return this.config;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ThemeRenderContext var2)) {
         return false;
      } else {
         ThemePalette var3 = this.resolve();
         ThemePalette var4 = var2.resolve();
         if (var3 == null ? var4 == null : var3.equals(var4)) {
            GuiMetrics var5 = this.update();
            GuiMetrics var6 = var2.update();
            if (var5 == null ? var6 == null : var5.equals(var6)) {
               ThemeColors var7 = this.apply();
               ThemeColors var8 = var2.apply();
               if (var7 == null ? var8 == null : var7.equals(var8)) {
                  ThemePaletteRegistry var9 = this.execute();
                  ThemePaletteRegistry var10 = var2.execute();
                  return var9 == null ? var10 == null : var9.equals(var10);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ThemePalette var3 = this.resolve();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      GuiMetrics var4 = this.update();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      ThemeColors var5 = this.apply();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      ThemePaletteRegistry var6 = this.execute();
      return var2 * 59 + (var6 == null ? 43 : var6.hashCode());
   }
   @Override
   public String toString() {
      return "ThemeContext(theme=" + this.resolve() + ", metrics=" + this.update() + ", colors=" + this.apply() + ", palette=" + this.execute() + ")";
   }
   public static class ColorState {
      private ThemePalette instance;
      private GuiMetrics data;
      private ThemeColors context;
      private ThemePaletteRegistry config;
      ColorState() {
      }
      public ThemeRenderContext.ColorState handle(ThemePalette var1) {
         this.instance = var1;
         return this;
      }
      public ThemeRenderContext.ColorState handle(GuiMetrics var1) {
         this.data = var1;
         return this;
      }
      public ThemeRenderContext.ColorState handle(ThemeColors var1) {
         this.context = var1;
         return this;
      }
      public ThemeRenderContext.ColorState handle(ThemePaletteRegistry var1) {
         this.config = var1;
         return this;
      }
      public ThemeRenderContext handle() {
         return new ThemeRenderContext(this.instance, this.data, this.context, this.config);
      }
      @Override
      public String toString() {
         return "ThemeContext.ThemeContextBuilder(theme="
            + this.instance
            + ", metrics="
            + this.data
            + ", colors="
            + this.context
            + ", palette="
            + this.config
            + ")";
      }
   }
}
