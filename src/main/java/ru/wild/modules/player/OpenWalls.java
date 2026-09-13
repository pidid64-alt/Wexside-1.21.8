package ru.wild.modules.player;

import java.util.Optional;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MouseButtonEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.NumberSetting;

@ModuleRegister(name = "OpenWalls", category = ModuleCategory.Player, description = "Открывает контейнеры через стены")
public class OpenWalls extends Module {
   private final NumberSetting source = new NumberSetting("Дистанция", 4.6F, 2.0F, 6.0F, 0.1F, false);
   private long target;

   public OpenWalls() {
      this.handle(this.source);
   }

   @EventHandler
   public void handle(MouseButtonEvent var1) {
      if (!var1.check()) {
         if (var1.onTick() && var1.resolve() == 1) {
            if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null && Module.client.currentScreen == null) {
               if (System.currentTimeMillis() - this.target >= 120L) {
                  BlockPos var2 = this.refresh();
                  if (var2 != null) {
                     ActionResult var3 = this.handle(var2);
                     if (var3 != ActionResult.FAIL) {
                        this.target = System.currentTimeMillis();
                        var1.process();
                     }
                  }
               }
            }
         }
      }
   }

   private BlockPos refresh() {
      Vec3d var1 = Module.client.player.getEyePos();
      Vec3d var2 = Module.client.player.getRotationVec(1.0F).normalize();
      Vec3d var3 = var1.add(var2.multiply(this.source.compute()));
      ChunkPos var4 = Module.client.player.getChunkPos();
      int var5 = Math.max(1, (int)Math.ceil(this.source.compute() / 16.0F) + 1);
      BlockPos var6 = null;
      double var7 = Double.MAX_VALUE;

      for (int var9 = var4.x - var5; var9 <= var4.x + var5; var9++) {
         for (int var10 = var4.z - var5; var10 <= var4.z + var5; var10++) {
            WorldChunk var11 = Module.client.world.getChunk(var9, var10);
            if (var11 != null) {
               for (BlockEntity var13 : var11.getBlockEntities().values()) {
                  if (this.handle(var13)) {
                     BlockPos var14 = var13.getPos();
                     if (!(Module.client.player.squaredDistanceTo(Vec3d.ofCenter(var14)) > this.source.compute() * this.source.compute())) {
                        Optional var15 = new Box(var14).expand(0.01).raycast(var1, var3);
                        if (!var15.isEmpty()) {
                           double var16 = var1.squaredDistanceTo((Vec3d)var15.get());
                           if (var16 < var7) {
                              var7 = var16;
                              var6 = var14.toImmutable();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var6;
   }

   private ActionResult handle(BlockPos var1) {
      Direction var2 = this.process(var1);
      Vec3d var3 = new Vec3d(
         var1.getX() + 0.5 + var2.getOffsetX() * 0.5, var1.getY() + 0.5 + var2.getOffsetY() * 0.5, var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5
      );
      BlockHitResult var4 = new BlockHitResult(var3, var2, var1, false);
      ActionResult var5 = Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var4);
      if (var5 != ActionResult.FAIL) {
         Module.client.player.swingHand(Hand.MAIN_HAND);
      }

      return var5;
   }

   private Direction process(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      Vec3d var3 = Module.client.player.getEyePos().subtract(var2);
      return Direction.getFacing(var3.x, var3.y, var3.z);
   }

   private boolean handle(BlockEntity var1) {
      return var1 instanceof ChestBlockEntity
         || var1 instanceof BarrelBlockEntity
         || var1 instanceof EnderChestBlockEntity
         || var1 instanceof ShulkerBoxBlockEntity
         || var1 instanceof HopperBlockEntity
         || var1 instanceof DispenserBlockEntity
         || var1 instanceof DropperBlockEntity
         || var1 instanceof AbstractFurnaceBlockEntity;
   }
}
