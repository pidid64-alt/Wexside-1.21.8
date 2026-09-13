package ru.wild.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import ru.wild.sdk.Compile;
import ru.wild.sdk.Loader;

public abstract class Command implements MinecraftContext {
   private final String instance;
   private final String data;
   private final String context;
   private final Map<String, Supplier<List<String>>> config = new HashMap<>();

   public Command(String var1, String var2, String var3) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
   }

   @Compile
   protected void handle(String var1, Supplier<List<String>> var2) {
      this.config.put(var1.toLowerCase(), var2);
   }

   public List<String> handle(String[] var1) {
      if (var1.length == 2) {
         return this.config.keySet().stream().filter(var1x -> var1x.startsWith(var1[1].toLowerCase())).toList();
      }

      if (var1.length == 3) {
         String var2 = var1[1].toLowerCase();
         if (this.config.containsKey(var2)) {
            return this.config.get(var2).get().stream().filter(var1x -> var1x.toLowerCase().startsWith(var1[2].toLowerCase())).toList();
         }
      }

      return new ArrayList<>();
   }

   public abstract void process(String[] var1);
   public String handle() {
      return this.instance;
   }
   public String process() {
      return this.data;
   }
   public String compute() {
      return this.context;
   }

   static {
      Loader.initialize();
   }
}
