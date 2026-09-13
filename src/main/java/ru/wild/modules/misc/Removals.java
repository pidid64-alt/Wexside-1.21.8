package ru.wild.modules.misc;

import java.util.Locale;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.NowPlayingToast;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.StringSetting;

@ModuleRegister(name = "Removals", description = "Гибкое отключение мешающих оверлеев, эффектов, частиц и звуков", category = ModuleCategory.Misc)
public class Removals extends Module {
   public static final String source = "Огонь";
   public static final String target = "Вода";
   public static final String pending = "Стена в глазах";
   public static final String previous = "Тыква";
   public static final String latest = "Порошковый снег";
   public static final String summary = "Подзорная труба";
   public static final String matrixBlend = "Портал";
   public static final String vectorMatch = "Тошнота (экран)";
   public static final String itemProject = "Виньетка";
   public static final String responseCompute = "Тряска от урона";
   public static final String providerFetch = "Тьма";
   public static final String profileDraw = "Слепота";
   public static final String vectorPerform = "Тошнота (эффект)";
   public static final String eventAttach = "Туман";
   public static final String serverRead = "Иконки эффектов";
   public static final String positionAdvance = "Взрывы";
   public static final String frameCheck = "Тотем";
   public static final String moduleCollect = "Эффекты зелий";
   public static final String providerClose = "Криты и удары";
   public static final String presetSave = "Чары стола";
   public static final String windowConvert = "Капли и вода";
   public static final String presetWrite = "Редстоун";
   public static final String colorMeasure = "Дым и огонь";
   public static final String animationSchedule = "Сердечки и деревня";
   public static final String rendererScan = "Порталы (частицы)";
   public static final String sourceBuild = "Фейерверки (частицы)";
   public static final String outputCollapse = "Дракон и скалк";
   public static final String profileInvoke = "Природный эмбиент";
   public static final String sourceSchedule = "Ветрозаряд";
   public static final String timerRender = "Взрывы (звук)";
   public static final String scaleSave = "Поршни";
   public static final String colorCompute = "Вода и лава";
   public static final String scaleAdapt = "Эмбиент (пещеры, мобы)";
   public static final String textureRun = "Порталы (звук)";
   public static final String indexBind = "Маяк";
   public static final String actionRead = "Опыт и уровень";
   public static final String configCollapse = "Подбор предметов";
   public static final String dataValidate = "Фейерверки (звук)";
   public static final String scaleRender = "Нот-блоки";
   public static final String clientRefresh = "Двери и контейнеры";
   public static final String keyFilter = "Гром и молния";
   public static final String requestAdapt = "Колокол";
   public static final String timerMeasure = "Тотемы (звук)";
   public static final String vectorEncode = "Наковальня";
   public static final String requestReceive = "Элитра";
   public static final String windowProcess = "Боссы (рёв)";
   public static final String packetSave = "Ветрозаряд и Мейс";
   public static final String entryAnimate = "Трава";
   public static final String playerCollect = "Растения и цветы";
   public static final String stateApply = "Листва";
   public static final String matrixFilter = "Снег (покров)";
   public static final String layerSample = "Стойки брони";
   public static final String worldSend = "Рамки";
   public static final String targetWrite = "Картины";
   public static final String resultEncode = "Дроп предметов";
   public static final String messageParse = "Опыт-орбы";
   public static final String providerRead = "Погода (дождь/снег)";
   public static final String matrixBlend2 = "Вода (жидкость)";
   public static final String scalePerform = "Лава (жидкость)";
   public static final String contextExpand = "Диктор";
   public static final String keyProcess = "Тосты и ачивки";
   public static final String actionConvert = "Анимация тотема";
   public static final ChoiceSetting screenRead = new ChoiceSetting(
      "Оверлеи экрана",
      new BooleanSetting("Огонь", false),
      new BooleanSetting("Вода", false),
      new BooleanSetting("Стена в глазах", false),
      new BooleanSetting("Тыква", false),
      new BooleanSetting("Порошковый снег", false),
      new BooleanSetting("Подзорная труба", false),
      new BooleanSetting("Портал", false),
      new BooleanSetting("Тошнота (экран)", false),
      new BooleanSetting("Виньетка", false),
      new BooleanSetting("Тряска от урона", false)
   );
   public static final ChoiceSetting animationExpand = new ChoiceSetting(
      "Эффекты и туман",
      new BooleanSetting("Тьма", false),
      new BooleanSetting("Слепота", false),
      new BooleanSetting("Тошнота (эффект)", false),
      new BooleanSetting("Туман", false),
      new BooleanSetting("Иконки эффектов", false)
   );
   public static final ChoiceSetting playerRun = new ChoiceSetting(
      "Частицы",
      new BooleanSetting("Взрывы", false),
      new BooleanSetting("Тотем", false),
      new BooleanSetting("Эффекты зелий", false),
      new BooleanSetting("Криты и удары", false),
      new BooleanSetting("Чары стола", false),
      new BooleanSetting("Капли и вода", false),
      new BooleanSetting("Редстоун", false),
      new BooleanSetting("Дым и огонь", false),
      new BooleanSetting("Сердечки и деревня", false),
      new BooleanSetting("Порталы (частицы)", false),
      new BooleanSetting("Фейерверки (частицы)", false),
      new BooleanSetting("Дракон и скалк", false),
      new BooleanSetting("Природный эмбиент", false),
      new BooleanSetting("Ветрозаряд", false)
   );
   public static final ChoiceSetting matrixRender = new ChoiceSetting(
      "Звуки",
      new BooleanSetting("Взрывы (звук)", false),
      new BooleanSetting("Поршни", false),
      new BooleanSetting("Вода и лава", false),
      new BooleanSetting("Эмбиент (пещеры, мобы)", false),
      new BooleanSetting("Порталы (звук)", false),
      new BooleanSetting("Маяк", false),
      new BooleanSetting("Опыт и уровень", false),
      new BooleanSetting("Подбор предметов", false),
      new BooleanSetting("Фейерверки (звук)", false),
      new BooleanSetting("Нот-блоки", false),
      new BooleanSetting("Двери и контейнеры", false),
      new BooleanSetting("Гром и молния", false),
      new BooleanSetting("Колокол", false),
      new BooleanSetting("Тотемы (звук)", false),
      new BooleanSetting("Наковальня", false),
      new BooleanSetting("Элитра", false),
      new BooleanSetting("Боссы (рёв)", false),
      new BooleanSetting("Ветрозаряд и Мейс", false)
   );
   public static final ChoiceSetting moduleTick = new ChoiceSetting(
      "Мир и сущности",
      new BooleanSetting("Трава", true),
      new BooleanSetting("Растения и цветы", true),
      new BooleanSetting("Листва", false),
      new BooleanSetting("Снег (покров)", false),
      new BooleanSetting("Стойки брони", true),
      new BooleanSetting("Рамки", true),
      new BooleanSetting("Картины", true),
      new BooleanSetting("Дроп предметов", false),
      new BooleanSetting("Опыт-орбы", false),
      new BooleanSetting("Погода (дождь/снег)", false),
      new BooleanSetting("Вода (жидкость)", false),
      new BooleanSetting("Лава (жидкость)", false)
   );
   public static final ChoiceSetting playerCollapse = new ChoiceSetting(
      "Интерфейс", new BooleanSetting("Диктор", true), new BooleanSetting("Тосты и ачивки", false), new BooleanSetting("Анимация тотема", false)
   );
   public static final BooleanSetting optionAdvance = new BooleanSetting("Не скрывать карты", true).handle(() -> !moduleTick.process("Рамки"));
   public static final StringSetting effectScan = new StringSetting("Свои звуки (через запятую)", "").handle(512);
   public static final StringSetting optionParse = new StringSetting("Свои частицы (через запятую)", "").handle(512);
   private static final Removals.State[] pointSubmit = new Removals.State[]{
      new Removals.State("Взрывы (звук)", "explode"),
      new Removals.State("Поршни", "piston"),
      new Removals.State("Вода и лава", "water", "lava", "bubble", "splash", "swim"),
      new Removals.State("Эмбиент (пещеры, мобы)", "ambient"),
      new Removals.State("Порталы (звук)", "portal"),
      new Removals.State("Маяк", "beacon"),
      new Removals.State("Опыт и уровень", "experience_orb", "levelup"),
      new Removals.State("Подбор предметов", "item.pickup"),
      new Removals.State("Фейерверки (звук)", "firework"),
      new Removals.State("Нот-блоки", "note_block"),
      new Removals.State("Двери и контейнеры", "door", "chest", "barrel", "shulker_box", "ender_chest"),
      new Removals.State("Гром и молния", "thunder", "lightning"),
      new Removals.State("Колокол", "bell"),
      new Removals.State("Тотемы (звук)", "totem"),
      new Removals.State("Наковальня", "anvil"),
      new Removals.State("Элитра", "elytra"),
      new Removals.State("Боссы (рёв)", "wither.spawn", "wither.death", "ender_dragon.death", "ender_dragon.growl"),
      new Removals.State("Ветрозаряд и Мейс", "wind_charge", "breeze", "mace.smash")
   };
   private static final Removals.State[] listenerPerform = new Removals.State[]{
      new Removals.State("Взрывы", "explosion"),
      new Removals.State("Тотем", "totem_of_undying"),
      new Removals.State("Эффекты зелий", "effect"),
      new Removals.State("Криты и удары", "crit", "enchanted_hit", "sweep_attack", "damage_indicator"),
      new Removals.State("Чары стола", "enchant", "nautilus").handle(new String[]{"enchanted_hit"}),
      new Removals.State("Капли и вода", "water", "splash", "bubble", "fishing", "rain", "lava"),
      new Removals.State("Редстоун", "dust").handle(new String[]{"falling_dust"}),
      new Removals.State("Дым и огонь", "smoke", "flame", "campfire", "spark"),
      new Removals.State("Сердечки и деревня", "heart", "angry_villager", "happy_villager"),
      new Removals.State("Порталы (частицы)", "portal"),
      new Removals.State("Фейерверки (частицы)", "firework", "flash"),
      new Removals.State("Дракон и скалк", "sculk", "dragon_breath", "sonic_boom", "shriek", "vibration"),
      new Removals.State("Природный эмбиент", "white_ash", "spore", "mycelium", "leaves", "snowflake", "cherry"),
      new Removals.State("Ветрозаряд", "gust")
   };
   private static Removals configMatch;
   private int actionRender = -1;

