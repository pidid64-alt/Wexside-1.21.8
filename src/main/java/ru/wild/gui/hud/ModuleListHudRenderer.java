package ru.wild.gui.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.MinecraftContext;
import ru.wild.core.ModuleListEffectShader;
import ru.wild.gui.theme.NeoStyleOptions;
import ru.wild.gui.theme.ThemePresets;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.TextMeasureCache;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.Easings;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;
import ru.wild.util.text.KeycodeNames;

@HudElementMetadata(handle = "ArrayList", process = "n")
public final class ModuleListHudRenderer extends ThemePresets {
   private static final String responseCompute = "Прямоугольник";
   private static final String providerFetch = "Обволакивание";
   private static final String profileDraw = "Классический";
   private static final String vectorPerform = "Новый";
   private static final String eventAttach = "Ferrofluid SDF";
   private static final ModuleListHudRenderer serverRead = new ModuleListHudRenderer();
   private static final int positionAdvance = 96;
   private static final List<ModuleListHudRenderer.TextureState> frameCheck = new ArrayList<>(64);
   private static final Map<Module, ModuleListHudRenderer.TextureState> moduleCollect = new IdentityHashMap<>(128);
   private static final SpringAnimationSpec providerClose = new SpringAnimationSpec(0.068F, 0.72F, 0.001F, 0.001F);
   private static final SpringAnimationSpec presetSave = new SpringAnimationSpec(0.105F, 0.84F, 0.001F, 0.001F);
   private static final SpringAnimationSpec windowConvert = new SpringAnimationSpec(0.064F, 0.76F, 0.01F, 0.01F);
   private static final SpringAnimationSpec presetWrite = new SpringAnimationSpec(0.075F, 0.7F, 0.001F, 0.001F);
   private static final SpringAnimationSpec colorMeasure = new SpringAnimationSpec(0.082F, 0.62F, 0.001F, 0.001F);
   private static final SpringAnimationSpec animationSchedule = new SpringAnimationSpec(0.12F, 0.82F, 0.001F, 0.001F);
   static final SpringAnimationSpec rendererScan = new SpringAnimationSpec(0.07F, 0.68F, 0.01F, 0.01F);
   static final SpringAnimationSpec sourceBuild = new SpringAnimationSpec(0.078F, 0.66F, 0.01F, 0.01F);
   private static final SpringAnimationSpec outputCollapse = new SpringAnimationSpec(0.09F, 0.74F, 0.001F, 0.001F);
   private static final SpringAnimationSpec profileInvoke = new SpringAnimationSpec(0.062F, 0.8F, 0.001F, 0.001F);
   private static final SpringAnimation sourceSchedule = new SpringAnimation(0.0F);
   private static final SpringAnimation timerRender = new SpringAnimation(0.0F);
   private static final SpringAnimation scaleSave = new SpringAnimation(0.0F);
   private static final SpringAnimation colorCompute = new SpringAnimation(0.0F);
   private static final SpringAnimation scaleAdapt = new SpringAnimation(0.0F);
   private static final SpringAnimation textureRun = new SpringAnimation(0.0F);
   private static final DoubleAnimator indexBind = new DoubleAnimator();
   private static final DoubleAnimator actionRead = new DoubleAnimator();
   private static final DoubleAnimator configCollapse = new DoubleAnimator();
   private static final DoubleAnimator dataValidate = new DoubleAnimator();
   private static final float[] scaleRender = new float[384];
   private static final float[] clientRefresh = new float[384];
   private static float[] keyFilter = new float[96];
   private static float[] requestAdapt = new float[96];
   private static boolean timerMeasure;
   public static final ArrayList<Module> instance = new ArrayList<>(64);
   private final ChoiceSetting vectorEncode = new ChoiceSetting(
      "Фильтр", new BooleanSetting("Combat", true), new BooleanSetting("Movement", true), new BooleanSetting("Player", true), new BooleanSetting("Misc", true)
   );
   private final ChoiceSetting requestReceive = new ChoiceSetting(
      "Вид",
      new BooleanSetting("Иконки категорий", true),
      new BooleanSetting("Индикатор", true),
      new BooleanSetting("Мягкое свечение", true),
      new BooleanSetting("Показывать бинд", false)
   );
   private final ModeSetting windowProcess = new ModeSetting("Стиль отображения", "Новый", "Классический", "Новый");
   private final ModeSetting packetSave = new ModeSetting("Форма фона", "Прямоугольник", "Прямоугольник", "Обволакивание");
   private final NumberSetting entryAnimate = new NumberSetting("Интервал строк", 0.0F, 0.0F, 8.0F, 0.5F, false);
   private final BooleanSetting playerCollect = new BooleanSetting("Ferrofluid SDF", true);
   private final NumberSetting stateApply = new NumberSetting("Слияние капель", 12.0F, 4.0F, 24.0F, 0.5F, false)
      .handle(() -> !this.playerCollect.compute() && !this.refresh());

   private ModuleListHudRenderer() {
      this.handle(this.vectorEncode, this.requestReceive, this.windowProcess, this.playerCollect, this.stateApply, this.packetSave, this.entryAnimate);
      HudProfileConfig.handle(this);
   }

   public static ModuleListHudRenderer process() {
      return serverRead;
   }

   public static void handle(RoundedRectRenderer var0) {
      serverRead.process(var0);
   }

   private void process(RoundedRectRenderer var1) {
      if (MinecraftContext.toggleState.player != null && WildClient.instance != null && WildClient.instance.data != null) {
         this.animate();
         if (this.encodePoint()) {
            this.compute(var1);
         } else {
            this.resolve(var1);
         }
      }
   }

   private boolean encodePoint() {
      return this.windowProcess.process("Классический");
   }

