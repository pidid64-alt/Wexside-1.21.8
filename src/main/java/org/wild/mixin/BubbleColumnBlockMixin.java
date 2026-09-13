package org.wild.mixin;

import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.wild.modules.misc.Removals;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
   @Redirect(
      method = "randomDisplayTick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addImportantParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V")
   )
   private void litka$noSoulSandBubbles(World var1, ParticleEffect var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      if (!this.shouldSkip(var2)) {
         var1.addImportantParticleClient(var2, var3, var5, var7, var9, var11, var13);
      }
   }

   private boolean shouldSkip(ParticleEffect var1) {
      return var1 != ParticleTypes.BUBBLE_COLUMN_UP ? false : Removals.handle(var1);
   }
}
