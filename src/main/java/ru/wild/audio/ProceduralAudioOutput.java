package ru.wild.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

public final class ProceduralAudioOutput {
   private static final int instance = 44100;
   private static final float data = 0.42F;
   private static final float context = 0.62F;
   private static final AtomicBoolean config = new AtomicBoolean(false);
   private static volatile Thread state;
   private static volatile boolean cache;
   private static volatile long output;

   private ProceduralAudioOutput() {
   }

   public static void handle() {
      if (!ProceduralSoundSynthesizer.update()) {
         compute();
      } else {
         long var0 = ProceduralSoundSynthesizer.onTick();
         if (var0 != 0L && var0 != output && config.compareAndSet(false, true)) {
            output = var0;
            cache = false;
            Thread var2 = new Thread(ProceduralAudioOutput::resolve, "Wild-AudioDeviceReset");
            var2.setDaemon(true);
            var2.setPriority(10);
            state = var2;
            var2.start();
         }
      }
   }

   public static void process() {
      handle();
   }

   public static void compute() {
      cache = true;
   }
   private static void resolve() {
      SourceDataLine var0 = null;
      boolean var17 = false /* VF: Semaphore variable */;

      label106: {
         try {
            var17 = true;
            ProceduralSoundSynthesizer.Mode var1 = ProceduralSoundSynthesizer.apply();
            long var2 = ProceduralSoundSynthesizer.onTick();
            float var4 = handle(var1, var2);
            byte[] var5 = handle(var1, var2, var4);
            AudioFormat var6 = new AudioFormat(Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);
            var0 = AudioSystem.getSourceDataLine(var6);
            var0.open(var6, Math.min(var5.length, 44100));
            var0.start();
            int var7 = 0;
            short var8 = 1024;

            while (var7 < var5.length && !cache) {
               int var9 = Math.min(var8, var5.length - var7);
               var0.write(var5, var7, var9);
               var7 += var9;
            }

            var0.drain();
            var17 = false;
            break label106;
         } catch (Throwable var21) {
            var17 = false;
         } finally {
            if (var17) {
               try {
                  if (var0 != null) {
                     var0.stop();
                     var0.flush();
                     var0.close();
                  }
               } catch (Throwable var18) {
               }

               config.set(false);
               state = null;
            }
         }

         try {
            if (var0 != null) {
               var0.stop();
               var0.flush();
               var0.close();
            }
         } catch (Throwable var19) {
         }

         config.set(false);
         state = null;
         return;
      }

      try {
         if (var0 != null) {
            var0.stop();
            var0.flush();
            var0.close();
         }
      } catch (Throwable var20) {
      }

      config.set(false);
      state = null;
   }

   private static byte[] handle(ProceduralSoundSynthesizer.Mode var0, long var1, float var3) {
      int var4 = Math.max(1, (int)(44100.0F * var3));
      ByteBuffer var5 = ByteBuffer.allocate(var4 * 2).order(ByteOrder.LITTLE_ENDIAN);
      ProceduralAudioOutput.State var6 = new ProceduralAudioOutput.State(var1 ^ -7935046062780286179L);
      float var7 = 0.0F;
      int var8 = 0;
      float var9 = 0.0F;
      float var10 = 0.0F;

      for (int var11 = 0; var11 < var4; var11++) {
         float var12 = var11 / 44100.0F;
         float var13 = (float)var11 / Math.max(1, var4 - 1);
         float var14 = handle(var13, var0);
         if (var8 <= 0) {
            var7 = handle(var0, var6, var12, var13);
            var8 = process(var0, var6, var13);
         } else {
            var8--;
         }

         float var15 = handle(var0, var6, var12, var13);
         float var16 = var15 * 0.36F + var7 * 0.64F;
         var16 += handle(var0, var12, var13);
         var16 += handle(var0, var6, var13);
         if (var0 == ProceduralSoundSynthesizer.Mode.VRAM_GARBAGE || var0 == ProceduralSoundSynthesizer.Mode.BROKEN_PIPELINE) {
            var16 = resolve(var16, 7.0F + var6.handle() * 11.0F);
         }

         if (compute(var0, var6, var13)) {
            var16 *= var0 == ProceduralSoundSynthesizer.Mode.BLACK_PANEL ? 0.015F : 0.1F;
         }

         if (var6.handle() < handle(var0, var13)) {
            var16 += (var6.handle() * 2.0F - 1.0F) * process(var0);
         }

         var9 = var9 * 0.995F + var16 * 0.005F;
         var16 -= var9;
         var16 = var10 * 0.18F + var16 * 0.82F;
         var10 = var16;
         var16 *= var14;
         var16 *= handle(var0);
         var16 = handle(var16);
         var16 = handle(var16, -0.62F, 0.62F);
         short var17 = (short)(var16 * 32767.0F);
         var5.putShort(var17);
      }

      return var5.array();
   }

