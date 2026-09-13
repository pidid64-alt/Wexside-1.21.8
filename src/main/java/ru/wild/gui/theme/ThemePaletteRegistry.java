package ru.wild.gui.theme;

import java.util.Arrays;
import java.util.List;
import ru.wild.util.render.PackedColor;

public final class ThemePaletteRegistry {
   public static final int instance = -1314568;
   public static final int data = -858662426;
   public static final int context = -1;
   public static final int config = -15328994;
   public static final int state = -435483376;
   public static final int cache = 1713580081;
   private static final ThemePaletteRegistry output = process();
   private final List<ThemePaletteRegistry.ColorState> current;

   public static ThemePaletteRegistry.ColorStop handle(boolean var0) {
      return var0 ? new ThemePaletteRegistry.ColorStop(-1314568, -858662426, -1) : new ThemePaletteRegistry.ColorStop(-15328994, -435483376, 1713580081);
   }

   public static ThemePaletteRegistry handle() {
      return output;
   }

   public static ThemePaletteRegistry process() {
      return new ThemePaletteRegistry(
         List.of(
            new ThemePaletteRegistry.ColorState("Sakura Breeze", ThemePalette.SAKURA_BREEZE, true, -18491, -16181, -1541, -18491, -16181, -1),
            new ThemePaletteRegistry.ColorState(
               "Vernal Solstice", ThemePalette.VERNAL_SOLSTICE, true, -13447886, -10496, -13447886, -10496, -6291605, -1441815, -4720701, -3416
            ),
            new ThemePaletteRegistry.ColorState("Porcelain Dawn", ThemePalette.PORCELAIN_DAWN, true, -20342, -30116, -20342, -30116, -13912),
            new ThemePaletteRegistry.ColorState(
               "Frutiger Aero", ThemePalette.FRUTIGER_AERO, true, -8657678, -13722666, -8657678, -13121888, -7346033, -13722666, -8657678
            ),
            new ThemePaletteRegistry.ColorState(
               "Midnight Azure", ThemePalette.MIDNIGHT_AZURE, -16715521, -16759553, -16715521, -16733953, -16759553, -12292609, -2556929, -16715521
            ),
            new ThemePaletteRegistry.ColorState("Cherry", ThemePalette.CHERRY, -24930, -32126),
            new ThemePaletteRegistry.ColorState("Rose", ThemePalette.ROSE, -24854, -32032),
            new ThemePaletteRegistry.ColorState("Sun", ThemePalette.SUN, -196706, -126),
            new ThemePaletteRegistry.ColorState("Tangerine", ThemePalette.TANGERINE, -15458, -29342),
            new ThemePaletteRegistry.ColorState("Amethyst", ThemePalette.AMETHYST, -6185217, -7437569),
            new ThemePaletteRegistry.ColorState("Aqua", ThemePalette.AQUA, -6364417, -8198913),
            new ThemePaletteRegistry.ColorState("Mint", ThemePalette.MINT, -6357069, -8192089),
            new ThemePaletteRegistry.ColorState("Teal", ThemePalette.LAGOON, -6357027, -10095918),
            new ThemePaletteRegistry.ColorState("Blush", ThemePalette.LOTUS, -24894, -32076),
            new ThemePaletteRegistry.ColorState("Orchid", ThemePalette.ORCHID, -23827, -36913),
            new ThemePaletteRegistry.ColorState("Nebula", ThemePalette.NEBULA, -5921793, -9603841),
            new ThemePaletteRegistry.ColorState("Aurora", ThemePalette.AURORA, -6357021, -11341636),
            new ThemePaletteRegistry.ColorState("Volt", ThemePalette.VOLT, -1507446, -4718787),
            new ThemePaletteRegistry.ColorState("Cyber", ThemePalette.CYBER, -7406593, -12985857),
            new ThemePaletteRegistry.ColorState("Coral", ThemePalette.CORAL, -25682, -41090),
            new ThemePaletteRegistry.ColorState("Arctic", ThemePalette.ARCTIC, -2622209, -7607553),
            new ThemePaletteRegistry.ColorState("Peacock", ThemePalette.PEACOCK, -8130305, -10841345),
            new ThemePaletteRegistry.ColorState("Candy", ThemePalette.CANDY, -22281, -6293249),
            new ThemePaletteRegistry.ColorState("Matrix", ThemePalette.MATRIX, -7471205, -12648600),
            new ThemePaletteRegistry.ColorState("Bloodmoon", ThemePalette.BLOODMOON, -30569, -52402),
            new ThemePaletteRegistry.ColorState("Noir", ThemePalette.NOIR, -987137, -5790503),
            new ThemePaletteRegistry.ColorState("Prism", ThemePalette.PRISM, -6422567, -6316289),
            new ThemePaletteRegistry.ColorState("Velvet", ThemePalette.VELVET, -1923585, -4756993),
            new ThemePaletteRegistry.ColorState("Custom", ThemePalette.CUSTOM, -1, -7433050),
            new ThemePaletteRegistry.ColorState("Astolfo-rainbow", ThemePalette.ASTOLFO_RAINBOW, -29969, -8128257, -29969, -22820, -8128257, -5636114),
            new ThemePaletteRegistry.ColorState("Lagune-rainbow", ThemePalette.LAGUNE_RAINBOW, -10027033, -12088321, -10027033, -10762241, -12088321, -5046284),
            new ThemePaletteRegistry.ColorState("0.5-rainbow", ThemePalette.HALF_RAINBOW, -3989, -41059, -3989, -25262, -41059, -7473153),
            new ThemePaletteRegistry.ColorState("Aurora-rainbow", ThemePalette.AURORA_RAINBOW, -5636168, -8549121, -5636168, -10291758, -8549121, -22028),
            new ThemePaletteRegistry.ColorState("Neon-rainbow", ThemePalette.NEON_RAINBOW, -8519833, -49678, -8519833, -13371393, -4006, -49678),
            new ThemePaletteRegistry.ColorState("Blossom-rainbow", ThemePalette.BLOSSOM_RAINBOW, -22584, -5777153, -22584, -8049, -5777153, -2709505),
            new ThemePaletteRegistry.ColorState("Abyss-rainbow", ThemePalette.ABYSS_RAINBOW, -8743937, -10813482, -8743937, -5215233, -10813482, -41074),
            new ThemePaletteRegistry.ColorState("Sunset-rainbow", ThemePalette.SUNSET_RAINBOW, -19622, -45175, -19622, -38070, -45175, -2781953),
            new ThemePaletteRegistry.ColorState("Glacier-rainbow", ThemePalette.GLACIER_RAINBOW, -2688001, -7627265, -2688001, -7607553, -7627265, -1),
            new ThemePaletteRegistry.ColorState("Chroma-rainbow", ThemePalette.CHROMA_RAINBOW, -41107, -10616904, -41107, -7076, -10616904, -9597697),
            new ThemePaletteRegistry.ColorState("Dream-rainbow", ThemePalette.DREAM_RAINBOW, -2775297, -9043994, -2775297, -24613, -9043994, -3956),
            new ThemePaletteRegistry.ColorState("Toxic-rainbow", ThemePalette.TOXIC_RAINBOW, -3604664, -13044993, -3604664, -3750, -13044993, -41759),
            new ThemePaletteRegistry.ColorState("Aurora Borealis", ThemePalette.AURORA_BOREALIS, -10747980, -9730561, -10747980, -37947, -14997, -9699351),
            new ThemePaletteRegistry.ColorState("Tokyo Neon", ThemePalette.TOKYO_NEON, -57736, -12255278, -57736, -8691201, -74951, -12255278),
            new ThemePaletteRegistry.ColorState("Galaxy", ThemePalette.GALAXY, -9740289, -41035, -9740289, -5350401, -41035, -10498049),
            new ThemePaletteRegistry.ColorState("Lava", ThemePalette.LAVA, -21955, -49828, -21955, -38083, -49828, -9877),
            new ThemePaletteRegistry.ColorState("Frost", ThemePalette.FROST, -5707521, -9710593, -5707521, -11552257, -8410881, -4853505),
            new ThemePaletteRegistry.ColorState("Sakura", ThemePalette.SAKURA, -13860, -32843, -13860, -24372, -32843, -2055937),
            new ThemePaletteRegistry.ColorState("Forest Mist", ThemePalette.FOREST_MIST, -9699390, -13057392, -9699390, -11541080, -13057392, -7340071),
            new ThemePaletteRegistry.ColorState("Cosmic Latte", ThemePalette.COSMIC_LATTE, -6752, -1660831, -6752, -669313, -1660831, -13126),
            new ThemePaletteRegistry.ColorState("Synthwave", ThemePalette.SYNTHWAVE, -51019, -13056513, -51019, -297473, -21668, -8692737),
            new ThemePaletteRegistry.ColorState("Holographic", ThemePalette.HOLOGRAPHIC, -24321, -6225921, -24321, -96, -6226016, -3104513),
            new ThemePaletteRegistry.ColorState("Midnight Ocean", ThemePalette.MIDNIGHT_OCEAN, -12612097, -9740289, -12612097, -13057793, -9740289, -5252609),
            new ThemePaletteRegistry.ColorState("Magma", ThemePalette.MAGMA, -22436, -49828, -22436, -37059, -49828, -3922881),
            new ThemePaletteRegistry.ColorState("Obsidian Ember", ThemePalette.OBSIDIAN_EMBER, -20119, -42198, -42198, -25531, -14221),
            new ThemePaletteRegistry.ColorState("Glacier Veil", ThemePalette.GLACIER_VEIL, -5706497, -12681729, -5706497, -11352065, -12681729, -8390688),
            new ThemePaletteRegistry.ColorState("Velvet Dusk", ThemePalette.VELVET_DUSK, -3563265, -8635667, -3563265, -8635667, -34106, -14221)
         )
      );
   }

