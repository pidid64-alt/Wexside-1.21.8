package ru.wild.modules.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.ModeSetting;
import ru.wild.automation.AuctionTradeExecutor;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AutoResell", category = ModuleCategory.Misc, description = "Автоматически перевыставляет товары")
public class AutoResell extends Module {
   public static AutoResell source;
   private final Stopwatch pending = new Stopwatch();
   private final Stopwatch previous = new Stopwatch();
   private final Stopwatch latest = new Stopwatch();
   public final ModeSetting target = new ModeSetting("Режим", "Стандарт", "Стандарт", "Князь");
   private final Stopwatch summary = new Stopwatch();
   private static final long matrixBlend = 900L;
   private static final long vectorMatch = 9000L;
   private static final long itemProject = 12000L;
   private final Pattern responseCompute = Pattern.compile("Подождите (\\d+) сек");
   private AutoResell.Mode providerFetch = AutoResell.Mode.WAITING;
   private long profileDraw = 0L;

   public AutoResell() {
      source = this;
   }

   @Override
   public void handle() {
      super.handle();
      this.providerFetch = AutoResell.Mode.WAITING;
      this.pending.handle();
      this.previous.handle();
      this.latest.handle();
      this.summary.handle();
   }

   @Override
   public void process() {
      super.process();
      AuctionTradeExecutor.process(false);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         if (this.target.process("Князь")) {
            if (this.summary.update(60000L)) {
               Module.client.player.networkHandler.sendChatCommand("ah resell");
               this.summary.handle();
            }
         } else {
            switch (this.providerFetch) {
               case WAITING:
                  if (this.pending.update(60000L) && AuctionTradeExecutor.check()) {
                     this.refresh();
                  }
                  break;
               case OPENING_MAIN_AH:
                  if (this.drawAnimation()) {
                     if (this.previous.update(200L)) {
                        this.handle(AutoResell.Mode.CLICKING_STORAGE);
                     }
                  } else {
                     if (this.animate() && this.latest.update(900L)) {
                        this.tick();
                        this.latest.handle();
                     }

                     if (this.previous.update(this.animate() ? 9000L : 5000L)) {
                        ChatLogger.handle("§c[AutoResell] §fМеню аукциона не открылось.");
                        this.render();
                     }
                  }
                  break;
               case CLICKING_STORAGE:
                  if (this.drawAnimation() && Module.client.currentScreen instanceof GenericContainerScreen var6) {
                     if (this.handle(var6)) {
                        this.handle(AutoResell.Mode.OPENING_STORAGE);
                     } else if (!this.animate() || this.previous.update(3000L)) {
                        ChatLogger.handle("§c[AutoResell] §fКнопка 'Хранилище' не найдена.");
                        this.render();
                     }
                     break;
                  }

                  this.render();
                  break;
               case OPENING_STORAGE:
                  if (this.encodePoint()) {
                     if (this.previous.update(200L)) {
                        this.handle(AutoResell.Mode.CLICKING_CLOCK);
                     }
                  } else {
                     if (this.animate() && this.drawAnimation() && Module.client.currentScreen instanceof GenericContainerScreen var5 && this.latest.update(900L)) {
                        this.handle(var5);
                        this.latest.handle();
                     }

                     if (this.previous.update(this.animate() ? 9000L : 5000L)) {
                        ChatLogger.handle("§c[AutoResell] §fХранилище не открылось.");
                        this.render();
                     }
                  }
                  break;
               case CLICKING_CLOCK:
                  if (this.encodePoint() && Module.client.currentScreen instanceof GenericContainerScreen var4) {
                     if (this.process(var4)) {
                        ChatLogger.handle("§d[AutoResell] §fПеревыставляем предметы...");
                        this.handle(AutoResell.Mode.WAITING_RESULT);
                     } else if (!this.animate() || this.previous.update(3500L)) {
                        AuctionTradeExecutor.tick();
                        this.render();
                     }
                     break;
                  }

                  this.render();
                  break;
               case WAITING_RESULT:
                  if (this.animate() && this.encodePoint() && Module.client.currentScreen instanceof GenericContainerScreen var2 && this.latest.update(900L)) {
                     this.process(var2);
                     this.latest.handle();
                  }

                  if (this.previous.update(this.animate() ? 12000L : 10000L)) {
                     ChatLogger.handle("§e[AutoResell] §fНет ответа от аукциона, возвращаю AutoBuy.");
                     this.render();
                  }
                  break;
               case COOLDOWN_WAIT:
                  if (this.pending.update(this.profileDraw) && AuctionTradeExecutor.check()) {
                     ChatLogger.handle("§d[AutoResell] §fПовторная попытка после ожидания...");
                     this.refresh();
                  }
            }
         }
      }
   }

   private void refresh() {
      if (this.encodePoint()) {
         this.handle(AutoResell.Mode.CLICKING_CLOCK);
      } else if (this.drawAnimation()) {
         this.handle(AutoResell.Mode.CLICKING_STORAGE);
      } else if (Module.client.player != null) {
         this.tick();
         this.handle(AutoResell.Mode.OPENING_MAIN_AH);
      }
   }

   private void render() {
      this.providerFetch = AutoResell.Mode.WAITING;
      this.pending.handle();
      this.previous.handle();
      this.latest.handle();
      AuctionTradeExecutor.process(true);
   }

   private void handle(AutoResell.Mode var1) {
      this.providerFetch = var1;
      this.previous.handle();
      this.latest.handle();
   }

   private void tick() {
      if (Module.client.player != null) {
         Module.client.player.networkHandler.sendChatCommand("ah");
      }
   }

   private boolean drawAnimation() {
      if (!(Module.client.currentScreen instanceof GenericContainerScreen var1)) {
         return false;
      } else {
         String var3 = var1.getTitle().getString();
         return var3 != null && (var3.contains("Аукцион") || var3.contains("Auction"));
      }
   }

   private boolean encodePoint() {
      if (!(Module.client.currentScreen instanceof GenericContainerScreen var1)) {
         return false;
      } else {
         String var3 = var1.getTitle().getString();
         return var3 != null && var3.contains("Хранилище");
      }
   }

   private boolean handle(GenericContainerScreen var1) {
      int var2 = this.handle(var1, Items.ENDER_CHEST);
      if (var2 == -1) {
         return false;
      }

      Module.client.interactionManager
         .clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
      return true;
   }

   private boolean process(GenericContainerScreen var1) {
      int var2 = this.handle(var1, Items.CLOCK);
      if (var2 == -1) {
         return false;
      }

      Module.client.interactionManager
         .clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2, 0, SlotActionType.PICKUP, Module.client.player);
      return true;
   }

   private boolean animate() {
      return AutoBuy.source != null && AutoBuy.source.latest.process("FunTime");
   }

   private int handle(GenericContainerScreen var1, Item var2) {
      if (var1 != null && var1.getScreenHandler() != null) {
         for (Slot var4 : ((GenericContainerScreenHandler)var1.getScreenHandler()).slots) {
            if (var4.id < ((GenericContainerScreenHandler)var1.getScreenHandler()).slots.size() - 36 && var4.hasStack() && var4.getStack().getItem() == var2) {
               return var4.id;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var7 = var2.content().getString();
         if (!var7.contains("Предметы успешно перевыставлены") && (!var7.contains("[✔]") || !var7.contains("перевыставлены"))) {
            if (this.animate() && var7.contains("В хранилище отсутствуют предметы для перевыставления")) {
               AuctionTradeExecutor.tick();
               ChatLogger.handle("§e[AutoResell] §fХранилище пустое, возвращаю AutoBuy.");
               this.render();
            }
         } else {
            AuctionTradeExecutor.render();
            ChatLogger.handle("§a[AutoResell] §fГотово.");
            this.render();
         }

         if (this.providerFetch != AutoResell.Mode.WAITING && var7.contains("Подождите") && var7.contains("сек")) {
            Matcher var4 = this.responseCompute.matcher(var7);
            if (var4.find()) {
               try {
                  int var5 = Integer.parseInt(var4.group(1));
                  ChatLogger.handle("§e[AutoResell] §fЖдем " + var5 + " сек (кулдаун)...");
                  this.profileDraw = (var5 + 1) * 1000L;
                  this.handle(AutoResell.Mode.COOLDOWN_WAIT);
                  this.pending.handle();
                  AuctionTradeExecutor.process(true);
               } catch (Exception var6) {
               }
            }
         }

         if (var7.contains("Не удалось выставить") && var7.contains("освободите хранилище")) {
            AuctionTradeExecutor.select();
         } else if (var7.contains("У Вас купили") && var7.contains("на /ah")) {
            AuctionTradeExecutor.refresh();
         } else if (var7.contains("выставлен на продажу за")) {
            AuctionTradeExecutor.onTick();
         }
      }
   }

   enum Mode {
      WAITING,
      OPENING_MAIN_AH,
      CLICKING_STORAGE,
      OPENING_STORAGE,
      CLICKING_CLOCK,
      WAITING_RESULT,
      COOLDOWN_WAIT;
   }
}
