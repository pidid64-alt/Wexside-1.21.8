package ru.wild.network;

import java.awt.Color;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import ru.wild.WildClient;
import ru.wild.api.module.PlayerMarkerModule;
import ru.wild.core.MinecraftContext;
import ru.wild.core.manager.FriendManager;

public class IrcClient extends WebSocketClient {
   public static IrcClient instance;
   private static String output = "";
   private static volatile boolean current;
   private static volatile Thread active;
   private static final Map<String, String> mode = new HashMap<>();
   public static final Map<String, String> data = new ConcurrentHashMap<>();
   public static final Map<String, IrcClient.CacheEntry> context = new ConcurrentHashMap<>();
   public static final Map<String, IrcClient.PrimaryCacheEntry> config = new ConcurrentHashMap<>();
   public static final Map<String, Integer> state = new ConcurrentHashMap<>();
   public static final Map<String, Long> cache = new ConcurrentHashMap<>();
   private static String selection = null;

   public static String handle() {
      if (MinecraftContext.toggleState.getCurrentServerEntry() != null) {
         String var0 = MinecraftContext.toggleState.getCurrentServerEntry().address.toLowerCase();
         int var1 = var0.indexOf(58);
         if (var1 != -1) {
            var0 = var0.substring(0, var1);
         }

         if (!var0.matches("^\\d{1,3}(\\.\\d{1,3}){3}$") && !var0.equals("localhost")) {
            String[] var2 = var0.split("\\.");
            return var2.length >= 2 ? var2[var2.length - 2] + "." + var2[var2.length - 1] : var0;
         } else {
            return var0;
         }
      } else {
         return MinecraftContext.toggleState.isInSingleplayer() ? "singleplayer" : "unknown";
      }
   }

   public IrcClient(URI var1) {
      super(var1);
      this.setDaemon(true);
      current = false;
      instance = this;
   }

   public static void process() {
      current = true;
      Thread var0 = active;
      active = null;
      if (var0 != null) {
         var0.interrupt();
      }

      IrcClient var1 = instance;
      instance = null;
      output = "";
      if (var1 != null) {
         try {
            var1.close();
         } catch (Throwable var3) {
         }
      }
   }

   public void onOpen(ServerHandshake var1) {
      System.out.println("[IRC] Успешно подключено к серверу! Ждем проверку...");
      handle(() -> {
         if (MinecraftContext.toggleState.player != null) {
            MinecraftContext.toggleState.player.sendMessage(Text.literal("§a[IRC] Соединение установлено, проверка..."), false);
         }
      });
   }

   public void compute() {
      if (this.isOpen() && MinecraftContext.toggleState.getSession() != null) {
         String var1 = MinecraftContext.toggleState.getSession().getUsername();
         if (!var1.equals(output)) {
            output = var1;

            try {
               JSONObject var2 = new JSONObject();
               var2.put("type", "handshake");
               var2.put("user", var1);
               var2.put("client", WildClient.instance != null && WildClient.instance.render() != null ? WildClient.instance.render() : "LitkaFree");
               this.send(var2.toString());
            } catch (Exception var3) {
            }
         }
      }
   }

   public void handle(double var1, double var3, double var5, String var7, float var8, String var9, boolean var10) {
      if (this.isOpen() && MinecraftContext.toggleState.getSession() != null) {
         try {
            JSONObject var11 = new JSONObject();
            var11.put("type", "pos_sync");
            var11.put("user", MinecraftContext.toggleState.getSession().getUsername());
            var11.put("client", WildClient.instance != null && WildClient.instance.render() != null ? WildClient.instance.render() : "LitkaFree");
            var11.put("x", var1);
            var11.put("y", var3);
            var11.put("z", var5);
            var11.put("dim", var7);
            var11.put("hp", var8);
            var11.put("server", handle());
            var11.put("anarchy", var9);
            var11.put("pvp", var10);
            this.send(var11.toString());
         } catch (Exception var12) {
         }
      }
   }

   public void onMessage(String var1) {
      handle(() -> this.handle(var1));
   }

