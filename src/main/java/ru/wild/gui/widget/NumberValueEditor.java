package ru.wild.gui.widget;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.SettingValue;
import ru.wild.core.AnimationClock;
import ru.wild.gui.theme.AccentColorToken;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.PopupAnimationCurve;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.render.ColorCompositor;
import ru.wild.util.render.RoundedRectRenderer;

public final class NumberValueEditor implements SettingEditor {
   private static final float instance = 80.0F;
   private static final float data = 18.0F;
   private static final float context = 18.0F;
   private static final float config = 17.0F;
   private static final float state = 18.0F;
   private static final float cache = 20.0F;
   private static final float output = 298.0F;
   private static final float current = 6.0F;
   private static final float active = 15.0F;
   private static final float mode = 3.0F;
   private static final float selection = 18.0F;
   private static final float enabled = 12.0F;
   private static final float renderer = 1.35F;
   private static final int handler = -14606047;
   private static final int animationDraw = -2500135;
   private static final int pointEncode = -7829368;
   private static final int animator = -1;
   private static final SpringParameters source = SpringParameters.handle(2.1F, 0.55F);
   private static final SpringParameters target = SpringParameters.handle(1.4F, 0.7F);
   private static final SpringParameters pending = SpringParameters.handle(8.0F, 0.8F);
   private static final SpringParameters previous = SpringParameters.handle(1.8F, 0.65F);
   private static final float latest = 5.0E-4F;
   private static final float summary = 5.0E-4F;
   private static final float matrixBlend = 1.0E-4F;
   private static final double vectorMatch = 1.0E-4;
   private static final float itemProject = 0.001F;
   private final Module responseCompute;
   private final NumberSetting providerFetch;
   private final SettingPopupHost profileDraw;
   private final SettingValue<Double> vectorPerform;
   private final String eventAttach;
   private final SpringFloat serverRead;
   private final SpringFloat positionAdvance;
   private final SpringFloat frameCheck;
   private final SpringFloat moduleCollect;
   private NumberValueEditor.Bounds providerClose = NumberValueEditor.Bounds.EMPTY;
   private NumberValueEditor.Bounds presetSave = NumberValueEditor.Bounds.EMPTY;
   private NumberValueEditor.Bounds windowConvert = NumberValueEditor.Bounds.EMPTY;
   private boolean presetWrite = false;
   private double colorMeasure;
   private int animationSchedule;
   private boolean rendererScan = false;

   public NumberValueEditor(Module var1, SettingPopupHost var2, NumberSetting var3, SettingValue<Double> var4) {
      this(var1, var2, var3, var4, null);
   }

