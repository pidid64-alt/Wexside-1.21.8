package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.HeadlessBotSession;
import ru.wild.config.StudioProfileGate;
import ru.wild.core.manager.FeatureManager;
import ru.wild.gui.theme.FoundryPresetLibrary;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeManager;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.widget.ModulePanelRegistry;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.shader.TransitionShader;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;

public final class ModernClickGuiState {
   public static final int instance = 96;
   static final SpringAnimation.State data = new SpringAnimation.State(18.0F, 2.55F, 0.0015F, 0.08F);
   static final long context = 1280L;
   static final long config = 420L;
   static final long state = 620L;
   private final Set<Module> cache = new HashSet<>();
   private final Map<String, SpringAnimation> output = new HashMap<>();
   private final SpringAnimation current = new SpringAnimation(0.0F);
   private final SpringAnimation active = new SpringAnimation(0.0F);
   private final SpringAnimation mode = new SpringAnimation(0.0F);
   private final SpringAnimation selection = new SpringAnimation(1.0F);
   private final Map<Integer, Long> enabled = new HashMap<>();
   private ModuleCategory renderer = ModuleCategory.Combat;
   private HeadlessBotSession handler;
   private boolean animationDraw;
   private boolean pointEncode;
   private boolean animator;
   private boolean source;
   private long target;
   private ThemePalette pending = ThemePalette.WILD;
   private boolean previous;
   private String latest = "";
   private boolean summary;
   private String matrixBlend = "";
   private boolean vectorMatch;
   private List<Integer> itemProject = List.of();
   private String responseCompute;
   private int providerFetch = -1;
   private boolean profileDraw;
   private float vectorPerform;
   private float eventAttach;
   private float serverRead;
   private float positionAdvance;
   private float frameCheck;
   private float moduleCollect;
   private float providerClose;
   private float presetSave;
   private float windowConvert;
   private float presetWrite;
   private float colorMeasure;
   private float animationSchedule;
   private float rendererScan;
   private float sourceBuild;
   private float outputCollapse;
   private float profileInvoke;
   private float sourceSchedule;
   private boolean timerRender;
   private boolean scaleSave;
   private float colorCompute;
   private float scaleAdapt;
   private long textureRun;
   private long indexBind;
   private long actionRead;
   private long configCollapse;
   private long dataValidate;
   private boolean scaleRender;
   private boolean clientRefresh;
   private boolean keyFilter;
   private boolean requestAdapt;
   private boolean timerMeasure;
   private float vectorEncode;
   private float requestReceive;
   private float windowProcess = 1.0F;
   private float packetSave;
   private float entryAnimate;
   private boolean playerCollect;
   private boolean stateApply;
   private float matrixFilter;
   private float layerSample;
   private float worldSend = 1.0F;
   private float targetWrite;
   private float resultEncode;
   private boolean messageParse;
   private NumberSetting providerRead;
   private ColorSetting matrixBlend2;
   private float scalePerform;
   private float contextExpand;
   private Module keyProcess;
   private KeybindSetting actionConvert;
   private BooleanSetting screenRead;
   private StringSetting animationExpand;
   private ModeSetting playerRun;
   private ShaderPresetSetting matrixRender;
   private int moduleTick = -1;
   private ThemeColors playerCollapse;
   private ThemeColors optionAdvance;
   private ThemePalette effectScan = ThemePalette.WILD;
   private float optionParse;
   private float pointSubmit;
   private long listenerPerform;
   private long configMatch;
   private ColorSetting actionRender;
   private boolean playerApply;
   private boolean bufferAdapt;
   private boolean playerUpdate;
   private float packetRead;
   private float rendererCancel;
   private float eventReceive;
   private float screenSubmit;
   private float cacheHandle;
   private float rangeRelease;
   private float indexSave;
   private float indexCheck;
   private float settingSchedule;
   private float inputAcquire;
   private float listenerRun;
   private float indexLoad;
   private float layoutSave;
   private float blockRun;
   private float playerEvaluate;
   private float outputFetch;
   private float scaleParse;
   private float sessionEncode;
   private float elementTick;
   private float regionAlign;
   private float resourceClamp;
   private float handlerRun;
   private float keyCheck;
   private float layerProject;
   private float entityFilter;
   private float layerSample2;
   private float sourceCancel;
   private float eventSend;
   private float providerOffset;
   private float messageParse2;
   private float shaderProject;
   private float inputInvoke;
   private float optionFetch;
   private float eventCollapse;
   private final Map<ColorSetting, Integer> stateAttach = new HashMap<>();
   private ColorSetting worldEvaluate;
   private String playerProject = "";
   private ColorSetting playerMatch;
   private String cacheClose = "";
   private boolean scaleSetup;
   private boolean indexSynchronize;
   private boolean taskInterpolate;
   private boolean sourceRefresh;
   private String playerSave;
   private float requestRun;
   private float frameProject;
   private long dataRelease;
   private String vectorRun;
   private static final char[] providerSynchronize = new char[65535];

   public void handle() {
      this.collapseOutput();
      if (WildClient.instance != null && WildClient.instance.selection != null) {
         this.renderer = WildClient.instance.selection.compute();
         this.pending = WildClient.instance.selection.process();
         this.effectScan = this.pending;
         if (WildClient.instance.selection.resolve()) {
            this.frameCheck = WildClient.instance.selection.update();
            this.moduleCollect = WildClient.instance.selection.apply();
            this.clientRefresh = true;
         }

         this.indexSynchronize = WildClient.instance.selection.execute() && WildClient.instance.selection.prepare();
      }

      this.moduleTick = -1;
      this.handler = null;
      this.collectModule();
   }

   public void process() {
      this.profileDraw = false;
      this.current.handle(0.0F);
      this.textureRun = System.currentTimeMillis();
      this.actionRead = this.textureRun;
   }

   public void compute() {
      this.profileDraw = true;
      this.summary = false;
      this.vectorMatch = false;
      this.collectModule();
      this.indexBind = System.currentTimeMillis();
   }

   public float handle(SpringAnimationSpec var1) {
      return this.current.handle(this.profileDraw ? 0.0F : 1.0F, var1);
   }

   public float resolve() {
      return this.current.process();
   }

   public boolean process(SpringAnimationSpec var1) {
      return this.profileDraw && this.current.process(0.0F, var1);
   }

   public List<Module> update() {
      this.collapseOutput();
      if (!this.animationDraw && !this.pointEncode && !this.animator && !this.source) {
         HeadlessBotSession var1 = this.execute();
         if (var1 != null) {
            List<Module> var2 = var1.refresh().process();
            return this.latest != null && !this.latest.isBlank() ? var2 : var2.stream().filter(var1x -> var1x.category == this.renderer).toList();
         } else if (WildClient.instance == null || WildClient.instance.data == null) {
            return List.of();
         } else {
            return this.latest != null && !this.latest.isBlank()
               ? WildClient.instance.data.process().stream().filter(var0 -> !(var0 instanceof AutoBuy)).toList()
               : WildClient.instance.data.handle(this.renderer).stream().filter(var0 -> !(var0 instanceof AutoBuy)).toList();
         }
      } else {
         return List.of();
      }
   }

   public List<Module> apply() {
      this.collapseOutput();
      if (!this.animationDraw && !this.pointEncode && !this.animator && !this.source) {
         HeadlessBotSession var1 = this.execute();
         if (var1 != null) {
            List<Module> var4 = var1.refresh().process();
            if (this.latest != null && !this.latest.isBlank()) {
               String var3 = this.latest.toLowerCase(Locale.ROOT).trim();
               return var4.stream().filter(var2x -> this.handle(var2x, var3)).toList();
            } else {
               return var4.stream().filter(var1x -> var1x.category == this.renderer).toList();
            }
         } else if (WildClient.instance == null || WildClient.instance.data == null) {
            return List.of();
         } else if (this.latest != null && !this.latest.isBlank()) {
            String var2 = this.latest.toLowerCase(Locale.ROOT).trim();
            return WildClient.instance.data.process().stream().filter(var0 -> !(var0 instanceof AutoBuy)).filter(var2x -> this.handle(var2x, var2)).toList();
         } else {
            return WildClient.instance.data.handle(this.renderer).stream().filter(var0 -> !(var0 instanceof AutoBuy)).toList();
         }
      } else {
         return List.of();
      }
   }

