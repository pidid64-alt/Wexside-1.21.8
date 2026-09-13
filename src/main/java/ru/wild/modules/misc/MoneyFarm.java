package ru.wild.modules.misc;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "MoneyFarm", category = ModuleCategory.Misc, description = "Крафтит и продает изумрудные предметы")
public class MoneyFarm extends Module {
   private static final String source = "Изумрудный меч";
   private static final String target = "Изумрудная кирка";
   private static final String pending = "Изумрудный топор";
   private static final int previous = 5;
   private static final long latest = 1200L;
   private static final long summary = 7000L;
   private static final int matrixBlend = 9;
   private static final int vectorMatch = 20;
   private static final Pattern itemProject = Pattern.compile("(\\d[\\d\\s.,]*\\d|\\d)");
   private final ModeSetting responseCompute = new ModeSetting("Предмет", "Изумрудный меч", "Изумрудный меч", "Изумрудная кирка", "Изумрудный топор");
   private final StringSetting providerFetch = new StringSetting("Цена продажи", "40000").handle(32);
   private final NumberSetting profileDraw = new NumberSetting("Задержка (мс)", 100.0F, 50.0F, 5000.0F, 50.0F, false);
   private final BooleanSetting vectorPerform = new BooleanSetting("Авто-покупка", true);
   private final BooleanSetting eventAttach = new BooleanSetting("Авто-продажа", false);
   private final NumberSetting serverRead = new NumberSetting("Перевыставить (сек)", 30.0F, 5.0F, 120.0F, 1.0F, false)
      .handle(() -> !this.eventAttach.compute());
   private final NumberSetting positionAdvance = new NumberSetting("Слоты аукциона", 5.0F, 1.0F, 50.0F, 1.0F, false).handle(() -> !this.eventAttach.compute());
   private final BooleanSetting frameCheck = new BooleanSetting("Уведомления", true);
   private MoneyFarm.Mode moduleCollect = MoneyFarm.Mode.IDLE;
   private final Stopwatch providerClose = new Stopwatch();
   private final Stopwatch presetSave = new Stopwatch();
   private BlockPos windowConvert;
   private int presetWrite = 0;
   private int colorMeasure = 0;
   private int animationSchedule = 0;
   private int rendererScan = 0;
   private int sourceBuild = 0;
   private boolean outputCollapse = false;
   private boolean profileInvoke = false;
   private boolean sourceSchedule = false;
   private boolean timerRender = false;
   private boolean scaleSave = false;
   private int colorCompute = 0;
   private int scaleAdapt = 0;
   private int textureRun = 0;
   private int indexBind = 0;
   private boolean actionRead = false;
   private int configCollapse = 0;
   private int dataValidate = -1;
   private long scaleRender = 0L;
   private long clientRefresh = 0L;
   private long keyFilter = 0L;
   private int requestAdapt = 0;

   public MoneyFarm() {
      this.handle(
         this.responseCompute,
         this.providerFetch,
         this.serverRead,
         this.positionAdvance,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach,
         this.frameCheck
      );
   }

   @Override
   public void handle() {
      super.handle();
      this.filterKey();
   }

