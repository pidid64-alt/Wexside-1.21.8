package ru.wild.api.setting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.SmoothTimer;

public class ColorSetting extends Setting {
   public static final int config = 8;
   public float state;
   public float cache;
   public float output;
   public float current;
   public float active;
   public boolean mode;
   public String selection;
   public SmoothTimer enabled = new EaseTimer(300, 1.0);
   public float renderer = 1.0F;
   public float handler = 1.0F;
   public float animationDraw = 1.0F;
   public final List<Integer> pointEncode = new ArrayList<>();
   protected float animator;
   protected float source;
   protected float target;
   protected float pending;

   public ColorSetting(String var1, float var2) {
      this.instance = var1;
      this.cache = 0.0F;
      this.output = 106.0F;
      this.current = 1.0F;
      if (!(var2 < this.cache) && !(var2 > this.output)) {
         this.state = var2;
         this.renderer = 1.0F;
         this.handler = 1.0F;
         this.animationDraw = 1.0F;
      } else {
         this.handle((int)var2);
      }

      this.onTick();
   }

   public ColorSetting(String var1, float var2, float var3, float var4) {
      this(var1, var2, var3, var4, 1.0F);
   }

   public ColorSetting(String var1, float var2, float var3, float var4, float var5) {
      this.instance = var1;
      this.cache = 0.0F;
      this.state = var2;
      this.output = 106.0F;
      this.current = 1.0F;
      this.renderer = compute(var3);
      this.handler = compute(var4);
      this.animationDraw = compute(var5);
      this.onTick();
   }

   public ColorSetting process(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   public Color compute() {
      float var1 = this.resolve();
      Color var2 = Color.getHSBColor(var1, this.renderer, this.handler);
      return new Color(var2.getRed(), var2.getGreen(), var2.getBlue(), Math.round(this.animationDraw * 255.0F));
   }

   public void handle(Color var1) {
      float[] var2 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), null);
      this.state = var2[0] * this.output;
      this.renderer = var2[1];
      this.handler = var2[2];
      this.animationDraw = var1.getAlpha() / 255.0F;
   }

   public void handle(int var1) {
      int var2 = var1 >= 0 && var1 <= 16777215 ? 0xFF000000 | var1 : var1;
      this.handle(new Color(var2, true));
   }

   public float resolve() {
      return compute(this.state / this.output);
   }

   public float update() {
      return this.resolve() * 360.0F;
   }

   public void handle(float var1) {
      float var2 = var1 % 360.0F;
      if (var2 < 0.0F) {
         var2 += 360.0F;
      }

      this.state = var2 / 360.0F * this.output;
   }

   public void process(float var1) {
      this.animationDraw = compute(var1);
   }

   public void process(int var1) {
      this.pointEncode.removeIf(var1x -> var1x == var1);
      this.pointEncode.add(0, var1);

      while (this.pointEncode.size() > 8) {
         this.pointEncode.remove(this.pointEncode.size() - 1);
      }
   }

   public void execute() {
      this.process(this.check());
   }

   public void compute(int var1) {
      if (var1 >= 0 && var1 < this.pointEncode.size()) {
         this.handle(this.pointEncode.get(var1));
      }
   }

   public void resolve(int var1) {
      if (var1 >= 0 && var1 < this.pointEncode.size()) {
         this.pointEncode.remove(var1);
      }
   }

   public int prepare() {
      return this.compute().getRGB();
   }

   public int check() {
      return this.compute().getRGB();
   }

   public int update(int var1) {
      Color var2 = this.compute();
      return var1 << 24 | var2.getRed() << 16 | var2.getGreen() << 8 | var2.getBlue();
   }

   private static float compute(float var0) {
      return Float.isFinite(var0) && !(var0 <= 0.0F) ? Math.min(var0, 1.0F) : 0.0F;
   }

   protected void onTick() {
      this.animator = this.state;
      this.source = this.renderer;
      this.target = this.handler;
      this.pending = this.animationDraw;
   }

   @Override
   public void process() {
      this.state = this.animator;
      this.renderer = this.source;
      this.handler = this.target;
      this.animationDraw = this.pending;
      this.mode = false;
   }
}
