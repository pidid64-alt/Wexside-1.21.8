package ru.wild.gui.theme;

import java.util.Arrays;
import net.minecraft.util.Identifier;
import ru.wild.api.module.AnticheatProfiles;
import ru.wild.render.texture.SvgHelper;

public final class AnticheatProfileColor {
   public static final int instance = 48;
   private static final Identifier[] data = new Identifier[AnticheatProfiles.Mode.values().length];
   private static final int[] context = new int[data.length];
   private static final int[] config = new int[data.length];
   private static final long[] state = new long[data.length];
   private static final long cache = 250000000L;

   private AnticheatProfileColor() {
   }

   public static int handle(AnticheatProfiles.Mode var0) {
      return handle(var0, 48);
   }

   public static int handle(AnticheatProfiles.Mode var0, int var1) {
      int var2 = var0.ordinal();
      Identifier var3 = data[var2];
      if (var3 == null) {
         return -1;
      } else {
         int var4 = handle(var1);
         int var5 = context[var2];
         if (config[var2] == var4 && var5 > 0) {
            return var5;
         } else {
            long var6 = System.nanoTime();
            if (config[var2] == var4 && var6 < state[var2]) {
               return -1;
            } else {
               int var8 = SvgHelper.handle(var3, var4, true);
               if (var8 > 0) {
                  config[var2] = var4;
                  context[var2] = var8;
                  state[var2] = 0L;
                  return var8;
               } else {
                  config[var2] = var4;
                  context[var2] = 0;
                  state[var2] = var6 + 250000000L;
                  return -1;
               }
            }
         }
      }
   }

   public static void handle() {
      handle(AnticheatProfiles.Mode.MATRIX, 48);
      handle(AnticheatProfiles.Mode.GRIM, 48);
   }

   private static int handle(int var0) {
      return Math.max(8, Math.min(512, var0));
   }

   public static void process() {
      Arrays.fill(context, 0);
      Arrays.fill(config, 0);
      Arrays.fill(state, 0L);
   }

   static {
      data[AnticheatProfiles.Mode.MATRIX.ordinal()] = Identifier.of(AnticheatProfiles.Mode.MATRIX.process());
      data[AnticheatProfiles.Mode.GRIM.ordinal()] = Identifier.of(AnticheatProfiles.Mode.GRIM.process());
   }
}
