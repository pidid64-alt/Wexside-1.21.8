package ru.wild.gui.screen;

import java.util.ArrayList;
import java.util.List;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.HeadlessBotSession;
import ru.wild.config.StudioProfileGate;
import ru.wild.core.CoreDiagnosticsPanel;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.widget.AnimatedUiElement;
import ru.wild.gui.widget.ModuleCardTransform;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.ModulePanelRegistry;
import ru.wild.gui.widget.ModulePlacement;
import ru.wild.gui.widget.ModuleTooltipMeasurer;
import ru.wild.gui.widget.SettingControlRenderer;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.gui.widget.VirtualListLayout;
import ru.wild.gui.widget.VisibilityTransform;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.render.StyledTextRenderer;

public final class ThemeBrowserLayout {
   private final ModuleTooltipMeasurer instance;
   private final StyledTextRenderer data;
   private final CoreDiagnosticsPanel context;

   public List<AnimatedUiElement> handle(ModernClickGuiState var1, ViewportLayoutState var2, ModuleLayoutResult var3, GuiMetrics var4, float var5) {
      ArrayList var6 = new ArrayList();
      this.apply(var6, var2, var4);
      this.handle(var6, var2, var4);
      this.compute(var6, var2, var4);
      this.execute(var6, var2, var4);
      this.resolve(var6, var2, var4);
      this.update(var6, var2, var4);
      this.prepare(var6, var2, var4);
      this.handle(var6, var1, var2, var4);
      float var7 = var1.handle(UiAnimationKeys.animate());
      VisibilityTransform var8 = VisibilityTransform.resolve(var7, var2, var4);
      if (!var1.executeCache() && !var8.visible()) {
         if (var1.validateData()) {
            this.render(var6, var2, var4);
            this.select(var6, var2, var4);
         } else if (var1.collapseConfig()) {
            this.process(var6, var1, var2, var4);
         } else if (!var1.renderScale() && !var1.refreshClient()) {
            this.handle(var6, var1, var3, var2, var4, var5);
         }

         return var6;
      } else {
         int var9 = var6.size();
         if (var1.executeCache() && var7 > 0.35F) {
            this.refresh(var6, var2, var4);
            this.check(var6, var2, var4);
         }

         this.handle(var6, var9, var8, var2, var4);
         int var10 = var6.size();
         this.onTick(var6, var2, var4);
         this.handle(var6, var10, var8, var2, var4);
         return var6;
      }
   }

   public List<AnimatedUiElement> handle(ModernClickGuiState var1, ViewportLayoutState var2, GuiMetrics var3, ThemePaletteRegistry var4) {
      ArrayList var5 = new ArrayList();
      this.handle(var5, var1, var2, var3, var4);
      return var5;
   }

