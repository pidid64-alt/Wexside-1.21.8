package ru.wild.gui.theme;

import java.awt.Color;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.SmoothTimer;

public enum ThemePalette {
   WILD("Wild", new Color(6061311), new Color(658192), new Color(1119002), new Color(1974835), new Color(16777215), new Color(9344166)),
   CHERRY("Cherry", new Color(16731501), new Color(1182219), new Color(1708050), new Color(3021599), new Color(16777215), new Color(10915474)),
   SUN("Sun", new Color(16758531), new Color(1183754), new Color(1709840), new Color(3024923), new Color(16777215), new Color(10920078)),
   MINT("Mint", new Color(62932), new Color(594448), new Color(924183), new Color(1519144), new Color(16777215), new Color(9283231)),
   AQUA("Aqua", new Color(48121), new Color(593938), new Color(923674), new Color(1517614), new Color(16777215), new Color(9346726)),
   AMETHYST("Amethyst", new Color(10309341), new Color(919826), new Color(1379866), new Color(2430766), new Color(16777215), new Color(10063526)),
   ROSE("Rose", new Color(15817653), new Color(1181968), new Color(1707543), new Color(3020584), new Color(16777215), new Color(10915485)),
   TANGERINE("Tangerine", new Color(16752494), new Color(1182472), new Color(1773580), new Color(3809045), new Color(16777215), new Color(11967119)),
   ORCHID("Orchid", new Color(16743132), new Color(1181716), new Color(1772829), new Color(3612734), new Color(16777215), new Color(12163765)),
   NEBULA("Nebula", new Color(8223999), new Color(527126), new Color(1053479), new Color(2238810), new Color(16777215), new Color(10199750)),
   AURORA("Aurora", new Color(6354376), new Color(463375), new Color(858648), new Color(1456948), new Color(16777215), new Color(9549229)),
   VOLT("Volt", new Color(13565765), new Color(987655), new Color(1514251), new Color(3423506), new Color(16777215), new Color(11385230)),
   CYBER("Cyber", new Color(3732223), new Color(397332), new Color(661278), new Color(1064007), new Color(16777215), new Color(9549244)),
   CORAL("Coral", new Color(16740234), new Color(1312778), new Color(1903889), new Color(4332840), new Color(16777215), new Color(12358557)),
   LAGOON("Lagoon", new Color(5105335), new Color(397841), new Color(728092), new Color(1195330), new Color(16777215), new Color(9353397)),
   LOTUS("Lotus", new Color(16751305), new Color(1181967), new Color(1773336), new Color(4202545), new Color(16777215), new Color(12425898)),
   ARCTIC("Arctic", new Color(12122111), new Color(462869), new Color(858145), new Color(1784394), new Color(16777215), new Color(10467522)),
   PEACOCK("Peacock", new Color(5822207), new Color(462100), new Color(791840), new Color(1454681), new Color(16777215), new Color(9545922)),
   CANDY("Candy", new Color(16747250), new Color(1181971), new Color(1838365), new Color(4070723), new Color(16777215), new Color(12688061)),
   MATRIX("Matrix", new Color(6094714), new Color(463367), new Color(858894), new Color(1522716), new Color(16777215), new Color(9615511)),
   BLOODMOON("Bloodmoon", new Color(16728157), new Color(1312263), new Color(1903117), new Color(4526873), new Color(16777215), new Color(12357269)),
   NOIR("Noir", new Color(14276607), new Color(526347), new Color(1118486), new Color(2697270), new Color(16777215), new Color(11118775)),
   PRISM("Prism", new Color(9240538), new Color(527634), new Color(1053982), new Color(2829914), new Color(16777215), new Color(10663357)),
   VELVET("Velvet", new Color(14052351), new Color(919571), new Color(1511455), new Color(3416391), new Color(16777215), new Color(11179708)),
   ASTOLFO_RAINBOW("Astolfo Rainbow", new Color(16747247), new Color(1181971), new Color(1772829), new Color(4202826), new Color(16777215), new Color(12884668)),
   LAGUNE_RAINBOW("Lagune Rainbow", new Color(3602431), new Color(397844), new Color(727584), new Color(1196107), new Color(16777215), new Color(9550274)),
   HALF_RAINBOW("0.5 Rainbow", new Color(16773227), new Color(1118214), new Color(1644298), new Color(3814161), new Color(16777215), new Color(12103821)),
   AURORA_RAINBOW("Aurora Rainbow", new Color(7536600), new Color(397838), new Color(662295), new Color(1326390), new Color(16777215), new Color(9550510)),
   NEON_RAINBOW("Neon Rainbow", new Color(6618986), new Color(462096), new Color(791578), new Color(1519933), new Color(16777215), new Color(9745080)),
   BLOSSOM_RAINBOW("Blossom Rainbow", new Color(16749240), new Color(1181966), new Color(1772823), new Color(4202547), new Color(16777215), new Color(12491438)),
   ABYSS_RAINBOW("Abyss Rainbow", new Color(7510271), new Color(395538), new Color(725024), new Color(1518164), new Color(16777215), new Color(9937860)),
   SUNSET_RAINBOW("Sunset Rainbow", new Color(16747610), new Color(1247495), new Color(1904651), new Color(4597525), new Color(16777215), new Color(12491662)),
   GLACIER_RAINBOW("Glacier Rainbow", new Color(11729151), new Color(462868), new Color(792352), new Color(1521739), new Color(16777215), new Color(10336196)),
   CHROMA_RAINBOW("Chroma Rainbow", new Color(13172570), new Color(724233), new Color(1185551), new Color(3160090), new Color(16777215), new Color(11188110)),
   DREAM_RAINBOW("Dream Rainbow", new Color(13147391), new Color(854292), new Color(1380384), new Color(3351370), new Color(16777215), new Color(11180994)),
   TOXIC_RAINBOW("Toxic Rainbow", new Color(11009853), new Color(659974), new Color(1055242), new Color(2506002), new Color(16777215), new Color(10795406)),
   AURORA_BOREALIS("Aurora Borealis", new Color(5167557), new Color(462610), new Color(989727), new Color(1454650), new Color(16777215), new Color(9483706)),
   TOKYO_NEON("Tokyo Neon", new Color(16735705), new Color(919314), new Color(1576224), new Color(3545672), new Color(16777215), new Color(12949704)),
   GALAXY("Galaxy", new Color(10190847), new Color(329231), new Color(921375), new Color(2040136), new Color(16777215), new Color(9342911)),
   LAVA("Lava", new Color(16739133), new Color(1312518), new Color(2034954), new Color(5054226), new Color(16777215), new Color(12619908)),
   FROST("Frost", new Color(7066623), new Color(462612), new Color(924192), new Color(1587266), new Color(16777215), new Color(9811645)),
   SAKURA("Sakura", new Color(16752844), new Color(1313295), new Color(1970200), new Color(4203061), new Color(16777215), new Color(12752298)),
   FOREST_MIST("Forest Mist", new Color(5236136), new Color(397837), new Color(859158), new Color(1588016), new Color(16777215), new Color(9549480)),
   COSMIC_LATTE("Cosmic Latte", new Color(16107903), new Color(1117704), new Color(1775116), new Color(4142368), new Color(16777215), new Color(12562062)),
   SYNTHWAVE("Synthwave", new Color(16726197), new Color(722710), new Color(1313828), new Color(3151953), new Color(16777215), new Color(11704517)),
   HOLOGRAPHIC("Holographic", new Color(13150463), new Color(591887), new Color(1184032), new Color(2894405), new Color(16777215), new Color(11052748)),
   MIDNIGHT_AZURE("Midnight Azure", new Color(61695), new Color(198418), new Color(463135), new Color(867430), new Color(16777215), new Color(12446463)),
   MIDNIGHT_OCEAN("Midnight Ocean", new Color(4165119), new Color(329743), new Color(725536), new Color(1386568), new Color(16777215), new Color(8954045)),
   MAGMA("Magma", new Color(16735277), new Color(984326), new Color(1706248), new Color(5052432), new Color(16777215), new Color(12619652)),
   VERNAL_SOLSTICE("Vernal Solstice", new Color(3329330), new Color(16580348), new Color(16777215), new Color(14283481), new Color(332037), new Color(332037)),
   SAKURA_BREEZE("Sakura Breeze", new Color(16758725), new Color(16580094), new Color(16777215), new Color(16777215), new Color(1710618), new Color(6710886)),
   OBSIDIAN_EMBER("Obsidian Ember", new Color(16738867), new Color(789003), new Color(1446164), new Color(2826534), new Color(16183272), new Color(11114637)),
   GLACIER_VEIL("Glacier Veil", new Color(5425151), new Color(463648), new Color(794416), new Color(1913672), new Color(15398655), new Color(9876166)),
   VELVET_DUSK("Velvet Dusk", new Color(10312959), new Color(1248288), new Color(1839664), new Color(3285575), new Color(16052219), new Color(10983106)),
   PORCELAIN_DAWN("Porcelain Dawn", new Color(16747100), new Color(16645112), new Color(16777215), new Color(15327960), new Color(2235157), new Color(7036755)),
   FRUTIGER_AERO("Frutiger Aero", new Color(3131874), new Color(15398650), new Color(16777215), new Color(13494770), new Color(928307), new Color(5141120)),
   CUSTOM("Custom", new Color(16777215), new Color(657930), new Color(1184274), new Color(2368548), new Color(16777215), new Color(10066329));

   public final String instance;
   private Color context;
   private Color config;
   private Color state;
   private Color cache;
   private Color output;
   private Color current;
   public final SmoothTimer data = new EaseTimer(300, 1.0);

   ThemePalette(String var3, Color var4, Color var5, Color var6, Color var7, Color var8, Color var9) {
      this.instance = var3;
      this.context = var4;
      this.config = var5;
      this.state = var6;
      this.cache = var7;
      this.output = var8;
      this.current = var9;
   }

   public void handle(Color var1, Color var2, Color var3, Color var4, Color var5, Color var6) {
      if (this == CUSTOM) {
         this.context = var1;
         this.config = var2;
         this.state = var3;
         this.cache = var4;
         this.output = var5;
         this.current = var6;
      }
   }
   public Color handle() {
      return this.context;
   }
   public Color process() {
      return this.config;
   }
   public Color compute() {
      return this.state;
   }
   public Color resolve() {
      return this.cache;
   }
   public Color update() {
      return this.output;
   }
   public Color apply() {
      return this.current;
   }
   public SmoothTimer execute() {
      return this.data;
   }
}