   @Override
   public void process() {
      this.filterKey();
      super.process();
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && var1.update().equals(PacketEvent.Mode.RECEIVE)) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var6 = var2.content().getString();
            String var4 = this.resolve(var6);
            if (this.moduleCollect != MoneyFarm.Mode.BUY_FIND_EMERALD
                  && this.moduleCollect != MoneyFarm.Mode.BUY_WAITING_CONFIRM
                  && this.moduleCollect != MoneyFarm.Mode.BUY_CLICK_LIME_PANE
               || !var4.contains("недостаточно") && !var4.contains("не хватает") && !var4.contains("нет монет") && !var4.contains("нет денег")) {
               if (var4.contains("не удалось выставить") && var4.contains("освободите хранилище")) {
                  this.outputCollapse = true;
                  this.profileInvoke = false;
                  this.timerRender = true;
               } else {
                  if (var4.contains("у вас купили") && var4.contains(this.resolve(this.adaptScale()))) {
                     this.sourceSchedule = true;
                     this.textureRun++;
                     this.indexBind = Math.max(0, this.indexBind - 1);
                     this.animationSchedule = 0;
                     this.colorMeasure = 0;
                     this.actionRead = false;
                     this.clientRefresh = System.currentTimeMillis();
                  }

                  if ((var4.contains("выставлен") || var4.contains("выставлено") || var4.contains("успешно выстав")) && var4.contains("продаж")) {
                     int var5 = this.scaleAdapt > 0 ? this.scaleAdapt : 1;
                     this.outputCollapse = false;
                     this.profileInvoke = true;
                     this.rendererScan += var5;
                     this.indexBind += var5;
                     this.textureRun = Math.max(0, this.textureRun - var5);
                     this.actionRead = true;
                     this.scaleAdapt = 0;
                     this.clientRefresh = System.currentTimeMillis();
                  }
               }
            } else {
               this.measureTimer();
               this.compute("§cНе хватает монет для покупки.");
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         this.processWindow();
         this.encodeVector();
         this.refreshClient();
         if (this.outputCollapse && this.moduleCollect == MoneyFarm.Mode.IDLE) {
            this.convertWindow();
         }

         switch (this.moduleCollect) {
            case IDLE:
               this.refresh();
               break;
            case BUY_OPENING_SHOP:
               this.render();
               break;
            case BUY_WAITING_SHOP:
               this.tick();
               break;
            case BUY_FIND_GOLD_INGOT:
               this.drawAnimation();
               break;
            case BUY_WAITING_EMERALD_MENU:
               this.encodePoint();
               break;
            case BUY_FIND_EMERALD:
               this.animate();
               break;
            case BUY_WAITING_CONFIRM:
               this.load();
               break;
            case BUY_CLICK_LIME_PANE:
               this.save();
               break;
            case BUY_CLOSING_SHOP:
               this.submit();
               break;
            case CHECK_SELL_GUI_OPENING:
               this.unload();
               break;
            case CHECK_SELL_GUI_WAITING:
               this.fetch();
               break;
            case CHECK_SELL_GUI_READING:
               this.measure();
               break;
            case FINDING_CRAFTING_TABLE:
               this.blendMatrix();
               break;
            case AIMING_CRAFTING_TABLE:
               this.matchVector();
               break;
            case OPENING_CRAFTING_TABLE:
               this.projectItem();
               break;
            case PLACING_ITEMS:
               this.computeResponse();
               break;
            case TAKING_RESULT:
               this.fetchProvider();
               break;
            case CLOSING_CRAFTING:
               this.drawProfile();
               break;
            case SELLING:
               this.performVector();
               break;
            case WAITING_SELL_RESULT:
               this.attachEvent();
               break;
            case RESALE_SEARCH_OWN_AH:
               this.readServer();
               break;
            case RESALE_WAITING_OWN_AH:
               this.advancePosition();
               break;
            case RESALE_TAKE_ITEM:
               this.checkFrame();
               break;
            case RESALE_CLOSING:
               this.collectModule();
               break;
            case RESALE_SELLING:
               this.closeProvider();
               break;
            case RESALE_WAIT_SELL_RESULT:
               this.savePreset();
         }
      }
   }

   private void refresh() {
      if (this.providerClose.update(this.renderScale())) {
         if (this.outputCollapse) {
            this.convertWindow();
         } else {
            if (this.eventAttach.compute() && this.textureRun > 0) {
               this.sourceSchedule = false;
               if (!this.actionRead) {
                  this.moduleCollect = MoneyFarm.Mode.CHECK_SELL_GUI_OPENING;
                  this.providerClose.handle();
                  return;
               }

               if (this.writePreset() > 0 && this.scheduleSource() >= this.writePreset()) {
                  this.moduleCollect = MoneyFarm.Mode.SELLING;
                  this.providerClose.handle();
                  return;
               }
            } else {
               if (this.eventAttach.compute() && this.indexBind > 0 && System.currentTimeMillis() - this.clientRefresh >= this.validateData()) {
                  this.convertWindow();
                  return;
               }

               if (this.eventAttach.compute() && this.indexBind > 0) {
                  return;
               }

               if (this.eventAttach.compute() && !this.actionRead) {
                  this.moduleCollect = MoneyFarm.Mode.CHECK_SELL_GUI_OPENING;
                  this.providerClose.handle();
                  return;
               }
            }

            if (this.eventAttach.compute() && this.indexBind == 0 && this.writePreset() > 0 && this.scheduleSource() >= this.writePreset()) {
               this.moduleCollect = MoneyFarm.Mode.SELLING;
               this.providerClose.handle();
            } else if (!this.eventAttach.compute() || !this.actionRead || this.writePreset() > 0) {
               if (this.eventAttach.compute() && this.actionRead && this.writePreset() > 0 && this.scheduleSource() >= this.writePreset()) {
                  if (this.scheduleSource() > 0) {
                     this.moduleCollect = MoneyFarm.Mode.SELLING;
                  }

                  this.providerClose.handle();
               } else if (this.vectorPerform.compute() && this.scheduleAnimation()) {
                  this.moduleCollect = MoneyFarm.Mode.BUY_OPENING_SHOP;
                  this.providerClose.handle();
               } else if (this.scanRenderer()) {
                  this.compute("§cНет палок в инвентаре. Положите палки для крафта.");
               } else if (this.measureColor()) {
                  this.moduleCollect = MoneyFarm.Mode.FINDING_CRAFTING_TABLE;
                  this.providerClose.handle();
               }
            }
         }
      }
   }

   private void render() {
      if (this.providerClose.update(this.renderScale())) {
         if (this.process(150L)) {
            Module.client.player.networkHandler.sendChatCommand("shop");
            this.moduleCollect = MoneyFarm.Mode.BUY_WAITING_SHOP;
            this.providerClose.handle();
         }
      }
   }

   private void tick() {
      if (Module.client.currentScreen instanceof GenericContainerScreen) {
         this.moduleCollect = MoneyFarm.Mode.BUY_FIND_GOLD_INGOT;
         this.providerClose.handle();
      } else {
         if (this.providerClose.update(10000L)) {
            this.compute("§cТаймаут магазина.");
         }
      }
   }

   private void drawAnimation() {
      if (this.providerClose.update(this.renderScale())) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            int var3 = this.handle(var1, Items.GOLD_INGOT);
            if (var3 != -1) {
               this.handle(var1, var3, 0, SlotActionType.PICKUP);
               this.moduleCollect = MoneyFarm.Mode.BUY_WAITING_EMERALD_MENU;
               this.providerClose.handle();
            } else {
               if (this.providerClose.update(5000L)) {
                  this.measureTimer();
                  this.compute("§cЗолотой слиток не найден.");
               }
            }
         } else {
            this.adaptRequest();
         }
      }
   }

   private void encodePoint() {
      if (this.providerClose.update(this.renderScale())) {
         if (!(Module.client.currentScreen instanceof GenericContainerScreen)) {
            this.adaptRequest();
         } else {
            this.moduleCollect = MoneyFarm.Mode.BUY_FIND_EMERALD;
            this.providerClose.handle();
         }
      }
   }

   private void animate() {
      if (this.providerClose.update(this.renderScale())) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            int var3 = this.apply(var1);
            if (var3 != -1) {
               this.handle(var1, var3, 1, SlotActionType.PICKUP);
               this.moduleCollect = MoneyFarm.Mode.BUY_WAITING_CONFIRM;
               this.providerClose.handle();
            } else {
               if (this.providerClose.update(5000L)) {
                  this.measureTimer();
                  this.compute("§cИзумруд не найден.");
               }
            }
         } else {
            this.adaptRequest();
         }
      }
   }

   private void load() {
      if (this.providerClose.update(this.renderScale())) {
         if (!this.scheduleAnimation()) {
            this.moduleCollect = MoneyFarm.Mode.BUY_CLOSING_SHOP;
            this.providerClose.handle();
         } else if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (this.execute(var1)) {
               this.moduleCollect = MoneyFarm.Mode.BUY_CLICK_LIME_PANE;
               this.providerClose.handle();
            } else {
               if (this.providerClose.update(5000L)) {
                  this.measureTimer();
                  this.compute("§cПодтверждение покупки изумрудов не открылось.");
               }
            }
         } else {
            this.adaptRequest();
         }
      }
   }

   private void save() {
      if (this.providerClose.update(this.renderScale())) {
         if (!this.scheduleAnimation()) {
            this.moduleCollect = MoneyFarm.Mode.BUY_CLOSING_SHOP;
            this.providerClose.handle();
         } else if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (!this.execute(var1)) {
               this.moduleCollect = MoneyFarm.Mode.BUY_WAITING_CONFIRM;
               this.providerClose.handle();
            } else {
               int var3 = this.handle(var1.getScreenHandler());
               if (var3 != -1) {
                  this.handle(var1, var3, 0, SlotActionType.PICKUP);
                  this.moduleCollect = MoneyFarm.Mode.BUY_CLOSING_SHOP;
                  this.providerClose.handle();
               } else {
                  if (this.providerClose.update(5000L)) {
                     this.measureTimer();
                     this.compute("§cЛаймовая панель не найдена.");
                  }
               }
            }
         } else {
            this.adaptRequest();
         }
      }
   }

   private void submit() {
      if (this.process(150L)) {
         this.adaptRequest();
      }
   }

   private void unload() {
      if (this.providerClose.update(50L)) {
         if (this.process(Module.client.currentScreen)) {
            if (handle(Module.client.currentScreen)) {
               Module.client.setScreen(null);
            }

            long var1 = this.readAction();
            if (var1 <= 0L) {
               this.compute("§cЦена продажи не задана.");
            } else if (this.saveScale()) {
               this.computeColor();
               this.handle(var1);
               this.moduleCollect = MoneyFarm.Mode.CHECK_SELL_GUI_WAITING;
               this.providerClose.handle();
            }
         }
      }
   }

   private void fetch() {
      if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.prepare(var1)) {
         this.moduleCollect = MoneyFarm.Mode.CHECK_SELL_GUI_READING;
         this.providerClose.handle();
      } else {
         if (this.providerClose.update(7000L)) {
            this.measureTimer();
            this.compute("§cSellgui не открылся для проверки слотов.");
         }
      }
   }

   private void measure() {
      if (this.providerClose.update(150L)) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.prepare(var1)) {
            int var3 = this.compute(var1);
            this.animationSchedule = Math.min(var3, this.collapseConfig());
            if (this.textureRun > 0) {
               this.textureRun = Math.min(this.textureRun, this.animationSchedule);
            }

            this.actionRead = true;
            this.colorMeasure = 0;
            this.measureTimer();
            if (var3 <= 0) {
               this.convertWindow();
            } else {
               this.moduleCollect = MoneyFarm.Mode.IDLE;
               this.providerClose.handle();
            }
         } else {
            this.adaptRequest();
         }
      }
   }

   private void blendMatrix() {
      if (this.process(150L)) {
         if (this.providerClose.update(this.renderScale())) {
            this.windowConvert = this.renderTimer();
            if (this.windowConvert == null) {
               this.compute("§cВерстак рядом не найден.");
            } else {
               this.requestAdapt = 0;
               this.moduleCollect = MoneyFarm.Mode.AIMING_CRAFTING_TABLE;
               this.providerClose.handle();
            }
         }
      }
   }

   private void matchVector() {
      if (this.windowConvert != null && this.handle(this.windowConvert)) {
         if (Module.client.player.squaredDistanceTo(Vec3d.ofCenter(this.windowConvert)) > 36.0) {
            this.windowConvert = this.renderTimer();
            if (this.windowConvert == null) {
               this.moduleCollect = MoneyFarm.Mode.FINDING_CRAFTING_TABLE;
               this.providerClose.handle();
            } else {
               this.providerClose.handle();
               this.requestAdapt = 0;
            }
         } else if (this.providerClose.update(120L)) {
            if (!this.handle(this.windowConvert, 10.0F)) {
               if (this.providerClose.update(1200L)) {
                  this.process(this.windowConvert);
               }

               if (this.providerClose.update(1800L)) {
                  this.requestAdapt++;
                  if (this.requestAdapt > 2) {
                     this.windowConvert = this.renderTimer();
                     this.moduleCollect = MoneyFarm.Mode.FINDING_CRAFTING_TABLE;
                     this.providerClose.handle();
                     return;
                  }

                  this.providerClose.handle();
               }
            } else {
               Direction var1 = this.compute(this.windowConvert);
               Vec3d var2 = Vec3d.ofCenter(this.windowConvert).add(Vec3d.of(var1.getVector()).multiply(0.5));
               double var3 = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;
               double var5 = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;
               if (var1.getAxis() != Axis.Y) {
                  var2 = var2.add(var3 * 0.1, 0.0, var5 * 0.1);
               }

               BlockHitResult var7 = new BlockHitResult(var2, var1, this.windowConvert, false);
               Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var7);
               Module.client.player.swingHand(Hand.MAIN_HAND);
               if (this.requestAdapt == 0) {
                  this.providerClose.handle();
               }

               this.moduleCollect = MoneyFarm.Mode.OPENING_CRAFTING_TABLE;
               this.providerClose.handle();
            }
         }
      } else {
         this.moduleCollect = MoneyFarm.Mode.FINDING_CRAFTING_TABLE;
         this.providerClose.handle();
      }
   }

   private void projectItem() {
      if (Module.client.currentScreen instanceof CraftingScreen) {
         this.moduleCollect = MoneyFarm.Mode.PLACING_ITEMS;
         this.providerClose.handle();
      } else if (this.providerClose.update(80L)) {
         if (this.providerClose.update(700L) && this.handle(this.windowConvert, 12.0F)) {
            Direction var1 = this.compute(this.windowConvert);
            Vec3d var2 = Vec3d.ofCenter(this.windowConvert).add(Vec3d.of(var1.getVector()).multiply(0.5));
            BlockHitResult var3 = new BlockHitResult(var2, var1, this.windowConvert, false);
            Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var3);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.providerClose.handle();
         } else {
            if (this.providerClose.update(3500L)) {
               this.windowConvert = null;
               this.moduleCollect = MoneyFarm.Mode.FINDING_CRAFTING_TABLE;
               this.providerClose.handle();
            }
         }
      }
   }

   private void computeResponse() {
      if (this.providerClose.update(50L)) {
         if (Module.client.currentScreen instanceof CraftingScreen var1) {
            CraftingScreenHandler var4 = (CraftingScreenHandler)var1.getScreenHandler();
            int var3 = var4.syncId;
            if (this.runTexture()) {
               this.handle(var4, var3, Items.EMERALD, 2);
               this.handle(var4, var3, Items.EMERALD, 5);
               this.handle(var4, var3, Items.STICK, 8);
            } else if (this.bindIndex()) {
               this.handle(var4, var3, Items.EMERALD, 1);
               this.handle(var4, var3, Items.EMERALD, 2);
               this.handle(var4, var3, Items.EMERALD, 4);
               this.handle(var4, var3, Items.STICK, 5);
               this.handle(var4, var3, Items.STICK, 8);
            } else {
               this.handle(var4, var3, Items.EMERALD, 1);
               this.handle(var4, var3, Items.EMERALD, 2);
               this.handle(var4, var3, Items.EMERALD, 3);
               this.handle(var4, var3, Items.STICK, 5);
               this.handle(var4, var3, Items.STICK, 8);
            }

            this.moduleCollect = MoneyFarm.Mode.TAKING_RESULT;
            this.providerClose.handle();
         } else {
            this.adaptRequest();
         }
      }
   }

   private void fetchProvider() {
      if (this.providerClose.update(50L)) {
         if (Module.client.currentScreen instanceof CraftingScreen var1) {
            Module.client.interactionManager
               .clickSlot(((CraftingScreenHandler)var1.getScreenHandler()).syncId, 0, 0, SlotActionType.QUICK_MOVE, Module.client.player);
            this.presetWrite++;
            this.colorMeasure++;
            this.moduleCollect = MoneyFarm.Mode.CLOSING_CRAFTING;
            this.providerClose.handle();
         } else {
            this.adaptRequest();
         }
      }
   }

   private void drawProfile() {
      if (this.providerClose.update(50L)) {
         this.measureTimer();
         if (this.eventAttach.compute()) {
            int var1 = this.writePreset();
            int var2 = this.scheduleSource();
            if (var1 > 0 && var2 < var1) {
               this.moduleCollect = this.measureColor() ? MoneyFarm.Mode.FINDING_CRAFTING_TABLE : MoneyFarm.Mode.IDLE;
            } else {
               this.moduleCollect = MoneyFarm.Mode.SELLING;
            }
         } else {
            this.moduleCollect = MoneyFarm.Mode.IDLE;
         }

         this.providerClose.handle();
      }
   }

   private void performVector() {
      if (this.providerClose.update(50L)) {
         if (this.outputCollapse) {
            this.convertWindow();
         } else {
            long var1 = this.readAction();
            if (var1 <= 0L) {
               this.compute("§cЦена продажи не задана.");
            } else if (!this.invokeProfile()) {
               this.moduleCollect = MoneyFarm.Mode.IDLE;
               this.providerClose.handle();
            } else if (Module.client.currentScreen instanceof GenericContainerScreen var3 && this.prepare(var3)) {
               this.handle(var3, false);
            } else if (handle(Module.client.currentScreen)) {
               Module.client.setScreen(null);
               this.providerClose.handle();
            } else if (Module.client.currentScreen != null) {
               this.measureTimer();
               this.providerClose.handle();
            } else if (this.saveScale()) {
               this.computeColor();
               this.profileInvoke = false;
               this.outputCollapse = false;
               this.handle(var1);
               this.moduleCollect = MoneyFarm.Mode.WAITING_SELL_RESULT;
               this.providerClose.handle();
            }
         }
      }
   }

   private void attachEvent() {
      if (this.outputCollapse) {
         this.convertWindow();
      } else if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.prepare(var1)) {
         this.handle(var1, false);
      } else if (this.profileInvoke) {
         this.profileInvoke = false;
         this.colorMeasure = 0;
         this.moduleCollect = MoneyFarm.Mode.IDLE;
         this.providerClose.handle();
      } else {
         if (this.providerClose.update(7000L)) {
            this.measureTimer();
            this.moduleCollect = this.invokeProfile() ? MoneyFarm.Mode.SELLING : MoneyFarm.Mode.IDLE;
            this.providerClose.handle();
         }
      }
   }

   private void readServer() {
      if (this.providerClose.update(this.renderScale())) {
         if (handle(Module.client.currentScreen)) {
            Module.client.setScreen(null);
         } else if (Module.client.currentScreen != null) {
            return;
         }

         if (!this.timerRender && this.invokeProfile()) {
            this.moduleCollect = MoneyFarm.Mode.RESALE_SELLING;
            this.providerClose.handle();
         } else {
            this.timerRender = false;
            String var1 = Module.client.player.getName().getString();
            this.handle(var1);
            this.sourceBuild = 0;
            this.moduleCollect = MoneyFarm.Mode.RESALE_WAITING_OWN_AH;
            this.providerClose.handle();
         }
      }
   }

   private void advancePosition() {
      if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.onTick(var1)) {
         this.moduleCollect = MoneyFarm.Mode.RESALE_TAKE_ITEM;
         this.providerClose.handle();
      } else {
         if (this.providerClose.update(10000L)) {
            this.compute("§cТаймаут поиска своих товаров.");
         }
      }
   }

   private void checkFrame() {
      if (this.providerClose.update(200L)) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (!this.onTick(var1)) {
               if (this.providerClose.update(10000L)) {
                  this.measureTimer();
                  this.moduleCollect = MoneyFarm.Mode.RESALE_SEARCH_OWN_AH;
                  this.providerClose.handle();
               }
            } else {
               int var3 = this.update(var1);
               if (var3 != -1) {
                  this.handle(var1, var3, 0, SlotActionType.QUICK_MOVE);
                  this.sourceBuild = 0;
                  this.providerClose.handle();
               } else if (this.sourceBuild++ < 2) {
                  this.providerClose.handle();
               } else {
                  this.measureTimer();
                  if (this.invokeProfile()) {
                     this.indexBind = 0;
                     this.moduleCollect = MoneyFarm.Mode.RESALE_SELLING;
                  } else {
                     this.outputCollapse = false;
                     this.rendererScan = 0;
                     this.indexBind = 0;
                     this.moduleCollect = MoneyFarm.Mode.IDLE;
                  }

                  this.providerClose.handle();
               }
            }
         } else {
            this.moduleCollect = MoneyFarm.Mode.RESALE_SEARCH_OWN_AH;
            this.providerClose.handle();
         }
      }
   }

   private int handle(GenericContainerScreen var1) {
      int var2 = 0;
      int var3 = this.check(var1);

      for (int var4 = 0; var4 < var3; var4++) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (var5.hasStack() && this.handle(var5.getStack(), true)) {
            var2++;
         }
      }

      return var2;
   }

   private void collectModule() {
      if (this.providerClose.update(300L)) {
         this.measureTimer();
         this.moduleCollect = MoneyFarm.Mode.RESALE_SELLING;
         this.providerClose.handle();
      }
   }

   private void closeProvider() {
      if (this.providerClose.update(50L)) {
         if (this.outputCollapse) {
            this.convertWindow();
         } else {
            long var1 = this.readAction();
            if (var1 <= 0L) {
               this.compute("§cЦена продажи не задана.");
            } else if (!this.invokeProfile()) {
               this.moduleCollect = MoneyFarm.Mode.RESALE_SEARCH_OWN_AH;
               this.providerClose.handle();
            } else if (Module.client.currentScreen instanceof GenericContainerScreen var3 && this.prepare(var3)) {
               this.handle(var3, true);
            } else if (handle(Module.client.currentScreen)) {
               Module.client.setScreen(null);
               this.providerClose.handle();
            } else if (Module.client.currentScreen != null) {
               this.measureTimer();
               this.providerClose.handle();
            } else if (this.saveScale()) {
               this.computeColor();
               this.profileInvoke = false;
               this.outputCollapse = false;
               this.handle(var1);
               this.moduleCollect = MoneyFarm.Mode.RESALE_WAIT_SELL_RESULT;
               this.providerClose.handle();
            }
         }
      }
   }

   private void savePreset() {
      if (this.sourceSchedule) {
         this.sourceSchedule = false;
      }

      if (this.outputCollapse) {
         this.convertWindow();
      } else if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.prepare(var1)) {
         this.handle(var1, true);
      } else if (this.profileInvoke) {
         this.profileInvoke = false;
         this.rendererScan = 0;
         this.moduleCollect = MoneyFarm.Mode.IDLE;
         this.providerClose.handle();
      } else {
         if (this.providerClose.update(7000L)) {
            this.measureTimer();
            this.moduleCollect = this.invokeProfile() ? MoneyFarm.Mode.RESALE_SELLING : MoneyFarm.Mode.IDLE;
            this.providerClose.handle();
         }
      }
   }

   private void convertWindow() {
      this.moduleCollect = MoneyFarm.Mode.RESALE_SEARCH_OWN_AH;
      this.outputCollapse = false;
      this.profileInvoke = false;
      this.timerRender = true;
      this.colorMeasure = 0;
      this.animationSchedule = 0;
      this.indexBind = 0;
      this.textureRun = 0;
      this.actionRead = false;
      this.providerClose.handle();
   }

   private void handle(CraftingScreenHandler var1, int var2, Item var3, int var4) {
      this.handle(var1, var2, var1x -> var1x.isOf(var3), var4);
   }

   private void handle(CraftingScreenHandler var1, int var2, Predicate<ItemStack> var3, int var4) {
      if (!var1.getSlot(var4).hasStack()) {
         int var5 = -1;

         for (int var6 = 10; var6 < var1.slots.size(); var6++) {
            Slot var7 = var1.getSlot(var6);
            if (var7.hasStack() && var3.test(var7.getStack())) {
               var5 = var6;
               break;
            }
         }

         if (var5 != -1) {
            Module.client.interactionManager.clickSlot(var2, var5, 0, SlotActionType.PICKUP, Module.client.player);
            Module.client.interactionManager.clickSlot(var2, var4, 1, SlotActionType.PICKUP, Module.client.player);
            Module.client.interactionManager.clickSlot(var2, var5, 0, SlotActionType.PICKUP, Module.client.player);
         }
      }
   }

   private void handle(GenericContainerScreen var1, boolean var2) {
      ScreenHandler var3 = var1.getScreenHandler();
      int var4 = this.check(var1);
      if (this.scaleSave) {
         if (this.providerClose.update(1500L)) {
            this.measureTimer();
         }
      } else {
         int var5 = 0;

         for (int var6 = this.compute(var2); var5 < 4 && this.colorCompute < var6; var5++) {
            int var7 = this.process(var1);
            int var8 = this.handle(var3, var4);
            if (var7 == -1 || var8 == -1) {
               break;
            }

            Module.client.interactionManager.clickSlot(var3.syncId, var8, 0, SlotActionType.PICKUP, Module.client.player);
            Module.client.interactionManager.clickSlot(var3.syncId, var7, 0, SlotActionType.PICKUP, Module.client.player);
            this.colorCompute++;
         }

         if (var5 > 0) {
            this.providerClose.handle();
         } else if (this.colorCompute <= 0) {
            this.measureTimer();
            this.moduleCollect = var2 ? MoneyFarm.Mode.RESALE_SEARCH_OWN_AH : MoneyFarm.Mode.IDLE;
            this.providerClose.handle();
         } else {
            int var9 = this.resolve(var1);
            if (var9 != -1) {
               this.handle(var1, var9, 0, SlotActionType.PICKUP);
               this.scaleSave = true;
               this.scaleAdapt = this.colorCompute;
               this.providerClose.handle();
            } else {
               if (this.providerClose.update(3000L)) {
                  this.measureTimer();
                  this.moduleCollect = var2 ? MoneyFarm.Mode.RESALE_SELLING : MoneyFarm.Mode.SELLING;
                  this.providerClose.handle();
               }
            }
         }
      }
   }

   private int compute(boolean var1) {
      if (var1) {
         return Integer.MAX_VALUE;
      } else {
         return this.textureRun > 0 ? this.textureRun : Math.max(0, this.animationSchedule);
      }
   }

   private int process(GenericContainerScreen var1) {
      int var2 = Math.min(9, this.check(var1));

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (!var4.hasStack()) {
            return var3;
         }
      }

      return -1;
   }

   private int compute(GenericContainerScreen var1) {
      int var2 = 0;
      int var3 = Math.min(9, this.check(var1));

      for (int var4 = 0; var4 < var3; var4++) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (!var5.hasStack()) {
            var2++;
         }
      }

      return var2;
   }

   private int writePreset() {
      return this.textureRun > 0 ? this.textureRun : this.animationSchedule;
   }

   private int handle(ScreenHandler var1, int var2) {
      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         Slot var4 = var1.getSlot(var3);
         if (var4.hasStack() && this.handle(var4.getStack(), true)) {
            return var3;
         }
      }

      return -1;
   }

   private int resolve(GenericContainerScreen var1) {
      int var2 = this.check(var1);
      int var3 = -1;

      for (int var4 = var2 - 1; var4 >= 0; var4--) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (var5.hasStack()) {
            ItemStack var6 = var5.getStack();
            String var7 = this.resolve(var6.getName().getString());
            if (var6.isOf(Items.LIME_DYE)
               || var6.isOf(Items.GREEN_DYE)
               || var6.isOf(Items.LIME_STAINED_GLASS_PANE)
               || var6.isOf(Items.GREEN_STAINED_GLASS_PANE)) {
               return var4;
            }

            if (var7.contains("выстав") || var7.contains("продать") || var7.contains("подтверд")) {
               var3 = var4;
            }
         }
      }

      return var3;
   }

   private boolean measureColor() {
      return !this.scheduleAnimation() && !this.scanRenderer();
   }

   private boolean scheduleAnimation() {
      return this.handle(Items.EMERALD) < this.buildSource();
   }

   private boolean scanRenderer() {
      return this.handle(Items.STICK) < this.collapseOutput();
   }

   private int buildSource() {
      return this.runTexture() ? 2 : 3;
   }

   private int collapseOutput() {
      return this.runTexture() ? 1 : 2;
   }

   private int handle(Item var1) {
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

   private boolean invokeProfile() {
      return this.scheduleSource() > 0;
   }

   private int scheduleSource() {
      if (Module.client.player == null) {
         return 0;
      }

      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.handle(var3, true)) {
            var1 += Math.max(1, var3.getCount());
         }
      }

      return var1;
   }

   private boolean handle(ItemStack var1, boolean var2) {
      if (var1 != null && !var1.isEmpty()) {
         String var3 = this.resolve(var1.getName().getString());
         if (var3.contains(this.resolve(this.adaptScale()))) {
            return true;
         } else if (!var2) {
            return false;
         } else if (this.runTexture()) {
            return var1.isOf(Items.DIAMOND_SWORD);
         } else {
            return this.bindIndex() ? var1.isOf(Items.DIAMOND_AXE) : var1.isOf(Items.DIAMOND_PICKAXE);
         }
      } else {
         return false;
      }
   }

   private int update(GenericContainerScreen var1) {
      int var2 = this.check(var1);

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (this.handle(var4) && this.handle(var4.getStack(), true)) {
            return var3;
         }
      }

      return -1;
   }

   private boolean handle(Slot var1) {
      return var1 != null && var1.hasStack() ? !this.handle(var1.getStack()) : false;
   }

   private boolean handle(ItemStack var1) {
      return var1.isOf(Items.GREEN_STAINED_GLASS_PANE)
         || var1.isOf(Items.BLACK_STAINED_GLASS_PANE)
         || var1.isOf(Items.LIME_STAINED_GLASS_PANE)
         || var1.isOf(Items.RED_STAINED_GLASS_PANE)
         || var1.isOf(Items.AIR);
   }

   private int apply(GenericContainerScreen var1) {
      int var2 = this.check(var1);

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (var4.hasStack()) {
            ItemStack var5 = var4.getStack();
            String var6 = this.resolve(var5.getName().getString());
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

   private int handle(GenericContainerScreen var1, Item var2) {
      int var3 = this.check(var1);

      for (int var4 = 0; var4 < var3; var4++) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (var5.hasStack() && var5.getStack().isOf(var2)) {
            return var4;
         }
      }

      return -1;
   }

   private boolean execute(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.resolve(var1.getTitle().getString());
      if (var2.contains("подтверждение покупки")) {
         return this.handle(var1.getScreenHandler()) != -1;
      }

      ScreenHandler var3 = var1.getScreenHandler();
      return this.handle(var3) != -1 && this.process(var3);
   }

   private boolean prepare(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.resolve(var1.getTitle().getString());
      return var2.contains("продажа") || var2.contains("sellgui") || var2.contains("sell gui");
   }

   private int handle(ScreenHandler var1) {
      int var2 = Math.min(var1.slots.size(), Math.max(0, var1.slots.size() - 36));

      for (int var3 = var2 - 1; var3 >= 0; var3--) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         String var5 = this.resolve(var4.getName().getString());
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

   private boolean process(ScreenHandler var1) {
      int var2 = Math.min(var1.slots.size(), Math.max(0, var1.slots.size() - 36));
      int var3 = 0;

      while (true) {
         if (var3 >= var2) {
            return false;
         }

         ItemStack var4 = var1.getSlot(var3).getStack();
         if (var4.isOf(Items.RED_STAINED_GLASS_PANE)) {
            break;
         }

         if (var4.isOf(Items.RED_CONCRETE)) {
            break;
         }

         var3++;
      }

      return true;
   }

   private void handle(GenericContainerScreen var1, int var2, int var3, SlotActionType var4) {
      Module.client.interactionManager.clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2, var3, var4, Module.client.player);
   }

   private int check(GenericContainerScreen var1) {
      int var2 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getRows();
      int var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size();
      return Math.max(0, Math.min(var2 * 9, var3));
   }

   private BlockPos renderTimer() {
      if (Module.client.player != null && Module.client.world != null) {
         BlockPos var1 = Module.client.player.getBlockPos();
         BlockPos var2 = null;
         double var3 = Double.MAX_VALUE;
         Vec3d var5 = Module.client.player.getEyePos();

         for (BlockPos var7 : BlockPos.iterate(var1.add(-5, -5, -5), var1.add(5, 5, 5))) {
            BlockPos var8 = var7.toImmutable();
            if (this.handle(var8)) {
               double var9 = Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var8));
               if (!(var9 > 25.0) && this.handle(var8, var5) && var9 < var3) {
                  var3 = var9;
                  var2 = var8;
               }
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   private boolean handle(BlockPos var1, Vec3d var2) {
      Vec3d var3 = Vec3d.ofCenter(var1);
      Vec3d var4 = var3.subtract(var2);
      double var5 = var4.length();
      if (var5 > 5.0) {
         return false;
      } else {
         BlockHitResult var7 = Module.client.world.raycast(new RaycastContext(var2, var3, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player));
         if (var7 == null || var7.getType() == Type.MISS) {
            return true;
         } else {
            return var7 instanceof BlockHitResult var8 ? var8.getBlockPos().equals(var1) : true;
         }
      }
   }

   private boolean handle(BlockPos var1) {
      return Module.client.world != null && Module.client.world.getBlockState(var1).isOf(Blocks.CRAFTING_TABLE);
   }

   private MoneyFarm.DataRecord handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      float var9 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var10 = (float)(-Math.toDegrees(Math.atan2(var5, Math.sqrt(var3 * var3 + var7 * var7))));
      return new MoneyFarm.DataRecord(var9, MathHelper.clamp(var10, -90.0F, 90.0F));
   }

   private boolean handle(BlockPos var1, float var2) {
      MoneyFarm.DataRecord var3 = this.handle(Vec3d.ofCenter(var1));
      float var4 = Math.abs(MathHelper.wrapDegrees(var3.yaw - Module.client.player.getYaw()));
      float var5 = Math.abs(var3.pitch - Module.client.player.getPitch());
      return var4 <= var2 && var5 <= var2;
   }

   private void process(BlockPos var1) {
      MoneyFarm.DataRecord var2 = this.handle(Vec3d.ofCenter(var1));
      Module.client.player.setYaw(var2.yaw);
      Module.client.player.setPitch(var2.pitch);
      Module.client.player.headYaw = var2.yaw;
      Module.client.player.bodyYaw = var2.yaw;
   }

   private Direction compute(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      Vec3d var3 = Vec3d.ofCenter(var1);
      Vec3d var4 = var2.subtract(var3);
      double var5 = Math.abs(var4.x);
      double var7 = Math.abs(var4.y);
      double var9 = Math.abs(var4.z);
      if (var7 > var5 && var7 > var9) {
         return var4.y > 0.0 ? Direction.UP : Direction.DOWN;
      } else if (var5 > var9) {
         return var4.x > 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return var4.z > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private void handle(String var1) {
      if (Module.client.player != null && var1 != null && !var1.isBlank()) {
         Module.client.player.networkHandler.sendChatCommand("ah " + var1.trim());
      }
   }

   private boolean onTick(GenericContainerScreen var1) {
      if (var1 != null && Module.client.player != null) {
         String var2 = this.resolve(var1.getTitle().getString());
         String var3 = this.resolve(Module.client.player.getName().getString());
         return AhHelper.handle(var1) || var2.contains(var3) || var2.contains("мои товары") || var2.contains("мои предметы") || var2.contains("поиск:");
      } else {
         return false;
      }
   }

   private boolean saveScale() {
      return System.currentTimeMillis() - this.scaleRender >= 1200L;
   }

   private void handle(long var1) {
      Module.client.player.networkHandler.sendChatCommand("ah sellgui " + var1);
      this.scaleRender = System.currentTimeMillis();
   }

   private void computeColor() {
      this.scaleSave = false;
      this.colorCompute = 0;
      this.scaleAdapt = 0;
      if (this.textureRun < 0) {
         this.textureRun = 0;
      }
   }

   private String adaptScale() {
      if (this.runTexture()) {
         return "Изумрудный меч";
      } else {
         return this.bindIndex() ? "Изумрудный топор" : "Изумрудная кирка";
      }
   }

   private boolean runTexture() {
      return this.responseCompute.process("Изумрудный меч");
   }

   private boolean bindIndex() {
      return this.responseCompute.process("Изумрудный топор");
   }

   private long readAction() {
      return this.process(this.providerFetch.compute());
   }

   private int collapseConfig() {
      return Math.max(1, Math.round(this.positionAdvance.compute()));
   }

   private long validateData() {
      return Math.max(1000L, Math.round(this.serverRead.compute() * 1000.0));
   }

   private long process(String var1) {
      if (var1 == null) {
         return 0L;
      }

      String var2 = var1.toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "").replace(",", "").replace(".", "");
      long var3 = 1L;
      if (var2.endsWith("тысяч")) {
         var3 = 1000L;
         var2 = var2.substring(0, var2.length() - 5);
      } else if (var2.endsWith("тысячи")) {
         var3 = 1000L;
         var2 = var2.substring(0, var2.length() - 6);
      } else if (var2.endsWith("тысяча")) {
         var3 = 1000L;
         var2 = var2.substring(0, var2.length() - 6);
      } else if (var2.endsWith("тыс")) {
         var3 = 1000L;
         var2 = var2.substring(0, var2.length() - 3);
      } else if (var2.endsWith("k") || var2.endsWith("к")) {
         var3 = 1000L;
         var2 = var2.substring(0, var2.length() - 1);
      } else if (var2.endsWith("m") || var2.endsWith("м")) {
         var3 = 1000000L;
         var2 = var2.substring(0, var2.length() - 1);
      }

      String var5 = var2.replaceAll("[^0-9]", "");
      if (var5.isEmpty()) {
         return 0L;
      }

      try {
         return Math.multiplyExact(Long.parseLong(var5), var3);
      } catch (ArithmeticException | NumberFormatException var7) {
         return 0L;
      }
   }

   private long renderScale() {
      return Math.max(50L, Math.round(this.profileDraw.compute()));
   }

   private void refreshClient() {
      if (this.moduleCollect != MoneyFarm.Mode.AIMING_CRAFTING_TABLE && this.moduleCollect != MoneyFarm.Mode.OPENING_CRAFTING_TABLE) {
         if (this.presetSave.update(10000L)) {
            ThreadLocalRandom var1 = ThreadLocalRandom.current();
            float var2 = var1.nextFloat() * 10.0F - 5.0F;
            float var3 = var1.nextFloat() * 6.0F - 3.0F;
            Module.client.player.setYaw(Module.client.player.getYaw() + var2);
            Module.client.player.setPitch(Math.max(-90.0F, Math.min(90.0F, Module.client.player.getPitch() + var3)));
            this.presetSave.handle();
         }
      }
   }

   private void filterKey() {
      this.moduleCollect = MoneyFarm.Mode.IDLE;
      this.windowConvert = null;
      this.presetWrite = 0;
      this.colorMeasure = 0;
      this.animationSchedule = 0;
      this.rendererScan = 0;
      this.sourceBuild = 0;
      this.outputCollapse = false;
      this.profileInvoke = false;
      this.sourceSchedule = false;
      this.timerRender = false;
      this.scaleSave = false;
      this.colorCompute = 0;
      this.scaleAdapt = 0;
      this.textureRun = 0;
      this.indexBind = 0;
      this.actionRead = false;
      this.configCollapse = 0;
      this.dataValidate = -1;
      this.scaleRender = 0L;
      this.clientRefresh = 0L;
      this.keyFilter = 0L;
      this.providerClose.handle();
      this.presetSave.handle();
   }

   private void compute(String var1) {
      this.apply(var1);
      this.adaptRequest();
   }

   private void adaptRequest() {
      this.moduleCollect = MoneyFarm.Mode.IDLE;
      this.configCollapse = 0;
      this.providerClose.handle();
   }

   private static boolean handle(Screen var0) {
      return var0 instanceof ChatScreen || var0 instanceof GameMenuScreen;
   }

   private boolean process(Screen var1) {
      return var1 == null || handle(var1);
   }

   private void measureTimer() {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      if (handle(Module.client.currentScreen)) {
         Module.client.setScreen(null);
      }
   }

   private boolean process(long var1) {
      if (this.process(Module.client.currentScreen)) {
         if (handle(Module.client.currentScreen)) {
            if (!this.providerClose.update(var1)) {
               return false;
            }

            Module.client.setScreen(null);
         }

         this.configCollapse = 0;
         return true;
      } else if (!this.providerClose.update(var1)) {
         return false;
      } else {
         this.measureTimer();
         this.configCollapse++;
         if (this.configCollapse >= 20) {
            Module.client.setScreen(null);
            this.configCollapse = 0;
            return true;
         } else {
            this.providerClose.handle();
            return false;
         }
      }
   }

   private String resolve(String var1) {
      return var1 == null ? "" : var1.replaceAll("(?i)§.", "").replaceAll("(?i)&.", "").toLowerCase(Locale.ROOT).trim();
   }

   private void encodeVector() {
      if (Module.client.player != null) {
         if ((this.moduleCollect == MoneyFarm.Mode.AIMING_CRAFTING_TABLE || this.moduleCollect == MoneyFarm.Mode.OPENING_CRAFTING_TABLE)
            && this.windowConvert != null) {
            this.process(Vec3d.ofCenter(this.windowConvert));
         } else {
            this.keyFilter = 0L;
         }
      }
   }

   private void process(Vec3d var1) {
      MoneyFarm.DataRecord var2 = this.handle(var1);
      float var3 = this.receiveRequest();
      boolean var4 = this.moduleCollect == MoneyFarm.Mode.AIMING_CRAFTING_TABLE;
      float var5 = var4 ? 0.42F : 0.11F;
      float var6 = 1.0F - (float)Math.pow(1.0F - var5, var3);
      float var7 = Module.client.player.getYaw();
      float var8 = Module.client.player.getPitch();
      float var9 = MathHelper.wrapDegrees(var2.yaw - var7);
      float var10 = var2.pitch - var8;
      float var11 = var7 + var9 * var6;
      float var12 = MathHelper.clamp(var8 + var10 * var6, -90.0F, 90.0F);
      Module.client.player.setYaw(var11);
      Module.client.player.setPitch(var12);
      Module.client.player.headYaw = var11;
      Module.client.player.bodyYaw = var11;
   }

   private float receiveRequest() {
      long var1 = System.nanoTime();
      if (this.keyFilter == 0L) {
         this.keyFilter = var1;
         return 1.0F;
      } else {
         float var3 = (float)(var1 - this.keyFilter) / 1.6666667E7F;
         this.keyFilter = var1;
         return MathHelper.clamp(var3, 0.25F, 4.0F);
      }
   }

   private boolean handle(BlockPos var1, double var2) {
      Vec3d var4 = Module.client.player.getEyePos();
      Vec3d var5 = Module.client.player.getRotationVec(1.0F);
      Vec3d var6 = var4.add(var5.multiply(var2));
      return Module.client.world.raycast(new RaycastContext(var4, var6, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player)) instanceof BlockHitResult var8
         ? var8.getBlockPos().equals(var1)
         : false;
   }

   private void processWindow() {
      this.dataValidate = -1;
      if (Module.client.world != null) {
         Scoreboard var1 = Module.client.world.getScoreboard();
         if (var1 != null) {
            ScoreboardObjective var2 = var1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (var2 != null) {
               Collection<ScoreboardEntry> var3;
               try {
                  var3 = var1.getScoreboardEntries(var2);
               } catch (Throwable var8) {
                  return;
               }

               for (ScoreboardEntry var5 : var3) {
                  if (var5 != null) {
                     String var6 = this.resolve(String.valueOf(var5.owner()));
                     if (var6.contains("монет") || var6.contains("coin") || var6.contains("money") || var6.contains("баланс") || var6.contains("$")) {
                        int var7 = this.update(var6);
                        if (var7 >= 0) {
                           this.dataValidate = var7;
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int update(String var1) {
      Matcher var2 = itemProject.matcher(var1);
      int var3 = -1;

      while (var2.find()) {
         String var4 = var2.group(1).replaceAll("[^0-9]", "");
         if (!var4.isBlank() && var4.length() <= 12) {
            try {
               var3 = Integer.parseInt(var4);
            } catch (NumberFormatException var6) {
            }
         }
      }

      return var3;
   }

   private void apply(String var1) {
      if (this.frameCheck.compute() && Module.client.player != null) {
         ChatLogger.handle("§8[§aMoneyFarm§8] §f" + var1);
      }
   }

   record DataRecord(float yaw, float pitch) {
   }

   enum Mode {
      IDLE,
      BUY_OPENING_SHOP,
      BUY_WAITING_SHOP,
      BUY_FIND_GOLD_INGOT,
      BUY_WAITING_EMERALD_MENU,
      BUY_FIND_EMERALD,
      BUY_WAITING_CONFIRM,
      BUY_CLICK_LIME_PANE,
      BUY_CLOSING_SHOP,
      CHECK_SELL_GUI_OPENING,
      CHECK_SELL_GUI_WAITING,
      CHECK_SELL_GUI_READING,
      FINDING_CRAFTING_TABLE,
      AIMING_CRAFTING_TABLE,
      OPENING_CRAFTING_TABLE,
      PLACING_ITEMS,
      TAKING_RESULT,
      CLOSING_CRAFTING,
      SELLING,
      WAITING_SELL_RESULT,
      RESALE_SEARCH_OWN_AH,
      RESALE_WAITING_OWN_AH,
      RESALE_TAKE_ITEM,
      RESALE_CLOSING,
      RESALE_SELLING,
      RESALE_WAIT_SELL_RESULT;
   }
}
