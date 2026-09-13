package ru.wild.gui.screen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.config.FavoritesStorage;
import ru.wild.config.FoundryStorage;
import ru.wild.core.AnimationClock;
import ru.wild.core.FoundryEntityPreviewRenderer;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.FoundryTemplateRegistry;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.MicaStylePresets;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.SavedThemePreset;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeKeys;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.ColorPickerWidget;
import ru.wild.gui.widget.FoundryNodeSearchOverlay;
import ru.wild.gui.widget.FuzzyToastMatcher;
import ru.wild.gui.widget.SurfaceInteractionRouter;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.FoundryNodeSurfaceShader;
import ru.wild.render.shader.FoundryPinShader;
import ru.wild.render.shader.FoundryPresetFactory;
import ru.wild.render.shader.FoundryWireRenderer;
import ru.wild.render.shader.HoloZoomShader;
import ru.wild.render.shader.ShaderBuildResult;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderGraphBlock;
import ru.wild.render.shader.ShaderGraphCompiler;
import ru.wild.render.shader.ShaderGraphEditor;
import ru.wild.render.shader.ShaderGraphLink;
import ru.wild.render.shader.ShaderGraphNode;
import ru.wild.render.shader.ShaderNodeDefinition;
import ru.wild.render.shader.ShaderNodeRegistry;
import ru.wild.render.shader.ShaderPinDefinition;
import ru.wild.render.shader.ShaderPinDirection;
import ru.wild.render.shader.ShaderPresetNameGenerator;
import ru.wild.render.shader.ShaderPreviewSession;
import ru.wild.render.shader.ShaderValueType;
import ru.wild.render.shader.ThemeShaderProgramCache;
import ru.wild.util.io.UndoHistory;
import ru.wild.util.math.RectBounds;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.render.RoundedRectRenderer;

