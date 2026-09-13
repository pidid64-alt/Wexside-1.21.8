package ru.wild.render;

import ru.wild.render.shader.ShaderRenderer;

public final class RoundedGeometryBatch {
   private final ShaderRenderer instance;

   public RoundedGeometryBatch(ShaderRenderer var1) {
      this.instance = var1;
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float[] var10) {
      this.instance.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10, float[] var11) {
      this.instance.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void handle(
      float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10, int var11, int var12, float[] var13
   ) {
      this.instance.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      float var13,
      boolean var14,
      int var15,
      float[] var16
   ) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      int var11,
      int var12,
      float var13,
      float var14,
      float var15,
      float var16,
      boolean var17,
      int var18,
      float[] var19
   ) {
      this.instance.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6, float[] var7) {
      this.instance.handle(var1, var2, var3, var4, var5, var6, var7);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, int var7, float[] var8) {
      this.instance.handle(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void handle() {
      this.instance.process();
   }
}
