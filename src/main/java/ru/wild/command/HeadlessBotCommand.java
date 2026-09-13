package ru.wild.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ru.wild.automation.HeadlessBotConnector;
import ru.wild.automation.HeadlessBotEngine;
import ru.wild.automation.HeadlessBotSession;
import ru.wild.core.Command;
import ru.wild.util.text.ChatLogger;

public final class HeadlessBotCommand extends Command {
   public HeadlessBotCommand() {
      super("bot", "Управление хедлесс-ботами", ".bot connect <ник> <ip[:port]> | remove | chat | control <ник> | return | list | clear");
      this.handle("connect", List::of);
      this.handle("remove", () -> HeadlessBotEngine.process().stream().map(HeadlessBotEngine.ServerEntry::name).collect(Collectors.toList()));
      this.handle("chat", () -> HeadlessBotEngine.instance.stream().map(HeadlessBotSession::handle).collect(Collectors.toList()));
      this.handle("control", () -> HeadlessBotEngine.instance.stream().map(HeadlessBotSession::handle).collect(Collectors.toList()));
      this.handle("return", List::of);
      this.handle("list", List::of);
      this.handle("clear", List::of);
   }

   @Override
   public void process(String[] var1) {
      if (var1.length != 0 && !var1[0].equalsIgnoreCase("help")) {
         switch (var1[0].toLowerCase()) {
            case "connect":
               if (var1.length < 3) {
                  ChatLogger.handle("§c[Bot] Использование: .bot connect <ник> <ip[:port] или домен>");
                  return;
               }

               HeadlessBotConnector.handle(var1[1], var1[2]);
               break;
            case "remove":
               if (var1.length < 2) {
                  ChatLogger.handle("§c[Bot] Использование: .bot remove <ник>");
                  return;
               }

               HeadlessBotEngine.ServerEntry var7 = HeadlessBotEngine.handle(var1[1]);
               if (var7 == null) {
                  ChatLogger.handle("§c[Bot] Бот не найден: " + var1[1]);
                  return;
               }

               if (!HeadlessBotEngine.compute(var1[1])) {
                  ChatLogger.handle("§7[Bot] Профиль уже отключён: §f" + var7.name());
                  return;
               }

               ChatLogger.handle("§7[Bot] §fОтключён §c" + var7.name());
               break;
            case "chat":
               if (var1.length < 3) {
                  ChatLogger.handle("§c[Bot] Использование: .bot chat <ник> <текст>");
                  return;
               }

               HeadlessBotSession var6 = HeadlessBotEngine.execute(var1[1]);
               if (var6 == null) {
                  ChatLogger.handle("§c[Bot] Бот не найден: " + var1[1]);
                  return;
               }

               String var5 = String.join(" ", Arrays.copyOfRange(var1, 2, var1.length));
               if (!var6.handle(var5)) {
                  ChatLogger.handle("§c[Bot] Бот ещё не вошёл, отключён или сообщение пустое/длиннее 256 символов");
                  return;
               }

               ChatLogger.handle("§7[Бот §a" + var6.handle() + "§7] §8→ §f" + var5);
               break;
            case "list":
               if (HeadlessBotEngine.process().isEmpty()) {
                  ChatLogger.handle("§7[Bot] Ботов нет");
                  return;
               }

               ChatLogger.handle(
                  "§7[Bot] Профили: §f"
                     + HeadlessBotEngine.process()
                        .stream()
                        .map(var0 -> var0.name() + " §8[" + var0.state().name().toLowerCase() + "§8]§f")
                        .collect(Collectors.joining(", "))
               );
               break;
            case "control":
               if (var1.length < 2) {
                  ChatLogger.handle("§c[Bot] Использование: .bot control <ник>");
                  return;
               }

               HeadlessBotSession var4 = HeadlessBotEngine.execute(var1[1]);
               if (var4 == null || !var4.onTick()) {
                  ChatLogger.handle("§c[Bot] Бот не найден или ещё не зашёл: " + var1[1]);
                  return;
               }

               if (!HeadlessBotEngine.handle(var4)) {
                  ChatLogger.handle("§c[Bot] Сейчас нельзя переключить управление: проверь соединение хоста и дождись завершения смены сервера");
                  return;
               }

               ChatLogger.handle("§7[Bot] Управление §a" + var1[1] + "§7. Верни себя: §f.bot return");
               break;
            case "return":
               HeadlessBotEngine.resolve();
               ChatLogger.handle("§7[Bot] Управление возвращено хосту");
               break;
            case "clear":
               HeadlessBotEngine.tick();
               ChatLogger.handle("§7[Bot] Все боты отключены");
               break;
            default:
               ChatLogger.handle("§c[Bot] Неизвестная под-команда: §f" + var1[0] + " §7— см. §f.bot");
         }
      } else {
         ChatLogger.handle("§7[Bot] Команды:");
         ChatLogger.handle("  §f.bot connect §7<ник> <ip[:port]/домен> §8— подключить бота");
         ChatLogger.handle("  §f.bot control §7<ник> §8— вселиться в бота (управление)");
         ChatLogger.handle("  §f.bot return §8— вернуться к хосту");
         ChatLogger.handle("  §f.bot chat §7<ник> <текст> §8— написать в чат от имени бота");
         ChatLogger.handle("  §f.bot list §8— список ботов");
         ChatLogger.handle("  §f.bot remove §7<ник> §8— отключить бота");
         ChatLogger.handle("  §f.bot clear §8— отключить всех");
         ChatLogger.handle("§8Менеджер и модули ботов — вкладка Bots в боковой панели ClickGUI.");
      }
   }
}
