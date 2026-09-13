package ru.wild.gui.widget;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.wild.module.api.Module;
import ru.wild.api.setting.MultiSelectSetting;
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

public final class ValueSelectorUI implements SettingEditor {
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
   private static final int pointEncode = -11184811;
   private static final float animator = 40.0F;
   private static final float source = 24.0F;
   private static final int target = 58131;
   private static final SpringParameters pending = SpringParameters.handle(1.4F, 0.7F);
   private static final SpringParameters previous = SpringParameters.handle(2.1F, 0.55F);
   private static final SpringParameters latest = SpringParameters.handle(2.2F, 0.6F);
   private static final float summary = 38.0F;
   private static final int matrixBlend = -14408668;
   private static final int vectorMatch = -13750738;
   private static final float itemProject = 18.0F;
   private static final float responseCompute = 6.0F;
   private static final int providerFetch = -1;
   private static final float profileDraw = 1.0E-4F;
   private static final float vectorPerform = 0.001F;
   private static final float eventAttach = 12.0F;
   private static final float serverRead = 10.0F;
   private static final String positionAdvance = "Select values";
   private static final String frameCheck = ", ";
   private final Module moduleCollect;
   private final MultiSelectSetting providerClose;
   private final SettingPopupHost presetSave;
   private final SettingValue<Set<String>> windowConvert;
   private final String presetWrite;
   private final List<String> colorMeasure;
   private final SpringFloat animationSchedule;
   private final SpringFloat rendererScan;
   private final List<SpringFloat> sourceBuild;
   private final List<SpringFloat> outputCollapse;
   private final List<SpringFloat> profileInvoke;
   private final List<Integer> sourceSchedule;
   private static float timerRender = Float.NaN;
   private ValueSelectorUI.Bounds scaleSave = ValueSelectorUI.Bounds.EMPTY;
   private ValueSelectorUI.Bounds colorCompute = ValueSelectorUI.Bounds.EMPTY;
   private ValueSelectorUI.Bounds scaleAdapt = ValueSelectorUI.Bounds.EMPTY;
   private ValueSelectorUI.Bounds textureRun = ValueSelectorUI.Bounds.EMPTY;
   private final List<ValueSelectorUI.Bounds> indexBind = new ArrayList<>();
   private float actionRead = 0.0F;
   private float configCollapse = 0.0F;
   private boolean dataValidate = false;
   private boolean scaleRender = false;
   private int clientRefresh = -1;
   private boolean keyFilter = false;
   private final LinkedHashSet<String> requestAdapt = new LinkedHashSet<>();

   public ValueSelectorUI(Module var1, SettingPopupHost var2, MultiSelectSetting var3, SettingValue<Set<String>> var4) {
      this(var1, var2, var3, var4, null);
   }

