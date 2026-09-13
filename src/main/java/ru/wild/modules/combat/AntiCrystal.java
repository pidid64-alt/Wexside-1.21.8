package ru.wild.modules.combat;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.module.ModuleRoles;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.world.ServerEnvironment;

@ModuleRoles(compute = "lichoday")
@ModuleRegister(
   name = "AntiCrystal",
   category = ModuleCategory.Combat,
   description = "Перекрывает блоками опасные базы под кристаллы рядом с вами",
   flags = ModuleFlag.RISKY
)
public class AntiCrystal extends Module {
   private final NumberSetting source = new NumberSetting("Radius", 4.2F, 2.0F, 6.0F, 0.1F, false);
   private final NumberSetting target = new NumberSetting("Delay", 55.0F, 0.0F, 220.0F, 5.0F, false);
   private final NumberSetting pending = new NumberSetting("Reaction", 25.0F, 0.0F, 150.0F, 5.0F, false);
   private final NumberSetting previous = new NumberSetting("Yaw Speed", 180.0F, 45.0F, 360.0F, 5.0F, false);
   private final NumberSetting latest = new NumberSetting("Pitch Speed", 170.0F, 45.0F, 360.0F, 5.0F, false);
   private final BooleanSetting summary = new BooleanSetting("Inventory Swap", true);
   private final BooleanSetting matrixBlend = new BooleanSetting("Restore Slot", false);
   private final BooleanSetting vectorMatch = new BooleanSetting("Packet Trigger", true);
   private final BooleanSetting itemProject = new BooleanSetting("Rescan", true);
   private final ArrayDeque<AntiCrystal.Snapshot> responseCompute = new ArrayDeque<>();
   private final Set<Long> providerFetch = new HashSet<>();
   private long profileDraw;

