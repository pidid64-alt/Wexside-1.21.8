package ru.wild.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.wild.module.api.Module;
import ru.wild.api.setting.ModeSetting;
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

public final class TextValueEditor implements SettingEditor {
   private static final float instance = 100.0F;
   private static final float data = 18.0F;
   private static final float context = 18.0F;
   private static final float config = 17.0F;
   private static final float state = 8.0F;
   private static final float cache = 298.0F;
   private static final float output = 38.0F;
   private static final float current = 6.0F;
   private static final float active = 18.0F;
   private static final int mode = -14408668;
   private static final float selection = 16.0F;
   private static final float enabled = 16.0F;
   private static final float renderer = 6.0F;
   private static final int handler = -7829368;
   private static final int animationDraw = -1;
   private static final float pointEncode = 40.0F;
   private static final float animator = 24.0F;
   private static final int source = 58131;
   private static final SpringParameters target = SpringParameters.handle(1.4F, 0.7F);
   private static final SpringParameters pending = SpringParameters.handle(2.1F, 0.55F);
   private static final float previous = 38.0F;
   private static final int latest = -14408668;
   private static final int summary = -13750738;
   private static final float matrixBlend = 18.0F;
   private static final float vectorMatch = 6.0F;
   private static final int itemProject = -1;
   private static final float responseCompute = 1.0E-4F;
   private static final float providerFetch = 0.001F;
   private final Module profileDraw;
   private final ModeSetting vectorPerform;
   private final SettingPopupHost eventAttach;
   private final SettingValue<String> serverRead;
   private final String positionAdvance;
   private final List<String> frameCheck;
   private final SpringFloat moduleCollect;
   private final SpringFloat providerClose;
   private final List<SpringFloat> presetSave;
   private static float windowConvert = Float.NaN;
   private TextValueEditor.Bounds presetWrite = TextValueEditor.Bounds.EMPTY;
   private TextValueEditor.Bounds colorMeasure = TextValueEditor.Bounds.EMPTY;
   private TextValueEditor.Bounds animationSchedule = TextValueEditor.Bounds.EMPTY;
   private TextValueEditor.Bounds rendererScan = TextValueEditor.Bounds.EMPTY;
   private final List<TextValueEditor.Bounds> sourceBuild = new ArrayList<>();
   private float outputCollapse = 0.0F;
   private float profileInvoke = 0.0F;
   private boolean sourceSchedule = false;
   private boolean timerRender = false;
   private int scaleSave = -1;

   public TextValueEditor(Module var1, SettingPopupHost var2, ModeSetting var3, SettingValue<String> var4) {
      this(var1, var2, var3, var4, null);
   }

