package ru.wild.command;

import java.util.ArrayList;
import java.util.List;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.core.Command;
import ru.wild.core.manager.FriendManager;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class CommandManager {
   private final List<Command> instance = new ArrayList<>();

   public CommandManager() {
      this.handle(new HelpCommand(this));
      this.handle(new FriendManager());
      this.handle(new PrefixCommand());
      this.handle(new ConfigCommand());
      this.handle(new CreeperFarmConfig());
      this.handle(new CocoaFarmConfig());
      this.handle(new ChorusFarmConfig());
      this.handle(new TelegramConfig());
      this.handle(new MacroCommand());
      this.handle(new GpsCommand());
      this.handle(new ModuleListCommand());
      this.handle(new AutoBuyCommand());
      this.handle(new AuctionPageCommand());
      this.handle(new AuctionSearchCommand());
      this.handle(new RctCommand());
      this.handle(new XrayCommand());
      this.handle(new RotationCommand());
      this.handle(new HeadlessBotCommand());
      this.handle(new PartyCommand());
   }

   @Compile
   public void handle(Command var1) {
      this.instance.add(var1);
      EventHandlerInvoker.handle(var1);
   }

   @Compile
   public void handle(String var1) {
      String var2 = WildClient.instance.fetchProvider();
      if (var1.startsWith(var2)) {
         String[] var3 = var1.substring(var2.length()).split(" ");
         String var4 = var3[0];
         if (var4.equalsIgnoreCase("ah.search")) {
            var4 = "ahsearch";
         }

         String[] var5 = new String[var3.length - 1];
         System.arraycopy(var3, 1, var5, 0, var5.length);

         for (Command var7 : this.instance) {
            if (var7 != null && var7.handle().equalsIgnoreCase(var4)) {
               var7.process(var5);
               return;
            }
         }

         ChatLogger.handle("§cНеизвестная команда. Напиши §7.help §cдля списка.");
      }
   }

   public List<Command> handle() {
      return this.instance;
   }

   static {
      Loader.initialize();
   }
}
