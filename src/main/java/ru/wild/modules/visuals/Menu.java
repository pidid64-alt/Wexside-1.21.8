package ru.wild.modules.visuals;

import net.minecraft.client.MinecraftClient;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.gui.screen.GuiLayoutSpec;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.SensitivityPresets;
import ru.wild.gui.theme.ThemePalette;

@ModuleRegister(name = "Menu", description = "Настройки клиента", category = ModuleCategory.Visuals)
public class Menu extends Module {
   public static Menu source;
   private static final float worldSend = 0.86F;
   private static final float targetWrite = 0.86F;
   public static final String target = "Графика";
   public static final String pending = "Эффекты";
   public static final String previous = "Темы";
   public static final String latest = "Производительность";
   public static final ModeSetting summary = new ModeSetting("Категория", "Графика", "Графика", "Эффекты", "Темы", "Производительность");
   public static final NumberSetting matrixBlend = new NumberSetting("Качество графики", 2.0F, 0.0F, 3.0F, 1.0F, false)
      .handle(SensitivityPresets.process())
      .handle(() -> !summary.process("Графика"));
   public static final BooleanSetting vectorMatch = new BooleanSetting("Применять пресет автоматически", true).handle(() -> !summary.process("Графика"));
   public static final ModeSetting itemProject = new ModeSetting("Стиль анимаций", "Smooth", "Smooth", "Snappy", "Bouncy", "Cinematic", "Linear")
      .handle(() -> !summary.process("Графика"));
   public static final NumberSetting responseCompute = new NumberSetting("Масштаб GUI", 0.86F, 0.55F, 1.7F, 0.01F, false).handle(() -> true);
   public static final NumberSetting providerFetch = new NumberSetting("Масштаб панели темы", 0.86F, 0.55F, 1.7F, 0.01F, false).handle(() -> true);
   public static final BooleanSetting profileDraw = new BooleanSetting("Волны клика", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting vectorPerform = new BooleanSetting("Волны темы", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting eventAttach = new BooleanSetting("Ударная волна темы", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting serverRead = new BooleanSetting("Размытие скролла", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting positionAdvance = new BooleanSetting("Переходы карт", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting frameCheck = new BooleanSetting("Переходы экрана", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting moduleCollect = new BooleanSetting("Дрейф цвета темы", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting providerClose = new BooleanSetting("Внутреннее свечение", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting presetSave = new BooleanSetting("Зерно плёнки", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting windowConvert = new BooleanSetting("Пульсация хотбара", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting presetWrite = new BooleanSetting("Анимации статусов", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting colorMeasure = new BooleanSetting("Вспышка урона", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting animationSchedule = new BooleanSetting("Пульсация регенерации", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting rendererScan = new BooleanSetting("Тряска при низком здоровье", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting sourceBuild = new BooleanSetting("След курсора в меню", true).handle(() -> !summary.process("Эффекты"));
   public static final BooleanSetting outputCollapse = new BooleanSetting("Параллакс главного меню", true).handle(() -> !summary.process("Эффекты"));
   public static final ColorSetting profileInvoke = new ColorSetting("Акцент темы", 66.0F, 0.64F, 1.0F).process(() -> !summary.process("Темы"));
   public static final ColorSetting sourceSchedule = new ColorSetting("Цвет панели", 68.0F, 0.28F, 0.08F).process(() -> !summary.process("Темы"));
   public static final ColorSetting timerRender = new ColorSetting("Цвет поверхности", 68.0F, 0.24F, 0.12F).process(() -> !summary.process("Темы"));
   public static final ColorSetting scaleSave = new ColorSetting("Цвет обводки", 68.0F, 0.32F, 0.38F).process(() -> !summary.process("Темы"));
   public static final ColorSetting colorCompute = new ColorSetting("Цвет текста", 0.0F, 0.0F, 1.0F).process(() -> !summary.process("Темы"));
   public static final ColorSetting scaleAdapt = new ColorSetting("Цвет приглушённого текста", 68.0F, 0.14F, 0.62F).process(() -> !summary.process("Темы"));
   public static final String textureRun = "Стандарт";
   public static final String indexBind = "Голограмма";
   public static final ModeSetting actionRead = new ModeSetting("Фон ClickGUI", "Голограмма", "Стандарт", "Голограмма").handle(() -> !summary.process("Темы"));
   public static final ShaderPresetSetting configCollapse = new ShaderPresetSetting("Foundry Shader", LivePreviewRenderer.BACKGROUND)
      .compute(() -> !summary.process("Темы"));
   public static final NumberSetting dataValidate = new NumberSetting("Максимальный блюр", 32.0F, 8.0F, 64.0F, 1.0F, false)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting scaleRender = new NumberSetting("Иридисцентный отлив", 0.6F, 0.0F, 1.0F, 0.01F, true)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting clientRefresh = new NumberSetting("Притяжение к курсору", 0.18F, 0.0F, 0.4F, 0.01F, false)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting keyFilter = new NumberSetting("Радиус прозрачности у курсора", 0.28F, 0.05F, 0.6F, 0.01F, false)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting requestAdapt = new NumberSetting("Размер островков", 1.8F, 0.8F, 3.5F, 0.05F, false)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting timerMeasure = new NumberSetting("Скорость течения", 0.55F, 0.0F, 1.5F, 0.01F, false)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting vectorEncode = new NumberSetting("Контраст островков", 0.55F, 0.0F, 1.0F, 0.01F, true)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting requestReceive = new NumberSetting("Виньетка", 0.35F, 0.0F, 1.0F, 0.01F, true)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting windowProcess = new NumberSetting("Яркость", 0.55F, 0.0F, 1.0F, 0.01F, true)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final NumberSetting packetSave = new NumberSetting("Насыщенность", 0.45F, 0.0F, 1.0F, 0.01F, true)
      .handle(() -> !summary.process("Темы") || !actionRead.process("Голограмма"));
   public static final BooleanSetting entryAnimate = new BooleanSetting("Упрощённые тени HUD", false).handle(() -> !summary.process("Производительность"));
   public static final BooleanSetting playerCollect = new BooleanSetting("Отключить блюр", false).handle(() -> !summary.process("Производительность"));
   public static final BooleanSetting stateApply = new BooleanSetting("Быстрые анимации", false).handle(() -> !summary.process("Производительность"));
   public static final BooleanSetting matrixFilter = new BooleanSetting("Пропускать частицы клиента", false)
      .handle(() -> !summary.process("Производительность"));
   private static int resultEncode = -1;
   public static final BooleanSetting layerSample = new BooleanSetting("Auto GUI scale initialized", false).handle(() -> true);

   public Menu() {
      source = this;
      this.searchName = "Menu";
      this.keyCode = 344;
      this.handle(
         summary,
         matrixBlend,
         vectorMatch,
         itemProject,
         responseCompute,
         providerFetch,
         layerSample,
         profileDraw,
         vectorPerform,
         eventAttach,
         serverRead,
         positionAdvance,
         frameCheck,
         moduleCollect,
         providerClose,
         presetSave,
         windowConvert,
         presetWrite,
         colorMeasure,
         animationSchedule,
         rendererScan,
         sourceBuild,
         outputCollapse,
         profileInvoke,
         sourceSchedule,
         timerRender,
         scaleSave,
         colorCompute,
         scaleAdapt,
         actionRead,
         configCollapse,
         dataValidate,
         scaleRender,
         clientRefresh,
         keyFilter,
         requestAdapt,
         timerMeasure,
         vectorEncode,
         requestReceive,
         windowProcess,
         packetSave,
         entryAnimate,
         playerCollect,
         stateApply,
         matrixFilter
      );
   }

   public static boolean handle(BooleanSetting var0) {
      refresh();

      try {
         return var0 == null || var0.compute();
      } catch (Throwable var2) {
         return true;
      }
   }

   public static void refresh() {
      try {
         if (!vectorMatch.compute()) {
            resultEncode = (int)matrixBlend.config;
            return;
         }

         int var0 = Math.round(matrixBlend.config);
         if (var0 != resultEncode) {
            resultEncode = var0;
            SensitivityPresets.handle(var0).compute();
         }
      } catch (Throwable var1) {
      }
   }

   public static SensitivityPresets render() {
      return SensitivityPresets.handle(Math.round(matrixBlend.config));
   }

   public static void handle(int var0) {
      int var1 = Math.max(0, Math.min(SensitivityPresets.values().length - 1, var0));
      matrixBlend.config = var1;
      if (vectorMatch.compute()) {
         resultEncode = var1;
         SensitivityPresets.handle(var1).compute();
      }
   }

   public static void tick() {
      int var0 = SensitivityPresets.ULTRA.ordinal();
      matrixBlend.config = var0;
      resultEncode = var0;
   }

   public static Menu drawAnimation() {
      return source;
   }

   public static void encodePoint() {
      ThemePalette.CUSTOM
         .handle(profileInvoke.compute(), sourceSchedule.compute(), timerRender.compute(), scaleSave.compute(), colorCompute.compute(), scaleAdapt.compute());
   }

   public static void handle(MinecraftClient var0, GuiLayoutSpec var1) {
      if (layerSample != null && !layerSample.compute()) {
         if (var0 != null && var0.getWindow() != null && var1 != null) {
            int var2 = var0.getWindow().getFramebufferWidth();
            int var3 = var0.getWindow().getFramebufferHeight();
            if (var2 > 0 && var3 > 0) {
               if (!(Math.abs(responseCompute.compute() - 0.86F) > 0.005F) && !(Math.abs(providerFetch.compute() - 0.86F) > 0.005F)) {
                  float var4 = handle(var0);
                  float var5 = Math.min(var2, var3);
                  float var6 = handle(var5 * 0.025F, 18.0F, 42.0F);
                  float var7 = Math.min((var2 - var6 * 2.0F) / var1.compute(), (var3 - var6 * 2.0F) / var1.resolve());
                  float var8 = var2 / Math.max(1.0F, var3);
                  float var9 = var8 > 2.05F ? 0.58F : (var8 < 1.45F ? 0.74F : 0.68F);
                  float var10 = var8 > 2.05F ? 0.8F : 0.76F;
                  float var11 = Math.min(var2 * var9 / var1.compute(), var3 * var10 / var1.resolve());
                  var11 = handle(Math.min(var11, var7), var1.submit(), var1.unload());
                  float var12 = handle(var11 / Math.max(0.001F, var4), responseCompute.state, responseCompute.cache);
                  responseCompute.handle(var12);
                  providerFetch.handle(handle(var12 * 0.94F, providerFetch.state, providerFetch.cache));
                  layerSample.process(true);
                  animate();
               } else {
                  layerSample.process(true);
                  animate();
               }
            }
         }
      }
   }

   private static float handle(MinecraftClient var0) {
      float var1;
      try {
         var1 = Math.max(1.0F, var0.getWindow().getScaleFactor());
      } catch (Throwable var4) {
         int var3 = Math.max(1, var0.getWindow().getScaledWidth());
         var1 = Math.max(1.0F, (float)var0.getWindow().getFramebufferWidth() / var3);
      }

      return 0.68F + Math.min(var1, 2.0F) * 0.28F;
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static void animate() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }
}
