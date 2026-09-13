package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.LightType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.network.TelegramApi;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.render.DualLayerBoxVertexEmitter;
import ru.wild.util.render.PackedColor;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "BaseFinder", category = ModuleCategory.Misc, description = "Ищет базы, пишет в ТГ и копает")
public class BaseFinder extends Module {
   public final ChoiceSetting source = new ChoiceSetting(
      "Блоки",
      new BooleanSetting("Сундуки", true),
      new BooleanSetting("Шалкера", true),
      new BooleanSetting("Бочки", true),
      new BooleanSetting("Наковальни", true),
      new BooleanSetting("Печка", false),
      new BooleanSetting("Эндер сундук", true)
   );
   public final BooleanSetting target = new BooleanSetting("Искать вагонетки", true);
   public final BooleanSetting pending = new BooleanSetting("Искать крестьян/аксолотлей", true);
   public final BooleanSetting previous = new BooleanSetting("Авто-туннель (#)", true);
   public final BooleanSetting latest = new BooleanSetting("Копать к находке", true).handle(() -> !this.previous.compute());
   public final BooleanSetting summary = new BooleanSetting("Выкл при игроке", true);
   public final BooleanSetting matrixBlend = new BooleanSetting("Проверки на свет", false);
   public final BooleanSetting vectorMatch = new BooleanSetting("Избегать мобов", false);
   public final BooleanSetting itemProject = new BooleanSetting("Рендерить находки", true);
   public final BooleanSetting responseCompute = new BooleanSetting("Уведомления в ТГ", false);
   public final NumberSetting providerFetch = new NumberSetting("Радиус чанков", 4.0F, 1.0F, 8.0F, 1.0F, true);
   public final ModeSetting profileDraw = new ModeSetting("Режим работы", "Tonnel", "Tonnel", "FunTime", "HolyWorld", "Поиск приватом");
   public final ModeSetting vectorPerform = new ModeSetting("Блок привата", "Изумрудная руда", "Изумрудная руда", "Алмазный блок")
      .handle(() -> !this.profileDraw.process("HolyWorld"));
   private final Set<BlockPos> serverRead = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final Map<BlockPos, Object> positionAdvance = new ConcurrentHashMap<>();
   private final Set<Integer> frameCheck = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static final int moduleCollect = 8;
   private static final int providerClose = 8;
   private static final int presetSave = 2;
   private static final int windowConvert = 2;
   private static final int presetWrite = 8192;
   private static final int colorMeasure = 100;
   private static final int animationSchedule = 3;
   private static final int rendererScan = 8;
   private static final int sourceBuild = 50;
   private static final int outputCollapse = 160;
   private static final double profileInvoke = 16384.0;
   private static final int sourceSchedule = 80;
   private int timerRender = 0;
   private int scaleSave = 0;
   private boolean colorCompute = false;
   private int scaleAdapt = 0;
   private int textureRun = 0;
   private BlockPos indexBind = null;
   private BlockPos actionRead = null;
   private Direction configCollapse = null;
   private final Map<Long, Integer> dataValidate = new ConcurrentHashMap<>();
   private BaseFinder.Mode scaleRender = BaseFinder.Mode.CHECK_SUPPLIES;
   private int clientRefresh = 0;
   private int keyFilter = 100;
   private BlockPos requestAdapt = null;
   private BlockPos timerMeasure = null;
   private BlockPos vectorEncode = null;
   private Direction requestReceive = null;
   private Direction windowProcess = null;
   private Direction packetSave = null;
   private BlockPos entryAnimate = null;
   private int playerCollect = 30;
   private int stateApply = -1;
   private int matrixFilter = 0;
   public static final Map<Object, Integer> eventAttach = new HashMap<>();
   private static final int layerSample = 1024;
   private static final RenderPipeline worldSend = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "block_esp_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer targetWrite = RenderLayer.of("block_esp_box", 1024, false, true, worldSend, MultiPhaseParameters.builder().build(false));

