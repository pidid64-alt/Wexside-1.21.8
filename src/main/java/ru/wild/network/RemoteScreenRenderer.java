package ru.wild.network;

import java.util.List;
import java.util.UUID;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import ru.wild.render.TexturedQuadsRenderer;
import ru.wild.render.WorldQuadBuilder;
import ru.wild.render.WorldRenderContext;

public final class RemoteScreenRenderer {
   private final RemoteCameraState instance = new RemoteCameraState();
   private final RemoteScreenSession data;

   public RemoteScreenRenderer(RemoteScreenSession var1) {
      this.data = var1;
   }

   public RemoteScreenSession handle() {
      return this.data;
   }

   public void handle(
      WorldRenderContext var1, List<PartyRoster.Point3d> var2, PartyRoster var3, RemoteScreenViewport var4, UUID var5, boolean var6, long var7, int var9
   ) {
      this.handle(var1, var2, var4);
      this.handle(var1, var2, var3, var4, var9, var7);
      this.handle(var1, var2, var4, var5, var6, var9);
      this.process(var1, var2, var4, var5, var6, var9);
   }

   private void handle(WorldRenderContext var1, List<PartyRoster.Point3d> var2, RemoteScreenViewport var3) {
      WorldQuadBuilder var4 = handle(var1, TexturedQuadsRenderer.handle());

      for (int var5 = 0; var5 < var2.size(); var5++) {
         this.handle((PartyRoster.Point3d)var2.get(var5), var3);
         double var6 = this.instance.select();
         this.handle(
            var4,
            -this.instance.resolve() - var6,
            this.instance.resolve() + var6,
            -this.instance.update() - var6,
            this.instance.update() + var6,
            -0.014,
            -15592938
         );
      }
   }

   private void handle(WorldRenderContext var1, List<PartyRoster.Point3d> var2, PartyRoster var3, RemoteScreenViewport var4, int var5, long var6) {
      for (int var8 = 0; var8 < var2.size(); var8++) {
         PartyRoster.Point3d var9 = (PartyRoster.Point3d)var2.get(var8);
         this.handle(var9, var4);
         this.data.handle(var1, this.instance, var9, var3.handle(var9, var6), var5);
      }
   }

   private void handle(WorldRenderContext var1, List<PartyRoster.Point3d> var2, RemoteScreenViewport var3, UUID var4, boolean var5, int var6) {
      for (int var7 = 0; var7 < var2.size(); var7++) {
         PartyRoster.Point3d var8 = (PartyRoster.Point3d)var2.get(var7);
         RemoteFrameBuffer var9 = this.data.process(var8.id());
         if (var9 != null && RemoteScreenViewport.handle(var8, var4, var5) && var8.id().equals(var3.process())) {
            this.handle(var8, var3);
            WorldQuadBuilder var10 = handle(var1, TexturedQuadsRenderer.resolve());

            for (int var11 = 0; var11 <= var9.handle(); var11++) {
               boolean var12 = var11 == var9.handle();
               this.handle(var10, var11, !var12 && var11 == var9.process() ? var6 : -870704608);
            }

            for (int var13 = 0; var13 < var9.handle(); var13++) {
               Identifier var14 = var9.process(var13);
               if (var14 != null) {
                  this.handle(var1, var13, var14);
               }
            }

            this.handle(var10, var9.handle());
         }
      }
   }

   private void process(WorldRenderContext var1, List<PartyRoster.Point3d> var2, RemoteScreenViewport var3, UUID var4, boolean var5, int var6) {
      WorldQuadBuilder var7 = handle(var1, TexturedQuadsRenderer.resolve());

      for (int var8 = 0; var8 < var2.size(); var8++) {
         PartyRoster.Point3d var9 = (PartyRoster.Point3d)var2.get(var8);
         this.handle(var9, var3);
         if (RemoteScreenViewport.handle(var9, var4, var5) && handle(var3, var9.id())) {
            boolean var10 = handle(var3, var9.id(), RemoteViewportInteractionMode.MOVE);
            this.handle(
               var7,
               -this.instance.save(),
               this.instance.save(),
               this.instance.unload(),
               this.instance.submit(),
               0.012,
               RemoteScreenSessionImpl.handle(var10 ? var6 : -1, var10 ? 0.95F : 0.5F)
            );
         }
      }
   }

