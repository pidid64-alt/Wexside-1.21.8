package ru.wild.gui.theme;
import ru.wild.modules.visuals.Menu;
import ru.wild.util.render.PackedColor;

public final class ThemeColors {
   private static final ThemeColors[][] instance = new ThemeColors[ThemePalette.values().length][2];
   private final int data;
   private final int context;
   private final int config;
   private final int state;
   private final int cache;
   private final int output;
   private final int current;
   private final int active;
   private final int mode;
   private final int selection;
   private final int enabled;
   private final int renderer;
   private final int handler;
   private final int animationDraw;
   private final int pointEncode;
   private final boolean animator;

   public int handle() {
      return this.animator ? -14705331 : -12452048;
   }

   public int process() {
      return this.animator ? -4181953 : -43920;
   }

   public int compute() {
      return this.animator ? -6200825 : -17847;
   }

   public int resolve() {
      int var1 = compute(this.data, -16777216);
      int var2 = compute(this.data, -1);
      return handle(this.animationDraw, var1, var2, compute(this.context, var1), compute(this.context, var2), 4.5F);
   }

   public static ThemeColors handle(ThemePalette var0) {
      return handle(var0, ThemePaletteRegistry.handle().compute(var0));
   }

   public static ThemeColors handle(ThemePalette var0, boolean var1) {
      if (var0 == null) {
         var0 = ThemePalette.WILD;
      }

      if (var0 == ThemePalette.CUSTOM) {
         Menu.encodePoint();
         return process(var0, var1);
      }

      int var2 = var1 ? 1 : 0;
      ThemeColors var3 = instance[var0.ordinal()][var2];
      if (var3 != null) {
         return var3;
      }

      ThemeColors var4 = process(var0, var1);
      instance[var0.ordinal()][var2] = var4;
      return var4;
   }

   private static ThemeColors process(ThemePalette var0, boolean var1) {
      ThemePaletteRegistry var2 = ThemePaletteRegistry.handle();
      int var3 = var2.resolve(var0);
      int var4 = var2.update(var0);
      int var5 = var1 ? var0.update().getRGB() : var0.resolve().getRGB();
      int var6 = var0.update().getRGB();
      int var7 = var0.apply().getRGB();
      if (var0 == ThemePalette.MIDNIGHT_AZURE) {
         return handle(
            update()
               .handle(handle(5, 10, 22, 238))
               .process(handle(8, 19, 34, 242))
               .compute(handle(189, 234, 255, 8))
               .resolve(handle(189, 234, 255, 13))
               .update(handle(189, 234, 255, 20))
               .apply(handle(189, 234, 255, 28))
               .execute(handle(189, 234, 255, 38))
               .prepare(handle(189, 234, 255, 50))
               .check(handle(0, 240, 255, 72))
               .onTick(handle(0, 240, 255, 90))
               .select(handle(189, 234, 255, 140))
               .refresh(handle(232, 251, 255, 214))
               .render(handle(255, 255, 255, 255))
               .tick(-16715521)
               .drawAnimation(-16759553)
               .handle(false)
               .handle()
         );
      }

      ThemeColors.ColorState var8 = update()
         .handle(handle(handle(var0.process().getRGB(), var4, var1 ? 0.04F : 0.05F), var1 ? 226 : 232))
         .process(handle(handle(var0.compute().getRGB(), var4, var1 ? 0.025F : 0.035F), var1 ? 240 : 238))
         .render(handle(var6, 255))
         .tick(var3)
         .drawAnimation(var4)
         .handle(var1);
      return var1
         ? handle(
            var8.compute(handle(var5, 4))
               .resolve(handle(var5, 8))
               .update(handle(var5, 12))
               .apply(handle(var5, 16))
               .execute(handle(var5, 24))
               .prepare(handle(var5, 31))
               .check(handle(var5, 42))
               .onTick(handle(var5, 54))
               .select(handle(var7, 190))
               .refresh(handle(var7, 255))
               .handle()
         )
         : handle(
            var8.compute(handle(var5, 3))
               .resolve(handle(var5, 5))
               .update(handle(var5, 8))
               .apply(handle(var5, 10))
               .execute(handle(var5, 15))
               .prepare(handle(var5, 20))
               .check(handle(var5, 31))
               .onTick(handle(var5, 41))
               .select(handle(var7, 61))
               .refresh(handle(var7, 122))
               .handle()
         );
   }

   public static ThemeColors handle(ThemePalette var0, ThemeColors var1, long var2) {
      if (var1 == null) {
         var1 = handle(var0);
      }

      return ThemeAnimationStyle.handle(var0, var1, var2);
   }

