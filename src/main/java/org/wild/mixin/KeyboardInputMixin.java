package org.wild.mixin;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.core.EventDispatchBoundary;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
   @Unique
   private MovementInputEvent inputEvent;

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;<init>(FF)V", shift = Shift.BEFORE))
   private void onTickBeforeMovementVector(CallbackInfo var1) {
      EventDispatchBoundary.handle();
      KeyboardInput var2 = (KeyboardInput)(Object)this;
      float var3 = getMovementMultiplier(var2.playerInput.forward(), var2.playerInput.backward());
      float var4 = getMovementMultiplier(var2.playerInput.left(), var2.playerInput.right());
      this.inputEvent = new MovementInputEvent(var3, var4, var2.playerInput.jump(), var2.playerInput.sneak(), var2.playerInput.sprint(), 0.3);
      EventHandlerInvoker.handle(this.inputEvent);
   }

   @Redirect(method = "tick", at = @At(value = "NEW", target = "Lnet/minecraft/util/math/Vec2f;"))
   private Vec2f redirectVec2fCreation(float var1, float var2) {
      return this.inputEvent != null ? new Vec2f(this.inputEvent.resolve(), this.inputEvent.compute()).normalize() : new Vec2f(var1, var2).normalize();
   }

   @Inject(
      method = "tick",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/client/input/KeyboardInput;playerInput:Lnet/minecraft/util/PlayerInput;",
         opcode = 181,
         shift = Shift.AFTER
      )
   )
   private void onTickAfterPlayerInput(CallbackInfo var1) {
      if (this.inputEvent != null) {
         KeyboardInput var2 = (KeyboardInput)(Object)this;
         PlayerInput var3 = var2.playerInput;
         PlayerInput var4 = new PlayerInput(
            var3.forward(), var3.backward(), var3.left(), var3.right(), this.inputEvent.update(), this.inputEvent.apply(), this.inputEvent.execute()
         );
         var2.playerInput = var4;
      }
   }

   @Inject(method = "tick", at = @At("RETURN"))
   private void onTickReturn(CallbackInfo var1) {
      this.inputEvent = null;
   }

   @Unique
   private static float getMovementMultiplier(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0.0F;
      } else {
         return var0 ? 1.0F : -1.0F;
      }
   }
}
