package org.wild.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.misc.Removals;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
   @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
   private <E extends Entity> void litka$skipEntities(E var1, Frustum var2, double var3, double var5, double var7, CallbackInfoReturnable<Boolean> var9) {
      if (Removals.process("Стойки брони") && var1 instanceof ArmorStandEntity) {
         var9.setReturnValue(false);
      } else if (!Removals.process("Рамки") || !(var1 instanceof ItemFrameEntity) && !(var1 instanceof GlowItemFrameEntity)) {
         if (Removals.process("Картины") && var1 instanceof PaintingEntity) {
            var9.setReturnValue(false);
         } else if (Removals.process("Дроп предметов") && var1 instanceof ItemEntity) {
            var9.setReturnValue(false);
         } else {
            if (Removals.process("Опыт-орбы") && var1 instanceof ExperienceOrbEntity) {
               var9.setReturnValue(false);
            }
         }
      } else {
         if (Removals.optionAdvance.compute()) {
            ItemStack var10 = ((ItemFrameEntity)var1).getHeldItemStack();
            if (var10 != null && var10.isOf(Items.FILLED_MAP)) {
               return;
            }
         }

         var9.setReturnValue(false);
      }
   }
}
