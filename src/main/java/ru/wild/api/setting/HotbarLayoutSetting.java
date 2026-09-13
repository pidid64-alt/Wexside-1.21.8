package ru.wild.api.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Arrays;
import java.util.function.Supplier;

public class HotbarLayoutSetting extends Setting {
   private static final int config = 9;
   private final String[] state = new String[9];
   private final String[] cache = new String[9];

   public HotbarLayoutSetting(String var1) {
      this.instance = var1;
      Arrays.fill(this.state, "");
      Arrays.fill(this.cache, "");
   }

   public String handle(int var1) {
      if (var1 >= 0 && var1 < 9) {
         return this.state[var1] == null ? "" : this.state[var1];
      } else {
         return "";
      }
   }

   public void handle(int var1, String var2) {
      if (var1 >= 0 && var1 < 9) {
         this.state[var1] = var2 == null ? "" : var2.trim();
      }
   }

   public void process(int var1) {
      this.handle(var1, "");
   }

   public void compute() {
      Arrays.fill(this.state, "");
   }

   public boolean resolve() {
      for (String var4 : this.state) {
         if (var4 != null && !var4.isBlank()) {
            return false;
         }
      }

      return true;
   }

   public String[] update() {
      return Arrays.copyOf(this.state, this.state.length);
   }

   public JsonArray execute() {
      JsonArray var1 = new JsonArray();

      for (String var5 : this.state) {
         var1.add(var5 == null ? "" : var5);
      }

      return var1;
   }

   public void handle(JsonElement var1) {
      Arrays.fill(this.state, "");
      if (var1 != null && var1.isJsonArray()) {
         JsonArray var2 = var1.getAsJsonArray();

         for (int var3 = 0; var3 < Math.min(9, var2.size()); var3++) {
            try {
               this.state[var3] = var2.get(var3).getAsString();
            } catch (Throwable var5) {
               this.state[var3] = "";
            }
         }
      }
   }

   public HotbarLayoutSetting handle(Supplier<Boolean> var1) {
      this.data = var1;
      return this;
   }

   @Override
   public void process() {
      System.arraycopy(this.cache, 0, this.state, 0, 9);
   }
}
