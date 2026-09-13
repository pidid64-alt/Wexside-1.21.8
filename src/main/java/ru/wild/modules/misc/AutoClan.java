package ru.wild.modules.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.util.text.ChatLogger;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(
   name = "AutoClan",
   category = ModuleCategory.Misc,
   description = "Автоматически создает клан а так же приглашает в него ваших друзей (если такие есть)"
)
public class AutoClan extends Module {
   public final KeybindSetting source = new KeybindSetting("Бинд", -1);
   private AutoClan.Mode target = AutoClan.Mode.IDLE;
   private int pending = 0;
   private int previous = 0;
   private int latest = 0;
   private List<String> summary = new ArrayList<>();
   private static final String matrixBlend = "abcdefghijklmnopqrstuvwxyz0123456789";

   public AutoClan() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (var1.resolve() == this.source.compute()) {
         }
      }
   }

   private void refresh() {
      if (Module.client.player != null) {
         this.render();
      }
   }

   private void render() {
      String var1 = this.tick();
      ChatLogger.handle("§7Создание клана с названием: §f" + var1);
      Module.client.player.networkHandler.sendChatMessage("/clan create " + var1);
      this.target = AutoClan.Mode.WAITING_CREATE_RESPONSE;
      this.pending = 0;
   }

   private String tick() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();
      int var2 = var1.nextInt(3, 6);
      StringBuilder var3 = new StringBuilder(var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.append("abcdefghijklmnopqrstuvwxyz0123456789".charAt(var1.nextInt("abcdefghijklmnopqrstuvwxyz0123456789".length())));
      }

      return var3.toString();
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.player != null && this.target != AutoClan.Mode.IDLE) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var4 = var2.content().getString();
            this.handle(var4);
         }
      }
   }

   private void handle(String var1) {
      if (this.target == AutoClan.Mode.WAITING_CREATE_RESPONSE) {
         if (var1.contains("Ошибка, клан с таким названием уже существует")) {
            ChatLogger.handle("§cНазвание занято, пробую другое...");
            this.pending = 0;
            this.target = AutoClan.Mode.CREATING_CLAN;
            return;
         }

         if (var1.contains("Супер! Вы успешно создали клан")) {
            ChatLogger.handle("§aКлан успешно создан!");
            this.drawAnimation();
            return;
         }

         if (var1.contains("Ошибка: Ты уже состоишь в клане")) {
            ChatLogger.handle("§eТы уже в клане, начинаю инвайтить друзей...");
            this.drawAnimation();
            return;
         }
      }

      if (this.target == AutoClan.Mode.INVITING_FRIENDS) {
      }
   }

   private void drawAnimation() {
      this.summary = this.encodePoint();
      if (this.summary.isEmpty()) {
         ChatLogger.handle("§cНет друзей онлайн на этом сервере!");
         this.animate();
      } else {
         ChatLogger.handle("§aНайдено §l" + this.summary.size() + "§a друзей онлайн. Начинаю инвайт...");
         this.previous = 0;
         this.latest = 0;
         this.target = AutoClan.Mode.INVITING_FRIENDS;
      }
   }

   private List<String> encodePoint() {
      ArrayList<String> var1 = new ArrayList<>();
      if (Module.client.player != null && Module.client.getNetworkHandler() != null) {
         List<String> var2 = ru.wild.core.manager.FriendManager.resolve();
         Collection<PlayerListEntry> var3 = Module.client.getNetworkHandler().getPlayerList();
         HashSet<String> var4 = new HashSet<>();

         for (PlayerListEntry var6 : var3) {
            if (var6.getProfile() != null && var6.getProfile().getName() != null) {
               var4.add(var6.getProfile().getName());
            }
         }

         String var8 = Module.client.player.getName().getString();

         for (String var7 : var2) {
            if (var4.contains(var7) && !var7.equalsIgnoreCase(var8)) {
               var1.add(var7);
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && this.target != AutoClan.Mode.IDLE) {
         this.pending++;
         switch (this.target) {
            case CREATING_CLAN:
               if (this.pending > 20) {
                  this.render();
               }
               break;
            case WAITING_CREATE_RESPONSE:
               if (this.pending > 100) {
                  ChatLogger.handle("§cТаймаут ожидания ответа сервера.");
                  this.animate();
               }
               break;
            case INVITING_FRIENDS:
               this.latest++;
               if (this.latest >= 25) {
                  this.latest = 0;
                  if (this.previous < this.summary.size()) {
                     String var2 = this.summary.get(this.previous);
                     Module.client.player.networkHandler.sendChatCommand("clan invite " + var2);
                     ChatLogger.handle("§7Инвайт: §f" + var2 + " §7(" + (this.previous + 1) + "/" + this.summary.size() + ")");
                     this.previous++;
                  } else {
                     ChatLogger.handle("§aВсе друзья приглашены! (" + this.summary.size() + " шт.)");
                     this.animate();
                  }
               }
         }
      }
   }

   private void animate() {
      this.target = AutoClan.Mode.IDLE;
      this.pending = 0;
      this.previous = 0;
      this.latest = 0;
      this.summary.clear();
   }

   @Override
   public void process() {
      this.animate();
      super.process();
   }

   enum Mode {
      IDLE,
      CREATING_CLAN,
      WAITING_CREATE_RESPONSE,
      INVITING_FRIENDS;
   }
}
