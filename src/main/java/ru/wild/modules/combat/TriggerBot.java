package ru.wild.modules.combat;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.combat.AdaptiveAttackTiming;
import ru.wild.core.manager.FriendManager;
import ru.wild.util.player.CombatRaycast;

@ModuleRegister(
   name = "TriggerBot",
   description = "Бьет энтити при наведении на него",
   category = ModuleCategory.Combat,
   flags = {ModuleFlag.RISKY, ModuleFlag.GRIM}
)
public class TriggerBot extends Module {
   public static NumberSetting source = new NumberSetting("Дистанция", 4.5F, 3.0F, 8.0F, 0.1F, false);
   public static ChoiceSetting target = new ChoiceSetting(
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
   public static ChoiceSetting pending = new ChoiceSetting(
      "Проверки до удара",
      new BooleanSetting("Бить через блоки", false),
      new BooleanSetting("Бить только оружием", false),
      new BooleanSetting("Не бить если кушаешь", true),
      new BooleanSetting("Не бить в контейнерах ", false),
      new BooleanSetting("Ломать щит", false),
      new BooleanSetting("Отжим щита", false)
   );
   public static ChoiceSetting previous = new ChoiceSetting(
      "Дополнительные настройки",
      new BooleanSetting("Расширенная настройки для атаки", false),
      new BooleanSetting("Умные криты", false),
      new BooleanSetting("Увеличенная дистанция удара", false)
   );
   public static NumberSetting latest = new NumberSetting("Радиус атаки для мобов", 4.5F, 3.0F, 8.0F, 0.1F, false)
      .handle(() -> !previous.process("Расширенная настройки для атаки"));
   public static NumberSetting summary = new NumberSetting("Радиус атаки для игроков", 4.5F, 3.0F, 8.0F, 0.1F, false)
      .handle(() -> !previous.process("Расширенная настройки для атаки"));
   public static LivingEntity matrixBlend;
   private static long vectorMatch = 0L;
   private static boolean itemProject = false;
   private static float responseCompute = 0.0F;

   public TriggerBot() {
      this.handle(source, target, pending, previous, latest, summary);
   }

   public static LivingEntity refresh() {
      return matrixBlend;
   }

   @Override
   public void process() {
      matrixBlend = null;
      itemProject = false;
      responseCompute = 0.0F;
      vectorMatch = 0L;
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         this.render();
         if (!AttackAura.drawAnimation()) {
            LivingEntity var2 = this.tick();
            if (var2 != null) {
               if (!this.drawAnimation()) {
                  float var3 = handle(var2);
                  float[] var4 = new float[]{var3, 0.0F, var3};
                  AdaptiveAttackTiming.handle(var2, true, true, false);
                  boolean var5 = !previous.process("Умные криты");
                  if (AdaptiveAttackTiming.handle(var2, false, true, var5, 0L, var4)) {
                     Runnable[] var6 = AdaptiveAttackTiming.handle(var2, pending.process("Ломать щит"));
                     Runnable[] var7 = AdaptiveAttackTiming.handle(true);
                     Runnable[] var8 = AdaptiveAttackTiming.process(false);
                     Runnable var9 = () -> {
                        var8[0].run();
                        var7[0].run();
                        var6[0].run();
                     };
                     Runnable var10 = () -> {
                        var6[1].run();
                        var7[1].run();
                        var8[1].run();
                     };
                     if (pending.process("Отжим щита") && Module.client.player.getActiveItem().getItem().equals(Items.SHIELD) && Module.client.player.isUsingItem()) {
                        Module.client.interactionManager.stopUsingItem(Module.client.player);
                     }

                     if (AdaptiveAttackTiming.handle(var2, var9, var10, Hand.MAIN_HAND, true)) {
                        matrixBlend = var2;
                     }
                  }
               }
            }
         }
      } else {
         matrixBlend = null;
      }
   }

   private void render() {
      if (matrixBlend != null) {
         if (!matrixBlend.isAlive()
            || matrixBlend.isRemoved()
            || Module.client.player == null
            || Module.client.player.distanceTo(matrixBlend) > handle(matrixBlend) + 2.0F) {
            matrixBlend = null;
         }
      }
   }

