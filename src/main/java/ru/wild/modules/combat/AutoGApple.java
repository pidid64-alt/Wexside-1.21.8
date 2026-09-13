package ru.wild.modules.combat;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "AutoGApple", category = ModuleCategory.Combat, description = "Автоматически есть яблочки")
public class AutoGApple extends Module {
   public final NumberSetting source = new NumberSetting("Здоровье", 10.0F, 1.0F, 20.0F, 1.0F, false);

   public AutoGApple() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
   }
}
