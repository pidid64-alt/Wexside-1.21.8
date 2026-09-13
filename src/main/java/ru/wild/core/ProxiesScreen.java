package ru.wild.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.network.ServerInfo.Status;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;
import ru.wild.WildClient;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.render.MainMenuBackgroundRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.SmoothedValue;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class ProxiesScreen extends Screen implements FrameRenderListener {
   private static final ThemePaletteRegistry instance = ThemePaletteRegistry.handle();
   private static final int data = 14;
   private static final long context = 140L;
   private static final long config = 70L;
   private static final ThreadFactory state = var0 -> {
      Thread var1 = new Thread(var0, "Wild Server Ping");
      var1.setDaemon(true);
      return var1;
   };
   private final Screen cache;
   private final MainMenuBackgroundRenderer output = new MainMenuBackgroundRenderer();
   private final WildMainMenuScreen.PrimaryNetworkState current = new WildMainMenuScreen.PrimaryNetworkState(24, 14);
   private final OpenGlStateSnapshot.NetworkState active = new OpenGlStateSnapshot.NetworkState();
   private MultiplayerServerListPinger mode = new MultiplayerServerListPinger();
   private final List<ServerInfo> selection = new ArrayList<>();
   private final List<ProxiesScreen.NetworkState> enabled = new ArrayList<>();
   private final Map<String, ProxiesScreen.TextureState> renderer = new HashMap<>();
   private final List<ProxiesScreen.NetworkState> handler = List.of(
      new ProxiesScreen.NetworkState("Join", ProxiesScreen.Action.JOIN),
      new ProxiesScreen.NetworkState("Direct", ProxiesScreen.Action.DIRECT),
      new ProxiesScreen.NetworkState("Add", ProxiesScreen.Action.ADD),
      new ProxiesScreen.NetworkState("Edit", ProxiesScreen.Action.EDIT),
      new ProxiesScreen.NetworkState("Delete", ProxiesScreen.Action.DELETE),
      new ProxiesScreen.NetworkState("Proxy", ProxiesScreen.Action.PROXY),
      new ProxiesScreen.NetworkState("Refresh", ProxiesScreen.Action.REFRESH),
      new ProxiesScreen.NetworkState("Back", ProxiesScreen.Action.BACK)
   );
   private final ProxiesScreen.State[] animationDraw = new ProxiesScreen.State[14];
   private final SmoothedValue pointEncode = new SmoothedValue(SpringAnimationSpec.update());
   private final SmoothedValue animator = new SmoothedValue(SpringAnimationSpec.update());
   private ServerList source;
   private long target;
   private long pending;
   private long previous;
   private long latest;
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
   private boolean frameCheck;
   private boolean moduleCollect;
   private boolean providerClose;
   private int presetSave;
   private int windowConvert;
   private int presetWrite = -6357021;
   private int colorMeasure = -11341636;
   private ThemePalette animationSchedule = ThemePalette.AURORA;
   private boolean rendererScan;
   private int sourceBuild = -1;
   private float outputCollapse;
   private float profileInvoke;
   private int sourceSchedule = 5;
   private int timerRender = -1;
   private String scaleSave = "Choose a server";
   private volatile ScheduledExecutorService colorCompute;
   private final AtomicInteger scaleAdapt = new AtomicInteger();
   private volatile int textureRun;
   private final AtomicInteger indexBind = new AtomicInteger();
   private volatile int actionRead;
   private float configCollapse = -100.0F;
   private long dataValidate;
   private float scaleRender;
   private float clientRefresh;
   private float keyFilter;
   private float requestAdapt;
   private boolean timerMeasure;
   private float vectorEncode;
   private float requestReceive;
   private float windowProcess;
   private float packetSave;
   private float entryAnimate;
   private float playerCollect;
   private float stateApply;
   private boolean matrixFilter;
   private final AtomicBoolean layerSample = new AtomicBoolean(false);

   public ProxiesScreen(Screen var1) {
      super(Text.literal("Wild Multiplayer"));
      this.cache = var1;

      for (int var2 = 0; var2 < this.animationDraw.length; var2++) {
         this.animationDraw[var2] = new ProxiesScreen.State();
      }
   }

   protected void init() {
      super.init();
      this.target = System.nanoTime();
      this.pending = this.target;
      this.previous = this.target;
      this.frameCheck = false;
      this.moduleCollect = false;
      this.providerClose = false;
      this.presetSave = 0;
      this.windowConvert = 0;
      this.outputCollapse = 0.0F;
      this.profileInvoke = 0.0F;
      this.process(true);
      this.pointEncode.handle(0.0F);
      this.animator.handle(0.0F);

      for (ProxiesScreen.NetworkState var2 : this.enabled) {
         var2.handle();
      }

      for (ProxiesScreen.NetworkState var4 : this.handler) {
         var4.handle();
      }

      this.process();
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      this.handle(var2, var3, var4, false);
   }

   @Override
   public void handle(int var1, int var2, float var3) {
      this.handle(var1, var2, var3, true);
   }

   public void tick() {
      super.tick();
   }
   private void handle(int var1, int var2, float var3, boolean var4) {
      Window var5 = this.client == null ? null : this.client.getWindow();
      if (var5 != null && !var5.hasZeroWidthOrHeight() && var5.getFramebufferWidth() > 0 && var5.getFramebufferHeight() > 0) {
         int var6 = var5.getFramebufferWidth();
         int var7 = var5.getFramebufferHeight();
         long var8 = System.nanoTime();
         float var10 = Math.max(0.001F, Math.min(0.05F, (float)(var8 - this.pending) / 1.0E9F));
         this.pending = var8;
         this.summary = (float)(var8 - this.target) / 1.0E9F;
         if (this.handle(var5, var6, var7, var1, var2, var8)) {
            var10 = 0.001F;
         }

         this.execute();
         this.handle(var5, var1, var2, var10, var8);
         this.process(var6, var7, var10);
         this.prepare();
         float var11 = (this.matrixBlend / Math.max(1.0F, var6) - 0.5F) * 2.0F;
         float var12 = (this.vectorMatch / Math.max(1.0F, var7) - 0.5F) * 2.0F;
         float var13 = this.pointEncode.handle(var11, var10);
         float var14 = this.animator.handle(var12, var10);
         this.handle(var6, var7, var13, var14, var10);
         int var15 = GL11.glGetInteger(36006);
         this.handle(var6, var7, var15, var13, var14, var8);
         if (var4) {
            OpenGlStateSnapshot.process(this.active);
            boolean var18 = false /* VF: Semaphore variable */;

            try {
               var18 = true;
               this.output.handle(this.current);
               var18 = false;
            } finally {
               if (var18) {
                  OpenGlStateSnapshot.compute(this.active);
               }
            }

            OpenGlStateSnapshot.compute(this.active);
            this.handle(this.current);
         }
      }
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 && this.client != null && this.client.getWindow() != null) {
         float var6 = this.handle(this.client.getWindow(), var1);
         float var7 = this.process(this.client.getWindow(), var3);
         long var8 = System.nanoTime();
         if (this.matrixFilter) {
            float var10 = 8.0F;
            if (var6 >= this.requestReceive - var10
               && var6 <= this.requestReceive + this.packetSave + var10
               && var7 >= this.windowProcess
               && var7 <= this.windowProcess + this.entryAnimate) {
               this.timerMeasure = true;
               if (var7 >= this.playerCollect && var7 <= this.playerCollect + this.stateApply) {
                  this.vectorEncode = var7 - this.playerCollect;
               } else {
                  this.vectorEncode = this.stateApply * 0.5F;
               }

               this.handle(var7);
               return true;
            }
         }

         for (ProxiesScreen.NetworkState var11 : this.handler) {
            if (var11.vectorMatch && var11.itemProject && var11.handle(var6, var7)) {
               var11.handler = 1.0F;
               var11.animationDraw = 1.0F;
               this.handle(var11.context);
               return true;
            }
         }

         for (ProxiesScreen.NetworkState var14 : this.enabled) {
            if (var14.vectorMatch && var14.itemProject && var14.context == ProxiesScreen.Action.SERVER && var14.handle(var6, var7) && !(var14.latest < 0.1F)) {
               if (this.sourceBuild == var14.summary && this.timerRender == var14.summary && var8 - this.latest < 360000000L) {
                  var14.handler = 1.0F;
                  var14.animationDraw = 1.0F;
                  this.handle(ProxiesScreen.Action.JOIN);
               } else {
                  this.sourceBuild = var14.summary;
                  this.scaleSave = "Ready";
                  var14.animationDraw = Math.max(var14.animationDraw, 0.38F);
               }

               this.timerRender = var14.summary;
               this.latest = var8;
               this.tick2();
               return true;
            }
         }

         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.selection.size() <= this.sourceSchedule) {
         return true;
      }

      this.dataValidate = System.nanoTime();
      this.outputCollapse -= (float)var7;
      int var9 = Math.max(0, this.selection.size() - Math.max(1, this.sourceSchedule));
      this.outputCollapse = process(this.outputCollapse, 0.0F, var9);
      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.timerMeasure && this.matrixFilter && this.client != null && this.client.getWindow() != null) {
         this.handle(this.process(this.client.getWindow(), var3));
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0 && this.timerMeasure) {
         this.timerMeasure = false;
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   private void handle(float var1) {
      float var2 = this.entryAnimate - this.stateApply;
      if (!(var2 <= 0.001F)) {
         float var3 = process(var1 - this.vectorEncode, this.windowProcess, this.windowProcess + var2);
         float var4 = (var3 - this.windowProcess) / var2;
         int var5 = Math.max(0, this.selection.size() - Math.max(1, this.sourceSchedule));
         this.dataValidate = System.nanoTime();
         this.outputCollapse = var4 * var5;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = (var3 & 2) != 0 || (var3 & 8) != 0;
      if (var1 == 256) {
         this.handle(ProxiesScreen.Action.BACK);
         return true;
      }

      if (var1 == 257 || var1 == 335) {
         this.handle(ProxiesScreen.Action.JOIN);
         return true;
      }

      if (var4 && var1 == 67) {
         this.render2();
         return true;
      }

      if (var1 == 82) {
         this.handle(ProxiesScreen.Action.REFRESH);
         return true;
      }

      if (var1 == 261) {
         this.handle(ProxiesScreen.Action.DELETE);
         return true;
      }

      if (var1 == 264) {
         if (var4) {
            this.compute(1);
         } else {
            this.resolve(1);
         }

         return true;
      } else if (var1 == 265) {
         if (var4) {
            this.compute(-1);
         } else {
            this.resolve(-1);
         }

         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (!this.selection.isEmpty() && var1 > ' ') {
         char var3 = Character.toLowerCase(var1);
         int var4 = this.sourceBuild < 0 ? -1 : this.sourceBuild;
         int var5 = this.selection.size();

         for (int var6 = 1; var6 <= var5; var6++) {
            int var7 = ((var4 + var6) % var5 + var5) % var5;
            ServerInfo var8 = this.selection.get(var7);
            String var9 = var8 == null ? "" : handle(var8.name, "");
            if (!var9.isEmpty() && Character.toLowerCase(var9.charAt(0)) == var3) {
               this.sourceBuild = var7;
               this.scaleSave = "Jumped to " + var9;
               this.tick2();
               return true;
            }
         }

         return true;
      } else {
         return super.charTyped(var1, var2);
      }
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void close() {
      this.handle(ProxiesScreen.Action.BACK);
   }

   public void removed() {
      this.process(true);
      this.encodePoint();
      this.output.close();
      super.removed();
   }

   private void process() {
      MinecraftClient var1 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var1 != null) {
         if (this.layerSample.compareAndSet(false, true)) {
            this.process(true);
            this.encodePoint();
            this.selection.clear();
            this.scaleSave = "Loading servers...";
            ServerList var2 = new ServerList(var1);
            CompletableFuture.runAsync(() -> {
               try {
                  var2.loadFile();
               } catch (Throwable var2x) {
               }
            }).whenComplete((var3, var4) -> var1.execute(() -> this.handle(var2, var4)));
         }
      }
   }

   private void handle(ServerList var1, Throwable var2) {
      try {
         this.source = var1;
         this.selection.clear();

         try {
            int var3 = var1 == null ? 0 : var1.size();

            for (int var4 = 0; var4 < var3; var4++) {
               ServerInfo var5 = var1.get(var4);
               if (var5 != null) {
                  this.selection.add(var5);
               }
            }
         } catch (Throwable var9) {
         }

         if (var2 != null) {
            this.scaleSave = "Failed to load servers";
         }

         if (this.selection.isEmpty()) {
            this.sourceBuild = -1;
            this.outputCollapse = 0.0F;
            this.profileInvoke = 0.0F;
            if (var2 == null) {
               this.scaleSave = "No saved servers";
            }
         } else {
            if (this.sourceBuild < 0 || this.sourceBuild >= this.selection.size()) {
               this.sourceBuild = 0;
            }

            this.outputCollapse = process(this.outputCollapse, 0.0F, Math.max(0, this.selection.size() - this.sourceSchedule));
            this.tick2();
            if (var2 == null) {
               this.scaleSave = "Choose a server";
            }

            this.handle(false);
         }
      } finally {
         this.layerSample.set(false);
      }
   }

   private void compute() {
      if (this.source != null) {
         try {
            this.source.saveFile();
         } catch (Throwable var2) {
         }
      }
   }

   private void handle(boolean var1) {
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var2 != null) {
         int var3 = ++this.actionRead;
         this.process(false);
         ArrayList var4 = new ArrayList<>(this.selection);
         this.scaleAdapt.set(0);
         this.textureRun = var4.size();
         this.indexBind.set(0);
         if (var1) {
            this.apply();
         }

         this.scaleSave = var1 ? "Refreshing servers..." : "Pinging servers...";
         this.handle(var2, var4, var3);
      }
   }

   private void handle(MinecraftClient var1, List<ServerInfo> var2, int var3) {
      if (var2.isEmpty()) {
         this.resolve();
      } else {
         MultiplayerServerListPinger var4 = this.mode;
         ScheduledExecutorService var5 = Executors.newSingleThreadScheduledExecutor(state);
         this.colorCompute = var5;
         var5.scheduleWithFixedDelay(() -> this.handle(var1, var4, var2, var3, var5), 140L, 70L, TimeUnit.MILLISECONDS);
      }
   }

   private void handle(MinecraftClient var1, MultiplayerServerListPinger var2, List<ServerInfo> var3, int var4, ScheduledExecutorService var5) {
      if (var4 == this.actionRead && !var5.isShutdown()) {
         try {
            if (this.scaleAdapt.get() < var3.size()) {
               int var6 = this.scaleAdapt.getAndIncrement();
               ServerInfo var7 = var6 < var3.size() ? (ServerInfo)var3.get(var6) : null;
               if (var7 == null) {
                  this.scaleAdapt.set(var3.size());
               } else {
                  this.handle(var1, var2, var7, var4);
               }
            }

            var2.tick();
            if (this.scaleAdapt.get() >= this.textureRun && this.indexBind.get() <= 0) {
               var1.execute(this::resolve);
               handle(var2);
               var5.shutdown();
               if (this.colorCompute == var5) {
                  this.colorCompute = null;
               }
            }
         } catch (Throwable var8) {
         }
      } else {
         handle(var2);
         var5.shutdown();
      }
   }

   private void handle(MinecraftClient var1, MultiplayerServerListPinger var2, ServerInfo var3, int var4) {
      this.indexBind.incrementAndGet();

      try {
         var1.execute(() -> this.handle(var3, var4));
         var2.add(var3, () -> var1.execute(() -> this.process(var3, var4)), () -> var1.execute(() -> this.compute(var3, var4)));
      } catch (Throwable var6) {
         var1.execute(() -> this.compute(var3, var4));
      }
   }

   private void handle(ServerInfo var1, int var2) {
      if (var2 == this.actionRead) {
         var1.setStatus(Status.PINGING);
         var1.playerCountLabel = Text.literal("...");
      }
   }

   private void process(ServerInfo var1, int var2) {
      if (var2 == this.actionRead) {
         CompletableFuture.runAsync(() -> {
            try {
               ServerList.updateServerListEntry(var1);
            } catch (Throwable var2x) {
            }
         }, Util.getMainWorkerExecutor());
         this.handle(var2);
      }
   }

   private void compute(ServerInfo var1, int var2) {
      if (var2 == this.actionRead) {
         var1.ping = -1L;
         var1.setStatus(Status.UNREACHABLE);
         if (var1.label == null || var1.label.getString().isBlank()) {
            var1.label = Text.literal("Cannot reach server");
         }

         var1.playerCountLabel = Text.literal("-");
         this.handle(var2);
      }
   }

   private void handle(int var1) {
      if (var1 == this.actionRead) {
         this.indexBind.updateAndGet(var0 -> Math.max(0, var0 - 1));
         this.resolve();
      }
   }

   private void resolve() {
      if (this.scaleAdapt.get() >= this.textureRun && this.indexBind.get() <= 0) {
         if (!this.selection.isEmpty()) {
            this.scaleSave = "Servers updated";
         }
      }
   }

   private void process(boolean var1) {
      if (var1) {
         this.actionRead++;
      }

      ScheduledExecutorService var2 = this.colorCompute;
      this.colorCompute = null;
      MultiplayerServerListPinger var3 = this.mode;
      this.mode = new MultiplayerServerListPinger();
      this.scaleAdapt.set(0);
      this.textureRun = 0;
      this.indexBind.set(0);
      if (var2 != null) {
         var2.shutdownNow();
      }

      CompletableFuture.runAsync(() -> handle(var3), Util.getMainWorkerExecutor());
   }

   private static void handle(MultiplayerServerListPinger var0) {
      try {
         var0.cancel();
      } catch (Throwable var2) {
      }
   }

   private void update() {
      if (this.selection.isEmpty()) {
         this.apply();
         this.process();
         this.scaleSave = "Refreshing servers...";
      } else {
         this.handle(true);
      }
   }

   private void apply() {
      this.configCollapse = this.summary;

      for (ProxiesScreen.NetworkState var2 : this.enabled) {
         if (var2.vectorMatch) {
            var2.animationDraw = Math.max(var2.animationDraw, 0.72F);
            var2.handler = Math.max(var2.handler, 0.16F);
            var2.pointEncode = Math.max(var2.pointEncode, 0.65F);
         }
      }

      for (ProxiesScreen.NetworkState var4 : this.handler) {
         if (var4.context == ProxiesScreen.Action.REFRESH) {
            var4.animationDraw = Math.max(var4.animationDraw, 1.0F);
            var4.handler = Math.max(var4.handler, 0.18F);
            break;
         }
      }
   }

   private void execute() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.AURORA;
      this.animationSchedule = var1;
      this.rendererScan = instance.compute(var1);
      this.presetWrite = instance.resolve(var1);
      this.colorMeasure = instance.update(var1);
   }

   private void handle(Window var1, int var2, int var3, float var4, long var5) {
      float var7 = this.handle(var1, var2);
      float var8 = this.process(var1, var3);
      if (!this.frameCheck) {
         this.matrixBlend = var7;
         this.vectorMatch = var8;
         this.itemProject = 0.0F;
         this.responseCompute = 0.0F;
         this.frameCheck = true;
      } else {
         float var9 = var7 - this.matrixBlend;
         float var10 = var8 - this.vectorMatch;
         float var11 = process(var9, var10);
         if (var11 > 0.2F) {
            this.itemProject = process(var9 / Math.max(1.0F, var1.getFramebufferWidth()) / var4, -3.0F, 3.0F);
            this.responseCompute = process(var10 / Math.max(1.0F, var1.getFramebufferHeight()) / var4, -3.0F, 3.0F);
         } else {
            float var12 = (float)Math.pow(8.0E-4F, var4);
            this.itemProject *= var12;
            this.responseCompute *= var12;
         }

         this.matrixBlend = var7;
         this.vectorMatch = var8;
         if (var11 > 1.5F) {
            this.previous = var5;
         }
      }
   }

   private void process(int var1, int var2, float var3) {
      if (!this.moduleCollect) {
         this.providerFetch = this.matrixBlend;
         this.profileDraw = this.vectorMatch;
         this.vectorPerform = 0.0F;
         this.eventAttach = 0.0F;
         this.moduleCollect = true;
      } else {
         float var4 = this.providerFetch;
         float var5 = this.profileDraw;
         float var6 = process(this.matrixBlend - this.providerFetch, this.vectorMatch - this.profileDraw);
         float var7 = (1.0F - (float)Math.pow(1.8E-5F, var3)) * (0.62F + process(var6 / 680.0F, 0.0F, 0.32F));
         this.providerFetch = this.providerFetch + (this.matrixBlend - this.providerFetch) * process(var7, 0.035F, 0.18F);
         this.profileDraw = this.profileDraw + (this.vectorMatch - this.profileDraw) * process(var7, 0.035F, 0.18F);
         float var8 = process((this.providerFetch - var4) / Math.max(1.0F, var1) / var3, -1.35F, 1.35F);
         float var9 = process((this.profileDraw - var5) / Math.max(1.0F, var2) / var3, -1.35F, 1.35F);
         float var10 = 1.0F - (float)Math.pow(0.004F, var3);
         this.vectorPerform = this.vectorPerform + (var8 - this.vectorPerform) * var10;
         this.eventAttach = this.eventAttach + (var9 - this.eventAttach) * var10;
      }
   }

   private void prepare() {
      if (!this.providerClose) {
         this.serverRead = this.providerFetch;
         this.positionAdvance = this.profileDraw;
         this.providerClose = true;
         this.handle(this.providerFetch, this.profileDraw, 0.24F);
      } else {
         float var1 = process(this.providerFetch - this.serverRead, this.profileDraw - this.positionAdvance);
         if (var1 > 8.5F) {
            this.handle(this.providerFetch, this.profileDraw, process(var1 / 240.0F, 0.08F, 0.38F));
            this.serverRead = this.providerFetch;
            this.positionAdvance = this.profileDraw;
         }
      }
   }

   private boolean handle(Window var1, int var2, int var3, int var4, int var5, long var6) {
      if (this.presetSave == var2 && this.windowConvert == var3) {
         return false;
      }

      this.presetSave = var2;
      this.windowConvert = var3;
      float var8 = process(this.handle(var1, var4), 0.0F, var2);
      float var9 = process(this.process(var1, var5), 0.0F, var3);
      this.matrixBlend = this.providerFetch = this.serverRead = var8;
      this.vectorMatch = this.profileDraw = this.positionAdvance = var9;
      this.itemProject = this.responseCompute = 0.0F;
      this.vectorPerform = this.eventAttach = 0.0F;
      this.frameCheck = true;
      this.moduleCollect = true;
      this.providerClose = true;
      this.previous = var6;
      this.timerMeasure = false;
      this.pointEncode.handle(0.0F);
      this.animator.handle(0.0F);
      this.profileInvoke = this.outputCollapse;
      this.check();
      this.handle(var8, var9, 0.14F);
      this.tick2();
      return true;
   }

   private void check() {
      for (ProxiesScreen.State var4 : this.animationDraw) {
         var4.instance = 0.0F;
         var4.data = 0.0F;
         var4.context = -100.0F;
         var4.config = 0.0F;
      }
   }

   private void handle(float var1, float var2, float var3) {
      int var4 = 0;
      float var5 = -1.0F;

      for (int var6 = 0; var6 < this.animationDraw.length; var6++) {
         float var7 = this.summary - this.animationDraw[var6].context;
         if (this.animationDraw[var6].config <= 0.0F) {
            var4 = var6;
            break;
         }

         if (var7 > var5) {
            var5 = var7;
            var4 = var6;
         }
      }

      this.animationDraw[var4].instance = var1;
      this.animationDraw[var4].data = var2;
      this.animationDraw[var4].context = this.summary;
      this.animationDraw[var4].config = var3;
   }

   private void handle(int var1, int var2, float var3, float var4, float var5) {
      float var6 = handle(var1, var2);
      float var7 = process(var1 * 0.38F, 520.0F * var6, 760.0F * var6);
      float var8 = process(var2 * 0.078F, 72.0F * var6, 94.0F * var6);
      float var9 = 14.0F * var6;
      this.sourceSchedule = Math.max(3, Math.min(6, (int)(var2 * 0.54F / (var8 + var9))));
      if (this.selection.size() < this.sourceSchedule && !this.selection.isEmpty()) {
         this.sourceSchedule = Math.max(1, this.selection.size());
      }

      int var10 = Math.max(0, this.selection.size() - Math.max(1, this.sourceSchedule));
      this.outputCollapse = process(this.outputCollapse, 0.0F, var10);
      float var11 = 1.0F - (float)Math.exp(-22.0F * var5);
      this.profileInvoke = this.profileInvoke + (this.outputCollapse - this.profileInvoke) * var11;
      if (Float.isNaN(this.profileInvoke)) {
         this.profileInvoke = this.outputCollapse;
      }

      float var12 = this.sourceSchedule * var8 + Math.max(0, this.sourceSchedule - 1) * var9;
      float var13 = var1 * 0.5F + var3 * 1.65F * var6;
      float var14 = var2 * 0.255F + var4 * 1.05F * var6;
      if (var14 + var12 > var2 * 0.79F) {
         var14 = var2 * 0.79F - var12;
      }

      var14 = Math.max(var2 * 0.18F, var14);
      this.scaleRender = var13 - var7 * 0.5F;
      this.clientRefresh = var14;
      this.keyFilter = var7;
      this.requestAdapt = var12;
      this.matrixFilter = var10 > 0;
      if (this.matrixFilter) {
         this.packetSave = Math.max(4.0F, 5.5F * var6);
         this.requestReceive = var13 + var7 * 0.5F + 16.0F * var6;
         this.windowProcess = var14;
         this.entryAnimate = var12;
         float var15 = process((float)this.sourceSchedule / this.selection.size(), 0.1F, 1.0F);
         this.stateApply = Math.max(34.0F * var6, this.entryAnimate * var15);
         float var16 = this.entryAnimate - this.stateApply;
         float var17 = var10 == 0 ? 0.0F : this.profileInvoke / var10;
         this.playerCollect = this.windowProcess + var16 * var17;
      }

      int var33 = (int)Math.floor(this.profileInvoke);
      float var34 = this.profileInvoke - var33;
      int var35 = this.selection.isEmpty() ? 1 : Math.min(this.selection.size(), this.sourceSchedule + 2);

      while (this.enabled.size() < var35) {
         this.enabled.add(new ProxiesScreen.NetworkState("", ProxiesScreen.Action.SERVER));
      }

      for (int var18 = 0; var18 < this.enabled.size(); var18++) {
         ProxiesScreen.NetworkState var19 = this.enabled.get(var18);
         if (var18 >= var35) {
            var19.vectorMatch = false;
         } else {
            var19.vectorMatch = true;
            var19.active = var7;
            var19.mode = var8;
            var19.state = var13 - var7 * 0.5F;
            var19.cache = var14 + (var18 - var34) * (var8 + var9);
            var19.selection = Math.min(var8 * 0.36F, 20.0F * var6);
            var19.previous = 58.0F * var6;
            var19.itemProject = !this.selection.isEmpty();
            var19.pointEncode = this.process(var18);
            if (this.selection.isEmpty()) {
               var19.instance = "No saved servers";
               var19.data = "Add a server or connect directly";
               var19.summary = -1;
               var19.matrixBlend = false;
               var19.latest = process(process((this.summary - 0.15F) / 0.92F, 0.0F, 1.0F));
            } else {
               int var20 = var33 + var18;
               ServerInfo var21 = var20 >= 0 && var20 < this.selection.size() ? this.selection.get(var20) : null;
               var19.summary = var20;
               var19.itemProject = var21 != null;
               var19.instance = var21 == null ? "" : handle(var21.name, "Unnamed server");
               var19.data = var21 == null ? "" : handle(var21.address, "No address");
               var19.matrixBlend = var20 == this.sourceBuild;
               float var22 = var19.cache + var8 * 0.5F;
               float var23 = var14;
               float var24 = var14 + var12;
               float var25 = var8 * 0.65F;
               float var26 = process((var22 - var23 + var25) / var25, 0.0F, 1.0F);
               float var27 = process((var24 + var25 - var22) / var25, 0.0F, 1.0F);
               float var28 = var26 * var27;
               var19.latest = process(process((this.summary - 0.15F - var18 * 0.045F) / 0.92F, 0.0F, 1.0F)) * var28;
               var19.animationDraw = Math.max(var19.animationDraw, var19.pointEncode * 0.34F);
            }

            this.handle(var19, var5, var6);
         }
      }

      float var36 = 10.0F * var6;
      float var37 = process(var1 * 0.08F, 95.0F * var6, 135.0F * var6);
      float var38 = 42.0F * var6;
      int var39 = Math.min(5, this.handler.size());
      int var40 = this.handler.size() - var39;
      float var41 = var39 * var37 + (var39 - 1) * var36;
      float var42 = var40 * var37 + (var40 - 1) * var36;
      float var43 = var1 * 0.5F - var41 * 0.5F + var3 * 1.35F * var6;
      float var44 = var1 * 0.5F - var42 * 0.5F + var3 * 1.35F * var6;
      float var45 = Math.min(var2 - var38 * 2.0F - var36 - 28.0F * var6, var14 + var12 + 24.0F * var6 + var4 * 0.45F * var6);

      for (int var46 = 0; var46 < this.handler.size(); var46++) {
         ProxiesScreen.NetworkState var29 = this.handler.get(var46);
         var29.vectorMatch = true;
         var29.active = var37;
         var29.mode = var38;
         boolean var30 = var46 < var39;
         int var31 = var30 ? var46 : var46 - var39;
         var29.state = (var30 ? var43 : var44) + var31 * (var37 + var36);
         var29.cache = var45 + (var30 ? 0.0F : var38 + var36);
         var29.selection = Math.min(var38 * 0.42F, 18.0F * var6);
         var29.previous = 42.0F * var6;
         var29.latest = process(process((this.summary - 0.38F - var46 * 0.035F) / 0.74F, 0.0F, 1.0F));
         var29.itemProject = this.process(var29.context);
         var29.matrixBlend = false;
         var29.pointEncode = var29.context == ProxiesScreen.Action.REFRESH ? this.process(0) : 0.0F;
         this.handle(var29, var5, var6);
      }
   }

   private float process(int var1) {
      float var2 = this.summary - this.configCollapse - var1 * 0.055F;
      if (!(var2 < 0.0F) && !(var2 > 0.86F)) {
         float var3 = process(var2 / 0.86F, 0.0F, 1.0F);
         return (float)Math.sin(var3 * Math.PI) * process(1.0F - var3 * 0.42F);
      } else {
         return 0.0F;
      }
   }

   private void handle(ProxiesScreen.NetworkState var1, float var2, float var3) {
      float var4 = handle(this.matrixBlend, this.vectorMatch, var1.state, var1.cache, var1.active, var1.mode, var1.selection);
      boolean var5 = var4 <= 0.0F;
      float var6 = var1.context == ProxiesScreen.Action.SERVER ? 42.0F * var3 : 24.0F * var3;
      float var7 = 1.0F - process(process(Math.max(0.0F, var4) / Math.max(1.0F, var6), 0.0F, 1.0F));
      float var8 = var1.matrixBlend ? 0.42F : 0.0F;
      float var9 = var1.itemProject ? Math.max(var7, var8) : 0.0F;
      float var10 = var1.itemProject && var5 ? 1.0F : var8 * 0.45F;
      var1.enabled = var1.enabled + (var10 - var1.enabled) * (1.0F - (float)Math.pow(1.1E-4F, var2));
      var1.renderer = var1.renderer + (var9 - var1.renderer) * (1.0F - (float)Math.pow(1.6E-4F, var2));
      var1.handler = var1.handler + (0.0F - var1.handler) * (1.0F - (float)Math.pow(1.8E-5F, var2));
      var1.animationDraw = var1.animationDraw + (0.0F - var1.animationDraw) * (1.0F - (float)Math.pow(6.0E-6F, var2));
      float var11 = process((this.providerFetch - var1.state) / Math.max(1.0F, var1.active), 0.0F, 1.0F);
      float var12 = process((this.profileDraw - var1.cache) / Math.max(1.0F, var1.mode), 0.0F, 1.0F);
      float var13 = 1.0F - (float)Math.pow(2.5E-4F, var2);
      var1.source = var1.source + (var11 - var1.source) * var13;
      var1.target = var1.target + (var12 - var1.target) * var13;
      float var14 = 1.0F
         + var1.renderer * (var1.context == ProxiesScreen.Action.SERVER ? 0.034F : 0.042F)
         + (var1.matrixBlend ? 0.008F : 0.0F)
         + var1.pointEncode * 0.018F
         - var1.handler * 0.065F;
      var1.animator = var1.config.handle(var14, var2);
      float var15 = (1.0F - var1.latest) * (var1.context == ProxiesScreen.Action.SERVER ? 18.0F : 11.0F) * var3;
      float var16 = (var1.source - 0.5F) * (var1.context == ProxiesScreen.Action.SERVER ? 9.5F : 6.5F) * var3 * var1.renderer;
      float var17 = (var1.target - 0.5F) * (var1.context == ProxiesScreen.Action.SERVER ? 5.5F : 4.0F) * var3 * var1.renderer
         - var1.enabled * 1.2F * var3
         + var15
         - var1.pointEncode * (var1.context == ProxiesScreen.Action.SERVER ? 5.0F : 2.5F) * var3;
      var1.output = var1.state + var16;
      var1.current = var1.cache + var17;
      var1.pending = process(
         process(this.vectorPerform, this.eventAttach) * 0.46F * var1.renderer + Math.abs(var1.config.process()) * 0.032F + var1.pointEncode * 0.22F,
         0.0F,
         1.0F
      );
   }

   private void handle(int var1, int var2, int var3, float var4, float var5, long var6) {
      float var8 = Math.max(0.0F, (float)(var6 - this.previous) / 1.0E9F);
      float var9 = process(process(this.vectorPerform, this.eventAttach), 0.0F, 3.0F);
      float var10 = Math.max((float)Math.exp(-var8 * 1.35F), process(var9 * 0.28F, 0.0F, 1.0F));
      float var11 = process(process(this.summary / 0.95F, 0.0F, 1.0F));
      float var12 = handle(var1, var2);
      float var13 = 0.0F;
      int var14 = 0;

      for (ProxiesScreen.NetworkState var16 : this.enabled) {
         if (var16.vectorMatch && !(var16.latest <= 0.01F)) {
            var13 = Math.max(var13, var16.animationDraw);
            this.handle(var14++, var16);
         }
      }

      for (ProxiesScreen.NetworkState var20 : this.handler) {
         if (var20.vectorMatch) {
            var13 = Math.max(var13, var20.animationDraw);
            this.handle(var14++, var20);
         }
      }

      this.current.check(var14);

      for (int var19 = 0; var19 < 14; var19++) {
         ProxiesScreen.State var21 = this.animationDraw[var19];
         float var17 = Math.max(0.0F, this.summary - var21.context);
         this.current.onTick(var19).handle(var21.instance / Math.max(1.0F, var1), var21.data / Math.max(1.0F, var2), var17, var17 > 3.1F ? 0.0F : var21.config);
      }

      this.current.prepare().handle(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      this.current.handle(var1, var2, var3, this.summary, this.summary);
      this.current.handle(this.providerFetch, this.profileDraw, this.vectorPerform, this.eventAttach, var9, 0.0F);
      this.current.handle(this.presetWrite, this.colorMeasure);
      this.current.process(-var4 * 0.0011F, -var5 * 9.0E-4F, var4 * 1.25F * var12, var5 * 1.05F * var12, var4 * 1.55F * var12, var5 * 1.35F * var12);
      this.current.compute(var10, var10 > 0.08F ? 1.0F : 0.88F, 0.0F, 0.0F, var11, process(var13, 0.0F, 1.0F));
      this.current
         .handle(
            this.animationSchedule == ThemePalette.SAKURA_BREEZE,
            this.animationSchedule == ThemePalette.VERNAL_SOLSTICE,
            this.animationSchedule == ThemePalette.MIDNIGHT_AZURE,
            this.rendererScan
         );
   }

   private void handle(int var1, ProxiesScreen.NetworkState var2) {
      float var3 = var2.itemProject ? var2.latest : var2.latest * 0.62F;
      this.current
         .handle(var1)
         .handle(
            var2.instance,
            var2.output,
            var2.current,
            var2.active,
            var2.mode,
            var2.selection,
            var2.enabled,
            var2.renderer,
            var2.handler,
            var3,
            var2.animationDraw,
            var2.previous,
            var2.animator,
            var2.source,
            var2.target,
            var2.pending
         );
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1) {
      try {
         WildClient.check();
         RoundedRectRenderer var2 = WildClient.handle();
         if (var2 == null) {
            return;
         }

         OpenGlStateSnapshot.NetworkState var3 = OpenGlStateSnapshot.handle();

         try {
            var2.handle(var1.onTick(), var1.select());
            float var4 = handle(var1.onTick(), var1.select());
            float var5 = var1.onTick() * 0.5F + var1.advancePosition() * 0.16F;
            float var6 = var1.select() * 0.135F + var1.checkFrame() * 0.1F;
            float var7 = process(var1.writePreset());
            var2.handle(FontRegistry.config, var5, var6, 38.0F * var4, "Multiplayer", this.compute(0.92F * var7), "c");
            String var8 = this.selection.size() == 1 ? "1 saved server" : this.selection.size() + " saved servers";
            var2.handle(FontRegistry.instance, var5, var6 + 28.0F * var4, 25.0F * var4, var8 + "  /  " + this.scaleSave, this.resolve(0.48F * var7), "c");
            var2.compute();
            var2.handle(
               this.scaleRender - 15.0F * var4,
               this.clientRefresh - 8.0F * var4,
               this.keyFilter + 30.0F * var4,
               this.requestAdapt + 16.0F * var4,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );

            for (ProxiesScreen.NetworkState var10 : this.enabled) {
               if (var10.vectorMatch && var10.latest > 0.01F) {
                  this.handle(var2, var10, var4);
               }
            }

            var2.compute();
            var2.apply();

            for (ProxiesScreen.NetworkState var18 : this.handler) {
               if (var18.vectorMatch) {
                  this.process(var2, var18, var4);
               }
            }

            if (this.matrixFilter) {
               float var17 = process(var1.writePreset());
               var2.handle(
                  this.requestReceive,
                  this.windowProcess,
                  this.packetSave,
                  this.entryAnimate,
                  this.packetSave * 0.5F,
                  this.rendererScan ? handle(0.0F, 0.0F, 0.0F, 0.045F * var17) : handle(1.0F, 1.0F, 1.0F, 0.05F * var17)
               );
               int var19 = handle(this.colorMeasure, this.presetWrite, 0.5F, (this.timerMeasure ? 0.75F : 0.45F) * var17);
               var2.handle(this.requestReceive, this.playerCollect, this.packetSave, this.stateApply, this.packetSave * 0.5F, var19);
            }

            var2.process();
         } finally {
            OpenGlStateSnapshot.compute(var3);
         }
      } catch (Throwable var15) {
      }
   }

   private void handle(RoundedRectRenderer var1, ProxiesScreen.NetworkState var2, float var3) {
      float var4 = var2.latest * (var2.itemProject ? 1.0F : 0.58F);
      float var5 = 25.0F * var3;
      ServerInfo var6 = var2.summary >= 0 && var2.summary < this.selection.size() ? this.selection.get(var2.summary) : null;
      float var7 = Math.min(var2.mode * 0.62F, 54.0F * var3);
      float var8 = var2.output + var5;
      float var9 = var2.current + var2.mode * 0.5F - var7 * 0.5F;
      float var10 = var2.matrixBlend ? 0.66F + 0.34F * (float)Math.sin(this.summary * 2.1F) : 0.36F + 0.16F * var2.renderer;
      int var11 = handle(this.colorMeasure, this.presetWrite, var10, (0.1F + var2.renderer * 0.16F + (var2.matrixBlend ? 0.12F : 0.0F)) * var4);
      var1.handle(var8, var9, var7, var7, var7 * 0.32F, var11);
      if (var2.pointEncode > 0.001F) {
         float var12 = var2.output + 26.0F * var3;
         float var13 = var2.current + var2.mode - 8.0F * var3;
         float var14 = (var2.active - 52.0F * var3) * var2.pointEncode;
         var1.handle(
            var12,
            var13,
            var14,
            2.4F * var3,
            1.2F * var3,
            handle(this.colorMeasure, this.presetWrite, 0.5F + var2.pointEncode * 0.25F, 0.42F * var4 * var2.pointEncode)
         );
      }

      ProxiesScreen.TextureState var21 = var6 == null ? null : (this.drawAnimation() ? this.update(var6) : this.resolve(var6));
      int var22 = var21 == null ? 0 : var21.handle();
      if (var22 > 0) {
         var1.handle(var22, var8 + 2.0F * var3, var9 + 2.0F * var3, var7 - 4.0F * var3, var7 - 4.0F * var3, 0.0F, 0.0F, 1.0F, 1.0F, var7 * 0.25F);
         var1.handle(var8, var9, var7, var7, var7 * 0.32F, handle(1.0F, 1.0F, 1.0F, (0.032F + var2.renderer * 0.026F) * var4));
      } else {
         var1.handle(
            FontRegistry.current,
            var8 + var7 * 0.5F,
            var9 + var7 * 0.72F,
            var7 * 0.82F,
            "w",
            this.rendererScan ? this.compute((0.72F + var2.renderer * 0.2F) * var4) : handle(1.0F, 1.0F, 1.0F, (0.72F + var2.renderer * 0.2F) * var4),
            "c"
         );
      }

      float var23 = var8 + var7 + 18.0F * var3;
      String var15 = var6 != null ? this.process(var6) : "";
      float var16 = var2.itemProject
         ? Math.max(72.0F * var3, RoundedRectRenderer.handle(FontRegistry.instance, var15, 24.0F * var3).instance + 24.0F * var3)
         : 0.0F;
      float var17 = var2.itemProject ? var16 + 48.0F * var3 : 80.0F * var3;
      float var18 = var2.active - (var23 - var2.output) - var17;
      String var19 = handle(var2.instance, var18, 25.0F * var3, FontRegistry.config);
      String var20 = handle(var2.data, var18, 22.0F * var3, FontRegistry.instance);
      var1.handle(
         FontRegistry.config, var23, var2.current + var2.mode * 0.5F - 6.0F * var3, 25.0F * var3, var19, this.compute((0.88F + var2.renderer * 0.08F) * var4)
      );
      var1.handle(
         FontRegistry.instance,
         var23,
         var2.current + var2.mode * 0.5F + 12.0F * var3,
         22.0F * var3,
         "IP: " + var20,
         this.resolve((0.4F + var2.renderer * 0.18F) * var4)
      );
      if (var2.itemProject && var6 != null) {
         this.handle(var1, var2, var6, var3, var4);
      }
   }

   private void process(RoundedRectRenderer var1, ProxiesScreen.NetworkState var2, float var3) {
      float var4 = var2.latest * (var2.itemProject ? 0.88F : 0.28F);
      float var5 = var2.output + var2.active * 0.5F;
      float var6 = var2.current + var2.mode * 0.5F;
      var1.handle(FontRegistry.instance, var5, var6 + 4.0F * var3, 26.0F * var3, var2.instance, this.compute(var4), "c");
   }

   private void handle(RoundedRectRenderer var1, ProxiesScreen.NetworkState var2, ServerInfo var3, float var4, float var5) {
      String var6 = this.process(var3);
      float var7 = 24.0F * var4;
      float var8 = RoundedRectRenderer.handle(FontRegistry.instance, var6, 24.0F * var4).instance;
      float var9 = Math.max(48.0F * var4, var8 + 16.0F * var4);
      float var10 = var2.output + var2.active - 24.0F * var4;
      float var11 = var10 - var9;
      float var12 = var2.current + var2.mode * 0.5F - var7 * 0.5F;
      var1.handle(
         var11,
         var12,
         var9,
         var7,
         var7 * 0.45F,
         this.rendererScan
            ? handle(1.0F, 1.0F, 1.0F, (0.54F + var2.renderer * 0.12F) * var5)
            : handle(0.018F, 0.022F, 0.028F, (0.44F + var2.renderer * 0.1F) * var5)
      );
      var1.handle(
         FontRegistry.instance, var11 + var9 * 0.5F, var12 + var7 * 0.66F, 24.0F * var4, var6, this.handle(var3, (0.72F + var2.renderer * 0.18F) * var5), "c"
      );
   }

   private String handle(ServerInfo var1) {
      if (var1.getStatus() == Status.PINGING) {
         return "Pinging server...";
      } else if (var1.getStatus() == Status.UNREACHABLE) {
         return var1.label == null ? "Server is offline" : var1.label.getString();
      } else if (var1.getStatus() == Status.INCOMPATIBLE && var1.version != null) {
         return "Version: " + var1.version.getString();
      } else {
         return var1.label != null && !var1.label.getString().isBlank() ? var1.label.getString().replace('\n', ' ') : "Waiting for response";
      }
   }

   private String process(ServerInfo var1) {
      if (var1.getStatus() == Status.PINGING) {
         return this.onTick();
      }

      String var2 = this.handle(var1.playerCountLabel);
      if (var1.players == null || var1.players.max() <= 0 && var1.players.online() <= 0) {
         if (this.handle(var2)) {
            return var2;
         } else {
            return var1.players != null ? var1.players.online() + "/" + var1.players.max() : "-";
         }
      } else {
         return var1.players.online() + "/" + var1.players.max();
      }
   }

   private String onTick() {
      int var1 = 1 + (int)(this.summary * 6.0F) % 3;
      return ".".repeat(var1);
   }

   private String handle(Text var1) {
      if (var1 == null) {
         return "";
      }

      String var2 = var1.getString();
      StringBuilder var3 = null;
      boolean var4 = false;
      int var5 = 0;
      int var6 = var2.length();

      while (var5 < var6 && Character.isWhitespace(var2.charAt(var5))) {
         var5++;
      }

      while (var6 > var5 && Character.isWhitespace(var2.charAt(var6 - 1))) {
         var6--;
      }

      for (int var7 = var5; var7 < var6; var7++) {
         char var8 = var2.charAt(var7);
         boolean var9 = Character.isWhitespace(var8);
         if (var9) {
            if (!var4) {
               if (var3 == null) {
                  var3 = new StringBuilder(var2.length());
                  var3.append(var2, var5, var7);
               }

               var3.append(' ');
               var4 = true;
            }
         } else {
            if (var3 != null) {
               var3.append(var8);
            }

            var4 = false;
         }
      }

      return var3 == null ? var2.substring(var5, var6) : var3.toString();
   }

   private boolean handle(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim();
         return !var2.equals("-") && !var2.equals("?") && !var2.equals("???") && !var2.equals("...");
      } else {
         return false;
      }
   }

   private String compute(ServerInfo var1) {
      if (var1.getStatus() == Status.PINGING) {
         return "ping";
      } else if (var1.ping >= 0L) {
         return var1.ping + " ms";
      } else {
         return var1.getStatus() == Status.UNREACHABLE ? "offline" : "-";
      }
   }

   private int handle(ServerInfo var1, float var2) {
      return switch (var1.getStatus()) {
         case SUCCESSFUL -> handle(this.colorMeasure, this.presetWrite, 0.35F + 0.25F * (float)Math.sin(this.summary * 1.6F), 0.82F * var2);
         case PINGING -> handle(0.68F, 0.76F, 0.84F, 0.62F * var2);
         case INCOMPATIBLE -> handle(1.0F, 0.7F, 0.36F, 0.72F * var2);
         case UNREACHABLE -> handle(1.0F, 0.32F, 0.36F, 0.72F * var2);
         case INITIAL -> handle(0.58F, 0.64F, 0.7F, 0.54F * var2);
         default -> throw new MatchException(null, null);
      };
   }

   private void handle(ProxiesScreen.Action var1) {
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var2 != null) {
         ProxiesScreen var3 = this;
         switch (var1) {
            case SERVER:
            default:
               break;
            case JOIN:
               var2.execute(this::select);
               break;
            case DIRECT:
               var2.execute(() -> this.handle(var2));
               break;
            case ADD:
               var2.execute(() -> this.process(var2));
               break;
            case EDIT:
               var2.execute(() -> this.compute(var2));
               break;
            case DELETE:
               var2.execute(() -> this.resolve(var2));
               break;
            case PROXY:
               var2.execute(() -> var2.setScreen(new ProxyImportScreen(var3)));
               break;
            case REFRESH:
               this.update();
               break;
            case BACK:
               var2.execute(() -> var2.setScreen(this.cache));
         }
      }
   }

   private void select() {
      MinecraftClient var1 = this.client == null ? MinecraftClient.getInstance() : this.client;
      ServerInfo var2 = this.refresh();
      if (var1 != null && var2 != null && var2.address != null && !var2.address.isBlank()) {
         this.scaleSave = "Resolving address...";
         CompletableFuture.<ServerAddress>supplyAsync(() -> ServerAddress.parse(var2.address), Util.getMainWorkerExecutor())
            .whenComplete((var3, var4) -> var1.execute(() -> {
               if (var4 == null && var3 != null) {
                  ConnectScreen.connect(this, var1, var3, var2, false, null);
               } else {
                  this.scaleSave = "Invalid server address";
               }
            }));
      } else {
         this.scaleSave = "Select a server";
      }
   }

   private void handle(MinecraftClient var1) {
      ServerInfo var2 = new ServerInfo("Direct Server", "", ServerType.OTHER);
      var1.setScreen(
         new DirectConnectScreen(
            this,
            var3 -> {
               if (var3) {
                  this.scaleSave = "Resolving address...";
                  CompletableFuture.<ServerAddress>supplyAsync(() -> ServerAddress.parse(var2.address), Util.getMainWorkerExecutor())
                     .whenComplete((var3x, var4) -> var1.execute(() -> {
                        if (var4 == null && var3x != null) {
                           ConnectScreen.connect(this, var1, var3x, var2, false, null);
                        } else {
                           this.scaleSave = "Invalid server address";
                           var1.setScreen(this);
                        }
                     }));
               } else {
                  var1.setScreen(this);
               }
            },
            var2
         )
      );
   }

   private void process(MinecraftClient var1) {
      ServerInfo var2 = new ServerInfo("Minecraft Server", "", ServerType.OTHER);
      var1.setScreen(new AddServerScreen(this, var3 -> {
         if (var3 && this.source != null) {
            try {
               this.source.add(var2, false);
               this.selection.add(var2);
               this.compute();
               this.sourceBuild = this.source.size() - 1;
               this.scaleSave = "Server added";
            } catch (Throwable var5) {
               this.scaleSave = "Failed to add server";
            }
         }

         var1.setScreen(this);
      }, var2));
   }

   private void compute(MinecraftClient var1) {
      ServerInfo var2 = this.refresh();
      if (var2 != null && this.source != null && this.sourceBuild >= 0 && this.sourceBuild < this.source.size()) {
         int var3 = this.sourceBuild;
         ServerInfo var4 = new ServerInfo(var2.name, var2.address, var2.getServerType());
         var4.copyWithSettingsFrom(var2);
         var1.setScreen(new AddServerScreen(this, var4x -> {
            if (var4x && this.source != null && var3 >= 0 && var3 < this.source.size()) {
               try {
                  this.source.set(var3, var4);
                  if (var3 < this.selection.size()) {
                     this.selection.set(var3, var4);
                  }

                  this.compute();
                  this.sourceBuild = var3;
                  this.scaleSave = "Server updated";
               } catch (Throwable var6) {
                  this.scaleSave = "Failed to save changes";
               }
            }

            var1.setScreen(this);
         }, var4));
      } else {
         this.scaleSave = "Select a server";
      }
   }

   private void resolve(MinecraftClient var1) {
      ServerInfo var2 = this.refresh();
      if (var2 != null && this.source != null) {
         String var3 = handle(var2.name, "Unnamed server");
         var1.setScreen(new ConfirmScreen(var3x -> {
            if (var3x && this.source != null) {
               try {
                  this.source.remove(var2);
                  this.selection.remove(var2);
                  this.compute();
                  this.sourceBuild = Math.min(this.sourceBuild, Math.max(0, this.source.size() - 1));
                  if (this.source.size() == 0) {
                     this.sourceBuild = -1;
                  }

                  this.scaleSave = "Server deleted";
               } catch (Throwable var5) {
                  this.scaleSave = "Failed to delete server";
               }
            }

            var1.setScreen(this);
         }, Text.literal("Delete server?"), Text.literal(var3)));
      } else {
         this.scaleSave = "Select a server";
      }
   }

   private ServerInfo refresh() {
      return this.sourceBuild >= 0 && this.sourceBuild < this.selection.size() ? this.selection.get(this.sourceBuild) : null;
   }

   private void render2() {
      ServerInfo var1 = this.refresh();
      if (var1 != null && var1.address != null && !var1.address.isBlank()) {
         MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
         if (var2 != null && var2.keyboard != null) {
            var2.keyboard.setClipboard(var1.address);
            this.scaleSave = "IP copied: " + var1.address;
         }
      } else {
         this.scaleSave = "Select a server";
      }
   }

   private void compute(int var1) {
      if (this.source != null && this.sourceBuild >= 0 && this.sourceBuild < this.selection.size()) {
         int var2 = this.sourceBuild + var1;
         if (var2 >= 0 && var2 < this.selection.size() && var2 < this.source.size()) {
            try {
               ServerInfo var3 = this.source.get(this.sourceBuild);
               ServerInfo var4 = this.source.get(var2);
               this.source.set(this.sourceBuild, var4);
               this.source.set(var2, var3);
               Collections.swap(this.selection, this.sourceBuild, var2);
               this.compute();
               this.sourceBuild = var2;
               this.scaleSave = "Server moved";
               this.tick2();
            } catch (Throwable var5) {
               this.scaleSave = "Failed to move server";
            }
         }
      } else {
         this.scaleSave = "Select a server";
      }
   }

   private boolean process(ProxiesScreen.Action var1) {
      boolean var2 = this.refresh() != null;

      return switch (var1) {
         case SERVER -> false;
         case JOIN, EDIT, DELETE -> var2;
         case DIRECT, ADD, PROXY, REFRESH, BACK -> true;
      };
   }

   private void resolve(int var1) {
      if (this.selection.isEmpty()) {
         this.sourceBuild = -1;
         this.scaleSave = "No saved servers";
      } else {
         this.sourceBuild = handle(this.sourceBuild + var1, 0, this.selection.size() - 1);
         this.scaleSave = "Ready";
         this.tick2();
      }
   }

   private void tick2() {
      if (this.sourceBuild >= 0) {
         if (this.sourceBuild < this.outputCollapse) {
            this.outputCollapse = this.sourceBuild;
         }

         if (this.sourceBuild > this.outputCollapse + this.sourceSchedule - 1.0F) {
            this.outputCollapse = this.sourceBuild - this.sourceSchedule + 1;
         }

         int var1 = Math.max(0, this.selection.size() - Math.max(1, this.sourceSchedule));
         this.outputCollapse = process(this.outputCollapse, 0.0F, var1);
      }
   }

   private ProxiesScreen.TextureState resolve(ServerInfo var1) {
      byte[] var2 = var1.getFavicon();
      if (var2 != null && var2.length != 0) {
         String var3 = this.handle(var1, var2);
         ProxiesScreen.TextureState var4 = this.renderer.get(var3);
         if (var4 != null) {
            return var4;
         }

         try {
            NativeImage var5 = NativeImage.read(var2);
            NativeImageBackedTexture var6 = new NativeImageBackedTexture(() -> "wild_server_icon", var5);
            var6.setFilter(true, false);
            var6.upload();
            ProxiesScreen.TextureState var7 = new ProxiesScreen.TextureState(var6);
            this.renderer.put(var3, var7);
            return var7;
         } catch (Throwable var8) {
            return null;
         }
      } else {
         return null;
      }
   }

   private ProxiesScreen.TextureState update(ServerInfo var1) {
      byte[] var2 = var1.getFavicon();
      return var2 != null && var2.length != 0 ? this.renderer.get(this.handle(var1, var2)) : null;
   }

   private boolean drawAnimation() {
      return System.nanoTime() - this.dataValidate < 180000000L || Math.abs(this.outputCollapse - this.profileInvoke) > 0.06F;
   }

   private String handle(ServerInfo var1, byte[] var2) {
      return handle(var1.address, "") + ":" + Arrays.hashCode(var2);
   }

   private void encodePoint() {
      for (ProxiesScreen.TextureState var2 : this.renderer.values()) {
         var2.close();
      }

      this.renderer.clear();
   }

   private float handle(Window var1, double var2) {
      return (float)(var2 * var1.getFramebufferWidth() / Math.max(1.0, var1.getScaledWidth()));
   }

   private float process(Window var1, double var2) {
      return (float)(var2 * var1.getFramebufferHeight() / Math.max(1.0, var1.getScaledHeight()));
   }

   private static String handle(String var0, String var1) {
      return var0 != null && !var0.isBlank() ? var0 : var1;
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

   private static float handle(float var0, float var1) {
      return process(Math.min(var0 / 1920.0F, var1 / 1080.0F) * 1.08F, 0.62F, 1.2F);
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

   private static float process(float var0, float var1) {
      return (float)Math.sqrt(var0 * var0 + var1 * var1);
   }

   private static float process(float var0) {
      float var1 = process(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static int handle(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static float update(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   private static float apply(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   private static float execute(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private int compute(float var1) {
      return this.rendererScan ? handle(0.1F, 0.1F, 0.1F, var1) : handle(1.0F, 1.0F, 1.0F, var1);
   }

   private int resolve(float var1) {
      return this.rendererScan ? handle(0.4F, 0.4F, 0.4F, var1) : handle(0.78F, 0.84F, 0.88F, var1);
   }

   private static int handle(float var0, float var1, float var2, float var3) {
      int var4 = Math.round(process(var0, 0.0F, 1.0F) * 255.0F);
      int var5 = Math.round(process(var1, 0.0F, 1.0F) * 255.0F);
      int var6 = Math.round(process(var2, 0.0F, 1.0F) * 255.0F);
      int var7 = Math.round(process(var3, 0.0F, 1.0F) * 255.0F);
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   private static int handle(int var0, int var1, float var2, float var3) {
      float var4 = process(var2, 0.0F, 1.0F);
      int var5 = PackedColor.resolve(var0, var1, var4);
      int var6 = Math.round(process(var3, 0.0F, 1.0F) * 255.0F);
      return var6 << 24 | var5;
   }

   enum Action {
      SERVER,
      JOIN,
      DIRECT,
      ADD,
      EDIT,
      DELETE,
      PROXY,
      REFRESH,
      BACK;
   }

   static final class NetworkState {
      String instance;
      String data = "";
      final ProxiesScreen.Action context;
      final SmoothedValue config = new SmoothedValue(SpringAnimationSpec.update());
      float state;
      float cache;
      float output;
      float current;
      float active;
      float mode;
      float selection;
      float enabled;
      float renderer;
      float handler;
      float animationDraw;
      float pointEncode;
      float animator = 1.0F;
      float source = 0.5F;
      float target = 0.5F;
      float pending;
      float previous;
      float latest;
      int summary = -1;
      boolean matrixBlend;
      boolean vectorMatch;
      boolean itemProject = true;

      NetworkState(String var1, ProxiesScreen.Action var2) {
         this.instance = var1;
         this.context = var2;
      }

      void handle() {
         this.enabled = 0.0F;
         this.renderer = 0.0F;
         this.handler = 0.0F;
         this.animationDraw = 0.0F;
         this.pointEncode = 0.0F;
         this.animator = 1.0F;
         this.source = 0.5F;
         this.target = 0.5F;
         this.pending = 0.0F;
         this.latest = 0.0F;
         this.matrixBlend = false;
         this.vectorMatch = false;
         this.itemProject = true;
         this.config.handle(1.0F);
      }

      boolean handle(float var1, float var2) {
         return ProxiesScreen.handle(var1, var2, this.state, this.cache, this.active, this.mode, this.selection) <= 0.0F;
      }
   }

   static final class State {
      float instance;
      float data;
      float context = -100.0F;
      float config;
   }

   static final class TextureState implements AutoCloseable {
      private final NativeImageBackedTexture instance;

      TextureState(NativeImageBackedTexture var1) {
         this.instance = var1;
      }

      int handle() {
         return this.instance.getGlTexture() instanceof GlTexture var1 ? var1.getGlId() : 0;
      }

      @Override
      public void close() {
         this.instance.close();
      }
   }
}
