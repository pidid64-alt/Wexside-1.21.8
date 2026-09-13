package ru.wild.util.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public final class FarmAreaRenderer {
   private FarmAreaRenderer() {
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      float var9 = (var8 >> 24 & 0xFF) / 255.0F;
      float var10 = (var8 >> 16 & 0xFF) / 255.0F;
      float var11 = (var8 >> 8 & 0xFF) / 255.0F;
      float var12 = (var8 & 0xFF) / 255.0F;
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
   }

   public static void process(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      float var9 = (var8 >> 24 & 0xFF) / 255.0F;
      float var10 = (var8 >> 16 & 0xFF) / 255.0F;
      float var11 = (var8 >> 8 & 0xFF) / 255.0F;
      float var12 = (var8 & 0xFF) / 255.0F;
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var9);
   }

   public static void handle(
      VertexConsumer var0, VertexConsumer var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10
   ) {
      compute(var0, var2, var3, var4, var5, var6, var7, var8, var9);
      process(var1, var2, var3, var4, var5, var6, var7, var8, var10);
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      float var9 = (var7 >> 24 & 0xFF) / 255.0F;
      float var10 = (var7 >> 16 & 0xFF) / 255.0F;
      float var11 = (var7 >> 8 & 0xFF) / 255.0F;
      float var12 = (var7 & 0xFF) / 255.0F;
      float var13 = (float)((Math.PI * 2) / var8);

      for (int var14 = 0; var14 < var8; var14++) {
         float var15 = var14 * var13;
         float var16 = (var14 + 1) * var13;
         float var17 = var2 + MathHelper.sin(var15) * var5;
         float var18 = var4 + MathHelper.cos(var15) * var5;
         float var19 = var2 + MathHelper.sin(var16) * var5;
         float var20 = var4 + MathHelper.cos(var16) * var5;
         var0.vertex(var1, var17, var3, var18).color(var10, var11, var12, var9);
         var0.vertex(var1, var19, var3, var20).color(var10, var11, var12, var9);
         var0.vertex(var1, var17, var3 + var6, var18).color(var10, var11, var12, var9);
         var0.vertex(var1, var19, var3 + var6, var20).color(var10, var11, var12, var9);
         if (var14 % (var8 / 8) == 0) {
            var0.vertex(var1, var17, var3, var18).color(var10, var11, var12, var9);
            var0.vertex(var1, var17, var3 + var6, var18).color(var10, var11, var12, var9);
         }
      }
   }

   public static void compute(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      float var9 = (var8 >> 24 & 0xFF) / 255.0F;
      float var10 = (var8 >> 16 & 0xFF) / 255.0F;
      float var11 = (var8 >> 8 & 0xFF) / 255.0F;
      float var12 = (var8 & 0xFF) / 255.0F;
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var9);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var9);
   }

   public static void process(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      float var9 = (var7 >> 24 & 0xFF) / 255.0F;
      float var10 = (var7 >> 16 & 0xFF) / 255.0F;
      float var11 = (var7 >> 8 & 0xFF) / 255.0F;
      float var12 = (var7 & 0xFF) / 255.0F;
      float var13 = (float)((Math.PI * 2) / var8);

      for (int var14 = 0; var14 < var8; var14++) {
         float var15 = var14 * var13;
         float var16 = (var14 + 1) * var13;
         float var17 = var2 + MathHelper.sin(var15) * var5;
         float var18 = var4 + MathHelper.cos(var15) * var5;
         float var19 = var2 + MathHelper.sin(var16) * var5;
         float var20 = var4 + MathHelper.cos(var16) * var5;
         var0.vertex(var1, var17, var3, var18).color(var10, var11, var12, var9);
         var0.vertex(var1, var17, var3 + var6, var18).color(var10, var11, var12, var9);
         var0.vertex(var1, var19, var3 + var6, var20).color(var10, var11, var12, var9);
         var0.vertex(var1, var19, var3, var20).color(var10, var11, var12, var9);
         var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
         var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var9);
         var0.vertex(var1, var17, var3, var18).color(var10, var11, var12, var9);
         var0.vertex(var1, var19, var3, var20).color(var10, var11, var12, var9);
         var0.vertex(var1, var2, var3 + var6, var4).color(var10, var11, var12, var9);
         var0.vertex(var1, var2, var3 + var6, var4).color(var10, var11, var12, var9);
         var0.vertex(var1, var19, var3 + var6, var20).color(var10, var11, var12, var9);
         var0.vertex(var1, var17, var3 + var6, var18).color(var10, var11, var12, var9);
      }
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      float var10 = (var8 >> 24 & 0xFF) / 255.0F;
      float var11 = (var8 >> 16 & 0xFF) / 255.0F;
      float var12 = (var8 >> 8 & 0xFF) / 255.0F;
      float var13 = (var8 & 0xFF) / 255.0F;
      float var14 = (var9 >> 24 & 0xFF) / 255.0F;
      float var15 = (var9 >> 16 & 0xFF) / 255.0F;
      float var16 = (var9 >> 8 & 0xFF) / 255.0F;
      float var17 = (var9 & 0xFF) / 255.0F;
      var0.vertex(var1, var2, var3, var4).color(var11, var12, var13, var10);
      var0.vertex(var1, var5, var3, var4).color(var11, var12, var13, var10);
      var0.vertex(var1, var5, var3, var7).color(var11, var12, var13, var10);
      var0.vertex(var1, var2, var3, var7).color(var11, var12, var13, var10);
      var0.vertex(var1, var2, var6, var4).color(var15, var16, var17, var14);
      var0.vertex(var1, var2, var6, var7).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var6, var7).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var6, var4).color(var15, var16, var17, var14);
      var0.vertex(var1, var2, var3, var4).color(var11, var12, var13, var10);
      var0.vertex(var1, var2, var6, var4).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var6, var4).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var3, var4).color(var11, var12, var13, var10);
      var0.vertex(var1, var2, var3, var7).color(var11, var12, var13, var10);
      var0.vertex(var1, var5, var3, var7).color(var11, var12, var13, var10);
      var0.vertex(var1, var5, var6, var7).color(var15, var16, var17, var14);
      var0.vertex(var1, var2, var6, var7).color(var15, var16, var17, var14);
      var0.vertex(var1, var2, var3, var4).color(var11, var12, var13, var10);
      var0.vertex(var1, var2, var3, var7).color(var11, var12, var13, var10);
      var0.vertex(var1, var2, var6, var7).color(var15, var16, var17, var14);
      var0.vertex(var1, var2, var6, var4).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var3, var4).color(var11, var12, var13, var10);
      var0.vertex(var1, var5, var6, var4).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var6, var7).color(var15, var16, var17, var14);
      var0.vertex(var1, var5, var3, var7).color(var11, var12, var13, var10);
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, int var9) {
      float var10 = (var7 >> 24 & 0xFF) / 255.0F;
      float var11 = (var7 >> 16 & 0xFF) / 255.0F;
      float var12 = (var7 >> 8 & 0xFF) / 255.0F;
      float var13 = (var7 & 0xFF) / 255.0F;
      float var14 = (var8 >> 24 & 0xFF) / 255.0F;
      float var15 = (var8 >> 16 & 0xFF) / 255.0F;
      float var16 = (var8 >> 8 & 0xFF) / 255.0F;
      float var17 = (var8 & 0xFF) / 255.0F;
      float var18 = (float)((Math.PI * 2) / var9);

      for (int var19 = 0; var19 < var9; var19++) {
         float var20 = var19 * var18;
         float var21 = (var19 + 1) * var18;
         float var22 = var2 + MathHelper.sin(var20) * var5;
         float var23 = var4 + MathHelper.cos(var20) * var5;
         float var24 = var2 + MathHelper.sin(var21) * var5;
         float var25 = var4 + MathHelper.cos(var21) * var5;
         var0.vertex(var1, var22, var3, var23).color(var11, var12, var13, var10);
         var0.vertex(var1, var22, var3 + var6, var23).color(var15, var16, var17, var14);
         var0.vertex(var1, var24, var3 + var6, var25).color(var15, var16, var17, var14);
         var0.vertex(var1, var24, var3, var25).color(var11, var12, var13, var10);
         var0.vertex(var1, var2, var3, var4).color(var11, var12, var13, var10);
         var0.vertex(var1, var2, var3, var4).color(var11, var12, var13, var10);
         var0.vertex(var1, var22, var3, var23).color(var11, var12, var13, var10);
         var0.vertex(var1, var24, var3, var25).color(var11, var12, var13, var10);
         var0.vertex(var1, var2, var3 + var6, var4).color(var15, var16, var17, var14);
         var0.vertex(var1, var2, var3 + var6, var4).color(var15, var16, var17, var14);
         var0.vertex(var1, var24, var3 + var6, var25).color(var15, var16, var17, var14);
         var0.vertex(var1, var22, var3 + var6, var23).color(var15, var16, var17, var14);
      }
   }

   private static void handle(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      var0.vertex(var1, var2, var3, var4).color(var8, var9, var10, var11);
      var0.vertex(var1, var2, var6, var7).color(var8, var9, var10, var11);
      var0.vertex(var1, var5, var6, var7).color(var8, var9, var10, var11);
      var0.vertex(var1, var5, var3, var4).color(var8, var9, var10, var11);
   }
}
