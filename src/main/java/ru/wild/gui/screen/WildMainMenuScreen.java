package ru.wild.gui.screen;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import ru.wild.WildClient;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.core.ProxiesScreen;
import ru.wild.core.ViaFabricPlusInstaller;
import ru.wild.gui.theme.MotionSpringPresets;
import ru.wild.gui.theme.SensitivityPresets;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.modules.visuals.Menu;
import ru.wild.profile.Profile;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.MainMenuBackgroundRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.util.math.DampedOscillator;
import ru.wild.util.render.RoundedRectRenderer;

public final class WildMainMenuScreen extends Screen implements FrameRenderListener {
   public static final String instance = "Src ready by SoftArax";
   public static final String data = "WILD";
   public static final String context = "w";
   public static final String config = "W";
   public static final float state = 0.83F;
   public static final float cache = 0.08F;
   public static final float output = 0.3F;
   public static final float current = 0.0188F;
   public static final float active = 0.0F;
   public static final float mode = 0.98F;
   public static final float selection = -0.62F;
   private static final float enabled = 0.047F;
   private static final float renderer = 0.7296875F;
   private static final ThemePaletteRegistry handler = ThemePaletteRegistry.handle();
   private static final int animationDraw = 14;
   private static final Identifier pointEncode = Identifier.ofVanilla("textures/gui/title/background/panorama");
   private static final SensitivityPresets[] animator = SensitivityPresets.values();
   static final int source = animator.length;
   static final String[] target = new String[]{"Low", "Balanced", "High", "Ultra"};
   private static final float[] pending = new float[]{0.55F, 0.72F, 0.86F, 1.0F};
   private static final float[] previous = new float[]{0.0F, 0.0F, 1.0F, 1.0F};
   private static final float[] latest = new float[]{0.0F, 0.0F, 1.0F, 1.0F};
   private static final String summary = "c";
   private static final float matrixBlend = 54.0F;
   private static final float vectorMatch = 14.0F;
   private static final float itemProject = 15.0F;
   private static final float responseCompute = 19.5F;
   private static final float providerFetch = 72.0F;
   private static final float profileDraw = 264.0F;
   private static final float vectorPerform = 424.0F;
   private static final float eventAttach = 0.226F;
   private static final float serverRead = 24.0F;
   private static final float positionAdvance = 2.0F;
   private static final float frameCheck = 3.2F;
   private static final float moduleCollect = 3.4F;
   private static final float providerClose = 0.0135F;
   private static final float presetSave = 2.2F;
   private static final float windowConvert = 0.052F;
   private static final float presetWrite = 0.62F;
   private static final float colorMeasure = 48.0F;
   private static final float animationSchedule = 0.32F;
   private static final float rendererScan = 0.32F;
   private static final float sourceBuild = 20.0F;
   private static final float outputCollapse = 4.0F;
   private static final float profileInvoke = 22.0F;
   private static final float sourceSchedule = 14.5F;
   private static final float timerRender = 40.0F;
   private static final float scaleSave = 40.0F;
   private static final float colorCompute = 18.0F;
   private static final float scaleAdapt = 13.0F;
   private static final float textureRun = 14.0F;
   private static final float indexBind = 0.32F;
   private static final float actionRead = 34.0F;
   private static final float configCollapse = 0.34F;
   private static final float dataValidate = 20.0F;
   private static final float scaleRender = 14.5F;
   private static final float clientRefresh = 40.0F;
   private static final float keyFilter = 0.42F;
   private static final float requestAdapt = 0.66F;
   private static final float timerMeasure = 150.0F;
   private static final float vectorEncode = 34.0F;
   private static final float requestReceive = 6.0F;
   private static final float windowProcess = 8.0F;
   private static final float packetSave = 12.0F;
   private static final float entryAnimate = 18.0F;
   private static final float playerCollect = 11.0F;
   private static final float stateApply = 4.0F;
   private static final int matrixFilter = 8;
   private static final float layerSample = 12.0F;
   private static final float worldSend = 340.0F;
   private static final float targetWrite = 520.0F;
   private static final float resultEncode = 52.0F;
   private static final float messageParse = 24.0F;
   private static final float providerRead = 1.0F;
   private static final float matrixBlend2 = 0.46F;
   private static final float scalePerform = 1.45F;
   static final String[] contextExpand = new String[]{
      "Half-scale nebula, glass blur and particles off",
      "Soft nebula, light glass blur, cursor trail",
      "Full nebula motion, particles, film grain",
      "Native-resolution nebula, every glass pass"
   };
   private static final float keyProcess = 13.0F;
   private static final float actionConvert = 20.5F;
   private static final float screenRead = 1.1F;
   private static final float animationExpand = 32.0F;
   private static final float playerRun = 0.47F;
   private static final float matrixRender = 92.0F;
   private static final float moduleTick = 1.12F;
   private static final float playerCollapse = 0.148F;
   private static final float optionAdvance = 102.0F;
   private static final float effectScan = 186.0F;
   private static final float optionParse = 4.2F;
   private static final float pointSubmit = 0.66F;
   private static final float listenerPerform = 2.6F;
   private static final float configMatch = 26.0F;
   private static final float actionRender = 0.18F;
   private static final float playerApply = 0.078F;
   private static final float bufferAdapt = 0.042F;
   private static final float playerUpdate = 0.06F;
   private static final float packetRead = 0.55F;
   private static final float rendererCancel = 150.0F;
   private static final float eventReceive = 14.0F;
   private static final float screenSubmit = 0.22F;
   private static final float cacheHandle = 0.62F;
   private static final float rangeRelease = 0.34F;
   private static final float indexSave = 0.88F;
   private static final float indexCheck = 0.55F;
   private static final float settingSchedule = 9.0F;
   private static final float inputAcquire = (float) (Math.PI * 2.0 / 3.0);
   private static final float listenerRun = 0.62F;
   private static final float indexLoad = 0.78F;
   private static final float layoutSave = 2.25F;
   private static final float blockRun = 0.35F;
   private static final float playerEvaluate = 0.42F;
   private static final float outputFetch = 128.0F;
   private static final float scaleParse = 0.6F;
   private static final float sessionEncode = 0.55F;
   private static final float elementTick = 9.0F;
   private static final float regionAlign = 4.35F;
   private static final float resourceClamp = 0.3719F;
   private static float handlerRun;
   private static float keyCheck = -1.0F;
   private final MainMenuBackgroundRenderer layerProject = new MainMenuBackgroundRenderer();
   private final RotatingCubeMapRenderer entityFilter = new RotatingCubeMapRenderer(new CubeMapRenderer(pointEncode));
   private final OpenGlStateSnapshot.NetworkState layerSample2 = new OpenGlStateSnapshot.NetworkState();
   private final OpenGlStateSnapshot.NetworkState sourceCancel = new OpenGlStateSnapshot.NetworkState();
   private boolean eventSend;
   private boolean providerOffset;
   private volatile boolean messageParse2;
   private final WildMainMenuScreen.FileEntry[] shaderProject = new WildMainMenuScreen.FileEntry[]{
      new WildMainMenuScreen.FileEntry("Singleplayer", WildMainMenuScreen.Mode.SINGLEPLAYER),
      new WildMainMenuScreen.FileEntry("Multiplayer", WildMainMenuScreen.Mode.MULTIPLAYER),
      new WildMainMenuScreen.FileEntry("Alt Manager", WildMainMenuScreen.Mode.ALT_MANAGER),
      new WildMainMenuScreen.FileEntry("Options", WildMainMenuScreen.Mode.OPTIONS),
      new WildMainMenuScreen.FileEntry("Quit", WildMainMenuScreen.Mode.QUIT)
   };
   private final WildMainMenuScreen.ScreenState[] inputInvoke = new WildMainMenuScreen.ScreenState[14];
   private final WildMainMenuScreen.PrimaryNetworkState optionFetch = new WildMainMenuScreen.PrimaryNetworkState(this.shaderProject.length, 14);
   private final WildMainMenuScreen.RuntimeNetworkState eventCollapse = new WildMainMenuScreen.RuntimeNetworkState();
   final WildMainMenuScreen.PrimaryFileEntry stateAttach = new WildMainMenuScreen.PrimaryFileEntry();
   private final DampedOscillator worldEvaluate = new DampedOscillator(MotionSpringPresets.context);
   private final DampedOscillator playerProject = new DampedOscillator(MotionSpringPresets.context);
   final DampedOscillator playerMatch = new DampedOscillator(MotionSpringPresets.instance);
   final DampedOscillator cacheClose = new DampedOscillator(MotionSpringPresets.instance);
   private final DampedOscillator scaleSetup = new DampedOscillator(MotionSpringPresets.data);
   private final DampedOscillator indexSynchronize = new DampedOscillator(MotionSpringPresets.data);
   private final DampedOscillator taskInterpolate = new DampedOscillator(MotionSpringPresets.source);
   private final DampedOscillator sourceRefresh = new DampedOscillator(MotionSpringPresets.animator);
   private final DampedOscillator playerSave = new DampedOscillator(MotionSpringPresets.animator);
   private final DampedOscillator requestRun = new DampedOscillator(MotionSpringPresets.target);
   private final DampedOscillator frameProject = new DampedOscillator(MotionSpringPresets.pending);
   private final DampedOscillator dataRelease = new DampedOscillator(MotionSpringPresets.pointEncode);
   private final DampedOscillator vectorRun = new DampedOscillator(MotionSpringPresets.summary);
   private final DampedOscillator providerSynchronize = new DampedOscillator(MotionSpringPresets.summary);
   private float pathProcess;
   private float positionReset;
   private long effectApply;
   private long cacheHandle2;
   private long sessionAdvance;
   private float keySample;
   private float playerPerform;
   float taskLoad;
   float pointSend;
   private float clientSubmit;
   private float pointSample;
   private boolean regionRefresh;
   private boolean pathCheck;
   private boolean contextParse;
   private boolean optionStop;
   private int mousePrepare = -6357021;
   private int sessionAdapt = -11341636;
   private ThemePalette profileMatch = ThemePalette.AURORA;
   private boolean entityAdvance;
   private float sessionCollect;
   private float inputAttach;
   private float pathProject;
   private float inputHandle;
   private float worldDispatch;
   private float optionAdvance2;
   private float clientDraw;
   private float providerMatch;
   private float profileWrite;
   private float effectMatch = -1.0F;
   private float cacheExecute;
   private float pointSynchronize;
   private float sessionBind;
   private float indexCancel;
   private float elementStop;
   private float itemAnimate;
   private float valueRead;
   private float configAlign;
   private String optionMeasure = "";
   private String stateFetch = "";
   private String itemAttach = "";
   private int keyCollapse = -1;
   private float timerFetch;
   private float taskFilter;
   private float indexAcquire;
   private float resourceStart;
   private float playerApply2;

   public WildMainMenuScreen() {
      super(Text.literal("Wild"));

      for (int var1 = 0; var1 < this.inputInvoke.length; var1++) {
         this.inputInvoke[var1] = new WildMainMenuScreen.ScreenState();
      }
   }

   protected void init() {
      super.init();
      this.effectApply = System.nanoTime();
      this.cacheHandle2 = this.effectApply;
      this.sessionAdvance = this.effectApply;
      this.keySample = 0.0F;
      this.playerPerform = 0.0F;
      this.worldEvaluate.handle(0.0F);
      this.playerProject.handle(0.0F);
      this.regionRefresh = false;
      this.pathCheck = false;
      this.contextParse = false;
      this.optionStop = false;
      this.frameProject.handle(0.0F);
      this.vectorRun.handle(0.0F);
      this.providerSynchronize.handle(0.0F);
      this.pathProcess = 0.0F;
      this.positionReset = 0.0F;
      this.dataRelease.handle(0.0F);
      this.pathProject = 0.0F;
      this.taskInterpolate.handle(execute());
      int var1 = this.apply();
      this.sourceRefresh.handle(pending[var1]);
      this.playerSave.handle(previous[var1]);
      this.requestRun.handle(latest[var1]);
      this.eventCollapse.handle(var1);
      this.stateAttach.handle();

      for (WildMainMenuScreen.FileEntry var5 : this.shaderProject) {
         var5.handle();
      }

      for (WildMainMenuScreen.ScreenState var9 : this.inputInvoke) {
         var9.config = 0.0F;
         var9.context = -100.0F;
      }
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      if (!this.messageParse2) {
         this.handle(var1);
      }

      this.handle(var2, var3, false);
   }

   private void handle(DrawContext var1) {
      if (var1 != null && this.client != null && this.width > 0 && this.height > 0) {
         if (this.providerOffset) {
            var1.fillGradient(0, 0, this.width, this.height, process(this.mousePrepare, 255), process(this.sessionAdapt, 255));
         } else {
            try {
               if (!this.eventSend) {
                  this.client.getTextureManager().registerTexture(pointEncode, new CubemapTexture(pointEncode));
                  this.eventSend = true;
               }

               this.entityFilter.render(var1, this.width, this.height, true);
               var1.fillGradient(0, 0, this.width, this.height, process(this.mousePrepare, 70), process(this.sessionAdapt, 110));
            } catch (Throwable var3) {
               this.providerOffset = true;
               ScreenRenderDiagnostics.handle("MainMenuPanorama", this, "panorama fallback failed", var3);
               var1.fillGradient(0, 0, this.width, this.height, process(this.mousePrepare, 255), process(this.sessionAdapt, 255));
            }
         }
      }
   }

