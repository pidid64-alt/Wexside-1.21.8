package ru.wild.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.network.IrcClient;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class IrcCommand extends Command {
   public IrcCommand() {
      super("irc", "Отправка сообщения в глобальный IRC чат", ".irc <сообщение>");
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("§cНеверный формат. Используйте: .irc <сообщение>");
      } else {
         StringBuilder var2 = new StringBuilder();

         for (String var6 : var1) {
            var2.append(var6).append(" ");
         }

         String var12 = var2.toString().trim();
         WildClient var13 = WildClient.instance;
         IrcClient var14 = var13 == null ? null : var13.computeResponse();
         if (var14 == null) {
            ChatLogger.handle("§c[IRC] Вы не подключены к серверу IRC. Сообщение не отправлено.");
         } else {
            WildClient var15 = WildClient.instance;
            IrcClient var7 = var15 == null ? null : var15.computeResponse();
            if (var7 != null && var7.isOpen()) {
               String var8 = null;
               MinecraftClient var9 = toggleState;
               if (var9 != null) {
                  Session var10 = var9.getSession();
                  if (var10 != null) {
                     var8 = var10.getUsername();
                  }
               }

               WildClient var16 = WildClient.instance;
               IrcClient var11 = var16 == null ? null : var16.computeResponse();
               if (var11 != null) {
                  var11.handle(var8, var12);
               }
            } else {
               ChatLogger.handle("§c[IRC] Вы не подключены к серверу IRC. Сообщение не отправлено.");
            }
         }
      }
   }

   static {
      Loader.initialize();
   }
}
