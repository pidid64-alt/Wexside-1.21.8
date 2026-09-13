package ru.wild.modules.combat;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.ExplosionImpl;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.PlayerUpdateEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.automation.RotationController;
import ru.wild.core.ViewRotationCoordinator;
import ru.wild.core.manager.FriendManager;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.player.RotationAngles;
import ru.wild.util.world.ServerEnvironment;

@ModuleRegister(
   name = "AutoExplosion",
   category = ModuleCategory.Combat,
   description = "Автоматически ставит и взрывает кристаллы на новом обсидиане",
   flags = ModuleFlag.RISKY
)
public class AutoExplosion extends Module {
   private static final float source = 6.0F;
   private final NumberSetting target = new NumberSetting("Количество кристаллов", 1.0F, 1.0F, 10.0F, 1.0F, false);
   private final BooleanSetting pending = new BooleanSetting("Не взрывать себя", false);
   private final BooleanSetting previous = new BooleanSetting("Не взрывать друзей", false);
   private final Set<BlockPos> latest = new HashSet<>();
   private final Set<BlockPos> summary = new HashSet<>();
   private final Queue<BlockPos> matrixBlend = new ArrayDeque<>();
   private BlockPos vectorMatch;
   private int itemProject;
   private int responseCompute = -1;
   private int providerFetch = -1;
   private int profileDraw;
   private BlockPos vectorPerform;

   public AutoExplosion() {
      this.handle(this.target, this.pending, this.previous);
   }

   @EventHandler
   public void handle(PlayerUpdateEvent var1) {
      if (!ServerEnvironment.handle() && Module.client.interactionManager != null && Module.client.getNetworkHandler() != null) {
         this.refresh();
         if (this.profileDraw > 0) {
            this.profileDraw--;
            if (this.profileDraw == 0) {
               this.tick();
            }
         } else if (this.vectorPerform == null) {
            if (this.vectorMatch == null) {
               this.render();
            }

            if (this.vectorMatch != null) {
               if (!this.handle(this.vectorMatch, 3.0)) {
                  this.drawAnimation();
               } else if (!this.resolve(this.vectorMatch)) {
                  this.drawAnimation();
               } else {
                  EndCrystalEntity var4 = this.process(this.vectorMatch);
                  if (var4 != null) {
                     if (!this.execute(this.vectorMatch)) {
                        this.drawAnimation();
                     } else {
                        this.handle(var4);
                        this.itemProject--;
                        if (this.itemProject <= 0) {
                           this.vectorMatch = null;
                        }
                     }
                  } else if (this.itemProject > 0 && !this.apply(this.vectorMatch)) {
                     this.drawAnimation();
                  } else {
                     if (this.itemProject > 0 && this.update(this.vectorMatch)) {
                        int var5 = InventorySlotActions.handle(Items.END_CRYSTAL);
                        if (var5 == -1) {
                           this.drawAnimation();
                           return;
                        }

                        this.handle(this.vectorMatch, var5);
                     }
                  }
               }
            }
         } else {
            BlockPos var2 = this.vectorPerform;
            this.vectorPerform = null;
            if (var2.equals(this.vectorMatch) && this.itemProject > 0) {
               EndCrystalEntity var3 = this.process(var2);
               if (var3 != null) {
                  if (!this.execute(var2)) {
                     this.drawAnimation();
                  } else {
                     this.handle(var3);
                     this.itemProject--;
                     if (this.itemProject <= 0) {
                        this.vectorMatch = null;
                     }
                  }
               } else {
                  if (this.apply(var2) && this.update(var2)) {
                     this.compute(var2);
                  } else {
                     this.drawAnimation();
                  }
               }
            } else {
               this.drawAnimation();
            }
         }
      }
   }

