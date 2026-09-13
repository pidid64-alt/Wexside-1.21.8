package ru.wild.modules.movement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.api.event.ClientUpdateEvent;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.MovementInputEvent;
import ru.wild.api.event.PacketEvent;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.module.ModuleFlag;
import ru.wild.automation.RotationController;
import ru.wild.util.inventory.InventorySlotActions;
import ru.wild.util.player.RotationAngles;

@ModuleRegister(
   name = "AutoDodge",
   category = ModuleCategory.Movement,
   description = "Автоматически реагирует на опасные зелья",
   flags = ModuleFlag.RISKY
)
public class AutoDodge extends Module {
   private final Map<Integer, AutoDodge.ColorStop> source = new HashMap<>();
   private static final int target = 50;
   private static final int pending = 20;
   private static final int previous = 100;
   private static final int latest = 70;
   private static final int summary = 8;
   private static final double matrixBlend = 0.05;
   private static final double vectorMatch = 0.99;
   private static final double itemProject = 0.05;
   private static final double responseCompute = 0.99;
   private int providerFetch;
   private int profileDraw;
   private int vectorPerform;
   private int eventAttach;
   private int serverRead = -1;
   private int positionAdvance = -1;
   private int frameCheck = -1;
   private ItemStack moduleCollect = ItemStack.EMPTY;
   private ItemStack providerClose = ItemStack.EMPTY;
   private int presetSave;
   private Vec3d windowConvert = Vec3d.ZERO;

   @EventHandler
   public void handle(ClientUpdateEvent var1) {
      if (Module.client.player != null && Module.client.world != null && Module.client.interactionManager != null) {
         this.tick();
         this.drawAnimation();
         this.refresh();
         if (this.eventAttach == 0) {
            Box var2 = Module.client.player.getBoundingBox().expand(2.0);
            double var3 = ((Integer)Module.client.options.getViewDistance().getValue()).intValue() * 16.0;
            Box var5 = Module.client.player.getBoundingBox().expand(var3);

            for (PotionEntity var7 : Module.client.world.getEntitiesByClass(PotionEntity.class, var5, var0 -> true)) {
               AutoDodge.ColorStop var8 = this.source.get(var7.getId());
               if (var8 != null
                  && this.handle(var7, var2)
                  && this.handle(var8.color())
                  && !(Module.client.player.distanceTo(var7) <= 2.3F)
                  && this.vectorPerform >= 0) {
                  this.compute(var7.getEyePos());
                  if (this.render()) {
                     this.vectorPerform = 5;
                     break;
                  }
               }
            }

            this.vectorPerform++;
         }
      } else {
         this.animate();
      }
   }

   @EventHandler
   public void handle(MovementInputEvent var1) {
      if (this.presetSave > 0 && this.windowConvert.lengthSquared() != 0.0) {
         double var2 = Math.toRadians(Module.client.player.getYaw());
         float var4 = (float)(-Math.sin(var2) * this.windowConvert.x + Math.cos(var2) * this.windowConvert.z);
         float var5 = (float)(Math.cos(var2) * this.windowConvert.x + Math.sin(var2) * this.windowConvert.z);
         var1.handle(MathHelper.clamp(var1.compute() + var4, -1.0F, 1.0F));
         var1.process(MathHelper.clamp(var1.resolve() + var5, -1.0F, 1.0F));
      }
   }

   @EventHandler
   public void handle(PacketEvent var1) {
      if (!var1.compute() && Module.client.player != null && Module.client.world != null) {
         Packet var2 = var1.resolve();
         this.handle(var2);
         if (var2 instanceof GameMessageS2CPacket var3 && var3.content().getString().equals("На этой анархии этот предмет не работает")) {
            this.vectorPerform = -50;
         }
      }
   }

