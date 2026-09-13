package ru.wild.modules.combat;

import java.util.Comparator;
import java.util.stream.StreamSupport;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.util.math.ResettableTimer;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(
   name = "AutoCristal",
   category = ModuleCategory.Combat,
   description = "Автоматическая установка и взрыв кристаллов",
   flags = {ModuleFlag.RISKY, ModuleFlag.VIP}
)
public class AutoCristal extends Module {
   public final ModeSetting source = new ModeSetting("Режим", "Авто", "Авто", "Недоразвитый");
   public final NumberSetting target = new NumberSetting("Скорость", 50.0F, 0.0F, 1000.0F, 10.0F, false);
   public final NumberSetting pending = new NumberSetting("Радиус поиска", 4.0F, 1.0F, 6.0F, 1.0F, false);
   public final ResettableTimer previous = new ResettableTimer();

   public AutoCristal() {
      this.handle(this.source, this.target, this.pending);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle()) {
         if (this.previous.process((int)this.target.compute())) {
            int var2 = this.handle(Items.END_CRYSTAL);
            int var3 = this.handle(Items.OBSIDIAN);
            EndCrystalEntity var4 = this.refresh();
            if (var4 != null) {
               this.handle(var4.getPos());
               Module.client.interactionManager.attackEntity(Module.client.player, var4);
               Module.client.player.swingHand(Hand.MAIN_HAND);
               this.previous.handle();
            } else if (var2 != -1) {
               BlockPos var5 = this.render();
               if (var5 != null) {
                  this.handle(var5.toCenterPos().add(0.0, 0.5, 0.0));
                  this.handle(var5, var2, Direction.UP);
                  this.previous.handle();
               } else {
                  if (this.source.process("Авто") && var3 != -1) {
                     BlockPos var6 = this.tick();
                     if (var6 != null) {
                        this.handle(var6.toCenterPos());
                        this.handle(var6.down(), var3, Direction.UP);
                        this.previous.handle();
                     }
                  }
               }
            }
         }
      }
   }

   private void handle(BlockPos var1, int var2, Direction var3) {
      this.handle(var2);
      Vec3d var4 = var1.toCenterPos().add(var3.getOffsetX() * 0.5, var3.getOffsetY() * 0.5, var3.getOffsetZ() * 0.5);
      BlockHitResult var5 = new BlockHitResult(var4, var3, var1, false);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, var5);
      Module.client.player.swingHand(Hand.MAIN_HAND);
   }

   private EndCrystalEntity refresh() {
      double var1 = this.pending.compute();
      return StreamSupport.<Entity>stream(Module.client.world.getEntities().spliterator(), false)
         .filter(var0 -> var0 instanceof EndCrystalEntity)
         .map(var0 -> (EndCrystalEntity)var0)
         .filter(var2 -> Module.client.player.distanceTo(var2) <= var1)
         .min(Comparator.comparingDouble(var0 -> Module.client.player.distanceTo(var0)))
         .orElse(null);
   }

   private BlockPos render() {
      double var1 = this.pending.compute();
      BlockPos var3 = Module.client.player.getBlockPos();
      int var4 = (int)var1;

      for (int var5 = -var4; var5 <= var4; var5++) {
         for (int var6 = -var4; var6 <= var4; var6++) {
            for (int var7 = -var4; var7 <= var4; var7++) {
               BlockPos var8 = var3.add(var5, var6, var7);
               if (!(Module.client.player.squaredDistanceTo(var8.toCenterPos()) > var1 * var1)
                  && (Module.client.world.getBlockState(var8).isOf(Blocks.OBSIDIAN) || Module.client.world.getBlockState(var8).isOf(Blocks.BEDROCK))
                  && Module.client.world.isAir(var8.up())
                  && Module.client.world.getOtherEntities(null, new Box(var8.up())).isEmpty()) {
                  return var8;
               }
            }
         }
      }

      return null;
   }

   private BlockPos tick() {
      double var1 = this.pending.compute();
      BlockPos var3 = Module.client.player.getBlockPos();
      int var4 = (int)var1;

      for (int var5 = -var4; var5 <= var4; var5++) {
         for (int var6 = -var4; var6 <= var4; var6++) {
            for (int var7 = -var4; var7 <= var4; var7++) {
               BlockPos var8 = var3.add(var5, var6, var7);
               if (!(Module.client.player.squaredDistanceTo(var8.toCenterPos()) > var1 * var1)
                  && Module.client.world.isAir(var8)
                  && Module.client.world.getBlockState(var8.down()).isSolidBlock(Module.client.world, var8.down())
                  && Module.client.world.isAir(var8.up())
                  && Module.client.world.getOtherEntities(null, new Box(var8)).isEmpty()
                  && Module.client.world.getOtherEntities(null, new Box(var8.up())).isEmpty()) {
                  return var8;
               }
            }
         }
      }

      return null;
   }

   private int handle(Item var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (Module.client.player.getInventory().getStack(var2).isOf(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private void handle(int var1) {
      if (var1 != Module.client.player.getInventory().getSelectedSlot() && var1 >= 0 && var1 < 9) {
         Module.client.player.getInventory().setSelectedSlot(var1);
         Module.client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   private void handle(Vec3d var1) {
      Vec3d var2 = var1.subtract(Module.client.player.getEyePos());
      float var3 = (float)Math.toDegrees(Math.atan2(-var2.x, var2.z));
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))));
      RotationController.handle(new RotationAngles(var3, var4), 180.0F, 180.0F, 180.0F, 180.0F, 1, 10, false);
   }

   @Override
   public void process() {
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      super.process();
   }
}
