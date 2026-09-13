package ru.wild.automation.combat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.WildClient;
import ru.wild.api.event.EntityAttackEvent;
import ru.wild.automation.RotationAnalyticsSnapshot;
import ru.wild.automation.RotationController;
import ru.wild.automation.RotationHeatmap;
import ru.wild.automation.RotationTrainingSampler;
import ru.wild.automation.RotationTrainingStatus;
import ru.wild.core.MinecraftContext;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.CombatRaycast;
import ru.wild.util.player.PlayerRaycast;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

public final class RotationRecorder implements MinecraftContext {
   private static final int instance = 3;
   private static final double data = 8.0;
   private static final double context = 0.14;
   private static final double config = 0.08;
   private static final Gson state = new GsonBuilder().setPrettyPrinting().create();
   private static final AtomicReference<RotationTrainingStatus> cache = new AtomicReference<>(RotationTrainingStatus.idle());
   private static final CopyOnWriteArrayList<Consumer<RotationTrainingStatus>> output = new CopyOnWriteArrayList<>();
   private static RotationRecorder.NetworkState current = new RotationRecorder.NetworkState();
   private static LivingEntity active;
   private static boolean mode;
   private static boolean renderer;
   private static boolean handler;
   private static boolean animationDraw;
   private static int pointEncode;
   private static int animator = -1;
   private static int source = -1;
   private static long target;
   private static long pending = -1L;
   private static int previous;
   private static int latest = Integer.MIN_VALUE;
   private static long summary;
   private static long matrixBlend;
   private static int vectorMatch;
   private static int itemProject;
   private static float responseCompute;
   private static float providerFetch;
   private static float profileDraw;
   private static float vectorPerform;
   private static double eventAttach;
   private static RotationRecorder.PrimaryNetworkState serverRead;
   private static final float positionAdvance = 0.85F;
   private static final float frameCheck = 0.3F;
   private static final float moduleCollect = 0.09F;
   private static final float providerClose = 0.05F;
   private static final float presetSave = 0.35F;
   private static final float windowConvert = 1.5F;
   private static final float presetWrite = 0.45F;
   private static final float colorMeasure = 38.0F;
   private static final float animationSchedule = 24.0F;
   private static final float rendererScan = 12.0F;
   private static final float sourceBuild = 8.0F;
   private static final float outputCollapse = 0.3F;
   private static float profileInvoke;
   private static float sourceSchedule;
   private static float timerRender;
   private static float scaleSave;
   private static String colorCompute = "default";
   private static final int scaleAdapt = 16;
   private static final int textureRun = 2;
   private static final int indexBind = 48;
   private static final int actionRead = 32;
   private static final int configCollapse = 2;
   private static final int dataValidate = 3;
   private static final float scaleRender = 180.0F;
   private static final float clientRefresh = 90.0F;
   private static final float keyFilter = 30.0F;
   private static final float requestAdapt = 6.0F;
   private static final float timerMeasure = 3.0F;
   private static final float vectorEncode = 0.6F;
   private static final float requestReceive = 10.0F;
   private static final float windowProcess = 0.55F;
   private static final float packetSave = 35.0F;
   private static final float entryAnimate = 18.0F;
   private static final float playerCollect = 0.5F;
   private static final float[] stateApply = new float[16];
   private static RotationHeatmap matrixFilter;
   private static boolean layerSample;
   private static volatile boolean worldSend;
   private static Thread targetWrite;
   private static float resultEncode;
   private static float messageParse;
   private static float providerRead;
   private static float matrixBlend2;
   private static float scalePerform;
   private static float contextExpand;
   private static int keyProcess;
   private static float actionConvert;
   private static float screenRead;
   private static final int[] animationExpand = new int[3];
   private static final int playerRun = 160;
   private static final float[] matrixRender = new float[160];
   private static final float[] moduleTick = new float[160];
   private static int playerCollapse;
   private static volatile boolean optionAdvance;
   private static volatile float[] effectScan;
   private static volatile float[] optionParse;
   private static volatile float pointSubmit = -1.0F;
   private static volatile int listenerPerform;
   private static volatile float configMatch;
   private static volatile float actionRender;
   private static volatile float playerApply;
   private static long bufferAdapt;
   private static int playerUpdate;

   private RotationRecorder() {
   }

   public static synchronized String handle() {
      if (toggleState.player != null && toggleState.world != null) {
         mode = true;
         renderer = false;
         current = new RotationRecorder.NetworkState();
         current.data = System.currentTimeMillis();
         current.state = savePreset();
         active = null;
         handler = false;
         animationDraw = false;
         pointEncode = 0;
         animator = -1;
         source = -1;
         target = 0L;
         pending = -1L;
         vectorMatch = 0;
         responseCompute = toggleState.player.getYaw();
         providerFetch = toggleState.player.getPitch();
         profileDraw = 0.0F;
         vectorPerform = 0.0F;
         eventAttach = toggleState.player.getVelocity().y;
         handle("TRAIN start profile=" + colorCompute + " sens=" + String.format(Locale.ROOT, "%.3f", current.state), false);
         resolve("AI recording: waiting target");
         return "Запись начата в профиль '" + colorCompute + "'. Ударьте игрока, моба или WildBot.";
      } else {
         return "Игрок не готов.";
      }
   }

   public static synchronized String process() {
      if (mode) {
         mode = false;
         active = null;
         if (current.cache.isEmpty()) {
            resolve("AI recording empty");
            return "Запись остановлена: паттерн пуст.";
         } else if (!collapseOutput()) {
            resolve("AI save failed");
            return "Не удалось сохранить паттерн.";
         } else {
            itemProject = current.cache.size();
            resolve("AI ready: " + itemProject + " frames");
            return "Профиль '" + colorCompute + "' сохранён: " + itemProject + " тиков, ударов: " + vectorMatch + ".";
         }
      } else if (renderer) {
         renderer = false;
         layerSample = false;
         animationDraw = false;
         serverRead = null;
         convertWindow();
         resolve("AI stopped");
         return "Воспроизведение остановлено.";
      } else {
         return "AI уже остановлен.";
      }
   }

