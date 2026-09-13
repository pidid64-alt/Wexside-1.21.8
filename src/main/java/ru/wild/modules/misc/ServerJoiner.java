package ru.wild.modules.misc;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "ServerJoiner", category = ModuleCategory.Misc, description = "Авто-заход на сервера (FunTime/SpookyTime)")
public class ServerJoiner extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "FunTime", "FunTime", "SpookyTime");
   private static final Pattern previous = Pattern.compile("anarchy\\s*(\\d+)");
   public final StringSetting target = new StringSetting("Анархия", "101").handle(() -> !this.source.process("FunTime"));
   public final BooleanSetting pending = new BooleanSetting("Выключать после входа", true);
   private final Stopwatch latest = new Stopwatch();
   private int summary = -1;
   private boolean matrixBlend;
   private boolean vectorMatch;

   public ServerJoiner() {
      this.handle(this.source, this.target, this.pending);
   }

   @Override
   public void handle() {
      super.handle();
      this.matrixBlend = false;
      this.vectorMatch = false;
      this.latest.handle();
      if (this.source.process("FunTime")) {
         this.summary = this.encodePoint();
         if (this.summary <= 0) {
            ChatLogger.handle("[ServerJoiner] Укажи корректную анархию в настройке.");
            this.toggle();
            return;
         }

         this.drawAnimation();
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && !this.matrixBlend) {
         if (this.source.process("FunTime")) {
            if (this.latest.update(50L)) {
               this.drawAnimation();
               this.latest.handle();
            }
         } else if (this.source.process("SpookyTime")) {
            if (this.vectorMatch && !this.tick()) {
               this.handle("[ServerJoiner] Успешный вход на дуэли SpookyTime.");
               return;
            }

            this.refresh();
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (this.source.process("FunTime") && var1.update().equals(PacketEvent.Mode.RECEIVE)) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var7 = this.process(var2.content().getString());
            if (!var7.isEmpty() && !var7.contains("сервер заполнен") && !var7.contains("были кикнуты при подключении")) {
               if (this.compute(var7)) {
                  this.handle("[ServerJoiner] Уже подключен к этой анархии.");
               } else {
                  Matcher var4 = previous.matcher(var7);
                  if (var4.find()) {
                     try {
                        if (Integer.parseInt(var4.group(1)) == this.summary) {
                           this.handle("[ServerJoiner] Зашёл на /an" + this.summary + ".");
                        }
                     } catch (NumberFormatException var6) {
                     }
                  }
               }
            }
         }
      }
   }

   private void refresh() {
      if (Module.client.currentScreen instanceof HandledScreen var1) {
         if (!this.handle(var1)) {
            ChatLogger.handle("[ServerJoiner] Открыт неверный экран для SpookyTime, модуль выключен.");
            this.setEnabled(false);
            return;
         }

         ScreenHandler var5 = var1.getScreenHandler();

         for (int var3 = 0; var3 < var5.slots.size(); var3++) {
            Slot var4 = (Slot)var5.slots.get(var3);
            if (var4.getStack().isOf(Items.RESPAWN_ANCHOR)) {
               Module.client.interactionManager.clickSlot(var5.syncId, var3, 0, SlotActionType.PICKUP, Module.client.player);
               Module.client.setScreen(null);
               this.vectorMatch = true;
               this.latest.handle();
               return;
            }
         }
      } else if (this.latest.update(500L)) {
         this.render();
         this.latest.handle();
      }
   }

   private boolean handle(HandledScreen<?> var1) {
      return this.process(var1.getTitle().getString()).equals("выберите режим: ");
   }

   private void render() {
      PlayerInventory var1 = Module.client.player.getInventory();

      for (int var2 = 0; var2 < 9; var2++) {
         if (var1.getStack(var2).isOf(Items.COMPASS)) {
            if (var1.getSelectedSlot() != var2) {
               var1.setSelectedSlot(var2);
               Module.client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var2));
            }

            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            return;
         }
      }
   }

   private boolean tick() {
      if (Module.client.player == null) {
         return false;
      }

      PlayerInventory var1 = Module.client.player.getInventory();

      for (int var2 = 0; var2 < 9; var2++) {
         if (var1.getStack(var2).isOf(Items.COMPASS)) {
            return true;
         }
      }

      return false;
   }

   private void drawAnimation() {
      if (Module.client.player != null && Module.client.player.networkHandler != null) {
         Module.client.player.networkHandler.sendChatMessage("/an" + this.summary);
      }
   }

   private void handle(String var1) {
      this.matrixBlend = true;
      ChatLogger.handle(var1);
      if (this.pending.compute()) {
         this.toggle();
      }
   }

   private int encodePoint() {
      String var1 = this.target.compute();
      if (var1 == null) {
         return -1;
      }

      String var2 = var1.replaceAll("\\D+", "");
      return var2.isEmpty() ? -1 : Integer.parseInt(var2);
   }

   private String process(String var1) {
      return var1 == null ? "" : var1.replaceAll("§.", "").toLowerCase(Locale.ROOT).trim();
   }

   private boolean compute(String var1) {
      return var1.contains("вы уже подключены к этому серверу") || var1.contains("already connected to this server");
   }
}
