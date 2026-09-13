package ru.wild.command;

import java.util.List;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.modules.misc.AutoBuy;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class AutoBuyCommand extends Command {
   public AutoBuyCommand() {
      super("autobuy", "Управление AutoBuy", ".autobuy <ignore/unignore/list/clear> [name]");
      this.handle("ignore", this::execute);
      this.handle("add", this::execute);
      this.handle("unignore", AutoBuy::render);
      this.handle("remove", AutoBuy::render);
      this.handle("list", List::of);
      this.handle("clear", List::of);
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         this.apply();
      } else {
         switch (var1[0].toLowerCase()) {
            case "add":
            case "ignore":
               this.compute(var1);
               break;
            case "del":
            case "remove":
            case "unignore":
               this.resolve(var1);
               break;
            case "list":
               this.resolve();
               break;
            case "clear":
               this.update();
               break;
            default:
               this.apply();
         }
      }
   }

   private void compute(String[] var1) {
      String var2 = this.update(var1);
      if (var2 == null || var2.isBlank()) {
         ChatLogger.handle("§cУкажите ник игрока: §f.autobuy ignore Nick");
      } else if (AutoBuy.resolve(var2)) {
         ChatLogger.handle("§e[AutoBuy] Игрок уже в ignore: §f" + var2);
      } else if (AutoBuy.process(var2)) {
         this.prepare();
         ChatLogger.handle("§a[AutoBuy] Игрок добавлен в ignore: §f" + var2);
      } else {
         ChatLogger.handle("§c[AutoBuy] Некорректный ник.");
      }
   }

   private void resolve(String[] var1) {
      String var2 = this.update(var1);
      if (var2 != null && !var2.isBlank()) {
         if (AutoBuy.compute(var2)) {
            this.prepare();
            ChatLogger.handle("§a[AutoBuy] Игрок удален из ignore: §f" + var2);
         } else {
            ChatLogger.handle("§e[AutoBuy] Игрок не найден в ignore: §f" + var2);
         }
      } else {
         ChatLogger.handle("§cУкажите ник игрока: §f.autobuy unignore Nick");
      }
   }

   private void resolve() {
      List var1 = AutoBuy.render();
      if (var1.isEmpty()) {
         ChatLogger.handle("§7[AutoBuy] Ignore-список продавцов пуст.");
      } else {
         ChatLogger.handle("§f[AutoBuy] Ignore-продавцы (§7" + var1.size() + "§f): §7" + String.join(", ", var1));
      }
   }

   private void update() {
      if (AutoBuy.render().isEmpty()) {
         ChatLogger.handle("§7[AutoBuy] Ignore-список продавцов уже пуст.");
      } else {
         AutoBuy.refresh();
         this.prepare();
         ChatLogger.handle("§a[AutoBuy] Ignore-список продавцов очищен.");
      }
   }

   private void apply() {
      ChatLogger.handle("§cИспользование: " + this.compute());
      ChatLogger.handle("§7Пример: §f.autobuy ignore QWEERZIK");
   }

   private String update(String[] var1) {
      if (var1.length < 2) {
         return null;
      } else if ("+".equals(var1[1])) {
         return var1.length >= 3 ? var1[2] : null;
      } else {
         return var1[1];
      }
   }

   private List<String> execute() {
      return toggleState.getNetworkHandler() == null
         ? List.of()
         : toggleState.getNetworkHandler()
            .getPlayerList()
            .stream()
            .map(var0 -> var0.getProfile().getName())
            .filter(var0 -> var0 != null && !var0.isBlank())
            .toList();
   }

   private void prepare() {
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   static {
      Loader.initialize();
   }
}
