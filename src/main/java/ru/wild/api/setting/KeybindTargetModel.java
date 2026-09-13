package ru.wild.api.setting;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import org.wild.module.api.Module;
import ru.wild.util.render.ColorVector;

public final class KeybindTargetModel {
   private static final double instance = 1.0E-6;
   private final KeybindTargetModel.Mode data;
   private final Module context;
   private final Setting config;
   private final String state;
   private final String cache;
   private final Object output;
   private final String current;
   private Object active;
   private Object mode;
   private int selection;
   private KeybindMode enabled;
   private int renderer;
   private KeybindMode handler;
   private boolean animationDraw;
   private boolean pointEncode;

   private KeybindTargetModel(
      KeybindTargetModel.Mode var1,
      Module var2,
      Setting var3,
      String var4,
      String var5,
      Object var6,
      String var7,
      Object var8,
      Object var9,
      int var10,
      KeybindMode var11
   ) {
      this.data = Objects.requireNonNull(var1, "targetType");
      this.context = var2;
      this.config = var3;
      this.state = var4 == null ? "" : var4;
      this.cache = var5 == null ? "" : var5;
      this.output = var6;
      this.current = var7 == null ? "" : var7;
      this.active = var8;
      this.mode = var9;
      this.selection = var10;
      this.enabled = Objects.requireNonNull(var11, "mode");
      this.renderer = var10;
      this.handler = var11;
      this.animationDraw = var10 == -1;
      this.pointEncode = this.animationDraw;
   }

   public static KeybindTargetModel handle(Module var0) {
      Objects.requireNonNull(var0, "module");
      int var1 = var0.keyCode > 0 ? var0.keyCode : -1;
      return new KeybindTargetModel(
         KeybindTargetModel.Mode.MODULE,
         var0,
         null,
         var0.displayName,
         var0.description,
         var0.enabled,
         "Modules toggle state is controlled by the mode.",
         null,
         null,
         var1,
         KeybindMode.TOGGLE
      );
   }

   public static KeybindTargetModel handle(Module var0, Setting var1, Object var2, Object var3, int var4, KeybindMode var5) {
      Objects.requireNonNull(var0, "module");
      Objects.requireNonNull(var1, "setting");
      Object var6 = var2;
      Object var7 = var3;
      return new KeybindTargetModel(KeybindTargetModel.Mode.SETTING, var0, var1, var1.instance, var0.displayName, var6, "", var7, var7, var4, var5);
   }

   public KeybindTargetModel.Mode handle() {
      return this.data;
   }

   public Module process() {
      return this.context;
   }

   public Setting compute() {
      return this.config;
   }

   public String resolve() {
      return this.state;
   }

   public String update() {
      return this.cache;
   }

   public Object apply() {
      return this.output;
   }

   public String execute() {
      return this.current;
   }

   public Object prepare() {
      return !this.refresh() ? null : this.active;
   }

   public void handle(Object var1) {
      this.projectItem();
      this.active = this.compute(var1);
      this.animationDraw = false;
   }

   public int check() {
      return this.selection;
   }

   public void handle(int var1) {
      if (var1 == -1 || var1 >= 32 && var1 <= 348) {
         this.selection = var1;
         if (var1 != -1) {
            this.animationDraw = false;
         }
      } else {
         throw new IllegalArgumentException("keyCode must be GLFW.GLFW_KEY_UNKNOWN or a valid GLFW key constant");
      }
   }

   public KeybindMode onTick() {
      return this.enabled;
   }

   public void handle(KeybindMode var1) {
      this.enabled = Objects.requireNonNull(var1, "mode");
   }

   public boolean select() {
      return this.data == KeybindTargetModel.Mode.MODULE;
   }

   public boolean refresh() {
      return this.data == KeybindTargetModel.Mode.SETTING;
   }

   public boolean render() {
      return this.data == KeybindTargetModel.Mode.SETTING && this.config != null
         ? this.config instanceof NumberSetting || this.config instanceof ModeSetting || this.config instanceof MultiSelectSetting
         : false;
   }

   public boolean tick() {
      return !this.current.isBlank();
   }

   public boolean drawAnimation() {
      return this.selection != this.renderer || this.enabled != this.handler || this.animationDraw != this.pointEncode || this.encodePoint();
   }

