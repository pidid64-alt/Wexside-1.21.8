package ru.wild.config;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.misc.UnHook;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.SmoothTimer;

public final class ConfigProfile implements JsonStateCodec {
   private final String cache;
   private final File output;
   public SmoothTimer instance = new EaseTimer(500, 1.0);
   public SmoothTimer data = new EaseTimer(300, 1.0);
   public SmoothTimer context = new EaseTimer(300, 1.0);
   public SmoothTimer config = new EaseTimer(300, 1.0);
   public SmoothTimer state = new EaseTimer(500, 1.0);

   public ConfigProfile(String var1) {
      this.cache = var1;
      this.output = new File(ConfigManager.instance, var1 + ".json");
      if (!this.output.exists()) {
         try {
            File var2 = this.output.getParentFile();
            if (var2 != null && !var2.exists() && !var2.mkdirs()) {
               System.out.println("[Config] Warning: failed to create parent dir for " + var1);
            }

            if (!this.output.createNewFile()) {
               System.out.println("[Config] Warning: failed to create file " + this.output.getAbsolutePath());
            }
         } catch (Exception var3) {
            System.out.println("[Config] Cannot create config '" + var1 + "': " + var3.getMessage());
         }
      }
   }

   public File handle() {
      return this.output;
   }

   public String process() {
      return this.cache;
   }

   @Override
   public JsonObject compute() {
      JsonObject var1 = new JsonObject();
      JsonObject var2 = new JsonObject();

      for (Module var4 : WildClient.instance.data.instance) {
         JsonObject var5 = var4.serialize();
         this.handle(var4, var5);
         var2.add(var4.displayName, var5);
      }

      var1.add("Features", var2);
      JsonObject var7 = new JsonObject();

      for (Entry var10 : ThemeRenderer.handle().update().entrySet()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("x", ((ThemeRenderer.DataRecord)var10.getValue()).nx());
         var6.addProperty("y", ((ThemeRenderer.DataRecord)var10.getValue()).ny());
         var6.addProperty("scaleX", ((ThemeRenderer.DataRecord)var10.getValue()).scaleX());
         var6.addProperty("scaleY", ((ThemeRenderer.DataRecord)var10.getValue()).scaleY());
         var6.addProperty("resized", ((ThemeRenderer.DataRecord)var10.getValue()).userResized());
         var7.add((String)var10.getKey(), var6);
      }

      var1.add("DraggablePositions", var7);
      JsonObject var9 = new JsonObject();

      for (Entry var12 : ThemeRenderer.handle().apply().entrySet()) {
         var9.addProperty((String)var12.getKey(), (Number)var12.getValue());
      }

      var1.add("PendingDraggableScales", var9);
      var1.add("HUDSettings", HudProfileConfig.update());
      return var1;
   }

