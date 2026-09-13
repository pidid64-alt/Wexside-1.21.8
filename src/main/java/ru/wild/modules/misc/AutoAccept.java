package ru.wild.modules.misc;

import java.util.Arrays;
import java.util.List;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.RunCommand;
import net.minecraft.text.ClickEvent.SuggestCommand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(name = "AutoAccept", category = ModuleCategory.Misc, description = "Автоматически принимает запросы на телепортацию и в клан")
public class AutoAccept extends Module {
   public final ChoiceSetting source = new ChoiceSetting("Принимать", new BooleanSetting("Запрос на ТП", true), new BooleanSetting("Запрос в клан", true));
   public final ModeSetting target = new ModeSetting("Принимать ТП от", "Друзей", "Друзей", "Всех").handle(() -> !this.source.process("Запрос на ТП"));
   public final BooleanSetting pending = new BooleanSetting("Принимать запрос в клан только от друзей", true)
      .handle(() -> !this.source.process("Запрос в клан"));
   private boolean previous;
   private boolean latest = false;
   private long summary = 0L;
   private final String[] matrixBlend = new String[]{
      "has requested teleport", "просит телепортироваться", "хочет телепортироваться к вам", "просит к вам телепортироваться"
   };
   private final String[] vectorMatch = new String[]{"приглашает вас в клан", "приглашает Вас в клан", "invited you to clan"};

   public AutoAccept() {
      this.handle(this.source, this.target, this.pending);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      ServerEnvironment.handle();
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         Text var8 = var2.content();
         String var4 = var8.getString();
         if (this.latest) {
            if (System.currentTimeMillis() - this.summary > 5000L) {
               this.latest = false;
            } else {
               String var5 = this.handle(var8, "Вступить");
               if (var5 != null) {
                  this.handle(var5);
                  this.latest = false;
                  return;
               }
            }
         }

         if (this.source.process("Запрос на ТП") && this.compute(var4)) {
            if (this.target.process("Всех")) {
               this.previous = true;
            } else {
               String var9 = var4.toLowerCase();

               for (String var7 : ru.wild.core.manager.FriendManager.resolve()) {
                  if (var9.contains(var7.toLowerCase())) {
                     this.previous = true;
                     break;
                  }
               }
            }
         }

         if (this.source.process("Запрос в клан") && this.resolve(var4)) {
            String var10 = this.handle(var4, "приглашает");
            if (var10 != null && !this.process(var10)) {
               return;
            }

            String var11 = this.handle(var8, "Вступить");
            if (var11 != null) {
               this.handle(var11);
            } else {
               this.latest = true;
               this.summary = System.currentTimeMillis();
            }
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (this.previous && Module.client.player != null) {
         Module.client.player.networkHandler.sendChatCommand("tpaccept");
         this.previous = false;
      }
   }

   private String handle(Text var1, String var2) {
      if (var1 == null) {
         return null;
      }

      if (var1.getStyle() != null && var1.getStyle().getClickEvent() != null) {
         ClickEvent var3 = var1.getStyle().getClickEvent();
         if (var1.getString().contains(var2)) {
            if (var3 instanceof RunCommand var8) {
               return var8.command();
            }

            if (var3 instanceof SuggestCommand var9) {
               return var9.command();
            }
         }
      }

      List<Text> var7 = var1.getSiblings();
      if (var7 != null) {
         for (Text var5 : var7) {
            String var6 = this.handle(var5, var2);
            if (var6 != null) {
               return var6;
            }
         }
      }

      return null;
   }

   private void handle(String var1) {
      if (Module.client.player != null && Module.client.player.networkHandler != null) {
         if (var1.startsWith("/")) {
            Module.client.player.networkHandler.sendChatCommand(var1.substring(1));
         } else {
            Module.client.player.networkHandler.sendChatCommand(var1);
         }
      }
   }

   private boolean process(String var1) {
      return !this.pending.compute() ? true : ru.wild.core.manager.FriendManager.handle(var1);
   }

   private String handle(String var1, String var2) {
      String var3 = var1.replaceAll("§.", "");
      int var4 = var3.indexOf(var2);
      if (var4 <= 0) {
         return null;
      }

      String var5 = var3.substring(0, var4).trim();
      int var6 = var5.lastIndexOf(32);
      return var6 >= 0 ? var5.substring(var6 + 1).trim() : var5.trim();
   }

   private boolean compute(String var1) {
      String var2 = var1.toLowerCase();
      return Arrays.stream(this.matrixBlend).anyMatch(var1x -> var2.contains(var1x.toLowerCase()));
   }

   private boolean resolve(String var1) {
      String var2 = var1.toLowerCase();
      return Arrays.stream(this.vectorMatch).anyMatch(var1x -> var2.contains(var1x.toLowerCase()));
   }
}
