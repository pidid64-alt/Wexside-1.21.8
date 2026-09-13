package ru.wild.api.event;

public abstract class CancellableEvent extends Event {
   private boolean instance;

   public void compute() {
      this.instance = true;
   }

   public boolean resolve() {
      return this.instance;
   }
}