   public ValueSelectorUI(Module var1, SettingPopupHost var2, MultiSelectSetting var3, SettingValue<Set<String>> var4, String var5) {
      this.moduleCollect = Objects.requireNonNull(var1, "module");
      this.presetSave = Objects.requireNonNull(var2, "popupContext");
      this.providerClose = Objects.requireNonNull(var3, "setting");
      this.windowConvert = Objects.requireNonNull(var4, "valueAccessor");
      this.presetWrite = handle(var5);
      this.colorMeasure = new ArrayList<>(var3.compute());
      this.animationSchedule = new SpringFloat(AnimationClock.handle(), pending, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.animationSchedule.handle(PopupAnimationCurve.context);
      this.rendererScan = new SpringFloat(AnimationClock.handle(), previous, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
      this.rendererScan.handle(PopupAnimationCurve.context);
      this.sourceBuild = new ArrayList<>();
      this.outputCollapse = new ArrayList<>();
      this.profileInvoke = new ArrayList<>();
      this.sourceSchedule = new ArrayList<>();
      this.render();
   }

   @Override
   public void handle() {
      List var1 = this.providerClose.compute();
      if (var1.size() != this.colorMeasure.size() || !this.colorMeasure.equals(var1)) {
         this.colorMeasure.clear();
         this.colorMeasure.addAll(var1);
         this.render();
         this.select();
      }

      this.animationSchedule.compute(this.scaleRender ? 1.0F : (this.dataValidate ? 0.5F : 0.0F));
      this.rendererScan.compute(this.scaleRender ? 1.0F : 0.0F);
      this.requestAdapt.clear();
      Set var2 = this.windowConvert.handle();
      if (var2 != null) {
         this.requestAdapt.addAll(var2);
      }

      this.keyFilter = !this.requestAdapt.isEmpty();

      for (int var3 = 0; var3 < this.sourceBuild.size(); var3++) {
         float var4 = this.scaleRender && var3 == this.clientRefresh ? 1.0F : 0.0F;
         this.sourceBuild.get(var3).compute(var4);
      }

      for (int var14 = 0; var14 < this.outputCollapse.size(); var14++) {
         String var16 = this.colorMeasure.get(var14);
         float var5 = this.requestAdapt.contains(var16) ? 1.0F : 0.0F;
         this.outputCollapse.get(var14).compute(var5);
      }

      boolean var15 = false;

      for (int var17 = 0; var17 < this.profileInvoke.size(); var17++) {
         SpringFloat var18 = this.outputCollapse.get(var17);
         SpringFloat var6 = this.profileInvoke.get(var17);
         float var7 = handle(var18.handle());
         float var8 = var18.compute();
         boolean var9 = var7 > 1.0E-4F;
         boolean var10 = var8 > 1.0E-4F;
         boolean var11 = Math.abs(var7 - var8) > 1.0E-4F;
         boolean var12 = var15 && (var9 || var11 && var10);
         var6.compute(var12 ? 1.0F : 0.0F);
         boolean var13 = var9 || var10;
         var15 = var15 || var13;
      }

      if (!this.scaleRender) {
         this.clientRefresh = -1;
      }
   }

   @Override
   public void handle(float var1, float var2, float var3) {
      this.scaleSave = new ValueSelectorUI.Bounds(var1, var2, var3, 100.0F);
      this.actionRead = var1 + 18.0F;
      this.configCollapse = var2 + 17.0F + 18.0F;
      float var4 = var1 + 18.0F;
      float var5 = this.configCollapse + 8.0F;
      this.colorCompute = new ValueSelectorUI.Bounds(var4, var5, 298.0F, 38.0F);
      this.scaleAdapt = new ValueSelectorUI.Bounds(var4 + 298.0F - 40.0F, var5, 40.0F, 38.0F);
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
         float var7 = handle(this.animationSchedule.handle());
         int var8 = handle(-7829368, var6);
         int var9 = handle(-1, var6);
         int var10 = ColorCompositor.handle(var8, var9, var7);
         var1.handle(FontRegistry.config, this.actionRead, this.configCollapse - 4.0F, 18.0F, this.refresh(), var10, "l");
         int var11 = handle(-14408668, var6);
         var1.handle(this.colorCompute.x, this.colorCompute.y, this.colorCompute.width, this.colorCompute.height, 6.0F, var11);
         int var12 = handle(AccentColorToken.handle(), var6);
         var1.handle(this.scaleAdapt.x, this.scaleAdapt.y, this.scaleAdapt.width, this.scaleAdapt.height, 0.0F, 6.0F, 6.0F, 0.0F, var12);
         float var13 = this.colorCompute.centerY() + 6.0F;
         float var14 = this.colorCompute.x + 16.0F;
         int var15 = handle(-7829368, var6);
         int var16 = handle(-1, var6);
         int var17 = ColorCompositor.handle(var15, var16, var7);
         float var18 = this.scaleAdapt.x - var14 - 10.0F;
         float var19 = this.colorCompute.height - 8.0F;
         if (var18 > 0.0F && var19 > 0.0F) {
            int var20 = Math.round(var14);
            int var21 = Math.round(this.colorCompute.y + 4.0F);
            int var22 = Math.max(0, Math.round(var18));
            int var23 = Math.max(0, Math.round(var19));
            var1.handle(var20, var21, var22, var23);

            try {
               this.sourceSchedule.clear();

               for (int var24 = 0; var24 < this.colorMeasure.size() && var24 < this.outputCollapse.size(); var24++) {
                  SpringFloat var25 = this.outputCollapse.get(var24);
                  float var26 = handle(var25.handle());
                  float var27 = var25.compute();
                  boolean var28 = var26 > 1.0E-4F;
                  boolean var29 = var26 > 1.0E-4F && var27 <= 1.0E-4F;
                  if (var28 || var29) {
                     this.sourceSchedule.add(var24);
                  }
               }

               if (!this.keyFilter && this.sourceSchedule.isEmpty()) {
                  int var52 = handle(-11184811, var6);
                  var1.handle(FontRegistry.config, var14, var13, 16.0F, "Select values", var52, "l");
               } else {
                  float var51 = var14;
                  float var54 = RoundedRectRenderer.handle(FontRegistry.config, ", ", 16.0F).instance;

                  for (int var55 = 0; var55 < this.sourceSchedule.size(); var55++) {
                     int var56 = this.sourceSchedule.get(var55);
                     SpringFloat var57 = this.outputCollapse.get(var56);
                     float var58 = handle(var57.handle());
                     if (!(var58 <= 1.0E-4F)) {
                        float var30 = this.profileInvoke.size() > var56 ? handle(this.profileInvoke.get(var56).handle()) : 0.0F;
                        String var31 = this.colorMeasure.get(var56);
                        float var32 = (1.0F - var58) * 12.0F;
                        int var33 = handle(var17, var58);
                        float var34 = var30 * var58;
                        if (var34 > 1.0E-4F) {
                           float var35 = var54;
                           float var36 = var34;
                           int var37 = handle(var17, var36);
                           var1.handle(FontRegistry.config, var51 + var32, var13, 16.0F, ", ", var37, "l");
                           var51 += Math.max(0.0F, var35 * var34);
                        }

                        float var59 = RoundedRectRenderer.handle(FontRegistry.config, var31, 16.0F).instance;
                        var1.handle(FontRegistry.config, var51 + var32, var13, 16.0F, var31, var33, "l");
                        var51 += Math.max(0.0F, var59 * var58);
                     }
                  }
               }
            } finally {
               var1.apply();
            }
         }

         float var47 = handle(this.rendererScan.handle()) * 180.0F;
         float var48 = this.scaleAdapt.centerX();
         float var49 = this.scaleAdapt.centerY();
         float var50 = tick();
         float var53 = var49 + var50;
         var1.handle(var48, var53);
         var1.handle(0.0F, -var50);
         var1.process(var47);
         var1.handle(0.0F, var50);
         var1.handle(-var48, -var53);
         boolean var42 = false /* VF: Semaphore variable */;

         try {
            var42 = true;
            var1.handle(FontRegistry.context, var48, var53, 24.0F, "\ue313", handle(-1, var6), "c");
            var42 = false;
         } finally {
            if (var42) {
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
      float var4 = handle(this.rendererScan.handle());
      if (!(var4 <= 0.001F)) {
         if (!this.colorMeasure.isEmpty() && !(this.textureRun.width <= 0.0F) && !(this.textureRun.height <= 0.0F)) {
            float var5 = handle(var3);
            float var6 = var2 * var5 * var4;
            if (!(var6 <= 1.0E-4F)) {
               int var7 = -14408668;
               var1.compute(1.0F, var4, this.textureRun.x, this.textureRun.y);

               try {
                  var1.handle(this.textureRun.x, this.textureRun.y, this.textureRun.width, this.textureRun.height, 6.0F, 6.0F, 6.0F, 6.0F, var7);

                  for (int var8 = 0; var8 < this.indexBind.size(); var8++) {
                     ValueSelectorUI.Bounds var9 = this.indexBind.get(var8);
                     String var10 = this.colorMeasure.get(var8);
                     boolean var11 = this.requestAdapt.contains(var10);
                     float var12 = var8 < this.sourceBuild.size() ? handle(this.sourceBuild.get(var8).handle()) : 0.0F;
                     if (var12 > 0.001F) {
                        int var13 = handle(-13750738, var12 * var6);
                        float var14 = var8 == 0 ? 6.0F : 0.0F;
                        float var15 = var8 == 0 ? 6.0F : 0.0F;
                        float var16 = var8 == this.indexBind.size() - 1 ? 6.0F : 0.0F;
                        float var17 = var8 == this.indexBind.size() - 1 ? 6.0F : 0.0F;
                        var1.handle(var9.x, var9.y, var9.width, var9.height, var14, var15, var16, var17, var13);
                     }

                     float var26 = var9.x + 16.0F;
                     float var27 = var9.centerY() + 6.0F;
                     int var28 = handle(-7829368, var6);
                     int var29 = handle(-1, var6);
                     float var30 = var11 ? 1.0F : var12 * 0.7F;
                     int var18 = ColorCompositor.handle(var28, var29, var30);
                     var1.handle(FontRegistry.config, var26, var27, 16.0F, var10, var18, "l");
                     float var19 = var8 < this.outputCollapse.size() ? handle(this.outputCollapse.get(var8).handle()) : (var11 ? 1.0F : 0.0F);
                     if (var19 > 1.0E-4F) {
                        float var20 = var9.x + var9.width - 16.0F + 2.0F;
                        float var21 = var9.centerY() + 6.0F + 3.0F;
                        int var22 = handle(-1, var6 * var19);
                        var1.handle(FontRegistry.context, var20, var21, 18.0F, "\ue5ca", var22, "r");
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
      return (this.scaleRender || this.rendererScan.handle() > 0.001F) && this.textureRun.width > 0.0F && this.textureRun.height > 0.0F;
   }

   @Override
   public boolean process(double var1, double var3, int var5) {
      if (!this.update()) {
         return false;
      }

      if (!this.scaleRender) {
         return false;
      }

      if (var5 != 0) {
         this.check();
         return true;
      }

      if (this.textureRun.contains(var1, var3)) {
         int var6 = this.handle(var3);
         if (var6 >= 0 && var6 < this.colorMeasure.size()) {
            this.handle(var6);
         }

         return true;
      } else if (!this.colorCompute.contains(var1, var3) && !this.scaleAdapt.contains(var1, var3)) {
         this.check();
         return true;
      } else {
         this.check();
         return true;
      }
   }

   @Override
   public boolean handle(double var1, double var3, int var5) {
      boolean var6 = this.colorCompute.contains(var1, var3) || this.scaleAdapt.contains(var1, var3);
      if (var5 == 2) {
         if (!var6) {
            return false;
         }

         this.check();
         LinkedHashSet var7 = new LinkedHashSet<>(this.requestAdapt);
         this.presetSave.openForSetting(this.moduleCollect, this.providerClose, var1, var3, var7);
         return true;
      } else if (var5 != 0) {
         return false;
      } else if (this.scaleRender) {
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
      boolean var5 = this.colorCompute.contains(var1, var3) || this.scaleAdapt.contains(var1, var3);
      boolean var6 = this.scaleRender && this.textureRun.contains(var1, var3);
      this.dataValidate = this.scaleRender ? false : var5;
      if (this.scaleRender) {
         if (var6) {
            this.clientRefresh = this.handle(var3);
         } else {
            this.clientRefresh = -1;
         }
      }
   }

   @Override
   public void apply() {
      this.onTick();
   }

   @Override
   public Setting compute() {
      return this.providerClose;
   }

   @Override
   public boolean resolve() {
      return true;
   }

   private void prepare() {
      this.scaleRender = true;
      this.rendererScan.compute(1.0F);
      this.animationSchedule.compute(1.0F);
   }

   private void check() {
      this.scaleRender = false;
      this.rendererScan.compute(0.0F);
      this.clientRefresh = -1;
   }

   private void onTick() {
      this.scaleRender = false;
      this.rendererScan.process(0.0F);
      this.clientRefresh = -1;
   }

   private void handle(int var1) {
      if (var1 >= 0 && var1 < this.colorMeasure.size()) {
         String var2 = this.colorMeasure.get(var1);
         LinkedHashSet var3 = new LinkedHashSet<>(this.requestAdapt);
         if (var3.contains(var2)) {
            var3.remove(var2);
         } else {
            var3.add(var2);
         }

         this.windowConvert.handle(var3);
         this.requestAdapt.clear();
         this.requestAdapt.addAll(var3);
      }
   }

   private void select() {
      this.indexBind.clear();
      if (this.colorMeasure.isEmpty()) {
         this.textureRun = ValueSelectorUI.Bounds.EMPTY;
      } else {
         float var1 = this.colorCompute.x;
         float var2 = this.colorCompute.y + this.colorCompute.height + 6.0F;
         float var3 = this.colorCompute.width;
         float var4 = 38.0F * this.colorMeasure.size();
         this.textureRun = new ValueSelectorUI.Bounds(var1, var2, var3, var4);
         float var5 = var2;

         for (int var6 = 0; var6 < this.colorMeasure.size(); var6++) {
            this.indexBind.add(new ValueSelectorUI.Bounds(var1, var5, var3, 38.0F));
            var5 += 38.0F;
         }
      }
   }

   private int handle(double var1) {
      if (!(var1 < this.textureRun.y) && !(var1 > this.textureRun.y + this.textureRun.height)) {
         double var3 = var1 - this.textureRun.y;
         if (var3 < 0.0) {
            return -1;
         }

         int var5 = (int)(var3 / 38.0);
         return var5 >= 0 && var5 < this.colorMeasure.size() ? var5 : -1;
      } else {
         return -1;
      }
   }

   private String refresh() {
      return this.presetWrite != null ? this.presetWrite : this.providerClose.instance;
   }

   private static String handle(String var0) {
      if (var0 == null) {
         return null;
      }

      String var1 = var0.trim();
      return var1.isEmpty() ? null : var1;
   }

   private void render() {
      this.sourceBuild.clear();
      this.outputCollapse.clear();
      this.profileInvoke.clear();
      this.sourceSchedule.clear();

      for (int var1 = 0; var1 < this.colorMeasure.size(); var1++) {
         SpringFloat var2 = new SpringFloat(AnimationClock.handle(), pending, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
         var2.handle(PopupAnimationCurve.context);
         this.sourceBuild.add(var2);
         SpringFloat var3 = new SpringFloat(AnimationClock.handle(), latest, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
         var3.handle(PopupAnimationCurve.context);
         this.outputCollapse.add(var3);
         SpringFloat var4 = new SpringFloat(AnimationClock.handle(), latest, 0.0F, 0.0F, 1.0F, 5.0E-4F, 5.0E-4F);
         var4.handle(PopupAnimationCurve.context);
         this.profileInvoke.add(var4);
      }
   }

   private static float tick() {
      if (Float.isNaN(timerRender)) {
         float var0 = FontRegistry.handle(FontRegistry.context, 58131, 24.0F);
         timerRender = var0;
      }

      return timerRender;
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

   record Bounds(float x, float y, float width, float height) {
      static final ValueSelectorUI.Bounds EMPTY = new ValueSelectorUI.Bounds(0.0F, 0.0F, 0.0F, 0.0F);

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
