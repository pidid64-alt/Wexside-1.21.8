package ru.wild.modules.misc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalNear;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.Perspective;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.modules.visuals.AncientXray;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(name = "AutoAncientBot", description = "Автоматический фарм древних обломков через ТНТ", category = ModuleCategory.Misc)
public class AutoAncientBot extends Module {
   private final BooleanSetting source = new BooleanSetting("Логи в чат", true);
   private final BooleanSetting target = new BooleanSetting("Пёрки", true);
   private final NumberSetting pending = new NumberSetting("Мин. дистанция пёрки", 16.0F, 8.0F, 48.0F, 1.0F, false).handle(() -> !this.target.compute());
   private final BooleanSetting previous = new BooleanSetting("Debug", false);
   private static final int latest = 26;
   private static final int summary = 6;
   private static final int matrixBlend = 2;
   private static final int vectorMatch = 36;
   private static final int itemProject = 36;
   private static final int responseCompute = 18000;
   private static final int providerFetch = 2500;
   private static final int profileDraw = 2500;
   private static final int vectorPerform = 16;
   private static final int eventAttach = 19;
   private static final int serverRead = 7000;
   private static final int positionAdvance = 300;
   private static final int frameCheck = 3500;
   private static final int moduleCollect = 2;
   private static final int providerClose = 22000;
   private static final int presetSave = 2;
   private static final double windowConvert = 4.2;
   private static final float presetWrite = 4.0F;
   private static final float colorMeasure = 140.0F;
   private static final float animationSchedule = 34.0F;
   private static final float rendererScan = 1.35F;
   private static final long sourceBuild = 90L;
   private static final long outputCollapse = 3000L;
   private static final long profileInvoke = 9000L;
   private static final int sourceSchedule = 2;
   private static final int timerRender = 4;
   private static final int scaleSave = 900;
   private static final int colorCompute = 900;
   private static final int scaleAdapt = 1400;
   private static final int textureRun = 8000;
   private static final int indexBind = 1400;
   private static final double actionRead = 9.0;
   private static final int configCollapse = 2500;
   private static final int dataValidate = 3;
   private static final double scaleRender = 0.03;
   private static final double clientRefresh = 0.99;
   private static final double keyFilter = 1.5;
   private static final int requestAdapt = 160;
   private static final double timerMeasure = 1.8;
   private static final double vectorEncode = 27.0;
   private static final int requestReceive = 2500;
   private static final int windowProcess = 1200;
   private static final int packetSave = 2000;
   private static final int entryAnimate = 5000;
   private static final double playerCollect = 25.0;
   private static final int stateApply = 3500;
   private static final float matrixFilter = 8.0F;
   private AutoAncientBot.FallbackMode layerSample = AutoAncientBot.FallbackMode.SEARCHING;
   private AutoAncientBot.SecondaryMode worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
   private final Stopwatch targetWrite = new Stopwatch();
   private final Stopwatch resultEncode = new Stopwatch();
   private final Stopwatch messageParse = new Stopwatch();
   private final Stopwatch providerRead = new Stopwatch();
   private final Stopwatch matrixBlend2 = new Stopwatch();
   private final Stopwatch scalePerform = new Stopwatch();
   private final Stopwatch contextExpand = new Stopwatch();
   private final Stopwatch keyProcess = new Stopwatch();
   private final Stopwatch actionConvert = new Stopwatch();
   private final Stopwatch screenRead = new Stopwatch();
   private final Stopwatch animationExpand = new Stopwatch();
   private final Stopwatch playerRun = new Stopwatch();
   private final Stopwatch matrixRender = new Stopwatch();
   private final Stopwatch moduleTick = new Stopwatch();
   private final Stopwatch playerCollapse = new Stopwatch();
   private final Set<BlockPos> optionAdvance = new HashSet<>();
   private final Set<BlockPos> effectScan = new HashSet<>();
   private final Map<BlockPos, Boolean> optionParse = new HashMap<>();
   private BlockPos pointSubmit;
   private BlockPos listenerPerform;
   private BlockPos configMatch;
   private BlockPos actionRender;
   private BlockPos playerApply;
   private BlockPos bufferAdapt;
   private BlockPos playerUpdate;
   private BlockPos packetRead;
   private boolean rendererCancel;
   private boolean eventReceive;
   private boolean screenSubmit;
   private boolean cacheHandle;
   private boolean rangeRelease;
   private boolean indexSave;
   private boolean indexCheck;
   private List<Block> settingSchedule = List.of();
   private List<Item> inputAcquire = List.of();
   private double listenerRun;
   private int indexLoad;
   private int layoutSave;
   private int blockRun;
   private int playerEvaluate;
   private BlockPos outputFetch;
   private int scaleParse = -1;
   private int sessionEncode = -1;
   private int elementTick = -1;
   private int regionAlign = -1;
   private boolean resourceClamp;
   private boolean handlerRun;
   private boolean keyCheck;
   private boolean layerProject;
   private boolean entityFilter;
   private boolean layerSample2;
   private boolean sourceCancel;
   private Vec3d eventSend;
   private AutoAncientBot.PrimaryMode providerOffset = AutoAncientBot.PrimaryMode.IDLE;
   private Vec3d messageParse2;
   private Vec3d shaderProject;
   private String inputInvoke;
   private float optionFetch;
   private float eventCollapse;
   private int stateAttach = -1;
   private int worldEvaluate;
   private Vec3d playerProject;
   private boolean playerMatch;
   private int cacheClose;
   private int scaleSetup;
   private int indexSynchronize;
   private int taskInterpolate;
   private int sourceRefresh;
   private long playerSave;

   public AutoAncientBot() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @Override
   public void handle() {
      if (Module.client.player != null && Module.client.world != null) {
         AttackAura var1 = WildClient.instance.data.handle(AttackAura.class);
         if (var1 != null && var1.enabled) {
            ChatLogger.handle("[AutoAncient] Disable HitAura first.");
            this.toggle();
         } else if (this.handle(Blocks.TNT.asItem()) != -1 && this.handle(Items.FLINT_AND_STEEL) != -1) {
            super.handle();
            if (Module.client.options != null) {
               Module.client.options.setPerspective(Perspective.FIRST_PERSON);
            }

            AncientXray var2 = this.measure();
            if (var2 != null) {
               var2.render();
            }

            this.computeResponse();
            this.optionAdvance.clear();
            this.effectScan.clear();
            this.optionParse.clear();
            this.bufferAdapt = null;
            this.playerUpdate = null;
            this.packetRead = null;
            this.pointSubmit = null;
            this.listenerPerform = null;
            this.configMatch = null;
            this.actionRender = null;
            this.playerApply = null;
            this.rendererCancel = false;
            this.indexLoad = 0;
            this.scaleParse = -1;
            this.sessionEncode = -1;
            this.elementTick = -1;
            this.regionAlign = -1;
            this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
            this.layerProject = false;
            this.eventSend = null;
            this.contextExpand.handle();
            this.keyProcess.handle();
            this.actionConvert.handle();
            this.screenRead.handle();
            this.animationExpand.handle();
            this.resourceClamp = false;
            this.handlerRun = false;
            this.keyCheck = false;
            this.entityFilter = false;
            this.layerSample2 = false;
            this.sourceCancel = false;
            this.eventReceive = false;
            this.providerOffset = AutoAncientBot.PrimaryMode.IDLE;
            this.messageParse2 = null;
            this.shaderProject = null;
            this.inputInvoke = null;
            this.stateAttach = -1;
            this.worldEvaluate = 0;
            this.playerProject = null;
            this.matrixRender.handle();
            this.moduleTick.handle();
            this.playerCollapse.handle();
            this.resultEncode.handle();
            this.messageParse.handle();
            this.blockRun = 0;
            this.playerMatch = false;
            this.cacheClose = 0;
            this.scaleSetup = 0;
            this.indexSynchronize = 0;
            this.taskInterpolate = 0;
            this.sourceRefresh = 0;
            this.playerSave = System.currentTimeMillis();
            this.listenerRun = Math.toRadians(Module.client.player.getYaw()) + (Math.PI / 2);
            this.handle(AutoAncientBot.FallbackMode.SEARCHING);
            this.process("Запущен. ТНТ: " + this.process(Blocks.TNT.asItem()) + ", пёрок: " + this.process(Items.ENDER_PEARL));
            if (this.target.compute() && this.handle(Items.ENDER_PEARL) == -1) {
               this.process("Пёрок в хотбаре нет — броски работать не будут.");
            }

            ViewRotationCoordinator.data = true;
            ViewRotationCoordinator.instance = true;
            ViewRotationCoordinator.context = Module.client.player.getYaw();
            ViewRotationCoordinator.config = Module.client.player.getPitch();
            this.handle("enabled");
         } else {
            ChatLogger.handle("[AutoAncient] TNT and flint must be in hotbar.");
            this.toggle();
         }
      } else {
         this.toggle();
      }
   }