   @Override
   public void handle(int var1, int var2, float var3) {
      this.handle(var1, var2, true);
   }
   private void handle(int var1, int var2, boolean var3) {
      Window var4 = this.client == null ? null : this.client.getWindow();
      if (var4 != null && !var4.hasZeroWidthOrHeight() && var4.getFramebufferWidth() > 0 && var4.getFramebufferHeight() > 0) {
         long var5 = MainMenuBackgroundRenderer.handle(MinecraftClient.getInstance(), var4.getFramebufferWidth(), var4.getFramebufferHeight());
         if (var5 < 0L) {
            if (var3) {
               this.messageParse2 = false;
            }
         } else {
            int var7 = MainMenuBackgroundRenderer.handle(var5);
            int var8 = MainMenuBackgroundRenderer.process(var5);
            long var9 = System.nanoTime();
            float var11 = Math.max(0.001F, Math.min(0.05F, (float)(var9 - this.cacheHandle2) / 1.0E9F));
            this.cacheHandle2 = var9;
            this.keySample = (float)(var9 - this.effectApply) / 1.0E9F;
            this.elementStop = LocalTime.now().toSecondOfDay() / 3600.0F;
            this.process();
            this.handle(var4, var1, var2, var9);
            this.process(var7, var8, var11);
            this.compute();
            this.resolve(var11);
            boolean var12 = Menu.handle(Menu.outputCollapse);
            float var13 = var12 ? (this.playerMatch.handle() / Math.max(1.0F, var7) - 0.5F) * 2.0F : 0.0F;
            float var14 = var12 ? (this.cacheClose.handle() / Math.max(1.0F, var8) - 0.5F) * 2.0F : 0.0F;
            this.sessionCollect = this.worldEvaluate.handle(var13, var11);
            this.inputAttach = this.playerProject.handle(var14, var11);
            this.handle(var7, var8, this.sessionCollect, this.inputAttach, var11);
            if (var3) {
               int var15 = GL11.glGetInteger(36006);
               GlCompatibilityProbe.handle(var15);
               this.handle(var7, var8, var15, this.sessionCollect, this.inputAttach, var9);
               OpenGlStateSnapshot.process(this.layerSample2);

               boolean var16;
               try {
                  var16 = this.layerProject.handle(this.optionFetch);
               } finally {
                  OpenGlStateSnapshot.compute(this.layerSample2);
               }

               this.messageParse2 = var16;
               this.resolve();
               if (this.optionFetch.compute() > 0) {
                  OpenGlStateSnapshot.process(this.layerSample2);
                  boolean var21 = false /* VF: Semaphore variable */;

                  try {
                     var21 = true;
                     this.layerProject.process(this.optionFetch);
                     var21 = false;
                  } finally {
                     if (var21) {
                        OpenGlStateSnapshot.compute(this.layerSample2);
                     }
                  }

                  OpenGlStateSnapshot.compute(this.layerSample2);
                  this.update();
               }
            }
         }
      } else {
         if (var3) {
            this.messageParse2 = false;
         }
      }
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 && this.client != null && this.client.getWindow() != null) {
         float var6 = handle(this.client.getWindow(), var1);
         float var7 = process(this.client.getWindow(), var3);
         if (this.stateAttach.resolve(var6, var7)) {
            return true;
         }

         if (this.eventCollapse.handle(var6, var7)) {
            this.eventCollapse.target = true;
            this.process(this.eventCollapse.handle(var6));
            return true;
         }

         for (WildMainMenuScreen.FileEntry var11 : this.shaderProject) {
            if (var11.handle(var6, var7)) {
               var11.config.handle(1.0F);
               var11.state.handle(1.0F);
               var11.selection = 0.0F;
               this.handle(var11.data);
               return true;
            }
         }

         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (var5 == 0 && this.eventCollapse.target && this.client != null && this.client.getWindow() != null) {
         this.process(this.eventCollapse.handle(handle(this.client.getWindow(), var1)));
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0 && this.eventCollapse.target) {
         this.eventCollapse.target = false;
         prepare();
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.client != null && this.client.getWindow() != null && var7 != 0.0) {
         float var9 = handle(this.client.getWindow(), var1);
         float var10 = process(this.client.getWindow(), var3);
         if (this.stateAttach.handle(var9, var10, var7)) {
            return true;
         }

         if (this.eventCollapse.handle(var9, var10)) {
            int var11 = resolve(this.apply() + (var7 > 0.0 ? 1 : -1));
            if (this.process(var11)) {
               prepare();
            }

            return true;
         }
      }

      return super.mouseScrolled(var1, var3, var5, var7);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.stateAttach.handle(var1) ? true : super.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.stateAttach.handle(var1) ? true : super.charTyped(var1, var2);
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void removed() {
      this.layerProject.close();
      super.removed();
   }

   public void handle(int var1, int var2) {
      try {
         this.layerProject.handle(var1, var2);
      } catch (Throwable var4) {
      }
   }

   private boolean process(int var1) {
      int var2 = resolve(var1);
      if (var2 == this.apply()) {
         return false;
      }

      Menu.handle(var2);
      this.eventCollapse.context.handle(1.0F);
      return true;
   }

   private void process() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.AURORA;
      this.profileMatch = var1;
      this.entityAdvance = handler.compute(var1);
      this.mousePrepare = handler.resolve(var1);
      this.sessionAdapt = handler.update(var1);
   }

   private void handle(Window var1, int var2, int var3, long var4) {
      float var6 = handle(var1, var2);
      float var7 = process(var1, var3);
      if (!this.regionRefresh) {
         this.regionRefresh = true;
         this.taskLoad = var6;
         this.pointSend = var7;
      } else {
         float var8 = compute(var6 - this.taskLoad, var7 - this.pointSend);
         this.taskLoad = var6;
         this.pointSend = var7;
         if (var8 > 1.5F) {
            this.sessionAdvance = var4;
         }

         this.optionStop = this.taskLoad > -1.0F
            && this.pointSend > -1.0F
            && this.taskLoad < var1.getFramebufferWidth() + 1.0F
            && this.pointSend < var1.getFramebufferHeight() + 1.0F;
      }
   }

   private void process(int var1, int var2, float var3) {
      if (!this.pathCheck) {
         this.playerMatch.handle(this.taskLoad);
         this.cacheClose.handle(this.pointSend);
         this.scaleSetup.handle(0.0F);
         this.indexSynchronize.handle(0.0F);
         this.pathCheck = true;
      } else {
         this.playerMatch.handle(this.taskLoad, var3);
         this.cacheClose.handle(this.pointSend, var3);
         float var4 = process(this.playerMatch.process() / Math.max(1.0F, var1), -1.8F, 1.8F);
         float var5 = process(this.cacheClose.process() / Math.max(1.0F, var2), -1.8F, 1.8F);
         this.scaleSetup.handle(var4, var3);
         this.indexSynchronize.handle(var5, var3);
         this.frameProject.handle(this.optionStop ? 1.0F : 0.0F, var3);
      }
   }

   private void resolve(float var1) {
      int var2 = this.apply();
      this.sourceRefresh.handle(pending[var2], var1);
      this.playerSave.handle(previous[var2], var1);
      boolean var3 = MenuAnimationClock.compute();
      this.requestRun.handle(var3 ? latest[var2] : 0.0F, var1);
      this.taskInterpolate.handle(execute(), var1);
      this.playerPerform = this.playerPerform + var1 * this.taskInterpolate.handle();
      this.positionReset = process(this.providerSynchronize.handle(this.keySample >= 0.06F ? 1.0F : 0.0F, var1), 0.0F, 1.0F);
      this.pathProcess = process(this.vectorRun.handle(this.keySample >= 0.55F ? 1.0F : 0.0F, var1), 0.0F, 1.0F);
   }

   private void compute() {
      if (MenuAnimationClock.compute() && Menu.handle(Menu.sourceBuild)) {
         float var1 = this.playerMatch.handle();
         float var2 = this.cacheClose.handle();
         if (!this.contextParse) {
            this.clientSubmit = var1;
            this.pointSample = var2;
            this.contextParse = true;
            this.handle(var1, var2, 0.36F);
         } else {
            float var3 = compute(var1 - this.clientSubmit, var2 - this.pointSample);
            if (var3 > 5.5F) {
               this.handle(var1, var2, process(var3 / 180.0F, 0.12F, 0.54F));
               this.clientSubmit = var1;
               this.pointSample = var2;
            }
         }
      }
   }

   private void handle(float var1, float var2, float var3) {
      int var4 = 0;
      float var5 = -1.0F;

      for (int var6 = 0; var6 < this.inputInvoke.length; var6++) {
         float var7 = this.keySample - this.inputInvoke[var6].context;
         if (this.inputInvoke[var6].config <= 0.0F) {
            var4 = var6;
            break;
         }

         if (var7 > var5) {
            var5 = var7;
            var4 = var6;
         }
      }

      this.inputInvoke[var4].instance = var1;
      this.inputInvoke[var4].data = var2;
      this.inputInvoke[var4].context = this.keySample;
      this.inputInvoke[var4].config = var3;
   }

   private void handle(int var1, int var2, float var3, float var4, float var5) {
      float var6 = process((float)var1, (float)var2);
      float var7 = process(var1 * 0.226F, 264.0F * var6, 424.0F * var6);
      float var8 = 54.0F * var6;
      float var9 = 14.0F * var6;
      float var10 = this.shaderProject.length * var8 + (this.shaderProject.length - 1) * var9;
      float var11 = var1 * 0.5F + var3 * 2.1F * var6;
      float var12 = handle(var2) * 1.12F;
      float var13 = 92.0F * var6;
      float var14 = var12 + var13 + var10;
      float var15 = var2 * 0.47F - var14 * 0.5F;
      float var16 = var15 + var12 * 0.5F;
      float var17 = var15 + var12 + var13 + var4 * 1.25F * var6;
      float var18 = Math.min(15.0F * var6, var8 * 0.5F);
      float var19 = 24.0F * var6;
      float var20 = compute(this.scaleSetup.handle(), this.indexSynchronize.handle());

      for (int var21 = 0; var21 < this.shaderProject.length; var21++) {
         WildMainMenuScreen.FileEntry var22 = this.shaderProject[var21];
         var22.matrixBlend = var7;
         var22.vectorMatch = var8;
         var22.pending = var11 - var7 * 0.5F;
         var22.previous = var17 + var21 * (var8 + var9);
         var22.itemProject = var18;
         boolean var23 = this.keySample >= 0.18F + var21 * 0.078F;
         float var24 = var22.animator.handle(var23 ? 1.0F : 0.0F, var5);
         var22.moduleCollect = process(var24, 0.0F, 1.0F);
         var22.providerClose = (1.0F - var24) * 26.0F * var6;
         float var25 = handle(
            this.taskLoad, this.pointSend, var22.pending, var22.previous + var22.providerClose, var22.matrixBlend, var22.vectorMatch, var22.itemProject
         );
         boolean var26 = var25 <= 0.0F;
         float var27 = 1.0F - onTick(process(Math.max(0.0F, var25) / Math.max(1.0F, var19), 0.0F, 1.0F));
         var22.current = var22.current ? var25 <= 2.0F * var6 : var26;
         float var28 = var22.current ? 1.0F : 0.0F;
         var22.responseCompute = process(var22.pointEncode.handle(var28, var5), 0.0F, 1.0F);
         var22.mode = var22.output.handle(var28, var5);
         if (var22.current && !var22.active) {
            var22.selection = 0.0F;
         }

         var22.active = var22.current;
         var22.selection = var22.current ? Math.min(1.0F, var22.selection + var5 / 0.62F) : 1.0F;
         var22.providerFetch = var22.context.handle(var27, var5);
         var22.profileDraw = var22.config.handle(0.0F, var5);
         var22.vectorPerform = var22.state.handle(0.0F, var5);
         float var29 = process((this.playerMatch.handle() - var22.pending) / Math.max(1.0F, var22.matrixBlend), 0.0F, 1.0F);
         float var30 = process((this.cacheClose.handle() - var22.previous) / Math.max(1.0F, var22.vectorMatch), 0.0F, 1.0F);
         var22.serverRead = var22.enabled.handle(var29, var5);
         var22.positionAdvance = var22.renderer.handle(var30, var5);
         float var31 = process((this.playerMatch.handle() - (var22.pending + var22.matrixBlend * 0.5F)) / Math.max(1.0F, var22.matrixBlend), -3.2F, 3.2F);
         float var32 = process(
            (this.cacheClose.handle() - (var22.previous + var22.providerClose + var22.vectorMatch * 0.5F)) / Math.max(1.0F, var22.vectorMatch), -3.2F, 3.2F
         );
         var22.source = var22.handler.handle(var31, var5);
         var22.target = var22.animationDraw.handle(var32, var5);
         float var33 = 1.0F - (1.0F - Math.min(var24, 1.0F)) * 0.042F;
         float var34 = var33 + var22.providerFetch * 0.0135F - var22.profileDraw * 0.052F;
         var22.eventAttach = var22.cache.handle(var34, var5);
         float var35 = (var22.serverRead - 0.5F) * 4.0F * var6 * var22.providerFetch;
         float var36 = (var22.positionAdvance - 0.5F) * 2.2F * var6 * var22.providerFetch - var22.mode * 3.4F * var6 + var22.profileDraw * 2.2F * var6;
         var22.latest = var22.pending + var35;
         var22.summary = var22.previous + var36;
         var22.frameCheck = process(var20 * 0.7F * var22.providerFetch + Math.abs(var22.cache.process()) * 0.02F, 0.0F, 1.0F);
      }

      this.handle(var1, var2, var6, var3, var4, var16);
      this.itemAnimate = var1 * 0.5F;
      this.valueRead = 65.6F * var6;
      this.configAlign = handle(20.5F, var6);
      this.apply(var6);
      this.pathProject = this.dataRelease.handle(this.update(var6), var5);
      float var37 = process(this.scaleSetup.handle() * 0.62F, -1.0F, 1.0F);
      this.clientDraw = var37 * this.providerMatch * 0.34F;
      this.eventCollapse.handle(this, var1, var2, var6, var5);
      this.stateAttach.handle(this, var1, var2, var6, var5);
   }

