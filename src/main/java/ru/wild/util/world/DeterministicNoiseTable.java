package ru.wild.util.world;

import java.util.Arrays;

public final class DeterministicNoiseTable {
   public static final byte instance = -1;
   private long[] data;
   private byte[] context;
   private int config;
   private int state;
   private int cache;

   public DeterministicNoiseTable() {
      this.handle(16);
   }

   private void handle(int var1) {
      this.data = new long[var1];
      this.context = new byte[var1];
      Arrays.fill(this.context, (byte)-1);
      this.config = var1 - 1;
      this.cache = var1 - (var1 >> 2) - (var1 >> 3);
      this.state = 0;
   }

   public int handle() {
      return this.state;
   }

   public int process() {
      return this.data.length;
   }

   public long[] compute() {
      return this.data;
   }

   public byte[] resolve() {
      return this.context;
   }

   private int compute(long var1) {
      long var3 = var1 * -7046029254386353131L;
      var3 ^= var3 >>> 32;
      return (int)var3 & this.config;
   }

   public byte handle(long var1) {
      int var3 = this.compute(var1);

      while (true) {
         byte var4 = this.context[var3];
         if (var4 == -1) {
            return -1;
         }

         if (this.data[var3] == var1) {
            return var4;
         }

         var3 = var3 + 1 & this.config;
      }
   }

   public boolean handle(long var1, byte var3) {
      int var4 = this.compute(var1);

      while (true) {
         byte var5 = this.context[var4];
         if (var5 == -1) {
            this.data[var4] = var1;
            this.context[var4] = var3;
            this.state++;
            if (this.state >= this.cache) {
               this.apply();
            }

            return true;
         }

         if (this.data[var4] == var1) {
            if (var5 == var3) {
               return false;
            }

            this.context[var4] = var3;
            return true;
         }

         var4 = var4 + 1 & this.config;
      }
   }

   public boolean process(long var1) {
      int var3 = this.compute(var1);

      while (true) {
         int var4 = this.context[var3];
         if (var4 == -1) {
            return false;
         }

         if (this.data[var3] == var1) {
            this.context[var3] = -1;
            this.state--;
            var4 = var3;
            int var5 = var3;

            while (true) {
               var5 = var5 + 1 & this.config;
               byte var6 = this.context[var5];
               if (var6 == -1) {
                  return true;
               }

               int var7 = this.compute(this.data[var5]);
               if ((var5 - var7 & this.config) >= (var5 - var4 & this.config)) {
                  this.data[var4] = this.data[var5];
                  this.context[var4] = var6;
                  this.context[var5] = -1;
                  var4 = var5;
               }
            }
         }

         var3 = var3 + 1 & this.config;
      }
   }

   public void update() {
      if (this.state != 0) {
         Arrays.fill(this.context, (byte)-1);
         this.state = 0;
      }
   }

   private void apply() {
      long[] var1 = this.data;
      byte[] var2 = this.context;
      this.handle(var1.length << 1);

      for (int var3 = 0; var3 < var2.length; var3++) {
         byte var4 = var2[var3];
         if (var4 != -1) {
            this.process(var1[var3], var4);
         }
      }
   }

   private void process(long var1, byte var3) {
      int var4 = this.compute(var1);

      while (this.context[var4] != -1) {
         var4 = var4 + 1 & this.config;
      }

      this.data[var4] = var1;
      this.context[var4] = var3;
      this.state++;
   }
}
