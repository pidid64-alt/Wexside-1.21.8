package ru.wild.core;

import java.util.ArrayList;
import net.minecraft.client.MinecraftClient;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.gui.hud.OverlayDebugRenderer;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.screen.ViewportLayoutState;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.VisibilityTransform;
import ru.wild.profile.Profile;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.shader.AvatarShader;
import ru.wild.render.texture.GigachadTexture;
import ru.wild.render.texture.TextureFilterState;
import ru.wild.util.math.DampedFloatTracker;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RenderRuntimeContext;
import ru.wild.util.render.RoundedRectRenderer;

public final class CoreDiagnosticsPanel {
   private static final float instance = 330.0F;
   private static final float data = 18.0F;
   private static final float context = 108.0F;
   private static final float config = 126.0F;
   private static final float state = 32.0F;
   private static final float cache = 32.0F;
   private static final float output = 10.0F;
   private static final float current = 34.0F;
   private static final float active = 7.0F;
   private static final float mode = 56.0F;
   private static final float selection = 8.0F;
   private static final float enabled = 12.0F;
   private static final float renderer = 24.0F;
   private static final float handler = 8.0F;
   private static final float animationDraw = 4.0F;
   private static final float pointEncode = 4.0F;
   private static final float animator = 9.0F;
   private static final int source = 4;
   private static final int target = 5;
   private static final String pending = "UID";
   private static final String previous = "SYSTEM";
   private static final String latest = "SHADER PIPELINE";
   private static final String summary = "Тема";
   private static final String matrixBlend = "Модули";
   private static final String vectorMatch = "Wild Core";
   private static final String itemProject = "Build";
   private static final String responseCompute = "Shader Stage";
   private static final String providerFetch = "Shader Exception";
   private static final String profileDraw = "CFI chain";
   private static final String vectorPerform = "Frames";
   private static final String eventAttach = "Anomalies";
   private static final String serverRead = "Texture Units";
   private static final String positionAdvance = "Matrices";
   private static final String frameCheck = "Mixin policy";
   private static final String moduleCollect = "Диагностика";
   private static final String providerClose = "Закрыть";
   private static final String presetSave = handle("wild-1.21.8-1787661348375");
   private static final RenderRuntimeContext windowConvert = new RenderRuntimeContext();
   private static final SpringAnimationSpec presetWrite = SpringAnimationSpec.render();
   private static final SpringAnimationSpec colorMeasure = SpringAnimationSpec.render();
   private static final SpringAnimationSpec animationSchedule = SpringAnimationSpec.render();
   private static final SpringAnimationSpec rendererScan = SpringAnimationSpec.drawAnimation();
   private final DiagnosticSnapshot sourceBuild = new DiagnosticSnapshot();
   private final DampedFloatTracker outputCollapse = new DampedFloatTracker(0.0F);
   private final DampedFloatTracker profileInvoke = new DampedFloatTracker(0.0F);
   private final DampedFloatTracker sourceSchedule = new DampedFloatTracker(0.0F);
   private String timerRender = "0";
   private String scaleSave = "0";
   private String colorCompute = "";
   private String scaleAdapt = "CORRUPTED";
   private int textureRun = Integer.MIN_VALUE;
   private int indexBind = Integer.MIN_VALUE;
   public void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ViewportLayoutState var3, ThemeRenderContext var4, float var5) {
      float var6 = !var2.filterMatrix() && var2.executeCache() ? 1.0F : 0.0F;
      VisibilityTransform var7 = VisibilityTransform.resolve(var5, var3, var4.update());
      float var8 = var7.alpha();
      if (!var7.visible()) {
         windowConvert.apply();
      } else {
         float var9 = compute(this.outputCollapse.handle(var6, var6 > 0.0F ? presetWrite : rendererScan));
         float var10 = compute(this.profileInvoke.handle(var6, var6 > 0.0F ? colorMeasure : rendererScan));
         float var11 = compute(this.sourceSchedule.handle(var6, var6 > 0.0F ? animationSchedule : rendererScan));
         RenderDiagnostics.handle().handle(this.sourceBuild);
         this.handle();
         GuiMetrics var12 = var4.update();
         ThemeColors var13 = var4.apply();
         float var14 = handle(var3, var12);
         float var15 = process(var3, var12);
         float var16 = handle(var12);
         float var17 = compute(var3, var12);
         float var18 = handle(var12, var17);
         var1.update(var8);
         var1.handle(var7.translateX(), var7.translateY());
         boolean var32 = false /* VF: Semaphore variable */;

         try {
            var32 = true;
            var1.handle(var7.scale(), var7.pivotX(), var7.pivotY());

            try {
               this.handle(var1, var12, var13, var14, var15, var16, var17, var12.handle(14.0F), var8);
               var1.compute();
               var1.handle(var14, var15, var16, var17, var12.handle(14.0F), var12.handle(14.0F), var12.handle(14.0F), var12.handle(14.0F));

               try {
                  float var19 = handle(var9, 0.0F, 0.72F);
                  var1.update(var19);
                  var1.handle(var12.handle(-10.0F) * (1.0F - var19), 0.0F);

                  try {
                     this.handle(var1, var2, var12, var13, var14, var15, var16, var18, var8 * var19);
                  } finally {
                     var1.prepare();
                     var1.onTick();
                  }

                  float var20 = handle(var10, 0.22F, 0.92F);
                  var1.update(var20);
                  var1.handle(var12.handle(12.0F) * (1.0F - var20), 0.0F);

                  try {
                     this.process(var1, var2, var12, var13, var14, var15, var16, var17, var18);
                  } finally {
                     var1.prepare();
                     var1.onTick();
                  }

                  float var21 = handle(var11, 0.42F, 1.0F);
                  var1.update(var21);
                  var1.handle(0.0F, var12.handle(10.0F) * (1.0F - var21));
                  boolean var51 = false /* VF: Semaphore variable */;

                  try {
                     var51 = true;
                     this.handle(var1, var2, var12, var13, var3);
                     var51 = false;
                  } finally {
                     if (var51) {
                        var1.prepare();
                        var1.onTick();
                     }
                  }

                  var1.prepare();
                  var1.onTick();
               } finally {
                  var1.compute();
                  var1.apply();
               }
            } finally {
               var1.check();
            }

            var32 = false;
         } finally {
            if (var32) {
               var1.prepare();
               var1.onTick();
            }
         }

         var1.prepare();
         var1.onTick();
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      if (!var3.unload()) {
         var1.handle(var4, var5, var6, var7, var8, var2.handle(30.0F) * var9, var2.handle(6.0F), ThemeColors.handle(0, 0, 0, Math.round(140.0F * var9)));
      }

      var1.handle(var4, var5, var6, var7, var8, var3.unload() ? 0.92F : 0.86F);
      int var10 = var3.unload()
         ? ThemeColors.handle(255, 255, 255, 234)
         : ThemeColors.handle(ThemeColors.handle(6, 8, 15, 246), ThemeColors.handle(var3.submit(), 118), 0.12F);
      var1.handle(var4, var5, var6, var7, var8, var10);
      int var11 = var3.unload() ? ThemeColors.handle(255, 255, 255, 150) : ThemeColors.handle(var3.save(), 38);
      var1.handle(var4, var5, var6, var7, var8, var11, Math.max(1.0F, var2.handle(1.0F)));
      var1.handle(
         var4 + var2.handle(1.0F),
         var5 + var2.handle(1.0F),
         Math.max(1.0F, var6 - var2.handle(2.0F)),
         Math.max(1.0F, var7 - var2.handle(2.0F)),
         Math.max(0.0F, var8 - var2.handle(1.0F)),
         var3.unload() ? ThemeColors.handle(255, 255, 255, 70) : var3.select(),
         0.5F
      );
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, float var5, float var6, float var7, float var8, float var9
   ) {
      float var10 = var3.handle(18.0F);
      float var11 = resolve(var5 + var10);
      float var12 = resolve(var6 + var10);
      float var13 = resolve(var7 - var10 * 2.0F);
      float var14 = resolve(var8 - var3.handle(8.0F));
      float var15 = var3.handle(12.0F);
      this.handle(var1, var3, var4, var11, var12, var13, var14, var15);
      float var16 = resolve(process(var13 * 0.26F, var3.handle(64.0F), var3.handle(72.0F)));
      float var17 = resolve(var11 + var3.handle(16.0F));
      float var18 = resolve(var12 + (var14 - var16) * 0.5F);
      this.handle(var1, var3, var4, var17, var18, var16, var9);
      int var19 = this.sourceBuild.vectorMatch == 0 ? var4.handle() : var4.process();
      float var20 = this.handle(var1, var2, var3, var4, var11, var12, var13, var14, var19);
      float var21 = var18 + var16 * 0.5F;
      float var22 = resolve(var17 + var16 + var3.handle(16.0F));
      float var23 = resolve(var22 + var3.handle(9.0F));
      float var24 = Math.max(var3.handle(56.0F), var20 - var3.handle(12.0F) - var23);
      var1.process(
         var22,
         resolve(var21 - var3.handle(15.0F)),
         var3.handle(1.0F),
         var3.handle(10.0F),
         var3.handle(1.0F),
         ThemeColors.handle(var4.save(), 255),
         ThemeColors.handle(var4.submit(), 255)
      );
      this.handle(
         var1,
         var2,
         var3,
         FontRegistry.config,
         var23,
         resolve(var21 - var3.handle(20.0F)),
         var3.handle(20.0F),
         16.0F,
         Profile.getUsername(),
         ModuleStateHelper.handle(var4),
         var24
      );
      this.handle(
         var1,
         var2,
         var3,
         FontRegistry.instance,
         var23,
         resolve(var21 + var3.handle(2.0F)),
         var3.handle(15.0F),
         8.0F,
         "UID " + this.timerRender,
         ModuleStateHelper.process(var4),
         var24
      );
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8) {
      if (var3.unload()) {
         var1.handle(var4, var5 + var2.handle(2.0F), var6, var7, var8, var2.handle(12.0F), var2.handle(1.5F), ThemeColors.handle(46, 59, 70, 20));
         int var14 = ThemeColors.handle(255, 255, 255, 240);
         int var15 = ThemeColors.handle(var14, ThemeColors.handle(var3.save(), 255), 0.14F);
         int var16 = ThemeColors.handle(var14, ThemeColors.handle(var3.submit(), 255), 0.09F);
         int var17 = ThemeColors.handle(var14, ThemeColors.handle(var3.save(), 255), 0.035F);
         var1.handle(var4, var5, var6, var7, var8, var15, var17, var16, var17);
         var1.process(var4, var5, var6, var7 * 0.42F, var8, var8, 0.0F, 0.0F, ThemeColors.handle(255, 255, 255, 140), ThemeColors.handle(255, 255, 255, 0));
         var1.handle(var4, var5, var6, var7, var8, ThemeColors.handle(var3.save(), 44), Math.max(1.0F, var2.handle(0.9F)));
      } else {
         var1.handle(var4, var5 + var2.handle(3.0F), var6, var7, var8, var2.handle(22.0F), var2.handle(3.0F), ThemeColors.handle(0, 0, 0, 128));
         int var9 = ThemeColors.handle(9, 12, 21, 240);
         int var10 = ThemeColors.handle(var9, ThemeColors.handle(var3.save(), 255), 0.3F);
         int var11 = ThemeColors.handle(var9, ThemeColors.handle(var3.save(), 255), 0.09F);
         int var12 = ThemeColors.handle(var9, ThemeColors.handle(var3.submit(), 255), 0.24F);
         int var13 = ThemeColors.handle(var9, ThemeColors.handle(var3.submit(), 255), 0.06F);
         var1.handle(var4, var5, var6, var7, var8, var10, var11, var12, var13);
         var1.process(var4, var5, var6, var7 * 0.46F, var8, var8, 0.0F, 0.0F, ThemeColors.handle(var3.load(), 18), ThemeColors.handle(0, 0, 0, 0));
         var1.handle(var4, var5, var6, var7, var8, ThemeColors.handle(var3.save(), 42), Math.max(1.0F, var2.handle(0.9F)));
         var1.handle(
            var4 + var2.handle(1.0F),
            var5 + var2.handle(1.0F),
            Math.max(1.0F, var6 - var2.handle(2.0F)),
            Math.max(1.0F, var7 - var2.handle(2.0F)),
            Math.max(0.0F, var8 - var2.handle(1.0F)),
            var3.select(),
            0.5F
         );
      }
   }

   private float handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, float var5, float var6, float var7, float var8, int var9
   ) {
      String var10 = process(this.sourceBuild.data);
      float var11 = Math.min(var7 * 0.3F, ModuleStateHelper.handle(var3, FontRegistry.config, var10, 8.0F));
      float var12 = var3.handle(22.0F);
      float var13 = resolve(var11 + var3.handle(28.0F));
      float var14 = resolve(var5 + var7 - var3.handle(14.0F) - var13);
      float var15 = resolve(var6 + (var8 - var12) * 0.5F);
      boolean var16 = ModuleStateHelper.handle(var2, var14, var15, var13, var12);
      float var17 = var2.handle("profile:chip:hover", var16 ? 1.0F : 0.0F, SpringAnimationSpec.select());
      int var18 = ThemeColors.handle(var9, Math.round((var4.unload() ? 24.0F : 34.0F) + 18.0F * var17));
      var1.handle(var14, var15, var13, var12, var12 * 0.5F, var18);
      var1.handle(var14, var15, var13, var12, var12 * 0.5F, ThemeColors.handle(var9, Math.round(96.0F + 60.0F * var17)), var3.handle(0.6F));
      var1.process(var14 + var3.handle(10.0F), var15 + var12 * 0.5F, var3.handle(2.2F), 0.0F, 1.0F, ThemeColors.handle(var9, 232));
      int var19 = var4.unload() ? var9 : ThemeColors.handle(var9, var4.load(), 0.22F);
      this.handle(var1, var2, var3, FontRegistry.config, var14 + var3.handle(17.0F), var15, var12, 8.0F, var10, var19, var13 - var3.handle(24.0F));
      return var14;
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7) {
      float var8 = var6 * 0.5F;
      float var9 = var4 + var8;
      float var10 = var5 + var8;
      var1.handle(
         var4 - var2.handle(2.0F),
         var5 - var2.handle(2.0F),
         var6 + var2.handle(4.0F),
         var6 + var2.handle(4.0F),
         var8 + var2.handle(4.0F),
         var2.handle(20.0F),
         var2.handle(2.5F),
         ThemeColors.handle(var3.save(), var3.unload() ? 44 : 92)
      );
      int var11 = GigachadTexture.handle();
      if (var11 > 0) {
         var1.process(var9, var10, var8, 0.0F, 1.0F, var3.unload() ? ThemeColors.handle(244, 246, 250, 255) : ThemeColors.handle(9, 12, 20, 255));
         var1.compute();
         AvatarShader.handle(var4, var5, var6, var11, var3.save(), var3.submit(), var7, var3.unload());
      } else {
         var1.process(
            var9, var10, var8 + var2.handle(2.5F), 0.0F, 1.0F, var3.unload() ? ThemeColors.handle(255, 255, 255, 250) : ThemeColors.handle(6, 9, 16, 255)
         );
         var1.process(var9, var10, var8 + var2.handle(1.0F), 0.0F, 1.0F, ThemeColors.handle(var3.save(), var3.unload() ? 150 : 224));
         var1.process(
            var9, var10, var8 - var2.handle(0.5F), 0.0F, 1.0F, var3.unload() ? ThemeColors.handle(250, 251, 254, 255) : ThemeColors.handle(13, 18, 30, 255)
         );
         OverlayDebugRenderer.handle(
            var1,
            var2,
            var9,
            var10 + var2.handle(0.6F),
            var2.handle(1.12F),
            ModuleStateHelper.resolve(var3),
            ThemeColors.handle(var3.submit(), var3.unload() ? 28 : 62)
         );
      }
   }

   private void process(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, float var5, float var6, float var7, float var8, float var9
   ) {
      float var10 = handle(var5, var3);
      float var11 = handle(var6, var3, var9);
      float var12 = process(var7, var3);
      float var13 = handle(var6, var8, var3, var9);
      float var14 = execute(var3);
      if (var14 <= var13 + var3.handle(1.0F)) {
         windowConvert.apply();
      }

      windowConvert.resolve(7.5F);
      windowConvert.handle(true);
      windowConvert.handle(Math.max(var14, var13), var13);
      windowConvert.compute();
      float var15 = process(windowConvert.prepare(), Math.min(0.0F, windowConvert.check()), 0.0F);
      float var16 = var3.handle(10.0F);
      var1.compute();
      var1.handle(var10, var11, var12, var13, var16, var16, var16, var16);

      try {
         float var17 = resolve(var11 + var3.handle(4.0F) + var15);
         var17 = this.compute(var1, var2, var3, var4, var10, var11, var12, var13, var17);
         var17 = this.handle(var1, var3, var4, var10, var11, var12, var13, var17, "SYSTEM");
         var17 = this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "Тема", var2.adaptRequest().name(), var4.save(), 0);
         var17 = this.handle(
            var1,
            var2,
            var3,
            var4,
            var10,
            var11,
            var12,
            var13,
            var17,
            "Wild Core",
            this.sourceBuild.vectorMatch == 0 ? process(this.sourceBuild.data) : process(this.sourceBuild.state),
            this.sourceBuild.vectorMatch == 0 ? var4.save() : var4.process(),
            1
         );
         var17 = this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "Build", presetSave, var4.submit(), 2);
         var17 = this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "Matrices", this.update(), this.handle(var4), 3);
         var17 = resolve(var17 + var3.handle(8.0F));
         var17 = this.handle(var1, var3, var4, var10, var11, var12, var13, var17, "SHADER PIPELINE");
         var17 = this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "Shader Stage", process(this.sourceBuild.animationDraw), var4.save(), 4);
         var17 = this.handle(
            var1,
            var2,
            var3,
            var4,
            var10,
            var11,
            var12,
            var13,
            var17,
            "Shader Exception",
            process(this.sourceBuild.pointEncode),
            "0".equals(this.sourceBuild.source) ? var4.submit() : var4.process(),
            5
         );
         var17 = this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "CFI chain", process(this.sourceBuild.context), var4.save(), 6);
         var17 = this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "Texture Units", this.apply(), var4.save(), 7);
         this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var17, "Mixin policy", process(this.sourceBuild.renderer), var4.submit(), 8);
      } finally {
         var1.compute();
         var1.apply();
      }

      this.process(var1, var3, var4, var10, var11, var12, var13, var14, var15);
      this.handle(var1, var2, var3, var4, var10, var11, var12, var13, var14, var15);
   }

   public static void handle(float var0) {
      float var1 = Math.min(0.0F, windowConvert.check());
      windowConvert.handle(var1 * Math.max(0.0F, Math.min(1.0F, var0)));
   }

   private float compute(
      RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, float var5, float var6, float var7, float var8, float var9
   ) {
      float var10 = var3.handle(56.0F);
      if (var9 + var10 >= var6 - var3.handle(3.0F) && var9 <= var6 + var8 + var3.handle(3.0F)) {
         float var11 = resolve(var5 + var3.handle(4.0F));
         float var12 = Math.max(var3.handle(120.0F), var7 - var3.handle(13.0F));
         float var13 = var3.handle(8.0F);
         float var14 = resolve((var12 - var13 * 2.0F) / 3.0F);
         float var15 = resolve(var11 + (var14 + var13) * 2.0F);
         int var16 = this.sourceBuild.vectorMatch == 0 ? var4.handle() : var4.process();
         this.handle(var1, var2, var3, var4, var11, var9, var14, var10, this.scaleSave, "Модули", var4.save(), 0);
         this.handle(var1, var2, var3, var4, resolve(var11 + var14 + var13), var9, var14, var10, process(this.sourceBuild.latest), "Frames", var4.submit(), 1);
         this.handle(
            var1,
            var2,
            var3,
            var4,
            var15,
            var9,
            Math.max(var3.handle(40.0F), var11 + var12 - var15),
            var10,
            process(this.sourceBuild.previous),
            "Anomalies",
            var16,
            2
         );
      }

      return resolve(var9 + var10 + var3.handle(12.0F));
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      String var10,
      int var11,
      int var12
   ) {
      float var13 = resolve(var5);
      float var14 = resolve(var6);
      float var15 = handle(var13, var7);
      float var16 = handle(var14, var8);
      boolean var17 = ModuleStateHelper.handle(var2, var13, var14, var15, var16);
      float var18 = var2.handle("profile:tile:hover:" + var12, var17 ? 1.0F : 0.0F, SpringAnimationSpec.select());
      float var19 = process(var18);
      var1.handle(0.0F, -var3.handle(0.9F) * var19);
      var1.handle(1.0F + var19 * 0.009F, var13 + var15 * 0.5F, var14 + var16 * 0.5F);

      try {
         float var20 = var3.handle(10.0F);
         int var21 = var4.unload()
            ? ThemeColors.handle(ModuleStateHelper.handle(var4, 0.16F), ThemeColors.handle(var11, 30), 0.1F + var19 * 0.26F)
            : ThemeColors.handle(ThemeColors.handle(8, 12, 23, 186), ThemeColors.handle(var11, 58), 0.16F + var19 * 0.16F);
         if (var19 > 0.01F) {
            var1.handle(var13, var14, var15, var16, var20, var3.handle(8.0F) * var19, var3.handle(1.4F), ThemeColors.handle(var11, Math.round(30.0F * var19)));
         }

         var1.handle(var13, var14, var15, var16, var20, var21);
         var1.handle(
            var13,
            var14,
            var15,
            var16,
            var20,
            ThemeColors.handle(var4.unload() ? ThemeColors.handle(255, 255, 255, 70) : var4.select(), ThemeColors.handle(var11, 150), var19),
            var3.handle(0.55F + var19 * 0.3F)
         );
         this.handle(
            var1,
            var2,
            var3,
            FontRegistry.config,
            resolve(var13 + var3.handle(12.0F)),
            resolve(var14 + var3.handle(7.0F)),
            var3.handle(24.0F),
            15.0F,
            process(var9),
            ModuleStateHelper.handle(var4),
            var15 - var3.handle(22.0F)
         );
         this.handle(
            var1,
            var2,
            var3,
            FontRegistry.instance,
            resolve(var13 + var3.handle(12.0F)),
            resolve(var14 + var16 - var3.handle(24.0F)),
            var3.handle(16.0F),
            7.0F,
            var10,
            ThemeColors.handle(ModuleStateHelper.process(var4), ModuleStateHelper.handle(var4), var19 * 0.24F),
            var15 - var3.handle(22.0F)
         );
      } finally {
         var1.check();
         var1.prepare();
      }
   }

   private float handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, String var9) {
      float var10 = var2.handle(24.0F);
      if (var8 + var10 >= var5 - var2.handle(3.0F) && var8 <= var5 + var7 + var2.handle(3.0F)) {
         float var11 = resolve(var4 + var2.handle(6.0F));
         float var12 = resolve(var4 + var6 - var2.handle(9.0F));
         float var13 = var2.handle(16.0F);
         float var14 = resolve(var8 + var10 - var13 - var2.handle(2.0F));
         ModuleStateHelper.handle(var1, var2, FontRegistry.config, var11, var14, var13, 7.5F, var9, ModuleStateHelper.process(var3));
         float var15 = ModuleStateHelper.handle(var2, FontRegistry.config, var9, 7.5F);
         float var16 = resolve(var11 + var15 + var2.handle(10.0F));
         float var17 = var12 - var16;
         if (var17 > var2.handle(8.0F)) {
            var1.handle(
               var16,
               resolve(var14 + var13 * 0.5F),
               var17,
               Math.max(1.0F, var2.handle(1.0F)),
               var2.handle(0.5F),
               var3.unload() ? ThemeColors.handle(0, 0, 0, 26) : var3.refresh()
            );
         }
      }

      return resolve(var8 + var10);
   }

   private float handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      String var10,
      String var11,
      int var12,
      int var13
   ) {
      float var14 = var3.handle(34.0F);
      if (var9 + var14 >= var6 - var3.handle(3.0F) && var9 <= var6 + var8 + var3.handle(3.0F)) {
         float var15 = var5 + var3.handle(4.0F);
         float var16 = Math.max(var3.handle(80.0F), var7 - var3.handle(13.0F));
         this.process(var1, var2, var3, var4, var15, var9, var16, var14, var10, var11, var12, var13);
      }

      return resolve(var9 + var14 + var3.handle(7.0F));
   }

   private void process(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      String var10,
      int var11,
      int var12
   ) {
      float var13 = resolve(var5);
      float var14 = resolve(var6);
      float var15 = handle(var13, var7);
      float var16 = handle(var14, var8);
      boolean var17 = ModuleStateHelper.handle(var2, var13, var14, var15, var16);
      float var18 = var2.handle("profile:row:hover:" + var12, var17 ? 1.0F : 0.0F, SpringAnimationSpec.select());
      float var19 = process(var18);
      var1.handle(0.0F, -var3.handle(0.9F) * var19);
      var1.handle(1.0F + var19 * 0.009F, var13 + var15 * 0.5F, var14 + var16 * 0.5F);

      try {
         int var20 = var4.unload()
            ? ThemeColors.handle(ModuleStateHelper.handle(var4, 0.14F), ThemeColors.handle(var11, 30), 0.08F + var19 * 0.28F)
            : ThemeColors.handle(ThemeColors.handle(8, 12, 23, 164), ThemeColors.handle(var11, 52), 0.14F + var19 * 0.16F);
         if (var19 > 0.01F) {
            var1.handle(
               var13,
               var14,
               var15,
               var16,
               var3.handle(9.0F),
               var3.handle(7.0F) * var19,
               var3.handle(1.2F),
               ThemeColors.handle(var11, Math.round(28.0F * var19))
            );
         }

         var1.handle(var13, var14, var15, var16, var3.handle(9.0F), var20);
         var1.handle(
            var13,
            var14,
            var15,
            var16,
            var3.handle(9.0F),
            ThemeColors.handle(var4.unload() ? ThemeColors.handle(255, 255, 255, 70) : var4.select(), ThemeColors.handle(var11, 154), var19),
            var3.handle(0.55F + var19 * 0.3F)
         );
         if (var19 > 0.01F) {
            var1.handle(
               var13 + var3.handle(1.2F),
               var14 + var16 * 0.27F,
               var3.handle(1.8F),
               var16 * 0.46F,
               var3.handle(0.9F),
               ThemeColors.handle(var11, Math.round(196.0F * var19))
            );
         }

         this.handle(var1, var3, var13 + var3.handle(17.0F), var14 + var16 * 0.5F, var12, var11, var4);
         float var21 = resolve(var13 + var3.handle(34.0F));
         float var22 = resolve(var13 + var15 * 0.52F);
         int var23 = ThemeColors.handle(ModuleStateHelper.process(var4), ModuleStateHelper.handle(var4), var19 * 0.28F);
         this.handle(
            var1, var2, var3, FontRegistry.instance, var21, var14, var16, 8.0F, var9, var23, Math.max(var3.handle(34.0F), var22 - var21 - var3.handle(10.0F))
         );
         this.handle(
            var1,
            var2,
            var3,
            FontRegistry.config,
            var22,
            var14,
            var16,
            8.5F,
            process(var10),
            ModuleStateHelper.handle(var4),
            Math.max(var3.handle(42.0F), var13 + var15 - var22 - var3.handle(12.0F))
         );
      } finally {
         var1.check();
         var1.prepare();
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, GuiMetrics var3, ThemeColors var4, ViewportLayoutState var5) {
      float var6 = this.update(var5, var3);
      float var7 = this.resolve(var5, var3);
      float var8 = this.compute(var3);
      this.handle(var1, var2, var3, var4, var7, var6, var8, "Диагностика", var4.save(), 0);
      this.handle(var1, var2, var3, var4, this.apply(var5, var3), this.execute(var5, var3), this.update(var3), "Закрыть", ModuleStateHelper.handle(var4), 2);
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      float var5,
      float var6,
      float var7,
      String var8,
      int var9,
      int var10
   ) {
      float var11 = resolve(var5);
      float var12 = resolve(var6);
      float var13 = handle(var11, var7);
      float var14 = var3.handle(var10 == 2 ? 32.0F : 32.0F);
      boolean var15 = ModuleStateHelper.handle(var2, var11, var12, var13, var14);
      float var16 = var2.handle("profile:action:hover:" + var10, var15 ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      float var17 = process(var16);
      var1.handle(0.0F, -var3.handle(1.0F) * var17);
      var1.handle(1.0F + var17 * 0.014F, var11 + var13 * 0.5F, var12 + var14 * 0.5F);

      try {
         int var18 = var4.unload()
            ? ThemeColors.handle(ModuleStateHelper.handle(var4, 0.25F), ThemeColors.handle(var9, 42), var17 * 0.34F)
            : ThemeColors.handle(ThemeColors.handle(8, 12, 23, 188), ThemeColors.handle(var9, 68), 0.18F + var17 * 0.22F);
         if (var17 > 0.01F) {
            var1.handle(
               var11, var12, var13, var14, var3.handle(9.0F), var3.handle(8.0F) * var17, var3.handle(1.5F), ThemeColors.handle(var9, Math.round(34.0F * var17))
            );
         }

         var1.handle(var11, var12, var13, var14, var3.handle(9.0F), var18);
         var1.handle(
            var11,
            var12,
            var13,
            var14,
            var3.handle(9.0F),
            ThemeColors.handle(var9, Math.round((var4.unload() ? 58.0F : 84.0F) + 92.0F * var17)),
            var3.handle(0.6F + 0.25F * var17)
         );
         this.process(var1, var3, var11 + var3.handle(17.0F), var12 + var14 * 0.5F, var10, var9, var4);
         this.handle(
            var1,
            var2,
            var3,
            FontRegistry.config,
            var11 + var3.handle(34.0F + var17 * 1.5F),
            var12,
            var14,
            8.5F,
            var8,
            ModuleStateHelper.handle(var4),
            var13 - var3.handle(44.0F)
         );
      } finally {
         var1.check();
         var1.prepare();
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var2.handle(12.0F);
      int var11 = var3.unload() ? ThemeColors.handle(255, 255, 255, 142) : ThemeColors.handle(5, 7, 13, 166);
      int var12 = var3.unload() ? ThemeColors.handle(255, 255, 255, 0) : ThemeColors.handle(5, 7, 13, 0);
      if (var9 < -var2.handle(0.5F)) {
         var1.process(var4, var5, var6, var10, var11, var12);
      }

      if (var8 + var9 > var7 + var2.handle(0.5F)) {
         var1.process(var4, var5 + var7 - var10, var6, var10, var12, var11);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      ThemeColors var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10
   ) {
      float var11 = Math.max(0.0F, var9 - var8);
      if (!(var11 <= var3.handle(1.0F))) {
         float var12 = Math.max(var3.handle(4.0F), var3.handle(5.0F));
         float var13 = resolve(var5 + var7 - var12);
         float var14 = Math.max(var3.handle(24.0F), var8 * var8 / Math.max(var8, var9));
         float var15 = resolve(var6 + (var8 - var14) * (Math.abs(var10) / Math.max(1.0F, var11)));
         ModuleStateHelper.handle(
            var1, var3, var4, var13, var6, var12, var8, var15, var14, 0.0F, 0.48F, 3L, var2.sampleLayer(), var2.sendWorld(), CoreDiagnosticsPanel::handle
         );
      }
   }
   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      GuiMetrics var3,
      FontObject var4,
      float var5,
      float var6,
      float var7,
      float var8,
      String var9,
      int var10,
      float var11
   ) {
      String var12 = process(var9);
      float var13 = resolve(var5);
      float var14 = resolve(var6);
      float var15 = Math.max(var3.handle(8.0F), resolve(var11));
      float var16 = resolve(var7);
      float var17 = ModuleStateHelper.handle(var3, var4, var12, var8);
      float var18 = Math.max(0.0F, var17 - var15 + var3.handle(8.0F));
      float var19 = 0.0F;
      if (var18 > var3.handle(1.0F) && ModuleStateHelper.handle(var2.sampleLayer(), var2.sendWorld(), var13, var14, var15, var16)) {
         float var20 = (float)(System.currentTimeMillis() % 2600L) / 2600.0F;
         float var21 = var20 < 0.5F ? var20 * 2.0F : 2.0F - var20 * 2.0F;
         var19 = resolve(var18 * var21);
      }

      var1.compute();
      var1.handle(var13, var14, var15, var16, 0.0F, 0.0F, 0.0F, 0.0F);
      boolean var24 = false /* VF: Semaphore variable */;

      try {
         var24 = true;
         TextureFilterState.handle();
         ModuleStateHelper.handle(var1, var3, var4, var13 - var19, var14, var16, var8, var12, var10);
         var24 = false;
      } finally {
         if (var24) {
            TextureFilterState.process();
            var1.compute();
            var1.apply();
         }
      }

      TextureFilterState.process();
      var1.compute();
      var1.apply();
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5, int var6, ThemeColors var7) {
      float var8 = var2.handle(1.0F);
      int var9 = ThemeColors.handle(var6, 216);
      var1.handle(
         var3 - 5.8F * var8, var4 - 5.8F * var8, 11.6F * var8, 11.6F * var8, 3.5F * var8, ThemeColors.handle(var9, 132), Math.max(0.65F, var2.handle(0.65F))
      );
      if (var5 % 3 == 0) {
         var1.handle(var3 - 3.8F * var8, var4 + 2.0F * var8, 7.6F * var8, 1.5F * var8, 0.75F * var8, var9);
         var1.handle(var3 - 1.0F * var8, var4 - 4.2F * var8, 2.0F * var8, 8.0F * var8, 1.0F * var8, ThemeColors.handle(var9, 196));
      } else if (var5 % 3 == 1) {
         var1.handle(var3 - 4.0F * var8, var4 - 2.7F * var8, 8.0F * var8, 1.5F * var8, 0.75F * var8, var9);
         var1.handle(var3 - 4.0F * var8, var4 + 1.4F * var8, 8.0F * var8, 1.5F * var8, 0.75F * var8, ThemeColors.handle(var9, 186));
      } else {
         var1.process(var3 - 3.2F * var8, var4, 1.7F * var8, 0.0F, 1.0F, var9);
         var1.process(var3 + 3.2F * var8, var4, 1.7F * var8, 0.0F, 1.0F, ThemeColors.handle(var9, 188));
         var1.handle(var3 - 1.6F * var8, var4 - 0.7F * var8, 3.2F * var8, 1.4F * var8, 0.7F * var8, ThemeColors.handle(ModuleStateHelper.handle(var7), 156));
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, float var3, float var4, int var5, int var6, ThemeColors var7) {
      float var8 = var2.handle(1.0F);
      int var9 = ThemeColors.handle(var5 == 2 ? ModuleStateHelper.handle(var7) : var6, 234);
      if (var5 == 0) {
         var1.handle(
            var3 - 6.0F * var8, var4 - 5.5F * var8, 12.0F * var8, 11.0F * var8, 3.0F * var8, ThemeColors.handle(var9, 118), Math.max(0.6F, var2.handle(0.6F))
         );
         var1.handle(var3 - 3.6F * var8, var4 + 1.6F * var8, 1.4F * var8, 3.0F * var8, 0.7F * var8, var9);
         var1.handle(var3 - 0.7F * var8, var4 - 1.6F * var8, 1.4F * var8, 6.2F * var8, 0.7F * var8, var9);
         var1.handle(var3 + 2.2F * var8, var4 - 4.0F * var8, 1.4F * var8, 8.6F * var8, 0.7F * var8, var9);
      } else if (var5 == 1) {
         var1.handle(var3 - 5.2F * var8, var4 - 4.0F * var8, 10.4F * var8, 1.4F * var8, 0.7F * var8, var9);
         var1.handle(var3 - 5.2F * var8, var4 - 0.5F * var8, 10.4F * var8, 1.4F * var8, 0.7F * var8, var9);
         var1.handle(var3 - 5.2F * var8, var4 + 3.0F * var8, 7.4F * var8, 1.4F * var8, 0.7F * var8, ThemeColors.handle(var9, 188));
      } else {
         var1.handle(var3, var4);
         var1.process(45.0F);

         try {
            var1.handle(-4.9F * var8, -0.8F * var8, 9.8F * var8, 1.6F * var8, 0.8F * var8, var9);
            var1.handle(-0.8F * var8, -4.9F * var8, 1.6F * var8, 9.8F * var8, 0.8F * var8, var9);
         } finally {
            var1.execute();
            var1.prepare();
         }
      }
   }

   public static boolean handle(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3, double var4) {
      if (!handle(var0, var1, var2, var3)) {
         return false;
      }

      windowConvert.handle(var4);
      return true;
   }

   public static boolean handle(ViewportLayoutState var0, GuiMetrics var1, float var2, float var3) {
      float var4 = handle(var0, var1);
      float var5 = process(var0, var1);
      float var6 = compute(var0, var1);
      float var7 = handle(var1, var6);
      return ModuleStateHelper.handle(var2, var3, handle(var4, var1), handle(var5, var1, var7), process(handle(var1), var1), handle(var5, var6, var1, var7));
   }

   public static float handle(ViewportLayoutState var0, GuiMetrics var1) {
      return resolve(var0.drawAnimation() + var1.handle(18.0F));
   }

   public static float process(ViewportLayoutState var0, GuiMetrics var1) {
      return resolve(var0.encodePoint() + var1.handle(18.0F));
   }

   static float handle(GuiMetrics var0) {
      return resolve(process(var0.handle(330.0F), var0.handle(292.0F), Math.max(var0.handle(306.0F), var0.check() * 0.48F)));
   }

   public static float compute(ViewportLayoutState var0, GuiMetrics var1) {
      float var2 = process(var0, var1);
      return Math.max(var1.handle(352.0F), resolve(var0.encodePoint() + var0.load()) - var2);
   }

   public float process(GuiMetrics var1) {
      return handle(var1);
   }

   public float resolve(ViewportLayoutState var1, GuiMetrics var2) {
      return resolve(handle(var1, var2) + var2.handle(18.0F));
   }

   public float update(ViewportLayoutState var1, GuiMetrics var2) {
      return resolve(this.execute(var1, var2) - var2.handle(10.0F) - this.resolve(var2));
   }

   public float compute(GuiMetrics var1) {
      return resolve(handle(var1) - var1.handle(36.0F));
   }

   public float resolve(GuiMetrics var1) {
      return resolve(var1.handle(32.0F));
   }

   public float apply(ViewportLayoutState var1, GuiMetrics var2) {
      return resolve(handle(var1, var2) + var2.handle(18.0F));
   }

   public float execute(ViewportLayoutState var1, GuiMetrics var2) {
      return resolve(process(var1, var2) + compute(var1, var2) - var2.handle(18.0F) - this.apply(var2));
   }

   public float update(GuiMetrics var1) {
      return resolve(handle(var1) - var1.handle(36.0F));
   }

   public float apply(GuiMetrics var1) {
      return resolve(var1.handle(32.0F));
   }

   private static float handle(GuiMetrics var0, float var1) {
      float var2 = var0.handle(74.0F);
      float var3 = var1 - var0.handle(36.0F) - var2 - var0.handle(150.0F);
      return resolve(process(var1 * 0.27F, var0.handle(108.0F), Math.max(var0.handle(108.0F), Math.min(var0.handle(126.0F), var3))));
   }

   private static float handle(float var0, GuiMetrics var1) {
      return resolve(var0 + var1.handle(18.0F));
   }

   private static float handle(float var0, GuiMetrics var1, float var2) {
      return resolve(var0 + var1.handle(18.0F) + var2 + var1.handle(8.0F));
   }

   private static float process(float var0, GuiMetrics var1) {
      return resolve(var0 - var1.handle(36.0F));
   }

   private static float handle(float var0, float var1, GuiMetrics var2, float var3) {
      float var4 = handle(var0, var2, var3);
      float var5 = resolve(var0 + var1 - var2.handle(18.0F) - var2.handle(74.0F) - var2.handle(12.0F));
      return Math.max(var2.handle(80.0F), var5 - var4);
   }

   private static float execute(GuiMetrics var0) {
      byte var1 = 9;
      float var2 = var0.handle(var1 * 34.0F + (var1 - 1) * 7.0F);
      return resolve(var0.handle(132.0F) + var2);
   }

   private void handle() {
      int var1 = process();
      if (var1 != this.textureRun) {
         this.textureRun = var1;
         this.timerRender = Integer.toString(var1);
      }

      int var2 = this.compute();
      if (var2 != this.indexBind) {
         this.indexBind = var2;
         this.scaleSave = Integer.toString(var2);
      }

      String var3 = process(this.sourceBuild.output);
      if (!var3.equals(this.colorCompute)) {
         this.colorCompute = var3;
         this.scaleAdapt = handle(var3, "finite") ? "OK" : "CORRUPTED";
      }
   }

   private static int process() {
      try {
         return Profile.getUid();
      } catch (Throwable var1) {
         return 0;
      }
   }

   private int compute() {
      try {
         if (WildClient.instance != null && WildClient.instance.data != null) {
            ArrayList var1 = WildClient.instance.data.process();
            int var2 = 0;

            for (int var3 = 0; var3 < var1.size(); var3++) {
               if (((Module)var1.get(var3)).enabled) {
                  var2++;
               }
            }

            return var2;
         } else {
            return 0;
         }
      } catch (Throwable var4) {
         return 0;
      }
   }

   private String resolve() {
      try {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null && var1.getSession() != null) {
            return var1.getSession().getUsername();
         }
      } catch (Throwable var2) {
      }

      return "Player";
   }

   private String update() {
      return this.scaleAdapt;
   }

   private String apply() {
      return this.sourceBuild.vectorMatch == 0 ? "Изолированы [TextureUnitGuard]" : process(this.sourceBuild.cache);
   }

   private int handle(ThemeColors var1) {
      return "OK".equals(this.scaleAdapt) ? var1.save() : var1.process();
   }

   private static boolean handle(String var0, String var1) {
      int var2 = var0.length() - var1.length();

      for (int var3 = 0; var3 <= var2; var3++) {
         if (var0.regionMatches(true, var3, var1, 0, var1.length())) {
            return true;
         }
      }

      return false;
   }

   private static String handle(String var0) {
      if (var0 != null && !var0.isBlank()) {
         return var0.length() <= 26 ? var0 : var0.substring(0, 23) + "...";
      } else {
         return "unknown";
      }
   }

   private static String process(String var0) {
      return var0 != null && !var0.isBlank() ? var0 : "none";
   }

   private static float handle(float var0, float var1, float var2) {
      return compute((var0 - var1) / Math.max(0.001F, var2 - var1));
   }

   private static float process(float var0) {
      float var1 = compute(var0);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float compute(float var0) {
      return var0 < 0.0F ? 0.0F : Math.min(var0, 1.0F);
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static float resolve(float var0) {
      return Math.round(var0);
   }

   private static float handle(float var0, float var1) {
      return Math.max(0.0F, resolve(var0 + var1) - resolve(var0));
   }
}
