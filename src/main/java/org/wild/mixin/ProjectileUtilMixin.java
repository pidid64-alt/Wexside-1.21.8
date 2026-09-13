package org.wild.mixin;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.wild.WildClient;
import ru.wild.modules.misc.FriendManager;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
   @ModifyVariable(method = "raycast", at = @At("HEAD"), argsOnly = true)
   private static Predicate<Entity> litka$ignoreFriendsCollision(Predicate<Entity> var0) {
      return !WildClient.prepare() ? var0 : var1 -> {
         if (var1 instanceof PlayerEntity var2) {
            FriendManager var3 = WildClient.instance.data.handle(FriendManager.class);
            if (var3 != null && var3.enabled && FriendManager.pending.compute() && ru.wild.core.manager.FriendManager.handle(var2.getName().getString())) {
               return false;
            }
         }

         return var0 != null && var0.test(var1);
      };
   }
}
