package ru.wild.modules.movement;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.util.math.ResettableTimer;
import ru.wild.util.player.LocalhostHelper;
import ru.wild.util.player.MovementPhysics;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "WaterSpeed", category = ModuleCategory.Movement, description = "Ускорение в воде!")
public class WaterSpeed extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "HVH", "HVH");
   private final ResettableTimer pending = new ResettableTimer();
   private final ResettableTimer previous = new ResettableTimer();
   private boolean latest = false;
   private boolean summary = false;
   float target;

   public WaterSpeed() {
      this.handle(this.source);
   }

   private boolean refresh() {
      BlockPos var1 = Module.client.player.getBlockPos();
      BlockPos var2 = var1.up(1);
      BlockPos var3 = var1.up(2);
      boolean var4 = Module.client.world.getBlockState(var2).getBlock() == Blocks.ICE || Module.client.world.getBlockState(var3).getBlock() == Blocks.ICE;
      boolean var5 = LocalhostHelper.handle(Blocks.ICE, var1, 1.0F, 1.0F);
      return var4 || var5;
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (MovementPhysics.handle()) {
            this.pending.handle();
         }

         if (this.source.process("FunTime") && Module.client.player.isTouchingWater()) {
            boolean var2 = Module.client.options.forwardKey.isPressed();
            RegistryEntry var3 = (RegistryEntry)Module.client.world
               .getRegistryManager()
               .getOrThrow(RegistryKeys.ENCHANTMENT)
               .getOptional(Enchantments.DEPTH_STRIDER)
               .orElseThrow();
            int var4 = EnchantmentHelper.getEquipmentLevel(var3, Module.client.player);
            boolean var5 = var4 >= 3;
            ItemStack var6 = Module.client.player.getOffHandStack();
            boolean var7 = !var6.isEmpty() && var6.getItem() == Items.PLAYER_HEAD;
            boolean var8 = false;
            if (var7) {
               AttributeModifiersComponent var9 = (AttributeModifiersComponent)var6.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
               if (var9 != null) {
                  var8 = var9.modifiers().stream().anyMatch(var0 -> {
                     boolean var1x = var0.slot() == AttributeModifierSlot.OFFHAND || var0.slot() == AttributeModifierSlot.ANY;
                     boolean var2x = var0.attribute() == EntityAttributes.MOVEMENT_SPEED;
                     EntityAttributeModifier var3x = var0.modifier();
                     boolean var4x = var3x.operation() == Operation.ADD_MULTIPLIED_TOTAL || var3x.operation() == Operation.ADD_MULTIPLIED_BASE;
                     return var1x && var2x && var4x && var3x.value() >= 0.14 && var3x.value() <= 0.16;
                  });
               }
            }

            boolean var12 = this.refresh();
            if (var12 && !this.latest && !this.summary) {
               this.latest = true;
               this.summary = true;
               this.previous.handle();
            }

            if (!this.latest || !this.summary || !this.previous.process(3000.0)) {
               this.target = 1.0481F;
            } else if (var5 && var12) {
               this.target = 1.175F;
            } else {
               this.target = 1.04839F;
            }

            if (!var12) {
               this.latest = false;
               this.summary = false;
            }

            if (var2) {
               Vec3d var10 = Module.client.player.getVelocity();
               Module.client.player.setVelocity(var10.x * this.target, var10.y, var10.z * this.target);
            }
         }
      }
   }
}