   public static synchronized String compute() {
      if (mode) {
         return "Сначала завершите запись командой .ai stop.";
      }

      RotationHeatmap var0 = RotationHeatmap.process(scanRenderer());
      boolean var1 = var0 != null && !var0.handle(16, 2);
      if (var1) {
         var0 = null;
      }

      RotationRecorder.NetworkState var2 = invokeProfile();
      boolean var3 = var2 != null && var2.cache != null && !var2.cache.isEmpty();
      if (var0 == null && !var3) {
         resolve("AI pattern missing");
         return var1 ? "Модель устарела (новый формат). Переобучите: .ai learn." : "Нет модели и паттерна. Сначала .ai train, затем .ai learn.";
      }

      if (var3) {
         handle(var2);
         current = var2;
      } else {
         current = new RotationRecorder.NetworkState();
      }

      itemProject = current.cache.size();
      vectorMatch = process(current.cache);
      matrixFilter = var0;
      layerSample = var0 != null;
      convertWindow();
      renderer = true;
      previous = 0;
      latest = Integer.MIN_VALUE;
      animationDraw = false;
      summary = 0L;
      matrixBlend = 0L;
      serverRead = null;
      if (layerSample) {
         handle("RUN model profile=" + colorCompute, false);
         resolve("AI brain ready");
         return "Нейромодель профиля '" + colorCompute + "' запущена.";
      } else {
         handle("RUN replay profile=" + colorCompute + " frames=" + itemProject, false);
         resolve("AI ready: " + itemProject + " frames");
         return "Воспроизведение профиля '" + colorCompute + "' запущено: " + itemProject + " тиков (модель не обучена, .ai learn).";
      }
   }

   public static synchronized String resolve() {
      if (mode) {
         return "Сначала завершите запись командой .ai stop.";
      }

      if (worldSend) {
         return "Обучение уже идёт. Дождитесь завершения.";
      }

      RotationRecorder.NetworkState var0 = invokeProfile();
      if (var0 != null && var0.cache != null && var0.cache.size() >= 16) {
         handle(var0);
         List<RotationRecorder.PrimaryNetworkState> var1 = var0.cache;
         int var2 = var1.size();
         float[] var3 = new float[var2];
         float[] var4 = new float[var2];

         for (int var5 = 0; var5 < var2; var5++) {
            RotationRecorder.PrimaryNetworkState var6 = (RotationRecorder.PrimaryNetworkState)var1.get(var5);
            var3[var5] = var6 == null ? 0.0F : var6.enabled;
            var4[var5] = var6 == null ? 0.0F : var6.renderer;
         }

         float[] var34 = handle(var3, 2);
         float[] var35 = handle(var4, 2);
         float[] var7 = new float[var2];
         int var8 = 0;

         for (int var9 = 0; var9 < var2; var9++) {
            RotationRecorder.PrimaryNetworkState var10 = (RotationRecorder.PrimaryNetworkState)var1.get(var9);
            if (var10 != null) {
               var7[var8++] = (float)var10.vectorPerform;
            }
         }

         float[] var36 = Arrays.copyOf(var7, var8);
         Arrays.sort(var36);
         float var37 = handle(var36, 0.34F);
         float var11 = handle(var36, 0.67F);
         float var12 = var37;
         float var13 = var11 <= var37 ? var37 + 0.5F : var11;
         int var14 = 0;
         int var15 = 0;
         int var16 = 0;
         float var17 = 0.0F;

         for (RotationRecorder.PrimaryNetworkState var19 : var1) {
            if (var19 != null && var19.providerClose) {
               var14++;
               if (var19.presetSave) {
                  var15++;
               } else {
                  var16++;
                  var17 += Math.abs(var19.cache) + Math.abs(var19.output);
               }
            }
         }

         float var38 = var14 > 0 ? (float)(var14 - var15) / var14 : 0.0F;
         float var39 = var16 > 0 ? var17 / var16 : 0.0F;
         float var20 = var0.state;
         ArrayList var21 = new ArrayList();
         ArrayList var22 = new ArrayList();

         for (int var23 = 0; var23 < 3; var23++) {
            var21.add(new ArrayList());
            var22.add(new ArrayList());
         }

         for (int var40 = 0; var40 < var2; var40++) {
            RotationRecorder.PrimaryNetworkState var24 = (RotationRecorder.PrimaryNetworkState)var1.get(var40);
            if (var24 != null) {
               int var25 = handle(var24.vectorPerform, var12, var13);
               ((List)var21.get(var25)).add(var3[var40] - var34[var40]);
               ((List)var22.get(var25)).add(var4[var40] - var35[var40]);
            }
         }

         ArrayList<float[]> var41 = new ArrayList<>();
         ArrayList<float[]> var42 = new ArrayList<>();

         for (int var43 = 0; var43 < var2 - 1; var43++) {
            RotationRecorder.PrimaryNetworkState var26 = (RotationRecorder.PrimaryNetworkState)var1.get(var43);
            RotationRecorder.PrimaryNetworkState var27 = (RotationRecorder.PrimaryNetworkState)var1.get(var43 + 1);
            if (var26 != null && var27 != null) {
               float var28 = MathHelper.wrapDegrees(var26.config - var26.data);
               float var29 = var26.state - var26.context;
               float var30 = 0.0F;
               float var31 = 0.0F;
               if (var43 >= 1) {
                  RotationRecorder.PrimaryNetworkState var32 = (RotationRecorder.PrimaryNetworkState)var1.get(var43 - 1);
                  if (var32 != null) {
                     var30 = var32.enabled;
                     var31 = var32.renderer;
                  }
               }

               float[] var51 = new float[16];
               handle(
                  var51,
                  var28,
                  var29,
                  var26.enabled,
                  var26.renderer,
                  var30,
                  var31,
                  var26.vectorPerform,
                  var26.eventAttach,
                  var26.providerFetch,
                  var26.moduleCollect,
                  var26.serverRead,
                  var26.frameCheck,
                  var26.matrixBlend,
                  var26.summary,
                  var26.colorMeasure,
                  var26.sourceBuild
               );
               float[] var33 = new float[]{MathHelper.clamp(var3[var43 + 1] / 30.0F, -1.0F, 1.0F), MathHelper.clamp(var4[var43 + 1] / 30.0F, -1.0F, 1.0F)};
               var41.add(var51);
               var42.add(var33);
            }
         }

         if (var41.size() < 8) {
            return "Слишком мало пар для обучения.";
         }

         float[][] var44 = var41.toArray(new float[0][]);
         float[][] var45 = var42.toArray(new float[0][]);
         float[][] var46 = handle(var21);
         float[][] var47 = handle(var22);
         effectScan = process(var3, 160);
         optionParse = process(var4, 160);
         listenerPerform = var44.length;
         pointSubmit = -1.0F;
         int var48 = MathHelper.clamp(500000 / var44.length, 300, 1500);
         String var49 = colorCompute;
         Path var50 = scanRenderer();
         handle(
            String.format(
               Locale.ROOT,
               "LEARN pairs=%d frames=%d buckets=[%d,%d,%d] thr=[%.2f,%.2f] miss=%.0f%% sens=%.3f epochs=%d",
               var44.length,
               var2,
               var46[0].length,
               var46[1].length,
               var46[2].length,
               var12,
               var13,
               var38 * 100.0F,
               var20,
               var48
            ),
            true
         );
         worldSend = true;
         resolve("AI training: " + var44.length + " pairs");
         targetWrite = new Thread(() -> {
            RotationTrainingSampler var12x = new RotationTrainingSampler(16, 48, 32, 2);
            boolean var22x = false /* VF: Semaphore variable */;

            label77: {
               try {
                  var22x = true;
                  var12x.handle(var44, var45, var48, 0.002F);
                  float var13x = var12x.handle(var44, var45);
                  pointSubmit = var13x;
                  handle("LEARN done loss=" + String.format(Locale.ROOT, "%.5f", var13x), false);
                  RotationHeatmap var14x = new RotationHeatmap(16, 2, 3, var12x, var46, var47);
                  var14x.config = var12;
                  var14x.state = var13;
                  var14x.cache = var20;
                  var14x.output = var38;
                  var14x.current = var39;
                  boolean var15x = var14x.handle(var50);
                  synchronized (RotationRecorder.class) {
                     if (var15x && var49.equals(colorCompute)) {
                        matrixFilter = var14x;
                        layerSample = renderer;
                     }
                  }

                  resolve(var15x ? "AI brain ready (loss " + String.format(Locale.ROOT, "%.4f", var13x) + ")" : "AI train save failed");
                  var22x = false;
                  break label77;
               } catch (Throwable var24x) {
                  resolve("AI train failed");
                  var22x = false;
               } finally {
                  if (var22x) {
                     worldSend = false;
                  }
               }

               worldSend = false;
               return;
            }

            worldSend = false;
         }, "Wild-AI-Train");
         targetWrite.setDaemon(true);
         targetWrite.start();
         return "Обучение профиля '" + var49 + "' запущено в фоне: " + var44.length + " пар, эпох: " + var48 + ".";
      } else {
         return "Недостаточно данных (нужно >= 16 тиков). Сначала .ai train.";
      }
   }

