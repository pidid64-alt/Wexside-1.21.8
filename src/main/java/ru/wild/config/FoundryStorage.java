package ru.wild.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.wild.WildClient;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.SavedThemePreset;
import ru.wild.gui.theme.ThemeKeys;
import ru.wild.gui.theme.ThemeSourceMetadata;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderNodeRegistry;
import ru.wild.render.shader.ShaderPresetNameGenerator;

public final class FoundryStorage {
   private static final FoundryStorage instance = new FoundryStorage();
   private static final String data = "active.json";
   private static final String context = ".theme.json";
   private static final String config = ".wifd";
   private static final String state = ".json";
   private static final DateTimeFormatter cache = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
   private final File output;
   private final Map<LivePreviewRenderer, String> current = new EnumMap<>(LivePreviewRenderer.class);
   private final Map<String, SavedThemePreset> active = new HashMap<>();
   private boolean mode;

   private FoundryStorage() {
      File var1 = WildClient.instance != null && WildClient.instance.cache != null
         ? new File(WildClient.instance.cache, "foundry")
         : new File(WildClient.process(), "foundry");
      this.output = var1;
      if (!var1.exists() && !var1.mkdirs()) {
         System.out.println("[FoundryStorage] cannot create directory " + var1.getAbsolutePath());
      }
   }

   public static FoundryStorage handle() {
      return instance;
   }

   public synchronized void handle(ShaderNodeRegistry var1) {
      if (!this.mode) {
         this.mode = true;
         this.active.clear();
         if (this.output.isDirectory()) {
            this.prepare();
            File[] var2 = this.output.listFiles((var0, var1x) -> var1x.endsWith(".theme.json"));
            ArrayList<File> var3 = new ArrayList<>();
            if (var2 != null) {
               for (File var7 : var2) {
                  if (!compute(var7.getName())) {
                     var3.add(var7);
                  } else {
                     try {
                        SavedThemePreset var8 = this.handle(var7, new JSONObject(handle(var7)));
                        this.active.put(var8.handle(), var8);
                     } catch (Throwable var9) {
                        System.out.println("[FoundryStorage] skip " + var7.getName() + ": " + var9.getMessage());
                     }
                  }
               }
            }

            boolean var10 = false;

            for (File var12 : var3) {
               var10 |= this.process(var12, var1);
            }

            if (var10) {
               this.check();
            }
         }
      }
   }

   public synchronized List<SavedThemePreset> process() {
      ArrayList<SavedThemePreset> var1 = new ArrayList<>(this.active.values());
      var1.sort((var0, var1x) -> Long.compare(var1x.select(), var0.select()));
      return var1;
   }

   public synchronized List<SavedThemePreset> handle(LivePreviewRenderer var1) {
      if (var1 == null) {
         return Collections.emptyList();
      }

      ArrayList<SavedThemePreset> var2 = new ArrayList<>();

      for (SavedThemePreset var4 : this.active.values()) {
         if (var1.handle().equals(var4.compute())) {
            var2.add(var4);
         }
      }

      var2.sort((var0, var1x) -> Long.compare(var1x.select(), var0.select()));
      return var2;
   }

   public synchronized SavedThemePreset handle(String var1) {
      return var1 == null ? null : this.active.get(var1);
   }

   public synchronized SavedThemePreset handle(LivePreviewRenderer var1, ShaderGraph var2, String var3, String var4) {
      if (var1 != null && var2 != null) {
         long var5 = System.currentTimeMillis();
         String var7 = var3 != null && !var3.isBlank() ? var3.trim() : var2.handle().process();
         if (var7 == null || var7.isBlank()) {
            var7 = ShaderPresetNameGenerator.handle();
         }

         ThemeSourceMetadata var8 = var2.handle();
         var8.handle(var7, execute());
         var8.handle(var7);
         var8.process(var5);
         var8.update("local");
         String var9 = ThemeKeys.handle(var2);
         SavedThemePreset var10 = var4 == null ? null : this.active.get(var4);
         if (var10 == null) {
            var10 = new SavedThemePreset(
               this.onTick(), var7, var1.handle(), var9, var8.compute(), var8.resolve(), var8.update(), "user", "saved", var8.check(), var5, var8.select()
            );
            this.active.put(var10.handle(), var10);
         } else {
            var10.handle(var7);
            var10.process(var1.handle());
            var10.compute(var9);
            var10.resolve(var8.compute());
            var10.update(var8.resolve());
            var10.apply(var8.update());
            var10.execute("user");
            var10.prepare("saved");
            var10.handle(var8.check());
            var10.process(var5);
            var10.handle(var8.select());
         }

         try {
            this.handle(var10, var8);
         } catch (IOException var12) {
            System.out.println("[FoundryStorage] save failed: " + var12.getMessage());
         }

         return var10;
      } else {
         return null;
      }
   }

