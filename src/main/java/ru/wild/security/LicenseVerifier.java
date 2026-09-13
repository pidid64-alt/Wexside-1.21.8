package ru.wild.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class LicenseVerifier {
   private static final long instance = Long.getLong("wild.license.cacheTtlMs", 30000L) * 1000000L;
   private static volatile LicenseVerifier.DataRecord data;

   private LicenseVerifier() {
   }

   public static boolean handle() {
      long var0 = System.nanoTime();
      long var2 = TrustedTimeProvider.compute();
      LicenseVerifier.DataRecord var4 = data;
      if (var4 == null || var0 - var4.checkedAtNano > instance || var4.valid && var2 >= var4.validUntil) {
         LicenseVerifier.DataRecord var5 = handle(var0, var2);
         data = var5;
         return var5.valid;
      } else {
         return var4.valid;
      }
   }

   public static boolean process() {
      long var0 = System.nanoTime();
      long var2 = TrustedTimeProvider.compute();
      LicenseVerifier.DataRecord var4 = data;
      if (var4 != null && var0 - var4.checkedAtNano <= instance) {
         return var4.fileMissing;
      }

      LicenseVerifier.DataRecord var5 = handle(var0, var2);
      data = var5;
      return var5.fileMissing;
   }

   private static LicenseVerifier.DataRecord handle(long var0, long var2) {
      long var4 = 0L;

      Path var6;
      try {
         var6 = compute();
      } catch (Throwable var15) {
         return new LicenseVerifier.DataRecord(false, var4, var0, true);
      }

      if (var6 != null && Files.exists(var6)) {
         try {
            JsonObject var7 = handle(var6);
            String var8 = var7.get("payload").getAsString();
            String var9 = var7.get("signature").getAsString();
            byte[] var10 = Base64.getUrlDecoder().decode(var8);
            byte[] var11 = Base64.getUrlDecoder().decode(var9);
            if (!handle(var10, var11, resolve())) {
               return new LicenseVerifier.DataRecord(false, var4, var0, false);
            } else {
               JsonObject var12 = JsonParser.parseString(new String(var10, StandardCharsets.UTF_8)).getAsJsonObject();
               var4 = var12.has("validUntil") ? var12.get("validUntil").getAsLong() : 0L;
               if (var4 <= var2) {
                  return new LicenseVerifier.DataRecord(false, var4, var0, false);
               } else {
                  String var13 = var12.has("hwidHash") ? var12.get("hwidHash").getAsString() : "";
                  if (var13.isBlank()) {
                     return new LicenseVerifier.DataRecord(false, var4, var0, false);
                  } else {
                     return !HardwareFingerprint.handle(var13)
                        ? new LicenseVerifier.DataRecord(false, var4, var0, false)
                        : new LicenseVerifier.DataRecord(true, var4, var0, false);
                  }
               }
            }
         } catch (Throwable var14) {
            return new LicenseVerifier.DataRecord(false, var4, var0, false);
         }
      } else {
         return new LicenseVerifier.DataRecord(false, var4, var0, true);
      }
   }

   private static JsonObject handle(Path var0) throws Exception {
      String var1 = Files.readString(var0, StandardCharsets.UTF_8);
      return JsonParser.parseString(var1).getAsJsonObject();
   }

   private static Path compute() {
      String var0 = System.getProperty("wild.license.path");
      if (var0 != null && !var0.isBlank()) {
         return Path.of(var0);
      }

      String var1 = System.getenv("WILD_LICENSE_PATH");
      if (var1 != null && !var1.isBlank()) {
         return Path.of(var1);
      }

      String var2 = System.getenv("APPDATA");
      if (var2 != null && !var2.isBlank()) {
         Path var3 = Path.of(var2, "WildClient", "license.json");
         if (Files.exists(var3)) {
            return var3;
         }
      }

      return Path.of(System.getProperty("user.home", "."), ".wildclient", "license.json");
   }

   private static PublicKey resolve() throws Exception {
      String var0 = "-----BEGIN PUBLIC KEY-----\nMCowBQYDK2VwAyEAgqu9hOrz4JQKl2izQlnpj+d8jkT988LVfYfXPvKyt2Y=\n-----END PUBLIC KEY-----\n"
         .replace("-----BEGIN PUBLIC KEY-----", "")
         .replace("-----END PUBLIC KEY-----", "")
         .replaceAll("\\s+", "");
      byte[] var1 = Base64.getDecoder().decode(var0);
      return KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(var1));
   }

   private static boolean handle(byte[] var0, byte[] var1, PublicKey var2) throws Exception {
      Signature var3 = Signature.getInstance("Ed25519");
      var3.initVerify(var2);
      var3.update(var0);
      return var3.verify(var1);
   }

   record DataRecord(boolean valid, long validUntil, long checkedAtNano, boolean fileMissing) {
   }
}
