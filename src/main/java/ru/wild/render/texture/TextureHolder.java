package ru.wild.render.texture;

import java.util.ArrayList;
import java.util.List;

public final class TextureHolder {
   private final int instance;
   private final int data;
   private final List<TextureHolder.PrimaryTextureState> context;
   private final List<TextureHolder.TextureState> config;
   private float state = Float.MAX_VALUE;
   private float cache = Float.MAX_VALUE;
   private float output = Float.MAX_VALUE;
   private float current = -Float.MAX_VALUE;
   private float active = -Float.MAX_VALUE;
   private float mode = -Float.MAX_VALUE;
   private boolean selection;

   public TextureHolder(int var1, int var2, List<TextureHolder.PrimaryTextureState> var3, List<TextureHolder.TextureState> var4) {
      this.instance = Math.max(1, var1);
      this.data = Math.max(1, var2);
      this.context = var3 == null ? new ArrayList<>() : var3;
      this.config = var4 == null ? new ArrayList<>() : var4;
   }

   public int handle() {
      return this.instance;
   }

   public int process() {
      return this.data;
   }

   public List<TextureHolder.PrimaryTextureState> compute() {
      return this.context;
   }

   public List<TextureHolder.TextureState> resolve() {
      return this.config;
   }

   public TextureHolder.PrimaryTextureState handle(int var1) {
      if (var1 >= 0 && var1 < this.context.size()) {
         return this.context.get(var1);
      } else {
         return this.context.isEmpty() ? null : this.context.get(0);
      }
   }

   public float update() {
      this.tick();
      return this.cache;
   }

   public float apply() {
      this.tick();
      return this.active;
   }

   public float execute() {
      this.tick();
      return (this.state + this.current) * 0.5F;
   }

   public float prepare() {
      this.tick();
      return (this.cache + this.active) * 0.5F;
   }

   public float check() {
      this.tick();
      return (this.output + this.mode) * 0.5F;
   }

   public float onTick() {
      this.tick();
      float var1 = this.active - this.cache;
      return var1 <= 0.0F ? 32.0F : var1;
   }

   public int select() {
      int[] var1 = new int[]{0};

      for (TextureHolder.TextureState var3 : this.config) {
         this.handle(var3, var1);
      }

      return var1[0];
   }

   private void handle(TextureHolder.TextureState var1, int[] var2) {
      var2[0] += var1.onTick().size();

      for (TextureHolder.TextureState var4 : var1.check()) {
         this.handle(var4, var2);
      }
   }

   public float refresh() {
      this.tick();
      float var1 = this.current - this.state;
      return var1 <= 0.0F ? 16.0F : var1;
   }

   public float render() {
      this.tick();
      float var1 = this.mode - this.output;
      return var1 <= 0.0F ? 16.0F : var1;
   }

   private void tick() {
      if (!this.selection) {
         this.selection = true;

         for (TextureHolder.TextureState var2 : this.config) {
            this.handle(var2);
         }

         if (this.cache > this.active) {
            this.state = this.cache = this.output = 0.0F;
            this.current = this.active = this.mode = 32.0F;
         }
      }
   }

   private void handle(TextureHolder.TextureState var1) {
      for (TextureHolder.RuntimeTextureState var3 : var1.onTick()) {
         this.handle(var3.handle(), var3.process(), var3.compute());
         this.handle(var3.resolve(), var3.update(), var3.apply());
      }

      for (TextureHolder.CachedTextureState var8 : var1.select()) {
         float[] var4 = var8.execute();

         for (byte var5 = 0; var5 + 2 < var4.length; var5 += 3) {
            this.handle(var8.handle() + var4[var5], var8.process() + var4[var5 + 1], var8.compute() + var4[var5 + 2]);
         }
      }

      for (TextureHolder.TextureState var9 : var1.check()) {
         this.handle(var9);
      }
   }

   private void handle(float var1, float var2, float var3) {
      if (var1 < this.state) {
         this.state = var1;
      }

      if (var2 < this.cache) {
         this.cache = var2;
      }

      if (var3 < this.output) {
         this.output = var3;
      }

      if (var1 > this.current) {
         this.current = var1;
      }

      if (var2 > this.active) {
         this.active = var2;
      }

      if (var3 > this.mode) {
         this.mode = var3;
      }
   }

