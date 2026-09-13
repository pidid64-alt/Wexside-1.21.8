package ru.wild.modules.visuals;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.math.EasingFunction;
import ru.wild.util.math.TimedEasingState;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "Animations", category = ModuleCategory.Visuals, description = "Анимки на все действия, таб, открытие инва итд")
public class Animations extends Module {
   private static final long vectorPerform = 240L;
   private static final long eventAttach = 220L;
   private static final long serverRead = 300L;
   private static final long positionAdvance = 240L;
   private static final long frameCheck = 90L;
   private static final long moduleCollect = 120L;
   private static final long providerClose = 260L;
   public final ChoiceSetting source = new ChoiceSetting(
      "Анимировать",
      new BooleanSetting("Чат", false),
      new BooleanSetting("Таб", false),
      new BooleanSetting("Инвентарь", false),
      new BooleanSetting("Сундуки", false),
      new BooleanSetting("Кнопки", false),
      new BooleanSetting("F5", false)
   );
   public final ModeSetting target = new ModeSetting(
      "Режим анимации",
      "Ease Out Back",
      "Linear",
      "Ease Out Quad",
      "Ease Out Cubic",
      "Ease Out Quart",
      "Ease Out Expo",
      "Ease Out Back",
      "Ease Out Elastic",
      "Ease Out Bounce",
      "Shrink Easing"
   );
   public final NumberSetting pending = new NumberSetting("Скорость чата", 1.0F, 0.1F, 3.0F, 0.1F, false).handle(() -> !this.source.process("Чат"));
   public final NumberSetting previous = new NumberSetting("Скорость таба", 1.0F, 0.1F, 3.0F, 0.1F, false).handle(() -> !this.source.process("Таб"));
   public final NumberSetting latest = new NumberSetting("Скорость инвентаря", 1.0F, 0.1F, 3.0F, 0.1F, false).handle(() -> !this.source.process("Инвентарь"));
   public final NumberSetting summary = new NumberSetting("Скорость сундуков", 1.0F, 0.1F, 3.0F, 0.1F, false).handle(() -> !this.source.process("Сундуки"));
   public final NumberSetting matrixBlend = new NumberSetting("Скорость кнопок", 1.0F, 0.1F, 3.0F, 0.1F, false).handle(() -> !this.source.process("Кнопки"));
   public final NumberSetting vectorMatch = new NumberSetting("Скорость F5", 1.0F, 0.1F, 3.0F, 0.1F, false).handle(() -> !this.source.process("F5"));
   public TimedEasingState itemProject;
   public TimedEasingState responseCompute;
   public TimedEasingState providerFetch;
   public static float profileDraw = 1.0F;
   private Perspective presetSave = Perspective.FIRST_PERSON;
   private Perspective windowConvert = Perspective.FIRST_PERSON;
   private Perspective presetWrite = Perspective.FIRST_PERSON;
   private long colorMeasure;
   private boolean animationSchedule;
   private boolean rendererScan;
   private long sourceBuild;
   private boolean outputCollapse;
   private long profileInvoke;
   private Screen sourceSchedule;
   private boolean timerRender;

