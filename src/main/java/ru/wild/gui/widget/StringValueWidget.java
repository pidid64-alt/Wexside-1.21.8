package ru.wild.gui.widget;

import java.util.Objects;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.SettingValue;

public final class StringValueWidget extends NewValueWidget<String> {
   public StringValueWidget(KeybindTargetModel var1, ModeSetting var2) {
      super(Objects.requireNonNull(var1, "model"), handle(var1, Objects.requireNonNull(var2, "setting")));
   }

   private static TextValueEditor handle(KeybindTargetModel var0, ModeSetting var1) {
      final String var2 = var1.state != null ? var1.state : "";
      SettingValue<String> var3 = handle(var0, var2, new NewValueWidget.Callback<String>() {
         public String handle(KeybindTargetModel var1) {
            Object var2x = var1.measure();
            return var2x != null ? var2x.toString() : var2;
         }

         public void handle(KeybindTargetModel var1, String var2x) {
            String var3x = var2x != null ? var2x : var2;
            var1.process(var3x);
         }
      });
      return new TextValueEditor(handle(var0), resolve(), var1, var3, "New Value");
   }
}
