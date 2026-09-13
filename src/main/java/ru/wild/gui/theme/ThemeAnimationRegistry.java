package ru.wild.gui.theme;

import java.util.EnumMap;
import java.util.Map;

public final class ThemeAnimationRegistry {
   private static final Map<ThemePalette, int[]> instance = new EnumMap<>(ThemePalette.class);
   private static final Map<ThemePalette, ThemeAnimationStyle> data = new EnumMap<>(ThemePalette.class);

   private ThemeAnimationRegistry() {
   }

   private static void handle(ThemePalette var0, ThemeAnimationStyle var1, int... var2) {
      instance.put(var0, var2);
      data.put(var0, var1);
   }

   public static int[] handle(ThemePalette var0) {
      return var0 == null ? null : instance.get(var0);
   }

   public static ThemeAnimationStyle process(ThemePalette var0) {
      if (var0 == null) {
         return ThemeAnimationStyle.STATIC;
      }

      ThemeAnimationStyle var1 = data.get(var0);
      return var1 == null ? ThemeAnimationStyle.STATIC : var1;
   }

   public static boolean compute(ThemePalette var0) {
      return var0 != null && data.get(var0) != null && data.get(var0) != ThemeAnimationStyle.STATIC;
   }

   static {
      handle(ThemePalette.ASTOLFO_RAINBOW, ThemeAnimationStyle.MULTI_GRADIENT, -29969, -22820, -8128257, -5636114, -19472, -29969);
      handle(ThemePalette.LAGUNE_RAINBOW, ThemeAnimationStyle.TWIN_LAYERS, -10027033, -10762241, -12088321, -5046284, -9649409, -10027033);
      handle(ThemePalette.HALF_RAINBOW, ThemeAnimationStyle.MULTI_GRADIENT, -3989, -25262, -41059, -7473153, -9856, -3989);
      handle(ThemePalette.AURORA_RAINBOW, ThemeAnimationStyle.MULTI_GRADIENT, -5636168, -10291758, -8549121, -22028, -8650800, -5636168);
      handle(ThemePalette.NEON_RAINBOW, ThemeAnimationStyle.HUE_WHEEL, -8519833, -13371393, -4006, -49678, -8519833);
      handle(ThemePalette.BLOSSOM_RAINBOW, ThemeAnimationStyle.MULTI_GRADIENT, -22584, -8049, -5777153, -2709505, -14116, -22584);
      handle(ThemePalette.ABYSS_RAINBOW, ThemeAnimationStyle.TWIN_LAYERS, -8743937, -5215233, -10813482, -41074, -8022017, -8743937);
      handle(ThemePalette.SUNSET_RAINBOW, ThemeAnimationStyle.MULTI_GRADIENT, -19622, -38070, -45175, -2781953, -18320, -19622);
      handle(ThemePalette.GLACIER_RAINBOW, ThemeAnimationStyle.BREATHING, -2688001, -7607553, -7627265, -1, -4725249, -2688001);
      handle(ThemePalette.CHROMA_RAINBOW, ThemeAnimationStyle.HUE_WHEEL, -41107, -7076, -10616904, -9597697, -41107);
      handle(ThemePalette.DREAM_RAINBOW, ThemeAnimationStyle.MULTI_GRADIENT, -2775297, -24613, -9043994, -3956, -1722881, -2775297);
      handle(ThemePalette.TOXIC_RAINBOW, ThemeAnimationStyle.HUE_WHEEL, -3604664, -3750, -13044993, -41759, -3604664);
      handle(ThemePalette.AURORA_BOREALIS, ThemeAnimationStyle.MULTI_GRADIENT, -10747980, -9730561, -37947, -14997, -9699351, -10747980);
      handle(ThemePalette.TOKYO_NEON, ThemeAnimationStyle.HUE_WHEEL, -57736, -12255278, -8691201, -74951, -57736);
      handle(ThemePalette.GALAXY, ThemeAnimationStyle.MULTI_GRADIENT, -9740289, -5350401, -41035, -10498049, -7000, -9740289);
      handle(ThemePalette.LAVA, ThemeAnimationStyle.TWIN_LAYERS, -21955, -38083, -49828, -3922881, -9877, -21955);
      handle(ThemePalette.FROST, ThemeAnimationStyle.BREATHING, -5707521, -9710593, -11552257, -8410881, -4853505, -5707521);
      handle(ThemePalette.SAKURA, ThemeAnimationStyle.MULTI_GRADIENT, -13860, -24372, -32843, -2055937, -10264, -13860);
      handle(ThemePalette.FOREST_MIST, ThemeAnimationStyle.MULTI_GRADIENT, -9699390, -11541080, -13057392, -7340071, -10485842, -9699390);
      handle(ThemePalette.COSMIC_LATTE, ThemeAnimationStyle.BREATHING, -6752, -669313, -1660831, -13126, -8011, -6752);
      handle(ThemePalette.SYNTHWAVE, ThemeAnimationStyle.TWIN_LAYERS, -51019, -13056513, -297473, -21668, -8692737, -51019);
      handle(ThemePalette.HOLOGRAPHIC, ThemeAnimationStyle.PRISMATIC_WAVE, -24321, -6225921, -96, -6226016, -3104513, -24321);
      handle(ThemePalette.MIDNIGHT_AZURE, ThemeAnimationStyle.BREATHING, -16715521, -16733953, -16759553, -12292609, -2556929, -16715521);
      handle(ThemePalette.MIDNIGHT_OCEAN, ThemeAnimationStyle.TWIN_LAYERS, -12612097, -13057793, -9740289, -8734721, -5252609, -12612097);
      handle(ThemePalette.MAGMA, ThemeAnimationStyle.MULTI_GRADIENT, -22436, -37059, -49828, -3922881, -8776415, -22436);
      handle(ThemePalette.VERNAL_SOLSTICE, ThemeAnimationStyle.BREATHING, -13447886, -10496, -6291605, -1441815, -4720701, -3416);
      handle(ThemePalette.OBSIDIAN_EMBER, ThemeAnimationStyle.BREATHING, -42198, -25531, -14221);
      handle(ThemePalette.GLACIER_VEIL, ThemeAnimationStyle.TWIN_LAYERS, -5706497, -11352065, -12681729, -8390688, -5706497);
      handle(ThemePalette.VELVET_DUSK, ThemeAnimationStyle.MULTI_GRADIENT, -3563265, -8635667, -34106, -14221, -3563265);
      handle(ThemePalette.PORCELAIN_DAWN, ThemeAnimationStyle.BREATHING, -20342, -30116, -13912);
      handle(ThemePalette.FRUTIGER_AERO, ThemeAnimationStyle.MULTI_GRADIENT, -8657678, -13121888, -7346033, -13722666, -8657678);
   }
}
