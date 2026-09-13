package org.wild.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wild.WildClient;
import ru.wild.modules.misc.SeeInvisibles;
import ru.wild.modules.visuals.Chams;
import ru.wild.modules.visuals.ChinaHat;
import ru.wild.modules.visuals.DeadEffect;
import ru.wild.modules.visuals.NameTags;
import ru.wild.render.FramebufferCapture;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.shader.PrismaticChamsPipeline;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {
   @Shadow
   protected EntityModel<? super LivingEntityRenderState> model;
   @Unique
   private PlayerEntityRenderState wild$lastPlayerState;

   @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
   private void wild$usePrismaticChams(LivingEntityRenderState var1, boolean var2, boolean var3, boolean var4, CallbackInfoReturnable<RenderLayer> var5) {
      if (!FramebufferCapture.handle().check()) {
         Chams var6 = Chams.load();
         if (var6 != null && var6.handle(var1)) {
            var5.setReturnValue(PrismaticChamsPipeline.handle(var6));
         }
      }
   }

   @Inject(method = "shouldRenderFeatures", at = @At("HEAD"), cancellable = true)
   private void wild$skipFeaturesForPrismaticChams(LivingEntityRenderState var1, CallbackInfoReturnable<Boolean> var2) {
      if (FramebufferCapture.handle().onTick()) {
         var2.setReturnValue(false);
      } else if (!FramebufferCapture.handle().check()) {
         Chams var3 = Chams.load();
         if (var3 != null && var3.process(var1)) {
            var2.setReturnValue(false);
         }
      }
   }

   @Inject(method = "getShadowRadius(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;)F", at = @At("HEAD"), cancellable = true)
   private void wild$hidePrismaticChamsShadow(LivingEntityRenderState var1, CallbackInfoReturnable<Float> var2) {
      if (!FramebufferCapture.handle().check()) {
         Chams var3 = Chams.load();
         if (var3 != null && var3.compute(var1)) {
            var2.setReturnValue(0.0F);
         }
      }
   }

   @Redirect(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"
      )
   )
   private VertexConsumer wild$captureBaseLayer(
      VertexConsumerProvider var1, RenderLayer var2, LivingEntityRenderState var3, MatrixStack var4, VertexConsumerProvider var5, int var6
   ) {
      VertexConsumer var7 = var1.getBuffer(var2);
      Chams var8 = Chams.load();
      return var8 != null && var8.handle(var3) ? var7 : FramebufferCapture.handle().handle(var7, var2, var3);
   }

   @Redirect(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
      )
   )
   private void redirectModelRender(
      EntityModel<LivingEntityRenderState> var1,
      MatrixStack var2,
      VertexConsumer var3,
      int var4,
      int var5,
      int var6,
      LivingEntityRenderState var7,
      MatrixStack var8,
      VertexConsumerProvider var9,
      int var10
   ) {
      if (FramebufferCapture.handle().check()) {
         var1.render(var2, var3, var4, var5, var6);
         this.wild$lastPlayerState = null;
      } else {
         EntityModel var11 = var1;
         Chams var12 = Chams.load();
         if (var12 != null && var12.handle(var7)) {
            float var17 = var12.resolve(var7);
            PrismaticChamsPipeline.resolve();
            if (var12.tick()) {
               this.wild$renderChamsPass(var1, var2, var9, var7, var4, var5, var6, PrismaticChamsPipeline.compute(), 1.0F, var17);
               this.wild$renderChamsPass(var1, var2, var9, var7, var4, var5, var6, PrismaticChamsPipeline.process(), 0.0F, var17);
            } else {
               RenderLayer var18 = var12.drawAnimation() ? PrismaticChamsPipeline.process() : PrismaticChamsPipeline.compute();
               float var15 = var12.drawAnimation() ? 0.0F : 1.0F;
               this.wild$renderChamsPass(var1, var2, var9, var7, var4, var5, var6, var18, var15, var17);
            }

            if (var7 instanceof PlayerEntityRenderState var19 && var11 instanceof PlayerEntityModel var21) {
               DeadEffect.handle(var19, var21, var2, var9, var4, var5);
            }

            this.wild$lastPlayerState = var7 instanceof PlayerEntityRenderState var20 ? var20 : null;
         } else {
            var1.render(var2, var3, var4, var5, this.wild$applySeeInvisiblesAlpha(var7, var6));
            if (var7 instanceof PlayerEntityRenderState var13 && var11 instanceof PlayerEntityModel var14) {
               DeadEffect.handle(var13, var14, var2, var9, var4, var5);
            }

            this.wild$lastPlayerState = var7 instanceof PlayerEntityRenderState var16 ? var16 : null;
         }
      }
   }

   @Unique
   private int wild$applySeeInvisiblesAlpha(LivingEntityRenderState var1, int var2) {
      if (!WildClient.prepare()) {
         return var2;
      } else if (var1 instanceof PlayerEntityRenderState && var1.invisible) {
         SeeInvisibles var3 = WildClient.instance.data.handle(SeeInvisibles.class);
         return var3 != null && var3.enabled ? ColorHelper.withAlpha(Math.round(var3.source.compute() * 255.0F), var2) : var2;
      } else {
         return var2;
      }
   }

   @Redirect(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"
      )
   )
   private void wild$captureFeatureRender(
      FeatureRenderer<?, ?> var1, MatrixStack var2, VertexConsumerProvider var3, int var4, EntityRenderState var5, float var6, float var7
   ) {
      if (var5 instanceof LivingEntityRenderState var8) {
         FramebufferCapture.handle().handle(var1, var2, var3, var4, var8, var6, var7);
      } else {
         ((FeatureRenderer)var1).render(var2, var3, var4, var5, var6, var7);
      }
   }

   @Inject(
      method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V")
   )
   private void wild$capturePlayerModelOverlays(LivingEntityRenderState var1, MatrixStack var2, VertexConsumerProvider var3, int var4, CallbackInfo var5) {
      try {
         if (FramebufferCapture.handle().check()) {
            this.wild$lastPlayerState = null;
            return;
         }

         PlayerEntityRenderState var6 = this.wild$lastPlayerState;
         this.wild$lastPlayerState = null;
         if (var6 == null || !(var1 instanceof PlayerEntityRenderState var7) || var7 != var6) {
            return;
         }

         if (!((Object)this.model instanceof PlayerEntityModel var9)) {
            return;
         }

         try {
            NameTags.handle(var6, var9, var2);
         } catch (RuntimeException var18) {
         }

         boolean var10 = false;

         try {
            Chams var11 = Chams.load();
            var10 = var11 != null && var11.process(var1);
         } catch (RuntimeException var19) {
         }

         if (var10) {
            return;
         }

         try {
            ChinaHat.handle(var6, var9, var2, var3, var4);
         } catch (RuntimeException var17) {
            RenderDiagnostics.handle().handle("ChinaHat pose capture", var17);
         }
      } finally {
         FramebufferCapture.handle().handle(var1);
      }
   }

   @Unique
   private void wild$renderChamsPass(
      EntityModel<LivingEntityRenderState> var1,
      MatrixStack var2,
      VertexConsumerProvider var3,
      LivingEntityRenderState var4,
      int var5,
      int var6,
      int var7,
      RenderLayer var8,
      float var9,
      float var10
   ) {
      Chams var11 = Chams.load();
      if (var11 != null) {
         PrismaticChamsPipeline.handle(var11, var4, var9, var10);
         VertexConsumer var12 = FramebufferCapture.handle().handle(var3.getBuffer(var8), var8, var4);
         var1.render(var2, var12, var5, var6, ColorHelper.withAlpha(255, var7));
         if (var3 instanceof Immediate var13) {
            var13.draw(var8);
         }
      }
   }
}
