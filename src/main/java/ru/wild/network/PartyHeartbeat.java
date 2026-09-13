package ru.wild.network;

public final class PartyHeartbeat {
   private long instance;
   private long data = Long.MAX_VALUE;
   private long context;
   private long config;
   private boolean state;

   public boolean handle() {
      return this.state;
   }

   public long process() {
      return System.currentTimeMillis() + this.instance;
   }

   public long compute() {
      return this.data == Long.MAX_VALUE ? 0L : this.data;
   }

   boolean handle(long var1) {
      return var1 >= this.context;
   }

   void process(long var1) {
      this.context = var1 + 10000L;
   }

   void handle(long var1, long var3, long var5) {
      long var7 = var5 - var1;
      if (var7 >= 0L && var7 <= 5000L) {
         if (var5 - this.config > 300000L) {
            this.config = var5;
            this.data = Long.MAX_VALUE;
         }

         if (var7 <= this.data) {
            this.data = var7;
            this.instance = var3 + var7 / 2L - var5;
            this.state = true;
         }
      }
   }

   void resolve() {
      this.instance = 0L;
      this.data = Long.MAX_VALUE;
      this.context = 0L;
      this.config = 0L;
      this.state = false;
   }
}
