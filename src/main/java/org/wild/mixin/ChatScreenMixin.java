package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.audio.ProceduralSoundSynthesizer;
import ru.wild.modules.visuals.Animations;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
   protected ChatScreenMixin(Text var1) {
      super(var1);
   }

   @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelChatScreenRenderDuringCorruption(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (ProceduralSoundSynthesizer.update()) {
         var5.cancel();
      }
   }

   @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelChatScreenBackgroundDuringCorruption(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (ProceduralSoundSynthesizer.update()) {
         var5.cancel();
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void litka$animateChat(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      Animations var6 = animations();
      if (var6 != null && var6.enabled && var6.source.process("Чат")) {
         if (var6.responseCompute == null) {
            var6.encodePoint();
         }

         if (!var6.load()) {
            var6.responseCompute.handle(1.0);
         } else {
            var6.responseCompute.handle(0.0);
         }

         float var7 = (float)var6.responseCompute.check();
         float var8 = (1.0F - var7) * 30.0F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(0.0F, var8);
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void litka$endChatAnimate(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      Animations var6 = animations();
      if (var6 != null && var6.enabled && var6.source.process("Чат")) {
         var1.getMatrices().popMatrix();
      }
   }

   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void litka$interceptEscape(int var1, int var2, int var3, CallbackInfoReturnable<Boolean> var4) {
      if (var1 == 256) {
         Animations var5 = animations();
         if (var5 != null && var5.enabled && var5.source.process("Чат")) {
            if (!var5.load()) {
               var5.animate();
               var4.setReturnValue(true);
            } else if (!var5.save()) {
               MinecraftClient var6 = MinecraftClient.getInstance();
               if (var6 != null) {
                  var5.unload();
                  var6.setScreen(null);
               }

               var4.setReturnValue(true);
            }
         }
      }
   }

   @Redirect(
      method = "keyPressed",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"),
      require = 0
   )
   private void litka$deferEnterClose(MinecraftClient var1, Screen var2) {
      Animations var3 = animations();
      if (var3 != null && var3.enabled && var3.source.process("Чат") && var2 == null) {
         if (!var3.load()) {
            var3.animate();
         }
      } else {
         var1.setScreen(var2);
      }
   }

   @Inject(method = "removed", at = @At("HEAD"))
   private void litka$onChatClose(CallbackInfo var1) {
      Animations var2 = animations();
      if (var2 != null) {
         var2.unload();
      }
   }

   private static Animations animations() {
      if (!WildClient.prepare()) {
         return null;
      } else {
         return WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(Animations.class) : null;
      }
   }
}
