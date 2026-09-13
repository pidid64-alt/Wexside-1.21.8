package ru.wild.util.render;

import java.awt.Color;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public final class PackedColor {
   private static final long output = 60000L;
   private static final ConcurrentHashMap<PackedColor.PrimaryState, PackedColor.State> current = new ConcurrentHashMap<>();
   private static final ScheduledExecutorService active = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "ColorUtil-CacheCleaner");
      var1.setDaemon(true);
      return var1;
   });
   private static final DelayQueue<PackedColor.State> mode = new DelayQueue<>();
   private static final double[] selection = compute();
   private static final ThreadLocal<float[]> enabled = ThreadLocal.withInitial(() -> new float[3]);
   public static final int instance = process(255, 0, 0);
   public static final int data = process(0, 255, 0);
   public static final int context = process(0, 0, 255);
   public static final int config = process(255, 255, 0);
   public static final int state = submit(255);
   public static final int cache = submit(0);

   public static int handle(int var0, int var1, float var2) {
      return compute(var0, var1, var2);
   }

   public static int handle(int var0, int var1) {
      return MathHelper.clamp(var1, 0, 255) << 24 | var0 & 16777215;
   }

   public static int handle(int var0, int var1, double var2) {
      return handle((double)var0, (double)var1, (double)((float)var2)).intValue();
   }

   public static Double handle(double var0, double var2, double var4) {
      return var0 + (var2 - var0) * var4;
   }

   public static int handle(int var0) {
      return var0 >>> 24;
   }

   public static int process(int var0) {
      return var0 >> 16 & 0xFF;
   }

   public static int compute(int var0) {
      return var0 >> 8 & 0xFF;
   }

   public static int resolve(int var0) {
      return var0 & 0xFF;
   }

   public static int process(int var0, int var1) {
      int var2 = process(var0);
      int var3 = compute(var0);
      int var4 = resolve(var0);
      var2 = Math.max(0, var2 - var1);
      var3 = Math.max(0, var3 - var1);
      var4 = Math.max(0, var4 - var1);
      return 0xFF000000 | var2 << 16 | var3 << 8 | var4;
   }

   public static int update(int var0) {
      float var1 = (var0 >> 24 & 0xFF) / 255.0F;
      float var2 = (var0 >> 16 & 0xFF) / 255.0F;
      float var3 = (var0 >> 8 & 0xFF) / 255.0F;
      float var4 = (var0 & 0xFF) / 255.0F;
      GL11.glColor4f(var2, var3, var4, var1);
      return var0;
   }

   public static int compute(int var0, int var1) {
      double var2 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      double var4;
      return Color.getHSBColor((var4 = var2 % 360.0) / 360.0 < 0.5 ? -((float)(var4 / 360.0)) : (float)(var4 / 360.0), 0.5F, 1.0F).hashCode();
   }

   public static int handle(float var0, int var1, int var2, int var3) {
      long var4 = System.currentTimeMillis() + var1;
      double var6 = (Math.sin(var4 * 0.001 * var0) + 1.0) / 2.0;
      return resolve(var2, var3, (float)var6);
   }

   public static int process(int var0, int var1, float var2) {
      return compute(var0, var1, var2);
   }

   public static int process(int var0, int var1, double var2) {
      return compute(var0, var1, (float)var2);
   }

   public static int compute(int var0, int var1, float var2) {
      float var3 = handle(var2);
      if (var3 <= 0.0F) {
         return var0;
      }

      if (var3 >= 1.0F) {
         return var1;
      }

      int var4 = var0 >>> 24 & 0xFF;
      int var5 = var1 >>> 24 & 0xFF;
      int var6 = Math.round(var4 + (var5 - var4) * var3);
      return handle(var0, var1, var3, var6);
   }

   public static int compute(int var0, int var1, double var2) {
      return resolve(var0, var1, (float)var2);
   }

   public static int resolve(int var0, int var1, float var2) {
      float var3 = handle(var2);
      if (var3 <= 0.0F) {
         return var0 & 16777215;
      } else {
         return var3 >= 1.0F ? var1 & 16777215 : handle(var0, var1, var3, 0) & 16777215;
      }
   }

   private static int handle(int var0, int var1, float var2, int var3) {
      double var4 = selection[var0 >>> 16 & 0xFF];
      double var6 = selection[var0 >>> 8 & 0xFF];
      double var8 = selection[var0 & 0xFF];
      double var10 = selection[var1 >>> 16 & 0xFF];
      double var12 = selection[var1 >>> 8 & 0xFF];
      double var14 = selection[var1 & 0xFF];
      double var16 = 0.4122214708 * var4 + 0.5363325363 * var6 + 0.0514459929 * var8;
      double var18 = 0.2119034982 * var4 + 0.6806995451 * var6 + 0.1073969566 * var8;
      double var20 = 0.0883024619 * var4 + 0.2817188376 * var6 + 0.6299787005 * var8;
      double var22 = Math.cbrt(var16);
      double var24 = Math.cbrt(var18);
      double var26 = Math.cbrt(var20);
      double var28 = 0.2104542553 * var22 + 0.793617785 * var24 - 0.0040720468 * var26;
      double var30 = 1.9779984951 * var22 - 2.428592205 * var24 + 0.4505937099 * var26;
      double var32 = 0.0259040371 * var22 + 0.7827717662 * var24 - 0.808675766 * var26;
      double var34 = 0.4122214708 * var10 + 0.5363325363 * var12 + 0.0514459929 * var14;
      double var36 = 0.2119034982 * var10 + 0.6806995451 * var12 + 0.1073969566 * var14;
      double var38 = 0.0883024619 * var10 + 0.2817188376 * var12 + 0.6299787005 * var14;
      double var40 = Math.cbrt(var34);
      double var42 = Math.cbrt(var36);
      double var44 = Math.cbrt(var38);
      double var46 = 0.2104542553 * var40 + 0.793617785 * var42 - 0.0040720468 * var44;
      double var48 = 1.9779984951 * var40 - 2.428592205 * var42 + 0.4505937099 * var44;
      double var50 = 0.0259040371 * var40 + 0.7827717662 * var42 - 0.808675766 * var44;
      double var52 = var28 + (var46 - var28) * var2;
      double var54 = var30 + (var48 - var30) * var2;
      double var56 = var32 + (var50 - var32) * var2;
      double var58 = var52 + 0.3963377774 * var54 + 0.2158037573 * var56;
      double var60 = var52 - 0.1055613458 * var54 - 0.0638541728 * var56;
      double var62 = var52 - 0.0894841775 * var54 - 1.291485548 * var56;
      double var64 = var58 * var58 * var58;
      double var66 = var60 * var60 * var60;
      double var68 = var62 * var62 * var62;
      int var70 = handle(4.0767416621 * var64 - 3.3077115913 * var66 + 0.2309699292 * var68);
      int var71 = handle(-1.2684380046 * var64 + 2.6097574011 * var66 - 0.3413193965 * var68);
      int var72 = handle(-0.0041960863 * var64 - 0.7034186147 * var66 + 1.707614701 * var68);
      return (var3 & 0xFF) << 24 | var70 << 16 | var71 << 8 | var72;
   }

   private static double[] compute() {
      double[] var0 = new double[256];

      for (int var1 = 0; var1 < var0.length; var1++) {
         double var2 = var1 / 255.0;
         var0[var1] = var2 <= 0.04045 ? var2 / 12.92 : Math.pow((var2 + 0.055) / 1.055, 2.4);
      }

      return var0;
   }

   private static int handle(double var0) {
      double var2 = var0 <= 0.0 ? 0.0 : Math.min(1.0, var0);
      double var4 = var2 <= 0.0031308 ? var2 * 12.92 : 1.055 * Math.pow(var2, 0.4166666666666667) - 0.055;
      int var6 = (int)Math.round(var4 * 255.0);
      if (var6 < 0) {
         return 0;
      } else {
         return var6 > 255 ? 255 : var6;
      }
   }

   private static float handle(float var0) {
      if (var0 < 0.0F) {
         return 0.0F;
      } else {
         return var0 > 1.0F ? 1.0F : var0;
      }
   }

   public static float[] apply(int var0) {
      return new float[]{(var0 >> 16 & 0xFF) / 255.0F, (var0 >> 8 & 0xFF) / 255.0F, (var0 & 0xFF) / 255.0F, (var0 >> 24 & 0xFF) / 255.0F};
   }

   public static int[] execute(int var0) {
      return new int[]{
         (int)((var0 >> 16 & 0xFF) / 255.0F), (int)((var0 >> 8 & 0xFF) / 255.0F), (int)((var0 & 0xFF) / 255.0F), (int)((var0 >> 24 & 0xFF) / 255.0F)
      };
   }

   public static int prepare(int var0) {
      return var0 >> 16 & 0xFF;
   }

   public static int check(int var0) {
      return var0 >> 8 & 0xFF;
   }

   public static int onTick(int var0) {
      return var0 & 0xFF;
   }

   public static int select(int var0) {
      return var0 >> 24 & 0xFF;
   }

   public static float refresh(int var0) {
      return prepare(var0) / 255.0F;
   }

   public static float render(int var0) {
      return check(var0) / 255.0F;
   }

   public static float tick(int var0) {
      return onTick(var0) / 255.0F;
   }

   public static float drawAnimation(int var0) {
      return select(var0) / 255.0F;
   }

   public static int[] encodePoint(int var0) {
      return new int[]{prepare(var0), check(var0), onTick(var0), select(var0)};
   }

   public static int[] animate(int var0) {
      return new int[]{prepare(var0), check(var0), onTick(var0)};
   }

   public static float[] load(int var0) {
      return new float[]{refresh(var0), render(var0), tick(var0), drawAnimation(var0)};
   }

   public static float[] save(int var0) {
      return new float[]{refresh(var0), render(var0), tick(var0)};
   }

   public static int handle(float var0, float var1, float var2, float var3) {
      return compute(Math.round(var0 * 255.0F), Math.round(var1 * 255.0F), Math.round(var2 * 255.0F), Math.round(var3 * 255.0F));
   }

   public static int handle(int var0, int var1, int var2, float var3) {
      return compute(var0, var1, var2, Math.round(var3 * 255.0F));
   }

   public static int handle(float var0, float var1, float var2) {
      return handle(var0, var1, var2, 1.0F);
   }

   public static int resolve(int var0, int var1) {
      return compute(var0, var0, var0, var1);
   }

   public static int handle(int var0, float var1) {
      return resolve(var0, Math.round(var1 * 255.0F));
   }

   public static int submit(int var0) {
      return process(var0, var0, var0);
   }

   public static int update(int var0, int var1) {
      return compute(prepare(var0), check(var0), onTick(var0), var1);
   }

   public static int process(int var0, float var1) {
      return handle(prepare(var0), check(var0), onTick(var0), var1);
   }

   public static int compute(int var0, float var1) {
      return compute(prepare(var0), check(var0), onTick(var0), Math.round(select(var0) * var1));
   }

   public static int resolve(int var0, float var1) {
      int var2 = select(var0);
      int var3 = compute(var0, var2 << 24 | 8421504, var1);
      int var4 = prepare(var3);
      int var5 = check(var3);
      int var6 = onTick(var3);
      float var7 = var1 / 2.0F;
      var4 = Math.round(var4 * var7);
      var5 = Math.round(var5 * var7);
      var6 = Math.round(var6 * var7);
      return compute(var4, var5, var6, var2);
   }

   public static int update(int var0, float var1) {
      return compute(Math.round(prepare(var0) * var1), Math.round(check(var0) * var1), Math.round(onTick(var0) * var1), select(var0));
   }

   public static int apply(int var0, float var1) {
      return compute(
         Math.min(255, Math.round(prepare(var0) / var1)),
         Math.min(255, Math.round(check(var0) / var1)),
         Math.min(255, Math.round(onTick(var0) / var1)),
         select(var0)
      );
   }

   public static int update(int var0, int var1, float var2) {
      return compute(var0, var1, var2);
   }

   public static int apply(int var0, int var1) {
      return update(var0, var1, 0.5F);
   }

   public static int[] handle(int var0, int var1, int var2) {
      int[] var3 = new int[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         float var5 = (float)var4 / (var2 - 1);
         var3[var4] = update(var0, var1, var5);
      }

      return var3;
   }

   public static int resolve(int var0, int var1, double var2) {
      return process(var0, var1, var2);
   }

   public static int handle(int var0, int var1, float var2, float var3, float var4) {
      int var5 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      float var6 = var5 / 360.0F;
      int var7 = Color.HSBtoRGB(var6, var2, var3);
      return compute(prepare(var7), check(var7), onTick(var7), Math.round(var4 * 255.0F));
   }

   public static int handle(int var0, int var1, int var2, int var3) {
      int var4 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      var4 = var4 >= 180 ? 360 - var4 : var4;
      return update(var2, var3, var4 / 180.0F);
   }

   public static int unload(int var0) {
      return handle(10, var0, handle(), update(handle(), 0.5F));
   }

   public static int handle() {
      return RoundedRectRenderer.ColorState.handle();
   }

   public static int process(int var0, int var1, int var2, int var3) {
      int var4 = (int)((System.currentTimeMillis() / var3 + var2) % 360L);
      var4 = (var4 > 180 ? 360 - var4 : var4) + 180;
      int var5 = resolve(var0, var1, (double)MathHelper.clamp(var4 / 180.0F - 1.0F, 0.0F, 1.0F));
      float[] var6 = Color.RGBtoHSB(prepare(var5), check(var5), onTick(var5), enabled.get());
      var6[1] *= 1.5F;
      var6[1] = Math.min(var6[1], 1.0F);
      return Color.HSBtoRGB(var6[0], var6[1], var6[2]);
   }

   public static int compute(int var0, int var1, int var2, int var3) {
      PackedColor.PrimaryState var4 = new PackedColor.PrimaryState(var0, var1, var2, var3);
      PackedColor.State var5 = current.computeIfAbsent(var4, var4x -> {
         PackedColor.State var5x = new PackedColor.State(var4x, resolve(var0, var1, var2, var3), 60000L);
         mode.offer(var5x);
         return var5x;
      });
      return var5.compute();
   }

   public static int process(int var0, int var1, int var2) {
      return compute(var0, var1, var2, 255);
   }

   private static int resolve(int var0, int var1, int var2, int var3) {
      return MathHelper.clamp(var3, 0, 255) << 24 | MathHelper.clamp(var0, 0, 255) << 16 | MathHelper.clamp(var1, 0, 255) << 8 | MathHelper.clamp(var2, 0, 255);
   }

   private static String update(int var0, int var1, int var2, int var3) {
      return var0 + "," + var1 + "," + var2 + "," + var3;
   }

   public static void process() {
      active.shutdown();
   }
   private PackedColor() {
   }

   static {
      active.scheduleWithFixedDelay(() -> {
         for (PackedColor.State var0 = mode.poll(); var0 != null; var0 = mode.poll()) {
            if (var0.handle()) {
               current.remove(var0.process());
            }
         }
      }, 0L, 1L, TimeUnit.SECONDS);
   }

   static class PrimaryState {
      final int instance;
      final int data;
      final int context;
      final int config;
      public int handle() {
         return this.instance;
      }
      public int process() {
         return this.data;
      }
      public int compute() {
         return this.context;
      }
      public int resolve() {
         return this.config;
      }
      public PrimaryState(int var1, int var2, int var3, int var4) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
      }
      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PackedColor.PrimaryState var2)) {
            return false;
         } else if (!var2.handle(this)) {
            return false;
         } else if (this.handle() != var2.handle()) {
            return false;
         } else if (this.process() != var2.process()) {
            return false;
         } else {
            return this.compute() != var2.compute() ? false : this.resolve() == var2.resolve();
         }
      }
      protected boolean handle(Object var1) {
         return var1 instanceof PackedColor.PrimaryState;
      }
      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.handle();
         var2 = var2 * 59 + this.process();
         var2 = var2 * 59 + this.compute();
         return var2 * 59 + this.resolve();
      }
   }

   static class State implements Delayed {
      private final PackedColor.PrimaryState instance;
      private final int data;
      private final long context;

      State(PackedColor.PrimaryState var1, int var2, long var3) {
         this.instance = var1;
         this.data = var2;
         this.context = System.currentTimeMillis() + var3;
      }

      @Override
      public long getDelay(TimeUnit var1) {
         long var2 = this.context - System.currentTimeMillis();
         return var1.convert(var2, TimeUnit.MILLISECONDS);
      }

      @Override
      public int compareTo(Delayed var1) {
         return var1 instanceof PackedColor.State ? Long.compare(this.context, ((PackedColor.State)var1).context) : 0;
      }

      public boolean handle() {
         return System.currentTimeMillis() > this.context;
      }
      public PackedColor.PrimaryState process() {
         return this.instance;
      }
      public int compute() {
         return this.data;
      }
      public long resolve() {
         return this.context;
      }
   }
}
