package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.ScreenOpenedEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.DynamicActionSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.ContainerScreenPolicy;
import ru.wild.automation.RotationPlayback;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.math.ActionDelay;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(
   name = "AutoVillageTrade",
   category = ModuleCategory.Misc,
   description = "Автоматически покупает товары у жителей",
   flags = ModuleFlag.VIP
)
public class AutoVillageTrade extends Module {
   private static final String pending = "Золотой слиток";
   private static final String previous = "Редстоун";
   private static final String latest = "Лазурит";
   private static final String summary = "Жемчуг Эндера";
   private static final String matrixBlend = "Бутылочка опыта";
   private static final String vectorMatch = "Стекло";
   private static final String itemProject = "Бирка";
   private static final String responseCompute = "Стрелы";
   private static final String providerFetch = "Хлеб";
   private static final String profileDraw = "Золотая морковь";
   private static final String vectorPerform = "Кварцевый блок";
   private static final String eventAttach = "Седло";
   private static final long serverRead = 10000L;
   private static final int positionAdvance = 2;
   private static final int frameCheck = 64;
   private static final long moduleCollect = 2500L;
   private static final long providerClose = 10000L;
   public final ModeSetting source = new ModeSetting(
      "Что покупать",
      "Золотой слиток",
      "Золотой слиток",
      "Редстоун",
      "Лазурит",
      "Жемчуг Эндера",
      "Бутылочка опыта",
      "Стекло",
      "Бирка",
      "Стрелы",
      "Хлеб",
      "Золотая морковь",
      "Кварцевый блок",
      "Седло"
   );
   private final NumberSetting presetSave = new NumberSetting("Макс. цена", 64.0F, 1.0F, 64.0F, 1.0F, false);
   private final NumberSetting windowConvert = new NumberSetting("Запас изумрудов", 64.0F, 0.0F, 2304.0F, 64.0F, false);
   private final NumberSetting presetWrite = new NumberSetting("Радиус жителя", 4.0F, 2.0F, 8.0F, 0.5F, false);
   private final NumberSetting colorMeasure = new NumberSetting("Задержка (мс)", 120.0F, 50.0F, 1000.0F, 10.0F, false);
   private final NumberSetting animationSchedule = new NumberSetting("КД рескана (сек)", 45.0F, 5.0F, 300.0F, 5.0F, false);
   private final BooleanSetting rendererScan = new BooleanSetting("Авто-изумруды", true);
   private final KeybindSetting sourceBuild = new KeybindSetting("Точка", -1);
   private final KeybindSetting outputCollapse = new KeybindSetting("Сундук", -1);
   private final DynamicActionSetting profileInvoke = new DynamicActionSetting("Сброс точек", 0, this::animate) {
      @Override
      public void resolve() {
         AutoVillageTrade.this.encodePoint();
      }
   };
   public final BooleanSetting target = new BooleanSetting("Не отображать экран", false);
   private static BlockPos sourceSchedule;
   private static BlockPos timerRender;
   private static BlockPos scaleSave;
   private final RotationPlayback colorCompute = new RotationPlayback();
   private final Stopwatch scaleAdapt = new Stopwatch();
   private final ActionDelay textureRun = new ActionDelay();
   private final Map<UUID, AutoVillageTrade.AnimationState> indexBind = new HashMap<>();
   private final List<BlockPos> actionRead = new ArrayList<>();
   private AutoVillageTrade.Mode configCollapse = AutoVillageTrade.Mode.IDLE;
   private UUID dataValidate;
   private int scaleRender = -1;
   private int clientRefresh;
   private int keyFilter;
   private int requestAdapt;
   private long timerMeasure;
   private long vectorEncode;
   private BlockPos requestReceive;
   private int windowProcess = -1;
   private Boolean packetSave;
   private Boolean entryAnimate;
   private String playerCollect = "Золотой слиток";
   private int stateApply = -1;
   private int matrixFilter;
   private int layerSample = -1;
   private long worldSend;
   private Screen targetWrite;

   public AutoVillageTrade() {
      this.handle(
         this.sourceBuild,
         this.outputCollapse,
         this.profileInvoke,
         this.source,
         this.presetSave,
         this.windowConvert,
         this.presetWrite,
         this.colorMeasure,
         this.animationSchedule,
         this.rendererScan,
         this.target
      );
   }

