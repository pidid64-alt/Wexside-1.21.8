package ru.wild.modules.misc;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.CameraRotationEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.AuctionTradeExecutor;
import ru.wild.automation.PurchaseTaskScheduler;
import ru.wild.command.RctCommand;
import ru.wild.gui.screen.ClickGuiModernScreen;
import ru.wild.gui.screen.ServerPresets;
import ru.wild.network.TelegramApi;
import ru.wild.util.inventory.AuctionLoreParser;
import ru.wild.util.inventory.AuctionSellerParser;
import ru.wild.util.inventory.EnchantedItemDetector;
import ru.wild.util.inventory.HolyWorldHelper;
import ru.wild.util.inventory.SpecialItemCatalog;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoBuy", category = ModuleCategory.Misc, description = "Автоматическая покупка предметов с аукциона")
public class AutoBuy extends Module {
   private static final Pattern indexBind = Pattern.compile(
      "(\\d+)\\s*[/\\\\]\\s*\\d+|(?i)(?:страниц\\w*|стр\\.?|page)\\s*[:#]?\\s*(\\d+)|(?i)(\\d+)\\s*(?:из|of)\\s*\\d+"
   );
   private static final Pattern actionRead = Pattern.compile("Подождите\\s+(\\d+)\\s*сек", 66);
   private static final Pattern configCollapse = Pattern.compile("подождите\\s+(\\d+)\\s*сек\\S*\\s+для\\s+использования\\s+этой\\s+команды", 66);
   private static final long dataValidate = 9000L;
   private static final long scaleRender = 2000L;
   private static final long clientRefresh = 250L;
   private static final long keyFilter = 2500L;
   private static final long requestAdapt = 4500L;
   private static final long timerMeasure = 12000L;
   private static final long vectorEncode = 500L;
   private static final long requestReceive = 15000L;
   private static final long windowProcess = 20000L;
   private static final long packetSave = 240000L;
   private static final long entryAnimate = 4000L;
   private static final long playerCollect = 8000L;
   private static final int stateApply = 3;
   private static final long matrixFilter = 2000L;
   private static final long layerSample = 4500L;
   private static final long worldSend = 600L;
   private static final long targetWrite = 1400L;
   private static final long resultEncode = 750L;
   private static final long messageParse = 15000L;
   private static final long providerRead = 2000L;
   private static final int matrixBlend = 50;
   private static final int scalePerform = 48;
   private static final String contextExpand = "__wild_funtime_shulker__";
   private static final int keyProcess = 0;
   private static final int actionConvert = 1;
   private static final int screenRead = 2;
   private static final int animationExpand = 3;
   private static final int playerRun = 4;
   private static final long matrixRender = 1200L;
   private static final long moduleTick = 4500L;
   private static final long playerCollapse = 90L;
   private static final float optionAdvance = (float) (Math.PI * 2);
   private static final long effectScan = 75L;
   private static final double optionParse = 1.0;
   private static final double pointSubmit = 4.0;
   private static final int listenerPerform = 3;
   private static final Pattern configMatch = Pattern.compile(
      "Вы\\s+купили\\s+(?:[-–—]\\s*)?(?:\\[([^\\]]+)]|(.+?))\\s*(?:[-–—]?\\s*[xхXХ](\\d+))?\\s+у\\s+(.+?)\\s+за\\s+([\\d\\s.,]+)\\s*[¤$]?", 66
   );
   private static final Set<String> actionRender = Set.of(
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
      "Талисман Сара",
      "Талисман Сары",
      "Вещи Крушителя",
      "Набор Крушителя",
      "Броня Крушителя",
      "Броня Крушителя с шипами",
      "Броня Крушителя шип",
      "Броня Крушителя без шипов",
      "Броня Крушителя без шип",
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
      "Опыт 50",
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
      "Божье касание",
      "Мощный Удар",
      "Мега Бульдозер",
      "Нерушимые Элитры"
   );
   private static final Set<String> playerApply = Set.of("Явная Пыль", "Дезориентация", "Трапка", "Отмычка к Сферам");
   public static AutoBuy source;
   public static boolean target = false;
   public static boolean pending = false;
   public static boolean previous = false;
   public final ModeSetting latest = new ModeSetting("Режим сервера", "FunTime", "FunTime", "SpookyTime", "HolyWorld");
   public final BooleanSetting summary = new BooleanSetting("Auto Parse", false);
   public final NumberSetting matrixBlend2 = new NumberSetting("Парс Скидка %", 20.0F, 1.0F, 100.0F, 1.0F, true).handle(() -> !this.summary.compute());
   public final BooleanSetting vectorMatch = new BooleanSetting("Auto ReParse", false);
   public final NumberSetting itemProject = new NumberSetting("ReParse каждые (мин)", 30.0F, 5.0F, 240.0F, 5.0F, false)
      .handle(() -> !this.vectorMatch.compute());
   public final BooleanSetting responseCompute = new BooleanSetting("Свап анархии (5-10 мин)", false).handle(() -> !this.latest.process("FunTime"));
   public final NumberSetting providerFetch = new NumberSetting("Кд обновления (мс)", 100.0F, 100.0F, 5000.0F, 50.0F, false);
   public final NumberSetting profileDraw = new NumberSetting("Кд покупки (мс)", 100.0F, 100.0F, 5000.0F, 50.0F, false);
   public final NumberSetting vectorPerform = new NumberSetting("Кд подтверждения (мс)", 50.0F, 0.0F, 1000.0F, 10.0F, false);
   public final BooleanSetting eventAttach = new BooleanSetting("Детект замедления аука", true);
   public final BooleanSetting serverRead = new BooleanSetting("Авто-фикс замедления", true).handle(() -> !this.eventAttach.compute());
   public final BooleanSetting positionAdvance = new BooleanSetting("Лаг статистика в чат", true).handle(() -> !this.eventAttach.compute());
   public final KeybindSetting frameCheck = new KeybindSetting("Бинд меню", -1);
   public final NumberSetting moduleCollect = new NumberSetting("Защита от подмены лота (мс)", 90.0F, 0.0F, 500.0F, 10.0F, false);
   public final BooleanSetting providerClose = new BooleanSetting("Скупка шулкеров", false);
   public final NumberSetting presetSave = new NumberSetting("Shulker Profit %", 18.0F, 0.0F, 200.0F, 1.0F, true).handle(() -> !this.providerClose.compute());
   public final NumberSetting windowConvert = new NumberSetting("Shulker Profit $", 50000.0F, 0.0F, 1.0E9F, 10000.0F, false)
      .handle(() -> !this.providerClose.compute());
   public final NumberSetting presetWrite = new NumberSetting("Shulker Value $", 100000.0F, 0.0F, 1.0E9F, 10000.0F, false)
      .handle(() -> !this.providerClose.compute());
   public static final Map<String, Long> colorMeasure = new LinkedHashMap<>();
   public static final Map<String, Integer> animationSchedule = new LinkedHashMap<>();
   public static final Map<String, Integer> rendererScan = new LinkedHashMap<>();
   public static final Map<String, Set<String>> sourceBuild = new LinkedHashMap<>();
   public static final List<String> outputCollapse = new ArrayList<>();
   public static final Set<String> profileInvoke = new HashSet<>();
   public static final Map<String, String> sourceSchedule = new LinkedHashMap<>();
   public static final List<AutoBuy.CacheEntry> timerRender = new ArrayList<>();
   private final Stopwatch bufferAdapt = new Stopwatch();
   private final Stopwatch playerUpdate = new Stopwatch();
   private final Stopwatch packetRead = new Stopwatch();
   private final Stopwatch rendererCancel = new Stopwatch();
   private final Stopwatch eventReceive = new Stopwatch();
   private final Stopwatch screenSubmit = new Stopwatch();
   private final Stopwatch cacheHandle = new Stopwatch();
   private final Stopwatch rangeRelease = new Stopwatch();
   private final Stopwatch indexSave = new Stopwatch();
   private final Stopwatch indexCheck = new Stopwatch();
   private final Stopwatch settingSchedule = new Stopwatch();
   private boolean inputAcquire = false;
   public static long scaleSave = 0L;
   public static long colorCompute = 0L;
   public static long scaleAdapt = 0L;
   public static long textureRun = 0L;
   private int listenerRun = -1;
   private boolean indexLoad = false;
   private int layoutSave = 0;
   private boolean blockRun = false;
   private String playerEvaluate = "";
   private String outputFetch = "";
   private boolean scaleParse = false;
   private final List<String> sessionEncode = new ArrayList<>();
   private int elementTick = 0;
   private boolean regionAlign = false;
   private boolean resourceClamp = false;
   private String handlerRun = "";
   private String keyCheck = "";
   private int layerProject = 0;
   private long entityFilter = 0L;
   private long layerSample2 = 0L;
   private long sourceCancel = 0L;
   private long eventSend = 0L;
   private boolean providerOffset = false;
   private boolean messageParse2 = false;
   private boolean shaderProject = false;
   private boolean inputInvoke = false;
   private int optionFetch = -1;
   private long eventCollapse = 0L;
   private long stateAttach = 0L;
   private long worldEvaluate = 200L;
   private long playerProject = 50L;
   private long playerMatch = 0L;
   private long cacheClose = 0L;
   private long scaleSetup = 0L;
   private long indexSynchronize = 0L;
   private int taskInterpolate = 0;
   private boolean sourceRefresh = false;
   private boolean playerSave = false;
   private boolean requestRun = false;
   private float frameProject = 0.0F;
   private float dataRelease = 0.0F;
   private float vectorRun = 0.0F;
   private float providerSynchronize = 1.0F;
   private float pathProcess = 0.0F;
   private float positionReset = 0.0F;
   private float effectApply = 0.0F;
   private float cacheHandle2 = 0.0F;
   private float sessionAdvance = 1.0F;
   private float keySample = 0.12F;
   private long playerPerform = 0L;
   private float taskLoad = 0.0F;
   private boolean pointSend = false;
   private long clientSubmit = 0L;
   private double pointSample = 0.0;
   private double regionRefresh = 0.0;
   private int pathCheck = -1;
   private String contextParse = "";
   private long optionStop = 0L;
   private int mousePrepare = -1;
   private String sessionAdapt = "";
   private int profileMatch = 0;
   private long entityAdvance = 0L;
   private String sessionCollect = "";
   private long inputAttach = 0L;
   private long pathProject = 0L;
   private int inputHandle = 50;
   private int worldDispatch = 0;
   private boolean optionAdvance2 = false;
   private long clientDraw = 0L;
   private long providerMatch = 0L;
   private long profileWrite = 0L;
   private int effectMatch = 0;
   private final Map<Item, List<AutoBuy.RuntimeDataRecord>> cacheExecute = new HashMap<>();
   private final List<AutoBuy.RuntimeDataRecord> pointSynchronize = new ArrayList<>();
   private int sessionBind = Integer.MIN_VALUE;
   private final PurchaseTaskScheduler indexCancel = new PurchaseTaskScheduler();
   private boolean elementStop = false;
   private long itemAnimate = 0L;
   private long valueRead = 0L;
   private boolean configAlign = false;
   private long optionMeasure = 0L;
   private int stateFetch = 0;
   private long itemAttach = 0L;

   public AutoBuy() {
      source = this;
      this.handle(
         this.latest,
         this.summary,
         this.matrixBlend2,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute,
         this.providerFetch,
         this.profileDraw,
         this.vectorPerform,
         this.moduleCollect,
         this.eventAttach,
         this.serverRead,
         this.positionAdvance,
         this.providerClose,
         this.presetSave,
         this.windowConvert,
         this.presetWrite,
         this.frameCheck
      );
   }

   private long projectItem() {
      String var1 = this.latest.compute();
      if (var1.equals("FunTime")) {
         return 225L;
      } else {
         return !var1.equals("SpookyTime") && !var1.equals("HolyWorld") ? (long)this.providerFetch.compute() : this.worldEvaluate;
      }
   }

   private long computeResponse() {
      String var1 = this.latest.compute();
      if (var1.equals("FunTime")) {
         return 10L;
      } else {
         return !var1.equals("SpookyTime") && !var1.equals("HolyWorld") ? (long)this.profileDraw.compute() : this.playerProject;
      }
   }

   private void fetchProvider() {
      this.worldEvaluate = ThreadLocalRandom.current().nextLong(200L, 401L);
      this.playerProject = ThreadLocalRandom.current().nextLong(30L, 81L);
   }

   public static long handle(String var0) {
      for (AutoBuy.CacheEntry var2 : timerRender) {
         if (var2.data.toLowerCase(Locale.ROOT).contains(var0.toLowerCase(Locale.ROOT))) {
            int var3 = Math.max(1, var2.context);
            return Math.max(1L, (var2.config + var3 - 1L) / var3);
         }
      }

      return 0L;
   }

   public static boolean process(String var0) {
      String var1 = refresh(var0);
      if (var1.isEmpty()) {
         return false;
      }

      sourceSchedule.put(select(var1), var1);
      return true;
   }

   public static boolean compute(String var0) {
      String var1 = select(var0);
      return !var1.isEmpty() && sourceSchedule.remove(var1) != null;
   }

   public static boolean resolve(String var0) {
      String var1 = select(var0);
      return !var1.isEmpty() && sourceSchedule.containsKey(var1);
   }

   public static void refresh() {
      sourceSchedule.clear();
   }

   public static List<String> render() {
      return new ArrayList<>(sourceSchedule.values());
   }

   private static String select(String var0) {
      return refresh(var0).toLowerCase(Locale.ROOT);
   }

   private static String refresh(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.replaceAll("§.", "").replace(' ', ' ').trim();
      if (var1.startsWith("+")) {
         var1 = var1.substring(1).trim();
      }

      return var1;
   }

   public static AutoBuy.CacheEntry tick() {
      return timerRender.isEmpty() ? null : timerRender.get(0);
   }

   public static int drawAnimation() {
      int var0 = 0;
      long var1 = scaleSave;

      for (AutoBuy.CacheEntry var4 : timerRender) {
         if (var4.state >= var1) {
            var0++;
         }
      }

      return var0;
   }

   public static int encodePoint() {
      int var0 = 0;
      long var1 = scaleSave;

      for (AutoBuy.CacheEntry var4 : timerRender) {
         if (var4.state >= var1) {
            var0 += Math.max(1, var4.context);
         }
      }

      return var0;
   }

   public static long animate() {
      long var0 = 0L;
      long var2 = scaleSave;

      for (AutoBuy.CacheEntry var5 : timerRender) {
         if (var5.state >= var2) {
            var0 += Math.max(0L, var5.config);
         }
      }

      return var0;
   }

   public static long load() {
      return colorCompute > 0L && scaleAdapt > 0L ? scaleAdapt - colorCompute : 0L;
   }

   public File save() {
      File var1 = new File(WildClient.instance.cache, "configs/autobuy");
      if (!var1.exists()) {
         var1.mkdirs();
      }

      return var1;
   }

