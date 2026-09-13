package org.wild.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.wild.mixin.acceser.GameRendererAccessor;
import ru.wild.WildClient;
import ru.wild.api.event.FrameRenderListener;
import ru.wild.audio.ProceduralAudioOutput;
import ru.wild.audio.ProceduralSoundSynthesizer;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.misc.Removals;
import ru.wild.modules.misc.UnHook;
import ru.wild.modules.player.PlayerHelper;
import ru.wild.modules.visuals.AspectRation;
import ru.wild.modules.visuals.GlowESP;
import ru.wild.modules.visuals.Hands;
import ru.wild.render.GlBreadcrumbLogger;
import ru.wild.render.GlCompatibilityProbe;
import ru.wild.render.GuiPostProcessor;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.render.shader.MainMenuShader;
import ru.wild.render.shader.ScreenTransitionRenderer;
import ru.wild.render.texture.MainFramebufferBinding;
import ru.wild.util.math.ClientMathUtil;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements MinecraftContext {
   @Unique
   private float currentZoom = 1.0F;

   @Shadow
   public abstract float getFarPlaneDistance();

   @Inject(method = "render", at = @At("HEAD"))
   private void wild$coreRenderHead(RenderTickCounter var1, boolean var2, CallbackInfo var3) {
      RenderDiagnostics.handle().resolve();
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void wild$coreRenderTail(RenderTickCounter var1, boolean var2, CallbackInfo var3) {
      RenderDiagnostics.handle().update();
   }

   @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
   private void onGetFov(Camera var1, float var2, boolean var3, CallbackInfoReturnable<Float> var4) {
      float var5 = PlayerHelper.presetSave ? PlayerHelper.windowConvert : 1.0F;
      if (this.currentZoom != var5) {
         this.currentZoom = this.currentZoom + (var5 - this.currentZoom) * 0.05F;
         if (Math.abs(this.currentZoom - var5) < 0.001F) {
            this.currentZoom = var5;
         }
      }

      float var6 = (Float)var4.getReturnValue();
      if (this.currentZoom < 1.0F) {
         var6 *= this.currentZoom;
      }

      if (ProceduralSoundSynthesizer.update()) {
         var6 *= ProceduralSoundSynthesizer.projectItem();
      }

      var4.setReturnValue(var6);
   }

   @Inject(method = "getBasicProjectionMatrix", at = @At("HEAD"), cancellable = true)
   public void getBasicProjectionMatrix(float var1, CallbackInfoReturnable<Matrix4f> var2) {
      if (WildClient.prepare()) {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            if (toggleState != null && toggleState.getWindow() != null) {
               int var3 = toggleState.getWindow().getFramebufferWidth();
               int var4 = toggleState.getWindow().getFramebufferHeight();
               if (var3 > 0 && var4 > 0 && !toggleState.getWindow().hasZeroWidthOrHeight()) {
                  float var5 = (float)var3 / var4 + AspectRation.refresh();
                  if (Float.isFinite(var5) && !(var5 <= 0.0F)) {
                     var2.cancel();
                     Matrix4f var6 = new Matrix4f().perspective(var1 * (float) (Math.PI / 180.0), var5, 0.05F, this.getFarPlaneDistance());
                     if (ProceduralSoundSynthesizer.update()) {
                        var6.m01(var6.m01() + ProceduralSoundSynthesizer.fetch());
                        var6.m10(var6.m10() + ProceduralSoundSynthesizer.measure());
                        var6.scale(ProceduralSoundSynthesizer.blendMatrix(), ProceduralSoundSynthesizer.matchVector(), 1.0F);
                     }

                     var2.setReturnValue(var6);
                  }
               } else {
                  var2.setReturnValue(new Matrix4f().perspective(var1 * (float) (Math.PI / 180.0), 1.0F, 0.05F, this.getFarPlaneDistance()));
               }
            }
         }
      }
   }

   @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
   private void skipWorldRenderWhenWindowInvalid(RenderTickCounter var1, CallbackInfo var2) {
      ProceduralAudioOutput.handle();
      EventDispatchBoundary.handle();
      if (ProceduralSoundSynthesizer.drawAnimation()) {
         ProceduralAudioOutput.compute();
         Runtime.getRuntime().halt(0);
      }

      if (ProceduralSoundSynthesizer.update() && ProceduralSoundSynthesizer.encodePoint()) {
         var2.cancel();
      } else {
         if (toggleState == null
            || toggleState.getWindow() == null
            || toggleState.getWindow().hasZeroWidthOrHeight()
            || toggleState.getWindow().getFramebufferWidth() <= 0
            || toggleState.getWindow().getFramebufferHeight() <= 0) {
            var2.cancel();
         }
      }
   }

   @Inject(
      method = "renderWorld",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
         shift = Shift.AFTER
      )
   )
   private void renderWorld(RenderTickCounter var1, CallbackInfo var2) {
      if (toggleState != null
         && toggleState.getWindow() != null
         && !toggleState.getWindow().hasZeroWidthOrHeight()
         && toggleState.getWindow().getFramebufferWidth() > 0
         && toggleState.getWindow().getFramebufferHeight() > 0) {
         if (toggleState.player != null && toggleState.world != null) {
            Camera var3 = toggleState.gameRenderer.getCamera();
            MatrixStack var4 = new MatrixStack();
            RenderSystem.getModelViewStack().pushMatrix().mul(var4.peek().getPositionMatrix());
            var4.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var3.getPitch()));
            var4.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var3.getYaw() + 180.0F));
            float var5 = toggleState.getRenderTickCounter().getTickProgress(true);
            float var6 = ((GameRendererAccessor)toggleState.gameRenderer).invokeGetFov(var3, var5, true);
            ClientMathUtil.instance.set(toggleState.gameRenderer.getBasicProjectionMatrix(var6));
            ClientMathUtil.data.set(RenderSystem.getModelViewMatrix());
            ClientMathUtil.context.set(var4.peek().getPositionMatrix());
            RenderSystem.getModelViewStack().popMatrix();
         }
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void wild$renderMainMenuOverlay(RenderTickCounter var1, boolean var2, CallbackInfo var3) {
      if (WildClient.prepare()) {
         if (toggleState != null
            && toggleState.getWindow() != null
            && !toggleState.getWindow().hasZeroWidthOrHeight()
            && toggleState.getWindow().getFramebufferWidth() > 0
            && toggleState.getWindow().getFramebufferHeight() > 0) {
            if (toggleState.currentScreen instanceof FrameRenderListener var4 && var4.handle()) {
               Framebuffer var34 = toggleState.getFramebuffer();
               if (var34 != null && var34.getColorAttachment() instanceof GlTexture var7) {
                  int var8 = var7.getGlId();
                  if (var8 > 0) {
                     int var9 = GL11.glGetInteger(36006);
                     int var10 = GL11.glGetInteger(36010);
                     int var11 = GL11.glGetInteger(36006);
                     GlCompatibilityProbe.handle(var9);
                     OpenGlStateSnapshot.NetworkState var12 = OpenGlStateSnapshot.handle();
                     boolean var13 = false;
                     int var14 = 0;

                     try {
                        var14 = MainFramebufferBinding.process();
                        if (var14 == 0) {
                           ScreenRenderDiagnostics.handle(var4, "raw-overlay", false, "temp fbo unavailable", null);
                           return;
                        }

                        GL30.glBindFramebuffer(36160, var14);
                        GL30.glFramebufferTexture2D(36160, 36064, 3553, var8, 0);
                        GL11.glDrawBuffer(36064);
                        var13 = GL30.glCheckFramebufferStatus(36160) == 36053;
                        if (var13) {
                           GL11.glColorMask(true, true, true, true);
                           GL11.glDisable(2929);
                           GL11.glDisable(2884);
                           GL11.glEnable(3042);
                           int var15 = (int)toggleState.mouse.getScaledX(toggleState.getWindow());
                           int var16 = (int)toggleState.mouse.getScaledY(toggleState.getWindow());
                           GuiRippleShader var17 = GuiRippleShader.handle();
                           boolean var18 = var17.handle(var4)
                              && var17.handle(toggleState.getWindow().getFramebufferWidth(), toggleState.getWindow().getFramebufferHeight());

                           try {
                              var4.handle(var15, var16, var1.getDynamicDeltaTicks());
                              ScreenRenderDiagnostics.handle(var4, "raw-overlay", true, "renderRawOverlay complete", null);
                           } finally {
                              if (var18) {
                                 var17.compute();
                              }
                           }
                        } else {
                           ScreenRenderDiagnostics.handle(var4, "raw-overlay", false, "temp fbo incomplete", null);
                        }
                     } catch (Throwable var31) {
                        GlBreadcrumbLogger.process("raw-overlay", "threw: " + var31);
                        ScreenRenderDiagnostics.handle(var4, "raw-overlay", false, "renderRawOverlay failed", var31);
                     } finally {
                        if (var14 != 0) {
                           GL30.glBindFramebuffer(36160, var14);
                           GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                        }

                        OpenGlStateSnapshot.compute(var12);
                        OpenGlStateSnapshot.handle(36009, var9);
                        OpenGlStateSnapshot.handle(36008, var10);
                        OpenGlStateSnapshot.handle(36160, var11);
                     }
                  }
               }
            }

            if (toggleState.getOverlay() instanceof SplashOverlay && !UnHook.target) {
               this.wild$renderLoadingOverlayAfterGui();
            } else {
               MainMenuShader.handle().resolve();
            }

            if (toggleState.currentScreen != null && toggleState.world == null && !(toggleState.currentScreen instanceof FrameRenderListener)) {
               try {
                  ScreenTransitionRenderer.handle().handle(var1.getDynamicDeltaTicks());
               } catch (Throwable var29) {
                  ScreenTransitionRenderer.handle().compute();
               }
            } else {
               ScreenTransitionRenderer.handle().compute();
            }

            if (WildClient.instance != null && WildClient.instance.data != null) {
               GlowESP var33 = WildClient.instance.data.handle(GlowESP.class);
               if (var33 != null) {
                  var33.refresh();
               }

               Hands var35 = WildClient.instance.data.handle(Hands.class);
               if (var35 != null) {
                  var35.refresh();
               }
            }
         }
      }
   }

   @Inject(method = "render", at = @At("TAIL"))
   private void wild$renderModernGuiComposite(RenderTickCounter var1, boolean var2, CallbackInfo var3) {
      GuiPostProcessor.handle(toggleState, var1.getDynamicDeltaTicks());
   }

   @Unique
   private void wild$renderLoadingOverlayAfterGui() {
      Framebuffer var1 = toggleState.getFramebuffer();
      if (var1 != null) {
         if (var1.getColorAttachment() instanceof GlTexture var3) {
            int var4 = var3.getGlId();
            if (var4 > 0) {
               int var5 = GL11.glGetInteger(36006);
               int var6 = GL11.glGetInteger(36010);
               int var7 = GL11.glGetInteger(36006);
               OpenGlStateSnapshot.NetworkState var8 = OpenGlStateSnapshot.handle();
               int var9 = 0;

               try {
                  var9 = MainFramebufferBinding.process();
                  if (var9 == 0) {
                     return;
                  }

                  GL30.glBindFramebuffer(36160, var9);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, var4, 0);
                  GL11.glDrawBuffer(36064);
                  if (GL30.glCheckFramebufferStatus(36160) == 36053) {
                     GL11.glColorMask(true, true, true, true);
                     GL11.glDisable(2929);
                     GL11.glDisable(2884);
                     GL11.glEnable(3042);
                     int var10 = (int)toggleState.mouse.getScaledX(toggleState.getWindow());
                     int var11 = (int)toggleState.mouse.getScaledY(toggleState.getWindow());
                     MainMenuShader.handle().handle(toggleState, var10, var11);
                     return;
                  }
               } catch (Throwable var15) {
                  return;
               } finally {
                  if (var9 != 0) {
                     GL30.glBindFramebuffer(36160, var9);
                     GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                  }

                  OpenGlStateSnapshot.compute(var8);
                  OpenGlStateSnapshot.handle(36009, var5);
                  OpenGlStateSnapshot.handle(36008, var6);
                  OpenGlStateSnapshot.handle(36160, var7);
               }
            }
         }
      }
   }

   @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
   private void cancelHurtCamera(MatrixStack var1, float var2, CallbackInfo var3) {
      if (Removals.handle("Тряска от урона")) {
         var3.cancel();
      }
   }
}
