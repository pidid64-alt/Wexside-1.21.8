package ru.wild.gui.widget;

import java.util.ArrayList;
import java.util.List;
import org.wild.module.api.Module;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.screen.ViewportLayoutState;
import ru.wild.render.font.FontRegistry;

public final class ModuleTooltipMeasurer {
   public ModuleLayoutResult handle(ModernClickGuiState var1, ViewportLayoutState var2, GuiMetrics var3) {
      List var4 = var1.apply();
      ArrayList var5 = new ArrayList(var4.size());
      float[] var6 = new float[]{0.0F, 0.0F};
      int var7 = 0;

      for (int var8 = 0; var8 < var4.size(); var8++) {
         Module var9 = (Module)var4.get(var8);
         ModulePanelRenderer var10 = ModulePanelRegistry.handle(var9);
         boolean var11 = var10 != null && var10.handle(var9, var1);
         int var12 = var11 ? -1 : var7 % 2;
         float var13 = var11 ? Math.max(var6[0], var6[1]) : var6[var12];
         float var14 = var11 ? var2.measure() : (var12 == 0 ? var2.measure() : var2.blendMatrix());
         float var15 = var2.submit() + var1.convertAction() + var13;
         float var16 = var11 ? var3.drawAnimation() * 2.0F + var3.execute() : var3.drawAnimation();
         float var17 = var1.handle(UiAnimationKeys.handle(var9));
         float var18 = this.handle(var9, var3, var1) * var17;
         float var19 = this.handle(var9, var16, var3);
         float var20 = var19 + var18;
         var5.add(new ModulePlacement(var9, var14, var15, var16, var20, var18));
         if (var11) {
            float var21 = var13 + var20 + var3.animate();
            var6[0] = var21;
            var6[1] = var21;
         } else {
            var6[var12] += var20 + var3.animate();
            var7++;
         }
      }

      float var22 = Math.max(0.0F, Math.max(var6[0], var6[1]) - var3.animate());
      if (!var5.isEmpty()) {
         var22 += this.handle(var3, var2, var22);
      }

      float var23 = Math.max(0.0F, var22 - var2.fetch());
      return new ModuleLayoutResult(var5, var23);
   }

   private float handle(GuiMetrics var1, ViewportLayoutState var2, float var3) {
      float var4 = Math.max(var1.tick(), var1.animate() * 2.0F);
      float var5 = var2.fetch() - var3;
      return var5 >= var4 ? 0.0F : var4;
   }

   public float handle(Module var1, GuiMetrics var2, ModernClickGuiState var3) {
      ModulePanelRenderer var4 = ModulePanelRegistry.handle(var1);
      if (var4 != null) {
         return var4.handle(var1, var2, var3);
      }

      float var5 = var2.handle(1.0F) + var2.handle(20.0F);
      List var6 = var1.apply();

      for (int var7 = 0; var7 < var6.size(); var7++) {
         Setting var8 = (Setting)var6.get(var7);
         if (var8 instanceof FloatSetting var9) {
            var5 += var2.handle(var9.compute());
         } else {
            float var12 = var3.handle(UiAnimationKeys.resolve(var8));
            float var10 = this.handle(var8, var2, var3);
            float var11 = this.handle(var8, var3, var2);
            var5 += (var10 + var11) * var12;
            if (var7 < var6.size() - 1) {
               var5 += var2.handle(12.0F) * var12;
            }
         }
      }

      return var5;
   }

   public float handle(Module var1, float var2, GuiMetrics var3) {
      String var4 = var1.description == null ? "" : var1.description;
      if (var4.isBlank()) {
         return var3.encodePoint();
      }

      float var5 = Math.max(var3.handle(160.0F), var2 - var3.handle(90.0F));
      int var6 = ModuleStateHelper.handle(FontRegistry.instance, var4, 10.0F, var5, 10).size();
      float var7 = var3.handle(54.0F) + Math.max(1, var6) * var3.handle(12.0F);
      return Math.max(var3.encodePoint(), var7);
   }

   public float handle(Setting var1, GuiMetrics var2, ModernClickGuiState var3) {
      if (var1 instanceof NumberSetting) {
         return var2.handle(22.0F);
      } else if (var1 instanceof ShaderPresetSetting) {
         return var2.handle(18.0F);
      } else if (var1 instanceof ColorSetting var11) {
         float var12 = var3.handle(UiAnimationKeys.prepare(var11));
         float var13 = var2.handle(16.0F);
         float var14 = var2.handle(186.0F);
         return var13 + var14 * var12;
      } else if (var1 instanceof FloatSetting var10) {
         return var2.handle(var10.compute());
      } else if (var1 instanceof ChoiceSetting var4) {
         float var5 = var2.drawAnimation() - var2.handle(32.0F);
         float var6 = var5 * 0.7F;
         int var7 = ModuleStateHelper.handle(var4, var6, var2);
         float var8 = var2.handle(14.0F);
         float var9 = var2.handle(3.0F);
         return var2.handle(1.0F) + var7 * var8 + (var7 > 1 ? (var7 - 1) * var9 : 0.0F) + var2.handle(1.0F);
      } else {
         return var2.handle(14.0F);
      }
   }

   public float handle(Setting var1, GuiMetrics var2) {
      if (var1 instanceof NumberSetting || var1 instanceof ColorSetting) {
         return var2.handle(22.0F);
      } else if (var1 instanceof ShaderPresetSetting) {
         return var2.handle(18.0F);
      } else if (var1 instanceof FloatSetting var9) {
         return var2.handle(var9.compute());
      } else if (var1 instanceof ChoiceSetting var3) {
         float var4 = var2.drawAnimation() - var2.handle(32.0F);
         float var5 = var4 * 0.7F;
         int var6 = ModuleStateHelper.handle(var3, var5, var2);
         float var7 = var2.handle(14.0F);
         float var8 = var2.handle(3.0F);
         return var2.handle(1.0F) + var6 * var7 + (var6 > 1 ? (var6 - 1) * var8 : 0.0F) + var2.handle(1.0F);
      } else {
         return var2.handle(14.0F);
      }
   }

   public float handle(Setting var1, ModernClickGuiState var2, GuiMetrics var3) {
      if (var1 instanceof ModeSetting var4) {
         float var5 = var2.handle(UiAnimationKeys.update(var4));
         if (var5 > 0.01F) {
            float var6 = var3.handle(6.0F) + var4.config.size() * var3.handle(18.0F) + var3.handle(4.0F);
            return var6 * var5;
         }
      }

      if (var1 instanceof ShaderPresetSetting var7) {
         float var8 = var2.handle(UiAnimationKeys.update(var7));
         if (var8 > 0.01F) {
            return SettingControlRenderer.handle(var7, var3) * var8;
         }
      }

      return 0.0F;
   }
}
