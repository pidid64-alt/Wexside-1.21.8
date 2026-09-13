package ru.wild.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Utf8FrameDecoder extends ByteToMessageDecoder {
   static final int instance = 8192;

   static ByteBuf handle(ByteBuf var0, String var1) {
      byte[] var2 = var1.getBytes(StandardCharsets.UTF_8);
      var0.writeInt(var2.length);
      var0.writeBytes(var2);
      return var0;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      if (var2.readableBytes() >= 4) {
         var2.markReaderIndex();
         int var4 = var2.readInt();
         if (var4 > 0 && var4 <= 8192) {
            if (var2.readableBytes() < var4) {
               var2.resetReaderIndex();
            } else {
               byte[] var5 = new byte[var4];
               var2.readBytes(var5);
               var3.add(new String(var5, StandardCharsets.UTF_8));
            }
         } else {
            var1.close();
         }
      }
   }
}
