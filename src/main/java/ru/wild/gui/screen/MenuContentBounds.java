package ru.wild.gui.screen;

import ru.wild.core.ModuleStateHelper;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.RectBounds;
import ru.wild.util.text.KeycodeNames;

public final class MenuContentBounds {
   private MenuContentBounds() {
   }

   public static RectBounds handle(ViewportLayoutState var0, GuiMetrics var1) {
      if (var0 != null && var1 != null) {
         String var2 = handle();
         float var3 = ModuleStateHelper.handle(FontRegistry.config, var2, 12.0F);
         float var4 = ModuleStateHelper.handle(FontRegistry.state, "g", 12.0F);
         float var5 = var0.check() + var0.refresh() - var1.handle(16.0F) - var4;
         float var6 = var5 - var1.handle(8.0F) - var3;
         float var7 = var1.handle(86.0F);
         float var8 = var1.handle(24.0F);
         float var9 = var6 - var1.handle(12.0F) - var7;
         float var10 = var0.onTick() + (var0.select() - var8) * 0.5F;
         return new RectBounds(var9, var10, var7, var8);
      } else {
         return new RectBounds(0.0F, 0.0F, 0.0F, 0.0F);
      }
   }

   private static String handle() {
      Menu var0 = Menu.drawAnimation();
      int var1 = var0 != null && var0.keyCode != -1 ? var0.keyCode : 344;
      return KeycodeNames.handle(var1);
   }
}
