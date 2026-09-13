package org.wild.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.HudRenderContext;
import ru.wild.audio.ProceduralAudioOutput;
import ru.wild.audio.ProceduralSoundSynthesizer;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.gui.screen.HudEditorScreen;
import ru.wild.gui.screen.RotationAnalyticsScreen;
import ru.wild.gui.screen.RotationBuilderScreen;
import ru.wild.gui.screen.ServerPresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.combat.AutoSwap;
import ru.wild.modules.misc.Removals;
import ru.wild.modules.visuals.Animations;
import ru.wild.modules.visuals.ColorPlus;
import ru.wild.modules.visuals.Hud;
import ru.wild.modules.visuals.MotionBlur;
import ru.wild.modules.visuals.ProtectInfo;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.ColorPlusShader;
import ru.wild.render.shader.MotionBlurRenderer;
import ru.wild.render.shader.SkyStylePresets;
import ru.wild.render.texture.MainFramebufferBinding;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.render.RoundedRectRenderer;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {
   @Unique
   private static final HudRenderContext wild$cachedEventScreen = new HudRenderContext();
   @Unique
   private long wild$lastCorruptionFrameMs;
   @Unique
   private float wild$heldTearY;
   @Unique
   private float wild$heldTearH;
   @Unique
   private float wild$heldTearShift;
   @Unique
   private float wild$panicWhite;
   @Unique
   private float wild$panicBlack;

   @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
   private void onRenderCrosshair(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      MinecraftClient var4 = MinecraftClient.getInstance();
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      } else {
         if (AutoSwap.refresh()
            || var4.currentScreen instanceof ServerPresets
            || var4.currentScreen instanceof ClickGuiModernScreen
            || var4.currentScreen instanceof HudEditorScreen
            || var4.currentScreen instanceof RotationBuilderScreen
            || var4.currentScreen instanceof RotationAnalyticsScreen) {
            var3.cancel();
         }
      }
   }

   @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
   private void onRenderStatusEffects(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      } else {
         if (Hud.previous.process("Potions") || wild$noRenderPotions()) {
            var3.cancel();
         }
      }
   }

   @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
   private void onRenderHotbar(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      } else {
         if (wild$customHotbarActive() || this.wild$foundryOverlayVisible() || MinecraftClient.getInstance().currentScreen instanceof HudEditorScreen) {
            var3.cancel();
         }
      }
   }

   @Unique
   private boolean wild$foundryOverlayVisible() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1 != null && var1.currentScreen instanceof ClickGuiModernScreen var2 ? var2.handle().handle() : false;
   }

   @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderStatusBars(DrawContext var1, CallbackInfo var2) {
      if (ProceduralSoundSynthesizer.update() || wild$customHotbarActive()) {
         var2.cancel();
      }
   }

   @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelHeldItemTooltip(DrawContext var1, CallbackInfo var2) {
      if (ProceduralSoundSynthesizer.update() || wild$customHotbarActive()) {
         var2.cancel();
      }
   }

   @Inject(
      method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void onRenderScoreboardSidebar(DrawContext var1, ScoreboardObjective var2, CallbackInfo var3) {
      if (WildClient.prepare()) {
         if (ProceduralSoundSynthesizer.update()) {
            var3.cancel();
         } else {
            ProtectInfo var4 = WildClient.instance.data.handle(ProtectInfo.class);
            if (var4 != null && var4.enabled && var4.itemProject.compute()) {
               var3.cancel();
            }
         }
      }
   }

   @Inject(method = "renderMainHud", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelMainHudDuringCorruption(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      }
   }

   @Inject(method = "renderPlayerList", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelPlayerListDuringCorruption(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      }
   }

   @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelOverlayMessageDuringCorruption(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      }
   }

   @Inject(method = "renderTitleAndSubtitle", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelTitleDuringCorruption(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      }
   }

   @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelVignette(DrawContext var1, Entity var2, CallbackInfo var3) {
      if (Removals.handle("Виньетка")) {
         var3.cancel();
      }
   }

   @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelSpyglass(DrawContext var1, float var2, CallbackInfo var3) {
      if (Removals.handle("Подзорная труба")) {
         var3.cancel();
      }
   }

   @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelPortalOverlay(DrawContext var1, float var2, CallbackInfo var3) {
      if (Removals.handle("Портал")) {
         var3.cancel();
      }
   }

   @Inject(method = "renderNauseaOverlay", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelNauseaOverlay(DrawContext var1, float var2, CallbackInfo var3) {
      if (Removals.handle("Тошнота (экран)")) {
         var3.cancel();
      }
   }

   @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelMiscOverlay(DrawContext var1, Identifier var2, float var3, CallbackInfo var4) {
      if (var2 != null) {
         String var5 = var2.getPath();
         if (var5.contains("pumpkin") && Removals.handle("Тыква")) {
            var4.cancel();
         } else {
            if (var5.contains("powder_snow") && Removals.handle("Порошковый снег")) {
               var4.cancel();
            }
         }
      }
   }

   @Redirect(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"), require = 0)
   private boolean wild$keepTabListForClose(KeyBinding var1) {
      if (!WildClient.prepare()) {
         return false;
      } else if (ProceduralSoundSynthesizer.update()) {
         return false;
      } else {
         boolean var2 = var1.isPressed();
         if (WildClient.drawProfile() && WildClient.instance != null && WildClient.instance.data != null) {
            Animations var3 = WildClient.instance.data.handle(Animations.class);
            return var3 != null && var3.enabled && var3.source.process("Таб") ? var3.compute(var2) : var2;
         } else {
            return var2;
         }
      }
   }

   @Redirect(
      method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardObjective;getDisplayName()Lnet/minecraft/text/Text;")
   )
   private Text litka$maskScoreboardTitle(ScoreboardObjective var1) {
      if (ProceduralSoundSynthesizer.update()) {
         return Text.empty();
      }

      if (var1 == null) {
         return Text.empty();
      }

      Text var2 = var1.getDisplayName();
      return (Text)(var2 != null ? ProtectInfo.handle(var2) : Text.empty());
   }

   @Redirect(
      method = "renderMainHud",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/bar/Bar;drawExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V"
      )
   )
   private void redirectDrawExperienceLevel(DrawContext var1, TextRenderer var2, int var3) {
      if (!ProceduralSoundSynthesizer.update() && !wild$customHotbarActive()) {
         Bar.drawExperienceLevel(var1, var2, var3);
      }
   }

   @Redirect(
      method = "renderMainHud",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/bar/Bar;renderBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
      ),
      require = 0
   )
   private void redirectRenderBar(Bar var1, DrawContext var2, RenderTickCounter var3) {
      if (!ProceduralSoundSynthesizer.update()) {
         if (!wild$customHotbarActive()) {
            var1.renderBar(var2, var3);
         }
      }
   }

   @Redirect(
      method = "renderMainHud",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/bar/Bar;renderAddons(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
      ),
      require = 0
   )
   private void redirectRenderAddons(Bar var1, DrawContext var2, RenderTickCounter var3) {
      if (!ProceduralSoundSynthesizer.update()) {
         if (!wild$customHotbarActive()) {
            var1.renderAddons(var2, var3);
         }
      }
   }

   @Inject(method = "render", at = @At("HEAD"))
   private void wild$applyColorPlus(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (WildClient.prepare()) {
         if (WildClient.drawProfile()) {
            if (WildClient.instance != null && WildClient.instance.data != null) {
               ColorPlus var4;
               MotionBlur var5;
               try {
                  var4 = (ColorPlus)WildClient.instance.data.process(ColorPlus.class);
                  var5 = (MotionBlur)WildClient.instance.data.process(MotionBlur.class);
               } catch (Throwable var20) {
                  return;
               }

               boolean var6 = var4 != null && var4.enabled;
               boolean var7 = var5 != null && var5.enabled;
               if (var6 || var7) {
                  MinecraftClient var8 = MinecraftClient.getInstance();
                  if (var8 != null && var8.world != null && var8.player != null) {
                     if (var8.getWindow() != null) {
                        int var9 = var8.getWindow().getFramebufferWidth();
                        int var10 = var8.getWindow().getFramebufferHeight();
                        if (var9 > 1 && var10 > 1) {
                           Framebuffer var11 = var8.getFramebuffer();
                           if (var11 != null) {
                              if (var11.getColorAttachment() instanceof GlTexture var13) {
                                 int var14 = var13.getGlId();
                                 if (var14 > 0) {
                                    if (var7) {
                                       if (var8.currentScreen == null) {
                                          try {
                                             MotionBlurRenderer.handle()
                                                .handle(
                                                   var8,
                                                   var8.gameRenderer.getCamera(),
                                                   new Matrix4f(ClientMathUtil.context),
                                                   new Matrix4f(ClientMathUtil.instance),
                                                   var5.refresh()
                                                );
                                          } catch (Throwable var19) {
                                             System.err.println("[SilkFlow] apply failed: " + var19.getMessage());
                                          }
                                       } else {
                                          MotionBlurRenderer.handle().process();
                                       }
                                    }

                                    if (var6) {
                                       SkyStylePresets var15 = var4.refresh();
                                       ColorPlusShader.ColorState var16 = new ColorPlusShader.ColorState();
                                       var16.instance = var4.target.compute();
                                       var16.data = var15.data + var4.previous.compute();
                                       var16.context = var15.context + var4.latest.compute();
                                       var16.config = var15.config + var4.summary.compute();
                                       var16.state = var15.state + var4.matrixBlend.compute();
                                       var16.cache = var15.cache + var4.vectorMatch.compute();
                                       var16.output = var15.output + var4.itemProject.compute();
                                       var16.current = var15.current + var4.responseCompute.compute();
                                       var16.active = var15.active[0];
                                       var16.mode = var15.active[1];
                                       var16.selection = var15.active[2];
                                       var16.enabled = var15.mode[0];
                                       var16.renderer = var15.mode[1];
                                       var16.handler = var15.mode[2];
                                       var16.animationDraw = var15.selection[0];
                                       var16.pointEncode = var15.selection[1];
                                       var16.animator = var15.selection[2];
                                       var16.source = 0.0F;
                                       var16.target = var15.renderer;
                                       var16.pending = var15.handler;
                                       var16.previous = var4.pending.compute() ? Math.max(0.0F, var15.animationDraw + var4.profileDraw.compute()) : 0.0F;
                                       var16.latest = Math.max(0.0F, var15.pointEncode + var4.vectorPerform.compute());
                                       var16.summary = true;

                                       try {
                                          ColorPlusShader.handle().handle(var14, var9, var10, var16);
                                       } catch (Throwable var18) {
                                          System.err.println("[ColorPlus] apply failed: " + var18.getMessage());
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void onRenderHud(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (WildClient.drawProfile()) {
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var4 != null && var4.player != null && var4.world != null && var4.getWindow() != null) {
            int var5 = var4.getWindow().getFramebufferWidth();
            int var6 = var4.getWindow().getFramebufferHeight();
            if (var5 > 0 && var6 > 0) {
               try {
                  WildClient.check();
               } catch (Throwable var25) {
                  return;
               }

               if (WildClient.handle() != null) {
                  OpenGlStateSnapshot.NetworkState var7 = OpenGlStateSnapshot.handle();
                  int var8 = 0;

                  try {
                     try {
                        FontRegistry.process();
                     } catch (Throwable var24) {
                     }

                     Framebuffer var9 = var4.getFramebuffer();
                     if (var9 != null) {
                        if (var9.getColorAttachment() instanceof GlTexture var11) {
                           int var12 = var11.getGlId();
                           var8 = MainFramebufferBinding.handle();
                           if (var8 == 0) {
                              OpenGlStateSnapshot.handle(36009, var7.instance);
                              OpenGlStateSnapshot.handle(36008, var7.data);
                           } else {
                              GL30.glBindFramebuffer(36160, var8);
                              GL30.glFramebufferTexture2D(36160, 36064, 3553, var12, 0);
                              GL11.glDrawBuffer(36064);
                              if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                                 GL30.glDeleteFramebuffers(var8);
                                 MainFramebufferBinding.handle(var8);
                                 var8 = 0;
                                 OpenGlStateSnapshot.handle(36009, var7.instance);
                                 OpenGlStateSnapshot.handle(36008, var7.data);
                              }
                           }
                        } else {
                           OpenGlStateSnapshot.handle(36009, var7.instance);
                           OpenGlStateSnapshot.handle(36008, var7.data);
                        }
                     } else {
                        OpenGlStateSnapshot.handle(36009, var7.instance);
                        OpenGlStateSnapshot.handle(36008, var7.data);
                     }

                     GL11.glColorMask(true, true, true, true);
                     GL11.glDisable(2929);
                     GL11.glEnable(3042);
                     RoundedRectRenderer var28 = WildClient.handle();
                     if (var28 == null) {
                        return;
                     }

                     ThemeRenderer var29 = ThemeRenderer.handle();
                     var29.handle(var4, var28, var5, var6);
                     var29.compute();
                     boolean var30 = ProceduralSoundSynthesizer.update();
                     ProceduralAudioOutput.handle();
                     boolean var13 = false;

                     try {
                        var28.handle(var5, var6);
                        var13 = true;
                        if (var30) {
                           this.wild$drawCorruption(var28, var5, var6);
                        } else {
                           wild$cachedEventScreen.handle(var4, var28, FontRegistry.instance, var5, var6, var1);
                           EventHandlerInvoker.handle(wild$cachedEventScreen);
                        }
                     } finally {
                        if (var13) {
                           var28.process();
                           var29.resolve();
                        }
                     }
                  } finally {
                     if (var8 != 0) {
                        GL30.glBindFramebuffer(36160, var8);
                        GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                     }

                     OpenGlStateSnapshot.compute(var7);
                  }
               }
            } else {
               WildClient.handle(var5, var6);
            }
         }
      }
   }

   @Unique
   private void wild$drawCorruption(RoundedRectRenderer var1, int var2, int var3) {
      if (var2 > 0 && var3 > 0) {
         long var4 = System.currentTimeMillis();
         if (var4 - this.wild$lastCorruptionFrameMs > 28L || ProceduralSoundSynthesizer.handle(9182, 120L, 0.36F, 42L)) {
            this.wild$lastCorruptionFrameMs = var4;
            this.wild$heldTearY = wild$norm(ProceduralSoundSynthesizer.process(901)) * var3;
            this.wild$heldTearH = 8.0F + Math.abs(ProceduralSoundSynthesizer.process(902)) * var3 * 0.22F;
            this.wild$heldTearShift = ProceduralSoundSynthesizer.process(903) * var2 * 0.42F;
         }

         int var6 = ProceduralSoundSynthesizer.refresh();
         float var7 = ProceduralSoundSynthesizer.render();
         float var8 = ProceduralSoundSynthesizer.handle(1250000000L, 2250000000L);
         if (ProceduralSoundSynthesizer.save()) {
            this.wild$panicWhite = Math.min(1.0F, this.wild$panicWhite + 0.68F + ProceduralSoundSynthesizer.collectModule() * 0.24F);
         } else {
            this.wild$panicWhite *= 0.58F;
         }

         if (ProceduralSoundSynthesizer.animate()) {
            this.wild$panicBlack = Math.min(1.0F, this.wild$panicBlack + 0.38F + ProceduralSoundSynthesizer.checkFrame() * 0.32F);
         } else {
            this.wild$panicBlack *= 0.72F;
         }

         this.wild$drawNoHudVoid(var1, var2, var3, var6, var7, var8);
         this.wild$drawBacklightPulse(var1, var2, var3, var6, var7);
         this.wild$drawScanMatrix(var1, var2, var3, var6, var7);
         this.wild$drawHeldTear(var1, var2, var3, var6, var7);
         this.wild$drawTconFailure(var1, var2, var3, var6, var7);
         this.wild$drawVramFailure(var1, var2, var3, var6, var7);
         this.wild$drawDeadPixels(var1, var2, var3, var6, var7);
         this.wild$drawEdgePressure(var1, var2, var3, var6, var7, var8);
         this.wild$drawBlackout(var1, var2, var3, var6, var7, var8);
      }
   }

   @Unique
   private void wild$drawNoHudVoid(RoundedRectRenderer var1, int var2, int var3, int var4, float var5, float var6) {
      int var7 = wild$alpha(42 + (int)(95.0F * ProceduralSoundSynthesizer.performVector()));
      var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(var7, 0, 0, 0));
      if (var4 <= 2) {
         int var8 = wild$alpha(18 + (int)(36.0F * var5));
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(var8, 2, 7, 13));
      }

      if (var4 >= 3) {
         int var9 = wild$alpha((int)(72.0F * var5));
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(var9, 7, 0, 0));
      }

      if (var6 > 0.0F) {
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(wild$alpha((int)(120.0F * var6)), 0, 0, 0));
      }
   }

   @Unique
   private void wild$drawBacklightPulse(RoundedRectRenderer var1, int var2, int var3, int var4, float var5) {
      if (this.wild$panicWhite > 0.01F) {
         int var6 = wild$alpha((int)(220.0F * this.wild$panicWhite));
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(var6, 255, 255, 255));
      }

      if (var4 >= 2 && ProceduralSoundSynthesizer.handle(12001, 260L, 0.38F + ProceduralSoundSynthesizer.collectModule() * 0.28F, 28L)) {
         int var7 = wild$alpha(60 + (int)(130.0F * var5));
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(var7, 230, 245, 255));
      }

      if (var4 >= 3 && ProceduralSoundSynthesizer.handle(12002, 720L, 0.26F + ProceduralSoundSynthesizer.checkFrame() * 0.34F, 95L)) {
         int var8 = wild$alpha(130 + (int)(90.0F * var5));
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(var8, 0, 0, 0));
      }
   }

   @Unique
   private void wild$drawScanMatrix(RoundedRectRenderer var1, int var2, int var3, int var4, float var5) {
      int var6 = var4 >= 3 ? 2 : 3;
      float var7 = (float)(System.nanoTime() / 1400000L % var6);
      int var8 = wild$alpha(20 + (int)(54.0F * var5));

      for (float var9 = -var7; var9 < var3; var9 += var6) {
         var1.handle(0.0F, var9, var2, 1.0F, wild$rgba(var8, 0, 0, 0));
      }

      int var15 = var4 >= 3 ? 7 : 3;

      for (int var10 = 0; var10 < var15; var10++) {
         float var11 = wild$norm(ProceduralSoundSynthesizer.handle(13000 + var10, 85L)) * var2;
         float var12 = 1.0F + Math.abs(ProceduralSoundSynthesizer.handle(13020 + var10, 130L)) * 5.0F * var5;
         int var13 = wild$alpha(12 + (int)(56.0F * var5));
         var1.handle(var11, 0.0F, var12, var3, wild$rgba(var13, 255, 255, 255));
      }

      int var16 = var4 >= 3 ? 18 : (var4 >= 2 ? 9 : 4);

      for (int var17 = 0; var17 < var16; var17++) {
         float var18 = wild$norm(ProceduralSoundSynthesizer.handle(13100 + var17, 18L + var17)) * var3;
         float var19 = 1.0F + Math.abs(ProceduralSoundSynthesizer.handle(13140 + var17, 44L)) * (var4 >= 3 ? 6.0F : 2.0F);
         int var14 = wild$alpha(28 + (int)(120.0F * var5 * Math.abs(ProceduralSoundSynthesizer.handle(13180 + var17, 31L))));
         var1.handle(0.0F, var18, var2, var19, wild$rgba(var14, 210, 228, 255));
      }
   }

   @Unique
   private void wild$drawHeldTear(RoundedRectRenderer var1, int var2, int var3, int var4, float var5) {
      if (var4 >= 2) {
         int var6 = wild$alpha(36 + (int)(105.0F * var5));
         float var7 = Math.max(0.0F, Math.min(var3, this.wild$heldTearY));
         float var8 = Math.max(1.0F, Math.min(var3 * 0.45F, this.wild$heldTearH));
         float var9 = this.wild$heldTearShift * var5;
         var1.handle(var9, var7, var2 + Math.abs(var9) * 2.0F, var8, wild$rgba(var6, 220, 220, 220));
         if (var4 >= 3) {
            var1.handle(var9 - 12.0F * var5, var7, var2 + Math.abs(var9) * 2.0F, Math.max(1.0F, var8 * 0.16F), wild$rgba(wild$alpha(var6 + 28), 255, 25, 38));
            var1.handle(
               var9 + 8.0F * var5,
               var7 + var8 * 0.34F,
               var2 + Math.abs(var9) * 2.0F,
               Math.max(1.0F, var8 * 0.12F),
               wild$rgba(wild$alpha(var6 + 18), 25, 255, 70)
            );
            var1.handle(
               var9 + 18.0F * var5,
               var7 + var8 * 0.66F,
               var2 + Math.abs(var9) * 2.0F,
               Math.max(1.0F, var8 * 0.1F),
               wild$rgba(wild$alpha(var6 + 18), 40, 80, 255)
            );
         }
      }
   }

   @Unique
   private void wild$drawTconFailure(RoundedRectRenderer var1, int var2, int var3, int var4, float var5) {
      int var6 = ProceduralSoundSynthesizer.computeResponse() + (var4 >= 3 ? 16 : 4);
      long var7 = var4 >= 3 ? 14L : 30L;

      for (int var9 = 0; var9 < var6; var9++) {
         float var10 = wild$norm(ProceduralSoundSynthesizer.handle(14000 + var9 * 7, var7 + var9)) * var3;
         float var11 = 1.0F + Math.abs(ProceduralSoundSynthesizer.handle(14001 + var9 * 7, var7 + 11L)) * (var4 >= 3 ? 26.0F : 9.0F) * var5;
         float var12 = ProceduralSoundSynthesizer.handle(14002 + var9 * 7, var7) * var2 * (var4 >= 3 ? 0.44F : 0.18F) * var5;
         float var13 = var2 + Math.abs(var12) * 2.0F;
         int var14 = wild$alpha(22 + (int)(118.0F * var5 * Math.abs(ProceduralSoundSynthesizer.handle(14003 + var9 * 7, var7))));
         int var15 = var9 % 11;
         if (var15 == 0) {
            var1.handle(var12, var10, var13, var11, wild$rgba(var14, 255, 25, 35));
         } else if (var15 == 1) {
            var1.handle(var12, var10, var13, var11, wild$rgba(var14, 28, 255, 70));
         } else if (var15 == 2) {
            var1.handle(var12, var10, var13, var11, wild$rgba(var14, 42, 86, 255));
         } else if (var15 == 3) {
            var1.handle(var12, var10, var13, var11, wild$rgba(wild$alpha(var14 + 30), 255, 255, 255));
         } else {
            var1.handle(var12, var10, var13, var11, wild$rgba(var14, 210, 210, 210));
         }
      }

      if (var4 >= 3) {
         int var16 = 3 + (int)(8.0F * ProceduralSoundSynthesizer.readServer());

         for (int var17 = 0; var17 < var16; var17++) {
            float var18 = wild$norm(ProceduralSoundSynthesizer.handle(14200 + var17, 44L)) * var3;
            float var19 = 12.0F + Math.abs(ProceduralSoundSynthesizer.handle(14250 + var17, 58L)) * var3 * 0.18F * var5;
            float var20 = ProceduralSoundSynthesizer.handle(14300 + var17, 32L) * var2 * 0.58F * var5;
            int var21 = wild$alpha(28 + (int)(112.0F * var5));
            var1.handle(var20, var18, var2 + Math.abs(var20) * 2.0F, var19, wild$rgba(var21, 230, 230, 230));
         }
      }
   }

   @Unique
   private void wild$drawVramFailure(RoundedRectRenderer var1, int var2, int var3, int var4, float var5) {
      if (var4 >= 2) {
         int var6 = ProceduralSoundSynthesizer.fetchProvider() + (var4 >= 3 ? 22 : 4);
         long var7 = var4 >= 3 ? 32L : 76L;

         for (int var9 = 0; var9 < var6; var9++) {
            float var10 = wild$norm(ProceduralSoundSynthesizer.handle(15000 + var9 * 6, var7)) * var2;
            float var11 = wild$norm(ProceduralSoundSynthesizer.handle(15001 + var9 * 6, var7 + 7L)) * var3;
            float var12 = 3.0F + Math.abs(ProceduralSoundSynthesizer.handle(15002 + var9 * 6, var7 + 13L)) * (var4 >= 3 ? 210.0F : 76.0F) * var5;
            float var13 = 2.0F + Math.abs(ProceduralSoundSynthesizer.handle(15003 + var9 * 6, var7 + 19L)) * (var4 >= 3 ? 116.0F : 38.0F) * var5;
            int var14 = wild$alpha(26 + (int)(130.0F * var5));
            int var15 = var9 % 17;
            int var16 = wild$byte(ProceduralSoundSynthesizer.handle(15004 + var9 * 6, var7 + 23L));
            if (var15 == 0) {
               var1.handle(var10, var11, var12, var13, wild$rgba(var14, 255, 0, 0));
            } else if (var15 == 1) {
               var1.handle(var10, var11, var12, var13, wild$rgba(var14, 0, 255, 70));
            } else if (var15 == 2) {
               var1.handle(var10, var11, var12, var13, wild$rgba(var14, 40, 80, 255));
            } else if (var15 == 3) {
               var1.handle(var10, var11, var12, var13, wild$rgba(wild$alpha(var14 + 30), 255, 255, 255));
            } else if (var15 == 4 && var4 >= 3) {
               var1.handle(var10, var11, var12, var13, wild$rgba(wild$alpha(var14 + 20), 0, 0, 0));
            } else {
               var1.handle(var10, var11, var12, var13, wild$rgba(var14, var16, var16, var16));
            }
         }
      }
   }

   @Unique
   private void wild$drawDeadPixels(RoundedRectRenderer var1, int var2, int var3, int var4, float var5) {
      int var6 = ProceduralSoundSynthesizer.drawProfile() + (var4 >= 3 ? 120 : 24);

      for (int var7 = 0; var7 < var6; var7++) {
         float var8 = wild$norm(ProceduralSoundSynthesizer.compute(16000 + var7 * 3)) * var2;
         float var9 = wild$norm(ProceduralSoundSynthesizer.compute(16001 + var7 * 3)) * var3;
         if (var4 >= 3 || !(ProceduralSoundSynthesizer.handle(16002 + var7 * 3, 230L) < -0.42F)) {
            float var10 = var4 >= 3 && var7 % 9 == 0 ? 2.0F : 1.0F;
            int var11 = wild$alpha(36 + (int)(205.0F * var5 * Math.abs(ProceduralSoundSynthesizer.handle(16100 + var7, 110L))));
            int var12 = var7 % 19;
            if (var12 == 0) {
               var1.handle(var8, var9, var10, var10, wild$rgba(var11, 255, 0, 0));
            } else if (var12 == 1) {
               var1.handle(var8, var9, var10, var10, wild$rgba(var11, 0, 255, 50));
            } else if (var12 == 2) {
               var1.handle(var8, var9, var10, var10, wild$rgba(var11, 40, 90, 255));
            } else if (var12 == 3) {
               var1.handle(var8, var9, var10, var10, wild$rgba(var11, 0, 0, 0));
            } else {
               var1.handle(var8, var9, var10, var10, wild$rgba(var11, 235, 235, 235));
            }
         }
      }
   }

   @Unique
   private void wild$drawEdgePressure(RoundedRectRenderer var1, int var2, int var3, int var4, float var5, float var6) {
      float var7 = var3 * (0.04F + Math.abs(ProceduralSoundSynthesizer.handle(17000, 70L)) * 0.12F * var5);
      float var8 = var3 * (0.04F + Math.abs(ProceduralSoundSynthesizer.handle(17001, 80L)) * 0.14F * var5);
      float var9 = var2 * (0.012F + Math.abs(ProceduralSoundSynthesizer.handle(17002, 95L)) * 0.055F * var5);
      float var10 = var2 * (0.012F + Math.abs(ProceduralSoundSynthesizer.handle(17003, 105L)) * 0.055F * var5);
      var1.handle(0.0F, 0.0F, var2, var7, wild$rgba(wild$alpha(55 + (int)(145.0F * var5)), 0, 0, 0));
      var1.handle(0.0F, var3 - var8, var2, var8, wild$rgba(wild$alpha(55 + (int)(155.0F * var5)), 0, 0, 0));
      if (var4 >= 3) {
         var1.handle(0.0F, 0.0F, var9, var3, wild$rgba(wild$alpha(45 + (int)(130.0F * var5)), 0, 0, 0));
         var1.handle(var2 - var10, 0.0F, var10, var3, wild$rgba(wild$alpha(45 + (int)(130.0F * var5)), 0, 0, 0));
      }

      if (var6 > 0.0F) {
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(wild$alpha((int)(150.0F * var6)), 0, 0, 0));
      }
   }

   @Unique
   private void wild$drawBlackout(RoundedRectRenderer var1, int var2, int var3, int var4, float var5, float var6) {
      if (this.wild$panicBlack > 0.01F) {
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(wild$alpha((int)(235.0F * this.wild$panicBlack)), 0, 0, 0));
      }

      if (ProceduralSoundSynthesizer.load()) {
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(wild$alpha(190 + (int)(62.0F * var5)), 0, 0, 0));
      }

      if (var4 >= 4) {
         float var7 = (float)(System.nanoTime() / 2300000L % Math.max(1, var3));
         var1.handle(0.0F, var7, var2, 2.0F, wild$rgba(235, 255, 255, 255));
         var1.handle(0.0F, var7 + 3.0F, var2, 1.0F, wild$rgba(130, 255, 30, 60));
         var1.handle(0.0F, var7 + 5.0F, var2, 1.0F, wild$rgba(130, 40, 90, 255));
         var1.handle(0.0F, 0.0F, var2, var3, wild$rgba(wild$alpha((int)(120.0F + 125.0F * var6)), 0, 0, 0));
      }

      if (var4 >= 5) {
         var1.handle(0.0F, 0.0F, var2, var3, -16777216);
      }
   }

   @Unique
   private static float wild$norm(float var0) {
      return (var0 + 1.0F) * 0.5F;
   }

   @Unique
   private static int wild$byte(float var0) {
      int var1 = (int)(wild$norm(var0) * 255.0F);
      if (var1 < 0) {
         return 0;
      } else {
         return var1 > 255 ? 255 : var1;
      }
   }

   @Unique
   private static int wild$alpha(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }

   @Unique
   private static int wild$rgba(int var0, int var1, int var2, int var3) {
      return (var0 & 0xFF) << 24 | (var1 & 0xFF) << 16 | (var2 & 0xFF) << 8 | var3 & 0xFF;
   }

   @Inject(method = "renderChat", at = @At("HEAD"), cancellable = true, require = 0)
   private void wild$cancelChatDuringCorruption(DrawContext var1, RenderTickCounter var2, CallbackInfo var3) {
      if (ProceduralSoundSynthesizer.update()) {
         var3.cancel();
      }
   }

   @Unique
   private static boolean wild$customHotbarActive() {
      if (!WildClient.prepare()) {
         return false;
      } else if (Hud.previous.process("HotBar") && WildClient.drawProfile() && WildClient.instance != null && WildClient.instance.data != null) {
         Hud var0 = WildClient.instance.data.handle(Hud.class);
         return var0 != null && var0.enabled;
      } else {
         return false;
      }
   }

   @Unique
   private static boolean wild$noRenderPotions() {
      return Removals.handle("Иконки эффектов");
   }
}
