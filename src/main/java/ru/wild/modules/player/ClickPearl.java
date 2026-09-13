package ru.wild.modules.player;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.modules.movement.Sprint;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRegister(
   name = "ClickPearl",
   category = ModuleCategory.Player,
   description = "Зажми бинд — превью траектории (Predictions), отпусти — бросок жемчуга"
)
public class ClickPearl extends Module {
   public static KeybindSetting source = new KeybindSetting("Кнопка жемчуга", -1, true);
   public static boolean target = false;
   private static final long previous = 100L;
   private static final String latest = "MiddleClick_Pearl";
   private static final long summary = 900L;
   private static final int matrixBlend = 3;
   private static final long vectorMatch = 150L;
   private static ItemStack itemProject = ItemStack.EMPTY;
   public static boolean pending = false;
   private boolean responseCompute = false;
   private boolean providerFetch = false;
   private int profileDraw = -1;
   private int vectorPerform = -1;
   private int eventAttach = -1;
   private Item serverRead = null;
   private long positionAdvance = 0L;
   private int frameCheck = 0;
   private int moduleCollect = 0;
   private boolean providerClose = false;
   private int presetSave = 0;
   private long windowConvert = 0L;
   private long presetWrite = 0L;

   public ClickPearl() {
      this.handle(source);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.interactionManager != null && Module.client.world != null) {
         this.load();
         if (this.frameCheck > 0) {
            this.drawAnimation();
         } else {
            boolean var2 = source.compute() != -1 && KeybindSetting.process(source.compute()) && Module.client.currentScreen == null;
            if (var2 && this.render()) {
               this.responseCompute = true;
               target = true;
            } else if (this.responseCompute) {
               this.responseCompute = false;
               target = false;
               if (Module.client.currentScreen == null) {
                  this.tick();
               }
            } else {
               target = false;
            }
         }
      } else {
         target = false;
         this.submit();
      }
   }

   private boolean render() {
      int var1 = InventorySlotActions.handle(Items.ENDER_PEARL);
      if (var1 == -1) {
         return false;
      }

      ItemStack var2 = this.process(var1);
      return !var2.isEmpty() && !Module.client.player.getItemCooldownManager().isCoolingDown(var2);
   }

   private void tick() {
      if (System.currentTimeMillis() - this.positionAdvance >= 100L) {
         int var1 = InventorySlotActions.handle(Items.ENDER_PEARL);
         if (var1 != -1) {
            ItemStack var2 = this.process(var1);
            if (!var2.isEmpty() && !Module.client.player.getItemCooldownManager().isCoolingDown(var2)) {
               itemProject = var2.copy();
               this.vectorPerform = Module.client.player.getInventory().getSelectedSlot();
               this.providerClose = false;
               pending = true;
               if (this.compute(var1)) {
                  this.profileDraw = this.resolve(var1);
                  this.providerFetch = false;
               } else {
                  this.eventAttach = var1;
                  this.serverRead = Module.client.player.getInventory().getStack(this.vectorPerform).getItem();
                  this.providerFetch = true;
               }

               this.frameCheck = 1;
               this.moduleCollect = 0;
            }
         }
      }
   }

   private void drawAnimation() {
      if (this.providerFetch) {
         this.save();
      }

      if (this.moduleCollect > 0) {
         this.moduleCollect--;
      } else {
         if (!this.providerFetch) {
            switch (this.frameCheck) {
               case 1:
                  if (this.profileDraw != this.vectorPerform) {
                     this.handle(this.profileDraw);
                  }

                  this.frameCheck = 2;
                  this.moduleCollect = 0;
                  break;
               case 2:
                  this.encodePoint();
                  this.frameCheck = 3;
                  this.moduleCollect = 0;
                  break;
               case 3:
                  if (this.profileDraw != this.vectorPerform) {
                     this.handle(this.vectorPerform);
                  }

                  this.animate();
            }
         } else {
            switch (this.frameCheck) {
               case 1:
                  this.frameCheck = 2;
                  this.moduleCollect = 0;
                  break;
               case 2:
                  Module.client.interactionManager
                     .clickSlot(Module.client.player.playerScreenHandler.syncId, this.eventAttach, this.vectorPerform, SlotActionType.SWAP, Module.client.player);
                  this.frameCheck = 3;
                  this.moduleCollect = 0;
                  break;
               case 3:
                  this.encodePoint();
                  this.frameCheck = 4;
                  this.moduleCollect = 0;
                  break;
               case 4:
                  Module.client.interactionManager
                     .clickSlot(Module.client.player.playerScreenHandler.syncId, this.eventAttach, this.vectorPerform, SlotActionType.SWAP, Module.client.player);
                  this.frameCheck = 5;
                  this.moduleCollect = 1;
                  break;
               case 5:
                  if (Module.client.getNetworkHandler() != null) {
                     Module.client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
                  }

                  this.providerClose = true;
                  this.presetSave = 3;
                  this.windowConvert = System.currentTimeMillis();
                  this.presetWrite = this.windowConvert;
                  this.animate();
            }
         }
      }
   }

   private void encodePoint() {
      Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
      Module.client.player.swingHand(Hand.MAIN_HAND);
   }

   private void animate() {
      this.positionAdvance = System.currentTimeMillis();
      SyntheticKeyState.handle().process("MiddleClick_Pearl");
      this.frameCheck = 0;
      this.moduleCollect = 0;
      this.providerFetch = false;
      pending = false;
   }

   private void load() {
      if (this.providerClose) {
         if (Module.client.player != null && Module.client.interactionManager != null) {
            long var1 = System.currentTimeMillis();
            if (var1 - this.windowConvert < 900L && this.presetSave > 0 && this.serverRead != null) {
               Item var3 = Module.client.player.getInventory().getStack(this.vectorPerform).getItem();
               if (var3 != this.serverRead) {
                  if (var1 - this.presetWrite >= 150L) {
                     Module.client.interactionManager
                        .clickSlot(Module.client.player.playerScreenHandler.syncId, this.eventAttach, this.vectorPerform, SlotActionType.SWAP, Module.client.player);
                     this.presetSave--;
                     this.presetWrite = var1;
                  }
               }
            } else {
               this.providerClose = false;
            }
         } else {
            this.providerClose = false;
         }
      }
   }

   private void handle(int var1) {
      if (var1 >= 0 && var1 <= 8 && Module.client.player != null) {
         Module.client.player.getInventory().setSelectedSlot(var1);
      }
   }

   private void save() {
      Sprint.latest = 2;
      Module.client.options.sprintKey.setPressed(false);
      Module.client.player.setSprinting(false);
      SyntheticKeyState.handle().handle("MiddleClick_Pearl");
   }

   private void submit() {
      if (this.frameCheck > 0) {
         SyntheticKeyState.handle().process("MiddleClick_Pearl");
      }

      this.responseCompute = false;
      this.providerFetch = false;
      this.frameCheck = 0;
      this.moduleCollect = 0;
      this.providerClose = false;
      pending = false;
   }

   private ItemStack process(int var1) {
      if (Module.client.player == null) {
         return ItemStack.EMPTY;
      } else if (var1 >= 36 && var1 <= 44) {
         return Module.client.player.getInventory().getStack(var1 - 36);
      } else {
         return var1 >= 0 && var1 < 36 ? Module.client.player.getInventory().getStack(var1) : ItemStack.EMPTY;
      }
   }

   public static ItemStack refresh() {
      return itemProject.copy();
   }

   @Override
   public void process() {
      target = false;
      this.submit();
      super.process();
   }

   private boolean compute(int var1) {
      return var1 >= 0 && var1 <= 8 || var1 >= 36 && var1 <= 44;
   }

   private int resolve(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         return var1;
      } else {
         return var1 >= 36 && var1 <= 44 ? var1 - 36 : -1;
      }
   }
}
