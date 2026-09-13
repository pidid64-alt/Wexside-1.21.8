package org.wild.module.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.ActionSetting;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.ColorSetting;
import ru.wild.api.setting.DynamicActionSetting;
import ru.wild.api.setting.FloatSetting;
import ru.wild.api.setting.HotbarLayoutSetting;
import ru.wild.api.setting.KeybindSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.MultiSelectSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.Setting;
import ru.wild.api.setting.SettingGroup;
import ru.wild.api.setting.ShaderPresetSetting;
import ru.wild.api.setting.StringSetting;
import ru.wild.audio.AudioResourceManager;
import ru.wild.gui.hud.NotificationHudSettings;
import ru.wild.modules.misc.ClientUtil;
import ru.wild.modules.visuals.Menu;
import ru.wild.util.math.DoubleAnimator;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.EasedDoubleAnimator;
import ru.wild.util.math.EasingFunctions;
import ru.wild.util.math.SmoothTimer;
import ru.wild.util.math.Vec2f;

public class Module extends SettingGroup {
   private static final String RESET_SETTINGS_LABEL = "Сброс настроек";
   private static final String RESET_TO_DEFAULTS_LABEL = "До заводских";
   public ModuleRegister moduleInfo = this.getClass().getAnnotation(ModuleRegister.class);
   public ModuleRoles roles = this.getClass().getAnnotation(ModuleRoles.class);
   public static MinecraftClient client = MinecraftClient.getInstance();
   public String displayName;
   public int keyCode;
   public boolean enabled;
   public boolean holdToActivate = false;
   public ModuleCategory category;
   public String searchName;
   public String description;
   public boolean bindingActive;
   public boolean visible = true;
   public Vec2f position = new Vec2f(0.0F, 0.0F);
   private final Set<ModuleFlag> flags = new HashSet<>();
   private boolean warningShown;
   private final DynamicActionSetting resetAction = new DynamicActionSetting("Сброс настроек", 0, () -> "До заводских").process(this::resetSettings);
   public DoubleAnimator toggleAnimation = new DoubleAnimator();
   public SmoothTimer enableAnimation = new EaseTimer(300, 1.0);
   public SmoothTimer secondaryAnimation = new EaseTimer(300, 1.0);
   public final EasedDoubleAnimator stateAnimation = new EasedDoubleAnimator();

   public Module() {
      this.displayName = this.moduleInfo.name();
      this.category = this.moduleInfo.category();
      this.keyCode = -1;
      this.enabled = false;
      this.description = this.moduleInfo.description();
      this.searchName = this.displayName;
      Collections.addAll(this.flags, this.moduleInfo.flags());
   }

   public void handle() {
      try {
         EventHandlerInvoker.handle(this);
      } catch (Exception var2) {
         var2.printStackTrace();
         this.enabled = false;
         return;
      }

      if (client.player != null && !(this instanceof Menu)) {
         NotificationHudSettings.handle(this.displayName, true);
         if (WildClient.instance.data.handle(ClientUtil.class).enabled && ClientUtil.target.compute() && ClientUtil.pending.process("Модули")) {
            AudioResourceManager.handle("Function_ON", ClientUtil.previous.compute() / 250.0F);
         }
      }

      this.stateAnimation.handle(1.0, 0.24F, EasingFunctions.handler);
   }

   public void process() {
      EventHandlerInvoker.process(this);
      if (client.player != null && !WildClient.performVector() && !(this instanceof Menu)) {
         NotificationHudSettings.handle(this.displayName, false);
         if (WildClient.instance.data.handle(ClientUtil.class).enabled && ClientUtil.target.compute() && ClientUtil.pending.process("Модули")) {
            AudioResourceManager.handle("Function_OFF", ClientUtil.previous.compute() / 250.0F);
         }
      }

      this.stateAnimation.handle(0.0, 0.24F, EasingFunctions.handler);
   }

   public void toggle() {
      this.setEnabled(!this.enabled, true);
   }

