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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.render.FarmAreaRenderer;
import ru.wild.util.render.PackedColor;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "ChorusFarm", category = ModuleCategory.Misc, description = "Авто-ферма плодов хоруса с отстрелом луком")
public class ChorusFarm extends Module {
   private static BlockPos summary;
   private static BlockPos matrixBlend;
   public final BooleanSetting source = new BooleanSetting("Сбивать плоды", true);
   public final NumberSetting target = new NumberSetting("Высота для сбора", 6.0F, 2.0F, 24.0F, 1.0F, false);
   public final BooleanSetting pending = new BooleanSetting("Авто-посадка", true);
   public final BooleanSetting previous = new BooleanSetting("Склад в сундук", true);
   public final BooleanSetting latest = new BooleanSetting("Логи", false);
   private static final double vectorMatch = 4.6;
   private static final double itemProject = 3.6;
   private static final double responseCompute = 28.0;
   private static final float providerFetch = 140.0F;
   private static final float profileDraw = 34.0F;
   private static final float vectorPerform = 1.35F;
   private static final float eventAttach = 4.0F;
   private static final float serverRead = 2.6F;
   private static final int positionAdvance = 20;
   private static final int frameCheck = 8;
   private static final int moduleCollect = 3;
   private static final long providerClose = 90L;
   private static final int presetSave = 40;
   private static final int windowConvert = 32;
   private static final int presetWrite = 4;
   private static final long colorMeasure = 150L;
   private static final long animationSchedule = 1800L;
   private static final long rendererScan = 5000L;
   private static final long sourceBuild = 2000L;
   private static final long outputCollapse = 900L;
   private static final int profileInvoke = 4;
   private static final double sourceSchedule = 1.62;
   private static final long timerRender = 60L;
   private static final int scaleSave = 6;
   private static final int colorCompute = 4;
   private static final long scaleAdapt = 8000L;
   private static final long textureRun = 6000L;
   private static final long indexBind = 30000L;
   private static final double actionRead = 4.2;
   private static final int configCollapse = 400;
   private static final long dataValidate = 4000L;
   private static final int scaleRender = 2;
   private static final long clientRefresh = 30000L;
   private static final long keyFilter = 3000L;
   private static final long requestAdapt = 18000L;
   private static final int timerMeasure = 128;
   private final Stopwatch vectorEncode = new Stopwatch();
   private final Stopwatch requestReceive = new Stopwatch();
   private final Stopwatch windowProcess = new Stopwatch();
   private final Stopwatch packetSave = new Stopwatch();
   private final Stopwatch entryAnimate = new Stopwatch();
   private final Stopwatch playerCollect = new Stopwatch();
   private final Stopwatch stateApply = new Stopwatch();
   private final Stopwatch matrixFilter = new Stopwatch();
   private final List<ChorusFarm.State> layerSample = new ArrayList<>();
   private final HashMap<BlockPos, Long> worldSend = new HashMap<>();
   private final HashMap<BlockPos, Long> targetWrite = new HashMap<>();
   private final HashMap<Integer, Long> resultEncode = new HashMap<>();
   private final HashMap<BlockPos, long[]> messageParse = new HashMap<>();
   private final Set<BlockPos> providerRead = new HashSet<>();
   private Set<BlockPos> matrixBlend2;
   private BlockPos scalePerform;
   private int contextExpand = 20;
   private BlockPos keyProcess;
   private ChorusFarm.PrimaryMode actionConvert = ChorusFarm.PrimaryMode.FARM;
   private ChorusFarm.State screenRead;
   private BlockPos animationExpand;
   private BlockPos playerRun;
   private BlockPos matrixRender;
   private BlockPos moduleTick;
   private int playerCollapse = -1;
   private int optionAdvance;
   private int effectScan = -1;
   private int optionParse;
   private long pointSubmit;
   private long listenerPerform;
   private double configMatch;
   private int actionRender;
   private int playerApply;
   private int bufferAdapt;
   private BlockPos playerUpdate;
   private boolean packetRead;
   private boolean rendererCancel;
   private int eventReceive;
   private int screenSubmit = -1;
   private boolean cacheHandle;
   private boolean rangeRelease;
   private boolean indexSave;
   private static final int indexCheck = 4096;
   private static final int settingSchedule = 16;
   private static final RenderPipeline inputAcquire = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "chorus_zone_fill"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .build()
   );
   private static final RenderPipeline listenerRun = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "chorus_zone_glow"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer indexLoad = RenderLayer.of("chorus_zone_fill", 4096, false, true, inputAcquire, MultiPhaseParameters.builder().build(false));
   private static final RenderLayer layoutSave = RenderLayer.of("chorus_zone_glow", 4096, false, true, listenerRun, MultiPhaseParameters.builder().build(false));
   private static final int blockRun = -2995201;
   private static final int playerEvaluate = -9822240;
   private static final int outputFetch = 18;

   public static void refresh() {
      summary = null;
      matrixBlend = null;
   }

   public ChorusFarm() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest);

      try {
         BaritoneAPI.getSettings().chunkCaching.value = false;
      } catch (Throwable var2) {
      }
   }

   @Override
   public void handle() {
      super.handle();
      this.actionConvert = ChorusFarm.PrimaryMode.FARM;
      this.layerSample.clear();
      this.worldSend.clear();
      this.screenRead = null;
      this.animationExpand = null;
      this.playerRun = null;
      this.matrixRender = null;
      this.moduleTick = null;
      this.playerCollapse = -1;
      this.optionAdvance = 0;
      this.effectScan = -1;
      this.optionParse = 0;
      this.pointSubmit = 0L;
      this.listenerPerform = 0L;
      this.configMatch = Double.MAX_VALUE;
      this.actionRender = 0;
      this.playerApply = 0;
      this.bufferAdapt = 0;
      this.playerUpdate = null;
      this.packetRead = false;
      this.rendererCancel = false;
      this.eventReceive = 0;
      this.screenSubmit = -1;
      this.resultEncode.clear();
      this.messageParse.clear();
      this.providerRead.clear();
      this.targetWrite.clear();
      this.matrixBlend2 = null;
      this.scalePerform = null;
      this.contextExpand = 20;
      this.keyProcess = null;
      this.requestReceive.handle();
      this.vectorEncode.handle();
      this.windowProcess.handle();
      this.packetSave.handle();
      this.entryAnimate.handle();
      this.playerCollect.handle();
      this.matrixFilter.handle();
      this.cacheHandle = (Boolean)BaritoneAPI.getSettings().allowBreak.value;
      this.rangeRelease = (Boolean)BaritoneAPI.getSettings().allowPlace.value;
      this.indexSave = (Boolean)BaritoneAPI.getSettings().allowSprint.value;
      BaritoneAPI.getSettings().allowBreak.value = false;
      BaritoneAPI.getSettings().allowPlace.value = false;
      BaritoneAPI.getSettings().chunkCaching.value = false;
      if (summary != null && matrixBlend != null) {
         this.process("Запуск, площадь " + this.tick(summary) + " — " + this.tick(matrixBlend));
      } else {
         ChatLogger.handle("§d[ChorusFarm] §fСначала задайте зону: §e.chorus pos1 §fи §e.chorus pos2");
      }
   }

   @Override
   public void process() {
      this.fetch();
      this.load();
      BaritoneAPI.getSettings().allowBreak.value = this.cacheHandle;
      BaritoneAPI.getSettings().allowPlace.value = this.rangeRelease;
      BaritoneAPI.getSettings().allowSprint.value = this.indexSave;
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      this.playerRun = null;
      this.matrixBlend2 = null;
      this.scalePerform = null;
      this.screenRead = null;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (summary == null || matrixBlend == null) {
            this.load();
         } else if (PlayerHelper.refresh()) {
            this.load();
            this.fetch();
         } else if (this.actionConvert != ChorusFarm.PrimaryMode.FARM && System.currentTimeMillis() > this.pointSubmit) {
            this.process("Тайм-аут депозит-сессии, блокирую сундук");
            this.compute(true);
         } else {
            switch (this.actionConvert) {
               case FARM:
                  this.drawAnimation();
                  break;
               case NAVIGATING:
                  this.drawProfile();
                  break;
               case INTERACTING:
                  this.performVector();
                  break;
               case WAITING_FOR_CONTAINER:
                  this.attachEvent();
                  break;
               case DEPOSITING:
                  this.readServer();
            }
         }
      }
   }

   private void drawAnimation() {
      this.closeProvider();
      if (this.previous.compute() && System.currentTimeMillis() >= this.listenerPerform && this.savePreset()) {
         BlockPos var1 = this.computeResponse();
         if (var1 != null) {
            this.load();
            this.moduleTick = var1;
            this.actionConvert = ChorusFarm.PrimaryMode.NAVIGATING;
            this.stateApply.handle();
            this.windowProcess.handle();
            this.configMatch = Double.MAX_VALUE;
            this.fetch();
            this.optionAdvance = 0;
            this.optionParse = 0;
            this.effectScan = -1;
            this.pointSubmit = System.currentTimeMillis() + 30000L;
            this.process("Инвентарь полон, иду к сундуку " + this.tick(var1));
            return;
         }
      }

      if (this.screenRead == null || !this.update(this.screenRead)) {
         this.screenRead = null;
         if (this.requestReceive.update(150L)) {
            this.save();
            this.requestReceive.handle();
         }

         ChorusFarm.State var2 = this.submit();
         if (var2 != null && this.execute(var2.data)) {
            var2 = null;
         }

         if (var2 != null && !var2.data.equals(this.animationExpand)) {
            this.animationExpand = var2.data;
            this.actionRender = 0;
            this.playerApply = 0;
            this.bufferAdapt = 0;
            this.playerUpdate = null;
            this.windowProcess.handle();
            this.playerCollect.handle();
            this.configMatch = Double.MAX_VALUE;
         }

         this.screenRead = var2;
      }

      if (this.screenRead == null || this.screenRead.instance != ChorusFarm.Mode.SHOOT) {
         this.load();
         this.playerUpdate = null;
         if (this.measure()) {
            return;
         }
      }

      if (this.screenRead == null) {
         if (!this.rendererCancel) {
            this.process("Целей нет, жду роста");
            this.rendererCancel = true;
         }

         this.fetch();
      } else {
         this.rendererCancel = false;
         switch (this.screenRead.instance) {
            case SHOOT:
               this.handle(this.screenRead);
               break;
            case PLANT:
               this.process(this.screenRead);
               break;
            case CLEAR:
               this.compute(this.screenRead);
         }
      }
   }

   private void handle(ChorusFarm.State var1) {
      Vec3d var2 = Vec3d.ofCenter(var1.data);
      if (this.playerUpdate != null) {
         double var3 = Module.client.player.getX() - (this.playerUpdate.getX() + 0.5);
         double var5 = Module.client.player.getZ() - (this.playerUpdate.getZ() + 0.5);
         if (!(var3 * var3 + var5 * var5 <= 1.4) && !this.windowProcess.update(5000L)) {
            this.load();
            this.handle(this.playerUpdate, 0);
            return;
         }

         this.playerUpdate = null;
         this.keyProcess = null;
         this.fetch();
         this.windowProcess.handle();
         this.playerCollect.handle();
         this.configMatch = Double.MAX_VALUE;
      }

      double var9 = Module.client.player.getEyePos().distanceTo(var2);
      if (var9 > 28.0) {
         this.load();
         BlockPos var11 = new BlockPos(var1.data.getX(), this.unload(), var1.data.getZ());
         if (!this.handle(var9, var11, 3)) {
            this.handle("не подойти к плоду " + this.tick(var1.data));
         }
      } else {
         this.fetch();
         if (!this.scanRenderer()) {
            this.process("Нет стрел, пропускаю отстрел");
            this.load();
            this.screenRead = null;
         } else if (!this.convertWindow()) {
            if (Module.client.player.getMainHandStack().getItem() instanceof BowItem) {
               if (!var1.data.equals(this.keyProcess)) {
                  this.keyProcess = var1.data;
                  this.contextExpand = this.handle(var1.data, var2);
               }

               float var10 = this.handle(this.contextExpand);
               RotationAngles var6 = this.handle(var2, var10);
               this.handle(var6);
               this.encodePoint();
               if (Module.client.player.getItemUseTime() < this.contextExpand) {
                  this.playerCollect.handle();
               } else if (new RotationAngles(Module.client.player).handle(var6) > 2.6F) {
                  this.playerCollect.handle();
               } else {
                  Vec3d var7 = Module.client.player.getVelocity();
                  if (var7.x * var7.x + var7.z * var7.z > 0.0025) {
                     this.playerCollect.handle();
                  } else if (!this.handle(var1.data, var10)) {
                     if (this.playerCollect.update(900L)) {
                        BlockPos var8 = this.bufferAdapt < 4 ? this.compute(var1.data) : null;
                        if (var8 != null) {
                           this.playerUpdate = var8;
                           this.bufferAdapt++;
                           this.windowProcess.handle();
                           this.configMatch = Double.MAX_VALUE;
                           this.load();
                           this.process("Меняю позицию для отстрела " + this.tick(var1.data));
                        } else {
                           this.worldSend.put(var1.data, System.currentTimeMillis() + 6000L);
                           this.handle("не навестись на плод " + this.tick(var1.data));
                        }
                     }
                  } else if (this.entryAnimate.update(60L)) {
                     this.animate();
                     this.entryAnimate.handle();
                     this.playerCollect.handle();
                     this.playerApply++;
                     if (this.playerApply >= 6) {
                        this.worldSend.put(var1.data, System.currentTimeMillis() + 6000L);
                        this.handle("плод " + this.tick(var1.data) + " не сбивается за 6 выстрелов");
                     }
                  }
               }
            }
         }
      }
   }

   private BlockPos compute(BlockPos var1) {
      BlockPos var2 = new BlockPos(var1.getX(), this.unload(), var1.getZ());
      Vec3d var3 = Vec3d.ofCenter(var1);
      BlockPos var4 = Module.client.player.getBlockPos();
      BlockPos var5 = null;
      double var6 = -Double.MAX_VALUE;

      for (int var8 = 0; var8 < 16; var8++) {
         double var9 = var8 * Math.PI / 8.0;
         double var11 = Math.cos(var9);
         double var13 = Math.sin(var9);

         for (int var15 = 3; var15 <= 6; var15++) {
            int var16 = var2.getX() + (int)Math.round(var11 * var15);
            int var17 = var2.getZ() + (int)Math.round(var13 * var15);
            BlockPos var18 = new BlockPos(var16, this.unload(), var17);
            if (this.check(var18)) {
               BlockPos var19 = this.resolve(var18);
               if (var19 != null) {
                  double var20 = var19.getX() - var4.getX();
                  double var22 = var19.getZ() - var4.getZ();
                  if (!(var20 * var20 + var22 * var22 < 4.0)) {
                     Vec3d var24 = new Vec3d(var19.getX() + 0.5, var19.getY() + 1.62, var19.getZ() + 0.5);
                     if (this.handle(var24, var3, var1)) {
                        double var25 = Math.sqrt(
                           (var19.getX() + 0.5 - (var2.getX() + 0.5)) * (var19.getX() + 0.5 - (var2.getX() + 0.5))
                              + (var19.getZ() + 0.5 - (var2.getZ() + 0.5)) * (var19.getZ() + 0.5 - (var2.getZ() + 0.5))
                        );
                        double var27 = -Math.abs(var25 - 4.0);
                        if (var27 > var6) {
                           var6 = var27;
                           var5 = var19;
                        }
                     }
                  }
               }
            }
         }
      }

      return var5;
   }

   private BlockPos resolve(BlockPos var1) {
      int[] var2 = new int[]{0, -1, 1, -2, 2};

      for (int var6 : var2) {
         BlockPos var7 = new BlockPos(var1.getX(), this.unload() + var6, var1.getZ());
         if (this.update(var7)) {
            return var7;
         }
      }

      return null;
   }

   private boolean update(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      BlockState var3 = Module.client.world.getBlockState(var1.up());
      BlockState var4 = Module.client.world.getBlockState(var1.down());
      boolean var5 = var2.isAir() || var2.getCollisionShape(Module.client.world, var1).isEmpty();
      boolean var6 = var3.isAir() || var3.getCollisionShape(Module.client.world, var1.up()).isEmpty();
      boolean var7 = !var4.isAir() && !var4.getCollisionShape(Module.client.world, var1.down()).isEmpty();
      return var5 && var6 && var7;
   }

   private boolean handle(Vec3d var1, Vec3d var2, BlockPos var3) {
      Vec3d var4 = var2.subtract(var1);
      double var5 = var4.length();
      if (var5 < 1.0E-6) {
         return true;
      }

      Vec3d var7 = var4.multiply(1.0 / var5);

      for (double var8 = 0.2; var8 < var5; var8 += 0.2) {
         Vec3d var10 = var1.add(var7.multiply(var8));
         BlockPos var11 = BlockPos.ofFloored(var10.x, var10.y, var10.z);
         if (var11.equals(var3)) {
            return true;
         }

         BlockState var12 = Module.client.world.getBlockState(var11);
         if (!var12.isAir() && !var12.getCollisionShape(Module.client.world, var11).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private void process(ChorusFarm.State var1) {
      this.load();
      if (!this.scheduleAnimation()) {
         this.process("Цветы хоруса закончились, посадка недоступна");
         this.screenRead = null;
      } else if (!this.writePreset()) {
         if (Module.client.player.getMainHandStack().isOf(Items.CHORUS_FLOWER)) {
            if (this.actionRender >= 4) {
               this.apply(var1.data);
               this.apply(var1.data.up());
               this.handle("посадка на " + this.tick(var1.data) + " не проходит, ресинк фантома");
            } else {
               BlockHitResult var2 = this.handle(var1.data, Direction.UP);
               if (var2 != null && !(Module.client.player.getEyePos().distanceTo(var2.getPos()) > 4.2)) {
                  this.windowProcess.handle();
                  this.fetch();
                  RotationAngles var3 = this.handle(var2.getPos());
                  this.handle(var3);
                  if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
                     if (this.vectorEncode.update(90L)) {
                        BlockHitResult var4 = this.invokeProfile();
                        BlockHitResult var5 = var4 != null && var4.getBlockPos().equals(var1.data) && var4.getSide() == Direction.UP ? var4 : var2;
                        Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var5);
                        Module.client.player.swingHand(Hand.MAIN_HAND);
                        this.actionRender++;
                        this.vectorEncode.handle();
                        this.playerRun = null;
                     }
                  }
               } else {
                  this.resolve(var1);
               }
            }
         }
      }
   }

   private void compute(ChorusFarm.State var1) {
      this.load();
      double var2 = Module.client.player.getEyePos().distanceTo(Vec3d.ofCenter(var1.data));
      if (var2 > 4.6) {
         if (!this.handle(var2, var1.data, 2)) {
            this.handle("не подойти к корню " + this.tick(var1.data));
         }
      } else {
         BlockHitResult var4 = this.render(var1.data);
         if (var4 != null && !(Module.client.player.getEyePos().distanceTo(var4.getPos()) > 4.2)) {
            this.windowProcess.handle();
            this.fetch();
            if (!this.measureColor()) {
               RotationAngles var5 = this.handle(var4.getPos());
               this.handle(var5);
               if (!(new RotationAngles(Module.client.player).handle(var5) > 4.0F)) {
                  BlockHitResult var6 = this.invokeProfile();
                  BlockHitResult var7 = var6 != null && var6.getBlockPos().equals(var1.data) ? var6 : var4;
                  if (!var1.data.equals(this.playerRun)) {
                     if (!this.vectorEncode.update(90L)) {
                        return;
                     }

                     Module.client.interactionManager.attackBlock(var1.data, var7.getSide());
                     this.playerRun = var1.data;
                     if (Module.client.world.getBlockState(var1.data.down()).isOf(Blocks.END_STONE)) {
                        Set var8 = this.handle(List.of(var1.data));
                        var8.remove(var1.data);
                        this.matrixBlend2 = var8;
                     } else {
                        this.matrixBlend2 = null;
                     }

                     this.packetSave.handle();
                     this.vectorEncode.handle();
                  } else {
                     if (this.packetSave.update(2000L)) {
                        this.apply(var1.data);
                        this.handle("корень " + this.tick(var1.data) + " не ломается, ресинк фантома");
                        return;
                     }

                     Module.client.interactionManager.updateBlockBreakingProgress(var1.data, var7.getSide());
                  }

                  Module.client.player.swingHand(Hand.MAIN_HAND);
               }
            }
         } else {
            this.resolve(var1);
         }
      }
   }

   private boolean handle(double var1, BlockPos var3, int var4) {
      BlockPos var5 = var3;
      if (var1 < this.configMatch - 0.4) {
         this.configMatch = var1;
         this.windowProcess.handle();
      }

      if (this.windowProcess.update(5000L)) {
         return false;
      }

      this.handle(var5, var4);
      return true;
   }

   private void handle(String var1) {
      this.process("Пропуск: " + var1);
      if (this.screenRead != null) {
         this.worldSend.put(this.screenRead.data, System.currentTimeMillis() + 8000L);
         this.apply(this.screenRead.data);
      }

      this.screenRead = null;
      this.playerRun = null;
      this.matrixBlend2 = null;
      this.fetch();
   }

   private void apply(BlockPos var1) {
      Module.client.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, var1, Direction.DOWN));
   }

   private boolean execute(BlockPos var1) {
      long var2 = System.currentTimeMillis();
      long[] var4 = this.messageParse.get(var1);
      if (var4 != null && var2 - var4[1] <= 4000L) {
         var4[0]++;
         var4[1] = var2;
         if (var4[0] >= 2L) {
            this.messageParse.remove(var1);
            this.worldSend.put(var1, var2 + 30000L);
            this.apply(var1);
            this.process("Фантомный блок " + this.tick(var1) + ", ресинк и пропуск");
            return true;
         } else {
            return false;
         }
      } else {
         this.messageParse.put(var1.toImmutable(), new long[]{1L, var2});
         if (this.messageParse.size() > 128) {
            this.messageParse.entrySet().removeIf(var2x -> var2 - var2x.getValue()[1] > 4000L);
         }

         return false;
      }
   }

   private void resolve(ChorusFarm.State var1) {
      double var2 = Module.client.player.getEyePos().distanceTo(Vec3d.ofCenter(var1.data));
      if (var2 > 4.6) {
         if (!this.handle(var2, var1.data, 1)) {
            this.handle("не подойти к " + this.tick(var1.data));
         }
      } else {
         if (this.windowProcess.update(1800L)) {
            this.handle("нет прямой видимости " + this.tick(var1.data));
         }
      }
   }

   private void encodePoint() {
      Module.client.options.useKey.setPressed(true);
      if (!Module.client.player.isUsingItem() && Module.client.interactionManager != null) {
         Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
      }

      this.packetRead = true;
   }

   private void animate() {
      Module.client.options.useKey.setPressed(false);
      this.packetRead = false;
      if (Module.client.interactionManager != null) {
         Module.client.interactionManager.stopUsingItem(Module.client.player);
      }

      Module.client.player.swingHand(Hand.MAIN_HAND);
   }

   private void load() {
      if (this.packetRead) {
         Module.client.options.useKey.setPressed(false);
         this.packetRead = false;
      }

      if (Module.client.player != null
         && Module.client.interactionManager != null
         && Module.client.player.isUsingItem()
         && Module.client.player.getActiveItem().getItem() instanceof BowItem) {
         Module.client.interactionManager.stopUsingItem(Module.client.player);
      }
   }

   private RotationAngles handle(Vec3d var1, float var2) {
      Vec3d var3 = Module.client.player.getEyePos().subtract(0.0, 0.1, 0.0);
      double var4 = var1.x - var3.x;
      double var6 = var1.z - var3.z;
      double var8 = Math.sqrt(var4 * var4 + var6 * var6);
      double var10 = var1.y - var3.y;
      float var12 = (float)Math.toDegrees(Math.atan2(-var4, var6));
      float var13 = this.handle(var8, var10, var2);
      return new RotationAngles(var12, var13);
   }

   private float handle(int var1) {
      float var2 = var1 / 20.0F;
      var2 = (var2 * var2 + var2 * 2.0F) / 3.0F;
      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      return var2 * 3.0F;
   }

   private int handle(BlockPos var1, Vec3d var2) {
      for (int var3 = 8; var3 < 20; var3++) {
         float var4 = this.handle(var3);
         RotationAngles var5 = this.handle(var2, var4);
         if (this.handle(var5.instance, var5.data, var4, var1)) {
            return Math.min(20, var3 + 3);
         }
      }

      return 20;
   }

   private float handle(double var1, double var3, float var5) {
      if (var1 < 0.35) {
         return var3 >= 0.0 ? -75.0F : 75.0F;
      }

      float var6 = -89.0F;
      float var7 = 89.0F;

      for (int var8 = 0; var8 < 60; var8++) {
         float var9 = (var6 + var7) / 2.0F;
         double var10 = this.handle(var1, var9, var5);
         if (var10 > var3) {
            var6 = var9;
         } else {
            var7 = var9;
         }
      }

      return (var6 + var7) / 2.0F;
   }

   private double handle(double var1, float var3, float var4) {
      double var5 = Math.toRadians(var3);
      double var7 = var4 * Math.cos(var5);
      double var9 = -var4 * Math.sin(var5);
      double var11 = 0.0;
      double var13 = 0.0;

      for (int var15 = 0; var15 < 600; var15++) {
         double var16 = var11;
         double var18 = var13;
         var11 += var7;
         var13 += var9;
         var7 *= 0.99;
         var9 *= 0.99;
         var9 -= 0.05;
         if (var11 >= var1) {
            double var20 = var11 - var16 > 0.001 ? (var1 - var16) / (var11 - var16) : 1.0;
            return var18 + (var13 - var18) * var20;
         }
      }

      return var13;
   }

   private boolean handle(BlockPos var1, float var2) {
      return this.handle(Module.client.player.getYaw(), Module.client.player.getPitch(), var2, var1);
   }

   private boolean handle(double var1, double var3, float var5, BlockPos var6) {
      double var7 = Math.toRadians(var1);
      double var9 = Math.toRadians(var3);
      double var11 = Math.cos(var9);
      Vec3d var13 = new Vec3d(-Math.sin(var7) * var11, -Math.sin(var9), Math.cos(var7) * var11);
      Vec3d var14 = var13.multiply(var5);
      Vec3d var15 = Module.client.player.getMovement();
      var14 = var14.add(var15.x, Module.client.player.isOnGround() ? 0.0 : var15.y, var15.z);
      Vec3d var16 = Module.client.player.getEyePos().subtract(0.0, 0.1, 0.0);
      double var17 = this.unload() - 6;

      for (int var19 = 0; var19 < 120; var19++) {
         Vec3d var20 = var16.add(var14);
         BlockHitResult var21 = Module.client.world.raycast(new RaycastContext(var16, var20, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player));
         if (var21.getType() == Type.BLOCK) {
            return var21.getBlockPos().equals(var6);
         }

         var16 = var20;
         var14 = var14.multiply(0.99).subtract(0.0, 0.05, 0.0);
         if (var16.y < var17) {
            break;
         }
      }

      return false;
   }

   private void save() {
      this.layerSample.clear();
      long var1 = System.currentTimeMillis();
      this.targetWrite.entrySet().removeIf(var2 -> var1 > var2.getValue());
      if (this.scalePerform != null) {
         BlockState var3 = Module.client.world.getBlockState(this.scalePerform);
         if (this.select(this.scalePerform) || !var3.isOf(Blocks.CHORUS_PLANT) && !var3.isOf(Blocks.CHORUS_FLOWER)) {
            this.scalePerform = null;
         }
      }

      boolean var22 = this.pending.compute() && this.scheduleAnimation();
      boolean var4 = this.source.compute();
      int var5 = (int)this.target.compute();
      int[] var6 = this.projectItem();
      ArrayList<BlockPos> var7 = new ArrayList<>();
      ArrayList<BlockPos> var8 = new ArrayList<>();
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;

      for (BlockPos var13 : BlockPos.iterate(var6[0], var6[1], var6[2], var6[3], var6[4], var6[5])) {
         BlockState var14 = Module.client.world.getBlockState(var13);
         boolean var15 = var14.isOf(Blocks.CHORUS_FLOWER);
         boolean var16 = var14.isOf(Blocks.CHORUS_PLANT);
         if (var15 || var16 || var14.isOf(Blocks.END_STONE)) {
            BlockPos var17 = var13.toImmutable();
            if (var14.isOf(Blocks.END_STONE)) {
               if (var22) {
                  BlockPos var18 = var17.up();
                  if (this.check(var18) && !this.select(var18) && !this.select(var17)) {
                     BlockState var19 = Module.client.world.getBlockState(var18);
                     if (var19.isAir() || var19.isReplaceable()) {
                        this.layerSample.add(new ChorusFarm.State(ChorusFarm.Mode.PLANT, var17, Direction.UP));
                        var10++;
                     }
                  }
               }
            } else {
               var8.add(var17);
               if (Module.client.world.getBlockState(var17.down()).isOf(Blocks.END_STONE) && !this.select(var17)) {
                  var7.add(var17);
               }
            }
         }
      }

      Set<BlockPos> var23 = this.handle(var7);
      int var24 = this.unload() + 4 + 1;

      for (BlockPos var27 : var8) {
         if (!var23.contains(var27) && !this.select(var27) && var27.getY() <= var24) {
            this.layerSample.add(new ChorusFarm.State(ChorusFarm.Mode.CLEAR, var27, null));
            var11++;
         }
      }

      HashSet var26 = new HashSet();

      for (BlockPos var30 : var7) {
         ChorusFarm.DataRecord var31 = this.prepare(var30);
         boolean var32 = !var31.flowers().isEmpty();
         boolean var33 = this.scalePerform != null && var30.equals(this.scalePerform);
         if (!var32) {
            if (!var33 && !this.providerRead.contains(var30)) {
               var26.add(var30);
            } else {
               this.layerSample.add(new ChorusFarm.State(ChorusFarm.Mode.CLEAR, var30, null));
               var11++;
            }
         } else if (var33 || var31.height() >= var5) {
            if (var4) {
               for (BlockPos var21 : var31.flowers()) {
                  if (!this.select(var21)) {
                     this.layerSample.add(new ChorusFarm.State(ChorusFarm.Mode.SHOOT, var21, null));
                     var9++;
                  }
               }
            } else {
               this.layerSample.add(new ChorusFarm.State(ChorusFarm.Mode.CLEAR, var30, null));
               var11++;
            }
         }
      }

      this.providerRead.clear();
      this.providerRead.addAll(var26);
      int var29 = var9 + var10 + var11;
      if (var29 > 0 && this.eventReceive == 0) {
         this.process("Найдено: отстрел " + var9 + ", посадка " + var10 + ", очистка " + var11);
      }

      this.eventReceive = var29;
   }

   private Set<BlockPos> handle(List<BlockPos> var1) {
      HashSet var2 = new HashSet();
      ArrayDeque var3 = new ArrayDeque();

      for (BlockPos var5 : var1) {
         if (var2.add(var5)) {
            var3.add(var5);
         }
      }

      while (!var3.isEmpty() && var2.size() < 1600) {
         BlockPos var11 = (BlockPos)var3.poll();

         for (Direction var8 : Direction.values()) {
            if (var8 != Direction.DOWN) {
               BlockPos var9 = var11.offset(var8);
               if (!var2.contains(var9)) {
                  BlockState var10 = Module.client.world.getBlockState(var9);
                  if (var10.isOf(Blocks.CHORUS_PLANT) || var10.isOf(Blocks.CHORUS_FLOWER)) {
                     var2.add(var9);
                     var3.add(var9);
                  }
               }
            }
         }
      }

      return var2;
   }

   private ChorusFarm.DataRecord prepare(BlockPos var1) {
      HashSet var2 = new HashSet();
      ArrayDeque var3 = new ArrayDeque();
      ArrayList var4 = new ArrayList();
      var3.add(var1);
      var2.add(var1);
      int var5 = var1.getY();
      int var6 = var5;

      while (!var3.isEmpty() && var2.size() < 400) {
         BlockPos var7 = (BlockPos)var3.poll();
         if (var7.getY() > var6) {
            var6 = var7.getY();
         }

         if (Module.client.world.getBlockState(var7).isOf(Blocks.CHORUS_FLOWER)) {
            var4.add(var7);
         }

         for (Direction var11 : Direction.values()) {
            if (var11 != Direction.DOWN) {
               BlockPos var12 = var7.offset(var11);
               if (!var2.contains(var12)) {
                  BlockState var13 = Module.client.world.getBlockState(var12);
                  if (var13.isOf(Blocks.CHORUS_PLANT) || var13.isOf(Blocks.CHORUS_FLOWER)) {
                     var2.add(var12);
                     var3.add(var12);
                  }
               }
            }
         }
      }

      return new ChorusFarm.DataRecord(var6 - var5 + 1, var4);
   }

   private ChorusFarm.State submit() {
      Vec3d var1 = Module.client.player.getEyePos();
      ChorusFarm.State var2 = null;
      double var3 = Double.MAX_VALUE;

      for (ChorusFarm.State var6 : this.layerSample) {
         if (this.update(var6) && !this.select(var6.data) && !this.refresh(var6.data)) {
            double var7 = var1.squaredDistanceTo(this.apply(var6));
            if (var6.instance == ChorusFarm.Mode.CLEAR) {
               var7 -= 64.0;
            } else if (var6.instance == ChorusFarm.Mode.PLANT) {
               var7 += 0.001;
            }

            if (this.scalePerform != null && var6.data.getSquaredDistance(this.scalePerform) < 64.0) {
               var7 -= 10000.0;
            }

            if (var7 < var3) {
               var3 = var7;
               var2 = var6;
            }
         }
      }

      return var2;
   }

   private boolean update(ChorusFarm.State var1) {
      if (var1 == null) {
         return false;
      }

      BlockState var2 = Module.client.world.getBlockState(var1.data);

      return switch (var1.instance) {
         case SHOOT -> this.source.compute() && var2.isOf(Blocks.CHORUS_FLOWER);
         case PLANT -> {
            if (!this.scheduleAnimation()) {
               yield false;
            } else if (!var2.isOf(Blocks.END_STONE)) {
               yield false;
            } else {
               BlockState var3 = Module.client.world.getBlockState(var1.data.up());
               yield var3.isAir() || var3.isReplaceable();
            }
         }
         case CLEAR -> var2.isOf(Blocks.CHORUS_PLANT) || var2.isOf(Blocks.CHORUS_FLOWER);
      };
   }

   private Vec3d apply(ChorusFarm.State var1) {
      return var1.instance == ChorusFarm.Mode.PLANT
         ? new Vec3d(var1.data.getX() + 0.5, var1.data.getY() + 1.0, var1.data.getZ() + 0.5)
         : Vec3d.ofCenter(var1.data);
   }

   private int unload() {
      return Math.min(summary.getY(), matrixBlend.getY());
   }

   private boolean check(BlockPos var1) {
      int var2 = Math.min(summary.getX(), matrixBlend.getX());
      int var3 = Math.max(summary.getX(), matrixBlend.getX());
      int var4 = Math.min(summary.getZ(), matrixBlend.getZ());
      int var5 = Math.max(summary.getZ(), matrixBlend.getZ());
      return var1.getX() >= var2 && var1.getX() <= var3 && var1.getZ() >= var4 && var1.getZ() <= var5;
   }

   private void handle(BlockPos var1, int var2) {
      IBaritone var3 = BaritoneAPI.getProvider().getPrimaryBaritone();
      boolean var4 = !var1.equals(this.matrixRender);
      if (var4 || !var3.getCustomGoalProcess().isActive()) {
         var3.getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, var2));
         if (var4) {
            this.process("Иду к " + this.tick(var1));
         }

         this.matrixRender = var1;
      }
   }

   private void fetch() {
      IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
      if (var1.getCustomGoalProcess().isActive()) {
         var1.getPathingBehavior().cancelEverything();
      }

      this.matrixRender = null;
   }

   private boolean measure() {
      if (this.scalePerform != null) {
         return false;
      }

      if (!this.matchVector()) {
         return false;
      }

      ItemEntity var1 = this.blendMatrix();
      if (var1 == null) {
         this.screenSubmit = -1;
         return false;
      }

      double var2 = Module.client.player.getX() - var1.getX();
      double var4 = Module.client.player.getZ() - var1.getZ();
      double var6 = var2 * var2 + var4 * var4;
      double var8 = Math.abs(Module.client.player.getY() - var1.getY());
      if (var6 <= 0.8 && var8 < 1.3) {
         this.screenSubmit = -1;
         return false;
      }

      if (var1.getId() != this.screenSubmit) {
         this.screenSubmit = var1.getId();
         this.matrixFilter.handle();
      }

      if (this.matrixFilter.update(1500L)) {
         BlockPos var10 = this.handle(var1);
         if (var10 != null) {
            this.scalePerform = var10;
            this.screenSubmit = -1;
            this.process("Плод завис на растении, харвест корня " + this.tick(var10));
            return false;
         }
      }

      if (this.matrixFilter.update(10000L)) {
         this.resultEncode.put(var1.getId(), System.currentTimeMillis() + 18000L);
         this.screenSubmit = -1;
         return false;
      } else {
         this.load();
         this.handle(BlockPos.ofFloored(var1.getX(), var1.getY() + 0.1, var1.getZ()), 0);
         return true;
      }
   }

   private ItemEntity blendMatrix() {
      Box var1 = new Box(
            Math.min(summary.getX(), matrixBlend.getX()),
            this.unload() - 4,
            Math.min(summary.getZ(), matrixBlend.getZ()),
            Math.max(summary.getX(), matrixBlend.getX()) + 1,
            this.unload() + 32,
            Math.max(summary.getZ(), matrixBlend.getZ()) + 1
         )
         .expand(2.5);
      List<ItemEntity> var2 = Module.client.world
         .getEntitiesByClass(
            ItemEntity.class, var1, var0 -> var0.isAlive() && (var0.getStack().isOf(Items.CHORUS_FRUIT) || var0.getStack().isOf(Items.CHORUS_FLOWER))
         );
      ItemEntity var3 = null;
      double var4 = Double.MAX_VALUE;
      long var6 = System.currentTimeMillis();

      for (ItemEntity var9 : var2) {
         Long var10 = this.resultEncode.get(var9.getId());
         if (var10 != null) {
            if (var6 <= var10) {
               continue;
            }

            this.resultEncode.remove(var9.getId());
         }

         double var11 = Module.client.player.squaredDistanceTo(var9);
         if (var11 < var4) {
            var4 = var11;
            var3 = var9;
         }
      }

      return var3;
   }

   private BlockPos handle(ItemEntity var1) {
      if (var1.getY() - this.unload() < 1.5) {
         return null;
      }

      BlockPos var2 = BlockPos.ofFloored(var1.getX(), var1.getY() + 0.05, var1.getZ());
      BlockPos var3 = null;

      for (BlockPos var7 : new BlockPos[]{var2.down(), var2, var2.up()}) {
         BlockState var8 = Module.client.world.getBlockState(var7);
         if (var8.isOf(Blocks.CHORUS_PLANT) || var8.isOf(Blocks.CHORUS_FLOWER)) {
            var3 = var7;
            break;
         }
      }

      return var3 == null ? null : this.onTick(var3);
   }

   private BlockPos onTick(BlockPos var1) {
      HashSet var2 = new HashSet();
      ArrayDeque var3 = new ArrayDeque();
      var2.add(var1);
      var3.add(var1);

      while (!var3.isEmpty() && var2.size() < 400) {
         BlockPos var4 = (BlockPos)var3.poll();
         BlockState var5 = Module.client.world.getBlockState(var4);
         if ((var5.isOf(Blocks.CHORUS_PLANT) || var5.isOf(Blocks.CHORUS_FLOWER))
            && Module.client.world.getBlockState(var4.down()).isOf(Blocks.END_STONE)
            && !this.select(var4)) {
            return var4;
         }

         for (Direction var9 : Direction.values()) {
            BlockPos var10 = var4.offset(var9);
            if (!var2.contains(var10)) {
               BlockState var11 = Module.client.world.getBlockState(var10);
               if (var11.isOf(Blocks.CHORUS_PLANT) || var11.isOf(Blocks.CHORUS_FLOWER)) {
                  var2.add(var10);
                  var3.add(var10);
               }
            }
         }
      }

      return null;
   }

   private boolean matchVector() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (var2.isEmpty()) {
            return true;
         }

         if ((var2.isOf(Items.CHORUS_FRUIT) || var2.isOf(Items.CHORUS_FLOWER)) && var2.getCount() < var2.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private int[] projectItem() {
      int var1 = Math.min(summary.getX(), matrixBlend.getX());
      int var2 = Math.min(summary.getZ(), matrixBlend.getZ());
      int var3 = Math.max(summary.getX(), matrixBlend.getX());
      int var4 = Math.max(summary.getZ(), matrixBlend.getZ());
      BlockPos var5 = Module.client.player.getBlockPos();
      var1 = Math.max(var1, var5.getX() - 40);
      var2 = Math.max(var2, var5.getZ() - 40);
      var3 = Math.min(var3, var5.getX() + 40);
      var4 = Math.min(var4, var5.getZ() + 40);
      int var6 = this.unload() - 4;
      int var7 = this.unload() + 32;
      return new int[]{var1, var6, var2, var3, var7, var4};
   }

   private BlockPos computeResponse() {
      int[] var1 = this.projectItem();
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

   private boolean fetchProvider() {
      return this.moduleTick != null && this.handle(Module.client.world.getBlockState(this.moduleTick));
   }

   private void compute(boolean var1) {
      if (var1) {
         this.listenerPerform = System.currentTimeMillis() + 30000L;
      }

      this.moduleTick = null;
      this.playerCollapse = -1;
      this.fetch();
      this.actionConvert = ChorusFarm.PrimaryMode.FARM;
   }

   private void drawProfile() {
      if (!this.fetchProvider()) {
         this.compute(false);
      } else if (this.stateApply.update(15000L)) {
         this.process("Не смог дойти до сундука, вернусь позже");
         this.compute(true);
      } else if (Module.client.player.getEyePos().distanceTo(Vec3d.ofCenter(this.moduleTick)) <= 4.5) {
         this.fetch();
         this.vectorEncode.handle();
         this.actionConvert = ChorusFarm.PrimaryMode.INTERACTING;
      } else {
         this.handle(this.moduleTick, 2);
      }
   }

   private void performVector() {
      if (!this.fetchProvider()) {
         this.compute(false);
      } else if (this.optionAdvance >= 3) {
         this.process("Сундук не открывается, блокирую");
         this.compute(true);
      } else {
         this.fetch();
         if (Module.client.player.getEyePos().distanceTo(Vec3d.ofCenter(this.moduleTick)) > 4.6) {
            this.stateApply.handle();
            this.actionConvert = ChorusFarm.PrimaryMode.NAVIGATING;
         } else {
            BlockHitResult var1 = this.render(this.moduleTick);
            Vec3d var2 = var1 != null ? var1.getPos() : Vec3d.ofCenter(this.moduleTick);
            RotationAngles var3 = this.handle(var2);
            this.handle(var3);
            if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
               if (this.vectorEncode.update(90L)) {
                  BlockHitResult var4 = var1 != null ? var1 : new BlockHitResult(var2, Direction.UP, this.moduleTick, false);
                  Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var4);
                  Module.client.player.swingHand(Hand.MAIN_HAND);
                  this.optionAdvance++;
                  this.playerCollapse = -1;
                  this.stateApply.handle();
                  this.vectorEncode.handle();
                  this.actionConvert = ChorusFarm.PrimaryMode.WAITING_FOR_CONTAINER;
               }
            }
         }
      }
   }

   private void attachEvent() {
      this.fetch();
      if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
         int var3 = ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId;
         if (Module.client.player.currentScreenHandler != null && Module.client.player.currentScreenHandler.syncId == var3) {
            this.playerCollapse = var3;
            this.effectScan = -1;
            this.optionParse = 0;
            this.stateApply.handle();
            this.actionConvert = ChorusFarm.PrimaryMode.DEPOSITING;
            return;
         }
      }

      if (this.stateApply.update(4000L)) {
         this.process("Сундук не ответил открытием, повтор подхода");
         this.stateApply.handle();
         this.actionConvert = ChorusFarm.PrimaryMode.NAVIGATING;
      }
   }

   private void readServer() {
      if (!(
         Module.client.currentScreen instanceof GenericContainerScreen var1
            && Module.client.player.currentScreenHandler != null
            && Module.client.player.currentScreenHandler.syncId == this.playerCollapse
            && ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId == this.playerCollapse
      )) {
         this.compute(false);
      } else if (this.stateApply.update(50L)) {
         int var6 = this.collectModule();
         if (this.effectScan >= 0 && var6 >= this.effectScan) {
            this.optionParse++;
         } else {
            this.optionParse = 0;
         }

         this.effectScan = var6;
         if (this.optionParse >= 3) {
            ChatLogger.handle("§d[ChorusFarm] §fСундук заполнен, освободите место");
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
               Module.client.interactionManager.clickSlot(this.playerCollapse, var5, 0, SlotActionType.QUICK_MOVE, Module.client.player);
               this.stateApply.handle();
            }
         }
      }
   }

   private int advancePosition() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.isOf(Items.CHORUS_FRUIT)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private int checkFrame() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.isOf(Items.CHORUS_FLOWER)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private int collectModule() {
      return this.advancePosition() + Math.max(0, this.checkFrame() - 128);
   }

   private int handle(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         Slot var4 = var1.getSlot(var3);
         if (var4.hasStack() && var4.getStack().isOf(Items.CHORUS_FRUIT)) {
            return var3;
         }
      }

      if (this.checkFrame() > 128) {
         for (int var5 = var2; var5 < var1.slots.size(); var5++) {
            Slot var6 = var1.getSlot(var5);
            if (var6.hasStack() && var6.getStack().isOf(Items.CHORUS_FLOWER)) {
               return var5;
            }
         }
      }

      return -1;
   }

   private boolean select(BlockPos var1) {
      Long var2 = this.worldSend.get(var1);
      if (var2 == null) {
         return false;
      } else if (System.currentTimeMillis() > var2) {
         this.worldSend.remove(var1);
         return false;
      } else {
         return true;
      }
   }

   private boolean refresh(BlockPos var1) {
      Long var2 = this.targetWrite.get(var1);
      if (var2 == null) {
         return false;
      } else if (System.currentTimeMillis() > var2) {
         this.targetWrite.remove(var1);
         return false;
      } else {
         return true;
      }
   }

   private void closeProvider() {
      if (this.playerRun != null) {
         BlockState var1 = Module.client.world.getBlockState(this.playerRun);
         if (!var1.isOf(Blocks.CHORUS_PLANT) && !var1.isOf(Blocks.CHORUS_FLOWER)) {
            if (this.matrixBlend2 != null) {
               long var2 = System.currentTimeMillis() + 3000L;

               for (BlockPos var5 : this.matrixBlend2) {
                  this.targetWrite.put(var5, var2);
               }

               this.matrixBlend2 = null;
            }

            this.playerRun = null;
         }
      }
   }

   private boolean savePreset() {
      return this.buildSource() >= 4 ? true : this.collapseOutput() == 0 && (this.advancePosition() > 0 || this.checkFrame() > 128);
   }

   private boolean convertWindow() {
      if (Module.client.player.getMainHandStack().getItem() instanceof BowItem) {
         return false;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).getItem() instanceof BowItem) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            return false;
         }
      }

      for (int var2 = 9; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).getItem() instanceof BowItem) {
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

   private boolean writePreset() {
      if (Module.client.player.getMainHandStack().isOf(Items.CHORUS_FLOWER)) {
         return false;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isOf(Items.CHORUS_FLOWER)) {
            Module.client.player.getInventory().setSelectedSlot(var1);
            return false;
         }
      }

      for (int var2 = 9; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isOf(Items.CHORUS_FLOWER)) {
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

   private boolean measureColor() {
      ItemStack var1 = Module.client.player.getMainHandStack();
      if (!(var1.getItem() instanceof BowItem) && !var1.isOf(Items.CHORUS_FLOWER)) {
         return false;
      }

      for (int var2 = 0; var2 < 9; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (!var3.isEmpty()
            && !(var3.getItem() instanceof BowItem)
            && !var3.isOf(Items.CHORUS_FLOWER)
            && !var3.isOf(Items.ARROW)
            && !var3.isOf(Items.CHORUS_FRUIT)) {
            Module.client.player.getInventory().setSelectedSlot(var2);
            return false;
         }
      }

      return false;
   }

   private boolean scheduleAnimation() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isOf(Items.CHORUS_FLOWER)) {
            return true;
         }
      }

      return false;
   }

   private boolean scanRenderer() {
      ItemStack var1 = Module.client.player.getMainHandStack();
      if (var1.getItem() instanceof BowItem && this.handle(var1)) {
         return true;
      }

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (var3.isOf(Items.ARROW) || var3.isOf(Items.SPECTRAL_ARROW) || var3.isOf(Items.TIPPED_ARROW)) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         ItemEnchantmentsComponent var2 = (ItemEnchantmentsComponent)var1.get(DataComponentTypes.ENCHANTMENTS);
         if (var2 != null && !var2.isEmpty()) {
            for (Entry var4 : var2.getEnchantmentEntries()) {
               if (((RegistryEntry)var4.getKey()).matchesKey(Enchantments.INFINITY)) {
                  return var4.getIntValue() > 0;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int buildSource() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isOf(Items.CHORUS_FRUIT)) {
            var1++;
         }
      }

      return var1;
   }

   private int collapseOutput() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
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
            if (var4.distanceTo(var17) <= 4.6 && this.process(var4, var17, var1)) {
               return new BlockHitResult(var17, var2, var1, false);
            }
         }
      }

      return null;
   }

   private boolean process(Vec3d var1, Vec3d var2, BlockPos var3) {
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
            if (!var11.isOf(Blocks.CHORUS_PLANT)
               && !var11.isOf(Blocks.CHORUS_FLOWER)
               && !var11.isAir()
               && !var11.getCollisionShape(Module.client.world, var10).isEmpty()) {
               return false;
            }
         }
      }

      return true;
   }

   private BlockHitResult render(BlockPos var1) {
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

   private BlockHitResult process(BlockPos var1, Direction var2) {
      Vec3d var3 = Module.client.player.getEyePos();
      double[] var4 = new double[]{0.5, 0.3, 0.7};

      for (double var8 : var4) {
         for (double var13 : var4) {
            Vec3d var15 = this.handle(var1, var2, var8, var13);
            BlockHitResult var16 = Module.client.world.raycast(new RaycastContext(var3, var15, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
            if (var16.getType() == Type.BLOCK && var16.getBlockPos().equals(var1) && var16.getSide() == var2) {
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
         case DOWN -> new Vec3d(var7 + var3, var9, var11 + var5);
         case UP -> new Vec3d(var7 + var3, var9 + 1.0, var11 + var5);
         default -> throw new MatchException(null, null);
      };
   }

   private Vec3d compute(BlockPos var1, Direction var2) {
      return new Vec3d(var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5);
   }

   private void handle(RotationAngles var1) {
      float var2 = new RotationAngles(Module.client.player).handle(var1);
      float var3 = Math.max(34.0F, Math.min(140.0F, var2 * 1.35F));
      RotationController.handle(var1, var3, var3, var3, var3, 2, 20, false);
   }

   private BlockHitResult invokeProfile() {
      double var1 = Math.toRadians(Module.client.player.getYaw());
      double var3 = Math.toRadians(Module.client.player.getPitch());
      double var5 = Math.cos(var3);
      Vec3d var7 = new Vec3d(-Math.sin(var1) * var5, -Math.sin(var3), Math.cos(var1) * var5);
      Vec3d var8 = Module.client.player.getEyePos();
      Vec3d var9 = var8.add(var7.multiply(5.0));
      BlockHitResult var10 = Module.client.world.raycast(new RaycastContext(var8, var9, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var10.getType() == Type.BLOCK ? var10 : null;
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

   private String tick(BlockPos var1) {
      return var1.getX() + " " + var1.getY() + " " + var1.getZ();
   }

   private void process(String var1) {
      if (this.latest.compute()) {
         ChatLogger.handle("§d[ChorusFarm] §7" + var1);
      }
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null && summary != null && matrixBlend != null) {
         if (WorldVertexBuffer.handle(Module.client)) {
            Vec3d var2 = Module.client.gameRenderer.getCamera().getPos();
            Matrix4f var3 = var1.compute().peek().getPositionMatrix();
            int var4 = this.unload() - 1;
            float var5 = (float)(Math.min(summary.getX(), matrixBlend.getX()) - var2.x);
            float var6 = (float)(var4 - var2.y);
            float var7 = (float)(Math.min(summary.getZ(), matrixBlend.getZ()) - var2.z);
            float var8 = (float)(Math.max(summary.getX(), matrixBlend.getX()) + 1 - var2.x);
            float var9 = (float)(var4 + 16 - var2.y);
            float var10 = (float)(Math.max(summary.getZ(), matrixBlend.getZ()) + 1 - var2.z);
            float var11 = (float)(System.nanoTime() / 1.0E9);
            Immediate var12 = WorldVertexBuffer.handle();

            try {
               VertexConsumer var13 = var12.getBuffer(indexLoad);
               VertexConsumer var14 = var12.getBuffer(layoutSave);
               this.handle(var14, var3, var5, var6, var7, var8, var9, var10, var11);
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = var7 - var4;

      for (int var10 = 0; var10 < 18; var10++) {
         float var11 = var10 / 18.0F;
         float var12 = (var10 + 1) / 18.0F;
         float var13 = var4 + var9 * var11;
         float var14 = var4 + var9 * var12;
         int var15 = PackedColor.handle(PackedColor.compute(-2995201, -9822240, var11), (int)(120.0F * (1.0F - 0.7F * var11)));
         int var16 = PackedColor.handle(PackedColor.compute(-2995201, -9822240, var12), (int)(120.0F * (1.0F - 0.7F * var12)));
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
      float var10 = var9 * 1.4F;
      int var11 = PackedColor.handle(-2995201, 200);
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
      return summary;
   }
   public static void handle(BlockPos var0) {
      summary = var0;
   }
   public static BlockPos tick() {
      return matrixBlend;
   }
   public static void process(BlockPos var0) {
      matrixBlend = var0;
   }

   record DataRecord(int height, List<BlockPos> flowers) {
   }

   enum Mode {
      SHOOT,
      PLANT,
      CLEAR;
   }

   enum PrimaryMode {
      FARM,
      NAVIGATING,
      INTERACTING,
      WAITING_FOR_CONTAINER,
      DEPOSITING;
   }

   static final class State {
      final ChorusFarm.Mode instance;
      final BlockPos data;
      final Direction context;

      State(ChorusFarm.Mode var1, BlockPos var2, Direction var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
      }
   }
}
