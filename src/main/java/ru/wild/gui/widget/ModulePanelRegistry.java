package ru.wild.gui.widget;

import java.util.List;
import org.wild.module.api.Module;
import ru.wild.gui.screen.AutoCraftScreen;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.screen.ViewportLayoutState;
import ru.wild.util.inventory.ServerItemCatalog;
import ru.wild.util.math.SpringAnimationSpec;

public final class ModulePanelRegistry {
   private static final List<ModulePanelRenderer> instance = List.of(new ServerItemCatalog(), new AutoCraftScreen());

   private ModulePanelRegistry() {
   }

   public static ModulePanelRenderer handle(Module var0) {
      for (ModulePanelRenderer var2 : instance) {
         if (var2.handle(var0)) {
            return var2;
         }
      }

      return null;
   }

   public static boolean process(Module var0) {
      return handle(var0) != null;
   }

   public static ModulePlacement handle(Module var0, ViewportLayoutState var1, GuiMetrics var2) {
      return new ModulePlacement(
         var0,
         var1.drawAnimation(),
         var1.encodePoint() - var2.encodePoint() - var2.handle(10.0F),
         var1.animate(),
         var2.encodePoint() + var1.load(),
         var1.load() + var2.handle(20.0F)
      );
   }

   public static void handle(ModernClickGuiState var0) {
      compute(var0);

      for (ModulePanelRenderer var2 : instance) {
         var2.handle(var0);
      }
   }

   public static void process(ModernClickGuiState var0) {
      compute(var0);

      for (ModulePanelRenderer var2 : instance) {
         var2.process(var0);
      }
   }

   public static void compute(ModernClickGuiState var0) {
      for (ModulePanelRenderer var2 : instance) {
         var2.compute(var0);
      }
   }

   public static void handle(Module var0, ModernClickGuiState var1, SpringAnimationSpec var2, SpringAnimationSpec var3) {
      ModulePanelRenderer var4 = handle(var0);
      if (var4 != null) {
         var4.handle(var0, var1, var2, var3);
      }
   }

   public static void handle(List<AnimatedUiElement> var0, ModernClickGuiState var1, ModulePlacement var2, GuiMetrics var3) {
      ModulePanelRenderer var4 = handle(var2.handle());
      if (var4 != null) {
         var4.handle(var0, var1, var2, var3);
      }
   }

   public static boolean handle(ModernClickGuiState var0, ModuleLayoutResult var1, GuiMetrics var2, float var3, float var4, double var5) {
      for (ModulePanelRenderer var8 : instance) {
         if (var8.handle(var0, var1, var2, var3, var4, var5)) {
            return true;
         }
      }

      return false;
   }

   public static boolean handle(ModernClickGuiState var0, float var1, float var2) {
      for (ModulePanelRenderer var4 : instance) {
         if (var4.handle(var0, var1, var2)) {
            return true;
         }
      }

      return false;
   }

   public static boolean resolve(ModernClickGuiState var0) {
      boolean var1 = false;

      for (ModulePanelRenderer var3 : instance) {
         if (var3.resolve(var0)) {
            var1 = true;
         }
      }

      return var1;
   }

   public static boolean handle(ModernClickGuiState var0, int var1) {
      for (ModulePanelRenderer var3 : instance) {
         if (var3.handle(var0, var1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean handle(ModernClickGuiState var0, char var1) {
      for (ModulePanelRenderer var3 : instance) {
         if (var3.handle(var0, var1)) {
            return true;
         }
      }

      return false;
   }
}
