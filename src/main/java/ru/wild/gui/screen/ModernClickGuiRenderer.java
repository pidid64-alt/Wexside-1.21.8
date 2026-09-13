package ru.wild.gui.screen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.config.StudioProfileGate;
import ru.wild.core.CoreDiagnosticsPanel;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.hud.HologramStatsRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.FoundryAssetBrowser;
import ru.wild.gui.widget.ModuleCardTransform;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.ModulePanelRegistry;
import ru.wild.gui.widget.ModulePlacement;
import ru.wild.gui.widget.ModuleSettingsPanel;
import ru.wild.gui.widget.ModuleTooltipMeasurer;
import ru.wild.gui.widget.SettingControlRenderer;
import ru.wild.gui.widget.SurfaceHitResolver;
import ru.wild.gui.widget.SurfaceInteractionRouter;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.gui.widget.VirtualListLayout;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.modules.visuals.Menu;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.render.StyledTextRenderer;
import ru.wild.util.text.RichTextParser;

public final class ModernClickGuiRenderer {
   private static final long instance = 1000L;
   private static final long data = 95L;
   private static final long context = 55L;
   private final ModernClickGuiState config = new ModernClickGuiState();
   private final GuiLayoutSpec state = GuiLayoutSpec.handle();
   private final ViewportLayoutState cache = new ViewportLayoutState();
   private final GuiLayoutEngine output = new GuiLayoutEngine(this.state);
   private final ModuleTooltipMeasurer current = new ModuleTooltipMeasurer();
   private final StyledTextRenderer active = new StyledTextRenderer();
   private final CoreDiagnosticsPanel mode = new CoreDiagnosticsPanel();
   private final ThemeBrowserLayout selection = new ThemeBrowserLayout(this.current, this.active, this.mode);
   private final ModernClickGuiInputHandler enabled = new ModernClickGuiInputHandler(this.selection, this.active, new RichTextParser());
   private final ThemePaletteRegistry renderer = ThemePaletteRegistry.handle();
   private FoundryWorkspace handler;
   private SpringAnimationSpec animationDraw = SpringAnimationSpec.update();
   private SpringAnimationSpec pointEncode = SpringAnimationSpec.handle();
   private SpringAnimationSpec animator = SpringAnimationSpec.process();
   private SpringAnimationSpec source = SpringAnimationSpec.compute();
   private SpringAnimationSpec target = SpringAnimationSpec.resolve();
   private SpringAnimationSpec pending = SpringAnimationSpec.prepare();
   private SpringAnimationSpec previous = SpringAnimationSpec.execute();
   private SpringAnimationSpec latest = SpringAnimationSpec.check();
   private SpringAnimationSpec summary = SpringAnimationSpec.onTick();
   private SpringAnimationSpec matrixBlend = SpringAnimationSpec.select();
   private SpringAnimationSpec vectorMatch = SpringAnimationSpec.refresh();
   private SpringAnimationSpec itemProject = SpringAnimationSpec.render();
   private SpringAnimationSpec responseCompute = SpringAnimationSpec.tick();
   private SpringAnimationSpec providerFetch = SpringAnimationSpec.drawAnimation();
   private SpringAnimationSpec profileDraw = SpringAnimationSpec.encodePoint();
   private final ModernClickGuiContentRenderer vectorPerform = new ModernClickGuiContentRenderer(
      new ThemeBrowserScreen(),
      new AutoBuyScreen(),
      new StudioScreen(),
      new AutoBuyContentRenderer(new ModuleSettingsPanel(new SettingControlRenderer())),
      this.mode,
      new HologramStatsRenderer()
   );
   private GuiMetrics eventAttach = GuiMetrics.handle(0.5F, this.state);
   private ThemeRenderContext serverRead = this.runTexture();
   private ModuleLayoutResult positionAdvance = ModuleLayoutResult.handle();
   private boolean frameCheck;
   private ThemePalette moduleCollect;
   private long providerClose;
   private boolean presetSave;
   private SurfaceHitResolver.Mode windowConvert = SurfaceHitResolver.Mode.NONE;
   private FoundryWorkspace presetWrite;
   private int colorMeasure = -1;

   void handle(MinecraftClient var1) {
      this.config.handle();
      Menu.handle(var1, this.state);
      this.config.process();
      ModulePanelRegistry.handle(this.config);
      this.frameCheck = false;
      this.moduleCollect = null;
      this.providerClose = System.currentTimeMillis();
      this.presetSave = false;
      this.validateData();
      this.process(var1);
      if (var1 != null && var1.mouse != null) {
         var1.mouse.unlockCursor();
      }

      EventDispatchBoundary.handle();
   }

   void handle(MinecraftClient var1, DrawContext var2, RoundedRectRenderer var3, int var4, int var5, float var6) {
      if (var1 != null && var1.getWindow() != null && var3 != null && var4 > 0 && var5 > 0) {
         long var7 = System.currentTimeMillis();
         if (this.presetSave || this.providerClose > 0L && var7 - this.providerClose > 1000L) {
            this.compute(var1);
         }

         this.providerClose = var7;
         this.presetSave = false;
         this.handle(var1, var4, var5);
         SurfaceInteractionRouter.handle();
         this.enabled.handle(this.config, this.config.sampleLayer());
         FoundryWorkspace var9 = !this.config.cancelIndex() && !(this.config.handle(UiAnimationKeys.blendMatrix()) > 0.0015F)
            ? this.handler
            : this.adaptScale();
         boolean var10 = var9 != null && var9.process(this.config);
         if (!var10) {
            this.vectorPerform.handle(var3, var2, this.config, this.cache, this.positionAdvance, this.serverRead, var4, var5);
         }

         if (var9 != null) {
            var9.handle(var3, this.config, this.serverRead, var4, var5);
         }
      } else {
         this.execute();
      }
   }

   void handle(float var1, float var2) {
      this.config.execute(var1);
      this.config.prepare(var2);
   }

   public boolean handle() {
      return this.config.cancelIndex() || this.config.handle(UiAnimationKeys.blendMatrix()) > 0.0015F;
   }

