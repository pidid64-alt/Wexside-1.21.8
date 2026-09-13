package ru.wild.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.SuggestCommand;
import net.minecraft.text.HoverEvent.ShowText;
import ru.wild.WildClient;
import ru.wild.audio.AudioResourceManager;
import ru.wild.core.Command;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class FriendManager extends Command {
   private static final Gson instance = new GsonBuilder().setPrettyPrinting().create();
   private static final File data = new File(WildClient.instance.cache, "friend.cfg");
   private static final Map<String, FriendManager.State> context = new HashMap<>();
   private static final SimpleDateFormat config = new SimpleDateFormat("dd.MM.yyyy HH:mm");

   public FriendManager() {
      super("friend", "Управление друзьями", ".friend <add/remove/list/clear> <name>");
      this.handle("add", () -> toggleState.getNetworkHandler().getPlayerList().stream().map(var0 -> var0.getProfile().getName()).toList());
      this.handle("remove", () -> new ArrayList<>(context.keySet()));
      this.handle("list", List::of);
      this.handle("clear", List::of);
      this.execute();
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: " + this.compute());
      } else {
         switch (var1[0].toLowerCase()) {
            case "add":
               this.compute(var1);
               break;
            case "remove":
               this.resolve(var1);
               break;
            case "list":
               this.apply();
               break;
            case "clear":
               this.update();
               break;
            default:
               ChatLogger.handle("§cНеизвестная подкоманда.");
         }
      }
   }

   @Compile
   private void compute(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите ник игрока.");
      } else {
         process(var1[1]);
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажите ник игрока.");
      } else {
         String var2 = var1[1].toLowerCase();
         FriendManager.State var3 = context.remove(var2);
         if (var3 == null) {
            ChatLogger.handle("§eИгрок не найден в списке друзей.");
         } else {
            this.prepare();
            AudioResourceManager.handle("friendremove", 100.0F, false);
            ChatLogger.handle("§cУдалён из друзей: §f" + var2);
         }
      }
   }

   @Compile
   private void update() {
      context.clear();
      this.prepare();
      ChatLogger.handle("§cСписок друзей очищен.");
   }

   @Compile
   private void apply() {
      if (context.isEmpty()) {
         ChatLogger.handle("§7Список друзей пуст.");
      } else {
         String var1 = WildClient.instance.fetchProvider();
         ChatLogger.handle("§fТвои друзья (§7" + context.size() + "§f):");
         context.values()
            .stream()
            .sorted(Comparator.comparing(var0 -> var0.data))
            .forEach(
               var1x -> {
                  MutableText var2 = Text.literal(" §7- §f" + var1x.instance + " §8[добавлен: " + config.format(var1x.data) + "] ");
                  MutableText var3 = Text.literal("§c[Удалить]")
                     .styled(
                        var2x -> var2x.withClickEvent(new SuggestCommand(var1 + "friend remove " + var1x.instance))
                           .withHoverEvent(new ShowText(Text.literal("§cНажмите, чтобы удалить друга")))
                     );
                  toggleState.player.sendMessage(var2.append(var3), false);
               }
            );
      }
   }

   public static boolean handle(String var0) {
      return var0 == null ? false : context.containsKey(var0.toLowerCase());
   }

   public static List<String> resolve() {
      return context.values().stream().map(var0 -> var0.instance).toList();
   }

   public static void process(String var0) {
      String var1 = var0.toLowerCase();
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.getSession() != null && var0.equalsIgnoreCase(var2.getSession().getUsername())) {
         ChatLogger.handle("§cНельзя добавить самого себя.");
      } else {
         if (context.containsKey(var1)) {
            context.remove(var1);
            check();
            AudioResourceManager.handle("friendremove", 100.0F, false);
            ChatLogger.handle("§cУдалён из друзей: §f" + var0);
         } else {
            FriendManager.State var3 = new FriendManager.State(var0, new Date());
            context.put(var1, var3);
            check();
            AudioResourceManager.handle("friendadd", 100.0F, false);
            ChatLogger.handle("§aДобавлен в друзья: §f" + var0 + " §7(" + config.format(var3.data) + ")");
         }
      }
   }

   @Compile
   private void execute() {
      if (data.exists()) {
         try (FileReader var1 = new FileReader(data)) {
            Type var2 = (new TypeToken<Map<String, FriendManager.State>>() {}).getType();
            Map var3 = (Map)instance.fromJson(var1, var2);
            context.clear();
            if (var3 != null) {
               context.putAll(var3);
            }
         } catch (IOException var6) {
         }
      }
   }

   @Compile
   private void prepare() {
      check();
   }

   @Compile
   private static void check() {
      try {
         if (!data.getParentFile().exists()) {
            data.getParentFile().mkdirs();
         }

         try (FileWriter var0 = new FileWriter(data)) {
            instance.toJson(context, var0);
         }
      } catch (IOException var5) {
      }
   }

   static {
      Loader.initialize();
   }

   static class State {
      String instance;
      Date data;

      State(String var1, Date var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
