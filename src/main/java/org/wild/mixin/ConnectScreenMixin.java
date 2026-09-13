package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.shader.ScreenBackdropRenderer;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin extends Screen {
   @Shadow
   private Text status;
   @Shadow
   private long lastNarrationTime;

   protected ConnectScreenMixin(Text var1) {
      super(var1);
   }

   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   private void wild$renderPremiumConnect(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      MinecraftClient var6 = MinecraftClient.getInstance();
      ConnectScreen var7 = (ConnectScreen)(Object)this;
      if (var6 == null) {
         ScreenRenderDiagnostics.handle("ConnectScreen.render", var7, "client missing", null);
      } else {
         if (!ScreenBackdropRenderer.handle().handle(var6, var2, var3, 1.0F, var7)) {
            int var8 = var6.getWindow() != null ? var6.getWindow().getScaledWidth() : this.width;
            int var9 = var6.getWindow() != null ? var6.getWindow().getScaledHeight() : this.height;
            var1.fillGradient(0, 0, var8, var9, -16447732, -15658213);
            ScreenRenderDiagnostics.handle(var7, "render.safe-fallback", "backdrop unavailable");
         } else {
            ScreenRenderDiagnostics.handle(var7, "render.custom", "connect-status overlay");
         }

         long var10 = Util.getMeasuringTimeMs();
         if (var10 - this.lastNarrationTime > 2000L && var6.getNarratorManager() != null) {
            this.lastNarrationTime = var10;
            var6.getNarratorManager().narrateSystemImmediately(Text.translatable("narrator.joining"));
         }

         super.render(var1, var2, var3, var4);
         ScreenBackdropRenderer.handle().handle(var6, this.status);
         var5.cancel();
      }
   }
}
