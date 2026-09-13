package ru.wild.modules.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.util.inventory.AuctionLoreParser;
import ru.wild.util.math.ActionDelay;

@ModuleRegister(name = "AhHelper", description = "Помощник на аукционе", category = ModuleCategory.Misc)
public class AhHelper extends Module {
   public static KeybindSetting source = new KeybindSetting("Поиск предмета в руке", -1);
   public static BooleanSetting target = new BooleanSetting("Показ самых дешевых предметов", true);
   public static BooleanSetting pending = new BooleanSetting("Показывать по ценам", true);
   public static final List<Integer> previous = new ArrayList<>();
   private static AhHelper latest;
   private static String summary = "";
   private static long matrixBlend;
   private static boolean vectorMatch;
   private static boolean itemProject;
   private static boolean responseCompute;
   private static long providerFetch;
   private final ActionDelay profileDraw = new ActionDelay();

   public AhHelper() {
      latest = this;
      this.handle(source, target, pending);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      animate();
      if (Module.client.player != null && target.compute()) {
         if (Module.client.currentScreen instanceof HandledScreen var2) {
            if (handle(var2)) {
               this.compute(var2);
            } else {
               previous.clear();
            }
         } else {
            previous.clear();
         }
      } else {
         previous.clear();
      }
   }

   public static boolean handle(HandledScreen<?> var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = compute(var0.getTitle().getString());
      return process(var1);
   }

   public static boolean process(HandledScreen<?> var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = compute(var0.getTitle().getString());
      return var1.contains("поиск:")
         || var1.contains("search:")
         || var1.contains("п:")
         || var1.contains("漢:")
         || var1.contains("\ud83d\udd0e")
         || var1.contains("\ud83d\udd0d");
   }

   private static boolean process(String var0) {
      return var0.contains("аукцион")
         || var0.contains("auction")
         || var0.contains("поиск:")
         || var0.contains("search:")
         || var0.contains("п:")
         || var0.contains("漢:")
         || var0.contains("\ud83d\udd0e:")
         || var0.contains("\ud83d\udd0d:");
   }

   private static String compute(String var0) {
      return var0 == null ? "" : var0.replaceAll("(?i)§.", "").replace(' ', ' ').replace('：', ':').replaceAll("\\s*:\\s*", ":").trim().toLowerCase(Locale.ROOT);
   }

   private void compute(HandledScreen<?> var1) {
      previous.clear();
      int[] var2 = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
      int[] var3 = new int[]{-1, -1, -1, -1};

      for (Slot var5 : var1.getScreenHandler().slots) {
         if (!handle(var1, var5)) {
            int var6 = AuctionLoreParser.process(var5);
            if (var6 > 0) {
               for (int var7 = 0; var7 < 4; var7++) {
                  if (var6 < var2[var7]) {
                     for (int var8 = 3; var8 > var7; var8--) {
                        var2[var8] = var2[var8 - 1];
                        var3[var8] = var3[var8 - 1];
                     }

                     var2[var7] = var6;
                     var3[var7] = var5.id;
                     break;
                  }
               }
            }
         }
      }

      for (int var9 = 0; var9 < 4; var9++) {
         if (var3[var9] != -1) {
            previous.add(var3[var9]);
         }
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (this.profileDraw.resolve(300L) && var1.resolve() == source.compute()) {
         this.encodePoint();
         this.profileDraw.handle();
      }
   }

   private void encodePoint() {
      if (Module.client.player != null) {
         ItemStack var1 = Module.client.player.getMainHandStack();
         if (!var1.isEmpty()) {
            String var2 = this.resolve(var1.getName().getString());
            String var3 = this.handle(var2);
            if (!var3.equals(var2)) {
               Module.client.player.networkHandler.sendChatCommand("ah search " + var3);
            } else {
               if (var2.isEmpty()) {
                  var2 = var1.getItem().getName().getString();
               }

               if (!var2.isEmpty()) {
                  Module.client.player.networkHandler.sendChatCommand("ah search " + var2);
               }
            }
         }
      }
   }

   private String resolve(String var1) {
      return var1 == null
         ? ""
         : var1.replaceAll("(?i)§.", "")
            .replaceAll("123", "")
            .replaceAll("(?i)&.", "")
            .replaceAll("\\[[^\\]]*]", " ")
            .replaceAll("[★✦✧✪✫✬✭✮✯✰❄☃⚒☠❤❣♕♛♜♞♟]", " ")
            .replace("xxx", " ")
            .replaceAll("\\s+", " ")
            .trim();
   }

   public String handle(String var1) {
      if (var1 == null) {
         return "";
      } else if (var1.contains("Рассадник монстров")) {
         return "Спавнер";
      } else if (var1.contains("TIER WHITE")) {
         return "вайт";
      } else if (var1.contains("TIER BLACK")) {
         return "блэк";
      } else {
         return var1.contains("Прогрузчик чанков [1x1]") ? "Прогрузчик чанков" : var1;
      }
   }

   public static void handle(String var0, long var1) {
      summary = update(var0);
      matrixBlend = var1;
      vectorMatch = !summary.isEmpty() && var1 > 0L;
      itemProject = true;
      responseCompute = false;
      providerFetch = System.currentTimeMillis();
   }

   public static void handle(long var0) {
      summary = "";
      matrixBlend = var0;
      vectorMatch = var0 > 0L;
      itemProject = false;
      responseCompute = false;
      providerFetch = System.currentTimeMillis();
   }

   public static void refresh() {
      summary = "";
      matrixBlend = 0L;
      vectorMatch = false;
      itemProject = false;
      responseCompute = false;
      providerFetch = 0L;
      previous.clear();
   }

   public static boolean render() {
      return vectorMatch;
   }

   public static String tick() {
      return summary;
   }

   public static long drawAnimation() {
      return matrixBlend;
   }

   public static boolean handle(HandledScreen<?> var0, Slot var1) {
      if (latest == null || !latest.enabled || !pending.compute() || !vectorMatch) {
         return false;
      }

      if (var0 != null && var1 != null && var1.hasStack() && resolve(var0)) {
         if (Module.client.player != null && var1.inventory == Module.client.player.getInventory()) {
            return false;
         }

         int var2 = AuctionLoreParser.process(var1);
         return var2 <= 0 ? false : var2 > matrixBlend;
      } else {
         return false;
      }
   }

   private static void animate() {
      if (vectorMatch) {
         if (Module.client.currentScreen instanceof HandledScreen var0) {
            if (resolve(var0)) {
               responseCompute = true;
            } else {
               if (responseCompute || System.currentTimeMillis() - providerFetch > 5000L) {
                  refresh();
               }
            }
         } else {
            if (responseCompute || System.currentTimeMillis() - providerFetch > 5000L) {
               refresh();
            }
         }
      }
   }

   private static boolean resolve(HandledScreen<?> var0) {
      return itemProject ? process(var0) : handle(var0);
   }

   private static String update(String var0) {
      return var0 == null
         ? ""
         : var0.replaceAll("(?i)§.", "")
            .replaceAll("(?i)&.", "")
            .replace(' ', ' ')
            .replace('_', ' ')
            .replace('-', ' ')
            .replaceAll("(?i)\\bminecraft:", "")
            .replaceAll("[^\\p{L}\\p{N}: ]", " ")
            .replaceAll("\\s+", " ")
            .trim()
            .toLowerCase(Locale.ROOT);
   }
}
