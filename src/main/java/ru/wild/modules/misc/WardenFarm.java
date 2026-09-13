package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.RotationController;
import ru.wild.automation.RotationPlayback;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRegister(name = "WardenFarm", category = ModuleCategory.Misc, description = "Умный авто-фарм Варден данжа")
public class WardenFarm extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "Варден", "Варден", "Медный данж");
   public final ChoiceSetting target = new ChoiceSetting(
      "Предметы для лута",
      new BooleanSetting("Дон зелья", false),
      new BooleanSetting("Сферы", true),
      new BooleanSetting("Талисманы", true),
      new BooleanSetting("Стрелы", false),
      new BooleanSetting("Незер вещи", true),
      new BooleanSetting("Оружие", false),
      new BooleanSetting("Броня", false),
      new BooleanSetting("Ценные предметы", true),
      new BooleanSetting("Яйца", false)
   );
   public final BooleanSetting pending = new BooleanSetting("Стелс режим (Варден)", true);
   public final BooleanSetting previous = new BooleanSetting("Отходить после лута", true);
   public final BooleanSetting latest = new BooleanSetting("Подбирать лут после смерти", true);
   public final BooleanSetting summary = new BooleanSetting("Освобождать хотбар", true);
   public final BooleanSetting matrixBlend = new BooleanSetting("Стоп при выбросе", true);
   public final NumberSetting vectorMatch = new NumberSetting("Ждать сундук до (сек)", 240.0F, 5.0F, 600.0F, 10.0F, false);
   public final BooleanSetting itemProject = new BooleanSetting("Авто еда и инвиз", true);
   public final BooleanSetting responseCompute = new BooleanSetting("Зелья скорости (брать и пить)", true).handle(() -> !this.itemProject.compute());
   public final BooleanSetting providerFetch = new BooleanSetting("Складывать дроп", true);
   public final ModeSetting profileDraw = new ModeSetting("Куда складывать", "Ресы", "Ресы", "В клан").handle(() -> !this.providerFetch.compute());
   public final BooleanSetting vectorPerform = new BooleanSetting("Свапать анархии", true);
   public final StringSetting eventAttach = new StringSetting("Анархии для фарма", "903,102,504").handle(() -> !this.vectorPerform.compute());
   public final StringSetting serverRead = new StringSetting("Базовая анархия", "109").handle(() -> !this.vectorPerform.compute());
   public final StringSetting positionAdvance = new StringSetting("Хом для вардена", "warden").handle(() -> !this.vectorPerform.compute());
   private final Map<String, Map<BlockPos, Long>> frameCheck = new ConcurrentHashMap<>();
   private final Map<String, Map<BlockPos, Long>> moduleCollect = new ConcurrentHashMap<>();
   private final Map<String, Integer> providerClose = new HashMap<>();
   private final Map<BlockPos, Long> presetSave = new HashMap<>();
   private final List<BlockPos> windowConvert = new ArrayList<>();
   private final Queue<Runnable> presetWrite = new ArrayDeque<>();
   private String colorMeasure = "UNKNOWN";
   private Map<BlockPos, Long> animationSchedule = new ConcurrentHashMap<>();
   private Map<BlockPos, Long> rendererScan = new ConcurrentHashMap<>();
   private IBaritone sourceBuild;
   private WardenFarm.CachedMode outputCollapse = WardenFarm.CachedMode.SEARCHING;
   private BlockPos profileInvoke = null;
   private BlockPos sourceSchedule = null;
   private BlockPos timerRender = null;
   private double scaleSave = -1.0;
   private final Stopwatch colorCompute = new Stopwatch();
   private final Stopwatch scaleAdapt = new Stopwatch();
   private final Stopwatch textureRun = new Stopwatch();
   private final Stopwatch indexBind = new Stopwatch();
   private final Stopwatch actionRead = new Stopwatch();
   private final Stopwatch configCollapse = new Stopwatch();
   private final Stopwatch dataValidate = new Stopwatch();
   private final Stopwatch scaleRender = new Stopwatch();
   private final Stopwatch clientRefresh = new Stopwatch();
   private final Stopwatch keyFilter = new Stopwatch();
   private boolean requestAdapt = false;
   private long timerMeasure = 0L;
   private String vectorEncode = "N/A";
   private int requestReceive = 0;
   private boolean windowProcess = true;
   private boolean packetSave = true;
   private boolean entryAnimate = true;
   private BlockPos playerCollect = null;
   private int stateApply = 0;
   private int matrixFilter = -1;
   private static final Pattern layerSample = Pattern.compile("(\\d{1,2}):(\\d{1,2})");
   private static final Pattern worldSend = Pattern.compile("(\\d{1,2}):(\\d{2})(?::(\\d{2}))?");
   private static final Pattern targetWrite = Pattern.compile("(\\d+)\\s*(с|s|сек|sec)");
   private static final Pattern resultEncode = Pattern.compile(
      "Смерть на координатах \\[(-?\\d+(?:[.,]\\d+)?),\\s*(-?\\d+(?:[.,]\\d+)?),\\s*(-?\\d+(?:[.,]\\d+)?)]"
   );
   private static final double messageParse = -2000.0;
   private static final double providerRead = -2000.0;
   private static final double matrixBlend2 = 2000.0;
   private static final double scalePerform = 2000.0;
   private static final double contextExpand = 62500.0;
   private static final double keyProcess = -2068.0;
   private static final double actionConvert = -1932.0;
   private static final double screenRead = -60.0;
   private static final double animationExpand = -20.0;
   private static final double playerRun = -2066.0;
   private static final double matrixRender = -1934.0;
   private static final long moduleTick = 3000L;
   private static final double playerCollapse = 3.0;
   private static final long optionAdvance = 5000L;
   private static final long effectScan = 20000L;
   private static final long optionParse = 35000L;
   private static final int pointSubmit = 1;
   private static final int listenerPerform = 3;
   private static final int configMatch = 1;
   private static final int actionRender = 16;
   private static final double playerApply = 24.0;
   private static final double bufferAdapt = 40.0;
   private static final double playerUpdate = 16.0;
   private static final long packetRead = 270000L;
   private static final long rendererCancel = 1200L;
   private static final long eventReceive = 4000L;
   private static final double screenSubmit = 2.9;
   private static final long cacheHandle = 1500L;
   private static final long rangeRelease = 100L;
   private static final long indexSave = 1000L;
   private static final long indexCheck = 750L;
   private static final double settingSchedule = 14.0;
   private static final long inputAcquire = 15000L;
   private static final long listenerRun = 45000L;
   private static final long indexLoad = 5000L;
   private static final double layoutSave = 25.0;
   private static final double blockRun = 20.0;
   private static final String[] playerEvaluate = new String[]{"ресы", "ресурс"};
   private static final String[] outputFetch = new String[]{"кит", "kit", "инвиз", "invis", "зель", "морков", "carrot", "припас", "скор", "speed"};
   private long scaleParse = 0L;
   private Runnable sessionEncode = null;
   private boolean elementTick = false;
   private final Set<BlockPos> regionAlign = new HashSet<>();
   private BlockPos resourceClamp = null;
   private int handlerRun = 0;
   private WardenFarm.SecondaryMode keyCheck = WardenFarm.SecondaryMode.NONE;
   private int[] layerProject = null;
   private BlockPos entityFilter = null;
   private long layerSample2 = 0L;
   private boolean sourceCancel = false;
   private boolean eventSend = false;
   private long providerOffset = 0L;
   private final Stopwatch messageParse2 = new Stopwatch();
   private BlockPos shaderProject = null;
   private long inputInvoke = 0L;
   private WardenFarm.PersistentMode optionFetch = WardenFarm.PersistentMode.NONE;
   private WardenFarm.FallbackMode eventCollapse = WardenFarm.FallbackMode.FIND;
   private BlockPos stateAttach = null;
   private String worldEvaluate = "N/A";
   private boolean playerProject = false;
   private boolean playerMatch = false;
   private boolean cacheClose = false;
   private boolean scaleSetup = false;
   private boolean indexSynchronize = false;
   private WardenFarm.PrimaryMode taskInterpolate = WardenFarm.PrimaryMode.NONE;
   private boolean sourceRefresh = false;
   private boolean playerSave = false;
   private boolean requestRun = false;
   private int frameProject = 0;
   private final Set<BlockPos> dataRelease = new HashSet<>();
   private int vectorRun = -1;
   private WardenFarm.RuntimeMode providerSynchronize = WardenFarm.RuntimeMode.NONE;
   private int pathProcess = 0;
   private long positionReset = 0L;
   private final RotationPlayback effectApply = new RotationPlayback();
   private double cacheHandle2;
   private double sessionAdvance;
   private long keySample = 0L;
   private List<Block> playerPerform = null;
   private Vec3d taskLoad = null;
   private long pointSend = 0L;
   private long clientSubmit = 0L;
   private WardenFarm.Mode pointSample = WardenFarm.Mode.NONE;
   private int regionRefresh = -1;
   private int pathCheck = -1;
   private final Stopwatch contextParse = new Stopwatch();
   private final Stopwatch optionStop = new Stopwatch();
   private final Stopwatch mousePrepare = new Stopwatch();
   private long sessionAdapt = 0L;
   private boolean profileMatch = false;
   private long entityAdvance = 0L;
   private long sessionCollect = 0L;
   private boolean inputAttach = false;
   private int pathProject = 0;
   private boolean inputHandle = false;
   private boolean worldDispatch = false;
   private final Stopwatch optionAdvance2 = new Stopwatch();
   private boolean clientDraw = false;
   private boolean providerMatch = false;
   private long profileWrite = 0L;
   private static final int effectMatch = 65536;
   private static final RenderPipeline cacheExecute = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "block_esp_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer pointSynchronize = RenderLayer.of(
      "chest_esp_box", 65536, false, true, cacheExecute, MultiPhaseParameters.builder().build(false)
   );

   public WardenFarm() {
      this.handle(
         this.source,
         this.target,
         this.pending,
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
         this.eventAttach,
         this.serverRead,
         this.positionAdvance
      );
   }

   @Override
   public void handle() {
      ViewRotationCoordinator.instance = true;
      super.handle();
      this.sourceBuild = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.windowProcess = (Boolean)BaritoneAPI.getSettings().allowSprint.value;
      this.packetSave = (Boolean)BaritoneAPI.getSettings().allowBreak.value;
      this.entryAnimate = (Boolean)BaritoneAPI.getSettings().allowPlace.value;
      this.savePacket();
      BaritoneAPI.getSettings().allowBreak.value = false;
      BaritoneAPI.getSettings().allowPlace.value = false;
      this.cacheHandle2 = Math.random() * Math.PI * 2.0;
      this.sessionAdvance = Math.random() * Math.PI * 2.0;
      this.indexBind.handle();
      this.tick();
      NicknameUtil.instance.handle();
      if (this.itemProject.compute() && this.vectorPerform.compute() && this.compute(NicknameUtil.instance.compute()) && this.updatePlayer()) {
         this.parseOption();
      }
   }

   @Override
   public void process() {
      super.process();
      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
         BaritoneAPI.getSettings().allowSprint.value = this.windowProcess;
      }

      BaritoneAPI.getSettings().allowBreak.value = this.packetSave;
      BaritoneAPI.getSettings().allowPlace.value = this.entryAnimate;
      this.animateEntry();
      if (Module.client.player != null) {
         Module.client.player.setSneaking(false);
      }

      this.saveLayout();
      this.evaluatePlayer();
      this.tick();
      this.effectApply.handle();
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
   }

   private void refresh() {
      if (this.dataValidate.update(1000L)) {
         NicknameUtil.instance.handle();
         String var1 = NicknameUtil.instance.compute();
         String var2 = var1 != null && !var1.equals("N/A") ? var1 : "UNKNOWN";
         if (!var2.equals(this.colorMeasure)) {
            this.colorMeasure = var2;
            this.animationSchedule = this.frameCheck.computeIfAbsent(this.colorMeasure, var0 -> new ConcurrentHashMap<>());
            this.rendererScan = this.moduleCollect.computeIfAbsent(this.colorMeasure, var0 -> new ConcurrentHashMap<>());
            this.render();
         }

         long var3 = System.currentTimeMillis();
         this.animationSchedule.entrySet().removeIf(var2x -> var2x.getValue() < var3);
         this.rendererScan.entrySet().removeIf(var2x -> var2x.getValue() < var3);
         this.dataValidate.handle();
      }
   }

   private void render() {
      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
      }

      this.applyState();
      this.windowConvert.clear();
      this.presetSave.clear();
      this.regionAlign.clear();
      this.presetWrite.clear();
      this.profileInvoke = null;
      this.sourceSchedule = null;
      this.timerRender = null;
      this.resourceClamp = null;
      this.handlerRun = 0;
      this.keyCheck = WardenFarm.SecondaryMode.NONE;
      this.shaderProject = null;
      this.scaleSave = -1.0;
      this.entityAdvance = 0L;
      this.pathProject = 0;
      this.inputHandle = false;
      this.inputAttach = false;
      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.configCollapse.handle();
      this.clientRefresh.process(-1000L);
   }

   private void tick() {
      this.windowConvert.clear();
      this.presetSave.clear();
      this.providerClose.clear();
      this.presetWrite.clear();
      this.regionAlign.clear();
      this.resourceClamp = null;
      this.handlerRun = 0;
      this.keyCheck = WardenFarm.SecondaryMode.NONE;
      this.profileInvoke = null;
      this.timerRender = null;
      this.scaleSave = -1.0;
      this.entityAdvance = 0L;
      this.inputAttach = false;
      this.layerProject = null;
      this.entityFilter = null;
      this.layerSample2 = 0L;
      this.bindIndex();
      this.shaderProject = null;
      this.inputInvoke = 0L;
      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.requestAdapt = false;
      this.scaleParse = 0L;
      this.sessionEncode = null;
      this.elementTick = false;
      this.profileMatch = false;
      this.playerMatch = false;
      this.expandAnimation();
      this.configCollapse.handle();
      RotationController.instance = RotationController.Mode.IDLE;
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.windowConvert.clear();
      this.presetSave.clear();
      this.regionAlign.clear();
      this.profileInvoke = null;
      this.layerProject = null;
      this.scaleSave = -1.0;
      this.shaderProject = null;
      this.inputInvoke = 0L;
      this.bindIndex();
      this.indexBind.handle();
      this.colorMeasure = "UNKNOWN";
      this.clientRefresh.process(-1000L);
      if (this.optionFetch == WardenFarm.PersistentMode.NONE
         && this.outputCollapse != WardenFarm.CachedMode.HUB_WAITING_FOR_CHEST
         && this.outputCollapse != WardenFarm.CachedMode.SWAPPING_TO_SAVE_ANARCHY
         && this.outputCollapse != WardenFarm.CachedMode.GOING_TO_STASH
         && this.outputCollapse != WardenFarm.CachedMode.OPENING_STASH
         && this.outputCollapse != WardenFarm.CachedMode.ROTATING_STASH
         && this.outputCollapse != WardenFarm.CachedMode.OPENING_STASH_BLOCK
         && this.outputCollapse != WardenFarm.CachedMode.WAITING_FOR_GUI_STASH
         && this.outputCollapse != WardenFarm.CachedMode.STORING_IN_CHEST) {
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      }
   }

   private boolean handle(Vec3d var1) {
      return this.source.process("Варден")
         ? (var1.x - -2000.0) * (var1.x - -2000.0) + (var1.z - -2000.0) * (var1.z - -2000.0) <= 62500.0
         : (var1.x - 2000.0) * (var1.x - 2000.0) + (var1.z - 2000.0) * (var1.z - 2000.0) <= 62500.0;
   }

   private boolean drawAnimation() {
      return Module.client.player != null && Module.client.world != null && "minecraft:overworld".equals(Module.client.world.getRegistryKey().getValue().toString())
         ? this.handle(Module.client.player.getPos())
         : false;
   }

   private boolean process(Vec3d var1) {
      return !this.source.process("Варден")
         ? this.handle(var1)
         : var1.x >= -2068.0 && var1.x <= -1932.0 && var1.y >= -60.0 && var1.y <= -20.0 && var1.z >= -2066.0 && var1.z <= -1934.0;
   }

   private int[] encodePoint() {
      if (this.source.process("Варден")) {
         int var7 = (int)(-2068.0 + Math.random() * 136.0);
         int var2 = (int)(-2066.0 + Math.random() * 132.0);
         return new int[]{var7, var2};
      } else {
         double var1 = Math.random() * Math.PI * 2.0;
         double var3 = Math.sqrt(Math.random()) * 240.0;
         int var5 = (int)(2000.0 + var3 * Math.cos(var1));
         int var6 = (int)(2000.0 + var3 * Math.sin(var1));
         return new int[]{var5, var6};
      }
   }

   private long handle(BlockPos var1) {
      return this.animationSchedule.getOrDefault(var1, 0L);
   }

   private String animate() {
      String[] var1 = this.eventAttach.compute().split(",");
      if (var1.length != 0 && !var1[0].trim().isEmpty()) {
         NicknameUtil.instance.handle();
         String var2 = NicknameUtil.instance.compute();

         for (int var3 = 0; var3 < var1.length; var3++) {
            if (var1[var3].trim().equals(var2)) {
               this.requestReceive = var3;
               break;
            }
         }

         this.requestReceive = (this.requestReceive + 1) % var1.length;
         return var1[this.requestReceive].trim();
      } else {
         return this.serverRead.compute();
      }
   }

   private boolean load() {
      boolean var1;
      if (NicknameUtil.process()) {
         var1 = true;
      } else if (PvPSafe.render()) {
         if (this.profileWrite == 0L) {
            this.profileWrite = System.currentTimeMillis();
         }

         var1 = System.currentTimeMillis() - this.profileWrite <= 90000L;
      } else {
         this.profileWrite = 0L;
         var1 = false;
      }

      if (var1) {
         this.sessionAdapt = System.currentTimeMillis();
      }

      return var1;
   }

   private boolean save() {
      return this.sessionAdapt > 0L && System.currentTimeMillis() - this.sessionAdapt < 1500L;
   }

   private void submit() {
      boolean var1 = this.sourceCancel
         || Module.client.player.isSneaking()
         || this.tickElement()
         || this.pointSample != WardenFarm.Mode.NONE
         || this.providerMatch;
      boolean var2 = this.sourceBuild != null && this.sourceBuild.getCustomGoalProcess().isActive();
      BaritoneAPI.getSettings().allowSprint.value = var2 && !var1;
      if (var1 && Module.client.player.isSprinting()) {
         Module.client.player.setSprinting(false);
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (this.indexBind.update(1000L)) {
            this.refresh();
            this.unload();
            this.submit();
            if (!Module.client.player.isDead() && !(Module.client.currentScreen instanceof DeathScreen)) {
               if (!(Module.client.currentScreen instanceof GenericContainerScreen)) {
                  this.matrixFilter = -1;
               }

               if (!this.drawAnimation()) {
                  this.bindIndex();
               }

               if (this.matrixBlend.compute() && AutoDrop.source) {
                  if (this.sourceBuild != null) {
                     this.sourceBuild.getPathingBehavior().cancelEverything();
                  }

                  this.saveLayout();
                  this.effectApply.handle();
                  RotationController.instance = RotationController.Mode.IDLE;
               } else if (!this.alignRegion()) {
                  this.collectPlayer();
                  if (this.elementTick && !this.projectItem()) {
                     this.elementTick = false;
                     this.expandContext();
                  } else if (this.optionFetch != WardenFarm.PersistentMode.NONE) {
                     if (Module.client.currentScreen instanceof GenericContainerScreen var12) {
                        this.sourceBuild.getPathingBehavior().cancelEverything();
                        if (this.handle(var12)) {
                           this.eventCollapse = WardenFarm.FallbackMode.WAIT_GUI;
                           this.resolve((GenericContainerScreenHandler)var12.getScreenHandler());
                        }
                     } else {
                        this.submitScreen();
                     }
                  } else if (this.taskInterpolate == WardenFarm.PrimaryMode.NONE || !this.collapsePlayer()) {
                     if (this.playerProject && !Module.client.player.isDead() && !(Module.client.currentScreen instanceof DeathScreen)) {
                        this.playerProject = false;
                        this.measure();
                     } else if (this.requestAdapt) {
                        if (System.currentTimeMillis() >= this.timerMeasure) {
                           NicknameUtil.instance.handle();
                           String var11 = NicknameUtil.instance.compute();
                           String var13 = !"N/A".equals(this.vectorEncode) && this.vectorEncode != null ? this.vectorEncode : this.matchConfig();
                           if ("N/A".equals(var11) || !var11.equals(var13) && !this.compute(var11)) {
                              if (!this.projectItem() && !"N/A".equals(var13)) {
                                 Module.client.player.networkHandler.sendChatCommand("an" + var13);
                                 this.timerMeasure = System.currentTimeMillis() + 8000L;
                              } else {
                                 this.timerMeasure = System.currentTimeMillis() + 2000L;
                              }
                           } else {
                              this.requestAdapt = false;
                              this.textureRun.handle();
                              this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
                              this.configCollapse.handle();
                           }
                        }
                     } else if (this.outputCollapse == WardenFarm.CachedMode.HUB_WAITING_FOR_CHEST) {
                        if (this.timerRender != null) {
                           long var10 = this.handle(this.timerRender) - System.currentTimeMillis();
                           if (var10 <= 2000L) {
                              this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
                              this.configCollapse.handle();
                              this.requestAdapt = true;
                              this.timerMeasure = System.currentTimeMillis();
                           }
                        } else {
                           this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
                           this.configCollapse.handle();
                        }
                     } else {
                        if (this.optionFetch == WardenFarm.PersistentMode.NONE && this.taskInterpolate == WardenFarm.PrimaryMode.NONE && !this.drawAnimation()) {
                           if ("UNKNOWN".equals(this.colorMeasure)) {
                              if (this.keySample == 0L) {
                                 this.keySample = System.currentTimeMillis();
                              } else if (System.currentTimeMillis() - this.keySample > 15000L) {
                                 this.keySample = 0L;
                                 this.requestAdapt = true;
                                 this.timerMeasure = System.currentTimeMillis();
                                 return;
                              }
                           } else {
                              this.keySample = 0L;
                           }
                        }

                        boolean var2 = this.optionFetch != WardenFarm.PersistentMode.NONE;
                        boolean var3 = this.outputCollapse == WardenFarm.CachedMode.SWAPPING_TO_SAVE_ANARCHY
                           || this.outputCollapse == WardenFarm.CachedMode.GOING_TO_STASH
                           || this.outputCollapse == WardenFarm.CachedMode.OPENING_STASH
                           || this.outputCollapse == WardenFarm.CachedMode.ROTATING_STASH
                           || this.outputCollapse == WardenFarm.CachedMode.OPENING_STASH_BLOCK
                           || this.outputCollapse == WardenFarm.CachedMode.WAITING_FOR_GUI_STASH
                           || this.outputCollapse == WardenFarm.CachedMode.STORING_IN_CHEST;
                        boolean var4 = Module.client.currentScreen instanceof GenericContainerScreen;
                        if (this.sourceBuild != null) {
                           if (!this.drawAnimation() && !var3 && !var2 && !var4) {
                              if (!this.playerMatch || !this.advanceOption()) {
                                 if (this.compute(NicknameUtil.instance.compute())
                                    && (this.taskInterpolate != WardenFarm.PrimaryMode.NONE || this.tickModule())) {
                                    ;
                                 }
                              }
                           } else {
                              if (this.drawAnimation()) {
                                 this.playerMatch = false;
                                 this.collapseConfig();
                                 this.runTexture();
                                 this.releaseRange();
                                 if (this.scaleRender.update(500L)) {
                                    this.readAction();
                                    this.scaleRender.handle();
                                 }
                              }

                              if (this.pointSample != WardenFarm.Mode.NONE && Module.client.currentScreen == null) {
                                 if (this.drawAnimation()) {
                                    this.sourceBuild.getPathingBehavior().cancelEverything();
                                    if (Module.client.player.isSprinting()) {
                                       Module.client.player.setSprinting(false);
                                    }

                                    return;
                                 }

                                 this.saveLayout();
                              }

                              if (Module.client.currentScreen instanceof GenericContainerScreen var5) {
                                 this.sourceBuild.getPathingBehavior().cancelEverything();
                                 String var15 = var5.getTitle().getString().toLowerCase().replaceAll("§.", "").trim();
                                 boolean var7 = var15.contains("клан") || var15.contains("clan") || var15.contains("хранилище");
                                 boolean var8 = this.source.process("Варден")
                                    ? var15.equals("сундук") || var15.equals("большой сундук") || var15.equals("chest") || var15.equals("large chest")
                                    : var15.equals("бочка") || var15.equals("barrel");
                                 boolean var9 = this.outputCollapse == WardenFarm.CachedMode.WAITING_FOR_GUI_STASH
                                    || this.outputCollapse == WardenFarm.CachedMode.STORING_IN_CHEST
                                    || !this.drawAnimation() && this.providerFetch.compute() && this.renderAction() && this.processKey();
                                 if (var7 && this.profileDraw.process("В клан")) {
                                    this.compute((GenericContainerScreenHandler)var5.getScreenHandler());
                                 } else if (var9) {
                                    this.outputCollapse = WardenFarm.CachedMode.STORING_IN_CHEST;
                                    this.compute((GenericContainerScreenHandler)var5.getScreenHandler());
                                 } else if (this.drawAnimation() && var8) {
                                    this.handle((GenericContainerScreenHandler)var5.getScreenHandler());
                                 }
                              } else {
                                 if (this.outputCollapse == WardenFarm.CachedMode.GOING_TO_CHEST
                                    || this.outputCollapse == WardenFarm.CachedMode.ROTATING
                                    || this.outputCollapse == WardenFarm.CachedMode.OPENING) {
                                    this.encodeVector();
                                 }

                                 switch (this.outputCollapse) {
                                    case SEARCHING:
                                       this.drawProfile();
                                       break;
                                    case GOING_TO_CHEST:
                                       this.attachEvent();
                                       break;
                                    case ROTATING:
                                       this.readServer();
                                       break;
                                    case OPENING:
                                       this.advancePosition();
                                       break;
                                    case WAITING_FOR_GUI:
                                       this.computeColor();
                                       break;
                                    case RETREATING:
                                       this.convertWindow();
                                       break;
                                    case GOING_TO_DEATH_LOOT:
                                       this.measureColor();
                                       break;
                                    case COLLECTING_DEATH_LOOT:
                                       this.scheduleAnimation();
                                    case HUB_WAITING_FOR_CHEST:
                                    default:
                                       break;
                                    case SWAPPING_TO_SAVE_ANARCHY:
                                       this.buildSource();
                                       break;
                                    case GOING_TO_STASH:
                                       this.collapseOutput();
                                       break;
                                    case OPENING_STASH:
                                       this.saveScale();
                                       break;
                                    case ROTATING_STASH:
                                       this.invokeProfile();
                                       break;
                                    case OPENING_STASH_BLOCK:
                                       this.scheduleSource();
                                       break;
                                    case WAITING_FOR_GUI_STASH:
                                       this.renderTimer();
                                 }

                                 if (this.optionFetch == WardenFarm.PersistentMode.NONE) {
                                    this.matchVector();
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               this.fetch();
            }
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && var1.update() == PacketEvent.Mode.RECEIVE) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var12 = var2.content().getString();
            if (this.latest.compute()) {
               Matcher var4 = resultEncode.matcher(var12);
               if (var4.find()) {
                  try {
                     double var5 = Double.parseDouble(var4.group(1).replace(',', '.'));
                     double var7 = Double.parseDouble(var4.group(2).replace(',', '.'));
                     double var9 = Double.parseDouble(var4.group(3).replace(',', '.'));
                     if (this.process(new Vec3d(var5, var7, var9))) {
                        this.entityFilter = BlockPos.ofFloored(var5, var7, var9);
                        this.layerSample2 = System.currentTimeMillis() + 270000L;
                     }
                  } catch (NumberFormatException var11) {
                  }
               }
            }

            if (this.taskInterpolate != WardenFarm.PrimaryMode.NONE || this.optionFetch == WardenFarm.PersistentMode.TELEPORT_WARDEN) {
               if (this.resolve(var12)) {
                  this.scaleSetup = true;
               }
            }
         }
      }
   }

   private void unload() {
      if (this.itemProject.compute() && this.vectorPerform.compute() && Module.client.player != null) {
         if (Module.client.currentScreen instanceof DeathScreen || Module.client.player.isDead()) {
            if (this.applyPlayer()) {
               if (!this.playerProject) {
                  NicknameUtil.instance.handle();
                  String var1 = NicknameUtil.instance.compute();
                  if (!"N/A".equals(var1)) {
                     this.worldEvaluate = var1;
                  }

                  this.renderMatrix();
                  this.expandAnimation();
                  this.playerProject = true;
                  this.playerMatch = true;
                  this.providerClose.clear();
                  this.presetWrite.clear();
                  this.profileMatch = false;
                  this.sessionEncode = null;
                  this.elementTick = false;
                  this.profileInvoke = null;
                  this.playerCollect = null;
                  this.layerProject = null;
                  this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
                  if (this.sourceBuild != null) {
                     this.sourceBuild.getPathingBehavior().cancelEverything();
                  }

                  this.configCollapse.handle();
                  this.textureRun.handle();
               }
            }
         }
      }
   }

   private void fetch() {
      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
      }

      this.saveLayout();
      if (this.mousePrepare.update(1000L)) {
         Module.client.player.requestRespawn();
         if (Module.client.currentScreen instanceof DeathScreen) {
            Module.client.setScreen(null);
         }

         this.mousePrepare.handle();
      }
   }

   private void measure() {
      this.renderMatrix();
      this.profileInvoke = null;
      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.configCollapse.handle();
      this.textureRun.handle();
      this.colorCompute.handle();
      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
      }

      NicknameUtil.instance.handle();
      if (this.updatePlayer()) {
         this.submitPoint();
      } else {
         if (!this.drawAnimation()) {
            this.playerMatch = true;
            this.colorCompute.handle();
         }
      }
   }

   private boolean blendMatrix() {
      if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
         String var4 = var1.getTitle().getString().toLowerCase().replaceAll("§.", "").trim();
         boolean var3 = this.source.process("Варден")
            ? var4.equals("сундук") || var4.equals("большой сундук") || var4.equals("chest") || var4.equals("large chest")
            : var4.equals("бочка") || var4.equals("barrel");
         if (this.drawAnimation() && var3) {
            return true;
         }
      }

      return this.outputCollapse == WardenFarm.CachedMode.ROTATING
         || this.outputCollapse == WardenFarm.CachedMode.OPENING
         || this.outputCollapse == WardenFarm.CachedMode.WAITING_FOR_GUI;
   }

   private void matchVector() {
      if (this.sessionEncode != null && !this.projectItem() && !this.blendMatrix()) {
         Runnable var1 = this.sessionEncode;
         this.sessionEncode = null;
         var1.run();
      }
   }

   private boolean projectItem() {
      if (this.scaleParse > 0L && System.currentTimeMillis() - this.scaleParse < 3000L) {
         return true;
      } else {
         return this.load() ? true : this.save();
      }
   }

   private void computeResponse() {
      this.scaleParse = System.currentTimeMillis();
   }

   private void handle(Runnable var1) {
      this.compute(var1);
   }

   private void process(Runnable var1) {
      this.compute(var1);
   }

   private void compute(Runnable var1) {
      if (!this.projectItem()) {
         var1.run();
      } else {
         this.sessionEncode = var1;
         this.fetchProvider();
      }
   }

   private void fetchProvider() {
      if (this.drawAnimation()) {
         if (this.outputCollapse != WardenFarm.CachedMode.SWAPPING_TO_SAVE_ANARCHY
            && this.outputCollapse != WardenFarm.CachedMode.GOING_TO_STASH
            && this.outputCollapse != WardenFarm.CachedMode.OPENING_STASH
            && this.outputCollapse != WardenFarm.CachedMode.ROTATING_STASH
            && this.outputCollapse != WardenFarm.CachedMode.OPENING_STASH_BLOCK
            && this.outputCollapse != WardenFarm.CachedMode.WAITING_FOR_GUI_STASH
            && this.outputCollapse != WardenFarm.CachedMode.STORING_IN_CHEST
            && this.outputCollapse != WardenFarm.CachedMode.HUB_WAITING_FOR_CHEST) {
            this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
            this.configCollapse.handle();
         }
      }
   }

   private void drawProfile() {
      this.applyState();
      if (!this.drawAnimation()) {
         if (this.compute(NicknameUtil.instance.compute())) {
            if (this.taskInterpolate != WardenFarm.PrimaryMode.NONE) {
               return;
            }

            this.tickModule();
         }
      } else if (this.itemProject.compute() && this.vectorPerform.compute() && this.compute(NicknameUtil.instance.compute()) && this.updatePlayer()) {
         this.parseOption();
      } else if (this.writePreset()) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
         this.outputCollapse = WardenFarm.CachedMode.GOING_TO_DEATH_LOOT;
         this.colorCompute.handle();
         this.textureRun.handle();
         this.actionRead.handle();
      } else {
         boolean var1 = this.sessionEncode != null || this.elementTick;
         if (var1) {
            if (this.sourceBuild.getCustomGoalProcess().isActive()) {
               this.sourceBuild.getPathingBehavior().cancelEverything();
            }

            this.savePreset();
         } else if (this.renderScale()) {
            this.performScale();
         } else {
            this.validateData();
            this.profileInvoke = this.processWindow();
            if (this.profileInvoke != null) {
               this.sourceSchedule = this.load(this.profileInvoke);
               this.pathProject = 0;
               this.inputHandle = false;
               this.outputCollapse = WardenFarm.CachedMode.GOING_TO_CHEST;
               this.adaptRequest();
               this.scaleSave = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.profileInvoke));
               this.entityAdvance = 0L;
               this.inputAttach = false;
               this.evaluatePlayer();
               this.clientDraw = false;
               this.textureRun.handle();
               this.configCollapse.handle();
            } else {
               boolean var2 = false;
               if (this.configCollapse.update(20000L)) {
                  var2 = this.adaptScale();
               }

               if (!var2) {
                  BlockPos var3 = this.performVector();
                  if (var3 != null) {
                     double var6 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var3));
                     if (var6 > 6.0) {
                        if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(3000L)) {
                           this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalNear(var3, 3));
                           this.actionRead.handle();
                        }
                     } else if (this.sourceBuild.getCustomGoalProcess().isActive()) {
                        this.sourceBuild.getPathingBehavior().cancelEverything();
                     }
                  } else {
                     if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(6000L)) {
                        int[] var4 = this.encodePoint();
                        this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalXZ(var4[0], var4[1]));
                        this.actionRead.handle();
                     }
                  }
               }
            }
         }
      }
   }

   private BlockPos performVector() {
      long var1 = System.currentTimeMillis();
      return this.windowConvert
         .stream()
         .filter(var1x -> !this.regionAlign.contains(var1x))
         .filter(this::check)
         .filter(var1x -> this.tick(var1x) <= this.receiveRequest())
         .filter(var3 -> {
            Long var4 = this.rendererScan.get(var3);
            return var4 == null || var4 <= var1;
         })
         .min(
            Comparator.<BlockPos>comparingLong(var1x -> Math.max(0L, this.tick(var1x)))
               .thenComparingDouble(var0 -> Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var0)))
         )
         .orElse(null);
   }

   private void attachEvent() {
      if (this.profileInvoke == null
         || this.compute(this.profileInvoke)
         || Module.client.world.getBlockState(this.profileInvoke).getBlock() != (this.source.process("Варден") ? Blocks.CHEST : Blocks.BARREL)) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else if (!this.check(this.profileInvoke)) {
         this.rendererScan.put(this.profileInvoke, System.currentTimeMillis() + 5000L);
         this.sourceBuild.getPathingBehavior().cancelEverything();
         this.profileInvoke = null;
         this.shaderProject = null;
         this.scaleSave = -1.0;
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else {
         double var1 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.profileInvoke));
         if (var1 < this.scaleSave - 1.0) {
            this.scaleSave = var1;
            this.textureRun.handle();
         }

         boolean var3 = var1 <= 2.9;
         boolean var4 = var3 && this.unload(this.profileInvoke);
         if (var4) {
            this.shaderProject = null;
            long var9 = this.handle(this.profileInvoke) - System.currentTimeMillis();
            BlockPos var7 = this.drawAnimation(this.profileInvoke);
            if (var7 != null) {
               this.encodePoint(var7);
            } else if (this.inputAttach || var9 <= 45000L) {
               this.sourceBuild.getPathingBehavior().cancelEverything();
               boolean var10 = var9 <= 0L && !this.process(this.profileInvoke);
               if (var10) {
                  this.entityAdvance = System.currentTimeMillis();
                  this.outputCollapse = WardenFarm.CachedMode.ROTATING;
                  this.colorCompute.handle();
               } else {
                  this.entityAdvance = 0L;
               }
            } else if (this.projectItem()) {
               this.sourceBuild.getPathingBehavior().cancelEverything();
            } else {
               NicknameUtil.instance.handle();
               String var8 = NicknameUtil.instance.compute();
               if (!"N/A".equals(var8)) {
                  this.vectorEncode = var8;
               }

               this.timerRender = this.profileInvoke;
               this.process(() -> {
                  Module.client.player.networkHandler.sendChatCommand("hub");
                  this.outputCollapse = WardenFarm.CachedMode.HUB_WAITING_FOR_CHEST;
               });
            }
         } else if (var3) {
            BlockPos var5 = this.drawAnimation(this.profileInvoke);
            if (var5 != null) {
               this.encodePoint(var5);
            } else {
               if (this.shaderProject == null || !this.shaderProject.equals(this.profileInvoke)) {
                  this.shaderProject = this.profileInvoke;
                  this.inputInvoke = System.currentTimeMillis();
               }

               if (!this.sourceBuild.getCustomGoalProcess().isActive()) {
                  this.sourceSchedule = this.load(this.profileInvoke);
                  if (this.sourceSchedule != null) {
                     this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.sourceSchedule));
                  }
               }

               if (System.currentTimeMillis() - this.inputInvoke >= 4000L) {
                  this.rendererScan.put(this.profileInvoke, System.currentTimeMillis() + 30000L);
                  this.sourceBuild.getPathingBehavior().cancelEverything();
                  this.profileInvoke = null;
                  this.shaderProject = null;
                  this.scaleSave = -1.0;
                  this.textureRun.handle();
                  this.actionRead.handle();
                  this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
                  this.configCollapse.handle();
               }
            }
         } else {
            this.shaderProject = null;
            if (!this.sourceBuild.getCustomGoalProcess().isActive()) {
               this.sourceSchedule = this.load(this.profileInvoke);
               this.adaptRequest();
            }

            if (this.textureRun.update(15000L)) {
               this.rendererScan.put(this.profileInvoke, System.currentTimeMillis() + 30000L);
               this.sourceBuild.getPathingBehavior().cancelEverything();
               this.profileInvoke = null;
               this.shaderProject = null;
               this.scaleSave = -1.0;
               this.textureRun.handle();
               this.actionRead.handle();
               this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
               this.configCollapse.handle();
            }
         }
      }
   }

   private void readServer() {
      if (this.profileInvoke == null) {
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else {
         BlockPos var1 = this.drawAnimation(this.profileInvoke);
         if (var1 != null) {
            this.encodePoint(var1);
         } else if (!this.measure(this.profileInvoke)) {
            this.scaleSave = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.profileInvoke));
            this.outputCollapse = WardenFarm.CachedMode.GOING_TO_CHEST;
            this.textureRun.handle();
         } else {
            RotationAngles var2 = this.compute(this.fetch(this.profileInvoke));
            this.effectApply.handle(this.handle(var2, this.handle(var2)), 35.0F, 35.0F, 35.0F, 35.0F, 20, 1);
            if (this.blendMatrix(this.profileInvoke) != null) {
               this.outputCollapse = WardenFarm.CachedMode.OPENING;
               this.colorCompute.handle();
            }
         }
      }
   }

   private void advancePosition() {
      if (this.profileInvoke == null) {
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else {
         BlockPos var1 = this.drawAnimation(this.profileInvoke);
         if (var1 != null) {
            this.encodePoint(var1);
         } else if (!this.measure(this.profileInvoke)) {
            this.scaleSave = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.profileInvoke));
            this.outputCollapse = WardenFarm.CachedMode.GOING_TO_CHEST;
            this.textureRun.handle();
         } else if (!this.encodeSession()) {
            this.applyState();
         } else {
            int var2 = Module.client.player.getInventory().getSelectedSlot();
            ItemStack var3 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var2);
            if (var3.getItem() == Items.TRIPWIRE_HOOK || var3.getName().getString().contains("[★]")) {
               for (int var4 = 0; var4 < 9; var4++) {
                  ItemStack var5 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var4);
                  if (var5.isEmpty() || var5.getItem() != Items.TRIPWIRE_HOOK && !var5.getName().getString().contains("[★]")) {
                     Module.client.player.getInventory().setSelectedSlot(var4);
                     this.colorCompute.handle();
                     break;
                  }
               }
            }

            RotationAngles var6 = this.compute(this.fetch(this.profileInvoke));
            this.effectApply.handle(this.handle(var6, 0.6F), 26.0F, 26.0F, 28.0F, 28.0F, 20, 1);
            BlockHitResult var7 = this.blendMatrix(this.profileInvoke);
            if (var7 == null) {
               if (this.colorCompute.update(700L)) {
                  this.outputCollapse = WardenFarm.CachedMode.ROTATING;
                  this.colorCompute.handle();
               }
            } else {
               if (this.colorCompute.update(0L)) {
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var7);
                  this.pathProject++;
                  this.inputHandle = false;
                  this.entityAdvance = System.currentTimeMillis();
                  this.applyState();
                  this.computeResponse();
                  this.rendererScan.put(this.profileInvoke, System.currentTimeMillis() + 5000L);
                  this.outputCollapse = WardenFarm.CachedMode.WAITING_FOR_GUI;
                  this.colorCompute.handle();
               }
            }
         }
      }
   }

   private boolean checkFrame() {
      for (PlayerEntity var2 : Module.client.world.getPlayers()) {
         if (var2 != Module.client.player && !var2.isDead() && Module.client.player.distanceTo(var2) <= 40.0) {
            return true;
         }
      }

      for (Entity var4 : Module.client.world.getEntities()) {
         if (var4 instanceof WardenEntity && Module.client.player.distanceTo(var4) <= 24.0) {
            return true;
         }
      }

      return false;
   }

   private Vec3d collectModule() {
      double var1 = Module.client.player.getX();
      double var3 = Module.client.player.getZ();
      double var5 = 0.0;
      double var7 = 0.0;

      for (PlayerEntity var10 : Module.client.world.getPlayers()) {
         if (var10 != Module.client.player && !var10.isDead()) {
            double var11 = var1 - var10.getX();
            double var13 = var3 - var10.getZ();
            double var15 = Math.max(1.0, Math.hypot(var11, var13));
            double var17 = 1.0 / (var15 * var15);
            var5 += var11 / var15 * var17;
            var7 += var13 / var15 * var17;
         }
      }

      for (Entity var21 : Module.client.world.getEntities()) {
         if (var21 instanceof WardenEntity) {
            double var22 = var1 - var21.getX();
            double var24 = var3 - var21.getZ();
            double var25 = Math.max(1.0, Math.hypot(var22, var24));
            double var26 = 1.5 / (var25 * var25);
            var5 += var22 / var25 * var26;
            var7 += var24 / var25 * var26;
         }
      }

      double var20 = Math.hypot(var5, var7);
      if (var20 < 1.0E-6) {
         double var23 = Math.random() * Math.PI * 2.0;
         return new Vec3d(Math.cos(var23), 0.0, Math.sin(var23));
      } else {
         return new Vec3d(var5 / var20, 0.0, var7 / var20);
      }
   }

   private double handle(double var1, double var3) {
      double var5 = 0.0;

      for (PlayerEntity var8 : Module.client.world.getPlayers()) {
         if (var8 != Module.client.player && !var8.isDead()) {
            double var9 = Math.hypot(var8.getX() - var1, var8.getZ() - var3);
            var5 += 12.0 / (var9 + 2.0);
            if (var9 < 10.0) {
               var5 += (10.0 - var9) * 2.0;
            }
         }
      }

      for (Entity var12 : Module.client.world.getEntities()) {
         if (var12 instanceof WardenEntity) {
            double var13 = Math.hypot(var12.getX() - var1, var12.getZ() - var3);
            var5 += 18.0 / (var13 + 2.0);
            if (var13 < 14.0) {
               var5 += (14.0 - var13) * 3.0;
            }
         }
      }

      return var5;
   }

   private boolean handle(double var1, double var3, double var5, double var7) {
      byte var9 = 6;

      for (int var10 = 1; var10 <= var9; var10++) {
         double var11 = (double)var10 / var9;
         double var13 = var1 + (var5 - var1) * var11;
         double var15 = var3 + (var7 - var3) * var11;

         for (PlayerEntity var18 : Module.client.world.getPlayers()) {
            if (var18 != Module.client.player && !var18.isDead() && Math.hypot(var18.getX() - var13, var18.getZ() - var15) < 6.0) {
               return false;
            }
         }
      }

      return true;
   }

   private int[] closeProvider() {
      Vec3d var1 = this.collectModule();
      double var2 = Math.atan2(var1.z, var1.x);
      double var4 = Module.client.player.getX();
      double var6 = Module.client.player.getZ();
      double var8 = Double.MAX_VALUE;
      int[] var10 = null;
      int[] var11 = null;
      double var12 = Double.MAX_VALUE;

      for (int var14 = 0; var14 < 32; var14++) {
         double var15 = Math.toRadians(15.0 + Math.random() * 65.0);
         double var17 = Math.random() < 0.8 ? var2 + (Math.random() * 2.0 - 1.0) * var15 : Math.random() * Math.PI * 2.0;
         double var19 = 22.0 + Math.random() * 26.0;
         double var21 = var4 + var19 * Math.cos(var17);
         double var23 = var6 + var19 * Math.sin(var17);
         if (this.process(new Vec3d(var21, Module.client.player.getY(), var23))) {
            double var25 = this.handle(var21, var23);
            if (var25 < var12) {
               var12 = var25;
               var11 = new int[]{(int)var21, (int)var23};
            }

            if (this.handle(var4, var6, var21, var23) && var25 < var8) {
               var8 = var25;
               var10 = new int[]{(int)var21, (int)var23};
            }
         }
      }

      if (var10 != null) {
         return var10;
      }

      if (var11 != null) {
         return var11;
      }

      double var27 = var4 + var1.x * 26.0;
      double var16 = var6 + var1.z * 26.0;
      return this.process(new Vec3d(var27, Module.client.player.getY(), var16)) ? new int[]{(int)var27, (int)var16} : null;
   }

   private void savePreset() {
      this.applyState();
      if (this.previous.compute() && this.drawAnimation() && this.checkFrame()) {
         int[] var1 = this.closeProvider();
         if (var1 != null) {
            this.layerProject = var1;
            this.sourceBuild.getPathingBehavior().cancelEverything();
            this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalXZ(var1[0], var1[1]));
            this.outputCollapse = WardenFarm.CachedMode.RETREATING;
            this.textureRun.handle();
            this.actionRead.handle();
         }
      }
   }

   private void convertWindow() {
      this.applyState();
      if (this.layerProject == null) {
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else {
         boolean var1 = Math.hypot(Module.client.player.getX() - this.layerProject[0], Module.client.player.getZ() - this.layerProject[1]) <= 3.0;
         if (!var1 && this.checkFrame() && !this.textureRun.update(15000L)) {
            if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(2500L)) {
               this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalXZ(this.layerProject[0], this.layerProject[1]));
               this.actionRead.handle();
            }
         } else {
            this.sourceBuild.getPathingBehavior().cancelEverything();
            this.layerProject = null;
            this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
            this.configCollapse.handle();
         }
      }
   }

   private boolean writePreset() {
      if (this.entityFilter == null) {
         return false;
      }

      if (this.latest.compute() && System.currentTimeMillis() <= this.layerSample2) {
         return true;
      }

      this.entityFilter = null;
      return false;
   }

   private void measureColor() {
      if (!this.writePreset()) {
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else {
         double var1 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.entityFilter));
         if (var1 <= 6.0) {
            this.sourceBuild.getPathingBehavior().cancelEverything();
            this.outputCollapse = WardenFarm.CachedMode.COLLECTING_DEATH_LOOT;
            this.colorCompute.handle();
            this.textureRun.handle();
            this.actionRead.handle();
         } else {
            if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(2500L)) {
               this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.entityFilter, 2));
               this.actionRead.handle();
            }

            if (this.textureRun.update(40000L)) {
               this.scanRenderer();
            }
         }
      }
   }

   private void scheduleAnimation() {
      if (!this.writePreset()) {
         this.scanRenderer();
      } else {
         ItemEntity var1 = null;
         double var2 = Double.MAX_VALUE;

         for (Entity var5 : Module.client.world.getEntities()) {
            if (var5 instanceof ItemEntity var6 && var6.isAlive() && !(var6.getPos().distanceTo(Vec3d.ofCenter(this.entityFilter)) > 16.0)) {
               double var7 = Module.client.player.getPos().distanceTo(var6.getPos());
               if (var7 < var2) {
                  var2 = var7;
                  var1 = var6;
               }
            }
         }

         if (var1 == null) {
            if (this.colorCompute.update(2000L)) {
               this.scanRenderer();
            }
         } else {
            this.colorCompute.handle();
            if (this.textureRun.update(90000L)) {
               this.scanRenderer();
            } else {
               if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(1500L)) {
                  this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalNear(var1.getBlockPos(), 1));
                  this.actionRead.handle();
               }
            }
         }
      }
   }

   private void scanRenderer() {
      this.entityFilter = null;
      this.layerSample2 = 0L;
      this.sourceBuild.getPathingBehavior().cancelEverything();
      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.configCollapse.handle();
      if (this.providerFetch.compute() && this.convertAction()) {
         this.performScale();
      }
   }

   private void buildSource() {
      NicknameUtil.instance.handle();
      if (NicknameUtil.instance.compute().equals(this.serverRead.compute())) {
         if (this.colorCompute.update(100L)) {
            this.outputCollapse = WardenFarm.CachedMode.GOING_TO_STASH;
            this.colorCompute.handle();
            this.textureRun.handle();
         }
      } else {
         this.colorCompute.handle();
         if (!this.projectItem() && this.textureRun.update(4000L)) {
            Module.client.player.networkHandler.sendChatCommand("an" + this.serverRead.compute());
            this.textureRun.handle();
         }
      }
   }

   private void collapseOutput() {
      this.applyState();
      if (!this.convertAction()) {
         this.readScreen();
      } else if (this.profileDraw.process("В клан")) {
         if (this.colorCompute.update(500L)) {
            this.outputCollapse = WardenFarm.CachedMode.OPENING_STASH;
            this.colorCompute.handle();
         }
      } else {
         if (this.colorCompute.update(300L)) {
            this.parseMessage();
            this.colorCompute.handle();
         }

         if (this.playerCollect != null && this.computeResponse(this.playerCollect)) {
            double var1 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.playerCollect));
            if (var1 <= 2.9 && this.unload(this.playerCollect)) {
               this.sourceBuild.getPathingBehavior().cancelEverything();
               this.outputCollapse = WardenFarm.CachedMode.ROTATING_STASH;
               this.colorCompute.handle();
            } else {
               if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(2500L)) {
                  this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.playerCollect, 1));
                  this.actionRead.handle();
               }

               if (this.textureRun.update(15000L)) {
                  this.sourceBuild.getPathingBehavior().cancelEverything();
                  this.readScreen();
               }
            }
         } else {
            if (this.textureRun.update(15000L)) {
               this.readScreen();
            }
         }
      }
   }

   private void invokeProfile() {
      if (this.playerCollect == null) {
         this.outputCollapse = WardenFarm.CachedMode.GOING_TO_STASH;
         this.colorCompute.handle();
         this.textureRun.handle();
      } else if (!this.measure(this.playerCollect)) {
         this.outputCollapse = WardenFarm.CachedMode.GOING_TO_STASH;
         this.colorCompute.handle();
         this.textureRun.handle();
      } else {
         RotationAngles var1 = this.compute(this.fetch(this.playerCollect));
         this.effectApply.handle(this.handle(var1, this.handle(var1)), 45.0F, 45.0F, 45.0F, 45.0F, 20, 1);
         if (this.blendMatrix(this.playerCollect) != null && this.colorCompute.update(70L)) {
            this.outputCollapse = WardenFarm.CachedMode.OPENING_STASH_BLOCK;
            this.colorCompute.handle();
         }
      }
   }

   private void scheduleSource() {
      int var1 = Module.client.player.getInventory().getSelectedSlot();
      ItemStack var2 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var1);
      if (var2.getItem() == Items.TRIPWIRE_HOOK || var2.getName().getString().contains("[★]")) {
         for (int var3 = 0; var3 < 9; var3++) {
            ItemStack var4 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var3);
            if (var4.isEmpty() || var4.getItem() != Items.TRIPWIRE_HOOK && !var4.getName().getString().contains("[★]")) {
               Module.client.player.getInventory().setSelectedSlot(var3);
               this.colorCompute.handle();
               break;
            }
         }
      }

      if (!this.measure(this.playerCollect)) {
         this.outputCollapse = WardenFarm.CachedMode.GOING_TO_STASH;
         this.colorCompute.handle();
         this.textureRun.handle();
      } else {
         RotationAngles var5 = this.compute(this.fetch(this.playerCollect));
         this.effectApply.handle(this.handle(var5, 0.6F), 26.0F, 26.0F, 28.0F, 28.0F, 20, 1);
         BlockHitResult var6 = this.blendMatrix(this.playerCollect);
         if (var6 == null) {
            if (this.colorCompute.update(700L)) {
               this.outputCollapse = WardenFarm.CachedMode.ROTATING_STASH;
               this.colorCompute.handle();
            }
         } else {
            if (this.colorCompute.update(90L)) {
               Module.client.player.swingHand(Hand.MAIN_HAND);
               Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var6);
               RotationController.instance = RotationController.Mode.IDLE;
               this.outputCollapse = WardenFarm.CachedMode.WAITING_FOR_GUI_STASH;
               this.colorCompute.handle();
            }
         }
      }
   }

   private void renderTimer() {
      this.sourceBuild.getPathingBehavior().cancelEverything();
      if (this.colorCompute.update(2000L)) {
         if (this.stateApply < 3) {
            this.stateApply++;
            this.outputCollapse = WardenFarm.CachedMode.GOING_TO_STASH;
            this.colorCompute.handle();
            this.textureRun.handle();
         } else {
            this.readScreen();
         }
      }
   }

   private void saveScale() {
      this.sourceBuild.getPathingBehavior().cancelEverything();
      if (this.colorCompute.update(1500L) && Module.client.currentScreen == null) {
         Module.client.player.networkHandler.sendChatCommand("clan storage");
         this.outputCollapse = WardenFarm.CachedMode.WAITING_FOR_GUI_STASH;
         this.colorCompute.handle();
      }
   }

   private void computeColor() {
      this.sourceBuild.getPathingBehavior().cancelEverything();
      this.applyState();
      if (this.encodeSession()) {
         if (this.entityAdvance > 0L && System.currentTimeMillis() - this.entityAdvance >= 1000L) {
            if (this.profileInvoke != null && this.pathProject < 2 && !this.inputHandle && this.measure(this.profileInvoke)) {
               this.outputCollapse = WardenFarm.CachedMode.OPENING;
               this.colorCompute.handle();
            } else {
               this.handle(10000L);
            }
         } else {
            if (this.colorCompute.update(100L) && this.profileInvoke != null && this.pathProject < 2 && this.measure(this.profileInvoke)) {
               BlockHitResult var1 = this.blendMatrix(this.profileInvoke);
               if (var1 != null) {
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
                  this.pathProject++;
                  this.applyState();
                  this.colorCompute.handle();
               }
            }
         }
      }
   }

   private void handle(long var1) {
      this.applyState();
      if (this.profileInvoke != null) {
         this.rendererScan.put(this.profileInvoke, System.currentTimeMillis() + var1);
      }

      this.profileInvoke = null;
      this.sourceSchedule = null;
      this.entityAdvance = 0L;
      this.pathProject = 0;
      this.inputHandle = false;
      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.configCollapse.handle();
   }

   private boolean adaptScale() {
      long var1 = Long.MAX_VALUE;

      for (BlockPos var4 : this.windowConvert) {
         long var5 = this.handle(var4) - System.currentTimeMillis();
         if (var5 < var1) {
            var1 = var5;
         }
      }

      if ((this.windowConvert.isEmpty() || var1 > this.receiveRequest()) && (this.vectorPerform.compute() || var1 != Long.MAX_VALUE)) {
         if (this.projectItem()) {
            return false;
         }

         NicknameUtil.instance.handle();
         String var7 = NicknameUtil.instance.compute();
         if (!"N/A".equals(var7)) {
            this.vectorEncode = var7;
         }

         this.sourceBuild.getPathingBehavior().cancelEverything();
         this.profileInvoke = null;
         if (this.vectorPerform.compute()) {
            String var8 = this.animate();
            this.vectorEncode = var8;
            this.process(() -> {
               Module.client.player.networkHandler.sendChatCommand("hub");
               this.requestAdapt = true;
               this.timerMeasure = System.currentTimeMillis() + 1700L;
            });
         } else {
            long var9 = var1 - 25000L;
            this.process(() -> {
               Module.client.player.networkHandler.sendChatCommand("hub");
               this.requestAdapt = true;
               this.timerMeasure = System.currentTimeMillis() + var9;
               this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
               this.configCollapse.handle();
            });
         }

         return true;
      } else {
         return false;
      }
   }

   private void runTexture() {
      if (!this.pending.compute()) {
         this.bindIndex();
      } else {
         boolean var1 = this.eventSend;
         long var2 = System.currentTimeMillis();
         if (this.messageParse2.update(300L)) {
            var1 = false;
            BlockPos var4 = Module.client.player.getBlockPos();

            for (BlockPos var6 : BlockPos.iterate(var4.add(-5, -5, -5), var4.add(5, 5, 5))) {
               Block var7 = Module.client.world.getBlockState(var6).getBlock();
               if (var7 == Blocks.SCULK || var7 == Blocks.SCULK_SENSOR || var7 == Blocks.SCULK_SHRIEKER || var7 == Blocks.SCULK_CATALYST) {
                  var1 = true;
                  break;
               }
            }

            this.eventSend = var1;
            this.messageParse2.handle();
         }

         if (var1) {
            this.providerOffset = var2;
            this.sourceCancel = true;
         }

         if (this.sourceCancel) {
            if (!var1 && var2 - this.providerOffset >= 1200L) {
               this.bindIndex();
            } else {
               if (!Module.client.player.isSneaking()) {
                  Module.client.player.setSneaking(true);
               }

               if ((Boolean)BaritoneAPI.getSettings().allowSprint.value) {
                  BaritoneAPI.getSettings().allowSprint.value = false;
               }
            }
         }
      }
   }

   private void bindIndex() {
      if (this.sourceCancel) {
         this.sourceCancel = false;
         this.providerOffset = 0L;
         this.eventSend = false;
         if (Module.client.player != null) {
            Module.client.player.setSneaking(false);
         }

         BaritoneAPI.getSettings().allowSprint.value = this.windowProcess;
      }
   }

   private long handle(String var1, boolean var2) {
      Matcher var3 = var2 ? layerSample.matcher(var1) : worldSend.matcher(var1);
      if (var3.find()) {
         try {
            if (var2) {
               return (Integer.parseInt(var3.group(1)) * 60L + Integer.parseInt(var3.group(2))) * 1000L;
            }

            int var8 = Integer.parseInt(var3.group(1));
            int var5 = Integer.parseInt(var3.group(2));
            return var3.group(3) != null ? (var8 * 3600L + var5 * 60L + Integer.parseInt(var3.group(3))) * 1000L : (var8 * 60L + var5) * 1000L;
         } catch (NumberFormatException var7) {
         }
      }

      Matcher var4 = targetWrite.matcher(var1);
      if (var4.find()) {
         try {
            return Integer.parseInt(var4.group(1)) * 1000L;
         } catch (NumberFormatException var6) {
         }
      }

      return -1L;
   }

   private void readAction() {
      boolean var1 = this.source.process("Варден");
      Block var2 = var1 ? Blocks.CHEST : Blocks.BARREL;

      for (Entity var4 : Module.client.world.getEntities()) {
         if (var4 instanceof ArmorStandEntity) {
            long var5 = this.handle(var4.getName().getString(), var1);
            if (var5 >= 0L) {
               BlockPos var7 = new BlockPos(var4.getBlockX(), var4.getBlockY() - 1, var4.getBlockZ());
               if (Module.client.world.getBlockState(var7).getBlock() == var2) {
                  this.animationSchedule.put(var7, System.currentTimeMillis() + var5);
               } else {
                  var7 = var7.down();
                  if (Module.client.world.getBlockState(var7).getBlock() == var2) {
                     this.animationSchedule.put(var7, System.currentTimeMillis() + var5);
                  }
               }
            }
         }
      }
   }

   private boolean process(BlockPos var1) {
      if (Module.client.world != null && var1 != null) {
         boolean var2 = this.source.process("Варден");

         for (Entity var4 : Module.client.world.getEntities()) {
            if (var4 instanceof ArmorStandEntity && this.handle(var4.getName().getString(), var2) > 250L) {
               BlockPos var5 = new BlockPos(var4.getBlockX(), var4.getBlockY() - 1, var4.getBlockZ());
               if (var5.equals(var1) || var5.down().equals(var1)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean compute(BlockPos var1) {
      Long var2 = this.rendererScan.get(var1);
      return var2 != null && var2 > System.currentTimeMillis() ? true : this.tick(var1) > this.update(var1);
   }

   private long resolve(BlockPos var1) {
      double var2 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var1));
      return (long)(var2 / 3.0 * 1000.0);
   }

   private long update(BlockPos var1) {
      return Math.max(20000L, this.resolve(var1) + 5000L);
   }

   private long apply(BlockPos var1) {
      return Math.max(this.resolve(var1), Math.max(0L, this.tick(var1)));
   }

   private void collapseConfig() {
      if (Module.client.world == null) {
         this.presetSave.clear();
      } else {
         long var1 = System.currentTimeMillis() + 5000L;

         for (Entity var4 : Module.client.world.getEntities()) {
            if (var4 instanceof WardenEntity var5 && this.process(var5.getPos())) {
               this.presetSave.put(var5.getBlockPos().toImmutable(), var1);
            }
         }

         long var6 = System.currentTimeMillis();
         this.presetSave.entrySet().removeIf(var2 -> var2.getValue() <= var6);
      }
   }

   private boolean execute(BlockPos var1) {
      if (!this.source.process("Варден")) {
         return false;
      }

      for (BlockPos var3 : this.presetSave.keySet()) {
         if (var3.getSquaredDistance(var1) < 25.0) {
            return true;
         }
      }

      return false;
   }

   private boolean prepare(BlockPos var1) {
      if (Module.client.world != null && Module.client.player != null) {
         Vec3d var2 = Vec3d.ofCenter(var1);

         for (PlayerEntity var4 : Module.client.world.getPlayers()) {
            if (var4 != Module.client.player
               && !var4.isDead()
               && !(var4.getPos().squaredDistanceTo(var2) >= 20.0)
               && (
                  !var4.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
                     || !var4.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
                     || !var4.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
                     || !var4.getEquippedStack(EquipmentSlot.FEET).isEmpty()
               )) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean check(BlockPos var1) {
      return this.save(var1) && (!this.source.process("Варден") || !this.execute(var1) && !this.prepare(var1));
   }

   private boolean onTick(BlockPos var1) {
      long var2 = Math.max(0L, this.tick(var1));
      return var2 >= 20000L && var2 <= 35000L;
   }

   private int select(BlockPos var1) {
      if (this.onTick(var1)) {
         return 0;
      } else {
         return this.tick(var1) <= 0L ? 1 : 2;
      }
   }

   private double refresh(BlockPos var1) {
      double var2 = var1.getX() + 0.5 - Module.client.player.getX();
      double var4 = var1.getY() + 0.5 - Module.client.player.getEyeY();
      double var6 = var1.getZ() + 0.5 - Module.client.player.getZ();
      return var2 * var2 + var6 * var6 + var4 * var4 * (var4 > 0.0 ? 2.0 : 1.0);
   }

   private void validateData() {
      if (this.clientRefresh.update(750L)) {
         this.clientRefresh.handle();
         this.windowConvert.clear();
         BlockPos var1 = Module.client.player.getBlockPos();
         ChunkPos var2 = new ChunkPos(var1);
         byte var3 = 10;
         Block var4 = this.source.process("Варден") ? Blocks.CHEST : Blocks.BARREL;

         for (int var5 = -var3; var5 <= var3; var5++) {
            for (int var6 = -var3; var6 <= var3; var6++) {
               WorldChunk var7 = Module.client.world.getChunk(var2.x + var5, var2.z + var6);
               if (var7 != null) {
                  for (BlockPos var9 : var7.getBlockEntities().keySet()) {
                     if (var7.getBlockState(var9).getBlock() == var4 && this.process(Vec3d.ofCenter(var9))) {
                        this.windowConvert.add(var9);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean renderScale() {
      return this.providerFetch.compute() && this.convertAction();
   }

   private void render(BlockPos var1) {
      this.applyState();
      if (var1 != null) {
         this.regionAlign.add(var1);
      }

      this.sourceBuild.getPathingBehavior().cancelEverything();
      this.profileInvoke = null;
      this.shaderProject = null;
      this.scaleSave = -1.0;
      this.entityAdvance = 0L;
      this.textureRun.handle();
      this.actionRead.handle();
      boolean var2 = this.providerFetch.compute() && this.convertAction();
      if (var2 && this.refreshClient()) {
         BlockPos var3 = this.filterKey();
         if (var3 != null) {
            this.profileInvoke = var3;
            this.sourceSchedule = this.load(var3);
            this.adaptRequest();
            this.scaleSave = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var3));
            this.outputCollapse = WardenFarm.CachedMode.GOING_TO_CHEST;
            this.entityAdvance = 0L;
            this.inputAttach = true;
            this.textureRun.handle();
            this.configCollapse.handle();
            return;
         }
      }

      if (!var2) {
         this.providerClose.clear();
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
         this.savePreset();
      } else {
         this.performScale();
         if (this.outputCollapse == WardenFarm.CachedMode.SEARCHING) {
            this.savePreset();
         }
      }
   }

   private boolean refreshClient() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private BlockPos filterKey() {
      this.validateData();
      long var1 = System.currentTimeMillis();
      return this.windowConvert
         .stream()
         .filter(var1x -> !this.regionAlign.contains(var1x))
         .filter(var3 -> {
            Long var4 = this.rendererScan.get(var3);
            return var4 == null || var4 <= var1;
         })
         .filter(this::check)
         .filter(var0 -> Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var0)) <= 14.0)
         .filter(var1x -> this.tick(var1x) <= 35000L)
         .min(
            Comparator.<BlockPos>comparingInt(this::select)
               .thenComparingLong(this::apply)
               .thenComparingDouble(var0 -> Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var0)))
         )
         .orElse(null);
   }

   private long tick(BlockPos var1) {
      return this.handle(var1) - System.currentTimeMillis();
   }

   private BlockPos drawAnimation(BlockPos var1) {
      if (Module.client.player == null) {
         return null;
      }

      long var2 = System.currentTimeMillis();
      BlockPos var4 = null;
      double var5 = Double.MAX_VALUE;

      for (BlockPos var8 : this.windowConvert) {
         if (!var8.equals(var1) && !this.regionAlign.contains(var8)) {
            Long var9 = this.rendererScan.get(var8);
            if ((var9 == null || var9 <= var2) && this.tick(var8) <= -300L) {
               double var10 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var8));
               if (!(var10 > 14.0) && !(var10 >= var5)) {
                  var5 = var10;
                  var4 = var8;
               }
            }
         }
      }

      return var4;
   }

   private void encodePoint(BlockPos var1) {
      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
      }

      this.applyState();
      this.profileInvoke = var1;
      this.pathProject = 0;
      this.inputHandle = false;
      this.sourceSchedule = this.load(var1);
      this.scaleSave = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var1));
      this.entityAdvance = 0L;
      this.inputAttach = false;
      this.shaderProject = null;
      this.textureRun.handle();
      this.actionRead.handle();
      this.outputCollapse = WardenFarm.CachedMode.GOING_TO_CHEST;
      this.adaptRequest();
      this.configCollapse.handle();
   }

   private void adaptRequest() {
      if (this.sourceBuild != null && this.profileInvoke != null) {
         if (this.sourceSchedule != null) {
            this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.sourceSchedule));
         } else {
            this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.profileInvoke, 1));
         }
      }
   }

   private BlockPos measureTimer() {
      long var1 = System.currentTimeMillis();
      return this.windowConvert
         .stream()
         .filter(var1x -> !this.regionAlign.contains(var1x))
         .filter(var3 -> {
            Long var4 = this.rendererScan.get(var3);
            return var4 == null || var4 <= var1;
         })
         .filter(this::check)
         .filter(var1x -> {
            long var2 = this.tick(var1x);
            return var2 >= 20000L && var2 <= 35000L;
         })
         .filter(var1x -> this.resolve(var1x) <= this.tick(var1x) + 5000L)
         .min(Comparator.<BlockPos>comparingLong(this::apply).thenComparingLong(var1x -> Math.max(0L, this.tick(var1x))).thenComparingDouble(this::refresh))
         .orElse(null);
   }

   private void encodeVector() {
      this.validateData();
      BlockPos var1 = this.measureTimer();
      if (var1 != null && !var1.equals(this.profileInvoke)) {
         long var2 = System.currentTimeMillis();
         long var4 = this.apply(var1);
         long var6 = this.profileInvoke == null ? Long.MAX_VALUE : this.apply(this.profileInvoke);
         if (this.profileInvoke == null || !this.onTick(this.profileInvoke) || var2 - this.sessionCollect >= 750L && var4 + 750L < var6) {
            this.sessionCollect = var2;
            this.encodePoint(var1);
         }
      }
   }

   private long receiveRequest() {
      return (long)(this.vectorMatch.compute() * 1000.0F);
   }

   private BlockPos processWindow() {
      return this.windowConvert
         .stream()
         .filter(var1 -> !this.regionAlign.contains(var1))
         .filter(var1 -> !this.compute(var1))
         .filter(this::check)
         .min(Comparator.<BlockPos>comparingInt(this::select).thenComparingLong(this::apply).thenComparingDouble(this::refresh))
         .orElse(null);
   }

   private boolean handle(Block var1) {
      return var1 == Blocks.SCULK || var1 == Blocks.SCULK_SENSOR || var1 == Blocks.SCULK_SHRIEKER || var1 == Blocks.SCULK_CATALYST || var1 == Blocks.SCULK_VEIN;
   }

   private boolean animate(BlockPos var1) {
      BlockPos var2 = var1.up();
      BlockPos var3 = var1.down();
      BlockState var4 = Module.client.world.getBlockState(var1);
      BlockState var5 = Module.client.world.getBlockState(var2);
      BlockState var6 = Module.client.world.getBlockState(var3);
      return var4.getFluidState().isEmpty()
         && var4.getCollisionShape(Module.client.world, var1).isEmpty()
         && var5.getFluidState().isEmpty()
         && var5.getCollisionShape(Module.client.world, var2).isEmpty()
         && !var6.getCollisionShape(Module.client.world, var3).isEmpty()
         && !this.handle(var4.getBlock())
         && !this.handle(var5.getBlock())
         && !this.handle(var6.getBlock());
   }

   private BlockPos load(BlockPos var1) {
      ArrayList<BlockPos> var2 = new ArrayList<>();
      var2.add(var1);
      int[] var3 = new int[]{1, -1, 0, 0};
      int[] var4 = new int[]{0, 0, 1, -1};
      if (this.source.process("╨Æ╨░╤Ç╨┤╨╡╨╜")) {
         for (int var5 = 0; var5 < var3.length; var5++) {
            BlockPos var6 = var1.add(var3[var5], 0, var4[var5]);
            if (Module.client.world.getBlockState(var6).isOf(Blocks.CHEST)) {
               var2.add(var6);
            }
         }
      }

      int[] var21 = new int[]{1, -1, 0, 0, 1, 1, -1, -1};
      int[] var22 = new int[]{0, 0, 1, -1, 1, -1, 1, -1};
      HashSet<BlockPos> var7 = new HashSet<>();

      for (BlockPos var9 : var2) {
         for (int var10 = 0; var10 < var21.length; var10++) {
            var7.add(var9.add(var21[var10], 0, var22[var10]));
         }
      }

      BlockPos var23 = null;
      double var24 = Double.MAX_VALUE;

      for (BlockPos var12 : var7) {
         if (this.animate(var12)) {
            Vec3d var13 = Vec3d.ofBottomCenter(var12).add(0.0, Module.client.player.getStandingEyeHeight(), 0.0);
            double var14 = Double.MAX_VALUE;
            boolean var16 = false;

            for (BlockPos var18 : var2) {
               Vec3d var19 = Vec3d.ofCenter(var18);
               var14 = Math.min(var14, var13.distanceTo(var19));
               BlockHitResult var20 = Module.client.world.raycast(new RaycastContext(var13, var19, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
               if (var20.getType() == Type.MISS || var2.contains(var20.getBlockPos())) {
                  var16 = true;
               }
            }

            if (var16 && !(var14 > 2.9)) {
               double var25 = Module.client.player.getPos().squaredDistanceTo(Vec3d.ofBottomCenter(var12));
               if (var25 < var24) {
                  var24 = var25;
                  var23 = var12;
               }
            }
         }
      }

      return var23;
   }

   private boolean save(BlockPos var1) {
      return this.load(var1) != null;
   }

   private Vec3d submit(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.getX();
      double var5 = var1.getY();
      double var7 = var1.getZ();
      Vec3d[] var9 = new Vec3d[]{
         new Vec3d(var3 + 0.5, var5 + 0.5, var7 + 0.5),
         new Vec3d(var3 + 0.5, var5 + 0.9, var7 + 0.5),
         new Vec3d(var3 + 0.5, var5 + 0.5, var7 + 0.05),
         new Vec3d(var3 + 0.5, var5 + 0.5, var7 + 0.95),
         new Vec3d(var3 + 0.05, var5 + 0.5, var7 + 0.5),
         new Vec3d(var3 + 0.95, var5 + 0.5, var7 + 0.5)
      };

      for (Vec3d var13 : var9) {
         BlockHitResult var14 = Module.client.world.raycast(new RaycastContext(var2, var13, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
         if (var14.getType() == Type.MISS || var14.getBlockPos().equals(var1)) {
            return var13;
         }
      }

      return null;
   }

   private boolean unload(BlockPos var1) {
      return this.submit(var1) != null;
   }

   private Vec3d fetch(BlockPos var1) {
      Vec3d var2 = this.submit(var1);
      return var2 != null ? var2 : Vec3d.ofCenter(var1);
   }

   private boolean measure(BlockPos var1) {
      return var1 != null && Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var1)) <= 2.9 && this.unload(var1);
   }

   private BlockHitResult blendMatrix(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      Vec3d var3 = var2.add(Module.client.player.getRotationVec(1.0F).multiply(3.4));
      BlockHitResult var4 = Module.client.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var4.getType() == Type.BLOCK && var4.getBlockPos().equals(var1) ? var4 : null;
   }

   private float handle(RotationAngles var1) {
      float var2 = new RotationAngles(Module.client.player).handle(var1);
      return Math.min(2.0F, 0.45F + var2 * 0.1F);
   }

   private void savePacket() {
      ArrayList var1 = new ArrayList((Collection)BaritoneAPI.getSettings().blocksToAvoid.value);
      this.playerPerform = new ArrayList<>(var1);
      Block[] var2 = new Block[]{Blocks.SCULK, Blocks.SCULK_SENSOR, Blocks.SCULK_SHRIEKER, Blocks.SCULK_CATALYST, Blocks.SCULK_VEIN};

      for (Block var6 : var2) {
         if (!var1.contains(var6)) {
            var1.add(var6);
         }
      }

      BaritoneAPI.getSettings().blocksToAvoid.value = var1;
   }

   private void animateEntry() {
      if (this.playerPerform != null) {
         BaritoneAPI.getSettings().blocksToAvoid.value = this.playerPerform;
         this.playerPerform = null;
      }
   }

   private void collectPlayer() {
      if (Module.client.player != null
         && this.sourceBuild != null
         && this.sourceBuild.getCustomGoalProcess().isActive()
         && !this.tickElement()
         && this.pointSample == WardenFarm.Mode.NONE) {
         long var1 = System.currentTimeMillis();
         Vec3d var3 = Module.client.player.getPos();
         if (this.taskLoad != null && !(var3.squaredDistanceTo(this.taskLoad) > 0.36)) {
            if (var1 - this.pointSend > 3500L) {
               this.sourceBuild.getPathingBehavior().cancelEverything();
               this.taskLoad = null;
            }
         } else {
            this.taskLoad = var3;
            this.pointSend = var1;
         }
      } else {
         this.taskLoad = null;
      }
   }

   private RotationAngles handle(RotationAngles var1, float var2) {
      double var3 = System.currentTimeMillis() / 1000.0;
      float var5 = (float)((Math.sin(var3 * 7.3 + this.cacheHandle2) * 0.62 + Math.sin(var3 * 13.7 + this.sessionAdvance) * 0.38) * var2);
      float var6 = (float)((Math.sin(var3 * 9.1 + this.sessionAdvance) * 0.55 + Math.sin(var3 * 15.9 + this.cacheHandle2) * 0.45) * var2 * 0.6);
      float var7 = Math.max(-90.0F, Math.min(90.0F, var1.data + var6));
      return new RotationAngles(var1.instance + var5, var7);
   }

   private RotationAngles compute(Vec3d var1) {
      if (Module.client.player == null) {
         return new RotationAngles(0.0F, 0.0F);
      }

      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      float var9 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var10 = (float)(-Math.toDegrees(Math.atan2(var5, Math.sqrt(var3 * var3 + var7 * var7))));
      return new RotationAngles(var9, var10);
   }

   private void applyState() {
      this.effectApply.handle();
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
   }

   private void handle(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         this.process(var1);
         if (!this.presetWrite.isEmpty()) {
            if (this.scaleAdapt.update(50L)) {
               this.presetWrite.poll().run();
               this.scaleAdapt.handle();
            }
         } else {
            this.filterMatrix();
            boolean var2 = false;
            int var3 = var1.slots.size() - 36;

            for (int var4 = 0; var4 < var3; var4++) {
               Slot var5 = (Slot)var1.slots.get(var4);
               if (var5.hasStack()) {
                  ItemStack var6 = var5.getStack();
                  if (this.execute(var6) && this.scaleAdapt.update(50L)) {
                     ItemStack var12 = var6.copy();
                     String var8 = this.apply(var12);
                     this.providerClose.put(var8, this.providerClose.getOrDefault(var8, 0) + var12.getCount());
                     this.inputHandle = true;
                     Module.client.interactionManager.clickSlot(var1.syncId, var4, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                     this.scaleAdapt.handle();
                     var2 = true;
                     return;
                  }

                  if (this.handle(var6) && this.scaleAdapt.update(50L)) {
                     int var7 = this.handle(this.keyCheck) - this.handlerRun;
                     if (this.handle(var1, var4, var7)) {
                        var2 = true;
                        return;
                     }
                  }
               }
            }

            if (!var2 && var3 > 0) {
               boolean var11 = this.handle(var1, var3);
               if (!var11) {
                  Module.client.player.closeHandledScreen();
                  this.applyState();
                  if (this.inputHandle) {
                     this.render(this.profileInvoke);
                  } else if (this.pathProject < 2 && this.profileInvoke != null) {
                     this.outputCollapse = WardenFarm.CachedMode.OPENING;
                     this.colorCompute.handle();
                  } else {
                     this.handle(30000L);
                  }
               }
            }
         }
      } else {
         this.applyState();
      }
   }

   private void filterMatrix() {
      if (this.profileInvoke != null) {
         if (!this.profileInvoke.equals(this.resourceClamp)) {
            this.resourceClamp = this.profileInvoke;
            this.handlerRun = 0;
            this.keyCheck = this.matchVector(this.profileInvoke);
         }
      }
   }

   private boolean handle(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = ((Slot)var1.slots.get(var3)).getStack();
         if (!var4.isEmpty() && (this.execute(var4) || this.handle(var4))) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(GenericContainerScreenHandler var1, int var2, int var3) {
      if (var3 <= 0) {
         return false;
      }

      Slot var4 = (Slot)var1.slots.get(var2);
      if (!var4.hasStack()) {
         return false;
      }

      int var5 = this.resolve(var1, var1.slots.size() - 36);
      if (var5 == -1) {
         return false;
      }

      int var6 = var4.getStack().getCount();
      if (var6 <= var3) {
         Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
      } else {
         Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);

         for (int var7 = 0; var7 < var3; var7++) {
            Module.client.interactionManager.clickSlot(var1.syncId, var5, 1, SlotActionType.PICKUP, Module.client.player);
         }

         Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
      }

      this.handlerRun = this.handlerRun + Math.min(var6, var3);
      this.scaleAdapt.handle();
      return true;
   }

   private void process(GenericContainerScreenHandler var1) {
      if (var1.syncId != this.vectorRun) {
         this.vectorRun = var1.syncId;
         this.scaleAdapt.handle();
         this.colorCompute.handle();
      }
   }

   private WardenFarm.SecondaryMode matchVector(BlockPos var1) {
      if (this.itemProject.compute() && var1 != null && Module.client.world != null) {
         String var2 = this.projectItem(var1).toLowerCase(Locale.ROOT);
         if (var2.contains("ресы") || var2.contains("ресурс")) {
            return WardenFarm.SecondaryMode.NONE;
         } else if (var2.contains("инвиз")) {
            return WardenFarm.SecondaryMode.INVIS;
         } else {
            return var2.contains("морков") ? WardenFarm.SecondaryMode.CARROT : WardenFarm.SecondaryMode.NONE;
         }
      } else {
         return WardenFarm.SecondaryMode.NONE;
      }
   }

   private String projectItem(BlockPos var1) {
      if (var1 != null && Module.client.world != null) {
         SignBlockEntity var2 = null;
         double var3 = Double.MAX_VALUE;
         BlockPos var5 = var1.add(-1, -1, -1);
         BlockPos var6 = var1.add(1, 1, 1);

         for (BlockPos var8 : BlockPos.iterate(var5, var6)) {
            if (Module.client.world.getBlockEntity(var8) instanceof SignBlockEntity var10) {
               double var11 = var8.getSquaredDistance(var1);
               if (var11 < var3) {
                  var3 = var11;
                  var2 = var10;
               }
            }
         }

         return var2 == null ? "" : this.handle(var2);
      } else {
         return "";
      }
   }

   private boolean handle(BlockPos var1, String[] var2, String... var3) {
      String var4 = this.projectItem(var1).toLowerCase(Locale.ROOT);
      if (var4.isEmpty()) {
         return false;
      }

      boolean var5 = false;

      for (String var9 : var2) {
         if (var4.contains(var9.toLowerCase(Locale.ROOT))) {
            var5 = true;
            break;
         }
      }

      if (!var5) {
         return false;
      }

      for (String var13 : var3) {
         if (var4.contains(var13.toLowerCase(Locale.ROOT))) {
            return false;
         }
      }

      return true;
   }

   private boolean computeResponse(BlockPos var1) {
      return var1 != null && this.handle(var1, playerEvaluate, "морков", "инвиз");
   }

   private boolean sampleLayer() {
      return this.readPacket() < 1 && !this.receiveEvent();
   }

   private boolean sendWorld() {
      return this.cancelRenderer() < 3;
   }

   private boolean writeTarget() {
      return this.responseCompute.compute() && !this.playerSave && this.blendMatrix2() < 1;
   }

   private boolean handle(String var1) {
      if (!var1.isEmpty() && !var1.contains("ресы") && !var1.contains("ресурс")) {
         for (String var5 : outputFetch) {
            if (var1.contains(var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean process(String var1) {
      if (!var1.contains("кит") && !var1.contains("kit") && !var1.contains("зель") && !var1.contains("припас")) {
         if (!this.sampleLayer() || !var1.contains("инвиз") && !var1.contains("invis")) {
            return !this.sendWorld() || !var1.contains("морков") && !var1.contains("carrot")
               ? this.writeTarget() && (var1.contains("скор") || var1.contains("speed") || var1.contains("инвиз") || var1.contains("invis"))
               : true;
         } else {
            return true;
         }
      } else {
         return this.sampleLayer() || this.sendWorld() || this.writeTarget();
      }
   }

   private BlockPos encodeResult() {
      if (Module.client.world != null && Module.client.player != null) {
         ChunkPos var1 = new ChunkPos(Module.client.player.getBlockPos());
         byte var2 = 10;
         BlockPos var3 = null;
         double var4 = Double.MAX_VALUE;

         for (int var6 = -var2; var6 <= var2; var6++) {
            for (int var7 = -var2; var7 <= var2; var7++) {
               WorldChunk var8 = Module.client.world.getChunk(var1.x + var6, var1.z + var7);
               if (var8 != null) {
                  for (BlockPos var10 : var8.getBlockEntities().keySet()) {
                     if (this.drawProfile(var10) && !this.dataRelease.contains(var10)) {
                        String var11 = this.projectItem(var10).toLowerCase(Locale.ROOT);
                        if (this.handle(var11) && this.process(var11)) {
                           double var12 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var10));
                           if (var12 < var4) {
                              var4 = var12;
                              var3 = var10;
                           }
                        }
                     }
                  }
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   private void fetchProvider(BlockPos var1) {
      if (var1 != null) {
         this.dataRelease.add(var1);

         for (BlockPos var5 : new BlockPos[]{var1.north(), var1.south(), var1.east(), var1.west()}) {
            if (Module.client.world.getBlockEntity(var5) instanceof ChestBlockEntity) {
               this.dataRelease.add(var5.toImmutable());
            }
         }
      }
   }

   private void parseMessage() {
      if (this.playerCollect != null && !this.computeResponse(this.playerCollect)) {
         this.playerCollect = null;
      }

      BlockPos var1 = this.readProvider();
      if (var1 != null) {
         this.playerCollect = var1;
      }
   }

   private boolean drawProfile(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      BlockEntity var2 = Module.client.world.getBlockEntity(var1);
      return var2 instanceof ChestBlockEntity || var2 instanceof BarrelBlockEntity || var2 instanceof ShulkerBoxBlockEntity;
   }

   private BlockPos readProvider() {
      return this.handle(playerEvaluate, "морков", "инвиз");
   }

   private BlockPos handle(String[] var1, String... var2) {
      if (Module.client.world != null && Module.client.player != null) {
         BlockPos var3 = Module.client.player.getBlockPos();
         ChunkPos var4 = new ChunkPos(var3);
         byte var5 = 10;
         BlockPos var6 = null;
         double var7 = Double.MAX_VALUE;

         for (int var9 = -var5; var9 <= var5; var9++) {
            for (int var10 = -var5; var10 <= var5; var10++) {
               WorldChunk var11 = Module.client.world.getChunk(var4.x + var9, var4.z + var10);
               if (var11 != null) {
                  for (BlockPos var13 : var11.getBlockEntities().keySet()) {
                     if (this.drawProfile(var13) && this.handle(var13, var1, var2)) {
                        double var14 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var13));
                        if (var14 < var7) {
                           var7 = var14;
                           var6 = var13;
                        }
                     }
                  }
               }
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   private String handle(SignBlockEntity var1) {
      StringBuilder var2 = new StringBuilder();

      for (Text var6 : var1.getFrontText().getMessages(false)) {
         var2.append(var6.getString()).append(' ');
      }

      for (Text var10 : var1.getBackText().getMessages(false)) {
         var2.append(var10.getString()).append(' ');
      }

      return var2.toString().replaceAll("§.", "").trim();
   }

   private int handle(WardenFarm.SecondaryMode var1) {
      return switch (var1) {
         case INVIS -> 1;
         case CARROT -> 3;
         default -> 0;
      };
   }

   private boolean handle(ItemStack var1) {
      if (this.itemProject.compute() && this.keyCheck != WardenFarm.SecondaryMode.NONE) {
         if (this.handlerRun >= this.handle(this.keyCheck)) {
            return false;
         }

         return switch (this.keyCheck) {
            case INVIS -> this.sampleLayer() && this.resolve(var1);
            case CARROT -> this.sendWorld() && this.refresh(var1);
            default -> false;
         };
      } else {
         return false;
      }
   }

   private boolean process(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      }

      String var2 = var1.getName().getString().toLowerCase(Locale.ROOT);
      if (!var2.contains("invis") && !var2.contains("невид")) {
         PotionContentsComponent var3 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var3 == null) {
            return false;
         }

         for (StatusEffectInstance var5 : var3.getEffects()) {
            if (var5.getEffectType().equals(StatusEffects.INVISIBILITY)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean compute(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      }

      String var2 = var1.getName().getString().toLowerCase(Locale.ROOT);
      if (!var2.contains("скорост") && !var2.contains("speed") && !var2.contains("swift")) {
         PotionContentsComponent var3 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var3 == null) {
            return false;
         }

         for (StatusEffectInstance var5 : var3.getEffects()) {
            if (var5.getEffectType().equals(StatusEffects.SPEED)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean resolve(ItemStack var1) {
      return !var1.isEmpty() && var1.isOf(Items.POTION) && this.process(var1) && !this.execute(var1);
   }

   private boolean update(ItemStack var1) {
      return !var1.isEmpty() && var1.isOf(Items.POTION) && this.compute(var1) && !this.execute(var1);
   }

   private int blendMatrix2() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.update(var3)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private void performScale() {
      if (this.providerFetch.compute() && this.convertAction()) {
         NicknameUtil.instance.handle();
         boolean var1 = this.vectorPerform.compute() && !NicknameUtil.instance.compute().equals(this.serverRead.compute());
         if (var1 && this.projectItem()) {
            this.sessionEncode = this::expandContext;
            this.fetchProvider();
         } else {
            this.expandContext();
         }
      } else {
         this.providerClose.clear();
         if (this.profileMatch) {
            this.profileMatch = false;
            this.submitPoint();
         }
      }
   }

   private void expandContext() {
      this.applyState();
      NicknameUtil.instance.handle();
      if (!this.convertAction()) {
         this.providerClose.clear();
         this.elementTick = false;
         this.profileMatch = false;
         this.sessionEncode = null;
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.configCollapse.handle();
      } else {
         this.playerCollect = null;
         this.stateApply = 0;
         this.matrixFilter = -1;
         this.parseMessage();
         if (this.vectorPerform.compute() && !NicknameUtil.instance.compute().equals(this.serverRead.compute())) {
            if (this.projectItem()) {
               this.elementTick = true;
               this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
               this.configCollapse.handle();
               return;
            }

            String var1 = NicknameUtil.instance.compute();
            if (!"N/A".equals(var1)) {
               this.vectorEncode = var1;
            }

            Module.client.player.networkHandler.sendChatCommand("an" + this.serverRead.compute());
            this.outputCollapse = WardenFarm.CachedMode.SWAPPING_TO_SAVE_ANARCHY;
            this.colorCompute.handle();
            this.textureRun.handle();
         } else {
            this.outputCollapse = WardenFarm.CachedMode.GOING_TO_STASH;
            this.colorCompute.handle();
            this.textureRun.handle();
         }
      }
   }

   private void compute(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         int var2 = var1.slots.size() - 36;
         int var3 = this.process(var1, var2);
         if (this.matrixFilter >= 0 && var3 >= this.matrixFilter) {
            if (this.keyFilter.update(6000L)) {
               this.readScreen();
               return;
            }
         } else {
            this.matrixFilter = var3;
            this.keyFilter.handle();
         }

         if (!this.presetWrite.isEmpty()) {
            if (this.scaleAdapt.update(50L)) {
               this.presetWrite.poll().run();
               this.scaleAdapt.handle();
            }
         } else {
            boolean var4 = false;

            for (int var5 = var2; var5 < var1.slots.size(); var5++) {
               Slot var6 = (Slot)var1.slots.get(var5);
               if (var6.hasStack()) {
                  ItemStack var7 = var6.getStack();
                  String var8 = this.apply(var7);
                  if (this.providerClose.getOrDefault(var8, 0) > 0 || this.execute(var7)) {
                     var4 = true;
                     int var9 = var5;
                     this.presetWrite.add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var9, 0, SlotActionType.QUICK_MOVE, Module.client.player));
                     this.providerClose.remove(var8);
                     return;
                  }
               }
            }

            if (!var4) {
               this.readScreen();
            }
         }
      }
   }

   private int process(GenericContainerScreenHandler var1, int var2) {
      int var3 = 0;

      for (int var4 = var2; var4 < var1.slots.size(); var4++) {
         Slot var5 = (Slot)var1.slots.get(var4);
         if (var5.hasStack()) {
            ItemStack var6 = var5.getStack();
            if (this.providerClose.getOrDefault(this.apply(var6), 0) > 0 || this.execute(var6)) {
               var3++;
            }
         }
      }

      return var3;
   }

   private boolean processKey() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.execute(Module.client.player.getInventory().getStack(var1))) {
            return true;
         }
      }

      return false;
   }

   private boolean convertAction() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && (this.execute(var2) || this.providerClose.getOrDefault(this.apply(var2), 0) > 0)) {
            return true;
         }
      }

      return false;
   }

   private void readScreen() {
      this.applyState();
      this.providerClose.clear();
      this.regionAlign.clear();
      this.presetWrite.clear();
      this.stateApply = 0;
      this.matrixFilter = -1;
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.configCollapse.handle();
      if (!this.profileMatch) {
         if (this.providerFetch.compute()
            && this.vectorPerform.compute()
            && !"N/A".equals(this.vectorEncode)
            && !this.vectorEncode.equals(this.serverRead.compute())) {
            this.process(() -> {
               this.requestAdapt = true;
               this.timerMeasure = System.currentTimeMillis() + 500L;
            });
         }
      } else {
         this.profileMatch = false;
         if ("N/A".equals(this.worldEvaluate) || this.worldEvaluate == null) {
            this.worldEvaluate = "N/A".equals(this.vectorEncode) ? this.matchConfig() : this.vectorEncode;
         }

         this.submitPoint();
      }
   }

   private String apply(ItemStack var1) {
      return var1.getItem().toString() + "|" + var1.getName().getString();
   }

   private boolean execute(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      }

      String var2 = var1.getName().getString();
      if (var2.contains("[★]")) {
         return true;
      }

      Item var3 = var1.getItem();
      if (this.target.process("Незер вещи") && this.process(var3)) {
         return true;
      }

      if (this.target.process("Дон зелья") && this.prepare(var1)) {
         return true;
      }

      if (this.target.process("Сферы") && this.check(var1)) {
         return true;
      }

      if (this.target.process("Талисманы") && this.onTick(var1)) {
         return true;
      }

      if (!this.target.process("Стрелы") || var3 != Items.ARROW && var3 != Items.TIPPED_ARROW && var3 != Items.SPECTRAL_ARROW) {
         if (this.target.process("Оружие") && this.handle(var3)) {
            return true;
         } else if (this.target.process("Броня") && AutoBuy.process(var3)) {
            return true;
         } else {
            return this.target.process("Яйца") && var3 instanceof SpawnEggItem ? true : this.target.process("Ценные предметы") && this.select(var1);
         }
      } else {
         return true;
      }
   }

   private boolean handle(Item var1) {
      return var1 == Items.WOODEN_SWORD
         || var1 == Items.STONE_SWORD
         || var1 == Items.IRON_SWORD
         || var1 == Items.GOLDEN_SWORD
         || var1 == Items.DIAMOND_SWORD
         || var1 == Items.NETHERITE_SWORD
         || var1 == Items.WOODEN_AXE
         || var1 == Items.STONE_AXE
         || var1 == Items.IRON_AXE
         || var1 == Items.GOLDEN_AXE
         || var1 == Items.DIAMOND_AXE
         || var1 == Items.NETHERITE_AXE
         || var1 == Items.TRIDENT
         || var1 == Items.MACE
         || var1 == Items.BOW
         || var1 == Items.CROSSBOW;
   }

   private boolean process(Item var1) {
      return var1 == Items.NETHERITE_HELMET
         || var1 == Items.NETHERITE_CHESTPLATE
         || var1 == Items.NETHERITE_LEGGINGS
         || var1 == Items.NETHERITE_BOOTS
         || var1 == Items.NETHERITE_SWORD
         || var1 == Items.NETHERITE_PICKAXE;
   }

   private boolean prepare(ItemStack var1) {
      return SpecialItemCatalog.save(var1)
         || SpecialItemCatalog.submit(var1)
         || SpecialItemCatalog.unload(var1)
         || SpecialItemCatalog.fetch(var1)
         || SpecialItemCatalog.measure(var1)
         || SpecialItemCatalog.blendMatrix(var1)
         || SpecialItemCatalog.matchVector(var1);
   }

   private boolean check(ItemStack var1) {
      return SpecialItemCatalog.handle(var1)
         || SpecialItemCatalog.process(var1)
         || SpecialItemCatalog.compute(var1)
         || SpecialItemCatalog.resolve(var1)
         || SpecialItemCatalog.update(var1)
         || SpecialItemCatalog.apply(var1)
         || SpecialItemCatalog.execute(var1)
         || SpecialItemCatalog.prepare(var1)
         || SpecialItemCatalog.check(var1);
   }

   private boolean onTick(ItemStack var1) {
      return SpecialItemCatalog.onTick(var1)
         || SpecialItemCatalog.select(var1)
         || SpecialItemCatalog.refresh(var1)
         || SpecialItemCatalog.render(var1)
         || SpecialItemCatalog.tick(var1)
         || SpecialItemCatalog.drawAnimation(var1)
         || SpecialItemCatalog.encodePoint(var1)
         || SpecialItemCatalog.animate(var1);
   }

   private boolean select(ItemStack var1) {
      Item var2 = var1.getItem();
      if (this.target.process("Незер вещи") && this.process(var2)) {
         return true;
      } else if (var2 instanceof BlockItem var3 && var3.getBlock() instanceof AbstractSkullBlock) {
         return true;
      } else if (var2 == Items.TOTEM_OF_UNDYING || var2 == Items.PAPER || var2 == Items.IRON_NUGGET || var2 == Items.TRIPWIRE_HOOK) {
         return true;
      } else if (var2 == Items.GUNPOWDER || var2 == Items.TNT || var2 == Items.NETHERITE_INGOT || var2 == Items.NETHER_STAR || var2 == Items.ENDER_EYE) {
         return true;
      } else if (var2 == Items.SNOWBALL || var2 == Items.SUGAR || var2 == Items.PHANTOM_MEMBRANE) {
         return true;
      } else {
         return var2 != Items.NETHERITE_SCRAP && var2 != Items.ELYTRA
            ? var2 == Items.CAMPFIRE
               || var2 == Items.SOUL_CAMPFIRE
               || var2 == Items.BEACON
               || var2 == Items.ENCHANTED_GOLDEN_APPLE
               || var2 == Items.GOLDEN_APPLE
               || var2 == Items.SPAWNER
            : true;
      }
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         Immediate var2 = WorldVertexBuffer.handle();

         try {
            Vec3d var3 = Module.client.gameRenderer.getCamera().getPos();
            Matrix4f var4 = var1.compute().peek().getPositionMatrix();
            VertexConsumer var5 = var2.getBuffer(pointSynchronize);
            if (this.providerFetch.compute() && this.profileDraw.process("Ресы") && this.playerCollect != null) {
               this.handle(var5, var4, this.playerCollect, var3, new Color(150, 50, 255, 120), new Color(150, 50, 255, 0));
            }

            if (this.entityFilter != null) {
               this.handle(var5, var4, this.entityFilter, var3, new Color(255, 220, 0, 140), new Color(255, 220, 0, 0));
            }

            if (!this.drawAnimation()) {
               return;
            }

            for (BlockPos var8 : this.windowConvert.stream().sorted(Comparator.comparingLong(this::handle)).limit(5L).collect(Collectors.toList())) {
               long var9 = this.handle(var8) - System.currentTimeMillis();
               Color var11;
               Color var12;
               if (var8.equals(this.profileInvoke)) {
                  float var13 = (float)(Math.sin(System.currentTimeMillis() / 60.0) * 0.5 + 0.5);
                  var11 = new Color(0, 150, 255, Math.min(255, (int)(80.0F + 150.0F * var13)));
                  var12 = new Color(0, 150, 255, 0);
               } else if (var9 <= 0L) {
                  var11 = new Color(0, 255, 150, 120);
                  var12 = new Color(0, 255, 150, 0);
               } else if (var9 <= 20000L) {
                  float var17 = (float)(Math.sin(System.currentTimeMillis() / 60.0) * 0.5 + 0.5);
                  var11 = new Color(255, 140, 0, Math.min(255, (int)(80.0F + 150.0F * var17)));
                  var12 = new Color(255, 140, 0, 0);
               } else {
                  var11 = new Color(255, 0, 0, 150);
                  var12 = new Color(255, 0, 0, 0);
               }

               this.handle(var5, var4, var8, var3, var11, var12);
            }
         } finally {
            WorldVertexBuffer.process();
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, BlockPos var3, Vec3d var4, Color var5, Color var6) {
      float var7 = (float)(var3.getX() - var4.x);
      float var8 = (float)(var3.getY() - var4.y);
      float var9 = (float)(var3.getZ() - var4.z);
      float var10 = (float)(var3.getX() + 1 - var4.x);
      float var11 = (float)(var3.getY() + 1 - var4.y);
      float var12 = (float)(var3.getZ() + 1 - var4.z);
      this.handle(var1, var2, var7, var8, var9, var10, var11, var12, var5, var6);
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, Color var9, Color var10) {
      int var11 = var9.getRed();
      int var12 = var9.getGreen();
      int var13 = var9.getBlue();
      int var14 = var9.getAlpha();
      int var15 = var10.getRed();
      int var16 = var10.getGreen();
      int var17 = var10.getBlue();
      int var18 = var10.getAlpha();
      var1.vertex(var2, var3, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var8).color(var11, var12, var13, var14);
      var1.vertex(var2, var6, var4, var5).color(var11, var12, var13, var14);
      var1.vertex(var2, var3, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var5).color(var15, var16, var17, var18);
      var1.vertex(var2, var6, var7, var8).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var7, var8).color(var15, var16, var17, var18);
   }

   private void expandAnimation() {
      this.optionFetch = WardenFarm.PersistentMode.NONE;
      this.performListener();
      this.playerProject = false;
      this.renderMatrix();
      this.saveLayout();
   }

   private void runPlayer() {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
      }

      this.expandAnimation();
      this.playerMatch = !this.drawAnimation();
      this.evaluatePlayer();
      this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
      this.profileInvoke = null;
      this.effectApply.handle();
      RotationController.instance = RotationController.Mode.IDLE;
      this.configCollapse.handle();
      this.colorCompute.handle();
      this.textureRun.handle();
   }

   private void renderMatrix() {
      this.cacheClose = false;
      this.scaleSetup = false;
      this.indexSynchronize = false;
      this.taskInterpolate = WardenFarm.PrimaryMode.NONE;
   }

   private boolean tickModule() {
      if (this.drawAnimation() || Module.client.player == null) {
         this.renderMatrix();
         return false;
      }

      if (this.taskInterpolate == WardenFarm.PrimaryMode.WAITING) {
         return true;
      }

      if (!this.cacheClose) {
         Module.client.player.networkHandler.sendChatCommand("home " + this.positionAdvance.compute().trim());
         this.cacheClose = true;
         this.taskInterpolate = WardenFarm.PrimaryMode.WAITING;
         this.scaleSetup = false;
         this.indexSynchronize = false;
         this.colorCompute.handle();
         this.textureRun.handle();
      }

      return true;
   }

   private boolean collapsePlayer() {
      if (this.taskInterpolate != WardenFarm.PrimaryMode.WAITING) {
         return false;
      }

      if (this.scaleSetup) {
         if (!this.indexSynchronize) {
            this.indexSynchronize = true;
         }

         if (this.textureRun.update(5000L)) {
            this.renderMatrix();
         }

         return true;
      } else {
         if (!this.drawAnimation() && !this.colorCompute.update(2500L)) {
            return true;
         }

         this.renderMatrix();
         return false;
      }
   }

   private boolean advanceOption() {
      if (!this.playerMatch || Module.client.player == null) {
         return false;
      }

      if (!this.drawAnimation() && this.vectorPerform.compute()) {
         NicknameUtil.instance.handle();
         String var1 = NicknameUtil.instance.compute();
         if (this.compute(var1)) {
            return this.taskInterpolate != WardenFarm.PrimaryMode.NONE || this.tickModule();
         }

         if (this.projectItem()) {
            return true;
         }

         if (this.colorCompute.update(3000L)) {
            String var2 = this.worldEvaluate;
            if (!this.compute(var2)) {
               var2 = this.matchConfig();
            }

            if (!this.compute(var2)) {
               this.playerMatch = false;
               return false;
            }

            Module.client.player.networkHandler.sendChatCommand("an" + var2);
            this.colorCompute.handle();
         }

         return true;
      } else {
         this.playerMatch = false;
         return false;
      }
   }

   private void scanEffect() {
      if (this.drawAnimation()) {
         this.optionFetch = WardenFarm.PersistentMode.USE_INVIS;
         this.colorCompute.handle();
      } else {
         if (this.tickModule()) {
            this.optionFetch = WardenFarm.PersistentMode.TELEPORT_WARDEN;
         } else {
            this.optionFetch = WardenFarm.PersistentMode.USE_INVIS;
            this.colorCompute.handle();
         }
      }
   }

   private void parseOption() {
      this.worldEvaluate = this.matchConfig();
      if (this.providerFetch.compute() && this.convertAction()) {
         this.profileMatch = true;
         this.performScale();
      } else {
         this.providerClose.clear();
         this.submitPoint();
      }
   }

   private void submitPoint() {
      if (this.itemProject.compute() && this.vectorPerform.compute()) {
         if (this.sourceBuild != null) {
            this.sourceBuild.getPathingBehavior().cancelEverything();
         }

         if (Module.client.player != null) {
            Module.client.player.closeHandledScreen();
         }

         if ("N/A".equals(this.worldEvaluate) || this.worldEvaluate == null) {
            this.worldEvaluate = this.matchConfig();
         }

         this.sourceRefresh = false;
         this.playerSave = false;
         this.requestRun = false;
         this.dataRelease.clear();
         this.performListener();
         this.optionFetch = WardenFarm.PersistentMode.SWAP_TO_BASE;
         this.outputCollapse = WardenFarm.CachedMode.SEARCHING;
         this.profileInvoke = null;
         this.colorCompute.handle();
         this.textureRun.handle();
      }
   }

   private void performListener() {
      this.eventCollapse = WardenFarm.FallbackMode.FIND;
      this.stateAttach = null;
      this.frameProject = 0;
      this.resourceClamp = null;
      this.handlerRun = 0;
      this.keyCheck = WardenFarm.SecondaryMode.NONE;
      this.presetWrite.clear();
      this.vectorRun = -1;
      this.providerSynchronize = WardenFarm.RuntimeMode.NONE;
      this.pathProcess = 0;
      this.positionReset = 0L;
   }

   private String matchConfig() {
      NicknameUtil.instance.handle();
      String var1 = NicknameUtil.instance.compute();
      if (this.compute(var1)) {
         return var1;
      }

      String[] var2 = this.eventAttach.compute().split(",");
      return var2.length > 0 && !var2[0].trim().isEmpty() ? var2[0].trim() : var1;
   }

   private boolean compute(String var1) {
      if (var1 != null && !"N/A".equals(var1) && !var1.equals(this.serverRead.compute())) {
         for (String var5 : this.eventAttach.compute().split(",")) {
            if (var5.trim().equals(var1)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean renderAction() {
      NicknameUtil.instance.handle();
      return NicknameUtil.instance.compute().equals(this.serverRead.compute());
   }

   private boolean applyPlayer() {
      return !this.compute(NicknameUtil.instance.compute())
         ? false
         : this.drawAnimation()
            || this.outputCollapse == WardenFarm.CachedMode.GOING_TO_CHEST
            || this.outputCollapse == WardenFarm.CachedMode.ROTATING
            || this.outputCollapse == WardenFarm.CachedMode.OPENING
            || this.outputCollapse == WardenFarm.CachedMode.WAITING_FOR_GUI;
   }

   private boolean adaptBuffer() {
      if (!this.itemProject.compute()) {
         return true;
      }

      boolean var1 = this.readPacket() >= 1 || this.receiveEvent() || this.requestRun;
      boolean var2 = this.cancelRenderer() >= 3 || this.sourceRefresh;
      return var1 && var2;
   }

   private boolean updatePlayer() {
      if (!this.itemProject.compute()) {
         return false;
      } else if (!this.receiveEvent() && this.readPacket() == 0 && !this.requestRun) {
         return true;
      } else {
         return this.responseCompute.compute() && !this.playerSave && !Module.client.player.hasStatusEffect(StatusEffects.SPEED) && this.blendMatrix2() == 0
            ? true
            : this.cancelRenderer() < 3 && !this.sourceRefresh;
      }
   }

   private int readPacket() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.resolve(var3)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private boolean refresh(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else if (!var1.isOf(Items.GOLDEN_CARROT) && !var1.isOf(Items.CARROT)) {
         String var2 = var1.getName().getString().toLowerCase(Locale.ROOT);
         return var2.contains("морков") || var2.contains("carrot");
      } else {
         return true;
      }
   }

   private int cancelRenderer() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.refresh(var3)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private boolean receiveEvent() {
      return Module.client.player != null && Module.client.player.hasStatusEffect(StatusEffects.INVISIBILITY);
   }

   private boolean resolve(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT).replaceAll("§.", "");
      return var2.contains("не найден")
         || var2.contains("не существует")
         || var2.contains("нет дома")
         || var2.contains("нет точки")
         || var2.contains("not found")
         || var2.contains("unknown home")
         || var2.contains("home") && var2.contains("нет");
   }

   private void submitScreen() {
      if (Module.client.player != null && this.sourceBuild != null) {
         NicknameUtil.instance.handle();
         switch (this.optionFetch) {
            case SWAP_TO_BASE:
               if (this.renderAction()) {
                  this.optionFetch = WardenFarm.PersistentMode.COLLECT_KIT;
                  this.performListener();
                  this.colorCompute.handle();
                  this.textureRun.handle();
               } else if (this.projectItem()) {
                  this.textureRun.handle();
               } else if (this.colorCompute.update(700L)) {
                  Module.client.player.networkHandler.sendChatCommand("an" + this.serverRead.compute());
                  this.optionFetch = WardenFarm.PersistentMode.WAIT_BASE;
                  this.colorCompute.handle();
                  this.textureRun.handle();
               }
               break;
            case WAIT_BASE:
               if (this.renderAction()) {
                  this.optionFetch = WardenFarm.PersistentMode.COLLECT_KIT;
                  this.performListener();
                  this.colorCompute.handle();
                  this.textureRun.handle();
               } else if (this.textureRun.update(20000L)) {
                  this.runPlayer();
               }
               break;
            case COLLECT_KIT:
               this.handleCache();
               break;
            case SWAP_TO_FARM:
               if (this.adaptBuffer()) {
                  String var1 = this.worldEvaluate;
                  if ("N/A".equals(var1) || var1 == null) {
                     var1 = this.matchConfig();
                  }

                  if (NicknameUtil.instance.compute().equals(var1)) {
                     this.scanEffect();
                  } else if (this.projectItem()) {
                     this.textureRun.handle();
                  } else if (this.colorCompute.update(700L)) {
                     Module.client.player.networkHandler.sendChatCommand("an" + var1);
                     this.optionFetch = WardenFarm.PersistentMode.WAIT_FARM;
                     this.colorCompute.handle();
                     this.textureRun.handle();
                  }
               } else if (this.textureRun.update(15000L)) {
                  this.runPlayer();
               } else {
                  this.optionFetch = WardenFarm.PersistentMode.COLLECT_KIT;
                  this.performListener();
               }
               break;
            case WAIT_FARM:
               if (this.compute(NicknameUtil.instance.compute())) {
                  this.scanEffect();
                  this.textureRun.handle();
               } else if (this.textureRun.update(20000L)) {
                  this.runPlayer();
               }
               break;
            case TELEPORT_WARDEN:
               if (this.collapsePlayer()) {
                  return;
               }

               this.optionFetch = WardenFarm.PersistentMode.USE_INVIS;
               this.colorCompute.handle();
               break;
            case USE_INVIS:
               if (!this.receiveEvent()) {
                  if (this.pointSample == WardenFarm.Mode.NONE) {
                     if (this.saveIndex() == -1) {
                        if (this.requestRun) {
                           this.runPlayer();
                        } else {
                           this.submitPoint();
                        }

                        return;
                     }

                     this.handle(WardenFarm.Mode.DRINK_INVIS);
                  } else {
                     this.loadIndex();
                  }

                  return;
               }

               this.runPlayer();
         }
      }
   }

   private void handleCache() {
      if (this.adaptBuffer() && !this.writeTarget()) {
         this.performListener();
         this.optionFetch = WardenFarm.PersistentMode.SWAP_TO_FARM;
         this.colorCompute.handle();
         this.textureRun.handle();
      } else {
         switch (this.eventCollapse) {
            case FIND:
               this.stateAttach = this.encodeResult();
               if (this.stateAttach == null) {
                  if (this.textureRun.update(1500L)) {
                     this.sourceRefresh = this.cancelRenderer() < 3;
                     this.playerSave = this.responseCompute.compute() && this.blendMatrix2() < 1;
                     if (!this.receiveEvent() && this.readPacket() < 1) {
                        this.requestRun = true;
                     }

                     this.performListener();
                     this.optionFetch = WardenFarm.PersistentMode.SWAP_TO_FARM;
                     this.colorCompute.handle();
                     this.textureRun.handle();
                  }

                  return;
               }

               this.eventCollapse = WardenFarm.FallbackMode.GOING;
               this.colorCompute.handle();
               this.textureRun.handle();
               break;
            case GOING:
               double var4 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.stateAttach));
               if (var4 <= 2.9 && this.unload(this.stateAttach)) {
                  this.sourceBuild.getPathingBehavior().cancelEverything();
                  this.eventCollapse = WardenFarm.FallbackMode.ROTATING;
                  this.colorCompute.handle();
               } else if (this.textureRun.update(15000L)) {
                  this.fetchProvider(this.stateAttach);
                  this.stateAttach = null;
                  this.eventCollapse = WardenFarm.FallbackMode.FIND;
                  this.textureRun.handle();
               } else if (!this.sourceBuild.getCustomGoalProcess().isActive() || this.actionRead.update(2500L)) {
                  this.sourceBuild.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.stateAttach, 1));
                  this.actionRead.handle();
               }
               break;
            case ROTATING:
               if (!this.measure(this.stateAttach)) {
                  this.eventCollapse = WardenFarm.FallbackMode.GOING;
                  this.colorCompute.handle();
                  this.textureRun.handle();
                  return;
               }

               RotationAngles var3 = this.compute(this.fetch(this.stateAttach));
               this.effectApply.handle(this.handle(var3, this.handle(var3)), 35.0F, 35.0F, 35.0F, 35.0F, 20, 1);
               if (this.blendMatrix(this.stateAttach) != null && this.colorCompute.update(100L)) {
                  this.eventCollapse = WardenFarm.FallbackMode.OPENING;
                  this.colorCompute.handle();
               }
               break;
            case OPENING:
               if (!this.measure(this.stateAttach)) {
                  this.eventCollapse = WardenFarm.FallbackMode.GOING;
                  this.colorCompute.handle();
                  this.textureRun.handle();
                  return;
               }

               RotationAngles var1 = this.compute(this.fetch(this.stateAttach));
               this.effectApply.handle(this.handle(var1, 0.6F), 18.0F, 18.0F, 20.0F, 20.0F, 20, 1);
               BlockHitResult var2 = this.blendMatrix(this.stateAttach);
               if (var2 == null) {
                  if (this.colorCompute.update(1200L)) {
                     this.eventCollapse = WardenFarm.FallbackMode.ROTATING;
                     this.colorCompute.handle();
                  }

                  return;
               }

               if (System.currentTimeMillis() - this.clientSubmit < 400L) {
                  return;
               }

               if (this.colorCompute.update(100L)) {
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var2);
                  RotationController.instance = RotationController.Mode.IDLE;
                  this.eventCollapse = WardenFarm.FallbackMode.WAIT_GUI;
                  this.colorCompute.handle();
               }
               break;
            case WAIT_GUI:
               if (this.colorCompute.update(1500L)) {
                  this.frameProject++;
                  if (this.frameProject >= 4) {
                     this.frameProject = 0;
                     this.stateAttach = null;
                     this.eventCollapse = WardenFarm.FallbackMode.FIND;
                  } else if (this.measure(this.stateAttach)) {
                     this.eventCollapse = WardenFarm.FallbackMode.OPENING;
                  } else {
                     this.eventCollapse = WardenFarm.FallbackMode.GOING;
                  }

                  this.colorCompute.handle();
                  this.textureRun.handle();
               }
         }
      }
   }

   private boolean handle(GenericContainerScreen var1) {
      String var2 = var1.getTitle().getString().toLowerCase(Locale.ROOT).replaceAll("§.", "").trim();
      return var2.contains("сундук") || var2.contains("chest") || var2.contains("бочка") || var2.contains("barrel") || var2.contains("шалкер");
   }

   private void resolve(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null && this.stateAttach != null) {
         this.process(var1);
         if (!this.presetWrite.isEmpty()) {
            if (this.scaleAdapt.update(60L)) {
               this.presetWrite.poll().run();
               this.scaleAdapt.handle();
            }
         } else {
            if (this.providerSynchronize != WardenFarm.RuntimeMode.NONE) {
               if (this.handle(this.providerSynchronize) >= this.pathProcess) {
                  this.providerSynchronize = WardenFarm.RuntimeMode.NONE;
                  this.pathProcess = 0;
                  this.positionReset = 0L;
                  this.colorCompute.handle();
               } else {
                  if (System.currentTimeMillis() - this.positionReset < 1200L) {
                     return;
                  }

                  this.providerSynchronize = WardenFarm.RuntimeMode.NONE;
                  this.pathProcess = 0;
                  this.positionReset = 0L;
               }
            }

            int var2 = var1.slots.size() - 36;
            int var3 = this.sampleLayer() ? 1 - this.readPacket() : 0;
            int var4 = this.writeTarget() && !Module.client.player.hasStatusEffect(StatusEffects.SPEED) ? 1 - this.blendMatrix2() : 0;
            int var5 = this.sendWorld() && !this.sourceRefresh ? 3 - this.cancelRenderer() : 0;
            if (var3 <= 0 && var4 <= 0 && var5 <= 0) {
               this.sourceRefresh = false;
               this.playerSave = false;
               this.requestRun = false;
               Module.client.player.closeHandledScreen();
               this.performListener();
               this.optionFetch = WardenFarm.PersistentMode.SWAP_TO_FARM;
               this.colorCompute.handle();
               this.textureRun.handle();
            } else if (!this.handle(var1, var2, this::resolve, var3, WardenFarm.RuntimeMode.INVIS)
               && !this.handle(var1, var2, this::update, var4, WardenFarm.RuntimeMode.SPEED)
               && !this.handle(var1, var2, this::refresh, var5, WardenFarm.RuntimeMode.CARROT)) {
               long var6 = this.compute(var1, var2) ? 350L : 1500L;
               if (this.colorCompute.update(var6)) {
                  this.fetchProvider(this.stateAttach);
                  Module.client.player.closeHandledScreen();
                  this.clientSubmit = System.currentTimeMillis();
                  this.performListener();
                  this.colorCompute.handle();
                  this.textureRun.handle();
               }
            } else {
               this.colorCompute.handle();
            }
         }
      }
   }

   private boolean handle(GenericContainerScreenHandler var1, int var2, Predicate<ItemStack> var3, int var4, WardenFarm.RuntimeMode var5) {
      if (var4 <= 0) {
         return false;
      }

      for (int var6 = 0; var6 < var2; var6++) {
         Slot var7 = (Slot)var1.slots.get(var6);
         if (var7.hasStack() && var3.test(var7.getStack())) {
            int var8 = this.handle(var1, var2, var3, var4);
            if (var8 == -1) {
               return false;
            }

            int var9 = var6;
            int var10 = Math.min(var7.getStack().getCount(), var4);
            this.providerSynchronize = var5;
            this.pathProcess = this.handle(var5) + var10;
            this.positionReset = System.currentTimeMillis();
            if (var7.getStack().getCount() <= var4) {
               this.presetWrite.add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var9, 0, SlotActionType.QUICK_MOVE, Module.client.player));
               return true;
            }

            this.presetWrite.add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var9, 0, SlotActionType.PICKUP, Module.client.player));

            for (int var11 = 0; var11 < var10; var11++) {
               this.presetWrite.add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var8, 1, SlotActionType.PICKUP, Module.client.player));
            }

            this.presetWrite.add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var9, 0, SlotActionType.PICKUP, Module.client.player));
            return true;
         }
      }

      return false;
   }

   private int handle(WardenFarm.RuntimeMode var1) {
      return switch (var1) {
         case INVIS -> this.readPacket();
         case SPEED -> this.blendMatrix2();
         case CARROT -> this.cancelRenderer();
         default -> 0;
      };
   }

   private int handle(GenericContainerScreenHandler var1, int var2, Predicate<ItemStack> var3, int var4) {
      for (int var5 = var2; var5 < var1.slots.size(); var5++) {
         ItemStack var6 = ((Slot)var1.slots.get(var5)).getStack();
         if (!var6.isEmpty() && var3.test(var6) && var6.getCount() + var4 <= var6.getMaxCount()) {
            return var5;
         }
      }

      return this.resolve(var1, var2);
   }

   private boolean compute(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         if (((Slot)var1.slots.get(var3)).hasStack()) {
            return true;
         }
      }

      return false;
   }

   private int resolve(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         if (!((Slot)var1.slots.get(var3)).hasStack()) {
            return var3;
         }
      }

      return -1;
   }

   private void releaseRange() {
      if (!this.itemProject.compute() || this.optionFetch != WardenFarm.PersistentMode.NONE || !this.encodeSession()) {
         this.saveLayout();
      } else if (this.outputCollapse == WardenFarm.CachedMode.SWAPPING_TO_SAVE_ANARCHY
         || this.outputCollapse == WardenFarm.CachedMode.GOING_TO_STASH
         || this.outputCollapse == WardenFarm.CachedMode.ROTATING_STASH
         || this.outputCollapse == WardenFarm.CachedMode.OPENING_STASH_BLOCK
         || this.outputCollapse == WardenFarm.CachedMode.WAITING_FOR_GUI_STASH
         || this.outputCollapse == WardenFarm.CachedMode.STORING_IN_CHEST
         || this.outputCollapse == WardenFarm.CachedMode.OPENING_STASH) {
         this.saveLayout();
      } else if (this.pointSample != WardenFarm.Mode.NONE) {
         this.loadIndex();
      } else if (Module.client.player.getHungerManager().getFoodLevel() <= 16 && this.acquireInput() != -1) {
         this.handle(WardenFarm.Mode.EAT_CARROT);
      } else if (this.responseCompute.compute() && !Module.client.player.hasStatusEffect(StatusEffects.SPEED)) {
         if (this.scheduleSetting() != -1) {
            this.handle(WardenFarm.Mode.DRINK_SPEED);
         }
      } else {
         if (!this.receiveEvent()) {
            if (this.saveIndex() != -1) {
               this.handle(WardenFarm.Mode.DRINK_INVIS);
            } else if (this.updatePlayer()) {
               this.parseOption();
            }
         }
      }
   }

   private int saveIndex() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.resolve(Module.client.player.getInventory().getStack(var1))) {
            return var1;
         }
      }

      return -1;
   }

   private int checkIndex() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isOf(Items.GOLDEN_CARROT)) {
            return var1;
         }
      }

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var3);
         if (this.refresh(var2)) {
            return var3;
         }
      }

      return -1;
   }

   private int scheduleSetting() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (this.update(var2) && !this.process(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private boolean render(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else if (this.refresh(var1)) {
         return true;
      } else {
         return var1.get(DataComponentTypes.FOOD) == null ? false : !this.execute(var1);
      }
   }

   private int acquireInput() {
      int var1 = this.checkIndex();
      if (var1 != -1) {
         return var1;
      }

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.render(var3)) {
            return var2;
         }
      }

      return -1;
   }

   private void handle(WardenFarm.Mode var1) {
      int var2 = switch (var1) {
         case DRINK_INVIS -> this.saveIndex();
         case EAT_CARROT -> this.acquireInput();
         case DRINK_SPEED -> this.scheduleSetting();
         default -> -1;
      };
      if (var2 != -1) {
         this.pathCheck = Module.client.player.getInventory().getSelectedSlot();
         this.regionRefresh = var2;
         this.pointSample = var1;
         this.contextParse.handle();
         this.clientDraw = false;
         this.providerMatch = false;
         this.handle(var2);
         if (var2 <= 8) {
            this.runListener();
         } else {
            Module.client.options.useKey.setPressed(true);
         }
      }
   }

   private void runListener() {
      Module.client.options.useKey.setPressed(true);
      if (!(Module.client.currentScreen instanceof ChatScreen) && !this.parseScale()) {
         if (Module.client.interactionManager != null) {
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
         }
      } else if (!this.clientDraw && Module.client.getNetworkHandler() != null) {
         Module.client.getNetworkHandler()
            .sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, Module.client.player.getYaw(), Module.client.player.getPitch()));
         this.clientDraw = true;
      }

      this.providerMatch = true;
   }

   private void loadIndex() {
      ItemStack var1 = Module.client.player.getMainHandStack();

      boolean var2 = switch (this.pointSample) {
         case DRINK_INVIS -> this.resolve(var1);
         case EAT_CARROT -> this.render(var1);
         case DRINK_SPEED -> this.update(var1);
         default -> false;
      };
      if (!var2) {
         if (this.contextParse.apply() < 300L && !this.parseScale()) {
            Module.client.options.useKey.setPressed(true);
         } else {
            this.saveLayout();
         }
      } else {
         if (!this.providerMatch) {
            this.runListener();
         } else if (!this.parseScale()) {
            Module.client.options.useKey.setPressed(true);
         }
         boolean var3 = switch (this.pointSample) {
            case DRINK_INVIS -> this.receiveEvent();
            case EAT_CARROT -> Module.client.player.getHungerManager().getFoodLevel() > 16;
            case DRINK_SPEED -> Module.client.player.hasStatusEffect(StatusEffects.SPEED);
            default -> true;
         };
         if (var3 || this.contextParse.update(4500L)) {
            this.saveLayout();
         }
      }
   }

   private void saveLayout() {
      Module.client.options.useKey.setPressed(false);
      if (Module.client.player != null && this.regionRefresh != -1 && this.pathCheck != -1) {
         Module.client.player.getInventory().setSelectedSlot(this.pathCheck);
      }

      this.pointSample = WardenFarm.Mode.NONE;
      this.regionRefresh = -1;
      this.pathCheck = -1;
      this.clientDraw = false;
      this.providerMatch = false;
   }

   private void runBlock() {
      if (!this.worldDispatch) {
         SyntheticKeyState.handle().handle("WardenFarmInvMove");
         this.worldDispatch = true;
      }

      this.optionAdvance2.handle();
   }

   private void evaluatePlayer() {
      if (this.worldDispatch) {
         SyntheticKeyState.handle().process("WardenFarmInvMove");
         this.worldDispatch = false;
      }
   }

   private void fetchOutput() {
      if (this.worldDispatch && this.optionAdvance2.update(200L)) {
         this.evaluatePlayer();
      }
   }

   private boolean parseScale() {
      return Module.client.currentScreen instanceof GameMenuScreen;
   }

   private boolean encodeSession() {
      return Module.client.currentScreen == null || Module.client.currentScreen instanceof ChatScreen || Module.client.currentScreen instanceof GameMenuScreen;
   }

   private boolean tickElement() {
      return !this.encodeSession();
   }

   private boolean alignRegion() {
      if (!this.summary.compute() || Module.client.player == null || Module.client.interactionManager == null) {
         this.fetchOutput();
         return false;
      }

      if (Module.client.player.isDead() || this.tickElement() || this.pointSample != WardenFarm.Mode.NONE) {
         this.fetchOutput();
         return false;
      }

      if (this.blendMatrix()
         || this.outputCollapse == WardenFarm.CachedMode.ROTATING_STASH
         || this.outputCollapse == WardenFarm.CachedMode.OPENING_STASH_BLOCK
         || this.outputCollapse == WardenFarm.CachedMode.WAITING_FOR_GUI_STASH
         || this.outputCollapse == WardenFarm.CachedMode.STORING_IN_CHEST
         || this.outputCollapse == WardenFarm.CachedMode.OPENING_STASH) {
         this.fetchOutput();
         return false;
      }

      if (this.optionFetch != WardenFarm.PersistentMode.NONE
         && this.eventCollapse != WardenFarm.FallbackMode.FIND
         && this.eventCollapse != WardenFarm.FallbackMode.GOING) {
         this.fetchOutput();
         return false;
      }

      int var1 = this.clampResource();
      if (var1 == -1) {
         this.fetchOutput();
         return false;
      }

      int var2 = this.runHandler();
      if (var2 == -1) {
         this.fetchOutput();
         return false;
      }

      if (this.sourceBuild != null) {
         this.sourceBuild.getPathingBehavior().cancelEverything();
      }

      this.runBlock();
      if (this.optionStop.update(150L)) {
         Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var2, var1, SlotActionType.SWAP, Module.client.player);
         this.optionStop.handle();
      }

      return true;
   }

   private int clampResource() {
      for (int var1 = 0; var1 <= 8; var1++) {
         if (!Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private int runHandler() {
      for (int var1 = 9; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private void handle(int var1) {
      if (var1 <= 8) {
         Module.client.player.getInventory().setSelectedSlot(var1);
         this.regionRefresh = var1;
      } else {
         int var2 = this.checkKey();
         if (var2 == -1) {
            var2 = this.pathCheck;
         }

         Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var1, var2, SlotActionType.SWAP, Module.client.player);
         Module.client.player.getInventory().setSelectedSlot(var2);
         this.regionRefresh = var2;
      }
   }

   private int checkKey() {
      for (int var1 = 0; var1 <= 8; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   enum CachedMode {
      SEARCHING,
      GOING_TO_CHEST,
      ROTATING,
      OPENING,
      WAITING_FOR_GUI,
      RETREATING,
      GOING_TO_DEATH_LOOT,
      COLLECTING_DEATH_LOOT,
      HUB_WAITING_FOR_CHEST,
      SWAPPING_TO_SAVE_ANARCHY,
      GOING_TO_STASH,
      OPENING_STASH,
      ROTATING_STASH,
      OPENING_STASH_BLOCK,
      WAITING_FOR_GUI_STASH,
      STORING_IN_CHEST;
   }

   enum FallbackMode {
      FIND,
      GOING,
      ROTATING,
      OPENING,
      WAIT_GUI;
   }

   enum Mode {
      NONE,
      DRINK_INVIS,
      EAT_CARROT,
      DRINK_SPEED;
   }

   enum PersistentMode {
      NONE,
      SWAP_TO_BASE,
      WAIT_BASE,
      COLLECT_KIT,
      SWAP_TO_FARM,
      WAIT_FARM,
      TELEPORT_WARDEN,
      USE_INVIS;
   }

   enum PrimaryMode {
      NONE,
      WAITING;
   }

   enum RuntimeMode {
      NONE,
      INVIS,
      SPEED,
      CARROT;
   }

   enum SecondaryMode {
      NONE,
      INVIS,
      CARROT;
   }
}
