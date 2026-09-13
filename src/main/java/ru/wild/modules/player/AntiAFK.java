package ru.wild.modules.player;

import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;

@ModuleRegister(name = "AntiAFK", category = ModuleCategory.Player, description = "Убирает вылет при входе в режим AFK")
public class AntiAFK extends Module {
   public static BooleanSetting source = new BooleanSetting("Кружится", false);
   public static BooleanSetting target = new BooleanSetting("Прыгать", true);
   public static BooleanSetting pending = new BooleanSetting("Отправлять сообщения", true);

   public AntiAFK() {
      this.handle(pending, target, source);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player.getHealth() > 0.0F) {
         if (source.compute() && Module.client.player.age % 60 == 0) {
            Module.client.player.setYaw(Module.client.player.getYaw() + 300.0F);
         }

         if (target.compute() && Module.client.player.age % 40 == 0 && !Module.client.options.jumpKey.isPressed() && Module.client.player.isOnGround()) {
            Module.client.player.jump();
         }

         if (pending.compute() && Module.client.player.age % 400 == 0) {
            Module.client.player.networkHandler.sendChatCommand("ak1");
         }
      }
   }
}
