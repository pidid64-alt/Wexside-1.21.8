package org.wild.mixin.acceser;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerAccessor {
   @Accessor("blockBreakingCooldown")
   void setBlockBreakingCooldown(int var1);

   @Accessor("blockBreakingCooldown")
   int getBlockBreakingCooldown();

   @Invoker("syncSelectedSlot")
   void invokeSyncSelectedSlot();
}
