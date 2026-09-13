package ru.wild.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import org.lwjgl.glfw.GLFW;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.core.Command;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.text.KeybindPresets;
import ru.wild.util.text.KeycodeNames;

public class MacroCommand extends Command {
   private static final List<String> data = List.of("add", "remove", "list", "run", "clear", "save", "load");
   private static final List<String> context = List.of(
      "A",
      "B",
      "C",
      "D",
      "E",
      "F",
      "G",
      "H",
      "I",
      "J",
      "K",
      "L",
      "M",
      "N",
      "O",
      "P",
      "Q",
      "R",
      "S",
      "T",
      "U",
      "V",
      "W",
      "X",
      "Y",
      "Z",
      "0",
      "1",
      "2",
      "3",
      "4",
      "5",
      "6",
      "7",
      "8",
      "9",
      "F1",
      "F2",
      "F3",
      "F4",
      "F5",
      "F6",
      "F7",
      "F8",
      "F9",
      "F10",
      "F11",
      "F12",
      "SPACE",
      "ENTER",
      "TAB",
      "INSERT",
      "DELETE",
      "HOME",
      "END",
      "PAGEUP",
      "PAGEDOWN",
      "LEFT",
      "RIGHT",
      "UP",
      "DOWN",
      "LSHIFT",
      "RSHIFT",
      "LCONTROL",
      "RCONTROL",
      "LALT",
      "RALT",
      "MOUSE3",
      "MOUSE4",
      "MOUSE5"
   );
   private static final String config = "\\|";
   private static final int state = -100;
   private static final char cache = '�';
   private final Gson output = new GsonBuilder().setPrettyPrinting().create();
   private final File current = new File(WildClient.instance.cache, "macros.cfg");
   public static final List<MacroCommand.State> instance = new CopyOnWriteArrayList<>();

   public MacroCommand() {
      super("macro", "Управление макросами", ".macro add <имя> <кнопка> <текст> | .macro list");
      this.handle(true);
   }

