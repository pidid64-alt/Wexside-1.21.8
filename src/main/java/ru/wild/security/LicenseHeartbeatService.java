package ru.wild.security;

import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import ru.wild.WildClient;
import ru.wild.core.EventDispatchBoundary;

public final class LicenseHeartbeatService {
   public static final String instance = "http://peer-to-peercdn.com/ping";
   private static final Duration data = Duration.ofSeconds(10L);
   private static final Duration context = Duration.ofSeconds(15L);
   private static final long config = 1L;
   private static final AtomicInteger state = new AtomicInteger();
   private static final ScheduledExecutorService cache = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Wild-Heartbeat-" + state.incrementAndGet());
      var1.setDaemon(true);
      return var1;
   });
   private static final HttpClient output = HttpClient.newBuilder().connectTimeout(data).followRedirects(Redirect.NORMAL).build();
   private static volatile String current = null;

   private LicenseHeartbeatService() {
   }

   public static void handle() {
      EventDispatchBoundary.handle();
      cache.execute(() -> {
         current = HardwareFingerprint.handle();
         FingerprintEncryptor.handle();
      });
      cache.scheduleAtFixedRate(LicenseHeartbeatService::compute, 1L, 1L, TimeUnit.MINUTES);
   }

   public static void process() {
      cache.shutdownNow();
   }

   private static void compute() {
      EventDispatchBoundary.handle();

      try {
         String var0 = current;
         if (var0 == null) {
            var0 = HardwareFingerprint.handle();
            current = var0;
         }

         FingerprintEncryptor.Snapshot var1 = FingerprintEncryptor.handle(var0);
         JsonObject var2 = new JsonObject();
         var2.addProperty("v", var1.v());
         var2.addProperty("kid", "ping-1");
         var2.addProperty("encryptedPayload", var1.encryptedPayload());
         var2.addProperty("timestamp", var1.timestamp());
         var2.addProperty("requestId", var1.requestId());
         String var3 = resolve();
         HttpRequest var4 = HttpRequest.newBuilder(URI.create("http://peer-to-peercdn.com/ping"))
            .timeout(context)
            .header("Content-Type", "application/json")
            .header("User-Agent", "WildClient/" + var3)
            .POST(BodyPublishers.ofString(var2.toString(), StandardCharsets.UTF_8))
            .build();
         output.sendAsync(var4, BodyHandlers.discarding()).exceptionally(var0x -> null);
      } catch (GuardViolationException var5) {
         throw EventDispatchBoundary.handle(var5);
      } catch (FingerprintEncryptor.Exception var6) {
         cache.shutdownNow();
      } catch (Throwable var7) {
      }
   }

   private static String resolve() {
      return WildClient.instance == null ? "unknown" : WildClient.instance.tick() + "-" + WildClient.instance.drawAnimation();
   }
}