   @Override
   public void handle(JsonObject var1) {
      System.out.println("[Config] Loading config: " + this.cache);
      if (var1 != null) {
         boolean var2 = false;
         if (var1.has("Features")) {
            JsonObject var3;
            try {
               var3 = var1.getAsJsonObject("Features");
            } catch (Throwable var18) {
               System.out.println("[Config] 'Features' object malformed, skipping");
               var3 = null;
            }

            if (var3 != null) {
               this.process(var3);
               int var4 = 0;

               for (Module var6 : WildClient.instance.data.instance) {
                  try {
                     if (var6.enabled) {
                        var6.setEnabled(false);
                     }

                     if (WildClient.instance.data.handle(var6) && var3.has(var6.displayName)) {
                        JsonObject var7 = null;

                        try {
                           var7 = var3.getAsJsonObject(var6.displayName);
                        } catch (Throwable var16) {
                        }

                        if (var7 != null) {
                           var2 |= this.handle(var6, var7);
                           var6.handle(var7);
                        }

                        if (var6.enabled) {
                           var4++;
                        }
                     }
                  } catch (Throwable var17) {
                     System.out.println("[Config] Failed to load module '" + var6.displayName + "': " + var17.getMessage());
                  }
               }
            }
         }

         if (var1.has("HUDSettings")) {
            try {
               HudProfileConfig.handle(var1.getAsJsonObject("HUDSettings"));
            } catch (Throwable var15) {
               System.out.println("[Config] Failed to load HUD settings: " + var15.getMessage());
            }
         }

         if (var1.has("DraggablePositions")) {
            JsonObject var20 = var1.getAsJsonObject("DraggablePositions");
            HashMap var22 = new HashMap();

            for (String var26 : var20.keySet()) {
               JsonObject var28 = var20.getAsJsonObject(var26);
               if (var28.has("x") && var28.has("y")) {
                  float var8 = var28.get("x").getAsFloat();
                  float var9 = var28.get("y").getAsFloat();
                  float var10 = var28.has("scaleX") ? var28.get("scaleX").getAsFloat() : 1.0F;
                  float var11 = var28.has("scaleY") ? var28.get("scaleY").getAsFloat() : 1.0F;
                  if (var10 > 10.0F || var10 <= 0.0F) {
                     var10 = 1.0F;
                  }

                  if (var11 > 10.0F || var11 <= 0.0F) {
                     var11 = 1.0F;
                  }

                  boolean var12 = var28.has("resized") && var28.get("resized").getAsBoolean();

                  try {
                     var22.put(var26, new ThemeRenderer.DataRecord(var8, var9, var10, var11, var12));
                  } catch (Exception var14) {
                     System.out.println("[Config] Failed to load position for: " + var26);
                  }
               }
            }

            ThemeRenderer.handle().handle(var22);
            System.out.println("[Config] Loaded " + var22.size() + " draggable positions");
         }

         HashMap var21 = new HashMap();
         if (var1.has("PendingDraggableScales")) {
            try {
               JsonObject var23 = var1.getAsJsonObject("PendingDraggableScales");

               for (String var27 : var23.keySet()) {
                  float var29 = var23.get(var27).getAsFloat();
                  if (Float.isFinite(var29) && var29 > 0.0F && var29 <= 10.0F) {
                     var21.put(var27, var29);
                  }
               }
            } catch (Throwable var19) {
            }
         }

         ThemeRenderer.handle().process(var21);
         if (var2 && WildClient.instance != null && WildClient.instance.renderer != null) {
            WildClient.instance.renderer.process(this.cache);
         }
      }
   }

   private void process(JsonObject var1) {
      if (var1 != null && var1.has("NoRender")) {
         JsonObject var2 = null;

         try {
            var2 = var1.getAsJsonObject("NoRender");
         } catch (Throwable var17) {
         }

         if (var2 != null && var2.has("Settings")) {
            JsonObject var3 = null;

            try {
               var3 = var2.getAsJsonObject("Settings");
            } catch (Throwable var16) {
            }

            if (var3 != null) {
               boolean var4 = false;

               try {
                  var4 = var2.has("enable") && var2.get("enable").getAsBoolean();
               } catch (Throwable var19) {
               }

               if (var4) {
                  JsonObject var5 = null;

                  try {
                     var5 = var1.has("Removals") ? var1.getAsJsonObject("Removals") : new JsonObject();
                  } catch (Throwable var15) {
                  }

                  if (var5 == null) {
                     var5 = new JsonObject();
                  }

                  JsonObject var6 = null;

                  try {
                     var6 = var5.has("Settings") ? var5.getAsJsonObject("Settings") : new JsonObject();
                  } catch (Throwable var14) {
                  }

                  if (var6 == null) {
                     var6 = new JsonObject();
                  }

                  boolean var7 = false;

                  try {
                     var7 = var5.has("enable") && var5.get("enable").getAsBoolean();
                  } catch (Throwable var18) {
                  }

                  if (var2.has("enable") && !var5.has("enable")) {
                     try {
                        var5.add("enable", var2.get("enable").deepCopy());
                     } catch (Throwable var13) {
                     }
                  }

                  if (!var7) {
                     var6.addProperty("Убрать траву", false);
                     var6.addProperty("Убрать растения", false);
                     var6.addProperty("Убрать стойки", false);
                     var6.addProperty("Убрать рамки", false);
                     var6.addProperty("Убрать картины", false);
                     var6.addProperty("Убрать дроп", false);
                     var6.addProperty("Убрать опыт", false);
                     var6.addProperty("Откл. диктор", false);
                  }

                  for (String var9 : var3.keySet()) {
                     if (!var6.has(var9)) {
                        try {
                           var6.add(var9, var3.get(var9).deepCopy());
                        } catch (Throwable var12) {
                        }
                     }
                  }

                  if (var3.has("Не рендерить") && !var6.has("Не рендерить")) {
                     try {
                        var6.add("Не рендерить", var3.get("Не рендерить").deepCopy());
                     } catch (Throwable var11) {
                     }
                  }

                  var5.add("Settings", var6);
                  var1.add("Removals", var5);
               }
            }
         }
      }
   }

   private boolean handle(Module var1, JsonObject var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else {
         return var1 instanceof UnHook ? var2.remove("enable") != null : false;
      }
   }
}