   public boolean encodePoint() {
      return this.refresh() && this.config != null ? !handle(this.config, this.active, this.mode) : false;
   }

   public void animate() {
      this.projectItem();
      this.animationDraw = this.pointEncode;
   }

   public void load() {
      this.save();
   }

   public void save() {
      this.renderer = this.selection;
      this.handler = this.enabled;
      this.pointEncode = this.animationDraw;
      if (this.refresh() && this.config != null) {
         this.mode = process(this.config, this.active);
      }
   }

   public void submit() {
      this.selection = this.renderer;
      this.enabled = this.handler;
      this.animationDraw = this.pointEncode;
      if (this.refresh()) {
         ;
      }
   }

   public void unload() {
      this.selection = -1;
      this.animationDraw = true;
      if (this.refresh()) {
         ;
      }
   }

   public boolean fetch() {
      return this.animationDraw;
   }

   public Object measure() {
      this.projectItem();
      return process(this.config, this.active);
   }

   public void process(Object var1) {
      this.projectItem();
      this.active = this.compute(Objects.requireNonNull(var1, "value"));
      this.animationDraw = false;
   }

   public String blendMatrix() {
      return this.context != null ? this.context.displayName : "";
   }

   public String matchVector() {
      return this.config != null ? this.config.instance : "";
   }

   private void projectItem() {
      if (!this.refresh()) {
         throw new IllegalStateException("Operation only supported for setting targets");
      }

      if (this.config == null) {
         throw new IllegalStateException("Setting context is not available");
      }
   }

   private Object compute(Object var1) {
      Objects.requireNonNull(var1, "value");
      if (this.config instanceof BooleanSetting) {
         if (var1 instanceof Boolean var3) {
            return var3;
         } else if (var1 instanceof Number var2) {
            return var2.doubleValue() != 0.0;
         } else {
            throw new IllegalArgumentException("Target value must be boolean-compatible");
         }
      } else if (this.config instanceof NumberSetting) {
         return this.resolve(var1);
      } else if (this.config instanceof ModeSetting) {
         return this.update(var1);
      } else if (this.config instanceof MultiSelectSetting) {
         return this.apply(var1);
      } else if (this.config instanceof ColorSetting) {
         return this.execute(var1);
      } else {
         return var1 instanceof String ? var1 : var1.toString();
      }
   }

   private Object resolve(Object var1) {
      if (this.config instanceof NumberSetting var2) {
         if (var1 instanceof Number var12) {
            double var4 = var12.doubleValue();
            if (!Double.isNaN(var4) && !Double.isInfinite(var4)) {
               double var6 = Math.min(Math.max(var4, var2.state), var2.cache);
               double var8 = Math.round((var6 - var2.state) / var2.output);
               double var10 = var2.state + var8 * var2.output;
               if (var10 < var2.state) {
                  var10 = var2.state;
               } else if (var10 > var2.cache) {
                  var10 = var2.cache;
               }

               return var10;
            } else {
               throw new IllegalArgumentException("Target value must be a finite number");
            }
         } else {
            throw new IllegalArgumentException("Target value must be numeric");
         }
      } else {
         throw new IllegalStateException("Setting is not a SliderSetting");
      }
   }

   private Object update(Object var1) {
      if (this.config instanceof ModeSetting var2) {
         String var4 = var1.toString();
         if (var2.config != null && var2.config.contains(var4)) {
            return var4;
         } else {
            throw new IllegalArgumentException("Unsupported option '" + var4 + "'");
         }
      } else {
         throw new IllegalStateException("Setting is not a ModeSetting");
      }
   }

   private Object apply(Object var1) {
      if (!(this.config instanceof MultiSelectSetting var2)) {
         throw new IllegalStateException("Setting is not a ListSetting");
      } else {
         var2.compute();
         if (!(var1 instanceof Collection var8)) {
            throw new IllegalArgumentException("Target value must be a collection");
         } else {
            LinkedHashSet var4 = new LinkedHashSet();

            for (Object var6 : var8) {
               if (var6 != null) {
                  String var7 = var6.toString();
                  if (var2.config == null || !var2.config.contains(var7)) {
                     throw new IllegalArgumentException("Unsupported option '" + var7 + "'");
                  }

                  var4.add(var7);
               }
            }

            return var4;
         }
      }
   }

