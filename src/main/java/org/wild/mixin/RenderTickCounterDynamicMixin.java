package org.wild.mixin;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.modules.movement.Timer;

@Environment(EnvType.CLIENT)
@Mixin(Dynamic.class)
public class RenderTickCounterDynamicMixin {
   @Shadow
   private float dynamicDeltaTicks;
   @Shadow
   private float tickProgress;
   @Shadow
   private long lastTimeMillis;
   @Shadow
   @Final
   private float tickTime;
   @Shadow
   @Final
   private FloatUnaryOperator targetMillisPerTick;

   @Inject(method = "beginRenderTick(J)I", at = @At("HEAD"), cancellable = true)
   private void wild$timer(long var1, CallbackInfoReturnable<Integer> var3) {
      if (Timer.source != 1.0F) {
         this.dynamicDeltaTicks = (float)(var1 - this.lastTimeMillis) / this.targetMillisPerTick.apply(this.tickTime) * Timer.source;
         this.lastTimeMillis = var1;
         this.tickProgress = this.tickProgress + this.dynamicDeltaTicks;
         int var4 = (int)this.tickProgress;
         this.tickProgress -= var4;
         var3.setReturnValue(var4);
      }
   }
}
