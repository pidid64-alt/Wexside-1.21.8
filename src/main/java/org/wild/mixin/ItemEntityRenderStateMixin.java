package org.wild.mixin;

import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.wild.render.ItemGroundStateCarrier;

@Mixin(ItemEntityRenderState.class)
public abstract class ItemEntityRenderStateMixin implements ItemGroundStateCarrier {
   @Unique
   private boolean wild$itemPhysicOnGround;

   @Override
   public void wild$setItemPhysicOnGround(boolean var1) {
      this.wild$itemPhysicOnGround = var1;
   }

   @Override
   public boolean wild$isItemPhysicOnGround() {
      return this.wild$itemPhysicOnGround;
   }
}
