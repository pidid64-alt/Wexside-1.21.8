package ru.wild.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import ru.wild.WildClient;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.core.Command;
import ru.wild.core.manager.FeatureManager;
import ru.wild.modules.misc.AhHelper;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public final class AuctionSearchCommand extends Command {
   public AuctionSearchCommand() {
      super("ahsearch", "Поиск лотов по названию и максимальной цене", ".ahsearch <название> <максимальная цена>");
   }

   @Compile
   @Override
   public void process(String[] var1) {
      ArrayList var2 = new ArrayList();

      for (String var6 : var1) {
         if (var6 != null && !var6.isBlank()) {
            var2.add(var6.trim());
         }
      }

      if (var2.size() == 1 && this.handle((String)var2.getFirst())) {
         AhHelper.refresh();
         ChatLogger.handle("§7[AhHelper] Фильтр поиска очищен.");
      } else {
         int var8 = this.handle(var2);
         if (var8 <= 0) {
            this.resolve();
         } else {
            Long var9 = this.process(var2.subList(var8, var2.size()));
            if (var9 != null && var9 > 0L) {
               String var10 = String.join(" ", var2.subList(0, var8)).trim();
               if (var10.isEmpty()) {
                  this.resolve();
               } else {
                  AhHelper var11 = null;
                  if (WildClient.instance != null && WildClient.instance.data != null) {
                     FeatureManager var7 = WildClient.instance.data;
                     if (var7 != null) {
                        var11 = var7.handle(AhHelper.class);
                     }
                  }

                  if (var11 != null && !var11.enabled) {
                     var11.setEnabled(true);
                  }

                  AhHelper.handle(var10, var9);
                  if (toggleState.player != null && toggleState.player.networkHandler != null) {
                     ClientPlayNetworkHandler var12 = toggleState.player.networkHandler;
                     if (var12 != null) {
                        var12.sendChatCommand("ah search " + var10);
                     }
                  }

                  BooleanSetting var13 = AhHelper.pending;
                  ChatLogger.handle(
                     "§7[AhHelper] Фильтр "
                        + (var13 != null && var13.compute() ? "§aвключен" : "§eзадан, чекбокс выключен")
                        + "§7: §f"
                        + var10
                        + " §7до §f"
                        + this.handle(var9)
                        + "$"
                  );
               }
            } else {
               ChatLogger.handle("§c[AhHelper] Максимальная цена должна быть положительным числом.");
            }
         }
      }
   }

   @Override
   public List<String> handle(String[] var1) {
      String var2 = var1.length == 0 ? "" : var1[var1.length - 1].toLowerCase(Locale.ROOT);
      return List.of("clear").stream().filter(var1x -> var1x.startsWith(var2)).toList();
   }

   private int handle(List<String> var1) {
      if (var1.size() >= 2 && this.handle((String)var1.getLast())) {
         int var2 = var1.size() - 1;

         while (var2 > 1 && this.process((String)var1.get(var2)) == 3 && this.handle((String)var1.get(var2 - 1))) {
            var2--;
         }

         return var2;
      } else {
         return -1;
      }
   }

   private Long process(List<String> var1) {
      StringBuilder var2 = new StringBuilder();

      for (String var4 : var1) {
         var2.append(var4.replaceAll("[^0-9]", ""));
      }

      if (var2.isEmpty()) {
         return null;
      }

      try {
         return Long.parseLong(var2.toString());
      } catch (NumberFormatException var5) {
         return null;
      }
   }

   private boolean handle(String var1) {
      return var1 != null && var1.matches("[0-9][0-9_.,]*");
   }

   private int process(String var1) {
      return var1.replaceAll("[^0-9]", "").length();
   }

   private boolean compute(String var1) {
      return var1.equalsIgnoreCase("clear") || var1.equalsIgnoreCase("off") || var1.equalsIgnoreCase("reset");
   }

   private String handle(long var1) {
      return String.format(Locale.ROOT, "%,d", var1).replace(',', ' ');
   }

   private void resolve() {
      ChatLogger.handle("§cИспользование: " + this.compute());
      ChatLogger.handle("§7Пример: §f.ahsearch зачарованное золотое яблоко 100 000");
      ChatLogger.handle("§7Сброс: §f.ahsearch clear");
   }

   static {
      Loader.initialize();
   }
}
