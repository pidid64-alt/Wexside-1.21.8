package org.wild.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.visuals.ProtectInfo;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
   @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
   private void litka$maskTabName(PlayerListEntry var1, CallbackInfoReturnable<Text> var2) {
      Text var3 = (Text)var2.getReturnValue();
      if (var3 != null) {
         var2.setReturnValue(ProtectInfo.handle(var3));
      }
   }
}
