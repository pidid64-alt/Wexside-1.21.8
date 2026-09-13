package ru.wild.modules.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.CameraRotationEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.inventory.AuctionLoreParser;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.CombatRaycast;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoFTCraftMembrana", category = ModuleCategory.Misc, description = "Автокрафт Божьей Ауры")
public final class AutoFTCraftMembrana extends Module {
   private static final long target = 550L;
   private static final long pending = 50L;
   private static final long previous = 78L;
   private static final long latest = 1000L;
   private static final int summary = 50;
   private static final int matrixBlend = 48;
   private static final long vectorMatch = 500L;
   private static final long itemProject = 900L;
   public static volatile boolean source;
   private final StringSetting responseCompute = new StringSetting("Макс цена алмазов (стак)", "150000").handle(9);
   private final StringSetting providerFetch = new StringSetting("Макс цена незерита", "500000").handle(9);
   private final StringSetting profileDraw = new StringSetting("Цена продажи", "700000").handle(9);
   private final NumberSetting vectorPerform = new NumberSetting("Разброс цены", 5000.0F, 0.0F, 50000.0F, 100.0F, false);
   private final NumberSetting eventAttach = new NumberSetting("Перевыставить (сек)", 30.0F, 5.0F, 120.0F, 1.0F, false);
   private final NumberSetting serverRead = new NumberSetting("Слоты аукциона", 5.0F, 1.0F, 50.0F, 1.0F, false);
   private final NumberSetting positionAdvance = new NumberSetting("Задержка (мс)", 350.0F, 100.0F, 1500.0F, 50.0F, false);
   private final NumberSetting frameCheck = new NumberSetting("Скорость ротации", 8.0F, 2.0F, 20.0F, 1.0F, false);
   private final BooleanSetting moduleCollect = new BooleanSetting("Отладка", true);
   private final Stopwatch providerClose = new Stopwatch();
   private final Stopwatch presetSave = new Stopwatch();
   private final Stopwatch windowConvert = new Stopwatch();
   private final Stopwatch presetWrite = new Stopwatch();
   private final Stopwatch colorMeasure = new Stopwatch();
   private final Queue<String> animationSchedule = new ConcurrentLinkedQueue<>();
   private final Set<BlockPos> rendererScan = new HashSet<>();
   private AutoFTCraftMembrana.Mode sourceBuild = AutoFTCraftMembrana.Mode.IDLE;
   private BlockPos outputCollapse;
   private int profileInvoke;
   private int sourceSchedule;
   private int timerRender;
   private int scaleSave;
   private int colorCompute;
   private int scaleAdapt;
   private boolean textureRun;
   private boolean indexBind;
   private boolean actionRead;
   private boolean configCollapse;
   private boolean dataValidate;
   private boolean scaleRender;
   private boolean clientRefresh;
   private boolean keyFilter;
   private int requestAdapt = 50;
   private int timerMeasure = -1;
   private int vectorEncode;
   private long requestReceive;
   private long windowProcess;
   private AutoFTCraftMembrana.Mode packetSave;
   private long entryAnimate;
   private long playerCollect;
   private long stateApply;
   private float matrixFilter;
   private float layerSample;
   private float worldSend;
   private float targetWrite;
   private float resultEncode;
   private float messageParse;
   private float providerRead;
   private float matrixBlend2;
   private float scalePerform;

   public AutoFTCraftMembrana() {
      this.handle(
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.eventAttach,
         this.serverRead,
         this.positionAdvance,
         this.frameCheck,
         this.moduleCollect
      );
   }

   @Override
   public void handle() {
      super.handle();
      this.saveScale();
      source = true;
      this.sourceBuild = AutoFTCraftMembrana.Mode.SYNC_AUCTION;
      this.handle("Включен", true);
   }

