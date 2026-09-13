package org.wild.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.modules.combat.AttackAura;
import ru.wild.modules.player.FakePlayer;
import ru.wild.modules.player.NoInteract;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
   @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
   private void noInteract(ClientPlayerEntity var1, Hand var2, BlockHitResult var3, CallbackInfoReturnable<ActionResult> var4) {
      if (WildClient.prepare()) {
         if (var1 != null) {
            NoInteract var5 = (NoInteract)WildClient.instance.data.process(NoInteract.class);
            if (var5 != null && var5.enabled) {
               if (AttackAura.textureRun == null) {
                  ClientWorld var6 = var1.clientWorld;
                  if (var6 != null) {
                     Block var7 = var6.getBlockState(var3.getBlockPos()).getBlock();
                     if (NoInteract.refresh().contains(var7)) {
                        var4.setReturnValue(ActionResult.FAIL);
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
   private void noInteractEntity(PlayerEntity var1, Entity var2, Hand var3, CallbackInfoReturnable<ActionResult> var4) {
      if (WildClient.prepare()) {
         if (FakePlayer.process(var2)) {
            var4.setReturnValue(ActionResult.SUCCESS);
         } else if (var2 instanceof ArmorStandEntity) {
            NoInteract var5 = (NoInteract)WildClient.instance.data.process(NoInteract.class);
            if (var5 != null && var5.enabled) {
               if (NoInteract.source.handle(0)) {
                  var4.setReturnValue(ActionResult.FAIL);
               }
            }
         }
      }
   }

   @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
   private void fakePlayerInteractAtLocation(PlayerEntity var1, Entity var2, EntityHitResult var3, Hand var4, CallbackInfoReturnable<ActionResult> var5) {
      if (FakePlayer.process(var2)) {
         var5.setReturnValue(ActionResult.SUCCESS);
      }
   }
}
