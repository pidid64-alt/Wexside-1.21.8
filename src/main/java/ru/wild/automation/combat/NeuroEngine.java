package ru.wild.automation.combat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.wild.automation.RotationController;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.modules.combat.AttackAura;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.text.ChatLogger;

public final class NeuroEngine implements MinecraftContext {
   private static final int instance = 18;
   private static final long data = 700L;
   private static final float context = 34.0F;
   private static final float config = 22.0F;
   private static final RotationProfileRepository state = new RotationProfileRepository();
   private static RotationProfileDataset.CacheEntry cache;
   private static final NeuroEngine.ColorState output = new NeuroEngine.ColorState();
   private static int current = -1;
   private static int active;
   private static int mode;
   private static String renderer = "Neuro idle";
   private static long handler;
   private static boolean animationDraw;

   private NeuroEngine() {
   }

   public static void handle(LivingEntity var0, boolean var1, boolean var2) {
      handle(var0, var1, var2, false);
   }

   public static void handle(LivingEntity var0, boolean var1, boolean var2, boolean var3) {
      if (toggleState.player != null && toggleState.world != null && var0 != null) {
         mode++;
         handle(var0);
         RotationAngles var4 = handle(var0, output);
         RotationAngles var5 = new RotationAngles(toggleState.player);
         float var6 = MathHelper.wrapDegrees(var4.instance - var5.instance);
         float var7 = var4.data - var5.data;
         float var8 = (float)Math.hypot(var6, var7);
         boolean var9 = var1 && !var2;
         handle(var8, var6, var7, var9, var2);
         if (handle(var0, var8, var9)) {
            handle(var0, var6, var7, var9);
         }

         RotationProfileDataset.PrimaryCacheEntry var10 = resolve();
         float var11 = process(var8, var9, var2, var10 != null);
         float var12 = handle(var10, var8, var9, var2);
         NeuroEngine.DataRecord var13 = handle(var10, var6, var7, var8, var11, var9);
         NeuroEngine.PrimaryDataRecord var14 = handle(var6, var7, var13, var8, var12, var9, var2);
         float var15 = var14.targetYawVelocity;
         float var16 = var14.targetPitchVelocity;
         float var17 = var14.yaw;
         float var18 = var14.pitch;
         float var19 = var5.instance + var17;
         float var20 = MathHelper.clamp(var5.data + var18, -90.0F, 90.0F);
         float var21 = Math.max(0.18F, Math.abs(var17));
         float var22 = Math.max(0.14F, Math.abs(var18));
         animationDraw = RotationController.cache <= 18;
         RotationController.handle(new RotationAngles(var19, var20), var21, var22, 30.0F, 30.0F, 1, 18, false);
         if (var10 != null) {
            active++;
         }

         output.source = var6;
         output.target = var7;
         output.state = true;
         renderer = "Neuro humanize " + state.handle() + "p";
         handle(
            var3,
            "aim target="
               + var0.getId()
               + " type="
               + update()
               + " sample="
               + active
               + "/"
               + apply()
               + " yawErr="
               + process(var6)
               + " pitchErr="
               + process(var7)
               + " yawBase="
               + process(var15)
               + " pitchBase="
               + process(var16)
               + " humanYaw="
               + process(var13.yaw)
               + " humanPitch="
               + process(var13.pitch)
               + " yawStep="
               + process(var17)
               + " pitchStep="
               + process(var18)
               + " speed="
               + process(var12)
               + " focus="
               + process(output.output)
               + "/"
               + process(output.active)
               + "/"
               + process(output.current)
               + " cfg="
               + process(execute())
               + "/"
               + process(onTick())
               + " hold="
               + output.config
               + " attack="
               + var9
               + " blocked="
               + var2
         );
      } else {
         handle(AttackAura.moduleCollect.compute());
         renderer = "Neuro idle";
         handle(var3, "idle target=" + (var0 == null ? "null" : var0.getId()));
      }
   }

