package ru.wild.api.setting;

import java.util.function.Supplier;

public class DynamicActionSetting extends ActionSetting {
   private final Supplier<String> cache;
   private Runnable output;

   public DynamicActionSetting(String var1, int var2, Supplier<String> var3) {
      super(var1, var2);
      this.cache = var3;
   }

   @Override
   public String update() {
      String var1 = this.cache == null ? null : this.cache.get();
      return var1 != null && !var1.isBlank() ? var1 : super.update();
   }

   @Override
   public void resolve() {
      if (this.output != null) {
         this.output.run();
      }
   }

   public DynamicActionSetting process(Runnable var1) {
      this.output = var1;
      return this;
   }

   public DynamicActionSetting process(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }
}
