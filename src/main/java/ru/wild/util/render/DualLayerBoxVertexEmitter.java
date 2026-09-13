package ru.wild.util.render;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public class DualLayerBoxVertexEmitter {
   private DualLayerBoxVertexEmitter() {
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      int var9 = var8 >> 16 & 0xFF;
      int var10 = var8 >> 8 & 0xFF;
      int var11 = var8 & 0xFF;
      int var12 = var8 >> 24 & 0xFF;
      var0.vertex(var1, var2, var6, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var6, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var6, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var6, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var3, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var3, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var3, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var3, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var3, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var6, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var6, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var3, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var3, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var6, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var6, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var3, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var3, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var3, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var6, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var2, var6, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var3, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var6, var4).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var6, var7).color(var9, var10, var11, var12);
      var0.vertex(var1, var5, var3, var7).color(var9, var10, var11, var12);
   }

   public static void handle(
      VertexConsumer var0,
      VertexConsumer var1,
      Matrix4f var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      int[] var15,
      int var16,
      int var17,
      double var18,
      double var20
   ) {
      ColoredGeometryEmitter.handle(var0, var2, var3, var5, var7, var9, var11, var13, var15, var16);
      ColoredGeometryEmitter.handle(var1, var2, var3, var5, var7, var9, var11, var13, var15, var17, var18, var20);
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      int var10 = var8 >> 16 & 0xFF;
      int var11 = var8 >> 8 & 0xFF;
      int var12 = var8 & 0xFF;
      int var13 = var8 >> 24 & 0xFF;
      int var14 = var9 >> 16 & 0xFF;
      int var15 = var9 >> 8 & 0xFF;
      int var16 = var9 & 0xFF;
      int var17 = var9 >> 24 & 0xFF;
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var3, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var6, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var6, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var3, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var3, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var3, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var6, var7).color(var10, var11, var12, var13);
      var0.vertex(var1, var5, var6, var4).color(var10, var11, var12, var13);
      var0.vertex(var1, var2, var6, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var6, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var6, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var6, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var3, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var3, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var3, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var3, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var3, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var6, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var6, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var3, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var3, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var6, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var6, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var3, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var3, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var3, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var6, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var2, var6, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var3, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var6, var4).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var6, var7).color(var14, var15, var16, var17);
      var0.vertex(var1, var5, var3, var7).color(var14, var15, var16, var17);
   }

   public static void handle(VertexConsumer var0, Matrix4f var1, int var2, float var3) {
      float var4 = var3 / 2.0F;
      int var5 = var2 >> 16 & 0xFF;
      int var6 = var2 >> 8 & 0xFF;
      int var7 = var2 & 0xFF;
      int var8 = var2 >> 24 & 0xFF;
      var0.vertex(var1, -var4, var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, -var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, -var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, -var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, -var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, -var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, -var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, -var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, -var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, -var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, -var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, -var4, var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, var4, -var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, -var4, var4).color(var5, var6, var7, var8);
      var0.vertex(var1, var4, -var4, -var4).color(var5, var6, var7, var8);
   }

   public static void process(VertexConsumer var0, Matrix4f var1, int var2, float var3) {
      float var4 = var3 / 2.0F;
      int var5 = var2 >> 16 & 0xFF;
      int var6 = var2 >> 8 & 0xFF;
      int var7 = var2 & 0xFF;
      int var8 = var2 >> 24 & 0xFF;
      handle(var0, var1, -var4, -var4, -var4, var4, -var4, -var4, var5, var6, var7, var8);
      handle(var0, var1, var4, -var4, -var4, var4, -var4, var4, var5, var6, var7, var8);
      handle(var0, var1, var4, -var4, var4, -var4, -var4, var4, var5, var6, var7, var8);
      handle(var0, var1, -var4, -var4, var4, -var4, -var4, -var4, var5, var6, var7, var8);
      handle(var0, var1, -var4, var4, -var4, var4, var4, -var4, var5, var6, var7, var8);
      handle(var0, var1, var4, var4, -var4, var4, var4, var4, var5, var6, var7, var8);
      handle(var0, var1, var4, var4, var4, -var4, var4, var4, var5, var6, var7, var8);
      handle(var0, var1, -var4, var4, var4, -var4, var4, -var4, var5, var6, var7, var8);
      handle(var0, var1, -var4, -var4, -var4, -var4, var4, -var4, var5, var6, var7, var8);
      handle(var0, var1, var4, -var4, -var4, var4, var4, -var4, var5, var6, var7, var8);
      handle(var0, var1, var4, -var4, var4, var4, var4, var4, var5, var6, var7, var8);
      handle(var0, var1, -var4, -var4, var4, -var4, var4, var4, var5, var6, var7, var8);
   }

   private static void handle(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, int var10, int var11
   ) {
      var0.vertex(var1, var2, var3, var4).color(var8, var9, var10, var11);
      var0.vertex(var1, var5, var6, var7).color(var8, var9, var10, var11);
   }
}
