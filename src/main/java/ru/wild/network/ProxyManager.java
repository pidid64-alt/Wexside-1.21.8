package ru.wild.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.ProxyConnectionEvent;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.io.File;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.wild.WildClient;

public final class ProxyManager {
   public static final String instance = "Socks4";
   public static final String data = "Socks5";
   private static final Gson active = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
   private static final Pattern mode = Pattern.compile(
      "(?i)(?:socks\\s*[45]|so+cks?\\s*[45])?\\s*(?:://)?([A-Za-z0-9._~%+\\-]+):([^\\s@]+)@([A-Za-z0-9.\\-]+):(\\d{1,5})"
   );
   private static final Pattern selection = Pattern.compile(
      "(?i)(?:socks\\s*[45]|so+cks?\\s*[45])?\\s*(?:://)?([A-Za-z0-9.\\-]+):(\\d{1,5}):([^\\s:]+):([^\\s]+)"
   );
   private static final Pattern enabled = Pattern.compile("(?i)(?<![A-Za-z0-9._:-])([A-Za-z0-9.\\-]+):(\\d{1,5})(?![A-Za-z0-9._:-])");
   public static volatile String context = "";
   public static volatile String config = "";
   public static volatile String state = "Socks5";
   public static volatile String cache = "";
   public static volatile String output = "";
   public static volatile boolean current = false;
   private static volatile boolean renderer;

   private ProxyManager() {
   }

   public static synchronized void handle() {
      if (!renderer) {
         renderer = true;
         File var0 = update();
         if (var0 != null && var0.exists() && var0.isFile()) {
            try (FileReader var1 = new FileReader(var0, StandardCharsets.UTF_8)) {
               JsonElement var2 = JsonParser.parseReader(var1);
               if (var2 != null && var2.isJsonObject()) {
                  JsonObject var3 = var2.getAsJsonObject();
                  current = handle(var3, "enabled", false);
                  state = process(handle(var3, "type", "Socks5"));
                  context = compute(handle(var3, "host", handle(var3, "ip", "")));
                  config = update(handle(var3, "port", ""));
                  cache = select(handle(var3, "username", ""));
                  output = select(handle(var3, "password", ""));
               }
            } catch (Throwable var6) {
            }
         }
      }
   }

   public static synchronized void process() {
      renderer = true;

      try {
         File var0 = update();
         if (var0 == null) {
            return;
         }

         JsonObject var1 = new JsonObject();
         var1.addProperty("enabled", current);
         var1.addProperty("type", process(state));
         var1.addProperty("host", compute(context));
         var1.addProperty("port", update(config));
         var1.addProperty("username", select(cache));
         var1.addProperty("password", select(output));
         handle(var0, active.toJson(var1).getBytes(StandardCharsets.UTF_8));
      } catch (Throwable var2) {
      }
   }

   public static synchronized void handle(ProxyManager.DataRecord var0) {
      if (var0 != null) {
         renderer = true;
         current = var0.enabled();
         state = process(var0.type());
         context = compute(var0.host());
         config = update(var0.port());
         cache = select(var0.username()).trim();
         output = select(var0.password());
         process();
      }
   }

   public static ProxyManager.DataRecord compute() {
      handle();
      return new ProxyManager.DataRecord(current, process(state), compute(context), update(config), select(cache).trim(), select(output));
   }

   public static ProxyHandler resolve() {
      return process(compute());
   }

   public static ProxyHandler process(ProxyManager.DataRecord var0) {
      ProxyManager.DataRecord var1 = compute(var0);
      if (var1.enabled() && handle(var1, true) == null) {
         InetSocketAddress var2 = new InetSocketAddress(var1.host(), var1.portInt());
         if (var1.isSocks4()) {
            String var4 = prepare(var1.username());
            return var4 == null ? new Socks4ProxyHandler(var2) : new Socks4ProxyHandler(var2, var4);
         } else {
            String var3 = prepare(var1.username());
            return var3 == null ? new Socks5ProxyHandler(var2) : new Socks5ProxyHandler(var2, var3, var1.password());
         }
      } else {
         return null;
      }
   }

