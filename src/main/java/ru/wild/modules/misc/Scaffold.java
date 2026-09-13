package ru.wild.modules.misc;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.automation.combat.RotationEngine;
import ru.wild.core.ViewRotationCoordinator;

@ModuleRegister(name = "Scaffold", description = "Ставит блоки под себя, пойдет под сервера с мини играми", category = ModuleCategory.Misc)
public class Scaffold extends Module {
   private BlockPos source = null;
   private Direction target = null;

   @Override
   public void handle() {
      this.source = null;
      this.target = null;
      super.handle();
   }

   @Override
   public void process() {
      Module.client.options.leftKey.setPressed(false);
      Module.client.options.rightKey.setPressed(false);
      Module.client.options.sneakKey.setPressed(false);
      super.process();
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null) {
         this.refresh();
         this.render();
         if (this.source != null && this.target != null && this.tick()) {
            RotationEngine.handle(this.source, this.target);
            if (this.process(this.source, this.target)) {
               this.handle(this.source, this.target);
            }
         }
      }
   }

   private void refresh() {
      BlockPos var1 = BlockPos.ofFloored(Module.client.player.getPos().add(0.0, -1.0, 0.0));
      if (!Module.client.world.getBlockState(var1).isReplaceable()) {
         this.source = null;
         this.target = null;
      } else {
         Direction[] var2 = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

         for (Direction var6 : var2) {
            BlockPos var7 = var1.offset(var6);
            BlockState var8 = Module.client.world.getBlockState(var7);
            if (!var8.isReplaceable() && var8.getFluidState().isEmpty()) {
               this.source = var7;
               this.target = var6.getOpposite();
               return;
            }
         }
      }
   }

   private void handle(BlockPos var1, Direction var2) {
      Vec3d var3 = Module.client.player.getEyePos();
      double var4 = var1.getX() + 0.5 + var2.getOffsetX() * 0.5;
      double var6 = var1.getY() + 0.5 + var2.getOffsetY() * 0.5;
      double var8 = var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5;
      if (var2.getAxis() != Axis.X) {
         var4 = MathHelper.clamp(var3.x, var1.getX() + 0.15, var1.getX() + 0.85);
      }

      if (var2.getAxis() != Axis.Y) {
         var6 = MathHelper.clamp(var3.y - 1.2, var1.getY() + 0.15, var1.getY() + 0.85);
      }

      if (var2.getAxis() != Axis.Z) {
         var8 = MathHelper.clamp(var3.z, var1.getZ() + 0.15, var1.getZ() + 0.85);
      }

      Vec3d var10 = new Vec3d(var4, var6, var8);
      BlockHitResult var11 = new BlockHitResult(var10, var2, var1, false);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var11);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      this.source = null;
      this.target = null;
   }

   private boolean process(BlockPos var1, Direction var2) {
      float var3 = ViewRotationCoordinator.context;
      float var4 = ViewRotationCoordinator.config;
      Vec3d var5 = Module.client.player.getEyePos();
      Vec3d var6 = this.handle(var4, var3);
      double var7 = var1.getX() + 0.5 + var2.getOffsetX() * 0.5;
      double var9 = var1.getY() + 0.5 + var2.getOffsetY() * 0.5;
      double var11 = var1.getZ() + 0.5 + var2.getOffsetZ() * 0.5;
      if (var2.getAxis() != Axis.X) {
         var7 = MathHelper.clamp(var5.x, var1.getX() + 0.15, var1.getX() + 0.85);
      }

      if (var2.getAxis() != Axis.Y) {
         var9 = MathHelper.clamp(var5.y - 1.2, var1.getY() + 0.15, var1.getY() + 0.85);
      }

      if (var2.getAxis() != Axis.Z) {
         var11 = MathHelper.clamp(var5.z, var1.getZ() + 0.15, var1.getZ() + 0.85);
      }

      Vec3d var13 = new Vec3d(var7, var9, var11).subtract(var5).normalize();
      double var14 = var6.dotProduct(var13);
      return var14 > 0.95;
   }

   private void render() {
      if (Module.client.options.backKey.isPressed() && this.tick() && !Module.client.options.jumpKey.isPressed()) {
         Module.client.options.leftKey.setPressed(false);
         Module.client.options.rightKey.setPressed(false);
         BlockPos var1 = BlockPos.ofFloored(Module.client.player.getX(), Module.client.player.getY() - 0.5, Module.client.player.getZ());
         boolean var2 = Module.client.world.getBlockState(var1).isReplaceable();
         Module.client.options.sneakKey.setPressed(var2);
      } else {
         Module.client.options.leftKey.setPressed(Module.client.options.leftKey.isPressed());
         Module.client.options.rightKey.setPressed(Module.client.options.rightKey.isPressed());
         Module.client.options.sneakKey.setPressed(Module.client.options.sneakKey.isPressed());
      }
   }

   private boolean tick() {
      return Module.client.player.getMainHandStack().getItem() instanceof BlockItem || Module.client.player.getOffHandStack().getItem() instanceof BlockItem;
   }

   private Vec3d handle(float var1, float var2) {
      float var3 = var1 * (float) (Math.PI / 180.0);
      float var4 = -var2 * (float) (Math.PI / 180.0);
      float var5 = MathHelper.cos(var4);
      float var6 = MathHelper.sin(var4);
      float var7 = MathHelper.cos(var3);
      float var8 = MathHelper.sin(var3);
      return new Vec3d(var6 * var7, -var8, var5 * var7);
   }
}
