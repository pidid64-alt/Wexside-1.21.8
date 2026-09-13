package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.network.PlayerListEntry;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.gui.widget.AnimatedTextLabel;
import ru.wild.modules.visuals.Hud;
import ru.wild.network.ServerTickRateTracker;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@HudElementMetadata(handle = "InformationHUD", process = "w")
public final class InformationHudRenderer extends ThemePresets implements MinecraftContext {
   private static final InformationHudRenderer instance = new InformationHudRenderer();
   private static double responseCompute = 0.0;
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();
   private static final DoubleAnimator vectorPerform = new DoubleAnimator();
   private static final DoubleAnimator eventAttach = new DoubleAnimator();
   private static boolean serverRead;
   private static final List<InformationHudRenderer.ColorStop> positionAdvance = new ArrayList<>(4);
   private final ModeSetting frameCheck = new ModeSetting("Вид", "Стандарт", "Стандарт", "Строка");
   private final BooleanSetting moduleCollect = new BooleanSetting("Показывать верхушку", true);
   private final BooleanSetting providerClose = new BooleanSetting("Анимация значений", true);
   private final Map<String, AnimatedTextLabel> presetSave = new HashMap<>();
   private final ChoiceSetting windowConvert = new ChoiceSetting(
         "Отображаемые данные",
         new BooleanSetting("Скорость (BPS)", true),
         new BooleanSetting("Тикрейт (TPS)", true),
         new BooleanSetting("Координаты (XYZ)", true),
         new BooleanSetting("Ping (MS)", true)
      )
      .process(true);

   private InformationHudRenderer() {
      this.handle(this.frameCheck);
      this.handle(this.moduleCollect);
      this.handle(this.providerClose);
      this.handle(this.windowConvert);
      HudProfileConfig.handle(this);
   }

