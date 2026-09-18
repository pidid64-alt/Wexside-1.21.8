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
import ru.wild.WildClient;
import ru.wild.modules.misc.UnHook;
import ru.wild.render.ScreenRenderDiagnostics;

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
      if (!WildClient.prepare() || UnHook.target) {
         return;
      }

      MinecraftClient var6 = MinecraftClient.getInstance();
      ConnectScreen var7 = (ConnectScreen)(Object)this;
      if (var6 == null) {
         ScreenRenderDiagnostics.handle("ConnectScreen.render", var7, "client missing", null);
      } else {
         // In 1.21.8 DrawContext submits GUI work for a later render pass.
         // Raw GL here draws into an unrelated FBO and can be cleared before
         // presentation. Queue the backdrop and status with the widgets instead.
         var1.fillGradient(0, 0, this.width, this.height, -16447732, -15658213);
         var1.drawCenteredTextWithShadow(var6.textRenderer, this.status, this.width / 2, this.height / 2 - 50, 0xFFFFFFFF);
         ScreenRenderDiagnostics.handle(var7, "render.queued", "connect-status overlay");

         long var10 = Util.getMeasuringTimeMs();
         if (var10 - this.lastNarrationTime > 2000L && var6.getNarratorManager() != null) {
            this.lastNarrationTime = var10;
            var6.getNarratorManager().narrateSystemImmediately(Text.translatable("narrator.joining"));
         }

         super.render(var1, var2, var3, var4);
         var5.cancel();
      }
   }
}
