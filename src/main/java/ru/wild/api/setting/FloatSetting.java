package ru.wild.api.setting;

import java.util.function.Supplier;

public class FloatSetting extends Setting {
   public float config;
   private final float state;

   public FloatSetting(float var1) {
      this.config = var1;
      this.state = var1;
   }

   public FloatSetting() {
      this.config = 15.0F;
      this.state = 15.0F;
   }

   public FloatSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   public float compute() {
      return this.config;
   }

   @Override
   public void process() {
      this.config = this.state;
   }
}