   public static void handle() {
      cache = null;
      current = -1;
      active = 0;
      output.handle();
      animationDraw = false;
      renderer = "Neuro reset";
   }

   public static void handle(boolean var0) {
      if (!animationDraw) {
         handle();
      } else {
         if (toggleState.player != null) {
            ViewRotationCoordinator.context = toggleState.player.getYaw();
            ViewRotationCoordinator.config = toggleState.player.getPitch();
            if (var0) {
               toggleState.player.setYaw(toggleState.player.getYaw());
               toggleState.player.setPitch(toggleState.player.getPitch());
               toggleState.player.headYaw = toggleState.player.getYaw();
            }
         }

         RotationController.instance = RotationController.Mode.IDLE;
         RotationController.cache = 0;
         RotationController.mode = false;
         RotationController.active = null;
         RotationController.current = 0;
         ViewRotationCoordinator.instance = var0 ? false : ViewRotationCoordinator.data;
         handle();
      }
   }

   public static void process() {
      handle();
   }

   public static String compute() {
      return renderer + " / " + state.process();
   }

   private static boolean handle(LivingEntity var0, float var1, boolean var2) {
      if (state.handle() == 0) {
         cache = null;
         current = -1;
         active = 0;
         return false;
      } else if (cache == null || var0.getId() != current) {
         return true;
      } else if (cache.enabled == null || active >= cache.enabled.size()) {
         return true;
      } else {
         return var2 && !"Attack".equalsIgnoreCase(cache.instance) && active > 2 ? true : var1 < 5.0F && "Flick".equalsIgnoreCase(cache.instance) && active > 2;
      }
   }

   private static void handle(LivingEntity var0, float var1, float var2, boolean var3) {
      cache = state.handle(MathHelper.clamp(var1, -45.0F, 45.0F), MathHelper.clamp(var2, -30.0F, 30.0F), var3);
      current = var0.getId();
      active = 0;
   }

   private static RotationProfileDataset.PrimaryCacheEntry resolve() {
      return cache != null && cache.enabled != null && !cache.enabled.isEmpty() ? cache.enabled.get(Math.min(active, cache.enabled.size() - 1)) : null;
   }

   private static float handle(float var0, boolean var1, boolean var2, boolean var3) {
      float var4 = Math.abs(var0);
      if (var4 < 0.001F) {
         return 0.0F;
      }

      float var5 = var1 ? 0.62F : 0.42F;
      float var6 = var1 ? 34.0F : 22.0F;
      float var7 = var1 ? 0.31F : 0.25F;
      if (var2) {
         var6 *= 1.12F;
         var7 *= 1.08F;
      }

      if (var3) {
         var6 *= 0.82F;
         var7 *= 0.88F;
      }

      if (var4 > 95.0F) {
         var7 += var1 ? 0.08F : 0.05F;
      }

      float var8 = var4 * var7 + var5;
      if (var4 < 3.0F) {
         var8 = Math.max(var5 * 0.45F, var4 * 0.68F);
      }

      var8 = MathHelper.clamp(var8, var5 * 0.45F, var6);
      var8 = Math.min(var8, var4);
      return Math.signum(var0) * var8;
   }

   private static NeuroEngine.PrimaryDataRecord handle(float var0, float var1, NeuroEngine.DataRecord var2, float var3, float var4, boolean var5, boolean var6) {
      float var7 = output.pending + var2.yaw * 0.72F;
      float var8 = output.previous + var2.pitch * 0.68F;
      float var9 = handle(var7, true, var3, var4, var5, var6);
      float var10 = handle(var8, false, var3, var4, var5, var6);
      float var11 = process(3.4F, 2.75F, 3.15F) * (var5 ? 1.12F : 1.0F);
      float var12 = process(2.4F, 1.95F, 2.25F) * (var5 ? 1.1F : 1.0F);
      if (var6) {
         var11 *= 0.78F;
         var12 *= 0.78F;
      }

      output.latest = handle(output.latest, var9, var11);
      output.summary = handle(output.summary, var10, var12);
      float var13 = handle(output.latest, var0, true, var3);
      float var14 = handle(output.summary, var1, false, var3);
      output.latest = var13 * 0.86F + output.latest * 0.14F;
      output.summary = var14 * 0.86F + output.summary * 0.14F;
      return new NeuroEngine.PrimaryDataRecord(var13, var14, var9, var10);
   }

