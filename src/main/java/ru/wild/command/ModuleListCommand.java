package ru.wild.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.lwjgl.glfw.GLFW;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.setting.KeybindMode;
import ru.wild.core.Command;
import ru.wild.core.manager.FeatureManager;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.text.KeybindPresets;
import ru.wild.util.text.KeycodeNames;

public class ModuleListCommand extends Command {
   private static final List<String> instance = List.of("list", "clear", "del", "delete", "remove", "unbind");
   private static final List<String> data = List.of(
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
      "ESCAPE",
      "BACKSPACE",
      "DELETE",
      "INSERT",
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
      "MOUSE1",
      "MOUSE2",
      "MOUSE3",
      "MOUSE4",
      "MOUSE5",
      "WHEEL_UP",
      "WHEEL_DOWN",
      "NONE"
   );

   public ModuleListCommand() {
      super("bind", "Управление биндами модулей", ".bind <module> <key> | .bind list | .bind del <module> | .bind clear");
   }

   @Override
   public List<String> handle(String[] var1) {
      if (var1.length == 2) {
         String var4 = var1[1].toLowerCase(Locale.ROOT);
         LinkedHashSet var5 = new LinkedHashSet();
         instance.stream().filter(var1x -> var1x.startsWith(var4)).forEach(var5::add);
         this.apply().stream().filter(var1x -> var1x.toLowerCase(Locale.ROOT).startsWith(var4)).forEach(var5::add);
         return new ArrayList<>(var5);
      }

      if (var1.length == 3) {
         String var2 = var1[1].toLowerCase(Locale.ROOT);
         String var3 = var1[2].toLowerCase(Locale.ROOT);
         if (this.resolve(var2)) {
            return this.apply().stream().filter(var1x -> var1x.toLowerCase(Locale.ROOT).startsWith(var3)).toList();
         }

         if (!instance.contains(var2)) {
            return data.stream().filter(var1x -> var1x.toLowerCase(Locale.ROOT).startsWith(var3)).toList();
         }
      }

      return List.of();
   }

   @Compile
   @Override
   public void process(String[] var1) {
      FeatureManager var2 = WildClient.instance.data;
      if (var2 == null) {
         ChatLogger.handle("§cМенеджер модулей не инициализирован.");
      } else if (var1.length == 0) {
         ChatLogger.handle("§cИспользование: " + this.handle());
      } else {
         switch (var1[0].toLowerCase(Locale.ROOT)) {
            case "list":
               this.update();
               break;
            case "clear":
               this.resolve();
               break;
            case "del":
            case "delete":
            case "remove":
            case "unbind":
               this.resolve(var1);
               break;
            default:
               this.compute(var1);
         }
      }
   }