   public synchronized SavedThemePreset handle(LivePreviewRenderer var1, ShaderGraph var2, String var3) {
      if (var1 != null && var2 != null) {
         SavedThemePreset var4 = var3 == null ? null : this.active.get(var3);
         String var5 = var4 == null ? var2.handle().process() : var4.process();
         return this.handle(var1, var2, var5, var3);
      } else {
         return null;
      }
   }

   public synchronized File compute() {
      File var1 = new File(this.output, "shaders");
      if (!var1.exists()) {
         var1.mkdirs();
      }

      return var1;
   }

   public synchronized File resolve() {
      if (!this.output.exists()) {
         this.output.mkdirs();
      }

      return this.output;
   }

   public synchronized File process(LivePreviewRenderer var1, ShaderGraph var2, String var3) {
      if (var2 == null) {
         return null;
      }

      LivePreviewRenderer var4 = var1 == null ? LivePreviewRenderer.handle(var2.process()) : var1;
      String var5 = var3 != null && !var3.isBlank() ? var3.trim() : var2.handle().process();
      if (var5 == null || var5.isBlank()) {
         var5 = ShaderPresetNameGenerator.handle();
      }

      ThemeSourceMetadata var6 = var2.handle();
      var6.handle(var5, execute());
      var6.handle(var5);
      var6.process(System.currentTimeMillis());
      var6.update("shared");
      String var7 = resolve(var5);
      String var8 = LocalDateTime.now().format(cache);
      File var9 = new File(this.compute(), var7 + "_" + var8 + ".wifd");

      try {
         var2.handle(var4.handle());
         JSONObject var10 = new JSONObject();
         var10.put("version", 4);
         var10.put("type", "wild_foundry");
         var10.put("target", var4.handle());
         var10.put("metadata", ThemeKeys.handle(var6));
         var10.put("graph", ThemeKeys.process(var2));
         Files.write(var9.toPath(), var10.toString(2).getBytes(StandardCharsets.UTF_8));
         return var9;
      } catch (Throwable var11) {
         System.out.println("[FoundryStorage] shared export failed: " + var11.getMessage());
         return null;
      }
   }

   public synchronized List<File> update() {
      File var1 = this.compute();
      File[] var2 = var1.listFiles((var0, var1x) -> {
         if (var1x == null) {
            return false;
         }

         String var2x = var1x.toLowerCase(Locale.ROOT);
         return var2x.endsWith(".wifd") || var2x.endsWith(".json");
      });
      if (var2 != null && var2.length != 0) {
         ArrayList<File> var3 = new ArrayList<>(List.of(var2));
         var3.sort((var0, var1x) -> Long.compare(var1x.lastModified(), var0.lastModified()));
         return var3;
      } else {
         return List.of();
      }
   }

   public synchronized ShaderGraph handle(File var1, ShaderNodeRegistry var2) {
      if (var1 != null && var2 != null && var1.isFile()) {
         try {
            String var3 = handle(var1);
            JSONObject var4 = new JSONObject(var3);
            JSONObject var5 = var4.optJSONObject("graph");
            if (var5 != null) {
               ShaderGraph var10 = ThemeKeys.handle(var5, var2);
               String var12 = var4.optString("target", "");
               if (!var12.isBlank()) {
                  var10.handle(var12);
               }

               ThemeSourceMetadata var13 = ThemeKeys.handle(var4.optJSONObject("metadata"), var4);
               var13.update("imported");
               var13.handle(var4.optString("displayName", ShaderPresetNameGenerator.handle()), var4.optString("author", execute()));
               var10.handle(var13);
               return var10;
            }

            String var6 = var4.optString("wildTheme", "");
            if (!var6.isBlank()) {
               ShaderGraph var11 = ThemeKeys.handle(var6, var2);
               ThemeSourceMetadata var8 = ThemeKeys.handle(var4.optJSONObject("metadata"), var4);
               var8.update("imported");
               var8.handle(var4.optString("displayName", ShaderPresetNameGenerator.handle()), var4.optString("author", execute()));
               var11.handle(var8);
               return var11;
            }

            if (var4.has("nodes") && var4.has("connections")) {
               ShaderGraph var7 = ThemeKeys.handle(var4, var2);
               var7.handle().update("imported");
               var7.handle().handle(var4.optString("displayName", ShaderPresetNameGenerator.handle()), var4.optString("author", execute()));
               return var7;
            }
         } catch (Throwable var9) {
            System.out.println("[FoundryStorage] shared import failed: " + var9.getMessage());
         }

         return null;
      } else {
         return null;
      }
   }

