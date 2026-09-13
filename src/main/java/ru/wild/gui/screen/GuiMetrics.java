package ru.wild.gui.screen;
import net.minecraft.client.MinecraftClient;
import ru.wild.modules.visuals.Menu;

public final class GuiMetrics {
   private final float instance;
   private final float data;
   private final float context;
   private final float config;
   private final float state;
   private final float cache;
   private final float output;
   private final float current;
   private final float active;
   private final float mode;
   private final float selection;
   private final float enabled;
   private final float renderer;
   private final float handler;
   private final float animationDraw;
   private final float pointEncode;
   private final float animator;
   private final float source;
   private final float target;

   public static GuiMetrics handle(MinecraftClient var0, GuiLayoutSpec var1) {
      return var0 != null && var0.getWindow() != null && var0.getWindow().getFramebufferWidth() > 0 && var0.getWindow().getFramebufferHeight() > 0
         ? handle(var0.getWindow().getFramebufferWidth(), var0.getWindow().getFramebufferHeight(), handle(var0), var1)
         : handle(var1.unload(), var1);
   }

   public static GuiMetrics handle(float var0, float var1, GuiLayoutSpec var2) {
      return handle(var0, var1, 1.0F, var2);
   }

   public static GuiMetrics handle(float var0, float var1, float var2, GuiLayoutSpec var3) {
      if (!(var0 <= 0.0F) && !(var1 <= 0.0F)) {
         float var4 = 16.0F;
         float var5 = (var0 - var4 * 2.0F) / var3.compute();
         float var6 = (var1 - var4 * 2.0F) / var3.resolve();
         float var7 = Math.min(var5, var6);
         float var8 = Math.max(1.0F, var2);
         float var9 = 0.68F + Math.min(var8, 2.0F) * 0.28F;
         float var10 = Math.max(var3.submit(), Math.min(var3.unload(), var9 * unload()));
         float var11 = Math.min(var10, var7);
         var11 = Math.max(var3.submit(), Math.min(var3.unload(), var11));
         float var12 = Math.max(var3.submit(), Math.min(var3.unload(), var9 * fetch()));
         float var13 = Math.max(var3.submit(), Math.min(var3.unload(), var12));
         return process(var11, var13, var3);
      } else {
         return handle(var3.unload(), var3);
      }
   }

   public static GuiMetrics handle(float var0, GuiLayoutSpec var1) {
      return process(var0, var0, var1);
   }

   public static GuiMetrics process(float var0, float var1, GuiLayoutSpec var2) {
      return handle()
         .handle(var0)
         .process(var1)
         .compute(Math.round(var2.compute() * var0))
         .resolve(Math.round(var2.resolve() * var0))
         .update(var2.update() * var0)
         .apply(var2.apply() * var0)
         .execute(var2.execute() * var0)
         .prepare(var2.prepare() * var0)
         .check(var2.check() * var0)
         .onTick(var2.onTick() * var0)
         .select(var2.select() * var0)
         .refresh(var2.refresh() * var0)
         .render(var2.render() * var0)
         .tick(var2.tick() * var0)
         .drawAnimation(var2.drawAnimation() * var0)
         .encodePoint(var2.encodePoint() * var0)
         .animate(var2.animate() * var0)
         .load(Math.round(var2.load() * var1))
         .save(Math.round(var2.save() * var1))
         .handle();
   }

   public float handle(float var1) {
      return var1 * this.instance;
   }

   public float process(float var1) {
      return var1 * this.data;
   }

   public GuiMetrics compute(float var1) {
      return handle()
         .handle(var1)
         .process(this.data)
         .compute(this.context)
         .resolve(this.config)
         .update(this.state)
         .apply(this.cache)
         .execute(this.output)
         .prepare(this.current)
         .check(this.active)
         .onTick(this.mode)
         .select(this.selection)
         .refresh(this.enabled)
         .render(this.renderer)
         .tick(this.handler)
         .drawAnimation(this.animationDraw)
         .encodePoint(this.pointEncode)
         .animate(this.animator)
         .load(this.source)
         .save(this.target)
         .handle();
   }