   public HeadlessBotSession execute() {
      HeadlessBotSession var1 = this.handler;
      if (var1 != null && !HeadlessBotEngine.instance.contains(var1)) {
         this.handler = null;
         return null;
      } else {
         return this.handler;
      }
   }

   public String prepare() {
      HeadlessBotSession var1 = this.execute();
      return var1 == null ? "Host" : var1.handle();
   }

   public void handle(int var1) {
      ArrayList var2 = new ArrayList<>(HeadlessBotEngine.instance);
      int var3 = var2.size() + 1;
      HeadlessBotSession var4 = this.execute();
      int var5 = var4 == null ? 0 : Math.max(0, var2.indexOf(var4) + 1);
      int var6 = ((var5 + var1) % var3 + var3) % var3;
      this.handler = var6 == 0 ? null : (HeadlessBotSession)var2.get(var6 - 1);
      this.fetchState();
   }

   private void fetchState() {
      this.filterTask();
      this.cache.clear();
      this.computeResponse();
      this.fetchProvider();
      this.drawProfile();
      this.handle((StringSetting)null);
      this.drawAnimation();
   }

   public void handle(ModuleCategory var1) {
      this.scaleSetup = false;
      this.animationDraw = false;
      this.pointEncode = false;
      this.animator = false;
      this.source = false;
      this.renderer = var1;
      this.filterTask();
      this.save();
      this.summary = false;
      this.handle((StringSetting)null);
      this.providerRead = null;
      this.matrixBlend2 = null;
      this.actionRead = System.currentTimeMillis();
      this.computeResponse();
      this.fetchProvider();
      this.drawProfile();
      this.drawAnimation();
      if (WildClient.instance != null && WildClient.instance.selection != null) {
         WildClient.instance.selection.handle(var1);
      }
   }

   public void check() {
      this.scaleSetup = false;
      this.animationDraw = true;
      this.pointEncode = false;
      this.animator = false;
      this.source = false;
      this.filterTask();
      this.save();
      this.summary = false;
      this.handle((StringSetting)null);
      this.providerRead = null;
      this.matrixBlend2 = null;
      this.actionRead = System.currentTimeMillis();
      this.configCollapse = this.actionRead;
      this.computeResponse();
      this.fetchProvider();
      this.drawProfile();
      this.drawAnimation();
   }

   public void onTick() {
      this.scaleSetup = false;
      this.animationDraw = false;
      this.pointEncode = true;
      this.animator = false;
      this.source = false;
      this.target++;
      this.filterTask();
      this.save();
      this.summary = false;
      this.handle((StringSetting)null);
      this.providerRead = null;
      this.matrixBlend2 = null;
      this.actionRead = System.currentTimeMillis();
      this.dataValidate = this.actionRead;
      this.computeResponse();
      this.fetchProvider();
      this.drawProfile();
      this.drawAnimation();
      this.attachItem();
   }

   public void select() {
      if (this.pointEncode) {
         this.pointEncode = false;
         this.save();
         this.actionRead = System.currentTimeMillis();
      } else {
         this.onTick();
      }
   }

   public void refresh() {
      if (StudioProfileGate.handle()) {
         this.scaleSetup = false;
         this.animationDraw = false;
         this.pointEncode = false;
         this.animator = true;
         this.source = false;
         this.filterTask();
         this.save();
         this.summary = false;
         this.handle((StringSetting)null);
         this.providerRead = null;
         this.matrixBlend2 = null;
         this.actionRead = System.currentTimeMillis();
         this.computeResponse();
         this.fetchProvider();
         this.drawProfile();
         this.drawAnimation();
         FoundryPresetLibrary.handle().compute();
      }
   }

   public void render() {
      if (this.animator) {
         this.animator = false;
         this.handle(this.renderer);
      } else {
         this.refresh();
      }
   }

   public void tick() {
      this.scaleSetup = false;
      this.animationDraw = false;
      this.pointEncode = false;
      this.animator = false;
      this.source = true;
      this.filterTask();
      this.save();
      this.summary = false;
      this.handle((StringSetting)null);
      this.providerRead = null;
      this.matrixBlend2 = null;
      this.actionRead = System.currentTimeMillis();
      this.computeResponse();
      this.fetchProvider();
      this.drawProfile();
      this.drawAnimation();
   }

   private void attachItem() {
      SpringAnimation var1 = this.output.get(UiAnimationKeys.check());
      if (var1 == null) {
         this.output.put(UiAnimationKeys.check(), new SpringAnimation(0.0F));
      } else {
         var1.handle(0.0F);
      }

      SpringAnimation var2 = this.output.get(UiAnimationKeys.onTick());
      if (var2 != null) {
         var2.handle(0.0F);
      }
   }

   public void drawAnimation() {
      this.output.entrySet().removeIf(var0 -> {
         String var1 = var0.getKey();
         return var1.startsWith("module:card:transition:") || var1.startsWith("module:card:entry:") || var1.startsWith("module:svis:");
      });
   }

   public void handle(ThemePalette var1, int var2) {
      if (this.pending != var1) {
         this.effectScan = this.pending;
         if (Menu.handle(Menu.vectorPerform)) {
            this.optionParse = this.vectorPerform;
            this.pointSubmit = this.eventAttach;
            this.listenerPerform = System.currentTimeMillis();
         }

         if (Menu.handle(Menu.eventAttach)) {
            ThemeColors var3 = ThemeColors.handle(var1);
            TransitionShader.handle().handle(this.vectorPerform, this.eventAttach, var3.save(), var3.submit());
         }
      }

      this.pending = var1;
      this.moduleTick = var2;
      if (WildClient.instance != null && WildClient.instance.selection != null) {
         WildClient.instance.selection.handle(var1);
      }
   }

   public void handle(float var1) {
      this.rendererScan = Math.max(0.0F, var1);
      if (this.rendererScan <= 0.001F) {
         this.animationSchedule = 0.0F;
         this.colorMeasure = 0.0F;
         this.active.handle(0.0F);
      }
   }

   public void process(float var1) {
      this.scaleAdapt = Math.max(0.0F, var1);
      if (this.scaleAdapt <= 0.001F) {
         this.colorCompute = 0.0F;
         this.mode.handle(0.0F);
      }
   }

   public void handle(float var1, GuiMetrics var2) {
      float var3 = this.handle(var2, this.scaleAdapt);
      this.colorCompute = this.handle(this.colorCompute, var1, -this.scaleAdapt, 0.0F, var3);
   }

   public void compute(float var1) {
      if (!(this.rendererScan <= 0.001F)) {
         this.animationSchedule = -this.rendererScan * this.handle(var1, 0.0F, 1.0F);
      }
   }

   public void resolve(float var1) {
      if (!(this.scaleAdapt <= 0.001F)) {
         this.colorCompute = -this.scaleAdapt * this.handle(var1, 0.0F, 1.0F);
      }
   }

   public void handle(float var1, float var2) {
      this.profileInvoke = Math.max(0.0F, var1);
      this.sourceSchedule = Math.max(0.0F, var2);
      this.sourceBuild = this.profileInvoke <= 0.001F ? 0.0F : this.handle(this.sourceBuild, 0.0F, this.profileInvoke);
      this.outputCollapse = this.sourceSchedule <= 0.001F ? 0.0F : this.handle(this.outputCollapse, 0.0F, this.sourceSchedule);
   }

