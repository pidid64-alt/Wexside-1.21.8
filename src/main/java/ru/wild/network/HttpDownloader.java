package ru.wild.network;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import ru.wild.WildClient;
import ru.wild.config.ConfigManager;
import ru.wild.config.ConfigProfile;

public final class HttpDownloader {
   public static final String instance = "https://raw.githubusercontent.com/Minecraft-Wild/configs/main/";
   private static final Duration data = Duration.ofSeconds(6L);
   private static final Duration context = Duration.ofSeconds(10L);
   private static final Pattern config = Pattern.compile("[A-Za-z0-9._-]+");
   private static final AtomicInteger state = new AtomicInteger();
   private static final ExecutorService cache = Executors.newFixedThreadPool(2, var0 -> {
      Thread var1 = new Thread(var0, "Wild-CloudConfig-" + state.incrementAndGet());
      var1.setDaemon(true);
      return var1;
   });
   private static final HttpClient output = HttpClient.newBuilder().connectTimeout(data).followRedirects(Redirect.NORMAL).executor(cache).build();
   private static volatile List<String> current = List.of();

   private HttpDownloader() {
   }

   public static CompletableFuture<HttpDownloader.SecondaryDataRecord> handle(String var0) {
      URI var1 = process(var0);
      String var2 = handle(var0, var1);
      if (var1 == null) {
         return CompletableFuture.completedFuture(HttpDownloader.SecondaryDataRecord.failure(var2, null, "Некорректное имя или URL."));
      }

      HttpRequest var3 = handle(var1);
      return output.sendAsync(var3, BodyHandlers.ofString(StandardCharsets.UTF_8))
         .thenComposeAsync(var2x -> handle(var2, var1, (HttpResponse<String>)var2x), cache)
         .exceptionally(var2x -> HttpDownloader.SecondaryDataRecord.failure(var2, var1.toString(), handle(var2x)));
   }

   public static CompletableFuture<HttpDownloader.DataRecord> handle() {
      URI var0 = resolve();
      HttpRequest var1 = handle(var0);
      return output.sendAsync(var1, BodyHandlers.ofString(StandardCharsets.UTF_8))
         .thenApplyAsync(var1x -> handle(var0, (HttpResponse<String>)var1x), cache)
         .exceptionally(var1x -> HttpDownloader.DataRecord.failure(var0.toString(), handle(var1x)));
   }

   public static List<String> process() {
      return current;
   }

   public static void compute() {
      cache.shutdownNow();
   }

   private static CompletableFuture<HttpDownloader.SecondaryDataRecord> handle(String var0, URI var1, HttpResponse<String> var2) {
      if (!handle(var2.statusCode())) {
         return CompletableFuture.completedFuture(HttpDownloader.SecondaryDataRecord.failure(var0, var1.toString(), "HTTP " + var2.statusCode()));
      }

      JsonElement var3;
      try {
         var3 = JsonParser.parseString((String)var2.body());
      } catch (Exception var5) {
         return CompletableFuture.completedFuture(HttpDownloader.SecondaryDataRecord.failure(var0, var1.toString(), "Некорректный JSON: " + process(var5)));
      }

      if (var3 != null && var3.isJsonObject()) {
         JsonObject var4 = var3.getAsJsonObject();
         return handle(() -> handle(var0, var1, var4)).thenApplyAsync(HttpDownloader::handle, cache);
      } else {
         return CompletableFuture.completedFuture(HttpDownloader.SecondaryDataRecord.failure(var0, var1.toString(), "Файл должен содержать JSON-объект."));
      }
   }

