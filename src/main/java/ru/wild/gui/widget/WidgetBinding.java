package ru.wild.gui.widget;

import java.util.Objects;
import org.wild.module.api.Module;
import ru.wild.api.setting.BooleanSetting;
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

public final class WidgetBinding implements SettingEditor {
   private static final float instance = 62.0F;
   private static final float data = 18.0F;
   private static final float context = 18.0F;
   private static final float config = 5.0F;
   private static final float state = 22.0F;
   private static final float cache = 4.0F;
   private static final float output = 18.0F;
   private static final float current = 16.0F;
   private static final float active = 5.0F;
   private static final SpringParameters mode = SpringParameters.handle(2.1F, 0.55F);
   private static final float selection = 0.001F;
   private static final SpringParameters enabled = SpringParameters.handle(1.4F, 0.7F);
   private final Module renderer;
   private final BooleanSetting handler;
   private final SettingPopupHost animationDraw;
   private final SettingValue<Boolean> pointEncode;
   private final String animator;
   private final SpringFloat source;
   private final SpringFloat target;
   private WidgetBinding.Bounds pending = WidgetBinding.Bounds.EMPTY;
   private WidgetBinding.Bounds previous = WidgetBinding.Bounds.EMPTY;
   private float latest = 0.0F;
   private float summary = 0.0F;
   private boolean matrixBlend = false;

   public WidgetBinding(Module var1, SettingPopupHost var2, BooleanSetting var3, SettingValue<Boolean> var4) {
      this(var1, var2, var3, var4, null);
   }

   public WidgetBinding(Module var1, SettingPopupHost var2, BooleanSetting var3, SettingValue<Boolean> var4, String var5) {
      this.renderer = Objects.requireNonNull(var1, "module");
      this.animationDraw = Objects.requireNonNull(var2, "popupContext");
      this.handler = Objects.requireNonNull(var3, "setting");
      this.pointEncode = Objects.requireNonNull(var4, "valueAccessor");
      this.animator = handle(var5);
      Object var6 = var4.handle();
      boolean var7 = var6 instanceof Boolean ? (Boolean)var6 : false;
      float var8 = var7 ? 1.0F : 0.0F;
      this.source = new SpringFloat(AnimationClock.handle(), mode, var8, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.source.handle(PopupAnimationCurve.instance);
      this.target = new SpringFloat(AnimationClock.handle(), enabled, var8, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.target.handle(PopupAnimationCurve.context);
   }

   @Override
   public void handle() {
      Object var1 = this.pointEncode.handle();
      boolean var2 = var1 instanceof Boolean ? (Boolean)var1 : false;
      this.source.compute(var2 ? 1.0F : 0.0F);
      this.prepare();
   }

   @Override
   public void handle(float var1, float var2, float var3) {
      this.pending = new WidgetBinding.Bounds(var1, var2, var3, 62.0F);
      float var4 = var1 + var3 - 18.0F - 22.0F;
      float var5 = var2 + 20.0F;
      this.previous = new WidgetBinding.Bounds(var4, var5, 22.0F, 22.0F);
      this.latest = var1 + 18.0F;
      this.summary = var2 + 31.0F + 5.0F;
   }

   @Override
   public float process() {
      return 62.0F;
   }

   @Override
   public void handle(RoundedRectRenderer var1, float var2, float var3, float var4) {
      float var5 = var2 * (float)handle(var3);
      if (!(var5 <= 0.0F)) {
         float var6 = this.source.handle();
         double var7 = handle(var5 * var6);
         if (var7 > 0.001F) {
            var1.handle(
               this.previous.x + 1.0F,
               this.previous.y + 1.0F,
               this.previous.width - 2.0F,
               this.previous.height - 2.0F,
               4.0F,
               ColorCompositor.handle(AccentColorToken.handle(), var7)
            );
         }

         double var9 = handle(var5);
         var1.handle(this.previous.x, this.previous.y, this.previous.width, this.previous.height, 4.0F, ColorCompositor.handle(5197646, var9), 1.0F);
         double var11 = handle(var6 * var5);
         if (var11 > 0.001F) {
            var1.handle(
               FontRegistry.context,
               this.previous.centerX(),
               this.previous.centerY() + 5.0F + 3.0F,
               16.0F,
               "\ue5ca",
               ColorCompositor.handle(16777215, var11),
               "c"
            );
         }

         double var13 = handle(var5);
         float var15 = this.target.handle();
         int var16 = ColorCompositor.handle(8947848, var13);
         int var17 = ColorCompositor.handle(16777215, var13);
         int var18 = ColorCompositor.handle(var16, var17, var15);
         var1.handle(FontRegistry.config, this.latest, this.summary, 18.0F, this.check(), var18, "l");
      }
   }

   @Override
   public boolean handle(double var1, double var3, int var5) {
      if (!this.pending.contains(var1, var3)) {
         return false;
      }

      if (var5 == 2) {
         Object var9 = this.pointEncode.handle();
         Boolean var10 = var9 instanceof Boolean ? (Boolean)var9 : false;
         this.animationDraw.openForSetting(this.renderer, this.handler, var1, var3, var10);
         return true;
      }

      if (var5 != 0) {
         return false;
      }

      Object var6 = this.pointEncode.handle();
      boolean var7 = var6 instanceof Boolean ? (Boolean)var6 : false;
      boolean var8 = !var7;
      this.pointEncode.handle(var8);
      this.source.compute(var8 ? 1.0F : 0.0F);
      return true;
   }

   @Override
   public Setting compute() {
      return this.handler;
   }

   @Override
   public boolean resolve() {
      return true;
   }

   @Override
   public void handle(double var1, double var3) {
      this.matrixBlend = this.pending.contains(var1, var3);
      this.prepare();
   }

   private void prepare() {
      Object var2 = this.pointEncode.handle();
      boolean var3 = var2 instanceof Boolean ? (Boolean)var2 : false;
      float var1;
      if (var3) {
         var1 = 1.0F;
      } else if (this.matrixBlend) {
         var1 = 0.5F;
      } else {
         var1 = 0.0F;
      }

      this.target.compute(var1);
   }

   private String check() {
      return this.animator != null ? this.animator : this.handler.instance;
   }

   private static String handle(String var0) {
      if (var0 == null) {
         return null;
      }

      String var1 = var0.trim();
      return var1.isEmpty() ? null : var1;
   }

   private static double handle(double var0) {
      if (var0 <= 0.0) {
         return 0.0;
      } else {
         return var0 >= 1.0 ? 1.0 : var0;
      }
   }

   record Bounds(float x, float y, float width, float height) {
      static final WidgetBinding.Bounds EMPTY = new WidgetBinding.Bounds(0.0F, 0.0F, 0.0F, 0.0F);

      boolean contains(double var1, double var3) {
         return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + this.height;
      }

      float centerX() {
         return this.x + this.width * 0.5F;
      }

      float centerY() {
         return this.y + this.height * 0.5F;
      }
   }
}
