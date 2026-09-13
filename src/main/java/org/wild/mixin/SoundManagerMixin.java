package org.wild.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem.PlayResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.misc.Removals;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
   @Inject(
      method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void wild$muteSound(SoundInstance var1, CallbackInfoReturnable<PlayResult> var2) {
      if (var1 != null) {
         if (Removals.handle(var1.getId())) {
            var2.setReturnValue(PlayResult.NOT_STARTED);
         }
      }
   }
}
