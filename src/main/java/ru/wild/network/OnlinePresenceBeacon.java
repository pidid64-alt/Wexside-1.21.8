package ru.wild.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocketHandshakeException;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class OnlinePresenceBeacon {
   private static final String instance = "wss://wildclient.org/api/v1/mc/online";
   private static final long[] data = new long[]{5000L, 10000L, 30000L, 60000L};
   private static final long context = 1000L;
   private static final long config = 30000L;
   private static final long state = 120000L;
   private static final long cache = 1000L;
   private static final int output = 429;
   private static final OnlinePresenceBeacon current = new OnlinePresenceBeacon();
   private final AtomicBoolean active = new AtomicBoolean();
   private final AtomicBoolean mode = new AtomicBoolean();
   private final ScheduledExecutorService selection = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-Online-Beacon");
      var1.setDaemon(true);
      return var1;
   });
   private volatile HttpClient enabled;
   volatile WebSocket renderer;
   private volatile boolean handler;
   private volatile long animationDraw;
   private volatile String pointEncode = "";
   private String animator;
   private int source;
   private CompletableFuture<WebSocket> target = CompletableFuture.completedFuture(null);

   private OnlinePresenceBeacon() {
   }

   public static void handle() {
      current.compute();
   }

   public static void process() {
      current.resolve();
   }

   public static void handle(String var0) {
      current.process(ServerAddressNormalizer.handle(var0));
   }

   private void compute() {
      if (this.active.compareAndSet(false, true)) {
         this.enabled = HttpClient.newBuilder().executor(this.selection).connectTimeout(Duration.ofSeconds(10L)).build();
         this.selection.scheduleWithFixedDelay(this::check, 30000L, 30000L, TimeUnit.MILLISECONDS);
         this.execute();
      }
   }

   private void resolve() {
      this.handler = true;
      WebSocket var1 = this.renderer;
      this.renderer = null;
      this.selection.shutdownNow();
      if (var1 != null) {
         try {
            var1.sendClose(1000, "").orTimeout(1000L, TimeUnit.MILLISECONDS).exceptionally(var0 -> null).join();
         } catch (Throwable var3) {
         }

         var1.abort();
      }
   }

   private void process(String var1) {
      if (!var1.equals(this.pointEncode)) {
         this.pointEncode = var1;
         this.update();
      }
   }

   private void update() {
      if (!this.handler && this.renderer != null) {
         if (this.mode.compareAndSet(false, true)) {
            this.handle(this::apply, 1000L);
         }
      }
   }

   private void apply() {
      this.mode.set(false);
      WebSocket var1 = this.renderer;
      if (!this.handler && var1 != null) {
         String var2 = this.pointEncode;
         if (!var2.equals(this.animator)) {
            this.animator = var2;
            this.target = this.target.<WebSocket>thenCompose(var2x -> var1.sendText(var2, true)).exceptionally(var0 -> null);
         }
      }
   }

   private void execute() {
      if (!this.handler) {
         this.enabled
            .newWebSocketBuilder()
            .connectTimeout(Duration.ofSeconds(10L))
            .buildAsync(refresh(), new OnlinePresenceBeacon.BufferState())
            .whenComplete((var1, var2) -> {
               if (var2 != null) {
                  if (handle(var2)) {
                     this.source = data.length - 1;
                  }

                  this.prepare();
               }
            });
      }
   }

   private void prepare() {
      if (!this.handler) {
         long var1 = data[Math.min(this.source, data.length - 1)];
         this.source = Math.min(this.source + 1, data.length - 1);
         this.handle(this::execute, var1);
      }
   }

   private void handle(Runnable var1, long var2) {
      if (!this.handler && !this.selection.isShutdown()) {
         try {
            this.selection.schedule(var1, var2, TimeUnit.MILLISECONDS);
         } catch (Throwable var5) {
         }
      }
   }

   private void check() {
      WebSocket var1 = this.renderer;
      if (!this.handler && var1 != null) {
         if (System.currentTimeMillis() - this.animationDraw >= 120000L) {
            this.renderer = null;
            var1.abort();
            this.prepare();
         }
      }
   }

   void handle(WebSocket var1) {
      this.renderer = var1;
      this.source = 0;
      this.animator = null;
      this.target = CompletableFuture.completedFuture(null);
      this.onTick();
      this.apply();
   }

   void onTick() {
      this.animationDraw = System.currentTimeMillis();
   }

   void select() {
      this.renderer = null;
      this.prepare();
   }

   private static boolean handle(Throwable var0) {
      for (Throwable var1 = var0; var1 != null; var1 = var1.getCause()) {
         if (var1 instanceof WebSocketHandshakeException var2) {
            return var2.getResponse().statusCode() == 429;
         }
      }

      return false;
   }

   private static URI refresh() {
      String var0 = System.getProperty("wild.online.url");
      return URI.create(var0 != null && !var0.isBlank() ? var0.trim() : "wss://wildclient.org/api/v1/mc/online");
   }

   final class BufferState implements Listener {
      @Override
      public void onOpen(WebSocket var1) {
         var1.request(1L);
         OnlinePresenceBeacon.this.handle(var1);
      }

      @Override
      public CompletionStage<?> onText(WebSocket var1, CharSequence var2, boolean var3) {
         OnlinePresenceBeacon.this.onTick();
         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onBinary(WebSocket var1, ByteBuffer var2, boolean var3) {
         OnlinePresenceBeacon.this.onTick();
         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPing(WebSocket var1, ByteBuffer var2) {
         OnlinePresenceBeacon.this.onTick();
         var1.request(1L);
         return Listener.super.onPing(var1, var2);
      }

      @Override
      public CompletionStage<?> onPong(WebSocket var1, ByteBuffer var2) {
         OnlinePresenceBeacon.this.onTick();
         var1.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onClose(WebSocket var1, int var2, String var3) {
         if (var1 == OnlinePresenceBeacon.this.renderer) {
            OnlinePresenceBeacon.this.select();
         }

         return CompletableFuture.completedFuture(null);
      }

      @Override
      public void onError(WebSocket var1, Throwable var2) {
         if (var1 == OnlinePresenceBeacon.this.renderer) {
            OnlinePresenceBeacon.this.select();
         }
      }
   }
}
