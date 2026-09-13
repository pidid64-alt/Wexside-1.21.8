package ru.wild.api.event;

public final class InputButtonEvent extends Event {
   private final long instance;
   private final int data;
   private final int context;
   private final int config;
   private final int state;

   public InputButtonEvent(long var1, int var3, int var4, int var5, int var6) {
      this.instance = var1;
      this.data = var3;
      this.context = var4;
      this.config = var5;
      this.state = var6;
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

   public int execute() {
      return this.state;
   }
}