   private void refresh() {
      BlockPos var1 = Module.client.player.getBlockPos();
      byte var2 = 3;
      HashSet var3 = new HashSet();

      for (int var4 = -var2; var4 <= var2; var4++) {
         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               BlockPos var7 = var1.add(var4, var5, var6).toImmutable();
               if (this.handle(var7, (double)var2)) {
                  var3.add(var7);
                  boolean var8 = Module.client.world.getBlockState(var7).isOf(Blocks.OBSIDIAN);
                  if (!this.latest.contains(var7)) {
                     if (var8) {
                        this.summary.add(var7);
                     } else {
                        this.summary.remove(var7);
                     }
                  } else {
                     boolean var9 = this.summary.contains(var7);
                     if (!var9 && var8) {
                        this.summary.add(var7);
                        this.handle(var7);
                     } else if (var9 && !var8) {
                        this.summary.remove(var7);
                     }
                  }
               }
            }
         }
      }

      this.latest.clear();
      this.latest.addAll(var3);
   }

   private void handle(BlockPos var1) {
      if (this.handle(var1, 3.0)) {
         if (!var1.equals(this.vectorMatch)) {
            if (!this.matrixBlend.contains(var1)) {
               this.matrixBlend.offer(var1);
            }
         }
      }
   }

   private void render() {
      while (!this.matrixBlend.isEmpty()) {
         BlockPos var1 = this.matrixBlend.poll();
         if (this.handle(var1, 3.0) && this.resolve(var1)) {
            this.vectorMatch = var1;
            this.itemProject = Math.max(1, Math.round(this.target.compute()));
            return;
         }
      }
   }

   private EndCrystalEntity process(BlockPos var1) {
      Box var2 = new Box(var1.getX() - 0.5, var1.getY() + 0.5, var1.getZ() - 0.5, var1.getX() + 1.5, var1.getY() + 3.0, var1.getZ() + 1.5);

      for (Entity var4 : Module.client.world.getOtherEntities(null, var2)) {
         if (var4 instanceof EndCrystalEntity var5) {
            return var5;
         }
      }

      return null;
   }

   private void handle(EndCrystalEntity var1) {
      this.handle(var1.getPos());
      Module.client.interactionManager.attackEntity(Module.client.player, var1);
      Module.client.player.swingHand(Hand.MAIN_HAND);
      this.profileDraw = 2;
   }

   private void handle(BlockPos var1, int var2) {
      this.handle(var2);
      this.vectorPerform = var1;
   }

   private void compute(BlockPos var1) {
      Vec3d var2 = var1.toCenterPos().add(0.0, 0.5, 0.0);
      this.handle(var2);
      Module.client.interactionManager.interactBlock(Module.client.player, Hand.MAIN_HAND, new BlockHitResult(var2, Direction.UP, var1, false));
      Module.client.player.swingHand(Hand.MAIN_HAND);
      this.profileDraw = 2;
   }

   private boolean resolve(BlockPos var1) {
      BlockState var2 = Module.client.world.getBlockState(var1);
      return var2.isOf(Blocks.OBSIDIAN) || var2.isOf(Blocks.BEDROCK);
   }

   private boolean update(BlockPos var1) {
      if (!this.resolve(var1)) {
         return false;
      }

      BlockPos var2 = var1.up();
      BlockPos var3 = var2.up();
      return Module.client.world.getBlockState(var2).isAir()
         && Module.client.world.getBlockState(var3).isAir()
         && Module.client.world.getOtherEntities(null, new Box(var2)).isEmpty()
         && Module.client.world.getOtherEntities(null, new Box(var3)).isEmpty();
   }

   private boolean handle(BlockPos var1, double var2) {
      return Module.client.player.squaredDistanceTo(var1.toCenterPos()) <= var2 * var2;
   }

   private boolean apply(BlockPos var1) {
      return this.pending.compute() && this.prepare(var1) ? false : this.execute(var1);
   }

   private boolean execute(BlockPos var1) {
      if (this.pending.compute() && this.prepare(var1)) {
         return false;
      }

      if (!this.previous.compute()) {
         return true;
      }

      for (PlayerEntity var3 : Module.client.world.getPlayers()) {
         if (var3 != Module.client.player && FriendManager.handle(var3.getName().getString()) && this.handle(var3, var1.toCenterPos().add(0.0, 1.0, 0.0))) {
            return false;
         }
      }

      return true;
   }

   private boolean prepare(BlockPos var1) {
      return Module.client.player.getBlockY() == var1.getY() + 1;
   }

   private boolean handle(PlayerEntity var1, Vec3d var2) {
      double var3 = 12.0;
      return var1.squaredDistanceTo(var2) > var3 * var3 ? false : ExplosionImpl.calculateReceivedDamage(var2, var1) > 0.0F;
   }

   private void handle(int var1) {
      if (this.responseCompute < 0) {
         this.responseCompute = Module.client.player.getInventory().getSelectedSlot();
      }

      if (this.process(var1)) {
         Module.client.player.getInventory().setSelectedSlot(this.compute(var1));
      } else {
         this.providerFetch = var1;
         Module.client.interactionManager
            .clickSlot(Module.client.player.playerScreenHandler.syncId, this.providerFetch, this.responseCompute, SlotActionType.SWAP, Module.client.player);
      }
   }

   private void tick() {
      if (this.responseCompute >= 0) {
         if (this.providerFetch >= 0) {
            Module.client.interactionManager
               .clickSlot(Module.client.player.playerScreenHandler.syncId, this.providerFetch, this.responseCompute, SlotActionType.SWAP, Module.client.player);
            if (Module.client.getNetworkHandler() != null) {
               Module.client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(Module.client.player.playerScreenHandler.syncId));
            }

            this.providerFetch = -1;
         }

         Module.client.player.getInventory().setSelectedSlot(this.responseCompute);
         this.responseCompute = -1;
      }
   }

   private boolean process(int var1) {
      return var1 >= 0 && var1 <= 8 || var1 >= 36 && var1 <= 44;
   }

   private int compute(int var1) {
      return var1 >= 36 ? var1 - 36 : var1;
   }

   private void drawAnimation() {
      this.vectorMatch = null;
      this.itemProject = 0;
      this.vectorPerform = null;
      this.tick();
   }

   private void handle(Vec3d var1) {
      Vec3d var2 = var1.subtract(Module.client.player.getEyePos());
      float var3 = (float)Math.toDegrees(Math.atan2(-var2.x, var2.z));
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))));
      RotationController.handle(new RotationAngles(var3, MathHelper.clamp(var4, -90.0F, 90.0F)), 360.0F, 360.0F, 360.0F, 360.0F, 2, 30, false);
   }

   @Override
   public void process() {
      if (this.responseCompute >= 0 && Module.client.player != null) {
         this.tick();
      }

      this.vectorMatch = null;
      this.itemProject = 0;
      this.responseCompute = -1;
      this.providerFetch = -1;
      this.profileDraw = 0;
      this.vectorPerform = null;
      this.matrixBlend.clear();
      this.latest.clear();
      this.summary.clear();
      RotationController.instance = RotationController.Mode.IDLE;
      RotationController.cache = 0;
      RotationController.active = null;
      ViewRotationCoordinator.instance = false;
      super.process();
   }
}
