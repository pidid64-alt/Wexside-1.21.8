package ru.wild.modules.visuals;

import com.google.gson.JsonObject;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldJoinedEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.render.FloatingStardustParticle;
import ru.wild.render.ShootingStarParticle;
import ru.wild.render.StardustSkyRenderer;
import ru.wild.render.shader.AuroraPresets;
import ru.wild.render.shader.StardustShader;

@ModuleRegister(
   name = "Stardust",
   description = "Цветное звездное небо и локальные светящиеся звезды",
   category = ModuleCategory.Visuals,
   flags = ModuleFlag.NEW
)
public final class Stardust extends Module {
   private static final String responseCompute = "Кастомные облака";
   private static final String providerFetch = "Цвет облаков";
   public static final ColorSetting source = new ColorSetting("Цвет неба", 72.0F, 0.72F, 1.0F);
   public static final ModeSetting target = new ModeSetting("Шейдер неба", AuroraPresets.AURORA.handle(), AuroraPresets.compute());
   public static final NumberSetting pending = new NumberSetting("Плотность звёзд", 1880.0F, 220.0F, 3600.0F, 20.0F, false);
   public static final NumberSetting previous = new NumberSetting("Яркость", 1.55F, 0.2F, 2.75F, 0.05F, false);
   public static final ModeSetting latest = new ModeSetting("Время суток", "Ночь", "День", "Закат", "Рассвет", "Ночь", "Полночь", "Полдень");
   public static final ChoiceSetting summary = new ChoiceSetting(
      "Настройки облаков", new BooleanSetting("Кастомные облака", false), new BooleanSetting("Цвет облаков", false)
   );
   public static final ColorSetting matrixBlend = new ColorSetting("Цвет облаков", 0.0F, 0.0F, 1.0F, 1.0F).process(() -> !measure());
   public static final NumberSetting vectorMatch = new NumberSetting("Сила цвета облаков", 1.0F, 0.0F, 1.0F, 0.05F, true).handle(() -> !measure());
   public static long itemProject = -1L;
   private static volatile boolean profileDraw;
   private static final float vectorPerform = (float) (Math.PI * 2);
   private static final float eventAttach = 16.0F;
   private static final float serverRead = 22.0F;
   private static final float positionAdvance = 0.74F;
   private int frameCheck;
   private int moduleCollect;
   private int providerClose;
   private int presetSave = Integer.MIN_VALUE;
   private int windowConvert = Integer.MIN_VALUE;
   private int presetWrite = Integer.MIN_VALUE;
   private static volatile int colorMeasure = 7175679;
   private static volatile int animationSchedule = 5435580;

   public Stardust() {
      StardustShader.handle();
      StardustSkyRenderer.handle();
      this.handle(source, target, pending, previous, latest, summary, matrixBlend, vectorMatch);
   }

   @Override
   public void handle() {
      this.frameCheck = 0;
      this.moduleCollect = 0;
      this.providerClose = 8;
      this.presetSave = Integer.MIN_VALUE;
      this.windowConvert = Integer.MIN_VALUE;
      this.presetWrite = Integer.MIN_VALUE;
      profileDraw = true;
      fetch();
      FloatingStardustParticle.process();
      ShootingStarParticle.process();
      super.handle();
   }

   @Override
   public void process() {
      profileDraw = false;
      itemProject = -1L;
      FloatingStardustParticle.process();
      ShootingStarParticle.process();
      super.process();
   }

   @Override
   public void handle(JsonObject var1) {
      process(var1);
      super.handle(var1);
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      MinecraftClient var2 = var1.compute();
      if (var2 != null && var2.world != null && var2.player != null && var2.gameRenderer != null) {
         fetch();
         blendMatrix();
         int var3 = Math.max(0, Math.round(pending.compute()));
         int var4 = colorMeasure;
         int var5 = save().process();
         if (var3 != this.presetSave || var4 != this.windowConvert || var5 != this.presetWrite) {
            this.frameCheck = 0;
            this.presetSave = var3;
            this.windowConvert = var4;
            this.presetWrite = var5;
            FloatingStardustParticle.process();
            ShootingStarParticle.process();
         }

         Camera var6 = var2.gameRenderer.getCamera();
         if (var6 != null) {
            Vec3d var7 = var6.getPos();
            ClientWorld var8 = var2.world;
            int var9 = FloatingStardustParticle.handle();
            int var10 = var3 - var9;
            if (var10 > 0) {
               int var11 = Math.min(var10, var9 == 0 ? Math.min(var3, 760) : 188);

               for (int var12 = 0; var12 < var11; var12++) {
                  this.handle(var8, var7, 16.0F, 22.0F);
               }
            }

            int var13 = ShootingStarParticle.handle();
            if (var13 < Math.max(2, Math.round(previous.compute() * 2.6F))) {
               this.providerClose--;
               if (this.providerClose <= 0) {
                  this.handle(var8, var7);
                  float var14 = process((this.moduleCollect + 1) * 0.618034F + 0.491F);
                  this.providerClose = Math.max(14, 64 - Math.round(previous.compute() * 14.0F) + (int)(var14 * 46.0F));
               }
            }
         }
      }
   }

