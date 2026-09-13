package ru.wild.command;

import java.util.List;
import java.util.Locale;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.core.manager.FeatureManager;
import ru.wild.modules.misc.AutoMine;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class AutoMineCommand extends Command {
   public AutoMineCommand() {
      super("automine", "Управление AutoMine", ".automine save");
      this.handle("save", List::of);
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: " + this.handle());
      } else {
         String var2 = var1[0];
         Locale var3 = Locale.ROOT;
         String var4 = var2 == null ? null : var2.toLowerCase(var3);
         if (var4 != null && "save".equals(var4)) {
            FeatureManager var5 = WildClient.instance.data;
            if (!(var5 instanceof FeatureManager)) {
               ChatLogger.handle("§cМенеджер модулей не инициализирован.");
            } else {
               FeatureManager var6 = WildClient.instance.data;
               if (var6 == null) {
                  ChatLogger.handle("§cAutoMine не найден.");
               } else {
                  AutoMine var7 = var6.handle(AutoMine.class);
                  if (var7 == null) {
                     ChatLogger.handle("§cAutoMine не найден.");
                  } else {
                     var7.refresh();
                  }
               }
            }
         } else {
            ChatLogger.handle("§cИспользование: " + this.handle());
         }
      }
   }

   static {
      Loader.initialize();
   }
}
