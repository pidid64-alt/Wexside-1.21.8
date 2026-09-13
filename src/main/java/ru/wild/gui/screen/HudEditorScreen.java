package ru.wild.gui.screen;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.HudRenderContext;
import ru.wild.config.HudProfileGate;
import ru.wild.core.manager.HudElementRegistry;
import ru.wild.gui.hud.ArmorHudRenderer;
import ru.wild.gui.hud.CooldownHud;
import ru.wild.gui.hud.HotbarHud;
import ru.wild.gui.hud.InformationHudRenderer;
import ru.wild.gui.hud.KeybindHud;
import ru.wild.gui.hud.ModuleListHudRenderer;
import ru.wild.gui.hud.MusicPlayerHudRenderer;
import ru.wild.gui.hud.NotificationHudSettings;
import ru.wild.gui.hud.PotionHudRenderer;
import ru.wild.gui.hud.ServerItemBindingsHud;
import ru.wild.gui.hud.SlotHudRenderer;
import ru.wild.gui.hud.TargetHudRenderer;
import ru.wild.gui.hud.WaterMarkRenderer;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.RoundedRectRenderer;

public final class HudEditorScreen extends Screen {
   private static volatile boolean instance;
   private static final String data = "panel";
   private static final String context = "header";
   private static final String config = "modules";
   private static final String state = "binds";
   private static final String cache = "content";
   private static final String output = "title";
   private static final String current = "icon";
   private static final String active = "slots";
   private static final String mode = "panelRadius";
   private static final String selection = "headerRadius";
   private static final String enabled = "contentRadius";
   private static final String renderer = "modulesRadius";
   private static final String handler = "bindsRadius";
   private static final String animationDraw = "rowRadius";
   private static final String pointEncode = "slotRadius";
   private static final String animator = "padding";
   private static final String source = "gap";
   private static final String target = "headerHeight";
   private static final String pending = "rowHeight";
   private static final String previous = "titleSize";
   private static final String latest = "iconSize";
   private static final String summary = "bindWidth";
   private static final String matrixBlend = "accentWidth";
   private static final String vectorMatch = "reset";
   private static final String itemProject = "centerX";
   private static final String responseCompute = "centerY";
   private static final String providerFetch = "presetSoft";
   private static final String profileDraw = "presetCompact";
   private static final String vectorPerform = "presetSharp";
   private static final String eventAttach = "CORNERS";
   private static final String serverRead = "SPACING";
   private static final String positionAdvance = "SIZE";
   private static final String frameCheck = "TYPOGRAPHY";
   private static final String moduleCollect = "ACTIONS";
   private static final String providerClose = "PRESETS";
   private static final String presetSave = "drag surface  ·  resize corner";
   private static final String[] windowConvert = new String[]{"panel", "header", "modules", "binds", "content", "title", "icon"};
   private static final String[] presetWrite = new String[]{"panel", "header", "content", "slots", "title", "icon"};
   private static final String[] colorMeasure = new String[]{"panel", "content", "slots"};
   private static final String[] animationSchedule = new String[]{"panel", "content", "modules"};
   private static final String[] rendererScan = new String[]{"panel", "content", "modules", "slots"};
   private static final String[] sourceBuild = new String[]{"panel", "content", "modules", "binds"};
   private static final String[] outputCollapse = new String[]{"panel", "header", "modules", "content", "title", "icon"};
   private static final String[] profileInvoke = new String[]{"panel", "header", "modules", "binds", "content", "title", "icon", "slots"};
   private static final String[] sourceSchedule = new String[]{"reset", "centerX", "centerY", "presetSoft", "presetCompact", "presetSharp"};
   private static final HudEditorScreen.DataRecord[] timerRender = new HudEditorScreen.DataRecord[]{
      new HudEditorScreen.DataRecord("panelRadius", "Panel radius", "CORNERS", 0.0F, 32.0F),
      new HudEditorScreen.DataRecord("headerRadius", "Header radius", "CORNERS", 0.0F, 28.0F),
      new HudEditorScreen.DataRecord("contentRadius", "Content radius", "CORNERS", 0.0F, 24.0F),
      new HudEditorScreen.DataRecord("modulesRadius", "Modules radius", "CORNERS", 0.0F, 24.0F),
      new HudEditorScreen.DataRecord("bindsRadius", "Binds radius", "CORNERS", 0.0F, 24.0F),
      new HudEditorScreen.DataRecord("rowRadius", "Row radius", "CORNERS", 0.0F, 22.0F),
      new HudEditorScreen.DataRecord("slotRadius", "Slot radius", "CORNERS", 0.0F, 14.0F),
      new HudEditorScreen.DataRecord("padding", "Padding", "SPACING", 2.0F, 18.0F),
      new HudEditorScreen.DataRecord("gap", "Gap", "SPACING", 0.0F, 18.0F),
      new HudEditorScreen.DataRecord("headerHeight", "Header height", "SIZE", 0.0F, 48.0F),
      new HudEditorScreen.DataRecord("rowHeight", "Row height", "SIZE", 14.0F, 42.0F),
      new HudEditorScreen.DataRecord("titleSize", "Title size", "TYPOGRAPHY", 14.0F, 38.0F),
      new HudEditorScreen.DataRecord("iconSize", "Icon size", "TYPOGRAPHY", 12.0F, 38.0F),
      new HudEditorScreen.DataRecord("bindWidth", "Bind column", "TYPOGRAPHY", -24.0F, 90.0F),
      new HudEditorScreen.DataRecord("accentWidth", "Accent width", "TYPOGRAPHY", 0.0F, 7.0F)
   };
   private static final HudEditorScreen.NamedEntry[] scaleSave = new HudEditorScreen.NamedEntry[]{
      new HudEditorScreen.NamedEntry("HUD_HotKeys", "KeyBinds", "HotKeys", FontRegistry.current, "q", HudEditorScreen.Mode.KEYBINDS, true),
      new HudEditorScreen.NamedEntry("HUD_Inventory", "Inventory", "Inventory", FontRegistry.state, "h", HudEditorScreen.Mode.INVENTORY, true),
      new HudEditorScreen.NamedEntry("HUD_Potions", "Potions", "Potions", FontRegistry.state, "t", HudEditorScreen.Mode.POTIONS, true),
      new HudEditorScreen.NamedEntry("HUD_CoolDowns", "Cooldowns", "Cool Downs", FontRegistry.state, "g", HudEditorScreen.Mode.COOLDOWNS, true),
      new HudEditorScreen.NamedEntry("HUD_Info", "Information", "PlayerInfo", FontRegistry.state, "e", HudEditorScreen.Mode.INFO, true),
      new HudEditorScreen.NamedEntry("HUD_WaterMark", "Watermark", "Watermark", FontRegistry.current, "w", HudEditorScreen.Mode.WATERMARK, true),
      new HudEditorScreen.NamedEntry("HUD_ArrayList", "ArrayList", "ArrayList", FontRegistry.current, "n", HudEditorScreen.Mode.ARRAYLIST, false),
      new HudEditorScreen.NamedEntry("HUD_TargetHUD", "TargetHUD", "TargetHud", FontRegistry.current, "r", HudEditorScreen.Mode.TARGET, false),
      new HudEditorScreen.NamedEntry("hud_armor", "Armor", "Armor", FontRegistry.state, "h", HudEditorScreen.Mode.SLOTS, false),
      new HudEditorScreen.NamedEntry("HUD_HotBar", "HotBar", "HotBar", FontRegistry.state, "h", HudEditorScreen.Mode.HOTBAR, false),
      new HudEditorScreen.NamedEntry("HUD_Notifications", "Notifications", "Notifications", FontRegistry.current, "l", HudEditorScreen.Mode.NOTIFICATION, false),
      new HudEditorScreen.NamedEntry("HUD_MusicPlayer", "Media", "MediaPlayer", FontRegistry.current, "m", HudEditorScreen.Mode.MEDIA, false),
      new HudEditorScreen.NamedEntry("HUD_ServerHelper", "Server", "Server Helper", FontRegistry.state, "e", HudEditorScreen.Mode.SERVER, false)
   };
   private static final String[] colorCompute = new String[]{"HitAura", "AutoTotem", "Speed", "InventoryMove"};
   private static final String[] scaleAdapt = new String[]{"R", "F", "V", "G"};
   private static final String[] textureRun = new String[]{"Strength III", "Fire Resistance", "Poison II"};
   private static final String[] indexBind = new String[]{"1:58", "6:40", "0:12"};
   private static final String[] actionRead = new String[]{"Ender Pearl", "Golden Apple", "Chorus Fruit"};
   private static final String[] configCollapse = new String[]{"8.4s", "2.1s", "0.7s"};
   private static final String[] dataValidate = new String[]{"BPS", "TPS", "XYZ", "PING"};
   private static final String[] scaleRender = new String[]{"7.42", "20.0", "120 64 -80", "42 ms"};
   private static final String[] clientRefresh = new String[]{"Module toggled", "Config saved", "Friend joined"};
   private static final String[] keyFilter = new String[]{"now", "1s", "4s"};
   private static final String[] requestAdapt = new String[]{"wild", "fr1zy", "144 fps", "12:40"};
   private static final String[] timerMeasure = new String[]{"w", "r", "u", "y"};
   private static final String[] vectorEncode = new String[]{"HitAura", "AutoTotem", "ElytraFly", "NoSlow"};
   private static final String[] requestReceive = new String[]{"Midnight Drive", "2:18 / 3:42", "Volume"};
   private static final String[] windowProcess = new String[]{"PLAYING", "", "72%"};
   private static final String[] packetSave = new String[]{"FunTime", "Anarchy-01", "Online"};
   private static final String[] entryAnimate = new String[]{"EU", "42 ms", "128"};
   private static final String[] playerCollect = new String[]{"", "", "", ""};
   private static final String[] stateApply = new String[]{
      "Keys", "Inventory", "Potions", "Cooldowns", "Info", "Watermark", "ArrayList", "Target", "Armor", "HotBar", "Alerts", "Media", "Server"
   };
   private static final String matrixFilter = "preview.resize";
   private final HudEditorScreen.LayoutMetrics[] layerSample = new HudEditorScreen.LayoutMetrics[scaleSave.length];
   private final Map<String, HudEditorScreen.LayoutMetrics> worldSend = new HashMap<>();
   private final Map<String, HudEditorScreen.LayoutMetrics> targetWrite = new HashMap<>();
   private final Map<String, HudEditorScreen.LayoutMetrics> resultEncode = new HashMap<>();
   private final Map<String, DoubleAnimator> messageParse = new HashMap<>();
   private final Map<String, DoubleAnimator> providerRead = new HashMap<>();
   private final Map<String, DoubleAnimator> matrixBlend2 = new HashMap<>();
   private final Map<String, String> scalePerform = new HashMap<>();
   private final ThemeRenderer.CacheEntry[] contextExpand = new ThemeRenderer.CacheEntry[8];
   private final DoubleAnimator keyProcess = new DoubleAnimator();
   private final DoubleAnimator actionConvert = new DoubleAnimator();
   private final DoubleAnimator screenRead = new DoubleAnimator();
   private final DoubleAnimator animationExpand = new DoubleAnimator();
   private final DoubleAnimator playerRun = new DoubleAnimator();
   private final DoubleAnimator matrixRender = new DoubleAnimator();
   private final DoubleAnimator moduleTick = new DoubleAnimator();
   private final DoubleAnimator playerCollapse = new DoubleAnimator();
   private String optionAdvance;
   private float effectScan;
   private float optionParse;
   private float pointSubmit;
   private float listenerPerform;
   private float configMatch = 1.0F;
   private float actionRender = 1.0F;
   private float playerApply;
   private float bufferAdapt;
   private final float[] playerUpdate = new float[scaleSave.length];
   private final float[] packetRead = new float[scaleSave.length];
   private boolean rendererCancel;
   private boolean eventReceive;
   private boolean screenSubmit;
   private boolean cacheHandle;
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
   private boolean sessionEncode;
   private boolean elementTick;
   private HudEditorScreen.LayoutMetrics regionAlign = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics resourceClamp = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics handlerRun = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics keyCheck = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics layerProject = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics entityFilter = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics layerSample2 = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics sourceCancel = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics eventSend = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics providerOffset = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics messageParse2 = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics shaderProject = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics inputInvoke = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics optionFetch = HudEditorScreen.LayoutMetrics.process();
   private String eventCollapse = "panel";
   private String stateAttach;
   private String worldEvaluate;
   private int playerProject;
   private String playerMatch = "";
   private String cacheClose = "";
   private String scaleSetup = "";
   private float indexSynchronize = 1.0F;
   private float taskInterpolate;
   private float sourceRefresh;
   private HudEditorScreen.LayoutMetrics playerSave = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics requestRun = HudEditorScreen.LayoutMetrics.process();
   private HudEditorScreen.LayoutMetrics frameProject = HudEditorScreen.LayoutMetrics.process();
   private static final ThemePaletteRegistry dataRelease = ThemePaletteRegistry.handle();
   private ThemePalette vectorRun;
   private ThemeColors providerSynchronize;

   public HudEditorScreen() {
      super(Text.literal("HUD Constructor"));
      handle();
      this.keyProcess.apply(0.0);

      for (int var1 = 0; var1 < this.layerSample.length; var1++) {
         this.layerSample[var1] = HudEditorScreen.LayoutMetrics.process();
      }

      for (int var5 = 0; var5 < this.contextExpand.length; var5++) {
         this.contextExpand[var5] = new ThemeRenderer.CacheEntry();
      }

      for (HudEditorScreen.DataRecord var4 : timerRender) {
         this.worldSend.put(var4.id, HudEditorScreen.LayoutMetrics.process());
      }

      for (String var10 : profileInvoke) {
         this.targetWrite.put(var10, HudEditorScreen.LayoutMetrics.process());
      }

      this.resultEncode.put("close", HudEditorScreen.LayoutMetrics.process());
      this.resultEncode.put("reset", HudEditorScreen.LayoutMetrics.process());
      this.resultEncode.put("centerX", HudEditorScreen.LayoutMetrics.process());
      this.resultEncode.put("centerY", HudEditorScreen.LayoutMetrics.process());
      this.resultEncode.put("presetSoft", HudEditorScreen.LayoutMetrics.process());
      this.resultEncode.put("presetCompact", HudEditorScreen.LayoutMetrics.process());
      this.resultEncode.put("presetSharp", HudEditorScreen.LayoutMetrics.process());
      this.drawAnimation();
      this.encodePoint();
      this.animate();
   }

