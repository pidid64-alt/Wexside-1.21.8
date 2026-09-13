package ru.wild.util.render;

import net.minecraft.client.util.math.MatrixStack;
import ru.wild.core.MinecraftContext;

public class MatrixTransformStack implements MinecraftContext {
   public static float instance = 2.0F;

   public static void handle(MatrixStack var0) {
      var0.push();
      double var1 = toggleState.getWindow().getScaleFactor();
      double var3 = var1 / (var1 * var1);
      var0.scale((float)(var3 * instance), (float)(var3 * instance), 1.0F);
   }

   public static void process(MatrixStack var0) {
      var0.pop();
   }

   public static void handle(MatrixStack var0, float var1, float var2, float var3) {
      var0.push();
      var0.translate(var1, var2, 0.0F);
      var0.scale(var3, var3, 1.0F);
      var0.translate(-var1, -var2, 0.0F);
   }

   public static void compute(MatrixStack var0) {
      var0.pop();
   }

   public static int handle(int var0) {
      return (int)(var0 * toggleState.getWindow().getScaleFactor() / instance);
   }

   public static int handle(float var0) {
      return (int)(var0 * toggleState.getWindow().getScaleFactor() / instance);
   }

   public static float process(float var0) {
      return var0 * toggleState.getWindow().getScaleFactor() / instance;
   }

   public static float[] handle(float var0, float var1) {
      double var2 = toggleState.getWindow().getScaleFactor();
      var0 = (float)(var0 * var2 / instance);
      var1 = (float)(var1 * var2 / instance);
      return new float[]{var0, var1};
   }

   public static void process(MatrixStack var0, float var1, float var2, float var3) {
      var0.translate(var1, var2, 0.0F);
      var0.scale(var3, var3, 1.0F);
      var0.translate(-var1, -var2, 0.0F);
   }
}
