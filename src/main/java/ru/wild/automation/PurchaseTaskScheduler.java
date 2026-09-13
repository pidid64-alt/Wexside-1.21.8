package ru.wild.automation;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public final class PurchaseTaskScheduler {
   public static final long instance = 4000L;
   private static final int data = 32;
   private static final int context = 10;
   private static final int config = 6;
   private static final int state = 3;
   private static final int cache = 10;
   private static final long output = 140L;
   private static final long current = 90L;
   private static final double active = 3.0;
   private static final double mode = 1.8;
   private static final double selection = 0.15;
   private static final long enabled = 150L;
   private static final double renderer = 2.5;
   private final AtomicLong handler = new AtomicLong();
   private volatile int animationDraw = -1;
   private final long[] pointEncode = new long[32];
   private final long[] animator = new long[10];
   private int source;
   private int target;
   private double pending = -1.0;
   private long previous;
   private long latest;
   private int summary;
   private int matrixBlend;
   private boolean vectorMatch;
   private long itemProject;

   public synchronized void handle() {
      this.handler.set(0L);
      this.animationDraw = -1;
      this.source = 0;
      this.target = 0;
      this.pending = -1.0;
      this.previous = 0L;
      this.latest = 0L;
      this.summary = 0;
      this.matrixBlend = 0;
      this.vectorMatch = false;
      this.itemProject = 0L;
   }

   public synchronized void process() {
      this.handler.set(0L);
      this.animationDraw = -1;
      this.source = 0;
      this.target = 0;
      this.previous = 0L;
      this.latest = 0L;
      this.summary = 0;
      this.matrixBlend = 0;
      this.vectorMatch = false;
      this.itemProject = 0L;
   }

   public void compute() {
      this.handler.set(0L);
      this.animationDraw = -1;
   }

   public void handle(int var1) {
      if (this.handler.get() == 0L) {
         this.animationDraw = var1;
         this.handler.compareAndSet(0L, System.currentTimeMillis());
      }
   }

   public void process(int var1) {
      if (var1 > 0) {
         this.refresh();
      }
   }

   public void compute(int var1) {
      if (var1 > 0 && var1 == this.animationDraw) {
         this.refresh();
      }
   }

   public void resolve(int var1) {
      if (var1 > 0) {
         this.refresh();
      }
   }

   public void resolve() {
      long var1 = this.handler.get();
      if (var1 != 0L) {
         if (System.currentTimeMillis() - var1 >= 4000L) {
            if (this.handler.compareAndSet(var1, 0L)) {
               this.animationDraw = -1;
               this.handle(4000L, true);
            }
         }
      }
   }

   private void refresh() {
      long var1 = this.handler.get();
      if (var1 != 0L) {
         if (this.handler.compareAndSet(var1, 0L)) {
            this.animationDraw = -1;
            this.handle(Math.max(1L, System.currentTimeMillis() - var1), false);
         }
      }
   }

   private synchronized void handle(long var1, boolean var3) {
      this.pointEncode[this.target] = var1;
      this.target = (this.target + 1) % 32;
      if (this.source < 32) {
         this.source++;
      }

      this.previous = var1;
      this.summary = var3 ? this.summary + 1 : 0;
      if (!var3 && !this.vectorMatch && this.matrixBlend == 0) {
         if (this.pending < 0.0) {
            this.pending = var1;
         } else if (var1 <= Math.max(150.0, this.pending * 2.5)) {
            this.pending = this.pending * 0.85 + var1 * 0.15;
         }
      }

      this.latest = this.render();
      this.tick();
   }

   private long render() {
      int var1 = Math.min(10, this.source);

      for (int var2 = 0; var2 < var1; var2++) {
         this.animator[var2] = this.pointEncode[(this.target - 1 - var2 + 64) % 32];
      }

      Arrays.sort(this.animator, 0, var1);
      return this.animator[var1 / 2];
   }

   private void tick() {
      if (!(this.pending < 0.0) && this.source >= 6) {
         long var1 = Math.max(140L, (long)(this.pending * 3.0));
         long var3 = Math.max(90L, (long)(this.pending * 1.8));
         if (this.latest >= var1) {
            this.matrixBlend++;
         } else if (this.latest <= var3) {
            this.matrixBlend = 0;
         }

         if (!this.vectorMatch) {
            if (this.matrixBlend >= 10 || this.summary >= 3) {
               this.vectorMatch = true;
               this.itemProject = System.currentTimeMillis();
            }
         } else if (this.latest <= var3 && this.summary == 0) {
            this.vectorMatch = false;
            this.matrixBlend = 0;
            this.itemProject = 0L;
         }
      }
   }

   public synchronized boolean update() {
      return this.vectorMatch;
   }

   public synchronized long apply() {
      return this.vectorMatch ? System.currentTimeMillis() - this.itemProject : 0L;
   }

   public synchronized long execute() {
      return this.latest;
   }

   public synchronized long prepare() {
      return this.pending < 0.0 ? 0L : Math.round(this.pending);
   }

   public synchronized long check() {
      return this.previous;
   }

   public synchronized int onTick() {
      return this.summary;
   }

   public synchronized int select() {
      return this.source;
   }
}