   private static float handle(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         try {
            return Math.max(1.0F, var0.getWindow().getScaleFactor());
         } catch (Exception var3) {
            int var2 = Math.max(1, var0.getWindow().getScaledWidth());
            return Math.max(1.0F, (float)var0.getWindow().getFramebufferWidth() / var2);
         }
      } else {
         return 1.0F;
      }
   }

   private static float unload() {
      try {
         return Menu.responseCompute == null ? 0.86F : Math.max(0.72F, Math.min(1.7F, Menu.responseCompute.compute()));
      } catch (Throwable var1) {
         return 0.86F;
      }
   }

   private static float fetch() {
      try {
         return Menu.providerFetch == null ? 0.86F : Math.max(0.72F, Math.min(1.7F, Menu.providerFetch.compute()));
      } catch (Throwable var1) {
         return 0.86F;
      }
   }
   GuiMetrics(
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
      float var19
   ) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var6;
      this.output = var7;
      this.current = var8;
      this.active = var9;
      this.mode = var10;
      this.selection = var11;
      this.enabled = var12;
      this.renderer = var13;
      this.handler = var14;
      this.animationDraw = var15;
      this.pointEncode = var16;
      this.animator = var17;
      this.source = var18;
      this.target = var19;
   }
   public static GuiMetrics.ColorState handle() {
      return new GuiMetrics.ColorState();
   }
   public float process() {
      return this.instance;
   }
   public float compute() {
      return this.data;
   }
   public float resolve() {
      return this.context;
   }
   public float update() {
      return this.config;
   }
   public float apply() {
      return this.state;
   }
   public float execute() {
      return this.cache;
   }
   public float prepare() {
      return this.output;
   }
   public float check() {
      return this.current;
   }
   public float onTick() {
      return this.active;
   }
   public float select() {
      return this.mode;
   }
   public float refresh() {
      return this.selection;
   }
   public float render() {
      return this.enabled;
   }
   public float tick() {
      return this.renderer;
   }
   public float drawAnimation() {
      return this.handler;
   }
   public float encodePoint() {
      return this.animationDraw;
   }
   public float animate() {
      return this.pointEncode;
   }
   public float load() {
      return this.animator;
   }
   public float save() {
      return this.source;
   }
   public float submit() {
      return this.target;
   }
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GuiMetrics var2)) {
         return false;
      } else if (Float.compare(this.process(), var2.process()) != 0) {
         return false;
      } else if (Float.compare(this.compute(), var2.compute()) != 0) {
         return false;
      } else if (Float.compare(this.resolve(), var2.resolve()) != 0) {
         return false;
      } else if (Float.compare(this.update(), var2.update()) != 0) {
         return false;
      } else if (Float.compare(this.apply(), var2.apply()) != 0) {
         return false;
      } else if (Float.compare(this.execute(), var2.execute()) != 0) {
         return false;
      } else if (Float.compare(this.prepare(), var2.prepare()) != 0) {
         return false;
      } else if (Float.compare(this.check(), var2.check()) != 0) {
         return false;
      } else if (Float.compare(this.onTick(), var2.onTick()) != 0) {
         return false;
      } else if (Float.compare(this.select(), var2.select()) != 0) {
         return false;
      } else if (Float.compare(this.refresh(), var2.refresh()) != 0) {
         return false;
      } else if (Float.compare(this.render(), var2.render()) != 0) {
         return false;
      } else if (Float.compare(this.tick(), var2.tick()) != 0) {
         return false;
      } else if (Float.compare(this.drawAnimation(), var2.drawAnimation()) != 0) {
         return false;
      } else if (Float.compare(this.encodePoint(), var2.encodePoint()) != 0) {
         return false;
      } else if (Float.compare(this.animate(), var2.animate()) != 0) {
         return false;
      } else if (Float.compare(this.load(), var2.load()) != 0) {
         return false;
      } else {
         return Float.compare(this.save(), var2.save()) != 0 ? false : Float.compare(this.submit(), var2.submit()) == 0;
      }
   }
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.process());
      var2 = var2 * 59 + Float.floatToIntBits(this.compute());
      var2 = var2 * 59 + Float.floatToIntBits(this.resolve());
      var2 = var2 * 59 + Float.floatToIntBits(this.update());
      var2 = var2 * 59 + Float.floatToIntBits(this.apply());
      var2 = var2 * 59 + Float.floatToIntBits(this.execute());
      var2 = var2 * 59 + Float.floatToIntBits(this.prepare());
      var2 = var2 * 59 + Float.floatToIntBits(this.check());
      var2 = var2 * 59 + Float.floatToIntBits(this.onTick());
      var2 = var2 * 59 + Float.floatToIntBits(this.select());
      var2 = var2 * 59 + Float.floatToIntBits(this.refresh());
      var2 = var2 * 59 + Float.floatToIntBits(this.render());
      var2 = var2 * 59 + Float.floatToIntBits(this.tick());
      var2 = var2 * 59 + Float.floatToIntBits(this.drawAnimation());
      var2 = var2 * 59 + Float.floatToIntBits(this.encodePoint());
      var2 = var2 * 59 + Float.floatToIntBits(this.animate());
      var2 = var2 * 59 + Float.floatToIntBits(this.load());
      var2 = var2 * 59 + Float.floatToIntBits(this.save());
      return var2 * 59 + Float.floatToIntBits(this.submit());
   }
   @Override
   public String toString() {
      return "Metrics(scale="
         + this.process()
         + ", themeScale="
         + this.compute()
         + ", guiW="
         + this.resolve()
         + ", guiH="
         + this.update()
         + ", padding="
         + this.apply()
         + ", gap="
         + this.execute()
         + ", sidebarW="
         + this.prepare()
         + ", bodyW="
         + this.check()
         + ", bodyH="
         + this.onTick()
         + ", headerH="
         + this.select()
         + ", searchW="
         + this.refresh()
         + ", contentH="
         + this.render()
         + ", contentPadding="
         + this.tick()
         + ", columnW="
         + this.drawAnimation()
         + ", moduleHeaderH="
         + this.encodePoint()
         + ", moduleGap="
         + this.animate()
         + ", scrollbarW="
         + this.load()
         + ", themeW="
         + this.save()
         + ", themeH="
         + this.submit()
         + ")";
   }
   public static class ColorState {
      private float instance;
      private float data;
      private float context;
      private float config;
      private float state;
      private float cache;
      private float output;
      private float current;
      private float active;
      private float mode;
      private float selection;
      private float enabled;
      private float renderer;
      private float handler;
      private float animationDraw;
      private float pointEncode;
      private float animator;
      private float source;
      private float target;
      ColorState() {
      }
      public GuiMetrics.ColorState handle(float var1) {
         this.instance = var1;
         return this;
      }
      public GuiMetrics.ColorState process(float var1) {
         this.data = var1;
         return this;
      }
      public GuiMetrics.ColorState compute(float var1) {
         this.context = var1;
         return this;
      }
      public GuiMetrics.ColorState resolve(float var1) {
         this.config = var1;
         return this;
      }
      public GuiMetrics.ColorState update(float var1) {
         this.state = var1;
         return this;
      }
      public GuiMetrics.ColorState apply(float var1) {
         this.cache = var1;
         return this;
      }
      public GuiMetrics.ColorState execute(float var1) {
         this.output = var1;
         return this;
      }
      public GuiMetrics.ColorState prepare(float var1) {
         this.current = var1;
         return this;
      }
      public GuiMetrics.ColorState check(float var1) {
         this.active = var1;
         return this;
      }
      public GuiMetrics.ColorState onTick(float var1) {
         this.mode = var1;
         return this;
      }
      public GuiMetrics.ColorState select(float var1) {
         this.selection = var1;
         return this;
      }
      public GuiMetrics.ColorState refresh(float var1) {
         this.enabled = var1;
         return this;
      }
      public GuiMetrics.ColorState render(float var1) {
         this.renderer = var1;
         return this;
      }
      public GuiMetrics.ColorState tick(float var1) {
         this.handler = var1;
         return this;
      }
      public GuiMetrics.ColorState drawAnimation(float var1) {
         this.animationDraw = var1;
         return this;
      }
      public GuiMetrics.ColorState encodePoint(float var1) {
         this.pointEncode = var1;
         return this;
      }
      public GuiMetrics.ColorState animate(float var1) {
         this.animator = var1;
         return this;
      }
      public GuiMetrics.ColorState load(float var1) {
         this.source = var1;
         return this;
      }
      public GuiMetrics.ColorState save(float var1) {
         this.target = var1;
         return this;
      }
      public GuiMetrics handle() {
         return new GuiMetrics(
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
            this.pointEncode,
            this.animator,
            this.source,
            this.target
         );
      }
      @Override
      public String toString() {
         return "Metrics.MetricsBuilder(scale="
            + this.instance
            + ", themeScale="
            + this.data
            + ", guiW="
            + this.context
            + ", guiH="
            + this.config
            + ", padding="
            + this.state
            + ", gap="
            + this.cache
            + ", sidebarW="
            + this.output
            + ", bodyW="
            + this.current
            + ", bodyH="
            + this.active
            + ", headerH="
            + this.mode
            + ", searchW="
            + this.selection
            + ", contentH="
            + this.enabled
            + ", contentPadding="
            + this.renderer
            + ", columnW="
            + this.handler
            + ", moduleHeaderH="
            + this.animationDraw
            + ", moduleGap="
            + this.pointEncode
            + ", scrollbarW="
            + this.animator
            + ", themeW="
            + this.source
            + ", themeH="
            + this.target
            + ")";
      }
   }
}