   @Override
   public void process() {
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.data = false;
      ViewRotationCoordinator.instance = false;
      super.process();
      IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
      this.drawAnimation(var1);
      this.animate(var1);
      this.unload();
      this.matchVector();
      this.animate();
      if (Module.client.options != null) {
         Module.client.options.useKey.setPressed(false);
         Module.client.options.attackKey.setPressed(false);
         Module.client.options.jumpKey.setPressed(false);
      }

      var1.getMineProcess().cancel();
      var1.getBuilderProcess().onLostControl();
      this.drawAnimation();
      var1.getSelectionManager().removeAllSelections();
      this.pointSubmit = null;
      if (this.eventReceive) {
         var1.getCommandManager().execute("resume");
         this.eventReceive = false;
      }

      this.fetchProvider();
      if (this.playerSave > 0L) {
         long var2 = Math.max(1L, (System.currentTimeMillis() - this.playerSave) / 1000L);
         this.process(
            "Итог: обломков "
               + this.indexSynchronize
               + ", ТНТ "
               + this.taskInterpolate
               + " (съедено "
               + this.scaleSetup
               + "), пёрок "
               + this.sourceRefresh
               + ", время "
               + var2 / 60L
               + " мин "
               + var2 % 60L
               + " с"
         );
         this.playerSave = 0L;
      }

      this.handle("disabled");
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         AncientXray var2 = this.measure();
         if (var2 == null) {
            ChatLogger.handle("[AutoAncient] AncientXray module is not registered.");
            this.toggle();
         } else {
            if (!var2.enabled) {
               var2.refresh();
            }

            IBaritone var3 = BaritoneAPI.getProvider().getPrimaryBaritone();
            if (!this.render(var3)) {
               if (!this.unload(var3)) {
                  if (!this.tick(var3)) {
                     if (!this.load(var3)) {
                        if (!this.encodePoint(var3)) {
                           if (this.layerSample != AutoAncientBot.FallbackMode.MINING
                              && this.layerSample != AutoAncientBot.FallbackMode.PLACING_TNT
                              && this.layerSample != AutoAncientBot.FallbackMode.IGNITING_TNT
                              && this.layerSample != AutoAncientBot.FallbackMode.WAITING_EXPLOSION) {
                              BlockPos var4 = this.resolve(var2);
                              if (var4 != null) {
                                 this.drawAnimation();
                                 this.process(var4);
                                 return;
                              }
                           }

                           switch (this.layerSample) {
                              case SEARCHING:
                                 this.handle(var3);
                                 break;
                              case MOVING_SEARCH:
                                 this.process(var3);
                                 break;
                              case MOVING_SITE:
                                 this.compute(var3);
                                 break;
                              case CLEARING_SITE:
                                 this.resolve(var3);
                                 break;
                              case PLACING_TNT:
                                 this.update(var3);
                                 break;
                              case IGNITING_TNT:
                                 this.apply(var3);
                                 break;
                              case WAITING_EXPLOSION:
                                 this.refresh();
                                 break;
                              case WAITING_SCAN:
                                 this.handle(var2);
                                 break;
                              case MINING:
                                 this.handle(var3, var2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.world != null) {
         AncientXray var2 = this.measure();
         if (var2 != null) {
            if (var1.resolve() instanceof ExplosionS2CPacket var3) {
               BlockPos var7 = BlockPos.ofFloored(var3.center());
               if (this.unload(var7)) {
                  this.rendererCancel = true;
                  this.playerApply = var7;
                  this.optionAdvance.clear();
                  this.optionParse.clear();
                  this.effectScan.add(var7.toImmutable());
                  this.layerProject = false;
                  if (!var2.enabled) {
                     var2.handle(var7, 10);
                  }

                  if (this.layerSample == AutoAncientBot.FallbackMode.WAITING_EXPLOSION) {
                     this.process("Взрыв! Сканирую обломки...");
                  }

                  if (this.layerSample == AutoAncientBot.FallbackMode.WAITING_EXPLOSION || this.layerSample == AutoAncientBot.FallbackMode.WAITING_SCAN) {
                     this.handle(AutoAncientBot.FallbackMode.WAITING_SCAN);
                  }
               }
            } else if (var1.resolve() instanceof BlockUpdateS2CPacket var4) {
               if (this.actionRender != null && var4.getPos().equals(this.actionRender) && var4.getState().isOf(Blocks.TNT)) {
                  this.playerMatch = true;
               }

               if (!var2.enabled) {
                  var2.handle(var4.getPos(), var4.getState().getBlock());
               }
            } else if (var1.resolve() instanceof ChunkDeltaUpdateS2CPacket var5) {
               var5.visitUpdates((var2x, var3x) -> {
                  if (this.actionRender != null && var2x.equals(this.actionRender) && var3x.isOf(Blocks.TNT)) {
                     this.playerMatch = true;
                  }

                  if (!var2.enabled) {
                     var2.handle(var2x, var3x.getBlock());
                  }
               });
            }
         }
      }
   }

   private void handle(IBaritone var1) {
      this.animate();
      BlockPos var2 = this.render();
      if (var2 != null) {
         this.configMatch = var2;
         this.handle(var1, var2);
         this.handle(AutoAncientBot.FallbackMode.MOVING_SITE);
         this.process("Место для взрыва: " + var2.toShortString());
      } else {
         this.select(var1);
      }
   }

   private void process(IBaritone var1) {
      if (!this.handle(this.listenerPerform, "лечу к зоне поиска")) {
         if (this.save(var1)) {
            this.submit(var1);
         } else {
            if (this.listenerPerform == null
               || this.process(this.listenerPerform, 9.0)
               || this.targetWrite.update(18000L)
               || !this.refresh(var1) && this.targetWrite.update(2500L)) {
               this.drawAnimation();
               this.handle(AutoAncientBot.FallbackMode.SEARCHING);
            }
         }
      }
   }

   private void compute(IBaritone var1) {
      if (this.configMatch == null) {
         this.handle(AutoAncientBot.FallbackMode.SEARCHING);
      } else if (!this.handle(this.configMatch, "лечу к месту взрыва")) {
         if (this.save(var1)) {
            this.submit(var1);
         } else if (this.targetWrite.update(22000L)) {
            this.indexLoad++;
            this.handle(AutoAncientBot.FallbackMode.SEARCHING);
         } else if (this.process(this.configMatch, 10.0) || !this.refresh(var1) && this.targetWrite.update(2500L)) {
            this.drawAnimation();
            if (!this.resolve(this.configMatch)) {
               this.indexLoad++;
               this.handle(AutoAncientBot.FallbackMode.SEARCHING);
            } else {
               this.actionRender = this.apply(this.configMatch);
               if (this.actionRender == null) {
                  this.indexLoad++;
                  this.handle(AutoAncientBot.FallbackMode.SEARCHING);
               } else {
                  this.cacheClose = 0;
                  this.playerMatch = false;
                  this.playerUpdate = null;
                  this.handle(AutoAncientBot.FallbackMode.CLEARING_SITE);
                  this.process("Расчищаю площадку: " + this.actionRender.toShortString());
               }
            }
         }
      }
   }

   private void resolve(IBaritone var1) {
      if (this.actionRender == null) {
         this.handle(AutoAncientBot.FallbackMode.SEARCHING);
      } else if (this.prepare(this.actionRender)) {
         this.drawAnimation();
         this.playerUpdate = null;
         this.handle(AutoAncientBot.FallbackMode.PLACING_TNT);
      } else if (this.targetWrite.update(14000L)) {
         this.indexLoad++;
         this.drawAnimation();
         this.handle(AutoAncientBot.FallbackMode.SEARCHING);
      } else {
         BlockPos var2 = this.handle(this.actionRender);
         if (var2 == null) {
            this.indexLoad++;
            this.drawAnimation();
            this.handle(AutoAncientBot.FallbackMode.SEARCHING);
         } else {
            BlockHitResult var3 = this.refresh(var2);
            if (var3 == null) {
               if (this.save(var1)) {
                  this.submit(var1);
               } else {
                  this.process(var1, this.actionRender);
               }
            } else {
               AutoAncientBot.Mode var4 = this.handle(var3.getBlockPos(), 3000L);
               if (var4 == AutoAncientBot.Mode.STUCK) {
                  this.indexLoad++;
                  this.handle(AutoAncientBot.FallbackMode.SEARCHING);
               } else {
                  if (var4 == AutoAncientBot.Mode.NO_REACH) {
                     this.process(var1, this.actionRender);
                  }
               }
            }
         }
      }
   }

   private BlockPos handle(BlockPos var1) {
      if (!Module.client.world.getBlockState(var1).isReplaceable()) {
         return var1;
      } else {
         return !Module.client.world.getBlockState(var1.up()).isReplaceable() ? var1.up() : null;
      }
   }

   private void update(IBaritone var1) {
      if (this.actionRender == null) {
         this.handle(AutoAncientBot.FallbackMode.SEARCHING);
      } else if (Module.client.world.getBlockState(this.actionRender).isOf(Blocks.TNT)) {
         if (this.playerMatch) {
            if (this.matrixBlend2.update(1400L)) {
               this.handle(AutoAncientBot.FallbackMode.IGNITING_TNT);
            }
         } else {
            if (this.matrixBlend2.update(2500L)) {
               this.scaleSetup++;
               this.cacheClose++;
               this.process("Сервер съел ТНТ — переставляю (#" + this.scaleSetup + ")");
               this.handle(this.actionRender, 0);
               if (this.cacheClose >= 3) {
                  this.process("ТНТ пропадает на этом месте — ищу другое");
                  this.indexLoad++;
                  this.handle(AutoAncientBot.FallbackMode.SEARCHING);
                  return;
               }

               this.targetWrite.handle();
               this.matrixBlend2.handle();
            }
         }
      } else if (!this.submit(this.actionRender)) {
         if (this.save(var1)) {
            this.submit(var1);
         } else {
            this.process(var1, this.actionRender);
         }
      } else {
         this.drawAnimation();
         if (!this.prepare(this.actionRender)) {
            this.indexLoad++;
            this.handle(AutoAncientBot.FallbackMode.SEARCHING);
         } else {
            Vec3d var2 = new Vec3d(this.actionRender.getX() + 0.5, this.actionRender.getY(), this.actionRender.getZ() + 0.5);
            RotationAngles var3 = this.handle(var2);
            this.handle(var3);
            if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
               if (this.matrixBlend2.update(900L)) {
                  if (!this.check(this.actionRender)) {
                     if (this.tick()) {
                        return;
                     }

                     this.toggle();
                     return;
                  }

                  this.matrixBlend2.handle();
               }

               if (this.targetWrite.update(8000L)) {
                  this.indexLoad++;
                  this.handle(AutoAncientBot.FallbackMode.SEARCHING);
               }
            }
         }
      }
   }

   private void apply(IBaritone var1) {
      if (this.actionRender == null) {
         this.handle(AutoAncientBot.FallbackMode.SEARCHING);
      } else if (!Module.client.world.getBlockState(this.actionRender).isOf(Blocks.TNT)) {
         if (this.targetWrite.update(600L)) {
            this.drawAnimation();
            this.rendererCancel = false;
            this.handle(AutoAncientBot.FallbackMode.WAITING_EXPLOSION);
         }
      } else if (!this.submit(this.actionRender)) {
         if (this.save(var1)) {
            this.submit(var1);
         } else {
            this.process(var1, this.actionRender);
         }
      } else {
         this.drawAnimation();
         BlockHitResult var2 = this.tick(this.actionRender);
         if (var2 != null) {
            RotationAngles var3 = this.handle(var2.getPos());
            this.handle(var3);
            if (!(new RotationAngles(Module.client.player).handle(var3) > 4.0F)) {
               if (this.targetWrite.update(1400L)) {
                  if (this.matrixBlend2.update(900L)) {
                     if (!this.onTick(this.actionRender)) {
                        this.indexLoad++;
                        this.handle(AutoAncientBot.FallbackMode.SEARCHING);
                        return;
                     }

                     this.rendererCancel = false;
                     this.taskInterpolate++;
                     this.drawAnimation();
                     this.handle(AutoAncientBot.FallbackMode.WAITING_EXPLOSION);
                     this.process("Поджёг ТНТ #" + this.taskInterpolate + " (" + this.actionRender.toShortString() + ")");
                  }
               }
            }
         }
      }
   }

   private void refresh() {
      if (!this.rendererCancel
         && this.actionRender != null
         && Module.client.world.getBlockState(this.actionRender).isOf(Blocks.TNT)
         && this.targetWrite.update(1800L)) {
         this.handle(AutoAncientBot.FallbackMode.IGNITING_TNT);
      } else {
         if (this.rendererCancel || this.targetWrite.update(6500L)) {
            this.handle(AutoAncientBot.FallbackMode.WAITING_SCAN);
         }
      }
   }

   private void handle(AncientXray var1) {
      if (this.targetWrite.update(1200L)) {
         BlockPos var2 = this.resolve(var1);
         if (var2 != null) {
            this.process(var2);
         } else if (this.handle(Blocks.TNT.asItem()) == -1 && this.playerApply != null && !this.layerProject) {
            var1.handle(this.playerApply, 2);
            this.layerProject = true;
            this.process("ТНТ закончилась — финальный скан вокруг взрыва");
            this.targetWrite.handle();
         } else {
            if (this.targetWrite.update(4500L)) {
               this.process("Обломков рядом нет — ищу новое место");
               this.bufferAdapt = null;
               this.playerUpdate = null;
               this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
               this.handle(AutoAncientBot.FallbackMode.SEARCHING);
               this.select(BaritoneAPI.getProvider().getPrimaryBaritone());
            }
         }
      }
   }

   private void handle(IBaritone var1, AncientXray var2) {
      if (this.worldSend != AutoAncientBot.SecondaryMode.BREAKING) {
         this.animate();
      }

      List<BlockPos> var3 = this.process(var2);
      if (var3.isEmpty()) {
         this.execute(var1);
         this.handle(AutoAncientBot.FallbackMode.SEARCHING);
         this.select(var1);
      } else if (this.bufferAdapt != null && !var3.contains(this.bufferAdapt)) {
         this.blendMatrix();
         this.compute(var2);
      } else if (this.bufferAdapt != null && !Module.client.world.getBlockState(this.bufferAdapt).isOf(Blocks.ANCIENT_DEBRIS)) {
         this.blendMatrix();
         this.compute(var2);
      } else {
         BlockPos var4 = this.bufferAdapt == null ? this.handle(var3) : this.bufferAdapt;
         if (this.bufferAdapt != null && this.bufferAdapt.equals(var4)) {
            if (this.worldSend == AutoAncientBot.SecondaryMode.APPROACHING) {
               this.process(var1, var2);
            } else {
               if (this.worldSend == AutoAncientBot.SecondaryMode.BREAKING) {
                  this.compute(var1, var2);
               }
            }
         } else {
            if (this.bufferAdapt == null) {
               this.layoutSave = 0;
            }

            this.handle(var1, var4, true);
         }
      }
   }

   private void handle(IBaritone var1, BlockPos var2, boolean var3) {
      this.bufferAdapt = var2.toImmutable();
      this.animate();
      this.drawAnimation();
      this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
      this.blockRun = 0;
      this.playerEvaluate = 0;
      this.outputFetch = null;
      this.playerUpdate = null;
      this.providerRead.handle();
      this.scalePerform.handle();
      if (var3) {
         this.targetWrite.handle();
      }

      this.handle("target ore " + this.bufferAdapt.toShortString());
   }

   private void execute(IBaritone var1) {
      var1.getMineProcess().cancel();
      var1.getBuilderProcess().onLostControl();
      this.drawAnimation();
      var1.getSelectionManager().removeAllSelections();
      this.bufferAdapt = null;
      this.playerUpdate = null;
      this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
      this.animate();
   }

   private List<BlockPos> process(AncientXray var1) {
      ArrayList<BlockPos> var2 = new ArrayList<>(var1.tick());
      var2.removeIf(var2x -> {
         boolean var3 = !Module.client.world.getBlockState(var2x).isOf(Blocks.ANCIENT_DEBRIS);
         if (var3) {
            var1.handle(var2x);
         }

         if (!var3 && !this.optionAdvance.contains(var2x) && this.load(var2x)) {
            this.optionAdvance.add(var2x.toImmutable());
            var1.handle(var2x);
            this.process("Пропускаю обломок " + var2x.toShortString() + " — замурован в лаве, не подойти");
            return true;
         } else {
            return var3 || this.optionAdvance.contains(var2x);
         }
      });
      return var2;
   }

   private BlockPos handle(List<BlockPos> var1) {
      return var1.stream().min(Comparator.comparingDouble(var0 -> Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var0)))).orElse((BlockPos)var1.get(0));
   }

   private void process(IBaritone var1, AncientXray var2) {
      this.animate();
      if (this.render(this.bufferAdapt)) {
         this.check(var1);
      } else if (this.scalePerform.update(22000L)) {
         this.handle(var2, "не смог дойти");
      } else if (!this.handle(this.bufferAdapt, "лечу к обломку")) {
         this.handle(var1, this.bufferAdapt, 2);
      }
   }

   private void compute(IBaritone var1, AncientXray var2) {
      BlockHitResult var3 = this.refresh(this.bufferAdapt);
      if (var3 == null) {
         this.playerUpdate = null;
         if (this.contextExpand.update(900L)) {
            this.playerEvaluate++;
            if (this.playerEvaluate > 4) {
               this.handle(var2, "не удержаться рядом (лава/обрыв)");
               return;
            }

            this.prepare(var1);
            this.handle("lost reach " + this.bufferAdapt.toShortString());
         }
      } else {
         this.contextExpand.handle();
         BlockPos var4 = var3.getBlockPos();
         if (!var4.equals(this.outputFetch)) {
            this.outputFetch = var4.toImmutable();
            this.playerEvaluate = 0;
         }

         boolean var5 = var4.equals(this.bufferAdapt);
         AutoAncientBot.Mode var6 = this.handle(var4, var5 ? 9000L : 3000L);
         if (var6 != AutoAncientBot.Mode.STUCK) {
            if (this.targetWrite.update(18000L)) {
               if (this.onTick(var1)) {
                  this.layoutSave++;
                  this.handle("retry ore " + this.bufferAdapt.toShortString() + " #" + this.layoutSave);
                  this.prepare(var1);
                  this.targetWrite.handle();
                  return;
               }

               this.handle(var2, "не выкопался за таймаут");
            }
         } else {
            this.blockRun++;
            if (var5 || this.blockRun > 2) {
               this.handle(var2, "фантомные блоки, ресинк");
            }
         }
      }
   }

   private void prepare(IBaritone var1) {
      this.animate();
      this.contextExpand.handle();
      this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
      this.scalePerform.handle();
      this.handle(var1, this.bufferAdapt, 2);
   }

   private void check(IBaritone var1) {
      this.drawAnimation();
      this.worldSend = AutoAncientBot.SecondaryMode.BREAKING;
      this.blockRun = 0;
      this.playerUpdate = null;
      this.targetWrite.handle();
      this.contextExpand.handle();
      this.handle("break ore " + this.bufferAdapt.toShortString());
   }

   private void process(BlockPos var1) {
      this.bufferAdapt = var1.toImmutable();
      this.layoutSave = 0;
      this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
      this.handle(AutoAncientBot.FallbackMode.MINING);
      this.handle(BaritoneAPI.getProvider().getPrimaryBaritone(), this.bufferAdapt, true);
      AncientXray var2 = this.measure();
      int var3 = var2 == null ? 0 : this.process(var2).size();
      this.process("Иду к обломку " + this.bufferAdapt.toShortString() + (var3 > 1 ? " (в очереди: " + var3 + ")" : ""));
   }

   private boolean onTick(IBaritone var1) {
      if (this.bufferAdapt != null && this.layoutSave < 2) {
         if (!Module.client.world.getBlockState(this.bufferAdapt).isOf(Blocks.ANCIENT_DEBRIS)) {
            return false;
         }

         double var2 = Module.client.player.squaredDistanceTo(Vec3d.ofCenter(this.bufferAdapt));
         return var1.getPathingBehavior().isPathing() || var2 <= 144.0 || this.measure(this.bufferAdapt);
      } else {
         return false;
      }
   }

   private void compute(AncientXray var1) {
      IBaritone var2 = BaritoneAPI.getProvider().getPrimaryBaritone();
      var2.getBuilderProcess().onLostControl();
      this.drawAnimation();
      var2.getSelectionManager().removeAllSelections();
      var1.handle(this.bufferAdapt);
      this.bufferAdapt = null;
      this.playerUpdate = null;
      this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
      this.animate();
   }

   private void handle(BlockPos var1, int var2) {
      if (Module.client.getNetworkHandler() != null) {
         for (int var3 = -var2; var3 <= var2; var3++) {
            for (int var4 = -var2; var4 <= var2; var4++) {
               for (int var5 = -var2; var5 <= var2; var5++) {
                  BlockPos var6 = var1.add(var3, var4, var5);
                  Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var6, Direction.UP));
                  Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, var6, Direction.UP));
               }
            }
         }
      }
   }