   public AutoVillageTrade.DataRecord handle(VillagerEntity var1) {
      if (this.enabled && var1 != null) {
         AutoVillageTrade.AnimationState var2 = this.indexBind.get(var1.getUuid());
         if (var2 != null && var2.mode && var2.cache != Integer.MAX_VALUE) {
            int var3 = Math.max(0, (var2.active - var2.current) * var2.output);
            boolean var4 = !var2.selection && var2.cache <= this.measureTimer() && var3 > 0;
            ItemStack var5 = new ItemStack(this.refreshClient(), Math.max(1, Math.min(99, var3)));
            return new AutoVillageTrade.DataRecord(var5, var2.cache, var2.output, var3, var4);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public void handle() {
      super.handle();
      this.resolve(false);
      this.playerCollect = this.source.compute();
      this.invokeProfile();
      this.buildSource();
      if (this.actionRead.isEmpty()) {
         this.process("Установи две точки маршрута через бинд «Точка».");
      } else {
         this.savePreset();
      }
   }

   @Override
   public void process() {
      this.collapseOutput();
      this.scheduleSource();
      this.colorCompute.handle();
      this.resolve(true);
      super.process();
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (Module.client.player != null && this.textureRun.resolve(300L)) {
         if (var1.resolve() == this.outputCollapse.compute()) {
            this.drawAnimation();
            this.textureRun.handle();
         } else if (var1.resolve() == this.sourceBuild.compute()) {
            if (this.requestAdapt == 0) {
               sourceSchedule = Module.client.player.getBlockPos();
               timerRender = null;
               this.requestAdapt = 1;
               this.process("Точка 1: " + sourceSchedule.toShortString());
            } else if (this.requestAdapt == 1) {
               timerRender = Module.client.player.getBlockPos();
               this.requestAdapt = 2;
               this.process("Точка 2: " + timerRender.toShortString());
            } else {
               sourceSchedule = Module.client.player.getBlockPos();
               timerRender = null;
               this.requestAdapt = 1;
               this.process("Точки сброшены. Точка 1: " + sourceSchedule.toShortString());
            }

            this.buildSource();
            if (this.enabled && !this.actionRead.isEmpty()) {
               this.savePreset();
            }

            this.textureRun.handle();
         }
      }
   }

   @EventHandler
   public void handle(ScreenOpenedEvent var1) {
      if (this.target.compute() && this.handle(var1.compute())) {
         this.targetWrite = var1.compute();
         var1.resolve();
      }
   }

   private void drawAnimation() {
      if (Module.client.crosshairTarget instanceof BlockHitResult var1 && Module.client.world != null) {
         BlockPos var3 = var1.getBlockPos();
         if (!this.update(var3)) {
            this.process("Это не сундук и не бочка.");
         } else {
            scaleSave = var3;
            this.process("Сундук для складирования установлен: " + var3.toShortString());
         }
      } else {
         this.process("Наведи прицел на сундук или бочку.");
      }
   }

   void encodePoint() {
      sourceSchedule = null;
      timerRender = null;
      this.requestAdapt = 0;
      this.actionRead.clear();
      this.indexBind.clear();
      this.dataValidate = null;
      this.scaleRender = -1;
      this.clientRefresh = 0;
      this.requestReceive = null;
      this.windowProcess = -1;
      this.configCollapse = AutoVillageTrade.Mode.IDLE;
      this.saveScale();
      this.collapseOutput();
      this.scaleAdapt.handle();
      this.process("Точки маршрута сброшены.");
   }

   private String animate() {
      if (sourceSchedule == null && timerRender == null) {
         return "Точки не заданы";
      } else {
         return timerRender == null ? "Сбросить 1 точку" : "Сбросить 2 точки";
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null && Module.client.getNetworkHandler() != null) {
         if (PlayerHelper.refresh()) {
            this.collapseOutput();
            this.colorCompute.handle();
         } else {
            if (this.actionRead.isEmpty()) {
               this.buildSource();
               if (this.actionRead.isEmpty()) {
                  return;
               }

               this.savePreset();
            }

            if (!this.source.compute().equals(this.playerCollect)) {
               this.playerCollect = this.source.compute();
               this.indexBind.clear();
               this.saveScale();
               this.savePreset();
            } else {
               switch (this.configCollapse) {
                  case IDLE:
                     this.load();
                     break;
                  case SCAN_ROUTE:
                     this.save();
                     break;
                  case OPEN_SCAN:
                     this.handle(AutoVillageTrade.Mode.WAIT_SCAN_SCREEN);
                     break;
                  case WAIT_SCAN_SCREEN:
                     this.handle(AutoVillageTrade.Mode.READ_SCAN_SCREEN, AutoVillageTrade.Mode.SCAN_ROUTE);
                     break;
                  case READ_SCAN_SCREEN:
                     this.unload();
                     break;
                  case CLOSE_SCAN_SCREEN:
                     this.process(AutoVillageTrade.Mode.SCAN_ROUTE);
                     break;
                  case MOVE_TO_TRADE:
                     this.submit();
                     break;
                  case OPEN_TRADE:
                     this.handle(AutoVillageTrade.Mode.WAIT_TRADE_SCREEN);
                     break;
                  case WAIT_TRADE_SCREEN:
                     this.handle(AutoVillageTrade.Mode.BUY_TRADE, AutoVillageTrade.Mode.IDLE);
                     break;
                  case BUY_TRADE:
                     this.fetch();
                     break;
                  case CLOSE_TRADE_SCREEN:
                     this.process(AutoVillageTrade.Mode.IDLE);
                     break;
                  case MOVE_TO_STORAGE:
                     this.blendMatrix();
                     break;
                  case OPEN_STORAGE:
                     this.matchVector();
                     break;
                  case WAIT_STORAGE_SCREEN:
                     this.projectItem();
                     break;
                  case PUT_STORAGE:
                     this.computeResponse();
                     break;
                  case BUY_EMERALDS_OPEN_SHOP:
                     this.drawProfile();
                     break;
                  case BUY_EMERALDS_WAIT_SHOP:
                     this.performVector();
                     break;
                  case BUY_EMERALDS_FIND_GOLD:
                     this.attachEvent();
                     break;
                  case BUY_EMERALDS_WAIT_MENU:
                     this.readServer();
                     break;
                  case BUY_EMERALDS_FIND_EMERALD:
                     this.advancePosition();
                     break;
                  case BUY_EMERALDS_WAIT_CONFIRM:
                     this.checkFrame();
                     break;
                  case BUY_EMERALDS_CONFIRM:
                     this.collectModule();
                     break;
                  case BUY_EMERALDS_CLOSE:
                     this.closeProvider();
                     break;
                  case WAIT_RESTOCK:
                     this.fetchProvider();
               }
            }
         }
      }
   }

   private void load() {
      if (this.runTexture()) {
         this.measure();
      } else if (!this.rendererScan.compute() || !this.collapseConfig()) {
         AutoVillageTrade.AnimationState var1 = this.measureColor();
         if (var1 != null) {
            this.dataValidate = var1.instance;
            this.scaleRender = var1.state;
            this.configCollapse = AutoVillageTrade.Mode.MOVE_TO_TRADE;
            this.scaleAdapt.handle();
         } else if (this.writePreset()) {
            this.savePreset();
         } else {
            this.configCollapse = AutoVillageTrade.Mode.WAIT_RESTOCK;
            this.scaleAdapt.handle();
         }
      } else if (!this.bindIndex()) {
         this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_OPEN_SHOP;
         this.scaleAdapt.handle();
      }
   }

   private void save() {
      if (this.clientRefresh >= this.actionRead.size()) {
         this.convertWindow();
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      } else {
         BlockPos var1 = this.actionRead.get(this.clientRefresh);
         this.handle(var1, 0);
         if (this.handle(var1, 1.2)) {
            VillagerEntity var2 = this.resolve(var1);
            if (var2 == null) {
               this.clientRefresh++;
            } else {
               this.dataValidate = var2.getUuid();
               this.scaleRender = -1;
               this.collapseOutput();
               this.configCollapse = AutoVillageTrade.Mode.OPEN_SCAN;
               this.scaleAdapt.handle();
            }
         }
      }
   }

   private void submit() {
      AutoVillageTrade.AnimationState var1 = this.scheduleAnimation();
      if (var1 == null) {
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
      } else if (this.process(Items.EMERALD) < Math.max(1, var1.cache)) {
         if (!this.validateData()) {
            this.configCollapse = AutoVillageTrade.Mode.IDLE;
            this.scaleAdapt.handle();
         } else if (!this.bindIndex()) {
            this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_OPEN_SHOP;
            this.scaleAdapt.handle();
         }
      } else {
         VillagerEntity var2 = this.handle(var1.instance);
         if (var2 != null) {
            var1.data = var2.getBlockPos();
            var1.context = var2.getId();
         }

         this.handle(var1.data, 2);
         if (this.handle(var1.data, this.presetWrite.compute() + 0.5F)) {
            this.collapseOutput();
            this.configCollapse = AutoVillageTrade.Mode.OPEN_TRADE;
            this.scaleAdapt.handle();
         }
      }
   }

   private void handle(AutoVillageTrade.Mode var1) {
      VillagerEntity var2 = this.handle(this.dataValidate);
      if (var2 != null && var2.isAlive()) {
         if (Module.client.player.squaredDistanceTo(var2.getPos()) > this.handle(this.presetWrite.compute() + 1.0F)) {
            this.scanRenderer();
            this.configCollapse = var1 == AutoVillageTrade.Mode.WAIT_SCAN_SCREEN ? AutoVillageTrade.Mode.SCAN_ROUTE : AutoVillageTrade.Mode.IDLE;
            this.scaleAdapt.handle();
         } else {
            RotationAngles var3 = this.handle(var2.getEyePos());
            this.colorCompute.handle(var3, 45.0F, 45.0F, 2, 15);
            if (!(new RotationAngles(Module.client.player).handle(var3) > 7.0F)) {
               if (this.scaleAdapt.update(this.adaptRequest())) {
                  Module.client.interactionManager.interactEntity(Module.client.player, var2, Hand.MAIN_HAND);
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  this.configCollapse = var1;
                  this.scaleAdapt.handle();
               }
            }
         }
      } else {
         this.scanRenderer();
         this.configCollapse = var1 == AutoVillageTrade.Mode.WAIT_SCAN_SCREEN ? AutoVillageTrade.Mode.SCAN_ROUTE : AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      }
   }

   private void handle(AutoVillageTrade.Mode var1, AutoVillageTrade.Mode var2) {
      if (Module.client.player.currentScreenHandler instanceof MerchantScreenHandler) {
         this.configCollapse = var1;
         this.scaleAdapt.handle();
      } else {
         if (this.scaleAdapt.update(2500L)) {
            this.scanRenderer();
            this.configCollapse = var2;
            this.scaleAdapt.handle();
         }
      }
   }

   private void unload() {
      if (Module.client.player.currentScreenHandler instanceof MerchantScreenHandler var1) {
         this.handle(this.dataValidate, var1.getRecipes());
         this.saveScale();
         this.configCollapse = AutoVillageTrade.Mode.CLOSE_SCAN_SCREEN;
         this.scaleAdapt.handle();
      } else {
         this.configCollapse = AutoVillageTrade.Mode.SCAN_ROUTE;
         this.scaleAdapt.handle();
      }
   }

   private void fetch() {
      if (!(Module.client.player.currentScreenHandler instanceof MerchantScreenHandler var1)) {
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      } else {
         AutoVillageTrade.AnimationState var5 = this.handle(this.dataValidate, var1.getRecipes());
         if (var5 != null && var5.mode && var5.state >= 0 && !var5.selection && var5.cache <= this.measureTimer()) {
            this.scaleRender = var5.state;
            if (this.process(Items.EMERALD) < var5.cache) {
               this.saveScale();
               if (!this.validateData()) {
                  this.configCollapse = AutoVillageTrade.Mode.CLOSE_TRADE_SCREEN;
                  this.scaleAdapt.handle();
               } else if (!this.bindIndex()) {
                  this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_OPEN_SHOP;
                  this.scaleAdapt.handle();
               }
            } else if (this.compute(this.refreshClient())) {
               if (this.scaleAdapt.update(this.adaptRequest())) {
                  var1.setRecipeIndex(this.scaleRender);
                  var1.switchTo(this.scaleRender);
                  Module.client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(this.scaleRender));
                  Slot var3 = var1.getSlot(2);
                  if (var3.hasStack() && var3.getStack().isOf(this.refreshClient())) {
                     int var4 = Math.max(1, var3.getStack().getCount());
                     Module.client.interactionManager.clickSlot(var1.syncId, 2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                     var5.handler += var4;
                     var5.animationDraw = var5.animationDraw + var5.cache;
                     var5.renderer = System.currentTimeMillis();
                     this.scaleAdapt.handle();
                  } else {
                     this.scaleAdapt.handle();
                  }
               }
            } else {
               if (scaleSave != null && this.process(this.refreshClient()) > 0) {
                  this.saveScale();
                  this.measure();
               } else {
                  this.process("Инвентарь заполнен. Установи сундук для складирования.");
                  this.saveScale();
                  this.configCollapse = AutoVillageTrade.Mode.CLOSE_TRADE_SCREEN;
               }

               this.scaleAdapt.handle();
            }
         } else {
            this.saveScale();
            this.configCollapse = AutoVillageTrade.Mode.CLOSE_TRADE_SCREEN;
            this.scaleAdapt.handle();
         }
      }
   }

   private void process(AutoVillageTrade.Mode var1) {
      if (this.scaleAdapt.update(150L)) {
         if (this.adaptScale()) {
            this.saveScale();
            this.scaleAdapt.handle();
         } else {
            this.configCollapse = var1;
            this.scaleAdapt.handle();
         }
      }
   }

   private void measure() {
      if (scaleSave == null) {
         this.process("Сундук для складирования не установлен.");
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      } else if (!this.update(scaleSave)) {
         this.process("Сундук для складирования недоступен.");
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      } else {
         this.collapseOutput();
         this.saveScale();
         this.stateApply = -1;
         this.matrixFilter = 0;
         this.configCollapse = AutoVillageTrade.Mode.MOVE_TO_STORAGE;
         this.scaleAdapt.handle();
      }
   }

   private void blendMatrix() {
      if (scaleSave != null && this.update(scaleSave)) {
         this.handle(scaleSave, 2);
         if (this.handle(scaleSave, 3.5)) {
            this.collapseOutput();
            this.configCollapse = AutoVillageTrade.Mode.OPEN_STORAGE;
            this.scaleAdapt.handle();
         }
      } else {
         this.process("Сундук для складирования недоступен.");
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      }
   }

   private void matchVector() {
      if (scaleSave == null || !this.update(scaleSave)) {
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      } else if (this.computeColor() != null) {
         this.configCollapse = AutoVillageTrade.Mode.PUT_STORAGE;
         this.scaleAdapt.handle();
      } else if (this.renderTimer()) {
         this.scaleAdapt.handle();
      } else {
         RotationAngles var1 = this.handle(Vec3d.ofCenter(scaleSave));
         this.colorCompute.handle(var1, 35.0F, 35.0F, 4, 15);
         if (!(new RotationAngles(Module.client.player).handle(var1) > 4.0F)) {
            if (this.scaleAdapt.update(this.adaptRequest())) {
               this.apply(scaleSave);
               this.configCollapse = AutoVillageTrade.Mode.WAIT_STORAGE_SCREEN;
               this.scaleAdapt.handle();
            }
         }
      }
   }

   private void projectItem() {
      if (this.computeColor() != null) {
         this.configCollapse = AutoVillageTrade.Mode.PUT_STORAGE;
         this.scaleAdapt.handle();
      } else {
         if (this.scaleAdapt.update(2500L)) {
            this.configCollapse = AutoVillageTrade.Mode.OPEN_STORAGE;
            this.scaleAdapt.handle();
         }
      }
   }

   private void computeResponse() {
      GenericContainerScreen var1 = this.computeColor();
      if (var1 == null) {
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
      } else {
         int var2 = this.process(this.refreshClient());
         if (var2 <= 0) {
            this.saveScale();
            this.configCollapse = AutoVillageTrade.Mode.IDLE;
            this.scaleAdapt.handle();
         } else if (this.scaleAdapt.update(150L)) {
            if (this.stateApply == var2) {
               this.matrixFilter++;
               if (this.matrixFilter >= 5) {
                  this.process("Сундук заполнен или предмет не перекладывается.");
                  this.saveScale();
                  this.configCollapse = AutoVillageTrade.Mode.IDLE;
                  this.scaleAdapt.handle();
                  return;
               }
            } else {
               this.matrixFilter = 0;
            }

            int var3 = this.handle(var1);
            if (var3 == -1) {
               this.saveScale();
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            } else {
               this.stateApply = var2;
               Module.client.interactionManager
                  .clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var3, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.scaleAdapt.handle();
            }
         }
      }
   }

   private void fetchProvider() {
      BlockPos var1 = this.filterKey();
      if (var1 != null && !this.handle(var1, 1.5)) {
         this.handle(var1, 0);
      } else {
         this.collapseOutput();
      }

      if (this.scaleAdapt.update(this.receiveRequest())) {
         this.savePreset();
      }
   }

   private void drawProfile() {
      if (!this.validateData()) {
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
      } else if (!this.bindIndex()) {
         if (this.adaptScale()) {
            this.saveScale();
            this.scaleAdapt.handle();
         } else if (this.scaleAdapt.update(this.adaptRequest())) {
            Module.client.player.networkHandler.sendChatCommand("shop");
            this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_WAIT_SHOP;
            this.scaleAdapt.handle();
         }
      }
   }

   private void performVector() {
      if (!this.bindIndex()) {
         if (this.computeColor() != null) {
            this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_FIND_GOLD;
            this.scaleAdapt.handle();
         } else {
            if (this.scaleAdapt.update(10000L)) {
               this.process("Таймаут открытия /shop.");
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            }
         }
      }
   }

   private void attachEvent() {
      if (!this.bindIndex()) {
         if (this.scaleAdapt.update(this.adaptRequest())) {
            GenericContainerScreen var1 = this.computeColor();
            if (var1 == null) {
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            } else {
               int var2 = this.handle(var1, Items.GOLD_INGOT);
               if (var2 != -1) {
                  this.handle(var1, var2, 0, SlotActionType.PICKUP);
                  this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_WAIT_MENU;
                  this.scaleAdapt.handle();
               } else {
                  if (this.scaleAdapt.update(5000L)) {
                     this.process("В /shop не найден раздел золотого слитка.");
                     this.saveScale();
                     this.configCollapse = AutoVillageTrade.Mode.IDLE;
                     this.scaleAdapt.handle();
                  }
               }
            }
         }
      }
   }

   private void readServer() {
      if (!this.bindIndex()) {
         if (this.scaleAdapt.update(this.adaptRequest())) {
            if (this.computeColor() == null) {
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            } else {
               this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_FIND_EMERALD;
               this.scaleAdapt.handle();
            }
         }
      }
   }

   private void advancePosition() {
      if (!this.bindIndex()) {
         if (this.scaleAdapt.update(this.adaptRequest())) {
            GenericContainerScreen var1 = this.computeColor();
            if (var1 == null) {
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            } else {
               int var2 = this.process(var1);
               if (var2 != -1) {
                  this.handle(var1, var2, 1, SlotActionType.PICKUP);
                  this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_WAIT_CONFIRM;
                  this.scaleAdapt.handle();
               } else {
                  if (this.scaleAdapt.update(5000L)) {
                     this.process("В /shop не найден слот изумрудов.");
                     this.saveScale();
                     this.configCollapse = AutoVillageTrade.Mode.IDLE;
                     this.scaleAdapt.handle();
                  }
               }
            }
         }
      }
   }

   private void checkFrame() {
      if (!this.bindIndex()) {
         if (this.scaleAdapt.update(this.adaptRequest())) {
            if (this.computeColor() == null) {
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            } else {
               this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_CONFIRM;
               this.scaleAdapt.handle();
            }
         }
      }
   }

   private void collectModule() {
      if (!this.bindIndex()) {
         if (this.scaleAdapt.update(this.adaptRequest())) {
            GenericContainerScreen var1 = this.computeColor();
            if (var1 == null) {
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            } else {
               int var2 = this.handle(var1.getScreenHandler());
               if (var2 != -1) {
                  this.layerSample = this.process(Items.EMERALD);
                  this.handle(var1, var2, 0, SlotActionType.PICKUP);
                  this.configCollapse = AutoVillageTrade.Mode.BUY_EMERALDS_CLOSE;
                  this.scaleAdapt.handle();
               } else {
                  if (this.scaleAdapt.update(5000L)) {
                     this.process("Не найден слот подтверждения покупки изумрудов.");
                     this.saveScale();
                     this.configCollapse = AutoVillageTrade.Mode.IDLE;
                     this.scaleAdapt.handle();
                  }
               }
            }
         }
      }
   }

   private void closeProvider() {
      if (this.scaleAdapt.update(250L)) {
         this.saveScale();
         if (this.layerSample < 0 || this.process(Items.EMERALD) > this.layerSample) {
            this.layerSample = -1;
            if (scaleSave != null && this.process(this.refreshClient()) > 0) {
               this.measure();
            } else {
               this.configCollapse = AutoVillageTrade.Mode.IDLE;
               this.scaleAdapt.handle();
            }
         } else if (this.scaleAdapt.update(2500L)) {
            this.process("Покупка изумрудов не изменила инвентарь. Повтор временно остановлен.");
            this.layerSample = -1;
            this.worldSend = System.currentTimeMillis() + 10000L;
            this.configCollapse = AutoVillageTrade.Mode.IDLE;
            this.scaleAdapt.handle();
         }
      }
   }

   private void savePreset() {
      this.keyFilter++;
      this.clientRefresh = 0;
      this.dataValidate = null;
      this.scaleRender = -1;
      this.requestReceive = null;
      this.configCollapse = AutoVillageTrade.Mode.SCAN_ROUTE;
      this.scaleAdapt.handle();
      this.process("Сканирую жителей: " + this.source.compute() + ".");
   }

   private void convertWindow() {
      this.timerMeasure = System.currentTimeMillis();
      this.compute(false);
   }

   private boolean writePreset() {
      if (this.indexBind.isEmpty()) {
         return true;
      }

      long var1 = System.currentTimeMillis();
      return var1 - this.timerMeasure >= this.receiveRequest()
         ? true
         : this.indexBind.values().stream().anyMatch(var3 -> var3.selection && var1 - var3.enabled >= this.receiveRequest());
   }

   private AutoVillageTrade.AnimationState handle(UUID var1, TradeOfferList var2) {
      if (var1 != null && var2 != null) {
         AutoVillageTrade.AnimationState var3 = this.indexBind.computeIfAbsent(var1, AutoVillageTrade.AnimationState::new);
         VillagerEntity var4 = this.handle(var1);
         if (var4 != null) {
            var3.context = var4.getId();
            var3.data = var4.getBlockPos();
         }

         var3.config = this.keyFilter;
         var3.enabled = System.currentTimeMillis();
         var3.mode = false;
         var3.state = -1;
         var3.cache = Integer.MAX_VALUE;
         var3.output = 1;
         var3.selection = true;
         Item var5 = this.refreshClient();

         for (int var6 = 0; var6 < var2.size(); var6++) {
            TradeOffer var7 = (TradeOffer)var2.get(var6);
            ItemStack var8 = var7.getSellItem();
            if (!var8.isEmpty() && var8.isOf(var5)) {
               int var9 = this.handle(var7);
               int var10 = Math.max(1, var8.getCount());
               boolean var11 = !var3.mode || this.handle(var9, var10) < this.handle(var3.cache, var3.output);
               if (var11) {
                  var3.mode = true;
                  var3.state = var6;
                  var3.cache = var9;
                  var3.output = var10;
                  var3.current = var7.getUses();
                  var3.active = var7.getMaxUses();
                  var3.selection = var7.isDisabled() || var9 > this.measureTimer();
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   private int handle(TradeOffer var1) {
      int var2 = 0;
      ItemStack var3 = var1.getDisplayedFirstBuyItem();
      ItemStack var4 = var1.getDisplayedSecondBuyItem();
      if (!var3.isEmpty() && var3.isOf(Items.EMERALD)) {
         var2 += var3.getCount();
      }

      if (!var4.isEmpty() && var4.isOf(Items.EMERALD)) {
         var2 += var4.getCount();
      }

      return var2 <= 0 ? Integer.MAX_VALUE : var2;
   }

   private AutoVillageTrade.AnimationState measureColor() {
      int var1 = this.process(Items.EMERALD);
      boolean var2 = this.validateData();
      return this.indexBind
         .values()
         .stream()
         .filter(var1x -> var1x.mode && !var1x.selection && var1x.state >= 0 && var1x.cache <= this.measureTimer())
         .filter(var2x -> var1 >= var2x.cache || var2)
         .min(
            Comparator.<AutoVillageTrade.AnimationState>comparingDouble(var1x -> this.handle(var1x.cache, var1x.output))
               .thenComparingDouble(var1x -> this.prepare(var1x.data))
         )
         .orElse(null);
   }

   private VillagerEntity resolve(BlockPos var1) {
      VillagerEntity var2 = null;
      double var3 = Double.MAX_VALUE;
      double var5 = this.handle(this.presetWrite.compute());

      for (Entity var8 : Module.client.world.getEntities()) {
         if (var8 instanceof VillagerEntity var9 && var9.isAlive()) {
            AutoVillageTrade.AnimationState var10 = this.indexBind.get(var9.getUuid());
            if (var10 == null || var10.config != this.keyFilter) {
               double var11 = this.handle(var1.getX() + 0.5 - var9.getX())
                  + this.handle(var1.getY() + 0.5 - var9.getY())
                  + this.handle(var1.getZ() + 0.5 - var9.getZ());
               if (!(var11 > var5) && !(var11 >= var3)) {
                  var2 = var9;
                  var3 = var11;
               }
            }
         }
      }

      return var2;
   }

   private VillagerEntity handle(UUID var1) {
      if (var1 != null && Module.client.world != null) {
         for (Entity var3 : Module.client.world.getEntities()) {
            if (var3 instanceof VillagerEntity var4 && var1.equals(var4.getUuid())) {
               return var4;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private AutoVillageTrade.AnimationState scheduleAnimation() {
      return this.dataValidate == null ? null : this.indexBind.get(this.dataValidate);
   }

   private void scanRenderer() {
      if (this.dataValidate != null) {
         AutoVillageTrade.AnimationState var1 = this.indexBind.computeIfAbsent(this.dataValidate, AutoVillageTrade.AnimationState::new);
         var1.selection = true;
         var1.enabled = System.currentTimeMillis();
         var1.config = this.keyFilter;
      }
   }

   private void buildSource() {
      this.actionRead.clear();
      if (sourceSchedule != null && timerRender != null) {
         int var1 = timerRender.getX() - sourceSchedule.getX();
         int var2 = timerRender.getZ() - sourceSchedule.getZ();
         int var3 = Math.max(Math.abs(var1), Math.abs(var2));
         if (var3 == 0) {
            this.actionRead.add(sourceSchedule);
         } else {
            for (int var4 = 0; var4 <= var3; var4++) {
               int var5 = sourceSchedule.getX() + Math.round(var1 * ((float)var4 / var3));
               int var6 = sourceSchedule.getZ() + Math.round(var2 * ((float)var4 / var3));
               BlockPos var7 = new BlockPos(var5, sourceSchedule.getY(), var6);
               if (this.actionRead.isEmpty() || !this.actionRead.get(this.actionRead.size() - 1).equals(var7)) {
                  this.actionRead.add(var7);
               }
            }
         }
      }
   }

   private void handle(BlockPos var1, int var2) {
      IBaritone var3 = BaritoneAPI.getProvider().getPrimaryBaritone();
      if (!var1.equals(this.requestReceive) || var2 != this.windowProcess || !var3.getCustomGoalProcess().isActive()) {
         this.requestReceive = var1;
         this.windowProcess = var2;
         if (var2 > 0) {
            var3.getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, var2));
         } else {
            var3.getCustomGoalProcess().setGoalAndPath(new GoalBlock(var1));
         }
      }
   }

   private void collapseOutput() {
      this.requestReceive = null;
      this.windowProcess = -1;

      try {
         BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      } catch (Throwable var2) {
      }
   }

   private void invokeProfile() {
      this.packetSave = (Boolean)BaritoneAPI.getSettings().allowBreak.value;
      this.entryAnimate = (Boolean)BaritoneAPI.getSettings().allowPlace.value;
      BaritoneAPI.getSettings().allowBreak.value = false;
      BaritoneAPI.getSettings().allowPlace.value = false;
   }

   private void scheduleSource() {
      if (this.packetSave != null) {
         BaritoneAPI.getSettings().allowBreak.value = this.packetSave;
      }

      if (this.entryAnimate != null) {
         BaritoneAPI.getSettings().allowPlace.value = this.entryAnimate;
      }

      this.packetSave = null;
      this.entryAnimate = null;
   }

   private int handle(GenericContainerScreen var1, Item var2) {
      int var3 = this.compute(var1);

      for (int var4 = 0; var4 < var3; var4++) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (var5.hasStack() && var5.getStack().isOf(var2)) {
            return var4;
         }
      }

      return -1;
   }

   private int handle(GenericContainerScreen var1) {
      int var2 = this.compute(var1);
      DefaultedList var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).slots;

      for (int var4 = var2; var4 < var3.size(); var4++) {
         Slot var5 = (Slot)var3.get(var4);
         if (var5.hasStack() && var5.getStack().isOf(this.refreshClient())) {
            return var4;
         }
      }

      return -1;
   }

   private int process(GenericContainerScreen var1) {
      int var2 = this.compute(var1);

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (var4.hasStack()) {
            ItemStack var5 = var4.getStack();
            String var6 = this.handle(var5.getName().getString());
            if (var5.isOf(Items.EMERALD)) {
               return var3;
            }

            if (var5.isOf(Items.PAPER) && (var6.contains("изумруд") || var6.contains("emerald"))) {
               return var3;
            }
         }
      }

      return -1;
   }

   private int handle(ScreenHandler var1) {
      int var2 = Math.min(var1.slots.size(), Math.max(0, var1.slots.size() - 36));

      for (int var3 = var2 - 1; var3 >= 0; var3--) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         String var5 = this.handle(var4.getName().getString());
         if (var5.contains("купить")
            || var4.isOf(Items.LIME_STAINED_GLASS_PANE)
            || var4.isOf(Items.GREEN_STAINED_GLASS_PANE)
            || var4.isOf(Items.GREEN_CONCRETE)
            || var4.isOf(Items.LIME_CONCRETE)) {
            return var3;
         }
      }

      return -1;
   }

   private void handle(GenericContainerScreen var1, int var2, int var3, SlotActionType var4) {
      Module.client.interactionManager.clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2, var3, var4, Module.client.player);
   }

   private int compute(GenericContainerScreen var1) {
      int var2 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getRows();
      int var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size();
      return Math.max(0, Math.min(var2 * 9, var3));
   }

   private boolean update(BlockPos var1) {
      return Module.client.world != null && var1 != null
         ? Module.client.world.getBlockEntity(var1) instanceof ChestBlockEntity || Module.client.world.getBlockEntity(var1) instanceof BarrelBlockEntity
         : false;
   }

   private void apply(BlockPos var1) {
      if (Module.client.player != null && Module.client.interactionManager != null && var1 != null) {
         Direction var2 = this.execute(var1);
         Vec3d var3 = new Vec3d(
            var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5
         );
         BlockHitResult var4 = new BlockHitResult(var3, var2, var1, false);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var4);
      }
   }

   private Direction execute(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      Vec3d var3 = Module.client.player.getEyePos().subtract(var2);
      return Direction.getFacing(var3.x, var3.y, var3.z);
   }

   private boolean renderTimer() {
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
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1.getName().getString();
         return var1.getItem() == Items.TRIPWIRE_HOOK || var2.contains("[★]");
      } else {
         return false;
      }
   }

   private void saveScale() {
      if (Module.client.player != null && (Module.client.currentScreen != null || ContainerScreenPolicy.handle(Module.client))) {
         Module.client.player.closeHandledScreen();
      }

      this.targetWrite = null;
   }

   private GenericContainerScreen computeColor() {
      GenericContainerScreen var1 = ContainerScreenPolicy.handle(Module.client, this.targetWrite, GenericContainerScreen.class);
      if (var1 == null && this.targetWrite instanceof GenericContainerScreen) {
         this.targetWrite = null;
      }

      return var1;
   }

   private boolean adaptScale() {
      return ContainerScreenPolicy.process(Module.client, this.targetWrite) || ContainerScreenPolicy.handle(Module.client);
   }

   private boolean handle(Screen var1) {
      return !(var1 instanceof GenericContainerScreen) && !(var1 instanceof MerchantScreen)
         ? false
         : this.configCollapse != AutoVillageTrade.Mode.IDLE
            && this.configCollapse != AutoVillageTrade.Mode.WAIT_RESTOCK
            && this.configCollapse != AutoVillageTrade.Mode.SCAN_ROUTE
            && this.configCollapse != AutoVillageTrade.Mode.MOVE_TO_TRADE
            && this.configCollapse != AutoVillageTrade.Mode.MOVE_TO_STORAGE;
   }

   private boolean runTexture() {
      return scaleSave != null && this.process(this.refreshClient()) > 0 && !this.compute(this.refreshClient());
   }

   private boolean bindIndex() {
      if (this.readAction()) {
         return false;
      } else if (scaleSave != null && this.process(this.refreshClient()) > 0) {
         this.saveScale();
         this.measure();
         return true;
      } else {
         this.process("Недостаточно места для покупки изумрудов.");
         this.worldSend = System.currentTimeMillis() + 10000L;
         this.saveScale();
         this.configCollapse = AutoVillageTrade.Mode.IDLE;
         this.scaleAdapt.handle();
         return true;
      }
   }

   private boolean readAction() {
      return this.handle(Items.EMERALD) >= 64;
   }

   private boolean collapseConfig() {
      return this.renderScale() ? false : this.process(Items.EMERALD) < this.encodeVector();
   }

   private boolean validateData() {
      return this.rendererScan.compute() && !this.renderScale();
   }

   private boolean renderScale() {
      return System.currentTimeMillis() < this.worldSend;
   }

   private int handle(Item var1) {
      if (Module.client.player == null) {
         return 0;
      }

      int var2 = 0;
      int var3 = new ItemStack(var1).getMaxCount();

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = Module.client.player.getInventory().getStack(var4);
         if (var5.isEmpty()) {
            var2 += var3;
         } else if (var5.isOf(var1)) {
            var2 += Math.max(0, var5.getMaxCount() - var5.getCount());
         }
      }

      return var2;
   }

   private int process(Item var1) {
      if (Module.client.player == null) {
         return 0;
      }

      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && var4.isOf(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private boolean compute(Item var1) {
      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.isEmpty()) {
            return true;
         }

         if (var3.isOf(var1) && var3.getCount() < var3.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private Item refreshClient() {
      return switch (this.source.compute()) {
         case "Редстоун" -> Items.REDSTONE;
         case "Лазурит" -> Items.LAPIS_LAZULI;
         case "Жемчуг Эндера" -> Items.ENDER_PEARL;
         case "Бутылочка опыта" -> Items.EXPERIENCE_BOTTLE;
         case "Стекло" -> Items.GLASS;
         case "Бирка" -> Items.NAME_TAG;
         case "Стрелы" -> Items.ARROW;
         case "Хлеб" -> Items.BREAD;
         case "Золотая морковь" -> Items.GOLDEN_CARROT;
         case "Кварцевый блок" -> Items.QUARTZ_BLOCK;
         case "Седло" -> Items.SADDLE;
         default -> Items.GOLD_INGOT;
      };
   }

   private void compute(boolean var1) {
      long var2 = System.currentTimeMillis();
      if (var1 || var2 - this.vectorEncode >= 1000L) {
         this.vectorEncode = var2;
         List<AutoVillageTrade.AnimationState> var4 = this.indexBind
            .values()
            .stream()
            .filter(var0 -> var0.mode)
            .sorted(
               Comparator.<AutoVillageTrade.AnimationState>comparingDouble(var1x -> this.handle(var1x.cache, var1x.output))
                  .thenComparingInt(var0 -> var0.context)
            )
            .toList();
         if (var4.isEmpty()) {
            this.process("Скан завершен: подходящих сделок нет.");
         } else {
            AutoVillageTrade.AnimationState var5 = (AutoVillageTrade.AnimationState)var4.get(0);
            long var6 = var4.stream().filter(var1x -> !var1x.selection && var1x.cache <= this.measureTimer()).count();
            this.process(
               "Скан завершен: найдено " + var4.size() + ", доступно сейчас " + var6 + ", лучший курс " + var5.cache + " изумр. за " + var5.output + " шт."
            );
         }
      }
   }

   private RotationAngles handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)Math.toDegrees(Math.atan2(-var3, var7));
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      return new RotationAngles(var11, MathHelper.clamp(var12, -90.0F, 90.0F));
   }

   private boolean handle(BlockPos var1, double var2) {
      return this.prepare(var1) <= var2 * var2;
   }

   private double prepare(BlockPos var1) {
      return Module.client.player != null && var1 != null
         ? Module.client.player.getPos().squaredDistanceTo(var1.getX() + 0.5, var1.getY(), var1.getZ() + 0.5)
         : Double.MAX_VALUE;
   }

   private BlockPos filterKey() {
      return sourceSchedule != null && timerRender != null
         ? new BlockPos((sourceSchedule.getX() + timerRender.getX()) / 2, sourceSchedule.getY(), (sourceSchedule.getZ() + timerRender.getZ()) / 2)
         : sourceSchedule;
   }

   private double handle(int var1, int var2) {
      return (double)var1 / Math.max(1, var2);
   }

   private double handle(double var1) {
      return var1 * var1;
   }

   private long adaptRequest() {
      return Math.max(50L, Math.round(this.colorMeasure.compute()));
   }

   private int measureTimer() {
      return Math.max(1, Math.round(this.presetSave.compute()));
   }

   private int encodeVector() {
      return Math.max(0, Math.round(this.windowConvert.compute()));
   }

   private long receiveRequest() {
      return Math.max(1000L, Math.round(this.animationSchedule.compute() * 1000.0F));
   }

   private String handle(String var1) {
      return var1 == null ? "" : var1.toLowerCase(Locale.ROOT).replace("§", "");
   }

   private void resolve(boolean var1) {
      this.configCollapse = AutoVillageTrade.Mode.IDLE;
      this.dataValidate = null;
      this.scaleRender = -1;
      this.clientRefresh = 0;
      this.requestReceive = null;
      this.windowProcess = -1;
      this.stateApply = -1;
      this.matrixFilter = 0;
      this.layerSample = -1;
      this.worldSend = 0L;
      this.targetWrite = null;
      if (var1) {
         this.actionRead.clear();
      }

      this.scaleAdapt.handle();
   }

   private void process(String var1) {
      ChatLogger.handle("§8[§aAutoVillageTrade§8] §f" + var1);
   }
   public static BlockPos refresh() {
      return sourceSchedule;
   }
   public static void handle(BlockPos var0) {
      sourceSchedule = var0;
   }
   public static BlockPos render() {
      return timerRender;
   }
   public static void process(BlockPos var0) {
      timerRender = var0;
   }
   public static BlockPos tick() {
      return scaleSave;
   }
   public static void compute(BlockPos var0) {
      scaleSave = var0;
   }

   static final class AnimationState {
      final UUID instance;
      BlockPos data;
      int context;
      int config = -1;
      int state = -1;
      int cache = Integer.MAX_VALUE;
      int output = 1;
      int current;
      int active;
      boolean mode;
      boolean selection = true;
      long enabled;
      long renderer;
      int handler;
      int animationDraw;

      private AnimationState(UUID var1) {
         this.instance = var1;
      }
   }

   public record DataRecord(ItemStack itemStack, int price, int itemCount, int availableAmount, boolean ready) {
   }

   enum Mode {
      IDLE,
      SCAN_ROUTE,
      OPEN_SCAN,
      WAIT_SCAN_SCREEN,
      READ_SCAN_SCREEN,
      CLOSE_SCAN_SCREEN,
      MOVE_TO_TRADE,
      OPEN_TRADE,
      WAIT_TRADE_SCREEN,
      BUY_TRADE,
      CLOSE_TRADE_SCREEN,
      MOVE_TO_STORAGE,
      OPEN_STORAGE,
      WAIT_STORAGE_SCREEN,
      PUT_STORAGE,
      BUY_EMERALDS_OPEN_SHOP,
      BUY_EMERALDS_WAIT_SHOP,
      BUY_EMERALDS_FIND_GOLD,
      BUY_EMERALDS_WAIT_MENU,
      BUY_EMERALDS_FIND_EMERALD,
      BUY_EMERALDS_WAIT_CONFIRM,
      BUY_EMERALDS_CONFIRM,
      BUY_EMERALDS_CLOSE,
      WAIT_RESTOCK;
   }
}
