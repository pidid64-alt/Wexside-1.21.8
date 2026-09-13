package ru.wild.gui.widget;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import org.wild.module.api.Module;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.RoundedRectRenderer;

public interface ModulePanelRenderer {
   boolean handle(Module var1);

   default boolean handle(Module var1, ModernClickGuiState var2) {
      return false;
   }

   default void handle(ModernClickGuiState var1) {
   }

   default void process(ModernClickGuiState var1) {
   }

   default void compute(ModernClickGuiState var1) {
   }

   float handle(Module var1, GuiMetrics var2, ModernClickGuiState var3);

   void handle(Module var1, ModernClickGuiState var2, SpringAnimationSpec var3, SpringAnimationSpec var4);

   void handle(RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ModulePlacement var4, ThemeRenderContext var5);

   void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ModulePlacement var3, GuiMetrics var4);

   default boolean handle(ModernClickGuiState var1, ModuleLayoutResult var2, GuiMetrics var3, float var4, float var5, double var6) {
      return false;
   }

   default boolean handle(ModernClickGuiState var1, float var2, float var3) {
      return false;
   }

   default boolean resolve(ModernClickGuiState var1) {
      return false;
   }

   default boolean handle(ModernClickGuiState var1, int var2) {
      return false;
   }

   default boolean handle(ModernClickGuiState var1, char var2) {
      return false;
   }
}
