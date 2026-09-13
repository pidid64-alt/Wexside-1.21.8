package org.wild.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.modules.visuals.NameTags;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.render.EntityRenderIdAccess;
import ru.wild.render.FramebufferCapture;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<S extends EntityRenderState> {
   @Inject(method = "updateRenderState", at = @At("TAIL"))
   private void wild$attachEntityId(Entity var1, S var2, float var3, CallbackInfo var4) {
      ((EntityRenderIdAccess)var2).wild$setEntityId(var1.getId());
   }

   @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
   private void renderLabelIfPresent(S var1, Text var2, MatrixStack var3, VertexConsumerProvider var4, int var5, CallbackInfo var6) {
      if (WildClient.prepare()) {
         NameTags var7 = WildClient.instance.data.handle(NameTags.class);
         if (var7 != null && var7.handle((int)(var1.width * 100.0F))) {
            var6.cancel();
         }
      }
   }

   @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private Text litka$maskNametag(Text var1) {
      return ProtectInfo.handle(var1);
   }

   @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
   private void skipLabelDuringCapture(EntityRenderState var1, Text var2, MatrixStack var3, VertexConsumerProvider var4, int var5, CallbackInfo var6) {
      if (FramebufferCapture.handle().check()) {
         var6.cancel();
      }
   }
}