   public static InformationHudRenderer process() {
      return instance;
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   public void process(RoundedRectRenderer var1) {
      if (toggleState.player != null) {
         boolean var2 = false;
         boolean var3 = true;
         profileDraw.handle();
         providerFetch.handle();
         profileDraw.handle(var3 ? 1.0 : 0.0, 0.18F, Easings.handler, false);
         if (var3) {
            if (!serverRead) {
               providerFetch.apply(-10.0);
            }

            providerFetch.handle(0.0, 0.2F, Easings.handler, false);
         } else {
            if (serverRead) {
               providerFetch.apply(0.0);
            }

            providerFetch.handle(10.0, 0.2F, Easings.handler, false);
         }

         serverRead = var3;
         float var4 = profileDraw.update();
         if (!(var4 <= 0.01F)) {
            float var5 = 24.0F;
            boolean var6 = this.moduleCollect.compute();
            boolean var7 = Hud.render();
            HudElementRegistry.ColorState var8 = var7 ? HudElementRegistry.handle("HUD_Info") : null;
            float var9 = var7 ? var8.current : 7.0F;
            float var10 = var6 ? (var7 ? var8.mode : 32.0F) : 0.0F;
            float var11 = var6 ? (var7 ? var8.active : 5.0F) : 0.0F;
            float var12 = 22.0F;
            float var13 = var7 ? var8.selection : 22.0F;
            float var14 = var7 ? Math.max(4.0F, var8.current + 3.0F) : 10.0F;
            positionAdvance.clear();
            if (this.windowConvert.process("Скорость (BPS)")) {
               positionAdvance.add(new InformationHudRenderer.ColorStop("BPS", encodePoint(), PackedColor.compute(255, 90, 90, 255)));
            }

            if (this.windowConvert.process("Тикрейт (TPS)")) {
               positionAdvance.add(new InformationHudRenderer.ColorStop("TPS", handle(ServerTickRateTracker.handle()), PackedColor.compute(255, 170, 40, 255)));
            }

            if (this.windowConvert.process("Координаты (XYZ)")) {
               positionAdvance.add(new InformationHudRenderer.ColorStop("XYZ", animate(), PackedColor.compute(100, 255, 100, 255)));
            }

            if (this.windowConvert.process("Ping (MS)")) {
               positionAdvance.add(new InformationHudRenderer.ColorStop("PING", load(), PackedColor.compute(120, 190, 255, 255)));
            }

            if (this.frameCheck.process("Строка")) {
               this.handle(var1, positionAdvance, var4);
            } else {
               String var15 = "Information";
               float var16 = TextMeasureCache.handle(FontRegistry.config, var15, var7 ? var8.enabled : 26.0F).instance;
               float var17 = var14 * 2.0F + 30.0F;

               for (InformationHudRenderer.ColorStop var19 : positionAdvance) {
                  float var20 = TextMeasureCache.handle(FontRegistry.instance, var19.label, var5).instance
                     + TextMeasureCache.handle(FontRegistry.instance, var19.value, var5).instance
                     + var14 * 2.0F
                     + 20.0F;
                  var17 = Math.max(var17, var20);
               }

               float var72 = positionAdvance.size() * var13 + 12.0F;
               float var73 = var17 + var9 * 2.0F;
               if (var6) {
                  float var74 = var16 + var12 + var14 * 2.0F + 24.0F;
                  var73 = Math.max(var73, var74 + var9 * 2.0F);
               }

               var17 = var73 - var9 * 2.0F;
               float var75 = var9 + var72 + var9;
               if (var6) {
                  var75 = var9 + var10 + var11 + var72 + var9;
               }

               vectorPerform.handle();
               eventAttach.handle();
               vectorPerform.handle(var73, 0.18F, Easings.handler, false);
               eventAttach.handle(var75, 0.18F, Easings.handler, false);
               float var21 = vectorPerform.update();
               float var22 = eventAttach.update();
               float var23 = 10.0F;
               float var24 = 10.0F;
               ThemeRenderer.PrimaryCacheEntry var25 = ThemeRenderer.handle().handle("HUD_Info", var23, var24, var21, var22);
               float var26 = var25.data + providerFetch.update();
               float var27 = var25.context;
               float var28 = Math.max(1.0F, var25.config);
               float var29 = Math.max(1.0F, var25.state);
               boolean var30 = this.handle(var28, var29, var21, var22, positionAdvance, var6);
               float var31 = var30 ? process(var29, 38.0F, 54.0F) : process(var29, Math.max(34.0F, var22 * 0.76F), Math.max(var22, var22 * 1.08F));
               var25 = ThemeRenderer.handle().handle(var25, var28, var31, var21, var22);
               var26 = var25.data + providerFetch.update();
               var27 = var25.context;
               var28 = var25.config;
               var31 = var25.state;
               var30 = this.handle(var28, var31, var21, var22, positionAdvance, var6);
               this.handle(var26, var27, var28, var31);
               float var32 = process(var28 / Math.max(1.0F, var21), 0.76F, 1.28F);
               float var33 = var30 ? 1.0F : process(var31 / Math.max(1.0F, var22), 0.78F, 1.08F);
               float var34 = process(Math.min(var32, var33), 0.78F, 1.12F);
               float var35 = process(var9 * var32, 5.0F, 10.0F);
               float var36 = process(var9 * var33, 5.0F, 9.0F);
               float var37 = var6 ? process(var10 * var33, 24.0F, 36.0F) : 0.0F;
               float var38 = var6 ? process(var11 * var33, 3.0F, 7.0F) : 0.0F;
               float var39 = process(var13 * var33, 17.0F, 26.0F);
               float var40 = process(var14 * var32, 7.0F, 13.0F);
               float var41 = process(var5 * var34, 18.0F, 27.0F);
               float var42 = var4 * this.target.compute();
               int var43 = (int)(255.0F * var42);
               int var44 = this.handle(var42);
               int var45 = this.process(var42);
               int var46 = this.compute(var42);
               int var47 = this.resolve(var42);
               int var48 = this.update(var42);
               int var49 = this.execute(var42);
               boolean var50 = this.select();
               if (var30) {
                  this.handle(var1, var26, var27, var28, var31, var15, var6, positionAdvance, var42, var48, var49);
                  ThemeRenderer.handle().handle(var25);
                  NeoStyleOptions.handle(
                     var1, this, var25, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight()
                  );
               } else {
                  float var51 = var7 ? var8.instance : process(Math.min(var28, var31) * 0.16F, 9.0F, 14.0F);
                  float var52 = var7 ? var8.data : 11.0F;
                  float var53 = var7 ? var8.context : (var6 ? 8.0F : 11.0F);
                  float var54 = Math.max(1.0F, var28 - var35 * 2.0F);
                  this.handle(var1, var26, var27, var28, var31, var51, var42);
                  if (var6) {
                     if (var50 || this.refresh() || this.render()) {
                        this.handle(var1, var26 + var35, var27 + var36, var54, var37, var52, var42);
                     } else if (var7) {
                        var1.handle(var26 + var35, var27 + var36, var54, var37, var52, var45);
                     } else {
                        var1.handle(var26 + var35, var27 + var36, var54, var37, 11.0F, 11.0F, 4.0F, 4.0F, var45);
                     }

                     float var55 = process(var12 * var33, 18.0F, 24.0F);
                     float var56 = var26 + var35 + var54 - process(10.0F * var32, 8.0F, 12.0F) - var55;
                     float var57 = var27 + var36 + (var37 - var55) / 2.0F;
                     if (var7) {
                        float var58 = var26 + var8.pointEncode.instance * var32;
                        float var59 = var27 + var8.pointEncode.data * var33;
                        var1.handle(FontRegistry.config, var58, var59, var8.enabled * var34, var15, var48);
                        float var60 = var8.renderer * var34;
                        float var61 = TextMeasureCache.handle(FontRegistry.state, "e", var60).instance;
                        float var62 = (var8.animator.context ? var26 + var28 : var26) + var8.animator.instance * var32;
                        float var63 = var27 + var8.animator.data * var33;
                        var1.handle(FontRegistry.state, var62, var63, var60, "e", var49);
                     } else {
                        float var86 = this.handle(FontRegistry.config, var15, process(26.0F * var34, 18.0F, 29.0F), Math.max(18.0F, var54 - var12 - 24.0F));
                        float var89 = var26 + var35 + process(10.0F * var32, 8.0F, 12.0F);
                        float var91 = Math.max(1.0F, var56 - var89 - 4.0F);
                        var1.handle(var89, var27 + var36, var91, var37, 0.0F, 0.0F, 0.0F, 0.0F);
                        var1.handle(FontRegistry.config, var89, handle(var27 + var36, var37, var86), var86, var15, var48);
                        var1.apply();
                        float var93 = process((var5 + 4.0F) * var34, 22.0F, 30.0F);
                        float var95 = TextMeasureCache.handle(FontRegistry.state, "e", var93).instance;
                        var1.handle(FontRegistry.state, var56 + (var55 - var95) / 2.0F, handle(var57, var55, var93), var93, "e", var49);
                     }
                  }

                  float var82 = var27 + var36 + var37 + var38;
                  if (!var6) {
                     var82 = var27 + var36;
                  }

                  float var84 = var26 + var35 + (var7 ? var8.source.instance * var32 : 0.0F);
                  var82 += var7 ? var8.source.data * var33 : 0.0F;
                  float var85;
                  if (var7) {
                     var85 = var72 * var33;
                  } else {
                     float var87 = Math.max(1.0F, var31 - var36 * 2.0F - var37 - var38);
                     var85 = Math.min(Math.max(12.0F, positionAdvance.size() * var39 + 12.0F), var87);
                  }

                  if (this.prepare() || var50 || this.refresh() || this.render()) {
                     if (var50 || this.refresh() || this.render()) {
                        this.process(var1, var84, var82, var54, var85, var53, var42);
                     } else if (var7) {
                        var1.handle(var84, var82, var54, var85, var53, var46);
                     } else {
                        var1.handle(var84, var82, var54, var85, var6 ? 4.0F : 11.0F, var6 ? 4.0F : 11.0F, 11.0F, 11.0F, var46);
                     }
                  }

                  var1.handle(var26, var27, var28, var31, var51, var51, var51, var51);
                  float var88 = var82 + Math.max(6.0F, (var85 - positionAdvance.size() * var39) * 0.5F);
                  int var90 = PackedColor.handle(this.update(1.0F), var43);

                  for (InformationHudRenderer.ColorStop var94 : positionAdvance) {
                     if (var7) {
                        var1.handle(FontRegistry.instance, var84 + var40, var88 + var39 / 2.0F + 3.0F * var33, var41, var94.label, var90);
                        int var96 = PackedColor.update(var94.valColor, var43);
                        float var98 = var84 + var54 - var40 - TextMeasureCache.handle(FontRegistry.instance, var94.value, var41).instance;
                        this.handle(
                           var1,
                           var94.label,
                           var94.value,
                           FontRegistry.instance,
                           var84,
                           var88,
                           var54,
                           var39,
                           var98,
                           var88 + var39 / 2.0F + 3.0F * var33,
                           var41,
                           var96
                        );
                     } else {
                        float var97 = Math.max(1.0F, var54 - var40 * 2.0F);
                        float var99 = this.handle(var94, var41, var97, process(var41 * 0.34F, 5.0F, 8.0F));
                        float var64 = handle(var88, var39, var99);
                        float var65 = var26 + var35 + var40;
                        float var66 = TextMeasureCache.handle(FontRegistry.instance, var94.label, var99).instance;
                        float var67 = TextMeasureCache.handle(FontRegistry.instance, var94.value, var99).instance;
                        float var68 = var26 + var35 + var40 + var97 - var67;
                        float var69 = var65 + var66 + process(var99 * 0.34F, 5.0F, 8.0F);
                        if (var68 < var69) {
                           var68 = var69;
                        }

                        var1.handle(var65 - 1.0F, var88, var97 + 2.0F, var39, 0.0F, 0.0F, 0.0F, 0.0F);
                        var1.handle(FontRegistry.instance, var65, var64, var99, var94.label, var90);
                        int var70 = PackedColor.update(var94.valColor, var43);
                        this.handle(var1, var94.label, var94.value, FontRegistry.instance, var65 - 1.0F, var88, var97 + 2.0F, var39, var68, var64, var99, var70);
                        var1.apply();
                     }

                     var88 += var39;
                  }

                  var1.apply();
                  ThemeRenderer.handle().handle(var25);
                  NeoStyleOptions.handle(
                     var1, this, var25, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight()
                  );
               }
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, List<InformationHudRenderer.ColorStop> var2, float var3) {
      float var4 = 22.0F;
      float var5 = 6.0F;
      float var6 = 15.0F;
      float var7 = 12.0F;
      float var8 = 28.0F;
      float var9 = 0.0F;

      for (int var10 = 0; var10 < var2.size(); var10++) {
         InformationHudRenderer.ColorStop var11 = (InformationHudRenderer.ColorStop)var2.get(var10);
         var9 += TextMeasureCache.handle(FontRegistry.instance, var11.label, var4).instance
            + var5
            + TextMeasureCache.handle(FontRegistry.instance, var11.value, var4).instance;
         if (var10 < var2.size() - 1) {
            var9 += var6;
         }
      }

      float var34 = var9 + var7 * 2.0F;
      vectorPerform.handle();
      eventAttach.handle();
      vectorPerform.handle(var34, 0.18F, Easings.handler, false);
      eventAttach.handle(var8, 0.18F, Easings.handler, false);
      float var35 = vectorPerform.update();
      float var12 = eventAttach.update();
      ThemeRenderer.PrimaryCacheEntry var13 = ThemeRenderer.handle().handle("HUD_Info_Row", 10.0F, 10.0F, var35, var12);
      float var14 = Math.max(1.0F, var13.config);
      float var15 = Math.max(1.0F, var13.state);
      var13 = ThemeRenderer.handle().handle(var13, var14, var15, var35, var12);
      float var16 = var13.data + providerFetch.update();
      float var17 = var13.context;
      var14 = var13.config;
      var15 = var13.state;
      this.handle(var16, var17, var14, var15);
      float var18 = process(var14 / Math.max(1.0F, var35), 0.6F, 3.5F);
      float var19 = var4 * var18;
      float var20 = var5 * var18;
      float var21 = var6 * var18;
      float var22 = var7 * var18;
      float var23 = 0.0F;

      for (int var24 = 0; var24 < var2.size(); var24++) {
         InformationHudRenderer.ColorStop var25 = (InformationHudRenderer.ColorStop)var2.get(var24);
         var23 += TextMeasureCache.handle(FontRegistry.instance, var25.label, var19).instance
            + var20
            + TextMeasureCache.handle(FontRegistry.instance, var25.value, var19).instance;
         if (var24 < var2.size() - 1) {
            var23 += var21;
         }
      }

      float var39 = var3 * this.target.compute();
      float var40 = process(var15 * 0.32F, 8.0F, 16.0F);
      this.handle(var1, var16, var17, var14, var15, var40, var39);
      var1.handle(var16, var17, var14, var15, var40, var40, var40, var40);
      float var26 = var16 + Math.max(var22, (var14 - var23) * 0.5F);
      float var27 = handle(var17, var15, var19);
      int var28 = PackedColor.handle(this.apply(1.0F), (int)(255.0F * var39));
      int var29 = (int)(255.0F * var39);

      for (InformationHudRenderer.ColorStop var31 : var2) {
         float var32 = TextMeasureCache.handle(FontRegistry.instance, var31.label, var19).instance;
         float var33 = TextMeasureCache.handle(FontRegistry.instance, var31.value, var19).instance;
         var1.handle(FontRegistry.instance, var26, var27, var19, var31.label, var28);
         var26 += var32 + var20;
         this.handle(
            var1, var31.label, var31.value, FontRegistry.instance, var16, var17, var14, var15, var26, var27, var19, PackedColor.update(var31.valColor, var29)
         );
         var26 += var33 + var21;
      }

      var1.apply();
      ThemeRenderer.handle().handle(var13);
      NeoStyleOptions.handle(var1, this, var13, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight());
   }

   private boolean handle(float var1, float var2, float var3, float var4, List<InformationHudRenderer.ColorStop> var5, boolean var6) {
      float var7 = var1 / Math.max(1.0F, var2);
      float var8 = var1 / Math.max(1.0F, var3);
      float var9 = this.handle(var5, var6);
      return var1 >= Math.max(190.0F, var9 * 0.86F) && var7 >= 2.35F && var8 >= 1.16F;
   }

   private float handle(List<InformationHudRenderer.ColorStop> var1, boolean var2) {
      float var3 = 22.0F;
      float var4 = var2 ? TextMeasureCache.handle(FontRegistry.config, "Information", 22.0F).instance + 36.0F : 0.0F;

      for (InformationHudRenderer.ColorStop var6 : var1) {
         var4 += TextMeasureCache.handle(FontRegistry.instance, var6.label, var3).instance;
         var4 += TextMeasureCache.handle(FontRegistry.instance, var6.value, var3).instance;
         var4 += 26.0F;
      }

      return var4 + 28.0F;
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      String var6,
      boolean var7,
      List<InformationHudRenderer.ColorStop> var8,
      float var9,
      int var10,
      int var11
   ) {
      float var12 = process(var5, 38.0F, 54.0F);
      float var13 = var3 + (var5 - var12) * 0.5F;
      float var14 = process(var12 * 0.28F, 10.0F, 14.0F);
      this.handle(var1, var2, var13, var4, var12, var14, var9);
      float var15 = process(var4 * 0.033F, 13.0F, 22.0F);
      float var16 = process(var12 * 0.18F, 6.0F, 9.0F);
      float var17 = var2 + var15;
      float var18 = var13 + var16;
      float var19 = Math.max(1.0F, var4 - var15 * 2.0F);
      float var20 = Math.max(1.0F, var12 - var16 * 2.0F);
      float var21 = process(var20 * 0.82F, 21.0F, 30.0F);
      float var22 = process(var20 * 0.78F, 21.0F, 29.0F);
      var1.handle(var2, var13, var4, var12, var14, var14, var14, var14);
      float var23 = var17;
      float var24 = var19;
      if (var7) {
         float var25 = process(var22 + 1.0F, 21.0F, 30.0F);
         float var26 = TextMeasureCache.handle(FontRegistry.config, var6, var22).instance;
         float var27 = TextMeasureCache.handle(FontRegistry.state, "e", var25).instance;
         boolean var28 = var19 >= 300.0F;
         float var29 = process(var26 + (var28 ? var27 + 15.0F : 0.0F), 78.0F, Math.min(var19 * 0.28F, 142.0F));
         float var30 = var17;
         var1.handle(FontRegistry.config, var30, handle(var18, var20, var22), var22, var6, var10);
         if (var28) {
            var1.handle(FontRegistry.state, var17 + var29 - var27, handle(var18, var20, var25), var25, "e", var11);
         }

         var23 += var29 + process(var4 * 0.018F, 8.0F, 16.0F);
         var24 = Math.max(1.0F, var17 + var19 - var23);
      }

      int var39 = Math.max(1, var8.size());
      float var40 = process(var21 * 0.42F, 7.0F, 12.0F);
      float var41 = var24 / var39;
      int var42 = PackedColor.handle(this.apply(1.0F), (int)(255.0F * var9));

      for (int var43 = 0; var43 < var8.size(); var43++) {
         InformationHudRenderer.ColorStop var44 = (InformationHudRenderer.ColorStop)var8.get(var43);
         float var31 = var23 + var41 * var43;
         float var32 = this.handle(var44, var21, Math.max(20.0F, var41 - 8.0F), var40);
         float var33 = TextMeasureCache.handle(FontRegistry.instance, var44.label, var32).instance;
         float var34 = TextMeasureCache.handle(FontRegistry.instance, var44.value, var32).instance;
         float var35 = var33 + var40 + var34;
         float var36 = var31 + Math.max(3.0F, (var41 - var35) * 0.5F);
         float var37 = handle(var18, var20, var32);
         int var38 = PackedColor.update(var44.valColor, (int)(255.0F * var9));
         var1.handle(var31 + 1.0F, var13, Math.max(1.0F, var41 - 2.0F), var12, 0.0F, 0.0F, 0.0F, 0.0F);
         var1.handle(FontRegistry.instance, var36, var37, var32, var44.label, var42);
         this.handle(
            var1,
            var44.label,
            var44.value,
            FontRegistry.instance,
            var31 + 1.0F,
            var13,
            Math.max(1.0F, var41 - 2.0F),
            var12,
            var36 + var33 + var40,
            var37,
            var32,
            var38
         );
         var1.apply();
      }

      var1.apply();
   }

   private void handle(
      RoundedRectRenderer var1,
      String var2,
      String var3,
      FontObject var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12
   ) {
      if (!this.providerClose.compute()) {
         var1.handle(var4, var9, var10, var11, var3, var12);
      } else {
         AnimatedTextLabel var13 = this.presetSave.get(var2);
         if (var13 == null) {
            var13 = new AnimatedTextLabel();
            this.presetSave.put(var2, var13);
         }

         var13.handle(var3);
         var13.process(var1, var4, var5, var6, var7, var8, Math.min(var8 * 0.45F, 6.0F), var9, var10, var11, var12);
      }
   }

   private float handle(InformationHudRenderer.ColorStop var1, float var2, float var3, float var4) {
      float var5 = var2;

      for (int var6 = 0; var6 < 8; var6++) {
         float var7 = TextMeasureCache.handle(FontRegistry.instance, var1.label, var5).instance;
         float var8 = TextMeasureCache.handle(FontRegistry.instance, var1.value, var5).instance;
         float var9 = var7 + var4 + var8;
         if (var9 <= var3 || var5 <= 16.0F) {
            return var5;
         }

         var5 = Math.max(16.0F, var5 * Math.max(0.72F, var3 / Math.max(1.0F, var9)));
      }

      return var5;
   }

   private float handle(FontObject var1, String var2, float var3, float var4) {
      float var5 = var3;

      for (int var6 = 0; var6 < 8; var6++) {
         float var7 = TextMeasureCache.handle(var1, var2, var5).instance;
         if (var7 <= var4 || var5 <= 16.0F) {
            return var5;
         }

         var5 = Math.max(16.0F, var5 * Math.max(0.72F, var4 / Math.max(1.0F, var7)));
      }

      return var5;
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + var1 * 0.5F + var2 * 0.18F;
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static String encodePoint() {
      double var0 = 0.0;
      if (toggleState.player != null) {
         double var2 = toggleState.player.getX() - toggleState.player.lastX;
         double var4 = toggleState.player.getZ() - toggleState.player.lastZ;
         var0 = Math.sqrt(var2 * var2 + var4 * var4) * 20.0;
      }

      responseCompute = responseCompute + (var0 - responseCompute) * 0.1;
      return process(responseCompute);
   }

   private static String animate() {
      if (toggleState.player != null) {
         int var0 = (int)toggleState.player.getX();
         int var1 = (int)toggleState.player.getY();
         int var2 = (int)toggleState.player.getZ();
         return var0 + " " + var1 + " " + var2;
      } else {
         return "0 0 0";
      }
   }

   private static String load() {
      if (toggleState.player != null && toggleState.getNetworkHandler() != null) {
         PlayerListEntry var0 = toggleState.getNetworkHandler().getPlayerListEntry(toggleState.player.getUuid());
         return var0 == null ? "0 ms" : var0.getLatency() + " ms";
      } else {
         return "0 ms";
      }
   }

   private static String handle(double var0) {
      int var2 = (int)Math.round(var0 * 10.0);
      int var3 = var2 / 10;
      int var4 = Math.abs(var2 % 10);
      return var3 + "." + var4;
   }

   private static String process(double var0) {
      int var2 = (int)Math.round(var0 * 100.0);
      int var3 = var2 / 100;
      int var4 = Math.abs(var2 % 100);
      return var3 + (var4 < 10 ? ".0" : ".") + var4;
   }

   record ColorStop(String label, String value, int valColor) {
   }
}
