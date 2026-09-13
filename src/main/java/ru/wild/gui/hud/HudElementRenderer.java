package ru.wild.gui.hud;

import ru.wild.gui.theme.LivePreviewRenderer;

public interface HudElementRenderer {
   LivePreviewRenderer compute();

   default String resolve() {
      return null;
   }

   default boolean update() {
      return false;
   }
}
