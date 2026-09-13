package ru.wild.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class AltVaultStore {
   private static final Gson instance = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
   private static final String data = "wild-alt-vault";
   private static final int context = 3;
   private static final int config = 180000;
   private static final int state = 128;
   private static final SecureRandom cache = new SecureRandom();

   private AltVaultStore() {
   }

   public static List<AltVaultStore.NamedEntry> handle(File var0) {
      List var1 = resolve(var0);
      if (var1 != null) {
         return handle(var1);
      }

      List var2 = resolve(execute(var0));
      return var2 != null ? handle(var2) : List.of();
   }

   static void handle(File var0, List<AltVaultStore.NamedEntry> var1) {
      handle(var0, var1, process(var0));
   }

   public static void handle(File var0, List<AltVaultStore.NamedEntry> var1, String var2) {
      try {
         File var3 = var0.getParentFile();
         if (var3 != null) {
            var3.mkdirs();
         }

         List<AltVaultStore.NamedEntry> var4 = handle(var1);
         String var5 = handle(var4, var2);
         byte[] var6 = handle(16);
         byte[] var7 = handle(12);
         JsonObject var8 = new JsonObject();
         var8.addProperty("version", 3);
         var8.addProperty("savedAt", System.currentTimeMillis());
         if (!var5.isEmpty()) {
            var8.addProperty("lastSelectedId", var5);
         }

         JsonArray var9 = new JsonArray();

         for (AltVaultStore.NamedEntry var11 : var4) {
            JsonObject var12 = new JsonObject();
            var12.addProperty("id", var11.id());
            var12.addProperty("name", var11.name());
            var12.addProperty("type", var11.type());
            var12.addProperty("password", var11.password());
            var12.addProperty("createdAt", var11.createdAt());
            var12.addProperty("lastUsedAt", var11.lastUsedAt());
            var9.add(var12);
         }

         var8.add("accounts", var9);
         byte[] var14 = handle(var0, instance.toJson(var8).getBytes(StandardCharsets.UTF_8), var6, var7);
         JsonObject var15 = new JsonObject();
         var15.addProperty("format", "wild-alt-vault");
         var15.addProperty("version", 3);
         var15.addProperty("cipher", "AES/GCM/NoPadding");
         var15.addProperty("kdf", "PBKDF2WithHmacSHA256");
         var15.addProperty("iterations", 180000);
         var15.addProperty("salt", Base64.getEncoder().encodeToString(var6));
         var15.addProperty("iv", Base64.getEncoder().encodeToString(var7));
         var15.addProperty("payload", Base64.getEncoder().encodeToString(var14));
         if (!var5.isEmpty()) {
            var15.addProperty("lastSelectedId", var5);
         }

         handle(var0, instance.toJson(var15).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var13) {
      }
   }

   public static String process(File var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = update(var0);
      return !var1.isEmpty() ? var1 : update(execute(var0));
   }

   static boolean compute(File var0) {
      if (var0 != null && var0.exists() && var0.isFile()) {
         try (FileReader var1 = new FileReader(var0, StandardCharsets.UTF_8)) {
            JsonElement var2 = JsonParser.parseReader(var1);
            if (var2 == null || !var2.isJsonObject()) {
               return false;
            }

            JsonObject var3 = var2.getAsJsonObject();
            return "wild-alt-vault".equals(handle(var3, "format", "")) && handle(var3, "version", 0) >= 3;
         } catch (Throwable var7) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static List<AltVaultStore.NamedEntry> resolve(File var0) {
      if (var0 != null && var0.exists() && var0.isFile()) {
         try (FileReader var1 = new FileReader(var0, StandardCharsets.UTF_8)) {
            JsonElement var2 = JsonParser.parseReader(var1);
            if (var2 == null || var2.isJsonNull()) {
               return null;
            }

            if (var2.isJsonArray()) {
               return handle(var2.getAsJsonArray());
            }

            if (!var2.isJsonObject()) {
               return null;
            }

            JsonObject var3 = var2.getAsJsonObject();
            if ("wild-alt-vault".equals(handle(var3, "format", ""))) {
               return handle(var0, var3);
            }

            if (var3.has("accounts") && var3.get("accounts").isJsonArray()) {
               return handle(var3.getAsJsonArray("accounts"));
            }
         } catch (Throwable var7) {
         }

         return null;
      } else {
         return null;
      }
   }

   private static String update(File var0) {
      if (var0 != null && var0.exists() && var0.isFile()) {
         try (FileReader var1 = new FileReader(var0, StandardCharsets.UTF_8)) {
            JsonElement var2 = JsonParser.parseReader(var1);
            if (var2 == null || !var2.isJsonObject()) {
               return "";
            } else {
               JsonObject var3 = var2.getAsJsonObject();
               String var4 = handle(var3, "lastSelectedId", "");
               if (!var4.isBlank()) {
                  return var4.trim();
               } else if ("wild-alt-vault".equals(handle(var3, "format", ""))) {
                  JsonObject var11 = process(var0, var3);
                  return var11 == null ? "" : handle(var11, "lastSelectedId", "").trim();
               } else {
                  return handle(var3, "lastSelectedId", "").trim();
               }
            }
         } catch (Throwable var9) {
            return "";
         }
      } else {
         return "";
      }
   }

   private static List<AltVaultStore.NamedEntry> handle(File var0, JsonObject var1) throws Exception {
      JsonObject var2 = process(var0, var1);
      if (var2 == null) {
         return null;
      } else {
         return var2.has("accounts") && var2.get("accounts").isJsonArray() ? handle(var2.getAsJsonArray("accounts")) : List.of();
      }
   }

   private static JsonObject process(File var0, JsonObject var1) throws Exception {
      int var2 = Math.max(60000, handle(var1, "iterations", 180000));
      byte[] var3 = Base64.getDecoder().decode(handle(var1, "salt", ""));
      byte[] var4 = Base64.getDecoder().decode(handle(var1, "iv", ""));
      byte[] var5 = Base64.getDecoder().decode(handle(var1, "payload", ""));
      if (var3.length >= 12 && var4.length == 12 && var5.length >= 24) {
         byte[] var6 = handle(var0, var5, var3, var4, var2);
         JsonElement var7 = JsonParser.parseString(new String(var6, StandardCharsets.UTF_8));
         return !var7.isJsonObject() ? null : var7.getAsJsonObject();
      } else {
         return null;
      }
   }

   private static List<AltVaultStore.NamedEntry> handle(JsonArray var0) {
      ArrayList var1 = new ArrayList();

      for (JsonElement var3 : var0) {
         if (var3.isJsonObject()) {
            JsonObject var4 = var3.getAsJsonObject();
            String var5 = handle(var4, "name", "");
            String var6 = handle(var4, "type", "CRACKED");
            String var7 = handle(var4, "password", "");
            String var8 = handle(var4, "id", "");
            long var9 = handle(var4, "createdAt", 0L);
            long var11 = handle(var4, "lastUsedAt", 0L);
            var1.add(new AltVaultStore.NamedEntry(var8, var5, var6, var7, var9, var11));
         }
      }

      return var1;
   }

   private static List<AltVaultStore.NamedEntry> handle(List<AltVaultStore.NamedEntry> var0) {
      long var1 = System.currentTimeMillis();
      LinkedHashMap var3 = new LinkedHashMap();

      for (AltVaultStore.NamedEntry var5 : var0) {
         if (var3.size() >= 128) {
            break;
         }

         String var6 = handle(var5.name());
         if (!var6.isBlank()) {
            String var7 = process(var5.type());
            String var8 = var6 + var7.toLowerCase(Locale.ROOT);
            if (!var3.containsKey(var8)) {
               String var9 = compute(var5.password());
               String var10 = handle(var5.id(), var6, var7);
               long var11 = var5.createdAt() > 0L ? var5.createdAt() : var1;
               long var13 = Math.max(0L, Math.min(var5.lastUsedAt(), var1 + 86400000L));
               var3.put(var8, new AltVaultStore.NamedEntry(var10, var6, var7, var9, var11, var13));
            }
         }
      }

      return new ArrayList<>(var3.values());
   }

   private static String handle(List<AltVaultStore.NamedEntry> var0, String var1) {
      String var2 = var1 == null ? "" : var1.trim();
      if (var2.isEmpty()) {
         return "";
      }

      for (AltVaultStore.NamedEntry var4 : var0) {
         if (var2.equals(var4.id())) {
            return var2;
         }
      }

      return "";
   }

   private static byte[] handle(File var0, byte[] var1, byte[] var2, byte[] var3) throws Exception {
      Cipher var4 = Cipher.getInstance("AES/GCM/NoPadding");
      var4.init(1, handle(var0, var2, 180000), new GCMParameterSpec(128, var3));
      var4.updateAAD("wild-alt-vault".getBytes(StandardCharsets.UTF_8));
      return var4.doFinal(var1);
   }

   private static byte[] handle(File var0, byte[] var1, byte[] var2, byte[] var3, int var4) throws Exception {
      Cipher var5 = Cipher.getInstance("AES/GCM/NoPadding");
      var5.init(2, handle(var0, var2, var4), new GCMParameterSpec(128, var3));
      var5.updateAAD("wild-alt-vault".getBytes(StandardCharsets.UTF_8));
      return var5.doFinal(var1);
   }
   private static SecretKeySpec handle(File var0, byte[] var1, int var2) throws Exception {
      char[] var3 = apply(var0).toCharArray();
      boolean var9 = false /* VF: Semaphore variable */;

      SecretKeySpec var6;
      try {
         var9 = true;
         SecretKeyFactory var4 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
         PBEKeySpec var5 = new PBEKeySpec(var3, var1, var2, 256);
         var6 = new SecretKeySpec(var4.generateSecret(var5).getEncoded(), "AES");
         var9 = false;
      } finally {
         if (var9) {
            Arrays.fill(var3, '\u0000');
         }
      }

      Arrays.fill(var3, '\u0000');
      return var6;
   }

   private static String apply(File var0) {
      String var1 = var0 != null && var0.getParentFile() != null ? var0.getParentFile().getAbsolutePath() : "";
      return "wild-alt-vault\n"
         + compute(System.getProperty("user.name"))
         + "\n"
         + compute(System.getProperty("user.home"))
         + "\n"
         + compute(System.getProperty("os.name"))
         + "\n"
         + compute(System.getenv("COMPUTERNAME"))
         + "\n"
         + var1;
   }

   private static void handle(File var0, byte[] var1) throws Exception {
      Path var2 = var0.toPath();
      Path var3 = var2.getParent();
      if (var3 != null) {
         Files.createDirectories(var3);
      }

      Path var4 = var2.resolveSibling(var2.getFileName() + ".tmp");

      try (FileChannel var5 = FileChannel.open(var4, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
         ByteBuffer var6 = ByteBuffer.wrap(var1);

         while (var6.hasRemaining()) {
            var5.write(var6);
         }

         var5.force(true);
      }

      if (Files.exists(var2)) {
         Files.copy(var2, execute(var0).toPath(), StandardCopyOption.REPLACE_EXISTING);
      }

      try {
         Files.move(var4, var2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException var9) {
         Files.move(var4, var2, StandardCopyOption.REPLACE_EXISTING);
      }

      handle(var3);
   }

   private static void handle(Path var0) {
      if (var0 != null) {
         try (FileChannel var1 = FileChannel.open(var0, StandardOpenOption.READ)) {
            var1.force(true);
         } catch (Throwable var6) {
         }
      }
   }

   private static byte[] handle(int var0) {
      byte[] var1 = new byte[var0];
      cache.nextBytes(var1);
      return var1;
   }

   private static File execute(File var0) {
      return new File(var0.getParentFile(), var0.getName() + ".bak");
   }

   private static String handle(String var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.trim();
      int var2 = var1.indexOf(64);
      if (var2 > 0) {
         var1 = var1.substring(0, var2);
      }

      var1 = var1.replaceAll("[^A-Za-z0-9_]", "");
      if (var1.length() > 16) {
         var1 = var1.substring(0, 16);
      }

      return var1;
   }

   private static String process(String var0) {
      String var1 = var0 == null ? "" : var0.trim().toUpperCase(Locale.ROOT);
      return "PREMIUM".equals(var1) ? "PREMIUM" : "CRACKED";
   }

   private static String handle(String var0, String var1, String var2) {
      String var3 = var0 == null ? "" : var0.trim();
      return var3.length() >= 16 && var3.length() <= 64 && var3.matches("[A-Za-z0-9_\\-]+")
         ? var3
         : UUID.nameUUIDFromBytes(("wild-alt-vault:" + var2 + ":" + var1).getBytes(StandardCharsets.UTF_8)).toString();
   }

   private static String handle(JsonObject var0, String var1, String var2) {
      try {
         JsonElement var3 = var0.get(var1);
         return var3 != null && !var3.isJsonNull() ? var3.getAsString() : var2;
      } catch (Throwable var4) {
         return var2;
      }
   }

   private static long handle(JsonObject var0, String var1, long var2) {
      try {
         JsonElement var4 = var0.get(var1);
         return var4 != null && !var4.isJsonNull() ? var4.getAsLong() : var2;
      } catch (Throwable var5) {
         return var2;
      }
   }

   private static int handle(JsonObject var0, String var1, int var2) {
      try {
         JsonElement var3 = var0.get(var1);
         return var3 != null && !var3.isJsonNull() ? var3.getAsInt() : var2;
      } catch (Throwable var4) {
         return var2;
      }
   }

   private static String compute(String var0) {
      return var0 == null ? "" : var0;
   }

   public record NamedEntry(String id, String name, String type, String password, long createdAt, long lastUsedAt) {
   }
}