   private void handle(String var1) {
      try {
         JSONObject var2 = new JSONObject(var1);
         if (var2.has("sys_msg")) {
            if (MinecraftContext.toggleState.player != null) {
               MinecraftContext.toggleState.player.sendMessage(Text.literal(var2.getString("sys_msg")), false);
            }

            return;
         }

         String var3 = var2.has("type") ? var2.getString("type") : "";
         if ("challenge".equals(var3)) {
            String var23 = var2.getString("salt");
            String var28 = "AiJgW2femCr4LFbNEqbMWVYX3SblusdD1TbUbPeoVarZCRQQnZ";
            String var33 = resolve();
            MessageDigest var36 = MessageDigest.getInstance("SHA-256");
            String var38 = var23 + var28 + var33;
            byte[] var40 = var36.digest(var38.getBytes(StandardCharsets.UTF_8));
            StringBuilder var42 = new StringBuilder();

            for (byte var52 : var40) {
               String var53 = Integer.toHexString(255 & var52);
               if (var53.length() == 1) {
                  var42.append('0');
               }

               var42.append(var53);
            }

            output = MinecraftContext.toggleState.getSession() != null ? MinecraftContext.toggleState.getSession().getUsername() : "Unknown";
            JSONObject var45 = new JSONObject();
            var45.put("type", "handshake");
            var45.put("user", output);
            var45.put("client", WildClient.instance != null && WildClient.instance.render() != null ? WildClient.instance.render() : "LitkaFree");
            var45.put("hwid", var33);
            var45.put("hash", var42.toString());
            this.send(var45.toString());
            if (MinecraftContext.toggleState.player != null) {
               MinecraftContext.toggleState.player.sendMessage(Text.literal("§a[IRC] Успешно авторизовано!"), false);
            }

            return;
         }

         if ("sync".equals(var3)) {
            JSONObject var22 = var2.getJSONObject("users");
            data.clear();
            Iterator var27 = var22.keys();

            while (var27.hasNext()) {
               String var32 = (String)var27.next();
               data.put(var32, var22.getString(var32));
            }

            return;
         }

         if ("pos_sync".equals(var3)) {
            String var21 = var2.getString("user");
            if (this.process(var21)) {
               return;
            }

            String var26 = var2.getString("client");
            double var31 = var2.getDouble("x");
            double var37 = var2.getDouble("y");
            double var41 = var2.getDouble("z");
            String var47 = var2.getString("dim");
            float var50 = (float)var2.getDouble("hp");
            String var14 = var2.has("server") ? var2.getString("server") : "unknown";
            String var15 = var2.has("anarchy") ? var2.getString("anarchy") : "N/A";
            boolean var16 = var2.has("pvp") && var2.getBoolean("pvp");
            String var17 = var21.toLowerCase();
            if (context.containsKey(var17)) {
               context.get(var17).handle(var31, var37, var41, var47, var50, var14, var15, var16);
            } else {
               context.put(var17, new IrcClient.CacheEntry(var21, var26, var31, var37, var41, var47, var50, var14, var15, var16));
            }

            return;
         }

         if ("target_sync".equals(var3)) {
            String var20 = var2.getString("user");
            if (this.process(var20)) {
               return;
            }

            String var25 = var2.getString("target");
            String var30 = var2.has("server") ? var2.getString("server") : "unknown";
            double var35 = var2.has("x") ? var2.getDouble("x") : 0.0;
            double var39 = var2.has("y") ? var2.getDouble("y") : 0.0;
            double var43 = var2.has("z") ? var2.getDouble("z") : 0.0;
            if (var25.isEmpty()) {
               config.remove(var20);
            } else if (config.containsKey(var20)) {
               config.get(var20).handle(var35, var39, var43, var30, var25);
            } else {
               config.put(var20, new IrcClient.PrimaryCacheEntry(var25, var30, var35, var39, var43));
            }

            return;
         }

         if ("totem_pop".equals(var3)) {
            String var19 = var2.has("attacker") ? var2.getString("attacker") : "";
            if (this.process(var19)) {
               return;
            }

            String var24 = var2.getString("victim");
            int var29 = var2.getInt("count");
            String var34 = var2.has("server") ? var2.getString("server") : "unknown";
            if (var34.equals(handle())) {
               state.put(var24, var29);
               cache.put(var24, System.currentTimeMillis());
            }

            return;
         }

         if ("chat".equals(var3)) {
            String var4 = var2.has("user") ? var2.getString("user") : "Unknown";
            String var5 = var2.has("msg") ? var2.getString("msg") : "";
            String var6 = var2.has("client") ? var2.getString("client") : "LitkaFree";
            String var7 = var2.has("role") ? var2.getString("role") : "User";
            data.put(var4, var6);
            String var8 = SocialLinks.context != null ? SocialLinks.context : "";
            String var9 = MinecraftContext.toggleState.getSession() != null ? MinecraftContext.toggleState.getSession().getUsername() : "Unknown";
            if (mode.containsKey(var8)) {
               var9 = mode.get(var8);
            }

            boolean var10 = var5.toLowerCase().contains("@" + var9.toLowerCase()) || var5.toLowerCase().contains(var9.toLowerCase());
            MutableText var11 = Text.empty();
            var11.append(Text.literal("§8["));
            if (var6.toLowerCase().contains("wild")) {
               var11.append(this.handle(var6, Color.DARK_GRAY, Color.WHITE));
            } else if (var6.toLowerCase().contains("nightix")) {
               var11.append(this.handle(var6, Color.WHITE, new Color(85, 85, 255)));
            } else {
               var11.append(Text.literal(var6).formatted(Formatting.AQUA));
            }

            var11.append(Text.literal("§8] "));
            switch (var7) {
               case "Developer":
                  var11.append(Text.literal("§8["))
                     .append(this.handle("Developer", new Color(170, 0, 255), new Color(255, 85, 255)))
                     .append(Text.literal("§8] "));
                  break;
               case "Admin":
                  var11.append(Text.literal("§8[")).append(this.handle("Admin", new Color(255, 85, 85), new Color(170, 0, 0))).append(Text.literal("§8] "));
            }

            switch (var7) {
               case "Developer":
                  var11.append(this.handle(var4, new Color(85, 255, 255), new Color(85, 85, 255)));
                  break;
               case "Admin":
                  var11.append(Text.literal("§c" + var4));
                  break;
               default:
                  var11.append(Text.literal("§7" + var4));
            }

            var11.append(Text.literal(" §8» "));
            if (var10) {
               var11.append(this.handle(var5, new Color(85, 255, 85), new Color(255, 170, 0)));
               if (MinecraftContext.toggleState.player != null) {
                  MinecraftContext.toggleState.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
               }
            } else {
               var11.append(Text.literal("§f" + var5));
            }

            if (MinecraftContext.toggleState.player != null) {
               MinecraftContext.toggleState.player.sendMessage(var11, false);
            }
         }
      } catch (Exception var18) {
         System.err.println("[IRC] Ошибка парсинга пакета: " + var18.getMessage());
         var18.printStackTrace();
      }
   }

