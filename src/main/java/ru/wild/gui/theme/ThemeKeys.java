package ru.wild.gui.theme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.wild.render.shader.ShaderGraph;
import ru.wild.render.shader.ShaderGraphBlock;
import ru.wild.render.shader.ShaderGraphLink;
import ru.wild.render.shader.ShaderNodeRegistry;

public final class ThemeKeys {
   public static final String instance = "WildTheme::";

   private ThemeKeys() {
   }

   public static String handle(ShaderGraph var0) {
      String var1 = process(var0).toString();
      byte[] var2 = handle(var1.getBytes(StandardCharsets.UTF_8));
      String var3 = Base64.getUrlEncoder().withoutPadding().encodeToString(var2);
      return "WildTheme::" + handle(var1) + "::" + var3;
   }

   public static ShaderGraph handle(String var0, ShaderNodeRegistry var1) {
      if (var0 != null && var0.startsWith("WildTheme::")) {
         String var2 = var0.substring("WildTheme::".length());
         int var3 = var2.indexOf("::");
         String var4 = var3 >= 0 ? var2.substring(var3 + 2) : var2;
         byte[] var5 = Base64.getUrlDecoder().decode(var4);
         String var6 = new String(process(var5), StandardCharsets.UTF_8);
         return handle(new JSONObject(var6), var1);
      } else {
         throw new IllegalArgumentException("Invalid WildTheme payload");
      }
   }

   public static JSONObject process(ShaderGraph var0) {
      JSONObject var1 = new JSONObject();
      var1.put("version", 3);
      var1.put("target", var0.process());
      var1.put("metadata", handle(var0.handle()));
      JSONArray var2 = new JSONArray();

      for (ShaderGraphBlock var4 : var0.compute()) {
         JSONObject var5 = new JSONObject();
         var5.put("id", var4.handle());
         var5.put("kind", var4.process());
         var5.put("x", var4.compute());
         var5.put("y", var4.resolve());
         JSONObject var6 = new JSONObject();

         for (Entry var8 : var4.apply().entrySet()) {
            var6.put((String)var8.getKey(), var8.getValue());
         }

         var5.put("values", var6);
         JSONObject var14 = new JSONObject();

         for (Entry var9 : var4.execute().entrySet()) {
            var14.put((String)var9.getKey(), var9.getValue());
         }

         var5.put("textValues", var14);
         var2.put(var5);
      }

      JSONArray var10 = new JSONArray();

      for (ShaderGraphLink var12 : var0.resolve()) {
         JSONObject var13 = new JSONObject();
         var13.put("fromNode", var12.handle());
         var13.put("fromPin", var12.process());
         var13.put("toNode", var12.compute());
         var13.put("toPin", var12.resolve());
         var10.put(var13);
      }

      var1.put("nodes", var2);
      var1.put("connections", var10);
      return var1;
   }

   public static ShaderGraph handle(JSONObject var0, ShaderNodeRegistry var1) {
      ShaderGraph var2 = new ShaderGraph();
      String var3 = var0.optString("target", "");
      if (!var3.isBlank()) {
         var2.handle(var3);
      }

      var2.handle(handle(var0.optJSONObject("metadata"), var0));
      JSONArray var4 = var0.optJSONArray("nodes");
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length(); var5++) {
            JSONObject var6 = var4.getJSONObject(var5);
            ShaderGraphBlock var7 = new ShaderGraphBlock(
               var6.getString("id"), var6.getString("kind"), (float)var6.optDouble("x", 0.0), (float)var6.optDouble("y", 0.0)
            );
            JSONObject var8 = var6.optJSONObject("values");
            if (var8 != null) {
               for (String var10 : var8.keySet()) {
                  var7.process(var10, (float)var8.optDouble(var10, 0.0));
               }
            }

            JSONObject var15 = var6.optJSONObject("textValues");
            if (var15 != null) {
               for (String var11 : var15.keySet()) {
                  var7.process(var11, var15.optString(var11, ""));
               }
            }

            var2.handle(var7, var1);
         }
      }

      JSONArray var12 = var0.optJSONArray("connections");
      if (var12 != null) {
         for (int var13 = 0; var13 < var12.length(); var13++) {
            JSONObject var14 = var12.getJSONObject(var13);
            var2.handle(var14.getString("fromNode"), var14.getString("fromPin"), var14.getString("toNode"), var14.getString("toPin"), var1);
         }
      }

      return var2;
   }

   public static JSONObject handle(ThemeSourceMetadata var0) {
      ThemeSourceMetadata var1 = var0 == null ? new ThemeSourceMetadata() : var0;
      JSONObject var2 = new JSONObject();
      var2.put("name", var1.process());
      var2.put("author", var1.compute());
      var2.put("description", var1.resolve());
      var2.put("complexity", var1.update());
      var2.put("source", var1.apply());
      var2.put("shapeSource", var1.execute());
      var2.put("createdAt", var1.check());
      var2.put("updatedAt", var1.onTick());
      var2.put("favorite", var1.select());
      var2.put("previewThumbnail", var1.prepare());
      return var2;
   }

   public static ThemeSourceMetadata handle(JSONObject var0, JSONObject var1) {
      ThemeSourceMetadata var2 = new ThemeSourceMetadata();
      JSONObject var3 = var0 == null ? new JSONObject() : var0;
      JSONObject var4 = var1 == null ? new JSONObject() : var1;
      var2.handle(var3.optString("name", var4.optString("displayName", "")));
      var2.process(var3.optString("author", var4.optString("author", "")));
      var2.compute(var3.optString("description", var4.optString("description", "")));
      var2.resolve(var3.optString("complexity", var4.optString("complexity", "Custom")));
      var2.update(var3.optString("source", var4.optString("source", "local")));
      var2.apply(var3.optString("shapeSource", var4.optString("shapeSource", "Host Rectangle")));
      var2.handle(var3.optLong("createdAt", var4.optLong("createdAt", 0L)));
      var2.process(var3.optLong("updatedAt", var4.optLong("updatedAt", 0L)));
      var2.handle(var3.optBoolean("favorite", var4.optBoolean("favorite", false)));
      var2.execute(var3.optString("previewThumbnail", var4.optString("previewThumbnail", "")));
      return var2;
   }

   private static byte[] handle(byte[] var0) {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         try (GZIPOutputStream var2 = new GZIPOutputStream(var1)) {
            var2.write(var0);
         }

         return var1.toByteArray();
      } catch (Exception var7) {
         throw new IllegalStateException("GZIP export failed", var7);
      }
   }

   private static byte[] process(byte[] var0) {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         try (GZIPInputStream var2 = new GZIPInputStream(new ByteArrayInputStream(var0))) {
            var2.transferTo(var1);
         }

         return var1.toByteArray();
      } catch (Exception var7) {
         throw new IllegalArgumentException("GZIP import failed", var7);
      }
   }

   private static String handle(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         byte[] var2 = var1.digest(var0.getBytes(StandardCharsets.UTF_8));
         StringBuilder var3 = new StringBuilder(16);

         for (int var4 = 0; var4 < 8; var4++) {
            var3.append(String.format("%02x", var2[var4] & 255));
         }

         return var3.toString();
      } catch (Exception var5) {
         return "0000000000000000";
      }
   }
}
