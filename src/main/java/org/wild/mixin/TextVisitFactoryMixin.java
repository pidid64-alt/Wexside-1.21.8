package org.wild.mixin;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextVisitFactory.class)
public class TextVisitFactoryMixin {
   @ModifyVariable(
      method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private static String wild$protectFormattedText(String var0) {
      return var0;
   }

   @ModifyVariable(
      method = "visitForwards(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private static String wild$protectForwardText(String var0) {
      return var0;
   }

   @ModifyVariable(
      method = "visitBackwards(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private static String wild$protectBackwardText(String var0) {
      return var0;
   }
}