   public static synchronized void handle(EntityAttackEvent var0) {
      if (var0 != null && var0.compute() instanceof LivingEntity var1 && var1 != toggleState.player) {
         if (mode) {
            if (active == null || active.getId() != var1.getId()) {
               active = var1;
               responseCompute = toggleState.player.getYaw();
               providerFetch = toggleState.player.getPitch();
               profileDraw = 0.0F;
               vectorPerform = 0.0F;
               eventAttach = toggleState.player.getVelocity().y;
            }

            long var4 = System.currentTimeMillis();
            handler = true;
            source = animator < 0 ? -1 : Math.max(0, pointEncode - animator);
            pending = target == 0L ? -1L : Math.max(0L, var4 - target);
            animator = pointEncode;
            target = var4;
            vectorMatch++;
            resolve("AI recording: " + current.cache.size() + " frames");
         }

         if (renderer) {
            animationDraw = false;
            summary = System.currentTimeMillis();
            matrixBlend = 0L;
            handle(String.format(Locale.ROOT, "ATTACK target=%d dist=%.2f", var1.getId(), toggleState.player.distanceTo(var1)), false);
         }
      }
   }

   public static synchronized void update() {
      if (mode && toggleState.player != null && toggleState.world != null && active != null && !active.isRemoved()) {
         Vec3d var0 = handle(active, toggleState.player.getYaw(), toggleState.player.getPitch());
         RotationAngles var1 = handle(var0);
         if (var1 != null) {
            float var2 = toggleState.player.getYaw();
            float var3 = toggleState.player.getPitch();
            Vec3d var4 = toggleState.player.getVelocity();
            PlayerInput var5 = toggleState.player.input == null ? PlayerInput.DEFAULT : toggleState.player.input.playerInput;
            RotationRecorder.PrimaryNetworkState var6 = new RotationRecorder.PrimaryNetworkState();
            var6.instance = pointEncode;
            var6.data = var2;
            var6.context = var3;
            var6.config = var1.instance;
            var6.state = var1.data;
            var6.cache = MathHelper.wrapDegrees(var2 - var1.instance);
            var6.output = var3 - var1.data;
            Box var7 = active.getBoundingBox();
            var6.current = process(var0.x, var7.minX, var7.maxX, 0.14);
            var6.active = process(var0.y, var7.minY, var7.maxY, 0.08);
            var6.mode = process(var0.z, var7.minZ, var7.maxZ, 0.14);
            var6.selection = true;
            var6.enabled = MathHelper.wrapDegrees(var2 - responseCompute);
            var6.renderer = var3 - providerFetch;
            var6.handler = var6.enabled - profileDraw;
            var6.animationDraw = var6.renderer - vectorPerform;
            var6.pointEncode = process(profileDraw, var6.enabled);
            var6.animator = process(vectorPerform, var6.renderer);
            var6.source = Math.abs(var6.enabled) < 0.035F && Math.abs(var6.renderer) < 0.035F;
            var6.target = (var5.forward() ? 1.0F : 0.0F) - (var5.backward() ? 1.0F : 0.0F);
            var6.pending = (var5.left() ? 1.0F : 0.0F) - (var5.right() ? 1.0F : 0.0F);
            var6.previous = var5.jump();
            var6.latest = var5.sneak();
            var6.summary = var5.sprint() || toggleState.player.isSprinting();
            var6.matrixBlend = toggleState.player.isOnGround();
            var6.vectorMatch = var4.x;
            var6.itemProject = var4.y;
            var6.responseCompute = var4.z;
            var6.providerFetch = Math.hypot(var4.x, var4.z);
            var6.profileDraw = var4.y - eventAttach;
            var6.vectorPerform = toggleState.player.distanceTo(active);
            var6.eventAttach = active.getY() - toggleState.player.getY();
            Vec3d var8 = resolve(active);
            var6.serverRead = var8.x;
            var6.positionAdvance = var8.y;
            var6.frameCheck = var8.z;
            var6.moduleCollect = Math.hypot(var8.x, var8.z);
            var6.providerClose = handler;
            var6.presetSave = handler && CombatRaycast.compute(var2, var3, toggleState.player.distanceTo(active) + 1.0, active, true);
            var6.windowConvert = handler ? source : -1;
            var6.presetWrite = handler ? pending : -1L;
            var6.colorMeasure = toggleState.player.getAttackCooldownProgress(0.5F);
            var6.animationSchedule = toggleState.player.handSwinging;
            var6.rendererScan = toggleState.player.handSwingProgress;
            var6.sourceBuild = active.hurtTime;
            current.cache.add(var6);
            handle(var6.enabled, var6.renderer, false);
            if (var6.providerClose) {
               handle(
                  String.format(
                     Locale.ROOT,
                     "%s point=(%.2f,%.2f,%.2f) dist=%.2f yawOff=%.2f pitchOff=%.2f int=%dt/%dms",
                     var6.presetSave ? "HIT" : "MISS",
                     var6.current,
                     var6.active,
                     var6.mode,
                     var6.vectorPerform,
                     var6.cache,
                     var6.output,
                     var6.windowConvert,
                     var6.presetWrite
                  ),
                  true
               );
            } else if ((var6.instance & 7) == 0) {
               handle(
                  String.format(
                     Locale.ROOT,
                     "REC t=%d aim=(%.2f,%.2f) yawD=%.2f pitchD=%.2f spd=%.3f dist=%.2f ground=%b sprint=%b",
                     var6.instance,
                     var6.current,
                     var6.active,
                     var6.enabled,
                     var6.renderer,
                     var6.providerFetch,
                     var6.vectorPerform,
                     var6.matrixBlend,
                     var6.summary
                  ),
                  true
               );
            }

            handler = false;
            source = -1;
            pending = -1L;
            responseCompute = var2;
            providerFetch = var3;
            profileDraw = var6.enabled;
            vectorPerform = var6.renderer;
            eventAttach = var4.y;
            pointEncode++;
            if ((pointEncode & 15) == 0) {
               resolve("AI recording: " + current.cache.size() + " frames");
            }
         }
      }
   }

