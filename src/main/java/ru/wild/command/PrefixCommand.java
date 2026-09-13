package ru.wild.command;

import java.util.List;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class PrefixCommand extends Command {
   public PrefixCommand() {
      super("prefix", "Изменение префикса команд", ".prefix set <symbol>");
      this.handle("set", List::of);
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length >= 2 && var1[0] != null && var1[0].equalsIgnoreCase("set")) {
         String var4 = var1[1];
         if (var4 != null && var4.length() > 1) {
            ChatLogger.handle("§cПрефикс должен быть одним символом!");
         } else {
            WildClient var3 = WildClient.instance;
            if (var3 != null) {
               var3.handle(var4);
            }

            ChatLogger.handle("§aПрефикс успешно изменен на: §f" + var4);
         }
      } else {
         String var2 = this.handle();
         ChatLogger.handle("§cИспользование: " + var2);
      }
   }

   static {
      Loader.initialize();
   }
}
