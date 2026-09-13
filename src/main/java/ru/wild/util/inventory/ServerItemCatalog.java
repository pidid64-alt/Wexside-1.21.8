package ru.wild.util.inventory;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.wild.module.api.Module;
import ru.wild.api.setting.MaxPriceSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.automation.PurchaseTaskScheduler;
import ru.wild.core.ModuleStateHelper;
import ru.wild.gui.screen.GuiMetrics;
import ru.wild.gui.screen.ModernClickGuiState;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeRenderContext;
import ru.wild.gui.widget.AnimatedUiElement;
import ru.wild.gui.widget.ModuleLayoutResult;
import ru.wild.gui.widget.ModulePanelRenderer;
import ru.wild.gui.widget.ModulePlacement;
import ru.wild.gui.widget.UiAnimationKeys;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.profile.Profile;
import ru.wild.render.font.FontObject;
import ru.wild.render.font.FontRegistry;
import ru.wild.util.math.EasingFunction;
import ru.wild.util.math.NumericTransform;
import ru.wild.util.math.SpringAnimation;
import ru.wild.util.math.SpringAnimationSpec;
import ru.wild.util.math.TimedEasingState;
import ru.wild.util.render.RenderRuntimeContext;
import ru.wild.util.render.RoundedRectRenderer;

public final class ServerItemCatalog implements ModulePanelRenderer {
   private static final SpringAnimationSpec instance = SpringAnimationSpec.handle();
   private static final SpringAnimationSpec data = SpringAnimationSpec.prepare();
   private static final SpringAnimationSpec context = SpringAnimationSpec.drawAnimation();
   private static final SpringAnimationSpec config = SpringAnimationSpec.prepare();
   private static final SimpleDateFormat state = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
   private static final SimpleDateFormat cache = new SimpleDateFormat("dd.MM.yyyy");
   private static final String[] output = new String[]{"FunTime", "SpookyTime", "HolyWorld"};
   private static final Map<String, String> current = Map.ofEntries(
      Map.entry("protection", "Защита"),
      Map.entry("fire_protection", "Огнеупорность"),
      Map.entry("feather_falling", "Невесомость"),
      Map.entry("blast_protection", "Взрывоустойчивость"),
      Map.entry("projectile_protection", "Защита от снарядов"),
      Map.entry("respiration", "Подводное дыхание"),
      Map.entry("aqua_affinity", "Подводник"),
      Map.entry("thorns", "Шипы"),
      Map.entry("depth_strider", "Подводная ходьба"),
      Map.entry("frost_walker", "Ледоход"),
      Map.entry("binding_curse", "Проклятие несъемности"),
      Map.entry("soul_speed", "Скорость души"),
      Map.entry("swift_sneak", "Проворство"),
      Map.entry("unbreaking", "Прочность"),
      Map.entry("mending", "Починка"),
      Map.entry("vanishing_curse", "Проклятие утраты"),
      Map.entry("efficiency", "Эффективность"),
      Map.entry("fortune", "Удача"),
      Map.entry("sharpness", "Острота"),
      Map.entry("smite", "Небесная кара"),
      Map.entry("bane_of_arthropods", "Бич членистоногих"),
      Map.entry("fire_aspect", "Заговор огня"),
      Map.entry("sweeping_edge", "Разящий клинок"),
      Map.entry("looting", "Добыча"),
      Map.entry("piercing", "Пронзатель"),
      Map.entry("multishot", "Тройной выстрел"),
      Map.entry("quick_charge", "Быстрая перезарядка"),
      Map.entry("luck_of_the_sea", "Морская удача")
   );
   private static final List<String> active = List.of(
      "Шлем Крушителя",
      "Нагрудник Крушителя",
      "Поножи Крушителя",
      "Ботинки Крушителя",
      "Меч Крушителя",
      "Кирка Крушителя",
      "Лук Крушителя",
      "Арбалет Крушителя",
      "Трезубец Крушителя",
      "Булава Крушителя",
      "Элитры Крушителя",
      "Удочка Крушителя",
      "Сфера Хаоса",
      "Сфера Титана",
      "Сфера Ареса",
      "Сфера Бестии",
      "Сфера Гидры",
      "Сфера Икара",
      "Сфера Эрида",
      "Сфера Сатира",
      "Талисман Демона",
      "Талисман Карателя",
      "Талисман Мрака",
      "Талисман Ярости",
      "Талисман Тирана",
      "Талисман Крушителя",
      "Талисман Раздора",
      "Зелье Ассасина",
      "Зелье Гнева",
      "Хлопушка",
      "Святая Вода",
      "Зелье Палладина",
      "Зелье Радиации",
      "Снотворное",
      "Пласт",
      "Опыт 15",
      "Опыт 30",
      "Опыт 45",
      "Вайт",
      "Блек",
      "Блок дамагер",
      "Прогрузчик чанков",
      "Маяк",
      "Проклятая Душа",
      "Драконий Скин",
      "Огненный Смерч",
      "Снежок Заморозка",
      "Божья Аура",
      "Серебро",
      "Божье Касание",
      "Мощный Удар",
      "Мега Бульдозер",
      "Нерушимые Элитры"
   );
   private static List<ServerItemCatalog.DataRecord> mode;
   private final List<ServerItemCatalog.Snapshot> selection = new ArrayList<>();
   private String enabled = "";
   private boolean renderer = false;
   private final Map<String, StringSetting> handler = new HashMap<>();
   private String animationDraw = null;
   private final RenderRuntimeContext pointEncode = new RenderRuntimeContext();
   private final RenderRuntimeContext animator = new RenderRuntimeContext();
   private final RenderRuntimeContext source = new RenderRuntimeContext();
   private final RenderRuntimeContext target = new RenderRuntimeContext();
   private float pending;
   private float previous;
   private float latest;
   private float summary;
   private float matrixBlend;
   private float vectorMatch;
   private float itemProject;
   private float responseCompute;
   private final TimedEasingState providerFetch = new TimedEasingState(EasingFunction.EASE_IN_OUT_QUAD, 460L);
   private TimedEasingState profileDraw = new TimedEasingState(EasingFunction.EASE_OUT_CUBIC, 600L);
   private int vectorPerform = -1;
   private final StringSetting eventAttach = new StringSetting("Catalog Search", "");
   private final Map<String, StringSetting> serverRead = new LinkedHashMap<>();
   private String positionAdvance = null;
   private String frameCheck = null;
   private boolean moduleCollect = false;
   private float providerClose = 0.0F;
   private float presetSave = 1.0F;
   private ServerItemCatalog.RuntimeDataRecord windowConvert = ServerItemCatalog.RuntimeDataRecord.hidden();
   private ServerItemCatalog.RuntimeDataRecord presetWrite = ServerItemCatalog.RuntimeDataRecord.hidden();
   private ServerItemCatalog.RuntimeDataRecord colorMeasure = ServerItemCatalog.RuntimeDataRecord.hidden();
   private ServerItemCatalog.RuntimeDataRecord animationSchedule = ServerItemCatalog.RuntimeDataRecord.hidden();
   private boolean rendererScan;
   private boolean sourceBuild;
   private boolean outputCollapse;
   private boolean profileInvoke;
   private float sourceSchedule;
   private int timerRender = 0;
   private String scaleSave;
   private long colorCompute;
   private float scaleAdapt;
   private float textureRun;

   @Override
   public boolean handle(Module var1) {
      return var1 instanceof AutoBuy;
   }

   @Override
   public boolean handle(Module var1, ModernClickGuiState var2) {
      return var2.invokeProfile().contains(var1) || var2.handle(UiAnimationKeys.handle(var1)) > 0.01F;
   }

   @Override
   public void handle(ModernClickGuiState var1) {
      this.resolve();
      this.renderer = false;
   }

   @Override
   public void process(ModernClickGuiState var1) {
      this.resolve();
   }

   @Override
   public void compute(ModernClickGuiState var1) {
      this.rendererScan = false;
      this.sourceBuild = false;
      this.outputCollapse = false;
      this.profileInvoke = false;
      this.sourceSchedule = 0.0F;
      this.frameCheck = null;
      this.moduleCollect = false;
      this.providerClose = 0.0F;
      this.presetSave = 1.0F;
   }

   @Override
   public float handle(Module var1, GuiMetrics var2, ModernClickGuiState var3) {
      return var2.handle(386.0F);
   }

   @Override
   public void handle(Module var1, ModernClickGuiState var2, SpringAnimationSpec var3, SpringAnimationSpec var4) {
      if (var1 instanceof AutoBuy var5) {
         boolean var6 = !var2.filterMatrix();
         boolean var7 = var6 && (var2.invokeProfile().contains(var1) || var2.collapseConfig());
         long var8 = var2.collapseConfig() ? var2.applyPlayer() : var2.compute(var1);
         long var10 = System.currentTimeMillis();
         var2.process(UiAnimationKeys.save(), var7 ? 1.0F : 0.0F, var7 ? var3 : context);
         List var12 = this.process(var5, this.eventAttach.state);
         int var13 = Math.min(var12.size(), 80);

         for (int var14 = 0; var14 < var13; var14++) {
            ServerItemCatalog.DataRecord var15 = (ServerItemCatalog.DataRecord)var12.get(var14);
            float var16 = !var7 || this.timerRender != 0 || var8 > 0L && var10 - var8 < 12L * var14 ? 0.0F : 1.0F;
            var2.process(UiAnimationKeys.handle(var15.key()), var16, var16 > 0.0F ? var3 : context);
         }

         List var18 = this.process();

         for (int var19 = 0; var19 < var18.size(); var19++) {
            String var21 = (String)var18.get(var19);
            float var17 = !var7 || this.timerRender != 0 || var8 > 0L && var10 - var8 < 24L * var19 + 70L ? 0.0F : 1.0F;
            var2.process(UiAnimationKeys.process(var21), var17, var17 > 0.0F ? var3 : context);
         }

         for (int var20 = 0; var20 < this.selection.size(); var20++) {
            ServerItemCatalog.Snapshot var22 = this.selection.get(var20);
            float var23 = !var7 || this.timerRender != 2 || var8 > 0L && var10 - var8 < 24L * var20 + 70L ? 0.0F : 1.0F;
            var2.process("cfg_entry:" + var22.name(), var23, var23 > 0.0F ? var3 : context);
         }
      }
   }
   @Override
   public void handle(RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ModulePlacement var4, ThemeRenderContext var5) {
      if (var4.handle() instanceof AutoBuy var6) {
         if (this.timerRender == 2 && !this.renderer) {
            this.handle(var6);
            this.renderer = true;
         }

         GuiMetrics var14 = var5.update();
         ServerItemCatalog.Bounds var8 = this.handle(var4, var14);
         if (!(var8.width() <= 1.0F) && !(var8.height() <= 1.0F)) {
            float var9 = var14.handle(4.0F);
            var1.compute();
            var1.handle(
               var8.x() - var9,
               var8.y() - var9,
               var8.width() + var9 * 2.0F,
               var8.height() + var9 * 2.0F,
               var14.handle(10.0F),
               var14.handle(10.0F),
               var14.handle(10.0F),
               var14.handle(10.0F)
            );
            boolean var12 = false /* VF: Semaphore variable */;

            try {
               var12 = true;
               this.handle(var1, var3, var6, var8, var5);
               if (this.timerRender == 1) {
                  this.handle(var1, var2, var3, var6, var8, var5);
                  var12 = false;
               } else if (this.timerRender == 2) {
                  this.process(var1, var3, var6, var8, var5);
                  var12 = false;
               } else {
                  this.process(var1, var2, var3, var6, var8, var5);
                  this.handle(var1, var2, var3, var8, var5);
                  var12 = false;
               }
            } finally {
               if (var12) {
                  var1.compute();
                  var1.apply();
               }
            }

            var1.compute();
            var1.apply();
            this.apply(var3);
         }
      }
   }

