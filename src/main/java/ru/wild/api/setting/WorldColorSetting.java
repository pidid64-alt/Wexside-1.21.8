package ru.wild.api.setting;

import java.awt.Color;
import java.util.function.Supplier;

public class WorldColorSetting extends ColorSetting {
   public WorldColorSetting(String var1, int var2) {
      super(var1, 0.0F);
      this.handle(var2);
      this.onTick();
   }

   public WorldColorSetting(String var1, Color var2) {
      super(var1, 0.0F);
      this.handle(var2);
      this.onTick();
   }

   public WorldColorSetting handle(Supplier<Boolean> var1) {
      super.process(var1);
      return this;
   }
}