   public boolean shouldPause() {
      return false;
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      this.prepare(this.handle((double)var2), this.process((double)var3));
      super.render(var1, var2, var3, var4);
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   public void renderInGameBackground(DrawContext var1) {
   }

   public void handle(RoundedRectRenderer var1, DrawContext var2, int var3, int var4) {
      if (!HudProfileGate.handle()) {
         if (this.client != null) {
            this.client.setScreen(null);
         }
      } else if (var1 != null && var3 > 0 && var4 > 0) {
         this.load();
         this.keyProcess.handle();
         this.keyProcess.handle(1.0, 0.42F, Easings.handler, false);
         this.actionConvert.handle();
         this.actionConvert.handle(this.stateAttach == null && !this.rendererCancel && !this.eventReceive ? 0.0 : 1.0, 0.18F, Easings.handler, false);
         float var5 = handle(this.keyProcess.update(), 0.0F, 1.0F);
         ThemeColors var6 = this.compute();
         this.handle(var3, var4);
         this.handle(var1, var3, var4, var5, var6);
         var1.update(var5);
         float var7 = 0.945F + 0.055F * var5;
         var1.compute(var7, var7, var3 * 0.5F, var4 * 0.5F);
         var1.handle(0.0F, (1.0F - var5) * this.handle(24.0F));

         try {
            this.handle(var1, var6);
            this.process(var1, var6);
            this.compute(var1, var6);
            this.resolve(var1, var6);
         } finally {
            var1.prepare();
            var1.check();
            var1.onTick();
         }
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.prepare(this.handle(var1), this.process((double)var3));
      if (var5 != 0) {
         return true;
      }

      HudEditorScreen.LayoutMetrics var6 = this.resultEncode.get("close");
      if (var6 != null && var6.handle(this.effectScan, this.optionParse)) {
         this.close();
         return true;
      }

      if (this.requestRun.handle(this.effectScan, this.optionParse)) {
         for (int var7 = 0; var7 < this.layerSample.length; var7++) {
            if (this.layerSample[var7].handle(this.effectScan, this.optionParse)) {
               this.playerProject = var7;
               this.eventCollapse = this.process(scaleSave[var7].kind);
               this.playerApply = 0.0F;
               this.playerUpdate[var7] = 0.0F;
               this.packetRead[var7] = 0.0F;
               this.encodePoint();
               this.animate();
               return true;
            }
         }
      }

      String var12 = this.apply(this.effectScan, this.optionParse);
      if (var12 != null) {
         this.handle(var12);
         return true;
      }

      String var8 = this.execute(this.effectScan, this.optionParse);
      if (var8 != null) {
         this.eventCollapse = var8;
         this.animate();
         return true;
      }

      String var9 = this.update(this.effectScan, this.optionParse);
      if (var9 != null) {
         this.worldEvaluate = var9;
         this.process(this.effectScan);
         return true;
      }

      if (this.inputInvoke.handle(this.effectScan, this.optionParse)) {
         this.eventReceive = true;
         this.eventCollapse = "panel";
         this.rangeRelease = this.effectScan;
         this.indexSave = this.optionParse;
         this.indexCheck = Math.max(1.0F, this.keyCheck.context);
         this.settingSchedule = Math.max(1.0F, this.keyCheck.config);
         this.inputAcquire = this.indexCheck / Math.max(0.001F, this.configMatch);
         this.listenerRun = this.settingSchedule / Math.max(0.001F, this.actionRender);
         this.indexLoad = ThemeRenderer.handle().handle(this.refresh(), this.inputAcquire, this.listenerRun);
         this.layoutSave = Math.max(0.001F, this.configMatch);
         this.blockRun = this.playerUpdate[this.playerProject];
         this.playerEvaluate = this.packetRead[this.playerProject];
         ThemeRenderer.DataRecord var13 = ThemeRenderer.handle().update().get(this.refresh());
         this.outputFetch = var13 == null ? 0.5F : var13.nx();
         this.scaleParse = var13 == null ? 0.5F : var13.ny();
         this.pointSubmit = this.effectScan;
         this.listenerPerform = this.optionParse;
         return true;
      }

      String var10 = this.handle(this.effectScan, this.optionParse);
      if (var10 == null) {
         return true;
      }

      boolean var11 = var10.equals(this.eventCollapse) && this.compute(var10);
      this.eventCollapse = var10;
      this.animate();
      if (var11) {
         this.stateAttach = var10;
      } else {
         this.rendererCancel = true;
      }

      this.pointSubmit = this.effectScan;
      this.listenerPerform = this.optionParse;
      return true;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      this.prepare(this.handle(var1), this.process((double)var3));
      this.stateAttach = null;
      this.rendererCancel = false;
      this.eventReceive = false;
      this.screenSubmit = false;
      this.cacheHandle = false;
      this.worldEvaluate = null;
      this.render2();
      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      this.prepare(this.handle(var1), this.process((double)var3));
      HudElementRegistry.ColorState var10 = this.check();
      if (this.worldEvaluate != null) {
         this.process(this.effectScan);
         return true;
      }

      if (this.eventReceive) {
         this.compute(this.effectScan, this.optionParse);
         return true;
      }

      if (this.rendererCancel) {
         this.process(this.effectScan - this.pointSubmit, this.optionParse - this.listenerPerform);
         this.pointSubmit = this.effectScan;
         this.listenerPerform = this.optionParse;
         return true;
      }

      if (this.stateAttach != null) {
         float var11 = (this.effectScan - this.pointSubmit) / Math.max(0.001F, this.configMatch);
         float var12 = (this.optionParse - this.listenerPerform) / Math.max(0.001F, this.actionRender);
         if ("title".equals(this.stateAttach)) {
            var10.pointEncode.instance += var11;
            var10.pointEncode.data += var12;
         } else if ("icon".equals(this.stateAttach)) {
            var10.animator.instance += var11;
         } else if ("modules".equals(this.stateAttach)) {
            var10.source.instance += var11;
            var10.source.data += var12;
         } else if ("binds".equals(this.stateAttach)) {
            var10.target.instance += var11;
            var10.target.data += var12;
         }

         var10.process();
         this.sessionEncode = true;
         this.animate();
         this.pointSubmit = this.effectScan;
         this.listenerPerform = this.optionParse;
         return true;
      } else {
         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      this.prepare(this.handle(var1), this.process((double)var3));
      if (this.playerSave.handle(this.effectScan, this.optionParse) && this.sourceRefresh > 0.0F) {
         this.taskInterpolate = handle(this.taskInterpolate - (float)var7 * this.handle(28.0F), 0.0F, this.sourceRefresh);
         return true;
      } else if (this.providerOffset.handle(this.effectScan, this.optionParse) && this.bufferAdapt > 0.0F) {
         this.playerApply = handle(this.playerApply - (float)var7 * this.handle(28.0F), 0.0F, this.bufferAdapt);
         return true;
      } else {
         return true;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.close();
         return true;
      } else if (var1 == 82) {
         this.apply();
         return true;
      } else if (var1 == 67) {
         this.handle(true, false);
         this.render2();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void close() {
      this.render2();
      super.close();
   }

   public void removed() {
      this.render2();
      super.removed();
   }

   private static void handle() {
      if (!instance) {
         instance = true;
         EventHandlerInvoker.handle(new Object() {
            @EventHandler(handle = 4)
            public void handle(HudRenderContext var1) {
               if (var1.compute() != null && var1.compute().currentScreen instanceof HudEditorScreen var2) {
                  var2.handle(var1.resolve(), var1.prepare(), var1.apply(), var1.execute());
                  if (var1.resolve() != null) {
                     var1.resolve().compute();
                  }
               }
            }
         });
      }
   }

   private void handle(RoundedRectRenderer var1, int var2, int var3, float var4, ThemeColors var5) {
      int var6 = var5.unload() ? handle(12, 14, 20, Math.round(48.0F * var4)) : handle(0, 0, 0, Math.round(96.0F * var4));
      var1.handle(0.0F, 0.0F, var2, var3, 0.0F, var6);
      var1.handle(
         0.0F,
         0.0F,
         var2,
         var3,
         ThemeColors.handle(var5.save(), Math.round(14.0F * var4)),
         handle(0, 0, 0, Math.round(26.0F * var4)),
         handle(0, 0, 0, Math.round(44.0F * var4)),
         ThemeColors.handle(var5.submit(), Math.round(16.0F * var4))
      );
      float var7 = this.actionConvert.update() * var4;
      if (var7 > 0.01F) {
         int var8 = this.process();
         ThemeRenderer.handle().handle(var2, var3, this.contextExpand, var8, this.stateAttach, this.effectScan, this.optionParse, var7);
      }
   }

   private int process() {
      int var1 = 0;
      var1 = this.handle(var1, "panel", this.keyCheck, "panel".equals(this.eventCollapse) ? 0.78F : 0.24F);
      var1 = this.handle(var1, "header", this.layerProject, "header".equals(this.eventCollapse) ? 0.72F : 0.22F);
      var1 = this.handle(var1, "modules", this.layerSample2, "modules".equals(this.eventCollapse) ? 0.92F : 0.34F);
      var1 = this.handle(var1, "binds", this.sourceCancel, "binds".equals(this.eventCollapse) ? 0.92F : 0.34F);
      var1 = this.handle(var1, "title", this.resourceClamp, "title".equals(this.eventCollapse) ? 0.62F : 0.18F);
      var1 = this.handle(var1, "icon", this.handlerRun, "icon".equals(this.eventCollapse) ? 0.72F : 0.2F);
      return this.handle(var1, "slots", this.eventSend, "slots".equals(this.eventCollapse) ? 0.84F : 0.24F);
   }

   private int handle(int var1, String var2, HudEditorScreen.LayoutMetrics var3, float var4) {
      if (var1 < this.contextExpand.length && var3 != null && !(var3.context <= 1.0F) && !(var3.config <= 1.0F)) {
         float var5 = (float)Math.sqrt(var3.context * var3.context + var3.config * var3.config) * 0.52F;
         this.contextExpand[var1]
            .handle(var2, var3.instance + var3.context * 0.5F, var3.data + var3.config * 0.5F, Math.max(24.0F, var5), var4, var3.context, var3.config);
         return var1 + 1;
      } else {
         return var1;
      }
   }

   private void handle(int var1, int var2) {
      this.indexSynchronize = handle(var2 / 760.0F, 0.82F, 3.0F);
      float var3 = Math.max(this.handle(28.0F), var1 * 0.05F);
      float var4 = Math.max(this.handle(24.0F), var2 * 0.06F);
      float var5 = Math.max(this.handle(320.0F), var1 - var3 * 2.0F);
      float var6 = Math.max(this.handle(240.0F), var2 - var4 * 2.0F);
      float var7 = handle(var1 * 0.76F, Math.min(this.handle(560.0F), var5), var5);
      float var8 = handle(var2 * 0.78F, Math.min(this.handle(380.0F), var6), var6);
      float var9 = var7 / Math.max(1.0F, var8);
      if (var9 > 2.05F) {
         var7 = var8 * 2.05F;
      } else if (var9 < 1.34F) {
         var8 = var7 / 1.34F;
      }

      float var10 = (var1 - var7) * 0.5F;
      float var11 = (var2 - var8) * 0.5F;
      this.regionAlign.handle(Math.round(var10), Math.round(var11), Math.round(var7), Math.round(var8));
      var10 = this.regionAlign.instance;
      var11 = this.regionAlign.data;
      var7 = this.regionAlign.context;
      var8 = this.regionAlign.config;
      float var12 = this.handle(14.0F);
      float var13 = var11 + this.handle(52.0F);
      float var14 = var11 + var8 - var12;
      float var15 = Math.max(this.handle(120.0F), var14 - var13);
      float var16 = this.handle(12.0F);
      float var17 = var10 + var12;
      float var18 = var7 - var12 * 2.0F;
      float var19 = this.handle(150.0F);
      float var20 = this.handle(300.0F);
      float var21 = handle(var18 * 0.22F, Math.min(var19, var18 * 0.3F), var20);
      float var22 = handle(var18 * 0.26F, Math.min(var19, var18 * 0.3F), var20);
      float var23 = var18 - var21 - var22 - var16 * 2.0F;
      float var24 = var18 * 0.34F;
      if (var23 < var24 && var21 + var22 > 0.0F) {
         float var25 = var24 - var23;
         float var26 = var21 + var22;
         var21 -= var25 * (var21 / var26);
         var22 -= var25 * (var22 / var26);
         var23 = var18 - var21 - var22 - var16 * 2.0F;
      }

      this.playerSave.handle(Math.round(var17), Math.round(var13), Math.round(var21), Math.round(var15));
      this.frameProject.handle(Math.round(var17 + var21 + var16), Math.round(var13), Math.round(Math.max(this.handle(80.0F), var23)), Math.round(var15));
      this.providerOffset
         .handle(Math.round(var17 + var21 + var16 + this.frameProject.context + var16), Math.round(var13), Math.round(var22), Math.round(var15));
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2) {
      float var3 = this.regionAlign.instance;
      float var4 = this.regionAlign.data;
      float var5 = this.regionAlign.context;
      float var6 = this.regionAlign.config;
      float var7 = this.handle(18.0F);
      var1.handle(var3, var4, var5, var6, var7, this.handle(30.0F), this.handle(8.0F), handle(0, 0, 0, var2.unload() ? 34 : 150));
      var1.handle(var3, var4, var5, var6, var7, this.handle(6.0F), this.handle(5.0F), handle(var2, var2.unload() ? 14 : 24));
      var1.handle(30.0F);
      var1.handle(var3, var4, var5, var6, var7, var2.unload() ? 0.96F : 0.92F);
      var1.handle(var3, var4, var5, var6, var7, var2.apply());
      var1.handle(var3, var4, var5, var6, var7, var2.render(), Math.max(1.0F, this.handle(1.0F)));
      var1.handle(
         FontRegistry.config,
         var3 + this.handle(24.0F),
         onTick(var4 + this.handle(30.0F), this.handle(21.0F)),
         this.handle(21.0F),
         "HUD Constructor",
         var2.load()
      );
      float var8 = Math.round(this.handle(30.0F));
      HudEditorScreen.LayoutMetrics var9 = this.resultEncode.get("close");
      var9.handle(Math.round(var3 + var5 - var8 - this.handle(16.0F)), Math.round(var4 + this.handle(30.0F) - var8 * 0.5F), var8, var8);
      float var10 = this.process("close", var9.handle(this.effectScan, this.optionParse) ? 1.0F : 0.0F);
      float var11 = var9.instance + var9.context * 0.5F;
      float var12 = var9.data + var9.config * 0.5F;
      var1.handle(
         var9.instance,
         var9.data,
         var9.context,
         var9.config,
         this.handle(9.0F),
         ThemeColors.handle(ThemeColors.handle(var2.load(), var2.unload() ? 14 : 10), ThemeColors.handle(var2.process(), 46), var10)
      );
      var1.handle(
         var9.instance,
         var9.data,
         var9.context,
         var9.config,
         this.handle(9.0F),
         ThemeColors.handle(var2.render(), ThemeColors.handle(var2.process(), 90), var10),
         1.0F
      );
      this.handle(var1, var11, var12, this.handle(5.0F), Math.max(1.5F, this.handle(2.0F)), ThemeColors.handle(handle(var2), var2.process(), var10 * 0.85F));
   }

   private float handle(float var1) {
      return var1 * this.indexSynchronize;
   }

   private ThemeColors compute() {
      ThemePalette var1 = null;

      try {
         if (WildClient.instance != null && WildClient.instance.selection != null) {
            var1 = WildClient.instance.selection.process();
         }
      } catch (Throwable var3) {
      }

      if (var1 == null) {
         var1 = ThemePalette.WILD;
      }

      if (var1 != this.vectorRun || this.providerSynchronize == null) {
         this.vectorRun = var1;
         this.providerSynchronize = ThemeColors.handle(var1, dataRelease.compute(var1));
      }

      return this.providerSynchronize;
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5, float var6) {
      float var7 = Math.round(var3);
      float var8 = Math.round(var4);
      float var9 = Math.round(var5);
      float var10 = Math.round(var6);
      float var11 = this.handle(14.0F);
      int var12 = ThemeColors.handle(ThemeColors.handle(var2.execute(), handle(0, 0, 0, 255), var2.unload() ? 0.02F : 0.05F), var2.unload() ? 182 : 186);
      var1.handle(var7, var8, var9, var10, var11, this.handle(16.0F), this.handle(2.0F), handle(0, 0, 0, var2.unload() ? 22 : 82));
      var1.handle(26.0F);
      var1.handle(var7, var8, var9, var10, var11, var2.unload() ? 0.74F : 0.86F);
      var1.handle(var7, var8, var9, var10, var11, var12);
      var1.handle(var7, var8, var9, var10, var11, var2.refresh(), Math.max(1.0F, this.handle(1.0F)));
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5) {
      var1.handle(Math.round(var3), Math.round(var4), Math.round(var5), Math.max(1.0F, this.handle(1.0F)), 0.0F, var2.refresh());
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!(var7 <= 0.0F) && !(var5 <= 0.0F)) {
         float var9 = handle(0.1F + 0.9F * var8, 0.0F, 1.0F);
         float var10 = Math.max(this.handle(30.0F), var5 * (var5 / (var5 + var7)));
         float var11 = var4 + (var5 - var10) * (var6 / Math.max(1.0F, var7));
         float var12 = Math.max(1.0F, Math.round(this.handle(2.5F)));
         float var13 = var12 * 0.5F;
         var1.handle(
            Math.round(var3),
            Math.round(var4),
            var12,
            Math.round(var5),
            var13,
            ThemeColors.handle(var2.load(), Math.round((var2.unload() ? 16.0F : 10.0F) * var9))
         );
         var1.handle(
            Math.round(var3),
            Math.round(var11),
            var12,
            Math.round(var10),
            var13,
            ThemeColors.handle(var2.save(), Math.round((var2.unload() ? 120.0F : 110.0F) * var9))
         );
      }
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2, boolean var3, boolean var4, float var5, float var6) {
      String var7 = var4 ? (var3 ? "LIVE" : "OFF") : "PREVIEW";
      int var8 = var4 ? (var3 ? var2.handle() : ThemeColors.handle(var2.load(), var2.unload() ? 120 : 110)) : var2.compute();
      boolean var9 = var4 && var3;
      float var10 = var9 ? check(1900.0F, 0.0F) : 0.0F;
      float var11 = this.handle(12.0F);
      float var12 = TextMeasureCache.process(FontRegistry.config, var7, var11);
      float var13 = Math.max(1.0F, Math.round(this.handle(6.0F)));
      float var14 = Math.round(var5 - var12);
      float var15 = Math.round(var14 - this.handle(8.0F) - var13);
      float var16 = Math.round(var6 - var13 * 0.5F);
      if (var9 && var10 > 0.0F) {
         var1.handle(
            var15, var16, var13, var13, var13 * 0.5F, this.handle(5.0F), this.handle(0.5F), ThemeColors.handle(var8, Math.round(35.0F + var10 * 120.0F))
         );
      }

      var1.handle(var15, var16, var13, var13, var13 * 0.5F, ThemeColors.handle(var8, var9 ? Math.round(170.0F + var10 * 85.0F) : 220));
      var1.handle(FontRegistry.config, var14, Math.round(onTick(var6, var11)), var11, var7, ThemeColors.handle(var8, var9 ? 235 : 210));
   }

   private String[] handle(HudEditorScreen.Mode var1) {
      return switch (var1) {
         case INVENTORY -> presetWrite;
         default -> windowConvert;
         case INFO, MEDIA, SERVER -> outputCollapse;
         case WATERMARK -> sourceBuild;
         case ARRAYLIST -> animationSchedule;
         case TARGET -> rendererScan;
         case SLOTS, HOTBAR -> colorMeasure;
      };
   }

   private String process(HudEditorScreen.Mode var1) {
      String[] var2 = this.handle(var1);

      for (String var6 : var2) {
         if ("panel".equals(var6)) {
            return var6;
         }
      }

      return var2[0];
   }

   private static boolean handle(String[] var0, String var1) {
      for (String var5 : var0) {
         if (var5.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(HudEditorScreen.NamedEntry var1, String var2) {
      HudEditorScreen.Mode var3 = var1.kind;
      if (var3 == HudEditorScreen.Mode.KEYBINDS) {
         if ("contentRadius".equals(var2) || "rowRadius".equals(var2)) {
            return false;
         }

         if ("slotRadius".equals(var2)) {
            return false;
         }
      }
      return switch (var2) {
         case "panelRadius", "padding", "gap" -> true;
         case "slotRadius" -> var3 == HudEditorScreen.Mode.INVENTORY
            || var3 == HudEditorScreen.Mode.HOTBAR
            || var3 == HudEditorScreen.Mode.SLOTS
            || var3 == HudEditorScreen.Mode.TARGET;
         case "rowRadius" -> var3 == HudEditorScreen.Mode.ARRAYLIST || var3 == HudEditorScreen.Mode.TARGET || var3 == HudEditorScreen.Mode.NOTIFICATION;
         case "contentRadius" -> var3 != HudEditorScreen.Mode.KEYBINDS && var3 != HudEditorScreen.Mode.WATERMARK;
         case "modulesRadius" -> var3 != HudEditorScreen.Mode.HOTBAR && var3 != HudEditorScreen.Mode.SLOTS;
         case "bindsRadius" -> var3 == HudEditorScreen.Mode.KEYBINDS
            || var3 == HudEditorScreen.Mode.POTIONS
            || var3 == HudEditorScreen.Mode.COOLDOWNS
            || var3 == HudEditorScreen.Mode.TARGET
            || var3 == HudEditorScreen.Mode.WATERMARK;
         case "bindWidth" -> var3 == HudEditorScreen.Mode.KEYBINDS
            || var3 == HudEditorScreen.Mode.POTIONS
            || var3 == HudEditorScreen.Mode.COOLDOWNS
            || var3 == HudEditorScreen.Mode.TARGET;
         case "accentWidth" -> var3 == HudEditorScreen.Mode.KEYBINDS
            || var3 == HudEditorScreen.Mode.POTIONS
            || var3 == HudEditorScreen.Mode.COOLDOWNS
            || var3 == HudEditorScreen.Mode.INFO;
         case "headerRadius", "headerHeight" -> var3 != HudEditorScreen.Mode.HOTBAR
            && var3 != HudEditorScreen.Mode.SLOTS
            && var3 != HudEditorScreen.Mode.WATERMARK
            && var3 != HudEditorScreen.Mode.ARRAYLIST;
         case "rowHeight" -> var3 != HudEditorScreen.Mode.HOTBAR && var3 != HudEditorScreen.Mode.SLOTS && var3 != HudEditorScreen.Mode.INVENTORY;
         case "titleSize", "iconSize" -> var3 != HudEditorScreen.Mode.ARRAYLIST && var3 != HudEditorScreen.Mode.HOTBAR && var3 != HudEditorScreen.Mode.SLOTS;
         default -> true;
      };
   }

   private void process(RoundedRectRenderer var1, ThemeColors var2) {
      float var3 = this.playerSave.instance;
      float var4 = this.playerSave.data;
      float var5 = this.playerSave.context;
      float var6 = this.playerSave.config;
      this.handle(var1, var2, var3, var4, var5, var6);
      float var7 = Math.round(this.handle(42.0F));
      var1.handle(
         FontRegistry.config, var3 + this.handle(16.0F), onTick(var4 + this.handle(21.0F), this.handle(16.0F)), this.handle(16.0F), "Elements", handle(var2)
      );
      String var8 = Integer.toString(scaleSave.length);
      var1.handle(
         FontRegistry.instance,
         var3 + var5 - this.handle(16.0F) - TextMeasureCache.process(FontRegistry.instance, var8, this.handle(13.0F)),
         onTick(var4 + this.handle(21.0F), this.handle(13.0F)),
         this.handle(13.0F),
         var8,
         process(var2)
      );
      this.handle(var1, var2, var3 + this.handle(14.0F), var4 + var7, var5 - this.handle(28.0F));
      float var9 = Math.round(var4 + var7 + this.handle(8.0F));
      float var10 = Math.round(Math.max(this.handle(40.0F), var4 + var6 - var9 - this.handle(8.0F)));
      this.requestRun.handle(Math.round(var3 + this.handle(4.0F)), var9, Math.round(var5 - this.handle(8.0F)), var10);
      var1.compute();
      var1.handle(
         this.requestRun.instance,
         this.requestRun.data,
         this.requestRun.context,
         this.requestRun.config,
         this.handle(8.0F),
         this.handle(8.0F),
         this.handle(8.0F),
         this.handle(8.0F)
      );

      try {
         float var11 = Math.round(this.handle(38.0F));
         float var12 = Math.round(this.handle(4.0F));
         float var13 = Math.round(var3 + this.handle(10.0F));
         float var14 = Math.round(var5 - this.handle(20.0F));
         float var15 = Math.round(this.handle(26.0F));
         float var16 = var9 - this.taskInterpolate;

         for (int var17 = 0; var17 < scaleSave.length; var17++) {
            HudEditorScreen.NamedEntry var18 = scaleSave[var17];
            HudEditorScreen.LayoutMetrics var19 = this.layerSample[var17];
            float var20 = Math.round(var16);
            var19.handle(var13, var20, var14, var11);
            boolean var21 = var17 == this.playerProject;
            boolean var22 = this.handle(var18);
            boolean var23 = var20 + var11 >= var9 && var20 <= var9 + var10;
            if (var23) {
               float var24 = this.process(var18.id, !var19.handle(this.effectScan, this.optionParse) && !var21 ? 0.0F : 1.0F);
               if (var21) {
                  var1.handle(var13, var20, var14, var11, this.handle(10.0F), handle(var2, var2.unload() ? 30 : 24));
                  var1.handle(var13, var20, var14, var11, this.handle(10.0F), handle(var2, var2.unload() ? 66 : 50), 1.0F);
                  float var25 = var11 - this.handle(16.0F);
                  float var26 = var20 + (var11 - var25) * 0.5F;
                  var1.handle(
                     Math.round(var13 + this.handle(4.0F)), Math.round(var26), Math.round(this.handle(3.0F)), Math.round(var25), this.handle(1.5F), var2.save()
                  );
               } else if (var24 > 0.01F) {
                  var1.handle(var13, var20, var14, var11, this.handle(10.0F), ThemeColors.handle(var2.load(), Math.round(var24 * (var2.unload() ? 13 : 10))));
               }

               boolean var42 = !var22;
               if (var42) {
                  var1.update(var2.unload() ? 0.5F : 0.4F);
               }

               int var43 = var21 ? var2.resolve() : ThemeColors.handle(process(var2), var2.load(), var24 * 0.5F);
               int var27 = var21 ? var2.load() : ThemeColors.handle(handle(var2), var2.load(), var24 * 0.4F);
               float var28 = Math.round(var13 + this.handle(11.0F));
               float var29 = Math.round(var20 + (var11 - var15) * 0.5F);
               var1.handle(var28, var29, var15, var15, this.handle(8.0F), var21 ? handle(var2, 40) : ThemeColors.handle(var2.load(), var2.unload() ? 14 : 12));
               var1.compute();
               var1.handle(var28, var29, var15, var15, this.handle(8.0F), this.handle(8.0F), this.handle(8.0F), this.handle(8.0F));

               try {
                  this.handle(var1, var18.iconFont, var18.icon, var28, var29, var15, this.handle(18.0F), var43);
               } finally {
                  var1.compute();
                  var1.apply();
               }

               float var30 = this.handle(16.0F);
               float var31 = Math.round(var28 + var15 + this.handle(12.0F));
               float var32 = var13 + var14 - this.handle(14.0F) - var31;
               String var33 = TextMeasureCache.process(FontRegistry.instance, var18.label, var30) <= var32 ? var18.label : stateApply[var17];
               var1.handle(FontRegistry.instance, var31, Math.round(onTick(var20 + var11 * 0.5F, var30)), var30, var33, var27);
               if (var42) {
                  var1.onTick();
               }
            }

            var16 += var11 + var12;
         }

         float var41 = scaleSave.length * var11 + Math.max(0, scaleSave.length - 1) * var12;
         this.sourceRefresh = Math.max(0.0F, var41 - var10);
         this.taskInterpolate = handle(this.taskInterpolate, 0.0F, this.sourceRefresh);
      } finally {
         var1.compute();
         var1.apply();
      }

      this.moduleTick.handle();
      this.moduleTick.handle(this.playerSave.handle(this.effectScan, this.optionParse) ? 1.0 : 0.0, 0.22F, Easings.handler, false);
      this.handle(var1, var2, var3 + var5 - this.handle(7.0F), var9, var10, this.taskInterpolate, this.sourceRefresh, this.moduleTick.update());
   }

   private void compute(RoundedRectRenderer var1, ThemeColors var2) {
      float var3 = this.frameProject.instance;
      float var4 = this.frameProject.data;
      float var5 = this.frameProject.context;
      float var6 = this.frameProject.config;
      this.handle(var1, var2, var3, var4, var5, var6);
      float var7 = Math.round(this.handle(42.0F));
      HudEditorScreen.NamedEntry var8 = this.select();
      var1.handle(
         FontRegistry.config, var3 + this.handle(16.0F), onTick(var4 + this.handle(21.0F), this.handle(16.0F)), this.handle(16.0F), var8.label, var2.load()
      );
      this.handle(var1, var2, this.handle(var8), var8.layoutBacked, var3 + var5 - this.handle(16.0F), var4 + this.handle(21.0F));
      this.handle(var1, var2, var3 + this.handle(14.0F), var4 + var7, var5 - this.handle(28.0F));
      float var9 = this.handle(14.0F);
      float var10 = var3 + var9;
      float var11 = var4 + var7 + this.handle(8.0F);
      float var12 = Math.max(this.handle(60.0F), var5 - var9 * 2.0F);
      float var13 = Math.max(this.handle(60.0F), var4 + var6 - var11 - var9);
      this.shaderProject.handle(Math.round(var10), Math.round(var11), Math.round(var12), Math.round(var13));
      var1.handle(
         this.shaderProject.instance,
         this.shaderProject.data,
         this.shaderProject.context,
         this.shaderProject.config,
         this.handle(10.0F),
         this.handle(10.0F),
         this.handle(1.0F),
         handle(0, 0, 0, var2.unload() ? 30 : 105)
      );
      var1.handle(
         this.shaderProject.instance,
         this.shaderProject.data,
         this.shaderProject.context,
         this.shaderProject.config,
         this.handle(10.0F),
         var2.unload() ? ThemeColors.handle(var2.execute(), 170) : handle(2, 6, 12, 126)
      );
      var1.handle(
         this.shaderProject.instance,
         this.shaderProject.data,
         this.shaderProject.context,
         this.shaderProject.config,
         this.handle(10.0F),
         var2.render(),
         Math.max(1.0F, this.handle(1.0F))
      );
      var1.compute();
      var1.handle(
         this.shaderProject.instance,
         this.shaderProject.data,
         this.shaderProject.context,
         this.shaderProject.config,
         this.handle(10.0F),
         this.handle(10.0F),
         this.handle(10.0F),
         this.handle(10.0F)
      );

      try {
         this.handle(var1, this.shaderProject.instance, this.shaderProject.data, this.shaderProject.context, this.shaderProject.config, var2);
         this.resolve();
         this.handle(var1, this.shaderProject.instance, this.shaderProject.data, this.shaderProject.context, this.shaderProject.config);
         this.update(var1, var2);
         this.apply(var1, var2);
         var1.handle(
            FontRegistry.instance,
            this.shaderProject.instance + this.handle(14.0F),
            this.shaderProject.data + this.shaderProject.config - this.handle(14.0F),
            this.handle(12.0F),
            "drag surface  ·  resize corner",
            process(var2)
         );
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      switch (this.select().kind) {
         case INVENTORY:
            this.process(var1, var2, var3, var4, var5);
            break;
         case POTIONS:
            this.handle(var1, var2, var3, var4, var5, "Potions", this.select().iconFont, this.select().icon, textureRun, indexBind, 24.0F, true);
            break;
         case COOLDOWNS:
            this.handle(var1, var2, var3, var4, var5, "Cooldowns", this.select().iconFont, this.select().icon, actionRead, configCollapse, 24.0F, true);
            break;
         case INFO:
            this.handle(var1, var2, var3, var4, var5, this.select().label, this.select().iconFont, this.select().icon, dataValidate, scaleRender, 24.0F, true);
            break;
         case WATERMARK:
            this.resolve(var1, var2, var3, var4, var5);
            break;
         case ARRAYLIST:
            this.update(var1, var2, var3, var4, var5);
            break;
         case TARGET:
            this.apply(var1, var2, var3, var4, var5);
            break;
         case SLOTS:
         case HOTBAR:
            this.compute(var1, var2, var3, var4, var5);
            break;
         case NOTIFICATION:
            this.handle(var1, var2, var3, var4, var5, "Notifications", this.select().iconFont, this.select().icon, clientRefresh, keyFilter, 22.0F, true);
            break;
         case MEDIA:
            this.handle(var1, var2, var3, var4, var5, "Now Playing", this.select().iconFont, this.select().icon, requestReceive, windowProcess, 22.0F, true);
            break;
         case SERVER:
            this.handle(var1, var2, var3, var4, var5, "Server Helper", this.select().iconFont, this.select().icon, packetSave, entryAnimate, 22.0F, true);
            break;
         default:
            this.handle(var1, var2, var3, var4, var5, "Binds", FontRegistry.current, "q", colorCompute, scaleAdapt, 22.0F, true);
      }
   }

   private void resolve() {
      this.resourceClamp.handle();
      this.handlerRun.handle();
      this.keyCheck.handle();
      this.layerProject.handle();
      this.entityFilter.handle();
      this.layerSample2.handle();
      this.sourceCancel.handle();
      this.eventSend.handle();
      this.inputInvoke.handle();
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, ThemeColors var6) {
      int var7 = handle(var6, var6.unload() ? 18 : 14);
      float var8 = Math.max(this.handle(20.0F), Math.round(this.handle(26.0F)));
      float var9 = Math.max(1.0F, Math.round(this.handle(1.5F)));

      for (float var10 = Math.round(var2 + var8 * 0.5F); var10 < var2 + var4; var10 += var8) {
         for (float var11 = Math.round(var3 + var8 * 0.5F); var11 < var3 + var5; var11 += var8) {
            var1.handle(Math.round(var10), Math.round(var11), var9, var9, var9 * 0.5F, var7);
         }
      }

      int var12 = handle(var6, var6.unload() ? 30 : 22);
      var1.handle(Math.round(var2 + var4 * 0.5F), Math.round(var3), 1.0F, Math.round(var5), 0.0F, var12);
      var1.handle(Math.round(var2), Math.round(var3 + var5 * 0.5F), Math.round(var4), 1.0F, 0.0F, var12);
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      String var6,
      FontObject var7,
      String var8,
      String[] var9,
      String[] var10,
      float var11,
      boolean var12
   ) {
      HudElementRegistry.ColorState var13 = this.check();
      var13.process();
      ThemePresets var14 = this.onTick();
      boolean var15 = this.select().kind != HudEditorScreen.Mode.ARRAYLIST;
      float var16 = this.handle(var6, var7, var8, var9, var10, var11, var12, var13);
      float var17 = var13.current + (var15 ? Math.max(0.0F, var13.mode) + var13.active : 0.0F) + var9.length * var13.selection + var13.current;
      HudEditorScreen.LayoutMetrics var18 = this.handle(var2, var3, var4, var5, var16, var17);
      float var19 = var18.instance;
      float var20 = var18.data;
      float var21 = var18.context;
      float var22 = var18.config;
      float var23 = this.configMatch;
      float var24 = this.actionRender;
      float var25 = Math.min(var23, var24);
      float var26 = var13.current * var23;
      float var27 = var13.current * var24;
      float var28 = var15 ? var13.mode * var24 : 0.0F;
      float var29 = var13.active * var23;
      float var30 = var15 ? var13.active * var24 : 0.0F;
      float var31 = var13.selection * var24;
      float var32 = var20 + var27 + var28 + var30;
      float var33 = var9.length * var31;
      float var34 = 0.0F;

      for (String var38 : var10) {
         var34 = Math.max(var34, TextMeasureCache.process(FontRegistry.instance, var38, var11));
      }

      float var49 = var12 ? Math.max(26.0F, var34 + 20.0F + var13.handler) * var23 : 0.0F;
      float var50 = var21 - var26 * 2.0F;
      float var51 = var12 ? Math.max(30.0F, var50 - var29 - var49) : var50;
      float var52 = var19 + var26 + this.handle(var13.source.instance, "modules.x") * var23;
      float var39 = var32 + this.handle(var13.source.data, "modules.y") * var24;
      float var40 = var19 + var26 + var51 + var29 + this.handle(var13.target.instance, "binds.x") * var23;
      float var41 = var32 + this.handle(var13.target.data, "binds.y") * var24;
      this.handle(var1, var14, var19, var20, var21, var22, var13.instance * var25, 0.95F);
      this.keyCheck.handle(var19, var20, var21, var22);
      if (var15) {
         this.layerProject.handle(var19 + var26, var20 + var27, var50, var28);
         this.handle(
            var1,
            var14,
            this.layerProject.instance,
            this.layerProject.data,
            this.layerProject.context,
            this.layerProject.config,
            var13.data * var25,
            false,
            0.95F
         );
      } else {
         this.layerProject.handle();
      }

      this.layerSample2.handle(var52, var39, var51, var33);
      this.handle(
         var1,
         var14,
         this.layerSample2.instance,
         this.layerSample2.data,
         this.layerSample2.context,
         this.layerSample2.config,
         var13.config * var25,
         true,
         0.95F
      );
      if (var12) {
         this.sourceCancel.handle(var40, var41, var49, var33);
         this.handle(
            var1,
            var14,
            this.sourceCancel.instance,
            this.sourceCancel.data,
            this.sourceCancel.context,
            this.sourceCancel.config,
            var13.state * var25,
            true,
            0.95F
         );
         handle(this.entityFilter, this.layerSample2, this.sourceCancel);
      } else {
         this.sourceCancel.handle();
         this.entityFilter.handle(this.layerSample2);
      }

      if (var15) {
         this.handle(var1, var14, var13, var19, var20, var21, var23, var24, var6, FontRegistry.config, var7, var8);
      } else {
         this.resourceClamp.handle();
         this.handlerRun.handle();
      }

      for (int var42 = 0; var42 < var9.length; var42++) {
         float var43 = var39 + var42 * var31;
         float var44 = var41 + var42 * var31;
         boolean var45 = this.select().kind == HudEditorScreen.Mode.POTIONS && var42 == 2;
         int var46 = var45 ? this.compute().process() : var14.prepare(0.9F);
         int var47 = var45 ? ThemeColors.handle(this.compute().process(), 235) : var14.update(0.9F);
         if (var13.animationDraw > 0.05F) {
            var1.handle(
               Math.round(var52 + 10.0F * var23),
               Math.round(var43 + (var31 - 8.0F * var24) * 0.5F),
               Math.max(1.0F, Math.round(var13.animationDraw * var23)),
               Math.max(1.0F, Math.round(8.0F * var24)),
               Math.max(0.8F, var13.animationDraw * 0.5F) * var23,
               var46
            );
         }

         var1.handle(FontRegistry.instance, var52 + 20.0F * var23, var43 + var31 * 0.5F + 4.0F * var24, var11 * var25, var9[var42], var47);
         if (var12) {
            float var48 = TextMeasureCache.process(FontRegistry.instance, var10[var42], var11 * var25);
            var1.handle(FontRegistry.instance, var40 + (var49 - var48) * 0.5F, var44 + var31 * 0.5F + 4.0F * var24, var11 * var25, var10[var42], var46);
         }
      }
   }
   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      HudElementRegistry.ColorState var6 = this.check();
      var6.process();
      ThemePresets var7 = this.onTick();
      float var8 = 22.0F;
      float var9 = 9.0F * var8 + var6.current * 2.0F;
      float var10 = 3.0F * var8 + var6.current * 2.0F;
      float var11 = TextMeasureCache.process(FontRegistry.config, "Inventory", var6.enabled);
      float var12 = Math.max(var9 + var6.current * 2.0F, var11 + 22.0F + var6.current * 2.0F + var6.renderer + 14.0F);
      float var13 = var6.current + var6.mode + var6.active + var10 + var6.current;
      HudEditorScreen.LayoutMetrics var14 = this.handle(var2, var3, var4, var5, var12, var13);
      float var15 = var14.instance;
      float var16 = var14.data;
      float var17 = var14.context;
      float var18 = var14.config;
      float var19 = this.configMatch;
      float var20 = this.actionRender;
      float var21 = Math.min(var19, var20);
      float var22 = var6.current * var19;
      float var23 = var6.current * var20;
      float var24 = var6.mode * var20;
      float var25 = var16 + var23 + var24 + var6.active * var20;
      float var26 = var17 - var22 * 2.0F;
      float var27 = var15 + var22 + this.handle(var6.source.instance, "modules.x") * var19;
      float var28 = var25 + this.handle(var6.source.data, "modules.y") * var20;
      float var29 = var10 * var20;
      this.handle(var1, var7, var15, var16, var17, var18, var6.instance * var21, 0.95F);
      this.keyCheck.handle(var15, var16, var17, var18);
      this.layerProject.handle(var15 + var22, var16 + var23, var26, var24);
      this.handle(
         var1, var7, this.layerProject.instance, this.layerProject.data, this.layerProject.context, this.layerProject.config, var6.data * var21, false, 0.95F
      );
      this.layerSample2.handle(var27, var28, var26, var29);
      this.entityFilter.handle(this.layerSample2);
      this.handle(
         var1, var7, this.layerSample2.instance, this.layerSample2.data, this.layerSample2.context, this.layerSample2.config, var6.context * var21, true, 0.95F
      );
      this.handle(var1, var7, var6, var15, var16, var17, var19, var20, "Inventory", FontRegistry.config, FontRegistry.state, "h");
      float var30 = var8 * var21;
      float var31 = var27 + (var26 - 9.0F * var30) * 0.5F;
      float var32 = var28 + (var29 - 3.0F * var30) * 0.5F;
      int var33 = var7.onTick() ? handle(0, 0, 0, 58) : var7.compute(0.72F);

      for (int var34 = 0; var34 < 3; var34++) {
         for (int var35 = 0; var35 < 9; var35++) {
            float var36 = var31 + var35 * var30;
            float var37 = var32 + var34 * var30;
            var1.handle(
               Math.round(var36 + 1.0F),
               Math.round(var37 + 1.0F),
               Math.max(1.0F, Math.round(var30 - 2.0F)),
               Math.max(1.0F, Math.round(var30 - 2.0F)),
               var6.output * var21,
               var33
            );
         }
      }

      this.eventSend.handle(Math.round(var31), Math.round(var32), Math.round(9.0F * var30), Math.round(3.0F * var30));
      if (this.client != null && this.client.player != null) {
         var1.compute();
         var1.handle(
            this.eventSend.instance,
            this.eventSend.data,
            this.eventSend.context,
            this.eventSend.config,
            var6.context * var21,
            var6.context * var21,
            var6.context * var21,
            var6.context * var21
         );
         boolean var44 = false /* VF: Semaphore variable */;

         try {
            var44 = true;
            int var46 = 9;
            float var47 = ItemStackOverlayRenderer.compute(Math.max(0.25F, (var30 - this.handle(4.0F)) / 16.0F));
            float var48 = 16.0F * var47;

            for (int var49 = 0; var49 < 3; var49++) {
               for (int var38 = 0; var38 < 9; var38++) {
                  ItemStack var39 = this.client.player.getInventory().getStack(var46);
                  if (var39 != null && !var39.isEmpty()) {
                     float var40 = var31 + var38 * var30 + (var30 - var48) * 0.5F;
                     float var41 = var32 + var49 * var30 + (var30 - var48) * 0.5F;
                     ItemStackOverlayRenderer.handle(
                        var1, var39, ItemStackOverlayRenderer.handle(var40), ItemStackOverlayRenderer.handle(var41), var47, var46, true, var46
                     );
                  }

                  var46++;
               }
            }

            var44 = false;
         } finally {
            if (var44) {
               var1.compute();
               var1.apply();
            }
         }

         var1.compute();
         var1.apply();
      }
   }

   private void compute(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      HudElementRegistry.ColorState var6 = this.check();
      var6.process();
      ThemePresets var7 = this.onTick();
      int var8 = this.select().kind == HudEditorScreen.Mode.HOTBAR ? 9 : 4;
      float var9 = this.select().kind == HudEditorScreen.Mode.HOTBAR ? 24.0F : 28.0F;
      float var10 = var8 * var9 + var6.current * 2.0F;
      float var11 = var9 + var6.current * 2.0F;
      HudEditorScreen.LayoutMetrics var12 = this.handle(var2, var3, var4, var5, var10, var11);
      float var13 = var12.instance;
      float var14 = var12.data;
      float var15 = var12.context;
      float var16 = var12.config;
      float var17 = Math.min(this.configMatch, this.actionRender);
      this.handle(var1, var7, var13, var14, var15, var16, var6.instance * var17, 0.95F);
      this.keyCheck.handle(var13, var14, var15, var16);
      this.layerSample2
         .handle(
            var13 + var6.current * this.configMatch,
            var14 + var6.current * this.actionRender,
            var15 - var6.current * 2.0F * this.configMatch,
            var16 - var6.current * 2.0F * this.actionRender
         );
      this.entityFilter.handle(this.layerSample2);
      float var18 = var9 * var17;
      float var19 = var13 + (var15 - var8 * var18) * 0.5F;
      float var20 = var14 + (var16 - var18) * 0.5F;

      for (int var21 = 0; var21 < var8; var21++) {
         float var22 = var19 + var21 * var18;
         int var23 = var21 == 0 ? var7.prepare(0.28F) : var7.compute(0.76F);
         var1.handle(
            Math.round(var22 + 1.0F),
            Math.round(var20 + 1.0F),
            Math.max(1.0F, Math.round(var18 - 2.0F)),
            Math.max(1.0F, Math.round(var18 - 2.0F)),
            var6.output * var17,
            var23
         );
      }

      this.eventSend.handle(Math.round(var19), Math.round(var20), Math.round(var8 * var18), Math.round(var18));
      if (this.client != null && this.client.player != null) {
         var1.compute();
         var1.handle(
            this.eventSend.instance,
            this.eventSend.data,
            this.eventSend.context,
            this.eventSend.config,
            var6.instance * var17,
            var6.instance * var17,
            var6.instance * var17,
            var6.instance * var17
         );

         try {
            float var30 = ItemStackOverlayRenderer.compute(Math.max(0.25F, (var18 - this.handle(5.0F)) / 16.0F));
            float var31 = 16.0F * var30;

            for (int var32 = 0; var32 < var8; var32++) {
               ItemStack var24 = this.handle(var32);
               if (var24 != null && !var24.isEmpty()) {
                  float var25 = var19 + var32 * var18 + (var18 - var31) * 0.5F;
                  float var26 = var20 + (var18 - var31) * 0.5F;
                  ItemStackOverlayRenderer.handle(
                     var1, var24, ItemStackOverlayRenderer.handle(var25), ItemStackOverlayRenderer.handle(var26), var30, var32, true, var32
                  );
               }
            }
         } finally {
            var1.compute();
            var1.apply();
         }
      }
   }

   private ItemStack handle(int var1) {
      if (this.client == null || this.client.player == null) {
         return ItemStack.EMPTY;
      }

      if (this.select().kind == HudEditorScreen.Mode.HOTBAR) {
         return this.client.player.getInventory().getStack(var1);
      }

      return switch (var1) {
         case 0 -> this.client.player.getEquippedStack(EquipmentSlot.HEAD);
         case 1 -> this.client.player.getEquippedStack(EquipmentSlot.CHEST);
         case 2 -> this.client.player.getEquippedStack(EquipmentSlot.LEGS);
         case 3 -> this.client.player.getEquippedStack(EquipmentSlot.FEET);
         default -> ItemStack.EMPTY;
      };
   }

   private void resolve(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      HudElementRegistry.ColorState var6 = this.check();
      var6.process();
      ThemePresets var7 = this.onTick();
      float var8 = var6.enabled;
      float var9 = var6.selection;
      float var10 = var6.current * 2.0F + var9;

      for (String var14 : requestAdapt) {
         var10 += var6.active + TextMeasureCache.process(FontRegistry.instance, var14, var8) + 36.0F;
      }

      float var28 = var9 + var6.current * 2.0F;
      HudEditorScreen.LayoutMetrics var29 = this.handle(var2, var3, var4, var5, var10, var28);
      float var30 = var29.instance;
      float var31 = var29.data;
      float var15 = var29.context;
      float var16 = var29.config;
      float var17 = this.configMatch;
      float var18 = this.actionRender;
      float var19 = Math.min(var17, var18);
      this.handle(var1, var7, var30, var31, var15, var16, var6.instance * var19, 0.95F);
      this.keyCheck.handle(var30, var31, var15, var16);
      float var20 = var30 + var6.current * var17;
      float var21 = var31 + var6.current * var18;
      float var22 = var9 * var18;
      float var23 = var9 * var17;
      var1.handle(
         Math.round(var20), Math.round(var21), Math.max(1.0F, Math.round(var23)), Math.max(1.0F, Math.round(var22)), var6.config * var19, var7.process(0.95F)
      );
      float var24 = TextMeasureCache.process(FontRegistry.current, "w", var6.renderer * var19);
      var1.handle(FontRegistry.current, var20 + (var23 - var24) * 0.5F, var21 + var22 * 0.5F + 5.5F * var18, var6.renderer * var19, "w", var7.prepare(0.95F));
      this.layerSample2.handle(var20, var21, var23, var22);
      var20 += var23;

      for (int var25 = 0; var25 < requestAdapt.length; var25++) {
         var20 += var6.active * var17;
         float var26 = TextMeasureCache.process(FontRegistry.instance, requestAdapt[var25], var8 * var19);
         float var27 = var26 + 36.0F * var17;
         var1.handle(
            Math.round(var20), Math.round(var21), Math.max(1.0F, Math.round(var27)), Math.max(1.0F, Math.round(var22)), var6.state * var19, var7.process(0.88F)
         );
         var1.handle(FontRegistry.current, var20 + 9.0F * var17, var21 + var22 * 0.5F + 5.0F * var18, 22.0F * var19, timerMeasure[var25], var7.prepare(0.9F));
         var1.handle(FontRegistry.instance, var20 + 25.0F * var17, var21 + var22 * 0.5F + 4.5F * var18, var8 * var19, requestAdapt[var25], var7.update(0.92F));
         var20 += var27;
      }

      this.entityFilter.handle(var30 + var6.current * var17, var21, var15 - var6.current * 2.0F * var17, var22);
      this.sourceCancel.handle(this.entityFilter);
   }

   private void update(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      this.handle(var1, var2, var3, var4, var5, "ArrayList", FontRegistry.current, "n", vectorEncode, playerCollect, 24.0F, false);
      this.layerProject.handle();
      this.resourceClamp.handle();
      this.handlerRun.handle();
   }

   private void apply(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      HudElementRegistry.ColorState var6 = this.check();
      var6.process();
      ThemePresets var7 = this.onTick();
      float var8 = 190.0F + var6.handler;
      float var9 = 72.0F + var6.current * 2.0F;
      HudEditorScreen.LayoutMetrics var10 = this.handle(var2, var3, var4, var5, var8, var9);
      float var11 = var10.instance;
      float var12 = var10.data;
      float var13 = var10.context;
      float var14 = var10.config;
      float var15 = this.configMatch;
      float var16 = this.actionRender;
      float var17 = Math.min(var15, var16);
      this.handle(var1, var7, var11, var12, var13, var14, var6.instance * var17, 0.95F);
      this.keyCheck.handle(var11, var12, var13, var14);
      float var18 = 46.0F * var17;
      float var19 = var11 + var6.current * var15;
      float var20 = var12 + (var14 - var18) * 0.5F;
      var1.handle(
         Math.round(var19),
         Math.round(var20),
         Math.max(1.0F, Math.round(var18)),
         Math.max(1.0F, Math.round(var18)),
         var6.output * var17 + 7.0F * var17,
         var7.process(0.95F)
      );
      var1.handle(FontRegistry.current, var19 + var18 * 0.32F, var20 + var18 * 0.63F, 28.0F * var17, "r", var7.prepare(0.9F));
      float var21 = var19 + var18 + var6.active * var15 + 8.0F * var15 + this.handle(var6.source.instance, "modules.x") * var15;
      float var22 = var12 + var6.current * var16 + 8.0F * var16 + this.handle(var6.source.data, "modules.y") * var16;
      var1.handle(FontRegistry.config, var21, var22 + 12.0F * var16, 24.0F * var17, "Enemy", var7.update(0.95F));
      float var23 = var22 + 28.0F * var16;
      float var24 = var13 - (var21 - var11) - var6.current * var15;
      var1.handle(
         Math.round(var21),
         Math.round(var23),
         Math.max(1.0F, Math.round(var24)),
         Math.max(1.0F, Math.round(8.0F * var16)),
         var6.cache * var17,
         var7.compute(0.88F)
      );
      var1.handle(
         Math.round(var21),
         Math.round(var23),
         Math.max(1.0F, Math.round(var24 * 0.68F)),
         Math.max(1.0F, Math.round(8.0F * var16)),
         var6.cache * var17,
         var7.prepare(0.9F)
      );
      this.layerSample2.handle(var21, var22, var13 - (var21 - var11) - var6.current * var15, 44.0F * var16);
      this.eventSend.handle(var19, var20, var18, var18);
      handle(this.entityFilter, this.layerSample2, this.eventSend);
   }

   private void handle(
      RoundedRectRenderer var1,
      ThemePresets var2,
      HudElementRegistry.ColorState var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      FontObject var10,
      FontObject var11,
      String var12
   ) {
      float var13 = Math.min(var7, var8);
      int var14 = var2.update(0.95F);
      int var15 = var2.prepare(0.95F);
      float var16 = var4 + this.handle(var3.pointEncode.instance, "title.x") * var7;
      float var17 = var5 + this.handle(var3.pointEncode.data, "title.y") * var8;
      float var18 = var3.enabled * var13;
      var1.handle(var10, var16, var17, var18, var9, var14);
      float var19 = TextMeasureCache.process(var10, var9, var18);
      this.resourceClamp.handle(var16 - 4.0F, var17 - var18 * 0.8F, var19 + 8.0F, var18);
      float var20 = var3.renderer * var13;
      float var21 = (var3.animator.context ? var4 + var6 : var4) + this.handle(var3.animator.instance, "icon.x") * var7;
      float var22 = var5 + this.handle(var3.animator.data, "icon.y") * var8;
      float var23 = TextMeasureCache.process(var11, var12, var20);
      var1.handle(var11, var21, var22, var20, var12, var15);
      this.handlerRun.handle(var21 - 6.0F, var22 - var20 * 0.85F, var23 + 12.0F, var20 + 4.0F);
   }

   private void handle(RoundedRectRenderer var1, ThemePresets var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      var2.handle(var1, Math.round(var3), Math.round(var4), Math.round(var5), Math.round(var6), var7, var8);
   }

   private void handle(RoundedRectRenderer var1, ThemePresets var2, float var3, float var4, float var5, float var6, float var7, boolean var8, float var9) {
      if (!(var5 <= 0.0F) && !(var6 <= 0.0F)) {
         var3 = Math.round(var3);
         var4 = Math.round(var4);
         var5 = Math.round(var5);
         var6 = Math.round(var6);
         if (!var8 || var2.prepare()) {
            if (var2.select()) {
               if (var8) {
                  var2.process(var1, var3, var4, var5, var6, var7, var9);
               } else if (!var2.handle(var3, var4, var5, var6, var7, false, var9, 1)) {
                  var1.handle(var3, var4, var5, var6, var7, var2.process(var9));
               }
            } else {
               var1.handle(var3, var4, var5, var6, var7, var8 ? var2.compute(var9) : var2.process(var9));
            }
         }
      }
   }

   private HudEditorScreen.LayoutMetrics handle(float var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = ThemeRenderer.handle().handle(this.refresh(), var5, var6);
      float var8 = this.handle(var3, var4, var5, var6, var7);
      this.configMatch = var8;
      this.actionRender = var8;
      float var9 = Math.round(var5 * this.configMatch);
      float var10 = Math.round(var6 * this.actionRender);
      float var11 = this.playerUpdate[this.playerProject];
      float var12 = this.packetRead[this.playerProject];
      float var13 = var1 + (var3 - var9) * 0.5F + var11;
      float var14 = var2 + (var4 - var10) * 0.5F + var12;
      float var15 = this.handle(10.0F);
      var13 = handle(var13, var1 + var15, Math.max(var1 + var15, var1 + var3 - var15 - var9));
      var14 = handle(var14, var2 + var15, Math.max(var2 + var15, var2 + var4 - var15 - var10));
      return this.optionFetch.handle(Math.round(var13), Math.round(var14), var9, var10);
   }

   private float handle(float var1, float var2, float var3, float var4, float var5) {
      float var6 = Math.max(this.handle(60.0F), var1 - this.handle(72.0F));
      float var7 = Math.max(this.handle(60.0F), var2 - this.handle(72.0F));
      float var8 = Math.min(var6 / Math.max(1.0F, var3), var7 / Math.max(1.0F, var4));
      var8 = handle(var8 * 0.76F, 0.55F, 1.7F);
      float var9 = Math.max(0.35F, (var1 - this.handle(22.0F)) / Math.max(1.0F, var3));
      float var10 = Math.max(0.35F, (var2 - this.handle(22.0F)) / Math.max(1.0F, var4));
      return handle(var8 * var5, 0.35F, Math.min(var9, var10));
   }

   private float handle(String var1, FontObject var2, String var3, String[] var4, String[] var5, float var6, boolean var7, HudElementRegistry.ColorState var8) {
      float var9 = TextMeasureCache.process(FontRegistry.config, var1, var8.enabled);
      float var10 = 0.0F;
      float var11 = 0.0F;

      for (int var12 = 0; var12 < var4.length; var12++) {
         var10 = Math.max(var10, TextMeasureCache.process(FontRegistry.instance, var4[var12], var6));
         if (var12 < var5.length) {
            var11 = Math.max(var11, TextMeasureCache.process(FontRegistry.instance, var5[var12], var6));
         }
      }

      float var14 = var10 + 24.0F;
      if (var7) {
         var14 += var8.active + var11 + 20.0F + var8.handler;
      }

      float var13 = var9 + var8.renderer + 36.0F;
      return Math.max(var14 + var8.current * 2.0F, var13 + var8.current * 2.0F);
   }
   private void resolve(RoundedRectRenderer var1, ThemeColors var2) {
      HudElementRegistry.ColorState var3 = this.check();
      this.update();
      float var4 = this.providerOffset.instance;
      float var5 = this.providerOffset.data;
      float var6 = this.providerOffset.context;
      float var7 = this.providerOffset.config;
      HudEditorScreen.NamedEntry var8 = this.select();
      String[] var9 = this.handle(var8.kind);
      if (!handle(var9, this.eventCollapse)) {
         this.eventCollapse = var9[0];
      }

      this.handle(var1, var2, var4, var5, var6, var7);
      float var10 = Math.round(this.handle(50.0F));
      var1.handle(FontRegistry.instance, var4 + this.handle(16.0F), var5 + this.handle(20.0F), this.handle(13.0F), var8.label, process(var2));
      var1.handle(FontRegistry.config, var4 + this.handle(16.0F), var5 + this.handle(39.0F), this.handle(16.0F), this.elementLabel(), var2.load());
      this.handle(var1, var2, var4 + this.handle(14.0F), var5 + var10, var6 - this.handle(28.0F));
      float var11 = var5 + var10 + this.handle(1.0F);
      float var12 = Math.max(this.handle(40.0F), var5 + var7 - var11 - this.handle(8.0F));
      this.messageParse2.handle(var4 + this.handle(4.0F), var11, var6 - this.handle(8.0F), var12);
      var1.compute();
      var1.handle(
         this.messageParse2.instance,
         this.messageParse2.data,
         this.messageParse2.context,
         this.messageParse2.config,
         this.handle(8.0F),
         this.handle(8.0F),
         this.handle(8.0F),
         this.handle(8.0F)
      );
      boolean var23 = false /* VF: Semaphore variable */;

      try {
         var23 = true;
         float var13 = var4 + this.handle(16.0F);
         float var14 = var6 - this.handle(32.0F);
         float var15 = var11 + this.handle(12.0F) - this.playerApply;
         if (!var8.layoutBacked) {
            var15 = this.process(var1, var2, var13, var15, var14);
         }

         var15 = this.handle(var1, var2, var13, var15, var14, var9);
         var15 += this.handle(15.0F);
         var15 = this.handle(var1, var2, var13, var15, var14, "ACTIONS");
         var15 = this.compute(var1, var2, var13, var15, var14);
         var15 += this.handle(15.0F);
         var15 = this.handle(var1, var2, var13, var15, var14, "PRESETS");
         var15 = this.handle(var1, var2, var13, var15, var14, var3);
         String var16 = null;

         for (HudEditorScreen.DataRecord var20 : timerRender) {
            if (this.handle(var8, var20.id)) {
               if (!var20.section.equals(var16)) {
                  var15 += this.handle(15.0F);
                  var15 = this.handle(var1, var2, var13, var15, var14, var20.section);
                  var16 = var20.section;
               }

               var15 = this.handle(var1, var2, var13, var15, var14, var20, this.handle(var3, var20.id));
            }
         }

         var15 += this.handle(6.0F);
         var15 = this.handle(var1, var2, var13, var15);
         var15 += this.handle(14.0F);
         float var36 = this.messageParse2.data + this.messageParse2.config;
         this.bufferAdapt = Math.max(0.0F, var15 + this.playerApply - var36);
         this.playerApply = handle(this.playerApply, 0.0F, this.bufferAdapt);
         var23 = false;
      } finally {
         if (var23) {
            var1.compute();
            var1.apply();
         }
      }

      var1.compute();
      var1.apply();
      this.playerCollapse.handle();
      this.playerCollapse.handle(this.providerOffset.handle(this.effectScan, this.optionParse) ? 1.0 : 0.0, 0.22F, Easings.handler, false);
      this.handle(
         var1,
         var2,
         var4 + var6 - this.handle(7.0F),
         this.messageParse2.data,
         this.messageParse2.config,
         this.playerApply,
         this.bufferAdapt,
         this.playerCollapse.update()
      );
   }

   private void update() {
      for (HudEditorScreen.DataRecord var4 : timerRender) {
         this.worldSend.get(var4.id).handle();
      }

      for (String var11 : profileInvoke) {
         this.targetWrite.get(var11).handle();
      }

      for (String var12 : sourceSchedule) {
         this.resultEncode.get(var12).handle();
      }
   }

   private float handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5, String var6) {
      var1.handle(FontRegistry.config, var3, var4 + this.handle(10.0F), this.handle(12.0F), var6, process(var2));
      float var7 = TextMeasureCache.process(FontRegistry.config, var6, this.handle(12.0F));
      float var8 = var3 + var7 + this.handle(10.0F);
      this.handle(var1, var2, var8, var4 + this.handle(6.0F), Math.max(0.0F, var3 + var5 - var8));
      return var4 + this.handle(24.0F);
   }

   private float process(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5) {
      float var6 = this.handle(46.0F);
      int var7 = var2.compute();
      var1.handle(Math.round(var3), Math.round(var4), Math.round(var5), Math.round(var6), this.handle(10.0F), ThemeColors.handle(var7, var2.unload() ? 26 : 20));
      var1.handle(Math.round(var3), Math.round(var4), Math.round(var5), Math.round(var6), this.handle(10.0F), ThemeColors.handle(var7, 72), 1.0F);
      float var8 = Math.round(this.handle(6.0F));
      var1.handle(Math.round(var3 + this.handle(14.0F)), Math.round(var4 + var6 * 0.5F - var8 * 0.5F), var8, var8, var8 * 0.5F, var7);
      float var9 = var3 + this.handle(14.0F) + var8 + this.handle(10.0F);
      var1.handle(FontRegistry.config, var9, var4 + this.handle(19.0F), this.handle(12.5F), "Style preview", ThemeColors.handle(var7, 240));
      var1.handle(FontRegistry.instance, var9, var4 + this.handle(34.0F), this.handle(12.0F), "Position & scale stay live", handle(var2));
      return var4 + var6 + this.handle(12.0F);
   }

   private float handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5, String[] var6) {
      int var7 = var6.length;
      int var8 = var7 <= 4 ? 1 : 2;
      int var9 = (int)Math.ceil((float)var7 / var8);
      float var10 = this.handle(7.0F);
      float var11 = this.handle(7.0F);
      float var12 = this.handle(33.0F);
      float var13 = this.handle(15.0F);
      float var14 = this.handle(9.0F);
      float var15 = (var5 - var10 * (var9 - 1)) / var9;
      HudEditorScreen.LayoutMetrics var16 = null;

      for (int var17 = 0; var17 < var7; var17++) {
         String var18 = var6[var17];
         int var19 = var17 % var9;
         int var20 = var17 / var9;
         float var21 = var3 + var19 * (var15 + var10);
         float var22 = var4 + var20 * (var12 + var11);
         HudEditorScreen.LayoutMetrics var23 = this.targetWrite.get(var18);
         var23.handle(Math.round(var21), Math.round(var22), Math.round(var15), Math.round(var12));
         if (var18.equals(this.eventCollapse)) {
            var16 = var23;
         }
      }

      if (var16 != null) {
         this.screenRead.handle();
         this.animationExpand.handle();
         this.playerRun.handle();
         this.matrixRender.handle();
         boolean var25 = !this.eventCollapse.equals(this.optionAdvance);
         if (!(this.playerRun.update() <= 0.0F) && var25) {
            this.screenRead.handle(var16.instance, 0.22F, Easings.selection, false);
            this.animationExpand.handle(var16.data, 0.22F, Easings.selection, false);
            this.playerRun.handle(var16.context, 0.22F, Easings.selection, false);
            this.matrixRender.handle(var16.config, 0.22F, Easings.selection, false);
            if (Math.abs(this.screenRead.update() - var16.instance) < 0.6F && Math.abs(this.animationExpand.update() - var16.data) < 0.6F) {
               this.optionAdvance = this.eventCollapse;
            }
         } else {
            this.screenRead.apply(var16.instance);
            this.animationExpand.apply(var16.data);
            this.playerRun.apply(var16.context);
            this.matrixRender.apply(var16.config);
            this.optionAdvance = this.eventCollapse;
         }

         float var27 = Math.round(this.screenRead.update());
         float var29 = Math.round(this.animationExpand.update());
         float var31 = Math.round(this.playerRun.update());
         float var33 = Math.round(this.matrixRender.update());
         var1.handle(var27, var29, var31, var33, var14, this.handle(7.0F), this.handle(0.5F), handle(var2, var2.unload() ? 34 : 46));
         var1.handle(var27, var29, var31, var33, var14, handle(var2, var2.unload() ? 42 : 36));
         var1.handle(var27, var29, var31, var33, var14, handle(var2, var2.unload() ? 98 : 76), 1.0F);
      }

      for (String var32 : var6) {
         HudEditorScreen.LayoutMetrics var34 = this.targetWrite.get(var32);
         boolean var35 = var32.equals(this.eventCollapse);
         float var36 = this.process(var32, !var34.handle(this.effectScan, this.optionParse) && !var35 ? 0.0F : 1.0F);
         if (!var35) {
            var1.handle(
               var34.instance,
               var34.data,
               var34.context,
               var34.config,
               var14,
               ThemeColors.handle(var2.load(), Math.round((var2.unload() ? 12.0F : 9.0F) + var36 * (var2.unload() ? 14.0F : 11.0F)))
            );
            var1.handle(var34.instance, var34.data, var34.context, var34.config, var14, var2.refresh(), 1.0F);
         }

         int var24 = var35 ? var2.load() : ThemeColors.handle(handle(var2), var2.load(), var36 * 0.4F);
         this.handle(var1, FontRegistry.instance, this.resolve(var32), var34.instance, var34.data, var34.context, var34.config, var13, var24);
      }

      return var4 + var8 * var12 + (var8 - 1) * var11;
   }

   private float compute(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5) {
      float var6 = this.handle(8.0F);
      float var7 = (var5 - var6 * 2.0F) / 3.0F;
      float var8 = this.handle(34.0F);
      this.handle(var1, var2, "centerX", var3, var4, var7, var8, "Center X", false, false);
      this.handle(var1, var2, "centerY", var3 + var7 + var6, var4, var7, var8, "Center Y", false, false);
      this.handle(var1, var2, "reset", var3 + (var7 + var6) * 2.0F, var4, var7, var8, "Reset", false, false);
      return var4 + var8;
   }

   private float handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5, HudElementRegistry.ColorState var6) {
      float var7 = this.handle(8.0F);
      float var8 = (var5 - var7 * 2.0F) / 3.0F;
      float var9 = this.handle(34.0F);
      int var10 = this.handle(var6);
      this.handle(var1, var2, "presetSoft", var3, var4, var8, var9, "Soft", true, var10 == 0);
      this.handle(var1, var2, "presetCompact", var3 + var8 + var7, var4, var8, var9, "Compact", true, var10 == 1);
      this.handle(var1, var2, "presetSharp", var3 + (var8 + var7) * 2.0F, var4, var8, var9, "Sharp", true, var10 == 2);
      return var4 + var9;
   }

   private int handle(HudElementRegistry.ColorState var1) {
      if (this.handle(var1, 17.0F, 8.0F)) {
         return 0;
      } else if (this.handle(var1, 10.0F, 5.0F)) {
         return 1;
      } else {
         return this.handle(var1, 4.0F, 7.0F) ? 2 : -1;
      }
   }

   private boolean handle(HudElementRegistry.ColorState var1, float var2, float var3) {
      return Math.abs(var1.instance - var2) < 1.2F && Math.abs(var1.current - var3) < 1.2F;
   }

   private void handle(
      RoundedRectRenderer var1, ThemeColors var2, String var3, float var4, float var5, float var6, float var7, String var8, boolean var9, boolean var10
   ) {
      HudEditorScreen.LayoutMetrics var11 = this.resultEncode.get(var3);
      var11.handle(Math.round(var4), Math.round(var5), Math.round(var6), Math.round(var7));
      float var12 = this.process(var3, var11.handle(this.effectScan, this.optionParse) ? 1.0F : 0.0F);
      float var13 = this.handle(11.0F);
      int var14;
      int var15;
      int var16;
      if (var10) {
         var14 = handle(var2, var2.unload() ? 44 : 38);
         var15 = handle(var2, var2.unload() ? 108 : 84);
         var16 = var2.load();
      } else {
         int var17 = var9 ? handle(var2, var2.unload() ? 22 : 18) : ThemeColors.handle(var2.load(), var2.unload() ? 16 : 12);
         var14 = ThemeColors.handle(var17, handle(var2, var2.unload() ? 42 : 36), var12);
         var15 = ThemeColors.handle(var2.refresh(), handle(var2, 92), var12);
         var16 = ThemeColors.handle(handle(var2), var2.load(), 0.2F + var12 * 0.6F);
      }

      var1.handle(var11.instance, var11.data, var11.context, var11.config, var13, var14);
      var1.handle(var11.instance, var11.data, var11.context, var11.config, var13, var15, 1.0F);
      this.handle(var1, FontRegistry.config, var8, var11.instance, var11.data, var11.context, var11.config, this.handle(15.0F), var16);
   }

   private float handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4, float var5, HudEditorScreen.DataRecord var6, float var7) {
      float var8 = handle((var7 - var6.min) / Math.max(0.001F, var6.max - var6.min), 0.0F, 1.0F);
      var1.handle(FontRegistry.instance, var3, var4 + this.handle(11.0F), this.handle(13.5F), var6.label, handle(var2));
      String var9 = this.scalePerform.get(var6.id);
      if (var9 == null) {
         var9 = "";
      }

      float var10 = this.handle(13.5F);
      float var11 = TextMeasureCache.process(FontRegistry.config, var9, var10);
      var1.handle(FontRegistry.config, var3 + var5 - var11, var4 + this.handle(11.0F), var10, var9, var2.resolve());
      float var12 = Math.round(var4 + this.handle(24.0F));
      float var13 = Math.max(2.0F, Math.round(this.handle(4.0F)));
      float var14 = Math.round(var3);
      float var15 = Math.round(var5);
      float var16 = var13 * 0.5F;
      float var17 = handle(this.handle(var8, var6.id), 0.0F, 1.0F);
      float var18 = Math.max(0.0F, var15 * var17);
      HudEditorScreen.LayoutMetrics var19 = this.worldSend.get(var6.id);
      var19.handle(var14, Math.round(var12 - this.handle(11.0F)), var15, Math.round(this.handle(26.0F)));
      boolean var20 = var6.id.equals(this.worldEvaluate);
      float var21 = this.process(var6.id + ".thumb", !var19.handle(this.effectScan, this.optionParse) && !var20 ? 0.0F : 1.0F);
      var1.handle(var14, var12, var15, var13, var16, var2.render());
      if (var18 > 1.0F) {
         var1.handle(var14, var12, var18, var13, var16, this.handle(4.0F), this.handle(0.5F), handle(var2, Math.round(36.0F + var21 * 70.0F)));
         var1.handle(var14, var12, var18, var13, var16, var2.save(), process(var2, 205));
      }

      float var22 = var14 + var18;
      float var23 = var12 + var13 * 0.5F;
      float var24 = this.handle(7.0F) + var21 * this.handle(1.0F);
      int var25 = Math.round((var20 ? 150.0F : 68.0F) + var21 * 95.0F);
      var1.handle(var22 - var24, var23 - var24, var24 * 2.0F, var24 * 2.0F, var24, this.handle(6.0F), this.handle(0.5F), handle(var2, var25));
      var1.process(var22, var23, var24, 0.0F, 1.0F, handle(var2, 240));
      var1.process(var22, var23, var24 - this.handle(1.6F), 0.0F, 1.0F, var2.load());
      return var4 + this.handle(40.0F);
   }

   private float handle(RoundedRectRenderer var1, ThemeColors var2, float var3, float var4) {
      HudElementRegistry.CacheEntry var5 = this.prepare();
      if (var5 == null) {
         var1.handle(FontRegistry.instance, var3, var4 + this.handle(13.0F), this.handle(13.0F), "Drag title, modules, binds or icon", process(var2));
         return var4 + this.handle(22.0F);
      } else if ("icon".equals(this.eventCollapse)) {
         var1.handle(FontRegistry.instance, var3, var4 + this.handle(13.0F), this.handle(13.0F), this.scaleSetup, handle(var2));
         return var4 + this.handle(22.0F);
      } else {
         var1.handle(FontRegistry.instance, var3, var4 + this.handle(13.0F), this.handle(13.0F), this.playerMatch, handle(var2));
         var1.handle(FontRegistry.instance, var3 + this.handle(102.0F), var4 + this.handle(13.0F), this.handle(13.0F), this.cacheClose, handle(var2));
         return var4 + this.handle(22.0F);
      }
   }

   private void update(RoundedRectRenderer var1, ThemeColors var2) {
      this.handle(var1, var2, this.keyCheck, "panel");
      this.handle(var1, var2, this.layerProject, "header");
      this.handle(var1, var2, this.entityFilter, "content");
      this.handle(var1, var2, this.layerSample2, "modules");
      this.handle(var1, var2, this.sourceCancel, "binds");
      this.handle(var1, var2, this.eventSend, "slots");
      this.handle(var1, var2, this.resourceClamp, "title");
      this.handle(var1, var2, this.handlerRun, "icon");
   }

   private void apply(RoundedRectRenderer var1, ThemeColors var2) {
      if (!(this.keyCheck.context <= 0.0F) && !(this.keyCheck.config <= 0.0F)) {
         if (this.rendererCancel && (this.screenSubmit || this.cacheHandle)) {
            int var3 = handle(var2, 170);
            if (this.screenSubmit) {
               var1.handle(
                  Math.round(this.shaderProject.instance + this.shaderProject.context * 0.5F),
                  Math.round(this.shaderProject.data),
                  Math.max(1.0F, this.handle(1.0F)),
                  Math.round(this.shaderProject.config),
                  0.0F,
                  var3
               );
            }

            if (this.cacheHandle) {
               var1.handle(
                  Math.round(this.shaderProject.instance),
                  Math.round(this.shaderProject.data + this.shaderProject.config * 0.5F),
                  Math.round(this.shaderProject.context),
                  Math.max(1.0F, this.handle(1.0F)),
                  0.0F,
                  var3
               );
            }
         }

         float var13 = !this.rendererCancel && !this.eventReceive ? 0.0F : 1.0F;
         float var4 = Math.min(this.handle(10.0F), Math.min(this.keyCheck.context, this.keyCheck.config) * 0.25F);
         int var5 = ThemeColors.handle(var2.save(), Math.round(110.0F + var13 * 100.0F));
         if (var13 > 0.0F) {
            var1.handle(
               this.keyCheck.instance,
               this.keyCheck.data,
               this.keyCheck.context,
               this.keyCheck.config,
               var4,
               this.handle(9.0F),
               this.handle(1.0F),
               ThemeColors.handle(var2.save(), 42)
            );
         }

         var1.handle(
            Math.round(this.keyCheck.instance),
            Math.round(this.keyCheck.data),
            Math.round(this.keyCheck.context),
            Math.round(this.keyCheck.config),
            var4,
            var5,
            Math.max(1.0F, this.handle(1.25F))
         );
         float var6 = this.handle(17.0F);
         float var7 = Math.round(this.keyCheck.instance + this.keyCheck.context - var6 * 0.72F);
         float var8 = Math.round(this.keyCheck.data + this.keyCheck.config - var6 * 0.72F);
         this.inputInvoke.handle(var7 - this.handle(3.0F), var8 - this.handle(3.0F), var6 + this.handle(6.0F), var6 + this.handle(6.0F));
         float var9 = this.process("preview.resize", !this.inputInvoke.handle(this.effectScan, this.optionParse) && !this.eventReceive ? 0.0F : 1.0F);
         float var10 = Math.max(1.0F, Math.round(var6));
         var1.handle(
            var7, var8, var10, var10, this.handle(5.0F), ThemeColors.handle(ThemeColors.handle(var2.apply(), 228), ThemeColors.handle(var2.save(), 92), var9)
         );
         var1.handle(
            var7, var8, var10, var10, this.handle(5.0F), ThemeColors.handle(var2.save(), Math.round(105.0F + var9 * 100.0F)), Math.max(1.0F, this.handle(1.0F))
         );
         float var11 = Math.max(1.0F, this.handle(1.0F));
         int var12 = ThemeColors.handle(var2.load(), Math.round(130.0F + var9 * 100.0F));
         var1.handle(
            Math.round(var7 + this.handle(5.0F)),
            Math.round(var8 + this.handle(11.0F)),
            Math.max(1.0F, Math.round(this.handle(7.0F))),
            Math.max(1.0F, Math.round(var11)),
            var11 * 0.5F,
            var12
         );
         var1.handle(
            Math.round(var7 + this.handle(8.0F)),
            Math.round(var8 + this.handle(8.0F)),
            Math.max(1.0F, Math.round(this.handle(4.0F))),
            Math.max(1.0F, Math.round(var11)),
            var11 * 0.5F,
            var12
         );
         var1.handle(
            Math.round(var7 + this.handle(11.0F)),
            Math.round(var8 + this.handle(5.0F)),
            Math.max(1.0F, Math.round(var11)),
            Math.max(1.0F, Math.round(var11)),
            var11 * 0.5F,
            var12
         );
      } else {
         this.inputInvoke.handle();
      }
   }

   private void handle(RoundedRectRenderer var1, ThemeColors var2, HudEditorScreen.LayoutMetrics var3, String var4) {
      if (var3 != null && !(var3.context <= 0.0F) && !(var3.config <= 0.0F)) {
         boolean var5 = var4.equals(this.eventCollapse);
         boolean var6 = var3.handle(this.effectScan, this.optionParse);
         float var7 = this.compute(var4, !var5 && !var6 ? 0.0F : 1.0F);
         int var8 = var5 ? ThemeColors.handle(var2.save(), 210) : ThemeColors.handle(var2.load(), Math.round(30.0F + var7 * 72.0F));
         var1.handle(
            Math.round(var3.instance),
            Math.round(var3.data),
            Math.max(1.0F, Math.round(var3.context)),
            Math.max(1.0F, Math.round(var3.config)),
            this.handle(4.0F),
            var8,
            var5 ? Math.max(1.5F, this.handle(1.5F)) : Math.max(1.0F, this.handle(1.0F))
         );
      }
   }

   private String handle(float var1, float var2) {
      if (this.resourceClamp.handle(var1, var2)) {
         return "title";
      } else if (this.handlerRun.handle(var1, var2)) {
         return "icon";
      } else if (this.eventSend.handle(var1, var2)) {
         return "slots";
      } else if (this.layerSample2.handle(var1, var2)) {
         return "modules";
      } else if (this.sourceCancel.handle(var1, var2)) {
         return "binds";
      } else if (this.layerProject.handle(var1, var2)) {
         return "header";
      } else if (this.entityFilter.handle(var1, var2)) {
         return "content";
      } else {
         return this.keyCheck.handle(var1, var2) ? "panel" : null;
      }
   }

   private void process(float var1, float var2) {
      if (!(this.shaderProject.context <= 0.0F) && !(this.shaderProject.config <= 0.0F) && !(this.keyCheck.context <= 0.0F) && !(this.keyCheck.config <= 0.0F)) {
         float var3 = this.handle(10.0F);
         float var4 = this.shaderProject.instance + var3;
         float var5 = this.shaderProject.data + var3;
         float var6 = this.shaderProject.instance + this.shaderProject.context - this.keyCheck.context - var3;
         float var7 = this.shaderProject.data + this.shaderProject.config - this.keyCheck.config - var3;
         float var8 = handle(this.keyCheck.instance + var1, var4, Math.max(var4, var6));
         float var9 = handle(this.keyCheck.data + var2, var5, Math.max(var5, var7));
         float var10 = this.handle(7.0F);
         float var11 = this.shaderProject.instance + (this.shaderProject.context - this.keyCheck.context) * 0.5F;
         float var12 = this.shaderProject.data + (this.shaderProject.config - this.keyCheck.config) * 0.5F;
         this.screenSubmit = Math.abs(var8 - var11) < var10;
         this.cacheHandle = Math.abs(var9 - var12) < var10;
         if (this.screenSubmit) {
            var8 = handle(var11, var4, Math.max(var4, var6));
         } else if (Math.abs(var8 - var4) < var10) {
            var8 = var4;
         } else if (Math.abs(var8 - var6) < var10) {
            var8 = Math.max(var4, var6);
         }

         if (this.cacheHandle) {
            var9 = handle(var12, var5, Math.max(var5, var7));
         } else if (Math.abs(var9 - var5) < var10) {
            var9 = var5;
         } else if (Math.abs(var9 - var7) < var10) {
            var9 = Math.max(var5, var7);
         }

         this.playerUpdate[this.playerProject] = this.playerUpdate[this.playerProject] + (var8 - this.keyCheck.instance);
         this.packetRead[this.playerProject] = this.packetRead[this.playerProject] + (var9 - this.keyCheck.data);
         this.resolve(var8, var9);
      }
   }

   private void compute(float var1, float var2) {
      float var3 = var1 - this.rangeRelease;
      float var4 = var2 - this.indexSave;
      float var5 = Math.max(1.0F, this.indexCheck * this.indexCheck + this.settingSchedule * this.settingSchedule);
      float var6 = 1.0F + (var3 * this.indexCheck + var4 * this.settingSchedule) / var5;
      ThemeRenderer.DataRecord var7 = ThemeRenderer.handle()
         .handle(this.refresh(), this.indexLoad * var6, this.outputFetch, this.scaleParse, this.inputAcquire, this.listenerRun);
      if (var7 != null) {
         float var8 = this.handle(this.shaderProject.context, this.shaderProject.config, this.inputAcquire, this.listenerRun, var7.scaleX());
         float var9 = var8 / this.layoutSave;
         float var10 = this.indexCheck * var9;
         float var11 = this.settingSchedule * var9;
         this.playerUpdate[this.playerProject] = this.blockRun + (var10 - this.indexCheck) * 0.5F;
         this.packetRead[this.playerProject] = this.playerEvaluate + (var11 - this.settingSchedule) * 0.5F;
         this.elementTick = true;
      }
   }

   private void resolve(float var1, float var2) {
      float var3 = this.handle(10.0F);
      float var4 = this.shaderProject.instance + var3;
      float var5 = this.shaderProject.data + var3;
      float var6 = Math.max(1.0F, this.shaderProject.context - var3 * 2.0F - this.keyCheck.context);
      float var7 = Math.max(1.0F, this.shaderProject.config - var3 * 2.0F - this.keyCheck.config);
      float var8 = this.keyCheck.context / Math.max(0.001F, this.configMatch);
      float var9 = this.keyCheck.config / Math.max(0.001F, this.actionRender);
      ThemeRenderer.handle().compute(this.refresh(), handle((var1 - var4) / var6, 0.0F, 1.0F), handle((var2 - var5) / var7, 0.0F, 1.0F), var8, var9);
      this.elementTick = true;
   }

   private String update(float var1, float var2) {
      if (!this.messageParse2.handle(var1, var2)) {
         return null;
      }

      for (Entry var4 : this.worldSend.entrySet()) {
         if (((HudEditorScreen.LayoutMetrics)var4.getValue()).handle(var1, var2)) {
            return (String)var4.getKey();
         }
      }

      return null;
   }

   private String apply(float var1, float var2) {
      if (!this.messageParse2.handle(var1, var2)) {
         return null;
      }

      for (Entry var4 : this.resultEncode.entrySet()) {
         if (!"close".equals(var4.getKey()) && ((HudEditorScreen.LayoutMetrics)var4.getValue()).handle(var1, var2)) {
            return (String)var4.getKey();
         }
      }

      return null;
   }

   private String execute(float var1, float var2) {
      if (!this.messageParse2.handle(var1, var2)) {
         return null;
      }

      for (Entry var4 : this.targetWrite.entrySet()) {
         if (((HudEditorScreen.LayoutMetrics)var4.getValue()).handle(var1, var2)) {
            return (String)var4.getKey();
         }
      }

      return null;
   }

   private void process(float var1) {
      HudElementRegistry.ColorState var2 = this.check();
      HudEditorScreen.DataRecord var3 = this.process(this.worldEvaluate);
      HudEditorScreen.LayoutMetrics var4 = this.worldSend.get(this.worldEvaluate);
      if (var3 != null && var4 != null) {
         this.handle(var2, var3.id, this.handle(var4, var1, var3.min, var3.max));
         var2.process();
         this.scalePerform.put(var3.id, compute(this.handle(var2, var3.id)));
         this.sessionEncode = true;
      }
   }

   private void handle(String var1) {
      if ("reset".equals(var1)) {
         this.apply();
      } else if ("centerX".equals(var1)) {
         this.handle(true, false);
      } else if ("centerY".equals(var1)) {
         this.handle(false, true);
      } else {
         HudElementRegistry.ColorState var2 = this.check();
         if ("presetSoft".equals(var1)) {
            this.process(var2);
         } else if ("presetCompact".equals(var1)) {
            this.compute(var2);
         } else if ("presetSharp".equals(var1)) {
            this.resolve(var2);
         }

         var2.process();
         this.encodePoint();
         this.sessionEncode = true;
      }
   }

   private void handle(boolean var1, boolean var2) {
      if (this.prepare() == null && !"content".equals(this.eventCollapse)) {
         this.process(var1, var2);
      } else {
         HudEditorScreen.LayoutMetrics var3 = this.execute();
         if (var3 != null && !(var3.context <= 0.0F) && !(this.keyCheck.context <= 0.0F)) {
            HudElementRegistry.ColorState var4 = this.check();
            float var5 = var1
               ? (this.keyCheck.instance + this.keyCheck.context * 0.5F - (var3.instance + var3.context * 0.5F)) / Math.max(0.001F, this.configMatch)
               : 0.0F;
            float var6 = var2
               ? (this.keyCheck.data + this.keyCheck.config * 0.5F - (var3.data + var3.config * 0.5F)) / Math.max(0.001F, this.actionRender)
               : 0.0F;
            if ("title".equals(this.eventCollapse)) {
               var4.pointEncode.instance += var5;
               var4.pointEncode.data += var6;
            } else if ("icon".equals(this.eventCollapse)) {
               var4.animator.instance += var5;
            } else if ("modules".equals(this.eventCollapse)) {
               var4.source.instance += var5;
               var4.source.data += var6;
            } else if ("binds".equals(this.eventCollapse)) {
               var4.target.instance += var5;
               var4.target.data += var6;
            } else if ("content".equals(this.eventCollapse)) {
               var4.source.instance += var5;
               var4.target.instance += var5;
               var4.source.data += var6;
               var4.target.data += var6;
            }

            var4.process();
            this.animate();
            this.sessionEncode = true;
         }
      }
   }

   private void process(boolean var1, boolean var2) {
      if (!(this.keyCheck.context <= 0.0F) && !(this.keyCheck.config <= 0.0F) && !(this.shaderProject.context <= 0.0F) && !(this.shaderProject.config <= 0.0F)) {
         float var3 = var1 ? this.shaderProject.instance + (this.shaderProject.context - this.keyCheck.context) * 0.5F : this.keyCheck.instance;
         float var4 = var2 ? this.shaderProject.data + (this.shaderProject.config - this.keyCheck.config) * 0.5F : this.keyCheck.data;
         this.playerUpdate[this.playerProject] = this.playerUpdate[this.playerProject] + (var3 - this.keyCheck.instance);
         this.packetRead[this.playerProject] = this.packetRead[this.playerProject] + (var4 - this.keyCheck.data);
         this.resolve(var3, var4);
      }
   }

   private void apply() {
      HudElementRegistry.process(this.refresh());
      this.eventCollapse = this.process(this.select().kind);
      this.playerApply = 0.0F;
      this.playerUpdate[this.playerProject] = 0.0F;
      this.packetRead[this.playerProject] = 0.0F;
      this.sessionEncode = false;
      this.elementTick = false;
      this.encodePoint();
      this.animate();
   }

   private HudEditorScreen.LayoutMetrics execute() {
      return switch (this.eventCollapse) {
         case "header" -> this.layerProject;
         case "modules" -> this.layerSample2;
         case "binds" -> this.sourceCancel;
         case "content" -> this.entityFilter;
         case "title" -> this.resourceClamp;
         case "icon" -> this.handlerRun;
         case "slots" -> this.eventSend;
         default -> this.keyCheck;
      };
   }

   private HudElementRegistry.CacheEntry prepare() {
      HudElementRegistry.ColorState var1 = this.check();

      return switch (this.eventCollapse) {
         case "title" -> var1.pointEncode;
         case "icon" -> var1.animator;
         case "modules" -> var1.source;
         case "binds" -> var1.target;
         default -> null;
      };
   }

   private void process(HudElementRegistry.ColorState var1) {
      var1.instance = 17.0F;
      var1.data = 13.0F;
      var1.context = 10.0F;
      var1.config = 10.0F;
      var1.state = 10.0F;
      var1.cache = 8.0F;
      var1.output = 6.0F;
      var1.current = 8.0F;
      var1.active = 6.0F;
      var1.mode = Math.max(30.0F, var1.mode);
      var1.selection = Math.max(22.0F, var1.selection);
      var1.handler = 10.0F;
      var1.animationDraw = 2.4F;
   }

   private void compute(HudElementRegistry.ColorState var1) {
      var1.instance = 10.0F;
      var1.data = 8.0F;
      var1.context = 5.0F;
      var1.config = 5.0F;
      var1.state = 5.0F;
      var1.cache = 4.0F;
      var1.output = 3.0F;
      var1.current = 5.0F;
      var1.active = 3.0F;
      var1.mode = Math.min(28.0F, Math.max(22.0F, var1.mode));
      var1.selection = 18.0F;
      var1.handler = -6.0F;
      var1.animationDraw = 1.4F;
   }

   private void resolve(HudElementRegistry.ColorState var1) {
      var1.instance = 4.0F;
      var1.data = 3.0F;
      var1.context = 2.0F;
      var1.config = 2.0F;
      var1.state = 2.0F;
      var1.cache = 1.0F;
      var1.output = 1.0F;
      var1.current = 7.0F;
      var1.active = 5.0F;
      var1.mode = 32.0F;
      var1.selection = 22.0F;
      var1.handler = 0.0F;
      var1.animationDraw = 2.0F;
   }

   private HudEditorScreen.DataRecord process(String var1) {
      for (HudEditorScreen.DataRecord var5 : timerRender) {
         if (var5.id.equals(var1)) {
            return var5;
         }
      }

      return null;
   }

   private float handle(HudElementRegistry.ColorState var1, String var2) {
      return switch (var2) {
         case "panelRadius" -> var1.instance;
         case "headerRadius" -> var1.data;
         case "contentRadius" -> var1.context;
         case "modulesRadius" -> var1.config;
         case "bindsRadius" -> var1.state;
         case "rowRadius" -> var1.cache;
         case "slotRadius" -> var1.output;
         case "padding" -> var1.current;
         case "gap" -> var1.active;
         case "headerHeight" -> var1.mode;
         case "rowHeight" -> var1.selection;
         case "titleSize" -> var1.enabled;
         case "iconSize" -> var1.renderer;
         case "bindWidth" -> var1.handler;
         case "accentWidth" -> var1.animationDraw;
         default -> 0.0F;
      };
   }

   private void handle(HudElementRegistry.ColorState var1, String var2, float var3) {
      switch (var2) {
         case "panelRadius":
            var1.instance = var3;
            break;
         case "headerRadius":
            var1.data = var3;
            break;
         case "contentRadius":
            var1.context = var3;
            break;
         case "modulesRadius":
            var1.config = var3;
            break;
         case "bindsRadius":
            var1.state = var3;
            break;
         case "rowRadius":
            var1.cache = var3;
            break;
         case "slotRadius":
            var1.output = var3;
            break;
         case "padding":
            var1.current = var3;
            break;
         case "gap":
            var1.active = var3;
            break;
         case "headerHeight":
            var1.mode = var3;
            break;
         case "rowHeight":
            var1.selection = var3;
            break;
         case "titleSize":
            var1.enabled = var3;
            break;
         case "iconSize":
            var1.renderer = var3;
            break;
         case "bindWidth":
            var1.handler = var3;
            break;
         case "accentWidth":
            var1.animationDraw = var3;
      }
   }

   private float handle(HudEditorScreen.LayoutMetrics var1, float var2, float var3, float var4) {
      float var5 = var1.context <= 0.0F ? 0.0F : handle((var2 - var1.instance) / var1.context, 0.0F, 1.0F);
      return var3 + (var4 - var3) * var5;
   }

   private HudElementRegistry.ColorState check() {
      return HudElementRegistry.handle(this.refresh());
   }

   private ThemePresets onTick() {
      return switch (this.refresh()) {
         case "HUD_Inventory" -> SlotHudRenderer.process();
         case "HUD_Potions" -> PotionHudRenderer.process();
         case "HUD_CoolDowns" -> CooldownHud.process();
         case "HUD_Info" -> InformationHudRenderer.process();
         case "HUD_WaterMark" -> WaterMarkRenderer.process();
         case "HUD_ArrayList" -> ModuleListHudRenderer.process();
         case "HUD_TargetHUD" -> TargetHudRenderer.process();
         case "hud_armor" -> ArmorHudRenderer.process();
         case "HUD_HotBar" -> HotbarHud.process();
         case "HUD_Notifications" -> NotificationHudSettings.process();
         case "HUD_MusicPlayer" -> MusicPlayerHudRenderer.process();
         case "HUD_ServerHelper" -> ServerItemBindingsHud.process();
         default -> KeybindHud.process();
      };
   }

   private HudEditorScreen.NamedEntry select() {
      return scaleSave[Math.max(0, Math.min(scaleSave.length - 1, this.playerProject))];
   }

   private boolean handle(HudEditorScreen.NamedEntry var1) {
      if (var1 == null) {
         return false;
      }

      try {
         return Hud.source.process(var1.settingName);
      } catch (Throwable var3) {
         return false;
      }
   }

   private String refresh() {
      return this.select().id;
   }

   private boolean compute(String var1) {
      return "title".equals(var1) || "icon".equals(var1) || "modules".equals(var1) || "binds".equals(var1);
   }

   private void render2() {
      if (this.sessionEncode) {
         this.sessionEncode = false;
         HudElementRegistry.update();
      }

      if (this.elementTick) {
         this.elementTick = false;
         if (WildClient.instance != null && WildClient.instance.renderer != null) {
            WildClient.instance.renderer.compute();
         }
      }
   }

   private String elementLabel() {
      return switch (this.eventCollapse) {
         case "panel" -> "Panel";
         case "header" -> "Header";
         case "modules" -> "Modules block";
         case "binds" -> "Binds block";
         case "content" -> "Content group";
         case "icon" -> "Icon";
         case "slots" -> "Slots";
         default -> "Title";
      };
   }

   private String resolve(String var1) {
      return switch (var1) {
         case "panel" -> "Panel";
         case "header" -> "Header";
         case "modules" -> "Modules";
         case "binds" -> "Binds";
         case "content" -> "Content";
         case "icon" -> "Icon";
         case "slots" -> "Slots";
         default -> "Title";
      };
   }

   private void drawAnimation() {
      HudElementRegistry.ColorState var1 = this.check();
      this.handle("title.x", var1.pointEncode.instance);
      this.handle("title.y", var1.pointEncode.data);
      this.handle("icon.x", var1.animator.instance);
      this.handle("icon.y", var1.animator.data);
      this.handle("modules.x", var1.source.instance);
      this.handle("modules.y", var1.source.data);
      this.handle("binds.x", var1.target.instance);
      this.handle("binds.y", var1.target.data);

      for (HudEditorScreen.DataRecord var5 : timerRender) {
         float var6 = handle((this.handle(var1, var5.id) - var5.min) / Math.max(0.001F, var5.max - var5.min), 0.0F, 1.0F);
         this.handle(var5.id, var6);
         this.update(var5.id + ".thumb");
      }

      for (HudEditorScreen.NamedEntry var15 : scaleSave) {
         this.update(var15.id);
      }

      for (String var16 : profileInvoke) {
         this.update(var16);
         DoubleAnimator var17 = new DoubleAnimator();
         var17.apply(0.0);
         this.matrixBlend2.put(var16, var17);
      }

      for (String var12 : this.resultEncode.keySet()) {
         this.update(var12);
      }

      this.update("preview.resize");
   }

   private void handle(String var1, float var2) {
      DoubleAnimator var3 = new DoubleAnimator();
      var3.apply(var2);
      this.messageParse.put(var1, var3);
   }

   private void update(String var1) {
      if (!this.providerRead.containsKey(var1)) {
         DoubleAnimator var2 = new DoubleAnimator();
         var2.apply(0.0);
         this.providerRead.put(var1, var2);
      }
   }

   private void encodePoint() {
      HudElementRegistry.ColorState var1 = this.check();

      for (HudEditorScreen.DataRecord var5 : timerRender) {
         this.scalePerform.put(var5.id, compute(this.handle(var1, var5.id)));
      }
   }

   private void animate() {
      HudElementRegistry.CacheEntry var1 = this.prepare();
      if (var1 == null) {
         this.playerMatch = "";
         this.cacheClose = "";
         this.scaleSetup = "";
      } else {
         this.playerMatch = String.format(Locale.ROOT, "X %.1f", var1.instance);
         this.cacheClose = String.format(Locale.ROOT, "Y %.1f", var1.data);
         this.scaleSetup = String.format(Locale.ROOT, "X %.1f    Y locked", var1.instance);
      }
   }

   private float handle(float var1, String var2) {
      DoubleAnimator var3 = this.messageParse.get(var2);
      if (var3 == null) {
         return var1;
      }

      var3.handle();
      var3.handle(var1, this.stateAttach == null ? 0.18F : 0.1F, Easings.handler, false);
      return var3.update();
   }

   private float process(String var1, float var2) {
      DoubleAnimator var3 = this.providerRead.get(var1);
      if (var3 == null) {
         return var2;
      }

      var3.handle();
      var3.handle(var2, 0.14F, Easings.handler, false);
      return var3.update();
   }

   private float compute(String var1, float var2) {
      DoubleAnimator var3 = this.matrixBlend2.get(var1);
      if (var3 == null) {
         return var2;
      }

      var3.handle();
      var3.handle(var2, 0.14F, Easings.handler, false);
      return var3.update();
   }

   private boolean handle(HudEditorScreen.LayoutMetrics var1) {
      return var1 != null && var1.handle(this.effectScan, this.optionParse);
   }

   private void prepare(float var1, float var2) {
      this.effectScan = var1;
      this.optionParse = var2;
   }

   private void load() {
      if (this.client != null && this.client.getWindow() != null && this.client.mouse != null) {
         double var1 = this.client.getWindow().getFramebufferWidth();
         double var3 = this.client.getWindow().getFramebufferHeight();
         if (!(var1 <= 0.0) && !(var3 <= 0.0)) {
            double var5 = this.client.mouse.getX();
            double var7 = this.client.mouse.getY();
            if (var5 >= 0.0 && var7 >= 0.0 && var5 <= var1 + 2.0 && var7 <= var3 + 2.0) {
               this.prepare((float)var5, (float)var7);
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

   private static void handle(HudEditorScreen.LayoutMetrics var0, HudEditorScreen.LayoutMetrics var1, HudEditorScreen.LayoutMetrics var2) {
      if (var0 != null) {
         if (var1 == null || var1.context <= 0.0F || var1.config <= 0.0F) {
            var0.handle(var2);
         } else if (var2 != null && !(var2.context <= 0.0F) && !(var2.config <= 0.0F)) {
            float var3 = Math.min(var1.instance, var2.instance);
            float var4 = Math.min(var1.data, var2.data);
            float var5 = Math.max(var1.instance + var1.context, var2.instance + var2.context);
            float var6 = Math.max(var1.data + var1.config, var2.data + var2.config);
            var0.handle(var3, var4, var5 - var3, var6 - var4);
         } else {
            var0.handle(var1);
         }
      }
   }

   private static String compute(float var0) {
      return String.format(Locale.ROOT, "%.1f", var0);
   }

   private static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static int handle(int var0, int var1, int var2, int var3) {
      return RoundedRectRenderer.ColorState.resolve(var0, var1, var2, Math.max(0, Math.min(255, var3)));
   }

   private static int handle(ThemeColors var0, int var1) {
      return ThemeColors.handle(var0.save(), Math.max(0, Math.min(255, var1)));
   }

   private static int process(ThemeColors var0, int var1) {
      return ThemeColors.handle(var0.submit(), Math.max(0, Math.min(255, var1)));
   }

   private static int handle(ThemeColors var0) {
      return ThemeColors.handle(var0.load(), var0.unload() ? 150 : 168);
   }

   private static int process(ThemeColors var0) {
      return ThemeColors.handle(var0.load(), var0.unload() ? 128 : 98);
   }

   private static float check(float var0, float var1) {
      double var2 = (float)(System.currentTimeMillis() % (long)Math.max(1.0F, var0)) / Math.max(1.0F, var0);
      return (float)(0.5 + 0.5 * Math.sin((var2 + var1) * Math.PI * 2.0));
   }

   private static float onTick(float var0, float var1) {
      return var0 + var1 * 0.3F;
   }

   private void handle(RoundedRectRenderer var1, FontObject var2, String var3, float var4, float var5, float var6, float var7, int var8) {
      float var9 = TextMeasureCache.process(var2, var3, var7);
      var1.handle(var2, Math.round(var4 + (var6 - var9) * 0.5F), Math.round(onTick(var5 + var6 * 0.5F, var7)), var7, var3, var8);
   }

   private void handle(RoundedRectRenderer var1, FontObject var2, String var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      float var10 = TextMeasureCache.process(var2, var3, var8);
      var1.handle(var2, Math.round(var4 + (var6 - var10) * 0.5F), Math.round(onTick(var5 + var7 * 0.5F, var8)), var8, var3, var9);
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, int var6) {
      var1.handle(var2, var3);
      var1.process(45.0F);
      var1.handle(-var4, -var5 * 0.5F, var4 * 2.0F, var5, var5 * 0.5F, var6);
      var1.execute();
      var1.process(-45.0F);
      var1.handle(-var4, -var5 * 0.5F, var4 * 2.0F, var5, var5 * 0.5F, var6);
      var1.execute();
      var1.prepare();
   }

   record DataRecord(String id, String label, String section, float min, float max) {
   }

   static final class LayoutMetrics {
      float instance;
      float data;
      float context;
      float config;

      LayoutMetrics() {
         this(0.0F, 0.0F, 0.0F, 0.0F);
      }

      LayoutMetrics(float var1, float var2, float var3, float var4) {
         this.handle(var1, var2, var3, var4);
      }

      HudEditorScreen.LayoutMetrics handle(float var1, float var2, float var3, float var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         return this;
      }

      HudEditorScreen.LayoutMetrics handle(HudEditorScreen.LayoutMetrics var1) {
         return var1 == null ? this.handle() : this.handle(var1.instance, var1.data, var1.context, var1.config);
      }

      HudEditorScreen.LayoutMetrics handle() {
         return this.handle(0.0F, 0.0F, 0.0F, 0.0F);
      }

      static HudEditorScreen.LayoutMetrics process() {
         return new HudEditorScreen.LayoutMetrics();
      }

      boolean handle(float var1, float var2) {
         return var1 >= this.instance && var2 >= this.data && var1 <= this.instance + this.context && var2 <= this.data + this.config;
      }
   }

   enum Mode {
      KEYBINDS,
      INVENTORY,
      POTIONS,
      COOLDOWNS,
      INFO,
      WATERMARK,
      ARRAYLIST,
      TARGET,
      SLOTS,
      HOTBAR,
      NOTIFICATION,
      MEDIA,
      SERVER;
   }

   record NamedEntry(String id, String label, String settingName, FontObject iconFont, String icon, HudEditorScreen.Mode kind, boolean layoutBacked) {
   }
}
