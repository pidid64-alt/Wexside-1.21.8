package ru.wild.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class PartySocketClient {
   private final Queue<String> instance = new ConcurrentLinkedQueue<>();
   volatile PartyConnectionState data = PartyConnectionState.OFFLINE;
   volatile Channel context;
   volatile boolean config = true;
   volatile String state = "";
   private EventLoopGroup cache;
   private PartyHostConfig output;
   PartyIdentity current;
   long active = 1000L;

   public void handle(PartyHostConfig var1, PartyIdentity var2) {
      this.handle();
      this.output = var1;
      this.current = var2;
      this.config = false;
      this.active = 1000L;
      this.state = "";
      this.cache = new NioEventLoopGroup(1, onTick());
      this.prepare();
   }

   public void handle() {
      this.config = true;
      this.data = PartyConnectionState.OFFLINE;
      Channel var1 = this.context;
      this.context = null;
      if (var1 != null) {
         var1.close();
      }

      if (this.cache != null) {
         this.cache.shutdownGracefully(0L, 200L, TimeUnit.MILLISECONDS);
         this.cache = null;
      }

      this.instance.clear();
   }

   public boolean handle(String var1) {
      Channel var2 = this.context;
      if (var2 != null && var2.isActive()) {
         var2.writeAndFlush(Utf8FrameDecoder.handle(var2.alloc().buffer(), var1));
         return true;
      } else {
         return false;
      }
   }

   public String process() {
      return this.instance.poll();
   }

   public PartyConnectionState compute() {
      return this.data;
   }

   public boolean resolve() {
      return this.data == PartyConnectionState.ONLINE;
   }

   public String update() {
      return this.state;
   }

   public PartyHostConfig apply() {
      return this.output;
   }

   public PartyIdentity execute() {
      return this.current;
   }

   private void prepare() {
      if (!this.config && this.cache != null) {
         this.data = PartyConnectionState.CONNECTING;
         ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(this.cache)).channel(NioSocketChannel.class))
                     .option(ChannelOption.TCP_NODELAY, true))
                  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000))
               .handler(new ChannelInitializer<SocketChannel>() {
                  protected void initChannel(SocketChannel var1) {
                     var1.pipeline().addLast(new ChannelHandler[]{new Utf8FrameDecoder()}).addLast(new ChannelHandler[]{PartySocketClient.this.new State()});
                  }
               }))
            .connect(this.output.process(), this.output.compute())
            .addListener(var1 -> {
               if (!var1.isSuccess()) {
                  this.state = handle(var1.cause());
                  this.check();
               }
            });
      }
   }

   void check() {
      if (!this.config && this.cache != null) {
         this.data = PartyConnectionState.RETRYING;
         long var1 = this.active;
         this.active = Math.min(30000L, this.active * 2L);
         this.cache.schedule(this::prepare, var1, TimeUnit.MILLISECONDS);
      } else {
         this.data = PartyConnectionState.OFFLINE;
      }
   }

   void process(String var1) {
      while (this.instance.size() >= 512) {
         this.instance.poll();
      }

      this.instance.add(var1);
   }

   static String handle(Throwable var0) {
      if (var0 == null) {
         return "";
      }

      String var1 = var0.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : var0.getClass().getSimpleName();
   }

   private static ThreadFactory onTick() {
      return var0 -> {
         Thread var1 = new Thread(var0, "Wild-Net");
         var1.setDaemon(true);
         return var1;
      };
   }

   final class State extends SimpleChannelInboundHandler<String> {
      public void channelActive(ChannelHandlerContext var1) {
         PartySocketClient.this.context = var1.channel();
         PartySocketClient.this.data = PartyConnectionState.ONLINE;
         PartySocketClient.this.active = 1000L;
         PartySocketClient.this.state = "";
         var1.writeAndFlush(Utf8FrameDecoder.handle(var1.alloc().buffer(), PartyProtocol.handle(PartySocketClient.this.current)));
      }

      protected void channelRead0(ChannelHandlerContext var1, String var2) {
         PartySocketClient.this.process(var2);
      }

      public void channelInactive(ChannelHandlerContext var1) {
         PartySocketClient.this.context = null;
         if (PartySocketClient.this.config) {
            PartySocketClient.this.data = PartyConnectionState.OFFLINE;
         } else {
            PartySocketClient.this.check();
         }
      }

      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
         PartySocketClient.this.state = PartySocketClient.handle(var2);
         var1.close();
      }
   }
}
