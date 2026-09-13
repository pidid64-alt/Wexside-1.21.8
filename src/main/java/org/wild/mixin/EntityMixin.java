package org.wild.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.core.manager.FriendManager;
import ru.wild.modules.combat.HitBox;
import ru.wild.modules.player.NoPush;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
   @Inject(method = "getTargetingMargin", at = @At("RETURN"), cancellable = true)
   private void client$getTargetingMargin(CallbackInfoReturnable<Float> var1) {
      if (WildClient.prepare()) {
         Entity var2 = (Entity)(Object)this;
         if (var2 instanceof PlayerEntity) {
            HitBox var3 = (HitBox)WildClient.instance.data.process(HitBox.class);
            if (var3 != null && var3.enabled) {
               if (HitBox.source.process("Обычный")) {
                  if (!(HitBox.pending.compute() && var2 instanceof PlayerEntity var4) || !FriendManager.handle(var4.getName().getString())) {
                     float var6 = (Float)var1.getReturnValue();
                     float var5 = HitBox.target.compute();
                     var1.setReturnValue(var6 + var5);
                  }
               }
            }
         }
      }
   }

   @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
   private void onPushAwayFrom(Entity var1, CallbackInfo var2) {
      if (WildClient.prepare()) {
         Entity var3 = (Entity)(Object)this;
         if (var3 instanceof ClientPlayerEntity) {
            if (WildClient.instance != null && WildClient.instance.data != null) {
               NoPush var4 = (NoPush)WildClient.instance.data.process(NoPush.class);
               if (var4 != null && var4.enabled) {
                  if (var1 instanceof PlayerEntity && var4.source.compute()) {
                     var2.cancel();
                  } else if (var1 instanceof LivingEntity && !(var1 instanceof PlayerEntity) && var4.target.compute()) {
                     var2.cancel();
                  }
               }
            }
         }
      }
   }
}
