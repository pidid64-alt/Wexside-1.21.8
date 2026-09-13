package ru.wild.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public final class FavoritesStorage {
   private static final FavoritesStorage instance = new FavoritesStorage();
   private static final int data = 8;
   private final LinkedHashSet<String> context = new LinkedHashSet<>();
   private final ArrayList<String> config = new ArrayList<>();
   private boolean state;

   private FavoritesStorage() {
   }

   public static FavoritesStorage handle() {
      return instance;
   }

   public synchronized Set<String> process() {
      this.update();
      return new LinkedHashSet<>(this.context);
   }

   public synchronized List<String> compute() {
      this.update();
      return new ArrayList<>(this.config);
   }

   public synchronized boolean handle(String var1) {
      this.update();
      return var1 != null && this.context.contains(var1);
   }

   public synchronized void process(String var1) {
      if (var1 != null && !var1.isBlank()) {
         this.update();
         if (!this.context.remove(var1)) {
            this.context.add(var1);
         }

         this.apply();
      }
   }

   public synchronized void compute(String var1) {
      if (var1 != null && !var1.isBlank()) {
         this.update();
         this.config.remove(var1);
         this.config.add(0, var1);

         while (this.config.size() > 8) {
            this.config.remove(this.config.size() - 1);
         }

         this.apply();
      }
   }

   private File resolve() {
      return new File(FoundryStorage.handle().resolve(), "library.json");
   }

   private void update() {
      if (!this.state) {
         this.state = true;

         try {
            File var1 = this.resolve();
            if (!var1.isFile()) {
               return;
            }

            JSONObject var2 = new JSONObject(new String(Files.readAllBytes(var1.toPath()), StandardCharsets.UTF_8));
            JSONArray var3 = var2.optJSONArray("favorites");
            if (var3 != null) {
               for (int var4 = 0; var4 < var3.length(); var4++) {
                  String var5 = var3.optString(var4, "");
                  if (!var5.isBlank()) {
                     this.context.add(var5);
                  }
               }
            }

            JSONArray var8 = var2.optJSONArray("recents");
            if (var8 != null) {
               for (int var9 = 0; var9 < var8.length() && this.config.size() < 8; var9++) {
                  String var6 = var8.optString(var9, "");
                  if (!var6.isBlank() && !this.config.contains(var6)) {
                     this.config.add(var6);
                  }
               }
            }
         } catch (Throwable var7) {
         }
      }
   }

   private void apply() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("favorites", new JSONArray(this.context));
         var1.put("recents", new JSONArray(this.config));
         Files.write(this.resolve().toPath(), var1.toString(2).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var2) {
      }
   }
}
