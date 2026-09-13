package ru.wild.audio;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import ru.wild.WildClient;

public class AudioResourceManager {
   private static final int instance = 16;
   private static final ExecutorService data = Executors.newFixedThreadPool(2, var0 -> {
      Thread var1 = new Thread(var0, "Wild-Audio");
      var1.setDaemon(true);
      return var1;
   });
   private static final List<Clip> context = Collections.synchronizedList(new ArrayList<>());
   private static volatile Clip config = null;

   public static void handle() {
      try {
         synchronized (context) {
            for (Clip var2 : context) {
               try {
                  if (var2 != null) {
                     if (var2.isRunning()) {
                        var2.stop();
                     }

                     if (var2.isOpen()) {
                        var2.close();
                     }
                  }
               } catch (Throwable var6) {
               }
            }

            context.clear();
         }

         Clip var9 = config;
         if (var9 != null) {
            try {
               if (var9.isRunning()) {
                  var9.stop();
               }

               if (var9.isOpen()) {
                  var9.close();
               }
            } catch (Throwable var5) {
            }
         }

         config = null;
         data.shutdownNow();
         data.awaitTermination(500L, TimeUnit.MILLISECONDS);
      } catch (Throwable var8) {
      }
   }

   public static void handle(String var0, float var1, boolean var2) {
      Clip var3 = config;
      if (var3 != null) {
         try {
            if (var3.isRunning()) {
               var3.stop();
            }

            if (var3.isOpen()) {
               var3.close();
            }
         } catch (Throwable var5) {
         }
      }

      String var4 = "/assets/" + "wild" + "/sound/mp3/" + var0;
      data.submit(() -> {
         try (
            InputStream var3x = WildClient.class.getResourceAsStream(var4);
            BufferedInputStream var4x = var3x != null ? new BufferedInputStream(var3x) : null;
            AudioInputStream var5x = var4x != null ? AudioSystem.getAudioInputStream(var4x) : null;
         ) {
            if (var5x != null) {
               Clip var6 = AudioSystem.getClip();
               var6.open(var5x);
               config = var6;

               try {
                  FloatControl var7 = (FloatControl)var6.getControl(Type.MASTER_GAIN);
                  float var8 = var7.getMinimum();
                  float var9 = var7.getMaximum();
                  float var10 = (float)(var8 * (1.0 - var1 / 100.0) + var9 * (var1 / 100.0));
                  var7.setValue(var10);
               } catch (Throwable var14) {
               }

               if (var2) {
                  var6.addLineListener(var1xx -> {
                     if (var1xx.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        if (var6 == config) {
                           try {
                              var6.setFramePosition(0);
                              var6.start();
                           } catch (Throwable var4xx) {
                           }
                        } else {
                           try {
                              if (var6.isOpen()) {
                                 var6.close();
                              }
                           } catch (Throwable var3xx) {
                           }
                        }
                     }
                  });
               } else {
                  var6.addLineListener(var1xx -> {
                     if (var1xx.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        try {
                           if (var6.isOpen()) {
                              var6.close();
                           }
                        } catch (Throwable var3xx) {
                        }

                        if (var6 == config) {
                           config = null;
                        }
                     }
                  });
               }

               var6.start();
            } else {
               System.err.println("[SoundUtil] mp3 not found: " + var4);
            }
         } catch (Throwable var18) {
            System.err.println("[SoundUtil] mp3 error: " + var18);
         }
      });
   }

   public static void handle(String var0, float var1) {
      synchronized (context) {
         for (int var3 = context.size() - 1; var3 >= 0; var3--) {
            Clip var4 = context.get(var3);
            if (var4 == null) {
               context.remove(var3);
            } else if (!var4.isRunning()) {
               try {
                  if (var4.isOpen()) {
                     var4.close();
                  }
               } catch (Throwable var7) {
               }

               context.remove(var3);
            }
         }
      }

      Objects.requireNonNull(WildClient.instance);
      String var9 = "/assets/" + "wild" + "/sound/wav/" + var0 + ".wav";
      float var10 = var1;
      data.submit(() -> {
         try (
            InputStream var2 = AudioResourceManager.class.getResourceAsStream(var9);
            BufferedInputStream var3x = var2 != null ? new BufferedInputStream(var2) : null;
            AudioInputStream var4x = var3x != null ? AudioSystem.getAudioInputStream(var3x) : null;
         ) {
            if (var4x != null) {
               Clip var5 = AudioSystem.getClip();
               var5.open(var4x);
               float var6 = var10 < 0.0F ? 0.0F : Math.min(1.0F, var10);

               try {
                  FloatControl var7x = (FloatControl)var5.getControl(Type.MASTER_GAIN);
                  double var8 = var6 <= 0.0F ? -80.0 : Math.log(var6) / Math.log(10.0) * 20.0;
                  var7x.setValue((float)var8);
               } catch (Throwable var15) {
               }

               var5.addLineListener(var1xx -> {
                  if (var1xx.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                     try {
                        if (var5.isOpen()) {
                           var5.close();
                        }
                     } catch (Throwable var8x) {
                     }

                     List var2x = context;
                     synchronized (context){} // $VF: monitorenter 
                     boolean var6x = false /* VF: Semaphore variable */;

                     try {
                        var6x = true;
                        context.remove(var5);
                        var6x = false;
                     } finally {
                        if (var6x) {
                        }
                     }
                  }
               });
               synchronized (context) {
                  while (context.size() >= 16) {
                     Clip var22 = context.remove(0);

                     try {
                        if (var22.isRunning()) {
                           var22.stop();
                        }

                        if (var22.isOpen()) {
                           var22.close();
                        }
                     } catch (Throwable var14) {
                     }
                  }

                  context.add(var5);
               }

               var5.start();
            }
         } catch (Throwable var20) {
            System.err.println("[SoundUtil] wav error: " + var20);
         }
      });
   }

   static {
      Runtime.getRuntime().addShutdownHook(new Thread(AudioResourceManager::handle, "Wild-SoundUtil-Shutdown"));
   }
}
