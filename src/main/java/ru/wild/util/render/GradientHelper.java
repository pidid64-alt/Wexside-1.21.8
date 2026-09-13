package ru.wild.util.render;

public final class GradientHelper {
   public static final int instance = 256;
   public static final int data = 10;
   public static final int context = 24;
   public static final int config = 52992;
   public static final int state = 6;
   public static final float cache = 0.0625F;
   public static final float output = (float)Math.sqrt(0.17626953F) + 0.14F;
   public static final float current = 0.286F;
   public static final float active = -0.515625F;
   public static final float mode = -0.801625F;
   public static final float selection = 0.105F;
   public static final float enabled = output * 0.105F;
   public static final float renderer = -0.771595F;
   public static final float handler = output * 1.008F;
   public static final float animationDraw = output * 1.025F;
   public static final float pointEncode = output * 0.025F;
   public static final float animator = output * 0.022F;
   public static final float source = -0.515625F;
   public static final float target = 0.985F;
   public static final float pending = 0.985F;
   public static final float previous = 0.88F;
   public static final float latest = 0.82F;
   public static final float summary = 0.7F;
   private static final float matrixBlend = handler - enabled;
   private static final float vectorMatch = 0.25597F;
   private static final float itemProject = output * 0.25597F / (0.286F * matrixBlend);
   private static final float responseCompute = (float) (Math.PI * 2);
   private static final float[] providerFetch = new float[257];
   private static final float[] profileDraw = new float[257];
   private static final float[] vectorPerform = new float[25];
   private static final float[] eventAttach = new float[25];

   private GradientHelper() {
   }

   public static void handle(GradientHelper.Callback var0) {
      resolve(var0);
      compute(var0);
      update(var0);
   }

   public static void process(GradientHelper.Callback var0) {
      handle(
         var0,
         0.0F,
         -0.515625F,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         -0.515625F,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         0.0F,
         1.0F,
         1.0F,
         0.0F,
         -0.515625F,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         1.0F,
         1.0F,
         0.0F,
         -0.515625F,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F
      );
   }

   public static float handle(float var0, float var1) {
      float var2 = Math.max(0.0F, var0);
      float var3 = (float)Math.sqrt((var2 - animationDraw) * (var2 - animationDraw) + var1 * var1);
      float var4 = handle((var3 - 0.1F) / 0.18F);
      float var5 = Math.max(handle((var2 - animationDraw * 0.72F) / (animationDraw * 0.23F)), handle((Math.abs(var1) - 0.12F) / 0.2F));
      return var4 * var5;
   }

   private static void compute(GradientHelper.Callback var0) {
      for (int var1 = 0; var1 < 256; var1++) {
         int var2 = var1 + 1;
         float var3 = var1 / 256.0F;
         float var4 = var2 / 256.0F;
         float var5 = profileDraw[var1];
         float var6 = providerFetch[var1];
         float var7 = profileDraw[var2];
         float var8 = providerFetch[var2];
         handle(
            var0,
            var5 * enabled,
            -0.771595F,
            var6 * enabled,
            var5 * 0.25597F,
            -matrixBlend,
            var6 * 0.25597F,
            var3,
            0.105F,
            0.7216F,
            var5 * handler,
            -0.515625F,
            var6 * handler,
            var5 * 0.25597F,
            -matrixBlend,
            var6 * 0.25597F,
            var3,
            0.985F,
            0.88F,
            var7 * handler,
            -0.515625F,
            var8 * handler,
            var7 * 0.25597F,
            -matrixBlend,
            var8 * 0.25597F,
            var4,
            0.985F,
            0.88F,
            var7 * enabled,
            -0.771595F,
            var8 * enabled,
            var7 * 0.25597F,
            -matrixBlend,
            var8 * 0.25597F,
            var4,
            0.105F,
            0.7216F
         );
      }
   }

   private static void resolve(GradientHelper.Callback var0) {
      for (int var1 = 0; var1 < 10; var1++) {
         float var2 = var1 / 10.0F;
         float var3 = (var1 + 1) / 10.0F;
         float var4 = enabled * var2;
         float var5 = enabled * var3;
         float var6 = -0.801625F + 0.030030001F * ((itemProject - 2.0F) * var2 * var2 * var2 + (3.0F - itemProject) * var2 * var2);
         float var7 = -0.801625F + 0.030030001F * ((itemProject - 2.0F) * var3 * var3 * var3 + (3.0F - itemProject) * var3 * var3);
         float var8 = 0.286F * (3.0F * (itemProject - 2.0F) * var2 * var2 + 2.0F * (3.0F - itemProject) * var2);
         float var9 = 0.286F * (3.0F * (itemProject - 2.0F) * var3 * var3 + 2.0F * (3.0F - itemProject) * var3);
         float var10 = 0.105F * var2;
         float var11 = 0.105F * var3;
         float var12 = 0.7F + 0.021600008F * var2;
         float var13 = 0.7F + 0.021600008F * var3;

         for (int var14 = 0; var14 < 256; var14++) {
            int var15 = var14 + 1;
            float var16 = var14 / 256.0F;
            float var17 = var15 / 256.0F;
            float var18 = profileDraw[var14];
            float var19 = providerFetch[var14];
            float var20 = profileDraw[var15];
            float var21 = providerFetch[var15];
            if (var1 == 0) {
               float var22 = (var14 + 0.5F) / 256.0F;
               handle(
                  var0,
                  0.0F,
                  -0.801625F,
                  0.0F,
                  0.0F,
                  -1.0F,
                  0.0F,
                  var22,
                  2.0F + var10,
                  var12,
                  var18 * var5,
                  var7,
                  var19 * var5,
                  var18 * var9,
                  -output,
                  var19 * var9,
                  var16,
                  2.0F + var11,
                  var13,
                  var20 * var5,
                  var7,
                  var21 * var5,
                  var20 * var9,
                  -output,
                  var21 * var9,
                  var17,
                  2.0F + var11,
                  var13
               );
            } else {
               handle(
                  var0,
                  var18 * var4,
                  var6,
                  var19 * var4,
                  var18 * var8,
                  -output,
                  var19 * var8,
                  var16,
                  2.0F + var10,
                  var12,
                  var18 * var5,
                  var7,
                  var19 * var5,
                  var18 * var9,
                  -output,
                  var19 * var9,
                  var16,
                  2.0F + var11,
                  var13,
                  var20 * var5,
                  var7,
                  var21 * var5,
                  var20 * var9,
                  -output,
                  var21 * var9,
                  var17,
                  2.0F + var11,
                  var13,
                  var20 * var4,
                  var6,
                  var21 * var4,
                  var20 * var8,
                  -output,
                  var21 * var8,
                  var17,
                  2.0F + var10,
                  var12
               );
            }
         }
      }
   }