public final class FoundryWorkspace implements AutoCloseable {
   private static final SimpleDateFormat instance = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);
   private static final String[] data = new String[]{"Save As", "Export .wifd", "Import", "Open Folder", "Cleanup Legacy", "Reset"};
   private static final String[] context = new String[]{"All", "Мои", "Пресеты"};
   private static final int config = 0;
   private static final int state = 1;
   private final ShaderNodeRegistry cache = new ShaderNodeRegistry();
   private final ShaderGraphCompiler output = new ShaderGraphCompiler(this.cache);
   private final ShaderPreviewSession current = new ShaderPreviewSession(this.output);
   private final FoundryNodeSearchOverlay active = new FoundryNodeSearchOverlay(this.cache);
   private final ColorPickerWidget mode = new ColorPickerWidget(this.cache, this.output);
   private final ShaderImportBrowser selection = new ShaderImportBrowser();
   private ShaderGraph enabled = FoundryPresetFactory.handle(this.cache);
   private float renderer = 520.0F;
   private float handler = 260.0F;
   private float animationDraw = 0.92F;
   private float pointEncode = 0.92F;
   private final SpringAnimation animator = new SpringAnimation(0.92F);
   private boolean source;
   private float target;
   private float pending;
   private float previous;
   private float latest;
   private float summary;
   private float matrixBlend;
   private long vectorMatch;
   private String itemProject;
   private float responseCompute;
   private float providerFetch;
   private String profileDraw;
   private final Set<String> vectorPerform = new LinkedHashSet<>();
   private final Set<String> eventAttach = new LinkedHashSet<>();
   private final Map<String, FoundryWorkspace.DataRecord> serverRead = new HashMap<>();
   private float positionAdvance;
   private float frameCheck;
   private boolean moduleCollect;
   private float providerClose;
   private float presetSave;
   private float windowConvert;
   private float presetWrite;
   private String colorMeasure;
   private String animationSchedule;
   private float rendererScan;
   private float sourceBuild;
   private float outputCollapse;
   private float profileInvoke;
   private long sourceSchedule;
   private String timerRender = "ready";
   private long scaleSave;
   private LivePreviewRenderer colorCompute = LivePreviewRenderer.HUD;
   private boolean scaleAdapt;
   private boolean textureRun;
   private boolean indexBind;
   private LivePreviewRenderer actionRead;
   private boolean configCollapse;
   private String dataValidate = "Host Rectangle";
   private FoundryWorkspace.Mode scaleRender = FoundryWorkspace.Mode.AUTO;
   private int clientRefresh;
   private final Map<Integer, ShaderBuildResult> keyFilter = new HashMap<>();
   private boolean requestAdapt;
   private final SpringFloat timerMeasure = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.7F, 0.86F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private final SpringFloat vectorEncode = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.4F, 0.78F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private final SpringFloat requestReceive = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.6F, 0.84F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private final SpringFloat windowProcess = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(3.0F, 0.88F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private final SpringFloat packetSave = new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.6F, 0.82F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   private boolean entryAnimate;
   private float playerCollect;
   private boolean stateApply;
   private float matrixFilter;
   private int layerSample;
   private String worldSend = "";
   private boolean targetWrite;
   private long resultEncode;
   private final Set<String> messageParse = new LinkedHashSet<>();
   private long providerRead;
   private String matrixBlend2;
   private int scalePerform = -1;
   private int contextExpand = -1;
   private long keyProcess;
   private final UndoHistory actionConvert = new UndoHistory();
   private String screenRead = ShaderPresetNameGenerator.handle();
   private boolean animationExpand;
   private long playerRun;
   private final Map<String, ShaderGraphNode> matrixRender = new HashMap<>();
   private final Map<String, ShaderGraphNode> moduleTick = new HashMap<>();
   private final Map<String, ShaderGraphNode> playerCollapse = new HashMap<>();
   private String optionAdvance;
   private String effectScan;
   private String optionParse;
   private String pointSubmit;
   private String listenerPerform;
   private final Map<String, SpringAnimation> configMatch = new HashMap<>();
   private final Map<String, SpringAnimation> actionRender = new HashMap<>();
   private final Map<String, SpringFloat> playerApply = new HashMap<>();
   private final Map<String, Boolean> bufferAdapt = new LinkedHashMap<>(16, 0.75F, true);
   private final Map<String, SpringFloat> playerUpdate = new HashMap<>();
   private final Map<String, SpringAnimation> packetRead = new HashMap<>();
   private final SpringAnimation rendererCancel = new SpringAnimation(0.0F);
   private float eventReceive;
   private float screenSubmit;
   private float cacheHandle;
   private float rangeRelease;
   private float indexSave;
   private float indexCheck;
   private boolean settingSchedule;
   private boolean inputAcquire;
   private float listenerRun;
   private float indexLoad;
   private boolean layoutSave;
   private float blockRun;
   private float playerEvaluate;
   private GuiMetrics outputFetch;
   private int scaleParse;
   private int sessionEncode;

   public FoundryWorkspace() {
      ShaderGraphEditor.handle().handle(this.cache);
      this.update(this.colorCompute);
      this.enabled.handle().handle(this.screenRead, FoundryStorage.execute());
      this.scheduleAnimation();
      this.screenRead = this.enabled.handle().process();
      this.contextExpand = this.enabled.update();
      this.current.handle(this.colorCompute);
      FoundryStorage.handle().handle(this.cache);
      MicaStylePresets.handle(this.cache, this.output);
   }

   public ShaderNodeRegistry handle() {
      return this.cache;
   }

   public ShaderGraph process() {
      return this.enabled;
   }

   public LivePreviewRenderer compute() {
      return this.colorCompute;
   }

   public boolean handle(ModernClickGuiState var1) {
      if (var1 != null) {
         if (var1.cancelIndex()) {
            return true;
         }

         if (var1.handle(UiAnimationKeys.blendMatrix()) > 0.035F) {
            return true;
         }
      }

      return false;
   }

   public boolean resolve() {
      return this.selection.process() || this.active.handle() || this.configCollapse;
   }

   public boolean process(ModernClickGuiState var1) {
      return var1 != null && (var1.cancelIndex() || var1.handle(UiAnimationKeys.blendMatrix()) > 0.0015F);
   }

   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      if (var1 != null && var2 != null && var3 != null && var4 > 0 && var5 > 0) {
         float var6 = var2.handle(UiAnimationKeys.blendMatrix());
         if (!(var6 <= 0.0015F)) {
            this.execute(var2.sampleLayer(), var2.sendWorld());
            this.measure();
            this.animationDraw = this.animator.handle(this.pointEncode, SpringAnimationSpec.execute());
            this.fetch();
            ThemeColors var7 = var3.apply();
            this.outputFetch = var3.update();
            this.scaleParse = var4;
            this.sessionEncode = var5;
            boolean var8 = var2.cancelIndex();
            float var9 = execute(var6);
            float var10 = this.handle(var6);
            float var11 = this.handle(var6, var8);
            float var12 = this.handle(var6, var8, var5);
            float var13 = var2.process(UiAnimationKeys.blendMatrix());
            float var14 = (float)(System.currentTimeMillis() % 12000L) / 12000.0F;
            this.timerMeasure.compute(this.scaleAdapt ? 1.0F : 0.0F);
            this.vectorEncode.compute(this.textureRun ? 1.0F : 0.0F);
            this.requestReceive.compute(this.indexBind ? 1.0F : 0.0F);
            this.windowProcess.compute(this.configCollapse ? 1.0F : 0.0F);
            this.packetSave.compute(this.stateApply ? 1.0F : 0.0F);
            var1.compute();
            this.handle(var1, var2, var3, var7, var4, var5, var9, var14, var6, var8);
            this.handle(var1, var3.update(), var7, var4, var5, var6, var8, var13, var14);
            var1.update(var10);
            var1.handle(0.0F, var12);
            var1.handle(var11, var4 * 0.5F, var5 * 0.5F);

            try {
               this.process(var1, var2, var7, var4, var5, var14);
               this.handle(var1, var7, var4, var5, var9);
               this.handle(var1, var2, var3);
               this.handle(var1, var2, var7, var4, var5, var9);
               this.handle(var1, var3, var7);
               this.execute(var1, var2, var3, var4, var5);
               this.handle(var1, var2, var3, var4);
               this.handle(var1, var2, var3, var4, var5, var9);
               this.prepare(var1, var2, var3, var4, var5);
               this.compute(var1, var2, var3, var4, var5);
               this.resolve(var1, var2, var3, var4, var5);
               this.update(var1, var2, var3, var4, var5);
               this.apply(var1, var2, var3, var4, var5);
               this.process(var1, var2, var3, var4, var5);
               this.active.handle(var1, var3, var2, var4, var5);
               this.handle(var1, var3, var4, var5);
               this.selection.handle(var1, var3.update(), var7, var2.sampleLayer(), var2.sendWorld(), var4, var5);
            } finally {
               var1.prepare();
               var1.prepare();
               var1.onTick();
            }

            this.process(var1, var3.update(), var7, var4, var5, var6, var8, var13, var14);
            this.measureColor();
            boolean var15 = var8 && var10 > 0.72F;
            if (var15 && System.currentTimeMillis() - this.providerRead > 130L) {
               this.scanRenderer();
               this.current.handle(this.colorCompute);
               this.current.handle(this.enabled);
               this.providerRead = System.currentTimeMillis();
            }

            boolean var16 = this.itemProject == null && this.colorMeasure == null && !this.moduleCollect && !this.source;
            if (var15 && var16 && this.enabled.update() != this.scalePerform && System.currentTimeMillis() - this.keyProcess > 1800L) {
               this.scalePerform = this.enabled.update();
               this.keyProcess = System.currentTimeMillis();
               this.scanRenderer();
               SavedThemePreset var17 = FoundryStorage.handle().handle(this.colorCompute, this.enabled, this.matrixBlend2);
               if (var17 != null) {
                  this.matrixBlend2 = var17.handle();
                  this.handle(var17.process(), this.enabled);
               }
            }
         }
      }
   }

   private void update() {
      this.scalePerform = this.enabled.update();
      this.keyProcess = System.currentTimeMillis();
   }

   private String apply() {
      return update(this.screenRead);
   }

   private String execute() {
      String var1 = this.current.process();
      if (var1 != null && !var1.isBlank()) {
         return "failed";
      } else {
         return this.enabled.update() != this.contextExpand ? "dirty" : "saved";
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ThemeRenderContext var3,
      ThemeColors var4,
      int var5,
      int var6,
      float var7,
      float var8,
      float var9,
      boolean var10
   ) {
      float var11 = this.process(var9, var10);
      if (MenuAnimationClock.handle()) {
         var1.handle(26.0F + 22.0F * var11);
         var1.handle(0.0F, 0.0F, var5, var6, 0.0F, this.handle(var4) ? 0.42F + 0.2F * var7 + 0.22F * var11 : 0.66F + 0.16F * var7 + 0.16F * var11);
      }

      var1.handle(0.0F, 0.0F, var5, var6, this.handle(var3, var7));
   }

   private float handle(float var1) {
      float var2 = execute(process(var1, 0.0F, 1.0F));
      return var2 * execute(process((var1 - 0.006F) / 0.64F, 0.0F, 1.0F));
   }

   private float handle(float var1, boolean var2) {
      float var3 = execute(process(var1, 0.0F, 1.0F));
      float var4 = (float)Math.sin(Math.PI * process(var2 ? var1 : 1.0F - var1, 0.0F, 1.0F));
      return var2 ? 0.952F + 0.048F * var3 + 0.01F * var4 * (1.0F - var3) : 0.97F + 0.03F * var3 - 0.01F * var4;
   }

   private float handle(float var1, boolean var2, int var3) {
      float var4 = execute(process(var1, 0.0F, 1.0F));
      float var5 = Math.max(18.0F, var3 * 0.032F);
      return var2 ? var5 * (1.0F - var4) : -var5 * (1.0F - var4);
   }

   private float process(float var1, boolean var2) {
      float var3 = execute(process(var1, 0.0F, 1.0F));
      return var2 ? 1.0F - var3 : (1.0F - var3) * 0.96F;
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, int var4, int var5, float var6, boolean var7, float var8, float var9) {
      float var10 = process(var7 ? var6 : 1.0F - var6, 0.0F, 1.0F);
      float var11 = (float)Math.sin(Math.PI * var10);
      float var12 = this.process(var6, var7);
      float var13 = process(var11 * 0.52F + Math.abs(var8) * 0.35F + var12 * 0.18F, 0.0F, 1.0F);
      if (!var7) {
         float var24 = execute(1.0F - process(var6, 0.0F, 1.0F));
         float var25 = process(var24 * 0.42F + Math.abs(var8) * 0.1F, 0.0F, 1.0F);
         int var26 = this.handle(var3) ? ThemeColors.handle(244, 247, 255, Math.round(54.0F * var25)) : ThemeColors.handle(0, 0, 0, Math.round(86.0F * var25));
         int var27 = ThemeColors.handle(var3.submit(), Math.round(14.0F * var13 * (1.0F - var24 * 0.35F)));
         var1.handle(0.0F, 0.0F, var4, var5, 0.0F, var26);
         var1.handle(0.0F, 0.0F, var4, var5, 0.0F, var27);
         float var29 = Math.max(var2.handle(7.0F), var5 * 0.01F);
         int var31 = this.handle(var3) ? ThemeColors.handle(18, 24, 40, Math.round(8.0F * var25)) : ThemeColors.handle(0, 0, 0, Math.round(18.0F * var25));
         var1.handle(0.0F, 0.0F, var4, var29, 0.0F, var31);
         var1.handle(0.0F, var5 - var29, var4, var29, 0.0F, var31);
      } else {
         int var14 = ThemeColors.handle(var3.save(), Math.round((this.handle(var3) ? 34 : 46) * var13));
         int var15 = ThemeColors.handle(var3.submit(), Math.round((this.handle(var3) ? 22 : 38) * var13));
         var1.handle(0.0F, 0.0F, var4, var5, var14);
         float var16 = execute(process(var7 ? var6 * 1.14F : var6, 0.0F, 1.0F));
         float var17 = (1.0F - var16) * var5 * 0.28F;
         if (var17 > 0.6F) {
            int var18 = this.handle(var3)
               ? ThemeColors.handle(244, 248, 255, Math.round(118.0F * (1.0F - var16)))
               : ThemeColors.handle(0, 0, 0, Math.round(150.0F * (1.0F - var16)));
            var1.handle(0.0F, 0.0F, var4, var17, 0.0F, var18);
            var1.handle(0.0F, var5 - var17, var4, var17, 0.0F, var18);
            var1.handle(0.0F, var17 - var2.handle(1.0F), var4, var2.handle(1.0F), 0.0F, ThemeColors.handle(var3.save(), Math.round(120.0F * (1.0F - var16))));
            var1.handle(0.0F, var5 - var17, var4, var2.handle(1.0F), 0.0F, ThemeColors.handle(var3.submit(), Math.round(120.0F * (1.0F - var16))));
         }

         float var28 = var7 ? var10 : 1.0F - var10;

         for (int var19 = 0; var19 < 5; var19++) {
            float var20 = process(var28 * 1.18F + var19 * 0.17F + var9 * 0.045F);
            float var21 = -var4 * 0.28F + var20 * var4 * 1.58F;
            float var22 = var2.handle(42 + var19 * 9) * (0.72F + var13);
            float var23 = var13 * (0.62F - var19 * 0.075F);
            var1.handle(var21, var5 * (0.42F + var19 * 0.035F));
            var1.process(-18.0F);
            var1.handle(
               -var22 * 0.5F,
               -var5,
               var22,
               var5 * 2.1F,
               var22 * 0.5F,
               ThemeColors.handle(var19 % 2 == 0 ? var3.save() : var3.submit(), Math.round(52.0F * var23))
            );
            var1.handle(-var22 * 0.08F, -var5, var22 * 0.16F, var5 * 2.1F, var22 * 0.08F, ThemeColors.handle(var3.load(), Math.round(18.0F * var23)));
            var1.execute();
            var1.prepare();
         }

         float var30 = process(0.18F + var12 * 0.62F + var13 * 0.22F, 0.0F, 1.0F);
         int var32 = this.handle(var3) ? ThemeColors.handle(18, 24, 40, Math.round(24.0F * var30)) : ThemeColors.handle(0, 0, 0, Math.round(78.0F * var30));
         float var33 = Math.max(var2.handle(42.0F), var4 * 0.035F);
         float var34 = Math.max(var2.handle(36.0F), var5 * 0.045F);
         var1.handle(0.0F, 0.0F, var4, var34, 0.0F, var32);
         var1.handle(0.0F, var5 - var34, var4, var34, 0.0F, var32);
         var1.handle(0.0F, 0.0F, var33, var5, 0.0F, var32);
         var1.handle(var4 - var33, 0.0F, var33, var5, 0.0F, var32);
         var1.handle(0.0F, 0.0F, var4, var5, var15);
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, int var4, int var5, float var6, boolean var7, float var8, float var9) {
      if (var7) {
         float var10 = process(var7 ? var6 : 1.0F - var6, 0.0F, 1.0F);
         float var11 = (float)Math.sin(Math.PI * var10);
         var11 = process(var11 * 0.34F + Math.abs(var8) * 0.22F, 0.0F, 1.0F);
         if (!(var11 <= 0.015F)) {
            float var12 = var5 * process(var10 * 0.85F + var9 * 0.18F);
            var1.handle(
               0.0F,
               var12 - var2.handle(1.2F),
               var4,
               var2.handle(2.4F),
               var2.handle(1.2F),
               ThemeColors.handle(var3.load(), Math.round((this.handle(var3) ? 34 : 48) * var11))
            );
            var1.handle(
               0.0F, var12 + var2.handle(3.5F), var4, var2.handle(1.0F), var2.handle(0.5F), ThemeColors.handle(var3.submit(), Math.round(78.0F * var11))
            );
            float var13 = var4 * (0.18F + 0.16F * var11);
            float var14 = var4 * process(var10 * 1.25F + 0.18F);
            var1.handle(var14, var5 * 0.5F);
            var1.process(12.0F);
            var1.handle(
               -var13 * 0.5F,
               -var5 * 0.62F,
               var13,
               var5 * 1.24F,
               var13 * 0.18F,
               var2.handle(34.0F) * var11,
               var2.handle(4.0F),
               ThemeColors.handle(var3.save(), Math.round(58.0F * var11))
            );
            var1.handle(-var13 * 0.5F, -var5 * 0.62F, var13, var5 * 1.24F, var13 * 0.18F, ThemeColors.handle(var3.save(), Math.round(18.0F * var11)));
            var1.execute();
            var1.prepare();
         }
      }
   }

   private int handle(ThemeRenderContext var1, float var2) {
      ThemeColors var3 = var1.apply();
      return this.handle(var3)
         ? ThemeColors.handle(ThemeColors.handle(246, 248, 252, Math.round(214.0F * var2)), ThemeColors.handle(var3.save(), Math.round(56.0F * var2)), 0.08F)
         : ThemeColors.handle(2, 4, 8, Math.round(240.0F * var2));
   }

   private int handle(ThemeColors var1, int var2) {
      return this.handle(var1)
         ? ThemeColors.handle(ThemeColors.handle(255, 255, 255, Math.min(255, var2 + 8)), ThemeColors.handle(var1.save(), var2), 0.038F)
         : ThemeColors.handle(ThemeColors.handle(8, 10, 16, var2), ThemeColors.handle(var1.save(), var2), 0.026F);
   }

   private int process(ThemeColors var1, int var2) {
      return this.handle(var1)
         ? ThemeColors.handle(ThemeColors.handle(248, 250, 254, Math.min(255, var2 + 6)), ThemeColors.handle(var1.submit(), var2), 0.034F)
         : ThemeColors.handle(ThemeColors.handle(6, 8, 13, var2), ThemeColors.handle(var1.submit(), var2), 0.022F);
   }

   private int compute(ThemeColors var1, int var2) {
      return this.handle(var1) ? ThemeColors.handle(20, 27, 42, Math.round(var2 * 0.36F)) : ThemeColors.handle(0, 0, 0, var2);
   }

   private boolean handle(ThemeColors var1) {
      return switch (this.scaleRender) {
         case AUTO -> var1 != null && var1.unload();
         case DARK -> false;
         case LIGHT -> true;
      };
   }

   private List<FoundryWorkspace.FallbackDataRecord> prepare() {
      ArrayList var1 = new ArrayList();
      if (this.layerSample != 2) {
         for (SavedThemePreset var3 : FoundryStorage.handle().process()) {
            var1.add(new FoundryWorkspace.FallbackDataRecord(var3, -1));
         }
      }

      if (this.layerSample != 1) {
         for (int var4 = 0; var4 < FoundryTemplateRegistry.instance.size(); var4++) {
            var1.add(new FoundryWorkspace.FallbackDataRecord(null, var4));
         }
      }

      return var1;
   }

   private void handle(GuiMetrics var1, RectBounds var2, float var3, float var4) {
      float var5 = this.resolve(var1);
      RectBounds var6 = this.compute(var2, var1);
      if (var6.contains(var3, var4)) {
         List var7 = this.prepare();
         int var8 = (int)Math.floor((var4 - var6.y() + this.matrixFilter) / var5);
         if (var8 >= 0 && var8 < var7.size()) {
            FoundryWorkspace.FallbackDataRecord var9 = (FoundryWorkspace.FallbackDataRecord)var7.get(var8);
            float var10 = var6.y() + var8 * var5 - this.matrixFilter;
            if (var9.presetIndex() >= 0) {
               if (this.compute(var2, var1, var10).contains(var3, var4)) {
                  this.clientRefresh = var9.presetIndex();
                  this.process(false);
               } else if (this.resolve(var2, var1, var10).contains(var3, var4)) {
                  this.clientRefresh = var9.presetIndex();
                  this.process(true);
               } else {
                  this.clientRefresh = var9.presetIndex();
                  this.drawAnimation(FoundryTemplateRegistry.instance.get(var9.presetIndex()).instance);
               }
            } else {
               SavedThemePreset var11 = var9.slot();
               LivePreviewRenderer var12 = LivePreviewRenderer.handle(var11.compute());
               boolean var13 = var11.handle().equals(FoundryStorage.handle().process(var12));
               if (this.process(var2, var1, var10).contains(var3, var4)) {
                  if (var13) {
                     PresetManager.handle().handle(var12);
                     ThemeShaderProgramCache.handle().compute(var12);
                     FoundryStorage.handle().handle(var12, null);
                  }

                  PresetManager.handle().handle(var11.process());
                  ThemeShaderProgramCache.handle().compute(var11.process());
                  FoundryStorage.handle().process(var11.handle());
                  if (var11.handle().equals(this.matrixBlend2)) {
                     this.matrixBlend2 = null;
                  }

                  this.drawAnimation("slot deleted");
               } else if (this.handle(var2, var1, var10).contains(var3, var4)) {
                  this.handle(var11);
               } else {
                  ShaderGraph var14 = FoundryStorage.handle().handle(var11.handle(), this.cache);
                  if (var14 != null) {
                     this.readServer();
                     this.enabled = var14;
                     this.scheduleAnimation();
                     LivePreviewRenderer var15 = var12;
                     this.colorCompute = var15 == LivePreviewRenderer.PREVIEW_ONLY ? LivePreviewRenderer.HUD : var15;
                     this.update(this.colorCompute);
                     this.current.handle(this.colorCompute);
                     this.screenRead = this.enabled.handle().process().isBlank() ? var11.process() : this.enabled.handle().process();
                     this.matrixBlend2 = var11.handle();
                     this.check();
                     this.current.handle(this.enabled);
                     this.handle(var11.process(), this.enabled);
                     this.contextExpand = this.enabled.update();
                     this.update();
                     this.drawAnimation("loaded " + var11.process());
                  }
               }
            }
         }
      }
   }

   private boolean handle(GuiMetrics var1, int var2, int var3, float var4, float var5, int var6) {
      RectBounds var7 = this.compute(var1, var2, var3);
      if (!this.stateApply) {
         return false;
      }

      if (var6 != 0) {
         return var7.contains(var4, var5);
      }

      if (!var7.contains(var4, var5)) {
         this.stateApply = false;
         return true;
      }

      RectBounds var8 = this.resolve(var7, var1);
      if (var8.contains(var4, var5)) {
         this.stateApply = false;
         return true;
      }

      for (int var9 = 0; var9 < context.length; var9++) {
         if (this.process(var7, var1, var9).contains(var4, var5)) {
            this.layerSample = var9;
            this.matrixFilter = 0.0F;
            return true;
         }
      }

      this.handle(var1, var7, var4, var5);
      return true;
   }

   private boolean handle(GuiMetrics var1, int var2, int var3, float var4, float var5, double var6) {
      if (!this.stateApply) {
         return false;
      }

      RectBounds var8 = this.compute(var1, var2, var3);
      if (!var8.contains(var4, var5)) {
         return false;
      }

      RectBounds var9 = this.compute(var8, var1);
      float var10 = this.prepare().size() * this.resolve(var1);
      this.matrixFilter = process(this.matrixFilter - (float)var6 * var1.handle(46.0F), 0.0F, Math.max(0.0F, var10 - var9.h()));
      return true;
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, RectBounds var3, int var4) {
      float var5 = var3.x() + var3.w() * 0.5F;
      float var6 = var3.y() + var3.h() * 0.5F;
      float var7 = var2.handle(9.0F);
      float var8 = var2.handle(9.0F);
      var1.handle(var5 - var7 * 0.5F, var6 - var8 * 0.32F, var7, var8 * 0.82F, var2.handle(1.6F), var4, 0.8F);
      var1.handle(var5 - var7 * 0.62F, var6 - var8 * 0.56F, var7 * 1.24F, var2.handle(1.3F), var2.handle(0.8F), var4);
      var1.handle(var5 - var7 * 0.22F, var6 - var8 * 0.78F, var7 * 0.44F, var2.handle(1.4F), var2.handle(0.8F), var4);
      var1.handle(var5 - var7 * 0.18F, var6 - var8 * 0.12F, var2.handle(1.0F), var8 * 0.45F, var2.handle(0.5F), ThemeColors.handle(var4, 170));
      var1.handle(var5 + var7 * 0.18F, var6 - var8 * 0.12F, var2.handle(1.0F), var8 * 0.45F, var2.handle(0.5F), ThemeColors.handle(var4, 170));
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5, boolean var6) {
      if (var6) {
         var1.process(var3, var4, var2.handle(4.2F), 0.0F, 1.0F, ThemeColors.handle(var5, 90));
         var1.process(var3, var4, var2.handle(2.2F), 0.0F, 1.0F, ThemeColors.handle(var5, 240));
      } else {
         var1.handle(var3 - var2.handle(2.4F), var4 - var2.handle(2.4F), var2.handle(4.8F), var2.handle(4.8F), var2.handle(1.4F), ThemeColors.handle(var5, 116));
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, float var5, float var6, float var7) {
      if (!(var5 <= var4.h() + 1.0F)) {
         float var8 = var4.x() + var4.w() - var2.handle(4.0F);
         float var9 = var4.y() + var2.handle(3.0F);
         float var10 = var4.h() - var2.handle(6.0F);
         float var11 = Math.max(var2.handle(36.0F), var10 * var4.h() / var5);
         float var12 = Math.max(1.0F, var5 - var4.h());
         float var13 = var9 + (var10 - var11) * (this.matrixFilter / var12);
         float var14 = SurfaceInteractionRouter.handle(
            7102L,
            var8 - var2.handle(3.0F),
            var9,
            var2.handle(9.0F),
            var10,
            var13,
            var11,
            var2.handle(6.0F),
            var6,
            var7,
            var2x -> this.matrixFilter = process(var2x, 0.0F, 1.0F) * var12
         );
         float var15 = var2.handle(2.0F) + var2.handle(2.0F) * var14;
         var1.handle(var8, var9, var2.handle(2.0F), var10, var2.handle(1.0F), var3.check());
         var1.handle(var8 + var2.handle(2.0F) - var15, var13, var15, var11, var2.handle(1.5F), ThemeColors.handle(var3.submit(), (int)(150.0F + 80.0F * var14)));
      }
   }
   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      float var6 = this.packetSave.handle();
      if (this.stateApply || !(var6 <= 0.01F)) {
         this.encodePoint();
         GuiMetrics var7 = var3.update();
         ThemeColors var8 = var3.apply();
         RectBounds var9 = this.compute(var7, var4, var5);
         var9 = new RectBounds(var9.x(), var9.y() - var7.handle(10.0F) * (1.0F - var6), var9.w(), var9.h());
         float var10 = var7.handle(12.0F);
         var1.update(var6);
         boolean var24 = false /* VF: Semaphore variable */;

         label203: {
            try {
               var24 = true;
               var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, var7.handle(22.0F), var7.handle(2.0F), this.compute(var8, 148));
               var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, this.process(var8, 232));
               var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, ThemeColors.handle(var8.save(), 108), 0.8F);
               var1.handle(
                  var9.x() + var7.handle(1.0F),
                  var9.y() + var7.handle(1.0F),
                  var9.w() - var7.handle(2.0F),
                  var7.handle(1.0F),
                  var7.handle(1.0F),
                  ThemeColors.handle(var8.load(), this.handle(var8) ? 58 : 18)
               );
               ModuleStateHelper.handle(
                  var1, var7, FontRegistry.config, var9.x() + var7.handle(12.0F), var9.y() + var7.handle(12.0F), 12.0F, "Library", this.process(var8)
               );
               ModuleStateHelper.handle(
                  var1,
                  var7,
                  FontRegistry.instance,
                  var9.x() + var7.handle(12.0F),
                  var9.y() + var7.handle(27.0F),
                  8.0F,
                  "your shaders and presets / preview, bind, apply",
                  ThemeColors.handle(var8.save(), 196)
               );
               RectBounds var11 = this.resolve(var9, var7);
               boolean var12 = var11.contains(var2.sampleLayer(), var2.sendWorld());
               var1.handle(
                  var11.x(),
                  var11.y(),
                  var11.w(),
                  var11.h(),
                  var7.handle(7.0F),
                  ThemeColors.handle(var8.check(), ThemeColors.handle(220, 80, 96, 112), var12 ? 1.0F : 0.0F)
               );
               var1.handle(
                  var11.x(), var11.y(), var11.w(), var11.h(), var7.handle(7.0F), ThemeColors.handle(var12 ? -37756 : var8.select(), var12 ? 210 : 64), 0.65F
               );
               this.handle(var1, var7, var8, var11.x() + var11.w() * 0.5F, var11.y() + var11.h() * 0.5F, 4, var12 ? 1.0F : 0.35F);

               for (int var13 = 0; var13 < context.length; var13++) {
                  this.handle(
                     var1, var7, var8, this.process(var9, var7, var13), context[var13], this.layerSample == var13, var2.sampleLayer(), var2.sendWorld()
                  );
               }

               List var30 = this.prepare();
               RectBounds var14 = this.compute(var9, var7);
               if (var30.isEmpty()) {
                  var1.handle(var14.x(), var14.y(), var14.w(), var14.h(), var7.handle(9.0F), ThemeColors.handle(255, 255, 255, this.handle(var8) ? 38 : 8));
                  var1.handle(var14.x(), var14.y(), var14.w(), var14.h(), var7.handle(9.0F), var8.select(), 0.65F);
                  ModuleStateHelper.handle(
                     var1,
                     var7,
                     FontRegistry.config,
                     var14.x() + var7.handle(12.0F),
                     var14.y() + var7.handle(18.0F),
                     10.0F,
                     "No saved shaders",
                     this.process(var8)
                  );
                  ModuleStateHelper.handle(
                     var1,
                     var7,
                     FontRegistry.instance,
                     var14.x() + var7.handle(12.0F),
                     var14.y() + var7.handle(34.0F),
                     8.0F,
                     "Ctrl+S or File / Save As stores the current graph here.",
                     this.compute(var8)
                  );
                  var24 = false;
                  break label203;
               }

               float var15 = this.resolve(var7);
               float var16 = var30.size() * var15;
               this.matrixFilter = process(this.matrixFilter, 0.0F, Math.max(0.0F, var16 - var14.h()));
               var1.compute();
               var1.handle(var14.x(), var14.y(), var14.w(), var14.h(), var7.handle(9.0F), var7.handle(9.0F), var7.handle(9.0F), var7.handle(9.0F));

               try {
                  for (int var17 = 0; var17 < var30.size(); var17++) {
                     float var18 = var14.y() + var17 * var15 - this.matrixFilter;
                     if (!(var18 > var14.y() + var14.h()) && !(var18 + var15 < var14.y())) {
                        FoundryWorkspace.FallbackDataRecord var19 = (FoundryWorkspace.FallbackDataRecord)var30.get(var17);
                        if (var19.presetIndex() >= 0) {
                           this.handle(var1, var2, var3, var7, var8, var9, var14, var19.presetIndex(), var18, var15, var4, var5, var6);
                        } else {
                           this.handle(var1, var2, var3, var7, var8, var9, var14, var19.slot(), var18, var15, var6);
                        }
                     }
                  }
               } finally {
                  var1.compute();
                  var1.apply();
               }

               this.handle(var1, var7, var8, var14, var16, var2.sampleLayer(), var2.sendWorld());
               var24 = false;
            } finally {
               if (var24) {
                  var1.onTick();
               }
            }

            var1.onTick();
            return;
         }

         var1.onTick();
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ThemeRenderContext var3,
      GuiMetrics var4,
      ThemeColors var5,
      RectBounds var6,
      RectBounds var7,
      SavedThemePreset var8,
      float var9,
      float var10,
      float var11
   ) {
      FoundryStorage var12 = FoundryStorage.handle();
      boolean var13 = var2.sampleLayer() >= var7.x()
         && var2.sampleLayer() <= var7.x() + var7.w()
         && var2.sendWorld() >= var9
         && var2.sendWorld() <= var9 + var10 - var4.handle(6.0F);
      boolean var14 = var8.handle().equals(this.matrixBlend2);
      LivePreviewRenderer var15 = LivePreviewRenderer.handle(var8.compute());
      boolean var16 = var8.handle().equals(var12.process(var15));
      RectBounds var17 = new RectBounds(var7.x(), var9, var7.w() - var4.handle(4.0F), var10 - var4.handle(6.0F));
      float var18 = Math.max(var13 ? 0.75F : 0.0F, var14 ? 0.58F : 0.0F);
      var1.handle(
         var17.x(),
         var17.y(),
         var17.w(),
         var17.h(),
         var4.handle(8.0F),
         ThemeColors.handle(ThemeColors.handle(255, 255, 255, this.handle(var5) ? 46 : 10), ThemeColors.handle(var5.save(), 72), var18)
      );
      var1.handle(
         var17.x(),
         var17.y(),
         var17.w(),
         var17.h(),
         var4.handle(8.0F),
         ThemeColors.handle(var5.select(), ThemeColors.handle(var16 ? var5.submit() : var5.save(), 126), Math.max(var18, var16 ? 0.38F : 0.0F)),
         0.65F
      );
      RectBounds var19 = this.update(var17, var4);
      this.handle(var1, var2, var3, var8, var19, var11);
      this.handle(var1, var4, var17.x() + var4.handle(12.0F), var17.y() + var17.h() * 0.5F, var16 ? var5.submit() : var5.save(), var14);
      float var20 = var19.x() + var19.w() + var4.handle(10.0F);
      RectBounds var21 = this.handle(var6, var4, var9);
      RectBounds var22 = this.process(var6, var4, var9);
      float var23 = Math.max(var4.handle(72.0F), var21.x() - var20 - var4.handle(10.0F));
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.config,
         var20,
         var17.y() + var4.handle(8.0F),
         10.0F,
         ModuleStateHelper.handle(var4, FontRegistry.config, var8.process(), 10.0F, var23),
         this.process(var5)
      );
      String var24 = (var15 == null ? "Unknown" : var15.process()) + (var16 ? " / bound" : "") + " / " + this.handle(var8.select());
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.instance,
         var20,
         var17.y() + var4.handle(25.0F),
         8.0F,
         ModuleStateHelper.handle(var4, FontRegistry.instance, var24, 8.0F, var23),
         ThemeColors.handle(var5.save(), 200)
      );
      String var25 = var8.execute() + " / " + PresetManager.handle().prepare(var8.process()).size() + " uniforms";
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.instance,
         var20,
         var17.y() + var4.handle(40.0F),
         8.0F,
         ModuleStateHelper.handle(var4, FontRegistry.instance, var25, 8.0F, var23),
         ThemeColors.handle(var5.submit(), 176)
      );
      boolean var26 = var21.contains(var2.sampleLayer(), var2.sendWorld());
      this.handle(var1, var4, var5, var21, var16 ? "Off" : "Bind", var26, var16);
      boolean var27 = var22.contains(var2.sampleLayer(), var2.sendWorld());
      var1.handle(
         var22.x(),
         var22.y(),
         var22.w(),
         var22.h(),
         var4.handle(7.0F),
         ThemeColors.handle(var5.check(), ThemeColors.handle(230, 82, 96, 128), var27 ? 1.0F : 0.0F)
      );
      var1.handle(var22.x(), var22.y(), var22.w(), var22.h(), var4.handle(7.0F), ThemeColors.handle(var27 ? -37756 : var5.select(), var27 ? 220 : 70), 0.65F);
      this.handle(var1, var4, var22, var27 ? ThemeColors.handle(255, 214, 220, 242) : ThemeColors.handle(var5.load(), 148));
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ThemeRenderContext var3,
      GuiMetrics var4,
      ThemeColors var5,
      RectBounds var6,
      RectBounds var7,
      int var8,
      float var9,
      float var10,
      int var11,
      int var12,
      float var13
   ) {
      FoundryTemplateRegistry.CacheEntry var14 = FoundryTemplateRegistry.instance.get(var8);
      boolean var15 = var2.sampleLayer() >= var7.x()
         && var2.sampleLayer() <= var7.x() + var7.w()
         && var2.sendWorld() >= var9
         && var2.sendWorld() <= var9 + var10 - var4.handle(6.0F);
      boolean var16 = var8 == this.clientRefresh;
      RectBounds var17 = new RectBounds(var7.x(), var9, var7.w() - var4.handle(4.0F), var10 - var4.handle(6.0F));
      float var18 = Math.max(var15 ? 0.62F : 0.0F, var16 ? 0.5F : 0.0F);
      var1.handle(
         var17.x(),
         var17.y(),
         var17.w(),
         var17.h(),
         var4.handle(8.0F),
         ThemeColors.handle(ThemeColors.handle(255, 255, 255, this.handle(var5) ? 46 : 10), ThemeColors.handle(var5.submit(), 66), var18)
      );
      var1.handle(
         var17.x(),
         var17.y(),
         var17.w(),
         var17.h(),
         var4.handle(8.0F),
         ThemeColors.handle(var5.select(), ThemeColors.handle(var16 ? var5.save() : var5.submit(), var16 ? 148 : 112), Math.max(var18, var16 ? 0.6F : 0.0F)),
         var16 ? 0.85F : 0.65F
      );
      if (var16) {
         var1.handle(
            var17.x(),
            var17.y() + var4.handle(9.0F),
            var4.handle(2.4F),
            var17.h() - var4.handle(18.0F),
            var4.handle(1.2F),
            ThemeColors.handle(var5.save(), 230)
         );
      }

      RectBounds var19 = this.update(var17, var4);
      FoundryEntityPreviewRenderer.handle(
         var1,
         var3,
         this.keyFilter.get(var8),
         "__preset_thumb_" + var8,
         var19.x(),
         var19.y(),
         var19.w(),
         var19.h(),
         var11,
         var12,
         var2.sampleLayer(),
         var2.sendWorld(),
         var13
      );
      var1.handle(var19.x(), var19.y(), var19.w(), var19.h(), var4.handle(6.0F), ThemeColors.handle(var5.submit(), Math.round(84.0F * var13)), 0.55F);
      this.handle(var1, var4, var17.x() + var4.handle(12.0F), var17.y() + var17.h() * 0.5F, var5.submit(), var16);
      float var20 = var19.x() + var19.w() + var4.handle(10.0F);
      RectBounds var21 = this.compute(var6, var4, var9);
      float var22 = Math.max(var4.handle(72.0F), var21.x() - var20 - var4.handle(10.0F));
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.config,
         var20,
         var17.y() + var4.handle(8.0F),
         10.0F,
         ModuleStateHelper.handle(var4, FontRegistry.config, var14.instance, 10.0F, var22),
         this.process(var5)
      );
      String var23 = var14.context.process();
      float var24 = ModuleStateHelper.handle(var4, FontRegistry.instance, var23, 7.0F) + var4.handle(12.0F);
      float var25 = var17.y() + var4.handle(23.0F);
      var1.handle(var20, var25, var24, var4.handle(13.0F), var4.handle(6.5F), ThemeColors.handle(var5.submit(), 44));
      ModuleStateHelper.handle(
         var1, var4, FontRegistry.instance, var20 + var4.handle(6.0F), var25, var4.handle(13.0F), 7.0F, var23, ThemeColors.handle(var5.submit(), 235)
      );
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.instance,
         var20 + var24 + var4.handle(8.0F),
         var25 + var4.handle(3.0F),
         7.5F,
         ModuleStateHelper.handle(var4, FontRegistry.instance, "preset / " + var14.config, 7.5F, Math.max(1.0F, var22 - var24 - var4.handle(8.0F))),
         ThemeColors.handle(var5.save(), 186)
      );
      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.instance,
         var20,
         var17.y() + var4.handle(40.0F),
         8.0F,
         ModuleStateHelper.handle(var4, FontRegistry.instance, var14.data, 8.0F, var22),
         this.compute(var5)
      );
      this.handle(var1, var4, var5, var21, "Use", var2.sampleLayer(), var2.sendWorld(), true);
      this.handle(var1, var4, var5, this.resolve(var6, var4, var9), "Merge", var2.sampleLayer(), var2.sendWorld(), false);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, SavedThemePreset var4, RectBounds var5, float var6) {
      GuiMetrics var7 = var3.update();
      ThemeColors var8 = var3.apply();
      float var9 = var7.handle(6.0F);
      var1.handle(var5.x(), var5.y(), var5.w(), var5.h(), var9, ThemeColors.handle(255, 255, 255, this.handle(var8) ? 48 : 10));
      boolean var10 = false;
      String var11 = var4 == null ? "" : var4.process();
      if (!var11.isBlank() && PresetManager.handle().update(var11)) {
         ShaderGraph var12 = PresetManager.handle().compute(var11);
         LivePreviewRenderer var13 = LivePreviewRenderer.handle(var4.compute());
         if (var13 == LivePreviewRenderer.PREVIEW_ONLY) {
            var13 = this.colorCompute;
         }

         if (var12 != null) {
            FoundryEntityPreviewRenderer.handle(
               var1,
               var3,
               var11,
               var13,
               var12,
               var5.x(),
               var5.y(),
               var5.w(),
               var5.h(),
               this.fetchProvider(),
               this.drawProfile(),
               var2.sampleLayer(),
               var2.sendWorld(),
               var6
            );
            var10 = true;
         }
      }

      if (!var10) {
         var1.process(
            var5.x(),
            var5.y(),
            var5.w(),
            var5.h(),
            var9,
            ThemeColors.handle(var8.save(), Math.round(70.0F * var6)),
            ThemeColors.handle(var8.submit(), Math.round(42.0F * var6))
         );
      }

      var1.handle(var5.x(), var5.y(), var5.w(), var5.h(), var9, ThemeColors.handle(var8.save(), Math.round(84.0F * var6)), 0.55F);
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, String var5, boolean var6, boolean var7) {
      float var8 = Math.max(var7 ? 0.62F : 0.0F, var6 ? 1.0F : 0.0F);
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(7.0F),
         ThemeColors.handle(var3.check(), ThemeColors.handle(var7 ? var3.submit() : var3.save(), 84), var8)
      );
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(7.0F),
         ThemeColors.handle(var3.select(), ThemeColors.handle(var7 ? var3.submit() : var3.save(), 152), var8),
         0.62F
      );
      float var9 = ModuleStateHelper.handle(var2, FontRegistry.config, var5, 9.0F);
      float var10 = var4.x() + (var4.w() - var9 - var2.handle(10.0F)) * 0.5F;
      int var11 = ThemeColors.handle(var7 ? var3.submit() : var3.save(), Math.round(160.0F + 80.0F * var8));
      float var12 = var4.y() + var4.h() * 0.5F;
      if (var7) {
         var1.process(var10 + var2.handle(3.0F), var12, var2.handle(3.0F), 0.0F, 1.0F, ThemeColors.handle(var11, 88));
         var1.process(var10 + var2.handle(3.0F), var12, var2.handle(1.6F), 0.0F, 1.0F, var11);
      } else {
         var1.handle(var10 + var2.handle(3.0F), var12, var2.handle(2.6F), 0.0F, 1.0F, 0.9F, var11);
      }

      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.config,
         var10 + var2.handle(10.0F),
         var4.y(),
         var4.h(),
         9.0F,
         var5,
         ThemeColors.handle(var3.animate(), var3.load(), 0.52F + var8 * 0.48F)
      );
   }

   public boolean handle(ModernClickGuiState var1, ThemeRenderContext var2, float var3, float var4, int var5, int var6, int var7) {
      if (var1 != null && var2 != null && this.handle(var1)) {
         this.rendererScan = var3;
         this.sourceBuild = var4;
         GuiMetrics var8 = var2.update();
         if (this.selection.process()) {
            boolean var17 = this.selection.handle(var3, var4, var5, var8, var6, var7);
            this.measureColor();
            return var17;
         }

         if (this.active.handle()) {
            RectBounds var9 = this.active.handle(var8, var6, var7);
            if (var9.contains(var3, var4)) {
               if (var5 == 0) {
                  ShaderNodeDefinition var18 = this.active.handle(var8, var6, var7, var3, var4);
                  if (var18 != null) {
                     this.handle(var18);
                     return true;
                  }

                  String var20 = this.active.process(var8, var6, var7, var3, var4);
                  if (var20 != null) {
                     this.active.handle(var20);
                     return true;
                  }
               }

               return true;
            }

            if (var5 == 0 || var5 == 1) {
               this.active.update();
               return true;
            }
         }

         if (this.configCollapse) {
            return this.compute(var8, var6, var7, var3, var4, var5);
         }

         if (this.optionParse != null && !this.handle(var8, var6, var7).contains(var3, var4)) {
            this.submit();
         }

         if (this.targetWrite && (this.entryAnimate || !this.handle(var8, var7).contains(var3, var4))) {
            this.targetWrite = false;
         }

         if (var5 == 0 && this.handle(var1, var8, var6, var3, var4)) {
            return true;
         }

         if (this.stateApply && this.handle(var8, var6, var7, var3, var4, var5)) {
            return true;
         }

         if (this.scaleAdapt && this.handle(var8, var6, var3, var4, var5)) {
            return true;
         }

         if (this.textureRun && this.process(var8, var6, var7, var3, var4, var5)) {
            return true;
         }

         if (this.indexBind && this.process(var8, var6, var3, var4, var5)) {
            return true;
         }

         if (this.resolve(var8, var6, var7, var3, var4, var5)) {
            return true;
         }

         RectBounds var16 = this.process(var8, var7);
         RectBounds var10 = this.compute(var8, var7);
         if (var5 == 0 && var10.contains(var3, var4)) {
            this.entryAnimate = !this.entryAnimate;
            return true;
         }

         if (this.entryAnimate || var5 != 0 || !var16.contains(var3, var4)) {
            RectBounds var11 = this.process(var8, var6, var7);
            if (var5 == 0 && var11.contains(var3, var4)) {
               if (var4 <= var11.y() + var8.handle(34.0F)) {
                  this.layoutSave = true;
                  this.blockRun = var3 - var11.x();
                  this.playerEvaluate = var4 - var11.y();
               }

               return true;
            } else {
               if (var5 == 0) {
                  ShaderGraphBlock var12 = this.update(var3, var4);
                  if (var12 != null) {
                     this.onTick(var12.handle());
                     this.handle(var12.handle());
                     return true;
                  }
               }

               if (var5 != 2 && (var5 != 0 || !this.computeResponse())) {
                  FoundryWorkspace.SecondaryDataRecord var21 = this.apply(var3, var4);
                  if (var5 == 0 && var21 != null) {
                     if (var21.direction == ShaderPinDirection.OUTPUT) {
                        this.colorMeasure = var21.nodeId;
                        this.animationSchedule = var21.pinId;
                        this.handle(var21.nodeId);
                     } else {
                        this.readServer();
                        if (this.enabled.handle(var21.nodeId, var21.pinId)) {
                           this.rendererCancel.handle(1.0F);
                        }

                        this.handle(var21.nodeId);
                     }

                     return true;
                  } else {
                     ShaderGraphBlock var13 = this.resolve(var3, var4);
                     if (var5 == 0 && var13 != null) {
                        if (Screen.hasShiftDown()) {
                           this.vectorPerform.add(var13.handle());
                           this.profileDraw = var13.handle();
                        } else if (!this.compute(var13.handle())) {
                           this.handle(var13.handle());
                        } else {
                           this.profileDraw = var13.handle();
                        }

                        if (execute(var13.process()) && this.compute(var13).contains(var3, var4)) {
                           this.readServer();
                           this.load();
                           ShaderGraphNode var24 = this.handle(var13);
                           var24.handle(var13.handle("value", "int_value".equals(var13.process()) ? 1.0F : 0.5F));
                           if (var24.handle(var3, var4, var5, this.compute(var13))) {
                              this.optionAdvance = var13.handle();
                           }

                           return true;
                        } else if (this.update(var13) && this.resolve(var13).contains(var3, var4)) {
                           this.readServer();
                           this.load();
                           this.save();
                           ShaderGraphNode var14 = this.process(var13);
                           var14.handle(var13.handle("name", this.apply(var13)));
                           if (var14.handle(var3, var4, var5, this.resolve(var13))) {
                              this.effectScan = var13.handle();
                           }

                           return true;
                        } else {
                           this.load();
                           this.save();
                           this.readServer();
                           this.itemProject = var13.handle();
                           this.responseCompute = this.update(var3) - var13.compute();
                           this.providerFetch = this.apply(var4) - var13.resolve();
                           this.handle(var3, var4);
                           return true;
                        }
                     } else {
                        if (this.optionAdvance != null) {
                           this.load();
                        }

                        if (this.effectScan != null) {
                           this.save();
                        }

                        if (var5 == 1) {
                           this.active.handle(var3, var4, null);
                           return true;
                        }

                        if (var5 == 0) {
                           this.moduleCollect = true;
                           this.eventAttach.clear();
                           if (Screen.hasShiftDown()) {
                              this.eventAttach.addAll(this.vectorPerform);
                           }

                           this.providerClose = var3;
                           this.presetSave = var4;
                           this.windowConvert = var3;
                           this.presetWrite = var4;
                           if (!Screen.hasShiftDown()) {
                              this.check();
                           }

                           return true;
                        } else {
                           return true;
                        }
                     }
                  }
               } else {
                  this.process(var3, var4);
                  return true;
               }
            }
         } else {
            if (this.handle(var8, var7).contains(var3, var4)) {
               this.targetWrite = true;
               this.resultEncode = System.currentTimeMillis();
               return true;
            }

            this.targetWrite = false;
            FoundryWorkspace.CachedDataRecord var19 = this.handle(var8, var7, var3, var4);
            if (var19 != null) {
               if (var19.row().type() == 0) {
                  if (!this.messageParse.remove(var19.row().category())) {
                     this.messageParse.add(var19.row().category());
                  }

                  return true;
               }

               ShaderNodeDefinition var22 = var19.row().def();
               if (var19.star()) {
                  FavoritesStorage.handle().process(var22.handle());
                  return true;
               }

               float var23 = this.update(var6 * 0.5F);
               float var25 = this.apply(var7 * 0.5F);
               this.readServer();
               ShaderGraphBlock var15 = this.enabled.handle(var22.handle(), var23 - var22.resolve() * 0.5F, var25 - this.process(var22) * 0.5F, this.cache);
               this.handle(var15.handle());
               this.configMatch.put(var15.handle(), new SpringAnimation(0.0F));
               FavoritesStorage.handle().compute(var22.handle());
               this.drawAnimation(var22.process());
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void handle(ShaderNodeDefinition var1) {
      float var2 = this.active.compute();
      float var3 = this.active.resolve();
      float var4 = this.update(var2);
      float var5 = this.apply(var3);
      this.readServer();
      ShaderGraphBlock var6 = this.enabled.handle(var1.handle(), var4 - var1.resolve() * 0.5F, var5 - this.process(var1) * 0.5F, this.cache);
      this.handle(var6.handle());
      this.configMatch.put(var6.handle(), new SpringAnimation(0.0F));
      if (var1.prepare()) {
         this.bufferAdapt.put(var6.handle(), true);
         this.refresh(var6.handle()).compute(1.0F);
         this.select(var6.handle());
      }

      if (this.active.process() != null && this.pointSubmit != null && this.listenerPerform != null) {
         String var7 = null;

         for (ShaderPinDefinition var9 : var1.update()) {
            if (var9.type() == this.active.process()) {
               var7 = var9.id();
               break;
            }
         }

         if (var7 != null) {
            this.enabled.handle(this.pointSubmit, this.listenerPerform, var6.handle(), var7, this.cache);
            this.rendererCancel.handle(1.0F);
         }
      }

      this.pointSubmit = null;
      this.listenerPerform = null;
      this.active.update();
      FavoritesStorage.handle().compute(var1.handle());
      this.drawAnimation(var1.process());
   }

   private void handle(String var1) {
      this.vectorPerform.clear();
      if (var1 != null) {
         this.vectorPerform.add(var1);
      }

      this.profileDraw = var1;
   }

   private void check() {
      this.vectorPerform.clear();
      this.profileDraw = null;
      this.optionParse = null;
   }

   private void onTick() {
      for (SpringFloat var2 : this.playerUpdate.values()) {
         if (var2 != null) {
            var2.compute(0.0F);
         }
      }

      this.bufferAdapt.clear();
      this.playerUpdate.clear();
   }

   private void process(String var1) {
      if (var1 != null) {
         this.playerCollapse.keySet().removeIf(var1x -> var1x.startsWith(var1 + ":"));
      }
   }

   private boolean compute(String var1) {
      return var1 != null && this.vectorPerform.contains(var1);
   }

   private void select() {
      if (this.vectorPerform.isEmpty()) {
         this.profileDraw = null;
      } else {
         if (this.profileDraw == null || !this.vectorPerform.contains(this.profileDraw)) {
            this.profileDraw = this.vectorPerform.iterator().next();
         }
      }
   }

   private void handle(float var1, float var2) {
      this.serverRead.clear();
      if (!this.vectorPerform.isEmpty() && this.vectorPerform.contains(this.itemProject)) {
         for (String var4 : this.vectorPerform) {
            ShaderGraphBlock var5 = this.enabled.compute(var4);
            if (var5 != null) {
               this.serverRead.put(var4, new FoundryWorkspace.DataRecord(var5.compute(), var5.resolve()));
            }
         }
      } else {
         ShaderGraphBlock var3 = this.enabled.compute(this.itemProject);
         if (var3 != null) {
            this.serverRead.put(var3.handle(), new FoundryWorkspace.DataRecord(var3.compute(), var3.resolve()));
         }
      }

      this.positionAdvance = this.update(var1);
      this.frameCheck = this.apply(var2);
   }

   private void handle(boolean var1) {
      float var2 = Math.min(this.providerClose, this.windowConvert);
      float var3 = Math.min(this.presetSave, this.presetWrite);
      float var4 = Math.max(this.providerClose, this.windowConvert);
      float var5 = Math.max(this.presetSave, this.presetWrite);
      this.vectorPerform.clear();
      if (var1) {
         this.vectorPerform.addAll(this.eventAttach);
      }

      for (ShaderGraphBlock var7 : this.enabled.compute()) {
         ShaderNodeDefinition var8 = this.cache.handle(var7.process());
         if (var8 != null) {
            float var9 = this.compute(var7.compute());
            float var10 = this.resolve(var7.resolve());
            float var11 = var8.resolve() * this.animationDraw;
            float var12 = this.handle(var8, var7) * this.animationDraw;
            if (handle(var2, var3, var4 - var2, var5 - var3, var9, var10, var11, var12)) {
               this.vectorPerform.add(var7.handle());
            }
         }
      }

      this.select();
   }

   private void refresh() {
      if (!this.vectorPerform.isEmpty()) {
         this.readServer();
         ArrayList<String> var1 = new ArrayList<>(this.vectorPerform);
         HashMap var2 = new HashMap();
         this.vectorPerform.clear();

         for (String var4 : var1) {
            ShaderGraphBlock var5 = this.enabled.compute(var4);
            if (var5 != null) {
               ShaderGraphBlock var6 = this.enabled.handle(var5.process(), var5.compute() + 42.0F, var5.resolve() + 42.0F, this.cache);
               var6.apply().putAll(var5.apply());
               var6.execute().putAll(var5.execute());
               var2.put(var4, var6.handle());
               this.vectorPerform.add(var6.handle());
               this.configMatch.put(var6.handle(), new SpringAnimation(0.0F));
            }
         }

         for (ShaderGraphLink var8 : new ArrayList<>(this.enabled.resolve())) {
            String var9 = (String)var2.get(var8.handle());
            String var10 = (String)var2.get(var8.compute());
            if (var9 != null && var10 != null) {
               this.enabled.handle(var9, var8.process(), var10, var8.resolve(), this.cache);
            }
         }

         this.select();
         this.rendererCancel.handle(1.0F);
         this.drawAnimation("duplicated " + this.vectorPerform.size());
      }
   }

   private void render() {
      this.screenRead = update(this.screenRead);
      FoundryStorage var1 = FoundryStorage.handle();
      SavedThemePreset var2 = this.matrixBlend2 == null ? null : var1.handle(this.matrixBlend2);
      String var3 = var2 == null ? "" : var2.process();
      this.scanRenderer();
      this.enabled.handle().handle(this.screenRead, FoundryStorage.execute());
      this.enabled.handle().handle(this.screenRead);
      this.enabled.handle().process(this.enabled.handle().compute().isBlank() ? FoundryStorage.execute() : this.enabled.handle().compute());
      this.enabled.handle().update("local");
      this.enabled.handle().process(System.currentTimeMillis());
      this.update(this.colorCompute);
      this.current.handle(this.colorCompute);
      boolean var4 = this.current.handle(this.screenRead, this.enabled);
      if (var4) {
         SavedThemePreset var5 = var1.handle(this.colorCompute, this.enabled, this.screenRead, this.matrixBlend2);
         if (var5 != null) {
            this.matrixBlend2 = var5.handle();
            this.handle(var5.process(), this.enabled);
         }

         if (!var3.isBlank() && !PresetManager.onTick(var3).equals(PresetManager.onTick(this.screenRead))) {
            PresetManager.handle().handle(var3);
            ThemeShaderProgramCache.handle().compute(var3);
         }

         this.contextExpand = this.enabled.update();
         this.drawAnimation("saved " + this.screenRead);
      } else {
         this.drawAnimation(this.current.process().isBlank() ? "compile failed" : this.current.process());
      }
   }

   private void tick() {
      this.screenRead = this.resolve(update(this.screenRead));
      this.matrixBlend2 = null;
      this.render();
   }

   private String resolve(String var1) {
      String var2 = var1 != null && !var1.isBlank() ? var1 : ShaderPresetNameGenerator.handle();

      for (int var3 = 1; var3 < 128; var3++) {
         String var4 = var3 == 1 ? var2 + " Copy" : var2 + " Copy " + var3;
         boolean var5 = false;

         for (SavedThemePreset var7 : FoundryStorage.handle().handle(this.colorCompute)) {
            if (var7.process().equalsIgnoreCase(var4)) {
               var5 = true;
               break;
            }
         }

         if (!var5 && !PresetManager.handle().update(var4)) {
            return var4;
         }
      }

      return var2 + " Copy " + System.currentTimeMillis() % 10000L;
   }

   private void drawAnimation() {
      int var1 = FoundryStorage.handle().handle(MicaStylePresets.handle());
      this.drawAnimation(var1 == 0 ? "no legacy slots" : "cleanup " + var1 + " legacy");
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      return var0 < var4 + var6 && var0 + var2 > var4 && var1 < var5 + var7 && var1 + var3 > var5;
   }

   private static String update(String var0) {
      String var1 = PresetManager.onTick(var0);
      return var1.isBlank() ? ShaderPresetNameGenerator.handle() : var1;
   }

   private static boolean handle(char var0) {
      return Character.isLetterOrDigit(var0) || var0 == ' ' || var0 == '_' || var0 == '-' || var0 == '.';
   }

   public boolean handle(ModernClickGuiState var1, float var2, float var3) {
      if (var1 != null && this.handle(var1)) {
         this.rendererScan = var2;
         this.sourceBuild = var3;
         if (this.layoutSave) {
            this.listenerRun = var2 - this.blockRun;
            this.indexLoad = var3 - this.playerEvaluate;
            return true;
         }

         if (this.source) {
            float var14 = this.renderer;
            float var16 = this.handler;
            this.renderer = this.previous + var2 - this.target;
            this.handler = this.latest + var3 - this.pending;
            this.compute(var14, var16);
            return true;
         }

         if (this.optionAdvance != null) {
            ShaderGraphNode var4 = this.matrixRender.get(this.optionAdvance);
            if (var4 != null && var4.handle(var2, var3, Screen.hasShiftDown())) {
               ShaderGraphBlock var15 = this.enabled.compute(this.optionAdvance);
               if (var15 != null) {
                  var15.process("value", handle(var15, var4.compute()));
                  this.enabled.apply();
               }

               return true;
            }
         }

         if (this.effectScan != null) {
            ShaderGraphNode var11 = this.moduleTick.get(this.effectScan);
            if (var11 != null && var11.handle(var2, var3, Screen.hasShiftDown())) {
               return true;
            }
         }

         if (this.optionParse != null) {
            ShaderGraphNode var12 = this.playerCollapse.get(this.optionParse);
            if (var12 != null && var12.handle(var2, var3, Screen.hasShiftDown())) {
               this.prepare(this.optionParse);
               return true;
            }
         }

         if (this.moduleCollect) {
            this.windowConvert = var2;
            this.presetWrite = var3;
            this.handle(Screen.hasShiftDown());
            return true;
         }

         if (this.itemProject == null) {
            return this.colorMeasure != null;
         }

         ShaderGraphBlock var13 = this.enabled.compute(this.itemProject);
         if (var13 != null) {
            if (this.serverRead.size() > 1 || this.serverRead.size() == 1 && this.serverRead.containsKey(var13.handle())) {
               float var5 = this.update(var2) - this.positionAdvance;
               float var6 = this.apply(var3) - this.frameCheck;

               for (Entry var8 : this.serverRead.entrySet()) {
                  ShaderGraphBlock var9 = this.enabled.compute((String)var8.getKey());
                  if (var9 != null) {
                     FoundryWorkspace.DataRecord var10 = (FoundryWorkspace.DataRecord)var8.getValue();
                     var9.handle(var10.x + var5, var10.y + var6);
                  }
               }
            } else {
               var13.handle(this.update(var2) - this.responseCompute, this.apply(var3) - this.providerFetch);
            }

            this.enabled.apply();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean process(ModernClickGuiState var1, float var2, float var3) {
      if (var1 != null && this.handle(var1)) {
         if (this.colorMeasure != null) {
            FoundryWorkspace.SecondaryDataRecord var4 = this.apply(var2, var3);
            if (var4 != null && var4.direction == ShaderPinDirection.INPUT) {
               this.readServer();
               boolean var11 = this.enabled.handle(this.colorMeasure, this.animationSchedule, var4.nodeId, var4.pinId, this.cache);
               if (var11) {
                  this.rendererCancel.handle(1.0F);
               }

               this.drawAnimation(var11 ? "linked" : "cycle / type guard");
            } else if (var4 == null && this.resolve(var2, var3) == null) {
               ShaderGraphBlock var5 = this.enabled.compute(this.colorMeasure);
               if (var5 != null) {
                  ShaderNodeDefinition var6 = this.cache.handle(var5.process());
                  ShaderPinDefinition var7 = var6 == null ? null : var6.process(this.animationSchedule);
                  if (var7 != null) {
                     this.pointSubmit = this.colorMeasure;
                     this.listenerPerform = this.animationSchedule;
                     this.active.handle(var2, var3, var7.type());
                  }
               }
            }
         }

         if (this.moduleCollect) {
            this.windowConvert = var2;
            this.presetWrite = var3;
            this.handle(Screen.hasShiftDown());
         }

         if (this.optionAdvance != null) {
            ShaderGraphNode var8 = this.matrixRender.get(this.optionAdvance);
            if (var8 != null) {
               if (var8.resolve(var2, var3)) {
                  ShaderGraphBlock var12 = this.enabled.compute(this.optionAdvance);
                  if (var12 != null) {
                     var12.process("value", handle(var12, var8.compute()));
                     this.enabled.apply();
                  }
               }

               if (!var8.prepare()) {
                  this.optionAdvance = null;
               }
            }
         }

         if (this.effectScan != null) {
            ShaderGraphNode var9 = this.moduleTick.get(this.effectScan);
            if (var9 != null) {
               if (var9.resolve(var2, var3)) {
                  ShaderGraphBlock var13 = this.enabled.compute(this.effectScan);
                  if (var13 != null) {
                     var13.process("name", var9.resolve());
                     this.enabled.apply();
                  }
               }

               if (!var9.prepare()) {
                  this.effectScan = null;
               }
            }
         }

         if (this.optionParse != null) {
            ShaderGraphNode var10 = this.playerCollapse.get(this.optionParse);
            if (var10 != null) {
               if (var10.resolve(var2, var3)) {
                  this.prepare(this.optionParse);
               }

               if (!var10.prepare()) {
                  this.prepare(this.optionParse);
                  this.optionParse = null;
               }
            }
         }

         this.source = false;
         this.layoutSave = false;
         this.itemProject = null;
         this.serverRead.clear();
         this.moduleCollect = false;
         this.eventAttach.clear();
         this.colorMeasure = null;
         this.animationSchedule = null;
         this.settingSchedule = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean handle(ModernClickGuiState var1, float var2, float var3, double var4) {
      if (var1 == null || !this.handle(var1)) {
         return false;
      } else if (this.selection.process()) {
         return this.selection
            .handle(
               var4,
               this.performVector(),
               this.scaleParse <= 0 ? this.fetchProvider() : this.scaleParse,
               this.sessionEncode <= 0 ? this.drawProfile() : this.sessionEncode
            );
      } else if (this.active.handle()) {
         this.active.handle(var4);
         return true;
      } else {
         GuiMetrics var6 = this.performVector();
         int var7 = this.scaleParse <= 0 ? this.fetchProvider() : this.scaleParse;
         int var8 = this.sessionEncode <= 0 ? this.drawProfile() : this.sessionEncode;
         if (this.handle(var6, var7, var8, var2, var3, var4)) {
            return true;
         } else if (!this.entryAnimate && this.process(var6, this.sessionEncode <= 0 ? this.drawProfile() : this.sessionEncode).contains(var2, var3)) {
            this.playerCollect = Math.max(0.0F, this.playerCollect - (float)var4 * var6.handle(28.0F));
            return true;
         } else {
            float var9 = (var2 - this.renderer) / Math.max(0.001F, this.pointEncode);
            float var10 = (var3 - this.handler) / Math.max(0.001F, this.pointEncode);
            float var11 = (float)Math.exp(var4 * 0.105);
            this.pointEncode = process(this.pointEncode * var11, 0.34F, 2.45F);
            this.renderer = var2 - var9 * this.pointEncode;
            this.handler = var3 - var10 * this.pointEncode;
            this.summary = 0.0F;
            this.matrixBlend = 0.0F;
            return true;
         }
      }
   }

   public boolean handle(ModernClickGuiState var1, char var2) {
      if (var1 == null || !this.handle(var1)) {
         return false;
      }

      if (this.selection.process()) {
         return this.selection.handle(var2);
      }

      if (this.animationExpand) {
         if (handle(var2) && this.screenRead.length() < 48) {
            this.screenRead = this.screenRead + var2;
            this.playerRun = System.currentTimeMillis();
         }

         return true;
      } else {
         if (this.active.handle() && var2 != ' ') {
            this.active.handle(var2);
            return true;
         }

         if (this.targetWrite) {
            if (handle(var2) && this.worldSend.length() < 40) {
               this.worldSend = this.worldSend + var2;
               this.resultEncode = System.currentTimeMillis();
               this.playerCollect = 0.0F;
            }

            return true;
         } else {
            if (this.optionAdvance != null) {
               ShaderGraphNode var3 = this.matrixRender.get(this.optionAdvance);
               if (var3 != null && var3.handle(var2)) {
                  return true;
               }
            }

            if (this.effectScan != null) {
               ShaderGraphNode var4 = this.moduleTick.get(this.effectScan);
               if (var4 != null && var4.handle(var2)) {
                  return true;
               }
            }

            if (this.optionParse != null) {
               ShaderGraphNode var5 = this.playerCollapse.get(this.optionParse);
               if (var5 != null && var5.handle(var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean handle(ModernClickGuiState var1, int var2) {
      if (var1 == null || !this.handle(var1)) {
         return false;
      }

      if (this.selection.process()) {
         boolean var11 = this.selection.handle(var2);
         this.measureColor();
         return var11;
      }

      if (this.animationExpand) {
         if (var2 == 256) {
            this.animationExpand = false;
            this.screenRead = update(this.screenRead);
            return true;
         }

         if (var2 == 257 || var2 == 335 || var2 == 258) {
            this.animationExpand = false;
            this.screenRead = update(this.screenRead);
            return true;
         }

         if (var2 == 259) {
            if (!this.screenRead.isEmpty()) {
               this.screenRead = this.screenRead.substring(0, this.screenRead.length() - 1);
               this.playerRun = System.currentTimeMillis();
            }

            return true;
         } else {
            return true;
         }
      } else if (this.active.handle()) {
         if (var2 == 256) {
            this.active.update();
            this.pointSubmit = null;
            this.listenerPerform = null;
            return true;
         }

         if (var2 == 257 || var2 == 335) {
            ShaderNodeDefinition var10 = this.active.prepare();
            if (var10 != null) {
               this.handle(var10);
            } else {
               this.active.update();
            }

            return true;
         } else if (var2 == 259) {
            this.active.apply();
            return true;
         } else if (var2 == 264) {
            this.active.handle(1);
            return true;
         } else if (var2 == 265) {
            this.active.handle(-1);
            return true;
         } else {
            return true;
         }
      } else {
         if (this.optionAdvance != null) {
            ShaderGraphNode var3 = this.matrixRender.get(this.optionAdvance);
            if (var3 != null && var3.handle(var2)) {
               if (!var3.update()) {
                  ShaderGraphBlock var13 = this.enabled.compute(this.optionAdvance);
                  if (var13 != null) {
                     var13.process("value", handle(var13, var3.compute()));
                     this.enabled.apply();
                  }

                  this.optionAdvance = null;
               }

               return true;
            }
         }

         if (this.effectScan != null) {
            ShaderGraphNode var7 = this.moduleTick.get(this.effectScan);
            if (var7 != null && var7.handle(var2)) {
               if (!var7.update()) {
                  ShaderGraphBlock var12 = this.enabled.compute(this.effectScan);
                  if (var12 != null) {
                     var12.process("name", var7.resolve());
                     this.enabled.apply();
                  }

                  this.effectScan = null;
               }

               return true;
            }
         }

         if (this.optionParse != null) {
            ShaderGraphNode var8 = this.playerCollapse.get(this.optionParse);
            if (var8 != null && var8.handle(var2)) {
               if (!var8.update()) {
                  this.prepare(this.optionParse);
                  this.optionParse = null;
               }

               return true;
            }
         }

         if (this.targetWrite) {
            if (var2 == 256) {
               this.worldSend = "";
               this.targetWrite = false;
               this.playerCollect = 0.0F;
               return true;
            }

            if (var2 == 257 || var2 == 335) {
               this.targetWrite = false;
               return true;
            }

            if (var2 == 259) {
               if (!this.worldSend.isEmpty()) {
                  this.worldSend = this.worldSend.substring(0, this.worldSend.length() - 1);
                  this.resultEncode = System.currentTimeMillis();
                  this.playerCollect = 0.0F;
               }

               return true;
            } else {
               return true;
            }
         } else {
            if (var2 == 32) {
               return true;
            }

            if (var2 == 68 && Screen.hasShiftDown()) {
               this.refresh();
               return true;
            }

            if (var2 == 256) {
               if (!this.scaleAdapt && !this.textureRun && !this.indexBind && !this.configCollapse && !this.stateApply) {
                  var1.matchVector(false);
               } else {
                  this.attachEvent();
               }

               return true;
            } else if (var2 != 261 && var2 != 259) {
               if (var2 == 76) {
                  this.entryAnimate = !this.entryAnimate;
                  return true;
               }

               if (Screen.hasControlDown()) {
                  if (var2 == 90) {
                     if (Screen.hasShiftDown()) {
                        this.collectModule();
                     } else {
                        this.checkFrame();
                     }

                     return true;
                  }

                  if (var2 == 89) {
                     this.collectModule();
                     return true;
                  }

                  if (var2 == 83) {
                     this.render();
                     return true;
                  }

                  if (var2 == 80) {
                     this.active.handle(this.rendererScan, this.sourceBuild, null);
                     this.drawAnimation("command");
                     return true;
                  }

                  if (var2 == 67) {
                     this.convertWindow();
                     return true;
                  }

                  if (var2 == 86) {
                     this.writePreset();
                     return true;
                  }

                  if (var2 == 82) {
                     this.closeProvider();
                     return true;
                  }

                  if (var2 == 48) {
                     this.renderer = 520.0F;
                     this.handler = 260.0F;
                     this.pointEncode = 0.92F;
                     this.animator.handle(this.pointEncode);
                     this.drawAnimation("view");
                     return true;
                  }
               }

               return true;
            } else {
               if (!this.vectorPerform.isEmpty()) {
                  this.readServer();
                  ArrayList<String> var9 = new ArrayList<>(this.vectorPerform);
                  boolean var4 = false;

                  for (String var6 : var9) {
                     if (this.enabled.process(var6)) {
                        var4 = true;
                        this.matrixRender.remove(var6);
                        this.moduleTick.remove(var6);
                        this.process(var6);
                        this.bufferAdapt.remove(var6);
                        this.playerUpdate.remove(var6);
                        if (var6.equals(this.optionAdvance)) {
                           this.optionAdvance = null;
                        }

                        if (var6.equals(this.effectScan)) {
                           this.effectScan = null;
                        }

                        if (this.optionParse != null && this.optionParse.startsWith(var6 + ":")) {
                           this.optionParse = null;
                        }
                     }
                  }

                  this.check();
                  if (var4) {
                     this.rendererCancel.handle(1.0F);
                  }

                  this.drawAnimation("deleted");
               }

               return true;
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4) {
      GuiMetrics var5 = var3.update();
      ThemeColors var6 = var3.apply();
      float var7 = var5.handle(34.0F);
      float var8 = var5.handle(28.0F);
      float var9 = var4 - var5.handle(68.0F);
      float var10 = var5.handle(60.0F);
      float var11 = var5.handle(14.0F);
      var1.handle(var7, var8, var9, var10, var11, var5.handle(22.0F), var5.handle(2.0F), this.compute(var6, 132));
      var1.handle(var7, var8, var9, var10, var11, 0.34F);
      var1.handle(var7, var8, var9, var10, var11, this.handle(var6, 226));
      var1.handle(var7, var8, var9, var10, var11, ThemeColors.handle(var6.save(), 52), 0.7F);
      var1.handle(var7 + var5.handle(1.0F), var8 + var5.handle(1.0F), var9 - var5.handle(2.0F), var5.handle(1.0F), var11, ThemeColors.handle(var6.load(), 18));
      var1.process(var7 + var5.handle(20.0F), var8 + var10 * 0.5F, var5.handle(4.0F), 0.0F, 1.0F, ThemeColors.handle(var6.save(), 235));
      ModuleStateHelper.handle(
         var1, var5, FontRegistry.config, var7 + var5.handle(32.0F), var8 + var5.handle(5.0F), var5.handle(24.0F), 13.0F, "Foundry", this.process(var6)
      );
      String var12 = this.current.compute().isBlank() ? "cold" : this.current.compute();
      String var13 = this.current.process();
      String var14 = !var13.isBlank() ? var13 : this.timerRender;
      int var15 = !var13.isBlank() ? ThemeColors.handle(255, 132, 132, 230) : ThemeColors.handle(var6.save(), 210);
      String var16 = this.enabled.handle().compute().isBlank()
         ? "#" + var12 + " / " + this.enabled.compute().size() + " nodes / " + this.enabled.resolve().size() + " links / " + var14
         : "#"
            + var12
            + " / "
            + this.enabled.handle().compute()
            + " / "
            + this.enabled.compute().size()
            + " nodes / "
            + this.enabled.resolve().size()
            + " links / "
            + var14;
      ModuleStateHelper.handle(
         var1,
         var5,
         FontRegistry.instance,
         var7 + var5.handle(32.0F),
         var8 + var5.handle(29.0F),
         var5.handle(18.0F),
         8.0F,
         ModuleStateHelper.handle(var5, FontRegistry.instance, var16, 8.0F, var5.handle(160.0F)),
         var15
      );
      this.handle(var1, var5, var6, this.handle(var5), "File", this.scaleAdapt, var2.sampleLayer(), var2.sendWorld(), 6);
      this.handle(var1, var5, var6, this.process(var5), var2);
      this.handle(var1, var5, var6, this.compute(var5), this.colorCompute.process(), this.textureRun, var2.sampleLayer(), var2.sendWorld(), 1);
      this.handle(var1, var5, var6, this.apply(var5, var4), "Library", this.stateApply, var2.sampleLayer(), var2.sendWorld(), 7);
      this.handle(var1, var5, var6, this.update(var5, var4), this.scaleRender.handle(), this.indexBind, var2.sampleLayer(), var2.sendWorld(), 3);
      this.handle(var1, var5, var6, this.resolve(var5, var4), "Close", false, var2.sampleLayer(), var2.sendWorld(), 4);
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, String var5, boolean var6, float var7, float var8, int var9
   ) {
      float var10 = var4.contains(var7, var8) ? 1.0F : 0.0F;
      float var11 = Math.max(var6 ? 0.82F : 0.0F, var10);
      int var12 = ThemeColors.handle(
         ThemeColors.handle(255, 255, 255, this.handle(var3) ? 70 : 11), ThemeColors.handle(var9 == 2 ? var3.submit() : var3.save(), 86), var11
      );
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), var12);
      var1.handle(
         var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(var3.select(), ThemeColors.handle(var3.save(), 128), var11), 0.7F
      );
      this.handle(var1, var2, var3, var4.x() + var2.handle(14.0F), var4.y() + var4.h() * 0.5F, var9, var11);
      float var13 = var4.x() + var2.handle(28.0F);
      float var14 = var4.w() - var2.handle(36.0F);
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.config,
         var13,
         var4.y(),
         var4.h(),
         9.0F,
         ModuleStateHelper.handle(var2, FontRegistry.config, var5, 9.0F, var14),
         ThemeColors.handle(this.compute(var3), this.process(var3), 0.55F + var11 * 0.45F)
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, int var6, float var7) {
      int var8 = ThemeColors.handle(var6 == 2 ? var3.submit() : var3.save(), Math.round(150.0F + 90.0F * var7));
      float var9 = var2.handle(5.6F);
      if (var6 == 0) {
         var1.handle(var4 - var9, var5 - var9 * 0.65F, var9 * 2.0F, var9 * 1.3F, var2.handle(2.0F), var8);
         var1.handle(var4 - var9 * 0.7F, var5 - var9, var9 * 0.9F, var2.handle(2.0F), var2.handle(1.0F), var8);
      } else if (var6 == 1) {
         var1.process(var4, var5, var9 * 0.9F, 0.0F, 1.0F, ThemeColors.handle(var8, 82));
         var1.process(var4, var5, var9 * 0.38F, 0.0F, 1.0F, var8);
      } else if (var6 == 2) {
         var1.handle(var4 - var9, var5 - var9, var9 * 0.72F, var9 * 0.72F, var2.handle(1.5F), var8);
         var1.handle(var4 + var9 * 0.18F, var5 - var9, var9 * 0.72F, var9 * 0.72F, var2.handle(1.5F), ThemeColors.handle(var8, 170));
         var1.handle(var4 - var9 * 0.42F, var5 + var9 * 0.18F, var9 * 0.72F, var9 * 0.72F, var2.handle(1.5F), ThemeColors.handle(var8, 210));
      } else if (var6 == 3) {
         var1.process(var4, var5, var9 * 0.88F, 0.0F, 1.0F, ThemeColors.handle(var8, 74));
         var1.handle(var4 - var9, var5 - var2.handle(0.8F), var9 * 2.0F, var2.handle(1.6F), var2.handle(1.0F), var8);
         var1.handle(var4 - var2.handle(0.8F), var5 - var9, var2.handle(1.6F), var9 * 2.0F, var2.handle(1.0F), var8);
      } else if (var6 == 5) {
         var1.handle(var4 - var9 * 1.05F, var5 - var9 * 0.78F, var9 * 1.62F, var2.handle(1.5F), var2.handle(1.0F), var8);
         var1.handle(var4 - var9 * 0.62F, var5 - var2.handle(0.75F), var9 * 1.78F, var2.handle(1.5F), var2.handle(1.0F), ThemeColors.handle(var8, 194));
         var1.handle(var4 - var9 * 1.05F, var5 + var9 * 0.78F, var9 * 1.62F, var2.handle(1.5F), var2.handle(1.0F), ThemeColors.handle(var8, 155));
         var1.process(var4 + var9 * 1.05F, var5 - var9 * 0.78F, var2.handle(1.9F), 0.0F, 1.0F, ThemeColors.handle(var8, 210));
         var1.process(var4 - var9 * 1.0F, var5, var2.handle(1.9F), 0.0F, 1.0F, ThemeColors.handle(var8, 170));
         var1.process(var4 + var9 * 0.92F, var5 + var9 * 0.78F, var2.handle(1.9F), 0.0F, 1.0F, var8);
      } else if (var6 == 6) {
         var1.handle(var4 - var9, var5 - var9 * 0.82F, var9 * 0.92F, var2.handle(2.2F), var2.handle(1.0F), var8);
         var1.handle(var4 - var9, var5 - var9 * 0.42F, var9 * 2.0F, var9 * 1.28F, var2.handle(1.6F), ThemeColors.handle(var8, 210));
         var1.handle(var4 - var9 * 0.74F, var5 - var9 * 0.12F, var9 * 1.48F, var2.handle(1.1F), var2.handle(0.5F), ThemeColors.handle(var3.load(), 96));
      } else if (var6 == 7) {
         var1.handle(var4 - var9, var5 - var9, var9 * 0.82F, var9 * 0.82F, var2.handle(1.4F), var8);
         var1.handle(var4 + var9 * 0.18F, var5 - var9, var9 * 0.82F, var9 * 0.82F, var2.handle(1.4F), ThemeColors.handle(var8, 176));
         var1.handle(var4 - var9, var5 + var9 * 0.18F, var9 * 2.0F, var2.handle(1.5F), var2.handle(0.8F), ThemeColors.handle(var8, 214));
         var1.handle(var4 - var9, var5 + var9 * 0.66F, var9 * 1.44F, var2.handle(1.5F), var2.handle(0.8F), ThemeColors.handle(var8, 150));
      } else {
         var1.handle(var4, var5);
         var1.process(45.0F);
         var1.handle(-var9, -var2.handle(0.8F), var9 * 2.0F, var2.handle(1.6F), var2.handle(1.0F), var8);
         var1.execute();
         var1.process(-45.0F);
         var1.handle(-var9, -var2.handle(0.8F), var9 * 2.0F, var2.handle(1.6F), var2.handle(1.0F), var8);
         var1.execute();
         var1.prepare();
      }
   }

   private void handle(String var1, ShaderGraph var2) {
      if (var1 != null && !var1.isBlank() && var2 != null) {
         if (var2 == this.enabled) {
            this.scanRenderer();
         }

         ShaderBuildResult var3 = this.output.handle(var2);
         PresetManager.handle().handle(var1, var2, var3, this.handle(var2));
      }
   }

   private void handle(SavedThemePreset var1) {
      if (var1 != null) {
         LivePreviewRenderer var2 = LivePreviewRenderer.handle(var1.compute());
         if (var2 == LivePreviewRenderer.PREVIEW_ONLY) {
            this.drawAnimation("preview-only slot");
         } else {
            FoundryStorage var3 = FoundryStorage.handle();
            if (var1.handle().equals(var3.process(var2))) {
               this.handle(var2);
            } else {
               ShaderGraph var4 = var3.handle(var1.handle(), this.cache);
               if (var4 == null) {
                  this.drawAnimation("slot load failed");
               } else {
                  var4.handle(var2.handle());
                  ShaderBuildResult var5 = this.output.handle(var4);
                  PresetManager.handle().handle(var1.process(), var4, var5, this.process(var1));
                  PresetManager.handle().handle(var2, var4, var5);
                  ThemeShaderProgramCache.handle().handle(var2, var5);
                  var3.handle(var2, var1.handle());
                  ShaderPresetSetting.handle(var2, var1.process());
                  this.drawAnimation("bound " + var2.process());
               }
            }
         }
      }
   }

   private PresetManager.PrimaryMode handle(ShaderGraph var1) {
      if (var1 != null && var1.handle() != null) {
         String var2 = var1.handle().apply();
         if ("preset".equalsIgnoreCase(var2)) {
            return PresetManager.PrimaryMode.PRESET;
         } else {
            return !"imported".equalsIgnoreCase(var2) && !"shared".equalsIgnoreCase(var2) ? PresetManager.PrimaryMode.USER : PresetManager.PrimaryMode.IMPORTED;
         }
      } else {
         return PresetManager.PrimaryMode.USER;
      }
   }

   private PresetManager.PrimaryMode process(SavedThemePreset var1) {
      if (var1 == null) {
         return PresetManager.PrimaryMode.USER;
      } else {
         String var2 = var1.prepare();
         if ("preset".equalsIgnoreCase(var2)) {
            return PresetManager.PrimaryMode.PRESET;
         } else {
            return !"imported".equalsIgnoreCase(var2) && !"shared".equalsIgnoreCase(var2) ? PresetManager.PrimaryMode.USER : PresetManager.PrimaryMode.IMPORTED;
         }
      }
   }
   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, ModernClickGuiState var5) {
      float var6 = !var4.contains(var5.sampleLayer(), var5.sendWorld()) && !this.animationExpand ? 0.0F : 1.0F;
      int var7 = ThemeColors.handle(ThemeColors.handle(255, 255, 255, this.handle(var3) ? 76 : 14), ThemeColors.handle(var3.save(), 64), var6);
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(7.0F), var7);
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(7.0F),
         ThemeColors.handle(var3.select(), ThemeColors.handle(var3.save(), 134), var6),
         this.animationExpand ? 1.0F : 0.7F
      );
      String var8 = this.screenRead != null && !this.screenRead.isBlank() ? this.screenRead : "Shader name";
      int var9 = this.screenRead != null && !this.screenRead.isBlank() ? this.process(var3) : this.compute(var3);
      var1.compute();
      var1.handle(
         var4.x() + var2.handle(8.0F),
         var4.y(),
         var4.w() - var2.handle(16.0F),
         var4.h(),
         var2.handle(6.0F),
         var2.handle(6.0F),
         var2.handle(6.0F),
         var2.handle(6.0F)
      );
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var4.x() + var2.handle(10.0F), var4.y(), var4.h(), 10.0F, var8, var9);
         if (this.animationExpand) {
            if ((System.currentTimeMillis() - this.playerRun) / 500L % 2L == 0L) {
               float var10 = ModuleStateHelper.handle(var2, FontRegistry.instance, var8, 10.0F);
               float var11 = Math.min(var4.x() + var4.w() - var2.handle(12.0F), var4.x() + var2.handle(10.0F) + var10 + var2.handle(2.0F));
               var1.handle(var11, var4.y() + var2.handle(6.0F), 1.0F, var4.h() - var2.handle(12.0F), 0.0F, ThemeColors.handle(var3.save(), 240));
               var14 = false;
            } else {
               var14 = false;
            }
         } else {
            var14 = false;
         }
      } finally {
         if (var14) {
            var1.compute();
            var1.apply();
         }
      }

      var1.compute();
      var1.apply();
   }

   private void handle(LivePreviewRenderer var1) {
      if (var1 != null) {
         PresetManager.handle().handle(var1);
         ThemeShaderProgramCache.handle().compute(var1);
         FoundryStorage.handle().handle(var1, null);
         ShaderPresetSetting.handle(var1);
         this.drawAnimation(var1.process() + " unbound");
      }
   }
   private void compute(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      float var6 = this.timerMeasure.handle();
      if (this.scaleAdapt || !(var6 <= 0.01F)) {
         GuiMetrics var7 = var3.update();
         ThemeColors var8 = var3.apply();
         RectBounds var9 = this.update(var7);
         var9 = new RectBounds(var9.x(), var9.y() - var7.handle(9.0F) * (1.0F - var6), var9.w(), var9.h());
         float var10 = var7.handle(14.0F);
         var1.update(var6);
         boolean var18 = false /* VF: Semaphore variable */;

         try {
            var18 = true;
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, var7.handle(24.0F), var7.handle(2.0F), this.compute(var8, 142));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, this.handle(var8, 236));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, ThemeColors.handle(var8.save(), 82), 0.8F);
            ModuleStateHelper.handle(
               var1, var7, FontRegistry.config, var9.x() + var7.handle(12.0F), var9.y() + var7.handle(12.0F), 12.0F, "File", this.process(var8)
            );
            ModuleStateHelper.handle(
               var1,
               var7,
               FontRegistry.instance,
               var9.x() + var7.handle(12.0F),
               var9.y() + var7.handle(28.0F),
               8.0F,
               "autosave on / Ctrl+S saves the named slot",
               ThemeColors.handle(var8.save(), 190)
            );
            float var11 = var9.y() + var7.handle(48.0F);
            SavedThemePreset var12 = this.matrixBlend2 == null ? null : FoundryStorage.handle().handle(this.matrixBlend2);
            this.handle(var1, var7, var8, var9.x() + var7.handle(12.0F), var11, "File", var12 == null ? "unsaved" : var12.process());
            this.handle(var1, var7, var8, var9.x() + var7.handle(12.0F), var11 + var7.handle(19.0F), "State", this.execute());
            this.handle(var1, var7, var8, var9.x() + var7.handle(12.0F), var11 + var7.handle(38.0F), "Target", this.colorCompute.process());
            this.handle(
               var1,
               var7,
               var8,
               var9.x() + var7.handle(12.0F),
               var11 + var7.handle(57.0F),
               "Uniforms",
               String.valueOf(this.output.handle(this.enabled).exposedUniforms().size())
            );
            this.handle(var1, var7, var8, var9.x() + var7.handle(12.0F), var11 + var7.handle(76.0F), "Source", this.enabled.handle().apply());
            SavedThemePreset var13 = FoundryStorage.handle().compute(this.colorCompute);
            this.handle(var1, var7, var8, var9.x() + var7.handle(12.0F), var11 + var7.handle(95.0F), "Bound", var13 == null ? "-" : var13.process());

            for (int var14 = 0; var14 < data.length; var14++) {
               RectBounds var15 = this.compute(var9, var7, var14);
               this.handle(var1, var7, var8, var15, data[var14], var2.sampleLayer(), var2.sendWorld(), var14 == 0 || var14 == 1);
            }

            var18 = false;
         } finally {
            if (var18) {
               var1.onTick();
            }
         }

         var1.onTick();
      }
   }

   private void resolve(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      float var6 = this.vectorEncode.handle();
      if (this.textureRun || !(var6 <= 0.01F)) {
         GuiMetrics var7 = var3.update();
         ThemeColors var8 = var3.apply();
         RectBounds var9 = this.resolve(var7, var4, var5);
         var9 = new RectBounds(var9.x(), var9.y() - var7.handle(10.0F) * (1.0F - var6), var9.w(), var9.h());
         float var10 = var7.handle(14.0F);
         var1.update(var6);

         try {
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, var7.handle(24.0F), var7.handle(2.0F), this.compute(var8, 148));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, this.handle(var8, 238));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, ThemeColors.handle(var8.save(), 90), 0.8F);
            ModuleStateHelper.handle(
               var1, var7, FontRegistry.config, var9.x() + var7.handle(16.0F), var9.y() + var7.handle(14.0F), 12.0F, "Target Studio", this.process(var8)
            );
            ModuleStateHelper.handle(
               var1,
               var7,
               FontRegistry.instance,
               var9.x() + var7.handle(16.0F),
               var9.y() + var7.handle(31.0F),
               8.0F,
               "pick where this shader runs — click a target to edit it",
               ThemeColors.handle(var8.save(), 190)
            );
            LivePreviewRenderer[] var11 = LivePreviewRenderer.tick();

            for (int var12 = 0; var12 < var11.length; var12++) {
               this.handle(var1, var7, var8, this.resolve(var9, var7, var12), var11[var12], var2.sampleLayer(), var2.sendWorld());
            }

            float var20 = var9.y() + var9.h() - var7.handle(76.0F);
            ModuleStateHelper.handle(
               var1, var7, FontRegistry.config, var9.x() + var7.handle(16.0F), var20, 9.0F, "Shape Source", ThemeColors.handle(var8.submit(), 220)
            );
            String[] var13 = new String[]{"Host Rectangle", "Inset Shape", "Full Quad"};

            for (int var14 = 0; var14 < var13.length; var14++) {
               RectBounds var15 = this.update(var9, var7, var14);
               this.handle(var1, var7, var8, var15, var13[var14], this.dataValidate.equals(var13[var14]), var2.sampleLayer(), var2.sendWorld());
            }
         } finally {
            var1.onTick();
         }
      }
   }

   private void encodePoint() {
      if (!this.requestAdapt) {
         this.requestAdapt = true;

         for (int var1 = 0; var1 < FoundryTemplateRegistry.instance.size(); var1++) {
            try {
               ShaderGraph var2 = FoundryTemplateRegistry.handle(FoundryTemplateRegistry.instance.get(var1), this.cache);
               this.keyFilter.put(var1, this.output.handle(var2));
            } catch (Throwable var3) {
            }
         }
      }
   }

   private void update(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      float var6 = this.requestReceive.handle();
      if (this.indexBind || !(var6 <= 0.01F)) {
         GuiMetrics var7 = var3.update();
         ThemeColors var8 = var3.apply();
         RectBounds var9 = this.execute(var7, var4);
         var9 = new RectBounds(var9.x() + var7.handle(10.0F) * (1.0F - var6), var9.y(), var9.w(), var9.h());
         float var10 = var7.handle(14.0F);
         var1.update(var6);

         try {
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, var7.handle(22.0F), var7.handle(2.0F), this.compute(var8, 136));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, this.handle(var8, 236));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, ThemeColors.handle(var8.save(), 84), 0.8F);
            ModuleStateHelper.handle(
               var1, var7, FontRegistry.config, var9.x() + var7.handle(16.0F), var9.y() + var7.handle(14.0F), 12.0F, "Settings", this.process(var8)
            );
            ModuleStateHelper.handle(
               var1,
               var7,
               FontRegistry.instance,
               var9.x() + var7.handle(16.0F),
               var9.y() + var7.handle(32.0F),
               8.0F,
               "core editor behavior",
               ThemeColors.handle(var8.save(), 184)
            );
            ModuleStateHelper.handle(
               var1,
               var7,
               FontRegistry.config,
               var9.x() + var7.handle(16.0F),
               var9.y() + var7.handle(64.0F),
               9.0F,
               "Foundry Theme",
               ThemeColors.handle(var8.submit(), 220)
            );
            FoundryWorkspace.Mode[] var11 = FoundryWorkspace.Mode.values();

            for (int var12 = 0; var12 < var11.length; var12++) {
               this.handle(
                  var1,
                  var7,
                  var8,
                  this.apply(var9, var7, var12),
                  var11[var12].handle(),
                  this.scaleRender == var11[var12],
                  var2.sampleLayer(),
                  var2.sendWorld()
               );
            }

            ModuleStateHelper.handle(
               var1,
               var7,
               FontRegistry.config,
               var9.x() + var7.handle(16.0F),
               var9.y() + var7.handle(118.0F),
               9.0F,
               "Shader Properties",
               ThemeColors.handle(var8.submit(), 220)
            );
            this.handle(var1, var7, var8, var9.x() + var7.handle(16.0F), var9.y() + var7.handle(140.0F), "Complexity", this.enabled.handle().update());
            this.handle(
               var1,
               var7,
               var8,
               var9.x() + var7.handle(16.0F),
               var9.y() + var7.handle(162.0F),
               "Uniforms",
               String.valueOf(this.output.handle(this.enabled).exposedUniforms().size())
            );
            this.handle(var1, var7, var8, var9.x() + var7.handle(16.0F), var9.y() + var7.handle(184.0F), "Shape", this.dataValidate);
         } finally {
            var1.onTick();
         }
      }
   }
   private void apply(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      float var6 = this.windowProcess.handle();
      if ((this.configCollapse || !(var6 <= 0.01F)) && this.actionRead != null) {
         GuiMetrics var7 = var3.update();
         ThemeColors var8 = var3.apply();
         RectBounds var9 = this.update(var7, var4, var5);
         float var10 = var7.handle(14.0F);
         var1.update(var6);
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            var13 = true;
            var1.handle(0.0F, 0.0F, var4, var5, 0.0F, ThemeColors.handle(0, 0, 0, Math.round((this.handle(var8) ? 42 : 82) * var6)));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, var7.handle(26.0F), var7.handle(2.0F), this.compute(var8, 172));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, this.handle(var8, 248));
            var1.handle(var9.x(), var9.y(), var9.w(), var9.h(), var10, ThemeColors.handle(var8.save(), 128), 0.9F);
            ModuleStateHelper.handle(
               var1, var7, FontRegistry.config, var9.x() + var7.handle(18.0F), var9.y() + var7.handle(16.0F), 12.0F, "Switch Target", this.process(var8)
            );
            ModuleStateHelper.handle(
               var1,
               var7,
               FontRegistry.instance,
               var9.x() + var7.handle(18.0F),
               var9.y() + var7.handle(38.0F),
               9.0F,
               "Current graph has unsaved changes. Save before switching to " + this.actionRead.process() + ".",
               this.compute(var8)
            );
            this.handle(var1, var7, var8, this.apply(var9, var7), "Save & Switch", var2.sampleLayer(), var2.sendWorld(), true);
            this.handle(var1, var7, var8, this.execute(var9, var7), "Switch", var2.sampleLayer(), var2.sendWorld(), false);
            this.handle(var1, var7, var8, this.prepare(var9, var7), "Cancel", var2.sampleLayer(), var2.sendWorld(), false);
            var13 = false;
         } finally {
            if (var13) {
               var1.onTick();
            }
         }

         var1.onTick();
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, LivePreviewRenderer var5, float var6, float var7) {
      boolean var8 = var4.contains(var6, var7);
      boolean var9 = var5 == this.colorCompute;
      SavedThemePreset var10 = FoundryStorage.handle().compute(var5);
      boolean var11 = PresetManager.handle().update(var5);
      float var12 = Math.max(var9 ? 0.82F : 0.0F, var8 ? 0.7F : 0.0F);
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(8.0F),
         ThemeColors.handle(ThemeColors.handle(255, 255, 255, this.handle(var3) ? 52 : 8), ThemeColors.handle(var3.save(), 74), var12)
      );
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(8.0F),
         ThemeColors.handle(var3.select(), ThemeColors.handle(var9 ? var3.save() : var3.submit(), var9 ? 150 : 96), var12),
         var9 ? 0.9F : 0.6F
      );
      int var13 = var9 ? var3.save() : ThemeColors.handle(120, 230, 150, 255);
      var1.process(var4.x() + var2.handle(15.0F), var4.y() + var2.handle(15.0F), var2.handle(3.1F), 0.0F, 1.0F, var13);
      if (var9) {
         var1.handle(var4.x() + var2.handle(15.0F), var4.y() + var2.handle(15.0F), var2.handle(5.4F), 0.0F, 1.0F, 0.9F, ThemeColors.handle(var3.save(), 150));
      }

      ModuleStateHelper.handle(
         var1, var2, FontRegistry.config, var4.x() + var2.handle(26.0F), var4.y() + var2.handle(8.0F), 10.0F, var5.process(), this.process(var3)
      );
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.instance,
         var4.x() + var2.handle(26.0F),
         var4.y() + var2.handle(24.0F),
         7.5F,
         ModuleStateHelper.handle(var2, FontRegistry.instance, this.process(var5), 7.5F, var4.w() - var2.handle(96.0F)),
         this.compute(var3)
      );
      if (var11) {
         String var14 = var10 == null ? "runtime" : var10.process();
         RectBounds var15 = this.handle(var4, var2);
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.instance,
            var4.x() + var4.w() - var2.handle(88.0F),
            var4.y() + var2.handle(25.0F),
            7.0F,
            ModuleStateHelper.handle(var2, FontRegistry.instance, "◆ " + var14, 7.0F, var2.handle(44.0F)),
            ThemeColors.handle(var3.submit(), 210)
         );
         boolean var16 = var15.contains(var6, var7);
         var1.handle(
            var15.x(),
            var15.y(),
            var15.w(),
            var15.h(),
            var2.handle(5.0F),
            ThemeColors.handle(ThemeColors.handle(var3.load(), 24), ThemeColors.handle(220, 80, 92, 126), var16 ? 1.0F : 0.0F)
         );
         var1.handle(var15.x(), var15.y(), var15.w(), var15.h(), var2.handle(5.0F), ThemeColors.handle(var16 ? -33652 : var3.load(), var16 ? 220 : 72), 0.58F);
         float var17 = ModuleStateHelper.handle(var2, FontRegistry.config, "Off", 8.0F);
         ModuleStateHelper.handle(
            var1, var2, FontRegistry.config, var15.x() + (var15.w() - var17) * 0.5F, var15.y(), var15.h(), 8.0F, "Off", var16 ? var3.load() : var3.animate()
         );
      }
   }

   private RectBounds handle(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var1.w() - var2.handle(44.0F), var1.y() + var1.h() - var2.handle(20.0F), var2.handle(36.0F), var2.handle(15.0F));
   }

   private String process(LivePreviewRenderer var1) {
      return switch (var1) {
         case HUD -> "Drives HUD element plates";
         case BACKGROUND -> "Drives the ClickGUI background";
         case ESP -> "Drives the TargetESP entity fill";
         default -> var1.compute();
      };
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, String var6, String var7) {
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var4, var5, 8.0F, var6, var3.animate());
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.config,
         var4 + var2.handle(82.0F),
         var5 - var2.handle(1.0F),
         9.0F,
         var7 != null && !var7.isBlank() ? var7 : "-",
         this.process(var3)
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, String var5, float var6, float var7, boolean var8) {
      float var9 = var4.contains(var6, var7) ? 1.0F : 0.0F;
      int var10 = ThemeColors.handle(255, 255, 255, this.handle(var3) ? 72 : 12);
      int var11 = ThemeColors.handle(var8 ? var3.save() : var3.submit(), 88);
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(var10, var11, var9));
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(8.0F),
         ThemeColors.handle(var3.select(), ThemeColors.handle(var8 ? var3.save() : var3.submit(), 122), var9),
         0.7F
      );
      int var12 = this.apply(var5);
      if (var12 >= 0 && var4.w() > var2.handle(78.0F)) {
         this.handle(var1, var2, var3, var4.x() + var2.handle(14.0F), var4.y() + var4.h() * 0.5F, var12, var8, var9);
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.config,
            var4.x() + var2.handle(28.0F),
            var4.y(),
            var4.h(),
            9.0F,
            ModuleStateHelper.handle(var2, FontRegistry.config, var5, 9.0F, var4.w() - var2.handle(36.0F)),
            this.process(var3)
         );
      } else {
         float var13 = ModuleStateHelper.handle(var2, FontRegistry.config, var5, 9.0F);
         ModuleStateHelper.handle(var1, var2, FontRegistry.config, var4.x() + (var4.w() - var13) * 0.5F, var4.y(), var4.h(), 9.0F, var5, this.process(var3));
      }
   }

   private int apply(String var1) {
      if (var1 == null) {
         return -1;
      } else if (var1.startsWith("Save")) {
         return 0;
      } else if (var1.startsWith("Slots")) {
         return 1;
      } else if (var1.startsWith("Export")) {
         return 2;
      } else if (var1.startsWith("Import")) {
         return 3;
      } else if (var1.startsWith("Open")) {
         return 4;
      } else if (var1.startsWith("Reset")) {
         return 5;
      } else if (var1.startsWith("Use")) {
         return 6;
      } else if (var1.startsWith("Merge")) {
         return 7;
      } else if (var1.startsWith("Cleanup")) {
         return 8;
      } else if (var1.startsWith("Switch")) {
         return 9;
      } else {
         return var1.startsWith("Cancel") ? 10 : -1;
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, int var6, boolean var7, float var8) {
      int var9 = ThemeColors.handle(var7 ? var3.save() : var3.submit(), Math.round(150.0F + 90.0F * var8));
      float var10 = var2.handle(5.2F);
      if (var6 == 0) {
         var1.handle(var4 - var10, var5 - var10, var10 * 2.0F, var10 * 2.0F, var2.handle(1.8F), var9);
         var1.handle(var4 - var10 * 0.58F, var5 + var10 * 0.1F, var10 * 1.16F, var10 * 0.52F, var2.handle(1.0F), ThemeColors.handle(var3.load(), 115));
      } else if (var6 == 1) {
         var1.handle(var4 - var10, var5 - var10, var10 * 0.78F, var10 * 0.78F, var2.handle(1.6F), var9);
         var1.handle(var4 + var10 * 0.22F, var5 - var10, var10 * 0.78F, var10 * 0.78F, var2.handle(1.6F), ThemeColors.handle(var9, 160));
         var1.handle(var4 - var10, var5 + var10 * 0.22F, var10 * 0.78F, var10 * 0.78F, var2.handle(1.6F), ThemeColors.handle(var9, 200));
      } else if (var6 == 2 || var6 == 3) {
         float var11 = var6 == 2 ? -1.0F : 1.0F;
         var1.handle(var4 - var2.handle(0.8F), var5 - var10 * 0.65F, var2.handle(1.6F), var10 * 1.3F, var2.handle(1.0F), var9);
         var1.handle(var4 - var10 * 0.72F, var5 + var11 * var10 * 0.55F, var10 * 1.44F, var2.handle(1.5F), var2.handle(1.0F), var9);
         var1.handle(var4 - var10, var5 - var11 * var10 * 0.95F, var10 * 2.0F, var2.handle(1.5F), var2.handle(1.0F), ThemeColors.handle(var9, 140));
      } else if (var6 == 4) {
         this.handle(var1, var2, var3, var4, var5, 0, var8);
      } else if (var6 == 5) {
         var1.process(var4, var5, var10, 0.0F, 0.82F, ThemeColors.handle(var9, 90));
         var1.handle(var4 + var10 * 0.2F, var5 - var10 * 0.9F, var10 * 0.78F, var2.handle(1.4F), var2.handle(1.0F), var9);
      } else if (var6 == 8) {
         var1.handle(var4 - var10 * 0.5F, var5 - var10 * 0.32F, var10, var10 * 0.92F, var2.handle(1.4F), var9, 0.8F);
         var1.handle(var4 - var10 * 0.68F, var5 - var10 * 0.56F, var10 * 1.36F, var2.handle(1.3F), var2.handle(0.8F), var9);
         var1.handle(var4 - var10 * 0.22F, var5 - var10 * 0.82F, var10 * 0.44F, var2.handle(1.3F), var2.handle(0.8F), var9);
         var1.handle(var4 - var2.handle(0.6F), var5 - var10 * 0.1F, var2.handle(1.2F), var10 * 0.5F, var2.handle(0.5F), ThemeColors.handle(var9, 170));
      } else if (var6 == 9) {
         var1.process(var4, var5, var10 * 0.9F, 0.0F, 1.0F, ThemeColors.handle(var9, 82));
         var1.process(var4, var5, var10 * 0.38F, 0.0F, 1.0F, var9);
      } else if (var6 == 10) {
         var1.handle(var4, var5);
         var1.process(45.0F);
         var1.handle(-var10 * 0.8F, -var2.handle(0.8F), var10 * 1.6F, var2.handle(1.6F), var2.handle(1.0F), var9);
         var1.execute();
         var1.process(-45.0F);
         var1.handle(-var10 * 0.8F, -var2.handle(0.8F), var10 * 1.6F, var2.handle(1.6F), var2.handle(1.0F), var9);
         var1.execute();
         var1.prepare();
      } else {
         var1.process(var4, var5, var10, 0.0F, 1.0F, ThemeColors.handle(var9, var6 == 6 ? 165 : 92));
         var1.process(var4, var5, var10 * 0.38F, 0.0F, 1.0F, var9);
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, String var5, boolean var6, float var7, float var8) {
      float var9 = var4.contains(var7, var8) ? 1.0F : 0.0F;
      float var10 = Math.max(var6 ? 0.84F : 0.0F, var9);
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var2.handle(8.0F),
         ThemeColors.handle(ThemeColors.handle(255, 255, 255, this.handle(var3) ? 64 : 10), ThemeColors.handle(var3.save(), 92), var10)
      );
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(var6 ? var3.save() : var3.load(), var6 ? 150 : 42), 0.65F);
      float var11 = ModuleStateHelper.handle(var2, FontRegistry.instance, var5, 8.0F);
      ModuleStateHelper.handle(
         var1,
         var2,
         FontRegistry.instance,
         var4.x() + (var4.w() - var11) * 0.5F,
         var4.y(),
         var4.h(),
         8.0F,
         var5,
         var6 ? this.process(var3) : this.compute(var3)
      );
   }

   private void execute(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      GuiMetrics var6 = var3.update();
      ThemeColors var7 = var3.apply();
      RectBounds var8 = this.compute(var6, var5);
      boolean var9 = var8.contains(var2.sampleLayer(), var2.sendWorld());
      var1.handle(
         var8.x(),
         var8.y(),
         var8.w(),
         var8.h(),
         var6.handle(7.0F),
         ThemeColors.handle(ThemeColors.handle(255, 255, 255, 8), ThemeColors.handle(var7.save(), 64), var9 ? 1.0F : 0.0F)
      );
      var1.handle(var8.x(), var8.y(), var8.w(), var8.h(), var6.handle(7.0F), ThemeColors.handle(var7.save(), 96), 0.7F);
      String var10 = this.entryAnimate ? ">" : "<";
      float var11 = ModuleStateHelper.handle(var6, FontRegistry.config, var10, 10.0F);
      ModuleStateHelper.handle(var1, var6, FontRegistry.config, var8.x() + (var8.w() - var11) * 0.5F, var8.y(), var8.h(), 10.0F, var10, var7.load());
      if (!this.entryAnimate) {
         RectBounds var12 = this.process(var6, var5);
         float var13 = var6.handle(14.0F);
         var1.handle(var12.x(), var12.y(), var12.w(), var12.h(), var13, var6.handle(18.0F), var6.handle(2.0F), this.compute(var7, 118));
         var1.handle(var12.x(), var12.y(), var12.w(), var12.h(), var13, this.process(var7, 220));
         var1.handle(var12.x(), var12.y(), var12.w(), var12.h(), var13, var7.select(), 0.7F);
         ModuleStateHelper.handle(
            var1, var6, FontRegistry.config, var12.x() + var6.handle(15.0F), var12.y() + var6.handle(14.0F), 12.0F, "Node Library", var7.load()
         );
         ModuleStateHelper.handle(
            var1,
            var6,
            FontRegistry.instance,
            var12.x() + var6.handle(15.0F),
            var12.y() + var6.handle(28.0F),
            8.0F,
            "click to spawn / RMB opens search",
            ThemeColors.handle(var7.save(), 156)
         );
         this.handle(var1, var6, var7, this.handle(var6, var5));
         List<FoundryWorkspace.PersistentDataRecord> var14 = this.animate();
         float var15 = var12.y() + var6.handle(74.0F);
         float var16 = var12.y() + var12.h() - var6.handle(14.0F);
         float var17 = Math.max(1.0F, var16 - var15);
         float var18 = this.handle(var6, var14);
         this.playerCollect = process(this.playerCollect, 0.0F, Math.max(0.0F, var18 - var17));
         FavoritesStorage var19 = FavoritesStorage.handle();
         var1.compute();
         var1.handle(
            var12.x() + var6.handle(8.0F),
            var15,
            var12.w() - var6.handle(16.0F),
            var17,
            var6.handle(8.0F),
            var6.handle(8.0F),
            var6.handle(8.0F),
            var6.handle(8.0F)
         );

         try {
            float var20 = var15 - this.playerCollect;

            for (FoundryWorkspace.PersistentDataRecord var22 : var14) {
               if (var22.type() == 0) {
                  float var37 = var6.handle(20.0F);
                  if (var20 + var37 >= var15 && var20 <= var16) {
                     boolean var39 = var2.sampleLayer() >= var12.x() + var6.handle(8.0F)
                        && var2.sampleLayer() < var12.x() + var12.w() - var6.handle(8.0F)
                        && var2.sendWorld() >= var20
                        && var2.sendWorld() < var20 + var37;
                     boolean var41 = this.messageParse.contains(var22.category());
                     if (var39) {
                        var1.handle(
                           var12.x() + var6.handle(8.0F),
                           var20,
                           var12.w() - var6.handle(16.0F),
                           var37,
                           var6.handle(6.0F),
                           ThemeColors.handle(var7.submit(), 26)
                        );
                     }

                     ModuleStateHelper.handle(
                        var1,
                        var6,
                        FontRegistry.config,
                        var12.x() + var6.handle(14.0F),
                        var20,
                        var37,
                        8.0F,
                        (var41 ? "▸ " : "▾ ") + var22.category().toUpperCase(Locale.ROOT),
                        ThemeColors.handle(var7.submit(), var39 ? 245 : 210)
                     );
                     String var43 = String.valueOf(var22.count());
                     float var45 = ModuleStateHelper.handle(var6, FontRegistry.instance, var43, 8.0F);
                     ModuleStateHelper.handle(
                        var1,
                        var6,
                        FontRegistry.instance,
                        var12.x() + var12.w() - var6.handle(18.0F) - var45,
                        var20,
                        var37,
                        8.0F,
                        var43,
                        ThemeColors.handle(var7.save(), var39 ? 210 : 140)
                     );
                  }

                  var20 += var6.handle(22.0F);
               } else {
                  ShaderNodeDefinition var23 = var22.def();
                  float var24 = var6.handle(24.0F);
                  if (var20 + var24 >= var15 && var20 <= var16) {
                     boolean var25 = var2.sampleLayer() >= var12.x() + var6.handle(8.0F)
                        && var2.sampleLayer() < var12.x() + var12.w() - var6.handle(8.0F)
                        && var2.sendWorld() >= var20
                        && var2.sendWorld() < var20 + var24;
                     float var26 = var25 ? 1.0F : 0.0F;
                     boolean var27 = var19.handle(var23.handle());
                     var1.handle(
                        var12.x() + var6.handle(8.0F),
                        var20,
                        var12.w() - var6.handle(16.0F),
                        var24,
                        var6.handle(7.0F),
                        ThemeColors.handle(ThemeColors.handle(255, 255, 255, 4), ThemeColors.handle(var7.save(), 54), var26)
                     );
                     var1.process(
                        var12.x() + var6.handle(19.0F),
                        var20 + var24 * 0.5F,
                        var6.handle(2.6F),
                        0.0F,
                        1.0F,
                        this.handle(var23.apply().isEmpty() ? null : var23.apply().get(0), var7)
                     );
                     ModuleStateHelper.handle(
                        var1,
                        var6,
                        FontRegistry.instance,
                        var12.x() + var6.handle(31.0F),
                        var20,
                        var24,
                        9.0F,
                        ModuleStateHelper.handle(var6, FontRegistry.instance, var23.process(), 9.0F, var12.w() - var6.handle(112.0F)),
                        var25 ? var7.load() : var7.animate()
                     );
                     String var28 = var23.apply().isEmpty() ? "out" : var23.apply().get(0).type().handle();
                     ModuleStateHelper.handle(
                        var1,
                        var6,
                        FontRegistry.instance,
                        var12.x() + var12.w() - var6.handle(72.0F),
                        var20,
                        var24,
                        8.0F,
                        var28,
                        ThemeColors.handle(var7.save(), var25 ? 230 : 150)
                     );
                     if (var27 || var25) {
                        RectBounds var29 = this.handle(var12, var6, var20, var24);
                        boolean var30 = var29.contains(var2.sampleLayer(), var2.sendWorld());
                        this.handle(var1, var6, var7, var29.x() + var29.w() * 0.5F, var29.y() + var29.h() * 0.5F, var27, var30 ? 1.0F : (var27 ? 0.8F : 0.35F));
                     }
                  }

                  var20 += var6.handle(26.0F);
               }
            }

            if (var14.isEmpty()) {
               ModuleStateHelper.handle(
                  var1, var6, FontRegistry.instance, var12.x() + var6.handle(16.0F), var15 + var6.handle(10.0F), 9.0F, "no matching nodes", var7.animate()
               );
            }
         } finally {
            var1.compute();
            var1.apply();
         }

         if (var18 > var17 + 1.0F) {
            float var34 = var12.x() + var12.w() - var6.handle(8.0F);
            float var35 = var15 + var6.handle(2.0F);
            float var36 = var17 - var6.handle(4.0F);
            float var38 = Math.max(var6.handle(34.0F), var36 * var17 / var18);
            float var40 = Math.max(1.0F, var18 - var17);
            float var42 = var35 + (var36 - var38) * (this.playerCollect / var40);
            float var44 = SurfaceInteractionRouter.handle(
               7101L,
               var34 - var6.handle(3.0F),
               var35,
               var6.handle(8.0F),
               var36,
               var42,
               var38,
               var6.handle(6.0F),
               var2.sampleLayer(),
               var2.sendWorld(),
               var2x -> this.playerCollect = process(var2x, 0.0F, 1.0F) * var40
            );
            float var46 = var6.handle(2.0F) + var6.handle(2.0F) * var44;
            var1.handle(var34, var35, var6.handle(2.0F), var36, var6.handle(1.0F), var7.check());
            var1.handle(
               var34 + var6.handle(2.0F) - var46, var42, var46, var38, var6.handle(1.5F), ThemeColors.handle(var7.save(), (int)(142.0F + 90.0F * var44))
            );
         }
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4) {
      float var5 = this.targetWrite ? 1.0F : 0.0F;
      float var6 = var2.handle(7.0F);
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var6, ThemeColors.handle(255, 255, 255, this.handle(var3) ? 78 : 12));
      var1.handle(
         var4.x(),
         var4.y(),
         var4.w(),
         var4.h(),
         var6,
         ThemeColors.handle(var3.select(), ThemeColors.handle(var3.save(), 176), Math.max(var5, this.worldSend.isEmpty() ? 0.0F : 0.5F)),
         this.targetWrite ? 1.0F : 0.65F
      );
      float var7 = var4.x() + var2.handle(11.0F);
      float var8 = var4.y() + var4.h() * 0.5F - var2.handle(1.0F);
      var1.handle(var7, var8, var2.handle(3.2F), 0.0F, 1.0F, 1.2F, ThemeColors.handle(var3.save(), 220));
      var1.handle(var7 + var2.handle(2.4F), var8 + var2.handle(2.4F), var2.handle(3.8F), 1.2F, 0.6F, ThemeColors.handle(var3.save(), 220));
      float var9 = var4.x() + var2.handle(21.0F);
      String var10 = this.worldSend.isEmpty() ? "Search nodes…" : this.worldSend;
      int var11 = this.worldSend.isEmpty() ? this.compute(var3) : this.process(var3);
      var1.compute();
      var1.handle(var4.x() + var2.handle(4.0F), var4.y(), var4.w() - var2.handle(8.0F), var4.h(), var6, var6, var6, var6);

      try {
         ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var9, var4.y(), var4.h(), 9.0F, var10, var11);
         if (this.targetWrite && (System.currentTimeMillis() - this.resultEncode) / 500L % 2L == 0L) {
            float var12 = var9
               + (this.worldSend.isEmpty() ? 0.0F : ModuleStateHelper.handle(var2, FontRegistry.instance, this.worldSend, 9.0F) + var2.handle(1.5F));
            var1.handle(
               Math.min(var12, var4.x() + var4.w() - var2.handle(8.0F)),
               var4.y() + var2.handle(4.5F),
               1.0F,
               var4.h() - var2.handle(9.0F),
               0.0F,
               ThemeColors.handle(var3.save(), 240)
            );
         }
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, boolean var6, float var7) {
      int var8 = ThemeColors.handle(var3.submit(), Math.round(120.0F + 130.0F * var7));
      float var9 = var2.handle(2.9F);
      if (var6) {
         var1.handle(var4 - var9, var5 - var9, var9 * 2.0F, var9 * 2.0F, var2.handle(1.0F), var8);
         var1.handle(var4, var5);
         var1.process(45.0F);
         var1.handle(-var9, -var9, var9 * 2.0F, var9 * 2.0F, var2.handle(1.0F), ThemeColors.handle(var8, 210));
         var1.execute();
         var1.prepare();
         var1.process(var4, var5, var2.handle(1.4F), 0.0F, 1.0F, ThemeColors.handle(var3.load(), 200));
      } else {
         var1.handle(var4 - var9, var5 - var9, var9 * 2.0F, var9 * 2.0F, var2.handle(1.0F), var8, 0.7F);
         var1.handle(var4, var5);
         var1.process(45.0F);
         var1.handle(-var9, -var9, var9 * 2.0F, var9 * 2.0F, var2.handle(1.0F), ThemeColors.handle(var8, 150), 0.7F);
         var1.execute();
         var1.prepare();
      }
   }

   private RectBounds handle(GuiMetrics var1, int var2) {
      RectBounds var3 = this.process(var1, var2);
      return new RectBounds(var3.x() + var1.handle(10.0F), var3.y() + var1.handle(42.0F), var3.w() - var1.handle(20.0F), var1.handle(22.0F));
   }

   private RectBounds handle(RectBounds var1, GuiMetrics var2, float var3, float var4) {
      return new RectBounds(var1.x() + var1.w() - var2.handle(30.0F), var3 + (var4 - var2.handle(16.0F)) * 0.5F, var2.handle(16.0F), var2.handle(16.0F));
   }

   private List<FoundryWorkspace.PersistentDataRecord> animate() {
      ArrayList var1 = new ArrayList();
      String var2 = this.worldSend == null ? "" : this.worldSend.toLowerCase(Locale.ROOT).trim();
      if (var2.isEmpty()) {
         FavoritesStorage var10 = FavoritesStorage.handle();
         ArrayList var12 = new ArrayList();

         for (String var16 : var10.process()) {
            ShaderNodeDefinition var7 = this.cache.handle(var16);
            if (var7 != null) {
               var12.add(var7);
            }
         }

         this.handle(var1, "Избранное", var12);
         ArrayList var15 = new ArrayList();

         for (String var19 : var10.compute()) {
            ShaderNodeDefinition var8 = this.cache.handle(var19);
            if (var8 != null) {
               var15.add(var8);
            }
         }

         this.handle(var1, "Недавние", var15);
         String var18 = null;
         ArrayList var20 = new ArrayList();

         for (ShaderNodeDefinition var9 : this.matchVector()) {
            if (!var9.compute().equals(var18)) {
               if (var18 != null) {
                  this.handle(var1, var18, var20);
               }

               var18 = var9.compute();
               var20 = new ArrayList();
            }

            var20.add(var9);
         }

         if (var18 != null) {
            this.handle(var1, var18, var20);
         }

         return var1;
      } else {
         ArrayList<FuzzyToastMatcher.DataRecord> var3 = new ArrayList<>();

         for (ShaderNodeDefinition var5 : this.cache.handle()) {
            FuzzyToastMatcher.DataRecord var6 = FuzzyToastMatcher.handle(var5, var2);
            if (var6 != null) {
               var3.add(var6);
            }
         }

         var3.sort(Comparator.<FuzzyToastMatcher.DataRecord>comparingInt(var0 -> -var0.score()).thenComparing(var0 -> var0.def().process()));

         for (FuzzyToastMatcher.DataRecord var13 : var3) {
            var1.add(new FoundryWorkspace.PersistentDataRecord(1, var13.def().compute(), var13.def(), 0));
         }

         return var1;
      }
   }

   private void handle(List<FoundryWorkspace.PersistentDataRecord> var1, String var2, List<ShaderNodeDefinition> var3) {
      if (!var3.isEmpty()) {
         var1.add(new FoundryWorkspace.PersistentDataRecord(0, var2, null, var3.size()));
         if (!this.messageParse.contains(var2)) {
            for (ShaderNodeDefinition var5 : var3) {
               var1.add(new FoundryWorkspace.PersistentDataRecord(1, var2, var5, 0));
            }
         }
      }
   }

   private float handle(GuiMetrics var1, List<FoundryWorkspace.PersistentDataRecord> var2) {
      float var3 = 0.0F;

      for (FoundryWorkspace.PersistentDataRecord var5 : var2) {
         var3 += var5.type() == 0 ? var1.handle(22.0F) : var1.handle(26.0F);
      }

      return var3;
   }

   private FoundryWorkspace.CachedDataRecord handle(GuiMetrics var1, int var2, float var3, float var4) {
      RectBounds var5 = this.process(var1, var2);
      float var6 = var5.y() + var1.handle(74.0F);
      float var7 = var5.y() + var5.h() - var1.handle(14.0F);
      if (!(var4 < var6) && !(var4 > var7) && !(var3 < var5.x() + var1.handle(8.0F)) && !(var3 >= var5.x() + var5.w() - var1.handle(8.0F))) {
         float var8 = var6 - this.playerCollect;

         for (FoundryWorkspace.PersistentDataRecord var10 : this.animate()) {
            if (var10.type() == 0) {
               if (var4 >= var8 && var4 < var8 + var1.handle(20.0F)) {
                  return new FoundryWorkspace.CachedDataRecord(var10, false);
               }

               var8 += var1.handle(22.0F);
            } else {
               float var11 = var1.handle(24.0F);
               if (var4 >= var8 && var4 < var8 + var11) {
                  boolean var12 = this.handle(var5, var1, var8, var11).contains(var3, var4);
                  return new FoundryWorkspace.CachedDataRecord(var10, var12);
               }

               var8 += var1.handle(26.0F);
               if (var8 > var7 + var1.handle(26.0F)) {
                  break;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5, float var6) {
      GuiMetrics var7 = var3.update();
      ThemeColors var8 = var3.apply();
      RectBounds var9 = this.process(var7, var4, var5);
      float var10 = var9.w();
      float var11 = var9.h();
      float var12 = var9.x();
      float var13 = var9.y();
      float var14 = var7.handle(15.0F);
      var1.handle(var12, var13, var10, var11, var14, var7.handle(22.0F), var7.handle(2.0F), this.compute(var8, 138));
      var1.handle(var12, var13, var10, var11, var14, this.process(var8, 210));
      var1.handle(var12, var13, var10, var11, var14, ThemeColors.handle(var8.submit(), 56), 0.8F);
      float var15 = var7.handle(12.0F);
      float var16 = var11 - var7.handle(50.0F);
      float var17 = var10 - var15 * 2.0F;
      float var18 = var12 + var15;
      float var19 = var13 + var7.handle(38.0F);
      FoundryEntityPreviewRenderer.handle(
         var1, var3, this.colorCompute, this.current, this.enabled, var18, var19, var17, var16, var4, var5, var2.sampleLayer(), var2.sendWorld(), var6
      );
      String var20 = this.current.process();
      float var21 = var7.handle(9.0F);
      if (!var20.isBlank()) {
         var1.handle(var18, var19, var17, var16, var21, ThemeColors.handle(8, 4, 6, Math.round(150.0F * var6)));
         var1.handle(var18, var19, var17, var16, var21, ThemeColors.handle(255, 110, 124, Math.round(142.0F * var6)), 0.7F);
         String var22 = "compile failed";
         float var23 = ModuleStateHelper.handle(var7, FontRegistry.config, var22, 10.0F);
         ModuleStateHelper.handle(
            var1,
            var7,
            FontRegistry.config,
            var18 + (var17 - var23) * 0.5F,
            var19 + var16 * 0.5F - var7.handle(17.0F),
            var7.handle(14.0F),
            10.0F,
            var22,
            ThemeColors.handle(255, 132, 132, 240)
         );
         String var24 = ModuleStateHelper.handle(var7, FontRegistry.instance, var20, 8.0F, var17 - var7.handle(24.0F));
         float var25 = ModuleStateHelper.handle(var7, FontRegistry.instance, var24, 8.0F);
         ModuleStateHelper.handle(
            var1,
            var7,
            FontRegistry.instance,
            var18 + (var17 - var25) * 0.5F,
            var19 + var16 * 0.5F + var7.handle(1.0F),
            var7.handle(12.0F),
            8.0F,
            var24,
            ThemeColors.handle(255, 182, 188, 218)
         );
      } else if (this.current.compute().isBlank()) {
         var1.process(
            var18,
            var19,
            var17,
            var16,
            var21,
            ThemeColors.handle(var8.save(), Math.round(52.0F * var6)),
            ThemeColors.handle(var8.submit(), Math.round(30.0F * var6))
         );
         var1.handle(var18, var19, var17, var16, var21, ThemeColors.handle(var8.save(), Math.round(74.0F * var6)), 0.6F);
         String var26 = "connect Master Output to see the result";
         float var27 = ModuleStateHelper.handle(var7, FontRegistry.instance, var26, 9.0F);
         ModuleStateHelper.handle(
            var1,
            var7,
            FontRegistry.instance,
            var18 + (var17 - var27) * 0.5F,
            var19 + var16 * 0.5F - var7.handle(7.0F),
            var7.handle(14.0F),
            9.0F,
            var26,
            this.compute(var8)
         );
      }

      ModuleStateHelper.handle(var1, var7, FontRegistry.config, var12 + var15, var13 + var7.handle(12.0F), 11.0F, "Master Preview", var8.load());
      ModuleStateHelper.handle(
         var1,
         var7,
         FontRegistry.instance,
         var12 + var15 + var7.handle(108.0F),
         var13 + var7.handle(14.0F),
         9.0F,
         this.colorCompute.process(),
         ThemeColors.handle(var8.submit(), 220)
      );
   }

   private void prepare(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, int var4, int var5) {
      GuiMetrics var6 = var3.update();
      ThemeColors var7 = var3.apply();
      RectBounds var8 = this.handle(var6, var4, var5);
      float var9 = var6.handle(14.0F);
      var1.handle(var8.x(), var8.y(), var8.w(), var8.h(), var9, var6.handle(20.0F), var6.handle(2.0F), this.compute(var7, 132));
      var1.handle(var8.x(), var8.y(), var8.w(), var8.h(), var9, this.handle(var7, 214));
      var1.handle(var8.x(), var8.y(), var8.w(), var8.h(), var9, ThemeColors.handle(var7.save(), 58), 0.7F);
      ShaderGraphBlock var10 = this.projectItem();
      if (var10 == null) {
         ModuleStateHelper.handle(
            var1, var6, FontRegistry.config, var8.x() + var6.handle(14.0F), var8.y() + var6.handle(14.0F), 12.0F, "Shader Settings", var7.load()
         );
         ModuleStateHelper.handle(
            var1,
            var6,
            FontRegistry.instance,
            var8.x() + var6.handle(14.0F),
            var8.y() + var6.handle(32.0F),
            8.0F,
            this.colorCompute.process() + " / " + this.dataValidate,
            ThemeColors.handle(var7.save(), 190)
         );
         this.process(var1, var6, var7, var8);
      } else {
         ShaderNodeDefinition var11 = this.cache.handle(var10.process());
         if (var11 != null) {
            ModuleStateHelper.handle(
               var1, var6, FontRegistry.config, var8.x() + var6.handle(14.0F), var8.y() + var6.handle(14.0F), 12.0F, var11.process(), var7.load()
            );
            ModuleStateHelper.handle(
               var1,
               var6,
               FontRegistry.instance,
               var8.x() + var6.handle(14.0F),
               var8.y() + var6.handle(30.0F),
               8.0F,
               var11.compute() + " / " + var10.process(),
               ThemeColors.handle(var7.save(), 190)
            );
            this.handle(var1, var6, var7, var8, var11, var10);
            float var12 = var8.y() + var6.handle(74.0F);
            if (var11.prepare()) {
               RectBounds var13 = this.process(var8, var6);
               boolean var14 = Boolean.TRUE.equals(this.bufferAdapt.get(var10.handle()));
               boolean var15 = var13.contains(var2.sampleLayer(), var2.sendWorld());
               var1.handle(
                  var13.x(),
                  var13.y(),
                  var13.w(),
                  var13.h(),
                  var6.handle(7.0F),
                  ThemeColors.handle(var7.check(), ThemeColors.handle(var14 ? var7.submit() : var7.save(), 76), !var15 && !var14 ? 0.0F : 1.0F)
               );
               var1.handle(var13.x(), var13.y(), var13.w(), var13.h(), var6.handle(7.0F), ThemeColors.handle(var7.save(), var14 ? 150 : 84), 0.65F);
               String var16 = var14 ? "Preview ON" : "Preview OFF";
               float var17 = ModuleStateHelper.handle(var6, FontRegistry.instance, var16, 9.0F);
               ModuleStateHelper.handle(
                  var1, var6, FontRegistry.instance, var13.x() + (var13.w() - var17) * 0.5F, var13.y(), var13.h(), 9.0F, var16, var7.load()
               );
            }

            if ("float_value".equals(var10.process())) {
               this.handle(var1, var6, var7, var10, "value", "Value", -12.0F, 12.0F, 0.01F, 0.5F, var8, 0, var2);
            } else if ("int_value".equals(var10.process())) {
               this.handle(var1, var6, var7, var10, "value", "Value", -64.0F, 64.0F, 1.0F, 1.0F, var8, 0, var2);
            } else if ("exposed_float".equals(var10.process())) {
               this.handle(var1, var6, var7, var10, "name", "Name", var8, 0, var2);
               float var19 = var10.handle("min", 0.0F);
               float var20 = var10.handle("max", 1.0F);
               if (var20 <= var19) {
                  var20 = var19 + 0.001F;
               }

               this.handle(var1, var6, var7, var10, "value", "Default", var19, var20, var10.handle("step", 0.01F), 0.5F, var8, 1, var2);
               this.handle(var1, var6, var7, var10, "min", "Min", -128.0F, 128.0F, 0.01F, 0.0F, var8, 2, var2);
               this.handle(var1, var6, var7, var10, "max", "Max", -128.0F, 128.0F, 0.01F, 1.0F, var8, 3, var2);
               this.handle(var1, var6, var7, var10, "step", "Step", 1.0E-4F, 16.0F, 0.001F, 0.01F, var8, 4, var2);
            } else if ("exposed_color".equals(var10.process())) {
               this.handle(var1, var6, var7, var10, "name", "Name", var8, 0, var2);
               this.handle(var1, var6, var7, var10, "r", "Red", 0.0F, 1.0F, 0.01F, 1.0F, var8, 1, var2);
               this.handle(var1, var6, var7, var10, "g", "Green", 0.0F, 1.0F, 0.01F, 1.0F, var8, 2, var2);
               this.handle(var1, var6, var7, var10, "b", "Blue", 0.0F, 1.0F, 0.01F, 1.0F, var8, 3, var2);
               this.handle(var1, var6, var7, var10, "a", "Alpha", 0.0F, 1.0F, 0.01F, 1.0F, var8, 4, var2);
               RectBounds var18 = new RectBounds(var8.x() + var8.w() - var6.handle(82.0F), var12, var6.handle(68.0F), var6.handle(18.0F));
               var1.handle(
                  var18.x(),
                  var18.y(),
                  var18.w(),
                  var18.h(),
                  var6.handle(6.0F),
                  ThemeColors.handle(
                     Math.round(var10.handle("r", 1.0F) * 255.0F),
                     Math.round(var10.handle("g", 1.0F) * 255.0F),
                     Math.round(var10.handle("b", 1.0F) * 255.0F),
                     Math.round(var10.handle("a", 1.0F) * 255.0F)
                  )
               );
               var1.handle(var18.x(), var18.y(), var18.w(), var18.h(), var6.handle(6.0F), var7.select(), 0.6F);
            } else {
               this.handle(var1, var6, var7, var8, var11, var10, var12);
            }
         }
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4) {
      float var5 = var4.y() + var2.handle(58.0F);
      this.handle(
         var1,
         var2,
         var3,
         var4.x() + var2.handle(14.0F),
         var5,
         "Name",
         this.screenRead != null && !this.screenRead.isBlank() ? this.screenRead : this.enabled.handle().process()
      );
      this.handle(var1, var2, var3, var4.x() + var2.handle(14.0F), var5 + var2.handle(22.0F), "Nodes", String.valueOf(this.enabled.compute().size()));
      this.handle(var1, var2, var3, var4.x() + var2.handle(14.0F), var5 + var2.handle(44.0F), "Links", String.valueOf(this.enabled.resolve().size()));
      this.handle(
         var1,
         var2,
         var3,
         var4.x() + var2.handle(14.0F),
         var5 + var2.handle(66.0F),
         "Uniforms",
         String.valueOf(this.output.handle(this.enabled).exposedUniforms().size())
      );
      this.handle(
         var1,
         var2,
         var3,
         var4.x() + var2.handle(14.0F),
         var5 + var2.handle(88.0F),
         "Author",
         this.enabled.handle().compute().isBlank() ? FoundryStorage.execute() : this.enabled.handle().compute()
      );
      String var6 = this.enabled.update() == this.contextExpand ? "saved" : "dirty";
      RectBounds var7 = new RectBounds(
         var4.x() + var2.handle(14.0F), var4.y() + var4.h() - var2.handle(42.0F), var4.w() - var2.handle(28.0F), var2.handle(28.0F)
      );
      var1.handle(
         var7.x(),
         var7.y(),
         var7.w(),
         var7.h(),
         var2.handle(8.0F),
         ThemeColors.handle(
            ThemeColors.handle(255, 255, 255, this.handle(var3) ? 50 : 9),
            ThemeColors.handle(this.enabled.update() == this.contextExpand ? var3.submit() : var3.save(), 72),
            0.86F
         )
      );
      var1.handle(
         var7.x(),
         var7.y(),
         var7.w(),
         var7.h(),
         var2.handle(8.0F),
         ThemeColors.handle(this.enabled.update() == this.contextExpand ? var3.submit() : var3.save(), 120),
         0.65F
      );
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.config, var7.x() + var2.handle(12.0F), var7.y(), var7.h(), 9.0F, "compile state: " + var6, this.process(var3)
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, ShaderNodeDefinition var5, ShaderGraphBlock var6) {
      float var7 = var4.y() + var2.handle(48.0F);
      String[] var8 = new String[]{var5.update().size() + " in", var5.apply().size() + " out", this.update(var6) ? "uniform" : var5.compute()};
      float var9 = var4.x() + var2.handle(14.0F);

      for (int var10 = 0; var10 < var8.length; var10++) {
         float var11 = var10 == 2 ? var4.w() - var2.handle(28.0F) - (var9 - var4.x() - var2.handle(14.0F)) : var2.handle(58.0F);
         RectBounds var12 = new RectBounds(var9, var7, var11, var2.handle(18.0F));
         var1.handle(var12.x(), var12.y(), var12.w(), var12.h(), var2.handle(6.0F), ThemeColors.handle(255, 255, 255, this.handle(var3) ? 42 : 8));
         var1.handle(var12.x(), var12.y(), var12.w(), var12.h(), var2.handle(6.0F), ThemeColors.handle(var10 == 2 ? var3.submit() : var3.save(), 72), 0.55F);
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.instance,
            var12.x() + var2.handle(8.0F),
            var12.y(),
            var12.h(),
            8.0F,
            ModuleStateHelper.handle(var2, FontRegistry.instance, var8[var10], 8.0F, var12.w() - var2.handle(16.0F)),
            this.compute(var3)
         );
         var9 += var11 + var2.handle(6.0F);
      }
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, ShaderNodeDefinition var5, ShaderGraphBlock var6, float var7
   ) {
      float var8 = var4.w() - var2.handle(28.0F);
      float var9 = var4.y() + var4.h() - var2.handle(16.0F) - var7;
      if ("output_color".equals(var6.process())) {
         this.process(var1, var2, var3, new RectBounds(var4.x() + var2.handle(14.0F), var7, var8, var9), var5, var6);
      } else {
         boolean var10 = !var5.update().isEmpty();
         boolean var11 = !var5.apply().isEmpty();
         if (var10 || var11) {
            if (var10 != var11) {
               RectBounds var16 = new RectBounds(var4.x() + var2.handle(14.0F), var7, var8, var9);
               if (var10) {
                  this.handle(var1, var2, var3, var16, "Inputs", var5.update(), var6, true);
               } else {
                  this.handle(var1, var2, var3, var16, "Outputs", var5.apply(), var6, false);
               }
            } else {
               float var12 = var2.handle(10.0F);
               float var13 = (var8 - var12) * 0.5F;
               RectBounds var14 = new RectBounds(var4.x() + var2.handle(14.0F), var7, var13, var9);
               RectBounds var15 = new RectBounds(var14.x() + var14.w() + var12, var7, var13, var14.h());
               this.handle(var1, var2, var3, var14, "Inputs", var5.update(), var6, true);
               this.handle(var1, var2, var3, var15, "Outputs", var5.apply(), var6, false);
            }
         }
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, RectBounds var4, ShaderNodeDefinition var5, ShaderGraphBlock var6) {
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(255, 255, 255, this.handle(var3) ? 34 : 6));
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(var3.submit(), 92), 0.55F);
      var1.handle(
         var4.x(), var4.y() + var2.handle(8.0F), var2.handle(2.2F), var4.h() - var2.handle(16.0F), var2.handle(1.1F), ThemeColors.handle(var3.submit(), 190)
      );
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.config, var4.x() + var2.handle(12.0F), var4.y() + var2.handle(8.0F), 9.0F, "Result", ThemeColors.handle(var3.submit(), 224)
      );
      String var7 = this.current.compute().isBlank() ? "cold" : "#" + this.current.compute();
      float var8 = var4.x() + var2.handle(12.0F);
      float var9 = var4.y() + var2.handle(26.0F);
      this.handle(var1, var2, var3, var8, var9, "Hash", var7);
      this.handle(var1, var2, var3, var8, var9 + var2.handle(18.0F), "State", this.execute());
      this.handle(var1, var2, var3, var8, var9 + var2.handle(36.0F), "Target", this.colorCompute.process());
      this.handle(var1, var2, var3, var8, var9 + var2.handle(54.0F), "Uniforms", String.valueOf(this.output.handle(this.enabled).exposedUniforms().size()));
      float var10 = var9 + var2.handle(76.0F);

      for (ShaderPinDefinition var12 : var5.update()) {
         if (var10 > var4.y() + var4.h() - var2.handle(14.0F)) {
            break;
         }

         boolean var13 = this.enabled.process(var6.handle(), var12.id()) != null;
         int var14 = this.handle(var12, var3);
         var1.process(var8 + var2.handle(3.0F), var10 + var2.handle(4.4F), var2.handle(2.6F), 0.0F, 1.0F, ThemeColors.handle(var14, var13 ? 245 : 130));
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.instance,
            var8 + var2.handle(12.0F),
            var10,
            8.0F,
            ModuleStateHelper.handle(
               var2, FontRegistry.instance, var12.label() + (var13 ? " / linked" : " / not connected"), 8.0F, var4.w() - var2.handle(60.0F)
            ),
            var13 ? this.process(var3) : this.compute(var3)
         );
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.instance,
            var4.x() + var4.w() - var2.handle(38.0F),
            var10,
            7.0F,
            var12.type().handle(),
            ThemeColors.handle(var14, var13 ? 220 : 150)
         );
         var10 += var2.handle(17.0F);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      RectBounds var4,
      String var5,
      List<ShaderPinDefinition> var6,
      ShaderGraphBlock var7,
      boolean var8
   ) {
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), ThemeColors.handle(255, 255, 255, this.handle(var3) ? 34 : 6));
      var1.handle(var4.x(), var4.y(), var4.w(), var4.h(), var2.handle(8.0F), var3.select(), 0.55F);
      ModuleStateHelper.handle(
         var1, var2, FontRegistry.config, var4.x() + var2.handle(10.0F), var4.y() + var2.handle(8.0F), 9.0F, var5, ThemeColors.handle(var3.submit(), 220)
      );
      float var9 = var4.y() + var2.handle(28.0F);

      for (ShaderPinDefinition var11 : var6) {
         if (var9 > var4.y() + var4.h() - var2.handle(18.0F)) {
            break;
         }

         int var12 = this.handle(var11, var3);
         boolean var13 = var8
            ? this.enabled.process(var7.handle(), var11.id()) != null
            : this.enabled.resolve().stream().anyMatch(var2x -> var2x.handle().equals(var7.handle()) && var2x.process().equals(var11.id()));
         var1.process(var4.x() + var2.handle(12.0F), var9 + var2.handle(6.0F), var2.handle(2.6F), 0.0F, 1.0F, ThemeColors.handle(var12, var13 ? 245 : 130));
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.instance,
            var4.x() + var2.handle(22.0F),
            var9,
            8.0F,
            ModuleStateHelper.handle(var2, FontRegistry.instance, var11.label(), 8.0F, var4.w() - var2.handle(62.0F)),
            var13 ? this.process(var3) : this.compute(var3)
         );
         ModuleStateHelper.handle(
            var1,
            var2,
            FontRegistry.instance,
            var4.x() + var4.w() - var2.handle(38.0F),
            var9,
            7.0F,
            var11.type().handle(),
            ThemeColors.handle(var12, var13 ? 220 : 150)
         );
         var9 += var2.handle(17.0F);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      ShaderGraphBlock var4,
      String var5,
      String var6,
      RectBounds var7,
      int var8,
      ModernClickGuiState var9
   ) {
      RectBounds var10 = this.handle(var7, var2, var8);
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var7.x() + var2.handle(14.0F), var10.y(), var10.h(), 9.0F, var6, var3.animate());
      String var11 = this.process(var4, var5);
      ShaderGraphNode var12 = this.render(var11);
      if (!var12.prepare()) {
         var12.handle(var4.handle(var5, this.apply(var4)));
      }

      var12.handle(var1, var2, var3, var10, var9.sampleLayer(), var9.sendWorld());
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      ShaderGraphBlock var4,
      String var5,
      String var6,
      float var7,
      float var8,
      float var9,
      float var10,
      RectBounds var11,
      int var12,
      ModernClickGuiState var13
   ) {
      RectBounds var14 = this.handle(var11, var2, var12);
      ModuleStateHelper.handle(var1, var2, FontRegistry.instance, var11.x() + var2.handle(14.0F), var14.y(), var14.h(), 9.0F, var6, var3.animate());
      String var15 = this.process(var4, var5);
      ShaderGraphNode var16 = this.handle(var15, var7, var8, var9);
      if (!var16.prepare()) {
         var16.handle(var4.handle(var5, var10));
      }

      var16.handle(var1, var2, var3, var14, var13.sampleLayer(), var13.sendWorld());
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3) {
      ArrayList<ShaderGraphBlock> var4 = new ArrayList<>(this.enabled.compute());
      var4.sort(Comparator.comparing(var1x -> this.compute(var1x.handle())));

      for (ShaderGraphBlock var6 : var4) {
         this.handle(var1, var2, var3, var6);
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeRenderContext var3, ShaderGraphBlock var4) {
      ShaderNodeDefinition var5 = this.cache.handle(var4.process());
      if (var5 != null) {
         GuiMetrics var6 = var3.update();
         ThemeColors var7 = var3.apply();
         float var8 = this.compute(var4.compute());
         float var9 = this.resolve(var4.resolve());
         float var10 = var5.resolve() * this.animationDraw;
         SpringFloat var11 = this.refresh(var4.handle());
         var11.compute(Boolean.TRUE.equals(this.bufferAdapt.get(var4.handle())) ? 1.0F : 0.0F);
         float var12 = var11.handle();
         float var13 = this.process(var5);
         float var14 = this.handle(var5, var4) * this.animationDraw;
         float var15 = Math.max(var6.handle(6.0F), 10.0F * this.animationDraw);
         boolean var16 = this.compute(var4.handle());
         boolean var17 = var2.sampleLayer() >= var8 && var2.sampleLayer() < var8 + var10 && var2.sendWorld() >= var9 && var2.sendWorld() < var9 + var14;
         SpringAnimation var18 = this.actionRender.computeIfAbsent(var4.handle(), var0 -> new SpringAnimation(0.0F));
         float var19 = var18.handle(var17 ? 1.0F : 0.0F, SpringAnimationSpec.select());
         SpringFloat var20 = this.playerApply.computeIfAbsent(var4.handle(), var1x -> this.unload());
         var20.compute(var16 ? 1.0F : (var17 ? 0.38F : 0.0F));
         float var21 = execute(var20.handle());
         SpringAnimation var22 = this.configMatch.computeIfAbsent(var4.handle(), var0 -> new SpringAnimation(1.0F));
         float var23 = var22.handle(1.0F, SpringAnimationSpec.check());
         float var24 = Math.max(var21, var19 * 0.45F);
         float var25 = Math.min(1.0F, (Math.abs(this.outputCollapse) + Math.abs(this.profileInvoke)) * 0.0012F);
         float var26 = Math.max(0.001F, var23) * (1.0F + var24 * 0.016F + var25 * (var17 ? 0.006F : 0.0F));
         var1.handle(var26, var8 + var10 * 0.5F, var9 + var14 * 0.5F);

         try {
            boolean var27 = FoundryNodeSurfaceShader.handle()
               .handle(
                  var1,
                  var8,
                  var9,
                  var10,
                  var14,
                  var15,
                  var19,
                  var21,
                  var25,
                  var7,
                  this.rendererScan,
                  this.sourceBuild,
                  this.scaleParse,
                  this.sessionEncode,
                  this.handle(var7)
               );
            if (!var27) {
               if (var19 > 0.001F) {
                  var1.handle(
                     var8,
                     var9,
                     var10,
                     var14,
                     var15,
                     var6.handle(12.0F) * var19,
                     var6.handle(1.1F),
                     ThemeColors.handle(var7.submit(), Math.round(34.0F * var19))
                  );
               }

               if (var21 > 0.001F) {
                  float var28 = 0.86F + 0.14F * (float)Math.sin((float)(System.currentTimeMillis() % 2200L) / 2200.0F * Math.PI * 2.0);
                  this.handle(var1, var8, var9, var10, var14, var15, var7.save(), var21 * var28 * 0.62F, var6);
               }

               var1.handle(
                  var8,
                  var9 + var6.handle(2.0F),
                  var10,
                  var14,
                  var15,
                  var6.handle(11.0F),
                  var6.handle(1.0F),
                  this.compute(var7, Math.round(82.0F + 24.0F * var21))
               );
               var1.handle(var8, var9, var10, var14, var15, 0.24F + 0.08F * var21);
               var1.handle(var8, var9, var10, var14, var15, this.handle(var7, Math.round(194.0F + 20.0F * var21)));
               if (var17) {
                  var1.handle(
                     var8 + 1.2F * this.animationDraw,
                     var9 + 1.2F * this.animationDraw,
                     var10 - 2.4F * this.animationDraw,
                     var14 - 2.4F * this.animationDraw,
                     Math.max(0.0F, var15 - 1.2F * this.animationDraw),
                     ThemeColors.handle(var7.save(), Math.round(10.0F * var19))
                  );
               }

               var1.handle(
                  var8,
                  var9,
                  var10,
                  var14,
                  var15,
                  ThemeColors.handle(var7.select(), ThemeColors.handle(var7.save(), 118), Math.max(var19 * 0.48F, var21 * 0.72F)),
                  0.55F
               );
            }

            int var43 = this.process(var7);
            int var29 = this.compute(var7);
            ModuleStateHelper.handle(
               var1,
               var6,
               FontRegistry.config,
               var8 + 14.0F * this.animationDraw,
               var9 + 12.0F * this.animationDraw,
               11.0F * this.animationDraw / Math.max(0.001F, var6.process()),
               var5.process(),
               var43
            );
            ModuleStateHelper.handle(
               var1,
               var6,
               FontRegistry.instance,
               var8 + 14.0F * this.animationDraw,
               var9 + 28.0F * this.animationDraw,
               8.5F * this.animationDraw / Math.max(0.001F, var6.process()),
               var5.compute(),
               var29
            );
            if (var5.prepare()) {
               String var30 = "Preview";
               float var31 = 7.5F * this.animationDraw / Math.max(0.001F, var6.process());
               float var32 = ModuleStateHelper.handle(var6, FontRegistry.instance, var30, var31);
               RectBounds var33 = this.handle(var5, var8, var9, var10);
               int var34 = ThemeColors.handle(ThemeColors.handle(var7.save(), this.handle(var7) ? 32 : 44), ThemeColors.handle(var7.submit(), 116), var12);
               var1.handle(var33.x(), var33.y(), var33.w(), var33.h(), var33.h() * 0.5F, var34);
               var1.handle(
                  var33.x(), var33.y(), var33.w(), var33.h(), var33.h() * 0.5F, ThemeColors.handle(var7.save(), Math.round(70.0F + 92.0F * var12)), 0.55F
               );
               ModuleStateHelper.handle(var1, var6, FontRegistry.instance, var33.x() + (var33.w() - var32) * 0.5F, var33.y(), var33.h(), var31, var30, var43);
            }

            var1.compute();
            var1.handle(
               var8 + 1.2F,
               var9 + 1.2F,
               var10 - 2.4F,
               var14 - 2.4F,
               Math.max(0.0F, var15 - 1.2F),
               Math.max(0.0F, var15 - 1.2F),
               Math.max(0.0F, var15 - 1.2F),
               Math.max(0.0F, var15 - 1.2F)
            );

            try {
               this.handle(var1, var3, var4, var8, var9, var10);
               this.handle(var1, var3, var4, var5, var8, var9, var10, var13, var12);
            } finally {
               var1.compute();
               var1.apply();
            }

            this.handle(var1, var3, var4, var5, var8, var9);
         } finally {
            var1.check();
         }
      }
   }

   private void handle(RoundedRectRenderer var1, ThemeRenderContext var2, ShaderGraphBlock var3, float var4, float var5, float var6) {
      GuiMetrics var7 = var2.update();
      ThemeColors var8 = var2.apply();
      if (this.update(var3)) {
         RectBounds var11 = this.resolve(var3);
         ShaderGraphNode var12 = this.process(var3);
         if (!var12.prepare()) {
            var12.handle(var3.handle("name", this.apply(var3)));
         }

         var12.handle(var1, var7, var8, var11, this.rendererScan, this.sourceBuild);
      } else if (execute(var3.process())) {
         RectBounds var9 = this.compute(var3);
         ShaderGraphNode var10 = this.handle(var3);
         if (!var10.prepare()) {
            var10.handle(var3.handle("value", "int_value".equals(var3.process()) ? 1.0F : 0.5F));
         }

         var10.handle(var1, var7, var8, var9, this.rendererScan, this.sourceBuild);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ThemeRenderContext var2,
      ShaderGraphBlock var3,
      ShaderNodeDefinition var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9
   ) {
      if (var3 != null && var4 != null && var4.prepare() && !(var9 <= 0.01F)) {
         GuiMetrics var10 = var2.update();
         ThemeColors var11 = var2.apply();
         float var12 = var5 + 6.0F * this.animationDraw;
         float var13 = var6 + (var8 + 4.0F) * this.animationDraw;
         float var14 = Math.max(1.0F, var7 - 12.0F * this.animationDraw);
         float var15 = Math.max(1.0F, 120.0F * this.animationDraw * var9);
         float var16 = Math.max(var10.handle(5.0F), 8.0F * this.animationDraw);
         var1.handle(var12, var13, var14, var15, var16, ThemeColors.handle(5, 7, 12, Math.round(156.0F * var9)));
         var1.handle(var12, var13, var14, var15, var16, ThemeColors.handle(var11.save(), Math.round(62.0F * var9)), 0.65F);
         if (!(this.animationDraw < 0.6F) && !(var15 < 14.0F)) {
            var1.compute();
            var1.handle(var12, var13, var14, var15, var16, var16, var16, var16);

            try {
               this.mode
                  .handle(this.enabled, var3.handle(), this.current, var1, var12, var13, var14, var15, this.fetchProvider(), this.drawProfile(), var11, var9);
            } finally {
               var1.compute();
               var1.apply();
            }
         }
      }
   }

   private ShaderGraphNode handle(ShaderGraphBlock var1) {
      return this.matrixRender
         .computeIfAbsent(
            var1.handle(), var1x -> "int_value".equals(var1.process()) ? ShaderGraphNode.handle(-64.0F, 64.0F) : ShaderGraphNode.handle(-12.0F, 12.0F)
         );
   }

   private static boolean execute(String var0) {
      return "float_value".equals(var0) || "int_value".equals(var0);
   }

   private static float handle(ShaderGraphBlock var0, float var1) {
      return "int_value".equals(var0.process()) ? Math.round(var1) : var1;
   }

   private ShaderGraphNode process(ShaderGraphBlock var1) {
      return this.moduleTick.computeIfAbsent(var1.handle(), var0 -> ShaderGraphNode.handle());
   }

   private void load() {
      if (this.optionAdvance != null) {
         ShaderGraphNode var1 = this.matrixRender.get(this.optionAdvance);
         if (var1 != null) {
            if (var1.update()) {
               var1.check();
            }

            ShaderGraphBlock var2 = this.enabled.compute(this.optionAdvance);
            if (var2 != null) {
               var2.process("value", handle(var2, var1.compute()));
               this.enabled.apply();
            }
         }

         this.optionAdvance = null;
      }
   }

   private void save() {
      if (this.effectScan != null) {
         ShaderGraphNode var1 = this.moduleTick.get(this.effectScan);
         if (var1 != null) {
            if (var1.update()) {
               var1.check();
            }

            ShaderGraphBlock var2 = this.enabled.compute(this.effectScan);
            if (var2 != null) {
               var2.process("name", var1.resolve());
               this.enabled.apply();
            }
         }

         this.effectScan = null;
      }
   }

   private void submit() {
      if (this.optionParse != null) {
         ShaderGraphNode var1 = this.playerCollapse.get(this.optionParse);
         if (var1 != null) {
            if (var1.update()) {
               var1.check();
            }

            this.prepare(this.optionParse);
         }

         this.optionParse = null;
      }
   }

   private void prepare(String var1) {
      if (var1 != null) {
         int var2 = var1.indexOf(58);
         if (var2 > 0 && var2 < var1.length() - 1) {
            ShaderGraphBlock var3 = this.enabled.compute(var1.substring(0, var2));
            ShaderGraphNode var4 = this.playerCollapse.get(var1);
            if (var3 != null && var4 != null) {
               String var5 = var1.substring(var2 + 1);
               if ("name".equals(var5)) {
                  var3.process("name", var4.resolve());
               } else {
                  var3.process(var5, var4.compute());
                  this.handle(var3, var5);
               }

               this.enabled.apply();
            }
         }
      }
   }

   private void handle(ShaderGraphBlock var1, String var2) {
      if (var1 != null) {
         if ("step".equals(var2)) {
            var1.process("step", Math.max(1.0E-4F, var1.handle("step", 0.01F)));
         } else {
            float var3 = var1.handle("min", 0.0F);
            float var4 = var1.handle("max", 1.0F);
            if (var4 <= var3) {
               if ("min".equals(var2)) {
                  var1.process("max", var3 + 0.001F);
               } else {
                  var1.process("min", var4 - 0.001F);
               }
            }
         }
      }
   }

   private boolean handle(ModernClickGuiState var1, GuiMetrics var2, int var3, float var4, float var5) {
      RectBounds var6 = this.process(var2);
      if (!var6.contains(var4, var5)) {
         this.animationExpand = false;
      }

      if (this.resolve(var2, var3).contains(var4, var5)) {
         var1.matchVector(false);
         this.attachEvent();
         return true;
      } else if (var6.contains(var4, var5)) {
         this.animationExpand = true;
         this.playerRun = System.currentTimeMillis();
         return true;
      } else if (this.handle(var2).contains(var4, var5)) {
         this.scaleAdapt = !this.scaleAdapt;
         this.textureRun = false;
         this.stateApply = false;
         this.indexBind = false;
         return true;
      } else if (this.compute(var2).contains(var4, var5)) {
         this.textureRun = !this.textureRun;
         this.scaleAdapt = false;
         this.stateApply = false;
         this.indexBind = false;
         return true;
      } else if (this.apply(var2, var3).contains(var4, var5)) {
         this.stateApply = !this.stateApply;
         this.scaleAdapt = false;
         this.textureRun = false;
         this.indexBind = false;
         this.matrixFilter = 0.0F;
         return true;
      } else if (this.update(var2, var3).contains(var4, var5)) {
         this.indexBind = !this.indexBind;
         this.scaleAdapt = false;
         this.textureRun = false;
         this.stateApply = false;
         return true;
      } else {
         return false;
      }
   }

   private boolean handle(GuiMetrics var1, int var2, float var3, float var4, int var5) {
      RectBounds var6 = this.update(var1);
      if (var5 != 0) {
         return var6.contains(var3, var4);
      }

      if (!var6.contains(var3, var4)) {
         this.scaleAdapt = false;
         return true;
      }

      for (int var7 = 0; var7 < data.length; var7++) {
         if (this.compute(var6, var1, var7).contains(var3, var4)) {
            if (var7 == 0) {
               this.tick();
            } else if (var7 == 1) {
               this.convertWindow();
            } else if (var7 == 2) {
               this.writePreset();
            } else if (var7 == 3) {
               this.savePreset();
            } else if (var7 == 4) {
               this.drawAnimation();
            } else if (var7 == 5) {
               this.closeProvider();
            }

            return true;
         }
      }

      return true;
   }

   private boolean process(GuiMetrics var1, int var2, int var3, float var4, float var5, int var6) {
      RectBounds var7 = this.resolve(var1, var2, var3);
      if (var6 != 0) {
         return var7.contains(var4, var5);
      }

      if (!var7.contains(var4, var5)) {
         this.textureRun = false;
         return true;
      }

      LivePreviewRenderer[] var8 = LivePreviewRenderer.tick();

      for (int var9 = 0; var9 < var8.length; var9++) {
         RectBounds var10 = this.resolve(var7, var1, var9);
         if (var10.contains(var4, var5)) {
            if (PresetManager.handle().update(var8[var9]) && this.handle(var10, var1).contains(var4, var5)) {
               this.handle(var8[var9]);
               return true;
            }

            this.compute(var8[var9]);
            return true;
         }
      }

      String[] var11 = new String[]{"Host Rectangle", "Inset Shape", "Full Quad"};

      for (int var12 = 0; var12 < var11.length; var12++) {
         if (this.update(var7, var1, var12).contains(var4, var5)) {
            this.readServer();
            this.dataValidate = var11[var12];
            this.scanRenderer();
            this.enabled.apply();
            this.current.handle(this.colorCompute);
            this.current.handle(this.enabled);
            this.drawAnimation(this.dataValidate);
            return true;
         }
      }

      return true;
   }

   private boolean process(GuiMetrics var1, int var2, float var3, float var4, int var5) {
      RectBounds var6 = this.execute(var1, var2);
      if (var5 != 0) {
         return var6.contains(var3, var4);
      }

      if (!var6.contains(var3, var4)) {
         this.indexBind = false;
         return true;
      }

      FoundryWorkspace.Mode[] var7 = FoundryWorkspace.Mode.values();

      for (int var8 = 0; var8 < var7.length; var8++) {
         if (this.apply(var6, var1, var8).contains(var3, var4)) {
            this.scaleRender = var7[var8];
            this.drawAnimation("theme " + this.scaleRender.handle());
            return true;
         }
      }

      return true;
   }

   private boolean compute(GuiMetrics var1, int var2, int var3, float var4, float var5, int var6) {
      if (var6 != 0) {
         return true;
      }

      RectBounds var7 = this.update(var1, var2, var3);
      if (this.apply(var7, var1).contains(var4, var5)) {
         this.render();
         this.resolve(this.actionRead);
         this.configCollapse = false;
         this.actionRead = null;
         return true;
      }

      if (this.execute(var7, var1).contains(var4, var5)) {
         this.resolve(this.actionRead);
         this.configCollapse = false;
         this.actionRead = null;
         return true;
      }

      if (!this.prepare(var7, var1).contains(var4, var5) && var7.contains(var4, var5)) {
         return true;
      }

      this.configCollapse = false;
      this.actionRead = null;
      return true;
   }

   private boolean resolve(GuiMetrics var1, int var2, int var3, float var4, float var5, int var6) {
      RectBounds var7 = this.handle(var1, var2, var3);
      if (!var7.contains(var4, var5)) {
         return false;
      }

      ShaderGraphBlock var8 = this.projectItem();
      if (var8 == null) {
         this.submit();
         return true;
      }

      ShaderNodeDefinition var9 = this.cache.handle(var8.process());
      if (var9 == null) {
         this.submit();
         return true;
      }

      if (var6 == 0 && var9.prepare() && this.process(var7, var1).contains(var4, var5)) {
         this.onTick(var8.handle());
         return true;
      }

      if (var6 != 0) {
         return true;
      }

      if ("float_value".equals(var8.process())) {
         return this.handle(var8, "value", -12.0F, 12.0F, 0.01F, 0.5F, var7, var1, 0, var4, var5, var6);
      }

      if ("int_value".equals(var8.process())) {
         return this.handle(var8, "value", -64.0F, 64.0F, 1.0F, 1.0F, var7, var1, 0, var4, var5, var6);
      }

      if ("exposed_float".equals(var8.process())) {
         if (this.handle(var8, "name", var7, var1, 0, var4, var5, var6)) {
            return true;
         }

         float var10 = var8.handle("min", 0.0F);
         float var11 = var8.handle("max", 1.0F);
         if (var11 <= var10) {
            var11 = var10 + 0.001F;
         }

         if (this.handle(var8, "value", var10, var11, var8.handle("step", 0.01F), 0.5F, var7, var1, 1, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "min", -128.0F, 128.0F, 0.01F, 0.0F, var7, var1, 2, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "max", -128.0F, 128.0F, 0.01F, 1.0F, var7, var1, 3, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "step", 1.0E-4F, 16.0F, 0.001F, 0.01F, var7, var1, 4, var4, var5, var6)) {
            return true;
         }
      }

      if ("exposed_color".equals(var8.process())) {
         if (this.handle(var8, "name", var7, var1, 0, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "r", 0.0F, 1.0F, 0.01F, 1.0F, var7, var1, 1, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "g", 0.0F, 1.0F, 0.01F, 1.0F, var7, var1, 2, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "b", 0.0F, 1.0F, 0.01F, 1.0F, var7, var1, 3, var4, var5, var6)) {
            return true;
         }

         if (this.handle(var8, "a", 0.0F, 1.0F, 0.01F, 1.0F, var7, var1, 4, var4, var5, var6)) {
            return true;
         }
      }

      this.submit();
      return true;
   }

   private boolean handle(ShaderGraphBlock var1, String var2, RectBounds var3, GuiMetrics var4, int var5, float var6, float var7, int var8) {
      RectBounds var9 = this.handle(var3, var4, var5);
      if (!var9.contains(var6, var7)) {
         return false;
      }

      this.readServer();
      this.submit();
      String var10 = this.process(var1, var2);
      ShaderGraphNode var11 = this.render(var10);
      var11.handle(var1.handle(var2, this.apply(var1)));
      if (var11.handle(var6, var7, var8, var9)) {
         this.optionParse = var10;
      }

      return true;
   }

   private boolean handle(
      ShaderGraphBlock var1,
      String var2,
      float var3,
      float var4,
      float var5,
      float var6,
      RectBounds var7,
      GuiMetrics var8,
      int var9,
      float var10,
      float var11,
      int var12
   ) {
      RectBounds var13 = this.handle(var7, var8, var9);
      if (!var13.contains(var10, var11)) {
         return false;
      }

      this.readServer();
      this.submit();
      String var14 = this.process(var1, var2);
      ShaderGraphNode var15 = this.handle(var14, var3, var4, var5);
      var15.handle(var1.handle(var2, var6));
      if (var15.handle(var10, var11, var12, var13)) {
         this.optionParse = var14;
      }

      return true;
   }

   private void handle(RoundedRectRenderer var1, ThemeRenderContext var2, ShaderGraphBlock var3, ShaderNodeDefinition var4, float var5, float var6) {
      GuiMetrics var7 = var2.update();
      ThemeColors var8 = var2.apply();
      FoundryPinShader var9 = FoundryPinShader.handle();
      boolean var10 = var9.handle(var1, this.scaleParse, this.sessionEncode);
      int var11 = this.handle(var8) ? ThemeColors.handle(255, 255, 255, 245) : ThemeColors.handle(8, 10, 16, 240);

      for (int var12 = 0; var12 < var4.update().size(); var12++) {
         ShaderPinDefinition var13 = var4.update().get(var12);
         float var14 = var5;
         float var15 = var6 + this.handle(var12) * this.animationDraw;
         float var16 = this.handle(var3.handle(), var13.id(), ShaderPinDirection.INPUT, var14, var15);
         float[] var17 = this.handle(var14, var15, var16);
         var14 = var17[0];
         var15 = var17[1];
         int var18 = this.handle(var13, var8);
         if (var10) {
            float var19 = Math.max(3.4F, 4.6F * this.animationDraw) + 3.4F * var16;
            float var20 = Math.max(1.8F, 2.1F * this.animationDraw);
            var9.handle(var1, var14, var15, var19, var20, var18, var11, var16, process(var3.handle().hashCode() * 0.0031F + var12 * 0.173F));
         } else {
            if (var16 > 0.001F) {
               var1.handle(
                  var14 - 5.0F * this.animationDraw,
                  var15 - 5.0F * this.animationDraw,
                  10.0F * this.animationDraw,
                  10.0F * this.animationDraw,
                  5.0F * this.animationDraw,
                  var7.handle(14.0F) * var16,
                  var7.handle(2.0F),
                  ThemeColors.handle(var18, Math.round(132.0F * var16))
               );
            }

            var1.process(var14, var15, Math.max(3.4F, 4.6F * this.animationDraw) + 3.4F * var16, 0.0F, 1.0F, var18);
            var1.process(var14, var15, Math.max(1.8F, 2.1F * this.animationDraw), 0.0F, 1.0F, var11);
         }

         ModuleStateHelper.handle(
            var1,
            var7,
            FontRegistry.instance,
            var14 + 10.0F * this.animationDraw,
            var15 - 6.3F * this.animationDraw,
            8.5F * this.animationDraw / Math.max(0.001F, var7.process()),
            var13.label(),
            this.compute(var8)
         );
      }

      for (int var21 = 0; var21 < var4.apply().size(); var21++) {
         ShaderPinDefinition var22 = var4.apply().get(var21);
         float var24 = var5 + var4.resolve() * this.animationDraw;
         float var27 = var6 + this.handle(var21) * this.animationDraw;
         float var29 = this.handle(var3.handle(), var22.id(), ShaderPinDirection.OUTPUT, var24, var27);
         float[] var30 = this.handle(var24, var27, var29);
         var24 = var30[0];
         var27 = var30[1];
         int var31 = this.handle(var22, var8);
         if (var10) {
            float var32 = Math.max(3.4F, 4.6F * this.animationDraw) + 3.4F * var29;
            float var34 = Math.max(1.8F, 2.1F * this.animationDraw);
            var9.handle(var1, var24, var27, var32, var34, var31, var11, var29, process(var3.handle().hashCode() * 0.0047F + var21 * 0.191F + 0.41F));
         } else {
            if (var29 > 0.001F) {
               var1.handle(
                  var24 - 5.0F * this.animationDraw,
                  var27 - 5.0F * this.animationDraw,
                  10.0F * this.animationDraw,
                  10.0F * this.animationDraw,
                  5.0F * this.animationDraw,
                  var7.handle(14.0F) * var29,
                  var7.handle(2.0F),
                  ThemeColors.handle(var31, Math.round(132.0F * var29))
               );
            }

            var1.process(var24, var27, Math.max(3.4F, 4.6F * this.animationDraw) + 3.4F * var29, 0.0F, 1.0F, var31);
            var1.process(var24, var27, Math.max(1.8F, 2.1F * this.animationDraw), 0.0F, 1.0F, var11);
         }

         float var33 = ModuleStateHelper.handle(var7, FontRegistry.instance, var22.label(), 8.5F * this.animationDraw / Math.max(0.001F, var7.process()));
         ModuleStateHelper.handle(
            var1,
            var7,
            FontRegistry.instance,
            var24 - 10.0F * this.animationDraw - var33,
            var27 - 6.3F * this.animationDraw,
            8.5F * this.animationDraw / Math.max(0.001F, var7.process()),
            var22.label(),
            this.compute(var8)
         );
      }

      if (var10) {
         var9.process();
      }
   }

   private float handle(String var1, String var2, ShaderPinDirection var3, float var4, float var5) {
      float var6 = (float)Math.hypot(this.rendererScan - var4, this.sourceBuild - var5);
      float var7 = var6 <= Math.max(18.0F, 22.0F * this.animationDraw) ? 1.0F : 0.0F;
      String var8 = var1 + "." + var2 + "." + var3.name();
      return this.packetRead.computeIfAbsent(var8, var0 -> new SpringAnimation(0.0F)).handle(var7, SpringAnimationSpec.onTick());
   }

   private float[] handle(float var1, float var2, float var3) {
      float var4 = this.rendererScan - var1;
      float var5 = this.sourceBuild - var2;
      float var6 = (float)Math.hypot(var4, var5);
      if (!(var6 <= 0.001F) && !(var3 <= 0.001F)) {
         float var7 = Math.min(5.5F * this.animationDraw, var6 * 0.22F) * var3;
         return new float[]{var1 + var4 / var6 * var7, var2 + var5 / var6 * var7};
      } else {
         return new float[]{var1, var2};
      }
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2, int var3, int var4, float var5) {
      HashMap var6 = new HashMap();
      Map var7 = this.blendMatrix();
      float var8 = this.rendererCancel.handle(0.0F, SpringAnimationSpec.check());
      FoundryWireRenderer var9 = FoundryWireRenderer.handle();
      if (var9.handle(var1, var3, var4, var5)) {
         for (ShaderGraphLink var11 : this.enabled.resolve()) {
            FoundryWorkspace.PrimaryDataRecord var12 = this.handle(var11.handle(), var11.process(), ShaderPinDirection.OUTPUT);
            FoundryWorkspace.PrimaryDataRecord var13 = this.handle(var11.compute(), var11.resolve(), ShaderPinDirection.INPUT);
            if (var12 != null && var13 != null) {
               ShaderPinDefinition var14 = this.process(var11.handle(), var11.process(), ShaderPinDirection.OUTPUT);
               ShaderPinDefinition var15 = this.process(var11.compute(), var11.resolve(), ShaderPinDirection.INPUT);
               FoundryWorkspace.RuntimeDataRecord var16 = this.handle(var11.handle(), var11.process(), var14, var15, var2);
               int var17 = this.handle(var11.handle(), var6);
               Integer var18 = (Integer)var7.get(handle(var11));
               float var19 = var18 == null ? -1.0F : process(var18.intValue() * 0.105F + process(var11) * 0.019F);
               this.handle(
                  var9,
                  var12.x,
                  var12.y,
                  var13.x,
                  var13.y,
                  var16.a(),
                  var16.b(),
                  false,
                  var19,
                  var8,
                  var17,
                  this.check(var11.handle()),
                  this.check(var11.compute())
               );
            }
         }

         var9.process();
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeColors var3, int var4, int var5, float var6) {
      if (this.colorMeasure != null && this.animationSchedule != null) {
         FoundryWorkspace.PrimaryDataRecord var7 = this.handle(this.colorMeasure, this.animationSchedule, ShaderPinDirection.OUTPUT);
         if (var7 == null) {
            this.settingSchedule = false;
         } else {
            float var8 = var2.sampleLayer();
            float var9 = var2.sendWorld();
            if (!this.settingSchedule) {
               this.indexSave = var8;
               this.indexCheck = var9;
               this.settingSchedule = true;
            } else {
               float var10 = Math.max(0.001F, Math.min(0.05F, SpringAnimation.handle()));
               float var11 = 1.0F - (float)Math.exp(-24.0F * var10);
               this.indexSave = this.indexSave + (var8 - this.indexSave) * var11;
               this.indexCheck = this.indexCheck + (var9 - this.indexCheck) * var11;
            }

            ShaderPinDefinition var15 = this.process(this.colorMeasure, this.animationSchedule, ShaderPinDirection.OUTPUT);
            FoundryWorkspace.RuntimeDataRecord var16 = this.handle(this.colorMeasure, this.animationSchedule, var15, var3, 0);
            int var12 = var16.a();
            int var13 = ThemeColors.handle(var16.b(), 190);
            FoundryWireRenderer var14 = FoundryWireRenderer.handle();
            if (var14.handle(var1, var4, var5, var6)) {
               this.handle(var14, var7.x, var7.y, this.indexSave, this.indexCheck, var12, var13, true, -1.0F, 1.0F, 0, this.check(this.colorMeasure), 1.0F);
               var14.process();
            }
         }
      } else {
         this.settingSchedule = false;
      }
   }

   private void handle(
      FoundryWireRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      boolean var8,
      float var9,
      float var10,
      int var11,
      float var12,
      float var13
   ) {
      float var14 = Math.abs(var4 - var2);
      float var15 = process((Math.abs(this.eventReceive) + Math.abs(this.screenSubmit)) * 0.012F, 0.0F, 1.0F);
      float var16 = Math.max(78.0F * this.animationDraw, var14 * (0.44F + 0.14F * var10 + var15 * 0.075F + Math.min(0.08F, var11 * 0.008F)));
      float var17 = var8 ? process((Math.abs(this.outputCollapse) + Math.abs(this.profileInvoke)) * 6.0E-4F, 0.0F, 1.0F) : 0.0F;
      float var18 = 1.2F + var10 * 0.2F + (var8 ? 0.34F : 0.0F) + var17 * 0.12F + var15 * 0.08F;
      boolean var19 = var8 || var9 >= 0.0F;
      float var20 = var9 >= 0.0F ? 0.118F + var10 * 0.036F + var15 * 0.02F + Math.min(0.028F, var11 * 0.002F) : 0.0F;
      float var21 = this.eventReceive * process(var12, 0.0F, 1.0F);
      float var22 = this.screenSubmit * process(var12, 0.0F, 1.0F);
      float var23 = this.eventReceive * process(var13, 0.0F, 1.0F);
      float var24 = this.screenSubmit * process(var13, 0.0F, 1.0F);
      var1.handle(var2, var3, var4, var5, var16, var6, var7, var18, var19, var20, var9, var21, var22, var23, var24);
   }

   private float check(String var1) {
      if (var1 == null || this.itemProject == null) {
         return 0.0F;
      } else if (var1.equals(this.itemProject)) {
         return 1.0F;
      } else {
         return this.serverRead.containsKey(var1) ? 0.92F : 0.0F;
      }
   }

   private SpringFloat unload() {
      return new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.4F, 0.72F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F);
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, int var7, float var8, GuiMetrics var9) {
      float var10 = process(var8, 0.0F, 1.0F);
      var1.handle(var2, var3, var4, var5, var6, var9.handle(15.0F) * var10, var9.handle(2.2F), ThemeColors.handle(var7, Math.round(96.0F * var10)));
      var1.handle(var2, var3, var4, var5, var6, var9.handle(30.0F) * var10, var9.handle(6.0F), ThemeColors.handle(var7, Math.round(36.0F * var10)));
   }

   private void process(float var1, float var2) {
      this.source = true;
      this.target = var1;
      this.pending = var2;
      this.previous = this.renderer;
      this.latest = this.handler;
      this.summary = 0.0F;
      this.matrixBlend = 0.0F;
      this.vectorMatch = System.nanoTime();
   }

   private void compute(float var1, float var2) {
      long var3 = System.nanoTime();
      float var5 = Math.max(0.001F, Math.min(0.05F, (float)(var3 - this.vectorMatch) / 1.0E9F));
      this.summary = (this.renderer - var1) / var5;
      this.matrixBlend = (this.handler - var2) / var5;
      this.vectorMatch = var3;
   }

   private void fetch() {
      if (!this.source && !this.layoutSave) {
         float var1 = SpringAnimation.handle();
         if (Math.abs(this.summary) < 0.01F && Math.abs(this.matrixBlend) < 0.01F) {
            this.summary = 0.0F;
            this.matrixBlend = 0.0F;
         } else {
            this.renderer = this.renderer + this.summary * var1;
            this.handler = this.handler + this.matrixBlend * var1;
            float var2 = (float)Math.exp(-8.8F * var1);
            this.summary *= var2;
            this.matrixBlend *= var2;
         }
      }
   }

   private void measure() {
      float var1 = Math.max(0.001F, Math.min(0.05F, SpringAnimation.handle()));
      boolean var2 = this.itemProject != null;
      float var3 = var2 ? process(this.outputCollapse * 0.018F, -42.0F, 42.0F) : 0.0F;
      float var4 = var2 ? process(this.profileInvoke * 0.018F, -42.0F, 42.0F) : 0.0F;
      float var5 = (var3 - this.eventReceive) * 82.0F - this.cacheHandle * 15.5F;
      float var6 = (var4 - this.screenSubmit) * 82.0F - this.rangeRelease * 15.5F;
      this.cacheHandle += var5 * var1;
      this.rangeRelease += var6 * var1;
      this.eventReceive = this.eventReceive + this.cacheHandle * var1;
      this.screenSubmit = this.screenSubmit + this.rangeRelease * var1;
      if (!var2
         && Math.abs(this.eventReceive) < 0.01F
         && Math.abs(this.screenSubmit) < 0.01F
         && Math.abs(this.cacheHandle) < 0.01F
         && Math.abs(this.rangeRelease) < 0.01F) {
         this.eventReceive = 0.0F;
         this.screenSubmit = 0.0F;
         this.cacheHandle = 0.0F;
         this.rangeRelease = 0.0F;
      }
   }

   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, ThemeColors var3, int var4, int var5, float var6) {
      HoloZoomShader.handle()
         .handle(var1, var4, var5, this.renderer, this.handler, this.animationDraw, var2.sampleLayer(), var2.sendWorld(), 0.95F, var6, var3, this.handle(var3));
   }

   private void handle(RoundedRectRenderer var1, ThemeRenderContext var2, ThemeColors var3) {
      if (this.moduleCollect) {
         GuiMetrics var4 = var2.update();
         float var5 = Math.min(this.providerClose, this.windowConvert);
         float var6 = Math.min(this.presetSave, this.presetWrite);
         float var7 = Math.abs(this.windowConvert - this.providerClose);
         float var8 = Math.abs(this.presetWrite - this.presetSave);
         if (!(var7 < 1.0F) && !(var8 < 1.0F)) {
            float var9 = var4.handle(6.0F);
            var1.handle(var5, var6, var7, var8, var9, ThemeColors.handle(var3.save(), 24));
            var1.handle(var5, var6, var7, var8, var9, ThemeColors.handle(var3.save(), 150), 0.9F);
            var1.handle(var5, var6, var7, var8, var9, var4.handle(14.0F), var4.handle(1.0F), ThemeColors.handle(var3.submit(), 34));
         }
      }
   }

   private void handle(RoundedRectRenderer var1, ThemeRenderContext var2, int var3, int var4) {
      GuiMetrics var5 = var2.update();
      ThemeColors var6 = var2.apply();
      String var7 = "RMB -> Node Browser | Space+LMB / MMB pan | LMB drag select | Shift+D duplicate | Wheel zoom | Ctrl+C/V share | Del erase | Ctrl+Z/Y undo | Ctrl+S save";
      float var8 = ModuleStateHelper.handle(var5, FontRegistry.instance, var7, 9.0F);
      float var9 = (var3 - var8) * 0.5F;
      float var10 = var4 - var5.handle(20.0F);
      var1.handle(
         var9 - var5.handle(10.0F), var10 - var5.handle(2.0F), var8 + var5.handle(20.0F), var5.handle(18.0F), var5.handle(8.0F), this.process(var6, 188)
      );
      var1.handle(
         var9 - var5.handle(10.0F),
         var10 - var5.handle(2.0F),
         var8 + var5.handle(20.0F),
         var5.handle(18.0F),
         var5.handle(8.0F),
         ThemeColors.handle(var6.save(), 56),
         0.6F
      );
      ModuleStateHelper.handle(
         var1, var5, FontRegistry.instance, var9, var10 - var5.handle(2.0F), var5.handle(18.0F), 9.0F, var7, ThemeColors.handle(var6.load(), 200)
      );
   }

   private int handle(String var1, Map<String, Integer> var2) {
      Integer var3 = (Integer)var2.get(var1);
      if (var3 != null) {
         return var3;
      }

      var2.put(var1, 0);
      int var4 = 0;

      for (ShaderGraphLink var6 : this.enabled.resolve()) {
         if (var6.compute().equals(var1)) {
            var4 = Math.max(var4, this.handle(var6.handle(), var2) + 1);
         }
      }

      var2.put(var1, var4);
      return var4;
   }

   private Map<String, Integer> blendMatrix() {
      HashMap var1 = new HashMap();
      LinkedHashSet<String> var2 = new LinkedHashSet<>();

      for (ShaderGraphBlock var4 : this.enabled.compute()) {
         if ("output_color".equals(var4.process())) {
            var2.add(var4.handle());
         }
      }

      LinkedHashSet<String> var10 = new LinkedHashSet<>(var2);

      for (int var11 = 0; !var2.isEmpty() && var11 < 256; var11++) {
         LinkedHashSet<String> var5 = new LinkedHashSet<>();

         for (String var7 : var2) {
            for (ShaderGraphLink var9 : this.enabled.resolve()) {
               if (var9.compute().equals(var7)) {
                  var1.putIfAbsent(handle(var9), var11);
                  if (var10.add(var9.handle())) {
                     var5.add(var9.handle());
                  }
               }
            }
         }

         var2 = var5;
      }

      return var1;
   }

   private static String handle(ShaderGraphLink var0) {
      return var0.update() + ">" + var0.apply();
   }

   private static float process(ShaderGraphLink var0) {
      int var1 = 17;
      var1 = var1 * 31 + var0.handle().hashCode();
      var1 = var1 * 31 + var0.process().hashCode();
      var1 = var1 * 31 + var0.compute().hashCode();
      var1 = var1 * 31 + var0.resolve().hashCode();
      return (var1 & 1023) / 1023.0F;
   }

   private static float process(float var0) {
      return var0 - (float)Math.floor(var0);
   }

   private List<ShaderNodeDefinition> matchVector() {
      ArrayList<ShaderNodeDefinition> var1 = new ArrayList<>(this.cache.handle());
      var1.sort(Comparator.comparing(ShaderNodeDefinition::compute).thenComparing(ShaderNodeDefinition::process, String.CASE_INSENSITIVE_ORDER));
      return var1;
   }

   private ShaderGraphBlock resolve(float var1, float var2) {
      ArrayList var3 = new ArrayList<>(this.enabled.compute());

      for (int var4 = var3.size() - 1; var4 >= 0; var4--) {
         ShaderGraphBlock var5 = (ShaderGraphBlock)var3.get(var4);
         ShaderNodeDefinition var6 = this.cache.handle(var5.process());
         if (var6 != null) {
            float var7 = this.compute(var5.compute());
            float var8 = this.resolve(var5.resolve());
            float var9 = var6.resolve() * this.animationDraw;
            float var10 = this.handle(var6, var5) * this.animationDraw;
            if (var1 >= var7 && var1 < var7 + var9 && var2 >= var8 && var2 < var8 + var10) {
               return var5;
            }
         }
      }

      return null;
   }

   private ShaderGraphBlock update(float var1, float var2) {
      ArrayList var3 = new ArrayList<>(this.enabled.compute());

      for (int var4 = var3.size() - 1; var4 >= 0; var4--) {
         ShaderGraphBlock var5 = (ShaderGraphBlock)var3.get(var4);
         ShaderNodeDefinition var6 = this.cache.handle(var5.process());
         if (var6 != null && var6.prepare()) {
            float var7 = this.compute(var5.compute());
            float var8 = this.resolve(var5.resolve());
            float var9 = var6.resolve() * this.animationDraw;
            if (this.handle(var6, var7, var8, var9).contains(var1, var2)) {
               return var5;
            }
         }
      }

      return null;
   }

   private void onTick(String var1) {
      if (var1 != null) {
         boolean var2 = !Boolean.TRUE.equals(this.bufferAdapt.get(var1));
         this.bufferAdapt.put(var1, var2);
         this.refresh(var1).compute(var2 ? 1.0F : 0.0F);
         if (var2) {
            this.select(var1);
         }
      }
   }

   private void select(String var1) {
      int var2 = 0;

      for (Boolean var4 : this.bufferAdapt.values()) {
         if (Boolean.TRUE.equals(var4)) {
            var2++;
         }
      }

      Iterator var5 = this.bufferAdapt.entrySet().iterator();

      while (var2 > 10 && var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         if (!((String)var6.getKey()).equals(var1) && Boolean.TRUE.equals(var6.getValue())) {
            var6.setValue(false);
            this.refresh((String)var6.getKey()).compute(0.0F);
            var2--;
         }
      }
   }

   private FoundryWorkspace.SecondaryDataRecord apply(float var1, float var2) {
      for (ShaderGraphBlock var4 : this.enabled.compute()) {
         ShaderNodeDefinition var5 = this.cache.handle(var4.process());
         if (var5 != null) {
            for (int var6 = 0; var6 < var5.update().size(); var6++) {
               ShaderPinDefinition var7 = var5.update().get(var6);
               float var8 = this.compute(var4.compute());
               float var9 = this.resolve(var4.resolve() + this.handle(var6));
               if (Math.hypot(var1 - var8, var2 - var9) <= Math.max(12.0F, 13.0F * this.animationDraw)) {
                  return new FoundryWorkspace.SecondaryDataRecord(var4.handle(), var7.id(), ShaderPinDirection.INPUT);
               }
            }

            for (int var10 = 0; var10 < var5.apply().size(); var10++) {
               ShaderPinDefinition var11 = var5.apply().get(var10);
               float var12 = this.compute(var4.compute() + var5.resolve());
               float var13 = this.resolve(var4.resolve() + this.handle(var10));
               if (Math.hypot(var1 - var12, var2 - var13) <= Math.max(12.0F, 13.0F * this.animationDraw)) {
                  return new FoundryWorkspace.SecondaryDataRecord(var4.handle(), var11.id(), ShaderPinDirection.OUTPUT);
               }
            }
         }
      }

      return null;
   }

   private FoundryWorkspace.PrimaryDataRecord handle(String var1, String var2, ShaderPinDirection var3) {
      ShaderGraphBlock var4 = this.enabled.compute(var1);
      if (var4 == null) {
         return null;
      }

      ShaderNodeDefinition var5 = this.cache.handle(var4.process());
      if (var5 == null) {
         return null;
      }

      List var6 = var3 == ShaderPinDirection.INPUT ? var5.update() : var5.apply();

      for (int var7 = 0; var7 < var6.size(); var7++) {
         if (((ShaderPinDefinition)var6.get(var7)).id().equals(var2)) {
            float var8 = var3 == ShaderPinDirection.INPUT ? var4.compute() : var4.compute() + var5.resolve();
            return new FoundryWorkspace.PrimaryDataRecord(this.compute(var8), this.resolve(var4.resolve() + this.handle(var7)));
         }
      }

      return null;
   }

   private int handle(String var1, String var2, ShaderPinDirection var3, ThemeColors var4) {
      return this.handle(this.process(var1, var2, var3), var4);
   }

   private ShaderPinDefinition process(String var1, String var2, ShaderPinDirection var3) {
      ShaderGraphBlock var4 = this.enabled.compute(var1);
      if (var4 == null) {
         return null;
      }

      ShaderNodeDefinition var5 = this.cache.handle(var4.process());
      if (var5 == null) {
         return null;
      }

      for (ShaderPinDefinition var8 : var3 == ShaderPinDirection.INPUT ? var5.update() : var5.apply()) {
         if (var8.id().equals(var2)) {
            return var8;
         }
      }

      return null;
   }

   private FoundryWorkspace.RuntimeDataRecord handle(String var1, String var2, ShaderPinDefinition var3, ShaderPinDefinition var4, ThemeColors var5) {
      FoundryWorkspace.RuntimeDataRecord var6 = this.handle(var1, var2, var3, var5, 0);
      if (var4 != null && var3 != null && var3.type() != var4.type()) {
         int var7 = ThemeColors.handle(this.handle(this.handle(var4, var5), var5), 246);
         return new FoundryWorkspace.RuntimeDataRecord(var6.b(), var7);
      } else {
         return var6;
      }
   }

   private FoundryWorkspace.RuntimeDataRecord handle(String var1, String var2, ShaderPinDefinition var3, ThemeColors var4, int var5) {
      int var6 = ThemeColors.handle(this.handle(this.handle(var3, var4), var4), 246);
      if (var3 != null && var3.type() == ShaderValueType.VEC4 && var5 <= 10) {
         ShaderGraphBlock var7 = this.enabled.compute(var1);
         if (var7 == null) {
            return new FoundryWorkspace.RuntimeDataRecord(var6, var6);
         } else {
            String var8 = var7.process();
            if ("theme_top".equals(var8)) {
               int var11 = ThemeColors.handle(this.handle(var4.save(), var4), 246);
               return new FoundryWorkspace.RuntimeDataRecord(var11, var11);
            } else if ("theme_bottom".equals(var8)) {
               int var10 = ThemeColors.handle(this.handle(var4.submit(), var4), 246);
               return new FoundryWorkspace.RuntimeDataRecord(var10, var10);
            } else if ("theme_panel".equals(var8)) {
               int var9 = ThemeColors.handle(this.handle(var4.apply(), var4), 246);
               return new FoundryWorkspace.RuntimeDataRecord(var9, var9);
            } else if ("color_ramp".equals(var8) || "color_pulse".equals(var8) || "vec4_mix".equals(var8)) {
               return this.handle(var1, "a", "b", var4, var5 + 1, var6);
            } else if ("color_gradient_map".equals(var8)) {
               return this.handle(var1, "a", "c", var4, var5 + 1, var6);
            } else if ("alpha_blend".equals(var8)
               || "blend_screen".equals(var8)
               || "blend_overlay".equals(var8)
               || "blend_multiply".equals(var8)
               || "blend_add".equals(var8)) {
               return this.handle(var1, "base", "layer", var4, var5 + 1, var6);
            } else if ("glass_surface".equals(var8)) {
               return this.handle(var1, "tint", var4, var5 + 1, var6);
            } else {
               return !"sdf_fill".equals(var8)
                     && !"rim_light".equals(var8)
                     && !"hover_glow".equals(var8)
                     && !"exposure_lift".equals(var8)
                     && !"color_multiply_scalar".equals(var8)
                     && !"color_desaturate".equals(var8)
                     && !"color_invert".equals(var8)
                     && !"color_screen_split".equals(var8)
                     && !"chromatic_aberration".equals(var8)
                     && !"posterize".equals(var8)
                     && !"bloom_lift".equals(var8)
                  ? new FoundryWorkspace.RuntimeDataRecord(var6, var6)
                  : this.handle(var1, "color", var4, var5 + 1, var6);
            }
         }
      } else {
         return new FoundryWorkspace.RuntimeDataRecord(var6, var6);
      }
   }

   private FoundryWorkspace.RuntimeDataRecord handle(String var1, String var2, String var3, ThemeColors var4, int var5, int var6) {
      FoundryWorkspace.RuntimeDataRecord var7 = this.handle(var1, var2, var4, var5, var6);
      FoundryWorkspace.RuntimeDataRecord var8 = this.handle(var1, var3, var4, var5, var6);
      return new FoundryWorkspace.RuntimeDataRecord(var7.a(), var8.b());
   }

   private FoundryWorkspace.RuntimeDataRecord handle(String var1, String var2, ThemeColors var3, int var4, int var5) {
      ShaderGraphLink var6 = this.enabled.process(var1, var2);
      if (var6 != null) {
         ShaderPinDefinition var9 = this.process(var6.handle(), var6.process(), ShaderPinDirection.OUTPUT);
         return this.handle(var6.handle(), var6.process(), var9, var3, var4);
      } else {
         ShaderPinDefinition var7 = this.process(var1, var2, ShaderPinDirection.INPUT);
         int var8 = this.handle(var7, var3, var5);
         return new FoundryWorkspace.RuntimeDataRecord(var8, var8);
      }
   }

   private int handle(ShaderPinDefinition var1, ThemeColors var2, int var3) {
      if (var1 != null && var1.type() == ShaderValueType.VEC4) {
         String var4 = var1.defaultExpression();
         if (var4 == null) {
            return var3;
         } else if (var4.contains("u_AccentTop")) {
            return ThemeColors.handle(this.handle(var2.save(), var2), 246);
         } else if (var4.contains("u_AccentBottom")) {
            return ThemeColors.handle(this.handle(var2.submit(), var2), 246);
         } else if (var4.contains("u_ThemeColors")) {
            return ThemeColors.handle(this.handle(var2.apply(), var2), 246);
         } else {
            return var4.contains("vec4(1.0") ? ThemeColors.handle(this.handle(var2.load(), var2), 246) : var3;
         }
      } else {
         return var3;
      }
   }

   private int handle(int var1, ThemeColors var2) {
      int var3 = ThemeColors.handle(var1, 255);
      float var4 = this.handle(var2) ? 0.02F : 0.16F;
      return ThemeColors.handle(var3, var2.load(), var4);
   }

   private int handle(ShaderPinDefinition var1, ThemeColors var2) {
      if (var1 == null) {
         return ThemeColors.handle(var2.save(), 220);
      }

      return switch (var1.type()) {
         case FLOAT -> ThemeColors.handle(250, 211, 126, 240);
         case VEC2 -> ThemeColors.handle(119, 210, 255, 240);
         case VEC3 -> ThemeColors.handle(var2.save(), 240);
         case VEC4 -> ThemeColors.handle(var2.submit(), 240);
         case INT -> ThemeColors.handle(155, 255, 61, 240);
      };
   }

   private int process(ThemeColors var1) {
      return this.handle(var1) ? ThemeColors.handle(10, 10, 10, 255) : var1.load();
   }

   private int compute(ThemeColors var1) {
      return this.handle(var1) ? ThemeColors.handle(10, 10, 10, 210) : var1.animate();
   }

   private float process(ShaderNodeDefinition var1) {
      int var2 = Math.max(var1.update().size(), var1.apply().size());
      float var3 = Math.max(96.0F, 60.0F + var2 * 26.0F);
      return !"float_value".equals(var1.handle())
            && !"int_value".equals(var1.handle())
            && !"exposed_float".equals(var1.handle())
            && !"exposed_color".equals(var1.handle())
         ? var3
         : var3 + 24.0F;
   }

   private float handle(ShaderNodeDefinition var1, ShaderGraphBlock var2) {
      float var3 = this.process(var1);
      if (var2 != null && var1.prepare()) {
         SpringFloat var4 = this.refresh(var2.handle());
         return var3 + var4.handle() * 128.0F;
      } else {
         return var3;
      }
   }

   private SpringFloat refresh(String var1) {
      return this.playerUpdate
         .computeIfAbsent(var1, var0 -> new SpringFloat(AnimationClock.handle(), SpringParameters.handle(2.6F, 0.78F), 0.0F, 0.0F, 1.0F, 0.001F, 0.001F));
   }

   private RectBounds handle(ShaderNodeDefinition var1, float var2, float var3, float var4) {
      float var5 = Math.max(52.0F * this.animationDraw, 0.0F);
      float var6 = Math.max(16.0F * this.animationDraw, 0.0F);
      return new RectBounds(var2 + var4 - var5 - 10.0F * this.animationDraw, var3 + 12.0F * this.animationDraw, var5, var6);
   }

   private float handle(int var1) {
      return 64.0F + var1 * 26.0F;
   }

   private float compute(float var1) {
      return this.renderer + var1 * this.animationDraw;
   }

   private float resolve(float var1) {
      return this.handler + var1 * this.animationDraw;
   }

   private float update(float var1) {
      return (var1 - this.renderer) / Math.max(0.001F, this.animationDraw);
   }

   private float apply(float var1) {
      return (var1 - this.handler) / Math.max(0.001F, this.animationDraw);
   }

   private RectBounds process(GuiMetrics var1, int var2) {
      return new RectBounds(var1.handle(42.0F), var1.handle(106.0F), var1.handle(232.0F), var2 - var1.handle(148.0F));
   }

   private RectBounds compute(GuiMetrics var1, int var2) {
      return this.entryAnimate
         ? new RectBounds(var1.handle(42.0F), var1.handle(106.0F), var1.handle(28.0F), var1.handle(28.0F))
         : new RectBounds(var1.handle(42.0F) + var1.handle(232.0F) - var1.handle(34.0F), var1.handle(106.0F), var1.handle(28.0F), var1.handle(28.0F));
   }

   private RectBounds handle(GuiMetrics var1, int var2, int var3) {
      float var4 = Math.min(var1.handle(342.0F), Math.max(var1.handle(286.0F), var2 * 0.25F));
      float var5 = Math.min(var1.handle(292.0F), Math.max(var1.handle(220.0F), var3 * 0.3F));
      return new RectBounds(var2 - var4 - var1.handle(42.0F), var1.handle(104.0F), var4, var5);
   }

   private RectBounds handle(RectBounds var1, GuiMetrics var2, int var3) {
      float var4 = var2.handle(27.0F);
      float var5 = var1.y() + var2.handle(74.0F) + var3 * var4;
      float var6 = var1.x() + var2.handle(90.0F);
      return new RectBounds(var6, var5, var1.w() - var2.handle(104.0F), var2.handle(20.0F));
   }

   private RectBounds process(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var1.w() - var2.handle(108.0F), var1.y() + var2.handle(14.0F), var2.handle(92.0F), var2.handle(22.0F));
   }

   private ShaderGraphBlock projectItem() {
      this.select();
      return this.profileDraw == null ? null : this.enabled.compute(this.profileDraw);
   }

   private String process(ShaderGraphBlock var1, String var2) {
      return var1.handle() + ":" + var2;
   }

   private ShaderGraphNode handle(String var1, float var2, float var3, float var4) {
      ShaderGraphNode var5 = this.playerCollapse.computeIfAbsent(var1, var2x -> ShaderGraphNode.handle(var2, var3));
      var5.process(var2, var3);
      float var6 = Math.max(2.0E-4F, Math.min(1.0F, Math.abs(var4) * 1.8F));
      var5.compute(var6, var6 * 0.1F);
      return var5;
   }

   private ShaderGraphNode render(String var1) {
      return this.playerCollapse.computeIfAbsent(var1, var0 -> ShaderGraphNode.handle());
   }

   private RectBounds compute(ShaderGraphBlock var1) {
      ShaderNodeDefinition var2 = this.cache.handle(var1.process());
      if (var2 == null) {
         return new RectBounds(0.0F, 0.0F, 0.0F, 0.0F);
      }

      float var3 = this.compute(var1.compute()) + 14.0F * this.animationDraw;
      float var4 = this.resolve(var1.resolve()) + 78.0F * this.animationDraw;
      float var5 = Math.max(1.0F, (var2.resolve() - 28.0F) * this.animationDraw);
      float var6 = Math.max(1.0F, 18.0F * this.animationDraw);
      return new RectBounds(var3, var4, var5, var6);
   }

   private RectBounds resolve(ShaderGraphBlock var1) {
      ShaderNodeDefinition var2 = this.cache.handle(var1.process());
      if (var2 == null) {
         return new RectBounds(0.0F, 0.0F, 0.0F, 0.0F);
      }

      float var3 = this.compute(var1.compute()) + 14.0F * this.animationDraw;
      float var4 = this.resolve(var1.resolve()) + 78.0F * this.animationDraw;
      float var5 = Math.max(1.0F, (var2.resolve() - 28.0F) * this.animationDraw);
      float var6 = Math.max(1.0F, 18.0F * this.animationDraw);
      return new RectBounds(var3, var4, var5, var6);
   }

   private RectBounds process(GuiMetrics var1, int var2, int var3) {
      float var4 = Math.min(var1.handle(340.0F), var2 * 0.31F);
      float var5 = Math.min(var1.handle(232.0F), var3 * 0.3F);
      if (!this.inputAcquire) {
         this.listenerRun = var2 - var4 - var1.handle(42.0F);
         this.indexLoad = var3 - var5 - var1.handle(42.0F);
         this.inputAcquire = true;
      }

      this.listenerRun = process(this.listenerRun, var1.handle(24.0F), Math.max(var1.handle(24.0F), var2 - var4 - var1.handle(24.0F)));
      this.indexLoad = process(this.indexLoad, var1.handle(94.0F), Math.max(var1.handle(94.0F), var3 - var5 - var1.handle(24.0F)));
      return new RectBounds(this.listenerRun, this.indexLoad, var4, var5);
   }

   private boolean update(ShaderGraphBlock var1) {
      return var1 != null && ("exposed_float".equals(var1.process()) || "exposed_color".equals(var1.process()));
   }

   private String apply(ShaderGraphBlock var1) {
      return var1 != null && "exposed_color".equals(var1.process()) ? "Color" : "Radius";
   }

   private boolean computeResponse() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.getWindow() != null ? GLFW.glfwGetKey(var1.getWindow().getHandle(), 32) == 1 : false;
   }

   private int fetchProvider() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.getWindow() != null ? Math.max(1, var1.getWindow().getFramebufferWidth()) : 1;
   }

   private int drawProfile() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.getWindow() != null ? Math.max(1, var1.getWindow().getFramebufferHeight()) : 1;
   }

   private GuiMetrics performVector() {
      return this.outputFetch != null ? this.outputFetch : GuiMetrics.handle(this.fetchProvider(), this.drawProfile(), GuiLayoutSpec.handle());
   }

   private RectBounds resolve(GuiMetrics var1, int var2) {
      float var3 = var1.handle(28.0F);
      return new RectBounds(var2 - this.apply(var1) - var1.handle(74.0F), var1.handle(46.0F), var1.handle(74.0F), var3);
   }

   private RectBounds update(GuiMetrics var1, int var2) {
      float var3 = var1.handle(28.0F);
      return new RectBounds(this.resolve(var1, var2).x() - var1.handle(10.0F) - var1.handle(96.0F), var1.handle(46.0F), var1.handle(96.0F), var3);
   }

   private RectBounds handle(GuiMetrics var1) {
      return new RectBounds(var1.handle(34.0F) + var1.handle(200.0F), var1.handle(46.0F), var1.handle(84.0F), var1.handle(28.0F));
   }

   private RectBounds process(GuiMetrics var1) {
      RectBounds var2 = this.handle(var1);
      return new RectBounds(var2.x() + var2.w() + var1.handle(10.0F), var1.handle(46.0F), var1.handle(220.0F), var1.handle(28.0F));
   }

   private RectBounds compute(GuiMetrics var1) {
      RectBounds var2 = this.process(var1);
      return new RectBounds(var2.x() + var2.w() + var1.handle(10.0F), var1.handle(46.0F), var1.handle(136.0F), var1.handle(28.0F));
   }

   private RectBounds apply(GuiMetrics var1, int var2) {
      RectBounds var3 = this.update(var1, var2);
      return new RectBounds(var3.x() - var1.handle(10.0F) - var1.handle(110.0F), var1.handle(46.0F), var1.handle(110.0F), var1.handle(28.0F));
   }

   private RectBounds compute(GuiMetrics var1, int var2, int var3) {
      RectBounds var4 = this.apply(var1, var2);
      float var5 = Math.min(var1.handle(520.0F), Math.max(var1.handle(420.0F), var2 * 0.3F));
      float var6 = process(var4.x() + var4.w() - var5, var1.handle(42.0F), var2 - var5 - var1.handle(42.0F));
      float var7 = var4.y() + var4.h() + var1.handle(10.0F);
      float var8 = Math.min(var1.handle(520.0F), Math.max(var1.handle(220.0F), var3 - var7 - var1.handle(34.0F)));
      return new RectBounds(var6, var7, var5, var8);
   }

   private RectBounds compute(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var2.handle(10.0F), var1.y() + var2.handle(74.0F), var1.w() - var2.handle(20.0F), var1.h() - var2.handle(84.0F));
   }

   private RectBounds process(RectBounds var1, GuiMetrics var2, int var3) {
      float var4 = var2.handle(8.0F);
      float var5 = (var1.w() - var2.handle(20.0F) - var4 * 2.0F) / 3.0F;
      return new RectBounds(var1.x() + var2.handle(10.0F) + var3 * (var5 + var4), var1.y() + var2.handle(42.0F), var5, var2.handle(24.0F));
   }

   private float resolve(GuiMetrics var1) {
      return var1.handle(68.0F);
   }

   private RectBounds resolve(RectBounds var1, GuiMetrics var2) {
      float var3 = var2.handle(24.0F);
      return new RectBounds(var1.x() + var1.w() - var3 - var2.handle(10.0F), var1.y() + var2.handle(10.0F), var3, var3);
   }

   private RectBounds update(RectBounds var1, GuiMetrics var2) {
      float var3 = var1.h() - var2.handle(14.0F);
      float var4 = var2.handle(78.0F);
      return new RectBounds(var1.x() + var2.handle(26.0F), var1.y() + var2.handle(7.0F), var4, var3);
   }

   private RectBounds handle(RectBounds var1, GuiMetrics var2, float var3) {
      float var4 = var2.handle(48.0F);
      float var5 = var2.handle(24.0F);
      RectBounds var6 = this.process(var1, var2, var3);
      return new RectBounds(var6.x() - var2.handle(8.0F) - var4, var3 + var2.handle(14.0F), var4, var5);
   }

   private RectBounds process(RectBounds var1, GuiMetrics var2, float var3) {
      float var4 = var2.handle(24.0F);
      return new RectBounds(var1.x() + var1.w() - var4 - var2.handle(18.0F), var3 + var2.handle(14.0F), var4, var4);
   }

   private RectBounds compute(RectBounds var1, GuiMetrics var2, float var3) {
      RectBounds var4 = this.resolve(var1, var2, var3);
      float var5 = var2.handle(52.0F);
      return new RectBounds(var4.x() - var2.handle(8.0F) - var5, var3 + var2.handle(19.0F), var5, var2.handle(24.0F));
   }

   private RectBounds resolve(RectBounds var1, GuiMetrics var2, float var3) {
      float var4 = var2.handle(58.0F);
      return new RectBounds(var1.x() + var1.w() - var4 - var2.handle(18.0F), var3 + var2.handle(19.0F), var4, var2.handle(24.0F));
   }

   private RectBounds update(GuiMetrics var1) {
      RectBounds var2 = this.handle(var1);
      float var3 = var1.handle(320.0F);
      return new RectBounds(var2.x(), var2.y() + var2.h() + var1.handle(10.0F), var3, var1.handle(300.0F));
   }

   private RectBounds compute(RectBounds var1, GuiMetrics var2, int var3) {
      float var4 = var2.handle(7.0F);
      float var5 = (var1.w() - var2.handle(24.0F) - var4) * 0.5F;
      float var6 = var2.handle(28.0F);
      int var7 = var3 & 1;
      int var8 = var3 >> 1;
      return new RectBounds(var1.x() + var2.handle(12.0F) + var7 * (var5 + var4), var1.y() + var2.handle(164.0F) + var8 * (var6 + var4), var5, var6);
   }

   private RectBounds resolve(GuiMetrics var1, int var2, int var3) {
      RectBounds var4 = this.compute(var1);
      float var5 = Math.min(var1.handle(520.0F), var2 - var1.handle(84.0F));
      float var6 = Math.min(var1.handle(252.0F), var3 - var4.y() - var4.h() - var1.handle(34.0F));
      float var7 = process(var4.x() + var4.w() - var5, var1.handle(42.0F), var2 - var5 - var1.handle(42.0F));
      return new RectBounds(var7, var4.y() + var4.h() + var1.handle(10.0F), var5, var6);
   }

   private RectBounds resolve(RectBounds var1, GuiMetrics var2, int var3) {
      float var4 = var2.handle(8.0F);
      float var5 = (var1.w() - var2.handle(24.0F) - var4) * 0.5F;
      float var6 = var2.handle(42.0F);
      int var7 = var3 & 1;
      int var8 = var3 >> 1;
      return new RectBounds(var1.x() + var2.handle(12.0F) + var7 * (var5 + var4), var1.y() + var2.handle(52.0F) + var8 * (var6 + var4), var5, var6);
   }

   private RectBounds update(RectBounds var1, GuiMetrics var2, int var3) {
      float var4 = var2.handle(7.0F);
      float var5 = (var1.w() - var2.handle(24.0F) - var4 * 2.0F) / 3.0F;
      return new RectBounds(var1.x() + var2.handle(12.0F) + var3 * (var5 + var4), var1.y() + var1.h() - var2.handle(40.0F), var5, var2.handle(26.0F));
   }

   private RectBounds execute(GuiMetrics var1, int var2) {
      RectBounds var3 = this.update(var1, var2);
      return new RectBounds(var3.x() - var1.handle(150.0F), var3.y() + var3.h() + var1.handle(10.0F), var1.handle(300.0F), var1.handle(236.0F));
   }

   private RectBounds apply(RectBounds var1, GuiMetrics var2, int var3) {
      float var4 = var2.handle(8.0F);
      float var5 = (var1.w() - var2.handle(32.0F) - var4 * 2.0F) / 3.0F;
      return new RectBounds(var1.x() + var2.handle(16.0F) + var3 * (var5 + var4), var1.y() + var2.handle(84.0F), var5, var2.handle(26.0F));
   }

   private RectBounds update(GuiMetrics var1, int var2, int var3) {
      float var4 = var1.handle(430.0F);
      float var5 = var1.handle(148.0F);
      return new RectBounds((var2 - var4) * 0.5F, (var3 - var5) * 0.5F, var4, var5);
   }

   private RectBounds apply(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var2.handle(18.0F), var1.y() + var1.h() - var2.handle(44.0F), var2.handle(132.0F), var2.handle(30.0F));
   }

   private RectBounds execute(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var2.handle(160.0F), var1.y() + var1.h() - var2.handle(44.0F), var2.handle(112.0F), var2.handle(30.0F));
   }

   private RectBounds prepare(RectBounds var1, GuiMetrics var2) {
      return new RectBounds(var1.x() + var1.w() - var2.handle(118.0F), var1.y() + var1.h() - var2.handle(44.0F), var2.handle(100.0F), var2.handle(30.0F));
   }

   private float apply(GuiMetrics var1) {
      return var1.handle(64.0F);
   }

   private void attachEvent() {
      this.scaleAdapt = false;
      this.textureRun = false;
      this.indexBind = false;
      this.configCollapse = false;
      this.stateApply = false;
      this.animationExpand = false;
   }

   private void readServer() {
      try {
         this.actionConvert.handle(this.advancePosition());
      } catch (Throwable var2) {
      }
   }

   private String advancePosition() {
      JSONObject var1 = ThemeKeys.process(this.enabled);
      JSONObject var2 = var1.optJSONObject("metadata");
      if (var2 != null) {
         var2.put("updatedAt", 0L);
         var2.put("source", "");
      }

      return var1.toString();
   }

   private void tick(String var1) {
      this.enabled = ThemeKeys.handle(new JSONObject(var1), this.cache);
      this.scheduleAnimation();
      LivePreviewRenderer var2 = LivePreviewRenderer.handle(this.enabled.process());
      if (var2 != null && var2 != LivePreviewRenderer.PREVIEW_ONLY) {
         this.colorCompute = var2;
      }

      this.update(this.colorCompute);
      this.current.handle(this.colorCompute);
      this.current.handle(this.enabled);
      this.check();
      this.matrixRender.keySet().removeIf(var1x -> this.enabled.compute(var1x) == null);
      this.moduleTick.keySet().removeIf(var1x -> this.enabled.compute(var1x) == null);
      this.playerCollapse.keySet().removeIf(var1x -> {
         int var2x = var1x.indexOf(58);
         String var3 = var2x > 0 ? var1x.substring(0, var2x) : var1x;
         return this.enabled.compute(var3) == null;
      });
      this.bufferAdapt.keySet().removeIf(var1x -> this.enabled.compute(var1x) == null);
      this.playerUpdate.keySet().removeIf(var1x -> this.enabled.compute(var1x) == null);
      this.optionAdvance = null;
      this.effectScan = null;
      this.optionParse = null;
      this.itemProject = null;
      this.colorMeasure = null;
      this.animationSchedule = null;
      if (!this.enabled.handle().process().isBlank()) {
         this.screenRead = this.enabled.handle().process();
      }
   }

   private void checkFrame() {
      try {
         String var1 = this.actionConvert.process(this.advancePosition());
         if (var1 == null) {
            this.drawAnimation("nothing to undo");
            return;
         }

         this.tick(var1);
         this.drawAnimation("undo");
      } catch (Throwable var2) {
         this.drawAnimation("undo failed");
      }
   }

   private void collectModule() {
      try {
         String var1 = this.actionConvert.compute(this.advancePosition());
         if (var1 == null) {
            this.drawAnimation("nothing to redo");
            return;
         }

         this.tick(var1);
         this.drawAnimation("redo");
      } catch (Throwable var2) {
         this.drawAnimation("redo failed");
      }
   }

   private void closeProvider() {
      this.readServer();
      this.enabled = FoundryPresetFactory.handle(this.cache);
      this.screenRead = ShaderPresetNameGenerator.handle();
      this.enabled.handle().handle(this.screenRead, FoundryStorage.execute());
      this.enabled.handle().handle(this.screenRead);
      this.enabled.handle().apply("Host Rectangle");
      this.scheduleAnimation();
      this.update(this.colorCompute);
      this.check();
      this.onTick();
      this.matrixBlend2 = null;
      this.current.close();
      this.contextExpand = this.enabled.update();
      this.update();
      this.drawAnimation("reset");
   }

   private void compute(LivePreviewRenderer var1) {
      if (var1 != null && var1 != this.colorCompute) {
         if (this.enabled != null && this.enabled.update() != this.contextExpand && !this.enabled.compute().isEmpty()) {
            this.actionRead = var1;
            this.configCollapse = true;
         } else {
            this.resolve(var1);
         }
      }
   }

   private void resolve(LivePreviewRenderer var1) {
      if (var1 != null) {
         this.readServer();
         this.colorCompute = var1;
         this.update(var1);
         this.scanRenderer();
         this.current.handle(var1);
         this.current.handle(this.enabled);
         this.drawAnimation(var1.process());
      }
   }

   private void process(boolean var1) {
      if (!FoundryTemplateRegistry.instance.isEmpty()) {
         int var2 = Math.max(0, Math.min(FoundryTemplateRegistry.instance.size() - 1, this.clientRefresh));
         this.readServer();
         FoundryTemplateRegistry.CacheEntry var3 = FoundryTemplateRegistry.instance.get(var2);
         ShaderGraph var4 = FoundryTemplateRegistry.handle(var3, this.cache);
         if (var4 != null) {
            if (var1) {
               this.process(var4);
               this.drawAnimation("merged " + var3.instance);
            } else {
               this.enabled = var4;
               this.scheduleAnimation();
               LivePreviewRenderer var5 = LivePreviewRenderer.handle(this.enabled.process());
               if (var5 != LivePreviewRenderer.PREVIEW_ONLY) {
                  this.colorCompute = var5;
               }

               this.screenRead = this.enabled.handle().process().isBlank() ? var3.instance : this.enabled.handle().process();
               this.enabled.handle().handle(this.screenRead, FoundryStorage.execute());
               this.matrixBlend2 = null;
               this.check();
               this.onTick();
               this.current.close();
               this.current.handle(this.colorCompute);
               this.current.handle(this.enabled);
               this.pointEncode = 0.78F;
               this.animator.handle(0.78F);
               this.renderer = 720.0F;
               this.handler = 360.0F;
               this.contextExpand = -1;
               this.update();
               this.drawAnimation("template: " + var3.instance);
            }
         }
      }
   }

   private void process(ShaderGraph var1) {
      if (var1 != null) {
         HashMap var2 = new HashMap();
         float var3 = this.performVector().handle(80.0F);
         float var4 = this.performVector().handle(80.0F);

         for (ShaderGraphBlock var6 : var1.compute()) {
            ShaderGraphBlock var7 = this.enabled.handle(var6.process(), var6.compute() + var3, var6.resolve() + var4, this.cache);
            var7.apply().putAll(var6.apply());
            var7.execute().putAll(var6.execute());
            var2.put(var6.handle(), var7.handle());
         }

         for (ShaderGraphLink var10 : var1.resolve()) {
            String var11 = (String)var2.get(var10.handle());
            String var8 = (String)var2.get(var10.compute());
            if (var11 != null && var8 != null) {
               this.enabled.handle(var11, var10.process(), var8, var10.resolve(), this.cache);
            }
         }

         this.enabled.apply();
      }
   }

   private void savePreset() {
      try {
         File var1 = FoundryStorage.handle().compute();
         if (!var1.exists()) {
            var1.mkdirs();
         }

         Util.getOperatingSystem().open(var1);
         this.drawAnimation("opened folder");
      } catch (Throwable var2) {
         this.drawAnimation("open folder failed");
      }
   }

   private String handle(long var1) {
      return var1 <= 0L ? "-" : instance.format(new Date(var1));
   }

   private void convertWindow() {
      try {
         this.update(this.colorCompute);
         this.scanRenderer();
         File var1 = FoundryStorage.handle().process(this.colorCompute, this.enabled, this.apply());
         if (var1 != null) {
            this.drawAnimation("exported -> " + var1.getName());
         } else {
            this.drawAnimation("export failed");
         }
      } catch (Throwable var2) {
         this.drawAnimation("export failed");
      }
   }

   private void writePreset() {
      try {
         List var1 = FoundryStorage.handle().update();
         this.selection.handle(var1);
         this.drawAnimation(var1.isEmpty() ? "no shader files" : "import");
      } catch (Throwable var2) {
         this.drawAnimation("import failed");
      }
   }

   private void measureColor() {
      File var1 = this.selection.compute();
      if (var1 != null) {
         try {
            ShaderGraph var2 = FoundryStorage.handle().handle(var1, this.cache);
            if (var2 == null) {
               this.drawAnimation("import failed");
               return;
            }

            this.readServer();
            this.enabled = var2;
            this.scheduleAnimation();
            LivePreviewRenderer var3 = LivePreviewRenderer.handle(this.enabled.process());
            this.colorCompute = var3 == LivePreviewRenderer.PREVIEW_ONLY ? this.colorCompute : var3;
            this.update(this.colorCompute);
            this.current.handle(this.colorCompute);
            this.screenRead = this.enabled.handle().process().isBlank()
               ? update(var1.getName().replace(".wifd", "").replace(".json", ""))
               : this.enabled.handle().process();
            this.check();
            this.onTick();
            this.matrixBlend2 = null;
            this.current.close();
            this.contextExpand = -1;
            this.update();
            this.drawAnimation("imported " + var1.getName());
         } catch (Throwable var4) {
            this.drawAnimation("import failed");
         }
      }
   }

   private void update(LivePreviewRenderer var1) {
      if (this.enabled != null && var1 != null) {
         this.enabled.handle(var1.handle());
      }
   }

   private void scheduleAnimation() {
      this.dataValidate = this.enabled != null && this.enabled.handle() != null ? this.enabled.handle().execute() : "Host Rectangle";
   }

   private void scanRenderer() {
      if (this.enabled != null && this.enabled.handle() != null) {
         this.enabled.handle().apply(this.dataValidate);
      }
   }

   private void drawAnimation(String var1) {
      this.timerRender = var1 != null && !var1.isBlank() ? var1 : "ready";
      this.scaleSave = System.currentTimeMillis() + 1500L;
   }

   private void execute(float var1, float var2) {
      long var3 = System.nanoTime();
      if (this.sourceSchedule != 0L) {
         float var5 = Math.max(0.001F, Math.min(0.05F, (float)(var3 - this.sourceSchedule) / 1.0E9F));
         this.outputCollapse = (var1 - this.rendererScan) / var5;
         this.profileInvoke = (var2 - this.sourceBuild) / var5;
      }

      this.sourceSchedule = var3;
      this.rendererScan = var1;
      this.sourceBuild = var2;
      if (System.currentTimeMillis() > this.scaleSave && this.current.process().isBlank()) {
         this.timerRender = "ready";
      }
   }

   private static float execute(float var0) {
      float var1 = process(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   @Override
   public void close() {
      this.current.close();
      this.mode.close();
   }

   record CachedDataRecord(FoundryWorkspace.PersistentDataRecord row, boolean star) {
   }

   record DataRecord(float x, float y) {
   }

   record FallbackDataRecord(SavedThemePreset slot, int presetIndex) {
   }

   enum Mode {
      AUTO("Auto"),
      DARK("Dark"),
      LIGHT("Light");

      private final String instance;

      Mode(String var3) {
         this.instance = var3;
      }

      String handle() {
         return this.instance;
      }
   }

   record PersistentDataRecord(int type, String category, ShaderNodeDefinition def, int count) {
   }

   record PrimaryDataRecord(float x, float y) {
   }

   record RuntimeDataRecord(int a, int b) {
   }

   record SecondaryDataRecord(String nodeId, String pinId, ShaderPinDirection direction) {
   }
}
