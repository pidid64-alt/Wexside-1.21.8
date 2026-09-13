package ru.wild.security;

public final class BuildFingerprint {
   private static final long instance = -3750763034362895579L;
   private static final long data = 1099511628211L;
   private long context;

   public BuildFingerprint() {
      this.handle(System.nanoTime() ^ handle("wild-1.21.8-1787661348375"));
   }

   public void handle(long var1) {
      this.context = -3750763034362895579L;
      this.process(var1);
   }

   public void handle(int var1) {
      this.context ^= var1 & 255L;
      this.context *= 1099511628211L;
      this.context ^= var1 >>> 8 & 255L;
      this.context *= 1099511628211L;
      this.context ^= var1 >>> 16 & 255L;
      this.context *= 1099511628211L;
      this.context ^= var1 >>> 24 & 255L;
      this.context *= 1099511628211L;
   }

   public void process(long var1) {
      this.handle((int)var1);
      this.handle((int)(var1 >>> 32));
   }

   public void handle(float var1) {
      this.handle(Float.floatToRawIntBits(var1));
   }

   public long handle() {
      return this.context;
   }

   static long handle(String var0) {
      long var1 = -3750763034362895579L;
      if (var0 == null) {
         return var1;
      }

      for (int var3 = 0; var3 < var0.length(); var3++) {
         var1 ^= var0.charAt(var3);
         var1 *= 1099511628211L;
      }

      return var1;
   }
}