   private void handle(ClientWorld var1, Vec3d var2, float var3, float var4) {
      float var5 = process((this.frameCheck + 1) * 0.618034F + 0.173F);
      float var6 = process((this.frameCheck + 1) * 0.7548777F + 0.419F);
      float var7 = process((this.frameCheck + 1) * 0.5698403F + 0.271F);
      float var8 = process((this.frameCheck + 1) * 0.4386875F + 0.617F);
      float var9 = process((this.frameCheck + 1) * 0.3271949F + 0.383F);
      float var10 = process((this.frameCheck + 1) * 0.27917233F + 0.719F);
      float var11 = process((this.frameCheck + 1) * 0.21132487F + 0.127F);
      float var12 = var5 * (float) (Math.PI * 2) + (var9 - 0.5F) * 0.42F;
      float var13 = (float)Math.sqrt(var8);
      float var14 = 1.4F + var13 * Math.max(1.0F, var4 - 1.4F);
      float var15 = var10;
      float var16;
      if (var15 < 0.12F) {
         var16 = 0.55F + var6 * 3.15F;
         var14 = 2.2F + var13 * Math.max(1.0F, var4 - 2.2F);
      } else if (var15 < 0.42F) {
         var16 = 2.2F + (float)Math.pow(var6, 0.76F) * (var3 * 0.58F);
      } else if (var15 < 0.84F) {
         var16 = 4.2F + (float)Math.pow(var6, 0.48F) * var3;
      } else {
         var16 = var3 * (0.7F + var6 * 0.44F) + var11 * 4.5F;
         var14 = 1.8F + (float)Math.sqrt(var9) * Math.max(1.0F, var4 * 0.82F - 1.8F);
      }

      float var17 = handle(0.58F, 1.0F, var11);
      var14 *= 1.0F - var17 * 0.16F;
      var16 += (var9 - 0.44F) * var17 * 2.8F;
      double var18 = Math.cos(var12);
      double var20 = Math.sin(var12);
      double var22 = (var7 - 0.5F) * 0.0013;
      double var24 = (var5 - 0.5F) * 0.001;
      double var26 = (var6 - 0.5F) * 0.0013;
      var1.addImportantParticleClient(
         FloatingStardustParticle.instance, true, var2.x + var18 * var14, var2.y + var16, var2.z + var20 * var14, var22, var24, var26
      );
      this.frameCheck++;
   }

   private void handle(ClientWorld var1, Vec3d var2) {
      float var3 = process((this.moduleCollect + 1) * 0.7548777F + 0.137F);
      float var4 = process((this.moduleCollect + 1) * 0.5698403F + 0.671F);
      float var5 = process((this.moduleCollect + 1) * 0.4386875F + 0.293F);
      float var6 = process((this.moduleCollect + 1) * 0.3271949F + 0.811F);
      float var7 = process((this.moduleCollect + 1) * 0.27917233F + 0.357F);
      float var8 = var3 * (float) (Math.PI * 2);
      float var9 = var8 + 2.1F + (var4 - 0.5F) * 0.86F;
      float var10 = 12.0F + var5 * 18.92F;
      float var11 = 16.0F * (0.64F + var6 * 0.78F) + 2.0F;
      double var12 = Math.cos(var8) * var10;
      double var14 = Math.sin(var8) * var10;
      double var16 = 0.118 + var7 * 0.092 + Math.min(0.08F, previous.compute() * 0.018F);
      double var18 = Math.cos(var9) * var16;
      double var20 = Math.sin(var9) * var16;
      double var22 = -0.03 - var4 * 0.052;
      var1.addImportantParticleClient(ShootingStarParticle.instance, true, var2.x + var12, var2.y + var11, var2.z + var14, var18, var22, var20);
      this.moduleCollect++;
   }

   @EventHandler
   public void handle(WorldJoinedEvent var1) {
      this.frameCheck = 0;
      this.moduleCollect = 0;
      this.providerClose = 8;
      FloatingStardustParticle.process();
      ShootingStarParticle.process();
   }

   public static boolean refresh() {
      return profileDraw;
   }