   public static final class CachedTextureState {
      private final float instance;
      private final float data;
      private final float context;
      private final float config;
      private final float state;
      private final float cache;
      private final float[] output;
      private final TextureHolder.FallbackTextureState[] current;

      public CachedTextureState(float var1, float var2, float var3, float var4, float var5, float var6, float[] var7, TextureHolder.FallbackTextureState[] var8) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
      }

      public float handle() {
         return this.instance;
      }

      public float process() {
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

      public float apply() {
         return this.cache;
      }

      public float[] execute() {
         return this.output;
      }

      public TextureHolder.FallbackTextureState[] prepare() {
         return this.current;
      }

      public boolean check() {
         return this.config != 0.0F || this.state != 0.0F || this.cache != 0.0F;
      }

      public float handle(int var1) {
         return this.output[var1 * 3];
      }

      public float process(int var1) {
         return this.output[var1 * 3 + 1];
      }

      public float compute(int var1) {
         return this.output[var1 * 3 + 2];
      }
   }

   public static final class FallbackTextureState {
      private final int instance;
      private final int[] data;
      private final float[] context;
      private final float[] config;
      private final int state;

      public FallbackTextureState(int var1, int[] var2, float[] var3, float[] var4, int var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
      }

      public int handle() {
         return this.instance;
      }

      public int handle(int var1) {
         return this.data[var1];
      }

      public float process(int var1) {
         return this.context[var1];
      }

      public float compute(int var1) {
         return this.config[var1];
      }

      public int process() {
         return this.state;
      }
   }

   public static final class PrimaryTextureState {
      private final String instance;
      private final byte[] data;
      private final int context;
      private final int config;

      public PrimaryTextureState(String var1, byte[] var2, int var3, int var4) {
         this.instance = var1 == null ? "texture" : var1;
         this.data = var2 == null ? new byte[0] : var2;
         this.context = Math.max(1, var3);
         this.config = Math.max(1, var4);
      }

      public String handle() {
         return this.instance;
      }

      public byte[] process() {
         return this.data;
      }

      public int compute() {
         return this.context;
      }

      public int resolve() {
         return this.config;
      }
   }

   public static final class RuntimeTextureState {
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
      private final TextureHolder.SecondaryTextureState[] handler;

      public RuntimeTextureState(
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
         TextureHolder.SecondaryTextureState[] var14
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
      }

      public float handle() {
         return this.instance;
      }

      public float process() {
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

      public float apply() {
         return this.cache;
      }

      public float execute() {
         return this.output;
      }

      public float prepare() {
         return this.current;
      }

      public float check() {
         return this.active;
      }

      public float onTick() {
         return this.mode;
      }

      public float select() {
         return this.selection;
      }

      public float refresh() {
         return this.enabled;
      }

      public float render() {
         return this.renderer;
      }

      public boolean tick() {
         return this.mode != 0.0F || this.selection != 0.0F || this.enabled != 0.0F;
      }

      public TextureHolder.SecondaryTextureState handle(int var1) {
         return var1 >= 0 && var1 < this.handler.length ? this.handler[var1] : null;
      }
   }

   public static final class SecondaryTextureState {
      private final int instance;
      private final float data;
      private final float context;
      private final float config;
      private final float state;

      public SecondaryTextureState(int var1, float var2, float var3, float var4, float var5) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
      }

      public int handle() {
         return this.instance;
      }

      public float process() {
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

   public static final class TextureState {
      private final String instance;
      private final float data;
      private final float context;
      private final float config;
      private final float state;
      private final float cache;
      private final float output;
      private final List<TextureHolder.TextureState> current = new ArrayList<>();
      private final List<TextureHolder.RuntimeTextureState> active = new ArrayList<>();
      private final List<TextureHolder.CachedTextureState> mode = new ArrayList<>();

      public TextureState(String var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         this.instance = var1 == null ? "" : var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
      }

      public String handle() {
         return this.instance;
      }

      public float process() {
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

      public float apply() {
         return this.cache;
      }

      public float execute() {
         return this.output;
      }

      public boolean prepare() {
         return this.state != 0.0F || this.cache != 0.0F || this.output != 0.0F;
      }

      public List<TextureHolder.TextureState> check() {
         return this.current;
      }

      public List<TextureHolder.RuntimeTextureState> onTick() {
         return this.active;
      }

      public List<TextureHolder.CachedTextureState> select() {
         return this.mode;
      }
   }
}
