package ru.wild.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.SurfaceInteractionRouter;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

public final class ModuleStateHelper {
   private static final Map<Long, ModuleStateHelper.PrimaryCacheEntry> instance = new HashMap<>();
   private static final float data = 1.08F;
   private static float context = 1.08F;
   private static long config;

   public static void handle(GuiMetrics var0) {
      float var1 = var0 == null ? 1.0F : Math.max(1.0F, var0.process());
      context = var1 * 1.08F;
   }

   public static int handle(ThemeRenderContext var0) {
      return handle(var0 == null ? null : var0.apply());
   }

   public static int handle(ThemeColors var0) {
      return var0 == null ? -1 : var0.load();
   }

   public static int process(ThemeRenderContext var0) {
      return process(var0 == null ? null : var0.apply());
   }

   public static int process(ThemeColors var0) {
      return var0 == null ? -1711276033 : var0.animate();
   }

   public static int compute(ThemeColors var0) {
      return var0 == null ? 1728053247 : var0.encodePoint();
   }

   public static int compute(ThemeRenderContext var0) {
      return resolve(var0 == null ? null : var0.apply());
   }

   public static int resolve(ThemeColors var0) {
      return var0 == null ? -1 : var0.load();
   }

   public static int update(ThemeColors var0) {
      if (var0 != null && var0.unload()) {
         return -131586;
      } else {
         return var0 == null ? -1 : var0.load();
      }
   }

   public static int apply(ThemeColors var0) {
      if (var0 != null && var0.unload()) {
         return -723465;
      } else {
         return var0 == null ? -1711276033 : ThemeColors.handle(var0.load(), var0.animate(), 0.1F);
      }
   }

   public static int execute(ThemeColors var0) {
      if (var0 == null) {
         return ThemeColors.handle(255, 255, 255, 178);
      }

      if (!var0.unload()) {
         if (check(var0)) {
            int var1 = var0.apply() >>> 24 & 0xFF;
            return ThemeColors.handle(var0.apply(), ThemeColors.handle(var0.submit(), var1), 0.13F);
         } else {
            return var0.apply();
         }
      } else {
         return ThemeColors.handle(ThemeColors.handle(255, 255, 255, 178), ThemeColors.handle(var0.save(), 178), 0.055F);
      }
   }

   public static int prepare(ThemeColors var0) {
      if (var0 == null) {
         return ThemeColors.handle(255, 255, 255, 196);
      }

      if (!var0.unload()) {
         if (check(var0)) {
            int var1 = var0.execute() >>> 24 & 0xFF;
            return ThemeColors.handle(var0.execute(), ThemeColors.handle(var0.save(), var1), 0.1F);
         } else {
            return var0.execute();
         }
      } else {
         return ThemeColors.handle(ThemeColors.handle(255, 255, 255, 196), ThemeColors.handle(var0.save(), 196), 0.045F);
      }
   }

   public static int handle(ThemeColors var0, float var1) {
      if (var0 == null) {
         return ThemeColors.handle(255, 255, 255, 174);
      }

      float var2 = execute(var1);
      if (!var0.unload()) {
         return ThemeColors.handle(var0.prepare(), var0.onTick(), var2);
      }

      int var3 = ThemeColors.handle(ThemeColors.handle(255, 255, 255, 214), ThemeColors.handle(255, 255, 255, 236), var2);
      return ThemeColors.handle(var3, ThemeColors.handle(var0.save(), var3 >>> 24 & 0xFF), 0.045F + 0.035F * var2);
   }

   public static int process(ThemeColors var0, float var1) {
      float var2 = execute(var1);
      if (var0 == null || var0.unload()) {
         return ThemeColors.handle(255, 255, 255, Math.round(153.0F * var2));
      } else {
         return check(var0) ? ThemeColors.handle(var0.save(), Math.round(52.0F * var2)) : ThemeColors.handle(var0.load(), Math.round(10.0F * var2));
      }
   }

   public static int compute(ThemeColors var0, float var1) {
      float var2 = execute(var1);
      return var0 != null && var0.unload() ? ThemeColors.handle(46, 59, 70, Math.round(32.0F * var2)) : ThemeColors.handle(0, 0, 0, Math.round(180.0F * var2));
   }

   public static int handle(ThemeColors var0, int var1, float var2) {
      float var3 = execute(var2);
      return var0 != null && var0.unload() ? ThemeColors.handle(46, 59, 70, Math.round(32.0F * var3)) : ThemeColors.handle(var1, Math.round(255.0F * var3));
   }

   public static void handle(
      RoundedRectRenderer var0,
      GuiMetrics var1,
      ThemeColors var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      if (var0 != null && var1 != null && var2 != null && !(var10 <= 0.0F)) {
         if (var2.unload()) {
            var0.handle(var3, var4, var5, var6, var7, var8, var9, ThemeColors.handle(46, 59, 70, Math.round(32.0F * execute(var10))));
         } else {
            var0.handle(var3, var4, var5, var6, var7, var8, var9, ThemeColors.handle(0, 0, 0, Math.round(180.0F * execute(var10))));
         }
      }
   }

