package ru.wild.api.setting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.wild.config.HudProfileConfig;

public abstract class ResettableSettingGroup {
   private static final String instance = "Сброс настроек";
   private static final String data = "До заводских";
   private final List<Setting> context = new ArrayList<>();
   private final DynamicActionSetting config = new DynamicActionSetting("Сброс настроек", 0, () -> "До заводских").process(() -> this.process());

   public void handle(Setting var1) {
      this.context.add(var1);
   }

   public void handle(Setting... var1) {
      if (var1 != null) {
         for (Setting var5 : var1) {
            this.handle(var5);
         }
      }
   }

   public void handle(Collection<Setting> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.context.removeAll(var1);
      }
   }

   public List<Setting> handle() {
      if (!this.compute()) {
         return this.context;
      }

      ArrayList var1 = new ArrayList<>(this.context);
      var1.add(this.config);
      return var1;
   }

   private void process() {
      for (Setting var2 : this.context) {
         if (this.process(var2)) {
            var2.process();
         }
      }

      HudProfileConfig.resolve();
   }

   private boolean compute() {
      for (Setting var2 : this.context) {
         if (this.process(var2)) {
            return true;
         }
      }

      return false;
   }

   private boolean process(Setting var1) {
      return var1 != null && !var1.context && !(var1 instanceof ActionSetting) && !(var1 instanceof FloatSetting);
   }
}
