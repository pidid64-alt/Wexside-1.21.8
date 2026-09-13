package org.wild.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {
   @Inject(method = "onSpawnPacket", at = @At("HEAD"), cancellable = true)
   private void wild$quietInvalidFishingOwner(EntitySpawnS2CPacket var1, CallbackInfo var2) {
      Entity var3 = (Entity)(Object)this;
      Entity var4 = var3.getWorld().getEntityById(var1.getEntityData());
      if (!(var4 instanceof PlayerEntity)) {
         var3.discard();
         var2.cancel();
      }
   }
}
