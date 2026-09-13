package ru.wild.modules.misc;

import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.util.player.NicknameUtil;

@ModuleRegister(name = "ChatHelper", description = "Обновляет параметры чата", category = ModuleCategory.Misc)
public class ChatHelper extends Module {
   public static final BooleanSetting source = new BooleanSetting("Антиспам чат", true);
   public static final BooleanSetting target = new BooleanSetting("Сохранять чат", true);
   public static final BooleanSetting pending = new BooleanSetting("Улучшенные команды", true);
   public static final BooleanSetting previous = new BooleanSetting("Расширенный просмотр чата ", true);

   public ChatHelper() {
      this.handle(source, target, pending, previous);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null) {
         if (var1.resolve() instanceof CommandExecutionC2SPacket var2) {
            String var10 = null;
            String var4 = var2.command();
            String var5 = var4.toLowerCase();
            int var6 = var5.indexOf("ah");
            int var7 = var5.indexOf(" me", var6);
            if (var7 != -1 && var6 != -1) {
               String var8 = Module.client.player.getName().getString();
               var10 = var4.substring(0, var7) + " " + var8 + var4.substring(var7 + 3);
            }

            if (var5.startsWith("clan")) {
               NicknameUtil var11 = new NicknameUtil();
               var11.handle();
               String var9 = var11.apply();
               if (var5.endsWith(" all") || var5.contains(" all")) {
                  var10 = var4.replaceAll("(?i)\\ball\\b", var9);
               }
            }

            if (var10 != null) {
               var1.process();
               Module.client.player.networkHandler.sendPacket(new CommandExecutionC2SPacket(var10));
            }
         }
      }
   }
}
