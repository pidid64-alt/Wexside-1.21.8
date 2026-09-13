package ru.wild.gui.hud;

import java.util.Locale;
import net.minecraft.util.math.MathHelper;
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

@HudElementMetadata(handle = "Neuro Monitor", process = "i")
public final class NeuroStatusHudRenderer extends ThemePresets implements MinecraftContext {
   private static final NeuroStatusHudRenderer instance = new NeuroStatusHudRenderer();
   private static final DoubleAnimator responseCompute = new DoubleAnimator();

   private NeuroStatusHudRenderer() {
      HudProfileConfig.handle(this);
   }

   public static void handle(RoundedRectRenderer var0) {
      instance.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (toggleState.player != null && toggleState.world != null) {
         RotationTrainingStatus var2 = RotationRecorder.computeResponse();
         long var3 = System.currentTimeMillis();
         boolean var5 = AttackAura.pending.process("AI") || RotationRecorder.matchVector() || var2.training() || var3 - var2.updatedAtMs() < 4000L;
         responseCompute.handle();
         responseCompute.handle(var5 ? 1.0 : 0.0, 0.2F, Easings.handler, false);
         float var6 = responseCompute.update();
         if (!(var6 <= 0.01F)) {
            float var7 = 306.0F;
            float var8 = 170.0F;
            float var9 = 12.0F;
            float var10 = 120.0F;
            ThemeRenderer.PrimaryCacheEntry var11 = ThemeRenderer.handle().handle("HUD_NeuroMonitor", var9, var10, var7, var8);
            float var12 = var6 * this.target.compute();
            float var13 = var11.data;
            float var14 = var11.context;
            float var15 = var11.config;
            float var16 = var11.state;
            this.handle(var13, var14, var15, var16);
            int var17 = this.update(var12);
            int var18 = this.apply(var12);
            int var19 = this.prepare(var12);
            int var20 = PackedColor.compute(255, 156, 86, (int)(255.0F * var12));
            int var21 = this.handle(var2, var12);
            this.handle(var1, var13, var14, var15, var16, 12.0F, var12);
            float var22 = 12.0F;
            var1.process(var13 + var22 + 4.0F, var14 + 15.0F, 4.0F, 0.0F, 360.0F, var21);
            var1.handle(FontRegistry.config, var13 + var22 + 14.0F, var14 + 18.0F, 21.0F, "Neuro Monitor", var17);
            String var23 = var2.text();
            float var24 = TextMeasureCache.handle(FontRegistry.instance, var23, 15.0F).instance;
            var1.handle(FontRegistry.instance, var13 + var15 - var22 - var24, var14 + 17.0F, 15.0F, var23, var18);
            String var25 = RotationRecorder.tick() < 0.0F ? "—" : String.format(Locale.ROOT, "%.4f", RotationRecorder.tick());
            String var26 = "Profile "
               + RotationRecorder.readServer()
               + "   Pairs "
               + RotationRecorder.drawAnimation()
               + "   Loss "
               + var25
               + "   Jitter "
               + String.format(Locale.ROOT, "%.2f", AttackAura.latest.compute());
            var1.handle(FontRegistry.instance, var13 + var22, var14 + 35.0F, 13.0F, var26, PackedColor.handle(var18, (int)(215.0F * var12)));
            float var27 = var13 + var22;
            float var28 = var15 - var22 * 2.0F;
            float var29 = 44.0F;
            float var30 = var14 + 50.0F;
            var1.handle(FontRegistry.instance, var27, var30, 13.0F, "Твой стиль (датасет)", var18);
            this.handle(var1, var27 + var28, var30, var19, var20, var12);
            this.handle(
               var1,
               var27,
               var30 + 5.0F,
               var28,
               var29,
               RotationRecorder.refresh(),
               RotationRecorder.render(),
               -1,
               var12,
               var19,
               var20,
               "Нет записи — .ai train -> .ai learn"
            );
            float var31 = var30 + 5.0F + var29 + 12.0F;
            String var32 = RotationRecorder.matchVector() ? "Твой аим — запись (live)" : (RotationRecorder.save() ? "Нейросеть — бой (live)" : "Live");
            var1.handle(FontRegistry.instance, var27, var31, 13.0F, var32, var18);
            this.handle(
               var1,
               var27,
               var31 + 5.0F,
               var28,
               var29,
               RotationRecorder.prepare(),
               RotationRecorder.check(),
               RotationRecorder.onTick(),
               var12,
               var19,
               var20,
               "Ожидание..."
            );
            ThemeRenderer.handle().handle(var11);
            NeoStyleOptions.handle(
               var1, this, var11, ThemeRenderer.handle(), toggleState.getWindow().getScaledWidth(), toggleState.getWindow().getScaledHeight()
            );
         }
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, int var4, int var5, float var6) {
      float var7 = 12.0F;
      String var8 = "Pitch";
      String var9 = "Yaw";
      float var10 = TextMeasureCache.handle(FontRegistry.instance, var8, var7).instance;
      float var11 = TextMeasureCache.handle(FontRegistry.instance, var9, var7).instance;
      float var12 = var2 - var10;
      var1.handle(FontRegistry.instance, var12, var3, var7, var8, PackedColor.handle(var5, (int)(255.0F * var6)));
      float var13 = var12 - 10.0F - var11;
      var1.handle(FontRegistry.instance, var13, var3, var7, var9, PackedColor.handle(var4, (int)(255.0F * var6)));
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float[] var6,
      float[] var7,
      int var8,
      float var9,
      int var10,
      int var11,
      String var12
   ) {
      var1.handle(var2, var3, var4, var5, 6.0F, PackedColor.compute(8, 10, 16, (int)(150.0F * var9)));
      if (this.update()) {
         var1.handle(var2, var3, var4, var5, 6.0F, this.resolve(var9), 1.0F);
      }

      float var13 = var3 + var5 * 0.5F;
      var1.handle(var2 + 3.0F, var13 - 0.5F, var4 - 6.0F, 1.0F, PackedColor.handle(var10, (int)(40.0F * var9)));
      if (var6 != null && var7 != null && var6.length != 0) {
         int var27 = Math.min(var6.length, var7.length);
         float var15 = 6.0F;

         for (int var16 = 0; var16 < var27; var16++) {
            float var17 = Math.abs(var6[var16]);
            if (var17 > var15) {
               var15 = var17;
            }

            float var18 = Math.abs(var7[var16]);
            if (var18 > var15) {
               var15 = var18;
            }
         }

         if (var15 > 35.0F) {
            var15 = 35.0F;
         }

         float var28 = var5 * 0.5F - 3.0F;
         float var29 = var28 / var15;
         float var30 = var4 / var27;
         float var19 = Math.max(1.0F, var30 * 0.9F);
         int var20 = PackedColor.handle(var10, (int)(225.0F * var9));
         int var21 = PackedColor.handle(var11, (int)(150.0F * var9));

         for (int var22 = 0; var22 < var27; var22++) {
            int var23 = var8 < 0 ? var22 : (var8 + var22) % var27;
            float var24 = var2 + var22 * var30;
            float var25 = MathHelper.clamp(var7[var23] * var29, -var28, var28);
            if (var25 >= 0.0F) {
               var1.handle(var24, var13 - var25, var19, var25, var21);
            } else {
               var1.handle(var24, var13, var19, -var25, var21);
            }

            float var26 = MathHelper.clamp(var6[var23] * var29, -var28, var28);
            if (var26 >= 0.0F) {
               var1.handle(var24, var13 - var26, var19, var26, var20);
            } else {
               var1.handle(var24, var13, var19, -var26, var20);
            }
         }
      } else {
         float var14 = TextMeasureCache.handle(FontRegistry.instance, var12, 12.0F).instance;
         var1.handle(FontRegistry.instance, var2 + (var4 - var14) * 0.5F, var13 + 4.0F, 12.0F, var12, PackedColor.compute(150, 156, 170, (int)(185.0F * var9)));
      }
   }

   private int handle(RotationTrainingStatus var1, float var2) {
      String var3 = var1.text().toLowerCase(Locale.ROOT);
      if (var3.contains("failed") || var3.contains("error") || var3.contains("missing") || var3.contains("устар")) {
         return PackedColor.compute(255, 96, 112, (int)(255.0F * var2));
      } else if (var1.training()) {
         return PackedColor.compute(255, 198, 92, (int)(255.0F * var2));
      } else if (var3.contains("recording") || var3.contains("запис")) {
         return PackedColor.compute(92, 235, 182, (int)(255.0F * var2));
      } else {
         return !var3.contains("brain") && !var3.contains("ready") && !var3.contains("replay")
            ? this.prepare(var2)
            : PackedColor.compute(128, 226, 255, (int)(255.0F * var2));
      }
   }
}
