package ru.wild.gui.screen;

import java.awt.Color;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.gui.widget.VirtualListLayout;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.render.RoundedRectRenderer;

public final class ThemeBrowserScreen {
   private final Map<ThemePaletteRegistry.ColorState, Integer> instance = new IdentityHashMap<>();

   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4, float var5) {
      float var6 = var2.handle(UiAnimationKeys.tick());
      if (!(var6 <= 0.005F)) {
         GuiMetrics var7 = handle(var4);
         ThemeColors var8 = var4.apply();
         float var9 = var3.projectItem();
         float var10 = var3.computeResponse();
         float var11 = var7.save();
         float var12 = var7.submit();
         float var13 = var7.handle(14.0F);
         float var14 = this.handle(var6);
         boolean var15 = !var2.filterMatrix() && !var2.runListener() && var6 < 0.995F;
         float var16 = compute(var9);
         float var17 = compute(var10);
         float var18 = Math.max(1.0F, compute(var9 + var11) - var16);
         float var19 = Math.max(1.0F, compute(var10 + var12) - var17);
         RoundedRectRenderer.PrimaryColorState var20 = var15 ? var1.process(var16, var17, var18, var19) : null;
         boolean var21 = false;
         if (var20 != null) {
            try {
               this.handle(var1, var2, var3, var4, var5, var9, var10, var11, var12, var13);
            } finally {
               var1.handle(var20);
            }

            int var22 = ModuleStateHelper.execute(var8);
            int var23 = var8.unload() ? ModuleStateHelper.process(var8, 0.95F) : ThemeColors.handle(ThemeColors.handle(255, 255, 255, 64), var8.save(), 0.3F);
            int var24 = var8.submit();
            int var25 = var8.save();
            float var26 = this.handle();
            var21 = var1.handle(var20, var16, var17, var18, var19, var13, var22, var23, var24, var25, var14, var26);
         }

         if (!var21) {
            this.handle(var1, var2, var3, var4, var5, var9, var10, var11, var12, var13);
         }
      }
   }

   private static GuiMetrics handle(ThemeRenderContext var0) {
      GuiMetrics var1 = var0.update();
      return var1.compute(var1.compute());
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ViewportLayoutState var3,
      ThemeRenderContext var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      GuiMetrics var11 = handle(var4);
      ThemeColors var12 = var4.apply();
      float var13 = var11.handle(8.0F);
      float var14 = var11.handle(44.0F);
      float var15 = var11.handle(8.0F);
      int var16 = var12.unload() ? ThemeColors.handle(255, 255, 255, 194) : ThemeColors.handle(15, 16, 19, 255);
      var1.handle(var6, var7, var8, var9, var10, ModuleStateHelper.execute(var12), 0.88F, var16);
      if (var12.unload()) {
         var1.handle(
            var6 + 1.0F,
            var7 + 1.0F,
            Math.max(1.0F, var8 - 2.0F),
            Math.max(1.0F, var9 - 2.0F),
            Math.max(0.0F, var10 - 1.0F),
            ModuleStateHelper.process(var12, 0.95F),
            1.0F
         );
      }

      this.handle(var1, var6, var7, var8, var9, var10, var12, var5);
      VirtualListLayout var17 = VirtualListLayout.handle(var3, var11);
      float var18 = var17.compute();
      float var19 = var17.process();
      float var20 = var17.resolve();
      var1.handle(var6 + var13, var7 + var13, var18, var14, var15, var15, var11.handle(4.0F), var11.handle(4.0F), ModuleStateHelper.prepare(var12));
      var1.handle(var6 + var13, var19, var18, var20, var11.handle(4.0F), var11.handle(4.0F), var15, var15, ModuleStateHelper.prepare(var12));
      if (var12.unload()) {
         var1.handle(
            var6 + var13 + 1.0F,
            var7 + var13 + 1.0F,
            Math.max(1.0F, var18 - 2.0F),
            Math.max(1.0F, var14 - 2.0F),
            Math.max(0.0F, var15 - 1.0F),
            Math.max(0.0F, var15 - 1.0F),
            Math.max(0.0F, var11.handle(4.0F) - 1.0F),
            Math.max(0.0F, var11.handle(4.0F) - 1.0F),
            ModuleStateHelper.process(var12, 0.72F),
            1.0F
         );
         var1.handle(
            var6 + var13 + 1.0F,
            var19 + 1.0F,
            Math.max(1.0F, var18 - 2.0F),
            Math.max(1.0F, var20 - 2.0F),
            Math.max(0.0F, var11.handle(4.0F) - 1.0F),
            Math.max(0.0F, var11.handle(4.0F) - 1.0F),
            Math.max(0.0F, var15 - 1.0F),
            Math.max(0.0F, var15 - 1.0F),
            ModuleStateHelper.process(var12, 0.72F),
            1.0F
         );
      }

      this.handle(var1, var11, var12, var6, var7, var8, var13, var14, var4);
      this.handle(var1, var2, var17, var11, var12);
      this.handle(var1, var2, var17, var4);
      this.handle(var1, var2, var3, var4, var19, var20);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, VirtualListLayout var3, GuiMetrics var4, ThemeColors var5) {
      float var6 = var3.update();
      float var7 = var3.apply();
      float var8 = var3.execute();
      float var9 = var3.prepare();
      float var10 = var4.handle(8.0F);
      float var11 = var2.handle(UiAnimationKeys.drawAnimation());
      float var12 = var2.handle(UiAnimationKeys.encodePoint());
      String var13 = var2.processWindow();
      boolean var14 = !var2.filterMatrix() && var2.savePacket();
      var1.handle(var6, var7, var8, var9, var10, ModuleStateHelper.prepare(var5));
      var1.handle(var6, var7, var8, var9, var10, ThemeColors.handle(var5.check(), var5.select(), var11));
      if (var5.unload()) {
         var1.handle(
            var6 + 1.0F,
            var7 + 1.0F,
            Math.max(1.0F, var8 - 2.0F),
            Math.max(1.0F, var9 - 2.0F),
            Math.max(0.0F, var10 - 1.0F),
            ModuleStateHelper.process(var5, 0.78F),
            1.0F
         );
      }

      if (var11 > 0.01F) {
         var1.handle(
            var6 + 1.0F,
            var7 + 1.0F,
            Math.max(1.0F, var8 - 2.0F),
            Math.max(1.0F, var9 - 2.0F),
            Math.max(0.0F, var10 - 1.0F),
            ThemeColors.handle(var5.save(), Math.round(50.0F * var11)),
            1.0F
         );
      }

      float var15 = var4.handle(10.0F);
      float var16 = var3.onTick();
      float var17 = var8 - var15 * 2.0F - var16;
      int var18 = ThemeColors.handle(var5.animate(), var5.load(), var11);
      float var19 = var13.isEmpty() ? 0.0F : ModuleStateHelper.handle(var4, FontRegistry.instance, var13, 10.0F);
      float var20 = var19 > var17 ? var17 - var19 : 0.0F;
      int var21 = (int)Math.floor(var6);
      int var22 = (int)Math.ceil(var6 + var8 - var16);
      var1.handle(var21, (int)Math.floor(var7), Math.max(1, var22 - var21), Math.max(1, (int)Math.ceil(var9)));
      if (!var13.isEmpty()) {
         ModuleStateHelper.handle(var1, var4, FontRegistry.instance, var6 + var15 + var20, var7, var9, 10.0F, var13, var18);
      } else if (!var14) {
         ModuleStateHelper.handle(var1, var4, FontRegistry.instance, var6 + var15, var7, var9, 10.0F, "Поиск тем...", var5.encodePoint());
      }

      if (var14) {
         float var23 = (float)((Math.sin(System.currentTimeMillis() * 0.006) + 1.0) * 0.5);
         float var24 = var4.handle(11.0F);
         var1.handle(
            var6 + var15 + var20 + var19 + var4.handle(1.0F),
            var7 + (var9 - var24) * 0.5F,
            Math.max(1.0F, var4.handle(1.0F)),
            var24,
            0.0F,
            ThemeColors.handle(var5.resolve(), Math.round(255.0F * var23))
         );
      }

      var1.apply();
      float var25 = Math.max(var11 * 0.3F, var12);
      if (var25 > 0.01F) {
         float var26 = ModuleStateHelper.handle(var4, FontRegistry.state, "l", 10.0F);
         ModuleStateHelper.handle(
            var1,
            var4,
            FontRegistry.state,
            var3.check() + (var16 - var26) * 0.5F,
            var7,
            var9,
            10.0F,
            "l",
            ThemeColors.handle(var5.resolve(), Math.round(255.0F * var25))
         );
      }
   }
   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, ThemeColors var7, float var8) {
      if (!var7.unload()) {
         var1.compute();
         var1.handle(var2, var3, var4, var5, var6, var6, var6, var6);
         boolean var24 = false /* VF: Semaphore variable */;

         try {
            var24 = true;
            float var9 = var8 * (float) (Math.PI * 2);
            float var10 = 0.8F + 0.2F * (float)Math.sin(var9 * 0.3);
            float var11 = var4 * 0.75F;
            float var12 = var5 * 0.55F;
            float var13 = Math.min(var11, var12) * 0.5F;
            float var14 = var2 + var4 * 0.05F + (float)Math.cos(var9 * 0.1) * var4 * 0.04F;
            float var15 = var3 + var5 * 0.06F + (float)Math.sin(var9 * 0.08) * var5 * 0.03F;
            var1.handle(var14, var15, var11, var12, var13, var11 * 0.45F, var11 * 0.12F, ThemeColors.handle(var7.save(), Math.round(3.0F * var10)));
            float var16 = 0.75F + 0.25F * (float)Math.sin(var9 * 0.22 + 2.094F);
            float var17 = var4 * 0.65F;
            float var18 = var5 * 0.5F;
            float var19 = Math.min(var17, var18) * 0.5F;
            float var20 = var2 + var4 * 0.35F + (float)Math.cos(var9 * 0.14 + 1.2F) * var4 * 0.05F;
            float var21 = var3 + var5 * 0.5F + (float)Math.sin(var9 * 0.1 + 0.7F) * var5 * 0.04F;
            var1.handle(var20, var21, var17, var18, var19, var17 * 0.4F, var17 * 0.1F, ThemeColors.handle(var7.submit(), Math.round(2.0F * var16)));
            var24 = false;
         } finally {
            if (var24) {
               var1.compute();
               var1.apply();
            }
         }

         var1.compute();
         var1.apply();
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var1.compute();
      var1.handle(var2, var3, var4, var5, var6, var6, var6, var6);

      try {
         long var8 = (long)(var7 * 10000.0F) + 9999L;
         float var10 = 36.0F;
         float var11 = 36.0F;
         int var12 = (int)Math.ceil(var4 / var10) + 1;
         int var13 = (int)Math.ceil(var5 / var11) + 1;

         for (int var14 = 0; var14 < var13; var14++) {
            for (int var15 = 0; var15 < var12; var15++) {
               long var16 = var8 + var15 * 73856093L + var14 * 19349663L ^ 25214903917L;
               var16 = var16 * 6364136223846793005L + 1442695040888963407L;
               int var18 = (int)(var16 >>> 48 & 15L);
               if (var18 <= 5) {
                  int var19 = 3 + (var18 & 3);
                  float var20 = Math.round(var2 + var15 * var10 + (float)(var16 >>> 32 & 15L) - 8.0F);
                  float var21 = Math.round(var3 + var14 * var11 + (float)(var16 >>> 16 & 15L) - 8.0F);
                  float var22 = 1.0F + (var18 & 1);
                  int var23 = (var18 & 1) == 0 ? ThemeColors.handle(255, 255, 255, var19) : ThemeColors.handle(0, 0, 0, var19 + 1);
                  var1.handle(var20, var21, var22, var22, 0.0F, var23);
               }
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, ThemeRenderContext var9
   ) {
      float var10 = var2.handle(30.0F);
      float var11 = var4 + var7 + var2.handle(8.0F);
      float var12 = var5 + var7 + (var8 - var10) * 0.5F;
      var1.handle(var11, var12, var10, var10, var2.handle(7.0F), var3.unload() ? ModuleStateHelper.handle(var3, 0.0F) : var3.check());
      var1.handle(
         var11 + 1.0F,
         var12 + 1.0F,
         Math.max(1.0F, var10 - 2.0F),
         Math.max(1.0F, var10 - 2.0F),
         Math.max(0.0F, var2.handle(7.0F) - 1.0F),
         var3.unload() ? ModuleStateHelper.process(var3, 0.82F) : var3.select(),
         1.0F
      );
      ModuleStateHelper.handle(var1, var2, var3, var11 + var2.handle(5.0F), var12 + var2.handle(5.0F), var2.handle(9.0F), var2.handle(2.0F));
      float var13 = var2.handle(5.0F);
      float var14 = var5 + var7;
      float var15 = var11 + var10 + var13;
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var15, var14, var8, 11.0F, "Src by SoftArax", var3.animate());
      var15 += ModuleStateHelper.handle(var2, FontRegistry.instance, "Src by SoftArax", 11.0F) + var13;
      ModuleStateHelper.handle(var1, var2, FontRegistry.current, var15, var14, var8, 8.0F, "k", var3.encodePoint());
      var15 += ModuleStateHelper.handle(var2, FontRegistry.current, "k", 8.0F) + var13;
      ModuleStateHelper.handle(var1, var2, FontRegistry.state, var15, var14, var8, 11.0F, "p", var3.resolve());
      var15 += ModuleStateHelper.handle(var2, FontRegistry.state, "p", 11.0F) + var13;
      ModuleStateHelper.handle(var1, var2, FontRegistry.config, var15, var14, var8, 11.0F, "Themes", var3.load());
      float var16 = var2.handle(20.0F);
      float var17 = var4 + var6 - var2.handle(15.0F) - var16;
      float var18 = var5 + var2.handle(20.0F);
      var1.handle(var17, var18, var16, var16, var2.handle(5.0F), var3.unload() ? ModuleStateHelper.handle(var3, 0.2F) : var3.onTick());
      var1.handle(
         var17 + 1.0F,
         var18 + 1.0F,
         Math.max(1.0F, var16 - 2.0F),
         Math.max(1.0F, var16 - 2.0F),
         Math.max(0.0F, var2.handle(5.0F) - 1.0F),
         var3.unload() ? ModuleStateHelper.process(var3, 0.86F) : var3.refresh(),
         1.0F
      );
      float var19 = ModuleStateHelper.handle(var2, FontRegistry.state, "l", 14.0F);
      ModuleStateHelper.handle(var1, var2, FontRegistry.state, var17 + (var16 - var19) * 0.5F, var18, var16, 14.0F, "l", var3.animate());
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, VirtualListLayout var3, ThemeRenderContext var4) {
      GuiMetrics var5 = handle(var4);
      ThemeColors var6 = var4.apply();
      float var7 = var3.compute();
      float var8 = var5.handle(8.0F);
      float var9 = var5.handle(8.0F);
      float var10 = var3.process();
      float var11 = var3.resolve();
      float var12 = var2.submit();
      float var13 = ModuleStateHelper.compute(var5);
      List var14 = var4.execute().compute();
      List var15 = var2.handle(var4.execute());
      int var16 = var2.sendEvent() >= 0 ? var2.sendEvent() : var4.execute().handle(var2.adaptRequest());
      if (var15.isEmpty()) {
         String var17 = "Ничего не найдено";
         float var18 = ModuleStateHelper.handle(var5, FontRegistry.instance, var17, 10.0F);
         ModuleStateHelper.handle(var1, var5, FontRegistry.instance, var3.handle() + (var7 - var18) * 0.5F, var10, var11, 10.0F, var17, var6.encodePoint());
      } else {
         ModuleStateHelper.handle(
            var1,
            var5,
            var6,
            var3.handle(),
            var10,
            var7,
            var11,
            var5.handle(4.0F),
            var5.handle(4.0F),
            var9,
            var9,
            var2.computeColor().compute(),
            () -> {
               for (int var12x = 0; var12x < var15.size(); var12x++) {
                  int var13x = (Integer)var15.get(var12x);
                  ThemePaletteRegistry.ColorState var14x = (ThemePaletteRegistry.ColorState)var14.get(var13x);
                  VirtualListLayout.Bounds var15x = var3.handle(var12x, var12);
                  if (var3.handle(var15x, var13)) {
                     float var16x = compute(var15x.x());
                     float var17x = compute(var15x.y());
                     float var18x = compute(var15x.x() + var15x.width());
                     float var19 = compute(var15x.y() + var15x.height());
                     float var20 = Math.max(var5.handle(1.0F), var18x - var16x);
                     float var21 = Math.max(var5.handle(1.0F), var19 - var17x);
                     float var22 = handle(var2.handle(UiAnimationKeys.handle(var13x)), 0.0F, 1.0F);
                     float var23 = handle(var2.handle(UiAnimationKeys.process(var13x)), 0.0F, 1.0F);
                     boolean var24 = var13x == var16;
                     float var25 = var23;
                     float var26 = Math.max(var22, var25);
                     if (var26 > 0.01F) {
                        float var27 = 0.24F + var26 * 0.52F;
                        int var28;
                        if (var6.unload()) {
                           int var29 = ThemeColors.handle(54, 72, 90, 255);
                           int var30 = ThemeColors.handle(ThemeColors.handle(var14x.resolve(), var14x.update(), 0.52F), 255);
                           var28 = ThemeColors.handle(ThemeColors.handle(var29, var30, 0.11F), Math.round(12.0F * var27));
                        } else {
                           var28 = ThemeColors.handle(0, 0, 0, Math.round(26.0F * var27));
                        }

                        var1.handle(var16x, var17x, var20, var21, var8, var5.handle(4.5F) * var27, var5.handle(0.65F) * var27, var28);
                     }

                     int var55 = var6.unload()
                        ? ThemeColors.handle(ModuleStateHelper.handle(var6, 0.0F), 242)
                        : ThemeColors.handle(ThemeColors.handle(var6.execute(), var6.apply(), 0.42F), 238);
                     float var56 = var25 * (var6.unload() ? 0.05F : 0.07F);
                     float var57 = var22 * (var6.unload() ? 0.008F : 0.012F);
                     float var58 = (var6.unload() ? 0.19F : 0.15F) + var56 + var57;
                     float var31 = (var6.unload() ? 0.23F : 0.19F) + var56 + var57;
                     int var32 = ThemeColors.handle(var55, ThemeColors.handle(var14x.resolve(), var55 >>> 24 & 0xFF), var58);
                     int var33 = ThemeColors.handle(var55, ThemeColors.handle(var14x.update(), var55 >>> 24 & 0xFF), var31);
                     int var34 = this.handle(var14x);
                     float var35 = var22 > 0.001F ? handle((var2.sampleLayer() - var16x) / Math.max(1.0F, var20), 0.07F, 0.93F) : 0.5F;
                     float var36 = var22 > 0.001F ? handle((var2.sendWorld() - var17x) / Math.max(1.0F, var21), 0.1F, 0.9F) : 0.5F;
                     var1.handle(
                        var16x, var17x, var20, var21, var8, var32, var33, var14x.resolve(), var34, var35, var36, var22, Math.max(var25, var23 * 0.3F), var24, 6
                     );
                     float var37 = var5.handle(28.0F);
                     float var38 = var5.handle(14.0F);
                     float var39 = process(var22);
                     float var40 = compute(var16x + var20 - var37 - var5.handle(10.0F));
                     float var41 = compute(var17x + (var21 - var38) * 0.5F);
                     float var42 = var5.handle(3.5F);
                     float var43 = var16x + var5.handle(10.0F);
                     String var44 = var14x.process();
                     float var45 = var16x + var20 - var5.handle(10.0F);
                     float var46 = Math.max(var5.handle(34.0F), var45 - var43);
                     float var47 = handle(var5, var44, var46);
                     int var48 = (int)Math.floor(var43);
                     int var49 = (int)Math.ceil(var45);
                     int var50 = (int)Math.floor(var17x);
                     int var51 = (int)Math.ceil(var17x + var21);
                     var1.handle(var48, var50, Math.max(1, var49 - var48), Math.max(1, var51 - var50));

                     try {
                        ModuleStateHelper.handle(
                           var1,
                           var5,
                           FontRegistry.config,
                           var43,
                           var17x,
                           var21,
                           var47,
                           var44,
                           ThemeColors.handle(var6.animate(), var6.load(), var26 * 0.72F + (var24 ? 0.18F : 0.0F))
                        );
                     } finally {
                        var1.apply();
                     }

                     var1.handle(
                        var40, var41, var37, var38, var42, var14x.resolve(), var14x.update(), var14x.resolve(), var34, 0.5F, 0.5F, var39, 0.0F, false, 5
                     );
                  }
               }
            }
         );
      }
   }

   private float handle(float var1) {
      float var2 = Math.max(0.0F, Math.min(1.0F, var1));
      return (float)Math.pow(var2, 1.42F);
   }

   private float handle() {
      return (float)(System.currentTimeMillis() % 1000000L) / 1000.0F;
   }

   private void handle(RoundedRectRenderer var1, ThemePaletteRegistry.ColorState var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = compute(var3);
      float var9 = compute(var4);
      float var10 = Math.max(1.0F, compute(var3 + var5) - var8);
      float var11 = Math.max(1.0F, compute(var4 + var6) - var9);
      float var12 = Math.min(var7, Math.min(var10, var11) * 0.5F);
      int[] var13 = var2.execute();
      int var14 = handle(var13, var2.resolve(), var2.update(), 0.0F);
      int var15 = handle(var13, var2.resolve(), var2.update(), 0.34F);
      int var16 = handle(var13, var2.resolve(), var2.update(), 0.72F);
      int var17 = handle(var13, var2.resolve(), var2.update(), 1.0F);
      var1.handle(var8, var9, var10, var11, var12, var14, var15, var16, var17);
   }

   private static int handle(int[] var0, int var1, int var2, float var3) {
      if (var0 == null || var0.length < 2) {
         return ThemeColors.handle(var1, var2, var3);
      }

      if (var3 <= 0.0F) {
         return var0[0];
      }

      if (var3 >= 1.0F) {
         return var0[var0.length - 1];
      }

      float var4 = var3 * (var0.length - 1);
      int var5 = Math.min(var0.length - 2, (int)var4);
      return ThemeColors.handle(var0[var5], var0[var5 + 1], var4 - var5);
   }

   private int handle(ThemePaletteRegistry.ColorState var1) {
      Integer var2 = this.instance.get(var1);
      if (var2 != null) {
         return var2;
      }

      int var3 = process(var1);
      this.instance.put(var1, var3);
      return var3;
   }

   private static int process(ThemePaletteRegistry.ColorState var0) {
      int[] var1 = var0.execute();
      if (var1 != null && var1.length > 2) {
         return var0.update();
      }

      int var2 = var0.resolve();
      int var3 = var0.update();
      float[] var4 = Color.RGBtoHSB(var2 >>> 16 & 0xFF, var2 >>> 8 & 0xFF, var2 & 0xFF, null);
      float[] var5 = Color.RGBtoHSB(var3 >>> 16 & 0xFF, var3 >>> 8 & 0xFF, var3 & 0xFF, null);
      float var6 = Math.abs(var4[0] - var5[0]);
      var6 = Math.min(var6, 1.0F - var6);
      if (!(var4[1] < 0.14F) && !(var5[1] < 0.14F) && !(var6 > 0.035F)) {
         float var7 = (float)Math.sin((var5[0] + 0.11F) * Math.PI * 2.0) >= 0.0F ? 0.048F : -0.048F;
         float var8 = var5[0] + var7;
         if (var8 < 0.0F) {
            var8++;
         } else if (var8 >= 1.0F) {
            var8--;
         }

         float var9 = handle(var5[1] * 0.86F + 0.12F, 0.24F, 0.94F);
         float var10 = handle(var5[2] * 0.96F + 0.04F, 0.18F, 1.0F);
         int var11 = 0xFF000000 | Color.HSBtoRGB(var8, var9, var10) & 16777215;
         return ThemeColors.handle(var3, var11, 0.25F);
      } else {
         return var3;
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static float handle(GuiMetrics var0, String var1, float var2) {
      float var3 = 10.0F;

      while (var3 > 7.0F && ModuleStateHelper.handle(var0, FontRegistry.config, var1, var3) > var2) {
         var3 -= 0.25F;
      }

      return var3;
   }

   private static float process(float var0) {
      float var1 = handle(var0, 0.0F, 1.0F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float compute(float var0) {
      return Math.round(var0 * 2.0F) * 0.5F;
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4, float var5, float var6) {
      GuiMetrics var7 = handle(var4);
      ThemeColors var8 = var4.apply();
      if (!(var2.submitPoint() <= 0.5F)) {
         float var9 = var7.handle(7.0F);
         float var10 = var7.handle(3.6F);
         float var11 = var3.projectItem() + var7.save() - var9 - var10 - var7.handle(0.8F);
         float var12 = var6 - var7.handle(10.0F);
         float var13 = var5 + var7.handle(5.0F);
         float var14 = Math.max(var7.handle(28.0F), var12 * (var12 / (var12 + var2.submitPoint())));
         float var15 = Math.min(1.0F, Math.max(0.0F, -var2.submit() / var2.submitPoint()));
         float var16 = var13 + (var12 - var14) * var15;
         VirtualListLayout var17 = VirtualListLayout.handle(var3, var7);
         var1.compute();
         var1.handle(
            var17.handle(), var17.process(), var17.compute(), var17.resolve(), var7.handle(4.0F), var7.handle(4.0F), var7.handle(8.0F), var7.handle(8.0F)
         );

         try {
            ModuleStateHelper.handle(
               var1,
               var7,
               var8,
               var11,
               var13,
               var10,
               var12,
               var16,
               var14,
               var2.computeColor().compute(),
               0.0F,
               2L,
               var2.sampleLayer(),
               var2.sendWorld(),
               var2::resolve
            );
         } finally {
            var1.compute();
            var1.apply();
         }
      }
   }
}