   @Override
   public void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ModulePlacement var3, GuiMetrics var4) {
      if (var3.handle() instanceof AutoBuy var5) {
         if (this.timerRender == 2 && !this.renderer) {
            this.handle(var5);
            this.renderer = true;
         }

         ServerItemCatalog.Bounds var7 = this.handle(var3, var4);
         if (!(var7.height() <= var4.handle(40.0F))) {
            this.handle(var1, var5, var7, var4);
            if (this.timerRender == 1) {
               this.compute(var1, var7, var4);
            } else if (this.timerRender == 2) {
               this.process(var1, var5, var7, var4);
            } else {
               this.handle(var1, var2, var5, var7, var4);
               this.handle(var1, var2, var7, var4);
            }

            var1.add(AnimatedUiElement.handle().handle(0).handle(var7.x()).process(var7.y()).compute(var7.width()).resolve(var7.height()).handle(var1x -> {
               var1x.check(false);
               if (!this.handle(var1x.filterEntity()) && this.update(var1x) == null) {
                  var1x.handle((StringSetting)null);
               }
            }).handle());
         }
      }
   }

   @Override
   public boolean handle(ModernClickGuiState var1, ModuleLayoutResult var2, GuiMetrics var3, float var4, float var5, double var6) {
      for (ModulePlacement var9 : var2.process()) {
         if (var9.handle() instanceof AutoBuy var10 && (var1.invokeProfile().contains(var9.handle()) || var1.collapseConfig())) {
            ServerItemCatalog.Bounds var12 = this.handle(var9, var3);
            if (this.timerRender == 1) {
               if (ModuleStateHelper.handle(var4, var5, var12.x(), var12.panelY(), var12.width(), var12.panelH())) {
                  this.handle(this.source, this.resolve(var12, var3), var6);
                  return true;
               }

               return false;
            }

            if (this.timerRender == 2) {
               if (ModuleStateHelper.handle(var4, var5, var12.x(), var12.panelY(), var12.width(), var12.panelH())) {
                  this.handle(this.target, this.update(var12, var3), var6);
                  return true;
               }

               return false;
            }

            if (ModuleStateHelper.handle(var4, var5, var12.leftX(), var12.panelY(), var12.leftW(), var12.panelH())) {
               this.handle(this.pointEncode, this.handle(var10, var12, var3), var6);
               return true;
            }

            if (ModuleStateHelper.handle(var4, var5, var12.rightX(), var12.panelY(), var12.rightW(), var12.panelH())) {
               this.handle(this.animator, this.compute(var12, var3), var6);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean handle(ModernClickGuiState var1, float var2, float var3) {
      if (this.frameCheck != null) {
         this.handle(this.frameCheck, var2, var1);
         return true;
      }

      if (this.timerRender == 1) {
         if (this.outputCollapse && this.colorMeasure.visible()) {
            this.process("history", var3, this.colorMeasure);
            return true;
         } else {
            return false;
         }
      } else if (this.timerRender == 2) {
         if (this.profileInvoke && this.animationSchedule.visible()) {
            this.process("cloud", var3, this.animationSchedule);
            return true;
         } else {
            return false;
         }
      } else if (this.rendererScan && this.windowConvert.visible()) {
         this.process("catalog", var3, this.windowConvert);
         return true;
      } else if (this.sourceBuild && this.presetWrite.visible()) {
         this.process("rules", var3, this.presetWrite);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean resolve(ModernClickGuiState var1) {
      boolean var2 = this.rendererScan || this.sourceBuild || this.outputCollapse || this.profileInvoke || this.frameCheck != null;
      this.compute(var1);
      return var2;
   }

   @Override
   public boolean handle(ModernClickGuiState var1, int var2) {
      if (var1.filterEntity() == this.eventAttach) {
         if (var2 == 256 || var2 == 257) {
            var1.handle((StringSetting)null);
            return true;
         } else if (var2 == 259 && !this.eventAttach.state.isEmpty()) {
            this.eventAttach.state = this.eventAttach.state.substring(0, this.eventAttach.state.length() - 1);
            this.compute();
            return true;
         } else if (var2 == 261 && !this.eventAttach.state.isEmpty()) {
            this.eventAttach.state = "";
            this.compute();
            return true;
         } else {
            return true;
         }
      } else {
         String var3 = this.update(var1);
         if (var3 != null) {
            StringSetting var6 = this.handler.get(var3);
            if (var2 == 256 || var2 == 257) {
               var1.handle((StringSetting)null);
               return true;
            } else if (var2 == 259 && !var6.state.isEmpty()) {
               var6.state = var6.state.substring(0, var6.state.length() - 1);
               return true;
            } else {
               return true;
            }
         } else {
            String var4 = this.execute(var1);
            if (var4 == null) {
               return false;
            } else {
               StringSetting var5 = this.serverRead.get(var4);
               if (var2 == 256 || var2 == 257) {
                  var1.handle((StringSetting)null);
                  var1.scheduleAnimation();
                  return true;
               } else if (var2 == 259 && !var5.state.isEmpty()) {
                  var5.state = MaxPriceSetting.handle(var5.state);
                  this.handle(var4, var5.state, var1);
                  return true;
               } else if (var2 == 261) {
                  var5.state = "";
                  this.handle(var4, var5.state, var1);
                  return true;
               } else {
                  return true;
               }
            }
         }
      }
   }

   @Override
   public boolean handle(ModernClickGuiState var1, char var2) {
      if (var1.filterEntity() == this.eventAttach) {
         if (!Character.isISOControl(var2) && this.eventAttach.state.length() < 64) {
            this.eventAttach.state = this.eventAttach.state + var2;
            this.compute();
         }

         return true;
      } else {
         String var3 = this.update(var1);
         if (var3 != null) {
            StringSetting var7 = this.handler.get(var3);
            if (!Character.isISOControl(var2) && var7.state.length() < 25 && String.valueOf(var2).matches("[a-zA-Z0-9_\\- ]")) {
               var7.state = var7.state + var2;
            }

            return true;
         } else {
            String var4 = this.execute(var1);
            if (var4 == null) {
               return false;
            }

            if (Character.isDigit(var2)) {
               StringSetting var5 = this.serverRead.get(var4);
               String var6 = MaxPriceSetting.handle(var5.state, var2);
               if (!var6.equals(var5.state)) {
                  var5.state = var6;
                  this.handle(var4, var5.state, var1);
               }
            }

            return true;
         }
      }
   }

   private void handle(AutoBuy var1) {
      this.selection.clear();
      File var2 = var1.save();
      if (var2.exists()) {
         File[] var3 = var2.listFiles((var0, var1x) -> var1x.endsWith(".json"));
         if (var3 != null) {
            String var4 = "Игрок";

            try {
               String var5 = Profile.getUsername();
               if (var5 != null) {
                  var4 = var5;
               }
            } catch (Throwable var10) {
               if (MinecraftClient.getInstance().getSession() != null) {
                  var4 = MinecraftClient.getInstance().getSession().getUsername();
               }
            }

            for (File var8 : var3) {
               String var9 = var8.getName().replace(".json", "");
               this.selection.add(new ServerItemCatalog.Snapshot(var9, var4, var8.lastModified()));
            }

            this.selection.sort((var0, var1x) -> Long.compare(var1x.timestamp, var0.timestamp));
         }
      }
   }

   private String update(ModernClickGuiState var1) {
      StringSetting var2 = var1.filterEntity();
      if (var2 == null) {
         return null;
      }

      for (Entry var4 : this.handler.entrySet()) {
         if (var4.getValue() == var2) {
            return (String)var4.getKey();
         }
      }

      return null;
   }

   private ServerItemCatalog.DataRecord handle(AutoBuy var1, String var2) {
      if (var2 == null) {
         return this.process("");
      }

      String var3 = var2.replace(' ', ' ').trim();
      var3 = var3.replaceAll("^\\[.*?\\]\\s*", "").trim();
      if (var3.matches("(?i).*\\s+[xхXХ]?\\d+[xхXХ]?$")) {
         int var4 = var3.lastIndexOf(32);
         if (var4 != -1) {
            var3 = var3.substring(0, var4).trim();
         }
      }

      if (var3.matches("(?i)^[xхXХ]?\\d+[xхXХ]?\\s+.*")) {
         int var8 = var3.indexOf(32);
         if (var8 != -1) {
            var3 = var3.substring(var8 + 1).trim();
         }
      }

      String var9 = var3.toLowerCase(Locale.ROOT);

      for (String var6 : active) {
         if (var9.contains(var6.toLowerCase(Locale.ROOT))) {
            return new ServerItemCatalog.DataRecord(var6, var6, ItemStack.EMPTY, true);
         }
      }

      for (ServerItemCatalog.DataRecord var11 : this.process(var1)) {
         if (var9.contains(var11.label().toLowerCase(Locale.ROOT)) || var9.contains(var11.key().toLowerCase(Locale.ROOT))) {
            return var11;
         }
      }

      return this.process(var3);
   }
   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, AutoBuy var3, ServerItemCatalog.Bounds var4, ThemeRenderContext var5) {
      GuiMetrics var6 = var5.update();
      ThemeColors var7 = var5.apply();
      ServerItemCatalog.CachedDataRecord var8 = this.handle(var4, var6);
      float var9 = var8.stripH();
      float var10 = var8.modeX();
      float var11 = var8.modeY();
      float var12 = var8.toggleW();
      float var13 = var8.toggleX();
      float var14 = var8.gap();
      float var15 = var8.tabBtnSize();
      float var16 = var8.chipW();
      float var17 = var10;

      for (String var21 : output) {
         boolean var22 = var3.latest.process(var21);
         float var23 = 10.0F;

         while (var23 > 8.0F && ModuleStateHelper.handle(FontRegistry.config, var21, var23) > var16 - var6.handle(12.0F)) {
            var23 -= 0.5F;
         }

         String var24 = UiAnimationKeys.apply("mode:" + var21);
         float var25 = var2.handle(var24, ModuleStateHelper.handle(var2, var17, var11, var16, var9) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
         var1.handle(ModuleStateHelper.handle(var25, var2.process(var24), 0.016F, 0.006F), var17 + var16 * 0.5F, var11 + var9 * 0.5F);
         boolean var28 = false /* VF: Semaphore variable */;

         try {
            var28 = true;
            this.handle(
               var1,
               var2,
               "mode:" + var21,
               var17,
               var11,
               var16,
               var9,
               var6.handle(8.0F),
               var25,
               var22 ? 0.78F : 0.0F,
               ServerItemCatalog.Mode.CONTROL,
               false,
               var6,
               var7
            );
            this.handle(
               var1,
               var6,
               FontRegistry.config,
               var17,
               var11,
               var16,
               var9,
               var23,
               var21,
               var22 ? var7.save() : ThemeColors.handle(var7.encodePoint(), var7.animate(), var25)
            );
            var28 = false;
         } finally {
            if (var28) {
               var1.check();
            }
         }

         var1.check();
         var17 += var16 + var14;
      }

      this.handle(var1, var2, "catalog_tab", this.timerRender == 0, var17, var11, var15, "W", false, var5);
      var17 += var15 + var14;
      this.handle(var1, var2, "history_tab", this.timerRender == 1, var17, var11, var15, "E", false, var5);
      var17 += var15 + var14;
      this.handle(var1, var2, "cloud_tab", this.timerRender == 2, var17, var11, var15, "Y", !var8.showReparse(), var5);
      if (var8.showReparse()) {
         this.handle(var1, var2, var3, var8, var5);
      }

      float var32 = var6.handle(4.0F);
      float var33 = (var12 - var32) * 0.5F;
      float var34 = var13;
      float var35 = var34 + var33 + var32;
      float var36 = var2.handle(
         UiAnimationKeys.apply("toggle:inactive"), ModuleStateHelper.handle(var2, var34, var11, var33, var9) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      float var37 = var2.handle(
         UiAnimationKeys.apply("toggle:active"), ModuleStateHelper.handle(var2, var35, var11, var33, var9) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      float var38 = var2.handle(UiAnimationKeys.compute(var3));
      this.handle(
         var1,
         var2,
         "toggle:inactive",
         var34,
         var11,
         var33,
         var9,
         var6.handle(8.0F),
         var36,
         (1.0F - var38) * 0.48F,
         ServerItemCatalog.Mode.CONTROL,
         false,
         var6,
         var7
      );
      this.handle(
         var1, var2, "toggle:active", var35, var11, var33, var9, var6.handle(8.0F), var37, var38 * 0.48F, ServerItemCatalog.Mode.CONTROL, false, var6, var7
      );
      this.handle(
         var1,
         var6,
         FontRegistry.instance,
         var34,
         var11,
         var33,
         var9,
         11.0F,
         "Пауза",
         ThemeColors.handle(ThemeColors.handle(var7.process(), 155), var7.encodePoint(), var38 * 0.82F)
      );
      this.handle(
         var1,
         var6,
         FontRegistry.instance,
         var35,
         var11,
         var33,
         var9,
         11.0F,
         "Активен",
         ThemeColors.handle(var7.encodePoint(), ThemeColors.handle(var7.handle(), 165), var38)
      );
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, AutoBuy var3, ServerItemCatalog.CachedDataRecord var4, ThemeRenderContext var5) {
      GuiMetrics var6 = var5.update();
      ThemeColors var7 = var5.apply();
      boolean var8 = var3.vectorMatch.compute();
      float var9 = var4.reparseX();
      float var10 = var4.modeY();
      float var11 = var4.stripH();
      float var12 = var4.reparseToggleW();
      float var13 = var4.reparseSliderX();
      float var14 = var4.reparseSliderW();
      float var15 = var2.handle(
         UiAnimationKeys.apply("reparse:toggle"), ModuleStateHelper.handle(var2, var9, var10, var12, var11) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      float var16 = var2.handle(UiAnimationKeys.apply("reparse:active"), var8 ? 1.0F : 0.0F, instance);
      this.handle(var1, var2, "reparse:toggle", var9, var10, var12, var11, var6.handle(8.0F), var15, var16, ServerItemCatalog.Mode.CONTROL, false, var6, var7);
      this.handle(var1, var6, FontRegistry.config, var9, var10, var12, var11, 10.0F, "ReParse", var8 ? ThemeColors.handle(var7.handle(), 180) : var7.animate());
      float var17 = var2.handle(
         UiAnimationKeys.apply("reparse:slider"), ModuleStateHelper.handle(var2, var13, var10, var14, var11) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      float var18 = var3.itemProject.compute();
      float var19 = this.handle(var18, var3.itemProject.state, var3.itemProject.cache);
      float var20 = var13 + var6.handle(8.0F);
      float var21 = var10 + var6.handle(21.0F);
      float var22 = Math.max(var6.handle(28.0F), var14 - var6.handle(16.0F));
      float var23 = var6.handle(4.0F);
      this.handle(
         var1,
         var2,
         "reparse:slider",
         var13,
         var10,
         var14 + var6.handle(9.0F),
         var11,
         var6.handle(8.0F),
         var17,
         var8 ? 0.14F : 0.0F,
         ServerItemCatalog.Mode.CONTROL,
         false,
         var6,
         var7
      );
      var1.handle(
         var20, var21, var22, var23, var6.handle(3.0F), ThemeColors.handle(ThemeColors.handle(var7.execute(), var7.apply(), 0.72F), var7.unload() ? 146 : 208)
      );
      var1.handle(var20, var21, var22, var23, var6.handle(3.0F), var7.onTick(), Math.max(0.5F, var6.handle(0.45F)));
      var1.handle(var20, var21, var22 * var19, var23, var6.handle(3.0F), ThemeColors.handle(var7.save(), 138));
      float var24 = var20 + var22 * var19;
      var1.handle(
         var24 - var6.handle(2.5F), var21 - var6.handle(2.0F), var6.handle(5.0F), var6.handle(8.0F), var6.handle(3.0F), var8 ? var7.save() : var7.animate()
      );
      String var25 = Math.round(var18) + " мин";
      float var26 = ModuleStateHelper.handle(FontRegistry.instance, var25, 9.0F);
      ModuleStateHelper.handle(
         var1,
         var6,
         FontRegistry.instance,
         var13 + (var14 - var26) * 0.5F,
         var10 + var6.handle(5.0F),
         var6.handle(10.0F),
         9.0F,
         var25,
         var8 ? var7.save() : var7.encodePoint()
      );
   }

   private ServerItemCatalog.CachedDataRecord handle(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      float var3 = var2.handle(34.0F);
      float var4 = var1.x();
      float var5 = var1.y();
      float var6 = var2.handle(150.0F);
      float var7 = var1.x() + var1.width() - var6;
      float var8 = var2.handle(8.0F);
      float var9 = var3;
      float var10 = var9 * 3.0F + var8 * 2.0F;
      float var11 = 0.0F;

      for (String var15 : output) {
         var11 = Math.max(var11, ModuleStateHelper.handle(FontRegistry.config, var15, 9.0F));
      }

      float var26 = Math.max(var2.handle(64.0F), var11 + var2.handle(18.0F));
      float var27 = var26 * output.length + var8 * (output.length - 1.0F);
      float var28 = var2.handle(158.0F);
      float var29 = var2.handle(224.0F);
      float var16 = Math.max(0.0F, var7 - var4);
      boolean var17 = var16 >= var27 + var10 + var28 + var8 * 3.0F;
      float var18 = var17 ? Math.min(var29, Math.max(var28, var16 - var27 - var10 - var8 * 3.0F)) : 0.0F;
      float var19 = MaxPriceSetting.handle(var16, var10, var18, var17, var8);
      float var20 = Math.max(var2.handle(24.0F), (var19 - var8 * (output.length - 1.0F)) / output.length);
      if (var17 && var20 < var26) {
         var17 = false;
         var18 = 0.0F;
         var19 = var16 - var10 - var8;
         var20 = Math.max(var2.handle(24.0F), (var19 - var8 * (output.length - 1.0F)) / output.length);
      }

      float var21 = var4 + var20 * output.length + var8 * output.length;
      float var22 = var21 + var10 + var8;
      float var23 = var17 ? Math.min(var2.handle(92.0F), Math.max(var2.handle(74.0F), var18 * 0.42F)) : 0.0F;
      float var24 = var22 + var23 + var8;
      float var25 = var17 ? Math.max(var2.handle(64.0F), var18 - var23 - var8) : 0.0F;
      return new ServerItemCatalog.CachedDataRecord(var3, var4, var5, var7, var6, var8, var9, var20, var17, var22, var18, var23, var24, var25);
   }

   private float handle(float var1, float var2, float var3) {
      return this.process((var1 - var2) / Math.max(0.001F, var3 - var2), 0.0F, 1.0F);
   }

   private void handle(AutoBuy var1, float var2, float var3, float var4) {
      float var5 = this.process((var2 - var3) / Math.max(1.0F, var4), 0.0F, 1.0F);
      float var6 = var1.itemProject.state;
      float var7 = var1.itemProject.cache;
      float var8 = Math.max(1.0F, var1.itemProject.output);
      float var9 = var6 + (var7 - var6) * var5;
      float var10 = var6 + (float)NumericTransform.compute((var9 - var6) / var8, 0.0F) * var8;
      var1.itemProject.handle(var10);
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      String var3,
      boolean var4,
      float var5,
      float var6,
      float var7,
      String var8,
      boolean var9,
      ThemeRenderContext var10
   ) {
      GuiMetrics var11 = var10.update();
      ThemeColors var12 = var10.apply();
      String var13 = UiAnimationKeys.apply("tab:" + var3);
      float var14 = var2.handle(var13, ModuleStateHelper.handle(var2, var5, var6, var7, var7) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.handle(ModuleStateHelper.handle(var14, var2.process(var13)), var5 + var7 * 0.5F, var6 + var7 * 0.5F);

      try {
         float var15 = var7 + (var9 ? var11.handle(9.0F) : 0.0F);
         this.handle(
            var1,
            var2,
            "tab:" + var3,
            var5,
            var6,
            var15,
            var7,
            var11.handle(8.0F),
            var14,
            var4 ? 0.74F : 0.0F,
            ServerItemCatalog.Mode.CONTROL,
            false,
            var11,
            var12
         );
         ModuleStateHelper.handle(
            var1,
            var11,
            FontRegistry.current,
            var5,
            var6,
            var7,
            var7,
            12.0F,
            var8,
            var4 ? var12.save() : ThemeColors.handle(var12.encodePoint(), var12.animate(), var14)
         );
      } finally {
         var1.check();
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      String var3,
      float var4,
      float var5,
      float var6,
      String var7,
      boolean var8,
      boolean var9,
      ThemeRenderContext var10
   ) {
      GuiMetrics var11 = var10.update();
      ThemeColors var12 = var10.apply();
      String var13 = UiAnimationKeys.apply("iconBtn:" + var3);
      float var14 = var2.handle(var13, ModuleStateHelper.handle(var2, var4, var5, var6, var6) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.handle(ModuleStateHelper.handle(var14, var2.process(var13)), var4 + var6 * 0.5F, var5 + var6 * 0.5F);

      try {
         this.handle(
            var1,
            var2,
            "iconBtn:" + var3,
            var4,
            var5,
            var6,
            var6,
            var11.handle(6.0F),
            var14,
            var9 ? 0.68F : 0.0F,
            ServerItemCatalog.Mode.CONTROL,
            false,
            var11,
            var12
         );
         if (var8) {
            var1.handle(
               var4 + var11.handle(1.0F),
               var5 + var11.handle(1.0F),
               var6 - var11.handle(2.0F),
               var6 - var11.handle(2.0F),
               var11.handle(5.0F),
               ThemeColors.handle(var12.process(), Math.round(10.0F + var14 * 24.0F))
            );
         }

         int var15 = var9 ? var12.save() : var12.animate();
         int var16 = var8 ? var12.process() : var12.load();
         int var17 = ThemeColors.handle(var15, var16, var14);
         ModuleStateHelper.handle(var1, var11, FontRegistry.current, var4, var5, var6, var6, 11.0F, var7, var17);
      } finally {
         var1.check();
      }
   }

   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, AutoBuy var3, ServerItemCatalog.Bounds var4, ThemeRenderContext var5) {
      GuiMetrics var6 = var5.update();
      ThemeColors var7 = var5.apply();
      float var8 = var2.handle(UiAnimationKeys.save());
      float var9 = this.update(var4, var6);
      float var10 = this.handle(this.target, var9);
      this.summary = var10 - this.responseCompute;
      this.responseCompute = var10;
      float var11 = var6.handle(62.0F);
      this.animationSchedule = this.handle(
         var4.x() + var4.width() - var6.handle(10.0F), var4.panelY() + var11, var4.scrollbarW(), var4.panelH() - var11 - var6.handle(10.0F), var9, var10, var6
      );
      this.handle(
         var1, var2, null, var4.x(), var4.panelY(), var4.width(), var4.panelH(), var6.handle(10.0F), 0.0F, 0.0F, ServerItemCatalog.Mode.WELL, false, var6, var7
      );
      float var12 = var4.x() + var6.handle(16.0F);
      float var13 = var4.panelY() + var6.handle(14.0F);
      var1.update(var8);

      try {
         ModuleStateHelper.handle(var1, var6, FontRegistry.config, var12, var13, var6.handle(16.0F), 13.0F, "Конфигурации покупаемых предметов", var7.animate());
         ModuleStateHelper.handle(
            var1,
            var6,
            FontRegistry.instance,
            var12,
            var13 + var6.handle(20.0F),
            var6.handle(12.0F),
            10.0F,
            "Загрузите готовый конфиг, чтобы не настраивать каждый предмет вручную.",
            var7.encodePoint()
         );
         float var14 = var6.handle(28.0F);
         float var15 = var6.handle(8.0F);
         float var16 = var4.x() + var4.width() - var6.handle(16.0F) - var14;
         this.handle(var1, var2, "cloud_btn_Y", var16, var13, var14, "Y", false, false, var5);
         var16 -= var14 + var15;
         this.handle(var1, var2, "cloud_btn_R", var16, var13, var14, "R", false, false, var5);
         var16 -= var14 + var15;
         this.handle(var1, var2, "cloud_btn_T", var16, var13, var14, "T", false, false, var5);
      } finally {
         var1.onTick();
      }

      float var68 = var4.x() + var6.handle(16.0F);
      float var69 = var4.panelY() + var11;
      float var72 = var4.width() - var6.handle(25.0F) - var4.scrollbarW() - var6.handle(0.0F);
      float var17 = var4.panelH() - var11 - var6.handle(10.0F);
      var1.compute();
      var1.handle(var68, var69, var72, var17, var6.handle(6.0F), var6.handle(6.0F), var6.handle(6.0F), var6.handle(6.0F));

      try {
         if (this.selection.isEmpty()) {
            var1.update(var8);

            try {
               float var73 = var69 + var17 * 0.5F - var6.handle(6.0F);
               String var74 = "Конфигурации не найдены";
               float var75 = ModuleStateHelper.handle(FontRegistry.instance, var74, 12.0F);
               ModuleStateHelper.handle(
                  var1,
                  var6,
                  FontRegistry.instance,
                  var68 + (var72 - var75) * 0.5F,
                  var73 - var6.handle(10.0F),
                  var6.handle(12.0F),
                  12.0F,
                  var74,
                  var7.encodePoint()
               );
            } finally {
               var1.onTick();
            }
         } else {
            float var18 = var6.handle(58.0F);
            float var19 = var6.handle(8.0F);
            float var20 = var72 - var6.handle(24.0F);

            for (int var21 = 0; var21 < this.selection.size(); var21++) {
               ServerItemCatalog.Snapshot var22 = this.selection.get(var21);
               float var23 = var69 + var10 + var21 * (var18 + var19);
               float var24 = var2.handle("cfg_entry:" + var22.name);
               float var25 = Math.min(var24, var8);
               if (!(var25 <= 0.01F)) {
                  float var26 = (1.0F - var24) * var6.handle(12.0F);
                  float var27 = var23 + var26;
                  if (!(var27 > var69 + var17) && !(var27 + var18 < var69)) {
                     StringSetting var28 = this.handler.computeIfAbsent(var22.name, var0 -> new StringSetting("Name", var0));
                     boolean var29 = var2.filterEntity() == var28;
                     if (!var29 && this.animationDraw != null && this.animationDraw.equals(var22.name)) {
                        String var30 = var28.state.trim();
                        if (!var30.isEmpty() && !var30.equals(var22.name)) {
                           var3.handle(var22.name, var30);
                           if (this.enabled.equals(var22.name)) {
                              this.enabled = var30;
                           }

                           this.handler.remove(var22.name);
                           this.handle(var3);
                           this.animationDraw = null;
                           return;
                        }

                        this.animationDraw = null;
                     }

                     if (var29) {
                        this.animationDraw = var22.name;
                     }

                     boolean var76 = this.enabled.equals(var22.name);
                     float var31 = var2.handle("cfg_active:" + var22.name, var76 ? 1.0F : 0.0F, instance);
                     float var32 = var2.handle(
                        "cfg_hover:" + var22.name, ModuleStateHelper.handle(var2, var68, var27, var20, var18) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
                     );
                     var1.update(var25);
                     var1.handle(
                        ModuleStateHelper.handle(var32, Math.abs(var2.process("cfg_hover:" + var22.name)), 0.01F, 5.0E-4F),
                        var68 + var20 * 0.5F,
                        var27 + var18 * 0.5F
                     );

                     try {
                        int var33 = ThemeColors.handle(var7.check(), var7.select(), var32);
                        int var34 = ThemeColors.handle(var33, ThemeColors.handle(var7.save(), 30), var31 * 0.35F);
                        var1.handle(var68, var27, var20, var18, var6.handle(8.0F), var34);
                        float var35 = var68 + var6.handle(16.0F);
                        float var36 = var27 + var6.handle(12.0F);
                        int var37 = ThemeColors.handle(var7.load(), var7.save(), var31);
                        if (var29) {
                           String var38 = var28.state;
                           if (System.currentTimeMillis() % 1000L > 500L) {
                              var38 = var38 + "|";
                           }

                           ModuleStateHelper.handle(var1, var6, FontRegistry.config, var35, var36, var6.handle(14.0F), 13.0F, var38, var7.load());
                        } else {
                           ModuleStateHelper.handle(var1, var6, FontRegistry.config, var35, var36, var6.handle(14.0F), 13.0F, var22.name, var37);
                        }

                        float var77 = var36 + var6.handle(22.0F);
                        float var39 = var35;
                        ModuleStateHelper.handle(
                           var1, var6, FontRegistry.current, var39, var77 - var6.handle(0.5F), var6.handle(12.0F), 8.0F, "r", var7.encodePoint()
                        );
                        var39 += var6.handle(14.0F);
                        float var40 = ModuleStateHelper.handle(FontRegistry.instance, var22.author, 10.0F);
                        ModuleStateHelper.handle(var1, var6, FontRegistry.instance, var39, var77, var6.handle(12.0F), 10.0F, var22.author, var7.animate());
                        var39 += var40 + var6.handle(6.0F);
                        ModuleStateHelper.handle(var1, var6, FontRegistry.current, var39, var77 + 0.5F, var6.handle(12.0F), 6.0F, "k", var7.encodePoint());
                        var39 += var6.handle(12.0F);
                        ModuleStateHelper.handle(
                           var1, var6, FontRegistry.current, var39, var77 - var6.handle(0.5F), var6.handle(12.0F), 10.0F, "Q", var7.encodePoint()
                        );
                        var39 += var6.handle(14.0F);
                        String var41 = cache.format(new Date(var22.timestamp));
                        ModuleStateHelper.handle(var1, var6, FontRegistry.instance, var39, var77, var6.handle(12.0F), 10.0F, var41, var7.animate());
                        float var42 = var6.handle(26.0F);
                        float var43 = var6.handle(8.0F);
                        float var44 = var68 + var20 + var6.handle(8.0F);
                        ModuleStateHelper.handle(var1, var6, FontRegistry.current, var44, var27 + (var18 - var42) * 0.5F, var42, 12.0F, "O", var7.animate());
                        float var45 = var68 + var20 - var6.handle(12.0F) - var42;
                        this.handle(var1, var2, "cfg_I_" + var22.name, var45, var27 + (var18 - var42) * 0.5F, var42, "I", true, false, var5);
                        var45 -= var42 + var43;
                        this.handle(var1, var2, "cfg_U_" + var22.name, var45, var27 + (var18 - var42) * 0.5F, var42, "U", false, var76, var5);
                     } finally {
                        var1.check();
                        var1.onTick();
                     }
                  }
               }
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }

      ModuleStateHelper.handle(var1, var6, var7, var68, var69, var72, var17, var6.handle(6.0F), this.summary);
      this.handle(var1, this.animationSchedule, this.profileInvoke, this.summary, var6, var7);
   }
   private void handle(
      RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, AutoBuy var4, ServerItemCatalog.Bounds var5, ThemeRenderContext var6
   ) {
      GuiMetrics var7 = var6.update();
      ThemeColors var8 = var6.apply();
      float var9 = var3.handle(UiAnimationKeys.save());
      float var10 = this.resolve(var5, var7);
      float var11 = this.handle(this.source, var10);
      this.latest = var11 - this.itemProject;
      this.itemProject = var11;
      int var12 = AutoBuy.timerRender.size();
      if (this.vectorPerform >= 0 && var12 > this.vectorPerform) {
         this.profileDraw = new TimedEasingState(EasingFunction.EASE_OUT_CUBIC, 600L);
         this.profileDraw.handle(1.0);
      }

      this.vectorPerform = var12;
      this.profileDraw.handle(1.0);
      this.providerFetch.handle(1.0);
      float var13 = this.process((float)this.providerFetch.check(), 0.0F, 1.0F);
      float var14 = this.process(1.0F - (float)this.profileDraw.check(), 0.0F, 1.0F);
      float var15 = var7.handle(42.0F);
      this.colorMeasure = this.handle(
         var5.x() + var5.width() - var7.handle(10.0F), var5.panelY() + var15, var5.scrollbarW(), var5.panelH() - var15 - var7.handle(10.0F), var10, var11, var7
      );
      this.handle(
         var1, var3, null, var5.x(), var5.panelY(), var5.width(), var5.panelH(), var7.handle(10.0F), 0.0F, 0.0F, ServerItemCatalog.Mode.WELL, false, var7, var8
      );
      float var16 = var5.x() + var7.handle(16.0F);
      float var17 = var5.panelY() + var7.handle(14.0F);
      var1.update(var9);
      boolean var64 = false /* VF: Semaphore variable */;

      try {
         var64 = true;
         ModuleStateHelper.handle(var1, var7, FontRegistry.config, var16, var17, var7.handle(16.0F), 14.0F, "История покупок", var8.animate());
         float var18 = ModuleStateHelper.handle(FontRegistry.config, "История покупок", 14.0F);
         this.handle(var1, var7, var8, var16 + var18 + var7.handle(12.0F), var17, var13, var14, var12);
         float var19 = var7.handle(75.0F);
         float var20 = var7.handle(20.0F);
         float var21 = var5.x() + var5.width() - var7.handle(16.0F) - var19;
         String var22 = "history_clear_all";
         float var23 = var3.handle(var22, ModuleStateHelper.handle(var3, var21, var17, var19, var20) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
         var1.handle(ModuleStateHelper.handle(var23, var3.process(var22)), var21 + var19 * 0.5F, var17 + var20 * 0.5F);
         var1.handle(
            var21,
            var17,
            var19,
            var20,
            var7.handle(6.0F),
            ThemeColors.handle(ThemeColors.handle(var8.process(), 20), ThemeColors.handle(var8.process(), 40), var23)
         );
         var1.handle(
            var21,
            var17,
            var19,
            var20,
            var7.handle(6.0F),
            ThemeColors.handle(ThemeColors.handle(var8.process(), 60), ThemeColors.handle(var8.process(), 120), var23),
            0.5F
         );
         float var24 = ModuleStateHelper.handle(FontRegistry.current, "I", 10.0F);
         float var25 = ModuleStateHelper.handle(FontRegistry.instance, "Очистить", 10.0F);
         float var26 = var7.handle(4.0F);
         float var27 = var21 + (var19 - (var24 + var26 + var25)) / 2.0F;
         ModuleStateHelper.handle(var1, var7, FontRegistry.current, var27, var17, var20, 10.0F, "I", ThemeColors.handle(var8.animate(), var8.process(), var23));
         ModuleStateHelper.handle(
            var1, var7, FontRegistry.instance, var27 + var24 + var26, var17, var20, 10.0F, "Очистить", ThemeColors.handle(var8.animate(), var8.load(), var23)
         );
         var1.check();
         var64 = false;
      } finally {
         if (var64) {
            var1.onTick();
         }
      }

      var1.onTick();
      float var69 = var5.x() + var7.handle(16.0F);
      float var70 = var5.panelY() + var15;
      float var71 = var5.width() - var7.handle(20.0F) - var5.scrollbarW();
      float var72 = var5.panelH() - var15 - var7.handle(10.0F);
      float var73 = this.source.prepare();
      float var74 = var71 - var7.handle(36.0F);
      var1.compute();
      var1.handle(var69, var70, var71, var72, var7.handle(6.0F), var7.handle(6.0F), var7.handle(6.0F), var7.handle(6.0F));

      try {
         if (AutoBuy.timerRender.isEmpty()) {
            var1.update(var9);

            try {
               String var75 = "История покупок пуста";
               float var77 = ModuleStateHelper.handle(FontRegistry.instance, var75, 12.0F);
               ModuleStateHelper.handle(
                  var1,
                  var7,
                  FontRegistry.instance,
                  var69 + (var71 - var77) * 0.5F,
                  var70 + var72 * 0.5F - var7.handle(6.0F),
                  var7.handle(12.0F),
                  12.0F,
                  var75,
                  var8.encodePoint()
               );
            } finally {
               var1.onTick();
            }
         } else {
            float var76 = var7.handle(42.0F);
            float var78 = var7.handle(6.0F);

            for (int var79 = 0; var79 < AutoBuy.timerRender.size(); var79++) {
               AutoBuy.CacheEntry var80 = AutoBuy.timerRender.get(var79);
               float var28 = var70 + var73 + var79 * (var76 + var78);
               if (!(var28 > var70 + var72) && !(var28 + var76 < var70)) {
                  var1.update(var9);

                  try {
                     var1.handle(var69, var28, var74, var76, var7.handle(8.0F), var8.check());
                     var1.handle(var69, var28, var74, var76, var7.handle(8.0F), var8.select(), 0.5F);
                     float var29 = var7.handle(28.0F);
                     float var30 = var69 + var7.handle(8.0F);
                     float var31 = var28 + (var76 - var29) * 0.5F;
                     ServerItemCatalog.DataRecord var32 = this.handle(var4, var80.data);
                     this.handle(var1, var2, var32, var30 + var7.handle(6.0F), var31 + var7.handle(6.0F), var7.handle(16.0F), var9, var69, var70, var71, var72);
                     float var33 = var30 + var29 + var7.handle(6.0F);
                     float var34 = var28;
                     String var35 = "Куплено ";
                     String var36 = (var80.context > 1 ? "x" + var80.context + " " : "") + var80.data;
                     String var37 = " за ";
                     String var38 = this.handle(var80.config);
                     float var39 = ModuleStateHelper.handle(FontRegistry.instance, var35, 10.0F);
                     float var40 = ModuleStateHelper.handle(FontRegistry.config, var36, 10.0F);
                     float var41 = ModuleStateHelper.handle(FontRegistry.instance, var37, 10.0F);
                     ModuleStateHelper.handle(var1, var7, FontRegistry.instance, var33, var34, var76, 10.0F, var35, var8.animate());
                     ModuleStateHelper.handle(var1, var7, FontRegistry.config, var33 + var39, var34 - 1.0F, var76, 10.0F, var36, var8.load());
                     ModuleStateHelper.handle(var1, var7, FontRegistry.instance, var33 + var39 + var40, var34, var76, 10.0F, var37, var8.animate());
                     ModuleStateHelper.handle(
                        var1,
                        var7,
                        FontRegistry.config,
                        var33 + var39 + var40 + var41,
                        var34 - 1.0F,
                        var76,
                        10.0F,
                        var38,
                        ThemeColors.handle(var8.handle(), 200)
                     );
                     String var42 = state.format(new Date(var80.state));
                     float var43 = ModuleStateHelper.handle(FontRegistry.instance, var42, 9.0F);
                     ModuleStateHelper.handle(
                        var1, var7, FontRegistry.instance, var69 + var74 - var43 - var7.handle(10.0F), var34, var76, 9.0F, var42, var8.encodePoint()
                     );
                     ModuleStateHelper.handle(
                        var1, var7, FontRegistry.current, var69 + var74 - var43 - var7.handle(24.0F), var34 - 1.0F, var76, 10.0F, "Q", var8.encodePoint()
                     );
                     float var44 = var7.handle(26.0F);
                     float var45 = var69 + var74 + var7.handle(6.0F);
                     this.handle(var1, var3, "hist_del_" + var80.state + "_" + var79, var45, var28 + (var76 - var44) * 0.5F, var44, "I", true, false, var6);
                  } finally {
                     var1.onTick();
                  }
               }
            }
         }
      } finally {
         var1.compute();
         var1.apply();
      }

      ModuleStateHelper.handle(var1, var7, var8, var69, var70, var71, var72, var7.handle(6.0F), this.latest);
      this.handle(var1, this.colorMeasure, this.outputCollapse, this.latest, var7, var8);
   }
   private void process(
      RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, AutoBuy var4, ServerItemCatalog.Bounds var5, ThemeRenderContext var6
   ) {
      GuiMetrics var7 = var6.update();
      ThemeColors var8 = var6.apply();
      float var9 = var3.handle(UiAnimationKeys.save());
      List var10 = this.process(var4, this.eventAttach.state);
      float var11 = this.handle(var4, var5, var7);
      float var12 = this.handle(this.pointEncode, var11);
      this.pending = var12 - this.matrixBlend;
      this.matrixBlend = var12;
      this.windowConvert = this.handle(var5.catalogScrollbarX(), var5.catalogViewportY(), var5.scrollbarW(), var5.catalogViewportH(), var11, var12, var7);
      this.handle(
         var1,
         var3,
         null,
         var5.leftX(),
         var5.panelY(),
         var5.leftW(),
         var5.panelH(),
         var7.handle(10.0F),
         0.0F,
         0.0F,
         ServerItemCatalog.Mode.WELL,
         false,
         var7,
         var8
      );
      ModuleStateHelper.handle(
         var1,
         var7,
         FontRegistry.config,
         var5.leftX() + var7.handle(12.0F),
         var5.panelY() + var7.handle(11.0F),
         var7.handle(14.0F),
         12.0F,
         "Каталог предметов",
         var8.animate()
      );
      ModuleStateHelper.handle(
         var1,
         var7,
         FontRegistry.instance,
         var5.leftX() + var7.handle(12.0F),
         var5.panelY() + var7.handle(28.0F),
         var7.handle(12.0F),
         10.0F,
         "ЛКМ по предмету — настроить цену",
         var8.encodePoint()
      );
      this.handle(var1, var3, var5, var7, var8);
      var1.compute();
      var1.handle(
         var5.catalogViewportX(),
         var5.catalogViewportY(),
         var5.catalogViewportW(),
         var5.catalogViewportH(),
         var7.handle(6.0F),
         var7.handle(6.0F),
         var7.handle(6.0F),
         var7.handle(6.0F)
      );
      boolean var15 = false /* VF: Semaphore variable */;

      try {
         var15 = true;
         this.handle(var1, var2, var3, var10, var5, var7, var8, var12, var9);
         var15 = false;
      } finally {
         if (var15) {
            var1.compute();
            var1.apply();
         }
      }

      var1.compute();
      var1.apply();
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      List<ServerItemCatalog.DataRecord> var4,
      ServerItemCatalog.Bounds var5,
      GuiMetrics var6,
      ThemeColors var7,
      float var8,
      float var9
   ) {
      int var10 = this.apply(var5, var6);
      float var11 = this.handle(var6);
      float var12 = this.process(var6);
      float var13 = this.compute(var6);
      int var14 = Math.max(1, (var4.size() + var10 - 1) / var10);
      int var15 = Math.max(0, (int)Math.floor(-var8 / (var12 + var13)) - 1);
      int var16 = Math.min(var14, (int)Math.ceil((var5.catalogViewportH() - var8) / (var12 + var13)) + 1);

      for (int var17 = var15; var17 < var16; var17++) {
         for (int var18 = 0; var18 < var10; var18++) {
            int var19 = var17 * var10 + var18;
            if (var19 >= var4.size()) {
               break;
            }

            ServerItemCatalog.DataRecord var20 = (ServerItemCatalog.DataRecord)var4.get(var19);
            float var21 = var5.catalogViewportX() + var18 * (var11 + var13);
            float var22 = var5.catalogViewportY() + var8 + var17 * (var12 + var13);
            if (!(var22 > var5.catalogViewportY() + var5.catalogViewportH()) && !(var22 + var12 < var5.catalogViewportY())) {
               float var23 = var3.handle(UiAnimationKeys.handle(var20.key()));
               if (var19 >= 80) {
                  var23 = var9;
               }

               if (!(var23 <= 0.01F)) {
                  float var24 = (1.0F - var23) * var6.handle(9.0F);
                  var1.update(var23);

                  try {
                     this.handle(
                        var1,
                        var2,
                        var3,
                        var20,
                        var21,
                        var22 + var24,
                        var11,
                        var12,
                        var6,
                        var7,
                        Math.min(var23, var9),
                        var5.catalogViewportX(),
                        var5.catalogViewportY(),
                        var5.catalogViewportW(),
                        var5.catalogViewportH()
                     );
                  } finally {
                     var1.onTick();
                  }
               }
            }
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      ServerItemCatalog.DataRecord var4,
      float var5,
      float var6,
      float var7,
      float var8,
      GuiMetrics var9,
      ThemeColors var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15
   ) {
      boolean var16 = AutoBuy.colorMeasure.containsKey(var4.key());
      boolean var17 = var4.key().equals(this.positionAdvance);
      int var18 = AutoBuy.check(var4.key());
      int var19 = AutoBuy.onTick(var4.key());
      float var20 = var3.handle(UiAnimationKeys.compute(var4.key()), ModuleStateHelper.handle(var3, var5, var6, var7, var8) ? 1.0F : 0.0F, config);
      float var21 = var3.handle("ab_settings_tile:" + var4.key(), var17 ? 1.0F : 0.0F, data);
      SpringAnimation var22 = var3.scheduleSource().get(UiAnimationKeys.compute(var4.key()));
      float var23 = var22 == null ? 0.0F : Math.abs(var22.compute());
      float var24 = ModuleStateHelper.handle(var20, var23, 0.018F, 0.006F);
      float var25 = var21;
      this.handle(var1, var3, "catalog:" + var4.key(), var5, var6, var7, var8, var9.handle(10.0F), var20, var25, ServerItemCatalog.Mode.TILE, true, var9, var10);
      float var26 = var9.handle(30.0F);
      float var27 = var5 + (var7 - var26) * 0.5F;
      float var28 = var6 + var9.handle(7.0F);
      float var29 = var27 + var26 * 0.5F;
      float var30 = var28 + var26 * 0.5F;
      var1.handle(var24, var29, var30);

      try {
         this.process(var1, var27, var28, var26, Math.max(var20, var21), var16 ? 0.22F : 0.0F, var9, var10);
         ItemStack var31 = var4.custom() ? ChaosSphereHelper.handle(var4.key()) : var4.stack();
         if (var31 != null && !var31.isEmpty()) {
            this.handle(var1, var2, var4, var27 + var9.handle(5.0F), var28 + var9.handle(5.0F), var9.handle(20.0F), var11, var12, var13, var14, var15);
         } else {
            this.handle(var1, var9, FontRegistry.config, var27, var28, var26, var26, 12.0F, "?", var10.drawAnimation());
         }
      } finally {
         var1.check();
      }

      if (this.execute(var4.key()) && (var18 > 0 || var19 < 100)) {
         String var38 = var18 + "-" + var19 + "%";
         float var32 = var9.handle(13.0F);
         float var33 = Math.max(var9.handle(24.0F), ModuleStateHelper.handle(FontRegistry.instance, var38, 7.5F) + var9.handle(8.0F));
         float var34 = Math.min(var27 + var26 - var33 + var9.handle(4.0F), var5 + var7 - var33);
         float var35 = Math.max(var6, var28 - var9.handle(5.0F));
         var1.handle(var34, var35, var33, var32, var9.handle(5.0F), ThemeColors.handle(var10.execute(), 238));
         var1.handle(var34, var35, var33, var32, var9.handle(5.0F), ThemeColors.handle(var10.save(), 116), 0.5F);
         this.handle(var1, var9, FontRegistry.instance, var34, var35, var33, var32, 7.5F, var38, ThemeColors.handle(var10.save(), 205));
      }

      ServerItemCatalog.MutableDataRecord var39 = this.handle(FontRegistry.instance, var4.label(), 8.8F, 7.2F, var7 - var9.handle(8.0F), 2);
      this.handle(
         var1,
         var9,
         FontRegistry.instance,
         var39,
         var5,
         var6 + var9.handle(43.0F),
         var9.handle(10.5F),
         ThemeColors.handle(var10.encodePoint(), var10.animate(), Math.max(var20, var16 ? 0.22F : 0.0F)),
         true,
         var7
      );
      String var40 = var16 ? this.handle(AutoBuy.colorMeasure.getOrDefault(var4.key(), 0L)) : "не задано";
      float var41 = var40.length() > 10 ? 7.5F : 8.3F;
      float var42 = ModuleStateHelper.handle(FontRegistry.instance, var40, var41);
      ModuleStateHelper.handle(
         var1,
         var9,
         FontRegistry.instance,
         var5 + (var7 - var42) * 0.5F,
         var6 + var9.handle(68.0F),
         var9.handle(10.0F),
         var41,
         var40,
         var16 ? ThemeColors.handle(var10.submit(), var10.save(), 0.65F) : var10.encodePoint()
      );
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, ServerItemCatalog.Bounds var3, GuiMetrics var4, ThemeColors var5) {
      float var6 = this.execute(var3, var4);
      float var7 = this.prepare(var3, var4);
      float var8 = this.check(var3, var4);
      float var9 = this.resolve(var4);
      float var10 = var2.handle(UiAnimationKeys.execute("catalog:search"), var2.filterEntity() == this.eventAttach ? 1.0F : 0.0F, data);
      float var11 = this.eventAttach.state != null && !this.eventAttach.state.isBlank() ? 1.0F : 0.0F;
      var1.handle(var6, var7, var8, var9, var4.handle(8.0F), ThemeColors.handle(var5.prepare(), var5.onTick(), var10));
      var1.handle(
         var6,
         var7,
         var8,
         var9,
         var4.handle(8.0F),
         ThemeColors.handle(var5.onTick(), ThemeColors.handle(var5.save(), 105), Math.max(var10, var11 * 0.35F)),
         Math.max(0.75F, var4.handle(0.55F))
      );
      if (var10 > 0.01F) {
         var1.handle(
            var6,
            var7,
            var8,
            var9,
            var4.handle(8.0F),
            var4.handle(10.0F) * var10,
            var4.handle(1.8F),
            ThemeColors.handle(var5.save(), Math.round(14.0F * var10))
         );
      }

      String var12 = this.eventAttach.state == null ? "" : this.eventAttach.state;
      String var13 = var12.isEmpty() ? "Поиск предметов" : var12;
      if (var2.filterEntity() == this.eventAttach && System.currentTimeMillis() % 1000L > 500L) {
         var13 = var13 + "|";
      }

      ModuleStateHelper.handle(
         var1,
         var4,
         FontRegistry.instance,
         var6 + var4.handle(12.0F),
         var7,
         var9,
         10.5F,
         ModuleStateHelper.handle(FontRegistry.instance, var13, 10.5F, var8 - var4.handle(44.0F)),
         var12.isEmpty() ? var5.encodePoint() : var5.animate()
      );
      if (!var12.isEmpty()) {
         ModuleStateHelper.handle(
            var1, var4, FontRegistry.state, var6 + var8 - var4.handle(25.0F), var7, var9, 10.0F, "l", ThemeColors.handle(var5.save(), 170)
         );
      }
   }
   private void handle(RoundedRectRenderer var1, DrawContext var2, ModernClickGuiState var3, ServerItemCatalog.Bounds var4, ThemeRenderContext var5) {
      GuiMetrics var6 = var5.update();
      ThemeColors var7 = var5.apply();
      float var8 = var3.handle(UiAnimationKeys.save());
      List var9 = this.process();
      float var10 = this.handle(var4, var6, var3);
      float var11 = this.handle(this.animator, var10);
      this.previous = var11 - this.vectorMatch;
      this.vectorMatch = var11;
      this.presetWrite = this.handle(var4.rulesScrollbarX(), var4.rulesViewportY(), var4.scrollbarW(), var4.rulesViewportH(), var10, var11, var6);
      this.handle(
         var1,
         var3,
         null,
         var4.rightX(),
         var4.panelY(),
         var4.rightW(),
         var4.panelH(),
         var6.handle(10.0F),
         0.0F,
         0.0F,
         ServerItemCatalog.Mode.WELL,
         false,
         var6,
         var7
      );
      ModuleStateHelper.handle(
         var1,
         var6,
         FontRegistry.config,
         var4.rightX() + var6.handle(12.0F),
         var4.panelY() + var6.handle(11.0F),
         var6.handle(14.0F),
         12.0F,
         "Настроенные предметы",
         var7.load()
      );
      ModuleStateHelper.handle(
         var1,
         var6,
         FontRegistry.instance,
         var4.rightX() + var6.handle(12.0F),
         var4.panelY() + var6.handle(28.0F),
         var6.handle(12.0F),
         10.0F,
         "Цена, статус, настройки и удаление",
         var7.animate()
      );
      this.process(var1, var3, var4, var6, var7);
      var1.compute();
      var1.handle(
         var4.rulesViewportX(),
         var4.rulesViewportY(),
         var4.rulesViewportW(),
         var4.rulesViewportH(),
         var6.handle(6.0F),
         var6.handle(6.0F),
         var6.handle(6.0F),
         var6.handle(6.0F)
      );
      boolean var31 = false /* VF: Semaphore variable */;

      try {
         var31 = true;
         if (var9.isEmpty()) {
            this.handle(var1, var4, var6, var7);
            var31 = false;
         } else {
            float var12 = var6.handle(6.0F);
            float var13 = var4.rulesViewportX() + var12;
            float var14 = var4.rulesViewportW() - var12 * 2.0F;
            float var15 = var4.rulesViewportY() + var11;

            for (int var16 = 0; var16 < var9.size(); var16++) {
               String var17 = (String)var9.get(var16);
               float var18 = this.handle(var3, var17);
               float var19 = this.handle(var17, var6, var18);
               if (!(var15 > var4.rulesViewportY() + var4.rulesViewportH()) && !(var15 + var19 < var4.rulesViewportY())) {
                  float var20 = var3.handle(UiAnimationKeys.process(var17));
                  if (var20 <= 0.01F) {
                     var15 += var19 + this.apply(var6);
                  } else {
                     float var21 = (1.0F - var20) * var6.handle(12.0F);
                     var1.update(var20);

                     try {
                        this.handle(
                           var1,
                           var2,
                           var3,
                           var17,
                           var13,
                           var15 + var21,
                           var14,
                           this.update(var6),
                           var6,
                           var7,
                           Math.min(var20, var8),
                           var4.rulesViewportX(),
                           var4.rulesViewportY(),
                           var4.rulesViewportW(),
                           var4.rulesViewportH()
                        );
                        if (var18 > 0.01F && this.execute(var17)) {
                           float var22 = this.handle(var17, var6);
                           float var23 = var15 + this.update(var6) + var6.handle(6.0F) * var18 + var21;
                           float var24 = Math.max(var6.handle(1.0F), var22 * var18);
                           var1.compute();
                           var1.handle(var13, var23, var14, var24, var6.handle(12.0F), var6.handle(12.0F), var6.handle(12.0F), var6.handle(12.0F));
                           var1.update(var18);

                           try {
                              this.process(
                                 var1,
                                 var2,
                                 var3,
                                 var17,
                                 var13,
                                 var23 - var6.handle(7.0F) * (1.0F - var18),
                                 var14,
                                 var22,
                                 var6,
                                 var7,
                                 Math.min(var20, var8) * var18,
                                 var4.rulesViewportX(),
                                 var4.rulesViewportY(),
                                 var4.rulesViewportW(),
                                 var4.rulesViewportH()
                              );
                           } finally {
                              var1.onTick();
                              var1.compute();
                              var1.apply();
                           }
                        }
                     } finally {
                        var1.onTick();
                     }

                     var15 += var19 + this.apply(var6);
                  }
               } else {
                  var15 += var19 + this.apply(var6);
               }
            }

            var31 = false;
         }
      } finally {
         if (var31) {
            var1.compute();
            var1.apply();
         }
      }

      var1.compute();
      var1.apply();
   }

   private void handle(RoundedRectRenderer var1, ServerItemCatalog.Bounds var2, GuiMetrics var3, ThemeColors var4) {
      String var5 = "Нет настроенных предметов";
      String var6 = "Выберите предмет из каталога";
      float var7 = var2.rulesViewportY() + var2.rulesViewportH() * 0.5F - var3.handle(14.0F);
      float var8 = ModuleStateHelper.handle(FontRegistry.config, var5, 12.0F);
      float var9 = ModuleStateHelper.handle(FontRegistry.instance, var6, 10.0F);
      ModuleStateHelper.handle(
         var1, var3, FontRegistry.config, var2.rulesViewportX() + (var2.rulesViewportW() - var8) * 0.5F, var7, var3.handle(14.0F), 12.0F, var5, var4.animate()
      );
      ModuleStateHelper.handle(
         var1,
         var3,
         FontRegistry.instance,
         var2.rulesViewportX() + (var2.rulesViewportW() - var9) * 0.5F,
         var7 + var3.handle(16.0F),
         var3.handle(12.0F),
         10.0F,
         var6,
         var4.encodePoint()
      );
   }

   private ServerItemCatalog.SecondaryDataRecord process(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      float var3 = var2.handle(24.0F);
      float var4 = var1.panelY() + var2.handle(12.0F);
      float var5 = var2.handle(38.0F);
      float var6 = var2.handle(38.0F);
      float var7 = var2.handle(122.0F);
      float var8 = var2.handle(6.0F);
      float var9 = var1.rightX() + var1.rightW() - var5 - var2.handle(12.0F);
      float var10 = var9 - var8 - var6;
      float var11 = var10 - var8 - var7;
      boolean var12 = var11 >= var1.rightX() + var2.handle(206.0F);
      return new ServerItemCatalog.SecondaryDataRecord(var12, var11, var4, var7, var3, var10, var6, var9, var5);
   }

   private void process(RoundedRectRenderer var1, ModernClickGuiState var2, ServerItemCatalog.Bounds var3, GuiMetrics var4, ThemeColors var5) {
      AutoBuy var6 = AutoBuy.source;
      if (var6 != null) {
         ServerItemCatalog.SecondaryDataRecord var7 = this.process(var3, var4);
         if (var7.visible()) {
            boolean var8 = var6.eventAttach.compute();
            boolean var9 = var6.positionAdvance.compute();
            PurchaseTaskScheduler var10 = var6.unload();
            boolean var11 = var8 && var10.select() > 0;
            boolean var12 = var8 && var6.submit();
            String var13;
            int var14;
            if (!var8) {
               var13 = "Детект: выкл";
               var14 = var5.encodePoint();
            } else if (!var11) {
               var13 = "Аук: нет данных";
               var14 = var5.animate();
            } else if (var12) {
               var13 = "Замедлен ~" + var10.execute() + "мс";
               var14 = var5.process();
            } else {
               var13 = "Аук ~" + var10.execute() + "мс";
               var14 = var5.handle();
            }

            float var15 = var2.handle(
               UiAnimationKeys.apply("lag:chip"),
               ModuleStateHelper.handle(var2, var7.chipX(), var7.chipY(), var7.chipW(), var7.chipH()) ? 1.0F : 0.0F,
               SpringAnimationSpec.onTick()
            );
            var1.handle(
               var7.chipX(), var7.chipY(), var7.chipW(), var7.chipH(), var4.handle(8.0F), ThemeColors.handle(var5.check(), var5.refresh(), var15 * 0.6F)
            );
            var1.handle(
               var7.chipX(),
               var7.chipY(),
               var7.chipW(),
               var7.chipH(),
               var4.handle(8.0F),
               ThemeColors.handle(var5.onTick(), ThemeColors.handle(var14, 110), var8 ? 0.65F : var15),
               0.5F
            );
            float var16 = Math.max(1.0F, var4.handle(1.25F));
            float var17 = var4.handle(10.0F);
            float var18 = var7.chipX() + var4.handle(10.0F);
            float var19 = var7.chipY() + (var7.chipH() - var17) * 0.5F;
            float var20 = var8 ? var4.handle(var12 ? 8.5F : 6.5F) : var4.handle(3.0F);
            var1.handle(var18, var19, var16, var17, var16 * 0.5F, ThemeColors.handle(var14, var8 ? 86 : 44));
            var1.handle(var18, var19 + (var17 - var20) * 0.5F, var16, var20, var16 * 0.5F, var8 ? ThemeColors.handle(var14, 210) : var5.encodePoint());
            String var21 = ModuleStateHelper.handle(var4, FontRegistry.config, var13, 9.0F, var7.chipW() - var4.handle(28.0F));
            ModuleStateHelper.handle(
               var1,
               var4,
               FontRegistry.config,
               var18 + var16 + var4.handle(7.0F),
               var7.chipY(),
               var7.chipH(),
               9.0F,
               var21,
               var8 ? ThemeColors.handle(var14, 190) : var5.animate()
            );
            boolean var22 = var6.serverRead.compute();
            float var23 = var2.handle(
               UiAnimationKeys.apply("lag:fix"),
               ModuleStateHelper.handle(var2, var7.fixX(), var7.chipY(), var7.fixW(), var7.chipH()) ? 1.0F : 0.0F,
               SpringAnimationSpec.onTick()
            );
            float var24 = var2.handle(UiAnimationKeys.apply("lag:fixOn"), var22 ? 1.0F : 0.0F, instance);
            int var25 = ThemeColors.handle(var5.check(), ThemeColors.handle(24, 140, 72, 72), var24);
            int var26 = ThemeColors.handle(var5.onTick(), ThemeColors.handle(var5.handle(), 95), var24);
            var1.handle(var7.fixX(), var7.chipY(), var7.fixW(), var7.chipH(), var4.handle(8.0F), ThemeColors.handle(var25, var5.refresh(), var23 * 0.5F));
            var1.handle(var7.fixX(), var7.chipY(), var7.fixW(), var7.chipH(), var4.handle(8.0F), ThemeColors.handle(var26, var5.refresh(), var23), 0.5F);
            this.handle(
               var1,
               var4,
               FontRegistry.config,
               var7.fixX(),
               var7.chipY(),
               var7.fixW(),
               var7.chipH(),
               9.0F,
               "фикс",
               var22 ? ThemeColors.handle(var5.handle(), 180) : var5.animate()
            );
            float var27 = var2.handle(
               UiAnimationKeys.apply("lag:stat"),
               ModuleStateHelper.handle(var2, var7.statX(), var7.chipY(), var7.statW(), var7.chipH()) ? 1.0F : 0.0F,
               SpringAnimationSpec.onTick()
            );
            float var28 = var2.handle(UiAnimationKeys.apply("lag:statOn"), var9 ? 1.0F : 0.0F, instance);
            int var29 = ThemeColors.handle(var5.check(), ThemeColors.handle(var5.save(), 52), var28);
            int var30 = ThemeColors.handle(var5.onTick(), ThemeColors.handle(var5.save(), 110), var28);
            var1.handle(var7.statX(), var7.chipY(), var7.statW(), var7.chipH(), var4.handle(8.0F), ThemeColors.handle(var29, var5.refresh(), var27 * 0.5F));
            var1.handle(var7.statX(), var7.chipY(), var7.statW(), var7.chipH(), var4.handle(8.0F), ThemeColors.handle(var30, var5.refresh(), var27), 0.5F);
            this.handle(
               var1, var4, FontRegistry.config, var7.statX(), var7.chipY(), var7.statW(), var7.chipH(), 9.0F, "стат", var9 ? var5.save() : var5.animate()
            );
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      String var4,
      float var5,
      float var6,
      float var7,
      float var8,
      GuiMetrics var9,
      ThemeColors var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15
   ) {
      boolean var16 = this.execute(var4);
      ServerItemCatalog.PrimaryDataRecord var17 = this.handle(var5, var6, var7, var8, var16, var9);
      boolean var18 = ModuleStateHelper.handle(var3, var17.priceX(), var17.controlY(), var17.priceW(), var17.controlH())
         || ModuleStateHelper.handle(var3, var17.statusX(), var17.controlY(), var17.statusW(), var17.controlH())
         || ModuleStateHelper.handle(var3, var17.deleteX(), var17.controlY(), var17.deleteW(), var17.controlH())
         || var16 && ModuleStateHelper.handle(var3, var17.settingsX(), var17.controlY(), var17.settingsW(), var17.controlH());
      String var19 = UiAnimationKeys.resolve(var4);
      float var20 = var3.handle(var19, ModuleStateHelper.handle(var3, var5, var6, var7, var8) && !var18 ? 1.0F : 0.0F, config);
      boolean var21 = !AutoBuy.profileInvoke.contains(var4);
      float var22 = var3.handle(this.refresh(var4));
      float var23 = var22;
      this.handle(var1, var3, "rule:" + var4, var5, var6, var7, var8, var9.handle(12.0F), var20, var23, ServerItemCatalog.Mode.CARD, true, var9, var10);
      ServerItemCatalog.DataRecord var24 = this.process(var4);
      float var25 = var9.handle(34.0F);
      float var26 = var5 + var9.handle(10.0F);
      float var27 = var6 + (var8 - var25) * 0.5F;
      this.handle(var1, var26, var27, var25, Math.max(var20, var23), var9, var10);
      this.handle(var1, var2, var24, var26 + var9.handle(8.0F), var27 + var9.handle(8.0F), var9.handle(18.0F), var11, var12, var13, var14, var15);
      float var28 = var3.handle(UiAnimationKeys.handle(this.prepare(var4)), var21 ? 1.0F : 0.0F, instance);
      this.handle(var1, var26, var27, var25, var28, var20, var9, var10);
      if (var17.titleW() > var9.handle(8.0F)) {
         ServerItemCatalog.MutableDataRecord var29 = this.handle(FontRegistry.config, var24.label(), 11.5F, 8.0F, var17.titleW(), 2);
         float var30 = var9.handle(11.5F);
         float var31 = var29.lines().size() * var30;
         float var32 = var6 + (var8 - var31) * 0.5F;
         int var33 = (int)Math.floor(var17.titleX());
         int var34 = (int)Math.ceil(var17.titleX() + var17.titleW());
         var1.handle(var33, (int)Math.floor(var6), Math.max(1, var34 - var33), Math.max(1, (int)Math.ceil(var8)));

         try {
            this.handle(var1, var9, FontRegistry.config, var29, var17.titleX(), var32, var30, var21 ? var10.load() : var10.animate(), false, var17.titleW());
         } finally {
            var1.apply();
         }
      }

      this.handle(var1, var3, var4, var17.priceX(), var17.controlY(), var17.priceW(), var17.controlH(), var9, var10);
      this.handle(var1, var3, var4, var17.statusX(), var17.controlY(), var17.statusW(), var17.controlH(), var9, var10, var28);
      this.process(var1, var3, var4, var17.deleteX(), var17.controlY(), var17.deleteW(), var17.controlH(), var9, var10);
      if (var16) {
         this.handle(var1, var3, var4, var17.settingsX(), var17.controlY(), var17.settingsW(), var9, var10);
      }
   }

   private ServerItemCatalog.PrimaryDataRecord handle(float var1, float var2, float var3, float var4, boolean var5, GuiMetrics var6) {
      float var7 = var6.handle(29.0F);
      float var8 = var5 ? var6.handle(29.0F) : 0.0F;
      float var9 = var6.handle(29.0F);
      float var10 = var6.handle(38.0F);
      float var11 = var6.handle(86.0F);
      float var12 = var6.handle(6.0F);
      float var13 = var6.handle(10.0F);
      MaxPriceSetting.DataRecord var14 = MaxPriceSetting.handle(var1, var3, var13, var12, var11, var10, var9, var8);
      float var15 = var1 + var6.handle(54.0F);
      float var16 = Math.max(0.0F, var14.priceX() - var15 - var6.handle(10.0F));
      return new ServerItemCatalog.PrimaryDataRecord(
         var15,
         var16,
         var14.priceX(),
         var14.priceWidth(),
         var14.statusX(),
         var14.statusWidth(),
         var14.deleteX(),
         var14.deleteWidth(),
         var14.settingsX(),
         var14.settingsWidth(),
         var2 + (var4 - var7) * 0.5F,
         var7
      );
   }
   private void handle(
      RoundedRectRenderer var1, ModernClickGuiState var2, String var3, float var4, float var5, float var6, float var7, GuiMetrics var8, ThemeColors var9
   ) {
      StringSetting var10 = this.prepare(var3);
      if (var2.filterEntity() != var10) {
         var10.state = this.check(var3);
      }

      boolean var11 = var2.filterEntity() == var10;
      float var12 = var2.handle(UiAnimationKeys.execute(var3), var11 ? 1.0F : 0.0F, data);
      float var13 = var2.handle(UiAnimationKeys.apply("price:" + var3), ModuleStateHelper.handle(var2, var4, var5, var6, var7) ? 1.0F : 0.0F, config);
      this.handle(var1, var2, "price:" + var3, var4, var5, var6, var7, var8.handle(8.0F), var13, var12, ServerItemCatalog.Mode.INSET, false, var8, var9);
      String var14 = var10.state == null ? "" : var10.state;
      String var15 = MaxPriceSetting.handle(var14, var11);
      boolean var16 = !var11 && var15.equals("Макс. цена");
      float var17 = var4 + var8.handle(8.0F);
      float var18 = var4 + var6 - var8.handle(8.0F);
      float var19 = Math.max(1.0F, var18 - var17);
      float var20 = ModuleStateHelper.handle(FontRegistry.instance, var15, 10.0F);
      float var21 = var17;
      if (var11 && !var15.isEmpty()) {
         var21 -= Math.max(0.0F, var20 - var19);
      }

      int var22 = (int)Math.floor(var17);
      int var23 = (int)Math.ceil(var18);
      var1.handle(var22, (int)Math.floor(var5), Math.max(1, var23 - var22), Math.max(1, (int)Math.ceil(var7)));
      boolean var28 = false /* VF: Semaphore variable */;

      try {
         var28 = true;
         ModuleStateHelper.handle(
            var1,
            var8,
            FontRegistry.instance,
            var21,
            var5,
            var7,
            10.0F,
            var15,
            var16 ? var9.encodePoint() : ThemeColors.handle(var9.animate(), var9.load(), var12 * 0.34F)
         );
         if (var11) {
            if (System.currentTimeMillis() % 1000L > 500L) {
               float var24 = var21 + var20 + var8.handle(1.0F);
               float var25 = var8.handle(11.0F);
               var1.handle(
                  var24,
                  var5 + (var7 - var25) * 0.5F,
                  Math.max(1.0F, var8.handle(1.0F)),
                  var25,
                  0.0F,
                  ThemeColors.handle(var9.resolve(), Math.round(150.0F + 90.0F * var12))
               );
               var28 = false;
            } else {
               var28 = false;
            }
         } else {
            var28 = false;
         }
      } finally {
         if (var28) {
            var1.apply();
         }
      }

      var1.apply();
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, GuiMetrics var7, ThemeColors var8) {
      float var9 = var7.handle(8.0F);
      float var10 = var2 + var4 - var9 - var7.handle(1.5F);
      float var11 = var3 + var4 - var9 - var7.handle(1.5F);
      float var12 = this.process(var5, 0.0F, 1.0F);
      int var13 = ThemeColors.handle(ThemeColors.handle(var8.execute(), 244), ThemeColors.handle(var8.handle(), 132), var12 * 0.34F);
      int var14 = ThemeColors.handle(var8.tick(), ThemeColors.handle(var8.handle(), 220), var12);
      int var15 = ThemeColors.handle(var8.encodePoint(), var8.handle(), var12);
      if (var12 > 0.01F) {
         var1.handle(
            var10,
            var11,
            var9,
            var9,
            var9 * 0.5F,
            var7.handle(5.0F + var6 * 2.0F),
            var7.handle(0.5F),
            ThemeColors.handle(var8.handle(), Math.round((34.0F + var6 * 20.0F) * var12))
         );
      }

      var1.handle(var10, var11, var9, var9, var9 * 0.5F, var13);
      var1.handle(var10, var11, var9, var9, var9 * 0.5F, var14, Math.max(0.5F, var7.handle(0.55F)));
      float var16 = var7.handle(3.0F + var12);
      var1.handle(var10 + (var9 - var16) * 0.5F, var11 + (var9 - var16) * 0.5F, var16, var16, var16 * 0.5F, var15);
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      String var3,
      float var4,
      float var5,
      float var6,
      float var7,
      GuiMetrics var8,
      ThemeColors var9,
      float var10
   ) {
      String var11 = UiAnimationKeys.apply(var3);
      float var12 = var2.handle(var11, ModuleStateHelper.handle(var2, var4, var5, var6, var7) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.handle(ModuleStateHelper.handle(var12, var2.process(var11), 0.02F, 0.006F), var4 + var6 * 0.5F, var5 + var7 * 0.5F);

      try {
         this.handle(
            var1, var2, "status:" + var3, var4, var5, var6, var7, var8.handle(8.0F), var12, var10 * 0.28F, ServerItemCatalog.Mode.CONTROL, false, var8, var9
         );
         float var13 = var8.handle(22.0F);
         float var14 = var8.handle(11.0F);
         float var15 = var4 + (var6 - var13) * 0.5F;
         float var16 = var5 + (var7 - var14) * 0.5F;
         float var17 = var14 * 0.5F;
         int var18 = ThemeColors.handle(ThemeColors.handle(var9.execute(), 226), ThemeColors.handle(var9.handle(), 116), var10);
         int var19 = ThemeColors.handle(var9.tick(), ThemeColors.handle(var9.handle(), 178), var10);
         var1.handle(var15, var16, var13, var14, var17, var18);
         var1.handle(var15, var16, var13, var14, var17, var19, Math.max(0.5F, var8.handle(0.55F)));
         float var20 = var8.handle(7.0F);
         float var21 = var8.handle(2.0F);
         float var22 = var15 + var21 + (var13 - var21 * 2.0F - var20) * var10;
         float var23 = var16 + (var14 - var20) * 0.5F;
         if (var10 > 0.01F) {
            var1.handle(
               var22,
               var23,
               var20,
               var20,
               var20 * 0.5F,
               var8.handle(5.0F + var12),
               var8.handle(0.4F),
               ThemeColors.handle(var9.handle(), Math.round((38.0F + var12 * 20.0F) * var10))
            );
         }

         int var24 = ThemeColors.handle(var9.animate(), var9.handle(), var10);
         var1.handle(var22, var23, var20, var20, var20 * 0.5F, var24);
         float var25 = var8.handle(2.0F);
         var1.handle(
            var22 + var8.handle(1.2F),
            var23 + var8.handle(1.1F),
            var25,
            var25,
            var25 * 0.5F,
            ThemeColors.handle(var9.load(), Math.round(52.0F + 68.0F * var10))
         );
      } finally {
         var1.check();
      }
   }

   private void process(
      RoundedRectRenderer var1, ModernClickGuiState var2, String var3, float var4, float var5, float var6, float var7, GuiMetrics var8, ThemeColors var9
   ) {
      String var10 = UiAnimationKeys.update(var3);
      float var11 = var2.handle(var10, ModuleStateHelper.handle(var2, var4, var5, var6, var7) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      var1.handle(ModuleStateHelper.handle(var11, var2.process(var10), 0.03F, 0.008F), var4 + var6 * 0.5F, var5 + var7 * 0.5F);

      try {
         this.handle(var1, var2, "delete:" + var3, var4, var5, var6, var7, var8.handle(8.0F), var11, 0.0F, ServerItemCatalog.Mode.CONTROL, false, var8, var9);
         float var12 = 10.0F;
         ModuleStateHelper.handle(
            var1, var8, FontRegistry.current, var4, var5, var6, var7, var12, "I", ThemeColors.handle(var9.encodePoint(), var9.process(), var11)
         );
      } finally {
         var1.check();
      }
   }

   private void handle(RoundedRectRenderer var1, ModernClickGuiState var2, String var3, float var4, float var5, float var6, GuiMetrics var7, ThemeColors var8) {
      boolean var9 = var3.equals(this.positionAdvance);
      String var10 = UiAnimationKeys.apply("settings:" + var3);
      float var11 = var2.handle(var10, ModuleStateHelper.handle(var2, var4, var5, var6, var6) ? 1.0F : 0.0F, SpringAnimationSpec.onTick());
      float var12 = var2.handle("ab_settings_on:" + var3, var9 ? 1.0F : 0.0F, data);
      var1.handle(ModuleStateHelper.handle(var11, var2.process(var10), 0.03F, 0.008F), var4 + var6 * 0.5F, var5 + var6 * 0.5F);

      try {
         this.handle(var1, var2, "settings:" + var3, var4, var5, var6, var6, var7.handle(8.0F), var11, var12, ServerItemCatalog.Mode.CONTROL, false, var7, var8);
         float var13 = 11.0F;
         ModuleStateHelper.handle(
            var1, var7, FontRegistry.context, var4, var5, var6, var6, var13, "I", ThemeColors.handle(var8.encodePoint(), var8.save(), Math.max(var11, var12))
         );
      } finally {
         var1.check();
      }
   }

   private void process(
      RoundedRectRenderer var1,
      DrawContext var2,
      ModernClickGuiState var3,
      String var4,
      float var5,
      float var6,
      float var7,
      float var8,
      GuiMetrics var9,
      ThemeColors var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15
   ) {
      float var16 = var3.handle(
         UiAnimationKeys.apply("settingsPanel:" + var4), ModuleStateHelper.handle(var3, var5, var6, var7, var8) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      float var17 = var3.handle(this.refresh(var4));
      this.handle(var1, var3, "settingsPanel:" + var4, var5, var6, var7, var8, var9.handle(12.0F), var16, var17, ServerItemCatalog.Mode.CARD, true, var9, var10);
      ServerItemCatalog.DataRecord var18 = this.process(var4);
      float var19 = var9.handle(16.0F);
      float var20 = var9.handle(32.0F);
      float var21 = var5 + var19;
      float var22 = var6 + var9.handle(12.0F);
      this.handle(var1, var21, var22, var20, Math.max(var16, var17), var9, var10);
      this.handle(var1, var2, var18, var21 + var9.handle(7.0F), var22 + var9.handle(7.0F), var9.handle(18.0F), var11, var12, var13, var14, var15);
      float var23 = var21 + var20 + var9.handle(10.0F);
      ModuleStateHelper.handle(var1, var9, FontRegistry.config, var23, var6 + var9.handle(12.0F), var9.handle(15.0F), 12.5F, "Настройки предмета", var10.load());
      String var24 = AutoBuy.check(var4) + "-" + AutoBuy.onTick(var4) + "%";
      float var25 = Math.max(var9.handle(48.0F), ModuleStateHelper.handle(FontRegistry.config, var24, 10.0F) + var9.handle(14.0F));
      float var26 = var5 + var7 - var19 - var25;
      float var27 = var6 + var9.handle(14.0F);
      float var28 = var9.handle(22.0F);
      int var29 = ThemeColors.handle(ThemeColors.handle(var10.execute(), var10.apply(), var10.unload() ? 0.34F : 0.44F), var10.unload() ? 164 : 220);
      int var30 = ThemeColors.handle(ThemeColors.handle(var10.execute(), var10.apply(), var10.unload() ? 0.48F : 0.7F), var10.unload() ? 144 : 202);
      float var31 = Math.max(0.5F, var9.handle(0.8F));
      var1.handle(var26, var27, var25, var28, var9.handle(7.0F), var29);
      var1.handle(var26 + var31, var27 + var31, var25 - var31 * 2.0F, var28 - var31 * 2.0F, var9.handle(6.0F), var30);
      var1.handle(var26, var27, var25, var28, var9.handle(7.0F), var10.select(), Math.max(0.5F, var9.handle(0.5F)));
      this.handle(var1, var9, FontRegistry.config, var26, var6 + var9.handle(14.0F), var25, var9.handle(22.0F), 10.0F, var24, var10.save());
      float var32 = var26 - var23 - var9.handle(8.0F);
      ServerItemCatalog.MutableDataRecord var33 = this.handle(FontRegistry.instance, var18.label(), 10.0F, 8.0F, var32, 2);
      this.handle(var1, var9, FontRegistry.instance, var33, var23, var6 + var9.handle(31.0F), var9.handle(10.0F), var10.animate(), false, var32);
      this.compute(var1, var3, var4, var5 + var19, var6 + var9.handle(58.0F), var7 - var19 * 2.0F, var9.handle(36.0F), var9, var10);
      ServerItemCatalog.PersistentDataRecord var34 = this.compute(var4);
      if (!var34.enchantments().isEmpty()) {
         ModuleStateHelper.handle(
            var1, var9, FontRegistry.instance, var5 + var19, var6 + var9.handle(108.0F), var9.handle(12.0F), 9.5F, "Зачарования", var10.animate()
         );
         this.handle(var1, var3, var4, var34.enchantments(), var5 + var19, var6 + var9.handle(128.0F), var7 - var19 * 2.0F, var9, var10);
      }
   }

   private void compute(
      RoundedRectRenderer var1, ModernClickGuiState var2, String var3, float var4, float var5, float var6, float var7, GuiMetrics var8, ThemeColors var9
   ) {
      int var10 = AutoBuy.check(var3);
      int var11 = AutoBuy.onTick(var3);
      float var12 = var4;
      float var13 = var5 + var8.handle(22.0F);
      float var14 = var6;
      float var15 = var8.handle(5.0F);
      float var16 = var12 + var14 * var10 / 100.0F;
      float var17 = var12 + var14 * var11 / 100.0F;
      float var18 = var2.handle(
         UiAnimationKeys.apply("durSlider:" + var3), ModuleStateHelper.handle(var2, var4, var5, var6, var7) ? 1.0F : 0.0F, SpringAnimationSpec.onTick()
      );
      float var19 = this.handle("durability:" + var3);
      float var20 = var19 <= 0.0F ? 0.0F : (float)Math.sin(var19 * Math.PI);
      ModuleStateHelper.handle(var1, var8, FontRegistry.instance, var4, var5, var8.handle(12.0F), 10.0F, "Диапазон прочности", var9.animate());
      float var21 = Math.max(0.5F, var8.handle(0.75F));
      var1.handle(
         var12,
         var13,
         var14,
         var15,
         var8.handle(3.0F),
         ThemeColors.handle(ThemeColors.handle(var9.execute(), var9.apply(), var9.unload() ? 0.38F : 0.76F), var9.unload() ? 154 : 218)
      );
      var1.handle(
         var12 + var21,
         var13 + var21,
         var14 - var21 * 2.0F,
         var15 - var21 * 2.0F,
         var8.handle(2.0F),
         ThemeColors.handle(ThemeColors.handle(var9.execute(), var9.apply(), var9.unload() ? 0.52F : 0.86F), var9.unload() ? 142 : 210)
      );
      var1.handle(var12, var13, var14, var15, var8.handle(3.0F), var9.onTick(), Math.max(0.5F, var8.handle(0.45F)));
      var1.handle(var16, var13, Math.max(var8.handle(3.0F), var17 - var16), var15, var8.handle(3.0F), ThemeColors.handle(var9.save(), 138));
      this.handle(var1, var16, var13 + var15 * 0.5F, var10 == 0 ? var9.animate() : var9.save(), var18 + var20 * 0.45F, var8, var9);
      this.handle(var1, var17, var13 + var15 * 0.5F, var11 == 100 ? var9.animate() : var9.save(), var18 + var20 * 0.45F, var8, var9);
      ModuleStateHelper.handle(
         var1, var8, FontRegistry.instance, var4, var5 + var8.handle(30.0F), var8.handle(10.0F), 8.5F, "Мин " + var10 + "%", var9.encodePoint()
      );
      String var22 = "Макс " + var11 + "%";
      float var23 = ModuleStateHelper.handle(FontRegistry.instance, var22, 8.5F);
      ModuleStateHelper.handle(
         var1, var8, FontRegistry.instance, var4 + var6 - var23, var5 + var8.handle(30.0F), var8.handle(10.0F), 8.5F, var22, var9.encodePoint()
      );
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, int var4, float var5, GuiMetrics var6, ThemeColors var7) {
      float var8 = this.process(var5, 0.0F, 1.0F);
      float var9 = var6.handle(10.0F + var8 * 1.7F);
      float var10 = var2 - var9 * 0.5F;
      float var11 = var3 - var9 * 0.5F;
      float var12 = Math.max(0.5F, var6.handle(1.0F));
      if (var8 > 0.012F) {
         var1.handle(
            var10,
            var11 + var6.handle(0.5F),
            var9,
            var9,
            var9 * 0.5F,
            var6.handle(4.5F) * var8,
            var6.handle(0.4F),
            ThemeColors.handle(var4, Math.round(12.0F + 16.0F * var8))
         );
      }

      var1.handle(var10, var11, var9, var9, var9 * 0.5F, ThemeColors.handle(ThemeColors.handle(var7.execute(), var7.apply(), 0.38F), var7.unload() ? 212 : 238));
      var1.handle(var10 + var12, var11 + var12, var9 - var12 * 2.0F, var9 - var12 * 2.0F, Math.max(0.0F, var9 * 0.5F - var12), ThemeColors.handle(var4, 228));
      var1.handle(var10, var11, var9, var9, var9 * 0.5F, var7.refresh(), Math.max(0.5F, var6.handle(0.5F)));
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, GuiMetrics var6, ThemeColors var7) {
      float var8 = this.process(var5, 0.0F, 1.0F);
      this.process(var1, var2, var3, var4, var8, var8, var6, var7);
   }

   private void process(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6, GuiMetrics var7, ThemeColors var8) {
      float var9 = this.process(var5, 0.0F, 1.0F);
      float var10 = this.process(var6, 0.0F, 1.0F);
      float var11 = var7.handle(9.0F);
      float var12 = Math.max(0.5F, var7.handle(1.0F));
      int var13 = ThemeColors.handle(ThemeColors.handle(var8.execute(), var8.apply(), var8.unload() ? 0.36F : 0.7F), var8.unload() ? 156 : 224);
      int var14 = ThemeColors.handle(ThemeColors.handle(var8.execute(), var8.apply(), var8.unload() ? 0.54F : 0.84F), var8.unload() ? 142 : 206);
      if (var9 > 0.012F) {
         float var15 = var4 * 0.46F;
         float var16 = var2 + (var4 - var15) * 0.5F;
         float var17 = var3 + (var4 - var15) * 0.5F;
         var1.handle(var16, var17, var15, var15, var15 * 0.5F, var7.handle(6.0F) * var9, 0.0F, ThemeColors.handle(var8.save(), Math.round(8.0F + 12.0F * var9)));
      }

      var1.handle(var2, var3, var4, var4, var11, var13);
      var1.handle(var2 + var12, var3 + var12, var4 - var12 * 2.0F, var4 - var12 * 2.0F, Math.max(0.0F, var11 - var12), var14);
      var1.handle(var2, var3, var4, var4, var11, var8.select(), Math.max(0.5F, var7.handle(0.5F)));
      var1.handle(
         var2 + var12,
         var3 + var12,
         var4 - var12 * 2.0F,
         var4 - var12 * 2.0F,
         Math.max(0.0F, var11 - var12),
         ThemeColors.handle(var8.onTick(), ThemeColors.handle(var8.save(), 62), Math.max(var9 * 0.34F, var10 * 0.26F)),
         Math.max(0.5F, var7.handle(0.45F))
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      String var3,
      String var4,
      String var5,
      boolean var6,
      boolean var7,
      float var8,
      float var9,
      float var10,
      float var11,
      GuiMetrics var12,
      ThemeColors var13
   ) {
      float var14 = var2.handle(
         UiAnimationKeys.apply("check:" + var3 + ":" + var4),
         var7 && ModuleStateHelper.handle(var2, var8, var9, var10, var11) ? 1.0F : 0.0F,
         SpringAnimationSpec.onTick()
      );
      float var15 = this.process(var2.handle("ab_check_on:" + var3 + ":" + var4, var6 && var7 ? 1.0F : 0.0F, instance), 0.0F, 1.0F);
      float var16 = var15 * var15 * (3.0F - 2.0F * var15);
      int var17 = var7 ? ThemeColors.handle(var13.animate(), var13.load(), var14) : var13.encodePoint();
      float var18 = var12.handle(14.0F);
      float var19 = var9 + (var11 - var18) * 0.5F;
      float var20 = this.handle("check:" + var3 + ":" + var4);
      float var21 = var20 <= 0.0F ? 0.0F : (float)Math.sin(var20 * Math.PI);
      float var22 = Math.max(0.5F, var12.handle(1.0F));
      if (var14 > 0.012F) {
         var1.handle(
            var8 - var12.handle(3.0F),
            var9,
            Math.max(0.0F, var10 - var12.handle(1.0F)),
            var11,
            var12.handle(6.0F),
            ThemeColors.handle(var13.load(), Math.round(4.0F + 8.0F * var14))
         );
      }

      var1.handle(
         var8,
         var19,
         var18,
         var18,
         var12.handle(4.0F),
         ThemeColors.handle(ThemeColors.handle(var13.execute(), var13.apply(), var13.unload() ? 0.42F : 0.74F), var13.unload() ? 168 : 224)
      );
      var1.handle(
         var8 + var22,
         var19 + var22,
         var18 - var22 * 2.0F,
         var18 - var22 * 2.0F,
         var12.handle(3.0F),
         ThemeColors.handle(
            ThemeColors.handle(ThemeColors.handle(var13.execute(), var13.apply(), var13.unload() ? 0.56F : 0.84F), var13.unload() ? 150 : 204),
            ThemeColors.handle(var13.save(), 54),
            var16 * 0.34F
         )
      );
      var1.handle(
         var8,
         var19,
         var18,
         var18,
         var12.handle(4.0F),
         ThemeColors.handle(var13.select(), ThemeColors.handle(var13.save(), 72), Math.max(var16 * 0.52F, var14 * 0.25F)),
         Math.max(0.5F, var12.handle(0.5F))
      );
      if (var16 > 0.001F) {
         float var23 = 7.5F;
         float var24 = ModuleStateHelper.handle(FontRegistry.current, "j", var23);
         var1.handle(0.7F + var16 * 0.3F, var8 + var18 * 0.5F, var19 + var18 * 0.5F);

         try {
            int var25 = ThemeColors.handle(var13.load(), Math.round(238.0F * var16));
            ModuleStateHelper.handle(
               var1,
               var12,
               FontRegistry.current,
               var8 + (var18 - var24) * 0.5F,
               var19,
               var18,
               var23,
               "j",
               ThemeColors.handle(var25, ThemeColors.handle(var13.save(), Math.round(238.0F * var16)), 0.34F + var21 * 0.2F)
            );
         } finally {
            var1.check();
         }
      }

      ModuleStateHelper.handle(
         var1,
         var12,
         FontRegistry.instance,
         var8 + var18 + var12.handle(7.0F),
         var9,
         var11,
         9.5F,
         ModuleStateHelper.handle(FontRegistry.instance, var5, 9.5F, var10 - var18 - var12.handle(10.0F)),
         var17
      );
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      String var3,
      List<ServerItemCatalog.FallbackDataRecord> var4,
      float var5,
      float var6,
      float var7,
      GuiMetrics var8,
      ThemeColors var9
   ) {
      if (var4.isEmpty()) {
         ModuleStateHelper.handle(var1, var8, FontRegistry.instance, var5, var6, var8.handle(14.0F), 9.5F, "Нет заданных зачарований", var9.encodePoint());
      } else {
         float var10 = var8.handle(8.0F);
         float var11 = var8.handle(22.0F);
         float var12 = (var7 - var10) * 0.5F;

         for (int var13 = 0; var13 < var4.size(); var13++) {
            ServerItemCatalog.FallbackDataRecord var14 = (ServerItemCatalog.FallbackDataRecord)var4.get(var13);
            float var15 = var5 + var13 % 2 * (var12 + var10);
            float var16 = var6 + var13 / 2 * (var11 + var8.handle(4.0F));
            boolean var17 = AutoBuy.process(var3, var14.key());
            this.handle(var1, var2, var3, var14.key(), var14.label(), var17, true, var15, var16, var12, var11, var8, var9);
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1, GuiMetrics var2, FontObject var3, float var4, float var5, float var6, float var7, float var8, String var9, int var10
   ) {
      float var11 = ModuleStateHelper.handle(var3, var9, var8);
      ModuleStateHelper.handle(var1, var2, var3, var4 + (var6 - var11) * 0.5F, var5, var7, var8, var9, var10);
   }
   private void handle(RoundedRectRenderer var1, GuiMetrics var2, ThemeColors var3, float var4, float var5, float var6, float var7, int var8) {
      if (!(var6 <= 0.01F)) {
         int var9 = var3.handle();
         float var10 = (1.0F - var6) * var2.handle(6.0F);
         var1.update(var6);
         boolean var19 = false /* VF: Semaphore variable */;

         try {
            var19 = true;
            float var11 = var4 + var10;
            float var12 = Math.max(1.0F, var2.handle(1.4F));
            float var13 = var2.handle(9.0F);
            float var14 = var5 + (var2.handle(16.0F) - var13) * 0.5F;
            float var15 = var2.handle(4.0F) + var2.handle(4.0F) * var7;
            var1.handle(var11, var14, var12, var13, var12 * 0.5F, ThemeColors.handle(var9, Math.round(82.0F + 56.0F * var7)));
            var1.handle(var11, var14 + (var13 - var15) * 0.5F, var12, var15, var12 * 0.5F, ThemeColors.handle(var9, Math.round(158.0F + 76.0F * var7)));
            String var16 = var8 <= 0 ? "Мониторинг" : "Покупок: " + var8;
            ModuleStateHelper.handle(
               var1,
               var2,
               FontRegistry.instance,
               var11 + var12 + var2.handle(6.0F),
               var5,
               var2.handle(16.0F),
               9.5F,
               var16,
               ThemeColors.handle(var9, Math.round(152.0F + 72.0F * var7))
            );
            var19 = false;
         } finally {
            if (var19) {
               var1.onTick();
            }
         }

         var1.onTick();
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      ModernClickGuiState var2,
      String var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      ServerItemCatalog.Mode var11,
      boolean var12,
      GuiMetrics var13,
      ThemeColors var14
   ) {
      float var15 = this.process(var9, 0.0F, 1.0F);
      float var16 = this.process(var10, 0.0F, 1.0F);
      float var17 = this.handle(var3);
      float var18 = var17 <= 0.0F ? 0.0F : (float)Math.sin(var17 * Math.PI);
      float var19 = Math.max(var15, Math.max(var16, var18));
      float var20 = var18 > 0.001F ? this.scaleAdapt : var2.sampleLayer();
      float var21 = var18 > 0.001F ? this.textureRun : var2.sendWorld();
      float var22 = var19 > 0.001F ? this.process((var20 - var4) / Math.max(1.0F, var6), 0.07F, 0.93F) : 0.72F;
      float var23 = var19 > 0.001F ? this.process((var21 - var5) / Math.max(1.0F, var7), 0.1F, 0.84F) : 0.18F;
      int var24 = this.handle(var14, var11);
      int var25 = var24 >>> 24 & 0xFF;

      float var26 = switch (var11) {
         case WELL -> var14.unload() ? 0.006F : 0.01F;
         case TILE -> var14.unload() ? 0.008F : 0.014F;
         case CARD -> var14.unload() ? 0.012F : 0.021F;
         case CONTROL -> var14.unload() ? 0.01F : 0.017F;
         case INSET -> var14.unload() ? 0.004F : 0.007F;
      };

      float var27 = switch (var11) {
         case WELL -> var14.unload() ? 0.006F : 0.012F;
         case TILE -> var14.unload() ? 0.008F : 0.017F;
         case CARD -> var14.unload() ? 0.012F : 0.026F;
         case CONTROL -> var14.unload() ? 0.01F : 0.021F;
         case INSET -> var14.unload() ? 0.007F : 0.013F;
      };
      int var28 = ThemeColors.handle(ThemeColors.handle(var24, ThemeColors.handle(var14.load(), 255), var26), var25);
      int var29 = var14.unload() ? ThemeColors.handle(58, 70, 82, 255) : ThemeColors.handle(0, 0, 0, 255);
      int var30 = ThemeColors.handle(ThemeColors.handle(var24, var29, var27), var25);
      float var31 = var11 == ServerItemCatalog.Mode.CARD && var12 ? 0.62F : 0.0F;
      float var32 = Math.max(var31, var12 ? var19 : 0.0F);
      if (var32 > 0.012F) {
         float var33 = var11 == ServerItemCatalog.Mode.CARD ? 0.72F + var32 * 0.38F : 0.28F + var32 * 0.72F;
         var1.handle(
            var4,
            var5 + var13.handle(0.8F) * var32,
            var6,
            var7,
            var8,
            var13.handle(var11 == ServerItemCatalog.Mode.CARD ? 5.2F : 4.2F) * var33,
            var13.handle(0.65F) * var33,
            var14.unload()
               ? ThemeColors.handle(52, 64, 76, Math.round((var11 == ServerItemCatalog.Mode.CARD ? 15.0F : 18.0F) * var33))
               : ThemeColors.handle(0, 0, 0, Math.round((var11 == ServerItemCatalog.Mode.CARD ? 34.0F : 40.0F) * var33))
         );
      }

      var1.handle(var4, var5, var6, var7, var8, var28, var30, var14.save(), var14.submit(), var22, var23, var15, Math.max(var16, var17), var17 > 0.001F, 6);
      int var37;
      if (var14.unload()) {
         var37 = ModuleStateHelper.process(var14, var11 == ServerItemCatalog.Mode.CARD ? 0.82F : (var11 == ServerItemCatalog.Mode.WELL ? 0.62F : 0.72F));
      } else {
         switch (var11) {
            case WELL:
            case INSET:
               var37 = var14.onTick();
               break;
            case TILE:
               var37 = var14.select();
               break;
            case CARD:
            case CONTROL:
               var37 = var14.refresh();
               break;
            default:
               throw new MatchException(null, null);
         }
      }

      int var35 = var37;
      float var34 = var11 == ServerItemCatalog.Mode.CARD ? var13.handle(0.65F) : var13.handle(0.55F);
      var1.handle(var4, var5, var6, var7, var8, var35, Math.max(0.5F, var34));
   }

   private int handle(ThemeColors var1, ServerItemCatalog.Mode var2) {
      if (var1.unload()) {
         int var3 = ModuleStateHelper.handle(var1, 0.0F);

         return switch (var2) {
            case WELL -> ThemeColors.handle(ThemeColors.handle(var3, ThemeColors.handle(var1.execute(), 255), 0.34F), 228);
            case TILE -> ThemeColors.handle(ThemeColors.handle(var3, ThemeColors.handle(var1.execute(), 255), 0.24F), 234);
            case CARD -> ThemeColors.handle(ThemeColors.handle(var3, ThemeColors.handle(var1.execute(), 255), 0.08F), 242);
            case CONTROL -> ThemeColors.handle(ThemeColors.handle(var3, ThemeColors.handle(var1.execute(), 255), 0.15F), 238);
            case INSET -> ThemeColors.handle(ThemeColors.handle(var3, ThemeColors.handle(var1.execute(), 255), 0.48F), 232);
         };
      } else {
         return switch (var2) {
            case WELL -> ThemeColors.handle(ThemeColors.handle(var1.execute(), var1.apply(), 0.7F), 242);
            case TILE -> ThemeColors.handle(ThemeColors.handle(var1.execute(), var1.apply(), 0.46F), 244);
            case CARD -> ThemeColors.handle(ThemeColors.handle(var1.execute(), var1.apply(), 0.16F), 248);
            case CONTROL -> ThemeColors.handle(ThemeColors.handle(var1.execute(), var1.apply(), 0.28F), 246);
            case INSET -> ThemeColors.handle(ThemeColors.handle(var1.execute(), var1.apply(), 0.74F), 244);
         };
      }
   }

   private void handle(String var1, ModernClickGuiState var2) {
      this.scaleSave = var1;
      this.colorCompute = System.currentTimeMillis();
      this.scaleAdapt = var2.sampleLayer();
      this.textureRun = var2.sendWorld();
   }

   private float handle(String var1) {
      if (var1 != null && this.scaleSave != null && this.scaleSave.equals(var1) && this.colorCompute > 0L) {
         float var2 = (float)(System.currentTimeMillis() - this.colorCompute) / 260.0F;
         if (var2 >= 1.0F) {
            this.scaleSave = null;
            this.colorCompute = 0L;
            return 0.0F;
         } else {
            return this.process(var2, 0.0F, 1.0F);
         }
      } else {
         return 0.0F;
      }
   }

   private ServerItemCatalog.MutableDataRecord handle(FontObject var1, String var2, float var3, float var4, float var5, int var6) {
      String var7 = var2 == null ? "" : var2.trim();
      float var8 = Math.max(4.5F, var4 * 0.55F);

      for (float var9 = var3; var9 >= var8; var9 -= 0.5F) {
         List var10 = this.handle(var1, var7, var9, var5);
         if (var10.size() <= var6) {
            return new ServerItemCatalog.MutableDataRecord(var10, var9);
         }
      }

      List var15 = this.handle(var1, var7, var8, var5);
      if (var15.size() <= var6) {
         return new ServerItemCatalog.MutableDataRecord(var15, var8);
      }

      ArrayList<String> var16 = new ArrayList<>(var6);

      for (int var11 = 0; var11 < var6; var11++) {
         int var12 = var11 * var15.size() / var6;
         int var13 = (var11 + 1) * var15.size() / var6;
         var16.add(String.join(" ", var15.subList(var12, var13)));
      }

      float var17 = var8;

      for (String var19 : var16) {
         float var14 = ModuleStateHelper.handle(var1, var19, var8);
         if (var14 > var5) {
            var17 = Math.min(var17, var8 * var5 / var14);
         }
      }

      return new ServerItemCatalog.MutableDataRecord(var16, Math.max(1.5F, var17));
   }

   private List<String> handle(FontObject var1, String var2, float var3, float var4) {
      ArrayList var5 = new ArrayList();
      if (var2.isEmpty()) {
         return var5;
      }

      StringBuilder var6 = new StringBuilder();

      for (String var10 : var2.split("\\s+")) {
         if (!var10.isEmpty()) {
            if (var6.isEmpty()) {
               this.handle(var5, var6, var1, var10, var3, var4);
            } else {
               String var11 = var6 + " " + var10;
               if (ModuleStateHelper.handle(var1, var11, var3) <= var4) {
                  var6.append(' ').append(var10);
               } else {
                  var5.add(var6.toString());
                  var6.setLength(0);
                  this.handle(var5, var6, var1, var10, var3, var4);
               }
            }
         }
      }

      if (!var6.isEmpty()) {
         var5.add(var6.toString());
      }

      return var5;
   }

   private void handle(List<String> var1, StringBuilder var2, FontObject var3, String var4, float var5, float var6) {
      if (ModuleStateHelper.handle(var3, var4, var5) <= var6) {
         var2.append(var4);
      } else {
         int var7 = 0;

         while (var7 < var4.length()) {
            int var8 = var4.codePointAt(var7);
            String var9 = new String(Character.toChars(var8));
            if (!var2.isEmpty() && ModuleStateHelper.handle(var3, var2 + var9, var5) > var6) {
               var1.add(var2.toString());
               var2.setLength(0);
            }

            var2.append(var9);
            var7 += Character.charCount(var8);
         }
      }
   }

   private void handle(
      RoundedRectRenderer var1,
      GuiMetrics var2,
      FontObject var3,
      ServerItemCatalog.MutableDataRecord var4,
      float var5,
      float var6,
      float var7,
      int var8,
      boolean var9,
      float var10
   ) {
      for (int var11 = 0; var11 < var4.lines().size(); var11++) {
         String var12 = var4.lines().get(var11);
         float var13 = var9 ? var5 + (var10 - ModuleStateHelper.handle(var3, var12, var4.size())) * 0.5F : var5;
         ModuleStateHelper.handle(var1, var2, var3, var13, var6 + var7 * var11, var7, var4.size(), var12, var8);
      }
   }

   private void handle(RoundedRectRenderer var1, ServerItemCatalog.RuntimeDataRecord var2, boolean var3, float var4, GuiMetrics var5, ThemeColors var6) {
      if (var2.visible()) {
         ModuleStateHelper.process(var1, var5, var6, var2.x(), var2.y(), var2.w(), var2.h(), var2.thumbY(), var2.thumbH(), var4, var3 ? 1.0F : 0.0F);
      }
   }

   private void handle(List<AnimatedUiElement> var1, AutoBuy var2, ServerItemCatalog.Bounds var3, GuiMetrics var4) {
      ServerItemCatalog.CachedDataRecord var5 = this.handle(var3, var4);
      float var6 = var5.stripH();
      float var7 = var5.modeX();
      float var8 = var5.toggleW();
      float var9 = var5.toggleX();
      float var10 = var5.gap();
      float var11 = var5.tabBtnSize();
      float var12 = var5.chipW();
      float var13 = var7;

      for (String var17 : output) {
         float var18 = var13;
         var1.add(AnimatedUiElement.handle().handle(0).handle(var18).process(var3.y()).compute(var12).resolve(var6).handle(var3x -> {
            this.handle("mode:" + var17, var3x);
            var2.latest.state = var17;
            var2.latest.current = var2.latest.config.indexOf(var17);
            this.process(this.pointEncode, 0.0F);
            var3x.check(false);
            var3x.scheduleAnimation();
         }).handle());
         var13 += var12 + var10;
      }

      var1.add(AnimatedUiElement.handle().handle(0).handle(var13).process(var3.y()).compute(var11).resolve(var11).handle(var1x -> {
         this.handle("tab:catalog_tab", var1x);
         this.timerRender = 0;
         var1x.scheduleAnimation();
      }).handle());
      var13 += var11 + var10;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var13).process(var3.y()).compute(var11).resolve(var11).handle(var1x -> {
         this.handle("tab:history_tab", var1x);
         this.timerRender = 1;
         this.providerFetch.resolve(0.0);
         this.providerFetch.process();
         var1x.scheduleAnimation();
      }).handle());
      var13 += var11 + var10;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var13).process(var3.y()).compute(var11).resolve(var11).handle(var1x -> {
         this.handle("tab:cloud_tab", var1x);
         this.timerRender = 2;
         var1x.scheduleAnimation();
      }).handle());
      if (var5.showReparse()) {
         var1.add(AnimatedUiElement.handle().handle(0).handle(var5.reparseX()).process(var3.y()).compute(var5.reparseToggleW()).resolve(var6).handle(var2x -> {
            this.handle("reparse:toggle", var2x);
            var2.vectorMatch.process(!var2.vectorMatch.compute());
            var2x.check(false);
            var2x.scheduleAnimation();
         }).handle());
         var1.add(
            AnimatedUiElement.handle().handle(0).handle(var5.reparseSliderX()).process(var3.y()).compute(var5.reparseSliderW()).resolve(var6).handle(var4x -> {
               this.handle("reparse:slider", var4x);
               float var5x = var5.reparseSliderX() + var4.handle(8.0F);
               float var6x = Math.max(var4.handle(28.0F), var5.reparseSliderW() - var4.handle(16.0F));
               this.handle(var2, var4x.sampleLayer(), var5x, var6x);
               var4x.handle(var2.itemProject);
               var4x.checkFrame(var5x);
               var4x.collectModule(var6x);
               var4x.check(false);
               var4x.scheduleAnimation();
            }).handle()
         );
      }

      float var21 = var4.handle(4.0F);
      float var22 = (var8 - var21) * 0.5F;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var9).process(var3.y()).compute(var22).resolve(var6).handle(var2x -> {
         this.handle("toggle:inactive", var2x);
         if (var2.enabled) {
            var2.toggle();
         }

         var2x.check(false);
         var2x.scheduleAnimation();
      }).handle());
      var1.add(AnimatedUiElement.handle().handle(0).handle(var9 + var22 + var21).process(var3.y()).compute(var22).resolve(var6).handle(var2x -> {
         this.handle("toggle:active", var2x);
         if (!var2.enabled) {
            var2.toggle();
         }

         var2x.check(false);
         var2x.scheduleAnimation();
      }).handle());
   }

   private void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, AutoBuy var3, ServerItemCatalog.Bounds var4, GuiMetrics var5) {
      this.handle(var1, var4, var5);
      List var6 = this.process(var3, this.eventAttach.state);
      ServerItemCatalog.RuntimeDataRecord var7 = this.handle(
         var4.catalogScrollbarX(),
         var4.catalogViewportY(),
         var4.scrollbarW(),
         var4.catalogViewportH(),
         this.handle(var3, var4, var5),
         this.pointEncode.prepare(),
         var5
      );
      float var8 = this.pointEncode.prepare();
      int var9 = this.apply(var4, var5);
      float var10 = this.handle(var5);
      float var11 = this.process(var5);
      float var12 = this.compute(var5);
      int var13 = Math.max(1, (var6.size() + var9 - 1) / var9);
      int var14 = Math.max(0, (int)Math.floor(-var8 / (var11 + var12)) - 1);
      int var15 = Math.min(var13, (int)Math.ceil((var4.catalogViewportH() - var8) / (var11 + var12)) + 1);

      for (int var16 = var14; var16 < var15; var16++) {
         for (int var17 = 0; var17 < var9; var17++) {
            int var18 = var16 * var9 + var17;
            if (var18 >= var6.size()) {
               break;
            }

            ServerItemCatalog.DataRecord var19 = (ServerItemCatalog.DataRecord)var6.get(var18);
            float var20 = var4.catalogViewportX() + var17 * (var10 + var12);
            float var21 = var4.catalogViewportY() + var8 + var16 * (var11 + var12);
            if (!(var21 > var4.catalogViewportY() + var4.catalogViewportH()) && !(var21 + var11 < var4.catalogViewportY())) {
               float var22 = var2.handle(UiAnimationKeys.handle(var19.key()));
               if (!(var22 < 0.98F)) {
                  var1.add(AnimatedUiElement.handle().handle(0).handle(var20).process(var21).compute(var10).resolve(var11).handle(var2x -> {
                     boolean var3x = !AutoBuy.colorMeasure.containsKey(var19.key());
                     this.handle("catalog:" + var19.key(), var2x);
                     AutoBuy.colorMeasure.putIfAbsent(var19.key(), 0L);
                     AutoBuy.profileInvoke.remove(var19.key());
                     if (var3x) {
                        var2x.scheduleSource().remove(UiAnimationKeys.process(var19.key()));
                     }

                     var2x.check(false);
                     var2x.handle(this.prepare(var19.key()));
                     var2x.scheduleAnimation();
                  }).handle());
                  var1.add(
                     AnimatedUiElement.handle()
                        .handle(1)
                        .handle(var20)
                        .process(var21)
                        .compute(var10)
                        .resolve(var11)
                        .update(var4.catalogViewportX())
                        .apply(var4.catalogViewportY())
                        .execute(var4.catalogViewportW())
                        .prepare(var4.catalogViewportH())
                        .handle(var2x -> {
                           this.handle("catalog:" + var19.key(), var2x);
                           this.process(var19.key(), var2x);
                        })
                        .handle()
                  );
               }
            }
         }
      }

      this.handle(var1, "catalog", var7, var5);
   }

   private void handle(List<AnimatedUiElement> var1, ServerItemCatalog.Bounds var2, GuiMetrics var3) {
      float var4 = this.execute(var2, var3);
      float var5 = this.prepare(var2, var3);
      float var6 = this.check(var2, var3);
      float var7 = this.resolve(var3);
      if (this.eventAttach.state != null && !this.eventAttach.state.isEmpty()) {
         var1.add(
            AnimatedUiElement.handle()
               .handle(0)
               .handle(var4 + var6 - var3.handle(34.0F))
               .process(var5)
               .compute(var3.handle(34.0F))
               .resolve(var7)
               .handle(var1x -> {
                  this.eventAttach.state = "";
                  var1x.handle((StringSetting)null);
                  this.compute();
               })
               .handle()
         );
      }

      var1.add(AnimatedUiElement.handle().handle(0).handle(var4).process(var5).compute(var6).resolve(var7).handle(var1x -> {
         var1x.check(false);
         var1x.handle(this.eventAttach);
      }).handle());
   }

   private void handle(List<AnimatedUiElement> var1, ModernClickGuiState var2, ServerItemCatalog.Bounds var3, GuiMetrics var4) {
      this.process(var1, var3, var4);
      List var5 = this.process();
      ServerItemCatalog.RuntimeDataRecord var6 = this.handle(
         var3.rulesScrollbarX(), var3.rulesViewportY(), var3.scrollbarW(), var3.rulesViewportH(), this.compute(var3, var4), this.animator.prepare(), var4
      );
      float var7 = this.animator.prepare();
      float var8 = this.update(var4);
      float var9 = this.apply(var4);
      float var10 = var4.handle(6.0F);
      float var11 = var3.rulesViewportX() + var10;
      float var12 = var3.rulesViewportW() - var10 * 2.0F;
      float var13 = var3.rulesViewportY() + var7;

      for (int var14 = 0; var14 < var5.size(); var14++) {
         String var15 = (String)var5.get(var14);
         float var16 = this.process(var2, var15);
         float var17 = this.handle(var15, var4, var16);
         if (!(var13 > var3.rulesViewportY() + var3.rulesViewportH()) && !(var13 + var17 < var3.rulesViewportY())) {
            float var18 = var2.handle(UiAnimationKeys.process(var15));
            if (var18 < 0.98F) {
               var13 += var17 + var9;
            } else {
               boolean var19 = this.execute(var15);
               ServerItemCatalog.PrimaryDataRecord var20 = this.handle(var11, var13, var12, var8, var19, var4);
               var1.add(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var20.deleteX())
                     .process(var20.controlY())
                     .compute(var20.deleteW())
                     .resolve(var20.controlH())
                     .handle(var2x -> {
                        this.handle("delete:" + var15, var2x);
                        this.process(var15, var2x);
                     })
                     .handle()
               );
               if (var19) {
                  var1.add(
                     AnimatedUiElement.handle()
                        .handle(0)
                        .handle(var20.settingsX())
                        .process(var20.controlY())
                        .compute(var20.settingsW())
                        .resolve(var20.controlH())
                        .handle(var2x -> {
                           this.handle("settings:" + var15, var2x);
                           this.positionAdvance = var15.equals(this.positionAdvance) ? null : var15;
                           var2x.check(false);
                           if (!var15.equals(this.execute(var2x))) {
                              var2x.handle((StringSetting)null);
                           }
                        })
                        .handle()
                  );
               }

               var1.add(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var20.statusX())
                     .process(var20.controlY())
                     .compute(var20.statusW())
                     .resolve(var20.controlH())
                     .handle(var2x -> {
                        this.handle("status:" + var15, var2x);
                        if (AutoBuy.profileInvoke.contains(var15)) {
                           AutoBuy.profileInvoke.remove(var15);
                        } else {
                           AutoBuy.profileInvoke.add(var15);
                        }

                        var2x.check(false);
                        var2x.scheduleAnimation();
                     })
                     .handle()
               );
               var1.add(
                  AnimatedUiElement.handle()
                     .handle(0)
                     .handle(var20.priceX())
                     .process(var20.controlY())
                     .compute(var20.priceW())
                     .resolve(var20.controlH())
                     .handle(var2x -> {
                        this.handle("price:" + var15, var2x);
                        var2x.check(false);
                        var2x.handle(this.prepare(var15));
                     })
                     .handle()
               );
               if (var15.equals(this.positionAdvance) && this.execute(var15) && var16 > 0.95F) {
                  this.handle(var1, var15, var11, var13 + var8 + var4.handle(6.0F) * var16, var12, this.handle(var15, var4) * var16, var4);
               }

               var13 += var17 + var9;
            }
         } else {
            var13 += var17 + var9;
         }
      }

      this.handle(var1, "rules", var6, var4);
   }

   private void handle(List<AnimatedUiElement> var1, String var2, float var3, float var4, float var5, float var6, GuiMetrics var7) {
      float var8 = var3 + var7.handle(16.0F);
      float var9 = var4 + var7.handle(58.0F);
      float var10 = var5 - var7.handle(32.0F);
      var1.add(
         AnimatedUiElement.handle()
            .handle(0)
            .handle(var8 - var7.handle(6.0F))
            .process(var9 + var7.handle(12.0F))
            .compute(var10 + var7.handle(12.0F))
            .resolve(var7.handle(24.0F))
            .handle(var4x -> {
               this.handle("durability:" + var2, var4x);
               this.handle(var2, var4x.sampleLayer(), var8, var10);
               this.handle(var2, var4x.sampleLayer(), var4x);
            })
            .handle()
      );
      ServerItemCatalog.PersistentDataRecord var11 = this.compute(var2);
      float var12 = var4 + var7.handle(128.0F);
      float var13 = (var5 - var7.handle(40.0F)) * 0.5F;
      float var14 = var7.handle(8.0F);
      float var15 = var7.handle(22.0F);

      for (int var16 = 0; var16 < var11.enchantments().size(); var16++) {
         ServerItemCatalog.FallbackDataRecord var17 = var11.enchantments().get(var16);
         this.handle(
            var1, var2, var17.key(), var3 + var7.handle(16.0F) + var16 % 2 * (var13 + var14), var12 + var16 / 2 * (var15 + var7.handle(4.0F)), var13, var15
         );
      }
   }

   private void process(List<AnimatedUiElement> var1, ServerItemCatalog.Bounds var2, GuiMetrics var3) {
      AutoBuy var4 = AutoBuy.source;
      if (var4 != null) {
         ServerItemCatalog.SecondaryDataRecord var5 = this.process(var2, var3);
         if (var5.visible()) {
            var1.add(
               AnimatedUiElement.handle().handle(0).handle(var5.chipX()).process(var5.chipY()).compute(var5.chipW()).resolve(var5.chipH()).handle(var1x -> {
                  var4.eventAttach.process(!var4.eventAttach.compute());
                  var1x.check(false);
                  var1x.scheduleAnimation();
               }).handle()
            );
            var1.add(
               AnimatedUiElement.handle().handle(0).handle(var5.fixX()).process(var5.chipY()).compute(var5.fixW()).resolve(var5.chipH()).handle(var1x -> {
                  var4.serverRead.process(!var4.serverRead.compute());
                  var1x.check(false);
                  var1x.scheduleAnimation();
               }).handle()
            );
            var1.add(
               AnimatedUiElement.handle().handle(0).handle(var5.statX()).process(var5.chipY()).compute(var5.statW()).resolve(var5.chipH()).handle(var1x -> {
                  var4.positionAdvance.process(!var4.positionAdvance.compute());
                  var1x.check(false);
                  var1x.scheduleAnimation();
               }).handle()
            );
         }
      }
   }

   private void handle(List<AnimatedUiElement> var1, String var2, String var3, float var4, float var5, float var6, float var7) {
      var1.add(AnimatedUiElement.handle().handle(0).handle(var4).process(var5).compute(var6).resolve(var7).handle(var3x -> {
         this.handle("check:" + var2 + ":" + var3, var3x);
         AutoBuy.handle(var2, var3, !AutoBuy.process(var2, var3));
         var3x.check(false);
         var3x.scheduleAnimation();
      }).handle());
   }

   private void handle(String var1, float var2, float var3, float var4) {
      int var5 = AutoBuy.check(var1);
      int var6 = AutoBuy.onTick(var1);
      float var7 = var3 + var4 * var5 / 100.0F;
      float var8 = var3 + var4 * var6 / 100.0F;
      this.frameCheck = var1;
      this.moduleCollect = Math.abs(var2 - var8) < Math.abs(var2 - var7);
      this.providerClose = var3;
      this.presetSave = Math.max(1.0F, var4);
   }

   private void handle(String var1, float var2, ModernClickGuiState var3) {
      int var4 = (int)NumericTransform.compute(this.process((var2 - this.providerClose) / this.presetSave, 0.0F, 1.0F) * 100.0F, 0.0F);
      int var5 = AutoBuy.check(var1);
      int var6 = AutoBuy.onTick(var1);
      if (this.moduleCollect) {
         var6 = Math.max(var5, var4);
      } else {
         var5 = Math.min(var6, var4);
      }

      AutoBuy.handle(var1, var5, var6);
      var3.check(false);
      var3.scheduleAnimation();
   }

   private void compute(List<AnimatedUiElement> var1, ServerItemCatalog.Bounds var2, GuiMetrics var3) {
      float var4 = this.resolve(var2, var3);
      float var5 = var3.handle(42.0F);
      ServerItemCatalog.RuntimeDataRecord var6 = this.handle(
         var2.x() + var2.width() - var3.handle(10.0F),
         var2.panelY() + var5,
         var2.scrollbarW(),
         var2.panelH() - var5 - var3.handle(10.0F),
         var4,
         this.source.prepare(),
         var3
      );
      this.handle(var1, "history", var6, var3);
      float var7 = var2.panelY() + var3.handle(14.0F);
      float var8 = var3.handle(64.0F);
      float var9 = var3.handle(20.0F);
      float var10 = var2.x() + var2.width() - var3.handle(16.0F) - var8;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var10).process(var7).compute(var8).resolve(var9).handle(var0 -> {
         AutoBuy.timerRender.clear();
         var0.scheduleAnimation();
      }).handle());
      float var11 = var3.handle(42.0F);
      float var12 = var3.handle(6.0F);
      float var13 = var2.x() + var3.handle(16.0F);
      float var14 = var2.panelY() + var5;
      float var15 = var2.width() - var3.handle(20.0F) - var2.scrollbarW();
      float var16 = var2.panelH() - var5 - var3.handle(10.0F);
      float var17 = this.source.prepare();
      float var18 = var15 - var3.handle(36.0F);

      for (int var19 = 0; var19 < AutoBuy.timerRender.size(); var19++) {
         float var20 = var14 + var17 + var19 * (var11 + var12);
         if (!(var20 > var14 + var16) && !(var20 + var11 < var14)) {
            float var21 = var3.handle(26.0F);
            float var22 = var13 + var18 + var3.handle(6.0F);
            int var23 = var19;
            var1.add(AnimatedUiElement.handle().handle(0).handle(var22).process(var20 + (var11 - var21) * 0.5F).compute(var21).resolve(var21).handle(var1x -> {
               if (var23 < AutoBuy.timerRender.size()) {
                  AutoBuy.timerRender.remove(var23);
                  var1x.scheduleAnimation();
               }
            }).handle());
         }
      }
   }

   private void process(List<AnimatedUiElement> var1, AutoBuy var2, ServerItemCatalog.Bounds var3, GuiMetrics var4) {
      float var5 = this.update(var3, var4);
      float var6 = var4.handle(62.0F);
      ServerItemCatalog.RuntimeDataRecord var7 = this.handle(
         var3.x() + var3.width() - var4.handle(10.0F),
         var3.panelY() + var6,
         var3.scrollbarW(),
         var3.panelH() - var6 - var4.handle(10.0F),
         var5,
         this.target.prepare(),
         var4
      );
      this.handle(var1, "cloud", var7, var4);
      float var8 = var3.panelY() + var4.handle(14.0F);
      float var9 = var4.handle(28.0F);
      float var10 = var4.handle(8.0F);
      float var11 = var3.x() + var3.width() - var4.handle(16.0F) - var9;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var11).process(var8).compute(var9).resolve(var9).handle(var1x -> {
         try {
            File var2x = var2.save();
            String var3x = System.getProperty("os.name").toLowerCase();
            if (var3x.contains("win")) {
               Runtime.getRuntime().exec(new String[]{"explorer", var2x.getAbsolutePath()});
            } else if (var3x.contains("mac")) {
               Runtime.getRuntime().exec(new String[]{"open", var2x.getAbsolutePath()});
            } else {
               Runtime.getRuntime().exec(new String[]{"xdg-open", var2x.getAbsolutePath()});
            }
         } catch (Exception var4x) {
         }
      }).handle());
      var11 -= var9 + var10;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var11).process(var8).compute(var9).resolve(var9).handle(var2x -> this.handle(var2)).handle());
      var11 -= var9 + var10;
      var1.add(AnimatedUiElement.handle().handle(0).handle(var11).process(var8).compute(var9).resolve(var9).handle(var2x -> {
         String var3x = "Default";
         String var4x = var3x;
         int var5x = 1;

         for (File var6x = var2.save(); new File(var6x, var4x + ".json").exists(); var5x++) {
            var4x = var3x + var5x;
         }

         var2.update(var4x);
         this.enabled = var4x;
         this.handle(var2);
      }).handle());
      float var12 = var4.handle(58.0F);
      float var13 = var4.handle(8.0F);
      float var14 = var3.x() + var4.handle(16.0F);
      float var15 = var3.panelY() + var6;
      float var16 = var3.width() - var4.handle(25.0F) - var3.scrollbarW();
      float var17 = var3.panelH() - var6 - var4.handle(10.0F);
      float var18 = this.target.prepare();
      float var19 = var16 - var4.handle(24.0F);

      for (int var20 = 0; var20 < this.selection.size(); var20++) {
         ServerItemCatalog.Snapshot var21 = this.selection.get(var20);
         float var22 = var15 + var18 + var20 * (var12 + var13);
         if (!(var22 > var15 + var17) && !(var22 + var12 < var15)) {
            float var23 = var4.handle(26.0F);
            float var24 = var4.handle(8.0F);
            float var25 = var14 + var19 - var4.handle(12.0F) - var23;
            var1.add(AnimatedUiElement.handle().handle(0).handle(var25).process(var22 + (var12 - var23) * 0.5F).compute(var23).resolve(var23).handle(var3x -> {
               var2.execute(var21.name);
               if (this.enabled.equals(var21.name)) {
                  this.enabled = "";
               }

               this.handle(var2);
            }).handle());
            var25 -= var23 + var24;
            var1.add(AnimatedUiElement.handle().handle(0).handle(var25).process(var22 + (var12 - var23) * 0.5F).compute(var23).resolve(var23).handle(var3x -> {
               var2.apply(var21.name);
               this.enabled = var21.name;
            }).handle());
            float var26 = var22 + var4.handle(12.0F);
            float var27 = ModuleStateHelper.handle(FontRegistry.config, var21.name, 13.0F);
            var1.add(
               AnimatedUiElement.handle()
                  .handle(0)
                  .handle(var14 + var4.handle(12.0F))
                  .process(var26 - var4.handle(4.0F))
                  .compute(var27 + var4.handle(24.0F))
                  .resolve(var4.handle(18.0F))
                  .handle(var2x -> var2x.handle(this.handler.computeIfAbsent(var21.name, var0 -> new StringSetting("Name", var0))))
                  .handle()
            );
         }
      }
   }

   private void handle(List<AnimatedUiElement> var1, String var2, ServerItemCatalog.RuntimeDataRecord var3, GuiMetrics var4) {
      if (var3.visible()) {
         float var5 = var4.handle(5.0F);
         var1.add(
            AnimatedUiElement.handle().handle(0).handle(var3.x() - var5).process(var3.y()).compute(var3.w() + var5 * 2.0F).resolve(var3.h()).handle(var3x -> {
               this.handle(var2, var3x.sendWorld(), var3);
               var3x.check(false);
               if (!this.handle(var3x.filterEntity()) && this.update(var3x) == null) {
                  var3x.handle((StringSetting)null);
               }
            }).handle()
         );
      }
   }

   private ServerItemCatalog.RuntimeDataRecord handle(float var1, float var2, float var3, float var4, float var5, float var6, GuiMetrics var7) {
      if (!(var5 <= 0.5F) && !(var4 <= var7.handle(8.0F))) {
         float var8 = Math.max(var7.handle(34.0F), var4 * (var4 / (var4 + var5)));
         var8 = Math.min(var4, var8);
         float var9 = Math.max(0.0F, var4 - var8);
         float var10 = var5 <= 0.001F ? 0.0F : this.process(-var6 / var5, 0.0F, 1.0F);
         float var11 = var2 + var9 * var10;
         return new ServerItemCatalog.RuntimeDataRecord(var1, var2, var3, var4, var5, var11, var8, true);
      } else {
         return ServerItemCatalog.RuntimeDataRecord.hidden(var1, var2, var3, var4);
      }
   }

   private void handle(String var1, float var2, ServerItemCatalog.RuntimeDataRecord var3) {
      if (var3.visible()) {
         this.rendererScan = "catalog".equals(var1);
         this.sourceBuild = "rules".equals(var1);
         this.outputCollapse = "history".equals(var1);
         this.profileInvoke = "cloud".equals(var1);
         if (var2 >= var3.thumbY() && var2 <= var3.thumbY() + var3.thumbH()) {
            this.sourceSchedule = var2 - var3.thumbY();
         } else {
            this.sourceSchedule = var3.thumbH() * 0.5F;
         }

         this.process(var1, var2, var3);
      }
   }

   private void process(String var1, float var2, ServerItemCatalog.RuntimeDataRecord var3) {
      if (var3.visible()) {
         float var4 = var3.travel();
         float var5 = this.process(var2 - this.sourceSchedule, var3.y(), var3.y() + var4);
         float var6 = var4 <= 0.001F ? 0.0F : (var5 - var3.y()) / var4;
         float var7 = -var3.maxScroll() * var6;
         if ("catalog".equals(var1)) {
            this.process(this.pointEncode, var7);
         } else if ("rules".equals(var1)) {
            this.process(this.animator, var7);
         } else if ("history".equals(var1)) {
            this.process(this.source, var7);
         } else if ("cloud".equals(var1)) {
            this.process(this.target, var7);
         }
      }
   }

   private ServerItemCatalog.Bounds handle(ModulePlacement var1, GuiMetrics var2) {
      float var3 = var1.process() + var2.handle(16.0F);
      float var4 = var2.handle(5.0F);
      float var5 = var1.compute() + var2.encodePoint() + var2.handle(10.0F) + var4;
      float var6 = var1.resolve() - var2.handle(32.0F);
      float var7 = Math.max(0.0F, var1.apply() - var2.handle(20.0F) - var4);
      float var8 = var2.handle(34.0F);
      float var9 = var2.handle(8.0F);
      float var10 = var5 + var8 + var9;
      float var11 = Math.max(var2.handle(80.0F), var7 - var8 - var9);
      float var12 = var2.handle(10.0F);
      float var13 = Math.min(var2.handle(300.0F), var6 * 0.44F);
      float var14 = Math.min(var2.handle(180.0F), var6 * 0.46F);
      float var15 = Math.min(var2.handle(220.0F), var6 * 0.46F);
      float var16 = Math.max(var2.handle(120.0F), var6 - var15 - var12);
      var13 = Math.max(var14, Math.min(var13, var16));
      if (var13 + var12 + var2.handle(120.0F) > var6) {
         var13 = Math.max(var2.handle(120.0F), var6 - var2.handle(120.0F) - var12);
      }

      float var17 = Math.max(var2.handle(120.0F), var6 - var13 - var12);
      float var18 = var3 + var13 + var12;
      float var19 = var2.handle(10.0F);
      float var20 = var2.handle(82.0F);
      float var21 = var2.handle(48.0F);
      float var22 = Math.max(var2.handle(5.5F), 4.0F);
      float var23 = var3 + var19;
      float var24 = var10 + var20;
      float var25 = Math.max(var2.handle(60.0F), var13 - var19 * 2.0F - var22 - var2.handle(5.0F));
      float var26 = Math.max(var2.handle(30.0F), var11 - var20 - var2.handle(12.0F));
      float var27 = var18 + var19;
      float var28 = var10 + var21;
      float var29 = Math.max(var2.handle(120.0F), var17 - var19 * 2.0F - var22 - var2.handle(5.0F));
      float var30 = Math.max(var2.handle(30.0F), var11 - var21 - var2.handle(12.0F));
      return new ServerItemCatalog.Bounds(
         var3,
         var5,
         var6,
         var7,
         var3,
         var18,
         var13,
         var17,
         var10,
         var11,
         var23,
         var24,
         var25,
         var26,
         var23 + var25 + var2.handle(5.0F),
         var27,
         var28,
         var29,
         var30,
         var27 + var29 + var2.handle(5.0F),
         var22
      );
   }

   private List<ServerItemCatalog.DataRecord> process(AutoBuy var1) {
      return this.process(var1, "");
   }

   private List<ServerItemCatalog.DataRecord> process(AutoBuy var1, String var2) {
      ArrayList<ServerItemCatalog.DataRecord> var3 = new ArrayList<>();
      if (var1 != null && var1.latest.process("HolyWorld")) {
         for (HolyWorldHelper.SecondaryDataRecord var10 : HolyWorldHelper.handle()) {
            var3.add(new ServerItemCatalog.DataRecord(var10.key(), var10.label(), new ItemStack(var10.item()), true));
         }
      } else {
         for (String var5 : active) {
            var3.add(new ServerItemCatalog.DataRecord(var5, var5, this.resolve(var5), true));
         }
      }

      var3.addAll(handle());
      String var9 = var2 == null ? "" : var2.trim().toLowerCase(Locale.ROOT);
      if (var9.isEmpty()) {
         return var3;
      }

      ArrayList<ServerItemCatalog.DataRecord> var11 = new ArrayList<>();

      for (ServerItemCatalog.DataRecord var7 : var3) {
         if (var7.label().toLowerCase(Locale.ROOT).contains(var9) || var7.key().toLowerCase(Locale.ROOT).contains(var9)) {
            var11.add(var7);
         }
      }

      return var11;
   }

   private static List<ServerItemCatalog.DataRecord> handle() {
      if (mode != null) {
         return mode;
      }

      ArrayList var0 = new ArrayList();

      for (Item var2 : Registries.ITEM) {
         if (var2 != Items.AIR) {
            Identifier var3 = Registries.ITEM.getId(var2);
            if (var3 != null && "minecraft".equals(var3.getNamespace())) {
               ItemStack var4 = var2.getDefaultStack();
               var0.add(new ServerItemCatalog.DataRecord(var3.toString(), var4.getName().getString(), var4, false));
            }
         }
      }

      var0.sort(Comparator.comparing(ServerItemCatalog.DataRecord::label, String.CASE_INSENSITIVE_ORDER));
      mode = List.copyOf(var0);
      return mode;
   }

   private ServerItemCatalog.DataRecord process(String var1) {
      if (HolyWorldHelper.handle(var1)) {
         HolyWorldHelper.SecondaryDataRecord var5 = HolyWorldHelper.compute(var1);
         if (var5 != null) {
            ItemStack var6 = HolyWorldHelper.apply(var5.key());
            if (var6.isEmpty()) {
               var6 = new ItemStack(var5.item());
            }

            return new ServerItemCatalog.DataRecord(var5.key(), var5.label(), var6, true);
         } else {
            return new ServerItemCatalog.DataRecord(var1 == null ? "" : var1, var1 == null ? "" : var1, ItemStack.EMPTY, true);
         }
      } else {
         if (active.contains(var1)) {
            return new ServerItemCatalog.DataRecord(var1, var1, this.resolve(var1), true);
         }

         if (var1 != null && var1.startsWith("minecraft:")) {
            Identifier var2 = Identifier.tryParse(var1);
            if (var2 != null) {
               Item var3 = (Item)Registries.ITEM.get(var2);
               if (var3 != Items.AIR) {
                  ItemStack var4 = var3.getDefaultStack();
                  return new ServerItemCatalog.DataRecord(var1, var4.getName().getString(), var4, false);
               }
            }
         }

         return new ServerItemCatalog.DataRecord(var1 == null ? "" : var1, var1 == null ? "" : var1, ItemStack.EMPTY, true);
      }
   }

   private ServerItemCatalog.PersistentDataRecord compute(String var1) {
      HolyWorldHelper.SecondaryDataRecord var2 = HolyWorldHelper.compute(var1);
      if (var2 != null) {
         ArrayList var7 = new ArrayList();

         for (String var5 : var2.enchantments()) {
            String var6 = HolyWorldHelper.update(var5);
            if (!var6.isBlank()) {
               var7.add(new ServerItemCatalog.FallbackDataRecord(var6, this.apply(var5)));
            }
         }

         return new ServerItemCatalog.PersistentDataRecord(var7);
      } else {
         ItemStack var3 = this.update(var1);
         return !var3.isEmpty() ? new ServerItemCatalog.PersistentDataRecord(this.handle(var3)) : new ServerItemCatalog.PersistentDataRecord(List.of());
      }
   }

   private ItemStack resolve(String var1) {
      ItemStack var2 = this.update(var1);
      if (!var2.isEmpty()) {
         return var2;
      }

      ItemStack var3 = ChaosSphereHelper.handle(var1);
      return var3 == null ? ItemStack.EMPTY : var3;
   }

   private ItemStack update(String var1) {
      if (var1 == null) {
         return ItemStack.EMPTY;
      }

      return switch (var1) {
         case "Шлем Крушителя" -> EnchantedItemDetector.handle();
         case "Нагрудник Крушителя" -> EnchantedItemDetector.process();
         case "Поножи Крушителя" -> EnchantedItemDetector.compute();
         case "Ботинки Крушителя" -> EnchantedItemDetector.resolve();
         case "Меч Крушителя" -> EnchantedItemDetector.update();
         case "Кирка Крушителя" -> EnchantedItemDetector.apply();
         case "Арбалет Крушителя" -> EnchantedItemDetector.execute();
         case "Трезубец Крушителя" -> EnchantedItemDetector.prepare();
         case "Булава Крушителя" -> EnchantedItemDetector.check();
         default -> ItemStack.EMPTY;
      };
   }

   private List<ServerItemCatalog.FallbackDataRecord> handle(ItemStack var1) {
      ItemEnchantmentsComponent var2 = (ItemEnchantmentsComponent)var1.get(DataComponentTypes.ENCHANTMENTS);
      if (var2 != null && !var2.isEmpty()) {
         ArrayList var3 = new ArrayList();

         for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var5 : var2.getEnchantmentEntries()) {
            String var6 = this.handle((RegistryEntry<Enchantment>)var5.getKey());
            if (!var6.isBlank()) {
               String var7 = var6 + ":" + var5.getIntValue();
               String var8 = HolyWorldHelper.update(var7);
               if (!var8.isBlank()) {
                  var3.add(new ServerItemCatalog.FallbackDataRecord(var8, this.apply(var7)));
               }
            }
         }

         return var3;
      } else {
         return List.of();
      }
   }

   private String handle(RegistryEntry<Enchantment> var1) {
      return var1.getKey().map(var0 -> var0.getValue().toString()).orElse("");
   }

   private String apply(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String[] var2 = var1.split(":");
         String var3 = var2.length >= 2 ? var2[1] : var1.replace("minecraft:", "");
         String var4 = current.getOrDefault(var3, var3.replace('_', ' '));
         return var2.length >= 3 ? var4 + " " + var2[2] : var4;
      } else {
         return "";
      }
   }

   private boolean execute(String var1) {
      HolyWorldHelper.SecondaryDataRecord var2 = HolyWorldHelper.compute(var1);
      if (var2 != null) {
         return AutoBuy.handle(var2.item());
      }

      ServerItemCatalog.DataRecord var3 = this.process(var1);
      return var3 != null && !var3.stack().isEmpty() && AutoBuy.handle(var3.stack().getItem());
   }

   private List<String> process() {
      return new ArrayList<>(AutoBuy.colorMeasure.keySet());
   }

   private StringSetting prepare(String var1) {
      return this.serverRead.computeIfAbsent(var1, var1x -> new StringSetting("Макс. цена", this.check(var1x)));
   }

   private String check(String var1) {
      long var2 = AutoBuy.colorMeasure.getOrDefault(var1, 0L);
      return var2 <= 0L ? "" : Long.toString(var2);
   }

   private void apply(ModernClickGuiState var1) {
      this.serverRead.entrySet().removeIf(var1x -> !AutoBuy.colorMeasure.containsKey(var1x.getKey()) && var1.filterEntity() != var1x.getValue());
   }

   private boolean handle(StringSetting var1) {
      return var1 != null && this.serverRead.containsValue(var1);
   }

   private String execute(ModernClickGuiState var1) {
      StringSetting var2 = var1.filterEntity();
      if (var2 == null) {
         return null;
      }

      for (Entry var4 : this.serverRead.entrySet()) {
         if (var4.getValue() == var2) {
            return (String)var4.getKey();
         }
      }

      return null;
   }

   private void handle(String var1, String var2, ModernClickGuiState var3) {
      AutoBuy.colorMeasure.put(var1, MaxPriceSetting.process(var2));
      var3.scheduleAnimation();
   }

   private long onTick(String var1) {
      return MaxPriceSetting.process(var1);
   }

   private void process(String var1, ModernClickGuiState var2) {
      StringSetting var3 = this.serverRead.remove(var1);
      if (var2.filterEntity() == var3) {
         var2.handle((StringSetting)null);
      }

      if (var1 != null && var1.equals(this.positionAdvance)) {
         this.positionAdvance = null;
      }

      AutoBuy.colorMeasure.remove(var1);
      AutoBuy.animationSchedule.remove(var1);
      AutoBuy.rendererScan.remove(var1);
      AutoBuy.sourceBuild.remove(var1);
      AutoBuy.profileInvoke.remove(var1);
      AutoBuy.outputCollapse.remove(var1);
      var2.check(false);
      var2.scheduleAnimation();
   }

   private void handle(
      RoundedRectRenderer var1,
      DrawContext var2,
      ServerItemCatalog.DataRecord var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      if (var3 != null && !(var7 < 0.05F)) {
         if (!(var4 + var6 <= var8) && !(var5 + var6 <= var9) && !(var4 >= var8 + var10) && !(var5 >= var9 + var11)) {
            ItemStack var12 = var3.custom() ? ChaosSphereHelper.handle(var3.key()) : var3.stack();
            if (var12 != null && !var12.isEmpty()) {
               float var13 = var6 / 16.0F;
               float var14 = var7 < 0.95F ? var7 : Math.min(1.0F, 0.5F + 0.5F * var7);
               if (var14 >= 0.999F) {
                  ItemStackOverlayRenderer.handle(var1, var12, var4, var5, var13, 0, false, 0);
               } else {
                  float var15 = var4 + var6 * 0.5F;
                  float var16 = var5 + var6 * 0.5F;
                  var1.handle(var14, var15, var16);

                  try {
                     ItemStackOverlayRenderer.handle(var1, var12, var4, var5, var13, 0, false, 0);
                  } finally {
                     var1.check();
                  }
               }
            }
         }
      }
   }

   private float handle(AutoBuy var1, ServerItemCatalog.Bounds var2, GuiMetrics var3) {
      int var4 = this.apply(var2, var3);
      int var5 = Math.max(1, (this.process(var1, this.eventAttach.state).size() + var4 - 1) / var4);
      float var6 = var5 * this.process(var3) + Math.max(0, var5 - 1) * this.compute(var3);
      return Math.max(0.0F, var6 - var2.catalogViewportH());
   }

   private float compute(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      return this.handle(var1, var2, null);
   }

   private float handle(ServerItemCatalog.Bounds var1, GuiMetrics var2, ModernClickGuiState var3) {
      List var4 = this.process();
      float var5 = 0.0F;

      for (int var6 = 0; var6 < var4.size(); var6++) {
         var5 += this.handle((String)var4.get(var6), var2, var3 == null ? this.select((String)var4.get(var6)) : this.process(var3, (String)var4.get(var6)));
         if (var6 < var4.size() - 1) {
            var5 += this.apply(var2);
         }
      }

      return Math.max(0.0F, var5 - var1.rulesViewportH());
   }

   private float resolve(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      float var3 = var2.handle(42.0F);
      float var4 = var2.handle(6.0F);
      float var5 = var2.handle(42.0F);
      float var6 = var1.panelH() - var5 - var2.handle(10.0F);
      float var7 = AutoBuy.timerRender.size() * var3 + Math.max(0, AutoBuy.timerRender.size() - 1) * var4;
      return Math.max(0.0F, var7 - var6);
   }

   private float update(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      float var3 = var2.handle(58.0F);
      float var4 = var2.handle(8.0F);
      float var5 = var1.panelH() - var2.handle(72.0F);
      float var6 = this.selection.size() * var3 + Math.max(0, this.selection.size() - 1) * var4;
      return Math.max(0.0F, var6 - var5);
   }

   private int apply(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      float var3 = this.handle(var2);
      float var4 = this.compute(var2);
      return Math.max(1, (int)((var1.catalogViewportW() + var4) / (var3 + var4)));
   }

   private float handle(GuiMetrics var1) {
      return var1.handle(72.0F);
   }

   private float process(GuiMetrics var1) {
      return var1.handle(86.0F);
   }

   private float compute(GuiMetrics var1) {
      return var1.handle(8.0F);
   }

   private float execute(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      return var1.leftX() + var2.handle(10.0F);
   }

   private float prepare(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      return var1.panelY() + var2.handle(45.0F);
   }

   private float check(ServerItemCatalog.Bounds var1, GuiMetrics var2) {
      return Math.max(var2.handle(80.0F), var1.leftW() - var2.handle(20.0F));
   }

   private float resolve(GuiMetrics var1) {
      return var1.handle(27.0F);
   }

   private void compute() {
      this.process(this.pointEncode, 0.0F);
   }

   private void resolve() {
      this.process(this.pointEncode, 0.0F);
      this.process(this.animator, 0.0F);
      this.process(this.source, 0.0F);
      this.process(this.target, 0.0F);
      this.windowConvert = ServerItemCatalog.RuntimeDataRecord.hidden();
      this.presetWrite = ServerItemCatalog.RuntimeDataRecord.hidden();
      this.colorMeasure = ServerItemCatalog.RuntimeDataRecord.hidden();
      this.animationSchedule = ServerItemCatalog.RuntimeDataRecord.hidden();
      this.rendererScan = false;
      this.sourceBuild = false;
      this.outputCollapse = false;
      this.profileInvoke = false;
      this.sourceSchedule = 0.0F;
      this.serverRead.clear();
      this.positionAdvance = null;
      this.frameCheck = null;
      this.handler.clear();
      this.animationDraw = null;
      this.vectorPerform = -1;
      this.providerFetch.resolve(0.0);
      this.providerFetch.process();
      this.scaleSave = null;
      this.colorCompute = 0L;
      this.scaleAdapt = 0.0F;
      this.textureRun = 0.0F;
   }

   private float update(GuiMetrics var1) {
      return var1.handle(72.0F);
   }

   private float handle(String var1, GuiMetrics var2, float var3) {
      return this.update(var2) + (var2.handle(6.0F) + this.handle(var1, var2)) * this.process(var3, 0.0F, 1.0F);
   }

   private float handle(String var1, GuiMetrics var2) {
      int var3 = this.compute(var1).enchantments().size();
      if (var3 == 0) {
         return var2.handle(112.0F);
      }

      int var4 = (var3 + 1) / 2;
      return var2.handle(128.0F + var4 * 22.0F + Math.max(0, var4 - 1) * 4.0F + 14.0F);
   }

   private float select(String var1) {
      return var1 != null && var1.equals(this.positionAdvance) && this.execute(var1) ? 1.0F : 0.0F;
   }

   private float handle(ModernClickGuiState var1, String var2) {
      return var1.process(this.refresh(var2), this.select(var2), instance);
   }

   private float process(ModernClickGuiState var1, String var2) {
      return var1.handle(this.refresh(var2));
   }

   private String refresh(String var1) {
      return "ab:armor-settings:open:" + var1;
   }

   private float apply(GuiMetrics var1) {
      return var1.handle(8.0F);
   }

   private String handle(long var1) {
      return MaxPriceSetting.handle(var1);
   }

   private float process(float var1, float var2, float var3) {
      return Math.max(var2, Math.min(var3, var1));
   }

   private float handle(RenderRuntimeContext var1, float var2) {
      var1.compute(-var2);
      var1.handle(this.process(var1.execute(), -var2, 0.0F));
      var1.compute();
      return var1.prepare();
   }

   private void handle(RenderRuntimeContext var1, float var2, double var3) {
      var1.compute(-var2);
      var1.handle(var3);
      var1.handle(this.process(var1.execute(), -var2, 0.0F));
   }

   private void process(RenderRuntimeContext var1, float var2) {
      var1.handle(var2);
      var1.process(var2);
   }

   record Bounds(
      float x,
      float y,
      float width,
      float height,
      float leftX,
      float rightX,
      float leftW,
      float rightW,
      float panelY,
      float panelH,
      float catalogViewportX,
      float catalogViewportY,
      float catalogViewportW,
      float catalogViewportH,
      float catalogScrollbarX,
      float rulesViewportX,
      float rulesViewportY,
      float rulesViewportW,
      float rulesViewportH,
      float rulesScrollbarX,
      float scrollbarW
   ) {
   }

   record CachedDataRecord(
      float stripH,
      float modeX,
      float modeY,
      float toggleX,
      float toggleW,
      float gap,
      float tabBtnSize,
      float chipW,
      boolean showReparse,
      float reparseX,
      float reparseW,
      float reparseToggleW,
      float reparseSliderX,
      float reparseSliderW
   ) {
   }

   record DataRecord(String key, String label, ItemStack stack, boolean custom) {
   }

   record FallbackDataRecord(String key, String label) {
   }

   enum Mode {
      WELL,
      TILE,
      CARD,
      CONTROL,
      INSET;
   }

   record MutableDataRecord(List<String> lines, float size) {
   }

   record PersistentDataRecord(List<ServerItemCatalog.FallbackDataRecord> enchantments) {
   }

   record PrimaryDataRecord(
      float titleX,
      float titleW,
      float priceX,
      float priceW,
      float statusX,
      float statusW,
      float deleteX,
      float deleteW,
      float settingsX,
      float settingsW,
      float controlY,
      float controlH
   ) {
   }

   record RuntimeDataRecord(float x, float y, float w, float h, float maxScroll, float thumbY, float thumbH, boolean visible) {
      static ServerItemCatalog.RuntimeDataRecord hidden() {
         return hidden(0.0F, 0.0F, 0.0F, 0.0F);
      }

      static ServerItemCatalog.RuntimeDataRecord hidden(float var0, float var1, float var2, float var3) {
         return new ServerItemCatalog.RuntimeDataRecord(var0, var1, var2, var3, 0.0F, var1, 0.0F, false);
      }

      float travel() {
         return Math.max(0.0F, this.h - this.thumbH);
      }
   }

   record SecondaryDataRecord(boolean visible, float chipX, float chipY, float chipW, float chipH, float fixX, float fixW, float statX, float statW) {
   }

   record Snapshot(String name, String author, long timestamp) {
   }
}
