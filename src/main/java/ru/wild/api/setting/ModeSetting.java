package ru.wild.api.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import ru.wild.util.math.EaseTimer;
import ru.wild.util.math.SmoothTimer;

public class ModeSetting extends Setting {
   public final List<String> config;
   public String state;
   public String cache;
   public SmoothTimer output = new EaseTimer(300, 1.0);
   public int current;
   public boolean active;
   private final String mode;

   public ModeSetting(String var1, String var2, String... var3) {
      this.instance = var1;
      this.config = new ArrayList<>(Arrays.asList(var3));
      this.current = this.config.indexOf(var2);
      if (this.current < 0) {
         this.current = 0;
      }

      this.state = this.config.get(this.current);
      this.mode = this.state;
   }

   public String compute() {
      return this.state;
   }

   public boolean process(String var1) {
      return this.state.equalsIgnoreCase(var1);
   }

   public ModeSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   public void handle(List<String> var1) {
      String var2 = this.state;
      this.config.clear();
      this.config.addAll(var1);
      this.current = this.config.indexOf(var2);
      if (this.current < 0) {
         this.current = this.config.indexOf(this.mode);
      }

      if (this.current < 0) {
         this.current = 0;
      }

      this.state = this.config.get(this.current);
   }

   @Override
   public void process() {
      int var1 = this.config.indexOf(this.mode);
      if (var1 < 0) {
         var1 = 0;
      }

      this.current = var1;
      this.state = this.config.get(var1);
      this.active = false;
   }
}
