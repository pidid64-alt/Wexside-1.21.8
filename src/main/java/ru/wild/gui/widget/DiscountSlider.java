package ru.wild.gui.widget;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import ru.wild.modules.misc.AutoBuy;

public final class DiscountSlider extends SliderWidget {
   private final AutoBuy instance;

   public DiscountSlider(AutoBuy var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5, handle(var1), process(var1));
      this.instance = var1;
   }

   protected void updateMessage() {
      this.setMessage(handle(this.instance));
   }

   protected void applyValue() {
      this.instance.matrixBlend2.handle(handle(this.instance, this.value));
      this.updateMessage();
   }

   public void handle() {
      this.value = process(this.instance);
      this.updateMessage();
   }

   private static Text handle(AutoBuy var0) {
      return Text.literal("Discount: " + Math.round(var0.matrixBlend2.compute()) + "%");
   }

   private static double process(AutoBuy var0) {
      float var1 = var0.matrixBlend2.state;
      float var2 = var0.matrixBlend2.cache;
      return var2 <= var1 ? 0.0 : Math.max(0.0, Math.min(1.0, (var0.matrixBlend2.compute() - var1) / (var2 - var1)));
   }

   private static float handle(AutoBuy var0, double var1) {
      double var3 = Math.max(0.0, Math.min(1.0, var1));
      float var5 = var0.matrixBlend2.state;
      float var6 = var0.matrixBlend2.cache;
      float var7 = (float)(var5 + var3 * (var6 - var5));
      float var8 = var0.matrixBlend2.output;
      if (var8 > 0.0F) {
         var7 = Math.round(var7 / var8) * var8;
      }

      return Math.max(var5, Math.min(var6, var7));
   }
}
