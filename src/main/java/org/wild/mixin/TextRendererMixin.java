package org.wild.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.render.texture.TextureFilterState;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
   @Inject(
      method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V",
      at = @At("HEAD")
   )
   private void wild$guardVanillaFontState(
      String var1,
      float var2,
      float var3,
      int var4,
      boolean var5,
      Matrix4f var6,
      VertexConsumerProvider var7,
      TextLayerType var8,
      int var9,
      int var10,
      CallbackInfo var11
   ) {
      TextureFilterState.handle();
   }

   @Inject(
      method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V",
      at = @At("RETURN")
   )
   private void wild$restoreVanillaFontState(
      String var1,
      float var2,
      float var3,
      int var4,
      boolean var5,
      Matrix4f var6,
      VertexConsumerProvider var7,
      TextLayerType var8,
      int var9,
      int var10,
      CallbackInfo var11
   ) {
      TextureFilterState.process();
   }

   @ModifyVariable(
      method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private String litka$maskGlobalString(String var1) {
      return var1 == null ? null : ProtectInfo.compute(var1);
   }

   @ModifyVariable(method = "getWidth(Ljava/lang/String;)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private String litka$maskWidthString(String var1) {
      return var1 == null ? null : ProtectInfo.compute(var1);
   }
}
