package ru.wild.util.math;

import ru.wild.modules.visuals.Menu;

public enum EasingStylePresets {
   SMOOTH("Smooth", 1.0F, 1.0F, 1.0F),
   SNAPPY("Snappy", 1.55F, 1.06F, 1.1F),
   BOUNCY("Bouncy", 0.82F, 0.62F, 0.85F),
   CINEMATIC("Cinematic", 0.55F, 1.0F, 0.92F),
   LINEAR("Linear", 2.1F, 1.16F, 1.5F);

   public static final EasingStylePresets DEFAULT = SMOOTH;
   private static final float state = 0.001F;
   private static final float cache = 0.05F;
   private static final float output = 0.985F;
   public final String instance;
   public final float data;
   public final float context;
   public final float config;

   EasingStylePresets(String var3, float var4, float var5, float var6) {
      this.instance = var3;
      this.data = var4;
      this.context = var5;
      this.config = var6;
   }

   public float handle(float var1) {
      return Math.max(0.001F, var1 * this.data);
   }

   public float process(float var1) {
      float var2 = var1 * this.context;
      if (var2 < 0.05F) {
         return 0.05F;
      } else {
         return var2 > 0.985F ? 0.985F : var2;
      }
   }

   public float compute(float var1) {
      return var1 * this.config;
   }

   public static EasingStylePresets handle() {
      try {
         return Menu.itemProject == null ? DEFAULT : handle(Menu.itemProject.compute());
      } catch (Throwable var1) {
         return DEFAULT;
      }
   }

   public static EasingStylePresets handle(String var0) {
      if (var0 == null) {
         return DEFAULT;
      }

      for (EasingStylePresets var4 : values()) {
         if (var4.instance.equalsIgnoreCase(var0)) {
            return var4;
         }
      }

      return DEFAULT;
   }

   public static String[] process() {
      EasingStylePresets[] var0 = values();
      String[] var1 = new String[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var2].instance;
      }

      return var1;
   }
}
