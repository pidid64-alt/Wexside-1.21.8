package ru.wild.modules.visuals;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.joml.Matrix4f;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.event.WorldRenderEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.render.WorldVertexBuffer;
import ru.wild.util.render.DualLayerBoxVertexEmitter;
import ru.wild.util.text.ChatLogger;

@ModuleRegister(name = "AncientXray", category = ModuleCategory.Visuals, description = "Поиск обломков после взрыва ТНТ", flags = ModuleFlag.VIP)
public class AncientXray extends Module {
   private final Set<BlockPos> source = ConcurrentHashMap.newKeySet();
   private final Set<BlockPos> target = ConcurrentHashMap.newKeySet();
   private final List<AncientXray.State> pending = new ArrayList<>();
   private static final int previous = 28;
   private static final int[] latest = new int[]{4, 10, 20, 40};
   private long summary = 0L;
   private static final int matrixBlend = 4096;
   private static final RenderPipeline vectorMatch = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("wild", "block_esp_box"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.LIGHTNING)
         .build()
   );
   private static final RenderLayer itemProject = RenderLayer.of("block_esp_box", 4096, false, true, vectorMatch, MultiPhaseParameters.builder().build(false));

   @Override
   public void handle() {
      super.handle();
      this.source.clear();
      this.target.clear();
      this.pending.clear();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         this.refresh();
      }
   }

   public void refresh() {
      if (Module.client.player != null && Module.client.world != null) {
         Iterator var1 = this.pending.iterator();

         while (var1.hasNext()) {
            AncientXray.State var2 = (AncientXray.State)var1.next();
            var2.data--;
            if (var2.data <= 0) {
               this.process(var2.instance, 28);
               var1.remove();
            }
         }

         if (System.currentTimeMillis() - this.summary > 50L) {
            for (BlockPos var3 : this.source) {
               if (!this.target.contains(var3)) {
                  this.target.add(var3);
                  this.resolve(var3);
                  this.summary = System.currentTimeMillis();
                  break;
               }
            }
         }
      }
   }

   public void render() {
      this.source.clear();
      this.target.clear();
      this.pending.clear();
   }

   public void handle(BlockPos var1, int var2) {
      if (var1 != null) {
         this.pending.add(new AncientXray.State(var1.toImmutable(), var2));
      }
   }

   public void handle(BlockPos var1, Block var2) {
      this.process(var1, var2);
   }

   public List<BlockPos> tick() {
      return new ArrayList<>(this.source);
   }

   public void handle(BlockPos var1) {
      this.source.remove(var1);
      this.target.remove(var1);
   }

   public boolean process(BlockPos var1) {
      return this.source.contains(var1);
   }

   public boolean compute(BlockPos var1) {
      return this.update(var1);
   }

   private void resolve(BlockPos var1) {
      if (Module.client.getNetworkHandler() != null) {
         Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, Direction.UP));
         Module.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, var1, Direction.UP));
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (Module.client.world != null) {
         if (var1.resolve() instanceof ExplosionS2CPacket var2) {
            BlockPos var10 = BlockPos.ofFloored(var2.center());

            for (int var9 : latest) {
               this.handle(var10, var9);
            }
         } else if (var1.resolve() instanceof BlockUpdateS2CPacket var3) {
            this.process(var3.getPos(), var3.getState().getBlock());
         } else if (var1.resolve() instanceof ChunkDeltaUpdateS2CPacket var4) {
            var4.visitUpdates((var1x, var2x) -> this.process(var1x, var2x.getBlock()));
         }
      }
   }

   private void process(BlockPos var1, Block var2) {
      BlockPos var3 = var1.toImmutable();
      if (var2 == Blocks.ANCIENT_DEBRIS) {
         if (this.update(var3) && this.source.add(var3)) {
            ChatLogger.handle("§6[AncientXray] §fОбломок найден §e" + var3.toShortString());
         }
      } else {
         this.source.remove(var3);
         this.target.remove(var3);
      }
   }

   private void process(BlockPos var1, int var2) {
      if (Module.client.world != null) {
         Mutable var3 = new Mutable();

         for (int var4 = -var2; var4 <= var2; var4++) {
            for (int var5 = -var2; var5 <= var2; var5++) {
               for (int var6 = -var2; var6 <= var2; var6++) {
                  var3.set(var1.getX() + var4, var1.getY() + var5, var1.getZ() + var6);
                  if (this.update(var3)) {
                     BlockPos var7 = var3.toImmutable();
                     if (this.source.add(var7)) {
                        ChatLogger.handle("§fОбнаружен обломок: §e" + var7.toShortString());
                     }
                  }
               }
            }
         }
      }
   }

   private boolean update(BlockPos var1) {
      if (Module.client.world == null) {
         return false;
      }

      Block var2 = Module.client.world.getBlockState(var1).getBlock();
      return var2 == Blocks.ANCIENT_DEBRIS && this.apply(var1) && !this.execute(var1) && this.prepare(var1) && !this.check(var1);
   }

   private boolean apply(BlockPos var1) {
      int var2 = 0;

      for (Direction var6 : Direction.values()) {
         Block var7 = Module.client.world.getBlockState(var1.offset(var6)).getBlock();
         if (var7 == Blocks.AIR || var7 == Blocks.LAVA || var7 == Blocks.CAVE_AIR) {
            if (++var2 >= 2) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean execute(BlockPos var1) {
      int var2 = 0;

      for (int var3 = -1; var3 <= 1; var3++) {
         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = -1; var5 <= 1; var5++) {
               Block var6 = Module.client.world.getBlockState(var1.add(var3, var4, var5)).getBlock();
               if (var6 == Blocks.NETHER_QUARTZ_ORE || var6 == Blocks.NETHER_GOLD_ORE) {
                  if (++var2 >= 4) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean prepare(BlockPos var1) {
      int var2 = 0;

      for (int var3 = -1; var3 <= 1; var3++) {
         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = -1; var5 <= 1; var5++) {
               Block var6 = Module.client.world.getBlockState(var1.add(var3, var4, var5)).getBlock();
               if (var6 == Blocks.AIR || var6 == Blocks.LAVA || var6 == Blocks.CAVE_AIR) {
                  if (++var2 >= 4) {
                     return true;
                  }
               }
            }
         }
      }

      return var2 >= 4;
   }

   private boolean check(BlockPos var1) {
      int var2 = 0;

      for (int var3 = -3; var3 <= 2; var3++) {
         for (int var4 = -2; var4 <= 2; var4++) {
            for (int var5 = -2; var5 <= 3; var5++) {
               if (Module.client.world.getBlockState(var1.add(var3, var4, var5)).getBlock() == Blocks.ANCIENT_DEBRIS) {
                  if (++var2 > 6) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   @EventHandler
   public void handle(WorldRenderEvent var1) {
      if (Module.client.world != null && Module.client.player != null && !this.source.isEmpty()) {
         Immediate var2 = WorldVertexBuffer.handle();

         try {
            Vec3d var3 = Module.client.gameRenderer.getCamera().getPos();
            Matrix4f var4 = var1.compute().peek().getPositionMatrix();
            int var5 = -2147418368;
            VertexConsumer var6 = var2.getBuffer(itemProject);

            for (BlockPos var8 : this.source) {
               if (!Module.client.world.getBlockState(var8).isOf(Blocks.ANCIENT_DEBRIS)) {
                  this.source.remove(var8);
               } else {
                  float var9 = (float)(var8.getX() - var3.x);
                  float var10 = (float)(var8.getY() - var3.y);
                  float var11 = (float)(var8.getZ() - var3.z);
                  float var12 = var9 + 1.0F;
                  float var13 = var10 + 1.0F;
                  float var14 = var11 + 1.0F;
                  DualLayerBoxVertexEmitter.handle(var6, var4, var9, var10, var11, var12, var13, var14, var5);
               }
            }
         } finally {
            WorldVertexBuffer.process();
         }
      }
   }

   static class State {
      BlockPos instance;
      int data;

      State(BlockPos var1, int var2) {
         this.instance = var1;
         this.data = var2;
      }
   }
}
