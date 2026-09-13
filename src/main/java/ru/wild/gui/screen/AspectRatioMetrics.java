package ru.wild.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class AspectRatioMetrics {
   private final double instance;
   private final double data;
   private int context;
   private int config;
   private static int state;

   public AspectRatioMetrics(MinecraftClient var1) {
      this.context = var1.getWindow().getWidth();
      this.config = var1.getWindow().getHeight();
      state = 1;
      short var2 = 2;
      if (var2 == 0) {
         var2 = 1000;
      }

      while (state < var2 && this.context / (state + 1) >= 320 && this.config / (state + 1) >= 240) {
         state++;
      }

      this.instance = (double)this.context / state;
      this.data = (double)this.config / state;
      this.context = MathHelper.ceil(this.instance);
      this.config = MathHelper.ceil(this.data);
   }

   public int handle() {
      return this.context;
   }

   public int process() {
      return this.config;
   }

   public int compute() {
      return this.context;
   }

   public int resolve() {
      return this.config;
   }

   public double update() {
      return this.instance;
   }

   public double apply() {
      return this.data;
   }

   public static int execute() {
      return state;
   }
}
