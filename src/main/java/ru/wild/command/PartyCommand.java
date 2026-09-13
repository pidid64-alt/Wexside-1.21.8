package ru.wild.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.wild.WildClient;
import ru.wild.core.Command;
import ru.wild.network.JsonRpcHandler;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;

public class PartyCommand extends Command {
   private static final Pattern config = Pattern.compile("^[A-Z0-9]{16}$");
   private static final Gson state = new GsonBuilder().setPrettyPrinting().create();
   private static final File cache;
   public static volatile String instance = "";
   public static volatile String data = "";
   public static volatile boolean context;
   private static volatile boolean output;

   public PartyCommand() {
      super("party", "Группа по коду (коды на 16 символов, лидер создаёт)", ".party <create|connect <код>|leave|list|kick <ник>>");
      this.handle("create", () -> List.of("создаёт группу, выдаёт код"));
      this.handle("connect", () -> List.of("КОД_16_СИМВОЛОВ"));
      this.handle("leave", () -> List.of("выйти (лидер удаляет всю группу)"));
      this.handle("list", () -> List.of("список участников"));
      this.handle("kick", () -> JsonRpcHandler.apply() != null ? JsonRpcHandler.apply() : List.of());
      onTick();
      JsonRpcHandler.context = PartyCommand::handle;
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: §f" + this.compute());
      } else {
         switch (var1[0].toLowerCase()) {
            case "create":
               this.apply();
               break;
            case "connect":
            case "join":
               this.compute(var1);
               break;
            case "leave":
               this.execute();
               break;
            case "list":
               this.prepare();
               break;
            case "kick":
               this.resolve(var1);
               break;
            default:
               ChatLogger.handle("§cНеизвестная подкоманда: §f" + var1[0]);
         }
      }
   }

   @Compile
   private void apply() {
      JsonRpcHandler var1 = JsonRpcHandler.instance;
      if (var1 != null && var1.isOpen()) {
         JsonRpcHandler.handle(var1, "create");
      } else {
         ChatLogger.handle("§c[Party] Сервер меток не подключён. Включи модуль Party.");
      }
   }

   @Compile
   private void compute(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажи код группы: §f.party connect <код>");
      } else {
         String var2 = var1[1].trim().toUpperCase();
         if (!config.matcher(var2).matches()) {
            ChatLogger.handle("§cКод должен состоять из 16 символов A-Z/0-9.");
         } else {
            JsonRpcHandler var3 = JsonRpcHandler.instance;
            if (var3 != null && var3.isOpen()) {
               JsonRpcHandler.handle(var3, "join", var2);
            } else {
               ChatLogger.handle("§c[Party] Сервер меток не подключён. Включи модуль Party.");
            }
         }
      }
   }

   @Compile
   private void execute() {
      if (instance.isEmpty()) {
         ChatLogger.handle("§eТы не в группе.");
      } else {
         JsonRpcHandler var1 = JsonRpcHandler.instance;
         if (var1 != null && var1.isOpen()) {
            JsonRpcHandler.handle(var1, "leave");
         } else {
            ChatLogger.handle("§c[Party] Сервер меток не подключён.");
         }
      }
   }

   @Compile
   private void prepare() {
      if (instance.isEmpty()) {
         ChatLogger.handle("§eТы не в группе.");
      } else {
         JsonRpcHandler var1 = JsonRpcHandler.instance;
         if (var1 != null && var1.isOpen()) {
            output = true;
            JsonRpcHandler.handle(var1, "list");
         } else {
            ChatLogger.handle("§c[Party] Сервер меток не подключён.");
         }
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cУкажи ник: §f.party kick <ник>");
      } else if (!context) {
         ChatLogger.handle("§cКикать может только создатель группы.");
      } else {
         JsonRpcHandler var2 = JsonRpcHandler.instance;
         if (var2 != null && var2.isOpen()) {
            String var3 = "kick";
            String var4 = var1[1];
            String[] var5 = new String[1];
            if (var4 != null) {
               var5[0] = var4;
            }

            JsonRpcHandler.handle(var2, var3, var5);
         } else {
            ChatLogger.handle("§c[Party] Сервер меток не подключён.");
         }
      }
   }

   private static void handle(JSONObject var0) {
      String var1 = var0.optString("op");
      switch (var1) {
         case "created":
            instance = var0.optString("code", "");
            data = var0.optString("owner", check());
            context = true;
            select();
            ChatLogger.handle("§aГруппа создана. Код группы: §f" + instance);
            ChatLogger.handle("§7Передай этот код друзьям: §f.party connect " + instance);
            break;
         case "joined":
            instance = var0.optString("code", "");
            data = var0.optString("owner", "");
            context = false;
            select();
            JsonRpcHandler.data.clear();
            ChatLogger.handle("§aТы в группе §f" + data + "§a. Код: §f" + instance);
            break;
         case "left":
            instance = "";
            data = "";
            context = false;
            JsonRpcHandler.update();
            select();
            JsonRpcHandler.data.clear();
            ChatLogger.handle("§eТы вышел из группы.");
            break;
         case "party_closed":
            instance = "";
            data = "";
            context = false;
            JsonRpcHandler.update();
            select();
            JsonRpcHandler.data.clear();
            ChatLogger.handle("§cГруппа закрыта владельцем.");
            break;
         case "kicked":
            instance = "";
            data = "";
            context = false;
            JsonRpcHandler.update();
            select();
            JsonRpcHandler.data.clear();
            ChatLogger.handle("§cТы был исключён из группы.");
            break;
         case "party_state":
            data = var0.optString("owner", "");
            context = data.equalsIgnoreCase(check());
            if (output) {
               output = false;
               handle(var0.optJSONArray("members"));
            }
            break;
         case "error":
            if (output) {
               output = false;
            }

            ChatLogger.handle("§c[Party] " + var0.optString("msg", "ошибка"));
      }
   }

   private static void handle(JSONArray var0) {
      if (var0 != null && var0.length() != 0) {
         ChatLogger.handle("§fУчастники группы §8(" + var0.length() + "§8):");

         for (int var1 = 0; var1 < var0.length(); var1++) {
            String var2 = var0.getString(var1);
            String var3 = var2.equalsIgnoreCase(data) ? " §a[глава]" : "";
            ChatLogger.handle(" §7- §f" + var2 + var3);
         }
      } else {
         ChatLogger.handle("§7В группе пока пусто.");
      }
   }

   private static String check() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 != null && var0.getSession() != null ? var0.getSession().getUsername() : "";
   }

   public static String resolve() {
      return instance;
   }

   public static List<String> update() {
      return JsonRpcHandler.apply();
   }

   @Compile
   private static void onTick() {
      if (cache.exists()) {
         try (FileReader var0 = new FileReader(cache)) {
            PartyCommand.State var1 = (PartyCommand.State)state.fromJson(var0, PartyCommand.State.class);
            if (var1 != null) {
               String var2 = var1.instance != null ? var1.instance : "";
               Field var3 = PartyCommand.class.getDeclaredField("UuUVuuUu");
               var3.setAccessible(true);
               var3.set(null, var2);
               String var4 = var1.data != null ? var1.data : "";
               Field var5 = PartyCommand.class.getDeclaredField("C00OOC00oO");
               var5.setAccessible(true);
               var5.set(null, var4);
               context = var1.data != null && var1.data.equalsIgnoreCase(check());
            }
         } catch (Exception var8) {
         }
      }
   }

   @Compile
   private static void select() {
      try {
         if (!cache.getParentFile().exists()) {
            cache.getParentFile().mkdirs();
         }

         try (FileWriter var0 = new FileWriter(cache)) {
            PartyCommand.State var1 = new PartyCommand.State();
            var1.instance = instance;
            var1.data = data;
            state.toJson(var1, var0);
         }
      } catch (Exception var5) {
      }
   }

   static {
      Loader.initialize();
      cache = new File(WildClient.instance.cache, "party.cfg");
   }

   static class State {
      String instance;
      String data;
   }
}
