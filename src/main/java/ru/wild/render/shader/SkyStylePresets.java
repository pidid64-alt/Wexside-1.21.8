package ru.wild.render.shader;

public enum SkyStylePresets {
   CINEMATIC(
      "Cinematic",
      0.03F,
      0.08F,
      0.06F,
      0.1F,
      -0.02F,
      0.05F,
      -0.02F,
      new float[]{-0.02F, -0.01F, 0.02F},
      new float[]{0.01F, 0.0F, -0.01F},
      new float[]{0.03F, 0.01F, -0.02F},
      0.55F,
      0.86F,
      64.0F,
      0.22F,
      0.08F
   ),
   VIBRANT(
      "Vibrant",
      0.04F,
      0.1F,
      0.16F,
      0.18F,
      -0.02F,
      0.02F,
      0.0F,
      new float[]{0.0F, 0.0F, 0.0F},
      new float[]{0.0F, 0.0F, 0.0F},
      new float[]{0.02F, 0.02F, 0.02F},
      0.45F,
      0.84F,
      60.0F,
      0.28F,
      0.04F
   ),
   NATURAL(
      "Natural",
      0.01F,
      0.04F,
      0.03F,
      0.06F,
      0.0F,
      0.02F,
      0.0F,
      new float[]{0.0F, 0.0F, 0.0F},
      new float[]{0.0F, 0.0F, 0.0F},
      new float[]{0.0F, 0.0F, 0.0F},
      0.3F,
      0.92F,
      48.0F,
      0.16F,
      0.02F
   ),
   SUNNY_FIELD(
      "Sunny Field",
      0.05F,
      0.06F,
      0.1F,
      0.18F,
      -0.03F,
      0.16F,
      -0.04F,
      new float[]{0.02F, 0.01F, -0.03F},
      new float[]{0.02F, 0.01F, -0.02F},
      new float[]{0.04F, 0.02F, -0.03F},
      0.65F,
      0.8F,
      70.0F,
      0.2F,
      0.06F
   ),
   URBAN_RAIN(
      "Urban Rain",
      -0.03F,
      0.05F,
      -0.06F,
      0.04F,
      0.01F,
      -0.18F,
      0.04F,
      new float[]{-0.02F, -0.01F, 0.04F},
      new float[]{0.0F, 0.01F, 0.02F},
      new float[]{-0.02F, -0.01F, 0.04F},
      0.4F,
      0.92F,
      56.0F,
      0.22F,
      0.1F
   ),
   SUNSET(
      "Sunset",
      0.03F,
      0.1F,
      0.1F,
      0.16F,
      -0.02F,
      0.2F,
      -0.06F,
      new float[]{0.04F, 0.01F, -0.04F},
      new float[]{0.02F, 0.0F, -0.02F},
      new float[]{0.05F, 0.02F, -0.03F},
      0.75F,
      0.78F,
      80.0F,
      0.2F,
      0.06F
   ),
   FROST(
      "Frost",
      0.01F,
      0.05F,
      -0.04F,
      0.04F,
      0.01F,
      -0.2F,
      -0.02F,
      new float[]{-0.02F, -0.01F, 0.04F},
      new float[]{-0.01F, 0.01F, 0.02F},
      new float[]{-0.02F, 0.01F, 0.04F},
      0.5F,
      0.86F,
      64.0F,
      0.24F,
      0.08F
   ),
   TEAL_ORANGE(
      "Teal-Orange",
      0.03F,
      0.12F,
      0.1F,
      0.12F,
      -0.02F,
      0.04F,
      0.0F,
      new float[]{-0.04F, -0.01F, 0.04F},
      new float[]{0.01F, 0.0F, -0.01F},
      new float[]{0.06F, 0.02F, -0.04F},
      0.6F,
      0.82F,
      70.0F,
      0.24F,
      0.08F
   ),
   VINTAGE(
      "Vintage",
      -0.02F,
      -0.02F,
      -0.08F,
      -0.04F,
      0.04F,
      0.1F,
      0.02F,
      new float[]{0.02F, 0.01F, -0.02F},
      new float[]{0.02F, 0.01F, -0.02F},
      new float[]{0.02F, 0.0F, -0.04F},
      0.3F,
      0.92F,
      50.0F,
      0.1F,
      0.16F
   ),
   NEON_NIGHT(
      "Neon Night",
      -0.02F,
      0.1F,
      0.16F,
      0.2F,
      0.02F,
      -0.1F,
      0.08F,
      new float[]{-0.02F, -0.02F, 0.05F},
      new float[]{0.0F, -0.01F, 0.02F},
      new float[]{0.02F, -0.02F, 0.06F},
      0.95F,
      0.74F,
      90.0F,
      0.22F,
      0.1F
   );

   public final String instance;
   public final float data;
   public final float context;
   public final float config;
   public final float state;
   public final float cache;
   public final float output;
   public final float current;
   public final float[] active;
   public final float[] mode;
   public final float[] selection;
   public final float enabled;
   public final float renderer;
   public final float handler;
   public final float animationDraw;
   public final float pointEncode;

   SkyStylePresets(
      String var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float[] var11,
      float[] var12,
      float[] var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      this.instance = var3;
      this.data = var4;
      this.context = var5;
      this.config = var6;
      this.state = var7;
      this.cache = var8;
      this.output = var9;
      this.current = var10;
      this.active = var11;
      this.mode = var12;
      this.selection = var13;
      this.enabled = var14;
      this.renderer = var15;
      this.handler = var16;
      this.animationDraw = var17;
      this.pointEncode = var18;
   }

   public static SkyStylePresets handle(String var0) {
      if (var0 == null) {
         return CINEMATIC;
      }

      for (SkyStylePresets var4 : values()) {
         if (var4.instance.equalsIgnoreCase(var0)) {
            return var4;
         }
      }

      return CINEMATIC;
   }

   public static String[] handle() {
      SkyStylePresets[] var0 = values();
      String[] var1 = new String[var0.length + 1];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var2].instance;
      }

      var1[var0.length] = "Custom";
      return var1;
   }
}