   private void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ViewportLayoutState var3, GuiMetrics var4, ThemePaletteRegistry var5) {
      if (var2.synchronizePoint()) {
         float var6 = var4.process(20.0F);
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var3.projectItem() + var4.save() - var4.process(16.0F) - var6)
               .process(var3.computeResponse() + var4.process(20.0F))
               .compute(var6)
               .resolve(var6)
               .handle(ModernClickGuiState::projectItem)
               .handle()
         );
         VirtualListLayout var7 = VirtualListLayout.handle(var3, var4);
         if (!var2.processWindow().isEmpty()) {
            var1.add(
               AnimatedUiElement.handle()
                  .handle(0)
                  .handle(var7.check())
                  .process(var7.apply())
                  .compute(var7.onTick())
                  .resolve(var7.prepare())
                  .handle(ModernClickGuiState::blendMatrix)
                  .handle()
            );
         }

         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var7.update())
               .process(var7.apply())
               .compute(var7.execute())
               .resolve(var7.prepare())
               .handle(ModernClickGuiState::measure)
               .handle()
         );
         float var8 = var7.process();
         float var9 = var8 + var7.resolve();
         float var10 = var2.submit();
         List var11 = var2.handle(var5);

         for (int var12 = 0; var12 < var11.size(); var12++) {
            int var13 = (Integer)var11.get(var12);
            VirtualListLayout.Bounds var14 = var7.handle(var12, var10);
            if (!(var14.y() + var14.height() < var8) && !(var14.y() > var9)) {
               var1.add(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var14.x())
                     .process(var14.y())
                     .compute(var14.width())
                     .resolve(var14.height())
                     .update(var7.handle())
                     .apply(var8)
                     .execute(var7.compute())
                     .prepare(var9 - var8)
                     .handle(var2x -> var2x.handle(var5.compute().get(var13).compute(), var13))
                     .handle()
               );
            }
         }
      }
   }

   private void handle(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(StudioScreen.apply(var2, var3))
            .process(StudioScreen.execute(var2, var3))
            .compute(StudioScreen.handle(var3))
            .resolve(StudioScreen.update(var2, var3))
            .handle(var0 -> {
               var0.check(false);
               var0.handle((StringSetting)null);
               var0.handle(false);
               var0.matchVector(!var0.cancelIndex());
            })
            .handle()
      );
   }

   private void process(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      float var4 = StudioScreen.onTick(var2, var3);
      float var5 = StudioScreen.check(var2, var3);
      float var6 = StudioScreen.process(var3);
      float var7 = StudioScreen.prepare(var2, var3);
      var1.add(AnimatedUiElement.handle().handle(0).handle(var4).process(var5).compute(var6).resolve(var7).handle(var0 -> var0.handle(1)).handle());
      var1.add(AnimatedUiElement.handle().handle(1).handle(var4).process(var5).compute(var6).resolve(var7).handle(var0 -> var0.handle(-1)).handle());
   }

   private void compute(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      if (StudioProfileGate.handle()) {
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(StudioScreen.compute(var2, var3))
               .process(StudioScreen.resolve(var2, var3))
               .compute(StudioScreen.handle(var2, var3))
               .resolve(StudioScreen.process(var2, var3))
               .handle(var0 -> {
                  var0.check(false);
                  var0.handle((StringSetting)null);
                  var0.handle(false);
                  var0.matchVector(false);
                  var0.render();
               })
               .handle()
         );
      }
   }

   private void resolve(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      float var4 = AutoBuyScreen.compute(var2, var3);
      float var5 = AutoBuyScreen.apply(var2, var3);
      float var6 = AutoBuyScreen.process(var3);
      var1.add(AnimatedUiElement.handle().handle(0).handle(var4).process(var5).compute(var6).resolve(var6).handle(ModernClickGuiState::check).handle());
   }

   private void update(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      float var4 = AutoBuyScreen.compute(var2, var3);
      float var5 = AutoBuyScreen.execute(var2, var3);
      float var6 = AutoBuyScreen.process(var3);
      var1.add(AnimatedUiElement.handle().handle(0).handle(var4).process(var5).compute(var6).resolve(var6).handle(ModernClickGuiState::tick).handle());
   }

   private void apply(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(AutoBuyScreen.handle(var2, var3))
            .process(AutoBuyScreen.process(var2, var3))
            .compute(AutoBuyScreen.handle(var3))
            .resolve(AutoBuyScreen.handle(var3))
            .handle(var0 -> {
               boolean var1x = !var0.synchronizePoint();
               var0.handle(var1x);
               if (var1x) {
                  var0.blendMatrix(true);
               }
            })
            .handle()
      );
   }

   private void execute(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      float var4 = AutoBuyScreen.compute(var2, var3);
      float var5 = AutoBuyScreen.update(var2, var3);
      ModuleCategory[] var6 = ModuleCategory.values();

      for (int var7 = 0; var7 < var6.length; var7++) {
         ModuleCategory var8 = var6[var7];
         float var9 = var5 + var7 * AutoBuyScreen.compute(var3);
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var4)
               .process(var9)
               .compute(AutoBuyScreen.process(var3))
               .resolve(AutoBuyScreen.process(var3))
               .handle(var1x -> var1x.handle(var8))
               .handle()
         );
      }
   }

   private void prepare(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      float var4 = AutoBuyScreen.compute(var2, var3);
      float var5 = AutoBuyScreen.resolve(var2, var3);
      float var6 = AutoBuyScreen.process(var3);
      var1.add(AnimatedUiElement.handle().handle(0).handle(var4).process(var5).compute(var6).resolve(var6).handle(ModernClickGuiState::attachEvent).handle());
   }

   private void check(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(this.context.apply(var2, var3))
            .process(this.context.execute(var2, var3))
            .compute(this.context.update(var3))
            .resolve(this.context.apply(var3))
            .handle(ModernClickGuiState::attachEvent)
            .handle()
      );
   }

   private void onTick(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(CoreDiagnosticsPanel.handle(var2, var3))
            .process(CoreDiagnosticsPanel.process(var2, var3))
            .compute(this.context.process(var3))
            .resolve(CoreDiagnosticsPanel.compute(var2, var3))
            .handle(var0 -> {})
            .handle()
      );
   }

   private void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ViewportLayoutState var3, GuiMetrics var4) {
      if (!var2.encodeVector().isEmpty() || var2.receiveRequest()) {
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var3.render() + var3.tick() - var4.handle(34.0F))
               .process(var3.onTick())
               .compute(var4.handle(34.0F))
               .resolve(var3.select())
               .handle(var0 -> {
                  var0.unload();
                  var0.check(false);
               })
               .handle()
         );
      }

      var1.add(AnimatedUiElement.handle().handle(0).handle(var3.render()).process(var3.onTick()).compute(var3.tick()).resolve(var3.select()).handle(var0 -> {
         var0.check(true);
         var0.onTick(false);
         var0.handle((StringSetting)null);
      }).handle());
   }

   private void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ModuleLayoutResult var3, ViewportLayoutState var4, GuiMetrics var5, float var6) {
      float var7 = var4.drawAnimation();
      float var8 = var4.encodePoint();
      float var9 = var4.animate();
      float var10 = var4.load();
      float var11 = var8 + var10;
      List var12 = var3.process();

      for (int var13 = var12.size() - 1; var13 >= 0; var13--) {
         ModulePlacement var14 = (ModulePlacement)var12.get(var13);
         ModuleCardTransform var15 = ModuleCardTransform.resolve(var2, var14, var5);
         if (var15.visible()) {
            float var16 = var15.pivotY() + (var14.compute() - var15.pivotY()) * var15.scale() + var15.hitTranslateY();
            float var17 = var16 + var14.update() * var15.scale();
            if (!(var16 >= var11) && !(var17 <= var8)) {
               int var18 = var1.size();
               float var19 = var15.pivotX() + (var6 - var15.pivotX()) / var15.scale();
               if (var2.invokeProfile().contains(var14.handle())) {
                  if (ModulePanelRegistry.process(var14.handle())) {
                     ModulePanelRegistry.handle(var1, var2, var14, var5);
                  } else {
                     this.handle(var1, var2, var14, var5, var19, var7, var8, var9, var10);
                  }
               }

               this.handle(var1, var14, var5, var7, var8, var9, var10);

               for (int var20 = var18; var20 < var1.size(); var20++) {
                  AnimatedUiElement var21 = ((AnimatedUiElement)var1.get(var20))
                     .process(var7, var8, var9, var10)
                     .handle(var14.process(), var14.compute(), var14.resolve(), var14.update())
                     .handle(var15.scale(), var15.pivotX(), var15.pivotY(), 0.0F, var15.hitTranslateY());
                  var1.set(var20, var21);
               }
            }
         }
      }
   }

   private void process(List<AnimatedUiElement> var1, ModernClickGuiState var2, ViewportLayoutState var3, GuiMetrics var4) {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         AutoBuy var5 = WildClient.instance.data.handle(AutoBuy.class);
         if (var5 != null && ModulePanelRegistry.process(var5)) {
            int var6 = var1.size();
            ModulePanelRegistry.handle(var1, var2, ModulePanelRegistry.handle(var5, var3, var4), var4);

            for (int var7 = var6; var7 < var1.size(); var7++) {
               var1.set(
                  var7,
                  ((AnimatedUiElement)var1.get(var7))
                     .process(var3.drawAnimation(), var3.encodePoint(), var3.animate(), var3.load())
                     .handle(var3.drawAnimation(), var3.encodePoint(), var3.animate(), var3.load())
               );
            }
         }
      }
   }

   private void select(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(var2.drawAnimation())
            .process(var2.encodePoint())
            .compute(var2.animate())
            .resolve(var2.load())
            .handle(var0 -> {})
            .handle()
      );
   }

   private void refresh(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(this.context.resolve(var2, var3))
            .process(this.context.update(var2, var3))
            .compute(this.context.compute(var3))
            .resolve(this.context.resolve(var3))
            .handle(ModernClickGuiState::select)
            .handle()
      );
   }

   private void render(List<AnimatedUiElement> var1, ViewportLayoutState var2, GuiMetrics var3) {
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(DiagnosticsScreen.handle(var2, var3))
            .process(DiagnosticsScreen.resolve(var2, var3))
            .compute(DiagnosticsScreen.handle(var3))
            .resolve(DiagnosticsScreen.resolve(var3))
            .handle(var0 -> RenderDiagnostics.handle().onTick())
            .handle()
      );
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(DiagnosticsScreen.process(var2, var3))
            .process(DiagnosticsScreen.resolve(var2, var3))
            .compute(DiagnosticsScreen.process(var3))
            .resolve(DiagnosticsScreen.resolve(var3))
            .handle(var0 -> RenderDiagnostics.handle().select())
            .handle()
      );
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(DiagnosticsScreen.compute(var2, var3))
            .process(DiagnosticsScreen.resolve(var2, var3))
            .compute(DiagnosticsScreen.compute(var3))
            .resolve(DiagnosticsScreen.resolve(var3))
            .handle(var0 -> RenderDiagnostics.handle().tick())
            .handle()
      );
   }

   private void handle(List<AnimatedUiElement> var1, ModulePlacement var2, GuiMetrics var3, float var4, float var5, float var6, float var7) {
      Module var8 = var2.handle();
      float var9 = this.instance.handle(var8, var2.resolve(), var3);
      float var10 = var2.compute() + var3.handle(16.0F);
      float var11 = var2.process() + var2.resolve() - var3.handle(16.0F) - var3.handle(24.0F);
      float var12 = var11 - var3.handle(22.0F);
      boolean var13 = ModulePanelRegistry.process(var8) || !var8.apply().isEmpty();
      if (var13) {
         var1.add(
            this.handle(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var12 - var3.handle(3.0F))
                     .process(var10 - var3.handle(3.0F))
                     .compute(var3.handle(20.0F))
                     .resolve(var3.handle(20.0F))
                     .handle(var1x -> var1x.handle(var8)),
                  var4,
                  var5,
                  var6,
                  var7
               )
               .handle()
         );
      }

      var1.add(
         this.handle(
               AnimatedUiElement.handle()
                  .handle(2)
                  .handle(var2.process())
                  .process(var2.compute())
                  .compute(var2.resolve())
                  .resolve(var9)
                  .handle(var1x -> var1x.process(var8)),
               var4,
               var5,
               var6,
               var7
            )
            .handle()
      );
      if (var13) {
         var1.add(
            this.handle(
                  AnimatedUiElement.handle()
                     .handle(1)
                     .handle(var2.process())
                     .process(var2.compute())
                     .compute(var2.resolve())
                     .resolve(var9)
                     .handle(var1x -> var1x.handle(var8)),
                  var4,
                  var5,
                  var6,
                  var7
               )
               .handle()
         );
      }

      var1.add(
         this.handle(
               AnimatedUiElement.handle().handle(0).handle(var2.process()).process(var2.compute()).compute(var2.resolve()).resolve(var9).handle(var1x -> {
                  HeadlessBotSession var2x = var1x.execute();
                  if (var2x != null) {
                     var2x.refresh().handle(var8.displayName, !var2x.refresh().process(var8.displayName));
                  } else if (WildClient.instance != null && WildClient.instance.data != null && WildClient.instance.data.process().contains(var8)) {
                     var8.toggle();
                  }
               }), var4, var5, var6, var7
            )
            .handle()
      );
   }

   private void handle(
      List<AnimatedUiElement> var1, ModernClickGuiState var2, ModulePlacement var3, GuiMetrics var4, float var5, float var6, float var7, float var8, float var9
   ) {
      float var10 = var3.process() + var4.handle(16.0F);
      float var11 = var3.compute() + this.instance.handle(var3.handle(), var3.resolve(), var4) + var4.handle(10.0F);
      float var12 = var3.resolve() - var4.handle(32.0F);

      for (Setting var14 : var3.handle().apply()) {
         if (var14 instanceof FloatSetting var57) {
            var11 += var4.handle(var57.compute());
         } else {
            float var15 = var2.handle(UiAnimationKeys.resolve(var14));
            float var16 = this.instance.handle(var14, var4, var2);
            float var17 = this.instance.handle(var14, var2, var4);
            if (var15 < 0.5F) {
               var11 += (var16 + var17 + var4.handle(12.0F)) * var15;
            } else {
               float var18 = (1.0F - var15) * var4.handle(8.0F);
               if (var14 instanceof ColorSetting var19 && var2.evaluateWorld() == var19) {
                  float var61 = var4.handle(16.0F);
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(SettingControlRenderer.process(var10, var12, var4))
                              .process(SettingControlRenderer.resolve(var11 + var18, var4))
                              .compute(SettingControlRenderer.update(var4))
                              .resolve(SettingControlRenderer.update(var4))
                              .handle(var5x -> this.data.handle(var5x, var14, var5, var10, var12)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
                  float var63 = var2.setupScale();
                  float var26 = var2.synchronizeIndex();
                  float var27 = var2.interpolateTask();
                  float var28 = var2.refreshSource();
                  if (var27 > 1.0F && var28 > 1.0F) {
                     var1.add(
                        this.handle(
                              AnimatedUiElement.handle()
                                 .handle(0)
                                 .handle(var63)
                                 .process(var26)
                                 .compute(var27)
                                 .resolve(var28)
                                 .handle(var2x -> this.data.handle(var2x, var19, var2x.sampleLayer(), var2x.sendWorld())),
                              var6,
                              var7,
                              var8,
                              var9
                           )
                           .handle()
                     );
                  }

                  float var29 = var2.savePlayer();
                  float var30 = var2.runRequest();
                  float var31 = var2.projectFrame();
                  float var32 = var2.releaseData();
                  if (var31 > 1.0F && var32 > 1.0F) {
                     var1.add(
                        this.handle(
                              AnimatedUiElement.handle()
                                 .handle(0)
                                 .handle(var29)
                                 .process(var30)
                                 .compute(var31)
                                 .resolve(var32)
                                 .handle(var2x -> this.data.process(var2x, var19, var2x.sampleLayer(), var2x.sendWorld())),
                              var6,
                              var7,
                              var8,
                              var9
                           )
                           .handle()
                     );
                  }

                  float var33 = var2.runVector();
                  float var34 = var2.synchronizeProvider();
                  float var35 = var2.processPath();
                  float var36 = var2.resetPosition();
                  if (var35 > 1.0F && var36 > 1.0F) {
                     var1.add(
                        this.handle(
                              AnimatedUiElement.handle()
                                 .handle(0)
                                 .handle(var33)
                                 .process(var34)
                                 .compute(var35)
                                 .resolve(var36)
                                 .handle(var2x -> this.data.handle(var2x, var19, var2x.sampleLayer())),
                              var6,
                              var7,
                              var8,
                              var9
                           )
                           .handle()
                     );
                  }

                  float var37 = var2.applyEffect();
                  float var38 = var2.handleCache2();
                  float var39 = var2.advanceSession();
                  float var40 = var2.sampleKey();
                  if (var39 > 1.0F && var40 > 1.0F) {
                     var1.add(
                        this.handle(
                              AnimatedUiElement.handle()
                                 .handle(0)
                                 .handle(var37)
                                 .process(var38)
                                 .compute(var39)
                                 .resolve(var40)
                                 .handle(var2x -> this.data.process(var2x, var19, var2x.sampleLayer())),
                              var6,
                              var7,
                              var8,
                              var9
                           )
                           .handle()
                     );
                  }

                  float var41 = var2.performPlayer();
                  float var42 = var2.loadTask();
                  float var43 = var2.sendPoint();
                  float var44 = var2.submitClient();
                  if (var43 > 1.0F && var44 > 1.0F) {
                     var1.add(
                        this.handle(
                              AnimatedUiElement.handle()
                                 .handle(0)
                                 .handle(var41)
                                 .process(var42)
                                 .compute(var43)
                                 .resolve(var44)
                                 .handle(var2x -> this.data.handle(var2x, var19, var2x.sampleLayer(), false)),
                              var6,
                              var7,
                              var8,
                              var9
                           )
                           .handle()
                     );
                     var1.add(
                        this.handle(
                              AnimatedUiElement.handle()
                                 .handle(1)
                                 .handle(var41)
                                 .process(var42)
                                 .compute(var43)
                                 .resolve(var44)
                                 .handle(var2x -> this.data.handle(var2x, var19, var2x.sampleLayer(), true)),
                              var6,
                              var7,
                              var8,
                              var9
                           )
                           .handle()
                     );
                  }

                  float var45 = var2.stopOption();
                  float var46 = var2.refreshRegion();
                  float var47 = var2.prepareMouse();
                  float var48 = var2.parseContext();
                  if (var47 > 1.0F && var48 > 1.0F) {
                     var1.add(this.handle(AnimatedUiElement.handle().handle(0).handle(var45).process(var46).compute(var47).resolve(var48).handle(var1x -> {
                        var19.handle(var1x.process(var19));
                        var1x.performVector();
                        var1x.scheduleAnimation();
                     }), var6, var7, var8, var9).handle());
                  }

                  float var49 = var2.adaptSession();
                  float var50 = var2.matchProfile();
                  float var51 = var2.advanceEntity();
                  float var52 = var2.collectSession();
                  if (var51 > 1.0F && var52 > 1.0F) {
                     var1.add(this.handle(AnimatedUiElement.handle().handle(0).handle(var49).process(var50).compute(var51).resolve(var52).handle(var1x -> {
                        var1x.apply((ColorSetting)null);
                        var1x.execute("");
                        var1x.update(var19);
                        var1x.apply(String.format("%06X", var19.check() & 16777215));
                     }), var6, var7, var8, var9).handle());
                  }

                  float var53 = var2.attachInput();
                  float var54 = var2.projectPath();
                  float var55 = var2.handleInput();
                  float var56 = var2.dispatchWorld();
                  if (var55 > 1.0F && var56 > 1.0F) {
                     var1.add(this.handle(AnimatedUiElement.handle().handle(0).handle(var53).process(var54).compute(var55).resolve(var56).handle(var1x -> {
                        var1x.update((ColorSetting)null);
                        var1x.apply("");
                        var1x.apply(var19);
                        var1x.execute(Integer.toString(Math.round(var19.animationDraw * 100.0F)));
                     }), var6, var7, var8, var9).handle());
                  }
               } else if (var14 instanceof ChoiceSetting var20) {
                  this.handle(var1, var20, var10, var11 + var18, var12, var16, var4, var6, var7, var8, var9);
               } else if (var14 instanceof ShaderPresetSetting var21) {
                  float var24 = SettingControlRenderer.handle(var21, var10, var12, var4);
                  float var25 = SettingControlRenderer.process(var11 + var18, var4);
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(var24)
                              .process(var25)
                              .compute(SettingControlRenderer.handle(var21, var12, var4))
                              .resolve(SettingControlRenderer.process(var4))
                              .handle(var1x -> var1x.handle(var21)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
                  if (var21.state) {
                     this.handle(var1, var21, var10, var11 + var18, var12, var4, var6, var7, var8, var9);
                  }
               } else if (var14 instanceof ModeSetting var22) {
                  float var58 = SettingControlRenderer.handle(var22, var10, var12, var4);
                  float var62 = SettingControlRenderer.handle(var11 + var18, var4);
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(var58)
                              .process(var62)
                              .compute(SettingControlRenderer.handle(var22, var12, var4))
                              .resolve(SettingControlRenderer.handle(var4))
                              .handle(var1x -> var1x.handle(var22)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
                  if (var22.active) {
                     this.handle(var1, var22, var10, var11 + var18, var12, var4, var6, var7, var8, var9);
                  }
               } else if (var14 instanceof BooleanSetting var23) {
                  float var59 = SettingControlRenderer.resolve(var4);
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(SettingControlRenderer.handle(var10, var12, var4))
                              .process(SettingControlRenderer.compute(var11 + var18, var4))
                              .compute(var59)
                              .resolve(var59)
                              .handle(var5x -> this.data.handle(var5x, var14, var5, var10, var12)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(2)
                              .handle(var10)
                              .process(var11 + var18 - var4.handle(2.0F))
                              .compute(var12)
                              .resolve(var16 + var4.handle(4.0F))
                              .handle(var1x -> var1x.handle(var23)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(1)
                              .handle(var10)
                              .process(var11 + var18 - var4.handle(2.0F))
                              .compute(var12)
                              .resolve(var16 + var4.handle(4.0F))
                              .handle(var1x -> {
                                 if (var23.cache != -1) {
                                    var23.output = !var23.output;
                                    var1x.scheduleAnimation();
                                 }
                              }),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
               } else if (var14 instanceof ColorSetting) {
                  float var60 = SettingControlRenderer.update(var4);
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(SettingControlRenderer.process(var10, var12, var4))
                              .process(SettingControlRenderer.resolve(var11 + var18, var4))
                              .compute(var60)
                              .resolve(var60)
                              .handle(var5x -> this.data.handle(var5x, var14, var5, var10, var12)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
               } else if (var14 instanceof NumberSetting) {
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(var10)
                              .process(var11 + var18 + var4.handle(3.0F))
                              .compute(var12)
                              .resolve(var4.handle(26.0F))
                              .handle(var5x -> this.data.handle(var5x, var14, var5, var10, var12)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
               } else {
                  var1.add(
                     this.handle(
                           AnimatedUiElement.handle()
                              .handle(0)
                              .handle(var10)
                              .process(var11 + var18 - var4.handle(2.0F))
                              .compute(var12)
                              .resolve(var16 + var4.handle(4.0F))
                              .handle(var5x -> this.data.handle(var5x, var14, var5, var10, var12)),
                           var6,
                           var7,
                           var8,
                           var9
                        )
                        .handle()
                  );
               }

               var11 += (var16 + var17 + var4.handle(12.0F)) * var15;
            }
         }
      }
   }

   private void handle(
      List<AnimatedUiElement> var1,
      ChoiceSetting var2,
      float var3,
      float var4,
      float var5,
      float var6,
      GuiMetrics var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      float var12 = var5 * 0.7F;
      float var13 = var3 + var5 - var12;
      float var14 = var7.handle(3.0F);
      float var15 = var7.handle(14.0F);
      float var16 = var7.handle(3.0F);
      float var17 = 0.0F;
      int var18 = 0;

      for (int var19 = 0; var19 < var2.config.size(); var19++) {
         int var20 = var19;
         BooleanSetting var21 = var2.config.get(var20);
         float var22 = ModuleStateHelper.handle(FontRegistry.instance, ModuleStateHelper.handle(var21), 8.0F);
         float var23 = Math.max(var7.handle(18.0F), var22 + var7.handle(8.0F));
         if (var17 > 0.0F && var17 + var23 > var12) {
            var18++;
            var17 = 0.0F;
         }

         float var24 = var13 + var17;
         float var25 = var4 + var7.handle(1.0F) + var18 * (var15 + var16);
         var1.add(
            this.handle(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var24)
                     .process(var25 - var7.handle(1.0F))
                     .compute(var23)
                     .resolve(var15 + var7.handle(2.0F))
                     .handle(var1x -> {
                        var21.process(!var21.resolve());
                        var1x.scheduleAnimation();
                     }),
                  var8,
                  var9,
                  var10,
                  var11
               )
               .handle()
         );
         var1.add(
            this.handle(
                  AnimatedUiElement.handle()
                     .handle(2)
                     .handle(var24)
                     .process(var25 - var7.handle(1.0F))
                     .compute(var23)
                     .resolve(var15 + var7.handle(2.0F))
                     .handle(var1x -> var1x.handle(var21)),
                  var8,
                  var9,
                  var10,
                  var11
               )
               .handle()
         );
         var1.add(
            this.handle(
                  AnimatedUiElement.handle()
                     .handle(1)
                     .handle(var24)
                     .process(var25 - var7.handle(1.0F))
                     .compute(var23)
                     .resolve(var15 + var7.handle(2.0F))
                     .handle(var1x -> {
                        if (var21.cache != -1) {
                           var21.output = !var21.output;
                           var1x.scheduleAnimation();
                        }
                     }),
                  var8,
                  var9,
                  var10,
                  var11
               )
               .handle()
         );
         var17 += var23 + var14;
      }
   }

   private void handle(
      List<AnimatedUiElement> var1, ModeSetting var2, float var3, float var4, float var5, GuiMetrics var6, float var7, float var8, float var9, float var10
   ) {
      float var11 = SettingControlRenderer.handle(var5);
      float var12 = SettingControlRenderer.handle(var3, var5);
      float var13 = var4 + var6.handle(14.0F) + var6.handle(4.0F);
      float var14 = var6.handle(18.0F);

      for (int var15 = 0; var15 < var2.config.size(); var15++) {
         int var16 = var15;
         float var17 = var13 + var6.handle(2.0F) + var15 * var14;
         var1.add(
            this.handle(
                  AnimatedUiElement.handle().handle(0).handle(var12).process(var17).compute(var11).resolve(var14).handle(var2x -> var2x.handle(var2, var16)),
                  var7,
                  var8,
                  var9,
                  var10
               )
               .handle()
         );
      }
   }

   private void handle(
      List<AnimatedUiElement> var1,
      ShaderPresetSetting var2,
      float var3,
      float var4,
      float var5,
      GuiMetrics var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      var2.compute();
      float var11 = SettingControlRenderer.process(var5);
      float var12 = SettingControlRenderer.process(var3, var5);
      float var13 = var4 + var6.handle(18.0F) + var6.handle(5.0F);
      float var14 = SettingControlRenderer.compute(var6);
      float var15 = var6.handle(4.0F);

      for (int var16 = 0; var16 < var2.config.size(); var16++) {
         int var17 = var16;
         float var18 = var13 + var15 + var16 * var14;
         var1.add(
            this.handle(
                  AnimatedUiElement.handle().handle(0).handle(var12).process(var18).compute(var11).resolve(var14).handle(var2x -> var2x.handle(var2, var17)),
                  var7,
                  var8,
                  var9,
                  var10
               )
               .handle()
         );
      }
   }

   private AnimatedUiElement.CacheEntry handle(AnimatedUiElement.CacheEntry var1, float var2, float var3, float var4, float var5) {
      return var1.update(var2).apply(var3).execute(var4).prepare(var5);
   }

   private void handle(List<AnimatedUiElement> var1, int var2, VisibilityTransform var3, ViewportLayoutState var4, GuiMetrics var5) {
      if (var3.visible()) {
         float var6 = CoreDiagnosticsPanel.handle(var4, var5);
         float var7 = CoreDiagnosticsPanel.process(var4, var5);
         float var8 = this.context.process(var5);
         float var9 = CoreDiagnosticsPanel.compute(var4, var5);

         for (int var10 = var2; var10 < var1.size(); var10++) {
            var1.set(
               var10,
               ((AnimatedUiElement)var1.get(var10))
                  .handle(var6, var7, var8, var9)
                  .handle(var3.scale(), var3.pivotX(), var3.pivotY(), var3.translateX(), var3.translateY())
            );
         }
      }
   }
   public ThemeBrowserLayout(ModuleTooltipMeasurer var1, StyledTextRenderer var2, CoreDiagnosticsPanel var3) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
   }
}
