package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoWood", category = ModuleCategory.Misc, description = "Автоматически выращивает и добывает дерево")
public class AutoWood extends Module {
   public ModeSetting source = new ModeSetting("Что добывать", "Тёмный дуб", "Тропик дерево", "Тёмный дуб", "Еловое дерево");
   public ModeSetting target = new ModeSetting("Что делать с деревом", "Ничего", "Ничего", "Продавать на ауке", "Складывать в сундук");
   public NumberSetting pending = new NumberSetting("Порог дерева", 64.0F, 64.0F, 640.0F, 64.0F, false).handle(() -> this.target.process("Ничего"));
   public BooleanSetting previous = new BooleanSetting("Чинить топор", false);
   public NumberSetting latest = new NumberSetting("Порог для починки топора", 300.0F, 100.0F, 2031.0F, 100.0F, false).handle(() -> !this.previous.compute());
   public BooleanSetting summary = new BooleanSetting("Пополнять муку из сундука", true);
   public NumberSetting matrixBlend = new NumberSetting("Радиус поиска сундуков", 12.0F, 4.0F, 40.0F, 1.0F, false);
   private static final double vectorMatch = 4.5;
   private static final int itemProject = 6;
   private static final int responseCompute = 64;
   private static final int providerFetch = 128;
   private AutoWood.Mode profileDraw = AutoWood.Mode.SETUP;
   private final List<List<BlockPos>> vectorPerform = new ArrayList<>();
   private boolean eventAttach = false;
   private BlockPos serverRead = null;
   private int positionAdvance = 0;
   private int frameCheck = -1;
   private BlockPos moduleCollect = null;
   private int providerClose = 0;
   private AutoWood.FallbackMode presetSave = AutoWood.FallbackMode.NONE;
   private int windowConvert = 0;
   private boolean presetWrite = false;
   private boolean colorMeasure = false;
   private IBaritone animationSchedule;
   private AutoWood.PrimaryMode rendererScan = AutoWood.PrimaryMode.NONE;
   private AutoWood.SecondaryMode sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
   private BlockPos outputCollapse = null;
   private boolean profileInvoke = false;
   private boolean sourceSchedule = false;
   private int timerRender = 0;
   private boolean scaleSave = false;
   private int colorCompute = -1;
   private int scaleAdapt = -1;
   private float textureRun = 0.0F;
   private final Stopwatch indexBind = new Stopwatch();
   private final Stopwatch actionRead = new Stopwatch();
   private final Stopwatch configCollapse = new Stopwatch();
   private final Stopwatch dataValidate = new Stopwatch();
   private final Stopwatch scaleRender = new Stopwatch();
   private final Stopwatch clientRefresh = new Stopwatch();
   private final Stopwatch keyFilter = new Stopwatch();
   private final Stopwatch requestAdapt = new Stopwatch();
   private final Stopwatch timerMeasure = new Stopwatch();
   private final Stopwatch vectorEncode = new Stopwatch();
   private final Queue<Runnable> requestReceive = new ArrayDeque<>();

