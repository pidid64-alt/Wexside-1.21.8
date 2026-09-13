package ru.wild.network;

import java.util.List;
import java.util.UUID;
import ru.wild.render.TexturedQuadsRenderer;
import ru.wild.render.WorldQuadBuilder;
import ru.wild.render.WorldRenderContext;

public final class RemoteScreenSessionImpl implements RemoteScreenSession {
   @Override
   public void handle(List<PartyRoster.Point3d> var1) {
   }

   @Override
   public RemoteInputSink handle(UUID var1) {
      return null;
   }

   @Override
   public RemoteFrameBuffer process(UUID var1) {
      return null;
   }

   @Override
   public void handle(WorldRenderContext var1, RemoteCameraState var2, PartyRoster.Point3d var3, long var4, int var6) {
      WorldQuadBuilder var7 = new WorldQuadBuilder(var1, var1.process().peek(), var1.handle(TexturedQuadsRenderer.resolve()));
      this.handle(var7, var2, -var2.resolve(), var2.resolve(), -var2.update(), var2.update(), 0.0, -15987696);
      float var8 = 0.25F + 0.35F * (float)Math.abs(Math.sin(System.currentTimeMillis() / 600.0));
      double var9 = var2.resolve() * 0.06;
      double var11 = Math.min(var9 * 0.22, var2.update() * 0.03);
      this.handle(var7, var2, -var9, var9, -var11, var11, 0.002, handle(var6, var8));
   }

   @Override
   public void handle() {
   }

   static int handle(int var0, float var1) {
      int var2 = Math.round(Math.max(0.0F, Math.min(1.0F, var1)) * (var0 >>> 24 & 0xFF));
      return var2 << 24 | var0 & 16777215;
   }

   private void handle(WorldQuadBuilder var1, RemoteCameraState var2, double var3, double var5, double var7, double var9, double var11, int var13) {
      var1.handle(
         var2.handle(var3, var11),
         var2.handle(var7),
         var2.process(var3, var11),
         var2.handle(var5, var11),
         var2.handle(var7),
         var2.process(var5, var11),
         var2.handle(var5, var11),
         var2.handle(var9),
         var2.process(var5, var11),
         var2.handle(var3, var11),
         var2.handle(var9),
         var2.process(var3, var11),
         var13
      );
   }
}
