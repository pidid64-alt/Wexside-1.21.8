package ru.wild.modules.misc;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.wild.mixin.acceser.ClientPlayerInteractionManagerAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MouseClickContextEvent;
import ru.wild.api.event.MouseUpdateEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.automation.RotationPlayback;
import ru.wild.command.GpsCommand;
import ru.wild.core.ModuleStateHelper;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.movement.Sprint;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.inventory.HolyWorldHelper;
import ru.wild.util.inventory.ItemStackOverlayRenderer;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.player.SyntheticKeyState;
import ru.wild.util.render.FarmAreaRenderer;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "ServerHelper", description = "Позволяет юзать предметы по бинду", category = ModuleCategory.Misc)
public class ServerHelper extends Module {
   public static ServerHelper source;
   private static final String indexBind = "Клавиша трапки";
   private static final String actionRead = "Клавиша трапки [FunTime]";
   private static final String configCollapse = "Клавиша трапки [HolyWorld]";
   private static final String dataValidate = "Клавиша снежка заморозки";
   private static final String scaleRender = "Клавиша снежка заморозки [FunTime]";
   private static final String clientRefresh = "Клавиша снежка заморозки [HolyWorld]";
   public final ModeSetting target = new ModeSetting("Режим работы", "FunTime", "FunTime", "HolyWorld");
   public final ModeSetting pending = new ModeSetting("Определение предмета", "По атрибуту", "По атрибуту", "По названию")
      .handle(() -> !this.target.process("FunTime"));
   public final ChoiceSetting previous = new ChoiceSetting(
      "Дополнительные настройки",
      new BooleanSetting("Стопы", true),
      new BooleanSetting("Рендерить границы", true),
      new BooleanSetting("Рендерить границы сквозь стены", false),
      new BooleanSetting("Авто GPS на ивенты", true)
   );
   public final KeybindSetting latest = new KeybindSetting("Клавиша дезориентации", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting summary = new KeybindSetting("Клавиша явной пыли", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting matrixBlend = new KeybindSetting("Клавиша божьей ауры", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting vectorMatch = new KeybindSetting("Клавиша пласта", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting itemProject = new KeybindSetting("Клавиша трапки", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting responseCompute = new KeybindSetting("Клавиша снежка заморозки", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting providerFetch = new KeybindSetting("Клавиша зелья ассасина", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting profileDraw = new KeybindSetting("Клавиша зелья паладина", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting vectorPerform = new KeybindSetting("Клавиша зелья снотворного", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting eventAttach = new KeybindSetting("Клавиша зелья гнева", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting serverRead = new KeybindSetting("Клавиша зелья святая вода", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting positionAdvance = new KeybindSetting("Клавиша зелья радиации", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting frameCheck = new KeybindSetting("Клавиша зелья хлопушки", -1, true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting moduleCollect = new KeybindSetting("Меню дон-зелий", -1).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting providerClose = new KeybindSetting("Клавиша трапки", -1, true).handle(() -> !this.target.process("HolyWorld"));
   public final KeybindSetting presetSave = new KeybindSetting("Клавиша снежка заморозки", -1, true).handle(() -> !this.target.process("HolyWorld"));
   public final KeybindSetting windowConvert = new KeybindSetting("Клавиша стана", -1, true).handle(() -> !this.target.process("HolyWorld"));
   public final KeybindSetting presetWrite = new KeybindSetting("Клавиша взрывной трапки", -1, true).handle(() -> !this.target.process("HolyWorld"));
   public final KeybindSetting colorMeasure = new KeybindSetting("Клавиша шалкера", -1, false);
   public final KeybindSetting animationSchedule = new KeybindSetting("Клавиша воздухана", -1, false);
   public final BooleanSetting rendererScan = new BooleanSetting("Кидать под себя", false);
   public final BooleanSetting sourceBuild = new BooleanSetting("Проекция Мега-бульдозера", true).handle(() -> !this.target.process("FunTime"));
   public final KeybindSetting outputCollapse = new KeybindSetting("Хорус", -1, false);
   public final BooleanSetting profileInvoke = new BooleanSetting("Таймеры структур", false).handle(() -> !this.target.process("FunTime"));
   public final ModeSetting sourceSchedule = new ModeSetting("Тип ивента", "Фантайм", "Фантайм", "Спуки тайм")
      .handle(() -> !this.target.process("FunTime") || !this.profileInvoke.compute());
   public final BooleanSetting timerRender = new BooleanSetting("Превью", false).handle(() -> !this.target.process("FunTime") || !this.profileInvoke.compute());
   public final BooleanSetting scaleSave = new BooleanSetting("Лог звуков (дебаг)", false)
      .handle(() -> !this.target.process("FunTime") || !this.profileInvoke.compute());
   public final BooleanSetting colorCompute = new BooleanSetting("Лог блока (дебаг)", false)
      .handle(() -> !this.target.process("FunTime") || !this.profileInvoke.compute());
   public final BooleanSetting scaleAdapt = new BooleanSetting("Swap Debug", false);
   private long keyFilter = 0L;
   private long requestAdapt = 0L;
   private static final long timerMeasure = 150L;
   private static final long vectorEncode = 5000L;
   private static final double requestReceive = 0.25;
   private static final long windowProcess = 0L;
   private static final long packetSave = 800L;
   private static final int entryAnimate = 3;
   private static final long playerCollect = 150L;
   private static final int stateApply = 3;
   private static final int matrixFilter = 1;
   private static final float layerSample = 90.0F;
   private static final float worldSend = 180.0F;
   private static final float targetWrite = 180.0F;
   private static final int resultEncode = 1;
   private static final int messageParse = 30;
   private static final int providerRead = 15;
   private static final double matrixBlend2 = 0.28;
   private static final int scalePerform = 7;
   private static final float contextExpand = 0.12F;
   private static final long keyProcess = 800L;
   private static final int actionConvert = 2;
   private final Queue<ServerHelper.CacheEntry> screenRead = new ArrayDeque<>();
   private ServerHelper.CacheEntry animationExpand = null;
   private ServerHelper.DataRecord playerRun = null;
   private long matrixRender = 0L;
   private boolean moduleTick = false;
   private int playerCollapse = 0;
   private int optionAdvance = -1;
   private final float[] effectScan = new float[7];
   private final ItemStack[] optionParse = new ItemStack[7];
   private final int[] pointSubmit = new int[7];
   private final boolean[] listenerPerform = new boolean[7];
   private final int[] configMatch = new int[7];
   private final float[] actionRender = new float[7];
   private final float[] playerApply = new float[7];
   private final float[] bufferAdapt = new float[7];
   private final float[] playerUpdate = new float[7];
   private final List<Predicate<ItemStack>> packetRead = new ArrayList<>(7);
   private String rendererCancel = "";
   private int eventReceive = 0;
   private int screenSubmit = 0;
   private int cacheHandle = -1;
   private int rangeRelease = -1;
   public static boolean textureRun = false;
   private static final DoubleAnimator indexSave = new DoubleAnimator();
   private static final DoubleAnimator indexCheck = new DoubleAnimator();
   private static final DoubleAnimator settingSchedule = new DoubleAnimator();
   private static final DoubleAnimator inputAcquire = new DoubleAnimator();
   private static boolean listenerRun;
   private float indexLoad = 100.0F;
   private float layoutSave = 100.0F;
   private ServerHelper.Mode blockRun = ServerHelper.Mode.IDLE;
   private final RotationPlayback playerEvaluate = new RotationPlayback();
   private final Stopwatch outputFetch = new Stopwatch();
   private final Stopwatch scaleParse = new Stopwatch();
   private long sessionEncode = 0L;
   private long elementTick = 0L;
   private int regionAlign = -1;
   private int resourceClamp = -1;
   private boolean handlerRun = false;
   private int keyCheck = -1;
   private int layerProject = -1;
   private Item entityFilter = null;
   private int layerSample2 = 0;
   private final Stopwatch sourceCancel = new Stopwatch();
   private final Stopwatch eventSend = new Stopwatch();
   private boolean providerOffset = false;
   private boolean messageParse2 = false;
   private boolean shaderProject = false;
   private boolean inputInvoke = false;
   private boolean optionFetch = false;
   private int eventCollapse = -1;
   private int stateAttach = 0;
   private float worldEvaluate;
   private float playerProject;
   private float playerMatch;
   private float cacheClose;
   private long scaleSetup;
   private long indexSynchronize;
   private boolean taskInterpolate = false;
   private boolean sourceRefresh = false;
   private int playerSave = 0;
   private boolean requestRun = false;
   private boolean frameProject = false;
   private Vec3d dataRelease = Vec3d.ZERO;
   private ServerHelper.SecondaryDataRecord vectorRun;
   private static final long providerSynchronize = 15000L;
   private static final long pathProcess = 20000L;
   private static final long positionReset = 60000L;
   private static final long effectApply = 30000L;
   private static final long cacheHandle2 = 20000L;
   private static final String sessionAdvance = "block.piston.extend";
   private static final String keySample = "block.anvil.place";
   private static final String playerPerform = "entity.ender_dragon.growl";
   private static final long taskLoad = 250L;
   private static final double pointSend = 16.0;
   private static final long clientSubmit = 180L;
   private static final long pointSample = 1500L;
   private static final int regionRefresh = 128;
   private static final ServerHelper.State[] pathCheck = new ServerHelper.State[]{
      new ServerHelper.State(
         "Драконий скин",
         30000L,
         0L,
         new ServerHelper.PrimaryCacheEntry("entity.wither.break_block", 1.0F, 0.7F),
         new ServerHelper.PrimaryCacheEntry("entity.ender_dragon.growl", 1.5F, 0.2F),
         new ServerHelper.PrimaryCacheEntry("ui.toast.challenge_complete", 1.5F, 0.35F),
         new ServerHelper.PrimaryCacheEntry("entity.evoker_fangs.attack", 0.85F, 0.5F)
      )
   };
   private static ItemStack contextParse;
   private static ItemStack optionStop;
   private static ItemStack mousePrepare;
   private static ItemStack sessionAdapt;
   private static final ServerHelper.PrimaryMode[] profileMatch = new ServerHelper.PrimaryMode[]{
      ServerHelper.PrimaryMode.TRAPKA, ServerHelper.PrimaryMode.PLAST, ServerHelper.PrimaryMode.DRAGON_TRAP, ServerHelper.PrimaryMode.DRAGON_PLAST
   };
   private static final int[][] entityAdvance = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
   private final List<ServerHelper.FallbackCacheEntry> sessionCollect = new ArrayList<>();
   private final ConcurrentLinkedDeque<ServerHelper.SecondaryCacheEntry> inputAttach = new ConcurrentLinkedDeque<>();
   private int pathProject = -1;
   private String inputHandle = "";
   private static final Set<Block> worldDispatch = Set.of(
      Blocks.GRASS_BLOCK,
      Blocks.DIRT,
      Blocks.COARSE_DIRT,
      Blocks.PODZOL,
      Blocks.ROOTED_DIRT,
      Blocks.MUD,
      Blocks.MYCELIUM,
      Blocks.MOSS_BLOCK,
      Blocks.DIRT_PATH,
      Blocks.FARMLAND,
      Blocks.STONE,
      Blocks.GRANITE,
      Blocks.DIORITE,
      Blocks.ANDESITE,
      Blocks.DEEPSLATE,
      Blocks.COBBLED_DEEPSLATE,
      Blocks.TUFF,
      Blocks.CALCITE,
      Blocks.COBBLESTONE,
      Blocks.MOSSY_COBBLESTONE,
      Blocks.GRAVEL,
      Blocks.SAND,
      Blocks.RED_SAND,
      Blocks.SANDSTONE,
      Blocks.CLAY,
      Blocks.BEDROCK,
      Blocks.DEAD_TUBE_CORAL_BLOCK,
      Blocks.SNOW_BLOCK,
      Blocks.ICE,
      Blocks.PACKED_ICE,
      Blocks.BLUE_ICE,
      Blocks.MAGMA_BLOCK,
      Blocks.NETHERRACK
   );
   private static final Pattern optionAdvance2 = Pattern.compile("координатах\\s+(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");
   private static final int clientDraw = 1024;
   private static final RenderPipeline providerMatch = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "helper_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer profileWrite = RenderLayer.of("helper_box", 1024, false, true, providerMatch, MultiPhaseParameters.builder().build(false));
   private static final RenderPipeline effectMatch = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "helper_lines"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer cacheExecute = RenderLayer.of(
      "helper_lines", 1024, false, true, effectMatch, MultiPhaseParameters.builder().lineWidth(new LineWidth(OptionalDouble.of(10.0))).build(false)
   );
   private static final RenderPipeline pointSynchronize = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "helper_box_no_depth"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer sessionBind = RenderLayer.of(
      "helper_box_no_depth", 1024, false, true, pointSynchronize, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderPipeline indexCancel = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "helper_lines_no_depth"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer elementStop = RenderLayer.of(
      "helper_lines_no_depth", 1024, false, true, indexCancel, MultiPhaseParameters.builder().lineWidth(new LineWidth(OptionalDouble.of(10.0))).build(false)
   );

   public ServerHelper() {
      source = this;
      this.itemProject.handle("Клавиша трапки [FunTime]");
      this.providerClose.handle("Клавиша трапки [HolyWorld]");
      this.responseCompute.handle("Клавиша снежка заморозки [FunTime]");
      this.presetSave.handle("Клавиша снежка заморозки [HolyWorld]");
      this.handle(
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
         this.positionAdvance,
         this.frameCheck,
         this.moduleCollect,
         this.windowConvert,
         this.providerClose,
         this.presetSave,
         this.presetWrite,
         this.colorMeasure,
         this.animationSchedule,
         this.rendererScan,
         this.sourceBuild,
         this.scaleAdapt,
         this.outputCollapse,
         this.profileInvoke,
         this.sourceSchedule
      );
   }

   @Override
   public void handle(JsonObject var1) {
      JsonObject var2 = var1 == null ? null : var1.deepCopy();
      if (var2 != null && var2.has("Settings")) {
         try {
            JsonObject var3 = var2.getAsJsonObject("Settings");
            if (var3.has("Клавиша трапки")) {
               int var4 = var3.get("Клавиша трапки").getAsInt();
               if (!var3.has("Клавиша трапки [FunTime]")) {
                  var3.addProperty("Клавиша трапки [FunTime]", var4);
               }

               if (!var3.has("Клавиша трапки [HolyWorld]")) {
                  var3.addProperty("Клавиша трапки [HolyWorld]", var4);
               }
            }

            if (var3.has("Клавиша снежка заморозки")) {
               int var6 = var3.get("Клавиша снежка заморозки").getAsInt();
               if (!var3.has("Клавиша снежка заморозки [FunTime]")) {
                  var3.addProperty("Клавиша снежка заморозки [FunTime]", var6);
               }

               if (!var3.has("Клавиша снежка заморозки [HolyWorld]")) {
                  var3.addProperty("Клавиша снежка заморозки [HolyWorld]", var6);
               }
            }
         } catch (Throwable var5) {
         }
      }

      super.handle(var2);
   }

   private boolean handle(KeybindSetting var1, InputButtonEvent var2) {
      if (var1.compute() != -1 && var2.resolve() == var1.compute()) {
         return var1.cache ? var2.apply() == 0 : var2.apply() == 1;
      } else {
         return false;
      }
   }

   private boolean handle(ItemStack var1, String var2) {
      return var1.getName().getString().toLowerCase(Locale.ROOT).contains(var2.toLowerCase(Locale.ROOT));
   }

   private boolean handle(ItemStack var1, String... var2) {
      for (String var6 : var2) {
         if (this.handle(var1, var6)) {
            return true;
         }
      }

      return false;
   }

   public Predicate<ItemStack> handle(Predicate<ItemStack> var1, String... var2) {
      return this.pending.process("По названию") ? var2x -> this.handle(var2x, var2) : var1;
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (this.optionFetch) {
         if (Module.client.player != null && Module.client.world != null && this.target.process("FunTime") && this.moduleCollect.compute() != -1) {
            if (!this.update(this.moduleCollect.compute())) {
               this.compute(true);
            }
         } else {
            this.compute(false);
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (Module.client.currentScreen == null) {
         if (this.target.process("FunTime") && var1.resolve() == this.moduleCollect.compute() && this.moduleCollect.compute() != -1) {
            if (var1.apply() == 1 && !this.optionFetch) {
               this.refresh();
               var1.process();
            } else if (var1.apply() == 0 && this.optionFetch) {
               this.compute(true);
               var1.process();
            }
         } else {
            ServerHelper.CacheEntry var2 = null;
            if (this.target.process("FunTime")) {
               if (this.handle(this.latest, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::computeResponse, new String[]{"Дезориентация"}), false);
               } else if (this.handle(this.summary, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::projectItem, new String[]{"Явная пыль"}), false);
               } else if (this.handle(this.vectorMatch, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::performVector, new String[]{"Пласт"}), false);
               } else if (this.handle(this.matrixBlend, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::saveScale, new String[]{"Божья аура"}), false);
               } else if (this.handle(this.itemProject, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::fetchProvider, new String[]{"Трапка"}), false);
               } else if (this.handle(this.responseCompute, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::renderTimer, new String[]{"Снежок заморозка"}), false);
               } else if (this.handle(this.providerFetch, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::save, new String[]{"Зелье Ассасина"}), false);
               } else if (this.handle(this.profileDraw, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::measure, "Зелье Паладина", "Зелье Палладина"), false);
               } else if (this.handle(this.vectorPerform, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::matchVector, new String[]{"Снотворное"}), false);
               } else if (this.handle(this.eventAttach, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::submit, new String[]{"Зелье Гнева"}), false);
               } else if (this.handle(this.serverRead, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::fetch, new String[]{"Святая вода"}), false);
               } else if (this.handle(this.positionAdvance, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::blendMatrix, new String[]{"Зелье Радиации"}), false);
               } else if (this.handle(this.frameCheck, var1)) {
                  var2 = new ServerHelper.CacheEntry(this.handle(SpecialItemCatalog::unload, new String[]{"Хлопушка"}), false);
               }
            }

            if (this.target.process("HolyWorld")) {
               if (this.handle(this.providerClose, var1)) {
                  var2 = new ServerHelper.CacheEntry(HolyWorldHelper::handle, false);
               } else if (this.handle(this.presetSave, var1)) {
                  var2 = new ServerHelper.CacheEntry(HolyWorldHelper::process, false);
               } else if (this.handle(this.windowConvert, var1)) {
                  var2 = new ServerHelper.CacheEntry(HolyWorldHelper::compute, false);
               } else if (this.handle(this.presetWrite, var1)) {
                  var2 = new ServerHelper.CacheEntry(HolyWorldHelper::resolve, false);
               }
            }

            if (this.handle(this.colorMeasure, var1)) {
               var2 = new ServerHelper.CacheEntry(var0 -> var0.getItem().toString().contains("shulker_box"), true);
            }

            if (this.handle(this.animationSchedule, var1)) {
               var2 = new ServerHelper.CacheEntry(var0 -> var0.isOf(Items.WIND_CHARGE), false, false, this.rendererScan.compute());
            }

            if (this.handle(this.outputCollapse, var1)) {
               var2 = new ServerHelper.CacheEntry(var0 -> var0.isOf(Items.CHORUS_FRUIT), false, true);
            }

            if (var2 != null && System.currentTimeMillis() - this.requestAdapt >= 150L) {
               this.requestAdapt = System.currentTimeMillis();
               this.screenRead.add(var2);
            }
         }
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      int var2 = -100 - var1.resolve();
      if (this.target.process("FunTime") && this.moduleCollect.compute() == var2) {
         if (var1.onTick() && !this.optionFetch) {
            this.handle((float)var1.execute(), (float)var1.prepare());
            this.refresh();
            var1.process();
            return;
         }

         if (var1.select() && this.optionFetch) {
            this.handle((float)var1.execute(), (float)var1.prepare());
            this.compute(true);
            var1.process();
            return;
         }
      }

      if (this.optionFetch) {
         this.handle((float)var1.execute(), (float)var1.prepare());
         var1.process();
      }
   }

   @EventHandler
   public void handle(MouseUpdateEvent var1) {
      if (this.optionFetch) {
         var1.process();
      }
   }

   @EventHandler
   public void handle(MouseClickContextEvent var1) {
      if (this.optionFetch) {
         var1.process();
      }
   }

   private void refresh() {
      if (Module.client.player != null && Module.client.world != null && Module.client.currentScreen == null && Module.client.getWindow() != null) {
         this.optionFetch = true;
         this.eventCollapse = -1;
         this.scaleSetup = this.indexSynchronize = System.nanoTime();
         Arrays.fill(this.effectScan, 0.0F);
         this.animate();
         this.playerMatch = this.worldEvaluate;
         this.cacheClose = this.playerProject;
         this.computeColor();
         if (Module.client.mouse != null) {
            Module.client.mouse.unlockCursor();
         }

         this.load();
      }
   }

   private void compute(boolean var1) {
      if (this.optionFetch) {
         this.encodePoint();
         this.computeColor();
         Window var2 = Module.client.getWindow();
         int var3 = var2 == null ? -1 : this.handle(this.worldEvaluate, this.playerProject, var2.getFramebufferWidth(), var2.getFramebufferHeight());
         this.optionFetch = false;
         this.eventCollapse = -1;
         if (var1 && var3 >= 0) {
            this.handle(var3);
         }

         if (Module.client.currentScreen == null && Module.client.mouse != null) {
            Module.client.mouse.lockCursor();
         }
      }
   }

   private void handle(int var1) {
      ServerHelper.CacheEntry var2 = this.process(var1);
      ServerHelper.DataRecord var3 = this.compute(var1);
      if (var2 != null && !var3.stack().isEmpty() && System.currentTimeMillis() - this.requestAdapt >= 150L) {
         this.requestAdapt = System.currentTimeMillis();
         if (!var3.isBundled()) {
            this.screenRead.add(var2);
         } else if (this.blockRun == ServerHelper.Mode.IDLE) {
            this.handle(var2, var3);
         }
      }
   }

   private boolean handle(ServerHelper.CacheEntry var1, ServerHelper.DataRecord var2) {
      if (Module.client.player != null && Module.client.interactionManager != null && this.blockRun == ServerHelper.Mode.IDLE) {
         this.animationExpand = var1;
         this.playerRun = var2;
         this.matrixRender = System.currentTimeMillis() + 800L;
         this.moduleTick = false;
         this.playerCollapse = 0;
         this.sessionEncode = System.nanoTime();
         this.blockRun = ServerHelper.Mode.EXTRACT;
         textureRun = true;
         this.submit();
         if (this.save()) {
            ChatLogger.handle("§8[§eSwapDebug§8] §fbundle extract start");
         }

         return true;
      } else {
         return false;
      }
   }

   private void render() {
      if (this.animationExpand != null && this.playerRun != null && Module.client.player != null) {
         if (System.currentTimeMillis() > this.matrixRender) {
            this.tick();
         } else if (!this.moduleTick) {
            if (!this.handle(this.playerRun, this.animationExpand)) {
               this.tick();
            } else {
               this.moduleTick = true;
               this.playerCollapse = 0;
            }
         } else if (this.playerCollapse++ >= 2) {
            int var1 = this.optionAdvance;
            if (var1 >= 0 && var1 < 36) {
               ItemStack var2 = Module.client.player.getInventory().getStack(var1);
               if (!var2.isEmpty() && this.animationExpand.instance.test(var2)) {
                  this.handle("bundle extract", this.sessionEncode);
                  ServerHelper.CacheEntry var6 = this.animationExpand;
                  this.animationExpand = null;
                  this.playerRun = null;
                  this.moduleTick = false;
                  this.optionAdvance = -1;
                  this.regionAlign = var1;
                  this.resourceClamp = Module.client.player.getInventory().getSelectedSlot();
                  this.providerOffset = var6.data;
                  this.messageParse2 = var6.context;
                  this.shaderProject = var6.config;
                  this.inputInvoke = var1 >= 9 && !var6.data;
                  if (this.inputInvoke) {
                     this.keyCheck = this.resourceClamp;
                     this.layerProject = var1;
                     this.entityFilter = Module.client.player.getInventory().getStack(this.resourceClamp).getItem();
                  }

                  this.taskInterpolate = false;
                  this.sourceRefresh = false;
                  this.playerSave = 0;
                  this.requestRun = false;
                  this.frameProject = false;
                  this.dataRelease = Module.client.player.getPos();
                  this.screenSubmit = 0;
                  this.outputFetch.handle();
                  this.scaleParse.handle();
                  this.elementTick = 0L;
                  this.blockRun = ServerHelper.Mode.PREPARE;
                  return;
               }
            }

            for (int var5 = 0; var5 < 36; var5++) {
               ItemStack var3 = Module.client.player.getInventory().getStack(var5);
               if (!var3.isEmpty() && this.animationExpand.instance.test(var3)) {
                  this.handle("bundle extract", this.sessionEncode);
                  ServerHelper.CacheEntry var4 = this.animationExpand;
                  this.animationExpand = null;
                  this.playerRun = null;
                  this.moduleTick = false;
                  this.optionAdvance = -1;
                  this.regionAlign = var5;
                  this.resourceClamp = Module.client.player.getInventory().getSelectedSlot();
                  this.providerOffset = var4.data;
                  this.messageParse2 = var4.context;
                  this.shaderProject = var4.config;
                  this.inputInvoke = var5 >= 9 && !var4.data;
                  if (this.inputInvoke) {
                     this.keyCheck = this.resourceClamp;
                     this.layerProject = var5;
                     this.entityFilter = Module.client.player.getInventory().getStack(this.resourceClamp).getItem();
                  }

                  this.taskInterpolate = false;
                  this.sourceRefresh = false;
                  this.playerSave = 0;
                  this.requestRun = false;
                  this.frameProject = false;
                  this.dataRelease = Module.client.player.getPos();
                  this.screenSubmit = 0;
                  this.outputFetch.handle();
                  this.scaleParse.handle();
                  this.elementTick = 0L;
                  this.blockRun = ServerHelper.Mode.PREPARE;
                  return;
               }
            }
         }
      } else {
         this.tick();
      }
   }

   private void tick() {
      this.animationExpand = null;
      this.playerRun = null;
      this.moduleTick = false;
      this.optionAdvance = -1;
      if (this.blockRun == ServerHelper.Mode.EXTRACT) {
         this.blockRun = ServerHelper.Mode.IDLE;
         textureRun = false;
         this.unload();
      }
   }

   private ServerHelper.CacheEntry process(int var1) {
      return switch (var1) {
         case 0 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::save, "Зелье Ассасина"), false);
         case 1 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::measure, "Зелье Паладина", "Зелье Палладина"), false);
         case 2 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::matchVector, "Снотворное"), false);
         case 3 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::submit, "Зелье Гнева"), false);
         case 4 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::fetch, "Святая вода"), false);
         case 5 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::blendMatrix, "Зелье Радиации"), false);
         case 6 -> new ServerHelper.CacheEntry(this.process(SpecialItemCatalog::unload, "Хлопушка"), false);
         default -> null;
      };
   }

   private Predicate<ItemStack> process(Predicate<ItemStack> var1, String... var2) {
      Predicate var3 = this.handle(var1, var2);
      return var1x -> var1x.isOf(Items.SPLASH_POTION) && var3.test(var1x);
   }

   private ServerHelper.DataRecord compute(int var1) {
      if (Module.client.player == null) {
         return new ServerHelper.DataRecord(ItemStack.EMPTY, -1, -1);
      }

      ServerHelper.CacheEntry var2 = this.process(var1);
      if (var2 == null) {
         return new ServerHelper.DataRecord(ItemStack.EMPTY, -1, -1);
      }

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && var2.instance.test(var4)) {
            return new ServerHelper.DataRecord(var4, var3, -1);
         }
      }

      return this.handle(var2.instance);
   }

   private ServerHelper.DataRecord handle(Predicate<ItemStack> var1) {
      if (Module.client.player == null) {
         return new ServerHelper.DataRecord(ItemStack.EMPTY, -1, -1);
      }

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.getItem() instanceof BundleItem) {
            BundleContentsComponent var4 = (BundleContentsComponent)var3.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (var4 != null) {
               for (int var5 = 0; var5 < var4.size(); var5++) {
                  ItemStack var6 = var4.get(var5);
                  if (!var6.isEmpty() && var1.test(var6)) {
                     return new ServerHelper.DataRecord(var6, var2, var5);
                  }
               }
            }
         }
      }

      return new ServerHelper.DataRecord(ItemStack.EMPTY, -1, -1);
   }

   private boolean handle(ServerHelper.DataRecord var1, ServerHelper.CacheEntry var2) {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.playerScreenHandler.getCursorStack().isEmpty()) {
         int var3 = this.drawAnimation();
         if (var3 == -1) {
            ChatLogger.handle("Для зелья из мешочка нужен свободный слот инвентаря");
            return false;
         } else {
            ItemStack var4 = Module.client.player.getInventory().getStack(var1.inventorySlot());
            BundleContentsComponent var5 = (BundleContentsComponent)var4.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (var4.getItem() instanceof BundleItem
               && var5 != null
               && var1.bundleStackIndex() < var5.size()
               && var2.instance.test(var5.get(var1.bundleStackIndex()))) {
               int var6 = this.resolve(var1.inventorySlot());
               int var7 = this.resolve(var3);
               BundleItem.setSelectedStackIndex(var4, var1.bundleStackIndex());
               Module.client.player.networkHandler.sendPacket(new BundleItemSelectedC2SPacket(var6, var1.bundleStackIndex()));
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var6, 1, SlotActionType.PICKUP, Module.client.player);
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var7, 0, SlotActionType.PICKUP, Module.client.player);
               this.optionAdvance = var3;
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private int drawAnimation() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private int resolve(int var1) {
      return var1 < 9 ? 36 + var1 : var1;
   }

   private void encodePoint() {
      if (Module.client.getWindow() != null) {
         double[] var1 = new double[1];
         double[] var2 = new double[1];
         GLFW.glfwGetCursorPos(Module.client.getWindow().getHandle(), var1, var2);
         this.handle((float)var1[0], (float)var2[0]);
      }
   }

   private void animate() {
      Window var1 = Module.client.getWindow();
      if (var1 != null && !var1.hasZeroWidthOrHeight()) {
         this.worldEvaluate = var1.getFramebufferWidth() * 0.5F;
         this.playerProject = var1.getFramebufferHeight() * 0.5F;
      }
   }

   private void load() {
      Window var1 = Module.client.getWindow();
      if (var1 != null && !var1.hasZeroWidthOrHeight()) {
         GLFW.glfwSetCursorPos(var1.getHandle(), var1.getWidth() * 0.5, var1.getHeight() * 0.5);
      }
   }

   private void handle(float var1, float var2) {
      if (Float.isFinite(var1) && Float.isFinite(var2)) {
         Window var3 = Module.client.getWindow();
         if (var3 != null
            && !var3.hasZeroWidthOrHeight()
            && var3.getFramebufferWidth() > 0
            && var3.getFramebufferHeight() > 0
            && var3.getWidth() > 0
            && var3.getHeight() > 0) {
            this.worldEvaluate = MathHelper.clamp(
               (float)((double)(var1 * var3.getFramebufferWidth()) / var3.getWidth()), 0.0F, Math.max(0.0F, var3.getFramebufferWidth() - 1.0F)
            );
            this.playerProject = MathHelper.clamp(
               (float)((double)(var2 * var3.getFramebufferHeight()) / var3.getHeight()), 0.0F, Math.max(0.0F, var3.getFramebufferHeight() - 1.0F)
            );
         } else {
            this.worldEvaluate = var1;
            this.playerProject = var2;
         }
      }
   }

   private boolean update(int var1) {
      if (Module.client.getWindow() == null) {
         return false;
      }

      long var2 = Module.client.getWindow().getHandle();
      if (var1 >= 0) {
         return InputUtil.isKeyPressed(var2, var1);
      }

      if (var1 > -100) {
         return false;
      }

      int var4 = -var1 - 100;
      return var4 >= 0 && var4 <= 7 && GLFW.glfwGetMouseButton(var2, var4) == 1;
   }

   @EventHandler
   private void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         this.scheduleSource();
         this.attachEvent();
         if (this.blockRun == ServerHelper.Mode.IDLE) {
            if (!this.screenRead.isEmpty()) {
               ServerHelper.CacheEntry var6 = this.screenRead.poll();
               int var3 = -1;

               for (int var4 = 0; var4 < 36; var4++) {
                  ItemStack var5 = Module.client.player.getInventory().getStack(var4);
                  if (var5 != null && !var5.isEmpty() && var6.instance.test(var5)) {
                     var3 = var4;
                     break;
                  }
               }

               if (var3 == -1) {
                  ServerHelper.DataRecord var7 = this.handle(var6.instance);
                  if (var7.isBundled()) {
                     this.handle(var6, var7);
                  }
               }

               if (var3 == -1) {
                  return;
               }

               this.regionAlign = var3;
               this.resourceClamp = Module.client.player.getInventory().getSelectedSlot();
               this.providerOffset = var6.data;
               this.messageParse2 = var6.context;
               this.shaderProject = var6.config;
               this.inputInvoke = var3 >= 9 && !var6.data;
               if (this.inputInvoke) {
                  this.keyCheck = this.resourceClamp;
                  this.layerProject = var3;
                  this.entityFilter = Module.client.player.getInventory().getStack(this.resourceClamp).getItem();
               }

               this.taskInterpolate = false;
               this.sourceRefresh = false;
               this.playerSave = 0;
               this.requestRun = false;
               this.frameProject = false;
               this.dataRelease = Module.client.player.getPos();
               this.screenSubmit = 0;
               this.outputFetch.handle();
               this.scaleParse.handle();
               this.sessionEncode = System.nanoTime();
               this.elementTick = 0L;
               this.blockRun = ServerHelper.Mode.PREPARE;
               textureRun = true;
               if (this.fetch()) {
                  this.elementTick = System.nanoTime();
                  this.computeResponse();
               } else {
                  this.submit();
               }
            }
         } else {
            if (!this.fetch()) {
               this.submit();
               Sprint.latest = 2;
               Module.client.options.sprintKey.setPressed(false);
               Module.client.player.setSprinting(false);
            }

            switch (this.blockRun) {
               case EXTRACT:
                  this.render();
                  return;
               case PREPARE:
                  if (this.providerOffset) {
                     if (this.outputFetch.handle(0L)) {
                        this.outputFetch.handle();
                        int var2 = this.regionAlign < 9 ? 36 + this.regionAlign : this.regionAlign;
                        Module.client.interactionManager
                           .clickSlot(Module.client.player.playerScreenHandler.syncId, var2, 1, SlotActionType.PICKUP, Module.client.player);
                        this.blockRun = ServerHelper.Mode.COOLDOWN;
                     }
                  } else if (this.fetch()) {
                     if (this.inputInvoke && !this.messageParse2) {
                        this.submit();
                        Sprint.latest = Math.max(Sprint.latest, 2);
                        Module.client.options.sprintKey.setPressed(false);
                        Module.client.player.setSprinting(false);
                        this.fetchProvider();
                        this.readServer();
                        this.blockRun = ServerHelper.Mode.PRE_RESTORE_STOP;
                        this.elementTick = System.nanoTime();
                        this.matchVector();
                        this.computeResponse();
                     } else {
                        this.measure();
                     }
                  } else {
                     this.blockRun = ServerHelper.Mode.SWAP;
                  }
                  break;
               case PRE_SWAP_STOP:
                  this.computeResponse();
                  if (!this.projectItem()) {
                     return;
                  }

                  this.handle("stop->swap " + (System.nanoTime() - this.elementTick) / 1000000L + "ms", this.elementTick);
                  this.fetchProvider();
                  if (this.messageParse2 && !this.scheduleAnimation()) {
                     this.collapseOutput();
                     return;
                  }

                  if (this.messageParse2) {
                     if (this.regionAlign < 9 && this.scheduleAnimation()) {
                        this.savePreset();
                     } else {
                        this.screenSubmit = 0;
                        this.blockRun = ServerHelper.Mode.WAIT_MAIN_HAND;
                     }
                  } else {
                     this.readServer();
                     this.blendMatrix();
                  }
                  break;
               case WAIT_MAIN_HAND:
                  this.computeResponse();
                  if (this.messageParse2 && this.scheduleAnimation()) {
                     this.savePreset();
                     return;
                  }

                  if (this.screenSubmit++ >= 3) {
                     this.collapseOutput();
                  }
                  break;
               case SWAP:
                  if (this.regionAlign >= 9) {
                     this.measure();
                     return;
                  }

                  Module.client.player.getInventory().setSelectedSlot(this.regionAlign);
                  ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
                  if (this.messageParse2) {
                     this.savePreset();
                     this.blockRun = ServerHelper.Mode.USE;
                  } else {
                     this.readServer();
                     this.blockRun = ServerHelper.Mode.RESTORE;
                  }
                  break;
               case USE:
                  if (this.messageParse2) {
                     if (this.sourceRefresh) {
                        this.convertWindow();
                     } else {
                        this.savePreset();
                     }
                  } else {
                     this.blockRun = ServerHelper.Mode.RESTORE;
                  }
                  break;
               case PRE_RESTORE_STOP:
                  this.computeResponse();
                  if (!this.projectItem()) {
                     return;
                  }

                  this.handle("stop->restore " + (System.nanoTime() - this.elementTick) / 1000000L + "ms", this.elementTick);
                  this.drawProfile();
                  this.blockRun = ServerHelper.Mode.COOLDOWN;
                  break;
               case RESTORE:
                  if (this.messageParse2) {
                     this.scanRenderer();
                  }

                  if (this.regionAlign >= 9) {
                     this.blendMatrix();
                     return;
                  }

                  Module.client.player.getInventory().setSelectedSlot(this.resourceClamp);
                  ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
                  this.blockRun = ServerHelper.Mode.COOLDOWN;
                  break;
               case COOLDOWN:
                  if (!this.providerOffset && this.regionAlign >= 9) {
                     Module.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
                     Module.client.player.closeHandledScreen();
                  }

                  if (this.previous.process("Стопы")) {
                     SyntheticKeyState.handle().process("ServerHelper_Lock");
                  }

                  this.collectModule();
                  this.handle("full swap", this.sessionEncode);
                  this.sessionEncode = 0L;
                  this.elementTick = 0L;
                  if (this.inputInvoke) {
                     this.handlerRun = true;
                     this.layerSample2 = 3;
                     this.sourceCancel.handle();
                     this.eventSend.handle();
                  }

                  this.blockRun = ServerHelper.Mode.IDLE;
                  textureRun = false;
                  this.providerOffset = false;
                  this.messageParse2 = false;
                  this.shaderProject = false;
                  this.inputInvoke = false;
                  this.sourceRefresh = false;
                  this.playerSave = 0;
            }
         }
      }
   }

   private boolean save() {
      if (this.scaleAdapt != null && this.scaleAdapt.compute()) {
         return true;
      }

      if (Module.client.player == null) {
         return false;
      }

      String var1 = Module.client.player.getName().getString();
      return "lichoday".equalsIgnoreCase(var1);
   }

   private void handle(String var1, long var2) {
      if (this.save() && var2 != 0L) {
         double var4 = (System.nanoTime() - var2) / 1.0E9;
         ChatLogger.handle("§8[§eSwapDebug§8] §f" + var1 + " §7" + String.format(Locale.ROOT, "%.3f", var4) + "s §8(stop 0ms, bundle 800ms/2t)");
      }
   }

   private void submit() {
      if (this.previous.process("Стопы")) {
         if (this.messageParse2 || this.regionAlign < 0 || this.regionAlign >= 9) {
            SyntheticKeyState.handle().handle("ServerHelper_Lock");
         }
      }
   }

   private void unload() {
      if (this.previous.process("Стопы")) {
         SyntheticKeyState.handle().process("ServerHelper_Lock");
      }
   }

   private boolean fetch() {
      return this.messageParse2 || this.inputInvoke;
   }

   private void measure() {
      this.scanRenderer();
      this.blockRun = ServerHelper.Mode.PRE_SWAP_STOP;
      this.matchVector();
      this.elementTick = System.nanoTime();
      this.computeResponse();
   }

   private void blendMatrix() {
      this.scanRenderer();
      this.blockRun = ServerHelper.Mode.PRE_RESTORE_STOP;
      this.matchVector();
      this.elementTick = System.nanoTime();
      this.computeResponse();
   }

   private void matchVector() {
      this.scaleParse.handle();
   }

   private boolean projectItem() {
      long var1 = this.shaderProject ? 100L : 0L;
      return this.scaleParse.prepare(var1);
   }

   private void computeResponse() {
      this.submit();
      Sprint.latest = Math.max(Sprint.latest, 2);
      Module.client.options.sprintKey.setPressed(false);
      Module.client.options.useKey.setPressed(false);
      Module.client.player.setSprinting(false);
   }

   private void fetchProvider() {
      if (this.regionAlign < 9) {
         Module.client.player.getInventory().setSelectedSlot(this.regionAlign);
         ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
      } else {
         this.performVector();
      }
   }

   private void drawProfile() {
      if (this.regionAlign < 9) {
         Module.client.player.getInventory().setSelectedSlot(this.resourceClamp);
         ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
      } else {
         this.performVector();
      }
   }

   private void performVector() {
      Module.client.interactionManager
         .clickSlot(Module.client.player.playerScreenHandler.syncId, this.regionAlign, this.resourceClamp, SlotActionType.SWAP, Module.client.player);
   }

   private void attachEvent() {
      if (this.handlerRun) {
         if (Module.client.player == null || Module.client.interactionManager == null) {
            this.handlerRun = false;
         } else if (this.layerSample2 <= 0 || this.sourceCancel.prepare(800L)) {
            this.handlerRun = false;
         } else if (this.blockRun == ServerHelper.Mode.IDLE) {
            Item var1 = Module.client.player.getInventory().getStack(this.keyCheck).getItem();
            Item var2 = Module.client.player.getInventory().getStack(this.layerProject).getItem();
            if (var1 != this.entityFilter && var2 == this.entityFilter) {
               if (this.eventSend.prepare(150L)) {
                  this.eventSend.handle();
                  Module.client.interactionManager
                     .clickSlot(Module.client.player.playerScreenHandler.syncId, this.layerProject, this.keyCheck, SlotActionType.SWAP, Module.client.player);
                  this.layerSample2--;
               }
            }
         }
      }
   }

   private void readServer() {
      if (this.shaderProject) {
         this.advancePosition();
      }

      Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
   }

   private void advancePosition() {
      this.playerEvaluate.handle(new RotationAngles(Module.client.player.getYaw(), 90.0F), 180.0F, 180.0F, 180.0F, 180.0F, 1, this.checkFrame());
      Module.client.options.jumpKey.setPressed(true);
      this.taskInterpolate = true;
      if (Module.client.player.isOnGround()) {
         Module.client.player.jump();
      }
   }

   private int checkFrame() {
      return AttackAura.textureRun != null ? 30 : 15;
   }

   private void collectModule() {
      if (this.taskInterpolate) {
         this.closeProvider();
         this.taskInterpolate = false;
      }
   }

   private void closeProvider() {
      if (Module.client.options != null && Module.client.getWindow() != null) {
         boolean var1 = InputUtil.isKeyPressed(Module.client.getWindow().getHandle(), Module.client.options.jumpKey.getDefaultKey().getCode());
         Module.client.options.jumpKey.setPressed(var1);
      }
   }

   private void savePreset() {
      this.dataRelease = Module.client.player.getPos();
      this.sourceRefresh = true;
      this.requestRun = false;
      this.frameProject = false;
      this.outputFetch.handle();
      Module.client.options.useKey.setPressed(true);
      Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
      this.blockRun = ServerHelper.Mode.USE;
   }

   private void convertWindow() {
      if (Module.client.player == null || Module.client.interactionManager == null) {
         this.writePreset();
      } else if (this.measureColor()) {
         if (!this.requestRun) {
            this.unload();
         }

         this.requestRun = true;
         this.playerSave = 0;
         Module.client.options.useKey.setPressed(true);
      } else {
         if (this.requestRun && !this.frameProject) {
            this.frameProject = true;
            Module.client.options.useKey.setPressed(false);
         }

         if (this.frameProject && this.buildSource()) {
            this.writePreset();
         } else if (!this.requestRun) {
            this.computeResponse();
            if (this.scheduleAnimation() && this.playerSave++ < 1) {
               this.savePreset();
            } else {
               this.writePreset();
            }
         } else {
            if (this.outputFetch.prepare(5000L)) {
               this.writePreset();
            }
         }
      }
   }

   private void writePreset() {
      this.scanRenderer();
      this.blendMatrix();
   }

   private boolean measureColor() {
      return Module.client.player != null
         && Module.client.player.isUsingItem()
         && Module.client.player.getActiveHand() == Hand.MAIN_HAND
         && Module.client.player.getActiveItem().isOf(Items.CHORUS_FRUIT);
   }

   private boolean scheduleAnimation() {
      return Module.client.player != null && Module.client.player.getMainHandStack().isOf(Items.CHORUS_FRUIT);
   }

   private void scanRenderer() {
      if (Module.client.options != null) {
         Module.client.options.useKey.setPressed(false);
      }

      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.isUsingItem()) {
         Module.client.interactionManager.stopUsingItem(Module.client.player);
         Module.client.player.stopUsingItem();
      }
   }

   private boolean buildSource() {
      return Module.client.player != null && Module.client.player.getPos().squaredDistanceTo(this.dataRelease) >= 0.25;
   }

   private void resolve(boolean var1) {
      if (Module.client.getWindow() != null) {
         KeyBinding[] var2 = new KeyBinding[]{
            Module.client.options.forwardKey, Module.client.options.backKey, Module.client.options.leftKey, Module.client.options.rightKey, Module.client.options.jumpKey
         };
         long var3 = Module.client.getWindow().getHandle();

         for (KeyBinding var8 : var2) {
            boolean var9 = var1 && InputUtil.isKeyPressed(var3, var8.getDefaultKey().getCode());
            var8.setPressed(var9);
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var9 = var2.content().getString();
         if (this.previous.process("Авто GPS на ивенты") && var9.contains("Появился на координатах")) {
            Matcher var5 = optionAdvance2.matcher(var9);
            if (var5.find()) {
               try {
                  float var6 = Float.parseFloat(var5.group(1));
                  float var7 = Float.parseFloat(var5.group(3));
                  GpsCommand.handle(var6, var7);
               } catch (NumberFormatException var8) {
               }
            }
         }
      } else if (var1.resolve() instanceof PlaySoundS2CPacket var3) {
         this.handle(var3);
      }
   }

   private void collapseOutput() {
      this.collectModule();
      if (this.blockRun != ServerHelper.Mode.IDLE) {
         SyntheticKeyState.handle().process("ServerHelper_Lock");
      }

      if (Module.client.options != null) {
         Module.client.options.useKey.setPressed(false);
      }

      if (this.eventReceive > 0 && this.previous.process("Стопы")) {
         SyntheticKeyState.handle().process("ServerHelper_Lock");
      }

      this.eventReceive = 0;
      this.screenSubmit = 0;
      this.regionAlign = -1;
      this.resourceClamp = -1;
      this.providerOffset = false;
      this.messageParse2 = false;
      this.shaderProject = false;
      this.inputInvoke = false;
      this.sourceRefresh = false;
      this.playerSave = 0;
      this.requestRun = false;
      this.frameProject = false;
      this.scaleParse.handle();
      this.handlerRun = false;
      this.animationExpand = null;
      this.playerRun = null;
      this.moduleTick = false;
      this.blockRun = ServerHelper.Mode.IDLE;
      textureRun = false;
   }

   @Override
   public void process() {
      this.compute(false);
      this.keyFilter = 0L;
      this.requestAdapt = 0L;
      this.screenRead.clear();
      this.animationExpand = null;
      this.playerRun = null;
      this.moduleTick = false;
      this.sessionCollect.clear();
      this.inputAttach.clear();
      this.pathProject = -1;
      this.collapseOutput();
      super.process();
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (!ServerEnvironment.handle()) {
         boolean var2 = this.previous.process("Рендерить границы");
         if (var2 || this.sourceBuild.compute()) {
            RenderLayer var3 = this.previous.process("Рендерить границы сквозь стены") ? sessionBind : profileWrite;
            RenderLayer var4 = this.previous.process("Рендерить границы сквозь стены") ? elementStop : cacheExecute;
            if (this.invokeProfile()) {
               var3 = sessionBind;
               var4 = elementStop;
            }

            if (this.sourceBuild.compute()) {
               this.handle(var1, sessionBind, elementStop);
            }

            if (var2) {
               if (this.summary.compute() != -1 && KeybindSetting.process(this.summary.compute())) {
                  double var5 = MathHelper.lerp(var1.resolve(), Module.client.player.lastRenderX, Module.client.player.getX());
                  double var7 = MathHelper.lerp(var1.resolve(), Module.client.player.lastRenderY, Module.client.player.getY());
                  double var9 = MathHelper.lerp(var1.resolve(), Module.client.player.lastRenderZ, Module.client.player.getZ());
                  double var11 = 3.0;
                  double var13 = 1.0;
                  boolean var15 = this.handle(var5, var7, var9, var11, var13);
                  int var16 = var15 ? PackedColor.update(new Color(255, 50, 50).getRGB(), 10) : PackedColor.update(new Color(50, 150, 255).getRGB(), 255);
                  int var17 = var15 ? new Color(255, 0, 0).getRGB() : new Color(0, 100, 255).getRGB();
                  Immediate var18 = WorldVertexBuffer.handle();

                  try {
                     Vec3d var19 = Module.client.gameRenderer.getCamera().getPos();
                     Matrix4f var20 = var1.compute().peek().getPositionMatrix();
                     VertexConsumer var21 = var18.getBuffer(var3);
                     FarmAreaRenderer.process(
                        var21, var20, (float)(var5 - var19.x), (float)(var7 - var19.y), (float)(var9 - var19.z), (float)var11, (float)var13, var16, 40
                     );
                     VertexConsumer var22 = var18.getBuffer(var4);
                     FarmAreaRenderer.handle(
                        var22,
                        var20,
                        (float)(var5 - var19.x),
                        (float)(var7 - 0.005F - var19.y),
                        (float)(var9 - var19.z),
                        (float)var11 + 0.005F,
                        (float)var13 + 0.01F,
                        var17,
                        40
                     );
                  } finally {
                     WorldVertexBuffer.process();
                  }
               }

               this.handle(this.itemProject, 2.0, 3.0, var1, var3, var4);
               this.handle(this.matrixBlend, 4.0, 2.0, var1, var3, var4);
               this.handle(this.vectorMatch, 2.0, 2.0, var1, var3, var4);
               this.handle(this.responseCompute, 2.0, 2.0, var1, var3, var4);
               this.handle(this.latest, 2.0, 2.0, var1, var3, var4);
            }
         }
      }
   }

   private void handle(WorldRenderEvent var1, RenderLayer var2, RenderLayer var3) {
      if (Module.client.world == null || !(Module.client.crosshairTarget instanceof BlockHitResult var4 && var4.getType() == Type.BLOCK)) {
         this.vectorRun = null;
      } else if (!this.handle(Module.client.player.getMainHandStack())) {
         this.vectorRun = null;
      } else {
         BlockPos var26 = var4.getBlockPos();
         if (Module.client.world.getBlockState(var26).isAir()) {
            this.vectorRun = null;
         } else {
            ServerHelper.SecondaryDataRecord var6 = this.handle(var26, var4.getSide());
            ServerHelper.SecondaryDataRecord var7 = this.vectorRun == null ? var6 : this.vectorRun.lerp(var6, 0.28);
            this.vectorRun = var7;
            int var8 = new Color(40, 220, 170).getRGB();
            int var9 = PackedColor.update(var8, 80);
            int var10 = PackedColor.update(var8, 8);
            int var11 = new Color(40, 255, 180, 235).getRGB();
            Immediate var12 = WorldVertexBuffer.handle();

            try {
               Vec3d var13 = Module.client.gameRenderer.getCamera().getPos();
               Matrix4f var14 = var1.compute().peek().getPositionMatrix();
               float var15 = (float)(var7.minX - var13.x);
               float var16 = (float)(var7.minY - var13.y);
               float var17 = (float)(var7.minZ - var13.z);
               float var18 = (float)(var7.maxX - var13.x);
               float var19 = (float)(var7.maxY - var13.y);
               float var20 = (float)(var7.maxZ - var13.z);
               VertexConsumer var21 = var12.getBuffer(var2);
               FarmAreaRenderer.handle(var21, var14, var15, var16, var17, var18, var19, var20, var9, var10);
               VertexConsumer var22 = var12.getBuffer(var3);
               FarmAreaRenderer.process(var22, var14, var15 - 0.008F, var16 - 0.008F, var17 - 0.008F, var18 + 0.008F, var19 + 0.008F, var20 + 0.008F, var11);
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   private ServerHelper.SecondaryDataRecord handle(BlockPos var1, Direction var2) {
      return switch (var2) {
         case EAST -> new ServerHelper.SecondaryDataRecord(var1.getX() - 4, var1.getY() - 4, var1.getZ() - 4, var1.getX() + 1, var1.getY() + 5, var1.getZ() + 5);
         case WEST -> new ServerHelper.SecondaryDataRecord(var1.getX(), var1.getY() - 4, var1.getZ() - 4, var1.getX() + 5, var1.getY() + 5, var1.getZ() + 5);
         case UP -> new ServerHelper.SecondaryDataRecord(var1.getX() - 4, var1.getY() - 4, var1.getZ() - 4, var1.getX() + 5, var1.getY() + 1, var1.getZ() + 5);
         case DOWN -> new ServerHelper.SecondaryDataRecord(var1.getX() - 4, var1.getY(), var1.getZ() - 4, var1.getX() + 5, var1.getY() + 5, var1.getZ() + 5);
         case SOUTH -> new ServerHelper.SecondaryDataRecord(
            var1.getX() - 4, var1.getY() - 4, var1.getZ() - 4, var1.getX() + 5, var1.getY() + 5, var1.getZ() + 1
         );
         case NORTH -> new ServerHelper.SecondaryDataRecord(var1.getX() - 4, var1.getY() - 4, var1.getZ(), var1.getX() + 5, var1.getY() + 5, var1.getZ() + 5);
         default -> throw new MatchException(null, null);
      };
   }

   private boolean handle(ItemStack var1) {
      if (var1 == null || var1.isEmpty() || !var1.isOf(Items.NETHERITE_PICKAXE)) {
         return false;
      }

      if (SpecialItemCatalog.bindIndex(var1)) {
         return true;
      }

      StringBuilder var2 = new StringBuilder(var1.getName().getString());
      LoreComponent var3 = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (var3 != null) {
         for (Text var5 : var3.lines()) {
            var2.append(' ').append(var5.getString());
         }
      }

      String var6 = var2.toString().replaceAll("§.", "").replace('ё', 'е').replace('Ё', 'Е').toLowerCase(Locale.ROOT);
      return var6.contains("мега-бульдозер") || var6.contains("мега бульдозер");
   }

   private boolean handle(double var1, double var3, double var5, double var7, double var9) {
      if (Module.client.world == null) {
         return false;
      }

      Box var11 = new Box(var1 - var7, var3, var5 - var7, var1 + var7, var3 + var9, var5 + var7);

      for (PlayerEntity var13 : Module.client.world.getPlayers()) {
         if (var13 != Module.client.player && var13.getBoundingBox().intersects(var11)) {
            return true;
         }
      }

      return false;
   }

   private void handle(KeybindSetting var1, double var2, double var4, WorldRenderEvent var6, RenderLayer var7, RenderLayer var8) {
      if (var1.compute() != -1 && KeybindSetting.process(var1.compute())) {
         this.handle(var6, var2, var4, var7, var8);
      }
   }

   private boolean invokeProfile() {
      if (Module.client.world != null && Module.client.gameRenderer != null) {
         Vec3d var1 = Module.client.gameRenderer.getCamera().getPos();
         BlockPos var2 = BlockPos.ofFloored(var1);
         BlockState var3 = Module.client.world.getBlockState(var2);
         return !var3.getCollisionShape(Module.client.world, var2).isEmpty();
      } else {
         return false;
      }
   }
   private void handle(WorldRenderEvent var1, double var2, double var4, RenderLayer var6, RenderLayer var7) {
      double var8 = MathHelper.lerp(var1.resolve(), Module.client.player.lastRenderX, Module.client.player.getX());
      double var10 = MathHelper.lerp(var1.resolve(), Module.client.player.lastRenderY, Module.client.player.getY());
      double var12 = MathHelper.lerp(var1.resolve(), Module.client.player.lastRenderZ, Module.client.player.getZ());
      boolean var14 = this.handle(var8, var10, var12, var2, var4);
      int var15 = var14 ? new Color(255, 30, 30).getRGB() : new Color(0, 130, 255).getRGB();
      int var16 = PackedColor.update(var15, 60);
      int var17 = PackedColor.update(var15, 0);
      int var18 = var14 ? new Color(255, 0, 0, 255).getRGB() : new Color(0, 150, 255, 255).getRGB();
      Immediate var19 = WorldVertexBuffer.handle();
      boolean var32 = false /* VF: Semaphore variable */;

      try {
         var32 = true;
         Vec3d var20 = Module.client.gameRenderer.getCamera().getPos();
         Matrix4f var21 = var1.compute().peek().getPositionMatrix();
         float var22 = (float)(var8 - var2 - var20.x);
         float var23 = (float)(var10 - var20.y);
         float var24 = (float)(var12 - var2 - var20.z);
         float var25 = (float)(var8 + var2 - var20.x);
         float var26 = (float)(var10 + var4 - var20.y);
         float var27 = (float)(var12 + var2 - var20.z);
         VertexConsumer var28 = var19.getBuffer(var6);
         FarmAreaRenderer.handle(var28, var21, var22, var23, var24, var25, var26, var27, var16, var17);
         VertexConsumer var29 = var19.getBuffer(var7);
         FarmAreaRenderer.process(var29, var21, var22 - 0.005F, var23 - 0.005F, var24 - 0.005F, var25 + 0.005F, var26 + 0.005F, var27 + 0.005F, var18);
         var32 = false;
      } finally {
         if (var32) {
            WorldVertexBuffer.process();
         }
      }

      WorldVertexBuffer.process();
   }

   public String handle(int var1, String var2) {
      if (var1 == -1 || var1 == 0) {
         return "-";
      }

      if (var1 <= -100 && var1 >= -110) {
         int var5 = -(var1 + 100);

         return switch (var5) {
            case 0 -> "LMB";
            case 1 -> "RMB";
            case 2 -> "MMB";
            default -> "M" + (var5 + 1);
         };
      } else if (var2 != null && !var2.isEmpty() && !var2.equals("Неизвестно")) {
         String var3 = var2.toUpperCase();
         var3 = var3.replace("KEY.KEYBOARD.", "")
            .replace("KEY.MOUSE.", "M")
            .replace("MOUSE ", "M")
            .replace("MOUSE", "M")
            .replace("BUTTON ", "M")
            .replace("BUTTON", "M")
            .replace("LEFT.SHIFT", "LSHIFT")
            .replace("LEFT SHIFT", "LSHIFT")
            .replace("RIGHT.SHIFT", "RSHIFT")
            .replace("RIGHT SHIFT", "RSHIFT")
            .replace("LEFT.ALT", "LALT")
            .replace("LEFT ALT", "LALT")
            .replace("RIGHT.ALT", "RALT")
            .replace("RIGHT ALT", "RALT")
            .replace("LEFT.CONTROL", "LCTRL")
            .replace("LEFT CONTROL", "LCTRL")
            .replace("RIGHT.CONTROL", "RCTRL")
            .replace("RIGHT CONTROL", "RCTRL")
            .replace("CONTROL", "CTRL")
            .replace("NUMPAD.", "N")
            .replace("NUMPAD ", "N")
            .replace("NUMPAD", "N")
            .replace("SPACE", "SPC")
            .replace("ПРОБЕЛ", "SPC")
            .replace("LEFT ", "L")
            .replace("RIGHT ", "R")
            .replace("ЛЕВАЯ ", "L")
            .replace("ПРАВАЯ ", "R")
            .replace("КНОПКА МЫШИ", "MB");
         if (var3.equals("M1") || var3.equals("LMB") || var3.equals("LEFT")) {
            return "LMB";
         } else if (var3.equals("M2") || var3.equals("RMB") || var3.equals("RIGHT")) {
            return "RMB";
         } else {
            return !var3.equals("M3") && !var3.equals("MMB") && !var3.equals("MIDDLE") ? var3 : "MMB";
         }
      } else {
         return String.valueOf(var1);
      }
   }

   private void scheduleSource() {
      if (this.profileInvoke.compute() && this.target.process("FunTime")) {
         if (Module.client.player != null && Module.client.world != null) {
            long var1 = System.currentTimeMillis();
            this.handle(var1);
            this.renderTimer();
            int var3 = this.saveScale();
            if (this.pathProject > 0 && var3 < this.pathProject) {
               this.handle(Module.client.player.getPos(), ServerHelper.PrimaryMode.TRAPKA, 15000L);
            }

            this.pathProject = var3;

            for (int var4 = this.sessionCollect.size() - 1; var4 >= 0; var4--) {
               ServerHelper.FallbackCacheEntry var5 = this.sessionCollect.get(var4);
               long var6 = var1 - var5.data;
               if (var5.state && var6 >= 500L) {
                  if (this.handle(var5.instance, Blocks.NETHERITE_BLOCK, 5, 9, 5)) {
                     var5.context = ServerHelper.PrimaryMode.DRAGON_PLAST;
                     var5.config = 30000L;
                     var5.state = false;
                  } else {
                     var5.context = ServerHelper.PrimaryMode.PLAST;
                     var5.state = false;
                     var5.output = true;
                     this.compute(var5);
                  }
               } else if (var5.current) {
                  if (var6 <= 450L) {
                     this.handle(var5);
                  } else {
                     var5.current = false;
                  }
               } else if (var5.output) {
                  if (var6 <= 450L) {
                     this.compute(var5);
                  } else {
                     var5.output = false;
                  }
               } else if (var5.cache) {
                  if (var6 <= 350L) {
                     this.update(var5);
                  } else {
                     var5.cache = false;
                  }
               }

               if (var6 > var5.config) {
                  this.sessionCollect.remove(var4);
               }
            }
         }
      } else {
         if (!this.sessionCollect.isEmpty()) {
            this.sessionCollect.clear();
         }

         if (!this.inputAttach.isEmpty()) {
            this.inputAttach.clear();
         }

         this.pathProject = -1;
      }
   }

   private void handle(PlaySoundS2CPacket var1) {
      if (this.profileInvoke.compute() && this.target.process("FunTime") && Module.client.world != null) {
         String var2 = ((SoundEvent)var1.getSound().value()).id().getPath();
         float var3 = var1.getPitch();
         float var4 = var1.getVolume();
         double var5 = var1.getX();
         double var7 = var1.getY();
         double var9 = var1.getZ();
         if (this.scaleSave.compute()) {
            ChatLogger.handle(String.format(Locale.US, "§e%s§7 pitch=§f%.2f§7 vol=§f%.2f§7 @ §f%.0f %.0f %.0f", var2, var3, var4, var5, var7, var9));
         }

         this.inputAttach.add(new ServerHelper.SecondaryCacheEntry(var2, var3, var4, var5, var7, var9, System.currentTimeMillis()));

         while (this.inputAttach.size() > 128) {
            this.inputAttach.pollFirst();
         }
      }
   }

   private void handle(long var1) {
      if (!this.inputAttach.isEmpty()) {
         for (ServerHelper.State var6 : pathCheck) {
            this.handle(var6);
         }

         for (ServerHelper.SecondaryCacheEntry var8 : this.inputAttach) {
            if (!var8.current && var1 - var8.output >= 180L) {
               this.handle(var8);
               var8.current = true;
            }
         }

         this.inputAttach.removeIf(var2 -> var1 - var2.output > 1500L);
      }
   }

   private void handle(ServerHelper.State var1) {
      for (ServerHelper.SecondaryCacheEntry var3 : this.inputAttach) {
         if (!var3.current && var1.handle(var3)) {
            ServerHelper.SecondaryCacheEntry[] var4 = new ServerHelper.SecondaryCacheEntry[var1.config.length];
            boolean var5 = true;

            for (int var6 = 0; var6 < var1.config.length; var6++) {
               ServerHelper.SecondaryCacheEntry var7 = null;

               for (ServerHelper.SecondaryCacheEntry var9 : this.inputAttach) {
                  if (!var9.current && !handle(var4, var9) && var1.config[var6].handle(var9) && Math.abs(var9.output - var3.output) <= 250L) {
                     double var10 = var9.config - var3.config;
                     double var12 = var9.state - var3.state;
                     double var14 = var9.cache - var3.cache;
                     if (!(var10 * var10 + var12 * var12 + var14 * var14 > 16.0)) {
                        var7 = var9;
                        break;
                     }
                  }
               }

               if (var7 == null) {
                  var5 = false;
                  break;
               }

               var4[var6] = var7;
            }

            if (var5) {
               for (ServerHelper.SecondaryCacheEntry var19 : var4) {
                  var19.current = true;
               }

               this.handle(new Vec3d(var3.config, var3.state, var3.cache), var1);
            }
         }
      }
   }

   private static boolean handle(ServerHelper.SecondaryCacheEntry[] var0, ServerHelper.SecondaryCacheEntry var1) {
      for (ServerHelper.SecondaryCacheEntry var5 : var0) {
         if (var5 == var1) {
            return true;
         }
      }

      return false;
   }

   private void handle(Vec3d var1, ServerHelper.State var2) {
      if (!this.resolve(var1)) {
         ServerHelper.FallbackCacheEntry var3 = new ServerHelper.FallbackCacheEntry(
            var1, System.currentTimeMillis(), ServerHelper.PrimaryMode.TRAPKA, var2.data
         );
         var3.active = var2;
         var3.current = true;
         this.sessionCollect.add(var3);
         this.handle(var3);
      }
   }

   private void handle(ServerHelper.FallbackCacheEntry var1) {
      int var2 = this.process(var1);
      if (var2 != 0) {
         var1.current = false;
         if (var2 == 1) {
            var1.context = ServerHelper.PrimaryMode.TRAPKA;
            var1.config = var1.active.data;
            var1.mode = null;
         } else {
            var1.context = ServerHelper.PrimaryMode.PLAST;
            var1.config = var1.active.context > 0L ? var1.active.context : (var2 == 3 ? 60000L : 20000L);
         }
      }
   }

   private int process(ServerHelper.FallbackCacheEntry var1) {
      if (Module.client.world == null) {
         return 0;
      }

      int var2 = MathHelper.floor(var1.instance.x);
      int var3 = MathHelper.floor(var1.instance.y);
      int var4 = MathHelper.floor(var1.instance.z);
      Mutable var5 = new Mutable();
      long var6 = Long.MIN_VALUE;
      double var8 = Double.MAX_VALUE;

      for (int var10 = -2; var10 <= 2; var10++) {
         for (int var11 = -2; var11 <= 2; var11++) {
            for (int var12 = -2; var12 <= 2; var12++) {
               var5.set(var2 + var10, var3 + var11, var4 + var12);
               if (this.handle(Module.client.world.getBlockState(var5), var5)) {
                  double var13 = var10 * var10 + var11 * var11 + var12 * var12;
                  if (var13 < var8) {
                     var8 = var13;
                     var6 = var5.asLong();
                  }
               }
            }
         }
      }

      if (var6 == Long.MIN_VALUE) {
         return 0;
      }

      short var32 = 9000;
      byte var33 = 18;
      ArrayDeque var34 = new ArrayDeque();
      HashSet var35 = new HashSet();
      var34.add(var6);
      var35.add(var6);
      int var14 = Integer.MAX_VALUE;
      int var15 = Integer.MAX_VALUE;
      int var16 = Integer.MAX_VALUE;
      int var17 = Integer.MIN_VALUE;
      int var18 = Integer.MIN_VALUE;
      int var19 = Integer.MIN_VALUE;
      int var20 = 0;

      while (!var34.isEmpty() && var35.size() <= var32) {
         long var21 = (Long)var34.poll();
         int var23 = BlockPos.unpackLongX(var21);
         int var24 = BlockPos.unpackLongY(var21);
         int var25 = BlockPos.unpackLongZ(var21);
         var20++;
         if (var23 < var14) {
            var14 = var23;
         }

         if (var23 > var17) {
            var17 = var23;
         }

         if (var24 < var15) {
            var15 = var24;
         }

         if (var24 > var18) {
            var18 = var24;
         }

         if (var25 < var16) {
            var16 = var25;
         }

         if (var25 > var19) {
            var19 = var25;
         }

         for (int var26 = 0; var26 < 6; var26++) {
            int var27 = var23 + entityAdvance[var26][0];
            int var28 = var24 + entityAdvance[var26][1];
            int var29 = var25 + entityAdvance[var26][2];
            if (Math.abs(var27 - var2) <= var33 && Math.abs(var28 - var3) <= var33 && Math.abs(var29 - var4) <= var33) {
               var5.set(var27, var28, var29);
               long var30 = var5.asLong();
               if (!var35.contains(var30) && this.handle(Module.client.world.getBlockState(var5), var5)) {
                  var35.add(var30);
                  var34.add(var30);
               }
            }
         }
      }

      if (var20 < 12) {
         return 0;
      }

      int var36 = var17 - var14;
      int var22 = var18 - var15;
      int var37 = var19 - var16;
      int var38 = Math.min(var36, Math.min(var22, var37));
      int var39 = Math.max(var36, Math.max(var22, var37));
      if (var38 * 3 > var39) {
         return 1;
      }

      boolean var40 = var22 <= var36 && var22 <= var37;
      var1.mode = new Vec3d((var14 + var17) / 2.0 + 0.5, (var15 + var18) / 2.0 + 0.5, (var16 + var19) / 2.0 + 0.5);
      return var40 ? 3 : 2;
   }

   private boolean handle(BlockState var1, BlockPos var2) {
      return !var1.isAir() && !worldDispatch.contains(var1.getBlock()) ? !var1.getCollisionShape(Module.client.world, var2).isEmpty() : false;
   }

   private void renderTimer() {
      if (this.colorCompute.compute()) {
         if (Module.client.crosshairTarget instanceof BlockHitResult var1 && var1.getType() == Type.BLOCK) {
            String var3 = Registries.BLOCK.getId(Module.client.world.getBlockState(var1.getBlockPos()).getBlock()).toString();
            if (!var3.equals(this.inputHandle)) {
               this.inputHandle = var3;
               ChatLogger.handle("§bблок:§f " + var3);
            }
         }
      }
   }

   private void handle(ServerHelper.SecondaryCacheEntry var1) {
      String var2 = var1.instance;
      float var3 = var1.data;
      float var4 = var1.context;
      Vec3d var5 = new Vec3d(var1.config, var1.state, var1.cache);
      if (!this.sourceSchedule.process("Спуки тайм")) {
         if (var2.equals("block.anvil.place") && process(var3, 1.1F) && process(var4, 0.7F)) {
            this.compute(var5);
         } else if (var2.equals("block.piston.extend") && process(var3, 0.5F) && process(var4, 0.7F)) {
            this.handle(var5, ServerHelper.PrimaryMode.TRAPKA, 15000L);
         } else if (var2.equals("entity.ender_dragon.growl") && process(var3, 1.0F) && process(var4, 0.2F)) {
            this.handle(var5);
         }
      } else if (var2.equals("block.piston.extend") && process(var3, 0.5F) && process(var4, 0.5F)) {
         this.handle(var5, ServerHelper.PrimaryMode.TRAPKA, 15000L);
      } else if (var2.equals("block.anvil.place") && process(var3, 0.5F) && process(var4, 0.5F)) {
         this.process(var5);
      } else if (var2.equals("entity.ender_dragon.growl") && process(var3, 0.7F) && process(var4, 0.5F)) {
         this.handle(var5);
      }
   }

   private void handle(Vec3d var1, ServerHelper.PrimaryMode var2, long var3) {
      if (!this.resolve(var1)) {
         this.sessionCollect.add(new ServerHelper.FallbackCacheEntry(var1, System.currentTimeMillis(), var2, var3));
      }
   }

   private void handle(Vec3d var1) {
      if (!this.resolve(var1)) {
         ServerHelper.FallbackCacheEntry var2 = new ServerHelper.FallbackCacheEntry(
            var1, System.currentTimeMillis(), ServerHelper.PrimaryMode.DRAGON_PLAST, 20000L
         );
         var2.cache = true;
         this.sessionCollect.add(var2);
         this.update(var2);
      }
   }

   private void process(Vec3d var1) {
      if (!this.resolve(var1)) {
         ServerHelper.FallbackCacheEntry var2 = new ServerHelper.FallbackCacheEntry(var1, System.currentTimeMillis(), ServerHelper.PrimaryMode.PLAST, 20000L);
         var2.state = true;
         this.sessionCollect.add(var2);
      }
   }

   private void compute(Vec3d var1) {
      if (!this.resolve(var1)) {
         ServerHelper.FallbackCacheEntry var2 = new ServerHelper.FallbackCacheEntry(var1, System.currentTimeMillis(), ServerHelper.PrimaryMode.PLAST, 20000L);
         var2.output = true;
         this.sessionCollect.add(var2);
         this.compute(var2);
      }
   }

   private void compute(ServerHelper.FallbackCacheEntry var1) {
      int var2 = this.resolve(var1);
      if (var2 > 0) {
         var1.config = var2 == 2 ? 60000L : 20000L;
         var1.output = false;
      }
   }

   private int resolve(ServerHelper.FallbackCacheEntry var1) {
      if (Module.client.world == null) {
         return 0;
      }

      Vec3d var2 = var1.instance;
      int var3 = MathHelper.floor(var2.x);
      int var4 = MathHelper.floor(var2.y);
      int var5 = MathHelper.floor(var2.z);
      Mutable var6 = new Mutable();
      long var7 = Long.MIN_VALUE;
      double var9 = Double.MAX_VALUE;

      for (int var11 = -3; var11 <= 3; var11++) {
         for (int var12 = -3; var12 <= 3; var12++) {
            for (int var13 = -3; var13 <= 3; var13++) {
               var6.set(var3 + var11, var4 + var12, var5 + var13);
               if (this.handle(Module.client.world.getBlockState(var6))) {
                  double var14 = var11 * var11 + var12 * var12 + var13 * var13;
                  if (var14 < var9) {
                     var9 = var14;
                     var7 = var6.asLong();
                  }
               }
            }
         }
      }

      if (var7 == Long.MIN_VALUE) {
         return 0;
      }

      short var33 = 6000;
      byte var34 = 16;
      ArrayDeque var35 = new ArrayDeque();
      HashSet var36 = new HashSet();
      var35.add(var7);
      var36.add(var7);
      int var15 = Integer.MAX_VALUE;
      int var16 = Integer.MAX_VALUE;
      int var17 = Integer.MAX_VALUE;
      int var18 = Integer.MIN_VALUE;
      int var19 = Integer.MIN_VALUE;
      int var20 = Integer.MIN_VALUE;
      int var21 = 0;

      while (!var35.isEmpty() && var36.size() <= var33) {
         long var22 = (Long)var35.poll();
         int var24 = BlockPos.unpackLongX(var22);
         int var25 = BlockPos.unpackLongY(var22);
         int var26 = BlockPos.unpackLongZ(var22);
         var6.set(var24, var25, var26);
         if (Module.client.world.getBlockState(var6).isOf(Blocks.DEAD_TUBE_CORAL_BLOCK)) {
            var21++;
            if (var24 < var15) {
               var15 = var24;
            }

            if (var24 > var18) {
               var18 = var24;
            }

            if (var25 < var16) {
               var16 = var25;
            }

            if (var25 > var19) {
               var19 = var25;
            }

            if (var26 < var17) {
               var17 = var26;
            }

            if (var26 > var20) {
               var20 = var26;
            }
         }

         for (int var27 = 0; var27 < 6; var27++) {
            int var28 = var24 + entityAdvance[var27][0];
            int var29 = var25 + entityAdvance[var27][1];
            int var30 = var26 + entityAdvance[var27][2];
            if (Math.abs(var28 - var3) <= var34 && Math.abs(var29 - var4) <= var34 && Math.abs(var30 - var5) <= var34) {
               var6.set(var28, var29, var30);
               long var31 = var6.asLong();
               if (!var36.contains(var31) && this.handle(Module.client.world.getBlockState(var6))) {
                  var36.add(var31);
                  var35.add(var31);
               }
            }
         }
      }

      if (var21 < 3) {
         return 0;
      }

      var1.mode = new Vec3d((var15 + var18) / 2.0 + 0.5, (var16 + var19) / 2.0 + 0.5, (var17 + var20) / 2.0 + 0.5);
      int var37 = var18 - var15;
      int var23 = var19 - var16;
      int var38 = var20 - var17;
      boolean var39 = var23 < var37 && var23 < var38;
      return var39 ? 2 : 1;
   }

   private boolean handle(BlockState var1) {
      return var1.isOf(Blocks.COBBLESTONE) || var1.isOf(Blocks.ANDESITE) || var1.isOf(Blocks.DEAD_TUBE_CORAL_BLOCK);
   }

   private boolean resolve(Vec3d var1) {
      long var2 = System.currentTimeMillis();

      for (ServerHelper.FallbackCacheEntry var5 : this.sessionCollect) {
         if (var2 - var5.data <= 500L && var5.instance.squaredDistanceTo(var1) <= 2.25) {
            return true;
         }
      }

      return false;
   }

   private void update(ServerHelper.FallbackCacheEntry var1) {
      if (Module.client.world != null) {
         if (this.handle(var1.instance, Blocks.RESPAWN_ANCHOR, 6, 3, 6)) {
            var1.context = ServerHelper.PrimaryMode.DRAGON_TRAP;
            var1.config = 30000L;
            var1.cache = false;
         }
      }
   }

   private boolean handle(Vec3d var1, Block var2, int var3, int var4, int var5) {
      if (Module.client.world == null) {
         return false;
      }

      Mutable var6 = new Mutable();
      int var7 = MathHelper.floor(var1.x);
      int var8 = MathHelper.floor(var1.y);
      int var9 = MathHelper.floor(var1.z);

      for (int var10 = -var3; var10 <= var3; var10++) {
         for (int var11 = -var5; var11 <= var5; var11++) {
            for (int var12 = -var4; var12 <= var4; var12++) {
               var6.set(var7 + var10, var8 + var12, var9 + var11);
               if (Module.client.world.getBlockState(var6).isOf(var2)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private int saveScale() {
      if (Module.client.player == null) {
         return 0;
      }

      int var1 = 0;
      PlayerInventory var2 = Module.client.player.getInventory();
      int var3 = var2.size();

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = var2.getStack(var4);
         if (var5.isOf(Items.NETHERITE_HOE)) {
            var1 += var5.getCount();
         }
      }

      return var1;
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.player != null && Module.client.world != null) {
         RoundedRectRenderer var2 = var1.resolve();
         if (var2 != null) {
            if (this.optionFetch && this.target.process("FunTime")) {
               this.handle(var2, var1.prepare(), var1.apply(), var1.execute());
            }

            if (this.profileInvoke.compute() && this.target.process("FunTime") && Module.client.gameRenderer != null) {
               boolean var3 = this.timerRender.compute();
               if (!this.sessionCollect.isEmpty() || var3) {
                  var2.handle(20.0F);
                  if (var3) {
                     this.handle(var2, var1.apply(), var1.execute());
                  }

                  long var4 = System.currentTimeMillis();
                  Vec3d var6 = Module.client.gameRenderer.getCamera().getPos();

                  for (ServerHelper.FallbackCacheEntry var8 : this.sessionCollect) {
                     long var9 = var8.config - (var4 - var8.data);
                     if (var9 > 0L) {
                        boolean var11 = var8.context == ServerHelper.PrimaryMode.PLAST && var8.mode != null;
                        Vec3d var12 = var11 ? var8.mode : new Vec3d(var8.instance.x, var8.instance.y + 1.4, var8.instance.z);
                        double var13 = var6.distanceTo(var12);
                        if (!(var13 > 110.0)) {
                           Vec3d var15 = ClientMathUtil.handle(var12);
                           if (var15 != null && !(var15.z <= 0.001) && !(var15.z > 1.0)) {
                              float var16 = (float)MathHelper.clamp(1.0 - (var13 - 6.0) / 390.0, 0.667, 1.033);
                              float var17 = MathHelper.clamp((float)var9 / (float)var8.config, 0.0F, 1.0F);
                              this.handle(var2, (float)var15.x, (float)var15.y, var16, var8.context, var9, var17, var11);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void handle(RoundedRectRenderer var1, DrawContext var2, int var3, int var4) {
      this.encodePoint();
      this.computeColor();
      this.eventCollapse = this.handle(this.worldEvaluate, this.playerProject, var3, var4);
      this.runTexture();
      if (this.stateAttach != 0) {
         var1.handle(7.0F);
         float var5 = var3 * 0.5F;
         float var6 = var4 * 0.5F;

         for (int var7 = 0; var7 < this.stateAttach; var7++) {
            int var8 = this.configMatch[var7];
            ServerHelper.PrimaryDataRecord var9 = this.handle(var7, this.stateAttach, var3, var4);
            float var10 = this.apply(var7);
            float var11 = var9.size() * (0.86F + var10 * 0.14F + this.effectScan[var8] * 0.035F);
            this.actionRender[var7] = var10;
            this.playerApply[var7] = var11;
            this.bufferAdapt[var7] = MathHelper.lerp(var10, var5, var9.centerX()) - var11 * 0.5F;
            this.playerUpdate[var7] = MathHelper.lerp(var10, var6, var9.centerY()) - var11 * 0.5F;
         }

         for (int var19 = 0; var19 < this.stateAttach; var19++) {
            int var22 = this.configMatch[var19];
            boolean var25 = this.eventCollapse == var22;
            boolean var28 = this.listenerPerform[var22];
            float var31 = this.actionRender[var19];
            float var12 = this.playerApply[var19];
            float var13 = this.bufferAdapt[var19];
            float var14 = this.playerUpdate[var19];
            float var15 = Math.max(8.0F, var12 * 0.16F);
            int var16 = var28
               ? PackedColor.compute(26, 44, 78, var25 ? 180 : 138)
               : (var25 ? PackedColor.compute(70, 66, 28, 168) : PackedColor.compute(24, 26, 32, 132));
            int var17 = var28
               ? PackedColor.compute(14, 22, 42, var25 ? 170 : 122)
               : (var25 ? PackedColor.compute(34, 34, 22, 156) : PackedColor.compute(12, 14, 18, 118));
            int var18 = var28
               ? PackedColor.compute(110, 175, 255, var25 ? 230 : 190)
               : (var25 ? PackedColor.compute(255, 245, 110, 215) : PackedColor.compute(255, 255, 255, 115));
            var1.update(var31);
            var1.handle(
               var13,
               var14,
               var12,
               var12,
               var15,
               var25 ? 10.0F : 6.0F,
               1.5F,
               var28 ? PackedColor.compute(60, 130, 255, var25 ? 70 : 45) : PackedColor.compute(0, 0, 0, var25 ? 90 : 60)
            );
            this.handle(var1, var13, var14, var12, var15, var16, var17, var18, var25 ? 23.0F : 60.0F, var28 ? 2.4F : (var25 ? 2.0F : 1.25F));
            var1.onTick();
         }

         var1.compute();

         for (int var20 = 0; var20 < this.stateAttach; var20++) {
            int var23 = this.configMatch[var20];
            ItemStack var26 = this.optionParse[var23];
            float var29 = this.actionRender[var20];
            if (!var26.isEmpty() && !(var29 <= 0.08F)) {
               float var32 = this.playerApply[var20];
               float var34 = var32 / 16.0F * (this.eventCollapse == var23 ? 0.42F : 0.386F);
               float var36 = 16.0F * var34;
               ItemStackOverlayRenderer.handle(
                  var2, var26, this.bufferAdapt[var20] + (var32 - var36) * 0.5F, this.playerUpdate[var20] + (var32 - var36) * 0.5F, var34, var23, false
               );
            }
         }

         for (int var21 = 0; var21 < this.stateAttach; var21++) {
            int var24 = this.configMatch[var21];
            int var27 = this.pointSubmit[var24];
            float var30 = this.actionRender[var21];
            if (var27 > 1 && !(var30 <= 0.25F)) {
               float var33 = this.playerApply[var21];
               float var35 = Math.max(12.0F, var33 * 0.22F);
               String var37 = String.valueOf(var27);
               float var38 = TextMeasureCache.process(FontRegistry.config, var37, var35);
               float var39 = this.bufferAdapt[var21] + var33 - var38 - var33 * 0.12F;
               float var40 = this.playerUpdate[var21] + var33 - var33 * 0.13F;
               var1.handle(FontRegistry.config, var39 + 1.0F, var40 + 1.0F, var35, var37, PackedColor.compute(0, 0, 0, (int)(170.0F * var30)));
               var1.handle(FontRegistry.config, var39, var40, var35, var37, PackedColor.compute(255, 255, 255, (int)(255.0F * var30)));
            }
         }

         var1.compute();
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, float var9, float var10) {
      ModuleStateHelper.handle(var1, var2, var3, var4, var4, var5, () -> {
         var1.handle(var2, var3, var4, var4, var5, var9);
         var1.process(var2, var3, var4, var4, 0.0F, var6, var7);
      });
      var1.handle(var2, var3, var4, var4, var5, var8, var10);
   }

   private int handle(float var1, float var2, int var3, int var4) {
      for (int var5 = 0; var5 < this.stateAttach; var5++) {
         ServerHelper.PrimaryDataRecord var6 = this.handle(var5, this.stateAttach, var3, var4);
         float var7 = var6.size() * 0.12F;
         if (ModuleStateHelper.handle(var1, var2, var6.x() - var7, var6.y() - var7, var6.size() + var7 * 2.0F, var6.size() + var7 * 2.0F)) {
            return this.configMatch[var5];
         }
      }

      return this.process(var1, var2, var3, var4);
   }

   private int process(float var1, float var2, int var3, int var4) {
      if (this.stateAttach == 0) {
         return -1;
      }

      float var5 = var1 - this.playerMatch;
      float var6 = var2 - this.cacheClose;
      float var7 = MathHelper.clamp(Math.min(var3, var4) * 0.035F, 18.0F, 38.0F);
      float var8 = var5 * var5 + var6 * var6;
      if (var8 < var7 * var7) {
         return -1;
      }

      float var9 = 1.0F / (float)Math.sqrt(var8);
      float var10 = var5 * var9;
      float var11 = var6 * var9;
      float var12 = (float)Math.cos(Math.min(Math.PI / this.stateAttach, 1.319468914507713));
      float var13 = -2.0F;
      int var14 = -1;

      for (int var15 = 0; var15 < this.stateAttach; var15++) {
         double var16 = this.handle(var15, this.stateAttach);
         float var18 = var10 * (float)Math.cos(var16) + var11 * (float)Math.sin(var16);
         if (var18 > var13) {
            var13 = var18;
            var14 = this.configMatch[var15];
         }
      }

      return var13 >= var12 ? var14 : -1;
   }

   private void computeColor() {
      this.stateAttach = 0;

      for (int var1 = 0; var1 < 7; var1++) {
         this.optionParse[var1] = ItemStack.EMPTY;
         this.pointSubmit[var1] = 0;
         this.listenerPerform[var1] = false;
      }

      if (Module.client.player != null) {
         List var9 = this.adaptScale();
         PlayerInventory var2 = Module.client.player.getInventory();

         for (int var3 = 0; var3 < 36; var3++) {
            ItemStack var4 = var2.getStack(var3);
            if (!var4.isEmpty()) {
               for (int var5 = 0; var5 < 7; var5++) {
                  if (((Predicate)var9.get(var5)).test(var4)) {
                     this.pointSubmit[var5] = this.pointSubmit[var5] + var4.getCount();
                     if (this.optionParse[var5].isEmpty()) {
                        this.optionParse[var5] = var4;
                     }
                     break;
                  }
               }
            }
         }

         for (int var10 = 0; var10 < 36; var10++) {
            ItemStack var12 = var2.getStack(var10);
            if (var12.getItem() instanceof BundleItem) {
               BundleContentsComponent var13 = (BundleContentsComponent)var12.get(DataComponentTypes.BUNDLE_CONTENTS);
               if (var13 != null) {
                  for (int var6 = 0; var6 < var13.size(); var6++) {
                     ItemStack var7 = var13.get(var6);
                     if (!var7.isEmpty()) {
                        for (int var8 = 0; var8 < 7; var8++) {
                           if (((Predicate)var9.get(var8)).test(var7)) {
                              this.pointSubmit[var8] = this.pointSubmit[var8] + var7.getCount();
                              if (this.optionParse[var8].isEmpty()) {
                                 this.optionParse[var8] = var7;
                                 this.listenerPerform[var8] = true;
                              }
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         for (int var11 = 0; var11 < 7; var11++) {
            if (!this.optionParse[var11].isEmpty()) {
               this.configMatch[this.stateAttach++] = var11;
            }
         }
      }
   }

   private List<Predicate<ItemStack>> adaptScale() {
      String var1 = this.pending.compute();
      if (this.packetRead.size() == 7 && var1.equals(this.rendererCancel)) {
         return this.packetRead;
      }

      this.packetRead.clear();

      for (int var2 = 0; var2 < 7; var2++) {
         ServerHelper.CacheEntry var3 = this.process(var2);
         if (var3 == null) {
            this.packetRead.add(var0 -> false);
         } else {
            this.packetRead.add(var3.instance);
         }
      }

      this.rendererCancel = var1;
      return this.packetRead;
   }

   private ServerHelper.PrimaryDataRecord handle(int var1, int var2, int var3, int var4) {
      float var5 = this.process(var3, var4);
      float var6 = this.handle(var5, var2, var3, var4);
      double var7 = this.handle(var1, var2);
      float var9 = var3 * 0.5F + (float)Math.cos(var7) * var6 - var5 * 0.5F;
      float var10 = var4 * 0.5F + (float)Math.sin(var7) * var6 - var5 * 0.5F;
      return new ServerHelper.PrimaryDataRecord(var9, var10, var5);
   }

   private double handle(int var1, int var2) {
      double var3 = var2 == 2 ? Math.PI : -Math.PI / 2;
      return var3 + (Math.PI * 2) * var1 / Math.max(1, var2);
   }

   private float process(int var1, int var2) {
      return MathHelper.clamp(Math.min(var1, var2) * 0.115F, 64.0F, 104.0F);
   }

   private float handle(float var1, int var2, int var3, int var4) {
      float var5 = MathHelper.clamp(Math.min(var3, var4) * 0.16F, 96.0F, 150.0F);
      return var2 < 2 ? var5 : Math.max(var5, var1 * 1.3F / (2.0F * (float)Math.sin(Math.PI / var2)));
   }

   private void runTexture() {
      long var1 = System.nanoTime();
      float var3 = this.indexSynchronize == 0L ? 0.016F : MathHelper.clamp((float)(var1 - this.indexSynchronize) / 1.0E9F, 0.001F, 0.05F);
      this.indexSynchronize = var1;

      for (int var4 = 0; var4 < 7; var4++) {
         this.effectScan[var4] = handle(this.effectScan[var4], var4 == this.eventCollapse ? 1.0F : 0.0F, var3, 20.0F);
      }
   }

   private float apply(int var1) {
      float var2 = (float)(System.nanoTime() - this.scaleSetup) / 1000000.0F - var1 * 24.0F;
      return handle(MathHelper.clamp(var2 / 135.0F, 0.0F, 1.0F));
   }

   private static float handle(float var0, float var1, float var2, float var3) {
      return var0 + (var1 - var0) * (1.0F - (float)Math.exp(-var3 * var2));
   }

   private static float handle(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   private void handle(RoundedRectRenderer var1, int var2, int var3) {
      long var4 = System.currentTimeMillis();
      float var6 = 0.9F;
      float var7 = 62.0F * var6;
      float var8 = var2 * 0.5F;
      float var9 = var3 * 0.36F;

      for (int var10 = 0; var10 < profileMatch.length; var10++) {
         ServerHelper.PrimaryMode var11 = profileMatch[var10];
         long var12 = handle(var11);
         long var14 = var12 - var4 % var12;
         float var16 = MathHelper.clamp((float)var14 / (float)var12, 0.0F, 1.0F);
         float var17 = var9 + var10 * var7;
         this.handle(var1, var8, var17, var6, var11, var14, var16, false);
      }
   }

   private static long handle(ServerHelper.PrimaryMode var0) {
      return switch (var0) {
         case TRAPKA -> 15000L;
         case PLAST -> 20000L;
         case DRAGON_TRAP -> 30000L;
         case DRAGON_PLAST -> 20000L;
      };
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, ServerHelper.PrimaryMode var5, long var6, float var8, boolean var9) {
      FontObject var10 = FontRegistry.config;
      float var11 = (float)var6 / 1000.0F;
      String var12 = String.format(Locale.US, "%.1f", var11).replace('.', ',') + " Second";
      float var13 = 26.0F * var4;
      SdfTextRenderer.State var14 = RoundedRectRenderer.handle(var10, var12, var13);
      float var15 = var14.instance;
      float var16 = var14.data;
      float var17 = 19.0F * var4;
      float var18 = 3.6F * var4;
      float var19 = var17 * 0.5F + 3.2F * var4;
      float var20 = var19 + var18;
      float var21 = var20 * 2.0F;
      float var22 = 8.0F * var4;
      float var23 = 14.0F * var4;
      float var24 = 7.5F * var4;
      float var25 = 9.0F * var4;
      float var26 = Math.max(var21, var16);
      float var27 = var26 + var24 * 2.0F;
      float var28 = var22 + var21 + var25 + var15 + var23;
      float var29 = var2 - var28 * 0.5F;
      float var30 = var9 ? var3 - var27 * 0.5F : var3 - var27 - 5.0F * var4;
      float var31 = MathHelper.clamp((float)var6 / 500.0F, 0.0F, 1.0F);
      var1.update(var31);
      int var32 = process(var5);
      float var33 = var27 * 0.5F;
      var1.handle(var29, var30, var28, var27, var33, 1.0F);
      var1.handle(var29, var30, var28, var27, var33, PackedColor.compute(15, 16, 22, 210));
      var1.handle(var29, var30, var28, var27, var33, PackedColor.compute(255, 255, 255, 28), 1.0F);
      float var34 = var29 + var22 + var20;
      float var35 = var30 + var27 * 0.5F;
      var1.process(var34, var35, var19, 0.0F, 1.0F, PackedColor.compute(12, 13, 18, 245));
      int var36 = PackedColor.compute(PackedColor.process(255, 72, 72), var32, var8);
      int var37 = PackedColor.compute(255, 255, 255, 40);
      float var38 = (var19 + var20) * 0.5F;
      float var39 = var18 * 0.62F;
      byte var40 = 46;
      int var41 = (int)Math.ceil(var40 * var8);

      for (int var42 = 0; var42 < var40; var42++) {
         double var43 = (-Math.PI / 2) + (double)var42 / var40 * Math.PI * 2.0;
         float var45 = (float)(Math.cos(var43) * var38);
         float var46 = (float)(Math.sin(var43) * var38);
         var1.process(var34 + var45, var35 + var46, var39, 0.0F, 1.0F, var42 < var41 ? var36 : var37);
      }

      ItemStackOverlayRenderer.handle(var1, compute(var5), var34 - var17 * 0.5F, var35 - var17 * 0.5F, var17 / 16.0F, 0, false, 0);
      float var47 = var29 + var22 + var21 + var25;
      float var48 = var30 + (var27 - var16) * 0.5F + var16 * 0.72F;
      var1.handle(var10, var47 + 1.0F, var48 + 1.0F, var13, var12, PackedColor.compute(0, 0, 0, 165));
      var1.handle(var10, var47, var48, var13, var12, PackedColor.compute(242, 244, 250, 255));
      var1.onTick();
   }

   static boolean process(float var0, float var1) {
      return Math.abs(var0 - var1) < 0.01F;
   }

   private static int process(ServerHelper.PrimaryMode var0) {
      return switch (var0) {
         case TRAPKA -> PackedColor.process(255, 150, 60);
         case PLAST -> PackedColor.process(90, 210, 150);
         case DRAGON_TRAP -> PackedColor.process(190, 110, 255);
         case DRAGON_PLAST -> PackedColor.process(225, 120, 210);
      };
   }

   private static ItemStack compute(ServerHelper.PrimaryMode var0) {
      return switch (var0) {
         case TRAPKA -> contextParse != null ? contextParse : (contextParse = new ItemStack(Items.NETHERITE_SCRAP));
         case PLAST -> optionStop != null ? optionStop : (optionStop = new ItemStack(Items.DRIED_KELP));
         case DRAGON_TRAP -> mousePrepare != null ? mousePrepare : (mousePrepare = new ItemStack(Items.DRAGON_EGG));
         case DRAGON_PLAST -> sessionAdapt != null ? sessionAdapt : (sessionAdapt = new ItemStack(Items.DRAGON_BREATH));
      };
   }

   static class CacheEntry {
      public final Predicate<ItemStack> instance;
      public final boolean data;
      public final boolean context;
      public final boolean config;

      public CacheEntry(Predicate<ItemStack> var1, boolean var2) {
         this(var1, var2, false);
      }

      public CacheEntry(Predicate<ItemStack> var1, boolean var2, boolean var3) {
         this(var1, var2, var3, false);
      }

      public CacheEntry(Predicate<ItemStack> var1, boolean var2, boolean var3, boolean var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }
   }

   record DataRecord(ItemStack stack, int inventorySlot, int bundleStackIndex) {
      boolean isBundled() {
         return this.bundleStackIndex >= 0;
      }
   }

   static final class FallbackCacheEntry {
      final Vec3d instance;
      final long data;
      ServerHelper.PrimaryMode context;
      long config;
      boolean state;
      boolean cache;
      boolean output;
      boolean current;
      ServerHelper.State active;
      Vec3d mode;

      FallbackCacheEntry(Vec3d var1, long var2, ServerHelper.PrimaryMode var4, long var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var4;
         this.config = var5;
      }
   }

   enum Mode {
      IDLE,
      EXTRACT,
      PREPARE,
      PRE_SWAP_STOP,
      WAIT_MAIN_HAND,
      SWAP,
      USE,
      PRE_RESTORE_STOP,
      RESTORE,
      COOLDOWN;
   }

   static final class PrimaryCacheEntry {
      final String instance;
      final float data;
      final float context;

      PrimaryCacheEntry(String var1, float var2, float var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }

      boolean handle(ServerHelper.SecondaryCacheEntry var1) {
         return var1.instance.equals(this.instance) && ServerHelper.process(var1.data, this.data) && ServerHelper.process(var1.context, this.context);
      }
   }

   record PrimaryDataRecord(float x, float y, float size) {
      float centerX() {
         return this.x + this.size * 0.5F;
      }

      float centerY() {
         return this.y + this.size * 0.5F;
      }
   }

   enum PrimaryMode {
      TRAPKA,
      PLAST,
      DRAGON_TRAP,
      DRAGON_PLAST;
   }

   static final class SecondaryCacheEntry {
      final String instance;
      final float data;
      final float context;
      final double config;
      final double state;
      final double cache;
      final long output;
      boolean current;

      SecondaryCacheEntry(String var1, float var2, float var3, double var4, double var6, double var8, long var10) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var6;
         this.cache = var8;
         this.output = var10;
      }
   }

   record SecondaryDataRecord(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {

      ServerHelper.SecondaryDataRecord lerp(ServerHelper.SecondaryDataRecord var1, double var2) {
         return new ServerHelper.SecondaryDataRecord(
            MathHelper.lerp(var2, this.minX, var1.minX),
            MathHelper.lerp(var2, this.minY, var1.minY),
            MathHelper.lerp(var2, this.minZ, var1.minZ),
            MathHelper.lerp(var2, this.maxX, var1.maxX),
            MathHelper.lerp(var2, this.maxY, var1.maxY),
            MathHelper.lerp(var2, this.maxZ, var1.maxZ)
         );
      }
   }

   static final class State {
      final String instance;
      final long data;
      final long context;
      final ServerHelper.PrimaryCacheEntry[] config;

      State(String var1, long var2, long var4, ServerHelper.PrimaryCacheEntry... var6) {
         this.instance = var1;
         this.data = var2;
         this.context = var4;
         this.config = var6;
      }

      boolean handle(ServerHelper.SecondaryCacheEntry var1) {
         for (ServerHelper.PrimaryCacheEntry var5 : this.config) {
            if (var5.handle(var1)) {
               return true;
            }
         }

         return false;
      }
   }
}
