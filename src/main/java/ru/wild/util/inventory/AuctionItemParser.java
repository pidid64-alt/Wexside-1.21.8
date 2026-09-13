package ru.wild.util.inventory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class AuctionItemParser {
   public static long handle(Slot var0) {
      if (!var0.hasStack()) {
         return 0L;
      }

      ItemStack var1 = var0.getStack();
      LoreComponent var2 = (LoreComponent)var1.getComponents().get(DataComponentTypes.LORE);
      if (var2 != null) {
         for (Text var5 : var2.lines()) {
            String var6 = var5.getString();
            if (var6.contains("Цена:")) {
               String var7 = var6.replaceAll("[^0-9]", "");
               if (!var7.isEmpty()) {
                  try {
                     return Long.parseLong(var7);
                  } catch (NumberFormatException var9) {
                  }
               }
            }
         }
      }

      return 0L;
   }

   public static String process(Slot var0) {
      if (!var0.hasStack()) {
         return null;
      }

      ItemStack var1 = var0.getStack();
      LoreComponent var2 = (LoreComponent)var1.getComponents().get(DataComponentTypes.LORE);
      if (var2 != null) {
         for (Text var5 : var2.lines()) {
            String var6 = var5.getString();
            if (var6.contains("Продавец:")) {
               return var6.replaceFirst(".*?Продавец:\\s*", "").trim();
            }
         }
      }

      return null;
   }
}
