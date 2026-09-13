package ru.wild.gui.widget;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.api.setting.MultiSelectSetting;
import ru.wild.api.setting.SettingValue;

public final class ModelWidget extends NewValueWidget<Set<String>> {
   public ModelWidget(KeybindTargetModel var1, MultiSelectSetting var2) {
      super(Objects.requireNonNull(var1, "model"), handle(var1, Objects.requireNonNull(var2, "setting")));
   }

   private static ValueSelectorUI handle(KeybindTargetModel var0, MultiSelectSetting var1) {
      final LinkedHashSet var2 = new LinkedHashSet<>(var1.output != null ? var1.output : Collections.emptyList());
      SettingValue<Set<String>> var3 = handle(var0, var2, new NewValueWidget.Callback<Set<String>>() {
         public Set<String> handle(KeybindTargetModel var1) {
            Object var2x = var1.measure();
            return this.handle(var2x, var2);
         }

         public void handle(KeybindTargetModel var1, Set<String> var2x) {
            var1.process(this.handle(var2x, var2));
         }

         private LinkedHashSet<String> handle(Object var1, Set<String> var2x) {
            LinkedHashSet var3x = new LinkedHashSet();
            Collection var4 = null;
            boolean var5 = false;
            if (var1 instanceof Collection var6) {
               var4 = var6;
               var5 = true;
            } else if (var1 instanceof Set var7) {
               var4 = var7;
               var5 = true;
            }

            if (var4 != null) {
               for (Object var8 : var4) {
                  if (var8 != null) {
                     var3x.add(var8.toString());
                  }
               }
            } else if (var1 instanceof Object[] var11) {
               var5 = true;

               for (Object var10 : var11) {
                  if (var10 != null) {
                     var3x.add(var10.toString());
                  }
               }
            }

            if (!var5 && var2x != null) {
               var3x.addAll(var2x);
            }

            return var3x;
         }
      });
      return new ValueSelectorUI(handle(var0), resolve(), var1, var3, "New Value");
   }
}