   boolean handle(float var1, float var2, int var3) {
      if (this.config.filterMatrix()) {
         return true;
      }

      this.config.execute(var1);
      this.config.prepare(var2);
      if (this.colorMeasure >= 0) {
         return true;
      }

      FoundryWorkspace var4 = this.computeColor();
      if (var4 != null) {
         this.presetWrite = var4;
         this.colorMeasure = var3;
         if (var3 == 0 && !var4.resolve() && SurfaceInteractionRouter.handle(var1, var2)) {
            return true;
         }

         var4.handle(this.config, this.serverRead, var1, var2, var3, this.renderScale(), this.refreshClient());
         return true;
      } else {
         if (this.handle()) {
            return true;
         }

         if (this.config.checkFrame()) {
            this.windowConvert = SurfaceHitResolver.Mode.MAIN;
            this.colorMeasure = var3;
            this.enabled
               .handle(
                  this.config, this.cache, this.positionAdvance, this.eventAttach, this.renderer, SurfaceHitResolver.Mode.MAIN, var1, var2, var1, var2, var3
               );
            return true;
         }

         SurfaceHitResolver.DataRecord var5 = SurfaceHitResolver.handle(this.bindIndex(), var1, var2);
         if (!var5.blocksLower()) {
            if (var3 == 0) {
               this.config.check(false);
               this.config.onTick(false);
               this.config.handle((StringSetting)null);
               this.config.performVector();
            }

            return true;
         } else {
            if (!var5.interactive()) {
               return true;
            }

            this.windowConvert = var5.surface();
            this.colorMeasure = var3;
            this.config.blendMatrix(var5.surface() == SurfaceHitResolver.Mode.THEME);
            boolean var6 = var5.surface() == SurfaceHitResolver.Mode.MAIN
               && var3 == 0
               && ModernClickGuiInputHandler.handle(this.cache, this.eventAttach, var5.localX(), var5.localY());
            if (var6 && this.vectorPerform.process() != null) {
               this.vectorPerform.process().process();
            }

            if (var5.surface() == SurfaceHitResolver.Mode.MAIN
               && this.readAction()
               && this.vectorPerform.process() != null
               && !var6
               && this.vectorPerform.process().handle(this.config, this.cache, this.serverRead, var5.localX(), var5.localY(), var3)) {
               return true;
            }

            if (var5.surface() == SurfaceHitResolver.Mode.MAIN
               && this.config.renderScale()
               && this.vectorPerform.handle() != null
               && this.vectorPerform.handle().handle(this.config, this.serverRead, var5.localX(), var5.localY(), var3)) {
               return true;
            }

            this.enabled
               .handle(
                  this.config,
                  this.cache,
                  this.positionAdvance,
                  this.eventAttach,
                  this.renderer,
                  var5.surface(),
                  var1,
                  var2,
                  var5.localX(),
                  var5.localY(),
                  var3
               );
            return true;
         }
      }
   }

   boolean process(float var1, float var2, int var3) {
      this.config.execute(var1);
      this.config.prepare(var2);
      if (this.colorMeasure < 0) {
         return false;
      }

      if (var3 != this.colorMeasure) {
         return true;
      }

      if (this.presetWrite != null) {
         SurfaceInteractionRouter.process();
         this.presetWrite.process(this.config, var1, var2);
         this.enabled.handle(this.config);
         this.validateData();
         return true;
      }

      float var4 = this.handle(this.windowConvert, var1);
      float var5 = this.process(this.windowConvert, var2);
      if (this.windowConvert == SurfaceHitResolver.Mode.MAIN && this.config.refreshClient() && this.vectorPerform.process() != null) {
         this.vectorPerform.process().handle(var4, var5, var3);
      }

      if (this.windowConvert == SurfaceHitResolver.Mode.MAIN && this.config.renderScale() && this.vectorPerform.handle() != null) {
         this.vectorPerform.handle().handle(this.config, var4, var5);
      }

      this.enabled.handle(this.config, var1, var2, var4, var5);
      this.validateData();
      return true;
   }

   boolean handle(float var1, float var2, int var3, float var4, float var5) {
      if (this.config.filterMatrix()) {
         return true;
      }

      this.config.execute(var1);
      this.config.prepare(var2);
      if (this.colorMeasure < 0 || var3 != this.colorMeasure) {
         return true;
      }

      if (this.presetWrite == null) {
         float var6 = this.handle(this.windowConvert, var1);
         float var7 = this.process(this.windowConvert, var2);
         if (this.windowConvert == SurfaceHitResolver.Mode.MAIN
            && this.readAction()
            && this.vectorPerform.process() != null
            && this.vectorPerform.process().handle(var6, var7, var3, var4, var5)) {
            return true;
         }

         if (this.windowConvert == SurfaceHitResolver.Mode.MAIN
            && this.config.renderScale()
            && this.vectorPerform.handle() != null
            && this.vectorPerform.handle().process(this.config, var6, var7)) {
            return true;
         }

         this.enabled.handle(this.config, this.cache, this.eventAttach, var1, var2, var6, var7);
         if (this.config.submitScreen() || this.config.runListener()) {
            this.process(MinecraftClient.getInstance());
         } else if (this.config.cancelRenderer() || this.config.receiveEvent()) {
            this.output.handle(this.eventAttach, this.config, this.cache);
         }

         return true;
      } else {
         if (SurfaceInteractionRouter.process(var1, var2)) {
            return true;
         }

         this.presetWrite.handle(this.config, var1, var2);
         return true;
      }
   }

   boolean handle(float var1, float var2, double var3, double var5) {
      if (this.config.filterMatrix()) {
         return true;
      }

      FoundryWorkspace var7 = this.computeColor();
      if (var7 != null) {
         return var7.handle(this.config, var1, var2, var5);
      }

      if (this.handle()) {
         return true;
      }

      SurfaceHitResolver.DataRecord var8 = SurfaceHitResolver.handle(this.bindIndex(), var1, var2);
      if (!var8.blocksLower()) {
         return false;
      }

      if (!var8.interactive()) {
         return true;
      }

      if (var8.surface() == SurfaceHitResolver.Mode.MAIN
         && this.readAction()
         && this.vectorPerform.process() != null
         && this.vectorPerform.process().handle(this.config, this.cache, this.serverRead, var8.localX(), var8.localY(), var5)) {
         return true;
      }

      if (var8.surface() == SurfaceHitResolver.Mode.MAIN
         && this.config.renderScale()
         && this.vectorPerform.handle() != null
         && this.vectorPerform.handle().handle(this.config, var8.localX(), var8.localY(), var5)) {
         return true;
      }

      this.enabled.handle(this.config, this.cache, this.positionAdvance, this.eventAttach, var8.surface(), var8.localX(), var8.localY(), var3, var5);
      return true;
   }