   public static synchronized void handle(LivingEntity var0) {
      if (renderer && !mode && toggleState.player != null && toggleState.world != null && var0 != null) {
         if (layerSample && matrixFilter != null) {
            process(var0);
         } else if (current.cache != null && !current.cache.isEmpty()) {
            if (latest != var0.getId()) {
               latest = var0.getId();
               previous = ThreadLocalRandom.current().nextInt(current.cache.size());
               animationDraw = false;
               summary = 0L;
               matrixBlend = 0L;
               closeProvider();
            }

            RotationRecorder.PrimaryNetworkState var1 = current.cache.get(previous);
            serverRead = var1;
            if (var1.providerClose && !animationDraw) {
               animationDraw = true;
               long var2 = handle(var1);
               matrixBlend = summary == 0L ? System.currentTimeMillis() : summary + var2;
            }

            Vec3d var21 = handle(var0, var1);
            RotationAngles var3 = handle(var21);
            if (var3 == null) {
               collectModule();
            } else {
               float var4 = toggleState.player.getYaw();
               float var5 = toggleState.player.getPitch();
               boolean var6 = CombatRaycast.compute(var4, var5, Math.max(8.0, toggleState.player.distanceTo(var0) + 1.0), var0, true);
               float var7 = var6 ? 0.85F : 0.3F;
               float var8 = Math.abs(var1.enabled) + Math.abs(var1.renderer);
               float var9 = 0.09F + var8 * 0.05F;
               float var10 = handle(var9, true);
               float var11 = handle(var9, false);
               float var12 = MathHelper.clamp(var1.cache * var7 + var10, -12.0F, 12.0F);
               float var13 = MathHelper.clamp(var1.output * var7 + var11, -8.0F, 8.0F);
               timerRender = timerRender + (var12 - timerRender) * 0.3F;
               scaleSave = scaleSave + (var13 - scaleSave) * 0.3F;
               float var14 = var3.instance + timerRender;
               float var15 = MathHelper.clamp(var3.data + scaleSave, -90.0F, 90.0F);
               RotationAngles var16 = new RotationAngles(var14, var15);
               float var17;
               float var18;
               if (var6) {
                  var17 = Math.max(0.45F, process(var1.enabled, var1.source));
                  var18 = Math.max(0.45F, process(var1.renderer, var1.source));
               } else {
                  float var19 = Math.abs(MathHelper.wrapDegrees(var14 - var4));
                  float var20 = Math.abs(var15 - var5);
                  var17 = Math.min(var19, 38.0F);
                  var18 = Math.min(var20, 24.0F);
               }

               RotationController.handle(var16, var17, var18, 40.0F, 40.0F, 0, 15, false);
               collectModule();
               if ((previous & 15) == 0) {
                  resolve("AI replay: " + previous + "/" + current.cache.size());
               }
            }
         }
      }
   }

   private static void collectModule() {
      previous++;
      if (previous >= current.cache.size()) {
         previous = 0;
         closeProvider();
      }
   }

   private static void closeProvider() {
      profileInvoke = 0.0F;
      sourceSchedule = 0.0F;
      timerRender = 0.0F;
      scaleSave = 0.0F;
   }

   private static float handle(float var0, boolean var1) {
      float var2 = (ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F) * var0;
      if (var1) {
         profileInvoke = profileInvoke + (var2 - profileInvoke) * 0.35F;
         return MathHelper.clamp(profileInvoke, -1.5F, 1.5F);
      } else {
         sourceSchedule = sourceSchedule + (var2 - sourceSchedule) * 0.35F;
         return MathHelper.clamp(sourceSchedule, -1.5F, 1.5F);
      }
   }

   private static void process(LivingEntity var0) {
      if (latest != var0.getId()) {
         latest = var0.getId();
         convertWindow();
      }

      Vec3d var1 = compute(var0);
      RotationAngles var2 = handle(var1);
      if (var2 != null) {
         float var3 = toggleState.player.getYaw();
         float var4 = toggleState.player.getPitch();
         float var5 = MathHelper.wrapDegrees(var2.instance - var3);
         float var6 = var2.data - var4;
         Vec3d var7 = toggleState.player.getVelocity();
         double var8 = Math.hypot(var7.x, var7.z);
         Vec3d var10 = resolve(var0);
         double var11 = Math.hypot(var10.x, var10.z);
         double var13 = toggleState.player.distanceTo(var0);
         handle(
            stateApply,
            var5,
            var6,
            resultEncode,
            messageParse,
            providerRead,
            matrixBlend2,
            var13,
            var0.getY() - toggleState.player.getY(),
            var8,
            var11,
            var10.x,
            var10.z,
            toggleState.player.isOnGround(),
            toggleState.player.isSprinting(),
            toggleState.player.getAttackCooldownProgress(0.5F),
            var0.hurtTime
         );
         float[] var15 = matrixFilter.active.handle(stateApply);
         float var16 = var15[0] * 30.0F;
         float var17 = var15[1] * 30.0F;
         float var18 = matrixFilter.config > 0.0F ? matrixFilter.config : 1.6F;
         float var19 = matrixFilter.state > var18 ? matrixFilter.state : var18 + 0.8F;
         int var20 = handle(var13, var18, var19);
         float var21 = matrixFilter.process(var20, animationExpand[var20]);
         float var22 = matrixFilter.compute(var20, animationExpand[var20]);
         if (matrixFilter.handle(var20) > 0) {
            animationExpand[var20]++;
         }

         float var23 = MathHelper.clamp(AttackAura.latest.compute(), 0.0F, 2.0F);
         scalePerform = scalePerform + (var21 * var23 - scalePerform) * 0.55F;
         contextExpand = contextExpand + (var22 * var23 - contextExpand) * 0.55F;
         float var24 = MathHelper.clamp(var16 + scalePerform, -35.0F, 35.0F);
         float var25 = MathHelper.clamp(var17 + contextExpand, -35.0F, 35.0F);
         var24 = handle(var24, var5);
         var25 = handle(var25, var6);
         if (AttackAura.matrixBlend.compute() && matrixFilter.output > 0.001F) {
            if (keyProcess > 0) {
               var24 = MathHelper.clamp(var24 + actionConvert, -35.0F, 35.0F);
               var25 = MathHelper.clamp(var25 + screenRead, -35.0F, 35.0F);
               keyProcess--;
            } else if (ThreadLocalRandom.current().nextFloat() < matrixFilter.output * 0.015F) {
               float var26 = Math.max(2.0F, matrixFilter.current);
               actionConvert = (ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F) * var26 * 0.5F;
               screenRead = (ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F) * var26 * 0.3F;
               keyProcess = ThreadLocalRandom.current().nextInt(2, 5);
            }
         }

         providerRead = resultEncode;
         matrixBlend2 = messageParse;
         resultEncode = var24;
         messageParse = var25;
         handle(var24, var25, true);
         configMatch = var5;
         actionRender = var6;
         playerApply = Math.abs(scalePerform) + Math.abs(contextExpand);
         if ((++playerUpdate & 7) == 0) {
            handle(
               String.format(
                  Locale.ROOT,
                  "NN err=(%.2f,%.2f) mean=(%.2f,%.2f) jit=(%.2f,%.2f) delta=(%.2f,%.2f) dist=%.2f bucket=%d",
                  var5,
                  var6,
                  var16,
                  var17,
                  scalePerform,
                  contextExpand,
                  var24,
                  var25,
                  var13,
                  var20
               ),
               true
            );
         }

         float var32 = var3 + var24;
         float var27 = MathHelper.clamp(var4 + var25, -90.0F, 90.0F);
         float var28 = Math.max(0.25F, Math.abs(var24));
         float var29 = Math.max(0.2F, Math.abs(var25));
         RotationController.handle(new RotationAngles(var32, var27), var28, var29, 40.0F, 40.0F, 0, 15, false);
      }
   }

