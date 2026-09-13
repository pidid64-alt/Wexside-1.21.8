package ru.wild.gui.widget;

import ru.wild.gui.screen.ModernClickGuiState;

@FunctionalInterface
public interface UiRenderCommand {
   void execute(ModernClickGuiState var1);
}