   boolean handle(int var1) {
      if (this.config.filterMatrix()) {
         return true;
      }

      if (var1 == 84 && Screen.hasControlDown()) {
         this.collapseConfig();
         if (this.vectorPerform.process() != null) {
            this.vectorPerform.process().process();
         }

         this.config.collectModule();
         if (this.config.cancelIndex()) {
            this.config.matchVector(false);
         } else {
            this.config.matchVector(this.adaptScale() != null);
         }

         return true;
      } else if (var1 == 77 && Screen.hasControlDown()) {
         this.collapseConfig();
         if (this.vectorPerform.process() != null) {
            this.vectorPerform.process().process();
         }

         this.config.collectModule();
         this.config.matchVector(false);
         this.config.render();
         return true;
      } else {
         FoundryWorkspace var2 = this.computeColor();
         if (var2 != null) {
            return var2.handle(this.config, var1);
         }

         if (this.readAction() && this.vectorPerform.process() != null && this.vectorPerform.process().handle(this.config, var1)) {
            return true;
         }

         if (this.config.renderScale() && this.vectorPerform.handle() != null && this.vectorPerform.handle().handle(this.config, var1)) {
            return true;
         }

         if (this.config.receiveRequest()) {
            if (Screen.hasControlDown()) {
               switch (var1) {
                  case 65:
                     if (!this.config.encodeVector().isEmpty()) {
                        this.config.prepare(true);
                     }

                     return true;
                  case 67:
                     if (!this.config.encodeVector().isEmpty()) {
                        MinecraftClient.getInstance().keyboard.setClipboard(this.config.encodeVector());
                     }

                     return true;
                  case 86:
                     String var3 = MinecraftClient.getInstance().keyboard.getClipboard();
                     if (var3 != null && !var3.isEmpty()) {
                        if (this.config.measureTimer()) {
                           this.config.compute("");
                        }

                        String var4 = this.config.encodeVector();
                        int var5 = 96 - var4.length();
                        if (var5 > 0) {
                           String var6 = var3.length() > var5 ? var3.substring(0, var5) : var3;
                           this.config.compute(var4 + var6);
                        }

                        this.config.prepare(false);
                        this.config.resolve(System.currentTimeMillis());
                     }

                     return true;
                  case 88:
                     if (!this.config.encodeVector().isEmpty()) {
                        MinecraftClient.getInstance().keyboard.setClipboard(this.config.encodeVector());
                        this.config.compute("");
                        this.config.prepare(false);
                        this.config.resolve(System.currentTimeMillis());
                     }

                     return true;
               }
            } else if (var1 == 263 || var1 == 262) {
               this.config.prepare(false);
            }
         }

         if (var1 == 70 && Screen.hasControlDown()) {
            this.config.collectModule();
            this.config.onTick(false);
            this.config.check(true);
            this.config.prepare(true);
            return true;
         } else {
            return this.enabled.handle(this.config, var1);
         }
      }
   }

   boolean handle(char var1) {
      if (this.config.filterMatrix()) {
         return true;
      } else {
         FoundryWorkspace var2 = this.computeColor();
         if (var2 != null) {
            var2.handle(this.config, var1);
            return true;
         } else if (this.readAction() && this.vectorPerform.process() != null && this.vectorPerform.process().handle(var1)) {
            return true;
         } else {
            return this.config.renderScale() && this.vectorPerform.handle() != null && this.vectorPerform.handle().handle(this.config, var1)
               ? true
               : this.enabled.handle(this.config, var1);
         }
      }
   }

   boolean process() {
      if (this.config.filterMatrix()) {
         return false;
      }

      this.collapseConfig();
      this.config.compute();
      return true;
   }

   boolean compute() {
      return this.config.process(this.animationDraw);
   }

   boolean resolve() {
      return this.config.filterMatrix();
   }

   public boolean update() {
      return this.config.cancelIndex()
         || this.readAction() && this.vectorPerform.process() != null && this.vectorPerform.process().handle()
         || this.config.renderScale() && this.vectorPerform.handle() != null && this.vectorPerform.handle().handle()
         || this.config.receiveRequest()
         || this.config.savePacket()
         || this.config.filterEntity() != null
         || this.config.checkFrame();
   }

   public void apply() {
      this.process(MinecraftClient.getInstance());
   }

   void execute() {
      this.presetSave = true;
      this.saveScale();
   }

   void prepare() {
      if (!this.frameCheck) {
         this.frameCheck = true;
         this.collapseConfig();
         SurfaceInteractionRouter.compute();
         this.config.collectModule();
         this.config.check(false);
         this.config.matchVector(false);
         FoundryWorkspace var1 = this.handler;
         if (var1 != null) {
            var1.close();
         }

         FoundryAssetBrowser var2 = this.vectorPerform.handle();
         if (var2 != null) {
            var2.process();
         }

         if (this.vectorPerform.process() != null) {
            this.vectorPerform.process().process();
         }

         ModulePanelRegistry.process(this.config);
         if (WildClient.instance != null && WildClient.instance.selection != null) {
            WildClient.instance.selection.handle(this.config.bindIndex());
            WildClient.instance.selection.handle(this.config.adaptRequest());
            WildClient.instance.selection.handle(this.config.parseMessage(), this.config.readProvider());
            WildClient.instance.selection.handle(this.config.synchronizePoint());
         }
      }
   }

   private void process(MinecraftClient var1) {
      if (var1 != null && var1.getWindow() != null) {
         int var2 = var1.getWindow().getFramebufferWidth();
         int var3 = var1.getWindow().getFramebufferHeight();
         if (var2 > 0 && var3 > 0) {
            this.handle(var1, var2, var3);
         }
      }
   }

   private void handle(MinecraftClient var1, int var2, int var3) {
      this.scheduleSource();
      this.process(var1, var2, var3);
      this.renderTimer();
      this.config.compute(this.profileDraw);
      this.serverRead = this.runTexture();
      this.config.handle(this.animationDraw);
      this.filterKey();
      this.positionAdvance = this.current.handle(this.config, this.cache, this.eventAttach);
      this.config.handle(this.positionAdvance.compute());
      this.config.handle(this.eventAttach);
      this.config.process(this.previous, this.eventAttach);
      this.positionAdvance = this.current.handle(this.config, this.cache, this.eventAttach);
      this.receiveRequest();
      this.collectPlayer();
   }

