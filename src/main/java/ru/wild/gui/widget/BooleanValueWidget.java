package ru.wild.gui.widget;

import java.util.Objects;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.api.setting.SettingValue;

public final class BooleanValueWidget extends NewValueWidget<Boolean> {
   public BooleanValueWidget(KeybindTargetModel var1, BooleanSetting var2) {
      super(Objects.requireNonNull(var1, "model"), handle(var1, Objects.requireNonNull(var2, "setting")));
   }

   private static WidgetBinding handle(KeybindTargetModel var0, BooleanSetting var1) {
      Boolean var2 = Boolean.FALSE;
      SettingValue<Boolean> var3 = handle(var0, var2, new NewValueWidget.Callback<Boolean>() {
         public Boolean handle(KeybindTargetModel var1) {
            Object var2x = var1.measure();
            return Boolean.TRUE.equals(var2x);
         }

         public void handle(KeybindTargetModel var1, Boolean var2x) {
            var1.process(Boolean.TRUE.equals(var2x));
         }
      });
      return new WidgetBinding(handle(var0), resolve(), var1, var3, "New Value");
   }
}
