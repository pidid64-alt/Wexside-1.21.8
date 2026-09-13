package ru.wild.modules.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.wild.mixin.acceser.ClientPlayerInteractionManagerAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MouseMotionEvent;
import ru.wild.api.event.MouseScrollEvent;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.event.PlayerMotionEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldReadyEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.RotationPlayback;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRoles(compute = {"lichoday", "bitrixtime", "oblamovvv"})
@ModuleRegister(
   name = "ActionRecorder",
   description = "Записывает и воспроизводит действия игрока",
   category = ModuleCategory.Player,
   flags = ModuleFlag.NEW
)
public class ActionRecorder extends Module {
   private static final String source = "KEY";
   private static final String target = "MOUSE";
   private static final String pending = "SCROLL";
   private final StringSetting previous = new StringSetting("File", "default").handle(48);
   private final KeybindSetting latest = new KeybindSetting("Record Key", -1);
   private final KeybindSetting summary = new KeybindSetting("Play Key", -1);
   private final KeybindSetting matrixBlend = new KeybindSetting("Stop Key", -1);
   private final BooleanSetting vectorMatch = new BooleanSetting("Infinite Loop", false);
   private final NumberSetting itemProject = new NumberSetting("Loops", 1.0F, 1.0F, 20.0F, 1.0F, false).handle(this.vectorMatch::compute);
   private final NumberSetting responseCompute = new NumberSetting("Play Duration Sec", 0.0F, 0.0F, 600.0F, 1.0F, false);
   private final NumberSetting providerFetch = new NumberSetting("Record Limit Sec", 0.0F, 0.0F, 600.0F, 1.0F, false);
   private final BooleanSetting profileDraw = new BooleanSetting("Auto Save", true);
   private final BooleanSetting vectorPerform = new BooleanSetting("Rotation Controller", true);
   private final NumberSetting eventAttach = new NumberSetting("Min Rotation Speed", 1.0F, 0.2F, 180.0F, 0.1F, false)
      .handle(() -> !this.vectorPerform.compute());
   private final Gson serverRead = new GsonBuilder().setPrettyPrinting().create();
   private final RotationPlayback positionAdvance = new RotationPlayback();
   private ActionRecorder.PrimaryAnimationState frameCheck;
   private ActionRecorder.PrimaryAnimationState moduleCollect;
   private boolean providerClose;
   private boolean presetSave;
   private int windowConvert;
   private int presetWrite;
   private int colorMeasure;
   private int animationSchedule = -1;
   private float rendererScan;
   private float sourceBuild;
   private float outputCollapse;
   private float profileInvoke;
   private boolean sourceSchedule;
   private boolean timerRender;
   private boolean scaleSave;
   private double colorCompute;
   private double scaleAdapt;
   private boolean textureRun;
   private boolean indexBind;
   private boolean actionRead;

   public ActionRecorder() {
      this.handle(
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach
      );
   }