   public void update(String var1) {
      try {
         File var2 = this.save();
         File var3 = new File(var2, var1 + ".json");
         JsonObject var4 = this.serialize();

         try (FileWriter var5 = new FileWriter(var3)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(var4, var5);
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   public void apply(String var1) {
      try {
         File var2 = new File(this.save(), var1 + ".json");
         if (!var2.exists()) {
            return;
         }

         try (FileReader var3 = new FileReader(var2)) {
            JsonObject var4 = JsonParser.parseReader(var3).getAsJsonObject();
            this.handle(var4);
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void execute(String var1) {
      try {
         File var2 = new File(this.save(), var1 + ".json");
         if (var2.exists()) {
            var2.delete();
         }
      } catch (Exception var3) {
      }
   }

   public void handle(String var1, String var2) {
      if (!var1.equals(var2)) {
         try {
            File var3 = this.save();
            File var4 = new File(var3, var1 + ".json");
            File var5 = new File(var3, var2 + ".json");
            if (var4.exists() && !var5.exists()) {
               var4.renameTo(var5);
            }
         } catch (Exception var6) {
         }
      }
   }

   @Override
   public JsonObject serialize() {
      JsonObject var1 = super.serialize();
      JsonObject var2 = new JsonObject();
      JsonObject var3 = new JsonObject();

      for (Entry<String, Long> var5 : colorMeasure.entrySet()) {
         var3.addProperty(var5.getKey(), var5.getValue());
      }

      JsonObject var14 = new JsonObject();
      LinkedHashSet<String> var15 = new LinkedHashSet<>();
      var15.addAll(animationSchedule.keySet());
      var15.addAll(rendererScan.keySet());

      for (String var7 : var15) {
         int var8 = check(var7);
         int var9 = onTick(var7);
         if (var8 > 0 || var9 < 100) {
            JsonObject var10 = new JsonObject();
            var10.addProperty("min", var8);
            var10.addProperty("max", var9);
            var14.add(var7, var10);
         }
      }

      JsonObject var16 = new JsonObject();

      for (Entry<String, Set<String>> var19 : sourceBuild.entrySet()) {
         if (var19.getValue() != null && !var19.getValue().isEmpty()) {
            JsonArray var22 = new JsonArray();

            for (String var11 : var19.getValue()) {
               var22.add(var11);
            }

            var16.add(var19.getKey(), var22);
         }
      }

      JsonArray var18 = new JsonArray();

      for (String var23 : outputCollapse) {
         var18.add(var23);
      }

      JsonArray var21 = new JsonArray();

      for (String var27 : profileInvoke) {
         var21.add(var27);
      }

      JsonArray var25 = new JsonArray();

      for (String var30 : sourceSchedule.values()) {
         var25.add(var30);
      }

      JsonArray var29 = new JsonArray();

      for (AutoBuy.CacheEntry var12 : timerRender) {
         JsonObject var13 = new JsonObject();
         var13.addProperty("original", var12.instance);
         var13.addProperty("clean", var12.data);
         var13.addProperty("qty", var12.context);
         var13.addProperty("price", var12.config);
         var13.addProperty("time", var12.state);
         var29.add(var13);
      }

      var2.add("Prices", var3);
      var2.add("DurabilityRanges", var14);
      var2.add("DisabledEnchantments", var16);
      var2.add("ParseItems", var18);
      var2.add("InactiveItems", var21);
      var2.add("IgnoredSellers", var25);
      var2.add("History", var29);
      var1.add("AutoBuyData", var2);
      return var1;
   }

   @Override
   public void handle(JsonObject var1) {
      super.handle(var1);
      if (!this.latest.config.contains(this.latest.state)) {
         this.latest.current = 0;
         this.latest.state = this.latest.config.get(0);
      } else {
         this.latest.current = this.latest.config.indexOf(this.latest.state);
      }

      if (var1 != null && var1.has("AutoBuyData") && var1.get("AutoBuyData").isJsonObject()) {
         JsonObject var2 = var1.getAsJsonObject("AutoBuyData");
         colorMeasure.clear();
         animationSchedule.clear();
         rendererScan.clear();
         sourceBuild.clear();
         outputCollapse.clear();
         profileInvoke.clear();
         sourceSchedule.clear();
         timerRender.clear();
         if (var2.has("Prices") && var2.get("Prices").isJsonObject()) {
            JsonObject var3 = var2.getAsJsonObject("Prices");

            for (String var5 : var3.keySet()) {
               try {
                  colorMeasure.put(var5, var3.get(var5).getAsLong());
               } catch (Exception var12) {
               }
            }
         }

         if (var2.has("DurabilityRanges") && var2.get("DurabilityRanges").isJsonObject()) {
            JsonObject var14 = var2.getAsJsonObject("DurabilityRanges");

            for (String var28 : var14.keySet()) {
               try {
                  JsonObject var6 = var14.getAsJsonObject(var28);
                  handle(var28, var6.has("min") ? var6.get("min").getAsInt() : 0, var6.has("max") ? var6.get("max").getAsInt() : 100);
               } catch (Exception var11) {
               }
            }
         }

         if (var2.has("DurabilityThresholds") && var2.get("DurabilityThresholds").isJsonObject()) {
            JsonObject var15 = var2.getAsJsonObject("DurabilityThresholds");

            for (String var29 : var15.keySet()) {
               try {
                  handle(var29, var15.get(var29).getAsInt());
               } catch (Exception var10) {
               }
            }
         }

         if (var2.has("DisabledEnchantments") && var2.get("DisabledEnchantments").isJsonObject()) {
            JsonObject var16 = var2.getAsJsonObject("DisabledEnchantments");

            for (String var30 : var16.keySet()) {
               try {
                  JsonArray var32 = var16.getAsJsonArray(var30);
                  LinkedHashSet<String> var7 = new LinkedHashSet<>();

                  for (JsonElement var9 : var32) {
                     if (var9.isJsonPrimitive()) {
                        var7.add(HolyWorldHelper.update(var9.getAsString()));
                     }
                  }

                  if (!var7.isEmpty()) {
                     sourceBuild.put(var30, var7);
                  }
               } catch (Exception var13) {
               }
            }
         }

         if (var2.has("ParseItems") && var2.get("ParseItems").isJsonArray()) {
            for (JsonElement var24 : var2.getAsJsonArray("ParseItems")) {
               if (var24.isJsonPrimitive()) {
                  outputCollapse.add(var24.getAsString());
               }
            }
         }

         if (var2.has("InactiveItems") && var2.get("InactiveItems").isJsonArray()) {
            for (JsonElement var25 : var2.getAsJsonArray("InactiveItems")) {
               if (var25.isJsonPrimitive()) {
                  profileInvoke.add(var25.getAsString());
               }
            }
         }

         if (var2.has("IgnoredSellers") && var2.get("IgnoredSellers").isJsonArray()) {
            for (JsonElement var26 : var2.getAsJsonArray("IgnoredSellers")) {
               if (var26.isJsonPrimitive()) {
                  process(var26.getAsString());
               }
            }
         }

         if (var2.has("History") && var2.get("History").isJsonArray()) {
            for (JsonElement var27 : var2.getAsJsonArray("History")) {
               if (var27.isJsonObject()) {
                  JsonObject var31 = var27.getAsJsonObject();
                  timerRender.add(
                     new AutoBuy.CacheEntry(
                        var31.get("original").getAsString(),
                        var31.get("clean").getAsString(),
                        var31.get("qty").getAsInt(),
                        var31.get("price").getAsLong(),
                        var31.get("time").getAsLong()
                     )
                  );
               }
            }
         }

         this.encodeResult();
      }
   }

   @Override
   public void resetToDefaults() {
      super.resetToDefaults();
      colorMeasure.clear();
      animationSchedule.clear();
      rendererScan.clear();
      sourceBuild.clear();
      outputCollapse.clear();
      profileInvoke.clear();
      sourceSchedule.clear();
      timerRender.clear();
      this.encodeResult();
   }

   @Override
   public void handle() {
      super.handle();
      this.fetchProvider();
      this.renderTimer();
      this.saveScale();
      this.renderScale();
      this.computeColor();
      scaleSave = System.currentTimeMillis();
      this.inputAcquire = false;
      colorCompute = 0L;
      scaleAdapt = 0L;
      textureRun = 0L;
      if (this.latest.process("FunTime")) {
         try {
            NicknameUtil var1 = new NicknameUtil();
            var1.handle();
            String var2 = var1.apply();
            if (!var2.isEmpty() && !var2.equals("0")) {
               colorCompute = Long.parseLong(var2);
               scaleAdapt = colorCompute;
               this.inputAcquire = true;
            }
         } catch (Exception var3) {
         }
      }

      this.rendererCancel.handle();
      this.indexSave.handle();
      this.indexCheck.handle();
      this.settingSchedule.handle();
      this.indexLoad = false;
      this.layoutSave = 0;
      this.outputFetch = "";
      this.entityFilter = 0L;
      this.layerSample2 = 0L;
      this.providerOffset = false;
      this.messageParse2 = false;
      this.inputInvoke = false;
      this.optionFetch = -1;
      this.eventCollapse = 0L;
      this.stateAttach = 0L;
      this.blockRun = false;
      this.scaleParse = false;
      this.elementTick = 0;
      this.resourceClamp = false;
      this.handlerRun = "";
      this.sourceCancel = 0L;
      this.eventSend = 0L;
      this.shaderProject = false;
      this.savePacket();
      this.scheduleAnimation();
      this.animateEntry();
      this.encodeResult();
      this.indexCancel.handle();
      this.elementStop = false;
      this.itemAnimate = 0L;
      this.valueRead = 0L;
      this.advancePosition();
      AuctionTradeExecutor.handle();
      this.regionAlign = this.summary.compute();
   }

   private void drawProfile() {
      if (!this.eventAttach.compute()) {
         this.indexCancel.compute();
         this.elementStop = false;
      } else {
         this.indexCancel.resolve();
         long var1 = System.currentTimeMillis();
         boolean var3 = this.indexCancel.update();
         if (var3 && !this.elementStop) {
            this.elementStop = true;
            this.valueRead = var1;
            if (ClientUtil.source.compute()) {
               TelegramApi.handle(
                  "[AutoBuy] Сервер замедлил аукцион: отклик ~"
                     + this.indexCancel.execute()
                     + "мс, норма ~"
                     + this.indexCancel.prepare()
                     + "мс, пинг "
                     + this.checkFrame()
               );
            }

            if (this.performVector()) {
               this.attachEvent();
            }
         } else if (!var3 && this.elementStop) {
            this.elementStop = false;
         }

         if (this.positionAdvance.compute() && this.indexCancel.select() > 0 && var1 - this.itemAnimate >= 2000L) {
            this.itemAnimate = var1;
            ChatLogger.handle("§7[AutoBuy] " + this.indexCancel.check());
         }
      }
   }

   private boolean performVector() {
      return this.serverRead.compute()
         && (this.latest.process("FunTime") || this.latest.process("HolyWorld"))
         && !target
         && !pending
         && !this.blockRun
         && !this.summary.compute()
         && !this.scaleParse
         && !this.collectPlayer()
         && !this.configAlign;
   }

   private void attachEvent() {
      long var1 = System.currentTimeMillis();
      this.stateFetch = var1 - this.itemAttach <= 240000L ? Math.min(2, this.stateFetch + 1) : 0;
      this.itemAttach = var1;
      this.indexCancel.process();
      this.elementStop = false;
      if (this.latest.process("FunTime") && this.stateFetch >= 1) {
         if (this.stateFetch >= 2) {
            this.applyState();
         } else {
            this.parseMessage();
         }
      } else {
         this.configAlign = true;
         this.optionMeasure = var1 + ThreadLocalRandom.current().nextLong(4000L, 8001L);
         this.indexLoad = false;
         this.providerOffset = false;
         this.outputFetch = "";
         this.savePacket();
         if (Module.client.player != null && Module.client.currentScreen != null) {
            Module.client.player.closeScreen();
         }
      }
   }

   private boolean readServer() {
      if (!this.configAlign) {
         return false;
      }

      if (Module.client.player != null && Module.client.currentScreen != null) {
         Module.client.player.closeScreen();
      }

      if (System.currentTimeMillis() < this.optionMeasure) {
         return true;
      }

      this.configAlign = false;
      this.optionMeasure = 0L;
      this.handle(0L, false);
      return true;
   }

   private void advancePosition() {
      this.configAlign = false;
      this.optionMeasure = 0L;
      this.stateFetch = 0;
      this.itemAttach = 0L;
   }

   private String checkFrame() {
      int var1 = this.collectModule();
      return var1 < 0 ? "?" : var1 + "мс";
   }

   private int collectModule() {
      if (Module.client.player != null && Module.client.getNetworkHandler() != null) {
         PlayerListEntry var1 = Module.client.getNetworkHandler().getPlayerListEntry(Module.client.player.getUuid());
         return var1 == null ? -1 : var1.getLatency();
      } else {
         return -1;
      }
   }

   public boolean submit() {
      return this.eventAttach.compute() && this.indexCancel.update();
   }

   public PurchaseTaskScheduler unload() {
      return this.indexCancel;
   }

   private void closeProvider() {
      if (this.latest.process("FunTime")) {
         try {
            long var1 = System.currentTimeMillis();
            if (var1 - this.stateAttach < 1000L) {
               return;
            }

            this.stateAttach = var1;
            NicknameUtil var3 = new NicknameUtil();
            var3.handle();
            String var4 = var3.apply();
            if (var4.isEmpty() || var4.equals("0")) {
               return;
            }

            scaleAdapt = Long.parseLong(var4);
            if (textureRun == 0L && colorCompute > 0L && animate() > 0L && scaleAdapt >= colorCompute) {
               textureRun = System.currentTimeMillis();
            }
         } catch (Exception var5) {
         }
      }
   }

   private void savePreset() {
      if (this.latest.process("FunTime")) {
         if (!this.inputAcquire) {
            try {
               NicknameUtil var1 = new NicknameUtil();
               var1.handle();
               String var2 = var1.apply();
               if (!var2.isEmpty() && !var2.equals("0")) {
                  colorCompute = Long.parseLong(var2);
                  scaleAdapt = colorCompute;
                  this.inputAcquire = true;
               }
            } catch (Exception var3) {
            }
         } else {
            this.closeProvider();
         }
      }
   }

   @Override
   public void process() {
      boolean var1 = this.summary.compute();
      if (!var1) {
         this.scanEffect();
      }

      this.indexLoad = false;
      this.layoutSave = 0;
      this.blockRun = false;
      this.playerEvaluate = "";
      this.outputFetch = "";
      this.entityFilter = 0L;
      this.layerSample2 = 0L;
      this.providerOffset = false;
      this.messageParse2 = false;
      this.inputInvoke = false;
      this.optionFetch = -1;
      this.eventCollapse = 0L;
      this.savePacket();
      this.scheduleAnimation();
      this.animateEntry();
      this.indexCancel.compute();
      this.elementStop = false;
      this.advancePosition();
      this.inputAcquire = false;
      AuctionTradeExecutor.handle();
      this.renderTimer();
      this.saveScale();
      this.renderScale();
      RctCommand var2 = RctCommand.resolve();
      if (var2 != null) {
         var2.handle(false);
      }

      super.process();
      if (var1) {
         this.submitPoint();
      }
   }

   private void convertWindow() {
      RctCommand var1 = RctCommand.resolve();
      if (var1 != null) {
         var1.handle(this.enabled && this.responseCompute.compute() && this.latest.process("FunTime"));
      }
   }

   public static void fetch() {
      AutoBuy var0 = source;
      if (var0 != null) {
         var0.scanEffect();
         var0.indexLoad = false;
         var0.layoutSave = 0;
         var0.blockRun = false;
         var0.playerEvaluate = "";
         var0.outputFetch = "";
         var0.entityFilter = 0L;
         var0.layerSample2 = 0L;
         var0.providerOffset = false;
         var0.messageParse2 = false;
         var0.inputInvoke = false;
         var0.optionFetch = -1;
         var0.eventCollapse = 0L;
         var0.scaleParse = false;
         var0.elementTick = 0;
         var0.resourceClamp = false;
         var0.handlerRun = "";
         var0.sourceCancel = 0L;
         var0.eventSend = 0L;
         var0.shaderProject = false;
         var0.renderTimer();
         var0.saveScale();
         var0.renderScale();
         var0.advancePosition();
      }

      target = false;
      pending = false;
      previous = false;
      AuctionTradeExecutor.handle();
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (this.enabled) {
         if (var1.apply() == 1 && var1.resolve() == this.frameCheck.compute() && Module.client.currentScreen == null) {
            Module.client.setScreen(new ServerPresets());
            var1.process();
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         this.convertWindow();
         if (!this.enabled) {
            this.parseOption();
            this.readAction();
         } else {
            this.readAction();
            AuctionTradeExecutor.process();
            this.drawProfile();
            this.writePreset();
            if (!this.buildSource()) {
               if (!this.vectorMatch.compute() && !this.resourceClamp) {
                  this.indexSave.handle();
               }

               this.renderMatrix();
               if (this.blockRun) {
                  if (this.rangeRelease.update(1500L)) {
                     Module.client.player.networkHandler.sendChatCommand("an" + this.playerEvaluate);
                     this.blockRun = false;
                     this.projectItem(this.playerEvaluate);
                  }
               } else if (!this.latest.process("FunTime") && !this.latest.process("HolyWorld")
                  || this.summary.compute()
                  || !this.indexLoad
                  || !this.expandContext()) {
                  if (!target && !pending) {
                     if (!this.latest.process("FunTime") || this.summary.compute() || !this.tickModule()) {
                        this.savePreset();
                        boolean var2 = this.summary.compute();
                        if (var2 && !this.regionAlign) {
                           var2 = this.update(false);
                        } else if (!var2 && this.regionAlign) {
                           this.measureTimer();
                        }

                        this.regionAlign = var2;
                        if (var2 && !this.sessionEncode.isEmpty()) {
                           this.measure();
                           this.readAction();
                        } else if (!this.readServer()) {
                           if (this.collectPlayer()) {
                              this.filterMatrix();
                           } else {
                              if (this.latest.process("FunTime") && !this.summary.compute()) {
                                 if (this.encodeVector()) {
                                    return;
                                 }

                                 if (this.rendererCancel.update(80000L)) {
                                    this.parseMessage();
                                    return;
                                 }

                                 if (this.indexLoad && this.expandContext()) {
                                    return;
                                 }

                                 if (this.convertAction()) {
                                    return;
                                 }
                              }

                              if (Module.client.currentScreen instanceof GenericContainerScreen var4) {
                                 GenericContainerScreenHandler var5 = (GenericContainerScreenHandler)var4.getScreenHandler();
                                 if (this.compute(var4)) {
                                    if (this.packetRead.update((long)this.vectorPerform.compute())) {
                                       int var10 = this.resolve(var5);
                                       if (var10 != -1 && this.apply(var4)) {
                                          Module.client.interactionManager.clickSlot(var5.syncId, var10, 0, SlotActionType.PICKUP, Module.client.player);
                                       } else {
                                          this.execute(var5);
                                       }

                                       this.packetRead.handle();
                                    }

                                    return;
                                 }

                                 if (this.resolve(var4) && !this.scaleParse) {
                                    if (this.latest.process("HolyWorld")) {
                                       this.process(var4);
                                       return;
                                    }

                                    boolean var6 = false;

                                    for (int var7 = 0; var7 < 45; var7++) {
                                       Slot var8 = var5.getSlot(var7);
                                       if (this.process(var8)) {
                                          String var9 = this.resolve(var8);
                                          if (var9 != null) {
                                             var6 = true;
                                             if (this.handle(var7, var9, var8) && this.playerUpdate.update(this.computeResponse())) {
                                                if (!this.latest.process("FunTime") && !this.latest.process("SpookyTime")) {
                                                   this.animate(var9);
                                                   Module.client.interactionManager.clickSlot(var5.syncId, var7, 0, SlotActionType.PICKUP, Module.client.player);
                                                } else {
                                                   this.pathProject = System.currentTimeMillis();
                                                   Module.client.interactionManager.clickSlot(var5.syncId, var7, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                                                }

                                                this.scheduleAnimation();
                                                this.playerUpdate.handle();
                                                this.packetRead.handle();
                                                this.fetchProvider();
                                                return;
                                             }
                                             break;
                                          }
                                       }
                                    }

                                    if (!var6) {
                                       this.scheduleAnimation();
                                    }

                                    if (!var6 && this.bufferAdapt.update(this.projectItem()) && this.handle(var5)) {
                                       this.bufferAdapt.handle();
                                       this.fetchProvider();
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         this.renderScale();
      }
   }

   private void writePreset() {
      if (this.pathProject != 0L) {
         if (!this.latest.process("FunTime") && !this.latest.process("SpookyTime")) {
            this.pathProject = 0L;
         } else if (System.currentTimeMillis() - this.pathProject >= 2000L) {
            this.pathProject = 0L;
            if (!this.indexLoad && !target && !pending && !this.blockRun && !this.summary.compute() && !this.scaleParse) {
               this.handle(0L, true);
            }
         }
      }
   }

   private int measureColor() {
      if (Module.client.currentScreen == null) {
         return -1;
      }

      String var1 = this.encodePoint(Module.client.currentScreen.getTitle().getString());
      if (var1.isEmpty()) {
         return -1;
      }

      Matcher var2 = indexBind.matcher(var1);
      if (!var2.find()) {
         return -1;
      }

      String var3 = var2.group(1);
      if (var3 == null) {
         var3 = var2.group(2);
      }

      if (var3 == null) {
         var3 = var2.group(3);
      }

      if (var3 == null) {
         return -1;
      }

      try {
         int var4 = Integer.parseInt(var3);
         return var4 < 1 ? -1 : var4;
      } catch (NumberFormatException var5) {
         return -1;
      }
   }

   private boolean handle(ScreenHandler var1) {
      int var2 = var1.syncId;
      if (this.latest.process("FunTime")) {
         int var3 = this.measureColor();
         int var4;
         if (var3 > 1) {
            var4 = 48;
         } else if (var3 == 1) {
            var4 = 50;
         } else {
            var4 = this.inputHandle;
            this.inputHandle = var4 == 50 ? 48 : 50;
         }

         if (var4 >= 0 && var4 < var1.slots.size()) {
            this.indexCancel.handle(var2);
            Module.client.interactionManager.clickSlot(var2, var4, 0, SlotActionType.PICKUP, Module.client.player);
            return true;
         } else {
            return false;
         }
      } else if (var1.slots.size() > 49) {
         this.indexCancel.handle(var2);
         Module.client.interactionManager.clickSlot(var2, 49, 0, SlotActionType.PICKUP, Module.client.player);
         return true;
      } else {
         return false;
      }
   }

   @EventHandler
   public void handle(CameraRotationEvent var1) {
      if (Module.client.player != null) {
         if (this.enabled && this.latest.process("HolyWorld") && this.taskInterpolate != 0) {
            this.compute(var1);
         } else {
            if (this.adaptScale()) {
               this.process(var1);
            }
         }
      }
   }

   private void process(GenericContainerScreen var1) {
      ScreenHandler var2 = var1.getScreenHandler();
      boolean var3 = false;
      int var4 = Math.min(45, var2.slots.size());

      for (int var5 = 0; var5 < var4; var5++) {
         Slot var6 = var2.getSlot(var5);
         if (this.process(var6)) {
            String var7 = this.resolve(var6);
            if (var7 != null) {
               var3 = true;
               if (this.handle(var5, var7) && this.playerUpdate.update(this.computeResponse())) {
                  this.invokeProfile();
                  this.animate(var7);
                  Module.client.interactionManager.clickSlot(var2.syncId, var5, 0, SlotActionType.PICKUP, Module.client.player);
                  this.scanRenderer();
                  this.playerUpdate.handle();
                  this.packetRead.handle();
                  this.fetchProvider();
                  return;
               }
               break;
            }
         }
      }

      if (!var3 && this.bufferAdapt.update(this.projectItem())) {
         int var8 = this.process(var2);
         if (var8 != -1) {
            this.indexCancel.handle(var2.syncId);
            Module.client.interactionManager.clickSlot(var2.syncId, var8, 0, SlotActionType.PICKUP, Module.client.player);
            this.scanRenderer();
            this.bufferAdapt.handle();
            this.fetchProvider();
         }
      } else if (!var3) {
         this.scanRenderer();
      }
   }

   private boolean handle(int var1, String var2, Slot var3) {
      long var4 = (long)this.moduleCollect.compute();
      if (var4 <= 0L) {
         this.scheduleAnimation();
         return true;
      }

      int var6 = var3 == null ? 0 : this.resolve(var3.getStack());
      long var7 = System.currentTimeMillis();
      if (this.mousePrepare == var1 && this.profileMatch == var6 && Objects.equals(this.sessionAdapt, var2)) {
         return var7 - this.entityAdvance >= var4;
      }

      this.mousePrepare = var1;
      this.sessionAdapt = var2;
      this.profileMatch = var6;
      this.entityAdvance = var7;
      return false;
   }

   private void scheduleAnimation() {
      this.mousePrepare = -1;
      this.sessionAdapt = "";
      this.profileMatch = 0;
      this.entityAdvance = 0L;
   }

   private boolean handle(int var1, String var2) {
      long var3 = System.currentTimeMillis();
      if (this.pathCheck == var1 && Objects.equals(this.contextParse, var2)) {
         return var3 - this.optionStop >= 90L;
      }

      this.pathCheck = var1;
      this.contextParse = var2;
      this.optionStop = var3;
      return false;
   }

   private void scanRenderer() {
      this.pathCheck = -1;
      this.contextParse = "";
      this.optionStop = 0L;
   }

   private int process(ScreenHandler var1) {
      int var2 = this.update(var1);
      if (var2 <= 0) {
         var2 = Math.min(54, var1.slots.size());
      }

      for (int var3 = Math.min(45, var2); var3 < var2; var3++) {
         if (this.process(var1.getSlot(var3).getStack())) {
            return var3;
         }
      }

      for (int var4 = 0; var4 < var2; var4++) {
         if (this.process(var1.getSlot(var4).getStack())) {
            return var4;
         }
      }

      return -1;
   }

   private boolean process(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && var1.isOf(Items.EMERALD) ? this.load(this.onTick(var1)).contains("обновитьаукцион") : false;
   }

   private boolean buildSource() {
      if (!this.latest.process("HolyWorld")) {
         this.renderTimer();
         return false;
      }

      long var1 = System.currentTimeMillis();
      if (this.taskInterpolate != 0 && var1 >= this.scaleSetup) {
         this.scheduleSource();
         return true;
      }

      if (this.taskInterpolate == 3 && this.playerSave) {
         return true;
      }

      if (this.handle(var1)) {
         this.collapseOutput();
         this.computeColor();
      }

      return false;
   }

   private boolean handle(long var1) {
      if (this.taskInterpolate != 0) {
         return false;
      } else if (var1 < this.playerMatch) {
         return false;
      } else if (!target && !pending && !this.blockRun && !this.summary.compute() && !this.scaleParse && !this.indexLoad) {
         return Module.client.currentScreen instanceof GenericContainerScreen var3 ? this.resolve(var3) : false;
      } else {
         return false;
      }
   }

   private void collapseOutput() {
      double var1 = ThreadLocalRandom.current().nextDouble();
      if (var1 < 0.42) {
         this.handle(4, ThreadLocalRandom.current().nextLong(650L, 2200L));
      } else if (var1 < 0.7) {
         this.handle(1, ThreadLocalRandom.current().nextLong(1000L, 2800L));
      } else {
         this.handle(2, ThreadLocalRandom.current().nextLong(1200L, 3000L));
      }
   }

   private void invokeProfile() {
      if (this.taskInterpolate == 0 && Module.client.player != null) {
         this.handle(2, ThreadLocalRandom.current().nextLong(900L, 1501L));
      }
   }

   private void compute(boolean var1) {
      boolean var2 = !var1 || AuctionTradeExecutor.compute();
      this.playerSave = var2;
      this.requestRun = var1;
      if (Module.client.player != null) {
         this.handle(3, ThreadLocalRandom.current().nextLong(900L, 1601L));
      } else if (var2) {
         AuctionTradeExecutor.compute(var1);
         return;
      }

      if (!var2) {
         AuctionTradeExecutor.compute(true);
      }
   }

   private void handle(int var1, long var2) {
      this.taskInterpolate = var1;
      this.cacheClose = System.currentTimeMillis();
      this.scaleSetup = this.cacheClose + var2;
      this.indexSynchronize = 0L;
      this.frameProject = Module.client.player.getYaw();
      this.dataRelease = Module.client.player.getPitch();
      this.vectorRun = (float)ThreadLocalRandom.current().nextDouble(0.0, Math.PI * 2);
      this.providerSynchronize = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
      this.sessionAdvance = 1.0F;
      this.keySample = (float)ThreadLocalRandom.current().nextDouble(0.06, 0.32);
      if (var1 == 4) {
         int var4 = ThreadLocalRandom.current().nextInt(1, 4);
         this.pathProcess = 360.0F * var4 + (float)ThreadLocalRandom.current().nextDouble(-90.0, 90.0);
         this.positionReset = (float)ThreadLocalRandom.current().nextDouble(8.0, 45.0);
         this.effectApply = 0.0F;
         this.cacheHandle2 = (float)ThreadLocalRandom.current().nextDouble(-15.0, 15.0);
         this.sessionAdvance = (float)ThreadLocalRandom.current().nextDouble(1.0, 4.0);
      } else if (var1 == 1) {
         this.pathProcess = (float)ThreadLocalRandom.current().nextDouble(18.0, 55.0);
         this.positionReset = (float)ThreadLocalRandom.current().nextDouble(5.0, 18.0);
         this.effectApply = this.providerSynchronize * (float)ThreadLocalRandom.current().nextDouble(8.0, 40.0);
         this.cacheHandle2 = (float)ThreadLocalRandom.current().nextDouble(-6.0, 6.0);
      } else if (var1 == 2) {
         this.pathProcess = (float)ThreadLocalRandom.current().nextDouble(8.0, 32.0);
         this.positionReset = (float)ThreadLocalRandom.current().nextDouble(3.0, 13.0);
         this.effectApply = this.providerSynchronize * (float)ThreadLocalRandom.current().nextDouble(4.0, 20.0);
         this.cacheHandle2 = (float)ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
      } else {
         this.pathProcess = (float)ThreadLocalRandom.current().nextDouble(12.0, 40.0);
         this.positionReset = (float)ThreadLocalRandom.current().nextDouble(-8.0, 8.0);
         this.effectApply = this.providerSynchronize * (float)ThreadLocalRandom.current().nextDouble(10.0, 30.0);
         this.cacheHandle2 = (float)ThreadLocalRandom.current().nextDouble(-6.0, 6.0);
      }
   }

   private void scheduleSource() {
      boolean var1 = this.sourceRefresh;
      boolean var2 = this.playerSave;
      boolean var3 = this.requestRun;
      this.renderTimer();
      if (var2) {
         AuctionTradeExecutor.compute(var3);
      } else {
         if (var1 && Module.client.player != null) {
            Module.client.player.networkHandler.sendChatCommand("ah");
         }
      }
   }

   private void renderTimer() {
      this.taskInterpolate = 0;
      this.cacheClose = 0L;
      this.scaleSetup = 0L;
      this.indexSynchronize = 0L;
      this.sourceRefresh = false;
      this.playerSave = false;
      this.requestRun = false;
      this.effectApply = 0.0F;
      this.cacheHandle2 = 0.0F;
      this.scanRenderer();
   }

   private void saveScale() {
      this.playerPerform = 0L;
      this.taskLoad = 0.0F;
   }

   private void computeColor() {
      this.playerMatch = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(1200L, 4501L);
   }

   private boolean adaptScale() {
      if (!this.latest.process("FunTime") || Module.client.world == null || Module.client.player == null) {
         this.saveScale();
         return false;
      } else if (!this.enabled && !this.summary.compute() && !this.scaleParse && !this.resourceClamp) {
         this.saveScale();
         return false;
      } else {
         return true;
      }
   }

   private void process(CameraRotationEvent var1) {
      float var2 = this.bindIndex();
      this.taskLoad += 0.185F * var2;
      if (this.taskLoad > (float) (Math.PI * 2)) {
         this.taskLoad = this.taskLoad - (float) (Math.PI * 2) * (float)Math.floor(this.taskLoad / (float) (Math.PI * 2));
      }

      float var3 = this.taskLoad;
      float var4 = (float)Math.sin(var3) * 0.82F + (float)Math.sin(var3 * 2.25F + 0.75F) * 0.16F + (float)Math.cos(var3 * 2.35F) * 0.06F;
      float var5 = (float)Math.cos(var3 * 1.18F + 0.45F) * 0.28F + (float)Math.sin(var3 * 2.05F) * 0.07F;
      var1.handle(var1.compute() + var4);
      var1.process(MathHelper.clamp(var1.resolve() + var5, -89.0F, 89.0F));
   }

   private void compute(CameraRotationEvent var1) {
      long var2 = System.currentTimeMillis();
      float var4 = Math.max(1.0F, (float)(this.scaleSetup - this.cacheClose));
      float var5 = MathHelper.clamp((float)(var2 - this.cacheClose) / var4, 0.0F, 1.0F);
      float var6 = var5 * var5 * (3.0F - 2.0F * var5);
      if (this.taskInterpolate == 4) {
         float var11 = this.frameProject + this.providerSynchronize * this.pathProcess * var6;
         float var13 = this.dataRelease
            + (float)Math.sin(this.vectorRun + var6 * (float) (Math.PI * 2) * this.sessionAdvance) * this.positionReset
            + this.cacheHandle2 * var6;
         this.process(var11, var13, var1);
      } else {
         float var7 = this.frameProject;
         float var8 = this.dataRelease;
         if (this.taskInterpolate == 1) {
            float var9 = this.vectorRun + this.providerSynchronize * var6 * (float) (Math.PI * 11.0 / 5.0);
            var7 += (float)Math.sin(var9) * this.pathProcess + this.providerSynchronize * var6 * 4.0F;
            var8 += (float)Math.cos(var9 * 0.85F) * this.positionReset;
         } else if (this.taskInterpolate == 2) {
            float var14 = this.vectorRun + var6 * 5.3407073F;
            var7 += (float)Math.sin(var14) * this.pathProcess;
            var8 += (float)Math.sin(var14 * 0.55F) * this.positionReset;
         } else if (this.taskInterpolate == 3) {
            var7 += this.providerSynchronize * this.pathProcess * var6 + (float)Math.sin(this.vectorRun + var6 * Math.PI) * 1.8F;
            var8 += this.positionReset * var6;
         }

         var7 += this.effectApply * var6;
         var8 += this.cacheHandle2 * var6;
         this.handle(var7, var8, var1);
      }
   }

   private void handle(float var1, float var2, CameraRotationEvent var3) {
      float var4 = Module.client.player.getYaw();
      float var5 = Module.client.player.getPitch();
      float var6 = this.runTexture();
      float var7 = this.keySample;
      float var8 = 1.0F - (float)Math.pow(1.0F - var7, var6);
      float var9 = var4 + MathHelper.wrapDegrees(var1 - var4) * var8;
      float var10 = var5 + (MathHelper.clamp(var2, -89.0F, 89.0F) - var5) * var8;
      Module.client.player.setYaw(var9);
      Module.client.player.setPitch(var10);
      Module.client.player.headYaw = var9;
      var3.handle(var9);
      var3.process(var10);
   }

   private void process(float var1, float var2, CameraRotationEvent var3) {
      float var4 = MathHelper.clamp(var2, -89.0F, 89.0F);
      Module.client.player.setYaw(var1);
      Module.client.player.setPitch(var4);
      Module.client.player.headYaw = var1;
      var3.handle(var1);
      var3.process(var4);
   }

   private float runTexture() {
      long var1 = System.nanoTime();
      if (this.indexSynchronize == 0L) {
         this.indexSynchronize = var1;
         return 1.0F;
      } else {
         float var3 = (float)(var1 - this.indexSynchronize) / 1.6666667E7F;
         this.indexSynchronize = var1;
         return MathHelper.clamp(var3, 0.25F, 4.0F);
      }
   }

   private float bindIndex() {
      long var1 = System.nanoTime();
      if (this.playerPerform == 0L) {
         this.playerPerform = var1;
         this.taskLoad = (float)ThreadLocalRandom.current().nextDouble(0.0, (float) (Math.PI * 2));
         return 1.0F;
      } else {
         float var3 = (float)(var1 - this.playerPerform) / 1.6666667E7F;
         this.playerPerform = var1;
         return MathHelper.clamp(var3, 0.25F, 4.0F);
      }
   }

   private void readAction() {
      if (this.collapseConfig()) {
         this.refreshClient();
      } else if (!this.validateData()) {
         this.renderScale();
      } else {
         if (!this.pointSend) {
            this.pointSend = true;
            this.clientSubmit = System.currentTimeMillis();
            this.pointSample = Module.client.player.getX();
            this.regionRefresh = Module.client.player.getZ();
         }

         long var1 = Math.max(0L, System.currentTimeMillis() - this.clientSubmit);
         boolean var3 = (var1 / 75L & 1L) == 0L;
         Boolean var4 = this.resolve(var3);
         if (var4 == null) {
            this.refreshClient();
         } else {
            this.handle(var4, !var4);
         }
      }
   }

   private boolean collapseConfig() {
      return this.latest.process("FunTime")
         && Module.client.world != null
         && Module.client.player != null
         && Module.client.currentScreen != null
         && (this.summary.compute() || this.scaleParse || this.resourceClamp);
   }

   private boolean validateData() {
      return this.latest.process("FunTime")
         && Module.client.world != null
         && Module.client.player != null
         && Module.client.currentScreen == null
         && (this.summary.compute() || this.scaleParse || this.resourceClamp || this.collectPlayer() || this.configAlign && this.enabled)
         && !target
         && !pending
         && !this.blockRun;
   }

   private void handle(boolean var1, boolean var2) {
      if (Module.client.options != null && Module.client.player != null) {
         Module.client.options.forwardKey.setPressed(false);
         Module.client.options.backKey.setPressed(false);
         Module.client.options.leftKey.setPressed(var1);
         Module.client.options.rightKey.setPressed(var2);
         this.handle(false, false, var1, var2);
      }
   }

   private Boolean resolve(boolean var1) {
      if (Module.client.player == null) {
         return var1;
      } else {
         double var2 = Module.client.player.getX() - this.pointSample;
         double var4 = Module.client.player.getZ() - this.regionRefresh;
         double var6 = var2 * var2 + var4 * var4;
         if (var6 <= 1.0) {
            return var1;
         } else {
            double var8 = Math.toRadians(Module.client.player.getYaw());
            double var10 = Math.cos(var8);
            double var12 = Math.sin(var8);
            double var14 = -var2;
            double var16 = -var4;
            double var18 = var10 * var14 + var12 * var16;
            if (Math.abs(var18) > 0.0025) {
               return var18 > 0.0;
            } else {
               return var6 >= 4.0 ? null : var1;
            }
         }
      }
   }

   private void renderScale() {
      if (this.pointSend) {
         this.pointSend = false;
         this.clientSubmit = 0L;
         this.pointSample = 0.0;
         this.regionRefresh = 0.0;
         if (Module.client.options != null) {
            this.handle(Module.client.options.forwardKey);
            this.handle(Module.client.options.backKey);
            this.handle(Module.client.options.leftKey);
            this.handle(Module.client.options.rightKey);
            this.handle(
               Module.client.options.forwardKey.isPressed(),
               Module.client.options.backKey.isPressed(),
               Module.client.options.leftKey.isPressed(),
               Module.client.options.rightKey.isPressed()
            );
         }
      }
   }

   private void refreshClient() {
      if (Module.client.options != null) {
         Module.client.options.forwardKey.setPressed(false);
         Module.client.options.backKey.setPressed(false);
         Module.client.options.leftKey.setPressed(false);
         Module.client.options.rightKey.setPressed(false);
         this.handle(false, false, false, false);
      }
   }

   private void handle(boolean var1, boolean var2, boolean var3, boolean var4) {
      if (Module.client.player != null && Module.client.player.input != null && Module.client.player.input.playerInput != null) {
         PlayerInput var5 = Module.client.player.input.playerInput;
         Module.client.player.input.playerInput = new PlayerInput(var1, var2, var3, var4, var5.jump(), var5.sneak(), var5.sprint());
      }
   }

   private void handle(KeyBinding var1) {
      if (var1 != null) {
         var1.setPressed(this.process(var1));
      }
   }

   private boolean process(KeyBinding var1) {
      return Module.client.getWindow() != null && var1 != null && InputUtil.isKeyPressed(Module.client.getWindow().getHandle(), var1.getDefaultKey().getCode());
   }

   public void measure() {
      if (!this.summary.compute()) {
         this.scanEffect();
      } else if (!this.sessionEncode.isEmpty()) {
         if (this.elementTick >= this.sessionEncode.size()) {
            this.adaptRequest();
         } else {
            if (!this.scaleParse) {
               if (!this.processWindow()) {
                  return;
               }

               if (this.screenSubmit.update(1000L) && Module.client.player != null) {
                  String var1 = this.sessionEncode.get(this.elementTick);
                  this.render(var1);
                  String var2 = this.drawAnimation(var1);
                  Module.client.player.networkHandler.sendChatCommand("ah search " + var2);
                  this.scaleParse = true;
                  this.cacheHandle.handle();
               }
            } else {
               if (!this.cacheHandle.update(600L)) {
                  return;
               }

               Screen var10 = Module.client.currentScreen;
               if (!(var10 instanceof GenericContainerScreen) && this.cacheHandle.update(2500L)) {
                  String var12 = this.sessionEncode.get(this.elementTick);
                  boolean var13 = this.handle(var12, this.tick(var12), "не открылся результат поиска");
                  if (var13) {
                     this.elementTick++;
                  }

                  if (!var13) {
                     ChatLogger.handle("§e[AutoParse] Ожидание кулдауна, повторный поиск: " + this.tick(var12));
                  }

                  this.scaleParse = false;
                  this.screenSubmit.handle();
                  if (Module.client.player != null && Module.client.currentScreen != null) {
                     Module.client.player.closeScreen();
                  }

                  return;
               }

               if (var10 instanceof GenericContainerScreen var11) {
                  String var3 = this.sessionEncode.get(this.elementTick);
                  if (this.resolve(var11) || this.handle(var11, var3)) {
                     String var4 = this.tick(var3);
                     int var5 = (int)this.matrixBlend2.compute();
                     AutoBuy.FallbackDataRecord var6 = this.process(var11, var3);
                     boolean var7 = false;
                     if (var6 != null) {
                        long var8 = this.handle(var6.unitPrice(), var5);
                        colorMeasure.put(var3, var8);
                        this.filterKey();
                        var7 = true;
                        ChatLogger.handle(
                           "§d[AutoParse] §f"
                              + var4
                              + ": мин. за 1 шт. §e"
                              + var6.unitPrice()
                              + "$ §7(лот "
                              + var6.lotPrice()
                              + "$ x"
                              + var6.count()
                              + ") §f(-"
                              + var5
                              + "%) -> ставим §a"
                              + var8
                              + "$"
                        );
                     } else {
                        var7 = this.handle(var3, var4, "не найден на странице");
                        if (!var7) {
                           ChatLogger.handle("§c[AutoParse] §f" + var4 + " не найден на странице.");
                        }
                     }

                     this.scaleParse = false;
                     if (var7) {
                        this.elementTick++;
                     }

                     this.screenSubmit.handle();
                     if (Module.client.player != null) {
                        Module.client.player.closeScreen();
                     }
                  }
               }
            }
         }
      }
   }

   private void render(String var1) {
      if (!Objects.equals(this.keyCheck, var1)) {
         this.keyCheck = var1 == null ? "" : var1;
         this.layerProject = 0;
      }
   }

   private boolean handle(String var1, String var2, String var3) {
      this.render(var1);
      if (this.layerProject >= 3) {
         ChatLogger.handle("§c[AutoParse] §f" + var2 + " пропущен: " + var3 + " после 3 повторных поисков.");
         this.filterKey();
         return true;
      } else {
         this.layerProject++;
         ChatLogger.handle("§e[AutoParse] §f" + var2 + ": " + var3 + ", повторный поиск " + this.layerProject + "/3.");
         return false;
      }
   }

   private void filterKey() {
      this.keyCheck = "";
      this.layerProject = 0;
   }

   private boolean update(boolean var1) {
      String var2 = this.latest.compute();
      LinkedHashSet<String> var3 = new LinkedHashSet<>();

      for (String var5 : outputCollapse) {
         if (this.compute(var5, var2)) {
            var3.add(var5);
         }
      }

      for (String var7 : colorMeasure.keySet()) {
         if (this.compute(var7, var2)) {
            var3.add(var7);
         }
      }

      this.sessionEncode.clear();
      this.sessionEncode.addAll(var3);
      this.elementTick = 0;
      this.scaleParse = false;
      this.handlerRun = "";
      this.sourceCancel = 0L;
      this.eventSend = 0L;
      this.shaderProject = false;
      this.filterKey();
      this.cacheHandle.handle();
      if (this.sessionEncode.isEmpty()) {
         ChatLogger.handle("§c[AutoBuy] Список предметов для парсинга пуст!");
         this.summary.process(false);
         this.resourceClamp = false;
         return false;
      } else {
         this.resourceClamp = var1;
         this.summary.process(true);
         this.regionAlign = true;
         this.screenSubmit.handle();
         ChatLogger.handle((var1 ? "§e[AutoBuy] Авто-репарс: " : "§a[AutoBuy] ") + "Начинаем парсинг " + this.sessionEncode.size() + " предметов...");
         return true;
      }
   }

   private void adaptRequest() {
      this.summary.process(false);
      this.regionAlign = false;
      this.scaleParse = false;
      this.handlerRun = "";
      this.sourceCancel = 0L;
      this.eventSend = 0L;
      this.filterKey();
      this.renderScale();
      this.performListener();
      if (this.resourceClamp) {
         this.resourceClamp = false;
         this.indexSave.handle();
         ChatLogger.handle("§a[AutoBuy] Авто-репарс завершён. Меняем анархию и возвращаем покупки.");
         this.apply(true);
      } else {
         ChatLogger.handle("§a[AutoBuy] Авто-парс успешно завершён! Цены обновлены.");
      }
   }

   private void measureTimer() {
      this.scaleParse = false;
      this.resourceClamp = false;
      this.handlerRun = "";
      this.sourceCancel = 0L;
      this.eventSend = 0L;
      this.filterKey();
      this.screenSubmit.handle();
      this.cacheHandle.handle();
      this.indexSave.handle();
      this.renderScale();
   }

   private boolean encodeVector() {
      if (!this.vectorMatch.compute() || !this.latest.process("FunTime")) {
         return false;
      }

      if (!this.summary.compute() && !this.resourceClamp && !this.indexLoad && !this.blockRun) {
         if (Module.client.currentScreen instanceof GenericContainerScreen var1 && this.compute(var1)) {
            return false;
         } else if (colorMeasure.isEmpty()) {
            this.indexSave.handle();
            return false;
         } else if (!this.indexSave.update(this.receiveRequest())) {
            return false;
         } else if (!this.update(true)) {
            this.indexSave.handle();
            return false;
         } else {
            int var3 = this.apply(false);
            if (var3 == -1) {
               this.scanEffect();
               this.indexSave.handle();
               return false;
            } else {
               this.handlerRun = String.valueOf(var3);
               this.sourceCancel = System.currentTimeMillis();
               this.eventSend = System.currentTimeMillis() + 2500L;
               this.shaderProject = false;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private long receiveRequest() {
      return Math.max(1L, (long)this.itemProject.compute()) * 60000L;
   }

   private boolean processWindow() {
      long var1 = System.currentTimeMillis();
      if (!this.handlerRun.isEmpty()) {
         long var3 = var1 - this.sourceCancel;
         String var5 = this.advanceOption();
         boolean var6 = this.handlerRun.equals(var5) && var3 >= 2500L;
         if (!var6 && !this.shaderProject && var3 >= 4500L && Module.client.player != null) {
            Module.client.player.networkHandler.sendChatCommand("an" + this.handlerRun);
            this.shaderProject = true;
            this.sourceCancel = var1;
            return false;
         }

         var3 = var1 - this.sourceCancel;
         boolean var7 = var3 >= 12000L;
         if (!var6 && !var7) {
            return false;
         }

         this.handlerRun = "";
         this.shaderProject = false;
      }

      return var1 >= this.eventSend;
   }

   private long handle(long var1, int var3) {
      double var4 = var3 / 100.0;
      long var6 = (long)(var1 * var4);
      return Math.max(1L, var1 - var6);
   }

   private String tick(String var1) {
      if (HolyWorldHelper.process(var1)) {
         return HolyWorldHelper.resolve(var1);
      }

      if (var1 != null && var1.startsWith("minecraft:")) {
         Identifier var2 = Identifier.tryParse(var1);
         if (var2 != null) {
            Item var3 = (Item)Registries.ITEM.get(var2);
            if (var3 != Items.AIR) {
               return var3.getDefaultStack().getName().getString();
            }
         }
      }

      return var1;
   }

   private String drawAnimation(String var1) {
      if (var1 == null) {
         return "";
      }

      return switch (var1) {
         case "Опыт 15" -> "Опыт с уровнем 15";
         case "Опыт 30" -> "Опыт с уровнем 30";
         case "Опыт 45" -> "Опыт с уровнем 45";
         case "Опыт 50" -> "Опыт с уровнем 50";
         default -> this.tick(var1);
      };
   }

   private boolean compute(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      } else {
         String var2 = this.encodePoint(var1.getTitle().getString());
         if (var2.contains("подтверждение покупки")) {
            return this.resolve(var1.getScreenHandler()) != -1;
         } else {
            return !this.sampleLayer() ? false : this.compute(var1.getScreenHandler());
         }
      }
   }

   private boolean compute(ScreenHandler var1) {
      return var1.slots.size() < 27 ? false : this.resolve(var1) != -1;
   }

   private int resolve(ScreenHandler var1) {
      int var2 = this.update(var1);

      for (int var3 = var2 - 1; var3 >= 0; var3--) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         String var5 = this.encodePoint(var4.getName().getString());
         if (var5.contains("купить")) {
            return var3;
         }

         if (var4.getItem() == Items.LIME_STAINED_GLASS_PANE
            || var4.getItem() == Items.GREEN_STAINED_GLASS_PANE
            || var4.getItem() == Items.GREEN_CONCRETE
            || var4.getItem() == Items.LIME_CONCRETE) {
            return var3;
         }
      }

      return -1;
   }

   private int update(ScreenHandler var1) {
      return Math.max(0, Math.min(54, var1.slots.size() - 36));
   }

   private String encodePoint(String var1) {
      return var1 == null ? "" : var1.replaceAll("§.", "").toLowerCase(Locale.ROOT).trim();
   }

   private boolean resolve(GenericContainerScreen var1) {
      return this.latest.process("HolyWorld") ? this.update(var1) : AhHelper.handle(var1);
   }

   public boolean handle(GenericContainerScreen var1) {
      return this.resolve(var1);
   }

   private boolean update(GenericContainerScreen var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = this.encodePoint(var1.getTitle().getString());
      return !var2.contains("аукцион") && !var2.contains("auction") ? this.process(var1.getScreenHandler()) != -1 : true;
   }

   private boolean handle(GenericContainerScreen var1, String var2) {
      if (var1 != null && var2 != null) {
         if (((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size() < 54) {
            return false;
         }

         String var3 = this.encodePoint(var1.getTitle().getString());
         String var4 = this.encodePoint(this.tick(var2));
         String var5 = this.encodePoint(this.drawAnimation(var2));
         if (!var3.contains(var4) && !var3.contains(var5)) {
            return false;
         }

         boolean var6 = false;
         int var7 = Math.min(54, ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size());

         for (int var8 = 45; var8 < var7; var8++) {
            ItemStack var9 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var8).getStack();
            if (var9.getItem() == Items.ARROW
               || var9.getItem() == Items.PAPER
               || var9.getItem() == Items.SPECTRAL_ARROW
               || var9.getItem() == Items.LIME_STAINED_GLASS_PANE) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            return false;
         }

         for (int var10 = 0; var10 < Math.min(45, ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size()); var10++) {
            Slot var11 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var10);
            if (this.process(var11) && this.process(var11, this.latest.compute()) > 0L) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private AutoBuy.FallbackDataRecord process(GenericContainerScreen var1, String var2) {
      long var3 = Long.MAX_VALUE;
      long var5 = Long.MAX_VALUE;
      int var7 = 1;
      boolean var8 = false;
      String var9 = this.latest.compute();

      for (int var10 = 0; var10 < Math.min(45, ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size()); var10++) {
         Slot var11 = ((GenericContainerScreenHandler)var1.getScreenHandler()).getSlot(var10);
         if (this.process(var11) && this.handle(var2, var11.getStack(), var9)) {
            long var12 = this.process(var11, var9);
            if (var12 > 0L) {
               int var14 = this.handle(var11);
               long var15 = this.process(var12, var14);
               if (var15 < var3 || var15 == var3 && var12 < var5) {
                  var3 = var15;
                  var5 = var12;
                  var7 = var14;
                  var8 = true;
               }
            }
         }
      }

      return var8 ? new AutoBuy.FallbackDataRecord(var3, var5, var7) : null;
   }

   private int handle(Slot var1) {
      return var1 != null && var1.hasStack() ? Math.max(1, var1.getStack().getCount()) : 1;
   }

   private long process(long var1, int var3) {
      int var4 = Math.max(1, var3);
      return Math.max(1L, (var1 + var4 - 1L) / var4);
   }

   private boolean process(Slot var1) {
      if (var1 != null && var1.hasStack()) {
         ItemStack var2 = var1.getStack();
         return !this.compute(var2);
      } else {
         return false;
      }
   }

   private boolean compute(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && var1.getItem() != Items.AIR) {
         Item var2 = var1.getItem();
         return var2 == Items.GREEN_STAINED_GLASS_PANE
            || var2 == Items.BLACK_STAINED_GLASS_PANE
            || var2 == Items.LIME_STAINED_GLASS_PANE
            || var2 == Items.RED_STAINED_GLASS_PANE
            || var2 == Items.GRAY_STAINED_GLASS_PANE
            || var2 == Items.WHITE_STAINED_GLASS_PANE
            || var2 == Items.ORANGE_STAINED_GLASS_PANE
            || var2 == Items.YELLOW_STAINED_GLASS_PANE
            || var2 == Items.ARROW
            || var2 == Items.SPECTRAL_ARROW
            || var2 == Items.PAPER
            || var2 == Items.BARRIER
            || var2 == Items.CHEST
            || var2 == Items.ENDER_CHEST
            || var2 == Items.HOPPER
            || var2 == Items.COMPASS;
      } else {
         return true;
      }
   }

   private boolean compute(Slot var1) {
      if (var1 != null && var1.hasStack() && !this.compute(var1.getStack())) {
         long var2 = AuctionLoreParser.process(var1);
         if (var2 <= 0L) {
            return false;
         } else {
            String var4 = AuctionLoreParser.handle(var1);
            if (var4 == null || var4.isBlank()) {
               return false;
            } else {
               return Module.client.player != null && var4.equalsIgnoreCase(Module.client.player.getName().getString()) ? false : !resolve(var4);
            }
         }
      } else {
         return false;
      }
   }

   private boolean apply(ScreenHandler var1) {
      int var2 = this.update(var1);
      int var3 = this.resolve(var1);

      for (int var4 = 0; var4 < var2; var4++) {
         if (var4 != var3) {
            Slot var5 = var1.getSlot(var4);
            if (this.process(var5) && this.compute(var5)) {
               AutoBuy.PrimaryDataRecord var6 = this.handle(var5, AuctionLoreParser.process(var5));
               if (var6 != null
                  && var6.buyable()
                  && (this.effectMatch == 0 || var6.fingerprint() == this.effectMatch)
                  && (this.providerMatch <= 0L || var6.lotPrice() <= this.providerMatch)
                  && (this.profileWrite <= 0L || var6.estimatedValue() >= this.profileWrite)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private AutoBuy.PrimaryDataRecord handle(Slot var1, long var2) {
      if (!this.providerClose.compute()) {
         return null;
      }

      if (var1 != null && var1.hasStack() && var2 > 0L) {
         ItemStack var4 = var1.getStack();
         if (!this.compute(var4.getItem())) {
            return null;
         }

         ContainerComponent var5 = (ContainerComponent)var4.get(DataComponentTypes.CONTAINER);
         if (var5 == null) {
            return null;
         }

         long var6 = this.handle(var5, 0);
         if (var6 <= 0L) {
            return null;
         }

         long var8 = var6 - var2;
         long var10 = Math.max(0L, (long)this.windowConvert.compute());
         long var12 = Math.max(0L, (long)this.presetWrite.compute());
         long var14 = (long)Math.ceil(var2 * (Math.max(0.0F, this.presetSave.compute()) / 100.0));
         boolean var16 = var6 >= var12 && var8 >= var10 && var8 >= var14;
         return new AutoBuy.PrimaryDataRecord(var2, var6, var8, this.resolve(var4), var16);
      } else {
         return null;
      }
   }

   private long handle(ContainerComponent var1, int var2) {
      if (var1 != null && var2 <= 2) {
         long var3 = 0L;
         boolean var5 = false;

         for (ItemStack var7 : var1.iterateNonEmptyCopy()) {
            if (var7 != null && !var7.isEmpty()) {
               var5 = true;
               long var8 = this.handle(var7, var2);
               if (var8 > 0L) {
                  var3 = this.handle(var3, var8);
               }
            }
         }

         return var5 ? var3 : 0L;
      } else {
         return 0L;
      }
   }

   private long handle(ItemStack var1, int var2) {
      long var3 = 0L;

      for (Entry<String, Long> var6 : colorMeasure.entrySet()) {
         String var7 = var6.getKey();
         Long var8 = var6.getValue();
         if (var7 != null
            && var8 != null
            && var8 > 0L
            && !profileInvoke.contains(var7)
            && this.compute(var7, "FunTime")
            && this.handle(var7, var1, "FunTime")
            && this.handle(var7, var1)) {
            var3 = Math.max(var3, this.compute(var8, Math.max(1, var1.getCount())));
         }
      }

      if (this.compute(var1.getItem()) && var2 < 2) {
         ContainerComponent var9 = (ContainerComponent)var1.get(DataComponentTypes.CONTAINER);
         if (var9 != null) {
            var3 = Math.max(var3, this.handle(var9, var2 + 1));
         }
      }

      return var3;
   }

   private boolean compute(Item var1) {
      return var1 == Items.SHULKER_BOX
         || var1 == Items.WHITE_SHULKER_BOX
         || var1 == Items.ORANGE_SHULKER_BOX
         || var1 == Items.MAGENTA_SHULKER_BOX
         || var1 == Items.LIGHT_BLUE_SHULKER_BOX
         || var1 == Items.YELLOW_SHULKER_BOX
         || var1 == Items.LIME_SHULKER_BOX
         || var1 == Items.PINK_SHULKER_BOX
         || var1 == Items.GRAY_SHULKER_BOX
         || var1 == Items.LIGHT_GRAY_SHULKER_BOX
         || var1 == Items.CYAN_SHULKER_BOX
         || var1 == Items.PURPLE_SHULKER_BOX
         || var1 == Items.BLUE_SHULKER_BOX
         || var1 == Items.BROWN_SHULKER_BOX
         || var1 == Items.GREEN_SHULKER_BOX
         || var1 == Items.RED_SHULKER_BOX
         || var1 == Items.BLACK_SHULKER_BOX;
   }

   private int resolve(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         Identifier var2 = Registries.ITEM.getId(var1.getItem());
         return Objects.hash(var2, var1.getCount(), var1.getName().getString(), var1.getComponents().hashCode());
      } else {
         return 0;
      }
   }

   private long handle(long var1, long var3) {
      try {
         return Math.addExact(var1, var3);
      } catch (ArithmeticException var6) {
         return Long.MAX_VALUE;
      }
   }

   private long compute(long var1, int var3) {
      try {
         return Math.multiplyExact(var1, Math.max(1, var3));
      } catch (ArithmeticException var5) {
         return Long.MAX_VALUE;
      }
   }

   private void animate(String var1) {
      this.sessionCollect = var1 == null ? "" : var1;
      this.inputAttach = System.currentTimeMillis();
      if (!"__wild_funtime_shulker__".equals(this.sessionCollect)) {
         this.providerMatch = 0L;
         this.profileWrite = 0L;
         this.effectMatch = 0;
      }
   }

   private void savePacket() {
      this.sessionCollect = "";
      this.inputAttach = 0L;
      this.providerMatch = 0L;
      this.profileWrite = 0L;
      this.effectMatch = 0;
   }

   private void animateEntry() {
      this.pathProject = 0L;
      this.worldDispatch = 0;
      this.optionAdvance2 = false;
      this.clientDraw = 0L;
   }

   private boolean collectPlayer() {
      return this.optionAdvance2 && this.latest.process("FunTime");
   }

   private void applyState() {
      this.optionAdvance2 = true;
      this.clientDraw = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(15000L, 20001L);
      this.indexLoad = false;
      this.outputFetch = "";
      this.savePacket();
      if (Module.client.player != null && Module.client.currentScreen != null) {
         Module.client.player.closeScreen();
      }
   }

   private void filterMatrix() {
      if (System.currentTimeMillis() >= this.clientDraw) {
         this.optionAdvance2 = false;
         this.clientDraw = 0L;
         this.parseMessage();
      }
   }

   private boolean sampleLayer() {
      if (this.sessionCollect.isEmpty()) {
         return false;
      } else if (System.currentTimeMillis() - this.inputAttach > 15000L) {
         this.savePacket();
         return false;
      } else {
         return true;
      }
   }

   private boolean apply(GenericContainerScreen var1) {
      if (var1 != null && this.sampleLayer()) {
         String var2 = this.sessionCollect;
         String var3 = this.latest.compute();
         if (!"__wild_funtime_shulker__".equals(var2)) {
            Long var4 = colorMeasure.get(var2);
            if (var4 == null || var4 <= 0L || profileInvoke.contains(var2)) {
               return false;
            } else {
               return !this.compute(var2, var3) ? false : this.handle(var1.getScreenHandler(), var2, var4, var3);
            }
         } else {
            return var3.equals("FunTime") && this.apply(var1.getScreenHandler());
         }
      } else {
         return false;
      }
   }

   private boolean handle(ScreenHandler var1, String var2, long var3, String var5) {
      int var6 = this.update(var1);
      int var7 = this.resolve(var1);

      for (int var8 = 0; var8 < var6; var8++) {
         if (var8 != var7) {
            Slot var9 = var1.getSlot(var8);
            if (this.process(var9) && (!var5.equals("FunTime") || this.compute(var9))) {
               ItemStack var10 = var9.getStack();
               if (this.handle(var2, var10, var5) && this.handle(var2, var10)) {
                  long var11 = this.process(var9, var5);
                  if (var11 <= 0L || this.process(var11, this.handle(var9)) <= var3) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void execute(ScreenHandler var1) {
      if (Module.client.player != null) {
         Module.client.player.closeScreen();
      }

      this.savePacket();
   }

   public static int prepare(String var0) {
      return check(var0);
   }

   public static int check(String var0) {
      return var0 == null ? 0 : Math.max(0, Math.min(100, animationSchedule.getOrDefault(var0, 0)));
   }

   public static int onTick(String var0) {
      return var0 == null ? 100 : Math.max(0, Math.min(100, rendererScan.getOrDefault(var0, 100)));
   }

   public static void handle(String var0, int var1) {
      handle(var0, var1, onTick(var0));
   }

   public static void handle(String var0, int var1, int var2) {
      if (var0 != null && !var0.isBlank()) {
         int var3 = Math.max(0, Math.min(100, var1));
         int var4 = Math.max(0, Math.min(100, var2));
         if (var3 > var4) {
            int var5 = var3;
            var3 = var4;
            var4 = var5;
         }

         if (var3 <= 0) {
            animationSchedule.remove(var0);
         } else {
            animationSchedule.put(var0, var3);
         }

         if (var4 >= 100) {
            rendererScan.remove(var0);
         } else {
            rendererScan.put(var0, var4);
         }
      }
   }

   public static boolean process(String var0, String var1) {
      Set<String> var2 = sourceBuild.get(var0);
      return var2 == null || !var2.contains(HolyWorldHelper.update(var1));
   }

   public static void handle(String var0, String var1, boolean var2) {
      if (var0 != null && !var0.isBlank()) {
         String var3 = HolyWorldHelper.update(var1);
         if (!var3.isBlank()) {
            if (var2) {
               Set<String> var4 = sourceBuild.get(var0);
               if (var4 != null) {
                  var4.remove(var3);
                  if (var4.isEmpty()) {
                     sourceBuild.remove(var0);
                  }
               }
            } else {
               sourceBuild.computeIfAbsent(var0, var0x -> new LinkedHashSet<>()).add(var3);
            }
         }
      }
   }

   public static Set<String> handle(String var0, List<String> var1) {
      LinkedHashSet<String> var2 = new LinkedHashSet<>();
      if (var1 == null) {
         return var2;
      }

      for (String var4 : var1) {
         String var5 = HolyWorldHelper.update(var4);
         if (!var5.isBlank() && process(var0, var5)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public static int handle(ItemStack var0) {
      if (var0 != null && !var0.isEmpty() && var0.isDamageable()) {
         int var1 = var0.getMaxDamage();
         if (var1 <= 0) {
            return 100;
         }

         int var2 = Math.max(0, var1 - var0.getDamage());
         return Math.max(0, Math.min(100, (int)(var2 * 100L / var1)));
      } else {
         return 100;
      }
   }

   private boolean handle(String var1, ItemStack var2) {
      if (var2 != null && handle(var2.getItem())) {
         int var3 = check(var1);
         int var4 = onTick(var1);
         int var5 = handle(var2);
         return var5 >= var3 && var5 <= var4;
      } else {
         return true;
      }
   }

   public static boolean handle(Item var0) {
      return var0 == null ? false : process(var0) || new ItemStack(var0).isDamageable();
   }

   public static boolean process(Item var0) {
      return var0 == Items.NETHERITE_HELMET
         || var0 == Items.DIAMOND_HELMET
         || var0 == Items.IRON_HELMET
         || var0 == Items.CHAINMAIL_HELMET
         || var0 == Items.GOLDEN_HELMET
         || var0 == Items.LEATHER_HELMET
         || var0 == Items.TURTLE_HELMET
         || var0 == Items.NETHERITE_CHESTPLATE
         || var0 == Items.DIAMOND_CHESTPLATE
         || var0 == Items.IRON_CHESTPLATE
         || var0 == Items.CHAINMAIL_CHESTPLATE
         || var0 == Items.GOLDEN_CHESTPLATE
         || var0 == Items.LEATHER_CHESTPLATE
         || var0 == Items.NETHERITE_LEGGINGS
         || var0 == Items.DIAMOND_LEGGINGS
         || var0 == Items.IRON_LEGGINGS
         || var0 == Items.CHAINMAIL_LEGGINGS
         || var0 == Items.GOLDEN_LEGGINGS
         || var0 == Items.LEATHER_LEGGINGS
         || var0 == Items.NETHERITE_BOOTS
         || var0 == Items.DIAMOND_BOOTS
         || var0 == Items.IRON_BOOTS
         || var0 == Items.CHAINMAIL_BOOTS
         || var0 == Items.GOLDEN_BOOTS
         || var0 == Items.LEATHER_BOOTS;
   }

   private String resolve(Slot var1) {
      String var2 = this.latest.compute();
      if (var1 != null && var1.hasStack()) {
         if (var2.equals("HolyWorld")) {
            return this.update(var1);
         }

         ItemStack var3 = var1.getStack();
         long var4 = this.process(var1, var2);
         String var6 = this.handle(var1, var2);
         if (var4 <= 0L) {
            return null;
         }

         if (var2.equals("FunTime") && !this.compute(var1)) {
            return null;
         }

         if (Module.client.player != null && var6 != null && var6.equalsIgnoreCase(Module.client.player.getName().getString())) {
            return null;
         }

         if (resolve(var6)) {
            return null;
         }

         if (var2.equals("FunTime")) {
            AutoBuy.PrimaryDataRecord var7 = this.handle(var1, var4);
            if (var7 != null && var7.buyable()) {
               this.providerMatch = var7.lotPrice();
               this.profileWrite = var7.estimatedValue();
               this.effectMatch = var7.fingerprint();
               return "__wild_funtime_shulker__";
            }
         }

         long var13 = this.process(var4, this.handle(var1));

         for (Entry<String, Long> var10 : colorMeasure.entrySet()) {
            String var11 = var10.getKey();
            Long var12 = var10.getValue();
            if (!profileInvoke.contains(var11)
               && var12 != null
               && this.compute(var11, var2)
               && this.handle(var11, var3, var2)
               && var13 <= var12
               && this.handle(var11, var3)) {
               return var11;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean compute(String var1, String var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (var1.startsWith("minecraft:")) {
         return true;
      } else if (HolyWorldHelper.handle(var1)) {
         return var2.equals("HolyWorld");
      } else if (var2.equals("FunTime")) {
         return actionRender.contains(var1);
      } else if (var2.equals("SpookyTime")) {
         return actionRender.contains(var1) || playerApply.contains(var1);
      } else {
         return var2.equals("HolyWorld") ? HolyWorldHelper.process(var1) : false;
      }
   }

   private String update(Slot var1) {
      ItemStack var2 = var1.getStack();
      if (this.process(var2)) {
         return null;
      }

      if (!var2.isOf(Items.BARRIER) && !var2.isOf(Items.CHEST) && !var2.isOf(Items.ENDER_CHEST)) {
         AutoBuy.SecondaryDataRecord var3 = this.execute(var1);
         if (var3.price() > 0L && var3.seller() != null) {
            if (Module.client.player != null && var3.seller().equalsIgnoreCase(Module.client.player.getName().getString())) {
               return null;
            }

            if (resolve(var3.seller())) {
               return null;
            }

            long var4 = this.process(var3.price(), this.handle(var1));
            this.sendWorld();
            AutoBuy.PrimaryCacheEntry var6 = new AutoBuy.PrimaryCacheEntry(var2);
            String var7 = this.handle(var2, var4, this.cacheExecute.get(var2.getItem()), var6);
            return var7 != null ? var7 : this.handle(var2, var4, this.pointSynchronize, var6);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void sendWorld() {
      int var1 = this.writeTarget();
      if (var1 != this.sessionBind) {
         this.sessionBind = var1;
         this.cacheExecute.clear();
         this.pointSynchronize.clear();

         for (Entry<String, Long> var3 : colorMeasure.entrySet()) {
            String var4 = var3.getKey();
            Long var5 = var3.getValue();
            if (var4 != null && !profileInvoke.contains(var4) && var5 != null) {
               HolyWorldHelper.SecondaryDataRecord var6 = HolyWorldHelper.compute(var4);
               Item var7 = this.handle(var4, var6);
               AutoBuy.RuntimeDataRecord var8 = new AutoBuy.RuntimeDataRecord(var4, var5, var6, var7, this.save(var4));
               if (var7 != null && var7 != Items.AIR) {
                  this.cacheExecute.computeIfAbsent(var7, var0 -> new ArrayList<>()).add(var8);
               } else {
                  this.pointSynchronize.add(var8);
               }
            }
         }
      }
   }

   private int writeTarget() {
      int var1 = 1;

      for (Entry<String, Long> var3 : colorMeasure.entrySet()) {
         var1 = 31 * var1 + Objects.hashCode(var3.getKey());
         var1 = 31 * var1 + Objects.hashCode(var3.getValue());
      }

      for (String var6 : profileInvoke) {
         var1 += Objects.hashCode(var6);
      }

      return var1;
   }

   private void encodeResult() {
      this.sessionBind = Integer.MIN_VALUE;
   }

   private Item handle(String var1, HolyWorldHelper.SecondaryDataRecord var2) {
      if (var2 != null) {
         return var2.item();
      }

      if (var1 != null && var1.startsWith("minecraft:")) {
         Identifier var3 = Identifier.tryParse(var1);
         if (var3 == null) {
            return null;
         }

         Item var4 = (Item)Registries.ITEM.get(var3);
         return var4 == Items.AIR ? null : var4;
      } else {
         return null;
      }
   }

   private String handle(ItemStack var1, long var2, List<AutoBuy.RuntimeDataRecord> var4, AutoBuy.PrimaryCacheEntry var5) {
      if (var4 != null && !var4.isEmpty()) {
         for (AutoBuy.RuntimeDataRecord var7 : var4) {
            if (var2 <= var7.maxPrice()) {
               HolyWorldHelper.SecondaryDataRecord var8 = var7.holyWorldEntry();
               if (var8 != null) {
                  if (var1.isOf(var8.item()) && this.handle(var7.itemName(), var8, var1, var5.handle(), var5.process()) && this.handle(var7.itemName(), var1)) {
                     return var7.itemName();
                  }
               } else if (this.handle(var7, var1, var5) && this.handle(var7.itemName(), var1)) {
                  return var7.itemName();
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean handle(AutoBuy.RuntimeDataRecord var1, ItemStack var2, AutoBuy.PrimaryCacheEntry var3) {
      Item var4 = var1.item();
      if (var4 != null && var4 != Items.AIR && !var2.isOf(var4)) {
         return false;
      } else {
         Identifier var5 = var3.update();
         String var6 = var1.itemName();
         if (var5 == null || !var6.equalsIgnoreCase(var5.toString()) && !var6.equalsIgnoreCase(var5.getPath())) {
            String var7 = var3.compute();
            return var7.equalsIgnoreCase(var6) || var3.resolve().equals(var1.normalizedName());
         } else {
            return true;
         }
      }
   }

   private boolean handle(String var1, HolyWorldHelper.SecondaryDataRecord var2, ItemStack var3, String var4, String var5) {
      Set<String> var6 = process(var2.item()) ? handle(var1, var2.enchantments()) : null;
      return HolyWorldHelper.handle(var2, var3, var4, var5, var6);
   }

   private boolean apply(Slot var1) {
      if (var1 != null && var1.hasStack()) {
         ItemStack var2 = var1.getStack();
         if (this.process(var2)) {
            return false;
         } else if (!var2.isOf(Items.BARRIER) && !var2.isOf(Items.CHEST) && !var2.isOf(Items.ENDER_CHEST)) {
            AutoBuy.SecondaryDataRecord var3 = this.execute(var1);
            return var3.price() > 0L && var3.seller() != null;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean handle(String var1, ItemStack var2, String var3) {
      if (var1 == null || var2 == null || var2.isEmpty()) {
         return false;
      }

      if (var3.equals("HolyWorld") && HolyWorldHelper.process(var1)) {
         HolyWorldHelper.SecondaryDataRecord var7 = HolyWorldHelper.compute(var1);
         if (var7 != null && var2.isOf(var7.item())) {
            String var8 = HolyWorldHelper.update(var2);
            String var6 = HolyWorldHelper.apply(var2);
            return this.handle(var1, var7, var2, var8, var6);
         } else {
            return false;
         }
      } else {
         if (!var3.equals("FunTime") && !var3.equals("SpookyTime") && !var3.equals("HolyWorld")) {
            return this.compute(var1, var2);
         }

         return switch (var1) {
            case "Сфера Хаоса" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.ATTACK_DAMAGE, 2.5),
               this.handle(EntityAttributes.MOVEMENT_SPEED, 0.07),
               this.handle(EntityAttributes.ATTACK_SPEED, 0.13),
               this.handle(EntityAttributes.ARMOR, 1.5),
               this.handle(EntityAttributes.MAX_HEALTH, -4.0),
               this.handle(EntityAttributes.GRAVITY, 0.09)
            );
            case "Сфера Титана" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.ARMOR, 2.5),
               this.handle(EntityAttributes.ARMOR_TOUGHNESS, 2.5),
               this.handle(EntityAttributes.MOVEMENT_SPEED, -0.15)
            );
            case "Сфера Ареса" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.ATTACK_DAMAGE, 6.0),
               this.handle(EntityAttributes.ARMOR, -2.0),
               this.handle(EntityAttributes.MAX_HEALTH, -2.0)
            );
            case "Сфера Бестии" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.ARMOR, 1.0),
               this.handle(EntityAttributes.MAX_HEALTH, 4.0),
               this.handle(EntityAttributes.MOVEMENT_SPEED, 0.1),
               this.handle(EntityAttributes.ATTACK_SPEED, 0.1)
            );
            case "Сфера Гидры" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.ARMOR, 2.0),
               this.handle(EntityAttributes.MAX_HEALTH, 4.0),
               this.handle(EntityAttributes.SUBMERGED_MINING_SPEED, 0.5),
               this.handle(EntityAttributes.OXYGEN_BONUS, 0.5)
            );
            case "Сфера Икара" -> this.handle(
               var2, Items.PLAYER_HEAD, this.handle(EntityAttributes.ATTACK_DAMAGE, 2.0), this.handle(EntityAttributes.MAX_HEALTH, 2.0)
            );
            case "Сфера Эрида" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.LUCK, 1.0),
               this.handle(EntityAttributes.MAX_HEALTH, 2.0),
               this.handle(EntityAttributes.BLOCK_INTERACTION_RANGE, 1.0)
            );
            case "Сфера Сатира" -> this.handle(
               var2,
               Items.PLAYER_HEAD,
               this.handle(EntityAttributes.ATTACK_DAMAGE, 2.0),
               this.handle(EntityAttributes.JUMP_STRENGTH, -0.1),
               this.handle(EntityAttributes.ATTACK_SPEED, 0.15)
            );
            case "Вещи Крушителя", "Набор Крушителя", "Броня Крушителя", "Броня Крушителя с шипами", "Броня Крушителя шип", "Броня Крушителя без шипов", "Броня Крушителя без шип", "Шлем Крушителя", "Нагрудник Крушителя", "Поножи Крушителя", "Ботинки Крушителя", "Меч Крушителя", "Кирка Крушителя", "Лук Крушителя", "Арбалет Крушителя", "Трезубец Крушителя", "Булава Крушителя", "Элитры Крушителя", "Удочка Крушителя" -> (
                  var3.equals("FunTime") || var3.equals("SpookyTime")
               )
               && this.process(var1, var2);
            case "Талисман Демона" -> this.handle(
               var2, Items.TOTEM_OF_UNDYING, this.handle(EntityAttributes.ATTACK_DAMAGE, 2.5), this.handle(EntityAttributes.ATTACK_SPEED, 0.1)
            );
            case "Талисман Карателя" -> this.handle(
               var2,
               Items.TOTEM_OF_UNDYING,
               this.handle(EntityAttributes.ATTACK_DAMAGE, 7.0),
               this.handle(EntityAttributes.MAX_HEALTH, -4.0),
               this.handle(EntityAttributes.MOVEMENT_SPEED, 0.1)
            );
            case "Талисман Мрака" -> this.handle(
               var2, Items.TOTEM_OF_UNDYING, this.handle(EntityAttributes.ARMOR, 1.5), this.handle(EntityAttributes.MAX_HEALTH, 1.5)
            );
            case "Талисман Ярости" -> this.handle(
               var2, Items.TOTEM_OF_UNDYING, this.handle(EntityAttributes.ATTACK_DAMAGE, 5.0), this.handle(EntityAttributes.MAX_HEALTH, -4.0)
            );
            case "Талисман Тирана" -> this.handle(
               var2,
               Items.TOTEM_OF_UNDYING,
               this.handle(EntityAttributes.ATTACK_DAMAGE, 2.0),
               this.handle(EntityAttributes.ARMOR, 2.0),
               this.handle(EntityAttributes.MAX_HEALTH, -4.0)
            );
            case "Талисман Крушителя" -> this.handle(
               var2,
               Items.TOTEM_OF_UNDYING,
               this.handle(EntityAttributes.MAX_HEALTH, 4.0),
               this.handle(EntityAttributes.ATTACK_DAMAGE, 3.0),
               this.handle(EntityAttributes.ARMOR_TOUGHNESS, 2.0),
               this.handle(EntityAttributes.ARMOR, 2.0)
            );
            case "Талисман Раздора" -> this.handle(
               var2,
               Items.TOTEM_OF_UNDYING,
               this.handle(EntityAttributes.ATTACK_DAMAGE, 4.0),
               this.handle(EntityAttributes.MAX_HEALTH, 2.0),
               this.handle(EntityAttributes.MOVEMENT_SPEED, 0.1),
               this.handle(EntityAttributes.ATTACK_SPEED, 0.1),
               this.handle(EntityAttributes.ARMOR, -3.0)
            );
            case "Зелье Ассасина" -> SpecialItemCatalog.save(var2);
            case "Зелье Гнева" -> this.handle(var2, Items.SPLASH_POTION, this.handle(EntityAttributes.ATTACK_DAMAGE, 5.0)) && SpecialItemCatalog.submit(var2);
            case "Талисман Сара", "Талисман Сары" -> this.handle(var2, Items.TOTEM_OF_UNDYING, this.handle(EntityAttributes.MAX_HEALTH, 2.0));
            case "Хлопушка" -> SpecialItemCatalog.unload(var2);
            case "Святая Вода" -> SpecialItemCatalog.fetch(var2);
            case "Зелье Палладина" -> SpecialItemCatalog.measure(var2);
            case "Зелье Радиации" -> SpecialItemCatalog.blendMatrix(var2);
            case "Снотворное" -> SpecialItemCatalog.matchVector(var2);
            case "Пласт" -> SpecialItemCatalog.performVector(var2);
            case "Вайт" -> SpecialItemCatalog.collectModule(var2);
            case "Блек" -> SpecialItemCatalog.closeProvider(var2);
            case "Блок дамагер" -> SpecialItemCatalog.scheduleAnimation(var2);
            case "Прогрузчик чанков" -> SpecialItemCatalog.scanRenderer(var2);
            case "Маяк" -> SpecialItemCatalog.buildSource(var2);
            case "Проклятая Душа" -> SpecialItemCatalog.collapseOutput(var2);
            case "Драконий Скин" -> SpecialItemCatalog.invokeProfile(var2);
            case "Огненный Смерч" -> SpecialItemCatalog.scheduleSource(var2);
            case "Снежок Заморозка" -> SpecialItemCatalog.renderTimer(var2);
            case "Божья Аура" -> SpecialItemCatalog.saveScale(var2);
            case "Серебро" -> SpecialItemCatalog.computeColor(var2);
            case "Божье Касание", "Божье касание" -> SpecialItemCatalog.adaptScale(var2);
            case "Мощный Удар" -> SpecialItemCatalog.runTexture(var2);
            case "Мега Бульдозер" -> SpecialItemCatalog.bindIndex(var2);
            case "Нерушимые Элитры" -> SpecialItemCatalog.readAction(var2);
            case "Опыт 15" -> SpecialItemCatalog.attachEvent(var2);
            case "Опыт 30" -> SpecialItemCatalog.readServer(var2);
            case "Опыт 45" -> SpecialItemCatalog.checkFrame(var2);
            case "Опыт 50" -> SpecialItemCatalog.advancePosition(var2);
            default -> this.compute(var1, var2);
         };
      }
   }

   private boolean process(String var1, ItemStack var2) {
      if (var1 != null && var2 != null && !var2.isEmpty()) {
         return switch (var1) {
            case "Вещи Крушителя", "Набор Крушителя" -> this.update(var2);
            case "Броня Крушителя", "Броня Крушителя с шипами", "Броня Крушителя шип", "Броня Крушителя без шипов", "Броня Крушителя без шип" -> this.apply(
               var2
            );
            case "Шлем Крушителя" -> this.handle(var1, EnchantedItemDetector.handle(), var2);
            case "Нагрудник Крушителя" -> this.handle(var1, EnchantedItemDetector.process(), var2);
            case "Поножи Крушителя" -> this.handle(var1, EnchantedItemDetector.compute(), var2);
            case "Ботинки Крушителя" -> this.handle(var1, EnchantedItemDetector.resolve(), var2);
            case "Меч Крушителя" -> this.handle(var1, EnchantedItemDetector.update(), var2);
            case "Кирка Крушителя" -> this.handle(var1, EnchantedItemDetector.apply(), var2);
            case "Арбалет Крушителя" -> this.handle(var1, EnchantedItemDetector.execute(), var2);
            case "Трезубец Крушителя" -> this.handle(var1, EnchantedItemDetector.prepare(), var2);
            case "Булава Крушителя" -> this.handle(var1, EnchantedItemDetector.check(), var2);
            case "Лук Крушителя" -> this.handle(var2, Items.BOW);
            case "Элитры Крушителя" -> this.handle(var2, Items.ELYTRA);
            case "Удочка Крушителя" -> this.handle(var2, Items.FISHING_ROD);
            default -> false;
         };
      } else {
         return false;
      }
   }

   private boolean update(ItemStack var1) {
      return this.apply(var1)
         || this.handle("Вещи Крушителя", EnchantedItemDetector.update(), var1)
         || this.handle("Вещи Крушителя", EnchantedItemDetector.apply(), var1)
         || this.handle("Вещи Крушителя", EnchantedItemDetector.execute(), var1)
         || this.handle("Вещи Крушителя", EnchantedItemDetector.prepare(), var1)
         || this.handle("Вещи Крушителя", EnchantedItemDetector.check(), var1);
   }

   private boolean apply(ItemStack var1) {
      return this.handle("Броня Крушителя", EnchantedItemDetector.handle(), var1)
         || this.handle("Броня Крушителя", EnchantedItemDetector.process(), var1)
         || this.handle("Броня Крушителя", EnchantedItemDetector.compute(), var1)
         || this.handle("Броня Крушителя", EnchantedItemDetector.resolve(), var1);
   }

   private boolean handle(String var1, ItemStack var2, ItemStack var3) {
      if (var2 == null || var2.isEmpty() || var3 == null || var3.isEmpty()) {
         return false;
      }

      if (!var3.isOf(var2.getItem())) {
         return false;
      }

      String var4 = this.load(var2.getName().getString());
      if (!var4.isEmpty() && !this.load(this.check(var3)).contains(var4)) {
         return false;
      }

      ItemEnchantmentsComponent var5 = (ItemEnchantmentsComponent)var2.get(DataComponentTypes.ENCHANTMENTS);
      if (var5 != null && !var5.isEmpty() && !this.handle(var1, var3, var5)) {
         return false;
      }

      LoreComponent var6 = (LoreComponent)var2.get(DataComponentTypes.LORE);
      if (var6 != null) {
         String var7 = this.load(this.check(var3));

         for (Text var9 : var6.lines()) {
            String var10 = this.load(var9.getString());
            if (!var10.isEmpty() && !var7.contains(var10)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean handle(String var1, ItemStack var2, ItemEnchantmentsComponent var3) {
      ItemEnchantmentsComponent var4 = (ItemEnchantmentsComponent)var2.get(DataComponentTypes.ENCHANTMENTS);

      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<RegistryEntry<Enchantment>> var6 : var3.getEnchantmentEntries()) {
         String var7 = this.handle(var6.getKey());
         if (var7.isBlank() || process(var1, var7)) {
            if (var4 == null || var4.isEmpty()) {
               return false;
            }

            if (this.handle(var4, var6.getKey()) < var6.getIntValue()) {
               return false;
            }
         }
      }

      return true;
   }

   private String handle(RegistryEntry<Enchantment> var1) {
      return var1.getKey().map(var0 -> HolyWorldHelper.update(var0.getValue().toString())).orElse("");
   }

   private int handle(ItemEnchantmentsComponent var1, RegistryEntry<Enchantment> var2) {
      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<RegistryEntry<Enchantment>> var4 : var1.getEnchantmentEntries()) {
         if (var4.getKey().equals(var2)) {
            return var4.getIntValue();
         }
      }

      return 0;
   }

   private boolean execute(ItemStack var1) {
      return this.handle(var1, Items.BOW) || this.handle(var1, Items.ELYTRA) || this.handle(var1, Items.FISHING_ROD);
   }

   private boolean handle(ItemStack var1, Item var2) {
      return var1 != null && !var1.isEmpty() && var1.isOf(var2) ? this.handle(var1, "крушител") && this.prepare(var1) : false;
   }

   private boolean handle(ItemStack var1, String var2) {
      return this.load(this.check(var1)).contains(this.load(var2));
   }

   private boolean prepare(ItemStack var1) {
      return var1.hasEnchantments()
         || var1.hasGlint()
         || var1.contains(DataComponentTypes.CUSTOM_NAME)
         || var1.contains(DataComponentTypes.LORE)
         || var1.contains(DataComponentTypes.CUSTOM_DATA);
   }

   private String check(ItemStack var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(var1.getName().getString()).append(' ');
      LoreComponent var3 = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (var3 != null) {
         for (Text var5 : var3.lines()) {
            var2.append(var5.getString()).append(' ');
         }
      }

      var2.append(var1.getComponents());
      return var2.toString();
   }

   private String onTick(ItemStack var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(var1.getName().getString()).append(' ');
      LoreComponent var3 = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (var3 != null) {
         for (Text var5 : var3.lines()) {
            var2.append(var5.getString()).append(' ');
         }
      }

      return var2.toString();
   }

   private String load(String var1) {
      return var1 == null ? "" : var1.replaceAll("(?i)§[0-9A-FK-OR]", "").toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "");
   }

   private boolean handle(ItemStack var1, Item var2, AutoBuy.DataRecord... var3) {
      if (var1 != null && !var1.isEmpty() && var1.isOf(var2)) {
         AttributeModifiersComponent var4 = (AttributeModifiersComponent)var1.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
         if (var4 == null) {
            return var3.length == 0;
         }

         HashMap<RegistryEntry<EntityAttribute>, Double> var5 = new HashMap<>();
         int var6 = 0;

         for (net.minecraft.component.type.AttributeModifiersComponent.Entry var8 : var4.modifiers()) {
            EntityAttributeModifier var9 = var8.modifier();
            var6++;
            var5.put(var8.attribute(), var9.value());
         }

         if (var6 == var3.length && var5.size() == var3.length) {
            for (AutoBuy.DataRecord var10 : var3) {
               Double var11 = (Double)var5.get(var10.attribute());
               if (var11 == null || Math.abs(var11 - var10.value()) > 1.0E-4) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private AutoBuy.DataRecord handle(RegistryEntry<EntityAttribute> var1, double var2) {
      return new AutoBuy.DataRecord(var1, var2);
   }

   private boolean compute(String var1, ItemStack var2) {
      if (var1 != null && var2 != null && !var2.isEmpty()) {
         if (var1.startsWith("minecraft:") || !var1.contains(":") && Identifier.tryParse("minecraft:" + var1) != null) {
            Identifier var7 = Identifier.tryParse(var1.contains(":") ? var1 : "minecraft:" + var1);
            if (var7 != null) {
               Item var4 = (Item)Registries.ITEM.get(var7);
               if (var4 != Items.AIR && var2.isOf(var4)) {
                  String var5 = var4.getDefaultStack().getName().getString();
                  String var6 = var2.getName().getString();
                  return var6.equalsIgnoreCase(var5) || this.save(var6).equals(this.save(var5));
               }
            }

            return false;
         } else {
            String var3 = var2.getName().getString();
            return var3.equalsIgnoreCase(var1) || this.save(var3).equals(this.save(var1));
         }
      } else {
         return false;
      }
   }

   String save(String var1) {
      return var1 == null ? "" : var1.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "");
   }

   private AutoBuy.SecondaryDataRecord execute(Slot var1) {
      if (var1 != null && var1.hasStack()) {
         LoreComponent var2 = (LoreComponent)var1.getStack().get(DataComponentTypes.LORE);
         if (var2 == null) {
            return new AutoBuy.SecondaryDataRecord(0L, null);
         }

         long var3 = 0L;
         String var5 = null;

         for (Text var7 : var2.lines()) {
            String var8 = this.unload(var7.getString());
            String var9 = var8.toLowerCase(Locale.ROOT);
            if (var5 == null) {
               int var10 = var9.indexOf("продавец:");
               if (var10 != -1) {
                  var5 = var8.substring(var10 + "продавец:".length()).trim();
               } else {
                  var10 = var9.indexOf("seller:");
                  if (var10 != -1) {
                     var5 = var8.substring(var10 + "seller:".length()).trim();
                  }
               }
            }

            if (var3 <= 0L && (var8.contains("$") || var8.contains("¤") || var9.contains("цена") || var9.contains("стоимость"))) {
               var3 = this.submit(var8);
            }
         }

         return new AutoBuy.SecondaryDataRecord(var3, var5);
      } else {
         return new AutoBuy.SecondaryDataRecord(0L, null);
      }
   }

   private String prepare(Slot var1) {
      if (var1 != null && var1.hasStack()) {
         LoreComponent var2 = (LoreComponent)var1.getStack().get(DataComponentTypes.LORE);
         if (var2 == null) {
            return null;
         }

         for (Text var4 : var2.lines()) {
            String var5 = this.unload(var4.getString());
            String var6 = var5.toLowerCase(Locale.ROOT);
            int var7 = var6.indexOf("продавец:");
            if (var7 != -1) {
               return var5.substring(var7 + "продавец:".length()).trim();
            }

            var7 = var6.indexOf("seller:");
            if (var7 != -1) {
               return var5.substring(var7 + "seller:".length()).trim();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private long check(Slot var1) {
      if (var1 != null && var1.hasStack()) {
         LoreComponent var2 = (LoreComponent)var1.getStack().get(DataComponentTypes.LORE);
         if (var2 == null) {
            return 0L;
         }

         for (Text var4 : var2.lines()) {
            String var5 = this.unload(var4.getString());
            String var6 = var5.toLowerCase(Locale.ROOT);
            if (var5.contains("$") || var5.contains("¤") || var6.contains("цена") || var6.contains("стоимость")) {
               long var7 = this.submit(var5);
               if (var7 > 0L) {
                  return var7;
               }
            }
         }

         return 0L;
      } else {
         return 0L;
      }
   }

   private long submit(String var1) {
      if (var1 == null) {
         return 0L;
      }

      String var2 = var1.replace(' ', ' ').toLowerCase(Locale.ROOT).trim();
      long var3 = 1L;
      if (var2.contains("млн") || var2.endsWith("m") || var2.endsWith("м")) {
         var3 = 1000000L;
      } else if (var2.contains("тыс") || var2.endsWith("k") || var2.endsWith("к")) {
         var3 = 1000L;
      }

      String var5 = var2.replaceAll("[^0-9]", "");
      if (var5.isEmpty()) {
         return 0L;
      }

      try {
         return Math.multiplyExact(Long.parseLong(var5), var3);
      } catch (ArithmeticException | NumberFormatException var7) {
         return 0L;
      }
   }

   private String unload(String var1) {
      return var1 == null ? "" : var1.replaceAll("§.", "").replace(' ', ' ').trim();
   }

   private String handle(Slot var1, String var2) {
      return switch (var2) {
         case "FunTime" -> AuctionLoreParser.handle(var1);
         case "SpookyTime" -> AuctionSellerParser.handle(var1);
         case "HolyWorld" -> this.prepare(var1);
         default -> null;
      };
   }

   private long process(Slot var1, String var2) {
      return switch (var2) {
         case "FunTime" -> AuctionLoreParser.process(var1);
         case "SpookyTime" -> AuctionSellerParser.process(var1);
         case "HolyWorld" -> this.check(var1);
         default -> 0L;
      };
   }

   private void fetch(String var1) {
      if (var1.contains("Вы успешно купили")) {
         this.measure(var1);
      } else {
         this.blendMatrix(var1);
      }
   }

   private void measure(String var1) {
      String var2 = "Вы успешно купили ";
      String var3 = " за ";
      int var4 = var1.indexOf(var2);
      int var5 = var1.indexOf(var3);
      if (var4 != -1 && var5 != -1) {
         String var6 = var1.substring(var4 + var2.length(), var5).replace(' ', ' ').trim();
         String var7 = var1.substring(var5 + var3.length()).replaceAll("[^\\d]", "").trim();
         if (!var7.isEmpty()) {
            this.handle(var6, Long.parseLong(var7));
         }
      }
   }

   private void blendMatrix(String var1) {
      Matcher var2 = configMatch.matcher(this.unload(var1));
      if (var2.find()) {
         String var3 = var2.group(1) != null ? var2.group(1) : var2.group(2);
         if (var3 != null && !var3.isBlank()) {
            String var4 = var2.group(3);
            String var5 = var2.group(5);
            String var6 = var5 == null ? "" : var5.replaceAll("[^\\d]", "");
            if (!var6.isEmpty()) {
               long var7 = Long.parseLong(var6);
               String var9 = this.matchVector(var3);
               if (var4 != null && !var4.isBlank()) {
                  var9 = var9 + " x" + var4.replaceAll("[^\\d]", "");
               }

               this.handle(var9, var7);
            }
         }
      }
   }

   private String matchVector(String var1) {
      String var2 = this.unload(var1).replace(' ', ' ').replaceAll("^[\\s\\-–—:]+", "").replaceAll("[\\s\\-–—:]+$", "").trim();
      var2 = HolyWorldHelper.execute(var2);
      HolyWorldHelper.SecondaryDataRecord var3 = HolyWorldHelper.compute(var2);
      return var3 == null ? var2 : var3.label();
   }

   private void handle(String var1, long var2) {
      int var4 = 1;
      String var5 = var1;
      if (var1.matches("(?i)^[xхXХ]?\\d+[xхXХ]?\\s+.*")) {
         String[] var6 = var1.split("\\s+", 2);
         String var7 = var6[0].replaceAll("[^\\d]", "");
         if (!var7.isEmpty()) {
            var4 = Integer.parseInt(var7);
         }

         var5 = var6[1].trim();
      } else if (var1.matches("(?i).*\\s+[xхXХ]?\\d+[xхXХ]?$")) {
         int var8 = var1.lastIndexOf(32);
         String var9 = var1.substring(var8 + 1).replaceAll("[^\\d]", "");
         if (!var9.isEmpty()) {
            var4 = Integer.parseInt(var9);
         }

         var5 = var1.substring(0, var8).trim();
      }

      timerRender.add(0, new AutoBuy.CacheEntry(var1, var5, var4, var2, System.currentTimeMillis()));
      if (timerRender.size() > 200) {
         timerRender.remove(timerRender.size() - 1);
      }

      if (ClientUtil.source.compute()) {
         TelegramApi.handle("[AutoBuy] Успешно куплено: " + var1 + " за " + var2);
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      boolean var2 = this.summary.compute() || this.scaleParse || this.resourceClamp;
      if (this.enabled || var2) {
         if (this.enabled && !var1.compute()) {
            if (var1.resolve() instanceof InventoryS2CPacket var3) {
               this.indexCancel.process(var3.syncId());
            } else if (var1.resolve() instanceof ScreenHandlerSlotUpdateS2CPacket var4) {
               this.indexCancel.compute(var4.getSyncId());
            } else if (var1.resolve() instanceof OpenScreenS2CPacket var5) {
               this.indexCancel.resolve(var5.getSyncId());
            }
         }

         if (var1.resolve() instanceof GameMessageS2CPacket var11) {
            String var13 = var11.content().getString();
            if (var2 && this.closeProvider(var13)) {
               return;
            }

            if (!this.enabled) {
               return;
            }

            if (var13.contains("Вы успешно купили") || var13.contains("Вы купили")) {
               long var14 = this.pathProject;
               boolean var7 = this.sampleLayer()
                  || (this.latest.process("FunTime") || this.latest.process("SpookyTime")) && var14 != 0L && System.currentTimeMillis() - var14 <= 15000L;
               this.pathProject = 0L;
               this.indexCancel.compute();
               this.savePacket();
               if (!var7) {
                  return;
               }

               boolean var8 = AutoSell.source != null && AutoSell.source.enabled && AutoSell.source.refresh();
               if (Module.client.player != null && Module.client.currentScreen != null) {
                  Module.client.player.closeScreen();
               }

               try {
                  this.fetch(var13);
               } catch (Exception var10) {
               }

               if (this.latest.process("HolyWorld")) {
                  this.compute(var8);
               } else {
                  AuctionTradeExecutor.compute(var8);
               }
            } else if (var13.contains("Не удалось выставить") && var13.contains("освободите хранилище")) {
               AuctionTradeExecutor.select();
               if (!this.expandAnimation()) {
                  AutoSell.drawAnimation();
                  ChatLogger.handle("§c[AutoBuy] Хранилище заполнено. Продажа приостановлена.");
                  AuctionTradeExecutor.resolve(true);
               }
            } else if (this.advancePosition(var13)) {
               AuctionTradeExecutor.refresh();
               ChatLogger.handle("§a[AutoBuy] Товар продан! Хранилище освободилось.");
               if (!this.expandAnimation()) {
                  AuctionTradeExecutor.resolve(true);
               }
            } else if (this.latest.process("FunTime") && !target && !pending && this.computeResponse(var13)) {
               this.performVector(var13);
            } else if ((this.latest.process("HolyWorld") || this.latest.process("FunTime")) && !target && !pending && this.fetchProvider(var13)) {
               this.drawProfile(var13);
            } else if (this.checkFrame(var13)) {
               this.savePacket();
               this.pathProject = 0L;
               if (Module.client.player != null) {
                  if (Module.client.currentScreen != null) {
                     Module.client.player.closeScreen();
                  }

                  this.handle(500L, true);
               }
            } else if (var13.contains("Предмет уже продан") || var13.contains("уже купили") || var13.contains("Недостаточно")) {
               this.savePacket();
               this.pathProject = 0L;
               ChatLogger.handle("§c[AutoBuy] §fНе удалось купить! (Предмет продан или ошибка)");
               this.handle(500L, true);
            } else if (var13.contains("Такого предмета Не существует")) {
               if (this.scaleParse) {
                  this.scaleParse = false;
                  this.elementTick++;
                  this.screenSubmit.handle();
                  ChatLogger.handle("§e[AutoParse] §fПредмет не существует на сервере, скип.");
               }
            } else if (var13.contains("выставлен на продажу за")) {
               AuctionTradeExecutor.onTick();
               if (AutoSell.source == null || !AutoSell.source.enabled) {
                  AuctionTradeExecutor.handle(true);
               }
            } else if (this.latest.process("FunTime") && var13.contains("Вы уже подключены к этому серверу")) {
               this.parseMessage();
            } else if (this.latest.process("FunTime") && this.collectModule(var13)) {
               this.parseMessage();
            } else if (this.latest.process("FunTime")
               && (var13.contains("Недопустимо нажимать в режиме AFK") || var13.contains("Данная команда недоступна в режиме AFK"))) {
               this.runPlayer();
            }
         }
      }
   }

   private int parseMessage() {
      return this.apply(true);
   }

   private int apply(boolean var1) {
      if (Module.client.player != null && this.latest.process("FunTime")) {
         int var2 = this.collapsePlayer();
         int var3 = var2 != -1 ? var2 : this.listenerRun;

         int var4;
         do {
            var4 = ThreadLocalRandom.current().nextInt(901, 904);
         } while (var4 == var3);

         this.listenerRun = var4;
         this.indexCancel.compute();
         if (Module.client.currentScreen != null) {
            Module.client.player.closeScreen();
         }

         Module.client.player.networkHandler.sendChatCommand("an" + var4);
         this.rendererCancel.handle();
         if (var1) {
            this.worldDispatch++;
            if (this.worldDispatch > 3) {
               this.worldDispatch = 0;
               this.applyState();
            } else {
               this.projectItem(String.valueOf(var4));
            }
         }

         return var4;
      } else {
         return -1;
      }
   }

   private long readProvider() {
      if (this.layoutSave == 0) {
         return ThreadLocalRandom.current().nextLong(9000L, 12000L);
      } else {
         return this.layoutSave < 5 ? ThreadLocalRandom.current().nextLong(1200L, 2200L) : ThreadLocalRandom.current().nextLong(3000L, 4500L);
      }
   }

   private long blendMatrix2() {
      return ThreadLocalRandom.current().nextLong(2000L, 4501L);
   }

   private long performScale() {
      return ThreadLocalRandom.current().nextLong(600L, 1401L);
   }

   private void projectItem(String var1) {
      this.savePacket();
      this.indexCancel.compute();
      this.outputFetch = var1 == null ? "" : var1;
      this.indexLoad = true;
      this.layoutSave = 0;
      this.entityFilter = System.currentTimeMillis();
      this.layerSample2 = System.currentTimeMillis() + this.blendMatrix2();
      this.providerOffset = false;
      this.messageParse2 = false;
      this.optionFetch = -1;
      this.eventCollapse = 0L;
      this.optionAdvance2 = false;
      this.clientDraw = 0L;
      this.eventReceive.handle();
   }

   void blendMatrix() {
      this.handle(0L, false);
   }

   private void process(long var1) {
      this.handle(var1, false);
   }

   private void handle(long var1, boolean var3) {
      if (Module.client.player != null) {
         this.savePacket();
         this.indexCancel.compute();
         this.optionFetch = this.readScreen();
         if (var3 && Module.client.currentScreen != null) {
            Module.client.player.closeScreen();
         }

         this.indexLoad = true;
         this.layoutSave = 0;
         this.outputFetch = "";
         this.entityFilter = System.currentTimeMillis();
         this.layerSample2 = System.currentTimeMillis() + Math.max(0L, var1);
         this.providerOffset = var3;
         this.messageParse2 = false;
         this.eventCollapse = this.layerSample2 + this.performScale();
         this.optionAdvance2 = false;
         this.clientDraw = 0L;
         this.eventReceive.handle();
      }
   }

   private boolean expandContext() {
      if (!this.providerOffset && Module.client.currentScreen instanceof GenericContainerScreen var1 && this.execute(var1)) {
         this.indexLoad = false;
         this.layoutSave = 0;
         this.outputFetch = "";
         this.entityFilter = 0L;
         this.layerSample2 = 0L;
         this.messageParse2 = false;
         this.optionFetch = -1;
         this.eventCollapse = 0L;
         this.settingSchedule.handle();
         return false;
      }

      if (!this.processKey()) {
         return true;
      }

      if (System.currentTimeMillis() < this.layerSample2) {
         return true;
      }

      if (this.layoutSave == 0 || this.eventReceive.update(this.readProvider())) {
         if (this.providerOffset && Module.client.currentScreen != null) {
            Module.client.player.closeScreen();
         }

         Module.client.player.networkHandler.sendChatCommand("ah");
         this.layoutSave++;
         this.providerOffset = false;
         this.eventCollapse = System.currentTimeMillis() + this.performScale();
         this.eventReceive.handle();
      }

      return true;
   }

   private boolean processKey() {
      if (this.outputFetch.isEmpty()) {
         return true;
      }

      String var1 = this.advanceOption();
      long var2 = System.currentTimeMillis() - this.entityFilter;
      boolean var4 = this.outputFetch.equals(var1) && var2 >= 2500L;
      if (!var4 && !this.messageParse2 && var2 >= 4500L && Module.client.player != null) {
         Module.client.player.networkHandler.sendChatCommand("an" + this.outputFetch);
         this.messageParse2 = true;
         this.entityFilter = System.currentTimeMillis();
         return false;
      }

      var2 = System.currentTimeMillis() - this.entityFilter;
      if (!var4 && var2 < 12000L) {
         return false;
      }

      this.outputFetch = "";
      this.messageParse2 = false;
      return true;
   }

   private boolean convertAction() {
      if (Module.client.player != null && !target && !pending && !this.blockRun && !this.summary.compute() && !this.scaleParse) {
         Screen var1 = Module.client.currentScreen;
         if (!(var1 instanceof ServerPresets) && !(var1 instanceof ClickGuiModernScreen)) {
            if (var1 instanceof GenericContainerScreen var2) {
               if (this.compute(var2)) {
                  this.settingSchedule.handle();
                  return false;
               }

               if (this.resolve(var2)) {
                  this.settingSchedule.handle();
                  return false;
               }
            }

            if (!this.settingSchedule.update(750L)) {
               return false;
            }

            this.handle(0L, true);
            this.settingSchedule.handle();
            return true;
         } else {
            this.settingSchedule.handle();
            return false;
         }
      } else {
         this.settingSchedule.handle();
         return false;
      }
   }

   private boolean execute(GenericContainerScreen var1) {
      if (!this.resolve(var1)) {
         return false;
      }

      if (System.currentTimeMillis() < this.eventCollapse) {
         return false;
      }

      int var2 = ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId;
      return this.optionFetch == -1 || var2 != this.optionFetch;
   }

   private int readScreen() {
      return Module.client.currentScreen instanceof GenericContainerScreen var1 && this.resolve(var1)
         ? ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId
         : -1;
   }

   private boolean computeResponse(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = var1.replaceAll("§.", "").toLowerCase(Locale.ROOT);
      return var2.contains("после входа на режим") && var2.contains("аукцион") && actionRead.matcher(var1).find();
   }

   private boolean fetchProvider(String var1) {
      return var1 == null ? false : configCollapse.matcher(this.unload(var1).toLowerCase(Locale.ROOT)).find();
   }

   private void drawProfile(String var1) {
      long var2 = this.readServer(var1);
      if (Module.client.player != null && Module.client.currentScreen != null) {
         Module.client.player.closeScreen();
      }

      this.handle(var2, true);
   }

   private void performVector(String var1) {
      long var2 = this.attachEvent(var1);
      if (Module.client.player != null && Module.client.currentScreen != null) {
         Module.client.player.closeScreen();
      }

      if (this.summary.compute()) {
         this.scaleParse = false;
         this.eventSend = System.currentTimeMillis() + var2;
         this.screenSubmit.handle();
         this.cacheHandle.handle();
      } else {
         this.process(var2);
      }
   }

   private long attachEvent(String var1) {
      Matcher var2 = actionRead.matcher(var1 == null ? "" : var1);
      if (!var2.find()) {
         return 9000L;
      }

      try {
         int var3 = Integer.parseInt(var2.group(1));
         return Math.max(9000L, var3 * 1000L + 2000L);
      } catch (NumberFormatException var4) {
         return 9000L;
      }
   }

   private long readServer(String var1) {
      Matcher var2 = configCollapse.matcher(this.unload(var1).toLowerCase(Locale.ROOT));
      if (!var2.find()) {
         return 1250L;
      }

      try {
         int var3 = Integer.parseInt(var2.group(1));
         return Math.max(250L, var3 * 1000L + 250L);
      } catch (NumberFormatException var4) {
         return 1250L;
      }
   }

   private boolean expandAnimation() {
      return AutoSell.source != null && AutoSell.source.enabled && AutoSell.source.refresh() && AutoSell.source.render();
   }

   private boolean advancePosition(String var1) {
      if (var1 == null) {
         return false;
      }

      if (var1.contains("У Вас купили") && var1.contains("на /ah")) {
         return true;
      }

      String var2 = this.unload(var1).toLowerCase(Locale.ROOT);
      return var2.contains("купил у вас") && var2.contains(" за ") && (var2.contains("¤") || var2.contains("$"));
   }

   private void runPlayer() {
      if (Module.client.player != null && this.latest.process("FunTime")) {
         String var1 = this.advanceOption();
         if ("N/A".equals(var1) && !this.playerEvaluate.isEmpty()) {
            var1 = this.playerEvaluate;
         }

         if ("N/A".equals(var1) && this.listenerRun != -1) {
            var1 = String.valueOf(this.listenerRun);
         }

         if (!"N/A".equals(var1)) {
            this.playerEvaluate = var1;
            Module.client.player.networkHandler.sendChatCommand("hub");
            this.blockRun = true;
            this.rangeRelease.handle();
            ChatLogger.handle("§e[AutoBuy] §fAFK заблокировал команду. Переподключаемся через /hub -> /an" + this.playerEvaluate + "...");
         }
      }
   }

   private void renderMatrix() {
      if (this.latest.process("FunTime")) {
         int var1 = this.collapsePlayer();
         if (var1 != -1) {
            this.listenerRun = var1;
            this.inputInvoke = false;
            this.indexCheck.handle();
         }
      }
   }

   private boolean tickModule() {
      if (Module.client.player != null && !this.indexLoad && !this.blockRun && this.listenerRun != -1 && !this.inputInvoke) {
         if (this.collapsePlayer() != -1) {
            return false;
         }

         if (!this.indexCheck.update(2500L)) {
            return false;
         }

         Module.client.player.networkHandler.sendChatCommand("an" + this.listenerRun);
         this.inputInvoke = true;
         this.projectItem(String.valueOf(this.listenerRun));
         ChatLogger.handle("§e[AutoBuy] §fПохоже, нас выкинуло в хаб. Повторно заходим на " + this.listenerRun + "...");
         return true;
      } else {
         return false;
      }
   }

   private int collapsePlayer() {
      String var1 = this.advanceOption();
      if ("N/A".equals(var1)) {
         return -1;
      }

      try {
         return Integer.parseInt(var1);
      } catch (NumberFormatException var3) {
         return -1;
      }
   }

   private String advanceOption() {
      try {
         NicknameUtil.instance.handle();
         String var1 = NicknameUtil.instance.compute();
         return var1 != null && !var1.isEmpty() ? var1 : "N/A";
      } catch (Exception var2) {
         return "N/A";
      }
   }

   private boolean checkFrame(String var1) {
      if (var1 == null) {
         return false;
      }

      if (var1.contains("Этот товар уже Купили!")) {
         return true;
      }

      if (!this.latest.process("FunTime")) {
         return false;
      }

      String var2 = var1.toLowerCase(Locale.ROOT);
      return var2.contains("ошибка! этот товар уже купили") || var2.contains("ошибка") && var2.contains("товар уже купили");
   }

   private boolean collectModule(String var1) {
      if (var1 == null) {
         return false;
      }

      String var2 = var1.replaceAll("§.", "").toLowerCase(Locale.ROOT);
      return var2.contains("были кикнуты при подключении") && var2.contains("сервер заполнен");
   }

   public void matchVector() {
      if (this.summary.compute()) {
         this.scanEffect();
      } else {
         if (this.update(false) && !this.enabled) {
            this.submitPoint();
         }
      }
   }

   private void scanEffect() {
      this.summary.process(false);
      this.scaleParse = false;
      this.elementTick = 0;
      this.regionAlign = false;
      this.resourceClamp = false;
      this.handlerRun = "";
      this.sourceCancel = 0L;
      this.eventSend = 0L;
      this.screenSubmit.handle();
      this.cacheHandle.handle();
      this.renderScale();
      this.performListener();
   }

   private void parseOption() {
      if (!this.summary.compute()) {
         this.performListener();
      } else if (!this.regionAlign && !this.update(false)) {
         this.performListener();
      } else {
         if (!this.sessionEncode.isEmpty()) {
            this.measure();
         }
      }
   }

   private boolean closeProvider(String var1) {
      if (var1 == null) {
         return false;
      } else if (this.latest.process("FunTime") && this.computeResponse(var1)) {
         this.performVector(var1);
         return true;
      } else if (var1.contains("Такого предмета Не существует") && this.scaleParse) {
         this.scaleParse = false;
         this.elementTick++;
         this.screenSubmit.handle();
         ChatLogger.handle("§e[AutoParse] §fПредмет не существует на сервере, скип.");
         return true;
      } else {
         return false;
      }
   }

   private void submitPoint() {
      if (!this.enabled) {
         EventHandlerInvoker.handle(this);
      }
   }

   private void performListener() {
      if (!this.enabled) {
         EventHandlerInvoker.process(this);
      }
   }

   public static class CacheEntry {
      public String instance;
      public String data;
      public int context;
      public long config;
      public long state;

      public CacheEntry(String var1, String var2, int var3, long var4, long var6) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var6;
      }
   }

   record DataRecord(RegistryEntry<EntityAttribute> attribute, double value) {
   }

   record FallbackDataRecord(long unitPrice, long lotPrice, int count) {
   }

   final class PrimaryCacheEntry {
      private final ItemStack data;
      private String context;
      private String config;
      private String state;
      private String cache;
      private Identifier output;

      PrimaryCacheEntry(ItemStack var2) {
         this.data = var2;
      }

      String handle() {
         if (this.context == null) {
            this.context = HolyWorldHelper.update(this.data);
         }

         return this.context;
      }

      String process() {
         if (this.config == null) {
            this.config = HolyWorldHelper.apply(this.data);
         }

         return this.config;
      }

      String compute() {
         if (this.state == null) {
            this.state = this.data.getName().getString();
         }

         return this.state;
      }

      String resolve() {
         if (this.cache == null) {
            this.cache = AutoBuy.this.save(this.compute());
         }

         return this.cache;
      }

      Identifier update() {
         if (this.output == null) {
            this.output = Registries.ITEM.getId(this.data.getItem());
         }

         return this.output;
      }
   }

   record PrimaryDataRecord(long lotPrice, long estimatedValue, long profit, int fingerprint, boolean buyable) {
   }

   record RuntimeDataRecord(String itemName, long maxPrice, HolyWorldHelper.SecondaryDataRecord holyWorldEntry, Item item, String normalizedName) {
   }

   record SecondaryDataRecord(long price, String seller) {
   }
}
