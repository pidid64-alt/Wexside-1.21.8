package ru.wild.util.player;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import ru.wild.core.MinecraftContext;

public final class LocalhostHelper implements MinecraftContext {
   public static boolean handle() {
      return toggleState.player == null || toggleState.world == null;
   }

   public static boolean handle(double var0, double var2, double var4) {
      BlockPos var6 = BlockPos.ofFloored(var0, var2, var4);
      return toggleState.world.getBlockState(var6).isFullCube(toggleState.world, var6);
   }

   public static Block handle(BlockPos var0) {
      return toggleState.world.getBlockState(var0).getBlock();
   }

   public static float handle(float var0) {
      double var1 = toggleState.player.getX() - toggleState.player.lastRenderX;
      double var3 = toggleState.player.getZ() - toggleState.player.lastRenderZ;
      float var5 = (float)(var1 * var1 + var3 * var3);
      float var6 = toggleState.player.lastBodyYaw;
      float var7 = var6;
      if (var5 > 0.0025000002F) {
         var7 = (float)MathHelper.atan2(var3, var1) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (toggleState.player != null && toggleState.player.handSwingProgress > 0.0F) {
      }

      float var8 = MathHelper.wrapDegrees(var0 - (var6 + MathHelper.wrapDegrees(var7 - var6) * 0.3F));
      var8 = MathHelper.clamp(var8, -50.0F, 50.0F);
      var6 = var0 - var8;
      if (var8 * var8 > 2500.0F) {
         var6 += var8 * 0.2F;
      }

      return var6;
   }

   public static boolean process() {
      Box var0 = toggleState.player.getBoundingBox();
      BlockPos var1 = toggleState.player.getBlockPos();
      return process(var1).stream().anyMatch(var1x -> handle(var0, var1x));
   }

   private static boolean handle(Box var0, BlockPos var1) {
      if (!toggleState.world.getBlockState(var1).isOf(Blocks.COBWEB)) {
         return false;
      }

      Box var2 = new Box(var1);
      return var0.intersects(var2);
   }

   private static List<BlockPos> process(BlockPos var0) {
      ArrayList var1 = new ArrayList();

      for (int var2 = var0.getX() - 2; var2 <= var0.getX() + 2; var2++) {
         for (int var3 = var0.getY() - 1; var3 <= var0.getY() + 4; var3++) {
            for (int var4 = var0.getZ() - 2; var4 <= var0.getZ() + 2; var4++) {
               var1.add(new BlockPos(var2, var3, var4));
            }
         }
      }

      return var1;
   }

   public static List<BlockPos> handle(BlockPos var0, float var1, float var2) {
      ArrayList var3 = new ArrayList();
      int var4 = var0.getX();
      int var5 = var0.getY();
      int var6 = var0.getZ();

      for (int var7 = var4 - (int)var1; var7 <= var4 + (int)var1; var7++) {
         for (int var8 = var6 - (int)var1; var8 <= var6 + (int)var1; var8++) {
            for (int var9 = var5; var9 <= var5 + (int)var2; var9++) {
               var3.add(new BlockPos(var7, var9, var8));
            }
         }
      }

      return var3;
   }

   public static boolean handle(Block var0, BlockPos var1, float var2, float var3) {
      return handle(var1, var2, var3).stream().map(var0x -> toggleState.world.getBlockState(var0x).getBlock()).anyMatch(var1x -> var1x.equals(var0));
   }

   public static boolean handle(String var0) {
      if (var0 == null || var0.isEmpty()) {
         return false;
      }

      if (!handle() && toggleState.getNetworkHandler() != null) {
         String var1 = null;
         if (toggleState.getCurrentServerEntry() != null) {
            var1 = toggleState.getCurrentServerEntry().address;
         }

         if ((var1 == null || var1.isEmpty()) && toggleState.getNetworkHandler().getConnection() != null) {
            SocketAddress var2 = toggleState.getNetworkHandler().getConnection().getAddress();
            if (var2 != null) {
               var1 = var2.toString();
               if (var1.startsWith("/")) {
                  var1 = var1.substring(1);
               }
            }
         }

         if (toggleState.isConnectedToLocalServer()) {
            var1 = "localhost";
         }

         return var1 != null && !var1.isEmpty() ? var1.toLowerCase().contains(var0.toLowerCase()) : false;
      } else {
         return false;
      }
   }
   private LocalhostHelper() {
   }
}
