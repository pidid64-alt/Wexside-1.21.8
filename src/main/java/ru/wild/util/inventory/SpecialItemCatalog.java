package ru.wild.util.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class SpecialItemCatalog {
   private static final List<StatusEffectInstance> instance = List.of(
      new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 9),
      new StatusEffectInstance(StatusEffects.SPEED, 400, 4),
      new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 9),
      new StatusEffectInstance(StatusEffects.GLOWING, 3600, 0)
   );
   private static final List<StatusEffectInstance> data = List.of(
      new StatusEffectInstance(StatusEffects.STRENGTH, 600, 4), new StatusEffectInstance(StatusEffects.SLOWNESS, 600, 3)
   );
   private static final List<StatusEffectInstance> context = List.of(
      new StatusEffectInstance(StatusEffects.RESISTANCE, 12000, 0),
      new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 12000, 0),
      new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 1200, 2),
      new StatusEffectInstance(StatusEffects.INVISIBILITY, 18000, 0)
   );
   private static final List<StatusEffectInstance> config = List.of(
      new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1),
      new StatusEffectInstance(StatusEffects.INVISIBILITY, 12000, 1),
      new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 0, 1)
   );
   private static final List<StatusEffectInstance> state = List.of(
      new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 3),
      new StatusEffectInstance(StatusEffects.SPEED, 6000, 2),
      new StatusEffectInstance(StatusEffects.HASTE, 1200, 0),
      new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 0, 1)
   );
   private static final List<StatusEffectInstance> cache = List.of(
      new StatusEffectInstance(StatusEffects.POISON, 1200, 1),
      new StatusEffectInstance(StatusEffects.WITHER, 1200, 1),
      new StatusEffectInstance(StatusEffects.SLOWNESS, 1800, 2),
      new StatusEffectInstance(StatusEffects.HUNGER, 1200, 4),
      new StatusEffectInstance(StatusEffects.GLOWING, 2400, 0)
   );
   private static final List<StatusEffectInstance> output = List.of(
      new StatusEffectInstance(StatusEffects.WEAKNESS, 1800, 1),
      new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 1),
      new StatusEffectInstance(StatusEffects.WITHER, 1800, 2),
      new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0)
   );
   private static final List<StatusEffectInstance> current = List.of(
      new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600, 0),
      new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600, 1),
      new StatusEffectInstance(StatusEffects.LUCK, 3600, 0),
      new StatusEffectInstance(StatusEffects.HASTE, 3600, 1)
   );
   private static final List<SpecialItemCatalog.DataRecord> active = List.of(
      new SpecialItemCatalog.DataRecord("Хлопушка", instance),
      new SpecialItemCatalog.DataRecord("Зелье Гнева", data),
      new SpecialItemCatalog.DataRecord("Зелье Палладина", context),
      new SpecialItemCatalog.DataRecord("Святая Вода", config),
      new SpecialItemCatalog.DataRecord("Зелье Ассасина", state),
      new SpecialItemCatalog.DataRecord("Зелье Радиации", cache),
      new SpecialItemCatalog.DataRecord("Снотворное", output)
   );

   public static List<SpecialItemCatalog.DataRecord> handle() {
      return active;
   }

   private static Map<RegistryEntry<EntityAttribute>, Double> collapseConfig(ItemStack var0) {
      AttributeModifiersComponent var1 = (AttributeModifiersComponent)var0.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      HashMap var2 = new HashMap();
      if (var1 == null) {
         return var2;
      }

      for (Entry var4 : var1.modifiers()) {
         EntityAttributeModifier var5 = var4.modifier();
         var2.put(var4.attribute(), var5.value());
      }

      return var2;
   }

   private static boolean handle(Map<RegistryEntry<EntityAttribute>, Double> var0, RegistryEntry<EntityAttribute> var1, double var2) {
      return Math.abs(var0.getOrDefault(var1, 0.0) - var2) < 1.0E-4;
   }

   private static boolean handle(ItemStack var0, String var1) {
      if (!var0.isOf(Items.PLAYER_HEAD)) {
         return false;
      }

      NbtComponent var2 = (NbtComponent)var0.get(DataComponentTypes.CUSTOM_DATA);
      if (var2 == null) {
         return false;
      }

      NbtCompound var3 = var2.copyNbt();
      return var3.getCompound("SkullOwner")
         .flatMap(var0x -> var0x.getCompound("Properties"))
         .flatMap(var0x -> var0x.getList("textures"))
         .filter(var0x -> !var0x.isEmpty())
         .flatMap(var0x -> var0x.getCompound(0))
         .flatMap(var0x -> var0x.getString("Value"))
         .map(var1x -> var1x.equals(var1))
         .orElse(false);
   }

   private static boolean handle(ItemStack var0, List<StatusEffectInstance> var1) {
      PotionContentsComponent var2 = (PotionContentsComponent)var0.get(DataComponentTypes.POTION_CONTENTS);
      if (var2 == null) {
         return false;
      }

      List<StatusEffectInstance> var3 = var2.customEffects();

      for (StatusEffectInstance var5 : var1) {
         boolean var6 = false;

         for (StatusEffectInstance var8 : var3) {
            if (var8.getEffectType().equals(var5.getEffectType()) && var8.getAmplifier() == var5.getAmplifier()) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            return false;
         }
      }

      return true;
   }

   private static boolean process(ItemStack var0, String var1) {
      return var0.getName().getString().contains(var1);
   }

   private static boolean compute(ItemStack var0, String var1) {
      return var0.getName().getString().toLowerCase(Locale.ROOT).contains(var1.toLowerCase(Locale.ROOT));
   }

   private static boolean resolve(ItemStack var0, String var1) {
      LoreComponent var2 = (LoreComponent)var0.get(DataComponentTypes.LORE);
      if (var2 == null) {
         return false;
      }

      for (Text var4 : var2.lines()) {
         if (var4.getString().contains(var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean update(ItemStack var0, String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      if (var0.getName().getString().toLowerCase(Locale.ROOT).contains(var2)) {
         return true;
      }

      LoreComponent var3 = (LoreComponent)var0.get(DataComponentTypes.LORE);
      if (var3 == null) {
         return false;
      }

      for (Text var5 : var3.lines()) {
         if (var5.getString().toLowerCase(Locale.ROOT).contains(var2)) {
            return true;
         }
      }

      return false;
   }

   public static boolean handle(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.MAX_HEALTH, -4.0)
         && handle(var1, EntityAttributes.ARMOR, 1.5)
         && handle(var1, EntityAttributes.ATTACK_DAMAGE, 2.5)
         && handle(var1, EntityAttributes.MOVEMENT_SPEED, 0.07)
         && handle(var1, EntityAttributes.ATTACK_SPEED, 0.13)
         && handle(var1, EntityAttributes.GRAVITY, 0.09);
   }

   public static boolean process(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ARMOR, 2.5)
         && handle(var1, EntityAttributes.ARMOR_TOUGHNESS, 2.5)
         && handle(var1, EntityAttributes.MOVEMENT_SPEED, -0.15);
   }

   public static boolean compute(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 6.0) && handle(var1, EntityAttributes.ARMOR, -2.0) && handle(var1, EntityAttributes.MAX_HEALTH, -2.0);
   }

   public static boolean resolve(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ARMOR, 1.0)
         && handle(var1, EntityAttributes.MAX_HEALTH, 4.0)
         && handle(var1, EntityAttributes.MOVEMENT_SPEED, 0.1)
         && handle(var1, EntityAttributes.ATTACK_SPEED, 0.1);
   }

   public static boolean update(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.MAX_HEALTH, 4.0)
         && handle(var1, EntityAttributes.ARMOR, 2.0)
         && handle(var1, EntityAttributes.SUBMERGED_MINING_SPEED, 0.5)
         && handle(var1, EntityAttributes.OXYGEN_BONUS, 0.5);
   }

   public static boolean apply(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 2.0) && handle(var1, EntityAttributes.MAX_HEALTH, 2.0);
   }

   public static boolean execute(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.LUCK, 1.0)
         && handle(var1, EntityAttributes.MAX_HEALTH, 2.0)
         && handle(var1, EntityAttributes.BLOCK_INTERACTION_RANGE, 1.0);
   }

   public static boolean prepare(ItemStack var0) {
      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 2.0)
         && handle(var1, EntityAttributes.JUMP_STRENGTH, -0.1)
         && handle(var1, EntityAttributes.ATTACK_SPEED, 0.15);
   }

   public static boolean check(ItemStack var0) {
      return var0.isOf(Items.PLAYER_HEAD) && process(var0, "Сфера Мороза") && resolve(var0, "Вечная мерзлота");
   }

   public static boolean onTick(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 2.5) && handle(var1, EntityAttributes.ATTACK_SPEED, 0.1);
   }

   public static boolean select(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 7.0)
         && handle(var1, EntityAttributes.MAX_HEALTH, -4.0)
         && handle(var1, EntityAttributes.MOVEMENT_SPEED, 0.1);
   }

   public static boolean refresh(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ARMOR, 1.5) && handle(var1, EntityAttributes.MAX_HEALTH, 1.5);
   }

   public static boolean render(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 5.0) && handle(var1, EntityAttributes.MAX_HEALTH, -4.0);
   }

   public static boolean tick(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 2.0) && handle(var1, EntityAttributes.ARMOR, 2.0) && handle(var1, EntityAttributes.MAX_HEALTH, -4.0);
   }

   public static boolean drawAnimation(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.MAX_HEALTH, 4.0)
         && handle(var1, EntityAttributes.ATTACK_DAMAGE, 3.0)
         && handle(var1, EntityAttributes.ARMOR_TOUGHNESS, 2.0)
         && handle(var1, EntityAttributes.ARMOR, 2.0);
   }

   public static boolean encodePoint(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.ATTACK_DAMAGE, 4.0)
         && handle(var1, EntityAttributes.MAX_HEALTH, 2.0)
         && handle(var1, EntityAttributes.MOVEMENT_SPEED, 0.1)
         && handle(var1, EntityAttributes.ATTACK_SPEED, 0.1)
         && handle(var1, EntityAttributes.ARMOR, -3.0);
   }

   public static boolean animate(ItemStack var0) {
      if (!var0.isOf(Items.TOTEM_OF_UNDYING)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      return handle(var1, EntityAttributes.MAX_HEALTH, 2.0);
   }

   public static String load(ItemStack var0) {
      if (onTick(var0)) {
         return "Талисман Демона";
      } else if (select(var0)) {
         return "Талисман Карателя";
      } else if (refresh(var0)) {
         return "Талисман Мрака";
      } else if (render(var0)) {
         return "Талисман Ярости";
      } else if (tick(var0)) {
         return "Талисман Тирана";
      } else if (drawAnimation(var0)) {
         return "Талисман Крушителя";
      } else if (encodePoint(var0)) {
         return "Талисман Раздора";
      } else {
         return animate(var0) ? "Талисман Сары" : "";
      }
   }

   public static boolean save(ItemStack var0) {
      if (!var0.isOf(Items.SPLASH_POTION)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      boolean var2 = handle(var1, EntityAttributes.ATTACK_DAMAGE, 12.0)
         && handle(var1, EntityAttributes.MOVEMENT_SPEED, 0.6)
         && handle(var1, EntityAttributes.ATTACK_SPEED, 0.1);
      return var2 || handle(var0, state);
   }

   public static boolean submit(ItemStack var0) {
      if (!var0.isOf(Items.SPLASH_POTION)) {
         return false;
      }

      Map var1 = collapseConfig(var0);
      boolean var2 = handle(var1, EntityAttributes.ATTACK_DAMAGE, 5.0);
      return var2 && handle(var0, data);
   }

   public static boolean unload(ItemStack var0) {
      return !var0.isOf(Items.SPLASH_POTION) ? false : handle(var0, instance);
   }

   public static boolean fetch(ItemStack var0) {
      return !var0.isOf(Items.SPLASH_POTION) ? false : handle(var0, config) || process(var0, "Святая вода");
   }

   public static boolean measure(ItemStack var0) {
      return !var0.isOf(Items.SPLASH_POTION) ? false : handle(var0, context);
   }

   public static boolean blendMatrix(ItemStack var0) {
      return !var0.isOf(Items.SPLASH_POTION) ? false : handle(var0, cache);
   }

   public static boolean matchVector(ItemStack var0) {
      return !var0.isOf(Items.SPLASH_POTION) ? false : handle(var0, output);
   }

   public static boolean projectItem(ItemStack var0) {
      return var0.isOf(Items.SUGAR) && process(var0, "Явная пыль") && resolve(var0, "Каст: Световая вспышка");
   }

   public static boolean computeResponse(ItemStack var0) {
      return var0.isOf(Items.ENDER_EYE) && process(var0, "Дезориентация") && resolve(var0, "Чем ближе цель");
   }

   public static boolean fetchProvider(ItemStack var0) {
      return var0.isOf(Items.NETHERITE_SCRAP) && process(var0, "Трапка") && resolve(var0, "Каст: Нерушимая клетка");
   }

   public static boolean drawProfile(ItemStack var0) {
      return var0.isOf(Items.TRIPWIRE_HOOK) && process(var0, "Отмычка к Сферам") && resolve(var0, "Открыть хранилище с Сферам");
   }

   public static boolean performVector(ItemStack var0) {
      return var0.isOf(Items.DRIED_KELP) && process(var0, "Пласт") && resolve(var0, "Каст: Нерушимая стена");
   }

   public static boolean attachEvent(ItemStack var0) {
      return var0.isOf(Items.EXPERIENCE_BOTTLE) && (update(var0, "Опыт с уровнем 15") || update(var0, "15 ур"));
   }

   public static boolean readServer(ItemStack var0) {
      return var0.isOf(Items.EXPERIENCE_BOTTLE) && (update(var0, "Опыт с уровнем 30") || update(var0, "30 ур"));
   }

   public static boolean advancePosition(ItemStack var0) {
      return var0.isOf(Items.EXPERIENCE_BOTTLE) && (update(var0, "Опыт с уровнем 50") || update(var0, "50 ур"));
   }

   public static boolean checkFrame(ItemStack var0) {
      return var0.isOf(Items.EXPERIENCE_BOTTLE) && (update(var0, "Опыт с уровнем 45") || update(var0, "45 ур"));
   }

   public static boolean collectModule(ItemStack var0) {
      return var0.isOf(Items.TNT) && process(var0, "WHITE") && resolve(var0, "в 10 раз сильнее");
   }

   public static boolean closeProvider(ItemStack var0) {
      return var0.isOf(Items.TNT) && process(var0, "BLACK") && resolve(var0, "взорвать обсидиан");
   }

   public static boolean savePreset(ItemStack var0) {
      return var0.isOf(Items.CAMPFIRE) && process(var0, "Случайный") && resolve(var0, "Уровень лута: Случайный");
   }

   public static boolean convertWindow(ItemStack var0) {
      return var0.isOf(Items.CAMPFIRE) && process(var0, "Обычный") && resolve(var0, "Уровень лута: Обычный");
   }

   public static boolean writePreset(ItemStack var0) {
      return var0.isOf(Items.CAMPFIRE) && process(var0, "Богатый") && resolve(var0, "Уровень лута: Богатый");
   }

   public static boolean measureColor(ItemStack var0) {
      return var0.isOf(Items.SOUL_CAMPFIRE) && process(var0, "Легендарный") && resolve(var0, "Уровень лута: Легендарный");
   }

   public static boolean scheduleAnimation(ItemStack var0) {
      return var0.isOf(Items.JIGSAW) && process(var0, "Блок дамагер") && resolve(var0, "Каст: Нанесение урона");
   }

   public static boolean scanRenderer(ItemStack var0) {
      return var0.isOf(Items.STRUCTURE_BLOCK) && process(var0, "1x1") && resolve(var0, "(1x1)");
   }

   public static boolean buildSource(ItemStack var0) {
      return var0.isOf(Items.BEACON) && process(var0, "Маяк") && resolve(var0, "раздающий Монеты");
   }

   public static boolean collapseOutput(ItemStack var0) {
      return var0.isOf(Items.SOUL_LANTERN) && process(var0, "Проклятая душа") && resolve(var0, "Обменяй души");
   }

   public static boolean invokeProfile(ItemStack var0) {
      return var0.isOf(Items.PAPER) && process(var0, "Драконий скин") && resolve(var0, "Драконий скин взамен");
   }

   public static boolean scheduleSource(ItemStack var0) {
      return var0.isOf(Items.FIRE_CHARGE) && process(var0, "Огненный смерч") && resolve(var0, "Каст: Огненная волна");
   }

   public static boolean renderTimer(ItemStack var0) {
      return var0.isOf(Items.SNOWBALL) && process(var0, "Снежок заморозка") && resolve(var0, "Каст: Ледяная сфера");
   }

   public static boolean saveScale(ItemStack var0) {
      return var0.isOf(Items.PHANTOM_MEMBRANE) && process(var0, "Божья аура") && resolve(var0, "Каст: Божественная аура");
   }

   public static boolean computeColor(ItemStack var0) {
      return var0.isOf(Items.IRON_NUGGET) && process(var0, "Серебро");
   }

   public static boolean adaptScale(ItemStack var0) {
      return var0.isOf(Items.GOLDEN_PICKAXE) && compute(var0, "Божье касание") && resolve(var0, "Может добыть спавнер");
   }

   public static boolean runTexture(ItemStack var0) {
      return var0.isOf(Items.GOLDEN_PICKAXE) && process(var0, "Мощный удар") && resolve(var0, "Может разрушить бедрок");
   }

   public static boolean bindIndex(ItemStack var0) {
      return var0.isOf(Items.NETHERITE_PICKAXE) && process(var0, "мега-бульдозер") && resolve(var0, "Вскапывает территорию");
   }

   public static boolean readAction(ItemStack var0) {
      return var0.isOf(Items.ELYTRA) && process(var0, "Нерушимые элитры") && resolve(var0, "Нерушимый предмет");
   }

   public record DataRecord(String name, List<StatusEffectInstance> effects) {
   }
}