   public synchronized boolean process(String var1) {
      if (var1 == null) {
         return false;
      }

      SavedThemePreset var2 = this.active.remove(var1);
      if (var2 == null) {
         return false;
      }

      for (Entry var4 : new ArrayList<>(this.current.entrySet())) {
         if (var1.equals(var4.getValue())) {
            this.current.remove(var4.getKey());
         }
      }

      try {
         Files.deleteIfExists(new File(this.output, var1).toPath());
      } catch (IOException var5) {
      }

      this.check();
      return true;
   }

   public synchronized ShaderGraph handle(String var1, ShaderNodeRegistry var2) {
      SavedThemePreset var3 = this.active.get(var1);
      if (var3 == null) {
         return null;
      }

      try {
         ShaderGraph var4 = ThemeKeys.handle(var3.resolve(), var2);
         var4.handle().handle(var3.process(), var3.update().isBlank() ? execute() : var3.update());
         var4.handle().handle(var3.process());
         if (!var3.update().isBlank()) {
            var4.handle().process(var3.update());
         }

         var4.handle().compute(var3.apply());
         var4.handle().resolve(var3.execute());
         var4.handle().handle(var3.onTick());
         var4.handle().process(var3.select());
         var4.handle().handle(var3.refresh());
         return var4;
      } catch (Throwable var5) {
         return null;
      }
   }

   public synchronized void handle(LivePreviewRenderer var1, String var2) {
      if (var1 != null) {
         if (var2 == null || var2.isBlank()) {
            this.current.remove(var1);
         } else if (this.active.containsKey(var2)) {
            this.current.put(var1, var2);
         }

         this.check();
      }
   }

   public synchronized String process(LivePreviewRenderer var1) {
      return this.current.get(var1);
   }

   public synchronized SavedThemePreset compute(LivePreviewRenderer var1) {
      String var2 = this.current.get(var1);
      return var2 == null ? null : this.active.get(var2);
   }

   public synchronized JSONArray apply() {
      JSONArray var1 = new JSONArray();

      for (SavedThemePreset var3 : this.process()) {
         JSONObject var4 = new JSONObject();
         var4.put("fileName", var3.handle());
         var4.put("displayName", var3.process());
         var4.put("target", var3.compute());
         var4.put("author", var3.update());
         var4.put("description", var3.apply());
         var4.put("complexity", var3.execute());
         var4.put("source", var3.prepare());
         var4.put("compileStatus", var3.check());
         var4.put("createdAt", var3.onTick());
         var4.put("updatedAt", var3.select());
         var4.put("favorite", var3.refresh());
         var1.put(var4);
      }

      return var1;
   }

   private void prepare() {
      File var1 = new File(this.output, "active.json");
      if (var1.exists()) {
         try {
            JSONObject var2 = new JSONObject(handle(var1));

            for (LivePreviewRenderer var6 : LivePreviewRenderer.values()) {
               String var7 = var2.optString(var6.handle(), null);
               if (var7 != null && !var7.isBlank()) {
                  this.current.put(var6, var7);
               }
            }
         } catch (Throwable var8) {
            System.out.println("[FoundryStorage] cannot read active bindings: " + var8.getMessage());
         }
      }
   }

   private void check() {
      try {
         JSONObject var1 = new JSONObject();

         for (Entry var3 : this.current.entrySet()) {
            var1.put(((LivePreviewRenderer)var3.getKey()).handle(), var3.getValue());
         }

         Files.write(new File(this.output, "active.json").toPath(), var1.toString(2).getBytes(StandardCharsets.UTF_8));
      } catch (IOException var4) {
         System.out.println("[FoundryStorage] cannot persist active bindings: " + var4.getMessage());
      }
   }

   private SavedThemePreset handle(File var1, JSONObject var2) {
      String var3 = var2.optString("wildTheme", "");
      ThemeSourceMetadata var4 = ThemeKeys.handle(var2.optJSONObject("metadata"), var2);
      if (var4.process().isBlank()) {
         var4.handle(compute(var1.getName()) ? ShaderPresetNameGenerator.handle() : var1.getName().replace(".theme.json", ""));
      }

      var4.handle(var4.process(), execute());
      long var5 = var4.onTick() > 0L ? var4.onTick() : var2.optLong("updatedAt", var1.lastModified());
      String var7 = var2.optString("target", "preview");
      String var8 = var2.optString("source", var4.apply().isBlank() ? "user" : var4.apply());
      String var9 = var2.optString("compileStatus", "saved");
      return new SavedThemePreset(
         var1.getName(), var4.process(), var7, var3, var4.compute(), var4.resolve(), var4.update(), var8, var9, var4.check(), var5, var4.select()
      );
   }