   public void onClose(int var1, String var2, boolean var3) {
      if (!current) {
         if (MinecraftContext.toggleState.player != null) {
         }

         Thread var4 = new Thread(() -> {
            boolean var6 = false /* VF: Semaphore variable */;

            label100: {
               label107: {
                  label108: {
                     try {
                        var6 = true;
                        Thread.sleep(5000L);
                        if (!current) {
                           if (!WildClient.performVector()) {
                              if (WildClient.instance != null) {
                                 if (WildClient.instance.data != null) {
                                    PlayerMarkerModule var1x = WildClient.instance.data.handle(PlayerMarkerModule.class);
                                    if (var1x != null) {
                                       if (var1x.enabled) {
                                          this.reconnectBlocking();
                                          var6 = false;
                                       } else {
                                          var6 = false;
                                       }
                                    } else {
                                       var6 = false;
                                    }
                                 } else {
                                    var6 = false;
                                 }
                              } else {
                                 var6 = false;
                              }
                              break label100;
                           }

                           var6 = false;
                        } else {
                           var6 = false;
                        }
                        break label107;
                     } catch (InterruptedException var7) {
                        Thread.currentThread().interrupt();
                        var6 = false;
                     } catch (Exception var8) {
                        var6 = false;
                        break label108;
                     } finally {
                        if (var6) {
                           if (Thread.currentThread() == active) {
                              active = null;
                           }
                        }
                     }

                     if (Thread.currentThread() == active) {
                        active = null;
                     }

                     return;
                  }

                  if (Thread.currentThread() == active) {
                     active = null;
                  }

                  return;
               }

               if (Thread.currentThread() == active) {
                  active = null;
               }

               return;
            }

            if (Thread.currentThread() == active) {
               active = null;
            }
         }, "IRC-Reconnect-Thread");
         var4.setDaemon(true);
         active = var4;
         var4.start();
      }
   }

   public void onError(Exception var1) {
   }