   private void handle(int var1, int var2, float var3, float var4, float var5, float var6) {
      float var7 = handle(var2);
      float var8 = process(Math.min(var1, var2) * 0.148F, 102.0F * var3, 186.0F * var3);
      float var9 = var8 * 4.2F;
      float var10 = var1 * 0.5F + var4 * 1.65F * var3;
      float var11 = Math.max(var8 * 0.34F + 32.0F * var3, var6) + var5 * 0.95F * var3 + var7 * 0.0F;
      this.pointSynchronize = var10 - var9 * 0.5F;
      this.sessionBind = var11 - var9 * 0.5F;
      this.indexCancel = var9;
      float var12 = var11 - var8 * 0.5F;
      float var13 = compute(var7) * 0.5F;
      if (var7 != this.effectMatch) {
         SdfTextRenderer var14 = RoundedRectRenderer.handle(FontRegistry.instance);
         this.cacheExecute = var14 == null ? var7 * 3.4F : var14.handle("Src ready by SoftArax", var13);
         this.effectMatch = var7;
      }

      float var16 = this.cacheExecute;
      float var15 = var12 + var8 * 0.5F + process(var7) * 0.5F + var7 * 0.98F;
      this.inputHandle = var10 + var4 * 2.15F * var3 * -0.62F;
      this.optionAdvance2 = var15;
      this.worldDispatch = var15 - var13 * 0.26F;
      this.providerMatch = var16 * 0.5F;
      this.profileWrite = Math.max(var13 * 0.9F, 13.0F * var3);
   }

   private float update(float var1) {
      if (this.optionStop && !(this.providerMatch <= 0.0F)) {
         float var2 = Math.abs(this.taskLoad - this.inputHandle) - this.providerMatch;
         float var3 = Math.abs(this.pointSend - this.worldDispatch) - this.profileWrite;
         float var4 = compute(Math.max(var2, 0.0F), Math.max(var3, 0.0F)) + Math.min(Math.max(var2, var3), 0.0F);
         if (var4 <= 0.0F) {
            return 1.0F;
         }

         float var5 = Math.max(1.0F, 150.0F * var1);
         return 1.0F - onTick(process(var4 / var5, 0.0F, 1.0F));
      } else {
         return 0.0F;
      }
   }

