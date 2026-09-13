package ru.wild.gui.widget;

import ru.wild.api.setting.Setting;
import ru.wild.util.render.RoundedRectRenderer;

public interface SettingEditor {
   void handle();

   default void handle(boolean var1) {
   }

   void handle(float var1, float var2, float var3);

   float process();

   default float execute() {
      return this.process();
   }

   default void process(RoundedRectRenderer var1, float var2, float var3) {
      this.handle(var1, var2, var3, 0.0F);
   }

   default void handle(RoundedRectRenderer var1, float var2, float var3, float var4) {
      this.process(var1, var2, var3);
   }

   void handle(double var1, double var3);

   default void handle(RoundedRectRenderer var1, float var2, float var3) {
   }

   default boolean update() {
      return false;
   }

   default boolean process(double var1, double var3, int var5) {
      return false;
   }

   default boolean handle(double var1, double var3, double var5, double var7) {
      return false;
   }

   default void apply() {
   }

   boolean handle(double var1, double var3, int var5);

   default boolean process(double var1, double var3, double var5, double var7) {
      return false;
   }

   default Setting compute() {
      return null;
   }

   default boolean resolve() {
      return false;
   }
}