   public static CompletableFuture<ProxyManager.PrimaryDataRecord> handle(ProxyManager.DataRecord var0, String var1, int var2, final int var3) {
      final ProxyManager.DataRecord var4 = compute(var0).withEnabled(true);
      String var5 = handle(var4, true);
      if (var5 != null) {
         return CompletableFuture.completedFuture(new ProxyManager.PrimaryDataRecord(false, 0L, var5));
      }

      final CompletableFuture var6 = new CompletableFuture();
      final NioEventLoopGroup var7 = new NioEventLoopGroup(1, var0x -> {
         Thread var1x = new Thread(var0x, "Wild Proxy Test");
         var1x.setDaemon(true);
         return var1x;
      });
      final long var8 = System.nanoTime();

      try {
         Bootstrap var10 = (Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(var7)).channel(NioSocketChannel.class))
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, var3))
            .handler(new ChannelInitializer<SocketChannel>() {
               protected void initChannel(SocketChannel var1) {
                  ProxyHandler var2x = ProxyManager.process(var4);
                  if (var2x == null) {
                     throw new IllegalStateException("Proxy config is invalid");
                  }

                  var2x.setConnectTimeoutMillis(var3);
                  var1.pipeline().addFirst("wild_proxy_test", var2x);
                  var1.pipeline().addLast("wild_proxy_result", new ChannelInboundHandlerAdapter() {
                     public void userEventTriggered(ChannelHandlerContext var1, Object var2x) throws Exception {
                        if (var2x instanceof ProxyConnectionEvent) {
                           long var3x = Math.max(1L, (System.nanoTime() - var8) / 1000000L);
                           var6.complete(new ProxyManager.PrimaryDataRecord(true, var3x, "OK"));
                           var1.close();
                           var7.shutdownGracefully();
                        } else {
                           super.userEventTriggered(var1, var2x);
                        }
                     }

                     public void exceptionCaught(ChannelHandlerContext var1, Throwable var2x) {
                        long var3x = Math.max(1L, (System.nanoTime() - var8) / 1000000L);
                        var6.complete(new ProxyManager.PrimaryDataRecord(false, var3x, ProxyManager.handle(var2x)));
                        var1.close();
                        var7.shutdownGracefully();
                     }

                     public void channelInactive(ChannelHandlerContext var1) throws Exception {
                        long var2x = Math.max(1L, (System.nanoTime() - var8) / 1000000L);
                        var6.complete(new ProxyManager.PrimaryDataRecord(false, var2x, "Connection closed"));
                        var7.shutdownGracefully();
                        super.channelInactive(var1);
                     }
                  });
               }
            });
         ChannelFuture var14 = var10.connect(InetSocketAddress.createUnresolved(var1, var2));
         var14.addListener((ChannelFutureListener)var4x -> {
            if (!var4x.isSuccess()) {
               long var5x = Math.max(1L, (System.nanoTime() - var8) / 1000000L);
               boolean var13x = false /* VF: Semaphore variable */;

               try {
                  var13x = true;
                  var6.complete(new ProxyManager.PrimaryDataRecord(false, var5x, handle(var4x.cause())));
                  var13x = false;
               } finally {
                  if (var13x) {
                     try {
                        var4x.channel().close();
                     } catch (Throwable var14x) {
                     }

                     var7.shutdownGracefully();
                  }
               }

               try {
                  var4x.channel().close();
               } catch (Throwable var15) {
               }

               var7.shutdownGracefully();
            }
         });
         var7.schedule(() -> {
            if (var6.complete(new ProxyManager.PrimaryDataRecord(false, var3, "Timed out"))) {
               try {
                  var14.channel().close();
               } catch (Throwable var5x) {
               }

               var7.shutdownGracefully();
            }
         }, var3 + 1000L, TimeUnit.MILLISECONDS);
      } catch (Throwable var13) {
         long var11 = Math.max(1L, (System.nanoTime() - var8) / 1000000L);
         var6.complete(new ProxyManager.PrimaryDataRecord(false, var11, handle(var13)));
         var7.shutdownGracefully();
      }

      return var6;
   }

   public static String handle(ProxyManager.DataRecord var0, boolean var1) {
      ProxyManager.DataRecord var2 = compute(var0);
      if (!var1 && !var2.enabled() && var2.host().isBlank() && var2.port().isBlank()) {
         return null;
      } else if (var2.host().isBlank()) {
         return "Proxy host is empty";
      } else if (!resolve(var2.host())) {
         return "Proxy host has invalid characters";
      } else {
         int var3 = execute(var2.port());
         if (var3 <= 0) {
            return "Proxy port is invalid";
         } else {
            return var2.isSocks5() && !var2.password().isBlank() && var2.username().isBlank() ? "SOCKS5 username is empty" : null;
         }
      }
   }

   public static ProxyManager.SecondaryDataRecord handle(String var0) {
      String var1 = select(var0).trim();
      if (var1.isEmpty()) {
         return ProxyManager.SecondaryDataRecord.empty();
      }

      String var2 = handle(var1, "Socks5");
      Matcher var3 = mode.matcher(var1);
      if (var3.find()) {
         return new ProxyManager.SecondaryDataRecord(var2, var3.group(3), var3.group(4), onTick(var3.group(1)), onTick(var3.group(2)));
      }

      Matcher var4 = selection.matcher(var1);
      if (var4.find()) {
         return new ProxyManager.SecondaryDataRecord(var2, var4.group(1), var4.group(2), onTick(var4.group(3)), onTick(var4.group(4)));
      }

      String var5 = "";
      String var6 = "";
      String var7 = "";
      String var8 = "";
      String[] var9 = var1.replace("\r", "").split("\n");

      for (String var13 : var9) {
         String var14 = var13.trim();
         String var15 = var14.toLowerCase(Locale.ROOT);
         String var16 = check(var14);
         if (!var16.isBlank()) {
            var4 = selection.matcher(var16);
            if (var4.find()) {
               return new ProxyManager.SecondaryDataRecord(var2, var4.group(1), var4.group(2), onTick(var4.group(3)), onTick(var4.group(4)));
            }

            if (var15.contains("wexside")) {
               ProxyManager.SecondaryDataRecord var17 = handle(var16);
               if (!var17.host().isBlank()) {
                  return var17.withType(var2);
               }
            }

            if (var15.contains("login") || var15.contains("username") || var15.contains("логин")) {
               var7 = var16.trim();
            } else if (var15.contains("password") || var15.contains("пароль")) {
               var8 = var16.trim();
            } else if (var15.contains("port") || var15.contains("порт")) {
               var6 = update(var16);
            } else if (var15.contains("proxy") || var15.contains("прокси")) {
               Matcher var21 = enabled.matcher(var16);
               if (var21.find()) {
                  var5 = var21.group(1);
                  var6 = var21.group(2);
               }
            } else if (var15.matches(".*\\bip\\b.*")) {
               var5 = compute(var16);
            }
         }
      }

      if (var5.isBlank() || var6.isBlank()) {
         Matcher var20 = enabled.matcher(var1);
         if (var20.find()) {
            var5 = var20.group(1);
            var6 = var20.group(2);
         }
      }

      if (var7.isBlank() && var8.isBlank() && !var5.isBlank() && var1.contains("@")) {
         var3 = mode.matcher(var1);
         if (var3.find()) {
            var7 = onTick(var3.group(1));
            var8 = onTick(var3.group(2));
         }
      }

      return new ProxyManager.SecondaryDataRecord(var2, compute(var5), update(var6), var7, var8);
   }

   public static String process(String var0) {
      String var1 = select(var0).trim().toLowerCase(Locale.ROOT).replace(" ", "");
      return var1.contains("4") ? "Socks4" : "Socks5";
   }

   private static String handle(String var0, String var1) {
      String var2 = select(var0).toLowerCase(Locale.ROOT).replace(" ", "");
      if (var2.contains("socks4") || var2.contains("sock4") || var2.contains("soock4")) {
         return "Socks4";
      } else {
         return !var2.contains("socks5") && !var2.contains("sock5") && !var2.contains("soock5") ? process(var1) : "Socks5";
      }
   }

   private static ProxyManager.DataRecord compute(ProxyManager.DataRecord var0) {
      return var0 == null
         ? new ProxyManager.DataRecord(false, "Socks5", "", "", "", "")
         : new ProxyManager.DataRecord(
            var0.enabled(), process(var0.type()), compute(var0.host()), update(var0.port()), select(var0.username()).trim(), select(var0.password())
         );
   }

   private static String compute(String var0) {
      String var1 = select(var0).trim();
      int var2 = var1.indexOf("://");
      if (var2 >= 0) {
         var1 = var1.substring(var2 + 3);
      }

      int var3 = var1.lastIndexOf(64);
      if (var3 >= 0 && var3 + 1 < var1.length()) {
         var1 = var1.substring(var3 + 1);
      }

      int var4 = var1.indexOf(47);
      if (var4 >= 0) {
         var1 = var1.substring(0, var4);
      }

      if (var1.startsWith("[")) {
         int var5 = var1.indexOf(93);
         if (var5 > 0) {
            return var1.substring(1, var5).trim();
         }
      }

      int var6 = var1.lastIndexOf(58);
      if (var6 > 0 && var1.indexOf(58) == var6 && apply(var1.substring(var6 + 1))) {
         var1 = var1.substring(0, var6);
      }

      return var1.trim();
   }

   private static boolean resolve(String var0) {
      String var1 = select(var0);
      if (var1.length() > 255) {
         return false;
      }

      for (int var2 = 0; var2 < var1.length(); var2++) {
         char var3 = var1.charAt(var2);
         if (!Character.isLetterOrDigit(var3) && var3 != '.' && var3 != '-' && var3 != '_' && var3 != ':') {
            return false;
         }
      }

      return true;
   }

   private static String update(String var0) {
      String var1 = select(var0).trim();
      StringBuilder var2 = new StringBuilder(5);

      for (int var3 = 0; var3 < var1.length() && var2.length() < 5; var3++) {
         char var4 = var1.charAt(var3);
         if (var4 >= '0' && var4 <= '9') {
            var2.append(var4);
         }
      }

      return var2.toString();
   }

   private static boolean apply(String var0) {
      String var1 = select(var0).trim();
      if (!var1.isEmpty() && var1.length() <= 5) {
         for (int var2 = 0; var2 < var1.length(); var2++) {
            char var3 = var1.charAt(var2);
            if (var3 < '0' || var3 > '9') {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static int execute(String var0) {
      try {
         int var1 = Integer.parseInt(select(var0).trim());
         return var1 > 0 && var1 <= 65535 ? var1 : -1;
      } catch (Throwable var2) {
         return -1;
      }
   }

   private static String prepare(String var0) {
      String var1 = select(var0).trim();
      return var1.isEmpty() ? null : var1;
   }

   private static String check(String var0) {
      int var1 = var0.indexOf(58);
      return var1 >= 0 && var1 + 1 < var0.length() ? var0.substring(var1 + 1).trim() : "";
   }

   private static String onTick(String var0) {
      String var1 = select(var0);

      try {
         return URLDecoder.decode(var1.replace("+", "%2B"), StandardCharsets.UTF_8);
      } catch (Throwable var3) {
         return var1;
      }
   }

   static String handle(Throwable var0) {
      for (Throwable var1 = var0; var1 != null; var1 = var1.getCause()) {
         String var2 = var1.getMessage();
         if (var2 != null && !var2.isBlank()) {
            String var3 = var2.replace('\n', ' ').replace('\r', ' ').trim();
            String var4 = var3.toLowerCase(Locale.ROOT);
            if (!var4.contains("authstatus") && !var4.contains("authentication")) {
               return var3;
            }

            return "SOCKS5 auth rejected: check login/password";
         }
      }

      return var0 == null ? "Unknown error" : var0.getClass().getSimpleName();
   }

   private static File update() {
      try {
         if (WildClient.instance != null && WildClient.instance.cache != null) {
            return new File(WildClient.instance.cache, "proxy.json");
         }
      } catch (Throwable var1) {
      }

      return new File(WildClient.process(), "proxy.json");
   }

   private static void handle(File var0, byte[] var1) throws Exception {
      Path var2 = var0.toPath();
      Path var3 = var2.getParent();
      if (var3 != null) {
         Files.createDirectories(var3);
      }

      Path var4 = var2.resolveSibling(var2.getFileName() + ".tmp");

      try (FileChannel var5 = FileChannel.open(var4, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
         ByteBuffer var6 = ByteBuffer.wrap(var1);

         while (var6.hasRemaining()) {
            var5.write(var6);
         }

         var5.force(true);
      }

      try {
         Files.move(var4, var2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException var9) {
         Files.move(var4, var2, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static String handle(JsonObject var0, String var1, String var2) {
      try {
         JsonElement var3 = var0.get(var1);
         return var3 != null && !var3.isJsonNull() ? var3.getAsString() : var2;
      } catch (Throwable var4) {
         return var2;
      }
   }

   private static boolean handle(JsonObject var0, String var1, boolean var2) {
      try {
         JsonElement var3 = var0.get(var1);
         return var3 != null && !var3.isJsonNull() ? var3.getAsBoolean() : var2;
      } catch (Throwable var4) {
         return var2;
      }
   }

   private static String select(String var0) {
      return var0 == null ? "" : var0;
   }

   public record DataRecord(boolean enabled, String type, String host, String port, String username, String password) {
      public boolean isSocks4() {
         return "Socks4".equals(ProxyManager.process(this.type));
      }

      public boolean isSocks5() {
         return !this.isSocks4();
      }

      public int portInt() {
         return ProxyManager.execute(this.port);
      }

      public ProxyManager.DataRecord withEnabled(boolean var1) {
         return new ProxyManager.DataRecord(var1, this.type, this.host, this.port, this.username, this.password);
      }
   }

   public record PrimaryDataRecord(boolean success, long millis, String message) {
   }

   public record SecondaryDataRecord(String type, String host, String port, String username, String password) {
      public static ProxyManager.SecondaryDataRecord empty() {
         return new ProxyManager.SecondaryDataRecord("Socks5", "", "", "", "");
      }

      public ProxyManager.SecondaryDataRecord withType(String var1) {
         return new ProxyManager.SecondaryDataRecord(ProxyManager.process(var1), this.host, this.port, this.username, this.password);
      }
   }
}
