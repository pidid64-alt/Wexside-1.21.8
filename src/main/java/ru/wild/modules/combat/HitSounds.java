package ru.wild.modules.combat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Util;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.audio.UserSoundPlayer;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "HitSounds", category = ModuleCategory.Combat, description = "Звуки при попадании и критическом ударе")
public class HitSounds extends Module {
   private static final int source = 48000;
   private static final ExecutorService target = Executors.newFixedThreadPool(1, var0 -> {
      Thread var1 = new Thread(var0, "Wild-HitSounds");
      var1.setDaemon(true);
      return var1;
   });
   private final ModeSetting pending = new ModeSetting("Тембр", "Органик", "Органик", "Стекло", "Глубокий", "Резкий");
   private final BooleanSetting previous = new BooleanSetting("Кастомные звуки", false);
   private final ActionSetting latest = new ActionSetting("Открыть папку", 0).handle(() -> compute("hitsounds"));
   private final NumberSetting summary = new NumberSetting("Громкость", 0.62F, 0.0F, 1.0F, 0.01F, true);
   private final NumberSetting matrixBlend = new NumberSetting("Высота тона", 1.0F, 0.72F, 1.34F, 0.01F, false);
   private final NumberSetting vectorMatch = new NumberSetting("Яркость", 0.58F, 0.0F, 1.0F, 0.01F, true);
   private final NumberSetting itemProject = new NumberSetting("Низкие частоты", 0.62F, 0.0F, 1.0F, 0.01F, true);
   private final NumberSetting responseCompute = new NumberSetting("Задержка", 35.0F, 0.0F, 180.0F, 1.0F, false);
   private final BooleanSetting providerFetch = new BooleanSetting("Слой крита", true);
   private long profileDraw;

   public HitSounds() {
      this.render();
      Thread var1 = new Thread(() -> {
         while (!Thread.currentThread().isInterrupted()) {
            try {
               Thread.sleep(1000L);
               this.render();
            } catch (InterruptedException var2) {
               Thread.currentThread().interrupt();
            } catch (Throwable var3) {
            }
         }
      }, "Wild-HitSounds-FolderWatcher");
      var1.setDaemon(true);
      var1.start();
      this.handle(
         this.previous, this.pending, this.latest, this.summary, this.matrixBlend, this.vectorMatch, this.itemProject, this.responseCompute, this.providerFetch
      );
   }

   @EventHandler
   public void handle(EntityAttackEvent var1) {
      this.render();
      if (!ServerEnvironment.handle() && var1 != null && var1.compute() instanceof LivingEntity var2) {
         long var15 = System.currentTimeMillis();
         if (!((float)(var15 - this.profileDraw) < this.responseCompute.compute())) {
            this.profileDraw = var15;
            boolean var5 = this.providerFetch.compute()
               && Module.client.player.fallDistance > 0.0
               && !Module.client.player.isOnGround()
               && !Module.client.player.isTouchingWater()
               && !Module.client.player.isClimbing();
            float var6 = Module.client.player.getAttackCooldownProgress(0.5F);
            float var7 = (float)Math.min(1.0, Module.client.player.distanceTo(var2) / 4.5F);
            float var8 = handle(0.42F + var6 * 0.48F + (var5 ? 0.26F : 0.0F) - var7 * 0.1F, 0.18F, 1.18F);
            float var9 = handle(this.summary.compute() * var8, 0.0F, 1.0F);
            float var10 = 0.965F + ThreadLocalRandom.current().nextFloat() * 0.071F;
            float var11 = this.matrixBlend.compute() * (var5 ? 1.075F : 1.0F) * var10;
            String var12 = this.pending.state;
            float var13 = this.vectorMatch.compute();
            float var14 = this.itemProject.compute();
            if (this.previous.compute() && process(var12)) {
               UserSoundPlayer.handle(handle(var12), var9);
            } else {
               target.execute(() -> handle(handle(var12, var9, var11, var13, var14, var5)));
            }
         }
      }
   }

   private static File handle(String var0) {
      return var0 != null && !var0.isBlank() && WildClient.process() != null
         ? new File(new File(WildClient.process(), "sounds/hitsounds"), new File(var0).getName())
         : null;
   }

