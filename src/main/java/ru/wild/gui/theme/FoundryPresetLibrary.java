package ru.wild.gui.theme;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.wild.WildClient;
import ru.wild.render.texture.AssetCategory;

public final class FoundryPresetLibrary {
   private static final String instance = "assets/wild/studio/presets/";
   private static FoundryPresetLibrary data;
   private final File context;
   private final List<ThemeParser> config = new ArrayList<>();
   private final Map<String, AssetCategory> state = new HashMap<>();
   private final Map<String, String> cache = new HashMap<>();
   private final Map<String, String> output = new HashMap<>();
   private final Map<String, AssetCategory> current = new LinkedHashMap<>();
   private String active = "";
   private boolean mode;
   private boolean selection;

   private FoundryPresetLibrary() {
      this.context = new File(WildClient.process(), "avatars");
   }

   public static FoundryPresetLibrary handle() {
      if (data == null) {
         data = new FoundryPresetLibrary();
      }

      return data;
   }

   public File process() {
      return this.context;
   }

   public synchronized void compute() {
      if (!this.selection) {
         this.selection = true;

         try {
            if (!this.context.exists()) {
               this.context.mkdirs();
            }

            this.select();
            this.onTick();
            this.prepare();
         } catch (Throwable var2) {
            System.out.println("[Studio] library init failed: " + var2.getClass().getSimpleName() + ": " + var2.getMessage());
         }
      }
   }

   public synchronized void resolve() {
      this.select();
      this.prepare();
   }

   public synchronized List<ThemeParser> update() {
      return new ArrayList<>(this.config);
   }