   private void compute(RoundedRectRenderer var1) {
      boolean var2 = !frameCheck.isEmpty() || MinecraftContext.toggleState.currentScreen instanceof ChatScreen;
      indexBind.handle();
      indexBind.handle(var2 ? 1.0 : 0.0, 0.22F, Easings.handler, false);
      float var3 = indexBind.update();
      if (!(var3 <= 0.01F)) {
         boolean var4 = this.requestReceive.process("Иконки категорий");
         boolean var5 = this.requestReceive.process("Индикатор");
         boolean var6 = this.requestReceive.process("Мягкое свечение");
         boolean var7 = this.requestReceive.process("Показывать бинд");
         float var8 = 24.0F;
         float var9 = 32.0F;
         float var10 = this.entryAnimate.compute();
         float var11 = 13.0F;
         float var12 = 4.0F;
         float var13 = 0.0F;
         float var14 = 0.0F;
         int var15 = 0;

         for (ModuleListHudRenderer.TextureState var17 : frameCheck) {
            float var18 = var17.context.update();
            if (!(var18 <= 0.01F)) {
               var15++;
               var13 = Math.max(var13, var17.handle(var8, var11, var5, var4, var7));
               var14 += var9 * var18;
               if (var15 > 1) {
                  var14 += var10 * var18;
               }
            }
         }

         boolean var51 = var15 > 0;
         if (!var51) {
            var13 = TextMeasureCache.process(FontRegistry.instance, "Нет активных модулей ", var8) + var11 * 2.0F;
            var14 = var9;
         }

         float var52 = var13 + var12 * 2.0F;
         float var53 = var14 + var12 * 2.0F;
         actionRead.handle();
         configCollapse.handle();
         actionRead.handle(var52, 0.18F, Easings.handler, false);
         configCollapse.handle(var53, 0.18F, Easings.handler, false);
         float var19 = Math.max(32.0F, actionRead.update());
         float var20 = Math.max(32.0F, configCollapse.update());
         float var21 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
         float var22 = Math.max(10.0F, var21 - var19 - 10.0F);
         float var23 = 120.0F;
         ThemeRenderer.PrimaryCacheEntry var24 = ThemeRenderer.handle().handle("HUD_ArrayList", var22, var23, var19, var20);
         float var25 = var24.data;
         float var26 = var24.context;
         float var27 = var24.config;
         float var28 = var24.state;
         this.handle(var25, var26, var27, var28);
         float var29 = var27 / Math.max(1.0F, var19);
         float var30 = var28 / Math.max(1.0F, var20);
         float var31 = Math.min(var29, var30);
         float var32 = var9 * var30;
         float var33 = var10 * var30;
         float var34 = var11 * var29;
         float var35 = var12 * var29;
         float var36 = var12 * var30;
         float var37 = var8 * var31;
         float var38 = var3 * this.target.compute();
         int var39 = this.update(var38);
         int var40 = this.apply(var38);
         float var41 = var25 + var27 * 0.5F > var21 * 0.5F ? 1.0F : 0.0F;
         dataValidate.handle();
         dataValidate.handle(var41, 0.26F, Easings.handler, false);
         float var42 = dataValidate.update();
         float var43 = var13 * var29;
         float var44 = var26 + var36;
         if (var51) {
            this.handle(var1, var25 + var35, var44, var32, var33, var11, var8, var29, var30, var38, var5, var4, var7, var6, var42);

            for (ModuleListHudRenderer.TextureState var46 : frameCheck) {
               float var47 = var46.context.update();
               if (!(var47 <= 0.01F)) {
                  float var48 = var46.handle(var8, var11, var5, var4, var7);
                  float var49 = Math.max(1.0F, var48 * var29 * var47);
                  float var50 = var25 + var35 + (var43 - var49) * var42;
                  this.handle(var1, var46, var50, var44, var49, var32, var34, var37, var29, var30, var38, var47, var39, var40, var5, var4, var7, var42);
                  var44 += var32 * var47 + var33 * var47;
               }
            }
         } else {
            this.handle(var1, var25 + var35, var44, Math.max(1.0F, var27 - var35 * 2.0F), var32, var34, var37, var38, var40, true);
         }

         ThemeRenderer.handle().handle(var24);
         NeoStyleOptions.handle(
            var1,
            this,
            var24,
            ThemeRenderer.handle(),
            MinecraftContext.toggleState.getWindow().getScaledWidth(),
            MinecraftContext.toggleState.getWindow().getScaledHeight()
         );
      }
   }