   public JsonObject serialize() {
      JsonObject var1 = new JsonObject();
      if (this.enabled) {
         var1.addProperty("enable", this.enabled);
      }

      if (this.keyCode != -1) {
         var1.addProperty("keyIndex", this.keyCode);
      }

      JsonObject var2 = new JsonObject();

      for (Setting var4 : this.select()) {
         if (var4 != null && !var4.context) {
            String var5 = var4.handle();
            Setting var6 = var4;
            switch (var6) {
               case BooleanSetting var8:
                  var2.addProperty(var5, var8.resolve());
                  if (var8.cache != -1) {
                     JsonObject var23 = new JsonObject();
                     var23.addProperty("key", var8.cache);
                     var23.addProperty("hold", var8.output);
                     var2.add(var5 + "$bind", var23);
                  }
                  break;
               case ModeSetting var9:
                  var2.addProperty(var5, var9.state);
                  break;
               case ShaderPresetSetting var10:
                  var2.addProperty(var5, var10.execute());
                  break;
               case MultiSelectSetting var11:
                  var2.addProperty(var5, String.join(", ", var11.output));
                  break;
               case NumberSetting var12:
                  var2.addProperty(var5, var12.config);
                  break;
               case KeybindSetting var13:
                  var2.addProperty(var5, var13.config);
                  break;
               case StringSetting var14:
                  var2.addProperty(var5, var14.state);
                  break;
               case HotbarLayoutSetting var15:
                  var2.add(var5, var15.execute());
                  break;
               case ColorSetting var16:
                  JsonObject var24 = new JsonObject();
                  var24.addProperty("current", var16.state);
                  var24.addProperty("saturation", var16.renderer);
                  var24.addProperty("brightness", var16.handler);
                  var2.add(var5, var24);
                  break;
               case ChoiceSetting var25:
                  ChoiceSetting var17 = (ChoiceSetting)var6;
                  JsonObject var18 = new JsonObject();
                  JsonObject var19 = new JsonObject();

                  for (BooleanSetting var21 : var17.config) {
                     var18.addProperty(var21.instance, var21.resolve());
                     if (var21.cache != -1) {
                        JsonObject var22 = new JsonObject();
                        var22.addProperty("key", var21.cache);
                        var22.addProperty("hold", var21.output);
                        var19.add(var21.instance, var22);
                     }
                  }

                  var2.add(var5, var18);
                  if (var19.size() > 0) {
                     var2.add(var5 + "$binds", var19);
                  }
                  continue;
               default:
            }
         }
      }

      var1.add("Settings", var2);
      return var1;
   }

   public void handle(JsonObject var1) {
      if (var1 != null) {
         try {
            if (var1.has("enable")) {
               this.setEnabled(var1.get("enable").getAsBoolean());
            }
         } catch (Throwable var31) {
         }

         try {
            if (var1.has("keyIndex")) {
               this.keyCode = var1.get("keyIndex").getAsInt();
            }
         } catch (Throwable var30) {
         }

         JsonObject var2 = null;

         try {
            var2 = var1.getAsJsonObject("Settings");
         } catch (Throwable var29) {
         }

         if (var2 != null) {
            for (Setting var4 : this.select()) {
               if (var4 != null && !var4.context) {
                  String var5 = var4.handle();
                  if (var2.has(var5)) {
                     try {
                        Setting var6 = var4;
                        switch (var6) {
                           case BooleanSetting var8:
                              var8.process(var2.get(var5).getAsBoolean());
                              JsonElement var33 = var2.get(var5 + "$bind");
                              if (var33 != null && var33.isJsonObject()) {
                                 JsonObject var35 = var33.getAsJsonObject();
                                 if (var35.has("key")) {
                                    var8.cache = var35.get("key").getAsInt();
                                 }

                                 if (var35.has("hold")) {
                                    var8.output = var35.get("hold").getAsBoolean();
                                 }
                              }
                              break;
                           case ModeSetting var9:
                              String var34 = var2.get(var5).getAsString();
                              if (var9.config != null && var9.config.contains(var34)) {
                                 var9.state = var34;
                                 var9.current = var9.config.indexOf(var34);
                              }
                              break;
                           case NumberSetting var10:
                              float var36 = var2.get(var5).getAsFloat();
                              if (!Float.isNaN(var36) && !Float.isInfinite(var36)) {
                                 var10.config = Math.max(var10.state, Math.min(var10.cache, var36));
                              }
                              break;
                           case ShaderPresetSetting var11:
                              var11.compute(var2.get(var5).getAsString());
                              break;
                           case KeybindSetting var12:
                              var12.config = var2.get(var5).getAsInt();
                              break;
                           case StringSetting var13:
                              var13.process(var2.get(var5).getAsString());
                              break;
                           case HotbarLayoutSetting var14:
                              var14.handle(var2.get(var5));
                              break;
                           case ColorSetting var15:
                              JsonElement var37 = var2.get(var5);
                              if (var37 != null && var37.isJsonObject()) {
                                 JsonObject var40 = var37.getAsJsonObject();
                                 if (var40.has("current")) {
                                    float var43 = var40.get("current").getAsFloat();
                                    if (!Float.isNaN(var43) && !Float.isInfinite(var43)) {
                                       var15.state = Math.max(var15.cache, Math.min(var15.output, var43));
                                    }
                                 }

                                 if (var40.has("saturation")) {
                                    float var44 = var40.get("saturation").getAsFloat();
                                    if (!Float.isNaN(var44) && !Float.isInfinite(var44)) {
                                       var15.renderer = Math.max(0.0F, Math.min(1.0F, var44));
                                    }
                                 }

                                 if (var40.has("brightness")) {
                                    float var45 = var40.get("brightness").getAsFloat();
                                    if (!Float.isNaN(var45) && !Float.isInfinite(var45)) {
                                       var15.handler = Math.max(0.0F, Math.min(1.0F, var45));
                                    }
                                 }
                              } else if (var37 != null) {
                                 float var39 = var37.getAsFloat();
                                 if (!Float.isNaN(var39) && !Float.isInfinite(var39)) {
                                    var15.state = Math.max(var15.cache, Math.min(var15.output, var39));
                                 }
                              }
                              break;
                           case ChoiceSetting var52:
                              ChoiceSetting var16 = (ChoiceSetting)var6;
                              JsonElement var38 = var2.get(var5);
                              if (var38 != null && var38.isJsonObject()) {
                                 JsonObject var41 = var38.getAsJsonObject();

                                 for (BooleanSetting var48 : var16.config) {
                                    if (var41.has(var48.instance)) {
                                       try {
                                          var48.process(var41.get(var48.instance).getAsBoolean());
                                       } catch (Throwable var28) {
                                       }
                                    }
                                 }
                              }

                              JsonElement var42 = var2.get(var5 + "$binds");
                              if (var42 != null && var42.isJsonObject()) {
                                 JsonObject var47 = var42.getAsJsonObject();

                                 for (BooleanSetting var50 : var16.config) {
                                    if (var47.has(var50.instance)) {
                                       try {
                                          JsonObject var51 = var47.getAsJsonObject(var50.instance);
                                          if (var51.has("key")) {
                                             var50.cache = var51.get("key").getAsInt();
                                          }

                                          if (var51.has("hold")) {
                                             var50.output = var51.get("hold").getAsBoolean();
                                          }
                                       } catch (Throwable var27) {
                                       }
                                    }
                                 }
                              }
                              break;
                           case MultiSelectSetting var17:
                              var17.compute();
                              JsonElement var18 = var2.get(var5);
                              if (var18 != null) {
                                 String var19 = var18.getAsString();
                                 String[] var20 = var19.split(",");
                                 ArrayList var21 = new ArrayList();

                                 for (String var25 : var20) {
                                    if (var25 != null) {
                                       String var26 = var25.trim();
                                       if (!var26.isEmpty() && var17.config != null && var17.config.contains(var26)) {
                                          var21.add(var26);
                                       }
                                    }
                                 }

                                 var17.output = var21;
                              }
                              break;
                           default:
                        }
                     } catch (Throwable var32) {
                     }
                  }
               }
            }
         }
      }
   }

