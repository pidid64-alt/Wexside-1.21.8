package ru.wild.api.module;

public final class AnticheatProfiles {
   public static final float instance = 4.0F;
   public static final AnticheatProfiles.PrimaryMode data = AnticheatProfiles.PrimaryMode.SLIDING_KNOB;

   private AnticheatProfiles() {
   }

   public static float handle(float var0, float var1, float var2, float var3, float var4) {
      float var5 = Math.max(0.0F, Math.min(var4, Math.min(var2, var3)));
      float var6 = Math.abs(var0) - var2 + var5;
      float var7 = Math.abs(var1) - var3 + var5;
      float var8 = Math.max(var6, 0.0F);
      float var9 = Math.max(var7, 0.0F);
      return Math.min(Math.max(var6, var7), 0.0F) + (float)Math.hypot(var8, var9) - var5;
   }

   public static float handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = render(drawAnimation(var5));
      if (var6 <= 0.0F) {
         return 0.0F;
      }

      float var7 = (float)Math.exp(-Math.abs(var0) / 2.2F);
      float var8 = var1 - var3;
      float var9 = var2 - var4;
      float var10 = (float)Math.exp(-(var8 * var8 + var9 * var9) / 14400.0F);
      return drawAnimation(var7 * var10 * var6);
   }

   public static float handle(float var0, float var1) {
      return handle(var0, 4.0F, var1);
   }

   public static float handle(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, var1);
      float var4 = Math.max(var3, var0);
      float var5 = Math.min(var4, var3 * 4.2F);
      float var6 = var3 + (var5 - var3) * select(var2);
      return var6 + (var4 - var5) * refresh(var2);
   }

   public static float process(float var0, float var1) {
      return process(var0, 4.0F, var1);
   }

   public static float process(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(var1, var0));
      return var3 + (Math.max(var3, var0) - var3) * select(var2);
   }

   public static float compute(float var0, float var1) {
      return compute(var0, 4.0F, var1);
   }

   public static float compute(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(var1, var0));
      float var4 = Math.max(var3, var0 * 1.08F);
      return var3 + (var4 - var3) * select(var2);
   }

   public static float handle(float var0) {
      return tick(onTick(0.34F, 0.66F, var0));
   }

   public static float process(float var0) {
      return tick(onTick(0.5F, 0.88F, var0));
   }

   public static float compute(float var0) {
      float var1 = tick(drawAnimation(var0));
      float var2 = 1.0F - var1;
      return 1.0F - var2 * var2;
   }

   public static float resolve(float var0, float var1, float var2) {
      return drawAnimation((var0 - var1) / Math.max(1.0E-5F, var2));
   }

   public static float process(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = Math.max(Math.max(var2 - var0, var0 - var2 - var4), 0.0F);
      float var7 = Math.max(Math.max(var3 - var1, var1 - var3 - var5), 0.0F);
      float var8 = Math.max(1.0F, Math.min(var4, var5) * 0.34F);
      return (float)Math.exp(-(var6 * var6 + var7 * var7) / (var8 * var8));
   }

   public static float resolve(float var0) {
      return Math.max(0.0F, var0) * 0.22F;
   }

   public static float update(float var0) {
      return Math.max(0.0F, var0) * 0.44F;
   }

   public static float apply(float var0) {
      return Math.max(0.0F, var0) * 0.28F;
   }

   public static float execute(float var0) {
      return Math.max(0.0F, var0) * 0.39F;
   }

   public static float prepare(float var0) {
      return Math.max(0.0F, var0) * 0.24F;
   }

   public static float update(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(var2, var1));
      return tick(drawAnimation((var0 - var3) / Math.max(1.0E-5F, var1 - var3)));
   }

   public static float apply(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, var0 - var1);
      return tick(drawAnimation(var3 / Math.max(1.0E-5F, var2 * 1.25F)));
   }

   public static float execute(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, var0) * 0.5F;
      float var4 = select(resolve(var0), prepare(var0), drawAnimation(var2));
      return select(var3, var4, drawAnimation(var1));
   }

   public static float resolve(float var0, float var1) {
      float var2 = Math.max(0.0F, var0) * 0.5F;
      return select(var2, update(var0), drawAnimation(var1));
   }

   public static float update(float var0, float var1) {
      float var2 = Math.max(0.0F, var0) * 0.5F;
      return select(var2, apply(var0), drawAnimation(var1));
   }

   public static float prepare(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, var0) * 0.5F;
      float var4 = select(execute(var0), prepare(var0), drawAnimation(var2));
      return select(var3, var4, drawAnimation(var1));
   }

   public static float handle(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = Math.max(0.0F, var4) * 0.5F;
      float var11 = Math.max(0.0F, var5) * 0.5F;
      float var12 = var0 - var2 - var10;
      float var13 = var1 - var3 - var11;
      float var14 = var12 > 0.0F ? (var13 > 0.0F ? var8 : var7) : (var13 > 0.0F ? var9 : var6);
      float var15 = Math.max(0.0F, Math.min(var14, Math.min(var10, var11)));
      float var16 = Math.abs(var12) - var10 + var15;
      float var17 = Math.abs(var13) - var11 + var15;
      float var18 = Math.max(var16, 0.0F);
      float var19 = Math.max(var17, 0.0F);
      return Math.min(Math.max(var16, var17), 0.0F) + (float)Math.hypot(var18, var19) - var15;
   }

   public static boolean process(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      return handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) <= 0.0F;
   }

   public static float check(float var0, float var1, float var2) {
      float var3 = var0 - Math.max(0.0F, var1);
      return var3 <= 0.0F ? 0.0F : tick(drawAnimation(var3 / Math.max(1.0E-5F, var2)));
   }

   public static float check(float var0) {
      return onTick(0.0F, 0.22F, var0);
   }

   public static float onTick(float var0) {
      return onTick(0.42F, 0.82F, var0);
   }

   private static float select(float var0) {
      return tick(onTick(0.0F, 0.54F, var0));
   }

   private static float refresh(float var0) {
      return tick(onTick(0.18F, 1.0F, var0));
   }

   public static float process(float var0, float var1, float var2, float var3, float var4) {
      float var5 = Math.max(0.0F, var3);
      float var6 = Math.max(0.0F, var1 - var2 - var5 * 2.0F);
      return var0 + var5 + var6 * drawAnimation(var4);
   }

   public static AnticheatProfiles.Mode handle(String var0) {
      if (handle(var0, "matrix")) {
         return AnticheatProfiles.Mode.MATRIX;
      } else if (handle(var0, "grim")) {
         return AnticheatProfiles.Mode.GRIM;
      } else if (handle(var0, "watchdog")) {
         return AnticheatProfiles.Mode.WATCHDOG;
      } else if (handle(var0, "vulcan")) {
         return AnticheatProfiles.Mode.VULCAN;
      } else if (handle(var0, "intave")) {
         return AnticheatProfiles.Mode.INTAVE;
      } else if (handle(var0, "spartan")) {
         return AnticheatProfiles.Mode.SPARTAN;
      } else if (handle(var0, "verus")) {
         return AnticheatProfiles.Mode.VERUS;
      } else if (process(var0, "ncp") || handle(var0, "nocheatplus")) {
         return AnticheatProfiles.Mode.NCP;
      } else {
         return compute(var0, "aac") ? AnticheatProfiles.Mode.AAC : AnticheatProfiles.Mode.NONE;
      }
   }

   private static boolean handle(String var0, String var1) {
      if (var0 != null && var1 != null && var1.length() <= var0.length()) {
         for (int var2 = 0; var2 <= var0.length() - var1.length(); var2++) {
            if (var0.regionMatches(true, var2, var1, 0, var1.length())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean process(String var0, String var1) {
      return compute(var0, var1) && process(var0) == var1.length();
   }

   private static boolean compute(String var0, String var1) {
      if (var0 != null && var1 != null) {
         int var2 = 0;

         for (int var3 = 0; var3 < var0.length() && var2 < var1.length(); var3++) {
            char var4 = var0.charAt(var3);
            if (var4 != '-' && var4 != '_' && !Character.isWhitespace(var4) && Character.toLowerCase(var4) != Character.toLowerCase(var1.charAt(var2++))) {
               return false;
            }
         }

         return var2 == var1.length();
      } else {
         return false;
      }
   }

   private static int process(String var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         if (var3 != '-' && var3 != '_' && !Character.isWhitespace(var3)) {
            var1++;
         }
      }

      return var1;
   }

   private static float onTick(float var0, float var1, float var2) {
      return render(drawAnimation((var2 - var0) / Math.max(1.0E-5F, var1 - var0)));
   }

   private static float render(float var0) {
      return var0 * var0 * (3.0F - 2.0F * var0);
   }

   private static float tick(float var0) {
      double var1 = drawAnimation(var0);
      return (float)(var1 * var1 * var1 * (var1 * (var1 * 6.0 - 15.0) + 10.0));
   }

   private static float select(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static float drawAnimation(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   public enum Mode {
      NONE("", "", 1.0F, 0.0F, 0.0F),
      MATRIX("W", "wild:svg/anticheat/matrix.svg", 0.92F, 0.0F, 0.0F),
      GRIM("Q", "wild:svg/anticheat/grim.svg", 1.04F, 0.0F, -0.18F),
      NCP("N", "", 1.0F, 0.0F, 0.0F),
      VULCAN("V", "", 1.0F, 0.0F, 0.0F),
      INTAVE("I", "", 1.0F, 0.0F, 0.0F),
      AAC("A", "", 1.0F, 0.0F, 0.0F),
      SPARTAN("S", "", 1.0F, 0.0F, 0.0F),
      VERUS("V", "", 1.0F, 0.0F, 0.0F),
      WATCHDOG("W", "", 1.0F, 0.0F, 0.0F);

      private final String instance;
      private final String data;
      private final float context;
      private final float config;
      private final float state;

      Mode(String var3, String var4, float var5, float var6, float var7) {
         this.instance = var3;
         this.data = var4;
         this.context = var5;
         this.config = var6;
         this.state = var7;
      }

      public String handle() {
         return this.instance;
      }

      public String process() {
         return this.data;
      }

      public float compute() {
         return this.context;
      }

      public float resolve() {
         return this.config;
      }

      public float update() {
         return this.state;
      }
   }

   public enum PrimaryMode {
      SLIDING_KNOB;
   }
}