   public NumberValueEditor(Module var1, SettingPopupHost var2, NumberSetting var3, SettingValue<Double> var4, String var5) {
      this.responseCompute = Objects.requireNonNull(var1, "module");
      this.profileDraw = Objects.requireNonNull(var2, "popupContext");
      this.providerFetch = Objects.requireNonNull(var3, "setting");
      this.vectorPerform = Objects.requireNonNull(var4, "valueAccessor");
      this.eventAttach = handle(var5);
      this.serverRead = new SpringFloat(AnimationClock.handle(), source, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.serverRead.handle(PopupAnimationCurve.context);
      this.positionAdvance = new SpringFloat(AnimationClock.handle(), target, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.positionAdvance.handle(PopupAnimationCurve.context);
      Double var6 = (Double)var4.handle();
      double var7 = var6 != null ? var6 : var3.config;
      this.colorMeasure = var7;
      this.animationSchedule = resolve(var3.output);
      float var9 = this.compute(this.colorMeasure);
      this.frameCheck = new SpringFloat(AnimationClock.handle(), pending, var9, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.moduleCollect = new SpringFloat(AnimationClock.handle(), previous, 1.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.moduleCollect.handle(PopupAnimationCurve.context);
   }

   @Override
   public void handle() {
      Double var1 = this.vectorPerform.handle();
      double var2 = var1 != null ? var1 : this.providerFetch.config;
      this.colorMeasure = var2;
      this.serverRead.compute(this.presetWrite ? 1.0F : 0.0F);
      this.frameCheck.compute(this.check());
      this.refresh();
   }

   @Override
   public void handle(boolean var1) {
      float var2 = var1 ? 1.0F : 0.0F;
      this.moduleCollect.compute(var2);
      if (!var1) {
         this.presetWrite = false;
         this.rendererScan = false;
         this.serverRead.compute(0.0F);
         this.refresh();
      }
   }

   @Override
   public void handle(float var1, float var2, float var3) {
      float var4 = Math.max(0.0F, this.execute());
      this.providerClose = new NumberValueEditor.Bounds(var1, var2, var3, 80.0F);
      this.presetSave = new NumberValueEditor.Bounds(var1, var2, var3, var4);
      float var5 = var1 + 18.0F;
      float var6 = var2 + 17.0F + 18.0F + 15.0F;
      this.windowConvert = new NumberValueEditor.Bounds(var5, var6, 298.0F, 6.0F);
   }

   @Override
   public float process() {
      return 80.0F;
   }

   @Override
   public float execute() {
      return 80.0F * handle(this.moduleCollect.handle());
   }

   @Override
   public void handle(RoundedRectRenderer var1, float var2, float var3, float var4) {
      float var5 = handle(this.moduleCollect.handle());
      if (!(var5 <= 0.001F)) {
         float var6 = var2 * handle(var3) * var5;
         if (!(var6 <= 1.0E-4F)) {
            var1.compute(1.0F, var5, this.providerClose.x, this.providerClose.y);

            try {
               this.prepare();
               float var7 = handle(this.positionAdvance.handle());
               int var8 = ColorCompositor.handle(-7829368, -1, var7);
               int var9 = handle(var8, var6);
               float var10 = this.providerClose.x + 18.0F;
               float var11 = this.providerClose.y + 17.0F + 18.0F;
               var1.handle(FontRegistry.config, var10, var11, 18.0F, this.onTick(), var9, "l");
               float var12 = this.providerClose.y + 20.0F + 18.0F;
               float var13 = this.providerClose.x + this.providerClose.width - 18.0F;
               var1.handle(FontRegistry.config, var13, var12, 18.0F, this.select(), var9, "r");
               int var14 = handle(-14606047, var6);
               var1.handle(this.windowConvert.x, this.windowConvert.y, this.windowConvert.width, this.windowConvert.height, 3.0F, var14);
               float var15 = handle(this.frameCheck.handle());
               float var16 = this.windowConvert.width * var15;
               if (var16 > 0.0F) {
                  int var17 = handle(AccentColorToken.handle(), var6);
                  float var18 = var16 >= this.windowConvert.width - 0.01F ? 3.0F : 0.0F;
                  var1.handle(this.windowConvert.x, this.windowConvert.y, var16, this.windowConvert.height, 3.0F, var18, var18, 3.0F, var17);
               }

               float var26 = this.windowConvert.x + var16;
               float var27 = this.windowConvert.y + this.windowConvert.height * 0.5F;
               float var19 = 1.0F + handle(this.serverRead.handle()) * 0.35000002F;
               float var20 = 12.0F * var19;
               float var21 = var20 * 0.5F;
               int var22 = handle(-2500135, var6);
               var1.process(var26, var27, var21, 0.0F, 1.0F, var22);
            } finally {
               var1.check();
            }
         }
      }
   }

   @Override
   public boolean handle(double var1, double var3, int var5) {
      if (!this.render() || !this.presetSave.contains(var1, var3)) {
         return false;
      }

      if (var5 == 2) {
         Double var6 = this.vectorPerform.handle();
         Double var7 = var6 != null ? var6 : this.providerFetch.config;
         this.profileDraw.openForSetting(this.responseCompute, this.providerFetch, var1, var3, var7);
         return true;
      }

      if (var5 != 0) {
         return false;
      }

      this.presetWrite = true;
      this.serverRead.compute(1.0F);
      this.refresh();
      return true;
   }

   @Override
   public Setting compute() {
      return this.providerFetch;
   }

   @Override
   public boolean resolve() {
      return true;
   }

   @Override
   public boolean process(double var1, double var3, double var5, double var7) {
      if (this.render() && this.presetSave.contains(var1, var3)) {
         if (Math.abs(var7) <= 1.0E-4) {
            return false;
         }

         MinecraftClient var9 = MinecraftClient.getInstance();
         if (var9 != null && var9.getWindow() != null) {
            long var10 = var9.getWindow().getHandle();
            if (GLFW.glfwGetKey(var10, 341) != 1) {
               return false;
            }

            if (!this.process(var7)) {
               return false;
            }

            this.serverRead.compute(1.0F);
            this.refresh();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void handle(double var1, double var3) {
      if (!this.render()) {
         this.rendererScan = false;
         this.refresh();
      } else {
         this.rendererScan = this.presetSave.contains(var1, var3);
         this.refresh();
      }
   }

   private void prepare() {
      if (this.presetWrite) {
         if (!this.render()) {
            this.presetWrite = false;
            this.serverRead.compute(0.0F);
            this.refresh();
         } else {
            MinecraftClient var1 = MinecraftClient.getInstance();
            if (var1 != null && var1.getWindow() != null) {
               long var2 = var1.getWindow().getHandle();
               if (GLFW.glfwGetMouseButton(var2, 0) != 1) {
                  this.presetWrite = false;
                  this.serverRead.compute(0.0F);
                  this.refresh();
               } else {
                  double[] var4 = new double[1];
                  double[] var5 = new double[1];
                  GLFW.glfwGetCursorPos(var2, var4, var5);
                  this.handle(handle(var4[0], var1));
               }
            } else {
               this.presetWrite = false;
               this.serverRead.compute(0.0F);
               this.refresh();
            }
         }
      }
   }

   private void handle(double var1) {
      if (!(this.windowConvert.width <= 0.0F) && this.render()) {
         double var3 = Math.min(Math.max(var1, this.windowConvert.x), this.windowConvert.x + this.windowConvert.width);
         double var5 = (var3 - this.windowConvert.x) / this.windowConvert.width;
         double var7 = this.providerFetch.state;
         double var9 = this.providerFetch.cache;
         if (!(var9 <= var7)) {
            double var11 = var7 + (var9 - var7) * var5;
            if (!(Math.abs(var11 - this.colorMeasure) <= 1.0E-4F)) {
               this.vectorPerform.handle(var11);
               Double var13 = this.vectorPerform.handle();
               this.colorMeasure = var13 != null ? var13 : this.providerFetch.config;
               this.frameCheck.compute(this.check());
            }
         }
      }
   }

   private boolean process(double var1) {
      double var3 = this.providerFetch.output;
      if (var3 <= 0.0) {
         return false;
      }

      double var5 = this.providerFetch.state;
      double var7 = this.providerFetch.cache;
      double var9 = Math.signum(var1);
      if (var9 == 0.0) {
         return false;
      }

      double var11 = Math.ceil(Math.abs(var1));
      if (var11 <= 0.0) {
         var11 = 1.0;
      }

      double var13 = this.colorMeasure + var3 * var11 * var9;
      double var15 = Math.min(Math.max(var13, var5), var7);
      if (Math.abs(var15 - this.colorMeasure) <= 1.0E-4F) {
         return false;
      }

      this.vectorPerform.handle(var15);
      Double var17 = this.vectorPerform.handle();
      double var18 = var17 instanceof Number ? var17.doubleValue() : this.providerFetch.config;
      this.colorMeasure = var18;
      this.refresh();
      this.frameCheck.compute(this.check());
      return true;
   }

   private float check() {
      return this.compute(this.colorMeasure);
   }

   private float compute(double var1) {
      double var3 = this.providerFetch.state;
      double var5 = this.providerFetch.cache;
      if (var5 <= var3) {
         return 0.0F;
      }

      double var7 = Math.min(Math.max(var1, var3), var5);
      return (float)((var7 - var3) / (var5 - var3));
   }

   private String onTick() {
      return this.eventAttach != null ? this.eventAttach : this.providerFetch.instance;
   }

   private static String handle(String var0) {
      if (var0 == null) {
         return null;
      }

      String var1 = var0.trim();
      return var1.isEmpty() ? null : var1;
   }

   private String select() {
      return this.animationSchedule <= 0
         ? String.format(Locale.US, "%.0f", this.colorMeasure)
         : String.format(Locale.US, "%1$." + this.animationSchedule + "f", this.colorMeasure);
   }

   private static int resolve(double var0) {
      BigDecimal var2 = BigDecimal.valueOf(var0);
      int var3 = var2.scale();
      if (var3 <= 0) {
         return 0;
      }

      BigDecimal var4 = var2.stripTrailingZeros();
      return Math.max(0, var4.scale());
   }

   private void refresh() {
      float var1;
      if (this.presetWrite) {
         var1 = 1.0F;
      } else if (this.rendererScan && this.render()) {
         var1 = 0.5F;
      } else {
         var1 = 0.0F;
      }

      this.positionAdvance.compute(var1);
   }

   private boolean render() {
      return handle(this.moduleCollect.handle()) > 0.001F;
   }

   private static float handle(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }

   private static int handle(int var0, float var1) {
      int var2 = var0 >>> 24 & 0xFF;
      int var3 = Math.round(var2 * var1);
      int var4 = var0 & 16777215;
      return var3 << 24 | var4;
   }

   private static double handle(double var0, int var2, float var3) {
      if (var2 <= 0) {
         return var0;
      } else if (Float.isFinite(var3) && !(Math.abs(var3 - 1.0F) <= 0.001F)) {
         double var4 = var2 * 0.5;
         return var4 + (var0 - var4) / var3;
      } else {
         return var0;
      }
   }

   private static double handle(double var0, MinecraftClient var2) {
      if (var2 != null && var2.getWindow() != null) {
         int var3 = var2.getWindow().getFramebufferWidth();
         if (var3 <= 0) {
            return var0;
         }

         float var4 = 1.0F;
         return Float.isFinite(var4) && !(Math.abs(var4) <= 1.0E-4F) ? handle(var0, var3, var4) : var0;
      } else {
         return var0;
      }
   }

   record Bounds(float x, float y, float width, float height) {
      static final NumberValueEditor.Bounds EMPTY = new NumberValueEditor.Bounds(0.0F, 0.0F, 0.0F, 0.0F);

      boolean contains(double var1, double var3) {
         return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + this.height;
      }
   }
}
