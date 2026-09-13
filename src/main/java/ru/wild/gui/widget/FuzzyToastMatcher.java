package ru.wild.gui.widget;

import java.util.Locale;
import ru.wild.render.shader.ShaderNodeDefinition;

public final class FuzzyToastMatcher {
   private FuzzyToastMatcher() {
   }

   public static FuzzyToastMatcher.DataRecord handle(ShaderNodeDefinition var0, String var1) {
      String var2 = var0.process().toLowerCase(Locale.ROOT);
      int var3 = 0;
      int[] var4 = handle(var2, var1);
      if (var4 != null) {
         var3 += 30;
         if (var2.startsWith(var1)) {
            var3 += 90;
         } else if (var2.contains(var1)) {
            var3 += 48;
         }

         int var5 = -2;

         for (int var9 : var4) {
            if (var9 == var5 + 1) {
               var3 += 10;
            } else if (var9 > var5 + 1 && var5 >= 0) {
               var3 -= Math.min(var9 - var5 - 1, 6);
            }

            if (var9 == 0 || handle(var2.charAt(var9 - 1))) {
               var3 += 14;
            }

            var5 = var9;
         }
      }

      String var10 = var0.handle().toLowerCase(Locale.ROOT);
      String var11 = var0.compute().toLowerCase(Locale.ROOT);
      if (var10.contains(var1)) {
         var3 += 22;
      }

      if (var11.contains(var1)) {
         var3 += 10;
      }

      return var3 <= 0 ? null : new FuzzyToastMatcher.DataRecord(var0, var3, var4 == null ? new int[0] : var4);
   }

   public static int[] handle(String var0, String var1) {
      int[] var2 = new int[var1.length()];
      int var3 = 0;

      for (int var4 = 0; var4 < var1.length(); var4++) {
         int var5 = var0.indexOf(var1.charAt(var4), var3);
         if (var5 < 0) {
            return null;
         }

         var2[var4] = var5;
         var3 = var5 + 1;
      }

      return var2;
   }

   public static boolean handle(char var0) {
      return var0 == ' ' || var0 == '_' || var0 == '.' || var0 == '-' || var0 == '(' || var0 == '/';
   }

   public record DataRecord(ShaderNodeDefinition def, int score, int[] titlePositions) {
   }
}