   public static int handle(int var0, int var1, int var2, int var3) {
      return (var3 & 0xFF) << 24 | (var0 & 0xFF) << 16 | (var1 & 0xFF) << 8 | var2 & 0xFF;
   }

   public static int handle(int var0, int var1) {
      return handle(var0 >> 16 & 0xFF, var0 >> 8 & 0xFF, var0 & 0xFF, Math.max(0, Math.min(255, var1)));
   }

   public static int handle(int var0, int var1, float var2) {
      return PackedColor.compute(var0, var1, var2);
   }

   public static int process(int var0, int var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, var2));
      int var4 = var0 >>> 16 & 0xFF;
      int var5 = var0 >>> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      int var7 = var0 >>> 24 & 0xFF;
      int var8 = var1 >>> 16 & 0xFF;
      int var9 = var1 >>> 8 & 0xFF;
      int var10 = var1 & 0xFF;
      int var11 = 255 - (255 - var4) * (255 - var8) / 255;
      int var12 = 255 - (255 - var5) * (255 - var9) / 255;
      int var13 = 255 - (255 - var6) * (255 - var10) / 255;
      return PackedColor.compute(var0, handle(var11, var12, var13, var7), var3);
   }

   public static ThemeColors handle(ThemeColors var0) {
      if (var0 == null) {
         return null;
      }

      int var1 = compute(var0.data, -16777216);
      int var2 = compute(var0.data, -1);
      int var3 = compute(var0.context, var1);
      int var4 = compute(var0.context, var2);
      return update()
         .handle(var0.data)
         .process(var0.context)
         .compute(var0.config)
         .resolve(var0.state)
         .update(var0.cache)
         .apply(var0.output)
         .execute(var0.current)
         .prepare(var0.active)
         .check(var0.mode)
         .onTick(var0.selection)
         .select(handle(var0.enabled, var1, var2, var3, var4, 3.0F))
         .refresh(handle(var0.renderer, var1, var2, var3, var4, 4.8F))
         .render(handle(var0.handler, var1, var2, var3, var4, 7.0F))
         .tick(var0.animationDraw)
         .drawAnimation(var0.pointEncode)
         .handle(var0.animator)
         .handle();
   }

   private static int handle(int var0, int var1, int var2, int var3, int var4, float var5) {
      if (handle(var0, var1, var2, var3, var4) >= var5) {
         return var0;
      }

      int var6 = handle(var0, 255);
      if (handle(var6, var1, var2, var3, var4) >= var5) {
         int var15 = var0 >>> 24 & 0xFF;
         int var16 = 255;

         for (int var17 = 0; var17 < 10; var17++) {
            int var18 = var15 + var16 >>> 1;
            int var19 = handle(var0, var18);
            if (handle(var19, var1, var2, var3, var4) >= var5) {
               var16 = var18;
            } else {
               var15 = var18 + 1;
            }
         }

         return handle(var0, var16);
      } else {
         int var7 = var6;
         float var8 = (handle(var1) + handle(var2) + handle(var3) + handle(var4)) * 0.25F;
         int var9 = var8 > 0.48F ? -16777216 : -1;
         float var10 = 0.0F;
         float var11 = 1.0F;

         for (int var12 = 0; var12 < 14; var12++) {
            float var13 = (var10 + var11) * 0.5F;
            int var14 = handle(handle(var7, var9, var13), 255);
            if (handle(var14, var1, var2, var3, var4) >= var5) {
               var11 = var13;
            } else {
               var10 = var13;
            }
         }

         return handle(handle(var7, var9, var11), 255);
      }
   }

   private static float handle(int var0, int var1, int var2, int var3, int var4) {
      return Math.min(Math.min(process(var0, var1), process(var0, var2)), Math.min(process(var0, var3), process(var0, var4)));
   }

   private static float process(int var0, int var1) {
      float var2 = handle(compute(var0, var1));
      float var3 = handle(var1);
      return (Math.max(var2, var3) + 0.05F) / (Math.min(var2, var3) + 0.05F);
   }

   private static int compute(int var0, int var1) {
      float var2 = (var0 >>> 24 & 0xFF) / 255.0F;
      int var3 = Math.round((var0 >>> 16 & 0xFF) * var2 + (var1 >>> 16 & 0xFF) * (1.0F - var2));
      int var4 = Math.round((var0 >>> 8 & 0xFF) * var2 + (var1 >>> 8 & 0xFF) * (1.0F - var2));
      int var5 = Math.round((var0 & 0xFF) * var2 + (var1 & 0xFF) * (1.0F - var2));
      return handle(var3, var4, var5, 255);
   }

   private static float handle(int var0) {
      return 0.2126F * process(var0 >>> 16 & 0xFF) + 0.7152F * process(var0 >>> 8 & 0xFF) + 0.0722F * process(var0 & 0xFF);
   }

   private static float process(int var0) {
      float var1 = var0 / 255.0F;
      return var1 <= 0.04045F ? var1 / 12.92F : (float)Math.pow((var1 + 0.055F) / 1.055F, 2.4F);
   }

   static int handle(int[] var0, float var1) {
      if (var0 != null && var0.length != 0) {
         if (var0.length == 1) {
            return var0[0];
         }

         float var2 = var1 - (float)Math.floor(var1);
         float var3 = var2 * (var0.length - 1);
         int var4 = Math.min(var0.length - 2, Math.max(0, (int)Math.floor(var3)));
         return handle(var0[var4], var0[var4 + 1], var3 - var4);
      } else {
         return -1;
      }
   }

   public static ThemeColors handle(ThemeColors var0, ThemeColors var1, float var2) {
      if (var2 <= 0.0F) {
         return var0;
      } else {
         return var2 >= 1.0F
            ? var1
            : handle(
               update()
                  .handle(handle(var0.data, var1.data, var2))
                  .process(handle(var0.context, var1.context, var2))
                  .compute(handle(var0.config, var1.config, var2))
                  .resolve(handle(var0.state, var1.state, var2))
                  .update(handle(var0.cache, var1.cache, var2))
                  .apply(handle(var0.output, var1.output, var2))
                  .execute(handle(var0.current, var1.current, var2))
                  .prepare(handle(var0.active, var1.active, var2))
                  .check(handle(var0.mode, var1.mode, var2))
                  .onTick(handle(var0.selection, var1.selection, var2))
                  .select(handle(var0.enabled, var1.enabled, var2))
                  .refresh(handle(var0.renderer, var1.renderer, var2))
                  .render(handle(var0.handler, var1.handler, var2))
                  .tick(handle(var0.animationDraw, var1.animationDraw, var2))
                  .drawAnimation(handle(var0.pointEncode, var1.pointEncode, var2))
                  .handle(var2 >= 0.5F ? var1.animator : var0.animator)
                  .handle()
            );
      }
   }

   static int handle(int var0, float var1) {
      return PackedColor.compute(handle(var0, 255), -1, var1);
   }
   ThemeColors(
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12,
      int var13,
      int var14,
      int var15,
      boolean var16
   ) {
      this.data = var1;
      this.context = var2;
      this.config = var3;
      this.state = var4;
      this.cache = var5;
      this.output = var6;
      this.current = var7;
      this.active = var8;
      this.mode = var9;
      this.selection = var10;
      this.enabled = var11;
      this.renderer = var12;
      this.handler = var13;
      this.animationDraw = var14;
      this.pointEncode = var15;
      this.animator = var16;
   }
   public static ThemeColors.ColorState update() {
      return new ThemeColors.ColorState();
   }
   public int apply() {
      return this.data;
   }
   public int execute() {
      return this.context;
   }
   public int prepare() {
      return this.config;
   }
   public int check() {
      return this.state;
   }
   public int onTick() {
      return this.cache;
   }
   public int select() {
      return this.output;
   }
   public int refresh() {
      return this.current;
   }
   public int render() {
      return this.active;
   }
   public int tick() {
      return this.mode;
   }
   public int drawAnimation() {
      return this.selection;
   }
   public int encodePoint() {
      return this.enabled;
   }
   public int animate() {
      return this.renderer;
   }
   public int load() {
      return this.handler;
   }
   public int save() {
      return this.animationDraw;
   }
   public int submit() {
      return this.pointEncode;
   }
   public boolean unload() {
      return this.animator;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ThemeColors var2)) {
         return false;
      } else if (this.apply() != var2.apply()) {
         return false;
      } else if (this.execute() != var2.execute()) {
         return false;
      } else if (this.prepare() != var2.prepare()) {
         return false;
      } else if (this.check() != var2.check()) {
         return false;
      } else if (this.onTick() != var2.onTick()) {
         return false;
      } else if (this.select() != var2.select()) {
         return false;
      } else if (this.refresh() != var2.refresh()) {
         return false;
      } else if (this.render() != var2.render()) {
         return false;
      } else if (this.tick() != var2.tick()) {
         return false;
      } else if (this.drawAnimation() != var2.drawAnimation()) {
         return false;
      } else if (this.encodePoint() != var2.encodePoint()) {
         return false;
      } else if (this.animate() != var2.animate()) {
         return false;
      } else if (this.load() != var2.load()) {
         return false;
      } else if (this.save() != var2.save()) {
         return false;
      } else {
         return this.submit() != var2.submit() ? false : this.unload() == var2.unload();
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.apply();
      var2 = var2 * 59 + this.execute();
      var2 = var2 * 59 + this.prepare();
      var2 = var2 * 59 + this.check();
      var2 = var2 * 59 + this.onTick();
      var2 = var2 * 59 + this.select();
      var2 = var2 * 59 + this.refresh();
      var2 = var2 * 59 + this.render();
      var2 = var2 * 59 + this.tick();
      var2 = var2 * 59 + this.drawAnimation();
      var2 = var2 * 59 + this.encodePoint();
      var2 = var2 * 59 + this.animate();
      var2 = var2 * 59 + this.load();
      var2 = var2 * 59 + this.save();
      var2 = var2 * 59 + this.submit();
      return var2 * 59 + (this.unload() ? 79 : 97);
   }
   @Override
   public String toString() {
      return "Colors(panel="
         + this.apply()
         + ", surface="
         + this.execute()
         + ", white01="
         + this.prepare()
         + ", white02="
         + this.check()
         + ", white03="
         + this.onTick()
         + ", white04="
         + this.select()
         + ", white06="
         + this.refresh()
         + ", white08="
         + this.render()
         + ", white12="
         + this.tick()
         + ", white16="
         + this.drawAnimation()
         + ", white24="
         + this.encodePoint()
         + ", white48="
         + this.animate()
         + ", white="
         + this.load()
         + ", accentTop="
         + this.save()
         + ", accentBottom="
         + this.submit()
         + ", lightMode="
         + this.unload()
         + ")";
   }
   public static class ColorState {
      private int instance;
      private int data;
      private int context;
      private int config;
      private int state;
      private int cache;
      private int output;
      private int current;
      private int active;
      private int mode;
      private int selection;
      private int enabled;
      private int renderer;
      private int handler;
      private int animationDraw;
      private boolean pointEncode;
      ColorState() {
      }
      public ThemeColors.ColorState handle(int var1) {
         this.instance = var1;
         return this;
      }
      public ThemeColors.ColorState process(int var1) {
         this.data = var1;
         return this;
      }
      public ThemeColors.ColorState compute(int var1) {
         this.context = var1;
         return this;
      }
      public ThemeColors.ColorState resolve(int var1) {
         this.config = var1;
         return this;
      }
      public ThemeColors.ColorState update(int var1) {
         this.state = var1;
         return this;
      }
      public ThemeColors.ColorState apply(int var1) {
         this.cache = var1;
         return this;
      }
      public ThemeColors.ColorState execute(int var1) {
         this.output = var1;
         return this;
      }
      public ThemeColors.ColorState prepare(int var1) {
         this.current = var1;
         return this;
      }
      public ThemeColors.ColorState check(int var1) {
         this.active = var1;
         return this;
      }
      public ThemeColors.ColorState onTick(int var1) {
         this.mode = var1;
         return this;
      }
      public ThemeColors.ColorState select(int var1) {
         this.selection = var1;
         return this;
      }
      public ThemeColors.ColorState refresh(int var1) {
         this.enabled = var1;
         return this;
      }
      public ThemeColors.ColorState render(int var1) {
         this.renderer = var1;
         return this;
      }
      public ThemeColors.ColorState tick(int var1) {
         this.handler = var1;
         return this;
      }
      public ThemeColors.ColorState drawAnimation(int var1) {
         this.animationDraw = var1;
         return this;
      }
      public ThemeColors.ColorState handle(boolean var1) {
         this.pointEncode = var1;
         return this;
      }
      public ThemeColors handle() {
         return new ThemeColors(
            this.instance,
            this.data,
            this.context,
            this.config,
            this.state,
            this.cache,
            this.output,
            this.current,
            this.active,
            this.mode,
            this.selection,
            this.enabled,
            this.renderer,
            this.handler,
            this.animationDraw,
            this.pointEncode
         );
      }
      @Override
      public String toString() {
         return "Colors.ColorsBuilder(panel="
            + this.instance
            + ", surface="
            + this.data
            + ", white01="
            + this.context
            + ", white02="
            + this.config
            + ", white03="
            + this.state
            + ", white04="
            + this.cache
            + ", white06="
            + this.output
            + ", white08="
            + this.current
            + ", white12="
            + this.active
            + ", white16="
            + this.mode
            + ", white24="
            + this.selection
            + ", white48="
            + this.enabled
            + ", white="
            + this.renderer
            + ", accentTop="
            + this.handler
            + ", accentBottom="
            + this.animationDraw
            + ", lightMode="
            + this.pointEncode
            + ")";
      }
   }
}