   private void resolve(RoundedRectRenderer var1) {
      boolean var2 = !frameCheck.isEmpty() || MinecraftContext.toggleState.currentScreen instanceof ChatScreen;
      if (var2 && !timerMeasure) {
         textureRun.compute(Math.max(textureRun.process(), 1.2F));
         scaleAdapt.handle(0.0F);
      }

      timerMeasure = var2;
      float var3 = tick(sourceSchedule.handle(var2 ? 1.0F : 0.0F, var2 ? providerClose : presetSave));
      float var4 = tick(scaleAdapt.handle(var2 ? 1.0F : 0.0F, outputCollapse));
      float var5 = Math.max(0.0F, textureRun.handle(0.0F, profileInvoke));
      if (!(var3 <= 0.01F)) {
         boolean var6 = this.requestReceive.process("Иконки категорий");
         boolean var7 = this.requestReceive.process("Индикатор");
         boolean var8 = this.requestReceive.process("Мягкое свечение");
         boolean var9 = this.requestReceive.process("Показывать бинд");
         float var10 = 24.0F;
         float var11 = 32.0F;
         float var12 = this.entryAnimate.compute();
         float var13 = 13.0F;
         float var14 = 4.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         int var17 = 0;

         for (ModuleListHudRenderer.TextureState var19 : frameCheck) {
            float var20 = tick(var19.config.process());
            if (!(var20 <= 0.01F) || !(Math.abs(var19.config.compute()) <= 0.01F)) {
               var17++;
               var15 = Math.max(var15, var19.handle(var10, var13, var7, var6, var9));
               var16 += var11 * var20;
               if (var17 > 1) {
                  var16 += var12 * var20;
               }
            }
         }

         boolean var56 = var17 > 0;
         if (!var56) {
            var15 = TextMeasureCache.process(FontRegistry.instance, "Нет активных модулей ", var10) + var13 * 2.0F;
            var16 = var11;
         }

         float var57 = var15 + var14 * 2.0F;
         float var58 = var16 + var14 * 2.0F;
         float var21 = Math.max(32.0F, timerRender.handle(var57, windowConvert));
         float var22 = Math.max(32.0F, scaleSave.handle(var58, windowConvert));
         float var23 = MinecraftContext.toggleState.getWindow().getFramebufferWidth();
         float var24 = Math.max(10.0F, var23 - var21 - 10.0F);
         float var25 = 120.0F;
         ThemeRenderer.PrimaryCacheEntry var26 = ThemeRenderer.handle().handle("HUD_ArrayList", var24, var25, var21, var22);
         float var27 = var26.data;
         float var28 = var26.context;
         float var29 = var26.config;
         float var30 = var26.state;
         this.handle(var27, var28, var29, var30);
         float var31 = var29 / Math.max(1.0F, var21);
         float var32 = var30 / Math.max(1.0F, var22);
         float var33 = Math.min(var31, var32);
         float var34 = var11 * var32;
         float var35 = var12 * var32;
         float var36 = var13 * var31;
         float var37 = var14 * var31;
         float var38 = var14 * var32;
         float var39 = var10 * var33;
         float var40 = var3 * this.target.compute();
         int var41 = this.update(var40);
         int var42 = this.apply(var40);
         float var43 = var27 + var29 * 0.5F > var23 * 0.5F ? 1.0F : 0.0F;
         float var44 = tick(colorCompute.handle(var43, presetWrite));
         float var45 = var15 * var31;
         float var46 = var27 + var37;
         float var47 = var28 + var38;
         ThemeRenderer var48 = ThemeRenderer.handle();
         float var49 = var48.execute();
         float var50 = var48.prepare();
         float var51 = MinecraftContext.toggleState.currentScreen instanceof ChatScreen && handle(var49, var50, var27, var28, var29, var30) ? 1.0F : 0.0F;
         boolean var52 = this.select() && !this.packetSave.process("Обволакивание");
         int var53 = var56 ? this.handle(var46, var47, var45, var34, var35, var10, var13, var31, var44, var7, var6, var9, var17, var52) : 0;
         if (var56 && var53 > 0) {
            this.handle(var1, var32, var40 * (0.82F + var4 * 0.18F), var7, var8, var44, var49, var50, var51, var5);

            for (ModuleListHudRenderer.TextureState var55 : frameCheck) {
               if (var55.handle()) {
                  this.process(
                     var1,
                     var55,
                     var55.animator,
                     var55.source,
                     var55.target,
                     var55.pending,
                     var36,
                     var39,
                     var31,
                     var32,
                     var40,
                     tick(var55.config.process()),
                     var41,
                     var42,
                     var7,
                     var6,
                     var9,
                     var44
                  );
               }
            }
         } else {
            this.handle(var1, var46, var47, Math.max(1.0F, var29 - var37 * 2.0F), var34, var36, var39, var40, var42, false);
         }

         ThemeRenderer.handle().handle(var26);
         NeoStyleOptions.handle(
            var1,
            this,
            var26,
            ThemeRenderer.handle(),
            MinecraftContext.toggleState.getWindow().getScaledWidth(),
            MinecraftContext.toggleState.getWindow().getScaledHeight()
         );
      }
   }

