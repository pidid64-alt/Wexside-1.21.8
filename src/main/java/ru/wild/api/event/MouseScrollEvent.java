package ru.wild.api.event;

public final class MouseScrollEvent extends Event {
   private final long instance;
   private final double data;
   private final double context;
   private final double config;
   private final double state;
   private final boolean cache;

   public MouseScrollEvent(long var1, double var3, double var5, double var7, double var9) {
      this(var1, var3, var5, var7, var9, false);
   }

   public MouseScrollEvent(long var1, double var3, double var5, double var7, double var9, boolean var11) {
      this.instance = var1;
      this.data = var3;
      this.context = var5;
      this.config = var7;
      this.state = var9;
      this.cache = var11;
   }

   public long compute() {
      return this.instance;
   }

   public double resolve() {
      return this.data;
   }

   public double update() {
      return this.context;
   }

   public double apply() {
      return this.config;
   }

   public double execute() {
      return this.state;
   }

   public boolean prepare() {
      return this.cache;
   }
}
