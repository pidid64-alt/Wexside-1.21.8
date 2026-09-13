package ru.wild.api.setting;

import java.util.function.Supplier;

public class StringSetting extends Setting {
   public static final int config = 256;
   public String state;
   public String cache;
   public int output = 256;
   private final String active;
   public boolean current;

   public StringSetting(String var1, String var2) {
      this.instance = var1;
      this.state = handle(var2, 256);
      this.active = this.state;
   }

   public String compute() {
      return this.state;
   }

   public void process(String var1) {
      this.state = handle(var1, this.output);
   }

   public StringSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      this.state = handle(this.active, this.output);
      this.current = false;
   }

   public StringSetting handle(int var1) {
      this.output = Math.max(1, var1);
      if (this.state != null && this.state.length() > this.output) {
         this.state = this.state.substring(0, this.output);
      }

      return this;
   }

   private static String handle(String var0, int var1) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() > var1 ? var0.substring(0, var1) : var0;
      }
   }
}
