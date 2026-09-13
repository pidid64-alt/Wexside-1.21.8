package ru.wild.gui.widget;

import ru.wild.util.render.RoundedRectRenderer;

public interface KeybindEditor {
   void handle(KeybindEditorRenderer.State var1);

   float update();

   void apply();

   void handle(double var1, double var3);

   boolean handle(double var1, double var3, int var5);

   boolean handle(double var1, double var3, double var5, double var7);

   void handle(RoundedRectRenderer var1, float var2, float var3);

   void process(RoundedRectRenderer var1, float var2, float var3);

   boolean execute();
}
