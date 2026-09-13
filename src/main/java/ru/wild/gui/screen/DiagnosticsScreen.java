package ru.wild.gui.screen;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import ru.wild.core.DiagnosticSnapshot;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.WildLogoRenderer;
import ru.wild.util.math.DampedFloatTracker;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;

public final class DiagnosticsScreen {
   private static final String instance = "Диагностика Wild Core";
   private static final String data = "рендер, шейдеры, GL и локальные слепки";
   private static final String context = "Слепок";
   private static final String config = "Папка";
   private static final String state = "Логи";
   private static final String cache = "Состояние";
   private static final String output = "Tracker ID";
   private static final String current = "Code";
   private static final String active = "Очередь";
   private static final String mode = "Ошибки";
   private static final String selection = "CFI chain";
   private static final String enabled = "Текстурные Юниты";
   private static final String renderer = "Матрицы";
   private static final String handler = "Frames";
   private static final String animationDraw = "Anomalies";
   private static final String pointEncode = "Что сейчас ломается";
   private static final String animator = "Файл слепка";
   private static final String source = "Mixin policy";
   private static final String target = "Privacy";
   private static final String pending = "Гайдлайн";
   private static final String previous = "1 смотри Code/Stage";
   private static final String latest = "2 жми Слепок";
   private static final String summary = "3 открой Логи";
   private static final String matrixBlend = "4 передай Tracker ID";
   private static final String vectorMatch = "Шейдерных исключений нет";
   private static final String itemProject = "Нажми Логи, чтобы загрузить latest.log";
   private static final String responseCompute = "Встроенный viewer";
   private static final String providerFetch = "latest.log tail";
   private static final String profileDraw = "Буфер событий";
   private static final String vectorPerform = "Core Load";
   private static final String eventAttach = "Render TPS";
   private static final String serverRead = "Latency";
   private static final float positionAdvance = 44.0F;
   private static final float frameCheck = 10.0F;
   private static final SpringAnimationSpec moduleCollect = SpringAnimationSpec.refresh();
   private final DiagnosticSnapshot providerClose = new DiagnosticSnapshot();
   private final DiagnosticsScreen.CacheEntry presetSave = new DiagnosticsScreen.CacheEntry();
   private final DampedFloatTracker windowConvert = new DampedFloatTracker(0.0F);
   private long presetWrite = Long.MIN_VALUE;
   private static float colorMeasure;
   private static float animationSchedule;
   private static float rendererScan;
   private static float sourceBuild;
   private static float outputCollapse;
   private static float profileInvoke;
   private static float sourceSchedule;
   private static float timerRender;

   public static float handle(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(render(var0, var1) + drawAnimation(var0, var1) - handle(var1) - process(var1) - compute(var1) - var1.handle(16.0F));
   }

   public static float process(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(render(var0, var1) + drawAnimation(var0, var1) - process(var1) - compute(var1) - var1.handle(8.0F));
   }

   public static float compute(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(render(var0, var1) + drawAnimation(var0, var1) - compute(var1));
   }

   public static float resolve(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(tick(var0, var1) + var1.handle(3.0F));
   }

   public static float handle(GuiMetrics var0) {
      return var0.handle(94.0F);
   }

   public static float process(GuiMetrics var0) {
      return var0.handle(78.0F);
   }

   public static float compute(GuiMetrics var0) {
      return var0.handle(68.0F);
   }

   public static float resolve(GuiMetrics var0) {
      return var0.handle(24.0F);
   }

   public static boolean handle(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3) {
      return ModuleStateHelper.handle(var2, var3, update(var0, var1), apply(var0, var1), execute(var0, var1), prepare(var0, var1));
   }

   public static boolean process(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3) {
      if (!(sourceSchedule <= 0.5F) && !(rendererScan <= 1.0F) && !(sourceBuild <= 1.0F)) {
         float var4 = update(var1);
         float var5 = Math.round(animationSchedule + sourceBuild - var4);
         return ModuleStateHelper.handle(var2, var3, colorMeasure, var5 - var1.handle(4.0F), rendererScan, var1.handle(12.0F));
      } else {
         return false;
      }
   }

   public static boolean compute(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3) {
      if (!(timerRender <= 0.5F) && !(rendererScan <= 1.0F) && !(sourceBuild <= 1.0F)) {
         float var4 = update(var1);
         float var5 = Math.round(colorMeasure + rendererScan - var4);
         return ModuleStateHelper.handle(var2, var3, var5 - var1.handle(4.0F), animationSchedule, var1.handle(12.0F), sourceBuild);
      } else {
         return false;
      }
   }

   public static float handle(ViewportLayoutState var0, GuiMetrics var1, float var2) {
      float var3 = apply(var1);
      float var4 = Math.max(1.0F, rendererScan - var3);
      return handle((var2 - colorMeasure - var3 * 0.5F) / var4);
   }

   public static float process(ViewportLayoutState var0, GuiMetrics var1, float var2) {
      float var3 = execute(var1);
      float var4 = Math.max(1.0F, sourceBuild - var3);
      return handle((var2 - animationSchedule - var3 * 0.5F) / var4);
   }

   private static float update(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = render(var0, var1);
      float var3 = drawAnimation(var0, var1);
      float var4 = handle(var3, var1);
      return Math.round(var2 + var4 + var1.handle(10.0F));
   }

   private static float apply(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = tick(var0, var1);
      float var3 = encodePoint(var0, var1);
      float var4 = Math.round(var2 + var1.handle(44.0F));
      float var5 = Math.round(var3 - var1.handle(44.0F));
      float var6 = process(var5, var1);
      return Math.round(var4 + var6 + var1.handle(8.0F));
   }

   private static float execute(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = drawAnimation(var0, var1);
      float var3 = handle(var2, var1);
      return Math.round(var2 - var3 - var1.handle(10.0F));
   }

   private static float prepare(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = encodePoint(var0, var1);
      float var3 = Math.round(var2 - var1.handle(44.0F));
      float var4 = process(var3, var1);
      return Math.max(var1.handle(24.0F), var3 - var4 - var1.handle(8.0F));
   }

   private static float check(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(update(var0, var1) + var1.handle(10.0F));
   }

   private static float onTick(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(apply(var0, var1) + var1.handle(30.0F));
   }

   private static float select(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(execute(var0, var1) - var1.handle(20.0F));
   }

   private static float refresh(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.max(var1.handle(24.0F), prepare(var0, var1) - var1.handle(38.0F));
   }