   @Override
   public void process() {
      source = false;
      this.saveScale();
      super.process();
      this.handle("Выключен", true);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         try {
            if (this.convertWindow()) {
               this.compute((CameraRotationEvent)null);
            }

            this.render();
            if (this.scanRenderer()) {
               this.windowProcess = 0L;
               return;
            }

            if (this.sourceBuild == AutoFTCraftMembrana.Mode.MILK_COW) {
               if (this.presetWrite.handle(50L)) {
                  this.presetWrite.handle();
                  this.drawAnimation();
               }

               return;
            }

            if (this.sourceBuild == AutoFTCraftMembrana.Mode.REMOVE_UNSOLD) {
               this.measure();
               return;
            }

            this.computeColor();
            if (!this.providerClose.handle((long)this.positionAdvance.compute())) {
               return;
            }

            this.providerClose.handle();
            this.refresh();
         } catch (Throwable var3) {
            ChatLogger.handle("§8[§6AutoFTCraftMembrana§8] §cОшибка: " + var3.getMessage());
            this.sourceBuild = AutoFTCraftMembrana.Mode.IDLE;
         }
      }
   }

   @EventHandler
   public void handle(CameraRotationEvent var1) {
      if (Module.client.player == null || Module.client.world == null || this.scanRenderer()) {
         this.windowProcess = 0L;
      } else if (!this.convertWindow() || !this.compute(var1)) {
         if (this.invokeProfile()) {
            this.windowProcess = 0L;
         } else {
            this.process(var1);
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.update() == PacketEvent.Mode.RECEIVE) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var6;
            try {
               var6 = var2.content().getString();
            } catch (Throwable var5) {
               return;
            }

            String var4 = this.resolve(var6).toLowerCase(Locale.ROOT);
            if (!var4.isBlank()) {
               this.animationSchedule.add(var4);
            }
         }
      }
   }

   private void refresh() {
      int var1 = Math.max(0, this.collectModule() - this.advancePosition() - this.checkFrame());
      switch (this.sourceBuild) {
         case IDLE:
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
            break;
         case SYNC_AUCTION:
            this.blendMatrix();
            break;
         case READ_AUCTION:
            this.matchVector();
            break;
         case CHECK:
            this.tick();
            break;
         case MILK_COW:
            this.drawAnimation();
            break;
         case BUY_DIAMOND:
            this.handle(Items.DIAMOND, this.responseCompute, "Алмаз", 64, var1 * 4);
            break;
         case BUY_NETHERITE:
            this.handle(Items.NETHERITE_INGOT, this.providerFetch, "Незеритовый слиток", 0, var1);
            break;
         case FIND_STATION:
            this.encodePoint();
            break;
         case OPEN_STATION:
            this.animate();
            break;
         case CRAFT:
            this.save();
            break;
         case SELL:
            this.submit();
            break;
         case WAIT_SALES:
            this.unload();
            break;
         case OPEN_AUCTION:
            this.fetch();
            break;
         case REMOVE_UNSOLD:
            this.measure();
      }
   }

   private void render() {
      String var1;
      while ((var1 = this.animationSchedule.poll()) != null) {
         this.handle(var1);
      }
   }

   private void handle(String var1) {
      if (var1.contains("не хватает монет")) {
         this.keyFilter = true;
         this.collapseOutput();
         this.sourceBuild = AutoFTCraftMembrana.Mode.WAIT_SALES;
         this.windowConvert.handle();
         this.handle("Недостаточно монет для покупки", true);
      } else if (var1.contains("освободите хранилище") || var1.contains("уберите предметы с продажи")) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.OPEN_AUCTION;
      } else if (!var1.contains("не можете продать воздух") && !var1.contains("не можете продать пустоту")) {
         if ((!var1.contains("выставлен") || !var1.contains("продаж")) && !var1.contains("listed")) {
            if (this.process(var1)) {
               this.keyFilter = false;
               this.sourceSchedule++;
               this.windowConvert.handle();
               if (this.advancePosition() > 0 && this.checkFrame() < this.collectModule()) {
                  this.attachEvent();
               }
            }
         } else {
            this.scaleSave = 0;
         }
      } else {
         this.scaleSave++;
         if (this.timerRender > 0) {
            this.timerRender--;
         } else {
            this.profileInvoke = Math.max(0, this.profileInvoke - 1);
         }

         if (this.scaleSave >= 3) {
            this.scaleSave = 0;
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         } else {
            this.attachEvent();
         }
      }
   }

   private void tick() {
      int var1 = this.process(Items.BUCKET);
      int var2 = this.process(Items.MILK_BUCKET);
      int var3 = this.process(Items.DIAMOND);
      int var4 = this.process(Items.NETHERITE_INGOT);
      int var5 = this.advancePosition();
      int var6 = this.collectModule();
      int var7 = this.checkFrame();
      int var8 = var6;
      int var9 = var5 + var7;
      int var10 = this.handle(var2, var3, var4);
      int var11 = Math.max(0, var8 - var9);
      int var12 = var11 * 4;
      int var13 = var11;
      if (var7 >= var6) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.WAIT_SALES;
         this.windowConvert.handle();
      } else if (var5 > 0 && var9 >= var6) {
         this.computeResponse();
      } else if (var2 < 4 && var1 > 0) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.MILK_COW;
      } else if (var10 > 0 && var9 < var8) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.FIND_STATION;
      } else if (this.keyFilter && var11 > 0) {
         if (var5 > 0) {
            this.computeResponse();
         } else if (var7 > 0) {
            this.sourceBuild = AutoFTCraftMembrana.Mode.WAIT_SALES;
         } else {
            if (var10 <= 0) {
               this.update("Не хватает монет для закупки.");
            }
         }
      } else if (var3 < var12) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.BUY_DIAMOND;
         this.vectorEncode = var3;
         this.textureRun = false;
         this.clientRefresh = false;
         this.presetSave.handle();
      } else if (var4 < var13) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.BUY_NETHERITE;
         this.vectorEncode = var4;
         this.textureRun = false;
         this.clientRefresh = false;
         this.presetSave.handle();
      } else if (var2 >= 4 && var3 >= 4 && var4 >= 1 && var9 < var8) {
         this.keyFilter = false;
         this.sourceBuild = AutoFTCraftMembrana.Mode.FIND_STATION;
      } else {
         if (var2 < 4 && var1 == 0) {
            if (var5 > 0) {
               this.computeResponse();
               return;
            }

            this.update("Нет пустых ведер для молока, выключаюсь.");
         }
      }
   }

   private void drawAnimation() {
      if (this.invokeProfile()) {
         this.collapseOutput();
         this.presetSave.handle();
      } else {
         CowEntity var1 = this.projectItem();
         if (var1 == null) {
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         } else if (Module.client.player.squaredDistanceTo(var1) > 20.25) {
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         } else if (this.handle(Items.BUCKET)) {
            if (this.handle(var1, 4.5)) {
               Module.client.interactionManager.interactEntity(Module.client.player, var1, Hand.MAIN_HAND);
               Module.client.player.swingHand(Hand.MAIN_HAND);
               this.presetSave.handle();
               this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
            }
         }
      }
   }

   private void handle(Item var1, StringSetting var2, String var3, int var4, int var5) {
      ScreenHandler var6 = Module.client.player.currentScreenHandler;
      int var7 = this.handle(var2);
      if (!this.invokeProfile()) {
         if (!this.textureRun) {
            this.collapseOutput();
            this.compute("ah search " + var3);
            this.requestAdapt = 50;
            this.timerMeasure = -1;
            this.textureRun = true;
            this.clientRefresh = false;
            this.presetSave.handle();
         } else {
            if (this.presetSave.handle(3500L)) {
               this.textureRun = false;
            }
         }
      } else {
         int var8 = this.process(var1);
         if (var8 <= this.vectorEncode && var8 < var5) {
            if (this.timerMeasure != var6.syncId) {
               this.timerMeasure = var6.syncId;
               this.presetSave.handle();
            } else if (this.presetSave.handle(550L)) {
               String var9 = this.buildSource();
               boolean var10 = var9.contains("подтвержд") || var9.contains("покупк") || var9.contains("confirm") || var9.contains("подозрительн");
               if (this.clientRefresh && !var10 && var6.slots.size() > 36) {
                  boolean var11 = false;
                  boolean var12 = false;
                  int var13 = var6.slots.size() - 36;

                  for (int var14 = 0; var14 < var13; var14++) {
                     ItemStack var15 = ((Slot)var6.slots.get(var14)).getStack();
                     if (var15.isOf(Items.LIME_STAINED_GLASS_PANE)) {
                        var11 = true;
                     }

                     if (var15.isOf(Items.RED_STAINED_GLASS_PANE)) {
                        var12 = true;
                     }
                  }

                  if (var11 && var12) {
                     var10 = true;
                  }
               }

               if (var10) {
                  int var19 = this.resolve(var6);
                  if (var19 >= 0) {
                     this.handle(var6, var19, 0, SlotActionType.PICKUP);
                     this.collapseOutput();
                     this.presetSave.handle();
                     this.clientRefresh = false;
                     this.textureRun = false;
                     this.keyFilter = false;
                     this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
                  } else if (this.presetSave.handle(3000L)) {
                     this.collapseOutput();
                     this.textureRun = false;
                     this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
                  }
               } else if (this.clientRefresh) {
                  if (this.presetSave.handle(2000L)) {
                     this.clientRefresh = false;
                  }
               } else {
                  int var18 = Math.max(0, var6.slots.size() - 36);
                  int var20 = -1;
                  int var21 = Integer.MAX_VALUE;

                  for (int var22 = 0; var22 < var18; var22++) {
                     Slot var23 = (Slot)var6.slots.get(var22);
                     if (var23.hasStack()) {
                        ItemStack var16 = var23.getStack();
                        if (var16.isOf(var1) && (var4 <= 0 || var16.getCount() == var4)) {
                           int var17 = AuctionLoreParser.process(var23);
                           if (var17 > 0 && var17 <= var7 && var17 < var21) {
                              var21 = var17;
                              var20 = var22;
                           }
                        }
                     }
                  }

                  if (var20 < 0) {
                     if (this.presetSave.handle(1000L)) {
                        this.handle(var6);
                     }
                  } else {
                     this.handle(var6, var20, 0, SlotActionType.PICKUP);
                     this.savePreset();
                     this.clientRefresh = true;
                     this.presetSave.handle();
                  }
               }
            }
         } else {
            this.keyFilter = false;
            this.collapseOutput();
            this.textureRun = false;
            this.clientRefresh = false;
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         }
      }
   }

   private boolean handle(ScreenHandler var1) {
      if (this.requestAdapt >= 0 && this.requestAdapt < var1.slots.size()) {
         this.handle(var1, this.requestAdapt, 0, SlotActionType.PICKUP);
         this.requestAdapt = this.requestAdapt == 50 ? 48 : 50;
         this.presetSave.handle();
         return true;
      } else {
         return false;
      }
   }

   private void encodePoint() {
      this.outputCollapse = this.readServer();
      if (this.outputCollapse == null) {
         this.update("Верстак рядом не найден, выключаюсь.");
      } else {
         this.colorCompute = 0;
         this.sourceBuild = AutoFTCraftMembrana.Mode.OPEN_STATION;
         this.presetSave.handle();
      }
   }

   private void animate() {
      if (this.outputCollapse == null) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.FIND_STATION;
      } else if (this.invokeProfile()) {
         this.collapseOutput();
      } else if (!this.handle(this.outputCollapse)) {
         this.outputCollapse = null;
         this.sourceBuild = AutoFTCraftMembrana.Mode.FIND_STATION;
      } else if (this.colorCompute >= 5) {
         this.load();
      } else {
         BlockHitResult var1 = this.handle(this.outputCollapse, 4.5);
         if (var1 == null) {
            if (this.presetSave.handle(1500L)) {
               this.load();
            }
         } else if (this.presetSave.handle(220L)) {
            Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.colorCompute++;
            this.sourceBuild = AutoFTCraftMembrana.Mode.CRAFT;
            this.presetSave.handle();
         }
      }
   }

   private void load() {
      if (this.outputCollapse != null) {
         this.rendererScan.add(this.outputCollapse);
      }

      this.outputCollapse = null;
      this.colorCompute = 0;
      this.sourceBuild = AutoFTCraftMembrana.Mode.FIND_STATION;
      this.presetSave.handle();
   }

   private void save() {
      ScreenHandler var1 = Module.client.player.currentScreenHandler;
      boolean var2 = var1 != null && Module.client.currentScreen != null && var1 != Module.client.player.playerScreenHandler && var1.slots.size() == 46;
      if (!var2) {
         if (this.presetSave.handle(1500L)) {
            this.sourceBuild = AutoFTCraftMembrana.Mode.OPEN_STATION;
         }
      } else {
         this.rendererScan.clear();
         if (!var1.getCursorStack().isEmpty()) {
            int var4 = this.handle(var1, var1.getCursorStack());
            if (var4 >= 0) {
               this.handle(var1, var4, 0, SlotActionType.PICKUP);
            }
         } else {
            if (var1.getSlot(0).hasStack()) {
               ItemStack var3 = var1.getSlot(0).getStack();
               if (this.handle(var3)) {
                  this.handle(var1, 0, 0, SlotActionType.QUICK_MOVE);
                  return;
               }
            }

            if (this.process(Items.MILK_BUCKET) >= 4 && this.process(Items.DIAMOND) >= 4 && this.process(Items.NETHERITE_INGOT) >= 1) {
               this.process(var1);
            } else {
               this.collapseOutput();
               this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
            }
         }
      }
   }

   private void submit() {
      int var1 = this.collectModule();
      ScreenHandler var2 = Module.client.player.currentScreenHandler;
      boolean var3 = var2 != null && Module.client.currentScreen != null && var2 != Module.client.player.playerScreenHandler;
      if (this.actionRead) {
         this.handle(var2, var3);
      } else {
         int var4 = this.advancePosition();
         if (this.checkFrame() >= var1 || var4 <= 0) {
            this.performVector();
         } else if (var3) {
            this.compute(var2);
         } else {
            if (!this.indexBind || this.presetSave.handle(3000L)) {
               this.collapseOutput();
               this.compute("ah sellgui " + this.closeProvider());
               this.indexBind = true;
               this.presetSave.handle();
            }
         }
      }
   }

   private void handle(ScreenHandler var1, boolean var2) {
      if (!var2) {
         this.timerRender = 0;
         this.performVector();
      } else {
         if (!this.configCollapse) {
            int var3 = this.update(var1);
            if (var3 >= 0) {
               this.handle(var1, var3, 0, SlotActionType.PICKUP);
               this.profileInvoke = this.profileInvoke + this.timerRender;
               this.timerRender = 0;
               this.configCollapse = true;
               this.presetSave.handle();
            } else if (this.presetSave.handle(2000L)) {
               this.timerRender = 0;
               this.performVector();
            }
         } else if (this.presetSave.handle(2000L)) {
            this.performVector();
         }
      }
   }

   private void unload() {
      int var1 = this.advancePosition();
      int var2 = this.collectModule();
      long var3 = (long)(this.eventAttach.compute() * 1000.0F);
      if (this.scaleRender) {
         if (this.windowConvert.handle(var3)) {
            this.scaleRender = false;
            this.sourceBuild = AutoFTCraftMembrana.Mode.OPEN_AUCTION;
            this.presetSave.handle();
         }
      } else if (var1 + this.checkFrame() < var2) {
         this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
      } else if (this.windowConvert.handle(var3)) {
         if (this.checkFrame() > 0) {
            this.sourceBuild = AutoFTCraftMembrana.Mode.OPEN_AUCTION;
         } else {
            this.renderTimer();
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         }
      }
   }

   private void fetch() {
      this.collapseOutput();
      this.compute("ah " + this.scheduleSource());
      this.timerMeasure = -1;
      this.sourceBuild = AutoFTCraftMembrana.Mode.REMOVE_UNSOLD;
      this.presetSave.handle();
      this.colorMeasure.handle();
   }

   private void measure() {
      ScreenHandler var1 = Module.client.player.currentScreenHandler;
      if (!this.invokeProfile()) {
         if (this.presetSave.handle(3000L)) {
            this.sourceBuild = AutoFTCraftMembrana.Mode.OPEN_AUCTION;
         }
      } else if (this.timerMeasure != var1.syncId) {
         this.timerMeasure = var1.syncId;
         this.presetSave.handle();
         this.colorMeasure.handle();
      } else if (this.presetSave.handle(550L)) {
         if (this.colorMeasure.handle(78L)) {
            this.colorMeasure.handle();
            int var2 = Math.max(0, var1.slots.size() - 36);

            for (int var3 = 0; var3 < var2; var3++) {
               Slot var4 = (Slot)var1.slots.get(var3);
               if (var4.hasStack() && this.handle(var4.getStack())) {
                  this.handle(var1, var3, 0, SlotActionType.PICKUP);
                  return;
               }
            }

            this.collapseOutput();
            this.renderTimer();
            this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         }
      }
   }

   private void blendMatrix() {
      this.collapseOutput();
      this.compute("ah " + this.scheduleSource());
      this.scaleAdapt++;
      this.timerMeasure = -1;
      this.sourceBuild = AutoFTCraftMembrana.Mode.READ_AUCTION;
      this.presetSave.handle();
   }

   private void matchVector() {
      ScreenHandler var1 = Module.client.player.currentScreenHandler;
      if (!this.invokeProfile()) {
         if (this.presetSave.handle(3000L)) {
            if (this.scaleAdapt < 3) {
               this.sourceBuild = AutoFTCraftMembrana.Mode.SYNC_AUCTION;
            } else {
               this.renderTimer();
               this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
            }

            this.presetSave.handle();
         }
      } else if (this.timerMeasure != var1.syncId) {
         this.timerMeasure = var1.syncId;
         this.presetSave.handle();
      } else if (this.presetSave.handle(550L)) {
         int var2 = 0;
         int var3 = Math.max(0, var1.slots.size() - 36);

         for (int var4 = 0; var4 < var3; var4++) {
            if (this.handle(var1.getSlot(var4).getStack())) {
               var2++;
            }
         }

         this.profileInvoke = var2;
         this.sourceSchedule = 0;
         this.timerRender = 0;
         this.collapseOutput();
         this.sourceBuild = AutoFTCraftMembrana.Mode.CHECK;
         this.presetSave.handle();
      }
   }

   private CowEntity projectItem() {
      CowEntity var1 = null;
      double var2 = Double.MAX_VALUE;
      Box var4 = new Box(Module.client.player.getBlockPos()).expand(4.0);

      for (Entity var6 : Module.client.world.getOtherEntities(Module.client.player, var4)) {
         if (var6 instanceof CowEntity var7) {
            double var8 = Module.client.player.squaredDistanceTo(var7);
            if (var8 < var2) {
               var2 = var8;
               var1 = var7;
            }
         }
      }

      return var1;
   }

   private void process(ScreenHandler var1) {
      int[] var2 = new int[]{1, 3, 7, 9};

      for (int var6 : var2) {
         this.handle(var1, var0 -> var0.isOf(Items.DIAMOND), var6);
      }

      int[] var8 = new int[]{2, 4, 6, 8};

      for (int var7 : var8) {
         this.handle(var1, var0 -> var0.isOf(Items.MILK_BUCKET), var7);
      }

      this.handle(var1, var0 -> var0.isOf(Items.NETHERITE_INGOT), 5);
   }

   private void computeResponse() {
      this.collapseOutput();
      this.scaleSave = 0;
      this.indexBind = false;
      this.actionRead = false;
      this.configCollapse = false;
      this.dataValidate = false;
      this.timerRender = 0;
      this.scaleRender = false;
      this.sourceBuild = AutoFTCraftMembrana.Mode.SELL;
      this.presetSave.handle();
   }

   private void compute(ScreenHandler var1) {
      if (this.checkFrame() + this.timerRender >= this.collectModule()) {
         this.fetchProvider();
      } else {
         int var2 = this.apply(var1);
         if (var2 < 0) {
            this.fetchProvider();
         } else if (this.execute(var1) < 0) {
            if (this.dataValidate) {
               this.fetchProvider();
            } else {
               this.drawProfile();
            }
         } else {
            this.handle(var1, var2, 0, SlotActionType.PICKUP);

            while (this.handle(var1.getCursorStack()) && this.checkFrame() + this.timerRender < this.collectModule()) {
               int var3 = this.execute(var1);
               if (var3 < 0) {
                  break;
               }

               this.handle(var1, var3, 1, SlotActionType.PICKUP);
               this.timerRender++;
               this.dataValidate = true;
            }

            this.prepare(var1);
            boolean var4 = this.checkFrame() + this.timerRender >= this.collectModule() || this.execute(var1) < 0 || this.advancePosition() <= 0;
            if (var4) {
               this.fetchProvider();
            } else {
               this.presetSave.handle();
            }
         }
      }
   }

   private void fetchProvider() {
      if (this.dataValidate) {
         this.actionRead = true;
         this.configCollapse = false;
         this.presetSave.handle();
      } else {
         this.performVector();
      }
   }

   private void drawProfile() {
      this.scaleRender = true;
      this.handle("Нет свободных ячеек аукциона, жду", true);
      this.performVector();
   }

   private void performVector() {
      ScreenHandler var1 = Module.client.player.currentScreenHandler;
      if (this.invokeProfile()) {
         this.prepare(var1);
      }

      this.collapseOutput();
      this.indexBind = false;
      this.actionRead = false;
      this.configCollapse = false;
      this.dataValidate = false;
      this.timerRender = 0;
      this.sourceBuild = AutoFTCraftMembrana.Mode.WAIT_SALES;
      this.windowConvert.handle();
   }

   private void attachEvent() {
      this.indexBind = false;
      this.actionRead = false;
      this.configCollapse = false;
      this.dataValidate = false;
      this.timerRender = 0;
      this.scaleRender = false;
      this.sourceBuild = AutoFTCraftMembrana.Mode.SELL;
      this.presetSave.handle();
   }

   private void handle(ScreenHandler var1, Predicate<ItemStack> var2, int var3) {
      Slot var4 = var1.getSlot(var3);
      if (var4.hasStack()) {
         if (!var2.test(var4.getStack())) {
            this.handle(var1, var3, 0, SlotActionType.QUICK_MOVE);
         }
      } else {
         int var5 = this.handle(var1, var2);
         if (var5 >= 0) {
            this.handle(var1, var5, 0, SlotActionType.PICKUP);
            this.handle(var1, var3, 1, SlotActionType.PICKUP);
            if (!var1.getCursorStack().isEmpty()) {
               this.handle(var1, var5, 0, SlotActionType.PICKUP);
            }
         }
      }
   }

   private boolean handle(Item var1) {
      if (this.invokeProfile()) {
         return false;
      }

      int var2 = Module.client.player.getInventory().getSelectedSlot();
      if (Module.client.player.getInventory().getStack(var2).isOf(var1)) {
         return true;
      }

      for (int var3 = 0; var3 <= 8; var3++) {
         if (Module.client.player.getInventory().getStack(var3).isOf(var1)) {
            Module.client.player.getInventory().setSelectedSlot(var3);
            Module.client.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var3));
            return true;
         }
      }

      for (int var4 = 9; var4 < 36; var4++) {
         if (Module.client.player.getInventory().getStack(var4).isOf(var1)) {
            Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var4, var2, SlotActionType.SWAP, Module.client.player);
            return Module.client.player.getInventory().getStack(var2).isOf(var1);
         }
      }

      return false;
   }

   private int resolve(ScreenHandler var1) {
      int var2 = Math.max(0, var1.slots.size() - 36);

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = ((Slot)var1.slots.get(var3)).getStack();
         if (!var4.isEmpty() && (var4.isOf(Items.LIME_STAINED_GLASS_PANE) || this.process(var4).contains("купить") || this.process(var4).contains("confirm"))) {
            return var3;
         }
      }

      return -1;
   }

   private int update(ScreenHandler var1) {
      int var2 = Math.max(0, var1.slots.size() - 36);

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4 = ((Slot)var1.slots.get(var3)).getStack();
         if (!var4.isEmpty()) {
            String var5 = this.process(var4);
            if (var5.contains("подтверд") || var5.contains("confirm")) {
               return var3;
            }
         }
      }

      return -1;
   }

   private int handle(ScreenHandler var1, Predicate<ItemStack> var2) {
      for (int var3 = 10; var3 < var1.slots.size(); var3++) {
         Slot var4 = (Slot)var1.slots.get(var3);
         if (var4.hasStack() && var2.test(var4.getStack())) {
            return var3;
         }
      }

      return -1;
   }

   private int handle(ScreenHandler var1, ItemStack var2) {
      for (int var3 = 10; var3 < var1.slots.size(); var3++) {
         Slot var4 = (Slot)var1.slots.get(var3);
         if (var4.hasStack() && var4.getStack().isOf(var2.getItem()) && var4.getStack().getCount() < var4.getStack().getMaxCount()) {
            return var3;
         }
      }

      for (int var5 = 10; var5 < var1.slots.size(); var5++) {
         if (!((Slot)var1.slots.get(var5)).hasStack()) {
            return var5;
         }
      }

      return -1;
   }

   private int apply(ScreenHandler var1) {
      int var2 = Math.max(0, var1.slots.size() - 36);

      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         Slot var4 = (Slot)var1.slots.get(var3);
         if (var4.hasStack() && this.handle(var4.getStack())) {
            return var3;
         }
      }

      return -1;
   }

   private int execute(ScreenHandler var1) {
      int var2 = Math.max(0, var1.slots.size() - 36);

      for (int var3 = 0; var3 < var2; var3++) {
         if (!((Slot)var1.slots.get(var3)).hasStack()) {
            return var3;
         }
      }

      return -1;
   }

   private void prepare(ScreenHandler var1) {
      ItemStack var2 = var1.getCursorStack();
      if (!var2.isEmpty()) {
         int var3 = Math.max(0, var1.slots.size() - 36);

         for (int var4 = var3; var4 < var1.slots.size(); var4++) {
            ItemStack var5 = ((Slot)var1.slots.get(var4)).getStack();
            if (var5.isOf(var2.getItem()) && var5.getCount() < var5.getMaxCount()) {
               this.handle(var1, var4, 0, SlotActionType.PICKUP);
               return;
            }
         }

         for (int var6 = var3; var6 < var1.slots.size(); var6++) {
            if (!((Slot)var1.slots.get(var6)).hasStack()) {
               this.handle(var1, var6, 0, SlotActionType.PICKUP);
               return;
            }
         }
      }
   }

   private BlockPos readServer() {
      BlockPos var1 = Module.client.player.getBlockPos();
      Mutable var2 = new Mutable();
      ArrayList var3 = new ArrayList();

      for (int var4 = -4; var4 <= 4; var4++) {
         for (int var5 = -2; var5 <= 2; var5++) {
            for (int var6 = -4; var6 <= 4; var6++) {
               var2.set(var1.getX() + var4, var1.getY() + var5, var1.getZ() + var6);
               if (this.handle(var2) && !this.rendererScan.contains(var2) && !(Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var2)) > 25.0)) {
                  var3.add(var2.toImmutable());
               }
            }
         }
      }

      return var3.isEmpty() ? null : (BlockPos)var3.get(ThreadLocalRandom.current().nextInt(var3.size()));
   }

   private boolean handle(BlockPos var1) {
      return Module.client.world.getBlockState(var1).isOf(Blocks.CRAFTING_TABLE);
   }

   private boolean handle(ItemStack var1) {
      return var1 != null && !var1.isEmpty()
         ? var1.isOf(Items.PHANTOM_MEMBRANE) && (SpecialItemCatalog.saveScale(var1) || this.process(var1).contains("божья аура"))
         : false;
   }

   private int process(Item var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && var4.isOf(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private int advancePosition() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (this.handle(var3)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private int checkFrame() {
      return Math.max(0, this.profileInvoke - this.sourceSchedule);
   }

   private int collectModule() {
      return Math.max(1, (int)this.serverRead.compute());
   }

   private int handle(int var1, int var2, int var3) {
      return var1 < 4 ? 0 : Math.max(0, Math.min(var2 / 4, var3));
   }

   private int handle(StringSetting var1) {
      String var2 = var1.compute();
      if (var2 == null) {
         return 0;
      }

      String var3 = var2.replaceAll("[^0-9]", "");
      if (var3.isBlank()) {
         return 0;
      }

      try {
         long var4 = Long.parseLong(var3);
         return var4 > 2147483647L ? Integer.MAX_VALUE : (int)var4;
      } catch (NumberFormatException var6) {
         return 0;
      }
   }

   private int closeProvider() {
      int var1 = this.handle(this.profileDraw);
      int var2 = (int)this.vectorPerform.compute();
      if (var2 <= 0) {
         return var1;
      }

      int var3 = Math.max(1, var1 - var2);
      int var4 = var1 + var2;
      return var3 + (int)(Math.random() * (var4 - var3 + 1));
   }

   private AutoFTCraftMembrana.DataRecord handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      float var9 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var10 = (float)(-Math.toDegrees(Math.atan2(var5, Math.sqrt(var3 * var3 + var7 * var7))));
      return new AutoFTCraftMembrana.DataRecord(var9, MathHelper.clamp(var10, -90.0F, 90.0F));
   }

   private void process(CameraRotationEvent var1) {
      if (Module.client.player != null) {
         Vec3d var2 = null;
         if (this.sourceBuild == AutoFTCraftMembrana.Mode.MILK_COW) {
            CowEntity var3 = this.projectItem();
            if (var3 != null) {
               var2 = var3.getPos().add(0.0, Math.min(1.15, var3.getHeight() * 0.72), 0.0);
            }
         } else if (this.sourceBuild == AutoFTCraftMembrana.Mode.OPEN_STATION && this.outputCollapse != null) {
            var2 = Vec3d.ofCenter(this.outputCollapse);
         }

         if (var2 == null) {
            this.windowProcess = 0L;
         } else {
            this.handle(var2, var1);
         }
      }
   }

   private void savePreset() {
      if (Module.client.player != null) {
         long var1 = System.currentTimeMillis();
         long var3 = ThreadLocalRandom.current().nextLong(500L, 901L);
         this.entryAnimate = var1;
         this.playerCollect = var1 + var3;
         this.stateApply = 0L;
         this.matrixFilter = Module.client.player.getYaw();
         this.layerSample = Module.client.player.getPitch();
         this.worldSend = (float)ThreadLocalRandom.current().nextDouble(0.0, Math.PI * 2);
         this.targetWrite = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
         this.resultEncode = (float)ThreadLocalRandom.current().nextDouble(8.0, 28.0);
         this.messageParse = (float)ThreadLocalRandom.current().nextDouble(3.0, 12.0);
         this.providerRead = this.targetWrite * (float)ThreadLocalRandom.current().nextDouble(4.0, 16.0);
         this.matrixBlend2 = (float)ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
         this.scalePerform = (float)ThreadLocalRandom.current().nextDouble(0.1, 0.3);
      }
   }

   private boolean convertWindow() {
      return this.playerCollect > 0L && System.currentTimeMillis() < this.playerCollect;
   }

   private boolean compute(CameraRotationEvent var1) {
      if (Module.client.player == null) {
         return false;
      }

      long var2 = System.currentTimeMillis();
      if (var2 < this.playerCollect && this.entryAnimate > 0L) {
         float var4 = Math.max(1.0F, (float)(this.playerCollect - this.entryAnimate));
         float var5 = MathHelper.clamp((float)(var2 - this.entryAnimate) / var4, 0.0F, 1.0F);
         float var6 = var5 * var5 * (3.0F - 2.0F * var5);
         float var7 = this.worldSend + var6 * 5.3407073F;
         float var8 = this.matrixFilter + (float)Math.sin(var7) * this.resultEncode + this.providerRead * var6;
         float var9 = this.layerSample + (float)Math.sin(var7 * 0.55F) * this.messageParse + this.matrixBlend2 * var6;
         float var10 = Module.client.player.getYaw();
         float var11 = Module.client.player.getPitch();
         float var12 = this.writePreset();
         float var13 = 1.0F - (float)Math.pow(1.0F - this.scalePerform, var12);
         float var14 = var10 + MathHelper.wrapDegrees(var8 - var10) * var13;
         float var15 = var11 + (MathHelper.clamp(var9, -89.0F, 89.0F) - var11) * var13;
         Module.client.player.setYaw(var14);
         Module.client.player.setPitch(var15);
         Module.client.player.headYaw = var14;
         if (var1 != null) {
            var1.handle(var14);
            var1.process(var15);
         }

         return true;
      } else {
         this.measureColor();
         return false;
      }
   }

   private float writePreset() {
      long var1 = System.nanoTime();
      if (this.stateApply == 0L) {
         this.stateApply = var1;
         return 1.0F;
      } else {
         float var3 = (float)(var1 - this.stateApply) / 1.6666667E7F;
         this.stateApply = var1;
         return MathHelper.clamp(var3, 0.25F, 4.0F);
      }
   }

   private void measureColor() {
      this.entryAnimate = 0L;
      this.playerCollect = 0L;
      this.stateApply = 0L;
   }

   private void handle(Vec3d var1, CameraRotationEvent var2) {
      AutoFTCraftMembrana.DataRecord var3 = this.handle(var1);
      float var4 = this.scheduleAnimation();
      float var5 = MathHelper.clamp(this.frameCheck.compute() / 100.0F, 0.02F, 0.2F);
      float var6 = 1.0F - (float)Math.pow(1.0F - var5, var4);
      float var7 = Module.client.player.getYaw();
      float var8 = Module.client.player.getPitch();
      float var9 = MathHelper.wrapDegrees(var3.yaw - var7);
      float var10 = var3.pitch - var8;
      float var11 = var7 + var9 * var6;
      float var12 = MathHelper.clamp(var8 + var10 * var6, -90.0F, 90.0F);
      Module.client.player.setYaw(var11);
      Module.client.player.setPitch(var12);
      Module.client.player.headYaw = var11;
      Module.client.player.bodyYaw = var11;
      var2.handle(var11);
      var2.process(var12);
   }

   private float scheduleAnimation() {
      long var1 = System.nanoTime();
      if (this.windowProcess == 0L) {
         this.windowProcess = var1;
         return 1.0F;
      } else {
         float var3 = (float)(var1 - this.windowProcess) / 1.6666667E7F;
         this.windowProcess = var1;
         return MathHelper.clamp(var3, 0.25F, 4.0F);
      }
   }

   private boolean handle(CowEntity var1, double var2) {
      EntityHitResult var4 = CombatRaycast.process(Module.client.player.getYaw(), Module.client.player.getPitch(), var2, var1, false);
      return var4 != null && var4.getEntity() == var1;
   }

   private BlockHitResult handle(BlockPos var1, double var2) {
      Vec3d var4 = Module.client.player.getEyePos();
      Vec3d var5 = Module.client.player.getRotationVec(1.0F);
      Vec3d var6 = var4.add(var5.multiply(var2));
      if (Module.client.world.raycast(new RaycastContext(var4, var6, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player)) instanceof BlockHitResult var8) {
         return var8.getBlockPos().equals(var1) ? var8 : null;
      } else {
         return null;
      }
   }

   private boolean scanRenderer() {
      return PlayerHelper.refresh() || PlayerHelper.moduleCollect || PlayerHelper.providerClose || Module.client.player.isUsingItem();
   }

   private String process(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            return this.resolve(var1.getName().getString()).toLowerCase(Locale.ROOT);
         } catch (Throwable var3) {
            return "";
         }
      } else {
         return "";
      }
   }

   private String buildSource() {
      if (Module.client.currentScreen == null) {
         return "";
      }

      try {
         Text var1 = Module.client.currentScreen.getTitle();
         return var1 == null ? "" : this.resolve(var1.getString()).toLowerCase(Locale.ROOT);
      } catch (Throwable var2) {
         return "";
      }
   }

   private boolean process(String var1) {
      return (var1.contains("купили") || var1.contains("куплен")) && (var1.contains("аура") || var1.contains("мембран") || var1.contains("membrane"));
   }

   private void handle(ScreenHandler var1, int var2, int var3, SlotActionType var4) {
      Module.client.interactionManager.clickSlot(var1.syncId, var2, var3, var4, Module.client.player);
   }

   private void collapseOutput() {
      if (this.invokeProfile()) {
         Module.client.player.closeHandledScreen();
      }
   }

   private boolean invokeProfile() {
      return Module.client.currentScreen != null
         && Module.client.player.currentScreenHandler != null
         && Module.client.player.currentScreenHandler != Module.client.player.playerScreenHandler;
   }

   private void compute(String var1) {
      Module.client.player.networkHandler.sendChatCommand(var1);
   }

   private String scheduleSource() {
      try {
         return Module.client.player.getName().getString();
      } catch (Throwable var2) {
         return "";
      }
   }

   private String resolve(String var1) {
      if (var1 == null) {
         return "";
      }

      String var2;
      try {
         var2 = Formatting.strip(var1);
      } catch (Throwable var4) {
         var2 = var1;
      }

      return var2 == null ? "" : var2.replace(' ', ' ').trim();
   }

   private void renderTimer() {
      this.profileInvoke = 0;
      this.sourceSchedule = 0;
      this.timerRender = 0;
      this.scaleSave = 0;
   }

   private void saveScale() {
      this.outputCollapse = null;
      this.rendererScan.clear();
      this.colorCompute = 0;
      this.textureRun = false;
      this.indexBind = false;
      this.actionRead = false;
      this.configCollapse = false;
      this.dataValidate = false;
      this.scaleRender = false;
      this.clientRefresh = false;
      this.keyFilter = false;
      this.requestAdapt = 50;
      this.timerMeasure = -1;
      this.scaleAdapt = 0;
      this.vectorEncode = 0;
      this.requestReceive = 0L;
      this.windowProcess = 0L;
      this.packetSave = null;
      this.measureColor();
      this.animationSchedule.clear();
      this.renderTimer();
      this.providerClose.handle();
      this.presetSave.handle();
      this.windowConvert.handle();
      this.presetWrite.handle();
      this.colorMeasure.handle();
      this.sourceBuild = AutoFTCraftMembrana.Mode.IDLE;
   }

   private void handle(String var1, boolean var2) {
      if (this.moduleCollect.compute()) {
         if (!var2) {
            long var3 = System.currentTimeMillis();
            if (this.packetSave == this.sourceBuild && var3 - this.requestReceive < 2500L) {
               return;
            }

            this.packetSave = this.sourceBuild;
            this.requestReceive = var3;
         }

         ChatLogger.handle("§8[§6AutoFTCraftMembrana§8] §7[" + this.sourceBuild + "] §f" + var1);
      }
   }

   private void computeColor() {
      if (this.moduleCollect.compute()) {
         long var1 = System.currentTimeMillis();
         if (var1 - this.requestReceive >= 5000L) {
            if (this.packetSave == this.sourceBuild) {
               ChatLogger.handle(
                  "§8[§6AutoFTCraftMembrana§8] §7["
                     + this.sourceBuild
                     + "] §fMilk="
                     + this.process(Items.MILK_BUCKET)
                     + " Dia="
                     + this.process(Items.DIAMOND)
                     + " Neth="
                     + this.process(Items.NETHERITE_INGOT)
                     + " Crafted="
                     + this.advancePosition()
                     + " AH="
                     + this.checkFrame()
                     + "/"
                     + this.collectModule()
               );
               this.requestReceive = var1;
            }
         }
      }
   }

   private void update(String var1) {
      ChatLogger.handle("§8[§6AutoFTCraftMembrana§8] §c" + var1);
      if (this.enabled) {
         this.toggle();
      }
   }

   record DataRecord(float yaw, float pitch) {
   }

   enum Mode {
      IDLE,
      SYNC_AUCTION,
      READ_AUCTION,
      CHECK,
      MILK_COW,
      BUY_DIAMOND,
      BUY_NETHERITE,
      FIND_STATION,
      OPEN_STATION,
      CRAFT,
      SELL,
      WAIT_SALES,
      OPEN_AUCTION,
      REMOVE_UNSOLD;
   }
}