   private void handle(AncientXray var1, String var2) {
      if (this.bufferAdapt != null) {
         this.optionAdvance.add(this.bufferAdapt.toImmutable());
         var1.handle(this.bufferAdapt);
         this.process("Пропускаю обломок " + this.bufferAdapt.toShortString() + " — " + var2);
      }

      IBaritone var3 = BaritoneAPI.getProvider().getPrimaryBaritone();
      var3.getMineProcess().cancel();
      var3.getBuilderProcess().onLostControl();
      this.drawAnimation();
      var3.getSelectionManager().removeAllSelections();
      this.bufferAdapt = null;
      this.playerUpdate = null;
      this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
      this.animate();
   }

   private BlockPos resolve(AncientXray var1) {
      return this.process(var1).stream().min(Comparator.comparingDouble(var0 -> Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var0)))).orElse(null);
   }

   private void select(IBaritone var1) {
      BlockPos var2 = Module.client.player.getBlockPos();
      if (this.indexLoad > 0 && this.indexLoad % 4 == 0) {
         this.listenerRun += Math.PI / 2;
      }

      byte var3 = 36;
      int var4 = var2.getX() + (int)Math.round(Math.cos(this.listenerRun) * var3);
      int var5 = var2.getZ() + (int)Math.round(Math.sin(this.listenerRun) * var3);
      this.listenerPerform = new BlockPos(var4, this.resolve(var2.getY()), var5);
      this.handle(var1, this.listenerPerform);
      this.indexLoad++;
      this.handle(AutoAncientBot.FallbackMode.MOVING_SEARCH);
      this.handle("search " + this.listenerPerform.toShortString());
   }

   private BlockPos render() {
      BlockPos var1 = Module.client.player.getBlockPos();
      byte var2 = 26;
      BlockPos var3 = null;
      int var4 = Integer.MIN_VALUE;

      for (int var5 = -var2; var5 <= var2; var5 += 4) {
         for (int var6 = -var2; var6 <= var2; var6 += 4) {
            for (byte var7 = -6; var7 <= 6; var7 += 2) {
               BlockPos var8 = new BlockPos(var1.getX() + var5, var1.getY() + var7, var1.getZ() + var6);
               int var9 = this.compute(var8);
               if (var9 != Integer.MIN_VALUE && (var3 == null || var9 > var4)) {
                  var3 = var8;
                  var4 = var9;
               }
            }
         }
      }

      return var3;
   }

   private int compute(BlockPos var1) {
      if (this.update(var1)) {
         return Integer.MIN_VALUE;
      }

      if (!this.fetch(var1.down())) {
         return Integer.MIN_VALUE;
      }

      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;

      for (int var6 = -5; var6 <= 5; var6++) {
         for (int var7 = -3; var7 <= 3; var7++) {
            for (int var8 = -5; var8 <= 5; var8++) {
               BlockPos var9 = var1.add(var6, var7, var8);
               BlockState var10 = Module.client.world.getBlockState(var9);
               Block var11 = var10.getBlock();
               var2++;
               if (this.handle(var11)) {
                  var4++;
               } else if (var11 == Blocks.LAVA) {
                  var5++;
               } else if (this.process(var11)) {
                  var3++;
               }
            }
         }
      }

      if (!(var3 < var2 * 0.48) && !(var4 > var2 * 0.34) && !(var5 > var2 * 0.2)) {
         double var12 = Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var1));
         return var3 * 3 - var4 * 4 - var5 * 5 - (int)(var12 * 0.02) + this.update(var1.getY());
      } else {
         return Integer.MIN_VALUE;
      }
   }

   private boolean resolve(BlockPos var1) {
      return this.compute(var1) != Integer.MIN_VALUE;
   }

   private boolean update(BlockPos var1) {
      double var2 = 842.4;

      for (BlockPos var5 : this.effectScan) {
         if (this.process(var1, var5) <= var2) {
            return true;
         }
      }

      return false;
   }

   private BlockPos apply(BlockPos var1) {
      BlockPos var2 = null;

      for (int var3 = 0; var3 <= 2; var3++) {
         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = -1; var5 <= 1; var5++) {
               BlockPos var6 = var1.add(var4, var3, var5);
               if (this.execute(var6)) {
                  if (this.prepare(var6)) {
                     return var6.toImmutable();
                  }

                  if (var2 == null) {
                     var2 = var6.toImmutable();
                  }
               }
            }
         }
      }

      if (var2 != null) {
         return var2;
      } else {
         return this.execute(var1) ? var1.toImmutable() : null;
      }
   }

   private boolean execute(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      BlockState var3 = Module.client.world.getBlockState(var1.up());
      return this.fetch(var1.down()) && var2.getFluidState().isEmpty() && var3.getFluidState().isEmpty();
   }

   private boolean prepare(BlockPos var1) {
      return this.fetch(var1.down()) && Module.client.world.getBlockState(var1).isReplaceable() && Module.client.world.getBlockState(var1.up()).isReplaceable();
   }

   private boolean tick() {
      AncientXray var1 = this.measure();
      if (var1 == null) {
         return false;
      }

      BlockPos var2 = this.resolve(var1);
      if (var2 == null) {
         return false;
      }

      this.process(var2);
      return true;
   }

   private boolean check(BlockPos var1) {
      int var2 = this.handle(Blocks.TNT.asItem());
      if (var2 == -1) {
         ChatLogger.handle("[AutoAncient] TNT is missing from hotbar.");
         return false;
      } else {
         InventorySlotActions.handle(var2);
         this.playerMatch = false;
         BlockPos var3 = var1.down();
         BlockHitResult var4 = new BlockHitResult(new Vec3d(var1.getX() + 0.5, var1.getY(), var1.getZ() + 0.5), Direction.UP, var3, false);
         Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var4);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         return true;
      }
   }

   private boolean onTick(BlockPos var1) {
      int var2 = this.handle(Items.FLINT_AND_STEEL);
      if (var2 == -1) {
         ChatLogger.handle("[AutoAncient] Flint and steel is missing from hotbar.");
         this.toggle();
         return false;
      }

      BlockHitResult var3 = this.tick(var1);
      if (var3 == null) {
         return false;
      }

      InventorySlotActions.handle(var2);
      BlockHitResult var4 = this.encodePoint();
      BlockHitResult var5 = var4 != null && var4.getBlockPos().equals(var1) ? var4 : var3;
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var5);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      return true;
   }

   private AutoAncientBot.Mode handle(BlockPos var1, long var2) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         BlockHitResult var4 = this.tick(var1);
         if (var4 != null && !(Module.client.player.getEyePos().distanceTo(var4.getPos()) > 4.2)) {
            this.drawAnimation();
            RotationAngles var5 = this.handle(var4.getPos());
            this.handle(var5);
            if (new RotationAngles(Module.client.player).handle(var5) > 4.0F) {
               return AutoAncientBot.Mode.AIMING;
            }

            BlockHitResult var6 = this.encodePoint();
            BlockHitResult var7 = var6 != null && var6.getBlockPos().equals(var1) ? var6 : var4;
            if (!var1.equals(this.playerUpdate)) {
               if (!this.messageParse.update(90L)) {
                  return AutoAncientBot.Mode.AIMING;
               }

               Module.client.interactionManager.attackBlock(var1, var7.getSide());
               this.playerUpdate = var1.toImmutable();
               this.resultEncode.handle();
               this.messageParse.handle();
            } else {
               if (this.resultEncode.update(var2)) {
                  this.select(var1);
                  this.playerUpdate = null;
                  return AutoAncientBot.Mode.STUCK;
               }

               Module.client.interactionManager.updateBlockBreakingProgress(var1, var7.getSide());
            }

            Module.client.player.swingHand(Hand.MAIN_HAND);
            return AutoAncientBot.Mode.BREAKING;
         } else {
            return AutoAncientBot.Mode.NO_REACH;
         }
      } else {
         return AutoAncientBot.Mode.NO_REACH;
      }
   }

   private void select(BlockPos var1) {
      if (Module.client.getNetworkHandler() != null) {
         Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, var1, Direction.DOWN));
      }
   }

   private void drawAnimation() {
      IBaritone var1 = BaritoneAPI.getProvider().getPrimaryBaritone();
      if (var1.getPathingBehavior().isPathing() || var1.getCustomGoalProcess().isActive()) {
         var1.getPathingBehavior().cancelEverything();
      }

      this.pointSubmit = null;
   }

   private boolean refresh(IBaritone var1) {
      return var1.getPathingBehavior().isPathing() || var1.getCustomGoalProcess().isActive();
   }

   private void handle(IBaritone var1, BlockPos var2, int var3) {
      BlockPos var4 = var2.toImmutable();
      boolean var5 = !var4.equals(this.pointSubmit);
      if (var5 || !var1.getCustomGoalProcess().isActive() && this.providerRead.update(600L)) {
         var1.getCustomGoalProcess().setGoalAndPath(new GoalNear(var4, var3));
         this.pointSubmit = var4;
         this.providerRead.handle();
         if (var5) {
            this.fetch();
            this.handle("walk " + var4.toShortString());
         }
      }
   }

   private void handle(RotationAngles var1) {
      float var2 = new RotationAngles(Module.client.player).handle(var1);
      float var3 = Math.max(34.0F, Math.min(140.0F, var2 * 1.35F));
      RotationController.handle(var1, var3, var3, var3, var3, 2, 20, false);
   }

   private RotationAngles handle(Vec3d var1) {
      Vec3d var2 = Module.client.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)Math.toDegrees(Math.atan2(-var3, var7));
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      return new RotationAngles(var11, MathHelper.clamp(var12, -90.0F, 90.0F));
   }

   private BlockHitResult encodePoint() {
      double var1 = Math.toRadians(Module.client.player.getYaw());
      double var3 = Math.toRadians(Module.client.player.getPitch());
      double var5 = Math.cos(var3);
      Vec3d var7 = new Vec3d(-Math.sin(var1) * var5, -Math.sin(var3), Math.cos(var1) * var5);
      Vec3d var8 = Module.client.player.getEyePos();
      Vec3d var9 = var8.add(var7.multiply(4.6000000000000005));
      BlockHitResult var10 = Module.client.world.raycast(new RaycastContext(var8, var9, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      return var10.getType() == Type.BLOCK ? var10 : null;
   }

   private BlockHitResult refresh(BlockPos var1) {
      return this.handle(var1, 4.2);
   }

   private BlockHitResult handle(BlockPos var1, double var2) {
      BlockHitResult var4 = this.tick(var1);
      if (var4 == null) {
         Vec3d var5 = Module.client.player.getEyePos();
         BlockHitResult var6 = Module.client.world
            .raycast(new RaycastContext(var5, Vec3d.ofCenter(var1), ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
         if (var6.getType() != Type.BLOCK) {
            return null;
         }

         BlockPos var7 = var6.getBlockPos();
         if (!var7.equals(var1) && Module.client.world.getBlockState(var7).getHardness(Module.client.world, var7) < 0.0F) {
            return null;
         }

         var4 = var6;
      }

      return Module.client.player.getEyePos().distanceTo(var4.getPos()) > var2 ? null : var4;
   }

   private boolean render(BlockPos var1) {
      return var1 != null && this.handle(var1, 3.7) != null;
   }

   private BlockHitResult tick(BlockPos var1) {
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

   private void animate() {
      if (Module.client.options != null) {
         Module.client.options.attackKey.setPressed(false);
      }

      this.playerUpdate = null;
   }

   private boolean render(IBaritone var1) {
      boolean var2 = PlayerHelper.refresh();
      if (var2) {
         if (!this.eventReceive) {
            var1.getCommandManager().execute("pause");
            this.eventReceive = true;
            this.animate();
         }

         return true;
      } else {
         if (this.eventReceive) {
            var1.getCommandManager().execute("resume");
            this.eventReceive = false;
         }

         return false;
      }
   }

   private boolean tick(IBaritone var1) {
      if (Module.client.player == null || Module.client.interactionManager == null || Module.client.options == null) {
         return false;
      }

      if (this.layerSample2) {
         if (this.load() && this.elementTick >= 0 && this.handle(this.elementTick) && !this.animationExpand.update(3500L)) {
            InventorySlotActions.handle(this.elementTick);
            Module.client.options.useKey.setPressed(true);
            if (!Module.client.player.isUsingItem()) {
               Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            }

            return true;
         } else {
            this.drawAnimation(var1);
            return false;
         }
      } else {
         if (!this.load()) {
            this.sourceCancel = false;
            return false;
         }

         int var2 = this.save();
         if (var2 == -1) {
            if (!this.sourceCancel) {
               ChatLogger.handle("[AutoAncient] Fire resistance potion is missing from hotbar.");
               this.sourceCancel = true;
            }

            return false;
         } else {
            this.sourceCancel = false;
            this.animate(var1);
            this.layerSample2 = true;
            this.elementTick = var2;
            this.regionAlign = Module.client.player.getInventory().getSelectedSlot();
            this.animationExpand.handle();
            if (!this.keyCheck) {
               var1.getCommandManager().execute("pause");
               this.keyCheck = true;
            }

            this.animate();
            InventorySlotActions.handle(this.elementTick);
            Module.client.options.useKey.setPressed(true);
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            this.handle("drink fire res " + this.elementTick);
            return true;
         }
      }
   }

   private void drawAnimation(IBaritone var1) {
      if (Module.client.options != null) {
         Module.client.options.useKey.setPressed(false);
      }

      if (Module.client.player != null && this.regionAlign >= 0 && this.regionAlign < 9) {
         InventorySlotActions.handle(this.regionAlign);
      }

      if (var1 != null && this.keyCheck) {
         var1.getCommandManager().execute("resume");
      }

      this.keyCheck = false;
      this.layerSample2 = false;
      this.elementTick = -1;
      this.regionAlign = -1;
   }

   private boolean load() {
      if (Module.client.player == null) {
         return false;
      }

      StatusEffectInstance var1 = Module.client.player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);
      return var1 == null || var1.getDuration() <= 300;
   }

   private int save() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (this.handle(var1)) {
            return var1;
         }
      }

      return -1;
   }

   private boolean handle(int var1) {
      if (Module.client.player != null && var1 >= 0 && var1 <= 8) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.isOf(Items.POTION)) {
            PotionContentsComponent var3 = (PotionContentsComponent)var2.get(DataComponentTypes.POTION_CONTENTS);
            if (var3 == null) {
               return false;
            }

            for (StatusEffectInstance var5 : var3.getEffects()) {
               RegistryEntry var6 = var5.getEffectType();
               if (var6.equals(StatusEffects.FIRE_RESISTANCE)) {
                  return true;
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

   private boolean encodePoint(IBaritone var1) {
      if (Module.client.player == null || Module.client.interactionManager == null || Module.client.options == null) {
         return false;
      }

      if (this.entityFilter) {
         if (Module.client.player.getHungerManager().getFoodLevel() < 19
            && this.scaleParse >= 0
            && this.process(this.scaleParse)
            && !this.screenRead.update(7000L)) {
            InventorySlotActions.handle(this.scaleParse);
            Module.client.options.useKey.setPressed(true);
            if (!Module.client.player.isUsingItem()) {
               Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            }

            return true;
         } else {
            this.animate(var1);
            return false;
         }
      } else if (this.layerSample != AutoAncientBot.FallbackMode.PLACING_TNT
         && this.layerSample != AutoAncientBot.FallbackMode.IGNITING_TNT
         && this.layerSample != AutoAncientBot.FallbackMode.WAITING_EXPLOSION) {
         if (Module.client.player.getHungerManager().getFoodLevel() <= 16 && Module.client.player.canConsume(false)) {
            int var2 = this.submit();
            if (var2 == -1) {
               return false;
            }

            this.entityFilter = true;
            this.scaleParse = var2;
            this.sessionEncode = Module.client.player.getInventory().getSelectedSlot();
            this.screenRead.handle();
            if (!this.handlerRun) {
               var1.getCommandManager().execute("pause");
               this.handlerRun = true;
            }

            this.animate();
            InventorySlotActions.handle(this.scaleParse);
            Module.client.options.useKey.setPressed(true);
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            this.handle("eat " + this.scaleParse);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void animate(IBaritone var1) {
      if (Module.client.options != null) {
         Module.client.options.useKey.setPressed(false);
      }

      if (Module.client.player != null && this.sessionEncode >= 0 && this.sessionEncode < 9) {
         InventorySlotActions.handle(this.sessionEncode);
      }

      if (var1 != null && this.handlerRun) {
         var1.getCommandManager().execute("resume");
      }

      this.handlerRun = false;
      this.entityFilter = false;
      this.scaleParse = -1;
      this.sessionEncode = -1;
   }

   private int submit() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (this.process(var1)) {
            return var1;
         }
      }

      return -1;
   }

   private boolean process(int var1) {
      if (Module.client.player != null && var1 >= 0 && var1 <= 8) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         return !var2.isEmpty() && var2.contains(DataComponentTypes.FOOD);
      } else {
         return false;
      }
   }

   private boolean load(IBaritone var1) {
      if (Module.client.player == null || Module.client.world == null || Module.client.options == null) {
         return false;
      }

      if (!Module.client.player.isInLava()) {
         if (this.resourceClamp) {
            this.unload();
         }

         return false;
      } else {
         if (!this.resourceClamp) {
            this.playerCollapse.handle();
            this.process("Упал в лаву — выбираюсь");
         }

         this.resourceClamp = true;
         this.animate(var1);
         this.animate();
         this.playerUpdate = null;
         BaritoneAPI.getSettings().assumeWalkOnLava.value = true;
         if (this.packetRead == null || this.actionConvert.update(2500L)) {
            BlockPos var2 = this.compute(10);
            if (var2 != null) {
               this.packetRead = var2.toImmutable();
               this.actionConvert.handle();
               this.handle("lava escape " + this.packetRead.toShortString());
            }
         }

         if (this.packetRead != null) {
            this.handle(var1, this.packetRead, 1);
         }

         if (this.providerOffset == AutoAncientBot.PrimaryMode.IDLE && this.target.compute() && this.playerCollapse.update(3500L)) {
            BlockPos var3 = this.compute(24);
            if (var3 != null && this.handle(Vec3d.ofBottomCenter(var3), "выбираюсь из лавы")) {
               this.playerCollapse.handle();
               Module.client.options.jumpKey.setPressed(true);
               return true;
            }

            this.playerCollapse.handle();
         }

         Module.client.options.jumpKey.setPressed(true);
         return true;
      }
   }

   private void unload() {
      if (Module.client.options != null) {
         Module.client.options.jumpKey.setPressed(false);
      }

      if (this.screenSubmit) {
         BaritoneAPI.getSettings().assumeWalkOnLava.value = false;
      }

      this.resourceClamp = false;
      this.packetRead = null;
   }

   private BlockPos compute(int var1) {
      BlockPos var2 = Module.client.player.getBlockPos();
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;

      for (int var6 = -var1; var6 <= var1; var6++) {
         for (int var7 = -2; var7 <= 7; var7++) {
            for (int var8 = -var1; var8 <= var1; var8++) {
               BlockPos var9 = var2.add(var6, var7, var8);
               if (this.drawAnimation(var9)) {
                  double var10 = Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var9)) + Math.max(0, var7) * 0.6;
                  if (var10 < var4) {
                     var4 = var10;
                     var3 = var9.toImmutable();
                  }
               }
            }
         }
      }

      return var3;
   }

   private boolean drawAnimation(BlockPos var1) {
      return this.fetch(var1.down())
         && this.encodePoint(var1)
         && this.encodePoint(var1.up())
         && !this.animate(var1.down())
         && !this.animate(var1)
         && !this.animate(var1.up());
   }

   private boolean encodePoint(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      return var2.getFluidState().isEmpty() && var2.getCollisionShape(Module.client.world, var1).isEmpty();
   }

   private boolean animate(BlockPos var1) {
      return Module.client.world.getBlockState(var1).isOf(Blocks.LAVA);
   }

   private boolean load(BlockPos var1) {
      int var2 = 0;

      for (Direction var6 : Direction.values()) {
         BlockPos var7 = var1.offset(var6);
         if (this.animate(var7)) {
            var2++;
         } else if (this.save(var7)) {
            return false;
         }
      }

      return var2 == 0 ? false : this.optionParse.computeIfAbsent(var1.toImmutable(), var1x -> this.process(var1x, 4) == null);
   }

   private boolean save(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      return var2.getFluidState().isEmpty() && var2.getCollisionShape(Module.client.world, var1).isEmpty();
   }

   private boolean save(IBaritone var1) {
      if (Module.client.player != null && var1.getPathingBehavior().isPathing()) {
         Vec3d var2 = Module.client.player.getPos();
         if (this.eventSend != null && !(var2.squaredDistanceTo(this.eventSend) > 0.04)) {
            return this.keyProcess.update(2500L);
         }

         this.eventSend = var2;
         this.keyProcess.handle();
         return false;
      } else {
         this.fetch();
         return false;
      }
   }

   private void fetch() {
      this.eventSend = Module.client.player == null ? null : Module.client.player.getPos();
      this.keyProcess.handle();
   }

   private void submit(IBaritone var1) {
      this.animate();
      this.handle(Module.client.player.getBlockPos(), 1);
      this.drawAnimation();
      switch (this.layerSample) {
         case MOVING_SEARCH:
            if (this.listenerPerform != null) {
               this.handle(var1, this.listenerPerform);
            }
            break;
         case MOVING_SITE:
            if (this.configMatch != null) {
               this.handle(var1, this.configMatch);
            }
            break;
         case CLEARING_SITE:
            if (this.actionRender != null) {
               this.process(var1, this.actionRender);
            }
            break;
         case PLACING_TNT:
         case IGNITING_TNT:
            if (this.actionRender != null) {
               this.process(var1, this.actionRender);
            }
      }

      this.fetch();
      this.handle("path rebuild");
   }

   private void handle(IBaritone var1, BlockPos var2) {
      this.handle(var1, var2, 1);
   }

   private void process(IBaritone var1, BlockPos var2) {
      this.handle(var1, var2, 2);
   }

   private boolean submit(BlockPos var1) {
      return Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var1)) <= 9.0;
   }

   private int resolve(int var1) {
      return var1 + MathHelper.clamp(36 - var1, -4, 4);
   }

   private int update(int var1) {
      int var2 = Math.abs(var1 - 36);
      return Math.max(-120, 90 - var2 * 6);
   }

   private boolean unload(BlockPos var1) {
      return this.actionRender != null
         ? this.handle(var1, this.actionRender) <= 2304.0
         : this.layerSample == AutoAncientBot.FallbackMode.WAITING_EXPLOSION || this.layerSample == AutoAncientBot.FallbackMode.WAITING_SCAN;
   }

   private boolean process(BlockPos var1, double var2) {
      return Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var1)) <= var2;
   }

   private boolean fetch(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      return !var2.isReplaceable() && var2.getFluidState().isEmpty();
   }

   private boolean measure(BlockPos var1) {
      for (Direction var5 : Direction.values()) {
         Block var6 = Module.client.world.getBlockState(var1.offset(var5)).getBlock();
         if (this.handle(var6) || var6 == Blocks.LAVA) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(Block var1) {
      return var1 == Blocks.AIR || var1 == Blocks.CAVE_AIR || var1 == Blocks.VOID_AIR;
   }

   private boolean process(Block var1) {
      return var1 == Blocks.NETHERRACK
         || var1 == Blocks.BASALT
         || var1 == Blocks.SMOOTH_BASALT
         || var1 == Blocks.BLACKSTONE
         || var1 == Blocks.SOUL_SAND
         || var1 == Blocks.SOUL_SOIL
         || var1 == Blocks.GRAVEL
         || var1 == Blocks.NETHER_GOLD_ORE
         || var1 == Blocks.NETHER_QUARTZ_ORE;
   }

   private int handle(Item var1) {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var2 = 0; var2 < 9; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isOf(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private double handle(BlockPos var1, BlockPos var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getY() - var2.getY();
      double var7 = var1.getZ() - var2.getZ();
      return var3 * var3 + var5 * var5 + var7 * var7;
   }

   private double process(BlockPos var1, BlockPos var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return var3 * var3 + var5 * var5;
   }

   private void handle(AutoAncientBot.FallbackMode var1) {
      if (this.layerSample != var1) {
         this.handle(this.layerSample + " -> " + var1);
      }

      this.layerSample = var1;
      this.targetWrite.handle();
      this.matrixBlend2.handle();
   }

   private AncientXray measure() {
      return WildClient.instance.data.handle(AncientXray.class);
   }

   private void handle(String var1) {
      if (this.previous.compute()) {
         ChatLogger.handle("[AutoAncient] " + var1);
      }
   }

   private void process(String var1) {
      if (this.source.compute()) {
         ChatLogger.handle("[AutoAncient] " + var1);
      }
   }

   private void blendMatrix() {
      if (this.bufferAdapt != null && this.worldSend == AutoAncientBot.SecondaryMode.BREAKING) {
         if (!Module.client.world.getBlockState(this.bufferAdapt).isOf(Blocks.ANCIENT_DEBRIS)) {
            this.indexSynchronize++;
            this.process("Обломок добыт (всего: " + this.indexSynchronize + ")");
         }
      }
   }

   private int process(Item var1) {
      if (Module.client.player == null) {
         return 0;
      }

      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (var4.isOf(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private boolean unload(IBaritone var1) {
      if (this.providerOffset == AutoAncientBot.PrimaryMode.IDLE) {
         return false;
      }

      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null && Module.client.options != null) {
         if (Module.client.player.isInLava()) {
            Module.client.options.jumpKey.setPressed(true);
         }

         if (this.stateAttach >= 0 && this.providerOffset == AutoAncientBot.PrimaryMode.AWAITING) {
            InventorySlotActions.handle(this.stateAttach);
            this.stateAttach = -1;
         }

         if (this.providerOffset == AutoAncientBot.PrimaryMode.AIMING) {
            if (this.playerRun.update(2000L)) {
               this.handle("pearl aim timeout");
               this.matchVector();
               return false;
            }

            int var6 = this.handle(Items.ENDER_PEARL);
            if (var6 == -1) {
               this.matchVector();
               return false;
            }

            RotationAngles var7 = new RotationAngles(this.optionFetch, this.eventCollapse);
            this.handle(var7);
            if (new RotationAngles(Module.client.player).handle(var7) > 2.5F) {
               return true;
            }

            Vec3d var4 = Module.client.player.getEyePos().subtract(0.0, 0.1, 0.0);
            AutoAncientBot.State var5 = this.handle(var4, this.messageParse2);
            if (var5 != null && !(var5.context > 1.8) && this.process(var5.config)) {
               this.optionFetch = var5.instance;
               this.eventCollapse = var5.data;
               if (new RotationAngles(Module.client.player).handle(new RotationAngles(this.optionFetch, this.eventCollapse)) > 2.5F) {
                  return true;
               }

               this.stateAttach = Module.client.player.getInventory().getSelectedSlot();
               InventorySlotActions.handle(var6);
               Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
               Module.client.player.swingHand(Hand.MAIN_HAND);
               this.sourceRefresh++;
               this.matrixRender.handle();
               this.shaderProject = Module.client.player.getPos();
               this.providerOffset = AutoAncientBot.PrimaryMode.AWAITING;
               this.playerRun.handle();
               return true;
            } else {
               this.handle("pearl solution lost");
               this.matchVector();
               return false;
            }
         } else {
            boolean var2 = this.messageParse2 != null && Module.client.player.squaredDistanceTo(this.messageParse2) <= 25.0;
            boolean var3 = this.shaderProject != null && Module.client.player.getPos().squaredDistanceTo(this.shaderProject) > 64.0;
            if (var2 || var3) {
               this.process("Телепорт: " + this.inputInvoke);
               this.worldEvaluate = 0;
               this.playerProject = null;
               this.fetch(var1);
               this.matchVector();
               return false;
            } else if (this.playerRun.update(5000L)) {
               this.worldEvaluate++;
               this.playerProject = this.messageParse2;
               this.process("Пёрка не долетела — эта цель в бане, иду пешком");
               this.matchVector();
               return false;
            } else {
               return true;
            }
         }
      } else {
         this.matchVector();
         return false;
      }
   }

   private void fetch(IBaritone var1) {
      this.handle(Module.client.player.getBlockPos(), 1);
      this.pointSubmit = null;
      if (this.layerSample == AutoAncientBot.FallbackMode.MINING) {
         this.worldSend = AutoAncientBot.SecondaryMode.APPROACHING;
         this.playerUpdate = null;
         this.scalePerform.handle();
      } else {
         this.submit(var1);
      }

      this.fetch();
   }

   private void matchVector() {
      if (this.stateAttach >= 0) {
         InventorySlotActions.handle(this.stateAttach);
         this.stateAttach = -1;
      }

      this.providerOffset = AutoAncientBot.PrimaryMode.IDLE;
      this.messageParse2 = null;
      this.inputInvoke = null;
      this.shaderProject = null;
   }

   private boolean handle(BlockPos var1, String var2) {
      if (var1 == null || !this.target.compute() || this.providerOffset != AutoAncientBot.PrimaryMode.IDLE) {
         return false;
      }

      if (!this.projectItem()) {
         return false;
      }

      Vec3d var3 = Vec3d.ofCenter(var1);
      if (var3.y - Module.client.player.getY() > 2.5) {
         return false;
      }

      double var4 = var3.x - Module.client.player.getX();
      double var6 = var3.z - Module.client.player.getZ();
      double var8 = var4 * var4 + var6 * var6;
      double var10 = this.pending.compute();
      if (var8 < var10 * var10) {
         return false;
      }

      if (!this.moduleTick.update(1200L)) {
         return false;
      }

      this.moduleTick.handle();
      BlockPos var12 = var1;
      if (var8 > 729.0) {
         Vec3d var13 = var3.subtract(Module.client.player.getPos()).normalize();
         var12 = BlockPos.ofFloored(Module.client.player.getPos().add(var13.multiply(27.0)));
      }

      BlockPos var14 = this.process(var12, 5);
      return var14 != null && this.handle(Vec3d.ofBottomCenter(var14), var2);
   }

   private boolean projectItem() {
      long var1 = 2500L * (1L + Math.min(this.worldEvaluate, 3));
      return this.matrixRender.update(var1);
   }

   private boolean handle(Vec3d var1, String var2) {
      if (!this.target.compute() || this.providerOffset != AutoAncientBot.PrimaryMode.IDLE || var1 == null) {
         return false;
      } else if (Module.client.player == null || Module.client.interactionManager == null) {
         return false;
      } else if (this.entityFilter || this.layerSample2) {
         return false;
      } else if (!this.projectItem()) {
         return false;
      } else if (this.handle(Items.ENDER_PEARL) == -1) {
         return false;
      } else {
         boolean var3 = Module.client.player.isInLava();
         if (!var3 && Module.client.player.getHealth() < 8.0F) {
            return false;
         } else if (!var3 && var1.y - Module.client.player.getY() > 2.5) {
            return false;
         } else if (this.playerProject != null && var1.squaredDistanceTo(this.playerProject) < 16.0) {
            return false;
         } else {
            Vec3d var4 = Module.client.player.getEyePos().subtract(0.0, 0.1, 0.0);
            AutoAncientBot.State var5 = this.handle(var4, var1);
            if (var5 != null && !(var5.context > 1.8) && this.process(var5.config)) {
               this.messageParse2 = var1;
               this.inputInvoke = var2;
               this.optionFetch = var5.instance;
               this.eventCollapse = var5.data;
               this.providerOffset = AutoAncientBot.PrimaryMode.AIMING;
               this.playerRun.handle();
               this.animate();
               this.drawAnimation();
               this.process("Кидаю пёрку: " + var2 + " → " + (int)Math.floor(var1.x) + " " + (int)Math.floor(var1.y) + " " + (int)Math.floor(var1.z));
               return true;
            } else {
               this.handle("pearl no solution: " + var2);
               return false;
            }
         }
      }
   }

   private BlockPos process(BlockPos var1, int var2) {
      BlockPos var3 = null;
      double var4 = Double.MAX_VALUE;

      for (int var6 = -var2; var6 <= var2; var6++) {
         for (int var7 = -var2; var7 <= var2; var7++) {
            for (int var8 = -var2; var8 <= var2; var8++) {
               BlockPos var9 = var1.add(var6, var7, var8);
               if (this.drawAnimation(var9)) {
                  double var10 = this.handle(var9, var1);
                  if (var10 < var4) {
                     var4 = var10;
                     var3 = var9.toImmutable();
                  }
               }
            }
         }
      }

      return var3;
   }

   private boolean process(Vec3d var1) {
      BlockPos var2 = BlockPos.ofFloored(var1);
      if (!this.animate(var2) && !this.animate(var2.up())) {
         for (int var3 = 1; var3 <= 4; var3++) {
            BlockPos var4 = var2.down(var3);
            if (this.animate(var4)) {
               return false;
            }

            if (this.fetch(var4)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private AutoAncientBot.State handle(Vec3d var1, Vec3d var2) {
      double var3 = var2.x - var1.x;
      double var5 = var2.z - var1.z;
      float var7 = (float)Math.toDegrees(Math.atan2(-var3, var5));
      AutoAncientBot.State var8 = null;

      for (float var9 = -6.0F; var9 <= 6.0F; var9 += 2.0F) {
         float var10 = var7 + var9;

         for (float var11 = -40.0F; var11 <= 80.0F; var11 += 2.0F) {
            AutoAncientBot.State var12 = this.handle(var1, var2, var10, var11);
            if (var12 != null && (var8 == null || var12.context < var8.context)) {
               var8 = var12;
            }
         }
      }

      if (var8 == null) {
         return null;
      }

      AutoAncientBot.State var13 = var8;

      for (float var14 = var8.instance - 2.0F; var14 <= var8.instance + 2.0F; var14 += 0.5F) {
         for (float var15 = var8.data - 2.0F; var15 <= var8.data + 2.0F; var15 += 0.3F) {
            AutoAncientBot.State var16 = this.handle(var1, var2, var14, var15);
            if (var16 != null && var16.context < var13.context) {
               var13 = var16;
            }
         }
      }

      return var13;
   }

   private AutoAncientBot.State handle(Vec3d var1, Vec3d var2, float var3, float var4) {
      Vec3d var5 = this.handle(var3, var4);
      Vec3d var6 = this.process(var1, var5);
      if (var6 == null) {
         return null;
      }

      double var7 = Math.sqrt(var6.squaredDistanceTo(var2));
      return new AutoAncientBot.State(MathHelper.wrapDegrees(var3), MathHelper.clamp(var4, -90.0F, 90.0F), var7, var6);
   }

   private Vec3d handle(float var1, float var2) {
      float var3 = var1 * (float) (Math.PI / 180.0);
      float var4 = var2 * (float) (Math.PI / 180.0);
      double var5 = -MathHelper.sin(var3) * MathHelper.cos(var4);
      double var7 = -MathHelper.sin(var4);
      double var9 = MathHelper.cos(var3) * MathHelper.cos(var4);
      Vec3d var11 = new Vec3d(var5, var7, var9).normalize().multiply(1.5);
      Vec3d var12 = Module.client.player.getMovement();
      return var11.add(var12.x, Module.client.player.isOnGround() ? 0.0 : var12.y, var12.z);
   }

   private Vec3d process(Vec3d var1, Vec3d var2) {
      if (Module.client.world == null) {
         return null;
      }

      Vec3d var3 = var1;
      Vec3d var4 = var2;

      for (int var5 = 0; var5 < 160; var5++) {
         var4 = var4.subtract(0.0, 0.03, 0.0).multiply(0.99);
         Vec3d var6 = var3.add(var4);
         BlockHitResult var7 = Module.client.world.raycast(new RaycastContext(var3, var6, ShapeType.COLLIDER, FluidHandling.NONE, Module.client.player));
         if (var7.getType() != Type.MISS) {
            return var7.getPos();
         }

         var3 = var6;
      }

      return var3;
   }

   private void computeResponse() {
      Settings var1 = BaritoneAPI.getSettings();
      this.cacheHandle = (Boolean)var1.allowPlace.value;
      this.rangeRelease = (Boolean)var1.allowBreak.value;
      this.indexSave = (Boolean)var1.assumeWalkOnLava.value;
      this.indexCheck = (Boolean)var1.walkWhileBreaking.value;
      List var2 = (List)var1.blocksToAvoid.value;
      this.settingSchedule = var2 == null ? List.of() : new ArrayList<>(var2);
      List var3 = (List)var1.acceptableThrowawayItems.value;
      this.inputAcquire = var3 == null ? List.of() : new ArrayList<>(var3);
      var1.allowPlace.value = true;
      var1.allowBreak.value = true;
      var1.assumeWalkOnLava.value = false;
      var1.walkWhileBreaking.value = false;
      if (var2 != null) {
         var2.remove(Blocks.LAVA);
      }

      if (var3 != null) {
         var3.remove(Blocks.TNT.asItem());
         this.handle(var3, Blocks.NETHERRACK.asItem());
         this.handle(var3, Blocks.BLACKSTONE.asItem());
         this.handle(var3, Blocks.BASALT.asItem());
         this.handle(var3, Blocks.COBBLESTONE.asItem());
      }

      this.screenSubmit = true;
   }

   private void fetchProvider() {
      if (this.screenSubmit) {
         Settings var1 = BaritoneAPI.getSettings();
         var1.allowPlace.value = this.cacheHandle;
         var1.allowBreak.value = this.rangeRelease;
         var1.assumeWalkOnLava.value = this.indexSave;
         var1.walkWhileBreaking.value = this.indexCheck;
         List var2 = (List)var1.blocksToAvoid.value;
         if (var2 != null) {
            var2.clear();
            var2.addAll(this.settingSchedule);
         }

         List var3 = (List)var1.acceptableThrowawayItems.value;
         if (var3 != null) {
            var3.clear();
            var3.addAll(this.inputAcquire);
         }

         this.screenSubmit = false;
      }
   }

   private void handle(List<Item> var1, Item var2) {
      if (!var1.contains(var2)) {
         var1.add(var2);
      }
   }

   enum FallbackMode {
      SEARCHING,
      MOVING_SEARCH,
      MOVING_SITE,
      CLEARING_SITE,
      PLACING_TNT,
      IGNITING_TNT,
      WAITING_EXPLOSION,
      WAITING_SCAN,
      MINING;
   }

   enum Mode {
      AIMING,
      BREAKING,
      STUCK,
      NO_REACH;
   }

   enum PrimaryMode {
      IDLE,
      AIMING,
      AWAITING;
   }

   enum SecondaryMode {
      APPROACHING,
      BREAKING;
   }

   static final class State {
      final float instance;
      final float data;
      final double context;
      final Vec3d config;

      State(float var1, float var2, double var3, Vec3d var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var5;
      }
   }
}
