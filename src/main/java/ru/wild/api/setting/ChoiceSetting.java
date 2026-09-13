package ru.wild.api.setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import ru.wild.util.math.DoubleAnimator;

public class ChoiceSetting extends Setting {
   public List<BooleanSetting> config;
   public boolean state;
   public boolean cache = false;
   public DoubleAnimator output = new DoubleAnimator();

   public ChoiceSetting(String var1, BooleanSetting... var2) {
      this.instance = var1;
      this.config = Arrays.asList(var2);
   }

   public ChoiceSetting process(boolean var1) {
      this.cache = var1;
      return this;
   }

   public int compute() {
      int var1 = 0;

      for (BooleanSetting var3 : this.config) {
         if (var3.resolve()) {
            var1++;
         }
      }

      return var1;
   }

   public boolean process(String var1) {
      for (BooleanSetting var3 : this.config) {
         if (var3.instance.equals(var1)) {
            return var3.compute();
         }
      }

      return false;
   }

   public boolean handle(int var1) {
      return var1 >= 0 && var1 < this.config.size() ? this.config.get(var1).compute() : false;
   }

   public ChoiceSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      this.state = false;

      for (BooleanSetting var2 : this.config) {
         if (var2 != null) {
            var2.process();
         }
      }
   }
}