   private Object execute(Object var1) {
      if (this.config instanceof ColorSetting var2) {
         if (var1 instanceof ColorVector var10) {
            return var10;
         } else if (var1 instanceof Number var9) {
            return ColorVector.handle(var9.intValue());
         } else if (var1 instanceof String var8) {
            try {
               String var4 = var8.startsWith("#") ? var8.substring(1) : var8;
               int var5 = (int)Long.parseUnsignedLong(var4, 16);
               int var6 = var4.length() > 6 ? var5 : 0xFF000000 | var5;
               return ColorVector.handle(var6);
            } catch (NumberFormatException var7) {
               throw new IllegalArgumentException("Invalid colour string: " + var8, var7);
            }
         } else {
            return ColorVector.handle(var2.update(), var2.renderer, var2.handler, var2.animationDraw);
         }
      } else {
         throw new IllegalStateException("Setting is not a HueSetting");
      }
   }

   private static Object handle(Setting var0, Object var1) {
      if (var0 instanceof BooleanSetting) {
         return handle((JsonElement)null, var1, var0);
      } else if (var0 instanceof NumberSetting) {
         return handle((JsonElement)null, var0, var1);
      } else if (var0 instanceof ModeSetting) {
         return process(null, var0, var1);
      } else if (var0 instanceof MultiSelectSetting) {
         return resolve(null, var0, var1);
      } else {
         return var0 instanceof ColorSetting ? update(null, var0, var1) : var1;
      }
   }

   private static Object handle(JsonElement var0, Object var1, Setting var2) {
      boolean var3 = var1 instanceof Boolean var4 ? var4 : Boolean.FALSE;
      if (var0 != null && var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isBoolean()) {
         var3 = var0.getAsBoolean();
      }

      return var3;
   }

   private static Object handle(JsonElement var0, Setting var1, Object var2) {
      if (var1 instanceof NumberSetting var3) {
         double var4 = var2 instanceof Number var6 ? var6.doubleValue() : var3.config;
         if (var0 != null && var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
            var4 = var0.getAsDouble();
         }

         double var12 = Math.min(Math.max(var4, var3.state), var3.cache);
         double var8 = Math.round((var12 - var3.state) / var3.output);
         double var10 = var3.state + var8 * var3.output;
         if (var10 < var3.state) {
            var10 = var3.state;
         } else if (var10 > var3.cache) {
            var10 = var3.cache;
         }

         return var10;
      } else {
         throw new IllegalStateException("Setting is not a SliderSetting");
      }
   }

   private static Object process(JsonElement var0, Setting var1, Object var2) {
      if (var1 instanceof ModeSetting var3) {
         String var4 = var2 instanceof String var5 ? var5 : (var3.state != null ? var3.state : "");
         if (var0 != null && var0.isJsonPrimitive()) {
            var4 = var0.getAsString();
         }

         if (var3.config == null || !var3.config.contains(var4)) {
            var4 = var3.state != null ? var3.state : "";
         }

         return var4;
      } else {
         throw new IllegalStateException("Setting is not a ModeSetting");
      }
   }

   private static Object compute(JsonElement var0, Setting var1, Object var2) {
      String var3 = var2 instanceof String var4 ? var4 : "";
      if (var0 != null && var0.isJsonPrimitive()) {
         var3 = var0.getAsString();
      }

      return var3;
   }

   private static Object resolve(JsonElement var0, Setting var1, Object var2) {
      if (!(var1 instanceof MultiSelectSetting var3)) {
         throw new IllegalStateException("Setting is not a ListSetting");
      } else {
         var3.compute();
         LinkedHashSet var4 = new LinkedHashSet();
         if (var0 != null && var0.isJsonArray()) {
            for (JsonElement var7 : var0.getAsJsonArray()) {
               if (var7.isJsonPrimitive()) {
                  String var8 = var7.getAsString();
                  if (var3.config != null && var3.config.contains(var8)) {
                     var4.add(var8);
                  }
               }
            }
         }

         return var4;
      }
   }