   @Override
   public void process() {
      if (this.providerClose) {
         this.compute(this.profileDraw.compute());
      }

      if (this.presetSave) {
         this.resolve(false);
      }

      super.process();
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (var1.apply() == 1 && this.latest.compute() != -1 && var1.resolve() == this.latest.compute()) {
         this.refresh();
         var1.process();
      } else if (var1.apply() == 1 && this.summary.compute() != -1 && var1.resolve() == this.summary.compute()) {
         this.render();
         var1.process();
      } else if (var1.apply() == 1 && this.matrixBlend.compute() != -1 && var1.resolve() == this.matrixBlend.compute()) {
         this.encodePoint();
         var1.process();
      } else {
         if (this.providerClose && var1.resolve() >= 0 && !this.resolve(var1.resolve())) {
            this.handle(
               ActionRecorder.AnimationState.handle(
                  this.windowConvert, this.frameCheck.cache.size(), var1.resolve(), var1.update(), var1.apply(), var1.execute()
               )
            );
         }
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (!var1.check() && this.providerClose) {
         if (!this.resolve(-100 - var1.resolve())) {
            this.handle(ActionRecorder.AnimationState.handle(this.windowConvert, this.frameCheck.cache.size(), var1.resolve(), var1.update(), var1.apply()));
         }
      }
   }

   @EventHandler
   public void handle(MouseScrollEvent var1) {
      if (!var1.prepare() && this.providerClose) {
         if ((!(var1.update() > 0.0) || !this.resolve(-200)) && (!(var1.update() < 0.0) || !this.resolve(-201))) {
            this.handle(ActionRecorder.AnimationState.handle(this.windowConvert, this.frameCheck.cache.size(), var1.resolve(), var1.update()));
         }
      }
   }

   @EventHandler(handle = 4)
   public void handle(MovementInputEvent var1) {
      if (this.presetSave) {
         ActionRecorder.SecondaryAnimationState var2 = this.process(this.presetWrite);
         if (var2 != null) {
            var1.handle(var2.current);
            var1.process(var2.active);
            var1.handle(var2.mode);
            var1.process(var2.selection);
            var1.compute(var2.enabled);
         }
      } else {
         if (this.providerClose) {
            this.outputCollapse = var1.compute();
            this.profileInvoke = var1.resolve();
            this.sourceSchedule = var1.update();
            this.timerRender = var1.apply();
            this.scaleSave = var1.execute();
         }
      }
   }

   @EventHandler(handle = 4)
   public void handle(MouseMotionEvent var1) {
      if (this.presetSave) {
         var1.process();
      } else {
         if (this.providerClose) {
            this.colorCompute = this.colorCompute + var1.compute();
            this.scaleAdapt = this.scaleAdapt + var1.resolve();
         }
      }
   }

   @EventHandler(handle = 4)
   public void handle(PlayerMotionEvent var1) {
      if (this.presetSave) {
         ActionRecorder.SecondaryAnimationState var2 = this.process(this.presetWrite);
         if (var2 != null) {
            this.handle(var2);
            this.handle(var2, var1);
            this.handle(this.presetWrite);
         }
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (this.providerClose) {
         this.animate();
         this.windowConvert++;
         if (this.providerFetch.compute() > 0.0F && this.windowConvert >= Math.round(this.providerFetch.compute() * 20.0F)) {
            this.compute(this.profileDraw.compute());
         }
      }

      if (this.presetSave) {
         if (this.animationSchedule != this.presetWrite) {
            ActionRecorder.SecondaryAnimationState var2 = this.process(this.presetWrite);
            if (var2 != null) {
               this.handle(var2);
            }

            this.handle(this.presetWrite);
         }

         this.submit();
      }
   }

   @EventHandler
   public void handle(WorldReadyEvent var1) {
      this.encodePoint();
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.encodePoint();
   }

   private void refresh() {
      if (this.providerClose) {
         this.compute(this.profileDraw.compute());
      } else {
         this.tick();
      }
   }

   private void render() {
      if (this.presetSave) {
         this.resolve(true);
      } else {
         if (this.providerClose) {
            this.compute(this.profileDraw.compute());
         }

         this.drawAnimation();
      }
   }

   private void tick() {
      if (!this.fetchProvider()) {
         ChatLogger.handle("[ActionRecorder] Player is not ready.");
      } else {
         if (this.presetSave) {
            this.resolve(false);
         }

         this.frameCheck = new ActionRecorder.PrimaryAnimationState();
         this.frameCheck.data = System.currentTimeMillis();
         this.frameCheck.context = this.projectItem();
         this.providerClose = true;
         this.windowConvert = 0;
         this.rendererScan = Module.client.player.getYaw();
         this.sourceBuild = Module.client.player.getPitch();
         this.outputCollapse = 0.0F;
         this.profileInvoke = 0.0F;
         this.sourceSchedule = false;
         this.timerRender = false;
         this.scaleSave = false;
         this.colorCompute = 0.0;
         this.scaleAdapt = 0.0;
         ChatLogger.handle("[ActionRecorder] Recording: " + this.frameCheck.context);
      }
   }

   private void compute(boolean var1) {
      if (this.providerClose) {
         this.providerClose = false;
         if (var1 && this.frameCheck != null) {
            this.handle(this.frameCheck);
         }

         int var2 = this.frameCheck != null && this.frameCheck.state != null ? this.frameCheck.state.size() : 0;
         ChatLogger.handle("[ActionRecorder] Recording stopped. Ticks: " + var2);
      }
   }

   private void drawAnimation() {
      if (!this.fetchProvider()) {
         ChatLogger.handle("[ActionRecorder] Player is not ready.");
      } else {
         ActionRecorder.PrimaryAnimationState var1 = this.fetch();
         if (var1 != null && var1.state != null && !var1.state.isEmpty()) {
            this.process(var1);
            this.moduleCollect = var1;
            this.presetSave = true;
            this.presetWrite = 0;
            this.colorMeasure = 0;
            this.animationSchedule = -1;
            this.textureRun = false;
            this.indexBind = false;
            this.actionRead = false;
            ChatLogger.handle("[ActionRecorder] Playback: " + this.moduleCollect.context);
         } else {
            ChatLogger.handle("[ActionRecorder] Recording is empty or missing.");
         }
      }
   }

   private void resolve(boolean var1) {
      if (this.presetSave) {
         this.presetSave = false;
         this.moduleCollect = null;
         this.presetWrite = 0;
         this.colorMeasure = 0;
         this.animationSchedule = -1;
         this.textureRun = false;
         this.indexBind = false;
         this.actionRead = false;
         this.positionAdvance.handle();
         this.computeResponse();
         if (var1) {
            ChatLogger.handle("[ActionRecorder] Playback stopped.");
         }
      }
   }

   private void encodePoint() {
      if (this.providerClose) {
         this.compute(this.profileDraw.compute());
      }

      if (this.presetSave) {
         this.resolve(false);
      }
   }

   private void animate() {
      if (this.fetchProvider() && this.frameCheck != null) {
         ActionRecorder.SecondaryAnimationState var1 = new ActionRecorder.SecondaryAnimationState();
         var1.instance = this.windowConvert;
         var1.data = Module.client.player.getYaw();
         var1.context = Module.client.player.getPitch();
         var1.config = Math.abs(MathHelper.wrapDegrees(var1.data - this.rendererScan));
         var1.state = Math.abs(var1.context - this.sourceBuild);
         var1.cache = this.colorCompute;
         var1.output = this.scaleAdapt;
         var1.current = this.outputCollapse;
         var1.active = this.profileInvoke;
         var1.mode = this.sourceSchedule;
         var1.selection = this.timerRender;
         var1.enabled = this.scaleSave;
         var1.renderer = Module.client.player.getInventory().getSelectedSlot();
         var1.handler = Module.client.player.getVelocity().x;
         var1.animationDraw = Module.client.player.getVelocity().y;
         var1.pointEncode = Module.client.player.getVelocity().z;
         var1.animator = Math.hypot(var1.handler, var1.pointEncode);
         this.frameCheck.state.add(var1);
         this.rendererScan = var1.data;
         this.sourceBuild = var1.context;
         this.colorCompute = 0.0;
         this.scaleAdapt = 0.0;
      }
   }

   private void handle(ActionRecorder.AnimationState var1) {
      if (this.frameCheck != null && this.frameCheck.cache != null) {
         this.frameCheck.cache.add(var1);
      }
   }

   private void handle(ActionRecorder.SecondaryAnimationState var1) {
      if (!this.fetchProvider()) {
         this.resolve(false);
      } else {
         this.compute(var1.renderer);
         Module.client.options.forwardKey.setPressed(var1.current > 0.0F);
         Module.client.options.backKey.setPressed(var1.current < 0.0F);
         Module.client.options.leftKey.setPressed(var1.active > 0.0F);
         Module.client.options.rightKey.setPressed(var1.active < 0.0F);
         Module.client.options.jumpKey.setPressed(var1.mode);
         Module.client.options.sneakKey.setPressed(var1.selection);
         Module.client.options.sprintKey.setPressed(var1.enabled);
         Module.client.options.attackKey.setPressed(this.textureRun);
         Module.client.options.useKey.setPressed(this.indexBind);
         Module.client.options.pickItemKey.setPressed(this.actionRead);
         Module.client.player.setSprinting(var1.enabled);
      }
   }

   private void handle(ActionRecorder.SecondaryAnimationState var1, PlayerMotionEvent var2) {
      if (Module.client.player != null) {
         if (this.vectorPerform.compute()) {
            float var3 = Math.abs(MathHelper.wrapDegrees(var1.data - Module.client.player.getYaw()));
            float var4 = Math.abs(var1.context - Module.client.player.getPitch());
            float var5 = Math.max(this.eventAttach.compute(), Math.max(var1.config, var3));
            float var6 = Math.max(this.eventAttach.compute(), Math.max(var1.state, var4));
            this.positionAdvance.handle(new RotationAngles(var1.data, var1.context), var5, var6, 1, 30);
            var2.handle(Module.client.player.getYaw());
            var2.process(Module.client.player.getPitch());
         } else {
            Module.client.player.setYaw(var1.data);
            Module.client.player.setPitch(var1.context);
            var2.handle(var1.data);
            var2.process(var1.context);
         }
      }
   }

   private void handle(int var1) {
      if (this.moduleCollect != null && this.moduleCollect.cache != null && this.animationSchedule != var1) {
         this.animationSchedule = var1;

         for (ActionRecorder.AnimationState var3 : this.moduleCollect.cache) {
            if (var3.instance == var1) {
               this.process(var3);
            }
         }
      }
   }

   private void process(ActionRecorder.AnimationState var1) {
      if ("KEY".equals(var1.context)) {
         this.compute(var1);
      } else if ("MOUSE".equals(var1.context)) {
         this.resolve(var1);
      }
   }

   private void compute(ActionRecorder.AnimationState var1) {
      boolean var2 = var1.cache != 0;
      if (this.handle(Module.client.options.attackKey, var1.config, var1.state)) {
         this.textureRun = var2;
         Module.client.options.attackKey.setPressed(var2);
         if (var2) {
            this.load();
         }
      } else if (this.handle(Module.client.options.useKey, var1.config, var1.state)) {
         this.indexBind = var2;
         Module.client.options.useKey.setPressed(var2);
         if (var2) {
            this.save();
         }
      } else if (this.handle(Module.client.options.pickItemKey, var1.config, var1.state)) {
         this.actionRead = var2;
         Module.client.options.pickItemKey.setPressed(var2);
      } else {
         if (var2 && Module.client.options.hotbarKeys != null) {
            for (int var3 = 0; var3 < Module.client.options.hotbarKeys.length; var3++) {
               if (this.handle(Module.client.options.hotbarKeys[var3], var1.config, var1.state)) {
                  this.compute(var3);
                  return;
               }
            }
         }
      }
   }

   private void resolve(ActionRecorder.AnimationState var1) {
      boolean var2 = var1.cache != 0;
      if (var1.config == 0) {
         this.textureRun = var2;
         Module.client.options.attackKey.setPressed(var2);
         if (var2) {
            this.load();
         }
      } else if (var1.config == 1) {
         this.indexBind = var2;
         Module.client.options.useKey.setPressed(var2);
         if (var2) {
            this.save();
         }
      } else {
         if (var1.config == 2) {
            this.actionRead = var2;
            Module.client.options.pickItemKey.setPressed(var2);
         }
      }
   }

   private void load() {
      if (this.fetchProvider() && Module.client.currentScreen == null) {
         if (Module.client.crosshairTarget instanceof EntityHitResult var1) {
            Entity var4 = var1.getEntity();
            if (var4 != null) {
               Module.client.interactionManager.attackEntity(Module.client.player, var4);
               Module.client.player.swingHand(Hand.MAIN_HAND);
               return;
            }
         }

         if (Module.client.crosshairTarget instanceof BlockHitResult var3 && Module.client.interactionManager.attackBlock(var3.getBlockPos(), var3.getSide())) {
            Module.client.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void save() {
      if (this.fetchProvider() && Module.client.currentScreen == null) {
         if (Module.client.crosshairTarget instanceof BlockHitResult var1) {
            ActionResult var4 = Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
            if (var4 != ActionResult.PASS && var4 != ActionResult.FAIL) {
               Module.client.player.swingHand(Hand.MAIN_HAND);
               return;
            }
         }

         ActionResult var3 = Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
         if (var3 != ActionResult.PASS && var3 != ActionResult.FAIL) {
            Module.client.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void submit() {
      int var1 = this.unload();
      if (var1 <= 0) {
         this.resolve(false);
      } else if (this.presetWrite + 1 < var1) {
         this.presetWrite++;
      } else {
         this.colorMeasure++;
         if (!this.vectorMatch.compute() && this.colorMeasure >= Math.max(1, Math.round(this.itemProject.compute()))) {
            this.resolve(true);
         } else {
            this.presetWrite = 0;
            this.animationSchedule = -1;
            this.textureRun = false;
            this.indexBind = false;
            this.actionRead = false;
         }
      }
   }

   private int unload() {
      if (this.moduleCollect != null && this.moduleCollect.state != null) {
         int var1 = this.moduleCollect.state.size();
         if (this.responseCompute.compute() > 0.0F) {
            var1 = Math.min(var1, Math.max(1, Math.round(this.responseCompute.compute() * 20.0F)));
         }

         return var1;
      } else {
         return 0;
      }
   }

   private ActionRecorder.SecondaryAnimationState process(int var1) {
      if (this.moduleCollect != null && this.moduleCollect.state != null && !this.moduleCollect.state.isEmpty()) {
         int var2 = Math.max(0, Math.min(var1, this.moduleCollect.state.size() - 1));
         ActionRecorder.SecondaryAnimationState var3 = this.moduleCollect.state.get(var2);
         if (var3.instance == var1) {
            return var3;
         }

         for (ActionRecorder.SecondaryAnimationState var5 : this.moduleCollect.state) {
            if (var5.instance == var1) {
               return var5;
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   private void handle(ActionRecorder.PrimaryAnimationState var1) {
      try {
         this.process(var1);
         Path var2 = this.measure();
         Files.createDirectories(var2.getParent());

         try (BufferedWriter var3 = Files.newBufferedWriter(var2, StandardCharsets.UTF_8)) {
            this.serverRead.toJson(var1, var3);
         }

         ChatLogger.handle("[ActionRecorder] Saved: " + var2.getFileName());
      } catch (Throwable var8) {
         ChatLogger.handle("[ActionRecorder] Save failed: " + var8.getMessage());
      }
   }

   private ActionRecorder.PrimaryAnimationState fetch() {
      try {
         Path var1 = this.measure();
         if (!Files.isRegularFile(var1)) {
            return null;
         }

         try (BufferedReader var2 = Files.newBufferedReader(var1, StandardCharsets.UTF_8)) {
            return (ActionRecorder.PrimaryAnimationState)this.serverRead.fromJson(var2, ActionRecorder.PrimaryAnimationState.class);
         }
      } catch (Throwable var7) {
         ChatLogger.handle("[ActionRecorder] Load failed: " + var7.getMessage());
         return null;
      }
   }

   private void process(ActionRecorder.PrimaryAnimationState var1) {
      if (var1.state == null) {
         var1.state = new ArrayList<>();
      }

      if (var1.cache == null) {
         var1.cache = new ArrayList<>();
      }

      var1.instance = 1;
      var1.context = this.projectItem();
      var1.config = var1.state.size();
      var1.state.sort(Comparator.comparingInt(var0 -> var0.instance));
      var1.cache.sort(Comparator.<ActionRecorder.AnimationState>comparingInt(var0 -> var0.instance).thenComparingInt(var0 -> var0.data));
   }

   private Path measure() {
      return this.blendMatrix().resolve(this.matchVector());
   }

   private Path blendMatrix() {
      return WildClient.instance != null && WildClient.instance.cache != null
         ? WildClient.instance.cache.toPath().resolve("action_records")
         : Module.client.runDirectory.toPath().resolve("Wild").resolve("action_records");
   }

   private String matchVector() {
      String var1 = this.projectItem();
      return var1.endsWith(".json") ? var1 : var1 + ".json";
   }

   private String projectItem() {
      String var1 = this.previous.compute();
      if (var1 == null || var1.isBlank()) {
         var1 = "default";
      }

      var1 = var1.trim().replace('\\', '/');
      int var2 = var1.lastIndexOf(47);
      if (var2 >= 0) {
         var1 = var1.substring(var2 + 1);
      }

      String var3 = var1.replaceAll("[^a-zA-Z0-9._-]", "_");
      if (var3.isBlank() || var3.equals(".") || var3.equals("..")) {
         var3 = "default";
      }

      if (var3.endsWith(".json")) {
         var3 = var3.substring(0, var3.length() - 5);
      }

      return var3;
   }

   private void compute(int var1) {
      if (Module.client.player != null && var1 >= 0 && var1 <= 8) {
         if (Module.client.player.getInventory().getSelectedSlot() != var1) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            if (Module.client.interactionManager instanceof ClientPlayerInteractionManagerAccessor var2) {
               var2.invokeSyncSelectedSlot();
            }
         }
      }
   }

   private boolean handle(KeyBinding var1, int var2, int var3) {
      return var1 != null && var1.matchesKey(var2, var3);
   }

   private boolean resolve(int var1) {
      return var1 != -1 && (var1 == this.latest.compute() || var1 == this.summary.compute() || var1 == this.matrixBlend.compute());
   }

   private void computeResponse() {
      if (Module.client.options != null) {
         this.handle(Module.client.options.forwardKey);
         this.handle(Module.client.options.backKey);
         this.handle(Module.client.options.leftKey);
         this.handle(Module.client.options.rightKey);
         this.handle(Module.client.options.jumpKey);
         this.handle(Module.client.options.sneakKey);
         this.handle(Module.client.options.sprintKey);
         Module.client.options.attackKey.setPressed(false);
         Module.client.options.useKey.setPressed(false);
         Module.client.options.pickItemKey.setPressed(false);
         if (Module.client.player != null) {
            Module.client.player.setSprinting(false);
         }
      }
   }

   private void handle(KeyBinding var1) {
      if (var1 != null && Module.client.getWindow() != null) {
         boolean var2 = InputUtil.isKeyPressed(Module.client.getWindow().getHandle(), var1.getDefaultKey().getCode());
         var1.setPressed(var2);
      }
   }

   private boolean fetchProvider() {
      return Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null && Module.client.options != null;
   }

   static final class AnimationState {
      int instance;
      int data;
      String context;
      int config;
      int state;
      int cache;
      int output;
      double current;
      double active;

      private AnimationState() {
      }

      static ActionRecorder.AnimationState handle(int var0, int var1, int var2, int var3, int var4, int var5) {
         ActionRecorder.AnimationState var6 = new ActionRecorder.AnimationState();
         var6.instance = var0;
         var6.data = var1;
         var6.context = "KEY";
         var6.config = var2;
         var6.state = var3;
         var6.cache = var4;
         var6.output = var5;
         return var6;
      }

      static ActionRecorder.AnimationState handle(int var0, int var1, int var2, int var3, int var4) {
         ActionRecorder.AnimationState var5 = new ActionRecorder.AnimationState();
         var5.instance = var0;
         var5.data = var1;
         var5.context = "MOUSE";
         var5.config = var2;
         var5.cache = var3;
         var5.output = var4;
         return var5;
      }

      static ActionRecorder.AnimationState handle(int var0, int var1, double var2, double var4) {
         ActionRecorder.AnimationState var6 = new ActionRecorder.AnimationState();
         var6.instance = var0;
         var6.data = var1;
         var6.context = "SCROLL";
         var6.current = var2;
         var6.active = var4;
         return var6;
      }
   }

   static final class PrimaryAnimationState {
      int instance = 1;
      long data;
      String context = "default";
      int config;
      List<ActionRecorder.SecondaryAnimationState> state = new ArrayList<>();
      List<ActionRecorder.AnimationState> cache = new ArrayList<>();
   }

   static final class SecondaryAnimationState {
      int instance;
      float data;
      float context;
      float config;
      float state;
      double cache;
      double output;
      float current;
      float active;
      boolean mode;
      boolean selection;
      boolean enabled;
      int renderer;
      double handler;
      double animationDraw;
      double pointEncode;
      double animator;
   }
}
