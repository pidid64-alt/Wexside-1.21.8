package org.wild.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandlerInvoker;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.ScreenInputEvent;
import ru.wild.core.EventDispatchBoundary;
import ru.wild.network.MovementPacketTracker;
import ru.wild.network.ServerTickRateTracker;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
   @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
   private static <T extends PacketListener> void handlePacketPre(Packet<T> var0, PacketListener var1, CallbackInfo var2) {
      EventDispatchBoundary.handle();
      boolean var3 = wild$dispatchReceiveEvent(var0);
      wild$updateTps(var0);
      if (var3) {
         var2.cancel();
      }
   }

   private static <T extends PacketListener> boolean wild$dispatchReceiveEvent(Packet<T> var0) {
      try {
         PacketEvent var1 = new PacketEvent(var0, PacketEvent.Mode.RECEIVE);
         boolean var2 = ServerTickRateTracker.handle(var1);
         EventHandlerInvoker.handle(var1);
         if (var2) {
            WildClient.execute();
         }

         return var1.handle();
      } catch (Throwable var3) {
         return false;
      }
   }

   private static void wild$updateTps(Packet<?> var0) {
      MovementPacketTracker.process(var0);
   }

   @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
   private void sendPre(Packet<?> var1, CallbackInfo var2) {
      EventDispatchBoundary.handle();

      try {
         PacketEvent var3 = new PacketEvent(var1, PacketEvent.Mode.SEND);
         EventHandlerInvoker.handle(var3);
         if (var3.handle()) {
            var2.cancel();
            return;
         }
      } catch (Throwable var7) {
      }

      MovementPacketTracker.handle(var1);
      if (var1 instanceof CloseHandledScreenC2SPacket var8) {
         try {
            MinecraftClient var4 = MinecraftClient.getInstance();
            if (var4 != null) {
               ScreenInputEvent var5 = new ScreenInputEvent(var4.currentScreen, var8.getSyncId());
               EventHandlerInvoker.handle(var5);
               if (var5.handle()) {
                  var2.cancel();
               }
            }
         } catch (Throwable var6) {
         }
      }
   }
}