   @Compile
   private void compute(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cИспользование: .bind <module> <key>");
      } else {
         Module var2 = this.handle(var1[0]);
         if (var2 == null) {
            this.process(var1[0]);
         } else {
            Integer var3 = this.compute(var1[1]);
            if (var3 == null) {
               ChatLogger.handle("§cНеизвестная клавиша: §f" + var1[1]);
            } else {
               KeybindPresets var4 = KeybindPresets.handle();
               int var5 = var3;
               KeybindMode var6 = KeybindMode.TOGGLE;
               if (var4 != null) {
                  var4.handle(var2, var5, var6);
               }

               this.execute();
               ChatLogger.handle("§aБинд установлен: §f" + var2.displayName + " §7-> §f" + this.handle(var3));
            }
         }
      }
   }

   @Compile
   private void resolve(String[] var1) {
      if (var1.length < 2) {
         ChatLogger.handle("§cИспользование: .bind del <module>");
      } else {
         Module var2 = this.handle(var1[1]);
         if (var2 == null) {
            this.process(var1[1]);
         } else {
            KeybindPresets var3 = KeybindPresets.handle();
            KeybindMode var4 = KeybindMode.TOGGLE;
            var3.handle(var2, -1, var4);
            this.execute();
            ChatLogger.handle("§aБинд удален: §f" + var2.displayName);
         }
      }
   }

   @Compile
   private void resolve() {
      int var1 = 0;
      FeatureManager var2 = WildClient.instance.data;
      if (var2 != null) {
         ArrayList var3 = var2.process();
         if (var3 != null) {
            Iterator var4 = var3.iterator();
            if (var4 != null) {
               while (var4.hasNext()) {
                  Module var5 = (Module)var4.next();
                  if (var5.keyCode != -1) {
                     var5.keyCode = -1;
                     var1++;
                  }
               }
            }
         }
      }

      this.execute();
      ChatLogger.handle("§aОчищено биндов модулей: §f" + var1);
   }

   @Compile
   private void update() {
      FeatureManager var1 = WildClient.instance.data;
      ArrayList<Module> var2 = var1 == null ? null : var1.process();
      Stream<Module> var3 = var2 == null ? null : var2.stream();
      Predicate<Module> var4 = var0 -> var0.keyCode != -1;
      Stream<Module> var5 = var3 == null ? null : var3.filter(var4);
      Function<Module, String> var6 = var0 -> var0.displayName.toLowerCase(Locale.ROOT);
      Comparator<Module> var7 = Comparator.comparing(var6);
      Stream<Module> var8 = var5 == null ? null : var5.sorted(var7);
      List<Module> var9 = var8 == null ? null : var8.toList();
      if (var9 != null && var9.isEmpty()) {
         ChatLogger.handle("§7Биндов модулей нет.");
      } else {
         int var10 = var9 == null ? 0 : var9.size();
         ChatLogger.handle("§fБинды модулей (§7" + var10 + "§f):");
         if (var9 != null) {
            for (Module var12 : var9) {
               String var13 = var12.displayName;
               int var14 = var12.keyCode;
               String var15 = this.handle(var14);
               ChatLogger.handle("§7- §f" + var13 + " §8[§e" + var15 + "§8]");
            }
         }
      }
   }

   private Module handle(String var1) {
      String var2 = this.update(var1);
      ArrayList var3 = new ArrayList();

      for (Module var5 : WildClient.instance.data.process()) {
         if (var2.equals(this.update(var5.displayName))
            || var2.equals(this.update(var5.description))
            || var2.equals(this.update(var5.getClass().getSimpleName()))) {
            return var5;
         }

         if (this.update(var5.displayName).contains(var2)
            || this.update(var5.description).contains(var2)
            || this.update(var5.getClass().getSimpleName()).contains(var2)) {
            var3.add(var5);
         }
      }

      return var3.size() == 1 ? (Module)var3.get(0) : null;
   }

   private void process(String var1) {
      List var2 = this.apply().stream().filter(var2x -> this.update(var2x).contains(this.update(var1))).limit(8L).toList();
      if (var2.isEmpty()) {
         ChatLogger.handle("§cМодуль не найден: §f" + var1);
      } else {
         ChatLogger.handle("§cНеоднозначный модуль: §f" + var1 + " §7(" + String.join(", ", var2) + ")");
      }
   }

   private List<String> apply() {
      return WildClient.instance.data == null
         ? List.of()
         : WildClient.instance.data.process().stream().map(var0 -> var0.displayName).sorted(String.CASE_INSENSITIVE_ORDER).toList();
   }

   private Integer compute(String var1) {
      String var2 = var1.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
      if (var2.equals("NONE") || var2.equals("NULL") || var2.equals("UNBOUND") || var2.equals("CLEAR")) {
         return -1;
      }

      if (var2.equals("WHEELUP") || var2.equals("WHEEL_UP") || var2.equals("MWHEELUP")) {
         return -200;
      }

      if (var2.equals("WHEELDOWN") || var2.equals("WHEEL_DOWN") || var2.equals("MWHEELDOWN")) {
         return -201;
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

         int var6 = KeycodeNames.handle(var2.replace("_", ""));
         if (var6 != -1) {
            return var6;
         }

         var6 = KeycodeNames.handle(var2);
         if (var6 != -1) {
            return var6;
         }

         try {
            Field var4 = GLFW.class.getField("GLFW_KEY_" + var2);
            return var4.getInt(null);
         } catch (ReflectiveOperationException var5) {
            return null;
         }
      } else {
         return -102;
      }
   }

   private String handle(int var1) {
      return KeybindPresets.handle().handle(var1);
   }

   private boolean resolve(String var1) {
      return var1.equals("del") || var1.equals("delete") || var1.equals("remove") || var1.equals("unbind");
   }

   private String update(String var1) {
      return var1 == null ? "" : var1.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9а-яё]", "");
   }

   private void execute() {
      if (WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   static {
      Loader.initialize();
   }
}
