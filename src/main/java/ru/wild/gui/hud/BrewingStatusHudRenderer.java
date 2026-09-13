package ru.wild.gui.hud;

import java.util.List;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.misc.AutoPottBot;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "Brew Monitor", process = "i")
public final class BrewingStatusHudRenderer extends ThemePresets implements MinecraftContext {
   private static final BrewingStatusHudRenderer instance = new BrewingStatusHudRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();
   private static final int providerFetch = 6;

   private BrewingStatusHudRenderer() {
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (toggleState.player != null && toggleState.world != null) {
         responseCompute.handle();
         responseCompute.handle(AutoPottBot.source ? 1.0 : 0.0, 0.2F, Easings.handler, false);
         float var2 = responseCompute.update();
         if (!(var2 <= 0.01F)) {
            List var3 = AutoPottBot.responseCompute;
            List var4 = AutoPottBot.providerFetch;
            int[] var5 = AutoPottBot.itemProject;
            int var6 = Math.min(var3.size(), 6);
            float var7 = 252.0F;
            float var8 = 52.0F;
            float var9 = 32.0F;
            float var10 = 15.0F;
            float var11 = var4.isEmpty() ? 0.0F : 16.0F;
            float var12 = var8 + var9 + var6 * var10 + var11 + 12.0F;
            ThemeRenderer.PrimaryCacheEntry var13 = ThemeRenderer.handle().handle("HUD_BrewMonitor", 12.0F, 300.0F, var7, var12);
            float var14 = var2 * this.target.compute();
            float var15 = var13.data;
            float var16 = var13.context;
            float var17 = var13.config;
            float var18 = var13.state;
            this.handle(var15, var16, var17, var18);
            int var19 = this.update(var14);
            int var20 = this.apply(var14);
            int var21 = AutoPottBot.source ? handle(5954680, var14) : handle(8421512, var14);
            this.handle(var1, var15, var16, var17, var18, 12.0F, var14);
            float var22 = 12.0F;
            var1.process(var15 + var22 + 4.0F, var16 + 15.0F, 4.0F, 0.0F, 360.0F, var21);
            var1.handle(FontRegistry.config, var15 + var22 + 14.0F, var16 + 18.0F, 21.0F, "Brew Monitor", var19);
            String var23 = AutoPottBot.target;
            float var24 = TextMeasureCache.handle(FontRegistry.instance, var23, 14.0F).instance;
            var1.handle(FontRegistry.instance, var15 + var17 - var22 - var24, var16 + 17.0F, 14.0F, var23, var20);
            String var25 = "Варок "
               + AutoPottBot.pending
               + "   вар "
               + AutoPottBot.previous
               + "   своб "
               + AutoPottBot.latest
               + "   гот "
               + AutoPottBot.summary
               + "   зелий ≈ "
               + AutoPottBot.matrixBlend;
            var1.handle(FontRegistry.instance, var15 + var22, var16 + 36.0F, 13.0F, var25, PackedColor.handle(var20, (int)(235.0F * var14)));
            float var26 = var16 + 54.0F;
            String var27 = "Вода " + var5[0] + "    Бут " + AutoPottBot.vectorMatch + "    Нарост " + var5[1] + "    Блэйз " + var5[2];
            String var28 = "Глоу " + var5[3] + "    Сахар " + var5[4] + "    Магма " + var5[5] + "    Редст " + var5[6];
            var1.handle(FontRegistry.instance, var15 + var22, var26, 12.5F, var27, var20);
            var26 += 14.0F;
            var1.handle(FontRegistry.instance, var15 + var22, var26, 12.5F, var28, var20);
            var26 += 18.0F;
            float var29 = 84.0F;
            float var30 = var15 + var17 - var22 - var29;

            for (int var31 = 0; var31 < var6; var31++) {
               AutoPottBot.ColorStop var32 = (AutoPottBot.ColorStop)var3.get(var31);
               var1.handle(FontRegistry.instance, var15 + var22, var26 + 1.0F, 12.5F, var32.label(), var19);
               float var33 = var26 + 2.5F;
               float var34 = 4.0F;
               var1.handle(var30, var33, var29, var34, var34 / 2.0F, handle(0, var14 * 0.55F));
               float var35 = Math.max(0.0F, Math.min(1.0F, var32.progress()));
               if (var35 > 0.001F) {
                  var1.handle(var30, var33, var29 * var35, var34, var34 / 2.0F, handle(var32.color(), var14));
               }

               var26 += var10;
            }

            if (!var4.isEmpty()) {
               String var38 = "Не хватает: " + String.join(", ", var4);
               var1.handle(FontRegistry.instance, var15 + var22, var26 + 2.0F, 12.5F, var38, handle(16737392, var14));
            }

            ThemeRenderer.handle().handle(var13);
            NeoStyleOptions.handle(
               var1, this, var13, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   private static int handle(int var0, float var1) {
      int var2 = (int)(255.0F * Math.max(0.0F, Math.min(1.0F, var1)));
      return PackedColor.compute(var0 >> 16 & 0xFF, var0 >> 8 & 0xFF, var0 & 0xFF, var2);
   }
}