   private static float handle(float var0, boolean var1, float var2, float var3, boolean var4, boolean var5) {
      float var6 = Math.abs(var0);
      if (var6 < 0.001F) {
         return 0.0F;
      }

      float var7 = (var1 ? 34.0F : 22.0F) * process(0.72F, 0.64F, 0.72F);
      float var8 = (float)Math.sqrt(var6) * (var1 ? 2.15F : 1.55F);
      float var9 = var6 * (var1 ? 0.052F : 0.04F);
      float var10 = var8 + var9;
      if (var6 < 7.0F) {
         var10 = var6 * process(0.54F, 0.43F, 0.48F) + (var1 ? 0.12F : 0.08F);
      }

      if (output.config > 0 && var2 < 30.0F) {
         var10 *= var4 ? 0.62F : 0.42F;
      }

      if (var5) {
         var10 *= 0.76F;
      }

      var10 *= var3;
      var10 = MathHelper.clamp(var10, 0.0F, var7);
      return Math.signum(var0) * Math.min(var10, var6 + (var2 < 7.0F ? (var1 ? 0.8F : 0.45F) : 0.0F));
   }

   private static float handle(float var0, float var1, float var2) {
      float var3 = var1 - var0;
      float var4 = var2 + Math.abs(var0) * 0.16F;
      return var0 + MathHelper.clamp(var3, -var4, var4);
   }

   private static float handle(RotationProfileDataset.PrimaryCacheEntry var0, float var1, boolean var2, boolean var3) {
      float var4 = var1 > 80.0F ? 1.08F : 0.96F;
      if (var0 != null) {
         float var5 = Math.abs(var0.config) + Math.abs(var0.state) + Math.abs(var0.cache) * 0.035F + Math.abs(var0.output) * 0.03F;
         var4 = 0.78F + MathHelper.clamp(var5 / 7.0F, 0.0F, 1.0F) * 0.48F;
         if (var0.current > 0.72F) {
            var4 -= 0.04F;
         }
      } else {
         var4 += (float)Math.sin(mode * 0.31F) * 0.04F;
      }

      float var9 = execute();
      var4 += (float)Math.sin(output.data * 0.23F + output.renderer) * 0.065F * var9;
      var4 += (float)Math.sin(output.data * 0.071F + output.renderer * 0.43F) * 0.035F * var9;
      if (output.config > 0 && var1 < 28.0F) {
         var4 *= var2 ? 0.74F : 0.58F;
      }

      if (var2) {
         var4 += 0.08F;
      }

      if (var3) {
         var4 -= 0.12F;
      }

      return MathHelper.clamp(var4, 0.72F, 1.28F);
   }

   private static float process(float var0, boolean var1, boolean var2, boolean var3) {
      float var4;
      if (var0 > 90.0F) {
         var4 = 0.52F;
      } else if (var0 > 35.0F) {
         var4 = 0.74F;
      } else if (var0 > 8.0F) {
         var4 = 1.0F;
      } else {
         var4 = 0.86F;
      }

      if (var1) {
         var4 *= 1.08F;
      }

      if (var2) {
         var4 *= 0.55F;
      }

      if (!var3) {
         var4 *= 0.45F;
      }

      return MathHelper.clamp(var4 * execute(), 0.0F, 2.25F);
   }

