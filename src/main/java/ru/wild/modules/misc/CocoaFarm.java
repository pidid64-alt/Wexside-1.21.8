package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalNear;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.render.FarmAreaRenderer;
import ru.wild.util.render.PackedColor;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "CocoaFarm", category = ModuleCategory.Misc, description = "Авто-ферма какао-бобов на тропических брёвнах")
public class CocoaFarm extends Module {
   private static BlockPos previous;
   private static BlockPos latest;
   public final BooleanSetting source = new BooleanSetting("Авто-посадка", true);
   public final BooleanSetting target = new BooleanSetting("Склад в сундук", true);
   public final BooleanSetting pending = new BooleanSetting("Логи", true);
   private static final double summary = 4.6;
   private static final double matrixBlend = 3.6;
   private static final float vectorMatch = 60.0F;
   private static final float itemProject = 5.0F;
   private static final float responseCompute = 0.5F;
   private static final float providerFetch = 4.0F;
   private static final long profileDraw = 300L;
   private static final int vectorPerform = 32;
   private static final long eventAttach = 300L;
   private static final long serverRead = 2500L;
   private static final long positionAdvance = 5000L;
   private static final long frameCheck = 4000L;
   private static final long moduleCollect = 12000L;
   private static final long providerClose = 30000L;
   private static final int presetSave = 6;
   private static final double windowConvert = 4.2;
   private static final long presetWrite = 6000L;
   private static final int colorMeasure = 3;
   private static final long animationSchedule = 30000L;
   private final Stopwatch rendererScan = new Stopwatch();
   private final Stopwatch sourceBuild = new Stopwatch();
   private final Stopwatch outputCollapse = new Stopwatch();
   private final Stopwatch profileInvoke = new Stopwatch();
   private final Stopwatch sourceSchedule = new Stopwatch();
   private final Stopwatch timerRender = new Stopwatch();
   private final List<CocoaFarm.State> scaleSave = new ArrayList<>();
   private final HashMap<BlockPos, Long> colorCompute = new HashMap<>();
   private final HashMap<Integer, Long> scaleAdapt = new HashMap<>();
   private final HashMap<BlockPos, long[]> textureRun = new HashMap<>();
   private CocoaFarm.PrimaryMode indexBind = CocoaFarm.PrimaryMode.FARM;
   private CocoaFarm.State actionRead;
   private BlockPos configCollapse;
   private BlockPos dataValidate;
   private BlockPos scaleRender;
   private BlockPos clientRefresh;
   private int keyFilter = -1;
   private int requestAdapt;
   private int timerMeasure = -1;
   private int vectorEncode;
   private long requestReceive;
   private long windowProcess;
   private double packetSave;
   private int entryAnimate;
   private boolean playerCollect;
   private int stateApply;
   private int matrixFilter = -1;
   private boolean layerSample;
   private boolean worldSend;
   private boolean targetWrite;
   private static final int[] resultEncode = new int[]{0, -1, 1, -2, 2, -3, -4};
   private static final int messageParse = 4096;
   private static final RenderPipeline providerRead = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "cocoa_zone_fill"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline matrixBlend2 = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "cocoa_zone_glow"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer scalePerform = RenderLayer.of(
      "cocoa_zone_fill", 4096, false, true, providerRead, MultiPhaseParameters.builder().build(false)
   );
   private static final RenderLayer contextExpand = RenderLayer.of(
      "cocoa_zone_glow", 4096, false, true, matrixBlend2, MultiPhaseParameters.builder().build(false)
   );
   private static final int keyProcess = -65409;
   private static final int actionConvert = -8781569;
   private static final int screenRead = 657938;
   private static final int animationExpand = 20;

   public static void refresh() {
      previous = null;
      latest = null;
   }

   public CocoaFarm() {
      this.handle(this.source, this.target);

      try {
         BaritoneAPI.getSettings().chunkCaching.value = false;
      } catch (Throwable var2) {
      }
   }

   @Override
   public void handle() {
      super.handle();
      this.indexBind = CocoaFarm.PrimaryMode.FARM;
      this.scaleSave.clear();
      this.colorCompute.clear();
      this.actionRead = null;
      this.configCollapse = null;
      this.dataValidate = null;
      this.scaleRender = null;
      this.clientRefresh = null;
      this.keyFilter = -1;
      this.requestAdapt = 0;
      this.timerMeasure = -1;
      this.vectorEncode = 0;
      this.requestReceive = 0L;
      this.windowProcess = 0L;
      this.packetSave = Double.MAX_VALUE;
      this.entryAnimate = 0;
      this.playerCollect = false;
      this.stateApply = 0;
      this.matrixFilter = -1;
      this.scaleAdapt.clear();
      this.textureRun.clear();
      this.sourceBuild.handle();
      this.rendererScan.handle();
      this.outputCollapse.handle();
      this.profileInvoke.handle();
      this.timerRender.handle();
      this.layerSample = (Boolean)BaritoneAPI.getSettings().allowBreak.value;
      this.worldSend = (Boolean)BaritoneAPI.getSettings().allowPlace.value;
      this.targetWrite = (Boolean)BaritoneAPI.getSettings().allowSprint.value;
      BaritoneAPI.getSettings().allowBreak.value = false;
      BaritoneAPI.getSettings().allowPlace.value = false;
      BaritoneAPI.getSettings().chunkCaching.value = false;
      if (previous != null && latest != null) {
         this.process("Запуск, зона " + this.select(previous) + " — " + this.select(latest));
      } else {
         ChatLogger.handle("§c[CocoaFarm] §fСначала задайте зону: §e.cocoa pos1 §fи §e.cocoa pos2");
      }
   }