   public void setEnabled(boolean enabled) {
      this.setEnabled(enabled, false);
   }

   public void enable() {
      this.setEnabled(true);
   }

   public void disable() {
      this.setEnabled(false);
   }

   public void resetToDefaults() {
      if (this.enabled) {
         this.setEnabled(false);
      }

      this.keyCode = -1;
      this.holdToActivate = false;
      this.bindingActive = false;

      for (Setting var2 : this.select()) {
         if (var2 != null && !var2.context) {
            var2.process();
         }
      }
   }

   @Override
   public List<Setting> apply() {
      List var1 = super.apply();
      if (this.hasResettableSettings()) {
         var1.add(this.resetAction);
      }

      return var1;
   }

   private void resetSettings() {
      for (Setting var2 : this.select()) {
         if (this.isResettableSetting(var2)) {
            var2.process();
         }
      }

      if (WildClient.instance != null && WildClient.instance.renderer != null) {
         WildClient.instance.renderer.compute();
      }
   }

   private boolean hasResettableSettings() {
      for (Setting var2 : this.select()) {
         if (this.isResettableSetting(var2)) {
            return true;
         }
      }

      return false;
   }

   private boolean isResettableSetting(Setting var1) {
      return var1 != null && var1 != this.resetAction && !var1.context && !(var1 instanceof ActionSetting) && !(var1 instanceof FloatSetting);
   }

   public Module addFlag(ModuleFlag flag) {
      if (flag != null) {
         this.flags.add(flag);
      }

      return this;
   }

   public Module addFlags(ModuleFlag... newFlags) {
      if (newFlags != null) {
         Collections.addAll(this.flags, newFlags);
         this.flags.remove(null);
      }

      return this;
   }

   public boolean hasFlag(ModuleFlag flag) {
      return flag != null && this.flags.contains(flag);
   }

   public Set<ModuleFlag> getFlags() {
      return Collections.unmodifiableSet(this.flags);
   }

   private void setEnabled(boolean enabled, boolean persist) {
      if (enabled && WildClient.instance != null && WildClient.instance.data != null && !WildClient.instance.data.handle(this)) {
         this.enabled = false;
      } else if (this.enabled != enabled) {
         this.enabled = enabled;
         if (enabled) {
            this.handle();
         } else {
            this.process();
         }

         if (persist && WildClient.instance != null && WildClient.instance.renderer != null) {
            WildClient.instance.renderer.compute();
         }
      }
   }

   private void showRiskWarning() {
      if (!this.warningShown && client.player != null && (this.hasFlag(ModuleFlag.RISKY) || this.hasFlag(ModuleFlag.PATCHED))) {
         this.warningShown = true;
         String var1 = this.hasFlag(ModuleFlag.RISKY) && this.hasFlag(ModuleFlag.PATCHED)
            ? "Risky/Patched"
            : (this.hasFlag(ModuleFlag.RISKY) ? "Risky" : "Patched");
         NotificationHudSettings.handle("warn", "Warning: " + this.displayName + " is currently flagged as " + var1 + ".", 3500L);
      }
   }
   public ModuleRoles getRoles() {
      return this.roles;
   }
   public int getKeyCode() {
      return this.keyCode;
   }
   public String getSearchName() {
      return this.searchName;
   }
}