   private void scheduleSource() {
      this.animationDraw = SpringAnimationSpec.update();
      this.pointEncode = SpringAnimationSpec.handle();
      this.animator = SpringAnimationSpec.process();
      this.source = SpringAnimationSpec.compute();
      this.target = SpringAnimationSpec.resolve();
      this.pending = SpringAnimationSpec.prepare();
      this.previous = SpringAnimationSpec.execute();
      this.latest = SpringAnimationSpec.check();
      this.summary = SpringAnimationSpec.onTick();
      this.matrixBlend = SpringAnimationSpec.select();
      this.vectorMatch = SpringAnimationSpec.refresh();
      this.itemProject = SpringAnimationSpec.render();
      this.responseCompute = SpringAnimationSpec.tick();
      this.providerFetch = SpringAnimationSpec.drawAnimation();
      this.profileDraw = SpringAnimationSpec.encodePoint();
   }

   private void renderTimer() {
      ThemePalette var1 = this.config.adaptRequest();
      ThemeColors var2 = ThemeColors.handle(var1, this.handle(var1));
      if (this.moduleCollect == null) {
         this.config.handle(var2);
      } else if (this.moduleCollect != var1) {
         this.config.process(this.moduleCollect);
         this.config.process(var2);
      }

      this.moduleCollect = var1;
   }

   private void process(MinecraftClient var1, int var2, int var3) {
      if (var1 != null && var1.getWindow() != null) {
         int var4 = var2 > 0 ? var2 : var1.getWindow().getFramebufferWidth();
         int var5 = var3 > 0 ? var3 : var1.getWindow().getFramebufferHeight();
         if (var4 <= 0 || var5 <= 0) {
            return;
         }

         this.eventAttach = this.output.handle(var1, var4, var5, this.config, this.cache);
         ModuleStateHelper.handle(this.eventAttach);
      }
   }

   private void compute(MinecraftClient var1) {
      this.saveScale();
      if (var1 != null && var1.mouse != null) {
         var1.mouse.unlockCursor();
      }
   }

   private void saveScale() {
      this.collapseConfig();
      SurfaceInteractionRouter.compute();
      this.config.collectModule();
      this.config.check(false);
      this.config.onTick(false);
      this.config.readServer();
   }

   private FoundryWorkspace computeColor() {
      if (this.config.cancelIndex() && !this.config.filterMatrix()) {
         FoundryWorkspace var1 = this.handler;
         if (var1 != null && var1.handle(this.config)) {
            return var1;
         }

         var1 = this.adaptScale();
         return var1 != null && var1.handle(this.config) ? var1 : null;
      } else {
         return null;
      }
   }

   private FoundryWorkspace adaptScale() {
      if (this.handler != null) {
         return this.handler;
      }

      try {
         this.handler = new FoundryWorkspace();
         return this.handler;
      } catch (Throwable var2) {
         this.config.matchVector(false);
         System.out.println("[ModernClickGui] Foundry init failed: " + var2.getClass().getSimpleName() + ": " + var2.getMessage());
         return null;
      }
   }

   private ThemeRenderContext runTexture() {
      ThemePalette var1 = this.config.adaptRequest();
      ThemeColors var2 = this.config.scanRenderer();
      if (var2 == null) {
         var2 = ThemeColors.handle(var1, this.handle(var1));
      }

      if (this.config.attachState() <= 0L) {
         var2 = ThemeColors.handle(var1, var2, System.currentTimeMillis());
      }

      return ThemeRenderContext.compute().handle(var1).handle(this.eventAttach).handle(var2).handle(this.renderer).handle();
   }

   private boolean handle(ThemePalette var1) {
      return this.renderer != null && this.renderer.compute(var1);
   }

   private boolean process(float var1, float var2) {
      return ModuleStateHelper.handle(var1, var2, this.cache.handle(), this.cache.process(), this.eventAttach.resolve(), this.eventAttach.update());
   }

   private SurfaceHitResolver.PrimaryDataRecord bindIndex() {
      float var1 = 0.94F + this.config.resolve() * 0.06F;
      float var2 = this.config.handle(UiAnimationKeys.tick());
      return new SurfaceHitResolver.PrimaryDataRecord(
         new SurfaceHitResolver.Bounds(
            this.cache.handle(), this.cache.process(), this.eventAttach.resolve(), this.eventAttach.update(), this.eventAttach.handle(24.0F), var1
         ),
         new SurfaceHitResolver.Bounds(
            this.cache.projectItem(), this.cache.computeResponse(), this.eventAttach.save(), this.eventAttach.submit(), this.eventAttach.process(14.0F), var1
         ),
         var2 > 0.005F,
         this.config.synchronizePoint() && var2 > 0.035F && !this.config.filterMatrix(),
         this.config.bindSession()
      );
   }

   private boolean readAction() {
      return this.config.refreshClient() && !this.config.executeCache() && this.config.handle(UiAnimationKeys.animate()) <= 0.01F;
   }

   private float handle(SurfaceHitResolver.Mode var1, float var2) {
      SurfaceHitResolver.PrimaryDataRecord var3 = this.bindIndex();
      SurfaceHitResolver.Bounds var4 = var1 == SurfaceHitResolver.Mode.THEME ? var3.theme() : var3.main();
      return var4 == null ? var2 : var4.localX(var2);
   }

   private float process(SurfaceHitResolver.Mode var1, float var2) {
      SurfaceHitResolver.PrimaryDataRecord var3 = this.bindIndex();
      SurfaceHitResolver.Bounds var4 = var1 == SurfaceHitResolver.Mode.THEME ? var3.theme() : var3.main();
      return var4 == null ? var2 : var4.localY(var2);
   }

   private void collapseConfig() {
      if (this.presetWrite != null) {
         this.presetWrite.process(this.config, this.config.sampleLayer(), this.config.sendWorld());
      }

      if (this.windowConvert == SurfaceHitResolver.Mode.MAIN && this.config.refreshClient() && this.vectorPerform.process() != null) {
         this.vectorPerform
            .process()
            .handle(this.handle(this.windowConvert, this.config.sampleLayer()), this.process(this.windowConvert, this.config.sendWorld()), this.colorMeasure);
      }

      if (this.windowConvert == SurfaceHitResolver.Mode.MAIN && this.config.renderScale() && this.vectorPerform.handle() != null) {
         this.vectorPerform
            .handle()
            .handle(this.config, this.handle(this.windowConvert, this.config.sampleLayer()), this.process(this.windowConvert, this.config.sendWorld()));
      }

      this.enabled.handle(this.config);
      this.validateData();
   }

