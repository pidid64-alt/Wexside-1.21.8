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
import net.minecraft.item.HoeItem;
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
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AppleFarmer", category = ModuleCategory.Misc, description = "Автоматически фармит для вас яблоки")
public class AppleFarmer extends Module {
   public final NumberSetting source = new NumberSetting("Дистанция", 4.5F, 3.0F, 4.5F, 0.1F, true);
   public final BooleanSetting target = new BooleanSetting("Авто-пополнение из сундуков", true);
   public final NumberSetting pending = new NumberSetting("Чинить при прочности <", 150.0F, 20.0F, 1000.0F, 10.0F, false).handle(() -> !this.target.compute());
   public final NumberSetting previous = new NumberSetting("Радиус поиска сундуков", 12.0F, 4.0F, 40.0F, 1.0F, false).handle(() -> !this.target.compute());
   public final NumberSetting latest = new NumberSetting("Разгрузка при свободных слотах ≤", 3.0F, 0.0F, 10.0F, 1.0F, false)
      .handle(() -> !this.target.compute());
   private AppleFarmer.Mode summary = AppleFarmer.Mode.FIND_SPOT;
   private BlockPos matrixBlend = null;
   private final List<BlockPos> vectorMatch = new ArrayList<>();
   private static final int itemProject = 2;
   private static final int responseCompute = 4;
   private static final int providerFetch = 8;
   private Direction profileDraw = Direction.NORTH;
   private BlockPos vectorPerform = null;
   private int eventAttach = 0;
   private int serverRead = 0;
   private IBaritone positionAdvance;
   private boolean frameCheck = false;
   private BlockPos moduleCollect = null;
   private AppleFarmer.PrimaryMode providerClose = AppleFarmer.PrimaryMode.NONE;
   private AppleFarmer.SecondaryMode presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
   private BlockPos windowConvert = null;
   private boolean presetWrite = false;
   private boolean colorMeasure = false;
   private int animationSchedule = 0;
   private boolean rendererScan = false;
   private int sourceBuild = -1;
   private int outputCollapse = -1;
   private float profileInvoke = 0.0F;
   private final Stopwatch sourceSchedule = new Stopwatch();
   private final Stopwatch timerRender = new Stopwatch();
   private final Stopwatch scaleSave = new Stopwatch();
   private final Stopwatch colorCompute = new Stopwatch();
   private final Stopwatch scaleAdapt = new Stopwatch();
   private final Stopwatch textureRun = new Stopwatch();
   private final Queue<Runnable> indexBind = new ArrayDeque<>();
   private boolean actionRead = false;
   private static final int configCollapse = 64;
   private static final int dataValidate = 64;
   private static final int scaleRender = 128;
   private static final int clientRefresh = 64;

