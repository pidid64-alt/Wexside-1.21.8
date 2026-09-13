package ru.wild.modules.player;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRegister(name = "ElytraHelper", description = "Автоматически юзает фейерверки/свапает на элики", category = ModuleCategory.Player)
public class ElytraHelper extends Module {
   public static ElytraHelper source;
   public static KeybindSetting target = new KeybindSetting("Свап на нагрудник", -1);
   public static KeybindSetting pending = new KeybindSetting("Фейерверк", -1);
   public final BooleanSetting previous = new BooleanSetting("Свапать при КД", false);
   private int latest = 0;
   private int summary = 0;
   private int matrixBlend = -1;
   private int vectorMatch = -1;
   private boolean itemProject = false;

   public ElytraHelper() {
      this.handle(target, pending, this.previous);
      source = this;
   }

   @EventHandler
   private void handle(InputButtonEvent var1) {
      if (var1.apply() == 1 && Module.client.player != null) {
         if (var1.resolve() == target.compute()
            && this.latest == 0
            && (!Module.client.player.isUsingItem() || Module.client.player.getOffHandStack().getItem() == Items.SHIELD)) {
            ItemStack var2 = Module.client.player.getEquippedStack(EquipmentSlot.CHEST);
            int var3 = var2.getItem() == Items.ELYTRA ? this.save() : InventorySlotActions.handle(Items.ELYTRA);
            if (var3 >= 0) {
               this.matrixBlend = var3;
               this.itemProject = false;
               this.latest = 1;
               this.drawAnimation();
            }
         }

         if (var1.resolve() == pending.compute() && this.latest == 0) {
            int var4 = InventorySlotActions.handle(Items.FIREWORK_ROCKET);
            if (var4 != -1) {
               this.matrixBlend = var4;
               this.itemProject = true;
               this.latest = 1;
               this.drawAnimation();
            }
         }
      }
   }

   @EventHandler
   private void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null || Module.client.currentScreen != null) {
         this.load();
      } else if (this.latest > 0) {
         if (this.summary > 0) {
            this.summary--;
         } else {
            this.drawAnimation();
         }
      } else {
         if (this.previous.compute()) {
            boolean var2 = Module.client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
            if (var2 && NicknameUtil.process()) {
               int var3 = this.save();
               if (var3 != -1) {
                  this.matrixBlend = var3;
                  this.itemProject = false;
                  this.latest = 1;
                  this.drawAnimation();
               }
            }
         }
      }
   }

   private void drawAnimation() {
      if (this.itemProject) {
         this.encodePoint();
      } else {
         this.animate();
      }
   }

   private void encodePoint() {
      switch (this.latest) {
         case 1:
            SyntheticKeyState.handle().handle("ElytraHelper_FW");
            if (Module.client.player.isSprinting()) {
               Module.client.player.setSprinting(false);
            }

            this.latest = 2;
            this.summary = 1;
            break;
         case 2:
            this.vectorMatch = Module.client.player.getInventory().getSelectedSlot();
            if (this.matrixBlend < 9) {
               InventorySlotActions.handle(this.matrixBlend);
            } else {
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.matrixBlend, this.vectorMatch, SlotActionType.SWAP, Module.client.player);
            }

            this.latest = 3;
            this.summary = 1;
            break;
         case 3:
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.latest = 4;
            this.summary = 1;
            break;
         case 4:
            if (this.matrixBlend < 9) {
               InventorySlotActions.handle(this.vectorMatch);
            } else {
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.matrixBlend, this.vectorMatch, SlotActionType.SWAP, Module.client.player);
            }

            SyntheticKeyState.handle().process("ElytraHelper_FW");
            this.latest = 0;
      }
   }

   private void animate() {
      switch (this.latest) {
         case 1:
            SyntheticKeyState.handle().handle("ElytraHelper");
            if (Module.client.player.isSprinting()) {
               Module.client.player.setSprinting(false);
            }

            this.latest = 2;
            this.summary = 1;
            break;
         case 2:
            InventorySlotActions.handle(this.matrixBlend, 6);
            if (Module.client.getNetworkHandler() != null) {
               Module.client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
            }

            this.latest = 3;
            this.summary = 1;
            break;
         case 3:
            SyntheticKeyState.handle().process("ElytraHelper");
            this.latest = 0;
      }
   }

   private void load() {
      if (this.latest > 0) {
         SyntheticKeyState.handle().process(this.itemProject ? "ElytraHelper_FW" : "ElytraHelper");
      }

      this.latest = 0;
      this.summary = 0;
      this.matrixBlend = -1;
   }

   private int save() {
      Item[] var1 = new Item[]{
         Items.NETHERITE_CHESTPLATE,
         Items.DIAMOND_CHESTPLATE,
         Items.CHAINMAIL_CHESTPLATE,
         Items.GOLDEN_CHESTPLATE,
         Items.IRON_CHESTPLATE,
         Items.LEATHER_CHESTPLATE
      };

      for (Item var5 : var1) {
         int var6 = InventorySlotActions.handle(var5);
         if (var6 != -1) {
            return var6;
         }
      }

      return -1;
   }

   public static boolean refresh() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         if (Module.client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            return true;
         }

         int var0 = InventorySlotActions.handle(Items.ELYTRA);
         if (var0 == -1) {
            return false;
         }

         InventorySlotActions.handle(var0, 6);
         if (Module.client.getNetworkHandler() != null) {
            Module.client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean render() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         Item[] var0 = new Item[]{
            Items.NETHERITE_CHESTPLATE,
            Items.DIAMOND_CHESTPLATE,
            Items.IRON_CHESTPLATE,
            Items.CHAINMAIL_CHESTPLATE,
            Items.GOLDEN_CHESTPLATE,
            Items.LEATHER_CHESTPLATE
         };

         for (Item var4 : var0) {
            int var5 = InventorySlotActions.handle(var4);
            if (var5 != -1) {
               InventorySlotActions.handle(var5, 6);
               if (Module.client.getNetworkHandler() != null) {
                  Module.client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean tick() {
      return handle(Items.FIREWORK_ROCKET);
   }

   private static boolean handle(Item var0) {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         int var1 = InventorySlotActions.handle(var0);
         if (var1 == -1) {
            return false;
         }

         int var2 = Module.client.player.getInventory().getSelectedSlot();
         boolean var3 = var1 >= 36 && var1 <= 44;
         if (var3) {
            InventorySlotActions.handle(var1 - 36);
         } else {
            Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var1, var2, SlotActionType.SWAP, Module.client.player);
         }

         Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
         Module.client.player.swingHand(Hand.MAIN_HAND);
         if (var3) {
            InventorySlotActions.handle(var2);
         } else {
            Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var1, var2, SlotActionType.SWAP, Module.client.player);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void process() {
      this.load();
      super.process();
   }
}
