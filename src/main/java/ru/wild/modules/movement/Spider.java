package ru.wild.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerMoveEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.ModeSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.util.math.Stopwatch;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(
   name = "Spider",
   category = ModuleCategory.Movement,
   description = "Позволяет лазить по стенам",
   flags = {ModuleFlag.RISKY, ModuleFlag.MATRIX}
)
public class Spider extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "FunTime", "FunTime");
   private final Stopwatch target = new Stopwatch();
   private final Stopwatch pending = new Stopwatch();
   private final Stopwatch previous = new Stopwatch();
   private final Stopwatch latest = new Stopwatch();
   private final Stopwatch summary = new Stopwatch();
   private boolean matrixBlend = true;

   public Spider() {
      this.handle(this.source);
   }

   @Override
   public void process() {
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      if (this.source.process("SpookyTime") && Module.client.options != null) {
         Module.client.options.sneakKey.setPressed(false);
      }

      super.process();
   }

   @EventHandler
   public void handle(PlayerMoveEvent var1) {
      if (!ServerEnvironment.handle()) {
         boolean var2 = Module.client.player.horizontalCollision;
         boolean var3 = var2 && Module.client.options.jumpKey.isPressed();
         if (this.source.process("FunTime") || this.source.process("FunTimeNew")) {
            this.handle(var1, var3);
         }

         if (this.source.process("FunTimeNew") && var2) {
            this.render();
         }

         if (this.source.process("FunTime v2") && var3) {
            this.compute(var1);
         }

         if (this.source.process("FunTime v3") && var2) {
            this.process(var1);
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (this.source.process("SpookyTime")) {
         this.refresh();
      }
   }

   private void process(PlayerMoveEvent var1) {
      int var2 = this.handle(Items.SPRUCE_BUTTON);
      if (var2 != -1) {
         if (Module.client.player.isOnGround()) {
            if (this.latest.update(100L)) {
               Module.client.player.jump();
               this.latest.handle();
            }
         } else {
            if (Module.client.player.fallDistance > 0.0 && Module.client.player.fallDistance < 1.5) {
               var1.handle(true);
               Module.client.player.setOnGround(true);
               Module.client.player.verticalCollision = true;
               this.handle(var2);
               Module.client.player.jump();
               Module.client.player.fallDistance = 0.0;
            }
         }
      }
   }

   private void handle(int var1) {
      float var2 = Direction.getHorizontalDegreesOrThrow(Module.client.player.getHorizontalFacing());
      float var3 = 79.0F;
      RotationAngles var4 = new RotationAngles(var2, var3);
      RotationController.handle(var4, 360.0F, 360.0F, 10, 1);
      Vec3d var5 = Module.client.player.getCameraPosVec(1.0F);
      Vec3d var6 = this.handle(var3, var2);
      Vec3d var7 = var5.add(var6.x * 4.0, var6.y * 4.0, var6.z * 4.0);
      BlockHitResult var8 = Module.client.world.raycast(new RaycastContext(var5, var7, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      if (var8 != null && var8.getType() == Type.BLOCK) {
         this.handle(var8, var1);
      }
   }

   private void refresh() {
      if (Module.client.player != null && Module.client.world != null) {
         if (!Module.client.player.horizontalCollision) {
            if (Module.client.options.sneakKey.isPressed()) {
               Module.client.options.sneakKey.setPressed(false);
            }
         } else {
            int var1 = this.handle(Items.WATER_BUCKET);
            int var2 = this.handle(Items.BUCKET);
            if (var1 != -1 || var2 != -1) {
               if (Module.client.player.isOnGround()) {
                  Module.client.player.jump();
               } else {
                  RotationAngles var3 = new RotationAngles(Module.client.player.getYaw(), 78.0F);
                  RotationController.handle(var3, 20.0F, 100.0F, 4, 1);
                  if (this.matrixBlend) {
                     this.process(var1);
                     double var4 = 2.0 + Math.random() * 2.0;
                     Vec3d var6 = Module.client.player.getVelocity();
                     Module.client.player.setVelocity(var6.x, var4, var6.z);
                     this.matrixBlend = false;
                     this.summary.handle();
                  }

                  if (this.summary.update(200L)) {
                     if (Module.client.player.isTouchingWater()) {
                        Module.client.player.jump();
                        if (var2 != -1) {
                           this.process(var2);
                        }
                     } else if (var1 != -1) {
                        this.process(var1);
                     }

                     this.summary.handle();
                  }

                  Module.client.options.sneakKey.setPressed(true);
               }
            }
         }
      }
   }

   private void process(int var1) {
      int var2 = Module.client.player.getInventory().getSelectedSlot();
      if (var1 != var2) {
         Module.client.player.getInventory().setSelectedSlot(var1);
         Module.client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }

      Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      if (var1 != var2) {
         Module.client.player.getInventory().setSelectedSlot(var2);
         Module.client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var2));
      }
   }

   private void handle(PlayerMoveEvent var1, boolean var2) {
      BlockPos var3 = BlockPos.ofFloored(Module.client.player.getPos());
      BlockPos var4 = var3.offset(Module.client.player.getHorizontalFacing());
      if (var2 && (this.handle(var4) || this.handle(var3))) {
         var1.handle(true);
         Module.client.player.setOnGround(true);
         Module.client.player.jump();
         Module.client.player.fallDistance = 0.0;
         this.target.handle();
      }
   }

   private boolean handle(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      Block var3 = var2.getBlock();
      boolean var4 = var3 instanceof TrapdoorBlock && Boolean.TRUE.equals(var2.get(Properties.OPEN)) && var2.contains(Properties.HORIZONTAL_FACING);
      return var3 instanceof FenceBlock
         || var2.isIn(BlockTags.WALLS)
         || var3 instanceof FenceGateBlock
         || var3 instanceof LanternBlock
         || var3 instanceof LightningRodBlock
         || var4;
   }

   private void render() {
      int var1 = this.handle(Items.LIGHTNING_ROD);
      if (var1 != -1) {
         RotationAngles var2 = new RotationAngles(Module.client.player.getYaw(), 58.1F);
         RotationController.handle(var2, 80.0F, 80.0F, 10, 1);
         if (Math.abs(Module.client.player.getPitch() - 57.1F) < 2.0F && Module.client.crosshairTarget instanceof BlockHitResult var3) {
            BlockPos var5 = var3.getBlockPos();
            if (var3.getSide() == Direction.UP
               && !Module.client.world.getBlockState(var5).isReplaceable()
               && Module.client.world.getBlockState(var5.up()).isReplaceable()
               && this.pending.update(50L)) {
               this.handle(var3, var1);
               this.pending.handle();
            }
         }
      }
   }

   private void compute(PlayerMoveEvent var1) {
      if (this.previous.update(400L)) {
         var1.handle(true);
         Module.client.player.setOnGround(true);
         Module.client.player.verticalCollision = true;
         Module.client.player.horizontalCollision = true;
         Module.client.player.jump();
         this.previous.handle();
         int var2 = this.handle(Items.COOKIE);
         if (var2 != -1 && Module.client.player.fallDistance > 0.0 && Module.client.player.fallDistance < 1.5) {
            this.compute(var2);
         }
      }
   }

   private void compute(int var1) {
      float var2 = Direction.getHorizontalDegreesOrThrow(Module.client.player.getHorizontalFacing());
      float var3 = 80.0F;
      RotationAngles var4 = new RotationAngles(var2, var3);
      RotationController.handle(var4, 100.0F, 100.0F, 10, 1);
      Vec3d var5 = Module.client.player.getCameraPosVec(1.0F);
      Vec3d var6 = this.handle(var3, var2);
      Vec3d var7 = var5.add(var6.x * 4.0, var6.y * 4.0, var6.z * 4.0);
      BlockHitResult var8 = Module.client.world.raycast(new RaycastContext(var5, var7, ShapeType.OUTLINE, FluidHandling.NONE, Module.client.player));
      if (var8 != null && var8.getType() == Type.BLOCK) {
         this.handle(var8, var1);
         Module.client.player.fallDistance = 0.0;
      }
   }

   private void handle(BlockHitResult var1, int var2) {
      int var3 = Module.client.player.getInventory().getSelectedSlot();
      Module.client.player.getInventory().setSelectedSlot(var2);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var1);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      Module.client.player.getInventory().setSelectedSlot(var3);
   }

   private int handle(Item var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         ItemStack var3 = Module.client.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && var3.getItem() == var1) {
            return var2;
         }
      }

      return -1;
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
