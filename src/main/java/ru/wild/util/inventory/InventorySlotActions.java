package ru.wild.util.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import ru.wild.core.MinecraftContext;

public class InventorySlotActions implements MinecraftContext {
   public static void handle(int var0) {
      if (toggleState.player != null && var0 >= 0 && var0 <= 8) {
         if (toggleState.player.getInventory().getSelectedSlot() != var0) {
            toggleState.player.getInventory().setSelectedSlot(var0);
            toggleState.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var0));
         }
      }
   }

   public static void handle(int var0, int var1) {
      if (toggleState.player != null && toggleState.interactionManager != null) {
         int var2 = toggleState.player.playerScreenHandler.syncId;
         if (var0 >= 36 && var0 <= 44) {
            toggleState.interactionManager.clickSlot(var2, var1, var0 % 9, SlotActionType.SWAP, toggleState.player);
         } else {
            int var3 = toggleState.player.getInventory().getSelectedSlot();
            toggleState.interactionManager.clickSlot(var2, var0, var3, SlotActionType.SWAP, toggleState.player);
            toggleState.interactionManager.clickSlot(var2, var1, var3, SlotActionType.SWAP, toggleState.player);
            toggleState.interactionManager.clickSlot(var2, var0, var3, SlotActionType.SWAP, toggleState.player);
         }
      }
   }

   public static int handle(Item var0) {
      if (toggleState.player == null) {
         return -1;
      }

      int var1 = -1;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = toggleState.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && var3.getItem() == var0) {
            var1 = var2;
            break;
         }
      }

      if (var1 < 9 && var1 != -1) {
         var1 += 36;
      }

      return var1;
   }

   public static int process(Item var0) {
      if (toggleState.player == null) {
         return -1;
      }

      for (int var1 = 0; var1 < 9; var1++) {
         ItemStack var2 = toggleState.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.isOf(var0)) {
            return var1;
         }
      }

      return -1;
   }

   public static int handle(Item var0, boolean var1) {
      return handle(var0, var1, false);
   }

   public static int handle(Item var0, boolean var1, boolean var2) {
      if (toggleState.player == null) {
         return -1;
      }

      int var3 = -1;
      if (var2) {
         for (int var4 = 0; var4 < 36; var4++) {
            ItemStack var5 = toggleState.player.getInventory().getStack(var4);
            if (!var5.isEmpty() && var5.getItem() == var0 && var5.hasEnchantments()) {
               var3 = var4;
               break;
            }
         }
      } else {
         for (int var6 = 0; var6 < 36; var6++) {
            ItemStack var8 = toggleState.player.getInventory().getStack(var6);
            if (!var8.isEmpty() && var8.getItem() == var0 && !var8.hasEnchantments()) {
               var3 = var6;
               break;
            }
         }

         if (var3 == -1 && !var1) {
            for (int var7 = 0; var7 < 36; var7++) {
               ItemStack var9 = toggleState.player.getInventory().getStack(var7);
               if (!var9.isEmpty() && var9.getItem() == var0) {
                  var3 = var7;
                  break;
               }
            }
         }
      }

      if (var3 < 9 && var3 != -1) {
         var3 += 36;
      }

      return var3;
   }
}
