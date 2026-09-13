package org.wild.mixin;

import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.visuals.ProtectInfo;

@Mixin(Team.class)
public class TeamMixin {
   @Inject(method = "decorateName", at = @At("RETURN"), cancellable = true)
   private void litka$maskScoreboardTeam(Text var1, CallbackInfoReturnable<MutableText> var2) {
      MutableText var3 = (MutableText)var2.getReturnValue();
      if (var3 != null) {
         var2.setReturnValue((MutableText)ProtectInfo.process(var3));
      }
   }
}
