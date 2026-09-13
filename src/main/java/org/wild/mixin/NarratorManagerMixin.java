package org.wild.mixin;

import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.modules.misc.Removals;

@Mixin(NarratorManager.class)
public class NarratorManagerMixin {
   @Inject(method = "narrateText(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
   private void wild$silenceNarrator(Text var1, CallbackInfo var2) {
      if (Removals.refresh()) {
         var2.cancel();
      }
   }
}