   private static Object update(JsonElement var0, Setting var1, Object var2) {
      if (var1 instanceof ColorSetting var3) {
         ColorVector var4;
         if (var2 instanceof ColorVector var5) {
            var4 = var5;
         } else if (var2 instanceof Number var6) {
            var4 = ColorVector.handle(var6.intValue());
         } else if (var2 instanceof String var7) {
            try {
               String var8 = var7.startsWith("#") ? var7.substring(1) : var7;
               int var9 = (int)Long.parseUnsignedLong(var8, 16);
               int var10 = var8.length() > 6 ? var9 : 0xFF000000 | var9;
               var4 = ColorVector.handle(var10);
            } catch (NumberFormatException var11) {
               var4 = ColorVector.handle(var3.update(), var3.renderer, var3.handler, var3.animationDraw);
            }
         } else {
            var4 = ColorVector.handle(var3.update(), var3.renderer, var3.handler, var3.animationDraw);
         }

         return var4;
      } else {
         throw new IllegalStateException("Setting is not a HueSetting");
      }
   }

   private static Collection<?> prepare(Object var0) {
      return var0 instanceof Collection var1 ? var1 : List.of();
   }

   private static Object process(Setting var0, Object var1) {
      if (var0 == null || var1 == null) {
         return var1;
      }

      if (var0 instanceof BooleanSetting) {
         return Boolean.TRUE.equals(var1);
      }

      if (var0 instanceof NumberSetting) {
         return ((Number)var1).doubleValue();
      }

      if (var0 instanceof ModeSetting || var0 instanceof StringSetting) {
         return var1.toString();
      }

      if (var0 instanceof MultiSelectSetting) {
         LinkedHashSet var2 = new LinkedHashSet();
         if (var1 instanceof Collection) {
            for (Object var5 : (Collection)var1) {
               if (var5 != null) {
                  var2.add(var5.toString());
               }
            }
         }

         return var2;
      } else {
         return var0 instanceof ColorSetting ? check(var1) : var1;
      }
   }

   private static boolean handle(Setting var0, Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      }

      if (var1 == null || var2 == null) {
         return false;
      }

      if (var0 instanceof BooleanSetting || var0 instanceof ModeSetting || var0 instanceof StringSetting) {
         return Objects.equals(var1, var2);
      }

      if (var0 instanceof NumberSetting) {
         return Math.abs(((Number)var1).doubleValue() - ((Number)var2).doubleValue()) <= 1.0E-6;
      }

      if (var0 instanceof MultiSelectSetting) {
         if (!(var1 instanceof Collection var7 && var2 instanceof Collection var10)) {
            return false;
         } else {
            return var7.size() != var10.size() ? false : new LinkedHashSet<>(handle(var7)).equals(new LinkedHashSet<>(handle(var10)));
         }
      } else if (var0 instanceof ColorSetting) {
         if (var1 instanceof ColorVector var3 && var2 instanceof ColorVector var9) {
            return var3.equals(var9);
         } else if (var1 instanceof Number var5 && var2 instanceof Number var8) {
            return var5.intValue() == var8.intValue();
         } else {
            return var1 instanceof String var6 && var2 instanceof String var4 ? var6.equalsIgnoreCase(var4) : false;
         }
      } else {
         return Objects.equals(var1, var2);
      }
   }

   private static List<String> handle(Collection<?> var0) {
      ArrayList var1 = new ArrayList(var0.size());

      for (Object var3 : var0) {
         if (var3 != null) {
            var1.add(var3.toString());
         }
      }

      return var1;
   }

   private static ColorVector check(Object var0) {
      if (var0 instanceof ColorVector var7) {
         return ColorVector.handle(var7.handle(), var7.process(), var7.compute(), var7.resolve());
      } else if (var0 instanceof Number var6) {
         return ColorVector.handle(var6.intValue());
      } else if (var0 instanceof String var1) {
         try {
            String var2 = var1.startsWith("#") ? var1.substring(1) : var1;
            int var3 = (int)Long.parseUnsignedLong(var2, 16);
            int var4 = var2.length() > 6 ? var3 : 0xFF000000 | var3;
            return ColorVector.handle(var4);
         } catch (NumberFormatException var5) {
            throw new IllegalArgumentException("Invalid colour string: " + var1, var5);
         }
      } else {
         throw new IllegalArgumentException("Unsupported colour value type: " + var0.getClass().getName());
      }
   }

   public enum Mode {
      MODULE,
      SETTING;
   }
}
