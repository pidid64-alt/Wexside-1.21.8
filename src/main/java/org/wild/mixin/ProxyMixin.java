package org.wild.mixin;

import io.netty.channel.Channel;
import io.netty.handler.proxy.ProxyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.network.ProxyManager;

@Mixin(targets = "net/minecraft/network/ClientConnection$1")
public class ProxyMixin {
   @Inject(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At("HEAD"))
   private void connect(Channel var1, CallbackInfo var2) {
      try {
         ProxyHandler var3 = ProxyManager.resolve();
         if (var3 != null) {
            var1.pipeline().addFirst("wild_proxy", var3);
         }
      } catch (Throwable var4) {
      }
   }
}
