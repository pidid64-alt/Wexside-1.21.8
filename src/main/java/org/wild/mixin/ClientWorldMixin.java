package org.wild.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.wild.modules.visuals.Stardust;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
   @ModifyReturnValue(method = "getCloudsColor(F)I", at = @At("RETURN"))
   private int wild$modifyStardustCloudColor(int var1, float var2) {
      return Stardust.handle(var1);
   }
}