   public TextValueEditor(Module var1, SettingPopupHost var2, ModeSetting var3, SettingValue<String> var4, String var5) {
      this.profileDraw = Objects.requireNonNull(var1, "module");
      this.eventAttach = Objects.requireNonNull(var2, "popupContext");
      this.vectorPerform = Objects.requireNonNull(var3, "setting");
      this.serverRead = Objects.requireNonNull(var4, "valueAccessor");
      this.positionAdvance = handle(var5);
      this.frameCheck = new ArrayList<>(var3.config != null ? var3.config : List.of());
      this.moduleCollect = new SpringFloat(AnimationClock.handle(), target, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.moduleCollect.handle(PopupAnimationCurve.context);
      this.providerClose = new SpringFloat(AnimationClock.handle(), pending, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.providerClose.handle(PopupAnimationCurve.context);
      this.presetSave = new ArrayList<>();
      this.render();
   }

   @Override
   public void handle() {
      List var1 = this.vectorPerform.config != null ? this.vectorPerform.config : List.of();
      if (var1.size() != this.frameCheck.size() || !this.frameCheck.equals(var1)) {
         this.frameCheck.clear();
         this.frameCheck.addAll(var1);
         this.render();
         this.select();
      }

      this.moduleCollect.compute(this.timerRender ? 1.0F : (this.timerRender ? 0.0F : (this.sourceSchedule ? 0.5F : 0.0F)));
      this.providerClose.compute(this.timerRender ? 1.0F : 0.0F);

      for (int var2 = 0; var2 < this.presetSave.size(); var2++) {
         float var3 = this.timerRender && var2 == this.scaleSave ? 1.0F : 0.0F;
         this.presetSave.get(var2).compute(var3);
      }

      if (!this.timerRender) {
         this.scaleSave = -1;
      }
   }

   @Override
   public void handle(float var1, float var2, float var3) {
      this.presetWrite = new TextValueEditor.Bounds(var1, var2, var3, 100.0F);
      this.outputCollapse = var1 + 18.0F;
      this.profileInvoke = var2 + 17.0F + 18.0F;
      float var4 = var1 + 18.0F;
      float var5 = this.profileInvoke + 8.0F;
      this.colorMeasure = new TextValueEditor.Bounds(var4, var5, 298.0F, 38.0F);
      this.animationSchedule = new TextValueEditor.Bounds(var4 + 298.0F - 40.0F, var5, 40.0F, 38.0F);
      this.select();
   }

   @Override
   public float process() {
      return 100.0F;
   }
   @Override
   public void handle(RoundedRectRenderer var1, float var2, float var3, float var4) {
      float var5 = handle(var3);
      float var6 = var2 * var5;
      if (!(var6 <= 1.0E-4F)) {
         float var7 = handle(this.moduleCollect.handle());
         int var8 = handle(-7829368, var6);
         int var9 = handle(-1, var6);
         int var10 = ColorCompositor.handle(var8, var9, var7);
         var1.handle(FontRegistry.config, this.outputCollapse, this.profileInvoke - 4.0F, 18.0F, this.refresh(), var10, "l");
         int var11 = handle(-14408668, var6);
         var1.handle(this.colorMeasure.x, this.colorMeasure.y, this.colorMeasure.width, this.colorMeasure.height, 6.0F, var11);
         int var12 = handle(AccentColorToken.handle(), var6);
         var1.handle(
            this.animationSchedule.x, this.animationSchedule.y, this.animationSchedule.width, this.animationSchedule.height, 0.0F, 6.0F, 6.0F, 0.0F, var12
         );
         float var13 = this.colorMeasure.centerY() + 6.0F;
         float var14 = this.colorMeasure.x + 16.0F;
         String var15 = this.serverRead.handle();
         if (var15 == null) {
            var15 = "";
         }

         int var16 = handle(-7829368, var6);
         int var17 = handle(-1, var6);
         int var18 = ColorCompositor.handle(var16, var17, var7);
         var1.handle(FontRegistry.config, var14, var13, 16.0F, var15, var18, "l");
         float var19 = handle(this.providerClose.handle()) * 180.0F;
         float var20 = this.animationSchedule.centerX();
         float var21 = this.animationSchedule.centerY();
         float var22 = tick();
         float var23 = var21 + var22;
         var1.handle(var20, var23);
         var1.handle(0.0F, -var22);
         var1.process(var19);
         var1.handle(0.0F, var22);
         var1.handle(-var20, -var23);
         boolean var26 = false /* VF: Semaphore variable */;

         try {
            var26 = true;
            var1.handle(FontRegistry.context, var20, var23, 24.0F, "\ue313", handle(-1, var6), "c");
            var26 = false;
         } finally {
            if (var26) {
               var1.prepare();
               var1.prepare();
               var1.execute();
               var1.prepare();
               var1.prepare();
            }
         }

         var1.prepare();
         var1.prepare();
         var1.execute();
         var1.prepare();
         var1.prepare();
      }
   }

   @Override
   public void handle(RoundedRectRenderer var1, float var2, float var3) {
      float var4 = handle(this.providerClose.handle());
      if (!(var4 <= 0.001F)) {
         if (!this.frameCheck.isEmpty() && !(this.rendererScan.width <= 0.0F) && !(this.rendererScan.height <= 0.0F)) {
            float var5 = handle(var3);
            float var6 = var2 * var5 * var4;
            if (!(var6 <= 1.0E-4F)) {
               int var7 = -14408668;
               var1.compute(1.0F, var4, this.rendererScan.x, this.rendererScan.y);

               try {
                  var1.handle(this.rendererScan.x, this.rendererScan.y, this.rendererScan.width, this.rendererScan.height, 6.0F, 6.0F, 6.0F, 6.0F, var7);
                  String var8 = this.serverRead.handle();
                  if (var8 == null) {
                     var8 = "";
                  }

                  for (int var9 = 0; var9 < this.sourceBuild.size(); var9++) {
                     TextValueEditor.Bounds var10 = this.sourceBuild.get(var9);
                     String var11 = this.frameCheck.get(var9);
                     boolean var12 = Objects.equals(var11, var8);
                     float var13 = var9 < this.presetSave.size() ? handle(this.presetSave.get(var9).handle()) : 0.0F;
                     if (var13 > 0.001F) {
                        int var14 = handle(-13750738, var13 * var6);
                        float var15 = var9 == 0 ? 6.0F : 0.0F;
                        float var16 = var9 == 0 ? 6.0F : 0.0F;
                        float var17 = var9 == this.sourceBuild.size() - 1 ? 6.0F : 0.0F;
                        float var18 = var9 == this.sourceBuild.size() - 1 ? 6.0F : 0.0F;
                        var1.handle(var10.x, var10.y, var10.width, var10.height, var15, var16, var17, var18, var14);
                     }

                     float var25 = var10.x + 16.0F;
                     float var26 = var10.centerY() + 6.0F;
                     int var27 = handle(-7829368, var6);
                     int var28 = handle(-1, var6);
                     float var29;
                     if (var12) {
                        var29 = 1.0F;
                     } else {
                        var29 = var13 * 0.7F;
                     }

                     int var19 = ColorCompositor.handle(var27, var28, var29);
                     var1.handle(FontRegistry.config, var25, var26, 16.0F, var11, var19, "l");
                     if (Objects.equals(var11, var8)) {
                        float var20 = var10.x + var10.width - 16.0F + 2.0F;
                        float var21 = var10.centerY() + 6.0F + 3.0F;
                        var1.handle(FontRegistry.context, var20, var21, 18.0F, "\ue5ca", handle(-1, var6), "r");
                     }
                  }
               } finally {
                  var1.check();
               }
            }
         }
      }
   }

   @Override
   public boolean update() {
      return (this.timerRender || this.providerClose.handle() > 0.001F) && this.rendererScan.width > 0.0F && this.rendererScan.height > 0.0F;
   }

   @Override
   public boolean process(double var1, double var3, int var5) {
      if (!this.update()) {
         return false;
      }

      if (!this.timerRender) {
         return false;
      }

      if (var5 != 0) {
         this.check();
         return true;
      }

      if (this.rendererScan.contains(var1, var3)) {
         int var6 = this.handle(var3);
         if (var6 >= 0 && var6 < this.frameCheck.size()) {
            this.handle(var6);
         }

         this.check();
         return true;
      } else if (!this.colorMeasure.contains(var1, var3) && !this.animationSchedule.contains(var1, var3)) {
         this.check();
         return true;
      } else {
         this.check();
         return true;
      }
   }

   @Override
   public boolean handle(double var1, double var3, int var5) {
      boolean var6 = this.colorMeasure.contains(var1, var3) || this.animationSchedule.contains(var1, var3);
      if (var5 == 2) {
         if (!var6) {
            return false;
         }

         this.check();
         String var7 = this.serverRead.handle();
         String var8 = var7 != null ? var7.toString() : "";
         this.eventAttach.openForSetting(this.profileDraw, this.vectorPerform, var1, var3, var8);
         return true;
      } else if (var5 != 0) {
         return false;
      } else if (this.timerRender) {
         return this.process(var1, var3, var5);
      } else if (var6) {
         this.prepare();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean handle(double var1, double var3, double var5, double var7) {
      return this.update();
   }

   @Override
   public void handle(double var1, double var3) {
      boolean var5 = this.colorMeasure.contains(var1, var3) || this.animationSchedule.contains(var1, var3);
      boolean var6 = this.timerRender && this.rendererScan.contains(var1, var3);
      this.sourceSchedule = this.timerRender ? false : var5;
      if (this.timerRender) {
         if (var6) {
            this.scaleSave = this.handle(var3);
         } else {
            this.scaleSave = -1;
         }
      }
   }

   @Override
   public void apply() {
      this.onTick();
   }

   @Override
   public Setting compute() {
      return this.vectorPerform;
   }

   @Override
   public boolean resolve() {
      return true;
   }

   private void prepare() {
      this.timerRender = true;
      this.providerClose.compute(1.0F);
      this.moduleCollect.compute(1.0F);
   }

   private void check() {
      this.timerRender = false;
      this.providerClose.compute(0.0F);
      this.scaleSave = -1;
   }

   private void onTick() {
      this.timerRender = false;
      this.providerClose.process(0.0F);
      this.scaleSave = -1;
   }

   private void handle(int var1) {
      if (var1 >= 0 && var1 < this.frameCheck.size()) {
         String var2 = this.frameCheck.get(var1);
         String var3 = this.serverRead.handle();
         if (!Objects.equals(var2, var3)) {
            this.serverRead.handle(var2);
         }
      }
   }

   private void select() {
      this.sourceBuild.clear();
      if (this.frameCheck.isEmpty()) {
         this.rendererScan = TextValueEditor.Bounds.EMPTY;
      } else {
         float var1 = this.colorMeasure.x;
         float var2 = this.colorMeasure.y + this.colorMeasure.height + 6.0F;
         float var3 = this.colorMeasure.width;
         float var4 = 38.0F * this.frameCheck.size();
         this.rendererScan = new TextValueEditor.Bounds(var1, var2, var3, var4);
         float var5 = var2;

         for (int var6 = 0; var6 < this.frameCheck.size(); var6++) {
            this.sourceBuild.add(new TextValueEditor.Bounds(var1, var5, var3, 38.0F));
            var5 += 38.0F;
         }
      }
   }

   private int handle(double var1) {
      if (!(var1 < this.rendererScan.y) && !(var1 > this.rendererScan.y + this.rendererScan.height)) {
         double var3 = var1 - this.rendererScan.y;
         if (var3 < 0.0) {
            return -1;
         }

         int var5 = (int)(var3 / 38.0);
         return var5 >= 0 && var5 < this.frameCheck.size() ? var5 : -1;
      } else {
         return -1;
      }
   }

   private String refresh() {
      return this.positionAdvance != null ? this.positionAdvance : this.vectorPerform.instance;
   }

   private static String handle(String var0) {
      if (var0 == null) {
         return null;
      }

      String var1 = var0.trim();
      return var1.isEmpty() ? null : var1;
   }

   private static float handle(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0;
      }
   }

   private void render() {
      this.presetSave.clear();

      for (int var1 = 0; var1 < this.frameCheck.size(); var1++) {
         SpringFloat var2 = new SpringFloat(AnimationClock.handle(), target, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
         var2.handle(PopupAnimationCurve.context);
         this.presetSave.add(var2);
      }
   }

   private static float tick() {
      if (Float.isNaN(windowConvert)) {
         float var0 = FontRegistry.handle(FontRegistry.context, 58131, 24.0F);
         windowConvert = var0;
      }

      return windowConvert;
   }

   private static int handle(int var0, float var1) {
      int var2 = var0 >>> 24 & 0xFF;
      int var3 = Math.round(var2 * var1);
      int var4 = var0 & 16777215;
      return var3 << 24 | var4;
   }

   record Bounds(float x, float y, float width, float height) {
      static final TextValueEditor.Bounds EMPTY = new TextValueEditor.Bounds(0.0F, 0.0F, 0.0F, 0.0F);

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