   public void process(float var1, float var2) {
      this.sourceBuild = this.profileInvoke <= 0.001F ? 0.0F : this.handle(this.sourceBuild - var1, 0.0F, this.profileInvoke);
      this.outputCollapse = this.sourceSchedule <= 0.001F ? 0.0F : this.handle(this.outputCollapse - var2, 0.0F, this.sourceSchedule);
   }

   public void update(float var1) {
      this.outputCollapse = this.sourceSchedule <= 0.001F ? 0.0F : this.handle(this.sourceSchedule * this.handle(var1, 0.0F, 1.0F), 0.0F, this.sourceSchedule);
   }

   public void apply(float var1) {
      this.sourceBuild = this.profileInvoke <= 0.001F ? 0.0F : this.handle(this.profileInvoke * this.handle(var1, 0.0F, 1.0F), 0.0F, this.profileInvoke);
   }

   public void encodePoint() {
      this.timerRender = true;
      this.scaleSave = false;
   }

   public void animate() {
      this.scaleSave = true;
      this.timerRender = false;
   }

   public boolean load() {
      boolean var1 = this.timerRender || this.scaleSave;
      this.timerRender = false;
      this.scaleSave = false;
      return var1;
   }

   public void save() {
      this.sourceBuild = 0.0F;
      this.outputCollapse = 0.0F;
      this.profileInvoke = 0.0F;
      this.sourceSchedule = 0.0F;
      this.timerRender = false;
      this.scaleSave = false;
   }

   public float handle(SpringAnimationSpec var1, GuiMetrics var2) {
      float var3 = this.handle(var2, this.scaleAdapt);
      this.colorCompute = this.handle(this.colorCompute, -this.scaleAdapt, 0.0F, var3);
      float var4 = this.mode.handle(this.colorCompute, SpringAnimation.State.handle());
      return this.handle(this.mode, var4, -this.scaleAdapt, 0.0F, var3);
   }

   public float submit() {
      return this.mode.process();
   }

   public void unload() {
      if (!this.latest.isEmpty() || this.previous) {
         this.latest = "";
         this.previous = false;
         this.summary = false;
         this.filterTask();
         this.actionRead = System.currentTimeMillis();
      }
   }

   public void handle(char var1) {
      if (this.previous) {
         this.latest = "";
         this.previous = false;
      }

      if (this.latest.length() < 96) {
         this.latest = this.latest + var1;
         this.filterTask();
         this.actionRead = System.currentTimeMillis();
      }
   }

   public void fetch() {
      if (this.previous) {
         this.latest = "";
         this.previous = false;
         this.filterTask();
         this.actionRead = System.currentTimeMillis();
      } else if (!this.latest.isEmpty()) {
         this.latest = this.latest.substring(0, this.latest.length() - 1);
         this.filterTask();
         this.actionRead = System.currentTimeMillis();
      }
   }

   public void measure() {
      this.vectorMatch = true;
      this.summary = false;
      this.handle((StringSetting)null);
   }

   public void blendMatrix() {
      if (!this.matrixBlend.isEmpty()) {
         this.matrixBlend = "";
         this.vectorMatch = false;
         this.acquireIndex();
      }
   }

   public void process(char var1) {
      if (this.matrixBlend.length() < 96) {
         this.matrixBlend = this.matrixBlend + var1;
         this.acquireIndex();
      }
   }

   public void matchVector() {
      if (!this.matrixBlend.isEmpty()) {
         this.matrixBlend = this.matrixBlend.substring(0, this.matrixBlend.length() - 1);
         this.acquireIndex();
      }
   }

