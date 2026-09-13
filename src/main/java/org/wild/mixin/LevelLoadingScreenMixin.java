package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.server.WorldGenerationProgressTracker;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.shader.ScreenBackdropRenderer;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {
   @Shadow
   @Final
   private WorldGenerationProgressTracker progressProvider;
   @Shadow
   private long lastNarrationTime;

   protected LevelLoadingScreenMixin(Text var1) {
      super(var1);
   }

   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   private void wild$renderPremiumLevelLoading(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      MinecraftClient var6 = MinecraftClient.getInstance();
      LevelLoadingScreen var7 = (LevelLoadingScreen)(Object)this;
      if (var6 == null) {
         ScreenRenderDiagnostics.handle("LevelLoadingScreen.render", var7, "client missing", null);
      } else if (!ScreenBackdropRenderer.handle().handle(var6, var2, var3, 1.0F, var7)) {
         ScreenRenderDiagnostics.handle(var7, "render.vanilla-fallback", "backdrop unavailable");
      } else {
         long var8 = Util.getMeasuringTimeMs();
         if (var8 - this.lastNarrationTime > 2000L) {
            this.lastNarrationTime = var8;
            this.narrateScreenIfNarrationEnabled(true);
         }

         ScreenBackdropRenderer.handle().handle(var6, this.progressProvider);
         ScreenRenderDiagnostics.handle(var7, "render.custom", "level-loading overlay");
         var5.cancel();
      }
   }
}