   public AutoWood() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend);
   }

   @Override
   public void handle() {
      super.handle();
      this.animationSchedule = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.profileDraw = AutoWood.Mode.SETUP;
      this.vectorPerform.clear();
      this.eventAttach = false;
      this.serverRead = null;
      this.positionAdvance = 0;
      this.frameCheck = -1;
      this.moduleCollect = null;
      this.presetSave = AutoWood.FallbackMode.NONE;
      this.windowConvert = 0;
      this.presetWrite = false;
      this.colorMeasure = false;
      this.collapseOutput();
      this.providerClose = 0;
   }

   @Override
   public void process() {
      if (Module.client.player != null && this.scaleSave) {
         try {
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, 45, this.colorCompute, SlotActionType.SWAP, Module.client.player);
            if (this.colorCompute >= 0) {
               Module.client.player.getInventory().setSelectedSlot(this.colorCompute);
            }

            Module.client.player.setPitch(this.textureRun);
         } catch (Exception var2) {
         }
      }

      this.scaleSave = false;
      if (this.animationSchedule != null) {
         this.animationSchedule.getPathingBehavior().cancelEverything();
      }

      this.collapseOutput();
      this.presetSave = AutoWood.FallbackMode.NONE;
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (!PlayerHelper.refresh()) {
            if (this.rendererScan != AutoWood.PrimaryMode.NONE && Module.client.currentScreen instanceof GenericContainerScreen var2) {
               if (this.rendererScan == AutoWood.PrimaryMode.DEPOSIT) {
                  this.process(var2);
               } else {
                  this.handle(var2);
               }
            } else if (this.rendererScan != AutoWood.PrimaryMode.NONE) {
               this.performVector();
            } else if (this.presetSave != AutoWood.FallbackMode.NONE) {
               this.measure();
            } else if (Module.client.currentScreen == null) {
               if (!this.eventAttach) {
                  this.animate();
               } else if (this.previous.compute() && this.invokeProfile() != -1) {
                  this.rendererScan = AutoWood.PrimaryMode.REPAIR;
                  this.drawProfile();
               } else if (this.unload()) {
                  this.presetSave = AutoWood.FallbackMode.EQUIP;
                  this.windowConvert = 0;
               } else if (this.fetch()) {
                  this.rendererScan = AutoWood.PrimaryMode.DEPOSIT;
                  this.drawProfile();
               } else {
                  this.providerClose++;
                  if (this.profileDraw == AutoWood.Mode.WAIT_FELL || this.providerClose >= 2) {
                     switch (this.profileDraw) {
                        case FARM:
                           this.load();
                           break;
                        case WAIT_FELL:
                           this.submit();
                           break;
                        default:
                           this.profileDraw = AutoWood.Mode.FARM;
                     }
                  }
               }
            }
         }
      }
   }

   private Item refresh() {
      return switch (this.source.compute()) {
         case "Тропик дерево" -> Items.JUNGLE_SAPLING;
         case "Еловое дерево" -> Items.SPRUCE_SAPLING;
         default -> Items.DARK_OAK_SAPLING;
      };
   }

   private Block render() {
      return switch (this.source.compute()) {
         case "Тропик дерево" -> Blocks.JUNGLE_SAPLING;
         case "Еловое дерево" -> Blocks.SPRUCE_SAPLING;
         default -> Blocks.DARK_OAK_SAPLING;
      };
   }

   private Block tick() {
      return switch (this.source.compute()) {
         case "Тропик дерево" -> Blocks.JUNGLE_LOG;
         case "Еловое дерево" -> Blocks.SPRUCE_LOG;
         default -> Blocks.DARK_OAK_LOG;
      };
   }

   private Block drawAnimation() {
      return switch (this.source.compute()) {
         case "Тропик дерево" -> Blocks.JUNGLE_LEAVES;
         case "Еловое дерево" -> Blocks.SPRUCE_LEAVES;
         default -> Blocks.DARK_OAK_LEAVES;
      };
   }

   private Item encodePoint() {
      return switch (this.source.compute()) {
         case "Тропик дерево" -> Items.JUNGLE_LOG;
         case "Еловое дерево" -> Items.SPRUCE_LOG;
         default -> Items.DARK_OAK_LOG;
      };
   }

   private void animate() {
      this.vectorPerform.clear();
      ArrayList var1 = new ArrayList();
      this.handle(this.render(), var1, false);
      this.handle(this.tick(), var1, true);
      if (this.vectorPerform.isEmpty()) {
         ChatLogger.handle("§c[AutoWood] §fПоставьте саженцы квадратами 2×2 рядом с собой и включите модуль");
         this.toggle();
      } else {
         this.serverRead = Module.client.player.getBlockPos();
         this.eventAttach = true;
         this.profileDraw = AutoWood.Mode.FARM;
         this.positionAdvance = 0;
         this.providerClose = 0;
         ChatLogger.handle("§a[AutoWood] §fНайдено площадок 2×2: " + this.vectorPerform.size());
      }
   }

   private void handle(Block var1, List<BlockPos> var2, boolean var3) {
      BlockPos var4 = Module.client.player.getBlockPos();

      for (BlockPos var6 : BlockPos.iterate(var4.add(-6, -3, -6), var4.add(6, 3, 6))) {
         if (this.handle(var6, var1)) {
            BlockPos var7 = var6.toImmutable();
            if (!var3 || Module.client.world.getBlockState(var7.down()).getBlock() != var1) {
               List<BlockPos> var8 = List.of(var7, var7.east(), var7.south(), var7.east().south());
               boolean var9 = false;

               for (BlockPos var11 : var8) {
                  if (var2.contains(var11)) {
                     var9 = true;
                     break;
                  }
               }

               if (!var9) {
                  boolean var13 = true;

                  for (BlockPos var12 : var8) {
                     if (!this.execute(var12)) {
                        var13 = false;
                        break;
                     }
                  }

                  if (var13) {
                     this.vectorPerform.add(new ArrayList<>(var8));
                     var2.addAll(var8);
                  }
               }
            }
         }
      }
   }

   private boolean handle(BlockPos var1, Block var2) {
      return Module.client.world.getBlockState(var1).getBlock() == var2
         && Module.client.world.getBlockState(var1.east()).getBlock() == var2
         && Module.client.world.getBlockState(var1.south()).getBlock() == var2
         && Module.client.world.getBlockState(var1.east().south()).getBlock() == var2;
   }

   private void load() {
      boolean var1 = false;

      for (int var2 = 0; var2 < this.vectorPerform.size(); var2++) {
         if (this.handle(var2)) {
            var1 = true;
            if (this.process(var2)) {
               return;
            }
         }
      }

      if (!var1) {
         for (List<BlockPos> var3 : this.vectorPerform) {
            for (BlockPos var5 : var3) {
               BlockState var6 = Module.client.world.getBlockState(var5);
               if (var6.getBlock() != this.render()) {
                  this.handle(var5, var6);
                  return;
               }
            }
         }

         this.moduleCollect = null;
         this.save();
      }
   }

   private boolean handle(int var1) {
      for (BlockPos var3 : this.vectorPerform.get(var1)) {
         if (Module.client.world.getBlockState(var3).getBlock() == this.tick()) {
            return true;
         }
      }

      return false;
   }

   private void handle(BlockPos var1, BlockState var2) {
      if (var2.getBlock() == this.drawAnimation()) {
         this.handle(var1);
      } else {
         this.moduleCollect = null;
         if (!var2.isReplaceable()) {
            if (this.vectorEncode.update(15000L)) {
               ChatLogger.handle("§e[AutoWood] §fМесто посадки занято посторонним блоком, жду освобождения");
               this.vectorEncode.handle();
            }
         } else {
            int var3 = this.process(this.refresh());
            if (var3 == -1) {
               var3 = this.compute(this.refresh());
            }

            if (var3 == -1) {
               if (this.vectorEncode.update(15000L)) {
                  ChatLogger.handle("§e[AutoWood] §fНет саженцев в инвентаре, жду дроп с листвы");
                  this.vectorEncode.handle();
               }
            } else if (this.apply(var1.down())) {
               int var4 = Module.client.player.getInventory().getSelectedSlot();
               Module.client.player.getInventory().setSelectedSlot(var3);
               this.update(var1.down());
               Module.client.player.getInventory().setSelectedSlot(var4);
               this.providerClose = 0;
            }
         }
      }
   }

   private void handle(BlockPos var1) {
      BlockHitResult var2 = this.prepare(var1);
      if (var2 == null) {
         this.moduleCollect = null;
      } else {
         RotationAngles var3 = this.handle(var2.getPos());
         RotationController.handle(var3, 65.0F, 65.0F, 65.0F, 65.0F, 2, 20, false);
         if (!(new RotationAngles(Module.client.player).handle(var3) > 6.0F)) {
            if (!var1.equals(this.moduleCollect)) {
               Module.client.interactionManager.attackBlock(var1, var2.getSide());
               this.moduleCollect = var1;
            } else {
               Module.client.interactionManager.updateBlockBreakingProgress(var1, var2.getSide());
            }

            Module.client.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private void save() {
      if (!this.vectorPerform.isEmpty()) {
         int var1 = this.vectorPerform.size();

         for (int var2 = 0; var2 < var1; var2++) {
            int var3 = (this.positionAdvance + var2) % var1;
            BlockPos var4 = null;

            for (BlockPos var6 : this.vectorPerform.get(var3)) {
               if (Module.client.world.getBlockState(var6).getBlock() == this.render()) {
                  var4 = var6;
                  break;
               }
            }

            if (var4 != null) {
               int var7 = this.process(Items.BONE_MEAL);
               if (var7 == -1) {
                  var7 = this.compute(Items.BONE_MEAL);
               }

               if (var7 == -1) {
                  if (this.summary.compute()) {
                     this.rendererScan = AutoWood.PrimaryMode.BONEMEAL;
                     this.drawProfile();
                     return;
                  }

                  ChatLogger.handle("§c[AutoWood] §fЗакончилась костная мука — выключаюсь");
                  this.toggle();
                  return;
               }

               if (!this.apply(var4)) {
                  return;
               }

               int var8 = Module.client.player.getInventory().getSelectedSlot();
               Module.client.player.getInventory().setSelectedSlot(var7);
               this.update(var4);
               Module.client.player.getInventory().setSelectedSlot(var8);
               this.positionAdvance = (var3 + 1) % var1;
               this.providerClose = 0;
               return;
            }
         }
      }
   }

   private boolean process(int var1) {
      BlockPos var2 = null;
      BlockHitResult var3 = null;

      for (BlockPos var5 : this.vectorPerform.get(var1)) {
         if (Module.client.world.getBlockState(var5).getBlock() == this.tick()) {
            BlockHitResult var6 = this.prepare(var5);
            if (var6 != null) {
               var2 = var5;
               var3 = var6;
               break;
            }
         }
      }

      if (var2 == null) {
         return false;
      }

      if (!(Module.client.player.getMainHandStack().getItem() instanceof AxeItem)) {
         int var7 = this.scheduleSource();
         if (var7 == -1) {
            if (this.vectorEncode.update(15000L)) {
               ChatLogger.handle("§e[AutoWood] §fНет топора в хотбаре, жду");
               this.vectorEncode.handle();
            }

            return true;
         }

         Module.client.player.getInventory().setSelectedSlot(var7);
      }

      RotationAngles var8 = this.handle(var3.getPos());
      RotationController.handle(var8, 65.0F, 65.0F, 65.0F, 65.0F, 2, 20, false);
      if (new RotationAngles(Module.client.player).handle(var8) > 6.0F) {
         return true;
      }

      Module.client.interactionManager.attackBlock(var2, var3.getSide());
      Module.client.player.swingHand(Hand.MAIN_HAND);
      this.frameCheck = var1;
      this.clientRefresh.handle();
      this.profileDraw = AutoWood.Mode.WAIT_FELL;
      this.providerClose = 0;
      return true;
   }

   private void submit() {
      if (this.frameCheck < 0 || this.frameCheck >= this.vectorPerform.size()) {
         this.profileDraw = AutoWood.Mode.FARM;
      } else if (!this.handle(this.frameCheck)) {
         this.frameCheck = -1;
         this.profileDraw = AutoWood.Mode.FARM;
         this.providerClose = 0;
      } else {
         if (this.clientRefresh.update(2000L)) {
            this.profileDraw = AutoWood.Mode.FARM;
            this.providerClose = 0;
         }
      }
   }

   private boolean unload() {
      if (!this.target.process("Продавать на ауке")) {
         return false;
      }

      if (this.profileDraw != AutoWood.Mode.FARM) {
         return false;
      }

      if (this.presetWrite) {
         if (!this.requestAdapt.update(30000L)) {
            return false;
         }

         this.presetWrite = false;
      }

      return this.handle(this.encodePoint()) >= (int)this.pending.compute();
   }

   private boolean fetch() {
      if (!this.target.process("Складывать в сундук")) {
         return false;
      }

      if (this.profileDraw != AutoWood.Mode.FARM) {
         return false;
      }

      if (this.colorMeasure) {
         if (!this.timerMeasure.update(30000L)) {
            return false;
         }

         this.colorMeasure = false;
      }

      return this.handle(this.encodePoint()) >= (int)this.pending.compute();
   }

   private void measure() {
      switch (this.presetSave) {
         case EQUIP:
            this.blendMatrix();
            break;
         case COMMAND:
            this.matchVector();
            break;
         case CONFIRM:
            this.projectItem();
            break;
         case WAIT_RESULT:
            this.computeResponse();
            break;
         default:
            this.fetchProvider();
      }
   }

   private void blendMatrix() {
      if (Module.client.currentScreen != null) {
         Module.client.player.closeHandledScreen();
      } else if (this.handle(this.encodePoint()) < 64) {
         this.fetchProvider();
      } else {
         int var1 = -1;
         int var2 = 0;

         for (int var3 = 0; var3 < 9; var3++) {
            ItemStack var4 = Module.client.player.getInventory().getStack(var3);
            if (var4.getItem() == this.encodePoint() && var4.getCount() > var2) {
               var1 = var3;
               var2 = var4.getCount();
            }
         }

         if (var1 != -1) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            if (Module.client.player.getMainHandStack().getItem() == this.encodePoint()) {
               this.presetSave = AutoWood.FallbackMode.COMMAND;
               this.keyFilter.handle();
            }
         } else {
            int var7 = -1;
            var2 = 0;

            for (int var8 = 9; var8 < 36; var8++) {
               ItemStack var5 = Module.client.player.getInventory().getStack(var8);
               if (var5.getItem() == this.encodePoint() && var5.getCount() > var2) {
                  var7 = var8;
                  var2 = var5.getCount();
               }
            }

            if (var7 == -1) {
               this.fetchProvider();
            } else {
               int var9 = this.bindIndex();
               if (var9 == -1) {
                  var9 = 0;
               }

               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var7, var9, SlotActionType.SWAP, Module.client.player);
            }
         }
      }
   }

   private void matchVector() {
      if (Module.client.player.getMainHandStack().getItem() != this.encodePoint()) {
         this.presetSave = AutoWood.FallbackMode.EQUIP;
      } else if (Module.client.currentScreen != null) {
         Module.client.player.closeHandledScreen();
      } else {
         Module.client.player.networkHandler.sendChatCommand("ah sell auto");
         this.keyFilter.handle();
         this.presetSave = AutoWood.FallbackMode.CONFIRM;
      }
   }

   private void projectItem() {
      if (this.keyFilter.update(1000L)) {
         Module.client.player.networkHandler.sendChatCommand("ah sell auto confirm");
         this.keyFilter.handle();
         this.presetSave = AutoWood.FallbackMode.WAIT_RESULT;
      }
   }

   private void computeResponse() {
      if (Module.client.player.getMainHandStack().getItem() != this.encodePoint()) {
         this.windowConvert = 0;
         if (this.handle(this.encodePoint()) >= 64) {
            this.presetSave = AutoWood.FallbackMode.EQUIP;
         } else {
            this.fetchProvider();
         }
      } else {
         if (this.keyFilter.update(6000L)) {
            this.windowConvert++;
            if (this.windowConvert >= 3) {
               ChatLogger.handle("§c[AutoWood] §fНе удалось продать дерево на аукционе, попробую позже");
               this.presetWrite = true;
               this.requestAdapt.handle();
               this.fetchProvider();
            } else {
               this.presetSave = AutoWood.FallbackMode.COMMAND;
               this.keyFilter.handle();
            }
         }
      }
   }

   private void fetchProvider() {
      this.presetSave = AutoWood.FallbackMode.NONE;
      this.providerClose = 0;
   }

   private void drawProfile() {
      this.profileInvoke = false;
      this.sourceSchedule = false;
      this.timerRender = 0;
      this.outputCollapse = null;
      this.scaleSave = false;
      this.scaleAdapt = -1;
      this.requestReceive.clear();
      this.indexBind.handle();
      this.actionRead.handle();
      this.configCollapse.handle();
      this.dataValidate.handle();
      switch (this.rendererScan) {
         case REPAIR:
            this.sourceBuild = this.handle(Items.EXPERIENCE_BOTTLE) > 0 ? AutoWood.SecondaryMode.REPAIRING : AutoWood.SecondaryMode.FIND_CHEST;
            break;
         case BONEMEAL:
            this.sourceBuild = this.saveScale() ? AutoWood.SecondaryMode.CRAFTING : AutoWood.SecondaryMode.FIND_CHEST;
            break;
         default:
            this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
      }
   }

   private void performVector() {
      if (Module.client.currentScreen == null || Module.client.currentScreen instanceof GenericContainerScreen) {
         switch (this.sourceBuild) {
            case FIND_CHEST:
               this.attachEvent();
               break;
            case GOING:
               this.readServer();
               break;
            case ROTATING:
               this.advancePosition();
               break;
            case OPENING:
               this.checkFrame();
               break;
            case WAIT_GUI:
               this.collectModule();
               break;
            case CRAFTING:
               this.savePreset();
               break;
            case REPAIRING:
               this.writePreset();
               break;
            case RETURNING:
               this.scheduleAnimation();
               break;
            default:
               this.buildSource();
         }
      }
   }

   private void attachEvent() {
      this.outputCollapse = this.process(this.rendererScan);
      if (this.outputCollapse == null) {
         this.process(
            "§c[AutoWood] §fНе найден сундук «" + this.compute(this.rendererScan) + "» в радиусе " + (int)this.matrixBlend.compute() + " бл. — выключаюсь"
         );
      } else {
         if (this.execute(this.outputCollapse) && this.resolve(this.outputCollapse)) {
            this.sourceBuild = AutoWood.SecondaryMode.ROTATING;
            this.indexBind.handle();
         } else {
            this.profileInvoke = true;
            this.sourceBuild = AutoWood.SecondaryMode.GOING;
            this.actionRead.handle();
            this.configCollapse.handle();
         }
      }
   }

   private void readServer() {
      if (this.outputCollapse != null && this.compute(this.outputCollapse)) {
         double var1 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.outputCollapse));
         if (var1 <= 4.5 && this.resolve(this.outputCollapse)) {
            if (this.animationSchedule != null) {
               this.animationSchedule.getPathingBehavior().cancelEverything();
            }

            this.sourceBuild = AutoWood.SecondaryMode.ROTATING;
            this.indexBind.handle();
         } else {
            if (this.animationSchedule != null && (!this.animationSchedule.getCustomGoalProcess().isActive() || this.actionRead.update(1500L))) {
               this.animationSchedule.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.outputCollapse, 2));
               this.actionRead.handle();
            }

            if (this.configCollapse.update(15000L)) {
               this.process("§c[AutoWood] §fНе удалось дойти до сундука «" + this.compute(this.rendererScan) + "»");
            }
         }
      } else {
         this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
      }
   }

   private void advancePosition() {
      if (this.outputCollapse == null) {
         this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
      } else {
         if (this.apply(this.outputCollapse)) {
            this.sourceBuild = AutoWood.SecondaryMode.OPENING;
            this.indexBind.handle();
         }
      }
   }

   private void checkFrame() {
      if (this.indexBind.update(200L)) {
         this.update(this.outputCollapse);
         this.sourceBuild = AutoWood.SecondaryMode.WAIT_GUI;
         this.indexBind.handle();
      }
   }

   private void collectModule() {
      if (!(Module.client.currentScreen instanceof GenericContainerScreen)) {
         if (this.indexBind.update(2500L)) {
            this.timerRender++;
            if (this.timerRender > 3) {
               this.process("§c[AutoWood] §fНе удалось открыть сундук «" + this.compute(this.rendererScan) + "»");
            } else {
               this.sourceBuild = AutoWood.SecondaryMode.ROTATING;
               this.indexBind.handle();
            }
         }
      }
   }

   private void handle(GenericContainerScreen var1) {
      GenericContainerScreenHandler var2 = (GenericContainerScreenHandler)var1.getScreenHandler();
      int var3 = var2.slots.size() - 36;
      if (var3 <= 0) {
         this.handle("§c[AutoWood] §fСундук «" + this.compute(this.rendererScan) + "» пуст — выключаюсь");
      } else if (this.dataValidate.update(120L)) {
         if (this.handle(this.rendererScan)) {
            this.closeProvider();
         } else {
            int var4 = this.handle(var2, var3, this.rendererScan);
            if (var4 == -1) {
               if (this.sourceSchedule) {
                  this.closeProvider();
               } else {
                  this.handle("§c[AutoWood] §fВ сундуке «" + this.compute(this.rendererScan) + "» нет нужных предметов — выключаюсь");
               }
            } else {
               Module.client.interactionManager.clickSlot(var2.syncId, var4, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.sourceSchedule = true;
               this.dataValidate.handle();
            }
         }
      }
   }

   private void process(GenericContainerScreen var1) {
      GenericContainerScreenHandler var2 = (GenericContainerScreenHandler)var1.getScreenHandler();
      int var3 = var2.slots.size() - 36;
      if (var3 <= 0) {
         if (Module.client.player != null) {
            Module.client.player.closeHandledScreen();
         }

         this.scanRenderer();
      } else if (this.dataValidate.update(120L)) {
         for (int var4 = var3; var4 < var2.slots.size(); var4++) {
            ItemStack var5 = ((Slot)var2.slots.get(var4)).getStack();
            if (var5.getItem() == this.encodePoint() && this.handle(var2, var3, var5)) {
               Module.client.interactionManager.clickSlot(var2.syncId, var4, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.sourceSchedule = true;
               this.dataValidate.handle();
               return;
            }
         }

         if (!this.sourceSchedule) {
            this.colorMeasure = true;
            this.timerMeasure.handle();
            ChatLogger.handle("§c[AutoWood] §fСундук «лут/дерево» переполнен — некуда складывать, попробую позже");
         }

         if (Module.client.player != null) {
            Module.client.player.closeHandledScreen();
         }

         this.scanRenderer();
      }
   }

   private boolean handle(GenericContainerScreenHandler var1, int var2, ItemStack var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         ItemStack var5 = ((Slot)var1.slots.get(var4)).getStack();
         if (var5.isEmpty()) {
            return true;
         }

         if (var5.getItem() == var3.getItem() && var5.getCount() < var5.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(AutoWood.PrimaryMode var1) {
      return switch (var1) {
         case REPAIR -> this.handle(Items.EXPERIENCE_BOTTLE) >= 64;
         case BONEMEAL -> this.renderTimer() >= 128;
         default -> true;
      };
   }

   private int handle(GenericContainerScreenHandler var1, int var2, AutoWood.PrimaryMode var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         ItemStack var5 = ((Slot)var1.slots.get(var4)).getStack();
         if (!var5.isEmpty() && this.handle(var5.getItem(), var3)) {
            return var4;
         }
      }

      return -1;
   }

   private boolean handle(Item var1, AutoWood.PrimaryMode var2) {
      return switch (var2) {
         case REPAIR -> var1 == Items.EXPERIENCE_BOTTLE;
         case BONEMEAL -> var1 == Items.BONE_MEAL || var1 == Items.BONE || var1 == Items.BONE_BLOCK;
         default -> false;
      };
   }

   private void closeProvider() {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }
      this.sourceBuild = switch (this.rendererScan) {
         case REPAIR -> AutoWood.SecondaryMode.REPAIRING;
         case BONEMEAL -> AutoWood.SecondaryMode.CRAFTING;
         default -> AutoWood.SecondaryMode.RETURNING;
      };
      if (this.sourceBuild == AutoWood.SecondaryMode.RETURNING) {
         this.actionRead.handle();
         this.configCollapse.handle();
      }

      this.indexBind.handle();
      this.dataValidate.handle();
      this.requestReceive.clear();
   }

   private void handle(String var1) {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      this.process(var1);
   }

   private void savePreset() {
      if (Module.client.currentScreen == null) {
         if (!this.requestReceive.isEmpty()) {
            if (this.dataValidate.update(90L)) {
               this.requestReceive.poll().run();
               this.dataValidate.handle();
            }
         } else if (this.handle(Items.BONE_MEAL) >= 128) {
            this.scanRenderer();
         } else {
            int var1 = this.computeColor();
            if (var1 == -1) {
               if (this.handle(Items.BONE_MEAL) == 0) {
                  this.process("§c[AutoWood] §fКостная мука закончилась и крафтить не из чего — выключаюсь");
               } else {
                  this.scanRenderer();
               }
            } else {
               int var2 = Module.client.player.playerScreenHandler.syncId;
               this.requestReceive.add(() -> Module.client.interactionManager.clickSlot(var2, var1, 0, SlotActionType.PICKUP, Module.client.player));
               this.requestReceive.add(() -> Module.client.interactionManager.clickSlot(var2, 1, 0, SlotActionType.PICKUP, Module.client.player));
               this.requestReceive.add(() -> Module.client.interactionManager.clickSlot(var2, 0, 0, SlotActionType.QUICK_MOVE, Module.client.player));
               this.requestReceive.add(this::convertWindow);
            }
         }
      }
   }

   private void convertWindow() {
      int var1 = Module.client.player.playerScreenHandler.syncId;

      for (int var2 = 1; var2 <= 4; var2++) {
         if (((Slot)Module.client.player.playerScreenHandler.slots.get(var2)).hasStack()) {
            Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
         }
      }

      if (!Module.client.player.playerScreenHandler.getCursorStack().isEmpty()) {
         int var3 = this.adaptScale();
         if (var3 != -1) {
            Module.client.interactionManager.clickSlot(var1, var3, 0, SlotActionType.PICKUP, Module.client.player);
         }
      }
   }

   private void writePreset() {
      if (Module.client.currentScreen == null) {
         int var1 = Module.client.player.playerScreenHandler.syncId;
         if (!this.scaleSave) {
            int var4 = this.invokeProfile();
            if (var4 == -1) {
               this.scanRenderer();
            } else if (this.handle(Items.EXPERIENCE_BOTTLE) == 0) {
               this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
            } else if (!Module.client.player.getOffHandStack().isEmpty()) {
               int var5 = this.adaptScale();
               if (var5 == -1) {
                  this.process("§c[AutoWood] §fОсвободите офф-хенд или место в инвентаре для починки");
               } else {
                  Module.client.interactionManager.clickSlot(var1, 45, 0, SlotActionType.PICKUP, Module.client.player);
                  Module.client.interactionManager.clickSlot(var1, var5, 0, SlotActionType.PICKUP, Module.client.player);
               }
            } else {
               this.colorCompute = var4;
               this.textureRun = Module.client.player.getPitch();
               Module.client.player.getInventory().setSelectedSlot(var4);
               Module.client.interactionManager.clickSlot(var1, 45, var4, SlotActionType.SWAP, Module.client.player);
               if (!this.measureColor()) {
                  Module.client.interactionManager.clickSlot(var1, 45, var4, SlotActionType.SWAP, Module.client.player);
                  Module.client.player.getInventory().setSelectedSlot(var4);
                  this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
               } else {
                  this.scaleSave = true;
                  this.scaleAdapt = -1;
                  this.scaleRender.handle();
                  this.indexBind.handle();
               }
            }
         } else {
            ItemStack var2 = Module.client.player.getOffHandStack();
            if (!var2.isEmpty() && var2.isDamageable() && var2.getDamage() != 0) {
               if (Module.client.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE && !this.measureColor()) {
                  this.compute(var1);
                  this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
               } else {
                  int var3 = var2.getDamage();
                  if (this.scaleAdapt == -1) {
                     this.scaleAdapt = var3;
                  }

                  if (var3 < this.scaleAdapt) {
                     this.scaleAdapt = var3;
                     this.scaleRender.handle();
                  } else if (this.scaleRender.update(4000L)) {
                     this.compute(var1);
                     this.process("§c[AutoWood] §fТопор не чинится (нет «Починки»?)");
                     return;
                  }

                  if (this.indexBind.update(120L)) {
                     Module.client.player.setPitch(90.0F);
                     Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
                     Module.client.player.swingHand(Hand.MAIN_HAND);
                     this.indexBind.handle();
                  }
               }
            } else {
               this.compute(var1);
            }
         }
      }
   }

   private void compute(int var1) {
      Module.client.interactionManager.clickSlot(var1, 45, this.colorCompute, SlotActionType.SWAP, Module.client.player);
      if (this.colorCompute >= 0) {
         Module.client.player.getInventory().setSelectedSlot(this.colorCompute);
      }

      Module.client.player.setPitch(this.textureRun);
      if (!Module.client.player.getOffHandStack().isEmpty()) {
         int var2 = this.adaptScale();
         if (var2 != -1) {
            Module.client.interactionManager.clickSlot(var1, 45, 0, SlotActionType.PICKUP, Module.client.player);
            Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.PICKUP, Module.client.player);
         }
      }

      this.scaleSave = false;
      this.scanRenderer();
   }

   private boolean measureColor() {
      int var1 = this.runTexture();
      if (var1 == -1) {
         return false;
      }

      if (var1 >= 36 && var1 <= 44) {
         Module.client.player.getInventory().setSelectedSlot(var1 - 36);
      } else {
         Module.client.interactionManager
            .clickSlot(
               Module.client.player.playerScreenHandler.syncId, var1, Module.client.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, Module.client.player
            );
      }

      return true;
   }

   private void scheduleAnimation() {
      if (this.profileInvoke && this.serverRead != null && this.animationSchedule != null) {
         if (!Module.client.player.getBlockPos().equals(this.serverRead) && !(Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.serverRead)) <= 0.7)) {
            if (!this.animationSchedule.getCustomGoalProcess().isActive() || this.actionRead.update(1500L)) {
               this.animationSchedule.getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.serverRead));
               this.actionRead.handle();
            }

            if (this.configCollapse.update(20000L)) {
               this.animationSchedule.getPathingBehavior().cancelEverything();
               this.buildSource();
            }
         } else {
            this.animationSchedule.getPathingBehavior().cancelEverything();
            this.buildSource();
         }
      } else {
         this.buildSource();
      }
   }

   private void scanRenderer() {
      this.sourceBuild = AutoWood.SecondaryMode.RETURNING;
      this.actionRead.handle();
      this.configCollapse.handle();
   }

   private void buildSource() {
      if (this.animationSchedule != null) {
         this.animationSchedule.getPathingBehavior().cancelEverything();
      }

      this.collapseOutput();
      this.profileDraw = AutoWood.Mode.FARM;
      this.providerClose = 0;
   }

   private void process(String var1) {
      ChatLogger.handle(var1);
      if (this.animationSchedule != null) {
         this.animationSchedule.getPathingBehavior().cancelEverything();
      }

      this.collapseOutput();
      this.toggle();
   }

   private void collapseOutput() {
      this.rendererScan = AutoWood.PrimaryMode.NONE;
      this.sourceBuild = AutoWood.SecondaryMode.FIND_CHEST;
      this.outputCollapse = null;
      this.profileInvoke = false;
      this.sourceSchedule = false;
      this.timerRender = 0;
      this.scaleSave = false;
      this.colorCompute = -1;
      this.scaleAdapt = -1;
      this.requestReceive.clear();
   }

   private int invokeProfile() {
      if (!this.previous.compute()) {
         return -1;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.isDamageable() && var2.getItem() instanceof AxeItem) {
            int var3 = var2.getMaxDamage() - var2.getDamage();
            if (var3 <= (int)this.latest.compute()) {
               return var1;
            }
         }
      }

      return -1;
   }

   private int scheduleSource() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).getItem() instanceof AxeItem) {
            return var1;
         }
      }

      return -1;
   }

   private int handle(Item var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (var4.getItem() == var1) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private int renderTimer() {
      return this.handle(Items.BONE_MEAL) + this.handle(Items.BONE) * 3 + this.handle(Items.BONE_BLOCK) * 9;
   }

   private boolean saveScale() {
      return this.handle(Items.BONE) > 0 || this.handle(Items.BONE_BLOCK) > 0;
   }

   private int computeColor() {
      for (int var1 = 9; var1 <= 44; var1++) {
         Item var2 = ((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).getStack().getItem();
         if (var2 == Items.BONE || var2 == Items.BONE_BLOCK) {
            return var1;
         }
      }

      return -1;
   }

   private int adaptScale() {
      for (int var1 = 9; var1 <= 44; var1++) {
         if (!((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).hasStack()) {
            return var1;
         }
      }

      return -1;
   }

   private int runTexture() {
      for (int var1 = 9; var1 <= 44; var1++) {
         if (((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).getStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            return var1;
         }
      }

      return -1;
   }

   private BlockPos process(AutoWood.PrimaryMode var1) {
      if (Module.client.world != null && Module.client.player != null) {
         BlockPos var2 = this.serverRead != null ? this.serverRead : Module.client.player.getBlockPos();
         int var3 = (int)this.matrixBlend.compute();
         BlockPos var4 = null;
         double var5 = Double.MAX_VALUE;

         for (BlockPos var8 : BlockPos.iterate(var2.add(-var3, -5, -var3), var2.add(var3, 5, var3))) {
            if (this.compute(var8) && this.handle(var8, var1)) {
               double var9 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(var8));
               if (var9 < var5) {
                  var5 = var9;
                  var4 = var8.toImmutable();
               }
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   private boolean handle(BlockPos var1, AutoWood.PrimaryMode var2) {
      String var3 = this.process(var1).toLowerCase(Locale.ROOT);
      if (var3.isEmpty()) {
         return false;
      }

      String[] var4;
      String[] var5;
      switch (var2) {
         case REPAIR:
            var4 = new String[]{"опыт"};
            var5 = new String[]{"кост", "мука"};
            break;
         case BONEMEAL:
            var4 = new String[]{"кост", "мука"};
            var5 = new String[]{"опыт", "лут", "дерев"};
            break;
         case DEPOSIT:
            var4 = new String[]{"лут", "дерев"};
            var5 = new String[]{"опыт", "кост", "мука"};
            break;
         default:
            return false;
      }

      boolean var6 = false;

      for (String var10 : var4) {
         if (var3.contains(var10)) {
            var6 = true;
            break;
         }
      }

      if (!var6) {
         return false;
      }

      for (String var14 : var5) {
         if (var3.contains(var14)) {
            return false;
         }
      }

      return true;
   }

   private String process(BlockPos var1) {
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

   private boolean compute(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      BlockEntity var2 = Module.client.world.getBlockEntity(var1);
      return var2 instanceof ChestBlockEntity || var2 instanceof BarrelBlockEntity || var2 instanceof ShulkerBoxBlockEntity;
   }

   private boolean resolve(BlockPos var1) {
      return this.prepare(var1) != null;
   }

   private String compute(AutoWood.PrimaryMode var1) {
      return switch (var1) {
         case REPAIR -> "опыт";
         case BONEMEAL -> "костная мука";
         case DEPOSIT -> "лут/дерево";
         default -> "";
      };
   }

   private void update(BlockPos var1) {
      Vec3d var2 = this.handle(var1, Direction.UP);
      BlockHitResult var3 = new BlockHitResult(var2, Direction.UP, var1, false);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var3);
      Module.client.player.swingHand(Hand.MAIN_HAND);
   }

   private boolean apply(BlockPos var1) {
      RotationAngles var2 = this.handle(this.handle(var1, Direction.UP));
      RotationController.handle(var2, 65.0F, 65.0F, 65.0F, 65.0F, 2, 20, false);
      return new RotationAngles(Module.client.player).handle(var2) <= 6.0F;
   }

   private Vec3d handle(BlockPos var1, Direction var2) {
      return new Vec3d(var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5);
   }

   private RotationAngles handle(Vec3d var1) {
      if (Module.client.player == null) {
         return new RotationAngles(0.0F, 0.0F);
      }

      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)Math.toDegrees(Math.atan2(-var3, var7));
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      return new RotationAngles(var11, var12);
   }

   private boolean execute(BlockPos var1) {
      return Module.client.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var1)) <= 20.25;
   }

   private BlockHitResult prepare(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double[] var3 = new double[]{0.5, 0.2, 0.8};

      for (double var7 : var3) {
         for (double var12 : var3) {
            for (double var17 : var3) {
               Vec3d var19 = new Vec3d(var1.getX() + var7, var1.getY() + var12, var1.getZ() + var17);
               BlockHitResult var20 = Module.client.world.raycast(new RaycastContext(var2, var19, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
               if (var20.getType() == Type.BLOCK && var20.getBlockPos().equals(var1)) {
                  return var20;
               }
            }
         }
      }

      return null;
   }

   private int process(Item var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (Module.client.player.getInventory().getStack(var2).getItem() == var1) {
            return var2;
         }
      }

      return -1;
   }

   private int compute(Item var1) {
      int var2 = -1;

      for (int var3 = 9; var3 < 36; var3++) {
         if (Module.client.player.getInventory().getStack(var3).getItem() == var1) {
            var2 = var3;
            break;
         }
      }

      if (var2 == -1) {
         return -1;
      }

      int var4 = this.bindIndex();
      if (var4 == -1) {
         return -1;
      }

      Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var2, var4, SlotActionType.SWAP, Module.client.player);
      return var4;
   }

   private int bindIndex() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      for (int var3 = 0; var3 < 9; var3++) {
         Item var2 = Module.client.player.getInventory().getStack(var3).getItem();
         if (!(var2 instanceof AxeItem)
            && var2 != this.refresh()
            && var2 != Items.BONE_MEAL
            && var2 != Items.BONE
            && var2 != Items.BONE_BLOCK
            && var2 != Items.EXPERIENCE_BOTTLE) {
            return var3;
         }
      }

      return -1;
   }

   enum FallbackMode {
      NONE,
      EQUIP,
      COMMAND,
      CONFIRM,
      WAIT_RESULT;
   }

   enum Mode {
      SETUP,
      FARM,
      WAIT_FELL;
   }

   enum PrimaryMode {
      NONE,
      REPAIR,
      BONEMEAL,
      DEPOSIT;
   }

   enum SecondaryMode {
      FIND_CHEST,
      GOING,
      ROTATING,
      OPENING,
      WAIT_GUI,
      CRAFTING,
      REPAIRING,
      RETURNING;
   }
}