   private void handle(int var1, int var2, int var3, float var4, float var5, long var6) {
      float var8 = Math.max(0.0F, (float)(var6 - this.sessionAdvance) / 1.0E9F);
      float var9 = process(compute(this.scaleSetup.handle(), this.indexSynchronize.handle()), 0.0F, 3.0F);
      float var10 = Math.max((float)Math.exp(-var8 * 1.45F), process(var9 * 0.3F, 0.0F, 1.0F));
      float var11 = process((float)var1, (float)var2);
      float var12 = this.eventCollapse.context.handle();
      WildMainMenuScreen.PrimaryNetworkState var13 = this.optionFetch;
      var13.handle(var1, var2, var3, this.keySample, this.playerPerform);
      var13.check(this.shaderProject.length);

      for (int var14 = 0; var14 < this.shaderProject.length; var14++) {
         WildMainMenuScreen.FileEntry var15 = this.shaderProject[var14];
         var12 = Math.max(var12, var15.vectorPerform);
         var13.handle(var14)
            .handle(
               var15.instance,
               var15.latest,
               var15.summary + var15.providerClose,
               var15.matrixBlend,
               var15.vectorMatch,
               var15.itemProject,
               var15.responseCompute,
               var15.providerFetch,
               var15.profileDraw,
               var15.moduleCollect,
               var15.vectorPerform,
               72.0F * var11,
               var15.eventAttach,
               var15.serverRead,
               var15.positionAdvance,
               var15.frameCheck
            );
         var13.handle(var14).handle(var15.source, var15.target);
         var13.handle(var14).handle(var15.selection);
      }

      for (int var17 = 0; var17 < 14; var17++) {
         WildMainMenuScreen.ScreenState var19 = this.inputInvoke[var17];
         float var16 = Math.max(0.0F, this.keySample - var19.context);
         var13.onTick(var17).handle(var19.instance / Math.max(1.0F, var1), var19.data / Math.max(1.0F, var2), var16, var16 > 3.1F ? 0.0F : var19.config);
      }

      var13.prepare()
         .handle(
            this.pointSynchronize,
            this.sessionBind,
            this.indexCancel,
            this.indexCancel,
            0.5F - 0.5F * (float)Math.cos(this.keySample * (float) (Math.PI * 2.0 / 3.0))
         );
      this.eventCollapse.handle(var13.mode, this.pathProcess);
      float var18 = onTick(this.positionReset);
      if (this.indexAcquire > 0.5F && var18 > 0.004F) {
         var13.resolve().handle(this.timerFetch, this.taskFilter, this.indexAcquire, this.indexAcquire * 4.35F, this.playerApply2, var18, var18, 0.3719F);
         var13.resolve().handle(this.playerMatch.handle() - this.timerFetch, this.cacheClose.handle() - this.taskFilter);
      } else {
         var13.resolve().handle();
      }

      var13.resolve(6);
      var13.execute(4);
      int var20 = this.stateAttach.handle(var13, 0, this.pathProcess, var1, var2);
      var13.prepare(this.stateAttach.handle(var13, this.pathProcess, var1, var2));
      if (this.eventCollapse.enabled > 0.004F) {
         WildMainMenuScreen.FallbackNetworkState var21 = var13.compute(var20++);
         var21.handle(
            this.eventCollapse.renderer,
            this.eventCollapse.handler,
            this.eventCollapse.animationDraw,
            this.eventCollapse.pointEncode,
            Math.min(this.eventCollapse.pointEncode * 0.32F, 20.0F * var11),
            34.0F * var11,
            this.pathProcess * this.eventCollapse.enabled,
            0.0F,
            0.0F,
            1.0F,
            1.0F
         );
         var21.handle(0.0F, -this.eventCollapse.pointEncode * 2.4F);
         var21.process(0.66F);
      }

      var13.update(var20);
      var13.handle(
         this.playerMatch.handle(), this.cacheClose.handle(), this.scaleSetup.handle(), this.indexSynchronize.handle(), var9, this.frameProject.handle()
      );
      var13.handle(this.mousePrepare, this.sessionAdapt);
      var13.process(-var4 * 0.0014F, -var5 * 0.0011F, var4 * 1.75F * var11, var5 * 1.35F * var11, var4 * 2.15F * var11, var5 * 1.72F * var11);
      var13.compute(var10, this.sourceRefresh.handle(), this.playerSave.handle(), this.requestRun.handle(), this.positionReset, var12);
      var13.handle(this.pathProject, this.clientDraw);
      var13.handle(
         this.profileMatch == ThemePalette.SAKURA_BREEZE,
         this.profileMatch == ThemePalette.VERNAL_SOLSTICE,
         this.profileMatch == ThemePalette.MIDNIGHT_AZURE,
         this.entityAdvance
      );
      var13.handle(this.elementStop);
   }
   private void resolve() {
      try {
         WildClient.check();
         RoundedRectRenderer var1 = WildClient.handle();
         if (var1 == null) {
            return;
         }

         OpenGlStateSnapshot.process(this.sourceCancel);
         boolean var2 = SdfTextRenderer.process(true);
         boolean var14 = false /* VF: Semaphore variable */;

         try {
            var14 = true;
            var1.handle(this.optionFetch.enabled, this.optionFetch.renderer);
            float var3 = process((float)this.optionFetch.enabled, (float)this.optionFetch.renderer);
            int var4 = this.entityAdvance
               ? (this.optionFetch.sourceSchedule ? handle(0.0196F, 0.0667F, 0.0196F, 1.0F) : handle(0.1F, 0.1F, 0.1F, 1.0F))
               : handle(1.0F, 1.0F, 1.0F, 0.92F);
            this.process(var1, 1.0F);

            for (int var5 = 0; var5 < this.optionFetch.handle(); var5++) {
               WildMainMenuScreen.PrimaryColorState var6 = this.optionFetch.handle(var5);
               float var7 = onTick(var6.mode);
               float var8 = handle(19.5F, var3) * var6.renderer;
               float var9 = var6.data + var6.config * 0.5F;
               float var10 = var6.context + var6.state * 0.5F + var8 * 0.17F;
               var1.handle(FontRegistry.instance, Math.round(var9), Math.round(var10), var8, var6.instance, handle(var4, var7), "c");
            }

            this.handle(var1, 1.0F);
            this.eventCollapse.handle(var1, this.optionFetch.mode, var3, this.entityAdvance, this.mousePrepare, this.sessionAdapt, 1.0F);
            float var17 = this.optionFetch.mode.check();
            this.stateAttach.timerMeasure = this.check(0.0F);
            this.stateAttach.handle(var1, var17, this.entityAdvance);
            if (this.eventCollapse.enabled > 0.004F) {
               float var18 = var17 * this.eventCollapse.enabled;
               int var19 = this.entityAdvance ? handle(0.14F, 0.13F, 0.18F, var18) : handle(0.9F, 0.92F, 0.99F, var18);
               var1.handle(
                  FontRegistry.instance,
                  Math.round(this.eventCollapse.renderer + this.eventCollapse.animationDraw * 0.5F),
                  Math.round(this.eventCollapse.handler + this.eventCollapse.pointEncode * 0.5F + this.eventCollapse.animator * 0.17F),
                  this.eventCollapse.animator,
                  contextExpand[this.eventCollapse.source],
                  var19,
                  "c"
               );
            }

            var1.process();
            var14 = false;
         } finally {
            if (var14) {
               SdfTextRenderer.process(var2);
               OpenGlStateSnapshot.compute(this.sourceCancel);
            }
         }

         SdfTextRenderer.process(var2);
         OpenGlStateSnapshot.compute(this.sourceCancel);
      } catch (Throwable var16) {
      }
   }
   private void update() {
      try {
         WildClient.check();
         RoundedRectRenderer var1 = WildClient.handle();
         if (var1 == null) {
            return;
         }

         OpenGlStateSnapshot.process(this.sourceCancel);
         boolean var2 = SdfTextRenderer.process(true);
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            var1.handle(this.optionFetch.enabled, this.optionFetch.renderer);
            this.stateAttach.timerMeasure = this.check(0.0F);
            this.stateAttach.process(var1, this.optionFetch.mode.check(), this.entityAdvance);
            var1.process();
            var6 = false;
         } finally {
            if (var6) {
               SdfTextRenderer.process(var2);
               OpenGlStateSnapshot.compute(this.sourceCancel);
            }
         }

         SdfTextRenderer.process(var2);
         OpenGlStateSnapshot.compute(this.sourceCancel);
      } catch (Throwable var8) {
      }
   }
   private void handle(RoundedRectRenderer var1, float var2) {
      float var3 = this.optionFetch.scheduleAnimation() * var2;
      if (!(var3 <= 0.004F) && !(this.indexCancel <= 0.0F)) {
         SdfTextRenderer var4 = RoundedRectRenderer.handle(FontRegistry.instance);
         if (var4 != null) {
            float var5 = handle(this.optionFetch.renderer);
            float var6 = compute(var5);
            float var7 = var6 * 0.5F;
            float var8 = var4.handle("Src ready by SoftArax", var7);
            float var9 = Math.round(this.inputHandle - var8 * 0.5F);
            float var10 = Math.round(this.optionAdvance2);
            float var11 = this.taskLoad + this.clientDraw;
            float var12 = Math.max(var8 * 0.22F, 8.0F);
            float var13 = onTick(this.optionFetch.writePreset());
            boolean var14 = SdfTextRenderer.process(false);
            boolean var25 = false /* VF: Semaphore variable */;

            try {
               var25 = true;

               for (int var15 = 0; var15 < "Src ready by SoftArax".length(); var15++) {
                  char var16 = "Src ready by SoftArax".charAt(var15);
                  if (var16 != ' ') {
                     float var17 = var4.handle("Src ready by SoftArax".substring(0, var15), var7);
                     float var18 = var9 + var17 + var4.handle(String.valueOf(var16), var7) * 0.5F;
                     float var19 = (float)Math.exp(-prepare((var18 - var11) / var12));
                     float var20 = var19 * var3 * var13;
                     if (!(var20 <= 0.006F)) {
                        int var21 = this.check(
                           "Src ready by SoftArax".length() > 1
                              ? (float)var15 / ("Src ready by SoftArax".length() - 1)
                              : 0.0F
                        );
                        int var22 = compute(var21, compute(var21, -1, 0.55F), process(var19 * 1.15F, 0.0F, 1.0F));
                        var1.handle(FontRegistry.instance, var9 + var17, var10, var6, String.valueOf(var16), handle(var22, var20), "l");
                     }
                  }
               }

               var25 = false;
            } finally {
               if (var25) {
                  SdfTextRenderer.process(var14);
               }
            }

            SdfTextRenderer.process(var14);
         }
      }
   }

   private void apply(float var1) {
      if (this.configAlign <= 0.0F) {
         this.indexAcquire = 0.0F;
      } else {
         LocalTime var2 = LocalTime.now();
         int var3 = var2.getHour() * 60 + var2.getMinute();
         String var4 = Profile.getUsername();
         if (var4 == null || var4.isBlank()) {
            var4 = this.client != null && this.client.getSession() != null ? this.client.getSession().getUsername() : null;
         }

         String var5 = var4 == null ? "" : var4.trim();
         if (var3 != this.keyCollapse || !var5.equals(this.stateFetch)) {
            this.keyCollapse = var3;
            this.stateFetch = var5;
            this.optionMeasure = compute(var2.getHour()) + (this.stateFetch.isEmpty() ? "!" : ", ");
            this.itemAttach = this.stateFetch.isEmpty() ? "" : "!";
         }

         this.playerApply2 = execute(var2.toSecondOfDay() / 3600.0F);
         float var6 = this.configAlign * 0.5F;
         float var7 = onTick(this.positionReset);
         float var8 = this.valueRead + (1.0F - var7) * 9.0F * var1;
         SdfTextRenderer var9 = RoundedRectRenderer.handle(FontRegistry.data);
         boolean var10 = !this.stateFetch.isEmpty() && handle(this.stateFetch);
         FontObject var11 = var10 ? FontRegistry.config : FontRegistry.data;
         SdfTextRenderer var12 = RoundedRectRenderer.handle(var11);
         float var13 = var9 == null ? var6 * this.optionMeasure.length() * 0.52F : var9.handle(this.optionMeasure, var6);
         float var14 = var12 != null && !this.stateFetch.isEmpty() ? var12.handle(this.stateFetch, var6) : 0.0F;
         float var15 = var9 != null && !this.itemAttach.isEmpty() ? var9.handle(this.itemAttach, var6) : 0.0F;
         float var16 = var6 * 0.62F;
         float var17 = var6 * 0.78F;
         float var18 = var16 * 2.0F + var17 + var13 + var14 + var15;
         float var19 = this.itemAnimate - var18 * 0.5F;
         this.indexAcquire = var16;
         this.timerFetch = var19 + var16;
         this.taskFilter = var8 - var6 * 0.36F;
         this.resourceStart = var19 + var16 * 2.0F + var17;
      }
   }

   private static float execute(float var0) {
      float var1 = var0 % 24.0F;
      if (var1 >= 8.0F && var1 < 17.0F) {
         return 1.0F;
      } else if (var1 >= 21.0F || var1 < 4.0F) {
         return 0.0F;
      } else {
         return var1 >= 4.0F && var1 < 8.0F ? onTick(process((var1 - 4.0F) / 4.0F, 0.0F, 1.0F)) : onTick(process((21.0F - var1) / 4.0F, 0.0F, 1.0F));
      }
   }

   private void process(RoundedRectRenderer var1, float var2) {
      float var3 = onTick(this.optionFetch.writePreset());
      float var4 = var3 * var2;
      if (!(var4 <= 0.004F) && !(this.configAlign <= 0.0F)) {
         float var5 = process((float)this.optionFetch.enabled, (float)this.optionFetch.renderer);
         float var6 = this.valueRead + (1.0F - var3) * 9.0F * var5;
         boolean var7 = !this.stateFetch.isEmpty() && handle(this.stateFetch);
         FontObject var8 = var7 ? FontRegistry.config : FontRegistry.data;
         SdfTextRenderer var9 = RoundedRectRenderer.handle(FontRegistry.data);
         SdfTextRenderer var10 = RoundedRectRenderer.handle(var8);
         float var11 = this.configAlign * 0.5F;
         float var12 = var9 == null ? var11 * this.optionMeasure.length() * 0.52F : var9.handle(this.optionMeasure, var11);
         float var13 = var10 != null && !this.stateFetch.isEmpty() ? var10.handle(this.stateFetch, var11) : 0.0F;
         int var14 = this.entityAdvance ? handle(0.16F, 0.16F, 0.21F, 1.0F) : handle(0.88F, 0.9F, 0.98F, 1.0F);
         float var15 = var4 * 0.82F;
         float var16 = this.resourceStart;
         var1.handle(FontRegistry.data, Math.round(var16), Math.round(var6), this.configAlign, this.optionMeasure, handle(var14, var15), "l");
         var16 += var12;
         if (!this.stateFetch.isEmpty()) {
            int var17 = this.entityAdvance ? compute(this.check(0.3F), -15066590, 0.42F) : this.check(0.3F);
            var1.handle(var8, Math.round(var16), Math.round(var6), this.configAlign, this.stateFetch, handle(var17, var4), "l");
            var16 += var13;
            var1.handle(FontRegistry.data, Math.round(var16), Math.round(var6), this.configAlign, this.itemAttach, handle(var14, var15), "l");
         }
      }
   }

   private static String compute(int var0) {
      if (var0 >= 6 && var0 < 12) {
         return "Доброе утро";
      } else if (var0 >= 12 && var0 < 18) {
         return "Добрый день";
      } else {
         return var0 >= 18 && var0 < 22 ? "Добрый вечер" : "Доброй ночи";
      }
   }

   private static float prepare(float var0) {
      return var0 * var0;
   }

   private int check(float var1) {
      float var2 = process(var1, 0.0F, 1.0F);
      float var3 = update(this.mousePrepare) + (update(this.sessionAdapt) - update(this.mousePrepare)) * var2;
      float var4 = apply(this.mousePrepare) + (apply(this.sessionAdapt) - apply(this.mousePrepare)) * var2;
      float var5 = execute(this.mousePrepare) + (execute(this.sessionAdapt) - execute(this.mousePrepare)) * var2;
      float var6 = Math.max(var3, Math.max(var4, var5));
      float var7 = var6 > 1.0E-4F ? Math.max(1.0F, 0.88F / var6) : 1.0F;
      return handle(var3 * var7, var4 * var7, var5 * var7, 1.0F);
   }

   private static int compute(int var0, int var1, float var2) {
      float var3 = process(var2, 0.0F, 1.0F);
      int var4 = Math.round((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var3);
      int var5 = Math.round((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var3);
      int var6 = Math.round((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var3);
      return 0xFF000000 | var4 << 16 | var5 << 8 | var6;
   }

   int apply() {
      return resolve(Math.round(Menu.matrixBlend.config));
   }

   static int resolve(int var0) {
      return Math.max(0, Math.min(source - 1, var0));
   }

   private static float execute() {
      return process(Menu.timerMeasure.compute(), 0.0F, 1.5F);
   }

   private static void prepare() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   public static float handle(int var0) {
      return Math.max(22.0F, Math.round(var0 * 0.047F));
   }

   public static float handle(float var0) {
      return var0 / 0.375F;
   }

   public static float handle(SdfTextRenderer var0, float var1) {
      if (var0 == null) {
         return 0.0F;
      }

      int var2 = Math.max(0, "WILD".length() - 1);
      return var0.handle("WILD", var1) + 0.08F * var1 * var2;
   }

   static float handle(float var0, float var1) {
      return Math.max(var0 * var1, 13.0F) * 2.0F;
   }

   private static boolean handle(String var0) {
      for (int var1 = 0; var1 < var0.length(); var1++) {
         if (var0.charAt(var1) > '~') {
            return false;
         }
      }

      return true;
   }

   public static float process(float var0) {
      if (var0 == keyCheck && handlerRun > 0.0F) {
         return handlerRun;
      }

      float var1 = handle(var0) * 0.5F;
      SdfTextRenderer var2 = RoundedRectRenderer.handle(FontRegistry.config);
      float var3 = var2 == null ? 0.0F : var2.process("W", var1);
      if (!(var3 > 0.0F)) {
         return 0.7296875F * var1;
      }

      handlerRun = var3;
      keyCheck = var0;
      return var3;
   }

   public static float compute(float var0) {
      return Math.max(28.0F, var0 * 0.46F);
   }

   private void handle(WildMainMenuScreen.Mode var1) {
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var2 != null) {
         WildMainMenuScreen var3 = this;
         switch (var1) {
            case SINGLEPLAYER:
               var2.execute(() -> var2.setScreen(new SelectWorldScreen(var3)));
               break;
            case MULTIPLAYER:
               var2.execute(() -> var2.setScreen(new ProxiesScreen(var3)));
               break;
            case ALT_MANAGER:
               var2.execute(() -> var2.setScreen(new AltVaultScreen(var3)));
               break;
            case OPTIONS:
               var2.execute(() -> var2.setScreen(new OptionsScreen(var3, var2.options)));
               break;
            case QUIT:
               var2.execute(var2::scheduleStop);
         }
      }
   }

   private static float handle(Window var0, double var1) {
      return (float)(var1 * var0.getFramebufferWidth() / Math.max(1.0, var0.getScaledWidth()));
   }

   private static float process(Window var0, double var1) {
      return (float)(var1 * var0.getFramebufferHeight() / Math.max(1.0, var0.getScaledHeight()));
   }

   private static float process(float var0, float var1) {
      float var2 = process(Menu.responseCompute.compute() / 0.86F, 0.72F, 1.46F);
      return process(Math.min(var0 / 1920.0F, var1 / 1080.0F) * 1.16F * var2, 0.66F, 2.6F);
   }

   static float handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var2 + var4 * 0.5F;
      float var8 = var3 + var5 * 0.5F;
      float var9 = var4 * 0.5F - var6;
      float var10 = var5 * 0.5F - var6;
      float var11 = Math.abs(var0 - var7) - var9;
      float var12 = Math.abs(var1 - var8) - var10;
      float var13 = Math.max(var11, 0.0F);
      float var14 = Math.max(var12, 0.0F);
      return (float)Math.sqrt(var13 * var13 + var14 * var14) + Math.min(Math.max(var11, var12), 0.0F) - var6;
   }

   private static float compute(float var0, float var1) {
      return (float)Math.sqrt(var0 * var0 + var1 * var1);
   }

   static float onTick(float var0) {
      float var1 = process(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   static float update(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   static float apply(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   static float execute(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private static int process(int var0, int var1) {
      int var2 = Math.max(0, Math.min(255, var1));
      return var0 & 16777215 | var2 << 24;
   }

   static int handle(int var0, float var1) {
      return process(var0, Math.round(process(var1, 0.0F, 1.0F) * 255.0F));
   }

   static int handle(float var0, float var1, float var2, float var3) {
      int var4 = Math.round(process(var0, 0.0F, 1.0F) * 255.0F);
      int var5 = Math.round(process(var1, 0.0F, 1.0F) * 255.0F);
      int var6 = Math.round(process(var2, 0.0F, 1.0F) * 255.0F);
      int var7 = Math.round(process(var3, 0.0F, 1.0F) * 255.0F);
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   public static final class ColorState {
      private float instance;
      private float data;
      private float context;
      private float config;
      private float state;
      private float cache;
      private float output;
      private float current;
      private float active;

      public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
      }

      public void handle(float var1, float var2) {
         this.current = var1;
         this.active = var2;
      }

      public float handle() {
         return this.instance;
      }

      public float process() {
         return this.data;
      }

      public float compute() {
         return this.context;
      }

      public float resolve() {
         return this.config;
      }

      public float update() {
         return this.state;
      }

      public float apply() {
         return this.cache;
      }

      public float execute() {
         return this.output;
      }

      public float prepare() {
         return this.current;
      }

      public float check() {
         return this.active;
      }
   }

   record DataRecord(String label, String note, WildMainMenuScreen.PrimaryMode kind, ViaFabricPlusInstaller.DataRecord version) {
   }

   public static final class FallbackNetworkState {
      private float instance;
      private float data;
      private float context;
      private float config;
      private float state;
      private float cache;
      private float output;
      private float current;
      private float active;
      private float mode = 1.0F;
      private float selection = 1.0F;
      private float enabled;
      private float renderer;
      private float handler;
      private float animationDraw;
      private float pointEncode;
      private float animator;
      private float source;
      private float target;
      private float pending;
      private float previous;
      private float latest;
      private float summary;
      private float matrixBlend = 1.0F;
      private float vectorMatch;
      private float itemProject;
      private float responseCompute;
      private float providerFetch = 1.0F;

      public void handle(float var1) {
         this.itemProject = var1;
      }

      public void process(float var1) {
         this.providerFetch = var1;
      }

      public float handle() {
         return this.providerFetch;
      }

      public void compute(float var1) {
         this.responseCompute = var1;
      }

      public float process() {
         return this.responseCompute;
      }

      public float compute() {
         return this.itemProject;
      }

      public void handle(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.pending = var1;
         this.previous = var2;
         this.latest = var3;
         this.summary = var4;
         this.matrixBlend = var5;
         this.vectorMatch = var6;
      }

      public float resolve() {
         return this.pending;
      }

      public float update() {
         return this.previous;
      }

      public float apply() {
         return this.latest;
      }

      public float execute() {
         return this.summary;
      }

      public float prepare() {
         return this.matrixBlend;
      }

      public float check() {
         return this.vectorMatch;
      }

      public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
         this.mode = var10;
         this.selection = var11;
         this.enabled = 0.0F;
         this.renderer = 0.0F;
         this.handler = 0.0F;
         this.animationDraw = 0.0F;
         this.pointEncode = 0.0F;
         this.animator = 0.0F;
         this.source = 0.0F;
         this.target = 0.0F;
         this.pending = 0.0F;
         this.previous = 0.0F;
         this.latest = 0.0F;
         this.summary = 0.0F;
         this.matrixBlend = 1.0F;
         this.vectorMatch = 0.0F;
         this.itemProject = 0.0F;
         this.responseCompute = 0.0F;
         this.providerFetch = 1.0F;
      }

      public void handle(float var1, float var2) {
         this.enabled = var1;
         this.renderer = var2;
      }

      public void process(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.handler = var1;
         this.animationDraw = var2;
         this.pointEncode = var3;
         this.animator = var4;
         this.source = var5;
         this.target = var6;
      }

      public float onTick() {
         return this.instance;
      }

      public float select() {
         return this.data;
      }

      public float refresh() {
         return this.context;
      }

      public float render() {
         return this.config;
      }

      public float tick() {
         return this.state;
      }

      public float drawAnimation() {
         return this.cache;
      }

      public float encodePoint() {
         return this.output;
      }

      public float animate() {
         return this.current;
      }

      public float load() {
         return this.active;
      }

      public float save() {
         return this.mode;
      }

      public float submit() {
         return this.selection;
      }

      public float unload() {
         return this.enabled;
      }

      public float fetch() {
         return this.renderer;
      }

      public float measure() {
         return this.handler;
      }

      public float blendMatrix() {
         return this.animationDraw;
      }

      public float matchVector() {
         return this.pointEncode;
      }

      public float projectItem() {
         return this.animator;
      }

      public float computeResponse() {
         return this.source;
      }

      public float fetchProvider() {
         return this.target;
      }
   }

   static final class FileEntry {
      final String instance;
      final WildMainMenuScreen.Mode data;
      final DampedOscillator context = new DampedOscillator(MotionSpringPresets.state);
      final DampedOscillator config = new DampedOscillator(MotionSpringPresets.output);
      final DampedOscillator state = new DampedOscillator(MotionSpringPresets.selection);
      final DampedOscillator cache = new DampedOscillator(MotionSpringPresets.active);
      final DampedOscillator output = new DampedOscillator(MotionSpringPresets.current);
      boolean current;
      boolean active;
      float mode;
      float selection = 1.0F;
      final DampedOscillator enabled = new DampedOscillator(MotionSpringPresets.enabled);
      final DampedOscillator renderer = new DampedOscillator(MotionSpringPresets.enabled);
      final DampedOscillator handler = new DampedOscillator(MotionSpringPresets.previous);
      final DampedOscillator animationDraw = new DampedOscillator(MotionSpringPresets.previous);
      final DampedOscillator pointEncode = new DampedOscillator(MotionSpringPresets.config);
      final DampedOscillator animator = new DampedOscillator(MotionSpringPresets.mode);
      float source;
      float target = -1.6F;
      float pending;
      float previous;
      float latest;
      float summary;
      float matrixBlend;
      float vectorMatch;
      float itemProject;
      float responseCompute;
      float providerFetch;
      float profileDraw;
      float vectorPerform;
      float eventAttach = 1.0F;
      float serverRead = 0.5F;
      float positionAdvance = 0.5F;
      float frameCheck;
      float moduleCollect;
      float providerClose;

      FileEntry(String var1, WildMainMenuScreen.Mode var2) {
         this.instance = var1;
         this.data = var2;
      }

      void handle() {
         this.responseCompute = 0.0F;
         this.providerFetch = 0.0F;
         this.profileDraw = 0.0F;
         this.vectorPerform = 0.0F;
         this.moduleCollect = 0.0F;
         this.providerClose = 0.0F;
         this.eventAttach = 1.0F;
         this.serverRead = 0.5F;
         this.positionAdvance = 0.5F;
         this.frameCheck = 0.0F;
         this.current = false;
         this.active = false;
         this.mode = 0.0F;
         this.selection = 1.0F;
         this.output.handle(0.0F);
         this.source = 0.0F;
         this.target = -1.6F;
         this.handler.handle(0.0F);
         this.animationDraw.handle(-1.6F);
         this.pointEncode.handle(0.0F);
         this.animator.handle(0.0F);
         this.context.handle(0.0F);
         this.config.handle(0.0F);
         this.state.handle(0.0F);
         this.cache.handle(1.0F);
         this.enabled.handle(0.5F);
         this.renderer.handle(0.5F);
      }

      boolean handle(float var1, float var2) {
         float var3 = this.matrixBlend * 0.5F * this.eventAttach;
         float var4 = this.vectorMatch * 0.5F * this.eventAttach;
         float var5 = this.latest + this.matrixBlend * 0.5F;
         float var6 = this.summary + this.providerClose + this.vectorMatch * 0.5F;
         return WildMainMenuScreen.handle(var1, var2, var5 - var3, var6 - var4, var3 * 2.0F, var4 * 2.0F, this.itemProject * this.eventAttach) <= 0.0F;
      }
   }

   enum Mode {
      SINGLEPLAYER,
      MULTIPLAYER,
      ALT_MANAGER,
      OPTIONS,
      QUIT;
   }

   public static final class NetworkState {
      private float instance;
      private float data;
      private float context;
      private float config;
      private float state;

      public void handle(float var1, float var2, float var3, float var4, float var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
      }

      public float handle() {
         return this.instance;
      }

      public float process() {
         return this.data;
      }

      public float compute() {
         return this.context;
      }

      public float resolve() {
         return this.config;
      }

      public float update() {
         return this.state;
      }
   }

   public static final class PrimaryColorState {
      String instance = "";
      float data;
      float context;
      float config;
      float state;
      private float cache;
      private float output;
      private float current;
      private float active;
      float mode;
      private float selection;
      private float enabled;
      float renderer = 1.0F;
      private float handler = 0.5F;
      private float animationDraw = 0.5F;
      private float pointEncode;
      private float animator;
      private float source;
      private boolean target;
      private float pending = 1.0F;
      private float previous;

      public void handle(
         String var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13,
         float var14,
         float var15,
         float var16
      ) {
         this.instance = var1 == null ? "" : var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
         this.mode = var10;
         this.selection = var11;
         this.enabled = var12;
         this.renderer = var13;
         this.handler = var14;
         this.animationDraw = var15;
         this.pointEncode = var16;
         this.animator = 0.0F;
         this.source = 0.0F;
         this.target = false;
         this.pending = 1.0F;
         this.previous = 0.0F;
      }

      public void handle(float var1, float var2) {
         this.animator = var1;
         this.source = var2;
         this.target = true;
      }

      public void handle(float var1) {
         this.pending = var1;
      }

      public void process(float var1) {
         this.previous = var1;
      }

      public float handle() {
         return this.previous;
      }

      public float process() {
         return this.pending;
      }

      public boolean compute() {
         return this.target;
      }

      public float resolve() {
         return this.animator;
      }

      public float update() {
         return this.source;
      }

      public String apply() {
         return this.instance;
      }

      public float execute() {
         return this.data;
      }

      public float prepare() {
         return this.context;
      }

      public float check() {
         return this.config;
      }

      public float onTick() {
         return this.state;
      }

      public float select() {
         return this.cache;
      }

      public float refresh() {
         return this.output;
      }

      public float render() {
         return this.current;
      }

      public float tick() {
         return this.active;
      }

      public float drawAnimation() {
         return this.mode;
      }

      public float encodePoint() {
         return this.selection;
      }

      public float animate() {
         return this.enabled;
      }

      public float load() {
         return this.renderer;
      }

      public float save() {
         return this.handler;
      }

      public float submit() {
         return this.animationDraw;
      }

      public float unload() {
         return this.pointEncode;
      }
   }

   static final class PrimaryFileEntry {
      private final DampedOscillator instance = new DampedOscillator(MotionSpringPresets.matrixBlend);
      private final DampedOscillator data = new DampedOscillator(MotionSpringPresets.matrixBlend);
      private final DampedOscillator context = new DampedOscillator(MotionSpringPresets.animationDraw);
      private final DampedOscillator config = new DampedOscillator(MotionSpringPresets.animationDraw);
      private final DampedOscillator state = new DampedOscillator(MotionSpringPresets.vectorMatch);
      private final DampedOscillator cache = new DampedOscillator(MotionSpringPresets.vectorMatch);
      private final DampedOscillator output = new DampedOscillator(MotionSpringPresets.vectorMatch);
      private final DampedOscillator current = new DampedOscillator(MotionSpringPresets.previous);
      private final DampedOscillator active = new DampedOscillator(MotionSpringPresets.previous);
      private final List<WildMainMenuScreen.DataRecord> mode = new ArrayList<>();
      private final List<WildMainMenuScreen.DataRecord> selection = new ArrayList<>();
      private final StringBuilder enabled = new StringBuilder();
      private List<ViaFabricPlusInstaller.DataRecord> renderer = List.of();
      private boolean handler;
      boolean animationDraw;
      private boolean pointEncode;
      private String animator = "1.21.8";
      private String source = "";
      private float target;
      private float pending;
      private float previous;
      private float latest;
      private float summary;
      private float matrixBlend;
      private float vectorMatch;
      private float itemProject;
      private float responseCompute;
      private float providerFetch;
      private float profileDraw;
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
      private int outputCollapse;
      private int profileInvoke;
      private int sourceSchedule = -1;
      private float timerRender;
      private float scaleSave;
      private float colorCompute;
      private float scaleAdapt;
      private float textureRun;
      private float indexBind;
      private float actionRead;
      private float configCollapse;
      private float dataValidate;
      private float scaleRender;
      private float clientRefresh;
      private float keyFilter;
      private float requestAdapt;
      int timerMeasure = -1;

      void handle() {
         this.animationDraw = false;
         this.pointEncode = false;
         this.sourceSchedule = -1;
         this.profileInvoke = 0;
         this.target = 0.0F;
         this.enabled.setLength(0);
         this.instance.handle(0.0F);
         this.data.handle(0.0F);
         this.context.handle(0.0F);
         this.config.handle(0.0F);
         this.state.handle(0.0F);
         this.cache.handle(0.0F);
         this.output.handle(0.0F);
         this.current.handle(0.0F);
         this.active.handle(0.0F);
         this.configCollapse = 0.0F;
         this.dataValidate = 0.0F;
         this.renderer = List.of();
         this.mode.clear();
         this.selection.clear();
         this.pending = 0.0F;
      }

      private List<WildMainMenuScreen.DataRecord> process() {
         return this.pointEncode ? this.selection : this.mode;
      }

      private void compute() {
         this.handler = ViaFabricPlusInstaller.handle();
         this.animator = this.handler ? ViaFabricPlusInstaller.execute() : ViaFabricPlusInstaller.resolve();
         this.mode.clear();
         if (!this.handler) {
            this.source = this.animator;
            String var4 = ViaFabricPlusInstaller.process() ? "Обновить ViaFabricPlus" : ViaInstallScreen.process();
            this.mode.add(new WildMainMenuScreen.DataRecord(var4, ViaInstallScreen.resolve(), WildMainMenuScreen.PrimaryMode.INSTALL, null));
            this.pending = 0.0F;
         } else {
            this.renderer = ViaFabricPlusInstaller.update();
            ViaFabricPlusInstaller.DataRecord var1 = ViaFabricPlusInstaller.apply();
            this.source = var1 == null ? this.animator : var1.label();

            for (ViaFabricPlusInstaller.DataRecord var3 : ViaFabricPlusInstaller.handle(this.renderer, var1, 8)) {
               this.mode.add(new WildMainMenuScreen.DataRecord(var3.label(), var3.autoDetect() ? "auto" : null, WildMainMenuScreen.PrimaryMode.VERSION, var3));
            }

            this.mode.add(new WildMainMenuScreen.DataRecord("Все версии…", String.valueOf(this.renderer.size()), WildMainMenuScreen.PrimaryMode.MORE, null));
            this.pending = 0.0F;
         }
      }

      private void resolve() {
         this.selection.clear();
         String var1 = this.enabled.toString().trim().toLowerCase(Locale.ROOT);

         for (ViaFabricPlusInstaller.DataRecord var3 : this.renderer) {
            if (var1.isEmpty() || var3.label().toLowerCase(Locale.ROOT).contains(var1)) {
               this.selection.add(new WildMainMenuScreen.DataRecord(var3.label(), handle(var3.group()), WildMainMenuScreen.PrimaryMode.VERSION, var3));
            }
         }

         this.profileInvoke = 0;
         this.output.handle(0.0F);
      }

      private static String handle(String var0) {
         return switch (var0) {
            case "RELEASE", "RELEASE_INITIAL" -> "Release";
            case "SPECIAL" -> "Special";
            case "CLASSIC" -> "Classic";
            case "ALPHA_INITIAL", "ALPHA_LATER" -> "Alpha";
            case "BETA_INITIAL", "BETA_LATER" -> "Beta";
            default -> "";
         };
      }

      void handle(WildMainMenuScreen var1, int var2, int var3, float var4, float var5) {
         SdfTextRenderer var6 = RoundedRectRenderer.handle(FontRegistry.instance);
         this.responseCompute = WildMainMenuScreen.handle(14.5F, var4);
         this.providerFetch = WildMainMenuScreen.handle(12.0F, var4);
         this.itemProject = 40.0F * var4;
         this.target -= var5;
         if (this.target <= 0.0F) {
            this.target = this.handler ? 0.75F : 0.35F;
            this.compute();
            if (this.pointEncode) {
               this.resolve();
            }
         }

         this.profileDraw = 20.0F * var4;
         if (this.pending <= 0.0F && var6 != null) {
            float var7 = this.profileDraw * 1.25F;
            float var8 = 0.0F;

            for (WildMainMenuScreen.DataRecord var10 : this.mode) {
               float var11 = var6.handle(var10.label(), this.responseCompute * 0.5F);
               if (var10.note() != null && !var10.note().isEmpty()) {
                  var11 += var7 + var6.handle(var10.note(), this.providerFetch * 0.5F);
               }

               var8 = Math.max(var8, var11);
            }

            this.pending = var8;
         }

         this.keyFilter = 11.0F * var4;
         this.requestAdapt = 4.0F * var4;
         float var15 = var6 == null ? 62.0F * var4 : var6.handle(this.animator, this.responseCompute * 0.5F);
         this.matrixBlend = Math.max(48.0F * var4, this.responseCompute * 1.1F);
         this.summary = Math.max(150.0F * var4, var15 + this.profileDraw * 2.0F + this.keyFilter * 2.0F + this.profileDraw * 0.55F);
         this.vectorMatch = this.matrixBlend * 0.32F;
         this.previous = var2 - 32.0F * var4 - this.summary;
         this.latest = var3 - 32.0F * var4 - this.matrixBlend;
         this.scaleRender = this.summary - this.profileDraw - this.keyFilter;
         this.clientRefresh = this.matrixBlend * 0.5F;
         this.providerClose = Math.max(34.0F * var4, this.responseCompute * 1.3F);
         this.presetSave = 6.0F * var4;
         this.moduleCollect = 8.0F * var4;
         this.frameCheck = Math.min(18.0F * var4, this.providerClose * 0.72F);
         this.serverRead = Math.max(this.summary, this.pending + (this.profileDraw + this.presetSave) * 2.0F);
         this.positionAdvance = this.mode.size() * this.providerClose + this.moduleCollect * 2.0F;
         this.vectorPerform = this.previous + this.summary - this.serverRead;
         this.eventAttach = this.latest - 12.0F * var4 - this.positionAdvance;
         this.colorMeasure = WildMainMenuScreen.process(var2 * 0.34F, 340.0F * var4, 520.0F * var4);
         this.sourceBuild = 52.0F * var4;
         float var16 = var3 - 32.0F * var4 * 4.0F;
         this.outputCollapse = Math.max(
            3, (int)Math.floor((Math.min(var3 * 0.62F, var16) - this.sourceBuild - this.moduleCollect * 2.0F) / Math.max(1.0F, this.providerClose))
         );
         this.outputCollapse = Math.min(this.outputCollapse, Math.max(3, this.selection.size()));
         this.animationSchedule = this.sourceBuild + this.outputCollapse * this.providerClose + this.moduleCollect * 2.0F;
         this.windowConvert = var2 * 0.5F - this.colorMeasure * 0.5F;
         this.presetWrite = var3 * 0.5F - this.animationSchedule * 0.5F;
         this.rendererScan = Math.min(24.0F * var4, this.animationSchedule * 0.2F);
         int var17 = Math.max(0, this.process().size() - this.update());
         this.profileInvoke = Math.max(0, Math.min(var17, this.profileInvoke));
         float var18 = var1.taskLoad;
         float var19 = var1.pointSend;
         boolean var12 = this.handle(var18, var19);
         this.sourceSchedule = this.animationDraw ? this.compute(var18, var19) : -1;
         this.scaleAdapt = this.context.handle(!var12 && !this.animationDraw ? 0.0F : 1.0F, var5);
         this.textureRun = this.config.handle(var12 ? 1.0F : (this.animationDraw ? 0.55F : 0.0F), var5);
         this.scaleSave = this.instance.handle(this.animationDraw && !this.pointEncode ? 1.0F : 0.0F, var5);
         this.colorCompute = this.data.handle(this.pointEncode ? 1.0F : 0.0F, var5);
         this.indexBind = this.state.handle(this.sourceSchedule >= 0 ? 1.0F : 0.0F, var5);
         this.timerRender = this.output.handle(this.profileInvoke * this.providerClose, var5);
         float var13 = this.pointEncode ? this.presetWrite + this.sourceBuild + this.moduleCollect : this.eventAttach + this.moduleCollect;
         float var14 = this.sourceSchedule >= 0 ? var13 + (this.sourceSchedule * this.providerClose - this.timerRender) : this.actionRead;
         this.actionRead = this.cache.handle(var14, var5);
         this.configCollapse = this.current
            .handle(
               WildMainMenuScreen.process((var1.playerMatch.handle() - (this.previous + this.summary * 0.5F)) / Math.max(1.0F, this.summary), -3.2F, 3.2F),
               var5
            );
         this.dataValidate = this.active
            .handle(
               WildMainMenuScreen.process((var1.cacheClose.handle() - (this.latest + this.matrixBlend * 0.5F)) / Math.max(1.0F, this.matrixBlend), -3.2F, 3.2F),
               var5
            );
      }

      private int update() {
         return this.pointEncode ? this.outputCollapse : this.mode.size();
      }

      private boolean handle(float var1, float var2) {
         return this.summary > 0.0F
            && WildMainMenuScreen.handle(var1, var2, this.previous, this.latest, this.summary, this.matrixBlend, this.vectorMatch) <= 0.0F;
      }

      private boolean process(float var1, float var2) {
         if (!this.animationDraw) {
            return false;
         } else {
            return this.pointEncode
               ? WildMainMenuScreen.handle(var1, var2, this.windowConvert, this.presetWrite, this.colorMeasure, this.animationSchedule, this.rendererScan)
                  <= 0.0F
               : this.serverRead > 0.0F
                  && this.positionAdvance > 0.0F
                  && WildMainMenuScreen.handle(var1, var2, this.vectorPerform, this.eventAttach, this.serverRead, this.positionAdvance, this.frameCheck)
                     <= 0.0F;
         }
      }

      private int compute(float var1, float var2) {
         if (!this.process(var1, var2)) {
            return -1;
         } else {
            float var3 = this.pointEncode ? this.presetWrite + this.sourceBuild + this.moduleCollect : this.eventAttach + this.moduleCollect;
            float var4 = (this.pointEncode ? this.presetWrite + this.animationSchedule : this.eventAttach + this.positionAdvance) - this.moduleCollect;
            if (!(var2 < var3) && !(var2 >= var4)) {
               float var5 = var2 - var3 + this.timerRender;
               int var6 = (int)Math.floor(var5 / Math.max(1.0F, this.providerClose));
               var6 = Math.min(var6, this.profileInvoke + Math.max(1, this.update()) - 1);
               return var6 >= 0 && var6 < this.process().size() ? var6 : -1;
            } else {
               return -1;
            }
         }
      }

      private void apply() {
         this.animationDraw = false;
         this.pointEncode = false;
         this.sourceSchedule = -1;
         this.enabled.setLength(0);
      }

      private boolean handle(WildMainMenuScreen.DataRecord var1) {
         switch (var1.kind()) {
            case VERSION:
               if (ViaFabricPlusInstaller.handle(var1.version())) {
                  this.source = var1.label();
                  this.animator = var1.label();
               }

               this.apply();
               this.target = 0.0F;
               break;
            case MORE:
               this.pointEncode = true;
               this.resolve();
               break;
            case INSTALL:
               if (ViaFabricPlusInstaller.process()) {
                  ViaInstallScreen.apply();
               } else {
                  ViaInstallScreen.update();
               }

               this.target = 0.0F;
         }

         return true;
      }

      boolean resolve(float var1, float var2) {
         if (this.animationDraw) {
            int var3 = this.compute(var1, var2);
            if (var3 >= 0) {
               return this.handle(this.process().get(var3));
            } else if (this.process(var1, var2)) {
               return true;
            } else if (this.pointEncode) {
               this.pointEncode = false;
               return true;
            } else {
               this.apply();
               return true;
            }
         } else if (this.handle(var1, var2)) {
            this.animationDraw = true;
            this.pointEncode = false;
            this.compute();
            this.profileInvoke = 0;
            this.output.handle(0.0F);
            return true;
         } else {
            return false;
         }
      }

      boolean handle(float var1, float var2, double var3) {
         if (!this.animationDraw) {
            return false;
         }

         if (!this.process(var1, var2)) {
            return true;
         }

         int var5 = Math.max(0, this.process().size() - Math.max(1, this.update()));
         this.profileInvoke = Math.max(0, Math.min(var5, this.profileInvoke + (var3 > 0.0 ? -1 : 1)));
         return true;
      }

      boolean handle(int var1) {
         if (!this.animationDraw) {
            return false;
         }

         if (var1 == 256) {
            if (this.pointEncode) {
               this.pointEncode = false;
            } else {
               this.apply();
            }

            return true;
         } else if (this.pointEncode && var1 == 259) {
            if (this.enabled.length() > 0) {
               this.enabled.setLength(this.enabled.length() - 1);
               this.resolve();
            }

            return true;
         } else {
            return false;
         }
      }

      boolean handle(char var1) {
         if (this.animationDraw && this.pointEncode && var1 >= ' ') {
            if (this.enabled.length() < 24) {
               this.enabled.append(var1);
               this.resolve();
            }

            return true;
         } else {
            return false;
         }
      }

      int handle(WildMainMenuScreen.PrimaryNetworkState var1, int var2, float var3, float var4, float var5) {
         WildMainMenuScreen.FallbackNetworkState var6 = var1.compute(var2++);
         var6.handle(
            this.previous, this.latest, this.summary, this.matrixBlend, this.vectorMatch, this.itemProject, var3, this.scaleAdapt, this.textureRun, 1.0F, 1.0F
         );
         var6.handle(this.configCollapse * this.summary, this.dataValidate * this.matrixBlend);
         var6.handle(this.scaleRender, this.clientRefresh, this.keyFilter, this.requestAdapt, this.scaleSave, 0.55F + 0.45F * this.scaleAdapt);
         var6.process(0.42F * (1.0F - this.scaleSave * 0.55F));
         return var2;
      }

      int handle(WildMainMenuScreen.PrimaryNetworkState var1, float var2, float var3, float var4) {
         int var5 = 0;
         float var6 = Math.max(this.scaleSave * 0.46F, this.colorCompute * 1.0F);
         float var7 = WildMainMenuScreen.process(Math.max(this.scaleSave, this.colorCompute) * 1.45F, 0.0F, 1.0F);
         if (var6 > 0.004F) {
            WildMainMenuScreen.FallbackNetworkState var8 = var1.apply(var5++);
            var8.handle(-4.0F, -4.0F, var3 + 8.0F, var4 + 8.0F, 0.0F, 0.0F, var2, 0.0F, var6, 1.0F, 1.0F);
            var8.handle(var7);
         }

         if (this.scaleSave > 0.004F) {
            WildMainMenuScreen.FallbackNetworkState var9 = var1.apply(var5++);
            var9.handle(
               this.vectorPerform,
               this.eventAttach,
               this.serverRead,
               this.positionAdvance,
               this.frameCheck,
               this.itemProject,
               var2,
               this.scaleAdapt,
               0.0F,
               this.scaleSave,
               -1.0F
            );
            var9.handle(this.configCollapse * this.serverRead, -this.positionAdvance * 2.4F);
            var9.compute(this.scaleSave);
            var9.process(this.scaleSave);
            if (!this.pointEncode && (this.sourceSchedule >= 0 || this.indexBind > 0.004F)) {
               var9.process(
                  this.presetSave,
                  this.actionRead - this.eventAttach,
                  this.serverRead - this.presetSave * 2.0F,
                  this.providerClose,
                  this.providerClose * 0.32F,
                  this.indexBind
               );
            }
         }

         if (this.colorCompute > 0.004F) {
            WildMainMenuScreen.FallbackNetworkState var10 = var1.apply(var5++);
            var10.handle(
               this.windowConvert,
               this.presetWrite,
               this.colorMeasure,
               this.animationSchedule,
               this.rendererScan,
               this.itemProject,
               var2 * this.colorCompute,
               1.0F,
               0.0F,
               1.0F,
               1.0F
            );
            var10.handle(0.0F, -this.animationSchedule * 2.4F);
            var10.compute(this.colorCompute);
            var10.process(this.colorCompute);
            if (this.pointEncode && (this.sourceSchedule >= 0 || this.indexBind > 0.004F)) {
               var10.process(
                  this.presetSave,
                  this.actionRead - this.presetWrite,
                  this.colorMeasure - this.presetSave * 2.0F,
                  this.providerClose,
                  this.providerClose * 0.32F,
                  this.indexBind
               );
            }
         }

         return var5;
      }

      void handle(RoundedRectRenderer var1, float var2, boolean var3) {
         if (!(var2 <= 0.004F)) {
            int var4 = var3 ? WildMainMenuScreen.handle(0.16F, 0.15F, 0.2F, 1.0F) : WildMainMenuScreen.handle(0.86F, 0.88F, 0.96F, 1.0F);
            int var5 = var3 ? WildMainMenuScreen.handle(0.06F, 0.05F, 0.09F, 1.0F) : WildMainMenuScreen.handle(1.0F, 1.0F, 1.0F, 1.0F);
            float var6 = this.latest + this.matrixBlend * 0.5F + this.responseCompute * 0.17F;
            float var7 = this.scaleRender - this.keyFilter * 0.8F;
            var1.handle(
               FontRegistry.instance,
               Math.round(this.previous + (this.profileDraw + var7) * 0.5F),
               Math.round(var6),
               this.responseCompute,
               this.animator,
               WildMainMenuScreen.handle(this.scaleAdapt > 0.35F ? var5 : var4, var2),
               "c"
            );
         }
      }

      void process(RoundedRectRenderer var1, float var2, boolean var3) {
         if (!(var2 <= 0.004F)) {
            int var4 = var3 ? WildMainMenuScreen.handle(0.16F, 0.15F, 0.2F, 1.0F) : WildMainMenuScreen.handle(0.86F, 0.88F, 0.96F, 1.0F);
            int var5 = var3 ? WildMainMenuScreen.handle(0.06F, 0.05F, 0.09F, 1.0F) : WildMainMenuScreen.handle(1.0F, 1.0F, 1.0F, 1.0F);
            int var6 = var3 ? WildMainMenuScreen.handle(0.42F, 0.41F, 0.46F, 1.0F) : WildMainMenuScreen.handle(0.58F, 0.6F, 0.7F, 1.0F);
            if (this.scaleSave > 0.004F && !this.pointEncode) {
               this.handle(
                  var1,
                  var2 * this.scaleSave,
                  this.vectorPerform,
                  this.eventAttach + this.moduleCollect,
                  this.eventAttach + this.positionAdvance - this.moduleCollect,
                  this.serverRead,
                  this.mode,
                  var4,
                  var5,
                  var6,
                  var3,
                  this.eventAttach + this.positionAdvance - this.positionAdvance * this.scaleSave
               );
            }

            if (this.colorCompute > 0.004F) {
               float var7 = var2 * this.colorCompute;
               float var8 = this.presetWrite + this.sourceBuild * 0.58F + this.responseCompute * 0.17F;
               var1.handle(
                  FontRegistry.instance,
                  Math.round(this.windowConvert + this.profileDraw),
                  Math.round(var8),
                  this.responseCompute,
                  "Версия протокола",
                  WildMainMenuScreen.handle(var5, var7),
                  "l"
               );
               String var9 = this.enabled.length() == 0 ? "начните печатать для поиска" : this.enabled.toString();
               var1.handle(
                  FontRegistry.instance,
                  Math.round(this.windowConvert + this.colorMeasure - this.profileDraw),
                  Math.round(var8),
                  this.providerFetch,
                  var9,
                  WildMainMenuScreen.handle(this.enabled.length() == 0 ? var6 : var5, var7),
                  "r"
               );
               this.handle(
                  var1,
                  var7,
                  this.windowConvert,
                  this.presetWrite + this.sourceBuild + this.moduleCollect,
                  this.presetWrite + this.animationSchedule - this.moduleCollect,
                  this.colorMeasure,
                  this.selection,
                  var4,
                  var5,
                  var6,
                  var3,
                  this.presetWrite
               );
            }
         }
      }

      private void handle(
         RoundedRectRenderer var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         List<WildMainMenuScreen.DataRecord> var7,
         int var8,
         int var9,
         int var10,
         boolean var11,
         float var12
      ) {
         int var13 = this.timerMeasure;

         for (int var14 = 0; var14 < var7.size(); var14++) {
            float var15 = var4 + var14 * this.providerClose - this.timerRender;
            float var16 = var15 + this.providerClose * 0.5F;
            if (!(var16 < var4 - this.providerClose) && !(var16 > var5 + this.providerClose)) {
               float var17 = WildMainMenuScreen.process(Math.min(var16 - var4, var5 - var16) / Math.max(1.0F, this.providerClose * 0.5F), 0.0F, 1.0F);
               float var18 = WildMainMenuScreen.process((var16 - var12) / Math.max(1.0F, this.providerClose * 0.8F), 0.0F, 1.0F);
               float var19 = var2 * WildMainMenuScreen.onTick(var17) * var18;
               if (!(var19 <= 0.004F)) {
                  WildMainMenuScreen.DataRecord var20 = (WildMainMenuScreen.DataRecord)var7.get(var14);
                  boolean var21 = var20.kind() == WildMainMenuScreen.PrimaryMode.VERSION && var20.label().equals(this.source);
                  boolean var22 = var14 == this.sourceSchedule;

                  int var23 = switch (var20.kind()) {
                     case VERSION -> var21 ? var13 : (var22 ? var9 : var8);
                     case MORE, INSTALL -> var22 ? var9 : var8;
                  };
                  float var24 = !var22 && !var21 ? 0.86F : 1.0F;
                  var1.handle(
                     FontRegistry.instance,
                     Math.round(var3 + this.presetSave + this.profileDraw * 0.75F),
                     Math.round(var16 + this.responseCompute * 0.17F),
                     this.responseCompute,
                     var20.label(),
                     WildMainMenuScreen.handle(var23, var19 * var24),
                     "l"
                  );
                  if (var20.note() != null && !var20.note().isEmpty() && !"Release".equals(var20.note())) {
                     var1.handle(
                        FontRegistry.instance,
                        Math.round(var3 + var6 - this.presetSave - this.profileDraw * 0.75F),
                        Math.round(var16 + this.providerFetch * 0.17F),
                        this.providerFetch,
                        var20.note(),
                        WildMainMenuScreen.handle(var10, var19 * 0.9F),
                        "r"
                     );
                  }
               }
            }
         }
      }
   }

   enum PrimaryMode {
      VERSION,
      MORE,
      INSTALL;
   }

   public static final class PrimaryNetworkState {
      private WildMainMenuScreen.PrimaryColorState[] instance;
      private WildMainMenuScreen.FallbackNetworkState[] data = new WildMainMenuScreen.FallbackNetworkState[0];
      private int context;
      private WildMainMenuScreen.FallbackNetworkState[] config = new WildMainMenuScreen.FallbackNetworkState[0];
      private int state;
      private final WildMainMenuScreen.PrimaryScreenState cache = new WildMainMenuScreen.PrimaryScreenState();
      private final WildMainMenuScreen.ColorState output = new WildMainMenuScreen.ColorState();
      private final WildMainMenuScreen.SecondaryFileEntry[] current;
      private final WildMainMenuScreen.NetworkState active = new WildMainMenuScreen.NetworkState();
      final WildMainMenuScreen.SecondaryNetworkState mode = new WildMainMenuScreen.SecondaryNetworkState();
      private int selection;
      int enabled;
      int renderer;
      private int handler;
      private float animationDraw;
      private float pointEncode;
      private float animator;
      private float source;
      private float target;
      private float pending;
      private float previous;
      private float latest;
      private float summary;
      private float matrixBlend;
      private float vectorMatch;
      private float itemProject;
      private float responseCompute;
      private float providerFetch;
      private float profileDraw;
      private float vectorPerform;
      private float eventAttach;
      private float serverRead;
      private float positionAdvance;
      private float frameCheck;
      private float moduleCollect;
      private float providerClose;
      private float presetSave;
      private float windowConvert = 1.0F;
      private float presetWrite;
      private float colorMeasure;
      private float animationSchedule;
      private float rendererScan;
      private float sourceBuild;
      private float outputCollapse;
      private boolean profileInvoke;
      boolean sourceSchedule;
      private boolean timerRender;
      private boolean scaleSave;
      private float colorCompute;

      public PrimaryNetworkState(int var1, int var2) {
         this.instance = new WildMainMenuScreen.PrimaryColorState[var1];

         for (int var3 = 0; var3 < var1; var3++) {
            this.instance[var3] = new WildMainMenuScreen.PrimaryColorState();
         }

         this.current = new WildMainMenuScreen.SecondaryFileEntry[var2];

         for (int var4 = 0; var4 < var2; var4++) {
            this.current[var4] = new WildMainMenuScreen.SecondaryFileEntry();
         }

         this.selection = var1;
      }

      public WildMainMenuScreen.PrimaryColorState handle(int var1) {
         this.process(var1 + 1);
         return this.instance[var1];
      }

      public void process(int var1) {
         if (var1 > this.instance.length) {
            int var2 = Math.max(var1, this.instance.length * 2);
            WildMainMenuScreen.PrimaryColorState[] var3 = new WildMainMenuScreen.PrimaryColorState[var2];
            System.arraycopy(this.instance, 0, var3, 0, this.instance.length);

            for (int var4 = this.instance.length; var4 < var2; var4++) {
               var3[var4] = new WildMainMenuScreen.PrimaryColorState();
            }

            this.instance = var3;
         }
      }

      public int handle() {
         return this.selection;
      }

      public WildMainMenuScreen.FallbackNetworkState compute(int var1) {
         this.resolve(var1 + 1);
         return this.data[var1];
      }

      public void resolve(int var1) {
         if (var1 > this.data.length) {
            int var2 = Math.max(var1, Math.max(4, this.data.length * 2));
            WildMainMenuScreen.FallbackNetworkState[] var3 = new WildMainMenuScreen.FallbackNetworkState[var2];
            System.arraycopy(this.data, 0, var3, 0, this.data.length);

            for (int var4 = this.data.length; var4 < var2; var4++) {
               var3[var4] = new WildMainMenuScreen.FallbackNetworkState();
            }

            this.data = var3;
         }
      }

      public int process() {
         return this.context;
      }

      public void update(int var1) {
         this.context = Math.max(0, Math.min(this.data.length, var1));
      }

      public WildMainMenuScreen.FallbackNetworkState apply(int var1) {
         this.execute(var1 + 1);
         return this.config[var1];
      }

      public void execute(int var1) {
         if (var1 > this.config.length) {
            int var2 = Math.max(var1, Math.max(4, this.config.length * 2));
            WildMainMenuScreen.FallbackNetworkState[] var3 = new WildMainMenuScreen.FallbackNetworkState[var2];
            System.arraycopy(this.config, 0, var3, 0, this.config.length);

            for (int var4 = this.config.length; var4 < var2; var4++) {
               var3[var4] = new WildMainMenuScreen.FallbackNetworkState();
            }

            this.config = var3;
         }
      }

      public int compute() {
         return this.state;
      }

      public void prepare(int var1) {
         this.state = Math.max(0, Math.min(this.config.length, var1));
      }

      public WildMainMenuScreen.PrimaryScreenState resolve() {
         return this.cache;
      }

      public WildMainMenuScreen.ColorState update() {
         return this.output;
      }

      public void check(int var1) {
         this.selection = Math.max(0, Math.min(this.instance.length, var1));
      }

      public WildMainMenuScreen.SecondaryFileEntry onTick(int var1) {
         return this.current[var1];
      }

      public void handle(int var1, int var2, int var3, float var4, float var5) {
         this.enabled = var1;
         this.renderer = var2;
         this.handler = var3;
         this.animationDraw = var4;
         this.pointEncode = var5;
      }

      public void handle(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.animator = var1;
         this.source = var2;
         this.target = var1 / Math.max(1.0F, this.enabled);
         this.pending = var2 / Math.max(1.0F, this.renderer);
         this.previous = var3;
         this.latest = var4;
         this.summary = var5;
         this.matrixBlend = var6;
      }

      public void handle(int var1, int var2) {
         this.vectorMatch = WildMainMenuScreen.update(var1);
         this.itemProject = WildMainMenuScreen.apply(var1);
         this.responseCompute = WildMainMenuScreen.execute(var1);
         this.providerFetch = WildMainMenuScreen.update(var2);
         this.profileDraw = WildMainMenuScreen.apply(var2);
         this.vectorPerform = WildMainMenuScreen.execute(var2);
      }

      public void process(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.eventAttach = var1;
         this.serverRead = var2;
         this.positionAdvance = var3;
         this.frameCheck = var4;
         this.moduleCollect = var5;
         this.providerClose = var6;
      }

      public void compute(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.presetSave = var1;
         this.windowConvert = WildMainMenuScreen.process(var2, 0.35F, 1.0F);
         this.presetWrite = WildMainMenuScreen.process(var3, 0.0F, 1.0F);
         this.colorMeasure = WildMainMenuScreen.process(var4, 0.0F, 1.0F);
         this.animationSchedule = var5;
         this.rendererScan = WildMainMenuScreen.process(var6, 0.0F, 1.0F);
      }

      public void handle(float var1, float var2) {
         this.sourceBuild = WildMainMenuScreen.process(var1, 0.0F, 1.0F);
         this.outputCollapse = var2;
      }

      public float apply() {
         return this.outputCollapse;
      }

      public void handle(boolean var1, boolean var2, boolean var3, boolean var4) {
         this.profileInvoke = var1;
         this.sourceSchedule = var2;
         this.timerRender = var3;
         this.scaleSave = var4;
      }

      public void handle(float var1) {
         this.colorCompute = var1;
      }

      public float execute() {
         return this.colorCompute;
      }

      public WildMainMenuScreen.NetworkState prepare() {
         return this.active;
      }

      public WildMainMenuScreen.SecondaryNetworkState check() {
         return this.mode;
      }

      public int onTick() {
         return this.enabled;
      }

      public int select() {
         return this.renderer;
      }

      public int refresh() {
         return this.handler;
      }

      public float render() {
         return this.animationDraw;
      }

      public float tick() {
         return this.pointEncode;
      }

      public float drawAnimation() {
         return this.animator;
      }

      public float encodePoint() {
         return this.source;
      }

      public float animate() {
         return this.target;
      }

      public float load() {
         return this.pending;
      }

      public float save() {
         return this.previous;
      }

      public float submit() {
         return this.latest;
      }

      public float unload() {
         return this.summary;
      }

      public float fetch() {
         return this.matrixBlend;
      }

      public float measure() {
         return this.vectorMatch;
      }

      public float blendMatrix() {
         return this.itemProject;
      }

      public float matchVector() {
         return this.responseCompute;
      }

      public float projectItem() {
         return this.providerFetch;
      }

      public float computeResponse() {
         return this.profileDraw;
      }

      public float fetchProvider() {
         return this.vectorPerform;
      }

      public float drawProfile() {
         return this.eventAttach;
      }

      public float performVector() {
         return this.serverRead;
      }

      public float attachEvent() {
         return this.positionAdvance;
      }

      public float readServer() {
         return this.frameCheck;
      }

      public float advancePosition() {
         return this.moduleCollect;
      }

      public float checkFrame() {
         return this.providerClose;
      }

      public float collectModule() {
         return this.presetSave;
      }

      public float closeProvider() {
         return this.windowConvert;
      }

      public float savePreset() {
         return this.presetWrite;
      }

      public float convertWindow() {
         return this.colorMeasure;
      }

      public float writePreset() {
         return this.animationSchedule;
      }

      public float measureColor() {
         return this.rendererScan;
      }

      public float scheduleAnimation() {
         return this.sourceBuild;
      }

      public boolean scanRenderer() {
         return this.profileInvoke;
      }

      public boolean buildSource() {
         return this.sourceSchedule;
      }

      public boolean collapseOutput() {
         return this.timerRender;
      }

      public boolean invokeProfile() {
         return this.scaleSave;
      }
   }

   public static final class PrimaryScreenState {
      private float instance;
      private float data;
      private float context;
      private float config;
      private float state;
      private float cache;
      private float output;
      private float current;
      private float active;
      private float mode;

      public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
      }

      public void handle(float var1, float var2) {
         this.active = var1;
         this.mode = var2;
      }

      public void handle() {
         this.context = 0.0F;
         this.cache = 0.0F;
      }

      public float process() {
         return this.instance;
      }

      public float compute() {
         return this.data;
      }

      public float resolve() {
         return this.context;
      }

      public float update() {
         return this.config;
      }

      public float apply() {
         return this.state;
      }

      public float execute() {
         return this.cache;
      }

      public float prepare() {
         return this.output;
      }

      public float check() {
         return this.current;
      }

      public float onTick() {
         return this.active;
      }

      public float select() {
         return this.mode;
      }
   }

   static final class RuntimeNetworkState {
      private final DampedOscillator instance = new DampedOscillator(MotionSpringPresets.renderer);
      private final DampedOscillator data = new DampedOscillator(MotionSpringPresets.animationDraw);
      final DampedOscillator context = new DampedOscillator(MotionSpringPresets.selection);
      private final DampedOscillator config = new DampedOscillator(MotionSpringPresets.animationDraw);
      private final DampedOscillator state = new DampedOscillator(MotionSpringPresets.enabled);
      private final DampedOscillator cache = new DampedOscillator(MotionSpringPresets.enabled);
      private final DampedOscillator output = new DampedOscillator(MotionSpringPresets.itemProject);
      private final float[] current;
      private final float[] active;
      private final float[] mode;
      private float selection;
      float enabled;
      float renderer;
      float handler;
      float animationDraw;
      float pointEncode;
      float animator;
      int source;
      boolean target;
      private boolean pending;
      private int previous;
      private float latest;
      private float summary;
      private float matrixBlend;
      private float vectorMatch;
      private float itemProject;
      private float responseCompute;
      private float providerFetch;
      private float profileDraw;
      private float vectorPerform;
      private float eventAttach;
      private float serverRead;

      RuntimeNetworkState() {
         this.current = new float[WildMainMenuScreen.source];
         this.active = new float[WildMainMenuScreen.source];
         this.mode = new float[WildMainMenuScreen.source];
         this.previous = -1;
      }

      void handle(int var1) {
         this.target = false;
         this.pending = false;
         this.previous = -1;
         this.instance.handle(var1);
         this.output.handle(0.0F);
         this.selection = 0.0F;
         this.enabled = 0.0F;
         this.source = var1;
         this.data.handle(0.0F);
         this.context.handle(0.0F);
         this.config.handle(0.0F);
         this.state.handle(0.5F);
         this.cache.handle(0.5F);
      }

      void handle(WildMainMenuScreen var1, int var2, int var3, float var4, float var5) {
         SdfTextRenderer var6 = RoundedRectRenderer.handle(FontRegistry.instance);
         this.providerFetch = WildMainMenuScreen.handle(14.5F, var4);
         this.responseCompute = 4.0F * var4;
         this.serverRead = 40.0F * var4;
         float var7 = 22.0F * var4;
         float var8 = 0.0F;

         for (int var9 = 0; var9 < WildMainMenuScreen.source; var9++) {
            this.mode[var9] = var6 == null ? 56.0F * var4 : var6.handle(WildMainMenuScreen.target[var9], this.providerFetch * 0.5F);
            var8 = Math.max(var8, this.mode[var9]);
         }

         float var18 = var8 + var7 * 2.0F;
         float var10 = this.responseCompute * 2.0F;

         for (int var11 = 0; var11 < WildMainMenuScreen.source; var11++) {
            this.active[var11] = var18;
            var10 += var18;
         }

         this.vectorMatch = Math.max(48.0F * var4, this.providerFetch * 1.1F);
         this.matrixBlend = var10;
         this.itemProject = this.vectorMatch * 0.32F;
         this.latest = 32.0F * var4;
         this.summary = var3 - this.vectorMatch - 32.0F * var4;
         float var19 = this.latest + this.responseCompute;

         for (int var12 = 0; var12 < WildMainMenuScreen.source; var12++) {
            this.current[var12] = var19 + this.active[var12] * 0.5F;
            var19 += this.active[var12];
         }

         float var20 = var1.taskLoad;
         float var13 = var1.pointSend;
         this.pending = this.handle(var20, var13) && !var1.stateAttach.animationDraw;
         int var14 = this.previous;
         this.previous = this.pending ? this.handle(var20) : -1;
         if (this.previous == var14 && this.previous >= 0) {
            this.selection += var5;
         } else {
            this.selection = 0.0F;
         }

         this.enabled = this.output.handle(this.previous >= 0 && this.selection >= 0.34F ? 1.0F : 0.0F, var5);
         this.pointEncode = 40.0F * var4;
         this.animator = WildMainMenuScreen.handle(13.0F, var4);
         this.source = WildMainMenuScreen.resolve(this.previous >= 0 ? this.previous : this.source);
         float var15 = var6 == null ? 220.0F * var4 : var6.handle(WildMainMenuScreen.contextExpand[this.source], this.animator * 0.5F);
         this.animationDraw = var15 + 36.0F * var4;
         float var16 = 32.0F * var4 * 0.5F;
         this.renderer = WildMainMenuScreen.process(
            this.current[this.source] - this.animationDraw * 0.5F, var16, Math.max(var16, var2 - this.animationDraw - var16)
         );
         this.handler = this.summary - 14.0F * var4 - this.pointEncode;
         int var17 = var1.apply();
         this.profileDraw = this.data.handle(this.pending ? 1.0F : 0.0F, var5);
         this.context.handle(0.0F, var5);
         this.vectorPerform = this.instance.handle(var17, var5);
         this.eventAttach = this.config.handle(!this.pending && !this.target ? 0.0F : 1.0F, var5);
         this.state.handle(WildMainMenuScreen.process((var1.playerMatch.handle() - this.latest) / Math.max(1.0F, this.matrixBlend), 0.0F, 1.0F), var5);
         this.cache.handle(WildMainMenuScreen.process((var1.cacheClose.handle() - this.summary) / Math.max(1.0F, this.vectorMatch), 0.0F, 1.0F), var5);
      }

      void handle(WildMainMenuScreen.SecondaryNetworkState var1, float var2) {
         var1.instance = this.latest;
         var1.data = this.summary;
         var1.context = this.matrixBlend;
         var1.config = this.vectorMatch;
         var1.state = this.itemProject;
         var1.cache = this.profileDraw;
         var1.output = this.context.handle();
         var1.current = var2;
         var1.active = this.state.handle();
         var1.mode = this.cache.handle();
         var1.source = this.serverRead;
         float var3 = WildMainMenuScreen.process(this.vectorPerform, -0.35F, WildMainMenuScreen.source - 1 + 0.35F);
         int var4 = Math.max(0, Math.min(WildMainMenuScreen.source - 2, (int)Math.floor(var3)));
         float var5 = var3 - var4;
         float var6 = this.current[var4] + (this.current[var4 + 1] - this.current[var4]) * var5;
         float var7 = this.active[var4] + (this.active[var4 + 1] - this.active[var4]) * WildMainMenuScreen.process(var5, 0.0F, 1.0F);
         float var8 = (float)Math.tanh(this.instance.process() / 9.0F);
         float var9 = 1.0F + 0.2F * Math.abs(var8);
         float var10 = 1.0F / (1.0F + (var9 - 1.0F) * 0.65F);
         float var11 = this.vectorMatch - this.responseCompute * 2.0F;
         var1.renderer = var7 * var9 - this.responseCompute * 0.5F;
         var1.handler = var11 * var10;
         var1.animator = var1.handler * 0.32F;
         var1.selection = var6 - var8 * var11 * 0.1F - var1.renderer * 0.5F - this.latest;
         var1.enabled = this.responseCompute + var11 * (1.0F - var10) * 0.5F;
         var1.animationDraw = this.eventAttach;
         var1.pointEncode = WildMainMenuScreen.process(var8, -1.0F, 1.0F);
      }

      void handle(RoundedRectRenderer var1, WildMainMenuScreen.SecondaryNetworkState var2, float var3, boolean var4, int var5, int var6, float var7) {
         float var8 = var2.current * var7;
         if (!(var8 <= 0.001F)) {
            float var9 = var2.data + var2.config * 0.5F + this.providerFetch * 0.17F;
            float var10 = var2.instance + var2.selection + var2.renderer * 0.5F;

            for (int var11 = 0; var11 < WildMainMenuScreen.source; var11++) {
               float var12 = this.current[var11];
               float var13 = 1.0F - WildMainMenuScreen.process(Math.abs(this.current[var11] - var10) / Math.max(1.0F, this.active[var11] * 0.7F), 0.0F, 1.0F);
               var13 = WildMainMenuScreen.onTick(var13);
               float var14 = var11 == this.previous ? var2.cache : 0.0F;
               float var15 = (0.52F + var14 * 0.28F) * (1.0F - var13) + 1.0F * var13;
               int var16 = var4 ? WildMainMenuScreen.handle(0.12F, 0.12F, 0.15F, var15 * var8) : WildMainMenuScreen.handle(0.88F, 0.9F, 0.97F, var15 * var8);
               int var17 = var4 ? WildMainMenuScreen.handle(0.08F, 0.07F, 0.12F, var8) : WildMainMenuScreen.handle(1.0F, 1.0F, 1.0F, var8);
               int var18 = var13 > 0.5F ? var17 : var16;
               var1.handle(FontRegistry.instance, Math.round(var12), Math.round(var9), this.providerFetch, WildMainMenuScreen.target[var11], var18, "c");
            }
         }
      }

      boolean handle(float var1, float var2) {
         float var3 = 6.0F;
         return this.matrixBlend > 0.0F
            && WildMainMenuScreen.handle(var1, var2, this.latest, this.summary - var3, this.matrixBlend, this.vectorMatch + var3 * 2.0F, this.itemProject)
               <= 0.0F;
      }

      int handle(float var1) {
         int var2 = 0;
         float var3 = Float.MAX_VALUE;

         for (int var4 = 0; var4 < WildMainMenuScreen.source; var4++) {
            float var5 = Math.abs(var1 - this.current[var4]);
            if (var5 < var3) {
               var3 = var5;
               var2 = var4;
            }
         }

         return var2;
      }
   }

   static final class ScreenState {
      float instance;
      float data;
      float context = -100.0F;
      float config;
   }

   public static final class SecondaryFileEntry {
      private float instance;
      private float data;
      private float context = 100.0F;
      private float config;

      public void handle(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }

      public float handle() {
         return this.instance;
      }

      public float process() {
         return this.data;
      }

      public float compute() {
         return this.context;
      }

      public float resolve() {
         return this.config;
      }
   }

   public static final class SecondaryNetworkState {
      float instance;
      float data;
      float context;
      float config;
      float state;
      float cache;
      float output;
      float current;
      float active = 0.5F;
      float mode = 0.5F;
      float selection;
      float enabled;
      float renderer;
      float handler;
      float animationDraw;
      float pointEncode;
      float animator;
      float source;

      public float handle() {
         return this.animator;
      }

      public float process() {
         return this.instance;
      }

      public float compute() {
         return this.data;
      }

      public float resolve() {
         return this.context;
      }

      public float update() {
         return this.config;
      }

      public float apply() {
         return this.state;
      }

      public float execute() {
         return this.cache;
      }

      public float prepare() {
         return this.output;
      }

      public float check() {
         return this.current;
      }

      public float onTick() {
         return this.active;
      }

      public float select() {
         return this.mode;
      }

      public float refresh() {
         return this.selection;
      }

      public float render() {
         return this.enabled;
      }

      public float tick() {
         return this.renderer;
      }

      public float drawAnimation() {
         return this.handler;
      }

      public float encodePoint() {
         return this.animationDraw;
      }

      public float animate() {
         return this.pointEncode;
      }

      public float load() {
         return this.source;
      }
   }
}
