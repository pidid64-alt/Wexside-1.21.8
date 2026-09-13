package ru.wild.config;

import java.util.ArrayList;
import java.util.List;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;

public abstract class ConfigStore<T> {
   private List<T> instance = new ArrayList<>();

   public List<T> apply() {
      return this.instance;
   }

   @Compile
   public void handle(ArrayList<T> var1) {
      this.instance = var1;
   }

   static {
      Loader.initialize();
   }
}
