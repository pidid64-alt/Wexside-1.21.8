package ru.wild.modules.misc;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.util.inventory.AuctionLoreParser;
import ru.wild.util.inventory.AuctionSellerParser;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(
   name = "EmeraldArmorFarm",
   category = ModuleCategory.Misc,
   description = "Крафтит изумрудную броню, сливает в наковальне и продает на /ah"
)
public class EmeraldArmorFarm extends Module {
   private static final int source = 5;
   private static final int target = 6;
   private static final double pending = 4.5;
   private static final int previous = 20;
   private static final long latest = 250L;
   private static final long summary = 1500L;
   private static final long matrixBlend = 7000L;
   private static final long vectorMatch = 500L;
   private static final int itemProject = 50;
   private static final int responseCompute = 48;
   private static final Pattern providerFetch = Pattern.compile("(?i)защит\\S{0,3}\\s*:?\\s*([0-9]+|[ivx]+)");
   private static final Pattern profileDraw = Pattern.compile(
      "(\\d+)\\s*[/\\\\]\\s*\\d+|(?i)(?:страниц\\w*|стр\\.?|page)\\s*[:#]?\\s*(\\d+)|(?i)(\\d+)\\s*(?:из|of)\\s*\\d+"
   );
   private final ModeSetting vectorPerform = new ModeSetting("Сервер", "FunTime", "FunTime", "SpookyTime");
   private final StringSetting eventAttach = new StringSetting("Цена продажи", "40000").handle(32);
   private final StringSetting serverRead = new StringSetting("Макс. цена опыта", "1000000").handle(32);
   private final ModeSetting positionAdvance = new ModeSetting("Бутылка опыта", "Опыт 45", "Опыт 15", "Опыт 30", "Опыт 45", "Опыт 50");
   private final NumberSetting frameCheck = new NumberSetting("Мин. уровень", 30.0F, 1.0F, 100.0F, 1.0F, false);
   private final NumberSetting moduleCollect = new NumberSetting("Бутылок бросать", 2.0F, 1.0F, 10.0F, 1.0F, false);
   private final NumberSetting providerClose = new NumberSetting("Радиус игроков", 3.0F, 0.0F, 20.0F, 1.0F, false);
   private final NumberSetting presetSave = new NumberSetting("Перевыставить (сек)", 30.0F, 5.0F, 120.0F, 1.0F, false);
   private final NumberSetting windowConvert = new NumberSetting("Задержка (мс)", 100.0F, 50.0F, 5000.0F, 50.0F, false);
   private final NumberSetting presetWrite = new NumberSetting("Буфер изумрудов", 128.0F, 64.0F, 512.0F, 64.0F, false);
   private final BooleanSetting colorMeasure = new BooleanSetting("Уведомления", true);
   private EmeraldArmorFarm.PrimaryMode animationSchedule = EmeraldArmorFarm.PrimaryMode.IDLE;
   private final Stopwatch rendererScan = new Stopwatch();
   private final Stopwatch sourceBuild = new Stopwatch();
   private final Stopwatch outputCollapse = new Stopwatch();
   private final Stopwatch profileInvoke = new Stopwatch();
   private final Stopwatch sourceSchedule = new Stopwatch();
   private final Stopwatch timerRender = new Stopwatch();
   private EmeraldArmorFarm.Mode scaleSave = EmeraldArmorFarm.Mode.NONE;
   private boolean colorCompute = false;
   private BlockPos scaleAdapt;
   private BlockPos textureRun;
   private int indexBind = 0;
   private int actionRead = 0;
   private boolean configCollapse = false;
   private boolean dataValidate = false;
   private boolean scaleRender = false;
   private boolean clientRefresh = false;
   private boolean keyFilter = false;
   private int requestAdapt = 0;
   private int timerMeasure = 0;
   private int vectorEncode = 0;
   private long requestReceive = 0L;
   private long windowProcess = 0L;
   private long packetSave = 0L;
   private int entryAnimate = 50;

   public EmeraldArmorFarm() {
      this.handle(
         this.vectorPerform,
         this.eventAttach,
         this.serverRead,
         this.positionAdvance,
         this.frameCheck,
         this.moduleCollect,
         this.providerClose,
         this.presetSave,
         this.windowConvert,
         this.presetWrite,
         this.colorMeasure
      );
   }

   @Override
   public void handle() {
      super.handle();
      this.performScale();
   }