   private void validateData() {
      this.windowConvert = SurfaceHitResolver.Mode.NONE;
      this.presetWrite = null;
      this.colorMeasure = -1;
   }

   private int renderScale() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.getWindow() != null ? Math.max(1, var1.getWindow().getFramebufferWidth()) : Math.round(this.eventAttach.resolve());
   }

   private int refreshClient() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.getWindow() != null ? Math.max(1, var1.getWindow().getFramebufferHeight()) : Math.round(this.eventAttach.update());
   }
   private void filterKey() {
      float var1 = this.config.sampleLayer();
      float var2 = this.config.sendWorld();
      SurfaceHitResolver.Bounds var3 = this.bindIndex().main();
      this.config.execute(var3.localX(var1));
      this.config.prepare(var3.localY(var2));
      boolean var17 = false /* VF: Semaphore variable */;

      try {
         var17 = true;
         boolean var4 = !this.config.filterMatrix() && this.config.receiveRequest();
         boolean var5 = !this.config.filterMatrix() && !this.config.encodeVector().isEmpty();
         boolean var6 = !this.config.filterMatrix() && this.config.synchronizePoint();
         boolean var7 = !this.config.filterMatrix() && this.config.cancelIndex();
         boolean var8 = !this.config.filterMatrix() && this.config.validateData();
         this.config.handle(UiAnimationKeys.handle(), var4 ? 1.0F : 0.0F, this.handle(var4, this.pending));
         this.config.handle(UiAnimationKeys.render(), var5 ? 1.0F : 0.0F, this.handle(var5, this.pointEncode));
         this.config.handle(UiAnimationKeys.tick(), var6 ? 1.0F : 0.0F, var6 ? this.animationDraw : this.pointEncode);
         this.config.handle(UiAnimationKeys.blendMatrix(), var7 ? 1.0F : 0.0F, var7 ? this.animationDraw : this.providerFetch);
         boolean var9 = !this.config.filterMatrix()
            && ModuleStateHelper.handle(
               this.config,
               StudioScreen.apply(this.cache, this.eventAttach),
               StudioScreen.execute(this.cache, this.eventAttach),
               StudioScreen.handle(this.eventAttach),
               StudioScreen.update(this.cache, this.eventAttach)
            );
         boolean var10 = var9 || var7;
         this.config.handle(UiAnimationKeys.matchVector(), var10 ? 1.0F : 0.0F, this.handle(var10, this.summary));
         boolean var11 = !this.config.filterMatrix() && this.config.renderScale();
         boolean var12 = StudioProfileGate.handle()
            && !this.config.filterMatrix()
            && ModuleStateHelper.handle(
               this.config,
               StudioScreen.compute(this.cache, this.eventAttach),
               StudioScreen.resolve(this.cache, this.eventAttach),
               StudioScreen.handle(this.cache, this.eventAttach),
               StudioScreen.process(this.cache, this.eventAttach)
            );
         boolean var13 = var12 || var11;
         this.config.handle(UiAnimationKeys.computeResponse(), var13 ? 1.0F : 0.0F, this.handle(var13, this.summary));
         this.config.handle(UiAnimationKeys.check(), var8 ? 1.0F : 0.0F, var8 ? this.animationDraw : this.providerFetch);
         this.adaptRequest();
         this.encodeVector();
         this.processWindow();
         this.savePacket();
         this.animateEntry();
         SurfaceHitResolver.Bounds var14 = this.bindIndex().theme();
         this.config.execute(var14.localX(var1));
         this.config.prepare(var14.localY(var2));
         this.measureTimer();
         var17 = false;
      } finally {
         if (var17) {
            this.config.execute(var1);
            this.config.prepare(var2);
         }
      }

      this.config.execute(var1);
      this.config.prepare(var2);
   }

   private void adaptRequest() {
      float var1 = AutoBuyScreen.compute(this.cache, this.eventAttach);
      float var2 = AutoBuyScreen.update(this.cache, this.eventAttach);
      ModuleCategory[] var3 = ModuleCategory.values();

      for (int var4 = 0; var4 < var3.length; var4++) {
         ModuleCategory var5 = var3[var4];
         float var6 = var2 + var4 * AutoBuyScreen.compute(this.eventAttach);
         boolean var7 = !this.config.filterMatrix()
            && ModuleStateHelper.handle(this.config, var1, var6, this.eventAttach.handle(40.0F), this.eventAttach.handle(40.0F));
         boolean var8 = !this.config.filterMatrix()
            && !this.config.collapseConfig()
            && !this.config.validateData()
            && !this.config.renderScale()
            && !this.config.refreshClient()
            && var5 == this.config.bindIndex();
         this.config.handle(UiAnimationKeys.handle(var5), var7 ? 1.0F : 0.0F, this.handle(var7, this.summary));
         this.config.handle(UiAnimationKeys.process(var5), var8 ? 1.0F : 0.0F, this.handle(var8, this.pointEncode));
      }

      float var14 = AutoBuyScreen.process(this.eventAttach);
      float var15 = AutoBuyScreen.apply(this.cache, this.eventAttach);
      boolean var16 = !this.config.filterMatrix() && ModuleStateHelper.handle(this.config, var1, var15, var14, var14);
      boolean var17 = !this.config.filterMatrix() && this.config.collapseConfig();
      this.config.handle(UiAnimationKeys.update(), var16 ? 1.0F : 0.0F, this.handle(var16, this.summary));
      this.config.handle(UiAnimationKeys.apply(), var17 ? 1.0F : 0.0F, this.handle(var17, this.pointEncode));
      float var18 = AutoBuyScreen.execute(this.cache, this.eventAttach);
      boolean var9 = !this.config.filterMatrix() && ModuleStateHelper.handle(this.config, var1, var18, var14, var14);
      boolean var10 = !this.config.filterMatrix() && this.config.refreshClient();
      this.config.handle(UiAnimationKeys.execute(), var9 ? 1.0F : 0.0F, this.handle(var9, this.summary));
      this.config.handle(UiAnimationKeys.prepare(), var10 ? 1.0F : 0.0F, this.handle(var10, this.pointEncode));
      float var11 = AutoBuyScreen.compute(this.cache, this.eventAttach);
      float var12 = AutoBuyScreen.resolve(this.cache, this.eventAttach);
      boolean var13 = !this.config.filterMatrix() && ModuleStateHelper.handle(this.config, var11, var12, var14, var14);
      this.config.handle(UiAnimationKeys.process(), var13 ? 1.0F : 0.0F, this.handle(var13, this.summary));
   }

   private void measureTimer() {
      if (!this.config.synchronizePoint() && this.config.handle(UiAnimationKeys.tick()) <= 0.005F) {
         this.config.process(0.0F);
      } else {
         VirtualListLayout var1 = VirtualListLayout.handle(this.cache, this.eventAttach);
         List<Integer> var2 = this.config.handle(this.renderer);
         this.config.process(Math.max(0.0F, var1.handle(var2.size()) - var1.resolve() + this.eventAttach.process(2.0F)));
         float var3 = this.config.handle(this.previous, this.eventAttach);
         float var4 = var1.handle();
         float var5 = var1.process();
         float var6 = var1.compute();
         float var7 = var1.resolve();
         boolean var8 = !this.config.filterMatrix() && ModuleStateHelper.handle(this.config, var4, var5, var6, var7);
         boolean var9 = !this.config.filterMatrix() && this.config.savePacket();
         boolean var10 = !this.config.filterMatrix() && !this.config.processWindow().isEmpty();
         this.config.handle(UiAnimationKeys.drawAnimation(), var9 ? 1.0F : 0.0F, this.handle(var9, this.pending));
         this.config.handle(UiAnimationKeys.encodePoint(), var10 ? 1.0F : 0.0F, this.handle(var10, this.pointEncode));
         int var11 = this.config.sendEvent() >= 0 ? this.config.sendEvent() : this.renderer.handle(this.config.adaptRequest());
         int var12 = this.renderer.compute().size();
         int[] var13 = new int[var12];
         Arrays.fill(var13, -1);
         int var14 = 0;

         while (var14 < var2.size()) {
            var13[var2.get(var14)] = var14++;
         }

         for (int var19 = 0; var19 < var12; var19++) {
            int var15 = var13[var19];
            if (var15 < 0) {
               this.config.handle(UiAnimationKeys.handle(var19), 0.0F, this.providerFetch);
               this.config.handle(UiAnimationKeys.process(var19), 0.0F, this.providerFetch);
            } else {
               VirtualListLayout.Bounds var16 = var1.handle(var15, var3);
               boolean var17 = var8 && ModuleStateHelper.handle(this.config, var16.x(), var16.y(), var16.width(), var16.height());
               boolean var18 = !this.config.filterMatrix() && var19 == var11;
               this.config.handle(UiAnimationKeys.handle(var19), var17 ? 1.0F : 0.0F, this.handle(var17, this.matrixBlend));
               this.config.handle(UiAnimationKeys.process(var19), var18 ? 1.0F : 0.0F, this.handle(var18, this.pointEncode));
            }
         }
      }
   }

   private void encodeVector() {
      List var1 = this.config.update();
      HashSet var2 = new HashSet<>(this.config.apply());

      for (Module var4 : (List<Module>) var1) {
         boolean var5 = var2.contains(var4) && !this.config.filterMatrix();
         if (var5) {
            boolean var6 = this.config.invokeProfile().contains(var4);
            boolean var7 = var4.enabled;
            this.config.handle(UiAnimationKeys.handle(var4), var6 ? 1.0F : 0.0F, var6 ? this.animator : this.source);
            this.config.handle(UiAnimationKeys.resolve(var4), var6 ? 1.0F : 0.0F, this.handle(var6, this.pointEncode));
            this.config.handle(UiAnimationKeys.compute(var4), var7 ? 1.0F : 0.0F, this.handle(var7, this.pointEncode));
            if (ModulePanelRegistry.process(var4)) {
               ModulePanelRegistry.handle(var4, this.config, this.pointEncode, this.latest);
            } else {
               this.handle(var4);
            }
         } else {
            this.config.handle(UiAnimationKeys.handle(var4), 0.0F, this.providerFetch);
            this.config.handle(UiAnimationKeys.resolve(var4), 0.0F, this.providerFetch);
            this.config.handle(UiAnimationKeys.compute(var4), 0.0F, this.providerFetch);
         }
      }
   }

   private void receiveRequest() {
      List var1 = this.config.update();
      HashSet var2 = new HashSet<>(this.config.apply());
      long var3 = System.currentTimeMillis() - this.config.renderAction();
      HashMap var5 = new HashMap();

      for (ModulePlacement var7 : this.positionAdvance.process()) {
         var5.put(var7.handle(), var7);
      }

      float var16 = this.cache.submit();
      float var17 = var16 + this.cache.fetch();
      int var8 = 0;

      for (Module var10 : (List<Module>) var1) {
         if (!this.config.filterMatrix() && var2.contains(var10)) {
            ModulePlacement var11 = (ModulePlacement)var5.get(var10);
            boolean var12 = var11 != null && var11.compute() + var11.update() >= var16 && var11.compute() <= var17;
            if (var12) {
               long var13 = 95L + var8 * 55L;
               float var15 = var3 >= var13 ? 1.0F : 0.0F;
               this.config.process(UiAnimationKeys.apply(var10), var15, var15 > 0.0F ? this.pointEncode : this.providerFetch);
               this.config.process(UiAnimationKeys.execute(var10), var15, var15 > 0.0F ? this.vectorMatch : this.providerFetch);
               this.config.process(UiAnimationKeys.prepare(var10), var15, var15 > 0.0F ? this.responseCompute : this.providerFetch);
               var8++;
            } else {
               this.handle(UiAnimationKeys.apply(var10));
               this.handle(UiAnimationKeys.execute(var10));
               this.handle(UiAnimationKeys.prepare(var10));
            }
         } else {
            this.config.handle(UiAnimationKeys.apply(var10), 0.0F, this.providerFetch);
            this.config.handle(UiAnimationKeys.execute(var10), 0.0F, this.providerFetch);
            this.config.handle(UiAnimationKeys.prepare(var10), 0.0F, this.providerFetch);
         }
      }
   }

   private void handle(String var1) {
      this.config.scheduleSource().computeIfAbsent(var1, var0 -> new SpringAnimation(1.0F)).handle(1.0F);
   }

   private void handle(Module var1) {
      boolean var2 = !this.config.filterMatrix() && this.config.invokeProfile().contains(var1);
      long var3 = this.config.compute(var1);
      long var5 = System.currentTimeMillis();
      int var7 = 0;

      for (Setting var9 : var1.apply()) {
         if (!(var9 instanceof FloatSetting)) {
            float var10;
            if (var2) {
               if (var3 > 0L) {
                  long var11 = 60L + var7 * 55L;
                  var10 = var5 - var3 > var11 ? 1.0F : 0.0F;
               } else {
                  var10 = 1.0F;
               }
            } else {
               var10 = 0.0F;
            }

            this.config.process(UiAnimationKeys.resolve(var9), var10, var10 > 0.0F ? this.target : this.source);
            if (var9 instanceof BooleanSetting var26) {
               boolean var31 = !this.config.filterMatrix() && var26.compute();
               this.config.handle(UiAnimationKeys.handle(var9), var31 ? 1.0F : 0.0F, this.handle(var31, this.pointEncode));
            } else if (var9 instanceof NumberSetting var12) {
               float var30 = !this.config.filterMatrix() ? ModuleStateHelper.handle(var12) : 0.0F;
               boolean var33 = !this.config.filterMatrix() && this.config.encodeSession() == var12;
               this.config.handle(UiAnimationKeys.handle(var9), var30, var30 > 0.0F ? this.pointEncode : this.providerFetch);
               this.config.handle(UiAnimationKeys.apply(var9), var30, var30 > 0.0F ? this.latest : this.providerFetch);
               this.config.handle(UiAnimationKeys.execute(var9), var33 ? 1.0F : 0.0F, this.handle(var33, this.pointEncode));
            } else if (var9 instanceof ColorSetting var13) {
               float var29 = !this.config.filterMatrix() ? ModuleStateHelper.handle(var13) : 0.0F;
               this.config.handle(UiAnimationKeys.handle(var9), var29, var29 > 0.0F ? this.pointEncode : this.providerFetch);
               boolean var32 = !this.config.filterMatrix() && this.config.evaluateWorld() == var13;
               float var19 = this.config.handle(UiAnimationKeys.prepare(var9));
               this.config.handle(UiAnimationKeys.prepare(var9), var32 ? 1.0F : 0.0F, this.handle(var32, this.pointEncode));
               if (var32) {
                  float var20 = var13.renderer;
                  float var21 = 1.0F - var13.handler;
                  if (var19 < 0.01F) {
                     SpringAnimation var22 = this.config.scheduleSource().computeIfAbsent(UiAnimationKeys.check(var9), var1x -> new SpringAnimation(var20));
                     var22.handle(var20);
                     SpringAnimation var23 = this.config.scheduleSource().computeIfAbsent(UiAnimationKeys.onTick(var9), var1x -> new SpringAnimation(var21));
                     var23.handle(var21);
                     SpringAnimation var24 = this.config.scheduleSource().computeIfAbsent(UiAnimationKeys.select(var9), var1x -> new SpringAnimation(var29));
                     var24.handle(var29);
                     SpringAnimation var25 = this.config
                        .scheduleSource()
                        .computeIfAbsent(UiAnimationKeys.refresh(var9), var1x -> new SpringAnimation(var13.animationDraw));
                     var25.handle(var13.animationDraw);
                  } else {
                     this.config.handle(UiAnimationKeys.check(var9), var20, this.latest);
                     this.config.handle(UiAnimationKeys.onTick(var9), var21, this.latest);
                     this.config.handle(UiAnimationKeys.select(var9), var29, this.latest);
                     this.config.handle(UiAnimationKeys.refresh(var9), var13.animationDraw, this.latest);
                  }
               } else {
                  this.config.handle(UiAnimationKeys.check(var9), var13.renderer, this.providerFetch);
                  this.config.handle(UiAnimationKeys.onTick(var9), 1.0F - var13.handler, this.providerFetch);
                  this.config.handle(UiAnimationKeys.select(var9), var29, this.providerFetch);
                  this.config.handle(UiAnimationKeys.refresh(var9), var13.animationDraw, this.providerFetch);
               }
            } else if (var9 instanceof ModeSetting var14) {
               boolean var28 = !this.config.filterMatrix() && var14.active;
               this.config.handle(UiAnimationKeys.update(var9), var28 ? 1.0F : 0.0F, this.handle(var28, this.pointEncode));
            } else if (var9 instanceof ShaderPresetSetting var15) {
               boolean var27 = !this.config.filterMatrix() && var15.state;
               this.config.handle(UiAnimationKeys.update(var9), var27 ? 1.0F : 0.0F, this.handle(var27, this.pointEncode));
            } else if (var9 instanceof ChoiceSetting var16) {
               for (int var17 = 0; var17 < var16.config.size(); var17++) {
                  boolean var18 = !this.config.filterMatrix() && var16.config.get(var17).compute();
                  this.config.handle(UiAnimationKeys.handle(var9, var17), var18 ? 1.0F : 0.0F, this.handle(var18, this.pointEncode));
               }
            }

            var7++;
         }
      }
   }

   private void processWindow() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         AutoBuy var1 = WildClient.instance.data.handle(AutoBuy.class);
         if (var1 != null) {
            ModulePanelRegistry.handle(var1, this.config, this.pointEncode, this.latest);
            boolean var2 = !this.config.filterMatrix() && var1.enabled;
            this.config.handle(UiAnimationKeys.compute(var1), var2 ? 1.0F : 0.0F, this.handle(var2, this.pointEncode));
         }
      }
   }

   private void savePacket() {
      boolean var1 = !this.config.filterMatrix() && this.config.executeCache();
      this.config.handle(UiAnimationKeys.animate(), var1 ? 1.0F : 0.0F, this.handle(var1, this.itemProject));
   }

   private void animateEntry() {
      if (!this.config.executeCache() && !this.config.validateData()) {
         boolean var1 = false;
         float var2 = AutoBuyScreen.compute(this.cache, this.eventAttach);
         float var3 = AutoBuyScreen.update(this.cache, this.eventAttach);
         if (ModuleStateHelper.handle(
            this.config,
            AutoBuyScreen.handle(this.cache, this.eventAttach),
            AutoBuyScreen.process(this.cache, this.eventAttach),
            AutoBuyScreen.handle(this.eventAttach),
            AutoBuyScreen.handle(this.eventAttach)
         )) {
            var1 = true;
         }

         if (ModuleStateHelper.handle(
            this.config,
            StudioScreen.apply(this.cache, this.eventAttach),
            StudioScreen.execute(this.cache, this.eventAttach),
            StudioScreen.handle(this.eventAttach),
            StudioScreen.update(this.cache, this.eventAttach)
         )) {
            var1 = true;
         }

         if (StudioProfileGate.handle()
            && ModuleStateHelper.handle(
               this.config,
               StudioScreen.compute(this.cache, this.eventAttach),
               StudioScreen.resolve(this.cache, this.eventAttach),
               StudioScreen.handle(this.cache, this.eventAttach),
               StudioScreen.process(this.cache, this.eventAttach)
            )) {
            var1 = true;
         }

         ModuleCategory[] var4 = ModuleCategory.values();

         for (int var5 = 0; var5 < var4.length; var5++) {
            float var6 = var3 + var5 * AutoBuyScreen.compute(this.eventAttach);
            if (ModuleStateHelper.handle(this.config, var2, var6, this.eventAttach.handle(40.0F), this.eventAttach.handle(40.0F))) {
               var1 = true;
               break;
            }
         }

         float var11 = AutoBuyScreen.process(this.eventAttach);
         float var12 = AutoBuyScreen.apply(this.cache, this.eventAttach);
         if (ModuleStateHelper.handle(this.config, var2, var12, var11, var11)) {
            var1 = true;
         }

         float var7 = AutoBuyScreen.execute(this.cache, this.eventAttach);
         if (ModuleStateHelper.handle(this.config, var2, var7, var11, var11)) {
            var1 = true;
         }

         float var8 = AutoBuyScreen.compute(this.cache, this.eventAttach);
         float var9 = AutoBuyScreen.resolve(this.cache, this.eventAttach);
         if (ModuleStateHelper.handle(this.config, var8, var9, var11, var11)) {
            var1 = true;
         }

         if (!var1) {
            this.config.readServer();
         }

         boolean var10 = !this.config.filterMatrix() && this.config.advancePosition();
         this.config.handle(UiAnimationKeys.load(), var10 ? 1.0F : 0.0F, this.handle(var10, this.pointEncode));
      } else {
         this.config.readServer();
         this.config.handle(UiAnimationKeys.load(), 0.0F, this.providerFetch);
      }
   }

   private void collectPlayer() {
      float var1 = this.config.sampleLayer();
      float var2 = this.config.sendWorld();
      SurfaceHitResolver.Bounds var3 = this.bindIndex().main();
      float var4 = var3.localX(var1);
      float var5 = var3.localY(var2);

      for (ModulePlacement var7 : this.positionAdvance.process()) {
         ModuleCardTransform var8 = ModuleCardTransform.resolve(this.config, var7, this.eventAttach);
         float var9 = var8.pivotX() + (var4 - var8.pivotX()) / var8.scale();
         float var10 = var8.pivotY() + (var5 - var8.hitTranslateY() - var8.pivotY()) / var8.scale();
         boolean var11 = !this.config.filterMatrix()
            && var8.visible()
            && ModuleStateHelper.handle(var9, var10, var7.process(), var7.compute(), var7.resolve(), var7.update())
            && ModuleStateHelper.handle(var4, var5, this.cache.drawAnimation(), this.cache.encodePoint(), this.cache.animate(), this.cache.load());
         this.config.handle(UiAnimationKeys.process(var7.handle()), var11 ? 1.0F : 0.0F, this.pending);
      }
   }

   private SpringAnimationSpec handle(boolean var1, SpringAnimationSpec var2) {
      return !this.config.filterMatrix() && var1 ? var2 : this.providerFetch;
   }
   public ModernClickGuiState check() {
      return this.config;
   }
   public GuiLayoutSpec onTick() {
      return this.state;
   }
   public ViewportLayoutState select() {
      return this.cache;
   }
   public GuiLayoutEngine refresh() {
      return this.output;
   }
   public ModuleTooltipMeasurer render() {
      return this.current;
   }
   public StyledTextRenderer tick() {
      return this.active;
   }
   public CoreDiagnosticsPanel drawAnimation() {
      return this.mode;
   }
   public ThemeBrowserLayout encodePoint() {
      return this.selection;
   }
   public ModernClickGuiInputHandler animate() {
      return this.enabled;
   }
   public ThemePaletteRegistry load() {
      return this.renderer;
   }
   public FoundryWorkspace save() {
      return this.handler;
   }
   public SpringAnimationSpec submit() {
      return this.animationDraw;
   }
   public SpringAnimationSpec unload() {
      return this.pointEncode;
   }
   public SpringAnimationSpec fetch() {
      return this.animator;
   }
   public SpringAnimationSpec measure() {
      return this.source;
   }
   public SpringAnimationSpec blendMatrix() {
      return this.target;
   }
   public SpringAnimationSpec matchVector() {
      return this.pending;
   }
   public SpringAnimationSpec projectItem() {
      return this.previous;
   }
   public SpringAnimationSpec computeResponse() {
      return this.latest;
   }
   public SpringAnimationSpec fetchProvider() {
      return this.summary;
   }
   public SpringAnimationSpec drawProfile() {
      return this.matrixBlend;
   }
   public SpringAnimationSpec performVector() {
      return this.vectorMatch;
   }
   public SpringAnimationSpec attachEvent() {
      return this.itemProject;
   }
   public SpringAnimationSpec readServer() {
      return this.responseCompute;
   }
   public SpringAnimationSpec advancePosition() {
      return this.providerFetch;
   }
   public SpringAnimationSpec checkFrame() {
      return this.profileDraw;
   }
   public ModernClickGuiContentRenderer collectModule() {
      return this.vectorPerform;
   }
   public GuiMetrics closeProvider() {
      return this.eventAttach;
   }
   public ThemeRenderContext savePreset() {
      return this.serverRead;
   }
   public ModuleLayoutResult convertWindow() {
      return this.positionAdvance;
   }
   public boolean writePreset() {
      return this.frameCheck;
   }
   public ThemePalette measureColor() {
      return this.moduleCollect;
   }
   public long scheduleAnimation() {
      return this.providerClose;
   }
   public boolean scanRenderer() {
      return this.presetSave;
   }
   public SurfaceHitResolver.Mode buildSource() {
      return this.windowConvert;
   }
   public FoundryWorkspace collapseOutput() {
      return this.presetWrite;
   }
   public int invokeProfile() {
      return this.colorMeasure;
   }
}
