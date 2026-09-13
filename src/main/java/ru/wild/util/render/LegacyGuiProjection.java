package ru.wild.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public class LegacyGuiProjection {
   private MinecraftClient data = MinecraftClient.getInstance();
   public static float instance = 2.0F;

   public static void handle() {
      GuiScaleMetrics var0 = new GuiScaleMetrics(MinecraftClient.getInstance());
      double var1 = GuiScaleMetrics.update() / Math.pow(GuiScaleMetrics.update(), 2.0);
      GL11.glPushMatrix();
      GL11.glScaled(var1 * instance, var1 * instance, var1 * instance);
   }

   public static void process() {
      GL11.glScaled(instance, instance, instance);
      GL11.glPopMatrix();
   }

   public static void handle(float var0, float var1, float var2) {
      MatrixStack var3 = new MatrixStack();
      var3.push();
      var3.translate(var0, var1, 0.0F);
      var3.scale(var2, var2, 1.0F);
      var3.translate(-var0, -var1, 0.0F);
   }

   public static void compute() {
      MatrixStack var0 = new MatrixStack();
      var0.pop();
   }

   public static int handle(int var0) {
      GuiScaleMetrics var1 = new GuiScaleMetrics(MinecraftClient.getInstance());
      return (int)(var0 * GuiScaleMetrics.update() / instance);
   }

   public static int handle(float var0) {
      GuiScaleMetrics var1 = new GuiScaleMetrics(MinecraftClient.getInstance());
      return (int)(var0 * GuiScaleMetrics.update() / instance);
   }

   public static float[] handle(float var0, float var1) {
      GuiScaleMetrics var2 = new GuiScaleMetrics(MinecraftClient.getInstance());
      var0 = var0 * GuiScaleMetrics.update() / instance;
      var1 = var1 * GuiScaleMetrics.update() / instance;
      return new float[]{var0, var1};
   }

   public static void process(float var0, float var1, float var2) {
      MatrixStack var3 = new MatrixStack();
      var3.translate(var0, var1, 0.0F);
      var3.scale(var2, var2, 1.0F);
      var3.translate(-var0, -var1, 0.0F);
   }
}
