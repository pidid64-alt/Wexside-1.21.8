package ru.wild.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.core.Command;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;
import ru.wild.util.player.NicknameUtil;
import ru.wild.util.text.ChatLogger;

public final class RctCommand extends Command {
   private static final int instance = 1;
   private static final int data = 66;
   private static final long context = 180L;
   private static final long config = 650L;
   private static final long state = 20000L;
   private static final long cache = 1000L;
   private static final long output = 300000L;
   private static final long current = 600000L;
   private static RctCommand active;
   private static final Pattern mode = Pattern.compile(
      "(?iu)(?:клан\\s*лайт|кланлайт|clan\\s*lite|clanlite|лайт|lite|анарх(?:ия|ии)?|anarchy)[^\\d#№]{0,24}[#№]?\\s*(\\d{1,2})(?!\\d)"
   );
   private static final Pattern renderer = Pattern.compile("(?u)[#№]\\s*(\\d{1,2})(?!\\d)");
   private static final Pattern handler = Pattern.compile("(?iu)анарх(?:ия)?\\s*[-#№]?\\s*(\\d{1,2})(?!\\d)");
   private int animationDraw = -1;
   private boolean pointEncode;
   private boolean animator;
   private boolean source;
   private boolean target;
   private long pending;
   private long previous;
   private long latest;
   private long summary;
   private long matrixBlend;
   private boolean vectorMatch;
   private long itemProject;

   public RctCommand() {
      super("rct", "Перезаход на выбранную Лайт анархию", ".rct [1-66]");
      active = this;
   }

   public static RctCommand resolve() {
      return active;
   }

   public void handle(boolean var1) {
      if (this.vectorMatch != var1) {
         this.vectorMatch = var1;
         this.itemProject = var1 ? this.update() : 0L;
      }
   }

   private long update() {
      return System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(300000L, 600001L);
   }

   private void apply() {
      if (this.vectorMatch) {
         if (this.itemProject == 0L) {
            this.itemProject = this.update();
         } else if (System.currentTimeMillis() >= this.itemProject) {
            int var1 = this.handle(this.prepare());
            this.itemProject = this.update();
            this.process(new String[]{String.valueOf(var1)});
         }
      }
   }

   private int handle(int var1) {
      byte var2 = 66;
      if (var2 <= 1) {
         return 1;
      }

      int var3;
      do {
         var3 = 1 + ThreadLocalRandom.current().nextInt(var2);
      } while (var3 == var1);

      return var3;
   }

