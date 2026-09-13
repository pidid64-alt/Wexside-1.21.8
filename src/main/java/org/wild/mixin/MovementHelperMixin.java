package org.wild.mixin;

import baritone.api.BaritoneAPI;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.precompute.Ternary;
import java.util.List;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.AzaleaBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MovementHelper.class)
public interface MovementHelperMixin {
   @Overwrite
   static Ternary a(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof AirBlock || var1 instanceof TrapdoorBlock) {
         return Ternary.a;
      }

      if (var1 instanceof AbstractFireBlock
         || var1 == Blocks.TRIPWIRE
         || var1 == Blocks.COBWEB
         || var1 == Blocks.END_PORTAL
         || var1 == Blocks.COCOA
         || var1 instanceof AbstractSkullBlock
         || var1 == Blocks.BUBBLE_COLUMN
         || var1 instanceof ShulkerBoxBlock
         || var1 instanceof SlabBlock
         || var1 == Blocks.HONEY_BLOCK
         || var1 == Blocks.END_ROD
         || var1 == Blocks.SWEET_BERRY_BUSH
         || var1 == Blocks.POINTED_DRIPSTONE
         || var1 instanceof AmethystClusterBlock
         || var1 instanceof AzaleaBlock) {
         return Ternary.c;
      }

      if (var1 == Blocks.BIG_DRIPLEAF) {
         return Ternary.c;
      }

      if (var1 == Blocks.POWDER_SNOW) {
         return Ternary.c;
      }

      if (((List)BaritoneAPI.getSettings().blocksToAvoid.value).contains(var1)) {
         return Ternary.c;
      }

      if (!(var1 instanceof DoorBlock) && !(var1 instanceof FenceGateBlock)) {
         if (var1 instanceof CarpetBlock) {
            return Ternary.b;
         } else if (var1 instanceof SnowBlock) {
            return Ternary.b;
         } else {
            FluidState var2 = var0.getFluidState();
            if (!var2.isEmpty()) {
               return var2.getFluid().getLevel(var2) != 8 ? Ternary.c : Ternary.b;
            } else if (var1 instanceof CauldronBlock) {
               return Ternary.c;
            } else {
               return var0.canPathfindThrough(NavigationType.LAND) ? Ternary.a : Ternary.c;
            }
         }
      } else {
         return var1 == Blocks.IRON_DOOR ? Ternary.c : Ternary.a;
      }
   }
}