   public int handle(ThemePalette var1) {
      for (int var2 = 0; var2 < this.current.size(); var2++) {
         if (this.current.get(var2).compute() == var1) {
            return var2;
         }
      }

      return -1;
   }

   public ThemePaletteRegistry.ColorState process(ThemePalette var1) {
      for (ThemePaletteRegistry.ColorState var3 : this.current) {
         if (var3.compute() == var1) {
            return var3;
         }
      }

      return null;
   }

   public boolean compute(ThemePalette var1) {
      ThemePaletteRegistry.ColorState var2 = this.process(var1);
      return var2 != null && var2.apply();
   }

   public int resolve(ThemePalette var1) {
      if (var1 == ThemePalette.CUSTOM) {
         return PackedColor.compute(handle(var1.handle().getRGB()), -1, 0.22F);
      } else {
         ThemePaletteRegistry.ColorState var2 = this.process(var1);
         if (var2 != null) {
            return handle(var2.resolve());
         } else {
            return var1 == ThemePalette.WILD ? -6375937 : PackedColor.compute(handle(var1.handle().getRGB()), -1, 0.22F);
         }
      }
   }

   public int update(ThemePalette var1) {
      if (var1 == ThemePalette.CUSTOM) {
         return handle(var1.handle().getRGB());
      } else {
         ThemePaletteRegistry.ColorState var2 = this.process(var1);
         if (var2 != null) {
            return handle(var2.update());
         } else {
            return var1 == ThemePalette.WILD ? -8216577 : handle(var1.handle().getRGB());
         }
      }
   }