   private static void handle(Runnable var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && !var1.isOnThread()) {
         var1.execute(var0);
      } else {
         var0.run();
      }
   }

   public void handle(String var1, String var2) {
      if (this.isOpen()) {
         try {
            JSONObject var3 = new JSONObject();
            String var4 = SocialLinks.context != null ? SocialLinks.context : "";
            String var5 = mode.getOrDefault(var4, var1);
            var3.put("type", "chat");
            var3.put("user", var5);
            var3.put("msg", var2);
            var3.put("client", WildClient.instance != null && WildClient.instance.render() != null ? WildClient.instance.render() : "LitkaFree");
            var3.put("discordId", var4);
            this.send(var3.toString());
         } catch (Exception var6) {
         }
      }
   }

   private MutableText handle(String var1, Color var2, Color var3) {
      MutableText var4 = Text.empty();
      int var5 = var1.length();

      for (int var6 = 0; var6 < var5; var6++) {
         float var7 = var5 > 1 ? (float)var6 / (var5 - 1) : 0.0F;
         int var8 = (int)(var2.getRed() * (1.0F - var7) + var3.getRed() * var7);
         int var9 = (int)(var2.getGreen() * (1.0F - var7) + var3.getGreen() * var7);
         int var10 = (int)(var2.getBlue() * (1.0F - var7) + var3.getBlue() * var7);
         TextColor var11 = TextColor.fromRgb(var8 << 16 | var9 << 8 | var10);
         var4.append(Text.literal(String.valueOf(var1.charAt(var6))).styled(var1x -> var1x.withColor(var11)));
      }

      return var4;
   }

   public static String resolve() {
      if (selection != null) {
         return selection;
      }

      try {
         String var0 = System.getenv("COMPUTERNAME")
            + System.getProperty("user.name")
            + System.getenv("PROCESSOR_IDENTIFIER")
            + System.getenv("PROCESSOR_LEVEL");
         MessageDigest var1 = MessageDigest.getInstance("MD5");
         byte[] var2 = var1.digest(var0.getBytes());
         StringBuilder var3 = new StringBuilder();

         for (byte var7 : var2) {
            var3.append(String.format("%02X", var7));
         }

         selection = var3.toString();
         return selection;
      } catch (Exception var8) {
         return "FALLBACK_HWID_" + System.currentTimeMillis();
      }
   }

   private boolean process(String var1) {
      if (var1 == null || var1.isEmpty()) {
         return false;
      }

      if (MinecraftContext.toggleState.getSession() != null && var1.equals(MinecraftContext.toggleState.getSession().getUsername())) {
         return false;
      }

      if (WildClient.instance != null && WildClient.instance.data != null) {
         PlayerMarkerModule var2 = WildClient.instance.data.handle(PlayerMarkerModule.class);
         if (var2 != null && "Только у друзей".equals(var2.source.compute())) {
            return !FriendManager.handle(var1);
         }
      }

      return false;
   }

   static {
      mode.put("811282287772565514", "fr1zy1337");
      mode.put("1386776511520178290", "Chaser");
      mode.put("1142359429090648134", "safurai4ik");
   }

   public static class CacheEntry {
      public String instance;
      public String data;
      public String context;
      public String config;
      public String state;
      public boolean cache;
      public double output;
      public double current;
      public double active;
      public double mode;
      public double selection;
      public double enabled;
      public float renderer;
      public long handler;

      public CacheEntry(String var1, String var2, double var3, double var5, double var7, String var9, float var10, String var11, String var12, boolean var13) {
         this.instance = var1;
         this.data = var2;
         this.output = this.mode = var3;
         this.current = this.selection = var5;
         this.active = this.enabled = var7;
         this.context = var9;
         this.renderer = var10;
         this.config = var11;
         this.state = var12;
         this.cache = var13;
         this.handler = System.currentTimeMillis();
      }

      public void handle(double var1, double var3, double var5, String var7, float var8, String var9, String var10, boolean var11) {
         this.mode = this.output;
         this.selection = this.current;
         this.enabled = this.active;
         this.output = var1;
         this.current = var3;
         this.active = var5;
         this.context = var7;
         this.renderer = var8;
         this.config = var9;
         this.state = var10;
         this.cache = var11;
         this.handler = System.currentTimeMillis();
      }
   }

   public static class PrimaryCacheEntry {
      public String instance;
      public String data;
      public double context;
      public double config;
      public double state;
      public double cache;
      public double output;
      public double current;
      public long active;

      public PrimaryCacheEntry(String var1, String var2, double var3, double var5, double var7) {
         this.instance = var1;
         this.data = var2;
         this.context = this.cache = var3;
         this.config = this.output = var5;
         this.state = this.current = var7;
         this.active = System.currentTimeMillis();
      }

      public void handle(double var1, double var3, double var5, String var7, String var8) {
         this.cache = this.context;
         this.output = this.config;
         this.current = this.state;
         this.context = var1;
         this.config = var3;
         this.state = var5;
         this.data = var7;
         this.instance = var8;
         this.active = System.currentTimeMillis();
      }
   }
}
