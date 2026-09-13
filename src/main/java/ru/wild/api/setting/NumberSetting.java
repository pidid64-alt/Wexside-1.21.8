package ru.wild.api.setting;

import java.util.function.Supplier;

public class NumberSetting extends Setting {
   public float config;
   public float state;
   public float cache;
   public float output;
   public float current;
   public boolean active;
   public boolean mode;
   public String selection;
   public String[] enabled;
   private final float renderer;

   public NumberSetting(String var1, float var2, float var3, float var4, float var5, boolean var6) {
      this.instance = var1;
      this.state = var3;
      this.config = var2;
      this.cache = var4;
      this.output = var5;
      this.selection = this.selection;
      this.mode = var6;
      this.renderer = var2;
   }

   public float compute() {
      return this.config;
   }

   public void handle(float var1) {
      if (!Float.isNaN(var1) && !Float.isInfinite(var1)) {
         this.config = Math.max(this.state, Math.min(this.cache, var1));
      }
   }

   public NumberSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      this.handle(this.renderer);
      this.active = false;
   }

   public NumberSetting handle(String... var1) {
      this.enabled = var1;
      return this;
   }

   public boolean resolve() {
      return this.enabled != null && this.enabled.length > 0;
   }

   public String process(float var1) {
      if (!this.resolve()) {
         return null;
      } else {
         int var2 = Math.round(this.cache - this.state);
         int var3 = Math.round(var1 - this.state);
         if (var2 > 0 && this.enabled.length == var2 + 1) {
            var3 = Math.max(0, Math.min(this.enabled.length - 1, var3));
            return this.enabled[var3];
         } else {
            float var4 = this.cache - this.state <= 0.0F ? 0.0F : (var1 - this.state) / (this.cache - this.state);
            int var5 = Math.max(0, Math.min(this.enabled.length - 1, Math.round(var4 * (this.enabled.length - 1))));
            return this.enabled[var5];
         }
      }
   }
}
