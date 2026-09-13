package ru.wild.api.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SettingGroup {
   private final ArrayList<Setting> instance = new ArrayList<>();

   public final void handle(Setting... var1) {
      this.instance.addAll(Arrays.asList(var1));
   }

   public final void handle(Collection<Setting> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.instance.removeAll(var1);
      }
   }

   public List<Setting> apply() {
      return this.instance.stream().filter(var0 -> {
         try {
            return !var0.data.get();
         } catch (Throwable var2) {
            return false;
         }
      }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public final List<Setting> select() {
      return this.instance;
   }
}
