package ru.wild.config;

import org.wild.module.api.Module;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.wild.WildClient;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.ResettableSettingGroup;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.core.manager.FeatureManager;
import ru.wild.gui.hud.HudElementMetadata;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;

public class HudProfileConfig {
   private static final List<ResettableSettingGroup> instance = new ArrayList<>();
   private static final Gson data = new GsonBuilder().setPrettyPrinting().create();
   private static JsonObject context = new JsonObject();
   private static boolean config;

   private static File apply() {
      return new File(WildClient.instance.cache, "hudP.cfg");
   }

   @Compile
   public static void handle() {
      if (config) {
         execute();
         prepare();
      } else {
         File var0 = apply();
         if (var0.exists()) {
            try (FileReader var1 = new FileReader(var0)) {
               JsonObject var2 = (JsonObject)data.fromJson(var1, JsonObject.class);
               Field var3 = HudProfileConfig.class.getDeclaredField("Module.client");
               var3.setAccessible(true);
               var3.set(null, var2);
               if (context != null) {
                  execute();
               }

               if (WildClient.instance != null && WildClient.instance.renderer != null) {
                  WildClient.instance.renderer.compute();
               }
            } catch (Exception var6) {
            }
         }
      }
   }

   @Compile
   public static void handle(ResettableSettingGroup var0) {
      if (var0 != null && handle(var0.getClass()) && !instance.contains(var0)) {
         instance.add(var0);
         handle(var0);
      }
   }

   public static List<ResettableSettingGroup> process() {
      return instance.stream().filter(var0 -> var0 != null && handle(var0.getClass())).toList();
   }

   public static void compute() {
      context = new JsonObject();

      for (ResettableSettingGroup var1 : process()) {
         for (Setting var3 : var1.handle()) {
            if (var3 != null && !var3.context) {
               var3.process();
            }
         }
      }

      resolve();
   }

   public static boolean handle(Class<?> var0) {
      return var0 != null && FeatureManager.handle(var0.getAnnotation(ModuleRoles.class));
   }

   @Compile
   private static void process(ResettableSettingGroup var0) {
      if (var0 != null) {
         Class var1 = var0.getClass();
         if (var1 != null) {
            HudElementMetadata var2 = (HudElementMetadata)var1.getAnnotation(HudElementMetadata.class);
            if (var2 != null) {
               JsonObject var3 = context;
               String var4 = var2.handle();
               if (var3 != null && var3.has(var4)) {
                  JsonObject var5 = context;
                  String var6 = var2.handle();
                  JsonObject var7 = var5 == null ? null : var5.getAsJsonObject(var6);
                  List var8 = var0.handle();
                  if (var8 != null) {
                     Iterator var9 = var8.iterator();
                     if (var9 != null) {
                        while (var9.hasNext()) {
                           Setting var10 = (Setting)var9.next();
                           if (var10 != null && !var10.context) {
                              if (var10 instanceof BooleanSetting var11) {
                                 if (var7 != null && var7.has(var11.instance)) {
                                    JsonElement var24 = var7.get(var11.instance);
                                    var11.process(var24 != null && var24.getAsBoolean());
                                 }
                              } else if (var10 instanceof NumberSetting var12) {
                                 if (var7 != null && var7.has(var12.instance)) {
                                    JsonElement var23 = var7.get(var12.instance);
                                    var12.handle(var23 == null ? 0.0F : var23.getAsFloat());
                                 }
                              } else if (var10 instanceof ModeSetting var13) {
                                 if (var7 != null && var7.has(var13.instance)) {
                                    JsonElement var22 = var7.get(var13.instance);
                                    String var25 = var22 == null ? null : var22.getAsString();
                                    List var26 = var13.config;
                                    int var27 = var26 == null ? -1 : var26.indexOf(var25);
                                    if (var27 >= 0) {
                                       var13.current = var27;
                                       var13.state = var26 == null ? null : (String)var26.get(var27);
                                    }
                                 }
                              } else if (var10 instanceof ShaderPresetSetting var14) {
                                 if (var7 != null && var7.has(var14.instance)) {
                                    JsonElement var21 = var7.get(var14.instance);
                                    var14.compute(var21 == null ? null : var21.getAsString());
                                 }
                              } else if (var10 instanceof ChoiceSetting var15 && var7 != null && var7.has(var15.instance)) {
                                 JsonObject var16 = var7.getAsJsonObject(var15.instance);
                                 List var17 = var15.config;
                                 if (var17 != null) {
                                    Iterator var18 = var17.iterator();
                                    if (var18 != null) {
                                       while (var18.hasNext()) {
                                          BooleanSetting var19 = (BooleanSetting)var18.next();
                                          if (var16 != null && var16.has(var19.instance)) {
                                             JsonElement var20 = var16.get(var19.instance);
                                             var19.process(var20 != null && var20.getAsBoolean());
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void resolve() {
      context = update();
      prepare();
      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   public static JsonObject update() {
      JsonObject var0 = new JsonObject();

      for (ResettableSettingGroup var2 : process()) {
         HudElementMetadata var3 = var2.getClass().getAnnotation(HudElementMetadata.class);
         if (var3 != null) {
            JsonObject var4 = new JsonObject();

            for (Setting var6 : var2.handle()) {
               if (var6 != null && !var6.context) {
                  if (var6 instanceof BooleanSetting var7) {
                     var4.addProperty(var7.instance, var7.compute());
                  } else if (var6 instanceof NumberSetting var8) {
                     var4.addProperty(var8.instance, var8.compute());
                  } else if (var6 instanceof ModeSetting var9) {
                     var4.addProperty(var9.instance, var9.compute());
                  } else if (var6 instanceof ShaderPresetSetting var10) {
                     var4.addProperty(var10.instance, var10.execute());
                  } else if (var6 instanceof ChoiceSetting var11) {
                     JsonObject var12 = new JsonObject();

                     for (BooleanSetting var14 : var11.config) {
                        var12.addProperty(var14.instance, var14.compute());
                     }

                     var4.add(var11.instance, var12);
                  }
               }
            }

            var0.add(var3.handle(), var4);
         }
      }

      return var0;
   }

   public static void handle(JsonObject var0) {
      if (var0 != null) {
         context = var0.deepCopy();
         config = true;
         execute();
         prepare();
      }
   }

   private static void execute() {
      for (ResettableSettingGroup var1 : instance) {
         process(var1);
      }
   }

   private static void prepare() {
      try {
         File var0 = apply();
         if (!var0.getParentFile().exists()) {
            var0.getParentFile().mkdirs();
         }

         try (FileWriter var1 = new FileWriter(var0)) {
            data.toJson(context, var1);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   static {
      Loader.initialize();
   }
}
