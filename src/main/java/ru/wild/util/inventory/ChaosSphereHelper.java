package ru.wild.util.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ChaosSphereHelper {
   private static final Map<String, ItemStack> instance = new HashMap<>();

   public static void handle(DrawContext var0, String var1, float var2, float var3) {
      ItemStack var4 = handle(var1);
      if (var4 != null && !var4.isEmpty()) {
         var0.drawItem(var4, (int)var2, (int)var3);
      }
   }

   public static ItemStack handle(String var0) {
      if (instance.containsKey(var0)) {
         return instance.get(var0);
      }

      if (HolyWorldHelper.process(var0)) {
         ItemStack var4 = HolyWorldHelper.apply(var0);
         if (var4.isEmpty()) {
            return var4;
         }

         instance.put(var0, var4);
         return var4;
      } else {
         ItemStack var1 = switch (var0) {
            case "Сфера Хаоса" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDI3ODY0MTkwMCwKICAicHJvZmlsZUlkIiA6ICIxNzRjZmRiNGEzY2I0M2I1YmZjZGU0MjRjM2JiMmM2ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJhZWwxOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lN2E3YWU3Y2RjZjYxNmU4YjdhNDIyMWE2MjFiMjQzNTc1M2M2MGVkNmEyNThlYTA2MGRhZTMwMDJmZmU5ZTI4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
            );
            case "Сфера Титана" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDM1NDQ1NTE5MiwKICAicHJvZmlsZUlkIiA6ICJkOTcwYzEzZTM4YWI0NzlhOTY1OGM1ZDQ1MjZkMTM0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcmltcHlMYWNlODUxMjciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFlOTY5ODQ1OGI3ODQxYzk2YWU0ZjI0ZWM4NGFlMDE3MjQxMDA2NDFjNTY0ZTJhN2IxODVmNDA2ZThlZDIzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
            );
            case "Сфера Ареса" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDM0Mzc3NDI1NSwKICAicHJvZmlsZUlkIiA6ICJhZWNkODIxZTQyYzE0ZDJlOThmNTA1OTg1MWI5OWMzNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJqdXNhbXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzE2YWRjNmJhZmNiNTdmZDcwN2RlZTdkZDZhNzM2ZmUxMjY3MTFkNTNhMWZkNmNlNzg5ZGE0MWIzYmUxM2YyYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
            );
            case "Сфера Бестии" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDM0MzgzNDkzMCwKICAicHJvZmlsZUlkIiA6ICI1MzUzNWIxN2M0ZDY0NWQ0YWUwY2U2ZjM4Zjk0NTFjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJVYml2aXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQxMWFjMTczODFiOWZjZTliYWIzYzcyYWZkYjdmMTk4NTcwZGFmNDczMmJkODExZDMxYzIyN2Q4MGZhMzliMSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
            );
            case "Сфера Гидры" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDI3ODUzMjE4MywKICAicHJvZmlsZUlkIiA6ICI1OGZmZWI5NTMxNGQ0ODcwYTQwYjVjYjQyZDRlYTU5OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTa2luREJuZXQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2UzYzExOGQ2OTZkOTEwZTU0ZGUwMmNhNGQ4MDc1NDNmOWIxOGMwMDhjOTgzOGQyZmY2OTM3NzYyMmZiMWQzMiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
            );
            case "Сфера Икара" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDI3ODU4MjQ5MSwKICAicHJvZmlsZUlkIiA6ICJhZWNkODIxZTQyYzE0ZDJlOThmNTA1OTg1MWI5OWMzNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSb2RyaVgyMDc1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M2ODAzZTZkNTY2N2EyZDYxMDYyOGJjM2IzMmY4NjNjZGE0OTVjNDY1NjE2ZGU2NTVjYjMyOTkzM2I2MWFmNzciLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
            );
            case "Сфера Эрида" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDM0Mzg2MTE4NywKICAicHJvZmlsZUlkIiA6ICJlZGUyYzdhMGFjNjM0MTNiYjA5ZDNmMGJlZTllYzhlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0aGVEZXZKYWRlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZlNGUyZjEwNDdmM2VjNmU5ZTQ1OTE4NDczOWUzM2I3YzFmYzYzYWQ4MjAyYmRhYjlmMDI0NTA4YWRkMjNlNWIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
            );
            case "Сфера Сатира" -> process(
               "ewogICJ0aW1lc3RhbXAiIDogMTc1MDI3ODYwODUyOCwKICAicHJvZmlsZUlkIiA6ICJkMTQ4NjFiM2UwZmM0Njk5OTFlMTcyNTllMzdiZjZhZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJyYXhpdG9jbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83NzFhOWE0OThiNGZhNWVjNDkzNjJmOWJjODhlZGE0ZjUyYjA0ZGU0OWQ3NWFhM2NhMzMyYTFmZWExYWEwZTU3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
            );
            case "Талисман Демона", "Талисман Карателя", "Талисман Мрака", "Талисман Ярости", "Талисман Тирана", "Талисман Крушителя", "Талисман Раздора", "Талисман Инфинити", "Талисман Стингера", "Тотем Бессмертия", "Тотем бессмертия", "Тотем" -> new ItemStack(
               Items.TOTEM_OF_UNDYING
            );
            case "Вещи Крушителя", "Набор Крушителя", "Меч Крушителя" -> new ItemStack(Items.NETHERITE_SWORD);
            case "Броня Крушителя", "Броня Крушителя с шипами", "Броня Крушителя шип", "Броня Крушителя без шипов", "Броня Крушителя без шип", "Нагрудник Крушителя" -> new ItemStack(
               Items.NETHERITE_CHESTPLATE
            );
            case "Шлем Крушителя" -> new ItemStack(Items.NETHERITE_HELMET);
            case "Поножи Крушителя" -> new ItemStack(Items.NETHERITE_LEGGINGS);
            case "Ботинки Крушителя" -> new ItemStack(Items.NETHERITE_BOOTS);
            case "Кирка Крушителя" -> new ItemStack(Items.NETHERITE_PICKAXE);
            case "Лук Крушителя" -> new ItemStack(Items.BOW);
            case "Арбалет Крушителя" -> new ItemStack(Items.CROSSBOW);
            case "Трезубец Крушителя" -> new ItemStack(Items.TRIDENT);
            case "Булава Крушителя" -> new ItemStack(Items.MACE);
            case "Элитры Крушителя" -> new ItemStack(Items.ELYTRA);
            case "Удочка Крушителя" -> new ItemStack(Items.FISHING_ROD);
            case "Зелье Ассасина", "Зелье Гнева", "Хлопушка", "Святая Вода", "Зелье Палладина", "Зелье Радиации", "Снотворное" -> new ItemStack(
               Items.SPLASH_POTION
            );
            case "Явная Пыль" -> new ItemStack(Items.SUGAR);
            case "Дезориентация" -> new ItemStack(Items.ENDER_EYE);
            case "Трапка" -> new ItemStack(Items.NETHERITE_SCRAP);
            case "Отмычка к Сферам" -> new ItemStack(Items.TRIPWIRE_HOOK);
            case "Пласт" -> new ItemStack(Items.DRIED_KELP);
            case "Опыт 15", "Опыт 30", "Опыт 45", "Опыт 50", "Пузырек опыта" -> new ItemStack(Items.EXPERIENCE_BOTTLE);
            case "Вайт", "Блек" -> new ItemStack(Items.TNT);
            case "Блок дамагер" -> new ItemStack(Items.JIGSAW);
            case "Прогрузчик чанков" -> new ItemStack(Items.STRUCTURE_BLOCK);
            case "Маяк" -> new ItemStack(Items.BEACON);
            case "Проклятая Душа" -> new ItemStack(Items.SOUL_LANTERN);
            case "Драконий Скин" -> new ItemStack(Items.PAPER);
            case "Огненный Смерч" -> new ItemStack(Items.FIRE_CHARGE);
            case "Снежок Заморозка" -> new ItemStack(Items.SNOWBALL);
            case "Божья Аура" -> new ItemStack(Items.PHANTOM_MEMBRANE);
            case "Серебро" -> new ItemStack(Items.IRON_NUGGET);
            case "Божье Касание", "Мощный Удар" -> new ItemStack(Items.GOLDEN_PICKAXE);
            case "Мега Бульдозер" -> new ItemStack(Items.NETHERITE_PICKAXE);
            case "Нерушимые Элитры" -> new ItemStack(Items.ELYTRA);
            case "Зачарованное Золотое Яблоко", "Зачарованное яблоко" -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
            case "Золотое Яблоко", "Золотое яблоко", "Яблоко" -> new ItemStack(Items.GOLDEN_APPLE);
            case "Алмаз", "Алмазы" -> new ItemStack(Items.DIAMOND);
            case "Эндер-жемчуг", "Эндер жемчуг", "Перл" -> new ItemStack(Items.ENDER_PEARL);
            case "Кристалл Энда", "Кристалл энда", "Кристалл" -> new ItemStack(Items.END_CRYSTAL);
            case "Обсидиан" -> new ItemStack(Items.OBSIDIAN);
            case "Якорь Возрождения", "Якорь возрождения", "Якорь" -> new ItemStack(Items.RESPAWN_ANCHOR);
            case "Светящийся Камень", "Светящийся камень", "Глоустоун" -> new ItemStack(Items.GLOWSTONE);
            case "Паутина" -> new ItemStack(Items.COBWEB);
            case "Стрела", "Стрелы" -> new ItemStack(Items.ARROW);
            case "Спавнер" -> new ItemStack(Items.SPAWNER);
            default -> new ItemStack(Items.BARRIER);
         };
         instance.put(var0, var1);
         return var1;
      }
   }

   private static ItemStack process(String var0) {
      ItemStack var1 = new ItemStack(Items.PLAYER_HEAD);
      GameProfile var2 = new GameProfile(UUID.randomUUID(), "CustomHead");
      var2.getProperties().put("textures", new Property("textures", var0));
      var1.set(DataComponentTypes.PROFILE, new ProfileComponent(var2));
      return var1;
   }
}
