package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.api.event.EntityVelocityEvent;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.PlayerLandingEvent;
import ru.wild.core.PlayerContextDispatcher;
import ru.wild.modules.misc.Removals;
import ru.wild.modules.movement.GrimGlide;
import ru.wild.modules.player.NoDelay;
import ru.wild.modules.visuals.SwingAnimation;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
   @Unique
   private final MinecraftClient wild$client = MinecraftClient.getInstance();

   @Shadow
   public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> var1);

   @Shadow
   @Nullable
   public abstract StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> var1);

   @Inject(method = "jump", at = @At("HEAD"))
   private void wild$jump(CallbackInfo var1) {
      if ((Object)this instanceof ClientPlayerEntity var2) {
         PlayerContextDispatcher.handle(var2, new PlayerLandingEvent());
      }
   }

   @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
   private void wild$onHasStatusEffect(RegistryEntry<StatusEffect> var1, CallbackInfoReturnable<Boolean> var2) {
      if ((Object)this instanceof ClientPlayerEntity && Removals.handle(var1)) {
         var2.setReturnValue(false);
      }
   }

   @Inject(method = "getStatusEffect", at = @At("HEAD"), cancellable = true)
   private void wild$onGetStatusEffect(RegistryEntry<StatusEffect> var1, CallbackInfoReturnable<StatusEffectInstance> var2) {
      if ((Object)this instanceof ClientPlayerEntity && Removals.handle(var1)) {
         var2.setReturnValue(null);
      }
   }

   @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
   private void wild$swingProgressHook(CallbackInfoReturnable<Integer> var1) {
      if (WildClient.prepare()) {
         if ((Object)this == this.wild$client.player && WildClient.drawProfile() && WildClient.instance != null && WildClient.instance.data != null) {
            SwingAnimation var2 = WildClient.instance.data.handle(SwingAnimation.class);
            if (var2 != null && var2.enabled && !SwingAnimation.source.process("Off") && SwingAnimation.refresh()) {
               float var3 = SwingAnimation.target.compute();
               if (!(var3 <= 0.0F)) {
                  int var4 = 6;
                  LivingEntity var5 = (LivingEntity)(Object)this;
                  if (StatusEffectUtil.hasHaste(var5)) {
                     var4 = Math.max(1, var4 - (1 + StatusEffectUtil.getHasteAmplifier(var5)));
                  } else if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                     StatusEffectInstance var6 = this.getStatusEffect(StatusEffects.MINING_FATIGUE);
                     if (var6 != null) {
                        var4 += (1 + var6.getAmplifier()) * 2;
                     }
                  }

                  var1.setReturnValue(Math.max(1, (int)(var4 / var3)));
               }
            }
         }
      }
   }

   @ModifyConstant(method = "tickMovement", constant = @Constant(intValue = 10))
   private int wild$modifyJumpTicks(int var1) {
      if (!WildClient.prepare()) {
         return var1;
      }

      if ((Object)this instanceof ClientPlayerEntity && WildClient.instance != null && WildClient.instance.data != null) {
         NoDelay var2 = WildClient.instance.data.handle(NoDelay.class);
         if (var2 != null && var2.enabled && NoDelay.source.compute()) {
            return NoDelay.refresh();
         }
      }

      return var1;
   }

   @Inject(method = "calcGlidingVelocity", at = @At("RETURN"), cancellable = true)
   private void wild$onCalcGlidingVelocity(Vec3d var1, CallbackInfoReturnable<Vec3d> var2) {
      if (WildClient.prepare()) {
         if ((Object)this instanceof ClientPlayerEntity && WildClient.drawProfile() && WildClient.instance != null && WildClient.instance.data != null) {
            GrimGlide var3 = WildClient.instance.data.handle(GrimGlide.class);
            if (var3 != null && var3.enabled) {
               EntityVelocityEvent var4 = new EntityVelocityEvent(var1.multiply(0.99F, 0.98F, 0.99F));
               EventHandlerInvoker.handle(var4);
               var2.setReturnValue(var4.compute());
            }
         }
      }
   }
}