   public static int handle(ChoiceSetting var0, float var1, GuiMetrics var2) {
      float var3 = var2.handle(3.0F);
      float var4 = 0.0F;
      int var5 = 1;

      for (int var6 = 0; var6 < var0.config.size(); var6++) {
         float var7 = handle(var2, FontRegistry.instance, handle(var0.config.get(var6)), 8.0F);
         float var8 = Math.max(var2.handle(18.0F), var7 + var2.handle(8.0F));
         if (var4 > 0.0F && var4 + var8 > var1) {
            var5++;
            var4 = 0.0F;
         }

         var4 += var8 + var3;
      }

      return var5;
   }

   public static String handle(BooleanSetting var0) {
      return var0.cache == -1 ? var0.instance : var0.instance + " [" + (var0.output ? "H " : "") + KeycodeNames.handle(var0.cache) + "]";
   }

   public static void handle(RoundedRectRenderer var0, GuiMetrics var1, FontObject var2, float var3, float var4, float var5, String var6, int var7) {
      float var8 = handle(var1, var5);
      var0.handle(var2, var3, var4 + var8, var8 * 2.0F, var6, var7);
   }

   public static void handle(RoundedRectRenderer var0, GuiMetrics var1, FontObject var2, float var3, float var4, float var5, float var6, String var7, int var8) {
      handle(var0, var1, var2, var3, handle(var1, var2, var4, var5, var6), var6, var7, var8);
   }

   public static void handle(
      RoundedRectRenderer var0, GuiMetrics var1, FontObject var2, float var3, float var4, float var5, float var6, String var7, int var8, String var9
   ) {
      float var10 = handle(var1, var6);
      var0.handle(var2, var3, handle(var1, var2, var4, var5, var6) + var10, var10 * 2.0F, var7, var8, var9);
   }

   public static void handle(
      RoundedRectRenderer var0, GuiMetrics var1, FontObject var2, float var3, float var4, float var5, float var6, float var7, String var8, int var9
   ) {
      if (var8 != null && !var8.isEmpty()) {
         float var10 = handle(var1, var7);
         int var11 = var8.codePointAt(0);
         float var12 = var3 + var5 * 0.5F - FontRegistry.process(var2, var11, var10);
         float var13 = var4 + var6 * 0.5F + FontRegistry.handle(var2, var11, var10);
         var0.handle(var2, var12, var13, var10 * 2.0F, var8, var9);
      }
   }

   public static float handle(FontObject var0, String var1, float var2) {
      return RoundedRectRenderer.handle(var0, var1 == null ? "" : var1, var2 * 2.0F * context).instance;
   }

   public static float handle(GuiMetrics var0, FontObject var1, String var2, float var3) {
      return RoundedRectRenderer.handle(var1, var2 == null ? "" : var2, handle(var0, var3) * 2.0F).instance;
   }

   public static float handle(GuiMetrics var0, FontObject var1, float var2) {
      float var3 = handle(var0, var2);
      return Math.max(var3, RoundedRectRenderer.handle(var1, "Ag", var3 * 2.0F).data);
   }

   public static float handle(GuiMetrics var0, FontObject var1, float var2, float var3, float var4) {
      return var2 + (var3 - handle(var0, var1, var4)) * 0.5F;
   }

   private static float handle(GuiMetrics var0, float var1) {
      float var2 = var0 == null ? 1.0F : Math.max(1.0F, var0.process());
      return var1 * var2 * 1.08F;
   }

   public static boolean handle(ModernClickGuiState var0, float var1, float var2, float var3, float var4) {
      return handle(var0.sampleLayer(), var0.sendWorld(), var1, var2, var3, var4);
   }

