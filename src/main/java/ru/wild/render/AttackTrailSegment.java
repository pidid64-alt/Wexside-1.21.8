package ru.wild.render;

import net.minecraft.util.math.Vec3d;

public final class AttackTrailSegment {
   private final double instance;
   private final double data;
   private final double context;
   private final double config;
   private final double state;
   private final double cache;
   private final long output;
   private final long current;
   private final float active;
   private final float mode;
   private final int selection;

   public AttackTrailSegment(Vec3d var1, Vec3d var2, long var3, long var5, float var7, float var8, int var9) {
      this.instance = var1.x;
      this.data = var1.y;
      this.context = var1.z;
      double var10 = var2.x;
      double var12 = var2.y;
      double var14 = var2.z;
      double var16 = Math.sqrt(var10 * var10 + var12 * var12 + var14 * var14);
      if (var16 < 1.0E-6) {
         var10 = 0.0;
         var12 = 0.0;
         var14 = 1.0;
      } else {
         var10 /= var16;
         var12 /= var16;
         var14 /= var16;
      }

      this.config = var10;
      this.state = var12;
      this.cache = var14;
      this.output = var3;
      this.current = Math.max(1L, var5);
      this.active = var7;
      this.mode = var8;
      this.selection = var9 & 16777215;
   }

   public double handle() {
      return this.instance;
   }

   public double process() {
      return this.data;
   }

   public double compute() {
      return this.context;
   }

   public double resolve() {
      return this.config;
   }

   public double update() {
      return this.state;
   }

   public double apply() {
      return this.cache;
   }

   public long execute() {
      return this.output;
   }

   public long prepare() {
      return this.current;
   }

   public float check() {
      return this.active;
   }

   public float onTick() {
      return this.mode;
   }

   public int select() {
      return this.selection;
   }

   public float handle(long var1) {
      float var3 = (float)(var1 - this.output) / (float)this.current;
      return var3 < 0.0F ? 0.0F : Math.min(var3, 1.0F);
   }

   public boolean process(long var1) {
      return var1 - this.output >= this.current;
   }
}
