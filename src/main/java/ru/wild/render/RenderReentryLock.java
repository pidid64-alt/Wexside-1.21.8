package ru.wild.render;

public final class RenderReentryLock {
   private long instance;
   private long data;
   private boolean context;

   public synchronized void handle() {
      this.instance++;
      this.context = false;
   }

   public synchronized void process() {
      this.data = this.instance;
      this.context = true;
   }

   public synchronized boolean compute() {
      boolean var1 = this.context && this.data == this.instance;
      this.context = false;
      return var1;
   }

   public synchronized void resolve() {
      this.context = false;
   }
}