   private static float handle(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static float handle(float var0, GuiMetrics var1) {
      return Math.round(handle(var0 * 0.265F, var1.handle(150.0F), var1.handle(182.0F)));
   }

   private static float process(float var0, GuiMetrics var1) {
      return Math.round(handle(var0 * 0.31F, var1.handle(104.0F), var1.handle(124.0F)));
   }
   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4) {
      RenderDiagnostics.handle().handle(this.providerClose);
      GuiMetrics var5 = var4.update();
      ThemeColors var6 = var4.apply();
      long var7 = var2.filterKey();
      if (var7 != this.presetWrite) {
         this.presetWrite = var7;
         this.handle();
      }

      this.presetSave.process();
      float var9 = var2.handle(UiAnimationKeys.check());
      if (!(var9 <= 0.001F)) {
         float var10 = render(var3, var5);
         float var11 = tick(var3, var5);
         float var12 = drawAnimation(var3, var5);
         float var13 = encodePoint(var3, var5);
         var1.update(var9);

         try {
            this.handle(var1, var5, var6, var10, var11, var12, var9);
            float var14 = Math.round(var11 + var5.handle(44.0F));
            float var15 = Math.round(var13 - var5.handle(44.0F));
            float var16 = handle(var12, var5);
            this.compute(var1, var5, var6, var10, var14, var16, var15);
            float var17 = Math.round(var10 + var16 + var5.handle(10.0F));
            float var18 = Math.round(var12 - var16 - var5.handle(10.0F));
            float var19 = this.providerClose.vectorPerform ? 1.0F : 0.0F;
            float var20 = handle(
               var2.handle(UiAnimationKeys.onTick(), var19, var19 > 0.0F ? SpringAnimationSpec.refresh() : SpringAnimationSpec.drawAnimation())
            );
            float var21 = process(var20);
            var1.compute();
            var1.handle(var17, var14, var18, var15, var5.handle(9.0F), var5.handle(9.0F), var5.handle(9.0F), var5.handle(9.0F));

            try {
               if (var21 < 0.999F) {
                  var1.update(1.0F - var21);
                  var1.handle(-var5.handle(14.0F) * var21, 0.0F);
                  var1.handle(1.0F - var21 * 0.018F, var17 + var18 * 0.5F, var14 + var15 * 0.5F);

                  try {
                     this.resolve(var1, var5, var6, var17, var14, var18, var15);
                  } finally {
                     var1.check();
                     var1.prepare();
                     var1.onTick();
                  }
               }

               if (var21 > 0.001F) {
                  var1.update(var21);
                  var1.handle(var5.handle(18.0F) * (1.0F - var21), var5.handle(5.0F) * (1.0F - var21));
                  var1.handle(0.982F + var21 * 0.018F, var17 + var18 * 0.5F, var14 + var15 * 0.5F);
                  boolean var38 = false /* VF: Semaphore variable */;

                  try {
                     var38 = true;
                     this.handle(var1, var2, var5, var6, var17, var14, var18, var15);
                     var38 = false;
                  } finally {
                     if (var38) {
                        var1.check();
                        var1.prepare();
                        var1.onTick();
                     }
                  }

                  var1.check();
                  var1.prepare();
                  var1.onTick();
               }
            } finally {
               var1.compute();
               var1.apply();
            }

            if (var21 <= 0.001F) {
               compute();
            }
         } finally {
            var1.onTick();
         }
      }
   }

