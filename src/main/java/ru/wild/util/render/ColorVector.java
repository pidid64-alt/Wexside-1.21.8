package ru.wild.util.render;

import java.awt.Color;
import java.util.Objects;

public final class ColorVector {
   private final float instance;
   private final float data;
   private final float context;
   private final float config;

   private ColorVector(float var1, float var2, float var3, float var4) {
      this.instance = update(var1);
      this.data = apply(var2);
      this.context = apply(var3);
      this.config = apply(var4);
   }

   public static ColorVector handle(float var0, float var1, float var2, float var3) {
      return new ColorVector(var0, var1, var2, var3);
   }

   public static ColorVector handle(float var0, float var1, float var2) {
      return new ColorVector(var0, var1, var2, 1.0F);
   }

   public static ColorVector handle(int var0) {
      int var1 = var0 >>> 16 & 0xFF;
      int var2 = var0 >>> 8 & 0xFF;
      int var3 = var0 & 0xFF;
      int var4 = var0 >>> 24 & 0xFF;
      float[] var5 = Color.RGBtoHSB(var1, var2, var3, null);
      return new ColorVector(var5[0] * 360.0F, var5[1], var5[2], var4 / 255.0F);
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

   public ColorVector handle(float var1) {
      return new ColorVector(var1, this.data, this.context, this.config);
   }

   public ColorVector process(float var1) {
      return new ColorVector(this.instance, var1, this.context, this.config);
   }

   public ColorVector compute(float var1) {
      return new ColorVector(this.instance, this.data, var1, this.config);
   }

   public ColorVector resolve(float var1) {
      return new ColorVector(this.instance, this.data, this.context, var1);
   }

   public ColorVector update() {
      return this;
   }

   public int apply() {
      float var1 = this.instance / 360.0F;
      Color var2 = Color.getHSBColor(var1, this.data, this.context);
      int var3 = var2.getRed();
      int var4 = var2.getGreen();
      int var5 = var2.getBlue();
      int var6 = Math.round(this.config * 255.0F);
      return var6 << 24 | var3 << 16 | var4 << 8 | var5;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ColorVector var2 = (ColorVector)var1;
         return Float.compare(var2.instance, this.instance) == 0
            && Float.compare(var2.data, this.data) == 0
            && Float.compare(var2.context, this.context) == 0
            && Float.compare(var2.config, this.config) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.instance, this.data, this.context, this.config);
   }

   private static float update(float var0) {
      if (!Float.isFinite(var0)) {
         return 0.0F;
      }

      float var1 = var0 % 360.0F;
      if (var1 < 0.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private static float apply(float var0) {
      return !(var0 <= 0.0F) && !Float.isNaN(var0) ? Math.min(var0, 1.0F) : 0.0F;
   }
}