   public Removals() {
      configMatch = this;
      this.handle(screenRead, animationExpand, playerRun, matrixRender, moduleTick, optionAdvance, playerCollapse, effectScan, optionParse);
   }

   @Override
   public void handle() {
      super.handle();
      this.actionRender = this.render();
      this.tick();
   }

   @Override
   public void process() {
      super.process();
      this.tick();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      int var2 = this.render();
      if (var2 != this.actionRender) {
         this.actionRender = var2;
         this.tick();
      }
   }

   private int render() {
      if (!this.enabled) {
         return 0;
      }

      byte var1 = 1;
      if (moduleTick.process("Трава")) {
         var1 |= 2;
      }

      if (moduleTick.process("Растения и цветы")) {
         var1 |= 4;
      }

      if (moduleTick.process("Листва")) {
         var1 |= 8;
      }

      if (moduleTick.process("Снег (покров)")) {
         var1 |= 16;
      }

      return var1;
   }

   private void tick() {
      if (!WildClient.performVector() && Module.client != null && Module.client.worldRenderer != null && Module.client.world != null) {
         Module.client.worldRenderer.reload();
      }
   }

   public static boolean handle(String var0) {
      return !animate() ? false : screenRead.process(var0) || animationExpand.process(var0) || moduleTick.process(var0) || playerCollapse.process(var0);
   }

