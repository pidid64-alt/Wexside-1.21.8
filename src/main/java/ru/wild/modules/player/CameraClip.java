package ru.wild.modules.player;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.CameraClipEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.module.ModuleCategory;

@ModuleRegister(name = "CameraClip", description = "Камера проходит сквозь блоки", category = ModuleCategory.Player)
public class CameraClip extends Module {
   @EventHandler
   public void handle(CameraClipEvent var1) {
      var1.process();
   }
}
