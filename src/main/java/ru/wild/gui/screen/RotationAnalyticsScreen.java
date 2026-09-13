package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.HudRenderContext;
import ru.wild.automation.RotationAnalyticsSnapshot;
import ru.wild.automation.combat.RotationRecorder;
import ru.wild.modules.combat.AttackAura;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.RoundedRectRenderer;

public final class RotationAnalyticsScreen extends Screen {
   private static volatile boolean instance;
   private static final String[] data = new String[]{"Аналитика", "Обучение", "Сравнение"};
   private static final float context = 52.0F;
   private static final float config = 28.0F;
   private static final float state = 30.0F;
   private static final float cache = 28.0F;
   private static final float output = 28.0F;
   private static final float current = 24.0F;
   private static final float active = 22.0F;
   private static final float mode = 28.0F;
   private final DoubleAnimator selection = new DoubleAnimator();
   private final List<RotationAnalyticsScreen.DataRecord> enabled = new ArrayList<>();
   private final List<RotationAnalyticsScreen.PrimaryScreenState> renderer = new ArrayList<>();
   private float handler;
   private float animationDraw;
   private float pointEncode = 1.0F;
   private float animator;
   private float source;
   private boolean target;
   private int pending;
   private RotationAnalyticsScreen.PrimaryScreenState previous;
   private RotationAnalyticsSnapshot latest;
   private long summary;
   private RotationAnalyticsScreen.ScreenState matrixBlend = new RotationAnalyticsScreen.ScreenState(0.0F, 0.0F, 0.0F, 0.0F);
   private RotationAnalyticsScreen.ScreenState vectorMatch = new RotationAnalyticsScreen.ScreenState(0.0F, 0.0F, 0.0F, 0.0F);
   private final RotationAnalyticsScreen.ScreenState[] itemProject = new RotationAnalyticsScreen.ScreenState[]{
      new RotationAnalyticsScreen.ScreenState(0.0F, 0.0F, 0.0F, 0.0F),
      new RotationAnalyticsScreen.ScreenState(0.0F, 0.0F, 0.0F, 0.0F),
      new RotationAnalyticsScreen.ScreenState(0.0F, 0.0F, 0.0F, 0.0F)
   };

   public RotationAnalyticsScreen() {
      super(Text.literal("AI Lab"));
      compute();
      this.handle();
   }

