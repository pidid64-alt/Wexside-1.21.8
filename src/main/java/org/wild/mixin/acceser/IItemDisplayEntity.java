package org.wild.mixin.acceser;

import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity.Data;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemDisplayEntity.class)
public interface IItemDisplayEntity {
   @Accessor("data")
   Data client$data();
}