   public static float render() {
      return previous.compute();
   }

   public static float tick() {
      return Math.min(previous.compute() * 0.74F, 1.65F);
   }

   public static float drawAnimation() {
      return 22.0F;
   }

   public static float encodePoint() {
      return 23.0F;
   }

   public static int animate() {
      return colorMeasure;
   }

   public static int load() {
      return animationSchedule;
   }

   public static AuroraPresets save() {
      return AuroraPresets.handle(target.compute());
   }

   public static int submit() {
      return save().process();
   }

   public static boolean unload() {
      return profileDraw && itemProject >= 0L;
   }

   public static int handle(int var0) {
      return !measure() ? var0 : handle(var0, matrixBlend.compute(), vectorMatch.compute());
   }

   private static void fetch() {
      String var0 = latest.compute();
      switch (var0) {
         case "День":
            itemProject = 1000L;
            break;
         case "Закат":
            itemProject = 12000L;
            break;
         case "Рассвет":
            itemProject = 23000L;
            break;
         case "Полночь":
            itemProject = 13000L;
            break;
         case "Ночь":
            itemProject = 18000L;
            break;
         case "Полдень":
            itemProject = 6000L;
            break;
         default:
            itemProject = 0L;
      }
   }

   private static boolean measure() {
      return profileDraw && summary.process("Кастомные облака") && summary.process("Цвет облаков");
   }

   private static void blendMatrix() {
      int var0 = source.prepare() & 16777215;
      int var1 = handle(var0, save());
      if (var0 != colorMeasure || var1 != animationSchedule) {
         colorMeasure = var0;
         animationSchedule = var1;
      }
   }

   private static int handle(int var0, AuroraPresets var1) {
      return switch (var1) {
         case STARDUST -> handle(var0, 0.34F, 0.68F, 1.08F);
         case TWILIGHT_RAYLEIGH -> handle(var0, 0.08F, 0.72F, 1.14F);
         case QUANTUM_NEBULA -> handle(var0, 0.5F, 0.86F, 1.2F);
         case CHRONOS_SINGULARITY -> handle(var0, 0.7F, 0.92F, 1.08F);
         default -> process(var0);
      };
   }

   private static void process(JsonObject var0) {
      if (var0 != null && var0.has("Settings")) {
         try {
            JsonObject var1 = var0.getAsJsonObject("Settings");
            if (var1 == null || !var1.has(target.instance)) {
               return;
            }

            String var2 = var1.get(target.instance).getAsString();
            AuroraPresets var3 = AuroraPresets.handle(var2);
            if (!var3.handle().equals(var2)) {
               var1.addProperty(target.instance, var3.handle());
            }
         } catch (Throwable var4) {
         }
      }
   }

   private static int process(int var0) {
      return handle(var0, 0.32F, 0.74F, 0.92F);
   }

   private static int handle(int var0, float var1, float var2, float var3) {
      int var4 = var0 >>> 16 & 0xFF;
      int var5 = var0 >>> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      float[] var7 = Color.RGBtoHSB(var4, var5, var6, null);
      float var8 = process(var7[0] + var1);
      float var9 = process(var2 + var7[1] * 0.2F, 0.0F, 1.0F);
      float var10 = process(var3 + var7[2] * 0.1F, 0.0F, 1.0F);
      return Color.HSBtoRGB(var8, var9, var10) & 16777215;
   }

   private static float handle(float var0, float var1, float var2) {
      float var3 = (var2 - var0) / (var1 - var0);
      if (var3 <= 0.0F) {
         return 0.0F;
      } else {
         return var3 >= 1.0F ? 1.0F : var3 * var3 * (3.0F - 2.0F * var3);
      }
   }

   private static float process(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   private static float handle(float var0) {
      return !Float.isFinite(var0) ? 0.0F : Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static float compute(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static int handle(int var0, Color var1, float var2) {
      float var3 = handle(var2 * (var1.getAlpha() / 255.0F));
      if (var3 <= 0.0F) {
         return var0;
      }

      int var4 = var0 >>> 24 & 0xFF;
      int var5 = var0 >>> 16 & 0xFF;
      int var6 = var0 >>> 8 & 0xFF;
      int var7 = var0 & 0xFF;
      int var8 = Math.round(compute(var5, var1.getRed(), var3));
      int var9 = Math.round(compute(var6, var1.getGreen(), var3));
      int var10 = Math.round(compute(var7, var1.getBlue(), var3));
      return var4 << 24 | var8 << 16 | var9 << 8 | var10;
   }

   private static float process(float var0) {
      return var0 - (float)Math.floor(var0);
   }
}
