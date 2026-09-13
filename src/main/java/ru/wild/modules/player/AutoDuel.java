package ru.wild.modules.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;

@ModuleRegister(name = "AutoDuel", category = ModuleCategory.Player, description = "Кидает за вас дуэли на ReallyWorld")
public class AutoDuel extends Module {
   public final ChoiceSetting source = new ChoiceSetting(
      "Режим: ",
      new BooleanSetting("Щиты", false),
      new BooleanSetting("Шипы 3", false),
      new BooleanSetting("Лук", false),
      new BooleanSetting("Тотемы", false),
      new BooleanSetting("НоДебафф", false),
      new BooleanSetting("Шары", true),
      new BooleanSetting("Классик", false),
      new BooleanSetting("Читерский рай", false),
      new BooleanSetting("Без эндер-жемчуга", false)
   );
   private final List<String> target = new ArrayList<>();
   private long pending = 0L;
   private long previous = 0L;
   private long latest = 0L;
   private static final Pattern summary = Pattern.compile("^\\w{3,16}$");
   private static final String[] matrixBlend = new String[]{
      "Щиты", "Шипы 3", "Лук", "Тотемы", "НоДебафф", "Шары", "Классик", "Читерский рай", "Без эндер-жемчуга"
   };

   public AutoDuel() {
      this.handle(this.source);
   }

   @Override
   public void handle() {
      super.handle();
      this.target.clear();
      this.pending = 0L;
      this.previous = System.currentTimeMillis();
      this.latest = 0L;
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.getNetworkHandler() != null) {
         this.render();
         List<String> var2 = this.refresh();
         long var3 = System.currentTimeMillis();
         if (var3 - this.previous > 800L * Math.max(var2.size(), 1)) {
            this.target.clear();
            this.previous = var3;
         }

         if (var3 - this.pending > 1000L) {
            for (String var6 : var2) {
               if (!this.target.contains(var6) && !var6.equals(Module.client.player.getGameProfile().getName())) {
                  Module.client.getNetworkHandler().sendChatCommand("duel " + var6);
                  this.target.add(var6);
                  this.pending = var3;
                  break;
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (var1.resolve() instanceof GameMessageS2CPacket var2) {
         String var4 = var2.content().getString().toLowerCase();
         if (var4.contains("начало") && var4.contains("через") && var4.contains("секунд")
            || var4.contains("дуэли » во время поединка запрещено использовать команды")
            || var4.contains("duel") && var4.contains("during") && var4.contains("forbidden")) {
            this.toggle();
         }
      }
   }

   private List<String> refresh() {
      return Module.client.getNetworkHandler()
         .getPlayerList()
         .stream()
         .map(var0 -> var0.getProfile().getName())
         .filter(var0 -> summary.matcher(var0).matches())
         .collect(Collectors.toList());
   }

   private void render() {
      if (Module.client.currentScreen instanceof GenericContainerScreen var1) {
         String var7 = var1.getTitle().getString();
         long var3 = System.currentTimeMillis();
         if (var7.contains("Выбор набора") || var7.contains("Kit selection")) {
            if (var3 - this.latest > 90L) {
               ArrayList var5 = new ArrayList();

               for (int var6 = 0; var6 < matrixBlend.length; var6++) {
                  if (this.source.process(matrixBlend[var6])) {
                     var5.add(var6);
                  }
               }

               if (!var5.isEmpty()) {
                  Collections.shuffle(var5);
                  int var8 = (Integer)var5.get(0);
                  Module.client.interactionManager
                     .clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var8, 0, SlotActionType.QUICK_MOVE, Module.client.player);
                  this.latest = var3;
               }
            }
         } else if ((var7.contains("Настройка поединка") || var7.contains("Duel setup")) && var3 - this.latest > 90L) {
            Module.client.interactionManager
               .clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, 0, 0, SlotActionType.QUICK_MOVE, Module.client.player);
            this.latest = var3;
         }
      }
   }
}
