package org.wild.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.SprintStateEvent;
import ru.wild.core.MinecraftContext;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements MinecraftContext {
   @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = Shift.AFTER))
   public void attackHook(CallbackInfo var1) {
      EventHandlerInvoker.handle(new SprintStateEvent());
   }
}