   @Override
   public void process() {
      this.blendMatrix();
      BaritoneAPI.getSettings().allowBreak.value = this.layerSample;
      BaritoneAPI.getSettings().allowPlace.value = this.worldSend;
      BaritoneAPI.getSettings().allowSprint.value = this.targetWrite;
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      this.dataValidate = null;
      this.actionRead = null;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (previous != null && latest != null) {
            if (PlayerHelper.refresh()) {
               this.blendMatrix();
            } else if (this.indexBind != CocoaFarm.PrimaryMode.FARM && System.currentTimeMillis() > this.requestReceive) {
               this.process("Тайм-аут депозит-сессии, блокирую сундук");
               this.compute(true);
            } else {
               switch (this.indexBind) {
                  case FARM:
                     this.drawAnimation();
                     break;
                  case NAVIGATING:
                     this.animate();
                     break;
                  case INTERACTING:
                     this.load();
                     break;
                  case WAITING_FOR_CONTAINER:
                     this.save();
                     break;
                  case DEPOSITING:
                     this.submit();
               }
            }
         }
      }
   }

   private void drawAnimation() {
      if (this.target.compute()
         && System.currentTimeMillis() >= this.windowProcess
         && (this.checkFrame() == 0 || this.advancePosition() >= 4)
         && this.advancePosition() > 1) {
         BlockPos var1 = this.drawProfile();
         if (var1 != null) {
            this.clientRefresh = var1;
            this.indexBind = CocoaFarm.PrimaryMode.NAVIGATING;
            this.sourceSchedule.handle();
            this.outputCollapse.handle();
            this.packetSave = Double.MAX_VALUE;
            this.blendMatrix();
            this.requestAdapt = 0;
            this.vectorEncode = 0;
            this.timerMeasure = -1;
            this.requestReceive = System.currentTimeMillis() + 30000L;
            this.process("Инвентарь полон, иду к сундуку " + this.select(var1));
            return;
         }
      }

      if (this.actionRead == null || !this.resolve(this.actionRead)) {
         this.actionRead = null;
         if (this.sourceBuild.update(300L)) {
            this.fetch();
            this.sourceBuild.handle();
         }

         CocoaFarm.State var9 = this.measure();
         if (var9 != null && this.resolve(var9.data)) {
            var9 = null;
         }

         if (var9 != null && !var9.data.equals(this.configCollapse)) {
            this.configCollapse = var9.data;
            this.entryAnimate = 0;
            this.outputCollapse.handle();
            this.packetSave = Double.MAX_VALUE;
         }

         this.actionRead = var9;
      }

      if (!this.matchVector()) {
         if (this.actionRead == null) {
            if (!this.playerCollect) {
               this.process("Целей нет, жду созревания");
               this.playerCollect = true;
            }

            this.blendMatrix();
         } else {
            this.playerCollect = false;
            BlockPos var10 = this.apply(this.actionRead);
            double var2 = Module.client.player.getX() - (var10.getX() + 0.5);
            double var4 = Module.client.player.getZ() - (var10.getZ() + 0.5);
            boolean var6 = var2 * var2 + var4 * var4 <= 1.44;
            double var7 = Module.client.player.getEyePos().distanceTo(this.update(this.actionRead));
            if (var7 > 4.6 || !var6 && var7 > 3.6) {
               if (!this.handle(var7, var10)) {
                  this.handle("не могу дойти до " + this.select(this.actionRead.data) + " (дист " + Math.round(var7 * 10.0) / 10.0 + ")");
               }
            } else {
               if (this.actionRead.instance == CocoaFarm.Mode.HARVEST) {
                  this.handle(this.actionRead);
               } else {
                  this.process(this.actionRead);
               }
            }
         }
      }
   }

   private boolean handle(double var1, BlockPos var3) {
      if (var1 < this.packetSave - 0.4) {
         this.packetSave = var1;
         this.outputCollapse.handle();
      }

      if (this.outputCollapse.update(5000L)) {
         return false;
      }

      this.execute(var3);
      return true;
   }

   private void handle(String var1) {
      this.process("Пропуск: " + var1);
      this.colorCompute.put(this.actionRead.data, System.currentTimeMillis() + 12000L);
      this.actionRead = null;
      this.dataValidate = null;
      this.blendMatrix();
   }

   private void compute(BlockPos var1) {
      Module.client.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, var1, Direction.DOWN));
   }

   private boolean resolve(BlockPos var1) {
      long var2 = System.currentTimeMillis();
      long[] var4 = this.textureRun.get(var1);
      if (var4 != null && var2 - var4[1] <= 6000L) {
         var4[0]++;
         var4[1] = var2;
         if (var4[0] >= 3L) {
            this.textureRun.remove(var1);
            this.colorCompute.put(var1, var2 + 30000L);
            this.compute(var1);
            this.process("Фантомный блок " + this.select(var1) + ", ресинк и пропуск");
            return true;
         } else {
            return false;
         }
      } else {
         this.textureRun.put(var1.toImmutable(), new long[]{1L, var2});
         if (this.textureRun.size() > 128) {
            this.textureRun.entrySet().removeIf(var2x -> var2 - var2x.getValue()[1] > 6000L);
         }

         return false;
      }
   }

   private void handle(CocoaFarm.State var1) {
      BlockHitResult var2 = this.execute(var1);
      if (var2 != null && !(Module.client.player.getEyePos().distanceTo(var2.getPos()) > 4.2)) {
         this.outputCollapse.handle();
         this.blendMatrix();
         RotationAngles var3 = this.handle(var2.getPos());
         this.handle(var3);
         if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
            if (!this.performVector()) {
               BlockHitResult var4 = this.collectModule();
               BlockHitResult var5 = var4 != null && var4.getBlockPos().equals(var1.data) ? var4 : var2;
               if (!var1.data.equals(this.dataValidate)) {
                  if (!this.rendererScan.update(300L)) {
                     return;
                  }

                  Module.client.interactionManager.attackBlock(var1.data, var5.getSide());
                  this.dataValidate = var1.data;
                  this.profileInvoke.handle();
                  this.rendererScan.handle();
               } else {
                  if (this.profileInvoke.update(4000L)) {
                     this.compute(var1.data);
                     this.handle("какао " + this.select(var1.data) + " не ломается, ресинк фантома");
                     return;
                  }

                  Module.client.interactionManager.updateBlockBreakingProgress(var1.data, var5.getSide());
               }

               Module.client.player.swingHand(Hand.MAIN_HAND);
            }
         }
      } else {
         this.compute(var1);
      }
   }

   private void process(CocoaFarm.State var1) {
      if (!this.readServer()) {
         this.process("Бобы закончились, посадка недоступна");
         this.actionRead = null;
      } else if (!this.attachEvent()) {
         if (Module.client.player.getMainHandStack().isOf(Items.COCOA_BEANS)) {
            if (this.entryAnimate >= 6) {
               this.compute(var1.data);
               this.compute(var1.data.offset(var1.context));
               this.handle("посадка " + this.select(var1.data.offset(var1.context)) + " не проходит, ресинк фантома");
            } else {
               BlockHitResult var2 = this.handle(var1.data, var1.context);
               if (var2 != null && !(Module.client.player.getEyePos().distanceTo(var2.getPos()) > 4.2)) {
                  this.outputCollapse.handle();
                  this.blendMatrix();
                  RotationAngles var3 = this.handle(var2.getPos());
                  this.handle(var3);
                  if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
                     if (this.rendererScan.update(300L)) {
                        BlockHitResult var4 = this.collectModule();
                        BlockHitResult var5 = var4 != null && var4.getBlockPos().equals(var1.data) && var4.getSide() == var1.context ? var4 : var2;
                        Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var5);
                        Module.client.player.swingHand(Hand.MAIN_HAND);
                        this.entryAnimate++;
                        this.rendererScan.handle();
                        this.dataValidate = null;
                     }
                  }
               } else {
                  this.compute(var1);
               }
            }
         }
      }
   }

   private void compute(CocoaFarm.State var1) {
      BlockPos var2 = this.apply(var1);
      double var3 = Module.client.player.getX() - (var2.getX() + 0.5);
      double var5 = Module.client.player.getZ() - (var2.getZ() + 0.5);
      double var7 = var3 * var3 + var5 * var5;
      if (var7 > 2.5) {
         if (!this.handle(Math.sqrt(var7), var2)) {
            this.handle("не могу подойти к " + this.select(var1.data));
         }
      } else {
         if (this.outputCollapse.update(2500L)) {
            this.handle("нет прямой видимости " + this.select(var1.data));
         }
      }
   }

   private boolean encodePoint() {
      return this.clientRefresh != null && this.handle(Module.client.world.getBlockState(this.clientRefresh));
   }

   private void compute(boolean var1) {
      if (var1) {
         this.windowProcess = System.currentTimeMillis() + 30000L;
      }

      this.clientRefresh = null;
      this.keyFilter = -1;
      this.blendMatrix();
      this.indexBind = CocoaFarm.PrimaryMode.FARM;
   }

   private void animate() {
      if (!this.encodePoint()) {
         this.compute(false);
      } else if (this.sourceSchedule.update(15000L)) {
         this.process("Не смог дойти до сундука, вернусь позже");
         this.compute(true);
      } else if (Module.client.player.getEyePos().distanceTo(Vec3d.ofCenter(this.clientRefresh)) <= 4.5) {
         this.blendMatrix();
         this.rendererScan.handle();
         this.indexBind = CocoaFarm.PrimaryMode.INTERACTING;
      } else {
         this.execute(this.clientRefresh);
      }
   }

   private void load() {
      if (!this.encodePoint()) {
         this.compute(false);
      } else if (this.requestAdapt >= 3) {
         this.process("Сундук не открывается, блокирую");
         this.compute(true);
      } else {
         this.blendMatrix();
         if (Module.client.player.getEyePos().distanceTo(Vec3d.ofCenter(this.clientRefresh)) > 4.6) {
            this.sourceSchedule.handle();
            this.indexBind = CocoaFarm.PrimaryMode.NAVIGATING;
         } else {
            BlockHitResult var1 = this.onTick(this.clientRefresh);
            Vec3d var2 = var1 != null ? var1.getPos() : Vec3d.ofCenter(this.clientRefresh);
            RotationAngles var3 = this.handle(var2);
            this.handle(var3);
            if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
               if (this.rendererScan.update(300L)) {
                  BlockHitResult var4 = var1 != null ? var1 : new BlockHitResult(var2, Direction.UP, this.clientRefresh, false);
                  Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var4);
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  this.requestAdapt++;
                  this.keyFilter = -1;
                  this.sourceSchedule.handle();
                  this.rendererScan.handle();
                  this.indexBind = CocoaFarm.PrimaryMode.WAITING_FOR_CONTAINER;
               }
            }
         }
      }
   }

   private void save() {
      this.blendMatrix();
      if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
         int var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId;
         if (Module.client.player.currentScreenHandler != null && Module.client.player.currentScreenHandler.syncId == var3) {
            this.keyFilter = var3;
            this.timerMeasure = -1;
            this.vectorEncode = 0;
            this.sourceSchedule.handle();
            this.indexBind = CocoaFarm.PrimaryMode.DEPOSITING;
            return;
         }
      }

      if (this.sourceSchedule.update(4000L)) {
         this.process("Сундук не ответил открытием, повтор подхода");
         this.sourceSchedule.handle();
         this.indexBind = CocoaFarm.PrimaryMode.NAVIGATING;
      }
   }

   private void submit() {
      if (!(
         Module.client.currentScreen instanceof GenericContainerScreen var1
            && Module.client.player.currentScreenHandler != null
            && Module.client.player.currentScreenHandler.syncId == this.keyFilter
            && ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId == this.keyFilter
      )) {
         this.compute(false);
      } else if (this.sourceSchedule.update(50L)) {
         int var6 = this.unload();
         if (this.timerMeasure >= 0 && var6 >= this.timerMeasure) {
            this.vectorEncode++;
         } else {
            this.vectorEncode = 0;
         }

         this.timerMeasure = var6;
         if (this.vectorEncode >= 3) {
            ChatLogger.handle("§c[CocoaFarm] §fСундук заполнен, освободите место");
            Module.client.player.closeHandledScreen();
            this.compute(true);
         } else {
            GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
            int var4 = var3.getRows() * 9;
            int var5 = this.handle(var3, var4);
            if (var5 == -1) {
               Module.client.player.closeHandledScreen();
               this.process("Депозит завершён");
               this.compute(false);
            } else {
               Module.client.interactionManager.clickSlot(this.keyFilter, var5, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.sourceSchedule.handle();
            }
         }
      }
   }

   private int unload() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.isOf(Items.COCOA_BEANS)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private int handle(GenericContainerScreenHandler var1, int var2) {
      boolean var3 = this.source.compute();
      boolean var4 = false;

      for (int var5 = var2; var5 < var1.slots.size(); var5++) {
         Slot var6 = var1.getSlot(var5);
         if (var6.hasStack() && var6.getStack().isOf(Items.COCOA_BEANS)) {
            if (!var3 || var4) {
               return var5;
            }

            var4 = true;
         }
      }

      return -1;
   }

   private void fetch() {
      this.scaleSave.clear();
      boolean var1 = this.source.compute() && this.readServer();
      int[] var2 = this.fetchProvider();
      int var3 = 0;
      int var4 = 0;

      for (BlockPos var6 : BlockPos.iterate(var2[0], var2[1], var2[2], var2[3], var2[4], var2[5])) {
         BlockState var7 = Module.client.world.getBlockState(var6);
         if (var7.isIn(BlockTags.JUNGLE_LOGS)) {
            BlockPos var8 = var6.toImmutable();

            for (Direction var10 : Type.HORIZONTAL) {
               BlockPos var11 = var8.offset(var10);
               if (!this.check(var11) && !this.check(var8)) {
                  BlockState var12 = Module.client.world.getBlockState(var11);
                  if (var12.isOf(Blocks.COCOA)) {
                     if ((Integer)var12.get(CocoaBlock.AGE) >= 2) {
                        this.scaleSave.add(new CocoaFarm.State(CocoaFarm.Mode.HARVEST, var11, var10));
                        var3++;
                     }
                  } else if (var1 && (var12.isAir() || var12.isReplaceable())) {
                     this.scaleSave.add(new CocoaFarm.State(CocoaFarm.Mode.PLANT, var8, var10));
                     var4++;
                  }
               }
            }
         }
      }

      int var13 = var3 + var4;
      if (var13 > 0 && this.stateApply == 0) {
         this.process("Найдено целей: сбор " + var3 + ", посадка " + var4);
      }

      this.stateApply = var13;
   }

   private CocoaFarm.State measure() {
      Vec3d var1 = Module.client.player.getEyePos();
      CocoaFarm.State var2 = null;
      double var3 = Double.MAX_VALUE;

      for (CocoaFarm.State var6 : this.scaleSave) {
         if (this.resolve(var6) && !this.check(var6.data)) {
            double var7 = var1.squaredDistanceTo(this.update(var6));
            if (var6.instance == CocoaFarm.Mode.PLANT) {
               var7 += 0.001;
            }

            if (var7 < var3) {
               var3 = var7;
               var2 = var6;
            }
         }
      }

      return var2;
   }

   private boolean resolve(CocoaFarm.State var1) {
      if (var1 == null) {
         return false;
      }

      if (var1.instance == CocoaFarm.Mode.HARVEST) {
         BlockState var4 = Module.client.world.getBlockState(var1.data);
         return var4.isOf(Blocks.COCOA) && (Integer)var4.get(CocoaBlock.AGE) >= 2;
      }

      if (!this.readServer()) {
         return false;
      }

      BlockState var2 = Module.client.world.getBlockState(var1.data);
      if (!var2.isIn(BlockTags.JUNGLE_LOGS)) {
         return false;
      }

      BlockState var3 = Module.client.world.getBlockState(var1.data.offset(var1.context));
      return var3.isAir() || var3.isReplaceable();
   }

   private Vec3d update(CocoaFarm.State var1) {
      return var1.instance == CocoaFarm.Mode.HARVEST ? Vec3d.ofCenter(var1.data) : this.compute(var1.data, var1.context);
   }

   private BlockPos apply(CocoaFarm.State var1) {
      BlockPos var2 = var1.instance == CocoaFarm.Mode.HARVEST ? var1.data : var1.data.offset(var1.context);
      BlockPos var3 = var1.instance == CocoaFarm.Mode.HARVEST ? var1.data.offset(var1.context) : var1.data.offset(var1.context, 2);
      int var4 = Module.client.player.getBlockPos().getY();
      BlockPos[] var5 = new BlockPos[]{var2, var3};

      for (BlockPos var9 : var5) {
         for (int var13 : resultEncode) {
            BlockPos var14 = new BlockPos(var9.getX(), var4 + var13, var9.getZ());
            if (this.update(var14) && this.apply(var14)) {
               return var14;
            }
         }
      }

      return new BlockPos(var2.getX(), var4, var2.getZ());
   }

   private boolean update(BlockPos var1) {
      int var2 = Math.min(previous.getX(), latest.getX());
      int var3 = Math.max(previous.getX(), latest.getX());
      int var4 = Math.min(previous.getY(), latest.getY());
      int var5 = Math.max(previous.getY(), latest.getY());
      int var6 = Math.min(previous.getZ(), latest.getZ());
      int var7 = Math.max(previous.getZ(), latest.getZ());
      return var1.getX() >= var2 && var1.getX() <= var3 && var1.getY() >= var4 && var1.getY() <= var5 && var1.getZ() >= var6 && var1.getZ() <= var7;
   }

   private boolean apply(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      BlockState var3 = Module.client.world.getBlockState(var1.up());
      BlockState var4 = Module.client.world.getBlockState(var1.down());
      boolean var5 = var2.isAir() || var2.getCollisionShape(Module.client.world, var1).isEmpty();
      boolean var6 = var3.isAir() || var3.getCollisionShape(Module.client.world, var1.up()).isEmpty();
      boolean var7 = !var4.isAir() && !var4.isOf(Blocks.COCOA) && !var4.getCollisionShape(Module.client.world, var1.down()).isEmpty();
      return var5 && var6 && var7;
   }

   private void execute(BlockPos var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
      boolean var3 = !var1.equals(this.scaleRender);
      if (var3 || !var2.getCustomGoalProcess().isActive()) {
         var2.getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, 1));
         if (var3) {
            this.process("Иду к " + this.select(var1));
         }

         this.scaleRender = var1;
      }
   }

   private void blendMatrix() {
      IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
      if (var1.getCustomGoalProcess().isActive()) {
         var1.getPathingBehavior().cancelEverything();
      }

      this.scaleRender = null;
   }

   private boolean matchVector() {
      if (!this.computeResponse()) {
         return false;
      }

      ItemEntity var1 = this.projectItem();
      if (var1 == null) {
         this.matrixFilter = -1;
         return false;
      }

      double var2 = Module.client.player.getX() - var1.getX();
      double var4 = Module.client.player.getZ() - var1.getZ();
      double var6 = var2 * var2 + var4 * var4;
      boolean var8 = this.actionRead == null;
      if (var6 > 36.0 && !var8) {
         return false;
      }

      if (var6 <= 1.7) {
         this.matrixFilter = -1;
         return false;
      }

      if (var1.getId() != this.matrixFilter) {
         this.matrixFilter = var1.getId();
         this.timerRender.handle();
      }

      if (this.timerRender.update(8000L)) {
         this.scaleAdapt.put(var1.getId(), System.currentTimeMillis() + 45000L);
         this.matrixFilter = -1;
         return false;
      } else {
         this.prepare(this.handle(var1));
         return true;
      }
   }

   private BlockPos handle(ItemEntity var1) {
      BlockPos var2 = BlockPos.ofFloored(var1.getX(), var1.getY() + 0.1, var1.getZ());
      int var3 = Module.client.player.getBlockPos().getY();

      for (int var7 : resultEncode) {
         BlockPos var8 = new BlockPos(var2.getX(), var3 + var7, var2.getZ());
         if (this.update(var8) && this.apply(var8)) {
            return var8;
         }
      }

      return new BlockPos(var2.getX(), var3, var2.getZ());
   }

   private ItemEntity projectItem() {
      Box var1 = Box.enclosing(previous, latest).expand(1.0);
      List<ItemEntity> var2 = Module.client.world.getEntitiesByClass(ItemEntity.class, var1, var0 -> var0.isAlive() && var0.getStack().isOf(Items.COCOA_BEANS));
      ItemEntity var3 = null;
      double var4 = Double.MAX_VALUE;
      long var6 = System.currentTimeMillis();

      for (ItemEntity var9 : var2) {
         Long var10 = this.scaleAdapt.get(var9.getId());
         if (var10 != null) {
            if (var6 <= var10) {
               continue;
            }

            this.scaleAdapt.remove(var9.getId());
         }

         double var11 = Module.client.player.squaredDistanceTo(var9);
         if (var11 < var4) {
            var4 = var11;
            var3 = var9;
         }
      }

      return var3;
   }

   private boolean computeResponse() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (var2.isEmpty()) {
            return true;
         }

         if (var2.isOf(Items.COCOA_BEANS) && var2.getCount() < var2.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private void prepare(BlockPos var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
      boolean var3 = !var1.equals(this.scaleRender);
      if (var3 || !var2.getCustomGoalProcess().isActive()) {
         var2.getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, 1));
         if (var3) {
            this.process("Подбираю лут " + this.select(var1));
         }

         this.scaleRender = var1;
      }
   }

   private int[] fetchProvider() {
      int var1 = Math.min(previous.getX(), latest.getX());
      int var2 = Math.min(previous.getY(), latest.getY());
      int var3 = Math.min(previous.getZ(), latest.getZ());
      int var4 = Math.max(previous.getX(), latest.getX());
      int var5 = Math.max(previous.getY(), latest.getY());
      int var6 = Math.max(previous.getZ(), latest.getZ());
      BlockPos var7 = Module.client.player.getBlockPos();
      var1 = Math.max(var1, var7.getX() - 32);
      var3 = Math.max(var3, var7.getZ() - 32);
      var4 = Math.min(var4, var7.getX() + 32);
      var6 = Math.min(var6, var7.getZ() + 32);
      return new int[]{var1, var2, var3, var4, var5, var6};
   }

   private BlockPos drawProfile() {
      int[] var1 = this.fetchProvider();
      Vec3d var2 = Module.client.player.getEyePos();
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;

      for (BlockPos var7 : BlockPos.iterate(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5])) {
         if (this.handle(Module.client.world.getBlockState(var7))) {
            double var8 = var2.squaredDistanceTo(Vec3d.ofCenter(var7));
            if (var8 < var4) {
               var4 = var8;
               var3 = var7.toImmutable();
            }
         }
      }

      return var3;
   }

   private boolean handle(BlockState var1) {
      return var1.isOf(Blocks.CHEST) || var1.isOf(Blocks.TRAPPED_CHEST) || var1.isOf(Blocks.BARREL);
   }

   private boolean check(BlockPos var1) {
      Long var2 = this.colorCompute.get(var1);
      if (var2 == null) {
         return false;
      } else if (System.currentTimeMillis() > var2) {
         this.colorCompute.remove(var1);
         return false;
      } else {
         return true;
      }
   }

   private int handle(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         ItemEnchantmentsComponent var2 = (ItemEnchantmentsComponent)var1.get(DataComponentTypes.ENCHANTMENTS);
         if (var2 != null && !var2.isEmpty()) {
            for (Entry var4 : var2.getEnchantmentEntries()) {
               if (((RegistryEntry)var4.getKey()).matchesKey(Enchantments.FORTUNE)) {
                  return var4.getIntValue();
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private boolean performVector() {
      if (this.handle(Module.client.player.getMainHandStack()) > 0) {
         return false;
      }

      int var1 = -1;
      int var2 = 0;

      for (int var3 = 0; var3 < 9; var3++) {
         int var4 = this.handle(Module.client.player.getInventory().getStack(var3));
         if (var4 > var2) {
            var2 = var4;
            var1 = var3;
         }
      }

      if (var1 != -1) {
         Module.client.player.getInventory().setSelectedSlot(var1);
         return false;
      }

      for (int var5 = 9; var5 < 36; var5++) {
         int var6 = this.handle(Module.client.player.getInventory().getStack(var5));
         if (var6 > var2) {
            var2 = var6;
            var1 = var5;
         }
      }

      if (var1 != -1) {
         Module.client.interactionManager
            .clickSlot(
               Module.client.player.playerScreenHandler.syncId, var1, Module.client.player.getInventory().getSelectedSlot(), SlotActionType.SWAP, Module.client.player
            );
         return true;
      } else {
         return false;
      }
   }

   private boolean attachEvent() {
      if (Module.client.player.getMainHandStack().isOf(Items.COCOA_BEANS)) {
         return false;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isOf(Items.COCOA_BEANS)) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            return false;
         }
      }

      for (int var2 = 9; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isOf(Items.COCOA_BEANS)) {
            Module.client.interactionManager
               .clickSlot(
                  Module.client.player.playerScreenHandler.syncId,
                  var2,
                  Module.client.player.getInventory().getSelectedSlot(),
                  SlotActionType.SWAP,
                  Module.client.player
               );
            return true;
         }
      }

      return false;
   }

   private boolean readServer() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isOf(Items.COCOA_BEANS)) {
            return true;
         }
      }

      return false;
   }

   private int advancePosition() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isOf(Items.COCOA_BEANS)) {
            var1++;
         }
      }

      return var1;
   }

   private int checkFrame() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private BlockHitResult execute(CocoaFarm.State var1) {
      BlockHitResult var2 = this.onTick(var1.data);
      if (var2 != null) {
         return var2;
      }

      Vec3d var3 = Module.client.player.getEyePos();
      Vec3d var4 = Vec3d.ofCenter(var1.data);
      return var3.distanceTo(var4) <= 4.6 && this.handle(var3, var4, var1.data) ? new BlockHitResult(var4, var1.context, var1.data, false) : null;
   }

   private BlockHitResult handle(BlockPos var1, Direction var2) {
      BlockHitResult var3 = this.process(var1, var2);
      if (var3 != null) {
         return var3;
      }

      Vec3d var4 = Module.client.player.getEyePos();
      Vec3d var5 = Vec3d.of(var2.getVector());
      if (var4.subtract(this.compute(var1, var2)).dotProduct(var5) <= 0.05) {
         return null;
      }

      double[] var6 = new double[]{0.5, 0.3, 0.7};

      for (double var10 : var6) {
         for (double var15 : var6) {
            Vec3d var17 = this.handle(var1, var2, var10, var15);
            if (var4.distanceTo(var17) <= 4.6 && this.handle(var4, var17, var1)) {
               return new BlockHitResult(var17, var2, var1, false);
            }
         }
      }

      return null;
   }

   private boolean handle(Vec3d var1, Vec3d var2, BlockPos var3) {
      Vec3d var4 = var2.subtract(var1);
      double var5 = var4.length();
      if (var5 < 1.0E-6) {
         return true;
      }

      var4 = var4.multiply(1.0 / var5);

      for (double var7 = 0.25; var7 < var5 - 0.05; var7 += 0.25) {
         Vec3d var9 = var1.add(var4.multiply(var7));
         BlockPos var10 = BlockPos.ofFloored(var9.x, var9.y, var9.z);
         if (!var10.equals(var3)) {
            BlockState var11 = Module.client.world.getBlockState(var10);
            if (!var11.isOf(Blocks.COCOA) && !var11.isAir() && !var11.getCollisionShape(Module.client.world, var10).isEmpty()) {
               return false;
            }
         }
      }

      return true;
   }

   private BlockHitResult onTick(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double[] var3 = new double[]{0.5, 0.2, 0.8};

      for (double var7 : var3) {
         for (double var12 : var3) {
            for (double var17 : var3) {
               Vec3d var19 = new Vec3d(var1.getX() + var7, var1.getY() + var12, var1.getZ() + var17);
               BlockHitResult var20 = Module.client.world.raycast(new RaycastContext(var2, var19, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
               if (var20.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK && var20.getBlockPos().equals(var1)) {
                  return var20;
               }
            }
         }
      }

      return null;
   }

   private BlockHitResult process(BlockPos var1, Direction var2) {
      Vec3d var3 = Module.client.player.getEyePos();
      double[] var4 = new double[]{0.5, 0.3, 0.7};

      for (double var8 : var4) {
         for (double var13 : var4) {
            Vec3d var15 = this.handle(var1, var2, var8, var13);
            BlockHitResult var16 = Module.client.world.raycast(new RaycastContext(var3, var15, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
            if (var16.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK && var16.getBlockPos().equals(var1) && var16.getSide() == var2) {
               return var16;
            }
         }
      }

      return null;
   }

   private Vec3d handle(BlockPos var1, Direction var2, double var3, double var5) {
      double var7 = var1.getX();
      double var9 = var1.getY();
      double var11 = var1.getZ();

      return switch (var2) {
         case NORTH -> new Vec3d(var7 + var3, var9 + var5, var11);
         case SOUTH -> new Vec3d(var7 + var3, var9 + var5, var11 + 1.0);
         case WEST -> new Vec3d(var7, var9 + var3, var11 + var5);
         case EAST -> new Vec3d(var7 + 1.0, var9 + var3, var11 + var5);
         default -> Vec3d.ofCenter(var1);
      };
   }

   private Vec3d compute(BlockPos var1, Direction var2) {
      return new Vec3d(var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5);
   }

   private void handle(RotationAngles var1) {
      float var2 = new RotationAngles(Module.client.player).handle(var1);
      float var3 = Math.max(5.0F, Math.min(60.0F, var2 * 0.5F));
      RotationController.handle(var1, var3, var3, var3, var3, 2, 20, false);
   }

   private BlockHitResult collectModule() {
      double var1 = Math.toRadians(Module.client.player.getYaw());
      double var3 = Math.toRadians(Module.client.player.getPitch());
      double var5 = Math.cos(var3);
      Vec3d var7 = new Vec3d(-Math.sin(var1) * var5, -Math.sin(var3), Math.cos(var1) * var5);
      Vec3d var8 = Module.client.player.getEyePos();
      Vec3d var9 = var8.add(var7.multiply(5.0));
      BlockHitResult var10 = Module.client.world.raycast(new RaycastContext(var8, var9, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var10.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK ? var10 : null;
   }

   private RotationAngles handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)Math.toDegrees(Math.atan2(-var3, var7));
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      return new RotationAngles(var11, var12);
   }

   private String select(BlockPos var1) {
      return var1.getX() + " " + var1.getY() + " " + var1.getZ();
   }

   private void process(String var1) {
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null && previous != null && latest != null) {
         if (WorldVertexBuffer.handle(Module.client)) {
            Vec3d var2 = Module.client.gameRenderer.getCamera().getPos();
            Matrix4f var3 = var1.compute().peek().getPositionMatrix();
            float var4 = (float)(Math.min(previous.getX(), latest.getX()) - var2.x);
            float var5 = (float)(Math.min(previous.getY(), latest.getY()) - var2.y);
            float var6 = (float)(Math.min(previous.getZ(), latest.getZ()) - var2.z);
            float var7 = (float)(Math.max(previous.getX(), latest.getX()) + 1 - var2.x);
            float var8 = (float)(Math.max(previous.getY(), latest.getY()) + 1 - var2.y);
            float var9 = (float)(Math.max(previous.getZ(), latest.getZ()) + 1 - var2.z);
            float var10 = (float)(System.nanoTime() / 1.0E9);
            Immediate var11 = WorldVertexBuffer.handle();

            try {
               VertexConsumer var12 = var11.getBuffer(scalePerform);
               VertexConsumer var13 = var11.getBuffer(contextExpand);
               this.process(var13, var3, var4, var5, var6, var7, var8, var9, var10);
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = var7 - var4;

      for (int var10 = 0; var10 < 20; var10++) {
         float var11 = var10 / 20.0F;
         float var12 = (var10 + 1) / 20.0F;
         float var13 = var4 + var9 * var11;
         float var14 = var4 + var9 * var12;
         int var15 = PackedColor.handle(PackedColor.compute(-65409, -8781569, var11), (int)(140.0F * (1.0F - 0.55F * var11)));
         int var16 = PackedColor.handle(PackedColor.compute(-65409, -8781569, var12), (int)(140.0F * (1.0F - 0.55F * var12)));
         this.handle(var1, var2, var3, var5, var6, var5, var13, var14, var15, var16);
         this.handle(var1, var2, var6, var8, var3, var8, var13, var14, var15, var16);
         this.handle(var1, var2, var3, var8, var3, var5, var13, var14, var15, var16);
         this.handle(var1, var2, var6, var5, var6, var8, var13, var14, var15, var16);
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10) {
      int var11 = PackedColor.process(var9);
      int var12 = PackedColor.compute(var9);
      int var13 = PackedColor.resolve(var9);
      int var14 = PackedColor.handle(var9);
      int var15 = PackedColor.process(var10);
      int var16 = PackedColor.compute(var10);
      int var17 = PackedColor.resolve(var10);
      int var18 = PackedColor.handle(var10);
      var1.vertex(var2, var3, var7, var4).color(var11, var12, var13, var14);
      var1.vertex(var2, var5, var7, var6).color(var11, var12, var13, var14);
      var1.vertex(var2, var5, var8, var6).color(var15, var16, var17, var18);
      var1.vertex(var2, var3, var8, var4).color(var15, var16, var17, var18);
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var7 - var4;
      if (!(var10 <= 0.01F)) {
         float var11 = (var9 * 0.35F % 1.0F + 1.0F) % 1.0F;
         float var12 = var11 < 0.5F ? var11 * 2.0F : (1.0F - var11) * 2.0F;
         float var13 = var4 + var10 * var12;
         int var14 = PackedColor.handle(-65409, 38);
         this.handle(var1, var2, var3, var5, var6, var8, var13, var14);
         int var15 = PackedColor.handle(-16719617, 90);
         float var16 = var6 - var3;
         float var17 = var8 - var5;
         int var18 = Math.min(10, Math.max(1, Math.round(var16 / 3.0F)));
         int var19 = Math.min(10, Math.max(1, Math.round(var17 / 3.0F)));
         float var20 = 0.015F;

         for (int var21 = 0; var21 <= var18; var21++) {
            float var22 = var3 + var16 * ((float)var21 / var18);
            this.process(var1, var2, var22 - var20, var5, var22 + var20, var8, var13, var15);
         }

         for (int var23 = 0; var23 <= var19; var23++) {
            float var24 = var5 + var17 * ((float)var23 / var19);
            this.process(var1, var2, var3, var24 - var20, var6, var24 + var20, var13, var15);
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      int var9 = PackedColor.process(var8);
      int var10 = PackedColor.compute(var8);
      int var11 = PackedColor.resolve(var8);
      int var12 = PackedColor.handle(var8);
      var1.vertex(var2, var3, var7, var4).color(var9, var10, var11, var12);
      var1.vertex(var2, var5, var7, var4).color(var9, var10, var11, var12);
      var1.vertex(var2, var5, var7, var6).color(var9, var10, var11, var12);
      var1.vertex(var2, var3, var7, var6).color(var9, var10, var11, var12);
   }

   private void process(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      int var9 = PackedColor.process(var8);
      int var10 = PackedColor.compute(var8);
      int var11 = PackedColor.resolve(var8);
      int var12 = PackedColor.handle(var8);
      var1.vertex(var2, var3, var7, var4).color(var9, var10, var11, var12);
      var1.vertex(var2, var5, var7, var4).color(var9, var10, var11, var12);
      var1.vertex(var2, var5, var7, var6).color(var9, var10, var11, var12);
      var1.vertex(var2, var3, var7, var6).color(var9, var10, var11, var12);
   }

   private void process(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var9 * 1.4F;
      int var11 = PackedColor.handle(-65409, 190);
      float var12 = 0.02F;
      float[][] var13 = new float[][]{
         {var3, var4, var5, var6, var4, var5},
         {var6, var4, var5, var6, var4, var8},
         {var6, var4, var8, var3, var4, var8},
         {var3, var4, var8, var3, var4, var5},
         {var3, var7, var5, var6, var7, var5},
         {var6, var7, var5, var6, var7, var8},
         {var6, var7, var8, var3, var7, var8},
         {var3, var7, var8, var3, var7, var5},
         {var3, var4, var5, var3, var7, var5},
         {var6, var4, var5, var6, var7, var5},
         {var6, var4, var8, var6, var7, var8},
         {var3, var4, var8, var3, var7, var8}
      };

      for (float[] var17 : var13) {
         this.handle(var1, var2, var17[0], var17[1], var17[2], var17[3], var17[4], var17[5], var12, var11, var10);
      }
   }

   private void handle(
      VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, float var11
   ) {
      float var12 = var6 - var3;
      float var13 = var7 - var4;
      float var14 = var8 - var5;
      float var15 = (float)Math.sqrt(var12 * var12 + var13 * var13 + var14 * var14);
      if (!(var15 < 1.0E-4F)) {
         float var16 = var12 / var15;
         float var17 = var13 / var15;
         float var18 = var14 / var15;
         float var19 = 0.45F;
         float var20 = 0.35F;
         float var21 = Math.max(var19 + var20, var15 / 40.0F);
         var19 = var21 * 0.56F;
         float var22 = -((var11 % var21 + var21) % var21);

         for (float var23 = var22; var23 < var15; var23 += var21) {
            float var24 = Math.max(0.0F, var23);
            float var25 = Math.min(var15, var23 + var19);
            if (!(var25 <= var24)) {
               float var26 = var3 + var16 * var24;
               float var27 = var4 + var17 * var24;
               float var28 = var5 + var18 * var24;
               float var29 = var3 + var16 * var25;
               float var30 = var4 + var17 * var25;
               float var31 = var5 + var18 * var25;
               FarmAreaRenderer.compute(
                  var1,
                  var2,
                  Math.min(var26, var29) - var9,
                  Math.min(var27, var30) - var9,
                  Math.min(var28, var31) - var9,
                  Math.max(var26, var29) + var9,
                  Math.max(var27, var30) + var9,
                  Math.max(var28, var31) + var9,
                  var10
               );
            }
         }
      }
   }
   public static BlockPos render() {
      return previous;
   }
   public static void handle(BlockPos var0) {
      previous = var0;
   }
   public static BlockPos tick() {
      return latest;
   }
   public static void process(BlockPos var0) {
      latest = var0;
   }

   enum Mode {
      HARVEST,
      PLANT;
   }

   enum PrimaryMode {
      FARM,
      NAVIGATING,
      INTERACTING,
      WAITING_FOR_CONTAINER,
      DEPOSITING;
   }

   static final class State {
      final CocoaFarm.Mode instance;
      final BlockPos data;
      final Direction context;

      State(CocoaFarm.Mode var1, BlockPos var2, Direction var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }
   }
}
