package org.wild.mixin.acceser;

import java.util.Map;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCooldownManager.class)
public interface ItemCooldownManagerAccessor {
   @Accessor("entries")
   Map<Identifier, ?> wild$getEntries();

   @Accessor("tick")
   int wild$getTick();
}
