package ru.wild.api.setting;

import java.util.function.Supplier;

public class Setting extends SettingGroup {
   public String instance;
   private String config;
   public Supplier<Boolean> data = () -> false;
   public boolean context = false;

   public String handle() {
      return this.config != null && !this.config.isBlank() ? this.config : this.instance;
   }

   public Setting handle(String var1) {
      this.config = var1;
      return this;
   }

   public Setting handle(boolean var1) {
      this.context = var1;
      return this;
   }

   public void process() {
   }
}
