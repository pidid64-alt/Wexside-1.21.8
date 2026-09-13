package ru.wild.render;

import net.minecraft.util.math.Vec3d;

public final class AttackArcSegment {
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
   private final float selection;
   private final float enabled;
   private final float renderer;
   private final int handler;

   public AttackArcSegment(Vec3d var1, Vec3d var2, long var3, long var5, float var7, float var8, float var9, float var10, int var11) {
      this.instance = var1.x;
      this.data = var1.y;
      this.context = var1.z;
      double var12 = var2.x;
      double var14 = var2.y;
      double var16 = var2.z;
      double var18 = Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
      if (var18 < 1.0E-6) {
         var12 = 0.0;
         var14 = 0.0;
         var16 = 1.0;
      } else {
         var12 /= var18;
         var14 /= var18;
         var16 /= var18;
      }

      this.config = var12;
      this.state = var14;
      this.cache = var16;
      this.output = var3;
      this.current = Math.max(1L, var5);
      this.active = Math.max(0.1F, var7);
      this.mode = Math.max(0.1F, var8);
      this.selection = Math.max(0.05F, var9);
      this.enabled = var10;
      this.renderer = this.active * 1.8F + 0.5F;
      this.handler = var11 & 16777215;
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

   public float select() {
      return this.selection;
   }

   public float refresh() {
      return this.enabled;
   }

   public float render() {
      return this.renderer;
   }

   public int tick() {
      return this.handler;
   }

   public float handle(long var1) {
      float var3 = (float)(var1 - this.output) / (float)this.current;
      return var3 < 0.0F ? 0.0F : Math.min(var3, 1.0F);
   }

   public boolean process(long var1) {
      return var1 - this.output >= this.current;
   }
}
