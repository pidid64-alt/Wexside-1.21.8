package ru.wild.gui.widget;

import java.util.Objects;
import ru.wild.api.setting.KeybindTargetModel;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.SettingValue;

public final class NumberValueWidget extends NewValueWidget<Double> {
   public NumberValueWidget(KeybindTargetModel var1, NumberSetting var2) {
      super(Objects.requireNonNull(var1, "model"), handle(var1, Objects.requireNonNull(var2, "setting")));
   }

   private static NumberValueEditor handle(KeybindTargetModel var0, NumberSetting var1) {
      final double var2 = var1.config;
      SettingValue<Double> var4 = handle(var0, var2, new NewValueWidget.Callback<Double>() {
         public Double handle(KeybindTargetModel var1) {
            return var1.measure() instanceof Number var3 ? var3.doubleValue() : var2;
         }

         public void handle(KeybindTargetModel var1, Double var2x) {
            double var3 = var2x != null ? var2x : var2;
            var1.process(var3);
         }
      });
      return new NumberValueEditor(handle(var0), resolve(), var1, var4, "New Value");
   }
}