   private static Vec3d compute(LivingEntity var0) {
      Vec3d var1 = PlayerRaycast.handle(var0.getBoundingBox(), false);
      return var1 != null ? var1 : handle(var0.getBoundingBox(), var0.getBoundingBox().getCenter());
   }

   private static float[] handle(float[] var0, int var1) {
      int var2 = var0.length;
      float[] var3 = new float[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         int var5 = Math.max(0, var4 - var1);
         int var6 = Math.min(var2 - 1, var4 + var1);
         float var7 = 0.0F;

         for (int var8 = var5; var8 <= var6; var8++) {
            var7 += var0[var8];
         }

         var3[var4] = var7 / (var6 - var5 + 1);
      }

      return var3;
   }

   private static int handle(double var0, float var2, float var3) {
      if (var0 < var2) {
         return 0;
      } else {
         return var0 < var3 ? 1 : 2;
      }
   }

   private static float handle(float[] var0, float var1) {
      if (var0.length == 0) {
         return 0.0F;
      }

      int var2 = MathHelper.clamp((int)(var1 * var0.length), 0, var0.length - 1);
      return var0[var2];
   }

   private static float savePreset() {
      try {
         return (float)((Double)toggleState.options.getMouseSensitivity().getValue()).doubleValue();
      } catch (Throwable var1) {
         return -1.0F;
      }
   }

   private static int handle(float var0, float var1, int var2) {
      float var3 = (var0 + var1) / (2.0F * var1);
      return MathHelper.clamp((int)(var3 * var2), 0, var2 - 1);
   }

   public static synchronized RotationAnalyticsSnapshot apply() {
      RotationAnalyticsSnapshot var0 = new RotationAnalyticsSnapshot();
      var0.data = colorCompute;
      RotationRecorder.NetworkState var1 = invokeProfile();
      if (var1 != null && var1.cache != null && var1.cache.size() >= 4) {
         handle(var1);
         List<RotationRecorder.PrimaryNetworkState> var2 = var1.cache;
         int var3 = var2.size();
         var0.context = var3;
         var0.config = process(var2);
         var0.current = var1.state;
         int var4 = 0;

         for (RotationRecorder.PrimaryNetworkState var6 : var2) {
            if (var6 != null && var6.providerClose && var6.presetSave) {
               var4++;
            }
         }

         var0.state = var4;
         var0.cache = Math.max(0, var0.config - var4);
         var0.output = var0.config > 0 ? (float)var0.cache / var0.config : 0.0F;
         float[] var31 = new float[var3];
         int var32 = 0;
         float var7 = Float.MAX_VALUE;
         float var8 = 0.0F;

         for (RotationRecorder.PrimaryNetworkState var10 : var2) {
            if (var10 != null) {
               float var11 = (float)var10.vectorPerform;
               var31[var32++] = var11;
               if (var11 < var7) {
                  var7 = var11;
               }

               if (var11 > var8) {
                  var8 = var11;
               }
            }
         }

         float[] var33 = Arrays.copyOf(var31, var32);
         Arrays.sort(var33);
         var0.selection = handle(var33, 0.34F);
         var0.enabled = handle(var33, 0.67F);
         if (var0.enabled <= var0.selection) {
            var0.enabled = var0.selection + 0.5F;
         }

         var0.active = var7 == Float.MAX_VALUE ? 0.0F : var7;
         var0.mode = var8;
         byte var34 = 20;
         var0.handler = var34;
         float[] var35 = new float[var34];
         int[] var12 = new int[var34];
         byte var13 = 21;
         var0.source = new int[var13];
         var0.target = new int[var13];
         var0.animator = 25.0F;
         float var14 = var0.mode - var0.active;
         if (var14 < 0.001F) {
            var14 = 1.0F;
         }

         for (RotationRecorder.PrimaryNetworkState var16 : var2) {
            if (var16 != null) {
               int var17 = handle(var16.vectorPerform, var0.selection, var0.enabled);
               var0.renderer[var17]++;
               float var18 = Math.abs(var16.enabled) + Math.abs(var16.renderer);
               int var19 = MathHelper.clamp((int)(((float)var16.vectorPerform - var0.active) / var14 * var34), 0, var34 - 1);
               var35[var19] += var18;
               var12[var19]++;
               var0.source[handle(var16.enabled, var0.animator, var13)]++;
               var0.target[handle(var16.renderer, var0.animator, var13)]++;
               if (Math.abs(var16.enabled) > 8.0F) {
                  var0.latest++;
               } else {
                  var0.summary++;
               }
            }
         }

         var0.animationDraw = new float[var34];
         float var36 = 0.0F;

         for (int var37 = 0; var37 < var34; var37++) {
            var0.animationDraw[var37] = var12[var37] > 0 ? var35[var37] / var12[var37] : 0.0F;
            if (var0.animationDraw[var37] > var36) {
               var36 = var0.animationDraw[var37];
            }
         }

         var0.pointEncode = var36;
         int var38 = 1;
         int var39 = 1;

         for (int var40 = 0; var40 < var13; var40++) {
            if (var0.source[var40] > var38) {
               var38 = var0.source[var40];
            }

            if (var0.target[var40] > var39) {
               var39 = var0.target[var40];
            }
         }

         var0.pending = var38;
         var0.previous = var39;
         float[] var41 = new float[var3];
         float[] var42 = new float[var3];

         for (int var20 = 0; var20 < var3; var20++) {
            RotationRecorder.PrimaryNetworkState var21 = (RotationRecorder.PrimaryNetworkState)var2.get(var20);
            var41[var20] = var21 == null ? 0.0F : var21.enabled;
            var42[var20] = var21 == null ? 0.0F : var21.renderer;
         }

         var0.matrixBlend = process(var41, 160);
         var0.vectorMatch = process(var42, 160);
         RotationHeatmap var43 = RotationHeatmap.process(scanRenderer());
         if (var43 != null && var43.handle(16, 2)) {
            var0.providerFetch = true;
            var0.profileDraw = pointSubmit;
            float[] var44 = new float[var3];
            float[] var22 = new float[var3];
            float[] var23 = new float[16];

            for (int var24 = 0; var24 < var3 - 1; var24++) {
               RotationRecorder.PrimaryNetworkState var25 = (RotationRecorder.PrimaryNetworkState)var2.get(var24);
               if (var25 != null) {
                  float var26 = MathHelper.wrapDegrees(var25.config - var25.data);
                  float var27 = var25.state - var25.context;
                  float var28 = 0.0F;
                  float var29 = 0.0F;
                  if (var24 >= 1) {
                     RotationRecorder.PrimaryNetworkState var30 = (RotationRecorder.PrimaryNetworkState)var2.get(var24 - 1);
                     if (var30 != null) {
                        var28 = var30.enabled;
                        var29 = var30.renderer;
                     }
                  }

                  handle(
                     var23,
                     var26,
                     var27,
                     var25.enabled,
                     var25.renderer,
                     var28,
                     var29,
                     var25.vectorPerform,
                     var25.eventAttach,
                     var25.providerFetch,
                     var25.moduleCollect,
                     var25.serverRead,
                     var25.frameCheck,
                     var25.matrixBlend,
                     var25.summary,
                     var25.colorMeasure,
                     var25.sourceBuild
                  );
                  float[] var45 = var43.active.handle(var23);
                  var44[var24] = var45[0] * 30.0F;
                  var22[var24] = var45[1] * 30.0F;
               }
            }

            var0.itemProject = process(var44, 160);
            var0.responseCompute = process(var22, 160);
         }

         var0.instance = true;
         return var0;
      } else {
         var0.instance = false;
         return var0;
      }
   }

