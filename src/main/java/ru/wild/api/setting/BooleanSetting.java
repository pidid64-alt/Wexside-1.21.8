package ru.wild.api.setting;

import java.util.function.Supplier;
import ru.wild.util.math.DoubleAnimator;

public class BooleanSetting extends Setting {
   private boolean active;
   private final boolean mode;
   public String config;
   public DoubleAnimator state = new DoubleAnimator();
   public int cache = -1;
   public boolean output = false;
   public boolean current = false;

   public BooleanSetting(String var1, boolean var2) {
      this.instance = var1;
      this.active = var2;
      this.mode = var2;
      this.config = this.config;
   }

   public boolean compute() {
      return this.cache != -1 && this.output ? this.active || KeybindSetting.process(this.cache) : this.active;
   }

   public boolean resolve() {
      return this.active;
   }

   public void process(boolean var1) {
      this.active = var1;
   }

   public BooleanSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      this.process(this.mode);
      this.cache = -1;
      this.output = false;
      this.current = false;
   }
}
