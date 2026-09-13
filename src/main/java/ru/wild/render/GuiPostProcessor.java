package ru.wild.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import ru.wild.WildClient;
import ru.wild.audio.ProceduralSoundSynthesizer;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.render.shader.TransitionShader;
import ru.wild.render.texture.MainFramebufferBinding;
import ru.wild.util.render.RoundedRectRenderer;

public final class GuiPostProcessor {
   private GuiPostProcessor() {
   }
   public static void handle(MinecraftClient var0, float var1) {
      if (var0 != null && var0.getWindow() != null && var0.currentScreen instanceof ClickGuiModernScreen var2 && !ProceduralSoundSynthesizer.update()) {
         int var28 = var0.getWindow().getFramebufferWidth();
         int var4 = var0.getWindow().getFramebufferHeight();
         if (var28 > 0 && var4 > 0) {
            try {
               WildClient.check();
            } catch (Throwable var25) {
               ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "renderer initialization failed", var25);
               return;
            }

            RoundedRectRenderer var5 = WildClient.handle();
            Framebuffer var6 = var0.getFramebuffer();
            if (var5 == null || var6 == null) {
               ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "renderer or main framebuffer unavailable", null);
            } else if (var6.getColorAttachment() instanceof GlTexture var8 && var8.getGlId() > 0) {
               OpenGlStateSnapshot.NetworkState var9 = OpenGlStateSnapshot.handle();
               int var10 = 0;
               int var11 = 0;
               int var12 = 0;
               int var13 = 0;
               boolean var14 = false;
               boolean var22 = false /* VF: Semaphore variable */;

               label316: {
                  label317: {
                     label328: {
                        label329: {
                           try {
                              var22 = true;
                              var10 = MainFramebufferBinding.handle();
                              if (var10 == 0) {
                                 ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "temp fbo unavailable", null);
                                 var22 = false;
                                 break label316;
                              }

                              if (!OpenGlStateSnapshot.handle(36160, var10)) {
                                 MainFramebufferBinding.handle(var10);
                                 var10 = 0;
                                 ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "temp fbo invalid", null);
                                 var22 = false;
                                 break label317;
                              }

                              var11 = GL30.glGetFramebufferAttachmentParameteri(36160, 36064, 36048);
                              if (var11 != 0) {
                                 var12 = GL30.glGetFramebufferAttachmentParameteri(36160, 36064, 36049);
                                 if (var11 == 5890) {
                                    var13 = GL30.glGetFramebufferAttachmentParameteri(36160, 36064, 36050);
                                 }
                              }

                              GL30.glFramebufferTexture2D(36160, 36064, 3553, var8.getGlId(), 0);
                              GL11.glDrawBuffer(36064);
                              if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                                 GL30.glDeleteFramebuffers(var10);
                                 MainFramebufferBinding.handle(var10);
                                 var10 = 0;
                                 ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "temp fbo incomplete", null);
                                 var22 = false;
                                 break label328;
                              }

                              GL11.glViewport(0, 0, var28, var4);
                              GL11.glColorMask(true, true, true, true);
                              GL11.glDisable(3089);
                              GL11.glDisable(2929);
                              GL11.glDisable(2884);
                              GL11.glEnable(3042);

                              try {
                                 FontRegistry.process();
                              } catch (Throwable var24) {
                              }

                              var5.handle(var28, var4);
                              var14 = true;
                              var2.handle(var5, null, var28, var4, var1);
                              var5.process();
                              var14 = false;
                              OpenGlStateSnapshot.handle(36160, var10);
                              GL11.glDrawBuffer(36064);
                              GL11.glViewport(0, 0, var28, var4);

                              try {
                                 TransitionShader.handle().handle(var1);
                              } catch (Throwable var23) {
                                 TransitionShader.handle().process();
                                 ScreenRenderDiagnostics.handle(var2, "theme-shockwave", false, "theme composition failed", var23);
                              }

                              OpenGlStateSnapshot.handle(36160, var10);
                              GL11.glDrawBuffer(36064);
                              GL11.glViewport(0, 0, var28, var4);
                              GuiRippleShader.handle().resolve();
                              ScreenRenderDiagnostics.handle(var2, "modern-gui", true, "post-vanilla composition complete", null);
                              var22 = false;
                           } catch (Throwable var26) {
                              ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "post-vanilla composition failed", var26);
                              var22 = false;
                              break label329;
                           } finally {
                              if (var22) {
                                 if (var14) {
                                    var5.handle();
                                 }

                                 if (var10 != 0 && OpenGlStateSnapshot.handle(36160, var10)) {
                                    if (var11 == 5890) {
                                       GL32.glFramebufferTexture(36160, 36064, var12, var13);
                                    } else if (var11 == 36161) {
                                       GL30.glFramebufferRenderbuffer(36160, 36064, 36161, var12);
                                    } else {
                                       GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                    }
                                 }

                                 OpenGlStateSnapshot.compute(var9);
                                 OpenGlStateSnapshot.resolve(var9);
                              }
                           }

                           if (var14) {
                              var5.handle();
                           }

                           if (var10 != 0 && OpenGlStateSnapshot.handle(36160, var10)) {
                              if (var11 == 5890) {
                                 GL32.glFramebufferTexture(36160, 36064, var12, var13);
                              } else if (var11 == 36161) {
                                 GL30.glFramebufferRenderbuffer(36160, 36064, 36161, var12);
                              } else {
                                 GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                              }
                           }

                           OpenGlStateSnapshot.compute(var9);
                           OpenGlStateSnapshot.resolve(var9);
                           return;
                        }

