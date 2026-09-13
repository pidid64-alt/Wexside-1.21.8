package ru.wild.modules.player;

import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.wild.mixin.acceser.ClientPlayerInteractionManagerAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.Stopwatch;

@ModuleRegister(name = "AutoInvisible", category = ModuleCategory.Player, description = "Автоматически пьёт зелье невидимости и возвращает прошлый слот")
public class AutoInvisible extends Module {
   public final NumberSetting source = new NumberSetting("Порог до зелья (сек)", 5.0F, 1.0F, 60.0F, 1.0F, false);
   private static final long target = 1850L;
   private final Stopwatch pending = new Stopwatch();
   private boolean previous;
   private int latest = -1;

   public AutoInvisible() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (Module.client.currentScreen != null) {
            if (this.previous) {
               this.refresh();
            }
         } else if (this.previous) {
            if (Module.client.player.isUsingItem() && !this.pending.update(1850L)) {
               Module.client.options.useKey.setPressed(true);
            } else {
               this.refresh();
            }
         } else if (this.render()) {
            int var2 = this.handle(this::handle);
            if (var2 != -1) {
               int var3 = this.handle(var2);
               if (var3 != -1) {
                  this.latest = Module.client.player.getInventory().getSelectedSlot();
                  this.process(var3);
                  Module.client.options.useKey.setPressed(true);
                  this.previous = true;
                  this.pending.handle();
               }
            }
         }
      }
   }

   private void refresh() {
      Module.client.options.useKey.setPressed(false);
      if (this.latest >= 0 && this.latest < 9) {
         this.process(this.latest);
      }

      this.previous = false;
      this.latest = -1;
   }

   private boolean render() {
      StatusEffectInstance var1 = Module.client.player.getStatusEffect(StatusEffects.INVISIBILITY);
      return var1 == null || var1.getDuration() <= (int)this.source.compute() * 20;
   }

   private int handle(Predicate<ItemStack> var1) {
      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && var1.test(var3)) {
            return var2;
         }
      }

      return -1;
   }

   private int handle(int var1) {
      if (var1 >= 0 && var1 < 9) {
         return var1;
      }

      int var2 = Module.client.player.getInventory().getSelectedSlot();

      for (int var3 = 0; var3 < 9; var3++) {
         if (Module.client.player.getInventory().getStack(var3).isEmpty()) {
            var2 = var3;
            break;
         }
      }

      int var4 = var1 < 9 ? var1 + 36 : var1;
      Module.client.interactionManager.clickSlot(Module.client.player.playerScreenHandler.syncId, var4, var2, SlotActionType.SWAP, Module.client.player);
      return var2;
   }

   private boolean handle(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && var1.isOf(Items.POTION)) {
         PotionContentsComponent var2 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         }

         for (StatusEffectInstance var4 : var2.getEffects()) {
            if (var4.getEffectType().equals(StatusEffects.INVISIBILITY)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void process(int var1) {
      Module.client.player.getInventory().setSelectedSlot(var1);
      if (Module.client.interactionManager instanceof ClientPlayerInteractionManagerAccessor var2) {
         var2.invokeSyncSelectedSlot();
      }
   }

   @Override
   public void process() {
      if (this.previous || Module.client.options != null && Module.client.options.useKey.isPressed()) {
         Module.client.options.useKey.setPressed(false);
         if (this.latest >= 0 && this.latest < 9 && Module.client.player != null) {
            this.process(this.latest);
         }
      }

      this.previous = false;
      this.latest = -1;
      super.process();
   }
}