   private static NeuroEngine.DataRecord handle(RotationProfileDataset.PrimaryCacheEntry var0, float var1, float var2, float var3, float var4, boolean var5) {
      float var6 = handle(var1);
      float var7 = handle(var2);
      float var8 = 0.0F;
      float var9 = 0.0F;
      if (var0 != null) {
         float var10 = check();
         float var11 = Math.abs(var0.config) * var6 * 0.14F + var0.config * 0.045F + Math.signum(var0.config) * Math.min(Math.abs(var0.cache) * 0.012F, 0.42F);
         float var12 = Math.abs(var0.state) * var7 * 0.115F + var0.state * 0.036F + Math.signum(var0.state) * Math.min(Math.abs(var0.output) * 0.01F, 0.34F);
         var8 += MathHelper.clamp(var11 * var10, -4.2F, 4.2F);
         var9 += MathHelper.clamp(var12 * var10, -2.75F, 2.75F);
         if (cache != null && var0.current > 0.82F && var3 < 10.0F) {
            var8 += var6 * MathHelper.clamp(Math.abs(cache.output) * 0.28F * var10, 0.0F, var5 ? 1.65F : 0.95F);
            var9 += var7 * MathHelper.clamp(Math.abs(cache.current) * 0.22F * var10, 0.0F, var5 ? 1.05F : 0.65F);
         }
      }

      float var20 = prepare();
      float var21 = ((float)Math.sin(output.data * 0.81F + output.handler) * 0.26F + (float)Math.sin(output.data * 1.37F + output.handler * 0.7F) * 0.11F)
         * var6
         * var20;
      float var22 = (
            (float)Math.sin(output.data * 0.67F + output.handler * 1.3F) * 0.18F + (float)Math.sin(output.data * 1.11F + output.handler * 0.4F) * 0.075F
         )
         * var7
         * var20;
      float var13 = (float)Math.sin(output.data * 0.097F + output.animationDraw) * 0.34F;
      float var14 = (float)Math.sin(output.data * 0.083F + output.animationDraw * 0.62F) * 0.22F;
      float var15 = MathHelper.clamp(Math.abs(var2) * 0.008F, 0.0F, 0.32F) * var6;
      float var16 = MathHelper.clamp(Math.abs(var1) * 0.005F, 0.0F, 0.22F) * var7;
      float var17 = var3 < 7.0F ? 1.28F : (var3 < 18.0F ? 1.12F : (var3 > 65.0F ? 0.72F : 1.0F));
      var8 += var21 + var13 + var15 + output.pointEncode;
      var9 += var22 + var14 + var16 + output.animator;
      return new NeuroEngine.DataRecord(var8 * var4 * var17, var9 * var4 * var17);
   }

   private static float handle(float var0, float var1, boolean var2, float var3) {
      float var4 = Math.abs(var1);
      if (var4 < 0.001F) {
         return 0.0F;
      }

      float var5 = var2 ? 39.44F : 24.64F;
      if (var4 > 2.8F && Math.signum(var0) != Math.signum(var1)) {
         var0 = Math.signum(var1) * Math.min(var4, var2 ? 0.34F : 0.24F);
      }

      float var6 = var3 < 7.0F ? (var2 ? 0.95F : 0.55F) : 0.0F;
      float var7 = Math.min(var5, var4 + var6);
      var0 = MathHelper.clamp(var0, -var7, var7);
      if (var3 > 5.0F && Math.abs(var0) > var4) {
         var0 = var1;
      }

      return var0;
   }

   private static float handle(float var0) {
      return var0 < 0.0F ? -1.0F : 1.0F;
   }

   private static String update() {
      return cache == null ? "synthetic" : cache.instance;
   }

   private static int apply() {
      return cache != null && cache.enabled != null ? cache.enabled.size() : 0;
   }

