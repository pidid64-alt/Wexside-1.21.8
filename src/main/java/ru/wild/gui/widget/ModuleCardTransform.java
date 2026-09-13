package ru.wild.gui.widget;

import org.wild.module.api.Module;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;

public record ModuleCardTransform(
   boolean visible, float searchVisibility, float cardEntry, float entryMotion, float scale, float pivotX, float pivotY, float slideY, float lift
) {
   public static ModuleCardTransform resolve(ModernClickGuiState var0, ModulePlacement var1, GuiMetrics var2) {
      Module var3 = var1.handle();
      float var4 = var0.handle(UiAnimationKeys.apply(var3));
      float var5 = var0.handle(UiAnimationKeys.execute(var3));
      float var6 = Math.max(0.0F, Math.min(1.0F, var5));
      float var7 = var6 * var6 * (3.0F - 2.0F * var6);
      float var8 = 0.85F + 0.15F * var7;
      String var9 = UiAnimationKeys.process(var3);
      float var10 = var0.handle(var9);
      float var11 = ModuleStateHelper.handle(var10, var0.process(var9));
      float var12 = Math.max(0.001F, var8 * var11);
      float var13 = var1.process() + var1.resolve() * 0.5F;
      float var14 = var1.compute() + var1.update() * 0.5F;
      float var15 = (1.0F - var7) * var2.handle(15.0F);
      float var16 = var10 * var2.handle(1.5F);
      return new ModuleCardTransform(var4 >= 0.01F && var5 >= 0.005F, var4, var5, var7, var12, var13, var14, var15, var16);
   }

   public float hitTranslateY() {
      return this.slideY - this.lift * this.scale;
   }
}