   @Override
   public List<String> handle(String[] var1) {
      if (var1.length == 2) {
         String var5 = var1[1].toLowerCase(Locale.ROOT);
         return data.stream().filter(var1x -> var1x.startsWith(var5)).toList();
      } else if (var1.length == 3) {
         String var4 = var1[1].toLowerCase(Locale.ROOT);
         String var3 = var1[2].toLowerCase(Locale.ROOT);
         return !this.compute(var4) && !var4.equals("run") && !var4.equals("exec")
            ? List.of()
            : this.prepare().stream().filter(var1x -> var1x.toLowerCase(Locale.ROOT).startsWith(var3)).toList();
      } else if (var1.length == 4 && this.process(var1[1].toLowerCase(Locale.ROOT))) {
         String var2 = var1[3].toLowerCase(Locale.ROOT);
         return context.stream().filter(var1x -> var1x.toLowerCase(Locale.ROOT).startsWith(var2)).toList();
      } else {
         return List.of();
      }
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (var1.length == 0) {
         this.execute();
      } else {
         String var2 = var1[0].toLowerCase(Locale.ROOT);
         if (this.process(var2)) {
            this.compute(var1);
         } else if (this.compute(var2)) {
            this.resolve(var1);
         } else {
            switch (var2) {
               case "run":
               case "exec":
                  this.update(var1);
                  break;
               case "list":
               case "ls":
                  this.resolve();
                  break;
               case "clear":
                  this.update();
                  break;
               case "save":
                  this.apply();
                  ChatLogger.handle("§aСохранено макросов: §f" + instance.size());
                  break;
               case "load":
               case "reload":
                  this.handle(false);
                  break;
               case "help":
               case "?":
                  this.execute();
                  break;
               default:
                  ChatLogger.handle("§cНеизвестная подкоманда: §f" + var1[0]);
                  this.execute();
            }
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (var1.apply() == 1) {
         this.handle(var1.resolve());
      }
   }

   @Compile
   private void handle(int var1) {
      if (!instance.isEmpty() && this.process(var1)) {
         if (toggleState.player != null && toggleState.player.networkHandler != null && toggleState.currentScreen == null) {
            for (MacroCommand.State var3 : instance) {
               if (var3 != null && var3.context == var1) {
                  this.handle(var3);
               }
            }
         }
      }
   }

   @Compile
   private void handle(MacroCommand.State var1) {
      if (var1 != null && var1.data != null) {
         if (toggleState.player != null && toggleState.player.networkHandler != null) {
            if (WildClient.instance != null && WildClient.instance.fetchProvider() != null) {
               for (String var5 : var1.data.split("\\|")) {
                  String var6 = var5.trim();
                  if (!var6.isEmpty()) {
                     if (var6.startsWith("/")) {
                        String var7 = var6.substring(1).trim();
                        if (!var7.isEmpty()) {
                           toggleState.player.networkHandler.sendChatCommand(var7);
                        }
                     } else if (var6.isEmpty() || !var6.startsWith(".")) {
                        toggleState.player.networkHandler.sendChatMessage(var6);
                     } else if (WildClient.instance != null && WildClient.instance.projectItem() != null) {
                        WildClient.instance.projectItem().handle(var6);
                     }
                  }
               }
            }
         }
      }
   }

   @Compile
   private void compute(String[] var1) {
      if (var1.length < 4) {
         ChatLogger.handle("§cИспользование: §f.macro add <имя> <кнопка> <текст>");
         ChatLogger.handle("§7Пример: §f.macro add spawn G /spawn");
      } else {
         String var2 = var1[1];
         Integer var3 = this.resolve(var1[2]);
         byte var4;
         int var5;
         if (var3 != null) {
            var4 = 3;
            var5 = var1.length;
         } else {
            var3 = this.resolve(var1[var1.length - 1]);
            if (var3 == null) {
               ChatLogger.handle("§cНеизвестная кнопка: §f" + var1[2] + " §7(и §f" + var1[var1.length - 1] + "§7)");
               ChatLogger.handle("§7Формат: §f.macro add <имя> <кнопка> <текст>");
               return;
            }

            var4 = 2;
            var5 = var1.length - 1;
         }

         String var6 = this.handle(var1, var4, var5);
         if (var6.isEmpty()) {
            ChatLogger.handle("§cПустой текст макроса.");
         } else {
            MacroCommand.State var7 = this.handle(var2);
            boolean var8 = var7 != null;
            if (var8) {
               instance.remove(var7);
            }

            MacroCommand.State var9 = new MacroCommand.State(var2, var3, var6);
            instance.add(var9);
            this.apply();
            String var10 = KeybindPresets.handle().handle(var3);
            if (var8) {
               ChatLogger.handle("§eМакрос §f" + var2 + " §eперезаписан: §b" + var6 + " §8[§e" + var10 + "§8]");
            } else {
               ChatLogger.handle("§aМакрос §f" + var2 + " §aна §e" + var10 + "§a: §b" + var6);
            }

            long var11 = 0L;

            for (MacroCommand.State var14 : instance) {
               if (var14.context == var9.context) {
                  var11++;
               }
            }

            if (var11 > 1L) {
               ChatLogger.handle("§7На этой кнопке уже §f" + var11 + " §7макроса, сработают все.");
            }
         }
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cИспользование: §f.macro remove <имя>");
      } else {
         MacroCommand.State var2 = this.handle(var1[1]);
         if (var2 == null) {
            ChatLogger.handle("§cМакрос не найден: §f" + var1[1]);
         } else {
            instance.remove(var2);
            this.apply();
            ChatLogger.handle("§aМакрос §f" + var2.instance + " §aудалён.");
         }
      }
   }

   @Compile
   private void update(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cИспользование: §f.macro run <имя>");
      } else {
         MacroCommand.State var2 = this.handle(var1[1]);
         if (var2 == null) {
            ChatLogger.handle("§cМакрос не найден: §f" + var1[1]);
         } else if (toggleState.player != null && toggleState.player.networkHandler != null) {
            this.handle(var2);
         } else {
            ChatLogger.handle("§cНужно быть на сервере.");
         }
      }
   }

   @Compile
   private void resolve() {
      if (instance.isEmpty()) {
         ChatLogger.handle("§7Список макросов пуст. §f.macro add <имя> <кнопка> <текст>");
      } else {
         ChatLogger.handle("§fМакросы (§7" + instance.size() + "§f):");

         for (MacroCommand.State var2 : instance) {
            String var3 = KeybindPresets.handle().handle(var2.context);
            ChatLogger.handle("§7- §f" + var2.instance + " §8[§e" + var3 + "§8] §7» §b" + var2.data);
         }
      }
   }

   @Compile
   private void update() {
      int var1 = instance.size();
      instance.clear();
      this.apply();
      ChatLogger.handle("§cУдалено макросов: §f" + var1);
   }

   @Compile
   public void handle(boolean var1) {
      if (!this.current.exists()) {
         if (!var1) {
            ChatLogger.handle("§7Файл макросов не найден, список пуст.");
         }
      } else {
         try {
            List<MacroCommand.State> var2 = this.handle(StandardCharsets.UTF_8);
            if (var2 == null) {
               var2 = this.handle(Charset.defaultCharset());
            }

            if (var2 == null) {
               return;
            }

            ArrayList<MacroCommand.State> var3 = new ArrayList<>();

            for (MacroCommand.State var5 : var2) {
               if (var5 != null && var5.instance != null && !var5.instance.isBlank() && var5.data != null && !var5.data.isBlank() && this.process(var5.context)
                  )
                {
                  var3.add(var5);
               }
            }

            instance.clear();
            instance.addAll(var3);
            if (!var1) {
               ChatLogger.handle("§aЗагружено макросов: §f" + instance.size());
            }
         } catch (Exception var6) {
            ChatLogger.handle("§cНе удалось прочитать macros.cfg.");
         }
      }
   }

   @Compile
   private List<MacroCommand.State> handle(Charset var1) {
      byte[] var2;
      try {
         var2 = Files.readAllBytes(this.current.toPath());
      } catch (IOException exception) {
         return null;
      }
      String var3 = new String(var2, var1);
      if (StandardCharsets.UTF_8.equals(var1) && var3.indexOf(65533) >= 0) {
         return null;
      }

      Type var4 = (new TypeToken<List<MacroCommand.State>>() {}).getType();
      return (List<MacroCommand.State>)this.output.fromJson(var3, var4);
   }

   @Compile
   private void apply() {
      try {
         File var1 = this.current.getParentFile();
         if (var1 != null && !var1.exists()) {
            var1.mkdirs();
         }

         try (OutputStreamWriter var2 = new OutputStreamWriter(new FileOutputStream(this.current), StandardCharsets.UTF_8)) {
            this.output.toJson(new ArrayList<>(instance), var2);
         }
      } catch (Exception var7) {
         ChatLogger.handle("§cНе удалось сохранить макросы: §f" + var7.getMessage());
      }
   }

   private void execute() {
      ChatLogger.handle("§f.macro add <имя> <кнопка> <текст> §7- создать или перезаписать");
      ChatLogger.handle("§f.macro remove <имя> §7| §f.macro list §7| §f.macro run <имя> §7| §f.macro clear §7| §f.macro load");
      ChatLogger.handle(
         "§7Текст с §f/ §7уходит командой на сервер, с §f"
            + (WildClient.instance == null ? "." : WildClient.instance.fetchProvider())
            + " §7- клиентской командой."
      );
      ChatLogger.handle("§7Несколько действий: §f.macro add kit G /kit tools | /home base");
   }

   private MacroCommand.State handle(String var1) {
      if (var1 == null) {
         return null;
      }

      for (MacroCommand.State var3 : instance) {
         if (var3 != null && var3.instance != null && var3.instance.equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return null;
   }

   private List<String> prepare() {
      return instance.stream().filter(var0 -> var0 != null && var0.instance != null).map(var0 -> var0.instance).toList();
   }

   private String handle(String[] var1, int var2, int var3) {
      StringBuilder var4 = new StringBuilder();

      for (int var5 = var2; var5 < var3; var5++) {
         if (var4.length() > 0) {
            var4.append(' ');
         }

         var4.append(var1[var5]);
      }

      return var4.toString().trim();
   }

   private boolean process(String var1) {
      return var1.equals("add") || var1.equals("set") || var1.equals("create");
   }

   private boolean compute(String var1) {
      return var1.equals("remove") || var1.equals("del") || var1.equals("delete") || var1.equals("rem");
   }

   private boolean process(int var1) {
      return var1 > 0 || var1 <= -100;
   }

   private Integer resolve(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
         if (var2.startsWith("GLFW_KEY_")) {
            var2 = var2.substring("GLFW_KEY_".length());
         } else if (var2.startsWith("KEY_")) {
            var2 = var2.substring("KEY_".length());
         }

         if (var2.equals("LMB") || var2.equals("MOUSELEFT") || var2.equals("MOUSE_LEFT")) {
            return -100;
         }

         if (var2.equals("RMB") || var2.equals("MOUSERIGHT") || var2.equals("MOUSE_RIGHT")) {
            return -101;
         }

         if (!var2.equals("MMB") && !var2.equals("MOUSEMIDDLE") && !var2.equals("MOUSE_MIDDLE")) {
            if (var2.matches("MOUSE_?\\d+")) {
               int var3 = Integer.parseInt(var2.replace("MOUSE", "").replace("_", ""));
               if (var3 >= 1 && var3 <= 16) {
                  return -100 - (var3 - 1);
               }
            }

            int var7 = KeycodeNames.handle(var2.replace("_", ""));
            if (var7 != -1) {
               return var7;
            }

            var7 = KeycodeNames.handle(var2);
            if (var7 != -1) {
               return var7;
            }

            try {
               Field var4 = GLFW.class.getField("GLFW_KEY_" + var2);
               int var5 = var4.getInt(null);
               return var5 > 0 ? var5 : null;
            } catch (ReflectiveOperationException var6) {
               return null;
            }
         } else {
            return -102;
         }
      } else {
         return null;
      }
   }

   static {
      Loader.initialize();
   }

   public static class State {
      public String instance;
      public String data;
      public int context;

      public State(String var1, int var2, String var3) {
         this.instance = var1;
         this.context = var2;
         this.data = var3;
      }
   }
}
