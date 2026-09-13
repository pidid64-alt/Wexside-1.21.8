package ru.wild.modules.visuals;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.HudRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.core.manager.FriendManager;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.render.RoundedRectRenderer;

@ModuleRegister(name = "ProtectInfo", description = "Скрывает ники, домены, бренды и заменяет скорборд", category = ModuleCategory.Visuals)
public class ProtectInfo extends Module {
   private static final String providerFetch = "Текст";
   private static final String profileDraw = "Исходящий чат";
   private static final String vectorPerform = "Домены/IP";
   private static final String eventAttach = "Скины";
   private static final String serverRead = "Рамки";
   private static final String positionAdvance = "Картины";
   private static final String[] frameCheck = new String[]{
      "Funtime",
      "Spookytime",
      "HolyWorld",
      "LonyGrief",
      "Wellmine",
      "ArtyGrief",
      "Aresmine",
      "Triada",
      "SlimeWorld",
      "VimeMC",
      "ReallyWorld",
      "MineBlaze",
      "DexLand",
      "TeslaCraft",
      "MusteryWorld",
      "Gamely",
      "SunRise",
      "MSTNetwork",
      "ReallyGrief",
      "MineLand",
      "LastCraft",
      "McSkill",
      "Hypixel",
      "Фантайм",
      "Фан тайм",
      "фантайм",
      "Фунтиме",
      "Fun time",
      "Fun-Time",
      "Fun_time",
      "FT",
      "spacetimes",
      "spookytime",
      "GuvsHvh"
   };
   private static final String moduleCollect = "Src by SoftArax";
   private static final Pattern providerClose = Pattern.compile("(?iu)\\b(" + String.join("|", frameCheck) + ")\\s*\\.\\s*([a-zа-я]{2,12})\\b");
   private static final Pattern presetSave = Pattern.compile(
      "(?iu)(?<![\\w.@-])(?!wildclient\\.org\\b)(?:https?://)?(?:[a-z0-9-]+\\.)+(?:ru|su|fun|net|org|com|me|pw|xyz|pro|gg|top|site|online)\\b(?:/[\\w\\-./?=&%#+~@:]*)?"
   );
   private static final Pattern windowConvert = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}(?::\\d{2,5})?\\b");
   private static final Pattern[] presetWrite = unload();
   private static final Identifier colorMeasure = Identifier.of("wild", "textures/png/zov.png");
   private static final Identifier animationSchedule = Identifier.of("wild", "textures/png/obla.png");
   private static final Identifier rendererScan = Identifier.of("wild", "textures/protect/streamer_skin.png");
   private static SkinTextures sourceBuild;
   private static final Pattern outputCollapse = Pattern.compile("(?i)(?:\\u00A7|\\u0412\\u00A7)[0-9a-fk-or]");
   private static final Pattern profileInvoke = Pattern.compile("(?iu)(?:анарх(?:ия|ии)?|anarchy|an)\\s*(?:[-:#№]|\\s)*\\d{1,5}");
   public final BooleanSetting source = new BooleanSetting("Друзья", false);
   public final ChoiceSetting target = new ChoiceSetting(
      "Что скрывать", new BooleanSetting("Свой ник", true), new BooleanSetting("Все ники", false), new BooleanSetting("Анархию", false)
   );
   public final ChoiceSetting pending = new ChoiceSetting(
      "Защита",
      new BooleanSetting("Текст", true),
      new BooleanSetting("Исходящий чат", true),
      new BooleanSetting("Домены/IP", true),
      new BooleanSetting("Скины", true),
      new BooleanSetting("Рамки", true),
      new BooleanSetting("Картины", true)
   );
   public final StringSetting previous = new StringSetting("Замена", "Wild");
   public final StringSetting latest = new StringSetting("Кастом ник", "Protect");
   public final StringSetting summary = new StringSetting("Кастом анархия", "Скрыто");
   public final BooleanSetting matrixBlend = new BooleanSetting("Цвет скорборда", true);
   public final ModeSetting vectorMatch = new ModeSetting("Оттенок", "Голубой", "Голубой", "Тёмно-синий");
   public final BooleanSetting itemProject = new BooleanSetting("Рендерить пнг", false);
   public final ModeSetting responseCompute = new ModeSetting("Вариация ПНГ ", "Чоткая", "Хмырь", "Чоткая");

   public ProtectInfo() {
      this.latest.handle(() -> !this.target.process("Свой ник"));
      this.summary.handle(() -> !this.target.process("Анархию"));
      this.vectorMatch.handle(() -> !this.matrixBlend.compute());
      this.responseCompute.handle(() -> !this.itemProject.compute());
      this.handle(
         this.source,
         this.target,
         this.pending,
         this.previous,
         this.latest,
         this.summary,
         this.matrixBlend,
         this.vectorMatch,
         this.itemProject,
         this.responseCompute
      );
   }

   @EventHandler
   public void handle(HudRenderContext var1) {
      if (this.itemProject.compute() && Module.client.world != null && Module.client.player != null) {
         RoundedRectRenderer var2 = var1.resolve();
         float var3 = 220.0F;
         float var4 = 250.0F;
         float var5 = var1.apply();
         float var6 = var1.execute();
         float var7 = var5 - var3 - 2.0F - 5.0F;
         float var8 = var6 / 2.0F - var4 / 2.0F - 60.0F;
         Identifier var9 = this.responseCompute.compute().equals("Чоткая") ? colorMeasure : animationSchedule;
         int var10 = handle(var9);
         if (var10 > 0) {
            GlStateManager._bindTexture(var10);
            GlStateManager._texParameter(3553, 10240, 9728);
            GlStateManager._texParameter(3553, 10241, 9728);
            var2.update(1.0F);
            float var11 = var7 + var3 / 2.0F;
            float var12 = var8 + var4 / 2.0F;
            var2.handle(var11, var12);
            var2.process(1.0F, -1.0F);
            var2.handle(-var11, -var12);
            var2.handle(var10, var7, var8, var3, var4);
            var2.prepare();
            var2.check();
            var2.prepare();
            var2.onTick();
         }
      }
   }

   public static boolean refresh() {
      ProtectInfo var0 = load();
      return var0 != null && var0.enabled;
   }

   public static String handle(String var0) {
      ProtectInfo var1 = load();
      if (var1 != null && var1.enabled) {
         String var2 = process(var0, var1);
         return !var1.pending.process("Текст") ? var2 : handle(var2, var1.pending.process("Домены/IP"), save());
      } else {
         return var0;
      }
   }

   public static String process(String var0) {
      ProtectInfo var1 = load();
      if (var1 != null && var1.enabled && var1.pending.process("Исходящий чат")) {
         String var2 = process(var0, var1);
         return handle(var2, var1.pending.process("Домены/IP"), save());
      } else {
         return var0;
      }
   }

   public static OrderedText handle(OrderedText var0) {
      ProtectInfo var1 = load();
      if (var0 != null && var1 != null && var1.enabled && var1.pending.process("Текст")) {
         ArrayList var2 = new ArrayList();
         StringBuilder var3 = new StringBuilder();
         var0.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            var2.add(new ProtectInfo.PrimaryDataRecord(var5x, var3x));
            var3.append(var5x);
            return true;
         });
         String var4 = var3.toString();
         ProtectInfo.DataRecord var5 = handle(var2, var4, var1.pending.process("Домены/IP"), save());
         return !var5.changed ? var0 : var1x -> {
            int var2x = 0;

            for (ProtectInfo.PrimaryDataRecord var4x : var5.tokens) {
               for (int var5x = 0; var5x < var4x.text.length(); var2x++) {
                  int var6 = var4x.text.codePointAt(var5x);
                  if (!var1x.accept(var2x, var4x.style, var6)) {
                     return false;
                  }

                  var5x += Character.charCount(var6);
               }
            }

            return true;
         };
      } else {
         return var0;
      }
   }

   public static boolean render() {
      ProtectInfo var0 = load();
      return var0 != null && var0.enabled && var0.pending.process("Скины");
   }

   public static boolean tick() {
      ProtectInfo var0 = load();
      return var0 != null && var0.enabled && var0.pending.process("Рамки");
   }

   public static boolean drawAnimation() {
      ProtectInfo var0 = load();
      return var0 != null && var0.enabled && var0.pending.process("Картины");
   }

   public static boolean encodePoint() {
      ProtectInfo var0 = load();
      return var0 != null && var0.enabled && var0.matrixBlend.compute();
   }

   public static SkinTextures animate() {
      if (sourceBuild == null) {
         sourceBuild = new SkinTextures(rendererScan, null, null, null, Model.SLIM, true);
      }

      return sourceBuild;
   }

   public static Text handle(Text var0) {
      if (var0 != null && Module.client.player != null) {
         ProtectInfo var1 = load();
         if (var1 != null && var1.enabled) {
            MutableText var2 = Text.empty();
            var0.visit((var1x, var2x) -> {
               String var3 = handle(var2x);
               var2.append(Text.literal(var3).setStyle(var1x));
               return Optional.empty();
            }, Style.EMPTY);
            return var2;
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static Text process(Text var0) {
      Text var1 = handle(var0);
      return !encodePoint() ? var1 : handle(var1, submit());
   }

   public static String compute(String var0) {
      return handle(var0);
   }

   public static String handle(String var0, ProtectInfo var1) {
      if (var0 != null && !var0.isEmpty()) {
         String var2 = process(var0, var1);
         if (var1.pending.process("Текст")) {
            var2 = handle(var2, var1.pending.process("Домены/IP"), handle(var1));
         }

         return var2;
      } else {
         return var0;
      }
   }

   private static ProtectInfo load() {
      return WildClient.instance != null && WildClient.instance.data != null ? WildClient.instance.data.handle(ProtectInfo.class) : null;
   }

   private static String save() {
      ProtectInfo var0 = load();
      return handle(var0);
   }

   private static String handle(ProtectInfo var0) {
      if (var0 == null) {
         return "Wild";
      }

      String var1 = var0.previous.compute().trim();
      return var1.isEmpty() ? "Wild" : var1;
   }

   private static Formatting submit() {
      ProtectInfo var0 = load();
      return var0 != null && "Тёмно-синий".equals(var0.vectorMatch.compute()) ? Formatting.DARK_BLUE : Formatting.AQUA;
   }

   private static String process(String var0, ProtectInfo var1) {
      if (var0 != null && !var0.isEmpty()) {
         String var2 = var0;
         if (var1.target.process("Свой ник")) {
            var2 = compute(var2, var1);
         }

         if (var1.target.process("Все ники")) {
            var2 = update(var2, var1);
         }

         if (var1.source.compute()) {
            var2 = resolve(var2, var1);
         }

         if (var1.target.process("Анархию")) {
            NicknameUtil.instance.handle(500L);
            var2 = process(var2, var1.summary.compute(), NicknameUtil.instance.compute());
         }

         return var2;
      } else {
         return var0;
      }
   }

   private static String compute(String var0, ProtectInfo var1) {
      String var2 = process(var1);
      String var3 = var0;
      if (Module.client != null) {
         if (Module.client.getSession() != null) {
            var3 = handle(var3, Module.client.getSession().getUsername(), var2);
         }

         if (Module.client.player != null) {
            var3 = handle(var3, Module.client.player.getGameProfile() == null ? null : Module.client.player.getGameProfile().getName(), var2);
            var3 = handle(var3, Module.client.player.getName() == null ? null : Module.client.player.getName().getString(), var2);
         }
      }

      return handle(var3, NicknameUtil.data, var2);
   }

   private static String resolve(String var0, ProtectInfo var1) {
      String var2 = process(var1);
      String var3 = var0;
      List<String> var4 = FriendManager.resolve();
      if (var4 != null && !var4.isEmpty()) {
         for (String var6 : var4) {
            var3 = handle(var3, var6, var2);
         }

         return var3;
      } else {
         return var3;
      }
   }

   private static String update(String var0, ProtectInfo var1) {
      if (Module.client != null && Module.client.getNetworkHandler() != null) {
         String var2 = process(var1);
         String var3 = var0;

         for (PlayerListEntry var5 : Module.client.getNetworkHandler().getPlayerList()) {
            if (var5 != null && var5.getProfile() != null) {
               var3 = handle(var3, var5.getProfile().getName(), var2);
            }
         }

         return var3;
      } else {
         return var0;
      }
   }

   private static String process(ProtectInfo var0) {
      String var1 = var0.latest.compute();
      return var1 != null && !var1.isBlank() ? var1 : "Protect";
   }

   private static String handle(String var0, String var1, String var2) {
      if (var0 != null && !var0.isEmpty() && var1 != null) {
         String var3 = var2 != null && !var2.isBlank() ? var2 : "Protect";
         String var4 = outputCollapse.matcher(var1).replaceAll("").trim();
         if (!var4.isEmpty() && !var4.equalsIgnoreCase("N/A") && !var4.equalsIgnoreCase(var3)) {
            Pattern var5 = Pattern.compile(Pattern.quote(var4), 66);
            Matcher var6 = var5.matcher(var0);
            StringBuilder var7 = new StringBuilder(var0.length());

            while (var6.find()) {
               String var8 = var6.group();
               if (handle(var0, var6.start(), var6.end()) && !handle(var0, var6.start(), var6.end(), var4, var3)) {
                  var6.appendReplacement(var7, Matcher.quoteReplacement(var3));
               } else {
                  var6.appendReplacement(var7, Matcher.quoteReplacement(var8));
               }
            }

            var6.appendTail(var7);
            return var7.toString();
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }

   private static boolean handle(String var0, int var1, int var2) {
      return (var1 <= 0 || !handle(var0.charAt(var1 - 1))) && (var2 >= var0.length() || !handle(var0.charAt(var2)));
   }

   private static boolean handle(char var0) {
      return Character.isLetterOrDigit(var0) || var0 == '_';
   }

   private static boolean handle(String var0, int var1, int var2, String var3, String var4) {
      if (var4 != null && !var4.isEmpty()) {
         Matcher var5 = Pattern.compile(Pattern.quote(var3), 66).matcher(var4);

         while (var5.find()) {
            String var6 = var4.substring(0, var5.start());
            String var7 = var4.substring(var5.end());
            int var8 = var1 - var6.length();
            int var9 = var2 + var7.length();
            if (var8 >= 0
               && var9 <= var0.length()
               && var0.regionMatches(true, var8, var6, 0, var6.length())
               && var0.regionMatches(true, var2, var7, 0, var7.length())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String process(String var0, String var1, String var2) {
      if (var0 != null && !var0.isEmpty()) {
         String var3 = var1 != null && !var1.isBlank() ? var1 : "Hidden";
         String var4 = profileInvoke.matcher(var0).replaceAll(Matcher.quoteReplacement(var3));
         if (var2 != null && !var2.equals("N/A") && !var2.isBlank()) {
            String var5 = outputCollapse.matcher(var4).replaceAll("").trim();
            if (var5.equals(var2) || var5.equals("-" + var2) || var5.equals("#" + var2) || var5.equals("№" + var2)) {
               return var3;
            }

            Pattern var6 = Pattern.compile("(?iu)(?:анарх(?:ия|ии)?|anarchy|an)\\s*(?:[-:#№]|\\s)*" + Pattern.quote(var2));
            var4 = var6.matcher(var4).replaceAll(Matcher.quoteReplacement(var3));
         }

         return var4;
      } else {
         return var0;
      }
   }

   private static Text handle(Text var0, Formatting var1) {
      MutableText var2 = Text.empty();
      var0.visit((var2x, var3) -> {
         Style var4 = var2x.withColor(var1);
         var2.append(Text.literal(var3).setStyle(var4));
         return Optional.empty();
      }, Style.EMPTY);
      return var2;
   }

   private static String handle(String var0, boolean var1, String var2) {
      if (var0 != null && !var0.isEmpty()) {
         String var3 = var0;
         if (var1) {
            var3 = update(var3);
            var3 = resolve(var3);
            var3 = windowConvert.matcher(var3).replaceAll(Matcher.quoteReplacement("wildclient.org"));
         }

         for (Pattern var7 : presetWrite) {
            var3 = var7.matcher(var3).replaceAll(Matcher.quoteReplacement(var2));
         }

         return var3;
      } else {
         return var0;
      }
   }

   private static ProtectInfo.DataRecord handle(List<ProtectInfo.PrimaryDataRecord> var0, String var1, boolean var2, String var3) {
      ProtectInfo.DataRecord var4 = new ProtectInfo.DataRecord(var0, var1, false);
      if (var2) {
         var4 = handle(var4, presetSave, var0x -> "wildclient.org");
         var4 = handle(var4, providerClose, var0x -> "wildclient.org");
         var4 = handle(var4, windowConvert, var0x -> "wildclient.org");
      }

      for (Pattern var8 : presetWrite) {
         var4 = handle(var4, var8, var1x -> var3);
      }

      return var4;
   }

   private static ProtectInfo.DataRecord handle(ProtectInfo.DataRecord var0, Pattern var1, Function<Matcher, String> var2) {
      Matcher var3 = var1.matcher(var0.text);
      if (!var3.find()) {
         return var0;
      }

      ArrayList<ProtectInfo.PrimaryDataRecord> var4 = new ArrayList<>();
      int var5 = 0;

      do {
         handle(var0.tokens, var4, var5, var3.start());
         var4.add(new ProtectInfo.PrimaryDataRecord((String)var2.apply(var3), handle(var0.tokens, var3.start())));
         var5 = var3.end();
      } while (var3.find());

      handle(var0.tokens, var4, var5, var0.text.length());
      return new ProtectInfo.DataRecord(var4, handle(var4), true);
   }

   private static void handle(List<ProtectInfo.PrimaryDataRecord> var0, List<ProtectInfo.PrimaryDataRecord> var1, int var2, int var3) {
      if (var2 < var3) {
         int var4 = 0;

         for (ProtectInfo.PrimaryDataRecord var6 : var0) {
            int var7 = var4;
            int var8 = var4 + var6.text.length();
            var4 = var8;
            int var9 = Math.max(var2, var7);
            int var10 = Math.min(var3, var8);
            if (var9 < var10) {
               var1.add(new ProtectInfo.PrimaryDataRecord(var6.text.substring(var9 - var7, var10 - var7), var6.style));
            }
         }
      }
   }

   private static Style handle(List<ProtectInfo.PrimaryDataRecord> var0, int var1) {
      int var2 = 0;
      Style var3 = Style.EMPTY;

      for (ProtectInfo.PrimaryDataRecord var5 : var0) {
         int var6 = var2 + var5.text.length();
         if (var1 < var6) {
            return var5.style;
         }

         var2 = var6;
         var3 = var5.style;
      }

      return var3;
   }

   private static String handle(List<ProtectInfo.PrimaryDataRecord> var0) {
      StringBuilder var1 = new StringBuilder();

      for (ProtectInfo.PrimaryDataRecord var3 : var0) {
         var1.append(var3.text);
      }

      return var1.toString();
   }

   private static String resolve(String var0) {
      return providerClose.matcher(var0).replaceAll(Matcher.quoteReplacement("wildclient.org"));
   }

   private static String update(String var0) {
      return presetSave.matcher(var0).replaceAll(Matcher.quoteReplacement("wildclient.org"));
   }

   private static Pattern[] unload() {
      Pattern[] var0 = new Pattern[frameCheck.length];

      for (int var1 = 0; var1 < frameCheck.length; var1++) {
         var0[var1] = Pattern.compile("(?iu)(?<![\\p{L}\\p{N}])" + apply(frameCheck[var1]) + "(?![\\p{L}\\p{N}])");
      }

      return var0;
   }

   private static String apply(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length(); var2++) {
         if (var2 > 0) {
            var1.append("[\\s._-]*");
         }

         var1.append(Pattern.quote(String.valueOf(var0.charAt(var2))));
      }

      return var1.toString();
   }

   private static int handle(Identifier var0) {
      if (Module.client == null) {
         return -1;
      } else {
         TextureManager var1 = Module.client.getTextureManager();
         if (var1 == null) {
            return -1;
         } else {
            AbstractTexture var2 = var1.getTexture(var0);
            if (var2 == null) {
               return -1;
            } else {
               return var2.getGlTexture() instanceof GlTexture var4 ? var4.getGlId() : -1;
            }
         }
      }
   }

   record DataRecord(List<ProtectInfo.PrimaryDataRecord> tokens, String text, boolean changed) {
   }

   record PrimaryDataRecord(String text, Style style) {
   }
}