                        if (var14) {
                           var5.handle();
                        }

                        if (var10 != 0 && OpenGlStateSnapshot.handle(36160, var10)) {
                           if (var11 == 5890) {
                              GL32.glFramebufferTexture(36160, 36064, var12, var13);
                           } else if (var11 == 36161) {
                              GL30.glFramebufferRenderbuffer(36160, 36064, 36161, var12);
                           } else {
                              GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                           }
                        }

                        OpenGlStateSnapshot.compute(var9);
                        OpenGlStateSnapshot.resolve(var9);
                        return;
                     }

                     if (var14) {
                        var5.handle();
                     }

                     if (var10 != 0 && OpenGlStateSnapshot.handle(36160, var10)) {
                        if (var11 == 5890) {
                           GL32.glFramebufferTexture(36160, 36064, var12, var13);
                        } else if (var11 == 36161) {
                           GL30.glFramebufferRenderbuffer(36160, 36064, 36161, var12);
                        } else {
                           GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                        }
                     }

                     OpenGlStateSnapshot.compute(var9);
                     OpenGlStateSnapshot.resolve(var9);
                     return;
                  }

                  if (var14) {
                     var5.handle();
                  }

                  if (var10 != 0 && OpenGlStateSnapshot.handle(36160, var10)) {
                     if (var11 == 5890) {
                        GL32.glFramebufferTexture(36160, 36064, var12, var13);
                     } else if (var11 == 36161) {
                        GL30.glFramebufferRenderbuffer(36160, 36064, 36161, var12);
                     } else {
                        GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                     }
                  }

                  OpenGlStateSnapshot.compute(var9);
                  OpenGlStateSnapshot.resolve(var9);
                  return;
               }

               if (var14) {
                  var5.handle();
               }

               if (var10 != 0 && OpenGlStateSnapshot.handle(36160, var10)) {
                  if (var11 == 5890) {
                     GL32.glFramebufferTexture(36160, 36064, var12, var13);
                  } else if (var11 == 36161) {
                     GL30.glFramebufferRenderbuffer(36160, 36064, 36161, var12);
                  } else {
                     GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                  }
               }

               OpenGlStateSnapshot.compute(var9);
               OpenGlStateSnapshot.resolve(var9);
            } else {
               ScreenRenderDiagnostics.handle(var2, "modern-gui", false, "main color attachment unavailable", null);
            }
         }
      }
   }
}