   private void handle(WorldQuadBuilder var1, int var2, int var3) {
      this.handle(
         var1,
         this.instance.handle(var2),
         this.instance.handle(var2) + this.instance.render(),
         this.instance.drawAnimation(),
         this.instance.encodePoint(),
         0.008,
         var3
      );
   }

   private void handle(WorldRenderContext var1, int var2, Identifier var3) {
      double var4 = this.instance.refresh() * 0.09;
      double var6 = this.instance.handle(var2) + var4;
      double var8 = this.instance.handle(var2) + this.instance.render() - var4;
      double var10 = this.instance.drawAnimation() + var4;
      double var12 = this.instance.encodePoint() - var4;
      handle(var1, TexturedQuadsRenderer.process(var3))
         .handle(
            this.instance.handle(var6, 0.011),
            this.instance.handle(var10),
            this.instance.process(var6, 0.011),
            this.instance.handle(var8, 0.011),
            this.instance.handle(var10),
            this.instance.process(var8, 0.011),
            this.instance.handle(var8, 0.011),
            this.instance.handle(var12),
            this.instance.process(var8, 0.011),
            this.instance.handle(var6, 0.011),
            this.instance.handle(var12),
            this.instance.process(var6, 0.011),
            0.0F,
            1.0F,
            1.0F,
            1.0F,
            1.0F,
            0.0F,
            0.0F,
            0.0F,
            -1
         );
   }

   private void handle(WorldQuadBuilder var1, int var2) {
      double var3 = this.instance.handle(var2) + this.instance.render() * 0.5;
      double var5 = this.instance.drawAnimation() + this.instance.refresh() * 0.5;
      double var7 = this.instance.refresh() * 0.22;
      double var9 = this.instance.refresh() * 0.055;
      this.handle(var1, var3 - var7, var3 + var7, var5 - var9, var5 + var9, 0.013, -855638017);
      this.handle(var1, var3 - var9, var3 + var9, var5 - var7, var5 + var7, 0.013, -855638017);
   }

   private static WorldQuadBuilder handle(WorldRenderContext var0, RenderLayer var1) {
      return new WorldQuadBuilder(var0, var0.process().peek(), var0.handle(var1));
   }

   private void handle(PartyRoster.Point3d var1, RemoteScreenViewport var2) {
      if (var2.handle(var1.id())) {
         this.instance.handle(var2.apply(), var2.execute(), var2.prepare(), var2.check(), var2.onTick(), var2.select());
      } else {
         this.instance.handle(var1);
      }
   }

   private void handle(WorldQuadBuilder var1, double var2, double var4, double var6, double var8, double var10, int var12) {
      var1.handle(
         this.instance.handle(var2, var10),
         this.instance.handle(var6),
         this.instance.process(var2, var10),
         this.instance.handle(var4, var10),
         this.instance.handle(var6),
         this.instance.process(var4, var10),
         this.instance.handle(var4, var10),
         this.instance.handle(var8),
         this.instance.process(var4, var10),
         this.instance.handle(var2, var10),
         this.instance.handle(var8),
         this.instance.process(var2, var10),
         var12
      );
   }

   private static boolean handle(RemoteScreenViewport var0, UUID var1) {
      return var0.resolve() ? var0.handle(var1) : var1.equals(var0.process());
   }

   private static boolean handle(RemoteScreenViewport var0, UUID var1, RemoteViewportInteractionMode var2) {
      return var0.resolve() ? var0.handle(var1) : var2 == var0.compute() && var1.equals(var0.handle());
   }
}
