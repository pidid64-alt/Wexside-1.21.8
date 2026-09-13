package ru.wild.modules.player;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;

@ModuleRegister(name = "NoPush", description = "Убирает отталкивания от игроков, мобов и блоков", category = ModuleCategory.Player)
public class NoPush extends Module {
   public BooleanSetting source = new BooleanSetting("Players", true);
   public BooleanSetting target = new BooleanSetting("Mobs", true);
   public BooleanSetting pending = new BooleanSetting("Blocks", true);

   public NoPush() {
      this.handle(this.source, this.target, this.pending);
   }
}
