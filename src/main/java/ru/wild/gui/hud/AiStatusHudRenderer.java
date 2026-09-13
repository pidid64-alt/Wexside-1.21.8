package ru.wild.gui.hud;

import java.util.Locale;
import ru.wild.api.module.ModuleRoles;
import ru.wild.automation.RotationTrainingStatus;
import ru.wild.automation.combat.RotationRecorder;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.combat.AttackAura;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRoles(compute = "lichoday")
@HudElementMetadata(handle = "AI Status", process = "i")
public final class AiStatusHudRenderer extends ThemePresets implements MinecraftContext {
   private static final AiStatusHudRenderer instance = new AiStatusHudRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();
   private static final DoubleAnimator providerFetch = new DoubleAnimator();
   private static final DoubleAnimator profileDraw = new DoubleAnimator();

   private AiStatusHudRenderer() {
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (toggleState.player != null && toggleState.world != null) {
         RotationTrainingStatus var2 = RotationRecorder.computeResponse();
         boolean var3 = AttackAura.pending.process("AI") || System.currentTimeMillis() - var2.updatedAtMs() < 2000L;
         responseCompute.handle();
         providerFetch.handle();
         profileDraw.handle();
         responseCompute.handle(var3 ? 1.0 : 0.0, 0.2F, Easings.handler, false);
         if (!(responseCompute.update() <= 0.01F)) {
            String var4 = "AI Aura";
            String var5 = var2.text();
            String var6 = "Frames " + var2.queuedRecords() + "  Saved " + var2.writtenRecords();
            float var7 = 24.0F;
            float var8 = 21.0F;
            float var9 = 18.0F;
            float var10 = TextMeasureCache.handle(FontRegistry.config, var4, var7).instance;
            float var11 = TextMeasureCache.handle(FontRegistry.instance, var5, var8).instance;
            float var12 = TextMeasureCache.handle(FontRegistry.instance, var6, var9).instance;
            float var13 = Math.max(142.0F, Math.max(var10 + var11 + 48.0F, var12 + 44.0F));
            float var14 = 48.0F;
            if (providerFetch.update() <= 1.0F) {
               providerFetch.apply(var13);
            }

            providerFetch.handle(var13, 0.18F, Easings.handler, false);
            float var15 = providerFetch.update();
            float var16 = var14;
            float var17 = (toggleState.getWindow().getFramebufferWidth() - var15) * 0.5F;
            float var18 = 52.0F;
            ThemeRenderer.PrimaryCacheEntry var19 = ThemeRenderer.handle().handle("HUD_AIStatus", var17, var18, var15, var16);
            float var20 = responseCompute.update() * this.target.compute();
            float var21 = var19.data;
            float var22 = var19.context;
            float var23 = var19.config;
            float var24 = var19.state;
            this.handle(var21, var22, var23, var24);
            float var25 = 12.0F;
            int var26 = this.process(var20);
            int var27 = this.update(var20);
            int var28 = this.apply(var20);
            int var29 = this.handle(var2, var20);
            float var30 = this.handle(var2);
            this.handle(var1, var21, var22, var23, var24, var25, var20);
            if (!this.select() && this.resolve()) {
               var1.handle(var21 + 8.0F, var22 + var24 - 3.0F, var23 - 16.0F, 4.0F, 6.0F, 12.0F, 1.0F, PackedColor.handle(var29, (int)(50.0F * var20)));
            }

            if (this.select()) {
               this.process(var1, var21 + 6.0F, var22 + 6.0F, var23 - 12.0F, var24 - 12.0F, 8.0F, var20);
            } else {
               var1.handle(var21 + 6.0F, var22 + 6.0F, var23 - 12.0F, var24 - 12.0F, 8.0F, var26);
            }

            var1.handle(
               var21 + 10.0F,
               var22 + var24 - 2.0F,
               var23 - 20.0F,
               1.0F,
               0.5F,
               PackedColor.handle(this.check(1.0F), (int)(22.0F * var20)),
               PackedColor.handle(var29, (int)(74.0F * var20))
            );
            float var31 = var21 + 20.0F;
            float var32 = var22 + var24 * 0.5F;
            var1.process(var31, var32, 8.0F + var30 * 5.0F, 0.0F, 360.0F, PackedColor.handle(var29, (int)(42.0F * var20 * (1.0F - var30 * 0.5F))));
            var1.process(var31, var32, 4.0F, 0.0F, 360.0F, var29);
            var1.handle(FontRegistry.config, var21 + 36.0F, var22 + 20.0F, var7, var4, var27);
            var1.handle(FontRegistry.instance, var21 + var23 - 12.0F - var11, var22 + 20.0F, var8, var5, var28);
            var1.handle(FontRegistry.instance, var21 + 36.0F, var22 + 38.0F, var9, var6, PackedColor.compute(155, 165, 180, (int)(165.0F * var20)));
            ThemeRenderer.handle().handle(var19);
            NeoStyleOptions.handle(
               var1, this, var19, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   private int handle(RotationTrainingStatus var1, float var2) {
      String var3 = var1.text().toLowerCase(Locale.ROOT);
      if (var3.contains("failed") || var3.contains("error") || var3.contains("missing")) {
         return PackedColor.compute(255, 96, 112, (int)(255.0F * var2));
      } else if (var1.training()) {
         return PackedColor.compute(255, 198, 92, (int)(255.0F * var2));
      } else if (var1.loadingModel()) {
         return PackedColor.compute(120, 176, 255, (int)(255.0F * var2));
      } else if (var3.contains("recording")) {
         return PackedColor.compute(92, 235, 182, (int)(255.0F * var2));
      } else {
         return !var3.contains("replay") && !var3.contains("ready")
            ? PackedColor.handle(this.prepare(1.0F), (int)(255.0F * var2))
            : PackedColor.compute(128, 226, 255, (int)(255.0F * var2));
      }
   }

   private float handle(RotationTrainingStatus var1) {
      boolean var2 = var1.training() || var1.text().contains("recording") || var1.text().contains("replay");
      profileDraw.handle(var2 ? 1.0 : 0.0, 0.2F, Easings.handler, false);
      return profileDraw.update() * (0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 180.0));
   }
}
