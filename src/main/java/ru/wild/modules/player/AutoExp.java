package ru.wild.modules.player;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.wild.mixin.acceser.ClientPlayerInteractionManagerAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.modules.movement.Sprint;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(name = "AutoExp", description = "Автоматически использует пузырьки опыта", category = ModuleCategory.Player)
public class AutoExp extends Module {
   private final KeybindSetting source = new KeybindSetting("Клавиша опыта", -1, true);
   private final NumberSetting target = new NumberSetting("Задержка", 80.0F, 20.0F, 300.0F, 10.0F, false);
   private final BooleanSetting pending = new BooleanSetting("Только изношенное", false);
   private final NumberSetting previous = new NumberSetting("Прочность до", 95.0F, 5.0F, 100.0F, 5.0F, false).handle(() -> !this.pending.compute());
   private final Stopwatch latest = new Stopwatch();
   private int summary = 0;
   private int matrixBlend = 0;
   private int vectorMatch = -1;
   private int itemProject = -1;
   private Hand responseCompute = Hand.MAIN_HAND;

   public AutoExp() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player == null || Module.client.interactionManager == null || this.source.compute() == -1) {
         this.animate();
      } else if (this.summary > 0) {
         if (this.matrixBlend > 0) {
            this.load();
            this.matrixBlend--;
         } else {
            this.render();
         }
      } else if (this.submit() && this.latest.update((long)this.target.compute())) {
         if (!this.pending.compute() || this.fetch()) {
            this.refresh();
         }
      }
   }

   private void refresh() {
      this.itemProject = Module.client.player.getInventory().getSelectedSlot();
      if (Module.client.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
         this.responseCompute = Hand.OFF_HAND;
         this.summary = Module.client.currentScreen instanceof InventoryScreen ? 1 : 2;
      } else {
         int var1 = this.unload();
         if (var1 != -1) {
            this.vectorMatch = var1;
            this.responseCompute = Hand.MAIN_HAND;
            this.summary = Module.client.currentScreen instanceof InventoryScreen ? 1 : (var1 < 9 ? 2 : 4);
         }
      }
   }

   private void render() {
      if (Module.client.player != null && Module.client.interactionManager != null) {
         switch (this.summary) {
            case 1:
               Module.client.player.closeHandledScreen();
               this.summary = this.vectorMatch >= 9 ? 4 : 2;
               this.matrixBlend = 2;
               break;
            case 2:
               this.load();
               this.tick();
               this.summary = 3;
               this.matrixBlend = 1;
               break;
            case 3:
               this.drawAnimation();
               break;
            case 4:
               this.load();
               this.save();
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.vectorMatch, this.itemProject, SlotActionType.SWAP, Module.client.player);
               Module.client.player.closeHandledScreen();
               this.summary = 5;
               this.matrixBlend = 2;
               break;
            case 5:
               this.load();
               this.tick();
               this.summary = 6;
               this.matrixBlend = 2;
               break;
            case 6:
               this.load();
               this.save();
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.vectorMatch, this.itemProject, SlotActionType.SWAP, Module.client.player);
               Module.client.player.closeHandledScreen();
               this.encodePoint();
               break;
            default:
               this.animate();
         }
      } else {
         this.animate();
      }
   }

   private void tick() {
      if (this.responseCompute == Hand.MAIN_HAND && this.vectorMatch >= 0 && this.vectorMatch < 9) {
         Module.client.player.getInventory().setSelectedSlot(this.vectorMatch);
      }

      Module.client.interactionManager.interactItem(Module.client.player, this.responseCompute);
      Module.client.player.swingHand(this.responseCompute);
   }

   private void drawAnimation() {
      if (this.responseCompute == Hand.MAIN_HAND && this.vectorMatch >= 0 && this.vectorMatch < 9 && this.itemProject != this.vectorMatch) {
         Module.client.player.getInventory().setSelectedSlot(this.itemProject);
         ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
      }

      this.encodePoint();
   }

   private void encodePoint() {
      this.latest.handle();
      this.animate();
   }

   private void animate() {
      this.summary = 0;
      this.matrixBlend = 0;
      this.vectorMatch = -1;
      this.itemProject = -1;
      this.responseCompute = Hand.MAIN_HAND;
   }

   private void load() {
      Sprint.latest = 2;
      Module.client.options.sprintKey.setPressed(false);
      if (Module.client.player.isSprinting()) {
         Module.client.player.setSprinting(false);
         if (Module.client.getNetworkHandler() != null) {
            Module.client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(Module.client.player, Mode.STOP_SPRINTING));
         }
      }
   }

   private void save() {
      if (Module.client.getNetworkHandler() != null) {
         Module.client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(Module.client.player, Mode.OPEN_INVENTORY));
      }
   }

   private boolean submit() {
      if (Module.client.currentScreen == null) {
         return KeybindSetting.process(this.source.compute());
      }

      if (Module.client.currentScreen instanceof InventoryScreen && Module.client.getWindow() != null) {
         long var1 = Module.client.getWindow().getHandle();
         int var3 = this.source.compute();
         if (var3 >= 0) {
            return InputUtil.isKeyPressed(var1, var3);
         } else {
            return var3 <= -100 ? GLFW.glfwGetMouseButton(var1, -var3 - 100) == 1 : false;
         }
      } else {
         return false;
      }
   }

   private int unload() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = Module.client.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.isOf(Items.EXPERIENCE_BOTTLE)) {
            return var1;
         }
      }

      return -1;
   }

   private boolean fetch() {
      for (int var1 = 0; var1 < Module.client.player.getInventory().size(); var1++) {
         if (this.handle(Module.client.player.getInventory().getStack(var1))) {
            return true;
         }
      }

      return this.handle(Module.client.player.getOffHandStack());
   }

   private boolean handle(ItemStack var1) {
      if (!var1.isEmpty() && var1.isDamageable()) {
         int var2 = var1.getMaxDamage();
         if (var2 <= 0) {
            return false;
         }

         int var3 = var2 - var1.getDamage();
         float var4 = var3 * 100.0F / var2;
         return var4 <= this.previous.compute();
      } else {
         return false;
      }
   }

   @Override
   public void process() {
      this.animate();
      super.process();
   }
}
