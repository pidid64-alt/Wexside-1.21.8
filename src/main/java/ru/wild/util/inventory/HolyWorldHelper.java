package ru.wild.util.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HolyWorldHelper {
   private static final String instance = "holyworld:";
   private static final double data = 1.0E-4;
   private static final Map<String, String> context = Map.of("sweeping", "sweeping_edge");
   private static final Map<String, List<String>> config = Map.ofEntries(
      Map.entry("spawner-getter-enchant", List.of("спавнер", "добытьспавнер", "spawnergetter")),
      Map.entry("impenetrable-enchant-custom", List.of("непробиваем", "impenetrable")),
      Map.entry("drill-enchant-custom", List.of("бур", "бульдозер", "drill")),
      Map.entry("exp-enchant-custom", List.of("опытный", "опыт", "exp")),
      Map.entry("foundry-enchant-custom", List.of("автоплавка", "автоплав", "foundry")),
      Map.entry("internal-enchant-custom", List.of("internal", "встроен")),
      Map.entry("magnet-enchant-custom", List.of("магнит", "magnet")),
      Map.entry("critical-enchant-custom", List.of("крит", "critical")),
      Map.entry("destroyer-enchant-custom", List.of("разрушитель", "destroyer")),
      Map.entry("rich-enchant-custom", List.of("богач", "rich")),
      Map.entry("mob-farmer-enchant", List.of("фармер", "фермер", "mobfarmer"))
   );
   static final Map<String, Integer> state = new HashMap<>();
   private static final List<HolyWorldHelper.SecondaryDataRecord> cache = List.of(
      process("Шлем инфинити", Items.NETHERITE_HELMET)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:aqua_affinity:1",
            "minecraft:fire_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:respiration:3",
            "minecraft:protection:5"
         )
         .handle(handle("minecraft:armor", 3.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый II"})
         .process(),
      process("Нагрудник инфинити", Items.NETHERITE_CHESTPLATE)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:fire_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:protection:5"
         )
         .handle(handle("minecraft:armor", 8.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый II"})
         .process(),
      process("Поножи инфинити", Items.NETHERITE_LEGGINGS)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:fire_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:protection:5"
         )
         .handle(handle("minecraft:armor", 6.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый II"})
         .process(),
      process("Ботинки инфинити", Items.NETHERITE_BOOTS)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:feather_falling:4",
            "minecraft:depth_strider:3",
            "minecraft:fire_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:protection:5",
            "minecraft:soul_speed:3"
         )
         .handle(handle("minecraft:armor", 3.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый II"})
         .process(),
      process("Талисман инфинити", Items.TOTEM_OF_UNDYING)
         .process("minecraft:unbreaking:1")
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Макс. здоровье II", "• Броня II", "• Урон II", "• Скорость II")
         .process(),
      process("Кирка этернити", Items.NETHERITE_PICKAXE)
         .process("minecraft:efficiency:10", "minecraft:fortune:5", "minecraft:unbreaking:5", "minecraft:mending:1")
         .handle(handle("minecraft:attack_damage", 5.0), handle("minecraft:attack_speed", -2.8F))
         .handle("Магнетизм I", "Неразрушимость I", "Автоплавка", "Опытный III", "Бур II")
         .process(),
      process("Шлем этернити", Items.NETHERITE_HELMET)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:aqua_affinity:1",
            "minecraft:fire_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:respiration:3",
            "minecraft:protection:5"
         )
         .handle(handle("minecraft:armor", 3.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый I"})
         .process(),
      process("Нагрудник этернити", Items.NETHERITE_CHESTPLATE)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:fire_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:protection:5"
         )
         .handle(handle("minecraft:armor", 8.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый I"})
         .process(),
      process("Штаны этернити", Items.NETHERITE_LEGGINGS)
         .process(
            "minecraft:blast_protection:5",
            "minecraft:fire_protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:protection:5"
         )
         .handle(handle("minecraft:armor", 6.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый I"})
         .process(),
      process("Ботинки этернити", Items.NETHERITE_BOOTS)
         .process(
            "minecraft:fire_protection:5",
            "minecraft:soul_speed:3",
            "minecraft:blast_protection:5",
            "minecraft:unbreaking:5",
            "minecraft:protection:5",
            "minecraft:projectile_protection:5",
            "minecraft:depth_strider:3",
            "minecraft:feather_falling:4"
         )
         .handle(handle("minecraft:armor", 3.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый I"})
         .process(),
      process("Меч этернити", Items.NETHERITE_SWORD)
         .process(
            "minecraft:smite:7",
            "minecraft:bane_of_arthropods:7",
            "minecraft:fire_aspect:2",
            "minecraft:mending:1",
            "minecraft:sweeping_edge:3",
            "minecraft:unbreaking:5",
            "minecraft:looting:5",
            "minecraft:sharpness:7"
         )
         .handle(handle("minecraft:attack_damage", 7.0), handle("minecraft:attack_speed", -2.4F))
         .handle("Разрушитель II", "Богач I", "Критический II")
         .process(),
      process("Талисман этернити", Items.TOTEM_OF_UNDYING)
         .process("minecraft:unbreaking:1")
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Скорость II", "• Урон II", "• Броня II")
         .process(),
      process("Сфера этернити", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Броня II", "• Скорость II", "• Урон II")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
         )
         .process(),
      process("Кирка стингер", Items.NETHERITE_PICKAXE)
         .process("minecraft:efficiency:8", "minecraft:unbreaking:4", "minecraft:mending:1", "minecraft:fortune:4")
         .handle(handle("minecraft:attack_damage", 5.0), handle("minecraft:attack_speed", -2.8F))
         .handle("Неразрушимость I", "Автоплавка", "Опытный III", "Бур I")
         .process(),
      process("Шлем стингер", Items.NETHERITE_HELMET)
         .process(
            "minecraft:fire_protection:4",
            "minecraft:blast_protection:4",
            "minecraft:aqua_affinity:1",
            "minecraft:unbreaking:4",
            "minecraft:protection:5",
            "minecraft:projectile_protection:4",
            "minecraft:respiration:3"
         )
         .handle(handle("minecraft:armor", 3.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .process(),
      process("Нагрудник стингер", Items.NETHERITE_CHESTPLATE)
         .process(
            "minecraft:blast_protection:4",
            "minecraft:fire_protection:4",
            "minecraft:unbreaking:4",
            "minecraft:protection:5",
            "minecraft:projectile_protection:4"
         )
         .handle(handle("minecraft:armor", 8.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый I"})
         .process(),
      process("Штаны стингер", Items.NETHERITE_LEGGINGS)
         .process(
            "minecraft:blast_protection:4",
            "minecraft:fire_protection:4",
            "minecraft:unbreaking:4",
            "minecraft:protection:4",
            "minecraft:projectile_protection:4"
         )
         .handle(handle("minecraft:armor", 6.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .handle(new String[]{"Непробиваемый I"})
         .process(),
      process("Ботинки стингер", Items.NETHERITE_BOOTS)
         .process(
            "minecraft:fire_protection:4",
            "minecraft:soul_speed:3",
            "minecraft:blast_protection:4",
            "minecraft:unbreaking:4",
            "minecraft:protection:4",
            "minecraft:projectile_protection:4",
            "minecraft:depth_strider:3",
            "minecraft:feather_falling:4"
         )
         .handle(handle("minecraft:armor", 3.0), handle("minecraft:armor_toughness", 3.0), handle("minecraft:knockback_resistance", 0.1F))
         .process(),
      process("Меч стингер", Items.NETHERITE_SWORD)
         .process(
            "minecraft:smite:7",
            "minecraft:bane_of_arthropods:7",
            "minecraft:fire_aspect:2",
            "minecraft:mending:1",
            "minecraft:sweeping_edge:3",
            "minecraft:unbreaking:4",
            "minecraft:looting:5",
            "minecraft:sharpness:6"
         )
         .handle(handle("minecraft:attack_damage", 7.0), handle("minecraft:attack_speed", -2.4F))
         .handle("Богач I", "Критический II")
         .process(),
      process("Талисман стингер", Items.TOTEM_OF_UNDYING)
         .process("minecraft:unbreaking:1")
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Скорость I", "• Броня II", "• Урон II")
         .process(),
      process("Сфера стингер", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Броня II", "• Скорость I", "• Урон II")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
         )
         .process(),
      process("Сфера Цербера", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:waypoint_transmit_range", -1.0)})
         .handle("Проклятие утраты", "• Спешка I", "• Урон V")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0="
         )
         .process(),
      process("Сфера Флеша", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 1.0)})
         .handle("Проклятие утраты", "• Броня I", "• Скорость III")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc0MDBlYTE5ZGJkODRmNzVjMzlhZDY4MjNhYzRlZjc4NmYzOWY0OGZjNmY4NDYwMjM2NmFjMjliODM3NDIyIn19fQ=="
         )
         .process(),
      process("Легендарная сфера", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:waypoint_transmit_range", -1.0)})
         .handle(new String[]{"• Урон III"})
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
         )
         .process(),
      process("Мифическая сфера", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Броня II", "• Урон III")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ=="
         )
         .process(),
      process("Мифическая сфера", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 3.0)})
         .handle("• Скорость II", "• Броня III")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ=="
         )
         .process(),
      process("Золотой Спавнер", Items.SPAWNER)
         .handle(
            "Особенности:",
            "виртуально фармит мобов",
            ".  без спавна сущностей;",
            "лут и опыт копятся",
            ".  во внутреннем хранилище;",
            "вставка яйца может",
            ".  сломать спавнер.",
            "Шанс уничтожения: 50.6%"
         )
         .process(),
      process("Взрывчатое вещество", Items.CLAY)
         .handle("Особенности:", "используется только для крафта", ".   взрывных предметов;", "можно перекрафтить в 9 пороха.")
         .process(),
      process("100", Items.EXPERIENCE_BOTTLE).handle("В пузырьке 30971 опыта (100 ур.)", "Киньте пузырек, чтобы получить опыт").process(),
      process("Загадочный спавнер", Items.SPAWNER)
         .handle(
            "Потенциальное содержание:",
            "• Брутальный пиглин — 25.0%",
            "• Ведьма — 7.0%",
            "• Блейз — 20.0%",
            "• Зомби — 18.0%",
            "• Скелет — 30.0%",
            "▍ Может вмещать в себе случайного моба,",
            "▍ с шансом из списка, указанного выше."
         )
         .process(),
      process("Загадочное яйцо призыва", Items.WITCH_SPAWN_EGG)
         .handle(
            "Потенциальное содержание:",
            "• Брутальный пиглин — 25.0%",
            "• Ведьма — 7.0%",
            "• Блейз — 20.0%",
            "• Зомби — 18.0%",
            "• Скелет — 30.0%",
            "▍ Может вмещать в себе случайного моба,",
            "▍ с шансом из списка, указанного выше."
         )
         .process(),
      process("Загадочное яйцо призыва", Items.CREEPER_SPAWN_EGG)
         .handle(
            "Потенциальное содержание:",
            "• Брутальный пиглин — 33.0%",
            "• Крипер — 2.0%",
            "• Блейз — 17.5%",
            "• Зомби — 17.5%",
            "• Скелет — 30.0%",
            "▍ Может вмещать в себе случайного моба,",
            "▍ с шансом из списка, указанного выше."
         )
         .process(),
      process("Загадочное яйцо призыва", Items.PIGLIN_BRUTE_SPAWN_EGG)
         .handle(
            "Потенциальное содержание:",
            "• Брутальный пиглин — 50.0%",
            "• Ведьма — 4.0%",
            "• Мини-зомби — 20.0%",
            "• Крипер — 1.0%",
            "• Блейз — 25.0%",
            "▍ Может вмещать в себе случайного моба,",
            "▍ с шансом из списка, указанного выше."
         )
         .process(),
      process("Трапка", Items.POPPED_CHORUS_FRUIT).process(),
      process("Ком снега", Items.SNOWBALL, "Снежок заморозки", "Снежок заморозка").process(),
      process("Стан", Items.NETHER_STAR).process(),
      process("Взрывная трапка", Items.PRISMARINE_SHARD, new String[]{"Взрывная"}).process(),
      process("С4", Items.TNT).handle("Особенности:", "разрушает блок незеритового привата;", "взрывает блоки обсидиана.").process(),
      process("Справедливость", Items.POTION)
         .handle(
            "Особенности:",
            "когда предмет в инвентаре, вы получаете",
            ".   защиту от различных дебафов слепота",
            ".   прыгучесть, отравление, иссушение",
            ".   медлительность и слабость."
         )
         .process(),
      process("Броневая элитра", Items.ELYTRA)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 8.0)})
         .handle("Особенности:", "имеет свойства алмазного нагрудника;", "позволяет летать как обычная элитра;", "возможно накладывать зачарования.")
         .process(),
      process("Арбалет этернити", Items.CROSSBOW)
         .process("minecraft:piercing:5", "minecraft:multishot:1", "minecraft:unbreaking:3", "minecraft:quick_charge:3")
         .handle(new String[]{"Оглушение II"})
         .process(),
      process("Сфера ᴀʀᴍᴏʀᴛᴀʟɪᴛʏ", Items.PLAYER_HEAD, "Сфера armortlity", "Сфера armortality")
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:armor", 2.0)})
         .handle("• Броня II", "• Макс. здоровье II", "• Урон II")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE2MmI5ZGU2YTI2Yjg2ODY5Y2EyMmVhNDBmMWJkZTgwYTA0MzBhNTQ1NDdiZWNjZThmZGE4NzA3Nzc3MjU4ZiJ9fX0="
         )
         .process(),
      process("Сфера immortality", Items.PLAYER_HEAD)
         .handle(new HolyWorldHelper.DataRecord[]{handle("minecraft:waypoint_transmit_range", -1.0)})
         .handle("• Скорость II", "• Урон III")
         .handle(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0="
         )
         .process(),
      process("15", Items.EXPERIENCE_BOTTLE).handle("В пузырьке 315 опыта (15 ур.)", "Киньте пузырек, чтобы получить опыт").process(),
      process("50", Items.EXPERIENCE_BOTTLE).handle("В пузырьке 5345 опыта (50 ур.)", "Киньте пузырек, чтобы получить опыт").process(),
      process("Особый компас", Items.COMPASS)
         .process("minecraft:luck_of_the_sea:1")
         .handle("Особенности:", "- ведёт к ближайшему или случайному", "- можно использовать раз в 8 часов.")
         .process(),
      process("Тнт-Пушка", Items.DISPENSER)
         .process("minecraft:soul_speed:10")
         .handle(
            "Особенности:",
            "- запускает летящий динамит",
            ".   со скоростью до 5 блоков за секунду;",
            "- при запуске сохраняет свойства",
            ".   особых динамитов и пиротехники;",
            "- можно сломать в чужом привате.",
            "● Данный товар можно"
         )
         .process(),
      process("Меч инфинити", Items.NETHERITE_SWORD)
         .process(
            "minecraft:sharpness:8",
            "minecraft:unbreaking:5",
            "minecraft:mending:1",
            "minecraft:fire_aspect:2",
            "minecraft:bane_of_arthropods:7",
            "minecraft:sweeping_edge:3",
            "minecraft:smite:7",
            "minecraft:looting:5"
         )
         .handle(handle("minecraft:attack_damage", 7.0), handle("minecraft:attack_speed", -2.4F))
         .handle("Богач VI", "Разрушитель II", "Критический II")
         .process(),
      process("Меч Цербера ", Items.NETHERITE_SWORD)
         .process(
            "minecraft:sharpness:9",
            "minecraft:unbreaking:5",
            "minecraft:mending:1",
            "minecraft:fire_aspect:2",
            "minecraft:bane_of_arthropods:7",
            "minecraft:sweeping_edge:3",
            "minecraft:smite:7",
            "minecraft:looting:5"
         )
         .handle(handle("minecraft:attack_damage", 7.0), handle("minecraft:attack_speed", -2.4F))
         .handle("Богач VI", "Разрушитель III", "Критический II", "● Данный товар можно")
         .process(),
      process("Нерушимые элитры", Items.ELYTRA).process(),
      process("Меч Выгодный фарм", Items.NETHERITE_SWORD)
         .handle(handle("minecraft:attack_damage", 7.0), handle("minecraft:attack_speed", -2.4F))
         .handle("Фармер II", "● Данный товар можно")
         .process(),
      process("Рюкзак инфинити", Items.LIME_SHULKER_BOX, new String[]{"- Рюкзак Iɴғɪɴɪᴛʏ -"})
         .handle("Особенности:", "- нельзя поставить на землю;", "- вместимость 36 слотов;", "● Данный товар можно")
         .process(),
      process("Рюкзак 1 уровень", Items.PINK_SHULKER_BOX, "Рюкзак I уровень", "Рюкзак (I уровень)")
         .handle("Особенности:", "- нельзя поставить на землю;", "- вместимость 9 слотов;")
         .process(),
      process("Рюкзак 2 уровень", Items.LIGHT_BLUE_SHULKER_BOX, "Рюкзак II уровень", "Рюкзак (II уровень)")
         .handle("Особенности:", "- нельзя поставить на землю;", "- вместимость 15 слотов;")
         .process(),
      process("Рюкзак 3 уровень", Items.RED_SHULKER_BOX, "Рюкзак III уровень", "Рюкзак (III уровень)")
         .handle("Особенности:", "- нельзя поставить на землю;", "- вместимость 21 слот;", "● Данный товар можно")
         .process(),
      process("Рюкзак 4 уровень", Items.MAGENTA_SHULKER_BOX, "Рюкзак IV уровень", "Рюкзак (IV уровень)")
         .handle("Особенности:", "- нельзя поставить на землю;", "- вместимость 27 слотов;", "● Данный товар можно")
         .process(),
      process("Руна Бессмертие", Items.ORANGE_DYE)
         .process("minecraft:luck_of_the_sea:1")
         .handle(
            "Эффект руны",
            "Особенности:",
            "после активации тотема с этим эффектом,",
            ".   Вы получите неуязвимость к урону",
            ".   продолжительностью 3 секунды;",
            "возможность наложить данный эффект",
            ".   на тотем через наковальню;"
         )
         .process(),
      process("Зелье исцеление", Items.POTION).process(),
      process("Зелье черепашьей мощи", Items.POTION).process(),
      process("Зелье черепашьей мощи", Items.POTION).process(),
      process("Эндер-жемчуг", Items.ENDER_PEARL).process(),
      process("Динамит а", Items.TNT).handle("Особенности:", "имеет в 3 раза больший радиус взрыва.").process(),
      process("Динамит б", Items.TNT).handle("Особенности:", "имеет в 10 раз больший радиус взрыва.").process(),
      process("Динамит б2", Items.TNT)
         .handle(
            "Особенности:", "взрывает практически все блоки", ".   в радиусе 12 блоков;", "не работает на всех стандартных", ".   заприваченных территориях;"
         )
         .process(),
      process("С4 взрывчатка", Items.TNT).handle("Особенности:", "разрушает блок незеритового привата;", "взрывает блоки обсидиана.").process()
   );
   private static final Map<String, HolyWorldHelper.SecondaryDataRecord> output = process();

   public static List<HolyWorldHelper.SecondaryDataRecord> handle() {
      return cache;
   }

   public static boolean handle(String var0) {
      return var0 != null && var0.startsWith("holyworld:");
   }

   public static boolean process(String var0) {
      return compute(var0) != null;
   }

   public static HolyWorldHelper.SecondaryDataRecord compute(String var0) {
      if (var0 != null && !var0.isBlank()) {
         HolyWorldHelper.SecondaryDataRecord var1 = output.get(var0);
         return var1 != null ? var1 : output.get(render(refresh(var0)));
      } else {
         return null;
      }
   }

   public static String resolve(String var0) {
      HolyWorldHelper.SecondaryDataRecord var1 = compute(var0);
      return var1 == null ? var0 : var1.label();
   }

   public static String update(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String[] var1 = var0.split(":");
         return var1.length >= 2 ? check(var1[0] + ":" + var1[1]) : check(var0);
      } else {
         return "";
      }
   }

   public static ItemStack apply(String var0) {
      HolyWorldHelper.SecondaryDataRecord var1 = compute(var0);
      if (var1 == null) {
         return ItemStack.EMPTY;
      } else {
         return var1.item() == Items.PLAYER_HEAD && var1.texture() != null && !var1.texture().isBlank()
            ? process(var1.texture(), var1.label())
            : new ItemStack(var1.item());
      }
   }

   public static boolean handle(ItemStack var0) {
      return handle("Трапка", var0);
   }

   public static boolean process(ItemStack var0) {
      return handle("Ком снега", var0);
   }

   public static boolean compute(ItemStack var0) {
      return handle("Стан", var0);
   }

   public static boolean resolve(ItemStack var0) {
      return handle("Взрывная трапка", var0);
   }

   public static boolean handle(String var0, ItemStack var1, String var2) {
      HolyWorldHelper.SecondaryDataRecord var3 = compute(var0);
      if (var3 != null && var1 != null && !var1.isEmpty() && var1.isOf(var3.item())) {
         String var4 = render(var2);
         if (var4.isEmpty()) {
            var4 = render(var1.getName().getString());
         }

         String var5 = render(execute(var1));
         return process(var3, var1, var4, var5);
      } else {
         return false;
      }
   }

   private static boolean handle(String var0, ItemStack var1) {
      HolyWorldHelper.SecondaryDataRecord var2 = compute(var0);
      return var2 != null && handle(var2, var1, update(var1), apply(var1));
   }

   public static boolean handle(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, String var3) {
      return handle(var0, var1, var2, var3, true, true, true, true);
   }

   public static boolean handle(
      HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, String var3, boolean var4, boolean var5, boolean var6, boolean var7
   ) {
      if (var0 != null && var1 != null && !var1.isEmpty() && var1.isOf(var0.item())) {
         String var8 = var2 == null ? "" : var2;
         if (var8.isEmpty()) {
            var8 = render(var1.getName().getString());
         }

         String var9 = var3 == null ? "" : var3;
         if (var9.isEmpty()) {
            var9 = var8;
         }

         return process(var0, var1, var8, var9, var4, var5, var6, var7);
      } else {
         return false;
      }
   }

   public static boolean handle(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, String var3, Set<String> var4) {
      if (var0 != null && var1 != null && !var1.isEmpty() && var1.isOf(var0.item())) {
         String var5 = var2 == null ? "" : var2;
         if (var5.isEmpty()) {
            var5 = render(var1.getName().getString());
         }

         String var6 = var3 == null ? "" : var3;
         if (var6.isEmpty()) {
            var6 = var5;
         }

         return process(var0, var1, var5, var6, var4);
      } else {
         return false;
      }
   }

   public static String update(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         StringBuilder var1 = new StringBuilder();
         var1.append(var0.getName().getString()).append(' ');
         LoreComponent var2 = (LoreComponent)var0.get(DataComponentTypes.LORE);
         if (var2 != null) {
            for (Text var4 : var2.lines()) {
               var1.append(var4.getString()).append(' ');
            }
         }

         return render(var1.toString());
      } else {
         return "";
      }
   }

   public static String apply(ItemStack var0) {
      return var0 != null && !var0.isEmpty() ? render(execute(var0)) : "";
   }

   public static String execute(String var0) {
      return var0 == null ? "" : drawAnimation(tick(var0)).trim();
   }

   private static boolean process(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, String var3) {
      if (handle(var0, var2)) {
         return false;
      }

      if (var3.isEmpty()) {
         var3 = var2;
      }

      boolean var4 = handle(var2, var0.aliases());
      if (!var4) {
         return false;
      } else {
         return !var0.hasRequirements() ? true : process(var0, var1, var2, var3, true, true, true, true);
      }
   }

   private static boolean process(
      HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, String var3, boolean var4, boolean var5, boolean var6, boolean var7
   ) {
      if (handle(var0, var2)) {
         return false;
      }

      if (var3.isEmpty()) {
         var3 = var2;
      }

      boolean var8 = handle(var2, var0.aliases());
      if (!var8) {
         return false;
      } else {
         return !var0.hasRequirements()
            ? true
            : (!var4 || process(var0, var3))
               && (!var5 || handle(var0, var1, var3))
               && (!var6 || process(var0, var1, var3))
               && (!var7 || compute(var0, var1, var3));
      }
   }

   private static boolean process(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, String var3, Set<String> var4) {
      if (handle(var0, var2)) {
         return false;
      }

      if (var3.isEmpty()) {
         var3 = var2;
      }

      boolean var5 = handle(var2, var0.aliases());
      if (!var5) {
         return false;
      } else {
         return !var0.hasRequirements() ? true : process(var0, var3) && handle(var0, var1, var3) && handle(var0, var1, var3, var4) && compute(var0, var1, var3);
      }
   }

   private static boolean handle(HolyWorldHelper.SecondaryDataRecord var0, String var1) {
      String var2 = render(var0.label());
      return var2.equals("элитры") && var1.contains("броневаяэлитра")
         || var2.equals("динамитb") && var1.contains("динамитb2")
         || var2.equals("зельечерепашьеймощи")
            && (var1.contains("зельечерепашьеймощиii") || var1.contains("черепашьямощьii") || var1.contains("черепашьямощь2"));
   }

   private static boolean process(HolyWorldHelper.SecondaryDataRecord var0, String var1) {
      for (String var3 : var0.lore()) {
         String var4 = render(var3);
         if (!var4.isEmpty() && !var1.contains(var4)) {
            return false;
         }
      }

      return true;
   }

   private static boolean handle(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2) {
      for (HolyWorldHelper.DataRecord var4 : var0.attributes()) {
         if (!handle(var1, var4) && !handle(var4, var2)) {
            return false;
         }
      }

      return true;
   }

   private static boolean process(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2) {
      for (String var4 : var0.enchantments()) {
         HolyWorldHelper.FallbackDataRecord var5 = onTick(var4);
         if (var5 != null) {
            if (prepare(var5.id())) {
               if (!handle(var1, var5.id(), var5.level()) && !handle(var2, var5.raw())) {
                  return false;
               }
            } else {
               boolean var6 = handle(var2, var5.raw()) || handle(var2, var5);
               if (var0.strictCheck() && !var6) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static boolean handle(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2, Set<String> var3) {
      for (String var5 : var0.enchantments()) {
         if (var3 == null || var3.contains(update(var5))) {
            HolyWorldHelper.FallbackDataRecord var6 = onTick(var5);
            if (var6 != null) {
               if (prepare(var6.id())) {
                  if (!handle(var1, var6.id(), var6.level()) && !handle(var2, var6.raw())) {
                     return false;
                  }
               } else {
                  boolean var7 = handle(var2, var6.raw()) || handle(var2, var6);
                  if (var0.strictCheck() && !var7) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   private static boolean compute(HolyWorldHelper.SecondaryDataRecord var0, ItemStack var1, String var2) {
      if (var0.effects().isEmpty()) {
         return true;
      }

      boolean var3 = var2.contains("hms")
         || var1.get(DataComponentTypes.ATTRIBUTE_MODIFIERS) != null
         || handle(var2, List.of("урон", "брон", "скор", "здоров", "damage", "armor", "speed", "health"));
      if (!var3) {
         return true;
      }

      for (String var5 : var0.effects()) {
         if (!process(var5, var1, var2)) {
            return false;
         }
      }

      return true;
   }

   private static boolean process(String var0, ItemStack var1, String var2) {
      String var3 = render(var0);
      if (!var3.isEmpty() && var2.contains(var3)) {
         return true;
      }

      HolyWorldHelper.PrimaryDataRecord var4 = select(var0);
      if (var4 == null) {
         return true;
      }

      RegistryEntry<EntityAttribute> var5 = switch (var4.type()) {
         case "damage" -> EntityAttributes.ATTACK_DAMAGE;
         case "armor" -> EntityAttributes.ARMOR;
         case "speed" -> EntityAttributes.MOVEMENT_SPEED;
         case "health" -> EntityAttributes.MAX_HEALTH;
         default -> null;
      };
      return var5 != null && handle(var1, var5, var4.level()) ? true : handle(var2, var4.type(), var4.level());
   }

   private static boolean handle(String var0, String var1, double var2) {
      String var4 = handle(var2);
      String var5 = handle((int)var2);

      for (String var11 : switch (var1) {
         case "damage" -> List.of("урон", "damage");
         case "armor" -> List.of("брон", "armor");
         case "speed" -> List.of("скор", "speed");
         case "health" -> List.of("здоров", "health");
         default -> List.of(var1);
      }) {
         String var9 = render(var11);
         if (var0.contains(var9 + var4) || var0.contains(var4 + var9) || !var5.isEmpty() && (var0.contains(var9 + var5) || var0.contains(var5 + var9))) {
            return true;
         }
      }

      return false;
   }

   private static boolean handle(String var0, HolyWorldHelper.FallbackDataRecord var1) {
      List<String> var2 = config.getOrDefault(var1.id(), List.of());
      if (var2.isEmpty()) {
         return false;
      }

      String var3 = handle((double)var1.level());
      String var4 = handle(var1.level());

      for (String var6 : var2) {
         String var7 = render(var6);
         if (!var7.isEmpty()) {
            if (!var0.contains(var7 + var3) && !var0.contains(var3 + var7)) {
               if (var4.isEmpty() || !var0.contains(var7 + var4) && !var0.contains(var4 + var7)) {
                  if (var1.level() <= 1 && var0.contains(var7)) {
                     return true;
                  }
                  continue;
               }

               return true;
            }

            return true;
         }
      }

      return false;
   }

   private static boolean handle(String var0, String var1) {
      String var2 = render(var1);
      return !var2.isEmpty() && var0.contains(var2);
   }

   private static boolean handle(HolyWorldHelper.DataRecord var0, String var1) {
      String var2 = process(var0.value());
      String var3 = handle(var0);
      if (!var3.isEmpty()) {
         if (var1.contains(var3 + var2)) {
            return true;
         }

         if (var1.contains(var2 + var3)) {
            return true;
         }
      }

      return false;
   }

   private static boolean handle(ItemStack var0, HolyWorldHelper.DataRecord var1) {
      AttributeModifiersComponent var2 = (AttributeModifiersComponent)var0.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (var2 == null) {
         return false;
      }

      for (Entry var4 : var2.modifiers()) {
         EntityAttributeModifier var5 = var4.modifier();
         if (handle(var1, var4.attribute()) && Math.abs(var5.value() - var1.value()) <= 1.0E-4) {
            return true;
         }
      }

      return false;
   }

   private static boolean handle(ItemStack var0, RegistryEntry<EntityAttribute> var1, double var2) {
      AttributeModifiersComponent var4 = (AttributeModifiersComponent)var0.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (var4 == null) {
         return false;
      }

      for (Entry var6 : var4.modifiers()) {
         EntityAttributeModifier var7 = var6.modifier();
         if (var6.attribute().equals(var1) && Math.abs(var7.value() - var2) <= 1.0E-4) {
            return true;
         }
      }

      return false;
   }

   private static boolean handle(HolyWorldHelper.DataRecord var0, RegistryEntry<EntityAttribute> var1) {
      if (var0.attribute() != null && var0.attribute().equals(var1)) {
         return true;
      }

      String var2 = encodePoint(var0.id());
      String var3 = encodePoint(process(var1));
      return !var2.isEmpty() && var2.equals(var3);
   }

   private static boolean handle(ItemStack var0, String var1, int var2) {
      ItemEnchantmentsComponent var3 = (ItemEnchantmentsComponent)var0.get(DataComponentTypes.ENCHANTMENTS);
      if (var3 != null && !var3.isEmpty()) {
         String var4 = check(var1);

         for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<RegistryEntry<Enchantment>> var6 : var3.getEnchantmentEntries()) {
            String var7 = handle(var6.getKey());
            if (var4.equals(check(var7)) && var6.getIntValue() >= var2) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String handle(RegistryEntry<Enchantment> var0) {
      Optional<Identifier> var1 = var0.getKey().map(var0x -> var0x.getValue());
      return var1.map(var0x -> var0x.toString()).orElse("");
   }

   private static boolean prepare(String var0) {
      String var1 = check(var0);

      return switch (var1) {
         case "aqua_affinity", "blast_protection", "depth_strider", "efficiency", "feather_falling", "fire_aspect", "fire_protection", "fortune", "luck_of_the_sea", "looting", "mending", "projectile_protection", "protection", "respiration", "sharpness", "smite", "soul_speed", "sweeping_edge", "thorns", "unbreaking", "bane_of_arthropods" -> true;
         default -> false;
      };
   }

   private static String check(String var0) {
      String var1 = var0 == null ? "" : var0.toLowerCase(Locale.ROOT).trim();
      int var2 = var1.indexOf(58);
      if (var2 >= 0 && var1.substring(0, var2).indexOf(45) < 0) {
         var1 = var1.substring(var2 + 1);
      }

      return context.getOrDefault(var1, var1);
   }

   private static HolyWorldHelper.FallbackDataRecord onTick(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim();
         int var2 = var1.lastIndexOf(58);
         String var3 = var2 > 0 ? var1.substring(0, var2).trim().toLowerCase(Locale.ROOT) : var1.toLowerCase(Locale.ROOT);
         int var4 = 1;
         if (var2 > 0 && var2 < var1.length() - 1) {
            try {
               var4 = Integer.parseInt(var1.substring(var2 + 1).replaceAll("[^0-9]", ""));
            } catch (NumberFormatException var6) {
               var4 = 1;
            }
         }

         return new HolyWorldHelper.FallbackDataRecord(var0, var3, Math.max(1, var4));
      } else {
         return null;
      }
   }

   private static HolyWorldHelper.PrimaryDataRecord select(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String[] var1 = var0.split(":", 2);
         if (var1.length != 2) {
            return null;
         }

         String var2 = var1[0].toLowerCase(Locale.ROOT).replace("hms-", "").trim();

         try {
            return new HolyWorldHelper.PrimaryDataRecord(var2, Double.parseDouble(var1[1].replace(',', '.')));
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Map<String, HolyWorldHelper.SecondaryDataRecord> process() {
      HashMap<String, HolyWorldHelper.SecondaryDataRecord> var0 = new HashMap<>();

      for (HolyWorldHelper.SecondaryDataRecord var2 : cache) {
         var0.put(var2.key(), var2);
         var0.put(render(var2.label()), var2);

         for (String var4 : var2.aliases()) {
            if (!var4.isEmpty()) {
               var0.putIfAbsent(var4, var2);
            }
         }
      }

      return Map.copyOf(var0);
   }

   private static HolyWorldHelper.SecondaryDataRecord handle(String var0, Item var1, String... var2) {
      return process(var0, var1, var2).process();
   }

   private static HolyWorldHelper.CacheEntry process(String var0, Item var1, String... var2) {
      return new HolyWorldHelper.CacheEntry(var0, var1, var2);
   }

   private static HolyWorldHelper.DataRecord handle(RegistryEntry<EntityAttribute> var0, double var1) {
      return new HolyWorldHelper.DataRecord(var0, process(var0), var1);
   }

   private static HolyWorldHelper.DataRecord handle(String var0, double var1) {
      return new HolyWorldHelper.DataRecord(null, var0, var1);
   }

   private static ItemStack process(String var0, String var1) {
      ItemStack var2 = new ItemStack(Items.PLAYER_HEAD);
      UUID var3 = UUID.nameUUIDFromBytes(("holyworld:" + var1 + var0).getBytes(StandardCharsets.UTF_8));
      GameProfile var4 = new GameProfile(var3, "");
      var4.getProperties().put("textures", new Property("textures", var0));
      var2.set(DataComponentTypes.PROFILE, new ProfileComponent(var4));
      return var2;
   }

   private static String refresh(String var0) {
      return handle(var0) ? var0.substring("holyworld:".length()) : var0;
   }

   static String render(String var0) {
      return var0 == null ? "" : drawAnimation(tick(var0).replaceAll("(?i)§[0-9A-FK-OR]", "").toLowerCase(Locale.ROOT)).replaceAll("[^\\p{L}\\p{N}]+", "");
   }

   private static String tick(String var0) {
      return var0.replace("ᴀ", "a")
         .replace("ʙ", "b")
         .replace("ᴄ", "c")
         .replace("ᴅ", "d")
         .replace("ᴇ", "e")
         .replace("ғ", "f")
         .replace("ɢ", "g")
         .replace("ʜ", "h")
         .replace("ɪ", "i")
         .replace("ᴊ", "j")
         .replace("ᴋ", "k")
         .replace("ʟ", "l")
         .replace("ᴍ", "m")
         .replace("ɴ", "n")
         .replace("ᴏ", "o")
         .replace("ᴘ", "p")
         .replace("ǫ", "q")
         .replace("ʀ", "r")
         .replace("ѕ", "s")
         .replace("ᴛ", "t")
         .replace("ᴜ", "u")
         .replace("ᴠ", "v")
         .replace("ᴡ", "w")
         .replace("х", "x")
         .replace("ʏ", "y")
         .replace("ᴢ", "z");
   }

   private static String drawAnimation(String var0) {
      return var0.replace("инфинити", "infinity").replace("этернити", "eternity").replace("етернити", "eternity").replace("стингер", "stinger");
   }

   private static boolean handle(String var0, List<String> var1) {
      if (var0 != null && !var0.isEmpty()) {
         for (String var3 : var1) {
            if (var3 != null && !var3.isEmpty() && var0.contains(var3)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String execute(ItemStack var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append(var0.getName().getString()).append(' ');
      LoreComponent var2 = (LoreComponent)var0.get(DataComponentTypes.LORE);
      if (var2 != null) {
         for (Text var4 : var2.lines()) {
            var1.append(var4.getString()).append(' ');
         }
      }

      var1.append(var0.getComponents());
      return var1.toString();
   }

   private static String handle(HolyWorldHelper.DataRecord var0) {
      String var1 = encodePoint(var0.id());
      if (!var1.isEmpty()) {
         return render(var1);
      } else {
         RegistryEntry<EntityAttribute> var2 = var0.attribute();
         if (var2 == null) {
            return "";
         } else if (var2.equals(EntityAttributes.ATTACK_DAMAGE)) {
            return "attackdamage";
         } else if (var2.equals(EntityAttributes.ARMOR)) {
            return "armor";
         } else if (var2.equals(EntityAttributes.MOVEMENT_SPEED)) {
            return "movementspeed";
         } else {
            return var2.equals(EntityAttributes.MAX_HEALTH) ? "maxhealth" : "";
         }
      }
   }

   private static String process(RegistryEntry<EntityAttribute> var0) {
      return var0 == null ? "" : var0.getKey().map(var0x -> var0x.getValue().toString()).orElse("");
   }

   private static String encodePoint(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.toLowerCase(Locale.ROOT).trim();
      if (var1.startsWith("minecraft:")) {
         var1 = var1.substring("minecraft:".length());
      }

      if (var1.startsWith("generic.")) {
         var1 = var1.substring("generic.".length());
      }

      return var1.replace('.', '_');
   }

   private static String handle(double var0) {
      return var0 == Math.rint(var0) ? String.valueOf((int)var0) : process(var0);
   }

   private static String process(double var0) {
      return var0 == Math.rint(var0) ? String.valueOf((int)var0) : String.valueOf(var0).replace(".", "");
   }

   private static String handle(int var0) {
      return switch (var0) {
         case 1 -> "i";
         case 2 -> "ii";
         case 3 -> "iii";
         case 4 -> "iv";
         case 5 -> "v";
         case 6 -> "vi";
         case 7 -> "vii";
         case 8 -> "viii";
         case 9 -> "ix";
         case 10 -> "x";
         default -> "";
      };
   }

   static final class CacheEntry {
      private final String instance;
      private final Item data;
      private final List<String> context = new ArrayList<>();
      private final List<String> config = new ArrayList<>();
      private final List<String> state = new ArrayList<>();
      private final List<String> cache = new ArrayList<>();
      private final List<HolyWorldHelper.DataRecord> output = new ArrayList<>();
      private String current;
      private boolean active;

      CacheEntry(String var1, Item var2, String... var3) {
         this.instance = var1;
         this.data = var2;
         this.context.add(HolyWorldHelper.render(var1));

         for (String var7 : var3) {
            this.context.add(HolyWorldHelper.render(var7));
         }
      }

      HolyWorldHelper.CacheEntry handle(String... var1) {
         this.config.addAll(List.of(var1));
         return this;
      }

      HolyWorldHelper.CacheEntry process(String... var1) {
         this.state.addAll(List.of(var1));
         return this;
      }

      private HolyWorldHelper.CacheEntry compute(String... var1) {
         this.cache.addAll(List.of(var1));
         return this;
      }

      HolyWorldHelper.CacheEntry handle(HolyWorldHelper.DataRecord... var1) {
         this.output.addAll(List.of(var1));
         return this;
      }

      HolyWorldHelper.CacheEntry handle(String var1) {
         this.current = var1;
         return this;
      }

      private HolyWorldHelper.CacheEntry handle() {
         this.active = true;
         return this;
      }

      HolyWorldHelper.SecondaryDataRecord process() {
         String var1 = "holyworld:" + HolyWorldHelper.render(this.instance);
         int var2 = HolyWorldHelper.state.merge(var1, 1, Integer::sum);
         return new HolyWorldHelper.SecondaryDataRecord(
            var2 == 1 ? var1 : var1 + ":" + var2,
            this.instance,
            this.data,
            List.copyOf(this.context),
            List.copyOf(this.config),
            List.copyOf(this.state),
            List.copyOf(this.cache),
            List.copyOf(this.output),
            this.current,
            this.active
         );
      }
   }

   public record DataRecord(RegistryEntry<EntityAttribute> attribute, String id, double value) {
   }

   record FallbackDataRecord(String raw, String id, int level) {
   }

   record PrimaryDataRecord(String type, double level) {
   }

   public record SecondaryDataRecord(
      String key,
      String label,
      Item item,
      List<String> aliases,
      List<String> lore,
      List<String> enchantments,
      List<String> effects,
      List<HolyWorldHelper.DataRecord> attributes,
      String texture,
      boolean strictCheck
   ) {
      boolean hasRequirements() {
         return !this.lore.isEmpty() || !this.enchantments.isEmpty() || !this.effects.isEmpty() || !this.attributes.isEmpty();
      }
   }
}
