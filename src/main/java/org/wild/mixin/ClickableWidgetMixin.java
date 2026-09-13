package org.wild.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.modules.visuals.Animations;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
   @Unique
   private SpringAnimation litka$buttonMotion;
   @Unique
   private boolean litka$buttonScaled;

   @Shadow
   public abstract int getX();

   @Shadow
   public abstract int getY();

   @Shadow
   public abstract int getWidth();

   @Shadow
   public abstract int getHeight();

   @Shadow
   public abstract boolean isHovered();

   @Inject(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;renderWidget(Lnet/minecraft/client/gui/DrawContext;IIF)V")
   )
   private void litka$preRenderWidget(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         this.litka$buttonScaled = false;
         if (WildClient.instance != null && WildClient.instance.data != null) {
            Animations var6 = WildClient.instance.data.handle(Animations.class);
            if (var6 != null && var6.enabled && var6.source.process("Кнопки")) {
               if (this.litka$buttonMotion == null) {
                  this.litka$buttonMotion = new SpringAnimation(1.0F);
               }

               float var7 = this.litka$buttonMotion.handle(this.isHovered() ? 1.03F : 1.0F, this.litka$buttonSpring(var6));
               float var8 = this.getX() + this.getWidth() * 0.5F;
               float var9 = this.getY() + this.getHeight() * 0.5F;
               var1.getMatrices().pushMatrix();
               var1.getMatrices().translate(var8, var9);
               var1.getMatrices().scale(var7, var7);
               var1.getMatrices().translate(-var8, -var9);
               this.litka$buttonScaled = true;
            }
         }
      }
   }

   @Unique
   private SpringAnimationSpec litka$buttonSpring(Animations var1) {
      SpringAnimationSpec var2 = SpringAnimationSpec.onTick();
      float var3 = var1.drawAnimation();
      return new SpringAnimationSpec(var2.load() * var3, var2.save(), var2.submit(), var2.unload());
   }

   @Inject(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/widget/ClickableWidget;renderWidget(Lnet/minecraft/client/gui/DrawContext;IIF)V",
         shift = Shift.AFTER
      )
   )
   private void litka$postRenderWidget(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.litka$buttonScaled) {
         var1.getMatrices().popMatrix();
         this.litka$buttonScaled = false;
      }
   }
}
