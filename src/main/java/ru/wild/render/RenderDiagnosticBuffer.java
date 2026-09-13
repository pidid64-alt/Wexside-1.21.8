package ru.wild.render;

import java.io.DataOutputStream;
import java.io.IOException;

public final class RenderDiagnosticBuffer {
   private static final int instance = 32;
   private final long[] data = new long[32];
   private final int[] context = new int[32];
   private final int[] config = new int[32];
   private final int[] state = new int[32];
   private final long[] cache = new long[32];
   private int output;
   private int current;
   private int active;

   void handle(long var1, int var3, int var4, int var5, long var6) {
      int var8 = this.output;
      this.data[var8] = var1;
      this.context[var8] = var3;
      this.config[var8] = var4;
      this.state[var8] = var5;
      this.cache[var8] = var6;
      this.output = var8 + 1 & 31;
      if (this.current < 32) {
         this.current++;
      }

      this.active++;
   }

   int handle() {
      return this.active;
   }

   int process() {
      return this.current;
   }

   int compute() {
      if (this.current <= 0) {
         return 0;
      }

      int var1 = this.output - 1 & 31;
      return this.context[var1];
   }

   int resolve() {
      if (this.current <= 0) {
         return 0;
      }

      int var1 = this.output - 1 & 31;
      return this.config[var1];
   }

   void handle(DataOutputStream var1) throws IOException {
      var1.writeInt(this.current);
      int var2 = this.output - this.current & 31;

      for (int var3 = 0; var3 < this.current; var3++) {
         int var4 = var2 + var3 & 31;
         var1.writeLong(this.data[var4]);
         var1.writeInt(this.context[var4]);
         var1.writeInt(this.config[var4]);
         var1.writeInt(this.state[var4]);
         var1.writeLong(this.cache[var4]);
      }
   }
}
