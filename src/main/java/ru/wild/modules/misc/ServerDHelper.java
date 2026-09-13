package ru.wild.modules.misc;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.ScreenOpenedEvent;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.automation.ContainerScreenPolicy;
import ru.wild.automation.RotationController;
import ru.wild.command.GpsCommand;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(
   name = "ServerDHelper",
   category = ModuleCategory.Misc,
   description = "Удобный модуль для данжа варден, подсветка а так же автоматический лут сундуков"
)
public class ServerDHelper extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "Варден", "Варден", "Медный данж");
   public final ModeSetting target = new ModeSetting("Режим работы", "Лутающий", "Лутающий", "Складывающий");
   public final BooleanSetting pending = new BooleanSetting("Складывать дроп в клан", false);
   public final ModeSetting previous = new ModeSetting("Режим", "Авто", "Авто", "По бинду").handle(() -> !this.pending.compute());
   public final KeybindSetting latest = new KeybindSetting("Бинд", -1).handle(() -> !this.previous.process("По бинду"));
   public final ChoiceSetting summary = new ChoiceSetting(
      "Предметы для лута",
      new BooleanSetting("Дон зелья", false),
      new BooleanSetting("Сферы", false),
      new BooleanSetting("Талисманы", false),
      new BooleanSetting("Модификаторы", false),
      new BooleanSetting("Незер вещи", false),
      new BooleanSetting("Стрелы", false),
      new BooleanSetting("Ценные предметы", false),
      new BooleanSetting("Яйца", false).handle(() -> !this.pending.compute())
   );
   public final BooleanSetting matrixBlend = new BooleanSetting("Установка точки на сундук", true);
   public final BooleanSetting vectorMatch = new BooleanSetting("Ротация на сундук", false);
   public final KeybindSetting itemProject = new KeybindSetting("Бинд на уст. сундука", -1).handle(() -> !this.target.process("Складывающий"));
   public final BooleanSetting responseCompute = new BooleanSetting("Не отображать экран", false);
   public static final Map<BlockPos, Long> providerFetch = new HashMap<>();
   public static final Map<BlockPos, Long> profileDraw = new HashMap<>();
   private final Queue<Runnable> vectorPerform = new ArrayDeque<>();
   private final Set<BlockPos> eventAttach = new HashSet<>();
   private final Set<BlockPos> serverRead = new HashSet<>();
   private final Map<BlockPos, ServerDHelper.State> positionAdvance = new HashMap<>();
   private final Map<BlockPos, Long> frameCheck = new HashMap<>();
   private final Map<String, Integer> moduleCollect = new HashMap<>();
   private BlockPos providerClose = null;
   private BlockPos presetSave = null;
   private ServerDHelper.PrimaryMode windowConvert = ServerDHelper.PrimaryMode.IDLE;
   private ServerDHelper.Mode presetWrite = ServerDHelper.Mode.IDLE;
   private final Stopwatch colorMeasure = new Stopwatch();
   private final Stopwatch animationSchedule = new Stopwatch();
   private final Stopwatch rendererScan = new Stopwatch();
   private final Stopwatch sourceBuild = new Stopwatch();
   private final Set<BlockPos> outputCollapse = new HashSet<>();
   private final Map<BlockPos, Long> profileInvoke = new HashMap<>();
   private int sourceSchedule = 0;
   private boolean timerRender = true;
   private boolean scaleSave = false;
   private final Stopwatch colorCompute = new Stopwatch();
   private String scaleAdapt = "N/A";
   private long textureRun = 500L;
   private static final long indexBind = 45000L;
   private boolean actionRead = false;
   private long configCollapse = 0L;
   private GenericContainerScreen dataValidate;
   private static final int[] scaleRender = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
   private static final Pattern clientRefresh = Pattern.compile("(\\d{1,2}):(\\d{1,2})");
   private static final Pattern keyFilter = Pattern.compile("(\\d{1,2}):(\\d{2})(?::(\\d{2}))?");
   private static final Pattern requestAdapt = Pattern.compile("(\\d+)\\s*(с|s|сек|sec)");
   private static final Set<String> timerMeasure = Set.of(
      "модификатор варпов",
      "модификатор вещания",
      "модификатор возврата",
      "модификатор исцеления",
      "модификатор наковальни",
      "модификатор насыщения",
      "модификатор очистки",
      "модификатор переименования",
      "модификатор подъёма",
      "модификатор починки",
      "модификатор прыжка",
      "модификатор эндер-сундука"
   );
   private static final double vectorEncode = 2000.0;
   private static final double requestReceive = 2000.0;
   private static final double windowProcess = 62500.0;
   private static final int packetSave = 1024;
   private static final RenderPipeline entryAnimate = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "block_esp_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer playerCollect = RenderLayer.of(
      "chest_esp_box", 1024, false, true, entryAnimate, MultiPhaseParameters.builder().build(false)
   );

   public ServerDHelper() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.itemProject,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.responseCompute
      );
   }

   @Override
   public void handle() {
      super.handle();
      if (Module.client.options != null) {
         this.timerRender = Module.client.options.pauseOnLostFocus;
         Module.client.options.pauseOnLostFocus = false;
      }
   }

   @Override
   public void process() {
      super.process();
      this.vectorPerform.clear();
      this.positionAdvance.clear();
      this.frameCheck.clear();
      if (Module.client.options != null) {
         Module.client.options.pauseOnLostFocus = this.timerRender;
      }

      if (!this.serverRead.isEmpty()) {
         GpsCommand.instance = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
         this.serverRead.clear();
      }

      this.windowConvert = ServerDHelper.PrimaryMode.IDLE;
      this.presetWrite = ServerDHelper.Mode.IDLE;
      this.presetSave = null;
      this.sourceSchedule = 0;
      this.dataValidate = null;
      this.profileInvoke.clear();
      this.submit();
      this.scaleSave = false;
   }

   @EventHandler
   public void handle(ScreenOpenedEvent var1) {
      if (this.responseCompute.compute() && var1.compute() instanceof GenericContainerScreen var2) {
         this.dataValidate = var2;
         var1.resolve();
      }
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.eventAttach.clear();
      this.serverRead.clear();
      this.outputCollapse.clear();
      this.profileInvoke.clear();
      this.presetSave = null;
      this.presetWrite = ServerDHelper.Mode.IDLE;
      this.sourceSchedule = 0;
      this.dataValidate = null;
      this.vectorPerform.clear();
      this.positionAdvance.clear();
      this.frameCheck.clear();
      this.submit();
   }

   private boolean refresh() {
      if (Module.client.player == null) {
         return false;
      }

      double var1 = Module.client.player.getX();
      double var3 = Module.client.player.getY();
      double var5 = Module.client.player.getZ();
      return !this.source.process("Варден")
         ? (var1 - 2000.0) * (var1 - 2000.0) + (var5 - 2000.0) * (var5 - 2000.0) <= 62500.0
         : var1 >= -2072.0 && var1 <= -1928.0 && var3 >= -56.0 && var3 <= -29.0 && var5 >= -2071.0 && var5 <= -1929.0;
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (this.scaleSave) {
            if (this.colorCompute.update(2000L)) {
               if (!"N/A".equals(this.scaleAdapt)) {
                  Module.client.player.networkHandler.sendChatCommand("an" + this.scaleAdapt);
                  ChatLogger.handle("§8[§6ServerDHelper§8] §aВозвращаемся на Анархию-" + this.scaleAdapt);
               } else {
                  ChatLogger.handle("§8[§6ServerDHelper§8] §cНе удалось определить номер анархии для реконнекта!");
               }

               this.scaleSave = false;
               if (this.target.process("Складывающий")) {
                  this.textureRun = 4000L;
                  this.windowConvert = ServerDHelper.PrimaryMode.REOPEN_CLAN;
                  this.colorMeasure.handle();
               }
            }
         } else {
            boolean var2 = this.refresh();
            if (!this.target.process("Лутающий") || var2) {
               if (this.target.process("Лутающий")) {
                  this.encodePoint();
                  if (this.actionRead && !this.drawAnimation() && System.currentTimeMillis() > this.configCollapse) {
                     Module.client.player.networkHandler.sendChatCommand("clan storage");
                     this.actionRead = false;
                  }
               } else if (this.target.process("Складывающий") && this.providerClose != null) {
                  this.animate();
               }

               GenericContainerScreen var3 = this.render();
               if (var3 != null) {
                  GenericContainerScreenHandler var4 = (GenericContainerScreenHandler)var3.getScreenHandler();
                  String var5 = var3.getTitle().getString().toLowerCase().replaceAll("§.", "").trim();
                  boolean var6 = var5.contains("клан") || var5.contains("clan") || var5.contains("хранилище");
                  if (this.target.process("Лутающий")) {
                     if (var6) {
                        this.resolve(var4);
                     } else {
                        boolean var7 = this.source.process("Варден")
                           ? var5.equals("сундук") || var5.equals("большой сундук") || var5.equals("chest") || var5.equals("large chest")
                           : var5.equals("бочка") || var5.equals("barrel");
                        if (var7) {
                           this.compute(var4);
                        }
                     }
                  } else if (this.target.process("Складывающий")) {
                     if (var6) {
                        this.handle(var4);
                     } else {
                        this.process(var4);
                     }
                  }
               }
            }
         }
      }
   }

   private GenericContainerScreen render() {
      GenericContainerScreen var1 = ContainerScreenPolicy.handle(Module.client, this.dataValidate, GenericContainerScreen.class);
      if (var1 == null) {
         this.dataValidate = null;
      }

      return var1;
   }

   private boolean tick() {
      return this.render() != null;
   }

   private boolean drawAnimation() {
      return ContainerScreenPolicy.process(Module.client, this.dataValidate) || ContainerScreenPolicy.handle(Module.client);
   }

   private void encodePoint() {
      if (!this.vectorMatch.compute()
         || !this.target.process("Лутающий")
         || Module.client.player == null
         || Module.client.world == null
         || Module.client.interactionManager == null) {
         this.save();
      } else if (this.tick()) {
         if (this.presetSave != null) {
            this.handle(this.presetSave);
         }

         this.save();
      } else {
         if (this.presetWrite == ServerDHelper.Mode.IDLE) {
            BlockPos var1 = this.load();
            if (var1 == null) {
               return;
            }

            this.presetSave = var1;
            this.presetWrite = ServerDHelper.Mode.ROTATING;
            this.sourceSchedule = 0;
            this.animationSchedule.handle();
         }

         if (this.presetSave != null && this.resolve(this.presetSave)) {
            switch (this.presetWrite) {
               case IDLE:
               default:
                  break;
               case ROTATING:
                  RotationAngles var2 = this.handle(Vec3d.ofCenter(this.presetSave));
                  RotationController.handle(var2, 999.0F, 999.0F, 60.0F, 60.0F, 2, 3, false);
                  if (new RotationAngles(Module.client.player).handle(var2) < 3.0F || this.animationSchedule.update(120L)) {
                     this.submit();
                     this.presetWrite = ServerDHelper.Mode.OPENING;
                     this.animationSchedule.handle();
                  }
                  break;
               case OPENING:
                  if (this.unload()) {
                     this.animationSchedule.handle();
                     return;
                  }

                  if (this.animationSchedule.update(90L)) {
                     this.process(this.presetSave);
                     this.sourceSchedule++;
                     this.presetWrite = ServerDHelper.Mode.WAITING_SCREEN;
                     this.animationSchedule.handle();
                  }
                  break;
               case WAITING_SCREEN:
                  if (this.animationSchedule.update(1400L)) {
                     if (this.sourceSchedule >= 2) {
                        this.handle(this.presetSave);
                        this.save();
                     } else {
                        this.presetWrite = ServerDHelper.Mode.ROTATING;
                        this.animationSchedule.handle();
                     }
                  }
            }
         } else {
            this.save();
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (Module.client.player != null) {
         if (this.target.process("Складывающий") && var1.resolve() == this.itemProject.compute()) {
            if (Module.client.crosshairTarget instanceof BlockHitResult var2) {
               BlockPos var4 = var2.getBlockPos();
               if (Module.client.world == null
                  || !(Module.client.world.getBlockEntity(var4) instanceof ChestBlockEntity)
                     && !(Module.client.world.getBlockEntity(var4) instanceof BarrelBlockEntity)) {
                  ChatLogger.handle("§8[§6ServerDHelper§8] §cСмотрите на сундук или бочку!");
               } else {
                  this.providerClose = var4;
                  ChatLogger.handle("§8[§6ServerDHelper§8] §aБазовый сундук установлен: " + var4.toShortString());
               }
            }
         } else if (!this.target.process("Лутающий") || this.refresh()) {
            if (this.target.process("Лутающий")
               && this.pending.compute()
               && this.previous.process("По бинду")
               && var1.resolve() == this.latest.compute()
               && !this.moduleCollect.isEmpty()) {
               Module.client.player.networkHandler.sendChatCommand("clan storage");
            }
         }
      }
   }

   private void animate() {
      if (!this.drawAnimation() && Module.client.player != null && Module.client.interactionManager != null) {
         switch (this.windowConvert) {
            case IDLE:
            default:
               break;
            case ROTATING:
               RotationAngles var1 = this.handle(new Vec3d(this.providerClose.getX() + 0.5, this.providerClose.getY() + 0.5, this.providerClose.getZ() + 0.5));
               RotationController.handle(var1, 35.0F, 35.0F, 35.0F, 35.0F, 20, 1, false);
               if (new RotationAngles(Module.client.player).handle(var1) < 4.0F) {
                  this.submit();
                  this.windowConvert = ServerDHelper.PrimaryMode.OPENING;
                  this.colorMeasure.handle();
               }
               break;
            case OPENING:
               if (this.unload()) {
                  this.colorMeasure.handle();
               } else if (this.colorMeasure.update(100L)) {
                  this.process(this.providerClose);
                  this.windowConvert = ServerDHelper.PrimaryMode.IDLE;
               }
               break;
            case REOPEN_CLAN:
               if (this.colorMeasure.update(this.textureRun)) {
                  Module.client.player.networkHandler.sendChatCommand("clan storage");
                  this.windowConvert = ServerDHelper.PrimaryMode.IDLE;
                  this.textureRun = 500L;
               }
         }
      }
   }

   private BlockPos load() {
      long var1 = System.currentTimeMillis();
      this.handle(var1);
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;

      for (Entry var7 : new HashMap<>(profileDraw).entrySet()) {
         BlockPos var8 = (BlockPos)var7.getKey();
         if ((Long)var7.getValue() > var1 && !this.outputCollapse.contains(var8) && !this.handle(var8, var1) && this.resolve(var8)) {
            double var9 = Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var8));
            if (!(var9 > 36.0) && var9 < var4) {
               var4 = var9;
               var3 = var8.toImmutable();
            }
         }
      }

      return var3;
   }

   private void save() {
      boolean var1 = this.presetWrite != ServerDHelper.Mode.IDLE || this.presetSave != null || this.sourceSchedule != 0;
      this.presetSave = null;
      this.presetWrite = ServerDHelper.Mode.IDLE;
      this.sourceSchedule = 0;
      if (var1) {
         this.submit();
      }
   }

   private void handle(BlockPos var1) {
      if (var1 != null) {
         BlockPos var2 = var1.toImmutable();
         this.outputCollapse.add(var2);
         this.profileInvoke.put(var2, System.currentTimeMillis() + 45000L);
         profileDraw.remove(var2);
         this.serverRead.remove(var2);
      }
   }

   private boolean handle(BlockPos var1, long var2) {
      Long var4 = this.profileInvoke.get(var1);
      if (var4 == null) {
         return false;
      } else if (var4 <= var2) {
         this.profileInvoke.remove(var1);
         return false;
      } else {
         return true;
      }
   }

   private void handle(long var1) {
      this.profileInvoke.entrySet().removeIf(var2 -> var2.getValue() <= var1);
   }

   private void submit() {
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
   }

   private boolean unload() {
      int var1 = Module.client.player.getInventory().getSelectedSlot();
      ItemStack var2 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var1);
      if (!this.handle(var2)) {
         return false;
      }

      for (int var3 = 0; var3 < 9; var3++) {
         ItemStack var4 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var3);
         if (var4.isEmpty() || !this.handle(var4)) {
            Module.client.player.getInventory().setSelectedSlot(var3);
            return true;
         }
      }

      return false;
   }

   private boolean handle(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      }

      String var2 = var1.getName().getString();
      return var1.getItem() == Items.TRIPWIRE_HOOK || var2.contains("[★]") || var2.contains("[в\u0098…]");
   }

   private void process(BlockPos var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         Direction var2 = this.compute(var1);
         Vec3d var3 = new Vec3d(
            var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5
         );
         BlockHitResult var4 = new BlockHitResult(var3, var2, var1, false);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var4);
      }
   }

   private Direction compute(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      Vec3d var3 = Module.client.player.getEyePos().subtract(var2);
      return Direction.getFacing(var3.x, var3.y, var3.z);
   }

   private boolean resolve(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      BlockEntity var2 = Module.client.world.getBlockEntity(var1);
      return this.source.process("Варден") ? var2 instanceof ChestBlockEntity : var2 instanceof BarrelBlockEntity;
   }

   private void handle(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (this.rendererScan.update(100L)) {
            boolean var2 = true;
            boolean var3 = false;

            for (int var4 = 0; var4 < 36; var4++) {
               ItemStack var5 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var4);
               if (var5.isEmpty()) {
                  var2 = false;
               } else {
                  var3 = true;
               }
            }

            boolean var11 = false;
            boolean var12 = false;

            for (int var9 : scaleRender) {
               if (var9 < var1.slots.size()) {
                  Slot var10 = (Slot)var1.slots.get(var9);
                  if (var10.hasStack() && var10.getStack().getItem() != Items.AIR) {
                     var12 = true;
                     if (!var2) {
                        Module.client.interactionManager.clickSlot(var1.syncId, var9, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                        var11 = true;
                     }
                  }
               }
            }

            if (var11) {
               this.rendererScan.handle();
            } else if (var3 && (var2 || !var12)) {
               Module.client.player.closeHandledScreen();
               this.windowConvert = ServerDHelper.PrimaryMode.ROTATING;
            }
         }
      }
   }

   private void process(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         int var2 = var1.slots.size() - 36;

         for (int var3 = var2; var3 < var1.slots.size(); var3++) {
            Slot var4 = (Slot)var1.slots.get(var3);
            if (var4.hasStack() && var4.getStack().getItem() != Items.AIR) {
               if (this.sourceBuild.update(150L)) {
                  Module.client.interactionManager.clickSlot(var1.syncId, var3, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                  this.sourceBuild.handle();
               }

               return;
            }
         }

         Module.client.player.closeHandledScreen();
         this.windowConvert = ServerDHelper.PrimaryMode.REOPEN_CLAN;
         this.colorMeasure.handle();
      }
   }

   private void compute(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         int var2 = var1.slots.size() - 36;

         for (int var3 = 0; var3 < var2; var3++) {
            Slot var4 = (Slot)var1.slots.get(var3);
            if (var4.hasStack() && this.compute(var4.getStack())) {
               if (this.rendererScan.update(50L)) {
                  ItemStack var5 = var4.getStack().copy();
                  String var6 = this.process(var5);
                  this.moduleCollect.put(var6, this.moduleCollect.getOrDefault(var6, 0) + var5.getCount());
                  Module.client.interactionManager.clickSlot(var1.syncId, var3, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                  this.rendererScan.handle();
               }

               return;
            }
         }

         Module.client.player.closeHandledScreen();
         this.handle(this.presetSave);
         this.save();
         if (this.pending.compute() && this.previous.process("Авто") && !this.moduleCollect.isEmpty()) {
            this.actionRead = true;
            this.configCollapse = System.currentTimeMillis() + 400L;
         }
      }
   }

   private void resolve(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (!this.vectorPerform.isEmpty()) {
            if (this.rendererScan.update(50L)) {
               this.vectorPerform.poll().run();
               this.rendererScan.handle();
            }
         } else if (this.moduleCollect.isEmpty()) {
            Module.client.player.closeHandledScreen();
         } else {
            int var2 = var1.slots.size() - 36;
            boolean var3 = false;

            for (int var4 = var2; var4 < var1.slots.size(); var4++) {
               Slot var5 = (Slot)var1.slots.get(var4);
               if (var5.hasStack()) {
                  String var6 = this.process(var5.getStack());
                  int var7 = this.moduleCollect.getOrDefault(var6, 0);
                  if (var7 > 0) {
                     var3 = true;
                     int var8 = var5.getStack().getCount();
                     if (var8 <= var7) {
                        int var9 = var4;
                        this.vectorPerform
                           .add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var9, 0, SlotActionType.QUICK_MOVE, Module.client.player));
                        int var10 = var7 - var8;
                        if (var10 <= 0) {
                           this.moduleCollect.remove(var6);
                        } else {
                           this.moduleCollect.put(var6, var10);
                        }
                     } else {
                        int var15 = -1;

                        for (int var16 = 0; var16 < var2; var16++) {
                           if (!((Slot)var1.slots.get(var16)).hasStack()) {
                              var15 = var16;
                              break;
                           }
                        }

                        if (var15 == -1) {
                           this.moduleCollect.clear();
                           this.vectorPerform.clear();
                           Module.client.player.closeHandledScreen();
                           return;
                        }

                        int var17 = var15;
                        int var11 = var4;
                        this.vectorPerform.add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var11, 0, SlotActionType.PICKUP, Module.client.player));
                        if (var7 <= var8 / 2) {
                           for (int var18 = 0; var18 < var7; var18++) {
                              this.vectorPerform
                                 .add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var17, 1, SlotActionType.PICKUP, Module.client.player));
                           }

                           this.vectorPerform
                              .add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var11, 0, SlotActionType.PICKUP, Module.client.player));
                        } else {
                           int var12 = var8 - var7;

                           for (int var13 = 0; var13 < var12; var13++) {
                              this.vectorPerform
                                 .add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var11, 1, SlotActionType.PICKUP, Module.client.player));
                           }

                           this.vectorPerform
                              .add(() -> Module.client.interactionManager.clickSlot(var1.syncId, var17, 0, SlotActionType.PICKUP, Module.client.player));
                        }

                        this.moduleCollect.remove(var6);
                     }

                     return;
                  }
               }
            }

            if (!var3) {
               this.moduleCollect.clear();
               this.vectorPerform.clear();
               Module.client.player.closeHandledScreen();
            }
         }
      }
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         boolean var2 = this.refresh();
         if (!this.target.process("Лутающий") || var2) {
            Immediate var3 = WorldVertexBuffer.handle();

            try {
               Vec3d var4 = Module.client.gameRenderer.getCamera().getPos();
               Matrix4f var5 = var1.compute().peek().getPositionMatrix();
               VertexConsumer var6 = var3.getBuffer(playerCollect);
               if (!var2) {
                  if (this.providerClose != null) {
                     this.handle(var6, var5, this.providerClose, var4, new Color(150, 50, 255, 120), new Color(150, 50, 255, 0));
                  }

                  return;
               }

               ChunkPos var7 = Module.client.player.getChunkPos();
               int var8 = (Integer)Module.client.options.getViewDistance().getValue();
               HashSet var9 = new HashSet();
               boolean var10 = this.source.process("Варден");

               for (int var11 = var7.x - var8; var11 <= var7.x + var8; var11++) {
                  for (int var12 = var7.z - var8; var12 <= var7.z + var8; var12++) {
                     WorldChunk var13 = Module.client.world.getChunk(var11, var12);
                     if (var13 != null) {
                        for (BlockEntity var15 : var13.getBlockEntities().values()) {
                           BlockPos var16 = var15.getPos();
                           if (this.providerClose != null && var16.equals(this.providerClose)) {
                              this.handle(var6, var5, this.providerClose, var4, new Color(150, 50, 255, 120), new Color(150, 50, 255, 0));
                           } else {
                              boolean var17 = var10 ? var15 instanceof ChestBlockEntity : var15 instanceof BarrelBlockEntity;
                              if (var17) {
                                 double var18 = var16.getX() + 0.5;
                                 double var20 = var16.getY() + 0.5;
                                 double var22 = var16.getZ() + 0.5;
                                 Iterator var24 = Module.client.world.getEntities().iterator();

                                 while (true) {
                                    if (var24.hasNext()) {
                                       Entity var25 = (Entity)var24.next();
                                       if (!(var25 instanceof ArmorStandEntity) || !(var25.squaredDistanceTo(var18, var20, var22) <= 4.0)) {
                                          continue;
                                       }

                                       long var26 = this.handle(var25.getName().getString(), var10);
                                       if (var26 == -1L) {
                                          continue;
                                       }

                                       providerFetch.put(var16, System.currentTimeMillis() + var26);
                                       this.frameCheck.merge(var16, var26, Long::max);
                                       if (!this.handle(var16, System.currentTimeMillis())) {
                                          this.outputCollapse.remove(var16);
                                       }
                                    }

                                    boolean var36 = false;
                                    long var37 = 0L;
                                    if (providerFetch.containsKey(var16)) {
                                       var37 = providerFetch.get(var16) - System.currentTimeMillis();
                                       if (var37 > 0L) {
                                          var36 = true;
                                          var9.add(var16);
                                          this.eventAttach.add(var16);
                                          if (var37 <= 5000L && this.matrixBlend.compute() && !this.serverRead.contains(var16)) {
                                             GpsCommand.handle(var16.getX(), var16.getZ());
                                             this.serverRead.add(var16);
                                          }
                                       } else {
                                          providerFetch.remove(var16);
                                          this.frameCheck.remove(var16);
                                          profileDraw.put(var16, System.currentTimeMillis() + 45000L);
                                       }
                                    }

                                    if (profileDraw.containsKey(var16)) {
                                       if (profileDraw.get(var16) - System.currentTimeMillis() > 0L) {
                                          var9.add(var16);
                                          this.eventAttach.add(var16);
                                          if (this.serverRead.contains(var16) && Module.client.player.squaredDistanceTo(var18, var20, var22) < 20.25) {
                                             this.handle(var16, "§aВы у цели. Метка снята.");
                                          }
                                       } else {
                                          profileDraw.remove(var16);
                                          if (this.serverRead.contains(var16)) {
                                             this.handle(var16, "§cВремя вышло. Метка снята.");
                                          }
                                       }
                                    }

                                    Color var27;
                                    Color var28;
                                    if (var36) {
                                       float var29 = (float)(Math.sin(System.currentTimeMillis() / 150.0) * 0.15 + 0.85);
                                       if (var37 <= 20000L) {
                                          float var30 = (float)(Math.sin(System.currentTimeMillis() / 60.0) * 0.5 + 0.5);
                                          var27 = new Color(255, 140, 0, Math.min(255, (int)((80.0F + 150.0F * var30) * var29)));
                                          var28 = new Color(255, 140, 0, 0);
                                       } else {
                                          var27 = new Color(255, 0, 0, Math.min(255, (int)(150.0F * var29)));
                                          var28 = new Color(255, 0, 0, 0);
                                       }
                                    } else {
                                       var27 = new Color(0, 255, 150, 120);
                                       var28 = new Color(0, 255, 150, 0);
                                    }

                                    this.handle(var6, var5, var16, var4, var27, var28);
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               Iterator var34 = this.eventAttach.iterator();

               while (var34.hasNext()) {
                  BlockPos var35 = (BlockPos)var34.next();
                  if (!var9.contains(var35)) {
                     var34.remove();
                     if (this.serverRead.contains(var35)) {
                        this.serverRead.remove(var35);
                        if (GpsCommand.instance.getX() == var35.getX() && GpsCommand.instance.getY() == var35.getZ()) {
                           GpsCommand.instance = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
                        }
                     }
                  }
               }
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (Module.client.world != null && Module.client.player != null && this.refresh()) {
         RoundedRectRenderer var2 = var1.resolve();
         Camera var3 = Module.client.gameRenderer.getCamera();
         Vec3d var4 = var3.getPos();
         long var5 = System.currentTimeMillis();
         HashSet var7 = new HashSet();

         for (Entry var9 : new HashMap<>(providerFetch).entrySet()) {
            BlockPos var10 = (BlockPos)var9.getKey();
            long var11 = (Long)var9.getValue() - var5;
            if (var11 > 0L) {
               BlockEntity var13 = Module.client.world.getBlockEntity(var10);
               boolean var14 = this.source.config.isEmpty() || this.source.compute().equalsIgnoreCase(this.source.config.get(0));
               boolean var15 = var14 ? var13 instanceof ChestBlockEntity : var13 instanceof BarrelBlockEntity;
               if (var15) {
                  Vec3d var16 = new Vec3d(var10.getX() + 0.5, var10.getY() + 1.28, var10.getZ() + 0.5);
                  if (!(var16.squaredDistanceTo(var4) < 1.0E-6)) {
                     Vec3d var17 = ClientMathUtil.handle(var16);
                     if (var17 != null && !(var17.z <= 0.001F) && !(var17.z > 1.0)) {
                        double var18 = var4.distanceTo(var16);
                        long var20 = Math.max(var11, this.frameCheck.getOrDefault(var10, var11));
                        float var22 = MathHelper.clamp((float)var11 / (float)Math.max(1L, var20), 0.0F, 1.0F);
                        ServerDHelper.State var23 = this.positionAdvance.computeIfAbsent(var10, var1x -> new ServerDHelper.State(var22));
                        var23.handle(true, var22);
                        var7.add(var10);
                        this.handle(var2, var23, (float)var17.x, (float)var17.y, (float)var18, var11);
                     }
                  }
               }
            }
         }

         Iterator var24 = this.positionAdvance.entrySet().iterator();

         while (var24.hasNext()) {
            Entry var25 = (Entry)var24.next();
            if (!var7.contains(var25.getKey())) {
               ((ServerDHelper.State)var25.getValue()).handle(false, 0.0F);
               if (((ServerDHelper.State)var25.getValue()).instance <= 0.02F) {
                  var24.remove();
               }
            }
         }

         this.frameCheck.keySet().removeIf(var0 -> !providerFetch.containsKey(var0));
      } else {
         this.positionAdvance.clear();
      }
   }

   private void handle(RoundedRectRenderer var1, ServerDHelper.State var2, float var3, float var4, float var5, long var6) {
      float var8 = this.handle(var2.instance);
      if (!(var8 <= 0.03F)) {
         float var9 = (float)MathHelper.clamp(16.0 / Math.max(var5, 12.0), 0.75, 1.15);
         float var10 = 6.0F * var9;
         float var11 = 4.0F * var9;
         float var12 = 23.0F * var9;
         float var13 = 22.0F * var9;
         float var14 = 18.0F * var9;
         float var15 = 4.0F * var9;
         String var16 = "КД";
         String var17 = this.process(var6);
         float var18 = RoundedRectRenderer.handle(FontRegistry.instance, var16, var14).instance;
         float var19 = RoundedRectRenderer.handle(FontRegistry.config, var17, var13).instance;
         float var20 = Math.max(48.0F * var9, var15 + var19 + var11 * 2.0F);
         float var21 = 0.88F + 0.12F * var8;
         float var22 = var3 - var20 / 2.0F;
         float var23 = var4 - var12 - 8.0F * var9 - (1.0F - var8) * 7.0F * var9;
         float var24 = var22 + var20 / 2.0F;
         float var25 = var23 + var12 / 2.0F;
         float var26 = 1.0F - MathHelper.clamp((float)var6 / 20000.0F, 0.0F, 1.0F);
         float var27 = var26 * (0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 90.0));
         int var28 = this.handle(RoundedRectRenderer.ColorState.compute(255, 70, 70, 255), RoundedRectRenderer.ColorState.compute(255, 175, 60, 255), var27);
         int var29 = this.handle(RoundedRectRenderer.ColorState.compute(25, 25, 26, 235), var8);
         int var30 = this.handle(RoundedRectRenderer.ColorState.compute(78, 78, 78, 176), var8);
         int var31 = this.handle(RoundedRectRenderer.ColorState.compute(160, 160, 165, 255), var8);
         int var32 = this.handle(RoundedRectRenderer.ColorState.compute(245, 245, 245, 255), var8);
         int var33 = this.handle(RoundedRectRenderer.ColorState.compute(0, 0, 0, 105), var8);
         int var34 = this.handle(var28, var8);
         var1.compute(var21, var21, var24, var25);
         var1.handle(var22, var23, var20, var12, var10, var29);
         var1.handle(var22, var23, var20, var12, var10, var30, Math.max(1.0F, 0.8F * var9));
         float var35 = var23 + 15.3F * var9;
         float var36 = var22 + var11;
         var1.handle(FontRegistry.config, var36 + var15, var35, var13, var17, var32);
         var1.check();
      }
   }

   private String process(long var1) {
      long var3 = Math.max(0L, (var1 + 999L) / 1000L);
      long var5 = var3 / 3600L;
      long var7 = var3 % 3600L / 60L;
      long var9 = var3 % 60L;
      return var5 > 0L ? String.format(Locale.ROOT, "%d:%02d:%02d", var5, var7, var9) : String.format(Locale.ROOT, "%02d:%02d", var7, var9);
   }

   private float handle(float var1) {
      float var2 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return 1.0F - (float)Math.pow(1.0F - var2, 3.0);
   }

   private int handle(int var1, int var2, float var3) {
      float var4 = MathHelper.clamp(var3, 0.0F, 1.0F);
      int var5 = var1 >> 24 & 0xFF;
      int var6 = var1 >> 16 & 0xFF;
      int var7 = var1 >> 8 & 0xFF;
      int var8 = var1 & 0xFF;
      int var9 = var2 >> 24 & 0xFF;
      int var10 = var2 >> 16 & 0xFF;
      int var11 = var2 >> 8 & 0xFF;
      int var12 = var2 & 0xFF;
      int var13 = (int)(var5 + (var9 - var5) * var4);
      int var14 = (int)(var6 + (var10 - var6) * var4);
      int var15 = (int)(var7 + (var11 - var7) * var4);
      int var16 = (int)(var8 + (var12 - var8) * var4);
      return RoundedRectRenderer.ColorState.compute(var14, var15, var16, var13);
   }

   private int handle(int var1, float var2) {
      int var3 = var1 >> 24 & 0xFF;
      int var4 = var1 >> 16 & 0xFF;
      int var5 = var1 >> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      int var7 = (int)MathHelper.clamp(var3 * var2, 0.0F, 255.0F);
      return RoundedRectRenderer.ColorState.compute(var4, var5, var6, var7);
   }

   private void handle(BlockPos var1, String var2) {
      profileDraw.remove(var1);
      this.serverRead.remove(var1);
      if (GpsCommand.instance.getX() == var1.getX() && GpsCommand.instance.getY() == var1.getZ()) {
         GpsCommand.instance = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
         ChatLogger.handle("§8[§6ServerDHelper§8] " + var2);
      }
   }

   private RotationAngles handle(Vec3d var1) {
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

   private long handle(String var1, boolean var2) {
      if (var2) {
         Matcher var3 = clientRefresh.matcher(var1);
         if (var3.find()) {
            try {
               return (Integer.parseInt(var3.group(1)) * 60L + Integer.parseInt(var3.group(2))) * 1000L;
            } catch (NumberFormatException var8) {
            }
         }
      } else {
         Matcher var9 = keyFilter.matcher(var1);
         if (var9.find()) {
            try {
               int var10 = Integer.parseInt(var9.group(1));
               int var5 = Integer.parseInt(var9.group(2));
               return var9.group(3) != null ? (var10 * 3600L + var5 * 60L + Integer.parseInt(var9.group(3))) * 1000L : (var10 * 60L + var5) * 1000L;
            } catch (NumberFormatException var7) {
            }
         }

         Matcher var4 = requestAdapt.matcher(var1);
         if (var4.find()) {
            try {
               return Integer.parseInt(var4.group(1)) * 1000L;
            } catch (NumberFormatException var6) {
            }
         }
      }

      return -1L;
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

   private String process(ItemStack var1) {
      return var1.getItem().toString() + "|" + var1.getName().getString();
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

   private boolean compute(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         String var2 = var1.getName().getString();
         if (var2.contains("[★]")) {
            return true;
         } else {
            Item var3 = var1.getItem();
            if (this.summary.process("Незер вещи") && this.handle(var3)) {
               return true;
            } else if (this.summary.process("Дон зелья") && this.update(var1)) {
               return true;
            } else if (this.summary.process("Сферы") && this.apply(var1)) {
               return true;
            } else if (this.summary.process("Талисманы") && this.execute(var1)) {
               return true;
            } else if (this.summary.process("Модификаторы") && this.resolve(var1)) {
               return true;
            } else if (!this.summary.process("Стрелы") || var3 != Items.ARROW && var3 != Items.TIPPED_ARROW && var3 != Items.SPECTRAL_ARROW) {
               return this.summary.process("Яйца") && var3 instanceof SpawnEggItem ? true : this.summary.process("Ценные предметы") && this.prepare(var1);
            } else {
               return true;
            }
         }
      }
   }

   private boolean resolve(ItemStack var1) {
      String var2 = var1.getName().getString().replaceAll("§.", "").trim().toLowerCase(Locale.ROOT);
      return timerMeasure.contains(var2);
   }

   private boolean update(ItemStack var1) {
      return SpecialItemCatalog.save(var1)
         || SpecialItemCatalog.submit(var1)
         || SpecialItemCatalog.unload(var1)
         || SpecialItemCatalog.fetch(var1)
         || SpecialItemCatalog.measure(var1)
         || SpecialItemCatalog.blendMatrix(var1)
         || SpecialItemCatalog.matchVector(var1);
   }

   private boolean apply(ItemStack var1) {
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

   private boolean execute(ItemStack var1) {
      return SpecialItemCatalog.onTick(var1)
         || SpecialItemCatalog.select(var1)
         || SpecialItemCatalog.refresh(var1)
         || SpecialItemCatalog.render(var1)
         || SpecialItemCatalog.tick(var1)
         || SpecialItemCatalog.drawAnimation(var1)
         || SpecialItemCatalog.encodePoint(var1)
         || SpecialItemCatalog.animate(var1);
   }

   private boolean handle(Item var1) {
      return var1 == Items.NETHERITE_HELMET
         || var1 == Items.NETHERITE_CHESTPLATE
         || var1 == Items.NETHERITE_LEGGINGS
         || var1 == Items.NETHERITE_BOOTS
         || var1 == Items.NETHERITE_SWORD
         || var1 == Items.NETHERITE_PICKAXE;
   }

   private boolean prepare(ItemStack var1) {
      Item var2 = var1.getItem();
      if (this.summary.process("Незер вещи") && this.handle(var2)) {
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
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var4 = var2.content().getString();
         if (var4.contains("Данная команда недоступна в режиме AFK")
            && !this.scaleSave
            && Module.client.player != null
            && Module.client.player.networkHandler != null) {
            NicknameUtil.instance.handle();
            this.scaleAdapt = NicknameUtil.instance.compute();
            Module.client.player.networkHandler.sendChatCommand("hub");
            this.scaleSave = true;
            this.colorCompute.handle();
            if (this.drawAnimation()) {
               Module.client.player.closeHandledScreen();
            }

            this.windowConvert = ServerDHelper.PrimaryMode.IDLE;
         }
      }
   }

   enum Mode {
      IDLE,
      ROTATING,
      OPENING,
      WAITING_SCREEN;
   }

   enum PrimaryMode {
      IDLE,
      ROTATING,
      OPENING,
      REOPEN_CLAN;
   }

   static class State {
      float instance;
      private float data;
      private long context;

      State(float var1) {
         this.data = var1;
         this.context = System.currentTimeMillis();
      }

      void handle(boolean var1, float var2) {
         long var3 = System.currentTimeMillis();
         float var5 = MathHelper.clamp((float)(var3 - this.context) / 16.666F, 0.5F, 3.0F);
         this.context = var3;
         this.instance = this.instance + ((var1 ? 1.0F : 0.0F) - this.instance) * MathHelper.clamp(0.18F * var5, 0.0F, 1.0F);
         this.data = this.data + (MathHelper.clamp(var2, 0.0F, 1.0F) - this.data) * MathHelper.clamp(0.12F * var5, 0.0F, 1.0F);
      }
   }
}