   private void handle() {
      this.latest = RotationRecorder.apply();
      this.summary = System.currentTimeMillis();
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      this.handle(this.handle(var2), this.process(var3));
      super.render(var1, var2, var3, var4);
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public void handle(RoundedRectRenderer var1, int var2, int var3) {
      if (var1 != null && var2 > 0 && var3 > 0) {
         this.resolve();
         long var4 = System.currentTimeMillis();
         if (!RotationRecorder.matchVector() && var4 - this.summary > 2000L) {
            this.handle();
         }

         this.selection.handle();
         this.selection.handle(this.target ? 0.0 : 1.0, this.target ? 0.18F : 0.22F, this.target ? Easings.renderer : Easings.serverRead, false);
         float var6 = handle(this.selection.update(), 0.0F, 1.0F);
         var1.handle(0.0F, 0.0F, var2, var3, 0.0F, handle(0, 0, 0, Math.round(170.0F * var6)));
         var1.update(var6);
         float var7 = (0.97F + 0.03F * var6) * 0.9F;
         this.pointEncode = var7;
         this.animator = var2 * 0.5F;
         this.source = var3 * 0.5F;
         var1.compute(var7, var7, var2 * 0.5F, var3 * 0.5F);
         float var8 = handle(var2 - 80.0F, 900.0F, 1120.0F);
         float var9 = handle(var3 - 70.0F, 600.0F, 780.0F);
         float var10 = (var2 - var8) * 0.5F;
         float var11 = (var3 - var9) * 0.5F;
         this.matrixBlend = new RotationAnalyticsScreen.ScreenState(var10, var11, var8, var9);
         var1.handle(20.0F);
         var1.handle(var10, var11, var8, var9, 18.0F, 1.0F);
         var1.handle(var10, var11, var8, var9, 18.0F, handle(13, 15, 21, 186));
         var1.handle(var10, var11, var8, var9, 18.0F, handle(255, 255, 255, 26), 2.0F);
         this.enabled.clear();
         this.renderer.clear();
         this.handle(var1);
         this.process(var1);
         float var12 = var10 + 24.0F;
         float var13 = var11 + 170.0F;
         float var14 = var8 - 48.0F;
         float var15 = var9 - 170.0F - 24.0F;
         switch (this.pending) {
            case 1:
               this.process(var1, var12, var13, var14, var15);
               break;
            case 2:
               this.compute(var1, var12, var13, var14, var15);
               break;
            default:
               this.handle(var1, var12, var13, var14, var15);
         }

         var1.check();
         var1.onTick();
         if (this.target && var6 <= 0.015F) {
            MinecraftClient.getInstance().execute(() -> {
               if (MinecraftClient.getInstance().currentScreen == this) {
                  MinecraftClient.getInstance().setScreen(null);
               }
            });
         }
      }
   }

   private void handle(RoundedRectRenderer var1) {
      float var2 = this.matrixBlend.instance + 24.0F;
      var1.handle(FontRegistry.config, var2, this.matrixBlend.data + 54.0F, 52.0F, "AI Lab", handle(245, 248, 255, 246));
      String var3 = "Профиль " + RotationRecorder.readServer() + "  •  " + RotationRecorder.fetchProvider();
      var1.handle(FontRegistry.instance, var2, this.matrixBlend.data + 88.0F, 28.0F, var3, handle(150, 160, 178, 220));
      float var4 = 44.0F;
      this.vectorMatch = new RotationAnalyticsScreen.ScreenState(
         this.matrixBlend.instance + this.matrixBlend.context - var4 - 18.0F, this.matrixBlend.data + 18.0F, var4, var4
      );
      boolean var5 = this.vectorMatch.handle(this.handler, this.animationDraw);
      var1.handle(
         this.vectorMatch.instance, this.vectorMatch.data, var4, var4, 10.0F, handle(var5 ? 235 : 40, var5 ? 80 : 44, var5 ? 92 : 52, var5 ? 235 : 150)
      );
      this.handle(var1, "X", this.vectorMatch, 30.0F, handle(245, 245, 250, 240));
   }

   private void process(RoundedRectRenderer var1) {
      float var2 = this.matrixBlend.instance + 24.0F;
      float var3 = this.matrixBlend.data + 108.0F;
      float var4 = 200.0F;
      float var5 = 46.0F;

      for (int var6 = 0; var6 < data.length; var6++) {
         RotationAnalyticsScreen.ScreenState var7 = new RotationAnalyticsScreen.ScreenState(var2 + var6 * (var4 + 10.0F), var3, var4, var5);
         this.itemProject[var6] = var7;
         boolean var8 = this.pending == var6;
         boolean var9 = var7.handle(this.handler, this.animationDraw);
         int var10 = var8 ? handle(96, 150, 240, 210) : handle(255, 255, 255, var9 ? 26 : 14);
         var1.handle(var7.instance, var7.data, var7.context, var7.config, 10.0F, var10);
         this.handle(var1, data[var6], var7, 30.0F, var8 ? handle(255, 255, 255, 246) : handle(180, 188, 204, 220));
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      RotationAnalyticsSnapshot var6 = this.latest;
      if (var6 != null && var6.instance) {
         var1.handle(
            FontRegistry.instance,
            var2,
            var3 + 22.0F,
            28.0F,
            "Кадров "
               + var6.context
               + "   Удары "
               + var6.config
               + "   Промахи "
               + Math.round(var6.output * 100.0F)
               + "%   Сенса "
               + String.format(Locale.ROOT, "%.2f", var6.current)
               + "   Дист "
               + compute(var6.active)
               + "-"
               + compute(var6.mode)
               + "м",
            handle(200, 208, 222, 230)
         );
         float var7 = (var4 - 18.0F) * 0.5F;
         float var8 = var3 + 44.0F;
         float var9 = (var5 - 44.0F - 18.0F) * 0.5F - 9.0F;
         this.handle(var1, var2, var8, var7, var9, var6);
         this.process(var1, var2 + var7 + 18.0F, var8, var7, var9, var6);
         float var10 = var8 + var9 + 18.0F;
         this.handle(var1, var2, var10, var7, var9, "Yaw дельты", var6.source, var6.pending, handle(110, 200, 255, 255));
         this.handle(var1, var2 + var7 + 18.0F, var10, var7, var9, "Pitch дельты", var6.target, var6.previous, handle(255, 156, 86, 255));
      } else {
         this.process(var1, var2, var3, var4, var5, "Нет записи. Вкладка Обучение -> Запись, затем вернись.");
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, RotationAnalyticsSnapshot var6) {
      this.handle(var1, var2, var3, var4, var5, "Дистанция: распределение");
      float var7 = var2 + 16.0F;
      float var8 = var3 + 44.0F;
      float var9 = var4 - 32.0F;
      float var10 = var5 - 58.0F;
      int var11 = Math.max(1, var6.renderer[0] + var6.renderer[1] + var6.renderer[2]);
      String[] var12 = new String[]{"Близко <" + compute(var6.selection), "Средне", "Далеко >" + compute(var6.enabled)};
      int[] var13 = new int[]{handle(92, 235, 182, 255), handle(110, 200, 255, 255), handle(255, 156, 86, 255)};
      float var14 = var9 / 3.0F - 14.0F;

      for (int var15 = 0; var15 < 3; var15++) {
         float var16 = (float)var6.renderer[var15] / var11;
         float var17 = var7 + var15 * (var9 / 3.0F) + 7.0F;
         float var18 = Math.max(3.0F, var16 * (var10 - 28.0F));
         var1.handle(var17, var8 + var10 - 26.0F - var18, var14, var18, 5.0F, var13[var15]);
         String var19 = Math.round(var16 * 100.0F) + "%";
         var1.handle(FontRegistry.instance, var17, var8 + var10 - 2.0F, 22.0F, var12[var15], handle(170, 178, 194, 220));
         var1.handle(FontRegistry.config, var17, var8 + var10 - 30.0F - var18, 24.0F, var19, handle(235, 240, 250, 235));
      }
   }

   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, RotationAnalyticsSnapshot var6) {
      this.handle(var1, var2, var3, var4, var5, "Скорость аима <-> дистанция");
      float var7 = var2 + 16.0F;
      float var8 = var3 + 44.0F;
      float var9 = var4 - 32.0F;
      float var10 = var5 - 64.0F;
      float var11 = Math.max(1.0F, var6.pointEncode);
      int var12 = var6.animationDraw == null ? 0 : var6.animationDraw.length;
      float var13 = var12 > 0 ? var9 / var12 : var9;

      for (int var14 = 0; var14 < var12; var14++) {
         float var15 = var6.animationDraw[var14] / var11;
         float var16 = Math.max(1.0F, var15 * (var10 - 4.0F));
         var1.handle(var7 + var14 * var13, var8 + var10 - var16, Math.max(1.0F, var13 * 0.85F), var16, 0.0F, handle(120, 170, 255, 230));
      }

      var1.handle(FontRegistry.instance, var7, var8 + var10 + 18.0F, 22.0F, compute(var6.active) + "м", handle(150, 158, 174, 200));
      String var17 = compute(var6.mode) + "м";
      var1.handle(
         FontRegistry.instance,
         var7 + var9 - TextMeasureCache.process(FontRegistry.instance, var17, 22.0F),
         var8 + var10 + 18.0F,
         22.0F,
         var17,
         handle(150, 158, 174, 200)
      );
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, String var6, int[] var7, int var8, int var9) {
      this.handle(var1, var2, var3, var4, var5, var6);
      if (var7 != null) {
         float var10 = var2 + 16.0F;
         float var11 = var3 + 44.0F;
         float var12 = var4 - 32.0F;
         float var13 = var5 - 58.0F;
         float var14 = var12 / var7.length;
         float var15 = Math.max(1.0F, var8);

         for (int var16 = 0; var16 < var7.length; var16++) {
            float var17 = Math.max(1.0F, var7[var16] / var15 * (var13 - 2.0F));
            var1.handle(var10 + var16 * var14, var11 + var13 - var17, Math.max(1.0F, var14 * 0.8F), var17, 0.0F, var9);
         }

         var1.handle(var10 + var12 * 0.5F - 0.5F, var11, 1.0F, var13, handle(255, 255, 255, 40));
      }
   }

   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      List var6 = RotationRecorder.advancePosition();
      String var7 = RotationRecorder.readServer();
      this.handle(var1, var2, var3, var4, 86.0F, "Профиль");
      RotationAnalyticsScreen.ScreenState var8 = new RotationAnalyticsScreen.ScreenState(var2 + 16.0F, var3 + 40.0F, 38.0F, 34.0F);
      RotationAnalyticsScreen.ScreenState var9 = new RotationAnalyticsScreen.ScreenState(var2 + 16.0F + 44.0F, var3 + 40.0F, 220.0F, 34.0F);
      RotationAnalyticsScreen.ScreenState var10 = new RotationAnalyticsScreen.ScreenState(var9.instance + var9.context + 8.0F, var3 + 40.0F, 38.0F, 34.0F);
      this.handle(var1, var8, "<", false, false, () -> this.handle(var6, -1));
      var1.handle(var9.instance, var9.data, var9.context, var9.config, 7.0F, handle(255, 255, 255, 16));
      this.handle(var1, var7, var9, 28.0F, handle(235, 240, 250, 235));
      this.handle(var1, var10, ">", false, false, () -> this.handle(var6, 1));
      float var11 = var3 + 104.0F;
      float var12 = (var4 - 24.0F) / 4.0F - 8.0F;
      boolean var13 = RotationRecorder.matchVector();
      boolean var14 = RotationRecorder.projectItem();
      boolean var15 = RotationRecorder.attachEvent();
      this.handle(
         var1, new RotationAnalyticsScreen.ScreenState(var2, var11, var12, 42.0F), var13 ? "Запись..." : "Запись", false, var13, RotationRecorder::handle
      );
      this.handle(var1, new RotationAnalyticsScreen.ScreenState(var2 + (var12 + 10.0F), var11, var12, 42.0F), "Стоп", true, false, () -> {
         RotationRecorder.process();
         this.handle();
      });
      this.handle(
         var1,
         new RotationAnalyticsScreen.ScreenState(var2 + (var12 + 10.0F) * 2.0F, var11, var12, 42.0F),
         var15 ? "Обучение..." : "Обучить",
         false,
         var15,
         RotationRecorder::resolve
      );
      this.handle(
         var1,
         new RotationAnalyticsScreen.ScreenState(var2 + (var12 + 10.0F) * 3.0F, var11, var12, 42.0F),
         var14 ? "Идёт" : "Запуск",
         false,
         var14,
         this::process
      );
      float var16 = var11 + 58.0F;
      this.handle(var1, var2, var16, var4, 120.0F, "Параметры");
      this.handle(
         var1,
         new RotationAnalyticsScreen.ScreenState(var2 + 16.0F, var16 + 46.0F, var4 - 32.0F, 30.0F),
         "AI Jitter (сила твоей тряски)",
         0.0F,
         2.0F,
         false,
         AttackAura.latest::compute,
         AttackAura.latest::handle
      );
      RotationAnalyticsScreen.ScreenState var17 = new RotationAnalyticsScreen.ScreenState(var2 + 16.0F, var16 + 84.0F, 230.0F, 28.0F);
      boolean var18 = AttackAura.summary.compute();
      this.handle(var1, var17, var18 ? "Логи: ВКЛ" : "Логи: ВЫКЛ", false, var18, () -> AttackAura.summary.process(!AttackAura.summary.compute()));
      RotationAnalyticsScreen.ScreenState var19 = new RotationAnalyticsScreen.ScreenState(var17.instance + var17.context + 12.0F, var16 + 84.0F, 260.0F, 28.0F);
      boolean var20 = AttackAura.matrixBlend.compute();
      this.handle(var1, var19, var20 ? "Промахи: ВКЛ" : "Промахи: ВЫКЛ", false, var20, () -> AttackAura.matrixBlend.process(!AttackAura.matrixBlend.compute()));
      RotationAnalyticsSnapshot var21 = this.latest;
      float var22 = var16 + 132.0F;
      String var23 = var21 != null && var21.instance ? String.valueOf(var21.context) : "-";
      String var24 = RotationRecorder.tick() < 0.0F ? "-" : String.format(Locale.ROOT, "%.4f", RotationRecorder.tick());
      String var25 = var21 != null && var21.instance ? "[" + var21.renderer[0] + "," + var21.renderer[1] + "," + var21.renderer[2] + "]" : "-";
      String var26 = var21 != null && var21.instance ? Math.round(var21.output * 100.0F) + "%" : "-";
      var1.handle(
         FontRegistry.instance,
         var2,
         var22 + 12.0F,
         28.0F,
         "Кадров " + var23 + "   Loss " + var24 + "   Бакеты " + var25 + "   Промахи " + var26,
         handle(195, 204, 220, 230)
      );
      var1.handle(
         FontRegistry.instance,
         var2,
         var22 + 44.0F,
         24.0F,
         "Совет: пиши на РАЗНЫХ дистанциях и веди по таргету плавно, не только флик.",
         handle(150, 158, 176, 205)
      );
   }

   private void compute(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      RotationAnalyticsSnapshot var6 = this.latest;
      if (var6 != null && var6.instance) {
         float var7 = (var5 - 18.0F) * 0.5F - 6.0F;
         this.handle(var1, var2, var3, var4, var7, "Yaw: ты vs нейросеть", var6.matrixBlend, var6.providerFetch ? var6.itemProject : null);
         this.handle(var1, var2, var3 + var7 + 18.0F, var4, var7, "Pitch: ты vs нейросеть", var6.vectorMatch, var6.providerFetch ? var6.responseCompute : null);
         if (!var6.providerFetch) {
            var1.handle(
               FontRegistry.instance, var2 + 16.0F, var3 + 34.0F, 24.0F, "Модель не обучена — оранжевой линии нет. Жми Обучить.", handle(255, 180, 110, 230)
            );
         } else {
            String var8 = var6.profileDraw < 0.0F ? "-" : String.format(Locale.ROOT, "%.4f", var6.profileDraw);
            String var9 = "Loss " + var8;
            var1.handle(
               FontRegistry.instance,
               var2 + var4 - TextMeasureCache.process(FontRegistry.instance, var9, 24.0F) - 16.0F,
               var3 + 34.0F,
               24.0F,
               var9,
               handle(150, 200, 255, 230)
            );
         }
      } else {
         this.process(var1, var2, var3, var4, var5, "Нет данных. Сначала запись и обучение.");
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, String var6, float[] var7, float[] var8) {
      this.handle(var1, var2, var3, var4, var5, var6);
      float var9 = var2 + 14.0F;
      float var10 = var3 + 42.0F;
      float var11 = var4 - 28.0F;
      float var12 = var5 - 64.0F;
      float var13 = var10 + var12 * 0.5F;
      var1.handle(var9, var13 - 0.5F, var11, 1.0F, handle(255, 255, 255, 36));
      float var14 = 6.0F;
      if (var7 != null) {
         for (float var18 : var7) {
            var14 = Math.max(var14, Math.abs(var18));
         }
      }

      if (var8 != null) {
         for (float var23 : var8) {
            var14 = Math.max(var14, Math.abs(var23));
         }
      }

      var14 = Math.min(var14, 35.0F);
      this.handle(var1, var9, var13, var11, var12 * 0.5F - 2.0F, var7, var14, handle(120, 210, 255, 235));
      this.handle(var1, var9, var13, var11, var12 * 0.5F - 2.0F, var8, var14, handle(255, 150, 90, 235));
      var1.handle(FontRegistry.instance, var9, var10 + var12 + 18.0F, 22.0F, "ты", handle(120, 210, 255, 220));
      var1.handle(FontRegistry.instance, var9 + 48.0F, var10 + var12 + 18.0F, 22.0F, "нейросеть", handle(255, 150, 90, 220));
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float[] var6, float var7, int var8) {
      if (var6 != null && var6.length != 0) {
         float var9 = var4 / var6.length;
         float var10 = var5 / var7;

         for (int var11 = 0; var11 < var6.length; var11++) {
            float var12 = MathHelper.clamp(var6[var11] * var10, -var5, var5);
            if (var12 >= 0.0F) {
               var1.handle(var2 + var11 * var9, var3 - var12, Math.max(1.0F, var9 * 0.8F), var12, var8);
            } else {
               var1.handle(var2 + var11 * var9, var3, Math.max(1.0F, var9 * 0.8F), -var12, var8);
            }
         }
      }
   }

   private void process() {
      RotationRecorder.compute();
      if (WildClient.instance != null && WildClient.instance.data != null && AttackAura.pending.config.contains("AI")) {
         AttackAura.pending.state = "AI";
         AttackAura.pending.current = AttackAura.pending.config.indexOf("AI");
         AttackAura var1 = WildClient.instance.data.handle(AttackAura.class);
         if (var1 != null && !var1.enabled) {
            var1.setEnabled(true);
         }
      }
   }

   private void handle(List<String> var1, int var2) {
      if (var1 != null && !var1.isEmpty()) {
         int var3 = var1.indexOf(RotationRecorder.readServer());
         var3 = Math.floorMod((var3 < 0 ? 0 : var3) + var2, var1.size());
         RotationRecorder.process((String)var1.get(var3));
         this.handle();
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, String var6) {
      var1.handle(var2, var3, var4, var5, 12.0F, handle(255, 255, 255, 12));
      var1.handle(var2, var3, var4, var5, 12.0F, handle(255, 255, 255, 22), 1.0F);
      var1.handle(FontRegistry.config, var2 + 16.0F, var3 + 26.0F, 28.0F, var6, handle(210, 218, 232, 235));
   }

   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, String var6) {
      float var7 = TextMeasureCache.process(FontRegistry.instance, var6, 28.0F);
      var1.handle(FontRegistry.instance, var2 + (var4 - var7) * 0.5F, var3 + var5 * 0.5F, 28.0F, var6, handle(170, 178, 196, 220));
   }

   private void handle(RoundedRectRenderer var1, RotationAnalyticsScreen.ScreenState var2, String var3, boolean var4, boolean var5, Runnable var6) {
      boolean var7 = var2.handle(this.handler, this.animationDraw);
      int var8;
      if (var5) {
         var8 = handle(96, 150, 240, 220);
      } else if (var4) {
         var8 = handle(var7 ? 230 : 150, var7 ? 78 : 52, var7 ? 90 : 60, var7 ? 230 : 170);
      } else {
         var8 = handle(255, 255, 255, var7 ? 34 : 18);
      }

      var1.handle(var2.instance, var2.data, var2.context, var2.config, 8.0F, var8);
      this.handle(var1, var3, var2, 28.0F, handle(238, 242, 250, 240));
      this.enabled.add(new RotationAnalyticsScreen.DataRecord(var2, var6));
   }

   private void handle(
      RoundedRectRenderer var1,
      RotationAnalyticsScreen.ScreenState var2,
      String var3,
      float var4,
      float var5,
      boolean var6,
      RotationAnalyticsScreen.Callback var7,
      RotationAnalyticsScreen.PrimaryCallback var8
   ) {
      var1.handle(
         FontRegistry.instance,
         var2.instance,
         var2.data - 6.0F,
         24.0F,
         var3 + "  " + String.format(Locale.ROOT, "%.2f", var7.get()),
         handle(190, 198, 214, 225)
      );
      float var9 = var2.data + 16.0F;
      var1.handle(var2.instance, var9, var2.context, 6.0F, 3.0F, handle(255, 255, 255, 30));
      float var10 = handle((var7.get() - var4) / (var5 - var4), 0.0F, 1.0F);
      var1.handle(var2.instance, var9, var2.context * var10, 6.0F, 3.0F, handle(110, 170, 255, 235));
      var1.process(var2.instance + var2.context * var10, var9 + 3.0F, 8.0F, 0.0F, 360.0F, handle(235, 242, 255, 245));
      this.renderer
         .add(
            new RotationAnalyticsScreen.PrimaryScreenState(
               var3, var4, var5, var6, var7, var8, new RotationAnalyticsScreen.ScreenState(var2.instance, var9 - 12.0F, var2.context, 30.0F)
            )
         );
   }

   private void handle(RoundedRectRenderer var1, String var2, RotationAnalyticsScreen.ScreenState var3, float var4, int var5) {
      float var6 = TextMeasureCache.process(FontRegistry.instance, var2, var4);
      var1.handle(FontRegistry.instance, var3.instance + (var3.context - var6) * 0.5F, var3.data + var3.config * 0.5F + var4 * 0.2F, var4, var2, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      float var6 = this.handle(this.handle(var1));
      float var7 = this.process(this.process(var3));
      if (this.vectorMatch.handle(var6, var7)) {
         this.close();
         return true;
      }

      for (int var8 = 0; var8 < this.itemProject.length; var8++) {
         if (this.itemProject[var8].handle(var6, var7)) {
            this.pending = var8;
            return true;
         }
      }

      for (RotationAnalyticsScreen.PrimaryScreenState var9 : this.renderer) {
         if (var9.output.handle(var6, var7)) {
            this.previous = var9;
            var9.handle(var6);
            return true;
         }
      }

      for (RotationAnalyticsScreen.DataRecord var12 : this.enabled) {
         if (var12.bounds().handle(var6, var7)) {
            var12.action().run();
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      this.previous = null;
      return super.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.previous != null) {
         this.previous.handle(this.handle(this.handle(var1)));
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.close();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void close() {
      this.target = true;
   }

   public boolean shouldPause() {
      return false;
   }

   private static void compute() {
      if (!instance) {
         instance = true;
         EventHandlerInvoker.handle(new Object() {
            @EventHandler
            public void handle(HudRenderContext var1) {
               if (var1.compute() != null && var1.compute().currentScreen instanceof RotationAnalyticsScreen var2) {
                  var2.handle(var1.resolve(), var1.apply(), var1.execute());
                  if (var1.resolve() != null) {
                     var1.resolve().compute();
                  }
               }
            }
         });
      }
   }

   private void handle(float var1, float var2) {
      this.handler = this.handle(var1);
      this.animationDraw = this.process(var2);
   }

   private float handle(float var1) {
      return this.pointEncode <= 0.0F ? var1 : (var1 - this.animator) / this.pointEncode + this.animator;
   }

   private float process(float var1) {
      return this.pointEncode <= 0.0F ? var1 : (var1 - this.source) / this.pointEncode + this.source;
   }

   private void resolve() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.getWindow() != null && var1.mouse != null) {
         double var2 = var1.getWindow().getFramebufferWidth();
         double var4 = var1.getWindow().getFramebufferHeight();
         if (!(var2 <= 0.0) && !(var4 <= 0.0)) {
            double var6 = var1.mouse.getX();
            double var8 = var1.mouse.getY();
            if (var6 >= 0.0 && var8 >= 0.0 && var6 <= var2 + 2.0 && var8 <= var4 + 2.0) {
               this.handle((float)var6, (float)var8);
            }
         }
      }
   }

   private float handle(double var1) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      if (var3 != null && var3.getWindow() != null) {
         int var4 = var3.getWindow().getFramebufferWidth();
         int var5 = var3.getWindow().getScaledWidth();
         return var4 > 0 && var5 > 0 ? (float)(var1 * var4 / Math.max(1.0, var5)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private float process(double var1) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      if (var3 != null && var3.getWindow() != null) {
         int var4 = var3.getWindow().getFramebufferHeight();
         int var5 = var3.getWindow().getScaledHeight();
         return var4 > 0 && var5 > 0 ? (float)(var1 * var4 / Math.max(1.0, var5)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private static String compute(float var0) {
      return String.format(Locale.ROOT, "%.1f", var0);
   }

   static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static int handle(int var0, int var1, int var2, int var3) {
      return RoundedRectRenderer.ColorState.resolve(var0, var1, var2, Math.max(0, Math.min(255, var3)));
   }

   interface Callback {
      float get();
   }

   record DataRecord(RotationAnalyticsScreen.ScreenState bounds, Runnable action) {
   }

   interface PrimaryCallback {
      void set(float var1);
   }

   final class PrimaryScreenState {
      final String instance;
      final float data;
      final float context;
      final boolean config;
      final RotationAnalyticsScreen.Callback state;
      final RotationAnalyticsScreen.PrimaryCallback cache;
      final RotationAnalyticsScreen.ScreenState output;

      PrimaryScreenState(
         String var2,
         float var3,
         float var4,
         boolean var5,
         RotationAnalyticsScreen.Callback var6,
         RotationAnalyticsScreen.PrimaryCallback var7,
         RotationAnalyticsScreen.ScreenState var8
      ) {
         this.instance = var2;
         this.data = var3;
         this.context = var4;
         this.config = var5;
         this.state = var6;
         this.cache = var7;
         this.output = var8;
      }

      void handle(float var1) {
         float var2 = RotationAnalyticsScreen.handle((var1 - this.output.instance) / this.output.context, 0.0F, 1.0F);
         float var3 = this.data + var2 * (this.context - this.data);
         if (this.config) {
            var3 = Math.round(var3);
         } else {
            var3 = Math.round(var3 * 100.0F) / 100.0F;
         }

         this.cache.set(RotationAnalyticsScreen.handle(var3, this.data, this.context));
      }
   }

   static final class ScreenState {
      final float instance;
      final float data;
      final float context;
      final float config;

      ScreenState(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }

      boolean handle(float var1, float var2) {
         return var1 >= this.instance && var1 <= this.instance + this.context && var2 >= this.data && var2 <= this.data + this.config;
      }
   }
}