   private LivingEntity tick() {
      LivingEntity var1 = null;
      double var2 = Double.MAX_VALUE;
      float var4 = Module.client.player.getYaw();
      float var5 = Module.client.player.getPitch();
      boolean var6 = pending.process("Бить через блоки");

      for (Entity var8 : Module.client.world.getEntities()) {
         if (var8 instanceof LivingEntity var9 && this.process(var9) && CombatRaycast.compute(var4, var5, handle(var9), var9, var6)) {
            double var10 = Module.client.player.squaredDistanceTo(var9);
            if (var10 < var2) {
               var2 = var10;
               var1 = var9;
            }
         }
      }

      return var1;
   }

   public static float handle(LivingEntity var0) {
      if (var0 == null) {
         return source.compute();
      }

      float var1 = source.compute();
      if (previous.process("Расширенная настройки для атаки")) {
         var1 = var0 instanceof PlayerEntity ? summary.compute() : latest.compute();
      }

      if (previous.process("Увеличенная дистанция удара")) {
         float var2 = var0.getHealth() + var0.getAbsorptionAmount();
         if (var2 >= 10.0F && var2 <= 12.0F) {
            long var3 = System.currentTimeMillis();
            if (var3 >= vectorMatch) {
               if (ThreadLocalRandom.current().nextInt(100) < 25) {
                  itemProject = true;
                  responseCompute = 0.1F + ThreadLocalRandom.current().nextFloat() * 0.05F;
                  vectorMatch = var3 + ThreadLocalRandom.current().nextLong(400L, 700L);
               } else {
                  itemProject = false;
                  responseCompute = 0.0F;
                  vectorMatch = var3 + ThreadLocalRandom.current().nextLong(1500L, 2500L);
               }
            }

            if (itemProject) {
               return var1 + responseCompute;
            }
         } else {
            itemProject = false;
            responseCompute = 0.0F;
         }
      }

      return var1;
   }

   private boolean drawAnimation() {
      return Module.client.player.isUsingItem() && pending.process("Не бить если кушаешь") && !(Module.client.player.getActiveItem().getItem() instanceof ShieldItem)
         || Module.client.currentScreen != null && pending.process("Не бить в контейнерах ")
         || !Module.client.player.getMainHandStack().isIn(ItemTags.SWORDS)
            && !Module.client.player.getMainHandStack().isIn(ItemTags.AXES)
            && pending.process("Бить только оружием");
   }

   private boolean process(LivingEntity var1) {
      if (var1 instanceof ClientPlayerEntity || var1 == Module.client.player) {
         return false;
      }

      if (var1.isAlive() && !var1.isInvulnerable() && !(var1 instanceof ArmorStandEntity)) {
         if (Module.client.player.distanceTo(var1) > handle(var1)) {
            return false;
         }

         if (!pending.process("Бить через блоки") && !Module.client.player.canSee(var1)) {
            return false;
         }

         if (!target.process("NPC") && this.compute(var1)) {
            return false;
         }

         if (var1 instanceof PlayerEntity var6) {
            if (!var6.isCreative() && !var6.isSpectator()) {
               boolean var7 = FriendManager.handle(var6.getName().getString());
               if (var7 && !target.process("Друзья")) {
                  return false;
               } else if (!var7 && !target.process("Игроки")) {
                  return false;
               } else if (AntiBot.handle(var6)) {
                  return false;
               } else {
                  boolean var8 = !this.handle(var6);
                  boolean var5 = var6.isInvisible();
                  if (var5) {
                     return var8 ? target.process("Голые невидимки") : target.process("Невидимки");
                  } else {
                     return !var8 || target.process("Голые");
                  }
               }
            } else {
               return false;
            }
         } else {
            boolean var2 = var1 instanceof Monster || var1 instanceof SlimeEntity;
            boolean var3 = var1 instanceof VillagerEntity || var1 instanceof MerchantEntity;
            boolean var4 = var1 instanceof AnimalEntity
               || var1 instanceof VillagerEntity
               || var1 instanceof WaterCreatureEntity
               || var1 instanceof AmbientEntity;
            if (var2 && target.process("Мобы")) {
               return true;
            } else {
               return var3 && target.process("Жители") ? true : var4 && target.process("Животные");
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

   private boolean compute(LivingEntity var1) {
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
}
