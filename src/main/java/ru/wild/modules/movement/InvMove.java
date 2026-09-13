package ru.wild.modules.movement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.ScreenInputEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.gui.screen.ClickGuiScreen;
import ru.wild.util.player.MovementPhysics;
import ru.wild.util.player.SyntheticKeyState;

@ModuleRegister(name = "InvMove", description = "Позволяет ходить с открытым инвентарём и меню клиента", category = ModuleCategory.Movement)
public class InvMove extends Module {
   public static ModeSetting source = new ModeSetting("Режим", "Grim", "Grim", "Vanilla", "FunTime");
   public static NumberSetting target = new NumberSetting("Задержка закрытия", 100.0F, 0.0F, 300.0F, 10.0F, false);
   private final List<Packet<?>> previous = new ArrayList<>();
   public boolean pending = false;
   private boolean latest = false;
   private boolean summary = false;
   private long matrixBlend = 0L;
   private static long vectorMatch = 0L;

   public InvMove() {
      this.handle(source, target);
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (this.pending) {
         SyntheticKeyState.handle().handle("GuiMove");
      } else {
         SyntheticKeyState.handle().process("GuiMove");
      }

      if (this.latest && System.currentTimeMillis() >= this.matrixBlend) {
         this.latest = false;
         this.drawAnimation();
         this.pending = false;
      }

      if (Module.client.player != null) {
         if (source.process("Vanilla")) {
            this.submit();
         } else if (source.process("Grim")) {
            this.save();
         } else if (source.process("FunTime")) {
            if (!MovementPhysics.handle() && !this.previous.isEmpty() && Module.client.currentScreen instanceof InventoryScreen) {
               this.render();
            }

            this.unload();
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.compute() && !this.summary && Module.client.currentScreen instanceof InventoryScreen) {
         label54: {
            if (var1.resolve() instanceof CloseHandledScreenC2SPacket && source.process("FunTime")) {
               if (!this.previous.isEmpty()) {
                  break label54;
               }

               if (MovementPhysics.handle()) {
                  break label54;
               }
            }

            if (var1.resolve() instanceof ClickSlotC2SPacket var2) {
               load();
               if (!source.process("Grim") || !Module.client.player.isSprinting() && !Module.client.player.isJumping()) {
                  if (source.process("FunTime") && MovementPhysics.handle() && (!this.previous.isEmpty() || this.handle(var2))) {
                     this.previous.add(var2);
                     var1.process();
                  }
               } else {
                  this.previous.add(var2);
                  var1.process();
               }
            }

            return;
         }

         var1.process();
         this.matrixBlend = System.currentTimeMillis() + this.animate();
         this.latest = true;
         this.pending = true;
      }
   }

   @EventHandler
   public void handle(ScreenInputEvent var1) {
      if (Module.client.currentScreen instanceof InventoryScreen) {
         if (source.process("Grim")) {
            if (!Module.client.player.isSprinting()) {
               this.encodePoint();
               return;
            }

            var1.process();
            this.pending = false;
            this.matrixBlend = System.currentTimeMillis() + this.animate();
            this.latest = true;
         } else if (source.process("FunTime")) {
            if (this.previous.isEmpty() && !MovementPhysics.handle()) {
               this.encodePoint();
               this.pending = false;
               this.latest = false;
            } else {
               var1.process();
               this.matrixBlend = System.currentTimeMillis() + this.animate();
               this.latest = true;
               this.pending = true;
            }
         }
      }
   }

   private void render() {
      if (!this.previous.isEmpty()) {
         load();
         this.summary = true;

         try {
            for (Packet var2 : this.previous) {
               if (Module.client.getNetworkHandler() != null) {
                  Module.client.getNetworkHandler().sendPacket(var2);
               }
            }
         } finally {
            this.summary = false;
            this.previous.clear();
         }
      }
   }

   private void tick() {
      if (Module.client.player != null) {
         Module.client.player.closeScreen();
      }
   }

   private void drawAnimation() {
      this.render();
      this.encodePoint();
      this.tick();
   }

   private void encodePoint() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         ScreenHandler var1 = Module.client.player.currentScreenHandler;
         if (var1 != null && !var1.getCursorStack().isEmpty()) {
            int var2 = this.handle(var1);
            if (var2 != -1) {
               boolean var3 = this.summary;
               this.summary = true;

               try {
                  load();
                  Module.client.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
               } finally {
                  this.summary = var3;
               }
            }
         }
      }
   }

   private int handle(ScreenHandler var1) {
      ItemStack var2 = var1.getCursorStack();
      int var3 = -1;

      for (Slot var5 : var1.slots) {
         if (var5.inventory == Module.client.player.getInventory()) {
            ItemStack var6 = var5.getStack();
            if (var6.isEmpty()) {
               if (var3 == -1) {
                  var3 = var5.id;
               }
            } else if (ItemStack.areItemsAndComponentsEqual(var6, var2) && var6.getCount() + var2.getCount() <= var6.getMaxCount()) {
               return var5.id;
            }
         }
      }

      return var3;
   }

   private long animate() {
      return (long)target.compute();
   }

   private boolean handle(ClickSlotC2SPacket var1) {
      return !var1.modifiedStacks().isEmpty();
   }

   private static void load() {
      vectorMatch = System.currentTimeMillis();
   }

   public static boolean refresh() {
      return Module.client.currentScreen instanceof InventoryScreen && System.currentTimeMillis() - vectorMatch < 350L;
   }

   private void save() {
      KeyBinding[] var1 = new KeyBinding[]{
         Module.client.options.forwardKey,
         Module.client.options.backKey,
         Module.client.options.leftKey,
         Module.client.options.rightKey,
         Module.client.options.jumpKey,
         Module.client.options.sprintKey
      };
      if (this.fetch()) {
         this.pending = false;
         this.handle(var1);
      } else if (this.latest) {
         this.pending = true;
      } else {
         if (!(Module.client.currentScreen instanceof InventoryScreen)) {
            this.pending = false;
         }

         if (Module.client.currentScreen instanceof InventoryScreen) {
            this.handle(var1);
         }
      }
   }

   private void submit() {
      if (!(Module.client.currentScreen instanceof InventoryScreen) && !this.fetch()) {
         this.pending = false;
      }

      KeyBinding[] var1 = new KeyBinding[]{
         Module.client.options.forwardKey,
         Module.client.options.backKey,
         Module.client.options.leftKey,
         Module.client.options.rightKey,
         Module.client.options.jumpKey,
         Module.client.options.sprintKey
      };
      if (Module.client.currentScreen instanceof InventoryScreen || this.fetch()) {
         this.pending = false;
         this.handle(var1);
      }
   }

   private void unload() {
      KeyBinding[] var1 = new KeyBinding[]{
         Module.client.options.forwardKey,
         Module.client.options.backKey,
         Module.client.options.leftKey,
         Module.client.options.rightKey,
         Module.client.options.jumpKey,
         Module.client.options.sprintKey
      };
      if (this.fetch()) {
         this.pending = false;
         this.handle(var1);
      } else if (this.latest) {
         this.pending = true;
      } else {
         if (!(Module.client.currentScreen instanceof InventoryScreen)) {
            this.pending = false;
         }

         if (Module.client.currentScreen instanceof InventoryScreen) {
            this.handle(var1);
         }
      }
   }

   private boolean fetch() {
      return Module.client.currentScreen instanceof ClickGuiScreen || Module.client.currentScreen instanceof ClickGuiModernScreen;
   }

   private void handle(KeyBinding[] var1) {
      long var2 = MinecraftClient.getInstance().getWindow().getHandle();
      ClickGuiModernScreen var4 = Module.client.currentScreen instanceof ClickGuiModernScreen ? (ClickGuiModernScreen)Module.client.currentScreen : null;
      boolean var5 = var4 != null && var4.handle().update();

      for (KeyBinding var9 : var1) {
         if (var5) {
            var9.setPressed(false);
         } else {
            int var10 = var9.getDefaultKey().getCode();
            boolean var11 = InputUtil.isKeyPressed(var2, var10);
            var9.setPressed(var11);
         }
      }
   }

   @Override
   public void process() {
      this.pending = false;
      this.latest = false;
      this.summary = false;
      SyntheticKeyState.handle().process("GuiMove");
      this.previous.clear();
      super.process();
   }
}
