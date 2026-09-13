package ru.wild.gui.screen;

import ru.wild.modules.visuals.Menu;

public final class MenuAnimationClock {
   private MenuAnimationClock() {
   }

   public static boolean handle() {
      try {
         return !Menu.playerCollect.compute();
      } catch (Throwable var1) {
         return true;
      }
   }

   public static boolean process() {
      try {
         return !Menu.entryAnimate.compute();
      } catch (Throwable var1) {
         return true;
      }
   }

   public static boolean compute() {
      try {
         return !Menu.matrixFilter.compute();
      } catch (Throwable var1) {
         return true;
      }
   }

   public static boolean resolve() {
      try {
         return Menu.stateApply.compute();
      } catch (Throwable var1) {
         return false;
      }
   }

   public static double update() {
      return resolve() ? 0.55 : 1.0;
   }

   public static float apply() {
      return process() ? 1.0F : 0.45F;
   }

   public static float execute() {
      return process() ? 1.0F : 0.6F;
   }
}