   public AntiCrystal() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend, this.vectorMatch, this.itemProject);
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (this.vectorMatch.compute() && !ServerEnvironment.handle() && var1 != null && var1.update().equals(PacketEvent.Mode.RECEIVE)) {
         if (var1.resolve() instanceof BlockUpdateS2CPacket var2) {
            this.handle(var2.getPos(), var2.getState());
         } else if (var1.resolve() instanceof ChunkDeltaUpdateS2CPacket var3) {
            var3.visitUpdates(this::handle);
         }
      }
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle() && Module.client.interactionManager != null && Module.client.getNetworkHandler() != null) {
         if (this.itemProject.compute()) {
            this.refresh();
         }

         this.tick();
         long var2 = System.currentTimeMillis();
         if (!((float)(var2 - this.profileDraw) < this.target.compute())) {
            AntiCrystal.Snapshot var4 = this.handle(var2);
            if (var4 != null) {
               int var5 = this.render();
               if (var5 >= 0) {
                  if (this.handle(var4.base(), var5)) {
                     this.profileDraw = var2;
                     this.providerFetch.remove(var4.base().asLong());
                     this.responseCompute.remove(var4);
                  }
               }
            }
         }
      } else {
         this.drawAnimation();
      }
   }

   @Override
   public void process() {
      this.drawAnimation();
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      super.process();
   }

   private void handle(BlockPos var1, BlockState var2) {
      if (var1 != null && var2 != null && this.handle(var2.getBlock()) && this.compute(var1)) {
         this.handle(var1);
      }
   }

   private void refresh() {
      BlockPos var1 = Module.client.player.getBlockPos();
      int var2 = MathHelper.ceil(this.source.compute());

      for (int var3 = -var2; var3 <= var2; var3++) {
         for (int var4 = -2; var4 <= 2; var4++) {
            for (int var5 = -var2; var5 <= var2; var5++) {
               BlockPos var6 = var1.add(var3, var4, var5);
               if (this.compute(var6) && this.handle(Module.client.world.getBlockState(var6).getBlock())) {
                  this.handle(var6);
               }
            }
         }
      }
   }

   private void handle(BlockPos var1) {
      if (this.resolve(var1)) {
         long var2 = var1.asLong();
         if (this.providerFetch.add(var2)) {
            this.responseCompute.addLast(new AntiCrystal.Snapshot(var1.toImmutable(), System.currentTimeMillis()));
         }
      }
   }

   private AntiCrystal.Snapshot handle(long var1) {
      AntiCrystal.Snapshot var3 = null;
      double var4 = Double.MAX_VALUE;

      for (AntiCrystal.Snapshot var7 : this.responseCompute) {
         if (!((float)(var1 - var7.createdAt()) < this.pending.compute()) && this.resolve(var7.base())) {
            double var8 = this.process(var7.base());
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private double process(BlockPos var1) {
      Vec3d var2 = var1.toCenterPos();
      Vec3d var3 = Module.client.player.getPos();
      Vec3d var4 = var2.subtract(var3);
      double var5 = var4.length();
      Vec3d var7 = Module.client.player.getVelocity();
      double var8 = 0.0;
      if (var7.horizontalLengthSquared() > 1.0E-5 && var4.horizontalLengthSquared() > 1.0E-5) {
         var8 = -var7.normalize().dotProduct(new Vec3d(var4.x, 0.0, var4.z).normalize()) * 0.42;
      }

      Vec3d var10 = Module.client.player.getRotationVec(1.0F);
      double var11 = var4.lengthSquared() <= 1.0E-5 ? 0.0 : -var10.normalize().dotProduct(var4.normalize()) * 0.22;
      double var13 = Math.abs(var2.y - Module.client.player.getY()) * 0.18;
      return var5 + var8 + var11 + var13;
   }

   private boolean compute(BlockPos var1) {
      return var1 != null && Module.client.player != null
         ? Module.client.player.squaredDistanceTo(var1.toCenterPos()) <= this.source.compute() * this.source.compute()
         : false;
   }

   private boolean resolve(BlockPos var1) {
      if (var1 != null && Module.client.world != null) {
         BlockState var2 = Module.client.world.getBlockState(var1);
         BlockPos var3 = var1.up();
         return this.handle(var2.getBlock())
            && Module.client.world.getBlockState(var3).isAir()
            && Module.client.world.getOtherEntities(null, Box.of(var3.toCenterPos(), 0.86, 0.86, 0.86)).isEmpty();
      } else {
         return false;
      }
   }

   private boolean handle(BlockPos var1, int var2) {
      int var3 = Module.client.player.getInventory().getSelectedSlot();
      int var4 = this.handle(var2, var3);
      if (var4 < 0) {
         return false;
      }

      this.handle(var4);
      Vec3d var5 = var1.toCenterPos().add(0.0, 0.5, 0.0);
      this.handle(var5);
      BlockHitResult var6 = new BlockHitResult(var5, Direction.UP, var1, false);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var6);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      if (this.matrixBlend.compute() && var3 != var4) {
         this.handle(var3);
      }

      return true;
   }

   private int handle(int var1, int var2) {
      if (var1 >= 0 && var1 < 9) {
         return var1;
      } else if (this.summary.compute() && var1 >= 9 && var1 <= 35) {
         int var3 = var2 >= 0 && var2 < 9 ? var2 : 0;
         int var4 = var1;
         Module.client.interactionManager.clickSlot(Module.client.player.currentScreenHandler.syncId, var4, var3, SlotActionType.SWAP, Module.client.player);
         return var3;
      } else {
         return -1;
      }
   }

   private void handle(int var1) {
      if (var1 >= 0 && var1 <= 8 && var1 != Module.client.player.getInventory().getSelectedSlot()) {
         Module.client.player.getInventory().setSelectedSlot(var1);
         Module.client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   private int render() {
      int var1 = -1;
      int var2 = Integer.MIN_VALUE;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = Module.client.player.getInventory().getStack(var3);
         if (this.handle(var4)) {
            int var5 = this.handle(var4, var3);
            if (var5 > var2) {
               var2 = var5;
               var1 = var3;
            }
         }
      }

      return var1;
   }

   private boolean handle(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && var1.getItem() instanceof BlockItem var2) {
         Block var5 = var2.getBlock();
         if (var5 != Blocks.AIR && var5 != Blocks.SAND && var5 != Blocks.RED_SAND && var5 != Blocks.GRAVEL && var5 != Blocks.ANVIL) {
            BlockState var4 = var5.getDefaultState();
            return var4.isSolidBlock(Module.client.world, BlockPos.ORIGIN);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int handle(ItemStack var1, int var2) {
      Block var3 = ((BlockItem)var1.getItem()).getBlock();
      int var4 = var2 < 9 ? 1000 : 0;
      if (var3 == Blocks.OBSIDIAN) {
         var4 += 80;
      } else if (var3 == Blocks.COBBLESTONE || var3 == Blocks.STONE || var3 == Blocks.DEEPSLATE) {
         var4 += 65;
      } else if (var3 == Blocks.NETHERRACK || var3 == Blocks.DIRT) {
         var4 += 40;
      }

      return var4 + Math.min(64, var1.getCount());
   }

   private void handle(Vec3d var1) {
      Vec3d var2 = var1.subtract(Module.client.player.getEyePos());
      float var3 = (float)Math.toDegrees(Math.atan2(-var2.x, var2.z));
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))));
      RotationController.handle(
         new RotationAngles(var3, MathHelper.clamp(var4, -90.0F, 90.0F)),
         this.previous.compute(),
         this.latest.compute(),
         this.previous.compute(),
         this.latest.compute(),
         2,
         16,
         false
      );
   }

   private boolean handle(Block var1) {
      return var1 == Blocks.OBSIDIAN || var1 == Blocks.BEDROCK;
   }

   private void tick() {
      Iterator var1 = this.responseCompute.iterator();
      long var2 = System.currentTimeMillis();

      while (var1.hasNext()) {
         AntiCrystal.Snapshot var4 = (AntiCrystal.Snapshot)var1.next();
         if (var2 - var4.createdAt() > 2500L || !this.resolve(var4.base())) {
            this.providerFetch.remove(var4.base().asLong());
            var1.remove();
         }
      }
   }

   private void drawAnimation() {
      this.responseCompute.clear();
      this.providerFetch.clear();
   }

   record Snapshot(BlockPos base, long createdAt) {
   }
}
