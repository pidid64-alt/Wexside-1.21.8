package ru.wild.gui.screen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ru.wild.core.AnimationClock;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.RectBounds;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.render.RoundedRectRenderer;

public final class ShaderImportBrowser {
   private static final SimpleDateFormat instance = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);
   private final SpringFloat data = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.5F, 0.82F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private final SpringFloat context = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.2F, 0.86F), 0.0F, 0.0F, 100000.0F, 0.01F, 0.01F);
   private final List<File> config = new ArrayList<>();
   private boolean state;
   private String cache = "";
   private int output = -1;
   private int current = -1;
   private float active;
   private File mode;

   public void handle(List<File> var1) {
      this.config.clear();
      if (var1 != null) {
         this.config.addAll(var1);
      }

      this.state = true;
      this.cache = "";
      this.output = -1;
      this.current = this.config.isEmpty() ? -1 : 0;
      this.mode = null;
      this.active = 0.0F;
      this.context.process(0.0F);
      this.data.compute(1.0F);
   }

   public void handle() {
      this.state = false;
      this.data.compute(0.0F);
   }

   public boolean process() {
      return this.state;
   }

   public File compute() {
      File var1 = this.mode;
      this.mode = null;
      return var1;
   }

   public boolean handle(float var1, float var2, int var3, GuiMetrics var4, int var5, int var6) {
      if (!this.state) {
         return false;
      }

      if (var3 != 0) {
         return true;
      }

      RectBounds var7 = this.handle(var4, var5, var6);
      RectBounds var8 = this.compute(var7, var4);
      RectBounds var9 = this.resolve(var7, var4);
      if (!var9.contains(var1, var2) && var7.contains(var1, var2)) {
         List var10 = this.resolve();
         if (var8.contains(var1, var2)) {
            if (this.current >= 0 && this.current < var10.size()) {
               this.mode = (File)var10.get(this.current);
               this.handle();
            }

            return true;
         } else {
            RectBounds var11 = this.process(var7, var4);
            if (var11.contains(var1, var2)) {
               float var12 = var4.handle(42.0F);
               int var13 = (int)Math.floor((var2 - var11.y() + this.context.handle()) / var12);
               if (var13 >= 0 && var13 < var10.size()) {
                  this.current = var13;
               }

               return true;
            } else {
               return true;
            }
         }
      } else {
         this.handle();
         return true;
      }
   }

   public boolean handle(double var1, GuiMetrics var3, int var4, int var5) {
      if (!this.state) {
         return false;
      }

      RectBounds var6 = this.process(this.handle(var3, var4, var5), var3);
      float var7 = this.resolve().size() * var3.handle(42.0F);
      float var8 = Math.max(0.0F, var7 - var6.h());
      this.active = Math.max(0.0F, Math.min(var8, this.active - (float)var1 * var3.handle(42.0F)));
      this.context.compute(this.active);
      return true;
   }

   public boolean handle(char var1) {
      if (!this.state) {
         return false;
      }

      if ((Character.isLetterOrDigit(var1) || var1 == ' ' || var1 == '_' || var1 == '-' || var1 == '.') && this.cache.length() < 64) {
         this.cache = this.cache + var1;
         this.current = this.resolve().isEmpty() ? -1 : 0;
         this.active = 0.0F;
         this.context.process(0.0F);
      }

      return true;
   }

   public boolean handle(int var1) {
      if (!this.state) {
         return false;
      }

      List var2 = this.resolve();
      if (var1 == 256) {
         this.handle();
         return true;
      }

      if (var1 == 259) {
         if (!this.cache.isEmpty()) {
            this.cache = this.cache.substring(0, this.cache.length() - 1);
            this.current = this.resolve().isEmpty() ? -1 : 0;
            this.active = 0.0F;
            this.context.process(0.0F);
         }

         return true;
      } else if (var1 == 264) {
         if (!var2.isEmpty()) {
            this.current = Math.min(var2.size() - 1, Math.max(0, this.current + 1));
         }

         return true;
      } else if (var1 == 265) {
         if (!var2.isEmpty()) {
            this.current = Math.max(0, this.current - 1);
         }

         return true;
      } else {
         if (var1 != 257 && var1 != 335) {
            return true;
         }

         if (this.current >= 0 && this.current < var2.size()) {
            this.mode = (File)var2.get(this.current);
            this.handle();
         }

         return true;
      }
   }

   public void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, int var6, int var7) {
      float var8 = this.data.handle();
      if (!(var8 <= 0.001F) && var1 != null && var2 != null && var3 != null) {
         RectBounds var9 = this.handle(var2, var6, var7);
         float var10 = var2.handle(14.0F) * (1.0F - var8);
         var9 = new RectBounds(var9.x(), var9.y() + var10, var9.w(), var9.h());
         var1.update(var8);

         try {
            var1.handle(0.0F, 0.0F, var6, var7, 0.0F, ThemeColors.handle(0, 0, 0, var3.unload() ? 72 : 116));
            float var11 = var2.handle(14.0F);
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var11, var2.handle(26.0F), var2.handle(2.0F), ThemeColors.handle(0, 0, 0, 164));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var11, this.handle(var3, 238));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var11, ThemeColors.handle(var3.save(), 92), 0.8F);
            ModuleStateHelper.handle(
               var1, var2, FontRegistry.config, var9.x() + var2.handle(20.0F), var9.y() + var2.handle(18.0F), 13.0F, "Import Foundry Shader", var3.load()
            );
            this.handle(var1, var2, var3, var9);
            this.handle(var1, var2, var3, var9, var4, var5);
            this.process(var1, var2, var3, var9, var4, var5);
         } finally {
            var1.onTick();
         }
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4) {
      RectBounds var5 = this.handle(var4, var2);
      var1.handle(var5.x(), var5.y(), var5.w(), var5.h(), var2.handle(8.0F), ThemeColors.handle(255, 255, 255, var3.unload() ? 126 : 16));
      var1.handle(var5.x(), var5.y(), var5.w(), var5.h(), var2.handle(8.0F), ThemeColors.handle(var3.save(), 82), 0.7F);
      String var6 = this.cache.isBlank() ? "Search" : this.cache;
      int var7 = this.cache.isBlank() ? var3.animate() : var3.load();
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var5.x() + var2.handle(12.0F), var5.y(), var5.h(), 10.0F, var6, var7);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, float var5, float var6) {
      RectBounds var7 = this.process(var4, var2);
      List var8 = this.resolve();
      float var9 = var2.handle(42.0F);
      this.output = -1;
      var1.compute();
      var1.handle(var7.x(), var7.y(), var7.w(), var7.h(), var2.handle(8.0F), var2.handle(8.0F), var2.handle(8.0F), var2.handle(8.0F));

      try {
         var1.handle(var7.x(), var7.y(), var7.w(), var7.h(), var2.handle(8.0F), ThemeColors.handle(255, 255, 255, var3.unload() ? 82 : 10));
         float var10 = this.context.handle();
         if (var8.isEmpty()) {
            String var11 = this.config.isEmpty() ? "No shared shaders" : "No matches";
            float var12 = ModuleStateHelper.handle(var2, FontRegistry.instance, var11, 10.0F);
            ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var7.x() + (var7.w() - var12) * 0.5F, var7.y(), var7.h(), 10.0F, var11, var3.animate());
         }

         for (int var22 = 0; var22 < var8.size(); var22++) {
            float var23 = var7.y() + var22 * var9 - var10;
            if (!(var23 > var7.y() + var7.h()) && !(var23 + var9 < var7.y())) {
               boolean var13 = var5 >= var7.x() && var5 < var7.x() + var7.w() && var6 >= var23 && var6 < var23 + var9;
               if (var13) {
                  this.output = var22;
               }

               boolean var14 = var22 == this.current;
               float var15 = var14 ? 1.0F : (var13 ? 0.62F : 0.0F);
               var1.handle(
                  var7.x() + var2.handle(6.0F),
                  var23 + var2.handle(4.0F),
                  var7.w() - var2.handle(12.0F),
                  var9 - var2.handle(8.0F),
                  var2.handle(7.0F),
                  ThemeColors.handle(ThemeColors.handle(255, 255, 255, var3.unload() ? 86 : 10), ThemeColors.handle(var3.save(), 76), var15)
               );
               File var16 = (File)var8.get(var22);
               String var17 = this.handle(var2, var16.getName(), var7.w() - var2.handle(132.0F), 10.0F);
               String var18 = instance.format(new Date(var16.lastModified()));
               ModuleStateHelper.handle(var1, var2, FontRegistry.config, var7.x() + var2.handle(18.0F), var23 + var2.handle(10.0F), 10.0F, var17, var3.load());
               ModuleStateHelper.handle(
                  var1, var2, FontRegistry.instance, var7.x() + var2.handle(18.0F), var23 + var2.handle(24.0F), 8.0F, var18, var3.animate()
               );
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, float var5, float var6) {
      this.handle(var1, var2, var3, this.resolve(var4, var2), "Cancel", var5, var6, false);
      this.handle(var1, var2, var3, this.compute(var4, var2), "Open", var5, var6, true);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, String var5, float var6, float var7, boolean var8) {
      boolean var9 = var4.contains(var6, var7);
      int var10 = ThemeColors.handle(
         ThemeColors.handle(255, 255, 255, var3.unload() ? 92 : 18), ThemeColors.handle(var8 ? var3.save() : var3.submit(), 94), var9 ? 1.0F : 0.0F
      );
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), var10);
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(var8 ? var3.save() : var3.submit(), var9 ? 148 : 78), 0.7F);
      float var11 = ModuleStateHelper.handle(var2, FontRegistry.config, var5, 10.0F);
      ModuleStateHelper.handle(var1, var2, FontRegistry.config, var4.x() + (var4.w() - var11) * 0.5F, var4.y(), var4.h(), 10.0F, var5, var3.load());
   }

   private List<File> resolve() {
      if (this.cache != null && !this.cache.isBlank()) {
         String var1 = this.cache.toLowerCase(Locale.ROOT);
         ArrayList var2 = new ArrayList();

         for (File var4 : this.config) {
            if (var4.getName().toLowerCase(Locale.ROOT).contains(var1)) {
               var2.add(var4);
            }
         }

         if (this.current >= var2.size()) {
            this.current = var2.isEmpty() ? -1 : var2.size() - 1;
         }

         return var2;
      } else {
         return new ArrayList<>(this.config);
      }
   }

   private RectBounds handle(GuiMetrics var1, int var2, int var3) {
      float var4 = Math.min(var1.handle(480.0F), var2 - var1.handle(48.0F));
      float var5 = Math.min(var1.handle(360.0F), var3 - var1.handle(64.0F));
      return new RectBounds((var2 - var4) * 0.5F, (var3 - var5) * 0.5F, var4, var5);
   }

   private RectBounds handle(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var2.handle(20.0F), var1.y() + var2.handle(52.0F), var1.w() - var2.handle(40.0F), var2.handle(34.0F));
   }

   private RectBounds process(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var2.handle(20.0F), var1.y() + var2.handle(98.0F), var1.w() - var2.handle(40.0F), var1.h() - var2.handle(158.0F));
   }

   private RectBounds compute(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var1.w() - var2.handle(112.0F), var1.y() + var1.h() - var2.handle(48.0F), var2.handle(92.0F), var2.handle(30.0F));
   }

   private RectBounds resolve(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var1.w() - var2.handle(214.0F), var1.y() + var1.h() - var2.handle(48.0F), var2.handle(92.0F), var2.handle(30.0F));
   }

   private String handle(GuiMetrics var1, String var2, float var3, float var4) {
      if (var2 == null) {
         return "";
      }

      if (ModuleStateHelper.handle(var1, FontRegistry.config, var2, var4) <= var3) {
         return var2;
      }

      String var5 = "...";
      String var6 = var2;

      while (!var6.isEmpty() && ModuleStateHelper.handle(var1, FontRegistry.config, var6 + var5, var4) > var3) {
         var6 = var6.substring(0, var6.length() - 1);
      }

      return var6.isEmpty() ? var5 : var6 + var5;
   }

   private int handle(ThemeColors var1, int var2) {
      return var1.unload() ? ThemeColors.handle(255, 255, 255, Math.min(255, var2 + 8)) : ThemeColors.handle(10, 12, 18, var2);
   }
}