   public List<Integer> handle(ThemePaletteRegistry var1) {
      List var2 = var1.compute();
      String var3 = this.matrixBlend == null ? "" : this.matrixBlend;
      if (var3.equals(this.responseCompute) && this.providerFetch == var2.size()) {
         return this.itemProject;
      }

      ArrayList var4 = new ArrayList(var2.size());
      if (var3.isBlank()) {
         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4.add(var5);
         }
      } else {
         String var9 = var3.toLowerCase(Locale.ROOT).trim();
         String var6 = this.onTick(var9);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            String var8 = ((ThemePaletteRegistry.ColorState)var2.get(var7)).process().toLowerCase(Locale.ROOT);
            if (var8.contains(var9) || !var6.equals(var9) && var8.contains(var6)) {
               var4.add(var7);
            }
         }
      }

      this.responseCompute = var3;
      this.providerFetch = var2.size();
      this.itemProject = var4;
      return var4;
   }

   public void handle(Module var1) {
      if (this.cache.contains(var1)) {
         this.cache.remove(var1);
      } else if (ModulePanelRegistry.process(var1) || !var1.apply().isEmpty()) {
         this.cache.add(var1);
         this.enabled.put(System.identityHashCode(var1), System.currentTimeMillis());
      }
   }

   public void projectItem() {
      this.indexSynchronize = false;
      this.requestAdapt = false;
      this.vectorMatch = false;
   }

   public void handle(boolean var1) {
      this.indexSynchronize = var1;
      this.vectorMatch = false;
   }

   public void handle(ModeSetting var1) {
      if (this.playerRun == var1) {
         this.playerRun = null;
         var1.active = false;
      } else {
         this.fetchProvider();
         if (this.playerRun != null) {
            this.playerRun.active = false;
         }

         this.playerRun = var1;
         var1.active = true;
      }
   }

   public void handle(ModeSetting var1, int var2) {
      if (var2 >= 0 && var2 < var1.config.size()) {
         var1.current = var2;
         var1.state = var1.config.get(var2);
      }

      var1.active = false;
      if (this.playerRun == var1) {
         this.playerRun = null;
      }

      this.scheduleAnimation();
   }

   public void computeResponse() {
      if (this.playerRun != null) {
         this.playerRun.active = false;
         this.playerRun = null;
      }
   }

   public void handle(ShaderPresetSetting var1) {
      if (var1 == null) {
         this.fetchProvider();
      } else {
         var1.compute();
         if (this.matrixRender == var1) {
            this.matrixRender = null;
            var1.state = false;
         } else {
            this.computeResponse();
            if (this.matrixRender != null) {
               this.matrixRender.state = false;
            }

            this.matrixRender = var1;
            var1.state = true;
         }
      }
   }

   public void handle(ShaderPresetSetting var1, int var2) {
      if (var1 != null) {
         var1.handle(var2);
         var1.state = false;
      }

      if (this.matrixRender == var1) {
         this.matrixRender = null;
      }

      this.scheduleAnimation();
   }

   public void fetchProvider() {
      if (this.matrixRender != null) {
         this.matrixRender.state = false;
         this.matrixRender = null;
      }
   }

   public void handle(ColorSetting var1) {
      if (this.actionRender == var1) {
         this.actionRender = null;
         this.stateAttach.remove(var1);
         this.collapseKey();
      } else {
         this.collapseKey();
         this.actionRender = var1;
         if (var1 != null) {
            this.stateAttach.put(var1, var1.check());
         }
      }

      this.performVector();
      this.playerApply = false;
      this.bufferAdapt = false;
      this.playerUpdate = false;
   }

   public void drawProfile() {
      if (this.actionRender != null) {
         this.stateAttach.remove(this.actionRender);
      }

      this.actionRender = null;
      this.collapseKey();
      this.performVector();
      this.playerApply = false;
      this.bufferAdapt = false;
      this.playerUpdate = false;
   }

   public int process(ColorSetting var1) {
      if (var1 == null) {
         return 0;
      }

      Integer var2 = this.stateAttach.get(var1);
      return var2 != null ? var2 : var1.check();
   }

   public void performVector() {
      this.worldEvaluate = null;
      this.playerProject = "";
      this.playerMatch = null;
      this.cacheClose = "";
   }

   private void collapseKey() {
      this.packetRead = 0.0F;
      this.rendererCancel = 0.0F;
      this.eventReceive = 0.0F;
      this.screenSubmit = 0.0F;
      this.cacheHandle = 0.0F;
      this.rangeRelease = 0.0F;
      this.indexSave = 0.0F;
      this.indexCheck = 0.0F;
      this.settingSchedule = 0.0F;
      this.inputAcquire = 0.0F;
      this.listenerRun = 0.0F;
      this.indexLoad = 0.0F;
      this.layoutSave = 0.0F;
      this.blockRun = 0.0F;
      this.playerEvaluate = 0.0F;
      this.outputFetch = 0.0F;
      this.scaleParse = 0.0F;
      this.sessionEncode = 0.0F;
      this.elementTick = 0.0F;
      this.regionAlign = 0.0F;
      this.resourceClamp = 0.0F;
      this.handlerRun = 0.0F;
      this.keyCheck = 0.0F;
      this.layerProject = 0.0F;
      this.entityFilter = 0.0F;
      this.layerSample2 = 0.0F;
      this.sourceCancel = 0.0F;
      this.eventSend = 0.0F;
      this.providerOffset = 0.0F;
      this.messageParse2 = 0.0F;
      this.shaderProject = 0.0F;
      this.inputInvoke = 0.0F;
      this.optionFetch = 0.0F;
      this.eventCollapse = 0.0F;
   }

   public void attachEvent() {
      this.scaleSetup = !this.scaleSetup;
   }

   public void handle(String var1, String var2, float var3, float var4) {
      if (var1 == null || !var1.equals(this.vectorRun) || !Objects.equals(var2, this.playerSave)) {
         this.vectorRun = var1;
         this.playerSave = var2;
         this.dataRelease = System.currentTimeMillis();
      }

      this.requestRun = var3;
      this.frameProject = var4;
   }

   public void readServer() {
      this.vectorRun = null;
      this.playerSave = null;
      this.dataRelease = 0L;
   }

   public boolean advancePosition() {
      return this.vectorRun != null && this.playerSave != null && System.currentTimeMillis() - this.dataRelease > 220L;
   }

   public void process(Module var1) {
      this.fetchTimer();
      this.keyProcess = var1;
      var1.bindingActive = true;
   }

   public void handle(KeybindSetting var1) {
      this.fetchTimer();
      this.actionConvert = var1;
      var1.output = true;
   }

   public void handle(BooleanSetting var1) {
      this.fetchTimer();
      this.screenRead = var1;
      var1.current = true;
   }

   public void process(int var1) {
      if (this.keyProcess != null) {
         this.keyProcess.keyCode = this.prepare(var1) ? -1 : var1;
         this.keyProcess.bindingActive = false;
         this.keyProcess = null;
         this.scheduleAnimation();
      }
   }

   public void compute(int var1) {
      if (this.actionConvert != null) {
         this.actionConvert.config = this.prepare(var1) ? -1 : var1;
         this.actionConvert.output = false;
         this.actionConvert = null;
         this.scheduleAnimation();
      }
   }

   public void resolve(int var1) {
      if (this.screenRead != null) {
         this.screenRead.cache = this.prepare(var1) ? -1 : var1;
         this.screenRead.current = false;
         this.screenRead = null;
         this.scheduleAnimation();
      }
   }

   public void update(int var1) {
      int var2 = -100 - var1;
      if (this.keyProcess != null) {
         this.keyProcess.keyCode = var2;
         this.keyProcess.bindingActive = false;
         this.keyProcess = null;
         this.scheduleAnimation();
      } else if (this.actionConvert != null) {
         this.actionConvert.config = var2;
         this.actionConvert.output = false;
         this.actionConvert = null;
         this.scheduleAnimation();
      } else if (this.screenRead != null) {
         this.screenRead.cache = var2;
         this.screenRead.current = false;
         this.screenRead = null;
         this.scheduleAnimation();
      }
   }

   public boolean checkFrame() {
      return this.keyProcess != null || this.actionConvert != null || this.screenRead != null;
   }

   public void collectModule() {
      this.fetchTimer();
      this.handle((StringSetting)null);
      this.computeResponse();
      this.fetchProvider();
      this.drawProfile();
      this.providerRead = null;
      this.matrixBlend2 = null;
      this.keyFilter = false;
      this.requestAdapt = false;
      this.timerMeasure = false;
      this.stateApply = false;
      this.scaleSetup = false;
   }

   public void handle(StringSetting var1) {
      if (this.animationExpand != null) {
         this.animationExpand.current = false;
      }

      this.animationExpand = var1;
      if (this.animationExpand != null) {
         this.animationExpand.current = true;
      }
   }

   public void process(SpringAnimationSpec var1, GuiMetrics var2) {
      float var3 = this.handle(var2, this.rendererScan);
      this.animationSchedule = this.handle(this.animationSchedule, -this.rendererScan, 0.0F, var3);
      float var4 = this.active.handle(this.animationSchedule, SpringAnimation.State.handle());
      this.colorMeasure = this.handle(this.active, var4, -this.rendererScan, 0.0F, var3);
   }

   public void handle(GuiMetrics var1, float var2, float var3) {
      if (!(var2 <= 0.0F) && !(var3 <= 0.0F)) {
         float var4 = var1.handle(8.0F);
         if (!this.scaleRender) {
            this.serverRead = Math.max(var4, (var2 - var1.resolve()) * 0.5F);
            this.positionAdvance = Math.max(var4, (var3 - var1.update()) * 0.5F);
            this.scaleRender = true;
         }

         this.serverRead = this.handle(this.serverRead, var4, Math.max(var4, var2 - var1.resolve() - var4));
         this.positionAdvance = this.handle(this.positionAdvance, var4, Math.max(var4, var3 - var1.update() - var4));
      }
   }

   public void process(GuiMetrics var1, float var2, float var3) {
      if (!(var2 <= 0.0F) && !(var3 <= 0.0F)) {
         float var4 = var1.handle(8.0F);
         if (!this.clientRefresh) {
            float var5 = var1.handle(12.0F);
            float var6 = this.positionAdvance - var1.submit() - var5;
            if (var6 < var4) {
               var6 = this.positionAdvance + var1.update() + var5;
            }

            if (var6 + var1.submit() > var3 - var4) {
               var6 = Math.max(var4, var3 - var1.submit() - var4);
            }

            float var7 = this.serverRead;
            var7 = Math.max(var4, Math.min(var7, var2 - var1.save() - var4));
            this.frameCheck = var7;
            this.moduleCollect = var6;
            this.clientRefresh = true;
         }

         this.frameCheck = this.handle(this.frameCheck, var4, Math.max(var4, var2 - var1.save() - var4));
         this.moduleCollect = this.handle(this.moduleCollect, var4, Math.max(var4, var3 - var1.submit() - var4));
      }
   }

   public void handle(float var1, float var2, ViewportLayoutState var3) {
      this.keyFilter = true;
      this.requestAdapt = false;
      this.providerClose = var1 - var3.handle();
      this.presetSave = var2 - var3.process();
      this.serverRead = var3.handle();
      this.positionAdvance = var3.process();
      this.scaleRender = true;
      this.summary = false;
      this.vectorMatch = false;
      this.handle((StringSetting)null);
   }

   public void compute(float var1, float var2) {
      if (this.keyFilter) {
         this.serverRead = var1 - this.providerClose;
         this.positionAdvance = var2 - this.presetSave;
         this.scaleRender = true;
      }
   }

   public void process(float var1, float var2, ViewportLayoutState var3) {
      this.requestAdapt = true;
      this.keyFilter = false;
      this.windowConvert = var1 - var3.projectItem();
      this.presetWrite = var2 - var3.computeResponse();
      this.frameCheck = var3.projectItem();
      this.moduleCollect = var3.computeResponse();
      this.clientRefresh = true;
      this.summary = false;
      this.vectorMatch = false;
      this.handle((StringSetting)null);
   }

   public boolean closeProvider() {
      boolean var1 = this.keyFilter;
      this.keyFilter = false;
      return var1;
   }

   public void handle(float var1, float var2, ViewportLayoutState var3, GuiMetrics var4) {
      this.timerMeasure = true;
      this.keyFilter = false;
      this.requestAdapt = false;
      this.vectorEncode = var1;
      this.requestReceive = var2;
      this.packetSave = var4 == null ? 1.0F : Math.max(1.0F, var4.resolve());
      this.entryAnimate = var4 == null ? 1.0F : Math.max(1.0F, var4.update());

      try {
         this.windowProcess = Menu.responseCompute == null ? 1.0F : Menu.responseCompute.compute();
      } catch (Throwable var6) {
         this.windowProcess = 1.0F;
      }

      this.summary = false;
      this.vectorMatch = false;
      this.handle((StringSetting)null);
      if (var3 != null) {
         this.serverRead = var3.handle();
         this.positionAdvance = var3.process();
         this.scaleRender = true;
      }
   }

   public void resolve(float var1, float var2) {
      if (this.timerMeasure && Menu.responseCompute != null) {
         float var3 = (var1 - this.vectorEncode) / Math.max(1.0F, this.packetSave);
         float var4 = (var2 - this.requestReceive) / Math.max(1.0F, this.entryAnimate);
         float var5 = Math.abs(var3) >= Math.abs(var4) ? var3 : var4;
         float var6 = this.windowProcess * (1.0F + var5);
         var6 = Math.max(0.72F, Math.min(1.7F, var6));

         try {
            Menu.responseCompute.handle(var6);
         } catch (Throwable var8) {
         }
      }
   }

   public boolean savePreset() {
      boolean var1 = this.timerMeasure;
      this.timerMeasure = false;
      if (var1) {
         this.scheduleAnimation();
      }

      return var1;
   }

   public void process(boolean var1) {
      this.playerCollect = var1;
   }

   public void process(float var1, float var2, ViewportLayoutState var3, GuiMetrics var4) {
      this.stateApply = true;
      this.keyFilter = false;
      this.requestAdapt = false;
      this.timerMeasure = false;
      this.matrixFilter = var1;
      this.layerSample = var2;
      this.targetWrite = var4 == null ? 1.0F : Math.max(1.0F, var4.save());
      this.resultEncode = var4 == null ? 1.0F : Math.max(1.0F, var4.submit());

      try {
         this.worldSend = Menu.providerFetch == null ? 1.0F : Menu.providerFetch.compute();
      } catch (Throwable var6) {
         this.worldSend = 1.0F;
      }

      this.summary = false;
      this.vectorMatch = false;
      this.handle((StringSetting)null);
      if (var3 != null) {
         this.frameCheck = var3.projectItem();
         this.moduleCollect = var3.computeResponse();
         this.clientRefresh = true;
      }
   }

   public void update(float var1, float var2) {
      if (this.stateApply && Menu.providerFetch != null) {
         float var3 = (var1 - this.matrixFilter) / Math.max(1.0F, this.targetWrite);
         float var4 = (var2 - this.layerSample) / Math.max(1.0F, this.resultEncode);
         float var5 = Math.abs(var3) >= Math.abs(var4) ? var3 : var4;
         float var6 = this.worldSend * (1.0F + var5);
         var6 = Math.max(0.72F, Math.min(1.7F, var6));

         try {
            Menu.providerFetch.handle(var6);
         } catch (Throwable var8) {
         }
      }
   }

   public boolean convertWindow() {
      boolean var1 = this.stateApply;
      this.stateApply = false;
      if (var1) {
         this.scheduleAnimation();
      }

      return var1;
   }

   public void compute(boolean var1) {
      this.messageParse = var1;
   }

   public void apply(float var1, float var2) {
      if (this.requestAdapt) {
         this.frameCheck = var1 - this.windowConvert;
         this.moduleCollect = var2 - this.presetWrite;
         this.clientRefresh = true;
      }
   }

   public boolean writePreset() {
      boolean var1 = this.requestAdapt;
      this.requestAdapt = false;
      if (var1 && WildClient.instance != null && WildClient.instance.selection != null) {
         WildClient.instance.selection.handle(this.frameCheck, this.moduleCollect);
      }

      return var1;
   }

   public void process(float var1, GuiMetrics var2) {
      float var3 = this.handle(var2, this.rendererScan);
      this.animationSchedule = this.handle(this.animationSchedule, var1, -this.rendererScan, 0.0F, var3);
      this.handle(var2);
   }

   public void handle(GuiMetrics var1) {
      float var2 = this.handle(var1, this.rendererScan);
      this.animationSchedule = this.handle(this.animationSchedule, -this.rendererScan, 0.0F, var2);
      this.colorMeasure = this.handle(this.active, this.colorMeasure, -this.rendererScan, 0.0F, var2);
   }

   public float handle(String var1, float var2, SpringAnimationSpec var3) {
      return this.output.computeIfAbsent(var1, var1x -> new SpringAnimation(var2)).handle(var2, var3);
   }

   public float handle(String var1, float var2, SpringAnimation.State var3) {
      return this.output.computeIfAbsent(var1, var1x -> new SpringAnimation(var2)).handle(var2, var3);
   }

   public float handle(String var1) {
      SpringAnimation var2 = this.output.get(var1);
      return var2 == null ? 0.0F : var2.process();
   }

   public float process(String var1) {
      SpringAnimation var2 = this.output.get(var1);
      return var2 == null ? 0.0F : var2.compute();
   }

   public float process(String var1, float var2, SpringAnimationSpec var3) {
      return this.output.computeIfAbsent(var1, var0 -> new SpringAnimation(0.0F)).handle(var2, var3);
   }

   public float measureColor() {
      if (this.profileDraw) {
         return -1.0F;
      }

      long var1 = System.currentTimeMillis();
      long var3 = var1 - this.textureRun;
      return var3 < 350L ? 0.0F : Math.min(1.0F, (float)(var3 - 350L) / 600.0F);
   }

   public void scheduleAnimation() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   public void handle(ThemeColors var1) {
      if (this.optionAdvance == null) {
         this.playerCollapse = var1;
         this.optionAdvance = var1;
         this.selection.handle(1.0F);
      }
   }

   public void process(ThemeColors var1) {
      this.playerCollapse = var1;
      this.optionAdvance = var1;
      this.configMatch = 0L;
      this.selection.handle(1.0F);
   }

   public void compute(SpringAnimationSpec var1) {
      if (this.optionAdvance != null && this.playerCollapse != null) {
         this.playerCollapse = this.optionAdvance;
         this.selection.handle(1.0F);
         this.configMatch = 0L;
      } else {
         this.selection.handle(1.0F);
         this.configMatch = 0L;
      }
   }

   public ThemeColors scanRenderer() {
      return this.optionAdvance == null ? this.playerCollapse : this.optionAdvance;
   }

   public float buildSource() {
      if (this.listenerPerform <= 0L) {
         return -1.0F;
      }

      long var1 = System.currentTimeMillis() - this.listenerPerform;
      float var3 = Math.min(1.0F, (float)var1 / 760.0F);
      if (var3 >= 1.0F) {
         this.listenerPerform = 0L;
      }

      return var3;
   }

   public long compute(Module var1) {
      return this.enabled.getOrDefault(System.identityHashCode(var1), 0L);
   }

   public void collapseOutput() {
      if (WildClient.instance != null) {
         if (WildClient.instance.data == null) {
            WildClient.instance.data = new FeatureManager();
         }

         if (WildClient.instance.selection == null) {
            WildClient.instance.selection = new ThemeManager();
            WildClient.instance.selection.handle();
         }
      }
   }

   private void fetchTimer() {
      if (this.keyProcess != null) {
         this.keyProcess.bindingActive = false;
      }

      if (this.actionConvert != null) {
         this.actionConvert.output = false;
      }

      if (this.screenRead != null) {
         this.screenRead.current = false;
      }

      this.keyProcess = null;
      this.actionConvert = null;
      this.screenRead = null;
   }

   private void filterTask() {
      this.animationSchedule = 0.0F;
      this.colorMeasure = 0.0F;
      this.active.handle(0.0F);
   }

   private void acquireIndex() {
      this.colorCompute = 0.0F;
      this.mode.handle(0.0F);
   }

   private boolean handle(Module var1, String var2) {
      if (var2.startsWith("#")) {
         return this.process(var1, var2);
      }

      String var3 = var1.description == null ? "" : var1.description;
      String var4 = this.onTick(var2);
      String var5 = var1.displayName.toLowerCase(Locale.ROOT);
      String var6 = var3.toLowerCase(Locale.ROOT);
      return var5.contains(var2) || var6.contains(var2) || !var4.equals(var2) && (var5.contains(var4) || var6.contains(var4));
   }

   private boolean process(Module var1, String var2) {
      String var3 = this.onTick(var2);
      String[] var4 = var3.trim().split("\\s+");
      boolean var5 = false;

      for (String var9 : var4) {
         if (var9 != null && !var9.isBlank()) {
            if (!var9.startsWith("#")) {
               return false;
            }

            ModuleFlag var10 = ModuleFlag.handle(var9);
            if (var10 == null || !var1.hasFlag(var10)) {
               return false;
            }

            var5 = true;
         }
      }

      return var5;
   }

   private String onTick(String var1) {
      StringBuilder var2 = new StringBuilder(var1.length());

      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         var2.append(var4 < providerSynchronize.length && providerSynchronize[var4] != 0 ? providerSynchronize[var4] : var4);
      }

      return var2.toString();
   }

   private boolean prepare(int var1) {
      return var1 == 256 || var1 == 261 || var1 == 259;
   }

   private float handle(float var1, float var2, float var3) {
      return Math.max(var2, Math.min(var3, var1));
   }

   private float handle(GuiMetrics var1, float var2) {
      float var3 = var1 == null ? 18.0F : var1.handle(18.0F);
      float var4 = var1 == null ? 42.0F : var1.handle(42.0F);
      return Math.max(var3, Math.min(var4, var3 + var2 * 0.12F));
   }

   private float handle(float var1, float var2, float var3, float var4, float var5) {
      float var6 = var1 + var2;
      if (var6 < var3) {
         return var3 - this.execute(var3 - var6, var5);
      } else {
         return var6 > var4 ? var4 + this.execute(var6 - var4, var5) : var6;
      }
   }

   private float handle(float var1, float var2, float var3, float var4) {
      float var5 = this.handle(var1, var2 - var4, var3 + var4);
      float var6 = SpringAnimation.handle();
      if (var5 < var2) {
         float var8 = var2 + SpringAnimation.handle(var5 - var2, var6, 12.8F);
         return Math.abs(var8 - var2) <= 0.35F ? var2 : var8;
      } else if (var5 > var3) {
         float var7 = var3 + SpringAnimation.handle(var5 - var3, var6, 12.8F);
         return Math.abs(var7 - var3) <= 0.35F ? var3 : var7;
      } else {
         return var5;
      }
   }

   private float handle(SpringAnimation var1, float var2, float var3, float var4, float var5) {
      float var6 = this.process(var2, var3 - var5, var4 + var5, 0.34F);
      if (Math.abs(var6 - var2) > 0.001F) {
         var1.compute(var6);
         var1.resolve(var1.compute() * 0.74F);
      }

      if (Math.abs(var6 - var3) <= 0.35F && var6 < var3) {
         var1.compute(var3);
         return var3;
      } else if (Math.abs(var6 - var4) <= 0.35F && var6 > var4) {
         var1.compute(var4);
         return var4;
      } else {
         return var6;
      }
   }

   private float process(float var1, float var2, float var3, float var4) {
      if (var1 < var2) {
         return var2 - (var2 - var1) * var4;
      } else {
         return var1 > var3 ? var3 + (var1 - var3) * var4 : var1;
      }
   }

   private float execute(float var1, float var2) {
      float var3 = Math.max(1.0F, var2);
      return var3 * (1.0F - (float)Math.exp(-Math.max(0.0F, var1) / (var3 * 0.62F)));
   }
   public Set<Module> invokeProfile() {
      return this.cache;
   }
   public Map<String, SpringAnimation> scheduleSource() {
      return this.output;
   }
   public SpringAnimation renderTimer() {
      return this.current;
   }
   public SpringAnimation saveScale() {
      return this.active;
   }
   public SpringAnimation computeColor() {
      return this.mode;
   }
   public SpringAnimation adaptScale() {
      return this.selection;
   }
   public Map<Integer, Long> runTexture() {
      return this.enabled;
   }
   public ModuleCategory bindIndex() {
      return this.renderer;
   }
   public HeadlessBotSession readAction() {
      return this.handler;
   }
   public boolean collapseConfig() {
      return this.animationDraw;
   }
   public boolean validateData() {
      return this.pointEncode;
   }
   public boolean renderScale() {
      return this.animator;
   }
   public boolean refreshClient() {
      return this.source;
   }
   public long filterKey() {
      return this.target;
   }
   public ThemePalette adaptRequest() {
      return this.pending;
   }
   public boolean measureTimer() {
      return this.previous;
   }
   public String encodeVector() {
      return this.latest;
   }
   public boolean receiveRequest() {
      return this.summary;
   }
   public String processWindow() {
      return this.matrixBlend;
   }
   public boolean savePacket() {
      return this.vectorMatch;
   }
   public List<Integer> animateEntry() {
      return this.itemProject;
   }
   public String collectPlayer() {
      return this.responseCompute;
   }
   public int applyState() {
      return this.providerFetch;
   }
   public boolean filterMatrix() {
      return this.profileDraw;
   }
   public float sampleLayer() {
      return this.vectorPerform;
   }
   public float sendWorld() {
      return this.eventAttach;
   }
   public float writeTarget() {
      return this.serverRead;
   }
   public float encodeResult() {
      return this.positionAdvance;
   }
   public float parseMessage() {
      return this.frameCheck;
   }
   public float readProvider() {
      return this.moduleCollect;
   }
   public float blendMatrix2() {
      return this.providerClose;
   }
   public float performScale() {
      return this.presetSave;
   }
   public float expandContext() {
      return this.windowConvert;
   }
   public float processKey() {
      return this.presetWrite;
   }
   public float convertAction() {
      return this.colorMeasure;
   }
   public float readScreen() {
      return this.animationSchedule;
   }
   public float expandAnimation() {
      return this.rendererScan;
   }
   public float runPlayer() {
      return this.sourceBuild;
   }
   public float renderMatrix() {
      return this.outputCollapse;
   }
   public float tickModule() {
      return this.profileInvoke;
   }
   public float collapsePlayer() {
      return this.sourceSchedule;
   }
   public boolean advanceOption() {
      return this.timerRender;
   }
   public boolean scanEffect() {
      return this.scaleSave;
   }
   public float parseOption() {
      return this.colorCompute;
   }
   public float submitPoint() {
      return this.scaleAdapt;
   }
   public long performListener() {
      return this.textureRun;
   }
   public long matchConfig() {
      return this.indexBind;
   }
   public long renderAction() {
      return this.actionRead;
   }
   public long applyPlayer() {
      return this.configCollapse;
   }
   public long adaptBuffer() {
      return this.dataValidate;
   }
   public boolean updatePlayer() {
      return this.scaleRender;
   }
   public boolean readPacket() {
      return this.clientRefresh;
   }
   public boolean cancelRenderer() {
      return this.keyFilter;
   }
   public boolean receiveEvent() {
      return this.requestAdapt;
   }
   public boolean submitScreen() {
      return this.timerMeasure;
   }
   public float handleCache() {
      return this.vectorEncode;
   }
   public float releaseRange() {
      return this.requestReceive;
   }
   public float saveIndex() {
      return this.windowProcess;
   }
   public float checkIndex() {
      return this.packetSave;
   }
   public float scheduleSetting() {
      return this.entryAnimate;
   }
   public boolean acquireInput() {
      return this.playerCollect;
   }
   public boolean runListener() {
      return this.stateApply;
   }
   public float loadIndex() {
      return this.matrixFilter;
   }
   public float saveLayout() {
      return this.layerSample;
   }
   public float runBlock() {
      return this.worldSend;
   }
   public float evaluatePlayer() {
      return this.targetWrite;
   }
   public float fetchOutput() {
      return this.resultEncode;
   }
   public boolean parseScale() {
      return this.messageParse;
   }
   public NumberSetting encodeSession() {
      return this.providerRead;
   }
   public ColorSetting tickElement() {
      return this.matrixBlend2;
   }
   public float alignRegion() {
      return this.scalePerform;
   }
   public float clampResource() {
      return this.contextExpand;
   }
   public Module runHandler() {
      return this.keyProcess;
   }
   public KeybindSetting checkKey() {
      return this.actionConvert;
   }
   public BooleanSetting projectLayer() {
      return this.screenRead;
   }
   public StringSetting filterEntity() {
      return this.animationExpand;
   }
   public ModeSetting sampleLayer2() {
      return this.playerRun;
   }
   public ShaderPresetSetting cancelSource() {
      return this.matrixRender;
   }
   public int sendEvent() {
      return this.moduleTick;
   }
   public ThemeColors offsetProvider() {
      return this.playerCollapse;
   }
   public ThemeColors parseMessage2() {
      return this.optionAdvance;
   }
   public ThemePalette projectShader() {
      return this.effectScan;
   }
   public float invokeInput() {
      return this.optionParse;
   }
   public float fetchOption() {
      return this.pointSubmit;
   }
   public long collapseEvent() {
      return this.listenerPerform;
   }
   public long attachState() {
      return this.configMatch;
   }
   public ColorSetting evaluateWorld() {
      return this.actionRender;
   }
   public boolean projectPlayer() {
      return this.playerApply;
   }
   public boolean matchPlayer() {
      return this.bufferAdapt;
   }
   public boolean closeCache() {
      return this.playerUpdate;
   }
   public float setupScale() {
      return this.packetRead;
   }
   public float synchronizeIndex() {
      return this.rendererCancel;
   }
   public float interpolateTask() {
      return this.eventReceive;
   }
   public float refreshSource() {
      return this.screenSubmit;
   }
   public float savePlayer() {
      return this.cacheHandle;
   }
   public float runRequest() {
      return this.rangeRelease;
   }
   public float projectFrame() {
      return this.indexSave;
   }
   public float releaseData() {
      return this.indexCheck;
   }
   public float runVector() {
      return this.settingSchedule;
   }
   public float synchronizeProvider() {
      return this.inputAcquire;
   }
   public float processPath() {
      return this.listenerRun;
   }
   public float resetPosition() {
      return this.indexLoad;
   }
   public float applyEffect() {
      return this.layoutSave;
   }
   public float handleCache2() {
      return this.blockRun;
   }
   public float advanceSession() {
      return this.playerEvaluate;
   }
   public float sampleKey() {
      return this.outputFetch;
   }
   public float performPlayer() {
      return this.scaleParse;
   }
   public float loadTask() {
      return this.sessionEncode;
   }
   public float sendPoint() {
      return this.elementTick;
   }
   public float submitClient() {
      return this.regionAlign;
   }
   public float samplePoint() {
      return this.resourceClamp;
   }
   public float refreshRegion() {
      return this.handlerRun;
   }
   public float checkPath() {
      return this.keyCheck;
   }
   public float parseContext() {
      return this.layerProject;
   }
   public float stopOption() {
      return this.entityFilter;
   }
   public float prepareMouse() {
      return this.layerSample2;
   }
   public float adaptSession() {
      return this.sourceCancel;
   }
   public float matchProfile() {
      return this.eventSend;
   }
   public float advanceEntity() {
      return this.providerOffset;
   }
   public float collectSession() {
      return this.messageParse2;
   }
   public float attachInput() {
      return this.shaderProject;
   }
   public float projectPath() {
      return this.inputInvoke;
   }
   public float handleInput() {
      return this.optionFetch;
   }
   public float dispatchWorld() {
      return this.eventCollapse;
   }
   public Map<ColorSetting, Integer> advanceOption2() {
      return this.stateAttach;
   }
   public ColorSetting drawClient() {
      return this.worldEvaluate;
   }
   public String matchProvider() {
      return this.playerProject;
   }
   public ColorSetting writeProfile() {
      return this.playerMatch;
   }
   public String matchEffect() {
      return this.cacheClose;
   }
   public boolean executeCache() {
      return this.scaleSetup;
   }
   public boolean synchronizePoint() {
      return this.indexSynchronize;
   }
   public boolean bindSession() {
      return this.taskInterpolate;
   }
   public boolean cancelIndex() {
      return this.sourceRefresh;
   }
   public String stopElement() {
      return this.playerSave;
   }
   public float animateItem() {
      return this.requestRun;
   }
   public float readValue() {
      return this.frameProject;
   }
   public long alignConfig() {
      return this.dataRelease;
   }
   public String measureOption() {
      return this.vectorRun;
   }
   public void process(ModuleCategory var1) {
      this.renderer = var1;
   }
   public void handle(HeadlessBotSession var1) {
      this.handler = var1;
   }
   public void resolve(boolean var1) {
      this.animationDraw = var1;
   }
   public void update(boolean var1) {
      this.pointEncode = var1;
   }
   public void apply(boolean var1) {
      this.animator = var1;
   }
   public void execute(boolean var1) {
      this.source = var1;
   }
   public void handle(long var1) {
      this.target = var1;
   }
   public void handle(ThemePalette var1) {
      this.pending = var1;
   }
   public void prepare(boolean var1) {
      this.previous = var1;
   }
   public void compute(String var1) {
      this.latest = var1;
   }
   public void check(boolean var1) {
      this.summary = var1;
   }
   public void resolve(String var1) {
      this.matrixBlend = var1;
   }
   public void onTick(boolean var1) {
      this.vectorMatch = var1;
   }
   public void handle(List<Integer> var1) {
      this.itemProject = var1;
   }
   public void update(String var1) {
      this.responseCompute = var1;
   }
   public void apply(int var1) {
      this.providerFetch = var1;
   }
   public void select(boolean var1) {
      this.profileDraw = var1;
   }
   public void execute(float var1) {
      this.vectorPerform = var1;
   }
   public void prepare(float var1) {
      this.eventAttach = var1;
   }
   public void check(float var1) {
      this.serverRead = var1;
   }
   public void onTick(float var1) {
      this.positionAdvance = var1;
   }
   public void select(float var1) {
      this.frameCheck = var1;
   }
   public void refresh(float var1) {
      this.moduleCollect = var1;
   }
   public void render(float var1) {
      this.providerClose = var1;
   }
   public void tick(float var1) {
      this.presetSave = var1;
   }
   public void drawAnimation(float var1) {
      this.windowConvert = var1;
   }
   public void encodePoint(float var1) {
      this.presetWrite = var1;
   }
   public void animate(float var1) {
      this.colorMeasure = var1;
   }
   public void load(float var1) {
      this.animationSchedule = var1;
   }
   public void save(float var1) {
      this.sourceBuild = var1;
   }
   public void submit(float var1) {
      this.outputCollapse = var1;
   }
   public void unload(float var1) {
      this.profileInvoke = var1;
   }
   public void fetch(float var1) {
      this.sourceSchedule = var1;
   }
   public void refresh(boolean var1) {
      this.timerRender = var1;
   }
   public void render(boolean var1) {
      this.scaleSave = var1;
   }
   public void measure(float var1) {
      this.colorCompute = var1;
   }
   public void process(long var1) {
      this.textureRun = var1;
   }
   public void compute(long var1) {
      this.indexBind = var1;
   }
   public void resolve(long var1) {
      this.actionRead = var1;
   }
   public void update(long var1) {
      this.configCollapse = var1;
   }
   public void apply(long var1) {
      this.dataValidate = var1;
   }
   public void tick(boolean var1) {
      this.scaleRender = var1;
   }
   public void drawAnimation(boolean var1) {
      this.clientRefresh = var1;
   }
   public void encodePoint(boolean var1) {
      this.keyFilter = var1;
   }
   public void animate(boolean var1) {
      this.requestAdapt = var1;
   }
   public void load(boolean var1) {
      this.timerMeasure = var1;
   }
   public void blendMatrix(float var1) {
      this.vectorEncode = var1;
   }
   public void matchVector(float var1) {
      this.requestReceive = var1;
   }
   public void projectItem(float var1) {
      this.windowProcess = var1;
   }
   public void computeResponse(float var1) {
      this.packetSave = var1;
   }
   public void fetchProvider(float var1) {
      this.entryAnimate = var1;
   }
   public void save(boolean var1) {
      this.stateApply = var1;
   }
   public void drawProfile(float var1) {
      this.matrixFilter = var1;
   }
   public void performVector(float var1) {
      this.layerSample = var1;
   }
   public void attachEvent(float var1) {
      this.worldSend = var1;
   }
   public void readServer(float var1) {
      this.targetWrite = var1;
   }
   public void advancePosition(float var1) {
      this.resultEncode = var1;
   }
   public void handle(NumberSetting var1) {
      this.providerRead = var1;
   }
   public void compute(ColorSetting var1) {
      this.matrixBlend2 = var1;
   }
   public void checkFrame(float var1) {
      this.scalePerform = var1;
   }
   public void collectModule(float var1) {
      this.contextExpand = var1;
   }
   public void resolve(Module var1) {
      this.keyProcess = var1;
   }
   public void process(KeybindSetting var1) {
      this.actionConvert = var1;
   }
   public void process(BooleanSetting var1) {
      this.screenRead = var1;
   }
   public void process(ModeSetting var1) {
      this.playerRun = var1;
   }
   public void process(ShaderPresetSetting var1) {
      this.matrixRender = var1;
   }
   public void execute(int var1) {
      this.moduleTick = var1;
   }
   public void compute(ThemeColors var1) {
      this.playerCollapse = var1;
   }
   public void resolve(ThemeColors var1) {
      this.optionAdvance = var1;
   }
   public void process(ThemePalette var1) {
      this.effectScan = var1;
   }
   public void closeProvider(float var1) {
      this.optionParse = var1;
   }
   public void savePreset(float var1) {
      this.pointSubmit = var1;
   }
   public void execute(long var1) {
      this.listenerPerform = var1;
   }
   public void prepare(long var1) {
      this.configMatch = var1;
   }
   public void resolve(ColorSetting var1) {
      this.actionRender = var1;
   }
   public void submit(boolean var1) {
      this.playerApply = var1;
   }
   public void unload(boolean var1) {
      this.bufferAdapt = var1;
   }
   public void fetch(boolean var1) {
      this.playerUpdate = var1;
   }
   public void convertWindow(float var1) {
      this.packetRead = var1;
   }
   public void writePreset(float var1) {
      this.rendererCancel = var1;
   }
   public void measureColor(float var1) {
      this.eventReceive = var1;
   }
   public void scheduleAnimation(float var1) {
      this.screenSubmit = var1;
   }
   public void scanRenderer(float var1) {
      this.cacheHandle = var1;
   }
   public void buildSource(float var1) {
      this.rangeRelease = var1;
   }
   public void collapseOutput(float var1) {
      this.indexSave = var1;
   }
   public void invokeProfile(float var1) {
      this.indexCheck = var1;
   }
   public void scheduleSource(float var1) {
      this.settingSchedule = var1;
   }
   public void renderTimer(float var1) {
      this.inputAcquire = var1;
   }
   public void saveScale(float var1) {
      this.listenerRun = var1;
   }
   public void computeColor(float var1) {
      this.indexLoad = var1;
   }
   public void adaptScale(float var1) {
      this.layoutSave = var1;
   }
   public void runTexture(float var1) {
      this.blockRun = var1;
   }
   public void bindIndex(float var1) {
      this.playerEvaluate = var1;
   }
   public void readAction(float var1) {
      this.outputFetch = var1;
   }
   public void collapseConfig(float var1) {
      this.scaleParse = var1;
   }
   public void validateData(float var1) {
      this.sessionEncode = var1;
   }
   public void renderScale(float var1) {
      this.elementTick = var1;
   }
   public void refreshClient(float var1) {
      this.regionAlign = var1;
   }
   public void filterKey(float var1) {
      this.resourceClamp = var1;
   }
   public void adaptRequest(float var1) {
      this.handlerRun = var1;
   }
   public void measureTimer(float var1) {
      this.keyCheck = var1;
   }
   public void encodeVector(float var1) {
      this.layerProject = var1;
   }
   public void receiveRequest(float var1) {
      this.entityFilter = var1;
   }
   public void processWindow(float var1) {
      this.layerSample2 = var1;
   }
   public void savePacket(float var1) {
      this.sourceCancel = var1;
   }
   public void animateEntry(float var1) {
      this.eventSend = var1;
   }
   public void collectPlayer(float var1) {
      this.providerOffset = var1;
   }
   public void applyState(float var1) {
      this.messageParse2 = var1;
   }
   public void filterMatrix(float var1) {
      this.shaderProject = var1;
   }
   public void sampleLayer(float var1) {
      this.inputInvoke = var1;
   }
   public void sendWorld(float var1) {
      this.optionFetch = var1;
   }
   public void writeTarget(float var1) {
      this.eventCollapse = var1;
   }
   public void update(ColorSetting var1) {
      this.worldEvaluate = var1;
   }
   public void apply(String var1) {
      this.playerProject = var1;
   }
   public void apply(ColorSetting var1) {
      this.playerMatch = var1;
   }
   public void execute(String var1) {
      this.cacheClose = var1;
   }
   public void measure(boolean var1) {
      this.scaleSetup = var1;
   }
   public void blendMatrix(boolean var1) {
      this.taskInterpolate = var1;
   }
   public void matchVector(boolean var1) {
      this.sourceRefresh = var1;
   }
   public void prepare(String var1) {
      this.playerSave = var1;
   }
   public void encodeResult(float var1) {
      this.requestRun = var1;
   }
   public void parseMessage(float var1) {
      this.frameProject = var1;
   }
   public void check(long var1) {
      this.dataRelease = var1;
   }
   public void check(String var1) {
      this.vectorRun = var1;
   }

   static {
      String var0 = "йцукенгшщзхъфывапролджэячсмитьбю";
      String var1 = "qwertyuiop[]asdfghjkl;'zxcvbnm,.";

      for (int var2 = 0; var2 < var0.length(); var2++) {
         providerSynchronize[var0.charAt(var2)] = var1.charAt(var2);
      }
   }
}
