package ru.wild.audio;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ProceduralSoundSynthesizer {
   public static final long instance = 80000000L;
   public static final long data = 180000000L;
   public static final long context = 650000000L;
   public static final long config = 1250000000L;
   public static final long state = 2250000000L;
   private static final AtomicBoolean cache = new AtomicBoolean(false);
   private static final AtomicBoolean output = new AtomicBoolean(false);
   private static volatile long current;
   private static volatile long active;
   private static volatile ProceduralSoundSynthesizer.Mode mode = ProceduralSoundSynthesizer.Mode.FRAMEBUFFER_COLLAPSE;
   private static volatile long selection = 80000000L;
   private static volatile long enabled = 650000000L;
   private static volatile long renderer = 1250000000L;
   private static volatile long handler = 2250000000L;
   private static volatile float animationDraw;
   private static volatile float pointEncode;
   private static volatile float animator;
   private static volatile float source;
   private static volatile float target;
   private static volatile float pending;

   private ProceduralSoundSynthesizer() {
   }

   public static void handle() {
      cache.set(true);
   }

   public static boolean process() {
      return cache.get();
   }

   public static void compute() {
      if (cache.get()) {
         if (!output.get()) {
            if (ThreadLocalRandom.current().nextInt(100) < 70) {
               resolve();
            }
         }
      }
   }

   public static void resolve() {
      if (output.compareAndSet(false, true)) {
         ThreadLocalRandom var0 = ThreadLocalRandom.current();
         current = System.nanoTime();
         active = var0.nextLong();
         ProceduralSoundSynthesizer.Mode[] var1 = ProceduralSoundSynthesizer.Mode.values();
         mode = var1[var0.nextInt(var1.length)];
         long var2 = var0.nextLong(35L, 125L);
         long var4 = var2 + var0.nextLong(220L, 760L);
         long var6 = var4 + var0.nextLong(260L, 980L);
         long var8 = var6 + var0.nextLong(220L, 920L);
         selection = var2 * 1000000L;
         enabled = var4 * 1000000L;
         renderer = var6 * 1000000L;
         handler = var8 * 1000000L;
         animationDraw = var0.nextFloat(0.1F, 0.88F);
         pointEncode = var0.nextFloat(0.04F, 0.62F);
         animator = var0.nextFloat(0.28F, 1.0F);
         source = var0.nextFloat(0.1F, 0.68F);
         target = var0.nextFloat(0.28F, 1.0F);
         pending = var0.nextFloat(0.2F, 1.0F);
         switch (mode) {
            case FRAMEBUFFER_COLLAPSE:
               animationDraw = Math.max(animationDraw, 0.6F);
               source = Math.max(source, 0.46F);
               target = Math.max(target, 0.66F);
               break;
            case VRAM_GARBAGE:
               pending = Math.max(pending, 0.88F);
               animator = Math.max(animator, 0.74F);
               pointEncode = Math.max(pointEncode, 0.18F);
               break;
            case DESYNC_FAILURE:
               target = Math.max(target, 0.88F);
               source = Math.max(source, 0.42F);
               animator = Math.max(animator, 0.6F);
               break;
            case TERMINAL_DEATH:
               animationDraw = Math.max(animationDraw, 0.76F);
               pointEncode = Math.max(pointEncode, 0.44F);
               source = Math.max(source, 0.54F);
               handler = Math.min(handler, 1420000000L + var0.nextLong(0L, 580000000L));
               break;
            case BLACK_PANEL:
               animationDraw = Math.max(animationDraw, 0.92F);
               pointEncode = Math.min(pointEncode, 0.16F);
               pending = Math.min(pending, 0.34F);
               animator = Math.min(animator, 0.48F);
               break;
            case BROKEN_PIPELINE:
               target = Math.max(target, 0.96F);
               animator = Math.max(animator, 0.86F);
               pending = Math.max(pending, 0.72F);
               source = Math.max(source, 0.52F);
         }

         try {
            ProceduralAudioOutput.process();
         } catch (Throwable var11) {
         }
      }
   }

   public static boolean update() {
      return output.get();
   }

   public static ProceduralSoundSynthesizer.Mode apply() {
      return mode;
   }

   public static long execute() {
      return output.get() ? Math.max(0L, System.nanoTime() - current) : 0L;
   }

   public static long prepare() {
      return execute() / 1000000L;
   }

   public static long check() {
      return prepare();
   }

   public static long onTick() {
      return active;
   }

   public static ProceduralSoundSynthesizer.PrimaryMode select() {
      if (!output.get()) {
         return ProceduralSoundSynthesizer.PrimaryMode.IDLE;
      } else {
         long var0 = execute();
         if (var0 < selection) {
            return ProceduralSoundSynthesizer.PrimaryMode.STRIKE;
         } else {
            return var0 < renderer ? ProceduralSoundSynthesizer.PrimaryMode.COLLAPSE : ProceduralSoundSynthesizer.PrimaryMode.DEATH;
         }
      }
   }

   public static int refresh() {
      if (!output.get()) {
         return 0;
      } else {
         long var0 = execute();
         if (var0 >= handler) {
            return 5;
         } else if (var0 >= renderer) {
            return 4;
         } else if (var0 >= enabled) {
            return 3;
         } else {
            return var0 >= selection ? 2 : 1;
         }
      }
   }

   public static float render() {
      if (!output.get()) {
         return 0.0F;
      } else {
         long var0 = execute();
         if (var0 < selection) {
            return 0.72F + 0.28F * handle((float)var0 / (float)Math.max(1L, selection));
         } else if (var0 < enabled) {
            return 0.82F + 0.18F * handle((float)(var0 - selection) / (float)Math.max(1L, enabled - selection));
         } else {
            return var0 < renderer ? 0.92F + 0.08F * handle((float)(var0 - enabled) / (float)Math.max(1L, renderer - enabled)) : 1.0F;
         }
      }
   }

   public static float tick() {
      return !output.get() ? 0.0F : process((float)execute() / (float)Math.max(1L, handler));
   }

   public static float handle(long var0, long var2) {
      if (!output.get()) {
         return 0.0F;
      } else {
         return var2 <= var0 ? 1.0F : process((float)(execute() - var0) / (float)(var2 - var0));
      }
   }

   public static boolean drawAnimation() {
      return output.get() && execute() >= handler;
   }

   public static boolean encodePoint() {
      if (!output.get()) {
         return false;
      }

      ProceduralSoundSynthesizer.PrimaryMode var0 = select();
      if (var0 == ProceduralSoundSynthesizer.PrimaryMode.IDLE) {
         return false;
      }

      float var1 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 0.18F;
         case VRAM_GARBAGE -> 0.08F;
         case DESYNC_FAILURE -> 0.24F;
         case TERMINAL_DEATH -> 0.36F;
         case BLACK_PANEL -> 0.14F;
         case BROKEN_PIPELINE -> 0.3F;
      };
      if (var0 == ProceduralSoundSynthesizer.PrimaryMode.STRIKE) {
         return handle(7001, 88L, 0.46F + source * 0.32F, 26L);
      } else {
         return var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH
            ? handle(var1 + 0.4F + source * 0.34F, process(7002, 24L))
            : handle(var1 + source * 0.34F * render(), process(7003, 30L));
      }
   }

   public static boolean animate() {
      if (!output.get()) {
         return false;
      } else {
         ProceduralSoundSynthesizer.PrimaryMode var0 = select();
         if (var0 == ProceduralSoundSynthesizer.PrimaryMode.IDLE) {
            return false;
         } else if (mode == ProceduralSoundSynthesizer.Mode.BLACK_PANEL && var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH) {
            return true;
         } else if (mode == ProceduralSoundSynthesizer.Mode.TERMINAL_DEATH && execute() > renderer + 120000000L) {
            return true;
         } else if (var0 == ProceduralSoundSynthesizer.PrimaryMode.STRIKE) {
            return handle(7101, 140L, animationDraw * 0.48F, 42L);
         } else {
            return var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH
               ? handle(0.36F + animationDraw * 0.58F, process(7102, 38L))
               : handle(0.08F + animationDraw * 0.42F * render(), process(7103, 48L));
         }
      }
   }

   public static boolean load() {
      if (!output.get()) {
         return false;
      } else {
         ProceduralSoundSynthesizer.PrimaryMode var0 = select();
         if (var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH && mode == ProceduralSoundSynthesizer.Mode.BLACK_PANEL) {
            return true;
         } else {
            return var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH && mode == ProceduralSoundSynthesizer.Mode.TERMINAL_DEATH
               ? execute() > renderer + 90000000L
               : animate() && handle(0.24F + animationDraw * 0.48F, process(7201, 62L));
         }
      }
   }

   public static boolean save() {
      if (!output.get()) {
         return false;
      } else {
         ProceduralSoundSynthesizer.PrimaryMode var0 = select();
         if (mode == ProceduralSoundSynthesizer.Mode.BLACK_PANEL) {
            return handle(7301, 820L, 0.1F, 24L);
         } else if (var0 == ProceduralSoundSynthesizer.PrimaryMode.STRIKE) {
            return handle(7302, 105L, 0.78F + pointEncode * 0.18F, 20L);
         } else {
            return var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH
               ? handle(7303, 240L, 0.24F + pointEncode * 0.44F, 36L)
               : handle(7304, 300L, 0.1F + pointEncode * 0.36F, 28L);
         }
      }
   }

   public static boolean submit() {
      if (!output.get()) {
         return false;
      }

      ProceduralSoundSynthesizer.PrimaryMode var0 = select();
      if (var0 == ProceduralSoundSynthesizer.PrimaryMode.IDLE) {
         return false;
      }

      if (var0 == ProceduralSoundSynthesizer.PrimaryMode.STRIKE) {
         return handle(7401, 140L, 0.32F, 42L);
      }

      float var1 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 0.44F;
         case VRAM_GARBAGE -> 0.16F;
         case DESYNC_FAILURE -> 0.28F;
         case TERMINAL_DEATH -> 0.5F;
         case BLACK_PANEL -> 0.38F;
         case BROKEN_PIPELINE -> 0.36F;
      };
      return handle(7402, 460L, var1, 86L + (long)(130.0F * render()));
   }

   public static boolean unload() {
      if (!output.get()) {
         return false;
      }

      ProceduralSoundSynthesizer.PrimaryMode var0 = select();
      if (var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH) {
         return true;
      }

      float var1 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 0.5F;
         case VRAM_GARBAGE -> 0.12F;
         case DESYNC_FAILURE -> 0.58F;
         case TERMINAL_DEATH -> 0.4F;
         case BLACK_PANEL -> 0.2F;
         case BROKEN_PIPELINE -> 0.72F;
      };
      return handle(var1 * target * render(), process(7501, 34L));
   }

   public static float fetch() {
      if (!output.get()) {
         return 0.0F;
      }

      float var0 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 0.12F;
         case VRAM_GARBAGE -> 0.05F;
         case DESYNC_FAILURE -> 0.24F;
         case TERMINAL_DEATH -> 0.18F;
         case BLACK_PANEL -> 0.04F;
         case BROKEN_PIPELINE -> 0.3F;
      };
      return handle(7601, 20L) * var0 * target * render();
   }

   public static float measure() {
      if (!output.get()) {
         return 0.0F;
      }

      float var0 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 0.16F;
         case VRAM_GARBAGE -> 0.07F;
         case DESYNC_FAILURE -> 0.3F;
         case TERMINAL_DEATH -> 0.2F;
         case BLACK_PANEL -> 0.06F;
         case BROKEN_PIPELINE -> 0.34F;
      };
      return handle(7602, 24L) * var0 * target * render();
   }

   public static float blendMatrix() {
      if (!output.get()) {
         return 1.0F;
      } else {
         return unload() ? Math.max(0.018F, 1.0F - Math.abs(handle(7603, 32L)) * 0.95F * target) : 1.0F + handle(7604, 28L) * 0.26F * target * render();
      }
   }

   public static float matchVector() {
      if (!output.get()) {
         return 1.0F;
      } else {
         return unload() ? Math.max(0.012F, 1.0F - Math.abs(handle(7605, 30L)) * 0.98F * target) : 1.0F + handle(7606, 26L) * 0.34F * target * render();
      }
   }

   public static float projectItem() {
      if (!output.get()) {
         return 1.0F;
      }

      if (mode == ProceduralSoundSynthesizer.Mode.BLACK_PANEL) {
         return 1.0F;
      }

      ProceduralSoundSynthesizer.PrimaryMode var0 = select();

      float var1 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 0.16F;
         case VRAM_GARBAGE -> 0.08F;
         case DESYNC_FAILURE -> 0.3F;
         case TERMINAL_DEATH -> 0.18F;
         case BLACK_PANEL -> 0.0F;
         case BROKEN_PIPELINE -> 0.26F;
      };
      return var0 == ProceduralSoundSynthesizer.PrimaryMode.STRIKE ? 1.0F + handle(7701, 16L) * var1 * 1.45F : 1.0F + handle(7702, 32L) * var1 * render();
   }

   public static int computeResponse() {
      if (!output.get()) {
         return 0;
      }

      byte var0 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 24;
         case VRAM_GARBAGE -> 36;
         case DESYNC_FAILURE -> 20;
         case TERMINAL_DEATH -> 14;
         case BLACK_PANEL -> 8;
         case BROKEN_PIPELINE -> 46;
      };
      return Math.max(1, (int)(var0 * (0.55F + animator * 0.78F) * render()));
   }

   public static int fetchProvider() {
      if (!output.get()) {
         return 0;
      }

      byte var0 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 18;
         case VRAM_GARBAGE -> 82;
         case DESYNC_FAILURE -> 24;
         case TERMINAL_DEATH -> 16;
         case BLACK_PANEL -> 6;
         case BROKEN_PIPELINE -> 60;
      };
      return Math.max(1, (int)(var0 * (0.36F + pending) * render()));
   }

   public static int drawProfile() {
      if (!output.get()) {
         return 0;
      }

      short var0 = switch (mode) {
         case FRAMEBUFFER_COLLAPSE -> 100;
         case VRAM_GARBAGE -> 290;
         case DESYNC_FAILURE -> 130;
         case TERMINAL_DEATH -> 86;
         case BLACK_PANEL -> 38;
         case BROKEN_PIPELINE -> 210;
      };
      return Math.max(1, (int)(var0 * (0.26F + pending) * render()));
   }

   public static float performVector() {
      if (!output.get()) {
         return 0.0F;
      }

      ProceduralSoundSynthesizer.PrimaryMode var0 = select();

      float var1 = switch (var0) {
         case IDLE -> 0.0F;
         case STRIKE -> 0.18F;
         case COLLAPSE -> 0.36F;
         case DEATH -> 0.8F;
      };
      if (mode == ProceduralSoundSynthesizer.Mode.BLACK_PANEL) {
         var1 += 0.22F;
      }

      if (mode == ProceduralSoundSynthesizer.Mode.TERMINAL_DEATH && var0 == ProceduralSoundSynthesizer.PrimaryMode.DEATH) {
         var1 += 0.18F;
      }

      return process(var1 + animationDraw * 0.3F * render());
   }

   public static float attachEvent() {
      if (!output.get()) {
         return 0.0F;
      }

      if (!save()) {
         return 0.0F;
      }

      float var0 = switch (select()) {
         case IDLE -> 0.0F;
         case STRIKE -> 0.84F;
         case COLLAPSE -> 0.56F;
         case DEATH -> 0.7F;
      };
      return process(var0 + pointEncode * 0.2F);
   }

   public static float readServer() {
      return animator;
   }

   public static float advancePosition() {
      return pending;
   }

   public static float checkFrame() {
      return animationDraw;
   }

   public static float collectModule() {
      return pointEncode;
   }

   public static float closeProvider() {
      return target;
   }

   public static float savePreset() {
      return source;
   }

   public static float handle(int var0) {
      long var1 = System.nanoTime() / 16000000L;
      return resolve(handle(active ^ var1 ^ var0 * -7046029254386353131L));
   }

   public static float process(int var0) {
      long var1 = System.nanoTime() / 7000000L;
      return resolve(handle(active ^ var1 ^ var0 * -4417276706812531889L));
   }

   public static float handle(int var0, long var1) {
      long var3 = Math.max(1L, var1);
      long var5 = prepare() / var3;
      return resolve(handle(active ^ var5 * -3335678366873096957L ^ var0 * -7046029254386353131L));
   }

   public static float compute(int var0) {
      return resolve(handle(active ^ var0 * -4417276706812531889L));
   }

   public static boolean handle(int var0, long var1, float var3) {
      if (!output.get()) {
         return false;
      }

      long var4 = Math.max(1L, var1);
      float var6 = process(var3);
      long var7 = process(handle(active ^ var0 * -7723592293110705685L)) % var4;
      long var9 = Math.floorMod(prepare() + var7, var4);
      return var9 < (long)((float)var4 * var6);
   }

   public static boolean handle(int var0, long var1, float var3, long var4) {
      if (!output.get()) {
         return false;
      }

      long var6 = Math.max(1L, var1);
      long var8 = Math.max(1L, Math.min(var4, var6));
      long var10 = prepare();
      long var12 = var10 / var6;
      long var14 = var10 % var6;
      float var16 = compute(handle(active ^ var12 * -4658895280553007687L ^ var0 * -7046029254386353131L));
      return var16 < process(var3) && var14 < var8;
   }

   public static boolean handle(float var0, long var1) {
      if (!output.get()) {
         return false;
      }

      long var3 = active ^ var1 * -2960836687051489901L;
      return compute(handle(var3)) < process(var0);
   }

   public static long process(int var0, long var1) {
      long var3 = Math.max(1L, var1);
      return prepare() / var3 * -7046029254386353131L ^ var0;
   }

   public static float handle(float var0) {
      float var1 = process(var0);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   public static float process(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }

   private static long handle(long var0) {
      var0 ^= var0 >>> 33;
      var0 *= -49064778989728563L;
      var0 ^= var0 >>> 33;
      var0 *= -4265267296055464877L;
      return var0 ^ var0 >>> 33;
   }

   private static long process(long var0) {
      return var0 & Long.MAX_VALUE;
   }

   private static float compute(long var0) {
      return (float)(var0 >>> 40 & 16777215L) / 1.6777215E7F;
   }

   private static float resolve(long var0) {
      return compute(var0) * 2.0F - 1.0F;
   }

   public enum Mode {
      FRAMEBUFFER_COLLAPSE,
      VRAM_GARBAGE,
      DESYNC_FAILURE,
      TERMINAL_DEATH,
      BLACK_PANEL,
      BROKEN_PIPELINE;
   }

   public enum PrimaryMode {
      IDLE,
      STRIKE,
      COLLAPSE,
      DEATH;
   }
}
