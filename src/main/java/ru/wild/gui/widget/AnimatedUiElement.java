package ru.wild.gui.widget;

import ru.wild.gui.screen.ModernClickGuiState;

public final class AnimatedUiElement {
   private final int instance;
   private final float data;
   private final float context;
   private final float config;
   private final float state;
   private final float cache;
   private final float output;
   private final float current;
   private final float active;
   private final boolean mode;
   private final float selection;
   private final float enabled;
   private final float renderer;
   private final float handler;
   private final boolean animationDraw;
   private final float pointEncode;
   private final float animator;
   private final float source;
   private final float target;
   private final float pending;
   private final UiRenderCommand previous;

   AnimatedUiElement(AnimatedUiElement.CacheEntry var1) {
      this.instance = var1.instance;
      this.data = var1.data;
      this.context = var1.context;
      this.config = var1.config;
      this.state = var1.state;
      this.cache = var1.cache;
      this.output = var1.output;
      this.current = var1.current;
      this.active = var1.active;
      this.mode = var1.current > 0.0F && var1.active > 0.0F;
      this.selection = 0.0F;
      this.enabled = 0.0F;
      this.renderer = 0.0F;
      this.handler = 0.0F;
      this.animationDraw = false;
      this.pointEncode = 1.0F;
      this.animator = 0.0F;
      this.source = 0.0F;
      this.target = 0.0F;
      this.pending = 0.0F;
      this.previous = var1.mode;
   }

   private AnimatedUiElement(
      AnimatedUiElement var1,
      float var2,
      float var3,
      float var4,
      float var5,
      boolean var6,
      float var7,
      float var8,
      float var9,
      float var10,
      boolean var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16
   ) {
      this.instance = var1.instance;
      this.data = var1.data;
      this.context = var1.context;
      this.config = var1.config;
      this.state = var1.state;
      this.cache = var2;
      this.output = var3;
      this.current = var4;
      this.active = var5;
      this.mode = var6;
      this.selection = var7;
      this.enabled = var8;
      this.renderer = var9;
      this.handler = var10;
      this.animationDraw = var11;
      this.pointEncode = Math.max(0.001F, var12);
      this.animator = var13;
      this.source = var14;
      this.target = var15;
      this.pending = var16;
      this.previous = var1.previous;
   }

   public static AnimatedUiElement.CacheEntry handle() {
      return new AnimatedUiElement.CacheEntry();
   }

   public boolean handle(float var1, float var2, int var3) {
      if ((this.instance < 0 || this.instance == var3) && this.handle(var1, var2)) {
         float var4 = this.handle(var1);
         float var5 = this.process(var2);
         return this.process(var4, var5) && var4 >= this.data && var5 >= this.context && var4 < this.data + this.config && var5 < this.context + this.state;
      } else {
         return false;
      }
   }

   public float handle(float var1) {
      return this.animator + (var1 - this.target - this.animator) / this.pointEncode;
   }

   public float process(float var1) {
      return this.source + (var1 - this.pending - this.source) / this.pointEncode;
   }

   public AnimatedUiElement handle(float var1, float var2, float var3, float var4, float var5) {
      return new AnimatedUiElement(
         this,
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
         var1,
         var2,
         var3,
         var4,
         var5
      );
   }

   public AnimatedUiElement handle(float var1, float var2, float var3, float var4) {
      return new AnimatedUiElement(
         this,
         this.cache,
         this.output,
         this.current,
         this.active,
         this.mode,
         var1,
         var2,
         var3,
         var4,
         var3 > 0.0F && var4 > 0.0F,
         this.pointEncode,
         this.animator,
         this.source,
         this.target,
         this.pending
      );
   }

   public AnimatedUiElement process(float var1, float var2, float var3, float var4) {
      if (var3 <= 0.0F || var4 <= 0.0F) {
         return this;
      }

      if (!this.mode) {
         return new AnimatedUiElement(
            this,
            var1,
            var2,
            var3,
            var4,
            true,
            this.selection,
            this.enabled,
            this.renderer,
            this.handler,
            this.animationDraw,
            this.pointEncode,
            this.animator,
            this.source,
            this.target,
            this.pending
         );
      }

      float var5 = Math.max(this.cache, var1);
      float var6 = Math.max(this.output, var2);
      float var7 = Math.min(this.cache + this.current, var1 + var3);
      float var8 = Math.min(this.output + this.active, var2 + var4);
      return new AnimatedUiElement(
         this,
         var5,
         var6,
         Math.max(0.0F, var7 - var5),
         Math.max(0.0F, var8 - var6),
         true,
         this.selection,
         this.enabled,
         this.renderer,
         this.handler,
         this.animationDraw,
         this.pointEncode,
         this.animator,
         this.source,
         this.target,
         this.pending
      );
   }

   public void handle(ModernClickGuiState var1) {
      if (this.previous != null) {
         this.previous.execute(var1);
      }
   }

   private boolean handle(float var1, float var2) {
      return !this.mode ? true : var1 >= this.cache && var2 >= this.output && var1 < this.cache + this.current && var2 < this.output + this.active;
   }

   private boolean process(float var1, float var2) {
      return !this.animationDraw
         ? true
         : var1 >= this.selection && var2 >= this.enabled && var1 < this.selection + this.renderer && var2 < this.enabled + this.handler;
   }

   public static final class CacheEntry {
      int instance = -1;
      float data;
      float context;
      float config;
      float state;
      float cache;
      float output;
      float current;
      float active;
      UiRenderCommand mode;

      public AnimatedUiElement.CacheEntry handle(int var1) {
         this.instance = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry handle(float var1) {
         this.data = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry process(float var1) {
         this.context = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry compute(float var1) {
         this.config = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry resolve(float var1) {
         this.state = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry update(float var1) {
         this.cache = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry apply(float var1) {
         this.output = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry execute(float var1) {
         this.current = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry prepare(float var1) {
         this.active = var1;
         return this;
      }

      public AnimatedUiElement.CacheEntry handle(UiRenderCommand var1) {
         this.mode = var1;
         return this;
      }

      public AnimatedUiElement handle() {
         return new AnimatedUiElement(this);
      }
   }
}