   public BaseFinder() {
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform
      );
      eventAttach.put(BlockEntityType.CHEST, PackedColor.update(new Color(255, 194, 84).getRGB(), 100));
      eventAttach.put(BlockEntityType.TRAPPED_CHEST, PackedColor.update(new Color(143, 109, 62).getRGB(), 100));
      eventAttach.put(BlockEntityType.ENDER_CHEST, PackedColor.update(new Color(153, 49, 238).getRGB(), 100));
      eventAttach.put(BlockEntityType.BARREL, PackedColor.update(new Color(250, 225, 62).getRGB(), 100));
      eventAttach.put(BlockEntityType.FURNACE, PackedColor.update(new Color(115, 115, 115).getRGB(), 100));
      eventAttach.put(BlockEntityType.SHULKER_BOX, PackedColor.update(new Color(246, 123, 123).getRGB(), 100));
      eventAttach.put(ChestMinecartEntity.class, PackedColor.update(new Color(255, 100, 0).getRGB(), 100));
      eventAttach.put(HopperMinecartEntity.class, PackedColor.update(new Color(100, 100, 100).getRGB(), 100));
      eventAttach.put(VillagerEntity.class, PackedColor.update(new Color(139, 90, 60).getRGB(), 100));
      eventAttach.put(AxolotlEntity.class, PackedColor.update(new Color(255, 182, 193).getRGB(), 100));
      this.checkFrame();
   }

   @Override
   public void handle() {
      ViewRotationCoordinator.instance = false;
      super.handle();
      if (this.profileDraw.process("HolyWorld")) {
         String var1 = this.vectorPerform.process("Алмазный блок") ? "алмазный блок" : "изумрудная руда";
         this.process("§eНужна кирка на шёлк и " + var1);
      } else if (this.profileDraw.process("FunTime") || this.profileDraw.process("Поиск приватом")) {
         this.process("§eНужна кирка на шёлк и 2 изумрудной руды");
      }

      this.serverRead.clear();
      this.positionAdvance.clear();
      this.frameCheck.clear();
      this.timerRender = 0;
      this.scaleSave = 0;
      this.colorCompute = false;
      this.scaleAdapt = 0;
      this.textureRun = 0;
      this.indexBind = null;
      this.actionRead = null;
      this.dataValidate.clear();
      this.scaleRender = BaseFinder.Mode.CHECK_SUPPLIES;
      this.clientRefresh = 0;
      this.keyFilter = 100;
      this.requestAdapt = null;
      this.timerMeasure = null;
      this.vectorEncode = null;
      this.requestReceive = null;
      this.windowProcess = null;
      this.packetSave = null;
      this.entryAnimate = null;
      this.stateApply = -1;
      this.matrixFilter = 0;
      if (Module.client.player != null) {
         if (this.profileDraw.process("Tonnel") && this.previous.compute()) {
            this.tick();
            this.encodePoint();
         }

         if (this.responseCompute.compute()) {
            if (!TelegramApi.handle()) {
               this.process("§cВнимание! Telegram не настроен. Используйте .tapi");
            } else {
               this.process("§aУведомления в Telegram включены.");
            }
         }
      }
   }

   @Override
   public void process() {
      ViewRotationCoordinator.instance = false;
      super.process();
      this.submit();
      if (Module.client.player != null && this.previous.compute()) {
         this.update("stop");
      }

      this.measure();
      this.colorCompute = false;
      this.actionRead = null;
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.world != null && Module.client.player != null) {
         this.scaleAdapt++;
         if (this.summary.compute()) {
            this.performVector();
            if (!this.enabled) {
               return;
            }
         }

         if (this.render() || this.profileDraw.process("Tonnel")) {
            this.refresh();
         }

         if (this.timerRender++ >= 10) {
            this.timerRender = 0;
            if (!this.vectorMatch.compute() || !this.advancePosition()) {
               this.attachEvent();
               if (this.target.compute() || this.pending.compute()) {
                  this.readServer();
               }
            }
         }
      }
   }

   private void refresh() {
      if (this.clientRefresh > 0) {
         this.clientRefresh--;
      } else {
         switch (this.scaleRender) {
            case CHECK_SUPPLIES:
               if (this.render() && this.fetchProvider() == -1) {
                  return;
               }

               this.tick();
               this.encodePoint();
               this.scaleRender = BaseFinder.Mode.TUNNELING;
               break;
            case TUNNELING:
               if (Module.client.player.isInLava()) {
                  this.measure();
                  this.submit();
                  this.process("§cПровалился в лаву! Экстренная остановка.");
                  this.toggle();
                  return;
               }

               if (this.load()) {
                  if (this.previous.compute()) {
                     this.unload();
                  } else {
                     this.handle(this.drawAnimation());
                  }
               } else if (this.resolve(this.drawAnimation())) {
                  if (this.previous.compute()) {
                     this.submit();
                  }

                  this.measure();
                  this.matchVector();
               } else if (this.compute(this.drawAnimation())) {
                  if (this.previous.compute()) {
                     this.submit();
                  }

                  this.measure();
                  this.handle("§6Лава впереди. Смещаюсь в сторону.");
               } else {
                  if (this.previous.compute()) {
                     this.unload();
                     this.measure();
                  } else {
                     if (this.save()) {
                        this.measure();
                        break;
                     }

                     this.fetch();
                  }

                  if (this.blendMatrix() >= this.playerCollect) {
                     if (this.previous.compute()) {
                        this.submit();
                     }

                     this.measure();
                     this.scaleRender = BaseFinder.Mode.STOPPING;
                     this.clientRefresh = 10;
                  }
               }
               break;
            case BORDER_SHIFTING:
               if (this.packetSave != null && this.entryAnimate != null) {
                  if (Module.client.player.isInLava()) {
                     this.submit();
                     this.measure();
                     this.process("§cПопал в лаву при смещении! Экстренная остановка.");
                     this.toggle();
                     return;
                  }

                  if (this.compute(this.drawAnimation())) {
                     if (this.previous.compute()) {
                        this.submit();
                     }

                     this.measure();
                     this.handle("§6Лава на пути смещения. Меняю линию.");
                  } else {
                     if (this.previous.compute()) {
                        this.process(this.packetSave);
                        this.measure();
                     } else {
                        if (this.save()) {
                           this.measure();
                           break;
                        }

                        this.fetch();
                     }

                     if (this.handle(this.entryAnimate, this.packetSave) >= this.keyFilter) {
                        if (this.previous.compute()) {
                           this.submit();
                        }

                        this.measure();
                        this.requestReceive = this.windowProcess;
                        this.vectorEncode = Module.client.player.getBlockPos().toImmutable();
                        this.packetSave = null;
                        this.entryAnimate = null;
                        this.encodePoint();
                        this.scaleRender = BaseFinder.Mode.TUNNELING;
                        this.clientRefresh = 6;
                     }
                  }
               } else {
                  this.scaleRender = BaseFinder.Mode.RESUMING;
                  this.clientRefresh = 2;
               }
               break;
            case STOPPING:
               this.measure();
               this.stateApply = Module.client.player.getInventory().getSelectedSlot();
               if (!this.render()) {
                  this.encodePoint();
                  this.scaleRender = BaseFinder.Mode.TUNNELING;
               } else if (this.fetchProvider() != -1) {
                  this.matrixFilter = 0;
                  this.scaleRender = BaseFinder.Mode.DIGGING_SPOT;
                  this.clientRefresh = 5;
               } else {
                  this.process("§cРуда закончилась! Жду пополнения...");
                  this.scaleRender = BaseFinder.Mode.CHECK_SUPPLIES;
               }
               break;
            case DIGGING_SPOT:
               this.measure();
               int var1 = this.drawProfile();
               if (var1 != -1 && Module.client.player.getInventory().getSelectedSlot() != var1) {
                  Module.client.player.getInventory().setSelectedSlot(var1);
               }

               Direction var2 = this.drawAnimation();
               BlockPos var3 = Module.client.player.getBlockPos();

               for (int var15 = 1; var15 <= 2; var15++) {
                  BlockPos var16 = var3.offset(var2, var15);
                  BlockPos var17 = var16.up();
                  BlockPos var18 = !Module.client.world.getBlockState(var16).isAir() ? var16 : (!Module.client.world.getBlockState(var17).isAir() ? var17 : null);
                  if (var18 != null) {
                     this.handle(Vec3d.ofCenter(var18));
                     Module.client.interactionManager.updateBlockBreakingProgress(var18, var2.getOpposite());
                     Module.client.player.swingHand(Hand.MAIN_HAND);
                     return;
                  }
               }

               this.scaleRender = BaseFinder.Mode.PLACING;
               this.clientRefresh = 5;
               break;
            case PLACING:
               this.measure();
               int var4 = this.fetchProvider();
               if (var4 != -1) {
                  Module.client.player.getInventory().setSelectedSlot(var4);
               }

               Direction var5 = this.drawAnimation();
               BlockPos var6 = Module.client.player.getBlockPos();
               BlockPos var7 = var6.offset(var5);
               BlockPos var8 = var6.offset(var5, 2);
               boolean var9 = false;
               BlockPos[] var10 = new BlockPos[]{
                  var7.down(),
                  var8.down(),
                  var6.offset(var5.rotateYCounterclockwise()).up(),
                  var6.offset(var5.rotateYClockwise()).up(),
                  var6.offset(var5.rotateYCounterclockwise()),
                  var6.offset(var5.rotateYClockwise()),
                  var7.up(),
                  var7
               };

               for (BlockPos var14 : var10) {
                  if (this.handle(var14)) {
                     this.timerMeasure = var14;
                     var9 = true;
                     break;
                  }
               }

               if (var9) {
                  this.scaleRender = BaseFinder.Mode.WAITING_CHAT;
                  this.clientRefresh = 10;
               } else if (this.matrixFilter++ < 1) {
                  this.scaleRender = BaseFinder.Mode.DIGGING_SPOT;
                  this.clientRefresh = 5;
               } else {
                  this.process("§7Некуда поставить блок. Пропуск.");
                  if (this.stateApply != -1) {
                     Module.client.player.getInventory().setSelectedSlot(this.stateApply);
                  }

                  this.scaleRender = BaseFinder.Mode.RESUMING;
                  this.clientRefresh = 5;
               }
               break;
            case WAITING_CHAT:
               this.measure();
               this.scaleRender = BaseFinder.Mode.BREAKING;
               break;
            case BREAKING:
               this.measure();
               if (this.timerMeasure != null) {
                  int var11 = this.drawProfile();
                  if (var11 != -1 && Module.client.player.getInventory().getSelectedSlot() != var11) {
                     Module.client.player.getInventory().setSelectedSlot(var11);
                  }

                  if (!Module.client.world.getBlockState(this.timerMeasure).isAir()) {
                     this.handle(Vec3d.ofCenter(this.timerMeasure));
                     Module.client.interactionManager.updateBlockBreakingProgress(this.timerMeasure, Direction.UP);
                     Module.client.player.swingHand(Hand.MAIN_HAND);
                     return;
                  }
               }

               this.scaleRender = BaseFinder.Mode.RESUMING;
               this.clientRefresh = 5;
               break;
            case RESUMING:
               this.encodePoint();
               this.scaleRender = BaseFinder.Mode.TUNNELING;
         }
      }
   }

   private void handle(Vec3d var1) {
      double var2 = var1.x - Module.client.player.getX();
      double var4 = var1.y - Module.client.player.getEyeY();
      double var6 = var1.z - Module.client.player.getZ();
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      float var10 = (float)(Math.toDegrees(Math.atan2(var6, var2)) - 90.0);
      float var11 = (float)Math.toDegrees(-Math.atan2(var4, var8));
      Module.client.player.setYaw(var10);
      Module.client.player.setPitch(var11);
   }

   private boolean handle(BlockPos var1) {
      if (!Module.client.world.getBlockState(var1).isAir()) {
         return false;
      }

      for (Direction var5 : Direction.values()) {
         BlockPos var6 = var1.offset(var5);
         if (!Module.client.world.getBlockState(var6).isAir()) {
            Direction var7 = var5.getOpposite();
            Vec3d var8 = new Vec3d(
               var6.getX() + 0.5 + var7.getOffsetX() * 0.5, var6.getY() + 0.5 + var7.getOffsetY() * 0.5, var6.getZ() + 0.5 + var7.getOffsetZ() * 0.5
            );
            BlockHitResult var9 = new BlockHitResult(var8, var7, var6, false);
            this.handle(var8);
            Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var9);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            return true;
         }
      }

      return false;
   }

   private boolean render() {
      return this.profileDraw.process("FunTime") || this.profileDraw.process("HolyWorld") || this.profileDraw.process("Поиск приватом");
   }

   private void tick() {
      if (Module.client.player != null) {
         if (this.vectorEncode == null) {
            this.vectorEncode = Module.client.player.getBlockPos().toImmutable();
         }

         if (this.requestReceive == null || !this.requestReceive.getAxis().isHorizontal()) {
            this.requestReceive = Module.client.player.getHorizontalFacing();
         }

         if (this.windowProcess == null || !this.windowProcess.getAxis().isHorizontal()) {
            this.windowProcess = this.requestReceive;
         }
      }
   }

   private Direction drawAnimation() {
      if (this.requestReceive == null || !this.requestReceive.getAxis().isHorizontal()) {
         this.tick();
      }

      return this.requestReceive != null ? this.requestReceive : Direction.NORTH;
   }

   private void encodePoint() {
      if (Module.client.player != null) {
         this.tick();
         this.handle(this.drawAnimation());
         this.requestAdapt = Module.client.player.getBlockPos();
         this.playerCollect = ThreadLocalRandom.current().nextInt(20, 30);
      }
   }

   private void animate() {
      this.measure();
      this.encodePoint();
      this.clientRefresh = 8;
   }

   private boolean load() {
      if (Module.client.player != null && this.vectorEncode != null && this.requestReceive != null) {
         BlockPos var1 = Module.client.player.getBlockPos();

         return switch (this.requestReceive.getAxis()) {
            case X -> Math.abs(var1.getZ() - this.vectorEncode.getZ()) > 1;
            case Z -> Math.abs(var1.getX() - this.vectorEncode.getX()) > 1;
            default -> false;
         };
      } else {
         return false;
      }
   }

   private void handle(Direction var1) {
      if (Module.client.player != null && var1 != null) {
         this.handle(Vec3d.ofCenter(Module.client.player.getBlockPos().offset(var1, 4)));
      }
   }

   private boolean save() {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         int var1 = this.drawProfile();
         if (var1 != -1 && Module.client.player.getInventory().getSelectedSlot() != var1) {
            Module.client.player.getInventory().setSelectedSlot(var1);
         }

         Direction var2 = this.drawAnimation();
         BlockPos var3 = Module.client.player.getBlockPos();
         BlockPos var4 = var3.offset(var2);
         BlockPos var5 = var4.up();
         BlockPos var6 = !Module.client.world.getBlockState(var4).isAir() ? var4 : (!Module.client.world.getBlockState(var5).isAir() ? var5 : null);
         if (var6 == null) {
            return false;
         }

         this.handle(Vec3d.ofCenter(var6));
         Module.client.interactionManager.updateBlockBreakingProgress(var6, var2.getOpposite());
         Module.client.player.swingHand(Hand.MAIN_HAND);
         return true;
      } else {
         return false;
      }
   }

   private void submit() {
      try {
         BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      } catch (Throwable var2) {
      }

      this.actionRead = null;
      this.configCollapse = null;
   }

   private void unload() {
      this.process(this.drawAnimation());
   }

   private void process(Direction var1) {
      if (Module.client.player != null && Module.client.world != null && var1 != null) {
         IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
         ViewRotationCoordinator.instance = false;
         if (this.actionRead == null || this.configCollapse != var1 || Module.client.player.getBlockPos().getManhattanDistance(this.actionRead) <= 2) {
            BlockPos var3 = Module.client.player.getBlockPos();
            BlockPos var4 = new BlockPos(var3.getX() + var1.getOffsetX() * 8, var3.getY(), var3.getZ() + var1.getOffsetZ() * 8);
            this.actionRead = var4;
            this.configCollapse = var1;

            try {
               var2.getCustomGoalProcess().setGoalAndPath(new GoalBlock(var4));
            } catch (Throwable var6) {
            }
         }
      }
   }

   private boolean compute(Direction var1) {
      if (Module.client.player != null && Module.client.world != null && var1 != null) {
         BlockPos var2 = Module.client.player.getBlockPos();

         for (int var3 = 0; var3 <= 3; var3++) {
            BlockPos var4 = var2.offset(var1, var3);
            BlockPos var5 = var4.up();
            BlockPos var6 = var4.down();
            if (this.process(var4) || this.process(var5) || this.process(var6)) {
               return true;
            }

            for (Direction var10 : new Direction[]{var1.rotateYCounterclockwise(), var1.rotateYClockwise()}) {
               if (this.process(var4.offset(var10)) || this.process(var5.offset(var10)) || this.process(var6.offset(var10))) {
                  return true;
               }
            }
         }

         return Module.client.player.isInLava();
      } else {
         return false;
      }
   }

   private boolean process(BlockPos var1) {
      return Module.client.world != null && var1 != null
         ? Module.client.world.getBlockState(var1).isOf(Blocks.LAVA)
            || Module.client.world.getFluidState(var1).isOf(Fluids.LAVA)
            || Module.client.world.getFluidState(var1).isOf(Fluids.FLOWING_LAVA)
         : false;
   }

   private void fetch() {
      if (Module.client.player != null && Module.client.options != null) {
         this.handle(this.drawAnimation());
         Module.client.options.forwardKey.setPressed(true);
         Module.client.options.backKey.setPressed(false);
         Module.client.options.leftKey.setPressed(false);
         Module.client.options.rightKey.setPressed(false);
         Module.client.options.sprintKey.setPressed(true);
      }
   }

   private void measure() {
      if (Module.client.options != null) {
         Module.client.options.forwardKey.setPressed(false);
         Module.client.options.backKey.setPressed(false);
         Module.client.options.leftKey.setPressed(false);
         Module.client.options.rightKey.setPressed(false);
         Module.client.options.sprintKey.setPressed(false);
      }
   }

   private int blendMatrix() {
      return Module.client.player != null && this.requestAdapt != null && this.requestReceive != null ? this.handle(this.requestAdapt, this.requestReceive) : 0;
   }

   private int handle(BlockPos var1, Direction var2) {
      if (Module.client.player != null && var1 != null && var2 != null) {
         BlockPos var3 = Module.client.player.getBlockPos();

         return switch (var2) {
            case EAST -> var3.getX() - var1.getX();
            case WEST -> var1.getX() - var3.getX();
            case SOUTH -> var3.getZ() - var1.getZ();
            case NORTH -> var1.getZ() - var3.getZ();
            default -> var1.getManhattanDistance(var3);
         };
      } else {
         return 0;
      }
   }

   private boolean resolve(Direction var1) {
      if (Module.client.world != null && Module.client.player != null && var1 != null) {
         WorldBorder var2 = Module.client.world.getWorldBorder();
         BlockPos var3 = Module.client.player.getBlockPos();
         int var4 = var3.getX();
         int var5 = var3.getZ();

         return switch (var1) {
            case EAST -> var2.getBoundEast() - var4 <= 100.0;
            case WEST -> var4 - var2.getBoundWest() <= 100.0;
            case SOUTH -> var2.getBoundSouth() - var5 <= 100.0;
            case NORTH -> var5 - var2.getBoundNorth() <= 100.0;
            default -> false;
         };
      } else {
         return false;
      }
   }

   private void matchVector() {
      Direction var1 = this.projectItem();
      if (var1 == null) {
         this.process("§cГраница мира слишком близко. Нет безопасного смещения.");
         this.toggle();
      } else {
         this.packetSave = var1;
         this.entryAnimate = Module.client.player.getBlockPos().toImmutable();
         this.requestReceive = var1;
         this.vectorEncode = this.entryAnimate;
         this.keyFilter = 100;
         this.process("§eГраница мира рядом. Смещаюсь на 100 блоков " + this.apply(var1) + ".");
         this.encodePoint();
         this.scaleRender = BaseFinder.Mode.BORDER_SHIFTING;
         this.clientRefresh = 4;
      }
   }

   private void handle(String var1) {
      Direction var2 = this.computeResponse();
      if (var2 == null) {
         this.process("§cБезопасного смещения нет. Останавливаюсь.");
         this.toggle();
      } else {
         this.packetSave = var2;
         this.entryAnimate = Module.client.player.getBlockPos().toImmutable();
         this.requestReceive = var2;
         this.vectorEncode = this.entryAnimate;
         this.keyFilter = 5;
         this.process(var1);
         this.encodePoint();
         this.scaleRender = BaseFinder.Mode.BORDER_SHIFTING;
         this.clientRefresh = 4;
      }
   }

   private Direction projectItem() {
      Direction var1 = this.windowProcess != null ? this.windowProcess : this.drawAnimation();
      Direction var2 = var1.rotateYCounterclockwise();
      Direction var3 = var1.rotateYClockwise();
      int var4 = this.update(var2);
      int var5 = this.update(var3);
      if (var4 >= 100 && var5 >= 100) {
         return var4 >= var5 ? var2 : var3;
      } else if (var4 >= 100) {
         return var2;
      } else if (var5 >= 100) {
         return var3;
      } else {
         return var4 >= var5 && var4 > 0 ? var2 : (var5 > 0 ? var3 : null);
      }
   }

   private Direction computeResponse() {
      Direction var1 = this.windowProcess != null ? this.windowProcess : this.drawAnimation();
      Direction var2 = var1.rotateYCounterclockwise();
      Direction var3 = var1.rotateYClockwise();
      boolean var4 = !this.compute(var2) && this.update(var2) > 2;
      boolean var5 = !this.compute(var3) && this.update(var3) > 2;
      if (var4 && var5) {
         return this.update(var2) >= this.update(var3) ? var2 : var3;
      } else if (var4) {
         return var2;
      } else {
         return var5 ? var3 : null;
      }
   }

   private int update(Direction var1) {
      if (Module.client.world != null && Module.client.player != null && var1 != null) {
         WorldBorder var2 = Module.client.world.getWorldBorder();
         BlockPos var3 = Module.client.player.getBlockPos();
         int var4 = var3.getX();
         int var5 = var3.getZ();

         return switch (var1) {
            case EAST -> (int)Math.floor(var2.getBoundEast() - var4);
            case WEST -> (int)Math.floor(var4 - var2.getBoundWest());
            case SOUTH -> (int)Math.floor(var2.getBoundSouth() - var5);
            case NORTH -> (int)Math.floor(var5 - var2.getBoundNorth());
            default -> 0;
         };
      } else {
         return 0;
      }
   }

   private String apply(Direction var1) {
      return switch (var1) {
         case EAST -> "вправо";
         case WEST -> "влево";
         case SOUTH -> "назад";
         case NORTH -> "вперёд";
         default -> "в сторону";
      };
   }

   private int fetchProvider() {
      return this.profileDraw.process("HolyWorld") && this.vectorPerform.process("Алмазный блок")
         ? this.handle(Items.DIAMOND_BLOCK)
         : this.handle(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE);
   }

   private int handle(Item... var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         Item var3 = Module.client.player.getInventory().getStack(var2).getItem();

         for (Item var7 : var1) {
            if (var3 == var7) {
               return var2;
            }
         }
      }

      return -1;
   }

   private int drawProfile() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isIn(ItemTags.PICKAXES)) {
            return var1;
         }
      }

      return -1;
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && var1.update() == PacketEvent.Mode.RECEIVE) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var4 = var2.content().getString();
            if (this.compute(var4)) {
               Module.client.execute(
                  () -> {
                     if (Module.client.player != null) {
                        String var1x = this.profileDraw.process("HolyWorld")
                           ? "§d!!! ПРИВАТ ПЕРЕКРЫВАЕТ ДРУГОЙ РЕГИОН !!!"
                           : "§d!!! НАЙДЕНО ПЕРЕСЕЧЕНИЕ РЕГИОНОВ !!!";
                        this.process(var1x);
                        if (this.responseCompute.compute()) {
                           String var2x = this.profileDraw.process("HolyWorld") ? "HolyWorld (Перекрывает регион)" : "FunTime (Регион пересекается)";
                           this.process(var2x, Module.client.player.getBlockX(), Module.client.player.getBlockY(), Module.client.player.getBlockZ());
                        }

                        if (this.render()) {
                           this.update("stop");
                           this.toggle();
                        }
                     }
                  }
               );
               return;
            }

            if (this.render() && this.scaleRender == BaseFinder.Mode.WAITING_CHAT && this.resolve(var4)) {
               Module.client.execute(() -> {
                  this.scaleRender = BaseFinder.Mode.BREAKING;
                  this.clientRefresh = 2;
               });
            }
         }
      }
   }

   private void performVector() {
      for (PlayerEntity var2 : Module.client.world.getPlayers()) {
         if (var2 != Module.client.player && !ru.wild.core.manager.FriendManager.handle(var2.getName().getString())) {
            String var3 = var2.getName().getString();
            int var4 = var2.getBlockX();
            int var5 = var2.getBlockY();
            int var6 = var2.getBlockZ();
            this.process("§cОБНАРУЖЕН ИГРОК: §f" + var3);
            if (this.responseCompute.compute()) {
               this.compute(var3, var4, var5, var6);
            }

            if (this.previous.compute()) {
               this.update("stop");
            }

            this.toggle();
            return;
         }
      }
   }

   private void attachEvent() {
      ChunkPos var1 = Module.client.player.getChunkPos();
      int var2 = (int)this.providerFetch.compute();

      for (int var3 = var1.x - var2; var3 <= var1.x + var2; var3++) {
         for (int var4 = var1.z - var2; var4 <= var1.z + var2; var4++) {
            WorldChunk var5 = Module.client.world.getChunk(var3, var4);
            if (var5 != null) {
               for (BlockEntity var7 : var5.getBlockEntities().values()) {
                  BlockEntityType var8 = var7.getType();
                  if (eventAttach.containsKey(var8) && this.handle(var7)) {
                     BlockPos var9 = var7.getPos();
                     if (!this.serverRead.contains(var9) && (!this.matrixBlend.compute() || this.compute(var9))) {
                        this.serverRead.add(var9);
                        this.positionAdvance.put(var9, var8);
                        String var10 = this.process(var7);
                        this.handle(var10, var9.getX(), var9.getY(), var9.getZ());
                     }
                  }
               }
            }
         }
      }

      if (this.profileDraw.process("HolyWorld")) {
         this.handle(var1, var2);
      }
   }

   private void readServer() {
      for (Entity var2 : Module.client.world.getEntities()) {
         if (!this.frameCheck.contains(var2.getId()) && !(var2.distanceTo(Module.client.player) > this.providerFetch.compute() * 16.0F)) {
            String var3 = null;
            if (this.target.compute()) {
               if (var2 instanceof ChestMinecartEntity) {
                  var3 = "Грузовая вагонетка";
               } else if (var2 instanceof HopperMinecartEntity) {
                  var3 = "Вагонетка с воронкой";
               }
            }

            if (var3 == null && this.pending.compute()) {
               if (var2 instanceof VillagerEntity) {
                  var3 = "Крестьянин";
               } else if (var2 instanceof AxolotlEntity) {
                  var3 = "Аксолотль";
               }
            }

            if (var3 != null) {
               this.frameCheck.add(var2.getId());
               BlockPos var4 = var2.getBlockPos();
               this.handle(var3, var4.getX(), var4.getY(), var4.getZ());
            }
         }
      }
   }

   private void handle(String var1, int var2, int var3, int var4) {
      this.process(String.format("§aНайден §f%s §aна XYZ: §f%d %d %d", var1, var2, var3, var4));
      if (this.responseCompute.compute()) {
         this.process(var1, var2, var3, var4);
      }

      if (!this.render() && this.previous.compute() && this.latest.compute() && !this.colorCompute) {
         this.colorCompute = true;
         this.process("§aНайдена цель! Перенаправляю Baritone...");
         this.update("goto " + var2 + " " + var3 + " " + var4);
      }
   }

   private void process(String var1, int var2, int var3, int var4) {
      if (TelegramApi.handle()) {
         String var5 = Module.client.getCurrentServerEntry() != null ? Module.client.getCurrentServerEntry().address : "Singleplayer";
         Thread var6 = new Thread(() -> {
            try {
               String var5x = String.format("База найдена!\n\nТип: %s\nКоординаты: %d %d %d\nСервер: %s\n", var1, var2, var3, var4, var5);
               TelegramApi.handle(var5x);
            } catch (Exception var6x) {
               ChatLogger.handle("§cОшибка отправки в Telegram: " + var6x.getMessage());
            }
         }, "Wild-BaseFinder-Telegram");
         var6.setDaemon(true);
         var6.start();
      }
   }

   private void compute(String var1, int var2, int var3, int var4) {
      if (TelegramApi.handle()) {
         String var5 = Module.client.getCurrentServerEntry() != null ? Module.client.getCurrentServerEntry().address : "Singleplayer";
         Thread var6 = new Thread(() -> {
            try {
               String var5x = String.format("Был обнаружен игрок\nНик: %s\nКоординаты: %d %d %d\nСервер: %s\n", var1, var2, var3, var4, var5);
               TelegramApi.handle(var5x);
            } catch (Exception var6x) {
               var6x.printStackTrace();
            }
         }, "Wild-BaseFinder-PlayerAlert");
         var6.setDaemon(true);
         var6.start();
      }
   }

   private boolean handle(BlockEntity var1) {
      if (var1 instanceof ChestBlockEntity && !this.source.process("Сундуки")) {
         return false;
      } else if (var1 instanceof EnderChestBlockEntity && !this.source.process("Эндер сундук")) {
         return false;
      } else if (var1 instanceof BarrelBlockEntity && !this.source.process("Бочки")) {
         return false;
      } else {
         return var1 instanceof FurnaceBlockEntity && !this.source.process("Печка")
            ? false
            : !(var1 instanceof ShulkerBoxBlockEntity) || this.source.process("Шалкера");
      }
   }

   private boolean compute(BlockPos var1) {
      return Module.client.world == null ? false : Module.client.world.getLightLevel(LightType.BLOCK, var1) >= 8;
   }

   private boolean advancePosition() {
      Box var1 = new Box(Module.client.player.getBlockPos()).expand(8.0);

      for (Entity var4 : Module.client.world.getOtherEntities(Module.client.player, var1)) {
         if (var4 instanceof HostileEntity && var4.isAlive()) {
            return true;
         }
      }

      return false;
   }

   private String process(BlockEntity var1) {
      if (var1 instanceof ChestBlockEntity) {
         return "Сундук";
      } else if (var1 instanceof EnderChestBlockEntity) {
         return "Эндер сундук";
      } else if (var1 instanceof BarrelBlockEntity) {
         return "Бочка";
      } else if (var1 instanceof FurnaceBlockEntity) {
         return "Печка";
      } else {
         return var1 instanceof ShulkerBoxBlockEntity ? "Шалкер" : "Неизвестный блок";
      }
   }

   private void handle(ChunkPos var1, int var2) {
      int var3 = var2 * 2 + 1;
      int var4 = var3 * var3;
      int var5 = Math.min(2, var4);

      for (int var6 = 0; var6 < var5; var6++) {
         int var7 = this.scaleSave++ % var4;
         int var8 = var7 / var3 - var2;
         int var9 = var7 % var3 - var2;
         this.handle(var1.x + var8, var1.z + var9);
      }

      if (this.scaleSave >= var4) {
         this.scaleSave %= var4;
      }
   }

   private void handle(int var1, int var2) {
      long var3 = ChunkPos.toLong(var1, var2);
      Integer var5 = this.dataValidate.get(var3);
      if (var5 == null || this.scaleAdapt - var5 >= 160) {
         WorldChunk var6 = Module.client.world.getChunk(var1, var2);
         if (var6 != null) {
            int var7 = var1 << 4;
            int var8 = var2 << 4;
            int var9 = Module.client.world.getBottomY();
            int var10 = Module.client.world.getTopYInclusive();
            Mutable var11 = new Mutable();

            for (int var12 = 0; var12 < 16; var12++) {
               for (int var13 = 0; var13 < 16; var13++) {
                  for (int var14 = var9; var14 <= var10; var14++) {
                     var11.set(var7 + var12, var14, var8 + var13);
                     Block var15 = var6.getBlockState(var11).getBlock();
                     if (this.handle(var15)) {
                        BlockPos var16 = var11.toImmutable();
                        if (!this.serverRead.contains(var16) && (!this.matrixBlend.compute() || this.compute(var16))) {
                           if (this.serverRead.size() >= 8192) {
                              return;
                           }

                           if (var15 != Blocks.SAND && var15 != Blocks.RED_SAND || !this.handle(var16, 50)) {
                              this.serverRead.add(var16);
                              this.positionAdvance.put(var16, var15);
                              this.handle(this.compute(var15), var16.getX(), var16.getY(), var16.getZ());
                           }
                        }
                     }
                  }
               }
            }

            this.dataValidate.put(var3, this.scaleAdapt);
         }
      }
   }

   private boolean handle(BlockPos var1, int var2) {
      if (Module.client.world == null) {
         return false;
      }

      WorldBorder var3 = Module.client.world.getWorldBorder();
      int var4 = var1.getX();
      int var5 = var1.getZ();
      return var4 - var3.getBoundWest() <= var2
         || var3.getBoundEast() - var4 <= var2
         || var5 - var3.getBoundNorth() <= var2
         || var3.getBoundSouth() - var5 <= var2;
   }

   private boolean handle(Block var1) {
      return var1 == Blocks.SPONGE
         || var1 == Blocks.WET_SPONGE
         || var1 == Blocks.END_STONE
         || var1 == Blocks.GLOWSTONE
         || var1 == Blocks.SAND
         || var1 == Blocks.RED_SAND
         || var1 == Blocks.MELON
         || var1 == Blocks.PUMPKIN
         || var1 == Blocks.CARVED_PUMPKIN
         || var1 == Blocks.COCOA
         || var1 == Blocks.SOUL_SAND
         || var1 == Blocks.FARMLAND
         || var1 == Blocks.GLASS
         || var1 == Blocks.TINTED_GLASS
         || var1 == Blocks.CACTUS
         || var1 instanceof TrapdoorBlock
         || var1 instanceof SlabBlock
         || var1 instanceof ButtonBlock
         || this.process(var1);
   }

   private boolean process(Block var1) {
      return var1 == Blocks.WHITE_STAINED_GLASS
         || var1 == Blocks.ORANGE_STAINED_GLASS
         || var1 == Blocks.MAGENTA_STAINED_GLASS
         || var1 == Blocks.LIGHT_BLUE_STAINED_GLASS
         || var1 == Blocks.YELLOW_STAINED_GLASS
         || var1 == Blocks.LIME_STAINED_GLASS
         || var1 == Blocks.PINK_STAINED_GLASS
         || var1 == Blocks.GRAY_STAINED_GLASS
         || var1 == Blocks.LIGHT_GRAY_STAINED_GLASS
         || var1 == Blocks.CYAN_STAINED_GLASS
         || var1 == Blocks.PURPLE_STAINED_GLASS
         || var1 == Blocks.BLUE_STAINED_GLASS
         || var1 == Blocks.BROWN_STAINED_GLASS
         || var1 == Blocks.GREEN_STAINED_GLASS
         || var1 == Blocks.RED_STAINED_GLASS
         || var1 == Blocks.BLACK_STAINED_GLASS;
   }

   private String compute(Block var1) {
      if (var1 == Blocks.SPONGE || var1 == Blocks.WET_SPONGE) {
         return "Губка";
      } else if (var1 == Blocks.END_STONE) {
         return "Эндерняк";
      } else if (var1 == Blocks.GLOWSTONE) {
         return "Светокамень";
      } else if (var1 instanceof TrapdoorBlock) {
         return "Люк";
      } else if (var1 == Blocks.SAND || var1 == Blocks.RED_SAND) {
         return "Песок";
      } else if (var1 == Blocks.MELON) {
         return "Арбуз";
      } else if (var1 == Blocks.PUMPKIN || var1 == Blocks.CARVED_PUMPKIN) {
         return "Тыква";
      } else if (var1 == Blocks.COCOA) {
         return "Какао";
      } else if (var1 == Blocks.SOUL_SAND) {
         return "Песок душ";
      } else if (var1 == Blocks.FARMLAND) {
         return "Вспаханная земля";
      } else if (var1 instanceof SlabBlock) {
         return "Плита";
      } else if (var1 == Blocks.GLASS || var1 == Blocks.TINTED_GLASS || this.process(var1)) {
         return "Стекло";
      } else if (var1 == Blocks.CACTUS) {
         return "Кактус";
      } else {
         return var1 instanceof ButtonBlock ? "Кнопка" : "HolyWorld блок";
      }
   }

   private void checkFrame() {
      byte var1 = 100;
      eventAttach.put(Blocks.SPONGE, PackedColor.update(new Color(222, 207, 67).getRGB(), var1));
      eventAttach.put(Blocks.WET_SPONGE, PackedColor.update(new Color(172, 184, 68).getRGB(), var1));
      eventAttach.put(Blocks.END_STONE, PackedColor.update(new Color(226, 222, 156).getRGB(), var1));
      eventAttach.put(Blocks.GLOWSTONE, PackedColor.update(new Color(255, 211, 91).getRGB(), var1));
      eventAttach.put(Blocks.SAND, PackedColor.update(new Color(219, 203, 142).getRGB(), var1));
      eventAttach.put(Blocks.RED_SAND, PackedColor.update(new Color(190, 98, 38).getRGB(), var1));
      eventAttach.put(Blocks.MELON, PackedColor.update(new Color(85, 176, 57).getRGB(), var1));
      eventAttach.put(Blocks.PUMPKIN, PackedColor.update(new Color(214, 119, 27).getRGB(), var1));
      eventAttach.put(Blocks.CARVED_PUMPKIN, PackedColor.update(new Color(214, 119, 27).getRGB(), var1));
      eventAttach.put(Blocks.COCOA, PackedColor.update(new Color(111, 67, 36).getRGB(), var1));
      eventAttach.put(Blocks.SOUL_SAND, PackedColor.update(new Color(83, 63, 55).getRGB(), var1));
      eventAttach.put(Blocks.FARMLAND, PackedColor.update(new Color(110, 75, 41).getRGB(), var1));
      eventAttach.put(Blocks.GLASS, PackedColor.update(new Color(180, 230, 240).getRGB(), var1));
      eventAttach.put(Blocks.TINTED_GLASS, PackedColor.update(new Color(80, 65, 95).getRGB(), var1));
      eventAttach.put(Blocks.CACTUS, PackedColor.update(new Color(56, 135, 45).getRGB(), var1));
   }

   private int handle(Object var1) {
      Integer var2 = eventAttach.get(var1);
      if (var2 != null) {
         return var2;
      }

      if (var1 instanceof Block var3) {
         if (var3 instanceof TrapdoorBlock) {
            return PackedColor.update(new Color(128, 92, 51).getRGB(), 100);
         }

         if (var3 instanceof SlabBlock) {
            return PackedColor.update(new Color(150, 150, 150).getRGB(), 100);
         }

         if (var3 instanceof ButtonBlock) {
            return PackedColor.update(new Color(178, 178, 178).getRGB(), 100);
         }

         if (this.process(var3)) {
            return PackedColor.update(new Color(125, 200, 230).getRGB(), 100);
         }
      }

      return -1;
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null && this.itemProject.compute()) {
         Immediate var2 = WorldVertexBuffer.handle();

         try {
            Vec3d var3 = Module.client.gameRenderer.getCamera().getPos();
            Matrix4f var4 = var1.compute().peek().getPositionMatrix();
            VertexConsumer var5 = var2.getBuffer(targetWrite);

            for (BlockPos var7 : this.serverRead) {
               double var8 = var7.getX() + 0.5 - var3.x;
               double var10 = var7.getY() + 0.5 - var3.y;
               double var12 = var7.getZ() + 0.5 - var3.z;
               if (!(var8 * var8 + var10 * var10 + var12 * var12 > 16384.0)) {
                  Object var14 = this.positionAdvance.get(var7);
                  int var15 = this.handle(var14);
                  if (var15 != -1) {
                     this.handle(var5, var4, var3, var7, var15);
                  }
               }
            }

            if (this.target.compute() || this.pending.compute()) {
               for (Entity var20 : Module.client.world.getEntities()) {
                  if (this.frameCheck.contains(var20.getId())) {
                     int var21 = -1;
                     if (var20 instanceof ChestMinecartEntity) {
                        var21 = eventAttach.get(ChestMinecartEntity.class);
                     } else if (var20 instanceof HopperMinecartEntity) {
                        var21 = eventAttach.get(HopperMinecartEntity.class);
                     } else if (var20 instanceof VillagerEntity) {
                        var21 = eventAttach.get(VillagerEntity.class);
                     } else if (var20 instanceof AxolotlEntity) {
                        var21 = eventAttach.get(AxolotlEntity.class);
                     }

                     if (var21 != -1) {
                        DualLayerBoxVertexEmitter.handle(
                           var5,
                           var4,
                           (float)(var20.getX() - 0.5 - var3.x),
                           (float)(var20.getY() - var3.y),
                           (float)(var20.getZ() - 0.5 - var3.z),
                           (float)(var20.getX() + 0.5 - var3.x),
                           (float)(var20.getY() + 0.5 - var3.y),
                           (float)(var20.getZ() + 0.5 - var3.z),
                           var21
                        );
                     }
                  }
               }
            }
         } finally {
            WorldVertexBuffer.process();
         }
      }
   }

   private void handle(VertexConsumer var1, Matrix4f var2, Vec3d var3, BlockPos var4, int var5) {
      float var6 = (float)(var4.getX() - var3.x);
      float var7 = (float)(var4.getY() - var3.y);
      float var8 = (float)(var4.getZ() - var3.z);
      float var9 = (float)(var4.getX() + 1 - var3.x);
      float var10 = (float)(var4.getY() + 1 - var3.y);
      float var11 = (float)(var4.getZ() + 1 - var3.z);
      DualLayerBoxVertexEmitter.handle(var1, var2, var6, var7, var8, var9, var10, var11, var5);
   }

   private void process(String var1) {
      ChatLogger.handle("§5[BaseFinder] " + var1);
   }

   private boolean compute(String var1) {
      if (this.profileDraw.process("HolyWorld")) {
         return var1.contains("перекрывает другой регион") || var1.contains("не можете разместить блок привата");
      } else {
         return !this.profileDraw.process("FunTime") && !this.profileDraw.process("Поиск приватом")
            ? false
            : var1.contains("Ваш регион пересекается") || var1.contains("[✠]") && var1.contains("пересекается");
      }
   }

   private boolean resolve(String var1) {
      if (this.profileDraw.process("HolyWorld")) {
         return var1.contains("Регион успешно создан") || var1.contains("успешно") && var1.contains("регион");
      } else {
         return !this.profileDraw.process("FunTime") && !this.profileDraw.process("Поиск приватом")
            ? false
            : var1.contains("Регион успешно создан") || var1.contains("[✠]") && var1.contains("успешно");
      }
   }

   private void update(String var1) {
      try {
         IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
         if (var2 != null) {
            var2.getCommandManager().execute(var1);
         }
      } catch (Throwable var3) {
      }
   }

   enum Mode {
      CHECK_SUPPLIES,
      TUNNELING,
      BORDER_SHIFTING,
      STOPPING,
      DIGGING_SPOT,
      PLACING,
      WAITING_CHAT,
      BREAKING,
      RESUMING;
   }
}