   private static void handle(LivingEntity var0) {
      if (output.instance != var0.getId()) {
         output.handle();
         output.instance = var0.getId();
         float var1 = onTick();
         float var2 = refresh();
         output.output = handle(-0.16F * var1, 0.16F * var1);
         output.current = handle(-0.1F * var1, 0.12F * var1);
         output.active = handle(0.58F - 0.06F * var2, 0.58F + 0.1F * var2);
         output.mode = handle(-0.24F * var1, 0.24F * var1);
         output.selection = handle(-0.16F * var1, 0.18F * var1);
         output.enabled = handle(0.58F - 0.1F * var2, 0.58F + 0.16F * var2);
         output.context = process(false);
         output.renderer = handle(0.0F, (float) (Math.PI * 2));
         output.handler = handle(0.0F, (float) (Math.PI * 2));
         output.animationDraw = handle(0.0F, (float) (Math.PI * 2));
      }
   }

   private static void handle(float var0, float var1, float var2, boolean var3, boolean var4) {
      output.data++;
      if (!output.cache) {
         output.pending = var1;
         output.previous = var2;
         output.cache = true;
      } else {
         float var5 = process(0.66F, 0.48F, 0.55F);
         if (var3) {
            var5 += 0.08F;
         }

         if (var4) {
            var5 *= 0.72F;
         }

         output.pending = output.pending + MathHelper.wrapDegrees(var1 - output.pending) * MathHelper.clamp(var5, 0.18F, 0.86F);
         output.previous = output.previous + (var2 - output.previous) * MathHelper.clamp(var5, 0.18F, 0.86F);
      }

      if (output.context-- <= 0) {
         float var8 = onTick();
         float var6 = refresh();
         output.mode = handle(-0.3F * var8, 0.3F * var8);
         output.selection = handle(-0.2F * var8, 0.22F * var8);
         output.enabled = handle(0.58F - 0.12F * var6, var3 ? 0.58F + 0.22F * var6 : 0.58F + 0.16F * var6);
         output.context = process(var3);
      }

      float var9 = (var3 ? 0.16F : 0.095F) * select();
      output.output = output.output + (output.mode - output.output) * var9;
      output.current = output.current + (output.selection - output.current) * var9;
      output.active = output.active + (output.enabled - output.active) * var9;
      if (output.config > 0) {
         output.config--;
      } else if (!var4 && var0 > 2.2F && var0 < 24.0F) {
         float var10 = var3 ? 0.012F : 0.034F;
         if (ThreadLocalRandom.current().nextFloat() < var10) {
            output.config = ThreadLocalRandom.current().nextInt(1, var3 ? 3 : 4);
         }
      }

      if (output.state && var0 < 32.0F) {
         float var11 = Math.abs(var1) - Math.abs(output.source);
         float var7 = Math.abs(var2) - Math.abs(output.target);
         if (var11 > 0.8F) {
            output.pointEncode = output.pointEncode - Math.signum(var1) * MathHelper.clamp(var11 * 0.075F, 0.0F, 0.52F);
         }

         if (var7 > 0.65F) {
            output.animator = output.animator - Math.signum(var2) * MathHelper.clamp(var7 * 0.055F, 0.0F, 0.34F);
         }
      }

      output.pointEncode *= var3 ? 0.76F : 0.68F;
      output.animator *= var3 ? 0.74F : 0.66F;
   }

