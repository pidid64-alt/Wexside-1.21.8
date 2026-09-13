package ru.wild.util.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.wild.mixin.acceser.BossBarHudAccessor;
import ru.wild.core.MinecraftContext;

public class NicknameUtil {
   public static final NicknameUtil instance = new NicknameUtil();
   private static final Pattern context = Pattern.compile("(?iu)(?:анарх(?:ия|ии)?|anarchy|an)\\s*[-:#№]?\\s*(\\d{1,5})");
   private static final Pattern config = Pattern.compile("([a-zA-Z0-9_]{3,16})");
   private static final Pattern state = Pattern.compile("Монет:\\s*(.+)");
   private static final Pattern cache = Pattern.compile("Токенов:\\s*(\\d+)");
   private static final Pattern output = Pattern.compile("Ранг:\\s*(.+)");
   private static final Pattern current = Pattern.compile("Убийств:\\s*(\\d+)");
   private static final Pattern active = Pattern.compile("Смертей:\\s*(\\d+)");
   private static final Pattern mode = Pattern.compile("Наиграно:\\s*(.+)");
   public static String data = "N/A";
   private String selection = "N/A";
   private String enabled = "N/A";
   private String renderer = "N/A";
   private String handler = "0";
   private String animationDraw = "0";
   private String pointEncode = "0";
   private String animator = "0";
   private String source = "0";
   private long target;

   public void handle(long var1) {
      long var3 = System.currentTimeMillis();
      if (var3 - this.target >= var1) {
         this.target = var3;
         this.handle();
      }
   }

   public void handle() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      this.selection = "N/A";
      this.handler = "0";
      this.animationDraw = "0";
      this.pointEncode = "0";
      this.animator = "0";
      this.source = "0";
      if (var1.world != null && var1.player != null) {
         Scoreboard var2 = var1.world.getScoreboard();
         ScoreboardObjective var3 = var2.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
         if (var3 != null) {
            String var4 = var3.getDisplayName().getString();
            Matcher var5 = context.matcher(this.handle(var4));
            if (var5.find()) {
               this.selection = var5.group(1);
            }

            List var6 = this.handle(var2, var3);

            for (int var7 = 0; var7 < var6.size(); var7++) {
               String var8 = (String)var6.get(var7);
               String var9 = this.handle(var8);
               if ("N/A".equals(this.selection)) {
                  Matcher var10 = context.matcher(var9);
                  if (var10.find()) {
                     this.selection = var10.group(1);
                  }
               }

               if (var7 < 5 && !var9.contains(":") && !var9.contains("=") && !var9.trim().isEmpty()) {
                  Matcher var16 = config.matcher(var9);
                  if (var16.find()) {
                     this.enabled = var16.group(1);
                     data = this.enabled;
                  }
               }

               Matcher var17 = output.matcher(var9);
               if (var17.find()) {
                  this.renderer = var17.group(1).trim();
               }

               Matcher var11 = state.matcher(var9);
               if (var11.find()) {
                  String var12 = var11.group(1);
                  this.handler = var12.replaceAll("[^0-9]", "");
               }

               Matcher var18 = cache.matcher(var9);
               if (var18.find()) {
                  this.animationDraw = var18.group(1);
               }

               Matcher var13 = current.matcher(var9);
               if (var13.find()) {
                  this.pointEncode = var13.group(1);
               }

               Matcher var14 = active.matcher(var9);
               if (var14.find()) {
                  this.animator = var14.group(1);
               }

               Matcher var15 = mode.matcher(var9);
               if (var15.find()) {
                  this.source = var15.group(1);
               }
            }
         }
      }
   }

   private List<String> handle(Scoreboard var1, ScoreboardObjective var2) {
      ArrayList var3 = new ArrayList();
      Collection var4 = var1.getScoreboardEntries(var2);
      ArrayList var5 = new ArrayList(var4);
      var5.sort(Comparator.comparingInt(ScoreboardEntry::value).reversed());
      int var6 = Math.min(var5.size(), 15);

      for (int var7 = 0; var7 < var6; var7++) {
         ScoreboardEntry var8 = (ScoreboardEntry)var5.get(var7);
         Team var9 = var1.getScoreHolderTeam(var8.owner());
         var3.add(Team.decorateName(var9, Text.literal(var8.owner())).getString());
      }

      return var3;
   }

   private String handle(String var1) {
      return var1 == null ? "" : var1.replaceAll("(?i)§[0-9a-fk-or]", "").trim();
   }

   public static boolean process() {
      if (MinecraftContext.toggleState.inGameHud != null && MinecraftContext.toggleState.inGameHud.getBossBarHud() != null) {
         Map<java.util.UUID, ClientBossBar> var0 = ((BossBarHudAccessor)MinecraftContext.toggleState.inGameHud.getBossBarHud()).getBossBars();

         for (ClientBossBar var2 : var0.values()) {
            String var3 = var2.getName().getString().toLowerCase();
            if (var3.contains("pvp-режим") || var3.contains("пвп")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
   public String compute() {
      return this.selection;
   }
   public String resolve() {
      return this.enabled;
   }
   public String update() {
      return this.renderer;
   }
   public String apply() {
      return this.handler;
   }
   public String execute() {
      return this.animationDraw;
   }
   public String prepare() {
      return this.pointEncode;
   }
   public String check() {
      return this.animator;
   }
   public String onTick() {
      return this.source;
   }
   public long select() {
      return this.target;
   }
}
