package ru.wild.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.math.MathHelper;

public class GuiScaleMetrics {
   private final double instance;
   private final double data;
   private int context;
   private int config;
   private static int state;

   public GuiScaleMetrics(MinecraftClient var1) {
      if (var1 != null && var1.getWindow() != null) {
         this.context = var1.getWindow().getWidth();
         this.config = var1.getWindow().getHeight();
         state = 1;
         boolean var2 = false;

         try {
            SimpleOption var3 = var1.options.getForceUnicodeFont();
            var2 = var3 != null && Boolean.TRUE.equals(var3.getValue());
         } catch (Exception var4) {
         }

         byte var5 = 2;

         while (state < var5 && this.context / (state + 1) >= 320 && this.config / (state + 1) >= 240) {
            state++;
         }

         if (var2 && state % 2 != 0 && state != 1) {
            state--;
         }

         this.instance = (double)this.context / state;
         this.data = (double)this.config / state;
         this.context = MathHelper.ceil(this.instance);
         this.config = MathHelper.ceil(this.data);
      } else {
         this.context = 1920;
         this.config = 1080;
         state = 1;
         this.instance = this.context;
         this.data = this.config;
      }
   }

   public int handle() {
      return this.context;
   }

   public int process() {
      return this.config;
   }

   public double compute() {
      return this.instance;
   }

   public double resolve() {
      return this.data;
   }

   public static int update() {
      return state;
   }
}