   public static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var1 >= var3 && var0 < var2 + var4 && var1 < var3 + var5;
   }

   public static float handle(float var0) {
      return handle(var0, 0.0F, 0.03F, 0.012F);
   }

   public static float handle(float var0, float var1) {
      return handle(var0, var1, 0.03F, 0.012F);
   }

   public static float handle(float var0, float var1, float var2, float var3) {
      float var4 = process(var0);
      float var5 = Math.min(Math.max(0.0F, var3), Math.abs(var1) * 0.16F);
      return 1.0F + var4 * var2 + var5;
   }

   public static float process(float var0) {
      float var1 = SpringAnimation.process(var0);
      return 1.0F - (float)Math.exp(-3.25F * var1);
   }

   public static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5, Runnable var6) {
      handle(var0, var1, var2, var3, var4, var5, var5, var5, var5, var6);
   }

   public static void handle(
      RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, Runnable var9
   ) {
      if (var0 != null && var9 != null && !(var3 <= 0.0F) && !(var4 <= 0.0F)) {
         var0.compute();
         var0.handle(var1, var2, var3, var4, var5, var6, var7, var8);

         try {
            var9.run();
         } finally {
            var0.compute();
            var0.apply();
         }
      }
   }

   public static void handle(RoundedRectRenderer var0, ThemeRenderContext var1, float var2, float var3, float var4, float var5) {
      handle(var0, var1.update(), var1.apply(), var2, var3, var4, var5);
   }

   public static void handle(RoundedRectRenderer var0, GuiMetrics var1, ThemeColors var2, float var3, float var4, float var5, float var6) {
      var0.process(var3, var4, var5, var5, var1.handle(3.0F), var2.save(), var2.submit());
      var0.process(var3 + var5 + var6, var4, var5, var5, var1.handle(3.0F), -24930, -32126);
      var0.process(var3, var4 + var5 + var6, var5, var5, var1.handle(3.0F), -24854, -32032);
      var0.process(var3 + var5 + var6, var4 + var5 + var6, var5, var5, var1.handle(3.0F), -6357069, -8192089);
   }

   public static void handle(RoundedRectRenderer var0, ThemeRenderContext var1, String var2, float var3, float var4, float var5) {
      GuiMetrics var6 = var1.update();
      ThemeColors var7 = var1.apply();
      String var8 = var2 == null ? "" : var2;
      float var9 = Math.min(var5 * 0.55F, handle(var6, FontRegistry.instance, var8, 10.0F));
      float var10 = Math.max(var6.handle(34.0F), var9 + var6.handle(12.0F));
      float var11 = var3 + var5 - var10;
      var0.handle(var11, var4, var10, var6.handle(16.0F), var6.handle(4.0F), var7.check());
      var0.handle(var11, var4, var10, var6.handle(16.0F), var6.handle(4.0F), var7.select(), 0.5F);
      handle(
         var0,
         var6,
         FontRegistry.instance,
         var11 + var6.handle(6.0F),
         var4,
         var6.handle(16.0F),
         10.0F,
         handle(FontRegistry.instance, var8, 10.0F, var10 - var6.handle(12.0F)),
         var7.animate()
      );
   }

   public static void handle(RoundedRectRenderer var0, ThemeRenderContext var1, float var2, float var3) {
      GuiMetrics var4 = var1.update();
      ThemeColors var5 = var1.apply();
      var0.handle(var2, var3, var4.handle(8.0F), var4.handle(8.0F), var4.handle(2.0F), update(var5));
      var0.handle(
         var2 + var4.handle(2.5F), var3 + var4.handle(2.0F), var4.handle(1.0F), var4.handle(4.0F), var4.handle(1.0F), ThemeColors.handle(21, 22, 26, 61)
      );
      var0.handle(
         var2 + var4.handle(4.5F), var3 + var4.handle(2.0F), var4.handle(1.0F), var4.handle(4.0F), var4.handle(1.0F), ThemeColors.handle(21, 22, 26, 61)
      );
   }

   public static List<String> handle(FontObject var0, String var1, float var2, float var3, int var4) {
      ArrayList var5 = new ArrayList();
      if (var1 != null && !var1.isBlank()) {
         StringBuilder var6 = new StringBuilder();

         for (String var10 : var1.split("\\s+")) {
            String var11 = var6.isEmpty() ? var10 : var6 + " " + var10;
            if (!(handle(var0, var11, var2) <= var3) && !var6.isEmpty()) {
               var5.add(var6.toString());
               var6 = new StringBuilder(var10);
               if (var5.size() == var4) {
                  break;
               }
            } else {
               var6 = new StringBuilder(var11);
            }
         }

         if (var5.size() < var4 && !var6.isEmpty()) {
            var5.add(var6.toString());
         }

         if (!var5.isEmpty()) {
            int var12 = var5.size() - 1;
            var5.set(var12, handle(var0, (String)var5.get(var12), var2, var3));
         }

         return var5;
      } else {
         return var5;
      }
   }

   public static String handle(FontObject var0, String var1, float var2, float var3) {
      String var4 = var1 == null ? "" : var1;
      if (handle(var0, var4, var2) <= var3) {
         return var4;
      }

      String var5 = "...";

      while (!var4.isEmpty() && handle(var0, var4 + var5, var2) > var3) {
         var4 = var4.substring(0, var4.length() - 1);
      }

      return var4 + var5;
   }

   public static String handle(GuiMetrics var0, FontObject var1, String var2, float var3, float var4) {
      String var5 = var2 == null ? "" : var2;
      if (handle(var0, var1, var5, var3) <= var4) {
         return var5;
      }

      String var6 = "...";

      while (!var5.isEmpty() && handle(var0, var1, var5 + var6, var3) > var4) {
         var5 = var5.substring(0, var5.length() - 1);
      }

      return var5 + var6;
   }

   public static String compute(float var0) {
      return Math.abs(var0 - Math.round(var0)) < 0.001F ? Integer.toString(Math.round(var0)) : String.format(Locale.ROOT, "%.1f", var0);
   }

   public static String process(float var0, float var1) {
      int var2 = update(var1);
      return var2 > 0 && !(Math.abs(var0 - Math.round(var0)) < 0.001F)
         ? String.format(Locale.ROOT, "%." + var2 + "f", var0)
         : Integer.toString(Math.round(var0));
   }

   private static int update(float var0) {
      if (Float.isFinite(var0) && !(var0 <= 0.0F)) {
         try {
            int var1 = new BigDecimal(Float.toString(Math.abs(var0))).stripTrailingZeros().scale();
            return Math.min(4, Math.max(0, var1));
         } catch (NumberFormatException var2) {
            return 1;
         }
      } else {
         return 1;
      }
   }

   public static float handle(NumberSetting var0) {
      float var1 = Math.max(1.0E-4F, var0.cache - var0.state);
      return Math.max(0.0F, Math.min(1.0F, (var0.config - var0.state) / var1));
   }

   public static float handle(ColorSetting var0) {
      float var1 = Math.max(1.0E-4F, var0.output - var0.cache);
      return Math.max(0.0F, Math.min(1.0F, (var0.state - var0.cache) / var1));
   }

   public static int process(float var0, float var1, float var2, float var3) {
      var0 %= 360.0F;
      if (var0 < 0.0F) {
         var0 += 360.0F;
      }

      float var4 = (1.0F - Math.abs(2.0F * var2 - 1.0F)) * var1;
      float var5 = var4 * (1.0F - Math.abs(var0 / 60.0F % 2.0F - 1.0F));
      float var6 = var2 - var4 * 0.5F;
      float var7;
      float var8;
      float var9;
      if (var0 < 60.0F) {
         var7 = var4;
         var8 = var5;
         var9 = 0.0F;
      } else if (var0 < 120.0F) {
         var7 = var5;
         var8 = var4;
         var9 = 0.0F;
      } else if (var0 < 180.0F) {
         var7 = 0.0F;
         var8 = var4;
         var9 = var5;
      } else if (var0 < 240.0F) {
         var7 = 0.0F;
         var8 = var5;
         var9 = var4;
      } else if (var0 < 300.0F) {
         var7 = var5;
         var8 = 0.0F;
         var9 = var4;
      } else {
         var7 = var4;
         var8 = 0.0F;
         var9 = var5;
      }

      return ThemeColors.handle(
         Math.round((var7 + var6) * 255.0F),
         Math.round((var8 + var6) * 255.0F),
         Math.round((var9 + var6) * 255.0F),
         Math.round(Math.max(0.0F, Math.min(1.0F, var3)) * 255.0F)
      );
   }

   public static int handle(float var0, float var1, float var2) {
      return compute(var0, var1, var2, 1.0F);
   }

   public static int compute(float var0, float var1, float var2, float var3) {
      var0 %= 1.0F;
      if (var0 < 0.0F) {
         var0++;
      }

      var1 = execute(var1);
      var2 = execute(var2);
      int var4 = (int)(var0 * 6.0F);
      float var5 = var0 * 6.0F - var4;
      float var6 = var2 * (1.0F - var1);
      float var7 = var2 * (1.0F - var5 * var1);
      float var8 = var2 * (1.0F - (1.0F - var5) * var1);
      float var9;
      float var10;
      float var11;
      switch (var4 % 6) {
         case 0:
            var9 = var2;
            var10 = var8;
            var11 = var6;
            break;
         case 1:
            var9 = var7;
            var10 = var2;
            var11 = var6;
            break;
         case 2:
            var9 = var6;
            var10 = var2;
            var11 = var8;
            break;
         case 3:
            var9 = var6;
            var10 = var7;
            var11 = var2;
            break;
         case 4:
            var9 = var8;
            var10 = var6;
            var11 = var2;
            break;
         default:
            var9 = var2;
            var10 = var6;
            var11 = var7;
      }

      return ThemeColors.handle(Math.round(var9 * 255.0F), Math.round(var10 * 255.0F), Math.round(var11 * 255.0F), Math.round(execute(var3) * 255.0F));
   }

   public static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = var4 / 6.0F;
      int[] var7 = new int[]{
         handle(0.0F, 1.0F, 1.0F),
         handle(0.16666667F, 1.0F, 1.0F),
         handle(0.33333334F, 1.0F, 1.0F),
         handle(0.5F, 1.0F, 1.0F),
         handle(0.6666667F, 1.0F, 1.0F),
         handle(0.8333333F, 1.0F, 1.0F),
         handle(1.0F, 1.0F, 1.0F)
      };
      var0.compute();
      var0.handle(var1, var2, var3, var4, var5, var5, var5, var5);

      try {
         for (int var8 = 0; var8 < 6; var8++) {
            float var9 = var2 + var8 * var6;
            var0.process(var1, var9, var3, var6 + 1.0F, 0.0F, var7[var8], var7[var8 + 1]);
         }
      } finally {
         var0.compute();
         var0.apply();
      }
   }

   public static float process(GuiMetrics var0) {
      return Math.max(15.0F, Math.min(20.0F, var0.handle(18.0F)));
   }

   public static float compute(GuiMetrics var0) {
      return process(var0) + Math.max(12.0F, var0.handle(18.0F));
   }

   public static void handle(
      RoundedRectRenderer var0,
      GuiMetrics var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      Runnable var11
   ) {
      handle(var0, var1, null, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void handle(
      RoundedRectRenderer var0,
      GuiMetrics var1,
      ThemeColors var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      Runnable var12
   ) {
      if (var0 != null && var12 != null && !(var5 <= 1.0F) && !(var6 <= 1.0F)) {
         if (!Menu.handle(Menu.serverRead)) {
            var0.compute();
            var0.handle(var3, var4, var5, var6, var7, var8, var9, var10);

            try {
               var12.run();
            } finally {
               var0.compute();
               var0.apply();
            }
         } else {
            long var13 = resolve(var3, var4, var5, var6);
            ModuleStateHelper.CacheEntry var15 = handle(var1, var13, var11, 1.8F, 30.0F, 150.0F);
            float var16 = var15.data;
            float var17 = var15.config;
            if (var16 < 0.006F) {
               var0.compute();
               var0.handle(var3, var4, var5, var6, var7, var8, var9, var10);

               try {
                  var12.run();
               } finally {
                  var0.compute();
                  var0.apply();
               }
            } else {
               RoundedRectRenderer.PrimaryColorState var18 = var0.handle(var3, var4, var5, var6);
               if (var18 == null) {
                  var0.compute();
                  var0.handle(var3, var4, var5, var6, var7, var8, var9, var10);

                  try {
                     var12.run();
                  } finally {
                     var0.compute();
                     var0.apply();
                  }
               } else {
                  try {
                     var12.run();
                  } finally {
                     var0.handle(var18);
                  }

                  float var19 = var1.handle(48.0F);
                  float var20 = Math.min(1.0F, var16 / Math.max(var19 * 0.08F, 1.0F));
                  float var21 = process(var1) * var20;
                  float var22 = Math.min(1.0F, var16 / Math.max(var1.handle(26.0F), 1.0F));
                  float var23 = Math.min(var1.handle(15.0F), var16 * 0.6F) * var22;
                  var0.handle(var18, var3, var4, var5, var6, var7, var8, var9, var10, var21, var23, var16, var22, 0.0F, var17);
               }
            }
         }
      }
   }
   public static void handle(
      RoundedRectRenderer var0, GuiMetrics var1, ThemeColors var2, float var3, float var4, float var5, float var6, float var7, float var8
   ) {
      long var9 = resolve(var3 + 41.7F, var4 + 19.3F, var5 + 3.1F, var6 + 2.4F);
      ModuleStateHelper.CacheEntry var11 = handle(var1, var9, var8, 4.05F, 28.0F, 88.0F);
      float var12 = Math.min(1.0F, var11.data / Math.max(var1.handle(48.0F), 1.0F));
      float var13 = 0.0F;
      if (!(var12 <= 1.0E-4F) && !(var5 <= 1.0F) && !(var6 <= 1.0F)) {
         float var14 = var11.config;
         float var15 = compute(Math.max(var12, var13 * 0.9F), 4.2F);
         float var16 = var1.handle(1.1F) + var1.handle(5.1F) * var15 + var1.handle(2.7F) * var13;
         float var17 = var1.handle(0.95F) + var1.handle(7.15F) * compute(var12, 4.18F);
         float var18 = Math.min(var6 * 0.14F, var1.handle(6.5F) + var1.handle(10.5F) * var15);
         float var19 = Math.min(var6 * 0.1F, var1.handle(4.2F) + var1.handle(7.1F) * var15);
         float var20 = (float)Math.pow(Math.max(var12 * 0.24F + var15 * 0.76F, 0.0F), 0.82F);
         float var21 = (float)Math.pow(Math.max(var12 * 0.32F + var15 * 0.68F, 0.0F), 1.02F);
         float var22 = (float)Math.pow(Math.max(0.0F, var13), 0.82F);
         int var23 = ThemeColors.handle(ThemeColors.handle(var2.apply(), var2.submit(), 0.08F), Math.round(18.0F * var20));
         int var24 = ThemeColors.handle(var2.apply(), Math.round(10.0F * var21));
         int var25 = ThemeColors.handle(ThemeColors.handle(var2.apply(), var2.save(), 0.11F), Math.round(14.0F * var22));
         int var26 = ThemeColors.handle(ThemeColors.handle(var2.check(), var2.submit(), 0.24F), Math.round(16.0F * var22));
         var0.compute();
         var0.compute(var3, var4, var5, var6, var16);
         var0.handle(var3, var4, var5, var6, var7, var7, var7, var7);
         boolean var33 = false /* VF: Semaphore variable */;

         try {
            var33 = true;
            if (var12 > 1.0E-4F) {
               for (int var27 = 0; var27 < 3; var27++) {
                  float var28 = (var27 + 1.0F) / 3.0F;
                  float var29 = var14 * var17 * var28;
                  float var30 = var12 * (0.019F - var27 * 0.0048F);
                  var0.process(var3, var4 + var29, var5, var6, var7, var30);
               }
            }

            if (var13 > 1.0E-4F) {
               float var35 = Math.min(Math.min(var5, var6) * 0.18F, var1.handle(1.15F) + var1.handle(2.4F) * var22);
               var0.process(var3, var4, var5, var6, var7, 0.016F + var13 * 0.016F);
               if (var5 - var35 * 2.0F > 1.0F && var6 - var35 * 1.35F > 1.0F) {
                  var0.process(
                     var3 + var35,
                     var4 + var35 * 0.65F,
                     var5 - var35 * 2.0F,
                     var6 - var35 * 1.35F,
                     Math.max(var1.handle(2.0F), var7 - var35 * 0.4F),
                     var13 * 0.014F
                  );
               }

               var0.handle(var3, var4, var5, var6, var7, var25);
               var0.handle(var3, var4, var5, var6, var7, var26, 0.5F);
            }

            if (var12 > 1.0E-4F) {
               if (var14 > 0.0F) {
                  var0.process(var3, var4, var5, var18, var7, var23, ThemeColors.handle(0, 0, 0, 0));
                  var0.process(var3, var4 + var6 - var19, var5, var19, var7, ThemeColors.handle(0, 0, 0, 0), var24);
                  var33 = false;
               } else {
                  var0.process(var3, var4 + var6 - var18, var5, var18, var7, ThemeColors.handle(0, 0, 0, 0), var23);
                  var0.process(var3, var4, var5, var19, var7, var24, ThemeColors.handle(0, 0, 0, 0));
                  var33 = false;
               }
            } else {
               var33 = false;
            }
         } finally {
            if (var33) {
               var0.compute();
               var0.apply();
            }
         }

         var0.compute();
         var0.apply();
      }
   }

   public static void process(
      RoundedRectRenderer var0,
      GuiMetrics var1,
      ThemeColors var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, 0L, -1.0F, -1.0F, null);
   }

   public static void handle(
      RoundedRectRenderer var0,
      GuiMetrics var1,
      ThemeColors var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      long var11,
      float var13,
      float var14,
      SurfaceInteractionRouter.Callback var15
   ) {
      if (var0 != null && var1 != null && var2 != null && !(var5 <= 0.0F) && !(var6 <= 0.0F) && !(var8 <= 0.0F)) {
         float var16 = 0.0F;
         if (var11 != 0L && var15 != null) {
            var16 = SurfaceInteractionRouter.handle(var11, var3, var4, var5, var6, var7, var8, Math.max(var1.handle(4.5F), 5.0F), var13, var14, var15);
         }

         long var17 = resolve(var3 + 73.1F, var4 + 11.7F, var5 + 5.4F, var6 + 3.2F);
         ModuleStateHelper.CacheEntry var19 = handle(var1, var17, var9, 3.4F, 24.0F, 78.0F);
         float var20 = var19.config;
         float var21 = Math.min(1.0F, var19.data / Math.max(var1.handle(48.0F), 1.0F));
         float var22 = var16;
         float var23 = Math.max(var21, Math.max(var10 * 0.72F, var16 * 0.85F));
         float var24 = 0.82F + 0.18F * resolve((float)System.currentTimeMillis() * 7.0E-4F + var7 * 0.015F);
         float var25 = var23 * var24;
         float var26 = var22 * (0.84F + 0.16F * var24);
         float var27 = Math.max(var5 * 0.5F, var1.handle(1.6F));
         float var28 = Math.min(var1.handle(1.15F), Math.max(var1.handle(0.85F), var5 * 0.26F));
         float var29 = var3 + var28;
         float var30 = Math.max(var1.handle(0.8F), var5 - var28 * 2.0F);
         float var31 = var4 + var1.handle(1.2F);
         float var32 = Math.max(var1.handle(8.0F), var6 - var1.handle(2.4F));
         float var33 = var1.handle(0.9F) * var16;
         float var34 = var3 - var33;
         float var35 = var5 + var33 * 2.0F;
         float var36 = Math.max(var35 * 0.5F, var1.handle(1.7F));
         var0.handle(var3, var4, var5, var6, var27, ThemeColors.handle(var2.prepare(), var2.onTick(), 0.26F + var23 * 0.22F));
         var0.handle(
            var29,
            var31,
            var30,
            var32,
            var30 * 0.5F,
            ThemeColors.handle(ThemeColors.handle(0, 0, 0, 24), ThemeColors.handle(var2.submit(), 42), 0.1F + var25 * 0.18F + var22 * 0.08F)
         );
         var0.handle(var3, var4, var5, var6, var27, ThemeColors.handle(var2.load(), Math.round(14.0F * var23 + 10.0F * var26)), 0.5F);
         process(var0, var1, var2, var3, var7, var5, var8, var21, var20);
         int var37 = ThemeColors.handle(var2.render(), ThemeColors.handle(var2.save(), 140), 0.12F + var25 * 0.28F + var22 * 0.08F);
         int var38 = ThemeColors.handle(var2.refresh(), ThemeColors.handle(var2.submit(), 154), 0.26F + var25 * 0.46F + var22 * 0.1F);
         var0.handle(
            var34 - var1.handle(0.25F),
            var7 + var1.handle(0.7F),
            var35 + var1.handle(0.5F),
            Math.max(var1.handle(12.0F), var8 - var1.handle(1.4F)),
            var36,
            var1.handle(3.0F + var25 * 4.5F + var22 * 2.1F),
            var1.handle(0.9F),
            var2.unload()
               ? ThemeColors.handle(46, 59, 70, Math.round(15.0F * var25 + 8.0F * var26))
               : ThemeColors.handle(var2.save(), Math.round(20.0F * var25 + 12.0F * var26))
         );
         var0.process(var34, var7, var35, var8, var36, var37, var38);
         float var39 = Math.max(var1.handle(0.75F), var35 * 0.22F);
         float var40 = Math.max(var1.handle(0.8F), var35 - var39 * 2.0F);
         float var41 = Math.min(Math.max(var1.handle(5.0F), var8 * (0.28F + var25 * 0.08F)), Math.max(var1.handle(6.0F), var8 - var1.handle(2.2F)));
         var0.process(
            var34 + var39,
            var7 + var1.handle(1.15F),
            var40,
            var41,
            var40 * 0.5F,
            ThemeColors.handle(var2.load(), Math.round(26.0F * var25 + 14.0F * var26)),
            ThemeColors.handle(255, 255, 255, 0)
         );
         float var42 = Math.max(var1.handle(1.0F), var35 * 0.28F);
         float var43 = Math.max(var1.handle(0.75F), var35 - var42 * 2.0F);
         float var44 = var7 + var1.handle(2.0F);
         float var45 = Math.max(var1.handle(7.0F), var8 - var1.handle(4.0F));
         var0.process(
            var34 + var42,
            var44,
            var43,
            var45,
            var43 * 0.5F,
            ThemeColors.handle(var2.load(), Math.round(18.0F * var25 + 10.0F * var26)),
            ThemeColors.handle(ThemeColors.handle(var2.save(), var2.submit(), 0.55F), Math.round(48.0F * var25 + 20.0F * var26))
         );
         var0.handle(
            var34,
            var7,
            var35,
            var8,
            var36,
            ThemeColors.handle(ThemeColors.handle(var2.load(), var2.save(), 0.14F + var25 * 0.1F + var22 * 0.06F), Math.round(38.0F * var25 + 14.0F * var26)),
            0.5F
         );
      }
   }

   public static void process(
      RoundedRectRenderer var0, GuiMetrics var1, ThemeColors var2, float var3, float var4, float var5, float var6, float var7, float var8
   ) {
      if (!(var7 <= 1.0E-4F) && !(var5 <= 0.0F) && !(var6 <= 0.0F)) {
         float var9 = Math.max(var1.handle(0.95F), var5 * 0.26F);
         float var10 = var3 + var9;
         float var11 = Math.max(var1.handle(0.75F), var5 - var9 * 2.0F);
         float var12 = var1.handle(1.8F) + var1.handle(7.5F) * var7;
         float var13 = var1.handle(1.4F) + var1.handle(4.5F) * var7;
         float var14 = var8 > 0.0F ? var4 - var12 : var4 + var6 - var13;
         float var15 = var12 + var13;
         float var16 = (float)Math.pow(var7, 0.82F);
         int var17 = ThemeColors.handle(ThemeColors.handle(var2.submit(), var2.save(), 0.56F), Math.round(11.0F * var16));
         int var18 = ThemeColors.handle(ThemeColors.handle(var2.load(), var2.save(), 0.16F), Math.round(18.0F * var16));
         var0.resolve();

         try {
            if (var8 > 0.0F) {
               var0.process(var10, var14, var11, var15 * 0.72F, var11 * 0.5F, var17, var18);
               var0.process(var10, var14 + var15 * 0.72F, var11, var15 * 0.28F, var11 * 0.5F, var18, ThemeColors.handle(255, 255, 255, 0));
            } else {
               var0.process(var10, var14, var11, var15 * 0.28F, var11 * 0.5F, ThemeColors.handle(255, 255, 255, 0), var18);
               var0.process(var10, var14 + var15 * 0.28F, var11, var15 * 0.72F, var11 * 0.5F, var18, var17);
            }
         } finally {
            var0.compute();
            var0.update();
         }
      }
   }

   private static float handle(GuiMetrics var0, float var1, float var2) {
      float var3 = Math.min(1.0F, Math.abs(var1) / Math.max(var0.handle(var2), 0.5F));
      return var3 <= 0.0F ? 0.0F : (float)Math.pow(var3, 0.82F);
   }

   private static ModuleStateHelper.CacheEntry handle(GuiMetrics var0, long var1, float var3, float var4, float var5, float var6) {
      long var7 = System.currentTimeMillis();
      ModuleStateHelper.PrimaryCacheEntry var9 = instance.computeIfAbsent(var1, var0x -> new ModuleStateHelper.PrimaryCacheEntry());
      float var10 = var9.cache == 0L ? 16.0F : Math.min(80.0F, Math.max(1.0F, (float)(var7 - var9.cache)));
      var9.cache = var7;
      float var11 = var0.handle(48.0F);
      float var12 = var0.handle(0.028F);
      float var13 = Math.abs(var3) * var12;
      float var14 = var11 * (1.0F - (float)Math.exp(-var13 / var11));
      float var15 = var3 < -0.001F ? -1.0F : (var3 > 0.001F ? 1.0F : 0.0F);
      if (var15 != 0.0F) {
         var9.config = var15;
      }

      float var16 = var14 > var9.instance ? var5 : var6;
      var9.instance = SpringAnimation.process(var9.instance, var14, var10, var16);
      var9.state = var3;
      if (var9.instance <= 0.006F && Math.abs(var3) <= 0.5F) {
         instance.remove(var1);
         handle(var7);
         return new ModuleStateHelper.CacheEntry(0.0F, 0.0F, 0.0F, var9.config == 0.0F ? 1.0F : var9.config);
      } else {
         handle(var7);
         return new ModuleStateHelper.CacheEntry(0.0F, var9.instance, 0.0F, var9.config == 0.0F ? 1.0F : var9.config);
      }
   }

   private static void handle(
      RoundedRectRenderer var0,
      GuiMetrics var1,
      ThemeColors var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12
   ) {
      int var13 = ThemeColors.handle(var2.save(), ThemeColors.handle(255, 255, 255, 255), 0.24F);
      int var14 = ThemeColors.handle(var2.save(), var2.submit(), 0.52F);
      float var15 = Math.max(0.5F, var1.handle(0.7F));
      float var16 = var3 + var15;
      float var17 = var4 + var15;
      float var18 = Math.max(1.0F, var5 - var15 * 2.0F);
      float var19 = Math.max(1.0F, var6 - var15 * 2.0F);
      float var20 = var1.handle(1.0F);
      float var21 = var16 + var20;
      float var22 = var17 + var20;
      float var23 = Math.max(1.0F, var18 - var20 * 2.0F);
      float var24 = Math.max(1.0F, var19 - var20 * 2.0F);
      float var25 = Math.max(0.0F, var7 - var15);
      float var26 = Math.max(0.0F, var8 - var15);
      float var27 = Math.max(0.0F, var9 - var15);
      float var28 = Math.max(0.0F, var10 - var15);
      float var29 = Math.max(0.0F, var25 - var20);
      float var30 = Math.max(0.0F, var26 - var20);
      float var31 = Math.max(0.0F, var27 - var20);
      float var32 = Math.max(0.0F, var28 - var20);
      float var33 = Math.max(var1.handle(1.1F), Math.min(var1.handle(2.0F), var5 - var1.handle(1.4F)));
      float var34 = var3 + var5 - var33 - var1.handle(0.7F);
      float var35 = var4 + var1.handle(5.0F);
      float var36 = Math.max(1.0F, var6 - var1.handle(10.0F));
      float var37 = 0.72F + 0.28F * resolve((float)System.currentTimeMillis() * 7.8E-4F + var4 * 0.018F);
      float var38 = (float)Math.pow(Math.max(0.0F, Math.min(1.0F, var12)), 0.72F);
      float var39 = (float)Math.pow(var38, 1.18F);
      float var40 = var39 * (0.78F + 0.22F * var37);
      float var41 = var40 * (0.82F + 0.18F * var37);
      float var42 = Math.min(var36, Math.max(var1.handle(10.0F), var36 * (0.18F + var12 * 0.08F)));
      float var43 = Math.max(0.0F, var36 - var42);
      float var44 = var35 + var43 * resolve((float)System.currentTimeMillis() * 9.2E-4F + var11 * 0.17F + var3 * 0.01F);
      var0.compute();
      var0.resolve();

      try {
         var0.handle(
            var16, var17, var18, var19, var25, var26, var27, var28, ThemeColors.handle(var13, Math.round(42.0F * var38)), Math.max(0.75F, var1.handle(0.7F))
         );
         var0.handle(var21, var22, var23, var24, var29, var30, var31, var32, ThemeColors.handle(var14, Math.round(18.0F * var39)), 0.5F);
         var0.handle(
            var34,
            var35,
            var33,
            var36,
            var33 * 0.5F,
            var1.handle(4.2F + 6.8F * var40),
            var1.handle(0.85F),
            ThemeColors.handle(var14, Math.round(16.0F * var40))
         );
         var0.process(
            var34,
            var35,
            var33,
            var36,
            var33 * 0.5F,
            ThemeColors.handle(var13, Math.round(34.0F * var40)),
            ThemeColors.handle(var14, Math.round(18.0F * var39))
         );
         var0.process(
            var34, var44, var33, var42, var33 * 0.5F, ThemeColors.handle(var2.load(), Math.round(22.0F * var41)), ThemeColors.handle(255, 255, 255, 0)
         );
      } finally {
         var0.compute();
         var0.update();
      }
   }

   private static float apply(float var0) {
      float var1 = Math.max(0.0F, Math.min(1.0F, (var0 - 0.035F) / 0.5F));
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float process(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, (var2 - var0) / Math.max(1.0E-5F, var1 - var0)));
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private static float compute(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, var0);
      if (var3 <= 0.0F) {
         return 0.0F;
      }

      float var4 = var3 / Math.max(var2, 1.0E-5F);
      var4 /= 1.0F + var4;
      float var5 = var3 / (var3 + Math.max(var1, 1.0E-5F) * 2.35F);
      return Math.max(0.0F, Math.min(1.0F, var4 * (0.58F + 0.92F * var5) * 1.42F));
   }

   private static float compute(float var0, float var1) {
      float var2 = Math.max(0.0F, Math.min(1.0F, var0));
      if (var2 <= 0.0F) {
         return 0.0F;
      }

      double var3 = Math.expm1(Math.max(1.0E-4F, var1));
      return var3 <= 1.0E-7 ? var2 : (float)(Math.expm1(var1 * var2) / var3);
   }

   public static float resolve(float var0) {
      float var1 = var0 - (float)Math.floor(var0);
      return 0.5F - 0.5F * (float)Math.cos(var1 * Math.PI * 2.0);
   }

   private static long resolve(float var0, float var1, float var2, float var3) {
      long var4 = 1469598103934665603L;
      var4 = (var4 ^ Math.round(var0 * 2.0F)) * 1099511628211L;
      var4 = (var4 ^ Math.round(var1 * 2.0F)) * 1099511628211L;
      var4 = (var4 ^ Math.round(var2 * 2.0F)) * 1099511628211L;
      return (var4 ^ Math.round(var3 * 2.0F)) * 1099511628211L;
   }

   private static float execute(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static boolean check(ThemeColors var0) {
      return var0 != null && (var0.save() & 16777215) == 61695 && (var0.submit() & 16777215) == 17663;
   }

   private static void handle(long var0) {
      if (var0 - config >= 1800L) {
         config = var0;
         Iterator var2 = instance.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            ModuleStateHelper.PrimaryCacheEntry var4 = (ModuleStateHelper.PrimaryCacheEntry)var3.getValue();
            if (var4 == null || var0 - var4.cache > 2600L) {
               var2.remove();
            }
         }
      }
   }
   private ModuleStateHelper() {
   }

   static final class CacheEntry {
      private final float instance;
      final float data;
      private final float context;
      final float config;

      CacheEntry(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }
   }

   static final class PrimaryCacheEntry {
      float instance;
      private float data;
      private float context;
      float config = 1.0F;
      float state;
      long cache;
   }
}
