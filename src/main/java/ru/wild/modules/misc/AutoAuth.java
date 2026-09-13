package ru.wild.modules.misc;

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.StringSetting;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "AutoAuth", description = "Авто регистр/логин на серверах", category = ModuleCategory.Misc)
public class AutoAuth extends Module {
   public final StringSetting source = new StringSetting("Пишите сюда ваш пороль", "");

   public AutoAuth() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (!ServerEnvironment.handle() && Module.client.world != null) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var5 = var2.content().getString();
            String var4 = this.source.compute();
            if ((var5.contains("Войдите") || var5.contains("/login")) && Module.client.player.networkHandler != null) {
               Module.client.player.networkHandler.sendChatCommand("login " + var4);
            }

            if ((var5.contains("Зарегистрируйтесь") || var5.contains("/reg")) && var4 != null && var4.length() >= 4 && Module.client.player.networkHandler != null
               )
             {
               Module.client.player.networkHandler.sendChatCommand("reg " + var4 + " " + var4);
            }
         }
      }
   }
}