   @Override
   public void process() {
      this.performScale();
      super.process();
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && var1.update().equals(PacketEvent.Mode.RECEIVE)) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var5 = var2.content().getString();
            String var4 = this.onTick(var5);
            if (this.animationSchedule != EmeraldArmorFarm.PrimaryMode.BUY_FIND_EMERALD
                  && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.BUY_WAITING_CONFIRM
                  && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.BUY_CLICK_LIME_PANE
                  && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.BUY_XP_WAITING_CONFIRM
                  && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.BUY_XP_CONFIRMING
               || !var4.contains("недостаточно") && !var4.contains("не хватает") && !var4.contains("нет монет") && !var4.contains("нет денег")) {
               if (var4.contains("не удалось выставить") && var4.contains("освободите хранилище")) {
                  this.scaleRender = true;
                  this.configCollapse = false;
                  this.clientRefresh = true;
               } else if (this.resolve(var5)) {
                  this.dataValidate = true;
                  this.requestAdapt = Math.max(0, this.requestAdapt - 1);
                  this.windowProcess = System.currentTimeMillis();
               } else {
                  if (this.compute(var5)) {
                     this.scaleRender = false;
                     this.configCollapse = true;
                     this.requestAdapt++;
                     this.windowProcess = System.currentTimeMillis();
                  }

                  if (var4.contains("вы успешно купили")
                     && (
                        this.animationSchedule == EmeraldArmorFarm.PrimaryMode.BUY_XP_WAITING_CONFIRM
                           || this.animationSchedule == EmeraldArmorFarm.PrimaryMode.BUY_XP_CONFIRMING
                           || this.animationSchedule == EmeraldArmorFarm.PrimaryMode.BUY_XP_CLOSING
                     )) {
                     this.keyFilter = true;
                  }
               }
            } else {
               this.processKey();
               this.check("§cНе хватает монет для покупки.");
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         this.readProvider();
         this.parseMessage();
         if (this.scaleRender && this.animationSchedule == EmeraldArmorFarm.PrimaryMode.IDLE) {
            this.computeColor();
         }

         switch (this.animationSchedule) {
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
            case BUY_XP_SEARCHING:
               this.unload();
               break;
            case BUY_XP_WAITING_AUCTION:
               this.fetch();
               break;
            case BUY_XP_READING_AUCTION:
               this.measure();
               break;
            case BUY_XP_WAITING_CONFIRM:
               this.matchVector();
               break;
            case BUY_XP_CONFIRMING:
               this.projectItem();
               break;
            case BUY_XP_CLOSING:
               this.computeResponse();
               break;
            case THROW_XP_WAIT_PLAYERS:
               this.fetchProvider();
               break;
            case THROW_XP_THROWING:
               this.drawProfile();
               break;
            case FINDING_CRAFTING_TABLE:
               this.performVector();
               break;
            case AIMING_CRAFTING_TABLE:
               this.attachEvent();
               break;
            case OPENING_CRAFTING_TABLE:
               this.readServer();
               break;
            case PLACING_ITEMS:
               this.advancePosition();
               break;
            case TAKING_RESULT:
               this.checkFrame();
               break;
            case CLOSING_CRAFTING:
               this.collectModule();
               break;
            case FINDING_ANVIL:
               this.closeProvider();
               break;
            case AIMING_ANVIL:
               this.savePreset();
               break;
            case OPENING_ANVIL:
               this.convertWindow();
               break;
            case HANDLING_ANVIL:
               this.writePreset();
               break;
            case CLOSING_ANVIL:
               this.measureColor();
               break;
            case SELLING:
               this.scheduleAnimation();
               break;
            case WAITING_SELL_RESULT:
               this.scanRenderer();
               break;
            case RESALE_SEARCH_OWN_AH:
               this.buildSource();
               break;
            case RESALE_WAITING_OWN_AH:
               this.collapseOutput();
               break;
            case RESALE_TAKE_ITEM:
               this.invokeProfile();
               break;
            case RESALE_CLOSING:
               this.scheduleSource();
               break;
            case RESALE_SELLING:
               this.renderTimer();
               break;
            case RESALE_WAIT_SELL_RESULT:
               this.saveScale();
         }
      }
   }

   private void refresh() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (this.requestAdapt > 0) {
            if (System.currentTimeMillis() - this.windowProcess >= this.writeTarget()) {
               this.computeColor();
            }
         } else if (this.runTexture()) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.SELLING;
            this.rendererScan.handle();
         } else if (this.handle(Items.EMERALD) < this.sendWorld()) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_OPENING_SHOP;
            this.rendererScan.handle();
         } else {
            EmeraldArmorFarm.Mode var1 = this.adaptScale();
            if (var1 != EmeraldArmorFarm.Mode.NONE) {
               if (var1 != this.scaleSave) {
                  this.scaleSave = var1;
                  this.colorCompute = false;
               }

               if (Module.client.player.experienceLevel < this.applyState()) {
                  if (this.readAction() > 0) {
                     this.timerMeasure = 0;
                     this.animationSchedule = EmeraldArmorFarm.PrimaryMode.THROW_XP_WAIT_PLAYERS;
                  } else {
                     this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_SEARCHING;
                  }

                  this.rendererScan.handle();
               } else if (!this.colorCompute && this.handle(this.scaleSave, 3) < 4) {
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.FINDING_CRAFTING_TABLE;
                  this.rendererScan.handle();
               } else {
                  this.colorCompute = true;
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.FINDING_ANVIL;
                  this.rendererScan.handle();
               }
            }
         }
      }
   }

   private void render() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (this.process(150L)) {
            Module.client.player.networkHandler.sendChatCommand("shop");
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_WAITING_SHOP;
            this.rendererScan.handle();
         }
      }
   }

   private void tick() {
      if (Module.client.currentScreen instanceof GenericContainerScreen) {
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_FIND_GOLD_INGOT;
         this.rendererScan.handle();
      } else {
         if (this.rendererScan.update(10000L)) {
            this.check("§cТаймаут магазина.");
         }
      }
   }

   private void drawAnimation() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            int var3 = this.handle(var1, Items.GOLD_INGOT);
            if (var3 != -1) {
               this.handle(var1, var3, 0, SlotActionType.PICKUP);
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_WAITING_EMERALD_MENU;
               this.rendererScan.handle();
            } else {
               if (this.rendererScan.update(5000L)) {
                  this.processKey();
                  this.check("§cЗолотой слиток не найден.");
               }
            }
         } else {
            this.expandContext();
         }
      }
   }

   private void encodePoint() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (!(Module.client.currentScreen instanceof GenericContainerScreen)) {
            this.expandContext();
         } else {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_FIND_EMERALD;
            this.rendererScan.handle();
         }
      }
   }

   private void animate() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            int var3 = this.process(var1);
            if (var3 != -1) {
               this.handle(var1, var3, 1, SlotActionType.PICKUP);
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_WAITING_CONFIRM;
               this.rendererScan.handle();
            } else {
               if (this.rendererScan.update(5000L)) {
                  this.processKey();
                  this.check("§cИзумруд не найден.");
               }
            }
         } else {
            this.expandContext();
         }
      }
   }

   private void load() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (!this.filterKey()) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_CLOSING_SHOP;
            this.rendererScan.handle();
         } else if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (this.compute(var1)) {
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_CLICK_LIME_PANE;
               this.rendererScan.handle();
            } else {
               if (this.rendererScan.update(5000L)) {
                  this.processKey();
                  this.check("§cПодтверждение покупки изумрудов не открылось.");
               }
            }
         } else {
            this.expandContext();
         }
      }
   }

   private void save() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (!this.filterKey()) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_CLOSING_SHOP;
            this.rendererScan.handle();
         } else if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (!this.compute(var1)) {
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_WAITING_CONFIRM;
               this.rendererScan.handle();
            } else {
               int var3 = this.process(var1.getScreenHandler());
               if (var3 != -1) {
                  this.handle(var1, var3, 0, SlotActionType.PICKUP);
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_CLOSING_SHOP;
                  this.rendererScan.handle();
               } else {
                  if (this.rendererScan.update(5000L)) {
                     this.processKey();
                     this.check("§cЛаймовая панель не найдена.");
                  }
               }
            }
         } else {
            this.expandContext();
         }
      }
   }

   private void submit() {
      if (this.process(150L)) {
         this.expandContext();
      }
   }

   private void unload() {
      if (this.rendererScan.update(this.encodeResult()) && Module.client.currentScreen == null) {
         long var1 = this.savePacket();
         if (var1 <= 0L) {
            this.check("§cМаксимальная цена опыта не задана.");
         } else {
            this.apply(this.collectPlayer());
            this.indexBind = 0;
            this.keyFilter = false;
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_WAITING_AUCTION;
            this.rendererScan.handle();
            this.outputCollapse.handle();
            this.profileInvoke.handle();
            this.sourceSchedule.handle();
         }
      }
   }

   private void fetch() {
      label21: {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (AhHelper.handle(var1)) {
               break label21;
            }

            if (this.compute(var1)) {
               break label21;
            }
         }

         if (this.rendererScan.update(10000L)) {
            this.check("§cТаймаут поиска опыта.");
         }

         return;
      }

      this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_READING_AUCTION;
      this.rendererScan.handle();
   }

   private void measure() {
      if (this.rendererScan.update(150L)) {
         if (!(Module.client.currentScreen instanceof GenericContainerScreen var1)) {
            if (this.rendererScan.update(10000L)) {
               this.expandContext();
            }
         } else if (this.compute(var1)) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_CONFIRMING;
            this.rendererScan.handle();
         } else if (!AhHelper.handle(var1)) {
            if (this.rendererScan.update(10000L)) {
               this.processKey();
               this.check("§cОткрылся не экран аукциона при покупке опыта.");
            }
         } else {
            long var11 = this.savePacket();
            ScreenHandler var4 = var1.getScreenHandler();
            boolean var5 = false;

            for (int var6 = 0; var6 < Math.min(45, var4.slots.size()); var6++) {
               Slot var7 = var4.getSlot(var6);
               if (this.handle(var7) && this.compute(var7.getStack())) {
                  long var8 = this.process(var7);
                  String var10 = this.compute(var7);
                  if ((Module.client.player == null || var10 == null || !var10.equalsIgnoreCase(Module.client.player.getName().getString()))
                     && var8 > 0L
                     && var8 <= var11) {
                     var5 = true;
                     if (this.profileInvoke.update(Math.max(50L, this.encodeResult()))) {
                        Module.client.interactionManager.clickSlot(var4.syncId, var6, 0, SlotActionType.PICKUP, Module.client.player);
                        this.profileInvoke.handle();
                        this.sourceSchedule.handle();
                        this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_WAITING_CONFIRM;
                        this.rendererScan.handle();
                        return;
                     }
                     break;
                  }
               }
            }

            if (!var5 && this.outputCollapse.update(250L)) {
               if (this.handle(var4)) {
                  this.outputCollapse.handle();
               } else {
                  this.processKey();
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_SEARCHING;
                  this.rendererScan.handle();
               }
            } else if (this.rendererScan.update(10000L)) {
               this.processKey();
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_SEARCHING;
               this.rendererScan.handle();
            } else {
               this.indexBind++;
            }
         }
      }
   }

   private boolean handle(ScreenHandler var1) {
      int var2 = var1.syncId;
      int var3 = this.blendMatrix();
      int var4;
      if (var3 > 1) {
         var4 = 48;
      } else if (var3 == 1) {
         var4 = 50;
      } else {
         var4 = this.entryAnimate;
         this.entryAnimate = var4 == 50 ? 48 : 50;
      }

      if (var4 >= 0 && var4 < var1.slots.size()) {
         Module.client.interactionManager.clickSlot(var2, var4, 0, SlotActionType.PICKUP, Module.client.player);
         return true;
      } else {
         return false;
      }
   }

   private int blendMatrix() {
      if (Module.client.currentScreen == null) {
         return -1;
      }

      String var1 = this.handle(Module.client.currentScreen.getTitle().getString());
      if (var1.isEmpty()) {
         return -1;
      }

      Matcher var2 = profileDraw.matcher(var1);
      if (!var2.find()) {
         return -1;
      }

      String var3 = var2.group(1);
      if (var3 == null) {
         var3 = var2.group(2);
      }

      if (var3 == null) {
         var3 = var2.group(3);
      }

      if (var3 == null) {
         return -1;
      }

      try {
         int var4 = Integer.parseInt(var3);
         return var4 < 1 ? -1 : var4;
      } catch (NumberFormatException var5) {
         return -1;
      }
   }

   private String handle(String var1) {
      return var1 == null ? "" : var1.replaceAll("§.", "").toLowerCase(Locale.ROOT).trim();
   }

   private void matchVector() {
      if (this.rendererScan.update(50L)) {
         if (!this.keyFilter && this.readAction() <= 0) {
            if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
               if (this.compute(var1)) {
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_CONFIRMING;
                  this.rendererScan.handle();
               } else {
                  if (this.rendererScan.update(4000L)) {
                     this.processKey();
                     this.check("§cПодтверждение покупки опыта не открылось.");
                  }
               }
            } else {
               if (this.rendererScan.update(4000L)) {
                  this.check("§cПодтверждение покупки опыта не открылось.");
               }
            }
         } else {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_CLOSING;
            this.rendererScan.handle();
         }
      }
   }

   private void projectItem() {
      if (this.rendererScan.update(50L)) {
         if (!this.keyFilter && this.readAction() <= 0) {
            if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
               int var3 = this.process(var1.getScreenHandler());
               if (var3 != -1 && this.sourceSchedule.update(Math.max(50L, this.encodeResult()))) {
                  this.handle(var1, var3, 0, SlotActionType.PICKUP);
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_CLOSING;
                  this.rendererScan.handle();
                  this.sourceSchedule.handle();
               } else {
                  if (this.rendererScan.update(5000L)) {
                     this.processKey();
                     this.check("§cКнопка подтверждения покупки опыта не найдена.");
                  }
               }
            } else {
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_CLOSING;
               this.rendererScan.handle();
            }
         } else {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_CLOSING;
            this.rendererScan.handle();
         }
      }
   }

   private void computeResponse() {
      if (this.rendererScan.update(300L)) {
         this.processKey();
         this.expandContext();
      }
   }

   private void fetchProvider() {
      if (this.rendererScan.update(300L)) {
         if (!this.adaptRequest()) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.THROW_XP_THROWING;
            this.rendererScan.handle();
            this.timerRender.handle();
         }
      }
   }

   private void drawProfile() {
      if (Module.client.player.experienceLevel >= this.applyState()) {
         this.renderScale();
         this.expandContext();
      } else if (this.timerMeasure >= this.filterMatrix()) {
         this.renderScale();
         this.expandContext();
      } else if (this.adaptRequest()) {
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.THROW_XP_WAIT_PLAYERS;
         this.rendererScan.handle();
      } else if (Module.client.currentScreen != null) {
         this.processKey();
      } else {
         float var1 = 87.0F + this.handle(-0.5F, 0.5F);
         Module.client.player.setPitch(var1);
         if (!this.collapseConfig()) {
            this.renderScale();
            this.expandContext();
         } else if (this.timerRender.update(200L)) {
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.timerMeasure++;
            this.timerRender.handle();
         }
      }
   }

   private void performVector() {
      if (this.process(150L)) {
         if (this.rendererScan.update(this.encodeResult())) {
            this.scaleAdapt = this.measureTimer();
            if (this.scaleAdapt == null) {
               this.check("§cВерстак рядом не найден.");
            } else {
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.AIMING_CRAFTING_TABLE;
               this.rendererScan.handle();
            }
         }
      }
   }

   private void attachEvent() {
      if (this.scaleAdapt == null || !this.handle(this.scaleAdapt)) {
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.FINDING_CRAFTING_TABLE;
         this.rendererScan.handle();
      } else if (this.handle(this.scaleAdapt, 4.5) && this.rendererScan.update(220L)) {
         BlockHitResult var1 = new BlockHitResult(Vec3d.ofCenter(this.scaleAdapt), this.compute(this.scaleAdapt), this.scaleAdapt, false);
         Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.OPENING_CRAFTING_TABLE;
         this.rendererScan.handle();
      }
   }

   private void readServer() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (Module.client.currentScreen instanceof CraftingScreen) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.PLACING_ITEMS;
            this.rendererScan.handle();
         } else {
            if (this.rendererScan.update(5000L)) {
               this.check("§cВерстак не открылся.");
            }
         }
      }
   }

   private void advancePosition() {
      if (this.rendererScan.update(50L)) {
         if (!(Module.client.currentScreen instanceof CraftingScreen var1)) {
            this.expandContext();
         } else {
            CraftingScreenHandler var9 = (CraftingScreenHandler)var1.getScreenHandler();
            int var3 = var9.syncId;
            int[] var4 = this.handle(this.scaleSave);

            for (int var8 : var4) {
               this.handle(var9, var3, Items.EMERALD, var8);
            }

            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.TAKING_RESULT;
            this.rendererScan.handle();
         }
      }
   }

   private void checkFrame() {
      if (this.rendererScan.update(50L)) {
         if (Module.client.currentScreen instanceof CraftingScreen var1) {
            Module.client.interactionManager
               .clickSlot(((CraftingScreenHandler)var1.getScreenHandler()).syncId, 0, 0, SlotActionType.QUICK_MOVE, Module.client.player);
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.CLOSING_CRAFTING;
            this.rendererScan.handle();
         } else {
            this.expandContext();
         }
      }
   }

   private void collectModule() {
      if (this.rendererScan.update(50L)) {
         this.processKey();
         this.expandContext();
      }
   }

   private void closeProvider() {
      if (this.process(150L)) {
         if (this.rendererScan.update(this.encodeResult())) {
            this.textureRun = this.encodeVector();
            if (this.textureRun == null) {
               this.check("§cНаковальня рядом не найдена.");
            } else {
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.AIMING_ANVIL;
               this.rendererScan.handle();
            }
         }
      }
   }

   private void savePreset() {
      if (this.textureRun == null || !this.process(this.textureRun)) {
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.FINDING_ANVIL;
         this.rendererScan.handle();
      } else if (this.handle(this.textureRun, 4.5) && this.rendererScan.update(220L)) {
         BlockHitResult var1 = new BlockHitResult(Vec3d.ofCenter(this.textureRun), this.compute(this.textureRun), this.textureRun, false);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.OPENING_ANVIL;
         this.rendererScan.handle();
      }
   }

   private void convertWindow() {
      if (this.rendererScan.update(this.encodeResult())) {
         if (Module.client.player.currentScreenHandler instanceof AnvilScreenHandler) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.HANDLING_ANVIL;
            this.rendererScan.handle();
         } else {
            if (this.rendererScan.update(5000L)) {
               this.check("§cНаковальня не открылась.");
            }
         }
      }
   }

   private void writePreset() {
      if (this.rendererScan.update(50L)) {
         if (Module.client.player.currentScreenHandler instanceof AnvilScreenHandler var1) {
            if (Module.client.player.experienceLevel < this.applyState()) {
               this.processKey();
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.BUY_XP_SEARCHING;
               this.rendererScan.handle();
            } else if (this.scaleSave != EmeraldArmorFarm.Mode.NONE && this.handle(this.scaleSave, 5) < 1) {
               int var5 = this.handle(this.scaleSave, 4) >= 2 ? 4 : 3;
               if (var5 == 3 && this.handle(this.scaleSave, 3) < 2) {
                  this.processKey();
                  this.colorCompute = false;
                  this.expandContext();
               } else {
                  for (int var3 = 0; var3 < 2; var3++) {
                     ItemStack var4 = this.process(var1, var3);
                     if (!var4.isEmpty() && !this.handle(var4, this.scaleSave, var5)) {
                        this.handle(var1, var3);
                        this.rendererScan.handle();
                        return;
                     }
                  }

                  for (int var6 = 0; var6 < 2; var6++) {
                     if (this.process(var1, var6).isEmpty()) {
                        int var7 = this.handle(var1, this.scaleSave, var5);
                        if (var7 == -1) {
                           this.processKey();
                           this.colorCompute = false;
                           this.expandContext();
                           return;
                        }

                        this.handle(var1, var7, var6);
                        this.rendererScan.handle();
                        return;
                     }
                  }

                  if (!this.process(var1, 2).isEmpty()) {
                     this.handle(var1, 2);
                     this.processKey();
                     this.expandContext();
                  } else {
                     this.rendererScan.handle();
                  }
               }
            } else {
               this.processKey();
               this.expandContext();
            }
         } else {
            this.expandContext();
         }
      }
   }

   private void measureColor() {
      if (this.rendererScan.update(50L)) {
         this.processKey();
         this.expandContext();
      }
   }

   private void scheduleAnimation() {
      if (this.rendererScan.update(50L)) {
         if (this.scaleRender) {
            this.computeColor();
         } else {
            long var1 = this.processWindow();
            if (var1 <= 0L) {
               this.check("§cЦена продажи не задана.");
            } else if (!this.bindIndex()) {
               this.expandContext();
            } else if (Module.client.currentScreen != null) {
               this.processKey();
               this.rendererScan.handle();
            } else if (this.receiveRequest()) {
               if (!this.refreshClient()) {
                  this.expandContext();
               } else {
                  this.configCollapse = false;
                  this.scaleRender = false;
                  this.handle(var1);
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.WAITING_SELL_RESULT;
                  this.rendererScan.handle();
               }
            }
         }
      }
   }

   private void scanRenderer() {
      if (this.scaleRender) {
         this.computeColor();
      } else {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.compute(var1)) {
            int var3 = this.process(var1.getScreenHandler());
            if (var3 != -1 && this.sourceSchedule.update(Math.max(50L, this.encodeResult()))) {
               this.handle(var1, var3, 0, SlotActionType.PICKUP);
               this.sourceSchedule.handle();
               this.rendererScan.handle();
               return;
            }
         }

         if (this.configCollapse) {
            this.configCollapse = false;
            this.animationSchedule = this.bindIndex() ? EmeraldArmorFarm.PrimaryMode.SELLING : EmeraldArmorFarm.PrimaryMode.IDLE;
            this.rendererScan.handle();
         } else {
            if (this.rendererScan.update(7000L)) {
               this.processKey();
               this.animationSchedule = this.bindIndex() ? EmeraldArmorFarm.PrimaryMode.SELLING : EmeraldArmorFarm.PrimaryMode.IDLE;
               this.rendererScan.handle();
            }
         }
      }
   }

   private void buildSource() {
      if (this.rendererScan.update(this.encodeResult()) && Module.client.currentScreen == null) {
         if (!this.clientRefresh && this.bindIndex()) {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SELLING;
            this.rendererScan.handle();
         } else {
            this.clientRefresh = false;
            String var1 = Module.client.player.getName().getString();
            this.execute(var1);
            this.vectorEncode = 0;
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_WAITING_OWN_AH;
            this.rendererScan.handle();
         }
      }
   }

   private void collapseOutput() {
      if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.update(var1)) {
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_TAKE_ITEM;
         this.rendererScan.handle();
      } else {
         if (this.rendererScan.update(10000L)) {
            this.check("§cТаймаут поиска своих товаров.");
         }
      }
   }

   private void invokeProfile() {
      if (this.rendererScan.update(200L)) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (!this.update(var1)) {
               if (this.rendererScan.update(10000L)) {
                  this.processKey();
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SEARCH_OWN_AH;
                  this.rendererScan.handle();
               }
            } else {
               int var3 = this.handle(var1);
               if (var3 != -1) {
                  this.handle(var1, var3, 0, SlotActionType.QUICK_MOVE);
                  this.vectorEncode = 0;
                  this.rendererScan.handle();
               } else if (this.vectorEncode++ < 2) {
                  this.rendererScan.handle();
               } else {
                  this.processKey();
                  if (this.bindIndex()) {
                     this.requestAdapt = 0;
                     this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SELLING;
                  } else {
                     this.scaleRender = false;
                     this.requestAdapt = 0;
                     this.expandContext();
                  }

                  this.rendererScan.handle();
               }
            }
         } else {
            this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SEARCH_OWN_AH;
            this.rendererScan.handle();
         }
      }
   }

   private void scheduleSource() {
      if (this.rendererScan.update(300L)) {
         this.processKey();
         this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SELLING;
         this.rendererScan.handle();
      }
   }

   private void renderTimer() {
      if (this.rendererScan.update(50L)) {
         if (this.scaleRender) {
            this.computeColor();
         } else {
            long var1 = this.processWindow();
            if (var1 <= 0L) {
               this.check("§cЦена продажи не задана.");
            } else if (!this.bindIndex()) {
               this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SEARCH_OWN_AH;
               this.rendererScan.handle();
            } else if (Module.client.currentScreen != null) {
               this.processKey();
               this.rendererScan.handle();
            } else if (this.receiveRequest()) {
               if (!this.refreshClient()) {
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SEARCH_OWN_AH;
                  this.rendererScan.handle();
               } else {
                  this.configCollapse = false;
                  this.scaleRender = false;
                  this.handle(var1);
                  this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_WAIT_SELL_RESULT;
                  this.rendererScan.handle();
               }
            }
         }
      }
   }

   private void saveScale() {
      if (this.dataValidate) {
         this.dataValidate = false;
      }

      if (this.scaleRender) {
         this.computeColor();
      } else if (this.configCollapse) {
         this.configCollapse = false;
         this.expandContext();
      } else {
         if (this.rendererScan.update(7000L)) {
            this.processKey();
            this.animationSchedule = this.bindIndex() ? EmeraldArmorFarm.PrimaryMode.RESALE_SELLING : EmeraldArmorFarm.PrimaryMode.IDLE;
            this.rendererScan.handle();
         }
      }
   }

   private void computeColor() {
      this.animationSchedule = EmeraldArmorFarm.PrimaryMode.RESALE_SEARCH_OWN_AH;
      this.scaleRender = false;
      this.configCollapse = false;
      this.clientRefresh = true;
      this.requestAdapt = 0;
      this.rendererScan.handle();
   }

   private int[] handle(EmeraldArmorFarm.Mode var1) {
      switch (var1) {
         case HELMET:
            return new int[]{1, 2, 3, 4, 6};
         case CHESTPLATE:
            return new int[]{1, 3, 4, 5, 6, 7, 8, 9};
         case LEGGINGS:
            return new int[]{1, 2, 3, 4, 6, 7, 9};
         case BOOTS:
            return new int[]{4, 6, 7, 9};
         default:
            return new int[0];
      }
   }

   private EmeraldArmorFarm.Mode adaptScale() {
      EmeraldArmorFarm.Mode[] var1 = new EmeraldArmorFarm.Mode[]{
         EmeraldArmorFarm.Mode.HELMET, EmeraldArmorFarm.Mode.CHESTPLATE, EmeraldArmorFarm.Mode.LEGGINGS, EmeraldArmorFarm.Mode.BOOTS
      };

      for (EmeraldArmorFarm.Mode var5 : var1) {
         if (this.handle(var5, 5) < 1) {
            return var5;
         }
      }

      return EmeraldArmorFarm.Mode.NONE;
   }

   private boolean runTexture() {
      return this.handle(EmeraldArmorFarm.Mode.HELMET, 5) >= 1
         && this.handle(EmeraldArmorFarm.Mode.CHESTPLATE, 5) >= 1
         && this.handle(EmeraldArmorFarm.Mode.LEGGINGS, 5) >= 1
         && this.handle(EmeraldArmorFarm.Mode.BOOTS, 5) >= 1;
   }

   private boolean bindIndex() {
      return this.handle(EmeraldArmorFarm.Mode.HELMET, 5)
            + this.handle(EmeraldArmorFarm.Mode.CHESTPLATE, 5)
            + this.handle(EmeraldArmorFarm.Mode.LEGGINGS, 5)
            + this.handle(EmeraldArmorFarm.Mode.BOOTS, 5)
         > 0;
   }

   private int handle(EmeraldArmorFarm.Mode var1, int var2) {
      if (Module.client.player == null) {
         return 0;
      }

      int var3 = 0;

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = Module.client.player.getInventory().getStack(var4);
         if (this.handle(var5) == var1 && this.process(var5) == var2) {
            var3 += Math.max(1, var5.getCount());
         }
      }

      return var3;
   }

   private EmeraldArmorFarm.Mode handle(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = this.onTick(var1.getName().getString());
         if (var2.contains("шлем") || var2.contains("каск")) {
            return EmeraldArmorFarm.Mode.HELMET;
         } else if (var2.contains("нагрудн") || var2.contains("кирас") || var2.contains("честплейт")) {
            return EmeraldArmorFarm.Mode.CHESTPLATE;
         } else if (var2.contains("понож") || var2.contains("леггинс") || var2.contains("штаны")) {
            return EmeraldArmorFarm.Mode.LEGGINGS;
         } else if (var2.contains("ботин") || var2.contains("сапог")) {
            return EmeraldArmorFarm.Mode.BOOTS;
         } else if (var1.isOf(Items.DIAMOND_HELMET)) {
            return EmeraldArmorFarm.Mode.HELMET;
         } else if (var1.isOf(Items.DIAMOND_CHESTPLATE)) {
            return EmeraldArmorFarm.Mode.CHESTPLATE;
         } else if (var1.isOf(Items.DIAMOND_LEGGINGS)) {
            return EmeraldArmorFarm.Mode.LEGGINGS;
         } else {
            return var1.isOf(Items.DIAMOND_BOOTS) ? EmeraldArmorFarm.Mode.BOOTS : EmeraldArmorFarm.Mode.NONE;
         }
      } else {
         return EmeraldArmorFarm.Mode.NONE;
      }
   }

   private int process(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = this.onTick(var1.getName().getString());
         LoreComponent var3 = (LoreComponent)var1.get(DataComponentTypes.LORE);
         if (var3 != null) {
            for (Text var5 : var3.lines()) {
               var2 = var2 + " " + this.onTick(var5.getString());
            }
         }

         Matcher var7 = providerFetch.matcher(var2);
         int var8 = 0;

         while (var7.find()) {
            int var6 = this.process(var7.group(1));
            if (var6 > var8) {
               var8 = var6;
            }
         }

         return var8;
      } else {
         return 0;
      }
   }

   private int process(String var1) {
      String var2 = var1.trim().toLowerCase(Locale.ROOT);
      if (var2.matches("\\d+")) {
         try {
            return Math.max(0, Math.min(10, Integer.parseInt(var2)));
         } catch (NumberFormatException var5) {
            return 0;
         }
      } else {
         switch (var2) {
            case "i":
               return 1;
            case "ii":
               return 2;
            case "iii":
               return 3;
            case "iv":
               return 4;
            case "v":
               return 5;
            case "vi":
               return 6;
            case "vii":
               return 7;
            case "viii":
               return 8;
            case "ix":
               return 9;
            case "x":
               return 10;
            default:
               return 0;
         }
      }
   }

   private boolean handle(ItemStack var1, EmeraldArmorFarm.Mode var2, int var3) {
      if (var1 != null && !var1.isEmpty()) {
         return this.handle(var1) != var2 ? false : this.process(var1) == var3;
      } else {
         return false;
      }
   }

   private int handle(AnvilScreenHandler var1, EmeraldArmorFarm.Mode var2, int var3) {
      for (int var4 = 3; var4 < var1.slots.size(); var4++) {
         ItemStack var5 = var1.getSlot(var4).getStack();
         if (this.handle(var5, var2, var3)) {
            return var4;
         }
      }

      return -1;
   }

   private int readAction() {
      if (Module.client.player == null) {
         return 0;
      }

      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.compute(var3)) {
            var1 += Math.max(1, var3.getCount());
         }
      }

      return var1;
   }

   private boolean compute(ItemStack var1) {
      if (var1 == null || var1.isEmpty()) {
         return false;
      }

      if (!var1.isOf(Items.EXPERIENCE_BOTTLE)) {
         return false;
      }

      String var2 = this.onTick(var1.getName().getString());
      LoreComponent var3 = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (var3 != null) {
         for (Text var5 : var3.lines()) {
            var2 = var2 + " " + this.onTick(var5.getString());
         }
      }

      int var6 = this.animateEntry();
      return var2.contains("опыт с уровнем " + var6) || var2.contains(var6 + " ур");
   }

   private boolean collapseConfig() {
      if (Module.client.player.getMainHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
         return true;
      } else {
         int var1 = this.validateData();
         if (var1 == -1) {
            return false;
         } else if (var1 >= 0 && var1 <= 8) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            return true;
         } else {
            int var2 = Module.client.player.getInventory().getSelectedSlot();
            Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var1, var2, SlotActionType.SWAP, Module.client.player);
            return true;
         }
      }
   }

   private int validateData() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (this.compute(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private void renderScale() {
      if (Module.client.player != null) {
         Module.client.player.setPitch(0.0F);
      }
   }

   private boolean refreshClient() {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.playerScreenHandler != null) {
         if (this.resolve(Module.client.player.getMainHandStack())) {
            return true;
         }

         for (int var1 = 0; var1 < 9; var1++) {
            if (this.resolve(Module.client.player.getInventory().getStack(var1))) {
               Module.client.player.getInventory().setSelectedSlot(var1);
               return true;
            }
         }

         int var3 = Module.client.player.getInventory().getSelectedSlot();

         for (int var2 = 9; var2 < 36; var2++) {
            if (this.resolve(Module.client.player.getInventory().getStack(var2))) {
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var2, var3, SlotActionType.SWAP, Module.client.player);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean resolve(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         EmeraldArmorFarm.Mode var2 = this.handle(var1);
         return var2 == EmeraldArmorFarm.Mode.NONE ? false : this.process(var1) == 5;
      } else {
         return false;
      }
   }

   private int handle(GenericContainerScreen var1) {
      int var2 = this.resolve(var1);

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (this.handle(var4) && this.resolve(var4.getStack())) {
            return var3;
         }
      }

      return -1;
   }

   private void handle(CraftingScreenHandler var1, int var2, Item var3, int var4) {
      if (!var1.getSlot(var4).hasStack()) {
         int var5 = -1;

         for (int var6 = 10; var6 < var1.slots.size(); var6++) {
            Slot var7 = var1.getSlot(var6);
            if (var7.hasStack() && var7.getStack().isOf(var3)) {
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

   private void handle(AnvilScreenHandler var1, int var2) {
      Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
   }

   private void handle(AnvilScreenHandler var1, int var2, int var3) {
      Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
      Module.client.interactionManager.clickSlot(var1.syncId, var3, 1, SlotActionType.PICKUP, Module.client.player);
      Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
   }

   private ItemStack process(AnvilScreenHandler var1, int var2) {
      return var1 != null && var2 >= 0 && var2 < var1.slots.size() ? var1.getSlot(var2).getStack() : ItemStack.EMPTY;
   }

   private boolean filterKey() {
      return this.handle(Items.EMERALD) < this.sendWorld();
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

   private int process(GenericContainerScreen var1) {
      int var2 = this.resolve(var1);

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (var4.hasStack()) {
            ItemStack var5 = var4.getStack();
            String var6 = this.onTick(var5.getName().getString());
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
      int var3 = this.resolve(var1);

      for (int var4 = 0; var4 < var3; var4++) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (var5.hasStack() && var5.getStack().isOf(var2)) {
            return var4;
         }
      }

      return -1;
   }

   private boolean compute(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.onTick(var1.getTitle().getString());
      if (var2.contains("подтверждение покупки")) {
         return this.process(var1.getScreenHandler()) != -1;
      }

      ScreenHandler var3 = var1.getScreenHandler();
      return this.process(var3) != -1 && this.compute(var3);
   }

   private int process(ScreenHandler var1) {
      int var2 = Math.min(var1.slots.size(), Math.max(0, var1.slots.size() - 36));

      for (int var3 = var2 - 1; var3 >= 0; var3--) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         String var5 = this.onTick(var4.getName().getString());
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

   private boolean compute(ScreenHandler var1) {
      int var2 = Math.min(var1.slots.size(), Math.max(0, var1.slots.size() - 36));

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         if (var4.isOf(Items.RED_STAINED_GLASS_PANE) || var4.isOf(Items.RED_CONCRETE)) {
            return true;
         }
      }

      return false;
   }

   private void handle(GenericContainerScreen var1, int var2, int var3, SlotActionType var4) {
      Module.client.interactionManager.clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2, var3, var4, Module.client.player);
   }

   private int resolve(GenericContainerScreen var1) {
      int var2 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getRows();
      int var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size();
      return Math.max(0, Math.min(var2 * 9, var3));
   }

   private boolean handle(Slot var1) {
      return var1 != null && var1.hasStack() ? !this.update(var1.getStack()) : false;
   }

   private boolean update(ItemStack var1) {
      return var1.isOf(Items.GREEN_STAINED_GLASS_PANE)
         || var1.isOf(Items.BLACK_STAINED_GLASS_PANE)
         || var1.isOf(Items.LIME_STAINED_GLASS_PANE)
         || var1.isOf(Items.RED_STAINED_GLASS_PANE)
         || var1.isOf(Items.AIR);
   }

   private long process(Slot var1) {
      return this.vectorPerform.process("SpookyTime") ? AuctionSellerParser.process(var1) : AuctionLoreParser.process(var1);
   }

   private String compute(Slot var1) {
      return this.vectorPerform.process("SpookyTime") ? AuctionSellerParser.handle(var1) : AuctionLoreParser.handle(var1);
   }

   private boolean compute(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.update(var1).toLowerCase(Locale.ROOT);
      return var2.contains("выстав") && var2.contains("продаж");
   }

   private boolean resolve(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.update(var1).toLowerCase(Locale.ROOT);
      return var2.contains("у вас купили") && (var2.contains("изумруд") || var2.contains(" на /ah") || var2.contains(" за "));
   }

   private String update(String var1) {
      return var1 == null ? "" : var1.replaceAll("§.", "").replace(' ', ' ').trim();
   }

   private boolean adaptRequest() {
      if (Module.client.world != null && Module.client.player != null) {
         double var1 = this.sampleLayer();
         if (var1 <= 0.0) {
            return false;
         }

         double var3 = var1 * var1;

         for (PlayerEntity var6 : Module.client.world.getPlayers()) {
            if (var6 != null && var6 != Module.client.player && !var6.isRemoved() && Module.client.player.squaredDistanceTo(var6) <= var3) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private BlockPos measureTimer() {
      if (Module.client.player != null && Module.client.world != null) {
         BlockPos var1 = Module.client.player.getBlockPos();
         ArrayList var2 = new ArrayList();

         for (BlockPos var4 : BlockPos.iterate(var1.add(-5, -5, -5), var1.add(5, 5, 5))) {
            BlockPos var5 = var4.toImmutable();
            if (this.handle(var5) && !(Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var5)) > 25.0)) {
               var2.add(var5);
            }
         }

         return var2.isEmpty() ? null : (BlockPos)var2.get(ThreadLocalRandom.current().nextInt(var2.size()));
      } else {
         return null;
      }
   }

   private boolean handle(BlockPos var1) {
      return Module.client.world != null && Module.client.world.getBlockState(var1).isOf(Blocks.CRAFTING_TABLE);
   }

   private BlockPos encodeVector() {
      if (Module.client.player != null && Module.client.world != null) {
         BlockPos var1 = Module.client.player.getBlockPos();
         Vec3d var2 = Module.client.player.getEyePos();
         BlockPos var3 = null;
         double var4 = Double.MAX_VALUE;

         for (int var6 = -6; var6 <= 6; var6++) {
            for (int var7 = -2; var7 <= 2; var7++) {
               for (int var8 = -6; var8 <= 6; var8++) {
                  BlockPos var9 = var1.add(var6, var7, var8);
                  if (this.process(var9)) {
                     Vec3d var10 = new Vec3d(var9.getX() + 0.5, var9.getY() + 0.9, var9.getZ() + 0.5);
                     double var11 = var2.squaredDistanceTo(var10);
                     if (var11 < var4) {
                        var4 = var11;
                        var3 = var9.toImmutable();
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

   private boolean process(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      Block var2 = Module.client.world.getBlockState(var1).getBlock();
      return var2 == Blocks.ANVIL || var2 == Blocks.CHIPPED_ANVIL || var2 == Blocks.DAMAGED_ANVIL;
   }

   private boolean handle(BlockPos var1, double var2) {
      Vec3d var4 = Module.client.player.getEyePos();
      Vec3d var5 = Module.client.player.getRotationVec(1.0F);
      Vec3d var6 = var4.add(var5.multiply(var2));
      return Module.client.world.raycast(new RaycastContext(var4, var6, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player)) instanceof BlockHitResult var8
         ? var8.getBlockPos().equals(var1)
         : false;
   }

   private void apply(String var1) {
      if (Module.client.player != null && var1 != null && !var1.isBlank()) {
         Module.client.player.networkHandler.sendChatCommand("ah search " + var1.trim());
      }
   }

   private void execute(String var1) {
      if (Module.client.player != null && var1 != null && !var1.isBlank()) {
         Module.client.player.networkHandler.sendChatCommand("ah " + var1.trim());
      }
   }

   private boolean update(GenericContainerScreen var1) {
      if (var1 != null && Module.client.player != null) {
         String var2 = this.onTick(var1.getTitle().getString());
         String var3 = this.onTick(Module.client.player.getName().getString());
         return AhHelper.handle(var1) || var2.contains(var3) || var2.contains("мои товары") || var2.contains("мои предметы") || var2.contains("поиск:");
      } else {
         return false;
      }
   }

   private boolean receiveRequest() {
      return System.currentTimeMillis() - this.requestReceive >= 1500L;
   }

   private void handle(long var1) {
      Module.client.player.networkHandler.sendChatCommand("ah sell " + var1);
      this.requestReceive = System.currentTimeMillis();
   }

   private long processWindow() {
      return this.prepare(this.eventAttach.compute());
   }

   private long savePacket() {
      return this.prepare(this.serverRead.compute());
   }

   private int animateEntry() {
      String var1 = this.positionAdvance.compute().replaceAll("[^0-9]", "");

      try {
         return Math.max(1, Integer.parseInt(var1));
      } catch (NumberFormatException var3) {
         return 45;
      }
   }

   private String collectPlayer() {
      return "Опыт с уровнем " + this.animateEntry();
   }

   private int applyState() {
      return Math.max(1, Math.round(this.frameCheck.compute()));
   }

   private int filterMatrix() {
      return Math.max(1, Math.round(this.moduleCollect.compute()));
   }

   private int sampleLayer() {
      return Math.max(0, Math.round(this.providerClose.compute()));
   }

   private int sendWorld() {
      return Math.max(1, Math.round(this.presetWrite.compute()));
   }

   private long writeTarget() {
      return Math.max(1000L, Math.round(this.presetSave.compute() * 1000.0));
   }

   private long encodeResult() {
      return Math.max(50L, Math.round(this.windowConvert.compute()));
   }

   private long prepare(String var1) {
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

   private void parseMessage() {
      if (this.animationSchedule != EmeraldArmorFarm.PrimaryMode.AIMING_CRAFTING_TABLE
         && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.OPENING_CRAFTING_TABLE
         && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.AIMING_ANVIL
         && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.OPENING_ANVIL) {
         if (this.animationSchedule != EmeraldArmorFarm.PrimaryMode.THROW_XP_THROWING
            && this.animationSchedule != EmeraldArmorFarm.PrimaryMode.THROW_XP_WAIT_PLAYERS) {
            if (this.sourceBuild.update(10000L)) {
               ThreadLocalRandom var1 = ThreadLocalRandom.current();
               float var2 = var1.nextFloat() * 10.0F - 5.0F;
               float var3 = var1.nextFloat() * 6.0F - 3.0F;
               Module.client.player.setYaw(Module.client.player.getYaw() + var2);
               Module.client.player.setPitch(Math.max(-90.0F, Math.min(90.0F, Module.client.player.getPitch() + var3)));
               this.sourceBuild.handle();
            }
         }
      }
   }

   private void readProvider() {
      if (Module.client.player != null) {
         Vec3d var1 = null;
         if ((
               this.animationSchedule == EmeraldArmorFarm.PrimaryMode.AIMING_CRAFTING_TABLE
                  || this.animationSchedule == EmeraldArmorFarm.PrimaryMode.OPENING_CRAFTING_TABLE
            )
            && this.scaleAdapt != null) {
            var1 = Vec3d.ofCenter(this.scaleAdapt);
         } else if ((
               this.animationSchedule == EmeraldArmorFarm.PrimaryMode.AIMING_ANVIL || this.animationSchedule == EmeraldArmorFarm.PrimaryMode.OPENING_ANVIL
            )
            && this.textureRun != null) {
            var1 = Vec3d.ofCenter(this.textureRun);
         }

         if (var1 == null) {
            this.packetSave = 0L;
         } else {
            this.handle(var1);
         }
      }
   }

   private void handle(Vec3d var1) {
      EmeraldArmorFarm.DataRecord var2 = this.process(var1);
      float var3 = this.blendMatrix2();
      float var4 = 0.11F;
      float var5 = 1.0F - (float)Math.pow(1.0F - var4, var3);
      float var6 = Module.client.player.getYaw();
      float var7 = Module.client.player.getPitch();
      float var8 = MathHelper.wrapDegrees(var2.yaw - var6);
      float var9 = var2.pitch - var7;
      float var10 = var6 + var8 * var5;
      float var11 = MathHelper.clamp(var7 + var9 * var5, -90.0F, 90.0F);
      Module.client.player.setYaw(var10);
      Module.client.player.setPitch(var11);
      Module.client.player.headYaw = var10;
      Module.client.player.bodyYaw = var10;
   }

   private EmeraldArmorFarm.DataRecord process(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      float var9 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var10 = (float)(-Math.toDegrees(Math.atan2(var5, Math.sqrt(var3 * var3 + var7 * var7))));
      return new EmeraldArmorFarm.DataRecord(var9, MathHelper.clamp(var10, -90.0F, 90.0F));
   }

   private float blendMatrix2() {
      long var1 = System.nanoTime();
      if (this.packetSave == 0L) {
         this.packetSave = var1;
         return 1.0F;
      } else {
         float var3 = (float)(var1 - this.packetSave) / 1.6666667E7F;
         this.packetSave = var1;
         return MathHelper.clamp(var3, 0.25F, 4.0F);
      }
   }

   private Direction compute(BlockPos var1) {
      Vec3d var2 = Module.client.player.getPos();
      Vec3d var3 = Vec3d.ofCenter(var1);
      double var4 = var2.x - var3.x;
      double var6 = var2.z - var3.z;
      if (Math.abs(var4) > Math.abs(var6)) {
         return var4 > 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return var6 > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private float handle(float var1, float var2) {
      return var1 + (var2 - var1) * ThreadLocalRandom.current().nextFloat();
   }

   private void performScale() {
      this.animationSchedule = EmeraldArmorFarm.PrimaryMode.IDLE;
      this.scaleSave = EmeraldArmorFarm.Mode.NONE;
      this.colorCompute = false;
      this.scaleAdapt = null;
      this.textureRun = null;
      this.indexBind = 0;
      this.actionRead = 0;
      this.configCollapse = false;
      this.dataValidate = false;
      this.scaleRender = false;
      this.clientRefresh = false;
      this.keyFilter = false;
      this.requestAdapt = 0;
      this.timerMeasure = 0;
      this.vectorEncode = 0;
      this.requestReceive = 0L;
      this.windowProcess = 0L;
      this.packetSave = 0L;
      this.entryAnimate = 50;
      this.rendererScan.handle();
      this.sourceBuild.handle();
      this.outputCollapse.handle();
      this.profileInvoke.handle();
      this.sourceSchedule.handle();
      this.timerRender.handle();
   }

   private void check(String var1) {
      this.select(var1);
      this.expandContext();
   }

   private void expandContext() {
      this.animationSchedule = EmeraldArmorFarm.PrimaryMode.IDLE;
      this.actionRead = 0;
      this.rendererScan.handle();
   }

   private void processKey() {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }
   }

   private boolean process(long var1) {
      if (Module.client.currentScreen == null) {
         this.actionRead = 0;
         return true;
      } else if (!this.rendererScan.update(var1)) {
         return false;
      } else {
         this.processKey();
         this.actionRead++;
         if (this.actionRead >= 20) {
            Module.client.setScreen(null);
            this.actionRead = 0;
            return true;
         } else {
            this.rendererScan.handle();
            return false;
         }
      }
   }

   private String onTick(String var1) {
      return var1 == null ? "" : var1.replaceAll("(?i)§.", "").replaceAll("(?i)&.", "").toLowerCase(Locale.ROOT).trim();
   }

   private void select(String var1) {
      if (this.colorMeasure.compute() && Module.client.player != null) {
         ChatLogger.handle("§8[§aEmeraldArmorFarm§8] §f" + var1);
      }
   }

   record DataRecord(float yaw, float pitch) {
   }

   enum Mode {
      HELMET,
      CHESTPLATE,
      LEGGINGS,
      BOOTS,
      NONE;
   }

   enum PrimaryMode {
      IDLE,
      BUY_OPENING_SHOP,
      BUY_WAITING_SHOP,
      BUY_FIND_GOLD_INGOT,
      BUY_WAITING_EMERALD_MENU,
      BUY_FIND_EMERALD,
      BUY_WAITING_CONFIRM,
      BUY_CLICK_LIME_PANE,
      BUY_CLOSING_SHOP,
      BUY_XP_SEARCHING,
      BUY_XP_WAITING_AUCTION,
      BUY_XP_READING_AUCTION,
      BUY_XP_WAITING_CONFIRM,
      BUY_XP_CONFIRMING,
      BUY_XP_CLOSING,
      THROW_XP_WAIT_PLAYERS,
      THROW_XP_THROWING,
      FINDING_CRAFTING_TABLE,
      AIMING_CRAFTING_TABLE,
      OPENING_CRAFTING_TABLE,
      PLACING_ITEMS,
      TAKING_RESULT,
      CLOSING_CRAFTING,
      FINDING_ANVIL,
      AIMING_ANVIL,
      OPENING_ANVIL,
      HANDLING_ANVIL,
      CLOSING_ANVIL,
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
