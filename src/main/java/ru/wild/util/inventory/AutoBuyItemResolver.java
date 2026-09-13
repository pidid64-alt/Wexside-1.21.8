package ru.wild.util.inventory;

import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.wild.mixin.acceser.HandledScreenAccessor;
import ru.wild.WildClient;
import ru.wild.util.text.ChatLogger;

public final class AutoBuyItemResolver {
   private AutoBuyItemResolver() {
   }

   public static String handle(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         List var1 = compute(var0);
         List var2 = resolve(var0);
         List var3 = update(var0);
         String var4 = apply(var0);
         StringBuilder var5 = new StringBuilder();
         var5.append("builder(\"")
            .append(compute(handle(var0.getName().getString(), process(var0.getItem()))))
            .append("\", ")
            .append(handle(var0.getItem()))
            .append(")");
         if (!var2.isEmpty()) {
            var5.append("\n        .enchantments(").append(handle(var2)).append(")");
         }

         if (!var3.isEmpty()) {
            var5.append("\n        .attributes(").append(String.join(", ", var3)).append(")");
         }

         if (!var1.isEmpty()) {
            var5.append("\n        .lore(").append(handle(var1)).append(")");
         }

         if (!var4.isBlank()) {
            var5.append("\n        .texture(\"").append(compute(var4)).append("\")");
         }

         var5.append("\n        .build(),");
         return var5.toString();
      } else {
         return "";
      }
   }

   public static String process(ItemStack var0) {
      String var1 = handle(var0);
      if (var1.isBlank()) {
         return "";
      }

      StringBuilder var2 = new StringBuilder(var1);
      var2.append("\n\ncomponents=").append(var0.getComponents());
      return var2.toString();
   }

   public static boolean handle(MinecraftClient var0) {
      if (var0 != null && var0.currentScreen instanceof HandledScreen var1) {
         Slot var5 = handle(var0, var1);
         if (var5 != null && var5.hasStack() && !var5.getStack().isEmpty()) {
            ItemStack var3 = var5.getStack();
            String var4 = handle(var3);
            if (var4.isBlank()) {
               ChatLogger.handle("§c[AutoBuy] §fНе удалось собрать код предмета.");
               return true;
            } else {
               var0.keyboard.setClipboard(var4);
               handle(var3, var4);
               ChatLogger.handle("§a[AutoBuy] §fКод предмета скопирован в буфер и записан в configs/autobuy/dumps.");
               return true;
            }
         } else {
            ChatLogger.handle("§c[AutoBuy] §fПод курсором нет предмета.");
            return true;
         }
      } else {
         return false;
      }
   }

   private static Slot handle(MinecraftClient var0, HandledScreen<?> var1) {
      HandledScreenAccessor var2 = (HandledScreenAccessor)var1;
      Slot var3 = var2.litka$getFocusedSlot();
      if (var3 != null) {
         return var3;
      }

      if (var0.getWindow() == null) {
         return null;
      }

      double var4 = var0.mouse.getScaledX(var0.getWindow());
      double var6 = var0.mouse.getScaledY(var0.getWindow());
      return var2.getSlotAtPosition(var4, var6);
   }

   private static void handle(ItemStack var0, String var1) {
      try {
         File var2 = WildClient.instance != null && WildClient.instance.cache != null ? WildClient.instance.cache : new File(".");
         File var3 = new File(var2, "configs/autobuy/dumps");
         if (!var3.exists()) {
            var3.mkdirs();
         }

         File var4 = new File(var3, "DonatItemsHW-snippets.txt");

         try (FileWriter var5 = new FileWriter(var4, true)) {
            var5.write("\n\n");
            var5.write(var1);
            var5.write("\n");
         }
      } catch (Exception var10) {
         ChatLogger.handle("§e[AutoBuy] §fКод скопирован, но файл дампа не записался: " + var10.getClass().getSimpleName());
      }
   }

   private static List<String> compute(ItemStack var0) {
      ArrayList var1 = new ArrayList();
      LoreComponent var2 = (LoreComponent)var0.get(DataComponentTypes.LORE);
      if (var2 == null) {
         return var1;
      }

      for (Text var4 : var2.lines()) {
         String var5 = process(var4.getString()).trim();
         if (!var5.isBlank() && !handle(var5)) {
            var1.add(var5);
         }
      }

      return var1;
   }

   private static List<String> resolve(ItemStack var0) {
      ArrayList<String> var1 = new ArrayList<>();
      ItemEnchantmentsComponent var2 = (ItemEnchantmentsComponent)var0.get(DataComponentTypes.ENCHANTMENTS);
      if (var2 != null && !var2.isEmpty()) {
         for (Entry var4 : var2.getEnchantmentEntries()) {
            RegistryEntry<?> enchantment = (RegistryEntry<?>)var4.getKey();
            String var5 = enchantment.getKey().map(key -> key.getValue().toString()).orElse("");
            if (!var5.isBlank()) {
               var1.add(var5 + ":" + var4.getIntValue());
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   private static List<String> update(ItemStack var0) {
      ArrayList var1 = new ArrayList();
      AttributeModifiersComponent var2 = (AttributeModifiersComponent)var0.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (var2 == null) {
         return var1;
      }

      for (net.minecraft.component.type.AttributeModifiersComponent.Entry var4 : var2.modifiers()) {
         EntityAttributeModifier var5 = var4.modifier();
         String var6 = handle(var4.attribute());
         if (!var6.isBlank()) {
            var1.add("attr(\"" + compute(var6) + "\", " + handle(var5.value()) + ")");
         }
      }

      return var1;
   }

   private static String apply(ItemStack var0) {
      ProfileComponent var1 = (ProfileComponent)var0.get(DataComponentTypes.PROFILE);
      if (var1 != null && var1.gameProfile() != null) {
         Collection var2 = var1.gameProfile().getProperties().get("textures");
         if (var2 != null && !var2.isEmpty()) {
            Property var3 = (Property)var2.iterator().next();
            return var3 != null && var3.value() != null ? var3.value() : "";
         } else {
            return "";
         }
      } else {
         return "";
      }
   }

   private static String handle(RegistryEntry<EntityAttribute> var0) {
      return var0.getKey().map(var0x -> var0x.getValue().toString()).orElse("");
   }

   private static String handle(Item var0) {
      Identifier var1 = Registries.ITEM.getId(var0);
      return !"minecraft".equals(var1.getNamespace())
         ? "Registries.ITEM.get(Identifier.of(\"" + compute(var1.toString()) + "\"))"
         : "Items." + var1.getPath().toUpperCase(Locale.ROOT);
   }

   private static String process(Item var0) {
      Identifier var1 = Registries.ITEM.getId(var0);
      return var1.getPath().replace('_', ' ');
   }

   private static String handle(String var0, String var1) {
      String var2 = process(var0).trim();
      return var2.isBlank() ? var1 : var2;
   }

   private static boolean handle(String var0) {
      String var1 = process(var0).toLowerCase(Locale.ROOT);
      return var1.contains("цена")
         || var1.contains("продавец")
         || var1.contains("купить")
         || var1.contains("нажмите")
         || var1.contains("лкм")
         || var1.contains("пкм")
         || var1.contains("shift")
         || var1.contains("страница")
         || var1.contains("истекает")
         || var1.contains("доступно")
         || var1.contains("аукцион");
   }

   private static String process(String var0) {
      return var0 == null ? "" : var0.replaceAll("(?i)§[0-9A-FK-OR]", "");
   }

   private static String handle(List<String> var0) {
      ArrayList var1 = new ArrayList();

      for (String var3 : var0) {
         var1.add("\"" + compute(var3) + "\"");
      }

      return String.join(", ", var1);
   }

   private static String handle(double var0) {
      BigDecimal var2 = BigDecimal.valueOf(var0).stripTrailingZeros();
      String var3 = var2.toPlainString();
      return var3.contains(".") ? var3 : var3 + ".0";
   }

   private static String compute(String var0) {
      return var0.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
   }
}
