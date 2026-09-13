package ru.wild.modules.movement;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;
import ru.wild.util.player.MovementPhysics;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "DragonFly", category = ModuleCategory.Movement, description = "Ускоряет вас в воздухе")
public class DragonFly extends Module {
   public final NumberSetting source = new NumberSetting("Скорость по X", 1.0F, 1.0F, 100.0F, 1.0F, false);
   public final NumberSetting target = new NumberSetting("Скорость по Y", 1.0F, 1.0F, 100.0F, 1.0F, false);

   public DragonFly() {
      this.handle(this.source, this.target);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (Module.client.player.getAbilities().flying) {
            double var2 = this.source.compute() / 10.0;
            double var4 = this.target.compute() / 10.0;
            double var6;
            if (Module.client.options.jumpKey.isPressed()) {
               var6 = var4;
            } else if (Module.client.options.sneakKey.isPressed()) {
               var6 = -var4;
            } else {
               var6 = 0.0;
            }

            if (MovementPhysics.handle()) {
               double[] var8 = MovementPhysics.handle(var2);
               Module.client.player.setVelocity(var8[0], var6, var8[1]);
            } else {
               Module.client.player.setVelocity(0.0, var6, 0.0);
            }
         }
      }
   }
}
