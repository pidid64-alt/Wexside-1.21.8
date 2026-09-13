package ru.wild.modules.misc;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ServerInfo;
import org.wild.mixin.acceser.BossBarHudAccessor;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "PvPSafe", category = ModuleCategory.Misc, description = "Защищает вас от лива в КД")
public class PvPSafe extends Module {
   private static final Set<String> source = Set.of(
      "hub",
      "lobby",
      "spawn",
      "leave",
      "quit",
      "disconnect",
      "server",
      "servers",
      "an",
      "anarchy",
      "realm",
      "menu",
      "logout",
      "reconnect",
      "play",
      "warp",
      "duel",
      "l"
   );
   private final ChoiceSetting target = new ChoiceSetting(
      "Сервер",
      new BooleanSetting("FunTime", true),
      new BooleanSetting("HolyWorld", true),
      new BooleanSetting("SpookyTime", true),
      new BooleanSetting("Любой другой", true)
   );
   private final BooleanSetting pending = new BooleanSetting("Notifications", true);
   private long previous;

   public PvPSafe() {
      this.handle(this.target, this.pending);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (this.tick()) {
         this.handle(AutoLeave.class);
         this.handle(ServerJoiner.class);
      }
   }

   public static boolean refresh() {
      PvPSafe var0 = animate();
      return var0 != null && var0.tick();
   }

   public static boolean render() {
      return handle(MinecraftClient.getInstance());
   }

   public static boolean handle(String var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = var0.trim();
      return !var1.startsWith("/") ? false : process(var1.substring(1));
   }

   public static boolean process(String var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = apply(var0);
      if (var1.isEmpty()) {
         return false;
      }

      String var2 = var1.split("\\s+", 2)[0];
      boolean var3 = source.contains(var2) || var2.matches("an\\d{1,5}");
      return !var3 ? false : resolve("command /" + var1);
   }

   public static boolean compute(boolean var0) {
      return resolve(var0 ? "server transfer" : "disconnect");
   }

   private boolean tick() {
      return this.enabled && Module.client != null && Module.client.player != null && Module.client.world != null
         ? this.drawAnimation() && handle(Module.client)
         : false;
   }

   private boolean drawAnimation() {
      String var1 = this.encodePoint();
      if (var1.contains("funtime") || var1.contains("fun-time")) {
         return this.target.process("FunTime");
      } else if (var1.contains("holyworld") || var1.contains("holy-world") || var1.contains("holy")) {
         return this.target.process("HolyWorld");
      } else {
         return !var1.contains("spookytime") && !var1.contains("spooky-time") && !var1.contains("spooky")
            ? this.target.process("Любой другой")
            : this.target.process("SpookyTime");
      }
   }

   private String encodePoint() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 == null) {
         return "";
      }

      ServerInfo var2 = var1.getCurrentServerEntry();
      return var2 != null && var2.address != null ? var2.address.toLowerCase(Locale.ROOT) : "";
   }

   private static boolean handle(MinecraftClient var0) {
      if (var0 != null && var0.inGameHud != null && var0.inGameHud.getBossBarHud() != null) {
         Map<?, ClientBossBar> var1 = ((BossBarHudAccessor)var0.inGameHud.getBossBarHud()).getBossBars();

         for (ClientBossBar var3 : var1.values()) {
            String var4 = execute(var3.getName().getString());
            if (compute(var4)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean compute(String var0) {
      return var0.contains("pvp")
         || var0.contains("пвп")
         || var0.contains("combat")
         || var0.contains("fight")
         || var0.contains("battle")
         || var0.contains("режим боя")
         || var0.contains("в бою")
         || var0.contains("до выхода")
         || var0.contains("нельзя выйти")
         || var0.contains("не выходите");
   }

   private static boolean resolve(String var0) {
      PvPSafe var1 = animate();
      if (var1 != null && var1.tick()) {
         var1.update(var0);
         return true;
      } else {
         return false;
      }
   }

   private void update(String var1) {
      if (this.pending.compute()) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.previous >= 1200L) {
            this.previous = var2;
            ChatLogger.handle("[PvPSafe] заблокировал " + var1);
         }
      }
   }

   private static String apply(String var0) {
      String var1 = var0.trim().toLowerCase(Locale.ROOT);

      while (var1.startsWith("/")) {
         var1 = var1.substring(1).trim();
      }

      return var1.replaceAll("\\s+", " ");
   }

   private static String execute(String var0) {
      return var0 == null ? "" : var0.replaceAll("(?i)§[0-9a-fk-or]", "").toLowerCase(Locale.ROOT).trim();
   }

   private static PvPSafe animate() {
      return WildClient.drawProfile() && WildClient.instance != null && WildClient.instance.data != null
         ? WildClient.instance.data.handle(PvPSafe.class)
         : null;
   }

   private <T extends Module> void handle(Class<T> var1) {
      Module var2 = WildClient.instance.data.handle(var1);
      if (var2 != null && var2.enabled) {
         var2.setEnabled(false);
      }
   }
}
