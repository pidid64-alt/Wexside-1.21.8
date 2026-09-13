package ru.wild.render.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import ru.wild.render.shader.ShaderRenderer;

public final class SdfTextRenderer {
   private static final Pattern instance = Pattern.compile("\n");
   private static final int data = 1710618;
   private static final int context = 6710886;
   private static volatile boolean config;
   private static volatile boolean state;
   private static final float[] cache = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F};
   private final ShaderRenderer output;
   private final TextureAtlasHelper current;
   private final String[] active = new String[1];

   public SdfTextRenderer(ShaderRenderer var1, TextureAtlasHelper var2) {
      this.output = Objects.requireNonNull(var1, "backend");
      this.current = Objects.requireNonNull(var2, "font");
   }

   public static boolean handle(boolean var0) {
      boolean var1 = config;
      config = var0;
      return var1;
   }

   public static boolean process(boolean var0) {
      boolean var1 = state;
      state = var0;
      return var1;
   }

   public void handle(float var1, float var2, float var3, String var4, int var5) {
      this.handle(var1, var2, var3, var4, var5, "l", cache);
   }

   public void handle(float var1, float var2, float var3, String var4, int var5, float[] var6) {
      this.handle(var1, var2, var3, var4, var5, "l", var6);
   }

   public void handle(float var1, float var2, float var3, String var4, int var5, String var6) {
      this.handle(var1, var2, var3, var4, var5, var6, cache);
   }

   public void handle(float var1, float var2, float var3, String var4, int var5, String var6, float[] var7) {
      if (!(var3 <= 0.0F)) {
         String var8 = var4 == null ? "" : var4;
         if (!var8.isEmpty()) {
            float[] var9 = var7 != null && var7.length >= 6 ? var7 : cache;
            float var10 = var3 / Math.max(1.0E-6F, this.current.apply());
            float var11 = this.current.execute() * var10;
            float var12 = var2;
            String var13 = var6 == null ? "l" : var6.toLowerCase();
            int var14 = process(var5);
            int var15 = this.current.handle();
            float var16 = this.current.update();
            String[] var17 = this.handle(var8);
            boolean var18 = state;

            for (String var22 : var17) {
               float var23 = this.update(var22, var10);
               float var24 = var1;
               if ("c".equals(var13)) {
                  var24 = var1 - var23 * 0.5F;
               } else if ("r".equals(var13)) {
                  var24 = var1 - var23;
               }

               float var25 = var12;
               if (var18) {
                  var24 = Math.round(var24);
                  var25 = Math.round(var25);
               }

               this.handle(var24, var25, var10, var22, var14, var9, var15, var16);
               var12 += var11;
            }
         }
      }
   }

   public void handle(float var1, float var2, float var3, String var4, int var5, int var6, float var7, String var8, float[] var9) {
      if (!(var3 <= 0.0F)) {
         String var10 = var4 == null ? "" : var4;
         if (!var10.isEmpty()) {
            float[] var11 = var9 != null && var9.length >= 6 ? var9 : cache;
            float var12 = var3 / Math.max(1.0E-6F, this.current.apply());
            float var13 = this.current.execute() * var12;
            float var14 = var2;
            String var15 = var8 == null ? "l" : var8.toLowerCase();
            int var16 = this.current.handle();
            float var17 = this.current.update();
            int var18 = process(var5);
            int var19 = process(var6);
            String[] var20 = this.handle(var10);
            boolean var21 = state;

            for (String var25 : var20) {
               float var26 = this.update(var25, var12);
               float var27 = var1;
               if ("c".equals(var15)) {
                  var27 = var1 - var26 * 0.5F;
               } else if ("r".equals(var15)) {
                  var27 = var1 - var26;
               }

               float var28 = var14;
               if (var21) {
                  var27 = Math.round(var27);
                  var28 = Math.round(var28);
               }

               this.handle(var27, var28, var12, var25, var18, var19, var7, Math.max(var26, 1.0E-6F), var11, var16, var17);
               var14 += var13;
            }
         }
      }
   }

   private String[] handle(String var1) {
      if (var1.indexOf(10) < 0) {
         this.active[0] = var1;
         return this.active;
      } else {
         return instance.split(var1, -1);
      }
   }

   public int handle() {
      return this.current.handle();
   }

   public float handle(String var1, float var2) {
      return this.update(var1 == null ? "" : var1, var2 / Math.max(1.0E-6F, this.current.apply()));
   }

   public float process(String var1, float var2) {
      return this.handle(var1, var2, true);
   }

   public float compute(String var1, float var2) {
      return this.handle(var1, var2, false);
   }

   private float handle(String var1, float var2, boolean var3) {
      String var4 = var1 == null ? "" : var1;
      if (!var4.isEmpty() && !(var2 <= 0.0F)) {
         float var5 = var2 / Math.max(1.0E-6F, this.current.apply());
         float var6 = this.current.select();
         float var7 = var3 ? -Float.MAX_VALUE : Float.MAX_VALUE;
         int var8 = 0;

         while (var8 < var4.length()) {
            int var9 = var4.codePointAt(var8);
            var8 += Character.charCount(var9);
            TextureAtlasHelper.TextureState var10 = this.current.handle(var9);
            if (var10 != null && var10.data) {
               float var11 = var3 ? var10.cache - var6 : var10.config + var6;
               var7 = var3 ? Math.max(var7, var11) : Math.min(var7, var11);
            }
         }

         return var7 != -Float.MAX_VALUE && var7 != Float.MAX_VALUE ? var7 * var5 : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public List<SdfTextRenderer.CacheEntry> handle(String var1, float var2, float var3, float var4) {
      ArrayList var5 = new ArrayList();
      String var6 = var1 == null ? "" : var1;
      if (!var6.isEmpty() && !(var2 <= 0.0F)) {
         float var7 = var2 / Math.max(1.0E-6F, this.current.apply());
         float var8 = Math.max(1.0F, this.current.compute());
         float var9 = var3;
         int var10 = -1;
         int var11 = 0;

         while (var11 < var6.length()) {
            int var12 = var6.codePointAt(var11);
            var11 += Character.charCount(var12);
            TextureAtlasHelper.TextureState var13 = this.current.handle(var12);
            int var14 = var12;
            if (var13 == null) {
               var13 = this.current.handle(63);
               var14 = 63;
               if (var13 == null) {
                  continue;
               }
            }

            if (var10 != -1) {
               var9 += this.current.handle(var10, var14) * var7;
            }

            if (var13.data) {
               float var15 = var9 + var13.context * var7;
               float var16 = var4 - var13.cache * var7;
               float var17 = var9 + var13.state * var7;
               float var18 = var4 - var13.config * var7;
               float var19 = Math.abs(var13.active - var13.output);
               float var20 = var19 > 1.0E-6F ? (var17 - var15) / (var19 * var8) : 1.0F;
               float var21 = this.current.update() * var20;
               if (var17 > var15 && var18 > var16) {
                  var5.add(new SdfTextRenderer.CacheEntry(var15, var16, var17, var18, var13.output, var13.mode, var13.active, var13.current, var21));
               }
            }

            var9 += var13.instance * var7;
            var10 = var14;
         }

         return var5;
      } else {
         return var5;
      }
   }

   private void handle(float var1, float var2, float var3, String var4, int var5, float[] var6, int var7, float var8) {
      if (!var4.isEmpty()) {
         float var9 = var1;
         float var10 = var2;
         int var11 = -1;
         int var12 = 0;

         while (var12 < var4.length()) {
            char var13 = var4.charAt(var12);
            if (var13 == '\\' && var12 + 9 < var4.length() && var4.charAt(var12 + 1) == 'c') {
               var12 += 10;
            } else {
               int var14 = var4.codePointAt(var12);
               int var15 = Character.charCount(var14);
               var12 += var15;
               TextureAtlasHelper.TextureState var16 = this.current.handle(var14);
               int var17 = var14;
               if (var16 == null) {
                  int var18 = handle(var14);
                  if (var18 != var14) {
                     var16 = this.current.handle(var18);
                     var17 = var18;
                  }
               }

               if (var16 == null) {
                  var16 = this.current.handle(63);
                  var17 = 63;
                  if (var16 == null) {
                     continue;
                  }
               }

               if (var11 != -1) {
                  var9 += this.current.handle(var11, var17) * var3;
               }

               if (var16.data) {
                  float var24 = var9 + var16.context * var3;
                  float var19 = var10 - var16.cache * var3;
                  float var20 = var9 + var16.state * var3;
                  float var21 = var10 - var16.config * var3;
                  float var22 = var20 - var24;
                  float var23 = var21 - var19;
                  if (var22 > 0.0F && var23 > 0.0F) {
                     this.output.resolve(var7, var8, var24, var19, var22, var23, var16.output, var16.mode, var16.active, var16.current, var5, var6);
                  }
               }

               var9 += var16.instance * var3;
               var11 = var17;
            }
         }
      }
   }

   private void handle(float var1, float var2, float var3, String var4, int var5, int var6, float var7, float var8, float[] var9, int var10, float var11) {
      if (!var4.isEmpty()) {
         float var12 = var1;
         float var13 = var2;
         int var14 = -1;
         int var15 = 0;

         while (var15 < var4.length()) {
            char var16 = var4.charAt(var15);
            if (var16 == '\\' && var15 + 9 < var4.length() && var4.charAt(var15 + 1) == 'c') {
               var15 += 10;
            } else {
               int var17 = var4.codePointAt(var15);
               int var18 = Character.charCount(var17);
               var15 += var18;
               TextureAtlasHelper.TextureState var19 = this.current.handle(var17);
               int var20 = var17;
               if (var19 == null) {
                  int var21 = handle(var17);
                  if (var21 != var17) {
                     var19 = this.current.handle(var21);
                     var20 = var21;
                  }
               }

               if (var19 == null) {
                  var19 = this.current.handle(63);
                  var20 = 63;
                  if (var19 == null) {
                     continue;
                  }
               }

               if (var14 != -1) {
                  var12 += this.current.handle(var14, var20) * var3;
               }

               float var30 = (var12 - var1 + var19.instance * var3 * 0.5F) / var8;
               float var22 = 0.5F + 0.5F * (float)Math.sin((var30 * 1.55F + var7) * Math.PI * 2.0);
               int var23 = handle(var5, var6, var22);
               if (var19.data) {
                  float var24 = var12 + var19.context * var3;
                  float var25 = var13 - var19.cache * var3;
                  float var26 = var12 + var19.state * var3;
                  float var27 = var13 - var19.config * var3;
                  float var28 = var26 - var24;
                  float var29 = var27 - var25;
                  if (var28 > 0.0F && var29 > 0.0F) {
                     this.output.resolve(var10, var11, var24, var25, var28, var29, var19.output, var19.mode, var19.active, var19.current, var23, var9);
                  }
               }

               var12 += var19.instance * var3;
               var14 = var20;
            }
         }
      }
   }

   public SdfTextRenderer.State resolve(String var1, float var2) {
      if (var2 <= 0.0F) {
         return new SdfTextRenderer.State(0.0F, 0.0F);
      }

      String var3 = var1 == null ? "" : var1;
      if (var3.isEmpty()) {
         return new SdfTextRenderer.State(0.0F, 0.0F);
      }

      float var4 = var2 / Math.max(1.0E-6F, this.current.apply());
      float var5 = this.current.execute() * var4;
      String[] var6 = this.handle(var3);
      float var7 = 0.0F;

      for (String var11 : var6) {
         var7 = Math.max(var7, this.update(var11, var4));
      }

      float var12 = Math.max(var5 * var6.length, var5);
      return new SdfTextRenderer.State(var7, var12);
   }

   private float update(String var1, float var2) {
      if (var1.isEmpty()) {
         return 0.0F;
      }

      float var3 = 0.0F;
      int var4 = -1;
      int var5 = 0;

      while (var5 < var1.length()) {
         char var6 = var1.charAt(var5);
         if (var6 == '\\' && var5 + 9 < var1.length() && var1.charAt(var5 + 1) == 'c') {
            var5 += 10;
         } else {
            int var7 = var1.codePointAt(var5);
            int var8 = Character.charCount(var7);
            var5 += var8;
            TextureAtlasHelper.TextureState var9 = this.current.handle(var7);
            int var10 = var7;
            if (var9 == null) {
               int var11 = handle(var7);
               if (var11 != var7) {
                  var9 = this.current.handle(var11);
                  var10 = var11;
               }
            }

            if (var9 == null) {
               var9 = this.current.handle(63);
               var10 = 63;
               if (var9 == null) {
                  continue;
               }
            }

            if (var4 != -1) {
               var3 += this.current.handle(var4, var10) * var2;
            }

            var3 += var9.instance * var2;
            var4 = var10;
         }
      }

      return var3;
   }

   private static int handle(int var0) {
      return var0 == 10028 ? 9733 : var0;
   }

   private static int process(int var0) {
      if (!config) {
         return var0;
      }

      int var1 = var0 >>> 24 & 0xFF;
      if (var1 == 0) {
         return var0;
      }

      int var2 = var0 >>> 16 & 0xFF;
      int var3 = var0 >>> 8 & 0xFF;
      int var4 = var0 & 0xFF;
      return var2 >= 210 && var3 >= 210 && var4 >= 210 ? var1 << 24 | (var1 < 180 ? 6710886 : 1710618) : var0;
   }

   private static int handle(int var0, int var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, var2));
      int var4 = process(var0 >>> 24 & 0xFF, var1 >>> 24 & 0xFF, var3);
      int var5 = process(var0 >>> 16 & 0xFF, var1 >>> 16 & 0xFF, var3);
      int var6 = process(var0 >>> 8 & 0xFF, var1 >>> 8 & 0xFF, var3);
      int var7 = process(var0 & 0xFF, var1 & 0xFF, var3);
      return var4 << 24 | var5 << 16 | var6 << 8 | var7;
   }

   private static int process(int var0, int var1, float var2) {
      return Math.round(var0 + (var1 - var0) * var2);
   }

   public static final class CacheEntry {
      public final float instance;
      public final float data;
      public final float context;
      public final float config;
      public final float state;
      public final float cache;
      public final float output;
      public final float current;
      public final float active;

      CacheEntry(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
      }
   }

   public static final class State {
      public final float instance;
      public final float data;

      public State(float var1, float var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
