package ru.wild.modules.misc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Util;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.audio.UserSoundPlayer;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "Totem Voices", description = "Заменяет звук тотема на кастомный", category = ModuleCategory.Misc)
public class TotemVoices extends Module {
   private final NumberSetting source = new NumberSetting("Громкость", 50.0F, 0.0F, 100.0F, 1.0F, false);
   private final ModeSetting target = new ModeSetting("Звук", "Хмм", "Хмм", "Это печально(", "Ебать это чё", "67!");
   private final BooleanSetting pending = new BooleanSetting("Кастомные звуки", false);
   private final ActionSetting previous = new ActionSetting("Открыть папку", 0).handle(() -> process("totem"));

   public TotemVoices() {
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
      }, "Wild-TotemVoices-FolderWatcher");
      var1.setDaemon(true);
      var1.start();
      this.handle(this.pending, this.target, this.previous, this.source);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      this.render();
      if (Module.client.player != null && Module.client.world != null) {
         if (var1.resolve() instanceof EntityStatusS2CPacket var2 && var2.getStatus() == 35) {
            Entity var4 = var2.getEntity(Module.client.world);
            if (var4 != null && var4.getId() == Module.client.player.getId()) {
               var1.process();
               this.refresh();
               Module.client.execute(() -> {
                  Module.client.particleManager.addEmitter(var4, ParticleTypes.TOTEM_OF_UNDYING, 30);
                  Module.client.gameRenderer.showFloatingItem(new ItemStack(Items.TOTEM_OF_UNDYING));
               });
            }
         }
      }
   }

   private void refresh() {
      if (this.pending.compute() && !handle(this.target.compute())) {
         File var4 = new File(new File(WildClient.process(), "sounds/totem"), new File(this.target.compute()).getName());
         UserSoundPlayer.handle(var4, this.source.compute() / 100.0F);
      } else {
         String var1;
         if (this.target.process("Хмм")) {
            var1 = "hm_pon.wav";
         } else if (this.target.process("Это печально(")) {
            var1 = "tusky_etopechalno.wav";
         } else if (this.target.process("67!")) {
            var1 = "pampimpoms.wav";
         } else {
            var1 = "ebat_eto_cho.wav";
         }

         String var2 = "/assets/" + "wild" + "/tusky/" + var1;
         Thread var3 = new Thread(() -> {
            try {
               InputStream var2x = TotemVoices.class.getResourceAsStream(var2);
               if (var2x == null) {
                  ChatLogger.handle("Не найден звук по пути: " + var2);
                  return;
               }

               AudioInputStream var3x = AudioSystem.getAudioInputStream(new BufferedInputStream(var2x));
               Clip var4x = AudioSystem.getClip();
               var4x.open(var3x);
               FloatControl var5 = (FloatControl)var4x.getControl(Type.MASTER_GAIN);
               float var6 = this.source.compute();
               if (var6 <= 0.0F) {
                  var5.setValue(var5.getMinimum());
               } else {
                  float var7 = (float)(Math.log10(var6 / 100.0) * 20.0);
                  var5.setValue(Math.max(var5.getMinimum(), Math.min(var5.getMaximum(), var7)));
               }

               var4x.start();
            } catch (Exception var8) {
               var8.printStackTrace();
               ChatLogger.handle("Ошибка воспроизведения: " + var8.getMessage());
            }
         }, "Wild-TotemVoice");
         var3.setDaemon(true);
         var3.start();
      }
   }

   private void render() {
      File var1 = new File(WildClient.process(), "sounds/totem");
      var1.mkdirs();
      ArrayList<String> var2 = new ArrayList<>(Arrays.asList("Хмм", "Это печально(", "Ебать это чё", "67!"));
      File[] var3 = var1.listFiles(var0 -> var0.isFile() && handle(var0));
      if (var3 != null) {
         Arrays.sort(var3, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

         for (File var7 : var3) {
            var2.add(var7.getName());
         }
      }

      this.target.handle(var2);
   }

   private static boolean handle(String var0) {
      return "Хмм".equals(var0) || "Это печально(".equals(var0) || "Ебать это чё".equals(var0) || "67!".equals(var0);
   }

   private static boolean handle(File var0) {
      String var1 = var0.getName().toLowerCase(Locale.ROOT);
      return var1.endsWith(".mp3") || var1.endsWith(".wav") || var1.endsWith(".aiff") || var1.endsWith(".au");
   }

   private static void process(String var0) {
      File var1 = new File(WildClient.process(), "sounds/" + var0);
      var1.mkdirs();

      try {
         Util.getOperatingSystem().open(var1);
      } catch (Throwable var3) {
         System.err.println("[Wild] Cannot open sound folder: " + var3.getMessage());
      }
   }
}
