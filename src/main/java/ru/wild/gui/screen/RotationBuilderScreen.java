package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.HudRenderContext;
import ru.wild.config.rotation.RotationPreset;
import ru.wild.config.rotation.RotationPresetRepository;
import ru.wild.gui.widget.EasingGraphRenderer;
import ru.wild.modules.combat.AttackAura;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.RoundedRectRenderer;

public final class RotationBuilderScreen extends Screen {
   private static volatile boolean instance;
   private static final String[] data = new String[]{"FunTime", "Spooky", "Holy", "Matrix", "Smooth", "Snap", "Custom"};
   private final RotationPreset context;
   private final RotationPresetRepository config;
   private List<RotationBuilderScreen.PrimaryAnimationState> state = new ArrayList<>();
   private final List<RotationBuilderScreen.PrimaryAnimationState> cache = new ArrayList<>();
   private final List<RotationBuilderScreen.PrimaryAnimationState> output = new ArrayList<>();
   private final List<RotationBuilderScreen.PrimaryDataRecord> current = new ArrayList<>();
   private final Map<String, DoubleAnimator> active = new HashMap<>();
   private final Map<String, DoubleAnimator> mode = new HashMap<>();
   private final Map<String, DoubleAnimator> selection = new HashMap<>();
   private final Map<String, DoubleAnimator> enabled = new HashMap<>();
   private final DoubleAnimator renderer = new DoubleAnimator();
   private final DoubleAnimator handler = new DoubleAnimator();
   private final DoubleAnimator animationDraw = new DoubleAnimator();
   private final DoubleAnimator pointEncode = new DoubleAnimator();
   private int animator;
   private RotationBuilderScreen.AnimationState source = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState target = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState pending = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState previous = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState latest = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState summary = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState matrixBlend = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState vectorMatch = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState itemProject = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState responseCompute = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState providerFetch = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState profileDraw = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState vectorPerform = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState eventAttach = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState serverRead = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState positionAdvance = RotationBuilderScreen.AnimationState.handle();
   private final List<RotationBuilderScreen.DataRecord> frameCheck = new ArrayList<>();
   private String moduleCollect = "";
   private long providerClose;
   private RotationBuilderScreen.AnimationState presetSave = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState windowConvert = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState presetWrite = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState colorMeasure = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState animationSchedule = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState rendererScan = RotationBuilderScreen.AnimationState.handle();
   private RotationBuilderScreen.AnimationState sourceBuild = RotationBuilderScreen.AnimationState.handle();
   private float outputCollapse;
   private float profileInvoke;
   private float sourceSchedule;
   private float timerRender;
   private RotationBuilderScreen.PrimaryAnimationState scaleSave;
   private int colorCompute = -1;
   private int scaleAdapt = -1;
   private float textureRun;
   private float indexBind;
   private float actionRead;
   private float configCollapse;
   private float dataValidate;
   private float scaleRender;
   private float clientRefresh;
   private float keyFilter;
   private boolean requestAdapt;
   private long timerMeasure;
   private int vectorEncode;
   private long requestReceive;
   private boolean windowProcess;
   private boolean packetSave;
   private boolean entryAnimate;
   private boolean playerCollect;
   private String stateApply = "";
   private String matrixFilter;
   private float layerSample;
   private float worldSend;

   public RotationBuilderScreen() {
      super(Text.literal("Rotation Builder"));
      this.context = RotationPreset.handle();
      this.config = RotationPresetRepository.handle();
      this.renderer.apply(0.0);
      this.handler.apply(1.0);
      this.animationDraw.apply(0.0);
      this.pointEncode.apply(0.0);
      initializeTemplates();
      this.handle();
   }