   @Compile
   @Override
   public void process(String[] var1) {
      if (toggleState.player != null && toggleState.player.networkHandler != null) {
         if (var1.length > 1) {
            this.render();
         } else {
            int var2;
            if (var1.length == 0) {
               var2 = this.check();
               if (var2 < 1 || var2 > 66) {
                  ChatLogger.handle("§c[RCT] Ошибка парса");
                  return;
               }
            } else {
               var2 = this.process(var1[0]);
               if (var2 < 1 || var2 > 66) {
                  ChatLogger.handle("§c[RCT] Номер анархии должен быть от 1 до 66.");
                  return;
               }
            }

            this.animationDraw = var2;
            this.pointEncode = true;
            this.source = false;
            this.target = false;
            this.pending = System.currentTimeMillis();
            this.previous = this.pending;
            this.latest = 0L;
            this.summary = 0L;
            this.matrixBlend = 0L;
            this.animator = this.onTick();
            if (toggleState.currentScreen != null) {
               toggleState.player.closeScreen();
            }

            if (this.animator) {
               toggleState.player.networkHandler.sendChatCommand("hub");
               toggleState.player.networkHandler.sendChatCommand("an" + this.animationDraw);
               this.matrixBlend = this.pending + 1000L;
               ChatLogger.handle("§7[RCT] FunTime переход на анархию §f#" + this.animationDraw + "§7...");
            } else {
               toggleState.player.networkHandler.sendChatCommand("hub");
               ChatLogger.handle("§7[RCT] Переход на Лайт анархию §f#" + this.animationDraw + "§7...");
            }
         }
      } else {
         ChatLogger.handle("§c[RCT] Игрок не подключен к серверу.");
      }
   }

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (toggleState.player != null && toggleState.world != null && toggleState.interactionManager != null) {
         if (!this.pointEncode) {
            this.apply();
         } else {
            long var2 = System.currentTimeMillis();
            if (var2 - this.pending > 20000L) {
               this.update("Истекло время ожидания меню или подключения.");
            } else if (this.animator) {
               this.handle(var2);
            } else if (this.target && this.prepare() == this.animationDraw) {
               this.select();
            } else if (this.target && var2 - this.latest > 8000L) {
               this.update("Сервер не подтвердил подключение к анархии #" + this.animationDraw + ".");
            } else if (toggleState.currentScreen instanceof GenericContainerScreen var4) {
               this.handle(var4, var2);
            } else {
               if (var2 - this.previous >= 650L) {
                  this.execute();
                  this.previous = var2;
               }
            }
         }
      }
   }

   private void handle(GenericContainerScreen var1, long var2) {
      if (var2 - this.previous >= 180L) {
         String var4 = this.resolve(var1.getTitle().getString());
         if (!var4.contains("выберите режим") && !var4.contains("select mode")) {
            if (var4.contains("выбор лайт анархии") || var4.contains("lite anarchy")) {
               Slot var8 = this.process(var1);
               if (var8 == null) {
                  if (this.source && var2 - this.previous >= 1200L) {
                     this.source = false;
                  }

                  if (!this.source) {
                     List var6 = this.compute(var1)
                        .stream()
                        .filter(var0 -> var0.getStack().isOf(Items.ARMOR_STAND))
                        .sorted(Comparator.comparingInt(var0 -> var0.id))
                        .toList();
                     int var7 = this.compute(this.animationDraw);
                     if (var7 >= 0 && var7 < var6.size()) {
                        this.handle(var1, (Slot)var6.get(var7));
                        this.source = true;
                        this.previous = var2;
                     }
                  }
               } else {
                  if (!this.target || var2 - this.previous >= 1200L) {
                     this.handle(var1, var8);
                     this.target = true;
                     this.latest = var2;
                     this.previous = var2;
                  }
               }
            }
         } else {
            if (this.summary == 0L) {
               this.summary = var2;
            }

            Slot var5 = this.handle(var1);
            if (var5 != null) {
               this.handle(var1, var5, SlotActionType.PICKUP);
               this.source = false;
               this.summary = 0L;
               this.previous = var2;
            } else if (var2 - this.summary >= 3000L) {
               this.update("Режим Лайт отсутствует в меню выбора.");
            }
         }
      }
   }

   private Slot handle(GenericContainerScreen var1) {
      Slot var2 = null;

      for (Slot var4 : this.compute(var1)) {
         ItemStack var5 = var4.getStack();
         if (var5.isOf(Items.PLAYER_HEAD)) {
            String var6 = this.resolve(var5.getName().getString());
            if (var6.equals("лайт") || var6.equals("lite")) {
               return var4;
            }

            String var7 = this.handle(var5);
            if ((var7.contains("анархия лайт") || var7.contains("lite anarchy"))
               && (var7.matches("(?s).*анархия\\s*1\\D+16.*") || var7.matches("(?s).*anarchy\\s*1\\D+16.*"))) {
               var2 = var4;
            }
         }
      }

      return var2;
   }

   private Slot process(GenericContainerScreen var1) {
      Pattern var2 = Pattern.compile("(?iu)#\\s*0*" + this.animationDraw + "(?!\\d)");

      for (Slot var4 : this.compute(var1)) {
         ItemStack var5 = var4.getStack();
         if (!var5.isEmpty() && !var5.isOf(Items.ARMOR_STAND) && var2.matcher(this.handle(var5)).find()) {
            return var4;
         }
      }

      return null;
   }

   private List<Slot> compute(GenericContainerScreen var1) {
      ArrayList var2 = new ArrayList();
      ScreenHandler var3 = var1.getScreenHandler();

      for (Slot var5 : var3.slots) {
         if (toggleState.player == null || var5.inventory != toggleState.player.getInventory()) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private String handle(ItemStack var1) {
      StringBuilder var2 = new StringBuilder(var1.getName().getString());
      LoreComponent var3 = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (var3 != null) {
         for (Text var5 : var3.lines()) {
            var2.append(' ').append(var5.getString());
         }
      }

      return this.resolve(var2.toString());
   }

   private void handle(GenericContainerScreen var1, Slot var2) {
      this.handle(var1, var2, SlotActionType.QUICK_MOVE);
   }

   private void handle(GenericContainerScreen var1, Slot var2, SlotActionType var3) {
      toggleState.interactionManager.clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, var2.id, 0, var3, toggleState.player);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (this.pointEncode && var1.update() == PacketEvent.Mode.RECEIVE) {
         if (var1.resolve() instanceof GameMessageS2CPacket var2) {
            String var4 = this.resolve(var2.content().getString());
            if (!var4.isEmpty()) {
               if (!this.target || !var4.contains("вы уже подключены к этому серверу") && !var4.contains("already connected to this server")) {
                  if (this.compute(var4)) {
                     this.update("Подключение не выполнено: " + var2.content().getString());
                  }
               } else {
                  this.select();
               }
            }
         }
      }
   }

   private void execute() {
      PlayerInventory var1 = toggleState.player.getInventory();

      for (int var2 = 0; var2 < 9; var2++) {
         if (var1.getStack(var2).isOf(Items.COMPASS)) {
            if (var1.getSelectedSlot() != var2) {
               var1.setSelectedSlot(var2);
               toggleState.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var2));
            }

            toggleState.interactionManager.interactItem(toggleState.player, Hand.MAIN_HAND);
            return;
         }
      }
   }

   private void handle(long var1) {
      if (this.prepare() == this.animationDraw) {
         this.select();
      } else if (this.matrixBlend != 0L && var1 >= this.matrixBlend) {
         toggleState.player.networkHandler.sendChatCommand("an" + this.animationDraw);
         this.matrixBlend = 0L;
         this.previous = var1;
      } else {
         if (this.matrixBlend == 0L && var1 - this.previous >= 4000L) {
            this.update("FunTime не подтвердил подключение к анархии #" + this.animationDraw + ".");
         }
      }
   }

   private int prepare() {
      int var1 = this.check();
      if (this.process(var1)) {
         return var1;
      }

      NicknameUtil.instance.handle();
      return this.process(NicknameUtil.instance.compute());
   }

   private int check() {
      if (toggleState.world == null) {
         return -1;
      }

      Scoreboard var1 = toggleState.world.getScoreboard();
      ArrayList<String> var2 = new ArrayList<>();
      this.handle(var1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR), var1, var2);

      for (ScoreboardObjective var4 : var1.getObjectives()) {
         this.handle(var4, var1, var2);
      }

      for (String var9 : var2) {
         int var5 = this.handle(handler, var9);
         if (this.process(var5)) {
            return var5;
         }
      }

      for (String var10 : var2) {
         int var12 = this.handle(mode, var10);
         if (this.process(var12)) {
            return var12;
         }
      }

      for (String var11 : var2) {
         int var13 = this.handle(renderer, var11);
         if (this.process(var13)) {
            return var13;
         }
      }

      return -1;
   }

   private void handle(ScoreboardObjective var1, Scoreboard var2, List<String> var3) {
      if (var1 != null) {
         var3.add(var1.getDisplayName().getString());

         for (ScoreboardEntry var6 : var2.getScoreboardEntries(var1)) {
            Team var7 = var2.getScoreHolderTeam(var6.owner());
            var3.add(Team.decorateName(var7, Text.literal(var6.owner())).getString());
         }
      }
   }

   private boolean onTick() {
      if (toggleState.world == null) {
         return false;
      }

      Scoreboard var1 = toggleState.world.getScoreboard();
      ScoreboardObjective var2 = var1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      if (var2 != null && this.handle(var2.getDisplayName().getString())) {
         return true;
      }

      for (ScoreboardObjective var4 : var1.getObjectives()) {
         if (this.handle(var4.getDisplayName().getString())) {
            return true;
         }
      }

      return false;
   }

   private boolean handle(String var1) {
      String var2 = this.resolve(var1);
      return var2.contains("анархия-") || var2.contains("анархия #") || var2.contains("anarchy-");
   }

   private int handle(Pattern var1, String var2) {
      Matcher var3 = var1.matcher(this.resolve(var2));
      return !var3.find() ? -1 : this.process(var3.group(1));
   }

   private boolean process(int var1) {
      return var1 >= 1 && var1 <= 66;
   }

   private int process(String var1) {
      if (var1 == null) {
         return -1;
      }

      String var2 = var1.replaceAll("\\D+", "");
      if (var2.isEmpty()) {
         return -1;
      }

      try {
         return Integer.parseInt(var2);
      } catch (NumberFormatException var4) {
         return -1;
      }
   }

   private int compute(int var1) {
      if (var1 <= 15) {
         return 0;
      } else if (var1 <= 31) {
         return 1;
      } else {
         return var1 <= 47 ? 2 : 3;
      }
   }

   private boolean compute(String var1) {
      return var1.contains("сервер заполнен")
         || var1.contains("были кикнуты при подключении")
         || var1.contains("не удалось подключ")
         || var1.contains("ошибка подключения")
         || var1.contains("сервер недоступен")
         || var1.contains("нет свободных слотов")
         || var1.contains("failed to connect")
         || var1.contains("could not connect")
         || var1.contains("server is full")
         || var1.contains("server unavailable");
   }

   private String resolve(String var1) {
      return var1 == null ? "" : var1.replaceAll("(?i)§.", "").replace(' ', ' ').replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);
   }

   private void select() {
      this.refresh();
   }

   private void update(String var1) {
      ChatLogger.handle("§c[RCT] " + var1);
      this.refresh();
   }

   private void refresh() {
      this.pointEncode = false;
      this.animator = false;
      this.source = false;
      this.target = false;
      this.animationDraw = -1;
      this.pending = 0L;
      this.previous = 0L;
      this.latest = 0L;
      this.summary = 0L;
      this.matrixBlend = 0L;
   }

   private void render() {
      ChatLogger.handle("§cИспользование: " + this.compute());
      ChatLogger.handle("§7Без номера команда использует текущую анархию из scoreboard.");
   }

   static {
      Loader.initialize();
   }
}
