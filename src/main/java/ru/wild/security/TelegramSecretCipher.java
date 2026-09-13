package ru.wild.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class TelegramSecretCipher {
   public static final String instance = "AES";
   public static final String data = "gUhDvBzdE4xq5f4BxkPvxv70VY44WsuH1O6s2nZ2F9U1w9y1VVG1mXQcUfbJM2DDUCd8NvtM0L4O1t1nn8FwwAVYlChNncdagiv9UR8FpLXXF8iMAtlWY4mEnYtLHPB3";

   public static String handle(String var0, String var1) throws Exception {
      Cipher var2 = Cipher.getInstance("AES");
      var2.init(1, new SecretKeySpec(handle(var1), "AES"));
      return Base64.getEncoder().encodeToString(var2.doFinal(var0.getBytes(StandardCharsets.UTF_8)));
   }

   public static String process(String var0, String var1) throws Exception {
      Cipher var2 = Cipher.getInstance("AES");
      var2.init(2, new SecretKeySpec(handle(var1), "AES"));
      return new String(var2.doFinal(Base64.getDecoder().decode(var0)), StandardCharsets.UTF_8);
   }

   public static byte[] handle(String var0) throws Exception {
      return MessageDigest.getInstance("SHA-256").digest(var0.getBytes(StandardCharsets.UTF_8));
   }

   private TelegramSecretCipher() {
   }
}
