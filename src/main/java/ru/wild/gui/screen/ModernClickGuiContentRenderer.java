package ru.wild.gui.screen;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;
import ru.wild.core.CoreDiagnosticsPanel;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.hud.HologramStatsRenderer;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.PreviewPlayerGenerator;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.gui.widget.FoundryAssetBrowser;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.SurfaceHitResolver;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.render.shader.DiffuseShaderRenderer;
import ru.wild.render.shader.FrutigerAeroShader;
import ru.wild.render.shader.GlacierVeilShader;
import ru.wild.render.shader.GuiRippleShader;
import ru.wild.render.shader.HoloBlurShader;
import ru.wild.render.shader.HologramBackdropShader;
import ru.wild.render.shader.MidnightAzureShader;
import ru.wild.render.shader.ObsidianEmberShader;
import ru.wild.render.shader.PorcelainDawnShader;
import ru.wild.render.shader.SakuraBreezeShader;
import ru.wild.render.shader.VernalSolsticeShader;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;

public final class ModernClickGuiContentRenderer {
   private final ThemeBrowserScreen instance;
   private final AutoBuyScreen data;
   private final StudioScreen context;
   private final AutoBuyContentRenderer config;
   private final CoreDiagnosticsPanel state;
   private final HologramStatsRenderer cache;
   private static final OffscreenRenderTarget output = new OffscreenRenderTarget();

   public FoundryAssetBrowser handle() {
      return this.config.handle();
   }