   private void render() {
      File var1 = new File(WildClient.process(), "sounds/hitsounds");
      var1.mkdirs();
      ArrayList<String> var2 = new ArrayList<>(Arrays.asList("Органик", "Стекло", "Глубокий", "Резкий"));
      File[] var3 = var1.listFiles(var0 -> var0.isFile() && handle(var0));
      if (var3 != null) {
         Arrays.sort(var3, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

         for (File var7 : var3) {
            var2.add(var7.getName());
         }
      }

      this.pending.handle(var2);
   }

   private static boolean process(String var0) {
      return var0 != null && !Arrays.asList("Органик", "Стекло", "Глубокий", "Резкий").contains(var0);
   }

   private static boolean handle(File var0) {
      String var1 = var0.getName().toLowerCase(Locale.ROOT);
      return var1.endsWith(".mp3") || var1.endsWith(".wav") || var1.endsWith(".aiff") || var1.endsWith(".au");
   }

   private static void compute(String var0) {
      File var1 = new File(WildClient.process(), "sounds/" + var0);
      var1.mkdirs();

      try {
         Util.getOperatingSystem().open(var1);
      } catch (Throwable var3) {
         System.err.println("[Wild] Cannot open sound folder: " + var3.getMessage());
      }
   }

   public static void refresh() {
      try {
         target.shutdownNow();
         target.awaitTermination(250L, TimeUnit.MILLISECONDS);
      } catch (Throwable var1) {
      }
   }

   private static byte[] handle(String var0, float var1, float var2, float var3, float var4, boolean var5) {
      int var6 = Math.round(48000.0F * (var5 ? 0.145F : 0.112F));
      byte[] var7 = new byte[var6 * 2];
      long var8 = System.nanoTime();

      float var10 = switch (var0) {
         case "Стекло" -> 238.0F;
         case "Глубокий" -> 96.0F;
         case "Резкий" -> 184.0F;
         default -> 132.0F;
      } * var2;

      float var26 = switch (var0) {
         case "Стекло" -> 1920.0F;
         case "Глубокий" -> 720.0F;
         case "Резкий" -> 2640.0F;
         default -> 1280.0F;
      } * var2;

      for (int var28 = 0; var28 < var6; var28++) {
         float var29 = var28 / 48000.0F;
         float var14 = handle(var8 + var28 * -7046029254386353131L);
         float var15 = 1.0F - (float)Math.exp(-var29 * 920.0F);
         float var16 = (float)Math.exp(-var29 * (var5 ? 22.0F : 29.0F));
         float var17 = (float)Math.exp(-var29 * 220.0F);
         float var18 = (float)Math.exp(-var29 * (var5 ? 36.0F : 52.0F));
         float var19 = (float)Math.sin((Math.PI * 2) * (var10 * var29 - var29 * var29 * var10 * 2.3F));
         float var20 = (float)Math.sin((Math.PI * 2) * (var10 * 0.47F * var29));
         float var21 = (float)Math.sin((Math.PI * 2) * (var26 * var29 + var29 * var29 * 1080.0F * var2));
         float var22 = (float)Math.sin((Math.PI * 2) * (var26 * 1.74F * var29 + Math.sin(var29 * 44.0F) * 0.018F));
         float var23 = var5 ? (float)Math.sin((Math.PI * 2) * (var26 * 2.1F * var29 + var29 * var29 * 1600.0F)) * (float)Math.exp(-var29 * 31.0F) : 0.0F;
         float var24 = var19 * var16 * (0.22F + var4 * 0.38F);
         var24 += var20 * var16 * var4 * 0.16F;
         var24 += var14 * var17 * (0.3F + var3 * 0.34F);
         var24 += var21 * var18 * var3 * 0.18F;
         var24 += var22 * var18 * var3 * ("Стекло".equals(var0) ? 0.22F : 0.07F);
         var24 += var23 * var3 * 0.22F;
         var24 *= var15 * var1;
         var24 = handle(var24);
         short var25 = (short)Math.round(handle(var24, -1.0F, 1.0F) * 32767.0F);
         var7[var28 * 2] = (byte)(var25 & 0xFF);
         var7[var28 * 2 + 1] = (byte)(var25 >> 8 & 0xFF);
      }

      return var7;
   }

   private static void handle(byte[] var0) {
      if (var0 != null && var0.length != 0) {
         AudioFormat var1 = new AudioFormat(48000.0F, 16, 1, true, false);

         try (SourceDataLine var2 = AudioSystem.getSourceDataLine(var1)) {
            var2.open(var1, Math.min(var0.length, 6000));
            var2.start();
            var2.write(var0, 0, var0.length);
            var2.drain();
         } catch (Throwable var7) {
         }
      }
   }

   private static float handle(long var0) {
      var0 ^= var0 >>> 33;
      var0 *= -49064778989728563L;
      var0 ^= var0 >>> 33;
      var0 *= -4265267296055464877L;
      var0 ^= var0 >>> 33;
      return (float)(var0 & 65535L) / 32767.5F - 1.0F;
   }

   private static float handle(float var0) {
      return (float)Math.tanh(var0 * 1.42F);
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }
}
