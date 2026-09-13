package ru.wild.gui.hud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.render.shader.WildLogoRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "AutoBuyInfoHUD", process = "")
public final class StatusHudRenderer extends ThemePresets implements MinecraftContext {
   private static final StatusHudRenderer instance = new StatusHudRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final List<StatusHudRenderer.ColorStop> vectorPerform = new ArrayList<>(8);
   private static final SimpleDateFormat eventAttach = new SimpleDateFormat("HH:mm:ss");

   private StatusHudRenderer() {
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   public void process(RoundedRectRenderer var1) {
      if (toggleState.player != null && AutoBuy.source != null) {
         AutoBuy var2 = AutoBuy.source;
         vectorPerform.clear();
         vectorPerform.add(
            new StatusHudRenderer.ColorStop(
               "Статус", var2.enabled ? "ON" : "OFF", var2.enabled ? PackedColor.compute(100, 255, 140, 255) : PackedColor.compute(255, 90, 90, 255)
            )
         );
         vectorPerform.add(new StatusHudRenderer.ColorStop("Режим", var2.latest.compute(), PackedColor.compute(120, 190, 255, 255)));
         vectorPerform.add(new StatusHudRenderer.ColorStop("Время", process(process()), PackedColor.compute(255, 255, 255, 255)));
         vectorPerform.add(new StatusHudRenderer.ColorStop("Сделки", String.valueOf(AutoBuy.drawAnimation()), PackedColor.compute(255, 190, 80, 255)));
         vectorPerform.add(new StatusHudRenderer.ColorStop("Предметы", String.valueOf(AutoBuy.encodePoint()), PackedColor.compute(255, 190, 80, 255)));
         vectorPerform.add(new StatusHudRenderer.ColorStop("Потрачено", handle(AutoBuy.animate()), PackedColor.compute(255, 120, 120, 255)));
         vectorPerform.add(new StatusHudRenderer.ColorStop("Баланс", encodePoint(), PackedColor.compute(160, 220, 255, 255)));
         vectorPerform.add(
            new StatusHudRenderer.ColorStop(
               "Окуп", animate(), AutoBuy.textureRun > 0L ? PackedColor.compute(100, 255, 140, 255) : PackedColor.compute(180, 180, 180, 255)
            )
         );
         responseCompute.handle();
         responseCompute.handle(1.0, 0.2F, Easings.handler, false);
         float var3 = responseCompute.update();
         if (!(var3 <= 0.01F)) {
            float var4 = 22.0F;
            float var5 = 7.0F;
            float var6 = 32.0F;
            float var7 = 22.0F;
            float var8 = 5.0F;
            float var9 = 28.0F;
            String var10 = "AutoBuy";
            float var11 = 0.0F;
            float var12 = 0.0F;

            for (StatusHudRenderer.ColorStop var14 : vectorPerform) {
               var11 = Math.max(var11, TextMeasureCache.process(FontRegistry.instance, var14.label(), var4));
               var12 = Math.max(var12, TextMeasureCache.process(FontRegistry.instance, var14.value(), var4));
            }

            float var53 = var11 + var12 + 20.0F + 22.0F;
            float var54 = TextMeasureCache.process(FontRegistry.config, var10, var9) + 42.0F;
            float var15 = Math.max(var53, var54) + var5 * 2.0F;
            float var16 = vectorPerform.size() * var7 + 10.0F;
            float var17 = var5 + var6 + var8 + var16 + var5;
            providerFetch.handle();
            profileDraw.handle();
            providerFetch.handle(var15, 0.18F, Easings.handler, false);
            profileDraw.handle(var17, 0.18F, Easings.handler, false);
            float var18 = providerFetch.update();
            float var19 = profileDraw.update();
            float var20 = 10.0F;
            float var21 = 155.0F;
            ThemeRenderer.PrimaryCacheEntry var22 = ThemeRenderer.handle().handle("HUD_AutoBuyInfo", var20, var21, var18, var19);
            float var23 = var22.data;
            float var24 = var22.context;
            float var25 = var22.config;
            float var26 = var22.state;
            this.handle(var23, var24, var25, var26);
            float var27 = var25 / Math.max(1.0F, var18);
            float var28 = var26 / Math.max(1.0F, var19);
            float var29 = Math.min(var27, var28);
            float var30 = var5 * var27;
            float var31 = var5 * var28;
            float var32 = var6 * var28;
            float var33 = var7 * var28;
            float var34 = var4 * var29;
            float var35 = var3 * this.target.compute();
            int var36 = (int)(255.0F * var35);
            int var37 = this.handle(var35);
            int var38 = this.process(var35);
            int var39 = this.compute(var35);
            int var40 = this.resolve(var35);
            int var41 = PackedColor.handle(this.update(1.0F), var36);
            int var42 = PackedColor.handle(this.apply(1.0F), var36);
            boolean var43 = this.select();
            float var44 = 14.0F;
            float var45 = var25 - var30 * 2.0F;
            this.handle(var1, var23, var24, var25, var26, var44, var35);
            if (var43) {
               this.handle(var1, var23 + var30, var24 + var31, var45, var32, 11.0F, var35);
            } else {
               var1.handle(var23 + var30, var24 + var31, var45, var32, 11.0F, 11.0F, 4.0F, 4.0F, var38);
            }

            var1.handle(FontRegistry.config, var23 + var30 + 10.0F * var27, var24 + var31 + var32 * 0.5F + 6.0F * var28, var9 * var29, var10, var41);
            var1.compute();
            float var46 = Math.min(var32, 32.0F * var29);
            WildLogoRenderer.handle(
               var23 + var30 + var45 - 18.0F * var27 - var46 * 0.5F,
               var24 + var31 + var32 * 0.5F - var46 * 0.5F,
               var46,
               this.prepare(1.0F),
               this.check(1.0F),
               var35,
               this.tick()
            );
            float var47 = var24 + var31 + var32 + var8 * var28;
            if (this.prepare() || var43) {
               if (var43) {
                  this.process(var1, var23 + var30, var47, var45, var16 * var28, 8.0F, var35);
               } else {
                  var1.handle(var23 + var30, var47, var45, var16 * var28, 4.0F, 4.0F, 11.0F, 11.0F, var39);
               }
            }

            var1.handle(var23, var24, var25, var26, var44, var44, var44, var44);
            float var48 = var47 + 5.0F * var28;

            for (StatusHudRenderer.ColorStop var50 : vectorPerform) {
               var1.handle(FontRegistry.instance, var23 + var30 + 10.0F * var27, var48 + var33 * 0.5F + 4.0F * var28, var34, var50.label(), var42);
               int var51 = PackedColor.handle(var50.color(), var36);
               float var52 = TextMeasureCache.process(FontRegistry.instance, var50.value(), var34);
               var1.handle(
                  FontRegistry.instance, var23 + var25 - var30 - 10.0F * var27 - var52, var48 + var33 * 0.5F + 4.0F * var28, var34, var50.value(), var51
               );
               var48 += var33;
            }

            var1.apply();
            ThemeRenderer.handle().handle(var22);
            NeoStyleOptions.handle(
               var1, this, var22, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   private static long process() {
      return AutoBuy.scaleSave <= 0L ? 0L : Math.max(0L, System.currentTimeMillis() - AutoBuy.scaleSave);
   }

   private static String encodePoint() {
      if (AutoBuy.colorCompute > 0L && AutoBuy.scaleAdapt > 0L) {
         long var0 = AutoBuy.load();
         return handle(AutoBuy.scaleAdapt) + " (" + (var0 >= 0L ? "+" : "") + handle(var0) + ")";
      } else {
         return "N/A";
      }
   }

   private static String animate() {
      if (AutoBuy.textureRun > 0L && AutoBuy.scaleSave > 0L) {
         return eventAttach.format(new Date(AutoBuy.textureRun)) + " (" + process(AutoBuy.textureRun - AutoBuy.scaleSave) + ")";
      } else {
         return AutoBuy.animate() <= 0L ? "-" : "ожидание";
      }
   }

   private static String handle(long var0) {
      long var2 = Math.abs(var0);
      String var4 = String.format(Locale.ROOT, "%,d", var2).replace(',', ' ') + "¤";
      return var0 < 0L ? "-" + var4 : var4;
   }

   private static String process(long var0) {
      long var2 = Math.max(0L, var0 / 1000L);
      long var4 = var2 / 3600L;
      long var6 = var2 % 3600L / 60L;
      long var8 = var2 % 60L;
      return var4 > 0L ? String.format(Locale.ROOT, "%d:%02d:%02d", var4, var6, var8) : String.format(Locale.ROOT, "%02d:%02d", var6, var8);
   }

   record ColorStop(String label, String value, int color) {
   }
}