   private boolean process(File var1, ShaderNodeRegistry var2) {
      String var3 = var1.getName();
      boolean var5 = false;

      try {
         JSONObject var6 = new JSONObject(handle(var1));
         SavedThemePreset var7 = this.handle(var1, var6);
         if (!this.process(var7.resolve(), var2)) {
            JSONObject var8 = var6.optJSONObject("graph");
            if (var8 != null) {
               ShaderGraph var9 = ThemeKeys.handle(var8, var2);
               if (var9 != null) {
                  var7.compute(ThemeKeys.handle(var9));
               }
            }
         }

         if (!this.process(var7.resolve(), var2)) {
            System.out.println("[FoundryStorage] keeping legacy file without loadable payload: " + var3);
            return false;
         }

         SavedThemePreset var12 = this.handle(var7);
         String var4;
         if (var12 != null) {
            var4 = var12.handle();
         } else {
            var4 = this.onTick();
            ThemeSourceMetadata var13 = ThemeKeys.handle(var6.optJSONObject("metadata"), var6);
            var13.handle(var7.process(), execute());
            var13.handle(var7.process());
            var13.handle(var7.onTick());
            var13.process(var7.select());
            var13.handle(var7.refresh());
            SavedThemePreset var10 = new SavedThemePreset(
               var4,
               var7.process(),
               var7.compute(),
               var7.resolve(),
               var7.update(),
               var7.apply(),
               var7.execute(),
               var7.prepare(),
               var7.check(),
               var7.onTick(),
               var7.select(),
               var7.refresh()
            );
            this.handle(var10, var13);
            this.active.put(var4, var10);
         }

         for (Entry var15 : new ArrayList<>(this.current.entrySet())) {
            if (var3.equals(var15.getValue())) {
               this.current.put((LivePreviewRenderer)var15.getKey(), var4);
               var5 = true;
            }
         }

         if (var5) {
            this.check();
         }

         Files.deleteIfExists(var1.toPath());
      } catch (Throwable var11) {
         System.out.println("[FoundryStorage] legacy migration failed for " + var3 + ": " + var11.getMessage());
         return var5;
      }

      return var5;
   }

   private boolean process(String var1, ShaderNodeRegistry var2) {
      if (var1 != null && !var1.isBlank()) {
         try {
            return ThemeKeys.handle(var1, var2) != null;
         } catch (Throwable var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   private SavedThemePreset handle(SavedThemePreset var1) {
      for (SavedThemePreset var3 : this.active.values()) {
         if (var3.process().equals(var1.process()) && var3.compute().equals(var1.compute()) && var3.resolve().equals(var1.resolve())) {
            return var3;
         }
      }

      return null;
   }

   private void handle(SavedThemePreset var1, ThemeSourceMetadata var2) throws IOException {
      JSONObject var3 = new JSONObject();
      var3.put("version", 4);
      var3.put("target", var1.compute());
      var3.put("source", var1.prepare());
      var3.put("compileStatus", var1.check());
      var3.put("wildTheme", var1.resolve());
      var3.put("metadata", ThemeKeys.handle(var2));
      Files.write(new File(this.output, var1.handle()).toPath(), var3.toString(2).getBytes(StandardCharsets.UTF_8));
   }

   private String onTick() {
      String var1;
      do {
         var1 = UUID.randomUUID() + ".theme.json";
      } while (this.active.containsKey(var1) || new File(this.output, var1).exists());

      return var1;
   }

   private static boolean compute(String var0) {
      if (var0 != null && var0.endsWith(".theme.json")) {
         String var1 = var0.substring(0, var0.length() - ".theme.json".length());
         if (var1.length() != 36) {
            return false;
         }

         try {
            UUID.fromString(var1);
            return true;
         } catch (IllegalArgumentException var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static String handle(File var0) throws IOException {
      return new String(Files.readAllBytes(var0.toPath()), StandardCharsets.UTF_8);
   }

   public static String execute() {
      if (WildClient.mode != null && !WildClient.mode.isBlank()) {
         return WildClient.mode.trim();
      }

      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0 != null && var0.getSession() != null && var0.getSession().getUsername() != null && !var0.getSession().getUsername().isBlank()
         ? var0.getSession().getUsername().trim()
         : "Unknown";
   }

   public synchronized int handle(Set<String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         int var2 = 0;

         for (SavedThemePreset var4 : new ArrayList<>(this.active.values())) {
            if (var1.contains(PresetManager.onTick(var4.process())) && this.process(var4.handle())) {
               var2++;
            }
         }

         return var2;
      } else {
         return 0;
      }
   }

   private static String resolve(String var0) {
      String var1 = var0 == null ? "theme" : var0.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
      return var1.isBlank() ? "theme" : var1;
   }
}
