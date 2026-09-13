package org.wild.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.core.ProxyImportScreen;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
   protected MultiplayerScreenMixin(Text var1) {
      super(var1);
   }

   @Inject(method = "init", at = @At("RETURN"))
   private void addProxyButton(CallbackInfo var1) {
      byte var2 = 80;
      byte var3 = 20;
      int var4 = this.width - var2 - 5;
      byte var5 = 5;
      this.addDrawableChild(
         ButtonWidget.builder(Text.literal("Proxy"), var1x -> this.client.setScreen(new ProxyImportScreen(this))).dimensions(var4, var5, var2, var3).build()
      );
   }
}
