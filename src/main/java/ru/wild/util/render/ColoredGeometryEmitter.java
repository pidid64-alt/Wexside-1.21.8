package ru.wild.util.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class ColoredGeometryEmitter {
   private ColoredGeometryEmitter() {
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, int var2, int var3, float var4) {
      float var5 = var4 / 2.0F;
      int var6 = var2 >> 16 & 0xFF;
      int var7 = var2 >> 8 & 0xFF;
      int var8 = var2 & 0xFF;
      var0.vertex(var1, -var5, -var5, 0.0F)
         .color(var6, var7, var8, var3)
         .texture(0.0F, 1.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
      var0.vertex(var1, var5, -var5, 0.0F)
         .color(var6, var7, var8, var3)
         .texture(1.0F, 1.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
      var0.vertex(var1, var5, var5, 0.0F)
         .color(var6, var7, var8, var3)
         .texture(1.0F, 0.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
      var0.vertex(var1, -var5, var5, 0.0F)
         .color(var6, var7, var8, var3)
         .texture(0.0F, 0.0F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 0.0F, 1.0F);
   }

   public static void handle(
      VertexConsumer var0, Matrix4f var1, double var2, double var4, double var6, double var8, double var10, double var12, int[] var14, int var15
   ) {
      int[] var16 = new int[4];
      int[][] var17 = new int[4][4];

      for (int var18 = 0; var18 < 4; var18++) {
         var16[var18] = PackedColor.update(var14[var18], var15);
         var17[var18][0] = var16[var18] >> 16 & 0xFF;
         var17[var18][1] = var16[var18] >> 8 & 0xFF;
         var17[var18][2] = var16[var18] & 0xFF;
         var17[var18][3] = var16[var18] >> 24 & 0xFF;
      }

      var0.vertex(var1, (float)var2, (float)var4, (float)var6).color(var17[0][0], var17[0][1], var17[0][2], var17[0][3]);
      var0.vertex(var1, (float)var8, (float)var4, (float)var6).color(var17[1][0], var17[1][1], var17[1][2], var17[1][3]);
      var0.vertex(var1, (float)var8, (float)var4, (float)var12).color(var17[2][0], var17[2][1], var17[2][2], var17[2][3]);
      var0.vertex(var1, (float)var2, (float)var4, (float)var12).color(var17[3][0], var17[3][1], var17[3][2], var17[3][3]);
      var0.vertex(var1, (float)var2, (float)var10, (float)var6).color(var17[0][0], var17[0][1], var17[0][2], var17[0][3]);
      var0.vertex(var1, (float)var2, (float)var10, (float)var12).color(var17[3][0], var17[3][1], var17[3][2], var17[3][3]);
      var0.vertex(var1, (float)var8, (float)var10, (float)var12).color(var17[2][0], var17[2][1], var17[2][2], var17[2][3]);
      var0.vertex(var1, (float)var8, (float)var10, (float)var6).color(var17[1][0], var17[1][1], var17[1][2], var17[1][3]);
      var0.vertex(var1, (float)var2, (float)var4, (float)var12).color(var17[3][0], var17[3][1], var17[3][2], var17[3][3]);
      var0.vertex(var1, (float)var8, (float)var4, (float)var12).color(var17[2][0], var17[2][1], var17[2][2], var17[2][3]);
      var0.vertex(var1, (float)var8, (float)var10, (float)var12).color(var17[2][0], var17[2][1], var17[2][2], var17[2][3]);
      var0.vertex(var1, (float)var2, (float)var10, (float)var12).color(var17[3][0], var17[3][1], var17[3][2], var17[3][3]);
      var0.vertex(var1, (float)var8, (float)var4, (float)var6).color(var17[1][0], var17[1][1], var17[1][2], var17[1][3]);
      var0.vertex(var1, (float)var2, (float)var4, (float)var6).color(var17[0][0], var17[0][1], var17[0][2], var17[0][3]);
      var0.vertex(var1, (float)var2, (float)var10, (float)var6).color(var17[0][0], var17[0][1], var17[0][2], var17[0][3]);
      var0.vertex(var1, (float)var8, (float)var10, (float)var6).color(var17[1][0], var17[1][1], var17[1][2], var17[1][3]);
      var0.vertex(var1, (float)var2, (float)var4, (float)var6).color(var17[0][0], var17[0][1], var17[0][2], var17[0][3]);
      var0.vertex(var1, (float)var2, (float)var4, (float)var12).color(var17[3][0], var17[3][1], var17[3][2], var17[3][3]);
      var0.vertex(var1, (float)var2, (float)var10, (float)var12).color(var17[3][0], var17[3][1], var17[3][2], var17[3][3]);
      var0.vertex(var1, (float)var2, (float)var10, (float)var6).color(var17[0][0], var17[0][1], var17[0][2], var17[0][3]);
      var0.vertex(var1, (float)var8, (float)var4, (float)var12).color(var17[2][0], var17[2][1], var17[2][2], var17[2][3]);
      var0.vertex(var1, (float)var8, (float)var4, (float)var6).color(var17[1][0], var17[1][1], var17[1][2], var17[1][3]);
      var0.vertex(var1, (float)var8, (float)var10, (float)var6).color(var17[1][0], var17[1][1], var17[1][2], var17[1][3]);
      var0.vertex(var1, (float)var8, (float)var10, (float)var12).color(var17[2][0], var17[2][1], var17[2][2], var17[2][3]);
   }

   public static void handle(
      VertexConsumer var0,
      Matrix4f var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      int[] var14,
      int var15,
      double var16,
      double var18
   ) {
      int[] var20 = new int[4];

      for (int var21 = 0; var21 < 4; var21++) {
         var20[var21] = PackedColor.update(var14[var21], var15);
      }

      handle(var0, var1, var2, var4, var6, var8, var4, var6, var20[0], var20[1], var16, var18);
      handle(var0, var1, var8, var4, var6, var8, var4, var12, var20[1], var20[2], var16, var18);
      handle(var0, var1, var8, var4, var12, var2, var4, var12, var20[2], var20[3], var16, var18);
      handle(var0, var1, var2, var4, var12, var2, var4, var6, var20[3], var20[0], var16, var18);
      handle(var0, var1, var2, var10, var6, var8, var10, var6, var20[0], var20[1], var16, var18);
      handle(var0, var1, var8, var10, var6, var8, var10, var12, var20[1], var20[2], var16, var18);
      handle(var0, var1, var8, var10, var12, var2, var10, var12, var20[2], var20[3], var16, var18);
      handle(var0, var1, var2, var10, var12, var2, var10, var6, var20[3], var20[0], var16, var18);
      handle(var0, var1, var2, var4, var6, var2, var10, var6, var20[0], var20[0], var16, var18);
      handle(var0, var1, var8, var4, var6, var8, var10, var6, var20[1], var20[1], var16, var18);
      handle(var0, var1, var8, var4, var12, var8, var10, var12, var20[2], var20[2], var16, var18);
      handle(var0, var1, var2, var4, var12, var2, var10, var12, var20[3], var20[3], var16, var18);
   }

   public static void handle(
      VertexConsumer var0,
      Matrix4f var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      int var14,
      int var15,
      double var16,
      double var18
   ) {
      double var20 = var8 - var2;
      double var22 = var10 - var4;
      double var24 = var12 - var6;
      double var26 = Math.sqrt(var20 * var20 + var22 * var22 + var24 * var24);
      if (!(var26 < 0.001)) {
         double var28 = var20 / var26;
         double var30 = var22 / var26;
         double var32 = var24 / var26;
         double var34 = var16 + var18;

         for (double var36 = 0.0; var36 < var26; var36 += var34) {
            double var38 = var36;
            double var40 = Math.min(var36 + var16, var26);
            if (var40 > var38) {
               double var42 = var2 + var28 * var38;
               double var44 = var4 + var30 * var38;
               double var46 = var6 + var32 * var38;
               double var48 = var2 + var28 * var40;
               double var50 = var4 + var30 * var40;
               double var52 = var6 + var32 * var40;
               double var54 = var38 / var26;
               int var56 = PackedColor.compute(var14, var15, (float)var54);
               var0.vertex(var1, (float)var42, (float)var44, (float)var46).color(var56 >> 16 & 0xFF, var56 >> 8 & 0xFF, var56 & 0xFF, var56 >>> 24 & 0xFF);
               var54 = var40 / var26;
               var56 = PackedColor.compute(var14, var15, (float)var54);
               var0.vertex(var1, (float)var48, (float)var50, (float)var52).color(var56 >> 16 & 0xFF, var56 >> 8 & 0xFF, var56 & 0xFF, var56 >>> 24 & 0xFF);
            }
         }
      }
   }
}
