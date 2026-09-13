package org.wild.mixin;

import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.modules.misc.Removals;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {
   private static final Set<Block> GRASS_BLOCKS = Set.of(
      Blocks.SHORT_GRASS,
      Blocks.TALL_GRASS,
      Blocks.FERN,
      Blocks.LARGE_FERN,
      Blocks.SHORT_DRY_GRASS,
      Blocks.TALL_DRY_GRASS,
      Blocks.SEAGRASS,
      Blocks.TALL_SEAGRASS,
      Blocks.POTTED_FERN
   );
   private static final Set<Block> PLANT_BLOCKS = Set.of(
      Blocks.DANDELION,
      Blocks.POPPY,
      Blocks.BLUE_ORCHID,
      Blocks.ALLIUM,
      Blocks.AZURE_BLUET,
      Blocks.RED_TULIP,
      Blocks.ORANGE_TULIP,
      Blocks.WHITE_TULIP,
      Blocks.PINK_TULIP,
      Blocks.OXEYE_DAISY,
      Blocks.CORNFLOWER,
      Blocks.LILY_OF_THE_VALLEY,
      Blocks.WITHER_ROSE,
      Blocks.SUNFLOWER,
      Blocks.LILAC,
      Blocks.ROSE_BUSH,
      Blocks.PEONY,
      Blocks.LILY_PAD,
      Blocks.BROWN_MUSHROOM,
      Blocks.RED_MUSHROOM,
      Blocks.POTTED_DANDELION,
      Blocks.POTTED_POPPY,
      Blocks.POTTED_BLUE_ORCHID,
      Blocks.POTTED_ALLIUM,
      Blocks.POTTED_AZURE_BLUET,
      Blocks.POTTED_RED_TULIP,
      Blocks.POTTED_ORANGE_TULIP,
      Blocks.POTTED_WHITE_TULIP,
      Blocks.POTTED_PINK_TULIP,
      Blocks.POTTED_OXEYE_DAISY,
      Blocks.POTTED_CORNFLOWER,
      Blocks.POTTED_LILY_OF_THE_VALLEY,
      Blocks.POTTED_WITHER_ROSE,
      Blocks.POTTED_RED_MUSHROOM,
      Blocks.POTTED_BROWN_MUSHROOM,
      Blocks.POTTED_CACTUS,
      Blocks.SUGAR_CANE,
      Blocks.CACTUS,
      Blocks.CACTUS_FLOWER,
      Blocks.BAMBOO,
      Blocks.BAMBOO_SAPLING,
      Blocks.POTTED_BAMBOO,
      Blocks.KELP,
      Blocks.KELP_PLANT,
      Blocks.VINE,
      Blocks.WEEPING_VINES,
      Blocks.WEEPING_VINES_PLANT,
      Blocks.TWISTING_VINES,
      Blocks.TWISTING_VINES_PLANT,
      Blocks.CAVE_VINES,
      Blocks.CAVE_VINES_PLANT,
      Blocks.SWEET_BERRY_BUSH,
      Blocks.NETHER_WART,
      Blocks.WHEAT,
      Blocks.CARROTS,
      Blocks.POTATOES,
      Blocks.BEETROOTS
   );
   private static final Set<Block> SNOW_BLOCKS = Set.of(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW);

   @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
   private void litka$skipHeavyBlocks(
      BlockState var1, BlockPos var2, BlockRenderView var3, MatrixStack var4, VertexConsumer var5, boolean var6, List<BlockModelPart> var7, CallbackInfo var8
   ) {
      Block var9 = var1.getBlock();
      if (Removals.process("Трава") && GRASS_BLOCKS.contains(var9)) {
         var8.cancel();
      } else if (Removals.process("Растения и цветы") && PLANT_BLOCKS.contains(var9)) {
         var8.cancel();
      } else if (Removals.process("Листва") && var9 instanceof LeavesBlock) {
         var8.cancel();
      } else {
         if (Removals.process("Снег (покров)") && SNOW_BLOCKS.contains(var9)) {
            var8.cancel();
         }
      }
   }
}
