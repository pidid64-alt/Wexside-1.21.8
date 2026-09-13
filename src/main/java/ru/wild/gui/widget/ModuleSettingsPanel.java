package ru.wild.gui.widget;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import org.wild.module.api.Module;
import ru.wild.api.module.AnticheatProfiles;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.core.AnimationClock;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.AnticheatProfileColor;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.modules.visuals.Menu;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.math.TooltipMotion;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

public final class ModuleSettingsPanel {
   static final float instance = 120.0F;
   static final float data = 14.0F;
   static final SpringParameters context = SpringParameters.handle((float)(Math.sqrt(120.0) / (Math.PI * 2)), 14.0F / (2.0F * (float)Math.sqrt(120.0)));
   static final SpringAnimationSpec config = SpringAnimationSpec.prepare();
   static final float state = (float) (Math.PI * 2.0 / 3.0);
   static final float cache = (float) (Math.PI * 2.0 / 9.0);
   static final float output = 1.35F;
   static final float current = 15.12F;
   static final float active = 26.0F;
   private final SettingControlRenderer mode;
   private final Map<Module, SpringFloat> selection = new IdentityHashMap<>();
   private final Map<Module, List<ModuleFlag>> enabled = new IdentityHashMap<>();
   private final Map<Module, EnumMap<ModuleFlag, String>> renderer = new IdentityHashMap<>();
   private final Map<ModuleFlag, String> handler = new EnumMap<>(ModuleFlag.class);
   public void handle(RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ModulePlacement var4, ThemeRenderContext var5, float var6) {
      Module var7 = var4.handle();
      GuiMetrics var8 = var5.update();
      ModuleCardTransform var9 = ModuleCardTransform.resolve(var3, var4, var8);
      if (var9.visible()) {
         ThemeColors var10 = var5.apply();
         float var11 = var4.process();
         float var12 = var4.compute();
         float var13 = var4.resolve();
         String var14 = UiAnimationKeys.process(var7);
         float var15 = var3.handle(var14);
         float var16 = var3.handle(UiAnimationKeys.compute(var7));
         float var17 = var3.handle(UiAnimationKeys.prepare(var7));
         float var18 = var9.searchVisibility();
         float var19 = var18 * Math.min(1.0F, var9.cardEntry());
         float var20 = var9.pivotX();
         float var21 = var9.pivotY();
         var1.update(var19);

         try {
            var1.handle(0.0F, var9.slideY());

            try {
               var1.handle(var9.scale(), var20, var21);

               try {
                  float var22 = var9.lift();
                  float var23 = var12 - var22;
                  float var24 = var4.update();
                  float var25 = var8.handle(8.0F);
                  boolean var26 = var3.filterMatrix();
                  float var27 = var26 ? 1.0F : this.compute(Math.min(var18, var17));
                  float var28 = this.handle();
                  float var29 = this.handle(0.02F, 0.9F, var6);
                  boolean var30 = Menu.handle(Menu.positionAdvance);
                  if (!var26 && var30) {
                     this.handle(var1, var8, var10, var11, var23, var13, var24, var25, var27);
                  }

                  if (var10.unload()) {
                     int var31 = ThemeColors.handle(46, 59, 70, Math.round(6.0F + 4.0F * var15 + 2.0F * var29));
                     var1.handle(var11, var23 + var8.handle(2.0F), var13, var24, var25, var8.handle(5.0F + 2.0F * var29 + var15), var8.handle(0.9F), var31);
                  }

                  if (var16 > 0.3F) {
                     int var105 = var10.unload()
                        ? ThemeColors.handle(46, 59, 70, Math.round(8.0F * var16))
                        : ThemeColors.handle(0, 0, 0, Math.round(14.0F * var16));
                     var1.handle(
                        var11,
                        var23,
                        var13,
                        var24,
                        var25,
                        var8.handle(var10.unload() ? 6.0F : 4.0F) * var16,
                        var8.handle(var10.unload() ? 1.0F : 0.75F),
                        var105
                     );
                  }

                  if (var15 > 0.01F) {
                     var1.handle(
                        var11,
                        var23,
                        var13,
                        var24,
                        var25,
                        var8.handle(var10.unload() ? 6.0F : 4.0F) * var15,
                        var8.handle(var10.unload() ? 1.0F : 0.75F),
                        var10.unload() ? ThemeColors.handle(46, 59, 70, Math.round(10.0F * var15)) : ThemeColors.handle(0, 0, 0, Math.round(22.0F * var15))
                     );
                  }

                  float var106 = var9.scale();
                  float var32 = var20 + (var3.sampleLayer() - var20) / var106;
                  float var33 = var21 + (var3.sendWorld() - var9.slideY() - var21) / var106;
                  float var34 = var15 > 0.001F ? this.compute((var32 - var11) / Math.max(1.0F, var13), 0.07F, 0.93F) : 0.5F;
                  float var35 = var15 > 0.001F ? this.compute((var33 - var23) / Math.max(1.0F, var24), 0.1F, 0.84F) : 0.5F;
                  float var36 = TooltipMotion.handle(var27);
                  var1.update(var36);
                  boolean var97 = false /* VF: Semaphore variable */;

                  try {
                     var97 = true;
                     this.handle(var1, var7, var11, var23, var13, var24, var25, var34, var35, var15, var5);
                     var97 = false;
                  } finally {
                     if (var97) {
                        var1.onTick();
                     }
                  }

                  var1.onTick();
                  boolean var37 = !var26 && var30 && var27 < 0.995F;
                  RoundedRectRenderer.PrimaryColorState var38 = var37 ? var1.process(var11, var23, var13, var24) : null;
                  boolean var39 = false;
                  if (var38 != null) {
                     try {
                        this.handle(var1, var2, var3, var4, var7, var11, var23, var13, var24, var16, var15, var32, var33, var5);
                     } finally {
                        var1.handle(var38);
                     }

                     float var40 = TooltipMotion.process(var27);
                     float var41 = Math.max(TooltipMotion.compute(var27), TooltipMotion.resolve(var27));
                     int var42 = ThemeColors.handle(ThemeColors.handle(var10.submit(), var10.save(), 0.5F), Math.round(255.0F * var41));
                     var1.update(var40);

                     try {
                        var39 = var1.handle(
                           var38, var11, var23, var13, var24, var25, ThemeColors.handle(var10.save(), 255), var10.submit(), var42, var27, var28
                        );
                     } finally {
                        var1.onTick();
                     }
                  }

                  if (!var39) {
                     float var107 = var37 ? TooltipMotion.process(var27) : 1.0F;
                     var1.update(var107);

                     try {
                        this.handle(var1, var2, var3, var4, var7, var11, var23, var13, var24, var16, var15, var32, var33, var5);
                     } finally {
                        var1.onTick();
                     }
                  }

                  if (var37) {
                     this.process(var1, var8, var10, var11, var23, var13, var24, var25, var27);
                  }

                  float var108 = (1.0F - var16) * (1.0F - var15 * 0.55F);
                  if (var108 > 0.01F) {
                     int var109 = ThemeColors.handle(var10.load(), Math.round(34.0F * var108));
                     var1.handle(var11, var23, var13, var24, var25, var109, 0.5F);
                  }
               } finally {
                  var1.check();
               }
            } finally {
               var1.prepare();
            }
         } finally {
            var1.onTick();
         }
      }
   }

   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = TooltipMotion.resolve(var9);
      if (!(var10 <= 1.0E-4F)) {
         float var11 = var10 / 0.085F;
         int var12 = ThemeColors.handle(var3.submit(), var3.save(), 0.42F);
         int var13 = ThemeColors.handle(var3.submit(), var3.load(), 0.2F);
         if (var3.unload()) {
            var1.handle(
               var4,
               var5 + var2.handle(2.0F),
               var6,
               var7,
               var8,
               var2.handle(20.0F) * var11,
               var2.handle(3.2F),
               ThemeColors.handle(46, 59, 70, Math.round(178.0F * var10))
            );
            var1.handle(
               var4,
               var5 + var2.handle(5.0F),
               var6,
               var7,
               var8,
               var2.handle(34.0F) * var11,
               var2.handle(4.2F),
               ThemeColors.handle(77, 91, 104, Math.round(64.0F * var10))
            );
         } else {
            var1.resolve();

            try {
               var1.handle(var4, var5, var6, var7, var8, var2.handle(18.0F) * var11, var2.handle(3.2F), ThemeColors.handle(var13, Math.round(196.0F * var10)));
               var1.handle(var4, var5, var6, var7, var8, var2.handle(9.0F) * var11, var2.handle(1.4F), ThemeColors.handle(var12, Math.round(255.0F * var10)));
            } finally {
               var1.update();
            }
         }
      }
   }

   private void process(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = TooltipMotion.compute(var9);
      if (!(var10 <= 1.0E-4F)) {
         float var11 = var2.handle(1.25F);
         int var12 = ThemeColors.handle(var3.submit(), var3.save(), 0.56F);
         if (!var3.unload()) {
            var1.resolve();
         }

         try {
            var1.handle(
               var4 + var11,
               var5 + var11,
               var6 - var11 * 2.0F,
               var7 - var11 * 2.0F,
               Math.max(0.0F, var8 - var11),
               ThemeColors.handle(var12, Math.round(255.0F * var10)),
               0.5F
            );
         } finally {
            if (!var3.unload()) {
               var1.update();
            }
         }
      }
   }

   private float handle(float var1, float var2, float var3) {
      float var4 = this.resolve((var3 - var1) / Math.max(1.0E-5F, var2 - var1));
      return var4 * var4 * (3.0F - 2.0F * var4);
   }

   private float handle(float var1) {
      float var2 = this.resolve(var1);
      return var2 * var2 * var2 * (var2 * (var2 * 6.0F - 15.0F) + 10.0F);
   }

   private float process(float var1) {
      float var2 = this.handle(var1);
      float var3 = this.handle(0.08F, 0.92F, var2);
      return NumericTransform.onTick(var2 * 0.72F + var3 * 0.28F, 0.0F, 1.0F);
   }

   private float compute(float var1) {
      float var2 = this.resolve(var1);
      return (float)Math.pow(var2, 1.42F);
   }

   private void handle(
      RoundedRectRenderer var1,
      Module var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      ThemeRenderContext var11
   ) {
      ThemeColors var12 = var11.apply();
      int var13 = var12.unload()
         ? ThemeColors.handle(ModuleStateHelper.handle(var12, var10), 242)
         : ThemeColors.handle(ThemeColors.handle(var12.execute(), var12.apply(), var2.enabled ? 0.18F : 0.24F), 242);
      var1.handle(var3, var4, var5, var6, var7, var13, var13, var12.save(), var12.submit(), var8, var9, var10, 0.0F, false, 6);
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ModulePlacement var4,
      Module var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      ThemeRenderContext var14
   ) {
      GuiMetrics var15 = var14.update();
      ThemeColors var16 = var14.apply();
      if (var10 > 0.01F) {
         int var17 = ThemeColors.handle(
            ThemeColors.handle(var16.save(), var16.submit(), var16.unload() ? 0.38F : 0.0F), Math.round((var16.unload() ? 26 : 20) * var10)
         );
         var1.handle(var6 + var15.handle(1.0F), var7, var8 - var15.handle(2.0F), var15.handle(1.0F), var15.handle(8.0F), var15.handle(8.0F), 0.0F, 0.0F, var17);
      }

      this.handle(var1, var3, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      if (var4.apply() > 0.01F) {
         this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var14);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ModulePlacement var4,
      Module var5,
      float var6,
      float var7,
      float var8,
      float var9,
      ThemeRenderContext var10
   ) {
      GuiMetrics var11 = var10.update();
      ThemeColors var12 = var10.apply();
      float var13 = this.resolve(var3.handle(UiAnimationKeys.handle(var5)));
      float var14 = this.handle(var5, var8, var10);
      float var15 = var7 + var14;
      float var16 = Math.max(var11.handle(1.0F), var9 - var14);
      boolean var17 = Menu.handle(Menu.positionAdvance);
      boolean var18 = !var3.filterMatrix() && var17 && var13 < 0.995F;
      if (!var18) {
         ModuleStateHelper.handle(var1, var6, var15, var8, var16, var11.handle(7.0F), () -> {
            var1.update(var13);
            boolean var14x = false /* VF: Semaphore variable */;

            try {
               var14x = true;
               this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
               var14x = false;
            } finally {
               if (var14x) {
                  var1.onTick();
               }
            }

            var1.onTick();
         });
      } else {
         RoundedRectRenderer.PrimaryColorState var19 = var1.process(var6, var15, var8, var16);
         boolean var20 = false;
         if (var19 != null) {
            try {
               this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            } finally {
               var1.handle(var19);
            }

            int var21 = var12.unload() ? ThemeColors.handle(var12.save(), var12.load(), 0.45F) : var12.save();
            int var22 = var12.unload() ? ThemeColors.handle(var12.submit(), var12.load(), 0.45F) : var12.submit();
            int var23 = ThemeColors.handle(ThemeColors.handle(var22, var21, 0.5F), Math.round(200.0F * var13));
            boolean var24 = var3.invokeProfile().contains(var5);
            float var25 = 0.988F + 0.012F * this.handle(0.0F, 0.6F, var13);
            var1.handle(var25, var6 + var8 * 0.5F, var15);
            var1.update(this.handle(0.02F, 0.3F, var13));

            try {
               var20 = var1.handle(
                  var19,
                  var6,
                  var15,
                  var8,
                  var16,
                  var11.handle(7.0F),
                  ThemeColors.handle(var21, var24 ? 255 : 0),
                  ThemeColors.handle(var22, 255),
                  var23,
                  var13,
                  this.handle()
               );
            } finally {
               var1.onTick();
               var1.check();
            }
         }

         if (!var20) {
            ModuleStateHelper.handle(var1, var6, var15, var8, var16, var11.handle(7.0F), () -> {
               var1.update(var13);
               boolean var14x = false /* VF: Semaphore variable */;

               try {
                  var14x = true;
                  this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
                  var14x = false;
               } finally {
                  if (var14x) {
                     var1.onTick();
                  }
               }

               var1.onTick();
            });
         }
      }
   }

   private void process(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ModulePlacement var4,
      Module var5,
      float var6,
      float var7,
      float var8,
      float var9,
      ThemeRenderContext var10
   ) {
      GuiMetrics var11 = var10.update();
      ThemeColors var12 = var10.apply();
      float var13 = this.handle(var5, var8, var10);
      var1.handle(var6 + var11.handle(1.0F), var7 + var13, var8 - var11.handle(2.0F), var11.handle(1.0F), var5.enabled ? var12.refresh() : var12.onTick());
      ModulePanelRenderer var14 = ModulePanelRegistry.handle(var5);
      if (var14 != null) {
         ModulePlacement var15 = new ModulePlacement(var5, var6, var7, var8, var9, var4.apply());
         var14.handle(var1, var2, var3, var15, var10);
      } else {
         this.handle(var1, var3, var5, var6 + var11.handle(16.0F), var7 + var13 + var11.handle(10.0F), var8 - var11.handle(32.0F), var10);
      }
   }

   private float handle() {
      return (float)(System.currentTimeMillis() % 1000000L) / 1000.0F;
   }

   private float resolve(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      Module var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      ThemeRenderContext var12
   ) {
      GuiMetrics var13 = var12.update();
      ThemeColors var14 = var12.apply();
      float var15 = var4 + var13.handle(16.0F);
      float var16 = var5 + var13.handle(16.0F);
      int var17 = ThemeColors.handle(ModuleStateHelper.process(var14), ModuleStateHelper.handle(var14), Math.max(var8, var9 * 0.45F));
      if (var8 > 0.01F) {
         var1.process(
            var15,
            var16 + var13.handle(2.0F),
            var13.handle(1.0F),
            var13.handle(10.0F),
            var13.handle(1.0F),
            ThemeColors.handle(var14.save(), Math.round(255.0F * var8)),
            ThemeColors.handle(var14.submit(), Math.round(255.0F * var8))
         );
      }

      float var18 = var15 + var13.handle(9.0F) * var8;
      ModuleStateHelper.handle(var1, var13, FontRegistry.config, var18, var16, var13.handle(14.0F), 12.0F, var3.searchName, var17);
      this.handle(var1, var2, var3, var4, var6, var16, var10, var11, var12);
      this.handle(var1, var2, var3, var4, var6, var16, var8, var9, var10, var11, var12);
      this.handle(var1, var3, var4, var5, var6, ThemeColors.handle(ModuleStateHelper.compute(var14), ModuleStateHelper.process(var14), var8), var12);
   }

   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, Module var3, float var4, float var5, float var6, float var7, float var8, ThemeRenderContext var9
   ) {
      if (!var3.getFlags().isEmpty()) {
         GuiMetrics var10 = var9.update();
         ThemeColors var11 = var9.apply();
         SpringFloat var12 = this.selection.computeIfAbsent(var3, var1x -> this.process());
         var12.compute(1.0F);
         float var13 = NumericTransform.onTick(var12.handle(), 0.0F, 1.0F);
         List<ModuleFlag> var14 = this.handle(var3);
         float var15 = var4 + var5 - var10.handle(16.0F) - var10.handle(24.0F);
         float var16 = var15 - var10.handle(this.process(var3) ? 32.0F : 9.0F);
         float var17 = var4 + var10.handle(90.0F);
         float var18 = var16 - var17;
         float var19 = var10.handle(15.5F);
         float var20 = var10.handle(4.0F);
         float var21 = var6 + var10.handle(7.0F);
         float var22 = var10.handle(5.0F);
         float var23 = var10.handle(17.4F);
         float var24 = var22 * Math.max(0, var14.size() - 1);

         for (ModuleFlag var26 : var14) {
            var24 += this.handle(var10, var26);
         }

         boolean var50 = var24 <= var18;
         int var51 = var14.size();
         float var27 = var51 <= 1 ? 0.0F : Math.min(0.42F, 0.07F * (var51 - 1)) / (var51 - 1);
         float var28 = Math.max(0.001F, 1.0F - var27 * Math.max(0, var51 - 1));
         float var29 = var16;

         for (int var30 = 0; var30 < var51; var30++) {
            ModuleFlag var31 = (ModuleFlag)var14.get(var30);
            float var32 = var50 ? this.handle(var10, var31) : var23;
            float var33 = var29 - var32;
            if (var33 < var17 && var30 > 0) {
               break;
            }

            float var34 = NumericTransform.onTick((var13 - var30 * var27) / var28, 0.0F, 1.0F);
            float var35 = this.process(var34);
            float var36 = AnticheatProfiles.handle(var32, var20, var35);
            float var37 = AnticheatProfiles.process(var19, var20, var35);
            float var38 = var33 + var32 - var36;
            float var39 = var21 - var37 * 0.5F;
            float var40 = Math.min(var36, AnticheatProfiles.compute(var19, var20, var35));
            float var41 = AnticheatProfiles.update(var37, var19, var20);
            float var42 = AnticheatProfiles.apply(var36, var40, var20);
            float var43 = AnticheatProfiles.execute(var37, var41, var42);
            float var44 = AnticheatProfiles.resolve(var37, var41);
            float var45 = AnticheatProfiles.update(var37, var41);
            float var46 = AnticheatProfiles.prepare(var37, var41, var42);
            String var47 = this.handle(var3, var31);
            boolean var48 = !var2.filterMatrix() && AnticheatProfiles.process(var7, var8, var38, var39, var36, var37, var43, var44, var45, var46);
            float var49 = var2.handle(var47, var48 ? 1.0F : 0.0F, config);
            this.handle(var1, var10, var11, var31, var38, var39, var36, var37, var40, var43, var44, var45, var46, var50, var35, var49, var7, var8);
            var29 -= var32 + var22;
         }
      }
   }

   private SpringFloat process() {
      SpringFloat var1 = new SpringFloat(AnimationClock.handle(), context, 0.0F, 0.0F, 1.0F, 6.0E-4F, 6.0E-4F);
      var1.handle(this::handle);
      return var1;
   }

   private List<ModuleFlag> handle(Module var1) {
      return this.enabled
         .computeIfAbsent(var1, var0 -> var0.getFlags().stream().sorted(Comparator.comparingInt(ModuleFlag::resolve).thenComparing(Enum::name)).toList());
   }

   private String handle(Module var1, ModuleFlag var2) {
      return this.renderer
         .computeIfAbsent(var1, var0 -> new EnumMap<>(ModuleFlag.class))
         .computeIfAbsent(var2, var2x -> "module:tag:hover:" + System.identityHashCode(var1) + ":" + var2.name());
   }

   private String handle(ModuleFlag var1) {
      return this.handler.computeIfAbsent(var1, var0 -> var0.handle().toUpperCase(Locale.ROOT));
   }

   private float handle(GuiMetrics var1, ModuleFlag var2) {
      String var3 = this.handle(var2);
      float var4 = ModuleStateHelper.handle(var1, FontRegistry.config, var3, 8.0F);
      return Math.max(var1.handle(29.0F), var4 + var1.handle(27.0F));
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      ThemeColors var3,
      ModuleFlag var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      boolean var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      if (!(var15 <= 0.001F)) {
         boolean var19 = var3.unload();
         int var20 = var4.process();
         float var21 = AnticheatProfiles.check(var15);
         float var22 = AnticheatProfiles.handle(var15);
         float var23 = AnticheatProfiles.process(var15);
         float var24 = AnticheatProfiles.compute(var16);
         float var25 = var5 + var7 - var9;
         float var26 = AnticheatProfiles.resolve(var17, var5, var7);
         float var27 = AnticheatProfiles.resolve(var18, var6, var8);
         float var28 = var24 * AnticheatProfiles.process(var17, var18, var5, var6, var7, var8);
         float var29 = var28 * var28;
         if (var29 > 0.001F) {
            var1.handle(
               var5,
               var6 + var2.handle(0.4F),
               var7,
               var8,
               var10,
               var11,
               var12,
               var13,
               var2.handle(4.4F) * var29,
               var2.handle(0.45F),
               ThemeColors.handle(var20, Math.round(24.0F * var29 * var21))
            );
         }

         int var30 = var19
            ? ThemeColors.handle(ThemeColors.handle(ModuleStateHelper.handle(var3, var28 * 0.16F), var20, 0.18F), Math.round(234.0F * var21))
            : ThemeColors.handle(ThemeColors.handle(var3.execute(), var20, 0.24F), Math.round(240.0F * var21));
         int var31 = var19
            ? ThemeColors.handle(ThemeColors.handle(ModuleStateHelper.handle(var3, 0.0F), var20, 0.15F), Math.round(230.0F * var21))
            : ThemeColors.handle(ThemeColors.handle(var3.apply(), var20, 0.14F), Math.round(238.0F * var21));
         int var32 = ThemeColors.handle(var20, var3.load(), var19 ? 0.16F : 0.32F);
         int var33 = ThemeColors.handle(var20, var3.load(), var19 ? 0.05F : 0.14F);
         var1.handle(var5, var6, var7, var8, var10, var11, var12, var13, var30, var31, var32, var33, var26, var27, var28, 0.0F, false, 6);
         AnticheatProfiles.Mode var34 = AnticheatProfiles.handle(var4.handle());
         String var35 = var34 == AnticheatProfiles.Mode.NONE ? var4.compute() : var34.handle();
         if (var22 > 0.001F) {
            int var36 = ThemeColors.handle(ThemeColors.handle(var20, var3.load(), var19 ? 0.42F : 0.76F), Math.round(248.0F * var22));
            this.handle(var1, var2, var34, var35, var25, var6, var9, var8, var36, var28);
         }

         if (var14 && var23 > 0.001F) {
            String var50 = this.handle(var4);
            float var37 = var5 + var2.handle(5.2F);
            float var38 = var25 - var2.handle(1.8F);
            float var39 = ModuleStateHelper.handle(var2, FontRegistry.config, var50, 8.0F);
            float var40 = AnticheatProfiles.check(Math.max(0.0F, var38 - var37), var39, var2.handle(3.0F));
            float var41 = var23 * var40;
            int var42 = (int)Math.floor(var37);
            int var43 = (int)Math.floor(var6);
            int var44 = (int)Math.ceil(var38);
            int var45 = (int)Math.ceil(var6 + var8);
            if (var41 > 0.001F && var44 > var42 && var45 > var43) {
               int var46 = ThemeColors.handle(ThemeColors.handle(ModuleStateHelper.process(var3), var20, var19 ? 0.3F : 0.42F), Math.round(244.0F * var41));
               var1.handle(var42, var43, var44 - var42, var45 - var43);

               try {
                  ModuleStateHelper.handle(var1, var2, FontRegistry.config, var37, var6, var8, 8.0F, var50, var46);
               } finally {
                  var1.apply();
               }
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      AnticheatProfiles.Mode var3,
      String var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      float var10
   ) {
      float var11 = Math.min(var7 * 0.56F, var8 * 0.6F);
      float var12 = var11 * var3.compute() * (1.0F + var10 * 0.045F);
      float var13 = var5 + (var7 - var12) * 0.5F + var2.handle(var3.resolve());
      float var14 = var6 + (var8 - var12) * 0.5F + var2.handle(var3.update());
      if (!var3.process().isEmpty()) {
         int var15 = AnticheatProfileColor.handle(var3);
         if (var15 > 0) {
            var1.handle(var15, var13, var14, var12, var12, var9, false);
            return;
         }
      }

      float var18 = var3 == AnticheatProfiles.Mode.NONE ? 7.2F : 8.1F;
      float var16 = ModuleStateHelper.handle(var2, FontRegistry.compute(), var4, var18);
      float var17 = var5 + (var7 - var16) * 0.5F;
      ModuleStateHelper.handle(var1, var2, FontRegistry.compute(), var17, var6, var8, var18, var4, var9);
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      Module var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      ThemeRenderContext var11
   ) {
      GuiMetrics var12 = var11.update();
      ThemeColors var13 = var11.apply();
      float var14 = var12.handle(24.0F);
      float var15 = var12.handle(14.0F);
      float var16 = var4 + var5 - var12.handle(16.0F) - var14;
      float var17 = var6;
      int var18 = var13.tick();
      int var19 = ThemeColors.handle(var13.submit(), var13.save(), 0.5F);
      int var20 = ThemeColors.handle(var18, var19, var7);
      if (var7 > 0.01F) {
         int var21 = var13.unload() ? ThemeColors.handle(0, 0, 0, Math.round(24.0F * var7)) : ThemeColors.handle(var13.submit(), Math.round(40.0F * var7));
         var1.handle(
            var16, var17, var14, var15, var15 * 0.5F, var12.handle(var13.unload() ? 8.0F : 4.0F) * var7, var12.handle(var13.unload() ? 1.5F : 1.0F), var21
         );
      }

      if (var7 > 0.5F) {
         var1.handle(var16, var17, var14, var15, var15 * 0.5F, var13.submit(), var13.save());
      } else {
         var1.handle(var16, var17, var14, var15, var15 * 0.5F, ThemeColors.handle(var18, var20, var7 * 2.0F));
      }

      float var25 = var12.handle(10.0F);
      float var22 = var12.handle(2.0F);
      float var23 = AnticheatProfiles.process(var16, var14, var25, var22, var7);
      int var24 = ThemeColors.handle(ModuleStateHelper.apply(var13), ModuleStateHelper.update(var13), var7);
      var1.handle(
         var23,
         var17 + var22,
         var25,
         var25,
         var25 * 0.5F,
         var12.handle(3.0F),
         var12.handle(0.5F),
         ThemeColors.handle(0, 0, 0, Math.round(60.0F * (0.5F + var7 * 0.5F)))
      );
      var1.handle(var23, var17 + var22, var25, var25, var25 * 0.5F, var24);
      if (this.process(var3)) {
         this.handle(var1, var2, var3, var16, var6, var7, var8, var9, var10, var11);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      Module var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      ThemeRenderContext var10
   ) {
      GuiMetrics var11 = var10.update();
      ThemeColors var12 = var10.apply();
      float var13 = var4 - var11.handle(22.0F);
      float var14 = var11.handle(20.0F);
      boolean var15 = !var2.filterMatrix() && ModuleStateHelper.handle(var8, var9, var13 - var11.handle(3.0F), var5 - var11.handle(3.0F), var14, var14);
      float var16 = var2.handle(UiAnimationKeys.update(var3), var15 ? 1.0F : 0.0F, config);
      float var17 = var2.handle(UiAnimationKeys.resolve(var3));
      float var18 = var2.handle(UiAnimationKeys.handle(var3));
      float var19 = var2.process(UiAnimationKeys.handle(var3));
      float var20 = Math.max(this.resolve(var17), Math.max(var7 * 0.3F, var16 * 0.55F));
      int var21 = ThemeColors.handle(ModuleStateHelper.compute(var12), ModuleStateHelper.process(var12), var6);
      int var22 = ThemeColors.handle(var21, var12.save(), var20 * 0.45F);
      int var23 = ThemeColors.handle(var21, var12.submit(), var20 * 0.3F);
      float var24 = Math.max(1.0F, var11.process());
      float var25 = 15.12F * var24;
      float var26 = 26.0F * var24;
      float var27 = var13 + 0.5003F * var25;
      float var28 = var5 + var11.handle(7.0F) - 0.04587F * var25;
      float var29 = var18 * (float) (Math.PI * 2.0 / 3.0);
      float var30 = Math.min((float) (Math.PI * 2.0 / 9.0), Math.abs(var19) * 240.0F * (float) (Math.PI * 2.0 / 3.0) * SpringAnimation.handle() * 1.35F);
      var1.handle(
         var27 - var26 * 0.5F,
         var28 - var26 * 0.5F,
         var26,
         var26,
         var26 * 0.5F,
         var22,
         var23,
         var12.save(),
         var12.submit(),
         var29,
         var30,
         var16,
         this.resolve(var18),
         var12.unload(),
         7
      );
   }

   private void handle(RoundedRectRenderer var1, Module var2, float var3, float var4, float var5, int var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      ThemeColors var9 = var7.apply();
      float var10 = var4 + var8.handle(38.0F);
      List var11 = ModuleStateHelper.handle(
         FontRegistry.instance, var2.description == null ? "" : var2.description, 10.0F, Math.max(var8.handle(160.0F), var5 - var8.handle(90.0F)), 10
      );

      for (int var12 = 0; var12 < var11.size(); var12++) {
         ModuleStateHelper.handle(
            var1,
            var8,
            FontRegistry.instance,
            var3 + var8.handle(16.0F),
            var10 + var12 * var8.handle(12.0F),
            var8.handle(12.0F),
            10.0F,
            (String)var11.get(var12),
            var6
         );
      }

      if (var2.bindingActive || var2.keyCode != -1) {
         String var14 = var2.bindingActive ? "..." : KeycodeNames.handle(var2.keyCode);
         float var13 = ModuleStateHelper.handle(FontRegistry.instance, var14, 10.0F);
         ModuleStateHelper.handle(
            var1, var8, FontRegistry.instance, var3 + var8.drawAnimation() - var8.handle(30.0F) - var13, var10, var8.handle(12.0F), 10.0F, var14, var6
         );
         ModuleStateHelper.handle(
            var1,
            var8,
            FontRegistry.state,
            var3 + var8.drawAnimation() - var8.handle(26.0F),
            var10,
            var8.handle(12.0F),
            10.0F,
            "g",
            ThemeColors.handle(var9.tick(), ModuleStateHelper.compute(var9), this.handle(var6))
         );
      }
   }

   private float handle(int var1) {
      return (var1 >>> 24 & 0xFF) / 255.0F;
   }

   private float process(float var1, float var2, float var3) {
      float var4 = this.resolve(var3);
      return var1 + (var2 - var1) * var4;
   }

   private float compute(float var1, float var2, float var3) {
      return Math.max(var2, Math.min(var3, var1));
   }

   private float handle(Module var1, float var2, ThemeRenderContext var3) {
      GuiMetrics var4 = var3.update();
      String var5 = var1.description == null ? "" : var1.description;
      if (var5.isBlank()) {
         return var4.encodePoint();
      }

      int var6 = ModuleStateHelper.handle(FontRegistry.instance, var5, 10.0F, Math.max(var4.handle(160.0F), var2 - var4.handle(90.0F)), 10).size();
      return Math.max(var4.encodePoint(), var4.handle(54.0F) + Math.max(1, var6) * var4.handle(12.0F));
   }

   private boolean process(Module var1) {
      return ModulePanelRegistry.process(var1) || !var1.apply().isEmpty();
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, Module var3, float var4, float var5, float var6, ThemeRenderContext var7) {
      GuiMetrics var8 = var7.update();
      float var9 = var5;

      for (Setting var11 : var3.apply()) {
         if (var11 instanceof FloatSetting var12) {
            var9 += var8.handle(var12.compute());
         } else {
            float var21 = var2.handle(UiAnimationKeys.resolve(var11));
            if (!(var21 < 0.01F)) {
               float var13 = (1.0F - var21) * var8.handle(8.0F);
               var1.update(var21);

               try {
                  this.mode.handle(var1, var2, var11, var4, var9 + var13, var6, var7);
               } finally {
                  var1.onTick();
               }

               float var14 = this.mode.handle(var11, var8, var2);
               float var15 = 0.0F;
               if (var11 instanceof ModeSetting var16) {
                  float var18 = var2.handle(UiAnimationKeys.update(var16));
                  if (var18 > 0.01F) {
                     var15 = (var8.handle(6.0F) + var16.config.size() * var8.handle(18.0F) + var8.handle(4.0F)) * var18;
                  }
               } else if (var11 instanceof ShaderPresetSetting var17) {
                  float var22 = var2.handle(UiAnimationKeys.update(var17));
                  if (var22 > 0.01F) {
                     var15 = SettingControlRenderer.handle(var17, var8) * var22;
                  }
               }

               var9 += (var14 + var15 + var8.handle(12.0F)) * var21;
            }
         }
      }
   }
   public ModuleSettingsPanel(SettingControlRenderer var1) {
      this.mode = var1;
   }
}