   public Animations() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend, this.vectorMatch);
   }

   public EasingFunction refresh() {
      String var1 = this.target.compute();

      for (EasingFunction var5 : EasingFunction.values()) {
         if (var5.toString().equalsIgnoreCase(var1)) {
            return var5;
         }
      }

      return EasingFunction.EASE_OUT_BACK;
   }

   public EasingFunction render() {
      return this.tick();
   }

   public EasingFunction tick() {
      EasingFunction var1 = this.refresh();

      return switch (var1) {
         case EASE_OUT_BACK, EASE_OUT_ELASTIC, SHRINK_EASING -> EasingFunction.EASE_OUT_QUAD;
         default -> var1;
      };
   }

   public float drawAnimation() {
      return handle(this.matrixBlend);
   }

   public void encodePoint() {
      if (this.rendererScan && this.responseCompute != null && !this.responseCompute.onTick()) {
         this.rendererScan = false;
         this.sourceBuild = 0L;
         float var1 = (float)this.responseCompute.check();
         long var2 = this.savePreset();
         long var4 = Math.max(handle(80L, this.pending), Math.round(var2 * (1.0 - var1)));
         this.responseCompute.handle(this.refresh());
         this.responseCompute.handle(var4);
         this.responseCompute.handle(1.0);
      } else {
         this.rendererScan = false;
         this.sourceBuild = 0L;
         if (this.responseCompute == null) {
            this.responseCompute = new TimedEasingState(this.refresh(), this.savePreset());
         }

         this.responseCompute.handle(this.refresh());
         this.responseCompute.handle(this.savePreset());
         this.responseCompute.handle(1.0);
      }
   }

   public void animate() {
      if (this.responseCompute == null) {
         this.responseCompute = new TimedEasingState(this.render(), this.convertWindow());
         this.responseCompute.resolve(1.0);
         this.responseCompute.process(1.0);
         this.responseCompute.compute(1.0);
         this.responseCompute.handle(true);
      }

      if (!this.rendererScan) {
         this.rendererScan = true;
         this.sourceBuild = System.currentTimeMillis();
         float var1 = (float)this.responseCompute.check();
         long var2 = this.convertWindow();
         long var4 = Math.max(handle(80L, this.pending), Math.round((float)var2 * var1));
         this.responseCompute.handle(this.render());
         this.responseCompute.handle(var4);
      }

      this.responseCompute.handle(0.0);
   }

   public boolean load() {
      return this.rendererScan;
   }

   public boolean save() {
      if (!this.rendererScan) {
         return false;
      }

      if (this.responseCompute == null) {
         return true;
      }

      long var1 = System.currentTimeMillis() - this.sourceBuild;
      return var1 >= this.responseCompute.resolve() + 20L ? true : this.responseCompute.onTick() && this.responseCompute.check() <= 0.001;
   }

   public float submit() {
      return this.responseCompute == null ? 0.0F : (float)this.responseCompute.check();
   }

   public void unload() {
      this.responseCompute = null;
      this.rendererScan = false;
      this.sourceBuild = 0L;
   }

   public boolean handle(Screen var1) {
      if (var1 != null && this.enabled) {
         boolean var2 = var1 instanceof InventoryScreen;
         return var2 && this.source.process("Инвентарь") ? true : !var2 && this.source.process("Сундуки");
      } else {
         return false;
      }
   }

   public float process(Screen var1) {
      if (!this.handle(var1)) {
         return 1.0F;
      }

      boolean var2 = this.outputCollapse && (this.sourceSchedule == null || this.sourceSchedule == var1);
      long var3 = this.handle(var1, var2);
      if (this.itemProject == null) {
         this.itemProject = new TimedEasingState(var2 ? this.tick() : this.refresh(), var3);
         if (var2) {
            this.itemProject.resolve(1.0);
            this.itemProject.process(1.0);
            this.itemProject.compute(1.0);
            this.itemProject.handle(true);
         }
      }

      this.itemProject.handle(var2 ? this.tick() : this.refresh());
      this.itemProject.handle(var3);
      this.itemProject.handle(var2 ? 0.0 : 1.0);
      return compute((float)this.itemProject.check());
   }

   public void compute(Screen var1) {
      if (var1 != null && !this.timerRender && this.handle(var1)) {
         if (this.itemProject == null) {
            this.itemProject = new TimedEasingState(this.tick(), this.resolve(var1));
            this.itemProject.resolve(1.0);
            this.itemProject.process(1.0);
            this.itemProject.compute(1.0);
            this.itemProject.handle(true);
         }

         if (!this.outputCollapse || this.sourceSchedule != var1) {
            this.outputCollapse = true;
            this.profileInvoke = System.currentTimeMillis();
            this.sourceSchedule = var1;
            this.itemProject.handle(this.tick());
            this.itemProject.handle(this.resolve(var1));
         }

         this.itemProject.handle(0.0);
      }
   }

   public boolean fetch() {
      return this.outputCollapse;
   }

   public boolean measure() {
      return this.timerRender;
   }

   public void blendMatrix() {
      this.itemProject = null;
      this.outputCollapse = false;
      this.profileInvoke = 0L;
      this.sourceSchedule = null;
      this.timerRender = false;
   }

   public boolean compute(boolean var1) {
      if (!this.enabled || !this.source.process("Таб")) {
         return var1;
      } else {
         return var1 ? true : this.providerFetch != null && (!this.providerFetch.onTick() || this.providerFetch.check() > 0.001);
      }
   }

   public float resolve(boolean var1) {
      long var2 = this.update(var1);
      if (this.providerFetch == null) {
         this.providerFetch = new TimedEasingState(var1 ? this.refresh() : this.tick(), var2);
         if (!var1) {
            this.providerFetch.resolve(1.0);
            this.providerFetch.process(1.0);
            this.providerFetch.compute(1.0);
            this.providerFetch.handle(true);
         }
      }

      this.providerFetch.handle(var1 ? this.refresh() : this.tick());
      this.providerFetch.handle(var2);
      this.providerFetch.handle(var1 ? 1.0 : 0.0);
      float var4 = compute((float)this.providerFetch.check());
      if (!var1 && this.providerFetch.onTick() && var4 <= 0.001F) {
         this.providerFetch = null;
      }

      return var4;
   }

   public float matchVector() {
      if (this.enabled && this.source.process("F5") && this.animationSchedule) {
         long var1 = System.currentTimeMillis() - this.colorMeasure;
         profileDraw = compute((float)var1 / (float)this.writePreset());
         if (profileDraw >= 1.0F) {
            profileDraw = 1.0F;
            this.animationSchedule = false;
         }

         return profileDraw;
      } else {
         profileDraw = 1.0F;
         this.animationSchedule = false;
         return profileDraw;
      }
   }

   public float projectItem() {
      float var1 = this.matchVector();
      return 1.0F - (float)Math.pow(1.0F - var1, 3.0);
   }

   public boolean computeResponse() {
      return this.animationSchedule && this.windowConvert == Perspective.FIRST_PERSON && this.presetWrite == Perspective.THIRD_PERSON_BACK;
   }

   public boolean fetchProvider() {
      return this.animationSchedule && this.windowConvert != Perspective.FIRST_PERSON && this.presetWrite == Perspective.FIRST_PERSON;
   }

   public boolean drawProfile() {
      return this.animationSchedule
         && (
            this.windowConvert == Perspective.THIRD_PERSON_BACK && this.presetWrite == Perspective.THIRD_PERSON_FRONT
               || this.windowConvert == Perspective.THIRD_PERSON_FRONT && this.presetWrite == Perspective.THIRD_PERSON_BACK
         );
   }

   public boolean performVector() {
      return this.drawProfile() && this.windowConvert == Perspective.THIRD_PERSON_BACK && this.presetWrite == Perspective.THIRD_PERSON_FRONT;
   }

   public boolean attachEvent() {
      return this.drawProfile() && this.windowConvert == Perspective.THIRD_PERSON_FRONT && this.presetWrite == Perspective.THIRD_PERSON_BACK;
   }

   public float readServer() {
      float var1 = this.projectItem();
      if (this.windowConvert == Perspective.THIRD_PERSON_BACK && this.presetWrite == Perspective.THIRD_PERSON_FRONT) {
         return 180.0F * var1;
      } else if (this.windowConvert == Perspective.THIRD_PERSON_FRONT && this.presetWrite == Perspective.THIRD_PERSON_BACK) {
         return 180.0F * (1.0F - var1);
      } else {
         return this.presetWrite == Perspective.THIRD_PERSON_FRONT ? 180.0F : 0.0F;
      }
   }

   public float handle(float var1) {
      float var2 = this.projectItem();
      if (this.windowConvert == Perspective.THIRD_PERSON_BACK && this.presetWrite == Perspective.THIRD_PERSON_FRONT) {
         return handle(var1, -var1, var2);
      } else if (this.windowConvert == Perspective.THIRD_PERSON_FRONT && this.presetWrite == Perspective.THIRD_PERSON_BACK) {
         return handle(var1, -var1, 1.0F - var2);
      } else {
         return this.presetWrite == Perspective.THIRD_PERSON_FRONT ? -var1 : var1;
      }
   }

   public float advancePosition() {
      if (!this.fetchProvider()) {
         return 0.0F;
      } else {
         return this.windowConvert == Perspective.THIRD_PERSON_FRONT ? 180.0F * (1.0F - this.projectItem()) : 0.0F;
      }
   }

   public float process(float var1) {
      if (!this.fetchProvider()) {
         return var1;
      } else {
         return this.windowConvert == Perspective.THIRD_PERSON_FRONT ? handle(-var1, var1, this.projectItem()) : var1;
      }
   }

   public float checkFrame() {
      return this.fetchProvider() ? 1.0F - this.projectItem() : 0.0F;
   }

   private boolean collectModule() {
      if (!this.outputCollapse) {
         return false;
      }

      if (this.itemProject == null) {
         return true;
      }

      long var1 = System.currentTimeMillis() - this.profileInvoke;
      return var1 >= this.itemProject.resolve() + 40L ? true : this.itemProject.onTick() && this.itemProject.check() <= 0.001;
   }

   private void closeProvider() {
      Screen var1 = this.sourceSchedule;
      this.timerRender = true;

      try {
         if (var1 != null && Module.client.currentScreen == var1) {
            var1.close();
         } else if (Module.client.currentScreen != null && this.handle(Module.client.currentScreen)) {
            Module.client.setScreen(null);
         }
      } finally {
         this.blendMatrix();
      }
   }

   private static float compute(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private long savePreset() {
      return handle(240L, this.pending);
   }

   private long convertWindow() {
      return handle(220L, this.pending);
   }

   private long update(boolean var1) {
      return handle(var1 ? 300L : 240L, this.previous);
   }

   private long handle(Screen var1, boolean var2) {
      return handle(var2 ? 120L : 90L, this.update(var1));
   }

   private long resolve(Screen var1) {
      return handle(120L, this.update(var1));
   }

   private long writePreset() {
      return handle(260L, this.vectorMatch);
   }

   private NumberSetting update(Screen var1) {
      return var1 instanceof InventoryScreen ? this.latest : this.summary;
   }

   private static long handle(long var0, NumberSetting var2) {
      return Math.max(1L, Math.round((float)var0 / handle(var2)));
   }

   private static float handle(NumberSetting var0) {
      if (var0 == null) {
         return 1.0F;
      }

      float var1 = var0.compute();
      return Float.isFinite(var1) && !(var1 <= 0.0F) ? var1 : 1.0F;
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (!Module.client.options.playerListKey.isPressed() && this.providerFetch != null) {
            this.resolve(false);
         }

         Perspective var2 = Module.client.options.getPerspective();
         if (var2 != this.presetSave) {
            this.windowConvert = this.presetSave;
            this.presetWrite = var2;
            this.presetSave = var2;
            if (this.enabled && this.source.process("F5")) {
               profileDraw = 0.0F;
               this.colorMeasure = System.currentTimeMillis();
               this.animationSchedule = true;
            } else {
               profileDraw = 1.0F;
               this.animationSchedule = false;
            }
         }

         this.matchVector();
         if (this.rendererScan && Module.client.currentScreen instanceof ChatScreen && this.save()) {
            Module.client.setScreen(null);
            this.unload();
         }

         if (this.outputCollapse && this.collectModule()) {
            this.closeProvider();
         }
      }
   }

   private static float handle(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * compute(var2);
   }
}