   private int handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      boolean var10,
      boolean var11,
      boolean var12,
      int var13,
      boolean var14
   ) {
      float var15 = var2;
      int var16 = 0;

      for (ModuleListHudRenderer.TextureState var18 : frameCheck) {
         float var19 = tick(var18.config.process());
         if (var19 <= 0.01F && Math.abs(var18.config.compute()) <= 0.01F) {
            var18.matrixBlend = false;
         } else {
            float var20 = Math.max(1.0F, var18.handle(var6, var7, var10, var11, var12) * var8);
            float var21 = var14 ? var3 : Math.max(1.0F, var20 * (0.32F + var19 * 0.68F));
            float var22 = Math.max(0.0F, var4 * var19);
            float var23 = var1 + (var3 - var21) * var9;
            var18.handle(var23, var15, var21, var22);
            var18.matrixBlend = var18.target > 0.75F && var18.pending > 0.75F;
            var15 += var4 * var19;
            if (++var16 < var13) {
               var15 += var5 * var19;
            }
         }
      }

      return var16;
   }

   private void handle(
      RoundedRectRenderer var1, float var2, float var3, boolean var4, boolean var5, float var6, float var7, float var8, float var9, float var10
   ) {
      int var11 = 0;
      float var12 = Float.MAX_VALUE;
      float var13 = Float.MAX_VALUE;
      float var14 = -Float.MAX_VALUE;
      float var15 = -Float.MAX_VALUE;
      float var16 = Float.MAX_VALUE;
      float var17 = 0.0F;
      float var18 = 0.0F;

      for (ModuleListHudRenderer.TextureState var20 : frameCheck) {
         if (var20.handle() && var11 < 96) {
            var11++;
            var12 = Math.min(var12, var20.animator);
            var13 = Math.min(var13, var20.source);
            var14 = Math.max(var14, var20.animator + var20.target);
            var15 = Math.max(var15, var20.source + var20.pending);
            var16 = Math.min(var16, var20.target);
            var17 = Math.max(var17, var20.target);
            var18 = Math.max(var18, Math.min(1.0F, (Math.abs(var20.output.compute()) + Math.abs(var20.current.compute())) * 0.012F));
         }
      }

      if (var11 > 0 && var12 != Float.MAX_VALUE && var13 != Float.MAX_VALUE && !(var14 <= var12) && !(var15 <= var13)) {
         boolean var34 = this.packetSave.process("Обволакивание");
         boolean var35 = this.playerCollect.compute() || this.refresh();
         float var21 = Math.max(1.0F, var15 - var13);
         float var22 = Math.max(1.0F, var21 / var11);
         float var23 = Math.min(15.0F * var2, var22 * 0.5F);
         if (keyFilter.length < var11) {
            keyFilter = new float[var11];
            requestAdapt = new float[var11];
         }

         float[] var24 = keyFilter;
         float[] var25 = requestAdapt;
         float var26 = var34 ? var17 : var14 - var12;
         float var27 = Math.max(var10, var18);
         int var28 = 0;

         for (ModuleListHudRenderer.TextureState var30 : frameCheck) {
            if (var30.handle() && var28 < var11) {
               int var31 = var28 * 4;
               float var32 = var34 ? var30.animator : var12;
               float var33 = var34 ? var30.target : var26;
               scaleRender[var31] = var32;
               scaleRender[var31 + 1] = var30.source;
               scaleRender[var31 + 2] = var33;
               scaleRender[var31 + 3] = var30.pending;
               clientRefresh[var31] = var30.output.compute();
               clientRefresh[var31 + 1] = var30.current.compute();
               clientRefresh[var31 + 2] = Math.max(0.0F, var30.cache.process());
               clientRefresh[var31 + 3] = tick(var30.state.process());
               var24[var28] = var33;
               var25[var28] = Math.max(1.0F, var30.source - var13 + var30.pending);
               var27 = Math.max(var27, clientRefresh[var31 + 2]);
               var28++;
            }
         }

         int var36 = this.refresh(var3);
         float var37 = this.drawAnimation();
         if (var35) {
            float var38 = Math.max(2.0F, this.stateApply.compute() * var2);
            float var40 = this.select() ? 1.0F : 0.0F;
            boolean var41 = ModuleListEffectShader.handle(
               var1,
               MinecraftContext.toggleState.getWindow().getFramebufferWidth(),
               MinecraftContext.toggleState.getWindow().getFramebufferHeight(),
               scaleRender,
               clientRefresh,
               var11,
               var23,
               var6,
               var3,
               var36,
               this.resolve(var3),
               this.prepare(var3),
               this.check(var3),
               this.update() || this.refresh(),
               var5 || this.resolve(),
               var4,
               var37,
               var7,
               var8,
               var9,
               var27,
               var38,
               var40
            );
            if (var41) {
               return;
            }
         }

         if (this.select()) {
            this.handle(var1, var12, var13, var26, var21, var23, var3);
         } else if (this.render()) {
            this.handle(var1, var12, var13, var26, var21, var23, var3);
         } else {
            float var39 = Math.max(1.5F, 2.0F * var2);
            if (var5) {
               this.handle(
                  var1,
                  var12,
                  var13,
                  var26,
                  var24,
                  var25,
                  var11,
                  var16,
                  var21,
                  var22,
                  var2,
                  var23,
                  var39,
                  PackedColor.handle(this.prepare(var3), Math.round(52.0F * var3)),
                  Math.max(8.0F, 10.0F * var2),
                  Math.max(1.0F, 1.4F * var2),
                  var6
               );
               this.handle(
                  var1,
                  var12,
                  var13,
                  var26,
                  var24,
                  var25,
                  var11,
                  var16,
                  var21,
                  var22,
                  var2,
                  var23,
                  var39,
                  PackedColor.handle(this.check(var3), Math.round(32.0F * var3)),
                  Math.max(16.0F, 22.0F * var2),
                  Math.max(2.0F, 3.0F * var2),
                  var6
               );
            }

            if (this.itemProject.process("Тень")) {
               this.handle(
                  var1,
                  var12,
                  var13,
                  var26,
                  var24,
                  var25,
                  var11,
                  var16,
                  var21,
                  var22,
                  var2,
                  var23,
                  var39,
                  this.select(var3),
                  Math.max(4.0F, 4.0F * var2),
                  Math.max(1.0F, 1.0F * var2),
                  var6
               );
            }

            this.handle(var1, var12, var13, var26, var24, var25, var11, var16, var21, var22, var2, var23, var39, var36, var3, false, true, var6);
            if (this.update()) {
               this.handle(
                  var1,
                  var12,
                  var13,
                  var26,
                  var24,
                  var25,
                  var11,
                  var16,
                  var21,
                  var22,
                  var2,
                  var23,
                  this.resolve(var3),
                  Math.max(1.0F, this.compute() * 0.55F),
                  var6
               );
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      float var15
   ) {
      int var16 = 0;

      for (ModuleListHudRenderer.TextureState var18 : frameCheck) {
         float var19 = var18.context.update();
         if (!(var19 <= 0.01F)) {
            var16++;
         }
      }

      if (var16 > 0) {
         if (keyFilter.length < var16) {
            keyFilter = new float[var16];
            requestAdapt = new float[var16];
         }

         float[] var31 = keyFilter;
         float[] var32 = requestAdapt;
         float var33 = 0.0F;
         float var20 = Float.MAX_VALUE;
         float var21 = 0.0F;
         int var22 = 0;

         for (ModuleListHudRenderer.TextureState var24 : frameCheck) {
            float var25 = var24.context.update();
            if (!(var25 <= 0.01F)) {
               float var26 = Math.max(1.0F, var24.handle(var7, var6, var11, var12, var13) * var8);
               float var27 = var4 * var25 + (var22 < var16 - 1 ? var5 * var25 : 0.0F);
               var33 += var27;
               var31[var22] = var26;
               var32[var22] = var33;
               var20 = Math.min(var20, var26);
               var21 = Math.max(var21, var26);
               var22++;
            }
         }

         if (!(var20 <= 1.0F) && !(var33 <= 1.0F)) {
            float var34 = Math.min(15.0F * var9, var4 * 0.5F);
            int var35 = this.render(var10);
            float var36 = Math.max(1.5F, 2.0F * var8);
            boolean var37 = this.packetSave.process("Обволакивание");
            if (this.render()) {
               this.handle(var1, var2, var3, var21, var33, var34, var10);
               if (var11) {
                  float var39 = Math.max(1.35F, 1.8F * var8);
                  float var40 = var2 + handle(5.0F * var8, var21 - 5.0F * var8 - var39, var15);
                  var1.process(var40, var3 + 5.0F * var9, var39, Math.max(1.0F, var33 - 10.0F * var9), var39, this.prepare(var10), this.check(var10));
               }
            } else {
               if (var14) {
                  if (var37) {
                     this.handle(
                        var1,
                        var2,
                        var3,
                        var21,
                        var31,
                        var32,
                        var16,
                        var20,
                        var33,
                        var4,
                        var9,
                        var34,
                        var36,
                        PackedColor.handle(this.prepare(var10), Math.round(52.0F * var10)),
                        Math.max(8.0F, 10.0F * var9),
                        Math.max(1.0F, 1.4F * var9),
                        var15
                     );
                     this.handle(
                        var1,
                        var2,
                        var3,
                        var21,
                        var31,
                        var32,
                        var16,
                        var20,
                        var33,
                        var4,
                        var9,
                        var34,
                        var36,
                        PackedColor.handle(this.check(var10), Math.round(32.0F * var10)),
                        Math.max(16.0F, 22.0F * var9),
                        Math.max(2.0F, 3.0F * var9),
                        var15
                     );
                  } else {
                     var1.handle(
                        var2,
                        var3,
                        var21,
                        var33,
                        var34,
                        Math.max(8.0F, 10.0F * var9),
                        Math.max(1.0F, 1.4F * var9),
                        PackedColor.handle(this.prepare(var10), Math.round(52.0F * var10))
                     );
                     var1.handle(
                        var2,
                        var3,
                        var21,
                        var33,
                        var34,
                        Math.max(16.0F, 22.0F * var9),
                        Math.max(2.0F, 3.0F * var9),
                        PackedColor.handle(this.check(var10), Math.round(32.0F * var10))
                     );
                  }
               }

               if (this.itemProject.process("Тень")) {
                  if (var37) {
                     this.handle(
                        var1,
                        var2,
                        var3,
                        var21,
                        var31,
                        var32,
                        var16,
                        var20,
                        var33,
                        var4,
                        var9,
                        var34,
                        var36,
                        this.select(var10),
                        Math.max(4.0F, 4.0F * var9),
                        Math.max(1.0F, 1.0F * var9),
                        var15
                     );
                  } else {
                     var1.handle(var2, var3, var21, var33, var34, Math.max(4.0F, 4.0F * var9), Math.max(1.0F, 1.0F * var9), this.select(var10));
                  }
               }

               if (this.onTick()) {
                  if (var37) {
                     this.handle(var1, var2, var3, var21, var31, var32, var16, var20, var33, var4, var9, var34, var36, var35, var10, true, false, var15);
                  } else {
                     var1.handle(23.0F);
                     var1.handle(var2, var3, var21, var33, var34, var10);
                  }
               }

               if (var37) {
                  this.handle(var1, var2, var3, var21, var31, var32, var16, var20, var33, var4, var9, var34, var36, var35, var10, false, true, var15);
               } else {
                  var1.handle(var2, var3, var21, var33, var34, var35);
               }

               if (this.update()) {
                  if (var37) {
                     this.handle(
                        var1,
                        var2,
                        var3,
                        var21,
                        var31,
                        var32,
                        var16,
                        var20,
                        var33,
                        var4,
                        var9,
                        var34,
                        this.resolve(var10),
                        Math.max(1.0F, this.compute() * 0.55F),
                        var15
                     );
                  } else {
                     var1.handle(var2, var3, var21, var33, var34, this.resolve(var10), Math.max(1.0F, this.compute() * 0.55F));
                  }
               }

               if (var11) {
                  float var38 = Math.max(1.35F, 1.8F * var8);
                  float var28 = var2 + handle(5.0F * var8, var21 - 5.0F * var8 - var38, var15);
                  int var29 = this.prepare(var10);
                  int var30 = this.check(var10);
                  var1.process(var28, var3 + 5.0F * var9, var38, Math.max(1.0F, var33 - 10.0F * var9), var38, var29, var30);
               }
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float[] var5,
      float[] var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      float var14,
      float var15
   ) {
      if (var7 > 0 && PackedColor.handle(var13) > 0) {
         float var16 = Math.max(1.0F, var14);
         float var17 = var16 * 0.5F;
         handle(var1, var2, var4, 0.0F, var3 + var12, var16, Math.max(1.0F, var9 - var12 * 2.0F), var17, var13, var15);
         handle(var1, var2, var4, var12, var3, Math.max(1.0F, var5[0] - var12 * 2.0F), var16, var17, var13, var15);

         for (int var18 = 0; var18 < var7; var18++) {
            float var19 = var18 == 0 ? 0.0F : var6[var18 - 1];
            float var20 = var6[var18];
            float var21 = var18 == 0 ? var5[var18] : var5[var18 - 1];
            float var22 = var18 == var7 - 1 ? var5[var18] : var5[var18 + 1];
            float var23 = var18 == 0 ? var12 : (var5[var18] > var21 + 0.5F ? this.process(var12, var5[var18] - var21, var10, var11) : 0.0F);
            float var24 = var18 == var7 - 1 ? var12 : (var5[var18] > var22 + 0.5F ? this.process(var12, var5[var18] - var22, var10, var11) : 0.0F);
            float var25 = var3 + var19 + var23;
            float var26 = var3 + var20 - var24;
            if (var26 > var25) {
               handle(var1, var2, var4, var5[var18] - var16, var25, var16, var26 - var25, var17, var13, var15);
            }

            if (var18 < var7 - 1 && Math.abs(var5[var18] - var5[var18 + 1]) > 0.5F) {
               float var27 = Math.min(var5[var18], var5[var18 + 1]);
               float var28 = Math.max(var5[var18], var5[var18 + 1]);
               float var29 = var28 - var27;
               float var30 = this.process(var12, var29, var10, var11);
               handle(var1, var2, var4, var27 + var30 * 0.35F, var3 + var20 - var17, Math.max(1.0F, var29 - var30 * 0.7F), var16, var17, var13, var15);
            }
         }

         float var31 = var5[var7 - 1];
         handle(var1, var2, var4, var12, var3 + var9 - var16, Math.max(1.0F, var31 - var12 * 2.0F), var16, var17, var13, var15);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float[] var5,
      float[] var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      int var14,
      float var15,
      boolean var16,
      boolean var17,
      float var18
   ) {
      for (int var19 = 0; var19 < var7; var19++) {
         float var20 = var19 == 0 ? 0.0F : var6[var19 - 1];
         float var21 = var6[var19] - var20;
         float var22 = var19 == 0 ? var5[var19] : var5[var19 - 1];
         float var23 = var19 == var7 - 1 ? var5[var19] : var5[var19 + 1];
         float var24 = var19 == 0 ? var12 : 0.0F;
         float var25 = var19 == var7 - 1 ? var12 : 0.0F;
         float var26 = var19 == 0 ? var12 : (var5[var19] > var22 + 0.5F ? this.process(var12, var5[var19] - var22, var10, var11) : 0.0F);
         float var27 = var19 == var7 - 1 ? var12 : (var5[var19] > var23 + 0.5F ? this.process(var12, var5[var19] - var23, var10, var11) : 0.0F);
         this.handle(var1, var2, var3 + var20, var4, 0.0F, var5[var19], var21, var24, var26, var27, var25, var14, var15, var16, var17, var18);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float[] var5,
      float[] var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      float var14
   ) {
      int var15 = PackedColor.handle(var13);
      if (var15 > 0) {
         float var16 = 0.0F;
         this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var16, var13, 1.0F, false, true, var14);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float[] var5,
      float[] var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      int var14,
      float var15,
      float var16,
      float var17
   ) {
      if (PackedColor.handle(var14) > 0 && !(var15 <= 0.0F) && !(var16 <= 0.0F)) {
         for (int var18 = 0; var18 < var7; var18++) {
            float var19 = var18 == 0 ? 0.0F : var6[var18 - 1];
            float var20 = var6[var18] - var19;
            float var21 = var18 == 0 ? var5[var18] : var5[var18 - 1];
            float var22 = var18 == var7 - 1 ? var5[var18] : var5[var18 + 1];
            float var23 = var18 == 0 ? var12 : 0.0F;
            float var24 = var18 == var7 - 1 ? var12 : 0.0F;
            float var25 = var18 == 0 ? var12 : (var5[var18] > var21 + 0.5F ? this.process(var12, var5[var18] - var21, var10, var11) : 0.0F);
            float var26 = var18 == var7 - 1 ? var12 : (var5[var18] > var22 + 0.5F ? this.process(var12, var5[var18] - var22, var10, var11) : 0.0F);
            this.handle(var1, var2, var3 + var19, var4, 0.0F, var5[var18], var20, var23, var25, var26, var24, var14, var15, var16, var17);
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12,
      float var13,
      float var14,
      float var15
   ) {
      if (!(var6 <= 0.5F) && !(var7 <= 0.5F)) {
         float var16 = var2 + handle(var5, var4 - var5 - var6, var15);
         float var17 = handle(var8, var9, var15);
         float var18 = handle(var9, var8, var15);
         float var19 = handle(var10, var11, var15);
         float var20 = handle(var11, var10, var15);
         var1.handle(var16, var3, var6, var7, var17, var18, var19, var20, var13, var14, var12);
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12,
      float var13,
      boolean var14,
      boolean var15,
      float var16
   ) {
      if (!(var6 <= 0.5F) && !(var7 <= 0.5F)) {
         float var17 = var2 + handle(var5, var4 - var5 - var6, var16);
         float var18 = handle(var8, var9, var16);
         float var19 = handle(var9, var8, var16);
         float var20 = handle(var10, var11, var16);
         float var21 = handle(var11, var10, var16);
         if (var14) {
            var1.handle(23.0F);
            var1.handle(var17, var3, var6, var7, var18, var19, var20, var21, var13);
         }

         if (var15) {
            var1.handle(var17, var3, var6, var7, var18, var19, var20, var21, var12);
         }
      }
   }

   private float process(float var1, float var2, float var3, float var4) {
      float var5 = Math.max(4.0F * var4, 3.0F);
      float var6 = Math.max(0.0F, var2) * 0.72F;
      float var7 = var3 * 0.42F;
      return Math.min(var1, Math.max(var5, Math.min(var6, var7)));
   }

   private int refresh(float var1) {
      int var2 = this.handle(var1);
      if (!"Тёмный".equals(this.previous.compute()) && (this.encodePoint() || !this.refresh())) {
         return var2;
      }

      int var3 = Math.round((210.0F - 92.0F * this.drawAnimation()) * var1);
      return PackedColor.handle(var2, Math.max(PackedColor.handle(var2), var3));
   }

   private int render(float var1) {
      int var2 = this.handle(var1);
      if ("Тёмный".equals(this.previous.compute())) {
         int var3 = Math.round(210.0F * var1);
         return PackedColor.handle(var2, Math.max(PackedColor.handle(var2), var3));
      } else {
         return var2;
      }
   }

   private void animate() {
      instance.clear();
      frameCheck.clear();
      boolean var1 = this.encodePoint();

      for (Module var3 : WildClient.instance.data.process()) {
         if (var3 != null && var3.category != ModuleCategory.Visuals && !"Menu".equals(var3.displayName) && this.handle(var3.category)) {
            ModuleListHudRenderer.TextureState var4 = moduleCollect.computeIfAbsent(var3, ModuleListHudRenderer.TextureState::new);
            var4.handle(var3);
            if (var1) {
               var4.context.handle();
               var4.context.handle(var3.enabled ? 1.0 : 0.0, 0.23F, Easings.handler, false);
               if (var3.enabled) {
                  instance.add(var3);
               }

               if (var3.enabled || var4.context.update() > 0.01F) {
                  frameCheck.add(var4);
               }
            } else {
               float var5 = var4.config.handle(var3.enabled ? 1.0F : 0.0F, var3.enabled ? colorMeasure : animationSchedule);
               var4.state.handle(var3.enabled ? 1.0F : 0.0F, outputCollapse);
               var4.cache.handle(0.0F, profileInvoke);
               if (var3.enabled) {
                  instance.add(var3);
               }

               if (var3.enabled || var5 > 0.01F || Math.abs(var4.config.compute()) > 0.01F) {
                  frameCheck.add(var4);
               }
            }
         }
      }

      frameCheck.sort(ModuleListHudRenderer.TextureState.instance);
   }

   private boolean handle(ModuleCategory var1) {
      return switch (var1 == null ? ModuleCategory.Misc : var1) {
         case Combat -> this.vectorEncode.process("Combat");
         case Movement -> this.vectorEncode.process("Movement");
         case Player -> this.vectorEncode.process("Player");
         case Misc -> this.vectorEncode.process("Misc");
         case Visuals -> false;
      };
   }

   private void handle(
      RoundedRectRenderer var1,
      ModuleListHudRenderer.TextureState var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      int var14,
      boolean var15,
      boolean var16,
      boolean var17,
      float var18
   ) {
      int var19 = Math.round(255.0F * var11 * var12);
      int var20 = PackedColor.handle(var13, var19);
      int var21 = PackedColor.handle(var14, Math.round(PackedColor.handle(var14) * var12));
      float var22 = var15 ? 8.0F * var9 : 0.0F;
      float var23 = var8 * 0.92F;
      float var24 = var16 && var2.enabled != null ? TextMeasureCache.process(FontRegistry.current, var2.enabled, var23) : 0.0F;
      float var25 = var16 && var2.enabled != null ? 7.0F * var9 : 0.0F;
      String var26 = var17 ? var2.renderer : "";
      boolean var27 = !var26.isEmpty();
      float var28 = var27 ? 6.0F * var9 : 0.0F;
      float var29 = var27 ? TextMeasureCache.process(FontRegistry.instance, var26, var8) : 0.0F;
      float var30 = Math.max(12.0F * var9, var5 - var7 * 2.0F - var22 - var24 - var25 - var28 - var29);
      String var31 = handle(var2.selection, var8, var30);
      float var32 = TextMeasureCache.process(FontRegistry.config, var31, var8);
      float var33 = var24 + var25 + var32 + var28 + var29;
      float var34 = var3 + var7 + var22;
      float var35 = var34 + (var24 > 0.0F ? var24 + var25 : 0.0F);
      float var36 = var3 + var5 - var7 - var22 - var33;
      float var37 = var36 + (var24 > 0.0F ? var24 + var25 : 0.0F);
      float var38 = handle(var34, var36, var18);
      float var39 = handle(var35, var37, var18);
      float var40 = var39 + var32 + var28;
      if (var16 && var2.enabled != null) {
         int var41 = PackedColor.handle(this.prepare(var11), var19);
         var1.handle(FontRegistry.current, var38, var4 + var6 * 0.5F + 5.0F * var10, var23, var2.enabled, var41);
      }

      float var43 = var4 + var6 * 0.5F + 5.0F * var10;
      var1.handle(FontRegistry.config, var39, var43, var8, var31, var20);
      if (var27) {
         var1.handle(FontRegistry.instance, var40, var43, var8, var26, var21);
      }

      if (var12 < 0.98F) {
         float var42 = 3.0F * var10;
         var1.process(var3 + var5 - var7 * 0.5F, var4 + var6 * 0.5F, var42, 0.0F, 360.0F, var21);
      }
   }

   private void process(
      RoundedRectRenderer var1,
      ModuleListHudRenderer.TextureState var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      int var14,
      boolean var15,
      boolean var16,
      boolean var17,
      float var18
   ) {
      if (!(var5 <= 2.0F) && !(var6 <= 4.0F) && !(var12 <= 0.005F)) {
         float var19 = tick(var2.state.process());
         float var20 = Math.min(1.0F, Math.max(0.0F, var2.cache.process()));
         int var21 = Math.round(255.0F * var11 * var12 * (0.58F + var19 * 0.42F));
         int var22 = PackedColor.handle(var13, var21);
         int var23 = PackedColor.handle(var14, Math.round(PackedColor.handle(var14) * var12));
         float var24 = var15 ? 8.0F * var9 : 0.0F;
         float var25 = var8 * 0.92F;
         float var26 = var16 && var2.enabled != null ? TextMeasureCache.process(FontRegistry.current, var2.enabled, var25) : 0.0F;
         float var27 = var16 && var2.enabled != null ? 7.0F * var9 : 0.0F;
         String var28 = var17 ? var2.renderer : "";
         boolean var29 = !var28.isEmpty();
         float var30 = var29 ? 6.0F * var9 : 0.0F;
         float var31 = var29 ? TextMeasureCache.process(FontRegistry.instance, var28, var8) : 0.0F;
         float var32 = Math.max(12.0F * var9, var5 - var7 * 2.0F - var24 - var26 - var27 - var30 - var31);
         String var33 = handle(var2.selection, var8, var32);
         float var34 = TextMeasureCache.process(FontRegistry.config, var33, var8);
         float var35 = var26 + var27 + var34 + var30 + var31;
         float var36 = var3 + var7 + var24;
         float var37 = var36 + (var26 > 0.0F ? var26 + var27 : 0.0F);
         float var38 = var3 + var5 - var7 - var24 - var35;
         float var39 = var38 + (var26 > 0.0F ? var26 + var27 : 0.0F);
         float var40 = handle(var36, var38, var18);
         float var41 = handle(var37, var39, var18);
         float var42 = var41 + var34 + var30;
         float var43 = var4 + var6 * 0.5F + 5.0F * var10 + process(var2.current.compute() * 0.018F, -2.8F * var10, 2.8F * var10);
         float var44 = Math.min(12.0F * var10, Math.max(1.0F, var6 * 0.5F));
         var1.handle(var3 + 1.0F, var4 + 1.0F, Math.max(1.0F, var5 - 2.0F), Math.max(1.0F, var6 - 2.0F), var44, var44, var44, var44);

         try {
            if (var16 && var2.enabled != null) {
               int var45 = PackedColor.compute(PackedColor.handle(this.prepare(var11), var21), PackedColor.compute(255, 255, 255, var21), var20 * 0.22F);
               var1.handle(FontRegistry.current, var40, var43, var25, var2.enabled, var45);
            }

            int var49 = PackedColor.compute(var22, PackedColor.compute(255, 255, 255, var21), var20 * 0.14F);
            var1.handle(FontRegistry.config, var41, var43, var8, var33, var49);
            if (var29) {
               var1.handle(FontRegistry.instance, var42, var43, var8, var28, var23);
            }
         } finally {
            var1.apply();
         }

         if (var12 < 0.98F) {
            float var50 = 3.0F * var10;
            var1.process(var3 + var5 - var7 * 0.5F, var4 + var6 * 0.5F, var50, 0.0F, 360.0F, var23);
         }
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10) {
      float var11 = Math.min(10.0F, var5 * 0.45F);
      this.handle(var1, var2, var3, var4, var5, var11, var8, var10);
      var1.handle(FontRegistry.instance, var2 + var6, var3 + var5 * 0.5F + 5.0F, var7, "Нет активных модулей ", var9);
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8) {
      if (this.itemProject.process("Тень")) {
         if (var8) {
            var1.handle(var2, var3, var4, var5, var6, 4.0F, 1.0F, PackedColor.compute(0, 0, 0, Math.round(80.0F * var7)));
         } else {
            var1.handle(var2, var3, var4, var5, var6, this.tick() ? 6.0F : 4.0F, 1.0F, this.select(var7));
         }
      }

      if (this.onTick()) {
         var1.handle(23.0F);
         var1.handle(var2, var3, var4, var5, var6, var7);
      }

      if (!var8 && this.refresh()) {
         this.process(var1, var2, var3, var4, var5, var6, var7);
      } else if (this.select()) {
         var1.handle(var2, var3, var4, var5, var6, this.handle(var7));
      } else {
         var1.handle(var2, var3, var4, var5, var6, this.prepare() ? this.compute(var7) : this.handle(var7));
         if (this.update()) {
            var1.handle(var2, var3, var4, var5, var6, this.resolve(var7), Math.max(1.0F, this.compute() * 0.55F));
         }
      }
   }

   private static void handle(
      RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, float var9
   ) {
      var0.handle(var1 + handle(var3, var2 - var3 - var5, var9), var4, var5, var6, var7, var8);
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * Math.max(0.0F, Math.min(1.0F, var2));
   }

   private static float tick(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 <= var2 + var4 && var1 >= var3 && var1 <= var3 + var5;
   }

   private static String handle(String var0, float var1, float var2) {
      if (var0 != null && !var0.isEmpty() && !(TextMeasureCache.process(FontRegistry.config, var0, var1) <= var2)) {
         String var3 = "...";
         float var4 = TextMeasureCache.process(FontRegistry.config, var3, var1);
         if (var4 >= var2) {
            return var3;
         }

         int var5 = 0;
         int var6 = var0.length();

         while (var5 < var6) {
            int var7 = var5 + var6 + 1 >>> 1;
            if (TextMeasureCache.process(FontRegistry.config, var0.substring(0, var7), var1) + var4 <= var2) {
               var5 = var7;
            } else {
               var6 = var7 - 1;
            }
         }

         return var5 <= 0 ? var3 : var0.substring(0, var5) + var3;
      } else {
         return var0 == null ? "" : var0;
      }
   }

   static final class TextureState {
      static final Comparator<ModuleListHudRenderer.TextureState> instance = (var0, var1) -> {
         int var2 = Float.compare(var1.handler, var0.handler);
         return var2 != 0 ? var2 : var0.selection.compareToIgnoreCase(var1.selection);
      };
      private final Module data;
      final DoubleAnimator context = new DoubleAnimator();
      final SpringAnimation config = new SpringAnimation(0.0F);
      final SpringAnimation state = new SpringAnimation(0.0F);
      final SpringAnimation cache = new SpringAnimation(0.0F);
      final SpringAnimation output = new SpringAnimation(0.0F);
      final SpringAnimation current = new SpringAnimation(0.0F);
      private final SpringAnimation active = new SpringAnimation(1.0F);
      private final SpringAnimation mode = new SpringAnimation(1.0F);
      String selection = "";
      String enabled;
      String renderer = "";
      private float handler;
      private float animationDraw;
      private float pointEncode;
      float animator;
      float source;
      float target = 1.0F;
      float pending = 1.0F;
      private boolean previous;
      private boolean latest;
      private boolean summary;
      boolean matrixBlend;

      private TextureState(Module var1) {
         this.data = var1;
      }

      void handle(Module var1) {
         boolean var2 = var1.enabled;
         if (!this.previous) {
            this.config.handle(var2 ? 1.0F : 0.0F);
            this.state.handle(var2 ? 1.0F : 0.0F);
            this.summary = var2;
            this.previous = true;
         } else if (var2 != this.summary) {
            this.cache.compute(Math.min(2.25F, this.cache.process() + (var2 ? 1.2F : 0.72F)));
            if (var2) {
               this.state.handle(0.0F);
            }

            this.summary = var2;
         }

         String var3 = var1.description;
         if (var3 == null || var3.isEmpty()) {
            var3 = var1.displayName;
         }

         if (!var3.equals(this.selection)) {
            this.selection = var3;
            this.handler = TextMeasureCache.process(FontRegistry.config, this.selection, 24.0F);
         }

         String var4 = var1.category == null ? null : var1.category.handle();
         if (var4 == null) {
            this.enabled = null;
            this.animationDraw = 0.0F;
         } else if (!var4.equals(this.enabled)) {
            this.enabled = var4;
            this.animationDraw = TextMeasureCache.process(FontRegistry.current, this.enabled, 22.08F);
         }

         String var5 = var1.keyCode == -1 ? "" : "[" + KeycodeNames.handle(var1.keyCode) + "]";
         if (!var5.equals(this.renderer)) {
            this.renderer = var5;
            this.pointEncode = this.renderer.isEmpty() ? 0.0F : TextMeasureCache.process(FontRegistry.instance, this.renderer, 24.0F);
         }
      }

      float handle(float var1, float var2, boolean var3, boolean var4, boolean var5) {
         float var6 = var4 && this.enabled != null ? TextMeasureCache.process(FontRegistry.current, this.enabled, var1 * 0.92F) + 7.0F : 0.0F;
         float var7 = var5 && !this.renderer.isEmpty() ? TextMeasureCache.process(FontRegistry.instance, this.renderer, var1) + 6.0F : 0.0F;
         return TextMeasureCache.process(FontRegistry.config, this.selection, var1) + var6 + var7 + var2 * 2.0F + (var3 ? 8.0F : 0.0F);
      }

      void handle(float var1, float var2, float var3, float var4) {
         if (!this.latest) {
            this.output.handle(var1);
            this.current.handle(var2);
            this.active.handle(var3);
            this.mode.handle(var4);
            this.latest = true;
         } else {
            this.output.handle(var1, ModuleListHudRenderer.rendererScan);
            this.current.handle(var2, ModuleListHudRenderer.rendererScan);
            this.active.handle(var3, ModuleListHudRenderer.sourceBuild);
            this.mode.handle(var4, ModuleListHudRenderer.sourceBuild);
         }

         this.animator = this.output.process();
         this.source = this.current.process();
         this.target = Math.max(0.0F, this.active.process());
         this.pending = Math.max(0.0F, this.mode.process());
      }

      boolean handle() {
         return this.matrixBlend;
      }
   }
}
