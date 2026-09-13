package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.utils.BetterBlockPos;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.ScreenOpenedEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.ContainerScreenPolicy;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoMine", description = "Полная автоматизация шахты", category = ModuleCategory.Misc)
public class AutoMine extends Module {
   public final StringSetting source = new StringSetting("Анархия для сброса", "903");
   public final KeybindSetting target = new KeybindSetting("Бинд на сундук", -1);
   public final BooleanSetting pending = new BooleanSetting("Не отображать экран", false);
   private final StringSetting previous = new StringSetting("AutoMineLayoutData", "").handle(() -> true);
   private final StringSetting latest = new StringSetting("AutoMineDropChest", "").handle(() -> true);
   private final Stopwatch summary = new Stopwatch();
   private final Stopwatch matrixBlend = new Stopwatch();
   private final Stopwatch vectorMatch = new Stopwatch();
   private final Stopwatch itemProject = new Stopwatch();
   private final Stopwatch responseCompute = new Stopwatch();
   private final Stopwatch providerFetch = new Stopwatch();
   private int profileDraw = 0;
   private final List<String> vectorPerform = Arrays.asList(
      "405", "503", "504", "505", "304", "902", "901", "404", "402", "401", "903", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210"
   );
   private static final BlockPos eventAttach = new BlockPos(-55, 93, 30);
   private static final BlockPos serverRead = new BlockPos(-73, 84, 48);
   private static final double positionAdvance = 4.0;
   private static final double frameCheck = 3.5;
   private static final int moduleCollect = 2500;
   private static final int providerClose = 2500;
   private AutoMine.Mode presetSave = AutoMine.Mode.IDLE;
   private boolean windowConvert = false;
   private BlockPos presetWrite = null;
   private BlockPos colorMeasure = null;
   private BlockPos animationSchedule = null;
   private BlockPos rendererScan = null;
   private BlockPos sourceBuild = null;
   private boolean outputCollapse = false;
   private double profileInvoke = -1.0;
   private double sourceSchedule = -1.0;
   private boolean timerRender;
   private boolean scaleSave;
   private boolean colorCompute;
   private boolean scaleAdapt;
   private List<Block> textureRun = List.of();
   private final Queue<Runnable> indexBind = new ArrayDeque<>();
   private final Map<Integer, AutoMine.DataRecord> actionRead = new HashMap<>();
   private static final AutoMine.DataRecord configCollapse = new AutoMine.DataRecord("", 0);
   private GenericContainerScreen dataValidate;
   private static final int scaleRender = 1024;
   private static final RenderPipeline clientRefresh = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "automine_block_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer keyFilter = RenderLayer.of(
      "automine_block_box", 1024, false, true, clientRefresh, MultiPhaseParameters.builder().build(false)
   );

   public AutoMine() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest);
   }

   @Override
   public void handle() {
      if (WildClient.instance.data.handle(AttackAura.class).enabled) {
         ChatLogger.handle("Отключите ауру для включения модуля");
         this.toggle();
      } else {
         super.handle();
         if (Module.client.options != null) {
            Module.client.options.setPerspective(Perspective.FIRST_PERSON);
         }

         ViewRotationCoordinator.instance = true;
         this.presetSave = AutoMine.Mode.IDLE;
         this.windowConvert = false;
         this.presetWrite = null;
         this.colorMeasure = null;
         this.animationSchedule = null;
         this.sourceBuild = null;
         this.outputCollapse = false;
         this.profileInvoke = -1.0;
         this.sourceSchedule = -1.0;
         this.indexBind.clear();
         this.drawProfile();
         this.collectModule();
         this.timerRender = (Boolean)BaritoneAPI.getSettings().allowPlace.value;
         this.scaleSave = (Boolean)BaritoneAPI.getSettings().allowBreak.value;
         this.colorCompute = (Boolean)BaritoneAPI.getSettings().legitMine.value;
         this.scaleAdapt = (Boolean)BaritoneAPI.getSettings().walkWhileBreaking.value;
         List var1 = (List)BaritoneAPI.getSettings().blocksToAvoidBreaking.value;
         this.textureRun = var1 == null ? List.of() : new ArrayList<>(var1);
         BaritoneAPI.getSettings().allowPlace.value = false;
         BaritoneAPI.getSettings().allowBreak.value = true;
         BaritoneAPI.getSettings().legitMine.value = false;
         BaritoneAPI.getSettings().walkWhileBreaking.value = false;
         List<Block> var2 = Arrays.asList(
            Blocks.SPRUCE_LOG,
            Blocks.SPRUCE_WOOD,
            Blocks.SPRUCE_PLANKS,
            Blocks.STRIPPED_SPRUCE_LOG,
            Blocks.STRIPPED_SPRUCE_WOOD,
            Blocks.OAK_LOG,
            Blocks.OAK_WOOD,
            Blocks.OAK_PLANKS,
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.COARSE_DIRT,
            Blocks.PODZOL,
            Blocks.STONE_BRICKS,
            Blocks.CRACKED_STONE_BRICKS,
            Blocks.MOSSY_STONE_BRICKS,
            Blocks.LADDER,
            Blocks.BEDROCK,
            Blocks.BARREL,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST
         );
         List var3 = (List)BaritoneAPI.getSettings().blocksToAvoidBreaking.value;
         if (var3 != null) {
            for (Block var5 : var2) {
               if (!var3.contains(var5)) {
                  var3.add(var5);
               }
            }
         }

         if (Module.client.player != null && Module.client.world != null) {
            if (this.matchVector()) {
               this.submit();
            } else if (this.attachEvent()) {
               this.render();
            } else if (this.performVector()) {
               this.tick();
            } else {
               Module.client.player.networkHandler.sendChatCommand("warp mine");
               this.presetSave = AutoMine.Mode.WAITING_FOR_TP;
               this.summary.handle();
            }
         }
      }
   }

   @Override
   public void process() {
      super.process();
      if (Module.client.options != null) {
         Module.client.options.setPerspective(Perspective.FIRST_PERSON);
         if (this.outputCollapse) {
            Module.client.options.sneakKey.setPressed(false);
            this.outputCollapse = false;
         }
      }

      ViewRotationCoordinator.instance = false;
      this.animate();
      this.indexBind.clear();
      BaritoneAPI.getSettings().allowPlace.value = this.timerRender;
      BaritoneAPI.getSettings().allowBreak.value = this.scaleSave;
      BaritoneAPI.getSettings().legitMine.value = this.colorCompute;
      BaritoneAPI.getSettings().walkWhileBreaking.value = this.scaleAdapt;
      List var1 = (List)BaritoneAPI.getSettings().blocksToAvoidBreaking.value;
      if (var1 != null) {
         var1.clear();
         var1.addAll(this.textureRun);
      }

      IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
      if (this.windowConvert) {
         var2.getCommandManager().execute("resume");
         this.windowConvert = false;
      }

      var2.getCommandManager().execute("stop");
      var2.getSelectionManager().removeAllSelections();
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      this.dataValidate = null;
   }

   @EventHandler
   public void handle(ScreenOpenedEvent var1) {
      if (this.pending.compute() && var1.compute() instanceof GenericContainerScreen var2) {
         if (this.presetSave == AutoMine.Mode.OPENING_DROP_CHEST
            || this.presetSave == AutoMine.Mode.WAITING_FOR_DROP_GUI
            || this.presetSave == AutoMine.Mode.DROPPING) {
            this.dataValidate = var2;
            var1.resolve();
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
         boolean var3 = PlayerHelper.refresh();
         if (var3) {
            if (!this.windowConvert) {
               var2.getCommandManager().execute("pause");
               this.windowConvert = true;
               this.animate();
            }
         } else {
            if (this.windowConvert) {
               var2.getCommandManager().execute("resume");
               this.windowConvert = false;
            }

            if (this.presetSave == AutoMine.Mode.MINING && this.presetWrite != null) {
               if (Module.client.player.isClimbing() && Module.client.options.attackKey.isPressed()) {
                  Module.client.options.sneakKey.setPressed(true);
                  this.outputCollapse = true;
               } else if (this.outputCollapse) {
                  Module.client.options.sneakKey.setPressed(false);
                  this.outputCollapse = false;
               }
            } else if (this.outputCollapse) {
               Module.client.options.sneakKey.setPressed(false);
               this.outputCollapse = false;
            }

            if (!this.matchVector() || this.presetSave != AutoMine.Mode.MINING && this.presetSave != AutoMine.Mode.GOING_TO_MINE) {
               GenericContainerScreen var4 = this.savePreset();
               if (this.presetSave == AutoMine.Mode.DROPPING && var4 != null) {
                  this.handle((GenericContainerScreenHandler)var4.getScreenHandler());
               } else {
                  switch (this.presetSave) {
                     case WAITING_FOR_TP:
                        if (this.summary.update(5500L)) {
                           this.tick();
                        }
                        break;
                     case GOING_TO_MINE:
                        this.handle(var2);
                        break;
                     case MINING:
                        this.encodePoint();
                        if (this.presetWrite == null || this.resolve(this.presetWrite)) {
                           this.drawAnimation();
                        }

                        if (this.summary.update(2000L)) {
                           if (!this.readServer()) {
                              var2.getCommandManager().execute("stop");
                              this.save();
                           }

                           this.summary.handle();
                        }
                        break;
                     case TELEPORTING_TO_DROP:
                        if (this.summary.update(6000L)) {
                           this.unload();
                        }
                        break;
                     case GOING_TO_DROP_CHEST:
                        this.process(var2);
                        break;
                     case ROTATING_DROP_CHEST:
                        this.fetch();
                        break;
                     case OPENING_DROP_CHEST:
                        this.measure();
                        break;
                     case WAITING_FOR_DROP_GUI:
                        if (this.savePreset() != null) {
                           this.presetSave = AutoMine.Mode.DROPPING;
                           this.matrixBlend.handle();
                        } else if (this.itemProject.update(3000L)) {
                           this.presetSave = AutoMine.Mode.OPENING_DROP_CHEST;
                           this.itemProject.handle();
                        }
                     case DROPPING:
                     default:
                        break;
                     case CHANGING_ANARCHY:
                        if (this.summary.update(2000L)) {
                           if (this.attachEvent()) {
                              this.render();
                           } else if (this.performVector()) {
                              this.tick();
                           } else {
                              Module.client.player.networkHandler.sendChatCommand("warp mine");
                              this.presetSave = AutoMine.Mode.WAITING_FOR_TP;
                              this.summary.handle();
                           }
                        }
                  }
               }
            } else {
               var2.getCommandManager().execute("stop");
               this.submit();
            }
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var4 = var2.content().getString();
         if ((var4.contains("Телепорт") || var4.contains("teleport") || var4.contains("Teleport"))
            && (
               this.presetSave == AutoMine.Mode.WAITING_FOR_TP
                  || this.presetSave == AutoMine.Mode.TELEPORTING_TO_DROP
                  || this.presetSave == AutoMine.Mode.CHANGING_ANARCHY
            )) {
            this.summary.handle();
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (this.target.compute() != -1 && var1.resolve() == this.target.compute()) {
            if (Module.client.crosshairTarget instanceof BlockHitResult var2) {
               BlockPos var4 = var2.getBlockPos();
               if (this.prepare(var4)) {
                  this.check(var4);
                  ChatLogger.handle("§8[§6AutoMine§8] §aСундук для сброса установлен: " + var4.toShortString());
               } else {
                  ChatLogger.handle("§8[§6AutoMine§8] §cСмотрите на сундук, бочку или шалкер.");
               }
            }
         }
      }
   }

   private void render() {
      if (this.readServer()) {
         this.presetSave = AutoMine.Mode.MINING;
         this.summary.handle();
         this.drawAnimation();
      } else {
         this.save();
      }
   }

   private void tick() {
      IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.animate();
      var1.getSelectionManager().removeAllSelections();
      var1.getCommandManager().execute("stop");
      this.animationSchedule = this.advancePosition();
      if (this.animationSchedule == null) {
         this.animationSchedule = this.convertWindow();
      }

      this.presetSave = AutoMine.Mode.GOING_TO_MINE;
      this.profileInvoke = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.animationSchedule));
      this.summary.handle();
      this.responseCompute.handle();
      this.providerFetch.handle();
      var1.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.animationSchedule, 2));
   }

   private void handle(IBaritone var1) {
      if (this.attachEvent()) {
         var1.getPathingBehavior().cancelEverything();
         this.animationSchedule = null;
         this.render();
      } else {
         if (this.animationSchedule == null || this.responseCompute.update(10000L)) {
            this.animationSchedule = this.advancePosition();
            if (this.animationSchedule == null) {
               this.animationSchedule = this.convertWindow();
            }
         }

         double var2 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.animationSchedule));
         if (this.profileInvoke < 0.0 || var2 < this.profileInvoke - 1.0) {
            this.profileInvoke = var2;
            this.providerFetch.handle();
         }

         if (!var1.getCustomGoalProcess().isActive() || this.responseCompute.update(2500L)) {
            var1.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.animationSchedule, 2));
            this.responseCompute.handle();
         }

         if (this.providerFetch.update(45000L)) {
            var1.getPathingBehavior().cancelEverything();
            this.save();
         }
      }
   }

   private void drawAnimation() {
      BlockPos var1 = this.load();
      if (var1 != null) {
         this.presetWrite = var1;
         this.colorMeasure = null;
         this.animate();
         IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
         var2.getCommandManager().execute("stop");
         var2.getSelectionManager().removeAllSelections();
         var2.getSelectionManager().addSelection(new BetterBlockPos(var1), new BetterBlockPos(var1));
         var2.getCommandManager().execute("sel cleararea");
      } else {
         this.presetWrite = null;
         this.colorMeasure = null;
         this.animate();
      }
   }

   private void encodePoint() {
      if (this.presetWrite != null && Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (!this.resolve(this.presetWrite)
            && !(Module.client.player.squaredDistanceTo(this.presetWrite.getX() + 0.5, this.presetWrite.getY() + 0.5, this.presetWrite.getZ() + 0.5) > 36.0)) {
            RotationAngles var1 = this.handle(Vec3d.ofCenter(this.presetWrite));
            RotationController.handle(var1, 65.0F, 65.0F, 65.0F, 65.0F, 2, 20, false);
            if (new RotationAngles(Module.client.player).handle(var1) > 6.0F) {
               if (Module.client.options != null) {
                  Module.client.options.attackKey.setPressed(false);
               }
            } else {
               BlockHitResult var2 = this.handle(this.presetWrite);
               if (var2 == null) {
                  this.animate();
               } else {
                  Module.client.options.attackKey.setPressed(true);
                  if (!this.presetWrite.equals(this.colorMeasure)) {
                     Module.client.interactionManager.attackBlock(this.presetWrite, var2.getSide());
                     this.colorMeasure = this.presetWrite;
                     this.vectorMatch.handle();
                  } else if (this.vectorMatch.update(45L)) {
                     Module.client.interactionManager.updateBlockBreakingProgress(this.presetWrite, var2.getSide());
                     Module.client.player.swingHand(Hand.MAIN_HAND);
                     this.vectorMatch.handle();
                  }
               }
            }
         } else {
            this.animate();
         }
      } else {
         this.animate();
      }
   }

   private BlockHitResult handle(BlockPos var1) {
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

      return this.process(var1) ? new BlockHitResult(Vec3d.ofCenter(var1), this.compute(var1), var1, false) : null;
   }

   private boolean process(BlockPos var1) {
      for (Direction var5 : Direction.values()) {
         if (this.resolve(var1.offset(var5))) {
            return true;
         }
      }

      return false;
   }

   private Direction compute(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos().subtract(Vec3d.ofCenter(var1));
      double var3 = Math.abs(var2.x);
      double var5 = Math.abs(var2.y);
      double var7 = Math.abs(var2.z);
      if (var5 >= var3 && var5 >= var7) {
         return var2.y > 0.0 ? Direction.UP : Direction.DOWN;
      } else if (var3 >= var7) {
         return var2.x > 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return var2.z > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private void animate() {
      if (Module.client.options != null) {
         Module.client.options.attackKey.setPressed(false);
      }

      this.colorMeasure = null;
   }

   private BlockPos load() {
      int var1 = this.writePreset();
      int var2 = this.measureColor();
      int var3 = this.scheduleAnimation();
      int var4 = this.scanRenderer();
      int var5 = this.buildSource();
      int var6 = this.collapseOutput();

      for (int var7 = var4; var7 >= var3; var7--) {
         BlockPos var8 = null;

         for (int var9 = var1; var9 <= var2; var9++) {
            for (int var10 = var5; var10 <= var6; var10++) {
               BlockPos var11 = new BlockPos(var9, var7, var10);
               Block var12 = Module.client.world.getBlockState(var11).getBlock();
               if ((var12 == Blocks.DIAMOND_ORE || var12 == Blocks.DEEPSLATE_DIAMOND_ORE)
                  && (var8 == null || Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var11)) < Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var8)))) {
                  var8 = var11;
               }
            }
         }

         if (var8 != null) {
            return var8;
         }
      }

      return null;
   }

   private boolean resolve(BlockPos var1) {
      Block var2 = Module.client.world.getBlockState(var1).getBlock();
      return var2 == Blocks.AIR || var2 == Blocks.CAVE_AIR || var2 == Blocks.VOID_AIR;
   }

   private void save() {
      if (this.profileDraw >= this.vectorPerform.size()) {
         this.profileDraw = 0;
      }

      String var1 = this.closeProvider();
      String var2 = this.vectorPerform.get(this.profileDraw);
      if (var1 != null && var2.equals(var1)) {
         this.profileDraw++;
         if (this.profileDraw >= this.vectorPerform.size()) {
            this.profileDraw = 0;
         }

         var2 = this.vectorPerform.get(this.profileDraw);
      }

      Module.client.player.networkHandler.sendChatCommand("an" + var2);
      this.profileDraw++;
      this.presetSave = AutoMine.Mode.CHANGING_ANARCHY;
      this.summary.handle();
   }

   private boolean submit() {
      String var1 = this.closeProvider();
      if (var1 == null) {
         this.handle("Укажите анархию для сброса.");
         return false;
      } else if (this.rendererScan == null) {
         this.handle("Установите сундук для сброса через бинд.");
         return false;
      } else {
         this.animate();
         this.indexBind.clear();
         Module.client.player.networkHandler.sendChatCommand("an" + var1);
         this.presetSave = AutoMine.Mode.TELEPORTING_TO_DROP;
         this.summary.handle();
         return true;
      }
   }

   private void unload() {
      if (this.rendererScan == null) {
         this.handle("Сундук для сброса не установлен.");
      } else {
         this.animate();
         this.indexBind.clear();
         IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
         var1.getSelectionManager().removeAllSelections();
         var1.getCommandManager().execute("stop");
         this.sourceBuild = this.update(this.rendererScan);
         this.presetSave = AutoMine.Mode.GOING_TO_DROP_CHEST;
         this.sourceSchedule = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.rendererScan));
         this.itemProject.handle();
         this.responseCompute.handle();
         this.providerFetch.handle();
      }
   }

   private void process(IBaritone var1) {
      if (this.rendererScan == null) {
         this.handle("Сундук для сброса не установлен.");
      } else {
         double var2 = Module.client.player.getPos().distanceTo(Vec3d.ofCenter(this.rendererScan));
         if (var2 <= 3.5 && this.execute(this.rendererScan)) {
            var1.getPathingBehavior().cancelEverything();
            this.presetSave = AutoMine.Mode.ROTATING_DROP_CHEST;
            this.itemProject.handle();
         } else {
            if (this.sourceBuild == null || this.responseCompute.update(10000L)) {
               this.sourceBuild = this.update(this.rendererScan);
            }

            BlockPos var4 = this.sourceBuild == null ? this.rendererScan : this.sourceBuild;
            int var5 = this.sourceBuild == null ? 3 : 1;
            if (!var1.getCustomGoalProcess().isActive() || this.responseCompute.update(2500L)) {
               var1.getCustomGoalProcess().setGoalAndPath(new GoalNear(var4, var5));
               this.responseCompute.handle();
            }

            if (this.sourceSchedule < 0.0 || var2 < this.sourceSchedule - 1.0) {
               this.sourceSchedule = var2;
               this.providerFetch.handle();
            }

            if (this.providerFetch.update(45000L)) {
               this.handle("Не удалось дойти до сундука для сброса.");
            }
         }
      }
   }

   private void fetch() {
      if (this.rendererScan != null && Module.client.player != null) {
         RotationAngles var1 = this.handle(Vec3d.ofCenter(this.rendererScan));
         RotationController.handle(var1, 35.0F, 35.0F, 35.0F, 35.0F, 20, 1, false);
         if (new RotationAngles(Module.client.player).handle(var1) < 5.0F && this.itemProject.update(150L)) {
            RotationController.instance = RotationController.Mode.IDLE;
            this.presetSave = AutoMine.Mode.OPENING_DROP_CHEST;
            this.itemProject.handle();
         }
      } else {
         this.handle("Сундук для сброса потерян.");
      }
   }

   private void measure() {
      if (this.rendererScan != null && Module.client.interactionManager != null) {
         this.checkFrame();
         if (this.itemProject.update(150L)) {
            BlockHitResult var1 = new BlockHitResult(Vec3d.ofCenter(this.rendererScan), Direction.UP, this.rendererScan, false);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
            this.presetSave = AutoMine.Mode.WAITING_FOR_DROP_GUI;
            this.itemProject.handle();
         }
      } else {
         this.handle("Сундук для сброса потерян.");
      }
   }

   private void handle(GenericContainerScreenHandler var1) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (!this.indexBind.isEmpty()) {
            if (this.matrixBlend.update(50L)) {
               this.indexBind.poll().run();
               this.matrixBlend.handle();
            }
         } else {
            int var2 = var1.slots.size() - 36;
            if (var2 <= 0) {
               this.handle("Открыт не контейнер для сброса.");
            } else if (this.computeResponse()) {
               this.handle(var1, var2);
            } else {
               this.process(var1, var2);
            }
         }
      }
   }

   private void handle(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         Slot var4 = (Slot)var1.slots.get(var3);
         int var5 = this.handle(var3, var2);
         AutoMine.DataRecord var6 = this.actionRead.getOrDefault(var5, configCollapse);
         if (!var4.hasStack()) {
            if (var6.count > 0) {
               if (!this.handle(var1, var2, var6, var3, 0)) {
                  this.handle("В сундуке нет предметов для восстановления раскладки.");
               }

               return;
            }
         } else {
            ItemStack var7 = var4.getStack();
            boolean var8 = var6.matches(var7);
            if (var6.count <= 0 || !var8) {
               if (!this.handle(var1, var2, var7)) {
                  this.handle("Сундук для сброса заполнен.");
                  return;
               } else {
                  int var11 = var3;
                  int var12 = var1.syncId;
                  this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var12, var11, 0, SlotActionType.QUICK_MOVE, Module.client.player));
                  return;
               }
            }

            if (var7.getCount() > var6.count) {
               int var9 = var7.getCount() - var6.count;
               int var10 = this.handle(var1, var2, var7, var9);
               if (var10 == -1) {
                  this.handle("Сундук для сброса заполнен.");
                  return;
               }

               this.handle(var1.syncId, var3, var10, var6.count, var9);
               return;
            }

            if (var7.getCount() < var6.count) {
               if (!this.handle(var1, var2, var6, var3, var7.getCount())) {
                  this.handle("В сундуке нет предметов для восстановления раскладки.");
               }

               return;
            }
         }
      }

      this.blendMatrix();
   }

   private void process(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         Slot var4 = (Slot)var1.slots.get(var3);
         if (var4.hasStack()) {
            ItemStack var5 = var4.getStack();
            int var6 = this.handle(var3, var2);
            int var7 = this.handle(var6, var5);
            int var8 = var5.getCount() - var7;
            if (var8 > 0) {
               if (var7 <= 0) {
                  if (!this.handle(var1, var2, var5)) {
                     this.handle("Сундук для сброса заполнен.");
                     return;
                  }

                  int var11 = var3;
                  int var10 = var1.syncId;
                  this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var10, var11, 0, SlotActionType.QUICK_MOVE, Module.client.player));
                  return;
               }

               int var9 = this.handle(var1, var2, var5, var8);
               if (var9 == -1) {
                  this.handle("Сундук для сброса заполнен.");
                  return;
               }

               this.handle(var1.syncId, var3, var9, var7, var8);
               return;
            }
         }
      }

      this.blendMatrix();
   }

   private void handle(int var1, int var2, int var3, int var4, int var5) {
      this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.PICKUP, Module.client.player));
      if (var4 <= var5) {
         for (int var6 = 0; var6 < var4; var6++) {
            this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var2, 1, SlotActionType.PICKUP, Module.client.player));
         }

         this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var3, 0, SlotActionType.PICKUP, Module.client.player));
      } else {
         for (int var7 = 0; var7 < var5; var7++) {
            this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var3, 1, SlotActionType.PICKUP, Module.client.player));
         }

         this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.PICKUP, Module.client.player));
      }
   }

   private boolean handle(GenericContainerScreenHandler var1, int var2, AutoMine.DataRecord var3, int var4, int var5) {
      int var6 = var3.count - var5;
      if (var6 <= 0) {
         return true;
      }

      int var7 = this.handle(var1, var2, var3);
      if (var7 == -1) {
         return false;
      }

      int var8 = ((Slot)var1.slots.get(var7)).getStack().getCount();
      int var9 = Math.min(var6, var8);
      this.process(var1.syncId, var7, var4, var8, var9);
      return true;
   }

   private void process(int var1, int var2, int var3, int var4, int var5) {
      this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.PICKUP, Module.client.player));
      if (var5 >= var4) {
         this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var3, 0, SlotActionType.PICKUP, Module.client.player));
      } else if (var5 <= var4 / 2) {
         for (int var6 = 0; var6 < var5; var6++) {
            this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var3, 1, SlotActionType.PICKUP, Module.client.player));
         }

         this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var2, 0, SlotActionType.PICKUP, Module.client.player));
      } else {
         int var8 = var4 - var5;

         for (int var7 = 0; var7 < var8; var7++) {
            this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var2, 1, SlotActionType.PICKUP, Module.client.player));
         }

         this.indexBind.add(() -> Module.client.interactionManager.clickSlot(var1, var3, 0, SlotActionType.PICKUP, Module.client.player));
      }
   }

   private int handle(GenericContainerScreenHandler var1, int var2, AutoMine.DataRecord var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         Slot var5 = (Slot)var1.slots.get(var4);
         if (var5.hasStack() && var3.matches(var5.getStack())) {
            return var4;
         }
      }

      return -1;
   }

   private void blendMatrix() {
      this.indexBind.clear();
      this.dataValidate = null;
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      this.presetSave = AutoMine.Mode.IDLE;
      this.sourceBuild = null;
      this.save();
   }

   private boolean matchVector() {
      return Module.client.player.getInventory().getEmptySlot() != -1 ? false : this.projectItem();
   }

   private boolean projectItem() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.getCount() > this.handle(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   private int handle(int var1, ItemStack var2) {
      AutoMine.DataRecord var3 = this.actionRead.get(var1);
      if (var3 != null) {
         return var3.matches(var2) ? Math.min(var3.count, var2.getCount()) : 0;
      } else {
         return 1;
      }
   }

   private int handle(int var1, int var2) {
      int var3 = var1 - var2;
      return var3 >= 27 ? var3 - 27 : var3 + 9;
   }

   private int handle(GenericContainerScreenHandler var1, int var2, ItemStack var3, int var4) {
      int var5 = -1;

      for (int var6 = 0; var6 < var2; var6++) {
         Slot var7 = (Slot)var1.slots.get(var6);
         if (!var7.hasStack()) {
            if (var5 == -1) {
               var5 = var6;
            }
         } else {
            ItemStack var8 = var7.getStack();
            if (this.handle(var3, var8) && var8.getCount() + var4 <= var8.getMaxCount()) {
               return var6;
            }
         }
      }

      return var5;
   }

   private boolean handle(GenericContainerScreenHandler var1, int var2, ItemStack var3) {
      for (int var4 = 0; var4 < var2; var4++) {
         Slot var5 = (Slot)var1.slots.get(var4);
         if (!var5.hasStack()) {
            return true;
         }

         ItemStack var6 = var5.getStack();
         if (this.handle(var3, var6) && var6.getCount() < var6.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(ItemStack var1, ItemStack var2) {
      return ItemStack.areItemsAndComponentsEqual(var1, var2);
   }

   public void refresh() {
      this.fetchProvider();
   }

   private boolean computeResponse() {
      return this.actionRead.size() >= 36;
   }

   private void fetchProvider() {
      if (Module.client.player == null) {
         ChatLogger.handle("§8[§6AutoMine§8] §cИгрок не загружен.");
      } else {
         this.actionRead.clear();
         StringBuilder var1 = new StringBuilder();
         Encoder var2 = Base64.getEncoder();

         for (int var3 = 0; var3 < 36; var3++) {
            ItemStack var4 = Module.client.player.getInventory().getStack(var3);
            String var5 = var4.isEmpty() ? "" : this.handle(var4);
            int var6 = var4.isEmpty() ? 0 : var4.getCount();
            this.actionRead.put(var3, new AutoMine.DataRecord(var5, var6));
            if (var3 > 0) {
               var1.append(';');
            }

            var1.append(var6).append(',').append(var2.encodeToString(var5.getBytes(StandardCharsets.UTF_8)));
         }

         this.previous.process(var1.toString());
         if (WildClient.instance.renderer != null) {
            WildClient.instance.renderer.compute();
         }

         ChatLogger.handle("§8[§6AutoMine§8] §aРаскладка инвентаря сохранена.");
      }
   }

   private void drawProfile() {
      this.actionRead.clear();
      String var1 = this.previous.compute();
      if (var1 != null && !var1.isBlank()) {
         String[] var2 = var1.split(";", -1);
         Decoder var3 = Base64.getDecoder();

         for (int var4 = 0; var4 < Math.min(36, var2.length); var4++) {
            String[] var5 = var2[var4].split(",", 2);
            if (var5.length == 2) {
               try {
                  int var6 = Integer.parseInt(var5[0]);
                  String var7 = new String(var3.decode(var5[1]), StandardCharsets.UTF_8);
                  this.actionRead.put(var4, new AutoMine.DataRecord(var7, var6));
               } catch (IllegalArgumentException var8) {
               }
            }
         }
      }
   }

   private String handle(ItemStack var1) {
      return var1.getItem().toString() + "|" + var1.getName().getString();
   }

   private boolean performVector() {
      if (Module.client.player == null) {
         return false;
      }

      double var1 = Module.client.player.getX() - eventAttach.getX();
      double var3 = Module.client.player.getZ() - eventAttach.getZ();
      return Math.sqrt(var1 * var1 + var3 * var3) < 300.0;
   }

   private boolean attachEvent() {
      if (Module.client.player == null) {
         return false;
      }

      BlockPos var1 = Module.client.player.getBlockPos();
      return var1.getX() >= this.writePreset() - 4.0
         && var1.getX() <= this.measureColor() + 4.0
         && var1.getY() >= this.scheduleAnimation() - 6
         && var1.getY() <= this.scanRenderer() + 8
         && var1.getZ() >= this.buildSource() - 4.0
         && var1.getZ() <= this.collapseOutput() + 4.0;
   }

   private boolean readServer() {
      return this.load() != null;
   }

   private BlockPos advancePosition() {
      if (Module.client.world != null && Module.client.player != null) {
         ArrayList<BlockPos> var1 = new ArrayList<>();

         for (int var2 = this.scheduleAnimation() - 2; var2 <= this.scanRenderer() + 2; var2++) {
            for (int var3 = this.writePreset() - 3; var3 <= this.measureColor() + 3; var3++) {
               for (int var4 = this.buildSource() - 3; var4 <= this.collapseOutput() + 3; var4++) {
                  BlockPos var5 = new BlockPos(var3, var2, var4);
                  if (this.apply(var5)) {
                     var1.add(var5);
                  }
               }
            }
         }

         BlockPos var6 = this.convertWindow();
         return var1.stream()
            .min(
               Comparator.<BlockPos>comparingDouble(var1x -> Vec3d.ofCenter(var1x).squaredDistanceTo(Vec3d.ofCenter(var6)))
                  .thenComparingDouble(var0 -> Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var0)))
            )
            .orElse(var6);
      } else {
         return null;
      }
   }

   private BlockPos update(BlockPos var1) {
      if (Module.client.world != null && Module.client.player != null && var1 != null) {
         ArrayList<BlockPos> var2 = new ArrayList<>();

         for (int var3 = -1; var3 <= 1; var3++) {
            for (int var4 = 1; var4 <= 3; var4++) {
               for (int var5 = -var4; var5 <= var4; var5++) {
                  for (int var6 = -var4; var6 <= var4; var6++) {
                     if (Math.max(Math.abs(var5), Math.abs(var6)) == var4) {
                        var2.add(var1.add(var5, var3, var6));
                     }
                  }
               }
            }
         }

         var2.sort(Comparator.comparingDouble(var0 -> Module.client.player.getPos().squaredDistanceTo(Vec3d.ofCenter(var0))));
         BlockPos var7 = null;

         for (BlockPos var9 : var2) {
            if (this.apply(var9)) {
               if (this.handle(var9, var1)) {
                  return var9;
               }

               if (var7 == null) {
                  var7 = var9;
               }
            }
         }

         return var7;
      } else {
         return null;
      }
   }

   private boolean apply(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      BlockState var2 = Module.client.world.getBlockState(var1);
      BlockState var3 = Module.client.world.getBlockState(var1.up());
      BlockState var4 = Module.client.world.getBlockState(var1.down());
      return var2.getCollisionShape(Module.client.world, var1).isEmpty()
         && var3.getCollisionShape(Module.client.world, var1.up()).isEmpty()
         && !var4.getCollisionShape(Module.client.world, var1.down()).isEmpty();
   }

   private boolean handle(BlockPos var1, BlockPos var2) {
      Vec3d var3 = Vec3d.ofCenter(var1).add(0.0, 1.2, 0.0);
      Vec3d var4 = Vec3d.ofCenter(var2);
      BlockHitResult var5 = Module.client.world.raycast(new RaycastContext(var3, var4, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var5.getType() == Type.MISS || var5.getBlockPos().equals(var2);
   }

   private boolean execute(BlockPos var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      Vec3d var3 = Vec3d.ofCenter(var1);
      BlockHitResult var4 = Module.client.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var4.getType() == Type.MISS || var4.getBlockPos().equals(var1);
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

   private void checkFrame() {
      int var1 = Module.client.player.getInventory().getSelectedSlot();
      ItemStack var2 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var1);
      if (var2.getItem() == Items.TRIPWIRE_HOOK || var2.getName().getString().contains("[★]")) {
         for (int var3 = 0; var3 < 9; var3++) {
            ItemStack var4 = (ItemStack)Module.client.player.getInventory().getMainStacks().get(var3);
            if (var4.isEmpty() || var4.getItem() != Items.TRIPWIRE_HOOK && !var4.getName().getString().contains("[★]")) {
               Module.client.player.getInventory().setSelectedSlot(var3);
               this.itemProject.handle();
               break;
            }
         }
      }
   }

   private boolean prepare(BlockPos var1) {
      return Module.client.world != null && var1 != null
         ? Module.client.world.getBlockEntity(var1) instanceof ChestBlockEntity
            || Module.client.world.getBlockEntity(var1) instanceof BarrelBlockEntity
            || Module.client.world.getBlockEntity(var1) instanceof ShulkerBoxBlockEntity
         : false;
   }

   private void check(BlockPos var1) {
      this.rendererScan = var1.toImmutable();
      this.sourceBuild = this.update(this.rendererScan);
      this.latest.process(var1.getX() + "," + var1.getY() + "," + var1.getZ());
      if (WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   private void collectModule() {
      String var1 = this.latest.compute();
      if (var1 != null && !var1.isBlank()) {
         String[] var2 = var1.split(",");
         if (var2.length == 3) {
            try {
               this.rendererScan = new BlockPos(Integer.parseInt(var2[0]), Integer.parseInt(var2[1]), Integer.parseInt(var2[2]));
            } catch (NumberFormatException var4) {
               this.rendererScan = null;
            }
         }
      }
   }

   private String closeProvider() {
      String var1 = this.source.compute();
      if (var1 == null) {
         return null;
      }

      String var2 = var1.trim()
         .toLowerCase()
         .replace("/", "")
         .replace("anarchy", "")
         .replace("анархия", "")
         .replace("an", "")
         .replace("аn", "")
         .replace(" ", "");
      return var2.isBlank() ? null : var2;
   }

   private void handle(String var1) {
      ChatLogger.handle("§8[§6AutoMine§8] §c" + var1);
      this.indexBind.clear();
      this.dataValidate = null;
      this.animate();
      this.presetSave = AutoMine.Mode.IDLE;
      if (Module.client.player != null) {
         Module.client.player.closeHandledScreen();
      }

      if (this.enabled) {
         this.toggle();
      }
   }

   private GenericContainerScreen savePreset() {
      GenericContainerScreen var1 = ContainerScreenPolicy.handle(Module.client, this.dataValidate, GenericContainerScreen.class);
      if (var1 == null) {
         this.dataValidate = null;
      }

      return var1;
   }

   private BlockPos convertWindow() {
      return new BlockPos((this.writePreset() + this.measureColor()) / 2, this.scheduleAnimation(), (this.buildSource() + this.collapseOutput()) / 2);
   }

   private int writePreset() {
      return Math.min(eventAttach.getX(), serverRead.getX());
   }

   private int measureColor() {
      return Math.max(eventAttach.getX(), serverRead.getX());
   }

   private int scheduleAnimation() {
      return Math.min(eventAttach.getY(), serverRead.getY());
   }

   private int scanRenderer() {
      return Math.max(eventAttach.getY(), serverRead.getY());
   }

   private int buildSource() {
      return Math.min(eventAttach.getZ(), serverRead.getZ());
   }

   private int collapseOutput() {
      return Math.max(eventAttach.getZ(), serverRead.getZ());
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         if (this.rendererScan != null || this.presetWrite != null) {
            Immediate var2 = WorldVertexBuffer.handle();

            try {
               Vec3d var3 = Module.client.gameRenderer.getCamera().getPos();
               Matrix4f var4 = var1.compute().peek().getPositionMatrix();
               VertexConsumer var5 = var2.getBuffer(keyFilter);
               if (this.rendererScan != null) {
                  this.handle(var5, var4, this.rendererScan, var3, new Color(150, 50, 255, 120), new Color(150, 50, 255, 0));
               }

               if (this.presetWrite != null && !this.resolve(this.presetWrite)) {
                  this.handle(var5, var4, this.presetWrite, var3, new Color(0, 180, 255, 130), new Color(0, 180, 255, 0));
               }
            } finally {
               WorldVertexBuffer.process();
            }
         }
      }
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

   record DataRecord(String key, int count) {

      boolean matches(ItemStack var1) {
         return !var1.isEmpty() && this.count > 0 && this.key.equals(var1.getItem().toString() + "|" + var1.getName().getString());
      }
   }

   enum Mode {
      IDLE,
      WAITING_FOR_TP,
      GOING_TO_MINE,
      MINING,
      TELEPORTING_TO_DROP,
      GOING_TO_DROP_CHEST,
      ROTATING_DROP_CHEST,
      OPENING_DROP_CHEST,
      WAITING_FOR_DROP_GUI,
      DROPPING,
      CHANGING_ANARCHY;
   }
}
