package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.modules.visuals.Animations;

@Mixin(PlayerListHud.class)
public class PlayerTabOverlayMixin {
   @Unique
   private boolean litka$tabScaled;

   @Inject(method = "render", at = @At("HEAD"))
   private void litka$preRenderTab(DrawContext var1, int var2, Scoreboard var3, ScoreboardObjective var4, CallbackInfo var5) {
      if (WildClient.prepare()) {
         this.litka$tabScaled = false;
         Animations var6 = WildClient.instance.data.handle(Animations.class);
         if (var6 != null && var6.enabled && var6.source.process("Таб")) {
            MinecraftClient var7 = MinecraftClient.getInstance();
            boolean var8 = var7 != null && var7.options.playerListKey.isPressed();
            float var9 = var6.resolve(var8);
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(var2 / 2.0F, 0.0F);
            var1.getMatrices().scale(var9, var9);
            var1.getMatrices().translate(-var2 / 2.0F, 0.0F);
            this.litka$tabScaled = true;
         }
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void litka$postRenderTab(DrawContext var1, int var2, Scoreboard var3, ScoreboardObjective var4, CallbackInfo var5) {
      if (this.litka$tabScaled) {
         var1.getMatrices().popMatrix();
         this.litka$tabScaled = false;
      }
   }
}
