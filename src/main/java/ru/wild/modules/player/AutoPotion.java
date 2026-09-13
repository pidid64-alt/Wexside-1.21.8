package ru.wild.modules.player;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.wild.mixin.acceser.ClientPlayerInteractionManagerAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.core.ViewRotationCoordinator;

@ModuleRegister(name = "AutoPotion", category = ModuleCategory.Player, description = "Автоматически кидает под вас взрывные зелья")
public class AutoPotion extends Module {
   public static AutoPotion source;
   public static boolean target = false;
   public final ChoiceSetting pending = new ChoiceSetting(
      "Что бафать: ", new BooleanSetting("Сила", false), new BooleanSetting("Скорость", false), new BooleanSetting("Огнестойкость", false)
   );
   public final BooleanSetting previous = new BooleanSetting("Кидать смотря вниз", false);
   private final BooleanSetting latest = new BooleanSetting("Только в PVP", false);
   private final Queue<Integer> summary = new ArrayDeque<>();
   private int matrixBlend = -1;
   private boolean vectorMatch = false;
   private int itemProject = 0;

   public AutoPotion() {
      source = this;
      this.handle(this.pending, this.previous, this.latest);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (this.summary.isEmpty()) {
            if (target) {
               this.tick();
            }

            if (this.itemProject > 0) {
               this.itemProject--;
            } else if (!this.latest.compute() || this.render()) {
               if (!this.previous.compute() || !(Module.client.player.getPitch() < 80.0F)) {
                  this.refresh();
               }
            }
         } else {
            if (!target) {
               target = true;
               if (!this.previous.compute()) {
                  ViewRotationCoordinator.context = Module.client.player.getYaw();
                  ViewRotationCoordinator.config = Module.client.player.getPitch();
                  ViewRotationCoordinator.instance = true;
               }

               if (this.matrixBlend == -1) {
                  this.matrixBlend = Module.client.player.getInventory().getSelectedSlot();
               }
            }

            Module.client.options.sprintKey.setPressed(false);
            Module.client.player.setSprinting(false);
            if (!this.previous.compute()) {
               Module.client.player.setPitch(90.0F);
            }

            int var2 = this.summary.poll();
            if (var2 < 9) {
               Module.client.player.getInventory().setSelectedSlot(var2);
               ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
            } else {
               this.vectorMatch = true;
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, var2, this.matrixBlend, SlotActionType.SWAP, Module.client.player);
            }

            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            if (var2 < 9) {
               Module.client.player.getInventory().setSelectedSlot(this.matrixBlend);
               ((ClientPlayerInteractionManagerAccessor)Module.client.interactionManager).invokeSyncSelectedSlot();
            } else {
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, var2, this.matrixBlend, SlotActionType.SWAP, Module.client.player);
            }

            if (this.summary.isEmpty()) {
               if (this.vectorMatch) {
                  Module.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
                  this.vectorMatch = false;
               }

               this.itemProject = 1;
               this.tick();
            }
         }
      }
   }

   private void refresh() {
      boolean var1 = this.pending.process("Сила") && !Module.client.player.hasStatusEffect(StatusEffects.STRENGTH);
      boolean var2 = this.pending.process("Скорость") && !Module.client.player.hasStatusEffect(StatusEffects.SPEED);
      boolean var3 = this.pending.process("Огнестойкость") && !Module.client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
      if (var1 || var2 || var3) {
         for (int var4 = 0; var4 < 36; var4++) {
            ItemStack var5 = Module.client.player.getInventory().getStack(var4);
            if (!var5.isEmpty() && var5.getItem() == Items.SPLASH_POTION) {
               PotionContentsComponent var6 = (PotionContentsComponent)var5.get(DataComponentTypes.POTION_CONTENTS);
               if (var6 != null) {
                  for (StatusEffectInstance var8 : var6.getEffects()) {
                     RegistryEntry var9 = var8.getEffectType();
                     if (var1 && var9.equals(StatusEffects.STRENGTH)) {
                        this.summary.add(var4);
                        var1 = false;
                        break;
                     }

                     if (var2 && var9.equals(StatusEffects.SPEED)) {
                        this.summary.add(var4);
                        var2 = false;
                        break;
                     }

                     if (var3 && var9.equals(StatusEffects.FIRE_RESISTANCE)) {
                        this.summary.add(var4);
                        var3 = false;
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean render() {
      for (PlayerEntity var2 : Module.client.world.getPlayers()) {
         if (var2 != Module.client.player && Module.client.player.squaredDistanceTo(var2) <= 225.0) {
            return true;
         }
      }

      return false;
   }

   private void tick() {
      target = false;
      this.matrixBlend = -1;
      if (!this.previous.compute()) {
         if (Module.client.player != null) {
            Module.client.player.setYaw(ViewRotationCoordinator.context);
            Module.client.player.setPitch(ViewRotationCoordinator.config);
         }

         ViewRotationCoordinator.instance = false;
      }
   }

   @Override
   public void process() {
      this.summary.clear();
      this.tick();
      super.process();
   }
}
