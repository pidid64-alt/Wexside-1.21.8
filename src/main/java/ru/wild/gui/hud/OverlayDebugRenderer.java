package ru.wild.gui.hud;

import ru.wild.gui.screen.GuiMetrics;
import ru.wild.util.render.RoundedRectRenderer;

public final class OverlayDebugRenderer {
   private OverlayDebugRenderer() {
   }

   public static void handle(RoundedRectRenderer var0, GuiMetrics var1, float var2, float var3, float var4, int var5, int var6) {
      float var7 = Math.max(var1.handle(0.35F), var4);
      var0.process(var2, var3 - 5.4F * var7, 4.3F * var7, 0.0F, 1.0F, var6);
      var0.handle(var2 - 6.8F * var7, var3 + 0.9F * var7, 13.6F * var7, 9.0F * var7, 4.5F * var7, var6);
      var0.process(var2, var3 - 5.4F * var7, 3.2F * var7, 0.0F, 1.0F, var5);
      var0.handle(var2 - 5.2F * var7, var3 + 1.7F * var7, 10.4F * var7, 7.1F * var7, 3.4F * var7, var5);
   }
}