   public AppleFarmer() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest);
   }

   @Override
   public void handle() {
      super.handle();
      this.positionAdvance = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.summary = AppleFarmer.Mode.FIND_SPOT;
      this.matrixBlend = null;
      this.vectorMatch.clear();
      this.vectorPerform = null;
      this.serverRead = 0;
      this.frameCheck = false;
      this.moduleCollect = null;
      this.actionRead = false;
      this.savePreset();
      this.eventAttach = 0;
   }

   @Override
   public void process() {
      if (Module.client.player != null && this.rendererScan) {
         try {
            Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, 45, this.sourceBuild, SlotActionType.SWAP, Module.client.player);
            if (this.sourceBuild >= 0) {
               Module.client.player.getInventory().setSelectedSlot(this.sourceBuild);
            }

            Module.client.player.setPitch(this.profileInvoke);
         } catch (Exception var2) {
         }
      }

      this.rendererScan = false;
      if (this.positionAdvance != null) {
         this.positionAdvance.getPathingBehavior().cancelEverything();
      }

      this.savePreset();
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      this.vectorPerform = null;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (!PlayerHelper.refresh()) {
            if (this.target.compute()) {
               if (this.providerClose != AppleFarmer.PrimaryMode.NONE && Module.client.currentScreen instanceof GenericContainerScreen var4) {
                  if (this.providerClose == AppleFarmer.PrimaryMode.UNLOAD) {
                     this.process(var4);
                  } else {
                     this.handle(var4);
                  }

                  return;
               }

               if (this.providerClose == AppleFarmer.PrimaryMode.NONE && this.frameCheck && Module.client.currentScreen == null) {
                  AppleFarmer.PrimaryMode var2 = this.animate();
                  if (var2 != AppleFarmer.PrimaryMode.NONE) {
                     this.providerClose = var2;
                     this.unload();
                  }
               }

               if (this.providerClose != AppleFarmer.PrimaryMode.NONE) {
                  this.fetch();
                  return;
               }
            }

            if (Module.client.currentScreen == null) {
               this.serverRead++;
               if (this.serverRead > 4) {
                  this.scheduleSource();
                  this.serverRead = 0;
               }

               this.eventAttach++;
               if (this.summary == AppleFarmer.Mode.BREAKING || this.eventAttach >= 2) {
                  switch (this.summary) {
                     case FIND_SPOT:
                        this.refresh();
                        break;
                     case PLACE:
                        this.render();
                        break;
                     case BONEMEAL:
                        this.tick();
                        break;
                     case SCAN_TREE:
                        this.drawAnimation();
                        break;
                     case BREAKING:
                        this.encodePoint();
                  }
               }
            }
         }
      }
   }

   private void refresh() {
      if (!this.frameCheck) {
         this.moduleCollect = Module.client.player.getBlockPos();
         this.profileDraw = Module.client.player.getHorizontalFacing();
         this.frameCheck = true;
      }

      BlockPos var1 = this.moduleCollect;
      BlockPos var2 = var1.offset(this.profileDraw);
      BlockPos var3 = var1.offset(this.profileDraw, 2);
      BlockState var4 = Module.client.world.getBlockState(var3);
      if (this.prepare(var2) && this.prepare(var2.up())) {
         if (var4.getBlock() == Blocks.OAK_SAPLING) {
            this.matrixBlend = var3;
            this.summary = AppleFarmer.Mode.BONEMEAL;
            this.eventAttach = 0;
         } else if (this.process(var4)) {
            this.matrixBlend = var3;
            this.summary = AppleFarmer.Mode.SCAN_TREE;
            this.eventAttach = 0;
         } else {
            BlockPos var5 = var3.down();
            if (this.execute(var5) && var4.isReplaceable()) {
               this.matrixBlend = var5.up();
               this.summary = AppleFarmer.Mode.PLACE;
            } else {
               ChatLogger.handle("§c[AppleFarmer] §fВстаньте напротив места посадки: земля должна быть через один блок перед вами");
               this.toggle();
            }

            this.eventAttach = 0;
         }
      } else {
         ChatLogger.handle("§c[AppleFarmer] §fМежду вами и местом посадки должен быть свободный блок");
         this.toggle();
         this.eventAttach = 0;
      }
   }

   private void render() {
      if (this.matrixBlend == null) {
         this.summary = AppleFarmer.Mode.FIND_SPOT;
      } else {
         BlockState var1 = Module.client.world.getBlockState(this.matrixBlend);
         if (var1.getBlock() == Blocks.OAK_SAPLING) {
            this.summary = AppleFarmer.Mode.BONEMEAL;
            this.eventAttach = 0;
         } else if (!var1.isReplaceable()) {
            this.summary = AppleFarmer.Mode.FIND_SPOT;
            this.eventAttach = 0;
         } else {
            int var2 = this.process(Items.OAK_SAPLING);
            if (var2 == -1) {
               var2 = this.compute(Items.OAK_SAPLING);
            }

            if (var2 == -1) {
               if (this.target.compute()) {
                  this.summary = AppleFarmer.Mode.FIND_SPOT;
                  this.eventAttach = 0;
               } else {
                  ChatLogger.handle("§c[AppleFarmer] §fНет саженцев");
                  this.toggle();
               }
            } else if (this.update(this.matrixBlend.down())) {
               int var3 = Module.client.player.getInventory().getSelectedSlot();
               Module.client.player.getInventory().setSelectedSlot(var2);
               this.resolve(this.matrixBlend.down());
               Module.client.player.getInventory().setSelectedSlot(var3);
               this.summary = AppleFarmer.Mode.BONEMEAL;
               this.eventAttach = 0;
            }
         }
      }
   }

   private void tick() {
      if (this.matrixBlend != null) {
         BlockState var1 = Module.client.world.getBlockState(this.matrixBlend);
         if (this.process(var1)) {
            this.summary = AppleFarmer.Mode.SCAN_TREE;
         } else if (var1.isReplaceable()) {
            this.summary = AppleFarmer.Mode.PLACE;
         } else if (var1.getBlock() != Blocks.OAK_SAPLING) {
            this.summary = AppleFarmer.Mode.FIND_SPOT;
         } else {
            int var2 = this.process(Items.BONE_MEAL);
            if (var2 == -1) {
               var2 = this.compute(Items.BONE_MEAL);
            }

            if (var2 == -1) {
               if (this.target.compute()) {
                  this.summary = AppleFarmer.Mode.FIND_SPOT;
                  this.eventAttach = 0;
               } else {
                  ChatLogger.handle("§c[AppleFarmer] §fНет костной муки");
                  this.toggle();
               }
            } else if (this.update(this.matrixBlend)) {
               int var3 = Module.client.player.getInventory().getSelectedSlot();
               Module.client.player.getInventory().setSelectedSlot(var2);
               this.resolve(this.matrixBlend);
               Module.client.player.getInventory().setSelectedSlot(var3);
               this.eventAttach = 0;
            }
         }
      }
   }

   private void drawAnimation() {
      this.vectorMatch.clear();
      BlockPos var1 = this.matrixBlend;
      if (var1 == null) {
         this.summary = AppleFarmer.Mode.PLACE;
      } else {
         double var2 = Math.min(this.source.compute(), 4.5F);
         int var4 = (int)Math.ceil(var2) + 1;
         BlockPos var5 = this.frameCheck && this.moduleCollect != null ? this.moduleCollect : Module.client.player.getBlockPos();

         for (int var6 = -var4; var6 <= var4; var6++) {
            for (int var7 = -2; var7 <= 8; var7++) {
               for (int var8 = -var4; var8 <= var4; var8++) {
                  BlockPos var9 = var5.add(var6, var7, var8);
                  BlockState var10 = Module.client.world.getBlockState(var9);
                  if (this.handle(var10) && this.check(var9) && (!this.process(var10) || this.handle(var9, var1))) {
                     this.vectorMatch.add(var9);
                  }
               }
            }
         }

         if (this.vectorMatch.isEmpty()) {
            this.summary = AppleFarmer.Mode.PLACE;
         } else {
            this.vectorMatch.sort(this::process);
            this.vectorPerform = null;
            this.summary = AppleFarmer.Mode.BREAKING;
         }
      }
   }

   private boolean handle(BlockPos var1, BlockPos var2) {
      return Math.abs(var1.getX() - var2.getX()) <= 4 && Math.abs(var1.getZ() - var2.getZ()) <= 4;
   }

   private void encodePoint() {
      this.vectorMatch.removeIf(var1x -> !this.handle(Module.client.world.getBlockState(var1x)) || !this.check(var1x));
      if (this.vectorMatch.isEmpty()) {
         this.summary = AppleFarmer.Mode.PLACE;
         this.vectorPerform = null;
      } else {
         BlockPos var1 = this.collapseOutput();
         if (var1 == null) {
            this.summary = AppleFarmer.Mode.SCAN_TREE;
            this.vectorPerform = null;
            this.eventAttach = 0;
         } else {
            BlockState var2 = Module.client.world.getBlockState(var1);
            BlockHitResult var3 = this.onTick(var1);
            if (var3 == null) {
               this.vectorPerform = null;
            } else {
               boolean var4 = this.compute(var2);
               boolean var5 = this.process(var2);
               if (var5) {
                  this.compute(true);
               } else if (var4) {
                  this.compute(false);
               }

               RotationAngles var6 = this.handle(var3.getPos());
               RotationController.handle(var6, 65.0F, 65.0F, 65.0F, 65.0F, 2, 20, false);
               if (!(new RotationAngles(Module.client.player).handle(var6) > 6.0F)) {
                  if (!var1.equals(this.vectorPerform)) {
                     Module.client.interactionManager.attackBlock(var1, var3.getSide());
                     this.vectorPerform = var1;
                  } else {
                     Module.client.interactionManager.updateBlockBreakingProgress(var1, var3.getSide());
                  }

                  Module.client.player.swingHand(Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   private AppleFarmer.PrimaryMode animate() {
      if (this.load()) {
         return AppleFarmer.PrimaryMode.UNLOAD;
      }

      if (this.pending.compute() > 0.0F && this.convertWindow() != -1) {
         return AppleFarmer.PrimaryMode.REPAIR;
      }

      if (this.summary == AppleFarmer.Mode.FIND_SPOT) {
         if (this.handle(Items.BONE_MEAL) == 0) {
            return AppleFarmer.PrimaryMode.BONEMEAL;
         }

         if (this.handle(Items.OAK_SAPLING) == 0) {
            return AppleFarmer.PrimaryMode.SAPLING;
         }
      }

      return AppleFarmer.PrimaryMode.NONE;
   }

   private boolean load() {
      if (this.actionRead) {
         if (!this.textureRun.update(30000L)) {
            return false;
         }

         this.actionRead = false;
      }

      return this.save() <= (int)this.latest.compute() && this.submit();
   }

   private int save() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private boolean submit() {
      return this.handle(Items.OAK_LOG) > 0 || this.handle(Items.APPLE) > 0 || this.handle(Items.STICK) > 0 || this.handle(Items.OAK_SAPLING) > 64;
   }

   private void unload() {
      this.presetWrite = false;
      this.colorMeasure = false;
      this.animationSchedule = 0;
      this.windowConvert = null;
      this.rendererScan = false;
      this.outputCollapse = -1;
      this.indexBind.clear();
      this.vectorPerform = null;
      this.vectorMatch.clear();
      this.sourceSchedule.handle();
      this.timerRender.handle();
      this.scaleSave.handle();
      this.colorCompute.handle();
      switch (this.providerClose) {
         case REPAIR:
            this.presetSave = this.handle(Items.EXPERIENCE_BOTTLE) > 0 ? AppleFarmer.SecondaryMode.REPAIRING : AppleFarmer.SecondaryMode.FIND_CHEST;
            break;
         case BONEMEAL:
            this.presetSave = this.measureColor() ? AppleFarmer.SecondaryMode.CRAFTING : AppleFarmer.SecondaryMode.FIND_CHEST;
            break;
         default:
            this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
      }
   }

   private void fetch() {
      if (Module.client.currentScreen == null || Module.client.currentScreen instanceof GenericContainerScreen) {
         switch (this.presetSave) {
            case FIND_CHEST:
               this.measure();
               break;
            case GOING:
               this.blendMatrix();
               break;
            case ROTATING:
               this.matchVector();
               break;
            case OPENING:
               this.projectItem();
               break;
            case WAIT_GUI:
               this.computeResponse();
               break;
            case CRAFTING:
               this.drawProfile();
               break;
            case REPAIRING:
               this.attachEvent();
               break;
            case RETURNING:
               this.advancePosition();
               break;
            case FACING:
               this.checkFrame();
               break;
            default:
               this.closeProvider();
         }
      }
   }

   private void measure() {
      this.windowConvert = this.compute(this.providerClose);
      if (this.windowConvert == null) {
         this.process("§c[AppleFarmer] §fНе найден сундук «" + this.resolve(this.providerClose) + "» в радиусе " + (int)this.previous.compute() + " бл.");
      } else {
         if (this.check(this.windowConvert) && this.compute(this.windowConvert)) {
            this.presetSave = AppleFarmer.SecondaryMode.ROTATING;
            this.sourceSchedule.handle();
         } else {
            this.presetWrite = true;
            this.presetSave = AppleFarmer.SecondaryMode.GOING;
            this.timerRender.handle();
            this.scaleSave.handle();
         }
      }
   }

   private void blendMatrix() {
      if (this.windowConvert != null && this.process(this.windowConvert)) {
         double var1 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.windowConvert));
         if (var1 <= this.source.compute() && this.compute(this.windowConvert)) {
            if (this.positionAdvance != null) {
               this.positionAdvance.getPathingBehavior().cancelEverything();
            }

            this.presetSave = AppleFarmer.SecondaryMode.ROTATING;
            this.sourceSchedule.handle();
         } else {
            if (this.positionAdvance != null && (!this.positionAdvance.getCustomGoalProcess().isActive() || this.timerRender.update(1500L))) {
               this.positionAdvance.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.windowConvert, 2));
               this.timerRender.handle();
            }

            if (this.scaleSave.update(15000L)) {
               this.process("§c[AppleFarmer] §fНе удалось дойти до сундука «" + this.resolve(this.providerClose) + "»");
            }
         }
      } else {
         this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
      }
   }

   private void matchVector() {
      if (this.windowConvert == null) {
         this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
      } else {
         if (this.update(this.windowConvert)) {
            this.presetSave = AppleFarmer.SecondaryMode.OPENING;
            this.sourceSchedule.handle();
         }
      }
   }

   private void projectItem() {
      if (this.sourceSchedule.update(200L)) {
         this.resolve(this.windowConvert);
         this.presetSave = AppleFarmer.SecondaryMode.WAIT_GUI;
         this.sourceSchedule.handle();
      }
   }

   private void computeResponse() {
      if (!(Module.client.currentScreen instanceof GenericContainerScreen)) {
         if (this.sourceSchedule.update(2500L)) {
            this.animationSchedule++;
            if (this.animationSchedule > 3) {
               this.process("§c[AppleFarmer] §fНе удалось открыть сундук «" + this.resolve(this.providerClose) + "»");
            } else {
               this.presetSave = AppleFarmer.SecondaryMode.ROTATING;
               this.sourceSchedule.handle();
            }
         }
      }
   }

   private void handle(GenericContainerScreen var1) {
      GenericContainerScreenHandler var2 = (GenericContainerScreenHandler)var1.getScreenHandler();
      int var3 = var2.slots.size() - 36;
      if (var3 <= 0) {
         this.handle("§c[AppleFarmer] §fСундук пуст");
      } else if (this.colorCompute.update(120L)) {
         if (this.handle(this.providerClose)) {
            this.fetchProvider();
         } else {
            int var4 = this.handle(var2, var3, this.providerClose);
            if (var4 == -1) {
               if (this.colorMeasure) {
                  this.fetchProvider();
               } else {
                  this.handle("§c[AppleFarmer] §fВ сундуке «" + this.resolve(this.providerClose) + "» нет нужных предметов");
               }
            } else {
               Module.client.interactionManager.clickSlot(var2.syncId, var4, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.colorMeasure = true;
               this.colorCompute.handle();
            }
         }
      }
   }

   private void process(GenericContainerScreen var1) {
      GenericContainerScreenHandler var2 = (GenericContainerScreenHandler)var1.getScreenHandler();
      int var3 = var2.slots.size() - 36;
      if (var3 <= 0) {
         this.handle("§c[AppleFarmer] §fСундук пуст");
      } else if (this.colorCompute.update(120L)) {
         for (int var4 = var3; var4 < var2.slots.size(); var4++) {
            ItemStack var5 = ((Slot)var2.slots.get(var4)).getStack();
            if (this.handle(var5) && this.handle(var2, var3, var5)) {
               Module.client.interactionManager.clickSlot(var2.syncId, var4, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.colorMeasure = true;
               this.colorCompute.handle();
               return;
            }
         }

         if (!this.colorMeasure) {
            this.actionRead = true;
            this.textureRun.handle();
            ChatLogger.handle("§c[AppleFarmer] §fСундук «яблоки» переполнен — некуда разгружать");
         }

         if (Module.client.player != null) {
            Module.client.player.closeHandledScreen();
         }

         this.collectModule();
      }
   }

   private boolean handle(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         Item var2 = var1.getItem();
         if (var2 == Items.OAK_LOG || var2 == Items.APPLE || var2 == Items.STICK) {
            return true;
         } else {
            return var2 == Items.OAK_SAPLING ? this.handle(Items.OAK_SAPLING) > 64 : false;
         }
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

   private boolean handle(AppleFarmer.PrimaryMode var1) {
      return switch (var1) {
         case REPAIR -> this.handle(Items.EXPERIENCE_BOTTLE) >= 64;
         case BONEMEAL -> this.writePreset() >= 128;
         case SAPLING -> this.handle(Items.OAK_SAPLING) >= 64;
         default -> true;
      };
   }

   private int handle(GenericContainerScreenHandler var1, int var2, AppleFarmer.PrimaryMode var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         ItemStack var5 = ((Slot)var1.slots.get(var4)).getStack();
         if (!var5.isEmpty() && this.handle(var5.getItem(), var3)) {
            return var4;
         }
      }

      return -1;
   }

   private boolean handle(Item var1, AppleFarmer.PrimaryMode var2) {
      return switch (var2) {
         case REPAIR -> var1 == Items.EXPERIENCE_BOTTLE;
         case BONEMEAL -> var1 == Items.BONE_MEAL || var1 == Items.BONE || var1 == Items.BONE_BLOCK;
         case SAPLING -> var1 == Items.OAK_SAPLING;
         default -> false;
      };
   }

   private void fetchProvider() {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      AppleFarmer.SecondaryMode var1 = this.process(this.providerClose);
      this.presetSave = var1;
      if (var1 == AppleFarmer.SecondaryMode.RETURNING) {
         this.timerRender.handle();
         this.scaleSave.handle();
      }

      this.sourceSchedule.handle();
      this.colorCompute.handle();
      this.indexBind.clear();
   }

   private AppleFarmer.SecondaryMode process(AppleFarmer.PrimaryMode var1) {
      return switch (var1) {
         case REPAIR -> AppleFarmer.SecondaryMode.REPAIRING;
         case BONEMEAL -> AppleFarmer.SecondaryMode.CRAFTING;
         default -> AppleFarmer.SecondaryMode.RETURNING;
      };
   }

   private void handle(String var1) {
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      this.process(var1);
   }

   private void drawProfile() {
      if (Module.client.currentScreen == null) {
         if (!this.indexBind.isEmpty()) {
            if (this.colorCompute.update(90L)) {
               this.indexBind.poll().run();
               this.colorCompute.handle();
            }
         } else if (this.handle(Items.BONE_MEAL) >= 128) {
            this.collectModule();
         } else {
            int var1 = this.scheduleAnimation();
            if (var1 == -1) {
               this.collectModule();
            } else {
               int var2 = Module.client.player.playerScreenHandler.syncId;
               this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var2, var1, 0, SlotActionType.PICKUP, Module.client.player));
               this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var2, 1, 0, SlotActionType.PICKUP, Module.client.player));
               this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var2, 0, 0, SlotActionType.QUICK_MOVE, Module.client.player));
               this.indexBind.add(this::performVector);
            }
         }
      }
   }

   private void performVector() {
      int var1 = Module.client.player.playerScreenHandler.syncId;

      for (int var2 = 1; var2 <= 4; var2++) {
         if (((Slot)Module.client.player.playerScreenHandler.slots.get(var2)).hasStack()) {
            Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.QUICK_MOVE, Module.client.player);
         }
      }

      if (!Module.client.player.playerScreenHandler.getCursorStack().isEmpty()) {
         int var3 = this.scanRenderer();
         if (var3 != -1) {
            Module.client.interactionManager.clickSlot(var1, var3, 0, SlotActionType.PICKUP, Module.client.player);
         }
      }
   }

   private void attachEvent() {
      if (Module.client.currentScreen == null) {
         int var1 = Module.client.player.playerScreenHandler.syncId;
         if (!this.rendererScan) {
            int var4 = this.convertWindow();
            if (var4 == -1) {
               this.collectModule();
            } else if (this.handle(Items.EXPERIENCE_BOTTLE) == 0) {
               this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
            } else if (!Module.client.player.getOffHandStack().isEmpty()) {
               int var5 = this.scanRenderer();
               if (var5 == -1) {
                  this.process("§c[AppleFarmer] §fОсвободите офф-хенд или место в инвентаре для починки");
               } else {
                  Module.client.interactionManager.clickSlot(var1, 45, 0, SlotActionType.PICKUP, Module.client.player);
                  Module.client.interactionManager.clickSlot(var1, var5, 0, SlotActionType.PICKUP, Module.client.player);
               }
            } else {
               this.sourceBuild = var4;
               this.profileInvoke = Module.client.player.getPitch();
               Module.client.player.getInventory().setSelectedSlot(var4);
               Module.client.interactionManager.clickSlot(var1, 45, var4, SlotActionType.SWAP, Module.client.player);
               if (!this.readServer()) {
                  Module.client.interactionManager.clickSlot(var1, 45, var4, SlotActionType.SWAP, Module.client.player);
                  Module.client.player.getInventory().setSelectedSlot(var4);
                  this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
               } else {
                  this.rendererScan = true;
                  this.outputCollapse = -1;
                  this.scaleAdapt.handle();
                  this.sourceSchedule.handle();
               }
            }
         } else {
            ItemStack var2 = Module.client.player.getOffHandStack();
            if (!var2.isEmpty() && var2.isDamageable() && var2.getDamage() != 0) {
               if (Module.client.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE && !this.readServer()) {
                  this.handle(var1);
                  this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
               } else {
                  int var3 = var2.getDamage();
                  if (this.outputCollapse == -1) {
                     this.outputCollapse = var3;
                  }

                  if (var3 < this.outputCollapse) {
                     this.outputCollapse = var3;
                     this.scaleAdapt.handle();
                  } else if (this.scaleAdapt.update(4000L)) {
                     this.handle(var1);
                     this.process("§c[AppleFarmer] §fИнструмент не чинится (нет «Починки»?)");
                     return;
                  }

                  if (this.sourceSchedule.update(120L)) {
                     Module.client.player.setPitch(90.0F);
                     Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
                     Module.client.player.swingHand(Hand.MAIN_HAND);
                     this.sourceSchedule.handle();
                  }
               }
            } else {
               this.handle(var1);
            }
         }
      }
   }

   private void handle(int var1) {
      Module.client.interactionManager.clickSlot(var1, 45, this.sourceBuild, SlotActionType.SWAP, Module.client.player);
      if (this.sourceBuild >= 0) {
         Module.client.player.getInventory().setSelectedSlot(this.sourceBuild);
      }

      Module.client.player.setPitch(this.profileInvoke);
      if (!Module.client.player.getOffHandStack().isEmpty()) {
         int var2 = this.scanRenderer();
         if (var2 != -1) {
            Module.client.interactionManager.clickSlot(var1, 45, 0, SlotActionType.PICKUP, Module.client.player);
            Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.PICKUP, Module.client.player);
         }
      }

      this.rendererScan = false;
   }

   private boolean readServer() {
      int var1 = this.buildSource();
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

   private void advancePosition() {
      if (this.presetWrite && this.moduleCollect != null && this.positionAdvance != null) {
         if (!Module.client.player.getBlockPos().equals(this.moduleCollect) && !(Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.moduleCollect)) <= 0.7)
            )
          {
            if (!this.positionAdvance.getCustomGoalProcess().isActive() || this.timerRender.update(1500L)) {
               this.positionAdvance.getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.moduleCollect));
               this.timerRender.handle();
            }

            if (this.scaleSave.update(20000L)) {
               this.positionAdvance.getPathingBehavior().cancelEverything();
               this.presetSave = AppleFarmer.SecondaryMode.FACING;
            }
         } else {
            this.positionAdvance.getPathingBehavior().cancelEverything();
            this.presetSave = AppleFarmer.SecondaryMode.FACING;
            this.sourceSchedule.handle();
         }
      } else {
         this.presetSave = AppleFarmer.SecondaryMode.FACING;
      }
   }

   private void checkFrame() {
      if (this.frameCheck) {
         Module.client.player.setYaw(this.handle(this.profileDraw));
         Module.client.player.setPitch(0.0F);
      }

      this.closeProvider();
   }

   private float handle(Direction var1) {
      return switch (var1) {
         case SOUTH -> 0.0F;
         case WEST -> 90.0F;
         case NORTH -> 180.0F;
         case EAST -> -90.0F;
         default -> Module.client.player.getYaw();
      };
   }

   private void collectModule() {
      this.presetSave = AppleFarmer.SecondaryMode.RETURNING;
      this.timerRender.handle();
      this.scaleSave.handle();
   }

   private void closeProvider() {
      if (this.positionAdvance != null) {
         this.positionAdvance.getPathingBehavior().cancelEverything();
      }

      this.savePreset();
      this.summary = AppleFarmer.Mode.FIND_SPOT;
      this.eventAttach = 0;
      this.vectorPerform = null;
      this.vectorMatch.clear();
   }

   private void process(String var1) {
      ChatLogger.handle(var1);
      if (this.positionAdvance != null) {
         this.positionAdvance.getPathingBehavior().cancelEverything();
      }

      this.savePreset();
      this.toggle();
   }

   private void savePreset() {
      this.providerClose = AppleFarmer.PrimaryMode.NONE;
      this.presetSave = AppleFarmer.SecondaryMode.FIND_CHEST;
      this.windowConvert = null;
      this.presetWrite = false;
      this.colorMeasure = false;
      this.animationSchedule = 0;
      this.rendererScan = false;
      this.sourceBuild = -1;
      this.outputCollapse = -1;
      this.indexBind.clear();
   }

   private int convertWindow() {
      for (int var1 = 0; var1 < 9; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.isDamageable() && (var2.getItem() instanceof AxeItem || var2.getItem() instanceof HoeItem)) {
            int var3 = var2.getMaxDamage() - var2.getDamage();
            if (var3 <= (int)this.pending.compute()) {
               return var1;
            }
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

   private int writePreset() {
      return this.handle(Items.BONE_MEAL) + this.handle(Items.BONE) * 3 + this.handle(Items.BONE_BLOCK) * 9;
   }

   private boolean measureColor() {
      return this.handle(Items.BONE) > 0 || this.handle(Items.BONE_BLOCK) > 0;
   }

   private int scheduleAnimation() {
      for (int var1 = 9; var1 <= 44; var1++) {
         Item var2 = ((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).getStack().getItem();
         if (var2 == Items.BONE || var2 == Items.BONE_BLOCK) {
            return var1;
         }
      }

      return -1;
   }

   private int scanRenderer() {
      for (int var1 = 9; var1 <= 44; var1++) {
         if (!((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).hasStack()) {
            return var1;
         }
      }

      return -1;
   }

   private int buildSource() {
      for (int var1 = 9; var1 <= 44; var1++) {
         if (((Slot)Module.client.player.playerScreenHandler.slots.get(var1)).getStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            return var1;
         }
      }

      return -1;
   }

   private BlockPos compute(AppleFarmer.PrimaryMode var1) {
      if (Module.client.world != null && Module.client.player != null) {
         BlockPos var2 = this.frameCheck && this.moduleCollect != null ? this.moduleCollect : Module.client.player.getBlockPos();
         int var3 = (int)this.previous.compute();
         BlockPos var4 = null;
         double var5 = Double.MAX_VALUE;

         for (BlockPos var8 : BlockPos.iterate(var2.add(-var3, -5, -var3), var2.add(var3, 5, var3))) {
            if (this.process(var8) && this.handle(var8, var1)) {
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

   private boolean handle(BlockPos var1, AppleFarmer.PrimaryMode var2) {
      String var3 = this.handle(var1).toLowerCase(Locale.ROOT);
      if (var3.isEmpty()) {
         return false;
      }

      String var4;
      String[] var5;
      switch (var2) {
         case REPAIR:
            var4 = "опыт";
            var5 = new String[]{"кост", "яблок"};
            break;
         case BONEMEAL:
            var4 = "кост";
            var5 = new String[]{"опыт", "яблок"};
            break;
         case SAPLING:
         case UNLOAD:
            var4 = "яблок";
            var5 = new String[]{"опыт", "кост"};
            break;
         default:
            return false;
      }

      if (!var3.contains(var4)) {
         return false;
      }

      for (String var9 : var5) {
         if (var3.contains(var9)) {
            return false;
         }
      }

      return true;
   }

   private String handle(BlockPos var1) {
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

   private boolean process(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      BlockEntity var2 = Module.client.world.getBlockEntity(var1);
      return var2 instanceof ChestBlockEntity || var2 instanceof BarrelBlockEntity || var2 instanceof ShulkerBoxBlockEntity;
   }

   private boolean compute(BlockPos var1) {
      return this.onTick(var1) != null;
   }

   private String resolve(AppleFarmer.PrimaryMode var1) {
      return switch (var1) {
         case REPAIR -> "опыт";
         case BONEMEAL -> "кости";
         case SAPLING, UNLOAD -> "яблоки";
         default -> "";
      };
   }

   private void resolve(BlockPos var1) {
      Vec3d var2 = this.handle(var1, Direction.UP);
      BlockHitResult var3 = new BlockHitResult(var2, Direction.UP, var1, false);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var3);
      Module.client.player.swingHand(Hand.MAIN_HAND);
   }

   private boolean update(BlockPos var1) {
      RotationAngles var2 = this.handle(this.handle(var1, Direction.UP));
      RotationController.handle(var2, 65.0F, 65.0F, 65.0F, 65.0F, 2, 20, false);
      return new RotationAngles(Module.client.player).handle(var2) <= 6.0F;
   }

   private Vec3d handle(BlockPos var1, Direction var2) {
      return new Vec3d(var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5);
   }

   private RotationAngles apply(BlockPos var1) {
      return this.handle(new Vec3d(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5));
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
      Block var2 = Module.client.world.getBlockState(var1).getBlock();
      return var2 == Blocks.GRASS_BLOCK || var2 == Blocks.DIRT || var2 == Blocks.COARSE_DIRT || var2 == Blocks.PODZOL;
   }

   private boolean prepare(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      return var2.isAir() || var2.isReplaceable();
   }

   private boolean handle(BlockState var1) {
      return this.process(var1) || this.compute(var1);
   }

   private boolean process(BlockState var1) {
      return var1.getBlock() == Blocks.OAK_LOG;
   }

   private boolean compute(BlockState var1) {
      return var1.getBlock() == Blocks.OAK_LEAVES;
   }

   private boolean check(BlockPos var1) {
      double var2 = Math.min(this.source.compute(), 4.5F);
      return Module.client.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var1)) <= var2 * var2;
   }

   private int process(BlockPos var1, BlockPos var2) {
      boolean var3 = this.process(Module.client.world.getBlockState(var1));
      boolean var4 = this.process(Module.client.world.getBlockState(var2));
      if (var3 != var4) {
         return var3 ? 1 : -1;
      }

      if (var3) {
         return Integer.compare(var1.getY(), var2.getY());
      }

      Vec3d var5 = Module.client.player.getEyePos();
      double var6 = var5.squaredDistanceTo(Vec3d.ofCenter(var1));
      double var8 = var5.squaredDistanceTo(Vec3d.ofCenter(var2));
      return Double.compare(var6, var8);
   }

   private BlockPos collapseOutput() {
      for (BlockPos var2 : this.vectorMatch) {
         if (this.onTick(var2) != null) {
            return var2;
         }
      }

      return null;
   }

   private BlockHitResult onTick(BlockPos var1) {
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

   private void compute(boolean var1) {
      int var2 = -1;
      ItemStack var3 = Module.client.player.getMainHandStack();
      if (!var1 || !(var3.getItem() instanceof AxeItem)) {
         if (var1 || !(var3.getItem() instanceof HoeItem)) {
            for (int var4 = 0; var4 < 9; var4++) {
               ItemStack var5 = Module.client.player.getInventory().getStack(var4);
               if (!var5.isEmpty()) {
                  if (var1 && var5.getItem() instanceof AxeItem) {
                     var2 = var4;
                     break;
                  }

                  if (!var1 && var5.getItem() instanceof HoeItem) {
                     var2 = var4;
                     break;
                  }
               }
            }

            if (var2 != -1) {
               Module.client.player.getInventory().setSelectedSlot(var2);
            }
         }
      }
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

      int var4 = this.invokeProfile();
      if (var4 == -1) {
         return -1;
      }

      Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var2, var4, SlotActionType.SWAP, Module.client.player);
      return var4;
   }

   private int invokeProfile() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isEmpty()) {
            return var1;
         }
      }

      for (int var3 = 0; var3 < 9; var3++) {
         Item var2 = Module.client.player.getInventory().getStack(var3).getItem();
         if (!(var2 instanceof AxeItem)
            && !(var2 instanceof HoeItem)
            && var2 != Items.OAK_SAPLING
            && var2 != Items.BONE_MEAL
            && var2 != Items.BONE
            && var2 != Items.BONE_BLOCK
            && var2 != Items.EXPERIENCE_BOTTLE) {
            return var3;
         }
      }

      return -1;
   }

   private void scheduleSource() {
      for (int var1 = 0; var1 < 9; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         boolean var3 = var2.getItem() == Items.BONE_MEAL || var2.getItem() == Items.OAK_SAPLING;
         if (var3 && var2.getCount() < 64) {
            int var4 = -1;
            int var5 = var2.getCount();

            for (int var6 = 9; var6 < 36; var6++) {
               ItemStack var7 = Module.client.player.getInventory().getStack(var6);
               if (var7.getItem() == var2.getItem() && var7.getCount() > var5) {
                  var4 = var6;
                  var5 = var7.getCount();
                  if (var5 == 64) {
                     break;
                  }
               }
            }

            if (var4 != -1) {
               Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var4, var1, SlotActionType.SWAP, Module.client.player);
               this.serverRead = 0;
               return;
            }
         }
      }
   }

   enum Mode {
      FIND_SPOT,
      PLACE,
      BONEMEAL,
      SCAN_TREE,
      BREAKING;
   }

   enum PrimaryMode {
      NONE,
      REPAIR,
      BONEMEAL,
      SAPLING,
      UNLOAD;
   }

   enum SecondaryMode {
      FIND_CHEST,
      GOING,
      ROTATING,
      OPENING,
      WAIT_GUI,
      CRAFTING,
      REPAIRING,
      RETURNING,
      FACING;
   }
}