   private static void handle(boolean var0, String var1) {
      if (var0) {
         long var2 = System.currentTimeMillis();
         String var4 = "[Neuro] " + var1;

         try {
            Path var5 = RotationProfileRepository.resolve().resolve("neuro_debug.log");
            Files.createDirectories(var5.getParent());
            Files.writeString(var5, var2 + " " + var4 + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (Throwable var6) {
         }

         if (var2 - handler >= 700L) {
            handler = var2;
            ChatLogger.handle(var4);
         }
      }
   }

   private static String process(float var0) {
      return String.format(Locale.ROOT, "%.2f", var0);
   }

   private static RotationAngles handle(LivingEntity var0, NeuroEngine.ColorState var1) {
      Vec3d var2 = toggleState.player.getEyePos();
      Box var3 = var0.getBoundingBox();
      Vec3d var4 = var0.getPos();
      Vec3d var5 = toggleState.player.getPos().subtract(var4);
      double var6 = Math.hypot(var5.x, var5.z);
      double var8 = var6 > 1.0E-4 ? var5.x / var6 : 0.0;
      double var10 = var6 > 1.0E-4 ? var5.z / var6 : 1.0;
      double var12 = var6 > 1.0E-4 ? var5.z / var6 : 1.0;
      double var14 = var6 > 1.0E-4 ? -var5.x / var6 : 0.0;
      double var16 = var1.output * Math.max(0.25, var0.getWidth());
      double var18 = var1.current * Math.max(0.25, var0.getWidth());
      double var20 = var3.minY + var0.getHeight() * 0.22;
      double var22 = var3.minY + var0.getHeight() * 0.84;
      Vec3d var24 = new Vec3d(
         MathHelper.clamp(var4.x + var12 * var16 + var8 * var18, var3.minX, var3.maxX),
         MathHelper.clamp(var3.minY + var0.getHeight() * var1.active, var20, var22),
         MathHelper.clamp(var4.z + var14 * var16 + var10 * var18, var3.minZ, var3.maxZ)
      );
      Vec3d var25 = var24.subtract(var2);
      float var26 = (float)Math.toDegrees(Math.atan2(-var25.x, var25.z));
      float var27 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var25.y, Math.hypot(var25.x, var25.z))), -90.0, 90.0);
      return new RotationAngles(var26, var27);
   }

   private static float handle(float var0, float var1) {
      return var0 + ThreadLocalRandom.current().nextFloat() * (var1 - var0);
   }

   private static int process(boolean var0) {
      float var1 = select();
      int var2 = Math.max(2, Math.round((var0 ? 5.0F : 7.0F) / var1));
      int var3 = Math.max(var2 + 1, Math.round((var0 ? 14.0F : 22.0F) / var1));
      return ThreadLocalRandom.current().nextInt(var2, var3 + 1);
   }

   private static float execute() {
      return process(1.05F, 1.45F, 1.75F);
   }

   private static float prepare() {
      return process(0.78F, 1.18F, 1.42F);
   }

   private static float check() {
      return process(1.0F, 1.35F, 1.65F);
   }

   private static float onTick() {
      return process(0.9F, 1.32F, 1.58F);
   }

   private static float select() {
      return process(0.82F, 1.08F, 1.32F);
   }

   private static float refresh() {
      return process(0.78F, 1.08F, 1.32F);
   }

   private static float process(float var0, float var1, float var2) {
      float var3 = switch (AttackAura.positionAdvance.compute()) {
         case "Stable" -> var0;
         case "Dynamic" -> var2;
         default -> var1;
      };
      return MathHelper.clamp(var3 * AttackAura.frameCheck.compute(), 0.0F, 3.0F);
   }

   static final class ColorState {
      int instance = -1;
      int data;
      int context;
      int config;
      boolean state;
      boolean cache;
      float output;
      float current;
      float active = 0.58F;
      float mode;
      float selection;
      float enabled = 0.58F;
      float renderer;
      float handler;
      float animationDraw;
      float pointEncode;
      float animator;
      float source;
      float target;
      float pending;
      float previous;
      float latest;
      float summary;

      void handle() {
         this.instance = -1;
         this.data = 0;
         this.context = 0;
         this.config = 0;
         this.state = false;
         this.cache = false;
         this.output = 0.0F;
         this.current = 0.0F;
         this.active = 0.58F;
         this.mode = 0.0F;
         this.selection = 0.0F;
         this.enabled = 0.58F;
         this.renderer = 0.0F;
         this.handler = 0.0F;
         this.animationDraw = 0.0F;
         this.pointEncode = 0.0F;
         this.animator = 0.0F;
         this.source = 0.0F;
         this.target = 0.0F;
         this.pending = 0.0F;
         this.previous = 0.0F;
         this.latest = 0.0F;
         this.summary = 0.0F;
      }
   }

   record DataRecord(float yaw, float pitch) {
   }

   record PrimaryDataRecord(float yaw, float pitch, float targetYawVelocity, float targetPitchVelocity) {
   }
}
