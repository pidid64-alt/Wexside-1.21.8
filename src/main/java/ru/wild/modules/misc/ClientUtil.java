package ru.wild.modules.misc;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.network.TelegramApi;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "ClientUtil", category = ModuleCategory.Misc, description = "Настройки для клиента")
public class ClientUtil extends Module {
   public static final BooleanSetting source = new BooleanSetting("Уведомления в Telegram", true);
   public static final BooleanSetting target = new BooleanSetting("Звуки клиента", true);
   public static ChoiceSetting pending = new ChoiceSetting(
      "Звуки", new BooleanSetting("Модули", true), new BooleanSetting("Уведомления", true).handle(() -> !target.compute())
   );
   public static NumberSetting previous = new NumberSetting("Громкость", 100.0F, 10.0F, 100.0F, 1.0F, false).handle(() -> !target.compute());

   public ClientUtil() {
      this.handle(source, target, pending, previous);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (source.compute() && !TelegramApi.handle()) {
         ChatLogger.handle("§cСписок пуст для отправки сообщений. Настройте API через .tapi");
         source.process(false);
      }
   }
}
