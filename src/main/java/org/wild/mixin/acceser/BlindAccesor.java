package org.wild.mixin.acceser;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface BlindAccesor {
   @Invoker("isBlind")
   boolean invokeIsBlind();
}