   public PreviewPlayerGenerator process() {
      return this.config.process();
   }
   public void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ViewportLayoutState var4,
      ModuleLayoutResult var5,
      ThemeRenderContext var6,
      int var7,
      int var8
   ) {
      if (var1 != null && var3 != null && var4 != null && var5 != null && var6 != null && var7 > 0 && var8 > 0) {
         float var9 = var3.resolve();
         if (!(var9 <= 0.001F)) {
            GuiMetrics var10 = var6.update();
            if (var10 != null) {
               RenderDiagnostics.handle().handle(var7, var8);

               try {
                  boolean var11 = var6.handle();
                  boolean var12 = var6.resolve() == ThemePalette.SAKURA_BREEZE;
                  boolean var13 = var6.resolve() == ThemePalette.VERNAL_SOLSTICE;
                  boolean var14 = var6.resolve() == ThemePalette.MIDNIGHT_AZURE;
                  boolean var15 = var6.resolve() == ThemePalette.FRUTIGER_AERO;
                  boolean var16 = var6.resolve() == ThemePalette.PORCELAIN_DAWN;
                  boolean var17 = var6.resolve() == ThemePalette.VELVET_DUSK;
                  boolean var18 = var6.resolve() == ThemePalette.OBSIDIAN_EMBER;
                  boolean var19 = var6.resolve() == ThemePalette.GLACIER_VEIL;
                  boolean var20 = SdfTextRenderer.handle(var11);

                  try {
                     GuiRippleShader.handle()
                        .handle(
                           var4.handle(),
                           var4.process(),
                           var10.resolve(),
                           var10.update(),
                           var10.handle(24.0F),
                           var3.synchronizePoint() ? var4.projectItem() : var4.handle(),
                           var3.synchronizePoint() ? var4.computeResponse() : var4.process(),
                           var3.synchronizePoint() ? var10.save() : 0.0F,
                           var3.synchronizePoint() ? var10.submit() : 0.0F,
                           var10.handle(14.0F)
                        );
                     if (MenuAnimationClock.handle()) {
                        var1.handle(32.0F);
                     }

                     float var21 = (float)(System.currentTimeMillis() % 10000L) / 10000.0F;
                     float var22 = var3.measureColor();
                     var1.update(var9);
                     boolean var68 = false /* VF: Semaphore variable */;

                     try {
                        var68 = true;
                        boolean var23 = false;

                        try {
                           var23 = !var14 && Menu.actionRead.process("Голограмма") && MenuAnimationClock.handle();
                        } catch (Throwable var78) {
                        }

                        if (!var23) {
                           var1.handle(0.0F, 0.0F, var7, var8, 0.0F, 0.92F);
                        }

                        if (var23) {
                           try {
                              var1.compute();
                              HoloBlurShader.handle()
                                 .handle(
                                    var7,
                                    var8,
                                    var3.sampleLayer(),
                                    var3.sendWorld(),
                                    var9,
                                    Menu.dataValidate.compute(),
                                    Menu.scaleRender.compute(),
                                    Menu.clientRefresh.compute(),
                                    Menu.keyFilter.compute(),
                                    Menu.requestAdapt.compute(),
                                    Menu.timerMeasure.compute(),
                                    Menu.vectorEncode.compute(),
                                    Menu.requestReceive.compute(),
                                    Menu.windowProcess.compute(),
                                    Menu.packetSave.compute()
                                 );
                           } catch (Throwable var77) {
                              RenderDiagnostics.handle().process("GuiRenderer.holoBlur", var77);
                           }

                           var1.handle(
                              0.0F,
                              0.0F,
                              var7,
                              var8,
                              var11 ? ThemeColors.handle(255, 255, 255, Math.round(26.0F * var9)) : ThemeColors.handle(0, 0, 0, Math.round(14.0F * var9))
                           );
                        } else {
                           var1.handle(
                              0.0F,
                              0.0F,
                              var7,
                              var8,
                              var11
                                 ? ThemeColors.handle(255, 255, 255, Math.round((var12 ? 22 : (var13 ? 34 : (var15 ? 24 : (var16 ? 26 : 112)))) * var9))
                                 : (
                                    var14
                                       ? ThemeColors.handle(3, 7, 18, Math.round(36.0F * var9))
                                       : (
                                          var17
                                             ? ThemeColors.handle(19, 12, 32, Math.round(36.0F * var9))
                                             : (
                                                var18
                                                   ? ThemeColors.handle(12, 10, 11, Math.round(34.0F * var9))
                                                   : (
                                                      var19
                                                         ? ThemeColors.handle(7, 19, 32, Math.round(36.0F * var9))
                                                         : ThemeColors.handle(0, 0, 0, Math.round(100.0F * var9))
                                                   )
                                             )
                                       )
                                 )
                           );
                        }

                        var1.handle(0.0F, 0.0F, var7, var8, var11 ? ThemeColors.handle(248, 250, 255, 154) : ThemeColors.handle(1, 3, 9, 126));
                        if (var12) {
                           try {
                              var1.compute();
                              SakuraBreezeShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(255, 255, 255, Math.round(4.0F * var9)));
                           } catch (Throwable var76) {
                              RenderDiagnostics.handle().process("GuiRenderer.sakuraBreeze", var76);
                           }
                        }

                        if (var13) {
                           try {
                              var1.compute();
                              VernalSolsticeShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(255, 255, 255, Math.round(5.0F * var9)));
                           } catch (Throwable var75) {
                              RenderDiagnostics.handle().process("GuiRenderer.vernalSolstice", var75);
                           }
                        }

                        if (var14) {
                           try {
                              var1.compute();
                              MidnightAzureShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(3, 7, 18, Math.round(18.0F * var9)));
                           } catch (Throwable var74) {
                              RenderDiagnostics.handle().process("GuiRenderer.midnightAzure", var74);
                           }
                        }

                        if (var15) {
                           try {
                              var1.compute();
                              FrutigerAeroShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(255, 255, 255, Math.round(4.0F * var9)));
                           } catch (Throwable var73) {
                              RenderDiagnostics.handle().process("GuiRenderer.frutigerAero", var73);
                           }
                        }

                        if (var16) {
                           try {
                              var1.compute();
                              PorcelainDawnShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(255, 255, 255, Math.round(5.0F * var9)));
                           } catch (Throwable var72) {
                              RenderDiagnostics.handle().process("GuiRenderer.porcelainDawn", var72);
                           }
                        }

                        if (var17) {
                           try {
                              var1.compute();
                              HologramBackdropShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(19, 12, 32, Math.round(18.0F * var9)));
                           } catch (Throwable var71) {
                              RenderDiagnostics.handle().process("GuiRenderer.velvetDusk", var71);
                           }
                        }

                        if (var18) {
                           try {
                              var1.compute();
                              ObsidianEmberShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(12, 10, 11, Math.round(16.0F * var9)));
                           } catch (Throwable var70) {
                              RenderDiagnostics.handle().process("GuiRenderer.obsidianEmber", var70);
                           }
                        }

                        if (var19) {
                           try {
                              var1.compute();
                              GlacierVeilShader.handle().handle(var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9);
                              var1.compute();
                              var1.handle(0.0F, 0.0F, var7, var8, ThemeColors.handle(7, 19, 32, Math.round(18.0F * var9)));
                           } catch (Throwable var69) {
                              RenderDiagnostics.handle().process("GuiRenderer.glacierVeil", var69);
                           }
                        }

                        String var24 = this.compute();
                        if (!var24.isBlank()) {
                           var1.compute();
                           boolean var25 = DiffuseShaderRenderer.handle(
                              var24, 0.0F, 0.0F, var7, var8, var7, var8, var3.sampleLayer(), var3.sendWorld(), var6.apply(), var9
                           );
                           var1.compute();
                           if (var25) {
                              var1.handle(
                                 0.0F,
                                 0.0F,
                                 var7,
                                 var8,
                                 var11 ? ThemeColors.handle(255, 255, 255, Math.round(10.0F * var9)) : ThemeColors.handle(0, 0, 0, Math.round(16.0F * var9))
                              );
                           }
                        }

                        float var82 = 0.94F + var9 * 0.06F;
                        this.handle(var1, var2, var3, var4, var5, var6, var82, var21, var22, var9, var7, var8);
                        this.cache.handle(var1, var3, var6, var7, var8);
                        var68 = false;
                     } finally {
                        if (var68) {
                           var1.onTick();
                        }
                     }

                     var1.onTick();
                  } finally {
                     SdfTextRenderer.handle(var20);
                  }
               } finally {
                  RenderDiagnostics.handle().prepare();
               }
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ViewportLayoutState var4,
      ModuleLayoutResult var5,
      ThemeRenderContext var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12
   ) {
      GuiMetrics var13 = var6.update();
      ThemeColors var14 = var6.apply();
      float var15 = var3.handle("panel:z:lift", var3.bindSession() ? 1.0F : 0.0F, SpringAnimationSpec.handle());
      if (var3.bindSession()) {
         this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, 1.0F - var15);
         this.handle(var1, var3, var4, var6, var7, var8, var10, var13, var14, var15);
      } else {
         this.handle(var1, var3, var4, var6, var7, var8, var10, var13, var14, 1.0F - var15);
         this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      ViewportLayoutState var3,
      ThemeRenderContext var4,
      float var5,
      float var6,
      float var7,
      GuiMetrics var8,
      ThemeColors var9,
      float var10
   ) {
      float var11 = var2.handle(UiAnimationKeys.tick());
      if (!(var11 <= 0.005F)) {
         float var12 = var3.projectItem() + var8.save() * 0.5F;
         float var13 = var3.computeResponse() + var8.submit() * 0.5F;
         GuiMetrics var14 = var8.compute(var8.compute());
         float var15 = 1.0F;
         float var16 = var2.sampleLayer();
         float var17 = var2.sendWorld();
         SurfaceHitResolver.Bounds var18 = new SurfaceHitResolver.Bounds(
            var3.projectItem(), var3.computeResponse(), var8.save(), var8.submit(), var8.process(14.0F), var15 * var5
         );
         var2.execute(var18.localX(var16));
         var2.prepare(var18.localY(var17));
         var1.handle(var15 * var5, var12, var13);

         try {
            float var19 = var7 * var11 * (1.0F + var10 * 0.3F);
            this.handle(var1, var3.projectItem(), var3.computeResponse(), var8.save(), var8.submit(), var8.process(14.0F), var14, var4, var19, var6);
            this.instance.handle(var1, var2, var3, var4, var6);
            this.handle(var1, var3.projectItem(), var3.computeResponse(), var8.save(), var8.submit(), var8.process(14.0F), var9, var11);
            this.handle(var1, var3.projectItem(), var3.computeResponse(), var8.save(), var8.submit(), var8.process(14.0F), var4, var11, var6, var14);
            this.handle(var1, var2, var3, var8, var9, var6);
         } finally {
            var1.check();
            var2.execute(var16);
            var2.prepare(var17);
         }
      }
   }
   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ViewportLayoutState var4,
      ModuleLayoutResult var5,
      ThemeRenderContext var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      GuiMetrics var13,
      ThemeColors var14,
      float var15
   ) {
      float var16 = var4.handle() + var13.resolve() * 0.5F;
      float var17 = var4.process() + var13.update() * 0.5F;
      float var18 = 1.0F;
      float var19 = var3.sampleLayer();
      float var20 = var3.sendWorld();
      SurfaceHitResolver.Bounds var21 = new SurfaceHitResolver.Bounds(
         var4.handle(), var4.process(), var13.resolve(), var13.update(), var13.handle(24.0F), var7 * var18
      );
      var3.execute(var21.localX(var19));
      var3.prepare(var21.localY(var20));
      var1.handle(var7 * var18, var16, var17);
      boolean var25 = false /* VF: Semaphore variable */;

      try {
         var25 = true;
         float var22 = var10 * (1.0F + var15 * 0.3F);
         this.handle(var1, var4.handle(), var4.process(), var13.resolve(), var13.update(), var13.handle(24.0F), var13, var6, var22, var8);
         this.handle(var1, var2, var3, var4, var5, var6, var8, var11, var12);
         this.handle(var1, var4.handle(), var4.process(), var13.resolve(), var13.update(), var13.handle(24.0F), var14);
         this.handle(var1, var4.handle(), var4.process(), var13.resolve(), var13.update(), var13.handle(24.0F), var6, 1.0F, var8, var13);
         this.handle(var1, var4, var13, var9);
         this.process(var1, var3, var4, var13, var14, var8);
         var25 = false;
      } finally {
         if (var25) {
            var1.check();
            var3.execute(var19);
            var3.prepare(var20);
         }
      }

      var1.check();
      var3.execute(var19);
      var3.prepare(var20);
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, GuiMetrics var4, ThemeColors var5, float var6) {
      boolean var7 = ModernClickGuiInputHandler.process(var3, var4, var2.sampleLayer(), var2.sendWorld());
      var2.compute(var7);
      boolean var8 = var2.runListener();
      float var9 = var2.handle(UiAnimationKeys.fetch(), !var7 && !var8 ? 0.0F : 1.0F, SpringAnimationSpec.onTick());
      float var10 = var2.handle(UiAnimationKeys.measure(), var8 ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.compute();
      var1.handle(
         var3.projectItem(),
         var3.computeResponse(),
         var4.save(),
         var4.submit(),
         var4.process(14.0F),
         var4.process(14.0F),
         var4.process(14.0F),
         var4.process(14.0F)
      );

      try {
         this.handle(
            var1,
            var3.projectItem() + var4.save() - var4.process(7.5F),
            var3.computeResponse() + var4.submit() - var4.process(7.5F),
            var4.process(1.0F),
            Math.max(var9, var10),
            var5
         );
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, GuiMetrics var4, ThemeColors var5, float var6) {
      boolean var7 = ModernClickGuiInputHandler.handle(var3, var4, var2.sampleLayer(), var2.sendWorld());
      var2.process(var7);
      boolean var8 = var2.submitScreen();
      float var9 = var2.handle(UiAnimationKeys.submit(), !var7 && !var8 ? 0.0F : 1.0F, SpringAnimationSpec.onTick());
      float var10 = var2.handle(UiAnimationKeys.unload(), var8 ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.compute();
      var1.handle(var3.handle(), var3.process(), var4.resolve(), var4.update(), var4.handle(24.0F), var4.handle(24.0F), var4.handle(24.0F), var4.handle(24.0F));

      try {
         this.handle(
            var1,
            var3.handle() + var4.resolve() - var4.handle(8.5F),
            var3.process() + var4.update() - var4.handle(8.5F),
            var4.handle(1.0F),
            Math.max(var9, var10),
            var5
         );
      } finally {
         var1.compute();
         var1.apply();
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, ThemeColors var6) {
      float var7 = Math.max(0.0F, Math.min(1.0F, var5));
      if (!(var7 <= 0.001F)) {
         float var8 = var7 * var7 * (3.0F - 2.0F * var7);
         int var9 = ThemeColors.handle(ThemeColors.handle(var6.save(), var6.submit(), 0.5F), Math.round(172.0F * var8));
         float var10 = Math.max(1.0F, var4);

         for (int var11 = 0; var11 < 3; var11++) {
            float var12 = var4 * (2.6F + var11 * 1.7F);
            float var13 = Math.round(var2 - var12);
            float var14 = Math.round(var3 - var11 * var4 * 2.25F);
            var1.handle(var13, var14, Math.max(1.0F, Math.round(var12)), var10, var10 * 0.5F, var9);
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, GuiMetrics var7, ThemeRenderContext var8, float var9, float var10
   ) {
      if (ThemeRenderer.handle().instance.current.process("Тень")) {
         ThemeColors var11 = var8.apply();
         if (var11.unload()) {
            var1.handle(
               var2, var3 + var7.handle(0.9F), var4, var5, var6, var7.handle(9.0F), var7.handle(0.9F), ThemeColors.handle(46, 59, 70, Math.round(16.0F * var9))
            );
            var1.handle(
               var2,
               var3 + var7.handle(2.6F),
               var4,
               var5,
               var6,
               var7.handle(20.0F),
               var7.handle(2.6F),
               ThemeColors.handle(77, 91, 104, Math.round(5.0F * var9))
            );
         } else {
            var1.handle(var2, var3, var4, var5, var6, var7.handle(14.0F), var7.handle(1.0F), ThemeColors.handle(0, 0, 0, Math.round(180.0F * var9)));
            if (var8.resolve() == ThemePalette.MIDNIGHT_AZURE) {
               float var14 = 0.78F + 0.22F * (float)Math.sin(var10 * Math.PI * 2.0);
               var1.handle(
                  var2,
                  var3 + var7.handle(2.0F),
                  var4,
                  var5,
                  var6,
                  var7.handle(44.0F) * var14,
                  var7.handle(5.5F),
                  ThemeColors.handle(var11.save(), Math.round(28.0F * var9 * var14))
               );
               var1.handle(
                  var2,
                  var3,
                  var4,
                  var5,
                  var6,
                  var7.handle(92.0F) * var14,
                  var7.handle(11.0F),
                  ThemeColors.handle(var11.submit(), Math.round(18.0F * var9 * var14))
               );
            } else {
               float var12 = 0.85F + 0.15F * (float)Math.sin(var10 * Math.PI * 2.0);
               int var13 = ThemeColors.handle(var11.submit(), var11.save(), 0.5F);
               var1.handle(
                  var2, var3, var4, var5, var6, var7.handle(60.0F) * var12, var7.handle(8.0F), ThemeColors.handle(var13, Math.round(5.0F * var9 * var12))
               );
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, ThemeRenderContext var7, float var8, float var9, GuiMetrics var10
   ) {
      if (var7.resolve() == ThemePalette.VERNAL_SOLSTICE && !(var8 <= 0.001F)) {
         ThemeColors var11 = var7.apply();
         float var12 = Math.max(0.0F, Math.min(1.0F, var8));
         float var13 = Math.max(var10.handle(80.0F), var4 * 0.22F);
         float var14 = var4 + var13 * 2.0F;
         float var15 = (var9 * 0.075F + var2 * 3.1E-4F + var3 * 1.9E-4F) % 1.0F;
         if (var15 < 0.0F) {
            var15++;
         }

         float var16 = var2 - var13 + var14 * var15;
         int var17 = ThemeColors.handle(var11.submit(), Math.round(42.0F * var12));
         int var18 = ThemeColors.handle(ThemeColors.handle(var11.submit(), ThemeColors.handle(255, 255, 255, 255), 0.42F), Math.round(60.0F * var12));
         var1.compute();
         var1.handle(var2, var3, var4, var5, var6, var6, var6, var6);

         try {
            var1.handle(var16, var3 + var10.handle(1.0F), var13 * 0.5F, Math.max(1.0F, var10.handle(1.1F)), 0.0F, 0, var17);
            var1.handle(var16 + var13 * 0.5F, var3 + var10.handle(1.0F), var13 * 0.5F, Math.max(1.0F, var10.handle(1.1F)), 0.0F, var18, 0);
            var1.process(
               var2 + var10.handle(1.0F),
               var3 + var6 * 0.35F,
               Math.max(1.0F, var10.handle(1.0F)),
               Math.max(1.0F, var5 - var6 * 0.7F),
               0.0F,
               ThemeColors.handle(var11.save(), Math.round(14.0F * var12)),
               ThemeColors.handle(var11.submit(), Math.round(10.0F * var12))
            );
         } finally {
            var1.compute();
            var1.apply();
         }
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, ThemeColors var7) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, 1.0F);
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, ThemeColors var7, float var8) {
      float var9 = Math.max(0.0F, Math.min(1.0F, var8));
      float var10 = Math.round(var2);
      float var11 = Math.round(var3);
      float var12 = Math.max(1.0F, var6 * 0.72F);
      float var13 = Math.max(0.0F, var4 - var12 * 2.0F);
      if (!(var13 <= 0.5F)) {
         int var14 = var7.unload()
            ? ThemeColors.handle(255, 255, 255, Math.round(46.0F * var9))
            : ThemeColors.handle(ThemeColors.handle(ThemeColors.handle(255, 255, 255, 255), var7.save(), 0.16F), Math.round(30.0F * var9));
         int var15 = ThemeColors.handle(var14, 0);
         float var16 = var13 * 0.5F;
         var1.handle(var10 + var12, var11 + 1.0F, var16, 1.0F, 0.0F, var15, var14);
         var1.handle(var10 + var12 + var16, var11 + 1.0F, var16, 1.0F, 0.0F, var14, var15);
      }
   }
   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ViewportLayoutState var4,
      ModuleLayoutResult var5,
      ThemeRenderContext var6,
      float var7,
      int var8,
      int var9
   ) {
      GuiMetrics var10 = var6.update();
      ThemeColors var11 = var6.apply();
      float var12 = var4.handle();
      float var13 = var4.process();
      float var14 = var10.resolve();
      float var15 = var10.update();
      float var16 = var10.handle(24.0F);
      int var17 = var11.unload() ? ThemeColors.handle(255, 255, 255, 212) : ThemeColors.handle(15, 16, 19, 255);
      var1.handle(var12, var13, var14, var15, var16, ModuleStateHelper.execute(var11), var11.unload() ? 0.96F : 0.92F, var17);
      if (var11.unload()) {
         var1.handle(
            var12 + 1.0F,
            var13 + 1.0F,
            Math.max(1.0F, var14 - 2.0F),
            Math.max(1.0F, var15 - 2.0F),
            Math.max(0.0F, var16 - 1.0F),
            ModuleStateHelper.process(var11, 0.96F),
            1.0F
         );
      }

      if (PresetManager.handle().update(LivePreviewRenderer.MENU_PANEL_BG)) {
         boolean var18 = this.handle(var1, LivePreviewRenderer.MENU_PANEL_BG, null, var12, var13, var14, var15, var16, var8, var9, var3, var11, 1.0F);
         if (var18) {
            var1.handle(
               var12,
               var13,
               var14,
               var15,
               var16,
               var11.unload() ? ThemeColors.handle(ModuleStateHelper.execute(var11), 58) : ThemeColors.handle(var11.apply(), this.handle(var11) ? 74 : 42)
            );
         }
      }

      String var34 = this.compute();
      if (!var34.isBlank()) {
         boolean var19 = this.handle(var1, null, var34, var12, var13, var14, var15, var16, var8, var9, var3, var11, 0.94F);
         if (var19) {
            var1.handle(
               var12,
               var13,
               var14,
               var15,
               var16,
               var11.unload() ? ThemeColors.handle(ModuleStateHelper.execute(var11), 54) : ThemeColors.handle(var11.apply(), this.handle(var11) ? 68 : 38)
            );
         }
      }

      this.process(var1, var12, var13, var14, var15, var16, var11, var7);
      this.data.handle(var1, var3, var4, var6);
      this.context.handle(var1, var3, var4, var6);
      float var35 = var3.handle(UiAnimationKeys.animate());
      if (var35 > 0.01F) {
         float var20 = this.state.process(var10);
         float var21 = (var20 + var10.handle(48.0F)) * var35;
         float var22 = 1.0F - var35 * 0.04F;
         float var23 = var4.drawAnimation() + var4.animate() * 0.5F + var21 * 0.5F;
         float var24 = var4.encodePoint() + var4.load() * 0.5F;
         var1.compute();
         var1.handle(
            var4.drawAnimation(),
            var4.encodePoint(),
            var4.animate(),
            var4.load(),
            var10.handle(4.0F),
            var10.handle(4.0F),
            var10.handle(16.0F),
            var10.handle(4.0F)
         );

         try {
            var1.handle(var22, var23, var24);
            var1.handle(var21, 0.0F);
            var1.update(1.0F - var35 * 0.5F);
            boolean var31 = false /* VF: Semaphore variable */;

            try {
               var31 = true;
               this.config.handle(var1, var2, var3, var4, var5, var6);
               var31 = false;
            } finally {
               if (var31) {
                  var1.onTick();
                  var1.prepare();
                  var1.check();
               }
            }

            var1.onTick();
            var1.prepare();
            var1.check();
            var1.handle(
               var4.drawAnimation(),
               var4.encodePoint(),
               var4.animate(),
               var4.load(),
               var11.unload() ? ThemeColors.handle(255, 255, 255, Math.round(74.0F * var35)) : ThemeColors.handle(0, 0, 0, Math.round(60.0F * var35))
            );
         } finally {
            var1.compute();
            var1.apply();
         }
      } else {
         this.config.handle(var1, var2, var3, var4, var5, var6);
      }

      this.state.handle(var1, var3, var4, var6, var35);
   }

   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, ThemeColors var7, float var8) {
      if (Menu.handle(Menu.providerClose)) {
         if (!var7.unload()) {
            var1.compute();
            var1.handle(var2, var3, var4, var5, var6, var6, var6, var6);

            try {
               float var9 = var8 * (float) (Math.PI * 2);
               float var10 = 0.8F + 0.2F * (float)Math.sin(var9 * 0.2);
               float var11 = var4 * 0.8F;
               float var12 = var5 * 0.65F;
               float var13 = Math.min(var11, var12) * 0.5F;
               float var14 = var2 + var4 * 0.06F + (float)Math.cos(var9 * 0.08) * var4 * 0.03F;
               float var15 = var3 + var5 * 0.04F + (float)Math.sin(var9 * 0.06) * var5 * 0.02F;
               var1.handle(var14, var15, var11, var12, var13, var11 * 0.5F, var11 * 0.15F, ThemeColors.handle(var7.save(), Math.round(3.0F * var10)));
               float var16 = 0.75F + 0.25F * (float)Math.sin(var9 * 0.25 + 2.094F);
               float var17 = var4 * 0.7F;
               float var18 = var5 * 0.6F;
               float var19 = Math.min(var17, var18) * 0.5F;
               float var20 = var2 + var4 * 0.35F + (float)Math.cos(var9 * 0.1 + 1.5) * var4 * 0.05F;
               float var21 = var3 + var5 * 0.45F + (float)Math.sin(var9 * 0.07 + 0.8F) * var5 * 0.04F;
               var1.handle(var20, var21, var17, var18, var19, var17 * 0.45F, var17 * 0.1F, ThemeColors.handle(var7.submit(), Math.round(2.0F * var16)));
            } finally {
               var1.compute();
               var1.apply();
            }
         }
      }
   }
   private boolean handle(
      RoundedRectRenderer var1,
      LivePreviewRenderer var2,
      String var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      ModernClickGuiState var11,
      ThemeColors var12,
      float var13
   ) {
      output.handle(var9, var10);
      if (!output.apply()) {
         return false;
      }

      var1.compute();
      OpenGlStateSnapshot.NetworkState var15 = OpenGlStateSnapshot.handle();
      float[] var16 = new float[4];
      GL11.glGetFloatv(3106, var16);
      boolean var22 = false /* VF: Semaphore variable */;

      boolean var14;
      try {
         var22 = true;
         output.handle();
         GL11.glDisable(3089);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16384);
         var14 = var3 != null
            ? DiffuseShaderRenderer.handle(var3, var4, var5, var6, var7, var9, var10, var11.sampleLayer(), var11.sendWorld(), var12, var13)
            : DiffuseShaderRenderer.handle(var2, var4, var5, var6, var7, var9, var10, var11.sampleLayer(), var11.sendWorld(), var12, var13);
         var22 = false;
      } finally {
         if (var22) {
            GL11.glClearColor(var16[0], var16[1], var16[2], var16[3]);
            OpenGlStateSnapshot.compute(var15);
         }
      }

      GL11.glClearColor(var16[0], var16[1], var16[2], var16[3]);
      OpenGlStateSnapshot.compute(var15);
      if (!var14) {
         return false;
      }

      var1.compute();
      float var17 = var4 / Math.max(1.0F, var9);
      float var18 = 1.0F - var5 / Math.max(1.0F, var10);
      float var19 = (var4 + var6) / Math.max(1.0F, var9);
      float var20 = 1.0F - (var5 + var7) / Math.max(1.0F, var10);
      var1.process(output.compute(), var4, var5, var6, var7, var17, var18, var19, var20, var8);
      return true;
   }

   private String compute() {
      try {
         return Menu.configCollapse.tick();
      } catch (Throwable var2) {
         return "";
      }
   }

   private boolean handle(ThemeColors var1) {
      return var1 != null && (var1.save() & 16777215) == 61695 && (var1.submit() & 16777215) == 17663;
   }
   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (Menu.handle(Menu.presetSave)) {
         var1.compute();
         var1.handle(var2, var3, var4, var5, var6, var6, var6, var6);
         boolean var26 = false /* VF: Semaphore variable */;

         try {
            var26 = true;
            long var8 = (long)(var7 * 10000.0F);
            float var10 = 40.0F;
            float var11 = 40.0F;
            int var12 = (int)Math.ceil(var4 / var10) + 1;
            int var13 = (int)Math.ceil(var5 / var11) + 1;

            for (int var14 = 0; var14 < var13; var14++) {
               for (int var15 = 0; var15 < var12; var15++) {
                  long var16 = var8 + var15 * 73856093L + var14 * 19349663L ^ 25214903917L;
                  var16 = var16 * 6364136223846793005L + 1442695040888963407L;
                  int var18 = (int)(var16 >>> 48 & 15L);
                  if (var18 <= 5) {
                     int var19 = 3 + (var18 & 3);
                     float var20 = var2 + var15 * var10 + (float)(var16 >>> 32 & 31L) - 16.0F;
                     float var21 = var3 + var14 * var11 + (float)(var16 >>> 16 & 31L) - 16.0F;
                     float var22 = 1.0F + (var18 & 1);
                     int var23 = (var18 & 1) == 0 ? ThemeColors.handle(255, 255, 255, var19) : ThemeColors.handle(0, 0, 0, var19 + 1);
                     var1.handle(var20, var21, var22, var22, 0.5F, var23);
                  }
               }
            }

            var26 = false;
         } finally {
            if (var26) {
               var1.compute();
               var1.apply();
            }
         }

         var1.compute();
         var1.apply();
      }
   }
   private void handle(RoundedRectRenderer var1, ViewportLayoutState var2, GuiMetrics var3, float var4) {
      if (!(var4 <= 0.0F) && !(var4 >= 1.0F)) {
         float var5 = var2.handle();
         float var6 = var2.process();
         float var7 = var3.resolve();
         float var8 = var3.update();
         float var9 = var3.handle(24.0F);
         float var10 = var7 * 0.18F;
         float var11 = var5 + (var7 + var10 * 2.0F) * var4 - var10;
         float var12 = Math.max(var5, var11 - var10 * 0.5F);
         float var13 = Math.min(var5 + var7, var11 + var10 * 0.5F);
         if (!(var13 <= var12)) {
            float var14 = (float)Math.sin(var4 * Math.PI);
            float var15 = 0.08F * var14;
            float var16 = var4 * 360.0F;
            int var17 = ModuleStateHelper.process(var16, 0.7F, 0.6F, var15);
            int var18 = ModuleStateHelper.process((var16 + 60.0F) % 360.0F, 0.7F, 0.6F, var15 * 0.5F);
            var1.compute();
            var1.handle(var5, var6, var7, var8, var9, var9, var9, var9);
            boolean var21 = false /* VF: Semaphore variable */;

            try {
               var21 = true;
               var1.handle(var12, var6, var13 - var12, var8, var17, var18);
               var21 = false;
            } finally {
               if (var21) {
                  var1.compute();
                  var1.apply();
               }
            }

            var1.compute();
            var1.apply();
         }
      }
   }
   public ModernClickGuiContentRenderer(
      ThemeBrowserScreen var1, AutoBuyScreen var2, StudioScreen var3, AutoBuyContentRenderer var4, CoreDiagnosticsPanel var5, HologramStatsRenderer var6
   ) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
   }
}