   private static int handle(int var0) {
      return 0xFF000000 | var0 & 16777215;
   }
   public ThemePaletteRegistry(List<ThemePaletteRegistry.ColorState> var1) {
      this.current = var1;
   }
   public List<ThemePaletteRegistry.ColorState> compute() {
      return this.current;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ThemePaletteRegistry var2)) {
         return false;
      } else {
         List var3 = this.compute();
         List var4 = var2.compute();
         if (var3 == null) {
            if (var4 != null) {
               return false;
            }
         } else if (!var3.equals(var4)) {
            return false;
         }

         return true;
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      List var3 = this.compute();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
   @Override
   public String toString() {
      return "ThemePalette(swatches=" + this.compute() + ")";
   }

   public static final class ColorState {
      private final String instance;
      private final ThemePalette data;
      private final int context;
      private final int config;
      private final boolean state;
      private final int[] cache;

      public ColorState(String var1, ThemePalette var2, int var3, int var4) {
         this(var1, var2, var3, var4, var3, var4);
      }

      public ColorState(String var1, ThemePalette var2, int var3, int var4, int... var5) {
         this(var1, var2, false, var3, var4, var5);
      }

      public ColorState(String var1, ThemePalette var2, boolean var3, int var4, int var5, int... var6) {
         this.instance = var1;
         this.data = var2;
         this.context = var4;
         this.config = var5;
         this.state = var3;
         this.cache = var6 != null && var6.length >= 2 ? var6 : new int[]{var4, var5};
      }

      public ThemePaletteRegistry.Mode handle() {
         return ThemePaletteRegistry.Mode.handle(this.data);
      }
      public String process() {
         return this.instance;
      }
      public ThemePalette compute() {
         return this.data;
      }
      public int resolve() {
         return this.context;
      }
      public int update() {
         return this.config;
      }
      public boolean apply() {
         return this.state;
      }
      public int[] execute() {
         return this.cache;
      }
      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ThemePaletteRegistry.ColorState var2)) {
            return false;
         } else if (this.resolve() != var2.resolve()) {
            return false;
         } else if (this.update() != var2.update()) {
            return false;
         } else if (this.apply() != var2.apply()) {
            return false;
         } else {
            String var3 = this.process();
            String var4 = var2.process();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ThemePalette var5 = this.compute();
               ThemePalette var6 = var2.compute();
               return (var5 == null ? var6 == null : var5.equals(var6)) ? Arrays.equals(this.execute(), var2.execute()) : false;
            } else {
               return false;
            }
         }
      }
      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.resolve();
         var2 = var2 * 59 + this.update();
         var2 = var2 * 59 + (this.apply() ? 79 : 97);
         String var3 = this.process();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         ThemePalette var4 = this.compute();
         var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
         return var2 * 59 + Arrays.hashCode(this.execute());
      }
      @Override
      public String toString() {
         return "ThemePalette.Swatch(displayName="
            + this.process()
            + ", theme="
            + this.compute()
            + ", top="
            + this.resolve()
            + ", bottom="
            + this.update()
            + ", lightMode="
            + this.apply()
            + ", stops="
            + Arrays.toString(this.execute())
            + ")";
      }
   }

   public record ColorStop(int baseColor, int darkShadowColor, int lightShadowColor) {
   }

   public enum Mode {
      CORE(0),
      BLOSSOM(1),
      AERO(2),
      BOTANICAL(3),
      CELESTIAL(4),
      PORCELAIN(5);

      private final int instance;

      Mode(int var3) {
         this.instance = var3;
      }

      public int handle() {
         return this.instance;
      }

      public static ThemePaletteRegistry.Mode handle(ThemePalette var0) {
         if (var0 == null) {
            return CORE;
         }

         return switch (var0) {
            case SAKURA_BREEZE, SAKURA, LOTUS, CANDY, BLOSSOM_RAINBOW -> BLOSSOM;
            case PORCELAIN_DAWN -> PORCELAIN;
            case FRUTIGER_AERO, GLACIER_VEIL, FROST, ARCTIC, AQUA, PEACOCK, LAGOON, LAGUNE_RAINBOW, GLACIER_RAINBOW -> AERO;
            case VERNAL_SOLSTICE, FOREST_MIST, MINT, AURORA, MATRIX, AURORA_BOREALIS, AURORA_RAINBOW -> BOTANICAL;
            case MIDNIGHT_AZURE, MIDNIGHT_OCEAN, NEBULA, GALAXY, NOIR, COSMIC_LATTE, HOLOGRAPHIC, ABYSS_RAINBOW, DREAM_RAINBOW -> CELESTIAL;
            default -> CORE;
         };
      }
   }
}