   private static float handle(ProceduralSoundSynthesizer.Mode var0, ProceduralAudioOutput.State var1, float var2, float var3) {
      return switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> {
            float var12 = handle(52.0F, var2) * 0.36F;
            float var17 = process(67.0F + 9.0F * handle(2.2F, var2), var2) * 0.31F;
            float var22 = compute(320.0F + 180.0F * handle(4.4F, var2), var2) * 0.16F;
            yield var12 + var17 + var22 + handle(var1) * 0.045F;
         }
         case VRAM_GARBAGE -> {
            float var11 = handle(71.0F, var2) * 0.22F;
            float var16 = process(86.0F + 46.0F * var1.handle(), var2) * 0.34F;
            float var21 = compute(520.0F + 960.0F * var1.handle(), var2) * 0.28F;
            yield var11 + var16 + var21 + handle(var1) * 0.24F;
         }
         case DESYNC_FAILURE -> {
            float var10 = handle(44.0F + 18.0F * handle(3.1F, var2), var2) * 0.3F;
            float var15 = process(79.0F + 58.0F * handle(7.0F, var2), var2) * 0.38F;
            float var20 = handle(760.0F + 330.0F * handle(11.0F, var2), var2) * 0.12F;
            yield var10 + var15 + var20 + handle(var1) * 0.055F;
         }
         case TERMINAL_DEATH -> {
            float var9 = handle(39.0F, var2) * 0.34F;
            float var14 = handle(78.0F, var2) * 0.22F;
            float var19 = process(58.0F + 5.0F * handle(1.4F, var2), var2) * 0.5F;
            float var7 = compute(180.0F + 70.0F * handle(3.2F, var2), var2) * 0.14F;
            yield var9 + var14 + var19 + var7;
         }
         case BLACK_PANEL -> {
            float var8 = handle(37.0F, var2) * 0.46F;
            float var13 = handle(74.0F, var2) * 0.22F;
            float var18 = process(49.0F, var2) * 0.17F;
            yield var8 + var13 + var18 + handle(var1) * 0.035F;
         }
         case BROKEN_PIPELINE -> {
            float var4 = handle(61.0F + 24.0F * handle(2.6F, var2), var2) * 0.28F;
            float var5 = process(76.0F + 72.0F * handle(8.4F, var2), var2) * 0.42F;
            float var6 = compute(630.0F + 1220.0F * var1.handle(), var2) * 0.31F;
            yield var4 + var5 + var6 + handle(var1) * 0.2F;
         }
      };
   }

   private static float handle(ProceduralSoundSynthesizer.Mode var0, float var1, float var2) {
      float var3 = process((var2 - 0.52F) / 0.34F);

      return switch (var0) {
         case VRAM_GARBAGE -> process(69.0F + 12.0F * handle(5.0F, var1), var1) * 0.28F * var3;
         default -> process(56.0F, var1) * 0.26F * var3;
         case TERMINAL_DEATH -> process(54.0F + 4.0F * handle(2.0F, var1), var1) * 0.42F * var3;
         case BLACK_PANEL -> handle(31.0F, var1) * 0.34F * var3;
         case BROKEN_PIPELINE -> process(57.0F + 18.0F * handle(6.0F, var1), var1) * 0.36F * var3;
      };
   }

   private static float handle(ProceduralSoundSynthesizer.Mode var0, ProceduralAudioOutput.State var1, float var2) {
      float var3 = switch (var0) {
         case VRAM_GARBAGE -> 0.055F + var2 * 0.035F;
         default -> 0.026F + var2 * 0.02F;
         case TERMINAL_DEATH -> 0.032F + var2 * 0.02F;
         case BLACK_PANEL -> 0.012F + var2 * 0.01F;
         case BROKEN_PIPELINE -> 0.06F + var2 * 0.04F;
      };
      if (var1.handle() > var3) {
         return 0.0F;
      }

      return (var1.handle() * 2.0F - 1.0F) * switch (var0) {
         case VRAM_GARBAGE, BROKEN_PIPELINE -> 0.36F;
         default -> 0.2F;
         case TERMINAL_DEATH -> 0.25F;
         case BLACK_PANEL -> 0.12F;
      };
   }

   private static int process(ProceduralSoundSynthesizer.Mode var0, ProceduralAudioOutput.State var1, float var2) {
      byte var3 = switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> 44;
         case VRAM_GARBAGE -> 22;
         case DESYNC_FAILURE -> 36;
         case TERMINAL_DEATH -> 80;
         case BLACK_PANEL -> 64;
         case BROKEN_PIPELINE -> 28;
      };
      int var4 = (int)(var3 * var2 * 0.75F);
      return 4 + var1.handle(Math.max(5, var3 + var4));
   }

   private static float handle(ProceduralSoundSynthesizer.Mode var0, long var1) {
      float var3 = process(handle(var1 ^ 828927517355L));

      return switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> 1.55F + var3 * 0.55F;
         case VRAM_GARBAGE -> 1.8F + var3 * 0.7F;
         case DESYNC_FAILURE -> 1.45F + var3 * 0.65F;
         case TERMINAL_DEATH -> 2.0F + var3 * 0.8F;
         case BLACK_PANEL -> 1.25F + var3 * 0.5F;
         case BROKEN_PIPELINE -> 1.85F + var3 * 0.75F;
      };
   }

   private static float handle(ProceduralSoundSynthesizer.Mode var0) {
      float var1 = switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> 0.37F;
         case VRAM_GARBAGE -> 0.39F;
         case DESYNC_FAILURE -> 0.38F;
         case TERMINAL_DEATH -> 0.42F;
         case BLACK_PANEL -> 0.32F;
         case BROKEN_PIPELINE -> 0.41F;
      };
      return Math.min(var1, 0.42F);
   }

   private static float handle(float var0, ProceduralSoundSynthesizer.Mode var1) {
      float var2 = process(var0 / 0.006F);
      float var3 = 1.0F - process((var0 - 0.86F) / 0.14F);
      float var4 = var2 * var3;
      if (var1 == ProceduralSoundSynthesizer.Mode.TERMINAL_DEATH) {
         var4 *= 0.88F + 0.12F * process(6.0F, var0);
      }

      if (var1 == ProceduralSoundSynthesizer.Mode.BLACK_PANEL) {
         var4 *= 0.82F + 0.18F * (1.0F - process((var0 - 0.36F) / 0.48F));
      }

      return handle(var4, 0.0F, 1.0F);
   }

   private static boolean compute(ProceduralSoundSynthesizer.Mode var0, ProceduralAudioOutput.State var1, float var2) {
      float var3 = switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> 0.014F + var2 * 0.024F;
         case VRAM_GARBAGE -> 0.014F + var2 * 0.026F;
         case DESYNC_FAILURE -> 0.016F + var2 * 0.026F;
         case TERMINAL_DEATH -> 0.02F + var2 * 0.04F;
         case BLACK_PANEL -> 0.045F + var2 * 0.07F;
         case BROKEN_PIPELINE -> 0.018F + var2 * 0.03F;
      };
      return var1.handle() < var3;
   }

   private static float handle(ProceduralSoundSynthesizer.Mode var0, float var1) {
      return switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> 0.008F + var1 * 0.014F;
         case VRAM_GARBAGE -> 0.015F + var1 * 0.03F;
         case DESYNC_FAILURE -> 0.01F + var1 * 0.018F;
         case TERMINAL_DEATH -> 0.008F + var1 * 0.016F;
         case BLACK_PANEL -> 0.004F + var1 * 0.006F;
         case BROKEN_PIPELINE -> 0.018F + var1 * 0.032F;
      };
   }

   private static float process(ProceduralSoundSynthesizer.Mode var0) {
      return switch (var0) {
         case FRAMEBUFFER_COLLAPSE -> 0.32F;
         case VRAM_GARBAGE -> 0.5F;
         case DESYNC_FAILURE -> 0.34F;
         case TERMINAL_DEATH -> 0.4F;
         case BLACK_PANEL -> 0.2F;
         case BROKEN_PIPELINE -> 0.54F;
      };
   }

   private static float handle(float var0, float var1) {
      return (float)Math.sin((Math.PI * 2) * var0 * var1);
   }

   private static float process(float var0, float var1) {
      return handle(var0, var1) >= 0.0F ? 1.0F : -1.0F;
   }

   private static float compute(float var0, float var1) {
      float var2 = var1 * var0;
      return 2.0F * (var2 - (float)Math.floor(var2 + 0.5F));
   }

   private static float handle(ProceduralAudioOutput.State var0) {
      return var0.handle() * 2.0F - 1.0F;
   }

   private static float resolve(float var0, float var1) {
      float var2 = Math.max(2.0F, var1);
      return Math.round(var0 * var2) / var2;
   }

   private static float handle(float var0) {
      return (float)Math.tanh(var0 * 1.45F);
   }

   private static float process(float var0) {
      float var1 = handle(var0, 0.0F, 1.0F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float handle(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   private static long handle(long var0) {
      var0 ^= var0 >>> 33;
      var0 *= -49064778989728563L;
      var0 ^= var0 >>> 33;
      var0 *= -4265267296055464877L;
      return var0 ^ var0 >>> 33;
   }

   private static float process(long var0) {
      return (float)(var0 >>> 40 & 16777215L) / 1.6777215E7F;
   }

   static final class State {
      private long instance;

      State(long var1) {
         this.instance = var1 == 0L ? -7046029254386353131L : var1;
      }

      float handle() {
         this.instance = this.instance ^ this.instance << 13;
         this.instance = this.instance ^ this.instance >>> 7;
         this.instance = this.instance ^ this.instance << 17;
         return (float)(this.instance >>> 40 & 16777215L) / 1.6777215E7F;
      }

      int handle(int var1) {
         return var1 <= 1 ? 0 : (int)(this.handle() * var1);
      }
   }
}
