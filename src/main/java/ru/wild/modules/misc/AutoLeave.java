package ru.wild.modules.misc;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.network.TelegramApi;
import ru.wild.util.math.ResettableTimer;

@ModuleRegister(name = "AutoLeave", category = ModuleCategory.Misc, description = "Автоматический выход")
public class AutoLeave extends Module {
   public final ModeSetting source = new ModeSetting("Режим работы", "Хаб", "Хаб", "Меню");
   public final ModeSetting target = new ModeSetting("Триггеры", "Игрок рядом", "Игрок рядом", "ХП");
   public final NumberSetting pending = new NumberSetting("Радиус игрока", 30.0F, 10.0F, 100.0F, 1.0F, false).handle(() -> !this.target.process("Игрок рядом"));
   public final NumberSetting previous = new NumberSetting("Порог здоровья", 10.0F, 1.0F, 20.0F, 1.0F, false).handle(() -> !this.target.process("ХП"));
   private final ResettableTimer latest = new ResettableTimer();

   public AutoLeave() {
      this.handle(this.source, this.target, this.pending, this.previous);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         if (this.latest.process(100.0)) {
            boolean var2 = false;
            String var3 = "";
            if (this.target.process("ХП")) {
               float var4 = Module.client.player.getHealth() + Module.client.player.getAbsorptionAmount();
               if (var4 <= this.previous.compute()) {
                  var2 = true;
                  var3 = "Мало здоровья (" + (int)var4 + " HP)";
               }
            } else if (this.target.process("Игрок рядом")) {
               for (AbstractClientPlayerEntity var5 : Module.client.world.getPlayers()) {
                  if (var5 != Module.client.player && !ru.wild.core.manager.FriendManager.handle(var5.getName().getString())) {
                     double var6 = Module.client.player.distanceTo(var5);
                     if (var6 <= this.pending.compute()) {
                        var2 = true;
                        var3 = var5.getName().getString();
                        break;
                     }
                  }
               }
            }

            if (var2) {
               this.handle(var3);
               this.latest.handle();
               this.toggle();
            }
         }
      }
   }

   private void handle(String var1) {
      if (this.source.process("Хаб")) {
         if (Module.client.player.networkHandler != null) {
            Module.client.player.networkHandler.sendChatMessage("/hub");
            if (ClientUtil.source.compute()) {
               TelegramApi.handle("[AutoLeave] Был замечен игрок, его ник - " + var1);
            }
         }
      } else if (this.source.process("Меню") && Module.client.getNetworkHandler() != null && Module.client.getNetworkHandler().getConnection() != null) {
         String var2 = var1;
         if (this.target.process("Игрок рядом")) {
            var2 = "Был замечен игрок, его ник - " + var1;
         }

         Module.client.getNetworkHandler().getConnection().disconnect(Text.of(var2));
      }
   }
}
