package ru.wild.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;

public final class FingerprintEncryptor {
   public static final String instance = "server-key-1";
   static final int data = 446;
   private static final String config = "-----BEGIN PUBLIC KEY-----\nMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA0QoLnE+hVCxsFnkwpKOD\nDOho6OoakoXkVlWMTSBRFzPJJkeFdiw++SfdW0YJtQIZuekmf5eZGqZTLXKBI8Jq\ngk/pX6qGmNeARjF2V5W1isT2xTxAAS5LefbWTDTuT2vLtdL5lcG3KZa3PnAoiaA3\n2E8gyE/ME/7LEI6lZXhmVIPMLBWyOdB4O9QBks1iX8tDbwQNTG30UIjYWAq9ZoeF\nkGe0amY9snhTEnhI+NvqCT486uOhVLsSQeggDgjj5jEAwJicUxVwALnJRDRn+rfJ\n4vPUpaYik12tIIbu4jEH5KVieWtMvY4or0Q9RxlMzBhbf0s6nElcAXY2cmocl8LK\niPCHhiaKyG1wEcbTFA+YqW/f6iEzi3Me5eSb/WAhpcFLRJi0H17cgBbINr4S3+DF\nLCEXVNEr33WKncrauNvsa0nwBQIfjJBS3DfKODRg11cvT1NWJxFP1MoVJzxxXoHk\nJGLMEuKPGYbC5IdidWV5+iGfxOoUzhKppDauntDRCqqS4F9eS0DsuQ1Z04x8Z/YG\nUq+2eFbrA/k3H8SLz4me8D9XH6fQJlGcpzZnhP7/2jKOuptfxWpnukL8Ysi7xF5+\n7htZciGCZpZ1DXxYpnNjxIYGzD9aKNpXWAUeZqXtvzvBjDcl/UGdMyPWeJlCfXZg\noJwlnWSm4E/mdVe0DS8V/PkCAwEAAQ==\n-----END PUBLIC KEY-----";
   public static final boolean context = compute();
   private static volatile PublicKey state;

   private FingerprintEncryptor() {
   }

   public static FingerprintEncryptor.Snapshot handle(String var0) {
      byte[] var1 = var0.getBytes(StandardCharsets.UTF_8);
      if (var1.length > 446) {
         throw new FingerprintEncryptor.SecondaryException(var1.length, 446);
      }

      byte[] var2 = handle(var1, process());
      String var3 = Base64.getUrlEncoder().withoutPadding().encodeToString(var2);
      return new FingerprintEncryptor.Snapshot(1, "server-key-1", var3, System.currentTimeMillis() / 1000L, UUID.randomUUID().toString());
   }

   public static void handle() {
      if (context) {
         String var0 = "FAKE-SMBIOS|FAKE-DISK|FAKE-BOARD|FAKE-CPU|FAKE-DEVICE";
         System.out.println("[FingerprintCrypto] ── self-test ──────────────────────────");

         boolean var6;
         try {
            process();
            var6 = true;
         } catch (java.lang.Exception var5) {
            var6 = false;
            System.out.println("[FingerprintCrypto] RSA public key loaded: false — " + var5.getMessage());
            System.out.println("[FingerprintCrypto] ── self-test FAILED ──────────────────");
            return;
         }

         System.out.println("[FingerprintCrypto] RSA public key loaded: " + var6);
         System.out.println("[FingerprintCrypto] RSA algorithm: RSA-OAEP-SHA256");

         FingerprintEncryptor.Snapshot var2;
         try {
            var2 = handle(var0);
         } catch (java.lang.Exception var4) {
            System.out.println("[FingerprintCrypto] encrypt() FAILED — " + var4.getMessage());
            System.out.println("[FingerprintCrypto] ── self-test FAILED ──────────────────");
            return;
         }

         System.out.println("[FingerprintCrypto] encryptedPayload length: " + var2.encryptedPayload().length());
         System.out.println("[FingerprintCrypto] requestId:               " + var2.requestId());
         System.out.println("[FingerprintCrypto] timestamp:               " + var2.timestamp());
         System.out
            .printf(
               "[FingerprintCrypto] DTO preview: {\"v\":%d,\"kid\":\"%s\",\"encryptedPayload\":\"%s...\",\"timestamp\":%d,\"requestId\":\"%s\"}%n",
               var2.v(),
               var2.kid(),
               var2.encryptedPayload().substring(0, Math.min(24, var2.encryptedPayload().length())),
               var2.timestamp(),
               var2.requestId()
            );
         System.out.println("[FingerprintCrypto] ── self-test OK ─────────────────────────");
      }
   }

