package ru.wild.gui.theme;

import ru.wild.modules.visuals.Menu;

public enum ThemeAnimationStyle {
   STATIC(0L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         return var1;
      }
   },
   MULTI_GRADIENT(11000L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         float var5 = handle(var3, this.instance);
         int var6 = ThemeColors.handle(handle(var2, var5), 0.18F);
         int var7 = handle(var2, var5 + 0.34F);
         return ThemeAnimationStyle.handle(var1, var6, var7);
      }
   },
   TWIN_LAYERS(13000L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         float var5 = handle(var3, this.instance);
         float var6 = 1.0F - var5;
         int var7 = ThemeColors.handle(handle(var2, var5), 0.16F);
         int var8 = handle(var2, var6 + 0.5F);
         return ThemeAnimationStyle.handle(var1, var7, var8);
      }
   },
   HUE_WHEEL(8500L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         float var5 = handle(var3, this.instance);
         int var6 = ThemeColors.handle(handle(var2, var5), 0.14F);
         int var7 = handle(var2, var5 + 0.3F);
         return ThemeAnimationStyle.handle(var1, var6, var7);
      }
   },
   BREATHING(7000L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         float var5 = handle(var3, this.instance);
         float var6 = 0.5F + 0.5F * (float)Math.sin(var5 * Math.PI * 2.0);
         int var7 = handle(var2, 0.05F);
         int var8 = handle(var2, 0.35F);
         int var9 = ThemeColors.handle(var7, 0.04F + 0.1F * var6);
         int var10 = ThemeColors.handle(var8, 0.02F * (1.0F - var6));
         return ThemeAnimationStyle.handle(var1, var9, var10);
      }
   },
   PRISMATIC_WAVE(8500L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         float var5 = handle(var3, this.instance);
         float var6 = 0.5F + 0.5F * (float)Math.sin(var3 * 0.0017);
         int var7 = ThemeColors.handle(handle(var2, var5 + var6 * 0.04F), 0.16F);
         int var8 = handle(var2, var5 + 0.27F + (1.0F - var6) * 0.04F);
         return ThemeAnimationStyle.handle(var1, var7, var8);
      }
   },
   RAINBOW_LINEAR(9000L) {
      @Override
      public ThemeColors handle(ThemeColors var1, int[] var2, long var3) {
         float var5 = handle(var3, this.instance);
         int var6 = handle(var2, var5);
         int var7 = ThemeColors.handle(handle(var2, var5 + 0.38F), 0.18F);
         return ThemeAnimationStyle.handle(var1, var7, var6);
      }
   };

   public final long instance;

   ThemeAnimationStyle(long var3) {
      this.instance = var3;
   }

   public abstract ThemeColors handle(ThemeColors var1, int[] var2, long var3);

   public static ThemeAnimationStyle handle(ThemePalette var0) {
      if (var0 == null) {
         return STATIC;
      }

      ThemeAnimationStyle var1 = ThemeAnimationRegistry.process(var0);
      return var1 == null ? STATIC : var1;
   }

   public static ThemeColors handle(ThemePalette var0, ThemeColors var1, long var2) {
      if (var0 == null || var1 == null) {
         return var1;
      }

      if (!Menu.handle(Menu.moduleCollect)) {
         return var1;
      }

      int[] var4 = ThemeAnimationRegistry.handle(var0);
      ThemeAnimationStyle var5 = handle(var0);
      return var5 != STATIC && var4 != null && var4.length >= 2 ? var5.handle(var1, var4, var2) : var1;
   }

   static float handle(long var0, long var2) {
      if (var2 <= 0L) {
         return 0.0F;
      }

      long var4 = var0 % var2;
      if (var4 < 0L) {
         var4 += var2;
      }

      return (float)var4 / (float)var2;
   }

   static int handle(int[] var0, float var1) {
      if (var0 != null && var0.length != 0) {
         if (var0.length == 1) {
            return var0[0];
         }

         float var2 = var1 - (float)Math.floor(var1);
         float var3 = var2 * (var0.length - 1);
         int var4 = Math.min(var0.length - 2, Math.max(0, (int)Math.floor(var3)));
         return ThemeColors.handle(var0[var4], var0[var4 + 1], var3 - var4);
      } else {
         return -1;
      }
   }

   static int handle(float var0, float var1, float var2) {
      var0 = (var0 % 360.0F + 360.0F) % 360.0F;
      float var3 = (1.0F - Math.abs(2.0F * var2 - 1.0F)) * var1;
      float var4 = var3 * (1.0F - Math.abs(var0 / 60.0F % 2.0F - 1.0F));
      float var5 = var2 - var3 * 0.5F;
      float var6;
      float var7;
      float var8;
      if (var0 < 60.0F) {
         var6 = var3;
         var7 = var4;
         var8 = 0.0F;
      } else if (var0 < 120.0F) {
         var6 = var4;
         var7 = var3;
         var8 = 0.0F;
      } else if (var0 < 180.0F) {
         var6 = 0.0F;
         var7 = var3;
         var8 = var4;
      } else if (var0 < 240.0F) {
         var6 = 0.0F;
         var7 = var4;
         var8 = var3;
      } else if (var0 < 300.0F) {
         var6 = var4;
         var7 = 0.0F;
         var8 = var3;
      } else {
         var6 = var3;
         var7 = 0.0F;
         var8 = var4;
      }

      return ThemeColors.handle(Math.round((var6 + var5) * 255.0F), Math.round((var7 + var5) * 255.0F), Math.round((var8 + var5) * 255.0F), 255);
   }

   static ThemeColors handle(ThemeColors var0, int var1, int var2) {
      return ThemeColors.update()
         .handle(var0.apply())
         .process(var0.execute())
         .compute(var0.prepare())
         .resolve(var0.check())
         .update(var0.onTick())
         .apply(var0.select())
         .execute(var0.refresh())
         .prepare(var0.render())
         .check(var0.tick())
         .onTick(var0.drawAnimation())
         .select(var0.encodePoint())
         .refresh(var0.animate())
         .render(var0.load())
         .tick(var1)
         .drawAnimation(var2)
         .handle(var0.unload())
         .handle();
   }
}