   private static float handle(float var0, float var1) {
      if (Math.abs(var1) < 18.0F) {
         return var0;
      }

      boolean var2 = Math.signum(var0) != Math.signum(var1);
      boolean var3 = Math.abs(var0) < 1.0F;
      return !var2 && !var3 ? var0 : MathHelper.clamp(var1 * 0.5F, -35.0F, 35.0F);
   }

   private static float[][] handle(List<List<Float>> var0) {
      float[][] var1 = new float[var0.size()][];

      for (int var2 = 0; var2 < var0.size(); var2++) {
         List var3 = (List)var0.get(var2);
         float[] var4 = new float[var3.size()];

         for (int var5 = 0; var5 < var4.length; var5++) {
            var4[var5] = (Float)var3.get(var5);
         }

         var1[var2] = var4;
      }

      return var1;
   }

   private static float[] process(float[] var0, int var1) {
      float[] var2 = new float[var1];
      int var3 = var0.length;
      if (var3 == 0) {
         return var2;
      }

      for (int var4 = 0; var4 < var1; var4++) {
         int var5 = (int)((long)var4 * var3 / var1);
         if (var5 >= var3) {
            var5 = var3 - 1;
         }

         var2[var4] = var0[var5];
      }

      return var2;
   }

   private static void handle(float var0, float var1, boolean var2) {
      matrixRender[playerCollapse] = var0;
      moduleTick[playerCollapse] = var1;
      playerCollapse = (playerCollapse + 1) % 160;
      optionAdvance = var2;
   }

   public static int execute() {
      return 160;
   }

   public static float[] prepare() {
      return matrixRender;
   }

   public static float[] check() {
      return moduleTick;
   }

   public static int onTick() {
      return playerCollapse;
   }

   public static boolean select() {
      return optionAdvance;
   }

   public static float[] refresh() {
      return effectScan;
   }

   public static float[] render() {
      return optionParse;
   }

   public static float tick() {
      return pointSubmit;
   }

   public static int drawAnimation() {
      return listenerPerform;
   }

   public static float encodePoint() {
      return configMatch;
   }

   public static float animate() {
      return actionRender;
   }

   public static float load() {
      return playerApply;
   }

   public static boolean save() {
      return layerSample && matrixFilter != null;
   }

   public static boolean submit() {
      return AttackAura.summary.compute();
   }

   public static Path unload() {
      return drawProfile().resolve("logs").resolve(handle(colorCompute) + ".log");
   }