   private static void update(GradientHelper.Callback var0) {
      for (int var1 = 0; var1 < 256; var1++) {
         int var2 = var1 + 1;
         float var3 = var1 / 256.0F;
         float var4 = var2 / 256.0F;
         float var5 = profileDraw[var1];
         float var6 = providerFetch[var1];
         float var7 = profileDraw[var2];
         float var8 = providerFetch[var2];

         for (int var9 = 0; var9 < 24; var9++) {
            int var10 = var9 + 1;
            float var11 = animationDraw + pointEncode * eventAttach[var9];
            float var12 = animationDraw + pointEncode * eventAttach[var10];
            float var13 = -0.515625F + animator * vectorPerform[var9];
            float var14 = -0.515625F + animator * vectorPerform[var10];
            float var15 = eventAttach[var9] / pointEncode;
            float var16 = vectorPerform[var9] / animator;
            float var17 = eventAttach[var10] / pointEncode;
            float var18 = vectorPerform[var10] / animator;
            float var19 = 0.985F * var9 / 24.0F;
            float var20 = 0.985F * var10 / 24.0F;
            handle(
               var0,
               var5 * var11,
               var13,
               var6 * var11,
               var5 * var15,
               var16,
               var6 * var15,
               var3,
               1.0F + var19,
               1.0F,
               var5 * var12,
               var14,
               var6 * var12,
               var5 * var17,
               var18,
               var6 * var17,
               var3,
               1.0F + var20,
               1.0F,
               var7 * var12,
               var14,
               var8 * var12,
               var7 * var17,
               var18,
               var8 * var17,
               var4,
               1.0F + var20,
               1.0F,
               var7 * var11,
               var13,
               var8 * var11,
               var7 * var15,
               var16,
               var8 * var15,
               var4,
               1.0F + var19,
               1.0F
            );
         }
      }
   }

   private static void handle(
      GradientHelper.Callback var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20,
      float var21,
      float var22,
      float var23,
      float var24,
      float var25,
      float var26,
      float var27,
      float var28,
      float var29,
      float var30,
      float var31,
      float var32,
      float var33,
      float var34,
      float var35,
      float var36
   ) {
      handle(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var21,
         var22,
         var23,
         var24,
         var25,
         var26,
         var27
      );
      handle(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var19,
         var20,
         var21,
         var22,
         var23,
         var24,
         var25,
         var26,
         var27,
         var28,
         var29,
         var30,
         var31,
         var32,
         var33,
         var34,
         var35,
         var36
      );
   }

   private static void handle(
      GradientHelper.Callback var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20,
      float var21,
      float var22,
      float var23,
      float var24,
      float var25,
      float var26,
      float var27
   ) {
      var0.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      var0.handle(var10, var11, var12, var13, var14, var15, var16, var17, var18);
      var0.handle(var19, var20, var21, var22, var23, var24, var25, var26, var27);
   }

   private static float handle(float var0) {
      float var1 = Math.max(0.0F, Math.min(1.0F, var0));
      return var1 * var1 * var1 * (var1 * (var1 * 6.0F - 15.0F) + 10.0F);
   }

   static {
      for (int var0 = 0; var0 <= 256; var0++) {
         float var1 = (float) (Math.PI * 2) * var0 / 256.0F;
         providerFetch[var0] = (float)Math.sin(var1);
         profileDraw[var0] = (float)Math.cos(var1);
      }

      providerFetch[256] = 0.0F;
      profileDraw[256] = 1.0F;

      for (int var2 = 0; var2 <= 24; var2++) {
         float var3 = (float) (Math.PI * 2) * var2 / 24.0F;
         vectorPerform[var2] = (float)Math.sin(var3);
         eventAttach[var2] = (float)Math.cos(var3);
      }

      vectorPerform[24] = 0.0F;
      eventAttach[24] = 1.0F;
   }

   public interface Callback {
      void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9);
   }
}
