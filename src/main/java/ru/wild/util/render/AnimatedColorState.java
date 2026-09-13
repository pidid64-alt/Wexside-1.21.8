package ru.wild.util.render;

import java.util.Arrays;
import ru.wild.core.AnimationClock;
import ru.wild.util.math.SpringAnimationSpec;

public final class AnimatedColorState implements AnimationClock.Predicate {
   public static final AnimatedColorState instance = new AnimatedColorState();
   private static final int data = 128;
   private static final int context = -1;
   private static final float config = 0.004166667F;
   private static final int state = 60;
   private static final float cache = 1.0E-4F;
   private static final float output = 0.25F;
   private static final float current = 0.016666668F;
   private float[] active;
   private float[] mode;
   private float[] selection;
   private float[] enabled;
   private float[] renderer;
   private float[] handler;
   private float[] animationDraw;
   private int[] pointEncode;
   private int[] animator;
   private int[] source;
   private int target = 128;
   private int pending;
   private int previous;
   private float latest;
   private boolean summary;

   private AnimatedColorState() {
      this.active = new float[this.target];
      this.mode = new float[this.target];
      this.selection = new float[this.target];
      this.enabled = new float[this.target];
      this.renderer = new float[this.target];
      this.handler = new float[this.target];
      this.animationDraw = new float[this.target];
      this.pointEncode = new int[this.target];
      this.animator = new int[this.target];
      this.source = new int[this.target];
      Arrays.fill(this.pointEncode, -1);
      Arrays.fill(this.animator, -1);

      for (int var1 = 0; var1 < this.target; var1++) {
         this.source[var1] = this.target - 1 - var1;
      }

      this.previous = this.target;
   }

   public int handle(float var1, SpringAnimationSpec var2) {
      SpringAnimationSpec var3 = var2 == null ? SpringAnimationSpec.handle() : var2;
      return this.handle(var1, var3.load(), var3.save(), var3.submit(), var3.unload());
   }

   public int handle(float var1, float var2, float var3, float var4, float var5) {
      if (this.pending == this.target) {
         this.compute();
      }

      int var6 = this.source[--this.previous];
      int var7 = this.pending++;
      this.active[var7] = var1;
      this.mode[var7] = var1;
      this.selection[var7] = 0.0F;
      this.enabled[var7] = var2;
      this.renderer[var7] = var3;
      this.handler[var7] = var4;
      this.animationDraw[var7] = var5;
      this.animator[var7] = var6;
      this.pointEncode[var6] = var7;
      this.process();
      return var6;
   }

   public void handle(int var1) {
      if (var1 >= 0 && var1 < this.target) {
         int var2 = this.pointEncode[var1];
         if (var2 != -1) {
            int var3 = --this.pending;
            if (var2 != var3) {
               this.active[var2] = this.active[var3];
               this.mode[var2] = this.mode[var3];
               this.selection[var2] = this.selection[var3];
               this.enabled[var2] = this.enabled[var3];
               this.renderer[var2] = this.renderer[var3];
               this.handler[var2] = this.handler[var3];
               this.animationDraw[var2] = this.animationDraw[var3];
               int var4 = this.animator[var3];
               this.animator[var2] = var4;
               this.pointEncode[var4] = var2;
            }

            this.animator[var3] = -1;
            this.pointEncode[var1] = -1;
            this.source[this.previous++] = var1;
         }
      }
   }

   public void handle(int var1, float var2) {
      int var3 = this.update(var1);
      if (var3 != -1) {
         this.mode[var3] = var2;
      }
   }

   public void process(int var1, float var2) {
      int var3 = this.update(var1);
      if (var3 != -1) {
         this.active[var3] = var2;
         this.mode[var3] = var2;
         this.selection[var3] = 0.0F;
      }
   }

   public float process(int var1) {
      int var2 = this.update(var1);
      return var2 == -1 ? 0.0F : this.active[var2];
   }

   public float compute(int var1) {
      int var2 = this.update(var1);
      return var2 == -1 ? 0.0F : this.mode[var2];
   }

   public boolean resolve(int var1) {
      int var2 = this.update(var1);
      return var2 == -1
         ? true
         : Math.abs(this.mode[var2] - this.active[var2]) <= this.handler[var2] && Math.abs(this.selection[var2]) <= this.animationDraw[var2];
   }

   public int handle() {
      return this.pending;
   }

   @Override
   public boolean handle(float var1) {
      int var2 = this.pending;
      if (var2 == 0) {
         this.latest = 0.0F;
         return true;
      }

      float var3 = var1;
      if (!Float.isFinite(var3) || var3 <= 0.0F) {
         var3 = 0.016666668F;
      } else if (var3 < 1.0E-4F) {
         var3 = 1.0E-4F;
      } else if (var3 > 0.25F) {
         var3 = 0.25F;
      }

      this.latest += var3;
      float[] var4 = this.active;
      float[] var5 = this.mode;
      float[] var6 = this.selection;
      float[] var7 = this.enabled;
      float[] var8 = this.renderer;
      float[] var9 = this.handler;
      float[] var10 = this.animationDraw;

      int var11;
      for (var11 = 0; this.latest >= 0.004166667F && var11 < 60; var11++) {
         for (int var12 = 0; var12 < var2; var12++) {
            float var13 = var4[var12];
            float var14 = var5[var12];
            float var15 = var6[var12] + (var14 - var13) * var7[var12] - var6[var12] * var8[var12];
            var13 += var15;
            if (Math.abs(var14 - var13) <= var9[var12] && Math.abs(var15) <= var10[var12]) {
               var13 = var14;
               var15 = 0.0F;
            }

            var4[var12] = var13;
            var6[var12] = var15;
         }

         this.latest -= 0.004166667F;
      }

      if (var11 == 60) {
         this.latest = 0.0F;
      }

      return true;
   }

   private int update(int var1) {
      return var1 >= 0 && var1 < this.target ? this.pointEncode[var1] : -1;
   }

   private void process() {
      if (!this.summary) {
         this.summary = true;
         AnimationClock.handle().handle(this);
      }
   }

   private void compute() {
      int var1 = this.target << 1;
      this.active = Arrays.copyOf(this.active, var1);
      this.mode = Arrays.copyOf(this.mode, var1);
      this.selection = Arrays.copyOf(this.selection, var1);
      this.enabled = Arrays.copyOf(this.enabled, var1);
      this.renderer = Arrays.copyOf(this.renderer, var1);
      this.handler = Arrays.copyOf(this.handler, var1);
      this.animationDraw = Arrays.copyOf(this.animationDraw, var1);
      this.pointEncode = Arrays.copyOf(this.pointEncode, var1);
      this.animator = Arrays.copyOf(this.animator, var1);
      this.source = Arrays.copyOf(this.source, var1);

      for (int var2 = this.target; var2 < var1; this.source[this.previous++] = var2++) {
         this.pointEncode[var2] = -1;
         this.animator[var2] = -1;
      }

      this.target = var1;
   }
}
