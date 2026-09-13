package ru.wild.util.inventory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class AuctionLoreParser {
   private static final Pattern instance = Pattern.compile("Продавец:\\s*(.+)");
   private static final Pattern data = Pattern.compile("\\$(?:[^\\d]*?Цена)?[^\\d]*?([0-9][\\d,]*)");

   public static String handle(Slot var0) {
      if (!var0.hasStack()) {
         return null;
      }

      ItemStack var1 = var0.getStack();
      LoreComponent var2 = (LoreComponent)var1.getComponents().get(DataComponentTypes.LORE);
      if (var2 != null) {
         for (Text var5 : var2.lines()) {
            String var6 = var5.getString().replaceAll("(?i)§[0-9A-FK-OR]", "");
            Matcher var7 = instance.matcher(var6);
            if (var7.find()) {
               return var7.group(1).trim();
            }
         }
      }

      return null;
   }

   public static int process(Slot var0) {
      if (!var0.hasStack()) {
         return 0;
      }

      ItemStack var1 = var0.getStack();
      LoreComponent var2 = (LoreComponent)var1.getComponents().get(DataComponentTypes.LORE);
      if (var2 != null) {
         for (Text var5 : var2.lines()) {
            String var6 = var5.getString();
            if (var6.contains("$") || var6.contains("Цена")) {
               String var7 = var6.replaceAll("[^0-9]", "");
               if (!var7.isEmpty()) {
                  try {
                     return Math.toIntExact(Long.parseLong(var7));
                  } catch (NumberFormatException var9) {
                  }
               }
            }
         }
      }

      return 0;
   }
}
