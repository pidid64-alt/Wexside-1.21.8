package ru.wild.modules.visuals;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "FullBright", description = "ПРОЗРЕВШИЙ", category = ModuleCategory.Visuals)
public class FullBright extends Module {
   private static final String responseCompute = "Гамма";
   private static final String providerFetch = "Эффект";
   private static final String profileDraw = "Динамический";
   private static final String vectorPerform = "Адаптивный";
   private static final String eventAttach = "Факел";
   public ModeSetting source = new ModeSetting("Тип", "Гамма", "Гамма", "Эффект", "Динамический", "Адаптивный", "Факел");
   public NumberSetting target = new NumberSetting("Порог", 0.53F, 0.5F, 0.6F, 0.01F, true).handle(() -> !this.tick());
   public NumberSetting pending = new NumberSetting("Кривая", 1.4F, 0.5F, 3.0F, 0.1F, false).handle(() -> !this.tick());
   public NumberSetting previous = new NumberSetting("Радиус", 10.0F, 5.0F, 20.0F, 1.0F, false).handle(() -> !this.drawAnimation());
   public static volatile boolean latest = false;
   public static volatile float summary = 10.0F;
   public static volatile double matrixBlend = 0.0;
   public static volatile double vectorMatch = 0.0;
   public static volatile double itemProject = 0.0;
   private int serverRead = Integer.MIN_VALUE;
   private int positionAdvance = Integer.MIN_VALUE;
   private int frameCheck = Integer.MIN_VALUE;
   private boolean moduleCollect = false;
   private int providerClose;
   private int presetSave;
   private int windowConvert;
   private int presetWrite;
   private int colorMeasure;
   private int animationSchedule;

   public FullBright() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @Override
   public void handle() {
      super.handle();
      this.serverRead = Integer.MIN_VALUE;
      this.positionAdvance = Integer.MIN_VALUE;
      this.frameCheck = Integer.MIN_VALUE;
      if (Module.client.worldRenderer != null && this.fetch()) {
         Module.client.worldRenderer.reload();
      }
   }

   @Override
   public void process() {
      super.process();
      this.submit();
      if (Module.client.worldRenderer != null && this.fetch()) {
         Module.client.worldRenderer.reload();
      }

      if (Module.client.player != null) {
         Module.client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null) {
         if (this.unload()) {
            Module.client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         }

         if (this.source.process("Эффект")) {
            Module.client.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, false, false));
         }

         this.save();
      }
   }

   private void save() {
      if (this.drawAnimation() && Module.client.world != null && Module.client.worldRenderer != null) {
         summary = this.previous.compute();
         matrixBlend = Module.client.player.getX();
         vectorMatch = Module.client.player.getEyeY();
         itemProject = Module.client.player.getZ();
         latest = true;
         int var1 = MathHelper.floor(matrixBlend);
         int var2 = MathHelper.floor(Module.client.player.getY());
         int var3 = MathHelper.floor(itemProject);
         if (var1 != this.serverRead || var2 != this.positionAdvance || var3 != this.frameCheck) {
            if (this.moduleCollect) {
               Module.client.worldRenderer
                  .scheduleBlockRenders(this.providerClose, this.presetSave, this.windowConvert, this.presetWrite, this.colorMeasure, this.animationSchedule);
            }

            int var4 = MathHelper.ceil(this.previous.compute()) + 1;
            this.providerClose = var1 - var4;
            this.presetSave = var2 - var4;
            this.windowConvert = var3 - var4;
            this.presetWrite = var1 + var4;
            this.colorMeasure = var2 + var4;
            this.animationSchedule = var3 + var4;
            this.moduleCollect = true;
            Module.client.worldRenderer
               .scheduleBlockRenders(this.providerClose, this.presetSave, this.windowConvert, this.presetWrite, this.colorMeasure, this.animationSchedule);
            this.serverRead = var1;
            this.positionAdvance = var2;
            this.frameCheck = var3;
         }
      } else {
         this.submit();
      }
   }

   private void submit() {
      if (latest || this.moduleCollect) {
         latest = false;
         if (this.moduleCollect && Module.client.worldRenderer != null) {
            Module.client.worldRenderer
               .scheduleBlockRenders(this.providerClose, this.presetSave, this.windowConvert, this.presetWrite, this.colorMeasure, this.animationSchedule);
         }

         this.moduleCollect = false;
         this.serverRead = Integer.MIN_VALUE;
         this.positionAdvance = Integer.MIN_VALUE;
         this.frameCheck = Integer.MIN_VALUE;
      }
   }

   public static int handle(int var0, int var1, int var2) {
      return handle(var0 + 0.5, var1 + 0.5, var2 + 0.5);
   }

   public static int handle(double var0, double var2, double var4) {
      if (!latest) {
         return 0;
      }

      float var6 = summary;
      if (var6 <= 0.0F) {
         return 0;
      }

      double var7 = var0 - matrixBlend;
      double var9 = var2 - vectorMatch;
      double var11 = var4 - itemProject;
      double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
      if (var13 >= var6) {
         return 0;
      }

      int var15 = Math.round(15.0F * (float)(1.0 - var13 / var6));
      return var15 < 1 ? 0 : Math.min(var15, 15);
   }

   public boolean refresh() {
      return this.source.process("Гамма");
   }

   public boolean render() {
      return this.source.process("Динамический");
   }

   public boolean tick() {
      return this.source.process("Адаптивный");
   }

   public boolean drawAnimation() {
      return this.source.process("Факел");
   }

   public float encodePoint() {
      return handle(this.target.compute(), 0.0F, 1.0F);
   }

   public float animate() {
      return handle(this.pending.compute(), 0.5F, 3.0F);
   }

   public float load() {
      if (Module.client.player != null && Module.client.world != null) {
         BlockPos var1 = Module.client.player.getBlockPos();
         float var2 = Module.client.world.getLightLevel(LightType.BLOCK, var1) / 15.0F;
         float var3 = Module.client.world.getLightLevel(LightType.SKY, var1) / 15.0F;
         float var4 = handle(Module.client.world.getTimeOfDay() % 24000L);
         float var5 = Math.max(var2, var3 * (1.0F - var4 * 0.7F));
         float var6 = handle(Math.max(1.0F - var5, var4 * 0.65F), 0.0F, 1.0F);
         float var7 = (float)Math.sin(System.currentTimeMillis() * 0.0018) * 0.035F;
         return 2.0F + handle(var6 + var7, 0.0F, 1.0F) * 198.0F;
      } else {
         return 80.0F;
      }
   }

   private boolean unload() {
      return this.source.process("Гамма") || this.source.process("Динамический") || this.source.process("Адаптивный");
   }

   private boolean fetch() {
      return this.source.process("Гамма") || this.source.process("Динамический");
   }

   private static float handle(long var0) {
      if (var0 < 12000L) {
         return 0.0F;
      } else if (var0 < 14000L) {
         return (float)(var0 - 12000L) / 2000.0F;
      } else {
         return var0 < 22000L ? 1.0F : 1.0F - (float)(var0 - 22000L) / 2000.0F;
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }
}
