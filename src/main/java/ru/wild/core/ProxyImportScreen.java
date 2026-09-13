package ru.wild.core;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;
import ru.wild.WildClient;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.gui.screen.MenuAnimationClock;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.modules.visuals.Menu;
import ru.wild.network.ProxyManager;
import ru.wild.render.MainMenuBackgroundRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.SmoothedValue;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class ProxyImportScreen extends Screen implements FrameRenderListener {
   private static final ThemePaletteRegistry instance = ThemePaletteRegistry.handle();
   private static final int data = 14;
   private final Screen context;
   private final MainMenuBackgroundRenderer config = new MainMenuBackgroundRenderer();
   private final ProxyImportScreen.PrimaryFileEntry state = new ProxyImportScreen.PrimaryFileEntry("Host", false, 255, ProxyImportScreen.PrimaryMode.HOST);
   private final ProxyImportScreen.PrimaryFileEntry cache = new ProxyImportScreen.PrimaryFileEntry("Port", false, 5, ProxyImportScreen.PrimaryMode.PORT);
   private final ProxyImportScreen.PrimaryFileEntry output = new ProxyImportScreen.PrimaryFileEntry("Username", false, 128, ProxyImportScreen.PrimaryMode.TEXT);
   private final ProxyImportScreen.PrimaryFileEntry current = new ProxyImportScreen.PrimaryFileEntry(
      "Password", true, 256, ProxyImportScreen.PrimaryMode.SECRET
   );
   private final List<ProxyImportScreen.PrimaryFileEntry> active = List.of(this.state, this.cache, this.output, this.current);
   private final List<ProxyImportScreen.PrimaryScreenState> mode = List.of(
      new ProxyImportScreen.PrimaryScreenState("Socks5", ProxyImportScreen.Mode.TYPE),
      new ProxyImportScreen.PrimaryScreenState("Enabled", ProxyImportScreen.Mode.ENABLED),
      new ProxyImportScreen.PrimaryScreenState("Paste", ProxyImportScreen.Mode.PASTE),
      new ProxyImportScreen.PrimaryScreenState("Test", ProxyImportScreen.Mode.TEST),
      new ProxyImportScreen.PrimaryScreenState("Save", ProxyImportScreen.Mode.SAVE),
      new ProxyImportScreen.PrimaryScreenState("Back", ProxyImportScreen.Mode.BACK)
   );
   private final ProxyImportScreen.FileEntry[] selection = new ProxyImportScreen.FileEntry[14];
   private final WildMainMenuScreen.PrimaryNetworkState enabled = new WildMainMenuScreen.PrimaryNetworkState(10, 14);
   private final OpenGlStateSnapshot.NetworkState renderer = new OpenGlStateSnapshot.NetworkState();
   private final OpenGlStateSnapshot.NetworkState handler = new OpenGlStateSnapshot.NetworkState();
   private final SmoothedValue animationDraw = new SmoothedValue(SpringAnimationSpec.update());
   private final SmoothedValue pointEncode = new SmoothedValue(SpringAnimationSpec.update());
   private long animator;
   private long source;
   private long target;
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
   private boolean eventAttach;
   private boolean serverRead;
   private boolean positionAdvance;
   private int frameCheck;
   private int moduleCollect;
   private int providerClose = -6357021;
   private int presetSave = -11341636;
   private ThemePalette windowConvert = ThemePalette.AURORA;
   private boolean presetWrite;
   private boolean colorMeasure;
   private boolean animationSchedule;
   private String rendererScan = "Socks5";
   private String sourceBuild = "Proxy disabled";
   private int outputCollapse;

   public ProxyImportScreen(Screen var1) {
      super(Text.literal("Proxy"));
      this.context = var1;

      for (int var2 = 0; var2 < this.selection.length; var2++) {
         this.selection[var2] = new ProxyImportScreen.FileEntry();
      }
   }

   protected void init() {
      super.init();
      this.animator = System.nanoTime();
      this.source = this.animator;
      this.target = this.animator;
      this.eventAttach = false;
      this.serverRead = false;
      this.positionAdvance = false;
      this.frameCheck = 0;
      this.moduleCollect = 0;
      this.animationDraw.handle(0.0F);
      this.pointEncode.handle(0.0F);
      if (!this.colorMeasure) {
         ProxyManager.DataRecord var1 = ProxyManager.compute();
         this.animationSchedule = var1.enabled();
         this.rendererScan = ProxyManager.process(var1.type());
         this.state.summary = var1.host();
         this.cache.summary = var1.port();
         this.output.summary = var1.username();
         this.current.summary = var1.password();

         for (ProxyImportScreen.PrimaryFileEntry var3 : this.active) {
            var3.matrixBlend = var3.summary.length();
         }

         this.sourceBuild = this.animationSchedule ? "Proxy enabled" : "Proxy disabled";
         this.colorMeasure = true;
      }

      for (ProxyImportScreen.PrimaryFileEntry var6 : this.active) {
         var6.update();
      }

      for (ProxyImportScreen.PrimaryScreenState var7 : this.mode) {
         var7.handle();
      }
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
         float var10 = Math.max(0.001F, Math.min(0.05F, (float)(var8 - this.source) / 1.0E9F));
         this.source = var8;
         this.pending = (float)(var8 - this.animator) / 1.0E9F;
         if (this.handle(var5, var6, var7, var1, var2, var8)) {
            var10 = 0.001F;
         }

         this.update();
         this.handle(var5, var1, var2, var10, var8);
         this.process(var6, var7, var10);
         this.apply();
         float var11 = (this.previous / Math.max(1.0F, var6) - 0.5F) * 2.0F;
         float var12 = (this.latest / Math.max(1.0F, var7) - 0.5F) * 2.0F;
         float var13 = this.animationDraw.handle(var11, var10);
         float var14 = this.pointEncode.handle(var12, var10);
         this.handle(var6, var7, var13, var14, var10);
         int var15 = GL11.glGetInteger(36006);
         this.handle(var6, var7, var15, var13, var14, var8);
         if (var4) {
            OpenGlStateSnapshot.process(this.renderer);

            try {
               this.config.handle(this.enabled);
            } finally {
               OpenGlStateSnapshot.compute(this.renderer);
            }

            this.handle(this.enabled);
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
      this.handle(ProxyImportScreen.Mode.BACK);
   }

   public void removed() {
      this.outputCollapse++;
      this.config.close();
      super.removed();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 && this.client != null && this.client.getWindow() != null) {
         float var6 = this.handle(this.client.getWindow(), var1);
         float var7 = this.process(this.client.getWindow(), var3);

         for (ProxyImportScreen.PrimaryFileEntry var9 : this.active) {
            if (var9.handle(var6, var7)) {
               this.handle(var9);
               var9.enabled = 1.0F;
               return true;
            }
         }

         this.compute();

         for (ProxyImportScreen.PrimaryScreenState var11 : this.mode) {
            if (var11.handle(var6, var7)) {
               var11.enabled = 1.0F;
               var11.renderer = 1.0F;
               this.handle(var11.pending);
               return true;
            }
         }

         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean charTyped(char var1, int var2) {
      ProxyImportScreen.PrimaryFileEntry var3 = this.resolve();
      if (var3 == null) {
         return super.charTyped(var1, var2);
      }

      if (var1 >= ' ' && var1 != 127) {
         var3.process(String.valueOf(var1));
      }

      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = (var3 & 2) != 0 || (var3 & 8) != 0;
      ProxyImportScreen.PrimaryFileEntry var5 = this.resolve();
      if (var1 == 256) {
         if (var5 != null) {
            this.compute();
            return true;
         } else {
            this.handle(ProxyImportScreen.Mode.BACK);
            return true;
         }
      } else {
         if (var1 == 258) {
            this.handle((var3 & 1) != 0 ? -1 : 1);
            return true;
         }

         if (var5 != null) {
            if (var4) {
               if (var1 == 65) {
                  var5.itemProject = true;
                  return true;
               }

               if (var1 == 67) {
                  if (this.client != null && this.client.keyboard != null && var5.itemProject) {
                     this.client.keyboard.setClipboard(var5.summary);
                  }

                  return true;
               }

               if (var1 == 86) {
                  if (this.client != null && this.client.keyboard != null) {
                     var5.process(this.client.keyboard.getClipboard());
                  }

                  return true;
               }
            }

            if (var1 == 259) {
               var5.process();
               return true;
            }

            if (var1 == 261) {
               var5.compute();
               return true;
            }

            if (var1 == 263) {
               var5.itemProject = false;
               var5.matrixBlend = handle(var5.matrixBlend - 1, 0, var5.summary.length());
               return true;
            }

            if (var1 == 262) {
               var5.itemProject = false;
               var5.matrixBlend = handle(var5.matrixBlend + 1, 0, var5.summary.length());
               return true;
            }

            if (var1 == 268) {
               var5.itemProject = false;
               var5.matrixBlend = 0;
               return true;
            }

            if (var1 == 269) {
               var5.itemProject = false;
               var5.matrixBlend = var5.summary.length();
               return true;
            }

            if (var1 != 257 && var1 != 335) {
               return true;
            }

            this.handle(ProxyImportScreen.Mode.SAVE);
            return true;
         } else {
            if (var4 && var1 == 86) {
               this.handle(ProxyImportScreen.Mode.PASTE);
               return true;
            }

            if (var1 != 257 && var1 != 335) {
               return super.keyPressed(var1, var2, var3);
            }

            this.handle(ProxyImportScreen.Mode.SAVE);
            return true;
         }
      }
   }

   private void handle(ProxyImportScreen.Mode var1) {
      MinecraftClient var2 = this.client == null ? MinecraftClient.getInstance() : this.client;
      if (var2 != null) {
         switch (var1) {
            case TYPE:
               this.rendererScan = "Socks5".equals(this.rendererScan) ? "Socks4" : "Socks5";
               this.sourceBuild = this.rendererScan + " selected";
               break;
            case ENABLED:
               this.animationSchedule = !this.animationSchedule;
               if (this.animationSchedule) {
                  this.sourceBuild = "Proxy enabled";
               } else {
                  ProxyManager.handle(this.process(false));
                  this.sourceBuild = "Proxy disabled";
               }
               break;
            case PASTE:
               this.handle(var2);
               break;
            case TEST:
               this.process(var2);
               break;
            case SAVE:
               this.handle(false);
               break;
            case BACK:
               var2.execute(() -> var2.setScreen(this.context));
         }
      }
   }

   private void handle(MinecraftClient var1) {
      String var2 = "";

      try {
         var2 = var1.keyboard == null ? "" : var1.keyboard.getClipboard();
      } catch (Throwable var4) {
      }

      ProxyManager.SecondaryDataRecord var3 = ProxyManager.handle(var2);
      if (!var3.host().isBlank() && !var3.port().isBlank()) {
         this.rendererScan = ProxyManager.process(var3.type());
         this.state.handle(var3.host());
         this.cache.handle(var3.port());
         this.output.handle(var3.username());
         this.current.handle(var3.password());
         this.animationSchedule = true;
         this.compute();
         this.sourceBuild = "Proxy imported";
      } else {
         this.sourceBuild = "Clipboard has no proxy";
      }
   }

   private void process(MinecraftClient var1) {
      this.process();
      ProxyManager.DataRecord var2 = this.process(true);
      String var3 = ProxyManager.handle(var2, true);
      if (var3 != null) {
         this.sourceBuild = var3;
      } else {
         int var4 = ++this.outputCollapse;
         this.sourceBuild = "Checking proxy...";
         ProxyManager.handle(var2, "mc.funtime.su", 25565, 8000).whenComplete((var4x, var5) -> var1.execute(() -> {
            if (var4 == this.outputCollapse) {
               if (var5 != null) {
                  this.sourceBuild = "Proxy failed: " + var5.getClass().getSimpleName();
               } else {
                  if (var4x.success()) {
                     this.animationSchedule = true;
                     ProxyManager.handle(var2);
                     this.sourceBuild = "Proxy OK and enabled: " + var4x.millis() + " ms";
                  } else {
                     this.sourceBuild = "Proxy failed: " + var4x.message();
                  }
               }
            }
         }));
      }
   }

   private void handle(boolean var1) {
      this.process();
      ProxyManager.DataRecord var2 = this.process(true);
      String var3 = ProxyManager.handle(var2, true);
      if (var3 != null) {
         this.sourceBuild = var3;
      } else {
         this.animationSchedule = true;
         ProxyManager.handle(var2);
         this.sourceBuild = "Proxy saved and enabled";
         if (var1 && this.client != null) {
            this.client.setScreen(this.context);
         }
      }
   }

   private void process() {
      String var1 = this.state.summary;
      ProxyManager.SecondaryDataRecord var2 = ProxyManager.handle(var1);
      if (!var2.host().isBlank()) {
         this.state.handle(var2.host());
         if (!var2.port().isBlank()) {
            this.cache.handle(var2.port());
         }

         if (!var2.username().isBlank()) {
            this.output.handle(var2.username());
         }

         if (!var2.password().isBlank()) {
            this.current.handle(var2.password());
         }

         this.rendererScan = ProxyManager.process(var2.type());
      } else {
         ProxyManager.SecondaryDataRecord var3 = ProxyManager.handle(this.state.summary + ":" + this.cache.summary);
         if (!var3.host().isBlank()) {
            this.state.handle(var3.host());
         }
      }
   }

   private ProxyManager.DataRecord process(boolean var1) {
      return new ProxyManager.DataRecord(var1, this.rendererScan, this.state.summary, this.cache.summary, this.output.summary, this.current.summary);
   }

   private void handle(ProxyImportScreen.PrimaryFileEntry var1) {
      for (ProxyImportScreen.PrimaryFileEntry var3 : this.active) {
         var3.vectorMatch = var3 == var1;
         var3.itemProject = false;
         if (var3.vectorMatch) {
            var3.matrixBlend = var3.summary.length();
         }
      }
   }

   private void handle(int var1) {
      ProxyImportScreen.PrimaryFileEntry var2 = this.resolve();
      int var3 = var2 == null ? (var1 > 0 ? -1 : this.active.size()) : this.active.indexOf(var2);
      int var4 = Math.floorMod(var3 + var1, this.active.size());
      this.handle(this.active.get(var4));
   }

   private void compute() {
      for (ProxyImportScreen.PrimaryFileEntry var2 : this.active) {
         var2.vectorMatch = false;
         var2.itemProject = false;
      }
   }

   private ProxyImportScreen.PrimaryFileEntry resolve() {
      for (ProxyImportScreen.PrimaryFileEntry var2 : this.active) {
         if (var2.vectorMatch) {
            return var2;
         }
      }

      return null;
   }

   private void update() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.AURORA;
      this.windowConvert = var1;
      this.presetWrite = instance.compute(var1);
      this.providerClose = instance.resolve(var1);
      this.presetSave = instance.update(var1);
   }

   private boolean handle(Window var1, int var2, int var3, int var4, int var5, long var6) {
      if (this.frameCheck == var2 && this.moduleCollect == var3) {
         return false;
      }

      this.frameCheck = var2;
      this.moduleCollect = var3;
      float var8 = process(this.handle(var1, var4), 0.0F, var2);
      float var9 = process(this.process(var1, var5), 0.0F, var3);
      this.previous = this.vectorMatch = this.profileDraw = var8;
      this.latest = this.itemProject = this.vectorPerform = var9;
      this.summary = this.matrixBlend = 0.0F;
      this.responseCompute = this.providerFetch = 0.0F;
      this.eventAttach = true;
      this.serverRead = true;
      this.positionAdvance = true;
      this.target = var6;
      this.animationDraw.handle(0.0F);
      this.pointEncode.handle(0.0F);
      this.execute();
      this.handle(var8, var9, 0.12F);
      return true;
   }

   private void handle(Window var1, int var2, int var3, float var4, long var5) {
      float var7 = this.handle(var1, var2);
      float var8 = this.process(var1, var3);
      if (!this.eventAttach) {
         this.previous = var7;
         this.latest = var8;
         this.summary = 0.0F;
         this.matrixBlend = 0.0F;
         this.eventAttach = true;
      } else {
         float var9 = var7 - this.previous;
         float var10 = var8 - this.latest;
         float var11 = process(var9, var10);
         if (var11 > 0.2F) {
            this.summary = process(var9 / Math.max(1.0F, var1.getFramebufferWidth()) / var4, -3.0F, 3.0F);
            this.matrixBlend = process(var10 / Math.max(1.0F, var1.getFramebufferHeight()) / var4, -3.0F, 3.0F);
         } else {
            float var12 = (float)Math.pow(8.0E-4F, var4);
            this.summary *= var12;
            this.matrixBlend *= var12;
         }

         this.previous = var7;
         this.latest = var8;
         if (var11 > 1.5F) {
            this.target = var5;
         }
      }
   }

   private void process(int var1, int var2, float var3) {
      if (!this.serverRead) {
         this.vectorMatch = this.previous;
         this.itemProject = this.latest;
         this.responseCompute = 0.0F;
         this.providerFetch = 0.0F;
         this.serverRead = true;
      } else {
         float var4 = this.vectorMatch;
         float var5 = this.itemProject;
         float var6 = process(this.previous - this.vectorMatch, this.latest - this.itemProject);
         float var7 = (1.0F - (float)Math.pow(3.5E-5F, var3)) * (0.72F + process(var6 / 520.0F, 0.0F, 0.42F));
         this.vectorMatch = this.vectorMatch + (this.previous - this.vectorMatch) * process(var7, 0.05F, 0.26F);
         this.itemProject = this.itemProject + (this.latest - this.itemProject) * process(var7, 0.05F, 0.26F);
         float var8 = process((this.vectorMatch - var4) / Math.max(1.0F, var1) / var3, -1.8F, 1.8F);
         float var9 = process((this.itemProject - var5) / Math.max(1.0F, var2) / var3, -1.8F, 1.8F);
         float var10 = 1.0F - (float)Math.pow(0.0025F, var3);
         this.responseCompute = this.responseCompute + (var8 - this.responseCompute) * var10;
         this.providerFetch = this.providerFetch + (var9 - this.providerFetch) * var10;
      }
   }

   private void apply() {
      if (MenuAnimationClock.compute() && Menu.handle(Menu.sourceBuild)) {
         if (!this.positionAdvance) {
            this.profileDraw = this.vectorMatch;
            this.vectorPerform = this.itemProject;
            this.positionAdvance = true;
            this.handle(this.vectorMatch, this.itemProject, 0.3F);
         } else {
            float var1 = process(this.vectorMatch - this.profileDraw, this.itemProject - this.vectorPerform);
            if (var1 > 5.5F) {
               this.handle(this.vectorMatch, this.itemProject, process(var1 / 190.0F, 0.1F, 0.48F));
               this.profileDraw = this.vectorMatch;
               this.vectorPerform = this.itemProject;
            }
         }
      }
   }

   private void execute() {
      for (ProxyImportScreen.FileEntry var4 : this.selection) {
         var4.instance = 0.0F;
         var4.data = 0.0F;
         var4.context = -100.0F;
         var4.config = 0.0F;
      }
   }

   private void handle(float var1, float var2, float var3) {
      int var4 = 0;
      float var5 = -1.0F;

      for (int var6 = 0; var6 < this.selection.length; var6++) {
         float var7 = this.pending - this.selection[var6].context;
         if (this.selection[var6].config <= 0.0F) {
            var4 = var6;
            break;
         }

         if (var7 > var5) {
            var5 = var7;
            var4 = var6;
         }
      }

      this.selection[var4].instance = var1;
      this.selection[var4].data = var2;
      this.selection[var4].context = this.pending;
      this.selection[var4].config = var3;
   }

   private void handle(int var1, int var2, float var3, float var4, float var5) {
      float var6 = handle(var1, var2);
      boolean var7 = var1 < 980.0F * var6;
      float var8 = 46.0F * var6;
      float var9 = 18.0F * var6;
      float var10 = 16.0F * var6;
      float var11 = var7 ? process(var1 * 0.68F, 300.0F * var6, 520.0F * var6) : process(var1 * 0.2F, 280.0F * var6, 410.0F * var6);
      float var12 = var7 ? var11 : var11 * 2.0F + var10;
      float var13 = 42.0F * var6;
      float var14 = 10.0F * var6;
      float var15 = var7 ? (var12 - var14) * 0.5F : process(var1 * 0.072F, 96.0F * var6, 128.0F * var6);
      int var16 = var7 ? 2 : 6;
      int var17 = var7 ? 3 : 1;
      float var18 = var16 * var15 + (var16 - 1) * var14;
      float var19 = var7 ? this.active.size() * var8 + (this.active.size() - 1) * var9 : var8 * 2.0F + var9;
      float var20 = var17 * var13 + (var17 - 1) * var14;
      float var21 = var19 + 34.0F * var6 + var20;
      float var22 = var1 * 0.5F + var3 * 1.35F * var6;
      float var23 = var2 * 0.305F + var4 * 0.92F * var6;
      if (var23 + var21 > var2 - 58.0F * var6) {
         var23 = var2 - var21 - 58.0F * var6;
      }

      var23 = Math.max(var2 * 0.21F, var23);
      float var24 = var22 - var12 * 0.5F;

      for (int var25 = 0; var25 < this.active.size(); var25++) {
         ProxyImportScreen.PrimaryFileEntry var26 = this.active.get(var25);
         int var27 = var7 ? 0 : var25 % 2;
         int var28 = var7 ? var25 : var25 / 2;
         float var29 = var24 + var27 * (var11 + var10);
         float var30 = var23 + var28 * (var8 + var9);
         this.handle(var26, var29, var30, var11, var8, var8 * 0.5F, var5, var6);
      }

      float var32 = var22 - var18 * 0.5F;
      float var33 = var23 + var19 + 34.0F * var6;

      for (int var34 = 0; var34 < this.mode.size(); var34++) {
         ProxyImportScreen.PrimaryScreenState var35 = this.mode.get(var34);
         int var36 = var34 % var16;
         int var37 = var34 / var16;
         var35.instance = this.process(var35.pending);
         var35.context = var32 + var36 * (var15 + var14);
         var35.config = var33 + var37 * (var13 + var14);
         var35.output = var15;
         var35.current = var13;
         var35.active = var13 * 0.5F;
         var35.source = 46.0F * var6;
         var35.target = compute(process((this.pending - 0.32F - var34 * 0.035F) / 0.76F, 0.0F, 1.0F));
         this.handle(var35, var5, var6);
      }
   }

   private void handle(ProxyImportScreen.PrimaryFileEntry var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      var1.context = var2;
      var1.config = var3;
      var1.output = var4;
      var1.current = var5;
      var1.active = var6;
      var1.source = 50.0F * var8;
      var1.target = compute(process((this.pending - 0.22F - this.active.indexOf(var1) * 0.04F) / 0.82F, 0.0F, 1.0F));
      this.handle(var1, var7, var8);
      var1.responseCompute = var1.responseCompute + ((var1.vectorMatch ? 1.0F : 0.0F) - var1.responseCompute) * (1.0F - (float)Math.pow(1.0E-4F, var7));
   }

   private void handle(ProxyImportScreen.ScreenState var1, float var2, float var3) {
      float var4 = handle(this.previous, this.latest, var1.context, var1.config, var1.output, var1.current, var1.active);
      boolean var5 = var4 <= 0.0F;
      float var6 = 1.0F - compute(process(Math.max(0.0F, var4) / Math.max(1.0F, 28.0F * var3), 0.0F, 1.0F));
      float var7 = var1 instanceof ProxyImportScreen.PrimaryFileEntry var8 && var8.vectorMatch ? 0.52F : 0.0F;
      float var13 = Math.max(var5 ? Math.max(0.74F, var6) : var6 * 0.48F, var7);
      var1.mode = var1.mode + ((var5 ? 1.0F : 0.0F) - var1.mode) * (1.0F - (float)Math.pow(1.0E-4F, var2));
      var1.selection = var1.selection + (var13 - var1.selection) * (1.0F - (float)Math.pow(1.4E-4F, var2));
      var1.enabled = var1.enabled + (0.0F - var1.enabled) * (1.0F - (float)Math.pow(1.8E-5F, var2));
      var1.renderer = var1.renderer + (0.0F - var1.renderer) * (1.0F - (float)Math.pow(6.0E-6F, var2));
      float var9 = process((this.vectorMatch - var1.context) / Math.max(1.0F, var1.output), 0.0F, 1.0F);
      float var10 = process((this.itemProject - var1.config) / Math.max(1.0F, var1.current), 0.0F, 1.0F);
      float var11 = 1.0F - (float)Math.pow(2.2E-4F, var2);
      var1.animationDraw = var1.animationDraw + (var9 - var1.animationDraw) * var11;
      var1.pointEncode = var1.pointEncode + (var10 - var1.pointEncode) * var11;
      float var12 = 1.0F + var1.selection * 0.04F - var1.enabled * 0.065F + var7 * 0.018F;
      var1.handler = var1.data.handle(var12, var2);
      var1.state = var1.context + (var1.animationDraw - 0.5F) * 5.0F * var3 * var1.selection;
      var1.cache = var1.config + (var1.pointEncode - 0.5F) * 3.5F * var3 * var1.selection - var1.mode * 1.2F * var3;
      var1.animator = process(process(this.responseCompute, this.providerFetch) * 0.42F * var1.selection + Math.abs(var1.data.process()) * 0.04F, 0.0F, 1.0F);
   }

   private void handle(int var1, int var2, int var3, float var4, float var5, long var6) {
      float var8 = Math.max(0.0F, (float)(var6 - this.target) / 1.0E9F);
      float var9 = process(process(this.responseCompute, this.providerFetch), 0.0F, 3.0F);
      float var10 = Math.max((float)Math.exp(-var8 * 1.28F), process(var9 * 0.24F, 0.0F, 1.0F));
      float var11 = compute(process(this.pending / 0.88F, 0.0F, 1.0F));
      float var12 = handle(var1, var2);
      float var13 = 0.0F;
      int var14 = 0;

      for (ProxyImportScreen.PrimaryFileEntry var16 : this.active) {
         this.handle(var14++, var16, var16.target);
         var13 = Math.max(var13, var16.renderer);
      }

      for (ProxyImportScreen.PrimaryScreenState var20 : this.mode) {
         this.handle(var14++, var20, var20.target);
         var13 = Math.max(var13, var20.renderer);
      }

      this.enabled.check(var14);

      for (int var19 = 0; var19 < 14; var19++) {
         ProxyImportScreen.FileEntry var21 = this.selection[var19];
         float var17 = Math.max(0.0F, this.pending - var21.context);
         this.enabled.onTick(var19).handle(var21.instance / Math.max(1.0F, var1), var21.data / Math.max(1.0F, var2), var17, var17 > 3.1F ? 0.0F : var21.config);
      }

      this.enabled.prepare().handle(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      this.enabled.handle(var1, var2, var3, this.pending * 0.58F, this.pending * 0.58F);
      this.enabled.handle(this.vectorMatch, this.itemProject, this.responseCompute * 0.56F, this.providerFetch * 0.56F, var9 * 0.56F, 0.0F);
      this.enabled.handle(this.providerClose, this.presetSave);
      this.enabled.process(-var4 * 8.0E-4F, -var5 * 6.2E-4F, var4 * 0.92F * var12, var5 * 0.78F * var12, var4 * 1.25F * var12, var5 * 1.05F * var12);
      this.enabled.compute(var10 * 0.64F, var10 > 0.1F ? 0.86F : 0.74F, 0.0F, 0.0F, 0.56F + var11 * 0.24F, process(var13, 0.0F, 1.0F));
      this.enabled
         .handle(
            this.windowConvert == ThemePalette.SAKURA_BREEZE,
            this.windowConvert == ThemePalette.VERNAL_SOLSTICE,
            this.windowConvert == ThemePalette.MIDNIGHT_AZURE,
            this.presetWrite
         );
   }

   private void handle(int var1, ProxyImportScreen.ScreenState var2, float var3) {
      this.enabled
         .handle(var1)
         .handle(
            var2.instance,
            var2.state,
            var2.cache,
            var2.output,
            var2.current,
            var2.active,
            var2.mode,
            var2.selection,
            var2.enabled,
            var3,
            var2.renderer,
            var2.source,
            var2.handler,
            var2.animationDraw,
            var2.pointEncode,
            var2.animator
         );
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1) {
      try {
         WildClient.check();
         RoundedRectRenderer var2 = WildClient.handle();
         if (var2 == null) {
            return;
         }

         OpenGlStateSnapshot.process(this.handler);

         try {
            var2.handle(var1.onTick(), var1.select());
            float var3 = handle(var1.onTick(), var1.select());
            float var4 = compute(process(this.pending / 0.82F, 0.0F, 1.0F));
            float var5 = var1.onTick() * 0.5F + var1.advancePosition() * 0.12F;
            float var6 = var1.select() * 0.126F + var1.checkFrame() * 0.08F;
            var2.handle(FontRegistry.config, var5, var6, 40.0F * var3, "Proxy", this.handle(0.94F * var4), "c");
            var2.handle(
               FontRegistry.instance, var5, var6 + 30.0F * var3, 24.0F * var3, this.rendererScan + "  /  " + this.sourceBuild, this.process(0.52F * var4), "c"
            );

            for (ProxyImportScreen.PrimaryFileEntry var8 : this.active) {
               this.handle(var2, var8, var3);
            }

            for (ProxyImportScreen.PrimaryScreenState var15 : this.mode) {
               this.handle(var2, var15, var3);
            }

            this.handle(var2, var1, var3);
            var2.process();
         } finally {
            OpenGlStateSnapshot.compute(this.handler);
         }
      } catch (Throwable var13) {
      }
   }

   private void handle(RoundedRectRenderer var1, WildMainMenuScreen.PrimaryNetworkState var2, float var3) {
      String var4 = this.prepare();
      if (!var4.isBlank()) {
         float var5 = 25.0F * var3;
         float var6 = var2.onTick() * 0.5F;
         float var7 = var2.select() - 30.0F * var3;
         var1.handle(FontRegistry.instance, var6, var7, var5, var4, this.process(0.4F * var2.writePreset()), "c");
      }
   }

   private void handle(RoundedRectRenderer var1, ProxyImportScreen.PrimaryFileEntry var2, float var3) {
      float var4 = var2.target;
      String var5 = var2.pending ? "*".repeat(var2.summary.length()) : var2.summary;
      boolean var6 = var5.isBlank();
      float var7 = 22.0F * var3;
      float var8 = var2.state + var7;
      float var9 = Math.max(8.0F * var3, var2.output - var7 * 2.0F);
      float var10 = 25.0F * var3;
      boolean var11 = var6 && !var2.vectorMatch;
      String var12 = var11 ? var2.instance : var5;
      int var13 = var11 ? this.process(0.42F * var4) : this.handle((0.78F + var2.responseCompute * 0.18F) * var4);
      if (!var6 || var2.vectorMatch) {
         var1.handle(
            FontRegistry.instance,
            var2.state + 18.0F * var3,
            var2.cache - 7.0F * var3,
            20.0F * var3,
            var2.instance,
            this.process((0.3F + var2.responseCompute * 0.28F) * var4)
         );
      }

      var1.handle(
         var8, var2.cache + 3.0F * var3, var9, var2.current - 6.0F * var3, var2.active * 0.55F, var2.active * 0.55F, var2.active * 0.55F, var2.active * 0.55F
      );
      if (!var12.isBlank()) {
         if (var11) {
            handle(var1, FontRegistry.instance, var2.state, var2.cache, var2.output, var2.current, var10, var12, var13);
         } else {
            float var14 = handle(FontRegistry.instance, var10, var2.cache, var2.current);
            if (var2.itemProject) {
               float var15 = RoundedRectRenderer.handle(FontRegistry.instance, var12, var10).instance;
               var1.handle(
                  resolve(var8 - var2.profileDraw - 2.0F * var3),
                  var2.cache + var2.current * 0.25F,
                  var15 + 4.0F * var3,
                  var2.current * 0.5F,
                  2.0F * var3,
                  handle(0.25F, 0.55F, 0.95F, 0.45F * var4)
               );
            }

            var1.handle(FontRegistry.instance, resolve(var8 - var2.profileDraw), resolve(var14), var10, var12, var13);
         }
      }

      if (var2.vectorMatch) {
         int var24 = handle(var2.matrixBlend, 0, var5.length());
         String var25 = var5.substring(0, var24);
         float var16 = RoundedRectRenderer.handle(FontRegistry.instance, var25, var10).instance;
         if (var2.itemProject) {
            var16 = RoundedRectRenderer.handle(FontRegistry.instance, var5, var10).instance;
         }

         float var17 = var2.profileDraw;
         float var18 = var9 - 9.0F * var3;
         if (var16 - var17 > var18) {
            var17 = var16 - var18;
         }

         if (var16 - var17 < 0.0F) {
            var17 = var16;
         }

         var17 = Math.max(0.0F, var17);
         var2.profileDraw = var2.profileDraw + (var17 - var2.profileDraw) * 0.3F;
         var2.providerFetch = var2.providerFetch + (var16 - var2.providerFetch) * 0.3F;
         float var19 = 0.54F + 0.46F * (float)Math.sin(this.pending * 5.4F);
         if (!var2.itemProject) {
            int var20 = handle(this.presetSave, this.providerClose, var19, (0.42F + var19 * 0.36F) * var4);
            float var21 = var8 + var2.providerFetch - var2.profileDraw + 2.0F * var3;
            float var22 = 20.0F * var3;
            float var23 = var2.cache + (var2.current - var22) * 0.5F;
            var1.handle(resolve(var21), resolve(var23), Math.max(1.25F * var3, 1.0F), var22, 1.0F * var3, var20);
         }

         var1.handle(
            var2.state,
            var2.cache,
            var2.output,
            var2.current,
            var2.active,
            handle(this.presetSave, this.providerClose, var19, 0.24F * var4 * (0.35F + var2.responseCompute * 0.65F)),
            1.0F * var3
         );
      }

      var1.apply();
   }

   private void handle(RoundedRectRenderer var1, ProxyImportScreen.PrimaryScreenState var2, float var3) {
      float var4 = var2.target * 0.9F;
      String var5 = handle(var2.instance, var2.output - 18.0F * var3, 24.0F * var3, FontRegistry.instance);
      handle(var1, FontRegistry.instance, var2.state, var2.cache, var2.output, var2.current, 24.0F * var3, var5, this.handle(var4));
   }

   private String process(ProxyImportScreen.Mode var1) {
      return switch (var1) {
         case TYPE -> this.rendererScan;
         case ENABLED -> this.animationSchedule ? "Enabled" : "Disabled";
         case PASTE -> "Paste";
         case TEST -> "Test";
         case SAVE -> "Save";
         case BACK -> "Back";
      };
   }

   private String prepare() {
      String var1 = this.state.summary.trim();
      String var2 = this.cache.summary.trim();
      if (var1.isBlank() || var2.isBlank()) {
         return "";
      } else {
         return "Socks5".equals(this.rendererScan) && !this.output.summary.isBlank()
            ? this.output.summary + ":" + "*".repeat(Math.min(10, this.current.summary.length())) + "@" + var1 + ":" + var2
            : var1 + ":" + var2;
      }
   }

   private int handle(float var1) {
      return this.presetWrite ? handle(0.1F, 0.1F, 0.1F, var1) : handle(1.0F, 1.0F, 1.0F, var1);
   }

   private int process(float var1) {
      return this.presetWrite ? handle(0.4F, 0.4F, 0.4F, var1) : handle(0.8F, 0.86F, 0.9F, var1);
   }

   private static void handle(RoundedRectRenderer var0, FontObject var1, float var2, float var3, float var4, float var5, float var6, String var7, int var8) {
      String var9 = var7 == null ? "" : var7;
      float var10 = RoundedRectRenderer.handle(var1, var9, var6).instance;
      float var11 = resolve(var2 + (var4 - var10) * 0.5F);
      float var12 = resolve(handle(var1, var6, var3, var5));
      var0.handle(var1, var11, var12, var6, var9, var8);
   }

   private float handle(Window var1, double var2) {
      return (float)(var2 * var1.getFramebufferWidth() / Math.max(1.0, var1.getScaledWidth()));
   }

   private float process(Window var1, double var2) {
      return (float)(var2 * var1.getFramebufferHeight() / Math.max(1.0, var1.getScaledHeight()));
   }

   private static float handle(FontObject var0, float var1, float var2, float var3) {
      try {
         return var2 + var3 * 0.5F + FontRegistry.handle(var0, 72, var1 * 0.5F);
      } catch (Throwable var5) {
         return var2 + var3 * 0.5F + var1 * 0.18F;
      }
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
      return process(Math.min(var0 / 1920.0F, var1 / 1080.0F) * 1.16F, 0.72F, 1.34F);
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

   private static float compute(float var0) {
      float var1 = process(var0, 0.0F, 1.0F);
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   static int handle(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static float resolve(float var0) {
      return Math.round(var0);
   }

   private static float process(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   private static float compute(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   private static float resolve(int var0) {
      return (var0 & 0xFF) / 255.0F;
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

   static final class FileEntry {
      float instance;
      float data;
      float context = -100.0F;
      float config;
   }

   enum Mode {
      TYPE,
      ENABLED,
      PASTE,
      TEST,
      SAVE,
      BACK;
   }

   static final class PrimaryFileEntry extends ProxyImportScreen.ScreenState {
      final boolean pending;
      private final int previous;
      private final ProxyImportScreen.PrimaryMode latest;
      String summary = "";
      int matrixBlend;
      boolean vectorMatch;
      boolean itemProject;
      float responseCompute;
      float providerFetch;
      float profileDraw;

      PrimaryFileEntry(String var1, boolean var2, int var3, ProxyImportScreen.PrimaryMode var4) {
         super(var1);
         this.pending = var2;
         this.previous = var3;
         this.latest = var4;
      }

      void handle(String var1) {
         this.summary = this.compute(var1 == null ? "" : var1);
         if (this.summary.length() > this.previous) {
            this.summary = this.summary.substring(0, this.previous);
         }

         this.matrixBlend = this.summary.length();
         this.itemProject = false;
         this.providerFetch = 0.0F;
         this.profileDraw = 0.0F;
      }

      void process(String var1) {
         String var2 = this.compute(var1 == null ? "" : var1);
         if (!var2.isEmpty()) {
            if (this.itemProject) {
               this.summary = "";
               this.matrixBlend = 0;
               this.itemProject = false;
            }

            int var3 = this.previous - this.summary.length();
            if (var3 > 0) {
               if (var2.length() > var3) {
                  var2 = var2.substring(0, var3);
               }

               int var4 = ProxyImportScreen.handle(this.matrixBlend, 0, this.summary.length());
               this.summary = this.summary.substring(0, var4) + var2 + this.summary.substring(var4);
               this.matrixBlend = var4 + var2.length();
            }
         }
      }

      void process() {
         if (this.itemProject) {
            this.resolve();
         } else if (this.matrixBlend > 0 && !this.summary.isEmpty()) {
            int var1 = ProxyImportScreen.handle(this.matrixBlend, 0, this.summary.length());
            if (var1 > 0) {
               this.summary = this.summary.substring(0, var1 - 1) + this.summary.substring(var1);
               this.matrixBlend = var1 - 1;
            }
         }
      }

      void compute() {
         if (this.itemProject) {
            this.resolve();
         } else {
            int var1 = ProxyImportScreen.handle(this.matrixBlend, 0, this.summary.length());
            if (var1 < this.summary.length()) {
               this.summary = this.summary.substring(0, var1) + this.summary.substring(var1 + 1);
               this.matrixBlend = var1;
            }
         }
      }

      private void resolve() {
         this.summary = "";
         this.matrixBlend = 0;
         this.providerFetch = 0.0F;
         this.profileDraw = 0.0F;
         this.itemProject = false;
      }

      void update() {
         this.handle();
         this.vectorMatch = false;
         this.itemProject = false;
         this.responseCompute = 0.0F;
         this.providerFetch = 0.0F;
         this.profileDraw = 0.0F;
         this.matrixBlend = ProxyImportScreen.handle(this.matrixBlend, 0, this.summary.length());
      }

      private String compute(String var1) {
         StringBuilder var2 = new StringBuilder(var1.length());

         for (int var3 = 0; var3 < var1.length(); var3++) {
            char var4 = var1.charAt(var3);
            if (var4 >= ' '
               && var4 != 127
               && (this.latest != ProxyImportScreen.PrimaryMode.PORT || var4 >= '0' && var4 <= '9')
               && (this.latest != ProxyImportScreen.PrimaryMode.HOST && this.latest != ProxyImportScreen.PrimaryMode.TEXT || !Character.isWhitespace(var4))) {
               var2.append(var4);
            }
         }

         return var2.toString();
      }
   }

   enum PrimaryMode {
      HOST,
      PORT,
      TEXT,
      SECRET;
   }

   static final class PrimaryScreenState extends ProxyImportScreen.ScreenState {
      final ProxyImportScreen.Mode pending;

      PrimaryScreenState(String var1, ProxyImportScreen.Mode var2) {
         super(var1);
         this.pending = var2;
      }
   }

   static class ScreenState {
      protected String instance;
      protected final SmoothedValue data = new SmoothedValue(SpringAnimationSpec.update());
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
      protected float renderer;
      protected float handler = 1.0F;
      protected float animationDraw = 0.5F;
      protected float pointEncode = 0.5F;
      protected float animator;
      protected float source;
      protected float target;

      protected ScreenState(String var1) {
         this.instance = var1;
      }

      protected boolean handle(float var1, float var2) {
         return ProxyImportScreen.handle(var1, var2, this.context, this.config, this.output, this.current, this.active) <= 0.0F;
      }

      protected void handle() {
         this.mode = 0.0F;
         this.selection = 0.0F;
         this.enabled = 0.0F;
         this.renderer = 0.0F;
         this.animator = 0.0F;
         this.target = 0.0F;
         this.handler = 1.0F;
         this.animationDraw = 0.5F;
         this.pointEncode = 0.5F;
         this.data.handle(1.0F);
      }
   }
}