   private void handle() {
      this.cache.clear();
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость Yaw мин", 0.0F, 180.0F, 35.0F, true, () -> this.context.selection, var1 -> this.context.selection = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость Yaw макс", 0.0F, 180.0F, 55.0F, true, () -> this.context.enabled, var1 -> this.context.enabled = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость Pitch мин", 0.0F, 120.0F, 6.0F, true, () -> this.context.renderer, var1 -> this.context.renderer = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость Pitch макс", 0.0F, 120.0F, 12.0F, true, () -> this.context.handler, var1 -> this.context.handler = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость удара Yaw", 0.0F, 240.0F, 65.0F, true, () -> this.context.animationDraw, var1 -> this.context.animationDraw = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость удара Pitch", 0.0F, 240.0F, 22.0F, true, () -> this.context.pointEncode, var1 -> this.context.pointEncode = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Рандом Yaw", 0.0F, 20.0F, 4.0F, false, () -> this.context.animator, var1 -> this.context.animator = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Рандом Pitch", 0.0F, 20.0F, 3.0F, false, () -> this.context.source, var1 -> this.context.source = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Осцилляция X", 0.0F, 1.0F, 0.2F, false, () -> this.context.target, var1 -> this.context.target = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Осцилляция Y", 0.0F, 1.0F, 0.12F, false, () -> this.context.pending, var1 -> this.context.pending = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Частота осцилляции", 0.2F, 3.0F, 1.0F, false, () -> this.context.previous, var1 -> this.context.previous = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Боковая точка", 0.0F, 0.6F, 0.0F, false, () -> this.context.latest, var1 -> this.context.latest = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость возврата", 5.0F, 120.0F, 30.0F, true, () -> this.context.summary, var1 -> this.context.summary = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Смена точки (сек)", 0.1F, 3.0F, 0.9F, false, () -> this.context.itemProject, var1 -> this.context.itemProject = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость смены точек", 0.1F, 3.0F, 1.0F, false, () -> this.context.moduleCollect, var1 -> this.context.moduleCollect = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Интерполяция оверлея", 0.05F, 1.0F, 0.35F, false, () -> this.context.positionAdvance, var1 -> this.context.positionAdvance = var1
            )
         );
      this.cache
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Скорость прицела", 0.2F, 3.0F, 1.0F, false, () -> this.context.frameCheck, var1 -> this.context.frameCheck = var1
            )
         );
      this.output.clear();
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Смещение Yaw", -30.0F, 30.0F, 0.0F, true, () -> this.context.providerFetch, var1 -> this.context.providerFetch = var1
            )
         );
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Смещение Pitch", -30.0F, 30.0F, 0.0F, true, () -> this.context.profileDraw, var1 -> this.context.profileDraw = var1
            )
         );
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Pitch минимум", -90.0F, 0.0F, -90.0F, true, () -> this.context.vectorPerform, var1 -> this.context.vectorPerform = var1
            )
         );
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Pitch максимум", 0.0F, 90.0F, 90.0F, true, () -> this.context.eventAttach, var1 -> this.context.eventAttach = var1
            )
         );
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Упреждение цели", 0.0F, 0.6F, 0.0F, false, () -> this.context.serverRead, var1 -> this.context.serverRead = var1
            )
         );
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Угол отвода", 0.0F, 90.0F, 80.0F, true, () -> this.context.presetSave, var1 -> this.context.presetSave = var1
            )
         );
      this.output
         .add(
            new RotationBuilderScreen.PrimaryAnimationState(
               "Интервал отвода (сек)", 1.5F, 15.0F, 5.0F, false, () -> this.context.windowConvert, var1 -> this.context.windowConvert = var1
            )
         );
      this.state = this.cache;
   }

   public boolean shouldPause() {
      return false;
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      this.compute(this.handle((double)var2), this.process((double)var3));
      super.render(var1, var2, var3, var4);
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public void handle(RoundedRectRenderer var1, int var2, int var3) {
      if (var1 != null && var2 > 0 && var3 > 0) {
         this.drawAnimation();
         this.render2();
         this.renderer.handle();
         this.renderer
            .handle(this.entryAnimate ? 0.0 : 1.0, this.entryAnimate ? 0.18F : 0.22F, this.entryAnimate ? Easings.renderer : Easings.serverRead, false);
         float var4 = handle(this.renderer.update(), 0.0F, 1.0F);
         var1.handle(0.0F, 0.0F, var2, var3, 0.0F, handle(0, 0, 0, Math.round(150.0F * var4)));
         var1.update(var4);
         float var5 = 0.97F + 0.03F * var4;
         var1.compute(var5, var5, var2 * 0.5F, var3 * 0.5F);
         this.process(var1, var2, var3);
         this.handle(var1);
         this.handle(var1, this.process());
         this.update(var1);
         this.process(var1);
         var1.check();
         var1.onTick();
         if (this.entryAnimate && var4 <= 0.015F) {
            this.compute();
         }
      }
   }

   private float process() {
      long var1 = System.currentTimeMillis();
      if (this.timerMeasure == 0L) {
         this.timerMeasure = var1;
      }

      float var3 = (float)(var1 - this.timerMeasure) / 1000.0F;
      this.timerMeasure = var1;
      return Math.min(0.1F, Math.max(0.0F, var3));
   }

   private void process(RoundedRectRenderer var1, int var2, int var3) {
      float var4 = handle(var2 - 120.0F, 620.0F, 780.0F);
      float var5 = handle(var3 - 120.0F, 420.0F, 520.0F);
      float var6 = (var2 - var4) * 0.5F;
      float var7 = (var3 - var5) * 0.5F;
      this.source = new RotationBuilderScreen.AnimationState(var6, var7, var4, var5);
      var1.handle(20.0F);
      var1.handle(var6, var7, var4, var5, 16.0F, 1.0F);
      var1.handle(var6, var7, var4, var5, 16.0F, handle(13, 15, 21, 180));
      var1.handle(var6, var7, var4, var5, 16.0F, handle(255, 255, 255, 26), 2.0F);
   }

   private void handle(RoundedRectRenderer var1) {
      float var2 = this.source.instance + 18.0F;
      float var3 = this.source.data + 16.0F;
      var1.handle(FontRegistry.config, var2, var3 + 14.0F, 26.0F, "Rotation Builder", handle(245, 248, 255, 246));
      boolean var4 = System.currentTimeMillis() < this.providerClose && !this.moduleCollect.isEmpty();
      if (var4) {
         float var5 = handle(this.animationDraw.update(), 0.0F, 1.0F);
         long var6 = this.providerClose - System.currentTimeMillis();
         if (var6 < 400L) {
            var5 *= handle((float)var6 / 400.0F, 0.0F, 1.0F);
         }

         var1.handle(FontRegistry.instance, var2, var3 + 34.0F, 22.0F, this.moduleCollect, handle(120, 220, 150, Math.round(235.0F * var5)));
      } else {
         var1.handle(
            FontRegistry.instance,
            var2,
            var3 + 32.0F,
            24.0F,
            "Текущий присет ротации: " + this.context.active + " - " + this.context.current,
            handle(154, 164, 180, 222)
         );
      }

      this.current.clear();
      float var18 = var2;
      float var19 = this.source.data + 58.0F;

      for (String var10 : data) {
         float var11 = TextMeasureCache.process(FontRegistry.instance, var10, 14.0F) + 18.0F;
         boolean var12 = var10.equals(this.context.active)
            || var10.equals(this.context.output)
            || "Custom".equals(var10) && "Custom".equals(this.context.current);
         this.current.add(new RotationBuilderScreen.PrimaryDataRecord(var10, new RotationBuilderScreen.AnimationState(var18, var19, var11, 24.0F), var12));
         var18 += var11 + 7.0F;
      }

      for (RotationBuilderScreen.PrimaryDataRecord var22 : this.current) {
         float var23 = this.handle("chip." + var22.label, var22.bounds.handle(this.actionRead, this.configCollapse));
         float var24 = this.process("chip." + var22.label, var22.active);
         float var25 = this.execute("chip." + var22.label);
         float var26 = 1.0F - var25 * 0.06F;
         float var13 = var22.bounds.instance + var22.bounds.context * 0.5F;
         float var14 = var22.bounds.data + var22.bounds.config * 0.5F;
         int var15 = handle(handle(255, 255, 255, Math.round(12.0F + var23 * 18.0F)), handle(95, 190, 255, 64), var24);
         var1.compute(var26, var26, var13, var14);
         var1.handle(var22.bounds.instance, var22.bounds.data, var22.bounds.context, var22.bounds.config, 7.0F, var15);
         float var16 = TextMeasureCache.process(FontRegistry.instance, var22.label, 20.0F);
         int var17 = handle(handle(188, 198, 212, 226), handle(235, 248, 255, 246), var24);
         var1.handle(FontRegistry.instance, var22.bounds.instance + (var22.bounds.context - var16) * 0.5F, var22.bounds.data + 16.0F, 20.0F, var22.label, var17);
         var1.check();
      }

      this.latest = new RotationBuilderScreen.AnimationState(this.source.instance + this.source.context - 18.0F - 26.0F, var3, 26.0F, 26.0F);
      this.handle(var1, "screen.close", this.latest, "l", FontRegistry.state, 26.0F, false);
      this.responseCompute = new RotationBuilderScreen.AnimationState(this.latest.instance - 8.0F - 26.0F, var3, 26.0F, 26.0F);
      this.handle(var1, "presets.open", this.responseCompute, "I", FontRegistry.context, 18.0F, this.windowProcess);
      float var21 = 104.0F;
      this.summary = new RotationBuilderScreen.AnimationState(this.source.instance + this.source.context - 18.0F - var21, var19, var21, 24.0F);
      this.handle(var1, "clear", this.summary, "Очистить точки", handle(255, 120, 120, 26), handle(255, 120, 120, 70), handle(245, 220, 220, 232));
      this.matrixBlend = new RotationBuilderScreen.AnimationState(this.summary.instance - 8.0F - 78.0F, var19, 78.0F, 24.0F);
      this.handle(var1, "reset", this.matrixBlend, "Сброс", handle(255, 255, 255, 14), handle(255, 255, 255, 36), handle(235, 242, 255, 230));
      this.itemProject = new RotationBuilderScreen.AnimationState(this.matrixBlend.instance - 8.0F - 92.0F, var19, 92.0F, 24.0F);
      this.handle(var1, "paste", this.itemProject, "Вставить", handle(120, 200, 255, 22), handle(120, 200, 255, 66), handle(225, 240, 255, 232));
      this.vectorMatch = new RotationBuilderScreen.AnimationState(this.itemProject.instance - 8.0F - 100.0F, var19, 100.0F, 24.0F);
      this.handle(var1, "copy", this.vectorMatch, "Копировать", handle(120, 255, 180, 22), handle(120, 255, 180, 66), handle(225, 255, 240, 232));
   }

   private void handle(RoundedRectRenderer var1, String var2, RotationBuilderScreen.AnimationState var3, String var4, FontObject var5, float var6, boolean var7) {
      float var8 = this.handle(var2, var3.handle(this.actionRead, this.configCollapse));
      float var9 = this.process(var2, var7);
      float var10 = this.execute(var2);
      float var11 = 1.0F - var10 * 0.08F;
      int var12 = handle(handle(255, 255, 255, Math.round(10.0F + var8 * 18.0F)), handle(95, 190, 255, 62), var9);
      int var13 = handle(handle(255, 255, 255, 22), handle(95, 210, 255, 112), var9);
      var1.compute(var11, var11, var3.instance + var3.context * 0.5F, var3.data + var3.config * 0.5F);
      var1.handle(var3.instance, var3.data, var3.context, var3.config, 8.0F, var12);
      float var14 = TextMeasureCache.process(var5, var4, var6);
      var1.handle(
         var5,
         var3.instance + (var3.context - var14) * 0.5F,
         var3.data + var3.config * 0.5F + var6 * 0.28F,
         var6,
         var4,
         handle(226, 239, 250, Math.round(224.0F + var8 * 26.0F))
      );
      var1.check();
   }

   private void handle(RoundedRectRenderer var1, String var2, RotationBuilderScreen.AnimationState var3, String var4, int var5, int var6, int var7) {
      float var8 = this.handle(var2, var3.handle(this.actionRead, this.configCollapse));
      float var9 = this.execute(var2);
      float var10 = 1.0F - var9 * 0.07F;
      float var11 = var3.instance + var3.context * 0.5F;
      float var12 = var3.data + var3.config * 0.5F;
      int var13 = handle(var5, var6, var8);
      var1.compute(var10, var10, var11, var12);
      var1.handle(var3.instance, var3.data, var3.context, var3.config, 7.0F, var13);
      this.handle(var1, var4, var3, 22.0F, var7);
      var1.check();
   }

   private void process(RoundedRectRenderer var1) {
      float var2 = handle(this.pointEncode.update(), 0.0F, 1.0F);
      if (var2 <= 0.01F) {
         this.providerFetch = RotationBuilderScreen.AnimationState.handle();
         this.frameCheck.clear();
      } else {
         float var3 = Math.min(336.0F, this.source.context - 36.0F);
         float var4 = this.source.config - 72.0F;
         float var5 = this.source.instance + this.source.context - var3 - 18.0F;
         float var6 = this.source.data + 54.0F;
         float var7 = 1.0F - (float)Math.pow(1.0F - var2, 3.0);
         float var8 = var6 - 28.0F * (1.0F - var7);
         this.providerFetch = new RotationBuilderScreen.AnimationState(var5, var8, var3, var4);
         var1.handle(this.source.instance, this.source.data, this.source.context, this.source.config, 16.0F, handle(13, 15, 21, Math.round(180.0F * var2)));
         var1.update(var2);
         var1.handle(var5, var8, var3, var4, 14.0F, handle(13, 15, 21, 255));
         float var9 = 14.0F;
         var1.handle(FontRegistry.config, var5 + var9, var8 + 23.0F, 24.0F, "Сохранённые ротации", handle(242, 247, 255, 246));
         var1.handle(FontRegistry.instance, var5 + var9, var8 + 41.0F, 22.0F, "Локальные пресеты текущего конструктора", handle(142, 154, 174, 210));
         this.profileDraw = new RotationBuilderScreen.AnimationState(var5 + var3 - var9 - 24.0F, var8 + 12.0F, 24.0F, 26.0F);
         this.handle(var1, "presets.close", this.profileDraw, "l", FontRegistry.state, 26.0F, false);
         this.vectorPerform = new RotationBuilderScreen.AnimationState(var5 + var9, var8 + 54.0F, var3 - var9 * 2.0F, 30.0F);
         float var10 = this.handle("presets.name", this.vectorPerform.handle(this.actionRead, this.configCollapse));
         float var11 = this.process("presets.name.active", this.packetSave);
         int var12 = handle(handle(255, 255, 255, Math.round(10.0F + var10 * 8.0F)), handle(95, 190, 255, 28), var11);
         int var13 = handle(handle(255, 255, 255, 24), handle(95, 210, 255, 124), var11);
         var1.handle(this.vectorPerform.instance, this.vectorPerform.data, this.vectorPerform.context, this.vectorPerform.config, 8.0F, var12);
         String var14 = this.stateApply.isEmpty() && !this.packetSave ? "Название пресета" : this.stateApply;
         int var15 = this.stateApply.isEmpty() && !this.packetSave ? handle(128, 140, 158, 190) : handle(229, 238, 250, 236);
         String var16 = this.handle(var14, this.vectorPerform.context - 22.0F, 21.0F);
         var1.handle(FontRegistry.instance, this.vectorPerform.instance + 10.0F, this.vectorPerform.data + 20.0F, 21.0F, var16, var15);
         if (this.packetSave && System.currentTimeMillis() / 480L % 2L == 0L) {
            float var17 = this.vectorPerform.instance + 10.0F + TextMeasureCache.process(FontRegistry.instance, var16, 21.0F) + 1.0F;
            var1.handle(var17, this.vectorPerform.data + 7.0F, 1.0F, 16.0F, 0.5F, handle(110, 215, 255, 230));
         }

         float var36 = var8 + 92.0F;
         float var18 = (var3 - var9 * 2.0F - 8.0F) * 0.5F;
         this.eventAttach = new RotationBuilderScreen.AnimationState(var5 + var9, var36, var18, 28.0F);
         this.serverRead = new RotationBuilderScreen.AnimationState(this.eventAttach.instance + var18 + 8.0F, var36, var18, 28.0F);
         this.handle(
            var1, "presets.create", this.eventAttach, "Сохранить новый", handle(95, 210, 255, 22), handle(95, 210, 255, 62), handle(228, 247, 255, 238)
         );
         var1.update(this.matrixFilter == null ? 0.42F : 1.0F);
         this.handle(var1, "presets.update", this.serverRead, "Обновить", handle(120, 255, 180, 18), handle(120, 255, 180, 54), handle(226, 255, 240, 232));
         var1.onTick();
         float var19 = var8 + 132.0F;
         float var20 = var8 + var4 - var9;
         this.positionAdvance = new RotationBuilderScreen.AnimationState(var5 + var9, var19, var3 - var9 * 2.0F, Math.max(20.0F, var20 - var19));
         var1.handle(
            this.positionAdvance.instance, this.positionAdvance.data, this.positionAdvance.context, this.positionAdvance.config, 8.0F, 8.0F, 8.0F, 8.0F
         );
         List var21 = this.config.process();
         this.frameCheck.clear();
         float var22 = 58.0F;
         float var23 = 8.0F;
         float var24 = var19 - this.layerSample;
         if (var21.isEmpty()) {
            var1.handle(this.positionAdvance.instance, this.positionAdvance.data, this.positionAdvance.context, 64.0F, 10.0F, handle(255, 255, 255, 8));
            this.handle(
               var1,
               "Сохранённых пресетов пока нет",
               new RotationBuilderScreen.AnimationState(this.positionAdvance.instance, this.positionAdvance.data, this.positionAdvance.context, 64.0F),
               24.0F,
               handle(145, 157, 176, 206)
            );
         } else {
            for (RotationPresetRepository.NamedEntry var26 : (List<RotationPresetRepository.NamedEntry>) var21) {
               RotationBuilderScreen.AnimationState var27 = new RotationBuilderScreen.AnimationState(
                  this.positionAdvance.instance, var24, this.positionAdvance.context, var22
               );
               boolean var28 = var26.id().equals(this.matrixFilter);
               float var29 = this.handle("preset.row." + var26.id(), var27.handle(this.actionRead, this.configCollapse));
               float var30 = this.process("preset.row.active." + var26.id(), var28);
               int var31 = handle(handle(255, 255, 255, Math.round(12.0F + var29 * 13.0F)), handle(95, 190, 255, 34), var30);
               var1.handle(var27.instance, var27.data, var27.context, var27.config, 10.0F, var31);
               String var32 = this.handle(var26.name(), var27.context - 154.0F, 24.0F);
               var1.handle(
                  FontRegistry.config,
                  var27.instance + 11.0F,
                  var27.data + 25.0F,
                  24.0F,
                  var32,
                  var28 ? handle(231, 248, 255, 246) : handle(218, 227, 240, 232)
               );
               var1.handle(
                  FontRegistry.instance,
                  var27.instance + 11.0F,
                  var27.data + 40.0F,
                  20.0F,
                  var28 ? "Выбран для редактирования" : "Нажмите, чтобы выбрать",
                  handle(135, 149, 169, 196)
               );
               RotationBuilderScreen.AnimationState var33 = new RotationBuilderScreen.AnimationState(
                  var27.instance + var27.context - 128.0F, var27.data + 9.0F, 72.0F, 22.0F
               );
               RotationBuilderScreen.AnimationState var34 = new RotationBuilderScreen.AnimationState(
                  var27.instance + var27.context - 50.0F, var27.data + 9.0F, 20.0F, 22.0F
               );
               RotationBuilderScreen.AnimationState var35 = new RotationBuilderScreen.AnimationState(
                  var27.instance + var27.context - 24.0F, var27.data + 9.0F, 20.0F, 22.0F
               );
               this.handle(var1, "preset.apply." + var26.id(), var33, "Применить", false);
               this.process(var1, "preset.copy." + var26.id(), var34, "k", FontRegistry.context, 18.0F, false);
               this.process(var1, "preset.delete." + var26.id(), var35, "l", FontRegistry.state, 20.0F, true);
               this.frameCheck.add(new RotationBuilderScreen.DataRecord(var26, var27, var33, var34, var35));
               var24 += var22 + var23;
            }
         }

         var1.apply();
         float var37 = var21.isEmpty() ? 64.0F : var21.size() * (var22 + var23) - var23;
         this.worldSend = Math.max(0.0F, var37 - this.positionAdvance.config);
         this.layerSample = handle(this.layerSample, 0.0F, this.worldSend);
         if (this.worldSend > 0.0F) {
            float var38 = this.positionAdvance.config;
            float var39 = Math.max(30.0F, var38 * (var38 / (var38 + this.worldSend)));
            float var40 = this.positionAdvance.data + (var38 - var39) * (this.layerSample / this.worldSend);
            var1.handle(
               this.positionAdvance.instance + this.positionAdvance.context - 3.0F, this.positionAdvance.data, 2.0F, var38, 1.0F, handle(255, 255, 255, 14)
            );
            var1.handle(this.positionAdvance.instance + this.positionAdvance.context - 3.0F, var40, 2.0F, var39, 1.0F, handle(95, 210, 255, 116));
         }

         var1.onTick();
      }
   }

   private void handle(RoundedRectRenderer var1, String var2, RotationBuilderScreen.AnimationState var3, String var4, boolean var5) {
      this.process(var1, var2, var3, var4, FontRegistry.instance, var4.length() > 2 ? 16.0F : 19.0F, var5);
   }

   private void process(
      RoundedRectRenderer var1, String var2, RotationBuilderScreen.AnimationState var3, String var4, FontObject var5, float var6, boolean var7
   ) {
      float var8 = this.handle(var2, var3.handle(this.actionRead, this.configCollapse));
      float var9 = this.execute(var2);
      int var10 = var7 ? handle(255, 105, 120, 18) : handle(255, 255, 255, 12);
      int var11 = var7 ? handle(255, 105, 120, 54) : handle(95, 210, 255, 42);
      int var12 = var7 ? handle(255, 204, 210, 232) : handle(218, 235, 248, 226);
      var1.compute(1.0F - var9 * 0.08F, 1.0F - var9 * 0.08F, var3.instance + var3.context * 0.5F, var3.data + var3.config * 0.5F);
      var1.handle(var3.instance, var3.data, var3.context, var3.config, 6.0F, handle(var10, var11, var8));
      float var13 = TextMeasureCache.process(var5, var4, var6);
      var1.handle(var5, var3.instance + (var3.context - var13) * 0.5F, var3.data + var3.config * 0.5F + var6 * 0.28F, var6, var4, var12);
      var1.check();
   }

   private String handle(String var1, float var2, float var3) {
      if (var1 != null && !var1.isEmpty()) {
         String var4 = var1;

         while (var4.length() > 1 && TextMeasureCache.process(FontRegistry.instance, var4, var3) > var2) {
            var4 = var4.substring(1);
         }

         return var4;
      } else {
         return "";
      }
   }

   private void handle(RoundedRectRenderer var1, float var2) {
      float var3 = this.source.data + 90.0F;
      float var4 = this.source.instance + 18.0F;
      float var5 = 238.0F;
      float var6 = this.source.data + this.source.config - var3 - 18.0F;
      this.target = new RotationBuilderScreen.AnimationState(var4, var3, var5, var6);
      var1.handle(var4, var3, var5, var6, 14.0F, handle(255, 255, 255, 10));
      var1.handle(var4, var3, var5, var6, 14.0F, handle(255, 255, 255, 20), 2.0F);
      float var7 = handle(this.handler.update(), 0.0F, 1.0F);
      var1.update(var7);
      var1.handle(
         FontRegistry.instance,
         var4 + 12.0F,
         var3 + 18.0F,
         24.0F,
         this.animator == 0 ? "Привью режим поведение ротации" : "Превью вектора",
         handle(176, 186, 202, 224)
      );
      float var8 = var3 + 30.0F;
      float var9 = var6 - 46.0F;
      this.timerRender = var9 * 0.82F;
      this.sourceSchedule = this.timerRender * 0.42F;
      this.outputCollapse = var4 + var5 * 0.5F;
      this.profileInvoke = var8 + var9 - 10.0F;
      if (this.animator == 0) {
         this.process(var2);
      } else {
         this.handle(var2);
      }

      EasingGraphRenderer.handle(
         var1,
         var4 + 4.0F,
         var8,
         var5 - 8.0F,
         var9,
         this.outputCollapse,
         this.profileInvoke,
         this.sourceSchedule,
         this.timerRender,
         this.animator == 0 ? 1.0F : 0.55F
      );
      if (this.animator == 0) {
         this.resolve(var1);
         this.compute(var1);
      } else {
         this.handle(var1, var4, var8, var5, var9);
      }

      var1.handle(
         FontRegistry.instance,
         var4 + 12.0F,
         var3 + var6 + 13.0F,
         18.0F,
         this.animator == 0
            ? "ЛКМ - точка на модели : ПКМ - удалить · " + this.context.presetWrite.size() + "/12"
            : "Голубой - база/смещение · жёлтый - упреждение · красный - итог",
         handle(146, 156, 172, 206)
      );
      var1.onTick();
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      EasingGraphRenderer.handle(
         var1,
         this.outputCollapse,
         this.profileInvoke,
         this.sourceSchedule,
         this.timerRender,
         this.context.providerFetch,
         this.context.profileDraw,
         this.context.vectorPerform,
         this.context.eventAttach,
         this.context.serverRead,
         this.dataValidate,
         this.scaleRender,
         1.0F
      );
      var1.handle(
         FontRegistry.instance,
         var2 + 12.0F,
         var3 + 6.0F,
         22.0F,
         String.format("Yaw %.1f° - Pitch %.1f°", this.clientRefresh, this.keyFilter),
         handle(210, 220, 235, 220)
      );
   }

   private void handle(float var1) {
      float var2 = this.context.providerFetch;
      float var3 = Math.max(this.context.vectorPerform, Math.min(this.context.eventAttach, this.context.profileDraw));
      float var4 = Math.max(0.05F, this.context.positionAdvance) * (0.5F + this.context.frameCheck * 0.5F) * Math.min(1.0F, var1 * 30.0F + 0.15F);
      float[] var5 = EasingGraphRenderer.process(this.outputCollapse, this.profileInvoke, this.sourceSchedule, this.timerRender, var2, var3);
      if (!this.requestAdapt) {
         this.clientRefresh = var2;
         this.keyFilter = var3;
         this.dataValidate = var5[0];
         this.scaleRender = var5[1];
         this.requestAdapt = true;
      } else {
         this.clientRefresh = this.clientRefresh + (var2 - this.clientRefresh) * var4;
         this.keyFilter = this.keyFilter + (var3 - this.keyFilter) * var4;
         float[] var6 = EasingGraphRenderer.process(
            this.outputCollapse, this.profileInvoke, this.sourceSchedule, this.timerRender, this.clientRefresh, this.keyFilter
         );
         this.dataValidate = this.dataValidate + (var6[0] - this.dataValidate) * var4;
         this.scaleRender = this.scaleRender + (var6[1] - this.scaleRender) * var4;
      }
   }

   private void compute(RoundedRectRenderer var1) {
      float var2 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 320.0);

      for (int var3 = 0; var3 < this.context.presetWrite.size(); var3++) {
         RotationPreset.State var4 = this.context.presetWrite.get(var3);
         float var5 = this.outputCollapse + var4.instance * this.sourceSchedule;
         float var6 = this.profileInvoke - var4.data * this.timerRender;
         boolean var7 = var3 == this.scaleAdapt;
         float var8 = this.execute("point." + var3);
         float var9 = 1.0F + var8 * 0.35F;
         if (var7) {
            float var10 = (8.0F + var2 * 2.0F) * var9;
            var1.handle(var5 - var10, var6 - var10, var10 * 2.0F, var10 * 2.0F, 6.0F, handle(95, 210, 255, 70));
         }

         float var11 = 4.0F * var9;
         var1.handle(var5 - var11, var6 - var11, var11 * 2.0F, var11 * 2.0F, 6.0F, handle(95, 210, 255, 238));
      }
   }

   private void resolve(RoundedRectRenderer var1) {
      float var2 = 3.0F + (this.context.animator + this.context.source) * 0.6F;
      var1.handle(this.dataValidate - var2, this.scaleRender - var2, var2 * 2.0F, var2 * 2.0F, 12.0F, handle(255, 110, 130, 42));
      var1.handle(this.dataValidate - 7.0F, this.scaleRender - 0.7F, 14.0F, 1.4F, 0.0F, handle(255, 90, 110, 235));
      var1.handle(this.dataValidate - 0.7F, this.scaleRender - 7.0F, 1.4F, 14.0F, 0.0F, handle(255, 90, 110, 235));
   }

   private void process(float var1) {
      long var4 = System.currentTimeMillis();
      float var2;
      float var3;
      if (!this.context.presetWrite.isEmpty()) {
         if (var4 >= this.requestReceive) {
            if ("Random".equals(this.context.vectorMatch)) {
               this.vectorEncode = (int)(Math.random() * this.context.presetWrite.size());
            } else {
               this.vectorEncode = (this.vectorEncode + 1) % this.context.presetWrite.size();
            }

            this.requestReceive = var4 + (long)(this.context.itemProject * 1000.0F / Math.max(0.1F, this.context.moduleCollect));
         }

         RotationPreset.State var6 = this.context.presetWrite.get(Math.min(this.vectorEncode, this.context.presetWrite.size() - 1));
         var2 = this.outputCollapse + var6.instance * this.sourceSchedule;
         var3 = this.profileInvoke - var6.data * this.timerRender;
      } else {
         float[] var14 = EasingGraphRenderer.handle(this.outputCollapse, this.profileInvoke, this.sourceSchedule, this.timerRender, 0.0F, 0.5625F);
         var2 = var14[0];
         var3 = var14[1];
      }

      var2 += (float)(Math.sin(var4 / (250.0 / Math.max(0.2F, this.context.previous))) * this.context.target * this.sourceSchedule * 0.5);
      var3 += (float)(Math.cos(var4 / (520.0 / Math.max(0.2F, this.context.previous))) * this.context.pending * this.timerRender * 0.3F);
      var2 += (float)(Math.cos(var4 / 40.0) * this.context.animator * 0.6F);
      var3 += (float)(Math.sin(var4 / 70.0) * this.context.source * 0.6F);
      if (!this.requestAdapt) {
         this.dataValidate = var2;
         this.scaleRender = var3;
         this.requestAdapt = true;
      } else {
         float var15 = (this.context.selection + this.context.enabled) * 0.5F;
         float var7 = (this.context.renderer + this.context.handler) * 0.5F;
         float var8 = Math.max(0.01F, var15 / 180.0F * this.sourceSchedule * var1 * 22.0F);
         float var9 = Math.max(0.01F, var7 / 120.0F * this.timerRender * var1 * 22.0F);
         this.dataValidate = this.dataValidate + handle(var2 - this.dataValidate, -var8, var8);
         this.scaleRender = this.scaleRender + handle(var3 - this.scaleRender, -var9, var9);
      }
   }

   private void update(RoundedRectRenderer var1) {
      float var2 = this.target.instance + this.target.context + 16.0F;
      float var3 = this.source.data + 90.0F;
      float var4 = this.source.instance + this.source.context - var2 - 18.0F;
      float var5 = this.source.data + this.source.config - var3 - 18.0F;
      this.pending = new RotationBuilderScreen.AnimationState(var2, var3, var4, var5);
      var1.handle(var2, var3, var4, var5, 11.0F, handle(255, 255, 255, 10));
      var1.handle(var2, var3, var4, var5, 11.0F, handle(255, 255, 255, 20), 2.0F);
      this.previous = new RotationBuilderScreen.AnimationState(var2 + 1.0F, var3 + 1.0F, var4 - 2.0F, var5 - 2.0F);
      var1.handle(this.previous.instance, this.previous.data, this.previous.context, this.previous.config, 11.0F, 11.0F, 11.0F, 11.0F);
      float var6 = var2 + 14.0F;
      float var7 = var4 - 28.0F;
      this.handle(var1, var6, var3 + 12.0F, var7);
      float var8 = handle(this.handler.update(), 0.0F, 1.0F);
      var1.update(var8);
      float var9 = var3 + 48.0F - this.textureRun;
      this.state = this.animator == 0 ? this.cache : this.output;
      float var10 = (var7 - 8.0F) * 0.5F;
      if (this.animator == 0) {
         this.presetWrite = new RotationBuilderScreen.AnimationState(var6, var9, var10, 24.0F);
         this.process(var1, "pmode", this.presetWrite, "Точка: " + this.refresh(), false);
         this.colorMeasure = new RotationBuilderScreen.AnimationState(var6 + var10 + 8.0F, var9, var10, 24.0F);
         this.process(var1, "mmode", this.colorMeasure, "Точки: " + this.context.vectorMatch, false);
         var9 += 32.0F;
         this.animationSchedule = new RotationBuilderScreen.AnimationState(var6, var9, var7, 24.0F);
         this.process(var1, "mhead", this.animationSchedule, this.context.matrixBlend ? "Голова: ВКЛ" : "Голова: ВЫКЛ", this.context.matrixBlend);
         var9 += 36.0F;
      } else {
         this.rendererScan = new RotationBuilderScreen.AnimationState(var6, var9, var10, 24.0F);
         this.process(var1, "pfollow", this.rendererScan, "Pitch: " + this.context.responseCompute, false);
         this.sourceBuild = new RotationBuilderScreen.AnimationState(var6 + var10 + 8.0F, var9, var10, 24.0F);
         this.process(var1, "laway", this.sourceBuild, this.context.providerClose ? "Отвод: ВКЛ" : "Отвод: ВЫКЛ", this.context.providerClose);
         var9 += 36.0F;
      }

      for (RotationBuilderScreen.PrimaryAnimationState var12 : this.state) {
         var12.handle(var6, var9, var7);
         this.handle(var1, var12);
         var9 += 34.0F;
      }

      var1.onTick();
      float var18 = var9 + this.textureRun;
      float var19 = var3 + var5 - 12.0F;
      this.indexBind = Math.max(0.0F, var18 - var19);
      this.textureRun = handle(this.textureRun, 0.0F, this.indexBind);
      var1.apply();
      if (this.indexBind > 0.0F) {
         float var13 = var5 - 16.0F;
         float var14 = Math.max(30.0F, var13 * (var5 / (var5 + this.indexBind)));
         float var15 = var3 + 8.0F + (var13 - var14) * (this.textureRun / this.indexBind);
         var1.handle(var2 + var4 - 6.0F, var3 + 8.0F, 3.0F, var13, 1.5F, handle(255, 255, 255, 18));
         var1.handle(var2 + var4 - 6.0F, var15, 3.0F, var14, 1.5F, handle(95, 210, 255, 130));
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4) {
      float var5 = (var4 - 8.0F) * 0.5F;
      this.presetSave = new RotationBuilderScreen.AnimationState(var2, var3, var5, 26.0F);
      this.windowConvert = new RotationBuilderScreen.AnimationState(var2 + var5 + 8.0F, var3, var5, 26.0F);
      this.handle(var1, this.presetSave, "Настройки для ротации ", this.animator == 0);
      this.handle(var1, this.windowConvert, "Вектор головы", this.animator == 1);
   }

   private void handle(RoundedRectRenderer var1, RotationBuilderScreen.AnimationState var2, String var3, boolean var4) {
      String var5 = "tab." + var3;
      float var6 = this.handle(var5, var2.handle(this.actionRead, this.configCollapse));
      float var7 = this.process(var5, var4);
      float var8 = this.execute(var5);
      float var9 = 1.0F - var8 * 0.05F;
      float var10 = var2.instance + var2.context * 0.5F;
      float var11 = var2.data + var2.config * 0.5F;
      int var12 = handle(handle(255, 255, 255, Math.round(10.0F + var6 * 16.0F)), handle(95, 190, 255, 60), var7);
      var1.compute(var9, var9, var10, var11);
      var1.handle(var2.instance, var2.data, var2.context, var2.config, 8.0F, var12);
      float var13 = TextMeasureCache.process(FontRegistry.instance, var3, 22.0F);
      int var14 = handle(handle(190, 200, 214, 224), handle(235, 248, 255, 246), var7);
      var1.handle(FontRegistry.instance, var2.instance + (var2.context - var13) * 0.5F, var2.data + 18.0F, 22.0F, var3, var14);
      var1.check();
   }

   private void process(RoundedRectRenderer var1, String var2, RotationBuilderScreen.AnimationState var3, String var4, boolean var5) {
      float var6 = this.handle(var2, var3.handle(this.actionRead, this.configCollapse));
      float var7 = this.process(var2, var5);
      float var8 = this.execute(var2);
      float var9 = 1.0F - var8 * 0.05F;
      float var10 = var3.instance + var3.context * 0.5F;
      float var11 = var3.data + var3.config * 0.5F;
      int var12 = handle(handle(255, 255, 255, Math.round(14.0F + var6 * 20.0F)), handle(95, 190, 255, 60), var7);
      int var13 = handle(handle(255, 255, 255, 24), handle(95, 210, 255, 124), var7);
      var1.compute(var9, var9, var10, var11);
      var1.handle(var3.instance, var3.data, var3.context, var3.config, 7.0F, var12);
      var1.handle(var3.instance, var3.data, var3.context, var3.config, 7.0F, var13, 1.0F);
      var1.handle(FontRegistry.instance, var3.instance + 9.0F, var3.data + 16.0F, 22.0F, var4, handle(212, 222, 236, 232));
      var1.check();
   }

   private void handle(RoundedRectRenderer var1, RotationBuilderScreen.PrimaryAnimationState var2) {
      float var3 = var2.cache.get();
      var1.handle(FontRegistry.instance, var2.current, var2.active + 10.0F, 22.0F, var2.instance, handle(190, 200, 214, 224));
      String var4 = var2.state ? String.valueOf(Math.round(var3)) : String.format("%.2f", var3);
      float var5 = TextMeasureCache.process(FontRegistry.instance, var4, 22.0F);
      var1.handle(FontRegistry.instance, var2.current + var2.mode - var5, var2.active + 10.0F, 22.0F, var4, handle(240, 246, 255, 226));
      float var6 = var2.active + 20.0F;
      float var7 = this.handle(var2);
      float var8 = this.execute("slider." + var2.instance);
      float var9 = 1.0F + var8 * 0.18F;
      var1.handle(var2.current, var6, var2.mode, 5.0F, 2.5F, handle(255, 255, 255, 28));
      var1.handle(var2.current, var6, var2.mode * var7, 5.0F, 2.5F, handle(95, 210, 255, 165));
      float var10 = var2.current + var2.mode * var7 - 4.0F;
      float var11 = var6 - 2.5F;
      float var12 = 9.0F * var9;
      float var13 = 10.0F * var9;
      var1.handle(var10 - (var12 - 9.0F) * 0.5F, var11 - (var13 - 10.0F) * 0.5F, var12, var13, 4.5F, handle(235, 250, 255, 246));
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.compute(this.handle(var1), this.process((double)var3));
      if (this.entryAnimate) {
         return true;
      }

      if (var5 == 0 && this.responseCompute.handle(this.actionRead, this.configCollapse)) {
         this.apply("presets.open");
         this.windowProcess = !this.windowProcess;
         this.packetSave = false;
         return true;
      }

      if (this.pointEncode.update() > 0.04F) {
         if (this.providerFetch.handle(this.actionRead, this.configCollapse)) {
            if (var5 == 0) {
               this.resolve();
            }

            return true;
         } else if (var5 == 0) {
            this.windowProcess = false;
            this.packetSave = false;
            return true;
         } else {
            return true;
         }
      } else if (var5 == 0) {
         if (this.latest.handle(this.actionRead, this.configCollapse)) {
            this.apply("screen.close");
            this.close();
            return true;
         }

         for (RotationBuilderScreen.PrimaryDataRecord var13 : this.current) {
            if (var13.bounds.handle(this.actionRead, this.configCollapse)) {
               this.apply("chip." + var13.label);
               this.context.handle(var13.label);
               return true;
            }
         }

         if (this.summary.handle(this.actionRead, this.configCollapse)) {
            this.apply("clear");
            this.context.compute();
            this.scaleAdapt = -1;
            return true;
         }

         if (this.matrixBlend.handle(this.actionRead, this.configCollapse)) {
            this.apply("reset");
            this.context.resolve();
            this.scaleAdapt = -1;
            this.requestAdapt = false;
            return true;
         }

         if (this.vectorMatch.handle(this.actionRead, this.configCollapse)) {
            this.apply("copy");
            this.prepare();
            return true;
         }

         if (this.itemProject.handle(this.actionRead, this.configCollapse)) {
            this.apply("paste");
            this.check();
            return true;
         }

         if (!this.previous.handle(this.actionRead, this.configCollapse)) {
            int var11 = this.handle(this.actionRead, this.configCollapse);
            if (var11 >= 0) {
               this.apply("point." + var11);
               this.scaleAdapt = var11;
               this.colorCompute = var11;
               return true;
            } else if (this.process(this.actionRead, this.configCollapse)) {
               float var14 = handle((this.actionRead - this.outputCollapse) / this.sourceSchedule, -0.5F, 0.5F);
               float var8 = handle((this.profileInvoke - this.configCollapse) / this.timerRender, 0.0F, 1.0F);
               this.context.handle(var14, var8);
               this.scaleAdapt = this.context.presetWrite.size() - 1;
               this.apply("point." + this.scaleAdapt);
               return true;
            } else {
               return true;
            }
         } else {
            if (this.presetSave.handle(this.actionRead, this.configCollapse)) {
               this.apply("tab.Настройки для ротации ");
               this.handle(0);
               return true;
            }

            if (this.windowConvert.handle(this.actionRead, this.configCollapse)) {
               this.apply("tab.Вектор головы");
               this.handle(1);
               return true;
            }

            if (this.animator == 0) {
               if (this.presetWrite.handle(this.actionRead, this.configCollapse)) {
                  this.apply("pmode");
                  this.select();
                  return true;
               }

               if (this.colorMeasure.handle(this.actionRead, this.configCollapse)) {
                  this.apply("mmode");
                  this.context.vectorMatch = handle(RotationPreset.data, this.context.vectorMatch, 1);
                  RotationPreset.apply();
                  return true;
               }

               if (this.animationSchedule.handle(this.actionRead, this.configCollapse)) {
                  this.apply("mhead");
                  this.context.matrixBlend = !this.context.matrixBlend;
                  RotationPreset.apply();
                  return true;
               }
            } else {
               if (this.rendererScan.handle(this.actionRead, this.configCollapse)) {
                  this.apply("pfollow");
                  this.context.responseCompute = handle(RotationPreset.context, this.context.responseCompute, 1);
                  RotationPreset.apply();
                  return true;
               }

               if (this.sourceBuild.handle(this.actionRead, this.configCollapse)) {
                  this.apply("laway");
                  this.context.providerClose = !this.context.providerClose;
                  RotationPreset.apply();
                  return true;
               }
            }

            for (RotationBuilderScreen.PrimaryAnimationState var15 : this.state) {
               if (var15.handle(this.actionRead, this.configCollapse)) {
                  this.apply("slider." + var15.instance);
                  this.scaleSave = var15;
                  var15.handle(this.actionRead);
                  RotationPreset.apply();
                  return true;
               }
            }

            return true;
         }
      } else {
         if (var5 == 1) {
            if (this.previous.handle(this.actionRead, this.configCollapse)) {
               if (this.animator == 0 && this.presetWrite.handle(this.actionRead, this.configCollapse)) {
                  this.apply("pmode");
                  this.context.mode = handle(RotationPreset.instance, this.context.mode, -1);
                  RotationPreset.apply();
                  return true;
               }

               if (this.animator == 0 && this.colorMeasure.handle(this.actionRead, this.configCollapse)) {
                  this.apply("mmode");
                  this.context.vectorMatch = handle(RotationPreset.data, this.context.vectorMatch, -1);
                  RotationPreset.apply();
                  return true;
               }

               if (this.animator == 1 && this.rendererScan.handle(this.actionRead, this.configCollapse)) {
                  this.apply("pfollow");
                  this.context.responseCompute = handle(RotationPreset.context, this.context.responseCompute, -1);
                  RotationPreset.apply();
                  return true;
               }

               for (RotationBuilderScreen.PrimaryAnimationState var7 : this.state) {
                  if (var7.handle(this.actionRead, this.configCollapse)) {
                     this.apply("slider." + var7.instance);
                     var7.output.set(var7.config);
                     this.context.process();
                     RotationPreset.apply();
                     return true;
                  }
               }

               return true;
            }

            int var6 = this.handle(this.actionRead, this.configCollapse);
            if (var6 >= 0) {
               this.apply("point." + var6);
               this.context.handle(this.context.presetWrite.get(var6));
               this.scaleAdapt = -1;
               return true;
            }
         }

         return true;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      this.compute(this.handle(var1), this.process((double)var3));
      if (this.scaleSave != null) {
         this.context.process();
         RotationPreset.apply();
         this.scaleSave = null;
      }

      if (this.colorCompute >= 0) {
         RotationPreset.apply();
         this.colorCompute = -1;
      }

      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      this.compute(this.handle(var1), this.process((double)var3));
      if (this.pointEncode.update() > 0.04F) {
         return true;
      } else if (this.scaleSave != null) {
         this.scaleSave.handle(this.actionRead);
         return true;
      } else if (this.colorCompute >= 0 && this.colorCompute < this.context.presetWrite.size()) {
         RotationPreset.State var10 = this.context.presetWrite.get(this.colorCompute);
         var10.instance = handle((this.actionRead - this.outputCollapse) / this.sourceSchedule, -0.5F, 0.5F);
         var10.data = handle((this.profileInvoke - this.configCollapse) / this.timerRender, 0.0F, 1.0F);
         return true;
      } else {
         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      this.compute(this.handle(var1), this.process((double)var3));
      if (this.pointEncode.update() > 0.04F) {
         if (this.positionAdvance.handle(this.actionRead, this.configCollapse) && this.worldSend > 0.0F) {
            this.layerSample = handle(this.layerSample - (float)var7 * 30.0F, 0.0F, this.worldSend);
         }

         return true;
      } else if (this.pending.handle(this.actionRead, this.configCollapse) && this.indexBind > 0.0F) {
         this.textureRun = handle(this.textureRun - (float)var7 * 28.0F, 0.0F, this.indexBind);
         return true;
      } else {
         return true;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!(this.pointEncode.update() > 0.04F)) {
         if (var1 == 256) {
            this.close();
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      } else {
         if (this.packetSave) {
            if (var1 == 259 && !this.stateApply.isEmpty()) {
               this.stateApply = this.stateApply.substring(0, this.stateApply.length() - 1);
               return true;
            }

            if (var1 == 86 && (var3 & 2) != 0 && this.client != null && this.client.keyboard != null) {
               this.resolve(this.client.keyboard.getClipboard());
               return true;
            }

            if (var1 == 257 || var1 == 335) {
               this.update();
               return true;
            }
         }

         if (var1 == 256) {
            if (this.packetSave) {
               this.packetSave = false;
            } else {
               this.windowProcess = false;
            }

            return true;
         } else {
            return true;
         }
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (this.pointEncode.update() > 0.04F && this.packetSave) {
         if (!Character.isISOControl(var1)) {
            this.resolve(String.valueOf(var1));
         }

         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   public void close() {
      if (!this.entryAnimate) {
         this.windowProcess = false;
         this.packetSave = false;
         this.entryAnimate = true;
      }
   }

   private void compute() {
      if (!this.playerCollect) {
         this.playerCollect = true;
         this.context.process();
         RotationPreset.apply();
         super.close();
      }
   }

   private int handle(float var1, float var2) {
      for (int var3 = this.context.presetWrite.size() - 1; var3 >= 0; var3--) {
         RotationPreset.State var4 = this.context.presetWrite.get(var3);
         float var5 = this.outputCollapse + var4.instance * this.sourceSchedule;
         float var6 = this.profileInvoke - var4.data * this.timerRender;
         if (Math.hypot(var1 - var5, var2 - var6) <= 8.0) {
            return var3;
         }
      }

      return -1;
   }

   private boolean process(float var1, float var2) {
      float var3 = (var1 - this.outputCollapse) / this.sourceSchedule;
      float var4 = (this.profileInvoke - var2) / this.timerRender;
      return EasingGraphRenderer.compute(var3, var4);
   }

   private void resolve() {
      if (this.profileDraw.handle(this.actionRead, this.configCollapse)) {
         this.apply("presets.close");
         this.windowProcess = false;
         this.packetSave = false;
      } else if (this.vectorPerform.handle(this.actionRead, this.configCollapse)) {
         this.apply("presets.name");
         this.packetSave = true;
      } else {
         this.packetSave = false;
         if (this.eventAttach.handle(this.actionRead, this.configCollapse)) {
            this.apply("presets.create");
            this.apply();
         } else if (this.serverRead.handle(this.actionRead, this.configCollapse)) {
            this.apply("presets.update");
            this.execute();
         } else {
            if (this.positionAdvance.handle(this.actionRead, this.configCollapse)) {
               for (RotationBuilderScreen.DataRecord var2 : this.frameCheck) {
                  String var3 = var2.preset.id();
                  if (var2.apply.handle(this.actionRead, this.configCollapse)) {
                     this.apply("preset.apply." + var3);
                     this.handle(var3);
                     return;
                  }

                  if (var2.copy.handle(this.actionRead, this.configCollapse)) {
                     this.apply("preset.copy." + var3);
                     this.process(var3);
                     return;
                  }

                  if (var2.delete.handle(this.actionRead, this.configCollapse)) {
                     this.apply("preset.delete." + var3);
                     this.compute(var3);
                     return;
                  }

                  if (var2.card.handle(this.actionRead, this.configCollapse)) {
                     this.apply("preset.row." + var3);
                     this.matrixFilter = var3;
                     this.stateApply = var2.preset.name();
                     return;
                  }
               }
            }
         }
      }
   }

   private void update() {
      if (this.matrixFilter == null) {
         this.apply();
      } else {
         this.execute();
      }
   }

   private void apply() {
      if (this.stateApply.trim().isEmpty()) {
         this.update("Введите название пресета");
         this.packetSave = true;
      } else {
         RotationPresetRepository.NamedEntry var1 = this.config.handle(this.stateApply, this.context);
         if (var1 == null) {
            this.update("Не удалось сохранить пресет");
         } else {
            this.matrixFilter = var1.id();
            this.stateApply = var1.name();
            this.layerSample = 0.0F;
            this.update("Пресет сохранён");
         }
      }
   }

   private void execute() {
      if (this.matrixFilter == null) {
         this.update("Сначала выберите пресет");
      } else if (this.stateApply.trim().isEmpty()) {
         this.update("Введите название пресета");
         this.packetSave = true;
      } else {
         RotationPresetRepository.NamedEntry var1 = this.config.handle(this.matrixFilter, this.stateApply, this.context);
         if (var1 == null) {
            this.update("Не удалось обновить пресет");
         } else {
            this.stateApply = var1.name();
            this.update("Пресет обновлён");
         }
      }
   }

   private void handle(String var1) {
      if (!this.config.handle(var1)) {
         this.update("Не удалось применить пресет");
      } else {
         this.onTick();
         RotationPresetRepository.NamedEntry var2 = this.config.compute(var1);
         this.matrixFilter = var1;
         this.stateApply = var2 == null ? this.stateApply : var2.name();
         this.scaleAdapt = -1;
         this.colorCompute = -1;
         this.requestAdapt = false;
         this.handle();
         this.update("Пресет применён");
      }
   }

   private void process(String var1) {
      RotationPresetRepository.NamedEntry var2 = this.config.compute(var1);
      if (var2 != null && this.client != null && this.client.keyboard != null) {
         this.client.keyboard.setClipboard(var2.key());
         this.update("Код пресета скопирован");
      } else {
         this.update("Не удалось скопировать код");
      }
   }

   private void compute(String var1) {
      if (!this.config.process(var1)) {
         this.update("Не удалось удалить пресет");
      } else {
         if (var1.equals(this.matrixFilter)) {
            this.matrixFilter = null;
            this.stateApply = "";
         }

         this.layerSample = handle(this.layerSample, 0.0F, this.worldSend);
         this.update("Пресет удалён");
      }
   }

   private void resolve(String var1) {
      if (var1 != null && !var1.isEmpty() && this.stateApply.length() < 40) {
         StringBuilder var2 = new StringBuilder(this.stateApply);

         for (int var3 = 0; var3 < var1.length() && var2.length() < 40; var3++) {
            char var4 = var1.charAt(var3);
            if (!Character.isISOControl(var4)) {
               var2.append(var4);
            }
         }

         this.stateApply = var2.toString();
      }
   }

   private void prepare() {
      try {
         String var1 = this.context.update();
         if (this.client != null && this.client.keyboard != null) {
            this.client.keyboard.setClipboard(var1);
            this.update("Ключ скопирован в буфер обмена");
         } else {
            this.update("Не удалось получить буфер обмена");
         }
      } catch (Throwable var2) {
         this.update("Ошибка при создании ключа");
      }
   }

   private void check() {
      try {
         if (this.client == null || this.client.keyboard == null) {
            this.update("Не удалось получить буфер обмена");
            return;
         }

         String var1 = this.client.keyboard.getClipboard();
         if (var1 == null || var1.trim().isEmpty()) {
            this.update("Буфер обмена пуст");
            return;
         }

         if (RotationPreset.process(var1)) {
            this.onTick();
            this.handle();
            this.scaleAdapt = -1;
            this.update("Ключ применён");
         } else {
            this.update("Неверный ключ");
         }
      } catch (Throwable var2) {
         this.update("Ошибка при вставке ключа");
      }
   }

   private void onTick() {
      int var1 = AttackAura.pending.config.indexOf("Custom");
      if (var1 >= 0) {
         AttackAura.pending.current = var1;
         AttackAura.pending.state = AttackAura.pending.config.get(var1);
         if (WildClient.instance != null && WildClient.instance.renderer != null) {
            WildClient.instance.renderer.compute();
         }
      }
   }

   private void update(String var1) {
      this.moduleCollect = var1;
      this.providerClose = System.currentTimeMillis() + 2600L;
      this.animationDraw.apply(0.0);
      this.animationDraw.handle(1.0, 0.22F, Easings.handler, false);
   }

   private void handle(int var1) {
      if (this.animator != var1) {
         this.animator = var1;
         this.textureRun = 0.0F;
         this.requestAdapt = false;
         this.handler.apply(0.0);
         this.handler.handle(1.0, 0.26F, Easings.handler, false);
      }
   }

   private void select() {
      this.context.mode = handle(RotationPreset.instance, this.context.mode, 1);
      RotationPreset.apply();
   }

   private static String handle(String[] var0, String var1, int var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < var0.length; var4++) {
         if (var0[var4].equals(var1)) {
            var3 = var4;
            break;
         }
      }

      var3 = (var3 + var2 % var0.length + var0.length) % var0.length;
      return var0[var3];
   }

   private String refresh() {
      return this.context.presetWrite.isEmpty() ? this.context.mode : "Custom";
   }

   private void handle(RoundedRectRenderer var1, String var2, RotationBuilderScreen.AnimationState var3, float var4, int var5) {
      float var6 = TextMeasureCache.process(FontRegistry.instance, var2, var4);
      var1.handle(FontRegistry.instance, var3.instance + (var3.context - var6) * 0.5F, var3.data + var3.config * 0.5F + var4 * 0.2F, var4, var2, var5);
   }

   private float handle(String var1, boolean var2) {
      DoubleAnimator var3 = this.active.computeIfAbsent(var1, var1x -> {
         DoubleAnimator var2x = new DoubleAnimator();
         var2x.apply(var2 ? 1.0 : 0.0);
         return var2x;
      });
      var3.handle();
      var3.handle(var2 ? 1.0 : 0.0, 0.14F, Easings.handler, false);
      return handle(var3.update(), 0.0F, 1.0F);
   }

   private void render2() {
      this.handler.handle();
      this.animationDraw.handle();
      this.pointEncode.handle();
      this.pointEncode
         .handle(this.windowProcess ? 1.0 : 0.0, this.windowProcess ? 0.24F : 0.18F, this.windowProcess ? Easings.serverRead : Easings.renderer, false);
      long var1 = this.providerClose - System.currentTimeMillis();
      if (var1 > 0L && var1 < 400L) {
         this.animationDraw.handle(0.0, 0.28F, Easings.renderer, false);
      }
   }

   private void apply(String var1) {
      DoubleAnimator var2 = this.mode.computeIfAbsent(var1, var0 -> {
         DoubleAnimator var1x = new DoubleAnimator();
         var1x.apply(0.0);
         return var1x;
      });
      var2.apply(1.0);
      var2.handle(0.0, 0.16F, Easings.current, false);
   }

   private float execute(String var1) {
      DoubleAnimator var2 = this.mode.get(var1);
      if (var2 == null) {
         return 0.0F;
      }

      var2.handle();
      return handle(var2.update(), 0.0F, 1.0F);
   }

   private float process(String var1, boolean var2) {
      DoubleAnimator var3 = this.selection.computeIfAbsent(var1, var1x -> {
         DoubleAnimator var2x = new DoubleAnimator();
         var2x.apply(var2 ? 1.0 : 0.0);
         return var2x;
      });
      var3.handle();
      var3.handle(var2 ? 1.0 : 0.0, 0.2F, Easings.handler, false);
      return handle(var3.update(), 0.0F, 1.0F);
   }

   private float handle(RotationBuilderScreen.PrimaryAnimationState var1) {
      float var2 = handle((var1.cache.get() - var1.data) / (var1.context - var1.data), 0.0F, 1.0F);
      DoubleAnimator var3 = this.enabled.computeIfAbsent(var1.instance, var1x -> {
         DoubleAnimator var2x = new DoubleAnimator();
         var2x.apply(var2);
         return var2x;
      });
      var3.handle();
      float var4 = this.scaleSave == var1 ? 0.08F : 0.16F;
      var3.handle(var2, var4, Easings.handler, false);
      return handle(var3.update(), 0.0F, 1.0F);
   }

   private static int handle(int var0, int var1, float var2) {
      var2 = Math.max(0.0F, Math.min(1.0F, var2));
      int var3 = var0 >> 24 & 0xFF;
      int var4 = var0 >> 16 & 0xFF;
      int var5 = var0 >> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      int var7 = var1 >> 24 & 0xFF;
      int var8 = var1 >> 16 & 0xFF;
      int var9 = var1 >> 8 & 0xFF;
      int var10 = var1 & 0xFF;
      int var11 = Math.round(var3 + (var7 - var3) * var2);
      int var12 = Math.round(var4 + (var8 - var4) * var2);
      int var13 = Math.round(var5 + (var9 - var5) * var2);
      int var14 = Math.round(var6 + (var10 - var6) * var2);
      return handle(var12, var13, var14, var11);
   }

   private static void initializeTemplates() {
      if (!instance) {
         instance = true;
         EventHandlerInvoker.handle(new Object() {
            @EventHandler
            public void handle(HudRenderContext var1) {
               if (var1.compute() != null && var1.compute().currentScreen instanceof RotationBuilderScreen var2) {
                  var2.handle(var1.resolve(), var1.apply(), var1.execute());
                  if (var1.resolve() != null) {
                     var1.resolve().compute();
                  }
               }
            }
         });
      }
   }

   private void compute(float var1, float var2) {
      this.actionRead = var1;
      this.configCollapse = var2;
   }

   private void drawAnimation() {
      if (this.client != null && this.client.getWindow() != null && this.client.mouse != null) {
         double var1 = this.client.getWindow().getFramebufferWidth();
         double var3 = this.client.getWindow().getFramebufferHeight();
         if (!(var1 <= 0.0) && !(var3 <= 0.0)) {
            double var5 = this.client.mouse.getX();
            double var7 = this.client.mouse.getY();
            if (var5 >= 0.0 && var7 >= 0.0 && var5 <= var1 + 2.0 && var7 <= var3 + 2.0) {
               this.compute((float)var5, (float)var7);
            }
         }
      }
   }

   private float handle(double var1) {
      if (this.client != null && this.client.getWindow() != null) {
         int var3 = this.client.getWindow().getFramebufferWidth();
         int var4 = this.client.getWindow().getScaledWidth();
         return var3 > 0 && var4 > 0 ? (float)(var1 * var3 / Math.max(1.0, var4)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   private float process(double var1) {
      if (this.client != null && this.client.getWindow() != null) {
         int var3 = this.client.getWindow().getFramebufferHeight();
         int var4 = this.client.getWindow().getScaledHeight();
         return var3 > 0 && var4 > 0 ? (float)(var1 * var3 / Math.max(1.0, var4)) : (float)var1;
      } else {
         return (float)var1;
      }
   }

   static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static int handle(int var0, int var1, int var2, int var3) {
      return RoundedRectRenderer.ColorState.resolve(var0, var1, var2, Math.max(0, Math.min(255, var3)));
   }

   static final class AnimationState {
      final float instance;
      final float data;
      final float context;
      final float config;

      AnimationState(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }

      static RotationBuilderScreen.AnimationState handle() {
         return new RotationBuilderScreen.AnimationState(0.0F, 0.0F, 0.0F, 0.0F);
      }

      boolean handle(float var1, float var2) {
         return var1 >= this.instance && var2 >= this.data && var1 <= this.instance + this.context && var2 <= this.data + this.config;
      }
   }

   interface Callback {
      float get();
   }

   record DataRecord(
      RotationPresetRepository.NamedEntry preset,
      RotationBuilderScreen.AnimationState card,
      RotationBuilderScreen.AnimationState apply,
      RotationBuilderScreen.AnimationState copy,
      RotationBuilderScreen.AnimationState delete
   ) {
   }

   final class PrimaryAnimationState {
      final String instance;
      final float data;
      final float context;
      final float config;
      final boolean state;
      final RotationBuilderScreen.Callback cache;
      final RotationBuilderScreen.PrimaryCallback output;
      float current;
      float active;
      float mode;

      PrimaryAnimationState(
         String var2, float var3, float var4, float var5, boolean var6, RotationBuilderScreen.Callback var7, RotationBuilderScreen.PrimaryCallback var8
      ) {
         this.instance = var2;
         this.data = var3;
         this.context = var4;
         this.config = var5;
         this.state = var6;
         this.cache = var7;
         this.output = var8;
      }

      void handle(float var1, float var2, float var3) {
         this.current = var1;
         this.active = var2;
         this.mode = var3;
      }

      boolean handle(float var1, float var2) {
         return var1 >= this.current && var1 <= this.current + this.mode && var2 >= this.active && var2 <= this.active + 30.0F;
      }

      void handle(float var1) {
         float var2 = RotationBuilderScreen.handle((var1 - this.current) / this.mode, 0.0F, 1.0F);
         float var3 = this.data + var2 * (this.context - this.data);
         if (this.state) {
            var3 = Math.round(var3);
         } else {
            var3 = Math.round(var3 * 100.0F) / 100.0F;
         }

         this.output.set(RotationBuilderScreen.handle(var3, this.data, this.context));
      }
   }

   interface PrimaryCallback {
      void set(float var1);
   }

   record PrimaryDataRecord(String label, RotationBuilderScreen.AnimationState bounds, boolean active) {
   }
}
