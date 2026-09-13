package ru.wild.modules.misc;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "SeeInvisibles", category = ModuleCategory.Misc, description = "Показ игроков в невидимости")
public class SeeInvisibles extends Module {
   public final NumberSetting source = new NumberSetting("Прозрачность", 0.5F, 0.3F, 1.0F, 0.1F, false);

   public SeeInvisibles() {
      this.handle(this.source);
   }
}
