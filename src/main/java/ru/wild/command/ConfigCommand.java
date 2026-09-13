package ru.wild.command;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import ru.wild.WildClient;
import ru.wild.config.ConfigManager;
import ru.wild.config.ConfigProfile;
import ru.wild.core.Command;
import ru.wild.gui.theme.AccentColorToken;
import ru.wild.network.HttpDownloader;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class ConfigCommand extends Command {
   private final File instance;

   public ConfigCommand() {
      super("config", "Управление конфигурациями", ".config <save/load/cloudload/cloudlist/list/delete/dir/reset> <name>");
      this.instance = ConfigManager.instance;
      this.handle("load", this::resolve);
      this.handle("delete", this::resolve);
      this.handle("dir", List::of);
      this.handle("reset", List::of);
      this.handle("save", this::resolve);
      this.handle("list", List::of);
      this.handle("cloudload", HttpDownloader::process);
      this.handle("cloudlist", List::of);
   }

   private List<String> resolve() {
      if (!this.instance.exists()) {
         return List.of();
      }

      File[] var1 = this.instance.listFiles((var0, var1x) -> var1x.endsWith(".cfg") || var1x.endsWith(".json"));
      return var1 == null ? List.of() : Arrays.stream(var1).map(var0 -> {
         String var1x = var0.getName();
         return var1x.substring(0, var1x.lastIndexOf(46));
      }).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (WildClient.instance.renderer == null) {
         ChatLogger.handle("§cСистема конфигураций еще не инициализирована.");
      } else if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: " + this.handle());
      } else {
         switch (var1[0].toLowerCase()) {
            case "save":
               this.compute(var1);
               break;
            case "load":
               this.resolve(var1);
               break;
            case "cloudload":
               this.update(var1);
               break;
            case "cloudlist":
               this.update();
               break;
            case "delete":
               this.apply(var1);
               break;
            case "list":
               this.execute();
               break;
            case "dir":
               this.apply();
               break;
            case "reset":
               this.prepare();
               break;
            default:
               ChatLogger.handle("§cНеизвестная подкоманда. Используйте: save, load, cloudload, cloudlist, delete, list, dir, reset");
         }
      }
   }

   @Compile
   private void compute(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите название конфига.");
      } else {
         String var2 = var1[1];
         if (WildClient.instance.renderer.process(var2)) {
            ChatLogger.handle("§aКонфиг §f'" + var2 + "' §aуспешно сохранен.");
         } else {
            ChatLogger.handle("§cНе удалось сохранить конфиг §f'" + var2 + "'§c.");
         }
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите название конфига для загрузки.");
      } else {
         String var2 = var1[1];
         ConfigManager var3 = WildClient.instance.renderer;
         if (var3 != null && var3.handle(var2)) {
            ChatLogger.handle("§aКонфиг §f'" + var2 + "' §aуспешно загружен.");
         } else {
            this.handle(var2, false);
         }
      }
   }

   @Compile
   private void update(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите название Cloud Config для загрузки.");
      } else {
         this.handle(var1[1], true);
      }
   }

   private void handle(String var1, boolean var2) {
      if (var2) {
         ChatLogger.handle("§7Принудительно загружаю Cloud Config §f'" + var1 + "'§7...");
      } else {
         ChatLogger.handle("§7Локальный конфиг §f'" + var1 + "' §7не найден. Запрашиваю облако...");
      }

      HttpDownloader.handle(var1).whenComplete((var2x, var3) -> this.handle(() -> {
         if (var3 != null) {
            ChatLogger.handle("§cОшибка Cloud Config: §7" + this.handle(var3));
         } else {
            if (var2x != null && var2x.success()) {
               ChatLogger.handle("§aCloud Config §f'" + var2x.name() + "' §aзагружен и сохранен.");
            } else {
               String var4 = var2x != null && var2x.error() != null ? var2x.error() : "неизвестная ошибка";
               ChatLogger.handle("§cCloud Config §f'" + var1 + "' §cне загружен: §7" + var4);
            }
         }
      }));
   }

   @Compile
   private void update() {
      ChatLogger.handle("§7Запрашиваю список Cloud Configs...");
      HttpDownloader.handle().whenComplete((var1, var2) -> this.handle(() -> {
         if (var2 != null) {
            ChatLogger.handle("§cОшибка Cloud Config index: §7" + this.handle(var2));
         } else if (var1 != null && var1.success()) {
            if (var1.names().isEmpty()) {
               ChatLogger.handle("§7Cloud Config index пуст.");
            } else {
               this.handle("Cloud Configs", var1.names());
            }
         } else {
            String var3 = var1 != null && var1.error() != null ? var1.error() : "неизвестная ошибка";
            ChatLogger.handle("§cНе удалось загрузить cloudlist: §7" + var3);
         }
      }));
   }

   @Compile
   private void apply(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите название конфига для удаления.");
      } else {
         String var2 = var1[1];
         if (WildClient.instance.renderer.resolve(var2)) {
            ChatLogger.handle("§aКонфиг §f'" + var2 + "' §aудален.");
         } else {
            ChatLogger.handle("§cКонфиг §f'" + var2 + "' §cне найден.");
         }
      }
   }

   @Compile
   private void apply() {
      try {
         if (!this.instance.exists()) {
            this.instance.mkdirs();
         }

         String var1 = System.getProperty("os.name").toLowerCase();
         if (var1.contains("win")) {
            Runtime.getRuntime().exec(new String[]{"explorer", this.instance.getAbsolutePath()});
         } else if (var1.contains("mac")) {
            Runtime.getRuntime().exec(new String[]{"open", this.instance.getAbsolutePath()});
         } else {
            Runtime.getRuntime().exec(new String[]{"xdg-open", this.instance.getAbsolutePath()});
         }

         ChatLogger.handle("§aПапка с конфигами открыта!");
         ChatLogger.handle("§7Путь: §f" + this.instance.getAbsolutePath());
      } catch (Exception var2) {
         ChatLogger.handle("§cНе удалось открыть папку с конфигами.");
      }
   }

   @Compile
   private void execute() {
      List<ConfigProfile> var1 = WildClient.instance.renderer.apply();
      if (var1.isEmpty()) {
         ChatLogger.handle("§7Нет доступных конфигов.");
      } else {
         this.handle("Доступные конфиги", var1.stream().map(profile -> profile.process()).toList());
      }
   }

   @Compile
   private void prepare() {
      if (WildClient.instance.renderer.update()) {
         ChatLogger.handle("§aКонфиг сброшен: модули выключены, бинды и настройки возвращены к значениям по умолчанию.");
      } else {
         ChatLogger.handle("§cНе удалось сбросить конфиг.");
      }
   }

   private void handle(String var1, List<String> var2) {
      MutableText var3 = Text.literal("§f" + var1 + ": ");
      int var4 = AccentColorToken.handle();

      for (int var5 = 0; var5 < var2.size(); var5++) {
         MutableText var6 = Text.literal((String)var2.get(var5)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(var4)));
         var3.append(var6);
         if (var5 < var2.size() - 1) {
            var3.append(Text.literal("§7 | "));
         }
      }

      if (toggleState.player != null) {
         toggleState.player.sendMessage(var3, false);
      } else {
         ChatLogger.handle(var1 + ": " + String.join(", ", var2));
      }
   }

   private void handle(Runnable var1) {
      if (toggleState == null) {
         var1.run();
      } else {
         toggleState.execute(var1);
      }
   }

   private String handle(Throwable var1) {
      Throwable var2 = var1;

      while (var2 instanceof CompletionException && var2.getCause() != null) {
         var2 = var2.getCause();
      }

      String var3 = var2 == null ? null : var2.getMessage();
      return var3 != null && !var3.isBlank() ? var3 : "неизвестная ошибка";
   }

   static {
      Loader.initialize();
   }
}
