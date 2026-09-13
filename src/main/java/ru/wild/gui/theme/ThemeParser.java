package ru.wild.gui.theme;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.wild.render.texture.AssetCategory;
import ru.wild.render.texture.ModelFaceLayout;
import ru.wild.render.texture.TextureHolder;

public final class ThemeParser {
   private final String instance;
   private final File data;
   private final File context;
   private final File config;
   private final String state;
   private String cache;
   private final String output;
   private final String current;
   private AssetCategory active;
   private String mode = "";
   private TextureHolder selection;
   private boolean enabled;
   private String renderer;

   public ThemeParser(String var1, File var2, File var3, File var4, String var5, String var6, String var7, AssetCategory var8) {
      this.instance = var1;
      this.data = var2;
      this.context = var3;
      this.config = var4;
      this.state = var5;
      this.cache = var5;
      this.output = var6;
      this.current = var7;
      this.active = var8 == null ? AssetCategory.MODELS : var8;
   }

   public static ThemeParser handle(String var0, File var1, AssetCategory var2) {
      if (var1 != null && var1.isDirectory()) {
         File var3 = handle(var1);
         if (var3 == null) {
            return null;
         }

         File var4 = new File(var1, "avatar.png");
         if (!var4.isFile()) {
            var4 = null;
         }

         String var5 = compute(var1.getName());
         String var6 = "";
         String var7 = "";
         File var8 = new File(var1, "avatar.json");
         if (var8.isFile()) {
            try {
               JSONObject var9 = new JSONObject(new String(Files.readAllBytes(var8.toPath()), StandardCharsets.UTF_8));
               var5 = compute(var9.optString("name", var5));
               var7 = var9.optString("color", "");
               JSONArray var10 = var9.optJSONArray("authors");
               if (var10 != null && var10.length() > 0) {
                  StringBuilder var11 = new StringBuilder();

                  for (int var12 = 0; var12 < var10.length(); var12++) {
                     if (var12 > 0) {
                        var11.append(", ");
                     }

                     var11.append(compute(var10.optString(var12, "")));
                  }

                  var6 = var11.toString();
               } else {
                  var6 = compute(var9.optString("author", ""));
               }
            } catch (Exception var13) {
            }
         }

         return new ThemeParser(var0, var1, var3, var4, var5.isEmpty() ? var1.getName() : var5, var6, var7, var2);
      } else {
         return null;
      }
   }

   private static File handle(File var0) {
      File[] var1 = var0.listFiles();
      if (var1 == null) {
         return null;
      }

      File var2 = null;
      File var3 = null;
      long var4 = -1L;

      for (File var9 : var1) {
         if (var9.isFile()) {
            String var10 = var9.getName().toLowerCase();
            if (var10.endsWith(".bbmodel") && !var10.contains("hud")) {
               if (var10.equals("model.bbmodel")) {
                  var2 = var9;
               }

               if (var9.length() > var4) {
                  var4 = var9.length();
                  var3 = var9;
               }
            }
         }
      }

      if (var2 != null) {
         return var2;
      } else {
         return var3 != null ? var3 : handle(var0, ".bbmodel");
      }
   }

   private static String compute(String var0) {
      return var0 == null ? "" : var0.replaceAll("§.", "").replaceAll("&[0-9A-Fa-fK-Ok-or]", "").trim();
   }

   private static File handle(File var0, String var1) {
      File[] var2 = var0.listFiles();
      if (var2 == null) {
         return null;
      }

      for (File var6 : var2) {
         if (var6.isFile() && var6.getName().toLowerCase().endsWith(var1)) {
            return var6;
         }
      }

      return null;
   }

   public String handle() {
      return this.instance;
   }

   public File process() {
      return this.data;
   }

   public File compute() {
      return this.config;
   }

   public String resolve() {
      return this.cache;
   }

   public String update() {
      return this.state;
   }

   void handle(String var1) {
      this.cache = var1 != null && !var1.trim().isEmpty() ? var1.trim() : this.state;
   }

   public String apply() {
      return this.output;
   }

   public String execute() {
      return this.current;
   }

   public AssetCategory prepare() {
      return this.active;
   }

   void handle(AssetCategory var1) {
      this.active = var1 == null ? AssetCategory.MODELS : var1;
   }

   public String check() {
      return this.mode;
   }

   void process(String var1) {
      this.mode = var1 == null ? "" : var1.trim();
   }

   public boolean onTick() {
      return this.render() != null;
   }

   public AssetCategory select() {
      TextureHolder var1 = this.render();
      if (var1 == null) {
         return AssetCategory.MODELS;
      }

      HashSet var2 = new HashSet();

      for (TextureHolder.TextureState var4 : var1.resolve()) {
         handle(var4, var2);
      }

      boolean var7 = var2.contains("body") || var2.contains("torso");
      boolean var8 = var2.contains("leftleg") || var2.contains("rightleg") || var2.contains("left_leg") || var2.contains("right_leg");
      boolean var5 = var2.contains("leftarm") || var2.contains("rightarm") || var2.contains("left_arm") || var2.contains("right_arm");
      boolean var6 = var2.contains("head");
      if (!var7 || !var8 && !var5) {
         if (var6 && !var7 && !var8) {
            return AssetCategory.ITEMS;
         } else {
            return !var7 && !var8 && !var5 && !var6 ? AssetCategory.PETS : AssetCategory.MODELS;
         }
      } else {
         return AssetCategory.MODELS;
      }
   }

   private static void handle(TextureHolder.TextureState var0, Set<String> var1) {
      if (var0.handle() != null) {
         var1.add(var0.handle().toLowerCase());
      }

      for (TextureHolder.TextureState var3 : var0.check()) {
         handle(var3, var1);
      }
   }

   public String refresh() {
      this.render();
      return this.renderer;
   }

   public TextureHolder render() {
      if (this.enabled) {
         return this.selection;
      }

      this.enabled = true;

      try {
         String var1 = new String(Files.readAllBytes(this.context.toPath()), StandardCharsets.UTF_8);
         this.selection = ModelFaceLayout.handle(var1);
         if (this.selection.resolve().isEmpty()) {
            this.renderer = "Пустая модель";
         }
      } catch (Throwable var2) {
         this.renderer = var2.getClass().getSimpleName() + ": " + var2.getMessage();
         this.selection = null;
      }

      return this.selection;
   }
}
