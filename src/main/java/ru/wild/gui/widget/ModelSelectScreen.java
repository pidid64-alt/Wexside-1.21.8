package ru.wild.gui.widget;

import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeParser;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.render.CosmeticModelRenderer;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.texture.TextureHolder;
import ru.wild.util.render.RoundedRectRenderer;

public final class ModelSelectScreen {
   private static final int instance = -15921388;
   private static final int data = -15197404;
   private static final int context = 12;
   private static final int config = -15657957;
   private static final int state = -14670802;
   private static final CosmeticModelRenderer cache = new CosmeticModelRenderer();
   private static final CosmeticModelRenderer output = new CosmeticModelRenderer();

   private ModelSelectScreen() {
   }

   public static void handle(
      RoundedRectRenderer var0,
      ThemeRenderContext var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ThemeParser var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      GuiMetrics var11 = var1.update();
      ThemeColors var12 = var1.apply();
      var0.handle(var2, var3, var4, var5, var11.handle(10.0F), var11.handle(10.0F), var11.handle(10.0F), var11.handle(10.0F));

      try {
         handle(var0, var2, var3, var4, var5);
         TextureHolder var13 = var6 == null ? null : var6.render();
         if (var13 == null) {
            handle(var0, var1, var2, var3, var4, var5, var6, var10);
         } else {
            float var14 = (float)(System.currentTimeMillis() % 100000L) * 0.001F;
            float var15 = Math.max(var13.refresh(), var13.render());
            float var16 = Math.min(var5 * 0.82F / var13.onTick(), var4 * 0.78F / var15) * Math.max(0.2F, var9);
            float var17 = var2 + var4 * 0.5F;
            float var18 = var3 + var5 * 0.5F + (float)Math.sin(var14 * 1.3F) * var16 * 0.3F;
            float var19 = var7 + (float)Math.sin(var14 * 0.25F) * 4.0F;
            cache.handle(var0, var13, var6.handle(), var17, var18, var16, var19, var8, var10, var14, true);
         }
      } finally {
         var0.compute();
         var0.apply();
      }

      var0.handle(var2, var3, var4, var5, var11.handle(10.0F), ThemeColors.handle(var12.save(), 96), 0.7F);
   }

   public static void handle(RoundedRectRenderer var0, TextureHolder var1, String var2, float var3, float var4, float var5, float var6, float var7) {
      if (var1 != null) {
         float var8 = Math.max(var1.refresh(), var1.render());
         float var9 = Math.min(var6 * 0.8F / var1.onTick(), var5 * 0.84F / var8);
         float var10 = var3 + var5 * 0.5F;
         float var11 = var4 + var6 * 0.52F;
         output.handle(var0, var1, var2, var10, var11, var9, 200.0F, -10.0F, var7, 0.0F, false);
      }
   }

   private static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4) {
      var0.handle(var1, var2, var3, var4, 0.0F, -15921388);
      int var5 = (int)Math.ceil(var3 / 12.0F);
      int var6 = (int)Math.ceil(var4 / 12.0F);

      for (int var7 = 0; var7 < var6; var7++) {
         for (int var8 = 0; var8 < var5; var8++) {
            if ((var7 + var8 & 1) != 0) {
               float var9 = var1 + var8 * 12;
               float var10 = var2 + var7 * 12;
               float var11 = Math.min(12.0F, var1 + var3 - var9);
               float var12 = Math.min(12.0F, var2 + var4 - var10);
               if (var11 > 0.0F && var12 > 0.0F) {
                  var0.handle(var9, var10, var11, var12, 0.0F, -15197404);
               }
            }
         }
      }

      float var13 = var2 + var4 * 0.86F;
      var0.handle(var1, var13, var3, var4 - (var13 - var2), 0.0F, -15657957);
      var0.handle(var1, var13, var3, 1.0F, 0.0F, -14670802);
   }

   private static void handle(RoundedRectRenderer var0, ThemeRenderContext var1, float var2, float var3, float var4, float var5, ThemeParser var6, float var7) {
      GuiMetrics var8 = var1.update();
      ThemeColors var9 = var1.apply();
      String var10 = var6 == null ? "Выберите модель" : "Не удалось загрузить модель";
      String var11 = var6 == null ? "" : handle(var6.refresh());
      float var12 = ModuleStateHelper.handle(FontRegistry.instance, var10, 11.0F);
      ModuleStateHelper.handle(
         var0,
         var8,
         FontRegistry.instance,
         var2 + (var4 - var12) * 0.5F,
         var3 + var5 * 0.46F,
         var8.handle(14.0F),
         11.0F,
         var10,
         ThemeColors.handle(var9.load(), Math.round(200.0F * var7))
      );
      if (!var11.isEmpty()) {
         float var13 = ModuleStateHelper.handle(FontRegistry.instance, var11, 9.0F);
         ModuleStateHelper.handle(
            var0,
            var8,
            FontRegistry.instance,
            var2 + (var4 - var13) * 0.5F,
            var3 + var5 * 0.46F + var8.handle(16.0F),
            var8.handle(12.0F),
            9.0F,
            var11,
            ThemeColors.handle(var9.submit(), Math.round(180.0F * var7))
         );
      }
   }

   private static String handle(String var0) {
      return var0 == null ? "" : var0;
   }
}
