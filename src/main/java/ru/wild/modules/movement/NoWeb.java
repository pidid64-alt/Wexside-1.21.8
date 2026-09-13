package ru.wild.modules.movement;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.util.math.ClientMathUtil;
import ru.wild.util.player.LocalhostHelper;
import ru.wild.util.player.MovementPhysics;

@ModuleRegister(name = "NoWeb", description = "Убирает замедление в паутине", category = ModuleCategory.Movement)
public class NoWeb extends Module {
   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!LocalhostHelper.handle() && LocalhostHelper.process()) {
         double[] var2 = MovementPhysics.handle((double)ClientMathUtil.compute(0.62F, 0.64F));
         Module.client.player.setVelocity(var2[0], Module.client.options.jumpKey.isPressed() ? 1.2 : (Module.client.options.sneakKey.isPressed() ? -2.0 : 0.0), var2[1]);
      }
   }
}
