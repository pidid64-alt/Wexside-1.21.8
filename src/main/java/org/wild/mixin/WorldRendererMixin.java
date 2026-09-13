package org.wild.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.modules.misc.Removals;
import ru.wild.modules.visuals.ChinaHat;
import ru.wild.modules.visuals.Stardust;
import ru.wild.modules.visuals.WorldTweaks;
import ru.wild.render.BlockEspRenderer;
import ru.wild.render.FramebufferCapture;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.StardustSkyRenderer;
import ru.wild.render.WorldRenderContext;
import ru.wild.render.WorldVertexBuffer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
   @Shadow
   @Final
   private DefaultFramebufferSet framebufferSet;
   @Shadow
   @Nullable
   private ClientWorld world;

   @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
   private void renderStardustSky(FrameGraphBuilder var1, Camera var2, float var3, GpuBufferSlice var4, CallbackInfo var5) {
      if (Stardust.refresh()) {
         var5.cancel();
         if (this.world != null && var2 != null) {
            CameraSubmersionType var6 = var2.getSubmersionType();
            if (var6 != CameraSubmersionType.POWDER_SNOW && var6 != CameraSubmersionType.LAVA && !this.wild$hasBlindnessOrDarkness(var2)) {
               FramePass var7 = var1.createPass("wild_stardust_sky");
               this.framebufferSet.mainFramebuffer = var7.transfer(this.framebufferSet.mainFramebuffer);
               var7.setRenderer(() -> {
                  RenderSystem.setShaderFog(var4);
                  StardustSkyRenderer.handle(var2, var3, Stardust.tick());
               });
            }
         }
      }
   }

   @Unique
   private boolean wild$hasBlindnessOrDarkness(Camera var1) {
      return !(var1.getFocusedEntity() instanceof LivingEntity var2)
         ? false
         : var2.hasStatusEffect(StatusEffects.BLINDNESS) || var2.hasStatusEffect(StatusEffects.DARKNESS);
   }

   @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
   private void wild$suppressWeather(FrameGraphBuilder var1, Vec3d var2, float var3, GpuBufferSlice var4, CallbackInfo var5) {
      if (WorldTweaks.refresh() || Removals.process("Погода (дождь/снег)")) {
         var5.cancel();
      }
   }

   @Inject(method = "addWeatherParticlesAndSound", at = @At("HEAD"), cancellable = true)
   private void wild$suppressWeatherFx(Camera var1, CallbackInfo var2) {
      if (Removals.process("Погода (дождь/снег)")) {
         var2.cancel();
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void beginEntityCapture(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5,
      Matrix4f var6,
      GpuBufferSlice var7,
      Vector4f var8,
      boolean var9,
      CallbackInfo var10
   ) {
      ChinaHat.refresh();
      BlockEspRenderer.handle(var6);
      StardustSkyRenderer.handle(var5, var6);
      FramebufferCapture.handle().handle((WorldRenderer)(Object)this, var2, var4);
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void publishWorldRenderEvent(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5,
      Matrix4f var6,
      GpuBufferSlice var7,
      Vector4f var8,
      boolean var9,
      CallbackInfo var10
   ) {
      MinecraftClient var11 = MinecraftClient.getInstance();
      if (!WorldVertexBuffer.handle(var11)) {
         FramebufferCapture.handle().select();
      } else {
         MatrixStack var12 = new MatrixStack();
         var12.multiplyPositionMatrix(new Matrix4f(var5));
         EventHandlerInvoker.handle(new WorldRenderEvent(var12, var2.getTickProgress(true)));
         FramebufferCapture.handle().select();
         GameRenderer var13 = var11.gameRenderer;
         if (var13 != null && var4 != null) {
            OpenGlStateSnapshot.NetworkState var14 = OpenGlStateSnapshot.handle();
            WorldRenderContext var15 = null;

            try {
               var15 = WorldRenderContext.handle(var11, var2, var4, var5, var6);
               float var16 = var15.apply();

               try {
                  EventHandlerInvoker.handle(new ru.wild.api.event.WorldRenderContext(var11, var13, var15, var16));
               } finally {
                  if (var15 != null) {
                     try {
                        var15.prepare();
                     } finally {
                        var15.close();
                     }
                  }
               }
            } finally {
               OpenGlStateSnapshot.compute(var14);
               if (ChinaHat.render()) {
                  OpenGlStateSnapshot.resolve(var14);
               }
            }
         }
      }
   }
}
