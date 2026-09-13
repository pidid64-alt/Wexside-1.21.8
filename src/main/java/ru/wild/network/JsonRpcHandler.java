package ru.wild.network;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonRpcHandler extends WebSocketClient {
   public static JsonRpcHandler instance;
   private static volatile boolean config;
   private static volatile Thread state;
   public static final Map<String, JsonRpcHandler.CacheEntry> data = new ConcurrentHashMap<>();
   public static volatile Consumer<JSONObject> context;
   private static volatile List<String> cache = new ArrayList<>();
   private static volatile boolean output;
   private static String current = "";
   private final String active;
   private final String mode;

   public JsonRpcHandler(String var1, String var2) {
      super(URI.create(var1));
      this.setDaemon(true);
      this.active = var1;
      this.mode = var2;
      config = false;
      instance = this;
   }

   public static void handle() {
      config = true;
      Thread var0 = state;
      state = null;
      if (var0 != null) {
         var0.interrupt();
      }

      JsonRpcHandler var1 = instance;
      instance = null;
      output = false;
      current = "";
      if (var1 != null) {
         try {
            var1.close();
         } catch (Throwable var3) {
         }
      }
   }

   public void onOpen(ServerHandshake var1) {
      current = "";
   }

   public void onMessage(String var1) {
      handle(() -> this.process(var1));
   }

   private void process(String var1) {
      try {
         JSONObject var2 = new JSONObject(var1);
         String var3 = var2.has("op") ? var2.getString("op") : "";
         switch (var3) {
            case "party_state":
               JSONArray var16 = var2.optJSONArray("members");
               ArrayList var17 = new ArrayList();
               if (var16 != null) {
                  for (int var18 = 0; var18 < var16.length(); var18++) {
                     var17.add(var16.getString(var18));
                  }
               }

               cache = List.copyOf(var17);
               data.clear();
               JSONArray var19 = var2.optJSONArray("markers");
               if (var19 != null) {
                  for (int var9 = 0; var9 < var19.length(); var9++) {
                     JSONObject var10 = var19.getJSONObject(var9);
                     data.put(
                        var10.getString("owner").toLowerCase(),
                        new JsonRpcHandler.CacheEntry(
                           var10.getString("owner"),
                           var10.optString("target", ""),
                           var10.getDouble("x"),
                           var10.getDouble("y"),
                           var10.getDouble("z"),
                           var10.optBoolean("entity", false)
                        )
                     );
                  }
               }

               output = true;
               break;
            case "member_update":
               JSONArray var15 = var2.optJSONArray("members");
               if (var15 == null) {
                  break;
               }

               ArrayList var7 = new ArrayList();

               for (int var8 = 0; var8 < var15.length(); var8++) {
                  var7.add(var15.getString(var8));
               }

               cache = List.copyOf(var7);
               break;
            case "member_left":
               String var14 = var2.optString("owner", "");
               if (!var14.isEmpty()) {
                  cache = cache.stream().filter(var1x -> !var1x.equalsIgnoreCase(var14)).collect(Collectors.toList());
               }

               data.remove(var14.toLowerCase());
               break;
            case "marker_update":
               String var6 = var2.getString("owner");
               data.put(
                  var6.toLowerCase(),
                  new JsonRpcHandler.CacheEntry(
                     var6, var2.optString("target", ""), var2.getDouble("x"), var2.getDouble("y"), var2.getDouble("z"), var2.optBoolean("entity", false)
                  )
               );
               break;
            case "marker_remove":
               data.remove(var2.getString("owner").toLowerCase());
               break;
            case "party_closed":
            case "kicked":
               data.clear();
               break;
            case "error":
               System.out.println("[PartyWS] Ошибка: " + var2.optString("msg"));
         }

         Consumer var13 = context;
         if (var13 != null) {
            try {
               var13.accept(var2);
            } catch (Exception var11) {
            }
         }
      } catch (Exception var12) {
      }
   }

   public void handle(String var1) {
      if (this.isOpen()) {
         if (var1 != null && !var1.isEmpty()) {
            if (!var1.equals(current)) {
               current = var1;

               try {
                  JSONObject var2 = new JSONObject();
                  var2.put("op", "auth");
                  var2.put("user", this.mode);
                  var2.put("party_id", var1);
                  this.send(var2.toString());
               } catch (Exception var3) {
               }
            }
         }
      }
   }

   public static void handle(JsonRpcHandler var0, String var1, String... var2) {
      if (var0 != null && var0.isOpen()) {
         try {
            JSONObject var3 = new JSONObject();
            var3.put("op", var1);
            var3.put("user", var0.mode);
            if (var2.length >= 1 && var2[0] != null && !var2[0].isEmpty()) {
               if ("join".equals(var1)) {
                  var3.put("code", var2[0]);
               } else if ("kick".equals(var1)) {
                  var3.put("target", var2[0]);
               }
            }

            var0.send(var3.toString());
         } catch (Exception var4) {
         }
      }
   }

   public void handle(double var1, double var3, double var5, boolean var7, String var8) {
      if (this.isOpen()) {
         try {
            JSONObject var9 = new JSONObject();
            var9.put("op", "set_marker");
            var9.put("user", this.mode);
            var9.put("x", var1);
            var9.put("y", var3);
            var9.put("z", var5);
            var9.put("entity", var7);
            var9.put("target", var8 == null ? "" : var8);
            this.send(var9.toString());
         } catch (Exception var10) {
         }
      }
   }

   public void process() {
      if (this.isOpen()) {
         try {
            JSONObject var1 = new JSONObject();
            var1.put("op", "clear_marker");
            var1.put("user", this.mode);
            this.send(var1.toString());
         } catch (Exception var2) {
         }
      }
   }

   public void compute() {
      if (this.isOpen()) {
         try {
            JSONObject var1 = new JSONObject();
            var1.put("op", "ping");
            this.send(var1.toString());
         } catch (Exception var2) {
         }
      }
   }

   public void onClose(int var1, String var2, boolean var3) {
      if (!config) {
         output = false;
         current = "";
         JsonRpcHandler var4 = this;
         Thread var5 = new Thread(() -> {
            try {
               Thread.sleep(5000L);
               if (!config) {
                  if (instance == var4 && !var4.isOpen()) {
                     try {
                        var4.reconnectBlocking();
                     } catch (InterruptedException var7) {
                        Thread.currentThread().interrupt();
                     } catch (Exception var8) {
                     }

                     return;
                  }

                  return;
               }
            } catch (InterruptedException var9) {
               Thread.currentThread().interrupt();
               return;
            } finally {
               if (Thread.currentThread() == state) {
                  state = null;
               }
            }
         }, "PartyWS-Reconnect-Thread");
         var5.setDaemon(true);
         state = var5;
         var5.start();
      }
   }

   public void onError(Exception var1) {
   }

   public boolean resolve() {
      return output;
   }

   public static void update() {
      current = "";
   }

   public static List<String> apply() {
      return cache;
   }

   private static void handle(Runnable var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && !var1.isOnThread()) {
         var1.execute(var0);
      } else {
         var0.run();
      }
   }

   public static class CacheEntry {
      public String instance;
      public String data;
      public double context;
      public double config;
      public double state;
      public long cache;
      public boolean output;

      public CacheEntry(String var1, String var2, double var3, double var5, double var7, boolean var9) {
         this.instance = var1;
         this.data = var2 == null ? "" : var2;
         this.context = var3;
         this.config = var5;
         this.state = var7;
         this.cache = System.currentTimeMillis();
         this.output = var9;
      }
   }
}