   private void handle() {
      this.presetSave.handle();
      this.windowConvert.handle(0.0F);
      compute();
      RenderDiagnostics.handle().drawAnimation();
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7) {
      float var8 = var2.handle(32.0F);
      this.process(var1, var2, var3, var4, var5, var8, var7);
      float var9 = var4 + var8 + var2.handle(11.0F);
      if (!var3.unload()) {
         var1.resolve();

         try {
            ModuleStateHelper.handle(
               var1,
               var2,
               FontRegistry.config,
               var9,
               var5 - var2.handle(1.0F),
               var2.handle(17.0F),
               13.0F,
               "Диагностика Wild Core",
               ThemeColors.handle(var3.save(), 40)
            );
         } finally {
            var1.update();
         }
      }

      ModuleStateHelper.handle(
         var1, var2, FontRegistry.config, var9, var5 - var2.handle(1.0F), var2.handle(17.0F), 13.0F, "Диагностика Wild Core", ModuleStateHelper.handle(var3)
      );
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.instance,
         var9,
         var5 + var2.handle(16.0F),
         var2.handle(14.0F),
         9.0F,
         "рендер, шейдеры, GL и локальные слепки",
         ModuleStateHelper.process(var3)
      );
      this.handle(var1, var2, var3, handle(var2, var4, var6), var5 + var2.handle(3.0F), handle(var2), "Слепок", var3.save(), false, 0);
      this.handle(var1, var2, var3, process(var2, var4, var6), var5 + var2.handle(3.0F), process(var2), "Папка", var3.submit(), false, 1);
      this.handle(
         var1, var2, var3, compute(var2, var4, var6), var5 + var2.handle(3.0F), compute(var2), "Логи", var3.save(), this.providerClose.vectorPerform, 2
      );
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7) {
      var1.compute();
      WildLogoRenderer.handle(Math.round(var4), Math.round(var5), Math.round(var6), var3.save(), var3.submit(), handle(var7), var3.unload());
      float var8 = 0.5F + 0.5F * (float)Math.sin((float)System.currentTimeMillis() * 0.00108F);
      float var9 = 15.0F;
      float var10 = var9 * (1.08F + var8 * 0.04F);
      float var11 = var4 + var6 * 0.5F;
      float var12 = var5 + var6 * 0.5F;
      float var13 = ModuleStateHelper.handle(var2, FontRegistry.current, "w", var9);
      float var14 = ModuleStateHelper.handle(var2, FontRegistry.current, "w", var10);
      float var15 = ModuleStateHelper.handle(var2, FontRegistry.current, var9);
      float var16 = ModuleStateHelper.handle(var2, FontRegistry.current, var10);
      float var17 = var12 - var15 * 0.5F - var2.handle(1.0F);
      float var18 = var12 - var16 * 0.5F - var2.handle(1.0F);
      if (!var3.unload()) {
         var1.resolve();

         try {
            ModuleStateHelper.handle(
               var1,
               var2,
               FontRegistry.current,
               var11 - var14 * 0.5F,
               var18,
               var10,
               "w",
               ThemeColors.handle(ThemeColors.handle(var3.submit(), 110), ThemeColors.handle(var3.save(), 130), var8)
            );
         } finally {
            var1.update();
         }
      }

      ModuleStateHelper.handle(
         var1, var2, FontRegistry.current, var11 - var13 * 0.5F, var17, var9, "w", ThemeColors.handle(ModuleStateHelper.resolve(var3), 246)
      );
   }

   private void compute(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7) {
      float var8 = var2.handle(10.0F);
      int var9 = var3.unload() ? ModuleStateHelper.handle(var3, 0.35F) : ThemeColors.handle(var3.prepare(), var3.onTick(), 0.36F);
      var1.handle(var4, var5, var6, var7, var8, var9);
      var1.handle(var4, var5, var6, var7, var8, ThemeColors.handle(var3.load(), var3.unload() ? 92 : 20), Math.max(0.6F, var2.handle(0.65F)));
      float var10 = var2.handle(9.0F);
      float var11 = var2.handle(4.0F);
      float var12 = var2.handle(35.0F);
      float var13 = Math.max(var2.handle(180.0F), var7 - var10 * 2.0F - var12 - var11 * 4.0F);
      float var14 = var13 / 5.0F;
      float var15 = var5 + var10;
      String var16 = this.providerClose.vectorMatch == 0 ? "Nominal" : "Anomaly";
      this.handle(
         var1,
         var2,
         var3,
         var4 + var10,
         var15,
         var6 - var10 * 2.0F,
         var14,
         "Состояние",
         var16,
         this.providerClose.vectorMatch == 0 ? var3.save() : var3.process()
      );
      var15 += var14 + var11;
      this.handle(var1, var2, var3, var4 + var10, var15, var6 - var10 * 2.0F, var14, "Tracker ID", this.providerClose.config, ModuleStateHelper.handle(var3));
      var15 += var14 + var11;
      this.handle(
         var1,
         var2,
         var3,
         var4 + var10,
         var15,
         var6 - var10 * 2.0F,
         var14,
         "Code",
         this.providerClose.state,
         this.providerClose.vectorMatch == 0 ? ModuleStateHelper.process(var3) : var3.process()
      );
      var15 += var14 + var11;
      this.handle(
         var1,
         var2,
         var3,
         var4 + var10,
         var15,
         var6 - var10 * 2.0F,
         var14,
         "Ошибки",
         this.providerClose.source,
         "0".equals(this.providerClose.source) ? ModuleStateHelper.process(var3) : var3.process()
      );
      var15 += var14 + var11;
      this.handle(var1, var2, var3, var4 + var10, var15, var6 - var10 * 2.0F, var14, "Очередь", this.providerClose.enabled, ModuleStateHelper.handle(var3));
      float var17 = Math.round(var5 + var7 - var10 - var12 + var2.handle(14.0F));
      float var18 = Math.round(var4 + var10);
      float var19 = Math.round(var6 - var10 * 2.0F);
      ModuleStateHelper.handle(var1, var2, FontRegistry.config, var18, var17, var2.handle(13.0F), 9.5F, "Буфер событий", ModuleStateHelper.handle(var3));
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.instance,
         var18 + var19 - var2.handle(28.0F),
         var17,
         var2.handle(13.0F),
         8.5F,
         this.providerClose.itemProject + "/32",
         ModuleStateHelper.process(var3)
      );
      float var20 = Math.round(var17 + var2.handle(18.0F));
      float var21 = Math.min(1.0F, this.providerClose.itemProject / 32.0F);
      var1.handle(var18, var20, var19, var2.handle(5.0F), var2.handle(2.5F), var3.select());
      var1.handle(var18, var20, var19 * var21, var2.handle(5.0F), var2.handle(2.5F), var3.save(), var3.submit());
   }

   private void resolve(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7) {
      float var8 = var2.handle(8.0F);
      float var9 = var2.handle(9.0F);
      float var10 = handle(var7 * 0.22F, var2.handle(78.0F), var2.handle(96.0F));
      float var11 = Math.round((var6 - var8 * 2.0F) / 3.0F);
      float var12 = handle(this.windowConvert.handle(1.0F, moduleCollect));
      int var13 = this.providerClose.vectorMatch == 0 ? var3.save() : var3.process();
      this.handle(
         var1,
         var2,
         var3,
         var4,
         var5,
         var11,
         var10,
         var9,
         "Core Load",
         this.presetSave.apply(),
         "CFI chain  " + this.providerClose.context,
         var3.save(),
         this.presetSave.context,
         this.presetSave.compute(),
         process(var12, 0.0F, 0.78F)
      );
      this.handle(
         var1,
         var2,
         var3,
         var4 + var11 + var8,
         var5,
         var11,
         var10,
         var9,
         "Render TPS",
         this.presetSave.execute(),
         "Frames  " + this.providerClose.latest,
         var3.submit(),
         this.presetSave.config,
         this.presetSave.resolve(),
         process(var12, 0.12F, 0.9F)
      );
      this.handle(
         var1,
         var2,
         var3,
         var4 + (var11 + var8) * 2.0F,
         var5,
         var6 - var11 * 2.0F - var8 * 2.0F,
         var10,
         var9,
         "Latency",
         this.presetSave.prepare(),
         "Anomalies  " + this.providerClose.previous,
         var13,
         this.presetSave.state,
         this.presetSave.update(),
         process(var12, 0.24F, 1.0F)
      );
      float var14 = Math.round(var5 + var10 + var8);
      float var15 = handle(var7 * 0.29F, var2.handle(88.0F), var2.handle(106.0F));
      float var16 = Math.round(var6 * 0.58F);
      this.handle(var1, var2, var3, var4, var14, var16, var15, var9);
      float var17 = Math.round(var4 + var16 + var8);
      float var18 = Math.round(var6 - var16 - var8);
      float var19 = Math.round((var15 - var8) * 0.5F);
      this.handle(
         var1,
         var2,
         var3,
         var17,
         var14,
         var18,
         var19,
         var9,
         "Текстурные Юниты",
         this.update(),
         this.providerClose.vectorMatch == 0 ? var3.submit() : var3.process()
      );
      this.handle(var1, var2, var3, var17, var14 + var19 + var8, var18, var19, var9, "Матрицы", this.resolve(), this.handle(var3));
      float var20 = Math.round(var14 + var15 + var8);
      float var21 = handle(var7 * 0.14F, var2.handle(42.0F), var2.handle(50.0F));
      String var22 = this.providerClose.selection != null && !"none".equals(this.providerClose.selection)
         ? this.providerClose.selection
         : this.providerClose.active;
      this.handle(var1, var2, var3, var4, var20, var6, var21, var9, "Файл слепка", var22, var3.submit());
      float var23 = Math.round(var20 + var21 + var8);
      float var24 = handle(var7 * 0.13F, var2.handle(40.0F), var2.handle(46.0F));
      this.process(var1, var2, var3, var4, var23, var6, var24, var9);
      float var25 = Math.round(var23 + var24 + var8);
      this.compute(var1, var2, var3, var4, var25, var6, Math.max(var2.handle(46.0F), var7 - (var25 - var5)), var9);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, float var5, float var6, float var7, float var8) {
      float var9 = var3.handle(8.0F);
      float var10 = var3.handle(9.0F);
      float var11 = process(var8, var3);
      float var12 = Math.round(var7 * 0.58F);
      this.handle(var1, var3, var4, var5, var6, var12, var11, var10);
      float var13 = Math.round(var5 + var12 + var9);
      float var14 = Math.round(var7 - var12 - var9);
      float var15 = Math.round((var11 - var9 * 2.0F) / 3.0F);
      this.handle(var1, var3, var4, var13, var6, var14, var15, var10, "CFI chain", this.providerClose.context, var4.save());
      this.handle(
         var1,
         var3,
         var4,
         var13,
         var6 + var15 + var9,
         var14,
         var15,
         var10,
         "Текстурные Юниты",
         this.update(),
         this.providerClose.vectorMatch == 0 ? var4.submit() : var4.process()
      );
      this.handle(
         var1, var3, var4, var13, var6 + (var15 + var9) * 2.0F, var14, var11 - var15 * 2.0F - var9 * 2.0F, var10, "Матрицы", this.resolve(), this.handle(var4)
      );
      float var16 = Math.round(var6 + var11 + var9);
      this.handle(var1, var2, var3, var4, var5, var16, var7, var8 - var11 - var9, var10);
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, String var7, int var8, boolean var9, int var10
   ) {
      float var11 = Math.round(var4);
      float var12 = Math.round(var5);
      float var13 = Math.round(var6);
      float var14 = resolve(var2);
      float var15 = var2.handle(7.0F);
      float var16 = var9 ? 1.0F : 0.0F;
      int var17 = var3.unload()
         ? ModuleStateHelper.handle(var3, 0.38F + var16 * 0.34F)
         : ThemeColors.handle(var3.prepare(), ThemeColors.handle(var8, 22), 0.36F + var16 * 0.2F);
      var1.handle(var11, var12, var13, var14, var15, var17);
      var1.handle(var11, var12, var13, var14, var15, ThemeColors.handle(var8, var3.unload() ? 68 : 58), Math.max(0.5F, var2.handle(0.55F)));
      float var18 = var2.handle(18.0F);
      float var19 = var11 + var2.handle(4.0F);
      float var20 = var12 + Math.round((var14 - var18) * 0.5F);
      var1.handle(var19, var20, var18, var18, var2.handle(5.0F), ThemeColors.handle(var8, var9 ? 68 : 38));
      this.handle(var1, var2, var19 + var18 * 0.5F, var20 + var18 * 0.5F, var10, var9 ? var3.load() : var8, var3);
      ModuleStateHelper.handle(var1, var2, FontRegistry.config, var11 + var2.handle(28.0F), var12, var14, 9.0F, var7, ModuleStateHelper.handle(var3));
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5, int var6, ThemeColors var7) {
      float var8 = var2.handle(1.0F);
      int var9 = ThemeColors.handle(var6, 235);
      if (var5 == 0) {
         var1.handle(var3 - 4.8F * var8, var4 - 4.4F * var8, 9.6F * var8, 8.8F * var8, 2.2F * var8, ThemeColors.handle(var9, 92));
         var1.handle(var3 - 2.8F * var8, var4 + 1.2F * var8, 1.4F * var8, 2.6F * var8, 0.7F * var8, var9);
         var1.handle(var3 - 0.2F * var8, var4 - 1.8F * var8, 1.4F * var8, 5.6F * var8, 0.7F * var8, var9);
         var1.handle(var3 + 2.4F * var8, var4 - 4.0F * var8, 1.4F * var8, 7.8F * var8, 0.7F * var8, var9);
      } else if (var5 == 1) {
         var1.handle(var3 - 5.2F * var8, var4 - 2.8F * var8, 10.4F * var8, 6.8F * var8, 1.8F * var8, ThemeColors.handle(var9, 108));
         var1.handle(var3 - 4.2F * var8, var4 - 4.4F * var8, 4.8F * var8, 2.6F * var8, 1.1F * var8, ThemeColors.handle(var9, 178));
         var1.handle(var3 - 2.6F * var8, var4 + 0.1F * var8, 5.2F * var8, 1.1F * var8, 0.55F * var8, var9);
      } else {
         var1.handle(var3 - 4.8F * var8, var4 - 4.0F * var8, 9.6F * var8, 1.3F * var8, 0.65F * var8, var9);
         var1.handle(var3 - 4.8F * var8, var4 - 0.6F * var8, 9.6F * var8, 1.3F * var8, 0.65F * var8, var9);
         var1.handle(var3 - 4.8F * var8, var4 + 2.8F * var8, 7.1F * var8, 1.3F * var8, 0.65F * var8, ThemeColors.handle(var9, 190));
         var1.process(var3 + 4.5F * var8, var4 + 3.4F * var8, 1.15F * var8, 0.0F, 1.0F, var7.submit());
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      String var10,
      int var11
   ) {
      float var12 = Math.round(var4);
      float var13 = Math.round(var5);
      float var14 = Math.round(var6);
      float var15 = Math.round(var7);
      float var16 = var12 + var2.handle(27.0F);
      float var17 = Math.max(var2.handle(12.0F), var14 - var2.handle(37.0F));
      float var18 = var2.handle(11.0F);
      float var19 = var2.handle(12.0F);
      float var20 = Math.round(var13 + var2.handle(18.0F));
      int var21 = var3.unload() ? ModuleStateHelper.handle(var3, 0.18F) : ThemeColors.handle(var3.prepare(), ThemeColors.handle(var11, 10), 0.16F);
      var1.handle(var12, var13, var14, var15, var8, var21);
      var1.handle(var12, var13, var14, var15, var8, ThemeColors.handle(var3.load(), var3.unload() ? 54 : 20), Math.max(0.5F, var2.handle(0.55F)));
      this.handle(var1, var2, var12 + var2.handle(14.0F), var13 + var2.handle(8.0F), var11, var3);
      this.handle(var1, var2, FontRegistry.config, var16, var13 + var2.handle(2.0F), var18, 9.5F, var9, ModuleStateHelper.process(var3), var17);
      this.handle(var1, var2, FontRegistry.config, var16, var20, var19, 9.0F, var10, ModuleStateHelper.handle(var3), var17);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = Math.round(var4);
      float var10 = Math.round(var5);
      float var11 = Math.round(var6);
      float var12 = Math.round(var7);
      boolean var13 = "0".equals(this.providerClose.source);
      int var14 = var13 ? var3.submit() : var3.process();
      int var15 = var3.unload()
         ? ModuleStateHelper.handle(var3, var13 ? 0.2F : 0.31F)
         : ThemeColors.handle(var3.prepare(), ThemeColors.handle(var14, var13 ? 12 : 28), 0.24F);
      var1.handle(var9, var10, var11, var12, var8, var15);
      var1.handle(var9, var10, var11, var12, var8, ThemeColors.handle(var3.load(), var3.unload() ? 58 : 22), Math.max(0.55F, var2.handle(0.6F)));
      float var16 = var2.handle(14.0F);
      float var17 = var9 + var16 + var2.handle(14.0F);
      float var18 = Math.max(var2.handle(16.0F), var11 - var16 - var2.handle(24.0F));
      this.handle(var1, var2, var9 + var16, var10 + var2.handle(12.0F), var14, var3);
      this.handle(
         var1,
         var2,
         FontRegistry.config,
         var17,
         var10 + var2.handle(5.0F),
         var2.handle(14.0F),
         10.0F,
         "Что сейчас ломается",
         ModuleStateHelper.process(var3),
         var18
      );
      this.handle(
         var1,
         var2,
         FontRegistry.config,
         var17,
         var10 + var2.handle(22.0F),
         var2.handle(16.0F),
         10.0F,
         var13 ? "Шейдерных исключений нет" : this.providerClose.animationDraw,
         ModuleStateHelper.handle(var3),
         var18
      );
      this.handle(
         var1,
         var2,
         FontRegistry.instance,
         var17,
         var10 + var2.handle(42.0F),
         var2.handle(14.0F),
         9.0F,
         var13 ? this.providerClose.target : this.providerClose.pointEncode,
         var13 ? ModuleStateHelper.process(var3) : var14,
         var18
      );
      this.handle(
         var1,
         var2,
         FontRegistry.instance,
         var17,
         var10 + var2.handle(59.0F),
         var2.handle(15.0F),
         8.5F,
         var13 ? "Нажми Логи, чтобы загрузить latest.log" : this.providerClose.animator,
         var13 ? ModuleStateHelper.process(var3) : ModuleStateHelper.handle(var3),
         var18
      );
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = Math.round(var4);
      float var10 = Math.round(var5);
      float var11 = Math.round(var6);
      float var12 = Math.round((var11 - var2.handle(8.0F)) * 0.5F);
      this.process(var1, var2, var3, var9, var10, var12, var7, var8, "Mixin policy", this.providerClose.renderer, var3.save());
      this.process(
         var1,
         var2,
         var3,
         var9 + var12 + var2.handle(8.0F),
         var10,
         var11 - var12 - var2.handle(8.0F),
         var7,
         var8,
         "Privacy",
         this.providerClose.handler,
         var3.submit()
      );
   }

   private void process(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      String var10,
      int var11
   ) {
      int var12 = var3.unload() ? ModuleStateHelper.handle(var3, 0.18F) : ThemeColors.handle(var3.prepare(), ThemeColors.handle(var11, 14), 0.18F);
      var1.handle(var4, var5, Math.round(var6), Math.round(var7), var8, var12);
      var1.handle(var4, var5, Math.round(var6), Math.round(var7), var8, ThemeColors.handle(var11, var3.unload() ? 46 : 42), Math.max(0.5F, var2.handle(0.55F)));
      float var13 = var4 + var2.handle(12.0F);
      float var14 = Math.max(var2.handle(12.0F), var6 - var2.handle(24.0F));
      this.handle(var1, var2, FontRegistry.config, var13, var5 + var2.handle(5.0F), var2.handle(13.0F), 9.5F, var9, ModuleStateHelper.process(var3), var14);
      this.handle(var1, var2, FontRegistry.config, var13, var5 + var2.handle(21.0F), var2.handle(14.0F), 9.0F, var10, ModuleStateHelper.handle(var3), var14);
   }

   private void compute(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = Math.round(var4);
      float var10 = Math.round(var5);
      float var11 = Math.round(var6);
      float var12 = Math.round(var7);
      int var13 = var3.unload() ? ModuleStateHelper.handle(var3, 0.24F) : ThemeColors.handle(var3.prepare(), var3.onTick(), 0.32F);
      var1.handle(var9, var10, var11, var12, var8, var13);
      var1.handle(var9, var10, var11, var12, var8, ThemeColors.handle(var3.load(), var3.unload() ? 76 : 24), Math.max(0.55F, var2.handle(0.6F)));
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.config,
         var9 + var2.handle(14.0F),
         var10 + var2.handle(5.0F),
         var2.handle(13.0F),
         9.5F,
         "Гайдлайн",
         ModuleStateHelper.process(var3)
      );
      float var14 = var10 + var2.handle(25.0F);
      this.handle(var1, var2, var3, var9 + var2.handle(14.0F), var14, "1 смотри Code/Stage", var3.save());
      this.handle(var1, var2, var3, var9 + var11 * 0.29F, var14, "2 жми Слепок", var3.submit());
      this.handle(var1, var2, var3, var9 + var11 * 0.53F, var14, "3 открой Логи", var3.save());
      this.handle(var1, var2, var3, var9 + var11 * 0.76F, var14, "4 передай Tracker ID", var3.submit());
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, float var5, float var6, float var7, float var8, float var9
   ) {
      float var10 = Math.round(var5);
      float var11 = Math.round(var6);
      float var12 = Math.round(var7);
      float var13 = Math.round(var8);
      int var14 = var4.unload()
         ? ThemeColors.handle(247, 248, 252, 226)
         : ThemeColors.handle(ThemeColors.handle(5, 7, 12, 238), ThemeColors.handle(var4.save(), 34), 0.22F);
      var1.handle(var10, var11, var12, var13, var9, var14);
      var1.handle(var10, var11, var12, var13, var9, ThemeColors.handle(var4.save(), var4.unload() ? 58 : 76), Math.max(0.55F, var3.handle(0.6F)));
      this.handle(
         var1,
         var3,
         FontRegistry.config,
         var10 + var3.handle(14.0F),
         var11 + var3.handle(6.0F),
         var3.handle(16.0F),
         10.5F,
         "Встроенный viewer",
         ModuleStateHelper.handle(var4),
         var3.handle(138.0F)
      );
      this.handle(
         var1,
         var3,
         FontRegistry.instance,
         var10 + var3.handle(166.0F),
         var11 + var3.handle(6.0F),
         var3.handle(16.0F),
         8.5F,
         this.providerClose.pending,
         ModuleStateHelper.process(var4),
         Math.max(var3.handle(40.0F), var12 - var3.handle(276.0F))
      );
      this.handle(
         var1,
         var3,
         FontRegistry.instance,
         var10 + var12 - var3.handle(92.0F),
         var11 + var3.handle(6.0F),
         var3.handle(16.0F),
         8.5F,
         "latest.log tail",
         ModuleStateHelper.process(var4),
         var3.handle(80.0F)
      );
      float var15 = var10 + var3.handle(10.0F);
      float var16 = var11 + var3.handle(30.0F);
      float var17 = var12 - var3.handle(20.0F);
      float var18 = Math.max(var3.handle(24.0F), var13 - var3.handle(38.0F));
      int var19 = Math.min(this.providerClose.responseCompute, 96);
      float var20 = Math.max(var3.handle(14.0F), Math.min(var3.handle(18.0F), var18 / Math.max(1, Math.min(96, 14))));
      float var21 = var3.handle(62.0F);
      float var22 = Math.max(var18, var19 * var20);
      float var23 = var17;

      for (int var24 = 0; var24 < var19; var24++) {
         float var25 = var21 + var3.handle(24.0F) + ModuleStateHelper.handle(var3, FontRegistry.instance, this.handle(this.providerClose.summary[var24]), 8.0F);
         var23 = Math.max(var23, var25);
      }

      float var34 = Math.max(0.0F, var22 - var18);
      float var35 = Math.max(0.0F, var23 - var17);
      handle(var15, var16, var17, var18, var23, var22, var35, var34);
      var2.handle(var34, var35);
      float var26 = Math.min(var2.runPlayer(), var34);
      float var27 = Math.min(var2.renderMatrix(), var35);
      var1.compute();
      var1.handle(var15, var16, var17, var18, var3.handle(6.0F), var3.handle(6.0F), var3.handle(6.0F), var3.handle(6.0F));

      try {
         for (int var28 = 0; var28 < var19; var28++) {
            float var29 = var16 + var28 * var20 - var26;
            if (!(var29 + var20 < var16) && !(var29 > var16 + var18)) {
               this.handle(var1, var3, var4, var15, var29, var17, var20, this.providerClose.summary[var28], this.providerClose.matrixBlend[var28], var27);
            }
         }

         if (var19 == 0) {
            ModuleStateHelper.handle(
               var1,
               var3,
               FontRegistry.instance,
               var15 + var3.handle(9.0F),
               var16 + var3.handle(3.0F),
               var3.handle(16.0F),
               9.0F,
               "Нажми Логи, чтобы загрузить latest.log",
               ModuleStateHelper.process(var4)
            );
         }
      } finally {
         var1.compute();
         var1.apply();
      }

      if (var34 > 0.5F) {
         float var36 = update(var3);
         float var38 = Math.round(var15 + var17 - var36);
         float var30 = execute(var3);
         float var31 = Math.round(var16 + (var18 - var30) * (var26 / Math.max(1.0F, var34)));
         ModuleStateHelper.process(var1, var3, var4, var38, var16, var36, var18, var31, var30, 0.0F, 0.42F);
      }

      if (var35 > 0.5F) {
         float var37 = update(var3);
         float var39 = Math.round(var16 + var18 - var37);
         float var40 = apply(var3);
         float var41 = Math.round(var15 + (var17 - var40) * (var27 / Math.max(1.0F, var35)));
         var1.handle(var15, var39, var17, var37, var37 * 0.5F, ThemeColors.handle(var4.prepare(), var4.onTick(), 0.42F));
         var1.handle(var41, var39, var40, var37, var37 * 0.5F, ThemeColors.handle(var4.save(), 165), ThemeColors.handle(var4.submit(), 150));
      }
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, String var8, int var9, float var10
   ) {
      int var11 = this.handle(var3, var9);
      if (var9 >= 2) {
         var1.handle(var4, var5, var6, var7, var2.handle(3.0F), ThemeColors.handle(var11, var9 == 3 ? 24 : 16));
      }

      float var12 = var4 + var2.handle(7.0F);
      float var13 = var5 + var7 * 0.5F;
      var1.process(var12, var13, var2.handle(2.2F), 0.0F, 1.0F, ThemeColors.handle(var11, 230));
      ModuleStateHelper.handle(var1, var2, FontRegistry.config, var4 + var2.handle(16.0F), var5, var7, 7.0F, this.handle(var9), ThemeColors.handle(var11, 238));
      float var14 = var4 + var2.handle(72.0F);
      float var15 = Math.max(var2.handle(18.0F), var6 - var2.handle(76.0F));
      var1.compute();
      var1.handle(var14, var5, var15, var7, 0.0F, 0.0F, 0.0F, 0.0F);

      try {
         ModuleStateHelper.handle(
            var1, var2, FontRegistry.instance, var14 - var10, var5, var7, 8.0F, this.handle(var8), var9 == 3 ? var3.process() : ModuleStateHelper.handle(var3)
         );
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, String var6, int var7) {
      float var8 = var2.handle(4.0F);
      var1.process(var4, var5 + var2.handle(8.0F), var8, 0.0F, 1.0F, ThemeColors.handle(var7, 210));
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.instance, var4 + var2.handle(9.0F), var5, var2.handle(16.0F), 8.5F, var6, ModuleStateHelper.handle(var3)
      );
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, String var8, String var9, int var10
   ) {
      float var11 = Math.round(var4);
      float var12 = Math.round(var5);
      float var13 = Math.round(var6);
      float var14 = Math.round(var7);
      float var15 = var2.handle(10.0F);
      int var16 = var3.unload() ? ModuleStateHelper.handle(var3, 0.13F) : ThemeColors.handle(var3.prepare(), var3.onTick(), 0.22F);
      var1.handle(var11, var12, var13, var14, var2.handle(7.0F), var16);
      var1.handle(var11, var12, var13, var14, var2.handle(7.0F), ThemeColors.handle(var3.load(), var3.unload() ? 52 : 15), Math.max(0.45F, var2.handle(0.5F)));
      float var17 = Math.max(var2.handle(12.0F), var13 - var15 * 2.0F);
      this.handle(
         var1, var2, FontRegistry.config, var11 + var15, var12 + var2.handle(5.0F), var2.handle(13.0F), 9.5F, var8, ModuleStateHelper.process(var3), var17
      );
      this.handle(var1, var2, FontRegistry.config, var11 + var15, var12 + var2.handle(19.0F), var2.handle(15.0F), 9.0F, var9, var10, var17);
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, FontObject var3, float var4, float var5, float var6, float var7, String var8, int var9, float var10
   ) {
      String var11 = this.handle(var8);
      float var12 = Math.max(var2.handle(8.0F), var10);
      float var13 = ModuleStateHelper.handle(var2, var3, var11, var7);
      if (var13 <= var12) {
         ModuleStateHelper.handle(var1, var2, var3, var4, var5, var6, var7, var11, var9);
      } else {
         float var14 = var13 - var12 + var2.handle(5.0F);
         float var15 = var14 * this.process();
         var1.compute();
         var1.handle(var4, var5, var12, var6, 0.0F, 0.0F, 0.0F, 0.0F);

         try {
            ModuleStateHelper.handle(var1, var2, var3, var4 - var15, var5, var6, var7, var11, var9);
         } finally {
            var1.compute();
            var1.apply();
         }
      }
   }

   private float process() {
      float var1 = (float)(System.currentTimeMillis() % 7200L) / 7200.0F;
      if (var1 < 0.18F) {
         return 0.0F;
      } else if (var1 < 0.44F) {
         return process((var1 - 0.18F) / 0.26F);
      } else if (var1 < 0.62F) {
         return 1.0F;
      } else {
         return var1 < 0.88F ? 1.0F - process((var1 - 0.62F) / 0.26F) : 0.0F;
      }
   }

   private static float process(float var0) {
      float var1 = handle(var0);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float process(float var0, float var1, float var2) {
      return handle((var0 - var1) / Math.max(0.001F, var2 - var1));
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      String var10,
      String var11,
      int var12,
      float[] var13,
      float var14,
      float var15
   ) {
      float var16 = Math.round(var4);
      float var17 = Math.round(var5);
      float var18 = Math.round(var6);
      float var19 = Math.round(var7);
      int var20 = var3.unload() ? ModuleStateHelper.handle(var3, 0.16F) : ThemeColors.handle(var3.prepare(), ThemeColors.handle(var12, 12), 0.18F);
      var1.handle(var16, var17, var18, var19, var8, var20);
      var1.handle(var16, var17, var18, var19, var8, ThemeColors.handle(var12, var3.unload() ? 54 : 40), Math.max(0.5F, var2.handle(0.6F)));
      float var21 = var2.handle(10.0F);
      float var22 = ModuleStateHelper.handle(var2, FontRegistry.config, var10, 10.5F);
      float var23 = Math.max(var2.handle(12.0F), var18 - var21 * 2.0F - var22 - var2.handle(6.0F));
      this.handle(
         var1, var2, FontRegistry.config, var16 + var21, var17 + var2.handle(6.0F), var2.handle(12.0F), 9.0F, var9, ModuleStateHelper.process(var3), var23
      );
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.config,
         var16 + var18 - var21 - var22,
         var17 + var2.handle(5.0F),
         var2.handle(13.0F),
         10.5F,
         var10,
         ThemeColors.handle(var12, 235)
      );
      float var24 = var16 + var21;
      float var25 = var17 + var2.handle(24.0F);
      float var26 = Math.max(var2.handle(8.0F), var18 - var21 * 2.0F);
      float var27 = var17 + var19 - var2.handle(15.0F);
      float var28 = Math.max(var2.handle(8.0F), var27 - var25);
      this.handle(var1, var2, var3, var24, var25, var26, var28, var12, var13, var14, process(var15));
      this.handle(
         var1,
         var2,
         FontRegistry.instance,
         var16 + var21,
         var17 + var19 - var2.handle(13.0F),
         var2.handle(11.0F),
         7.5F,
         var11,
         ModuleStateHelper.process(var3),
         Math.max(var2.handle(12.0F), var18 - var21 * 2.0F)
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      float[] var9,
      float var10,
      float var11
   ) {
      var1.handle(var4, var5 + var7 - var2.handle(0.75F), var6, var2.handle(0.75F), 0.0F, ThemeColors.handle(var8, var3.unload() ? 40 : 32));
      int var12 = var9.length;
      if (var12 >= 2 && !(var10 <= 1.0E-4F) && !(var11 <= 0.001F)) {
         float var13 = var6 / (var12 - 1);
         int var14 = ThemeColors.handle(var8, var3.unload() ? 118 : 150);
         int var15 = ThemeColors.handle(var8, var3.unload() ? 12 : 18);
         int var16 = ThemeColors.handle(var8, 235);
         float var17 = 0.0F;
         float var18 = 0.0F;

         for (int var19 = 0; var19 < var12; var19++) {
            float var20 = handle(this.presetSave.handle(var9, var19) / var10) * var11;
            float var21 = var20 * var7;
            float var22 = var4 + var19 * var13;
            float var23 = var5 + var7 - var21;
            if (var21 > 0.5F) {
               var1.process(var22 - var13 * 0.5F, var23, var13 + var2.handle(0.6F), var21, 0.0F, var14, var15);
            }

            if (var19 > 0) {
               this.handle(var1, var2, var17, var18, var22, var23, var16);
            }

            var17 = var22;
            var18 = var23;
         }
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, float var5, float var6, int var7) {
      float var8 = var5 - var3;
      float var9 = var6 - var4;
      float var10 = (float)Math.sqrt(var8 * var8 + var9 * var9);
      float var11 = Math.max(1.0F, var2.handle(1.4F));
      if (var10 < 0.001F) {
         var1.handle(var3 - var11 * 0.5F, var4 - var11 * 0.5F, var11, var11, var11 * 0.5F, var7);
      } else {
         float var12 = (float)Math.toDegrees(Math.atan2(var9, var8));
         var1.handle(var3, var4);
         var1.process(var12);

         try {
            var1.handle(0.0F, -var11 * 0.5F, var10, var11, var11 * 0.5F, var7);
         } finally {
            var1.execute();
            var1.prepare();
         }
      }
   }

   private static void handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      colorMeasure = var0;
      animationSchedule = var1;
      rendererScan = var2;
      sourceBuild = var3;
      outputCollapse = var4;
      profileInvoke = var5;
      sourceSchedule = var6;
      timerRender = var7;
   }

   private static void compute() {
      colorMeasure = 0.0F;
      animationSchedule = 0.0F;
      rendererScan = 0.0F;
      sourceBuild = 0.0F;
      outputCollapse = 0.0F;
      profileInvoke = 0.0F;
      sourceSchedule = 0.0F;
      timerRender = 0.0F;
   }

   private static float update(GuiMetrics var0) {
      return Math.max(var0.handle(5.0F), var0.handle(4.0F));
   }

   private static float apply(GuiMetrics var0) {
      return !(rendererScan <= 1.0F) && !(outputCollapse <= rendererScan)
         ? Math.max(var0.handle(28.0F), rendererScan * rendererScan / Math.max(rendererScan, outputCollapse))
         : rendererScan;
   }

   private static float execute(GuiMetrics var0) {
      return !(sourceBuild <= 1.0F) && !(profileInvoke <= sourceBuild)
         ? Math.max(var0.handle(18.0F), sourceBuild * sourceBuild / Math.max(sourceBuild, profileInvoke))
         : sourceBuild;
   }

   private String resolve() {
      String var1 = this.handle(this.providerClose.output);
      return var1.toLowerCase(Locale.ROOT).contains("finite") ? "OK" : "CORRUPTED";
   }

   private String update() {
      return this.providerClose.vectorMatch == 0 ? "Изолированы [TextureUnitGuard]" : this.handle(this.providerClose.cache);
   }

   private int handle(ThemeColors var1) {
      return "OK".equals(this.resolve()) ? var1.save() : var1.process();
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5, ThemeColors var6) {
      float var7 = var2.handle(1.0F);
      var1.handle(
         var3 - 5.2F * var7,
         var4 - 5.2F * var7,
         10.4F * var7,
         10.4F * var7,
         3.0F * var7,
         ThemeColors.handle(var5, var6.unload() ? 96 : 124),
         Math.max(0.6F, var2.handle(0.65F))
      );
      var1.handle(var3 - 0.9F * var7, var4 - 3.7F * var7, 1.8F * var7, 7.4F * var7, 0.9F * var7, ThemeColors.handle(var5, 214));
      var1.handle(var3 - 3.6F * var7, var4 + 1.9F * var7, 7.2F * var7, 1.5F * var7, 0.75F * var7, ThemeColors.handle(var5, 178));
   }

   private int handle(ThemeColors var1, int var2) {
      return switch (var2) {
         case 2 -> var1.compute();
         case 3 -> var1.process();
         case 4 -> var1.save();
         default -> var1.submit();
      };
   }

   private String handle(int var1) {
      return switch (var1) {
         case 2 -> "WARN";
         case 3 -> "ERROR";
         case 4 -> "GL";
         default -> "INFO";
      };
   }

   private String handle(String var1) {
      return var1 != null && !var1.isBlank() ? var1 : "none";
   }

   private static float handle(GuiMetrics var0, float var1, float var2) {
      return Math.round(var1 + var2 - handle(var0) - process(var0) - compute(var0) - var0.handle(16.0F));
   }

   private static float process(GuiMetrics var0, float var1, float var2) {
      return Math.round(var1 + var2 - process(var0) - compute(var0) - var0.handle(8.0F));
   }

   private static float compute(GuiMetrics var0, float var1, float var2) {
      return Math.round(var1 + var2 - compute(var0));
   }

   private static float render(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.drawAnimation() + var1.handle(18.0F));
   }

   private static float tick(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.encodePoint() + var1.handle(18.0F));
   }

   private static float drawAnimation(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.animate() - var1.handle(36.0F));
   }

   private static float encodePoint(ViewportLayoutState var0, GuiMetrics var1) {
      return Math.round(var0.load() - var1.handle(36.0F));
   }

   static final class CacheEntry {
      private static final int instance = 48;
      private static final long data = 50L;
      final float[] context = new float[48];
      final float[] config = new float[48];
      final float[] state = new float[48];
      private int cache;
      private long output;
      private float current;
      private float active;
      private float mode;
      private OperatingSystemMXBean selection;
      private boolean enabled;

      void handle() {
         this.current = this.check();
         this.active = this.onTick();
         this.mode = this.select();

         for (int var1 = 0; var1 < 48; var1++) {
            this.context[var1] = this.current;
            this.config[var1] = this.active;
            this.state[var1] = this.mode;
         }

         this.cache = 47;
         this.output = System.currentTimeMillis();
      }

      void process() {
         this.current = this.check();
         this.active = this.onTick();
         this.mode = this.select();
         long var1 = System.currentTimeMillis();
         if (var1 - this.output < 50L) {
            this.context[this.cache] = this.current;
            this.config[this.cache] = this.active;
            this.state[this.cache] = this.mode;
         } else {
            this.output = var1;
            this.cache = (this.cache + 1) % 48;
            this.context[this.cache] = this.current;
            this.config[this.cache] = this.active;
            this.state[this.cache] = this.mode;
         }
      }

      float handle(float[] var1, int var2) {
         return var1[(this.cache + 1 + var2) % 48];
      }

      float compute() {
         return 1.0F;
      }

      float resolve() {
         float var1 = 1.0F;

         for (int var2 = 0; var2 < 48; var2++) {
            var1 = Math.max(var1, this.config[var2]);
         }

         return Math.max(60.0F, var1 * 1.12F);
      }

      float update() {
         float var1 = 1.0F;

         for (int var2 = 0; var2 < 48; var2++) {
            var1 = Math.max(var1, this.state[var2]);
         }

         return Math.max(80.0F, var1 * 1.2F);
      }

      String apply() {
         return Math.round(this.current * 100.0F) + "%";
      }

      String execute() {
         return Integer.toString(Math.round(this.active));
      }

      String prepare() {
         return Math.round(this.mode) + " ms";
      }

      private float check() {
         try {
            if (!this.enabled) {
               this.enabled = true;
               if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean var2) {
                  this.selection = var2;
               }
            }

            if (this.selection != null) {
               double var4 = this.selection.getProcessCpuLoad();
               if (var4 >= 0.0) {
                  return (float)Math.min(1.0, var4);
               }
            }
         } catch (Throwable var3) {
         }

         return this.current;
      }

      private float onTick() {
         try {
            MinecraftClient var1 = MinecraftClient.getInstance();
            if (var1 != null) {
               return Math.max(0.0F, var1.getCurrentFps());
            }
         } catch (Throwable var2) {
         }

         return this.active;
      }

      private float select() {
         try {
            MinecraftClient var1 = MinecraftClient.getInstance();
            if (var1 != null && var1.player != null && var1.getNetworkHandler() != null) {
               PlayerListEntry var2 = var1.getNetworkHandler().getPlayerListEntry(var1.player.getUuid());
               if (var2 != null) {
                  return Math.max(0.0F, var2.getLatency());
               }
            }
         } catch (Throwable var3) {
         }

         return this.mode;
      }
   }
}