   public static boolean process(String var0) {
      return animate() && moduleTick.process(var0);
   }

   public static boolean handle(RegistryEntry<StatusEffect> var0) {
      if (!animate()) {
         return false;
      } else if (var0 == StatusEffects.DARKNESS) {
         return animationExpand.process("Тьма");
      } else if (var0 == StatusEffects.BLINDNESS) {
         return animationExpand.process("Слепота");
      } else {
         return var0 == StatusEffects.NAUSEA ? animationExpand.process("Тошнота (эффект)") : false;
      }
   }

   public static boolean handle(ParticleEffect var0) {
      return animate() && playerRun.process("Капли и вода");
   }

   public static boolean handle(Identifier var0) {
      if (animate() && var0 != null && encodePoint()) {
         String var1 = var0.getPath();

         for (Removals.State var5 : pointSubmit) {
            if (matrixRender.process(var5.instance) && var5.handle(var1)) {
               return true;
            }
         }

         return handle(effectScan.compute(), var0);
      } else {
         return false;
      }
   }

   public static boolean process(ParticleEffect var0) {
      if (animate() && var0 != null && drawAnimation()) {
         Identifier var1 = Registries.PARTICLE_TYPE.getId(var0.getType());
         if (var1 == null) {
            return false;
         }

         String var2 = var1.getPath();

         for (Removals.State var6 : listenerPerform) {
            if (playerRun.process(var6.instance) && var6.handle(var2)) {
               return true;
            }
         }

         return handle(optionParse.compute(), var1);
      } else {
         return false;
      }
   }

   public static boolean handle(Toast var0) {
      return animate() && var0 != null && playerCollapse.process("Тосты и ачивки")
         ? var0 instanceof AdvancementToast || var0 instanceof RecipeToast || var0 instanceof TutorialToast || var0 instanceof NowPlayingToast
         : false;
   }

   public static boolean refresh() {
      return animate() && playerCollapse.process("Диктор");
   }

   private static boolean handle(String var0, Identifier var1) {
      if (var0 != null && !var0.isBlank()) {
         String var2 = var1.toString();

         for (String var6 : var0.toLowerCase(Locale.ROOT).split(",")) {
            String var7 = var6.trim();
            if (!var7.isEmpty() && var2.contains(var7)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean drawAnimation() {
      for (BooleanSetting var1 : playerRun.config) {
         if (var1.compute()) {
            return true;
         }
      }

      return !optionParse.compute().isBlank();
   }

   private static boolean encodePoint() {
      for (BooleanSetting var1 : matrixRender.config) {
         if (var1.compute()) {
            return true;
         }
      }

      return !effectScan.compute().isBlank();
   }

   private static boolean animate() {
      Removals var0 = load();
      return var0 != null && var0.enabled;
   }

   private static Removals load() {
      if (configMatch != null) {
         return configMatch;
      } else {
         return WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(Removals.class) : null;
      }
   }

   static final class State {
      final String instance;
      final String[] data;
      String[] context = new String[0];

      State(String var1, String... var2) {
         this.instance = var1;
         this.data = var2;
      }

      Removals.State handle(String... var1) {
         this.context = var1;
         return this;
      }

      boolean handle(String var1) {
         for (String var5 : this.context) {
            if (var1.contains(var5)) {
               return false;
            }
         }

         for (String var9 : this.data) {
            if (var1.contains(var9)) {
               return true;
            }
         }

         return false;
      }
   }
}
