package ru.wild.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;
import ru.wild.util.math.NumericTransform;

public class RenderRuntimeContext {
   private static final float context = 0.35F;
   public static MinecraftClient instance = MinecraftClient.getInstance();
   private static GuiScaleMetrics config;
   private static Window state;
   private float cache;
   private float output;
   private float current;
   private float active = 8.0F;
   private boolean mode;
   float data;

   public static GuiScaleMetrics handle() {
      if (config == null) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null && var0.getWindow() != null) {
            config = new GuiScaleMetrics(var0);
         }
      }

      return config;
   }

   public static Window process() {
      if (state == null) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null) {
            state = var0.getWindow();
         }
      }

      return state;
   }

   public RenderRuntimeContext() {
      this.handle(true);
   }

   public void compute() {
      this.output = this.handle(this.output, this.cache, NumericTransform.apply((double)(this.active / 100.0F)));
      if (Math.abs(this.cache - this.output) <= 0.35F) {
         this.output = this.cache;
      }
   }

   public void handle(double var1) {
      if (this.mode) {
         float var3 = (float)var1 * (this.active * 10.0F);
         float var4 = 0.0F;
         this.cache = Math.min(Math.max(this.cache + var3 / 2.0F, this.current - var4), var4);
      }
   }

   public <T extends Number> T handle(T var1, T var2, double var3) {
      double var5 = var1.doubleValue();
      double var7 = var2.doubleValue();
      double var9 = var5 + var3 * (var7 - var5);
      if (var1 instanceof Integer) {
         return (T)Integer.valueOf((int)Math.round(var9));
      } else if (var1 instanceof Double) {
         return (T)Double.valueOf(var9);
      } else if (var1 instanceof Float) {
         return (T)Float.valueOf((float)var9);
      } else if (var1 instanceof Long) {
         return (T)Long.valueOf(Math.round(var9));
      } else if (var1 instanceof Short) {
         return (T)Short.valueOf((short)Math.round(var9));
      } else if (var1 instanceof Byte) {
         return (T)Byte.valueOf((byte)Math.round(var9));
      } else {
         throw new IllegalArgumentException("Unsupported type: " + var1.getClass().getSimpleName());
      }
   }

   public static void resolve() {
      GL11.glEnable(3089);
   }

   public static void update() {
      GL11.glDisable(3089);
   }

   public static void handle(Window var0, double var1, double var3, double var5, double var7) {
      if (var1 + var5 != var1 && var3 + var7 != var3 && !(var1 < 0.0) && !(var3 + var7 < 0.0)) {
         double var9 = var0.getScaleFactor();
         GL11.glScissor(
            (int)Math.round(var1 * var9),
            (int)Math.round((var0.getScaledHeight() - (var3 + var7)) * var9),
            (int)Math.round(var5 * var9),
            (int)Math.round(var7 * var9)
         );
      }
   }

   public void apply() {
      this.output = 0.0F;
      this.cache = 0.0F;
   }

   public void handle(float var1, float var2) {
      this.current = -var1 + var2;
   }

   public void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6) {
      if (!(this.check() >= 0.0F)) {
         float var7 = this.check() != 0.0F ? this.prepare() / this.check() : 0.0F;
         float var8 = var5 - this.check() / (this.check() - var5) * var5;
         this.data = NumericTransform.handle(var8, this.data, NumericTransform.apply(0.9F));
         boolean var9 = this.data < var5 && this.data > 0.0F;
         if (var9) {
            float var10 = var2;
            float var11 = var3 + var5 * var7 - this.data * var7;
            int var12 = RoundedRectRenderer.ColorState.select(
               RoundedRectRenderer.ColorState.apply(1, 1), (int)NumericTransform.onTick(255.0F * var6, 0.0F, 255.0F)
            );
            int var13 = RoundedRectRenderer.ColorState.select(
               RoundedRectRenderer.ColorState.apply(1, 1), (int)NumericTransform.onTick(20.0F * var6, 0.0F, 20.0F)
            );
            var1.handle(var2, var3, var4, var5, var13);
            var1.handle(var10, var11, var4, this.data, 1.0F, var12);
         }
      }
   }

   public float execute() {
      return this.cache;
   }

   public void handle(float var1) {
      this.cache = var1;
   }

   public float prepare() {
      return Math.abs(this.cache - this.output) <= 0.35F ? Math.round(this.output) : this.output;
   }

   public void process(float var1) {
      this.output = var1;
   }

   public float check() {
      return this.current;
   }

   public void compute(float var1) {
      this.current = var1;
   }

   public float onTick() {
      return this.active;
   }

   public void resolve(float var1) {
      this.active = var1;
   }

   public boolean select() {
      return this.mode;
   }

   public void handle(boolean var1) {
      this.mode = var1;
   }
}