   static PublicKey process() {
      PublicKey var0 = state;
      if (var0 != null) {
         return var0;
      }

      synchronized (FingerprintEncryptor.class) {
         var0 = state;
         if (var0 != null) {
            return var0;
         }

         state = process(
            "-----BEGIN PUBLIC KEY-----\nMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA0QoLnE+hVCxsFnkwpKOD\nDOho6OoakoXkVlWMTSBRFzPJJkeFdiw++SfdW0YJtQIZuekmf5eZGqZTLXKBI8Jq\ngk/pX6qGmNeARjF2V5W1isT2xTxAAS5LefbWTDTuT2vLtdL5lcG3KZa3PnAoiaA3\n2E8gyE/ME/7LEI6lZXhmVIPMLBWyOdB4O9QBks1iX8tDbwQNTG30UIjYWAq9ZoeF\nkGe0amY9snhTEnhI+NvqCT486uOhVLsSQeggDgjj5jEAwJicUxVwALnJRDRn+rfJ\n4vPUpaYik12tIIbu4jEH5KVieWtMvY4or0Q9RxlMzBhbf0s6nElcAXY2cmocl8LK\niPCHhiaKyG1wEcbTFA+YqW/f6iEzi3Me5eSb/WAhpcFLRJi0H17cgBbINr4S3+DF\nLCEXVNEr33WKncrauNvsa0nwBQIfjJBS3DfKODRg11cvT1NWJxFP1MoVJzxxXoHk\nJGLMEuKPGYbC5IdidWV5+iGfxOoUzhKppDauntDRCqqS4F9eS0DsuQ1Z04x8Z/YG\nUq+2eFbrA/k3H8SLz4me8D9XH6fQJlGcpzZnhP7/2jKOuptfxWpnukL8Ysi7xF5+\n7htZciGCZpZ1DXxYpnNjxIYGzD9aKNpXWAUeZqXtvzvBjDcl/UGdMyPWeJlCfXZg\noJwlnWSm4E/mdVe0DS8V/PkCAwEAAQ==\n-----END PUBLIC KEY-----"
         );
         return state;
      }
   }

   private static PublicKey process(String var0) {
      String var1 = var0.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s+", "");

      byte[] var2;
      try {
         var2 = Base64.getDecoder().decode(var1);
      } catch (IllegalArgumentException var5) {
         throw new FingerprintEncryptor.Exception("PEM contains invalid Base64", var5);
      }

      try {
         return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(var2));
      } catch (java.lang.Exception var4) {
         throw new FingerprintEncryptor.Exception("Cannot parse RSA-4096 public key: " + var4.getMessage(), var4);
      }
   }

   private static byte[] handle(byte[] var0, PublicKey var1) {
      try {
         Cipher var2 = Cipher.getInstance("RSA/ECB/OAEPPadding");
         OAEPParameterSpec var3 = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSpecified.DEFAULT);
         var2.init(1, var1, var3);
         return var2.doFinal(var0);
      } catch (java.lang.Exception var4) {
         throw new FingerprintEncryptor.PrimaryException("RSA-OAEP-SHA256 encryption failed: " + var4.getMessage(), var4);
      }
   }

   private static boolean compute() {
      String var0 = System.getProperty("wild.crypto.selftest");
      return var0 != null ? Boolean.parseBoolean(var0.trim()) : "true".equalsIgnoreCase(System.getenv("WILD_CRYPTO_SELFTEST"));
   }

   public static final class Exception extends RuntimeException {
      public Exception(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   public static final class PrimaryException extends RuntimeException {
      public PrimaryException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   public static final class SecondaryException extends RuntimeException {
      public SecondaryException(int var1, int var2) {
         super("Payload too large for RSA-4096-OAEP-SHA256: " + var1 + " bytes (max " + var2 + ")");
      }
   }

   public record Snapshot(int v, String kid, String encryptedPayload, long timestamp, String requestId) {
   }
}
