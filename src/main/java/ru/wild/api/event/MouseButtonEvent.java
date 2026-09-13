package ru.wild.api.event;

public final class MouseButtonEvent extends Event {
   private final long instance;
   private final int data;
   private final int context;
   private final int config;
   private final double state;
   private final double cache;
   private final boolean output;

   public MouseButtonEvent(long var1, int var3, int var4, int var5, double var6, double var8) {
      this(var1, var3, var4, var5, var6, var8, false);
   }

   public MouseButtonEvent(long var1, int var3, int var4, int var5, double var6, double var8, boolean var10) {
      this.instance = var1;
      this.data = var3;
      this.context = var4;
      this.config = var5;
      this.state = var6;
      this.cache = var8;
      this.output = var10;
   }

   public long compute() {
      return this.instance;
   }

   public int resolve() {
      return this.data;
   }

   public int update() {
      return this.context;
   }

   public int apply() {
      return this.config;
   }

   public double execute() {
      return this.state;
   }

   public double prepare() {
      return this.cache;
   }

   public boolean check() {
      return this.output;
   }

   public boolean onTick() {
      return this.context == 1;
   }

   public boolean select() {
      return this.context == 0;
   }
}