   private void handle(Packet<?> var1) {
      if (var1 instanceof BundlePacket var15) {
         for (Object var16 : var15.getPackets()) {
            Packet<?> var17 = (Packet<?>)var16;
            this.handle(var17);
         }
      } else if (var1 instanceof EntitySpawnS2CPacket var2
         && (var2.getEntityType() == EntityType.SPLASH_POTION || var2.getEntityType() == EntityType.LINGERING_POTION)) {
         Vec3d var3 = new Vec3d(var2.getX(), var2.getY(), var2.getZ());
         Vec3d var4 = new Vec3d(var2.getVelocityX(), var2.getVelocityY(), var2.getVelocityZ());
         double var5 = Double.MAX_VALUE;
         int var7 = -1;

         for (AbstractClientPlayerEntity var9 : Module.client.world.getPlayers()) {
            if (var9 != Module.client.player && !(Module.client.player.squaredDistanceTo(var9) > 400.0)) {
               int var10 = this.handle(var9.getMainHandStack());
               if (var10 == -1) {
                  var10 = this.handle(var9.getOffHandStack());
               }

               if (var10 != -1) {
                  double var11 = var3.distanceTo(var9.getPos());
                  if (!(var11 > 25.0)) {
                     boolean var13 = var9.getY() - var3.y > 2.0 && new Vec3d(var3.x - var9.getX(), 0.0, var3.z - var9.getZ()).length() < 15.0;
                     boolean var14 = var4.lengthSquared() > 1.0E-6 && var4.normalize().dotProduct(var9.getRotationVec(1.0F).normalize()) > 0.1;
                     if ((var13 || var14) && var11 < var5) {
                        var5 = var11;
                        var7 = var10;
                     }
                  }
               }
            }
         }

         if (var7 != -1) {
            this.vectorPerform = 0;
            this.source.put(var2.getEntityId(), new AutoDodge.ColorStop(var7));
         }
      }
   }

   private int handle(ItemStack var1) {
      if (!var1.isOf(Items.SPLASH_POTION)) {
         return -1;
      }

      PotionContentsComponent var2 = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      return var2 == null ? -1 : var2.getColor() & 16777215;
   }

   private boolean handle(PotionEntity var1, Box var2) {
      return this.handle(var1.getPos(), var1.getVelocity(), var1, var2);
   }

   private boolean handle(Vec3d var1, Vec3d var2, Entity var3, Box var4) {
      for (int var5 = 0;
         var5 < 70
            && var2.lengthSquared() >= 1.0E-6
            && var1.y >= Module.client.world.getBottomY()
            && var1.y <= Module.client.world.getBottomY() + Module.client.world.getHeight();
         var5++
      ) {
         double var6 = Module.client.world.getFluidState(BlockPos.ofFloored(var1)).isIn(FluidTags.WATER) ? 0.8 : 0.99;
         var2 = new Vec3d(var2.x * var6, (var2.y - 0.05) * var6, var2.z * var6);
         Vec3d var8 = var1.add(var2);
         BlockHitResult var9 = Module.client.world.raycast(new RaycastContext(var1, var8, ShapeType.COLLIDER, FluidHandling.NONE, var3));
         if (var9.getType() == Type.BLOCK) {
            return this.handle(var4, var1, var9.getPos());
         }

         if (this.handle(var4, var1, var8)) {
            return true;
         }

         var1 = var8;
      }

      return false;
   }

   private void refresh() {
      if (this.presetSave > 0) {
         this.presetSave--;
      }

      Box var1 = Module.client.player.getBoundingBox().expand(32.0);
      Box var2 = Module.client.player.getBoundingBox().expand(0.25);

      for (PersistentProjectileEntity var4 : Module.client.world.getEntitiesByClass(PersistentProjectileEntity.class, var1, this::handle)) {
         if (var4.getOwner() != Module.client.player
            && !(var4.getVelocity().lengthSquared() < 1.0E-4)
            && this.handle(var4.getPos(), var4.getVelocity(), var4, var2)) {
            this.windowConvert = this.handle(var4.getVelocity());
            this.presetSave = 8;
            return;
         }
      }
   }

