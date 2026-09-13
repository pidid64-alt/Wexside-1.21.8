package ru.wild.security;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.NamedParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class WildSnapshotCrypto {
   private static final byte[] instance = new byte[]{87, 83, 78, 49};
   private static final byte[] data = "WildSnap-v1".getBytes(StandardCharsets.UTF_8);
   private static final byte[] context = "wildsnap/aes-gcm".getBytes(StandardCharsets.UTF_8);
   private static final String config = "MCowBQYDK2VuAyEAKimzdBToBe4IjoYMuCYjJrr36rpeC+pSXoyJ9NSdR38=";
   private static final SecureRandom state = new SecureRandom();

   private WildSnapshotCrypto() {
   }

   public static byte[] handle(byte[] var0) throws Exception {
      KeyFactory var1 = KeyFactory.getInstance("X25519");
      PublicKey var2 = var1.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode("MCowBQYDK2VuAyEAKimzdBToBe4IjoYMuCYjJrr36rpeC+pSXoyJ9NSdR38=")));
      return handle(var0, var2);
   }

   static byte[] handle(byte[] var0, PublicKey var1) throws Exception {
      KeyPairGenerator var2 = KeyPairGenerator.getInstance("X25519");
      var2.initialize(NamedParameterSpec.X25519);
      KeyPair var3 = var2.generateKeyPair();
      KeyAgreement var4 = KeyAgreement.getInstance("X25519");
      var4.init(var3.getPrivate());
      var4.doPhase(var1, true);
      byte[] var5 = var4.generateSecret();
      byte[] var6 = process(var5, var3.getPublic().getEncoded());
      byte[] var7 = new byte[12];
      state.nextBytes(var7);
      Cipher var8 = Cipher.getInstance("AES/GCM/NoPadding");
      var8.init(1, new SecretKeySpec(var6, "AES"), new GCMParameterSpec(128, var7));
      byte[] var9 = var8.doFinal(var0);
      ByteArrayOutputStream var10 = new ByteArrayOutputStream(var9.length + 128);
      var10.writeBytes(instance);
      handle(var10, var3.getPublic().getEncoded().length);
      var10.writeBytes(var3.getPublic().getEncoded());
      var10.write(var7.length);
      var10.writeBytes(var7);
      process(var10, var9.length);
      var10.writeBytes(var9);
      Arrays.fill(var5, (byte)0);
      Arrays.fill(var6, (byte)0);
      return var10.toByteArray();
   }

   public static byte[] handle(byte[] var0, byte[] var1) throws Exception {
      ByteBuffer var2 = ByteBuffer.wrap(var0);

      for (byte var6 : instance) {
         if (var2.get() != var6) {
            throw new IllegalArgumentException("bad wildsnap magic");
         }
      }

      int var17 = Short.toUnsignedInt(var2.getShort());
      byte[] var18 = new byte[var17];
      var2.get(var18);
      int var19 = Byte.toUnsignedInt(var2.get());
      byte[] var20 = new byte[var19];
      var2.get(var20);
      int var7 = var2.getInt();
      byte[] var8 = new byte[var7];
      var2.get(var8);
      KeyFactory var9 = KeyFactory.getInstance("X25519");
      PublicKey var10 = var9.generatePublic(new X509EncodedKeySpec(var18));
      PrivateKey var11 = var9.generatePrivate(new PKCS8EncodedKeySpec(var1));
      KeyAgreement var12 = KeyAgreement.getInstance("X25519");
      var12.init(var11);
      var12.doPhase(var10, true);
      byte[] var13 = var12.generateSecret();
      byte[] var14 = process(var13, var18);
      Cipher var15 = Cipher.getInstance("AES/GCM/NoPadding");
      var15.init(2, new SecretKeySpec(var14, "AES"), new GCMParameterSpec(128, var20));
      byte[] var16 = var15.doFinal(var8);
      Arrays.fill(var13, (byte)0);
      Arrays.fill(var14, (byte)0);
      return var16;
   }

   private static byte[] process(byte[] var0, byte[] var1) throws Exception {
      Mac var2 = Mac.getInstance("HmacSHA256");
      var2.init(new SecretKeySpec(data, "HmacSHA256"));
      byte[] var3 = var2.doFinal(var0);
      var2.init(new SecretKeySpec(var3, "HmacSHA256"));
      var2.update(context);
      var2.update(var1);
      var2.update((byte)1);
      byte[] var4 = var2.doFinal();
      Arrays.fill(var3, (byte)0);
      return Arrays.copyOf(var4, 32);
   }

   private static void handle(ByteArrayOutputStream var0, int var1) {
      var0.write(var1 >>> 8 & 0xFF);
      var0.write(var1 & 0xFF);
   }

   private static void process(ByteArrayOutputStream var0, int var1) {
      var0.write(var1 >>> 24 & 0xFF);
      var0.write(var1 >>> 16 & 0xFF);
      var0.write(var1 >>> 8 & 0xFF);
      var0.write(var1 & 0xFF);
   }
}
