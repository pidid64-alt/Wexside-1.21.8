package ru.wild.modules.misc;

import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.AuctionTradeExecutor;
import ru.wild.util.inventory.HolyWorldHelper;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoSell", category = ModuleCategory.Misc, description = "Автоматически выставляет предметы на продажу")
public class AutoSell extends Module {
   public static AutoSell source;
   private static final String vectorMatch = "По одной штуке";
   private static final String itemProject = "Все сразу";
   private static final String responseCompute = "Одна цена";
   private static final int providerFetch = 9;
   private static final long profileDraw = 5000L;
   private static final long vectorPerform = 12000L;
   private static final long eventAttach = 5000L;
   private static final long serverRead = 3000L;
   private static final long positionAdvance = 1500L;
   private ModeSetting frameCheck = new ModeSetting("Сервер", "FunTime", "HolyWorld", "FunTime");
   public final ModeSetting target = new ModeSetting("Режим", "По бинду", "По бинду", "Авто");
   public final ModeSetting pending = new ModeSetting("Режим продажи", "По одной штуке", "По одной штуке", "Все сразу", "Одна цена");
   public final NumberSetting previous = new NumberSetting("Наценка %", 10.0F, 0.0F, 100.0F, 10.0F, false)
      .handle(() -> !this.target.process("По бинду") || this.pending.process("Одна цена"));
   public final KeybindSetting latest = new KeybindSetting("Бинд продажи", -1).handle(() -> !this.target.process("По бинду"));
   private final BooleanSetting moduleCollect = new BooleanSetting("Отладка", false);
   public final StringSetting summary = new StringSetting("Цена одной продажи", "1000").handle(32).handle(() -> !this.pending.process("Одна цена"));
   public final ModeSetting matrixBlend = new ModeSetting("Выкладка одной цены", "По одной штуке", "По одной штуке", "Все сразу")
      .handle(() -> !this.pending.process("Одна цена"));
   private final Stopwatch providerClose = new Stopwatch();
   private final Stopwatch presetSave = new Stopwatch();
   private final Stopwatch windowConvert = new Stopwatch();
   private static final long presetWrite = 1000L;
   private static final long colorMeasure = 4000L;
   private static final long animationSchedule = 1000L;
   private AutoSell.Mode rendererScan = AutoSell.Mode.IDLE;
   private boolean sourceBuild = false;
   private long outputCollapse = 0L;
   private String profileInvoke = "";
   private boolean sourceSchedule = false;
   private String timerRender = "";
   private String scaleSave = "";
   private Item colorCompute = Items.AIR;
   private int scaleAdapt = 0;
   private boolean textureRun = false;
   private String indexBind = "";
   private String actionRead = "";
   private Item configCollapse = Items.AIR;
   private boolean dataValidate = false;
   private int scaleRender = 0;
   private boolean clientRefresh = false;
   private boolean keyFilter = false;
   private boolean requestAdapt = false;
   private int timerMeasure = 0;
   private int vectorEncode = 0;
   private int requestReceive = 0;
   private int windowProcess = 0;
   private int packetSave = 9;
   private boolean entryAnimate = false;
   private boolean playerCollect = false;
   private boolean stateApply = false;
   private int matrixFilter = 0;
   private long layerSample = 0L;
   private long worldSend = 0L;
   private long targetWrite = 0L;
   private long resultEncode = 0L;
   private int messageParse = -1;
   private long providerRead = 0L;
   private long matrixBlend2 = 0L;
   private boolean scalePerform = false;

   public AutoSell() {
      source = this;
      this.handle(this.frameCheck, this.target, this.previous, this.latest, this.moduleCollect, this.pending, this.summary, this.matrixBlend);
   }

   public boolean refresh() {
      return this.target.process("Авто");
   }

   public boolean render() {
      return this.fetchProvider();
   }

   @Override
   public void handle() {
      super.handle();
      AuctionTradeExecutor.handle(false);
      AuctionTradeExecutor.process(false);
      AuctionTradeExecutor.handle();
      this.tick();
      this.providerClose.handle();
   }

   @Override
   public void process() {
      super.process();
      if (this.sourceBuild) {
         AuctionTradeExecutor.handle(false);
      }

      if (AuctionTradeExecutor.apply()) {
         AuctionTradeExecutor.process(false);
      }

      AuctionTradeExecutor.handle();
      this.tick();
   }

   public void tick() {
      this.rendererScan = AutoSell.Mode.IDLE;
      this.sourceBuild = false;
      this.outputCollapse = 0L;
      this.profileInvoke = "";
      this.sourceSchedule = false;
      this.timerRender = "";
      this.scaleSave = "";
      this.colorCompute = Items.AIR;
      this.scaleAdapt = 0;
      this.textureRun = false;
      this.indexBind = "";
      this.actionRead = "";
      this.configCollapse = Items.AIR;
      this.dataValidate = false;
      this.scaleRender = 0;
      this.clientRefresh = false;
      this.keyFilter = false;
      this.requestAdapt = false;
      this.timerMeasure = 0;
      this.vectorEncode = 0;
      this.requestReceive = 0;
      this.windowProcess = 0;
      this.packetSave = 9;
      this.entryAnimate = false;
      this.playerCollect = false;
      this.stateApply = false;
      this.matrixFilter = 0;
      this.worldSend = 0L;
      this.targetWrite = 0L;
      this.resultEncode = 0L;
      this.messageParse = -1;
      this.matrixBlend2 = 0L;
      this.scalePerform = false;
   }

   public static void drawAnimation() {
      if (source != null) {
         source.tick();
      }
   }

   private void encodePoint() {
      this.compute(true);
   }

