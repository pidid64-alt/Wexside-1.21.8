package ru.wild.api.setting;

import java.util.function.Supplier;

public class ActionSetting extends Setting {
   public int config;
   public String state;
   private String cache = "Run";
   private Runnable output;
   private final int current;

   public ActionSetting(String var1, int var2) {
      this.instance = var1;
      this.config = var2;
      this.current = var2;
   }

   public int compute() {
      return this.config;
   }

   public void handle(int var1) {
      this.config = var1;
   }

   public void resolve() {
      this.config++;
      if (this.output != null) {
         this.output.run();
      }
   }

   public String update() {
      return this.cache;
   }

   public ActionSetting process(String var1) {
      this.cache = var1;
      return this;
   }

   public ActionSetting handle(Runnable var1) {
      this.output = var1;
      return this;
   }

   public ActionSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      this.config = this.current;
   }
}
