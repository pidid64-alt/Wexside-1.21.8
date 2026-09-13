package ru.wild.util.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DimensionLabelRegistry {
   private static final List<Waypoint> instance = new ArrayList<>();

   private DimensionLabelRegistry() {
   }

   public static void handle(Waypoint var0) {
      handle(var0.handle());
      instance.add(var0);
   }

   public static boolean handle(String var0) {
      boolean var1 = false;

      for (int var2 = 0; var2 < instance.size(); var2++) {
         Waypoint var3 = instance.get(var2);
         if (var3.update() && var3.handle().equalsIgnoreCase(var0)) {
            var3.apply();
            var1 = true;
         }
      }

      return var1;
   }

   public static int handle() {
      int var0 = 0;

      for (int var1 = 0; var1 < instance.size(); var1++) {
         Waypoint var2 = instance.get(var1);
         if (var2.update()) {
            var2.apply();
            var0++;
         }
      }

      return var0;
   }

   public static void process() {
      for (int var0 = instance.size() - 1; var0 >= 0; var0--) {
         if (instance.get(var0).execute()) {
            instance.remove(var0);
         }
      }
   }

   public static boolean compute() {
      return instance.isEmpty();
   }

   public static int resolve() {
      return instance.size();
   }

   public static Waypoint handle(int var0) {
      return instance.get(var0);
   }

   public static Waypoint process(String var0) {
      for (int var1 = 0; var1 < instance.size(); var1++) {
         Waypoint var2 = instance.get(var1);
         if (var2.update() && var2.handle().equalsIgnoreCase(var0)) {
            return var2;
         }
      }

      return null;
   }

   public static int update() {
      int var0 = 0;

      for (int var1 = 0; var1 < instance.size(); var1++) {
         if (instance.get(var1).update()) {
            var0++;
         }
      }

      return var0;
   }

   public static List<String> apply() {
      ArrayList var0 = new ArrayList(instance.size());

      for (int var1 = 0; var1 < instance.size(); var1++) {
         Waypoint var2 = instance.get(var1);
         if (var2.update()) {
            var0.add(var2.handle());
         }
      }

      return var0;
   }

   public static String compute(String var0) {
      for (int var1 = 1; var1 < 1000; var1++) {
         String var2 = var0 + " " + var1;
         if (!resolve(var2)) {
            return var2;
         }
      }

      return var0;
   }

   private static boolean resolve(String var0) {
      for (int var1 = 0; var1 < instance.size(); var1++) {
         Waypoint var2 = instance.get(var1);
         if (var2.update() && var2.handle().toLowerCase(Locale.ROOT).equals(var0.toLowerCase(Locale.ROOT))) {
            return true;
         }
      }

      return false;
   }
}