   private void compute(boolean var1) {
      this.tick();
      this.providerClose.handle();
      if (this.fetchProvider()
         || this.closeProvider()
         || !var1
         || !this.refresh()
         || !AuctionTradeExecutor.execute()
         || !this.measureColor()
         || !this.scheduleAnimation()
         || !this.animate()) {
         AuctionTradeExecutor.handle(true);
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (this.target.process("По бинду") && var1.resolve() == this.latest.compute() && !this.sourceBuild && Module.client.player != null) {
         if (System.currentTimeMillis() - this.providerRead < 3000L) {
            return;
         }

         if (!this.resolve(Module.client.player.getMainHandStack())) {
            ChatLogger.handle("§3[AutoSell] §fВозьмите предмет в руку");
            return;
         }

         if (this.animate()) {
            this.scalePerform = true;
            this.providerRead = System.currentTimeMillis();
            this.update("Запущена продажа по бинду.");
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         boolean var2 = AuctionTradeExecutor.update();
         boolean var3 = AuctionTradeExecutor.execute();
         if (this.refresh() && this.fetchProvider() && !this.sourceBuild) {
            if (this.providerClose.update(500L)) {
               if (AuctionTradeExecutor.compute()) {
                  this.sourceBuild = true;
                  this.computeResponse();
                  return;
               }

               if (var3) {
                  if (!this.textureRun && !this.resolve(Module.client.player.getMainHandStack())) {
                     this.scheduleAnimation();
                  }

                  if (!this.animate()) {
                     this.handle("§c[AutoSell] §fДля режима одной цены возьмите нужный предмет в руку или дождитесь покупки AutoBuy.");
                     this.providerClose.handle();
                  }
               }
            }
         } else if (this.refresh() && !this.sourceBuild && (!this.closeProvider() || var2) && this.providerClose.update(500L) && var3) {
            if (this.closeProvider()) {
               if (!this.animate() && var2) {
                  AuctionTradeExecutor.handle(true);
               }
            } else if (this.scheduleAnimation()) {
               this.animate();
            } else if (var2) {
               AuctionTradeExecutor.handle(true);
            }
         }

         if (this.sourceBuild) {
            if (this.fetchProvider() && this.rendererScan == AutoSell.Mode.IDLE) {
               this.measure();
               if (!this.sourceBuild || this.rendererScan == AutoSell.Mode.IDLE) {
                  return;
               }
            }

            switch (this.rendererScan) {
               case PREPARING:
                  if (this.fetchProvider()) {
                     if (this.closeProvider()) {
                        ChatLogger.handle("§c[AutoSell] §fРежим одной цены через sellgui доступен только для FunTime.");
                        this.compute(false);
                        return;
                     }

                     if (Module.client.currentScreen != null) {
                        this.checkFrame();
                        this.presetSave.handle();
                        return;
                     }

                     if (!this.drawProfile()) {
                        this.encodePoint();
                        return;
                     }

                     if (!this.performVector()) {
                        this.encodePoint();
                        return;
                     }

                     this.rendererScan = AutoSell.Mode.SELLGUI_SELLING;
                     this.presetSave.handle();
                  } else if (this.closeProvider()) {
                     if (Module.client.currentScreen != null) {
                        this.checkFrame();
                     }

                     this.savePreset();
                     if ((!this.resolve(Module.client.player.getMainHandStack()) || !this.update(Module.client.player.getMainHandStack()))
                        && !this.scheduleAnimation()) {
                        if (this.windowConvert.update(4000L)) {
                           this.resolve(true);
                        }

                        return;
                     }

                     this.scaleAdapt = 0;
                     this.rendererScan = AutoSell.Mode.HOLY_SELLING;
                     this.presetSave.handle();
                     this.windowConvert.handle();
                  } else {
                     if (this.refresh()) {
                        if (!this.resolve(Module.client.player.getMainHandStack()) && !this.scheduleAnimation()) {
                           this.encodePoint();
                           return;
                        }
                     } else if (!this.resolve(Module.client.player.getMainHandStack())) {
                        this.encodePoint();
                        return;
                     }

                     if (this.pending.process("По одной штуке")) {
                        this.rendererScan = AutoSell.Mode.SPLITTING;
                     } else {
                        this.rendererScan = AutoSell.Mode.SEARCHING;
                     }

                     this.presetSave.handle();
                  }
                  break;
               case SPLITTING:
                  if (this.presetSave.update(150L)) {
                     if (this.convertWindow()) {
                        this.rendererScan = AutoSell.Mode.SEARCHING;
                     } else {
                        this.encodePoint();
                     }

                     this.presetSave.handle();
                  }
                  break;
               case SEARCHING:
                  if (this.presetSave.update(50L)) {
                     ItemStack var10 = Module.client.player.getMainHandStack();
                     if (var10.isEmpty()) {
                        this.rendererScan = AutoSell.Mode.PREPARING;
                        return;
                     }

                     if (this.pending.process("По одной штуке") && var10.getCount() > 1) {
                        this.rendererScan = AutoSell.Mode.SPLITTING;
                        return;
                     }

                     String var13 = this.apply(var10);
                     if (var13.isEmpty()) {
                        ChatLogger.handle("§3[AutoSell] Не удалось определить имя предмета");
                        this.encodePoint();
                        return;
                     }

                     this.profileInvoke = var13;
                     if (Module.client.currentScreen != null) {
                        if (Module.client.currentScreen instanceof GenericContainerScreen var6) {
                           this.messageParse = ((GenericContainerScreenHandler)var6.getScreenHandler()).syncId;
                        }

                        this.checkFrame();
                        this.presetSave.handle();
                        return;
                     }

                     this.load();
                     this.rendererScan = AutoSell.Mode.SCANNING;
                     this.matrixBlend2 = 0L;
                     this.presetSave.handle();
                     this.windowConvert.handle();
                  }
                  break;
               case SCANNING:
                  if (Module.client.currentScreen instanceof GenericContainerScreen var9 && this.apply(var9)) {
                     if (this.matrixBlend2 == 0L) {
                        this.matrixBlend2 = System.currentTimeMillis();
                        this.update("Аукцион открыт, ожидание 1,5 сек.");
                     }

                     boolean var12 = this.scalePerform ? System.currentTimeMillis() - this.matrixBlend2 >= 1500L : this.presetSave.update(350L);
                     if (var12) {
                        try {
                           this.check(var9);
                        } catch (Exception var8) {
                           this.encodePoint();
                        }
                     }
                  } else if (this.presetSave.update(this.collectModule() ? 6500L : 2000L)) {
                     this.encodePoint();
                  }
                  break;
               case SELLING:
                  if (this.presetSave.update(50L)) {
                     if (this.outputCollapse > 0L) {
                        this.save();
                     }

                     this.rendererScan = AutoSell.Mode.FINISHING;
                     this.presetSave.handle();
                     this.windowConvert.handle();
                  }
                  break;
               case FINISHING:
                  if (this.collectModule() && this.outputCollapse > 0L && this.windowConvert.update(900L)) {
                     this.save();
                     this.windowConvert.handle();
                  }

                  if (this.presetSave.update(this.collectModule() ? 10000L : 8000L)) {
                     ChatLogger.handle("§e[AutoSell] §fНет ответа от аукциона, возвращаю AutoBuy.");
                     this.compute(false);
                  }
                  break;
               case SELLGUI_SELLING:
                  this.update(false);
                  break;
               case SELLGUI_WAITING_RESULT:
                  this.apply(false);
                  break;
               case RESALE_SEARCH_OWN_AH:
                  this.blendMatrix();
                  break;
               case RESALE_WAITING_OWN_AH:
                  this.matchVector();
                  break;
               case RESALE_TAKE_ITEM:
                  this.projectItem();
                  break;
               case RESALE_SELLING:
                  this.update(true);
                  break;
               case RESALE_WAIT_SELL_RESULT:
                  this.apply(true);
                  break;
               case HOLY_SELLING:
                  if (this.windowConvert.update(4000L)) {
                     this.resolve(true);
                  } else if (!this.resolve(Module.client.player.getMainHandStack()) || !this.update(Module.client.player.getMainHandStack())) {
                     this.scaleAdapt = 0;
                     this.rendererScan = AutoSell.Mode.PREPARING;
                     this.presetSave.handle();
                  } else if (this.scaleAdapt == 0) {
                     this.submit();
                     this.scaleAdapt = 1;
                     this.presetSave.handle();
                  } else if (this.scaleAdapt == 1 && this.presetSave.update(1000L)) {
                     this.unload();
                     this.scaleAdapt = 2;
                     this.presetSave.handle();
                  }
                  break;
               case HOLY_OPENING_AUCTION:
                  if (Module.client.currentScreen instanceof GenericContainerScreen var4 && this.handle(var4)) {
                     this.resolve(false);
                  } else if (this.presetSave.update(1000L)) {
                     this.fetch();
                     this.presetSave.handle();
                  }
            }
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var5 = var2.content().getString();
         if (!this.check(var5)) {
            if (this.prepare(var5)) {
               AuctionTradeExecutor.onTick();
               if (this.sourceBuild) {
                  if (this.fetchProvider()) {
                     int var6 = Math.max(1, this.vectorEncode);
                     this.requestReceive += var6;
                     if (this.playerCollect) {
                        this.packetSave = Math.max(1, Math.min(this.packetSave, this.requestReceive));
                     }

                     this.windowProcess = Math.max(0, this.windowProcess - var6);
                     this.vectorEncode = 0;
                     this.playerCollect = false;
                     this.keyFilter = false;
                     this.clientRefresh = true;
                     this.targetWrite = System.currentTimeMillis();
                     return;
                  }

                  if (this.closeProvider()) {
                     this.fetch();
                     this.rendererScan = AutoSell.Mode.HOLY_OPENING_AUCTION;
                     this.presetSave.handle();
                     this.windowConvert.handle();
                  } else {
                     this.compute(true);
                  }
               }
            } else if (var5.contains("Не удалось выставить") && var5.contains("освободите хранилище")) {
               AuctionTradeExecutor.select();
               if (this.sourceBuild) {
                  if (this.fetchProvider()) {
                     this.keyFilter = true;
                     this.vectorEncode = 0;
                     this.playerCollect = false;
                     return;
                  }

                  ChatLogger.handle("§c[AutoSell] Хранилище заполнено. Продажа приостановлена.");
                  this.compute(false);
               }
            }
         } else {
            AuctionTradeExecutor.refresh();
            if (this.fetchProvider() && (this.sourceBuild || this.requestReceive > 0)) {
               int var4 = this.process(var5);
               this.requestReceive = Math.max(0, this.requestReceive - var4);
               this.windowProcess += var4;
               this.targetWrite = System.currentTimeMillis();
               if (this.sourceBuild) {
                  this.rendererScan = AutoSell.Mode.IDLE;
                  this.presetSave.handle();
               }

               return;
            }

            if (this.sourceBuild) {
               if (this.closeProvider()) {
                  this.resolve(true);
               } else {
                  this.compute(false);
               }
            }
         }
      }
   }

   private boolean animate() {
      if (this.fetchProvider()) {
         if (this.closeProvider()) {
            ChatLogger.handle("§c[AutoSell] §fРежим одной цены через sellgui доступен только для FunTime.");
            return false;
         }

         if (this.advancePosition() <= 0L) {
            ChatLogger.handle("§c[AutoSell] §fЦена одной продажи не задана.");
            return false;
         }

         if (!this.drawProfile()) {
            return false;
         }
      } else if (this.closeProvider() && !this.savePreset()) {
         return false;
      }

      if (!AuctionTradeExecutor.prepare()) {
         return false;
      }

      if (this.fetchProvider()) {
         this.requestReceive = 0;
         this.windowProcess = 0;
         this.packetSave = 9;
         this.targetWrite = 0L;
         this.vectorEncode = 0;
         this.playerCollect = false;
      }

      this.sourceBuild = true;
      this.rendererScan = AutoSell.Mode.PREPARING;
      this.presetSave.handle();
      this.windowConvert.handle();
      return true;
   }

   private void load() {
      if (Module.client.player != null && !this.profileInvoke.isEmpty()) {
         Module.client.player.networkHandler.sendChatCommand("ah search " + this.profileInvoke);
         this.update("Поиск: " + this.profileInvoke);
      }
   }

   private void save() {
      if (Module.client.player != null && this.outputCollapse > 0L) {
         Module.client.player.networkHandler.sendChatCommand("ah sell " + this.outputCollapse);
         this.update("Выставление за " + this.outputCollapse + ".");
      }
   }

   private void resolve(boolean var1) {
      this.tick();
      this.providerClose.handle();
      AuctionTradeExecutor.handle(var1);
   }

   private void submit() {
      if (Module.client.player != null) {
         if (Module.client.currentScreen != null) {
            Module.client.player.closeScreen();
         }

         Module.client.player.networkHandler.sendChatCommand("ah sell auto");
      }
   }

   private void unload() {
      if (Module.client.player != null) {
         Module.client.player.networkHandler.sendChatCommand("ah sell auto confirm");
      }
   }

   private void fetch() {
      if (Module.client.player != null) {
         Module.client.player.networkHandler.sendChatCommand("ah");
      }
   }

   private boolean handle(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      }

      if (AutoBuy.source != null && AutoBuy.source.handle(var1)) {
         return true;
      }

      String var2 = this.execute(var1.getTitle().getString()).toLowerCase(Locale.ROOT);
      return var2.contains("аукцион") || var2.contains("auction");
   }

   private void measure() {
      if (this.keyFilter) {
         this.computeResponse();
      } else if (this.windowProcess > 0 && this.performVector()) {
         if (this.requestReceive < this.packetSave) {
            this.rendererScan = AutoSell.Mode.SELLGUI_SELLING;
            this.presetSave.handle();
         } else {
            if (System.currentTimeMillis() - this.targetWrite >= 5000L) {
               this.computeResponse();
            }
         }
      } else if (this.requestReceive > 0) {
         if (this.performVector() && this.requestReceive < this.packetSave) {
            this.rendererScan = AutoSell.Mode.SELLGUI_SELLING;
            this.presetSave.handle();
         } else {
            if (System.currentTimeMillis() - this.targetWrite >= 5000L) {
               this.computeResponse();
            }
         }
      } else if (this.performVector()) {
         this.rendererScan = AutoSell.Mode.SELLGUI_SELLING;
         this.presetSave.handle();
      } else {
         this.execute(true);
      }
   }

   private void update(boolean var1) {
      if (this.presetSave.update(50L)) {
         if (this.keyFilter) {
            this.computeResponse();
         } else {
            long var2 = this.advancePosition();
            if (var2 <= 0L) {
               ChatLogger.handle("§c[AutoSell] §fЦена одной продажи не задана.");
               this.execute(false);
            } else if (this.drawProfile() && this.performVector()) {
               if (Module.client.currentScreen instanceof GenericContainerScreen var4 && this.update(var4)) {
                  this.handle(var4, var1);
               } else if (Module.client.currentScreen != null) {
                  this.checkFrame();
                  this.presetSave.handle();
               } else if (this.attachEvent()) {
                  this.readServer();
                  this.clientRefresh = false;
                  this.keyFilter = false;
                  this.handle(var2);
                  this.rendererScan = var1 ? AutoSell.Mode.RESALE_WAIT_SELL_RESULT : AutoSell.Mode.SELLGUI_WAITING_RESULT;
                  this.presetSave.handle();
                  this.windowConvert.handle();
               }
            } else {
               this.rendererScan = AutoSell.Mode.IDLE;
               this.presetSave.handle();
            }
         }
      }
   }

   private void apply(boolean var1) {
      if (this.keyFilter) {
         this.computeResponse();
      } else if (Module.client.currentScreen instanceof GenericContainerScreen var2 && this.update(var2)) {
         this.handle(var2, var1);
      } else if (this.clientRefresh) {
         this.clientRefresh = false;
         this.readServer();
         this.rendererScan = this.requestReceive > 0
            ? AutoSell.Mode.IDLE
            : (this.performVector() ? (var1 ? AutoSell.Mode.RESALE_SELLING : AutoSell.Mode.SELLGUI_SELLING) : AutoSell.Mode.IDLE);
         this.presetSave.handle();
      } else {
         if (this.presetSave.update(12000L)) {
            this.checkFrame();
            this.vectorEncode = 0;
            this.rendererScan = this.requestReceive > 0
               ? AutoSell.Mode.IDLE
               : (this.performVector() ? (var1 ? AutoSell.Mode.RESALE_SELLING : AutoSell.Mode.SELLGUI_SELLING) : AutoSell.Mode.IDLE);
            if (this.rendererScan == AutoSell.Mode.IDLE && this.requestReceive <= 0) {
               this.execute(false);
            }

            this.presetSave.handle();
         }
      }
   }

   private void blendMatrix() {
      long var1 = System.currentTimeMillis();
      if (var1 >= this.resultEncode) {
         if (Module.client.currentScreen != null) {
            this.checkFrame();
            this.resultEncode = var1 + 350L;
         } else if (!this.stateApply && !this.requestAdapt && this.performVector()) {
            this.rendererScan = AutoSell.Mode.RESALE_SELLING;
            this.presetSave.handle();
         } else {
            this.requestAdapt = false;
            if (Module.client.player == null) {
               this.execute(false);
            } else {
               this.matrixFilter++;
               this.handle(Module.client.player.getName().getString(), this.matrixFilter);
               this.timerMeasure = 0;
               this.rendererScan = AutoSell.Mode.RESALE_WAITING_OWN_AH;
               this.presetSave.handle();
               this.windowConvert.handle();
            }
         }
      }
   }

   private void matchVector() {
      if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.execute(var1)) {
         this.rendererScan = AutoSell.Mode.RESALE_TAKE_ITEM;
         this.presetSave.handle();
      } else if (this.windowConvert.update(18000L)) {
         ChatLogger.handle("§c[AutoSell] §fТаймаут поиска своих товаров.");
         this.execute(false);
      } else if (Module.client.currentScreen != null && this.presetSave.update(1200L)) {
         this.checkFrame();
         this.rendererScan = AutoSell.Mode.RESALE_SEARCH_OWN_AH;
         this.resultEncode = System.currentTimeMillis() + 350L;
         this.presetSave.handle();
      } else if (this.presetSave.update(4000L)) {
         this.rendererScan = AutoSell.Mode.RESALE_SEARCH_OWN_AH;
         this.resultEncode = System.currentTimeMillis();
         this.presetSave.handle();
      }
   }

   private void projectItem() {
      if (this.presetSave.update(200L)) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
            if (!this.execute(var1)) {
               if (this.presetSave.update(10000L)) {
                  this.checkFrame();
                  this.rendererScan = AutoSell.Mode.RESALE_SEARCH_OWN_AH;
                  this.presetSave.handle();
               }
            } else {
               int var3 = this.process(var1);
               if (var3 != -1) {
                  this.handle(var1, var3, 0, SlotActionType.QUICK_MOVE);
                  this.timerMeasure = 0;
                  this.presetSave.handle();
               } else if (this.timerMeasure++ < 2) {
                  this.presetSave.handle();
               } else {
                  this.checkFrame();
                  this.keyFilter = false;
                  if (!this.performVector()) {
                     ChatLogger.handle("§e[AutoSell] §fСвои лоты не найдены, предметов в инвентаре нет.");
                     this.execute(true);
                  } else {
                     this.stateApply = false;
                     this.rendererScan = AutoSell.Mode.RESALE_SELLING;
                     this.presetSave.handle();
                  }
               }
            }
         } else {
            this.rendererScan = AutoSell.Mode.RESALE_SEARCH_OWN_AH;
            this.presetSave.handle();
         }
      }
   }

   private void handle(GenericContainerScreen var1, boolean var2) {
      ScreenHandler var3 = var1.getScreenHandler();
      int var4 = this.prepare(var1);
      if (this.dataValidate) {
         if (this.presetSave.update(1500L)) {
            this.checkFrame();
         }
      } else {
         int var5 = 0;

         for (int var6 = this.prepare(var2); var5 < 4 && this.scaleRender < var6; var5++) {
            int var7 = this.compute(var1);
            int var8 = this.handle(var3, var4);
            if (var7 == -1) {
               this.entryAnimate = true;
               break;
            }

            if (var8 == -1) {
               break;
            }

            if (this.matrixBlend.process("По одной штуке")) {
               if (!this.handle(var3, var8, var7)) {
                  break;
               }
            } else {
               Module.client.interactionManager.clickSlot(var3.syncId, var8, 0, SlotActionType.PICKUP, Module.client.player);
               Module.client.interactionManager.clickSlot(var3.syncId, var7, 0, SlotActionType.PICKUP, Module.client.player);
            }

            this.scaleRender++;
         }

         if (var5 > 0) {
            this.presetSave.handle();
         } else if (this.scaleRender > 0) {
            int var9 = this.resolve(var1);
            if (var9 != -1) {
               this.handle(var1, var9, 0, SlotActionType.PICKUP);
               this.dataValidate = true;
               this.vectorEncode = this.scaleRender;
               this.playerCollect = this.entryAnimate;
               if (this.entryAnimate) {
                  this.packetSave = Math.max(1, this.scaleRender);
               }

               this.presetSave.handle();
            } else {
               if (this.presetSave.update(3000L)) {
                  this.checkFrame();
                  this.rendererScan = var2 ? AutoSell.Mode.RESALE_SELLING : AutoSell.Mode.SELLGUI_SELLING;
                  this.presetSave.handle();
               }
            }
         } else {
            this.checkFrame();
            if (this.entryAnimate && this.requestReceive > 0) {
               this.packetSave = Math.max(1, Math.min(this.packetSave, this.requestReceive));
               this.handle("§e[AutoSell] §fВ sellgui нет свободных слотов, жду перевыставление.");
            }

            this.rendererScan = var2 ? AutoSell.Mode.RESALE_SEARCH_OWN_AH : AutoSell.Mode.IDLE;
            if (this.rendererScan == AutoSell.Mode.IDLE && this.requestReceive <= 0) {
               this.execute(true);
            } else {
               this.presetSave.handle();
            }
         }
      }
   }

   private void computeResponse() {
      this.keyFilter = false;
      this.clientRefresh = false;
      this.requestAdapt = true;
      this.stateApply = true;
      this.timerMeasure = 0;
      this.requestReceive = 0;
      this.windowProcess = 0;
      this.entryAnimate = false;
      this.playerCollect = false;
      this.matrixFilter = 0;
      this.resultEncode = System.currentTimeMillis() + 350L;
      this.readServer();
      this.rendererScan = AutoSell.Mode.RESALE_SEARCH_OWN_AH;
      this.presetSave.handle();
      this.windowConvert.handle();
      this.checkFrame();
      AuctionTradeExecutor.handle(false);
      AuctionTradeExecutor.check();
   }

   private void execute(boolean var1) {
      if (AuctionTradeExecutor.apply()) {
         AuctionTradeExecutor.process(var1);
      }

      this.compute(var1);
   }

   private boolean fetchProvider() {
      return this.pending.process("Одна цена");
   }

   private void handle(String var1) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.worldSend >= 3000L) {
         this.worldSend = var2;
         ChatLogger.handle(var1);
      }
   }

   private boolean drawProfile() {
      if (this.textureRun) {
         return true;
      } else if (Module.client.player != null && this.resolve(Module.client.player.getMainHandStack())) {
         this.handle(Module.client.player.getMainHandStack());
         return true;
      } else {
         AutoBuy.CacheEntry var1 = AutoBuy.tick();
         if (var1 != null) {
            this.textureRun = true;
            this.indexBind = var1.data == null ? "" : var1.data;
            this.actionRead = var1.instance == null ? "" : var1.instance;
            this.configCollapse = Items.AIR;
            return true;
         } else {
            return false;
         }
      }
   }

   private void handle(ItemStack var1) {
      if (this.resolve(var1)) {
         this.textureRun = true;
         this.indexBind = this.apply(var1);
         this.actionRead = var1.getName().getString();
         this.configCollapse = var1.getItem();
      }
   }

   private boolean performVector() {
      if (Module.client.player == null) {
         return false;
      }

      if (this.process(Module.client.player.getMainHandStack())) {
         return true;
      }

      for (int var1 = 0; var1 < 36; var1++) {
         if (this.process(Module.client.player.getInventory().getStack(var1))) {
            return true;
         }
      }

      return false;
   }

   private boolean process(ItemStack var1) {
      if (this.resolve(var1) && this.textureRun) {
         if (this.configCollapse != Items.AIR && var1.getItem() != this.configCollapse) {
            return false;
         }

         String var2 = this.apply(this.apply(var1));
         String var3 = this.apply(this.indexBind);
         String var4 = this.apply(this.actionRead);
         return var3.isEmpty() && var4.isEmpty()
            ? true
            : !var2.isEmpty()
               && (
                  var2.equals(var3)
                     || var2.equals(var4)
                     || !var3.isEmpty() && (var2.contains(var3) || var3.contains(var2))
                     || !var4.isEmpty() && (var2.contains(var4) || var4.contains(var2))
               );
      } else {
         return false;
      }
   }

   private int handle(ScreenHandler var1, int var2) {
      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         Slot var4 = var1.getSlot(var3);
         if (var4.hasStack() && this.process(var4.getStack())) {
            return var3;
         }
      }

      return -1;
   }

   private int process(GenericContainerScreen var1) {
      int var2 = this.prepare(var1);

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (var4.hasStack() && this.process(var4.getStack())) {
            return var3;
         }
      }

      return -1;
   }

   private int compute(GenericContainerScreen var1) {
      int var2 = Math.min(9, this.prepare(var1));

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3);
         if (!var4.hasStack()) {
            return var3;
         }
      }

      return -1;
   }

   private int resolve(GenericContainerScreen var1) {
      int var2 = this.prepare(var1);
      int var3 = -1;

      for (int var4 = var2 - 1; var4 >= 0; var4--) {
         Slot var5 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var4);
         if (var5.hasStack()) {
            ItemStack var6 = var5.getStack();
            String var7 = this.execute(var6.getName().getString()).toLowerCase(Locale.ROOT);
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

   private boolean update(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.execute(var1.getTitle().getString()).toLowerCase(Locale.ROOT);
      return var2.contains("продажа") || var2.contains("sellgui") || var2.contains("sell gui");
   }

   private boolean apply(GenericContainerScreen var1) {
      if (var1 != null && ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId != this.messageParse) {
         if (AhHelper.handle(var1)) {
            return true;
         }

         int var2 = Math.min(45, ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size());

         for (int var3 = 0; var3 < var2; var3++) {
            if (handle(((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var3)) > 0L) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean execute(GenericContainerScreen var1) {
      if (var1 != null && Module.client.player != null) {
         String var2 = this.execute(var1.getTitle().getString()).toLowerCase(Locale.ROOT);
         String var3 = this.execute(Module.client.player.getName().getString()).toLowerCase(Locale.ROOT);
         return AhHelper.handle(var1) || var2.contains(var3) || var2.contains("мои товары") || var2.contains("мои предметы") || var2.contains("поиск:");
      } else {
         return false;
      }
   }

   private int prepare(GenericContainerScreen var1) {
      int var2 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getRows();
      int var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size();
      return Math.max(0, Math.min(var2 * 9, var3));
   }

   private void handle(GenericContainerScreen var1, int var2, int var3, SlotActionType var4) {
      Module.client.interactionManager.clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2, var3, var4, Module.client.player);
   }

   private boolean handle(ScreenHandler var1, int var2, int var3) {
      if (Module.client.interactionManager != null && Module.client.player != null) {
         Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
         if (var1.getCursorStack().isEmpty()) {
            return false;
         }

         Module.client.interactionManager.clickSlot(var1.syncId, var3, 1, SlotActionType.PICKUP, Module.client.player);
         if (!var1.getCursorStack().isEmpty()) {
            Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
         }

         return true;
      } else {
         return false;
      }
   }

   private void handle(String var1, int var2) {
      if (Module.client.player != null && var1 != null && !var1.isBlank()) {
         String var3 = "ah " + var1.trim();
         Module.client.player.networkHandler.sendChatCommand(var3);
      }
   }

   private boolean attachEvent() {
      return System.currentTimeMillis() - this.layerSample >= 5000L;
   }

   private void handle(long var1) {
      if (Module.client.player != null) {
         Module.client.player.networkHandler.sendChatCommand("ah sellgui " + var1);
         this.layerSample = System.currentTimeMillis();
      }
   }

   private void readServer() {
      this.dataValidate = false;
      this.scaleRender = 0;
      this.vectorEncode = 0;
      this.entryAnimate = false;
      this.playerCollect = false;
   }

   private long advancePosition() {
      return this.resolve(this.summary.compute());
   }

   private int prepare(boolean var1) {
      if (var1) {
         return Integer.MAX_VALUE;
      } else {
         int var2 = Math.max(1, this.packetSave - this.requestReceive);
         if (this.windowProcess > 0) {
            return Math.max(1, Math.min(this.windowProcess, var2));
         } else {
            return this.requestReceive > 0 ? var2 : Integer.MAX_VALUE;
         }
      }
   }

   private int process(String var1) {
      if (var1 == null) {
         return 1;
      }

      String var2 = this.execute(var1).toLowerCase(Locale.ROOT);
      int var3 = var2.indexOf(" за ");
      if (var3 > 0) {
         var2 = var2.substring(0, var3);
      }

      int var4 = this.handle(var2, "x");
      if (var4 > 0) {
         return var4;
      }

      var4 = this.handle(var2, "х");
      if (var4 > 0) {
         return var4;
      }

      var4 = this.process(var2, "предмет");
      if (var4 > 0) {
         return var4;
      }

      var4 = this.process(var2, "шт");
      return var4 > 0 ? var4 : 1;
   }

   private int handle(String var1, String var2) {
      int var3 = var1.indexOf(var2);
      if (var3 < 0) {
         return 0;
      }

      String var4 = var1.substring(var3 + var2.length()).replaceFirst("[^0-9]*", "").replaceFirst("[^0-9].*$", "");
      return this.compute(var4);
   }

   private int process(String var1, String var2) {
      int var3 = var1.indexOf(var2);
      if (var3 <= 0) {
         return 0;
      }

      String var4 = var1.substring(0, var3).trim();
      String var5 = var4.replaceFirst("^.*?([0-9]+)\\s*$", "$1");
      return var5.equals(var4) && !var5.matches("[0-9]+") ? 0 : this.compute(var5);
   }

   private int compute(String var1) {
      if (var1 != null && !var1.isBlank()) {
         try {
            return Math.max(0, Integer.parseInt(var1));
         } catch (NumberFormatException var3) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private long resolve(String var1) {
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

   private void checkFrame() {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }
   }

   private boolean collectModule() {
      return AutoBuy.source != null && AutoBuy.source.latest.process("FunTime");
   }

   private boolean closeProvider() {
      return this.fetchProvider()
         ? this.frameCheck.process("HolyWorld")
         : this.frameCheck.process("HolyWorld") || AutoBuy.source != null && AutoBuy.source.latest.process("HolyWorld");
   }

   private boolean savePreset() {
      if (!this.closeProvider()) {
         return true;
      } else if (this.sourceSchedule) {
         return true;
      } else {
         AutoBuy.CacheEntry var1 = AutoBuy.tick();
         if (var1 != null) {
            this.sourceSchedule = true;
            this.timerRender = var1.data == null ? "" : var1.data;
            this.scaleSave = var1.instance == null ? "" : var1.instance;
            this.colorCompute = Items.AIR;
            return true;
         } else if (Module.client.player != null && this.resolve(Module.client.player.getMainHandStack())) {
            this.compute(Module.client.player.getMainHandStack());
            return true;
         } else {
            return false;
         }
      }
   }

   private void compute(ItemStack var1) {
      if (this.resolve(var1)) {
         this.sourceSchedule = true;
         this.timerRender = this.apply(var1);
         this.scaleSave = var1.getName().getString();
         this.colorCompute = var1.getItem();
      }
   }

   private boolean convertWindow() {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.playerScreenHandler != null) {
         ItemStack var1 = Module.client.player.getMainHandStack();
         if (var1.isEmpty()) {
            return false;
         } else if (var1.getCount() == 1) {
            return true;
         } else {
            int var2 = this.writePreset();
            if (var2 == -1) {
               ChatLogger.handle("§3[AutoSell] Нет места для стака");
               return false;
            } else {
               int var3 = Module.client.player.getInventory().getSelectedSlot() + 36;
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var3, 0, SlotActionType.PICKUP, Module.client.player);
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var3, 1, SlotActionType.PICKUP, Module.client.player);
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private int writePreset() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 9; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      for (int var2 = 36; var2 < 45; var2++) {
         if (var2 != Module.client.player.getInventory().getSelectedSlot() + 36 && Module.client.player.getInventory().getStack(var2 - 36).isEmpty()) {
            return var2;
         }
      }

      return -1;
   }

   private boolean measureColor() {
      if (Module.client.player == null) {
         return false;
      }

      if (this.resolve(Module.client.player.getMainHandStack())) {
         return true;
      }

      for (int var1 = 0; var1 < 36; var1++) {
         if (this.resolve(Module.client.player.getInventory().getStack(var1))) {
            return true;
         }
      }

      return false;
   }

   private boolean scheduleAnimation() {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.playerScreenHandler != null) {
         if (this.closeProvider()) {
            if (!this.savePreset()) {
               return false;
            }

            if (this.sourceSchedule) {
               return this.scanRenderer();
            }
         }

         if (this.resolve(Module.client.player.getMainHandStack())) {
            return true;
         }

         for (int var1 = 0; var1 < 9; var1++) {
            if (this.resolve(Module.client.player.getInventory().getStack(var1))) {
               InventorySlotActions.handle(var1);
               this.presetSave.handle();
               return true;
            }
         }

         for (int var2 = 9; var2 < 36; var2++) {
            if (this.resolve(Module.client.player.getInventory().getStack(var2))) {
               Module.client.interactionManager
                  .clickSlot(
                     Module.client.player.playerScreenHandler.syncId,
                     var2,
                     Module.client.player.getInventory().getSelectedSlot(),
                     SlotActionType.SWAP,
                     Module.client.player
                  );
               this.presetSave.handle();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean scanRenderer() {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.player.playerScreenHandler != null) {
         ItemStack var1 = Module.client.player.getMainHandStack();
         if (this.update(var1)) {
            this.compute(var1);
            return true;
         }

         for (int var2 = 0; var2 < 9; var2++) {
            ItemStack var3 = Module.client.player.getInventory().getStack(var2);
            if (this.update(var3)) {
               this.compute(var3);
               InventorySlotActions.handle(var2);
               this.presetSave.handle();
               return true;
            }
         }

         int var5 = Module.client.player.getInventory().getSelectedSlot();

         for (int var6 = 9; var6 < 36; var6++) {
            ItemStack var4 = Module.client.player.getInventory().getStack(var6);
            if (this.update(var4)) {
               this.compute(var4);
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var6, var5, SlotActionType.SWAP, Module.client.player);
               this.presetSave.handle();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean resolve(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && var1.getItem() != Items.AIR;
   }

   private boolean update(ItemStack var1) {
      if (this.resolve(var1) && this.sourceSchedule) {
         if (this.colorCompute != Items.AIR && var1.getItem() != this.colorCompute) {
            return false;
         }

         HolyWorldHelper.SecondaryDataRecord var2 = HolyWorldHelper.compute(this.timerRender);
         if (var2 == null) {
            var2 = HolyWorldHelper.compute(this.scaleSave);
         }

         if (var2 != null && HolyWorldHelper.handle(var2, var1, HolyWorldHelper.update(var1), HolyWorldHelper.apply(var1))) {
            return true;
         }

         String var3 = this.apply(this.apply(var1));
         String var4 = this.apply(this.timerRender);
         String var5 = this.apply(this.scaleSave);
         return var4.isEmpty() && var5.isEmpty()
            ? true
            : !var3.isEmpty()
               && (
                  var3.equals(var4)
                     || var3.equals(var5)
                     || !var4.isEmpty() && (var3.contains(var4) || var4.contains(var3))
                     || !var5.isEmpty() && (var3.contains(var5) || var5.contains(var3))
               );
      } else {
         return false;
      }
   }

   private void check(GenericContainerScreen var1) {
      double var2 = Double.MAX_VALUE;
      boolean var4 = false;
      int var5 = Math.min(45, ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size());

      for (int var6 = 0; var6 < var5; var6++) {
         Slot var7 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var6);
         if (var7.hasStack()) {
            long var8 = handle(var7);
            if (var8 > 0L) {
               int var10 = Math.max(1, var7.getStack().getCount());
               double var11 = (double)var8 / var10;
               if (var11 < var2) {
                  var2 = var11;
                  var4 = true;
               }
            }
         }
      }

      String var19 = Module.client.player != null ? this.apply(Module.client.player.getMainHandStack()) : "";
      int var20 = Module.client.player != null ? Math.max(1, Module.client.player.getMainHandStack().getCount()) : 1;
      int var21 = this.pending.process("Все сразу") ? var20 : 1;
      long var9 = AutoBuy.handle(var19);
      long var22 = var9 > 0L ? Math.max(1L, (long)Math.ceil(var9 * 1.02 * var21)) : 1L;
      if (Module.client.player != null) {
         Module.client.player.closeScreen();
      }

      if (var4) {
         long var13 = this.pending.process("Все сразу") ? (long)(var2 * var20) : (long)var2;
         long var15;
         if (this.refresh()) {
            var15 = var13 - 1L;
            if (var9 > 0L && var15 < var22) {
               var15 = var22;
            }

            if (var15 < 1L) {
               var15 = 1L;
            }
         } else {
            double var17 = 1.0 + this.previous.compute() / 100.0;
            var15 = (long)(var13 * var17);
         }

         this.outputCollapse = var15;
         this.update("Минимальная цена: " + (long)var2 + ", выбрана: " + var15 + ".");
         this.rendererScan = AutoSell.Mode.SELLING;
         this.presetSave.handle();
      } else if (this.refresh() && var9 > 0L) {
         this.outputCollapse = var22;
         this.update("Лоты не найдены, выбрана минимальная прибыль: " + var22 + ".");
         this.rendererScan = AutoSell.Mode.SELLING;
         this.presetSave.handle();
      } else {
         ChatLogger.handle("§3[AutoSell] Конкурентов нет, ставьте вручную");
         this.compute(false);
      }
   }

   private void update(String var1) {
      if (this.moduleCollect.compute()) {
         ChatLogger.handle("§7[AutoSell] §f" + var1);
      }
   }

   private String apply(ItemStack var1) {
      String var2 = var1.getName().getString();
      if (var2.contains("TIER WHITE")) {
         return "вайт";
      }

      if (var2.contains("TIER BLACK")) {
         return "блэк";
      }

      if (var2.contains("Рассадник монстров")) {
         return "Спавнер";
      }

      if (var2.contains("Прогрузчик чанков [1x1]")) {
         return "Прогрузчик чанков";
      }

      if (var2.contains("Яйцо призыва зомби-крестьянина")) {
         return "Яйцо зомби-крестьянина";
      }

      String var3 = var2.replaceAll("(?i)§.", "")
         .replaceAll("(?i)&.", "")
         .replace(' ', ' ')
         .replaceAll("\\[[^\\]]*]", " ")
         .replaceAll("[★✦✧✪✫✬✭✮✯✰❄☃⚒☠❤❣♕♛♜♞♟\ud83c\udf79]", " ")
         .replace("xxx", " ")
         .replaceAll("\\s+", " ")
         .trim();
      if (var3.isEmpty()) {
         var3 = this.execute(var1.getItem().getName().getString());
      }

      return var3;
   }

   private String apply(String var1) {
      return HolyWorldHelper.execute(this.execute(var1)).toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "");
   }

   private String execute(String var1) {
      return var1 == null ? "" : var1.replaceAll("§.", "").replace(' ', ' ').trim();
   }

   private boolean prepare(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.execute(var1).toLowerCase(Locale.ROOT);
      return var2.contains("выстав") && var2.contains("продаж");
   }

   private boolean check(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.execute(var1).toLowerCase(Locale.ROOT);
      return !var2.contains("у вас купили") || !var2.contains("на /ah") && !var2.contains(" за ")
         ? var2.contains("купил у вас") && var2.contains(" за ") && (var2.contains("¤") || var2.contains("$"))
         : true;
   }

   public static long handle(Slot var0) {
      if (!var0.hasStack()) {
         return 0L;
      }

      ItemStack var1 = var0.getStack();
      LoreComponent var2 = (LoreComponent)var1.getComponents().get(DataComponentTypes.LORE);
      if (var2 != null) {
         for (Text var4 : var2.lines()) {
            String var5 = var4.getString();
            if (var5.contains("$") || var5.contains("Цена")) {
               String var6 = var5.replaceAll("[^0-9]", "");
               if (!var6.isEmpty()) {
                  try {
                     return Long.parseLong(var6);
                  } catch (NumberFormatException var8) {
                  }
               }
            }
         }
      }

      return 0L;
   }

   enum Mode {
      IDLE,
      PREPARING,
      SPLITTING,
      SEARCHING,
      SCANNING,
      SELLING,
      FINISHING,
      SELLGUI_SELLING,
      SELLGUI_WAITING_RESULT,
      RESALE_SEARCH_OWN_AH,
      RESALE_WAITING_OWN_AH,
      RESALE_TAKE_ITEM,
      RESALE_SELLING,
      RESALE_WAIT_SELL_RESULT,
      HOLY_SELLING,
      HOLY_OPENING_AUCTION;
   }
}
