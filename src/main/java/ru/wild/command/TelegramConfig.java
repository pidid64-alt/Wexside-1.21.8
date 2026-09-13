package ru.wild.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.network.TelegramApi;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.security.TelegramSecretCipher;
import ru.wild.util.text.ChatLogger;

public class TelegramConfig extends Command {
   private final Gson instance = new GsonBuilder().setPrettyPrinting().create();
   private final File data = new File(WildClient.instance.cache, "telegram.cfg");

   public TelegramConfig() {
      super("tapi", "телеграм API для отправки уведомлений в ТГ", ".tapi <token/chatid/test/clear/info/help/dir/load>");
      this.handle("token", List::of);
      this.handle("chatid", List::of);
      this.handle("test", List::of);
      this.handle("clear", List::of);
      this.handle("info", List::of);
      this.handle("help", List::of);
      this.handle("dir", List::of);
      this.handle("load", List::of);
      this.check();
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: " + this.compute());
      } else {
         switch (var1[0].toLowerCase()) {
            case "dir":
               this.prepare();
               break;
            case "help":
               this.execute();
               break;
            case "info":
               this.apply();
               break;
            case "load":
               this.check();
               break;
            case "test":
               this.resolve();
               break;
            case "clear":
               this.update();
               break;
            case "token":
               this.compute(var1);
               break;
            case "chatid":
               this.resolve(var1);
               break;
            default:
               ChatLogger.handle("§cНеизвестная подкоманда.");
         }
      }
   }

   @Compile
   private void compute(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите токен бота.");
         ChatLogger.handle("§7Пример: §f.tapi token 7836941137:AAGSPTZ8lVbmXUX7zjjijRjs7iyCqgg7aXE");
      } else {
         String var2 = var1[1];
         if (!var2.matches("\\d+:[A-Za-z0-9_-]+")) {
            ChatLogger.handle("§cНеверный формат токена!");
            ChatLogger.handle("§7Формат: §f<числа>:<буквы и цифры>");
            ChatLogger.handle("§7Пример: §f7836941137:AAGSPTZ8lVbmXUX7zjjijRjs7iyCqgg7aXE");
         } else {
            try {
               TelegramConfig.State var3 = this.onTick();
               var3.instance = TelegramSecretCipher.handle(
                  var2, "gUhDvBzdE4xq5f4BxkPvxv70VY44WsuH1O6s2nZ2F9U1w9y1VVG1mXQcUfbJM2DDUCd8NvtM0L4O1t1nn8FwwAVYlChNncdagiv9UR8FpLXXF8iMAtlWY4mEnYtLHPB3"
               );
               this.handle(var3);
               this.process(var3);
               ChatLogger.handle("§aТокен бота успешно сохранён (зашифрован)");
               if (var3.data != null && !var3.data.isEmpty()) {
                  ChatLogger.handle("§aИспользуйте §f.tapi test §aдля проверки");
               } else {
                  ChatLogger.handle("§eТеперь установите Chat ID: §f.tapi chatid <ID>");
               }
            } catch (Exception var4) {
               ChatLogger.handle("§cОшибка шифрования токена.");
               var4.printStackTrace();
            }
         }
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите Chat ID.");
         ChatLogger.handle("§7Пример: §f.tapi chatid 123456789");
         ChatLogger.handle("§7Или для групп: §f.tapi chatid -100123456789");
      } else {
         String var2 = var1[1];
         if (!var2.matches("-?\\d+")) {
            ChatLogger.handle("§cChat ID должен быть числом!");
            ChatLogger.handle("§7Для личных чатов: §f123456789");
            ChatLogger.handle("§7Для групп: §f-100123456789");
         } else {
            try {
               TelegramConfig.State var3 = this.onTick();
               var3.data = TelegramSecretCipher.handle(
                  var2, "gUhDvBzdE4xq5f4BxkPvxv70VY44WsuH1O6s2nZ2F9U1w9y1VVG1mXQcUfbJM2DDUCd8NvtM0L4O1t1nn8FwwAVYlChNncdagiv9UR8FpLXXF8iMAtlWY4mEnYtLHPB3"
               );
               this.handle(var3);
               this.process(var3);
               ChatLogger.handle("§aChat ID успешно сохранён (зашифрован)");
               if (var3.instance != null && !var3.instance.isEmpty()) {
                  ChatLogger.handle("§aИспользуйте §f.tapi test §aдля проверки");
               } else {
                  ChatLogger.handle("§eТеперь установите токен: §f.tapi token <токен>");
               }
            } catch (Exception var4) {
               ChatLogger.handle("§cОшибка шифрования Chat ID.");
               var4.printStackTrace();
            }
         }
      }
   }

   @Compile
   private void resolve() {
      if (!TelegramApi.handle()) {
         ChatLogger.handle("§cСначала настройте токен и Chat ID!");
         ChatLogger.handle("§7Используйте: §f.tapi token <токен>");
         ChatLogger.handle("§7Используйте: §f.tapi chatid <ID>");
      } else {
         String var1 = "Тестовое сообщение от Wild Client!\nВаш Telegram API настроен корректно.";
         TelegramApi.handle(var1);
         ChatLogger.handle("§aТестовое сообщение отправлено в Telegram!");
      }
   }

   @Compile
   private void update() {
      try {
         TelegramConfig.State var1 = new TelegramConfig.State();
         this.handle(var1);
         TelegramApi.handle("", "");
         ChatLogger.handle("§cДанные Telegram API очищены.");
      } catch (Exception var2) {
         ChatLogger.handle("§cОшибка очистки данных.");
      }
   }

   @Compile
   private void apply() {
      try {
         TelegramConfig.State var1 = this.onTick();
         boolean var2 = var1.instance != null && !var1.instance.isEmpty();
         boolean var3 = var1.data != null && !var1.data.isEmpty();
         ChatLogger.handle("§fИнформация о Telegram API:");
         ChatLogger.handle(" §7Токен бота: " + (var2 ? "§aУстановлен ✓" : "§cНе установлен ✗"));
         ChatLogger.handle(" §7Chat ID: " + (var3 ? "§aУстановлен ✓" : "§cНе установлен ✗"));
         ChatLogger.handle(" §7Статус: " + (TelegramApi.handle() ? "§aГотов к работе" : "§cТребуется настройка"));
         if (var2 && var3) {
            ChatLogger.handle(" §7Используйте §f.tapi test §7для проверки");
         } else {
            ChatLogger.handle("");
            ChatLogger.handle("§eКак настроить:");
            if (!var2) {
               ChatLogger.handle("§c1. Создайте бота:");
               ChatLogger.handle(" §7• Найдите §f@BotFather §7в Telegram");
               ChatLogger.handle(" §7• Отправьте §f/newbot");
               ChatLogger.handle(" §7• Скопируйте токен");
               ChatLogger.handle(" §7• §f.tapi token <токен>");
               ChatLogger.handle("");
            }

            if (!var3) {
               ChatLogger.handle("§c2. Получите Chat ID:");
               ChatLogger.handle(" §a▸ Способ 1 (простой):");
               ChatLogger.handle("   §7• Найдите §f@userinfobot §7в Telegram");
               ChatLogger.handle("   §7• Нажмите START");
               ChatLogger.handle("   §7• Скопируйте число");
               ChatLogger.handle("");
               ChatLogger.handle(" §a▸ Способ 2 (через бота):");
               ChatLogger.handle("   §7• Найдите §f@getmyid_bot");
               ChatLogger.handle("   §7• Нажмите START");
               ChatLogger.handle("   §7• Получите Chat ID");
               ChatLogger.handle("");
               ChatLogger.handle(" §a▸ Способ 3 (ручной):");
               ChatLogger.handle("   §7• Напишите своему боту");
               ChatLogger.handle("   §7• Откройте в браузере:");
               ChatLogger.handle("   §fhttps://api.telegram.org/bot<ТОКЕН>/getUpdates");
               ChatLogger.handle("   §7• Найдите §fchat.id §7в JSON");
               ChatLogger.handle("");
               ChatLogger.handle(" §7Затем: §f.tapi chatid <ID>");
            }
         }
      } catch (Exception var4) {
         ChatLogger.handle("§cОшибка получения информации.");
         var4.printStackTrace();
      }
   }

   @Compile
   private void execute() {
      ChatLogger.handle("§f§l═══════════════════════════════════════");
      ChatLogger.handle("§e§l          КАК ПОЛУЧИТЬ CHAT ID?");
      ChatLogger.handle("§f§l═══════════════════════════════════════");
      ChatLogger.handle("");
      ChatLogger.handle("§a§l▸ Способ 1 - Самый простой:");
      ChatLogger.handle(" §71. Найдите бота §f@userinfobot §7в Telegram");
      ChatLogger.handle(" §72. Нажмите §fSTART");
      ChatLogger.handle(" §73. Бот пришлёт вам Chat ID");
      ChatLogger.handle(" §74. Скопируйте число: §f.tapi chatid <число>");
      ChatLogger.handle("");
      ChatLogger.handle("§a§l▸ Способ 2 - Через другого бота:");
      ChatLogger.handle(" §7Найдите любого из этихботов:");
      ChatLogger.handle(" §f• @getmyid_bot");
      ChatLogger.handle(" §f• @RawDataBot");
      ChatLogger.handle(" §f• @myidbot");
      ChatLogger.handle(" §7Нажмите START и получите Chat ID");
      ChatLogger.handle("");
      ChatLogger.handle("§a§l▸ Способ 3 - Через API:");
      ChatLogger.handle(" §71. Напишите своему боту любое сообщение");
      ChatLogger.handle(" §72. Откройте в браузере:");
      ChatLogger.handle(" §fhttps://api.telegram.org/bot<ВАШ_ТОКЕН>/getUpdates");
      ChatLogger.handle(" §73. Найдите в JSON: §f\"chat\":{\"id\":123456789}");
      ChatLogger.handle(" §74. Используйте: §f.tapi chatid 123456789");
      ChatLogger.handle("");
      ChatLogger.handle("§c§l▸ Для отправки в ГРУППУ:");
      ChatLogger.handle(" §71. Добавьте бота в группу");
      ChatLogger.handle(" §72. Напишите что-то в группе");
      ChatLogger.handle(" §73. Используйте getUpdates (способ 3)");
      ChatLogger.handle(" §74. Chat ID группы начинается с §f-100");
      ChatLogger.handle("");
      ChatLogger.handle("§f§l═══════════════════════════════════════");
      ChatLogger.handle("§7Подробнее: §fhttps://t.me/userinfobot");
      ChatLogger.handle("§f§l═══════════════════════════════════════");
   }

   @Compile
   private void prepare() {
      try {
         File var1 = this.data.getParentFile();
         if (!var1.exists()) {
            var1.mkdirs();
         }

         String var2 = System.getProperty("os.name").toLowerCase();
         if (var2.contains("win")) {
            Runtime.getRuntime().exec(new String[]{"explorer", var1.getAbsolutePath()});
         } else if (var2.contains("mac")) {
            Runtime.getRuntime().exec(new String[]{"open", var1.getAbsolutePath()});
         } else {
            Runtime.getRuntime().exec(new String[]{"xdg-open", var1.getAbsolutePath()});
         }

         ChatLogger.handle("§aПапка с конфигом открыта");
      } catch (Exception var3) {
         ChatLogger.handle("§cНе удалось открыть папку.");
      }
   }

   @Compile
   private void check() {
      TelegramConfig.State var1 = this.onTick();
      this.process(var1);
      if (TelegramApi.handle() && toggleState.inGameHud != null && toggleState.inGameHud.getChatHud() != null) {
         ChatLogger.handle("§aTelegram API загружен успешно.");
      }
   }

   @Compile
   private TelegramConfig.State onTick() {
      if (!this.data.exists()) {
         return new TelegramConfig.State();
      }

      try (BufferedReader var1 = Files.newBufferedReader(this.data.toPath(), StandardCharsets.UTF_8)) {
         TelegramConfig.State var2 = (TelegramConfig.State)this.instance.fromJson(var1, TelegramConfig.State.class);
         return var2 != null ? var2 : new TelegramConfig.State();
      } catch (Exception var6) {
         var6.printStackTrace();
         return new TelegramConfig.State();
      }
   }

   @Compile
   private void handle(TelegramConfig.State var1) throws Exception {
      if (!this.data.getParentFile().exists()) {
         this.data.getParentFile().mkdirs();
      }

      try (BufferedWriter var2 = Files.newBufferedWriter(this.data.toPath(), StandardCharsets.UTF_8)) {
         this.instance.toJson(var1, var2);
      }
   }

   private void process(TelegramConfig.State var1) {
      try {
         String var2 = "";
         String var3 = "";
         if (var1.instance != null && !var1.instance.isEmpty()) {
            var2 = TelegramSecretCipher.process(
               var1.instance,
               "gUhDvBzdE4xq5f4BxkPvxv70VY44WsuH1O6s2nZ2F9U1w9y1VVG1mXQcUfbJM2DDUCd8NvtM0L4O1t1nn8FwwAVYlChNncdagiv9UR8FpLXXF8iMAtlWY4mEnYtLHPB3"
            );
         }

         if (var1.data != null && !var1.data.isEmpty()) {
            var3 = TelegramSecretCipher.process(
               var1.data, "gUhDvBzdE4xq5f4BxkPvxv70VY44WsuH1O6s2nZ2F9U1w9y1VVG1mXQcUfbJM2DDUCd8NvtM0L4O1t1nn8FwwAVYlChNncdagiv9UR8FpLXXF8iMAtlWY4mEnYtLHPB3"
            );
         }

         TelegramApi.handle(var2, var3);
      } catch (Exception var4) {
         ChatLogger.handle("§cОшибка расшифровки данных.");
         var4.printStackTrace();
      }
   }

   static {
      Loader.initialize();
   }

   static class State {
      String instance;
      String data;
   }
}
