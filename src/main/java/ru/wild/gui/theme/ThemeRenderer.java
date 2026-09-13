package ru.wild.gui.theme;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import ru.wild.WildClient;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.api.setting.Setting;
import ru.wild.config.HudProfileConfig;
import ru.wild.core.AnimationClock;
import ru.wild.core.GravityGridRenderer;
import ru.wild.gui.hud.HudElementMetadata;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.SpringFloat;
import ru.wild.util.math.SpringParameters;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class ThemeRenderer {
   private static final float data = 0.65F;
   private static final float context = 1.0F;
   static final SpringParameters config = SpringParameters.handle(4.0F, 0.85F);
   static final SpringParameters state = SpringParameters.handle(2.5F, 0.9F);
   static final SpringParameters cache = SpringParameters.handle(6.0F, 0.8F);
   static final SpringParameters output = SpringParameters.handle(5.0F, 0.85F);
   private static final SpringParameters current = SpringParameters.handle(2.2F, 1.0F);
   private static final float active = 12.0F;
   private static final float mode = 5.0F;
   private static final float selection = 6.0F;
   private static final int enabled = 32;
   private static final ThemeRenderer.Bounds renderer = new ThemeRenderer.Bounds(0.82F, 1.18F, 0.72F, 0.22F, 6.0F);
   private static final ThemeRenderer.Bounds handler = new ThemeRenderer.Bounds(0.78F, 1.16F, 0.46F, 0.1F, 6.0F);
   private static final ThemeRenderer.Bounds animationDraw = new ThemeRenderer.Bounds(0.72F, 1.48F, 0.42F, 0.34F, 6.0F);
   private static final ThemeRenderer.Bounds pointEncode = new ThemeRenderer.Bounds(0.78F, 1.34F, 0.36F, 0.26F, 6.0F);
   private static final ThemeRenderer.Bounds animator = new ThemeRenderer.Bounds(0.72F, 1.36F, 0.38F, 0.42F, 6.0F);
   private static final ThemeRenderer.Bounds source = new ThemeRenderer.Bounds(0.72F, 1.32F, 0.32F, 0.56F, 6.0F);
   private static final ThemeRenderer.Bounds target = new ThemeRenderer.Bounds(0.72F, 1.18F, 0.34F, 0.52F, 6.0F);
   private static final ThemeRenderer.Bounds pending = new ThemeRenderer.Bounds(0.7F, 1.42F, 0.45F, 0.72F, 6.0F);
   private static final ThemeRenderer.Bounds previous = new ThemeRenderer.Bounds(0.78F, 1.12F, 0.38F, 0.34F, 6.0F);
   private static final ThemeRenderer.Bounds latest = new ThemeRenderer.Bounds(0.72F, 1.32F, 0.38F, 0.56F, 6.0F);
   private static final ThemeRenderer.Bounds summary = new ThemeRenderer.Bounds(0.76F, 1.32F, 0.24F, 0.3F, 6.0F);
   private static final ThemeRenderer.Bounds matrixBlend = new ThemeRenderer.Bounds(0.72F, 1.35F, 0.4F, 0.58F, 6.0F);
   private static final ThemeRenderer.Bounds vectorMatch = new ThemeRenderer.Bounds(0.72F, 1.42F, 0.42F, 0.55F, 6.0F);
   private static final ThemeRenderer itemProject = new ThemeRenderer();
   private final Map<String, ThemeRenderer.PrimaryAnimationState> responseCompute = new HashMap<>();
   private final Map<String, ThemeRenderer.DataRecord> providerFetch = new ConcurrentHashMap<>();
   private final Map<String, Float> profileDraw = new ConcurrentHashMap<>();
   private final ThemeRenderer.CacheEntry[] vectorPerform = new ThemeRenderer.CacheEntry[32];
   private final ThemeRenderer.CacheEntry[] eventAttach = new ThemeRenderer.CacheEntry[32];
   private final double[] serverRead = new double[1];
   private final double[] positionAdvance = new double[1];
   private final ThemeRenderer.AnimationState frameCheck = new ThemeRenderer.AnimationState();
   private RoundedRectRenderer moduleCollect;
   private MinecraftClient providerClose;
   private boolean presetSave;
   private float windowConvert;
   private float presetWrite;
   private boolean colorMeasure;
   private boolean animationSchedule;
   private boolean rendererScan;
   private boolean sourceBuild;
   private boolean outputCollapse;
   private boolean profileInvoke;
   private boolean sourceSchedule;
   private boolean timerRender;
   private boolean scaleSave;
   private int colorCompute;
   private int scaleAdapt;
   private String textureRun = null;
   private float indexBind = Float.NaN;
   private float actionRead = Float.NaN;
   private float configCollapse = 0.0F;
   private float dataValidate = Float.NaN;
   private float scaleRender = Float.MAX_VALUE;
   private boolean clientRefresh;
   private boolean keyFilter;
   private boolean requestAdapt;
   private float timerMeasure;
   private float vectorEncode;
   private final SpringFloat requestReceive = new SpringFloat(AnimationClock.handle(), output, 0.0F, 0.0F, 1.0F, 0.01F, 0.01F);
   private final SpringFloat windowProcess = new SpringFloat(AnimationClock.handle(), current, 0.0F, 0.0F, 1.0F, 0.01F, 0.01F);
   private float packetSave = 1.0F;
   private float entryAnimate = 1.0F;
   private String playerCollect = "Тёмный";
   private float stateApply = 5.5F;
   private float matrixFilter = 18.0F;
   private float layerSample = 0.72F;
   private String worldSend = "Выпуклая";
   private boolean targetWrite = true;
   private boolean resultEncode = true;
   private boolean messageParse = true;
   private boolean providerRead = true;
   private boolean matrixBlend2 = true;
   private boolean scalePerform = true;
   private int contextExpand;
   private int keyProcess;
   private ThemePalette actionConvert;
   private boolean screenRead;
   private ThemeColors animationExpand;
   public final ThemeRenderer.SecondaryCacheEntry instance = new ThemeRenderer.SecondaryCacheEntry();

   private ThemeRenderer() {
      for (int var1 = 0; var1 < 32; var1++) {
         this.vectorPerform[var1] = new ThemeRenderer.CacheEntry();
         this.eventAttach[var1] = new ThemeRenderer.CacheEntry();
      }
   }

   public static ThemeRenderer handle() {
      return itemProject;
   }

   public void process() {
      this.animationSchedule = false;
      this.textureRun = null;
   }

   public void handle(MinecraftClient var1, RoundedRectRenderer var2, int var3, int var4) {
      this.providerClose = var1;
      this.moduleCollect = Objects.requireNonNull(var2, "renderer");
      this.colorCompute = Math.max(0, var3);
      this.scaleAdapt = Math.max(0, var4);
      this.presetSave = true;
      this.colorMeasure = false;
      this.clientRefresh = false;
      this.indexBind = Float.NaN;
      this.actionRead = Float.NaN;
      if (var1 != null && var1.getWindow() != null) {
         this.scaleSave = var1.currentScreen instanceof ChatScreen;
         long var5 = var1.getWindow().getHandle();
         if (var5 != 0L) {
            GLFW.glfwGetCursorPos(var5, this.serverRead, this.positionAdvance);
            if (Double.isFinite(this.serverRead[0]) && Double.isFinite(this.positionAdvance[0])) {
               this.windowConvert = (float)this.serverRead[0];
               this.presetWrite = (float)this.positionAdvance[0];
               this.colorMeasure = true;
            }

            if (this.colorMeasure && this.scaleSave) {
               boolean var7 = GLFW.glfwGetMouseButton(var5, 0) == 1;
               this.rendererScan = var7 && !this.sourceBuild;
               this.animationSchedule = var7;
               this.sourceBuild = var7;
               boolean var8 = GLFW.glfwGetMouseButton(var5, 2) == 1;
               this.profileInvoke = var8 && !this.outputCollapse;
               this.outputCollapse = var8;
               boolean var9 = GLFW.glfwGetMouseButton(var5, 1) == 1;
               this.sourceSchedule = var9 && !this.timerRender;
               this.timerRender = var9;
            } else {
               this.animationSchedule = this.rendererScan = this.profileInvoke = this.sourceSchedule = false;
               this.sourceBuild = this.outputCollapse = this.timerRender = false;
            }
         }

         if (!this.animationSchedule) {
            this.textureRun = null;
         }

         if (!this.animationSchedule && this.keyFilter) {
            this.keyFilter = false;
            if (WildClient.instance != null && WildClient.instance.renderer != null) {
               WildClient.instance.renderer.compute();
            }
         }
      } else {
         this.animationSchedule = this.rendererScan = this.profileInvoke = this.sourceSchedule = false;
         this.sourceBuild = this.outputCollapse = this.timerRender = false;
         this.scaleSave = false;
      }
   }

   public void compute() {
      this.refresh();
      boolean var1 = this.scaleSave && this.animationSchedule && this.textureRun != null;
      this.windowProcess.compute(var1 ? 1.0F : 0.0F);
      float var2 = this.windowProcess.handle();
      if ((this.scaleSave || !(var2 <= 0.01F)) && this.colorCompute > 0 && this.scaleAdapt > 0) {
         ThemeColors var3 = this.render();
         int var4 = var3 == null ? -7473153 : var3.save();
         int var5 = var3 == null ? -41059 : var3.submit();
         GravityGridRenderer.handle()
            .handle(
               this.colorCompute,
               this.scaleAdapt,
               this.vectorPerform,
               this.contextExpand,
               this.textureRun,
               this.windowConvert,
               this.presetWrite,
               var2,
               var4,
               var5
            );
      }
   }

   public void handle(int var1, int var2, ThemeRenderer.CacheEntry[] var3, int var4, String var5, float var6, float var7, float var8) {
      ThemeColors var9 = this.render();
      int var10 = var9 == null ? -7473153 : var9.save();
      int var11 = var9 == null ? -41059 : var9.submit();
      GravityGridRenderer.handle().handle(var1, var2, var3, var4, var5, var6, var7, var8, var10, var11);
   }

   public void resolve() {
      if (this.scaleSave && this.colorMeasure) {
         float var1 = 170.0F;
         float var2 = 24.0F;

         for (Setting var4 : this.instance.handle()) {
            if (var4 instanceof BooleanSetting) {
               var2 += 28.0F;
            } else if (var4 instanceof NumberSetting) {
               var2 += 40.0F;
            } else if (var4 instanceof ModeSetting) {
               var2 += 28.0F;
            } else if (var4 instanceof ChoiceSetting var5) {
               var2 += 28.0F + var5.config.size() * 28.0F * var5.output.update();
            }
         }

         NeoStyleOptions.Bounds var33 = this.moduleCollect != null
            ? NeoStyleOptions.handle(this.moduleCollect, this.instance, this.timerMeasure, this.vectorEncode, 0.0F, 0.0F)
            : new NeoStyleOptions.Bounds(this.timerMeasure, this.vectorEncode, var1, var2);
         boolean var35 = this.requestAdapt && var33.contains(this.windowConvert, this.presetWrite, 10.0F);
         if (this.sourceSchedule) {
            if (this.clientRefresh) {
               this.requestAdapt = false;
            } else if (!var35) {
               this.requestAdapt = !this.requestAdapt;
               if (this.requestAdapt) {
                  this.timerMeasure = this.windowConvert;
                  this.vectorEncode = this.presetWrite;
               }
            }

            this.sourceSchedule = false;
         }

         if (this.rendererScan && (this.clientRefresh || !var35 && this.requestAdapt)) {
            this.requestAdapt = false;
         }
      }

      if (!this.scaleSave) {
         this.requestAdapt = false;
      }

      this.requestReceive.compute(this.requestAdapt ? 1.0F : 0.0F);
      float var31 = this.requestReceive.handle();
      if (var31 > 0.01F && this.moduleCollect != null) {
         this.moduleCollect.handle(this.colorCompute, this.scaleAdapt);
         NeoStyleOptions.handle(
            this.moduleCollect,
            this.instance,
            this.timerMeasure,
            this.vectorEncode,
            0.0F,
            0.0F,
            this.colorCompute,
            this.scaleAdapt,
            var31,
            this.windowConvert,
            this.presetWrite,
            this.rendererScan,
            this.animationSchedule
         );
         this.moduleCollect.process();
         float var32 = this.instance.instance.compute();
         float var34 = this.instance.data.compute();
         String var36 = this.instance.context.compute();
         float var37 = this.instance.config.compute();
         float var6 = this.instance.state.compute();
         float var7 = this.instance.cache.compute();
         String var8 = this.instance.output.compute();
         boolean var9 = this.instance.current.process("Тень");
         boolean var10 = this.instance.current.process("Обводка");
         boolean var11 = this.instance.current.process("Темные зоны");
         boolean var12 = this.instance.current.process("Верхняя накладка");
         boolean var13 = this.instance.current.process("Нижняя накладка");
         boolean var14 = this.instance.current.process("Тёмный рект поверх");
         if (var32 != this.packetSave
            || var34 != this.entryAnimate
            || !var36.equals(this.playerCollect)
            || var37 != this.stateApply
            || var6 != this.matrixFilter
            || var7 != this.layerSample
            || !var8.equals(this.worldSend)
            || var9 != this.targetWrite
            || var10 != this.resultEncode
            || var11 != this.messageParse
            || var12 != this.providerRead
            || var13 != this.matrixBlend2
            || var14 != this.scalePerform) {
            this.packetSave = var32;
            this.entryAnimate = var34;
            this.playerCollect = var36;
            this.stateApply = var37;
            this.matrixFilter = var6;
            this.layerSample = var7;
            this.worldSend = var8;
            this.targetWrite = var9;
            this.resultEncode = var10;
            this.messageParse = var11;
            this.providerRead = var12;
            this.matrixBlend2 = var13;
            this.scalePerform = var14;

            for (ResettableSettingGroup var16 : HudProfileConfig.process()) {
               if (var16 != this.instance) {
                  label184:
                  for (Setting var18 : var16.handle()) {
                     String settingName = var18.instance;
                     if (var18 instanceof NumberSetting numberSetting) {
                        switch (settingName) {
                           case "Прозрачность" -> numberSetting.handle(var32);
                           case "Прозрачность тёмных элементов" -> numberSetting.handle(var34);
                           case "Нео дистанция" -> numberSetting.handle(var37);
                           case "Нео размытие" -> numberSetting.handle(var6);
                           case "Нео интенсивность" -> numberSetting.handle(var7);
                           default -> {
                           }
                        }
                     } else if (var18 instanceof ModeSetting modeSetting) {
                        String mode = switch (settingName) {
                           case "Стилистика" -> var36;
                           case "Нео форма" -> var8;
                           default -> null;
                        };
                        if (mode != null) {
                           int modeIndex = modeSetting.config.indexOf(mode);
                           if (modeIndex != -1) {
                              modeSetting.current = modeIndex;
                              modeSetting.state = mode;
                           }
                        }
                     } else if (var18 instanceof ChoiceSetting choiceSetting && settingName.equals("Визуал")) {
                        for (BooleanSetting option : choiceSetting.config) {
                           switch (option.instance) {
                              case "Тень" -> option.process(var9);
                              case "Обводка" -> option.process(var10);
                              case "Темные зоны" -> option.process(var11);
                              case "Верхняя накладка" -> option.process(var12);
                              case "Нижняя накладка" -> option.process(var13);
                              case "Тёмный рект поверх" -> option.process(var14);
                              default -> {
                              }
                           }
                        }
                     }
                  }
               }
            }

            HudProfileConfig.resolve();
         }
      }

      this.presetSave = false;
      this.moduleCollect = null;
   }

   public ThemeRenderer.PrimaryCacheEntry handle(String var1, float var2, float var3, float var4, float var5) {
      if (!this.presetSave) {
         throw new IllegalStateException("beginFrame must be called first");
      }

      String var6 = var1.trim();
      ThemeRenderer.DataRecord var7 = this.providerFetch.get(var6);
      ThemeRenderer.PrimaryAnimationState var8 = this.responseCompute.computeIfAbsent(var1, var0 -> new ThemeRenderer.PrimaryAnimationState());
      var8.active = handle(var4);
      var8.mode = handle(var5);
      ThemeRenderer.Bounds var9 = this.compute(var6);
      boolean var10 = this.colorMeasure && this.scaleSave;
      Float var11 = var7 == null ? this.profileDraw.remove(var6) : null;
      boolean var12 = var11 != null;
      boolean var13 = var7 != null && var7.userResized() || var12;
      float var14 = var12 ? var11 : this.handle(var7, var13);
      ThemeRenderer.AnimationState var15 = this.compute(var6, var4, var5, var14);
      var14 = var15.compute();
      if (!var8.context) {
         var8.output = var15.handle();
         var8.current = var15.process();
      }

      float var16 = this.handle(var7, var2, var8.output, var9);
      float var17 = this.process(var7, var3, var8.current, var9);
      if (var12) {
         this.handle(var6, var16, var17, var14, var14, true);
         var7 = this.providerFetch.get(var6);
      }

      float var18 = var8.instance ? var8.renderer.handle() : var16;
      float var19 = var8.instance ? var8.handler.handle() : var17;
      boolean var20 = handle(this.windowConvert, this.presetWrite, var18, var19, var8.output, var8.current);
      boolean var21 = handle(this.windowConvert, this.presetWrite, var18 + var8.output - 12.0F, var19 + var8.current - 12.0F, 12.0F, 12.0F);
      if (var10 && (var20 || var21)) {
         this.clientRefresh = true;
      }

      var8.animationDraw.compute(!var10 || !var20 && !var21 ? 0.0F : 1.0F);
      if (var10 && this.sourceSchedule && var20) {
         var8.config = !var8.config;
         this.requestAdapt = false;
         this.sourceSchedule = false;
      }

      if (!this.scaleSave) {
         var8.config = false;
      }

      var8.pointEncode.compute(var8.config ? 1.0F : 0.0F);
      if (var10 && this.profileInvoke && (var20 || var21)) {
         this.handle(var6, var8.renderer.compute(), var8.handler.compute(), 1.0F, 1.0F, false);
         var7 = this.providerFetch.get(var6);
         var13 = false;
         var14 = 1.0F;
         var15 = this.compute(var6, var4, var5, var14);
         var8.output = var15.handle();
         var8.current = var15.process();
         var16 = this.handle(var7, var2, var8.output, var9);
         var17 = this.process(var7, var3, var8.current, var9);
         if (var1.equals(this.textureRun)) {
            this.textureRun = null;
         }

         var8.context = false;
         this.profileInvoke = false;
      }

      if (!var8.instance) {
         var8.renderer.process(var16);
         var8.handler.process(var17);
         var8.instance = true;
      }

      if (!this.animationSchedule || !var10) {
         var8.data = false;
         var8.context = false;
      } else if (!var8.data && !var8.context && (this.textureRun == null || this.textureRun.equals(var1))) {
         if (var21) {
            var8.context = true;
            this.textureRun = var1;
            var8.state = var8.output - this.windowConvert;
            var8.cache = var8.current - this.presetWrite;
         } else if (var20) {
            var8.data = true;
            this.textureRun = var1;
            var8.state = this.windowConvert - var18;
            var8.cache = this.presetWrite - var19;
         }
      }

      boolean var22 = var8.data && var1.equals(this.textureRun);
      boolean var23 = var8.context && var1.equals(this.textureRun);
      if (var22 || var23) {
         this.requestAdapt = false;
      }

      boolean var10000;
      label187: {
         label186: {
            if (this.providerClose != null && this.providerClose.getWindow() != null) {
               if (GLFW.glfwGetKey(this.providerClose.getWindow().getHandle(), 341) == 1) {
                  break label186;
               }

               if (GLFW.glfwGetKey(this.providerClose.getWindow().getHandle(), 345) == 1) {
                  break label186;
               }
            }

            var10000 = false;
            break label187;
         }

         var10000 = true;
      }

      boolean var24 = var10000;
      if (var22) {
         var16 = this.handle(this.windowConvert - var8.state, var8.output, var9);
         var17 = this.process(this.presetWrite - var8.cache, var8.current, var9);
         if (var24) {
            var16 = Math.round(var16 / 10.0F) * 10.0F;
            var17 = Math.round(var17 / 10.0F) * 10.0F;
         }

         float var35 = this.process(var6, var16, var8.output);
         float var38 = this.compute(var6, var17, var8.current);
         var16 = this.handle(var35, var8.output, var9);
         var17 = this.process(var38, var8.current, var9);
         this.handle(var6, var16, var17, var14, var14, var13);
      } else if (var23) {
         float var25 = Math.max(1.0F, this.windowConvert + var8.state);
         float var26 = Math.max(1.0F, this.presetWrite + var8.cache);
         float var27 = var25 / Math.max(1.0F, var4);
         float var28 = var26 / Math.max(1.0F, var5);
         float var29 = (var27 + var28) * 0.5F;
         if (var24) {
            var29 = Math.round(var29 * 20.0F) / 20.0F;
         }

         ThemeRenderer.AnimationState var30 = this.compute(var6, var4, var5, var29);
         var8.output = var30.handle();
         var8.current = var30.process();
         var14 = var30.compute();
         var16 = this.handle(var16, var8.output, var9);
         var17 = this.process(var17, var8.current, var9);
         this.handle(var6, var16, var17, var14, var14, true);
      } else if (var13 && var7 != null && (Math.abs(var7.scaleX() - var14) > 0.001F || Math.abs(var7.scaleY() - var14) > 0.001F)) {
         this.handle(var6, var16, var17, var14, var14, Math.abs(var14 - 1.0F) > 0.01F);
      }

      var16 = this.handle(var16, var8.output, var9);
      var17 = this.process(var17, var8.current, var9);
      var8.renderer.compute(var16);
      var8.handler.compute(var17);
      float var40 = var8.renderer.handle();
      float var41 = var8.handler.handle();
      float var42 = !var22 && !var23 ? 1.0F : 0.65F;
      var8.enabled.compute(var42);
      float var43 = var8.enabled.handle();
      boolean var44 = false;
      if (var43 < 0.99F) {
         this.moduleCollect.update(var43);
         var44 = true;
      }

      boolean var45 = var10 && handle(this.windowConvert, this.presetWrite, var40 + var8.output - 12.0F, var41 + var8.current - 12.0F, 12.0F, 12.0F);
      return var8.selection
         .handle(
            var1, var40, var41, var8.output, var8.current, var22 || var23, var45, var44, var8.config, var8.animationDraw.handle(), var8.pointEncode.handle()
         );
   }

   public ThemeRenderer.PrimaryCacheEntry process(String var1, float var2, float var3, float var4, float var5) {
      if (!this.presetSave) {
         throw new IllegalStateException("beginFrame must be called first");
      }

      String var6 = var1.trim();
      ThemeRenderer.DataRecord var7 = this.providerFetch.get(var6);
      ThemeRenderer.PrimaryAnimationState var8 = this.responseCompute.computeIfAbsent(var1, var0 -> new ThemeRenderer.PrimaryAnimationState());
      var8.active = handle(var4);
      var8.mode = handle(var5);
      ThemeRenderer.Bounds var9 = this.compute(var6);
      boolean var10 = this.colorMeasure && this.scaleSave;
      Float var11 = var7 == null ? this.profileDraw.remove(var6) : null;
      boolean var12 = var11 != null;
      boolean var13 = var7 != null && var7.userResized() || var12;
      float var14 = var12 ? var11 : this.handle(var7, var13);
      ThemeRenderer.AnimationState var15 = this.compute(var6, var4, var5, var14);
      var14 = var15.compute();
      if (!var8.context) {
         var8.output = var15.handle();
         var8.current = var15.process();
      }

      float var16 = this.handle(var7, var2, var8.output, var9);
      float var17 = this.process(var7, var3, var8.current, var9);
      if (var12) {
         this.handle(var6, var16, var17, var14, var14, true);
         var7 = this.providerFetch.get(var6);
      }

      float var18 = process(var2, var16);
      float var19 = process(var3, var17);
      boolean var20 = handle(this.windowConvert, this.presetWrite, var18, var19, var8.output, var8.current);
      boolean var21 = handle(this.windowConvert, this.presetWrite, var18 + var8.output - 12.0F, var19 + var8.current - 12.0F, 12.0F, 12.0F);
      if (var10 && (var20 || var21)) {
         this.clientRefresh = true;
      }

      var8.animationDraw.compute(!var10 || !var20 && !var21 ? 0.0F : 1.0F);
      if (var10 && this.sourceSchedule && var20) {
         var8.config = !var8.config;
         this.requestAdapt = false;
         this.sourceSchedule = false;
      }

      if (!this.scaleSave) {
         var8.config = false;
      }

      var8.pointEncode.compute(var8.config ? 1.0F : 0.0F);
      var8.data = false;
      if (var10 && this.profileInvoke && (var20 || var21)) {
         this.handle(var6, var16, var17, 1.0F, 1.0F, false);
         var7 = this.providerFetch.get(var6);
         var13 = false;
         var14 = 1.0F;
         var15 = this.compute(var6, var4, var5, var14);
         var8.output = var15.handle();
         var8.current = var15.process();
         if (var1.equals(this.textureRun)) {
            this.textureRun = null;
         }

         var8.context = false;
         this.profileInvoke = false;
      }

      if (!this.animationSchedule || !var10) {
         var8.context = false;
      } else if (!var8.context && (this.textureRun == null || this.textureRun.equals(var1)) && var21) {
         var8.context = true;
         this.textureRun = var1;
         var8.state = var8.output - this.windowConvert;
         var8.cache = var8.current - this.presetWrite;
      }

      boolean var22 = var8.context && var1.equals(this.textureRun);
      if (var22) {
         this.requestAdapt = false;
         float var23 = Math.max(1.0F, this.windowConvert + var8.state);
         float var24 = Math.max(1.0F, this.presetWrite + var8.cache);
         float var25 = var23 / Math.max(1.0F, var4);
         float var26 = var24 / Math.max(1.0F, var5);
         float var27 = (var25 + var26) * 0.5F;
         if (this.providerClose != null
            && this.providerClose.getWindow() != null
            && (GLFW.glfwGetKey(this.providerClose.getWindow().getHandle(), 341) == 1 || GLFW.glfwGetKey(this.providerClose.getWindow().getHandle(), 345) == 1)
            )
          {
            var27 = Math.round(var27 * 20.0F) / 20.0F;
         }

         ThemeRenderer.AnimationState var28 = this.compute(var6, var4, var5, var27);
         var8.output = var28.handle();
         var8.current = var28.process();
         var14 = var28.compute();
         this.handle(var6, var16, var17, var14, var14, true);
      } else if (var13 && var7 != null && (Math.abs(var7.scaleX() - var14) > 0.001F || Math.abs(var7.scaleY() - var14) > 0.001F)) {
         this.handle(var6, var16, var17, var14, var14, Math.abs(var14 - 1.0F) > 0.01F);
      }

      float var32 = var22 ? 0.65F : 1.0F;
      var8.enabled.compute(var32);
      float var33 = var8.enabled.handle();
      boolean var34 = false;
      if (var33 < 0.99F) {
         this.moduleCollect.update(var33);
         var34 = true;
      }

      boolean var35 = var10 && handle(this.windowConvert, this.presetWrite, var18 + var8.output - 12.0F, var19 + var8.current - 12.0F, 12.0F, 12.0F);
      return var8.selection
         .handle(var1, var18, var19, var8.output, var8.current, var22, var35, var34, var8.config, var8.animationDraw.handle(), var8.pointEncode.handle());
   }

   public ThemeRenderer.PrimaryCacheEntry handle(ThemeRenderer.PrimaryCacheEntry var1, float var2, float var3, float var4, float var5) {
      if (var1 != null && var1.instance != null && !(var2 <= 0.0F) && !(var3 <= 0.0F) && Float.isFinite(var2) && Float.isFinite(var3)) {
         ThemeRenderer.PrimaryAnimationState var6 = this.responseCompute.get(var1.instance);
         if (var6 == null) {
            return var1;
         }

         ThemeRenderer.Bounds var7 = this.compute(var1.instance.trim());
         float var8 = (float)Math.sqrt(Math.max(1.0E-4F, var2 / Math.max(1.0F, var4) * (var3 / Math.max(1.0F, var5))));
         ThemeRenderer.AnimationState var9 = this.compute(var1.instance, var4, var5, var8);
         float var10 = var9.handle();
         float var11 = var9.process();
         float var12 = this.handle(var1.data, var10, var7);
         float var13 = this.process(var1.context, var11, var7);
         var6.output = var10;
         var6.current = var11;
         String var14 = var1.instance.trim();
         float var15 = this.handle(Float.isFinite(var6.renderer.compute()) ? var6.renderer.compute() : var12, var10, var7);
         float var16 = this.process(Float.isFinite(var6.handler.compute()) ? var6.handler.compute() : var13, var11, var7);
         var6.renderer.compute(var15);
         var6.handler.compute(var16);
         boolean var17 = Math.abs(var9.compute() - 1.0F) > 0.01F;
         this.handle(var14, var15, var16, var9.compute(), var9.compute(), var17);
         boolean var18 = this.scaleSave
            && this.colorMeasure
            && handle(this.windowConvert, this.presetWrite, var12 + var10 - 12.0F, var13 + var11 - 12.0F, 12.0F, 12.0F);
         return var1.handle(var1.instance, var12, var13, var10, var11, var1.current, var18, var1.mode, var1.selection, var1.cache, var1.output);
      } else {
         return var1;
      }
   }

   public void handle(ThemeRenderer.PrimaryCacheEntry var1) {
      this.process(
         var1, var1 == null ? 0.0F : var1.data, var1 == null ? 0.0F : var1.context, var1 == null ? 0.0F : var1.config, var1 == null ? 0.0F : var1.state
      );
   }

   public void process(ThemeRenderer.PrimaryCacheEntry var1, float var2, float var3, float var4, float var5) {
      if (var1 != null && this.moduleCollect != null) {
         this.compute(var1, var2, var3, var4, var5);
         if (this.scaleSave && var1.instance != null) {
            float var6 = var4;
            float var7 = var5;
            float var8 = var2;
            float var9 = var3;
            float var10 = Math.max(var1.cache, var1.current ? 1.0F : 0.0F);
            if (var10 > 0.01F) {
               float var11 = Math.max(5.0F, Math.min(12.0F, Math.min(var6, var7) * 0.16F));
               int var12 = PackedColor.compute(255, 255, 255, (int)((var1.current ? 62 : 34) * var10));
               int var13 = PackedColor.compute(125, 210, 255, (int)((var1.current ? 28 : 14) * var10));
               this.moduleCollect.handle(var8, var9, var6, var7, var11, var1.current ? 7.0F : 4.0F, 0.8F, var13);
               this.moduleCollect.handle(var8, var9, var6, var7, var11, var12, var1.current ? 1.25F : 1.0F);
            }

            int var14 = !var1.active && !var1.current ? 721420287 : -2130706433;
            float var15 = var8 + var6 - 6.0F;
            float var16 = var9 + var7 - 6.0F;
            this.moduleCollect.handle(var15, var16, 2.5F, 2.5F, 1.0F, var14);
            this.moduleCollect.handle(var15 - 4.5F, var16, 2.5F, 2.5F, 1.0F, var14);
            this.moduleCollect.handle(var15, var16 - 4.5F, 2.5F, 2.5F, 1.0F, var14);
            if (var1.cache > 0.01F && !var1.current) {
               this.handle(this.moduleCollect, var8, var9, var6, var1.cache);
            }

            if (var1.current) {
               this.handle(this.moduleCollect);
            }
         }

         if (var1.mode) {
            this.moduleCollect.onTick();
         }
      }
   }

   private void process(ThemeRenderer.PrimaryCacheEntry var1) {
      this.compute(
         var1, var1 == null ? 0.0F : var1.data, var1 == null ? 0.0F : var1.context, var1 == null ? 0.0F : var1.config, var1 == null ? 0.0F : var1.state
      );
   }

   private void compute(ThemeRenderer.PrimaryCacheEntry var1, float var2, float var3, float var4, float var5) {
      if (var1 != null && var1.instance != null && Float.isFinite(var2) && Float.isFinite(var3) && !(var4 <= 0.0F) && !(var5 <= 0.0F)) {
         int var6 = this.keyProcess;
         if (var6 >= 32) {
            if (!var1.current) {
               return;
            }

            var6 = 31;
         } else {
            this.keyProcess++;
         }

         float var7 = var2 + var4 * 0.5F;
         float var8 = var3 + var5 * 0.5F;
         float var9 = (float)Math.sqrt(var4 * var4 + var5 * var5) * 0.5F;
         float var10 = var1.current ? 1.0F : 0.34F + Math.min(0.24F, var1.cache * 0.24F);
         this.eventAttach[var6].handle(var1.instance, var7, var8, Math.max(34.0F, var9), var10, var4, var5);
      }
   }

   private void refresh() {
      this.contextExpand = this.keyProcess;

      for (int var1 = 0; var1 < this.contextExpand; var1++) {
         this.vectorPerform[var1].handle(this.eventAttach[var1]);
      }

      this.keyProcess = 0;
   }

   private ThemeColors render() {
      ThemePalette var1 = ThemePalette.WILD;
      if (WildClient.instance != null && WildClient.instance.selection != null) {
         var1 = WildClient.instance.selection.process();
      }

      boolean var2 = var1 == ThemePalette.VERNAL_SOLSTICE
         || var1 == ThemePalette.SAKURA_BREEZE
         || var1 == ThemePalette.PORCELAIN_DAWN
         || var1 == ThemePalette.FRUTIGER_AERO;
      if (this.animationExpand == null || this.actionConvert != var1 || this.screenRead != var2 || var1 == ThemePalette.CUSTOM) {
         this.animationExpand = ThemeColors.handle(var1, var2);
         this.actionConvert = var1;
         this.screenRead = var2;
      }

      return this.animationExpand;
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5) {
      String var6 = "ЛКМ - Для перемещения";
      String var7 = "ПКМ - Для настроек";
      float var8 = 25.0F;
      float var9 = RoundedRectRenderer.handle(FontRegistry.instance, var6, var8).instance;
      float var10 = RoundedRectRenderer.handle(FontRegistry.instance, var7, var8).instance;
      float var11 = var8 * 2.0F + 4.0F;
      float var12 = var2 + var4 / 2.0F;
      float var13 = var3 - var11 - 8.0F;
      int var14 = (int)(255.0F * var5);
      if (var14 > 5) {
         int var15 = RoundedRectRenderer.ColorState.compute(255, 255, 255, var14);
         var1.handle(FontRegistry.instance, var12 - var9 / 2.0F, var13 + 6.0F, var8, var6, var15);
         var1.handle(FontRegistry.instance, var12 - var10 / 2.0F, var13 + 6.0F + var8 + 2.0F, var8, var7, var15);
      }
   }

   private float handle(ThemeRenderer.DataRecord var1, float var2, float var3, ThemeRenderer.Bounds var4) {
      return var1 != null && this.colorCompute > 0 ? this.handle(var1.nx() * this.colorCompute, var3, var4) : this.handle(var2, var3, var4);
   }

   private float process(ThemeRenderer.DataRecord var1, float var2, float var3, ThemeRenderer.Bounds var4) {
      return var1 != null && this.scaleAdapt > 0 ? this.process(var1.ny() * this.scaleAdapt, var3, var4) : this.process(var2, var3, var4);
   }

   private void handle(String var1, float var2, float var3, float var4, float var5, boolean var6) {
      if (this.colorCompute > 0 && this.scaleAdapt > 0 && var1 != null) {
         float var7 = var2 / this.colorCompute;
         float var8 = var3 / this.scaleAdapt;
         ThemeRenderer.DataRecord var9 = this.providerFetch.get(var1);
         if (!this.handle(var9, var7, var8, var4, var5, var6)) {
            this.providerFetch.put(var1, new ThemeRenderer.DataRecord(var7, var8, var4, var5, var6));
            this.keyFilter = true;
         }
      }
   }

   private boolean handle(ThemeRenderer.DataRecord var1, float var2, float var3, float var4, float var5, boolean var6) {
      return var1 == null
         ? false
         : Math.abs(var1.nx() - var2) < 1.0E-5F
            && Math.abs(var1.ny() - var3) < 1.0E-5F
            && Math.abs(var1.scaleX() - var4) < 1.0E-4F
            && Math.abs(var1.scaleY() - var5) < 1.0E-4F
            && var1.userResized() == var6;
   }

   private float process(String var1, float var2, float var3) {
      this.indexBind = Float.NaN;
      this.tick();
      this.handle(var2, 0.0F, 0.0F);
      this.handle(var2, this.colorCompute - var3, this.colorCompute);
      this.handle(var2, this.colorCompute * 0.5F - var3 * 0.5F, this.colorCompute * 0.5F);

      for (Entry var5 : this.responseCompute.entrySet()) {
         if (!((String)var5.getKey()).equals(var1)) {
            ThemeRenderer.PrimaryAnimationState var6 = (ThemeRenderer.PrimaryAnimationState)var5.getValue();
            if (var6.instance && !(var6.output <= 0.0F) && !(var6.current <= 0.0F)) {
               float var7 = var6.renderer.compute();
               float var8 = var6.output;
               this.handle(var2, var7, var7);
               this.handle(var2, var7 + var8, var7 + var8);
               this.handle(var2, var7 - var3, var7);
               this.handle(var2, var7 + var8 - var3, var7 + var8);
               this.handle(var2, var7 + var8 * 0.5F - var3 * 0.5F, var7 + var8 * 0.5F);
            }
         }
      }

      if (Float.isFinite(this.dataValidate)) {
         this.indexBind = this.dataValidate;
         return this.configCollapse;
      } else {
         return var2;
      }
   }

   private float compute(String var1, float var2, float var3) {
      this.actionRead = Float.NaN;
      this.tick();
      this.handle(var2, 0.0F, 0.0F);
      this.handle(var2, this.scaleAdapt - var3, this.scaleAdapt);
      this.handle(var2, this.scaleAdapt * 0.5F - var3 * 0.5F, this.scaleAdapt * 0.5F);

      for (Entry var5 : this.responseCompute.entrySet()) {
         if (!((String)var5.getKey()).equals(var1)) {
            ThemeRenderer.PrimaryAnimationState var6 = (ThemeRenderer.PrimaryAnimationState)var5.getValue();
            if (var6.instance && !(var6.output <= 0.0F) && !(var6.current <= 0.0F)) {
               float var7 = var6.handler.compute();
               float var8 = var6.current;
               this.handle(var2, var7, var7);
               this.handle(var2, var7 + var8, var7 + var8);
               this.handle(var2, var7 - var3, var7);
               this.handle(var2, var7 + var8 - var3, var7 + var8);
               this.handle(var2, var7 + var8 * 0.5F - var3 * 0.5F, var7 + var8 * 0.5F);
            }
         }
      }

      if (Float.isFinite(this.dataValidate)) {
         this.actionRead = this.dataValidate;
         return this.configCollapse;
      } else {
         return var2;
      }
   }

   private void tick() {
      this.configCollapse = 0.0F;
      this.dataValidate = Float.NaN;
      this.scaleRender = Float.MAX_VALUE;
   }

   private void handle(float var1, float var2, float var3) {
      float var4 = Math.abs(var1 - var2);
      if (!(var4 > 5.0F) && !(var4 >= this.scaleRender)) {
         this.configCollapse = var2;
         this.dataValidate = var3;
         this.scaleRender = var4;
      }
   }

   private void handle(RoundedRectRenderer var1) {
      int var2 = PackedColor.compute(125, 210, 255, 118);
      int var3 = PackedColor.compute(125, 210, 255, 32);
      if (Float.isFinite(this.indexBind)) {
         var1.handle(this.indexBind - 0.75F, 0.0F, 1.5F, this.scaleAdapt, 1.0F, 9.0F, 2.0F, var3);
         var1.handle(this.indexBind - 0.5F, 0.0F, 1.0F, this.scaleAdapt, 0.5F, var2);
      }

      if (Float.isFinite(this.actionRead)) {
         var1.handle(0.0F, this.actionRead - 0.75F, this.colorCompute, 1.5F, 1.0F, 9.0F, 2.0F, var3);
         var1.handle(0.0F, this.actionRead - 0.5F, this.colorCompute, 1.0F, 0.5F, var2);
      }
   }

   private ThemeRenderer.Bounds compute(String var1) {
      if (handle(var1, "hotbar")) {
         return renderer;
      } else if (handle(var1, "watermark")) {
         return handler;
      } else if (handle(var1, "targethud")) {
         return animationDraw;
      } else if (handle(var1, "info")) {
         return pointEncode;
      } else if (handle(var1, "inventory")) {
         return animator;
      } else if (handle(var1, "autobuy")) {
         return source;
      } else if (handle(var1, "music")) {
         return target;
      } else if (handle(var1, "arraylist")) {
         return pending;
      } else if (handle(var1, "notifications")) {
         return previous;
      } else if (handle(var1, "potions") || handle(var1, "cooldowns")) {
         return latest;
      } else if (handle(var1, "armor") || handle(var1, "aistatus")) {
         return summary;
      } else {
         return !handle(var1, "staff") && !handle(var1, "party") && !handle(var1, "serverhelper") && !handle(var1, "hotkeys") ? vectorMatch : matrixBlend;
      }
   }

   private static boolean handle(String var0, String var1) {
      if (var0 != null && var1 != null && var1.length() <= var0.length()) {
         int var2 = var0.length() - var1.length();

         for (int var3 = 0; var3 <= var2; var3++) {
            if (var0.regionMatches(true, var3, var1, 0, var1.length())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public float handle(String var1, float var2) {
      return this.handle(var1, var2, Float.NaN, Float.NaN);
   }

   public float handle(String var1, float var2, float var3, float var4) {
      String var5 = var1 == null ? "" : var1.trim();
      ThemeRenderer.PrimaryAnimationState var6 = this.responseCompute.get(var5);
      if (var6 == null && var1 != null) {
         var6 = this.responseCompute.get(var1);
      }

      if (var6 != null && var6.active > 0.0F && var6.mode > 0.0F) {
         return this.process(var5, var6.active, var6.mode, var2);
      }

      if (Float.isFinite(var3) && var3 > 0.0F && Float.isFinite(var4) && var4 > 0.0F) {
         return this.process(var5, var3, var4, var2);
      }

      ThemeRenderer.Bounds var7 = this.compute(var1);
      return process(process(var2, 1.0F), var7.minScale(), var7.maxScale());
   }

   public float handle(String var1) {
      return this.handle(var1, Float.NaN, Float.NaN);
   }

   public float handle(String var1, float var2, float var3) {
      if (var1 != null && !var1.isBlank()) {
         ThemeRenderer.DataRecord var4 = this.providerFetch.get(var1);
         Float var5 = var4 == null ? this.profileDraw.get(var1) : null;
         float var6 = var5 == null ? this.handle(var4, var4 != null && var4.userResized()) : var5;
         return this.handle(var1, var6, var2, var3);
      } else {
         return 1.0F;
      }
   }

   public ThemeRenderer.DataRecord handle(String var1, float var2, float var3, float var4, float var5, float var6) {
      if (var1 != null && !var1.isBlank() && this.colorCompute > 0 && this.scaleAdapt > 0) {
         String var7 = var1.trim();
         ThemeRenderer.PrimaryAnimationState var8 = this.responseCompute.get(var7);
         if (var8 == null) {
            var8 = this.responseCompute.get(var1);
         }

         float var9;
         label70: {
            var9 = this.handle(var7, var2, var5, var6);
            ThemeRenderer.DataRecord var10 = this.providerFetch.get(var7);
            if (var10 == null) {
               if (var8 == null) {
                  break label70;
               }

               if (!var8.instance) {
                  break label70;
               }
            }

            this.profileDraw.remove(var7);
            float var11;
            float var12;
            if (var10 != null) {
               var11 = var10.nx() * this.colorCompute;
               var12 = var10.ny() * this.scaleAdapt;
            } else if (var8 != null && var8.instance) {
               var11 = var8.renderer.compute();
               var12 = var8.handler.compute();
            } else {
               var11 = process(process(var3, 0.5F), 0.0F, 1.0F) * this.colorCompute;
               var12 = process(process(var4, 0.5F), 0.0F, 1.0F) * this.scaleAdapt;
            }

            float var13 = var8 != null && !(var8.active <= 0.0F) ? var8.active : handle(var5);
            float var14 = var8 != null && !(var8.mode <= 0.0F) ? var8.mode : handle(var6);
            ThemeRenderer.AnimationState var15 = this.compute(var7, var13, var14, var9);
            ThemeRenderer.Bounds var16 = this.compute(var7);
            var11 = this.handle(var11, var15.handle(), var16);
            var12 = this.process(var12, var15.process(), var16);
            this.handle(var7, var11, var12, var15.compute(), var15.compute(), true);
            if (var8 != null) {
               var8.output = var15.handle();
               var8.current = var15.process();
               var8.renderer.compute(var11);
               var8.handler.compute(var12);
            }

            return this.providerFetch.get(var7);
         }

         this.profileDraw.put(var7, var9);
         return new ThemeRenderer.DataRecord(process(process(var3, 0.5F), 0.0F, 1.0F), process(process(var4, 0.5F), 0.0F, 1.0F), var9, var9, true);
      } else {
         return null;
      }
   }

   public ThemeRenderer.DataRecord compute(String var1, float var2, float var3, float var4, float var5) {
      if (var1 != null && !var1.isBlank() && this.colorCompute > 0 && this.scaleAdapt > 0) {
         String var6 = var1.trim();
         ThemeRenderer.PrimaryAnimationState var7 = this.responseCompute.get(var6);
         if (var7 == null) {
            var7 = this.responseCompute.get(var1);
         }

         ThemeRenderer.DataRecord var8 = this.providerFetch.get(var6);
         boolean var9 = var8 != null && var8.userResized() || this.profileDraw.containsKey(var6);
         float var10 = this.handle(var6, var4, var5);
         this.profileDraw.remove(var6);
         float var11 = var7 != null && !(var7.active <= 0.0F) ? var7.active : handle(var4);
         float var12 = var7 != null && !(var7.mode <= 0.0F) ? var7.mode : handle(var5);
         ThemeRenderer.AnimationState var13 = this.compute(var6, var11, var12, var10);
         ThemeRenderer.Bounds var14 = this.compute(var6);
         float var15 = var14.padding();
         float var16 = var14.padding();
         float var17 = Math.max(var15, this.colorCompute - var13.handle() - var14.padding());
         float var18 = Math.max(var16, this.scaleAdapt - var13.process() - var14.padding());
         float var19 = var15 + (var17 - var15) * process(process(var2, 0.5F), 0.0F, 1.0F);
         float var20 = var16 + (var18 - var16) * process(process(var3, 0.5F), 0.0F, 1.0F);
         this.handle(var6, var19, var20, var13.compute(), var13.compute(), var9 || Math.abs(var13.compute() - 1.0F) > 0.01F);
         if (var7 != null) {
            var7.output = var13.handle();
            var7.current = var13.process();
            var7.renderer.compute(var19);
            var7.handler.compute(var20);
         }

         return this.providerFetch.get(var6);
      } else {
         return null;
      }
   }

   private float process(String var1, float var2, float var3, float var4) {
      ThemeRenderer.Bounds var5 = this.compute(var1);
      float var6 = handle(var2);
      float var7 = handle(var3);
      float var8 = this.colorCompute > 1 ? Math.max(1.0F, this.colorCompute * var5.maxWidthRatio() - var5.padding() * 2.0F) : var6 * var5.maxScale();
      float var9 = this.scaleAdapt > 1 ? Math.max(1.0F, this.scaleAdapt * var5.maxHeightRatio() - var5.padding() * 2.0F) : var7 * var5.maxScale();
      float var10 = Math.min(var5.maxScale(), Math.min(var8 / var6, var9 / var7));
      var10 = Math.max(0.08F, var10);
      float var11 = Math.min(var5.minScale(), var10);
      return process(process(var4, 1.0F), var11, var10);
   }

   private ThemeRenderer.AnimationState compute(String var1, float var2, float var3, float var4) {
      float var5 = handle(var2);
      float var6 = handle(var3);
      float var7 = this.process(var1, var5, var6, var4);
      return this.frameCheck.handle(var5 * var7, var6 * var7, var7);
   }

   private float handle(ThemeRenderer.DataRecord var1, boolean var2) {
      if (var2 && var1 != null) {
         float var3 = process(var1.scaleX(), 1.0F);
         float var4 = process(var1.scaleY(), 1.0F);
         return !(var3 <= 0.0F) && !(var4 <= 0.0F) && !(var3 > 12.0F) && !(var4 > 12.0F) ? (float)Math.sqrt(Math.max(1.0E-4F, var3 * var4)) : 1.0F;
      } else {
         return 1.0F;
      }
   }

   private float handle(float var1, float var2, ThemeRenderer.Bounds var3) {
      if (this.colorCompute <= 0) {
         return process(var1, 0.0F);
      }

      float var4 = Math.max(0.0F, var3 == null ? 6.0F : var3.padding());
      float var5 = Math.min(var4, Math.max(0.0F, this.colorCompute - 1.0F));
      float var6 = Math.max(var5, this.colorCompute - Math.max(1.0F, var2) - var4);
      return process(process(var1, var5), var5, var6);
   }

   private float process(float var1, float var2, ThemeRenderer.Bounds var3) {
      if (this.scaleAdapt <= 0) {
         return process(var1, 0.0F);
      }

      float var4 = Math.max(0.0F, var3 == null ? 6.0F : var3.padding());
      float var5 = Math.min(var4, Math.max(0.0F, this.scaleAdapt - 1.0F));
      float var6 = Math.max(var5, this.scaleAdapt - Math.max(1.0F, var2) - var4);
      return process(process(var1, var5), var5, var6);
   }

   private static boolean handle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return Float.isFinite(var0)
         && Float.isFinite(var1)
         && Float.isFinite(var2)
         && Float.isFinite(var3)
         && Float.isFinite(var4)
         && Float.isFinite(var5)
         && var4 > 0.0F
         && var5 > 0.0F
         && var0 >= var2
         && var0 <= var2 + var4
         && var1 >= var3
         && var1 <= var3 + var5;
   }

   private static float handle(float var0, float var1) {
      return Math.max(0.0F, Math.min(var0, var1));
   }

   private static float process(float var0, float var1, float var2) {
      return var2 < var1 ? var1 : Math.max(var1, Math.min(var0, var2));
   }

   private static float handle(float var0) {
      return Float.isFinite(var0) && var0 > 1.0F ? var0 : 1.0F;
   }

   private static float process(float var0, float var1) {
      return Float.isFinite(var0) ? var0 : var1;
   }

   public Map<String, ThemeRenderer.DataRecord> update() {
      return this.providerFetch;
   }

   public Map<String, Float> apply() {
      return this.profileDraw;
   }

   public void handle(Map<String, ThemeRenderer.DataRecord> var1) {
      this.providerFetch.clear();
      this.profileDraw.clear();
      if (var1 != null) {
         this.providerFetch.putAll(var1);
      }

      this.responseCompute.clear();
      this.textureRun = null;
      this.keyFilter = false;
   }

   public void process(Map<String, Float> var1) {
      this.profileDraw.clear();
      if (var1 != null) {
         for (Entry var3 : var1.entrySet()) {
            String var4 = (String)var3.getKey();
            Float var5 = (Float)var3.getValue();
            if (var4 != null && !var4.isBlank() && var5 != null && Float.isFinite(var5) && !(var5 <= 0.0F) && !this.providerFetch.containsKey(var4)) {
               this.profileDraw.put(var4.trim(), this.handle(var4, var5));
            }
         }
      }
   }

   public void process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim();
         this.providerFetch.remove(var2);
         this.profileDraw.remove(var2);
         ThemeRenderer.PrimaryAnimationState var3 = this.responseCompute.remove(var2);
         if (var3 == null) {
            var3 = this.responseCompute.remove(var1);
         }

         if (var2.equals(this.textureRun) || var1.equals(this.textureRun)) {
            this.textureRun = null;
         }
      }
   }
   public float execute() {
      return this.windowConvert;
   }
   public float prepare() {
      return this.presetWrite;
   }
   public boolean check() {
      return this.animationSchedule;
   }
   public boolean onTick() {
      return this.rendererScan;
   }
   public String select() {
      return this.textureRun;
   }

   static final class AnimationState {
      private float instance;
      private float data;
      private float context;

      ThemeRenderer.AnimationState handle(float var1, float var2, float var3) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         return this;
      }

      float handle() {
         return this.instance;
      }

      float process() {
         return this.data;
      }

      float compute() {
         return this.context;
      }
   }

   record Bounds(float minScale, float maxScale, float maxWidthRatio, float maxHeightRatio, float padding) {
   }

   public static final class CacheEntry {
      public String instance = "";
      public float data;
      public float context;
      public float config;
      public float state;
      public float cache;
      public float output;

      public void handle(String var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         this.instance = var1 == null ? "" : var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
      }

      void handle(ThemeRenderer.CacheEntry var1) {
         if (var1 == null) {
            this.handle("", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
         } else {
            this.handle(var1.instance, var1.data, var1.context, var1.config, var1.state, var1.cache, var1.output);
         }
      }
   }

   public record DataRecord(float nx, float ny, float scaleX, float scaleY, boolean userResized) {
   }

   static final class PrimaryAnimationState {
      boolean instance;
      boolean data;
      boolean context;
      boolean config;
      float state;
      float cache;
      float output;
      float current;
      float active;
      float mode;
      final ThemeRenderer.PrimaryCacheEntry selection = new ThemeRenderer.PrimaryCacheEntry();
      final SpringFloat enabled = new SpringFloat(AnimationClock.handle(), ThemeRenderer.state, 1.0F, 0.0F, 1.0F, 0.001F, 0.001F);
      final SpringFloat renderer = new SpringFloat(AnimationClock.handle(), ThemeRenderer.config, 0.0F, -9999.0F, 9999.0F, 0.1F, 0.1F);
      final SpringFloat handler = new SpringFloat(AnimationClock.handle(), ThemeRenderer.config, 0.0F, -9999.0F, 9999.0F, 0.1F, 0.1F);
      final SpringFloat animationDraw = new SpringFloat(AnimationClock.handle(), ThemeRenderer.cache, 0.0F, 0.0F, 1.0F, 0.01F, 0.01F);
      final SpringFloat pointEncode = new SpringFloat(AnimationClock.handle(), ThemeRenderer.output, 0.0F, 0.0F, 1.0F, 0.01F, 0.01F);
   }

   public static final class PrimaryCacheEntry {
      public String instance;
      public float data;
      public float context;
      public float config;
      public float state;
      public float cache;
      public float output;
      public boolean current;
      public boolean active;
      public boolean mode;
      public boolean selection;

      PrimaryCacheEntry() {
      }

      ThemeRenderer.PrimaryCacheEntry handle(
         String var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7, boolean var8, boolean var9, float var10, float var11
      ) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.current = var6;
         this.active = var7;
         this.mode = var8;
         this.selection = var9;
         this.cache = var10;
         this.output = var11;
         return this;
      }
   }

   @HudElementMetadata(handle = "GlobalHUD", process = "w")
   public static class SecondaryCacheEntry extends ResettableSettingGroup {
      public final NumberSetting instance = new NumberSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F, true);
      public final NumberSetting data = new NumberSetting("Прозрачность тёмных элементов", 1.0F, 0.0F, 1.0F, 0.05F, true);
      public final ModeSetting context = new ModeSetting("Стилистика", "Тёмный", "Тёмный", "Светлый", "Блюр", "Неоморфизм", "Феррофлюид", "Призма");
      public final NumberSetting config = new NumberSetting("Нео дистанция", 5.5F, 2.0F, 18.0F, 0.5F, false)
         .handle(() -> !ThemePresets.handle(this.context.compute()));
      public final NumberSetting state = new NumberSetting("Нео размытие", 18.0F, 6.0F, 48.0F, 1.0F, false)
         .handle(() -> !ThemePresets.handle(this.context.compute()));
      public final NumberSetting cache = new NumberSetting("Нео интенсивность", 0.72F, 0.1F, 1.0F, 0.05F, true)
         .handle(() -> !ThemePresets.handle(this.context.compute()));
      public final ModeSetting output = new ModeSetting("Нео форма", "Выпуклая", "Плоская", "Выпуклая", "Вогнутая")
         .handle(() -> !ThemePresets.handle(this.context.compute()));
      public final ChoiceSetting current = new ChoiceSetting(
         "Визуал",
         new BooleanSetting("Тень", true),
         new BooleanSetting("Обводка", true),
         new BooleanSetting("Темные зоны", true),
         new BooleanSetting("Верхняя накладка", true),
         new BooleanSetting("Нижняя накладка", true),
         new BooleanSetting("Тёмный рект поверх", true)
      );

      public SecondaryCacheEntry() {
         this.handle(this.instance);
         this.handle(this.data);
         this.handle(this.context);
         this.handle(this.config);
         this.handle(this.state);
         this.handle(this.cache);
         this.handle(this.output);
         this.handle(this.current);
         HudProfileConfig.handle(this);
      }
   }
}