   public synchronized List<ThemeParser> handle(AssetCategory var1) {
      ArrayList var2 = new ArrayList();

      for (ThemeParser var4 : this.config) {
         if (var4.prepare() == var1) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public synchronized ThemeParser apply() {
      for (ThemeParser var2 : this.config) {
         if (var2.handle().equals(this.active)) {
            return var2;
         }
      }

      return null;
   }

   public synchronized void handle(ThemeParser var1) {
      this.active = var1 == null ? "" : var1.handle();
      this.mode = var1 != null;
      this.refresh();
   }

   public synchronized boolean execute() {
      return this.mode && !this.active.isEmpty();
   }

   public synchronized void handle(boolean var1) {
      this.mode = var1 && !this.active.isEmpty();
      this.refresh();
   }

   public synchronized void handle(ThemeParser var1, AssetCategory var2) {
      if (var1 != null && var2 != null) {
         var1.handle(var2);
         this.state.put(var1.handle(), var2);
         this.refresh();
      }
   }

   public synchronized void handle(ThemeParser var1, String var2) {
      if (var1 != null) {
         String var3 = var2 == null ? "" : var2.trim();
         if (var3.isEmpty()) {
            this.cache.remove(var1.handle());
            var1.handle((String)null);
         } else {
            this.cache.put(var1.handle(), var3);
            var1.handle(var3);
         }

         this.config.sort((var0, var1x) -> var0.resolve().compareToIgnoreCase(var1x.resolve()));
         this.refresh();
      }
   }

   public synchronized void process(ThemeParser var1, String var2) {
      if (var1 != null) {
         String var3 = var2 == null ? "" : var2.trim();
         var1.process(var3);
         if (var3.isEmpty()) {
            this.output.remove(var1.handle());
         } else {
            this.output.put(var1.handle(), var3);
         }

         this.refresh();
      }
   }

   public synchronized boolean process(ThemeParser var1) {
      if (var1 == null) {
         return false;
      }

      boolean var2 = handle(var1.process());
      this.config.remove(var1);
      this.cache.remove(var1.handle());
      this.output.remove(var1.handle());
      this.state.remove(var1.handle());
      if (var1.handle().equals(this.active)) {
         this.active = "";
         this.mode = false;
      }

      this.refresh();
      return var2;
   }

   private static boolean handle(File var0) {
      if (var0 == null) {
         return false;
      }

      File[] var1 = var0.listFiles();
      if (var1 != null) {
         for (File var5 : var1) {
            handle(var5);
         }
      }

      return var0.delete();
   }

   public synchronized String handle(File var1, AssetCategory var2) {
      if (var1 != null && var1.exists()) {
         try {
            String var3 = resolve(compute(var1.getName()));
            if (var3.isEmpty()) {
               var3 = "import";
            }

            File var4 = this.process(var3);
            if (var1.isDirectory()) {
               this.handle(var1.toPath(), var4.toPath());
            } else {
               String var5 = resolve(var1);
               if ("rar".equals(var5) || "7z".equals(var5)) {
                  return "Это " + var5.toUpperCase(Locale.ROOT) + ", не .zip — распакуйте вручную";
               }

               if (!"zip".equals(var5)) {
                  return "Нужен .zip или папка";
               }

               try (InputStream var6 = Files.newInputStream(var1.toPath())) {
                  this.handle(var6, var4);
               }
            }

            String var13 = this.compute(var4) + "/";
            int var14 = this.config.size();
            this.prepare();
            int var7 = 0;

            for (ThemeParser var9 : this.config) {
               if (var9.handle().startsWith(var13)) {
                  if (this.state.get(var9.handle()) == null) {
                     var9.handle(var2);
                     this.state.put(var9.handle(), var2);
                  }

                  var7++;
               }
            }

            this.refresh();
            return var7 == 0 ? "Аватары не найдены (нет avatar.json)" : "Импортировано: " + var7 + (this.config.size() > var14 ? "" : "");
         } catch (Throwable var12) {
            return "Ошибка: " + var12.getClass().getSimpleName();
         }
      } else {
         return "Файл не найден";
      }
   }

   private void prepare() {
      this.config.clear();
      if (this.context.isDirectory()) {
         this.check();
         ArrayList var1 = new ArrayList();
         this.handle(this.context, var1, 0);

         for (File var3 : (List<File>) var1) {
            String var4 = this.compute(var3);
            AssetCategory var5 = this.state.get(var4);
            ThemeParser var6 = ThemeParser.handle(var4, var3, var5 != null ? var5 : this.handle(var4));
            if (var6 != null) {
               if (var5 == null) {
                  try {
                     var6.handle(var6.select());
                  } catch (Throwable var9) {
                  }
               }

               String var7 = this.cache.get(var4);
               if (var7 != null && !var7.isEmpty()) {
                  var6.handle(var7);
               }

               String var8 = this.output.get(var4);
               if (var8 != null && !var8.isEmpty()) {
                  var6.process(var8);
               }

               this.config.add(var6);
            }
         }

         this.config.sort((var0, var1x) -> var0.resolve().compareToIgnoreCase(var1x.resolve()));
      }
   }

   private void handle(File var1, List<File> var2, int var3) {
      if (var1 != null && var1.isDirectory() && var3 <= 8) {
         if (this.process(var1)) {
            var2.add(var1);
         } else {
            File[] var4 = var1.listFiles();
            if (var4 != null) {
               for (File var8 : var4) {
                  if (var8.isDirectory()) {
                     this.handle(var8, var2, var3 + 1);
                  }
               }
            }
         }
      }
   }

   private void check() {
      File[] var1 = this.context.listFiles();
      if (var1 != null) {
         for (File var5 : var1) {
            if (var5.isFile() && var5.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
               if (!"zip".equals(resolve(var5))) {
                  System.out.println("[Studio] skipping non-zip archive (RAR/7z?): " + var5.getName());
               } else {
                  try {
                     String var6 = resolve(compute(var5.getName()));
                     if (var6.isEmpty()) {
                        var6 = "import";
                     }

                     File var7 = this.process(var6);

                     try (InputStream var8 = Files.newInputStream(var5.toPath())) {
                        this.handle(var8, var7);
                     }

                     var5.delete();
                  } catch (Throwable var13) {
                     System.out.println("[Studio] loose import failed " + var5.getName() + ": " + var13.getMessage());
                  }
               }
            }
         }
      }
   }

   private boolean process(File var1) {
      File[] var2 = var1.listFiles();
      if (var2 == null) {
         return false;
      }

      for (File var6 : var2) {
         if (var6.isFile() && var6.getName().toLowerCase(Locale.ROOT).endsWith(".bbmodel")) {
            return true;
         }
      }

      return false;
   }

   private AssetCategory handle(String var1) {
      for (Entry var3 : this.current.entrySet()) {
         if (var1.startsWith((String)var3.getKey() + "/") || var1.equals(var3.getKey())) {
            return (AssetCategory)var3.getValue();
         }
      }

      return AssetCategory.MODELS;
   }

   private String compute(File var1) {
      String var2 = this.context.getAbsolutePath();
      String var3 = var1.getAbsolutePath();
      String var4 = var3.length() > var2.length() ? var3.substring(var2.length()) : var3;
      var4 = var4.replace('\\', '/');

      while (var4.startsWith("/")) {
         var4 = var4.substring(1);
      }

      return var4;
   }

   private void onTick() {
      String var1 = this.update("assets/wild/studio/presets/index.json");
      if (var1 != null) {
         JSONObject var2 = new JSONObject(var1);
         JSONArray var3 = var2.optJSONArray("presets");
         if (var3 != null) {
            for (int var4 = 0; var4 < var3.length(); var4++) {
               JSONObject var5 = var3.optJSONObject(var4);
               if (var5 != null) {
                  String var6 = var5.optString("file", "");
                  AssetCategory var7 = AssetCategory.handle(var5.optString("category", "models"));
                  if (!var6.isEmpty()) {
                     String var8 = resolve(compute(var6));
                     this.current.put(var8, var7);
                     File var9 = new File(this.context, var8);
                     if (!var9.isDirectory()) {
                        byte[] var10 = this.apply("assets/wild/studio/presets/" + var6);
                        if (var10 != null) {
                           try {
                              this.handle(new ByteArrayInputStream(var10), var9);
                           } catch (IOException var12) {
                              System.out.println("[Studio] preset seed failed " + var6 + ": " + var12.getMessage());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static String resolve(File var0) {
      try (InputStream var1 = Files.newInputStream(var0.toPath())) {
         byte[] var2 = var1.readNBytes(4);
         if (var2.length >= 2 && var2[0] == 80 && var2[1] == 75) {
            return "zip";
         }

         if (var2.length >= 4 && (var2[0] & 255) == 82 && (var2[1] & 255) == 97 && (var2[2] & 255) == 114 && (var2[3] & 255) == 33) {
            return "rar";
         }

         if (var2.length >= 4 && (var2[0] & 255) == 55 && (var2[1] & 255) == 122 && (var2[2] & 255) == 188 && (var2[3] & 255) == 175) {
            return "7z";
         }
      } catch (IOException var6) {
      }

      return "unknown";
   }

   private void handle(InputStream var1, File var2) throws IOException {
      if (!var2.exists()) {
         var2.mkdirs();
      }

      Path var3 = var2.toPath().normalize();

      ZipEntry var5;
      try (ZipInputStream var4 = new ZipInputStream(var1)) {
         while ((var5 = var4.getNextEntry()) != null) {
            Path var6 = var3.resolve(var5.getName()).normalize();
            if (var6.startsWith(var3)) {
               if (var5.isDirectory()) {
                  Files.createDirectories(var6);
               } else {
                  Files.createDirectories(var6.getParent());
                  Files.copy(var4, var6, StandardCopyOption.REPLACE_EXISTING);
               }

               var4.closeEntry();
            }
         }
      }
   }

   private void handle(Path var1, Path var2) throws IOException {
      Files.walk(var1).forEach(var2x -> {
         try {
            Path var3 = var2.resolve(var1.relativize(var2x).toString());
            if (Files.isDirectory(var2x)) {
               Files.createDirectories(var3);
            } else {
               Files.createDirectories(var3.getParent());
               Files.copy(var2x, var3, StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      });
   }

   private File process(String var1) {
      File var2 = new File(this.context, var1);

      for (int var3 = 2; var2.exists(); var3++) {
         var2 = new File(this.context, var1 + "-" + var3);
      }

      return var2;
   }

   private static String compute(String var0) {
      int var1 = var0.lastIndexOf(46);
      return var1 > 0 ? var0.substring(0, var1) : var0;
   }

   private static String resolve(String var0) {
      return var0.trim().replaceAll("[^a-zA-Z0-9._ -]", "_");
   }

   private void select() {
      this.state.clear();
      this.cache.clear();
      this.output.clear();
      this.active = "";
      this.mode = false;
      File var1 = new File(this.context, "index.json");
      if (var1.isFile()) {
         try {
            JSONObject var2 = new JSONObject(new String(Files.readAllBytes(var1.toPath()), StandardCharsets.UTF_8));
            this.active = var2.optString("selected", "");
            this.mode = var2.optBoolean("equipped", !this.active.isEmpty()) && !this.active.isEmpty();
            JSONObject var3 = var2.optJSONObject("tabs");
            if (var3 != null) {
               for (String var5 : var3.keySet()) {
                  this.state.put(var5, AssetCategory.handle(var3.optString(var5, "models")));
               }
            }

            JSONObject var9 = var2.optJSONObject("names");
            if (var9 != null) {
               for (String var6 : var9.keySet()) {
                  this.cache.put(var6, var9.optString(var6, ""));
               }
            }

            JSONObject var11 = var2.optJSONObject("prefixes");
            if (var11 != null) {
               for (String var7 : var11.keySet()) {
                  this.output.put(var7, var11.optString(var7, ""));
               }
            }
         } catch (Exception var8) {
         }
      }
   }

   private void refresh() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("selected", this.active);
         var1.put("equipped", this.mode);
         JSONObject var2 = new JSONObject();

         for (Entry var4 : this.state.entrySet()) {
            var2.put((String)var4.getKey(), ((AssetCategory)var4.getValue()).handle());
         }

         var1.put("tabs", var2);
         JSONObject var8 = new JSONObject();

         for (Entry var5 : this.cache.entrySet()) {
            var8.put((String)var5.getKey(), var5.getValue());
         }

         var1.put("names", var8);
         JSONObject var10 = new JSONObject();

         for (Entry var6 : this.output.entrySet()) {
            var10.put((String)var6.getKey(), var6.getValue());
         }

         var1.put("prefixes", var10);
         File var12 = new File(this.context, "index.json");
         Files.write(var12.toPath(), var1.toString(2).getBytes(StandardCharsets.UTF_8));
      } catch (Exception var7) {
      }
   }

   private String update(String var1) {
      byte[] var2 = this.apply(var1);
      return var2 == null ? null : new String(var2, StandardCharsets.UTF_8);
   }

   private byte[] apply(String var1) {
      ClassLoader var2 = FoundryPresetLibrary.class.getClassLoader();

      try (InputStream var3 = var2.getResourceAsStream(var1)) {
         return var3 == null ? null : var3.readAllBytes();
      } catch (IOException var8) {
         return null;
      }
   }
}
