package ru.wild.gui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.Setting;
import ru.wild.gui.screen.ModuleCategoryPanel;
import ru.wild.gui.screen.ModuleSettingListRenderer;
import ru.wild.render.BlurStateManager;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RenderRuntimeContext;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.InputNames;

public final class ModuleCategoryColumn {
   private static final float instance = 20.0F;
   private static final float data = 8.0F;
   private static final float context = 4.0F;
   private static final float config = 20.0F;
   private static final float state = 4.0F;
   private static final float cache = 4.0F;
   private static final float output = 4.0F;
   private final ModuleCategory current;
   private final RenderRuntimeContext active = new RenderRuntimeContext();
   private float mode;
   private float selection;
   private float enabled;
   private float renderer;

   public ModuleCategoryColumn(ModuleCategory var1) {
      this.current = var1;
   }

   public void handle(float var1, float var2, float var3, float var4) {
      this.mode = var1;
      this.selection = var2;
      this.enabled = var3;
      this.renderer = var4;
   }

   public void handle(RoundedRectRenderer var1, int var2, int var3, float var4) {
      int var5 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.update(1, 1), (int)(30.0F * var4));
      int var6 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.compute(1, 1), (int)(160.0F * var4));
      int var7 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.resolve(1, 1), (int)(190.0F * var4));
      int var8 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(220.0F * var4));
      int var9 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(90.0F * var4));
      int var10 = RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.execute(1, 1), (int)(220.0F * var4));
      if (BlurStateManager.output.compute()) {
         var1.handle(this.mode, this.selection, this.enabled, this.renderer, 8.0F, var4);
      }

      var1.handle(this.mode, this.selection, this.enabled, this.renderer, 8.0F, var5, 1.0F);
      var1.handle(this.mode, this.selection, this.enabled, this.renderer, 8.0F, var7);
      var1.handle(
         this.mode,
         this.selection,
         this.enabled,
         20.0F,
         8.0F,
         8.0F,
         0.0F,
         0.0F,
         RoundedRectRenderer.ColorState.select(RoundedRectRenderer.ColorState.apply(1, 1), (int)(15.0F * var4))
      );
      var1.handle(FontRegistry.config, this.mode + 4.0F + 5.0F, this.selection + 6.0F + 6.5F, 16.0F, this.current.process(), var10);
      var1.handle(FontRegistry.state, this.mode + 4.0F + 100.0F, this.selection + 6.0F + 6.5F, 16.0F, this.current.handle(), var10);
      float var11 = this.mode + 4.0F;
      float var12 = this.selection + 20.0F + 4.0F;
      float var13 = this.enabled - 8.0F;
      float var14 = this.renderer - 20.0F - 8.0F;
      boolean var15 = NumericTransform.handle(var2, var3, var11, var12, var13, var14);
      this.active.handle(var15);
      this.active.resolve(6.0F);
      List<Module> var16 = this.handle();
      float var17 = 0.0F;

      for (Module var19 : var16) {
         EasedDoubleAnimator var20 = ModuleCategoryPanel.handle(var19);
         var20.handle();
         float var21 = ModuleSettingListRenderer.handle(var1, var19.apply(), var13 - 10.0F);
         float var22 = var21 > 0.0F ? var21 + 4.0F : 0.0F;
         float var23 = var22 * var20.update();
         var17 += 20.0F + var23 + 4.0F;
      }

      var17 = Math.max(0.0F, var17 - 4.0F);
      this.active.handle(var17, var14);
      this.active.compute();
      var1.handle(var11, var12, var13, var14, 0.0F, 0.0F, 0.0F, 0.0F);
      float var41 = var12 + this.active.prepare();

      for (Module var43 : var16) {
         EasedDoubleAnimator var44 = ModuleCategoryPanel.handle(var43);
         float var45 = var44.update();
         float var46 = ModuleSettingListRenderer.handle(var1, var43.apply(), var13 - 10.0F);
         float var24 = var46 > 0.0F ? var46 + 4.0F : 0.0F;
         float var25 = var24 * var45;
         float var26 = var11;
         float var27 = var41;
         float var28 = var13;
         float var29 = 20.0F + var25;
         if (!(var27 + var29 < var12 - 20.0F) && !(var27 > var12 + var14 + 20.0F)) {
            int var30 = var43.enabled ? PackedColor.update(var6, var8, 0.1F) : var6;
            var1.handle(var26, var27, var28, var29, 4.0F, var30);
            float var31 = var27 + 5.0F + 6.5F;
            var1.handle(FontRegistry.instance, var26 + 6.0F, var31, 13.0F, var43.displayName, var43.enabled ? var10 : var9);
            if (var43.bindingActive || var43.keyCode != -1) {
               String var32 = var43.bindingActive ? "..." : InputNames.process(var43.keyCode);
               float var33 = RoundedRectRenderer.handle(FontRegistry.instance, var32, 10.0F).instance + 6.0F;
               float var34 = var26 + var28 - var33 - 16.0F;
               var1.handle(var34, var27 + 4.0F, var33, 10.0F, 3.0F, var6);
               var1.handle(FontRegistry.config, var34 + 3.0F, var27 + 4.0F + 6.8F, 10.0F, var32, var9);
            }

            if (!var43.apply().isEmpty()) {
               float var47 = var26 + var28 - 12.0F;
               float var49 = var31 + 1.0F;
               var1.handle(FontRegistry.context, var47, var49, 13.0F, "X", PackedColor.update(var9, 0, var45));
            }

            if (var25 > 0.5F && var46 > 0.0F) {
               float var48 = var26 + 5.0F;
               float var50 = var27 + 20.0F + 2.0F;
               float var51 = var28 - 10.0F;
               var1.handle(var26, var27 + 20.0F, var28, var25, 0.0F, 0.0F, 4.0F, 4.0F);
               float var36 = 0.0F;

               for (Setting var38 : var43.apply()) {
                  if (!var38.data.get()) {
                     float var39 = ModuleSettingListRenderer.handle(
                        var1, var38, var48, var50 + var36, var51, var2, var3, var4 * var45, var5, var8, var9, var10, var6
                     );
                     var36 += var39 + ModuleSettingListRenderer.handle();
                  }
               }

               var1.apply();
            }

            var41 += var29 + 4.0F;
         } else {
            var41 += var29 + 4.0F;
         }
      }

      var1.apply();
   }

   public boolean handle(RoundedRectRenderer var1, int var2, int var3, int var4) {
      float var5 = this.mode + 4.0F;
      float var6 = this.selection + 20.0F + 4.0F;
      float var7 = this.enabled - 8.0F;
      float var8 = this.renderer - 20.0F - 8.0F;
      if (!NumericTransform.handle(var2, var3, var5, var6, var7, var8)) {
         return false;
      }

      List<Module> var9 = this.handle();
      float var10 = var6 + this.active.prepare();

      for (Module var12 : var9) {
         EasedDoubleAnimator var13 = ModuleCategoryPanel.handle(var12);
         float var14 = var13.update();
         float var15 = ModuleSettingListRenderer.handle(var1, var12.apply(), var7 - 10.0F);
         float var16 = var15 > 0.0F ? var15 + 4.0F : 0.0F;
         float var17 = var16 * var14;
         float var18 = var5;
         float var19 = var10;
         float var20 = var7;
         float var21 = 20.0F;
         if (NumericTransform.handle(var2, var3, var18, var19, var20, var21)) {
            if (var4 == 1 && !var12.apply().isEmpty()) {
               ModuleCategoryPanel.process(var12);
               return true;
            }

            if (var4 == 2) {
               if (BlurStateManager.matrixBlend != null && BlurStateManager.matrixBlend != var12) {
                  BlurStateManager.matrixBlend.bindingActive = false;
               }

               var12.bindingActive = !var12.bindingActive;
               BlurStateManager.matrixBlend = var12.bindingActive ? var12 : null;
               return true;
            }

            if (var4 == 0) {
               var12.toggle();
               return true;
            }
         }

         if (var17 > 0.5F) {
            float var22 = var18 + 5.0F;
            float var23 = var19 + 20.0F + 2.0F;
            float var24 = var20 - 10.0F;
            float var25 = 0.0F;

            for (Setting var27 : var12.apply()) {
               if (!var27.data.get()) {
                  float var28 = ModuleSettingListRenderer.handle(var1, var27, var24);
                  if (ModuleSettingListRenderer.handle(var1, var27, var22, var23 + var25, var24, var2, var3, var4)) {
                     return true;
                  }

                  var25 += var28 + ModuleSettingListRenderer.handle();
               }
            }
         }

         float var29 = 20.0F + var17;
         var10 += var29 + 4.0F;
      }

      return false;
   }

   public boolean handle(float var1, float var2, double var3) {
      float var5 = this.mode + 4.0F;
      float var6 = this.selection + 20.0F + 4.0F;
      float var7 = this.enabled - 8.0F;
      float var8 = this.renderer - 20.0F - 8.0F;
      if (NumericTransform.handle(var1, var2, var5, var6, var7, var8)) {
         this.active.handle(var3);
         return true;
      } else {
         return false;
      }
   }

   private List<Module> handle() {
      if (WildClient.instance.data == null) {
         return Collections.emptyList();
      }

      ArrayList<Module> var1 = WildClient.instance.data.handle(this.current);
      String var2 = BlurStateManager.providerFetch == null ? "" : BlurStateManager.providerFetch.trim().toLowerCase();
      return var2.isEmpty() ? var1 : var1.stream().filter(var1x -> var1x.displayName != null && var1x.displayName.toLowerCase().contains(var2)).toList();
   }
}
