package ru.wild.render.shader;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SamplerDirtyFlags {
   private final AtomicBoolean instance = new AtomicBoolean();
   private final AtomicBoolean data = new AtomicBoolean();

   public void handle() {
      this.instance.set(true);
   }

   public boolean process() {
      return this.instance.get();
   }

   public boolean compute() {
      return this.instance.get() && this.data.compareAndSet(false, true);
   }

   public void resolve() {
      this.data.set(false);
   }

   public void update() {
      this.data.set(false);
   }

   public void handle(boolean var1) {
      this.instance.set(!var1);
   }
}
