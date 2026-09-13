package ru.wild.gui.widget;

import ru.wild.core.CoreDiagnosticsPanel;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ViewportLayoutState;

public record VisibilityTransform(boolean visible, float alpha, float scale, float pivotX, float pivotY, float translateX, float translateY) {
   public static VisibilityTransform resolve(float var0, ViewportLayoutState var1, GuiMetrics var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, var0));
      float var4 = CoreDiagnosticsPanel.handle(var1, var2);
      float var5 = CoreDiagnosticsPanel.process(var1, var2);
      float var6 = Math.max(var2.handle(306.0F), var2.check() * 0.48F);
      float var7 = CoreDiagnosticsPanel.compute(var1, var2);
      float var8 = var1.compute() + var2.handle(36.0F);
      float var9 = var1.resolve() + var1.apply() - var2.handle(36.0F);
      float var10 = var3 * var3 * (3.0F - 2.0F * var3);
      float var11 = 0.965F + var3 * 0.035F;
      float var12 = var8 + (var4 + var6 * 0.5F - var8) * var3;
      float var13 = var9 + (var5 + var7 * 0.5F - var9) * var3;
      float var14 = var2.handle(-18.0F) * (1.0F - var10);
      float var15 = var2.handle(12.0F) * (1.0F - var10);
      return new VisibilityTransform(var3 >= 0.01F, var3, var11, var12, var13, var14, var15);
   }

   public float localX(float var1) {
      return this.pivotX + (var1 - this.translateX - this.pivotX) / Math.max(0.001F, this.scale);
   }

   public float localY(float var1) {
      return this.pivotY + (var1 - this.translateY - this.pivotY) / Math.max(0.001F, this.scale);
   }
}
