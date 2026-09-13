package org.wild.mixin.acceser;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
   @Invoker("getFov")
   float invokeGetFov(Camera var1, float var2, boolean var3);
}