   private static HttpDownloader.PrimaryDataRecord handle(String var0, URI var1, JsonObject var2) {
      if (WildClient.instance == null || WildClient.instance.renderer == null) {
         return HttpDownloader.PrimaryDataRecord.failure(var0, var1.toString(), "ConfigManager не инициализирован.");
      }

      if (!WildClient.instance.renderer.handle(var0, var2)) {
         return HttpDownloader.PrimaryDataRecord.failure(var0, var1.toString(), "Не удалось применить конфиг.");
      }

      ConfigProfile var3 = WildClient.instance.renderer.compute(var0);
      return var3 == null
         ? HttpDownloader.PrimaryDataRecord.failure(var0, var1.toString(), "Не удалось подготовить локальную копию.")
         : HttpDownloader.PrimaryDataRecord.success(var0, var1.toString(), var3.handle(), var3.compute());
   }

   private static HttpDownloader.SecondaryDataRecord handle(HttpDownloader.PrimaryDataRecord var0) {
      if (!var0.success()) {
         return HttpDownloader.SecondaryDataRecord.failure(var0.name(), var0.url(), var0.error());
      }

      try {
         if (!ConfigManager.instance.exists() && !ConfigManager.instance.mkdirs()) {
            return HttpDownloader.SecondaryDataRecord.failure(var0.name(), var0.url(), "Не удалось создать папку конфигов.");
         }

         String var1 = new GsonBuilder().setPrettyPrinting().create().toJson(var0.object());
         Files.writeString(var0.file().toPath(), var1, StandardCharsets.UTF_8);
         return HttpDownloader.SecondaryDataRecord.success(var0.name(), var0.url());
      } catch (Exception var2) {
         return HttpDownloader.SecondaryDataRecord.failure(var0.name(), var0.url(), "Конфиг применен, но не сохранен на диск.");
      }
   }

   private static HttpDownloader.DataRecord handle(URI var0, HttpResponse<String> var1) {
      if (!handle(var1.statusCode())) {
         return HttpDownloader.DataRecord.failure(var0.toString(), "HTTP " + var1.statusCode());
      }

      JsonElement var2;
      try {
         var2 = JsonParser.parseString((String)var1.body());
      } catch (Exception var8) {
         return HttpDownloader.DataRecord.failure(var0.toString(), "Некорректный index.json: " + process(var8));
      }

      if (var2 != null && var2.isJsonArray()) {
         JsonArray var3 = var2.getAsJsonArray();
         LinkedHashSet var4 = new LinkedHashSet();

         for (JsonElement var6 : var3) {
            String var7 = handle(var6);
            if (var7 != null) {
               var4.add(var7);
            }
         }

         List var9 = List.copyOf(var4);
         current = var9;
         return HttpDownloader.DataRecord.success(var9, var0.toString());
      } else {
         return HttpDownloader.DataRecord.failure(var0.toString(), "index.json должен быть JSON-массивом.");
      }
   }

