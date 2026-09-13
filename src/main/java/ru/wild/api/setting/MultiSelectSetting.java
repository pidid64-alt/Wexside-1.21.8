package ru.wild.api.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MultiSelectSetting extends Setting {
   public List<String> config;
   public boolean state;
   public String cache;
   public List<String> output = new ArrayList<>();
   private Supplier<List<String>> active;
   protected List<String> current = new ArrayList<>();

   public MultiSelectSetting(String var1, String... var2) {
      this.instance = var1;
      this.config = Arrays.asList(var2);
      this.cache = this.cache;
      this.resolve();
   }

   public MultiSelectSetting(String var1, Supplier<List<String>> var2) {
      this.instance = var1;
      this.active = var2;
      this.config = new ArrayList<>();
      this.compute();
      this.resolve();
   }

   public MultiSelectSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   public MultiSelectSetting process(Supplier<List<String>> var1) {
      this.active = var1;
      this.compute();
      return this;
   }

   public List<String> compute() {
      if (this.active == null) {
         return this.config == null ? Collections.emptyList() : this.config;
      }

      List<String> var1;
      try {
         var1 = this.active.get();
      } catch (Throwable var5) {
         var1 = Collections.emptyList();
      }

      ArrayList<String> var2 = new ArrayList<>();
      if (var1 != null) {
         for (String var4 : var1) {
            if (var4 != null && !var4.isBlank() && !var2.contains(var4)) {
               var2.add(var4);
            }
         }
      }

      this.config = var2;
      if (this.output != null) {
         this.output.removeIf(var1x -> var1x == null || !var2.contains(var1x));
      } else {
         this.output = new ArrayList<>();
      }

      return this.config;
   }

   protected void resolve() {
      this.current = this.output == null ? new ArrayList<>() : new ArrayList<>(this.output);
   }

   @Override
   public void process() {
      this.compute();
      this.state = false;
      this.output = new ArrayList<>();
      if (this.current != null) {
         for (String var2 : this.current) {
            if (var2 != null && this.config != null && this.config.contains(var2)) {
               this.output.add(var2);
            }
         }
      }
   }

   public String update() {
      this.compute();
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < this.config.size(); var2++) {
         var1.append(this.config.get(var2));
         if (var2 == 2 && this.config.size() > 3) {
            var1.append("...");
            break;
         }

         if (var2 < this.config.size() - 1) {
            var1.append(", ");
         }
      }

      return var1.toString();
   }

   public boolean process(String var1) {
      this.compute();
      return this.output.contains(var1);
   }
}
