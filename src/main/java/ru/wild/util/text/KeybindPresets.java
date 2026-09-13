package ru.wild.util.text;

import net.minecraft.client.MinecraftClient;
import org.wild.module.api.Module;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.InputButtonEvent;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.event.MouseScrollEvent;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ChoiceSetting;
import ru.wild.api.setting.KeybindMode;
import ru.wild.api.setting.Setting;

public class KeybindPresets {
   private static final KeybindPresets instance = new KeybindPresets();
   private static final MinecraftClient data = MinecraftClient.getInstance();
   private boolean context = false;
   private boolean config = false;

   public static KeybindPresets handle() {
      return instance;
   }

   public void process() {
      if (!this.context) {
         EventHandlerInvoker.handle(this);
         this.context = true;
      }
   }

   @EventHandler
   public void handle(InputButtonEvent var1) {
      if (!var1.handle()) {
         if (var1.apply() == 1) {
            if (var1.resolve() >= 0) {
               if (WildClient.instance.data != null) {
                  Module[] var2 = WildClient.instance.data.handle(var1.resolve());
                  if (var2 != null) {
                     for (Module var6 : var2) {
                        var6.toggle();
                     }
                  }

                  this.process(var1.resolve());
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (!var1.handle()) {
         if (!var1.check()) {
            if (var1.onTick()) {
               if (data == null || data.currentScreen == null) {
                  if (!this.config) {
                     if (WildClient.instance.data != null) {
                        int var2 = -100 - var1.resolve();
                        Module[] var3 = WildClient.instance.data.handle(var2);
                        if (var3 != null) {
                           for (Module var7 : var3) {
                              var7.toggle();
                           }
                        }

                        this.process(var2);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void handle(MouseScrollEvent var1) {
      if (!var1.handle()) {
         if (!var1.prepare()) {
            if (data == null || data.currentScreen == null) {
               if (!this.config && WildClient.instance.data != null) {
                  if (!(Math.abs(var1.update()) < 1.0E-4)) {
                     int var2 = var1.update() > 0.0 ? -200 : -201;
                     Module[] var3 = WildClient.instance.data.handle(var2);
                     if (var3 != null) {
                        for (Module var7 : var3) {
                           var7.toggle();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void process(int var1) {
      for (Module var3 : WildClient.instance.data.instance) {
         for (Setting var5 : var3.select()) {
            if (var5 instanceof BooleanSetting var6) {
               this.handle(var6, var1);
            } else if (var5 instanceof ChoiceSetting var7) {
               for (BooleanSetting var9 : var7.config) {
                  this.handle(var9, var1);
               }
            }
         }
      }
   }

   private void handle(BooleanSetting var1, int var2) {
      if (var1.cache == var2 && !var1.output) {
         var1.process(!var1.resolve());
      }
   }

   public void compute() {
   }

   public void handle(String var1) {
   }

   public void handle(boolean var1) {
      this.config = var1;
   }

   public boolean resolve() {
      return this.config;
   }

   public void handle(Module var1, int var2, KeybindMode var3) {
      if (var1 != null) {
         var1.keyCode = var2;
      }
   }

   public void handle(Module var1, Setting var2, KeybindMode var3, int var4, Object var5) {
   }

   public void handle(String var1, String var2) {
   }

   public Object process(String var1, String var2) {
      return null;
   }

   public String handle(int var1) {
      if (var1 == -200) {
         return "Wheel Up";
      } else if (var1 == -201) {
         return "Wheel Down";
      } else if (var1 <= -100) {
         return "Mouse " + (Math.abs(var1 + 100) + 1);
      } else if (var1 == -1) {
         return "None";
      } else if (var1 >= 65 && var1 <= 90) {
         return String.valueOf((char)(65 + (var1 - 65)));
      } else if (var1 >= 48 && var1 <= 57) {
         return String.valueOf((char)(48 + (var1 - 48)));
      } else if (var1 == 32) {
         return "Space";
      } else if (var1 == 257) {
         return "Enter";
      } else if (var1 == 256) {
         return "Escape";
      } else if (var1 == 259) {
         return "Backspace";
      } else if (var1 == 258) {
         return "Tab";
      } else if (var1 == 340 || var1 == 344) {
         return "Shift";
      } else if (var1 == 341 || var1 == 345) {
         return "Ctrl";
      } else if (var1 == 342 || var1 == 346) {
         return "Alt";
      } else {
         return var1 >= 290 && var1 <= 314 ? "F" + (var1 - 290 + 1) : "Key " + var1;
      }
   }
}