   private static String handle(JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         String var1 = null;

         try {
            if (var0.isJsonPrimitive()) {
               var1 = var0.getAsString();
            } else if (var0.isJsonObject()) {
               JsonObject var2 = var0.getAsJsonObject();
               if (var2.has("name")) {
                  var1 = var2.get("name").getAsString();
               }
            }
         } catch (Exception var3) {
            return null;
         }

         return compute(var1);
      } else {
         return null;
      }
   }

   private static URI process(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim();
         if (!var1.startsWith("https://") && !var1.startsWith("http://")) {
            String var2 = compute(var1);
            if (var2 == null) {
               return null;
            }

            String var3 = URLEncoder.encode(var2, StandardCharsets.UTF_8).replace("+", "%20");
            return URI.create(update() + var3 + ".json");
         } else {
            try {
               return URI.create(var1);
            } catch (Exception var4) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static URI resolve() {
      return URI.create(update() + "index.json");
   }

   private static HttpRequest handle(URI var0) {
      return HttpRequest.newBuilder(var0).timeout(context).header("Accept", "application/json").header("User-Agent", "WildClient-CloudConfig").GET().build();
   }

   private static String update() {
      String var0 = System.getProperty("wild.config.repo");
      if (var0 == null || var0.isBlank()) {
         var0 = System.getenv("WILD_CONFIG_REPO");
      }

      if (var0 == null || var0.isBlank()) {
         var0 = "https://raw.githubusercontent.com/Minecraft-Wild/configs/main/";
      }

      var0 = var0.trim();
      return var0.endsWith("/") ? var0 : var0 + "/";
   }

   private static String compute(String var0) {
      if (var0 == null) {
         return null;
      }

      String var1 = var0.trim();
      if (var1.endsWith(".json")) {
         var1 = var1.substring(0, var1.length() - 5);
      }

      return !var1.isBlank() && config.matcher(var1).matches() ? var1 : null;
   }

   private static String handle(String var0, URI var1) {
      String var2 = null;
      if (var0 != null) {
         String var3 = var0.trim();
         if (!var3.startsWith("https://") && !var3.startsWith("http://")) {
            var2 = compute(var3);
         } else {
            String var4 = var1 == null ? "" : var1.getPath();
            int var5 = var4.lastIndexOf(47);
            var2 = var5 >= 0 ? var4.substring(var5 + 1) : "cloud";
         }
      }

      if (var2 == null || var2.isBlank()) {
         var2 = "cloud";
      }

      if (var2.endsWith(".json")) {
         var2 = var2.substring(0, var2.length() - 5);
      }

      var2 = var2.replaceAll("[^A-Za-z0-9._-]", "_");
      return var2.isBlank() ? "cloud" : var2;
   }

   private static boolean handle(int var0) {
      return var0 >= 200 && var0 < 300;
   }

   private static <T> CompletableFuture<T> handle(Callable<T> var0) {
      CompletableFuture var1 = new CompletableFuture();
      MinecraftClient var2 = MinecraftClient.getInstance();
      Runnable var3 = () -> {
         try {
            var1.complete(var0.call());
         } catch (Throwable var3x) {
            var1.completeExceptionally(var3x);
         }
      };
      if (var2 == null) {
         var3.run();
      } else {
         var2.execute(var3);
      }

      return var1;
   }

   private static String handle(Throwable var0) {
      Throwable var1 = var0;

      while (var1 instanceof CompletionException && var1.getCause() != null) {
         var1 = var1.getCause();
      }

      return process(var1);
   }

   private static String process(Throwable var0) {
      if (var0 == null) {
         return "неизвестная ошибка";
      }

      String var1 = var0.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : var0.getClass().getSimpleName();
   }

   public record DataRecord(boolean success, List<String> names, String url, String error) {
      public DataRecord(boolean success, List<String> names, String url, String error) {
         names = names == null ? List.of() : List.copyOf(new ArrayList(names));
         this.success = success;
         this.names = names;
         this.url = url;
         this.error = error;
      }

      public static HttpDownloader.DataRecord success(List<String> var0, String var1) {
         return new HttpDownloader.DataRecord(true, var0, var1, null);
      }

      public static HttpDownloader.DataRecord failure(String var0, String var1) {
         return new HttpDownloader.DataRecord(false, List.of(), var0, var1);
      }
   }

   record PrimaryDataRecord(boolean success, String name, String url, File file, JsonObject object, String error) {
      public static HttpDownloader.PrimaryDataRecord success(String var0, String var1, File var2, JsonObject var3) {
         return new HttpDownloader.PrimaryDataRecord(true, var0, var1, var2, var3, null);
      }

      public static HttpDownloader.PrimaryDataRecord failure(String var0, String var1, String var2) {
         return new HttpDownloader.PrimaryDataRecord(false, var0, var1, null, null, var2);
      }
   }

   public record SecondaryDataRecord(boolean success, String name, String url, String error) {
      public static HttpDownloader.SecondaryDataRecord success(String var0, String var1) {
         return new HttpDownloader.SecondaryDataRecord(true, var0, var1, null);
      }

      public static HttpDownloader.SecondaryDataRecord failure(String var0, String var1, String var2) {
         return new HttpDownloader.SecondaryDataRecord(false, var0, var1, var2);
      }
   }
}
