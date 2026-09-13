package ru.wild.gui.widget;

import java.util.List;

public final class UiElementPicker {
   private UiElementPicker() {
   }

   public static AnimatedUiElement handle(List<AnimatedUiElement> var0, float var1, float var2, int var3) {
      if (var0 != null && !var0.isEmpty()) {
         for (AnimatedUiElement var5 : var0) {
            if (var5 != null && var5.handle(var1, var2, var3)) {
               return var5;
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
