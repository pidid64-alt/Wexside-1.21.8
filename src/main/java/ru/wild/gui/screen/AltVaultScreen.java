package ru.wild.gui.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.session.Session;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import ru.wild.WildClient;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.config.AltVaultStore;
import ru.wild.core.ClientSessionSwitcher;
import ru.wild.gui.theme.MotionSpringPresets;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.MainMenuBackgroundRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.security.AccountTierResolver;
import ru.wild.util.math.DampedOscillator;
import ru.wild.util.math.SmoothedValue;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class AltVaultScreen extends Screen implements FrameRenderListener {
   private static final ThemePaletteRegistry instance = ThemePaletteRegistry.handle();
   private static final int data = 14;
   private static final long context = 350L;
   private static final float config = 28.0F;
   private static final float state = 34.0F;
   private static final float cache = 10.0F;
   private static final float output = 8.5F;
   private static final float current = 44.0F;
   private static final float active = 21.0F;
   private static final float mode = 0.108F;
   private static final float selection = 27.0F;
   private static final float enabled = 30.0F;
   private static final float renderer = 9.0F;
   private static final float handler = 352.0F;
   private static final float animationDraw = 0.295F;
   private static final float pointEncode = 64.0F;
   private static final float animator = 8.0F;
   private static final float source = 14.0F;
   private static final float target = 40.0F;
   private static final float pending = 12.0F;
   private static final float previous = 27.0F;
   private static final float latest = 17.0F;
   private static final float summary = 5.0F;
   private static final float matrixBlend = 28.0F;
   private static final float vectorMatch = 6.0F;
   private static final float itemProject = 0.62F;
   private static final float responseCompute = 0.4922F;
   private static final String[] providerFetch = new String[]{"y", "M", "v"};
   private static final float[] profileDraw = new float[]{1.0F, 1.09F, 0.91F};
   private static final int vectorPerform = 3;
   private static final float eventAttach = 96.0F;
   private static final int serverRead = 7;
   private static final float positionAdvance = 3.5F;
   private static final float frameCheck = 46.0F;
   private static final float moduleCollect = 8.0F;
   private static final float providerClose = 8.0F;
   private static final float presetSave = 20.0F;
   private static final float windowConvert = 22.0F;
   private static final float presetWrite = 23.0F;
   private static final float colorMeasure = 24.0F;
   private static final float animationSchedule = 104.0F;
   private static final float rendererScan = 18.0F;
   private static final float sourceBuild = 62.0F;
   private static final float outputCollapse = 15.0F;
   private static final float profileInvoke = 15.0F;
   private static final float sourceSchedule = 22.0F;
   private static final float timerRender = 3.0F;
   private static final float scaleSave = 12.0F;
   private static final float colorCompute = 46.0F;
   private static final float scaleAdapt = 10.0F;
   private static final float textureRun = 244.0F;
   private static final float indexBind = 50.0F;
   private static final float actionRead = 76.0F;
   private static final float configCollapse = 158.0F;
   private static final float dataValidate = 184.0F;
   private static final float scaleRender = 198.0F;
   private static final float clientRefresh = 184.0F;
   private static final long keyFilter = 2600000000L;
   private static final long requestAdapt = 2600000000L;
   private static final long timerMeasure = 360000000L;
   private static final float vectorEncode = 0.85F;
   private static final ScheduledExecutorService requestReceive = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-AltVaultSave");
      var1.setDaemon(true);
      return var1;
   });
   private static final ExecutorService windowProcess = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-AltSkinLookup");
      var1.setDaemon(true);
      return var1;
   });
   private static final Map<String, Identifier> packetSave = new ConcurrentHashMap<>();
   private static final Set<String> entryAnimate = ConcurrentHashMap.newKeySet();
   private static volatile GameProfileRepository playerCollect;
   private static final String[] stateApply = new String[]{
      "x",
      "z",
      "q",
      "v",
      "mx",
      "im",
      "by",
      "not",
      "its",
      "real",
      "just",
      "i",
      "fx",
      "rx",
      "nx",
      "neo",
      "raw",
      "low",
      "old",
      "the",
      "mr",
      "lil",
      "big",
      "dr",
      "sir",
      "yo",
      "ez",
      "op",
      "gg",
      "yt",
      "tv",
      "wild",
      "pro",
      "uwu",
      "ya",
      "el",
      "an",
      "su",
      "ko"
   };
   private static final String[] matrixFilter = new String[]{
      "alex",
      "dani",
      "nik",
      "max",
      "roma",
      "kir",
      "drew",
      "mark",
      "luka",
      "tim",
      "ivan",
      "mira",
      "sasha",
      "art",
      "lev",
      "egor",
      "mike",
      "tony",
      "vlad",
      "step",
      "andrew",
      "niko",
      "den",
      "semy",
      "yar",
      "kost",
      "ilya",
      "gleb",
      "dima",
      "serg",
      "matvey",
      "rad",
      "kira",
      "mila",
      "sonya",
      "kai",
      "leo",
      "rian",
      "noah",
      "mason",
      "kevin",
      "rem",
      "zen",
      "nova",
      "pixel",
      "byte",
      "void",
      "ray",
      "fox",
      "wolf",
      "moon",
      "storm",
      "rain",
      "ash",
      "raven",
      "cole",
      "liam",
      "owen",
      "eric",
      "aron",
      "milo",
      "tomas",
      "nolan",
      "ron",
      "lars",
      "vega",
      "skye",
      "jack",
      "finn",
      "theo",
      "hugo",
      "bruno",
      "diego",
      "enzo",
      "jude",
      "reed",
      "cruz",
      "jax",
      "zane",
      "ace",
      "dash",
      "blake",
      "cody",
      "trey",
      "jett",
      "knox",
      "beck",
      "reid",
      "colt",
      "gage",
      "wade",
      "zeke",
      "onyx",
      "jinx",
      "flux",
      "ghost",
      "frost",
      "blaze",
      "drake",
      "hawk",
      "lynx",
      "puma",
      "arlo",
      "remy",
      "yuki",
      "aki",
      "ren",
      "sora",
      "haru",
      "kaze",
      "mei",
      "rio",
      "neon",
      "echo",
      "dusk",
      "sage",
      "wren"
   };
   private static final String[] layerSample = new String[]{
      "",
      "",
      "",
      "x",
      "yy",
      "on",
      "er",
      "ix",
      "is",
      "way",
      "pro",
      "mc",
      "dev",
      "boy",
      "top",
      "live",
      "sky",
      "craft",
      "mine",
      "play",
      "hd",
      "fps",
      "low",
      "new",
      "old",
      "go",
      "run",
      "win",
      "bit",
      "core",
      "qq",
      "zz",
      "xd",
      "yt",
      "gg",
      "ez",
      "op",
      "wow",
      "god",
      "main",
      "gang",
      "ster",
      "izz",
      "us",
      "io",
      "ly",
      "ne"
   };
   private static final String[] worldSend = new String[]{
      "ka",
      "ki",
      "ko",
      "mi",
      "mo",
      "ra",
      "ri",
      "ro",
      "sa",
      "si",
      "so",
      "ta",
      "ti",
      "to",
      "ne",
      "ni",
      "no",
      "la",
      "li",
      "lo",
      "ve",
      "vi",
      "vo",
      "za",
      "ze",
      "zu",
      "da",
      "de",
      "du",
      "ny",
      "re",
      "xo",
      "ku",
      "ke",
      "fa",
      "fi",
      "fo",
      "ga",
      "go",
      "ha",
      "hi",
      "ho",
      "ba",
      "bo",
      "pa",
      "po",
      "wu",
      "yo",
      "ju",
      "ce",
      "dra",
      "vex",
      "zar",
      "kra",
      "nyx",
      "rox"
   };
   private final Screen targetWrite;
   private final MainMenuBackgroundRenderer resultEncode = new MainMenuBackgroundRenderer();
   private final WildMainMenuScreen.PrimaryNetworkState messageParse = new WildMainMenuScreen.PrimaryNetworkState(20, 14);
   private final OpenGlStateSnapshot.NetworkState providerRead = new OpenGlStateSnapshot.NetworkState();
   private final List<AltVaultScreen.FileEntry> matrixBlend2 = new ArrayList<>();
   private final Set<String> scalePerform = new HashSet<>();
   private final Map<String, String> contextExpand = new HashMap<>();
   private final AltVaultScreen.PrimaryNetworkState keyProcess = new AltVaultScreen.PrimaryNetworkState(
      "Login", AltVaultScreen.Action.USE, AltVaultScreen.AccountType.PRIMARY
   );
   private final AltVaultScreen.PrimaryNetworkState actionConvert = new AltVaultScreen.PrimaryNetworkState(
      "Add", AltVaultScreen.Action.ADD_CRACKED, AltVaultScreen.AccountType.SECONDARY
   );
   private final AltVaultScreen.PrimaryNetworkState screenRead = new AltVaultScreen.PrimaryNetworkState(
      "Random", AltVaultScreen.Action.RANDOM, AltVaultScreen.AccountType.SECONDARY
   );
   private final AltVaultScreen.PrimaryNetworkState animationExpand = new AltVaultScreen.PrimaryNetworkState(
      "Edit", AltVaultScreen.Action.EDIT, AltVaultScreen.AccountType.SECONDARY
   );
   private final AltVaultScreen.PrimaryNetworkState playerRun = new AltVaultScreen.PrimaryNetworkState(
      "Delete", AltVaultScreen.Action.DELETE, AltVaultScreen.AccountType.DESTRUCTIVE
   );
   private final AltVaultScreen.PrimaryNetworkState matrixRender = new AltVaultScreen.PrimaryNetworkState(
      "Create identity", AltVaultScreen.Action.CREATE_FIRST, AltVaultScreen.AccountType.PRIMARY
   );
   private final List<AltVaultScreen.PrimaryNetworkState> moduleTick = List.of(
      this.keyProcess, this.actionConvert, this.screenRead, this.animationExpand, this.playerRun
   );
   private final AltVaultScreen.NetworkState playerCollapse = new AltVaultScreen.NetworkState("Username", false);
   private final AltVaultScreen.AnimationState optionAdvance = new AltVaultScreen.AnimationState();
   private final AltVaultScreen.SecondaryNetworkState[] effectScan = new AltVaultScreen.SecondaryNetworkState[14];
   private final SmoothedValue optionParse = new SmoothedValue(SpringAnimationSpec.update());
   private final SmoothedValue pointSubmit = new SmoothedValue(SpringAnimationSpec.update());
   private final DampedOscillator listenerPerform = new DampedOscillator(MotionSpringPresets.matrixBlend);
   private final DampedOscillator configMatch = new DampedOscillator(MotionSpringPresets.itemProject);
   private final DampedOscillator actionRender = new DampedOscillator(MotionSpringPresets.summary);
   private float playerApply;
   private float bufferAdapt;
   private float playerUpdate;
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
   private long layoutSave;
   private long blockRun;
   private long playerEvaluate;
   private long outputFetch;
   private long scaleParse;
   private long sessionEncode;
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
   private boolean messageParse2;
   private boolean shaderProject;
   private boolean inputInvoke;
   private int optionFetch;
   private int eventCollapse;
   private int stateAttach = -6357021;
   private int worldEvaluate = -11341636;
   private ThemePalette playerProject = ThemePalette.AURORA;
   private boolean playerMatch;
   private int cacheClose = -1;
   private int scaleSetup = 5;
   private String indexSynchronize = "";
   private String taskInterpolate = null;
   private String sourceRefresh = null;
   private float playerSave;
   private float requestRun;
   private float frameProject;
   private boolean dataRelease;
   private float vectorRun;
   private float providerSynchronize;
   private float pathProcess;
   private float positionReset;
   private float effectApply;
   private float cacheHandle2;
   private float sessionAdvance;
   private float keySample;
   private boolean playerPerform;
   private int taskLoad = -1;
   private int pointSend = -1;
   private float clientSubmit;
   private float pointSample;
   private float regionRefresh;
   private volatile ScheduledFuture<?> pathCheck;

   public AltVaultScreen(Screen var1) {
      super(Text.literal("Alt Manager"));
      this.targetWrite = var1;

      for (int var2 = 0; var2 < this.effectScan.length; var2++) {
         this.effectScan[var2] = new AltVaultScreen.SecondaryNetworkState();
      }
   }

   public static void handle(MinecraftClient var0) {
      AltVaultScreen.CacheEntry.handle(var0);
   }

   protected void init() {
      super.init();
      boolean var1 = this.layoutSave != 0L;
      this.layoutSave = System.nanoTime();
      this.blockRun = this.layoutSave;
      this.playerEvaluate = this.layoutSave;
      this.elementTick = 0.0F;
      this.messageParse2 = false;
      this.shaderProject = false;
      this.inputInvoke = false;
      this.optionFetch = 0;
      this.eventCollapse = 0;
      this.playerSave = 0.0F;
      this.requestRun = 0.0F;
      this.frameProject = 0.0F;
      this.taskLoad = -1;
      this.pointSend = -1;
      this.sourceRefresh = null;
      this.sessionEncode = 0L;
      this.listenerPerform.handle(0.0F);
      this.configMatch.handle(0.0F);
      this.actionRender.handle(0.0F);
      this.optionParse.handle(0.0F);
      this.pointSubmit.handle(0.0F);
      this.playerCollapse.resolve();
      this.optionAdvance.handle();

      for (AltVaultScreen.PrimaryNetworkState var3 : this.moduleTick) {
         var3.handle();
      }

      this.matrixRender.handle();
      this.handle(var1);
      this.onTick();
   }

   public void resize(MinecraftClient var1, int var2, int var3) {
      int var4 = this.cacheClose;
      float var5 = this.playerSave;
      String var6 = this.indexSynchronize;
      long var7 = this.scaleParse;
      super.resize(var1, var2, var3);
      this.cacheClose = var4;
      this.playerSave = var5;
      this.requestRun = var5;
      this.indexSynchronize = var6;
      this.scaleParse = var7;
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      this.handle(var2, var3, var4, false);
   }

   @Override
   public void handle(int var1, int var2, float var3) {
      this.handle(var1, var2, var3, true);
   }

   private void handle(int var1, int var2, float var3, boolean var4) {
      Window var5 = this.client == null ? null : this.client.getWindow();
      if (var5 != null && !var5.hasZeroWidthOrHeight() && var5.getFramebufferWidth() > 0 && var5.getFramebufferHeight() > 0) {
         int var6 = var5.getFramebufferWidth();
         int var7 = var5.getFramebufferHeight();
         long var8 = System.nanoTime();
         float var10 = Math.max(0.001F, Math.min(0.05F, (float)(var8 - this.blockRun) / 1.0E9F));
         this.blockRun = var8;
         this.elementTick = (float)(var8 - this.layoutSave) / 1.0E9F;
         if (this.handle(var5, var6, var7, var1, var2, var8)) {
            var10 = 0.001F;
         }

         this.fetchProvider();
         this.handle(var5, var1, var2, var10, var8);
         this.process(var6, var7, var10);
         this.drawProfile();
         this.process(var8);
         float var11 = (this.regionAlign / Math.max(1.0F, var6) - 0.5F) * 2.0F;
         float var12 = (this.resourceClamp / Math.max(1.0F, var7) - 0.5F) * 2.0F;
         float var13 = this.optionParse.handle(var11, var10);
         float var14 = this.pointSubmit.handle(var12, var10);
         this.handle(var6, var7, var13, var14, var10, var8);
         this.computeResponse();
         int var15 = GL11.glGetInteger(36006);
         this.handle(var6, var7, var15, var13, var14, var8);
         if (var4) {
            OpenGlStateSnapshot.process(this.providerRead);

            try {
               this.resultEncode.handle(this.messageParse);
            } finally {
               OpenGlStateSnapshot.compute(this.providerRead);
            }

            this.handle(this.messageParse);
         }
      }
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void close() {
      this.handle(AltVaultScreen.Action.BACK);
   }

   public void removed() {
      if (WildClient.performVector()) {
         this.handle(0L);
      } else {
         this.update();
      }

      AltVaultScreen.CacheEntry.handle();
      this.resultEncode.close();
      super.removed();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 && this.client != null && this.client.getWindow() != null) {
         float var6 = this.handle(this.client.getWindow(), var1);
         float var7 = this.process(this.client.getWindow(), var3);
         long var8 = System.nanoTime();
         if (this.playerPerform) {
            float var10 = 10.0F;
            if (var6 >= this.providerSynchronize - var10
               && var6 <= this.providerSynchronize + this.positionReset + var10
               && var7 >= this.pathProcess
               && var7 <= this.pathProcess + this.effectApply) {
               this.dataRelease = true;
               this.frameProject = 0.85F;
               if (var7 >= this.cacheHandle2 && var7 <= this.cacheHandle2 + this.sessionAdvance) {
                  this.vectorRun = var7 - this.cacheHandle2;
               } else {
                  this.vectorRun = this.sessionAdvance * 0.5F;
               }

               this.handle(var7);
               return true;
            }
         }

         if (this.optionAdvance.handle(var6, var7)) {
            this.optionAdvance.selection = 1.0F;
            this.handle(AltVaultScreen.Action.BACK);
            return true;
         }

         for (int var13 = 0; var13 < this.matrixBlend2.size(); var13++) {
            AltVaultScreen.FileEntry var11 = this.matrixBlend2.get(var13);
            if (!var11.indexBind && !var11.textureRun && var11.actionRead) {
               int var12 = this.handle(var11, var6, var7);
               if (var12 >= 0) {
                  this.playerCollapse.summary = false;
                  var11.renderer[var12] = 1.0F;
                  this.handle(var13, null);
                  this.handle(var11, var12);
                  return true;
               }

               if (var11.handle(var6, var7)) {
                  this.playerCollapse.summary = false;
                  this.animate();
                  if (this.cacheClose == var13 && var8 - this.outputFetch < 360000000L) {
                     var11.vectorPerform = 1.0F;
                     var11.eventAttach = 1.0F;
                     this.handle(AltVaultScreen.Action.USE);
                  } else {
                     this.handle(var13, "Selected " + var11.data);
                     var11.eventAttach = Math.max(var11.eventAttach, 0.42F);
                     this.compute();
                  }

                  this.outputFetch = var8;
                  if (!var11.config && this.collectModule() > 1) {
                     this.pointSend = var13;
                     this.clientSubmit = var7 - var11.previous;
                     this.regionRefresh = var7;
                     this.pointSample = var7;
                  }

                  return true;
               }
            }
         }

         if (this.playerCollapse.handle(var6, var7)) {
            this.playerCollapse.summary = true;
            this.playerCollapse.matrixBlend = false;
            this.playerCollapse.latest = this.playerCollapse.previous.length();
            this.playerCollapse.selection = 1.0F;
            this.animate();
            return true;
         }

         if (this.actionRender.handle() > 0.5F && this.matrixRender.handle(var6, var7)) {
            this.matrixRender.selection = 1.0F;
            this.matrixRender.enabled = 1.0F;
            this.handle(AltVaultScreen.Action.CREATE_FIRST);
            return true;
         }

         for (AltVaultScreen.PrimaryNetworkState var15 : this.moduleTick) {
            if (var15.previous && var15.handle(var6, var7)) {
               this.playerCollapse.summary = false;
               var15.selection = 1.0F;
               var15.enabled = 1.0F;
               this.handle(var15.target);
               return true;
            }
         }

         this.playerCollapse.summary = false;
         this.animate();
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.collectModule() <= this.scaleSetup) {
         return true;
      }

      this.playerSave -= (float)var7;
      int var9 = Math.max(0, this.collectModule() - Math.max(1, this.scaleSetup));
      this.playerSave = compute(this.playerSave, 0.0F, var9);
      this.frameProject = 0.85F;
      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.client != null && this.client.getWindow() != null) {
         float var10 = this.process(this.client.getWindow(), var3);
         if (this.dataRelease && this.playerPerform) {
            this.handle(var10);
            this.frameProject = 0.85F;
            return true;
         }

         if (this.taskLoad >= 0) {
            this.pointSample = var10;
            return true;
         }

         if (this.pointSend >= 0) {
            this.pointSample = var10;
            if (Math.abs(var10 - this.regionRefresh) > 3.5F * process(this.optionFetch, this.eventCollapse)) {
               if (this.pointSend < this.matrixBlend2.size()) {
                  this.taskLoad = this.pointSend;
                  this.matrixBlend2.get(this.taskLoad).presetWrite = 1.0F;
               }

               this.pointSend = -1;
            }

            return true;
         } else {
            return super.mouseDragged(var1, var3, var5, var6, var8);
         }
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0) {
         this.pointSend = -1;
         if (this.dataRelease) {
            this.dataRelease = false;
            return true;
         }

         if (this.taskLoad >= 0) {
            this.process();
            return true;
         }
      }

      return super.mouseReleased(var1, var3, var5);
   }

   private void handle(float var1) {
      float var2 = this.effectApply - this.sessionAdvance;
      if (!(var2 <= 0.001F)) {
         float var3 = compute(var1 - this.vectorRun, this.pathProcess, this.pathProcess + var2);
         float var4 = (var3 - this.pathProcess) / var2;
         int var5 = Math.max(0, this.collectModule() - Math.max(1, this.scaleSetup));
         this.playerSave = var4 * var5;
      }
   }

   private void process() {
      int var1 = this.taskLoad;
      this.taskLoad = -1;
      if (var1 >= 0 && var1 < this.matrixBlend2.size()) {
         AltVaultScreen.FileEntry var2 = this.matrixBlend2.get(var1);
         var2.presetWrite = 0.0F;
         int var3 = this.handle(var2);
         if (var3 != var1) {
            this.matrixBlend2.remove(var1);
            this.matrixBlend2.add(handle(var3, 0, this.matrixBlend2.size()), var2);
            this.cacheClose = this.process(var2.instance);
            this.compute("Reordered " + var2.data);
            this.execute();
         }
      }
   }

   private int handle(float var1, float var2, float var3) {
      float var4 = compute(this.pointSample - this.clientSubmit, this.bufferAdapt, this.bufferAdapt + var3 - var1);
      return Math.round((var4 - this.bufferAdapt) / Math.max(var1 + var2, 1.0F) + this.requestRun);
   }

   private int handle(AltVaultScreen.FileEntry var1) {
      float var2 = Math.max(var1.vectorMatch, 1.0F);
      int var3 = this.handle(var2, var1.responseCompute, this.packetRead);
      int var4 = 0;

      for (int var5 = 0; var5 < this.matrixBlend2.size(); var5++) {
         AltVaultScreen.FileEntry var6 = this.matrixBlend2.get(var5);
         if (var6 != var1 && !var6.indexBind && !var6.textureRun) {
            if (var4 == var3) {
               return var5;
            }

            var4++;
         }
      }

      return this.matrixBlend2.size() - 1;
   }

   public boolean charTyped(char var1, int var2) {
      if (!this.playerCollapse.summary) {
         return super.charTyped(var1, var2);
      }

      if (var1 >= 'A' && var1 <= 'Z' || var1 >= 'a' && var1 <= 'z' || var1 >= '0' && var1 <= '9' || var1 == '_') {
         this.playerCollapse.handle(var1);
      }

      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         if (this.sourceRefresh != null) {
            this.animate();
            this.compute("Delete cancelled");
            return true;
         } else if (this.load()) {
            this.fetch();
            return true;
         } else if (this.playerCollapse.summary) {
            this.playerCollapse.summary = false;
            this.playerCollapse.matrixBlend = false;
            return true;
         } else {
            this.handle(AltVaultScreen.Action.BACK);
            return true;
         }
      } else {
         if (this.playerCollapse.summary) {
            boolean var4 = (var3 & 2) != 0 || (var3 & 8) != 0;
            if (var4) {
               if (var1 == 67) {
                  if (this.client != null && this.client.keyboard != null && !this.playerCollapse.previous.isEmpty()) {
                     this.client.keyboard.setClipboard(this.playerCollapse.previous);
                  }

                  return true;
               }

               if (var1 == 86) {
                  if (this.client != null && this.client.keyboard != null) {
                     String var5 = this.client.keyboard.getClipboard();
                     if (var5 != null) {
                        this.playerCollapse.handle(var5.replaceAll("[^A-Za-z0-9_]", ""));
                     }
                  }

                  return true;
               }

               if (var1 == 65) {
                  this.playerCollapse.matrixBlend = true;
                  return true;
               }
            }

            if (var1 == 259) {
               this.playerCollapse.process();
               return true;
            }

            if (var1 == 261) {
               this.playerCollapse.compute();
               return true;
            }

            if (var1 == 263) {
               this.playerCollapse.matrixBlend = false;
               this.playerCollapse.latest = handle(this.playerCollapse.latest - 1, 0, this.playerCollapse.previous.length());
               return true;
            }

            if (var1 == 262) {
               this.playerCollapse.matrixBlend = false;
               this.playerCollapse.latest = handle(this.playerCollapse.latest + 1, 0, this.playerCollapse.previous.length());
               return true;
            }

            if (var1 == 257 || var1 == 335) {
               this.handle(AltVaultScreen.Action.ADD_CRACKED);
               return true;
            }
         }

         boolean var6 = (var3 & 2) != 0 || (var3 & 8) != 0;
         if (var6 && var1 == 67) {
            this.blendMatrix();
            return true;
         } else if (var6 && var1 == 83) {
            this.matchVector();
            return true;
         } else if (var6 && var1 == 71) {
            this.handle(5);
            return true;
         } else if (var1 == 257 || var1 == 335) {
            this.handle(AltVaultScreen.Action.USE);
            return true;
         } else if (var1 == 261) {
            this.handle(AltVaultScreen.Action.DELETE);
            return true;
         } else if (var1 == 264) {
            this.process(1);
            return true;
         } else if (var1 == 265) {
            this.process(-1);
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      }
   }

   private void handle(boolean var1) {
      this.matrixBlend2.clear();
      this.contextExpand.clear();
      File var2 = this.check();
      boolean var3 = var2.exists() && AltVaultStore.handle(var2).isEmpty();

      for (AltVaultStore.NamedEntry var5 : AltVaultStore.handle(var2)) {
         this.contextExpand.put(var5.id(), var5.password());
         this.matrixBlend2
            .add(
               new AltVaultScreen.FileEntry(
                  var5.name(), AltVaultScreen.PrimaryAccountType.handle(var5.type()), false, this.elementTick, var5.id(), var5.createdAt(), var5.lastUsedAt()
               )
            );
      }

      MinecraftClient var7 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var7 != null && var7.getSession() != null) {
         ClientSessionSwitcher.handle(var7);
         ClientSessionSwitcher.process(var7)
            .filter(var1x -> !var1x.getUsername().equalsIgnoreCase(var7.getSession().getUsername()))
            .ifPresent(var1x -> this.handle(var1x, true));
         this.handle(var7.getSession(), true);
      }

      for (int var8 = 0; var8 < this.matrixBlend2.size(); var8++) {
         AltVaultScreen.FileEntry var6 = this.matrixBlend2.get(var8);
         var6.target = var1 ? -1.0F : this.elementTick + 0.14F + var8 * 0.055F;
         var6.current.handle(var1 ? 1.0F : 0.0F);
         var6.presetSave = var1 ? 1.0F : 0.0F;
      }

      if (var3) {
         this.resolve();
      }
   }

   private void compute() {
      this.handle(350L);
   }

   private void resolve() {
      this.handle(0L);
   }
   private void update() {
      ScheduledFuture var1 = this.pathCheck;
      if (var1 != null) {
         var1.cancel(false);
         this.pathCheck = null;
      }

      boolean var6 = false /* VF: Semaphore variable */;

      label44: {
         try {
            var6 = true;
            this.pathCheck = requestReceive.schedule((Runnable)this::apply, 0L, TimeUnit.MILLISECONDS);
            this.pathCheck.get(10L, TimeUnit.SECONDS);
            var6 = false;
            break label44;
         } catch (Throwable var7) {
            var6 = false;
         } finally {
            if (var6) {
               this.pathCheck = null;
            }
         }

         this.pathCheck = null;
         return;
      }

      this.pathCheck = null;
   }

   private void handle(long var1) {
      ScheduledFuture var3 = this.pathCheck;
      if (var3 != null) {
         var3.cancel(false);
      }

      this.pathCheck = requestReceive.schedule((Runnable)this::apply, var1, TimeUnit.MILLISECONDS);
   }

   private void apply() {
      File var1 = this.check();
      ArrayList var2;
      String var3;
      synchronized (this.matrixBlend2) {
         var2 = new ArrayList();

         for (AltVaultScreen.FileEntry var6 : this.matrixBlend2) {
            if (!var6.indexBind && !var6.textureRun && !var6.config) {
               var2.add(
                  new AltVaultStore.NamedEntry(
                     var6.instance, var6.data, var6.context.name(), this.contextExpand.getOrDefault(var6.instance, ""), var6.handler, var6.animationDraw
                  )
               );
            }
         }

         var3 = this.prepare();
      }

      AltVaultStore.handle(var1, var2, var3);
   }

   private void execute() {
      this.resolve();
   }

   private String prepare() {
      AltVaultScreen.FileEntry var1 = this.projectItem();
      return var1 != null && !var1.config ? var1.instance : AltVaultStore.process(this.check());
   }

   private File check() {
      File var1 = WildClient.instance != null && WildClient.instance.cache != null ? WildClient.instance.cache : WildClient.process();
      return new File(var1, "accounts.json");
   }

   private void onTick() {
      MinecraftClient var1 = this.client == null ? MinecraftClient.getInstance() : this.client;
      String var2 = var1 != null && var1.getSession() != null ? var1.getSession().getUsername() : "";
      String var3 = AltVaultStore.process(this.check());
      int var4 = this.process(var3);
      if (var4 >= 0) {
         this.handle(var4, null);
      } else {
         int var5 = this.handle(var2);
         if (var5 >= 0) {
            this.handle(var5, null);
         } else if (!this.matrixBlend2.isEmpty()) {
            this.handle(0, null);
         } else {
            this.cacheClose = -1;
         }
      }
   }

   private int handle(String var1) {
      if (var1 != null && !var1.isBlank()) {
         MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
         boolean var3 = var2 != null
            && var2.getSession() != null
            && var2.getSession().getAccountType() != net.minecraft.client.session.Session.AccountType.LEGACY;
         AltVaultScreen.PrimaryAccountType var4 = var3 ? AltVaultScreen.PrimaryAccountType.PREMIUM : AltVaultScreen.PrimaryAccountType.CRACKED;

         for (int var5 = 0; var5 < this.matrixBlend2.size(); var5++) {
            AltVaultScreen.FileEntry var6 = this.matrixBlend2.get(var5);
            if (!var6.indexBind && !var6.textureRun && var6.context == var4 && var6.data.equals(var1)) {
               return var5;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         for (int var2 = 0; var2 < this.matrixBlend2.size(); var2++) {
            AltVaultScreen.FileEntry var3 = this.matrixBlend2.get(var2);
            if (!var3.indexBind && !var3.textureRun && var1.equals(var3.instance)) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int handle(String var1, AltVaultScreen.PrimaryAccountType var2) {
      if (var1 != null && !var1.isBlank()) {
         for (int var3 = 0; var3 < this.matrixBlend2.size(); var3++) {
            AltVaultScreen.FileEntry var4 = this.matrixBlend2.get(var3);
            if (!var4.indexBind && !var4.textureRun && var4.context == var2 && var4.data.equals(var1)) {
               return var3;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void handle(int var1, String var2) {
      if (var1 >= 0 && var1 < this.matrixBlend2.size()) {
         if (this.cacheClose != var1) {
            AltVaultScreen.FileEntry var3 = this.matrixBlend2.get(var1);
            var3.colorMeasure = 0.0F;
            var3.clientRefresh = true;
            this.animate();
         }

         this.cacheClose = var1;
         if (var2 != null) {
            this.compute(var2);
         }

         this.checkFrame();
      } else {
         this.cacheClose = -1;
      }
   }

   private void compute(String var1) {
      this.indexSynchronize = var1 == null ? "" : var1;
      this.scaleParse = this.indexSynchronize.isEmpty() ? 0L : System.nanoTime() + 2600000000L;
   }

   private void select() {
      if (this.load()) {
         this.measure();
      } else {
         String var1 = check(this.playerCollapse.previous);
         if (var1.isBlank()) {
            this.compute("Enter a username first");
            this.playerCollapse.enabled = 1.0F;
            this.playerCollapse.summary = true;
         } else {
            int var2 = this.handle(var1, AltVaultScreen.PrimaryAccountType.CRACKED);
            if (var2 >= 0) {
               this.handle(var2, "Identity already exists");
               AltVaultScreen.FileEntry var6 = this.matrixBlend2.get(var2);
               var6.eventAttach = 1.0F;
               this.compute();
            } else {
               long var3 = System.currentTimeMillis();
               AltVaultScreen.FileEntry var5 = new AltVaultScreen.FileEntry(
                  var1, AltVaultScreen.PrimaryAccountType.CRACKED, false, this.elementTick, process(var1, AltVaultScreen.PrimaryAccountType.CRACKED), var3, 0L
               );
               var5.eventAttach = 1.0F;
               this.matrixBlend2.add(var5);
               this.scalePerform.add(var1.toLowerCase(Locale.ROOT));
               this.handle(this.matrixBlend2.size() - 1, "Added " + var1);
               this.playerCollapse.compute();
               this.execute();
            }
         }
      }
   }

   private void refresh() {
      Set var1 = this.render2();

      for (int var2 = 0; var2 < 256; var2++) {
         String var3 = generatePassword();
         if (!var3.isBlank() && !var1.contains(var3.toLowerCase(Locale.ROOT))) {
            this.resolve(var3);
            return;
         }
      }

      String var4 = handle(var1);
      if (!var4.isBlank()) {
         this.resolve(var4);
      } else {
         this.compute("Generated identity collision");
         this.playerCollapse.enabled = 1.0F;
      }
   }

   private Set<String> render2() {
      HashSet var1 = new HashSet<>(this.scalePerform);

      for (AltVaultScreen.FileEntry var3 : this.matrixBlend2) {
         if (!var3.indexBind && !var3.textureRun) {
            var1.add(var3.data.toLowerCase(Locale.ROOT));
         }
      }

      String var4 = check(this.playerCollapse.previous);
      if (!var4.isBlank()) {
         var1.add(var4.toLowerCase(Locale.ROOT));
      }

      return var1;
   }

   private void resolve(String var1) {
      this.scalePerform.add(var1.toLowerCase(Locale.ROOT));
      this.playerCollapse.compute();
      this.playerCollapse.handle(var1);
      this.playerCollapse.summary = true;
      this.playerCollapse.matrixBlend = true;
      this.playerCollapse.enabled = 1.0F;
      this.compute("Rolled " + var1);
   }

   private static String generatePassword() {
      ThreadLocalRandom var0 = ThreadLocalRandom.current();

      for (int var1 = 0; var1 < 28; var1++) {
         String var2 = matrixFilter[var0.nextInt(matrixFilter.length)];
         String var3 = matrixFilter[var0.nextInt(matrixFilter.length)];
         String var4 = stateApply[var0.nextInt(stateApply.length)];
         String var5 = layerSample[var0.nextInt(layerSample.length)];
         String var6 = handle(var0, var0.nextInt(2, 5));
         String var7 = var2.substring(0, Math.min(var2.length(), var0.nextInt(2, Math.min(4, var2.length()) + 1)));
         String var8 = var3.substring(0, Math.min(var3.length(), var0.nextInt(2, Math.min(4, var3.length()) + 1)));
         String var9 = var0.nextInt(100) < 18 ? "_" : "";
         String var10 = process(var0);
         String var11 = var0.nextInt(100) < 40 ? var10 : "";
         String var12 = var0.nextInt(100) < 45 ? var5 : "";

         String var13 = switch (var0.nextInt(20)) {
            case 0 -> prepare(var2) + prepare(var3);
            case 1 -> var4 + prepare(var2);
            case 2 -> prepare(var2) + var5;
            case 3 -> var2 + var9 + var10;
            case 4 -> prepare(var7) + prepare(var3);
            case 5 -> var2 + prepare(var8);
            case 6 -> prepare(var6) + (var0.nextInt(100) < 28 ? var5 : "");
            case 7 -> var6 + var9 + var10;
            case 8 -> prepare(var2) + prepare(var8) + var11;
            case 9 -> var4 + var9 + var6;
            case 10 -> var7 + prepare(handle(var0, var0.nextInt(1, 3))) + var5;
            case 11 -> update(var2) + var12;
            case 12 -> prepare(var2) + "_" + prepare(var3);
            case 13 -> apply(var2) + prepare(var3);
            case 14 -> "xX" + prepare(var2) + "Xx";
            case 15 -> execute(var2) + var11;
            case 16 -> prepare(var4) + prepare(var2) + var5;
            case 17 -> prepare(var2) + update(var8);
            case 18 -> prepare(var2) + handle(var0);
            default -> prepare(var2) + var12 + (var0.nextInt(100) < 36 ? var10 : "");
         };
         var13 = check(var13);
         if (var13.length() >= 3 && var13.length() <= 16) {
            return var13;
         }
      }

      return handle(Set.of());
   }

   private static String update(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         StringBuilder var1 = new StringBuilder(var0.length());

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = Character.toLowerCase(var0.charAt(var2));

            var1.append(switch (var3) {
               case 'a' -> '4';
               default -> var0.charAt(var2);
               case 'e' -> '3';
               case 'i' -> '1';
               case 'o' -> '0';
               case 's' -> '5';
               case 't' -> '7';
            });
         }

         return var1.toString();
      } else {
         return "";
      }
   }

   private static String apply(String var0) {
      if (var0 != null && var0.length() >= 4) {
         StringBuilder var1 = new StringBuilder(var0.length());

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            boolean var4 = "aeiouAEIOU".indexOf(var3) >= 0;
            if (!var4 || var2 == 0) {
               var1.append(var3);
            }
         }

         return var1.length() < 2 ? var0 : var1.toString();
      } else {
         return var0 == null ? "" : var0;
      }
   }

   private static String execute(String var0) {
      return var0 != null && !var0.isEmpty() ? var0 + var0.charAt(var0.length() - 1) : "";
   }

   private static String handle(ThreadLocalRandom var0) {
      int var1 = var0.nextInt(100);
      return var1 < 10 ? "0" + var1 : String.valueOf(var1);
   }

   private static String handle(ThreadLocalRandom var0, int var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1; var3++) {
         var2.append(worldSend[var0.nextInt(worldSend.length)]);
      }

      return var2.toString();
   }

   private static String process(ThreadLocalRandom var0) {
      return switch (var0.nextInt(4)) {
         case 0 -> String.valueOf(var0.nextInt(7, 99));
         case 1 -> String.valueOf(var0.nextInt(100, 999));
         case 2 -> String.valueOf(var0.nextInt(1000, 9999));
         default -> String.valueOf(var0.nextInt(10, 9999));
      };
   }

   private static String handle(Set<String> var0) {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();

      for (int var2 = 0; var2 < 64; var2++) {
         String var3 = Long.toUnsignedString(var1.nextLong(), 36);
         if (var3.length() > 8) {
            var3 = var3.substring(0, 8);
         }

         String var4 = check("Wild" + var3);
         if (!var4.isBlank() && !var0.contains(var4.toLowerCase(Locale.ROOT))) {
            return var4;
         }
      }

      return "";
   }

   private static String prepare(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         return var0.length() == 1 ? var0.toUpperCase(Locale.ROOT) : var0.substring(0, 1).toUpperCase(Locale.ROOT) + var0.substring(1);
      } else {
         return "";
      }
   }

   private void drawAnimation() {
      AltVaultScreen.FileEntry var1 = this.projectItem();
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var1 != null && var2 != null) {
         this.process(var1);
      } else {
         this.compute("Select an identity first");
      }
   }

   private void process(AltVaultScreen.FileEntry var1) {
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var1 != null && var2 != null) {
         ClientSessionSwitcher.handle(var2);
         boolean var3 = false;
         if (var1.context == AltVaultScreen.PrimaryAccountType.PREMIUM) {
            var3 = ClientSessionSwitcher.handle(var2, var1.data);
         }

         if (!var3) {
            ClientSessionSwitcher.process(var2, var1.data);
         }

         var1.animationDraw = System.currentTimeMillis();
         var1.eventAttach = 1.0F;
         var1.colorMeasure = 0.0F;
         var1.clientRefresh = true;
         AccountTierResolver.handle(var1.data, "");
         this.compute("Signed in as " + var1.data);
         this.execute();
      }
   }

   private void encodePoint() {
      AltVaultScreen.FileEntry var1 = this.projectItem();
      if (var1 == null) {
         this.compute("Select an identity first");
      } else {
         this.compute(var1);
      }
   }

   private void compute(AltVaultScreen.FileEntry var1) {
      if (var1 != null) {
         if (var1.instance.equals(this.sourceRefresh)) {
            this.resolve(var1);
         } else {
            this.sourceRefresh = var1.instance;
            this.sessionEncode = System.nanoTime() + 2600000000L;
            this.compute("Delete " + var1.data + "? Click again");
         }
      }
   }

   private void animate() {
      this.sourceRefresh = null;
      this.sessionEncode = 0L;
   }

   private void process(long var1) {
      if (this.sourceRefresh != null) {
         if (var1 >= this.sessionEncode || this.process(this.sourceRefresh) < 0) {
            this.animate();
         }
      }
   }

   private float compute(long var1) {
      return this.sourceRefresh != null && this.sessionEncode > 0L ? compute((float)(this.sessionEncode - var1) / 2.6E9F, 0.0F, 1.0F) : 0.0F;
   }

   private void resolve(AltVaultScreen.FileEntry var1) {
      int var2 = this.process(var1.instance);
      if (var2 < 0) {
         this.animate();
      } else {
         var1.textureRun = true;
         var1.windowConvert = this.elementTick;
         var1.eventAttach = 1.0F;
         this.animate();
         this.compute("Removed " + var1.data);
         this.cacheClose = this.matrixBlend2.size() <= 1 ? -1 : (var2 >= this.matrixBlend2.size() - 1 ? var2 - 1 : var2 + 1);
         this.checkFrame();
         this.execute();
      }
   }

   private boolean load() {
      return this.taskInterpolate != null && this.process(this.taskInterpolate) >= 0;
   }

   private boolean save() {
      if (this.load()) {
         return true;
      }

      AltVaultScreen.FileEntry var1 = this.projectItem();
      return var1 != null && !var1.config;
   }

   private void submit() {
      if (this.load()) {
         this.fetch();
      } else {
         this.unload();
      }
   }

   private void unload() {
      AltVaultScreen.FileEntry var1 = this.projectItem();
      this.update(var1);
   }

   private void update(AltVaultScreen.FileEntry var1) {
      if (var1 != null && !var1.config) {
         this.taskInterpolate = var1.instance;
         this.playerCollapse.compute();
         this.playerCollapse.handle(var1.data);
         this.playerCollapse.summary = true;
         this.playerCollapse.matrixBlend = true;
         this.playerCollapse.enabled = 1.0F;
         this.compute("Renaming " + var1.data);
      } else {
         this.compute("Pick a saved identity to rename");
         this.playerCollapse.enabled = 1.0F;
      }
   }

   private void fetch() {
      this.taskInterpolate = null;
      this.playerCollapse.compute();
      this.playerCollapse.summary = false;
      this.playerCollapse.matrixBlend = false;
      this.compute("Rename cancelled");
   }

   private void measure() {
      String var1 = check(this.playerCollapse.previous);
      if (var1.isBlank()) {
         this.compute("Enter a username first");
         this.playerCollapse.enabled = 1.0F;
      } else {
         int var2 = this.process(this.taskInterpolate);
         if (var2 < 0) {
            this.taskInterpolate = null;
            this.compute("Identity not found");
         } else {
            AltVaultScreen.FileEntry var3 = this.matrixBlend2.get(var2);
            int var4 = this.handle(var1, var3.context);
            if (var4 >= 0 && var4 != var2) {
               this.compute("Identity already exists");
               this.playerCollapse.enabled = 1.0F;
            } else if (var3.data.equals(var1)) {
               this.taskInterpolate = null;
               this.playerCollapse.compute();
               this.playerCollapse.summary = false;
               this.compute("Renamed identity");
            } else {
               String var5 = process(var1, var3.context);
               String var6 = this.contextExpand.getOrDefault(var3.instance, "");
               AltVaultScreen.FileEntry var7 = new AltVaultScreen.FileEntry(var1, var3.context, false, -1.0F, var5, var3.handler, var3.animationDraw);
               var7.presetSave = 1.0F;
               var7.current.handle(1.0F);
               var7.eventAttach = 1.0F;
               var7.output.handle(var3.output.handle());
               this.matrixBlend2.set(var2, var7);
               this.contextExpand.remove(var3.instance);
               this.contextExpand.put(var5, var6);
               this.scalePerform.add(var1.toLowerCase(Locale.ROOT));
               this.cacheClose = var2;
               this.taskInterpolate = null;
               this.playerCollapse.compute();
               this.playerCollapse.summary = false;
               this.compute("Renamed to " + var1);
               this.execute();
            }
         }
      }
   }

   private void blendMatrix() {
      AltVaultScreen.FileEntry var1 = this.projectItem();
      if (var1 != null && this.client != null && this.client.keyboard != null) {
         this.client.keyboard.setClipboard(var1.data);
         this.compute("Copied " + var1.data);
      } else {
         this.compute("Select an identity first");
      }
   }

   private void matchVector() {
      if (this.matrixBlend2.size() >= 2) {
         AltVaultScreen.FileEntry var1 = this.projectItem();
         String var2 = var1 == null ? null : var1.instance;
         this.matrixBlend2.sort((var0, var1x) -> Long.compare(var1x.animationDraw, var0.animationDraw));
         if (var2 != null) {
            int var3 = this.process(var2);
            if (var3 >= 0) {
               this.cacheClose = var3;
            }
         }

         this.compute("Sorted by last used");
         this.checkFrame();
         this.compute();
      }
   }

   private void handle(int var1) {
      int var2 = 0;
      long var3 = System.currentTimeMillis();

      for (int var5 = 0; var5 < var1; var5++) {
         Set var6 = this.render2();
         String var7 = generatePassword();
         if (var7.isBlank() || var6.contains(var7.toLowerCase(Locale.ROOT))) {
            var7 = handle(var6);
         }

         if (!var7.isBlank()) {
            AltVaultScreen.FileEntry var8 = new AltVaultScreen.FileEntry(
               var7, AltVaultScreen.PrimaryAccountType.CRACKED, false, this.elementTick, process(var7, AltVaultScreen.PrimaryAccountType.CRACKED), var3, 0L
            );
            var8.eventAttach = 1.0F;
            this.matrixBlend2.add(var8);
            this.scalePerform.add(var7.toLowerCase(Locale.ROOT));
            var2++;
         }
      }

      if (var2 > 0) {
         this.handle(this.matrixBlend2.size() - 1, "Added " + var2 + " identities");
         this.execute();
      } else {
         this.compute("Generation collision");
         this.playerCollapse.enabled = 1.0F;
      }
   }

   private AltVaultScreen.FileEntry projectItem() {
      if (this.cacheClose >= 0 && this.cacheClose < this.matrixBlend2.size()) {
         AltVaultScreen.FileEntry var1 = this.matrixBlend2.get(this.cacheClose);
         return !var1.indexBind && !var1.textureRun ? var1 : null;
      } else {
         return null;
      }
   }

   private void computeResponse() {
      boolean var1 = false;

      for (int var2 = this.matrixBlend2.size() - 1; var2 >= 0; var2--) {
         AltVaultScreen.FileEntry var3 = this.matrixBlend2.get(var2);
         if (var3.textureRun && this.elementTick - var3.windowConvert > 0.46F) {
            this.matrixBlend2.remove(var2);
            var1 = true;
            if (this.cacheClose >= var2) {
               this.cacheClose--;
            }
         }
      }

      if (var1) {
         this.cacheClose = this.matrixBlend2.isEmpty() ? -1 : handle(this.cacheClose, 0, this.matrixBlend2.size() - 1);
         this.checkFrame();
      }
   }

   private void handle(AltVaultScreen.FileEntry var1, int var2) {
      switch (var2) {
         case 0:
            this.process(var1);
            break;
         case 1:
            this.update(var1);
            break;
         default:
            this.compute(var1);
      }
   }

   private void handle(AltVaultScreen.Action var1) {
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      switch (var1) {
         case USE:
            this.drawAnimation();
            break;
         case ADD_CRACKED:
            this.select();
            break;
         case RANDOM:
            this.refresh();
            break;
         case EDIT:
            this.submit();
            break;
         case DELETE:
            this.encodePoint();
            break;
         case BACK:
            if (var2 != null) {
               var2.execute(() -> var2.setScreen(this.targetWrite));
            }
            break;
         case CREATE_FIRST:
            this.refresh();
            this.select();
      }
   }

   private void fetchProvider() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.AURORA;
      this.playerProject = var1;
      this.playerMatch = instance.compute(var1);
      this.stateAttach = instance.resolve(var1);
      this.worldEvaluate = instance.update(var1);
   }

   private void handle(Window var1, int var2, int var3, float var4, long var5) {
      float var7 = this.handle(var1, var2);
      float var8 = this.process(var1, var3);
      if (!this.messageParse2) {
         this.regionAlign = var7;
         this.resourceClamp = var8;
         this.handlerRun = 0.0F;
         this.keyCheck = 0.0F;
         this.messageParse2 = true;
      } else {
         float var9 = var7 - this.regionAlign;
         float var10 = var8 - this.resourceClamp;
         float var11 = compute(var9, var10);
         if (var11 > 0.2F) {
            this.handlerRun = compute(var9 / Math.max(1.0F, var1.getFramebufferWidth()) / var4, -3.0F, 3.0F);
            this.keyCheck = compute(var10 / Math.max(1.0F, var1.getFramebufferHeight()) / var4, -3.0F, 3.0F);
         } else {
            float var12 = (float)Math.pow(8.0E-4F, var4);
            this.handlerRun *= var12;
            this.keyCheck *= var12;
         }

         this.regionAlign = var7;
         this.resourceClamp = var8;
         if (var11 > 1.5F) {
            this.playerEvaluate = var5;
         }
      }
   }

   private void process(int var1, int var2, float var3) {
      if (!this.shaderProject) {
         this.layerProject = this.regionAlign;
         this.entityFilter = this.resourceClamp;
         this.layerSample2 = 0.0F;
         this.sourceCancel = 0.0F;
         this.shaderProject = true;
      } else {
         float var4 = this.layerProject;
         float var5 = this.entityFilter;
         float var6 = compute(this.regionAlign - this.layerProject, this.resourceClamp - this.entityFilter);
         float var7 = (1.0F - (float)Math.pow(1.8E-5F, var3)) * (0.58F + compute(var6 / 780.0F, 0.0F, 0.28F));
         this.layerProject = this.layerProject + (this.regionAlign - this.layerProject) * compute(var7, 0.028F, 0.16F);
         this.entityFilter = this.entityFilter + (this.resourceClamp - this.entityFilter) * compute(var7, 0.028F, 0.16F);
         float var8 = compute((this.layerProject - var4) / Math.max(1.0F, var1) / var3, -1.25F, 1.25F);
         float var9 = compute((this.entityFilter - var5) / Math.max(1.0F, var2) / var3, -1.25F, 1.25F);
         float var10 = 1.0F - (float)Math.pow(0.0045F, var3);
         this.layerSample2 = this.layerSample2 + (var8 - this.layerSample2) * var10;
         this.sourceCancel = this.sourceCancel + (var9 - this.sourceCancel) * var10;
      }
   }

   private void drawProfile() {
      if (!this.inputInvoke) {
         this.eventSend = this.layerProject;
         this.providerOffset = this.entityFilter;
         this.inputInvoke = true;
         this.process(this.layerProject, this.entityFilter, 0.18F);
      } else {
         float var1 = compute(this.layerProject - this.eventSend, this.entityFilter - this.providerOffset);
         if (var1 > 10.5F) {
            this.process(this.layerProject, this.entityFilter, compute(var1 / 280.0F, 0.06F, 0.3F));
            this.eventSend = this.layerProject;
            this.providerOffset = this.entityFilter;
         }
      }
   }

   private boolean handle(Window var1, int var2, int var3, int var4, int var5, long var6) {
      if (this.optionFetch == var2 && this.eventCollapse == var3) {
         return false;
      }

      this.optionFetch = var2;
      this.eventCollapse = var3;
      float var8 = compute(this.handle(var1, var4), 0.0F, var2);
      float var9 = compute(this.process(var1, var5), 0.0F, var3);
      this.regionAlign = this.layerProject = this.eventSend = var8;
      this.resourceClamp = this.entityFilter = this.providerOffset = var9;
      this.handlerRun = this.keyCheck = 0.0F;
      this.layerSample2 = this.sourceCancel = 0.0F;
      this.messageParse2 = true;
      this.shaderProject = true;
      this.inputInvoke = true;
      this.playerEvaluate = var6;
      this.dataRelease = false;
      this.taskLoad = -1;
      this.pointSend = -1;
      this.optionParse.handle(0.0F);
      this.pointSubmit.handle(0.0F);
      this.requestRun = this.playerSave;

      for (AltVaultScreen.FileEntry var11 : this.matrixBlend2) {
         var11.scaleRender = false;
      }

      this.performVector();
      this.process(var8, var9, 0.12F);
      this.checkFrame();
      return true;
   }

   private void performVector() {
      for (AltVaultScreen.SecondaryNetworkState var4 : this.effectScan) {
         var4.instance = 0.0F;
         var4.data = 0.0F;
         var4.context = -100.0F;
         var4.config = 0.0F;
      }
   }

   private void process(float var1, float var2, float var3) {
      int var4 = 0;
      float var5 = -1.0F;

      for (int var6 = 0; var6 < this.effectScan.length; var6++) {
         float var7 = this.elementTick - this.effectScan[var6].context;
         if (this.effectScan[var6].config <= 0.0F) {
            var4 = var6;
            break;
         }

         if (var7 > var5) {
            var5 = var7;
            var4 = var6;
         }
      }

      this.effectScan[var4].instance = var1;
      this.effectScan[var4].data = var2;
      this.effectScan[var4].context = this.elementTick;
      this.effectScan[var4].config = var3;
   }

   private void handle(int var1, int var2, float var3, float var4, float var5, long var6) {
      float var8 = process(var1, var2);
      float var9 = 28.0F * var8;
      float var10 = var3 * 1.15F * var8;
      float var11 = var4 * 0.7F * var8;
      this.screenSubmit = var2 * 0.108F + var4 * 0.45F * var8;
      this.cacheHandle = this.screenSubmit + 27.0F * var8;
      this.optionAdvance.cache = 34.0F * var8;
      this.optionAdvance.output = 34.0F * var8;
      this.optionAdvance.current = this.optionAdvance.cache * 0.5F;
      this.optionAdvance.data = var9;
      this.optionAdvance.context = var9;
      this.listenerRun = this.optionAdvance.data + this.optionAdvance.cache * 0.5F;
      this.indexLoad = this.optionAdvance.context + this.optionAdvance.output * 0.5F;
      this.optionAdvance.source = execute(compute((this.elementTick - 0.05F) / 0.66F, 0.0F, 1.0F));
      boolean var12 = this.optionAdvance.handle(this.regionAlign, this.resourceClamp);
      this.optionAdvance.active = this.optionAdvance.active + ((var12 ? 1.0F : 0.0F) - this.optionAdvance.active) * (1.0F - (float)Math.pow(1.0E-4F, var5));
      this.optionAdvance.selection = this.optionAdvance.selection + (0.0F - this.optionAdvance.selection) * (1.0F - (float)Math.pow(1.8E-5F, var5));
      int var13 = this.collectModule();
      boolean var14 = var13 == 0;
      this.actionRender.handle(var14 ? 1.0F : 0.0F, var5);
      float var15 = compute(352.0F * var8, 240.0F * var8, var1 - var9 * 4.0F);
      float var16 = 64.0F * var8;
      float var17 = 8.0F * var8;
      float var18 = 46.0F * var8;
      float var19 = var18 * 0.295F;
      float var20 = var18 * 2.0F + 8.0F * var8;
      float var21 = this.cacheHandle + 30.0F * var8;
      float var22 = var2 - var9;
      float var23 = var22 - var21 - var20 - 22.0F * var8;
      int var24 = Math.max(1, (int)Math.floor((var23 + var17) / (var16 + var17)));
      var24 = Math.min(var24, 7);
      this.scaleSetup = var14 ? 1 : Math.max(1, Math.min(var24, var13));
      float var25 = var14 ? 244.0F * var8 : this.scaleSetup * var16 + Math.max(0, this.scaleSetup - 1) * var17;
      int var26 = Math.max(0, var13 - Math.max(1, this.scaleSetup));
      this.playerSave = compute(this.playerSave, 0.0F, var26);
      float var27 = 1.0F - (float)Math.exp(-22.0F * var5);
      this.requestRun = this.requestRun + (this.playerSave - this.requestRun) * var27;
      if (Float.isNaN(this.requestRun)) {
         this.requestRun = this.playerSave;
      }

      this.frameProject = Math.max(0.0F, this.frameProject - var5);
      float var28 = var25 + 22.0F * var8 + var20;
      this.rendererCancel = compute((var2 - var28) * 0.465F, var21, Math.max(var21, var22 - var28)) + var11;
      this.eventReceive = this.rendererCancel + var28;
      float var29 = var1 * 0.5F + var10;
      this.playerApply = var29 - var15 * 0.5F;
      this.bufferAdapt = this.rendererCancel;
      this.playerUpdate = var15;
      this.packetRead = var25;
      this.playerPerform = var26 > 0;
      float var30 = !this.dataRelease && !(this.frameProject > 0.0F) ? (this.attachEvent() ? 0.62F : 0.0F) : 1.0F;
      float var31 = this.configMatch.handle(this.playerPerform ? var30 : 0.0F, var5);
      if (this.playerPerform) {
         this.positionReset = Math.max(2.5F, 3.0F * var8);
         this.providerSynchronize = this.playerApply + var15 + 12.0F * var8;
         this.pathProcess = this.bufferAdapt;
         this.effectApply = var25;
         float var32 = compute((float)this.scaleSetup / var13, 0.1F, 1.0F);
         this.sessionAdvance = Math.max(28.0F * var8, this.effectApply * var32);
         float var33 = this.effectApply - this.sessionAdvance;
         float var34 = var26 == 0 ? 0.0F : this.requestRun / var26;
         this.cacheHandle2 = this.pathProcess + var33 * var34;
      }

      this.keySample = var31;
      String var53 = this.readServer();
      int var54 = 0;
      int var55 = this.taskLoad >= 0 ? this.compute(this.taskLoad) : -1;

      for (int var35 = 0; var35 < this.matrixBlend2.size(); var35++) {
         AltVaultScreen.FileEntry var36 = this.matrixBlend2.get(var35);
         if (var36.indexBind) {
            var36.actionRead = false;
            var36.presetSave = 0.0F;
         } else {
            int var37 = var54;
            if (!var36.textureRun) {
               var54++;
            }

            float var38 = var37;
            if (this.taskLoad >= 0 && var35 != this.taskLoad) {
               int var39 = this.handle(var16, var17, var25);
               int var40 = var37 > var55 ? var37 - 1 : var37;
               var38 = var40 + (var40 >= var39 ? 1 : 0);
            }

            float var60 = var38 - this.requestRun;
            var36.actionRead = var60 > -1.02F && var60 < this.scaleSetup + 0.02F;
            float var62 = this.bufferAdapt + var60 * (var16 + var17);
            if (!var36.scaleRender) {
               var36.output.handle(var62);
               var36.scaleRender = true;
            }

            boolean var41 = var35 == this.taskLoad;
            float var42 = var41
               ? compute(this.pointSample - this.clientSubmit, this.bufferAdapt, this.bufferAdapt + var25 - var16)
               : var36.output.handle(var62, var5);
            if (var41) {
               var36.output.handle(var42);
               var36.actionRead = true;
            }

            var36.pending = this.playerApply;
            var36.previous = var42;
            var36.matrixBlend = var15;
            var36.vectorMatch = var16;
            var36.responseCompute = var17;
            var36.itemProject = var16 * 0.295F;
            var36.providerClose = 46.0F * var8;
            var36.colorCompute = var35 == this.cacheClose;
            var36.scaleAdapt = this.handle(var36, var53);
            var36.configCollapse = var36.instance.equals(this.sourceRefresh);
            var36.dataValidate = var41;
            this.handle(var36, var8);
            if (!var36.actionRead && !var36.textureRun) {
               var36.providerFetch = 0.0F;
               var36.profileDraw = var36.colorCompute ? 0.42F : 0.0F;
               var36.vectorPerform = 0.0F;
               var36.moduleCollect = 0.0F;
               var36.latest = var36.pending;
               var36.summary = var36.previous;
               var36.serverRead = 1.0F;

               for (int var43 = 0; var43 < 3; var43++) {
                  var36.enabled[var43] = 0.0F;
               }
            } else {
               this.handle(var36, var5, var8, var6);
            }

            float var67 = (Math.min(var36.previous + var16, this.bufferAdapt + var25) - Math.max(var36.previous, this.bufferAdapt)) / Math.max(var16, 1.0F);
            float var44 = var41 ? 1.0F : execute(compute((var67 - 0.34F) / 0.58F, 0.0F, 1.0F));
            var36.rendererScan = var44;
            float var45 = var36.target < 0.0F ? 1.0F : compute((this.elementTick - var36.target) / 0.1F, 0.0F, 1.0F);
            float var46 = var36.current.handle(var45, var5);
            float var47 = var36.textureRun ? execute(compute(1.0F - (this.elementTick - var36.windowConvert) / 0.42F, 0.0F, 1.0F)) : 1.0F;
            var36.presetSave = !var36.actionRead && !var36.textureRun ? 0.0F : compute(var46, 0.0F, 1.15F) * var44 * var47;
            if (var36.clientRefresh) {
               var36.colorMeasure += var5 / 0.52F;
               if (var36.colorMeasure >= 1.0F) {
                  var36.colorMeasure = 1.0F;
                  var36.clientRefresh = false;
               }
            }
         }
      }

      this.rangeRelease = this.bufferAdapt + var25 + 22.0F * var8;
      this.indexSave = this.rangeRelease + var18 + 8.0F * var8;
      this.actionConvert.instance = this.load() ? "Save" : "Add";
      this.animationExpand.instance = this.load() ? "Cancel" : "Edit";
      this.playerRun.instance = this.sourceRefresh != null ? "Confirm" : "Delete";
      float var56 = handle(23.0F, var8);
      float var57 = handle(24.0F, var8);
      float var58 = 18.0F * var8;
      float var59 = 8.0F * var8;
      float var61 = RoundedRectRenderer.handle(FontRegistry.instance, "Delete", var56).instance + var58 * 2.0F;
      float var63 = RoundedRectRenderer.handle(FontRegistry.config, "Confirm", var56).instance + var58 * 2.0F;
      float var64 = Math.max(62.0F * var8, Math.max(var61, var63));
      float var65 = 20.0F * var8;
      float var68 = var15 - var65 - var64;
      float var70 = (var68 - var59 * 2.0F) / 3.0F;
      if (var70 < 62.0F * var8) {
         var65 = var59;
         var68 = var15 - var65 - var64;
         var70 = (var68 - var59 * 2.0F) / 3.0F;
      }

      var70 = Math.max(var70, 22.0F * var8);
      float var72 = Math.max(104.0F * var8, RoundedRectRenderer.handle(FontRegistry.config, "Login", var57).instance + var58 * 2.4F);
      var72 = Math.min(var72, var15 * 0.42F);
      float var74 = var15 - var59 - var72;
      this.handle(
         this.playerCollapse,
         this.playerApply,
         this.rangeRelease,
         var74,
         var18,
         var19,
         var5,
         var8,
         1.0F,
         execute(compute((this.elementTick - 0.24F) / 0.74F, 0.0F, 1.0F))
      );
      this.playerCollapse.vectorMatch = this.playerCollapse.vectorMatch
         + ((this.playerCollapse.summary ? 1.0F : 0.0F) - this.playerCollapse.vectorMatch) * (1.0F - (float)Math.pow(1.0E-4F, var5));
      this.playerCollapse.providerFetch = this.playerCollapse
         .pending
         .handle(!this.playerCollapse.summary && this.playerCollapse.previous.isBlank() ? 0.0F : 1.0F, var5);
      this.keyProcess.previous = this.process(AltVaultScreen.Action.USE);
      this.handle(
         this.keyProcess,
         this.playerApply + var74 + var59,
         this.rangeRelease,
         var72,
         var18,
         var19,
         var5,
         var8,
         this.keyProcess.previous ? 1.0F : 0.3F,
         execute(compute((this.elementTick - 0.3F) / 0.74F, 0.0F, 1.0F))
      );
      float var75 = this.listenerPerform.handle(this.sourceRefresh != null ? 1.0F : 0.0F, var5);
      float var48 = Math.min(var64, var61 + (var63 - var61) * var75);
      AltVaultScreen.PrimaryNetworkState[] var49 = new AltVaultScreen.PrimaryNetworkState[]{this.actionConvert, this.screenRead, this.animationExpand};

      for (int var50 = 0; var50 < var49.length; var50++) {
         AltVaultScreen.PrimaryNetworkState var51 = var49[var50];
         var51.previous = this.process(var51.target);
         this.handle(
            var51,
            this.playerApply + var50 * (var70 + var59),
            this.indexSave,
            var70,
            var18,
            var19,
            var5,
            var8,
            var51.previous ? 1.0F : 0.3F,
            execute(compute((this.elementTick - 0.36F - var50 * 0.042F) / 0.74F, 0.0F, 1.0F))
         );
      }

      this.playerRun.previous = this.process(AltVaultScreen.Action.DELETE);
      this.handle(
         this.playerRun,
         this.playerApply + var15 - var48,
         this.indexSave,
         var48,
         var18,
         var19,
         var5,
         var8,
         this.playerRun.previous ? 1.0F : 0.3F,
         execute(compute((this.elementTick - 0.48F) / 0.74F, 0.0F, 1.0F))
      );
      this.playerRun.latest = var75;
      this.inputAcquire = 50.0F * var8;
      this.indexCheck = this.playerApply + var15 * 0.5F;
      this.settingSchedule = this.bufferAdapt + 76.0F * var8;
      float var76 = 184.0F * var8;
      this.handle(
         this.matrixRender,
         this.playerApply + var15 * 0.5F - var76 * 0.5F,
         this.bufferAdapt + 198.0F * var8,
         var76,
         var18,
         var19,
         var5,
         var8,
         1.0F,
         this.actionRender.handle()
      );
   }

   private void handle(AltVaultScreen.FileEntry var1, float var2) {
      float var3 = 14.0F * var2;
      var1.animationSchedule = var2;
      var1.outputCollapse = apply(Math.min(var1.vectorMatch * 0.7F, 40.0F * var2));
      var1.sourceBuild = var1.pending + var3;
      var1.profileInvoke = var1.sourceBuild + var1.outputCollapse + 12.0F * var2;
      var1.scaleSave = 96.0F * var2;
      var1.timerRender = var1.pending + var1.matrixBlend - var3 - var1.scaleSave;
      var1.sourceSchedule = Math.max(36.0F * var2, var1.timerRender - var1.profileInvoke - 8.0F * var2);
   }

   private int handle(AltVaultScreen.FileEntry var1, float var2, float var3) {
      if (!(var1.providerFetch <= 0.04F) && !var1.textureRun) {
         float var4 = 28.0F * var1.animationSchedule;
         float var5 = 6.0F * var1.animationSchedule;
         float var6 = var1.previous + var1.vectorMatch * 0.5F - var4 * 0.5F;
         if (!(var3 < var6) && !(var3 > var6 + var4)) {
            for (int var7 = 0; var7 < 3; var7++) {
               float var8 = var1.timerRender + var7 * (var4 + var5);
               if (var2 >= var8 && var2 <= var8 + var4) {
                  return var7;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private void handle(
      AltVaultScreen.PrimaryAnimationState var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10
   ) {
      var1.data = var2;
      var1.context = var3;
      var1.cache = var4;
      var1.output = var5;
      var1.current = var6;
      var1.animator = 46.0F * var8;
      var1.source = var10;
      this.handle(var1, var7, var8, var9);
   }

   private void handle(AltVaultScreen.PrimaryAnimationState var1, float var2, float var3, float var4) {
      float var5 = handle(this.regionAlign, this.resourceClamp, var1.data, var1.context, var1.cache, var1.output, var1.current);
      float var6 = 1.0F - execute(compute(Math.max(0.0F, var5) / Math.max(1.0F, 40.0F * var3), 0.0F, 1.0F));
      boolean var7 = var5 <= 0.0F;
      var1.active = var1.active + ((var7 ? 1.0F : 0.0F) * var4 - var1.active) * (1.0F - (float)Math.pow(1.0E-4F, var2));
      var1.mode = var1.mode + (var6 * var4 - var1.mode) * (1.0F - (float)Math.pow(1.5E-4F, var2));
      var1.selection = var1.selection + (0.0F - var1.selection) * (1.0F - (float)Math.pow(1.8E-5F, var2));
      var1.enabled = var1.enabled + (0.0F - var1.enabled) * (1.0F - (float)Math.pow(6.0E-6F, var2));
      float var8 = compute((this.layerProject - var1.data) / Math.max(1.0F, var1.cache), 0.0F, 1.0F);
      float var9 = compute((this.entityFilter - var1.context) / Math.max(1.0F, var1.output), 0.0F, 1.0F);
      float var10 = 1.0F - (float)Math.pow(1.8E-4F, var2);
      var1.handler = var1.handler + (var8 - var1.handler) * var10;
      var1.animationDraw = var1.animationDraw + (var9 - var1.animationDraw) * var10;
      var1.renderer = 1.0F;
      var1.config = var1.data + (var1.handler - 0.5F) * 4.5F * var3 * var1.mode;
      var1.state = var1.context + (var1.animationDraw - 0.5F) * 3.0F * var3 * var1.mode - var1.active * 1.4F * var3 + var1.selection * 1.8F * var3;
      var1.pointEncode = compute(compute(this.layerSample2, this.sourceCancel) * 0.42F * var1.mode, 0.0F, 1.0F);
   }

   private void handle(AltVaultScreen.FileEntry var1, float var2, float var3, long var4) {
      float var6 = handle(this.regionAlign, this.resourceClamp, var1.pending, var1.previous, var1.matrixBlend, var1.vectorMatch, var1.itemProject);
      float var7 = 1.0F - execute(compute(Math.max(0.0F, var6) / Math.max(1.0F, 44.0F * var3), 0.0F, 1.0F));
      boolean var8 = var6 <= 0.0F && !var1.textureRun && var1.actionRead || var1.dataValidate;
      float var9 = var1.colorCompute ? 0.28F : 0.0F;
      var1.providerFetch = var1.providerFetch + ((var8 ? 1.0F : 0.0F) - var1.providerFetch) * (1.0F - (float)Math.pow(1.0E-4F, var2));
      var1.profileDraw = var1.profileDraw + ((var1.actionRead ? Math.max(var7, var9) : 0.0F) - var1.profileDraw) * (1.0F - (float)Math.pow(1.5E-4F, var2));
      var1.vectorPerform = var1.vectorPerform + (0.0F - var1.vectorPerform) * (1.0F - (float)Math.pow(1.8E-5F, var2));
      var1.eventAttach = var1.eventAttach + (0.0F - var1.eventAttach) * (1.0F - (float)Math.pow(6.0E-6F, var2));
      var1.presetWrite = var1.presetWrite + ((var1.dataValidate ? 1.0F : 0.0F) - var1.presetWrite) * (1.0F - (float)Math.pow(4.0E-5F, var2));
      var1.animationSchedule = var3;
      int var10 = this.handle(var1, this.regionAlign, this.resourceClamp);

      for (int var11 = 0; var11 < 3; var11++) {
         float var12 = var10 == var11 ? 1.0F : 0.0F;
         var1.enabled[var11] = var1.enabled[var11] + (var12 - var1.enabled[var11]) * (1.0F - (float)Math.pow(4.0E-5F, var2));
         var1.renderer[var11] = var1.renderer[var11] + (0.0F - var1.renderer[var11]) * (1.0F - (float)Math.pow(1.8E-5F, var2));
      }

      this.handle(var1, var4);
      this.process(var1, var4);
      float var15 = compute((this.layerProject - var1.pending) / Math.max(1.0F, var1.matrixBlend), 0.0F, 1.0F);
      float var16 = compute((this.entityFilter - var1.previous) / Math.max(1.0F, var1.vectorMatch), 0.0F, 1.0F);
      float var13 = 1.0F - (float)Math.pow(1.8E-4F, var2);
      var1.positionAdvance = var1.positionAdvance + (var15 - var1.positionAdvance) * var13;
      var1.frameCheck = var1.frameCheck + (var16 - var1.frameCheck) * var13;
      var1.serverRead = 1.0F;
      float var14 = (1.0F - compute(var1.current.handle(), 0.0F, 1.0F)) * 22.0F * var3;
      var1.latest = var1.pending + (var1.positionAdvance - 0.5F) * 5.0F * var3 * var1.profileDraw;
      var1.summary = var1.previous
         + (var1.frameCheck - 0.5F) * 3.5F * var3 * var1.profileDraw
         - var1.providerFetch * 1.6F * var3
         - var1.presetWrite * 5.0F * var3
         + var1.vectorPerform * 2.0F * var3
         + var14;
      var1.moduleCollect = compute(compute(this.layerSample2, this.sourceCancel) * 0.42F * var1.profileDraw, 0.0F, 1.0F);
   }

   private boolean attachEvent() {
      float var1 = 12.0F * process(this.optionFetch, this.eventCollapse) + this.positionReset + 10.0F;
      return this.regionAlign >= this.playerApply - var1
         && this.regionAlign <= this.playerApply + this.playerUpdate + var1
         && this.resourceClamp >= this.bufferAdapt
         && this.resourceClamp <= this.bufferAdapt + this.packetRead;
   }

   private void handle(int var1, int var2, int var3, float var4, float var5, long var6) {
      float var8 = Math.max(0.0F, (float)(var6 - this.playerEvaluate) / 1.0E9F);
      float var9 = compute(compute(this.layerSample2, this.sourceCancel), 0.0F, 3.0F);
      float var10 = Math.max((float)Math.exp(-var8 * 1.25F), compute(var9 * 0.22F, 0.0F, 1.0F));
      float var11 = execute(compute(this.elementTick / 0.86F, 0.0F, 1.0F));
      float var12 = process(var1, var2);
      int var13 = 0;

      for (AltVaultScreen.FileEntry var15 : this.matrixBlend2) {
         if (!var15.indexBind && var15.actionRead && !(var15.presetSave <= 0.001F)) {
            WildMainMenuScreen.PrimaryColorState var16 = this.messageParse.handle(var13++);
            float var17 = Math.max(var15.profileDraw, var15.dataValidate ? 0.85F : 0.0F);
            var16.handle(
               var15.data,
               var15.latest,
               var15.summary,
               var15.matrixBlend,
               var15.vectorMatch,
               var15.itemProject,
               var15.providerFetch,
               var17,
               var15.vectorPerform,
               compute(var15.presetSave, 0.0F, 1.0F),
               var15.eventAttach,
               var15.providerClose,
               var15.serverRead,
               var15.positionAdvance,
               var15.frameCheck,
               var15.moduleCollect
            );
            var16.process(this.apply(var15));
            if (var15.clientRefresh) {
               var16.handle(execute(var15.colorMeasure));
            }
         }
      }

      this.handle(
         var13++,
         this.playerCollapse,
         this.playerCollapse.source,
         Math.max(this.playerCollapse.mode, this.playerCollapse.vectorMatch * 0.72F),
         this.playerCollapse.vectorMatch * 0.72F
      );
      this.handle(
         var13++,
         this.keyProcess,
         this.keyProcess.previous ? this.keyProcess.source : this.keyProcess.source * 0.55F,
         this.keyProcess.mode,
         this.keyProcess.previous ? 0.8F : 0.0F
      );

      for (AltVaultScreen.PrimaryNetworkState var22 : List.of(this.actionConvert, this.screenRead, this.animationExpand, this.playerRun)) {
         float var24 = var22 == this.playerRun ? this.playerRun.latest : 0.0F;
         this.handle(var13++, var22, var22.previous ? var22.source : var22.source * 0.55F, var22.mode, var24 * 0.86F);
      }

      if (this.matrixRender.source > 0.002F) {
         this.handle(var13++, this.matrixRender, this.matrixRender.source, this.matrixRender.mode, 0.8F);
      }

      this.messageParse.check(var13);
      this.messageParse.update(0);
      float var21 = this.actionRender.handle();
      if (var21 > 0.004F) {
         this.messageParse.update().handle(this.indexCheck, this.settingSchedule, this.inputAcquire, this.inputAcquire * 2.1F, var21, var21, 3.17F);
         this.messageParse.update().handle(this.layerProject, this.entityFilter);
      } else {
         this.messageParse.update().handle(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      }

      for (int var23 = 0; var23 < 14; var23++) {
         AltVaultScreen.SecondaryNetworkState var25 = this.effectScan[var23];
         float var26 = Math.max(0.0F, this.elementTick - var25.context);
         this.messageParse
            .onTick(var23)
            .handle(var25.instance / Math.max(1.0F, var1), var25.data / Math.max(1.0F, var2), var26, var26 > 3.1F ? 0.0F : var25.config);
      }

      this.messageParse.prepare().handle(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      this.messageParse.handle(var1, var2, var3, this.elementTick * 0.46F, this.elementTick * 0.46F);
      this.messageParse.handle(this.layerProject, this.entityFilter, this.layerSample2 * 0.42F, this.sourceCancel * 0.42F, var9 * 0.42F, 0.0F);
      this.messageParse.handle(this.stateAttach, this.worldEvaluate);
      this.messageParse.process(-var4 * 6.5E-4F, -var5 * 5.0E-4F, var4 * 0.75F * var12, var5 * 0.62F * var12, var4 * 1.2F * var12, var5 * 1.0F * var12);
      this.messageParse.compute(var10 * 0.55F, var10 > 0.1F ? 0.82F : 0.72F, 0.0F, 0.0F, 0.58F + var11 * 0.18F, 0.0F);
      this.messageParse
         .handle(
            this.playerProject == ThemePalette.SAKURA_BREEZE,
            this.playerProject == ThemePalette.VERNAL_SOLSTICE,
            this.playerProject == ThemePalette.MIDNIGHT_AZURE,
            this.playerMatch
         );
   }

   private void handle(int var1, AltVaultScreen.PrimaryAnimationState var2, float var3, float var4, float var5) {
      WildMainMenuScreen.PrimaryColorState var6 = this.messageParse.handle(var1);
      var6.handle(
         var2.instance,
         var2.config,
         var2.state,
         var2.cache,
         var2.output,
         var2.current,
         var2.active,
         var4,
         var2.selection,
         var3,
         var2.enabled,
         var2.animator,
         var2.renderer,
         var2.handler,
         var2.animationDraw,
         var2.pointEncode
      );
      var6.process(compute(var5, 0.0F, 1.0F) * compute(var3, 0.0F, 1.0F));
   }

   private float apply(AltVaultScreen.FileEntry var1) {
      float var2 = var1.scaleAdapt ? 0.92F : (var1.colorCompute ? 0.4F : 0.0F);
      return compute(var2 * compute(var1.presetSave, 0.0F, 1.0F) * var1.rendererScan, 0.0F, 1.0F);
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1) {
      try {
         WildClient.check();
         RoundedRectRenderer var2 = WildClient.handle();
         if (var2 == null) {
            return;
         }

         OpenGlStateSnapshot.NetworkState var3 = OpenGlStateSnapshot.handle();
         boolean var4 = false;

         try {
            var2.handle(var1.onTick(), var1.select());
            var4 = true;
            float var5 = process(var1.onTick(), var1.select());
            long var6 = System.nanoTime();
            float var8 = execute(compute(this.elementTick / 0.82F, 0.0F, 1.0F));
            this.handle(var2, var1, var5, var8, var6);
            this.handle(var2, var5);

            for (AltVaultScreen.FileEntry var10 : this.matrixBlend2) {
               if (!var10.indexBind && var10.actionRead && !(var10.presetSave <= 0.002F)) {
                  this.handle(var2, var10, var5);
               }
            }

            var2.compute();
            var2.handle(
               this.playerApply - 46.0F * var5,
               this.bufferAdapt - 10.0F * var5,
               this.playerUpdate + 92.0F * var5,
               this.packetRead + 20.0F * var5,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );
            AltVaultScreen.FileEntry var21 = null;

            for (int var22 = 0; var22 < this.matrixBlend2.size(); var22++) {
               AltVaultScreen.FileEntry var11 = this.matrixBlend2.get(var22);
               if (!var11.indexBind && var11.actionRead && !(var11.presetSave <= 0.002F)) {
                  if (var11.dataValidate) {
                     var21 = var11;
                  } else {
                     this.handle(var2, var11, var5, var6);
                  }
               }
            }

            if (var21 != null) {
               this.handle(var2, var21, var5, var6);
            }

            var2.compute();
            var2.apply();
            this.process(var2, var8);
            this.compute(var2, var5);
            this.handle(var2, this.playerCollapse, var5);
            this.handle(var2, this.keyProcess, var5);
            this.process(var2, this.actionConvert, var5);
            this.process(var2, this.screenRead, var5);
            this.process(var2, this.animationExpand, var5);
            this.handle(var2, this.playerRun, var5, var6);
         } finally {
            if (var4) {
               try {
                  var2.process();
               } catch (Throwable var18) {
               }
            }

            OpenGlStateSnapshot.compute(var3);
         }
      } catch (Throwable var20) {
      }
   }

   private void handle(RoundedRectRenderer var1, WildMainMenuScreen.PrimaryNetworkState var2, float var3, float var4, long var5) {
      float var7 = var2.onTick() * 0.5F + var2.advancePosition() * 0.1F;
      var1.handle(FontRegistry.config, var7, update(this.screenSubmit), 44.0F * var3, "Alt Manager", this.process(0.95F * var4), "c");
      boolean var8 = this.scaleParse > var5 && !this.indexSynchronize.isEmpty();
      float var9 = var8 ? compute((float)(this.scaleParse - var5) / 3.2E8F, 0.0F, 1.0F) : 0.0F;
      float var10 = (1.0F - var9) * var4;
      float var11 = update(this.cacheHandle);
      float var12 = handle(21.0F, var3);
      if (var10 > 0.004F) {
         String var13 = "Active identity";
         String var14 = this.advancePosition();
         String var15 = "  ·  ";
         FontObject var16 = FontRegistry.instance;
         FontObject var17 = FontRegistry.config;
         float var18 = RoundedRectRenderer.handle(var16, var13, var12).instance;
         float var19 = RoundedRectRenderer.handle(var16, var15, var12).instance;
         float var20 = RoundedRectRenderer.handle(var17, var14, var12).instance;
         float var21 = var7 - (var18 + var19 + var20) * 0.5F;
         var1.handle(var16, update(var21), var11, var12, var13, this.compute(0.54F * var10));
         var21 += var18;
         var1.handle(var16, update(var21), var11, var12, var15, this.compute(0.3F * var10));
         var21 += var19;
         var1.handle(var17, update(var21), var11, var12, var14, handle(this.worldEvaluate, this.stateAttach, 0.42F, 0.92F * var10));
      }

      if (var9 > 0.004F) {
         var1.handle(FontRegistry.instance, var7, var11, var12, this.indexSynchronize, this.process(0.74F * var9 * var4), "c");
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.FileEntry var2, float var3) {
      float var4 = compute(var2.presetSave, 0.0F, 1.0F) * execute(compute((var2.rendererScan - 0.9F) / 0.1F, 0.0F, 1.0F));
      if (!(var4 <= 0.02F)) {
         float var5 = var2.scaleAdapt ? 1.0F : (var2.colorCompute ? 0.3F : 0.0F);
         if (var2.configCollapse) {
            var5 = 1.0F;
         }

         if (!(var5 <= 0.01F)) {
            float var6 = 13.0F * var3;
            float var7 = 1.5F * var3;
            int var8 = var2.configCollapse ? this.resolve(0.24F * var5 * var4) : handle(this.worldEvaluate, this.stateAttach, 0.5F, 0.15F * var5 * var4);
            var1.handle(var2.latest, var2.summary, var2.matrixBlend, var2.vectorMatch, var2.itemProject, var6, var7, var8);
         }
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.FileEntry var2, float var3, long var4) {
      float var6 = compute(var2.presetSave, 0.0F, 1.0F);
      if (!(var6 <= 0.004F)) {
         float var7 = var2.latest - var2.pending;
         float var8 = var2.summary - var2.previous;
         float var9 = var2.previous + var2.vectorMatch * 0.5F + var8;
         float var10 = update(var2.outputCollapse);
         float var11 = update(var2.sourceBuild + var7);
         float var12 = update(var9 - var2.outputCollapse * 0.5F);
         this.process(var1, var2, var11, var12, var10, var6, var3);
         float var13 = handle(27.0F, var3);
         float var14 = handle(17.0F, var3);
         float var15 = handle(FontRegistry.config, var13);
         float var16 = handle(FontRegistry.instance, var14);
         float var17 = 5.0F * var3;
         float var18 = var9 - (var15 + var17 + var16 + var14 * 0.105F) * 0.5F;
         float var19 = update(var2.profileInvoke + var7);
         int var20 = var2.scaleAdapt
            ? handle(this.worldEvaluate, this.stateAttach, 0.34F, (0.95F + var2.profileDraw * 0.05F) * var6)
            : this.process((var2.colorCompute ? 0.94F : 0.74F + var2.profileDraw * 0.12F) * var6);
         String var21 = handle(var2.data, var2.sourceSchedule, var13, FontRegistry.config);
         var1.handle(FontRegistry.config, var19, update(var18 + var15), var13, var21, var20);
         String var22 = var2.active;
         if (var22 != null && !var22.isEmpty()) {
            int var23 = var2.scaleAdapt
               ? handle(this.worldEvaluate, this.stateAttach, 0.5F, 0.62F * var6)
               : this.compute((var2.colorCompute ? 0.58F : 0.46F) * var6);
            var1.handle(FontRegistry.instance, var19, update(var18 + var15 + var17 + var16), var14, var22, var23);
         }

         float var27 = execute(compute(var2.providerFetch * 1.12F, 0.0F, 1.0F));
         if (var27 > 0.01F) {
            float var24 = var9 - 28.0F * var3 * 0.5F;
            this.handle(var1, var2, var2.timerRender + var7, var24, var3, var6 * var27, var27);
         }

         if (var2.scaleAdapt || var2.colorCompute || var2.configCollapse) {
            float var28 = var2.configCollapse ? 1.0F : (var2.scaleAdapt ? 0.46F : 0.16F);
            int var25 = var2.configCollapse
               ? this.resolve(0.72F * var6)
               : (
                  var2.scaleAdapt
                     ? handle(this.worldEvaluate, this.stateAttach, 0.42F, var28 * var6)
                     : (this.playerMatch ? handle(0.0F, 0.0F, 0.0F, var28 * var6) : handle(1.0F, 1.0F, 1.0F, var28 * var6))
               );
            var1.handle(var2.latest, var2.summary, var2.matrixBlend, var2.vectorMatch, var2.itemProject, var25, Math.max(1.25F * var3, 1.0F));
         }

         if (var2.configCollapse) {
            float var29 = this.compute(var4);
            float var30 = var2.itemProject;
            float var26 = Math.max(0.0F, var2.matrixBlend - var30 * 2.0F);
            var1.handle(var2.latest + var30, var2.summary + var2.vectorMatch - 3.2F * var3, var26 * var29, 2.0F * var3, var3, this.resolve(0.85F * var6));
         }
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.FileEntry var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 28.0F * var5;
      float var9 = 6.0F * var5;

      for (int var10 = 0; var10 < 3; var10++) {
         float var11 = var2.enabled[var10];
         float var12 = var2.renderer[var10];
         float var13 = compute(var6 * (0.55F + 0.45F * var7), 0.0F, 1.0F);
         boolean var14 = var10 == 2;
         float var15 = (1.0F - var7) * 8.0F * var5;
         float var16 = var3 + var10 * (var8 + var9) + var15;
         float var17 = var4 - var11 * 1.4F * var5 + var12 * 1.2F * var5;
         if (var11 > 0.02F) {
            int var18 = var14 ? this.resolve(0.26F * var11 * var13) : handle(this.worldEvaluate, this.stateAttach, 0.5F, 0.26F * var11 * var13);
            this.handle(var1, var16 + var8 * 0.5F, var17 + var8 * 0.5F, var8 * 0.46F, var5, var18);
         }

         int var19 = var14
            ? this.resolve((0.5F + var11 * 0.44F) * var13)
            : (this.playerMatch ? handle(0.14F, 0.14F, 0.17F, (0.66F + var11 * 0.3F) * var13) : handle(1.0F, 1.0F, 1.0F, (0.7F + var11 * 0.28F) * var13));
         if (!var14 && var11 > 0.02F) {
            var19 = handle(var19, handle(this.worldEvaluate, this.stateAttach, 0.4F, 1.0F), var11 * 0.6F, (0.7F + var11 * 0.28F) * var13);
         }

         this.handle(var1, providerFetch[var10], var16 + var8 * 0.5F, var17 + var8 * 0.5F, var8 * 0.62F * profileDraw[var10], var19);
      }
   }

   private void handle(RoundedRectRenderer var1, String var2, float var3, float var4, float var5, int var6) {
      var1.handle(FontRegistry.context, update(var3), update(var4 + var5 * 0.4922F), var5 * 2.0F, var2, var6, "c");
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, int var6) {
      float var7 = Math.max(1.5F * var5, 1.0F);
      var1.handle(var2 - var7, var3 - var7, var7 * 2.0F, var7 * 2.0F, var7, var4 * 0.26F, var4 * 0.45F, var6);
   }

   private void handle(RoundedRectRenderer var1, float var2) {
      float var3 = compute(this.optionAdvance.source, 0.0F, 1.0F);
      if (!(var3 <= 0.004F)) {
         float var4 = compute(this.optionAdvance.active, 0.0F, 1.0F);
         float var5 = compute(this.optionAdvance.selection, 0.0F, 1.0F);
         float var6 = 5.0F * var2;
         float var7 = 8.5F * var2;
         float var8 = this.listenerRun - var4 * 1.6F * var2 + var5 * 0.8F * var2;
         float var9 = this.indexLoad;
         if (var4 > 0.02F) {
            this.handle(var1, var8, var9, this.optionAdvance.cache * 0.52F, var2, handle(this.worldEvaluate, this.stateAttach, 0.5F, 0.24F * var4 * var3));
         }

         int var10 = this.playerMatch ? handle(0.12F, 0.12F, 0.15F, (0.56F + var4 * 0.38F) * var3) : handle(1.0F, 1.0F, 1.0F, (0.6F + var4 * 0.36F) * var3);
         if (var4 > 0.02F) {
            var10 = handle(var10, handle(this.worldEvaluate, this.stateAttach, 0.4F, 1.0F), var4 * 0.62F, (0.6F + var4 * 0.36F) * var3);
         }

         float var11 = Math.max(2.0F * var2, 1.4F);
         this.handle(var1, var8 + var6, var9 - var7, var8 - var6, var9, var11, var10);
         this.handle(var1, var8 - var6, var9, var8 + var6, var9 + var7, var11, var10);
      }
   }
   private void process(RoundedRectRenderer var1, AltVaultScreen.FileEntry var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var5 * 0.295F;
      boolean var9 = GlCompatibilityProbe.process();
      OpenGlStateSnapshot.NetworkState var10 = null;
      if (var9) {
         var1.compute();
         var10 = OpenGlStateSnapshot.handle();
         int var11 = this.client.getWindow().getFramebufferHeight();
         int var12 = Math.max(0, (int)Math.floor(var3));
         int var13 = Math.max(0, (int)Math.floor(var11 - var4 - var5));
         int var14 = Math.max(1, (int)Math.ceil(var3 + var5) - var12);
         int var15 = Math.max(1, (int)Math.ceil(var5));
         GL11.glEnable(3089);
         GL11.glScissor(var12, var13, var14, var15);
      }

      boolean var18 = false /* VF: Semaphore variable */;

      try {
         var18 = true;
         var1.handle(var3, var4, var5, var5, var8, this.playerMatch ? handle(1.0F, 1.0F, 1.0F, 0.3F * var6) : handle(0.06F, 0.07F, 0.09F, 0.42F * var6));
         this.handle(var1, var2, var3, var4, var5, var6);
         if (!var9) {
            var1.handle(
               var3,
               var4,
               var5,
               var5,
               var8,
               handle(1.0F, 1.0F, 1.0F, 0.055F * var6),
               handle(1.0F, 1.0F, 1.0F, 0.018F * var6),
               handle(0.0F, 0.0F, 0.0F, 0.06F * var6),
               handle(0.0F, 0.0F, 0.0F, 0.03F * var6)
            );
         }

         int var20 = var2.scaleAdapt
            ? handle(this.worldEvaluate, this.stateAttach, 0.42F, 0.66F * var6)
            : (
               var2.colorCompute
                  ? (this.playerMatch ? handle(0.0F, 0.0F, 0.0F, 0.22F * var6) : handle(1.0F, 1.0F, 1.0F, 0.26F * var6))
                  : (this.playerMatch ? handle(0.0F, 0.0F, 0.0F, 0.12F * var6) : handle(1.0F, 1.0F, 1.0F, 0.13F * var6))
            );
         var1.handle(var3, var4, var5, var5, var8, var20, Math.max(1.2F * var7, 1.0F));
         var18 = false;
      } finally {
         if (var18) {
            if (var10 != null) {
               var1.compute();
               OpenGlStateSnapshot.compute(var10);
            }
         }
      }

      if (var10 != null) {
         var1.compute();
         OpenGlStateSnapshot.compute(var10);
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.FileEntry var2, float var3, float var4, float var5, float var6) {
      float var7 = var5 * 0.295F;

      try {
         MinecraftClient var8 = this.client == null ? MinecraftClient.getInstance() : this.client;
         Identifier var9 = this.handle(var8, var2);
         AbstractTexture var10 = var8.getTextureManager().getTexture(var9);
         if (var10 != null && var10.getGlTexture() instanceof GlTexture var11 && var11.getGlId() > 0) {
            int var14 = var11.getGlId();
            if (var2.animator != var14) {
               var10.setFilter(false, false);
               GL11.glBindTexture(3553, var14);
               GL11.glTexParameteri(3553, 10241, 9728);
               GL11.glTexParameteri(3553, 10240, 9728);
               var2.animator = var14;
            }

            var1.update(var6);
            var1.handle(var14, var3, var4, var5, var5, 0.125F, 0.125F, 0.25F, 0.25F, var7);
            var1.handle(var14, var3, var4, var5, var5, 0.625F, 0.125F, 0.75F, 0.25F, var7);
            var1.onTick();
            return;
         }
      } catch (Throwable var13) {
      }

      var1.handle(var3, var4, var5, var5, var7, this.playerMatch ? handle(1.0F, 1.0F, 1.0F, 0.42F * var6) : handle(0.07F, 0.08F, 0.1F, 0.62F * var6));
   }

   private void process(RoundedRectRenderer var1, float var2) {
      float var3 = this.keySample * var2;
      if (this.playerPerform && !(var3 <= 0.01F)) {
         var1.handle(
            this.providerSynchronize,
            this.pathProcess,
            this.positionReset,
            this.effectApply,
            this.positionReset * 0.5F,
            this.playerMatch ? handle(0.0F, 0.0F, 0.0F, 0.05F * var3) : handle(1.0F, 1.0F, 1.0F, 0.055F * var3)
         );
         int var4 = handle(this.worldEvaluate, this.stateAttach, 0.5F, (this.dataRelease ? 0.8F : 0.48F) * var3);
         var1.handle(this.providerSynchronize, this.cacheHandle2, this.positionReset, this.sessionAdvance, this.positionReset * 0.5F, var4);
      }
   }

   private void compute(RoundedRectRenderer var1, float var2) {
      float var3 = this.actionRender.handle();
      if (!(var3 <= 0.01F)) {
         float var4 = this.playerApply + this.playerUpdate * 0.5F;
         var1.handle(
            FontRegistry.config, var4, update(this.bufferAdapt + 158.0F * var2), handle(30.0F, var2), "No identities yet", this.process(0.84F * var3), "c"
         );
         var1.handle(
            FontRegistry.instance,
            var4,
            update(this.bufferAdapt + 184.0F * var2),
            handle(17.0F, var2),
            "Roll a name or type one below",
            this.compute(0.54F * var3),
            "c"
         );
         this.handle(var1, this.matrixRender, var2);
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.NetworkState var2, float var3) {
      float var4 = compute(var2.source, 0.0F, 1.0F);
      if (!(var4 <= 0.004F)) {
         FontObject var5 = FontRegistry.instance;
         String var6 = var2.target ? "*".repeat(var2.previous.length()) : var2.previous;
         float var7 = 15.0F * var3;
         float var8 = var2.config + var7;
         float var9 = Math.max(8.0F * var3, var2.cache - var7 * 2.0F);
         float var10 = handle(22.0F, var3);
         float var11 = handle(15.0F, var3);
         float var12 = compute(var2.vectorMatch, 0.0F, 1.0F);
         float var13 = execute(compute(var2.providerFetch, 0.0F, 1.0F));
         if (var12 > 0.02F) {
            var1.handle(
               var2.config,
               var2.state,
               var2.cache,
               var2.output,
               var2.current,
               15.0F * var3,
               1.0F * var3,
               handle(this.worldEvaluate, this.stateAttach, 0.5F, (this.playerMatch ? 0.09F : 0.17F) * var12 * var4)
            );
         }

         float var14 = handle(var5, var11);
         float var15 = handle(var5, var10);
         float var16 = Math.max(var2.output * 0.09F, var11 * 0.3F);
         float var17 = var2.state + (var2.output - (var14 + var16 + var15 + var10 * 0.105F)) * 0.5F;
         float var18 = var17 + var14;
         float var19 = var17 + var14 + var16 + var15;
         float var20 = handle(var5, var10, var2.state, var2.output);
         float var21 = var20 + (var18 - var20) * var13;
         float var22 = var10 + (var11 - var10) * var13;
         float var23 = execute(compute((var2.providerFetch - 0.42F) / 0.58F, 0.0F, 1.0F)) * var4;
         var1.handle(var8 - 5.0F * var3, var2.state, var9 + 5.0F * var3, var2.output, 0.0F, 0.0F, 0.0F, 0.0F);
         int var24 = this.playerMatch ? handle(0.3F, 0.31F, 0.34F, 1.0F) : handle(0.78F, 0.84F, 0.89F, 1.0F);
         int var25 = handle(this.worldEvaluate, this.stateAttach, 0.42F, 1.0F);
         float var26 = var4 * (0.44F + 0.14F * var13 + 0.24F * var12);
         var1.handle(
            var5, update(var8), update(var21), var22, this.load() ? "New name" : var2.instance, handle(var24, var25, var13 * (0.34F + 0.66F * var12), var26)
         );
         if (var2.summary) {
            String var27 = var6.substring(0, handle(var2.latest, 0, var6.length()));
            float var28 = RoundedRectRenderer.handle(var5, var27, var10).instance;
            if (var2.matrixBlend) {
               var28 = RoundedRectRenderer.handle(var5, var6, var10).instance;
            }

            float var29 = var2.responseCompute;
            float var30 = var9 - 9.0F * var3;
            if (var28 - var29 > var30) {
               var29 = var28 - var30;
            }

            if (var28 - var29 < 0.0F) {
               var29 = var28;
            }

            var2.responseCompute = var2.responseCompute + (Math.max(0.0F, var29) - var2.responseCompute) * 0.3F;
            var2.itemProject = var2.itemProject + (var28 - var2.itemProject) * 0.3F;
         } else if (var6.isBlank()) {
            var2.responseCompute = 0.0F;
            var2.itemProject = 0.0F;
         }

         if (var23 > 0.004F && !var6.isBlank()) {
            float var32 = var8 - var2.responseCompute;
            float var35 = var19 - var10 * 0.375F;
            float var36 = var10 * 0.505F;
            if (var2.matrixBlend) {
               float var37 = RoundedRectRenderer.handle(var5, var6, var10).instance;
               var1.handle(
                  update(var32 - 3.0F * var3),
                  update(var35),
                  var37 + 6.0F * var3,
                  var36,
                  var36 * 0.3F,
                  handle(this.worldEvaluate, this.stateAttach, 0.5F, 0.28F * var23)
               );
            }

            var1.handle(var5, update(var32), update(var19), var10, var6, this.process((0.82F + var12 * 0.14F) * var23));
            if (var2.summary && !var2.matrixBlend) {
               float var38 = 0.54F + 0.46F * (float)Math.sin(this.elementTick * 5.4F);
               var1.handle(
                  update(var8 + var2.itemProject - var2.responseCompute + 1.5F * var3),
                  update(var35),
                  Math.max(1.4F * var3, 1.0F),
                  var36,
                  Math.max(0.7F * var3, 0.5F),
                  handle(this.worldEvaluate, this.stateAttach, var38, (0.46F + var38 * 0.4F) * var4)
               );
            }
         } else if (var2.summary) {
            float var31 = 0.54F + 0.46F * (float)Math.sin(this.elementTick * 5.4F);
            float var34 = var19 - var10 * 0.375F;
            var1.handle(
               update(var8 + 1.5F * var3),
               update(var34),
               Math.max(1.4F * var3, 1.0F),
               var10 * 0.505F,
               Math.max(0.7F * var3, 0.5F),
               handle(this.worldEvaluate, this.stateAttach, var31, (0.46F + var31 * 0.4F) * var4 * var13)
            );
         }

         var1.apply();
         float var33 = (0.07F + var12 * 0.36F) * var4;
         var1.handle(
            var2.config,
            var2.state,
            var2.cache,
            var2.output,
            var2.current,
            handle(this.worldEvaluate, this.stateAttach, 0.5F, var33),
            Math.max(1.2F * var3, 1.0F)
         );
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.PrimaryNetworkState var2, float var3) {
      float var4 = compute(var2.source, 0.0F, 1.0F) * (var2.previous ? 1.0F : 0.42F);
      if (!(var4 <= 0.004F)) {
         float var5 = var2.previous ? 0.15F + var2.active * 0.13F : 0.05F;
         var1.handle(var2.config, var2.state, var2.cache, var2.output, var2.current, handle(this.worldEvaluate, this.stateAttach, 0.5F, var5 * var4));
         var1.handle(
            var2.config,
            var2.state,
            var2.cache,
            var2.output,
            var2.current,
            handle(this.worldEvaluate, this.stateAttach, 0.42F, (0.36F + var2.active * 0.26F) * var4),
            Math.max(1.2F * var3, 1.0F)
         );
         float var6 = handle(24.0F, var3);
         String var7 = handle(var2.instance, var2.cache - 18.0F * var3, var6, FontRegistry.config);
         handle(var1, FontRegistry.config, var2.config, var2.state, var2.cache, var2.output, var6, var7, this.process((0.94F + var2.active * 0.06F) * var4));
      }
   }

   private void process(RoundedRectRenderer var1, AltVaultScreen.PrimaryNetworkState var2, float var3) {
      float var4 = compute(var2.source, 0.0F, 1.0F) * (var2.previous ? 1.0F : 0.34F);
      if (!(var4 <= 0.004F)) {
         float var5 = handle(23.0F, var3);
         String var6 = handle(var2.instance, var2.cache - 18.0F * var3, var5, FontRegistry.instance);
         handle(var1, FontRegistry.instance, var2.config, var2.state, var2.cache, var2.output, var5, var6, this.process((0.74F + var2.active * 0.22F) * var4));
      }
   }

   private void handle(RoundedRectRenderer var1, AltVaultScreen.PrimaryNetworkState var2, float var3, long var4) {
      float var6 = compute(var2.source, 0.0F, 1.0F) * (var2.previous ? 1.0F : 0.3F);
      if (!(var6 <= 0.004F)) {
         float var7 = compute(var2.latest, 0.0F, 1.0F);
         if (var7 > 0.01F) {
            var1.handle(var2.config, var2.state, var2.cache, var2.output, var2.current, 12.0F * var3, 1.0F * var3, this.resolve(0.2F * var7 * var6));
            var1.handle(var2.config, var2.state, var2.cache, var2.output, var2.current, this.resolve((0.11F + var2.active * 0.06F) * var7 * var6));
            var1.handle(
               var2.config,
               var2.state,
               var2.cache,
               var2.output,
               var2.current,
               this.resolve((0.4F + var2.active * 0.24F) * var7 * var6),
               Math.max(1.2F * var3, 1.0F)
            );
            float var8 = this.compute(var4);
            float var9 = var2.current;
            float var10 = Math.max(0.0F, var2.cache - var9 * 2.0F);
            var1.handle(var2.config + var9, var2.state + var2.output - 3.2F * var3, var10 * var8, 2.0F * var3, var3, this.resolve(0.74F * var7 * var6));
         }

         float var13 = handle(23.0F, var3);
         String var14 = handle(var2.instance, var2.cache - 18.0F * var3, var13, FontRegistry.instance);
         int var15 = this.playerMatch ? handle(0.32F, 0.32F, 0.35F, 1.0F) : handle(1.0F, 1.0F, 1.0F, 1.0F);
         int var11 = this.resolve(1.0F);
         float var12 = ((0.46F + var2.active * 0.26F) * (1.0F - var7) + (0.9F + var2.active * 0.1F) * var7) * var6;
         handle(
            var1,
            var7 > 0.5F ? FontRegistry.config : FontRegistry.instance,
            var2.config,
            var2.state,
            var2.cache,
            var2.output,
            var13,
            var14,
            handle(var15, var11, var7, var12)
         );
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      float var8 = var4 - var2;
      float var9 = var5 - var3;
      float var10 = (float)Math.sqrt(var8 * var8 + var9 * var9);
      if (!(var10 < 0.05F)) {
         float var11 = (float)Math.toDegrees(Math.atan2(var9, var8));
         var1.handle((var2 + var4) * 0.5F, (var3 + var5) * 0.5F);
         var1.process(var11);
         var1.handle(-(var10 + var6) * 0.5F, -var6 * 0.5F, var10 + var6, var6, var6 * 0.5F, var7);
         var1.execute();
         var1.prepare();
      }
   }

   private void handle(AltVaultScreen.FileEntry var1, long var2) {
      if (var2 - var1.requestAdapt >= 1000000000L || !var1.keyFilter) {
         var1.keyFilter = true;
         var1.requestAdapt = var2;

         try {
            var1.timerMeasure = AltVaultScreen.CacheEntry.handle(var1.instance, var1.data);
         } catch (Throwable var5) {
            var1.timerMeasure = null;
         }
      }
   }

   private int process(float var1) {
      return this.playerMatch ? handle(0.1F, 0.1F, 0.1F, var1) : handle(1.0F, 1.0F, 1.0F, var1);
   }

   private int compute(float var1) {
      return this.playerMatch ? handle(0.3F, 0.31F, 0.34F, var1) : handle(0.8F, 0.86F, 0.9F, var1);
   }

   private int resolve(float var1) {
      return this.playerMatch ? handle(0.78F, 0.19F, 0.17F, var1) : handle(1.0F, 0.44F, 0.4F, var1);
   }

   private static void handle(RoundedRectRenderer var0, FontObject var1, float var2, float var3, float var4, float var5, float var6, String var7, int var8) {
      String var9 = var7 == null ? "" : var7;
      float var10 = RoundedRectRenderer.handle(var1, var9, var6).instance;
      float var11 = update(var2 + (var4 - var10) * 0.5F);
      float var12 = update(handle(var1, var6, var3, var5));
      var0.handle(var1, var11, var12, var6, var9, var8);
   }

   private boolean process(AltVaultScreen.Action var1) {
      return switch (var1) {
         case USE -> this.projectItem() != null;
         case ADD_CRACKED -> !check(this.playerCollapse.previous).isBlank();
         case RANDOM, BACK, CREATE_FIRST -> true;
         case EDIT -> this.save();
         case DELETE -> this.projectItem() != null;
      };
   }

   private Identifier handle(MinecraftClient var1, AltVaultScreen.FileEntry var2) {
      Identifier var3 = packetSave.get(var2.cache);
      if (var3 != null) {
         return var3;
      }

      this.process(var1, var2);
      if (!var2.source) {
         var2.pointEncode = var1.getSkinProvider().getSkinTextures(var2.state).texture();
         var2.source = true;
      }

      return var2.pointEncode;
   }

   private void process(MinecraftClient var1, AltVaultScreen.FileEntry var2) {
      if (var1 != null && var2.data.length() >= 3 && entryAnimate.add(var2.cache)) {
         GameProfile var3 = var1.getGameProfile();
         if (var3 != null && var2.data.equalsIgnoreCase(var3.getName()) && !var3.getProperties().isEmpty()) {
            handle(var1, var2.cache, var3);
         } else {
            String var4 = var2.data;
            String var5 = var2.cache;
            windowProcess.execute(() -> {
               try {
                  GameProfile var3x = (GameProfile)process(var1).findProfileByName(var4).orElse(null);
                  if (var3x == null || var3x.getId() == null) {
                     return;
                  }

                  ProfileResult var4x = var1.getSessionService().fetchProfile(var3x.getId(), false);
                  GameProfile var5x = var4x != null && var4x.profile() != null ? var4x.profile() : var3x;
                  var1.execute(() -> handle(var1, var5, var5x));
               } catch (Throwable var6) {
                  entryAnimate.remove(var5);
               }
            });
         }
      }
   }

   private static void handle(MinecraftClient var0, String var1, GameProfile var2) {
      try {
         var0.getSkinProvider().fetchSkinTextures(var2).thenAccept(var1x -> var1x.ifPresent(var1xx -> packetSave.put(var1, var1xx.texture())));
      } catch (Throwable var4) {
      }
   }

   private static GameProfileRepository process(MinecraftClient var0) {
      GameProfileRepository var1 = playerCollect;
      if (var1 == null) {
         synchronized (entryAnimate) {
            var1 = playerCollect;
            if (var1 == null) {
               var1 = new YggdrasilAuthenticationService(var0.getNetworkProxy()).createProfileRepository();
               playerCollect = var1;
            }
         }
      }

      return var1;
   }

   private boolean handle(AltVaultScreen.FileEntry var1, String var2) {
      if (var2.isBlank()) {
         return false;
      }

      MinecraftClient var3 = this.client == null ? MinecraftClient.getInstance() : this.client;
      boolean var4 = var3 != null && var3.getSession() != null && var3.getSession().getAccountType() != net.minecraft.client.session.Session.AccountType.LEGACY;
      return var1.data.equals(var2) && var1.context == (var4 ? AltVaultScreen.PrimaryAccountType.PREMIUM : AltVaultScreen.PrimaryAccountType.CRACKED);
   }

   private String readServer() {
      MinecraftClient var1 = this.client == null ? MinecraftClient.getInstance() : this.client;
      return var1 != null && var1.getSession() != null ? var1.getSession().getUsername() : "";
   }

   private String advancePosition() {
      String var1 = this.readServer();
      return var1.isBlank() ? "no session" : var1;
   }

   private void process(int var1) {
      if (this.collectModule() == 0) {
         this.cacheClose = -1;
         this.compute("No identities");
      } else {
         int var2 = this.cacheClose < 0 ? (var1 >= 0 ? -1 : this.matrixBlend2.size()) : this.cacheClose;

         for (int var3 = 0; var3 < this.matrixBlend2.size(); var3++) {
            var2 = handle(var2 + var1, 0, this.matrixBlend2.size() - 1);
            AltVaultScreen.FileEntry var4 = this.matrixBlend2.get(var2);
            if (!var4.indexBind && !var4.textureRun) {
               this.handle(var2, "Selected " + var4.data);
               var4.eventAttach = Math.max(var4.eventAttach, 0.24F);
               this.compute();
               return;
            }

            if (var1 > 0 && var2 == this.matrixBlend2.size() - 1 || var1 < 0 && var2 == 0) {
               return;
            }
         }
      }
   }

   private void checkFrame() {
      if (this.cacheClose >= 0 && this.cacheClose < this.matrixBlend2.size()) {
         AltVaultScreen.FileEntry var1 = this.matrixBlend2.get(this.cacheClose);
         if (!var1.indexBind && !var1.textureRun) {
            int var2 = this.compute(this.cacheClose);
            int var3 = (int)Math.floor(this.playerSave);
            int var4 = var3 + this.scaleSetup - 1;
            if (var2 < var3 || var2 > var4) {
               if (var2 < this.playerSave) {
                  this.playerSave = var2;
               }

               if (var2 > this.playerSave + this.scaleSetup - 1.0F) {
                  this.playerSave = var2 - this.scaleSetup + 1;
               }

               int var5 = Math.max(0, this.collectModule() - Math.max(1, this.scaleSetup));
               this.playerSave = compute(this.playerSave, 0.0F, var5);
               this.frameProject = 0.85F;
            }
         }
      }
   }

   private int collectModule() {
      int var1 = 0;

      for (AltVaultScreen.FileEntry var3 : this.matrixBlend2) {
         if (!var3.textureRun && !var3.indexBind) {
            var1++;
         }
      }

      return var1;
   }

   private int compute(int var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < var1; var3++) {
         if (!this.matrixBlend2.get(var3).textureRun) {
            var2++;
         }
      }

      return var2;
   }

   private float handle(Window var1, double var2) {
      return (float)(var2 * var1.getFramebufferWidth() / Math.max(1.0, var1.getScaledWidth()));
   }

   private float process(Window var1, double var2) {
      return (float)(var2 * var1.getFramebufferHeight() / Math.max(1.0, var1.getScaledHeight()));
   }

   static String check(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.trim();
      int var2 = var1.indexOf(64);
      if (var2 > 0) {
         var1 = var1.substring(0, var2);
      }

      var1 = var1.replaceAll("[^A-Za-z0-9_]", "");
      if (var1.length() > 16) {
         var1 = var1.substring(0, 16);
      }

      return var1;
   }

   private void handle(Session var1, boolean var2) {
      if (var1 != null) {
         String var3 = check(var1.getUsername());
         if (!var3.isBlank()) {
            AltVaultScreen.PrimaryAccountType var4 = var1.getAccountType() == net.minecraft.client.session.Session.AccountType.LEGACY
               ? AltVaultScreen.PrimaryAccountType.CRACKED
               : AltVaultScreen.PrimaryAccountType.PREMIUM;
            if (this.handle(var3, var4) < 0) {
               long var5 = System.currentTimeMillis();
               this.matrixBlend2.add(0, new AltVaultScreen.FileEntry(var3, var4, var2, this.elementTick, process(var3, var4), var5, var5));
            }
         }
      }
   }

   static String process(String var0, AltVaultScreen.PrimaryAccountType var1) {
      return UUID.nameUUIDFromBytes(("wild-alt-vault:" + var1.name() + ":" + var0).getBytes(StandardCharsets.UTF_8)).toString();
   }

   private static float handle(float var0, float var1) {
      return Math.max(var0 * var1, 18.0F);
   }

   private static float handle(FontObject var0, float var1) {
      try {
         float var2 = FontRegistry.handle(var0, 72, var1 * 0.5F);
         if (var2 > 0.05F) {
            return var2 * 2.0F;
         }
      } catch (Throwable var3) {
      }

      return var1 * 0.36F;
   }

   private void process(AltVaultScreen.FileEntry var1, long var2) {
      AltVaultScreen.ServerEntry var4 = var1.timerMeasure;
      int var5 = var1.scaleAdapt ? 0 : (var4 != null ? 1 : (var1.animationDraw > 0L ? 2 : 3));

      long var6 = switch (var5) {
         case 1 -> var4.totalMs() / 60000L;
         case 2 -> (System.currentTimeMillis() - var1.animationDraw) / 60000L;
         default -> 0L;
      };
      if (var1.selection != var5 || var1.mode != var6) {
         var1.selection = var5;
         var1.mode = var6;

         var1.active = switch (var5) {
            case 0 -> "Current session";
            case 1 -> "Played " + update(var4.totalMs());
            case 2 -> "Last used " + resolve(System.currentTimeMillis() - var1.animationDraw);
            default -> "Never signed in";
         };
      }
   }

   private static String resolve(long var0) {
      long var2 = Math.max(0L, var0 / 60000L);
      if (var2 < 2L) {
         return "just now";
      }

      if (var2 < 60L) {
         return var2 + "m ago";
      }

      long var4 = var2 / 60L;
      if (var4 < 24L) {
         return var4 + "h ago";
      }

      long var6 = var4 / 24L;
      if (var6 < 7L) {
         return var6 + "d ago";
      }

      long var8 = var6 / 7L;
      return var8 < 9L ? var8 + "w ago" : Math.max(1L, var6 / 30L) + "mo ago";
   }

   private static float handle(FontObject var0, float var1, float var2, float var3) {
      try {
         return var2 + var3 * 0.5F + FontRegistry.handle(var0, 72, var1 * 0.5F);
      } catch (Throwable var5) {
         return var2 + var3 * 0.5F + var1 * 0.18F;
      }
   }

   private static float update(float var0) {
      return Math.round(var0);
   }

   private static float apply(float var0) {
      return Math.max(16.0F, Math.round(var0 / 8.0F) * 8.0F);
   }

   private static String onTick(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         StringBuilder var1 = new StringBuilder(var0.length());

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            if (var3 == 167) {
               var2++;
            } else if (var3 == '&' && var2 + 1 < var0.length() && handle(var0.charAt(var2 + 1))) {
               var2++;
            } else if (!Character.isISOControl(var3)) {
               var1.append(var3);
            }
         }

         return var1.toString().trim();
      } else {
         return "";
      }
   }

   private static boolean handle(char var0) {
      return var0 >= '0' && var0 <= '9'
         || var0 >= 'a' && var0 <= 'f'
         || var0 >= 'A' && var0 <= 'F'
         || var0 >= 'k' && var0 <= 'o'
         || var0 >= 'K' && var0 <= 'O'
         || var0 == 'r'
         || var0 == 'R';
   }

   private static String handle(String var0, float var1, float var2, FontObject var3) {
      if (var0 == null) {
         return "";
      }

      if (var1 <= 0.0F) {
         return "";
      }

      if (RoundedRectRenderer.handle(var3, var0, var2).instance <= var1) {
         return var0;
      }

      String var4 = "...";
      if (RoundedRectRenderer.handle(var3, var4, var2).instance > var1) {
         return "";
      }

      int var5 = 1;
      int var6 = var0.length();
      int var7 = 1;

      while (var5 <= var6) {
         int var8 = var5 + var6 >>> 1;
         if (RoundedRectRenderer.handle(var3, var0.substring(0, var8) + var4, var2).instance <= var1) {
            var7 = var8;
            var5 = var8 + 1;
         } else {
            var6 = var8 - 1;
         }
      }

      return var0.substring(0, var7) + var4;
   }

   private static String update(long var0) {
      long var2 = Math.max(0L, var0 / 1000L);
      long var4 = var2 / 3600L;
      long var6 = var2 % 3600L / 60L;
      long var8 = var2 % 60L;
      if (var4 > 0L) {
         return var6 > 0L ? var4 + "h " + var6 + "m" : var4 + "h";
      } else if (var6 <= 0L) {
         return Math.max(1L, var8) + "s";
      } else {
         return var8 > 0L && var6 < 10L ? var6 + "m " + var8 + "s" : var6 + "m";
      }
   }

   private static float process(float var0, float var1) {
      float var2 = compute(Menu.responseCompute.compute() / 0.86F, 0.72F, 1.46F);
      return compute(Math.min(var0 / 1920.0F, var1 / 1080.0F) * 1.1F * var2, 0.68F, 2.2F);
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

   private static float execute(float var0) {
      float var1 = compute(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   private static float compute(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   static int handle(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static int handle(float var0, float var1, float var2, float var3) {
      int var4 = Math.round(compute(var0, 0.0F, 1.0F) * 255.0F);
      int var5 = Math.round(compute(var1, 0.0F, 1.0F) * 255.0F);
      int var6 = Math.round(compute(var2, 0.0F, 1.0F) * 255.0F);
      int var7 = Math.round(compute(var3, 0.0F, 1.0F) * 255.0F);
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   private static int handle(int var0, int var1, float var2, float var3) {
      float var4 = compute(var2, 0.0F, 1.0F);
      int var5 = PackedColor.resolve(var0, var1, var4);
      int var6 = Math.round(compute(var3, 0.0F, 1.0F) * 255.0F);
      return var6 << 24 | var5;
   }

   enum AccountType {
      PRIMARY,
      SECONDARY,
      DESTRUCTIVE;
   }

   enum Action {
      USE,
      ADD_CRACKED,
      RANDOM,
      EDIT,
      DELETE,
      BACK,
      CREATE_FIRST;
   }

   static final class AnimationState extends AltVaultScreen.PrimaryAnimationState {
      AnimationState() {
         super("Back");
      }
   }

   static final class CacheEntry {
      private static final Gson instance = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
      private static final long data = 10000L;
      private static final long context = 60000L;
      private static final Map<String, ServerStatusCache> config = new HashMap<>();
      private static final Map<String, byte[]> state = new HashMap<>();
      private static boolean cache;
      private static boolean output;
      private static boolean current;
      private static String active = "";
      private static String mode = "";
      private static long selection;
      private static long enabled;

      private CacheEntry() {
      }

      static synchronized void handle(MinecraftClient var0) {
         process();
         long var1 = System.currentTimeMillis();
         ServerListEntrySnapshot var3 = process(var0);
         String var4 = compute(var0);
         if (var3 != null && !var4.isBlank()) {
            AltVaultScreen.PrimaryAccountType var5 = resolve(var0);
            String var6 = AltVaultScreen.process(var4, var5);
            if (var6.equals(active) && var3.key().equals(mode)) {
               long var7 = Math.min(60000L, Math.max(0L, var1 - selection));
               selection = var1;
               if (var7 > 0L) {
                  handle(var6, var4, var3, var7, var1);
               }

               handle(var1, false);
            } else {
               handle(var1);
               active = var6;
               mode = var3.key();
               selection = var1;
               handle(var6, var4, var3, 0L, var1);
               handle(var1, false);
            }
         } else {
            handle(var1);
            handle(var1, true);
         }
      }

      static synchronized void handle() {
         process();
         long var0 = System.currentTimeMillis();
         handle(var0);
         handle(var0, true);
      }

      static synchronized AltVaultScreen.ServerEntry handle(String var0, String var1) {
         process();
         ServerStatusRecord var2 = null;
         ArrayList var3 = new ArrayList(3);
         if (var0 != null && !var0.isBlank()) {
            var3.add(var0);
         }

         String var4 = AltVaultScreen.check(var1);
         if (!var4.isBlank()) {
            var3.add(AltVaultScreen.process(var4, AltVaultScreen.PrimaryAccountType.CRACKED));
            var3.add(AltVaultScreen.process(var4, AltVaultScreen.PrimaryAccountType.PREMIUM));
         }

         HashSet var5 = new HashSet();

         for (String var7 : (List<String>) var3) {
            if (var7 != null && !var7.isBlank() && var5.add(var7)) {
               ServerStatusCache var8 = config.get(var7);
               if (var8 != null) {
                  for (ServerStatusRecord var10 : var8.context.values()) {
                     if (var10.state > 0L && (var2 == null || var10.state > var2.state || var10.state == var2.state && var10.cache > var2.cache)) {
                        var2 = var10;
                     }
                  }
               }
            }
         }

         if (var2 == null) {
            return null;
         }

         byte[] var11 = var2.config == null ? null : Arrays.copyOf(var2.config, var2.config.length);
         if ((var11 == null || var11.length == 0) && !var2.context.isBlank()) {
            var11 = handle(var2.context);
            if (var11 != null && var11.length > 0) {
               var2.config = Arrays.copyOf(var11, var11.length);
               current = true;
            }
         }

         return new AltVaultScreen.ServerEntry(var2.handle(), var2.context, var11, var2.state, var2.cache);
      }

      private static void handle(long var0) {
         if (!active.isBlank() && !mode.isBlank() && selection > 0L) {
            ServerStatusCache var2 = config.get(active);
            ServerStatusRecord var3 = var2 == null ? null : var2.context.get(mode);
            if (var3 != null) {
               long var4 = Math.min(60000L, Math.max(0L, var0 - selection));
               if (var4 > 0L) {
                  var3.state += var4;
                  var3.cache = var0;
                  current = true;
               }
            }
         }

         active = "";
         mode = "";
         selection = 0L;
      }

      private static void handle(String var0, String var1, ServerListEntrySnapshot var2, long var3, long var5) {
         ServerStatusCache var7 = config.computeIfAbsent(var0, var2x -> new ServerStatusCache(var0, var1));
         var7.data = var1;
         ServerStatusRecord var8 = var7.context.computeIfAbsent(var2.key(), var1x -> new ServerStatusRecord(var2.key(), var2.name(), var2.address()));
         var8.data = var2.name();
         var8.context = var2.address();
         if (var2.favicon() != null && var2.favicon().length > 0) {
            var8.config = Arrays.copyOf(var2.favicon(), var2.favicon().length);
         }

         var8.state = var8.state + Math.max(0L, var3);
         var8.cache = var5;
         current = true;
      }

      private static ServerListEntrySnapshot process(MinecraftClient var0) {
         if (var0 != null && var0.player != null && var0.world != null && var0.getNetworkHandler() != null) {
            try {
               if (var0.isConnectedToLocalServer()) {
                  return new ServerListEntrySnapshot("local:localhost", "Local Server", "localhost", null);
               }
            } catch (Throwable var8) {
            }

            ServerInfo var1 = null;

            try {
               var1 = var0.getCurrentServerEntry();
            } catch (Throwable var7) {
            }

            if (var1 == null) {
               try {
                  var1 = var0.getNetworkHandler().getServerInfo();
               } catch (Throwable var6) {
               }
            }

            if (var1 != null) {
               String var2 = compute(var1.address);
               if (!var2.isBlank()) {
                  String var10 = apply(var1.name).trim();
                  if (var10.isBlank()) {
                     var10 = update(var2);
                  }

                  byte[] var4 = var1.getFavicon();
                  return new ServerListEntrySnapshot("server:" + process(var2), var10, var2, var4 == null ? null : Arrays.copyOf(var4, var4.length));
               }
            }

            try {
               SocketAddress var9 = var0.getNetworkHandler().getConnection().getAddress();
               String var3 = var9 == null ? "" : compute(var9.toString());
               if (!var3.isBlank()) {
                  return new ServerListEntrySnapshot("server:" + process(var3), update(var3), var3, null);
               }
            } catch (Throwable var5) {
            }

            return null;
         } else {
            return null;
         }
      }

      private static String compute(MinecraftClient var0) {
         try {
            return var0 != null && var0.getSession() != null ? AltVaultScreen.check(var0.getSession().getUsername()) : "";
         } catch (Throwable var2) {
            return "";
         }
      }

      private static AltVaultScreen.PrimaryAccountType resolve(MinecraftClient var0) {
         try {
            if (var0 != null && var0.getSession() != null && var0.getSession().getAccountType() != net.minecraft.client.session.Session.AccountType.LEGACY) {
               return AltVaultScreen.PrimaryAccountType.PREMIUM;
            }
         } catch (Throwable var2) {
         }

         return AltVaultScreen.PrimaryAccountType.CRACKED;
      }

      private static void process() {
         if (!cache) {
            cache = true;
            File var0 = compute();
            if (var0 != null && var0.exists() && var0.isFile()) {
               try {
                  JsonElement var1 = JsonParser.parseString(Files.readString(var0.toPath(), StandardCharsets.UTF_8));
                  if (var1 == null || !var1.isJsonObject()) {
                     return;
                  }

                  JsonObject var2 = var1.getAsJsonObject();
                  JsonElement var3 = var2.get("accounts");
                  if (var3 == null || !var3.isJsonArray()) {
                     return;
                  }

                  for (JsonElement var5 : var3.getAsJsonArray()) {
                     if (var5.isJsonObject()) {
                        JsonObject var6 = var5.getAsJsonObject();
                        String var7 = handle(var6, "id", "");
                        String var8 = AltVaultScreen.check(handle(var6, "name", ""));
                        if (!var7.isBlank()) {
                           ServerStatusCache var9 = new ServerStatusCache(var7, var8);
                           JsonElement var10 = var6.get("servers");
                           if (var10 != null && var10.isJsonArray()) {
                              for (JsonElement var12 : var10.getAsJsonArray()) {
                                 if (var12.isJsonObject()) {
                                    JsonObject var13 = var12.getAsJsonObject();
                                    String var14 = process(handle(var13, "key", ""));
                                    String var15 = compute(handle(var13, "address", ""));
                                    String var16 = apply(handle(var13, "name", "")).trim();
                                    byte[] var17 = resolve(handle(var13, "favicon", ""));
                                    long var18 = Math.max(0L, handle(var13, "totalMs", 0L));
                                    long var20 = Math.max(0L, handle(var13, "lastActiveAt", 0L));
                                    if (!var14.isBlank() && var18 > 0L) {
                                       ServerStatusRecord var22 = new ServerStatusRecord(var14, var16, var15);
                                       var22.config = var17;
                                       var22.state = var18;
                                       var22.cache = var20;
                                       var9.context.put(var14, var22);
                                    }
                                 }
                              }
                           }

                           if (!var9.context.isEmpty()) {
                              config.put(var7, var9);
                           }
                        }
                     }
                  }
               } catch (Throwable var23) {
               }
            }
         }
      }

      private static void handle(long var0, boolean var2) {
         if (current && (var2 || var0 - enabled >= 10000L)) {
            File var3 = compute();
            if (var3 != null) {
               try {
                  File var4 = var3.getParentFile();
                  if (var4 != null) {
                     var4.mkdirs();
                  }

                  JsonObject var5 = new JsonObject();
                  var5.addProperty("version", 1);
                  var5.addProperty("updatedAt", var0);
                  JsonArray var6 = new JsonArray();

                  for (ServerStatusCache var8 : config.values()) {
                     if (!var8.instance.isBlank() && !var8.context.isEmpty()) {
                        JsonObject var9 = new JsonObject();
                        var9.addProperty("id", var8.instance);
                        var9.addProperty("name", var8.data);
                        JsonArray var10 = new JsonArray();

                        for (ServerStatusRecord var12 : var8.context.values()) {
                           if (var12.state > 0L) {
                              JsonObject var13 = new JsonObject();
                              var13.addProperty("key", var12.instance);
                              var13.addProperty("name", var12.data);
                              var13.addProperty("address", var12.context);
                              if (var12.config != null && var12.config.length > 0) {
                                 var13.addProperty("favicon", Base64.getEncoder().encodeToString(var12.config));
                              }

                              var13.addProperty("totalMs", var12.state);
                              var13.addProperty("lastActiveAt", var12.cache);
                              var10.add(var13);
                           }
                        }

                        if (var10.size() > 0) {
                           var9.add("servers", var10);
                           var6.add(var9);
                        }
                     }
                  }

                  var5.add("accounts", var6);
                  Files.writeString(var3.toPath(), instance.toJson(var5), StandardCharsets.UTF_8);
                  current = false;
                  enabled = var0;
               } catch (Throwable var14) {
               }
            }
         }
      }

      private static File compute() {
         try {
            File var0 = WildClient.instance != null ? WildClient.instance.cache : new File(MinecraftClient.getInstance().runDirectory, "Wild");
            return new File(var0, "account_server_stats.json");
         } catch (Throwable var1) {
            return null;
         }
      }

      private static byte[] handle(String var0) {
         resolve();
         byte[] var1 = state.get(process(var0));
         return var1 == null ? null : Arrays.copyOf(var1, var1.length);
      }

      private static void resolve() {
         if (!output) {
            output = true;

            try {
               MinecraftClient var0 = MinecraftClient.getInstance();
               ServerList var1 = new ServerList(var0);
               var1.loadFile();
               int var2 = var1.size();

               for (int var3 = 0; var3 < var2; var3++) {
                  ServerInfo var4 = var1.get(var3);
                  if (var4 != null && var4.address != null && !var4.address.isBlank()) {
                     byte[] var5 = var4.getFavicon();
                     if (var5 != null && var5.length != 0) {
                        state.put(process(var4.address), Arrays.copyOf(var5, var5.length));
                     }
                  }
               }
            } catch (Throwable var6) {
            }
         }
      }

      private static String process(String var0) {
         return compute(var0).toLowerCase(Locale.ROOT);
      }

      private static String compute(String var0) {
         String var1 = apply(var0).trim();
         if (var1.startsWith("/")) {
            var1 = var1.substring(1);
         }

         int var2 = var1.indexOf("<unresolved>");
         if (var2 >= 0) {
            var1 = var1.substring(0, var2) + var1.substring(var2 + "<unresolved>".length());
         }

         return var1.trim();
      }

      private static byte[] resolve(String var0) {
         String var1 = apply(var0).trim();
         if (var1.isBlank()) {
            return null;
         }

         try {
            return Base64.getDecoder().decode(var1);
         } catch (Throwable var3) {
            return null;
         }
      }

      static String update(String var0) {
         String var1 = compute(var0);
         int var2 = var1.indexOf(47);
         if (var2 >= 0 && var2 + 1 < var1.length()) {
            var1 = var1.substring(var2 + 1);
         }

         return var1.isBlank() ? "Server" : var1;
      }

      private static String handle(JsonObject var0, String var1, String var2) {
         try {
            JsonElement var3 = var0.get(var1);
            return var3 != null && !var3.isJsonNull() ? var3.getAsString() : var2;
         } catch (Throwable var4) {
            return var2;
         }
      }

      private static long handle(JsonObject var0, String var1, long var2) {
         try {
            JsonElement var4 = var0.get(var1);
            return var4 != null && !var4.isJsonNull() ? var4.getAsLong() : var2;
         } catch (Throwable var5) {
            return var2;
         }
      }

      static String apply(String var0) {
         return var0 == null ? "" : var0;
      }
   }

   static final class FileEntry {
      final String instance;
      final String data;
      final AltVaultScreen.PrimaryAccountType context;
      final boolean config;
      final GameProfile state;
      final String cache;
      final DampedOscillator output = new DampedOscillator(MotionSpringPresets.vectorMatch);
      final DampedOscillator current = new DampedOscillator(MotionSpringPresets.mode);
      String active = "";
      long mode = Long.MIN_VALUE;
      int selection = -1;
      final float[] enabled = new float[3];
      final float[] renderer = new float[3];
      final long handler;
      long animationDraw;
      Identifier pointEncode;
      int animator;
      boolean source;
      float target;
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
      float eventAttach;
      float serverRead = 1.0F;
      float positionAdvance = 0.5F;
      float frameCheck = 0.5F;
      float moduleCollect;
      float providerClose;
      float presetSave;
      float windowConvert;
      float presetWrite;
      float colorMeasure = 1.0F;
      float animationSchedule = 1.0F;
      float rendererScan = 1.0F;
      float sourceBuild;
      float outputCollapse;
      float profileInvoke;
      float sourceSchedule;
      float timerRender;
      float scaleSave;
      boolean colorCompute;
      boolean scaleAdapt;
      boolean textureRun;
      boolean indexBind;
      boolean actionRead = true;
      boolean configCollapse;
      boolean dataValidate;
      boolean scaleRender;
      boolean clientRefresh;
      boolean keyFilter;
      long requestAdapt;
      AltVaultScreen.ServerEntry timerMeasure;

      FileEntry(String var1, AltVaultScreen.PrimaryAccountType var2, boolean var3, float var4, String var5, long var6, long var8) {
         this.instance = var5;
         this.data = var1;
         this.context = var2;
         this.config = var3;
         this.handler = var6;
         this.animationDraw = var8;
         this.target = var4;
         this.state = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + var1).getBytes(StandardCharsets.UTF_8)), var1);
         this.cache = var1.toLowerCase(Locale.ROOT);
         this.current.handle(0.0F);
      }

      boolean handle(float var1, float var2) {
         return AltVaultScreen.handle(var1, var2, this.pending, this.previous, this.matrixBlend, this.vectorMatch, this.itemProject) <= 0.0F;
      }
   }

   static final class NetworkState extends AltVaultScreen.PrimaryAnimationState {
      final boolean target;
      final DampedOscillator pending = new DampedOscillator(MotionSpringPresets.vectorMatch);
      String previous = "";
      int latest;
      boolean summary;
      boolean matrixBlend;
      float vectorMatch;
      float itemProject;
      float responseCompute;
      float providerFetch;

      NetworkState(String var1, boolean var2) {
         super(var1);
         this.target = var2;
      }

      void handle(String var1) {
         if (this.matrixBlend) {
            this.previous = "";
            this.latest = 0;
            this.matrixBlend = false;
         }

         String var2 = var1.replaceAll("[^A-Za-z0-9_]", "");
         if (!var2.isEmpty()) {
            int var3 = 16 - this.previous.length();
            if (var3 > 0) {
               if (var2.length() > var3) {
                  var2 = var2.substring(0, var3);
               }

               this.previous = this.previous.substring(0, this.latest) + var2 + this.previous.substring(this.latest);
               this.latest = this.latest + var2.length();
            }
         }
      }

      void handle(char var1) {
         this.handle(String.valueOf(var1));
      }

      void process() {
         if (this.matrixBlend) {
            this.compute();
         } else if (this.latest > 0 && !this.previous.isEmpty()) {
            this.previous = this.previous.substring(0, this.latest - 1) + this.previous.substring(this.latest);
            this.latest--;
         }
      }

      void compute() {
         this.previous = "";
         this.latest = 0;
         this.itemProject = this.responseCompute = 0.0F;
         this.matrixBlend = false;
      }

      void resolve() {
         this.handle();
         this.summary = false;
         this.matrixBlend = false;
         this.vectorMatch = this.responseCompute = 0.0F;
         this.latest = AltVaultScreen.handle(this.latest, 0, this.previous.length());
         this.providerFetch = this.previous.isBlank() ? 0.0F : 1.0F;
         this.pending.handle(this.providerFetch);
      }
   }

   enum PrimaryAccountType {
      PREMIUM,
      CRACKED;

      static AltVaultScreen.PrimaryAccountType handle(String var0) {
         if (var0 == null) {
            return CRACKED;
         }

         try {
            return valueOf(var0.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return CRACKED;
         }
      }
   }

   static class PrimaryAnimationState {
      protected String instance;
      protected float data;
      protected float context;
      protected float config;
      protected float state;
      protected float cache;
      protected float output;
      protected float current;
      protected float active;
      protected float mode;
      protected float selection;
      protected float enabled;
      protected float renderer = 1.0F;
      protected float handler = 0.5F;
      protected float animationDraw = 0.5F;
      protected float pointEncode;
      protected float animator;
      protected float source;

      protected PrimaryAnimationState(String var1) {
         this.instance = var1;
      }

      protected boolean handle(float var1, float var2) {
         return AltVaultScreen.handle(var1, var2, this.data, this.context, this.cache, this.output, this.current) <= 0.0F;
      }

      protected void handle() {
         this.active = this.mode = this.selection = this.enabled = this.pointEncode = this.source = 0.0F;
         this.renderer = 1.0F;
         this.handler = this.animationDraw = 0.5F;
      }
   }

   static final class PrimaryNetworkState extends AltVaultScreen.PrimaryAnimationState {
      final AltVaultScreen.Action target;
      private final AltVaultScreen.AccountType pending;
      boolean previous = true;
      float latest;

      PrimaryNetworkState(String var1, AltVaultScreen.Action var2, AltVaultScreen.AccountType var3) {
         super(var1);
         this.target = var2;
         this.pending = var3;
      }

      @Override
      protected void handle() {
         super.handle();
         this.latest = 0.0F;
      }
   }

   static final class SecondaryNetworkState {
      float instance;
      float data;
      float context = -100.0F;
      float config;
   }

   record ServerEntry(String displayName, String address, byte[] favicon, long totalMs, long lastActiveAt) {
   }
}