   private static void handle(String var0, boolean var1) {
      if (AttackAura.summary.compute()) {
         long var2 = System.currentTimeMillis();
         String var4 = "[AI] " + var0;

         try {
            Path var5 = unload();
            Files.createDirectories(var5.getParent());
            Files.writeString(var5, var2 + " " + var4 + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (Throwable var6) {
         }

         if (var1 && var2 - bufferAdapt >= 1500L) {
            bufferAdapt = var2;
            ChatLogger.handle(var4);
         }
      }
   }

   private static void handle(
      float[] var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      boolean var19,
      boolean var20,
      float var21,
      float var22
   ) {
      var0[0] = MathHelper.clamp(var1 / 180.0F, -1.0F, 1.0F);
      var0[1] = MathHelper.clamp(var2 / 90.0F, -1.0F, 1.0F);
      var0[2] = MathHelper.clamp(var3 / 30.0F, -1.0F, 1.0F);
      var0[3] = MathHelper.clamp(var4 / 30.0F, -1.0F, 1.0F);
      var0[4] = MathHelper.clamp(var5 / 30.0F, -1.0F, 1.0F);
      var0[5] = MathHelper.clamp(var6 / 30.0F, -1.0F, 1.0F);
      var0[6] = MathHelper.clamp((float)(var7 / 6.0), 0.0F, 1.5F);
      var0[7] = MathHelper.clamp((float)(var9 / 3.0), -1.0F, 1.0F);
      var0[8] = MathHelper.clamp((float)(var11 / 0.6F), 0.0F, 1.5F);
      var0[9] = MathHelper.clamp((float)(var13 / 0.6F), 0.0F, 1.5F);
      var0[10] = MathHelper.clamp((float)(var15 / 0.6F), -1.5F, 1.5F);
      var0[11] = MathHelper.clamp((float)(var17 / 0.6F), -1.5F, 1.5F);
      var0[12] = var19 ? 1.0F : 0.0F;
      var0[13] = var20 ? 1.0F : 0.0F;
      var0[14] = MathHelper.clamp(var21, 0.0F, 1.0F);
      var0[15] = MathHelper.clamp(var22 / 10.0F, 0.0F, 1.0F);
   }

   public static synchronized boolean fetch() {
      if (!renderer || mode) {
         return false;
      } else if (layerSample && matrixFilter != null) {
         return toggleState.player != null && toggleState.player.getAttackCooldownProgress(0.0F) >= 0.9F;
      } else if (serverRead == null) {
         return false;
      } else {
         return vectorMatch == 0
            ? toggleState.player != null && toggleState.player.getAttackCooldownProgress(0.0F) >= 0.92F
            : animationDraw && System.currentTimeMillis() >= matrixBlend;
      }
   }

   public static synchronized void measure() {
      previous = 0;
      latest = Integer.MIN_VALUE;
      animationDraw = false;
      summary = 0L;
      matrixBlend = 0L;
      serverRead = null;
      convertWindow();
      closeProvider();
   }

   private static void convertWindow() {
      resultEncode = 0.0F;
      messageParse = 0.0F;
      providerRead = 0.0F;
      matrixBlend2 = 0.0F;
      scalePerform = 0.0F;
      contextExpand = 0.0F;
      keyProcess = 0;
      actionConvert = 0.0F;
      screenRead = 0.0F;

      for (int var0 = 0; var0 < animationExpand.length; var0++) {
         animationExpand[var0] = 0;
      }
   }

   public static synchronized void blendMatrix() {
      if (mode && current.cache != null && !current.cache.isEmpty()) {
         collapseOutput();
      }

      mode = false;
      renderer = false;
   }

   public static boolean matchVector() {
      return mode;
   }

   public static boolean projectItem() {
      return renderer;
   }

   public static RotationTrainingStatus computeResponse() {
      return cache.get();
   }

   public static String fetchProvider() {
      return cache.get().text();
   }

   public static void handle(Consumer<RotationTrainingStatus> var0) {
      if (var0 != null) {
         output.add(var0);
         var0.accept(cache.get());
      }
   }

   public static void process(Consumer<RotationTrainingStatus> var0) {
      output.remove(var0);
   }

   public static Path drawProfile() {
      return WildClient.instance != null && WildClient.instance.cache != null
         ? WildClient.instance.cache.toPath().resolve("AI")
         : toggleState.runDirectory.toPath().resolve("Wild").resolve("AI");
   }

   private static Vec3d handle(LivingEntity var0, float var1, float var2) {
      Vec3d var3 = toggleState.player.getEyePos();
      Vec3d var4 = CombatRaycast.handle(var2, var1);
      Box var5 = var0.getBoundingBox();
      Optional var6 = var5.expand(0.05).raycast(var3, var3.add(var4.multiply(8.0)));
      if (var6.isPresent()) {
         return handle(var5, (Vec3d)var6.get());
      }

      Vec3d var7 = var5.getCenter();
      double var8 = Math.max(0.1, var7.subtract(var3).dotProduct(var4));
      Vec3d var10 = var3.add(var4.multiply(var8));
      return handle(var5, var10);
   }

   private static Vec3d handle(LivingEntity var0, RotationRecorder.PrimaryNetworkState var1) {
      Box var2 = var0.getBoundingBox();
      double var3;
      double var5;
      double var7;
      if (var1.selection) {
         var3 = handle(var1.current, 0.14);
         var5 = handle(var1.active, 0.08);
         var7 = handle(var1.mode, 0.14);
      } else {
         var3 = 0.5;
         var5 = MathHelper.clamp(0.5 + var1.output / 180.0, 0.25, 0.75);
         var7 = 0.5;
      }

      Vec3d var9 = resolve(var0);
      double var10 = MathHelper.clamp(toggleState.player.distanceTo(var0) / 4.0, 0.25, 0.85);
      var3 += var9.x * var10 / Math.max(0.01, var2.getLengthX());
      var5 += var9.y * var10 / Math.max(0.01, var2.getLengthY());
      var7 += var9.z * var10 / Math.max(0.01, var2.getLengthZ());
      var3 = handle(var3, 0.14);
      var5 = handle(var5, 0.08);
      var7 = handle(var7, 0.14);
      return new Vec3d(MathHelper.lerp(var3, var2.minX, var2.maxX), MathHelper.lerp(var5, var2.minY, var2.maxY), MathHelper.lerp(var7, var2.minZ, var2.maxZ));
   }

   private static RotationAngles handle(Vec3d var0) {
      if (var0 != null && toggleState.player != null) {
         Vec3d var1 = var0.subtract(toggleState.player.getEyePos());
         if (var1.lengthSquared() < 1.0E-8) {
            return null;
         }

         float var2 = (float)Math.toDegrees(Math.atan2(-var1.x, var1.z));
         float var3 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var1.y, Math.hypot(var1.x, var1.z))), -90.0, 90.0);
         return new RotationAngles(var2, var3);
      } else {
         return null;
      }
   }

   private static Vec3d handle(Box var0, Vec3d var1) {
      return new Vec3d(handle(var1.x, var0.minX, var0.maxX, 0.14), handle(var1.y, var0.minY, var0.maxY, 0.08), handle(var1.z, var0.minZ, var0.maxZ, 0.14));
   }

   private static double handle(double var0, double var2, double var4, double var6) {
      double var8 = var4 - var2;
      if (var8 <= 1.0E-6) {
         return var2;
      }

      double var10 = var8 * var6;
      return MathHelper.clamp(var0, var2 + var10, var4 - var10);
   }

   private static double process(double var0, double var2, double var4, double var6) {
      double var8 = var4 - var2;
      return var8 <= 1.0E-6 ? 0.5 : handle((var0 - var2) / var8, var6);
   }

   private static double handle(double var0, double var2) {
      return MathHelper.clamp(var0, var2, 1.0 - var2);
   }

   private static Vec3d resolve(LivingEntity var0) {
      Vec3d var1 = var0.getVelocity();
      Vec3d var2 = new Vec3d(var0.getX() - var0.lastX, var0.getY() - var0.lastY, var0.getZ() - var0.lastZ);
      return var2.lengthSquared() > var1.lengthSquared() ? var2 : var1;
   }

   private static boolean process(float var0, float var1) {
      return Math.abs(var0) > 0.02F && Math.abs(var1) > 0.02F && Math.signum(var0) != Math.signum(var1);
   }

   private static float process(float var0, boolean var1) {
      float var2 = Math.abs(var0);
      return var1 ? 0.0F : var2;
   }

   private static long handle(RotationRecorder.PrimaryNetworkState var0) {
      if (var0.presetWrite > 0L) {
         return var0.presetWrite;
      } else {
         return var0.windowConvert > 0 ? var0.windowConvert * 50L : 0L;
      }
   }

   private static Path writePreset() {
      return drawProfile().resolve("profiles");
   }

   private static Path measureColor() {
      return writePreset().resolve(handle(colorCompute) + ".json");
   }

   private static Path scheduleAnimation() {
      return drawProfile().resolve("models");
   }

   private static Path scanRenderer() {
      return scheduleAnimation().resolve(handle(colorCompute) + ".json");
   }

   public static boolean performVector() {
      return Files.isRegularFile(scanRenderer());
   }

   public static boolean attachEvent() {
      return worldSend;
   }

   private static Path buildSource() {
      return drawProfile().resolve("rotation_pattern.json");
   }

   static String handle(String var0) {
      String var1 = var0 != null && !var0.isBlank() ? var0.trim() : "default";
      var1 = var1.replace('\\', '/');
      int var2 = var1.lastIndexOf(47);
      if (var2 >= 0) {
         var1 = var1.substring(var2 + 1);
      }

      if (var1.endsWith(".json")) {
         var1 = var1.substring(0, var1.length() - 5);
      }

      var1 = var1.replaceAll("[^a-zA-Z0-9._-]", "_");
      if (var1.isBlank() || var1.equals(".") || var1.equals("..")) {
         var1 = "default";
      }

      return var1;
   }

   private static String compute(String var0) {
      return var0 != null && var0.endsWith(".json") ? var0.substring(0, var0.length() - 5) : var0;
   }

   public static String readServer() {
      return colorCompute;
   }

   public static synchronized String process(String var0) {
      if (mode) {
         return "Нельзя менять профиль во время записи (.ai stop сначала).";
      }

      colorCompute = handle(var0);
      resolve("AI profile: " + colorCompute);
      return "Активный профиль: " + colorCompute;
   }

   public static List<String> advancePosition() {
      ArrayList<String> var0 = new ArrayList<>();

      try {
         Path var1 = writePreset();
         if (Files.isDirectory(var1)) {
            try (Stream<Path> var2 = Files.list(var1)) {
               var2.filter(var0x -> Files.isRegularFile(var0x) && var0x.getFileName().toString().endsWith(".json"))
                  .forEach(var1x -> var0.add(compute(var1x.getFileName().toString())));
            }
         }
      } catch (Throwable var7) {
      }

      var0.sort(String::compareToIgnoreCase);
      return var0;
   }

   public static synchronized String checkFrame() {
      List<String> var0 = advancePosition();
      return var0.isEmpty()
         ? "Профили не найдены. Активный: " + colorCompute
         : "Профили (" + var0.size() + "): " + String.join(", ", var0) + " | активный: " + colorCompute;
   }

   private static boolean collapseOutput() {
      try {
         handle(current);
         Path var0 = measureColor();
         Files.createDirectories(var0.getParent());

         try (BufferedWriter var1 = Files.newBufferedWriter(var0, StandardCharsets.UTF_8)) {
            state.toJson(current, var1);
         }

         return true;
      } catch (Throwable var6) {
         return false;
      }
   }

   private static RotationRecorder.NetworkState invokeProfile() {
      try {
         Path var0 = measureColor();
         if (!Files.isRegularFile(var0)) {
            Path var1 = buildSource();
            if (!"default".equals(handle(colorCompute)) || !Files.isRegularFile(var1)) {
               return null;
            }

            var0 = var1;
         }

         try (BufferedReader var7 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            return (RotationRecorder.NetworkState)state.fromJson(var7, RotationRecorder.NetworkState.class);
         }
      } catch (Throwable var6) {
         return null;
      }
   }

   private static void handle(RotationRecorder.NetworkState var0) {
      int var1 = var0.instance;
      var0.instance = 3;
      if (var0.cache == null) {
         var0.cache = new ArrayList<>();
      }

      if (var1 < 2) {
         for (RotationRecorder.PrimaryNetworkState var3 : var0.cache) {
            if (var3 != null) {
               var3.selection = false;
            }
         }
      }

      if (var1 < 3) {
         float var8 = 0.0F;
         float var9 = 0.0F;
         double var4 = 0.0;

         for (RotationRecorder.PrimaryNetworkState var7 : var0.cache) {
            if (var7 != null) {
               var7.handler = var7.enabled - var8;
               var7.animationDraw = var7.renderer - var9;
               var7.profileDraw = var7.itemProject - var4;
               var7.pointEncode = process(var8, var7.enabled);
               var7.animator = process(var9, var7.renderer);
               var7.source = Math.abs(var7.enabled) < 0.035F && Math.abs(var7.renderer) < 0.035F;
               if (var7.presetWrite <= 0L && var7.windowConvert > 0) {
                  var7.presetWrite = var7.windowConvert * 50L;
               }

               var8 = var7.enabled;
               var9 = var7.renderer;
               var4 = var7.itemProject;
            }
         }
      }

      var0.context = var0.cache.size();
      var0.config = process(var0.cache);
   }

   private static int process(List<RotationRecorder.PrimaryNetworkState> var0) {
      int var1 = 0;
      if (var0 != null) {
         for (RotationRecorder.PrimaryNetworkState var3 : var0) {
            if (var3 != null && var3.providerClose) {
               var1++;
            }
         }
      }

      return var1;
   }

   private static void resolve(String var0) {
      long var1 = current.cache == null ? 0L : current.cache.size();
      RotationTrainingStatus var3 = new RotationTrainingStatus(var0, mode, worldSend, var1, itemProject, 0L, System.currentTimeMillis());
      cache.set(var3);

      for (Consumer var5 : output) {
         try {
            var5.accept(var3);
         } catch (Throwable var7) {
         }
      }
   }

   static final class NetworkState {
      int instance = 3;
      long data;
      int context;
      int config;
      float state;
      List<RotationRecorder.PrimaryNetworkState> cache = new ArrayList<>();
   }

   static final class PrimaryNetworkState {
      int instance;
      float data;
      float context;
      float config;
      float state;
      float cache;
      float output;
      double current;
      double active;
      double mode;
      boolean selection;
      float enabled;
      float renderer;
      float handler;
      float animationDraw;
      boolean pointEncode;
      boolean animator;
      boolean source;
      float target;
      float pending;
      boolean previous;
      boolean latest;
      boolean summary;
      boolean matrixBlend;
      double vectorMatch;
      double itemProject;
      double responseCompute;
      double providerFetch;
      double profileDraw;
      double vectorPerform;
      double eventAttach;
      double serverRead;
      double positionAdvance;
      double frameCheck;
      double moduleCollect;
      boolean providerClose;
      boolean presetSave;
      int windowConvert;
      long presetWrite;
      float colorMeasure;
      boolean animationSchedule;
      float rendererScan;
      int sourceBuild;
   }
}
