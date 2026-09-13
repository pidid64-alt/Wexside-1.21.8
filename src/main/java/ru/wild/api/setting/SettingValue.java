package ru.wild.api.setting;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import ru.wild.util.render.ColorVector;

public interface SettingValue<T> {
   T handle();

   void handle(T var1);

   T process();

   void compute();

   static SettingValue<?> handle(Setting var0) {
      Objects.requireNonNull(var0, "setting");
      if (var0 instanceof BooleanSetting var1) {
         return new SettingValue<Boolean>() {
            @Override
            public Boolean handle() {
               return var1.compute();
            }

            public void handle(Boolean var1x) {
               if (var1x != null) {
                  var1.process(var1x);
               }
            }

            @Override
            public Boolean process() {
               return false;
            }

            @Override
            public void compute() {
               var1.process(false);
            }
         };
      } else if (var0 instanceof NumberSetting var2) {
         return new SettingValue<Double>() {
            @Override
            public Double handle() {
               return (double)var2.compute();
            }

            public void handle(Double var1) {
               if (var1 != null) {
                  var2.config = var1.floatValue();
               }
            }

            @Override
            public Double process() {
               return (double)var2.state;
            }

            @Override
            public void compute() {
               var2.config = var2.state;
            }
         };
      } else if (var0 instanceof ModeSetting var3) {
         return new SettingValue<String>() {
            @Override
            public String handle() {
               return var3.compute();
            }

            public void handle(String var1) {
               if (var3.config.contains(var1)) {
                  var3.state = var1;
                  var3.current = var3.config.indexOf(var1);
               }
            }

            @Override
            public String process() {
               return var3.config.isEmpty() ? "" : var3.config.get(0);
            }

            @Override
            public void compute() {
               if (!var3.config.isEmpty()) {
                  var3.state = var3.config.get(0);
                  var3.current = 0;
               }
            }
         };
      } else if (var0 instanceof MultiSelectSetting var4) {
         return new SettingValue<Set<String>>() {
            @Override
            public Set<String> handle() {
               return new LinkedHashSet<>(var4.output != null ? var4.output : List.of());
            }

            public void handle(Set<String> var1) {
               if (var1 != null) {
                  var4.output = new ArrayList<>(var1);
               } else {
                  var4.output = new ArrayList<>();
               }
            }

            @Override
            public Set<String> process() {
               return new LinkedHashSet<>();
            }

            @Override
            public void compute() {
               var4.output = new ArrayList<>();
            }
         };
      } else {
         return var0 instanceof ColorSetting var5 ? new SettingValue<ColorVector>() {
            @Override
            public ColorVector handle() {
               return ColorVector.handle(var5.update(), var5.renderer, var5.handler, var5.animationDraw);
            }

            public void handle(ColorVector var1) {
               if (var1 != null) {
                  var5.handle(var1.handle());
                  var5.renderer = var1.process();
                  var5.handler = var1.compute();
                  var5.animationDraw = var1.resolve();
               }
            }

            @Override
            public ColorVector process() {
               return ColorVector.handle(0.0F, 1.0F, 1.0F, 1.0F);
            }

            @Override
            public void compute() {
               var5.state = 0.0F;
               var5.renderer = 1.0F;
               var5.handler = 1.0F;
               var5.animationDraw = 1.0F;
            }
         } : new SettingValue<Object>() {
            @Override
            public Object handle() {
               return null;
            }

            @Override
            public void handle(Object var1) {
            }

            @Override
            public Object process() {
               return null;
            }

            @Override
            public void compute() {
            }
         };
      }
   }
}
