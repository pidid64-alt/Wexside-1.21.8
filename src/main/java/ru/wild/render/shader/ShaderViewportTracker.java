package ru.wild.render.shader;

public final class ShaderViewportTracker {
   private static final ShaderViewportTracker instance = new ShaderViewportTracker();
   private final Object data = new Object();
   private long context = 0L;
   private long config = 0L;
   private int state = 0;
   private int cache = 0;
   private int output = 0;
   private int current = 0;

   private ShaderViewportTracker() {
   }

   public static ShaderViewportTracker handle() {
      return instance;
   }

   public void handle(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         long var3 = System.nanoTime();
         synchronized (this.data) {
            this.context = var3;
            this.state = 0;
            this.cache = 0;
         }
      }
   }
   public void handle(int var1) {
      if (var1 < 0) {
         var1 = 0;
      }

      Object var2 = this.data;
      synchronized (this.data){} // $VF: monitorenter 

      try {
         this.state++;
         this.cache += var1;
      } finally {
      }
   }

   public void process() {
      long var1 = System.nanoTime();
      synchronized (this.data) {
         if (this.context <= 0L) {
            this.context = var1;
            this.config = 0L;
            this.output = this.state;
            this.current = this.cache;
            this.state = 0;
            this.cache = 0;
         } else {
            this.config = Math.max(0L, var1 - this.context);
            this.output = this.state;
            this.current = this.cache;
            this.context = var1;
            this.state = 0;
            this.cache = 0;
         }
      }
   }

   public ShaderViewportTracker.DataRecord compute() {
      synchronized (this.data) {
         return new ShaderViewportTracker.DataRecord(this.config, this.output, this.current);
      }
   }

   public record DataRecord(long frameDurationNanos, int drawCalls, int triangles) {
      public DataRecord(long frameDurationNanos, int drawCalls, int triangles) {
         frameDurationNanos = Math.max(0L, frameDurationNanos);
         drawCalls = Math.max(0, drawCalls);
         triangles = Math.max(0, triangles);
         this.frameDurationNanos = frameDurationNanos;
         this.drawCalls = drawCalls;
         this.triangles = triangles;
      }

      public double frameTimeMillis() {
         return this.frameDurationNanos / 1000000.0;
      }

      public double framesPerSecond() {
         return this.frameDurationNanos > 0L ? 1.0E9 / this.frameDurationNanos : 0.0;
      }
   }
}