   private boolean handle(PersistentProjectileEntity var1) {
      return var1 instanceof ArrowEntity || var1 instanceof TridentEntity;
   }

   private Vec3d handle(Vec3d var1) {
      Vec3d var2 = new Vec3d(-var1.z, 0.0, var1.x).normalize();
      Vec3d var3 = var2.negate();
      if (this.process(var2)) {
         return var2;
      } else {
         return this.process(var3) ? var3 : var2;
      }
   }

   private boolean process(Vec3d var1) {
      return !Module.client.world.getBlockCollisions(Module.client.player, Module.client.player.getBoundingBox().offset(var1.multiply(0.75))).iterator().hasNext();
   }

   private boolean handle(Box var1, Vec3d var2, Vec3d var3) {
      return new Box(
            Math.min(var2.x, var3.x),
            Math.min(var2.y, var3.y),
            Math.min(var2.z, var3.z),
            Math.max(var2.x, var3.x),
            Math.max(var2.y, var3.y),
            Math.max(var2.z, var3.z)
         )
         .expand(0.12)
         .intersects(var1);
   }

   private boolean handle(int var1) {
      int var2 = 0xFF000000 | var1;
      return var2 == -13447886 || var2 == -16776961;
   }

   private boolean render() {
      if (Module.client.player.getItemCooldownManager().isCoolingDown(Items.DRIED_KELP.getDefaultStack())) {
         return false;
      }

      int var1 = InventorySlotActions.handle(Items.DRIED_KELP);
      if (var1 == -1) {
         return false;
      }

      this.serverRead = Module.client.player.getInventory().getSelectedSlot();
      this.positionAdvance = var1 >= 36 && var1 <= 44 ? -1 : var1;
      this.eventAttach = 1;
      return true;
   }

   private void tick() {
      if (this.eventAttach != 0) {
         if (this.eventAttach == 1) {
            if (this.positionAdvance >= 0) {
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.positionAdvance, this.serverRead, SlotActionType.SWAP, Module.client.player);
            } else {
               int var1 = InventorySlotActions.process(Items.DRIED_KELP);
               if (var1 == -1) {
                  this.encodePoint();
                  return;
               }

               Module.client.player.getInventory().setSelectedSlot(var1);
            }

            this.eventAttach = 2;
         } else if (this.eventAttach == 2) {
            Module.client.interactionManager.interactItem(Module.client.player, Hand.MAIN_HAND);
            Module.client.player.swingHand(Hand.MAIN_HAND);
            this.eventAttach = 3;
         } else {
            if (this.positionAdvance >= 0) {
               Module.client.interactionManager
                  .clickSlot(Module.client.player.playerScreenHandler.syncId, this.positionAdvance, this.serverRead, SlotActionType.SWAP, Module.client.player);
            }

            Module.client.player.getInventory().setSelectedSlot(this.serverRead);
            this.encodePoint();
         }
      }
   }

   private void drawAnimation() {
      Iterator var1 = this.source.keySet().iterator();

      while (var1.hasNext()) {
         if (Module.client.world.getEntityById((Integer)var1.next()) == null) {
            var1.remove();
         }
      }
   }

   private void compute(Vec3d var1) {
      Vec3d var2 = var1.subtract(Module.client.player.getEyePos());
      float var3 = (float)Math.toDegrees(Math.atan2(-var2.x, var2.z));
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))));
      RotationController.handle(new RotationAngles(var3, var4), 180.0F, 180.0F, 180.0F, 180.0F, 1, 1, false);
   }

   private void encodePoint() {
      this.eventAttach = 0;
      this.serverRead = -1;
      this.positionAdvance = -1;
      this.presetSave = 0;
      this.windowConvert = Vec3d.ZERO;
   }

   private void animate() {
      this.source.clear();
      this.vectorPerform = 0;
      this.encodePoint();
   }

   @Override
   public void process() {
      this.animate();
      super.process();
   }

   record ColorStop(int color) {
   }
}
