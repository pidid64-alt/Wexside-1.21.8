package org.wild.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.modules.visuals.Animations;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
   @Unique
   private boolean litka$inventoryScaled;

   @Inject(method = "render", at = @At("HEAD"))
   private void litka$preInventoryRender(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         this.litka$inventoryScaled = false;
         if (WildClient.instance != null && WildClient.instance.data != null) {
            Animations var6 = WildClient.instance.data.handle(Animations.class);
            Screen var7 = (Screen)(Object)this;
            if (var6 != null && var6.handle(var7)) {
               float var8 = var6.process(var7);
               float var9 = var1.getScaledWindowWidth() / 2.0F;
               float var10 = var1.getScaledWindowHeight() / 2.0F;
               var1.getMatrices().pushMatrix();
               var1.getMatrices().translate(var9, var10);
               var1.getMatrices().scale(var8, var8);
               var1.getMatrices().translate(-var9, -var10);
               this.litka$inventoryScaled = true;
            }
         }
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void litka$postInventoryRender(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.litka$inventoryScaled) {
         var1.getMatrices().popMatrix();
         this.litka$inventoryScaled = false;
      }
   }
}
