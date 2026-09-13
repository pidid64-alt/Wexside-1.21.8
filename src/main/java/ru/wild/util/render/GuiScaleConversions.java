package ru.wild.util.render;

import net.minecraft.client.MinecraftClient;

public class GuiScaleConversions {
   public static float instance = 2.0F;

   public static void handle(RoundedRectRenderer var0) {
      GuiScaleMetrics var1 = new GuiScaleMetrics(MinecraftClient.getInstance());
      float var2 = (float)(GuiScaleMetrics.update() / Math.pow(GuiScaleMetrics.update(), 2.0));
      var0.handle(var2 * instance, var2 * instance, var2 * instance);
   }

   public static void process(RoundedRectRenderer var0) {
      var0.handle(instance, instance, instance);
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
}
