package ru.wild.modules.combat;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.CameraRotationEvent;
import ru.wild.api.event.ClientTickEvent;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.DynamicActionSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.combat.AcceleratedAimPattern;
import ru.wild.automation.combat.AdaptiveAttackTiming;
import ru.wild.automation.combat.AdaptiveJitterAimPattern;
import ru.wild.automation.combat.AttackAimSequencer;
import ru.wild.automation.combat.AuraRotationPlanner;
import ru.wild.automation.combat.AuraTargetingStrategy;
import ru.wild.automation.combat.BoundingBoxAimPattern;
import ru.wild.automation.combat.CombatRotationPlanner;
import ru.wild.automation.combat.NeuroEngine;
import ru.wild.automation.combat.OrganicAimPattern;
import ru.wild.automation.combat.PredictiveAimPattern;
import ru.wild.automation.combat.RotationEngine;
import ru.wild.automation.combat.RotationRecorder;
import ru.wild.automation.combat.SensitivityAimPattern;
import ru.wild.automation.combat.ServerTags;
import ru.wild.automation.combat.SmartCritsHelper;
import ru.wild.automation.combat.SnapAimPattern;
import ru.wild.core.manager.FeatureManager;
import ru.wild.core.manager.FriendManager;
import ru.wild.gui.screen.RotationAnalyticsScreen;
import ru.wild.gui.screen.RotationBuilderScreen;
import ru.wild.modules.misc.FreeLock;
import ru.wild.modules.misc.ServerHelper;
import ru.wild.modules.movement.Sprint;
import ru.wild.modules.player.ClickPearl;
import ru.wild.network.MovementPacketTracker;
import ru.wild.util.player.CameraAimProjection;
import ru.wild.util.player.EntityAimGeometry;
import ru.wild.util.player.LocalhostHelper;
import ru.wild.util.player.MovementPhysics;
import ru.wild.util.player.SyntheticKeyState;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(
   name = "AttackAura",
   description = "Автоматически бьет энтити - таргетов",
   category = ModuleCategory.Combat,
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class AttackAura extends Module {
   public static NumberSetting source = new NumberSetting("Радиус атаки", 3.0F, 3.0F, 6.0F, 0.1F, false);
   public static NumberSetting target = new NumberSetting("Радиус обнаружения", 1.0F, 0.0F, 5.0F, 0.1F, false);
   public static ModeSetting pending = new ModeSetting("Режим ротации", "Smooth", animate());
   public static ActionSetting previous = new ActionSetting("Конструктор ротации", 0)
      .process("Открыть")
      .handle(AttackAura::refreshClient)
      .handle(() -> !pending.process("Custom"));
   public static NumberSetting latest = new NumberSetting("AI Jitter", 1.0F, 0.0F, 2.0F, 0.05F, false).handle(() -> !pending.process("AI"));
   public static BooleanSetting summary = new BooleanSetting("AI Debug Log", false).handle(() -> !pending.process("AI"));
   public static BooleanSetting matrixBlend = new BooleanSetting("AI Human Misses", false).handle(() -> !pending.process("AI"));
   public static ActionSetting vectorMatch = new ActionSetting("AI Lab", 0)
      .process("Открыть")
      .handle(AttackAura::filterKey)
      .handle(() -> !pending.process("AI"));
   public static ModeSetting itemProject = new ModeSetting("Режим снапа", "Fast", "Fast", "Smooth", "Random")
      .handle(() -> !pending.process("Snap") && !pending.process("FOV"));
   public static NumberSetting responseCompute = new NumberSetting("FOV", 90.0F, 5.0F, 180.0F, 1.0F, true).handle(() -> !pending.process("FOV"));
   public static BooleanSetting providerFetch = new BooleanSetting("Отображать FOV", true).handle(() -> !pending.process("FOV"));
   public static NumberSetting profileDraw = new NumberSetting("Скорость Legit", 0.08F, 0.02F, 0.4F, 0.01F, false).handle(() -> !pending.process("Legit"));
   public static BooleanSetting vectorPerform = new BooleanSetting("SidePoint Extra Checks", false).handle(() -> !pending.process("Side Point"));
   public static DynamicActionSetting eventAttach = new DynamicActionSetting("Neuro Status", 0, NeuroEngine::compute)
      .process(() -> !pending.process("Neuro") || !load());
   public static BooleanSetting serverRead = new BooleanSetting("Neuro Debug", false).handle(() -> !pending.process("Neuro") || !load());
   public static ModeSetting positionAdvance = new ModeSetting("Neuro Profile", "Human", "Stable", "Human", "Dynamic")
      .handle(() -> !pending.process("Neuro") || !load());
   public static NumberSetting frameCheck = new NumberSetting("Neuro Strength", 1.25F, 0.0F, 2.0F, 0.05F, false)
      .handle(() -> !pending.process("Neuro") || !load());
   public static BooleanSetting moduleCollect = new BooleanSetting("Neuro Client Finish", false).handle(() -> !pending.process("Neuro") || !load());
   public static ChoiceSetting providerClose = new ChoiceSetting(
      "Цели",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Голые", true),
      new BooleanSetting("Невидимки", true),
      new BooleanSetting("Голые невидимки", false),
      new BooleanSetting("Друзья", false),
      new BooleanSetting("NPC", true),
      new BooleanSetting("Мобы", false),
      new BooleanSetting("Животные", false),
      new BooleanSetting("Жители", false)
   );
   public static ModeSetting presetSave = new ModeSetting("Тайминг удара", "Быстрый", "Быстрый", "Динамичный");
   public static BooleanSetting windowConvert = new BooleanSetting("Адаптивный тайминг", false);
   public static ModeSetting presetWrite = new ModeSetting("Режим спринта", "Обычный", "Обычный", "Обновленный", "Тестовый", "Легит");
   public static ChoiceSetting colorMeasure = new ChoiceSetting(
      "Проверки до удара",
      new BooleanSetting("Бить через блоки", false),
      new BooleanSetting("Бить только оружием", false),
      new BooleanSetting("Не бить если кушаешь", true),
      new BooleanSetting("Не бить в контейнерах ", false),
      new BooleanSetting("Ломать щит", false),
      new BooleanSetting("Отжим щита", false)
   );
   public static ChoiceSetting animationSchedule = new ChoiceSetting(
      "Дополнительные настройки",
      new BooleanSetting("Расширенная настройки для атаки", true),
      new BooleanSetting("Умные криты", false),
      new BooleanSetting("Увеличенная дистанция удара", false),
      new BooleanSetting("Приоритет ближайшей цели", false)
   );
   public static NumberSetting rendererScan = new NumberSetting("Радиус атаки для мобов", 3.0F, 3.0F, 6.0F, 0.1F, false)
      .handle(() -> !animationSchedule.process("Расширенная настройки для атаки") && !providerClose.process("Мобы"));
   public static NumberSetting sourceBuild = new NumberSetting("Радиус атаки для игроков", 3.0F, 3.0F, 6.0F, 0.1F, false)
      .handle(() -> !animationSchedule.process("Расширенная настройки для атаки") && !providerClose.process("Игроки"));
   public static ModeSetting outputCollapse = new ModeSetting("Режим движения", "Default", "Default", "Free", "Target", "Преследование");
   public static BooleanSetting profileInvoke = new BooleanSetting("Булава", false);
   public static ModeSetting sourceSchedule = new ModeSetting("Режим булавы", "Авто", "Авто", "Бинд").handle(() -> !profileInvoke.compute());
   public static KeybindSetting timerRender = new KeybindSetting("Кнопка булавы", -1).handle(() -> !profileInvoke.compute() || !sourceSchedule.process("Бинд"));
   public static NumberSetting scaleSave = new NumberSetting("Высота булавы", 2.0F, 0.5F, 6.0F, 0.1F, false)
      .handle(() -> !profileInvoke.compute() || !sourceSchedule.process("Авто"));
   public static BooleanSetting colorCompute = new BooleanSetting("Усиление урона", false).handle(() -> !profileInvoke.compute());
   public static BooleanSetting scaleAdapt = new BooleanSetting("Отладка булавы", false).handle(() -> !profileInvoke.compute());
   public static LivingEntity textureRun;
   public static boolean indexBind = false;
   private static final Runnable requestAdapt = AdaptiveJitterAimPattern::handle;
   private static final Runnable timerMeasure = AttackAimSequencer::handle;
   private static long vectorEncode = 0L;
   private static boolean requestReceive = false;
   private static float windowProcess = 0.0F;
   private static long packetSave = 0L;
   private static float entryAnimate = 0.0F;
   private static long playerCollect = 0L;
   private static long stateApply = 0L;
   private static int matrixFilter = Integer.MIN_VALUE;
   private boolean layerSample = false;
   private boolean worldSend = false;
   private static final String targetWrite = "AuraMace";
   private static final int resultEncode = 40;
   private static final int messageParse = 4;
   private static final long providerRead = 300L;
   static int matrixBlend2 = 0;
   private static int scalePerform = 0;
   private static boolean contextExpand = false;
   private static int keyProcess = -1;
   private static int actionConvert = -1;
   private static boolean screenRead = false;
   private static int animationExpand = -1;
   private static boolean playerRun = false;
   private static boolean matrixRender = false;
   private static int moduleTick = 0;
   private static int playerCollapse = 0;
   private static long optionAdvance = 0L;
   public static long actionRead = 0L;
   public static long configCollapse = ThreadLocalRandom.current().nextLong(90000L, 180000L);
   public static boolean dataValidate = false;
   public static long scaleRender = 0L;
   public static int clientRefresh = 0;

   private static String[] animate() {
      return load()
         ? new String[]{
            "Matrix", "Random Smooth ", "Snap", "FOV", "Smooth ", "FunTime", "FT-New", "FTTESTT", "SpookyTime", "ST-Test", "Legit", "Custom", "AI", "Neuro"
         }
         : new String[]{"Matrix", "Random Smooth ", "Snap", "FOV", "Smooth ", "FunTime", "FT-New", "SpookyTime", "ST-Test", "Legit", "Custom", "AI"};
   }

   private static boolean load() {
      return FeatureManager.handle(AttackAura.State.class.getAnnotation(ModuleRoles.class));
   }

   private static boolean save() {
      return (pending.process("Neuro") || pending.process("FTTESTT")) && !load();
   }

   public AttackAura() {
      AdaptiveAttackTiming.onTick();
      this.handle(
         source,
         target,
         pending,
         previous,
         latest,
         summary,
         matrixBlend,
         vectorMatch,
         itemProject,
         responseCompute,
         providerFetch,
         profileDraw,
         eventAttach,
         serverRead,
         positionAdvance,
         frameCheck,
         moduleCollect,
         providerClose,
         presetSave,
         windowConvert,
         presetWrite,
         colorMeasure,
         animationSchedule,
         rendererScan,
         sourceBuild,
         outputCollapse,
         profileInvoke,
         sourceSchedule,
         timerRender,
         scaleSave,
         colorCompute,
         scaleAdapt,
         ElytraTarget.previous
      );
   }

   public static boolean refresh() {
      return matrixBlend2 != 0;
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (this.enabled && !drawAnimation() && pending.process("FOV") && var1.resolve() != null && providerFetch.compute()) {
         CameraAimProjection.handle(var1.resolve(), responseCompute.compute(), var1.apply(), var1.execute());
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      SmartCritsHelper.compute();
      if (drawAnimation()) {
         AdaptiveJitterAimPattern.compute();
         AuraRotationPlanner.handle();
         AttackAimSequencer.process();
         this.adaptRequest();
         this.measureTimer();
      } else if (textureRun != null && Module.client.player != null && Module.client.world != null) {
         this.readAction();
         if (!pending.process("Legit")) {
            this.fetch();
         }
      } else {
         AdaptiveJitterAimPattern.compute();
         AuraRotationPlanner.handle();
         AttackAimSequencer.process();
         this.adaptRequest();
         this.measureTimer();
      }
   }

   @EventHandler
   public void handle(CameraRotationEvent var1) {
      if (!drawAnimation() && pending.process("Legit")) {
         if (textureRun != null && Module.client.player != null && Module.client.world != null) {
            SensitivityAimPattern.handle(textureRun, var1);
         }
      }
   }

   @EventHandler
   public void handle(MovementInputEvent var1) {
      if (!drawAnimation() && presetWrite.process("Тестовый")) {
         if (textureRun != null && Module.client.player != null && Module.client.world != null && Module.client.currentScreen == null) {
            Vec3d var2 = textureRun.getPos().add(0.0, textureRun.getHeight() * 0.5, 0.0).subtract(Module.client.player.getEyePos());
            float var3 = (float)Math.toDegrees(Math.atan2(-var2.x, var2.z));
            MovementPhysics.handle(var1, var3);
            if (AdaptiveAttackTiming.process(textureRun, process(textureRun))) {
               var1.handle(0.0F);
               var1.process(0.0F);
            }
         }
      }
   }

   @EventHandler
   public void handle(ClientTickEvent var1) {
      if (!drawAnimation()) {
         if (colorMeasure.process("Синхрон с ТПС")) {
            this.readAction();
            if (!presetWrite.process("Легит") && this.computeResponse()) {
               Module.client.player.setSprinting(false);
               Module.client.options.sprintKey.setPressed(false);
            }

            if (!this.advancePosition()) {
               this.tick();
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      this.collectModule();
      if (drawAnimation()) {
         this.adaptRequest();
         this.blendMatrix();
         this.fetchProvider();
         MovementPhysics.compute();
      } else if (!Module.client.player.isAlive()) {
         this.adaptRequest();
         this.blendMatrix();
         this.fetchProvider();
         MovementPhysics.compute();
         this.toggle();
      } else {
         if (textureRun == null || !this.compute(textureRun)) {
            this.performVector();
         }

         if (textureRun == null) {
            AdaptiveJitterAimPattern.compute();
            ServerTags.resolve();
            AttackAimSequencer.process();
            this.adaptRequest();
            this.measureTimer();
            this.blendMatrix();
            this.fetchProvider();
            MovementPhysics.compute();
         } else if (Module.client.currentScreen != null) {
            this.blendMatrix();
            this.fetchProvider();
            this.drawProfile();
         } else if (matrixBlend2 != 0) {
            this.blendMatrix();
            this.fetchProvider();
            MovementPhysics.compute();
            SyntheticKeyState.handle().handle("AuraMace");
         } else {
            this.matchVector();
            if (outputCollapse.process("Free")) {
               this.blendMatrix();
               MovementPhysics.handle(Module.client.gameRenderer.getCamera().getYaw());
            } else if (outputCollapse.process("Target")) {
               this.blendMatrix();
               MovementPhysics.handle(Module.client.player.getYaw(), textureRun.getPos());
            } else {
               MovementPhysics.compute();
            }

            if (outputCollapse.process("Преследование")) {
               this.measure();
            } else {
               this.blendMatrix();
            }

            if (!colorMeasure.process("Синхрон с ТПС")) {
               this.readAction();
               if (!presetWrite.process("Легит") && this.computeResponse()) {
                  Module.client.player.setSprinting(false);
                  Module.client.options.sprintKey.setPressed(false);
               }

               if (!this.advancePosition()) {
                  this.tick();
               }
            }
         }
      }
   }

   public static float handle(LivingEntity var0) {
      if (var0 == null) {
         return source.compute();
      }

      float var1 = source.compute();
      if (animationSchedule.process("Расширенная настройки для атаки")) {
         if (var0 instanceof PlayerEntity) {
            var1 = sourceBuild.compute();
         } else {
            var1 = rendererScan.compute();
         }
      }

      if (animationSchedule.process("Увеличенная дистанция удара")) {
         float var2 = var0.getHealth() + var0.getAbsorptionAmount();
         if (var2 >= 10.0F && var2 <= 12.0F) {
            long var3 = System.currentTimeMillis();
            if (var3 >= vectorEncode) {
               if (ThreadLocalRandom.current().nextInt(100) < 25) {
                  requestReceive = true;
                  windowProcess = 0.1F + ThreadLocalRandom.current().nextFloat() * 0.05F;
                  vectorEncode = var3 + ThreadLocalRandom.current().nextLong(400L, 700L);
               } else {
                  requestReceive = false;
                  windowProcess = 0.0F;
                  vectorEncode = var3 + ThreadLocalRandom.current().nextLong(1500L, 2500L);
               }
            }

            if (requestReceive) {
               return var1 + windowProcess;
            }
         } else {
            requestReceive = false;
            windowProcess = 0.0F;
         }
      }

      return var1;
   }

   public static float[] process(LivingEntity var0) {
      float var1 = handle(var0);
      return new float[]{var1, target.compute(), var1 + target.compute()};
   }

   public boolean render() {
      return true;
   }

   public void tick() {
      assert Module.client.player != null;
      if (matrixBlend2 == 0) {
         float var1 = handle(textureRun);
         if (!(EntityAimGeometry.handle((Entity)textureRun) >= var1)) {
            float[] var2 = process(textureRun);
            var2 = new float[]{var2[0], var2[1], var2[0] + var2[1]};
            if (!Module.client.player.hasStatusEffect(StatusEffects.BLINDNESS) && !Module.client.player.isFlyingVehicle() && presetWrite.process("Обычный")) {
               boolean var20 = true;
            } else {
               boolean var10000 = false;
            }

            if (textureRun != null) {
               if (!pending.process("FOV") || CameraAimProjection.handle(textureRun, responseCompute.compute())) {
                  if (!pending.process("AI") || RotationRecorder.fetch()) {
                     AdaptiveAttackTiming.handle(textureRun, true, this.render(), false);
                     boolean var4 = pending.process("FT-New");
                     boolean var5 = profileInvoke.compute() && Module.client.player.getMainHandStack().getItem() == Items.MACE;
                     boolean var6 = var5 && colorCompute.compute();
                     if (!var6 || this.submit()) {
                        boolean var7;
                        if (var5 && contextExpand) {
                           long var8 = sourceSchedule.process("Авто") ? -2000L : 0L;
                           boolean var10 = EntityAimGeometry.handle(textureRun, var2[0], true);
                           boolean var11 = AdaptiveAttackTiming.handle(var8);
                           boolean var12 = !this.render() || AdaptiveAttackTiming.handle(textureRun, var2[0]);
                           boolean var13 = !sourceSchedule.process("Авто") || !matrixRender;
                           var7 = matrixBlend2 == 0 && !playerRun && var13 && var10 && var11 && var12;
                        } else if (var4) {
                           var7 = SmartCritsHelper.handle(textureRun, 0) && (!this.render() || AdaptiveAttackTiming.handle(textureRun, var2[0]));
                        } else {
                           var7 = AdaptiveAttackTiming.handle(textureRun, this.render(), true, true, validateData(), var2);
                        }

                        if (var7) {
                           if (var4) {
                              if (!SmartCritsHelper.process(textureRun)) {
                                 return;
                              }

                              if (!SmartCritsHelper.handle(colorMeasure.process("Отжим щита"))) {
                                 return;
                              }
                           }

                           if (var4 || !windowConvert.compute() || var6 || !AdaptiveAttackTiming.handle(textureRun)) {
                              Runnable[] var15 = AdaptiveAttackTiming.handle(textureRun, !var4 && colorMeasure.process("Ломать щит"));
                              Runnable[] var9 = AdaptiveAttackTiming.handle(!var4);
                              Runnable[] var16 = AdaptiveAttackTiming.process(false);
                              Runnable var17 = () -> {
                                 var16[0].run();
                                 var9[0].run();
                                 var15[0].run();
                              };
                              Runnable var18 = () -> {
                                 var15[1].run();
                                 var9[1].run();
                                 var16[1].run();
                              };
                              if (!var4
                                 && colorMeasure.process("Отжим щита")
                                 && Module.client.player.getActiveItem().getItem().equals(Items.SHIELD)
                                 && Module.client.player.isUsingItem()) {
                                 Module.client.interactionManager.stopUsingItem(Module.client.player);
                              }

                              if (!var4 && windowConvert.compute() && !var6) {
                                 Runnable var19 = pending.process("FunTime") ? requestAdapt : (pending.process("ST-Test") ? timerMeasure : null);
                                 AdaptiveAttackTiming.handle(textureRun, var17, var18, Hand.MAIN_HAND, true, var19);
                              } else {
                                 if (AdaptiveAttackTiming.handle(textureRun, var17, var18, Hand.MAIN_HAND, true)) {
                                    if (pending.process("FunTime")) {
                                       AdaptiveJitterAimPattern.handle();
                                    } else if (var4) {
                                       SmartCritsHelper.resolve();
                                    } else if (pending.process("SpookyTime")) {
                                       OrganicAimPattern.handle();
                                    } else if (pending.process("ST-Test")) {
                                       AttackAimSequencer.handle();
                                    }

                                    this.computeColor();
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean submit() {
      if (Module.client.world != null && this.unload()) {
         Vec3d var1 = Module.client.player.getVelocity();
         Box var2 = Module.client.player.getBoundingBox();
         Vec3d var3 = Entity.adjustMovementForCollisions(
            Module.client.player, var1, var2, Module.client.world, Module.client.world.getEntityCollisions(Module.client.player, var2.stretch(var1))
         );
         return var3.y > var1.y + 1.0E-7;
      } else {
         return true;
      }
   }

   private boolean unload() {
      return Module.client.player != null && !Module.client.player.isOnGround() && !Module.client.player.isGliding() && Module.client.player.getVelocity().y < -1.0E-4;
   }

   private void fetch() {
      if (textureRun != null && !drawAnimation() && !save() && (matrixBlend2 == 0 || screenRead)) {
         if (!pending.process("FT-New")) {
            PredictiveAimPattern.compute();
            AuraRotationPlanner.process();
         }

         if (!pending.process("ST-Test")) {
            AttackAimSequencer.process();
         }

         this.readAction();
         double var1 = Module.client.player.getY() - textureRun.getY();
         boolean var3 = Module.client.player.getMainHandStack().getItem() == Items.MACE;
         boolean var4 = !Module.client.player.isOnGround() && (var1 >= 2.0 || var3);
         if (!pending.process("Legit") && var3 && var4 && Module.client.player.getY() > textureRun.getY()) {
            RotationEngine.handle(textureRun);
         } else if (!pending.process("Legit") && profileInvoke.compute() && var3) {
            SnapAimPattern.handle(textureRun);
         } else {
            float[] var5 = process(textureRun);
            var5 = new float[]{var5[0], var5[1], var5[0] + var5[1]};
            boolean var6 = AdaptiveAttackTiming.handle(textureRun, false, true, true, validateData(), var5);
            switch (pending.compute()) {
               case "Random Smooth ":
                  AcceleratedAimPattern.handle(
                     textureRun, AdaptiveAttackTiming.handle(textureRun, false, true, true, handle(-50L), var5), handle(textureRun), this.advancePosition()
                  );
                  break;
               case "Matrix":
                  if (LocalhostHelper.handle("spookytime")) {
                     RotationEngine.handle(textureRun, var6);
                  } else if (LocalhostHelper.handle("holy")) {
                     RotationEngine.process(textureRun, var6);
                  } else if (LocalhostHelper.handle("ares")) {
                     RotationEngine.compute(textureRun, var6);
                  } else {
                     RotationEngine.handle(textureRun, var6);
                  }
                  break;
               case "Snap":
                  RotationEngine.handle(textureRun, AdaptiveAttackTiming.handle(textureRun, false, true, true, handle(-50L), var5), itemProject.compute());
                  break;
               case "FOV":
                  boolean var9 = CameraAimProjection.handle(textureRun, responseCompute.compute());
                  boolean var10 = var9 && AdaptiveAttackTiming.handle(textureRun, false, true, true, handle(-50L), var5);
                  RotationEngine.process(textureRun, var10, itemProject.compute());
                  break;
               case "Smooth ":
                  SnapAimPattern.handle(textureRun);
                  break;
               case "FunTime":
                  AdaptiveJitterAimPattern.handle(textureRun);
                  break;
               case "FT-New":
                  AuraRotationPlanner.handle(textureRun);
                  break;
               case "FTTESTT":
                  ServerTags.handle(textureRun);
                  break;
               case "SpookyTime":
                  OrganicAimPattern.handle(textureRun);
                  break;
               case "ST-Test":
                  AttackAimSequencer.handle(textureRun, var6);
                  break;
               case "Custom":
                  AuraTargetingStrategy.handle(textureRun);
                  break;
               case "Lony Grief":
                  CombatRotationPlanner.handle(textureRun);
                  break;
               case "Side Point":
                  BoundingBoxAimPattern.handle(textureRun);
                  break;
               case "AI":
                  RotationRecorder.handle(textureRun);
                  break;
               case "Neuro":
                  NeuroEngine.handle(textureRun, var6, this.advancePosition(), serverRead.compute());
            }
         }
      }
   }

   private void measure() {
      if (Module.client.player != null && textureRun != null && Module.client.options != null && Module.client.getWindow() != null) {
         if (!Module.client.player.isUsingItem() && !Module.client.player.isSneaking() && !Module.client.player.hasVehicle() && Module.client.currentScreen == null) {
            float[] var1 = MovementPhysics.process();
            float var2 = var1[0];
            float var3 = var1[1];
            Vec3d var4 = textureRun.getPos();
            if (var2 != 0.0F || var3 != 0.0F) {
               Vec3d var5 = this.handle(textureRun.getYaw());
               Vec3d var6 = this.handle(textureRun.getYaw() + 90.0F);
               var4 = var4.add(var5.multiply(var2)).add(var6.multiply(-var3));
            }

            Vec3d var16 = var4.subtract(Module.client.player.getPos());
            if (var16.x * var16.x + var16.z * var16.z < 1.0E-4) {
               this.handle(1.0F, 0.0F, true, false);
            } else {
               float var17 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var16.z, var16.x)) - 90.0);
               float var7 = MovementPacketTracker.handle(Module.client.player.getYaw());
               float var8 = 0.0F;
               float var9 = 0.0F;
               float var10 = Float.MAX_VALUE;

               for (float var11 = -1.0F; var11 <= 1.0F; var11++) {
                  for (float var12 = -1.0F; var12 <= 1.0F; var12++) {
                     if (var11 != 0.0F || var12 != 0.0F) {
                        double var13 = MathHelper.wrapDegrees(Math.toDegrees(MovementPhysics.handle(var7, var11, var12)));
                        float var15 = this.handle(var17, (float)var13);
                        if (var15 < var10) {
                           var10 = var15;
                           var8 = var11;
                           var9 = var12;
                        }
                     }
                  }
               }

               boolean var18 = Module.client.player.horizontalCollision && Module.client.player.isOnGround();
               this.handle(var8, var9, true, var18);
            }
         } else {
            this.blendMatrix();
         }
      } else {
         this.blendMatrix();
      }
   }

   private Vec3d handle(float var1) {
      double var2 = Math.toRadians(var1);
      return new Vec3d(-Math.sin(var2), 0.0, Math.cos(var2));
   }

   private float handle(float var1, float var2) {
      return Math.abs(MathHelper.wrapDegrees(var1 - var2));
   }

   private void handle(float var1, float var2, boolean var3, boolean var4) {
      if (Module.client.options != null) {
         this.layerSample = true;
         Module.client.options.forwardKey.setPressed(var1 > 0.0F);
         Module.client.options.backKey.setPressed(var1 < 0.0F);
         Module.client.options.leftKey.setPressed(var2 > 0.0F);
         Module.client.options.rightKey.setPressed(var2 < 0.0F);
         boolean var5 = var3;
         if (presetWrite.process("Легит") && textureRun != null && AdaptiveAttackTiming.process(textureRun, process(textureRun))) {
            Module.client.player.setSprinting(false);
            var5 = false;
         }

         Module.client.options.sprintKey.setPressed(var5);
         if (var4) {
            Module.client.options.jumpKey.setPressed(true);
         } else if (!this.process(Module.client.options.jumpKey)) {
            Module.client.options.jumpKey.setPressed(false);
         }
      }
   }

   private void blendMatrix() {
      if (this.layerSample && Module.client.options != null) {
         this.layerSample = false;
         this.handle(Module.client.options.forwardKey);
         this.handle(Module.client.options.backKey);
         this.handle(Module.client.options.leftKey);
         this.handle(Module.client.options.rightKey);
         this.handle(Module.client.options.jumpKey);
         this.handle(Module.client.options.sprintKey);
      }
   }

   private void matchVector() {
      if (presetWrite.process("Легит")
         && Module.client.player != null
         && Module.client.world != null
         && Module.client.options != null
         && Module.client.currentScreen == null) {
         this.worldSend = true;
         boolean var1 = this.computeResponse();
         if (var1) {
            Module.client.player.setSprinting(false);
            Module.client.options.sprintKey.setPressed(false);
         } else {
            Module.client.options.sprintKey.setPressed(this.projectItem());
         }
      } else {
         this.fetchProvider();
      }
   }

   private boolean projectItem() {
      return Module.client.options == null ? false : this.process(Module.client.options.forwardKey) && !this.process(Module.client.options.backKey);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (pending.process("FT-New") && var1.compute()) {
         if (var1.resolve() instanceof HandSwingC2SPacket || var1.resolve() instanceof UpdateSelectedSlotC2SPacket) {
            SmartCritsHelper.update();
         }
      }
   }

   private boolean computeResponse() {
      if (textureRun == null || Module.client.player == null) {
         return false;
      } else {
         return pending.process("FT-New") ? SmartCritsHelper.handle(textureRun) : AdaptiveAttackTiming.process(textureRun, process(textureRun));
      }
   }

   private void fetchProvider() {
      if (this.worldSend && Module.client.options != null) {
         this.worldSend = false;
         this.handle(Module.client.options.sprintKey);
      }
   }

   private void drawProfile() {
      if (Module.client.options != null) {
         Module.client.options.forwardKey.setPressed(false);
         Module.client.options.backKey.setPressed(false);
         Module.client.options.leftKey.setPressed(false);
         Module.client.options.rightKey.setPressed(false);
         Module.client.options.jumpKey.setPressed(false);
         this.handle(Module.client.options.sprintKey);
      }
   }

   private void handle(KeyBinding var1) {
      if (var1 != null) {
         var1.setPressed(this.process(var1));
      }
   }

   private boolean process(KeyBinding var1) {
      return var1 == null ? false : var1.isPressed();
   }

   private void performVector() {
      LivingEntity var1 = textureRun;
      LivingEntity var2 = null;
      double var3 = Double.MAX_VALUE;
      Vec3d var5 = Module.client.player.getEyePos();
      Vec3d var6 = Module.client.player.getRotationVec(1.0F).normalize();

      for (Entity var8 : Module.client.world.getEntities()) {
         if (var8 instanceof LivingEntity var9 && this.compute(var9)) {
            double var10;
            if (animationSchedule.process("Приоритет ближайшей цели")) {
               var10 = Module.client.player.squaredDistanceTo(var9);
            } else {
               Vec3d var12 = var9.getPos().add(0.0, var9.getHeight() * 0.5, 0.0);
               Vec3d var13 = var12.subtract(var5).normalize();
               var10 = Math.acos(MathHelper.clamp(var6.dotProduct(var13), -1.0, 1.0));
            }

            if (var10 < var3) {
               var3 = var10;
               var2 = var9;
            }
         }
      }

      textureRun = var2;
      if (pending.process("FunTime") && var1 != null && var2 == null) {
         AdaptiveJitterAimPattern.process();
      }
   }

   private float attachEvent() {
      return handle(textureRun) + target.compute();
   }

   private boolean compute(LivingEntity var1) {
      return this.handle(var1, handle(var1) + target.compute());
   }

   private boolean handle(LivingEntity var1, float var2) {
      if (var1 instanceof ClientPlayerEntity || var1 == Module.client.player) {
         return false;
      }

      if (var1.isAlive() && !var1.isInvulnerable() && !(var1 instanceof ArmorStandEntity)) {
         if (Module.client.player.distanceTo(var1) > var2) {
            return false;
         }

         if (!colorMeasure.process("Бить через блоки") && !Module.client.player.canSee(var1)) {
            return false;
         }

         if (!providerClose.process("NPC") && this.resolve(var1)) {
            return false;
         }

         if (var1 instanceof PlayerEntity var7) {
            if (!var7.isCreative() && !var7.isSpectator()) {
               boolean var8 = FriendManager.handle(var7.getName().getString());
               if (var8 && !providerClose.process("Друзья")) {
                  return false;
               } else if (!var8 && !providerClose.process("Игроки")) {
                  return false;
               } else {
                  boolean var9 = !this.handle(var7);
                  boolean var6 = var7.isInvisible();
                  if (AntiBot.handle(var7)) {
                     return false;
                  } else if (var6) {
                     return var9 ? providerClose.process("Голые невидимки") : providerClose.process("Невидимки");
                  } else {
                     return !var9 || providerClose.process("Голые");
                  }
               }
            } else {
               return false;
            }
         } else {
            boolean var3 = var1 instanceof Monster || var1 instanceof SlimeEntity;
            boolean var4 = var1 instanceof VillagerEntity || var1 instanceof MerchantEntity;
            boolean var5 = var1 instanceof AnimalEntity
               || var1 instanceof VillagerEntity
               || var1 instanceof WaterCreatureEntity
               || var1 instanceof AmbientEntity;
            if (var3 && providerClose.process("Мобы")) {
               return true;
            } else {
               return var4 && providerClose.process("Жители") ? true : var5 && providerClose.process("Животные");
            }
         }
      } else {
         return false;
      }
   }

   private boolean handle(PlayerEntity var1) {
      return !var1.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
         || !var1.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
         || !var1.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
         || !var1.getEquippedStack(EquipmentSlot.FEET).isEmpty();
   }

   private boolean resolve(LivingEntity var1) {
      String var2 = this.process(var1.getName().getString());
      String var3 = this.process(var1.getDisplayName().getString());
      String var4 = var1.getCustomName() == null ? "" : this.process(var1.getCustomName().getString());
      String var5 = "";
      String var6 = "";
      if (var1.getScoreboardTeam() != null) {
         var5 = this.process(var1.getScoreboardTeam().getPrefix().getString());
         var6 = this.process(var1.getScoreboardTeam().getSuffix().getString());
      }

      if (this.handle(var2) || this.handle(var3) || this.handle(var4) || this.handle(var5) || this.handle(var6)) {
         return true;
      } else if (!(var1 instanceof PlayerEntity var7)) {
         return false;
      } else {
         boolean var8 = Module.client.getNetworkHandler() != null && Module.client.getNetworkHandler().getPlayerListEntry(var7.getUuid()) == null;
         boolean var9 = var2.matches("\\d{1,8}") || var2.startsWith("cit-");
         return var8 || var9 && (!var3.equals(var2) || !var5.isEmpty() || !var6.isEmpty());
      }
   }

   private boolean handle(String var1) {
      return var1.contains("npc") || var1.contains("znpc") || var1.contains("нпс") || var1.contains("наставник");
   }

   private String process(String var1) {
      return var1 == null ? "" : var1.replaceAll("(?i)§.", "").replaceAll("(?i)&.", "").replaceAll("\\p{Cntrl}", "").trim().toLowerCase(Locale.ROOT);
   }

   @Override
   public void handle() {
      FreeLock var1 = WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(FreeLock.class) : null;
      if (var1 != null && var1.enabled && var1.refresh()) {
         this.enabled = false;
         ChatLogger.handle("Отключите FreeLock перед включением AttackAura");
      } else {
         SmartCritsHelper.handle();
         super.handle();
      }
   }

   @Override
   public void toggle() {
      super.toggle();
      this.readServer();
   }

   private void readServer() {
      this.adaptScale();
      this.adaptRequest();
      this.measureTimer();
      AdaptiveAttackTiming.select();
      this.blendMatrix();
      this.fetchProvider();
      RotationRecorder.measure();
      AdaptiveJitterAimPattern.process();
      PredictiveAimPattern.compute();
      ServerTags.update();
      OrganicAimPattern.process();
      AttackAimSequencer.compute();
      textureRun = null;
      SyntheticKeyState.handle().process("Aura");
      if (Module.client.player != null) {
         dataValidate = false;
         scaleRender = 0L;
      }

      requestReceive = false;
      windowProcess = 0.0F;
      vectorEncode = 0L;
      entryAnimate = 0.0F;
      packetSave = 0L;
      playerCollect = 0L;
      stateApply = 0L;
      matrixFilter = Integer.MIN_VALUE;
   }

   private boolean advancePosition() {
      return drawAnimation()
         ? true
         : Module.client.player.isUsingItem()
               && colorMeasure.process("Не бить если кушаешь")
               && !(Module.client.player.getActiveItem().getItem() instanceof ShieldItem)
            || Module.client.currentScreen != null && colorMeasure.process("Не бить в контейнерах ")
            || !Module.client.player.getMainHandStack().isIn(ItemTags.SWORDS)
               && !Module.client.player.getMainHandStack().isIn(ItemTags.AXES)
               && Module.client.player.getMainHandStack().getItem() != Items.MACE
               && colorMeasure.process("Бить только оружием");
   }

   private int handle(Item var1) {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var2 = 0; var2 < 9; var2++) {
         if (Module.client.player.getInventory().getStack(var2).getItem() == var1) {
            return var2;
         }
      }

      return -1;
   }

   private int checkFrame() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         if (Module.client.player.getInventory().getStack(var1).isIn(ItemTags.SWORDS)) {
            return var1;
         }
      }

      return -1;
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (this.enabled && profileInvoke.compute() && sourceSchedule.process("Бинд")) {
         if (timerRender.compute() != -1 && var1.resolve() == timerRender.compute() && var1.apply() == 1) {
            if (Module.client.currentScreen == null && !drawAnimation()) {
               this.convertWindow();
            }
         }
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (!var1.check()) {
         if (this.enabled && profileInvoke.compute() && sourceSchedule.process("Бинд")) {
            int var2 = -100 - var1.resolve();
            if (timerRender.compute() != -1 && timerRender.compute() == var2 && var1.onTick()) {
               if (Module.client.currentScreen == null && !drawAnimation()) {
                  this.convertWindow();
               }
            }
         }
      }
   }

   private void collectModule() {
      if (Module.client.player == null || Module.client.world == null || Module.client.interactionManager == null) {
         this.runTexture();
      } else if (matrixBlend2 != 0) {
         this.scheduleAnimation();
      } else if (profileInvoke.compute() && !drawAnimation()) {
         if (Module.client.currentScreen == null) {
            if (sourceSchedule.process("Авто")) {
               this.closeProvider();
            }
         }
      } else {
         if (contextExpand) {
            this.measureColor();
         }
      }
   }

   private void closeProvider() {
      LivingEntity var1 = this.savePreset();
      boolean var2 = var1 != null && Module.client.player.getY() - var1.getY() >= scaleSave.compute();
      if (!contextExpand) {
         if (!var2) {
            matrixRender = false;
         } else {
            if (!matrixRender && Module.client.player.getMainHandStack().getItem() != Items.MACE && this.bindIndex() != -1) {
               moduleTick = 0;
               this.writePreset();
            }
         }
      } else {
         moduleTick++;
         if (playerRun || moduleTick > 40 && (!colorCompute.compute() || !this.unload())) {
            matrixRender = true;
            this.measureColor();
         }
      }
   }

   private LivingEntity savePreset() {
      if (Module.client.player != null && Module.client.world != null) {
         float var1 = handle((LivingEntity)null) + target.compute() + scaleSave.compute() + 2.0F;
         double var2 = handle((LivingEntity)null) + target.compute() + 1.5;
         double var4 = var2 * var2;
         LivingEntity var6 = null;
         double var7 = Double.MAX_VALUE;

         for (Entity var10 : Module.client.world.getEntities()) {
            if (var10 instanceof LivingEntity var11 && !(Module.client.player.getY() - var11.getY() < scaleSave.compute())) {
               double var12 = var11.getX() - Module.client.player.getX();
               double var14 = var11.getZ() - Module.client.player.getZ();
               double var16 = var12 * var12 + var14 * var14;
               if (!(var16 > var4) && this.handle(var11, var1) && var16 < var7) {
                  var7 = var16;
                  var6 = var11;
               }
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   private void convertWindow() {
      if (matrixBlend2 == 0 && Module.client.player != null) {
         if (contextExpand) {
            this.measureColor();
         } else {
            this.writePreset();
         }
      }
   }

   private boolean writePreset() {
      if (!contextExpand && matrixBlend2 == 0 && Module.client.player != null) {
         if (sourceSchedule.process("Авто") && System.currentTimeMillis() < optionAdvance) {
            return false;
         }

         if (Module.client.player.getMainHandStack().getItem() == Items.MACE) {
            return false;
         }

         int var1 = this.bindIndex();
         if (var1 == -1) {
            return false;
         }

         int var2 = Module.client.player.getInventory().getSelectedSlot();
         if (var1 == var2) {
            return false;
         }

         actionConvert = var2;
         if (var1 < 9) {
            screenRead = true;
            animationExpand = var1;
            keyProcess = -1;
         } else {
            screenRead = false;
            animationExpand = -1;
            keyProcess = var1;
         }

         this.compute(screenRead ? "свап IN хотбар слот=" + var1 : "свап IN инвентарь слот=" + var1);
         matrixBlend2 = 1;
         scalePerform = 0;
         this.renderTimer();
         return true;
      } else {
         return false;
      }
   }

   private boolean measureColor() {
      if (contextExpand && matrixBlend2 == 0) {
         this.compute("свап OUT старт");
         matrixBlend2 = 11;
         scalePerform = 0;
         this.renderTimer();
         return true;
      } else {
         return false;
      }
   }

   private void scheduleAnimation() {
      SyntheticKeyState.handle().handle("AuraMace");
      this.saveScale();
      if (scalePerform > 0) {
         scalePerform--;
      } else {
         switch (matrixBlend2) {
            case 1:
               matrixBlend2 = 2;
               scalePerform = 0;
               break;
            case 2:
               this.collapseOutput();
               contextExpand = true;
               matrixBlend2 = 3;
               scalePerform = 1;
               break;
            case 3:
               this.scheduleSource();
               break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            default:
               this.scheduleSource();
               break;
            case 11:
               matrixBlend2 = 12;
               scalePerform = 0;
               break;
            case 12:
               this.invokeProfile();
               playerCollapse = 4;
               matrixBlend2 = 13;
               scalePerform = 1;
               break;
            case 13:
               if (this.scanRenderer()) {
                  this.buildSource();
               } else if (playerCollapse > 0) {
                  playerCollapse--;
                  this.invokeProfile();
                  scalePerform = 1;
               } else {
                  this.buildSource();
               }
         }
      }
   }

   private boolean scanRenderer() {
      return Module.client.player == null ? true : Module.client.player.getMainHandStack().getItem() != Items.MACE;
   }

   private void buildSource() {
      this.compute("восстановлено");
      contextExpand = false;
      keyProcess = -1;
      actionConvert = -1;
      screenRead = false;
      animationExpand = -1;
      playerCollapse = 0;
      playerRun = false;
      optionAdvance = System.currentTimeMillis() + 300L;
      this.scheduleSource();
   }

   private void collapseOutput() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (screenRead) {
            if (animationExpand >= 0 && animationExpand <= 8) {
               Module.client.player.getInventory().setSelectedSlot(animationExpand);
            }
         } else if (keyProcess >= 0 && actionConvert >= 0 && actionConvert <= 8) {
            this.compute("clickSlot IN");
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, keyProcess, actionConvert, SlotActionType.SWAP, Module.client.player);
         }
      }
   }

   private void invokeProfile() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (screenRead) {
            if (actionConvert >= 0 && actionConvert <= 8) {
               Module.client.player.getInventory().setSelectedSlot(actionConvert);
            }
         } else if (keyProcess >= 0 && actionConvert >= 0 && actionConvert <= 8) {
            this.compute("clickSlot OUT");
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, keyProcess, actionConvert, SlotActionType.SWAP, Module.client.player);
         }
      }
   }

   private void scheduleSource() {
      matrixBlend2 = 0;
      scalePerform = 0;
      SyntheticKeyState.handle().process("AuraMace");
   }

   private void renderTimer() {
      AdaptiveAttackTiming.select();
      SyntheticKeyState.handle().handle("AuraMace");
      this.saveScale();
   }

   private void saveScale() {
      Sprint.latest = 2;
      if (Module.client.options != null) {
         Module.client.options.sprintKey.setPressed(false);
      }

      if (Module.client.player != null) {
         Module.client.player.setSprinting(false);
      }
   }

   private void compute(String var1) {
      if (scaleAdapt.compute()) {
         int var2 = 0;
         int var3 = 0;
         if (Module.client.player != null && Module.client.player.input != null) {
            var2 = (Module.client.player.input.playerInput.forward() ? 1 : 0) - (Module.client.player.input.playerInput.backward() ? 1 : 0);
            var3 = (Module.client.player.input.playerInput.left() ? 1 : 0) - (Module.client.player.input.playerInput.right() ? 1 : 0);
         }

         ChatLogger.handle("[Булава] " + var1 + " (fwd=" + var2 + " str=" + var3 + ")");
      }
   }

   private void computeColor() {
      if (profileInvoke.compute() && sourceSchedule.process("Авто") && contextExpand && Module.client.player != null) {
         if (Module.client.player.getMainHandStack().getItem() == Items.MACE) {
            playerRun = true;
            matrixRender = true;
         }
      }
   }

   private void adaptScale() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (contextExpand) {
            this.invokeProfile();
         }

         this.runTexture();
      } else {
         this.runTexture();
      }
   }

   private void runTexture() {
      if (matrixBlend2 != 0) {
         SyntheticKeyState.handle().process("AuraMace");
      }

      matrixBlend2 = 0;
      scalePerform = 0;
      contextExpand = false;
      keyProcess = -1;
      actionConvert = -1;
      screenRead = false;
      animationExpand = -1;
      playerRun = false;
      matrixRender = false;
      moduleTick = 0;
      playerCollapse = 0;
      optionAdvance = 0L;
   }

   private int bindIndex() {
      if (Module.client.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 36; var1++) {
         if (Module.client.player.getInventory().getStack(var1).getItem() == Items.MACE) {
            return var1;
         }
      }

      return -1;
   }

   public static boolean drawAnimation() {
      return ServerHelper.textureRun || ClickPearl.pending || AutoSwap.matrixBlend || AutoTotem.source;
   }

   private void readAction() {
      if (!collapseConfig()) {
         entryAnimate = 0.0F;
         packetSave = 0L;
      } else {
         long var1 = System.currentTimeMillis();
         if (var1 >= packetSave || entryAnimate <= 0.0F) {
            entryAnimate = ThreadLocalRandom.current().nextFloat(0.08F, 0.32F);
            packetSave = var1 + ThreadLocalRandom.current().nextLong(55L, 130L);
         }

         Module.client.player.fallDistance = entryAnimate;
      }
   }

   private static boolean collapseConfig() {
      return Module.client.player != null && Module.client.world != null && Module.client.player.isOnGround() && encodePoint();
   }

   public static boolean encodePoint() {
      if (Module.client.player != null && Module.client.world != null) {
         BlockPos var0 = BlockPos.ofFloored(Module.client.player.getX(), Module.client.player.getBoundingBox().minY - 0.05, Module.client.player.getZ());
         BlockPos var1 = BlockPos.ofFloored(Module.client.player.getX(), Module.client.player.getBoundingBox().maxY + 0.2, Module.client.player.getZ());
         return handle(var0) && handle(var1);
      } else {
         return false;
      }
   }

   private static boolean handle(BlockPos var0) {
      BlockState var1 = Module.client.world.getBlockState(var0);
      return !var1.getCollisionShape(Module.client.world, var0).isEmpty();
   }

   private static long validateData() {
      return handle(0L);
   }

   private static long handle(long var0) {
      if (animationSchedule.process("Умные криты") && Module.client.player != null && textureRun != null) {
         long var2 = System.currentTimeMillis();
         int var4 = textureRun.getId();
         if (var4 != matrixFilter || var2 >= playerCollect || AdaptiveAttackTiming.drawAnimation() < 75.0F) {
            matrixFilter = var4;
            stateApply = renderScale();
            playerCollect = var2 + ThreadLocalRandom.current().nextLong(95L, 180L);
         }

         return var0 + stateApply;
      } else {
         return var0;
      }
   }

   private static long renderScale() {
      long var0 = -35L;
      long var2 = 28L;
      boolean var4 = Module.client.player.fallDistance > 0.0 || Module.client.player.getVelocity().y < -0.0784;
      if (var4) {
         var0 -= 18L;
         var2 -= 8L;
      }

      if (collapseConfig()) {
         var0 -= 22L;
         var2 -= 6L;
      } else if (Module.client.player.isOnGround()) {
         var0 += 8L;
         var2 += 18L;
      }

      if (textureRun.hurtTime > 0) {
         var0 = Math.max(var0, 4L);
         var2 += 34L;
      }

      double var5 = handle(textureRun) - EntityAimGeometry.handle((Entity)textureRun);
      if (var5 < 0.35F) {
         var0 += 10L;
         var2 += 22L;
      } else if (var5 > 1.0) {
         var0 -= 8L;
      }

      if (var2 < var0) {
         var2 = var0;
      }

      return ThreadLocalRandom.current().nextLong(var0, var2 + 1L);
   }

   @Override
   public void process() {
      MovementPhysics.compute();
      this.adaptScale();
      this.adaptRequest();
      this.measureTimer();
      AdaptiveAttackTiming.select();
      this.fetchProvider();
      AdaptiveJitterAimPattern.process();
      ServerTags.resolve();
      AttackAimSequencer.process();
      SmartCritsHelper.process();
      PredictiveAimPattern.compute();
      super.process();
   }

   private static void refreshClient() {
      if (Module.client != null) {
         Module.client.execute(() -> Module.client.setScreen(new RotationBuilderScreen()));
      }
   }

   private static void filterKey() {
      if (Module.client != null) {
         Module.client.execute(() -> Module.client.setScreen(new RotationAnalyticsScreen()));
      }
   }

   private void adaptRequest() {
      if (pending.process("Lony Grief")) {
         CombatRotationPlanner.handle();
      }

      if (pending.process("Side Point")) {
         BoundingBoxAimPattern.handle();
      }
   }

   private void measureTimer() {
      if (pending.process("Neuro")) {
         NeuroEngine.handle(moduleCollect.compute());
      }
   }

   @ModuleRoles(compute = {"lichoday", "bitrixtime", "oblamovvv"})
   static final class State {
      private State() {
      }
   }
}
